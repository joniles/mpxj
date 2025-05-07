# How To: Write MPX files
Versions of Microsoft Project up to Project 98 could read and write MPX files as
a data interchange format. Versions of Project after Project 98 until Project
2010 can only read MPX files. Versions of Microsoft Project after 2010 cannot
read MPX files. Other third party project planning applications continue to use
MPX as a data interchange format.

## Writing MPX files
The sample code below illustrates how to write data to an MPX file.

=== "Java"
	```java
	package org.mpxj.howto.write;
	
	import org.mpxj.ProjectFile;
	import org.mpxj.writer.FileFormat;
	import org.mpxj.writer.UniversalProjectWriter;
	
	public class MPX
	{
		public void write(ProjectFile project, String fileName) throws Exception
		{
			new UniversalProjectWriter(FileFormat.MPX).write(project, fileName);
		}
	}
	```

=== "C#"
	```c#
	using MPXJ.Net;
	
	namespace MPXJ.Samples.HowToWrite;
	
	public class MPX
	{
		public void Write(ProjectFile project, string fileName)
		{
			new UniversalProjectWriter(FileFormat.MPX).Write(project, fileName);
		}
	}
	```

## Using MPXWriter
If required, the `MPXWriter` class can be used directly, which provides access
to additional options, as described below.

### Locale
The MPX file format is actually locale specific, so in the example code below we
can see that the writer is being asked to produce a file suitable for reading
with a localized German version of Microsoft Project.


=== "Java"
	```java
	package org.mpxj.howto.write;
	
	import org.mpxj.ProjectFile;
	import org.mpxj.mpx.MPXWriter;
	
	import java.util.Locale;
	
	public class MPXLocale
	{
		public void write(ProjectFile project, String fileName) throws Exception
		{
			MPXWriter writer = new MPXWriter();
			writer.setLocale(Locale.GERMAN);
			writer.write(project, fileName);
		}
	}
	```

=== "C#"
	```c#
	using System.Globalization;
	using MPXJ.Net;
	
	namespace MPXJ.Samples.HowToWrite;
	
	public class MPXLocale
	{
		public void Write(ProjectFile project, string fileName)
		{
			var writer = new MPXWriter();
			writer.Culture = CultureInfo.GetCultureInfo("de");
			writer.Write(project, fileName);
		}
	}
	```

The locales supported by the MPX writer class can be retrieved using
the following call:

=== "Java"
	```java
	MPXWriter.getSupportedLocales()
	```

=== "C#"
	```c#
	MPXWriter.SupportedCultures
	```

### Locale Defaults
By default the MPX writer ignores the date, time, number and currency formats
specified in the project properties and instead uses the defaults from the
specified locale. Calling the `setUseLocaleDefaults` method and passing `false`
ensures that the information present in the project properties is used instead
of the locale defaults. This is illustrated in the sample code below:

=== "Java"
	```java
	package org.mpxj.howto.write;
	
	import org.mpxj.ProjectFile;
	import org.mpxj.mpx.MPXWriter;
	
	import java.util.Locale;
	
	public class MPXLocaleDefaults
	{
		public void write(ProjectFile project, String fileName) throws Exception
		{
			MPXWriter writer = new MPXWriter();
			writer.setLocale(Locale.GERMAN);
			writer.setUseLocaleDefaults(false);
			writer.write(project, fileName);
		}
	}
	```

=== "C#"
	```c#
	using System.Globalization;
	using MPXJ.Net;
	
	namespace MPXJ.Samples.HowToWrite;
	
	public class MPXLocaleDefaults
	{
		public void Write(ProjectFile project, string fileName)
		{
			var writer = new MPXWriter();
			writer.Culture = CultureInfo.GetCultureInfo("de");
			writer.UseCultureDefaults = false;
			writer.Write(project, fileName);
		}
	}
	```
