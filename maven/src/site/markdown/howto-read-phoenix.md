## How To: Read Phoenix Project Manager files
Phoenix Project Manager uses PPX files.

### Reading PPX files
The simplest way to read a PPX file is to use the `UniversalProjectReader`:

```java
import net.sf.mpxj.ProjectFile;
import net.sf.mpxj.reader.UniversalProjectReader;

...

UniversalProjectReader reader = new UniversalProjectReader();
ProjectFile project = reader.read("my-sample.ppx");
```

You can work directly with the `PhoenixReader` by replacing `UniversalProjectReader` with `PhoenixReader`, although this offers no particular advantage as there are no additional configuration settings available on the `PhoenixReader` class.
