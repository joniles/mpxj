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
public class MPXUnits
{
   /**
    * Constructs instance from a float value.
    *
    * @param value value
    */
   public MPXUnits (float value)
   {
      m_value = value;
   }

   /**
    * Constructs instance from a Number value.
    *
    * @param value value
    */
   public MPXUnits (Number value)
   {
      this (value.floatValue());
   }

   /**
    * Constructs instance from a String value.
    *
    * @param value value
    */
   public MPXUnits (String value)
   {
      this (Float.parseFloat(value));
   }

   /**
    * Accessor method for value
    *
    * @return value
    */
   public float getValue()
   {
      return (m_value);
   }

   /**
    * This method generates a string in MPX format representing the
    * contents of this record.
    *
    * @return string containing the data for this record in MPX format.
    */
   public String toString ()
   {
      return (FLOAT_FORMAT.format(m_value));
   }

   /**
    * Internal value
    */
   private float m_value;

   private static final DecimalFormat FLOAT_FORMAT = new DecimalFormat ("#.##");
}
