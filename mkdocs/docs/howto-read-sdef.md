# How To: Read SDEF files
The Standard Data Exchange Format (SDEF) is the US Army Corps of Engineers
standard format for exchanging schedule data between systems. The definition of
this format can be found 
[here](https://www.publications.usace.army.mil/Portals/76/Publications/EngineerRegulations/ER_1-1-11.pdf).

## Reading SDEF files
The simplest way to read an SDEF file is to use the `UniversalProjectReader`:

```java
import net.sf.mpxj.ProjectFile;
import net.sf.mpxj.reader.UniversalProjectReader;

...

UniversalProjectReader reader = new UniversalProjectReader();
ProjectFile project = reader.read("my-sample.sdef");
```

You can work directly with the `SDEFReader` by replacing
`UniversalProjectReader` with `SDEFReader`, although this offers no particular
advantage as there are no additional configuration settings available on the
`SDEFReader` class.
