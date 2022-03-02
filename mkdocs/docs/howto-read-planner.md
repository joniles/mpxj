# How To: Read Planner files
Gnome Planner is a popular open source planning tool which writes its own
XML files.

## Reading Planner files
The simplest way to read a Planner file is to use the `UniversalProjectReader`:

```java
import net.sf.mpxj.ProjectFile;
import net.sf.mpxj.reader.UniversalProjectReader;

// ...

UniversalProjectReader reader = new UniversalProjectReader();
ProjectFile project = reader.read("my-sample.xml");
```

You can work directly with the `PlannerReader` by replacing
`UniversalProjectReader` with `PlannerReader`, although this offers no
particular advantage as there are no additional configuration settings available
on the `PlannerReader` class.
