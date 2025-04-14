# How To: Read MPD files
Microsoft Project from Project 98 until Project 2003 could read and write
schedules as Microsoft Access database files with the extension MPD. Versions
of Microsoft Project after 2003 can import projects from MPD databases but
cannot create or write to them. Project 98 creates a database with a schema
known as MPD8, which MPXJ does not currently support reading. Project 2000
onward uses a schema called MPD9 which MPXJ can read.

## Reading MPD files
The simplest way to read an MPD file is to use the `UniversalProjectReader`:

=== "Java"
	```java
	package org.mpxj.howto.read;
	
	import org.mpxj.ProjectFile;
	import org.mpxj.reader.UniversalProjectReader;
	
	public class MPDFile
	{
		public void read() throws Exception
		{
			UniversalProjectReader reader = new UniversalProjectReader();
			ProjectFile project = reader.read("my-sample.mpd");
		}
	}
	```

=== "C#"
	```c#
	using MPXJ.Net;
	
	namespace MPXJ.Samples.HowToRead;

	public class MPDFile
	{
		public void Read()
		{
			var reader = new UniversalProjectReader();
			var project = reader.Read("my-sample.mpd");
		}
	}
	```

> Note that the `UniversalProjectReader.Read()` method assumes that you are
> reading a project with ID of 1 from the MPD file. This is the default when a
> single project is written to an MPD file. Calling the `ReadAll` method will
> return all of the projects in the MPD file.


## Using MPDFileReader
You can work directly with the `MPDFileReader` by replacing
`UniversalProjectReader` with `MPDFileReader`. The sample code below illustrates
how the reader can be used to retrieve a list of the projects in the file via
the `listProjects` method. We can then select the ID of the project we want to
read and use the `setProjecID` method to pass this to the reader. Finally,
calling the `read` method reads the required project.

=== "Java"
	```java
	package org.mpxj.howto.read;
	
	import org.mpxj.ProjectFile;
	import org.mpxj.mpd.MPDFileReader;
	
	import java.io.File;
	import java.util.Map;
	
	public class MPDFileWithReader
	{
		public void read() throws Exception
		{
			File file = new File("my-sample.mpd");
			MPDFileReader reader = new MPDFileReader();
	
			// Retrieve the project details
			Map<Integer, String> projects = reader.listProjects(file);
	
			// Look up the project we want to read from the map.
			// For this example we'll just use a hard-coded value.
			Integer projectID = Integer.valueOf(1);
			
			// Set the ID f the project we want to read
			reader.setProjectID(projectID);
	
			// Read the project
			ProjectFile project = reader.read(file);
		}
	}
	```

=== "C#"
	```c#
	using MPXJ.Net;
	
	namespace MPXJ.Samples.HowToRead;
	
	public class MPDFileWithReader
	{
	 	public void Read()
	 	{
		  	var reader = new MPDFileReader();
	
		  	// Retrieve the project details
		  	var projects = reader.ListProjects("my-sample.mpd");
	
		  	// Look up the project we want to read from the map.
		  	// For this example we'll just use a hard-coded value.
		  	var projectID = 1;
	
		  	// Set the ID f the project we want to read
		  	reader.ProjectID = projectID;
	
		  	// Read the project
		  	var project = reader.Read("my-sample.mpd");
	 	}
	}
	```
