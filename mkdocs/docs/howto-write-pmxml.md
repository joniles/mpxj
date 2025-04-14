# How To: Write PMXML files
The XML file format supported by Primavera P6 for import and export is known as
PMXML.

## Writing PMXML files
The sample code below illustrates how to write data to a PMXML file.

=== "Java"
	```java
	package org.mpxj.howto.write;
	
	import org.mpxj.ProjectFile;
	import org.mpxj.writer.FileFormat;
	import org.mpxj.writer.UniversalProjectWriter;
	
	public class PMXML
	{
		public void write(ProjectFile project, String fileName) throws Exception
		{
			new UniversalProjectWriter(FileFormat.PMXML).write(project, fileName);
		}
	}
	```

=== "C#"
	```c#
	using MPXJ.Net;
	
	namespace MPXJ.Samples.HowToWrite;
	
	public class PMXML
	{
		public void Write(ProjectFile project, string fileName)
		{
			new UniversalProjectWriter(FileFormat.PMXML).Write(project, fileName);
		}
	}
	```

## Using PrimaveraPMFileWriter
If required, the `PrimaveraPMFileWriter` class can be used directly, which
provides access to additional options, as described below.

### Baselines
By default baselines are not written to PMXML files. If the `ProjectFile`
instance you are writing contains a baseline, this can be included in the PMXML
file by calling the `setWriteBaselines` method as shown below.

=== "Java"
	```java
	package org.mpxj.howto.write;
	
	import org.mpxj.ProjectFile;
	import org.mpxj.primavera.PrimaveraPMFileWriter;
	
	public class PMXMLBaselines
	{
		public void write(ProjectFile project, String fileName) throws Exception
		{
			PrimaveraPMFileWriter writer = new PrimaveraPMFileWriter();
			writer.setWriteBaselines(true);
			writer.write(project, fileName);
		}
	}
	```

=== "C#"
	```c#
	using MPXJ.Net;
	
	namespace MPXJ.Samples.HowToWrite;
	
	public class PMXMLBaselines
	{
	 	public void Write(ProjectFile project, string fileName)
	 	{
		  	var writer = new PrimaveraPMFileWriter();
		  	writer.WriteBaselines = true;
		  	writer.Write(project, fileName);
	 	}
	}
	```

