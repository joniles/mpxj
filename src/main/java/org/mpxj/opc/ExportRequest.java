package org.mpxj.opc;

class ExportRequest
{
   public ExportRequest(OpcProject project)
   {
      m_project = project;
      m_projectInfo.setProjectId(project.getProjectId());
   }

   public boolean getExportCompressedFile()
   {
      return m_exportCompressedFile;
   }

   public void setExportCompressedFile(boolean exportCompressedFile)
   {
      m_exportCompressedFile = exportCompressedFile;
   }

   public long getWorkspaceId()
   {
      return m_project.getWorkspaceId();
   }

   public ExportRequestProject[] getProjectsInfo()
   {
      return new ExportRequestProject[] { m_projectInfo };
   }

   private final OpcProject m_project;
   private boolean m_exportCompressedFile;
   private final ExportRequestProject  m_projectInfo = new ExportRequestProject();
}
