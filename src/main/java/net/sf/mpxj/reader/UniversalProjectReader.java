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
import java.io.FileInputStream;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Properties;
import java.util.Set;
import java.util.regex.Pattern;

import org.apache.poi.poifs.filesystem.POIFSFileSystem;

import net.sf.mpxj.MPXJException;
import net.sf.mpxj.ProjectFile;
import net.sf.mpxj.asta.AstaDatabaseFileReader;
import net.sf.mpxj.asta.AstaDatabaseReader;
import net.sf.mpxj.asta.AstaFileReader;
import net.sf.mpxj.common.CharsetHelper;
import net.sf.mpxj.common.FileHelper;
import net.sf.mpxj.common.InputStreamHelper;
import net.sf.mpxj.common.StreamHelper;
import net.sf.mpxj.conceptdraw.ConceptDrawProjectReader;
import net.sf.mpxj.fasttrack.FastTrackReader;
import net.sf.mpxj.ganttdesigner.GanttDesignerReader;
import net.sf.mpxj.ganttproject.GanttProjectReader;
import net.sf.mpxj.listener.ProjectListener;
import net.sf.mpxj.merlin.MerlinReader;
import net.sf.mpxj.mpd.MPDDatabaseReader;
import net.sf.mpxj.mpp.MPPReader;
import net.sf.mpxj.mpx.MPXReader;
import net.sf.mpxj.mspdi.MSPDIReader;
import net.sf.mpxj.phoenix.PhoenixInputStream;
import net.sf.mpxj.phoenix.PhoenixReader;
import net.sf.mpxj.planner.PlannerReader;
import net.sf.mpxj.primavera.PrimaveraDatabaseReader;
import net.sf.mpxj.primavera.PrimaveraPMFileReader;
import net.sf.mpxj.primavera.PrimaveraXERFileReader;
import net.sf.mpxj.primavera.p3.P3DatabaseReader;
import net.sf.mpxj.primavera.p3.P3PRXFileReader;
import net.sf.mpxj.primavera.suretrak.SureTrakDatabaseReader;
import net.sf.mpxj.primavera.suretrak.SureTrakSTXFileReader;
import net.sf.mpxj.projectlibre.ProjectLibreReader;
import net.sf.mpxj.synchro.SynchroReader;
import net.sf.mpxj.turboproject.TurboProjectReader;

/**
 * This class implements a universal project reader: given a file or a stream this reader
 * will sample the content and determine the type of file it has been given. It will then
 * instantiate the correct reader for that file type and proceed to read the file.
 */
