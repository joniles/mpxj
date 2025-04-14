/*
 * file:       NumberHelper.java
 * author:     Jon Iles
 * copyright:  (c) Packwood Software 2005
 * date:       25/03/2005
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

package org.mpxj.common;

import java.math.BigInteger;
import java.util.Arrays;

/**
 * This class contains utility methods for handling Number objects and
 * numeric primitives.
 */
public final class NumberHelper
{
   /**
    * This method retrieves an int value from a Number instance. It
    * returns zero by default if a null value is supplied.
    *
    * @param value Number instance
    * @return int value
    */
   public static final int getInt(Number value)
   {
      return (value == null ? 0 : value.intValue());
   }

   /**
    * This method retrieves an int value from a String instance.
    * It returns zero by default if a null value or an empty string is supplied.
    *
    * @param value string representation of an integer
    * @return int value
    */
   public static final int getInt(String value)
   {
      return (value == null || value.isEmpty() ? 0 : Integer.parseInt(value));
   }

   /**
    * This method retrieves a double value from a String instance.
    * It returns zero by default if a null value or an empty string is supplied.
    *
    * @param value string representation of a double
    * @return double value
    */
   public static final double getDouble(String value)
   {
      return (value == null || value.isEmpty() ? 0 : Double.parseDouble(value));
   }

   /**
    * This method retrieves a double value from a String instance.
    * It returns null by default if a null value or an empty string is supplied.
    *
    * @param value string representation of a double
    * @return double value
    */
   public static final Double getDoubleObject(String value)
   {
      if (value == null || value.isEmpty())
      {
         return null;
      }
      return Double.valueOf(value);
   }

   /**
    * Utility method used to convert an arbitrary Number into an Integer.
    *
    * @param value Number instance
    * @return Integer instance
    */
   public static final Integer getInteger(Number value)
   {
      Integer result = null;
      if (value != null)
      {
         if (value instanceof Integer)
         {
            result = (Integer) value;
         }
         else
         {
            result = Integer.valueOf((int) Math.round(value.doubleValue()));
         }
      }
      return (result);
   }

   /**
    * Converts a string representation of an integer into an Integer object.
    * Silently ignores any parse exceptions and returns null.
    *
    * @param value String representation of an integer
    * @return Integer instance
    */
   public static final Integer getInteger(String value)
   {
      Integer result;

      try
      {
         result = value == null ? null : Integer.valueOf(Integer.parseInt(value));
      }

      catch (Exception ex)
      {
         result = null;
      }

      return (result);
   }

   /**
    * Utility method used to convert a Number into a BigInteger.
    *
    * @param value Number instance
    * @return BigInteger instance
    */
   public static final BigInteger getBigInteger(Number value)
   {
      BigInteger result = null;
      if (value != null)
      {
         if (value instanceof BigInteger)
         {
            result = (BigInteger) value;
         }
         else
         {
            result = BigInteger.valueOf(Math.round(value.doubleValue()));
         }
      }
      return (result);
   }

   /**
    * Utility method used to convert a Number into a double.
    * This has been implemented to allow a singleton to be
    * used to represent zero. This makes a considerable saving
    * in memory utilisation.
    *
    * @param value Number instance
    * @return double value
    */
   public static final double getDouble(Number value)
   {
      return (value == null ? 0 : value.doubleValue());
   }

   /**
    * Utility method used to convert a double into a Double.
    * This has been implemented to allow a singleton to be
    * used to represent zero. This makes a considerable saving
    * in memory utilisation.
    *
    * NOTE: as of Java 1.5 the Double.valueOf method is
    * supposed to implement caching. The current JDK 1.5 implementation
    * doesn't appear to do this, so we'll leave this method alone for
    * now. We can look at replacing this when we move to 1.6.
    *
    * @param value Number instance
    * @return double value
    */
   public static final Double getDouble(double value)
   {
      return (value == 0 ? DOUBLE_ZERO : Double.valueOf(value));
   }

   /**
    * Utility method used to round a double to the given precision.
    *
    * @param value value to truncate
    * @param precision Number of decimals to round to.
    * @return double value
    */
   public static final double round(double value, double precision)
   {
      precision = Math.pow(10, precision);
      return Math.round(value * precision) / precision;
   }

   /**
    * Utility method to convert a String to an Integer, and
    * handles null values.
    *
    * @param value string representation of an integer
    * @return int value
    */
   public static final Integer parseInteger(String value)
   {
      return (value == null || value.isEmpty() ? null : Integer.valueOf(Integer.parseInt(value)));
   }

   /**
    * This method is used to compare two numbers. The unusual point
    * about this method is that it takes account of null values.
    * If the two number objects are both null, these are taken to
    * be equal, if one is null and the other is not null, these are
    * taken to be different. Finally, if we have two valid number
    * objects, these are compared in the normal manner to determine
    * equality.
    *
    * @param lhs left hand argument
    * @param rhs right hand argument
    * @return result of equality test
    */
   public static boolean equals(Number lhs, Number rhs)
   {
      boolean result = false;

      if (lhs == null && rhs == null)
      {
         result = true;
      }
      else
      {
         if (lhs != null && rhs != null)
         {
            result = lhs.equals(rhs);
         }
      }

      return (result);
   }

   /**
    * Compare two integers, accounting for null values.
    *
    * @param n1 integer value
    * @param n2 integer value
    * @return comparison result
    */
   public static int compare(Integer n1, Integer n2)
   {
      int result;
      if (n1 == null || n2 == null)
      {
         result = (n1 == null && n2 == null ? 0 : (n1 == null ? 1 : -1));
      }
      else
      {
         result = n1.compareTo(n2);
      }
      return (result);
   }

   /**
    * Compares two doubles for equality, within an allowable range
    * of difference.
    *
    * @param lhs value to test
    * @param rhs value to test
    * @param delta allowable difference
    * @return boolean value
    */
   public static boolean equals(double lhs, double rhs, double delta)
   {
      return Math.abs(lhs - rhs) < delta;
   }

   /**
    * Calculate the sum of a list of numbers and express the result as a Double instance.
    *
    * @param values list of numbers
    * @return Double instance
    */
   public static Double sumAsDouble(Number... values)
   {
      return Double.valueOf(Arrays.stream(values).mapToDouble(NumberHelper::getDouble).sum());
   }

   public static final Double DOUBLE_ZERO = Double.valueOf(0);
}
