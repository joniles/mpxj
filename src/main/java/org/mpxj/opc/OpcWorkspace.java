package org.mpxj.opc;

class OpcWorkspace
{
   public long getWorkspaceId()
   {
      return m_workspaceId;
   }

   public void setWorkspaceId(long workspaceId)
   {
      m_workspaceId = workspaceId;
   }

   public String getWorkspaceName()
   {
      return m_workspaceName;
   }

   public void setWorkspaceName(String workspaceName)
   {
      m_workspaceName = workspaceName;
   }

   private long m_workspaceId;
   private String m_workspaceName;
}
