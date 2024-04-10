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
import java.nio.file.Files;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Properties;
import java.util.Set;
import java.util.Stack;
import java.util.regex.Pattern;

import net.sf.mpxj.common.ConnectionHelper;
import net.sf.mpxj.openplan.OpenPlanReader;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;

import com.healthmarketscience.jackcess.Database;
import com.healthmarketscience.jackcess.DatabaseBuilder;

import net.sf.mpxj.MPXJException;
import net.sf.mpxj.ProjectFile;
import net.sf.mpxj.asta.AstaMdbReader;
import net.sf.mpxj.asta.AstaSqliteReader;
import net.sf.mpxj.asta.AstaTextFileReader;
import net.sf.mpxj.common.AutoCloseableHelper;
import net.sf.mpxj.common.CharsetHelper;
import net.sf.mpxj.common.FileHelper;
import net.sf.mpxj.common.InputStreamHelper;
import net.sf.mpxj.common.SQLite;
import net.sf.mpxj.conceptdraw.ConceptDrawProjectReader;
import net.sf.mpxj.fasttrack.FastTrackReader;
import net.sf.mpxj.ganttdesigner.GanttDesignerReader;
import net.sf.mpxj.ganttproject.GanttProjectReader;
import net.sf.mpxj.merlin.MerlinReader;
import net.sf.mpxj.mpd.MPDFileReader;
import net.sf.mpxj.mpp.MPPReader;
import net.sf.mpxj.mpx.MPXReader;
import net.sf.mpxj.mspdi.MSPDIReader;
import net.sf.mpxj.phoenix.PhoenixInputStream;
import net.sf.mpxj.phoenix.PhoenixReader;
import net.sf.mpxj.planner.PlannerReader;
import net.sf.mpxj.primavera.PrimaveraDatabaseFileReader;
import net.sf.mpxj.primavera.PrimaveraPMFileReader;
import net.sf.mpxj.primavera.PrimaveraXERFileReader;
import net.sf.mpxj.primavera.p3.P3DatabaseReader;
import net.sf.mpxj.primavera.p3.P3PRXFileReader;
import net.sf.mpxj.primavera.suretrak.SureTrakDatabaseReader;
import net.sf.mpxj.primavera.suretrak.SureTrakSTXFileReader;
import net.sf.mpxj.projectcommander.ProjectCommanderReader;
import net.sf.mpxj.projectlibre.ProjectLibreReader;
import net.sf.mpxj.sage.SageReader;
import net.sf.mpxj.sdef.SDEFReader;
import net.sf.mpxj.synchro.SynchroReader;
import net.sf.mpxj.turboproject.TurboProjectReader;

/**
 * This class implements a universal project reader: given a file or a stream this reader
 * will sample the content and determine the type of file it has been given. It will then
 * instantiate the correct reader for that file type and proceed to read the file.
 */
public final class UniversalProjectReader extends AbstractProjectReader
{
   public interface ProxyReader
   {
      public ProjectFile read()  throws MPXJException;

      public List<ProjectFile> readAll()  throws MPXJException;
   }

   private class StreamReader implements ProxyReader
   {
      public StreamReader(ProjectReader reader, InputStream stream)
      {
         m_reader = reader;
         m_stream = stream;
      }

      @Override public ProjectFile read() throws MPXJException
      {
         addListenersToReader(m_reader);
         m_reader.setCharset(m_charset);
         m_reader.setProperties(m_properties);
         return m_reader.read(m_stream);
      }

      @Override public List<ProjectFile> readAll() throws MPXJException
      {
         addListenersToReader(m_reader);
         m_reader.setCharset(m_charset);
         m_reader.setProperties(m_properties);
         return m_reader.readAll(m_stream);
      }

      private final ProjectReader m_reader;
      private final InputStream m_stream;
   }

   private class FileReader implements ProxyReader
   {
      public FileReader(ProjectReader reader, File file)
      {
         m_reader = reader;
         m_file = file;
      }

      @Override public ProjectFile read() throws MPXJException
      {
         addListenersToReader(m_reader);
         m_reader.setCharset(m_charset);
         m_reader.setProperties(m_properties);
         return m_reader.read(m_file);
      }

