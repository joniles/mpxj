/*
 * file:       DateHelper.java
 * author:     Jon Iles
 * copyright:  (c) Packwood Software 2006
 * date:       Jan 18, 2006
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

package net.sf.mpxj.common;

import java.time.LocalDateTime;
import java.util.ArrayDeque;
import java.util.Calendar;
import java.util.Date;
import java.util.Deque;
import java.util.TimeZone;

/**
 * Utility methods for manipulating dates.
 */
public final class DateHelper
{
   /**
    * Constructor.
    */
   private DateHelper()
   {
      // private constructor to prevent instantiation
   }

   /**
    * Acquire a Calendar instance and set the initial date.
    *
    * @param date initial date
    * @return Calendar instance
    */
   public static Calendar popCalendar(Date date)
   {
      Calendar calendar;
      Deque<Calendar> calendars = CALENDARS.get();
      if (calendars.isEmpty())
      {
         calendar = Calendar.getInstance();
      }
      else
      {
         calendar = calendars.pop();
      }

      calendar.setTime(date);
      return calendar;
   }

   /**
    * Return a Calendar instance.
    *
    * @param cal Calendar instance to return
    */
   public static void pushCalendar(Calendar cal)
   {
      CALENDARS.get().push(cal);
   }

   /**
    * Date representing NA at the start of a date range: January 01 00:00:00 1984.
    */
   public static final LocalDateTime START_DATE_NA = LocalDateTime.of(1984, 1, 1, 0, 0);

   /**
    * Date representing NA at the end of a date range: Friday December 31 23:59:00 2049.
    * That's actually the value used by older versions of MS Project. The most recent version
    * of MS Project uses Friday December 31 23:59:06 2049 (note the six extra seconds).
    * The problem with using this value to represent NA at the end of a date range is it
    * isn't interpreted correctly by older versions of MS Project. The compromise here is that
    * we'll use the value recognised by older versions of MS Project, which will work as expected
    * and display NA as the end date. For the current version of MS Project this will display a
    * the end date as 2049, rather than NA, but this should still be interpreted correctly.
    * TODO: consider making this behaviour configurable.
    */
   public static final LocalDateTime END_DATE_NA = LocalDateTime.of(2049, 12, 31, 23, 59);

   /**
    * Number of milliseconds per minute.
    */
   public static final long MS_PER_MINUTE = 60 * 1000;

   /**
    * Number of milliseconds per minute.
    */
   public static final long MS_PER_HOUR = 60 * MS_PER_MINUTE;

   /**
    * Number of milliseconds per day.
    */
   public static final long MS_PER_DAY = 24 * MS_PER_HOUR;

   private static final ThreadLocal<Deque<Calendar>> CALENDARS = ThreadLocal.withInitial(ArrayDeque::new);
}
