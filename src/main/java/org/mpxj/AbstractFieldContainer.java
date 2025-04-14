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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

import org.mpxj.listener.FieldListener;

/**
 * Implementation of common functionality for the FieldContainer interface.
 *
 * @param <T> container type
 */
public abstract class AbstractFieldContainer<T> extends ProjectEntity implements FieldContainer
{
   /**
    * Constructor.
    *
    * @param file Parent file
    */
   protected AbstractFieldContainer(ProjectFile file)
   {
      super(file);
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

      dependencies.forEach(f -> set(f, null));
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

      Object oldValue = value == null ? m_fields.remove(field) : m_fields.put(field, value);
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
      Object result = alwaysCalculatedField ? null : m_fields.get(field);
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
      return m_fields.get(field);
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

   private boolean m_clearDependentFieldsEnabled = true;
   private final Map<FieldType, Object> m_fields = new HashMap<>();
   private List<FieldListener> m_listeners;
}
