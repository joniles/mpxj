## How To: Read Planner files
Merlin Planner is a Mac application. MPXJ provides experimental support for reading some Merlin Planner files.
The Merlin file format does not necessarily contain a full set of start and finish dates for each task.
Merlin calculates these dates when it displays a schedule. At the moment MPXJ lacks this functionality, so
you may not find start and finish dates for each task.



### Reading Merlin files
The simplest way to read a Merlin file is to use the `UniversalProjectReader`:

```java
import net.sf.mpxj.ProjectFile;
import net.sf.mpxj.reader.UniversalProjectReader;

...

UniversalProjectReader reader = new UniversalProjectReader();
ProjectFile project = reader.read("my-sample-merlin-project");
```

Note that on a Mac Merlin projects are not single files, but rather they are directories
containing multiple files (the Mac Finder normally hides this from you). When using MPXJ
to read a Merlin project you pass the directory name to the `UniversalProjectReader` class.

You can work directly with the `MerlinReader` by replacing `UniversalProjectReader` with `MerlinReader`,
although this offers no particular advantage as there are no additional configuration settings available on the `MerlinReader` class.
