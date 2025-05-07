/*
 * file:       FieldTypeHelper.java
 * author:     Jon Iles
 * copyright:  (c) Packwood Software 2011
 * date:       2011-05-17
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

import java.util.Locale;

import org.mpxj.AssignmentField;
import org.mpxj.ConstraintField;
import org.mpxj.DataType;
import org.mpxj.Duration;
import org.mpxj.FieldType;
import org.mpxj.FieldTypeClass;
import org.mpxj.Priority;
import org.mpxj.ProjectField;
import org.mpxj.ProjectFile;
import org.mpxj.Rate;
import org.mpxj.ResourceField;
import org.mpxj.TaskField;

/**
 * Utility class containing methods relating to the FieldType class.
 */
public final class FieldTypeHelper
{
   /**
    * Retrieve an MPP9/MPP12 field ID based on an MPXJ FieldType instance.
    *
    * @param type FieldType instance
    * @return field ID
    */
   public static int getFieldID(FieldType type)
   {
      int result;
      if (type == null)
      {
         result = -1;
      }
      else
      {
         switch (type.getFieldTypeClass())
         {
            case TASK:
            {
               result = MPPTaskField.TASK_FIELD_BASE | MPPTaskField.getID(type);
               break;
            }

            case RESOURCE:
            {
               result = MPPResourceField.RESOURCE_FIELD_BASE | MPPResourceField.getID(type);
               break;
            }

            case ASSIGNMENT:
            {
               result = MPPAssignmentField.ASSIGNMENT_FIELD_BASE | MPPAssignmentField.getID(type);
               break;
            }

            case PROJECT:
            {
               result = MPPProjectField.PROJECT_FIELD_BASE | MPPProjectField.getID(type);
               break;
            }

            default:
            {
               result = -1;
            }
         }
      }
      return result;
   }

   /**
    * Retrieve a FieldType instance based on an ID value from
    * an MPP9, MPP12 or MPP14 file.
    *
    * @param project parent project
    * @param fieldID field ID
    * @return FieldType instance
    */
   public static final FieldType getInstance(ProjectFile project, int fieldID)
   {
      return getInstance(project, fieldID, DataType.CUSTOM);
   }

   /**
    * Retrieve a FieldType instance based on an ID value from
    * an MPP9, MPP12 or MPP14 file.
    *
    * @param project parent project
    * @param fieldID field ID
    * @param customFieldDataType custom field data type
    * @return FieldType instance
    */
   public static final FieldType getInstance(ProjectFile project, int fieldID, DataType customFieldDataType)
   {
      if (fieldID == -1)
      {
         return null;
      }

      FieldType result;
      int prefix = fieldID & 0xFFFF0000;
      int index = fieldID & 0x0000FFFF;

      switch (prefix)
      {
         case MPPTaskField.TASK_FIELD_BASE:
         {
            result = MPPTaskField.getInstance(project, index, customFieldDataType);
            if (result == null)
            {
               result = getPlaceholder(TaskField.class, index);
            }
            break;
         }

         case MPPResourceField.RESOURCE_FIELD_BASE:
         {
            result = MPPResourceField.getInstance(project, index, customFieldDataType);
            if (result == null)
            {
               result = getPlaceholder(ResourceField.class, index);
            }
            break;
         }

         case MPPAssignmentField.ASSIGNMENT_FIELD_BASE:
         {
            result = MPPAssignmentField.getInstance(project, index, customFieldDataType);
            if (result == null)
            {
               result = getPlaceholder(AssignmentField.class, index);
            }
            break;
         }

         case MPPConstraintField.CONSTRAINT_FIELD_BASE:
         {
            result = MPPConstraintField.getInstance(index);
            if (result == null)
            {
               result = getPlaceholder(ConstraintField.class, index);
            }
            break;
         }

         case MPPProjectField.PROJECT_FIELD_BASE:
         {
            result = MPPProjectField.getInstance(project, index, customFieldDataType);
            if (result == null)
            {
               result = getPlaceholder(ProjectField.class, index);
            }
            break;
         }

         default:
         {
            result = getPlaceholder(null, index);
            break;
         }
      }

      return result;
   }

   /**
    * Generate a placeholder for an unknown type.
    *
    * @param type expected type
    * @param fieldID field ID
    * @return placeholder
    */
   private static FieldType getPlaceholder(final Class<?> type, final int fieldID)
   {
      return new FieldType()
      {
         @Override public FieldTypeClass getFieldTypeClass()
         {
            return FieldTypeClass.UNKNOWN;
         }

         @Override public String name()
         {
            return "UNKNOWN";
         }

         @Override public int getValue()
         {
            return fieldID;
         }

         @Override public String getName()
         {
            return "Unknown" + (type == null ? "" : " " + type.getSimpleName() + "(" + fieldID + ")");
         }

         @Override public String getName(Locale locale)
         {
            return getName();
         }

         @Override public DataType getDataType()
         {
            return null;
         }

         @Override public FieldType getUnitsType()
         {
            return null;
         }

         @Override public String toString()
         {
            return getName();
         }
      };
   }

   /**
    * In some circumstances MS Project refers to the text version of a field (e.g. Start Text rather than Start) when we
    * actually need to process the non-text version of the field. This method performs that mapping.
    *
    * @param field field to mapped
    * @return mapped field
    */
   public static FieldType mapTextFields(FieldType field)
   {
      if (!(field instanceof TaskField))
      {
         return field;
      }

      TaskField taskField = (TaskField) field;
      switch (taskField)
      {
         case START_TEXT:
         {
            field = TaskField.START;
            break;
         }

         case FINISH_TEXT:
         {
            field = TaskField.FINISH;
            break;
         }

         case DURATION_TEXT:
         {
            field = TaskField.DURATION;
            break;
         }

         default:
         {
            break;
         }
      }

      return field;
   }

   /**
    * Determines if this value is the default value for the given field type.
    *
    * @param type field type
    * @param value value
    * @return true if the value is not default
    */
   public static final boolean valueIsNotDefault(FieldType type, Object value)
   {
      boolean result = true;

      if (value == null)
      {
         result = false;
      }
      else
      {
         DataType dataType = type.getDataType();
         switch (dataType)
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

            default:
            {
               break;
            }
         }
      }

      return result;
   }
}
