/*
 * file:       SubProjectContainer.java
 * author:     Jon Iles
 * copyright:  (c) Packwood Software 2002-2015
 * date:       23/04/2015
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
 * Manages the sub projects belonging to a project.
 */
public class SubProjectContainer extends ListWithCallbacks<SubProject>
{
   /**
    * This package-private method is used to add resource sub project details.
    *
    * @param project sub project
    */
   public void setResourceSubProject(SubProject project)
   {
      m_resourceSubProject = project;
   }

   /**
    * Retrieves details of the sub project file used as a resource pool.
    *
    * @return sub project details
    */
   public SubProject getResourceSubProject()
   {
      return m_resourceSubProject;
   }

   private SubProject m_resourceSubProject;
}
