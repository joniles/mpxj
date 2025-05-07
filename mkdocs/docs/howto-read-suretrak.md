# How To: Read SureTrak files
A Primavera SureTrak installation stores project data as a database consisting
of a number of individual files. In a typical SureTrak installation files for a
number of different projects live in a single projects directory. A SureTrak
user can back up an individual project to create an STX file, which is a
compressed archive containing all of the files from a single project.

## Reading STX files
The simplest way to read an STX file is to use the `UniversalProjectReader`:

=== "Java"
	```java
	package org.mpxj.howto.read;
	
	import org.mpxj.ProjectFile;
	import org.mpxj.reader.UniversalProjectReader;
	
	public class SureTrak
	{
		public void read() throws Exception
		{
			UniversalProjectReader reader = new UniversalProjectReader();
			ProjectFile project = reader.read("my-sample.stx");
		}
	}
	```

=== "C#"
	```c#
	using MPXJ.Net;
	
	namespace MPXJ.Samples.HowToRead;

	public class SureTrak
	{
		public void Read()
		{
			var reader = new UniversalProjectReader();
			var project = reader.Read("my-sample.stx");
		}
	}
	```

You can work directly with the `SureTrakSTXFileReader` by replacing
`UniversalProjectReader` with `SureTrakSTXFileReader`, although this offers no
particular advantage as there are no additional configuration settings available
on the `SureTrakSTXFileReader` class.

## Reading a SureTrak directory
If you are working with a directory containing SureTrak project data you have
two options. If you know that the directory only contains a single project, you
can use the `UniversalProjectReader`:

=== "Java"
	```java
	package org.mpxj.howto.read;
	
	import org.mpxj.ProjectFile;
	import org.mpxj.reader.UniversalProjectReader;
	
	public class SureTrakDirectory
	{
		public void read() throws Exception
		{
			UniversalProjectReader reader = new UniversalProjectReader();
			ProjectFile project = reader.read("my-suretrak-directory");
		}
	}
	```

=== "C#"
	```c#
	using MPXJ.Net;
	
	namespace MPXJ.Samples.HowToRead;

	public class SureTrakDirectory
	{
		public void Read()
		{
			var reader = new UniversalProjectReader();
			var project = reader.Read("my-suretrak-directory");
		}
	}
	```

If the directory happens to contain multiple projects the
`UniversalProjectReader` will simply read the first one it finds, in alphabetic
order.

If you know that the directory you are working with contains multiple projects,
you will need to use the `SureTrakDatabaseReader` class.

=== "Java"
	```java
	package org.mpxj.howto.read;
	
	import org.mpxj.ProjectFile;
	import org.mpxj.primavera.suretrak.SureTrakDatabaseReader;
	
	import java.util.List;
	
	public class SureTrakListProjects
	{
		public void read() throws Exception
		{
			// Find a list of the project names
			String directory = "my-suretrak-directory";
			List<String> projectNames = SureTrakDatabaseReader.listProjectNames(directory);
	
			// Tell the reader which project to work with
			SureTrakDatabaseReader reader = new SureTrakDatabaseReader();
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
	
	public class SureTrakListProjects
	{
	 	public void Read()
	 	{
		  	// Find a list of the project names
		  	var directory = "my-suretrak-directory";
		  	var projectNames = SureTrakDatabaseReader.ListProjectNames(directory);
	
		  	// Tell the reader which project to work with
		  	var reader = new SureTrakDatabaseReader();
		  	reader.ProjectName = projectNames[0];
	
		  	// Read the project
		  	var project = reader.Read(directory);
	 	}
	}
	```

As the example above shows, the `SureTrakDatabaseReader` class provides a method
which lists the names of the SureTrak projects it finds in a directory. You can
then select which project you want to load, and call the `setProjectName` method
of the reader to make this selection. You can then call the `read` method
passing in the name of the directory, and the reader will extract data for the
selected project.
