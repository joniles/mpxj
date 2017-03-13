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

package net.sf.mpxj.common;

import java.util.Locale;

import net.sf.mpxj.AssignmentField;
import net.sf.mpxj.ConstraintField;
import net.sf.mpxj.DataType;
import net.sf.mpxj.FieldType;
import net.sf.mpxj.FieldTypeClass;
import net.sf.mpxj.ResourceField;
import net.sf.mpxj.TaskField;

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
      switch (type.getFieldTypeClass())
      {
         case TASK:
         {
            result = MPPTaskField.TASK_FIELD_BASE | MPPTaskField.getID((TaskField) type);
            break;
         }

         case RESOURCE:
         {
            result = MPPResourceField.RESOURCE_FIELD_BASE | MPPResourceField.getID((ResourceField) type);
            break;
         }

         case ASSIGNMENT:
         {
            result = MPPAssignmentField.ASSIGNMENT_FIELD_BASE | MPPAssignmentField.getID((AssignmentField) type);
            break;
         }

         default:
         {
            result = -1;
         }

      }
      return result;
   }

   /**
    * Retrieve a FieldType instance based on an ID value from
    * an MPP9 or MPP12 file.
    *
    * @param fieldID field ID
    * @return FieldType instance
    */
   public static final FieldType getInstance(int fieldID)
   {
      FieldType result;
      int prefix = fieldID & 0xFFFF0000;
      int index = fieldID & 0x0000FFFF;

      switch (prefix)
      {
         case MPPTaskField.TASK_FIELD_BASE:
         {
            result = MPPTaskField.getInstance(index);
            if (result == null)
            {
               result = getPlaceholder(TaskField.class, index);
            }
            break;
         }

         case MPPResourceField.RESOURCE_FIELD_BASE:
         {
            result = MPPResourceField.getInstance(index);
            if (result == null)
            {
               result = getPlaceholder(ResourceField.class, index);
            }
            break;
         }

         case MPPAssignmentField.ASSIGNMENT_FIELD_BASE:
         {
            result = MPPAssignmentField.getInstance(index);
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

         default:
         {
            result = getPlaceholder(null, index);
            break;
         }
      }

      return result;
   }

   /**
    * Retrieve a FieldType instance based on an ID value from
    * an MPP14 field, mapping the START_TEXT, FINISH_TEXT, and DURATION_TEXT
    * values to START, FINISH, and DURATION respectively.
    *
    * @param fieldID field ID
    * @return FieldType instance
    */
   public static final FieldType getInstance14(int fieldID)
   {
      FieldType result;
      int prefix = fieldID & 0xFFFF0000;
      int index = fieldID & 0x0000FFFF;

      switch (prefix)
      {
         case MPPTaskField.TASK_FIELD_BASE:
         {
            result = MPPTaskField14.getInstance(index);
            if (result == null)
            {
               result = getPlaceholder(TaskField.class, index);
            }
            break;
         }

         case MPPResourceField.RESOURCE_FIELD_BASE:
         {
            result = MPPResourceField14.getInstance(index);
            if (result == null)
            {
               result = getPlaceholder(ResourceField.class, index);
            }
            break;
         }

         case MPPAssignmentField.ASSIGNMENT_FIELD_BASE:
         {
            result = MPPAssignmentField14.getInstance(index);
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
            return "Unknown " + (type == null ? "" : type.getSimpleName() + "(" + fieldID + ")");
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
    * In some circumstances MS Project refers to the text version of a field (e.g. Start Text rather than Star) when we
    * actually need to process the non-text version of the field. This method performs that mapping.
    *
    * @param field field to mapped
    * @return mapped field
    */
   public static FieldType mapTextFields(FieldType field)
   {
      if (field != null && field.getFieldTypeClass() == FieldTypeClass.TASK)
      {
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
      }

      return field;
   }
}
