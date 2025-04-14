# How To: Write SDEF files
SDEF is the Standard Data Exchange Format, as defined by the USACE
(United States Army Corps of Engineers). SDEF is a fixed column format text
file, used to import a project schedule up into the QCS (Quality Control
System) software from USACE. The
specification for the file format can be found
[here](https://www.publications.usace.army.mil/Portals/76/Publications/EngineerRegulations/ER_1-1-11.pdf).

## Writing SDEF files
The sample code below illustrates how to write data to an SDEF file.

=== "Java"
	```java
	package org.mpxj.howto.write;
	
	import org.mpxj.ProjectFile;
	import org.mpxj.writer.FileFormat;
	import org.mpxj.writer.UniversalProjectWriter;
	
	public class SDEF
	{
		public void write(ProjectFile project, String fileName) throws Exception
		{
			new UniversalProjectWriter(FileFormat.SDEF).write(project, fileName);
		}
	}
	```

=== "C#"
	```c#
	using MPXJ.Net;
	
	namespace MPXJ.Samples.HowToWrite;
	
	public class SDEF
	{
	 	public void Write(ProjectFile project, string fileName)
	 	{
		  	new UniversalProjectWriter(FileFormat.SDEF).Write(project, fileName);
	 	}
	}
	```

## Using SDEFWriter
If required, the `SDEFWriter` class can be used directly, which
provides access to additional options, as described below.

### Charset
By default SDEF files are written using the `US_ASCII` charset. The `setCharset`
method on the `SDEFWriter` class can be used to change this if required:

```java
package org.mpxj.howto.write;

import org.mpxj.ProjectFile;
import org.mpxj.sdef.SDEFWriter;

import java.nio.charset.StandardCharsets;

public class SDEFCharset
{
   public void write(ProjectFile project, String fileName) throws Exception
   {
      SDEFWriter writer = new SDEFWriter();
      writer.setCharset(StandardCharsets.UTF_8);
      writer.write(project, fileName);
   }
}
```