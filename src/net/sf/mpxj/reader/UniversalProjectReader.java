
package net.sf.mpxj.reader;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Properties;
import java.util.Set;
import java.util.regex.Pattern;

import net.sf.mpxj.MPXJException;
import net.sf.mpxj.ProjectFile;
import net.sf.mpxj.asta.AstaDatabaseFileReader;
import net.sf.mpxj.asta.AstaDatabaseReader;
import net.sf.mpxj.asta.AstaFileReader;
import net.sf.mpxj.common.InputStreamHelper;
import net.sf.mpxj.listener.ProjectListener;
import net.sf.mpxj.mpd.MPDDatabaseReader;
import net.sf.mpxj.mpp.MPPReader;
import net.sf.mpxj.mpx.MPXReader;
import net.sf.mpxj.planner.PlannerReader;
import net.sf.mpxj.primavera.PrimaveraDatabaseReader;
import net.sf.mpxj.primavera.PrimaveraPMFileReader;
import net.sf.mpxj.primavera.PrimaveraXERFileReader;

public class UniversalProjectReader extends AbstractProjectReader
{

   @Override public void addProjectListener(ProjectListener listener)
   {
      // TODO Auto-generated method stub

   }

   @Override public ProjectFile read(InputStream inputStream) throws MPXJException
   {
      try
      {
         BufferedInputStream bis = new BufferedInputStream(inputStream);
         bis.mark(BUFFER_SIZE);
         byte[] buffer = new byte[BUFFER_SIZE];
         int bytesRead = bis.read(buffer);
         bis.reset();

         //
         // If the file is smaller than the buffer we are peeking into,
         // it's not a valid schedule file.
         //
         if (bytesRead != BUFFER_SIZE)
         {
            throw new MPXJException(MPXJException.INVALID_FILE);
         }

         if (matchesFingerprint(buffer, MPP_FINGERPRINT))
         {
            return new MPPReader().read(bis);
         }

         if (matchesFingerprint(buffer, PP_FINGERPRINT))
         {
            return new AstaFileReader().read(bis);
         }

         if (matchesFingerprint(buffer, MPX_FINGERPRINT))
         {
            return new MPXReader().read(bis);
         }

         if (matchesFingerprint(buffer, XER_FINGERPRINT))
         {
            return new PrimaveraXERFileReader().read(bis);
         }

         if (matchesFingerprint(buffer, PLANNER_FINGERPRINT))
         {
            return new PlannerReader().read(bis);
         }

         if (matchesFingerprint(buffer, PMXML_FINGERPRINT))
         {
            return new PrimaveraPMFileReader().read(bis);
         }

         if (matchesFingerprint(buffer, MDB_FINGERPRINT))
         {
            return handleMDBFile(bis);
         }

         if (matchesFingerprint(buffer, SQLITE_FINGERPRINT))
         {
            return handleSQLiteFile(bis);
         }

         return null;
      }

      catch (Exception ex)
      {
         throw new MPXJException(MPXJException.INVALID_FILE, ex);
      }
   }

   private boolean matchesFingerprint(byte[] buffer, byte[] fingerprint)
   {
      return Arrays.equals(fingerprint, Arrays.copyOf(buffer, fingerprint.length));
   }

   private boolean matchesFingerprint(byte[] buffer, Pattern fingerprint)
   {
      return fingerprint.matcher(new String(buffer)).matches();
   }

   private ProjectFile handleMDBFile(InputStream is) throws Exception
   {
      File file = InputStreamHelper.writeStreamToTempFile(is, ".mdb");

      try
      {
         Class.forName("sun.jdbc.odbc.JdbcOdbcDriver");
         String url = "jdbc:odbc:DRIVER=Microsoft Access Driver (*.mdb);DBQ=" + file.getCanonicalPath();
         Set<String> tableNames = populateTableNames(url);

         if (tableNames.contains("MSP_PROJECTS"))
         {
            return new MPDDatabaseReader().read(file);
         }

         if (tableNames.contains("EXCEPTIONN"))
         {
            return new AstaDatabaseReader().read(file);
         }

         return null;
      }

      finally
      {
         file.delete();
      }
   }

