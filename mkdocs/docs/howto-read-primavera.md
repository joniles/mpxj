# How To: Read a Primavera P6 database
Reading from a Primavera database is a slightly different proposition
to reading file-based project data, as a database connection is required.

## Java
The example below illustrates how to do this for a Primavera database
hosted in SQL Server, using the open source JTDS JDBC driver. 
The only difference when reading from an Oracle
database will be the JDBC driver and connection string used.

```java
import java.sql.Connection;
import java.sql.DriverManager;
import net.sf.mpxj.ProjectFile;
import net.sf.mpxj.primavera.PrimaveraDatabaseReader;

// ...

//
// Load the JDBC driver
//
String driverClass="net.sourceforge.jtds.jdbc.Driver";
Class.forName(driverClass);

//
// Open a database connection. You will need to change
// these details to match the name of your server, database, user and password.
//
String connectionString="jdbc:jtds:sqlserver://localhost/PMDB;user=pmdb;password=pmdb";
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
```

You can also connect to a standalone SQLite P6 database. This
is easier to achieve as a specific reader class has been created
which manages the database connection for you:

```java
import net.sf.mpxj.ProjectFile;
import net.sf.mpxj.primavera.PrimaveraDatabaseFileReader;

...

PrimaveraDatabaseFileReader reader = new PrimaveraDatabaseFileReader();

//
// Retrieve a list of the projects available in the database
//
Map<Integer,String> projects = reader.listProjects("PPMDBSQLite.db");

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
```

## .Net
The situation is a little more complicated when using the .Net version of MPXJ.
In this case you are still actually running Java code, so you need to use a JDBC
driver to establish a database connection.

Your first step will be to convert your JDBC driver to a .Net assembly using
IKVM. For example the command line below converts a version of Microsoft's SQL
Server JDBC driver to a .Net assembly:

```
c:\java\ikvm-8.0.5449.1\bin\ikvmc.exe -out:mssql-jdbc-6.4.0.jre8.dll -target:library -keyfile:c:\java\mpxj\src.net\mpxj.snk -version:6.4.0.0 mssql-jdbc-6.4.0.jre8.jar
```

You can then add a reference to this assembly to your project. Configuring the
JDBC driver needs to be done in a slightly different way than you would when using
Java. Here we need to create an instance of the JDBC driver class directly,
rather than referencing it by name as we would in Java.

```C#
//
// Configure the connection
//
var driver = new SQLServerDriver();
var connectionProperties = new Properties();
var connection = driver.connect(connectionString, connectionProperties);

//
// Configure the reader
//
var reader = new PrimaveraDatabaseReader();
reader.Connection = connection;

```

You can find the complete code for this
[here](https://github.com/joniles/mpxj/blob/master/src.net/MpxjPrimaveraConvert/MpxjPrimaveraConvert.cs).

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
import net.sf.mpxj.ProjectFile;
import net.sf.mpxj.primavera.PrimaveraDatabaseReader;

// ...

PrimaveraDatabaseReader reader = new PrimaveraDatabaseReader();
reader.setMatchPrimaveraWBS(false);
```

### WBS is Full Path
Currently, the WBS attribute of summary tasks (WBS entities in P6) will be a dot
separated hierarchy of all the parent WBS attributes.
In this example, `root.wbs1.wbs2` is the WBS attribute for `wbs2` which has
the parents `root` and `wbs1`. To disable this behaviour, and simply record
the code for the current WBS entry (in the example above `wbs2`) call the
`setWbsIsFullPath` method, passing in `false`, as illustrated below.  


```java
import net.sf.mpxj.ProjectFile;
import net.sf.mpxj.primavera.PrimaveraDatabaseReader;

// ...

PrimaveraDatabaseReader reader = new PrimaveraDatabaseReader();
reader.setWbsIsFullPath(false);
```

### Reading Additional Attributes
A data-driven approach is used to extract the attributes used by MPXJ from the
database. You can if you wish change the way attributes are read from the file,
or add support for additional attributes. This assumes that you know the column
name of the attributes you want to work with in the database. To make changes
you will need to retrieve the maps which define which MPXJ attributes are used
to store which columns from the database:

```java
PrimaveraDatabaseReader reader = new PrimaveraDatabaseReader();
Map<FieldType, String> resourceFieldMap = reader.getResourceFieldMap();
Map<FieldType, String> wbsFieldMap = reader.getWbsFieldMap();
Map<FieldType, String> activityFieldMap = reader.getActivityFieldMap();
Map<FieldType, String> assignmentFieldMap = reader.getAssignmentFieldMap();
```

These maps will contain the default mapping between columns and MPXJ attributes.
You can modify these existing mappings, or add new ones, for example:

```java
//
// Change the field used to store rsrc_id
//
activityFieldMap.remove(TaskField.NUMBER1);
activityFieldMap.put(TaskField.NUMBER2, "rsrc_id");

//
// Read an Activity column called an_example_field and store it in TEXT10
//
activityFieldMap.put(TaskField.TEXT10, "an_example_field");
```

### Ignore Errors
By default MPXJ will ignore errors when parsing attributes from a Primavera
database. This behavior is controlled using the `setIgnoreErrors` method. The
example below illustrates how we can force the `PrimaveraDatabaseReader` to
report errors encountered when reading from a Primavera database:

```java
import net.sf.mpxj.ProjectFile;
import net.sf.mpxj.mspdi.MSPDIReader;

// ...

PrimaveraDatabaseReader reader = new PrimaveraDatabaseReader();

reader.setIgnoreErrors(false);
```

Note that if errors are ignored when reading from a Primavera database, the
ignored errors are available by using the `ProjectFile.getIgnoredErrors()`
method.
