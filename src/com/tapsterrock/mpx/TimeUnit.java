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
    * This method is used to parse a string representation of a time
    * unit, and return the appropriate constant value.
    *
    * @param units string representation of a time unit
    * @return numeric constant
    * @throws MPXException normally thrown when parsing fails
    */
   static int parse (String units, Locale locale)
      throws MPXException
   {
      Map map = LocaleData.getMap(locale, LocaleData.TIME_UNITS_MAP);
      Integer result = (Integer)map.get(units);
      if (result == null)
      {
         throw new MPXException (MPXException.INVALID_TIME_UNIT + " " +units);
      }
      return (result.intValue());
   }


   /**
    * This method takes a numeric constant representing a time unit
    * and returns a string representation.
    *
    * @param units numeric constant representing a time unit
    * @return string representation
    */
   static String format (int units, Locale locale)
   {
      String result;
      String[] unitNames = LocaleData.getStringArray(locale, LocaleData.TIME_UNITS_ARRAY);

      if (units < 0 || units >= unitNames.length)
      {
         result = "";
      }
      else
      {
         result = unitNames[units];
      }

      return (result);
   }

   /**
    * Constant representing Minutes
    */
   public static final int MINUTES = 0;

   /**
    * Constant representing Hours
    */
   public static final int HOURS = 1;

   /**
    * Constant representing Days
    */
   public static final int DAYS = 2;

   /**
    * Constant representing Weeks
    */
   public static final int WEEKS = 3;

   /**
    * Constant representing Months
    */
   public static final int MONTHS = 4;

   /**
    * Constant representing Years
    */
   public static final int YEARS = 5;

   /**
    * Constant representing Percent
    */
   public static final int PERCENT = 6;

   /**
    * Constant representing Elapsed Minutes
    */
   public static final int ELAPSED_MINUTES = 7;

   /**
    * Constant representing Elapsed Hours
    */
   public static final int ELAPSED_HOURS = 8;

   /**
    * Constant representing Elapsed Days
    */
   public static final int ELAPSED_DAYS = 9;

   /**
    * Constant representing Elapsed Weeks
    */
   public static final int ELAPSED_WEEKS = 10;

   /**
    * Constant representing Elapsed Months
    */
   public static final int ELAPSED_MONTHS = 11;

   /**
    * Constant representing Elapsed Years
    */
   public static final int ELAPSED_YEARS = 12;

   /**
    * Constant representing Elapsed Percent
    */
   public static final int ELAPSED_PERCENT = 13;
}
