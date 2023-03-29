# How To: Write SDEF files

SDEF is the Standard Data Exchange Format, as defined by the USACE
(United States Army Corps of Engineers). SDEF is a fixed column format text
file, used to import a project schedule up into the QCS (Quality Control
System) software from USACE. The
specification for the file format can be found
[here](https://www.publications.usace.army.mil/Portals/76/Publications/EngineerRegulations/ER_1-1-11.pdf).

The sample code below illustrates how to write data to an SDEF file.

```java
import net.sf.mpxj.ProjectFile;
import net.sf.mpxj.sdef.SDEFWriter;

// ...

SDEFWriter writer = new SDEFWriter();
writer.write(projectFile, outputFileName);
```


## Using SDEFWriter

### Charset
By default SDEF files are written using the `US_ASCII` charset. The `setCharset`
method on the `SDEFWriter` class can be used to change this if required:

```java
import java.nio.charset.StandardCharsets;

import net.sf.mpxj.ProjectFile;
import net.sf.mpxj.sdef.SDEFWriter;

// ...

SDEFWriter writer = new SDEFWriter();
writer.setCharset(StandardCharsets.UTF_8);
writer.write(projectFile, outputFileName);
```