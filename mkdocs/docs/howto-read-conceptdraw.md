# How To: Read ConceptDraw PROJECT files
ConceptDraw PROJECT writes CDPX, CPDZ and CPDTZ files.

## Reading  CDPX, CPDZ and CPDTZ files
The simplest way to read a  CDPX, CPDZ or CPDTZ file is to use the
`UniversalProjectReader`:


=== "Java"
	```java
	package org.mpxj.howto.read;
	
	import org.mpxj.ProjectFile;
	import org.mpxj.reader.UniversalProjectReader;
	
	public class ConceptDraw
	{
		public void read() throws Exception
		{
			UniversalProjectReader reader = new UniversalProjectReader();
			ProjectFile project = reader.read("my-sample.cdpx");
		}
	}
	```

=== "C#"
	```c#
	using MPXJ.Net;
	
	namespace MPXJ.Samples.HowToRead;
	
	public class ConceptDraw
	{
	 	public void Read()
	 	{
		  	var reader = new UniversalProjectReader();
		  	var project = reader.Read("my-sample.cdpx");
	 	}
	}
	```

You can work directly with the `ConceptDrawProjectReader` by replacing
`UniversalProjectReader` with `ConceptDrawProjectReader`, although this offers
no particular advantage as there are no additional configuration settings
available on the `ConceptDrawProjectReader` class.
