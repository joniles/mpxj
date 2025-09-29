# How To: Read Primavera P6 Web Services

Primavera P6 Web Services (p6ws) provides SOAP and REST APIs to allow you to
access data held in Primavera P6. P6 Web Services is often installed as part of
the Primavera P6 Enterprise Project Portfolio Management (EPPM) product. You
may also find that it is available if you are using Oracle-hosted Primavera P6
Professional. Third party P6 hosting services may also provide access to P6 Web
Services.

## Authorization

You will need to use a P6 user account to access the APIs exposed by P6 Web
Services. Best practice suggests creating a user account dedicated to this
purpose, although a normal user account can be used. You will need to ensure
that the user account is authorized to use Web Services. This setting can be
found in the EPPM web interface on the Administration tab under User
Administration. The Module Access tab at the bottom of the page lists Web
Services as one of the available modules.

The user account will also need to have access to the projects you wish to work
with via the API. At minimum the user account will need read access, including
the "Export Project Data" privilege.

## Authentication

The P6 Web Services REST APIs allow for the use of Basic Authentication and
OAuth authentication mechanisms. Note that the API documentation indicates that
OAuth is only supported for P6 Web Services hosted on Oracle Cloud
Infrastructure. The type of authentication used is selected when you create an
instance of the reader class, as illustrated by the examples in the sections
below.

> NOTE: I have not had the opportunity to test OAuth authentication with P6 Web Services.
> You can create an instance of the MPXJ reader class with a Bearer token,
> which assumes that
> you have managed the OAuth authentication process yourself. Please 
> [get in touch](mailto:jon@timephased.com)
> if you are using or need to use OAuth as I'd like to improve MPXJ's 
> support in this area.


## Read a Project

This example assumes the use of Basic Authentication to connect to P6 Web
Services. In order to make a connection you'll need the URL of your P6 Web
Services installation, the name of the database you will be connecting to, and
a username and password.

> By default, the path to a P6 Web Services installation will be `p6ws`,
> so for example if you navigate to `https://my-p6-host/p6` to access
> the P6 web interface, the P6 Web Services URL is likely to be 
> `https://my-p6-host/p6ws`. You may need to ask your P6 administrator to
> confirm this as it is possible to configure the path used.

You will need to ask your P6 Administrator to confirm the database name.
This information is available via the P6 EPPM administration web pages.

Once you have all of these details, you can create an instance of
the reader and retrieve a list of the available projects.

```java
import org.mpxj.MPXJException;
import org.mpxj.ProjectFile;
import org.mpxj.primavera.webservices.WebServicesProject;
import org.mpxj.primavera.webservices.WebServicesReader;

// ...

WebServicesReader reader = new WebServicesReader(
 "https://my-p6-host/p6ws",
 "my-database-name",
 "my-user-name",
 "my-password");
List<WebServicesProject> wsProjects = reader.getProjects();
```

Each `WebServicesProject` instance contains the name and unique ID (Object ID)
of each project along with the user-visible P6 project identifier (ID), the
data date of the project, and finally the unique ID of the current baseline,
if one has been set.

Once you have selected the project you are interested in, you can read that
project into a `ProjectFile` instance by passing the relevant
`WebServicesProject` instance to the reader:


```java
// In this example, we'll just use the first project returned
WebServicesProject wsProject = wsProjects.get(0);
ProjectFile mpxjProject = reader.readProject(wsProject);
```

You now have access to the project data via the `ProjectFile` instance.

In the example above we have used an instance of `WebServicesProject` returned
by the `getProjects` method of the reader when we called `readProject`.
This is not strictly necessary, you can construct your own
`WebServicesProject` instance which just needs to include the project unique
ID (Object ID), as illustrated by the example below.

```java
WebServicesProject wsProject = new WebServicesProject();
wsProject.setObjectId(123);

WebServicesReader reader = new WebServicesReader(
 "https://my-p6-host/p6ws",
 "my-database-name",
 "my-user-name",
 "my-password");

ProjectFile mpxjProject = reader.readProject(wsProject);
```

This means that you can store and manage details of the projects returned by the
`getProjects` call in your own code, then populate a `WebServicesProject`
instance when you need to retrieve data for a specific project.

You will have noticed that the `WebServicesProject` class contains the unique ID
of the current baseline for the project you are reading. By default if this
value is present, MPXJ will read the baseline data. If you wish to
change this behavior, there is a second version of the `readProject` method
which takes a Boolean argument allowing you to determine whether or not the
baseline is read, as the example below illustrates:

```java
ProjectFile mpxjProject = reader.readProject(wsProject, false);
```
In this case we're passing `false` for the `includeBaseline` argument, so
the current baseline project will not be exported.

## Export a Project

As well as reading a project from P6 Web Services and returning a `ProjectFile`
instance, the `WebServicesReader` allows you to export a project directly to an
XML or XER file. You may find this useful if you need to retain a snapshot of
the project from a given point in time, or pass the project data on to another
system for further processing. 


```java
WebServicesReader reader = new WebServicesReader(
 "https://my-p6-host/p6ws",
 "my-database-name",
 "my-user-name",
 "my-password");
List<WebServicesProject> wsProjects = reader.getProjects();

// In this example, we'll just use the first project returned
WebServicesProject wsProject = wsProjects.get(0);
reader.exportProject(
	wsProject, 
	"export.xml", 
	WebServicesExportType.XML, 
	true, 
	false);
```

In the example above, we're passing an instance of the `WebServiceProject` class
to the `exportProject` method to indicate which project we wish to export. The
second argument is the name of the file into which the project will be
exported. The third argument represents the type of file we wish to export, in
this case we're writing a PMXML file. The finally two arguments are Boolean
flags indicating respectively if we wish to export the current baseline, and if
we wish to export the project as a zip file.

> Note that baseline export is only possible when writing a PMXML file.
> If the selected file format is XER, the Boolean flag indicating that the 
> current baseline should be exported is ignored.

To export an XER file instead of a PMXML file, the call to `exportProject`
would look like this:

```java
reader.exportProject(
	wsProject, 
	"export.xer", 
	WebServicesExportType.XER, 
	false, 
	false);
```

If we would prefer to write the project data to a zip file, the call to
`exportProject` would be as follows:

```java
reader.exportProject(
	wsProject, 
	"export.xer.zip", 
	WebServicesExportType.XER, 
	false, 
	true);
```

Finally, MPXJ also provides a version of the `exportProject` method which takes
an `OutputStream` instance as an argument rather than a filename, allowing you
to manage the file data yourself.

## OAuth

As noted in the introduction, Oracle indicates that OAuth can be used to provide
authentication for the P6 Web Services APIs when hosted on Oracle Cloud
Infrastructure. At present MPXJ assumes that you have managed the OAuth process
yourself and have obtained a suitable bearer token. The code below illustrates
how you'd use this bearer token when initializing `WebServicesReader`:

```java
// ...
WebServicesReader reader = new WebServicesReader(
 "https://my-p6-host/p6ws",
 "my-bearer-token"
);
List<WebServicesProject> wsProjects = reader.getProjects();
```

> Please [get in touch](mailto:jon@timephased.com)
> if you are using or need to use OAuth as I'd like to improve MPXJ's 
> support in this area.
