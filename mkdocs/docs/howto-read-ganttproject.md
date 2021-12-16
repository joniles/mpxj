# How To: Read GanttProject files
GanttProject writes schedule data to GAN files (which are actually just XML files).

## Reading GAN files
The simplest way to read a GAN file is to use the `UniversalProjectReader`:

```java
import net.sf.mpxj.ProjectFile;
import net.sf.mpxj.reader.UniversalProjectReader;

// ...

UniversalProjectReader reader = new UniversalProjectReader();
ProjectFile project = reader.read("my-sample.gan");
```

You can work directly with the `GanttProjectReader` by replacing
`UniversalProjectReader` with `GanttProjectReader`, although this offers no
particular advantage as there are no additional configuration settings available
on the `GanttProjectReader` class.
