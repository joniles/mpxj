

package org.mpxj.msplanner;

import java.util.UUID;

public class MsPlannerProject
{
   /**
    * Constructor.
    */
   public MsPlannerProject()
   {

   }

   /**
    * Constructor.
    *
    * @param id project unique ID
    * @param name project name
    */
   public MsPlannerProject(UUID id, String name)
   {
      m_projectId = id;
      m_projectName = name;
   }

   /**
    * Retrieve the project's unique ID.
    *
    * @return unique ID
    */
   public UUID getProjectId()
   {
      return m_projectId;
   }

   /**
    * Set the project's unique ID.
    *
    * @param projectId unique ID
    */
   public void setProjectId(UUID projectId)
   {
      m_projectId = projectId;
   }

   /**
    * Retrieve the project's name.
    *
    * @return project name
    */
   public String getProjectName()
   {
      return m_projectName;
   }

   /**
    * Set the project's name.
    *
    * @param projectName project name
    */
   public void setProjectName(String projectName)
   {
      m_projectName = projectName;
   }

   @Override public String toString()
   {
      return "[MsPlannerProject projectId=" + m_projectId + ", projectName=" + m_projectName + "]";
   }

   private UUID m_projectId;
   private String m_projectName;
}
