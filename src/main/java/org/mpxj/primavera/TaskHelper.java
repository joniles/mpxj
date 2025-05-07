/*
 * file:       TaskHelper.java
 * author:     Jon Iles
 * copyright:  (c) Packwood Software 2023
 * date:       07/03/2023
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
 * Provides methods to work with task fields when importing and exporting
 * schedules to P6.
 */
final class TaskHelper
{
   /**
    * Retrieve the WBS code attribute.
    *
    * @param task Task instance
    * @return WBS code attribute
    */
   public static String getWbsCode(Task task)
   {
      // If we don't have a WBS code, use a default value
      String code = task.getWBS();
      if (code == null || code.isEmpty())
      {
         code = DEFAULT_WBS_CODE;
      }
      else
      {
         String prefix = null;
         String projectID = task.getParentFile().getProjectProperties().getProjectID();
         String separator = task.getParentFile().getProjectProperties().getWbsCodeSeparator();
         if (task.getParentTask() == null && projectID != null)
         {
            prefix = projectID + separator;
         }
         else
         {
            if (task.getParentTask() != null)
            {
               prefix = task.getParentTask().getWBS() + separator;
            }
         }

         // If we have a parent task, and it looks like WBS contains the full path
         // (including the parent's WBS), remove the parent's WBS. This matches
         // how P6 exports this value. This test is brittle as it assumes
         // the default WBS separator has been used.
         if (prefix != null && code.startsWith(prefix))
         {
            code = code.substring(prefix.length());
         }
      }
      return code;
   }

   private static final String DEFAULT_WBS_CODE = "WBS";
}
