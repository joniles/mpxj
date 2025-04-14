/*
 * file:       PrimaveraConvert.java
 * author:     Jon Iles
 * copyright:  (c) Packwood Software 2010
 * date:       23/03/2010
 */

/*
 * This library is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as published by the
 * Free Software Foundation; either version 2.1 of the License, or (at your
 * option) any later version.
 *
 * This library is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY
 * or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public
 * License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this library; if not, write to the Free Software Foundation, Inc.,
 * 59 Temple Place, Suite 330, Boston, MA 02111-1307, USA.
 */

package org.mpxj.sample;

import java.sql.Connection;
import java.sql.DriverManager;
import java.util.Arrays;
import java.util.Properties;
import java.util.stream.Collectors;

import org.mpxj.ProjectFile;
import org.mpxj.primavera.PrimaveraDatabaseReader;
import org.mpxj.writer.FileFormat;
import org.mpxj.writer.UniversalProjectWriter;

/**
 * This utility is design simply to illustrate the use of the
 * PrimaveraReader class. Example commend line:
 * <p/>
 * {@code PrimaveraConvert "com.microsoft.sqlserver.jdbc.SQLServerDriver" "jdbc:sqlserver://localhost;database=PMDB;user=privuser;password=privuser" 1 "c:\temp\project1.xml"}
 * <p/>
 * This assumes the use of the Microsoft JDBC driver to access the PMDB database on
 * a local SQL Server instance. The project with ID=1 is exported to
 * an MSPDI file.
 */
public final class PrimaveraConvert
{
   /**
    * Main method.
    *
    * @param args array of command line arguments
    */
   public static void main(String[] args)
   {
      try
      {
         if (args.length != 5)
         {
            System.out.println("Usage: PrimaveraConvert <JDBC Driver Class> <JDBC connection string> <project ID> <output format> <output file name>");
            System.out.println("(valid output format values: " + Arrays.stream(FileFormat.values()).map(Enum::name).collect(Collectors.joining(", ")) + ")");
         }
         else
         {
            PrimaveraConvert convert = new PrimaveraConvert();
            convert.process(args[0], args[1], args[2], FileFormat.valueOf(args[3]), args[4]);
         }
      }

      catch (Exception ex)
      {
         ex.printStackTrace(System.out);
      }
   }

   /**
    * Extract Primavera project data and export in another format.
    *
    * @param driverClass JDBC driver class name
    * @param connectionString JDBC connection string
    * @param projectID project ID
    * @param outputFormat output format
    * @param outputFile output file
    */
   public void process(String driverClass, String connectionString, String projectID, FileFormat outputFormat, String outputFile) throws Exception
   {
      Class.forName(driverClass);
      Properties props = new Properties();

      //
      // This is not a very robust way to detect that we're working with SQLite...
      // If you are trying to grab data from a standalone P6 using SQLite, the
      // SQLite JDBC driver needs this property in order to correctly parse timestamps.
      // A better approach is to use UniversalProjectReader or PrimaveraDatabaseFileReader
      // rather than connecting to the database via JDBC.
      //
      if (driverClass.equals("org.sqlite.JDBC"))
      {
         props.setProperty("date_string_format", "yyyy-MM-dd HH:mm:ss");
      }

      Connection c = DriverManager.getConnection(connectionString, props);
      PrimaveraDatabaseReader reader = new PrimaveraDatabaseReader();
      reader.setConnection(c);

      PrimaveraDatabaseReader baselineReader = new PrimaveraDatabaseReader();
      baselineReader.setConnection(c);

      processProject(reader, baselineReader, Integer.parseInt(projectID), outputFormat, outputFile);
   }

   /**
    * Process a single project.
    *
    * @param reader Primavera reader
    * @param baselineReader Primavera reader
    * @param projectID required project ID
    * @param outputFormat output format
    * @param outputFile output file name
    */
   private void processProject(PrimaveraDatabaseReader reader, PrimaveraDatabaseReader baselineReader, int projectID, FileFormat outputFormat, String outputFile) throws Exception
   {
      System.out.println("Reading Primavera database started.");
      long start = System.currentTimeMillis();
      reader.setProjectID(projectID);
      ProjectFile projectFile = reader.read();
      long elapsed = System.currentTimeMillis() - start;
      System.out.println("Reading database completed in " + elapsed + "ms.");

      Integer baselineProjectID = projectFile.getProjectProperties().getBaselineProjectUniqueID();
      if (baselineProjectID != null)
      {
         System.out.println("Reading baseline started.");
         start = System.currentTimeMillis();

         baselineReader.setProjectID(baselineProjectID.intValue());
         ProjectFile baselineProjectFile = baselineReader.read();
         projectFile.setBaseline(baselineProjectFile);
         elapsed = System.currentTimeMillis() - start;
         System.out.println("Reading baseline completed in " + elapsed + "ms.");
      }

      System.out.println("Writing output file started.");
      start = System.currentTimeMillis();
      new UniversalProjectWriter(outputFormat).write(projectFile, outputFile);
      elapsed = System.currentTimeMillis() - start;
      System.out.println("Writing output completed in " + elapsed + "ms.");
   }
}
