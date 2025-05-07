# How To: Read Deltek Open Plan BK3 files
Deltek Open Plan is a planning tool for Windows which can store schedule
data in a variety of databases, and export schedules to BK3 files.

## Reading Open Plan files
The simplest way to read an Open Plan file is to use the
`UniversalProjectReader`:

=== "Java"
	```java
	package org.mpxj.howto.read;
	
	import org.mpxj.ProjectFile;
	import org.mpxj.reader.UniversalProjectReader;
	
	public class OpenPlan
	{
		public void read() throws Exception
		{
			UniversalProjectReader reader = new UniversalProjectReader();
			ProjectFile project = reader.read("my-sample.bk3");
		}
	}
	```

=== "C#"
	```c#
	using MPXJ.Net;
	
	namespace MPXJ.Samples.HowToRead;
	
	public class OpenPlan
	{
	 	public void Read()
	 	{
		  	var reader = new UniversalProjectReader();
		  	var project = reader.Read("my-sample.bk3");
	 	}
	}
	```

You can work directly with the `OpenPlanReader` by replacing
`UniversalProjectReader` with `OpenPlanReader`, although this offers no
particular advantage as there are no additional configuration settings available
on the `OpenPlanReader` class.
