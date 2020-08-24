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
A PMXML file can contain multiple projects. You can ask MPXJ to read all of the projects contained in the file:

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

There is a variant of the `readAll` method taking a Boolean argument which determines
if cross-project predecessors and successors are linked together. For example, if we
have Project A with Task 1, and Project B with Task 2 and Task 1 is a predecessor of Task 2,
when the `linkCrossProjectRelations` argument is set to true, Task 1 will appear as a predecessor
of Task 2 despite the two tasks appearing in different projects. If `linkCrossProjectRelations` is set
to false (or any of the other methods are used to read data from an PMXML file),
Task 1 and Task 2 will no be linked.

The sample below shows how `readAll` can be called with the `linkCrossProjectRelations`
argument set to true:

```java
import net.sf.mpxj.ProjectFile;
import net.sf.mpxj.primavera.PrimaveraPMFileReader;

...

PrimaveraPMFileReader reader = new PrimaveraPMFileReader();
InputStream is = new FileInputStream("my-sample.xml");
List<ProjectFile> files = reader.readAll(is, true);
```
