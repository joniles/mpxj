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

/**
 * This class represents percentage values as found in an MPX file.
 */
final class MPXPercentage extends Number
{
   /**
    * This constructor creates an instance of this class from a formatted
    * string value.
    *
    * @param value percentage value
    * @throws MPXException when the string parse fails
    */
   MPXPercentage (String value)
      throws MPXException
   {
      m_value = FORMAT.parse(value).doubleValue();
   }

   /**
    * This constructor creates an instance of this class from a number value.
    *
    * @param value percentage value
    */
   MPXPercentage (Number value)
   {
      m_value = value.doubleValue();
   }

   /**
    * This constructor creates an instance of this class from a number value.
    *
    * @param value percentage value
    */
   MPXPercentage (double value)
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
      return (FORMAT.format(m_value) + "%");
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
    * Internal value
    */
   private double m_value;

   /**
    * Number formatter.
    */
   private static final MPXNumberFormat FORMAT = new MPXNumberFormat("##0.##", '.', ',');
}