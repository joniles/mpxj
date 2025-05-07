# How To: Read MSPDI files
The Microsoft Project Data Interchange (MSPDI) format is an XML file format
which Microsoft Project has been able to read and write since Project 2002.

## Reading MSPDI files
The simplest way to read an MSPDI file is to use the `UniversalProjectReader`:

=== "Java"
	```java
	package org.mpxj.howto.read;
	
	import org.mpxj.ProjectFile;
	import org.mpxj.reader.UniversalProjectReader;
	
	public class MSDPI
	{
		public void read() throws Exception
		{
			UniversalProjectReader reader = new UniversalProjectReader();
			ProjectFile project = reader.read("my-sample.xml");
		}
	}
	```

=== "C#"
	```c#
	using MPXJ.Net;
	
	namespace MPXJ.Samples.HowToRead;

	public class MSPDI
	{
		public void Read()
		{
			var reader = new UniversalProjectReader();
			var project = reader.Read("my-sample.xml");
		}
	}
	```

## Using MSPDIReader
You can work directly with the `MSPDIReader` class by replacing
`UniversalProjectReader` with `MSPDIReader`. This provides access to additional
options, as described below.

### Charset
By default MPXJ assumes that MSPDI files are encoded as UTF-8. The
`UniversalProjectReader` understands Unicode Byte Order Marks (BOM) and will
adjust the encoding appropriately if a BOM is present. If you have an MSPDI file
with an unusual encoding, you can manually set the encoding used by the reader,
as illustrated below.

=== "Java"
	```java
	package org.mpxj.howto.read;
	
	import org.mpxj.ProjectFile;
	import org.mpxj.mspdi.MSPDIReader;
	
	import java.nio.charset.Charset;
	
	public class MSPDIWithCharset
	{
		public void read() throws Exception
		{
			MSPDIReader reader = new MSPDIReader();
	
			reader.setCharset(Charset.forName("GB2312"));
			ProjectFile project = reader.read("my-sample.xml");
		}
	}
	```

=== "C#"
	```c#
	using System.Text;
	using MPXJ.Net;
	
	namespace MPXJ.Samples.HowToRead;

	public class MSPDIWithLocale
	{
		public void Read()
		{
			var reader = new MSPDIReader();
			reader.Encoding = Encoding.GetEncoding("GB2312");
			var project = reader.Read("my-sample.xml");
		}
	}
	```

### Microsoft Project Compatibility
Microsoft Project will read MSPDI files which are not valid XML according to the
MSPDI schema. By default MPXJ has been configured to take the same approach. If
for some reason you wish to apply strict validation when reading an MSPDI file,
you can do this using the  `setMicrosoftProjectCompatibleInput` method, as shown
below.

=== "Java"
	```java
	package org.mpxj.howto.read;
	
	import org.mpxj.ProjectFile;
	import org.mpxj.mspdi.MSPDIReader;
	
	public class MSPDICompatibleInput
	{
		public void read() throws Exception
		{
			MSPDIReader reader = new MSPDIReader();
			reader.setMicrosoftProjectCompatibleInput(false);
			ProjectFile project = reader.read("my-sample.xml");
		}
	}
	```

=== "C#"
	```c#
	using MPXJ.Net;
	
	namespace MPXJ.Samples.HowToRead;

	public class MSPDICompatibleInput
	{
		public void Read()
		{
			var reader = new MSPDIReader();
			reader.MicrosoftProjectCompatibleInput = false;
			var project = reader.Read("my-sample.xml");
		}
	}
	```

### Ignore Errors
By default MPXJ will ignore errors when parsing attributes from an MSPDI file.
This behavior is controlled using the `setIgnoreErrors` method. The example
below illustrates how we can force the `MSPDIReader` to report
errors encountered when reading a file:

=== "Java"
	```java
	package org.mpxj.howto.read;
	
	import org.mpxj.ProjectFile;
	import org.mpxj.mspdi.MSPDIReader;
	
	public class MSPDIIgnoreErrors
	{
		public void read() throws Exception
		{
			MSPDIReader reader = new MSPDIReader();
	
			reader.setIgnoreErrors(false);
			ProjectFile project = reader.read("my-sample.xml");
		}
	}
	```

=== "C#"
	```c#
	using MPXJ.Net;
	
	namespace MPXJ.Samples.HowToRead;
	
	public class MSPDIIgnoreErrors
	{
	 	public void Read()
	 	{
		  	var reader = new MSPDIReader();
		  	reader.IgnoreErrors = false;
		  	var project = reader.Read("my-sample.xml");
	 	}
	}
	```

Note that if errors are ignored when reading a file, the ignored errors
are available by using the `ProjectFile.getIgnoredErrors()` method.