public final class UniversalProjectReader implements ProjectReader
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
    * Package private method used when handling byte order mark.
    * Tells the reader to skip a number of bytes before starting to read from the stream.
    *
    * @param skipBytes number of bytes to skip
    */
   void setSkipBytes(int skipBytes)
   {
      m_skipBytes = skipBytes;
   }

   /**
    * Package private method used when handling byte order mark.
    * Notes the charset indicated by the byte order mark.
    *
    * @param charset character set indicated by byte order mark
    */
   void setCharset(Charset charset)
   {
      m_charset = charset;
   }

   @Override public ProjectFile read(String fileName) throws MPXJException
   {
      return read(new File(fileName));
   }

   @Override public ProjectFile read(File file) throws MPXJException
   {
      try
      {
         ProjectFile result;
         if (file.isDirectory())
         {
            result = handleDirectory(file);
         }
         else
         {
            FileInputStream fis = null;

            try
            {
               fis = new FileInputStream(file);
               ProjectFile projectFile = read(fis);
               fis.close();
               return (projectFile);
            }

            finally
            {
               StreamHelper.closeQuietly(fis);
            }
         }
         return result;
      }
      catch (Exception ex)
      {
         throw new MPXJException(MPXJException.INVALID_FILE, ex);
      }
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
         bis.skip(m_skipBytes);
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
            return null;
         }

         //
         // Always check for BOM first. Regex-based fingerprints may ignore these otherwise.
         //
         if (matchesFingerprint(buffer, UTF8_BOM_FINGERPRINT))
         {
            return handleByteOrderMark(bis, UTF8_BOM_FINGERPRINT.length, CharsetHelper.UTF8);
         }

         if (matchesFingerprint(buffer, UTF16_BOM_FINGERPRINT))
         {
            return handleByteOrderMark(bis, UTF16_BOM_FINGERPRINT.length, CharsetHelper.UTF16);
         }

         if (matchesFingerprint(buffer, UTF16LE_BOM_FINGERPRINT))
         {
            return handleByteOrderMark(bis, UTF16LE_BOM_FINGERPRINT.length, CharsetHelper.UTF16LE);
         }

         //
         // Now check for file fingerprints
         //
         if (matchesFingerprint(buffer, BINARY_PLIST))
         {
            return handleBinaryPropertyList(bis);
         }

         if (matchesFingerprint(buffer, OLE_COMPOUND_DOC_FINGERPRINT))
         {
            return handleOleCompoundDocument(bis);
         }

         if (matchesFingerprint(buffer, MSPDI_FINGERPRINT_1) || matchesFingerprint(buffer, MSPDI_FINGERPRINT_2))
         {
            MSPDIReader reader = new MSPDIReader();
            reader.setCharset(m_charset);
            return reader.read(bis);
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
            return handleXerFile(bis);
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

         if (matchesFingerprint(buffer, PHOENIX_FINGERPRINT))
         {
            return readProjectFile(new PhoenixReader(), new PhoenixInputStream(bis));
         }

         if (matchesFingerprint(buffer, PHOENIX_XML_FINGERPRINT))
         {
            return readProjectFile(new PhoenixReader(), bis);
         }

         if (matchesFingerprint(buffer, FASTTRACK_FINGERPRINT))
         {
            return readProjectFile(new FastTrackReader(), bis);
         }

         if (matchesFingerprint(buffer, PROJECTLIBRE_FINGERPRINT))
         {
            return readProjectFile(new ProjectLibreReader(), bis);
         }

         if (matchesFingerprint(buffer, GANTTPROJECT_FINGERPRINT))
         {
            return readProjectFile(new GanttProjectReader(), bis);
         }

         if (matchesFingerprint(buffer, TURBOPROJECT_FINGERPRINT))
         {
            return readProjectFile(new TurboProjectReader(), bis);
         }

         if (matchesFingerprint(buffer, DOS_EXE_FINGERPRINT))
         {
            return handleDosExeFile(bis);
         }

         if (matchesFingerprint(buffer, CONCEPT_DRAW_FINGERPRINT))
         {
            return readProjectFile(new ConceptDrawProjectReader(), bis);
         }

         if (matchesFingerprint(buffer, SYNCHRO_FINGERPRINT))
         {
            return readProjectFile(new SynchroReader(), bis);
         }

         if (matchesFingerprint(buffer, GANTT_DESIGNER_FINGERPRINT))
         {
            return readProjectFile(new GanttDesignerReader(), bis);
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
      return fingerprint.matcher(m_charset == null ? new String(buffer) : new String(buffer, m_charset)).matches();
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
    * We have an OLE compound document... but is it an MPP file?
    *
    * @param stream file input stream
    * @return ProjectFile instance
    */
   private ProjectFile handleOleCompoundDocument(InputStream stream) throws Exception
   {
      POIFSFileSystem fs = new POIFSFileSystem(POIFSFileSystem.createNonClosingInputStream(stream));
      String fileFormat = MPPReader.getFileFormat(fs);
      if (fileFormat != null && fileFormat.startsWith("MSProject"))
      {
         MPPReader reader = new MPPReader();
         addListeners(reader);
         return reader.read(fs);
      }
      return null;
   }

   /**
    * We have a binary property list.
    *
    * @param stream file input stream
    * @return ProjectFile instance
    */
   private ProjectFile handleBinaryPropertyList(InputStream stream) throws Exception
   {
      // This is an unusual case. I have seen an instance where an MSPDI file was downloaded
      // as a web archive, which is a binary property list containing the file data.
      // This confused the UniversalProjectReader as it found a valid MSPDI fingerprint
      // but the binary plist header caused the XML parser to fail.
      // I'm not inclined to add support for extracting files from binary plists at the moment,
      // so adding this fingerprint allows us to cleanly reject the file as unsupported
      // rather than getting a confusing error from one of the other file type readers.
      return null;
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
         FileHelper.deleteQuietly(file);
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

         if (tableNames.contains("ZSCHEDULEITEM"))
         {
            return readProjectFile(new MerlinReader(), file);
         }

         return null;
      }

      finally
      {
         FileHelper.deleteQuietly(file);
      }
   }

   /**
    * We have identified that we have a zip file. Extract the contents into
    * a temporary directory and process.
    *
    * @param stream schedule data
    * @return ProjectFile instance
    */
   private ProjectFile handleZipFile(InputStream stream) throws Exception
   {
      File dir = null;

      try
      {
         dir = InputStreamHelper.writeZipStreamToTempDir(stream);
         ProjectFile result = handleDirectory(dir);
         if (result != null)
         {
            return result;
         }
      }

      finally
      {
         FileHelper.deleteQuietly(dir);
      }

      return null;
   }

   /**
    * We have a directory. Determine if this contains a multi-file database we understand, if so
    * process it. If it does not contain a database, test each file within the directory
    * structure to determine if it contains a file whose format we understand.
    *
    * @param directory directory to process
    * @return ProjectFile instance if we can process anything, or null
    */
   private ProjectFile handleDirectory(File directory) throws Exception
   {
      ProjectFile result = handleDatabaseInDirectory(directory);
      if (result == null)
      {
         result = handleFileInDirectory(directory);
      }
      return result;
   }

   /**
    * Given a directory, determine if it contains a multi-file database whose format
    * we can process.
    *
    * @param directory directory to process
    * @return ProjectFile instance if we can process anything, or null
    */
   private ProjectFile handleDatabaseInDirectory(File directory) throws Exception
   {
      byte[] buffer = new byte[BUFFER_SIZE];
      File[] files = directory.listFiles();
      if (files != null)
      {
         for (File file : files)
         {
            if (file.isDirectory())
            {
               continue;
            }

            FileInputStream fis = new FileInputStream(file);
            int bytesRead = fis.read(buffer);
            fis.close();

            //
            // If the file is smaller than the buffer we are peeking into,
            // it's probably not a valid schedule file.
            //
            if (bytesRead != BUFFER_SIZE)
            {
               continue;
            }

            if (matchesFingerprint(buffer, BTRIEVE_FINGERPRINT))
            {
               return handleP3BtrieveDatabase(directory);
            }

            if (matchesFingerprint(buffer, STW_FINGERPRINT))
            {
               return handleSureTrakDatabase(directory);
            }
         }
      }
      return null;
   }

   /**
    * Given a directory, determine if it  (or any subdirectory) contains a file
    * whose format we understand.
    *
    * @param directory directory to process
    * @return ProjectFile instance if we can process anything, or null
    */
   private ProjectFile handleFileInDirectory(File directory) throws Exception
   {
      List<File> directories = new ArrayList<File>();
      File[] files = directory.listFiles();

      if (files != null)
      {
         // Try files first
         for (File file : files)
         {
            if (file.isDirectory())
            {
               directories.add(file);
            }
            else
            {
               UniversalProjectReader reader = new UniversalProjectReader();
               ProjectFile result = reader.read(file);
               if (result != null)
               {
                  return result;
               }
            }
         }

         // Haven't found a file we can read? Try the directories.
         for (File file : directories)
         {
            ProjectFile result = handleDirectory(file);
            if (result != null)
            {
               return result;
            }
         }
      }
      return null;
   }

   /**
    * Determine if we have a P3 Btrieve multi-file database.
    *
    * @param directory directory to process
    * @return ProjectFile instance if we can process anything, or null
    */
   private ProjectFile handleP3BtrieveDatabase(File directory) throws Exception
   {
      return P3DatabaseReader.setProjectNameAndRead(directory);
   }

   /**
    * Determine if we have a SureTrak multi-file database.
    *
    * @param directory directory to process
    * @return ProjectFile instance if we can process anything, or null
    */
   private ProjectFile handleSureTrakDatabase(File directory) throws Exception
   {
      return SureTrakDatabaseReader.setProjectNameAndRead(directory);
   }

   /**
    * The file we are working with has a byte order mark. Skip this and try again to read the file.
    *
    * @param stream schedule data
    * @param length length of the byte order mark
    * @param charset charset indicated by byte order mark
    * @return ProjectFile instance
    */
   private ProjectFile handleByteOrderMark(InputStream stream, int length, Charset charset) throws Exception
   {
      UniversalProjectReader reader = new UniversalProjectReader();
      reader.setSkipBytes(length);
      reader.setCharset(charset);
      return reader.read(stream);
   }

   /**
    * This could be a self-extracting archive. If we understand the format, expand
    * it and check the content for files we can read.
    *
    * @param stream schedule data
    * @return ProjectFile instance
    */
   private ProjectFile handleDosExeFile(InputStream stream) throws Exception
   {
      File file = InputStreamHelper.writeStreamToTempFile(stream, ".tmp");
      InputStream is = null;

      try
      {
         is = new FileInputStream(file);
         if (is.available() > 1350)
         {
            StreamHelper.skip(is, 1024);

            // Bytes at offset 1024
            byte[] data = new byte[2];
            is.read(data);

            if (matchesFingerprint(data, WINDOWS_NE_EXE_FINGERPRINT))
            {
               StreamHelper.skip(is, 286);

               // Bytes at offset 1312
               data = new byte[34];
               is.read(data);
               if (matchesFingerprint(data, PRX_FINGERPRINT))
               {
                  is.close();
                  is = null;
                  return readProjectFile(new P3PRXFileReader(), file);
               }
            }

            if (matchesFingerprint(data, STX_FINGERPRINT))
            {
               StreamHelper.skip(is, 31742);
               // Bytes at offset 32768
               data = new byte[4];
               is.read(data);
               if (matchesFingerprint(data, PRX3_FINGERPRINT))
               {
                  is.close();
                  is = null;
                  return readProjectFile(new SureTrakSTXFileReader(), file);
               }
            }
         }
         return null;
      }

      finally
      {
         StreamHelper.closeQuietly(is);
         FileHelper.deleteQuietly(file);
      }
   }

   /**
    * XER files can contain multiple projects when there are cross-project dependencies.
    * As the UniversalProjectReader is designed just to read a single project, we need
    * to select one project from those available in the XER file.
    * The original project selected for export by the user will have its "export flag"
    * set to true. We'll return the first project we find where the export flag is
    * set to true, otherwise we'll just return the first project we find in the file.
    *
    * @param stream schedule data
    * @return ProjectFile instance
    */
   private ProjectFile handleXerFile(InputStream stream) throws Exception
   {
      PrimaveraXERFileReader reader = new PrimaveraXERFileReader();
      reader.setCharset(m_charset);
      List<ProjectFile> projects = reader.readAll(stream);
      ProjectFile project = null;
      for (ProjectFile file : projects)
      {
         if (file.getProjectProperties().getExportFlag())
         {
            project = file;
            break;
         }
      }
      if (project == null && !projects.isEmpty())
      {
         project = projects.get(0);
      }
      return project;
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

   private int m_skipBytes;
   private Charset m_charset;
   private List<ProjectListener> m_projectListeners;

   private static final int BUFFER_SIZE = 512;

   private static final byte[] OLE_COMPOUND_DOC_FINGERPRINT =
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
      (byte) 'X'
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

   private static final byte[] PHOENIX_FINGERPRINT =
   {
      (byte) 'P',
      (byte) 'P',
      (byte) 'X',
      (byte) '!',
      (byte) '!',
      (byte) '!',
      (byte) '!'
   };

   private static final byte[] BINARY_PLIST =
   {
      (byte) 'b',
      (byte) 'p',
      (byte) 'l',
      (byte) 'i',
      (byte) 's',
      (byte) 't'
   };

   private static final byte[] FASTTRACK_FINGERPRINT =
   {
      (byte) 0x1C,
      (byte) 0x00,
      (byte) 0x00,
      (byte) 0x00,
      (byte) 0x8B,
      (byte) 0x00,
      (byte) 0x00,
      (byte) 0x00
   };

   private static final byte[] PROJECTLIBRE_FINGERPRINT =
   {
      (byte) 0xAC,
      (byte) 0xED,
      (byte) 0x00,
      (byte) 0x05
   };

   private static final byte[] BTRIEVE_FINGERPRINT =
   {
      (byte) 0x46,
      (byte) 0x43,
      (byte) 0x00,
      (byte) 0x00
   };

   private static final byte[] STW_FINGERPRINT =
   {
      (byte) 0x53,
      (byte) 0x54,
      (byte) 0x57
   };

   private static final byte[] DOS_EXE_FINGERPRINT =
   {
      (byte) 0x4D,
      (byte) 0x5A
   };

   private static final byte[] WINDOWS_NE_EXE_FINGERPRINT =
   {
      (byte) 0x4E,
      (byte) 0x45
   };

   private static final byte[] STX_FINGERPRINT =
   {
      (byte) 0x55,
      (byte) 0x8B
   };

   private static final byte[] SYNCHRO_FINGERPRINT =
   {
      (byte) 0xB6,
      (byte) 0x17
   };

   private static final byte[] UTF8_BOM_FINGERPRINT =
   {
      (byte) 0xEF,
      (byte) 0xBB,
      (byte) 0xBF
   };

   private static final byte[] UTF16_BOM_FINGERPRINT =
   {
      (byte) 0xFE,
      (byte) 0xFF
   };

   private static final byte[] UTF16LE_BOM_FINGERPRINT =
   {
      (byte) 0xFF,
      (byte) 0xFE
   };

   private static final Pattern PLANNER_FINGERPRINT = Pattern.compile(".*<project.*mrproject-version.*", Pattern.DOTALL);

   private static final Pattern PMXML_FINGERPRINT = Pattern.compile(".*(<BusinessObjects|APIBusinessObjects).*", Pattern.DOTALL);

   private static final Pattern MSPDI_FINGERPRINT_1 = Pattern.compile(".*xmlns=\"http://schemas\\.microsoft\\.com/project.*", Pattern.DOTALL);

   private static final Pattern MSPDI_FINGERPRINT_2 = Pattern.compile(".*<Project.*<SaveVersion>.*", Pattern.DOTALL);
   
   private static final Pattern PHOENIX_XML_FINGERPRINT = Pattern.compile(".*<project.*version=\"(\\d+|\\d+\\.\\d+)\".*update_mode=\"(true|false)\".*>.*", Pattern.DOTALL);

   private static final Pattern GANTTPROJECT_FINGERPRINT = Pattern.compile(".*<project.*webLink.*", Pattern.DOTALL);

   private static final Pattern TURBOPROJECT_FINGERPRINT = Pattern.compile(".*dWBSTAB.*", Pattern.DOTALL);

   private static final Pattern PRX_FINGERPRINT = Pattern.compile("!Self-Extracting Primavera Project", Pattern.DOTALL);

   private static final Pattern PRX3_FINGERPRINT = Pattern.compile("PRX3", Pattern.DOTALL);

   private static final Pattern CONCEPT_DRAW_FINGERPRINT = Pattern.compile(".*Application=\\\"CDProject\\\".*", Pattern.DOTALL);

   private static final Pattern GANTT_DESIGNER_FINGERPRINT = Pattern.compile(".*<Gantt Version=.*", Pattern.DOTALL);

}
