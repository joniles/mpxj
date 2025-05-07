# How To: Read a Primavera P6 database
Primavera P6 supports the use of SQLite, SQL Server and Oracle databases. SQLite
is a single file database, and in common with the other file-based schedule
formats MPXJ provides a reader class. To read schedules from SQL Server and
Oracle databases you will need to use a JDBC connection with MPXJ. These
approaches are described in the sections below.

## SQLite
The `PrimaveraDatabaseFileReader` provides convenient access to P6 schedules
stored as a SQLite database. By default P6 will create a database called 
`PPMDBSQLite.db` in the Windows user's `My Documents`  folder. The example code
below illustrates how we'd list the schedules in this file, and reda one of
those schedules using it ID.


=== "Java"
	```java
	package org.mpxj.howto.read;
	
	import org.mpxj.ProjectFile;
	import org.mpxj.primavera.PrimaveraDatabaseFileReader;
	
	import java.io.File;
	import java.util.Map;
	
	public class P6Sqlite
	{
		public void read() throws Exception
		{
			PrimaveraDatabaseFileReader reader = new PrimaveraDatabaseFileReader();
	
			//
			// Retrieve a list of the projects available in the database
			//
			File file = new File("PPMDBSQLite.db");
			Map<Integer,String> projects = reader.listProjects(file);
	
			//
			// At this point you'll select the project
			// you want to work with.
			//
	
			//
			// Now open the selected project using its ID
			//
			int selectedProjectID = 1;
			reader.setProjectID(selectedProjectID);
			ProjectFile projectFile = reader.read(file);
		}
	}
	```

=== "C#"
	```c#
	using MPXJ.Net;
	
	namespace MPXJ.Samples.HowToRead;

	public class P6Sqlite
	{
		public void Read()
		{
			var reader = new PrimaveraDatabaseFileReader();
	
			//
			// Retrieve a list of the projects available in the database
			//
			var file = "PPMDBSQLite.db";
			var projects = reader.ListProjects(file);
	
			//
			// At this point you'll select the project
			// you want to work with.
			//
	
			//
			// Now open the selected project using its ID
			//
			int selectedProjectID = 1;
			reader.ProjectID = selectedProjectID;
			var projectFile = reader.Read(file);
		}
	}
	```

## JDBC in Java
For P6 schedules hosted in either a SQL Server databases or an Oracle database,
we must use a JDBC driver with the `PrimaveraDatabaseReader` reader in order to
access this data. In this example we're reading a schedule from a SQL Server
database using Microsoft's JDBC driver. This code assumes that you have added
the JDBC driver as a dependency to your Java project.

```java
package org.mpxj.howto.read;

import org.mpxj.ProjectFile;
import org.mpxj.primavera.PrimaveraDatabaseReader;

import java.sql.Connection;
import java.sql.DriverManager;
import java.util.Map;

public class P6JDBC
{
   public void read() throws Exception
   {
      //
      // Load the JDBC driver
      //
      String driverClass = "com.microsoft.sqlserver.jdbc.SQLServerDriver";
      Class.forName(driverClass);

      //
      // Open a database connection. You will need to change
      // these details to match the name of your server, database, user and password.
      //
      String connectionString = "jdbc:sqlserver://localhost:1433;databaseName=my-database-name;user=my-user-name;password=my-password;";
      Connection c = DriverManager.getConnection(connectionString);
      PrimaveraDatabaseReader reader = new PrimaveraDatabaseReader();
      reader.setConnection(c);

      //
      // Retrieve a list of the projects available in the database
      //
      Map<Integer, String> projects = reader.listProjects();

      //
      // At this point you'll select the project
      // you want to work with.
      //

      //
      // Now open the selected project using its ID
      //
      int selectedProjectID = 1;
      reader.setProjectID(selectedProjectID);
      ProjectFile projectFile = reader.read();
   }
}
```


## JDBC in .Net
The approach for reading schedule data from a SQL Server or Orcale database is
very similar to that used with the Java version. The main difference is how we
add the JDBC driver to our project as a dependency. To do this we add a
`MavenReference` to our project. The example below show how I have added this
just after the reference to the `MPXJ.Net` package:

```xml
<ItemGroup>
	<PackageReference Include="MPXJ.Net" Version="14.0.0" />
	<MavenReference Include="com.microsoft.sqlserver:mssql-jdbc" Version="12.6.2.jre8" />
</ItemGroup>
```

> Note that the IKVM's conversion of Java code to .Net being works by
> implementing a Java 8 (sometimes also known as a Java 1.8) virtual machine.
> If you have a choice of Java packages to use which are targeted at different
> Java versions, select the Java 8 version - as illustrated in the example above.

Now we can use the JDBC driver to create a connection to our database,
as the sample code below illustrates.

```c#
using com.microsoft.sqlserver.jdbc;
using MPXJ.Net;

namespace MPXJ.Samples.HowToRead;

public class P6JDBC
{
	public void Read()
	{
		//
		// Load the JDBC driver
		//
		var driver = new SQLServerDriver();

		//
		// Open a database connection. You will need to change
		// these details to match the name of your server, database, user and password.
		//
		var connectionString = "jdbc:sqlserver://localhost:1433;databaseName=my-database-name;user=my-user-name;password=my-password;";
		var connection = driver.connect(connectionString, null);
		var reader = new PrimaveraDatabaseReader();
		reader.Connection = connection;

		//
		// Retrieve a list of the projects available in the database
		//
		var projects = reader.ListProjects();

		//
		// At this point you'll select the project
		// you want to work with.
		//

		//
		// Now open the selected project using its ID
		//
		int selectedProjectID = 1;
		reader.ProjectID = selectedProjectID;
		var projectFile = reader.Read();
	}
}
```

