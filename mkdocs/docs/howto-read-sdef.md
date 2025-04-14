# How To: Read SDEF files
The Standard Data Exchange Format (SDEF) is the US Army Corps of Engineers
standard format for exchanging schedule data between systems. The definition of
this format can be found 
[here](https://www.publications.usace.army.mil/Portals/76/Publications/EngineerRegulations/ER_1-1-11.pdf).

## Reading SDEF files
The simplest way to read an SDEF file is to use the `UniversalProjectReader`:

=== "Java"
	```java
	package org.mpxj.howto.read;
	
	import org.mpxj.ProjectFile;
	import org.mpxj.reader.UniversalProjectReader;
	
	public class SDEF
	{
		public void read() throws Exception
		{
			UniversalProjectReader reader = new UniversalProjectReader();
			ProjectFile project = reader.read("my-sample.sdef");
		}
	}
	```

=== "C#"
	```c#	
	using MPXJ.Net;
	
	namespace MPXJ.Samples.HowToRead;

	public class SDEF
	{
		public void Read()
		{
			var reader = new UniversalProjectReader();
			var project = reader.Read("my-sample.sdef");
		}
	}
	```

## Using SDEFReader
You can work directly with the `SDEFReader` class by replacing
`UniversalProjectReader` with `SDEFReader`. This provides access to additional
options, as described below.

### Ignore Errors
By default MPXJ will ignore errors when parsing attributes from an SDEF file.
This behavior is controlled using the `setIgnoreErrors` method. The example
below illustrates how we can force the `SDEFReader` to report
errors encountered when reading a file:

=== "Java"
	```java
	package org.mpxj.howto.read;
	
	import org.mpxj.ProjectFile;
	import org.mpxj.sdef.SDEFReader;
	
	public class SDEFIgnoreErrors
	{
		public void read() throws Exception
		{
			SDEFReader reader = new SDEFReader();
			reader.setIgnoreErrors(false);
			ProjectFile project = reader.read("my-sample.sdef");
		}
	}
	```

=== "C#"
	```c#
	using MPXJ.Net;
	
	namespace MPXJ.Samples.HowToRead;
	
	public class SDEFIgnoreErrors
	{
	 	public void Read()
	 	{
		  	var reader = new SDEFReader();
		  	reader.IgnoreErrors = false;
		  	var project = reader.Read("my-sample.sdef");
	 	}
	}
	```

Note that if errors are ignored when reading a file, the ignored errors
are available by using the `ProjectFile.getIgnoredErrors()` method.
