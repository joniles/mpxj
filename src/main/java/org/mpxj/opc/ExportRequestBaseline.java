package org.mpxj.opc;

class ExportRequestBaseline
{
   public ExportRequestBaseline(long projectBaselineId)
   {
      m_projectBaselineId = projectBaselineId;
   }

   public long getProjectBaselineId()
   {
      return m_projectBaselineId;
   }

   private final long m_projectBaselineId;
}
