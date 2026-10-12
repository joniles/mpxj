/*
 * file:       AbstractFieldContainer.java
 * author:     Jon Iles
 * copyright:  (c) Timephased Limited 2023
 * date:       2023-02-07
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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

import org.mpxj.listener.FieldListener;

/**
 * Implementation of common functionality for the FieldContainer interface.
 *
 * @param <T> container type
 */
public abstract class AbstractFieldContainer<T> implements FieldContainer
{
   /**
    * Constructor.
    *
    * @param slots slots for this kind of entity's own fields, shared with its siblings
    */
   AbstractFieldContainer(FieldSlots<?> slots)
   {
      m_slots = slots;
   }

   /**
    * Allow the entity to take action in response to the changed field.
    *
    * @param field updated field
    * @param oldValue old value of the updated field
    * @param newValue new value of the updated field
    */
   abstract void handleFieldChange(FieldType field, Object oldValue, Object newValue);

   /**
    * Determine if the supplied field is always calculated.
    *
    * @param field field to check
    * @return true if this field is always calculated
    */
   abstract boolean getAlwaysCalculatedField(FieldType field);

   /**
    * Retrieve the method used to calculate the value of the supplied field.
    *
    * @param field target field
    * @return calculation function, or null if the field is not calculated
    */
   abstract Function<T, Object> getCalculationMethod(FieldType field);

   /**
    * Clear any dependent fields which will need to be recalculated
    * in response to a changed field.
    *
    * @param dependencyMap ma of field dependencies
    * @param field changed field.
    */
   void clearDependentFields(Map<FieldType, List<FieldType>> dependencyMap, FieldType field)
   {
      if (!m_clearDependentFieldsEnabled)
      {
         return;
      }

      List<FieldType> dependencies = dependencyMap.get(field);
      if (dependencies == null)
      {
         return;
      }

      // Indexed: forEach would allocate a lambda on every call.
      for (int index = 0; index < dependencies.size(); index++)
      {
         set(dependencies.get(index), null);
      }
   }

   /**
    * Disable events firing when fields are updated.
    */
   public void disableEvents()
   {
      m_clearDependentFieldsEnabled = false;
   }

   /**
    * Enable events firing when fields are updated. This is the default state.
    */
   public void enableEvents()
   {
      m_clearDependentFieldsEnabled = true;
   }

   @Override public void set(FieldType field, Object value)
   {
      if (field == null)
      {
         return;
      }

      Object oldValue = store(field, value);
      if (oldValue == value)
      {
         return;
      }

      if ((oldValue == null && value != null) || (oldValue != null && value == null) || (oldValue != null && !oldValue.equals(value)))
      {
         handleFieldChange(field, oldValue, value);
         fireFieldChangeEvent(field, oldValue, value);
      }
   }

   @SuppressWarnings("unchecked") @Override public Object get(FieldType field)
   {
      if (field == null)
      {
         return null;
      }

      boolean alwaysCalculatedField = getAlwaysCalculatedField(field);
      Object result = alwaysCalculatedField ? null : lookup(field);
      if (result == null)
      {
         Function<T, Object> f = getCalculationMethod(field);
         if (f != null)
         {
            result = f.apply((T) this);
            if (result != null && !alwaysCalculatedField)
            {
               set(field, result);
            }
         }
      }

      return result;
   }

   @Override public Object getCachedValue(FieldType field)
   {
      return field == null ? null : lookup(field);
   }

   @Override public void addFieldListener(FieldListener listener)
   {
      if (m_listeners == null)
      {
         m_listeners = new ArrayList<>();
      }
      m_listeners.add(listener);
   }

   @Override public void removeFieldListener(FieldListener listener)
   {
      if (m_listeners != null)
      {
         m_listeners.remove(listener);
      }
   }

   /**
    * Send a change event to any external listeners.
    *
    * @param field field changed
    * @param oldValue old field value
    * @param newValue new field value
    */
   private void fireFieldChangeEvent(FieldType field, Object oldValue, Object newValue)
   {
      if (m_listeners != null)
      {
         m_listeners.forEach(l -> l.fieldChange(this, field, oldValue, newValue));
      }
   }

   /**
    * Retrieve the value stored for a field.
    *
    * @param field field
    * @return stored value, or null if none is stored
    */
   private Object lookup(FieldType field)
   {
      if (m_slots.owns(field))
      {
         int slot = m_slots.find(field);
         return slot < 0 || slot >= m_values.length ? null : m_values[slot];
      }

      int index = indexOfOther(field);
      return index < 0 ? null : m_otherValues[index + 1];
   }

   /**
    * Store the value of a field.
    *
    * @param field field
    * @param value new value, or null
    * @return the value previously stored
    */
   private Object store(FieldType field, Object value)
   {
      // Is the supplied field an enum value?
      if (m_slots.owns(field))
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

      int index = indexOfOther(field);
      if (index >= 0)
      {
         Object oldValue = m_otherValues[index + 1];
         if (value == null)
         {
            Object[] remaining = new Object[m_otherValues.length - 2];
            System.arraycopy(m_otherValues, 0, remaining, 0, index);
            System.arraycopy(m_otherValues, index + 2, remaining, index, remaining.length - index);
            m_otherValues = remaining;
         }
         else
         {
            m_otherValues[index + 1] = value;
         }
         return oldValue;
      }

      if (value != null)
      {
         m_otherValues = Arrays.copyOf(m_otherValues, m_otherValues.length + 2);
         m_otherValues[m_otherValues.length - 2] = field;
         m_otherValues[m_otherValues.length - 1] = value;
      }

      return null;
   }

   /**
    * Find where a field other than this kind's own is stored.
    *
    * @param field field
    * @return index of the field in m_otherValues, its value following it, or -1 if it is not stored
    */
   private int indexOfOther(FieldType field)
   {
      for (int index = 0; index < m_otherValues.length; index += 2)
      {
         if (field.equals(m_otherValues[index]))
         {
            return index;
         }
      }
      return -1;
   }

   private boolean m_clearDependentFieldsEnabled = true;

   private List<FieldListener> m_listeners;

   /**
    * Maps an enumerated field type to an array index.
    */
   private final FieldSlots<?> m_slots;

   /**
    * Enumerated field values.
    */
   private Object[] m_values = NO_VALUES;

   /**
    * Non-enumerated field type and value pairs.
    * Each entity is likely to hold a small number of these
    * values so a linear search through them costs less
    * than the memory overhead required by a Map.
    */
   private Object[] m_otherValues = NO_VALUES;

   private static final Object[] NO_VALUES = new Object[0];
}
