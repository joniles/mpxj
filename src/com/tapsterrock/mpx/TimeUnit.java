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
   static int parse (String units)
      throws MPXException
   {
      int result;

      switch (units.charAt(0))
      {
         case 'm':
         {
            if (units.length() > 1 && units.charAt(1) == 'o')
            {
               result = MONTHS;
            }
            else
            {
               result = MINUTES;
            }
            break;
         }

         case 'h':
         {
            result = HOURS;
            break;
         }

         case 'd':
         {
            result = DAYS;
            break;
         }

         case 'w':
         {
            result = WEEKS;
            break;
         }

         case 'y':
         {
            result = YEARS;
            break;
         }

         case '%':
         {
            result = PERCENT;
            break;
         }

         case 'e':
         {
            if (units.length() == 1)
            {
               throw new MPXException (MPXException.INVALID_TIME_UNIT + " " +units);
            }

            switch (units.charAt(1))
            {
               case 'm':
               {
                  if (units.length() > 2 && units.charAt(2) == 'o')
                  {
                     result = ELAPSED_MONTHS;
                  }
                  else
                  {
                     result = ELAPSED_MINUTES;
                  }
                  break;
               }

               case 'h':
               {
                  result = ELAPSED_HOURS;
                  break;
               }

               case 'd':
               {
                  result = ELAPSED_DAYS;
                  break;
               }

               case 'w':
               {
                  result = ELAPSED_WEEKS;
                  break;
               }

               case 'y':
               {
                  result = ELAPSED_YEARS;
                  break;
               }

               case '%':
               {
                  result = ELAPSED_PERCENT;
                  break;
               }

               default:
               {
                  throw new MPXException ("Invalid time unit: " + units);
               }
            }

            break;
         }

         default:
         {
            throw new MPXException ("Invalid time unit: " + units);
         }
      }

      return (result);
   }


   /**
    * This method takes a numeric constant representing a time unit
    * and returns a string representation.
    *
    * @param units numeric constant representing a time unit
    * @return string representation
    */
   static String format (int units)
   {
      String result;

      if (units < 0 || units >= UNIT_NAMES.length)
      {
         result = "";
      }
      else
      {
         result = UNIT_NAMES[units];
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

   /**
    * Array of text names for the above time units.
    * The position of the name in the array corresponds to
    * the value of the constants.
    */
   private static final String[] UNIT_NAMES =
   {
      "m",
      "h",
      "d",
      "w",
      "mon",
      "y",
      "%",
      "em",
      "eh",
      "ed",
      "ew",
      "emon",
      "ey",
      "e%"
   };
}
