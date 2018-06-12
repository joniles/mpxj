## How To: Read MS Project MPD files
Microsoft Project from Project 98 until Project 2003 could read and write
schedules as MPD databases. 

### Reading MS Project MPD files
The simplest way to read an MS Project MPD file is to use the `UniversalProjectReader`:

```java
import net.sf.mpxj.ProjectFile;
import net.sf.mpxj.reader.UniversalProjectReader;

...

UniversalProjectReader reader = new UniversalProjectReader();
ProjectFile project = reader.read("my-sample.mpd");
```
In order for this to work the `UniversalProjectReader` assumes that the JDBC-ODBC bridge driver is available.
An MS Project MPD file can contain multiple projects. The `UniversalProjectReader` assumes that a single
project is present in the MPD file, and opens the project with an ID of 1.

### Using MPDDatabaseReader
You can work directly with the `MPDDatabaseReader` class by replacing `UniversalProjectReader`
with `MPDDatabaseReader`. This provides access to additional options, as described below.

#### Setting the database connection

#### Selecting a project
If the MPD file contains multiple projects, you can retrieve details of the available
projects using the `listProjects` method. This returns a map of project IDs and project names.
The sample code below illustrates how to retrieve this list of projects, and select the specific
project that you want to read. In this case we read each project in the file in turn.

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


