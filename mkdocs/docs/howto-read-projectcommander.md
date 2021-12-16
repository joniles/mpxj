# How To: Read Project Commander files
Project Commander is a planning tool for Windows which writes its own PC file
format.

## Reading Project Commander files
The simplest way to read a Project Commander file is to use the
`UniversalProjectReader`:

```java
import net.sf.mpxj.ProjectFile;
import net.sf.mpxj.reader.UniversalProjectReader;

// ...

UniversalProjectReader reader = new UniversalProjectReader();
ProjectFile project = reader.read("my-sample.pc");
```

You can work directly with the `ProjectCommanderReader` by replacing
`UniversalProjectReader` with `ProjectCommanderReader`, although this offers no
particular advantage as there are no additional configuration settings available
on the `ProjectCommanderReader` class.
