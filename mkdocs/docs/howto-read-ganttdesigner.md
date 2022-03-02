# How To: Read Gantt Designer files
Gantt Designer writes schedule data to GNT files.

## Reading GNT files
The simplest way to read a GNT file is to use the `UniversalProjectReader`:

```java
import net.sf.mpxj.ProjectFile;
import net.sf.mpxj.reader.UniversalProjectReader;

// ...

UniversalProjectReader reader = new UniversalProjectReader();
ProjectFile project = reader.read("my-sample.gnt");
```

You can work directly with the `GanttDesignerReader` by replacing
`UniversalProjectReader` with `GanttDesignerReader`, although this offers no
particular advantage as there are no additional configuration settings available
on the `GanttDesignerReader` class.
