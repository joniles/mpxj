/*
 * file:       UserFieldCounters.java
 * author:     Mario Fuentes
 * copyright:  (c) Packwood Software 2013
 * date:       22/03/2010
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

package net.sf.mpxj.primavera;

import java.util.HashMap;
import java.util.Map;

import net.sf.mpxj.FieldType;
import net.sf.mpxj.common.NumberHelper;

/**
 * Simple container holding counters used to generate field names
 * for user defined field types.
 */

class UserFieldCounters
{
   /**
    * Constructor.
    */
   public UserFieldCounters()
   {
      for (UserFieldDataType type : UserFieldDataType.values())
      {
         m_names[type.ordinal()] = type.getDefaultFieldNames();
      }
   }

   /**
    * Allow the caller to override the default field name assigned
    * to a user defined data type.
    *
    * @param type target type
    * @param fieldNames field names overriding the default
    */
   public void setFieldNamesForType(UserFieldDataType type, String[] fieldNames)
   {
      m_names[type.ordinal()] = fieldNames;
   }

   /**
    * Generate the next available field for a user defined field.
    *
    * @param <E> field type class
    * @param clazz class of the desired field enum
    * @param type user defined field type.
    * @return field of specified type
    */
   public <E extends Enum<E> & FieldType> E nextField(Class<E> clazz, UserFieldDataType type)
   {
      for (String name : m_names[type.ordinal()])
      {
         int i = NumberHelper.getInt(m_counters.get(name)) + 1;
         try
         {
            E e = Enum.valueOf(clazz, name + i);
            m_counters.put(name, Integer.valueOf(i));
            return e;
         }
         catch (IllegalArgumentException ex)
         {
            // try the next name
         }
      }

      // no more fields available
      throw new IllegalArgumentException("No fields for type " + type + " available");
   }

   /**
    * Reset the counters ready to process a new project.
    */
   public void reset()
   {
      m_counters.clear();
   }

   private final Map<String, Integer> m_counters = new HashMap<String, Integer>();
   private final String[][] m_names = new String[UserFieldDataType.values().length][];
}
