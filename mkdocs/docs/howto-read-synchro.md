# How To: Read Synchro Scheduler files
Synchro Scheduler writes SP files.

## Reading SP files
The simplest way to read an SP file is to use the `UniversalProjectReader`:

=== "Java"
	```java
	package org.mpxj.howto.read;
	
	import org.mpxj.ProjectFile;
	import org.mpxj.reader.UniversalProjectReader;
	
	public class Synchro
	{
		public void read() throws Exception
		{
			UniversalProjectReader reader = new UniversalProjectReader();
			ProjectFile project = reader.read("my-sample.sp");
		}
	}
	```

=== "C#"
	```c#
	using MPXJ.Net;
	
	namespace MPXJ.Samples.HowToRead;
	
	public class Synchro
	{
	 	public void Read()
	 	{
		  	var reader = new UniversalProjectReader();
		  	var project = reader.Read("my-sample.sp");
	 	}
	}
	```

You can work directly with the `SynchroReader` by replacing
`UniversalProjectReader` with `SynchroReader`, although this offers no
particular advantage as there are no additional configuration settings available
on the `SynchroReader` class.
