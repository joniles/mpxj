/*
 * file:       DataExportUtility.java
 * author:     Jon Iles
 * copyright:  (c) Packwood Software 2011
 * date:       05/04/2011
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

package net.sf.mpxj.utility;

import java.io.FileWriter;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.sql.Types;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Simple utility to export data to an XML file from an arbitrary database
 * schema.
 */
public final class DataExportUtility
{
   /**
    * Command line entry point.
    *
    * @param argv command line arguments
    */
   public static void main(String[] argv)
   {
      if (argv.length != 2)
      {
         System.out.println("DataExport <filename> <output directory>");
      }
      else
      {
         Connection connection = null;

         try
         {
            Class.forName("sun.jdbc.odbc.JdbcOdbcDriver");
            String url = "jdbc:odbc:DRIVER=Microsoft Access Driver (*.mdb);DBQ=" + argv[0];
            connection = DriverManager.getConnection(url);

            DataExportUtility dx = new DataExportUtility();
            dx.process(connection, argv[1]);
         }

         catch (Exception ex)
         {
            ex.printStackTrace();
         }

         finally
         {
            if (connection != null)
            {
               try
               {
                  connection.close();
               }

               catch (SQLException ex)
               {
                  // silently ignore exceptions when closing connection
               }
            }
         }
      }
   }

   /**
    * Export data base contents to a directory using supplied connection.
    *
    * @param connection database connection
    * @param directory target directory
    * @throws Exception
    */
   public void process(Connection connection, String directory) throws Exception
   {
      connection.setAutoCommit(true);

      //
      // Retrieve meta data about the connection
      //
      DatabaseMetaData dmd = connection.getMetaData();

      String[] types =
      {
         "TABLE"
      };

      FileWriter fw = new FileWriter(directory);
      PrintWriter pw = new PrintWriter(fw);

      pw.println("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
      pw.println();
      pw.println("<database>");

      ResultSet tables = dmd.getTables(null, null, null, types);
      while (tables.next() == true)
      {
         processTable(pw, connection, tables.getString("TABLE_NAME"));
      }

      pw.println("</database>");

      pw.close();

      tables.close();
   }

   /**
    * Process a single table.
    *
    * @param pw output print writer
    * @param connection database connection
    * @param name table name
    * @throws Exception
    */
   private void processTable(PrintWriter pw, Connection connection, String name) throws Exception
   {
      System.out.println("Processing " + name);

      //
      // Prepare statement to retrieve all data
      //
      PreparedStatement ps = connection.prepareStatement("select * from " + name);

      //
      // Execute the query
      //
      ResultSet rs = ps.executeQuery();

      //
      // Retrieve column meta data
      //
      ResultSetMetaData rmd = ps.getMetaData();

      int index;
      int columnCount = rmd.getColumnCount();
      String[] columnNames = new String[columnCount];
      int[] columnTypes = new int[columnCount];
      int[] columnPrecision = new int[columnCount];
      int[] columnScale = new int[columnCount];

      for (index = 0; index < columnCount; index++)
      {
         columnNames[index] = rmd.getColumnName(index + 1);
         columnTypes[index] = rmd.getColumnType(index + 1);
         if (columnTypes[index] == Types.NUMERIC)
         {
            columnPrecision[index] = rmd.getPrecision(index + 1);
            columnScale[index] = rmd.getScale(index + 1);
         }
      }

      //
      // Generate the output file
      //
      pw.println("<table name=\"" + name + "\">");

      StringBuilder buffer = new StringBuilder(255);
      DateFormat df = new SimpleDateFormat("dd/MM/yyyy hh:mm", Locale.UK);

      while (rs.next() == true)
      {
         pw.println(" <row>");

         for (index = 0; index < columnCount; index++)
         {
            switch (columnTypes[index])
            {
               case Types.BINARY:
               case Types.BLOB:
               case Types.LONGVARBINARY:
               case Types.VARBINARY:
               {
                  pw.print("  <column name=\"" + columnNames[index] + "\" type=\"" + columnTypes[index] + "\">");

                  pw.println("[BINARY DATA]");

                  pw.println("</column>");

                  break;
               }

               case Types.DATE:
               case Types.TIME:
               {
                  Date data = rs.getDate(index + 1);
                  //if (data != null)
                  {
                     pw.print("  <column name=\"" + columnNames[index] + "\" type=\"" + columnTypes[index] + "\">");
                     if (data != null)
                     {
                        pw.print(df.format(data));
                     }
                     pw.println("</column>");
                  }
                  break;
               }

               case Types.TIMESTAMP:
               {
                  Timestamp data = rs.getTimestamp(index + 1);
                  //if (data != null)
                  {
                     pw.print("  <column name=\"" + columnNames[index] + "\" type=\"" + columnTypes[index] + "\">");
                     if (data != null)
                     {
                        pw.print(data.toString());
                     }
                     pw.println("</column>");
                  }
                  break;
               }

               case Types.NUMERIC:
               {
                  //
                  // If we have a non-null value, map the value to a
                  // more specific type
                  //
                  String data = rs.getString(index + 1);
                  //if (data != null)
                  {
                     int type = Types.NUMERIC;
                     int precision = columnPrecision[index];
                     int scale = columnScale[index];

                     if (scale == 0)
                     {
                        if (precision == 10)
                        {
                           type = Types.INTEGER;
                        }
                        else
                        {
                           if (precision == 5)
                           {
                              type = Types.SMALLINT;
                           }
                           else
                           {
                              if (precision == 1)
                              {
                                 type = Types.BIT;
                              }
                           }
                        }
                     }
                     else
                     {
                        if (precision > 125)
                        {
                           type = Types.DOUBLE;
                        }
                     }

                     pw.print("  <column name=\"" + columnNames[index] + "\" type=\"" + type + "\">");
                     if (data != null)
                     {
                        pw.print(data);
                     }
                     pw.println("</column>");
                  }
                  break;
               }

               default:
               {
                  String data = rs.getString(index + 1);
                  //if (data != null)
                  {
                     pw.print("  <column name=\"" + columnNames[index] + "\" type=\"" + columnTypes[index] + "\">");
                     if (data != null)
                     {
                        pw.print(escapeText(buffer, data));
                     }
                     pw.println("</column>");
                  }
                  break;
               }
            }
         }

         pw.println(" </row>");
      }

      pw.println("</table>");

      ps.close();

   }

   /**
    * Quick and dirty XML text escape.
    *
    * @param sb working string buffer
    * @param text input text
    * @return escaped text
    */
   private String escapeText(StringBuilder sb, String text)
   {
      int length = text.length();
      char c;

      sb.setLength(0);

      for (int loop = 0; loop < length; loop++)
      {
         c = text.charAt(loop);

         switch (c)
         {
            case '<':
            {
               sb.append("&lt;");
               break;
            }

            case '>':
            {
               sb.append("&gt;");
               break;
            }

            case '&':
            {
               sb.append("&amp;");
               break;
            }

            default:
            {
               if (validXMLCharacter(c) == true)
               {
                  if (c > 127)
                  {
                     sb.append("&#" + (int) c + ";");
                  }
                  else
                  {
                     sb.append(c);
                  }
               }

               break;
            }
         }
      }

      return (sb.toString());
   }

   /**
    * Quick and dirty valid XML character test.
    *
    * @param c input character
    * @return Boolean flag
    */
   private boolean validXMLCharacter(char c)
   {
      return (c == 0x9 || c == 0xA || c == 0xD || (c >= 0x20 && c <= 0xD7FF) || (c >= 0xE000 && c <= 0xFFFD) || (c >= 0x10000 && c <= 0x10FFFF));
   }
}
