/*
 * file:       DateOrder.java
 * author:     Jon Iles
 * copyright:  (c) Tapster Rock Limited 2002-2003
 * date:       04/01/2005
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
 * Instances of this class represent enumerated date order values.
 */
public final class DateOrder
{
   /**
    * Private constructor.
    *
    * @param value date order value
    */
   private DateOrder (int value)
   {
      m_value = value;
   }

   /**
    * Retrieves the int representation of the date order.
    *
    * @return date order value
    */
   public int getValue ()
   {
      return (m_value);
   }

   /**
    * Retrieve a DateOrder instance representing the supplied value.
    *
    * @param value date order value
    * @return DateOrder instance
    */
   public static DateOrder getInstance (int value)
   {
      DateOrder result;

      switch (value)
      {
         case DMY_VALUE:
         {
            result = DMY;
            break;
         }

         case YMD_VALUE:
         {
            result = YMD;
            break;
         }

         default:
         case MDY_VALUE:
         {
            result = MDY;
            break;
         }
      }

      return (result);
   }

   /**
    * Returns a string representation of the date order type
    * to be used as part of an MPX file.
    *
    * @return string representation
    */
   public String toString ()
   {
      return (Integer.toString(m_value));
   }


   private int m_value;

   /**
    * Constant representing MDY.
    */
   public static final int MDY_VALUE = 0;

   /**
    * Constant representing DMY.
    */
   public static final int DMY_VALUE = 1;

   /**
    * Constant representing YMD.
    */
   public static final int YMD_VALUE = 2;


   /**
    * Constant representing MDY.
    */
   public static final DateOrder MDY = new DateOrder(MDY_VALUE);

   /**
    * Constant representing DMY.
    */
   public static final DateOrder DMY = new DateOrder(DMY_VALUE);

   /**
    * Constant representing YMD.
    */
   public static final DateOrder YMD = new DateOrder(YMD_VALUE);

}
