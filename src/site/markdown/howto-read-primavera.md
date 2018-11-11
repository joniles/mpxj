## How To: Read a Primavera P6 database
Reading from a Primavera database is a slightly different proposition
to reading file-based project data, as a database connection is required.

### Java 
The example below illustrates how to do this for a Primavera database
hosted in SQL Server, using the open source JTDS JDBC driver. 
The only difference when reading from an Oracle
database will be the JDBC driver and connection string used.

```
import java.sql.Connection;
import java.sql.DriverManager;
import net.sf.mpxj.ProjectFile;
import net.sf.mpxj.primavera.PrimaveraDatabaseReader;

...

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

You can also connect to a standalone SQLite P6 database, although a 
property has to be set on the database connection in order for
date and time values to be read correctly.

```
import java.sql.Connection;
import java.sql.DriverManager;
import net.sf.mpxj.ProjectFile;
import net.sf.mpxj.primavera.PrimaveraDatabaseReader;

...

//
// Load the JDBC driver
//
String driverClass="org.sqlite.JDBC";
Class.forName(driverClass);

//
// Open a database connection. You will need to change
// these details to match the location of your database file.
//
String connectionString="jdbc:sqlite:C:/temp/PPMDBSQLite.db";
Properties props = new Properties();
props.setProperty("date_string_format", "yyyy-MM-dd HH:mm:ss");
Connection c = DriverManager.getConnection(connectionString, props);
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

### .Net
The situation is a little more complicated when using the .Net version of MPXJ.
In this case you are still actually running Java code, so you need to use a JDBC
driver to establish a database connection.

Your first step will be to convert your JDBC driver to a .Net assembly using IKVM. For example
the command line below converts a version of Microsoft's SQL Server JDBC driver to a .Net
assembly:

```
c:\java\ikvm-8.0.5449.1\bin\ikvmc.exe -out:mssql-jdbc-6.4.0.jre8.dll -target:library -keyfile:c:\java\mpxj\src.net\mpxj.snk -version:6.4.0.0 mssql-jdbc-6.4.0.jre8.jar
```

You can then add a reference to this assembly to your project. Configuring the JDBC driver
needs to be done in a slightly different way than you would using Java. Here we need to
create an instance of the JDBC driver class directly, rather than referencing it by name as we would in Java.

```
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

You can find the complete code for this [here](https://github.com/joniles/mpxj/blob/master/src.net/MpxjPrimaveraConvert/MpxjPrimaveraConvert.cs).
