# How To: Read Microsoft Planner

## Microsoft Planner
Microsoft Planner is Microsoft's replacement for  Project Online, which has now
been discontinued. It provides a browser-based interface allowing you to
create, manage and collaborate on project plans. Various subscription tiers are
available, the lowest of which only offers basic "task list" functionality.
Higher tiers include functionality closer to that provided by Microsoft
Project.

## Reading Projects
MPXJ uses the Microsoft Dynamics API to read project data from Planner.
This is achieved using the `MsPlannerReader` class illustrated below:

=== "Java"
	```java
	// The URL for your organisation's Dynamics server instance
	String dynamicsServerUrl = "https://example.api.crm11.dynamics.com";

	// We're assuming you have already authenticated and have an access token
	String accessToken = "my-access-token-from-oauth";

	// Create a reader
	MsPlannerReader reader = new MsPlannerReader(dynamicsServerUrl, accessToken);

	// Retrieve the projects available and print their details
	List<MsPlannerProject> projects = reader.getProjects();
	for (MsPlannerProject project : projects)
	{
	   System.out.println("ID: " + project.getProjectId()
	   	+ " Name: " + project.getProjectName());
	}

	// Get the ID of the first project on the list
	UUID projectID = projects.get(0).getProjectId();

	// Now read the project
	ProjectFile project = reader.readProject(projectID);
	```
=== "C#"
	```c#
	// The URL for your organisation's Dynamics server instance
	var dynamicsServerUrl = "https://example.api.crm11.dynamics.com";
	
	// We're assuming you have already authenticated and have an access token
	var accessToken = "my-access-token-from-oauth";
	
	// Create a reader
	var reader = new MsPlannerReader(dynamicsServerUrl, accessToken);
	
	// Retrieve the projects available and print their details
	var projects = reader.GetProjects();
	foreach (var p in projects)
	{
    	System.Console.WriteLine("ID: " + p.ProjectId + " Name: " + p.ProjectName);
	}
	
	// Get the ID of the first project on the list
	var projectId = projects.First().ProjectId;
	
	// Now read the project
	var project = reader.ReadProject(projectId);
	```

As the example illustrates, the reader is initialized with the URL of your
organisation's Microsoft Dynamics server (which hosts your Planner instance),
and an access token. We'll discuss in more detail in the sections below how to
get an access token and how to find the Dynamics URL you need.

Once you have the reader set up, the `getProjects` method can be called to
retrieve a list of the available projects. These are returned as a list of
`MsPlannerProject` instances. Each `MsPlannerProject` instance contains the
unique ID of the project along with its name.

We can then read an individual project by passing the project's unique ID to the 
`readProject` method, which will return a `ProjectFile` instance.


## Authentication
To get authentication working we'll be using Microsoft Entra which is
Microsoft's cloud based Identity and Access management (IAM) platform. Entra
can be found at [https://entra.microsoft.com/](https://entra.microsoft.com/).

What we'll be configuring in Entra is an App Registration which will 
allow us to authenticate with delegated permissions as a Planner user. This
means that once authenticated we'll be making API calls "on behalf of"
the user who provided their credentials.

For convenience you'll find [some sample code on GitHub](https://github.com/joniles/mpxj-java-samples/blob/main/src/main/java/org/mpxj/howto/use/microsoft/DesktopMicrosoftAuthenticator.java)
which uses the configuration I'm describing here to retrieve an access token.
This sample code is purely provided as a simple working example to get you
started. In production you'd need to use a version of
[Microsoft's  MSAL library](https://learn.microsoft.com/en-us/entra/identity-platform/msal-overview)
for your language of choice, or another tried-and-tested OAuth library to manage
authentication.

In summary we'll need to:

1. Create an App Registration and take note of the `Application (client) ID` - 
	this is our Client ID
2. In the `Certificates & secrets` section, we'll create a Client Secret and
	copy the value - this is our Client Secret
3. In the `Authentication` section we'll need to add a Redirect URI. This is
	where the user's browser will redirect on completion of authentication. For
	the example code I've linked to above this will be a `localhost` URL.
4. Finally you'll need to add the `Dynamics CRM` permission
	`user_impersonation`.


The final thing we'll need is the "resource" we'll be requesting access to
when we authenticate, which in this case will be the URL for your
Dynamics server instance. For the example code shown above,
we'd need to request the resource `https://example.api.crm11.dynamics.com`.

With the App Registration in Entra configured as described above, you should be
able to use the details noted above with the [sample authentication code](https://github.com/joniles/mpxj-java-samples/blob/main/src/main/java/org/mpxj/howto/use/microsoft/DesktopMicrosoftAuthenticator.java)
to authenticate with Microsoft and retrieve an access token.

## Microsoft Dynamics Server URL
There are a various ways to locate the Microsoft Dynamics URL you'll require
to use `MsPlannerReader`.

A simple manual approach is to navigate to `https://make.powerapps.com/` and
sign in. Microsoft Planner is an application built in Powerapps, and when you
select the Apps entry in the menu bar on the left of the screen you should see
an entry for Planner in the table of applications. Hovering over this entry
will reveal a "play" icon. Click this to start the Planner application, then
once the application has started the URL in the address bar of your web browser
will start with something like `https://org12345678.crm11.dynamics.com/`. This
is the URL you'll need.

To locate the Dynamics URL programmatically you can make a `GET` request to the
following Global Discovery API:

```
https://globaldisco.crm.dynamics.com/api/discovery/v2.0/Instances
```

You'll need to pass a bearer token to authenticate. The bearer token can be
requested via OAuth as described above, but in this instance the resource you
need to request the token for will be `https://disco.crm.dynamics.com`.

The Global Discovery API will return a list of environments you have access to,
typically this will be the default environment for your organisation, plus
a personal environment. The URL you need will be in the `ApiUrl` attribute.
