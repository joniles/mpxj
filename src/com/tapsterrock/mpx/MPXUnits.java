/*
 * file:       MPXUnits.java
 * author:     Jon Iles
 * copyright:  (c) Tapster Rock Limited 2002-2003
 * date:       01/01/2003
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

import java.text.DecimalFormat;

/**
 * This class deals with an odd case where the max units value,
 * which is a percentage, is represented as a float between 0 and 1.
 * The formatting used by MSP is not the same as that used for all other
 * floats, hence this special case.
 */
class MPXUnits extends Number
{
   /**
    * Constructs instance from a String value. This constructor is used
    * when reading the units value from an MPX file. Note that in an
    * MPX file the value is in the range 0.0 to 1.0, whereas we hold
    * the value as a percentage in the range 0.0 to 100.0.
    *
    * @param value value
    */
   public MPXUnits (String value)
   {
      m_value = Double.parseDouble(value) * 100;
   }

   /**
    * Constructs instance from a Number value. Note that the value
    * should be in the range 0.0 to 100.0.
    *
    * @param value value
    */
   public MPXUnits (Number value)
   {
      m_value = value.doubleValue();
   }

   /**
    * Constructs instance from a double value. Note that the value
    * should be in the range 0.0 to 100.0.
    *
    * @param value value
    */
   public MPXUnits (double value)
   {
      m_value = value;
   }

   /**
    * This method generates a string in MPX format representing the
    * contents of this record.
    *
    * @return string containing the data for this record in MPX format.
    */
   public String toString ()
   {
      return (FORMAT.format(m_value/100));
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
   private static final DecimalFormat FORMAT = new DecimalFormat ("#.##");
}
