/*
 * file:       DatatypeConverter.java
 * author:     Jon Iles
 * copyright:  (c) Packwood Software 2016
 * date:       09/06/2016
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

package net.sf.mpxj.asta;

import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.Calendar;


import net.sf.mpxj.common.DateHelper;

/*
 * Duration Types
 *
 * 10 Elapsed Year
 * 11 Elapsed Quarter
 * 12 Elapsed Month
 * 13 Elapsed Week
 * 14 Elapsed Day
 * 15 Elapsed Half Day
 * 16 Elapsed Hours
 * 17 Elapsed Minutes
 * 18 Elapsed Seconds
 *
 * 19 Year
 * 20 Quarter
 * 21 Month
 * 22 Week
 * 23 Day
 * 24 Half Day
 * 25 Hours
 * 26 Minutes
 * 27 Seconds
 */

/**
 * Methods for handling Asta data types.
 */
final class DatatypeConverter
{
   /**
    * Parse a string.
    *
    * @param value string representation
    * @return String value
    */
   public static String parseString(String value)
   {
      if (value != null)
      {
         // Strip angle brackets if present
         if (!value.isEmpty() && value.charAt(0) == '<')
         {
            value = value.substring(1, value.length() - 1);
         }

         // Strip quotes if present
         if (!value.isEmpty() && value.charAt(0) == '"')
         {
            value = value.substring(1, value.length() - 1);
         }
      }
      return value;
   }

   /**
    * Parse the string representation of a double.
    *
    * @param value string representation
    * @return Java representation
    */
   public static Number parseDouble(String value) throws ParseException
   {

      Number result = null;
      value = parseString(value);

      // If we still have a value
      if (value != null && !value.isEmpty() && !value.equals("-1 -1"))
      {
         int index = value.indexOf("E+");
         if (index != -1)
         {
            value = value.substring(0, index) + 'E' + value.substring(index + 2);
         }

         if (value.indexOf('E') != -1)
         {
            result = DOUBLE_FORMAT.get().parse(value);
         }
         else
         {
            result = Double.valueOf(value);
         }
      }

      return result;
   }

   /**
    * Parse a string representation of a Boolean value.
    *
    * @param value string representation
    * @return Boolean value
    */
   public static Boolean parseBoolean(String value) throws ParseException
   {
      Boolean result = null;
      Integer number = parseInteger(value);
      if (number != null)
      {
         result = number.intValue() == 0 ? Boolean.FALSE : Boolean.TRUE;
      }

      return result;
   }

   /**
    * Parse a string representation of an Integer value.
    *
    * @param value string representation
    * @return Integer value
    */
   public static Integer parseInteger(String value) throws ParseException
   {
      Integer result = null;

      if (value.length() > 0 && value.indexOf(' ') == -1)
      {
         if (value.indexOf('.') == -1)
         {
            result = Integer.valueOf(value);
         }
         else
         {
            Number n = DatatypeConverter.parseDouble(value);
            result = Integer.valueOf(n.intValue());
         }
      }

      return result;
   }

   /**
    * Parse the string representation of a timestamp.
    *
    * @param value string representation
    * @return Java representation
    */
   public static LocalDateTime parseEpochTimestamp(String value)
   {
      LocalDateTime result = null;

      if (value.length() > 0)
      {
         if (!value.equals("-1 -1"))
         {
            Calendar cal = DateHelper.popCalendar(JAVA_EPOCH);

            int index = value.indexOf(' ');
            if (index == -1)
            {
               if (value.length() < 6)
               {
                  value = "000000" + value;
                  value = value.substring(value.length() - 6);
               }

               int hours = Integer.parseInt(value.substring(0, 2));
               int minutes = Integer.parseInt(value.substring(2, 4));
               int seconds = Integer.parseInt(value.substring(4));

               cal.set(Calendar.HOUR, hours);
               cal.set(Calendar.MINUTE, minutes);
               cal.set(Calendar.SECOND, seconds);
            }
            else
            {
               long astaDays = Long.parseLong(value.substring(0, index));
               int astaSeconds = Integer.parseInt(value.substring(index + 1));

               cal.add(Calendar.DAY_OF_YEAR, (int) (astaDays - ASTA_EPOCH));
               cal.set(Calendar.MILLISECOND, 0);
               cal.set(Calendar.SECOND, 0);
               cal.set(Calendar.HOUR, 0);
               cal.add(Calendar.SECOND, astaSeconds);
            }

            result = cal.getTime();
            DateHelper.pushCalendar(cal);
         }
      }

      return result;
   }

   /**
    * Parse a timestamp value.
    *
    * @param value timestamp as String
    * @return timestamp as Date
    */
   public static LocalDateTime parseBasicTimestamp(String value) throws ParseException
   {
      LocalDateTime result = null;

      if (value.length() > 0)
      {
         if (!value.equals("-1 -1") && !value.equals("0"))
         {
            DateFormat df;
            if (value.endsWith(" 0"))
            {
               df = DATE_FORMAT1.get();
            }
            else
            {
               if (value.indexOf(' ') == -1)
               {
                  df = DATE_FORMAT2.get();
               }
               else
               {
                  df = TIMESTAMP_FORMAT.get();
                  int timeIndex = value.indexOf(' ') + 1;
                  if (timeIndex + 6 > value.length())
                  {
                     String time = value.substring(timeIndex);
                     value = value.substring(0, timeIndex) + "0" + time;
                  }
               }
            }

            result = df.parse(value);
         }
      }

      //System.out.println(value + "=>" + result);
      return result;
   }

   /**
    * Parse a time value.
    *
    * @param value time as String
    * @return time as Date
    */
   public static LocalDateTime parseBasicTime(String value) throws ParseException
   {
      LocalDateTime result = null;

      if (value.length() > 0)
      {
         if (!value.equals("0"))
         {
            value = "000000" + value;
            value = value.substring(value.length() - 6);
            result = TIME_FORMAT.get().parse(value);
         }
      }

      return result;
   }

   private static final ThreadLocal<DateFormat> TIMESTAMP_FORMAT = ThreadLocal.withInitial(() -> new SimpleDateFormat("yyyyMMdd HHmmss"));

   private static final ThreadLocal<DateFormat> DATE_FORMAT1 = ThreadLocal.withInitial(() -> new SimpleDateFormat("yyyyMMdd 0"));

   private static final ThreadLocal<DateFormat> DATE_FORMAT2 = ThreadLocal.withInitial(() -> new SimpleDateFormat("yyyyMMdd"));

   private static final ThreadLocal<DateFormat> TIME_FORMAT = ThreadLocal.withInitial(() -> new SimpleDateFormat("HHmmss"));

   private static final ThreadLocal<DecimalFormat> DOUBLE_FORMAT = ThreadLocal.withInitial(() -> new DecimalFormat("#.#E0"));

   private static final long JAVA_EPOCH = -2208988800000L;
   private static final long ASTA_EPOCH = 2415021L;
}
