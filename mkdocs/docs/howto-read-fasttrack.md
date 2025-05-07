# How To: Read FastTrack Schedule files
FastTrack Schedule writes schedule data to FTS files. Note that MPXJ has only
been tested with FTS files produced by FastTrack 10.

## Reading FTS files
The simplest way to read an FTS file is to use the `UniversalProjectReader`:

=== "Java"
	```java
	package org.mpxj.howto.read;
	
	import org.mpxj.ProjectFile;
	import org.mpxj.reader.UniversalProjectReader;
	
	public class FastTrack
	{
		public void read() throws Exception
		{
			UniversalProjectReader reader = new UniversalProjectReader();
			ProjectFile project = reader.read("my-sample.fts");
		}
	}
	```

=== "C#"
	```c#
	using MPXJ.Net;
	
	namespace MPXJ.Samples.HowToRead;
	
	public class FastTrack
	{
	 	public void Read()
	 	{
		  	var reader = new UniversalProjectReader();
		  	var project = reader.Read("my-sample.fts");
	 	}
	}
	```

You can work directly with the `FastTrackReader` by replacing
`UniversalProjectReader` with `FastTrackReader`, although this offers no
particular advantage as there are no additional configuration settings available
on the `FastTrackReader` class.
