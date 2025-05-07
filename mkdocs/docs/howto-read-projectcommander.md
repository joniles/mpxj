# How To: Read Project Commander files
Project Commander is a planning tool for Windows which writes its own PC file
format.

## Reading Project Commander files
The simplest way to read a Project Commander file is to use the
`UniversalProjectReader`:

=== "Java"
	```java
	package org.mpxj.howto.read;
	
	import org.mpxj.ProjectFile;
	import org.mpxj.reader.UniversalProjectReader;
	
	public class ProjectCommander
	{
		public void read() throws Exception
		{
			UniversalProjectReader reader = new UniversalProjectReader();
			ProjectFile project = reader.read("my-sample.pc");
		}
	}
	```

=== "C#"
	```c#
	using MPXJ.Net;
	
	namespace MPXJ.Samples.HowToRead;
	
	public class ProjectCommander
	{
	 	public void Read()
	 	{
		  	var reader = new UniversalProjectReader();
		  	var project = reader.Read("my-sample.pc");
	 	}
	}
	```

You can work directly with the `ProjectCommanderReader` by replacing
`UniversalProjectReader` with `ProjectCommanderReader`, although this offers no
particular advantage as there are no additional configuration settings available
on the `ProjectCommanderReader` class.
