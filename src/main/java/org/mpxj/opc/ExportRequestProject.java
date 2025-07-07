package org.mpxj.opc;

class ExportRequestProject
{
   public long getProjectId()
   {
      return m_projectId;
   }

   public void setProjectId(long projectId)
   {
      m_projectId = projectId;
   }

   public ExportRequestBaseline[] getBaselineProjectInfo()
   {
      return m_baselineProjectInfo;
   }

   public void setBaselineProjectInfo(ExportRequestBaseline[] baselineProjectInfo)
   {
      m_baselineProjectInfo = baselineProjectInfo;
   }

   private long m_projectId;
   private ExportRequestBaseline[] m_baselineProjectInfo;
}
