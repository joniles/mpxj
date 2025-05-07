# Converting Files

To convert project data between different formats you read the source 
data using an appropriate Reader class, then write the data using a Writer
class which matches the format you want to convert to.

MPXJ can do a lot of the work for you, as the example below illustrates. The
`UniversalProjectReader` will detect the type of schedule being read and handle
it accordingly. The `UniversalProjectWriter` class manages the individual
writer classes for you, taking an argument representing the type of file you
want to write.

=== "Java"
	```java
    package org.mpxj.howto.convert;
    
    import org.mpxj.ProjectFile;
    import org.mpxj.reader.UniversalProjectReader;
    import org.mpxj.writer.FileFormat;
    import org.mpxj.writer.UniversalProjectWriter;
    
    public class ConvertUniversal
    {
       public void convert(String inputFile, FileFormat format, String outputFile) throws Exception
       {
          ProjectFile projectFile = new UniversalProjectReader().read(inputFile);
          new UniversalProjectWriter(format).write(projectFile, outputFile);
       }
    }
	```

=== "C#"
	```c#
    using MPXJ.Net;
    
    public class ConvertUniversal
    {
        public void Convert(string inputFile, FileFormat format, string outputFile)
        {
            var projectFile = new UniversalProjectReader().Read(inputFile);
            new UniversalProjectWriter(format).Write(projectFile, outputFile);
        }
    }
	```

=== "Python"
	```python
	import jpype
	import mpxj
	
	jpype.startJVM()
	
	from org.mpxj.reader import UniversalProjectReader
	from org.mpxj.writer import FileFormat
	from org.mpxj.writer import UniversalProjectWriter

	def convert(input_file, format, output_file):
		project_file = UniversalProjectReader().read(input_file);
		UniversalProjectWriter(format).write(project_file, output_file);
	
	jpype.shutdownJVM()
	```

If you already know the file types you are converting between,
you can use the specific Reader and Writer classes, as shown below.

=== "Java"
	```java
    package org.mpxj.howto.convert;
    
    import org.mpxj.ProjectFile;
    import org.mpxj.mpp.MPPReader;
    import org.mpxj.mpx.MPXWriter;
    import org.mpxj.reader.ProjectReader;
    import org.mpxj.writer.ProjectWriter;
    
    public class ConvertMppToMpx
    {
       public void convert(String inputFile, String outputFile) throws Exception
       {
          ProjectReader reader = new MPPReader();
          ProjectFile projectFile = reader.read(inputFile);
    
          ProjectWriter writer = new MPXWriter();
          writer.write(projectFile, outputFile);
       }
    }
	```

=== "C#"
	```c#
    using MPXJ.Net;
    
    public class ConvertMppToMpx
    {
        public void Convert(string inputFile, string outputFile)
        {
            var reader = new MPPReader();
            var projectFile = reader.Read(inputFile);
    
            var writer = new MPXWriter();
            writer.Write(projectFile, outputFile);
        }
    }
	```


=== "Python"
	```python
	import jpype
	import mpxj
	
	jpype.startJVM()
	
	from org.mpxj.mpp import MPPReader
	from org.mpxj.mpx import MPXWriter
	
	def convert(input_file, output_file):
		reader = MPPReader()
		project_file = reader.read(input_file)
		writer = MPXWriter()
		writer.write(project_file, output_file)
	
	jpype.shutdownJVM()
	
	```
