/*
 * file:       ResourceAssignmentContainer.java
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

/**
 * Manages the collection of resource assignments belonging to a project.
 */
public class ResourceAssignmentContainer extends ProjectEntityContainer<ResourceAssignment>
{
   /**
    * Constructor.
    *
    * @param projectFile parent project
    */
   public ResourceAssignmentContainer(ProjectFile projectFile)
   {
      super(projectFile);
   }

   @Override public void removed(ResourceAssignment assignment)
   {
      assignment.getTask().removeResourceAssignment(assignment);
      Resource resource = assignment.getResource();
      if (resource != null)
      {
         resource.removeResourceAssignment(assignment);
      }
   }
}
