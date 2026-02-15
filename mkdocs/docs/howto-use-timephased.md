# How To: Use Timephased Data

As you may have seen elsewhere in this documentation, the preferred way to read
from most sources of schedule data is to use the `UniversalProjectReader`:

=== "Java"
	```java
	package org.mpxj.howto.use.universal;
	
	import org.mpxj.ProjectFile;
	import org.mpxj.reader.ProjectReader;
	import org.mpxj.reader.UniversalProjectReader;
	
	public class SimpleExample
	{
		public void process() throws Exception
		{
			ProjectReader reader = new UniversalProjectReader();
			ProjectFile project = reader.read("example.mpp");
		}
	}
	```
=== "C#"
	```c#
	namespace MpxjSamples.HowToUse.Universal;

	using MPXJ.Net;
		 
	public class SimpleExample
	{
		 public void Process()
		 {
			  var reader = new UniversalProjectReader();
			  var project = reader.Read("example.mpp");
		 }
	}
	```

