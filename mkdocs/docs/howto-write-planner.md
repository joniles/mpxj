# How To: Write Planner files
[Gnome Planner](https://wiki.gnome.org/Apps/Planner) is a simple cross platform planning tool. MPXJ can be used
to write a schedule as a Planner file, which the Gnome Planner application
can open.

## Writing Planner files
The sample code below illustrates how to write data to a Planner file.

=== "Java"
	```java
	package org.mpxj.howto.write;
	
	import org.mpxj.ProjectFile;
	import org.mpxj.writer.FileFormat;
	import org.mpxj.writer.UniversalProjectWriter;
	
	public class Planner
	{
		public void write(ProjectFile project, String fileName) throws Exception
		{
			new UniversalProjectWriter(FileFormat.PLANNER).write(project, fileName);
		}
	}
	```

=== "C#"
	```c#
	using MPXJ.Net;
	
	namespace MPXJ.Samples.HowToWrite;
	
	public class Planner
	{
		public void Write(ProjectFile project, string fileName)
		{
			new UniversalProjectWriter(FileFormat.PLANNER).Write(project, fileName);
		}
	}
	```

## Using PlannerWriter
If required, the `PlannerWriter` class can be used directly, which provides
access to additional options, as described below.

### Charset
The character set used to write a Planner file can be specified using the
`setCharset` method, as illustrated below.

=== "Java"
	```java
	package org.mpxj.howto.write;
	
	import org.mpxj.ProjectFile;
	import org.mpxj.planner.PlannerWriter;
	
	import java.nio.charset.Charset;
	
	public class PlannerCharset
	{
		public void write(ProjectFile project, String fileName) throws Exception
		{
			PlannerWriter writer = new PlannerWriter();
			writer.setCharset(Charset.forName("GB2312"));
			writer.write(project, fileName);
		}
	}
	```

=== "C#"
	```c#
	using System.Text;
	using MPXJ.Net;
	
	namespace MPXJ.Samples.HowToWrite;
	
	public class PlannerCharset
	{
	 	public void Write(ProjectFile project, string fileName)
	 	{
		  	var writer = new PlannerWriter();
		  	writer.Encoding = Encoding.GetEncoding("GB2312");
		  	writer.Write(project, fileName);
	 	}
	}
	```
