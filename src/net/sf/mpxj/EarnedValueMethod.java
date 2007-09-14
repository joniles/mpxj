/*
 * file:       EarnedValueMethod.java
 * author:     Jon Iles
 * copyright:  (c) Tapster Rock Limited 2002-2003
 * date:       02/12/2004
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

import net.sf.mpxj.utility.MpxjEnum;

/**
 * Instances of this class represent enumerated earned value method values.
 */
public final class EarnedValueMethod implements MpxjEnum
{
   /**
    * Private constructor.
    *
    * @param value earned value method value
    */
   private EarnedValueMethod (int value)
   {
      m_value = value;
   }

   /**
    * Retrieves the int representation of the earned value method.
    *
    * @return earned value method value
    */
   public int getValue ()
   {
      return (m_value);
   }

   /**
    * Retrieve an EarnedValueMethod instance representing the supplied value.
    *
    * @param value earned value method
    * @return EarnedValueMethod instance
    */
   public static EarnedValueMethod getInstance (int value)
   {
      EarnedValueMethod result;

      switch (value)
      {
         case PERCENT_COMPLETE_VALUE:
         {
            result = PERCENT_COMPLETE;
            break;
         }

         default:
         case PHYSICAL_PERCENT_COMPLETE_VALUE:
         {
            result = PHYSICAL_PERCENT_COMPLETE;
            break;
         }
      }

      return (result);
   }



   private int m_value;

   /**
    * Constant representing Percent Complete.
    */
   public static final int PERCENT_COMPLETE_VALUE = 0;

   /**
    * Constant representing Physical Percent Complete.
    */
   public static final int PHYSICAL_PERCENT_COMPLETE_VALUE = 1;

   /**
    * Constant representing Percent Complete.
    */
   public static final EarnedValueMethod PERCENT_COMPLETE = new EarnedValueMethod(PERCENT_COMPLETE_VALUE);

   /**
    * Constant representing Physical Percent Complete.
    */
   public static final EarnedValueMethod PHYSICAL_PERCENT_COMPLETE = new EarnedValueMethod(PHYSICAL_PERCENT_COMPLETE_VALUE);
}
