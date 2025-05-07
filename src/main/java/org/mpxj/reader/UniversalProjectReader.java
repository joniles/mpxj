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

package org.mpxj.reader;

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

import org.mpxj.HasCharset;
import org.mpxj.common.ConnectionHelper;
import org.mpxj.edrawproject.EdrawProjectReader;
import org.mpxj.openplan.OpenPlanReader;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;

import com.healthmarketscience.jackcess.Database;
import com.healthmarketscience.jackcess.DatabaseBuilder;

import org.mpxj.MPXJException;
import org.mpxj.ProjectFile;
import org.mpxj.asta.AstaMdbReader;
import org.mpxj.asta.AstaSqliteReader;
import org.mpxj.asta.AstaTextFileReader;
import org.mpxj.common.AutoCloseableHelper;
import org.mpxj.common.CharsetHelper;
import org.mpxj.common.FileHelper;
import org.mpxj.common.InputStreamHelper;
import org.mpxj.common.SQLite;
import org.mpxj.conceptdraw.ConceptDrawProjectReader;
import org.mpxj.fasttrack.FastTrackReader;
import org.mpxj.ganttdesigner.GanttDesignerReader;
import org.mpxj.ganttproject.GanttProjectReader;
import org.mpxj.merlin.MerlinReader;
import org.mpxj.mpd.MPDFileReader;
import org.mpxj.mpp.MPPReader;
import org.mpxj.mpx.MPXReader;
import org.mpxj.mspdi.MSPDIReader;
import org.mpxj.phoenix.PhoenixInputStream;
import org.mpxj.phoenix.PhoenixReader;
import org.mpxj.planner.PlannerReader;
import org.mpxj.primavera.PrimaveraDatabaseFileReader;
import org.mpxj.primavera.PrimaveraPMFileReader;
import org.mpxj.primavera.PrimaveraXERFileReader;
import org.mpxj.primavera.p3.P3DatabaseReader;
import org.mpxj.primavera.p3.P3PRXFileReader;
import org.mpxj.primavera.suretrak.SureTrakDatabaseReader;
import org.mpxj.primavera.suretrak.SureTrakSTXFileReader;
import org.mpxj.projectcommander.ProjectCommanderReader;
import org.mpxj.projectlibre.ProjectLibreReader;
import org.mpxj.sage.SageReader;
import org.mpxj.sdef.SDEFReader;
import org.mpxj.synchro.SynchroReader;
import org.mpxj.turboproject.TurboProjectReader;

/**
 * This class implements a universal project reader: given a file or a stream
 * this reader will sample the content and determine the type of file it has
 * been given. It will then instantiate the correct reader for that file type
 * and proceed to read the file.
 */
public final class UniversalProjectReader extends AbstractProjectReader
{
   /**
    * The classes implementing this interface provide access to an instance of
    * the {@code ProjectReader} class (via the {@code getProjectReader} method)
    * which is the class that {@code UniversalProjectReader} has determined
    * should be used to read the file or stream you have passed it. You can use
    * the {@code ProjectReader} instance to take decisions in your own code
    * based on the type of file represented by the reader, or you can customize
    * the behaviour of the reader by setting its properties before reading a
    * schedule.
    * <p>
    * Once you have obtained an instance of the {@code ProjectReaderProxy}
    * class, you can call the {@code read} or {@code readAll} methods to read
    * the schedule data. Note that you must use these methods to read the
    * schedule data from the supplied file or stream rather than calling the
    * read methods on the {@code ProjectReader} instance as the
    * {@code UniversalProjectReader} has pre-processed the data you
    * supplied to locate the schedule.
    * <p>
    * Note: you must release the resources held by instances of this class by
    * arranging to call the {@code close} method. To assist with this, this
    * interface extends {@code AutoCloseable}.
    */
   public interface ProjectReaderProxy extends AutoCloseable
   {
      /**
       * Retrieve the {@code ProjectReader} instance which will be used to read
       * the supplied file or stream.
       *
       * @return {@code ProjectReader} instance
       */
      ProjectReader getProjectReader();

      /**
       * Read a single {@code ProjectFile} instance from the supplied file or stream.
       *
       * @return {@code ProjectFile} instance or {@code null}
       */
      ProjectFile read() throws MPXJException;

      /**
       * Read a list of {@code ProjectFile} instances from the supplied file or stream.
       *
       * @return {@code ProjectFile} instance or an empty list
       */
      List<ProjectFile> readAll() throws MPXJException;
   }

   /**
    * Internal {@code ProjectReaderProxy} implementation for streams.
    */
   private class StreamReaderProxy implements ProjectReaderProxy
   {
      public StreamReaderProxy(ProjectReader reader, InputStream stream)
      {
         m_reader = reader;
         m_stream = stream;
      }

