/*
 * file:       AstaDataType.java
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
import java.util.Calendar;
import java.util.Date;

/**
 * Methods for handling Asta data types.
 */
final class AstaDataType
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
    * @throws ParseException
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
            value = value.substring(0, index) + 'E' + value.substring(index + 2, value.length());
         }

         if (value.indexOf('E') != -1)
         {
            DecimalFormat df = DOUBLE_FORMAT.get();
            if (df == null)
            {
               df = new DecimalFormat("#.#E0");
               DOUBLE_FORMAT.set(df);
            }

            result = df.parse(value);
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
            Number n = AstaDataType.parseDouble(value);
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
   public static Date parseEpochTimestamp(String value)
   {
      Date result = null;

      if (value.length() > 0)
      {
         if (!value.equals("-1 -1"))
         {
            Calendar cal = Calendar.getInstance();
            cal.setTimeInMillis(JAVA_EPOCH);

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
   public static Date parseBasicTimestamp(String value) throws ParseException
   {
      Date result = null;

      if (value.length() > 0)
      {
         if (!value.equals("-1 -1") && !value.equals("0"))
         {
            DateFormat df;
            if (value.endsWith(" 0"))
            {
               df = DATE_FORMAT1.get();
               if (df == null)
               {
                  df = new SimpleDateFormat("yyyyMMdd 0");
                  DATE_FORMAT1.set(df);
               }
            }
            else
            {
               if (value.indexOf(' ') == -1)
               {
                  df = DATE_FORMAT2.get();
                  if (df == null)
                  {
                     df = new SimpleDateFormat("yyyyMMdd");
                     DATE_FORMAT2.set(df);
                  }
               }
               else
               {
                  df = TIMESTAMP_FORMAT.get();
                  if (df == null)
                  {
                     df = new SimpleDateFormat("yyyyMMdd HHmmss");
                     TIMESTAMP_FORMAT.set(df);
                  }

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
   public static Date parseBasicTime(String value) throws ParseException
   {
      Date result = null;

      if (value.length() > 0)
      {
         if (!value.equals("0"))
         {
            DateFormat df;
            df = TIME_FORMAT.get();
            if (df == null)
            {
               df = new SimpleDateFormat("HHmmss");
               TIME_FORMAT.set(df);
            }
            value = "000000" + value;
            value = value.substring(value.length() - 6);
            result = df.parse(value);
         }
      }

      return result;
   }

   private static final ThreadLocal<DateFormat> TIMESTAMP_FORMAT = new ThreadLocal<DateFormat>();
   private static final ThreadLocal<DateFormat> DATE_FORMAT1 = new ThreadLocal<DateFormat>();
   private static final ThreadLocal<DateFormat> DATE_FORMAT2 = new ThreadLocal<DateFormat>();
   private static final ThreadLocal<DateFormat> TIME_FORMAT = new ThreadLocal<DateFormat>();
   private static final long JAVA_EPOCH = -2208988800000L;
   private static final long ASTA_EPOCH = 2415021L;
   private static final ThreadLocal<DecimalFormat> DOUBLE_FORMAT = new ThreadLocal<DecimalFormat>();
}
