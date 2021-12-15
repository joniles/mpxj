# How To: Read TurboProject files
TurboProject writes schedule data to PEP files.

## Reading PEP files
The simplest way to read a PEP file is to use the `UniversalProjectReader`:

```java
import net.sf.mpxj.ProjectFile;
import net.sf.mpxj.reader.UniversalProjectReader;

// ...

UniversalProjectReader reader = new UniversalProjectReader();
ProjectFile project = reader.read("my-sample.pep");
```

You can work directly with the `TurboProjectReader` by replacing
`UniversalProjectReader` with `TurboProjectReader`, although this offers no
particular advantage as there are no additional configuration settings available
on the `TurboProjectReader` class.
