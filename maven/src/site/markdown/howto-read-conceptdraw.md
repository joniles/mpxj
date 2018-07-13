## How To: Read ConceptDraw PROJECT files
 ConceptDraw PROJECT writes CDPX, CPDZ and CPDTZ files.

### Reading PPX files
The simplest way to read a  CDPX, CPDZ or CPDTZ file is to use the `UniversalProjectReader`:

```java
import net.sf.mpxj.ProjectFile;
import net.sf.mpxj.reader.UniversalProjectReader;

...

UniversalProjectReader reader = new UniversalProjectReader();
ProjectFile project = reader.read("my-sample.cdpx");
```

You can work directly with the `PhoenixReader` by replacing `UniversalProjectReader` with `ConceptDrawProjectReader`, although this offers no particular advantage as there are no additional configuration settings available on the `ConceptDrawProjectReader` class.
