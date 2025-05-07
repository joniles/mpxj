/*
 * file:       ExternalProjectContainer.java
 * author:     Jon Iles
 * copyright:  (c) Packwood Software
 * date:       2023-05-06
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

package org.mpxj;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import org.mpxj.reader.UniversalProjectReader;

/**
 * Package private class used to manage locating and reading external project files.
 * This will cache the results so multiple attempts to read the same file
 * will be served from the cache.
 */
class ExternalProjectContainer
{
   /**
    * Constructor.
    *
    * @param file parent project
    */
   public ExternalProjectContainer(ProjectFile file)
   {
      m_file = file;
   }

   /**
    * Add an existing ProjectFile instance.
    *
    * @param fileName original path to the project
    * @param projectFile ProjectFile instance
    */
   public void add(String fileName, ProjectFile projectFile)
   {
      File file = new File(fileName);
      m_fileMap.put(fileName, file);
      m_projectMap.put(file, projectFile);
   }

   /**
    * Read the file.
    *
    * @param fileName path to the file
    * @return ProjectFile instance or null
    */
   public ProjectFile read(String fileName)
   {
      if (fileName == null || fileName.isEmpty())
      {
         return null;
      }

      File file;
      if (m_fileMap.containsKey(fileName))
      {
         file = m_fileMap.get(fileName);
      }
      else
      {
         file = findFile(fileName);
         m_fileMap.put(fileName, file);
      }

      if (file == null)
      {
         return null;
      }

      if (m_projectMap.containsKey(file))
      {
         return m_projectMap.get(file);
      }

      ProjectFile project = readFile(file);
      m_projectMap.put(file, project);
      return project;
   }

   /**
    * Find the named file, either at the specified path, or  in the current
    * directory for the process. If the Subproject Working Directory has
    * been configured in ProjectConfig, use this instead.
    *
    * @param fileName file to locate
    * @return File instance or null
    */
   private File findFile(String fileName)
   {
      // Try to find the file using the full path
      File file = new File(fileName);
      if (!file.exists())
      {
         // No luck, so we split the path - we'll always have a path with Windows separators
         int index = fileName.lastIndexOf("\\");
         if (index == -1)
         {
            return null;
         }

         // try the process working directory, or a caller supplied search directory
         String name = fileName.substring(index + 1);
         File subprojectWorkingDirectory = m_file.getProjectConfig().getSubprojectWorkingDirectory();
         file = subprojectWorkingDirectory == null ? new File(name) : new File(subprojectWorkingDirectory, name);
         if (!file.exists())
         {
            return null;
         }
      }

      return file;
   }

   /**
    * Read a project file.
    *
    * @param file file to read
    * @return ProjectFile instance or null
    */
   private ProjectFile readFile(File file)
   {
      ProjectFile project;

      try
      {
         project = new UniversalProjectReader().read(file);
         if (project != null)
         {
            // subproject inherits the search directory
            project.getProjectConfig().setSubprojectWorkingDirectory(m_file.getProjectConfig().getSubprojectWorkingDirectory());
         }
      }

      catch (MPXJException ex)
      {
         project = null;
      }

      return project;
   }

   private final ProjectFile m_file;
   private final Map<String, File> m_fileMap = new HashMap<>();
   private final Map<File, ProjectFile> m_projectMap = new HashMap<>();
}
