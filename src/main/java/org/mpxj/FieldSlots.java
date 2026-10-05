/*
 * file:       FieldSlots.java
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
 * Assigns each field of one kind of entity a slot in that entity's array of values, shared by every entity of that
 * kind which reads its fields through the same instance - typically every task, or every resource assignment, of a
 * ProjectContext.
 *
 * <p>A schedule file states only a small part of the fields an entity could hold - a P6 task around 70 of the 1,009
 * task fields, the same 70 on nearly every task - so slots are handed out in the order fields are first set, rather
 * than one per enum constant. An entity's array then only needs to reach the highest slot it uses.</p>
 *
 * <p>Only the constants of the enum this instance was created for are given slots. Any other field type, a
 * user defined field for example, is stored by the entity itself.</p>
 *
 * <p>Like the rest of a ProjectContext, an instance is not thread safe.</p>
 *
 * @param <E> field enum whose constants are given slots
 */
final class FieldSlots<E extends Enum<E> & FieldType>
{
   /**
    * Constructor.
    *
    * @param fieldType enum whose constants are given slots
    */
   FieldSlots(Class<E> fieldType)
   {
      m_fieldType = fieldType;
      m_slots = new int[fieldType.getEnumConstants().length];
      Arrays.fill(m_slots, -1);
   }

   /**
    * Determine if the supplied field is one of those given slots by this instance.
    *
    * @param field field to test
    * @return true if the field is given a slot here
    */
   boolean owns(FieldType field)
   {
      return m_fieldType.isInstance(field);
   }

   /**
    * Retrieve the slot of a field, if any entity has been given a value for it.
    *
    * @param field field owned by this instance
    * @return slot index, or -1 if this field has never been set
    */
   int find(FieldType field)
   {
      return m_slots[m_fieldType.cast(field).ordinal()];
   }

   /**
    * Retrieve the slot of a field, giving it the next free slot if it has none yet.
    *
    * @param field field owned by this instance
    * @return slot index
    */
   int claim(FieldType field)
   {
      int ordinal = m_fieldType.cast(field).ordinal();
      if (m_slots[ordinal] < 0)
      {
         m_slots[ordinal] = m_count++;
      }
      return m_slots[ordinal];
   }

   /**
    * Retrieve the number of slots handed out so far. An entity growing its array
    * grows it to this length, so that it does not have to grow again for each new field.
    *
    * @return number of slots
    */
   int count()
   {
      return m_count;
   }

   private final Class<E> m_fieldType;

   /**
    * Slot by enum ordinal, -1 for a field without one.
    */
   private final int[] m_slots;
   private int m_count;
}
