/*
 * file:       ReadOptions.java
 * author:     Jon Iles
 * date:       2024-11-26
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

package org.mpxj.explorer;

/**
 * Represents options used when reading project files.
 */
class ReadOptions
{
   /**
    * Toggle open all flag.
    */
   public void toggleOpenAll()
   {
      m_openAll = !m_openAll;
   }

   /**
    * Retrieve the open all flag.
    *
    * @return open all flag
    */
   public boolean getOpenAll()
   {
      return m_openAll;
   }

   /**
    * Toggle the link cross project relations flag.
    */
   public void toggleLinkCrossProjectRelations()
   {
      m_linkCrossProjectRelations = !m_linkCrossProjectRelations;
   }

   /**
    * Retrieve the link cross project relations flag.
    *
    * @return link cross project relations flag
    */
   public boolean getLinkCrossProjectRelations()
   {
      return m_linkCrossProjectRelations;
   }

   /**
    * Toggle the expand subprojects flag.
    */
   public void toggleExpandSubprojects()
   {
      m_expandSubprojects = !m_expandSubprojects;
   }

   /**
    * Retrieve the expand subprojects flag.
    *
    * @return expand subprojects flag
    */
   public boolean getExpandSubprojects()
   {
      return m_expandSubprojects;
   }

   /**
    * Toggle the remove external tasks flag.
    */
   public void toggleRemoveExternalTasks()
   {
      m_removeExternalTasks = !m_removeExternalTasks;
   }

   /**
    * Retrieve the remove external tasks flag.
    *
    * @return remove external tasks flag
    */
   public boolean getRemoveExternalTasks()
   {
      return m_removeExternalTasks;
   }

   private boolean m_openAll;
   private boolean m_linkCrossProjectRelations;
   private boolean m_expandSubprojects;
   private boolean m_removeExternalTasks = true;
}