      @Override public ProjectReader getProjectReader()
      {
         return m_reader;
      }

      @Override public ProjectFile read() throws MPXJException
      {
         return m_reader.read(m_stream);
      }

      @Override public List<ProjectFile> readAll() throws MPXJException
      {
         return m_reader.readAll(m_stream);
      }

      @Override public void close()
      {
         cleanup();
      }

      private final ProjectReader m_reader;
      private final InputStream m_stream;
   }

   /**
    * Internal {@code ProjectReaderProxy} implementation for files.
    */
   private class FileReaderProxy implements ProjectReaderProxy
   {
      public FileReaderProxy(ProjectReader reader, File file)
      {
         m_reader = reader;
         m_file = file;
      }

      @Override public ProjectReader getProjectReader()
      {
         return m_reader;
      }

      @Override public ProjectFile read() throws MPXJException
      {
         return m_reader.read(m_file);
      }

      @Override public List<ProjectFile> readAll() throws MPXJException
      {
         return m_reader.readAll(m_file);
      }

      @Override public void close()
      {
         cleanup();
      }

      private final ProjectReader m_reader;
      private final File m_file;
   }

   /**
    * Internal extensible {@code ProjectReaderProxy} implementation.
    *
    * @param <R> reader class instance
    * @param <T> data source
    */
   private abstract class GenericReaderProxy<R extends ProjectReader, T> implements ProjectReaderProxy
   {
      public GenericReaderProxy(R reader, T source)
      {
         m_reader = reader;
         m_source = source;
      }

      @Override public R getProjectReader()
      {
         return m_reader;
      }

      @Override public void close()
      {
         cleanup();
      }

      protected final R m_reader;
      protected final T m_source;
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
      try (ProjectReaderProxy reader = getProjectReaderProxy(file))
      {
         return reader == null ? null : reader.read();
      }

      catch (MPXJException ex)
      {
         throw ex;
      }

      catch (Exception ex)
      {
         throw new RuntimeException(ex);
      }
   }

   @Override public List<ProjectFile> readAll(File file) throws MPXJException
   {
      try (ProjectReaderProxy reader = getProjectReaderProxy(file))
      {
         return reader == null ? Collections.emptyList() : reader.readAll();
      }

      catch (MPXJException ex)
      {
         throw ex;
      }

      catch (Exception ex)
      {
         throw new RuntimeException(ex);
      }
   }

   @Override public ProjectFile read(InputStream inputStream) throws MPXJException
   {
      try (ProjectReaderProxy reader = getProjectReaderProxy(inputStream))
      {
         return reader == null ? null : reader.read();
      }

      catch (MPXJException ex)
      {
         throw ex;
      }

      catch (Exception ex)
      {
         throw new RuntimeException(ex);
      }
   }

   @Override public List<ProjectFile> readAll(InputStream inputStream) throws MPXJException
   {
      try (ProjectReaderProxy reader = getProjectReaderProxy(inputStream))
      {
         return reader == null ? Collections.emptyList() : reader.readAll();
      }

      catch (MPXJException ex)
      {
         throw ex;
      }

      catch (Exception ex)
      {
         throw new RuntimeException(ex);
      }
   }

   /**
    * Retrieve a {@code ProjectReaderProxy} instance which provides access to
    * the {@code ProjectReader} required to read a schedule from the named file.
    *
    * @param fileName name of file containing schedule data
    * @return {@code ProjectReaderProxy} instance or null if no suitable reader can be found
    */
   public ProjectReaderProxy getProjectReaderProxy(String fileName) throws MPXJException
   {
      return getProjectReaderProxy(new File(fileName));
   }

   /**
    * Retrieve a {@code ProjectReaderProxy} instance which provides access to
    * the {@code ProjectReader} required to read a schedule from the supplied
    * {@code File instance}.
    *
    * @param file file containing schedule data
    * @return {@code ProjectReaderProxy} instance or null if no suitable reader can be found
    */
   public ProjectReaderProxy getProjectReaderProxy(File file) throws MPXJException
   {
      try
      {
         return file.isDirectory() ? handleDirectory(file) : handleFile(file);
      }

      catch (Exception ex)
      {
         throw new MPXJException(MPXJException.INVALID_FILE, ex);
      }
   }

