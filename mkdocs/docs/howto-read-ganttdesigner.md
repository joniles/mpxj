# How To: Read Gantt Designer files
Gantt Designer writes schedule data to GNT files.

## Reading GNT files
The simplest way to read a GNT file is to use the `UniversalProjectReader`:

=== "Java"
	```java
	package org.mpxj.howto.read;
	
	import org.mpxj.ProjectFile;
	import org.mpxj.reader.UniversalProjectReader;
	
	public class GanttDesigner
	{
		public void read() throws Exception
		{
			UniversalProjectReader reader = new UniversalProjectReader();
			ProjectFile project = reader.read("my-sample.gnt");
		}
	}
	```

=== "C#"
	```c#
	using MPXJ.Net;
	
	namespace MPXJ.Samples.HowToRead;
	
	public class GanttDesigner
	{
	 	public void Read()
	 	{
		  	var reader = new UniversalProjectReader();
		  	var project = reader.Read("my-sample.gnt");
	 	}
	}
	
	```

You can work directly with the `GanttDesignerReader` by replacing
`UniversalProjectReader` with `GanttDesignerReader`, although this offers no
particular advantage as there are no additional configuration settings available
on the `GanttDesignerReader` class.
