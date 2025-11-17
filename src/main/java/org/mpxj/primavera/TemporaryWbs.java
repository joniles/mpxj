/*
 * file:       TemporaryWbs.java
 * author:     Jon Iles
 * date:       2025-11-17
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

package org.mpxj.primavera;

import org.mpxj.Task;

/**
 * Represents a temporary WBS entry created to ensure that the WBS hierarchy
 * is in a form acceptable to P6.
 */
class TemporaryWbs
{
   /**
    * Constructor.
    *
    * @param task temporary wbs entry
    * @param outlineLevel original outline level
    */
   public TemporaryWbs(Task task, Integer outlineLevel)
   {
      m_task = task;
      m_outlineLevel = outlineLevel;
   }

   /**
    * retrieve the temporary wbs entry.
    *
    * @return temporary wbs entry
    */
   public Task getTask()
   {
      return m_task;
   }

   /**
    * Retrieve the original outline level.
    *
    * @return original outline level
    */
   public Integer getOutlineLevel()
   {
      return m_outlineLevel;
   }

   private final Task m_task;
   private final Integer m_outlineLevel;
}
