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
