/*
 * file:       MPPAssignmentField.java
 * author:     Jon Iles
 * copyright:  (c) Packwood Software 2011
 * date:       14/04/2011
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

package net.sf.mpxj;

/**
 * Utility class used to map between the integer values held in MS Project
 * to represent an assignment field, and the enumerated type used to represent
 * assignment fields in MPXJ.
 */
public final class MPPAssignmentField
{
   /**
    * Retrieve an instance of the AssignmentField class based on the data read from an
    * MS Project file.
    *
    * @param value value from an MS Project file
    * @return AssignmentField instance
    */
   public static AssignmentField getInstance(int value)
   {
      AssignmentField result = null;

      if (value >= 0 && value < FIELD_ARRAY.length)
      {
         result = FIELD_ARRAY[value];
      }

      return (result);
   }

   /**
    * Retrieve the ID of a field, as used by MS Project.
    * 
    * @param value field instance
    * @return field ID
    */
   public static int getID(AssignmentField value)
   {
      return (ID_ARRAY[value.getValue()]);
   }

   public static final int MAX_VALUE = 271;

   private static final AssignmentField[] FIELD_ARRAY = new AssignmentField[MAX_VALUE];

   static
   {
      FIELD_ARRAY[0] = AssignmentField.UNIQUE_ID;
      FIELD_ARRAY[1] = AssignmentField.TASK_UNIQUE_ID;
      FIELD_ARRAY[2] = AssignmentField.RESOURCE_UNIQUE_ID;
      FIELD_ARRAY[20] = AssignmentField.START;
      FIELD_ARRAY[21] = AssignmentField.FINISH;
      FIELD_ARRAY[7] = AssignmentField.ASSIGNMENT_UNITS;
      FIELD_ARRAY[49] = AssignmentField.PLANNED_WORK_DATA;
      FIELD_ARRAY[50] = AssignmentField.COMPLETE_WORK_DATA;
      FIELD_ARRAY[12] = AssignmentField.REMAINING_WORK;
      FIELD_ARRAY[270] = AssignmentField.VARIABLE_RATE_UNITS;
      FIELD_ARRAY[28] = AssignmentField.ACTUAL_COST;
      FIELD_ARRAY[10] = AssignmentField.ACTUAL_WORK;
      FIELD_ARRAY[26] = AssignmentField.COST;
      FIELD_ARRAY[25] = AssignmentField.ASSIGNMENT_DELAY;
      FIELD_ARRAY[145] = AssignmentField.LEVELING_DELAY;
      FIELD_ARRAY[55] = AssignmentField.LEVELING_DELAY_UNITS;
      FIELD_ARRAY[8] = AssignmentField.WORK;
      FIELD_ARRAY[32] = AssignmentField.BASELINE_COST;
      FIELD_ARRAY[147] = AssignmentField.BASELINE_FINISH;
      FIELD_ARRAY[146] = AssignmentField.BASELINE_START;
      FIELD_ARRAY[16] = AssignmentField.BASELINE_WORK;
   }

   private static final int[] ID_ARRAY = new int[AssignmentField.MAX_VALUE];
   static
   {
      for (int loop = 0; loop < FIELD_ARRAY.length; loop++)
      {
         AssignmentField assignmentField = FIELD_ARRAY[loop];
         if (assignmentField != null)
         {
            ID_ARRAY[assignmentField.getValue()] = loop;
         }
      }
   }

   public static final int ASSIGNMENT_FIELD_BASE = 0x0F400000;
}
