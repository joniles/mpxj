# How To: Read Schedule Grid files
Schedule grid files are produced when a schedule is exported from Sage 100
Contractor.

## Reading Schedule Grid files
The simplest way to read a schedule grid file is to use the
`UniversalProjectReader`:

```java
import net.sf.mpxj.ProjectFile;
import net.sf.mpxj.reader.UniversalProjectReader;

// ...

UniversalProjectReader reader = new UniversalProjectReader();
ProjectFile project = reader.read("my-sample.schedule_grid");
```

## Using SageReader
You can work directly with the `SageReader` class by replacing
`UniversalProjectReader` with `SageReader`. This provides access to additional
options, as described below.

### Ignore Errors
By default MPXJ will ignore errors when parsing attributes from a Schedule Grid file.
This behavior is controlled using the `setIgnoreErrors` method. The example
below illustrates how we can force the `SageReader` to report
errors encountered when reading a file:

```java
import net.sf.mpxj.ProjectFile;
import net.sf.mpxj.sage.SageReader;

// ...

SageReader reader = new SageReader();

reader.setIgnoreErrors(false);
ProjectFile project = reader.read("my-sample.schedule_grid");
```

Note that if errors are ignored when reading a file, the ignored errors
are available by using the `ProjectFile.getIgnoredErrors()` method.
