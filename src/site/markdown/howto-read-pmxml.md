## How To: Read Primavera PMXML files
Primavera P6 can export data in an XML format known as PMXML.

### Reading PMXML files
The simplest way to read a PMXML file is to use the `UniversalProjectReader`:

```java
import net.sf.mpxj.ProjectFile;
import net.sf.mpxj.reader.UniversalProjectReader;

...

UniversalProjectReader reader = new UniversalProjectReader();
ProjectFile project = reader.read("my-sample.xml");
```

### Using PrimaveraPMFileReader
You can work directly with the `PrimaveraPMFileReader` by replacing `UniversalProjectReader` with `PrimaveraPMFileReader`.
This provides access to additional options, as described below.

#### WBS is Full Path
Currently the WBS attribute of summary tasks (WBS entities in P6) will be a dot
separated hierarchy of all of the parent WBS attributes.
In this example, `root.wbs1.wbs2` is the WBS attribute for `wbs2` which has
the parents `root` and `wbs1`. To disabled this behaviour, and simply record
the code for the current WBS entry (in the example above `wbs2`) call the
`setWbsIsFullPath` method, passing in `false`, as illustrated below.  


```java
import net.sf.mpxj.ProjectFile;
import net.sf.mpxj.primavera.PrimaveraPMFileReader;

...

PrimaveraPMFileReader reader = new PrimaveraPMFileReader();
reader.setWbsIsFullPath(false);
```

#### Multiple Projects
A PMXML file can contain multiple projects. By default MPXJ reads the first non-external project
it finds in the file, otehrwise it defaults to the first project it finds.
You can however use MPXJ to list the projects contained in
a PMXML file, as shown below:

```java
import net.sf.mpxj.primavera.PrimaveraPMFileReader;

...

PrimaveraPMFileReader reader = new PrimaveraPMFileReader();
FileInputStream is = new FileInputStream("my-sample.xml");
Map<Integer, String> projects = reader.listProjects(is);
System.out.println("ID\tName");
for (Entry<Integer, String> entry : projects.entrySet())
{
   System.out.println(entry.getKey()+"\t"+entry.getValue());
}
```
The call to `listProjects` returns a `Map` whose key is the project ID,
and the values are project short names.

Once you have decided which of these projects you want to work with, you can
call `setProjectID` to tell the reader which project to open, as shown below.

```java
import net.sf.mpxj.ProjectFile;
import net.sf.mpxj.primavera.PrimaveraPMFileReader;

...

PrimaveraPMFileReader reader = new PrimaveraPMFileReader();
reader.setProjectID(123);
ProjectFile file = reader.read("my-sample.xml");
```

Alternatively you can ask MPXJ to read all of the projects contained in the file:

```java
import net.sf.mpxj.ProjectFile;
import net.sf.mpxj.primavera.PrimaveraPMFileReader;

...

PrimaveraPMFileReader reader = new PrimaveraPMFileReader();
InputStream is = new FileInputStream("my-sample.xml");
List<ProjectFile> files = reader.readAll(is);
```

The call to the `readAll` method returns a list of `ProjectFile` instances corresponding
to the projects in the PMXML file.

#### Link Cross-Project Relations
A PMXML file can contain multiple projects with relations between activities which span
those projects. By default these cross-project relations are ignored. However, if you set the
`linkCrossProjectRelations` reader attribute to `true`, MPXJ will attempt to
link these relations across projects: 

```java
import net.sf.mpxj.ProjectFile;
import net.sf.mpxj.primavera.PrimaveraPMFileReader;

...

PrimaveraPMFileReader reader = new PrimaveraPMFileReader();
reader.setLinkCrossProjectRelations(true);
InputStream is = new FileInputStream("my-sample.xml");
List<ProjectFile> files = reader.readAll(is);
```
