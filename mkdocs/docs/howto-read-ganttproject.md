# How To: Read GanttProject files
GanttProject writes schedule data to GAN files (which are actually just XML files).

## Reading GAN files
The simplest way to read a GAN file is to use the `UniversalProjectReader`:

=== "Java"
	```java
	package org.mpxj.howto.read;
	
	import org.mpxj.ProjectFile;
	import org.mpxj.reader.UniversalProjectReader;
	
	public class GanttProject
	{
		public void read() throws Exception
		{
			UniversalProjectReader reader = new UniversalProjectReader();
			ProjectFile project = reader.read("my-sample.gan");
		}
	}
	
	```

=== "C#"
	```c#
	using MPXJ.Net;
	
	namespace MPXJ.Samples.HowToRead;
	
	public class GanttProject
	{
	 	public void Read()
	 	{
		  	var reader = new UniversalProjectReader();
		  	var project = reader.Read("my-sample.gan");
	 	}
	}
	
	```

You can work directly with the `GanttProjectReader` by replacing
`UniversalProjectReader` with `GanttProjectReader`, although this offers no
particular advantage as there are no additional configuration settings available
on the `GanttProjectReader` class.
