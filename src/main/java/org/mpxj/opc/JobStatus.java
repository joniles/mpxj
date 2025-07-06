package org.mpxj.opc;

class JobStatus
{
   public String getJobType()
   {
      return m_jobType;
   }

   public void setJobType(String jobType)
   {
      m_jobType = jobType;
   }

   public String getJobStatus()
   {
      return m_jobStatus;
   }

   public void setJobStatus(String jobStatus)
   {
      m_jobStatus = jobStatus;
   }

   public long getJobId()
   {
      return m_jobId;
   }

   public void setJobId(long jobId)
   {
      m_jobId = jobId;
   }

   private String m_jobType;
   private String m_jobStatus;
   private long m_jobId;
}
