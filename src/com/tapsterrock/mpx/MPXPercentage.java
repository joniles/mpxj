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
 *
 * @todo extend Number class?
 */
public class MPXPercentage
{
   /**
    * This constructor creates an instance of this class from a float value.
    *
    * @param value percentage value
    */
   public MPXPercentage (float value)
   {
      m_value = value;
   }

   /**
    * This constructor creates an instance of this class from a formatted
    * string value.
    *
    * @param value percentage value
    * @throws MPXException when the string parse fails
    */
   public MPXPercentage (String value)
      throws MPXException
   {
      m_value = FORMAT.parse(value).floatValue();
   }

   /**
    * Accessor method
    *
    * @return value
    */
   public float getValue ()
   {
      return (m_value);
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


   private float m_value;

   private static final MPXNumberFormat FORMAT = new MPXNumberFormat("##0.##");
}