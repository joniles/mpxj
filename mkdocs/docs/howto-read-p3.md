# How To: Read P3 files
A Primavera P3 installation stores project data as a database consisting of a
number of individual files. In a typical P3 installation files for a number of
different projects live in a single projects directory. A P3 user can back up an
individual project to create a PRX file, which is a compressed archive
containing all of the files from a single project.

## Reading PRX files
The simplest way to read a PRX file is to use the `UniversalProjectReader`:

=== "Java"
	```java
	package org.mpxj.howto.read;
	
	import org.mpxj.ProjectFile;
	import org.mpxj.reader.UniversalProjectReader;
	
	public class P3
	{
		public void read() throws Exception
		{
			UniversalProjectReader reader = new UniversalProjectReader();
			ProjectFile project = reader.read("my-sample.prx");
		}
	}
	```

=== "C#"
	```c#
	using MPXJ.Net;
	
	namespace MPXJ.Samples.HowToRead;

	public class P3
	{
		public void Read()
		{
			var reader = new UniversalProjectReader();
			var project = reader.Read("my-sample.prx");
		}
	}
	```

You can work directly with the `P3PRXFileReader` by replacing
`UniversalProjectReader` with `P3PRXFileReader`, although this offers no
particular advantage as there are no additional configuration settings available
on the `P3PRXFileReader` class.

## Reading a P3 directory
If you are working with a directory containing P3 project data you have two
options. If you know that the directory only contains a single project, you can
use the `UniversalProjectReader`:

=== "Java"
	```java
	package org.mpxj.howto.read;
	
	import org.mpxj.ProjectFile;
	import org.mpxj.reader.UniversalProjectReader;
	
	public class P3Directory
	{
		public void read() throws Exception
		{
			UniversalProjectReader reader = new UniversalProjectReader();
			ProjectFile project = reader.read("my-p3-directory");
		}
	}
	```

=== "C#"
	```c#
	using MPXJ.Net;
	
	namespace MPXJ.Samples.HowToRead;

	public class P3Directory
	{
		public void Read()
		{
			var reader = new UniversalProjectReader();
			var project = reader.Read("my-p3-directory");
		}
	}
	```

If the directory happens to contain multiple projects the
`UniversalProjectReader` will simply read the first one it finds, in alphabetic
order.

If you know that the directory you are working with contains multiple projects,
you will need to use the `P3DatabaseReader` class.

=== "Java"
	```java
	package org.mpxj.howto.read;
	
	import org.mpxj.ProjectFile;
	import org.mpxj.primavera.p3.P3DatabaseReader;
	
	import java.util.List;
	
	public class P3NamedProject
	{
		public void read() throws Exception
		{
			// Find a list of the project names
			String directory = "my-p3-directory";
			List<String> projectNames = P3DatabaseReader.listProjectNames(directory);
	
			// Tell the reader which project to work with
			P3DatabaseReader reader = new P3DatabaseReader();
			reader.setProjectName(projectNames.get(0));
	
			// Read the project
			ProjectFile project = reader.read(directory);
		}
	}
	```

=== "C#"
	```c#
	using MPXJ.Net;
	
	namespace MPXJ.Samples.HowToRead;
	
	public class P3NamedProject
	{
	 	public void Read()
	 	{
		  	// Find a list of the project names
		  	var directory = "my-p3-directory";
		  	var projectNames = P3DatabaseReader.ListProjectNames(directory);
	
		  	// Tell the reader which project to work with
		  	var reader = new P3DatabaseReader();
		  	reader.ProjectName = projectNames[0];
	
		  	// Read the project
		  	var project = reader.Read(directory);
	 	}
	}
	```

As the example above shows, the `P3DatabaseReader` class provides a method which
lists the names of the P3 projects it finds in a directory. You can then select
which project you want to load, and call the `setProjectName` method of the
reader to make this selection. You can then call the `read` method passing in
the name of the directory, and the reader will extract data for the selected
project.
