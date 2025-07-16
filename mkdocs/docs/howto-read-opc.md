# How To: Read Oracle Primavera Cloud projects

Oracle Primavera Cloud (henceforth abbreviated to OPC) is Oracle's
"next generation" scheduling product. The nomenclature is confusing as this is
 not a hosted version of Primavera P6, but rather a distinct product which is
 different from the various hosted options available for Primavera
 P6 and P6 EPPM.

OPC provides a REST API which is used by MPXJ to read project data. You will
need to ensure that you have 
[configured access to the API](https://docs.oracle.com/cd/E80480_01/English/integration/primavera_rest_api/D207871.html)
before attempting to read project data using MPXJ.

## Authentication

At the time of writing, for most users OPC only supports a form of basic
authentication. OAuth is currently only available for users whose OPC instance
is hosted in the Oracle US Defense Cloud (OC3).

> Please contact me if you have access to OAuth authentication for OPC
> and I will update the reader to allow you to use this.

In order to connect the OPC with MPXJ you will require a user name, a password,
and the hostname for your OPC instance. The hostname will typically follow
the pattern `something.oraclecloud.com`, similar to one of these examples:

* `primavera.oraclecloud.com`
* `primavera-us2.oraclecloud.com`
* `primavera-eu1.oraclecloud.com`

This will be the first part of the URL your users would normally see when
they are logged in to OPC.

## Read a project

Once you have the username password and hostname, you can create an instance of
the reader and retrieve a list of the available projects.

```java
import org.mpxj.opc.OpcProject;
import org.mpxj.opc.OpcReader;

// ...
OpcReader reader = new OpcReader("my-opc-host.oraclecloud.com",
	"my-username", "my-password");
List<OpcProject> opcProjects = reader.getProjects();
```

The `OpcProject` instances contain the name and ID of each project along with
the ID of the OPC workspace the project is associated with. Once you have
selected the project you are interested in, you can read that project into a
`ProjectFile` instance by passing the relevant `OpcProject` instance to the
reader:

```java
// In this example, we'll just use the first project returned
OpcProject opcProject = opcProjects.get(0);
ProjectFile mpxjProject = reader.readProject(opcProject);
```

You now have access to the project data via the `ProjectFile` instance.

In the example above we have used an instance of `OpcProject` returned by the
`getProjects()` method of the reader when we called `readProject()`.
This is not strictly necessary, you can
construct your own `OpcProject` instance which just needs to include the
project and workspace ID, as illustrated by the code below.

```java
OpcProject opcProject = new OpcProject();
opcProject.setWorkspaceId(123);
opcProject.setProjectId(456);

OpcReader reader = new OpcReader("myopchost.oraclecloud.com", "myusername", "mypassword");
ProjectFile mpxjProject = reader.readProject(opcProject);
```

This means that you can store and manage details of the projects returned by the
`getProjects()` call in your won code, then populate an `OpcProject` instance
when you need to retrieve data for a specific project.


## Export a project
As well as reading a project from OPC and returning a `ProjectFile` instance,
the `OpcReader` allows you to export a project directly to an XML or XER file.
You may find this useful if you need to retain a snapshot of the project from
a given point in time, or pass the project data on to another system for
further processing. 

```java
OpcReader reader = new OpcReader("myopchost.oraclecloud.com", "myusername", "mypassword");
List<OpcProject> opcProjects = reader.getProjects();

// In this example, we'll just use the first project returned
OpcProject opcProject = opcProjects.get(0);
reader.exportProject(opcProject, "export-file.xer", OpcExportType.XER, false);
```

In the example above, the second argument is the name of the file we'll create,
the third argument represents the file type we wish to write. If we're writing
an XML file, the `exportProject` call would look like this:

```java
reader.exportProject(opcProject, "export-file.xml", OpcExportType.XML, false);
```

The final argument determines whether the result file is compressed or not.
Setting this argument to true will create a zip file containing the requested
XER or XML file:

```java
reader.exportProject(opcProject, "export-file.zip", OpcExportType.XML, true);
```

