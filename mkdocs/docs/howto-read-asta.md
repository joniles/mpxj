# How To: Read Asta Powerproject and Easyproject files
Asta Powerproject and Asta Easyproject both use PP files.

## Reading PP files
The simplest way to read a PP file is to use the `UniversalProjectReader`:

```java
import net.sf.mpxj.ProjectFile;
import net.sf.mpxj.reader.UniversalProjectReader;

// ...

UniversalProjectReader reader = new UniversalProjectReader();
ProjectFile project = reader.read("my-sample.pp");
```

You can work directly with the `AstaFileReader` by replacing
`UniversalProjectReader` with `AstaFileReader`, although this offers no
particular advantage as there are no additional configuration settings available
on the `AstaFileReader` class.
