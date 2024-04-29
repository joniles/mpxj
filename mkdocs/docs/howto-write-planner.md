# How To: Write Planner files

[Gnome Planner](https://wiki.gnome.org/Apps/Planner) is a simple cross platform planning tool. MPXJ can be used
to write a schedule as a Planner file, which the Gnome Planner application
can open.

The sample code below illustrates how to write data to a Planner file.

```java
import net.sf.mpxj.ProjectFile;
import net.sf.mpxj.planner.PlannerWriter;

// ...

PlannerWriter writer = new PlannerWriter();
writer.write(projectFile, outputFileName);
```
