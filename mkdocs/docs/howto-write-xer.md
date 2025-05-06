# How To: Write XER files
XER files have been imported and exported by Primavera software since the
earliest days of P6 and this format is still often the preferred way to
move schedule data between instances of P6 even today.

## Writing XER files
The sample code below illustrates how to write data to an XER file.

=== "Java"
	```java
	package org.mpxj.howto.write;
	
	import org.mpxj.ProjectFile;
	import org.mpxj.writer.FileFormat;
	import org.mpxj.writer.UniversalProjectWriter;
	
	public class XER
	{
		public void write(ProjectFile project, String fileName) throws Exception
		{
			new UniversalProjectWriter(FileFormat.XER).write(project, fileName);
		}
	}
	```

=== "C#"
	```c#
	using MPXJ.Net;
	
	namespace MPXJ.Samples.HowToWrite;
	
	public class XER
	{
		public void Write(ProjectFile project, string fileName)
		{
			new UniversalProjectWriter(FileFormat.XER).Write(project, fileName);
		}
	}
	```

## Using PrimaveraXERFileWriter
If required, the `PrimaveraXERFileWriter` class can be used directly, which
provides access to additional options, as described below.

### Charset
By default XER files written by MPXJ are encoded using the Windows-1252
character set. If you need to use a different character set, the `setCharset`
method can be used to achieve this, as illustrated by the code
below.

=== "Java"
	```java
	package org.mpxj.howto.write;
	
	import org.mpxj.ProjectFile;
	import org.mpxj.primavera.PrimaveraXERFileWriter;
	
	import java.nio.charset.Charset;
	
	public class XERCharset
	{
		public void write(ProjectFile project, String fileName) throws Exception
		{
			PrimaveraXERFileWriter writer = new PrimaveraXERFileWriter();
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
	
	public class XERCharset
	{
	 	public void Write(ProjectFile project, string fileName)
	 	{
		  	var writer = new PrimaveraXERFileWriter();
		  	writer.Encoding = Encoding.GetEncoding("GB2312");
		  	writer.Write(project, fileName);
	 	}
	}
	```
