/*
 * file:       PairStore.java
 * author:     Petr Janeček
 * date:       2026-10-06
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

package org.mpxj;

import java.util.Arrays;

/**
 * Fields which are not represented by enum instances
 * (e.g. User Defined Fields, Enterprise fields, and so on)
 * are represneted in this store. As there are likely to be
 * a low number of fields represented in this store, the
 * field-value pairs are stored as adjacent array entries,
 * with a linear search used to locate the required field.
 */
class PairStore
{
   /**
    * Look up the value of the supplied FieldType.
    *
    * @param field FieldType instance
    * @return field value
    */
   public Object lookup(FieldType field)
   {
      int index = indexOf(field);
      return index < 0 ? null : m_values[index + 1];
   }

   /**
    * Set the value of the supplied FieldType in this store.
    *
    * @param field FieldType instance
    * @param value field value
    * @return previous field value being overwritten by this operation
    */
   public Object store(FieldType field, Object value)
   {
      int index = indexOf(field);
      if (index >= 0)
      {
         Object oldValue = m_values[index + 1];
         if (value == null)
         {
            Object[] remaining = new Object[m_values.length - 2];
            System.arraycopy(m_values, 0, remaining, 0, index);
            System.arraycopy(m_values, index + 2, remaining, index, remaining.length - index);
            m_values = remaining;
         }
         else
         {
            m_values[index + 1] = value;
         }
         return oldValue;
      }

      if (value != null)
      {
         m_values = Arrays.copyOf(m_values, m_values.length + 2);
         m_values[m_values.length - 2] = field;
         m_values[m_values.length - 1] = value;
      }

      return null;
   }

   /**
    * Retrieve the index of the supplied field in the array.
    * The field value will be the following array entry.
    * Returns -1 if the requested field is not currently stored.
    *
    * @param field FieldType instance
    * @return array index or -1 if the requested field is not stored
    */
   private int indexOf(FieldType field)
   {
      for (int index = 0; index < m_values.length; index += 2)
      {
         if (field.equals(m_values[index]))
         {
            return index;
         }
      }
      return -1;
   }

   /**
    * Field values.
    */
   private Object[] m_values = NO_VALUES;

   /**
    * Constant representing an empty store.
    */
   private static final Object[] NO_VALUES = new Object[0];
}
