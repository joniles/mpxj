/*
 * file:       MPXPercentage.java
 * author:     Scott Melville
 *             Jon Iles
 * copyright:  (c) Tapster Rock Limited 2002-2003
 * date:       15/08/2002
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

import java.text.ParseException;

/**
 * This class represents percentage values as found in an MPX file.
 */
final class MPXPercentage extends Number implements ToStringRequiresFile
{
   /**
    * This constructor creates an instance of this class from a number value.
    *
    * @param value percentage value
    */
   private MPXPercentage (double value)
   {
      m_value = value;
   }

   /**
    * This method builds a String representation of the percentage represented
    * by this instance.
    *
    * @return string representation of the rate
    */
   public String toString ()
   {
      return (toString(DEFAULT_DECIMAL_FORMAT));
   }

   /**
    * This method builds a String representation of the percentage represented
    * by this instance.
    *
    * @param mpx parent mpx file
    * @return string representation of the rate
    */
   public String toString (MPXFile mpx)
   {
      return (toString(mpx.getPercentageDecimalFormat()));
   }

   /**
    * This method builds a String representation of the percentage represented
    * by this instance.
    *
    * @param format number format to use
    * @return string representation of the rate
    */
   String toString (MPXNumberFormat format)
   {
      return (format.format(m_value) + "%");
   }

   /**
    * Returns the value of the specified number as an <code>int</code>.
    * This may involve rounding.
    *
    * @return  the numeric value represented by this object after conversion
    *          to type <code>int</code>.
    */
   public int intValue()
   {
      return ((int)m_value);
   }

   /**
    * Returns the value of the specified number as a <code>long</code>.
    * This may involve rounding.
    *
    * @return  the numeric value represented by this object after conversion
    *          to type <code>long</code>.
    */
   public long longValue()
   {
      return ((long)m_value);
   }

   /**
    * Returns the value of the specified number as a <code>float</code>.
    * This may involve rounding.
    *
    * @return  the numeric value represented by this object after conversion
    *          to type <code>float</code>.
    */
   public float floatValue()
   {
      return ((float)m_value);
   }

   /**
    * Returns the value of the specified number as a <code>double</code>.
    * This may involve rounding.
    *
    * @return  the numeric value represented by this object after conversion
    *          to type <code>double</code>.
    */
   public double doubleValue ()
   {
      return (m_value);
   }
   
   /**
    * Retrieve an MPXPercentage instance representing the supplied value.
    * 
    * @param value percentage value
    * @return MPXPercentage instance
    */
   public static MPXPercentage getInstance (double value)
   {
      MPXPercentage result;
      
      if (value == 0)
      {
         result = ZERO;
      }
      else
      {
         result = new MPXPercentage(value);
      }
      
      return (result);
   }

   /**
    * Retrieve an MPXPercentage instance representing the supplied value.
    * 
    * @param value percentage value
    * @return MPXPercentage instance
    */   
   public static MPXPercentage getInstance (Number value)
   {
      return (getInstance(value.doubleValue()));
   }
   
   /**
    * Retrieve an MPXPercentage instance representing the supplied value.
    * 
    * @param value percentage value formatted as a string
    * @param format number format
    * @return MPXPercentage instance
    */   
   public static MPXPercentage getInstance (String value, MPXNumberFormat format)
      throws MPXException
   {
      try
      {
         return (getInstance(format.parse(value).doubleValue()));
      }
      
      catch (ParseException ex)
      {
         throw new MPXException ("Failed to parse percentage", ex);
      }
   }
   
   /**
    * Internal value.
    */
   private double m_value;

   /**
    * Number formatter format string.
    */
   static final String DECIMAL_FORMAT_STRING = "##0.##";

   /**
    * Number formatter.
    */
   private static final MPXNumberFormat DEFAULT_DECIMAL_FORMAT = new MPXNumberFormat(DECIMAL_FORMAT_STRING, '.', ',');
   
   private static final MPXPercentage ZERO = new MPXPercentage(0);
}
