# How To: Read Primavera PMXML files
Primavera P6 can export data in an XML format known as PMXML.

## Reading PMXML files
The simplest way to read a PMXML file is to use the `UniversalProjectReader`:

=== "Java"
	```java
	package org.mpxj.howto.read;
	
	import org.mpxj.ProjectFile;
	import org.mpxj.reader.UniversalProjectReader;
	
	public class PMXML
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

	public class PMXML
	{
		public void Read()
		{
			var reader = new UniversalProjectReader();
			var project = reader.Read("my-sample.xml");
		}
	}
	```

## Using PrimaveraPMFileReader
You can work directly with the `PrimaveraPMFileReader` by replacing
`UniversalProjectReader` with `PrimaveraPMFileReader`. This provides access to
additional options, as described below.

### Multiple Projects
A PMXML file can contain multiple projects. By default, MPXJ reads the first
non-external project it finds in the file, otherwise it defaults to the first
project it finds. You can however use MPXJ to list the projects contained in a
PMXML file, as shown below:

=== "Java"
	```java
	package org.mpxj.howto.read;
	
	import org.mpxj.primavera.PrimaveraPMFileReader;
	
	import java.io.FileInputStream;
	import java.util.Map;
	
	public class PMXMLListProjects
	{
		public void read() throws Exception
		{
			PrimaveraPMFileReader reader = new PrimaveraPMFileReader();
			FileInputStream is = new FileInputStream("my-sample.xml");
			Map<Integer, String> projects = reader.listProjects(is);
			System.out.println("ID\tName");
			for (Map.Entry<Integer, String> entry : projects.entrySet())
			{
				System.out.println(entry.getKey()+"\t"+entry.getValue());
			}
		}
	}
	```

=== "C#"
	```c#
	using MPXJ.Net;
	
	namespace MPXJ.Samples.HowToRead;

	public class PMXMLListProjects
	{
		public void Read()
		{
			var reader = new PrimaveraPMFileReader();
			var stream = new FileStream("my-sample.xml",
				FileMode.Open, FileAccess.Read, FileShare.None);
			var projects = reader.ListProjects(stream);
			System.Console.WriteLine("ID\tName");
			foreach (var entry in projects)
			{
					System.Console.WriteLine($"{entry.Key}\t{entry.Value}");
			}
		}
	}
	```

The call to `listProjects` returns a `Map` whose key is the project ID,
and the values are project short names.

Once you have decided which of these projects you want to work with, you can
call `setProjectID` to tell the reader which project to open, as shown below.

```java
package org.mpxj.howto.read;

import org.mpxj.ProjectFile;
import org.mpxj.primavera.PrimaveraPMFileReader;

public class PMXMLProjectID
{
   public void read() throws Exception
   {
      PrimaveraPMFileReader reader = new PrimaveraPMFileReader();
      reader.setProjectID(123);
      ProjectFile file = reader.read("my-sample.xml");
   }
}
```

Alternatively you can ask MPXJ to read all the projects contained in the file:

=== "Java"
	```java
	package org.mpxj.howto.read;
	
	import org.mpxj.ProjectFile;
	import org.mpxj.primavera.PrimaveraPMFileReader;
	
	import java.util.List;
	
	public class PMXMLReadAll
	{
		public void read() throws Exception
		{
			PrimaveraPMFileReader reader = new PrimaveraPMFileReader();
			List<ProjectFile> files = reader.readAll("my-sample.xml");
		}
	}
	```


=== "C#"
	```c#
	using MPXJ.Net;
	
	namespace MPXJ.Samples.HowToRead;

	public class PMXMLReadAll
	{
		public void Read()
		{
			var reader = new UniversalProjectReader();
			var project = reader.ReadAll("my-sample.xml");
		}
	}
	```

The call to the `readAll` method returns a list of `ProjectFile` instances
corresponding to the projects in the PMXML file.

> Note that when calling the `readAll` method for a PMXML file, the list of
> projects returned will include baseline projects. You can determine which
> projects are baseline projects by calling the `ProjectProperties` method
> `getProjectIsBaseline()`, which will return `true` for baseline projects.

### Link Cross-Project Relations
A PMXML file can contain multiple projects with relations between activities
which span those projects. By default, these cross-project relations are ignored.
However, if you set the `linkCrossProjectRelations` reader attribute to `true`,
MPXJ will attempt to link these relations across projects: 

=== "Java"
	```java
	package org.mpxj.howto.read;
	
	import org.mpxj.ProjectFile;
	import org.mpxj.primavera.PrimaveraPMFileReader;
	
	import java.util.List;
	
	public class PMXMLLinkCrossProject
	{
		public void read() throws Exception
		{
			PrimaveraPMFileReader reader = new PrimaveraPMFileReader();
			reader.setLinkCrossProjectRelations(true);
			List<ProjectFile> files = reader.readAll("my-sample.xml");
		}
	}
	```

=== "C#"
	```c#
	using MPXJ.Net;
	
	namespace MPXJ.Samples.HowToRead;
	
	public class PMXMLLinkCrossProject
	{
	 	public void Read()
	 	{
		  	var reader = new PrimaveraPMFileReader();
		  	reader.LinkCrossProjectRelations = true;
		  	var files = reader.ReadAll("my-sample.xml");
	 	}
	}
	```

### Baselines
Users can export PMXML files from P6 which contain the baseline project
along with the main project being exported. When the `readAll` method
is used to read a PMXML file, MPXJ will attempt to populate the baseline
fields of the main project if it can locate the baseline project in
the PMXML file.

By default the "Planned Dates" strategy is used to populate baseline fields,
which is the approach P6 uses when the "Earned Value Calculation" method is
set to  "Budgeted values with planned dates".

`PrimaveraPMFileReader` provides a method allowing the strategy to be changed,
thus allowing you to select the "Current Dates" strategy, which is the approach
used by P6 when the Earned Value Calculation method is set to "At Completion
values with current dates" or "Budgeted values with current dates". The example
below illustrates how this method is used:

```java
package org.mpxj.howto.read;

import org.mpxj.ProjectFile;
import org.mpxj.primavera.PrimaveraPMFileReader;
import org.mpxj.primavera.PrimaveraBaselineStrategy;

import java.util.List;

public class PMXMLBaselines
{
   public void read() throws Exception
   {
      PrimaveraPMFileReader reader = new PrimaveraPMFileReader();
      reader.setBaselineStrategy(PrimaveraBaselineStrategy.CURRENT_ATTRIBUTES);
      List<ProjectFile> files = reader.readAll("my-sample.xml");
   }
}
```

See the [How To Use Baselines section](howto-use-baselines.md)
for more information on how MPXJ works with baselines.
