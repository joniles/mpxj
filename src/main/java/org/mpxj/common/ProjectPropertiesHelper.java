package org.mpxj.common;

import org.mpxj.ProjectProperties;

public final class ProjectPropertiesHelper
{
   public static String getProjectTitle(ProjectProperties properties)
   {
      String title = properties.getProjectTitle();
      if (title == null || title.isEmpty())
      {
         //         if (!m_projectFile.getTasks().isEmpty())
         //         {
         //            title = m_projectFile.getTasks().get(0).getName();
         //         }

         if (title == null || title.isEmpty())
         {
            title = "Project1";
         }
      }
      return title;
   }
}
