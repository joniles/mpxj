package net.sf.mpxj.openplan;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collections;
import java.util.List;

import net.sf.mpxj.MPXJException;
import net.sf.mpxj.ProjectConfig;
import net.sf.mpxj.ProjectFile;
import net.sf.mpxj.ProjectProperties;
import net.sf.mpxj.common.AutoCloseableHelper;
import net.sf.mpxj.reader.AbstractProjectStreamReader;
import org.apache.poi.poifs.filesystem.DirectoryEntry;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;

public final class OpenPlanReader extends AbstractProjectStreamReader
{
   @Override public ProjectFile read(InputStream is) throws MPXJException
   {
      try
      {
         return read(new POIFSFileSystem(is));
      }

      catch (IOException ex)
      {
         throw new MPXJException(MPXJException.READ_ERROR, ex);
      }
   }

   @Override public List<ProjectFile> readAll(InputStream inputStream) throws MPXJException
   {
      return Collections.singletonList(read(inputStream));
   }

   @Override public ProjectFile read(File file) throws MPXJException
   {
      POIFSFileSystem fs = null;

      try
      {
         // Note we provide this version of the read method rather than using
         // the AbstractProjectStreamReader version as we can work with the File
         // instance directly for reduced memory consumption and the ability
         // to open larger MPP files.
         fs = new POIFSFileSystem(file);
         return read(fs);
      }

      catch (IOException ex)
      {
         throw new MPXJException(MPXJException.READ_ERROR, ex);
      }

      finally
      {
         AutoCloseableHelper.closeQuietly(fs);
      }
   }


   /**
    * Alternative entry point allowing an MPP file to be read from
    * a user-supplied POI file stream.
    *
    * @param fs POI file stream
    * @return ProjectFile instance
    */
   public ProjectFile read(POIFSFileSystem fs) throws MPXJException
   {
      try
      {
         m_file = new ProjectFile();
         ProjectConfig config = m_file.getProjectConfig();

         config.setAutoWBS(false);

         addListenersToProject(m_file);

         //
         // Open the file system and retrieve the root directory
         //
         processProjects(fs.getRoot());



         //
         // Add some analytics
         //
         ProjectProperties projectProperties = m_file.getProjectProperties();
         projectProperties.setFileApplication("Deltek OpenPlan");
         projectProperties.setFileType("BK3");
         m_file.readComplete();

         return m_file;
      }

      catch (OpenPlanException ex)
      {
         throw new MPXJException(MPXJException.READ_ERROR, ex);
      }
   }

   private void processProjects(DirectoryEntry root)
   {
      root.getEntryNames().stream().filter(s -> s.toUpperCase().endsWith("_PRJ")).forEach(s -> processProject(root, s));
   }

   private void processProject(DirectoryEntry parent, String name)
   {
      DirectoryEntry dir = getDirectoryEntry(parent, name);
      List<Row> rows = new OpenPlanTable(dir, "PRJ").read();
      if (rows.size() != 1)
      {
         throw new OpenPlanException("Expecting 1 project row, found " + rows.size());
      }

      Row row = rows.get(0);
      System.out.println(row);
   }

   private DirectoryEntry getDirectoryEntry(DirectoryEntry root, String name)
   {
      try
      {
         return (DirectoryEntry) root.getEntry(name);
      }

      catch (FileNotFoundException e)
      {
         throw new OpenPlanException(e);
      }
   }

   private ProjectFile m_file;
}