## EPS
The samples in the previous sections demonstrate use of the `listProjects()`
method to retrieve a `Map` containing the unique IDs and names for the projects
in the P6 database. An alternative to this, providing access to more detail,
is to retrieve the Enterprise Project Structure (EPS) from the database.

The EPS allows P6 users to create a hierarchy of "folders", or EPS nodes,
which are used to organise a portfolio of projects into a logical structure.
The projects in P6 form the leaf nodes of this hierarchy.


## Options
This section documents the additional options provided by the
PrimaveraDatabaseReader.

 
### Activity WBS
In the original implementation of the database handling code, MPXJ would assign
each task representing a Primavera Activity its own distinct WBS value. This
does not match Primavera's behaviour where all of a WBS element's child
activities will have the same WBS value as the parent WBS element. MPXJ's
default behaviour now matches Primavera, but should you wish to you can revert
to the original behaviour by calling the `setMatchPrimaveraWBS` as shown below.

=== "Java"
	```java
	package org.mpxj.howto.read;
	
	import org.mpxj.primavera.PrimaveraDatabaseReader;
	
	public class P6ActivityWbs
	{
		public void read() throws Exception
		{
			PrimaveraDatabaseReader reader = new PrimaveraDatabaseReader();
			reader.setMatchPrimaveraWBS(false);
		}
	}
	```

=== "C#"
	```c#
	using MPXJ.Net;
	
	namespace MPXJ.Samples.HowToRead;

	public class P6ActivityWbs
	{
		public void Read()
		{
			var reader = new PrimaveraDatabaseReader();
			reader.MatchPrimaveraWBS = false;
		}
	}
	```

### WBS is Full Path
Currently, the WBS attribute of summary tasks (WBS entities in P6) will be a dot
separated hierarchy of all the parent WBS attributes.
In this example, `root.wbs1.wbs2` is the WBS attribute for `wbs2` which has
the parents `root` and `wbs1`. To disable this behaviour, and simply record
the code for the current WBS entry (in the example above `wbs2`) call the
`setWbsIsFullPath` method, passing in `false`, as illustrated below.  

=== "Java"
	```java
	package org.mpxj.howto.read;
	
	import org.mpxj.primavera.PrimaveraDatabaseReader;
	
	public class P6WbsFullPath
	{
		public void read() throws Exception
		{
			PrimaveraDatabaseReader reader = new PrimaveraDatabaseReader();
			reader.setWbsIsFullPath(false);
		}
	}
	```

=== "C#"
	```c#
	using MPXJ.Net;
	
	namespace MPXJ.Samples.HowToRead;

	public class P6WbsFullPath
	{
		public void Read()
		{
			var reader = new PrimaveraDatabaseReader();
			reader.WbsIsFullPath = false;
		}
	}
	```


### Ignore Errors
By default MPXJ will ignore errors when parsing attributes from a Primavera
database. This behavior is controlled using the `setIgnoreErrors` method. The
example below illustrates how we can force the `PrimaveraDatabaseReader` to
report errors encountered when reading from a Primavera database:

=== "Java"
	```java
	package org.mpxj.howto.read;
	
	import org.mpxj.primavera.PrimaveraDatabaseReader;
	
	public class P6IgnoreErrors
	{
		public void read() throws Exception
		{
			PrimaveraDatabaseReader reader = new PrimaveraDatabaseReader();
			reader.setIgnoreErrors(false);
		}
	}
	```

=== "C#"
	```c#
	using MPXJ.Net;
	
	namespace MPXJ.Samples.HowToRead;
	
	public class P6IgnoreErrors
	{
	 	public void Read()
	 	{
		  	var reader = new PrimaveraDatabaseReader();
		  	reader.IgnoreErrors = false;
	 	}
	}
	```

Note that if errors are ignored when reading from a Primavera database, the
ignored errors are available by using the `ProjectFile.getIgnoredErrors()`
method.

### Reading Additional Attributes
A data-driven approach is used to extract the attributes used by MPXJ from the
database. You can if you wish change the way attributes are read from the file,
or add support for additional attributes. This assumes that you know the column
name of the attributes you want to work with in the database. To make changes
you will need to retrieve the maps which define which MPXJ attributes are used
to store which columns from the database:

```java
package org.mpxj.howto.read;

import org.mpxj.FieldType;
import org.mpxj.primavera.PrimaveraDatabaseReader;

import java.util.Map;

public class P6AttributeMaps
{
   public void read() throws Exception
   {
      PrimaveraDatabaseReader reader = new PrimaveraDatabaseReader();
      Map<FieldType, String> resourceFieldMap = reader.getResourceFieldMap();
      Map<FieldType, String> wbsFieldMap = reader.getWbsFieldMap();
      Map<FieldType, String> activityFieldMap = reader.getActivityFieldMap();
      Map<FieldType, String> assignmentFieldMap = reader.getAssignmentFieldMap();
   }
}
```

These maps will contain the default mapping between columns and MPXJ attributes.
You can modify these existing mappings, or add new ones, for example:

```java
package org.mpxj.howto.read;

import org.mpxj.FieldType;
import org.mpxj.TaskField;
import org.mpxj.primavera.PrimaveraDatabaseReader;

import java.util.Map;

public class P6AttributeConfig
{
   public void read() throws Exception
   {
      PrimaveraDatabaseReader reader = new PrimaveraDatabaseReader();
      Map<FieldType, String> activityFieldMap = reader.getActivityFieldMap();

      //
      // Store rsrc_id in NUMBER1
      //
      activityFieldMap.put(TaskField.NUMBER1, "rsrc_id");

      //
      // Read an Activity column called an_example_field and store it in TEXT10
      //
      activityFieldMap.put(TaskField.TEXT10, "an_example_field");
   }
}
```

