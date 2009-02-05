/*
 * file:       TimephasedResourceAssignment.java
 * author:     Jon Iles
 * copyright:  (c) Packwood Software 2008
 * date:       25/10/2008
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

package net.sf.mpxj;

import java.util.Date;

/**
 * This class represents a span of a number of days of work. 
 * The duration of work carried out on each day is represented by the
 * "per day" attribute. The start and end of the span is represented by 
 * "start work" and "cumulative work" attributes. 
 */
public final class TimephasedResourceAssignment
{
   /**
    * Retrieve the start date.
    * 
    * @return start date
    */
   public Date getStart()
   {
      return m_start;
   }

   /**
    * Set the point at which work starts.
    * 
    * @param start start date
    */
   public void setStart(Date start)
   {
      m_start = start;
   }

   /**
    * Retrieve the duration of work allocated per day.
    * 
    * @return duration of work per day
    */
   public Duration getWorkPerDay()
   {
      return m_workPerDay;
   }

   /**
    * Set the duration of work allocated per day.
    * 
    * @param workPerDay duration of work per day
    */
   public void setWorkPerDay(Duration workPerDay)
   {
      m_workPerDay = workPerDay;
   }

   /**
    * Retrieve the modified flag.
    * 
    * @return modified flag
    */
   public boolean getModified()
   {
      return m_modified;
   }

   /**
    * Set the modified flag. 
    * 
    * @param modified modified flag
    */
   public void setModified(boolean modified)
   {
      m_modified = modified;
   }

   /**
    * Retrieve the total work duration.
    * 
    * @return total work duration
    */
   public Duration getTotalWork()
   {
      return m_totalWork;
   }

   /**
    * Set the total work duration.
    * 
    * @param totalWork cumulative work duration
    */
   public void setTotalWork(Duration totalWork)
   {
      m_totalWork = totalWork;
   }

   /**
    * Retrieve the finish date.
    * 
    * @return finish date
    */
   public Date getFinish()
   {
      return m_finish;
   }

   /**
    * Set the finish date.
    * 
    * @param finish finish date
    */
   public void setFinish(Date finish)
   {
      m_finish = finish;
   }

   /**
    * {@inheritDoc}
    */
   @Override public String toString()
   {
      return "[TimephasedResourceAssignment startWork=" + m_start + " totalWork=" + m_totalWork + " finishWork=" + m_finish + " workPerDay=" + m_workPerDay + " modified=" + m_modified + "]";
   }

   private Date m_start;
   private Duration m_totalWork;
   private Date m_finish;
   private Duration m_workPerDay;
   private boolean m_modified;
}
