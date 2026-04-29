/*
 * file:       ProjectPropertiesHelper.java
 * author:     Jon Iles
 * date:       2026-04-29
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

package org.mpxj.common;

import org.mpxj.ProjectProperties;

/**
 * Helper methods relating to the ProjectProperties class.
 */
public final class ProjectPropertiesHelper
{
   /**
    * This method retrieves a title for the project. It will use
    * the explicitly set title from the project properties,
    * otherwise it will fall back on the name of the summary class.
    * Finally, if there is no summary class it wil use a default value.
    *
    * @param properties ProjectProperties instance
    * @return project title
    */
   public static String getProjectTitle(ProjectProperties properties)
   {
      String title = properties.getProjectTitle();
      if (title == null || title.isEmpty())
      {
         // We don't have an explicit title set.
         // If the parent project has a single child task
         // assume that this is a summary task and use
         // the task name as the project title.
         if (properties.getParentFile().getChildTasks().size() == 1)
         {
            title = properties.getParentFile().getChildTasks().get(0).getName();
         }

         // If we still don't have a title set, use a default value.
         if (title == null || title.isEmpty())
         {
            title = "Project1";
         }
      }
      return title;
   }
}
