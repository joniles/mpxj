# Converting Files

To convert project data between different formats you read the source 
data using an appropriate Reader class, the write the data using a Writer
class which matches the format you want to convert to.

MPXJ can do a lot of the work for you, as the example below illustrates. The
`UniversalProjectReader` will detect the type of schedule being read and handle
it accordingly. The `ProjectWriterUtility` will use the extension of the output
file to determine the type of file written.

The extensions recognised by the `ProjectWriterUtility` class are:

* MPX
* XML (writes an MSPDI file)
* PMXML
* PLANNER
* JSON
* SDEF

```java
import net.sf.mpxj.ProjectFile;
import net.sf.mpxj.reader.UniversalProjectReader;
import net.sf.mpxj.writer.ProjectWriter;
import net.sf.mpxj.writer.ProjectWriterUtility;

...

UniversalProjectReader reader = new UniversalProjectReader();
ProjectFile projectFile = reader.read(inputFile);

ProjectWriter writer = ProjectWriterUtility.getProjectWriter(outputFile);
writer.write(projectFile, outputFile);
```

If you already know the file types you are converting between,
you can use the specific Reader and Writer classes, as shown below.

```java
import net.sf.mpxj.ProjectFile;
import net.sf.mpxj.reader.ProjectReader;
import net.sf.mpxj.writer.ProjectWriter;
import net.sf.mpxj.mpp.MPPReader;
import net.sf.mpxj.mpx.MPXWriter;
...

ProjectReader reader = new MPPReader();
ProjectFile projectFile = reader.read(inputFile);

ProjectWriter writer = new MPXWriter();
writer.write(projectFile, outputFile);
```




