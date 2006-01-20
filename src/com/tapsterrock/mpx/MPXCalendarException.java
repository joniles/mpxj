/*
 * file:       MPXCalendarException.java
 * author:     Jon Iles
 * copyright:  (c) Tapster Rock Limited 2002-2003
 * date:       28/11/2003
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

package com.tapsterrock.mpx;

import java.util.Date;

/**
 * This class represents instances of Calendar Exception records from
 * an MPX file. It is used to define exceptions to the working days described
 * in both base and resource calendars.
 */
public final class MPXCalendarException extends MPXRecord
{
   /**
    * Default constructor.
    *
    * @param file the parent file to which this record belongs.
    * @throws MPXException Thrown on parse errors
    */
   MPXCalendarException (ProjectFile file)
      throws MPXException
   {
      super(file);
   }

   /**
    * Returns the from date.
    *
    * @return Date
    */
   public Date getFromDate ()
   {
      return (m_fromDate);
   }

   /**
    * Sets from date.
    *
    * @param from date
    */
   public void setFromDate (Date from)
   {
      m_fromDate = DateUtility.getDayStartDate(from);
   }

   /**
    * Get to date.
    *
    * @return Date
    */
   public Date getToDate ()
   {
      return (m_toDate);
   }

   /**
    * Sets To Date.
    *
    * @param to Date
    */
   public void setToDate (Date to)
   {
      m_toDate = DateUtility.getDayEndDate(to);
   }

   /**
    * Gets working status.
    *
    * @return boolean value
    */
   public boolean getWorking ()
   {
      return  (m_working);
   }

   /**
    * Sets working status of this exception.
    *
    * @param flag Boolean flag
    */
   public void setWorking (boolean flag)
   {
      m_working = flag;
   }

   /**
    * Get FromTime1.
    *
    * @return Time
    */
   public Date getFromTime1 ()
   {
      return (m_fromTime1);
   }

   /**
    * Sets from time 1.
    *
    * @param from Time
    */
   public void setFromTime1 (Date from)
   {
      m_fromTime1 = from;
   }

   /**
    * Get ToTime1.
    *
    * @return Time
    */
   public Date getToTime1 ()
   {
      return (m_toTime1);
   }

   /**
    * Sets to time 1.
    *
    * @param to Time
    */
   public void setToTime1 (Date to)
   {
      m_toTime1 = to;
   }

   /**
    * Get FromTime2.
    *
    * @return Time
    */
   public Date getFromTime2 ()
   {
      return (m_fromTime2);
   }

   /**
    * Sets from time 2.
    *
    * @param from Time
    */
   public void setFromTime2 (Date from)
   {
      m_fromTime2 = from;
   }

   /**
    * Get ToTime2.
    *
    * @return Time
    */
   public Date getToTime2 ()
   {
      return (m_toTime2);
   }

   /**
    * Sets to time 2.
    *
    * @param to Time
    */
   public void setToTime2 (Date to)
   {
      m_toTime2 = to;
   }

   /**
    * Get ToTime3.
    * 
    * @return Time
    */
   public Date getFromTime3 ()
   {
      return (m_fromTime3);
   }

   /**
    * Sets from time 3.
    *
    * @param from Time
    */
   public void setFromTime3 (Date from)
   {
      m_fromTime3 = from;
   }

   /**
    * Get ToTime3.
    *
    * @return Time
    */
   public Date getToTime3 ()
   {
      return (m_toTime3);
   }

   /**
    * Sets to time 3.
    *
    * @param to Time
    */
   public void setToTime3 (Date to)
   {
      m_toTime3 = to;
   }

   /**
    * This method determines whether the given date falls in the range of
    * dates covered by this exception. Note that this method assumes that both
    * the start and end date of this exception have been set.
    *
    * @param date Date to be tested
    * @return Boolean value
    */
   public boolean contains (Date date)
   {
      boolean result = false;

      if (date != null)
      {
         long time = date.getTime();

         if (time >= getFromDate().getTime() && time <= getToDate().getTime())
         {
            result = true;
         }
      }

      return (result);
   }
   
   private Date m_fromDate;
   private Date m_toDate;
   private boolean m_working;
   private Date m_fromTime1;
   private Date m_toTime1;
   private Date m_fromTime2;
   private Date m_toTime2;
   private Date m_fromTime3;
   private Date m_toTime3;
}