   /**
    * Retrieve a {@code ProjectReaderProxy} instance which provides access to
    * the {@code ProjectReader} required to read a schedule from the supplied
    * {@code InputStream instance}.
    *
    * @param inputStream stream containing schedule data
    * @return {@code ProjectReaderProxy} instance or null if no suitable reader can be found
    */
   public ProjectReaderProxy getProjectReaderProxy(InputStream inputStream) throws MPXJException
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
            return new StreamReaderProxy(configure(new MSPDIReader()), bis);
         }

         if (matchesFingerprint(buffer, PP_FINGERPRINT))
         {
            return new StreamReaderProxy(configure(new AstaTextFileReader()), bis);
         }

         if (matchesFingerprint(buffer, MPX_FINGERPRINT))
         {
            return new StreamReaderProxy(configure(new MPXReader()), bis);
         }

         if (matchesFingerprint(buffer, XER_FINGERPRINT))
         {
            return new StreamReaderProxy(configure(new PrimaveraXERFileReader()), bis);
         }

         if (matchesFingerprint(buffer, PLANNER_FINGERPRINT))
         {
            return new StreamReaderProxy(configure(new PlannerReader()), bis);
         }

         if (matchesFingerprint(buffer, PMXML_FINGERPRINT))
         {
            return new StreamReaderProxy(configure(new PrimaveraPMFileReader()), bis);
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
            return new StreamReaderProxy(configure(new PhoenixReader()), new PhoenixInputStream(bis));
         }

         if (matchesFingerprint(buffer, PHOENIX_XML_FINGERPRINT1) || matchesFingerprint(buffer, PHOENIX_XML_FINGERPRINT2))
         {
            return new StreamReaderProxy(configure(new PhoenixReader()), bis);
         }

         if (matchesFingerprint(buffer, FASTTRACK_FINGERPRINT))
         {
            return new StreamReaderProxy(configure(new FastTrackReader()), bis);
         }

         if (matchesFingerprint(buffer, PROJECTLIBRE_FINGERPRINT))
         {
            return new StreamReaderProxy(configure(new ProjectLibreReader()), bis);
         }

         if (matchesFingerprint(buffer, GANTTPROJECT_FINGERPRINT))
         {
            return new StreamReaderProxy(configure(new GanttProjectReader()), bis);
         }

         if (matchesFingerprint(buffer, TURBOPROJECT_FINGERPRINT))
         {
            return new StreamReaderProxy(configure(new TurboProjectReader()), bis);
         }

         if (matchesFingerprint(buffer, DOS_EXE_FINGERPRINT))
         {
            return handleDosExeFile(bis);
         }

         if (matchesFingerprint(buffer, CONCEPT_DRAW_FINGERPRINT))
         {
            return new StreamReaderProxy(configure(new ConceptDrawProjectReader()), bis);
         }

         if (matchesFingerprint(buffer, SYNCHRO_FINGERPRINT))
         {
            return new StreamReaderProxy(configure(new SynchroReader()), bis);
         }

         if (matchesFingerprint(buffer, GANTT_DESIGNER_FINGERPRINT))
         {
            return new StreamReaderProxy(configure(new GanttDesignerReader()), bis);
         }

         if (matchesFingerprint(buffer, SDEF_FINGERPRINT))
         {
            return new StreamReaderProxy(configure(new SDEFReader()), bis);
         }

         if (matchesFingerprint(buffer, SCHEDULE_GRID_FINGERPRINT))
         {
            return new StreamReaderProxy(configure(new SageReader()), bis);
         }

         if (matchesFingerprint(buffer, PROJECT_COMMANDER_FINGERPRINT_1) || matchesFingerprint(buffer, PROJECT_COMMANDER_FINGERPRINT_2))
         {
            return new StreamReaderProxy(configure(new ProjectCommanderReader()), bis);
         }

         if (matchesFingerprint(buffer, EDRAW_PROJECT_FINGERPRINT))
         {
            return new StreamReaderProxy(configure(new EdrawProjectReader()), bis);
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
   private ProjectReaderProxy handleOleCompoundDocument(InputStream stream) throws Exception
   {
      POIFSFileSystem fs;
      File file;

      try
      {
         // Reading from a File instance is more memory efficient than using an InputStream.
         // This also avoids a hard limit imposed by POI when reading certain very large files.
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
         return new GenericReaderProxy<OpenPlanReader, POIFSFileSystem>(configure(new OpenPlanReader()), fs)
         {
            @Override public ProjectFile read() throws MPXJException
            {
               return m_reader.read(m_source);
            }

            @Override public List<ProjectFile> readAll() throws MPXJException
            {
               return Collections.singletonList(m_reader.read(m_source));
            }
         };
      }

      String fileFormat = MPPReader.getFileFormat(fs);
      if (fileFormat == null || !fileFormat.startsWith("MSProject"))
      {
         return null;
      }

      return new GenericReaderProxy<MPPReader, POIFSFileSystem>(configure(new MPPReader()), fs)
      {
         @Override public ProjectFile read() throws MPXJException
         {
            return m_reader.read(m_source);
         }

         @Override public List<ProjectFile> readAll() throws MPXJException
         {
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
   private ProjectReaderProxy handleBinaryPropertyList(InputStream stream)
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
   private ProjectReaderProxy handleMDBFile(InputStream stream) throws Exception
   {
      File file = InputStreamHelper.writeStreamToTempFile(stream, ".mdb");
      m_cleanup.push(() -> FileHelper.deleteQuietly(file));

      Set<String> tableNames = populateMdbTableNames(file);

      if (tableNames.contains("MSP_PROJECTS"))
      {
         return new FileReaderProxy(configure(new MPDFileReader()), file);
      }

      if (tableNames.contains("EXCEPTIONN"))
      {
         return new FileReaderProxy(configure(new AstaMdbReader()), file);
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
   private ProjectReaderProxy handleSQLiteFile(InputStream stream) throws Exception
   {
      File file = InputStreamHelper.writeStreamToTempFile(stream, ".sqlite");
      m_cleanup.push(() -> FileHelper.deleteQuietly(file));

      Set<String> tableNames = populateSqliteTableNames(file);

      if (tableNames.contains("EXCEPTIONN"))
      {
         return new FileReaderProxy(configure(new AstaSqliteReader()), file);
      }

      if (tableNames.contains("PROJWBS"))
      {
         return new FileReaderProxy(configure(new PrimaveraDatabaseFileReader()), file);
      }

      if (tableNames.contains("ZSCHEDULEITEM"))
      {
         return new FileReaderProxy(configure(new MerlinReader()), file);
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
   private ProjectReaderProxy handleZipFile(InputStream stream) throws Exception
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
   private ProjectReaderProxy handleFile(File file) throws Exception
   {
      FileInputStream fis = new FileInputStream(file);
      m_cleanup.push(() -> AutoCloseableHelper.closeQuietly(fis));
      return getProjectReaderProxy(fis);
   }

   /**
    * We have a directory. Determine if this contains a multi-file database we understand, if so
    * process it. If it does not contain a database, test each file within the directory
    * structure to determine if it contains a file whose format we understand.
    *
    * @param directory directory to process
    * @return ProjectFile instance if we can process anything, or null
    */
   private ProjectReaderProxy handleDirectory(File directory) throws Exception
   {
      ProjectReaderProxy result = handleDatabaseInDirectory(directory);
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
   private ProjectReaderProxy handleDatabaseInDirectory(File directory) throws Exception
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
   private ProjectReaderProxy handleFileInDirectory(File directory) throws Exception
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
               m_cleanup.push(reader::cleanup);
               reader.m_properties = m_properties;
               ProjectReaderProxy result = reader.getProjectReaderProxy(file);
               if (result != null)
               {
                  return result;
               }
            }
         }

         // Haven't found a file we can read? Try the directories.
         for (File file : directories)
         {
            ProjectReaderProxy result = handleDirectory(file);
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
   private ProjectReaderProxy handleP3BtrieveDatabase(File directory)
   {
      return new GenericReaderProxy<P3DatabaseReader, File>(configure(new P3DatabaseReader()), directory)
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
   private ProjectReaderProxy handleSureTrakDatabase(File directory)
   {
      return new GenericReaderProxy<SureTrakDatabaseReader, File>(configure(new SureTrakDatabaseReader()), directory)
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
   private ProjectReaderProxy handleByteOrderMark(InputStream stream, int length, Charset charset) throws Exception
   {
      UniversalProjectReader reader = new UniversalProjectReader();
      m_cleanup.push(reader::cleanup);
      reader.m_properties = m_properties;
      reader.m_skipBytes = length;
      reader.m_charset = charset;
      return reader.getProjectReaderProxy(stream);
   }

   /**
    * This could be a self-extracting archive. If we understand the format, expand
    * it and check the content for files we can read.
    *
    * @param stream schedule data
    * @return ProjectFile instance
    */
   private ProjectReaderProxy handleDosExeFile(InputStream stream) throws Exception
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
                  return new FileReaderProxy(configure(new P3PRXFileReader()), file);
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
                  return new FileReaderProxy(configure(new SureTrakSTXFileReader()), file);
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

   private <T extends ProjectReader> T configure(T reader)
   {
      if (reader instanceof HasCharset && m_charset != null)
      {
         ((HasCharset) reader).setCharset(m_charset);
      }
      addListenersToReader(reader);
      return reader;
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

   private static final Pattern EDRAW_PROJECT_FINGERPRINT = Pattern.compile(".*<Document.*DocGuid=.*", Pattern.DOTALL);
}
