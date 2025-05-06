/*
 * file:       MPDFileReader.java
 * author:     Jon Iles
 * copyright:  (c) Packwood Software 2022
 * date:       01/07/2022
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

package org.mpxj.mpd;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.mpxj.MPXJException;
import org.mpxj.ProjectFile;
import org.mpxj.reader.AbstractProjectFileReader;

/**
 * This class provides a generic front end to read project data from
 * a database.
 */
public final class MPDFileReader extends AbstractProjectFileReader
{
   /**
    * Populates a Map instance representing the IDs and names of
    * projects available in the current database.
    *
    * @param file project file
    * @return Map instance containing ID and name pairs
    */
   public Map<Integer, String> listProjects(File file) throws MPXJException
   {
      MPD9FileReader reader = new MPD9FileReader();
      reader.setDatabaseFile(file);
      return reader.listProjects();
   }

   /**
    * Set the ID of the project to be read.
    *
    * @param projectID project ID
    */
   public void setProjectID(int projectID)
   {
      m_projectID = Integer.valueOf(projectID);
   }

   @Override public ProjectFile read(File file) throws MPXJException
   {
      MPD9FileReader reader = new MPD9FileReader();
      reader.setDatabaseFile(file);
      reader.setProjectID(m_projectID == null ? Integer.valueOf(1) : m_projectID);
      return reader.read();
   }

   @Override public List<ProjectFile> readAll(File file) throws MPXJException
   {
      MPD9FileReader reader = new MPD9FileReader();
      reader.setDatabaseFile(file);
      List<ProjectFile> result = new ArrayList<>();
      Set<Integer> ids = reader.listProjects().keySet();
      for (Integer id : ids)
      {
         reader.setProjectID(id);
         result.add(reader.read());
      }
      return result;
   }

   private Integer m_projectID;
}