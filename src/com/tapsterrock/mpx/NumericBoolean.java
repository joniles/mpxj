/*
 * file:       NumericBoolean.java
 * author:     Jon Iles
 * copyright:  (c) Tapster Rock Limited 2002-2003
 * date:       17/02/2003
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
 * In an ideal world this class would be derived from the java.lang.Boolean
 * class, but in the real world... This class has been provided to allow
 * Boolean values to be represented that are stored as numeric values in the
 * MPX file. The class allows various int, boolean and String values to be
 * converted into instances of this class
 *
 */
public final class NumericBoolean
{
   /**
    * Constructor. Creates an instance of this class from a boolean value.
    *
    * @param value Boolean flag
    */
   private NumericBoolean (boolean value)
   {
      m_value = value;
   }

   /**
    * This method retrieves an instance of this class representing the
    * value passed in as an argument.
    *
    * @param value Boolean flag
    * @return Instance of NumericBoolean class
    */
   public static NumericBoolean getInstance (boolean value)
   {
      return (value == false ? FALSE : TRUE);
   }

   /**
    * This method retrieves an instance of this class representing the
    * value passed in as an argument. Note that zero maps to false,
    * and one maps to true.
    *
    * @param value flag
    * @return Instance of NumericBoolean class
    */
   public static NumericBoolean getInstance (int value)
   {
      return (value == 0 ? FALSE : TRUE);
   }

   /**
    * This method retrieves an instance of this class representing the
    * value passed in as an argument. Note that zero maps to false,
    * and one maps to true.
    *
    * @param value flag
    * @return Instance of NumericBoolean class
    */
   public static NumericBoolean getInstance (Integer value)
   {
      return (getInstance (value.intValue()));
   }

   /**
    * This method retrieves an instance of this class representing the
    * value passed in as an argument. Note that this method is expecting
    * an integer value encoded as a string, where zero maps to false,
    * and one maps to true.
    *
    * @param value flag
    * @return Instance of NumericBoolean class
    */
   public static NumericBoolean getInstance (String value)
   {
      return (getInstance (Integer.parseInt(value)));
   }

   /**
    * This method retrieves the boolean value represented by
    * an instance of this class.
    *
    * @return boolean value
    */
   public boolean booleanValue ()
   {
      return (m_value);
   }

   /**
    * This method formats a boolean value as a number.
    *
    * @return String representation of a boolean value
    */
   public String toString ()
   {
      return (m_value == true ? "1" : "0");
   }

   /**
    * Constant representing a boolean value of true.
    */
   public static final NumericBoolean TRUE = new NumericBoolean (true);

   /**
    * Constant representing a boolean value of false.
    */
   public static final NumericBoolean FALSE = new NumericBoolean (false);

   /**
    * Internal representation of a boolean value.
    */
   private boolean m_value;
}

