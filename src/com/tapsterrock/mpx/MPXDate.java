/*
 * file:       MPXDate.java
 * author:     Jon Iles
 * copyright:  (c) Tapster Rock Limited 2002-2003
 * date:       01/01/2003
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

import java.util.Calendar;
import java.util.Date;


/**
 * This class represents a date within an MPX file. It is used to
 * distinguish between dates and times, which both use Date objects
 * as their underlying representation.
 */
final class MPXDate extends Date
{
   /**
    * Constructor allowing a specific date to be set.
    *
    * @param format Date format
    * @param date date required expressed in milliseconds.
    */
   MPXDate (MPXDateFormat format, Date date)
   {
      super (date.getTime());
      m_format = format;
   }

   /**
    * Returns a new MPXDate instance whose underlying Java Date value
    * represents the start of the day (i.e. the time of day is 00:00:00.000)
    *
    * @return day start date
    */
   public MPXDate getDayStartDate ()
   {
      Calendar cal = Calendar.getInstance();
      cal.setTime(this);
      cal.set(Calendar.HOUR_OF_DAY, 0);
      cal.set(Calendar.MINUTE, 0);
      cal.set(Calendar.SECOND, 0);
      cal.set(Calendar.MILLISECOND, 0);
      return (new MPXDate(m_format, cal.getTime()));
   }

   /**
    * Returns a new MPXDate instance whose underlying Java Date value
    * represents the end of the day (i.e. the time of days is 11:59:59.999)
    *
    * @return day start date
    */
   public MPXDate getDayEndDate ()
   {
      Calendar cal = Calendar.getInstance();
      cal.setTime(this);
      cal.set(Calendar.MILLISECOND, 999);
      cal.set(Calendar.SECOND, 59);
      cal.set(Calendar.MINUTE, 59);
      cal.set(Calendar.HOUR_OF_DAY, 23);
      return (new MPXDate(m_format, cal.getTime()));
   }

   /**
    * This method builds a String representation of the date represented
    * by this instance.
    *
    * @return string representation of the date
    */
   public String toString ()
   {
      return (m_format.format (this));
   }

   /**
    * Date formatter.
    */
   private MPXDateFormat m_format;
}
