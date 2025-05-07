/*
 * file:       PopulatedFields.java
 * author:     Jon Iles
 * copyright:  (c) Packwood Software 2020
 * date:       18/11/2020
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

import java.util.Collection;
import java.util.EnumSet;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.mpxj.AccrueType;
import org.mpxj.Duration;
import org.mpxj.EarnedValueMethod;
import org.mpxj.FieldContainer;
import org.mpxj.FieldType;
import org.mpxj.Priority;
import org.mpxj.ProjectFile;
import org.mpxj.ProjectProperties;
import org.mpxj.Rate;
import org.mpxj.TaskMode;
import org.mpxj.TaskType;
import org.mpxj.TimeUnit;
import org.mpxj.UserDefinedField;

/**
 * Given a collection of objects containing fields, return a set representing
 * all of the fields which have a non-null value in any of the objects.
 *
 * @param <E> field enumeration
 * @param <T> object type
 */
public class PopulatedFields<E extends Enum<E> & FieldType, T extends FieldContainer>
{
   /**
    * Constructor.
    *
    * @param project parent project
    * @param fieldEnumType enumeration representing the set of fields for the parent container
    * @param userDefinedFields collection of user defined fields for the parent container
    * @param collection collection of objects containing fields
    */
   public PopulatedFields(ProjectFile project, Class<E> fieldEnumType, Collection<UserDefinedField> userDefinedFields, Collection<T> collection)
   {
      m_fields = new HashSet<>(EnumSet.allOf(fieldEnumType));
      m_fields.addAll(userDefinedFields);
      m_collection = collection;

      ProjectProperties props = project.getProjectProperties();
      m_defaultDurationUnits = props.getDefaultDurationUnits();
      m_defaultTaskType = props.getDefaultTaskType();
      m_defaultTaskEarnedValueMethod = props.getDefaultTaskEarnedValueMethod();
      m_defaultFixedCostAccrual = props.getDefaultFixedCostAccrual();
   }

   /**
    * Retrieve the set of fields populated across the collection of objects.
    *
    * @return populated fields
    */
   public Set<FieldType> getPopulatedFields()
   {
      Set<FieldType> unusedFields = new HashSet<>(m_fields);

      for (FieldContainer item : m_collection)
      {
         unusedFields.removeIf(e -> fieldIsPopulated(item, e));
      }

      Set<FieldType> usedFields = new HashSet<>(m_fields);
      usedFields.removeAll(unusedFields);

      return usedFields;
   }

   /**
    * Returns true if the field is populated with a non-default value.
    *
    * @param item field container
    * @param type field type
    * @return true if the field is populated with a non-default value
    */
   private boolean fieldIsPopulated(FieldContainer item, FieldType type)
   {
      Object value = item.getCachedValue(type);
      return value != null && fieldIsNotDefaultValue(value, type);
   }

   /**
    * Returns true if the value is non-default.
    *
    * @param value field value
    * @param type field type
    * @return true if the value is non-default
    */
   private boolean fieldIsNotDefaultValue(Object value, FieldType type)
   {
      boolean result;

      if (value instanceof Collection<?>)
      {
         return !((Collection<?>) value).isEmpty();
      }

      if (value instanceof Map<?, ?>)
      {
         return !((Map<?, ?>) value).isEmpty();
      }

      switch (type.getDataType())
      {
         case STRING:
         case NOTES:
         {
            result = !(value.toString()).isEmpty();
            break;
         }

         case NUMERIC:
         case CURRENCY:
         case PERCENTAGE:
         case UNITS:
         case INTEGER:
         case SHORT:
         {
            result = ((Number) value).doubleValue() != 0.0;
            break;
         }

         case WORK:
         case DURATION:
         {
            // Baseline durations can have string values
            if (value instanceof String)
            {
               result = !((String) value).isEmpty();
            }
            else
            {
               result = ((Duration) value).getDuration() != 0.0;
            }
            break;
         }

         case RATE:
         {
            result = ((Rate) value).getAmount() != 0.0;
            break;
         }

         case BOOLEAN:
         {
            result = ((Boolean) value).booleanValue();
            break;
         }

         case PRIORITY:
         {
            result = ((Priority) value).getValue() != Priority.MEDIUM;
            break;
         }

         case TIME_UNITS:
         {
            result = value != m_defaultDurationUnits;
            break;
         }

         case TASK_TYPE:
         {
            result = value != m_defaultTaskType;
            break;
         }

         case EARNED_VALUE_METHOD:
         {
            result = value != m_defaultTaskEarnedValueMethod;
            break;
         }

         case ACCRUE:
         {
            result = value != m_defaultFixedCostAccrual;
            break;
         }

         case TASK_MODE:
         {
            result = value != TaskMode.AUTO_SCHEDULED;
            break;
         }

         default:
         {
            result = true;
            break;
         }
      }

      return result;
   }

   private final Set<FieldType> m_fields;
   private final Collection<T> m_collection;
   private final TimeUnit m_defaultDurationUnits;
   private final TaskType m_defaultTaskType;
   private final EarnedValueMethod m_defaultTaskEarnedValueMethod;
   private final AccrueType m_defaultFixedCostAccrual;
}
