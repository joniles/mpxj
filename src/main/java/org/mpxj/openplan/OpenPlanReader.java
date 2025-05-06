/*
 * file:       OpenPlanReader.java
 * author:     Jon Iles
 * date:       2024-02-27
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

package org.mpxj.openplan;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.stream.Collectors;

import org.mpxj.MPXJException;
import org.mpxj.ProjectFile;
import org.mpxj.common.AutoCloseableHelper;
import org.mpxj.reader.AbstractProjectStreamReader;
import org.apache.poi.poifs.filesystem.DirectoryEntry;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;

/**
 * Reads schedule data from a Deltek Open Plan BK3 file.
 */
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

   @Override public ProjectFile read(File file) throws MPXJException
   {
      List<ProjectFile> projects = readAll(file);
      return projects.isEmpty() ? null : projects.get(0);
   }

   @Override public List<ProjectFile> readAll(File file) throws MPXJException
   {
      POIFSFileSystem fs = null;

      try
      {
         // Note we provide this version of the read method rather than using
         // the AbstractProjectStreamReader version as we can work with the File
         // instance directly for reduced memory consumption and the ability
         // to open larger MPP files.
         fs = new POIFSFileSystem(file);
         return readAll(fs);
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
    * Read a single project from the BK3 file represented by the POIFSFileSystem instance.
    *
    * @param fs POIFSFileSystem instance
    * @return ProjectFile instance or null
    */
   public ProjectFile read(POIFSFileSystem fs) throws MPXJException
   {
      List<ProjectFile> projects = readAll(fs);
      return projects.isEmpty() ? null : projects.get(0);
   }

   /**
    * Read all projects from the BK3 file represented by the POIFSFileSystem instance.
    *
    * @param fs POIFSFileSystem instance
    * @return list of ProjectFile instances
    */
   public List<ProjectFile> readAll(POIFSFileSystem fs) throws MPXJException
   {
      try
      {
         return processProjects(fs.getRoot());
      }

      catch (OpenPlanException ex)
      {
         throw new MPXJException(MPXJException.READ_ERROR, ex);
      }
   }

   /**
    * Read a schedules from each of the PRJ directories present in the file.
    *
    * @param root root directory
    * @return list of ProjectFile instances
    */
   private List<ProjectFile> processProjects(DirectoryEntry root)
   {
      return root.getEntryNames().stream().filter(s -> s.toUpperCase().endsWith("_PRJ")).map(s -> processProject(root, s)).collect(Collectors.toList());
   }

   /**
    * Read a single schedule from a PRJ directory.
    *
    * @param root root directory
    * @param name PRJ directory name
    * @return ProjectFile instance
    */
   private ProjectFile processProject(DirectoryEntry root, String name)
   {
      return new ProjectDirectoryReader(root).read(name);
   }
}
