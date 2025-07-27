/*
 * file:       JobStatus.java
 * author:     Jon Iles
 * date:       2025-07-09
 */

/*
 * This library is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as published by the
 * Free Software Foundation; either version 2.1 of the License, or (at your
 * option) any later version.
 *
 * This library is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY
 * or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public
 * License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this library; if not, write to the Free Software Foundation, Inc.,
 * 59 Temple Place, Suite 330, Boston, MA 02111-1307, USA.
 */

package org.mpxj.opc;

/**
 * Represents the JSON response when querying the status of a project export job.
 */
class JobStatus
{
   /**
    * Retrieve the job type.
    *
    * @return job type
    */
   public String getJobType()
   {
      return m_jobType;
   }

   /**
    * Set the job type.
    *
    * @param jobType job type
    */
   public void setJobType(String jobType)
   {
      m_jobType = jobType;
   }

   /**
    * Retrieve the job status.
    *
    * @return job status
    */
   public String getJobStatus()
   {
      return m_jobStatus;
   }

   /**
    * Set the job status.
    *
    * @param jobStatus job status
    */
   public void setJobStatus(String jobStatus)
   {
      m_jobStatus = jobStatus;
   }

   /**
    * Retrieve the job ID.
    *
    * @return job ID
    */
   public long getJobId()
   {
      return m_jobId;
   }

   /**
    * Set the job ID.
    *
    * @param jobId job ID
    */
   public void setJobId(long jobId)
   {
      m_jobId = jobId;
   }

   private String m_jobType;
   private String m_jobStatus;
   private long m_jobId;
}
