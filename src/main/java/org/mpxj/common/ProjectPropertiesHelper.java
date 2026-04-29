package org.mpxj.common;

import org.mpxj.ProjectFile;
import org.mpxj.ProjectProperties;

public final class ProjectPropertiesHelper
{
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