      @Override public List<ProjectFile> readAll() throws MPXJException
      {
         addListenersToReader(m_reader);
         m_reader.setCharset(m_charset);
         m_reader.setProperties(m_properties);
         return m_reader.readAll(m_file);
      }

      private final ProjectReader m_reader;
      private final File m_file;
   }

   private abstract class GenericReader<R extends ProjectReader, T> implements ProxyReader
   {
      public GenericReader(R reader, T source)
      {
         m_reader = reader;
         m_source = source;
      }

      protected final R m_reader;
      protected final T m_source;
   }

   /**
    * Pass a set of Properties to configure the behavior of the reader class selected by
    * {@code UniversalProjectReader} to read a schedule. Users of {@code UniversalProjectReader} are
    * not expected to know what type of schedule they are working with ahead of time, so
    * {@code UniversalProjectReader} will select the correct reader for the supplied file type
    * use it to read the file.
    * <p>
    * Users of {@code UniversalProjectReader} may still want to configure the behavior of the individual reader
    * classes, but as {@code UniversalProjectReader} hides their use from callers, an alternative
    * mechanism is required to allow configuration information to be passed. In this case a {@code Properties}
    * instance can be passed containing properties in this form:
    * {@code <class name>.<property name>=<property value>}.
    * <p>
    * Here's an example of a single property value:
    * <pre>
    * net.sf.mpxj.phoenix.PhoenixReader.UseActivityCodesForTaskHierarchy=true
    * </pre>
    * This approach allows properties for multiple different reader classes to be specified,
    * in one {@code Properties} instance, with only the relevant properties being applied to the reader
    * class actually used by {@code UniversalProjectReader} to read the supplied schedule.
    *
    * @param props properties to set
    * @return current UniversalProjectReader instance to allow method chaining
    */
   @Override public ProjectReader setProperties(Properties props)
   {
      m_properties = props;
      return this;
   }

   @Override public ProjectFile read(String fileName) throws MPXJException
   {
      return read(new File(fileName));
   }

   @Override public List<ProjectFile> readAll(String fileName) throws MPXJException
   {
      return readAll(new File(fileName));
   }

   @Override public ProjectFile read(File file) throws MPXJException
   {
      try
      {
         return readInternal(file).read();
      }

      finally
      {
         cleanup();
      }
   }

   @Override public List<ProjectFile> readAll(File file) throws MPXJException
   {
      try
      {
         return readInternal(file).readAll();
      }

      finally
      {
         cleanup();
      }
   }

   @Override public ProjectFile read(InputStream inputStream) throws MPXJException
   {
      try
      {
         return readInternal(inputStream).read();
      }

      finally
      {
         cleanup();
      }
   }

   @Override public List<ProjectFile> readAll(InputStream inputStream) throws MPXJException
   {
      try
      {
         return readInternal(inputStream).readAll();
      }

      finally
      {
         cleanup();
      }
   }

   private ProxyReader readInternal(File file) throws MPXJException
   {
      try
      {
         return file.isDirectory() ? handleDirectory(file) :  handleFile(file);
      }

      catch (Exception ex)
      {
         throw new MPXJException(MPXJException.INVALID_FILE, ex);
      }
   }

