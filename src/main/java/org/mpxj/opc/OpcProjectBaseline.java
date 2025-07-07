package org.mpxj.opc;

public class OpcProjectBaseline
{
   public long getProjectBaselineId()
   {
      return m_projectBaselineId;
   }

   public void setProjectBaselineId(long projectBaselineId)
   {
      m_projectBaselineId = projectBaselineId;
   }

   public String getName()
   {
      return m_name;
   }

   public void setName(String name)
   {
      m_name = name;
   }

   @Override public String toString()
   {
      return "[OpcProjectBaseline project BaselineId=" + m_projectBaselineId + ", name=" + m_name + "]";
   }

   private long m_projectBaselineId;
   private String m_name;
}
