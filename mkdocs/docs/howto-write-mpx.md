# How To: Write MPX files

Versions of Microsoft Project up to Project 98 could read and write MPX files as
a data interchange format. Versions of Project after Project 98 until Project
2010 can only read MPX files. Versions of Microsoft Project after 2010 cannot
read MPX files. Other third party project planning applications continue to use
MPX as a data interchange format.

The sample code below illustrates how to write data to an MPX file.

```java
import net.sf.mpxj.ProjectFile;
import net.sf.mpxj.writer.FileFormat;
import net.sf.mpxj.writer.UniversalProjectWriter;

// ...

new UniversalProjectWriter().withFormat(FileFormat.MPX).write(project, fileName);
```

## Using MPXWriter

If required, the `MPXWriter` class can be used directly, which provides access
to additional options, as described below.

### Locale

The MPX file format is actually locale specific, so in the example code below we
can see that the writer is being asked to produce a file suitable for reading
with a localized German version of Microsoft Project.


```java
import net.sf.mpxj.ProjectFile;
import net.sf.mpxj.mpx.MPXWriter;

// ...

MPXWriter writer = new MPXWriter();
writer.setLocale(Locale.GERMAN);
writer.write(projectFile, outputFileName);
```

The locales supported by the MPX writer class can be retrieved using
the following call:

```java
MPXWriter.getSupportedLocales()
```

### Locale Defaults

By default the MPX writer ignores the date, time, number and currency formats
specified in the project properties and instead uses the defaults from the
specified locale. Calling the `setUseLocaleDefaults` method and passing `false`
ensures that the information present in the project properties is used instead
of the locale defaults. This is illustrated in the sample code below:

```java
import net.sf.mpxj.ProjectFile;
import net.sf.mpxj.mpx.MPXWriter;

// ...

MPXWriter writer = new MPXWriter();
writer.setLocale(Locale.GERMAN);
writer.setUseLocaleDefaults(false);
writer.write(projectFile, outputFileName);
```
