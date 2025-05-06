# How To: Read TurboProject files
TurboProject writes schedule data to PEP files.

## Reading PEP files
The simplest way to read a PEP file is to use the `UniversalProjectReader`:

=== "Java"
	```java
	package org.mpxj.howto.read;
	
	import org.mpxj.ProjectFile;
	import org.mpxj.reader.UniversalProjectReader;
	
	public class TurboProject
	{
		public void read() throws Exception
		{
			UniversalProjectReader reader = new UniversalProjectReader();
			ProjectFile project = reader.read("my-sample.pep");
		}
	}
	```

=== "C#"
	```c#
	using MPXJ.Net;
	
	namespace MPXJ.Samples.HowToRead;
	
	public class TurboProject
	{
	 	public void Read()
	 	{
		  	var reader = new UniversalProjectReader();
		  	var project = reader.Read("my-sample.pep");
	 	}
	}
	```

You can work directly with the `TurboProjectReader` by replacing
`UniversalProjectReader` with `TurboProjectReader`, although this offers no
particular advantage as there are no additional configuration settings available
on the `TurboProjectReader` class.
