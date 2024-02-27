# How To: Deltek Open Plan BK3 files
Deltek Open Plan is a planning tool for Windows which can store schedule
data in a variety of databases, and export schedules to BK3 files.

## Reading Open Plan files
The simplest way to read an Open Plan file is to use the
`UniversalProjectReader`:

```java
import net.sf.mpxj.ProjectFile;
import net.sf.mpxj.reader.UniversalProjectReader;

// ...

UniversalProjectReader reader = new UniversalProjectReader();
ProjectFile project = reader.read("my-sample.bk3");
```

You can work directly with the `OpenPlanReader` by replacing
`UniversalProjectReader` with `OpenPlanReader`, although this offers no
particular advantage as there are no additional configuration settings available
on the `OpenPlanReader` class.
