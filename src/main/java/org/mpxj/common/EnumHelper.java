/*
 * file:       EnumHelper.java
 * author:     Jon Iles
 * copyright:  (c) Packwood Software 2010
 * date:       31/03/2010
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

package org.mpxj.common;

import java.lang.reflect.Array;
import java.util.EnumSet;

import org.mpxj.MpxjEnum;

/**
 * Utility method for working with enumerations.
 */
public final class EnumHelper
{
   /**
    * Creates a lookup array based on the "value" associated with an MpxjEnum.
    *
    * @param <E> target enumeration
    * @param c enumeration class
    * @return lookup array
    */
   public static final <E extends Enum<E>> E[] createTypeArray(Class<E> c)
   {
      return createTypeArray(c, 0);
   }

   /**
    * Creates a lookup array based on the "value" associated with an MpxjEnum.
    *
    * @param <E> target enumeration
    * @param c enumeration class
    * @param arraySizeOffset offset to apply to the array size
    * @return lookup array
    */
   @SuppressWarnings(
   {
      "unchecked"
   }) public static final <E extends Enum<E>> E[] createTypeArray(Class<E> c, int arraySizeOffset)
   {
      EnumSet<E> set = EnumSet.allOf(c);
      E[] array = (E[]) Array.newInstance(c, set.size() + arraySizeOffset);

      for (E e : set)
      {
         int index = ((MpxjEnum) e).getValue();
         if (index >= 0)
         {
            array[index] = e;
         }
      }
      return array;
   }
}
