# How To: Write PMXML files

The XML file format supported by Primavera P6 for import and export is known as
PMXML.

The sample code below illustrates how to write data to a PMXML file.

```java
import net.sf.mpxj.ProjectFile;
import net.sf.mpxj.primavera.PrimaveraPMFileWriter;

// ...

PrimaveraPMFileWriter writer = new PrimaveraPMFileWriter();
writer.write(projectFile, outputFileName);
```

## Using PrimaveraPMFileWriter

### Baselines

By default baselines are not written to PMXML files. If the `ProjectFile`
instance you are writing contains a baseline, this can be included in the PMXML
file by calling the `setWriteBaselines` method as shown below.

```java
import net.sf.mpxj.ProjectFile;
import net.sf.mpxj.primavera.PrimaveraPMFileWriter;

// ...

PrimaveraPMFileWriter writer = new PrimaveraPMFileWriter();
writer.setWriteBaselines(true);
writer.write(projectFile, outputFileName);
```


