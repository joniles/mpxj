/*
 * file:       TimephasedResourceAssignment
 * author:     Jon Iles
 * copyright:  (c) Packwood Software Limited 2008
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



/**
 * This class represents a span of a number of days of work. 
 * The duration of work carried out on each day is represented by the
 * "per day" attribute. The start and end of the span is represented by 
 * "start work" and "cumulative work" attributes. 
 */
public final class TimephasedResourceAssignment
{
   /**
    * Retrieve the point at which work starts.
    * 
    * @return start work duration
    */
   public Duration getStartWork()
   {
      return m_startWork;
   }
   
   /**
    * Set the point at which work starts.
    * 
    * @param startWork start work duration
    */
   public void setStartWork(Duration startWork)
   {
      m_startWork = startWork;
   }
   
   /**
    * Retrieve the cumulative work duration.
    * 
    * @return cumulative work duration
    */
   public Duration getCumulativeWork()
   {
      return m_cumulativeWork;
   }
   
   /**
    * Set the cumulative work duration.
    * 
    * @param cumulativeWork cumulative work duration
    */
   public void setCumulativeWork(Duration cumulativeWork)
   {
      m_cumulativeWork = cumulativeWork;
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
   public boolean getModified ()
   {
      return m_modified;
   }
   
   /**
    * Set the modified flag. 
    * 
    * @param modified modified flag
    */
   public void setModified (boolean modified)
   {
      m_modified = modified;
   }
   
   /**
    * {@inheritDoc}
    */
   @Override
   public String toString ()
   {
      return "[TimephasedResourceAssignment startWork="+m_startWork+" cumulativeWork="+m_cumulativeWork+" workPerDay="+m_workPerDay+" modified="+m_modified+"]";
   }
   
   private Duration m_startWork;
   private Duration m_cumulativeWork;
   private Duration m_workPerDay;
   private boolean m_modified;
}
