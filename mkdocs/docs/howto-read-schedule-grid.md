## How To: Read Schedule Grid files
Schedule grid files are produced when a schedule is exported from Sage 100 Contractor.

### Reading Schedule Grid files
The simplest way to read a schedule grid file is to use the `UniversalProjectReader`:

```java
import net.sf.mpxj.ProjectFile;
import net.sf.mpxj.reader.UniversalProjectReader;

...

UniversalProjectReader reader = new UniversalProjectReader();
ProjectFile project = reader.read("my-sample.schedule_grid");
```

You can work directly with the `SageReader` by replacing `UniversalProjectReader` with `SageReader`, although this offers no particular advantage as there are no additional configuration settings available on the `SageReader` class.
