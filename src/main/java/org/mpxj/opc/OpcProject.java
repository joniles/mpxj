package org.mpxj.opc;

public class OpcProject
{
   public long getProjectId()
   {
      return m_projectId;
   }

   public void setProjectId(long projectId)
   {
      m_projectId = projectId;
   }

   public long getWorkspaceId()
   {
      return m_workspaceId;
   }

   public void setWorkspaceId(long workspaceId)
   {
      m_workspaceId = workspaceId;
   }

   public String getProjectName()
   {
      return projectName;
   }

   public void setProjectName(String projectName)
   {
      this.projectName = projectName;
   }

   @Override public String toString()
   {
      return "[OpcProject projectId=" + m_projectId + ", workspaceId=" + m_workspaceId + ", projectName=" + projectName + "]";
   }

   private long m_projectId;
   private long m_workspaceId;
   private String projectName;
}
