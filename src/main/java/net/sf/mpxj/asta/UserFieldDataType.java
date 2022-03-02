/*
 * file:       UserFieldDataType.java
 * author:     Jon Iles
 * copyright:  (c) Packwood Software 2021
 * date:       26/05/2021
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

package net.sf.mpxj.asta;

import java.util.HashMap;
import java.util.Map;

import net.sf.mpxj.FieldType;

/**
 * Factory used to retrieve the next available field type appropriate
 * for storing an Asta custom field.

 * @param <E> field type enum
 */
class UserFieldDataType<E extends Enum<E> & FieldType>
{
   /**
    * Constructor.
    *
    * @param clazz field type class
    */
   public UserFieldDataType(Class<E> clazz)
   {
      m_class = clazz;
   }

   /**
    * Retrieve the next available field.
    *
    * @param dataType Asta data type
    * @return FieldType instance or null if no field is available
    */
   public E nextField(Integer dataType)
   {
      try
      {
         String customFieldName = TYPE_MAP.get(dataType);
         int index = m_counters.compute(customFieldName, (k, v) -> v == null ? Integer.valueOf(1) : Integer.valueOf(v.intValue() + 1)).intValue();
         return Enum.valueOf(m_class, customFieldName + index);
      }

      catch (Exception ex)
      {
         // If we've run out of a particular custom field type, we'll end up here.
         // We'll return null to ignore this type.
         return null;
      }
   }

   private final Class<E> m_class;
   private final Map<String, Integer> m_counters = new HashMap<>();

   private static final Map<Integer, String> TYPE_MAP = new HashMap<>();
   static
   {
      TYPE_MAP.put(Integer.valueOf(0), "FLAG"); // Boolean
      TYPE_MAP.put(Integer.valueOf(6), "NUMBER"); // Integer
      TYPE_MAP.put(Integer.valueOf(8), "NUMBER"); // Float
      TYPE_MAP.put(Integer.valueOf(9), "TEXT"); // String
      TYPE_MAP.put(Integer.valueOf(13), "DATE"); // Date
      TYPE_MAP.put(Integer.valueOf(15), "DURATION"); // Duration
      TYPE_MAP.put(Integer.valueOf(24), "TEXT"); // URL
   }
}
