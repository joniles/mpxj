/*
 * file:       NumberUtility.java
 * author:     Jon Iles
 * copyright:  (c) Tapster Rock Limited 2005
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
 
package com.tapsterrock.mpx;

import java.math.BigInteger;

/**
 * This class contains utility methods for handling Number objects and
 * numeric primitives.
 */
public final class NumberUtility
{
   /**
    * This method retrieves an int value from a Number instance. It
    * returns zero by default if a null value is supplied.
    * 
    * @param value Number instance
    * @return int value
    */
   public static final int getInt (Number value)
   {
      return (value==null?0:value.intValue());
   }

   /**
    * This method retrieves an int value from a String instance.
    * It returns zero by default if a null value or an empty string is supplied.
    * 
    * @param value string representation of an integer
    * @return int value
    */
   public static final int getInt (String value)
   {
      return (value==null||value.length()==0?0:Integer.parseInt(value));
   }
   
   /**
    * Utility method used to convert an arbitrary Number into an Integer.
    *
    * @param value Number instance
    * @return Integer instance
    */
   public static final Integer getInteger (Number value)
   {
      Integer result = null;
      if (value != null)
      {
         if (value instanceof Integer)
         {
            result = (Integer)value;
         }
         else
         {
            result = new Integer(value.intValue());
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
   public static final Integer getInteger (String value)
   {
      Integer result;
      
      try
      {
         result = new Integer (value);
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
   public static final BigInteger getBigInteger (Number value)
   {
      BigInteger result = null;
      if (value != null)         
      {
         if (value instanceof BigInteger)
         {
            result = (BigInteger)value;
         }
         else
         {
            result = BigInteger.valueOf(value.longValue());
         }
      }
      return (result);
   }
   
   /**
    * Utility method used to convert a Number into a double.
    *
    * @param value Number instance
    * @return double value
    */
   public static final double getDouble (Number value)
   {
      return (value==null?0:value.doubleValue());
   }   
}
