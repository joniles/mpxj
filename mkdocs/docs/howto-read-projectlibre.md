# How To: Read ProjectLibre files
ProjectLibre writes schedule data to POD files. MPXJ can read POD files written
by ProjectLibre version 1.5.5 and later versions.

## Reading POD files
The simplest way to read a POD file is to use the `UniversalProjectReader`:

```java
import net.sf.mpxj.ProjectFile;
import net.sf.mpxj.reader.UniversalProjectReader;

// ...

UniversalProjectReader reader = new UniversalProjectReader();
ProjectFile project = reader.read("my-sample.pod");
```

You can work directly with the `ProjectLibreReader` by replacing
`UniversalProjectReader` with `ProjectLibreReader`, although this offers no
particular advantage as there are no additional configuration settings available
on the `ProjectLibreReader` class.
