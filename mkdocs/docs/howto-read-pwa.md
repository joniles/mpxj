# How To: Read Microsoft Project Server Projects

## What's in a Name?
Microsoft Project Server is a project and portfolio management platform built on
top of Sharepoint. It does not include its own project management application,
but rather uses Microsoft Project Professional on the desktop as the main
user-facing front end to allow projects to be developed and managed, while
providing central storage of projects, portfolio management and reporting on
the backend server.

Project Server can be installed on premise, although it has become more common
to see use of the Microsoft-hosted version of Project Server, branded as
Project Online. There are also third-party hosting options, but these
aren't covered here.

A core part of the Project Server platform is the Project Web App (PWA) which
among other things provides an API which allow data to be extracted from Project
Server. This is the component MPXJ works with, and for the remainder of this
documentation we'll refer to PWA as a catch-all term for the various flavors of
Project Server.

> At the time of writing it appears that Project Online will be
> [discontinued by Microsoft on September 30, 2026](https://techcommunity.microsoft.com/blog/plannerblog/microsoft-project-online-is-retiring-what-you-need-to-know/4450558).
> Although Project Online will no longer be available, on premise versions of
> Project Server remain available, and should be accessible through MPXJ
> using the reader described here.
 
## Reading Projects
MPXJ uses the API provided by PWA to read project data. This is achieved using
the `PwaReader` class illustrated below:

=== "Java"
	```java
	// The URL for your Project Server instance
	String projectServerUrl = "https://example.sharepoint.com/sites/pwa";
	
	// We're assuming you have already authenticated
	// as a user and have an access token
	String accessToken = "my-access-token-from-oauth";
	
	// Create a reader
	PwaReader reader = new PwaReader(projectServerUrl, accessToken);
	
	// Retrieve the projects available and print their details
	List<PwaProject> projects = reader.getProjects();
	for (PwaProject project : projects)
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
	// The URL for your Project Server instance
	var projectServerUrl = "https://example.sharepoint.com/sites/pwa";
	
	// We're assuming you have already authenticated as a user and have an access token
	var accessToken = "my-access-token-from-oauth";
	
	// Create a reader
	var reader = new PwaReader(projectServerUrl, accessToken);
	
	// Retrieve the projects available and print their details
	var projects = reader.GetProjects();
	foreach (var p in projects)
	{
    	System.Console.WriteLine("ID: " + p.ProjectId
    		+ " Name: " + p.ProjectName);
	}
	
	// Get the ID of the first project on the list
	var projectId = projects.First().ProjectId;
	
	// Now read the project
	var project = reader.ReadProject(projectId);
	```

As the example illustrates, the reader is initialized with the URL of your
Project Server instance, and an access token. We'll discuss in more detail
in a later section how to get an access token.

Once you have the reader set up, the `getProjects` method can be called to
retrieve a list of the available projects. These are returned as a list of
`PwaProject` instances. Each `PwaProject` instance contains the unique ID of
the project along with its name.

We can then read an individual project by passing the project's unique ID to the 
`readProject` method, which will return a `ProjectFile` instance.

## Caveats

MPXJ is using the API exposed by PWA to read the various component parts of the
a project and assemble them into a `ProjectFile` instance. There are
unfortunately some gaps in the data available via the API, in particular
calendar data is incomplete (it is not possible to read the normal working
hours for a given calendar), resource calendars are not available, and any
calendars assigned to tasks are not available.

What this means in practice is that the project data returned by MPXJ is a useful
snapshot of the current state of a project, but it does not contain enough
data to allow you to modify or re-schedule the project using CPM.

> Why can't we just download an MPP file? That's an interesting question,
> after all isn't that what Microsoft Project Professional is doing when
> it is working with Project Server? Sadly, the situation isn't that simple.
> It appears that Microsoft Project Professional uses private
> APIs provided by Project Server to download a set of binary data which
> it then reassembles into a full MPP file. It would certainly be feasible
> to replicate this using MPXJ, but for Project Online these private APIs depend
> on an authentication mechanism which is specific to Microsoft and Microsoft
> Project Professional on the desktop.
> 
> For on premise Project Server installations where Sharepoint users can be
> created who can authenticate with just a username and password,
> it is possible to call these private APIs. If you have an on premise 
> installation like this and would like to help me improve MPXJ's functionality,
> [please get in touch](mailto:jon@timephased.com).

## Authentication
This is probably the trickiest part of getting data out of Project Server. The
exact mechanism you'll need to use will depend on how your Project Server
instance is hosted. In this section I will focus on Project Online as it is a
fairly common way of using Project Server, but the concepts covered here are
likely to be transferable to other types of installation.

> If you have an on premise version of Project Server (or Project Server hosted
> by an third party) which allows you to authenticate with just a username and
> password, please [get in touch](mailto:jon@timephased.com).
> I'm keen to collaborate with you to get authentication working in your
> environment.
 
With Project Online, the only option is to use "modern authentication" - there
is no option to have a user account with just a username and password. This
means that we need to accommodate users being prompted to enter their username,
password and provide a second authentication factor like TOTP or a code
delivered by SMS. Fortunately this is all handled for us by Microsoft, so I'll
explain what needs to be set up to get this to work for us.

To get authentication working we'll be using Microsoft Entra which is
Microsoft's cloud based Identity and Access management (IAM) platform. Entra
can be found at [https://entra.microsoft.com/](https://entra.microsoft.com/).

What we'll be configuring in Entra is an App Registration which will 
allow us to authenticate with delegated permissions as a Sharepoint user. This
means that once authenticated we'll be making API calls "on behalf of"
the user who provided their credentials.

For convenience you'll find [some sample code on GitHub](https://github.com/joniles/mpxj-java-samples/blob/main/src/main/java/org/mpxj/howto/use/pwa/DesktopMicrosoftAuthenticator.java)
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
4. Finally you'll need to add the following Delegated Sharepoint permissions:
	`AllSites.FullControl` and `ProjectWebApp.FullControl`.


The final thing we'll need is the "resource" we'll be requesting access to
when we authenticate. In this case, if the URL to access your Project Server
instance is `https://example.sharepoint.com/sites/pwa`, the 
resource we need to use will be `https://example.sharepoint.com`.

With the App Registration in Entra configured as described above, you should be
able to use the details noted above with the [sample authentication code](https://github.com/joniles/mpxj-java-samples/blob/main/src/main/java/org/mpxj/howto/use/pwa/DesktopMicrosoftAuthenticator.java)
to authenticate with Microsoft and retrieve an access token. Once you have an
access token you can use `PwaReader` to read project data!
