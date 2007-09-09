/*
 * file:       CurrencySymbolPosition.java
 * author:     Jon Iles
 * copyright:  (c) Tapster Rock Limited 2002-2003
 * date:       07/01/2005
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

package net.sf.mpxj;

/**
 * Instances of this class represent enumerated currency symbol position values.
 */
public final class CurrencySymbolPosition
{
   /**
    * Private constructor.
    *
    * @param value currency symbol position value
    */
   private CurrencySymbolPosition (int value)
   {
      m_value = value;
   }

   /**
    * Retrieves the int representation of the currency symbol position value.
    *
    * @return currency symbol position value
    */
   public int getValue ()
   {
      return (m_value);
   }

   /**
    * Retrieve a CurrencySymbolPosition instance representing the supplied value.
    *
    * @param value currency symbol position value
    * @return CurrencySymbolPosition instance
    */
   public static CurrencySymbolPosition getInstance (int value)
   {
      CurrencySymbolPosition result;

      switch (value)
      {
         case AFTER_VALUE:
         {
            result = AFTER;
            break;
         }

         case AFTER_WITH_SPACE_VALUE:
         {
            result = AFTER_WITH_SPACE;
            break;
         }

         case BEFORE_WITH_SPACE_VALUE:
         {
            result = BEFORE_WITH_SPACE;
            break;
         }

         default:
         case BEFORE_VALUE:
         {
            result = BEFORE;
            break;
         }
      }

      return (result);
   }

   /**
    * Returns a string representation of the currency symbol position type
    * to be used as part of an MPX file.
    *
    * @return string representation
    */
   @Override public String toString ()
   {
      return (Integer.toString(m_value));
   }

   private int m_value;

   /**
    * Constant representing symbol position after.
    */
   public static final int AFTER_VALUE = 0;

   /**
    * Constant representing symbol position before.
    */
   public static final int BEFORE_VALUE = 1;

   /**
    * Constant representing symbol position after with space.
    */
   public static final int AFTER_WITH_SPACE_VALUE = 2;

   /**
    * Constant representing symbol position before with space.
    */
   public static final int BEFORE_WITH_SPACE_VALUE = 3;

   /**
    * Constant representing symbol position after.
    */
   public static final CurrencySymbolPosition AFTER = new CurrencySymbolPosition(AFTER_VALUE);

   /**
    * Constant representing symbol position before.
    */
   public static final CurrencySymbolPosition BEFORE = new CurrencySymbolPosition(BEFORE_VALUE);

   /**
    * Constant representing symbol position after with space.
    */
   public static final CurrencySymbolPosition AFTER_WITH_SPACE = new CurrencySymbolPosition(AFTER_WITH_SPACE_VALUE);

   /**
    * Constant representing symbol position before with space.
    */
   public static final CurrencySymbolPosition BEFORE_WITH_SPACE = new CurrencySymbolPosition(BEFORE_WITH_SPACE_VALUE);
}
