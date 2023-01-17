/*
 * file:       MPPTaskField14.java
 * author:     Jon Iles
 * copyright:  (c) Packwood Software 2010
 * date:       09/03/2010
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

import java.util.Arrays;

import net.sf.mpxj.TaskField;

/**
 * Utility class used to map between the integer values held in MS Project
 * to represent a task field, and the enumerated type used to represent
 * task fields in MPXJ.
 */
public class MPPTaskField14
{
   /**
    * Retrieve an instance of the TaskField class based on the data read from an
    * MS Project file.
    *
    * @param value value from an MS Project file
    * @return TaskField instance
    */
   public static TaskField getInstance(int value)
   {
      TaskField result = null;

      if (value >= 0 && value < FIELD_ARRAY.length)
      {
         result = FIELD_ARRAY[value];
      }
      else
      {
         if ((value & 0x8000) != 0)
         {
            int baseValue = TaskField.ENTERPRISE_CUSTOM_FIELD1.getValue();
            int id = baseValue + (value & 0xFFF);
            result = TaskField.getInstance(id);
         }
      }

      return (result);
   }

   /**
    * Retrieve the ID of a field, as used by MS Project.
    *
    * @param value field instance
    * @return field ID
    */
   public static int getID(TaskField value)
   {
      int result;

      if (MPPTaskField.ENTERPRISE_CUSTOM_FIELDS.contains(value))
      {
         int baseValue = TaskField.ENTERPRISE_CUSTOM_FIELD1.getValue();
         int id = value.getValue() - baseValue;
         result = 0x8000 + id;
      }
      else
      {
         result = ID_ARRAY[value.getValue()];
      }
      return result;
   }


   private static final TaskField[] FIELD_ARRAY = new TaskField[MPPTaskField.FIELD_ARRAY.length];

   static
   {
      System.arraycopy(MPPTaskField.FIELD_ARRAY, 0, FIELD_ARRAY, 0, MPPTaskField.FIELD_ARRAY.length);

      FIELD_ARRAY[29] = TaskField.SCHEDULED_DURATION;
      FIELD_ARRAY[35] = TaskField.SCHEDULED_START;
      FIELD_ARRAY[36] = TaskField.SCHEDULED_FINISH;

      FIELD_ARRAY[43] = TaskField.BASELINE_ESTIMATED_START;
      FIELD_ARRAY[44] = TaskField.BASELINE_ESTIMATED_FINISH;
      FIELD_ARRAY[27] = TaskField.BASELINE_ESTIMATED_DURATION;

      FIELD_ARRAY[482] = TaskField.BASELINE1_ESTIMATED_START;
      FIELD_ARRAY[483] = TaskField.BASELINE1_ESTIMATED_FINISH;
      FIELD_ARRAY[487] = TaskField.BASELINE1_ESTIMATED_DURATION;

      FIELD_ARRAY[493] = TaskField.BASELINE2_ESTIMATED_START;
      FIELD_ARRAY[494] = TaskField.BASELINE2_ESTIMATED_FINISH;
      FIELD_ARRAY[498] = TaskField.BASELINE2_ESTIMATED_DURATION;

      FIELD_ARRAY[504] = TaskField.BASELINE3_ESTIMATED_START;
      FIELD_ARRAY[505] = TaskField.BASELINE3_ESTIMATED_FINISH;
      FIELD_ARRAY[509] = TaskField.BASELINE3_ESTIMATED_DURATION;

      FIELD_ARRAY[515] = TaskField.BASELINE4_ESTIMATED_START;
      FIELD_ARRAY[516] = TaskField.BASELINE4_ESTIMATED_FINISH;
      FIELD_ARRAY[520] = TaskField.BASELINE4_ESTIMATED_DURATION;

      FIELD_ARRAY[526] = TaskField.BASELINE5_ESTIMATED_START;
      FIELD_ARRAY[527] = TaskField.BASELINE5_ESTIMATED_FINISH;
      FIELD_ARRAY[531] = TaskField.BASELINE5_ESTIMATED_DURATION;

      FIELD_ARRAY[544] = TaskField.BASELINE6_ESTIMATED_START;
      FIELD_ARRAY[545] = TaskField.BASELINE6_ESTIMATED_FINISH;
      FIELD_ARRAY[549] = TaskField.BASELINE6_ESTIMATED_DURATION;

      FIELD_ARRAY[555] = TaskField.BASELINE7_ESTIMATED_START;
      FIELD_ARRAY[556] = TaskField.BASELINE7_ESTIMATED_FINISH;
      FIELD_ARRAY[560] = TaskField.BASELINE7_ESTIMATED_DURATION;

      FIELD_ARRAY[566] = TaskField.BASELINE8_ESTIMATED_START;
      FIELD_ARRAY[567] = TaskField.BASELINE8_ESTIMATED_FINISH;
      FIELD_ARRAY[571] = TaskField.BASELINE8_ESTIMATED_DURATION;

      FIELD_ARRAY[577] = TaskField.BASELINE9_ESTIMATED_START;
      FIELD_ARRAY[578] = TaskField.BASELINE9_ESTIMATED_FINISH;
      FIELD_ARRAY[582] = TaskField.BASELINE9_ESTIMATED_DURATION;

      FIELD_ARRAY[588] = TaskField.BASELINE10_ESTIMATED_START;
      FIELD_ARRAY[589] = TaskField.BASELINE10_ESTIMATED_FINISH;
      FIELD_ARRAY[593] = TaskField.BASELINE10_ESTIMATED_DURATION;
   }

   private static final int[] ID_ARRAY = new int[TaskField.MAX_VALUE];
   static
   {
      Arrays.fill(ID_ARRAY, -1);

      for (int loop = 0; loop < FIELD_ARRAY.length; loop++)
      {
         TaskField taskField = FIELD_ARRAY[loop];
         if (taskField != null)
         {
            ID_ARRAY[taskField.getValue()] = loop;
         }
      }
   }
}