   private ProjectFile handleSQLiteFile(InputStream is) throws Exception
   {
      File file = InputStreamHelper.writeStreamToTempFile(is, ".sqlite");

      try
      {
         Class.forName("org.sqlite.JDBC");
         String url = "jdbc:sqlite:" + file.getCanonicalPath();
         Set<String> tableNames = populateTableNames(url);

         if (tableNames.contains("EXCEPTIONN"))
         {
            return new AstaDatabaseFileReader().read(file);
         }

         if (tableNames.contains("PROJWBS"))
         {
            Connection connection = null;
            try
            {
               Properties props = new Properties();
               props.setProperty("date_string_format", "yyyy-MM-dd HH:mm:ss");
               connection = DriverManager.getConnection(url, props);
               PrimaveraDatabaseReader reader = new PrimaveraDatabaseReader();
               reader.setConnection(connection);
               return reader.read();
            }
            finally
            {
               if (connection != null)
               {
                  connection.close();
               }
            }
         }

         return null;
      }

      finally
      {
         file.delete();
      }
   }

   private Set<String> populateTableNames(String url) throws SQLException
   {
      Set<String> tableNames = new HashSet<String>();
      Connection connection = null;
      ResultSet rs = null;

      try
      {
         connection = DriverManager.getConnection(url);
         DatabaseMetaData dmd = connection.getMetaData();
         rs = dmd.getTables(null, null, null, null);
         while (rs.next())
         {
            tableNames.add(rs.getString("TABLE_NAME").toUpperCase());
         }
      }

      finally
      {
         if (rs != null)
         {
            rs.close();
         }

         if (connection != null)
         {
            connection.close();
         }
      }

      return tableNames;
   }

   private static final int BUFFER_SIZE = 255;

   private byte[] MPP_FINGERPRINT =
   {
      (byte) 0xD0,
      (byte) 0xCF,
      (byte) 0x11,
      (byte) 0xE0,
      (byte) 0xA1,
      (byte) 0xB1,
      (byte) 0x1A,
      (byte) 0xE1
   };

   private static final byte[] PP_FINGERPRINT =
   {
      (byte) 0x00,
      (byte) 0x00,
      (byte) 0x30,
      (byte) 0x30,
      (byte) 0x30,
      (byte) 0x30,
      (byte) 0x30,
      (byte) 0x30
   };

   private static final byte[] MPX_FINGERPRINT =
   {
      (byte) 'M',
      (byte) 'P',
      (byte) 'X',
      (byte) ','
   };

   private static final byte[] MDB_FINGERPRINT =
   {
      (byte) 0x00,
      (byte) 0x01,
      (byte) 0x00,
      (byte) 0x00,
      (byte) 'S',
      (byte) 't',
      (byte) 'a',
      (byte) 'n',
      (byte) 'd',
      (byte) 'a',
      (byte) 'r',
      (byte) 'd',
      (byte) ' ',
      (byte) 'J',
      (byte) 'e',
      (byte) 't',
      (byte) ' ',
      (byte) 'D',
      (byte) 'B',
   };

   private static final byte[] SQLITE_FINGERPRINT =
   {
      (byte) 'S',
      (byte) 'Q',
      (byte) 'L',
      (byte) 'i',
      (byte) 't',
      (byte) 'e',
      (byte) ' ',
      (byte) 'f',
      (byte) 'o',
      (byte) 'r',
      (byte) 'm',
      (byte) 'a',
      (byte) 't'
   };

   private static final byte[] XER_FINGERPRINT =
   {
      (byte) 'E',
      (byte) 'R',
      (byte) 'M',
      (byte) 'H',
      (byte) 'D',
      (byte) 'R'
   };

   private static final Pattern PLANNER_FINGERPRINT = Pattern.compile(".*<project.*mrproject-version.*", Pattern.DOTALL);

   private static final Pattern PMXML_FINGERPRINT = Pattern.compile(".*<APIBusinessObjects.*", Pattern.DOTALL);
}
