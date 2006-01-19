/*
 * file:       TimeUnit.java
 * author:     Jon Iles
 * copyright:  (c) Tapster Rock Limited 2002-2003
 * date:       03/01/2003
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

import java.util.Locale;
import java.util.Map;

/**
 * This class contains utility functions allowing time unit specifications
 * to be parsed and formatted.
 */
public final class TimeUnit
{
   /**
    * Private constructor.
    *
    * @param value time unit value
    */
   private TimeUnit (int value)
   {
      m_value = value;
   }

   /**
    * Retrieves the int representation of the time unit.
    *
    * @return time unit value
    */
   public int getValue ()
   {
      return (m_value);
   }
   
   /**
    * Returns an integer representation of the time unit.
    *
    * @return integer representation of the time unit.
    */
   public String toString ()
   {
      return (Integer.toString(m_value));
   }

   /**
    * Retrieve a TimeUnit instance representing the supplied value.
    *
    * @param value time unit value
    * @return TimeUnit instance
    */
   public static TimeUnit getInstance (int value)
   {
      TimeUnit result;

      switch (value)
      {
         case MINUTES_VALUE:
         {
            result = MINUTES;
            break;
         }

         case HOURS_VALUE:
         {
            result = HOURS;
            break;
         }

         case DAYS_VALUE:
         {
            result = DAYS;
            break;
         }

         case WEEKS_VALUE:
         {
            result = WEEKS;
            break;
         }

         case MONTHS_VALUE:
         {
            result = MONTHS;
            break;
         }

         case YEARS_VALUE:
         {
            result = YEARS;
            break;
         }

         case PERCENT_VALUE:
         {
            result = PERCENT;
            break;
         }

         case ELAPSED_MINUTES_VALUE:
         {
            result = ELAPSED_MINUTES;
            break;
         }

         case ELAPSED_HOURS_VALUE:
         {
            result = ELAPSED_HOURS;
            break;
         }

         case ELAPSED_DAYS_VALUE:
         {
            result = ELAPSED_DAYS;
            break;
         }

         case ELAPSED_WEEKS_VALUE:
         {
            result = ELAPSED_WEEKS;
            break;
         }

         case ELAPSED_MONTHS_VALUE:
         {
            result = ELAPSED_MONTHS;
            break;
         }

         case ELAPSED_YEARS_VALUE:
         {
            result = ELAPSED_YEARS;
            break;
         }

         case ELAPSED_PERCENT_VALUE:
         {
            result = ELAPSED_PERCENT;
            break;
         }

         default:
         {
            result = DAYS;
            break;
         }
      }

      return (result);
   }

   /**
    * This method is used to parse a string representation of a time
    * unit, and return the appropriate constant value.
    *
    * @param units string representation of a time unit
    * @param locale target locale
    * @return numeric constant
    * @throws MPXException normally thrown when parsing fails
    */
   static TimeUnit parse (String units, Locale locale)
      throws MPXException
   {
      Map map = LocaleData.getMap(locale, LocaleData.TIME_UNITS_MAP);
      Integer result = (Integer)map.get(units);
      if (result == null)
      {
         throw new MPXException (MPXException.INVALID_TIME_UNIT + " " +units);
      }
      return (getInstance(result.intValue()));
   }


   private int m_value;

   /**
    * Constant representing Minutes.
    */
   public static final int MINUTES_VALUE = 0;

   /**
    * Constant representing Hours.
    */
   public static final int HOURS_VALUE = 1;

   /**
    * Constant representing Days.
    */
   public static final int DAYS_VALUE = 2;

   /**
    * Constant representing Weeks.
    */
   public static final int WEEKS_VALUE = 3;

   /**
    * Constant representing Months.
    */
   public static final int MONTHS_VALUE = 4;

   /**
    * Constant representing Years.
    */
   public static final int YEARS_VALUE = 5;

   /**
    * Constant representing Percent.
    */
   public static final int PERCENT_VALUE = 6;

   /**
    * Constant representing Elapsed Minutes.
    */
   public static final int ELAPSED_MINUTES_VALUE = 7;

   /**
    * Constant representing Elapsed Hours.
    */
   public static final int ELAPSED_HOURS_VALUE = 8;

   /**
    * Constant representing Elapsed Days.
    */
   public static final int ELAPSED_DAYS_VALUE = 9;

   /**
    * Constant representing Elapsed Weeks.
    */
   public static final int ELAPSED_WEEKS_VALUE = 10;

   /**
    * Constant representing Elapsed Months.
    */
   public static final int ELAPSED_MONTHS_VALUE = 11;

   /**
    * Constant representing Elapsed Years.
    */
   public static final int ELAPSED_YEARS_VALUE = 12;

   /**
    * Constant representing Elapsed Percent.
    */
   public static final int ELAPSED_PERCENT_VALUE = 13;


   /**
    * Constant representing Minutes.
    */
   public static final TimeUnit MINUTES = new TimeUnit(MINUTES_VALUE);

   /**
    * Constant representing Hours.
    */
   public static final TimeUnit HOURS = new TimeUnit(HOURS_VALUE);

   /**
    * Constant representing Days.
    */
   public static final TimeUnit DAYS = new TimeUnit(DAYS_VALUE);

   /**
    * Constant representing Weeks.
    */
   public static final TimeUnit WEEKS = new TimeUnit(WEEKS_VALUE);

   /**
    * Constant representing Months.
    */
   public static final TimeUnit MONTHS = new TimeUnit(MONTHS_VALUE);

   /**
    * Constant representing Years.
    */
   public static final TimeUnit YEARS = new TimeUnit(YEARS_VALUE);

   /**
    * Constant representing Percent.
    */
   public static final TimeUnit PERCENT = new TimeUnit(PERCENT_VALUE);

   /**
    * Constant representing Elapsed Minutes.
    */
   public static final TimeUnit ELAPSED_MINUTES = new TimeUnit(ELAPSED_MINUTES_VALUE);

   /**
    * Constant representing Elapsed Hours.
    */
   public static final TimeUnit ELAPSED_HOURS = new TimeUnit(ELAPSED_HOURS_VALUE);

   /**
    * Constant representing Elapsed Days.
    */
   public static final TimeUnit ELAPSED_DAYS = new TimeUnit(ELAPSED_DAYS_VALUE);

   /**
    * Constant representing Elapsed Weeks.
    */
   public static final TimeUnit ELAPSED_WEEKS = new TimeUnit(ELAPSED_WEEKS_VALUE);

   /**
    * Constant representing Elapsed Months.
    */
   public static final TimeUnit ELAPSED_MONTHS = new TimeUnit(ELAPSED_MONTHS_VALUE);

   /**
    * Constant representing Elapsed Years.
    */
   public static final TimeUnit ELAPSED_YEARS = new TimeUnit(ELAPSED_YEARS_VALUE);

   /**
    * Constant representing Elapsed Percent.
    */
   public static final TimeUnit ELAPSED_PERCENT = new TimeUnit(ELAPSED_PERCENT_VALUE);

}
