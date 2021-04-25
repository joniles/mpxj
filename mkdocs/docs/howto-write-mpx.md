# How To: Write MPX files

Versions of Microsoft Project up to Project 98 could read and write MPX files as
a data interchange format. Versions of Project after Project 98 until Project
2010 can only read MPX files. Versions of Microsoft Project after 2010 cannot
read MPX files. Other third party project planning applications continue to use
MPX as a data interchange format.

The sample code below illustrates how to write data to an MPX file.

```java
import net.sf.mpxj.ProjectFile;
import net.sf.mpxj.mpx.MPXWriter;

// ...

MPXWriter writer = new MPXWriter();
writer.write(projectFile, outputFileName);
```
