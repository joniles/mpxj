/*
 * file:       AbstractProjectReader.java
 * author:     Jon Iles
 * copyright:  (c) Packwood Software 2020
 * date:       04/09/2020
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

import java.util.ArrayList;
import java.util.List;

import org.mpxj.ProjectFile;
import org.mpxj.listener.ProjectListener;

/**
 * Abstract implementation of the ProjectReader interface
 * for readers which consume a file.
 */
public abstract class AbstractProjectReader implements ProjectReader
{
   @Override public void addProjectListener(ProjectListener listener)
   {
      if (m_projectListeners == null)
      {
         m_projectListeners = new ArrayList<>();
      }
      m_projectListeners.add(listener);
   }

   /**
    * Common method to add listeners to a project.
    *
    * @param project target project
    */
   protected void addListenersToProject(ProjectFile project)
   {
      project.getEventManager().addProjectListeners(m_projectListeners);
   }

   /**
    * Common method to add listeners to a reader.
    *
    * @param reader target reader
    */
   protected void addListenersToReader(ProjectReader reader)
   {
      if (m_projectListeners != null)
      {
         m_projectListeners.forEach(reader::addProjectListener);
      }
   }

   private List<ProjectListener> m_projectListeners;
}
