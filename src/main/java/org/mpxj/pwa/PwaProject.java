package org.mpxj.pwa;

import java.util.UUID;

public class PwaProject
{
   public PwaProject()
   {

   }

   public PwaProject(UUID id, String name)
   {
      m_projectId = id;
      m_projectName = name;
   }

   public UUID getProjectId()
   {
      return m_projectId;
   }

   public void setProjectId(UUID projectId)
   {
      m_projectId = projectId;
   }

   public String getProjectName()
   {
      return m_projectName;
   }

   public void setProjectName(String projectName)
   {
      m_projectName = projectName;
   }

   @Override public String toString()
   {
      return "[PwaProject projectId=" + m_projectId + ", projectName=" + m_projectName + "]";
   }

   private UUID m_projectId;
   private String m_projectName;
}
