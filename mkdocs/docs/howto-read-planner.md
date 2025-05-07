# How To: Read Planner files
Gnome Planner is a popular open source planning tool which writes its own
XML files.

## Reading Planner files
The simplest way to read a Planner file is to use the `UniversalProjectReader`:

=== "Java"
	```java
	package org.mpxj.howto.read;
	
	import org.mpxj.ProjectFile;
	import org.mpxj.reader.UniversalProjectReader;
	
	public class Planner
	{
		public void read() throws Exception
		{
			UniversalProjectReader reader = new UniversalProjectReader();
			ProjectFile project = reader.read("my-sample.xml");
		}
	}
	```

=== "C#"
	```c#
	using MPXJ.Net;
	
	namespace MPXJ.Samples.HowToRead;
	
	public class Planner
	{
	 	public void Read()
	 	{
		  	var reader = new UniversalProjectReader();
		  	var project = reader.Read("my-samplexml");
	 	}
	}
	```

You can work directly with the `PlannerReader` by replacing
`UniversalProjectReader` with `PlannerReader`, although this offers no
particular advantage as there are no additional configuration settings available
on the `PlannerReader` class.
