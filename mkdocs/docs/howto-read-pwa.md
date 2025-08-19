# How To: Read Microsoft Project Server projects

## What's in a name?
Microsoft Project Server is a project and portfolio management platform built on
top of Sharepoint. It does not include its own project management application,
but rather uses Microsoft Project Professional on the desktop as the main
user-facing front end to allow projects to be developed and managed, while
providing central storage of projects, portfolio management and reporting on
the backend server.

Project Server can be installed on premise, although it has become more common
to see use of the Microsoft-hosted version of Project Server, branded as
Project Online. There are also third-party hosting options, although these
aren't covered here.

A core part of the Project Server platform is the Project Web App (PWA) which
among other things provides an API which allow data to be extracted from Project
Server. This is the component MPXJ works with, and for the remainder of this
documentation we'll refer to PWA as a catch-all term for the various flavors of
Project Server.

## Reading PWA Projects with MPXJ
MPXJ uses the API provided by PWA to read project data. This is achieved using
the `PwaReader` class illustrated below:

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
> It appears that Microsoft Project Professional uses a set of private
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
> please get in touch via the support link on the sidebar.


## Authentication


