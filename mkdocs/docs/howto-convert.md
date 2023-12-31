# Converting Files

To convert project data between different formats you read the source 
data using an appropriate Reader class, then write the data using a Writer
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
package org.mpxj.howto.convert;

import net.sf.mpxj.ProjectFile;
import net.sf.mpxj.reader.UniversalProjectReader;
import net.sf.mpxj.writer.ProjectWriter;
import net.sf.mpxj.writer.ProjectWriterUtility;

public class ConvertUniversal
{
   public void convert(String inputFile, String outputFile) throws Exception
   {
      UniversalProjectReader reader = new UniversalProjectReader();
      ProjectFile projectFile = reader.read(inputFile);

      ProjectWriter writer = ProjectWriterUtility.getProjectWriter(outputFile);
      writer.write(projectFile, outputFile);
   }
}
```

If you already know the file types you are converting between,
you can use the specific Reader and Writer classes, as shown below.

```java
package org.mpxj.howto.convert;

import net.sf.mpxj.ProjectFile;
import net.sf.mpxj.mpp.MPPReader;
import net.sf.mpxj.mpx.MPXWriter;
import net.sf.mpxj.reader.ProjectReader;
import net.sf.mpxj.writer.ProjectWriter;

public class ConvertMppToMpx
{
   public void convert(String inputFile, String outputFile) throws Exception
   {
      ProjectReader reader = new MPPReader();
      ProjectFile projectFile = reader.read(inputFile);

      ProjectWriter writer = new MPXWriter();
      writer.write(projectFile, outputFile);
   }
}
```




