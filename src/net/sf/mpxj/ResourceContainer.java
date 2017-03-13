/*
 * file:       ResourceContainer.java
 * author:     Jon Iles
 * copyright:  (c) Packwood Software 2002-2015
 * date:       20/04/2015
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

package net.sf.mpxj;

import java.util.Iterator;

import net.sf.mpxj.common.NumberHelper;

/**
 * Manages the collection of resources belonging to a project.
 */
public class ResourceContainer extends ProjectEntityWithIDContainer<Resource>
{
   /**
    * Constructor.
    *
    * @param projectFile parent project
    */
   public ResourceContainer(ProjectFile projectFile)
   {
      super(projectFile);
   }

   @Override public void removed(Resource resource)
   {
      m_uniqueIDMap.remove(resource.getUniqueID());
      m_idMap.remove(resource.getID());

      Iterator<ResourceAssignment> iter = m_projectFile.getAllResourceAssignments().iterator();
      Integer resourceUniqueID = resource.getUniqueID();
      while (iter.hasNext() == true)
      {
         ResourceAssignment assignment = iter.next();
         if (NumberHelper.equals(assignment.getResourceUniqueID(), resourceUniqueID))
         {
            assignment.getTask().removeResourceAssignment(assignment);
            iter.remove();
         }
      }

      ProjectCalendar calendar = resource.getResourceCalendar();
      if (calendar != null)
      {
         calendar.remove();
      }
   }

   /**
    * Add a resource to the project.
    *
    * @return new resource instance
    */
   public Resource add()
   {
      Resource resource = new Resource(m_projectFile);
      add(resource);
      return (resource);
   }
}
