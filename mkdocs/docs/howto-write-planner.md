# How To: Write Planner files
[Gnome Planner](https://wiki.gnome.org/Apps/Planner) is a simple cross platform planning tool. MPXJ can be used
to write a schedule as a Planner file, which the Gnome Planner application
can open.

## Writing Planner files
The sample code below illustrates how to write data to a Planner file.

```java
import net.sf.mpxj.ProjectFile;
import net.sf.mpxj.writer.FileFormat;
import net.sf.mpxj.writer.UniversalProjectWriter;

// ...

new UniversalProjectWriter(FileFormat.PLANNER).write(project, fileName);
```

## Using PlannerWriter
If required, the `PlannerWriter` class can be used directly, which provides
access to additional options, as described below.

### Charset
The character set used to write a Planner file can be specified using the
`setCharset` method, as illustrated below.

```java
import net.sf.mpxj.ProjectFile;
import net.sf.mpxj.planner.PlannerWriter;

// ...

PlannerWriter writer = new PlannerWriter();
writer.setCharset(Charset.forName("GB2312"));
writer.write(projectFile, outputFileName);
```
