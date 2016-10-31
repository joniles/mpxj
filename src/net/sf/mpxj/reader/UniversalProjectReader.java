/*
 * file:       UniversalProjectReader.java
 * author:     Jon Iles
 * copyright:  (c) Packwood Software 2016
 * date:       2016-10-13
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
import java.util.LinkedList;
import java.util.List;
import java.util.Properties;
import java.util.Set;
import java.util.regex.Pattern;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

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
import net.sf.mpxj.mspdi.MSPDIReader;
import net.sf.mpxj.planner.PlannerReader;
import net.sf.mpxj.primavera.PrimaveraDatabaseReader;
import net.sf.mpxj.primavera.PrimaveraPMFileReader;
import net.sf.mpxj.primavera.PrimaveraXERFileReader;

/**
 * This class implements a universal project reader: given a file or a stream this reader
 * will sample the content and determine the type of file it has been given. It will then
 * instantiate the correct reader for that file type and proceed to read the file.
 */
public class UniversalProjectReader extends AbstractProjectReader
{
   /**
    * {@inheritDoc}
    */
   @Override public void addProjectListener(ProjectListener listener)
   {
      if (m_projectListeners == null)
      {
         m_projectListeners = new LinkedList<ProjectListener>();
      }
      m_projectListeners.add(listener);
   }

   /**
    * Note that this method returns null if we can't determine the file type.
    *
    * {@inheritDoc}
    */
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
         // it's probably not a valid schedule file.
         //
         if (bytesRead != BUFFER_SIZE)
         {
            throw new MPXJException(MPXJException.INVALID_FILE);
         }

         if (matchesFingerprint(buffer, MPP_FINGERPRINT))
         {
            return readProjectFile(new MPPReader(), bis);
         }

         if (matchesFingerprint(buffer, MSPDI_FINGERPRINT))
         {
            return new MSPDIReader().read(bis);
         }

         if (matchesFingerprint(buffer, PP_FINGERPRINT))
         {
            return readProjectFile(new AstaFileReader(), bis);
         }

         if (matchesFingerprint(buffer, MPX_FINGERPRINT))
         {
            return readProjectFile(new MPXReader(), bis);
         }

         if (matchesFingerprint(buffer, XER_FINGERPRINT))
         {
            return readProjectFile(new PrimaveraXERFileReader(), bis);
         }

         if (matchesFingerprint(buffer, PLANNER_FINGERPRINT))
         {
            return readProjectFile(new PlannerReader(), bis);
         }

         if (matchesFingerprint(buffer, PMXML_FINGERPRINT))
         {
            return readProjectFile(new PrimaveraPMFileReader(), bis);
         }

         if (matchesFingerprint(buffer, MDB_FINGERPRINT))
         {
            return handleMDBFile(bis);
         }

         if (matchesFingerprint(buffer, SQLITE_FINGERPRINT))
         {
            return handleSQLiteFile(bis);
         }

         if (matchesFingerprint(buffer, ZIP_FINGERPRINT))
         {
            return handleZipFile(bis);
         }