   /**
    * Internal implementation of the read method. This primarily works with the
    * supplied InputStream instance however if the stream is from a file,
    * we'll pass a File instance too. This allows us to read MPP files
    * using a more memory-efficient approach based on the File than would
    * otherwise be possible using an input stream. This also avoids a hard
    * limit imposed by POI when reading certain very large files.
    *
    * @param inputStream input stream to read from
    * @return list of schedules
    */
   private ProxyReader readInternal(InputStream inputStream) throws MPXJException
   {
      try
      {
         BufferedInputStream bis = new BufferedInputStream(inputStream);
         InputStreamHelper.skip(bis, m_skipBytes);
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

         if (matchesFingerprint(buffer, MSPDI_FINGERPRINT_1) || matchesFingerprint(buffer, MSPDI_FINGERPRINT_2) || matchesFingerprint(buffer, MSPDI_FINGERPRINT_3))
         {
            return new StreamReader(new MSPDIReader(), bis);
         }

         if (matchesFingerprint(buffer, PP_FINGERPRINT))
         {
            return new StreamReader(new AstaTextFileReader(), bis);
         }

         if (matchesFingerprint(buffer, MPX_FINGERPRINT))
         {
            return new StreamReader(new MPXReader(), bis);
         }

         if (matchesFingerprint(buffer, XER_FINGERPRINT))
         {
            return new StreamReader(new PrimaveraXERFileReader(), bis);
         }

         if (matchesFingerprint(buffer, PLANNER_FINGERPRINT))
         {
            return new StreamReader(new PlannerReader(), bis);
         }

         if (matchesFingerprint(buffer, PMXML_FINGERPRINT))
         {
            return new StreamReader(new PrimaveraPMFileReader(), bis);
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
            return new StreamReader(new PhoenixReader(), new PhoenixInputStream(bis));
         }

         if (matchesFingerprint(buffer, PHOENIX_XML_FINGERPRINT1) || matchesFingerprint(buffer, PHOENIX_XML_FINGERPRINT2))
         {
            return new StreamReader(new PhoenixReader(), bis);
         }

         if (matchesFingerprint(buffer, FASTTRACK_FINGERPRINT))
         {
            return new StreamReader(new FastTrackReader(), bis);
         }

         if (matchesFingerprint(buffer, PROJECTLIBRE_FINGERPRINT))
         {
            return new StreamReader(new ProjectLibreReader(), bis);
         }

         if (matchesFingerprint(buffer, GANTTPROJECT_FINGERPRINT))
         {
            return new StreamReader(new GanttProjectReader(), bis);
         }

         if (matchesFingerprint(buffer, TURBOPROJECT_FINGERPRINT))
         {
            return new StreamReader(new TurboProjectReader(), bis);
         }

         if (matchesFingerprint(buffer, DOS_EXE_FINGERPRINT))
         {
            return handleDosExeFile(bis);
         }

         if (matchesFingerprint(buffer, CONCEPT_DRAW_FINGERPRINT))
         {
            return new StreamReader(new ConceptDrawProjectReader(), bis);
         }

         if (matchesFingerprint(buffer, SYNCHRO_FINGERPRINT))
         {
            return new StreamReader(new SynchroReader(), bis);
         }

         if (matchesFingerprint(buffer, GANTT_DESIGNER_FINGERPRINT))
         {
            return new StreamReader(new GanttDesignerReader(), bis);
         }

         if (matchesFingerprint(buffer, SDEF_FINGERPRINT))
         {
            return new StreamReader(new SDEFReader(), bis);
         }

         if (matchesFingerprint(buffer, SCHEDULE_GRID_FINGERPRINT))
         {
            return new StreamReader(new SageReader(), bis);
         }

         if (matchesFingerprint(buffer, PROJECT_COMMANDER_FINGERPRINT_1) || matchesFingerprint(buffer, PROJECT_COMMANDER_FINGERPRINT_2))
         {
            return new StreamReader(new ProjectCommanderReader(), bis);
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
    * We have an OLE compound document... but is it an MPP file?
    *
    * @param stream file input stream
    * @return ProjectFile instance
    */
   private ProxyReader handleOleCompoundDocument(InputStream stream) throws Exception
   {
      POIFSFileSystem fs;
      File file;

      try
      {
         file = InputStreamHelper.writeStreamToTempFile(stream, ".dat");
         m_cleanup.push(() -> FileHelper.deleteQuietly(file));

         fs = new POIFSFileSystem(file);
         m_cleanup.push(() -> AutoCloseableHelper.closeQuietly(fs));
      }

      catch (Exception ex)
      {
         return null;
      }

      if (fs.getRoot().getEntryNames().contains("SourceInfo"))
      {
         return new GenericReader<OpenPlanReader, POIFSFileSystem>(new OpenPlanReader(), fs)
         {
            @Override public ProjectFile read() throws MPXJException
            {
               addListenersToReader(m_reader);
               return m_reader.read(m_source);
            }

            @Override public List<ProjectFile> readAll() throws MPXJException
            {
               addListenersToReader(m_reader);
               return Collections.singletonList(m_reader.read(m_source));
            }
         };
      }

      String fileFormat = MPPReader.getFileFormat(fs);
      if (fileFormat == null || !fileFormat.startsWith("MSProject"))
      {
         return null;
      }

      return new GenericReader<MPPReader, POIFSFileSystem>(new MPPReader(), fs)
      {
         @Override public ProjectFile read() throws MPXJException
         {
            addListenersToReader(m_reader);
            m_reader.setProperties(m_properties);
            return m_reader.read(m_source);
         }
         @Override public List<ProjectFile> readAll() throws MPXJException
         {
            addListenersToReader(m_reader);
            m_reader.setProperties(m_properties);
            return Collections.singletonList(m_reader.read(m_source));
         }
      };
   }

   /**
    * We have a binary property list.
    *
    * @param stream file input stream
    * @return ProjectFile instance
    */
   private ProxyReader handleBinaryPropertyList(InputStream stream)
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
   private ProxyReader handleMDBFile(InputStream stream) throws Exception
   {
      File file = InputStreamHelper.writeStreamToTempFile(stream, ".mdb");
      m_cleanup.push(() -> FileHelper.deleteQuietly(file));

      Set<String> tableNames = populateMdbTableNames(file);

      if (tableNames.contains("MSP_PROJECTS"))
      {
         return new FileReader(new MPDFileReader(), file);
      }

      if (tableNames.contains("EXCEPTIONN"))
      {
         return new FileReader(new AstaMdbReader(), file);
      }

      return null;
   }

   /**
    * We have identified that we have a SQLite file. This could be a Primavera Project database
    * or an Asta database. Open the database and use the table names present to determine
    * which type this is.
    *
    * @param stream schedule data
    * @return ProjectFile instance
    */
   private ProxyReader handleSQLiteFile(InputStream stream) throws Exception
   {
      File file = InputStreamHelper.writeStreamToTempFile(stream, ".sqlite");
      m_cleanup.push(() -> FileHelper.deleteQuietly(file));

      Set<String> tableNames = populateSqliteTableNames(file);

      if (tableNames.contains("EXCEPTIONN"))
      {
         return new FileReader(new AstaSqliteReader(), file);
      }

      if (tableNames.contains("PROJWBS"))
      {
         return new FileReader(new PrimaveraDatabaseFileReader(), file);
      }

      if (tableNames.contains("ZSCHEDULEITEM"))
      {
         return new FileReader(new MerlinReader(), file);
      }

      return null;
   }

   /**
    * We have identified that we have a zip file. Extract the contents into
    * a temporary directory and process.
    *
    * @param stream schedule data
    * @return ProjectFile instance
    */
   private ProxyReader handleZipFile(InputStream stream) throws Exception
   {
      File dir = InputStreamHelper.writeZipStreamToTempDir(stream);
      m_cleanup.push(() -> FileHelper.deleteQuietly(dir));
      return handleDirectory(dir);
   }

   /**
    * Open and read a file.
    *
    * @param file File instance
    * @return ProjectFile instance
    */
   private ProxyReader handleFile(File file) throws Exception
   {
      FileInputStream fis = new FileInputStream(file);
      m_cleanup.push(() -> AutoCloseableHelper.closeQuietly(fis));
      return readInternal(fis);
   }

   /**
    * We have a directory. Determine if this contains a multi-file database we understand, if so
    * process it. If it does not contain a database, test each file within the directory
    * structure to determine if it contains a file whose format we understand.
    *
    * @param directory directory to process
    * @return ProjectFile instance if we can process anything, or null
    */
   private ProxyReader handleDirectory(File directory) throws Exception
   {
      ProxyReader result = handleDatabaseInDirectory(directory);
      if (result != null)
      {
         return result;
      }
      return handleFileInDirectory(directory);
   }

   /**
    * Given a directory, determine if it contains a multi-file database whose format
    * we can process.
    *
    * @param directory directory to process
    * @return ProjectFile instance if we can process anything, or null
    */
   private ProxyReader handleDatabaseInDirectory(File directory) throws Exception
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
   private ProxyReader handleFileInDirectory(File directory) throws Exception
   {
      List<File> directories = new ArrayList<>();
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
               m_cleanup.push(() -> reader.cleanup());
               reader.m_properties = m_properties;
               ProxyReader result = reader.readInternal(file);
               if (result != null)
               {
                  return result;
               }
            }
         }

         // Haven't found a file we can read? Try the directories.
         for (File file : directories)
         {
            ProxyReader result = handleDirectory(file);
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
   private ProxyReader handleP3BtrieveDatabase(File directory) throws Exception
   {
      return new GenericReader<P3DatabaseReader, File>(new P3DatabaseReader(), directory)
      {
         @Override public ProjectFile read() throws MPXJException
         {
            List<String> projects = P3DatabaseReader.listProjectNames(directory);
            if (projects.isEmpty())
            {
               return null;
            }

            m_reader.setProjectName(projects.get(0));
            return m_reader.read(m_source);
         }

         @Override public List<ProjectFile> readAll() throws MPXJException
         {
            addListenersToReader(m_reader);
            m_reader.setProperties(m_properties);
            return m_reader.readAll(m_source);
         }
      };
   }

   /**
    * Determine if we have a SureTrak multi-file database.
    *
    * @param directory directory to process
    * @return ProjectFile instance if we can process anything, or null
    */
   private ProxyReader handleSureTrakDatabase(File directory) throws Exception
   {
      return new GenericReader<SureTrakDatabaseReader, File>(new SureTrakDatabaseReader(), directory)
      {
         @Override public ProjectFile read() throws MPXJException
         {
            List<String> projects = SureTrakDatabaseReader.listProjectNames(directory);
            if (projects.isEmpty())
            {
               return null;
            }

            m_reader.setProjectName(projects.get(0));
            return m_reader.read(m_source);
         }

         @Override public List<ProjectFile> readAll() throws MPXJException
         {
            addListenersToReader(m_reader);
            m_reader.setProperties(m_properties);
            return m_reader.readAll(m_source);
         }
      };
   }

   /**
    * The file we are working with has a byte order mark. Skip this and try again to read the file.
    *
    * @param stream schedule data
    * @param length length of the byte order mark
    * @param charset charset indicated by byte order mark
    * @return ProjectFile instance
    */
   private ProxyReader handleByteOrderMark(InputStream stream, int length, Charset charset) throws Exception
   {
      UniversalProjectReader reader = new UniversalProjectReader();
      m_cleanup.push(() -> reader.cleanup());
      reader.m_properties = m_properties;
      reader.m_skipBytes = length;
      reader.m_charset = charset;
      return reader.readInternal(stream);
   }

   /**
    * This could be a self-extracting archive. If we understand the format, expand
    * it and check the content for files we can read.
    *
    * @param stream schedule data
    * @return ProjectFile instance
    */
   private ProxyReader handleDosExeFile(InputStream stream) throws Exception
   {
      File file = InputStreamHelper.writeStreamToTempFile(stream, ".tmp");
      m_cleanup.push(() -> FileHelper.deleteQuietly(file));

      InputStream is = null;
      try
      {
         is = Files.newInputStream(file.toPath());
         if (is.available() > 1350)
         {
            InputStreamHelper.skip(is, 1024);

            // Bytes at offset 1024
            byte[] data = InputStreamHelper.read(is, 2);
            if (matchesFingerprint(data, WINDOWS_NE_EXE_FINGERPRINT))
            {
               InputStreamHelper.skip(is, 286);

               // Bytes at offset 1312
               data = InputStreamHelper.read(is, 34);
               if (matchesFingerprint(data, PRX_FINGERPRINT))
               {
                  is.close();
                  is = null;
                  return new FileReader(new P3PRXFileReader(), file);
               }
            }

            if (matchesFingerprint(data, STX_FINGERPRINT))
            {
               InputStreamHelper.skip(is, 31742);
               // Bytes at offset 32768
               data = InputStreamHelper.read(is, 4);
               if (matchesFingerprint(data, PRX3_FINGERPRINT))
               {
                  is.close();
                  is = null;
                  return new FileReader(new SureTrakSTXFileReader(), file);
               }
            }
         }
         return null;
      }

      finally
      {
         AutoCloseableHelper.closeQuietly(is);
      }
   }

   private Set<String> populateMdbTableNames(File file) throws Exception
   {
      try (Database database = DatabaseBuilder.open(file))
      {
         return database.getTableNames();
      }
   }

   private Set<String> populateSqliteTableNames(File file) throws Exception
   {
      try (Connection connection = SQLite.createConnection(file))
      {
         return ConnectionHelper.getTableNames(connection);
      }
   }

   private void cleanup()
   {
      while (!m_cleanup.isEmpty())
      {
         m_cleanup.pop().run();
      }
   }

   private Properties m_properties;
   private int m_skipBytes;
   private Charset m_charset;
   private final Stack<Runnable> m_cleanup = new Stack<>();

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

   private static final byte[] SDEF_FINGERPRINT =
   {
      (byte) 'V',
      (byte) 'O',
      (byte) 'L',
      (byte) 'M'
   };

   private static final byte[] SCHEDULE_GRID_FINGERPRINT =
   {
      (byte) '*',
      (byte) '*',
      (byte) '*',
      (byte) '*',
      (byte) ' ',
      (byte) 'S',
      (byte) 'c',
      (byte) 'h',
      (byte) 'e',
      (byte) 'd',
      (byte) 'u',
      (byte) 'l',
      (byte) 'e',
      (byte) ' ',
      (byte) 'G',
      (byte) 'r',
      (byte) 'i',
      (byte) 'd'
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

   private static final byte[] PROJECT_COMMANDER_FINGERPRINT_1 =
   {
      (byte) 0x00,
      (byte) 0x80,
      (byte) 0x01,
      (byte) 0x00
   };

   private static final byte[] PROJECT_COMMANDER_FINGERPRINT_2 =
   {
      (byte) 0x02,
      (byte) 0x80,
      (byte) 0x01,
      (byte) 0x00
   };

   private static final Pattern XER_FINGERPRINT = Pattern.compile("ERMHDR.*", Pattern.DOTALL);

   private static final Pattern PLANNER_FINGERPRINT = Pattern.compile(".*<project.*mrproject-version.*", Pattern.DOTALL);

   private static final Pattern PMXML_FINGERPRINT = Pattern.compile(".*(<BusinessObjects|APIBusinessObjects).*", Pattern.DOTALL);

   private static final Pattern MSPDI_FINGERPRINT_1 = Pattern.compile(".*xmlns=\"http://schemas\\.microsoft\\.com/project.*", Pattern.DOTALL);

   private static final Pattern MSPDI_FINGERPRINT_2 = Pattern.compile(".*<Project.*<SaveVersion>.*", Pattern.DOTALL);

   private static final Pattern MSPDI_FINGERPRINT_3 = Pattern.compile(".*<Project.*<Title>.*", Pattern.DOTALL);

   private static final Pattern PHOENIX_XML_FINGERPRINT1 = Pattern.compile(".*<project.*version=\"(\\d+|\\d+\\.\\d+)\".*update_mode=\"(true|false)\".*>.*", Pattern.DOTALL);

   private static final Pattern PHOENIX_XML_FINGERPRINT2 = Pattern.compile(".*<project.*version=\"(\\d+|\\d+\\.\\d+)\".*application=\"Phoenix.*", Pattern.DOTALL);

   private static final Pattern GANTTPROJECT_FINGERPRINT = Pattern.compile(".*<project.*webLink.*", Pattern.DOTALL);

   private static final Pattern TURBOPROJECT_FINGERPRINT = Pattern.compile(".*dWBSTAB.*", Pattern.DOTALL);

   private static final Pattern PRX_FINGERPRINT = Pattern.compile("!Self-Extracting Primavera Project", Pattern.DOTALL);

   private static final Pattern PRX3_FINGERPRINT = Pattern.compile("PRX3", Pattern.DOTALL);

   private static final Pattern CONCEPT_DRAW_FINGERPRINT = Pattern.compile(".*Application=\"CDProject\".*", Pattern.DOTALL);

   private static final Pattern GANTT_DESIGNER_FINGERPRINT = Pattern.compile(".*<Gantt Version=.*", Pattern.DOTALL);
}
