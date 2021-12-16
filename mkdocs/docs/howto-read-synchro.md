# How To: Read Synchro Scheduler files
Synchro Scheduler writes SP files.

## Reading SP files
The simplest way to read an SP file is to use the `UniversalProjectReader`:

```java
import net.sf.mpxj.ProjectFile;
import net.sf.mpxj.reader.UniversalProjectReader;

// ...

UniversalProjectReader reader = new UniversalProjectReader();
ProjectFile project = reader.read("my-sample.sp");
```

You can work directly with the `SynchroReader` by replacing
`UniversalProjectReader` with `SynchroReader`, although this offers no
particular advantage as there are no additional configuration settings available
on the `SynchroReader` class.
