package org.mpxj.opc;

import java.util.List;
import java.util.stream.Collectors;

class ExportRequest
{
   public ExportRequest(OpcProject project, List<OpcProjectBaseline> baselines, boolean compressed)
   {
      m_project = project;
      m_projectInfo.setProjectId(project.getProjectId());
      m_projectInfo.setBaselineProjectInfo(baselines.stream().map(b -> new ExportRequestBaseline(b.getProjectBaselineId())).toArray(ExportRequestBaseline[]::new));
      m_exportCompressedFile = compressed;
   }

   public boolean getExportCompressedFile()
   {
      return m_exportCompressedFile;
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
   private final boolean m_exportCompressedFile;
   private final ExportRequestProject  m_projectInfo = new ExportRequestProject();
}
