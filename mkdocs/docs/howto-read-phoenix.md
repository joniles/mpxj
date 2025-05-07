# How To: Read Phoenix Project Manager files
Phoenix Project Manager uses PPX files.

## Reading PPX files
The simplest way to read a PPX file is to use the `UniversalProjectReader`:

=== "Java"
	```java
	package org.mpxj.howto.read;
	
	import org.mpxj.ProjectFile;
	import org.mpxj.reader.UniversalProjectReader;
	
	public class Phoenix
	{
		public void read() throws Exception
		{
			UniversalProjectReader reader = new UniversalProjectReader();
			ProjectFile project = reader.read("my-sample.ppx");
		}
	}
	```

=== "C#"
	```c#
	using MPXJ.Net;
	
	namespace MPXJ.Samples.HowToRead;
	
	public class Phoenix
	{
	 	public void Read()
	 	{
		  	var reader = new UniversalProjectReader();
		  	var project = reader.Read("my-sample.ppx");
	 	}
	}
	```

You can work directly with the `PhoenixReader` by replacing
`UniversalProjectReader` with `PhoenixReader`, although this offers no
particular advantage as there are no additional configuration settings available
on the `PhoenixReader` class.
