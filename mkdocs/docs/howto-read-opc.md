# How To: Read Oracle Primavera Cloud projects

Oracle Primavera Cloud (OPC) is Oracle's
"next generation" scheduling product. The nomenclature is confusing as this is
not a hosted version of Primavera P6, but rather a distinct product which is
different from the various hosted options available for Primavera P6 and P6
EPPM.

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

This will be the first part of the URL your users would normally see in their
browser's address bar once they are logged in to OPC.

## Read a project

Once you have the username, password and hostname, you can create an instance of
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
`getProjects()` method of the reader when we called `readProject()`. This is
not strictly necessary, you can construct your own `OpcProject` instance which
just needs to include the project and workspace IDs, as illustrated by the code
below.

```java
OpcProject opcProject = new OpcProject();
opcProject.setWorkspaceId(123);
opcProject.setProjectId(456);

OpcReader reader = new OpcReader("my-opc-host.oraclecloud.com",
		"my-username", "my-password");
ProjectFile mpxjProject = reader.readProject(opcProject);
```

This means that you can store and manage details of the projects returned by the
`getProjects()` call in your own code, then populate an `OpcProject` instance
when you need to retrieve data for a specific project.


## Export a project

As well as reading a project from OPC and returning a `ProjectFile` instance,
the `OpcReader` allows you to export a project directly to an XML or XER file.
You may find this useful if you need to retain a snapshot of the project from
a given point in time, or pass the project data on to another system for
further processing. 

```java
OpcReader reader = new OpcReader("my-opc-host.oraclecloud.com",
	"my-username", "my-password");
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

The final argument determines whether the resulting file is compressed or not.
Setting this argument to true will create a zip file containing the requested
XER or XML file:

```java
reader.exportProject(opcProject, "export-file.zip", OpcExportType.XML, true);
```

There are two additional forms of the `exportProject` method, one which takes
a `File` instance representing the target file, and one which takes an
`OutputStream` instance to which the file data will be written:

```java
// Output destination specified by a File instance
File file = new File("export-file.xer");
reader.exportProject(opcProject, file, OpcExportType.XER, false);

// Output sent to an OutputStream
try (OutputStream out = Files.newOutputStream(Paths.get("export-file.xer")))
{
 reader.exportProject(opcProject, out, OpcExportType.XER, false);
}
```

## Baselines

The baselines available in OPC for a given project can be retrieved using the
`getProjectBaselines` method:

```java
List<OpcProjectBaseline> baselines = reader.getProjectBaselines(opcProject);
```

The `getProjectBaselines` method returns a list of `OpcProjectBaseline`
instances, each containing the name and ID of a baseline. This information can be
used to include baseline data as part of the project read from OPC:

```java
// We're assuming that the project has more than one baseline.
// We'll just request data from the first baseline.
List<OpcProjectBaseline> requiredBaselines =
	Collections.singletonList(baselines.get(0));
ProjectFile mpxjProject = reader.readProject(opcProject, requiredBaselines);
```

In the example above we're just requesting that data for one baseline is
included. As we're passing a list of `OpcProjectBaseline` instances to the
`readProject` method, we can request as many of the project's baselines as
we need - just include them in this list.

As we saw previously with the `OpcProject` class, we don't need to directly
reuse the `OpcProjectBaseline` instances returned by the `getProjectBaselines`
method. The only required value in this class is the baseline ID. You can
manage the storage of the baseline names and IDs in your own code, and just
construct suitable `OpcProjectBaseline` instances when you wish to export a
project with a baseline, as the example below illustrates:

```java
List<OpcProjectBaseline> requiredBaselines = new ArrayList<>();

OpcProjectBaseline baseline1 = new OpcProjectBaseline();
baseline1.setProjectBaselineId(789);
requiredBaselines.add(baseline1);

OpcProjectBaseline baseline2 = new OpcProjectBaseline();
baseline2.setProjectBaselineId(790);
requiredBaselines.add(baseline2);

ProjectFile mpxjProject = reader.readProject(opcProject, requiredBaselines);
```

Finally, as well as reading a project with baselines directly using MPXJ,
you can also export a project including baselines. As before there are several
variants of the `exportProject` method taking a file name, `File` instance
or an `OutputStream` instance. The example below illustrates exporting a
project with a baseline to a named file:

```java
reader.exportProject(opcProject, requiredBaselines,
	"export-file.xml", OpcExportType.XML, false);
```

> Note that only the XML-based file format supports the inclusion of
> baselines. If you request an XER export, baseline data will not be present
> in the exported file.

## Pagination
For requests made to the OPC API where more than 5000 items are returned, the
API changes the structure of its response to use pagination. MPXJ currently
doesn't support this paginated structure. If you have more than 5000 projects
in a workspace, or more than 5000 baselines for a project, please contact me
and I will update MPXJ to support this.
