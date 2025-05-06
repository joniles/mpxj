/*
 * file:       MPXTaskField.java
 * author:     Jon Iles
 * copyright:  (c) Packwood Software 2005
 * date:       20-Feb-2006
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

package org.mpxj.mpx;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.mpxj.FieldType;
import org.mpxj.TaskField;
import org.mpxj.common.TaskFieldLists;

/**
 * Utility class used to map between the integer values held in an MPX file
 * to represent a task field, and the enumerated type used to represent
 * task fields in MPXJ.
 */
final class MPXTaskField
{
   /**
    * Retrieve an instance of the TaskField class based on the data read from an
    * MPX file.
    *
    * @param value value from an MS Project file
    * @return TaskField instance
    */
   public static TaskField getMpxjField(int value)
   {
      TaskField result = null;

      if (value >= 0 && value < MPX_MPXJ_ARRAY.length)
      {
         result = MPX_MPXJ_ARRAY[value];
      }

      return (result);
   }

   /**
    * Retrieve the integer value used to represent a task field in an
    * MPX file.
    *
    * @param value MPXJ task field value
    * @return MPX field value
    */
   public static int getMpxField(int value)
   {
      int result = 0;

      if (value >= 0 && value < MPXJ_MPX_ARRAY.length)
      {
         result = MPXJ_MPX_ARRAY[value];
      }
      return (result);
   }

   public static final int MAX_FIELDS = 153;

