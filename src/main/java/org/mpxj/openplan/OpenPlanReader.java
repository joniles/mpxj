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
import org.mpxj.mscfb.CompoundFile;
import org.mpxj.common.AutoCloseableHelper;
import org.mpxj.reader.AbstractProjectStreamReader;

/**
 * Reads schedule data from a Deltek Open Plan BK3 file.
 */
public final class OpenPlanReader extends AbstractProjectStreamReader
{
   @Override public ProjectFile read(InputStream is) throws MPXJException
   {
      try
      {
         return read(new CompoundFile(is));
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
      CompoundFile cfb = null;

      try
      {
         // Note we provide this version of the read method rather than using
         // the AbstractProjectStreamReader version as we can work with the File
         // instance directly for reduced memory consumption and the ability
         // to open larger files.
         cfb = new CompoundFile(file);
         return readAll(cfb);
      }

      catch (IOException ex)
      {
         throw new MPXJException(MPXJException.READ_ERROR, ex);
      }

      finally
      {
         AutoCloseableHelper.closeQuietly(cfb);
      }
   }

   /**
    * Read a single project from the BK3 file represented by the POIFSFileSystem instance.
    *
    * @param file parent file
    * @return ProjectFile instance or null
    */
   public ProjectFile read(CompoundFile file) throws MPXJException
   {
      List<ProjectFile> projects = readAll(file);
      return projects.isEmpty() ? null : projects.get(0);
   }

   /**
    * Read all projects from the BK3 file represented by the POIFSFileSystem instance.
    *
    * @param file parent file
    * @return list of ProjectFile instances
    */
   public List<ProjectFile> readAll(CompoundFile file) throws MPXJException
   {
      try
      {
         return processProjects(file);
      }

      catch (IOException | OpenPlanException ex)
      {
         throw new MPXJException(MPXJException.READ_ERROR, ex);
      }
   }

   /**
    * Read a schedules from each of the PRJ directories present in the file.
    *
    * @param file parent file
    * @return list of ProjectFile instances
    */
   private List<ProjectFile> processProjects(CompoundFile file) throws IOException
   {
      return file.listFiles("/").stream().filter(s -> s.toUpperCase().endsWith("_PRJ")).map(s -> processProject(file, s)).collect(Collectors.toList());
   }

   /**
    * Read a single schedule from a PRJ directory.
    *
    * @param file parent file
    * @param path project path
    * @return ProjectFile instance
    */
   private ProjectFile processProject(CompoundFile file, String path)
   {
      return new ProjectDirectoryReader(file, path).read();
   }
}
