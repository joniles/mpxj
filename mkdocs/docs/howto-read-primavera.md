# How To: Read a Primavera P6 database
Reading from a Primavera database is a slightly different proposition
to reading file-based project data, as a database connection is required.

## Java
The example below illustrates how to do this for a Primavera database
hosted in SQL Server, using the open source JTDS JDBC driver. 
The only difference when reading from an Oracle
database will be the JDBC driver and connection string used.

```java
package org.mpxj.howto.read;

import net.sf.mpxj.ProjectFile;
import net.sf.mpxj.primavera.PrimaveraDatabaseReader;

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
      String driverClass="com.microsoft.sqlserver.jdbc.SQLServerDriver";
      Class.forName(driverClass);

      //
      // Open a database connection. You will need to change
      // these details to match the name of your server, database, user and password.
      //
      String connectionString="jdbc:sqlserver://localhost:1433;databaseName=my-database-name;user=my-user-name;password=my-password;";
      Connection c = DriverManager.getConnection(connectionString);
      PrimaveraDatabaseReader reader = new PrimaveraDatabaseReader();
      reader.setConnection(c);

      //
      // Retrieve a list of the projects available in the database
      //
      Map<Integer,String> projects = reader.listProjects();

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

You can also connect to a standalone SQLite P6 database. This
is easier to achieve as a specific reader class has been created
which manages the database connection for you:

```java
package org.mpxj.howto.read;

import net.sf.mpxj.ProjectFile;
import net.sf.mpxj.primavera.PrimaveraDatabaseFileReader;

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
      ProjectFile projectFile = reader.read("PPMDBSQLite.db");
   }
}
```

## .Net
This documentation will be provided shortly.

## Using PrimaveraDatabaseReader
This section documents the additional options provided by the PrimaveraDatabaseReader.

 
### Activity WBS
In the original implementation of the database handling code, MPXJ would assign
each task representing a Primavera Activity its own distinct WBS value. This
does not match Primavera's behaviour where all of a WBS element's child
activities will have the same WBS value as the parent WBS element. MPXJ's
default behaviour now matches Primavera, but should you wish to you can revert
to the original behaviour by calling the `setMatchPrimaveraWBS` as shown below.

```java
package org.mpxj.howto.read;

import net.sf.mpxj.primavera.PrimaveraDatabaseReader;

public class P6ActivityWbs
{
   public void read() throws Exception
   {
      PrimaveraDatabaseReader reader = new PrimaveraDatabaseReader();
      reader.setMatchPrimaveraWBS(false);
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


```java
package org.mpxj.howto.read;

import net.sf.mpxj.primavera.PrimaveraDatabaseReader;

public class P6WbsFullPath
{
   public void read() throws Exception
   {
      PrimaveraDatabaseReader reader = new PrimaveraDatabaseReader();
      reader.setWbsIsFullPath(false);
   }
}
```

### Reading Additional Attributes
A data-driven approach is used to extract the attributes used by MPXJ from the
database. You can if you wish change the way attributes are read from the file,
or add support for additional attributes. This assumes that you know the column
name of the attributes you want to work with in the database. To make changes
you will need to retrieve the maps which define which MPXJ attributes are used
to store which columns from the database:

```java
package org.mpxj.howto.read;

import net.sf.mpxj.FieldType;
import net.sf.mpxj.primavera.PrimaveraDatabaseReader;

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

import net.sf.mpxj.FieldType;
import net.sf.mpxj.TaskField;
import net.sf.mpxj.primavera.PrimaveraDatabaseReader;

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

### Ignore Errors
By default MPXJ will ignore errors when parsing attributes from a Primavera
database. This behavior is controlled using the `setIgnoreErrors` method. The
example below illustrates how we can force the `PrimaveraDatabaseReader` to
report errors encountered when reading from a Primavera database:

```java
package org.mpxj.howto.read;

import net.sf.mpxj.primavera.PrimaveraDatabaseReader;

public class P6IgnoreErrors
{
   public void read() throws Exception
   {
      PrimaveraDatabaseReader reader = new PrimaveraDatabaseReader();
      reader.setIgnoreErrors(false);
   }
}
```

Note that if errors are ignored when reading from a Primavera database, the
ignored errors are available by using the `ProjectFile.getIgnoredErrors()`
method.