         return null;
      }

      catch (Exception ex)
      {
         throw new MPXJException(MPXJException.INVALID_FILE, ex);
      }
   }

   /**
    * Determine if the start of the buffer matches a fingerprint byte array.
    *
    * @param buffer bytes from file
    * @param fingerprint fingerprint bytes
    * @return true if the file matches the fingerprint
    */
   private boolean matchesFingerprint(byte[] buffer, byte[] fingerprint)
   {
      return Arrays.equals(fingerprint, Arrays.copyOf(buffer, fingerprint.length));
   }

   /**
    * Determine if the buffer, when expressed as text, matches a fingerprint regular expression.
    *
    * @param buffer bytes from file
    * @param fingerprint fingerprint regular expression
    * @return true if the file matches the fingerprint
    */
   private boolean matchesFingerprint(byte[] buffer, Pattern fingerprint)
   {
      return fingerprint.matcher(new String(buffer)).matches();
   }

   /**
    * Adds listeners and reads from a stream.
    *
    * @param reader reader for file type
    * @param stream schedule data
    * @return ProjectFile instance
    */
   private ProjectFile readProjectFile(ProjectReader reader, InputStream stream) throws MPXJException
   {
      addListeners(reader);
      return reader.read(stream);
   }

   /**
    * Adds listeners and reads from a file.
    *
    * @param reader reader for file type
    * @param file schedule data
    * @return ProjectFile instance
    */
   private ProjectFile readProjectFile(ProjectReader reader, File file) throws MPXJException
   {
      addListeners(reader);
      return reader.read(file);
   }

   /**
    * We have identified that we have an MDB file. This could be a Microsoft Project database
    * or an Asta database. Open the database and use the table names present to determine
    * which type this is.
    *
    * @param stream schedule data
    * @return ProjectFile instance
    */
   private ProjectFile handleMDBFile(InputStream stream) throws Exception
   {
      File file = InputStreamHelper.writeStreamToTempFile(stream, ".mdb");

      try
      {
         Class.forName("sun.jdbc.odbc.JdbcOdbcDriver");
         String url = "jdbc:odbc:DRIVER=Microsoft Access Driver (*.mdb);DBQ=" + file.getCanonicalPath();
         Set<String> tableNames = populateTableNames(url);

         if (tableNames.contains("MSP_PROJECTS"))
         {
            return readProjectFile(new MPDDatabaseReader(), file);
         }

         if (tableNames.contains("EXCEPTIONN"))
         {
            return readProjectFile(new AstaDatabaseReader(), file);
         }

         return null;
      }

      finally
      {
         file.delete();
      }
   }

   /**
    * We have identified that we have a SQLite file. This could be a Primavera Project database
    * or an Asta database. Open the database and use the table names present to determine
    * which type this is.
    *
    * @param stream schedule data
    * @return ProjectFile instance
    */
   private ProjectFile handleSQLiteFile(InputStream stream) throws Exception
   {
      File file = InputStreamHelper.writeStreamToTempFile(stream, ".sqlite");

      try
      {
         Class.forName("org.sqlite.JDBC");
         String url = "jdbc:sqlite:" + file.getCanonicalPath();
         Set<String> tableNames = populateTableNames(url);

         if (tableNames.contains("EXCEPTIONN"))
         {
            return readProjectFile(new AstaDatabaseFileReader(), file);
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
               addListeners(reader);
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

   /**
    * We have identified that we have a zip file. Let's assume that it contains just one
    * entry so pass the stream representing that entry to UniversalProjectReader to
    * see if we recognise the file type.
    *
    * @param stream schedule data
    * @return ProjectFile instance
    */
   private ProjectFile handleZipFile(InputStream stream) throws Exception
   {
      ZipInputStream zip = new ZipInputStream(stream);
      ZipEntry entry = zip.getNextEntry();
      if (entry != null)
      {
         return new UniversalProjectReader().read(zip);
      }
      return null;
   }

   /**
    * Open a database and build a set of table names.
    *
    * @param url database URL
    * @return set containing table names
    */
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

   /**
    * Adds any listeners attached to this reader to the reader created internally.
    *
    * @param reader internal project reader
    */
   private void addListeners(ProjectReader reader)
   {
      if (m_projectListeners != null)
      {
         for (ProjectListener listener : m_projectListeners)
         {
            reader.addProjectListener(listener);
         }
      }
   }

   private List<ProjectListener> m_projectListeners;

   private static final int BUFFER_SIZE = 512;

   private static final byte[] MPP_FINGERPRINT =
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

   private static final byte[] ZIP_FINGERPRINT =
   {
      (byte) 'P',
      (byte) 'K'
   };

   private static final Pattern PLANNER_FINGERPRINT = Pattern.compile(".*<project.*mrproject-version.*", Pattern.DOTALL);

   private static final Pattern PMXML_FINGERPRINT = Pattern.compile(".*<APIBusinessObjects.*", Pattern.DOTALL);

   private static final Pattern MSPDI_FINGERPRINT = Pattern.compile(".*xmlns=\"http://schemas\\.microsoft\\.com/project\".*", Pattern.DOTALL);

}
