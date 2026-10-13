/*
 * file:       SlotStore.java
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
 * Fields represented by an enumerated value are stored in
 * an array. This store is initialized using a shared
 * FieldSlots instance which maps from an enumeration
 * representing a field to an index in the array
 * where the field value is stored.
 */
class SlotStore
{
   /**
    * Constructor.
    *
    * @param slots FieldSlots instance representing the enum to array index mapping
    */
   public SlotStore(FieldSlots<?> slots)
   {
      m_slots = slots;
   }

   /**
    * Determines if a field of the supplied type is represented by this store.
    *
    * @param field FieldType instance
    * @return true if this field type is represented by this store
    */
   public boolean owns(FieldType field)
   {
      return m_slots.owns(field);
   }

   /**
    * Look up the value of the supplied FieldType.
    *
    * @param field FieldType instance
    * @return field value
    */
   public Object lookup(FieldType field)
   {
      int slot = m_slots.find(field);
      return slot < 0 || slot >= m_values.length ? null : m_values[slot];
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
      // Which slot have we allocated for this enum value
      int slot = m_slots.find(field);

      // Is this a valid index for our existing array?
      if (slot < 0 || slot >= m_values.length)
      {
         // Not a valid index, but if the value is null we can ignore it
         if (value == null)
         {
            return null;
         }

         // Do we need to claim a new slot for this field?
         if (slot < 0)
         {
            slot = m_slots.claim(field);
         }

         // Extend the array to accommodate all fields claimed for this
         // entity type. This reduces the need to resize the array later.
         m_values = Arrays.copyOf(m_values, m_slots.count());
      }

      Object oldValue = m_values[slot];
      m_values[slot] = value;
      return oldValue;
   }

   /**
    * Maps an enumerated field type to an array index.
    */
   private final FieldSlots<?> m_slots;

   /**
    * Field values.
    */
   private Object[] m_values = NO_VALUES;

   /**
    * Constant representing an empty store.
    */
   private static final Object[] NO_VALUES = new Object[0];
}
