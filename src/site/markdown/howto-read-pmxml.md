## How To: Read Primavera PMXML files
Primavera P6 can export data in an XML format known as PMXML.

### Reading PMXML files
The simplest way to read a PMXML file is to use the `UniversalProjectReader`:

```java
import net.sf.mpxj.ProjectFile;
import net.sf.mpxj.reader.UniversalProjectReader;

...

UniversalProjectReader reader = new UniversalProjectReader();
ProjectFile project = reader.read("my-sample.xml");
```

You can work directly with the `PrimaveraPMFileReader` by replacing `UniversalProjectReader` with `PrimaveraPMFileReader`, although this offers no particular advantage as there are no additional configuration settings available on the `PrimaveraPMFileReader` class.
