# How To: Read ProjectLibre files
ProjectLibre writes schedule data to POD files. MPXJ can read POD files written
by ProjectLibre version 1.5.5 and later versions.

## Reading POD files
The simplest way to read a POD file is to use the `UniversalProjectReader`:

=== "Java"
	```java
	package org.mpxj.howto.read;
	
	import org.mpxj.ProjectFile;
	import org.mpxj.reader.UniversalProjectReader;
	
	public class ProjectLibre
	{
		public void read() throws Exception
		{
			UniversalProjectReader reader = new UniversalProjectReader();
			ProjectFile project = reader.read("my-sample.pod");
		}
	}
	```

=== "C#"
	```c#
	using MPXJ.Net;
	
	namespace MPXJ.Samples.HowToRead;
	
	public class ProjectLibre
	{
	 	public void Read()
	 	{
		  	var reader = new UniversalProjectReader();
		  	var project = reader.Read("my-sample.pod");
	 	}
	}
	```

You can work directly with the `ProjectLibreReader` by replacing
`UniversalProjectReader` with `ProjectLibreReader`, although this offers no
particular advantage as there are no additional configuration settings available
on the `ProjectLibreReader` class.