   private static final TaskField[] MPX_MPXJ_ARRAY = new TaskField[MAX_FIELDS];
   static
   {
      MPX_MPXJ_ARRAY[32] = TaskField.ACTUAL_COST;
      MPX_MPXJ_ARRAY[42] = TaskField.ACTUAL_DURATION;
      MPX_MPXJ_ARRAY[59] = TaskField.ACTUAL_FINISH;
      MPX_MPXJ_ARRAY[58] = TaskField.ACTUAL_START;
      MPX_MPXJ_ARRAY[22] = TaskField.ACTUAL_WORK;
      MPX_MPXJ_ARRAY[31] = TaskField.BASELINE_COST;
      MPX_MPXJ_ARRAY[41] = TaskField.BASELINE_DURATION;
      MPX_MPXJ_ARRAY[57] = TaskField.BASELINE_FINISH;
      MPX_MPXJ_ARRAY[56] = TaskField.BASELINE_START;
      MPX_MPXJ_ARRAY[21] = TaskField.BASELINE_WORK;
      MPX_MPXJ_ARRAY[86] = TaskField.BCWP;
      MPX_MPXJ_ARRAY[85] = TaskField.BCWS;
      MPX_MPXJ_ARRAY[135] = TaskField.CONFIRMED;
      MPX_MPXJ_ARRAY[68] = TaskField.CONSTRAINT_DATE;
      MPX_MPXJ_ARRAY[91] = TaskField.CONSTRAINT_TYPE;
      MPX_MPXJ_ARRAY[15] = TaskField.CONTACT;
      MPX_MPXJ_ARRAY[30] = TaskField.COST;
      MPX_MPXJ_ARRAY[34] = TaskField.COST_VARIANCE;
      MPX_MPXJ_ARRAY[36] = TaskField.COST1;
      MPX_MPXJ_ARRAY[37] = TaskField.COST2;
      MPX_MPXJ_ARRAY[38] = TaskField.COST3;
      MPX_MPXJ_ARRAY[125] = TaskField.CREATED;
      MPX_MPXJ_ARRAY[82] = TaskField.CRITICAL;
      MPX_MPXJ_ARRAY[88] = TaskField.CV;
      MPX_MPXJ_ARRAY[92] = TaskField.LEVELING_DELAY;
      MPX_MPXJ_ARRAY[40] = TaskField.DURATION;
      MPX_MPXJ_ARRAY[45] = TaskField.DURATION_VARIANCE;
      MPX_MPXJ_ARRAY[46] = TaskField.DURATION1;
      MPX_MPXJ_ARRAY[47] = TaskField.DURATION2;
      MPX_MPXJ_ARRAY[48] = TaskField.DURATION3;
      MPX_MPXJ_ARRAY[53] = TaskField.EARLY_FINISH;
      MPX_MPXJ_ARRAY[52] = TaskField.EARLY_START;
      MPX_MPXJ_ARRAY[51] = TaskField.FINISH;
      MPX_MPXJ_ARRAY[67] = TaskField.FINISH_VARIANCE;
      MPX_MPXJ_ARRAY[61] = TaskField.FINISH1;
      MPX_MPXJ_ARRAY[63] = TaskField.FINISH2;
      MPX_MPXJ_ARRAY[65] = TaskField.FINISH3;
      MPX_MPXJ_ARRAY[127] = TaskField.FINISH4;
      MPX_MPXJ_ARRAY[129] = TaskField.FINISH5;
      MPX_MPXJ_ARRAY[35] = TaskField.FIXED_COST;
      MPX_MPXJ_ARRAY[110] = TaskField.FLAG1;
      MPX_MPXJ_ARRAY[119] = TaskField.FLAG10;
      MPX_MPXJ_ARRAY[111] = TaskField.FLAG2;
      MPX_MPXJ_ARRAY[112] = TaskField.FLAG3;
      MPX_MPXJ_ARRAY[113] = TaskField.FLAG4;
      MPX_MPXJ_ARRAY[114] = TaskField.FLAG5;
      MPX_MPXJ_ARRAY[115] = TaskField.FLAG6;
      MPX_MPXJ_ARRAY[116] = TaskField.FLAG7;
      MPX_MPXJ_ARRAY[117] = TaskField.FLAG8;
      MPX_MPXJ_ARRAY[118] = TaskField.FLAG9;
      MPX_MPXJ_ARRAY[93] = TaskField.FREE_SLACK;
      MPX_MPXJ_ARRAY[123] = TaskField.HIDE_BAR;
      MPX_MPXJ_ARRAY[90] = TaskField.ID;
      MPX_MPXJ_ARRAY[55] = TaskField.LATE_FINISH;
      MPX_MPXJ_ARRAY[54] = TaskField.LATE_START;
      MPX_MPXJ_ARRAY[122] = TaskField.LINKED_FIELDS;
      MPX_MPXJ_ARRAY[83] = TaskField.MARKED;
      MPX_MPXJ_ARRAY[81] = TaskField.MILESTONE;
      MPX_MPXJ_ARRAY[1] = TaskField.NAME;
      MPX_MPXJ_ARRAY[14] = TaskField.NOTES;
      MPX_MPXJ_ARRAY[140] = TaskField.NUMBER1;
      MPX_MPXJ_ARRAY[141] = TaskField.NUMBER2;
      MPX_MPXJ_ARRAY[142] = TaskField.NUMBER3;
      MPX_MPXJ_ARRAY[143] = TaskField.NUMBER4;
      MPX_MPXJ_ARRAY[144] = TaskField.NUMBER5;
      MPX_MPXJ_ARRAY[121] = TaskField.OBJECTS;
      MPX_MPXJ_ARRAY[3] = TaskField.OUTLINE_LEVEL;
      MPX_MPXJ_ARRAY[99] = TaskField.OUTLINE_NUMBER;
      MPX_MPXJ_ARRAY[44] = TaskField.PERCENT_COMPLETE;
      MPX_MPXJ_ARRAY[25] = TaskField.PERCENT_WORK_COMPLETE;
      MPX_MPXJ_ARRAY[70] = TaskField.PREDECESSORS;
      MPX_MPXJ_ARRAY[95] = TaskField.PRIORITY;
      MPX_MPXJ_ARRAY[97] = TaskField.PROJECT;
      MPX_MPXJ_ARRAY[33] = TaskField.REMAINING_COST;
      MPX_MPXJ_ARRAY[43] = TaskField.REMAINING_DURATION;
      MPX_MPXJ_ARRAY[23] = TaskField.REMAINING_WORK;
      MPX_MPXJ_ARRAY[16] = TaskField.RESOURCE_GROUP;
      MPX_MPXJ_ARRAY[73] = TaskField.RESOURCE_INITIALS;
      MPX_MPXJ_ARRAY[72] = TaskField.RESOURCE_NAMES;
      MPX_MPXJ_ARRAY[151] = TaskField.RESUME;
      MPX_MPXJ_ARRAY[152] = TaskField.RESUME;
      MPX_MPXJ_ARRAY[84] = TaskField.ROLLUP;
      MPX_MPXJ_ARRAY[50] = TaskField.START;
      MPX_MPXJ_ARRAY[66] = TaskField.START_VARIANCE;
      MPX_MPXJ_ARRAY[60] = TaskField.START1;
      MPX_MPXJ_ARRAY[62] = TaskField.START2;
      MPX_MPXJ_ARRAY[64] = TaskField.START3;
      MPX_MPXJ_ARRAY[126] = TaskField.START4;
      MPX_MPXJ_ARRAY[128] = TaskField.START5;
      MPX_MPXJ_ARRAY[150] = TaskField.STOP;
      MPX_MPXJ_ARRAY[96] = TaskField.SUBPROJECT_FILE;
      MPX_MPXJ_ARRAY[71] = TaskField.SUCCESSORS;
      MPX_MPXJ_ARRAY[120] = TaskField.SUMMARY;
      MPX_MPXJ_ARRAY[87] = TaskField.SV;
      MPX_MPXJ_ARRAY[4] = TaskField.TEXT1;
      MPX_MPXJ_ARRAY[13] = TaskField.TEXT10;
      MPX_MPXJ_ARRAY[5] = TaskField.TEXT2;
      MPX_MPXJ_ARRAY[6] = TaskField.TEXT3;
      MPX_MPXJ_ARRAY[7] = TaskField.TEXT4;
      MPX_MPXJ_ARRAY[8] = TaskField.TEXT5;
      MPX_MPXJ_ARRAY[9] = TaskField.TEXT6;
      MPX_MPXJ_ARRAY[10] = TaskField.TEXT7;
      MPX_MPXJ_ARRAY[11] = TaskField.TEXT8;
      MPX_MPXJ_ARRAY[12] = TaskField.TEXT9;
      MPX_MPXJ_ARRAY[94] = TaskField.TOTAL_SLACK;
      MPX_MPXJ_ARRAY[80] = TaskField.TYPE;
      MPX_MPXJ_ARRAY[98] = TaskField.UNIQUE_ID;
      MPX_MPXJ_ARRAY[74] = TaskField.UNIQUE_ID_PREDECESSORS;
      MPX_MPXJ_ARRAY[75] = TaskField.UNIQUE_ID_SUCCESSORS;
      MPX_MPXJ_ARRAY[136] = TaskField.UPDATE_NEEDED;
      MPX_MPXJ_ARRAY[2] = TaskField.WBS;
      MPX_MPXJ_ARRAY[20] = TaskField.WORK;
      MPX_MPXJ_ARRAY[24] = TaskField.WORK_VARIANCE;
   }

   private static final int[] MPXJ_MPX_ARRAY = new int[TaskField.MAX_VALUE];
   static
   {
      for (int loop = 0; loop < MPX_MPXJ_ARRAY.length; loop++)
      {
         TaskField field = MPX_MPXJ_ARRAY[loop];
         if (field != null)
         {
            MPXJ_MPX_ARRAY[field.getValue()] = loop;
         }
      }
   }

   public static final List<FieldType> CUSTOM_FIELDS = new ArrayList<>();
   static
   {
      Arrays.stream(MPX_MPXJ_ARRAY).filter(TaskFieldLists.CUSTOM_FIELDS::contains).forEach(CUSTOM_FIELDS::add);
   }
}
