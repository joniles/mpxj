# How To: Write PMXML files
The XML file format supported by Primavera P6 for import and export is known as
PMXML.

## Writing PMXML files
The sample code below illustrates how to write data to a PMXML file.

```java
package org.mpxj.howto.write;

import net.sf.mpxj.ProjectFile;
import net.sf.mpxj.writer.FileFormat;
import net.sf.mpxj.writer.UniversalProjectWriter;

public class PMXML
{
   public void write(ProjectFile project, String fileName) throws Exception
   {
      new UniversalProjectWriter(FileFormat.PMXML).write(project, fileName);
   }
}
```

## Using PrimaveraPMFileWriter
If required, the `PrimaveraPMFileWriter` class can be used directly, which
provides access to additional options, as described below.

### Baselines
By default baselines are not written to PMXML files. If the `ProjectFile`
instance you are writing contains a baseline, this can be included in the PMXML
file by calling the `setWriteBaselines` method as shown below.

```java
package org.mpxj.howto.write;

import net.sf.mpxj.ProjectFile;
import net.sf.mpxj.primavera.PrimaveraPMFileWriter;

public class PMXMLBaselines
{
   public void write(ProjectFile project, String fileName) throws Exception
   {
      PrimaveraPMFileWriter writer = new PrimaveraPMFileWriter();
      writer.setWriteBaselines(true);
      writer.write(project, fileName);
   }
}
```


