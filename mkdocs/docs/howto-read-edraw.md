# How To: Read Edraw Project EDPX files
Edraw Project is an easy to use tool which allows users to rapidly create and
maintain professional looking project plans.

## Reading Edraw Project files
The simplest way to read an Edraw Project file is to use the
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
			ProjectFile project = reader.read("my-sample.edpx");
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
		  	var project = reader.Read("my-sample.edpx");
	 	}
	}
	```

You can work directly with the `EdrawProjectReader` by replacing
`UniversalProjectReader` with `EdrawProjectReader`, although this offers no
particular advantage as there are no additional configuration settings available
on the `EdrawProjectReader` class.
