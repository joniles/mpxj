using com.microsoft.sqlserver.jdbc;
using java.util;
using net.sf.mpxj.primavera;
using net.sf.mpxj.writer;
using System;

namespace MpxjSample
{
    /// <summary>
    /// The purpose of this class is to demonstrate how you can configure a JDBC driver to work with the .Net
    /// version of MPXJ in order to extract data from a Primavera P6 SQL Server database.
    /// 
    /// Your first step will be to convert your JDBC driver to a .Net assembly using IKVM. For this example code we're
    /// using Microsoft's SQL Server JDBC Driver:
    /// 
    /// c:\java\ikvm-8.1.5717.0\bin\ikvmc.exe -out:mssql-jdbc-6.4.0.jre8.dll -target:library -keyfile:c:\java\mpxj\src.net\mpxj.snk -version:6.4.0.0 mssql-jdbc-6.4.0.jre8.jar
    /// 
    /// You can then add a reference to this assembly to your project, and the code below should work.
    /// 
    /// The utility is expecting to receive three command line arguments, a JDBC connection string, a project ID, and finally an outut file name.
    /// The structure of the connection string for the Microsoft JDBC driver is defined here:
    /// https://docs.microsoft.com/en-us/sql/connect/jdbc/building-the-connection-url?view=sql-server-2017
    /// 
    /// The available projectIDs can be found by calling `reader.ListProjects()`. The value is the primary key from the `project` table in
    /// the P6 database corresponding to the project you want to export.
    /// 
    /// The output file name is the file into which the utility will write an MSPDI file representing the data extracted from P6.
    /// 
    /// Here's an example of the arguments you might pass to this utility:
    /// 
    /// jdbc:sqlserver://myhostname;user=myusername;password=mypassword;databaseName=mydatabase 1234 project1.xml
    /// </summary>
    class MpxjPrimaveraConvert
    {
        static void Main(string[] args)
        {
          try
          {
             if (args.Length != 3)
             {
                 Console.Out.WriteLine("Usage: MpxjPrimaveraConvert <JDBC connection string> <project ID> <output file name>");
             }
             else
             {
                MpxjPrimaveraConvert convert = new MpxjPrimaveraConvert();
                convert.process(args[0], args[1], args[2]);
             }
          }

          catch (Exception ex)
          {
              Console.Out.WriteLine(ex.ToString());
          }
        }

        public void process (string connectionString, string projectID, string outputFile)
        {
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
            reader.ProjectID = Int32.Parse(projectID);
            
            Console.Out.WriteLine("Reading from database started.");
            var start = DateTime.Now;
            var projectFile = reader.Read();
            var elapsed = DateTime.Now - start;
            Console.Out.WriteLine("Reading from database completed in " + elapsed.TotalMilliseconds + "ms.");

            Console.Out.WriteLine("Writing output file started.");
            start = DateTime.Now;
            var writer = ProjectWriterUtility.getProjectWriter(outputFile);
            writer.write(projectFile, outputFile);
            elapsed = DateTime.Now - start;
            Console.Out.WriteLine("Writing output completed in " + elapsed.TotalMilliseconds + "ms.");
        }
    }
}
