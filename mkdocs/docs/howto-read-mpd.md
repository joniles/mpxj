# How To: Read MPD files
Microsoft Project from Project 98 until Project 2003 could read and write
schedules as Microsoft Access database files with the extension MPD. Versions
of Microsoft Project after 2003 can import projects from MPD databases but
cannot create or write to them. Project 98 crates a database with a schema
known as MPD8, which MPXJ does not currently support reading. Project 2000
onward uses a schema call MPD9 which MPXJ can read.

Coincidentally, Microsoft Project Server originally shared the same database
schema as the MPD9 file format. This means that the `MPDDatabaseReader` class
may also be used to read data from a Project Server SQL Server database.

## Reading MPD files
The simplest way to read an MPD file is to use the `UniversalProjectReader`:

```java
import net.sf.mpxj.ProjectFile;
import net.sf.mpxj.reader.UniversalProjectReader;

// ...

UniversalProjectReader reader = new UniversalProjectReader();
ProjectFile project = reader.read("my-sample.mpd");
```
In order for this to work the `UniversalProjectReader` assumes that the
JDBC-ODBC bridge driver is available. As an MPD file can contain multiple
projects, the `UniversalProjectReader` assumes that a single project is present
in the file with an ID of 1. This is normally the case when a single project is
saved as an MPD file.

## Using MPDDatabaseReader
You can work directly with the `MPDDatabaseReader` class by replacing
`UniversalProjectReader` with `MPDDatabaseReader`. This provides access to
additional options, as described below.

### Setting the database connection
Three `read` methods are provided by the `MPDDatabaseReader` class which allow
you to work directly with an MPD file, either by passing in a file name a `File`
instance or an `InputStream` instance. These methods use the JDBC-ODBC bridge
driver to open the database. An alternative approach is for you to provide your
own database connection. To that end the `MPDDatabaseReader` class provides two
additional methods: `setConnection` and `setDataSource` which allows you to
supply a JDBC `Connection` instance or a JDBC `DataSource` instance.

You can of course use this to set up your own JDBC connection to read from the
MDB file, however these methods may be more useful if you wish to read data from
a Microsoft Project Server database instance, which shares the same schema as
the MDB file.

### Selecting a project
If the MPD file contains multiple projects, you can retrieve details of the
available projects using the `listProjects` method. This returns a map of
project IDs and project names. The sample code below illustrates how to retrieve
this list of projects, and select the specific project that you want to read. In
this case we read each project in the file in turn.

```java
import java.util.Map;
import java.util.Map.Entry;
import net.sf.mpxj.mpd.MPDDatabaseReader;

MPDDatabaseReader reader = new MPDDatabaseReader();
reader.setConnection(connection);
Map<Integer, String> projects = reader.listProjects();
for (Entry<Integer, String> entry : projects.entrySet())
{
   System.out.println("Project name: " + entry.getValue());
   reader.setProjectID(entry.getKey());
   reader.read();
}

```
