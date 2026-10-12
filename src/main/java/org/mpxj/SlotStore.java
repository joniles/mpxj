package org.mpxj;

import java.util.Arrays;

class SlotStore
{
   public SlotStore(FieldSlots<?> slots)
   {
      m_slots = slots;
   }

   public boolean owns(FieldType field)
   {
      return m_slots.owns(field);
   }

   public Object lookup(FieldType field)
   {
      int slot = m_slots.find(field);
      return slot < 0 || slot >= m_values.length ? null : m_values[slot];
   }

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
    * Enumerated field values.
    */
   private Object[] m_values = NO_VALUES;


   private static final Object[] NO_VALUES = new Object[0];
}
