/*
 * file:       MPPTaskField.java
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

package org.mpxj.common;

import java.util.Arrays;

import org.mpxj.DataType;
import org.mpxj.FieldType;
import org.mpxj.FieldTypeClass;
import org.mpxj.ProjectFile;
import org.mpxj.TaskField;
import org.mpxj.UserDefinedField;

/**
 * Utility class used to map between the integer values held in MS Project
 * to represent a task field, and the enumerated type used to represent
 * task fields in MPXJ.
 */
public class MPPTaskField
{
   /**
    * Retrieve an instance of the TaskField class based on the data read from an
    * MS Project file.
    *
    * @param project parent project
    * @param value value from an MS Project file
    * @return TaskField instance
    */
   public static FieldType getInstance(ProjectFile project, int value)
   {
      return getInstance(project, value, DataType.CUSTOM);
   }

   /**
    * Retrieve an instance of the TaskField class based on the data read from an
    * MS Project file.
    *
    * @param project parent project
    * @param value value from an MS Project file
    * @param customFieldDataType custom field data type
    * @return TaskField instance
    */
   public static FieldType getInstance(ProjectFile project, int value, DataType customFieldDataType)
   {
      if ((value & 0x8000) != 0)
      {
         return project.getUserDefinedFields().getOrCreateTaskField(Integer.valueOf(value), (k) -> {
            int id = (k.intValue() & 0xFFF) + 1;
            return new UserDefinedField.Builder(project)
               .uniqueID(Integer.valueOf(TASK_FIELD_BASE + k.intValue()))
               .internalName("ENTERPRISE_CUSTOM_FIELD" + id)
               .externalName("Enterprise Custom Field " + id)
               .fieldTypeClass(FieldTypeClass.TASK)
               .dataType(customFieldDataType)
               .build();
         });
      }

      FieldType result = null;
      if (value >= 0 && value < FIELD_ARRAY.length)
      {
         if (NumberHelper.getInt(project.getProjectProperties().getMppFileType()) == 14)
         {
            result = mapMpp14(value);
         }

         if (result == null)
         {
            result = FIELD_ARRAY[value];
         }
      }

      return result;
   }

   /**
    * Retrieve the ID of a field, as used by MS Project.
    *
    * @param value field instance
    * @return field ID
    */
   public static int getID(FieldType value)
   {
      int result;

      if (value instanceof UserDefinedField)
      {
         result = value.getValue();
      }
      else
      {
         result = ID_ARRAY[value.getValue()];
      }
      return result;
   }

   /**
    * Handle field ID values which are different in MPP14 files compared to other MPP file variants.
    *
    * @param value field ID
    * @return field type
    */
   private static FieldType mapMpp14(int value)
   {
      FieldType result;

      switch (value)
      {
         case 27:
         {
            result = TaskField.BASELINE_ESTIMATED_DURATION;
            break;
         }

         case 29:
         {
            result = TaskField.SCHEDULED_DURATION;
            break;
         }

         case 35:
         {
            result = TaskField.SCHEDULED_START;
            break;
         }

         case 36:
         {
            result = TaskField.SCHEDULED_FINISH;
            break;
         }

         case 43:
         {
            result = TaskField.BASELINE_ESTIMATED_START;
            break;
         }

         case 44:
         {
            result = TaskField.BASELINE_ESTIMATED_FINISH;
            break;
         }

         case 482:
         {
            result = TaskField.BASELINE1_ESTIMATED_START;
            break;
         }

         case 483:
         {
            result = TaskField.BASELINE1_ESTIMATED_FINISH;
            break;
         }

         case 487:
         {
            result = TaskField.BASELINE1_ESTIMATED_DURATION;
            break;
         }

         case 493:
         {
            result = TaskField.BASELINE2_ESTIMATED_START;
            break;
         }

         case 494:
         {
            result = TaskField.BASELINE2_ESTIMATED_FINISH;
            break;
         }

         case 498:
         {
            result = TaskField.BASELINE2_ESTIMATED_DURATION;
            break;
         }

         case 504:
         {
            result = TaskField.BASELINE3_ESTIMATED_START;
            break;
         }

         case 505:
         {
            result = TaskField.BASELINE3_ESTIMATED_FINISH;
            break;
         }

         case 509:
         {
            result = TaskField.BASELINE3_ESTIMATED_DURATION;
            break;
         }

         case 515:
         {
            result = TaskField.BASELINE4_ESTIMATED_START;
            break;
         }

         case 516:
         {
            result = TaskField.BASELINE4_ESTIMATED_FINISH;
            break;
         }

         case 520:
         {
            result = TaskField.BASELINE4_ESTIMATED_DURATION;
            break;
         }

         case 526:
         {
            result = TaskField.BASELINE5_ESTIMATED_START;
            break;
         }

         case 527:
         {
            result = TaskField.BASELINE5_ESTIMATED_FINISH;
            break;
         }

         case 531:
         {
            result = TaskField.BASELINE5_ESTIMATED_DURATION;
            break;
         }

         case 544:
         {
            result = TaskField.BASELINE6_ESTIMATED_START;
            break;
         }

         case 545:
         {
            result = TaskField.BASELINE6_ESTIMATED_FINISH;
            break;
         }

         case 549:
         {
            result = TaskField.BASELINE6_ESTIMATED_DURATION;
            break;
         }

         case 555:
         {
            result = TaskField.BASELINE7_ESTIMATED_START;
            break;
         }

         case 556:
         {
            result = TaskField.BASELINE7_ESTIMATED_FINISH;
            break;
         }

         case 560:
         {
            result = TaskField.BASELINE7_ESTIMATED_DURATION;
            break;
         }

         case 566:
         {
            result = TaskField.BASELINE8_ESTIMATED_START;
            break;
         }

         case 567:
         {
            result = TaskField.BASELINE8_ESTIMATED_FINISH;
            break;
         }

         case 571:
         {
            result = TaskField.BASELINE8_ESTIMATED_DURATION;
            break;
         }

         case 577:
         {
            result = TaskField.BASELINE9_ESTIMATED_START;
            break;
         }

         case 578:
         {
            result = TaskField.BASELINE9_ESTIMATED_FINISH;
            break;
         }

         case 582:
         {
            result = TaskField.BASELINE9_ESTIMATED_DURATION;
            break;
         }

         case 588:
         {
            result = TaskField.BASELINE10_ESTIMATED_START;
            break;
         }

         case 589:
         {
            result = TaskField.BASELINE10_ESTIMATED_FINISH;
            break;
         }

         case 593:
         {
            result = TaskField.BASELINE10_ESTIMATED_DURATION;
            break;
         }

         default:
         {
            result = null;
            break;
         }
      }

      return result;
   }

   private static final int MAX_VALUE = 1414;

   static final TaskField[] FIELD_ARRAY = new TaskField[MAX_VALUE];

   static
   {
      //FIELD_ARRAY[135] = TaskField.PARENT_TASK;
      FIELD_ARRAY[0] = TaskField.WORK;
      FIELD_ARRAY[1] = TaskField.BASELINE_WORK;
      FIELD_ARRAY[2] = TaskField.ACTUAL_WORK;
      FIELD_ARRAY[3] = TaskField.WORK_VARIANCE;
      FIELD_ARRAY[4] = TaskField.REMAINING_WORK;
      FIELD_ARRAY[5] = TaskField.COST;
      FIELD_ARRAY[6] = TaskField.BASELINE_COST;
      FIELD_ARRAY[7] = TaskField.ACTUAL_COST;
      FIELD_ARRAY[8] = TaskField.FIXED_COST;
      FIELD_ARRAY[9] = TaskField.COST_VARIANCE;
      FIELD_ARRAY[10] = TaskField.REMAINING_COST;
      FIELD_ARRAY[11] = TaskField.BCWP;
      FIELD_ARRAY[12] = TaskField.BCWS;
      FIELD_ARRAY[13] = TaskField.SV;
      FIELD_ARRAY[14] = TaskField.NAME;
      FIELD_ARRAY[15] = TaskField.NOTES;
      FIELD_ARRAY[16] = TaskField.WBS;
      FIELD_ARRAY[17] = TaskField.CONSTRAINT_TYPE;
      FIELD_ARRAY[18] = TaskField.CONSTRAINT_DATE;
      FIELD_ARRAY[19] = TaskField.CRITICAL;
      FIELD_ARRAY[20] = TaskField.LEVELING_DELAY;
      FIELD_ARRAY[21] = TaskField.FREE_SLACK;
      FIELD_ARRAY[22] = TaskField.TOTAL_SLACK;
      FIELD_ARRAY[23] = TaskField.ID;
      FIELD_ARRAY[24] = TaskField.MILESTONE;
      FIELD_ARRAY[25] = TaskField.PRIORITY;
      FIELD_ARRAY[26] = TaskField.SUBPROJECT_FILE;
      FIELD_ARRAY[27] = TaskField.BASELINE_DURATION;
      FIELD_ARRAY[28] = TaskField.ACTUAL_DURATION;
      FIELD_ARRAY[29] = TaskField.DURATION;
      FIELD_ARRAY[30] = TaskField.DURATION_VARIANCE;
      FIELD_ARRAY[31] = TaskField.REMAINING_DURATION;
      FIELD_ARRAY[32] = TaskField.PERCENT_COMPLETE;
      FIELD_ARRAY[33] = TaskField.PERCENT_WORK_COMPLETE;
      FIELD_ARRAY[34] = TaskField.FIXED_DURATION;
      FIELD_ARRAY[35] = TaskField.START;
      FIELD_ARRAY[36] = TaskField.FINISH;
      FIELD_ARRAY[37] = TaskField.EARLY_START;
      FIELD_ARRAY[38] = TaskField.EARLY_FINISH;
      FIELD_ARRAY[39] = TaskField.LATE_START;
      FIELD_ARRAY[40] = TaskField.LATE_FINISH;
      FIELD_ARRAY[41] = TaskField.ACTUAL_START;
      FIELD_ARRAY[42] = TaskField.ACTUAL_FINISH;
      FIELD_ARRAY[43] = TaskField.BASELINE_START;
      FIELD_ARRAY[44] = TaskField.BASELINE_FINISH;
      FIELD_ARRAY[45] = TaskField.START_VARIANCE;
      FIELD_ARRAY[46] = TaskField.FINISH_VARIANCE;
      FIELD_ARRAY[47] = TaskField.PREDECESSORS;
      FIELD_ARRAY[48] = TaskField.SUCCESSORS;
      FIELD_ARRAY[49] = TaskField.RESOURCE_NAMES;
      FIELD_ARRAY[50] = TaskField.RESOURCE_INITIALS;
      FIELD_ARRAY[51] = TaskField.TEXT1;
      FIELD_ARRAY[52] = TaskField.START1;
      FIELD_ARRAY[53] = TaskField.FINISH1;
      FIELD_ARRAY[54] = TaskField.TEXT2;
      FIELD_ARRAY[55] = TaskField.START2;
      FIELD_ARRAY[56] = TaskField.FINISH2;
      FIELD_ARRAY[57] = TaskField.TEXT3;
      FIELD_ARRAY[58] = TaskField.START3;
      FIELD_ARRAY[59] = TaskField.FINISH3;
      FIELD_ARRAY[60] = TaskField.TEXT4;
      FIELD_ARRAY[61] = TaskField.START4;
      FIELD_ARRAY[62] = TaskField.FINISH4;
      FIELD_ARRAY[63] = TaskField.TEXT5;
      FIELD_ARRAY[64] = TaskField.START5;
      FIELD_ARRAY[65] = TaskField.FINISH5;
      FIELD_ARRAY[66] = TaskField.TEXT6;
      FIELD_ARRAY[67] = TaskField.TEXT7;
      FIELD_ARRAY[68] = TaskField.TEXT8;
      FIELD_ARRAY[69] = TaskField.TEXT9;
      FIELD_ARRAY[70] = TaskField.TEXT10;
      FIELD_ARRAY[71] = TaskField.MARKED;
      FIELD_ARRAY[72] = TaskField.FLAG1;
      FIELD_ARRAY[73] = TaskField.FLAG2;
      FIELD_ARRAY[74] = TaskField.FLAG3;
      FIELD_ARRAY[75] = TaskField.FLAG4;
      FIELD_ARRAY[76] = TaskField.FLAG5;
      FIELD_ARRAY[77] = TaskField.FLAG6;
      FIELD_ARRAY[78] = TaskField.FLAG7;
      FIELD_ARRAY[79] = TaskField.FLAG8;
      FIELD_ARRAY[80] = TaskField.FLAG9;
      FIELD_ARRAY[81] = TaskField.FLAG10;
      FIELD_ARRAY[82] = TaskField.ROLLUP;
      FIELD_ARRAY[83] = TaskField.CV;
      FIELD_ARRAY[84] = TaskField.PROJECT;
      FIELD_ARRAY[85] = TaskField.OUTLINE_LEVEL;
      FIELD_ARRAY[86] = TaskField.UNIQUE_ID;
      FIELD_ARRAY[87] = TaskField.NUMBER1;
      FIELD_ARRAY[88] = TaskField.NUMBER2;
      FIELD_ARRAY[89] = TaskField.NUMBER3;
      FIELD_ARRAY[90] = TaskField.NUMBER4;
      FIELD_ARRAY[91] = TaskField.NUMBER5;
      FIELD_ARRAY[92] = TaskField.SUMMARY;
      FIELD_ARRAY[93] = TaskField.CREATED;
      FIELD_ARRAY[94] = TaskField.NOTES;
      FIELD_ARRAY[95] = TaskField.UNIQUE_ID_PREDECESSORS;
      FIELD_ARRAY[96] = TaskField.UNIQUE_ID_SUCCESSORS;
      FIELD_ARRAY[97] = TaskField.OBJECTS;
      FIELD_ARRAY[98] = TaskField.LINKED_FIELDS;
      FIELD_ARRAY[99] = TaskField.RESUME;
      FIELD_ARRAY[100] = TaskField.STOP;
      FIELD_ARRAY[101] = TaskField.RESUME_NO_EARLIER_THAN;
      FIELD_ARRAY[102] = TaskField.OUTLINE_NUMBER;
      FIELD_ARRAY[103] = TaskField.DURATION1;
      FIELD_ARRAY[104] = TaskField.DURATION2;
      FIELD_ARRAY[105] = TaskField.DURATION3;
      FIELD_ARRAY[106] = TaskField.COST1;
      FIELD_ARRAY[107] = TaskField.COST2;
      FIELD_ARRAY[108] = TaskField.COST3;
      FIELD_ARRAY[109] = TaskField.HIDE_BAR;
      FIELD_ARRAY[110] = TaskField.CONFIRMED;
      FIELD_ARRAY[111] = TaskField.UPDATE_NEEDED;
      FIELD_ARRAY[112] = TaskField.CONTACT;
      FIELD_ARRAY[113] = TaskField.RESOURCE_GROUP;
      FIELD_ARRAY[119] = TaskField.COMPLETE_THROUGH;
      FIELD_ARRAY[120] = TaskField.ACWP;
      FIELD_ARRAY[128] = TaskField.TYPE;
      FIELD_ARRAY[129] = TaskField.RECURRING;
      FIELD_ARRAY[132] = TaskField.EFFORT_DRIVEN;
      FIELD_ARRAY[152] = TaskField.DURATION_UNITS;
      FIELD_ARRAY[160] = TaskField.PARENT_TASK_UNIQUE_ID;
      FIELD_ARRAY[163] = TaskField.OVERTIME_WORK;
      FIELD_ARRAY[164] = TaskField.ACTUAL_OVERTIME_WORK;
      FIELD_ARRAY[165] = TaskField.REMAINING_OVERTIME_WORK;
      FIELD_ARRAY[166] = TaskField.REGULAR_WORK;
      FIELD_ARRAY[168] = TaskField.OVERTIME_COST;
      FIELD_ARRAY[169] = TaskField.ACTUAL_OVERTIME_COST;
      FIELD_ARRAY[170] = TaskField.REMAINING_OVERTIME_COST;
      FIELD_ARRAY[178] = TaskField.LEVELING_DELAY_UNITS;
      FIELD_ARRAY[179] = TaskField.BASELINE_DURATION_UNITS;
      FIELD_ARRAY[181] = TaskField.ACTUAL_DURATION_UNITS;
      FIELD_ARRAY[183] = TaskField.DURATION1_UNITS;
      FIELD_ARRAY[184] = TaskField.DURATION2_UNITS;
      FIELD_ARRAY[185] = TaskField.DURATION3_UNITS;
      FIELD_ARRAY[200] = TaskField.FIXED_COST_ACCRUAL;
      FIELD_ARRAY[202] = TaskField.RECURRING;
      FIELD_ARRAY[203] = TaskField.RECURRING_DATA;
      FIELD_ARRAY[205] = TaskField.INDICATORS;
      FIELD_ARRAY[215] = TaskField.HYPERLINK_DATA;
      FIELD_ARRAY[217] = TaskField.HYPERLINK;
      FIELD_ARRAY[218] = TaskField.HYPERLINK_ADDRESS;
      FIELD_ARRAY[219] = TaskField.HYPERLINK_SUBADDRESS;
      FIELD_ARRAY[220] = TaskField.HYPERLINK_HREF;
      FIELD_ARRAY[224] = TaskField.ASSIGNMENT;
      FIELD_ARRAY[225] = TaskField.OVERALLOCATED;
      FIELD_ARRAY[232] = TaskField.EXTERNAL_TASK;
      FIELD_ARRAY[242] = TaskField.SUBPROJECT_TASK_UNIQUE_ID;
      FIELD_ARRAY[246] = TaskField.SUBPROJECT_READ_ONLY;
      FIELD_ARRAY[249] = TaskField.OUTLINE_LEVEL;
      FIELD_ARRAY[250] = TaskField.RESPONSE_PENDING;
      FIELD_ARRAY[251] = TaskField.TEAMSTATUS_PENDING;
      FIELD_ARRAY[252] = TaskField.LEVELING_CAN_SPLIT;
      FIELD_ARRAY[253] = TaskField.LEVEL_ASSIGNMENTS;
      FIELD_ARRAY[255] = TaskField.SUBPROJECT_TASK_ID;
      FIELD_ARRAY[256] = TaskField.WORK_CONTOUR;
      FIELD_ARRAY[258] = TaskField.COST4;
      FIELD_ARRAY[259] = TaskField.COST5;
      FIELD_ARRAY[260] = TaskField.COST6;
      FIELD_ARRAY[261] = TaskField.COST7;
      FIELD_ARRAY[262] = TaskField.COST8;
      FIELD_ARRAY[263] = TaskField.COST9;
      FIELD_ARRAY[264] = TaskField.COST10;
      FIELD_ARRAY[265] = TaskField.DATE1;
      FIELD_ARRAY[266] = TaskField.DATE2;
      FIELD_ARRAY[267] = TaskField.DATE3;
      FIELD_ARRAY[268] = TaskField.DATE4;
      FIELD_ARRAY[269] = TaskField.DATE5;
      FIELD_ARRAY[270] = TaskField.DATE6;
      FIELD_ARRAY[271] = TaskField.DATE7;
      FIELD_ARRAY[272] = TaskField.DATE8;
      FIELD_ARRAY[273] = TaskField.DATE9;
      FIELD_ARRAY[274] = TaskField.DATE10;
      FIELD_ARRAY[275] = TaskField.DURATION4;
      FIELD_ARRAY[276] = TaskField.DURATION5;
      FIELD_ARRAY[277] = TaskField.DURATION6;
      FIELD_ARRAY[278] = TaskField.DURATION7;
      FIELD_ARRAY[279] = TaskField.DURATION8;
      FIELD_ARRAY[280] = TaskField.DURATION9;
      FIELD_ARRAY[281] = TaskField.DURATION10;
      FIELD_ARRAY[282] = TaskField.START6;
      FIELD_ARRAY[283] = TaskField.FINISH6;
      FIELD_ARRAY[284] = TaskField.START7;
      FIELD_ARRAY[285] = TaskField.FINISH7;
      FIELD_ARRAY[286] = TaskField.START8;
      FIELD_ARRAY[287] = TaskField.FINISH8;
      FIELD_ARRAY[288] = TaskField.START9;
      FIELD_ARRAY[289] = TaskField.FINISH9;
      FIELD_ARRAY[290] = TaskField.START10;
      FIELD_ARRAY[291] = TaskField.FINISH10;
      FIELD_ARRAY[292] = TaskField.FLAG11;
      FIELD_ARRAY[293] = TaskField.FLAG12;
      FIELD_ARRAY[294] = TaskField.FLAG13;
      FIELD_ARRAY[295] = TaskField.FLAG14;
      FIELD_ARRAY[296] = TaskField.FLAG15;
      FIELD_ARRAY[297] = TaskField.FLAG16;
      FIELD_ARRAY[298] = TaskField.FLAG17;
      FIELD_ARRAY[299] = TaskField.FLAG18;
      FIELD_ARRAY[300] = TaskField.FLAG19;
      FIELD_ARRAY[301] = TaskField.FLAG20;
      FIELD_ARRAY[302] = TaskField.NUMBER6;
      FIELD_ARRAY[303] = TaskField.NUMBER7;
      FIELD_ARRAY[304] = TaskField.NUMBER8;
      FIELD_ARRAY[305] = TaskField.NUMBER9;
      FIELD_ARRAY[306] = TaskField.NUMBER10;
      FIELD_ARRAY[307] = TaskField.NUMBER11;
      FIELD_ARRAY[308] = TaskField.NUMBER12;
      FIELD_ARRAY[309] = TaskField.NUMBER13;
      FIELD_ARRAY[310] = TaskField.NUMBER14;
      FIELD_ARRAY[311] = TaskField.NUMBER15;
      FIELD_ARRAY[312] = TaskField.NUMBER16;
      FIELD_ARRAY[313] = TaskField.NUMBER17;
      FIELD_ARRAY[314] = TaskField.NUMBER18;
      FIELD_ARRAY[315] = TaskField.NUMBER19;
      FIELD_ARRAY[316] = TaskField.NUMBER20;
      FIELD_ARRAY[317] = TaskField.TEXT11;
      FIELD_ARRAY[318] = TaskField.TEXT12;
      FIELD_ARRAY[319] = TaskField.TEXT13;
      FIELD_ARRAY[320] = TaskField.TEXT14;
      FIELD_ARRAY[321] = TaskField.TEXT15;
      FIELD_ARRAY[322] = TaskField.TEXT16;
      FIELD_ARRAY[323] = TaskField.TEXT17;
      FIELD_ARRAY[324] = TaskField.TEXT18;
      FIELD_ARRAY[325] = TaskField.TEXT19;
      FIELD_ARRAY[326] = TaskField.TEXT20;
      FIELD_ARRAY[327] = TaskField.TEXT21;
      FIELD_ARRAY[328] = TaskField.TEXT22;
      FIELD_ARRAY[329] = TaskField.TEXT23;
      FIELD_ARRAY[330] = TaskField.TEXT24;
      FIELD_ARRAY[331] = TaskField.TEXT25;
      FIELD_ARRAY[332] = TaskField.TEXT26;
      FIELD_ARRAY[333] = TaskField.TEXT27;
      FIELD_ARRAY[334] = TaskField.TEXT28;
      FIELD_ARRAY[335] = TaskField.TEXT29;
      FIELD_ARRAY[336] = TaskField.TEXT30;
      FIELD_ARRAY[337] = TaskField.DURATION4_UNITS;
      FIELD_ARRAY[338] = TaskField.DURATION5_UNITS;
      FIELD_ARRAY[339] = TaskField.DURATION6_UNITS;
      FIELD_ARRAY[340] = TaskField.DURATION7_UNITS;
      FIELD_ARRAY[341] = TaskField.DURATION8_UNITS;
      FIELD_ARRAY[342] = TaskField.DURATION9_UNITS;
      FIELD_ARRAY[343] = TaskField.DURATION10_UNITS;
      FIELD_ARRAY[349] = TaskField.RESOURCE_PHONETICS;
      FIELD_ARRAY[360] = TaskField.INDEX;
      FIELD_ARRAY[366] = TaskField.ASSIGNMENT_DELAY;
      FIELD_ARRAY[367] = TaskField.ASSIGNMENT_UNITS;
      FIELD_ARRAY[368] = TaskField.COST_RATE_TABLE;
      FIELD_ARRAY[369] = TaskField.PRELEVELED_START;
      FIELD_ARRAY[370] = TaskField.PRELEVELED_FINISH;
      FIELD_ARRAY[372] = TaskField.SUMMARY_PROGRESS;
      FIELD_ARRAY[387] = TaskField.SUMMARY_PROGRESS;
      FIELD_ARRAY[396] = TaskField.ESTIMATED;
      FIELD_ARRAY[399] = TaskField.IGNORE_RESOURCE_CALENDAR;
      FIELD_ARRAY[401] = TaskField.CALENDAR_UNIQUE_ID;
      FIELD_ARRAY[402] = TaskField.TASK_CALENDAR;
      FIELD_ARRAY[403] = TaskField.DURATION1_ESTIMATED;
      FIELD_ARRAY[404] = TaskField.DURATION2_ESTIMATED;
      FIELD_ARRAY[405] = TaskField.DURATION3_ESTIMATED;
      FIELD_ARRAY[406] = TaskField.DURATION4_ESTIMATED;
      FIELD_ARRAY[407] = TaskField.DURATION5_ESTIMATED;
      FIELD_ARRAY[408] = TaskField.DURATION6_ESTIMATED;
      FIELD_ARRAY[409] = TaskField.DURATION7_ESTIMATED;
      FIELD_ARRAY[410] = TaskField.DURATION8_ESTIMATED;
      FIELD_ARRAY[411] = TaskField.DURATION9_ESTIMATED;
      FIELD_ARRAY[412] = TaskField.DURATION10_ESTIMATED;
      FIELD_ARRAY[413] = TaskField.BASELINE_DURATION_ESTIMATED;
      FIELD_ARRAY[416] = TaskField.OUTLINE_CODE1;
      FIELD_ARRAY[417] = TaskField.OUTLINE_CODE1_INDEX;
      FIELD_ARRAY[418] = TaskField.OUTLINE_CODE2;
      FIELD_ARRAY[419] = TaskField.OUTLINE_CODE2_INDEX;
      FIELD_ARRAY[420] = TaskField.OUTLINE_CODE3;
      FIELD_ARRAY[421] = TaskField.OUTLINE_CODE3_INDEX;
      FIELD_ARRAY[422] = TaskField.OUTLINE_CODE4;
      FIELD_ARRAY[423] = TaskField.OUTLINE_CODE4_INDEX;
      FIELD_ARRAY[424] = TaskField.OUTLINE_CODE5;
      FIELD_ARRAY[425] = TaskField.OUTLINE_CODE5_INDEX;
      FIELD_ARRAY[426] = TaskField.OUTLINE_CODE6;
      FIELD_ARRAY[427] = TaskField.OUTLINE_CODE6_INDEX;
      FIELD_ARRAY[428] = TaskField.OUTLINE_CODE7;
      FIELD_ARRAY[429] = TaskField.OUTLINE_CODE7_INDEX;
      FIELD_ARRAY[430] = TaskField.OUTLINE_CODE8;
      FIELD_ARRAY[431] = TaskField.OUTLINE_CODE8_INDEX;
      FIELD_ARRAY[432] = TaskField.OUTLINE_CODE9;
      FIELD_ARRAY[433] = TaskField.OUTLINE_CODE9_INDEX;
      FIELD_ARRAY[434] = TaskField.OUTLINE_CODE10;
      FIELD_ARRAY[435] = TaskField.OUTLINE_CODE10_INDEX;
      FIELD_ARRAY[437] = TaskField.DEADLINE;
      FIELD_ARRAY[438] = TaskField.START_SLACK;
      FIELD_ARRAY[439] = TaskField.FINISH_SLACK;
      FIELD_ARRAY[441] = TaskField.VAC;
      FIELD_ARRAY[446] = TaskField.GROUP_BY_SUMMARY;
      FIELD_ARRAY[449] = TaskField.WBS_PREDECESSORS;
      FIELD_ARRAY[450] = TaskField.WBS_SUCCESSORS;
      FIELD_ARRAY[451] = TaskField.RESOURCE_TYPE;
      FIELD_ARRAY[452] = TaskField.HYPERLINK_SCREEN_TIP;
      FIELD_ARRAY[458] = TaskField.SUBPROJECT_TASKS_UNIQUEID_OFFSET;
      FIELD_ARRAY[480] = TaskField.BASELINE_FIXED_COST;
      FIELD_ARRAY[481] = TaskField.ENTERPRISE_DATA;
      FIELD_ARRAY[482] = TaskField.BASELINE1_START;
      FIELD_ARRAY[483] = TaskField.BASELINE1_FINISH;
      FIELD_ARRAY[484] = TaskField.BASELINE1_COST;
      FIELD_ARRAY[485] = TaskField.BASELINE1_WORK;
      FIELD_ARRAY[487] = TaskField.BASELINE1_DURATION;
      FIELD_ARRAY[488] = TaskField.BASELINE1_DURATION_UNITS;
      FIELD_ARRAY[489] = TaskField.BASELINE1_FIXED_COST;
      FIELD_ARRAY[493] = TaskField.BASELINE2_START;
      FIELD_ARRAY[494] = TaskField.BASELINE2_FINISH;
      FIELD_ARRAY[495] = TaskField.BASELINE2_COST;
      FIELD_ARRAY[496] = TaskField.BASELINE2_WORK;
      FIELD_ARRAY[498] = TaskField.BASELINE2_DURATION;
      FIELD_ARRAY[499] = TaskField.BASELINE2_DURATION_UNITS;
      FIELD_ARRAY[500] = TaskField.BASELINE2_FIXED_COST;
      FIELD_ARRAY[504] = TaskField.BASELINE3_START;
      FIELD_ARRAY[505] = TaskField.BASELINE3_FINISH;
      FIELD_ARRAY[506] = TaskField.BASELINE3_COST;
      FIELD_ARRAY[507] = TaskField.BASELINE3_WORK;
      FIELD_ARRAY[509] = TaskField.BASELINE3_DURATION;
      FIELD_ARRAY[510] = TaskField.BASELINE3_DURATION_UNITS;
      FIELD_ARRAY[511] = TaskField.BASELINE3_FIXED_COST;
      FIELD_ARRAY[515] = TaskField.BASELINE4_START;
      FIELD_ARRAY[516] = TaskField.BASELINE4_FINISH;
      FIELD_ARRAY[517] = TaskField.BASELINE4_COST;
      FIELD_ARRAY[518] = TaskField.BASELINE4_WORK;
      FIELD_ARRAY[520] = TaskField.BASELINE4_DURATION;
      FIELD_ARRAY[521] = TaskField.BASELINE4_DURATION_UNITS;
      FIELD_ARRAY[522] = TaskField.BASELINE4_FIXED_COST;
      FIELD_ARRAY[526] = TaskField.BASELINE5_START;
      FIELD_ARRAY[527] = TaskField.BASELINE5_FINISH;
      FIELD_ARRAY[528] = TaskField.BASELINE5_COST;
      FIELD_ARRAY[529] = TaskField.BASELINE5_WORK;
      FIELD_ARRAY[531] = TaskField.BASELINE5_DURATION;
      FIELD_ARRAY[532] = TaskField.BASELINE5_DURATION_UNITS;
      FIELD_ARRAY[533] = TaskField.BASELINE5_FIXED_COST;
      FIELD_ARRAY[537] = TaskField.CPI;
      FIELD_ARRAY[538] = TaskField.SPI;
      FIELD_ARRAY[539] = TaskField.CVPERCENT;
      FIELD_ARRAY[540] = TaskField.SVPERCENT;
      FIELD_ARRAY[541] = TaskField.EAC;
      FIELD_ARRAY[542] = TaskField.TCPI;
      FIELD_ARRAY[543] = TaskField.STATUS;
      FIELD_ARRAY[544] = TaskField.BASELINE6_START;
      FIELD_ARRAY[545] = TaskField.BASELINE6_FINISH;
      FIELD_ARRAY[546] = TaskField.BASELINE6_COST;
      FIELD_ARRAY[547] = TaskField.BASELINE6_WORK;
      FIELD_ARRAY[549] = TaskField.BASELINE6_DURATION;
      FIELD_ARRAY[550] = TaskField.BASELINE6_DURATION_UNITS;
      FIELD_ARRAY[551] = TaskField.BASELINE6_FIXED_COST;
      FIELD_ARRAY[555] = TaskField.BASELINE7_START;
      FIELD_ARRAY[556] = TaskField.BASELINE7_FINISH;
      FIELD_ARRAY[557] = TaskField.BASELINE7_COST;
      FIELD_ARRAY[558] = TaskField.BASELINE7_WORK;
      FIELD_ARRAY[560] = TaskField.BASELINE7_DURATION;
      FIELD_ARRAY[561] = TaskField.BASELINE7_DURATION_UNITS;
      FIELD_ARRAY[562] = TaskField.BASELINE7_FIXED_COST;
      FIELD_ARRAY[566] = TaskField.BASELINE8_START;
      FIELD_ARRAY[567] = TaskField.BASELINE8_FINISH;
      FIELD_ARRAY[568] = TaskField.BASELINE8_COST;
      FIELD_ARRAY[569] = TaskField.BASELINE8_WORK;
      FIELD_ARRAY[571] = TaskField.BASELINE8_DURATION;
      FIELD_ARRAY[572] = TaskField.BASELINE8_DURATION_UNITS;
      FIELD_ARRAY[573] = TaskField.BASELINE8_FIXED_COST;
      FIELD_ARRAY[577] = TaskField.BASELINE9_START;
      FIELD_ARRAY[578] = TaskField.BASELINE9_FINISH;
      FIELD_ARRAY[579] = TaskField.BASELINE9_COST;
      FIELD_ARRAY[580] = TaskField.BASELINE9_WORK;
      FIELD_ARRAY[582] = TaskField.BASELINE9_DURATION;
      FIELD_ARRAY[583] = TaskField.BASELINE9_DURATION_UNITS;
      FIELD_ARRAY[584] = TaskField.BASELINE9_FIXED_COST;
      FIELD_ARRAY[588] = TaskField.BASELINE10_START;
      FIELD_ARRAY[589] = TaskField.BASELINE10_FINISH;
      FIELD_ARRAY[590] = TaskField.BASELINE10_COST;
      FIELD_ARRAY[591] = TaskField.BASELINE10_WORK;
      FIELD_ARRAY[593] = TaskField.BASELINE10_DURATION;
      FIELD_ARRAY[594] = TaskField.BASELINE10_DURATION_UNITS;
      FIELD_ARRAY[595] = TaskField.BASELINE10_FIXED_COST;
      FIELD_ARRAY[599] = TaskField.ENTERPRISE_COST1;
      FIELD_ARRAY[600] = TaskField.ENTERPRISE_COST2;
      FIELD_ARRAY[601] = TaskField.ENTERPRISE_COST3;
      FIELD_ARRAY[602] = TaskField.ENTERPRISE_COST4;
      FIELD_ARRAY[603] = TaskField.ENTERPRISE_COST5;
      FIELD_ARRAY[604] = TaskField.ENTERPRISE_COST6;
      FIELD_ARRAY[605] = TaskField.ENTERPRISE_COST7;
      FIELD_ARRAY[606] = TaskField.ENTERPRISE_COST8;
      FIELD_ARRAY[607] = TaskField.ENTERPRISE_COST9;
      FIELD_ARRAY[608] = TaskField.ENTERPRISE_COST10;
      FIELD_ARRAY[609] = TaskField.ENTERPRISE_DATE1;
      FIELD_ARRAY[610] = TaskField.ENTERPRISE_DATE2;
      FIELD_ARRAY[611] = TaskField.ENTERPRISE_DATE3;
      FIELD_ARRAY[612] = TaskField.ENTERPRISE_DATE4;
      FIELD_ARRAY[613] = TaskField.ENTERPRISE_DATE5;
      FIELD_ARRAY[614] = TaskField.ENTERPRISE_DATE6;
      FIELD_ARRAY[615] = TaskField.ENTERPRISE_DATE7;
      FIELD_ARRAY[616] = TaskField.ENTERPRISE_DATE8;
      FIELD_ARRAY[617] = TaskField.ENTERPRISE_DATE9;
      FIELD_ARRAY[618] = TaskField.ENTERPRISE_DATE10;
      FIELD_ARRAY[619] = TaskField.ENTERPRISE_DATE11;
      FIELD_ARRAY[620] = TaskField.ENTERPRISE_DATE12;
      FIELD_ARRAY[621] = TaskField.ENTERPRISE_DATE13;
      FIELD_ARRAY[622] = TaskField.ENTERPRISE_DATE14;
      FIELD_ARRAY[623] = TaskField.ENTERPRISE_DATE15;
      FIELD_ARRAY[624] = TaskField.ENTERPRISE_DATE16;
      FIELD_ARRAY[625] = TaskField.ENTERPRISE_DATE17;
      FIELD_ARRAY[626] = TaskField.ENTERPRISE_DATE18;
      FIELD_ARRAY[627] = TaskField.ENTERPRISE_DATE19;
      FIELD_ARRAY[628] = TaskField.ENTERPRISE_DATE20;
      FIELD_ARRAY[629] = TaskField.ENTERPRISE_DATE21;
      FIELD_ARRAY[630] = TaskField.ENTERPRISE_DATE22;
      FIELD_ARRAY[631] = TaskField.ENTERPRISE_DATE23;
      FIELD_ARRAY[632] = TaskField.ENTERPRISE_DATE24;
      FIELD_ARRAY[633] = TaskField.ENTERPRISE_DATE25;
      FIELD_ARRAY[634] = TaskField.ENTERPRISE_DATE26;
      FIELD_ARRAY[635] = TaskField.ENTERPRISE_DATE27;
      FIELD_ARRAY[636] = TaskField.ENTERPRISE_DATE28;
      FIELD_ARRAY[637] = TaskField.ENTERPRISE_DATE29;
      FIELD_ARRAY[638] = TaskField.ENTERPRISE_DATE30;
      FIELD_ARRAY[639] = TaskField.ENTERPRISE_DURATION1;
      FIELD_ARRAY[640] = TaskField.ENTERPRISE_DURATION2;
      FIELD_ARRAY[641] = TaskField.ENTERPRISE_DURATION3;
      FIELD_ARRAY[642] = TaskField.ENTERPRISE_DURATION4;
      FIELD_ARRAY[643] = TaskField.ENTERPRISE_DURATION5;
      FIELD_ARRAY[644] = TaskField.ENTERPRISE_DURATION6;
      FIELD_ARRAY[645] = TaskField.ENTERPRISE_DURATION7;
      FIELD_ARRAY[646] = TaskField.ENTERPRISE_DURATION8;
      FIELD_ARRAY[647] = TaskField.ENTERPRISE_DURATION9;
      FIELD_ARRAY[648] = TaskField.ENTERPRISE_DURATION10;
      FIELD_ARRAY[649] = TaskField.ENTERPRISE_DURATION1_UNITS;
      FIELD_ARRAY[650] = TaskField.ENTERPRISE_DURATION2_UNITS;
      FIELD_ARRAY[651] = TaskField.ENTERPRISE_DURATION3_UNITS;
      FIELD_ARRAY[652] = TaskField.ENTERPRISE_DURATION4_UNITS;
      FIELD_ARRAY[653] = TaskField.ENTERPRISE_DURATION5_UNITS;
      FIELD_ARRAY[654] = TaskField.ENTERPRISE_DURATION6_UNITS;
      FIELD_ARRAY[655] = TaskField.ENTERPRISE_DURATION7_UNITS;
      FIELD_ARRAY[656] = TaskField.ENTERPRISE_DURATION8_UNITS;
      FIELD_ARRAY[657] = TaskField.ENTERPRISE_DURATION9_UNITS;
      FIELD_ARRAY[658] = TaskField.ENTERPRISE_DURATION10_UNITS;
      FIELD_ARRAY[659] = TaskField.ENTERPRISE_FLAG1;
      FIELD_ARRAY[660] = TaskField.ENTERPRISE_FLAG2;
      FIELD_ARRAY[661] = TaskField.ENTERPRISE_FLAG3;
      FIELD_ARRAY[662] = TaskField.ENTERPRISE_FLAG4;
      FIELD_ARRAY[663] = TaskField.ENTERPRISE_FLAG5;
      FIELD_ARRAY[664] = TaskField.ENTERPRISE_FLAG6;
      FIELD_ARRAY[665] = TaskField.ENTERPRISE_FLAG7;
      FIELD_ARRAY[666] = TaskField.ENTERPRISE_FLAG8;
      FIELD_ARRAY[667] = TaskField.ENTERPRISE_FLAG9;
      FIELD_ARRAY[668] = TaskField.ENTERPRISE_FLAG10;
      FIELD_ARRAY[669] = TaskField.ENTERPRISE_FLAG11;
      FIELD_ARRAY[670] = TaskField.ENTERPRISE_FLAG12;
      FIELD_ARRAY[671] = TaskField.ENTERPRISE_FLAG13;
      FIELD_ARRAY[672] = TaskField.ENTERPRISE_FLAG14;
      FIELD_ARRAY[673] = TaskField.ENTERPRISE_FLAG15;
      FIELD_ARRAY[674] = TaskField.ENTERPRISE_FLAG16;
      FIELD_ARRAY[675] = TaskField.ENTERPRISE_FLAG17;
      FIELD_ARRAY[676] = TaskField.ENTERPRISE_FLAG18;
      FIELD_ARRAY[677] = TaskField.ENTERPRISE_FLAG19;
      FIELD_ARRAY[678] = TaskField.ENTERPRISE_FLAG20;
      FIELD_ARRAY[699] = TaskField.ENTERPRISE_NUMBER1;
      FIELD_ARRAY[700] = TaskField.ENTERPRISE_NUMBER2;
      FIELD_ARRAY[701] = TaskField.ENTERPRISE_NUMBER3;
      FIELD_ARRAY[702] = TaskField.ENTERPRISE_NUMBER4;
      FIELD_ARRAY[703] = TaskField.ENTERPRISE_NUMBER5;
      FIELD_ARRAY[704] = TaskField.ENTERPRISE_NUMBER6;
      FIELD_ARRAY[705] = TaskField.ENTERPRISE_NUMBER7;
      FIELD_ARRAY[706] = TaskField.ENTERPRISE_NUMBER8;
      FIELD_ARRAY[707] = TaskField.ENTERPRISE_NUMBER9;
      FIELD_ARRAY[708] = TaskField.ENTERPRISE_NUMBER10;
      FIELD_ARRAY[709] = TaskField.ENTERPRISE_NUMBER11;
      FIELD_ARRAY[710] = TaskField.ENTERPRISE_NUMBER12;
      FIELD_ARRAY[711] = TaskField.ENTERPRISE_NUMBER13;
      FIELD_ARRAY[712] = TaskField.ENTERPRISE_NUMBER14;
      FIELD_ARRAY[713] = TaskField.ENTERPRISE_NUMBER15;
      FIELD_ARRAY[714] = TaskField.ENTERPRISE_NUMBER16;
      FIELD_ARRAY[715] = TaskField.ENTERPRISE_NUMBER17;
      FIELD_ARRAY[716] = TaskField.ENTERPRISE_NUMBER18;
      FIELD_ARRAY[717] = TaskField.ENTERPRISE_NUMBER19;
      FIELD_ARRAY[718] = TaskField.ENTERPRISE_NUMBER20;
      FIELD_ARRAY[719] = TaskField.ENTERPRISE_NUMBER21;
      FIELD_ARRAY[720] = TaskField.ENTERPRISE_NUMBER22;
      FIELD_ARRAY[721] = TaskField.ENTERPRISE_NUMBER23;
      FIELD_ARRAY[722] = TaskField.ENTERPRISE_NUMBER24;
      FIELD_ARRAY[723] = TaskField.ENTERPRISE_NUMBER25;
      FIELD_ARRAY[724] = TaskField.ENTERPRISE_NUMBER26;
      FIELD_ARRAY[725] = TaskField.ENTERPRISE_NUMBER27;
      FIELD_ARRAY[726] = TaskField.ENTERPRISE_NUMBER28;
      FIELD_ARRAY[727] = TaskField.ENTERPRISE_NUMBER29;
      FIELD_ARRAY[728] = TaskField.ENTERPRISE_NUMBER30;
      FIELD_ARRAY[729] = TaskField.ENTERPRISE_NUMBER31;
      FIELD_ARRAY[730] = TaskField.ENTERPRISE_NUMBER32;
      FIELD_ARRAY[731] = TaskField.ENTERPRISE_NUMBER33;
      FIELD_ARRAY[732] = TaskField.ENTERPRISE_NUMBER34;
      FIELD_ARRAY[733] = TaskField.ENTERPRISE_NUMBER35;
      FIELD_ARRAY[734] = TaskField.ENTERPRISE_NUMBER36;
      FIELD_ARRAY[735] = TaskField.ENTERPRISE_NUMBER37;
      FIELD_ARRAY[736] = TaskField.ENTERPRISE_NUMBER38;
      FIELD_ARRAY[737] = TaskField.ENTERPRISE_NUMBER39;
      FIELD_ARRAY[738] = TaskField.ENTERPRISE_NUMBER40;
      FIELD_ARRAY[739] = TaskField.ENTERPRISE_OUTLINE_CODE1;
      FIELD_ARRAY[741] = TaskField.ENTERPRISE_OUTLINE_CODE2;
      FIELD_ARRAY[743] = TaskField.ENTERPRISE_OUTLINE_CODE3;
      FIELD_ARRAY[745] = TaskField.ENTERPRISE_OUTLINE_CODE4;
      FIELD_ARRAY[747] = TaskField.ENTERPRISE_OUTLINE_CODE5;
      FIELD_ARRAY[749] = TaskField.ENTERPRISE_OUTLINE_CODE6;
      FIELD_ARRAY[751] = TaskField.ENTERPRISE_OUTLINE_CODE7;
      FIELD_ARRAY[753] = TaskField.ENTERPRISE_OUTLINE_CODE8;
      FIELD_ARRAY[755] = TaskField.ENTERPRISE_OUTLINE_CODE9;
      FIELD_ARRAY[757] = TaskField.ENTERPRISE_OUTLINE_CODE10;
      FIELD_ARRAY[759] = TaskField.ENTERPRISE_OUTLINE_CODE11;
      FIELD_ARRAY[761] = TaskField.ENTERPRISE_OUTLINE_CODE12;
      FIELD_ARRAY[763] = TaskField.ENTERPRISE_OUTLINE_CODE13;
      FIELD_ARRAY[765] = TaskField.ENTERPRISE_OUTLINE_CODE14;
      FIELD_ARRAY[767] = TaskField.ENTERPRISE_OUTLINE_CODE15;
      FIELD_ARRAY[769] = TaskField.ENTERPRISE_OUTLINE_CODE16;
      FIELD_ARRAY[771] = TaskField.ENTERPRISE_OUTLINE_CODE17;
      FIELD_ARRAY[773] = TaskField.ENTERPRISE_OUTLINE_CODE18;
      FIELD_ARRAY[775] = TaskField.ENTERPRISE_OUTLINE_CODE19;
      FIELD_ARRAY[777] = TaskField.ENTERPRISE_OUTLINE_CODE20;
      FIELD_ARRAY[779] = TaskField.ENTERPRISE_OUTLINE_CODE21;
      FIELD_ARRAY[781] = TaskField.ENTERPRISE_OUTLINE_CODE22;
      FIELD_ARRAY[783] = TaskField.ENTERPRISE_OUTLINE_CODE23;
      FIELD_ARRAY[785] = TaskField.ENTERPRISE_OUTLINE_CODE24;
      FIELD_ARRAY[787] = TaskField.ENTERPRISE_OUTLINE_CODE25;
      FIELD_ARRAY[789] = TaskField.ENTERPRISE_OUTLINE_CODE26;
      FIELD_ARRAY[791] = TaskField.ENTERPRISE_OUTLINE_CODE27;
      FIELD_ARRAY[793] = TaskField.ENTERPRISE_OUTLINE_CODE28;
      FIELD_ARRAY[795] = TaskField.ENTERPRISE_OUTLINE_CODE29;
      FIELD_ARRAY[797] = TaskField.ENTERPRISE_OUTLINE_CODE30;
      FIELD_ARRAY[799] = TaskField.ENTERPRISE_TEXT1;
      FIELD_ARRAY[800] = TaskField.ENTERPRISE_TEXT2;
      FIELD_ARRAY[801] = TaskField.ENTERPRISE_TEXT3;
      FIELD_ARRAY[802] = TaskField.ENTERPRISE_TEXT4;
      FIELD_ARRAY[803] = TaskField.ENTERPRISE_TEXT5;
      FIELD_ARRAY[804] = TaskField.ENTERPRISE_TEXT6;
      FIELD_ARRAY[805] = TaskField.ENTERPRISE_TEXT7;
      FIELD_ARRAY[806] = TaskField.ENTERPRISE_TEXT8;
      FIELD_ARRAY[807] = TaskField.ENTERPRISE_TEXT9;
      FIELD_ARRAY[808] = TaskField.ENTERPRISE_TEXT10;
      FIELD_ARRAY[809] = TaskField.ENTERPRISE_TEXT11;
      FIELD_ARRAY[810] = TaskField.ENTERPRISE_TEXT12;
      FIELD_ARRAY[811] = TaskField.ENTERPRISE_TEXT13;
      FIELD_ARRAY[812] = TaskField.ENTERPRISE_TEXT14;
      FIELD_ARRAY[813] = TaskField.ENTERPRISE_TEXT15;
      FIELD_ARRAY[814] = TaskField.ENTERPRISE_TEXT16;
      FIELD_ARRAY[815] = TaskField.ENTERPRISE_TEXT17;
      FIELD_ARRAY[816] = TaskField.ENTERPRISE_TEXT18;
      FIELD_ARRAY[817] = TaskField.ENTERPRISE_TEXT19;
      FIELD_ARRAY[818] = TaskField.ENTERPRISE_TEXT20;
      FIELD_ARRAY[819] = TaskField.ENTERPRISE_TEXT21;
      FIELD_ARRAY[820] = TaskField.ENTERPRISE_TEXT22;
      FIELD_ARRAY[821] = TaskField.ENTERPRISE_TEXT23;
      FIELD_ARRAY[822] = TaskField.ENTERPRISE_TEXT24;
      FIELD_ARRAY[823] = TaskField.ENTERPRISE_TEXT25;
      FIELD_ARRAY[824] = TaskField.ENTERPRISE_TEXT26;
      FIELD_ARRAY[825] = TaskField.ENTERPRISE_TEXT27;
      FIELD_ARRAY[826] = TaskField.ENTERPRISE_TEXT28;
      FIELD_ARRAY[827] = TaskField.ENTERPRISE_TEXT29;
      FIELD_ARRAY[828] = TaskField.ENTERPRISE_TEXT30;
      FIELD_ARRAY[829] = TaskField.ENTERPRISE_TEXT31;
      FIELD_ARRAY[830] = TaskField.ENTERPRISE_TEXT32;
      FIELD_ARRAY[831] = TaskField.ENTERPRISE_TEXT33;
      FIELD_ARRAY[832] = TaskField.ENTERPRISE_TEXT34;
      FIELD_ARRAY[833] = TaskField.ENTERPRISE_TEXT35;
      FIELD_ARRAY[834] = TaskField.ENTERPRISE_TEXT36;
      FIELD_ARRAY[835] = TaskField.ENTERPRISE_TEXT37;
      FIELD_ARRAY[836] = TaskField.ENTERPRISE_TEXT38;
      FIELD_ARRAY[837] = TaskField.ENTERPRISE_TEXT39;
      FIELD_ARRAY[838] = TaskField.ENTERPRISE_TEXT40;
      FIELD_ARRAY[839] = TaskField.BASELINE1_DURATION_ESTIMATED;
      FIELD_ARRAY[840] = TaskField.BASELINE2_DURATION_ESTIMATED;
      FIELD_ARRAY[841] = TaskField.BASELINE3_DURATION_ESTIMATED;
      FIELD_ARRAY[842] = TaskField.BASELINE4_DURATION_ESTIMATED;
      FIELD_ARRAY[843] = TaskField.BASELINE5_DURATION_ESTIMATED;
      FIELD_ARRAY[844] = TaskField.BASELINE6_DURATION_ESTIMATED;
      FIELD_ARRAY[845] = TaskField.BASELINE7_DURATION_ESTIMATED;
      FIELD_ARRAY[846] = TaskField.BASELINE8_DURATION_ESTIMATED;
      FIELD_ARRAY[847] = TaskField.BASELINE9_DURATION_ESTIMATED;
      FIELD_ARRAY[848] = TaskField.BASELINE10_DURATION_ESTIMATED;
      FIELD_ARRAY[849] = TaskField.ENTERPRISE_PROJECT_COST1;
      FIELD_ARRAY[850] = TaskField.ENTERPRISE_PROJECT_COST2;
      FIELD_ARRAY[851] = TaskField.ENTERPRISE_PROJECT_COST3;
      FIELD_ARRAY[852] = TaskField.ENTERPRISE_PROJECT_COST4;
      FIELD_ARRAY[853] = TaskField.ENTERPRISE_PROJECT_COST5;
      FIELD_ARRAY[854] = TaskField.ENTERPRISE_PROJECT_COST6;
      FIELD_ARRAY[855] = TaskField.ENTERPRISE_PROJECT_COST7;
      FIELD_ARRAY[856] = TaskField.ENTERPRISE_PROJECT_COST8;
      FIELD_ARRAY[857] = TaskField.ENTERPRISE_PROJECT_COST9;
      FIELD_ARRAY[858] = TaskField.ENTERPRISE_PROJECT_COST10;
      FIELD_ARRAY[859] = TaskField.ENTERPRISE_PROJECT_DATE1;
      FIELD_ARRAY[860] = TaskField.ENTERPRISE_PROJECT_DATE2;
      FIELD_ARRAY[861] = TaskField.ENTERPRISE_PROJECT_DATE3;
      FIELD_ARRAY[862] = TaskField.ENTERPRISE_PROJECT_DATE4;
      FIELD_ARRAY[863] = TaskField.ENTERPRISE_PROJECT_DATE5;
      FIELD_ARRAY[864] = TaskField.ENTERPRISE_PROJECT_DATE6;
      FIELD_ARRAY[865] = TaskField.ENTERPRISE_PROJECT_DATE7;
      FIELD_ARRAY[866] = TaskField.ENTERPRISE_PROJECT_DATE8;
      FIELD_ARRAY[867] = TaskField.ENTERPRISE_PROJECT_DATE9;
      FIELD_ARRAY[868] = TaskField.ENTERPRISE_PROJECT_DATE10;
      FIELD_ARRAY[869] = TaskField.ENTERPRISE_PROJECT_DATE11;
      FIELD_ARRAY[870] = TaskField.ENTERPRISE_PROJECT_DATE12;
      FIELD_ARRAY[871] = TaskField.ENTERPRISE_PROJECT_DATE13;
      FIELD_ARRAY[872] = TaskField.ENTERPRISE_PROJECT_DATE14;
      FIELD_ARRAY[873] = TaskField.ENTERPRISE_PROJECT_DATE15;
      FIELD_ARRAY[874] = TaskField.ENTERPRISE_PROJECT_DATE16;
      FIELD_ARRAY[875] = TaskField.ENTERPRISE_PROJECT_DATE17;
      FIELD_ARRAY[876] = TaskField.ENTERPRISE_PROJECT_DATE18;
      FIELD_ARRAY[877] = TaskField.ENTERPRISE_PROJECT_DATE19;
      FIELD_ARRAY[878] = TaskField.ENTERPRISE_PROJECT_DATE20;
      FIELD_ARRAY[879] = TaskField.ENTERPRISE_PROJECT_DATE21;
      FIELD_ARRAY[880] = TaskField.ENTERPRISE_PROJECT_DATE22;
      FIELD_ARRAY[881] = TaskField.ENTERPRISE_PROJECT_DATE23;
      FIELD_ARRAY[882] = TaskField.ENTERPRISE_PROJECT_DATE24;
      FIELD_ARRAY[883] = TaskField.ENTERPRISE_PROJECT_DATE25;
      FIELD_ARRAY[884] = TaskField.ENTERPRISE_PROJECT_DATE26;
      FIELD_ARRAY[885] = TaskField.ENTERPRISE_PROJECT_DATE27;
      FIELD_ARRAY[886] = TaskField.ENTERPRISE_PROJECT_DATE28;
      FIELD_ARRAY[887] = TaskField.ENTERPRISE_PROJECT_DATE29;
      FIELD_ARRAY[888] = TaskField.ENTERPRISE_PROJECT_DATE30;
      FIELD_ARRAY[889] = TaskField.ENTERPRISE_PROJECT_DURATION1;
      FIELD_ARRAY[890] = TaskField.ENTERPRISE_PROJECT_DURATION2;
      FIELD_ARRAY[891] = TaskField.ENTERPRISE_PROJECT_DURATION3;
      FIELD_ARRAY[892] = TaskField.ENTERPRISE_PROJECT_DURATION4;
      FIELD_ARRAY[893] = TaskField.ENTERPRISE_PROJECT_DURATION5;
      FIELD_ARRAY[894] = TaskField.ENTERPRISE_PROJECT_DURATION6;
      FIELD_ARRAY[895] = TaskField.ENTERPRISE_PROJECT_DURATION7;
      FIELD_ARRAY[896] = TaskField.ENTERPRISE_PROJECT_DURATION8;
      FIELD_ARRAY[897] = TaskField.ENTERPRISE_PROJECT_DURATION9;
      FIELD_ARRAY[898] = TaskField.ENTERPRISE_PROJECT_DURATION10;
      FIELD_ARRAY[909] = TaskField.ENTERPRISE_PROJECT_OUTLINE_CODE1;
      FIELD_ARRAY[910] = TaskField.ENTERPRISE_PROJECT_OUTLINE_CODE2;
      FIELD_ARRAY[911] = TaskField.ENTERPRISE_PROJECT_OUTLINE_CODE3;
      FIELD_ARRAY[912] = TaskField.ENTERPRISE_PROJECT_OUTLINE_CODE4;
      FIELD_ARRAY[913] = TaskField.ENTERPRISE_PROJECT_OUTLINE_CODE5;
      FIELD_ARRAY[914] = TaskField.ENTERPRISE_PROJECT_OUTLINE_CODE6;
      FIELD_ARRAY[915] = TaskField.ENTERPRISE_PROJECT_OUTLINE_CODE7;
      FIELD_ARRAY[916] = TaskField.ENTERPRISE_PROJECT_OUTLINE_CODE8;
      FIELD_ARRAY[917] = TaskField.ENTERPRISE_PROJECT_OUTLINE_CODE9;
      FIELD_ARRAY[918] = TaskField.ENTERPRISE_PROJECT_OUTLINE_CODE10;
      FIELD_ARRAY[919] = TaskField.ENTERPRISE_PROJECT_OUTLINE_CODE11;
      FIELD_ARRAY[920] = TaskField.ENTERPRISE_PROJECT_OUTLINE_CODE12;
      FIELD_ARRAY[921] = TaskField.ENTERPRISE_PROJECT_OUTLINE_CODE13;
      FIELD_ARRAY[922] = TaskField.ENTERPRISE_PROJECT_OUTLINE_CODE14;
      FIELD_ARRAY[923] = TaskField.ENTERPRISE_PROJECT_OUTLINE_CODE15;
      FIELD_ARRAY[924] = TaskField.ENTERPRISE_PROJECT_OUTLINE_CODE16;
      FIELD_ARRAY[925] = TaskField.ENTERPRISE_PROJECT_OUTLINE_CODE17;
      FIELD_ARRAY[926] = TaskField.ENTERPRISE_PROJECT_OUTLINE_CODE18;
      FIELD_ARRAY[927] = TaskField.ENTERPRISE_PROJECT_OUTLINE_CODE19;
      FIELD_ARRAY[928] = TaskField.ENTERPRISE_PROJECT_OUTLINE_CODE20;
      FIELD_ARRAY[929] = TaskField.ENTERPRISE_PROJECT_OUTLINE_CODE21;
      FIELD_ARRAY[930] = TaskField.ENTERPRISE_PROJECT_OUTLINE_CODE22;
      FIELD_ARRAY[931] = TaskField.ENTERPRISE_PROJECT_OUTLINE_CODE23;
      FIELD_ARRAY[932] = TaskField.ENTERPRISE_PROJECT_OUTLINE_CODE24;
      FIELD_ARRAY[933] = TaskField.ENTERPRISE_PROJECT_OUTLINE_CODE25;
      FIELD_ARRAY[934] = TaskField.ENTERPRISE_PROJECT_OUTLINE_CODE26;
      FIELD_ARRAY[935] = TaskField.ENTERPRISE_PROJECT_OUTLINE_CODE27;
      FIELD_ARRAY[936] = TaskField.ENTERPRISE_PROJECT_OUTLINE_CODE28;
      FIELD_ARRAY[937] = TaskField.ENTERPRISE_PROJECT_OUTLINE_CODE29;
      FIELD_ARRAY[938] = TaskField.ENTERPRISE_PROJECT_OUTLINE_CODE30;
      FIELD_ARRAY[969] = TaskField.ENTERPRISE_PROJECT_FLAG1;
      FIELD_ARRAY[970] = TaskField.ENTERPRISE_PROJECT_FLAG2;
      FIELD_ARRAY[971] = TaskField.ENTERPRISE_PROJECT_FLAG3;
      FIELD_ARRAY[972] = TaskField.ENTERPRISE_PROJECT_FLAG4;
      FIELD_ARRAY[973] = TaskField.ENTERPRISE_PROJECT_FLAG5;
      FIELD_ARRAY[974] = TaskField.ENTERPRISE_PROJECT_FLAG6;
      FIELD_ARRAY[975] = TaskField.ENTERPRISE_PROJECT_FLAG7;
      FIELD_ARRAY[976] = TaskField.ENTERPRISE_PROJECT_FLAG8;
      FIELD_ARRAY[977] = TaskField.ENTERPRISE_PROJECT_FLAG9;
      FIELD_ARRAY[978] = TaskField.ENTERPRISE_PROJECT_FLAG10;
      FIELD_ARRAY[979] = TaskField.ENTERPRISE_PROJECT_FLAG11;
      FIELD_ARRAY[980] = TaskField.ENTERPRISE_PROJECT_FLAG12;
      FIELD_ARRAY[981] = TaskField.ENTERPRISE_PROJECT_FLAG13;
      FIELD_ARRAY[982] = TaskField.ENTERPRISE_PROJECT_FLAG14;
      FIELD_ARRAY[983] = TaskField.ENTERPRISE_PROJECT_FLAG15;
      FIELD_ARRAY[984] = TaskField.ENTERPRISE_PROJECT_FLAG16;
      FIELD_ARRAY[985] = TaskField.ENTERPRISE_PROJECT_FLAG17;
      FIELD_ARRAY[986] = TaskField.ENTERPRISE_PROJECT_FLAG18;
      FIELD_ARRAY[987] = TaskField.ENTERPRISE_PROJECT_FLAG19;
      FIELD_ARRAY[988] = TaskField.ENTERPRISE_PROJECT_FLAG20;
      FIELD_ARRAY[1009] = TaskField.ENTERPRISE_PROJECT_NUMBER1;
      FIELD_ARRAY[1010] = TaskField.ENTERPRISE_PROJECT_NUMBER2;
      FIELD_ARRAY[1011] = TaskField.ENTERPRISE_PROJECT_NUMBER3;
      FIELD_ARRAY[1012] = TaskField.ENTERPRISE_PROJECT_NUMBER4;
      FIELD_ARRAY[1013] = TaskField.ENTERPRISE_PROJECT_NUMBER5;
      FIELD_ARRAY[1014] = TaskField.ENTERPRISE_PROJECT_NUMBER6;
      FIELD_ARRAY[1015] = TaskField.ENTERPRISE_PROJECT_NUMBER7;
      FIELD_ARRAY[1016] = TaskField.ENTERPRISE_PROJECT_NUMBER8;
      FIELD_ARRAY[1017] = TaskField.ENTERPRISE_PROJECT_NUMBER9;
      FIELD_ARRAY[1018] = TaskField.ENTERPRISE_PROJECT_NUMBER10;
      FIELD_ARRAY[1019] = TaskField.ENTERPRISE_PROJECT_NUMBER11;
      FIELD_ARRAY[1020] = TaskField.ENTERPRISE_PROJECT_NUMBER12;
      FIELD_ARRAY[1021] = TaskField.ENTERPRISE_PROJECT_NUMBER13;
      FIELD_ARRAY[1022] = TaskField.ENTERPRISE_PROJECT_NUMBER14;
      FIELD_ARRAY[1023] = TaskField.ENTERPRISE_PROJECT_NUMBER15;
      FIELD_ARRAY[1024] = TaskField.ENTERPRISE_PROJECT_NUMBER16;
      FIELD_ARRAY[1025] = TaskField.ENTERPRISE_PROJECT_NUMBER17;
      FIELD_ARRAY[1026] = TaskField.ENTERPRISE_PROJECT_NUMBER18;
      FIELD_ARRAY[1027] = TaskField.ENTERPRISE_PROJECT_NUMBER19;
      FIELD_ARRAY[1028] = TaskField.ENTERPRISE_PROJECT_NUMBER20;
      FIELD_ARRAY[1029] = TaskField.ENTERPRISE_PROJECT_NUMBER21;
      FIELD_ARRAY[1030] = TaskField.ENTERPRISE_PROJECT_NUMBER22;
      FIELD_ARRAY[1031] = TaskField.ENTERPRISE_PROJECT_NUMBER23;
      FIELD_ARRAY[1032] = TaskField.ENTERPRISE_PROJECT_NUMBER24;
      FIELD_ARRAY[1033] = TaskField.ENTERPRISE_PROJECT_NUMBER25;
      FIELD_ARRAY[1034] = TaskField.ENTERPRISE_PROJECT_NUMBER26;
      FIELD_ARRAY[1035] = TaskField.ENTERPRISE_PROJECT_NUMBER27;
      FIELD_ARRAY[1036] = TaskField.ENTERPRISE_PROJECT_NUMBER28;
      FIELD_ARRAY[1037] = TaskField.ENTERPRISE_PROJECT_NUMBER29;
      FIELD_ARRAY[1038] = TaskField.ENTERPRISE_PROJECT_NUMBER30;
      FIELD_ARRAY[1039] = TaskField.ENTERPRISE_PROJECT_NUMBER31;
      FIELD_ARRAY[1040] = TaskField.ENTERPRISE_PROJECT_NUMBER32;
      FIELD_ARRAY[1041] = TaskField.ENTERPRISE_PROJECT_NUMBER33;
      FIELD_ARRAY[1042] = TaskField.ENTERPRISE_PROJECT_NUMBER34;
      FIELD_ARRAY[1043] = TaskField.ENTERPRISE_PROJECT_NUMBER35;
      FIELD_ARRAY[1044] = TaskField.ENTERPRISE_PROJECT_NUMBER36;
      FIELD_ARRAY[1045] = TaskField.ENTERPRISE_PROJECT_NUMBER37;
      FIELD_ARRAY[1046] = TaskField.ENTERPRISE_PROJECT_NUMBER38;
      FIELD_ARRAY[1047] = TaskField.ENTERPRISE_PROJECT_NUMBER39;
      FIELD_ARRAY[1048] = TaskField.ENTERPRISE_PROJECT_NUMBER40;
      FIELD_ARRAY[1049] = TaskField.ENTERPRISE_PROJECT_TEXT1;
      FIELD_ARRAY[1050] = TaskField.ENTERPRISE_PROJECT_TEXT2;
      FIELD_ARRAY[1051] = TaskField.ENTERPRISE_PROJECT_TEXT3;
      FIELD_ARRAY[1052] = TaskField.ENTERPRISE_PROJECT_TEXT4;
      FIELD_ARRAY[1053] = TaskField.ENTERPRISE_PROJECT_TEXT5;
      FIELD_ARRAY[1054] = TaskField.ENTERPRISE_PROJECT_TEXT6;
      FIELD_ARRAY[1055] = TaskField.ENTERPRISE_PROJECT_TEXT7;
      FIELD_ARRAY[1056] = TaskField.ENTERPRISE_PROJECT_TEXT8;
      FIELD_ARRAY[1057] = TaskField.ENTERPRISE_PROJECT_TEXT9;
      FIELD_ARRAY[1058] = TaskField.ENTERPRISE_PROJECT_TEXT10;
      FIELD_ARRAY[1059] = TaskField.ENTERPRISE_PROJECT_TEXT11;
      FIELD_ARRAY[1060] = TaskField.ENTERPRISE_PROJECT_TEXT12;
      FIELD_ARRAY[1061] = TaskField.ENTERPRISE_PROJECT_TEXT13;
      FIELD_ARRAY[1062] = TaskField.ENTERPRISE_PROJECT_TEXT14;
      FIELD_ARRAY[1063] = TaskField.ENTERPRISE_PROJECT_TEXT15;
      FIELD_ARRAY[1064] = TaskField.ENTERPRISE_PROJECT_TEXT16;
      FIELD_ARRAY[1065] = TaskField.ENTERPRISE_PROJECT_TEXT17;
      FIELD_ARRAY[1066] = TaskField.ENTERPRISE_PROJECT_TEXT18;
      FIELD_ARRAY[1067] = TaskField.ENTERPRISE_PROJECT_TEXT19;
      FIELD_ARRAY[1068] = TaskField.ENTERPRISE_PROJECT_TEXT20;
      FIELD_ARRAY[1069] = TaskField.ENTERPRISE_PROJECT_TEXT21;
      FIELD_ARRAY[1070] = TaskField.ENTERPRISE_PROJECT_TEXT22;
      FIELD_ARRAY[1071] = TaskField.ENTERPRISE_PROJECT_TEXT23;
      FIELD_ARRAY[1072] = TaskField.ENTERPRISE_PROJECT_TEXT24;
      FIELD_ARRAY[1073] = TaskField.ENTERPRISE_PROJECT_TEXT25;
      FIELD_ARRAY[1074] = TaskField.ENTERPRISE_PROJECT_TEXT26;
      FIELD_ARRAY[1075] = TaskField.ENTERPRISE_PROJECT_TEXT27;
      FIELD_ARRAY[1076] = TaskField.ENTERPRISE_PROJECT_TEXT28;
      FIELD_ARRAY[1077] = TaskField.ENTERPRISE_PROJECT_TEXT29;
      FIELD_ARRAY[1078] = TaskField.ENTERPRISE_PROJECT_TEXT30;
      FIELD_ARRAY[1079] = TaskField.ENTERPRISE_PROJECT_TEXT31;
      FIELD_ARRAY[1080] = TaskField.ENTERPRISE_PROJECT_TEXT32;
      FIELD_ARRAY[1081] = TaskField.ENTERPRISE_PROJECT_TEXT33;
      FIELD_ARRAY[1082] = TaskField.ENTERPRISE_PROJECT_TEXT34;
      FIELD_ARRAY[1083] = TaskField.ENTERPRISE_PROJECT_TEXT35;
      FIELD_ARRAY[1084] = TaskField.ENTERPRISE_PROJECT_TEXT36;
      FIELD_ARRAY[1085] = TaskField.ENTERPRISE_PROJECT_TEXT37;
      FIELD_ARRAY[1086] = TaskField.ENTERPRISE_PROJECT_TEXT38;
      FIELD_ARRAY[1087] = TaskField.ENTERPRISE_PROJECT_TEXT39;
      FIELD_ARRAY[1088] = TaskField.ENTERPRISE_PROJECT_TEXT40;
      FIELD_ARRAY[1089] = TaskField.RESOURCE_ENTERPRISE_OUTLINE_CODE1;
      FIELD_ARRAY[1090] = TaskField.RESOURCE_ENTERPRISE_OUTLINE_CODE2;
      FIELD_ARRAY[1091] = TaskField.RESOURCE_ENTERPRISE_OUTLINE_CODE3;
      FIELD_ARRAY[1092] = TaskField.RESOURCE_ENTERPRISE_OUTLINE_CODE4;
      FIELD_ARRAY[1093] = TaskField.RESOURCE_ENTERPRISE_OUTLINE_CODE5;
      FIELD_ARRAY[1094] = TaskField.RESOURCE_ENTERPRISE_OUTLINE_CODE6;
      FIELD_ARRAY[1095] = TaskField.RESOURCE_ENTERPRISE_OUTLINE_CODE7;
      FIELD_ARRAY[1096] = TaskField.RESOURCE_ENTERPRISE_OUTLINE_CODE8;
      FIELD_ARRAY[1097] = TaskField.RESOURCE_ENTERPRISE_OUTLINE_CODE9;
      FIELD_ARRAY[1098] = TaskField.RESOURCE_ENTERPRISE_OUTLINE_CODE10;
      FIELD_ARRAY[1099] = TaskField.RESOURCE_ENTERPRISE_OUTLINE_CODE11;
      FIELD_ARRAY[1100] = TaskField.RESOURCE_ENTERPRISE_OUTLINE_CODE12;
      FIELD_ARRAY[1101] = TaskField.RESOURCE_ENTERPRISE_OUTLINE_CODE13;
      FIELD_ARRAY[1102] = TaskField.RESOURCE_ENTERPRISE_OUTLINE_CODE14;
      FIELD_ARRAY[1103] = TaskField.RESOURCE_ENTERPRISE_OUTLINE_CODE15;
      FIELD_ARRAY[1104] = TaskField.RESOURCE_ENTERPRISE_OUTLINE_CODE16;
      FIELD_ARRAY[1105] = TaskField.RESOURCE_ENTERPRISE_OUTLINE_CODE17;
      FIELD_ARRAY[1106] = TaskField.RESOURCE_ENTERPRISE_OUTLINE_CODE18;
      FIELD_ARRAY[1107] = TaskField.RESOURCE_ENTERPRISE_OUTLINE_CODE19;
      FIELD_ARRAY[1108] = TaskField.RESOURCE_ENTERPRISE_OUTLINE_CODE20;
      FIELD_ARRAY[1109] = TaskField.RESOURCE_ENTERPRISE_OUTLINE_CODE21;
      FIELD_ARRAY[1110] = TaskField.RESOURCE_ENTERPRISE_OUTLINE_CODE22;
      FIELD_ARRAY[1111] = TaskField.RESOURCE_ENTERPRISE_OUTLINE_CODE23;
      FIELD_ARRAY[1112] = TaskField.RESOURCE_ENTERPRISE_OUTLINE_CODE24;
      FIELD_ARRAY[1113] = TaskField.RESOURCE_ENTERPRISE_OUTLINE_CODE25;
      FIELD_ARRAY[1114] = TaskField.RESOURCE_ENTERPRISE_OUTLINE_CODE26;
      FIELD_ARRAY[1115] = TaskField.RESOURCE_ENTERPRISE_OUTLINE_CODE27;
      FIELD_ARRAY[1116] = TaskField.RESOURCE_ENTERPRISE_OUTLINE_CODE28;
      FIELD_ARRAY[1117] = TaskField.RESOURCE_ENTERPRISE_OUTLINE_CODE29;
      FIELD_ARRAY[1118] = TaskField.RESOURCE_ENTERPRISE_RBS;
      FIELD_ARRAY[1119] = TaskField.PHYSICAL_PERCENT_COMPLETE;
      FIELD_ARRAY[1120] = TaskField.REQUEST_DEMAND;
      FIELD_ARRAY[1121] = TaskField.STATUS_INDICATOR;
      FIELD_ARRAY[1122] = TaskField.EARNED_VALUE_METHOD;
      FIELD_ARRAY[1129] = TaskField.RESOURCE_ENTERPRISE_MULTI_VALUE_CODE20;
      FIELD_ARRAY[1130] = TaskField.RESOURCE_ENTERPRISE_MULTI_VALUE_CODE21;
      FIELD_ARRAY[1131] = TaskField.RESOURCE_ENTERPRISE_MULTI_VALUE_CODE22;
      FIELD_ARRAY[1132] = TaskField.RESOURCE_ENTERPRISE_MULTI_VALUE_CODE23;
      FIELD_ARRAY[1133] = TaskField.RESOURCE_ENTERPRISE_MULTI_VALUE_CODE24;
      FIELD_ARRAY[1134] = TaskField.RESOURCE_ENTERPRISE_MULTI_VALUE_CODE25;
      FIELD_ARRAY[1135] = TaskField.RESOURCE_ENTERPRISE_MULTI_VALUE_CODE26;
      FIELD_ARRAY[1136] = TaskField.RESOURCE_ENTERPRISE_MULTI_VALUE_CODE27;
      FIELD_ARRAY[1137] = TaskField.RESOURCE_ENTERPRISE_MULTI_VALUE_CODE28;
      FIELD_ARRAY[1138] = TaskField.RESOURCE_ENTERPRISE_MULTI_VALUE_CODE29;
      FIELD_ARRAY[1139] = TaskField.ACTUAL_WORK_PROTECTED;
      FIELD_ARRAY[1140] = TaskField.ACTUAL_OVERTIME_WORK_PROTECTED;
      FIELD_ARRAY[1143] = TaskField.GUID;
      FIELD_ARRAY[1144] = TaskField.TASK_CALENDAR_GUID;
      FIELD_ARRAY[1146] = TaskField.DELIVERABLE_GUID;
      FIELD_ARRAY[1147] = TaskField.DELIVERABLE_TYPE;
      FIELD_ARRAY[1152] = TaskField.DELIVERABLE_START;
      FIELD_ARRAY[1153] = TaskField.DELIVERABLE_FINISH;
      FIELD_ARRAY[1165] = TaskField.PUBLISH;
      FIELD_ARRAY[1166] = TaskField.STATUS_MANAGER;
      FIELD_ARRAY[1167] = TaskField.ERROR_MESSAGE;
      FIELD_ARRAY[1169] = TaskField.SUBPROJECT_GUID;
      FIELD_ARRAY[1170] = TaskField.ASSIGNMENT_OWNER;
      FIELD_ARRAY[1171] = TaskField.BUDGET_WORK;
      FIELD_ARRAY[1172] = TaskField.BUDGET_COST;
      FIELD_ARRAY[1173] = TaskField.BASELINE_FIXED_COST_ACCRUAL;
      FIELD_ARRAY[1174] = TaskField.BASELINE_DELIVERABLE_START;
      FIELD_ARRAY[1175] = TaskField.BASELINE_DELIVERABLE_FINISH;
      FIELD_ARRAY[1176] = TaskField.BASELINE_BUDGET_WORK;
      FIELD_ARRAY[1177] = TaskField.BASELINE_BUDGET_COST;
      FIELD_ARRAY[1180] = TaskField.BASELINE1_FIXED_COST_ACCRUAL;
      FIELD_ARRAY[1181] = TaskField.BASELINE1_DELIVERABLE_START;
      FIELD_ARRAY[1182] = TaskField.BASELINE1_DELIVERABLE_FINISH;
      FIELD_ARRAY[1183] = TaskField.BASELINE1_BUDGET_WORK;
      FIELD_ARRAY[1184] = TaskField.BASELINE1_BUDGET_COST;
      FIELD_ARRAY[1187] = TaskField.BASELINE2_FIXED_COST_ACCRUAL;
      FIELD_ARRAY[1188] = TaskField.BASELINE2_DELIVERABLE_START;
      FIELD_ARRAY[1189] = TaskField.BASELINE2_DELIVERABLE_FINISH;
      FIELD_ARRAY[1190] = TaskField.BASELINE2_BUDGET_WORK;
      FIELD_ARRAY[1191] = TaskField.BASELINE2_BUDGET_COST;
      FIELD_ARRAY[1194] = TaskField.BASELINE3_FIXED_COST_ACCRUAL;
      FIELD_ARRAY[1195] = TaskField.BASELINE3_DELIVERABLE_START;
      FIELD_ARRAY[1196] = TaskField.BASELINE3_DELIVERABLE_FINISH;
      FIELD_ARRAY[1197] = TaskField.BASELINE3_BUDGET_WORK;
      FIELD_ARRAY[1198] = TaskField.BASELINE3_BUDGET_COST;
      FIELD_ARRAY[1201] = TaskField.BASELINE4_FIXED_COST_ACCRUAL;
      FIELD_ARRAY[1202] = TaskField.BASELINE4_DELIVERABLE_START;
      FIELD_ARRAY[1203] = TaskField.BASELINE4_DELIVERABLE_FINISH;
      FIELD_ARRAY[1204] = TaskField.BASELINE4_BUDGET_WORK;
      FIELD_ARRAY[1205] = TaskField.BASELINE4_BUDGET_COST;
      FIELD_ARRAY[1208] = TaskField.BASELINE5_FIXED_COST_ACCRUAL;
      FIELD_ARRAY[1209] = TaskField.BASELINE5_DELIVERABLE_START;
      FIELD_ARRAY[1210] = TaskField.BASELINE5_DELIVERABLE_FINISH;
      FIELD_ARRAY[1211] = TaskField.BASELINE5_BUDGET_WORK;
      FIELD_ARRAY[1212] = TaskField.BASELINE5_BUDGET_COST;
      FIELD_ARRAY[1215] = TaskField.BASELINE6_FIXED_COST_ACCRUAL;
      FIELD_ARRAY[1216] = TaskField.BASELINE6_DELIVERABLE_START;
      FIELD_ARRAY[1217] = TaskField.BASELINE6_DELIVERABLE_FINISH;
      FIELD_ARRAY[1218] = TaskField.BASELINE6_BUDGET_WORK;
      FIELD_ARRAY[1219] = TaskField.BASELINE6_BUDGET_COST;
      FIELD_ARRAY[1222] = TaskField.BASELINE7_FIXED_COST_ACCRUAL;
      FIELD_ARRAY[1223] = TaskField.BASELINE7_DELIVERABLE_START;
      FIELD_ARRAY[1224] = TaskField.BASELINE7_DELIVERABLE_FINISH;
      FIELD_ARRAY[1225] = TaskField.BASELINE7_BUDGET_WORK;
      FIELD_ARRAY[1226] = TaskField.BASELINE7_BUDGET_COST;
      FIELD_ARRAY[1229] = TaskField.BASELINE8_FIXED_COST_ACCRUAL;
      FIELD_ARRAY[1230] = TaskField.BASELINE8_DELIVERABLE_START;
      FIELD_ARRAY[1231] = TaskField.BASELINE8_DELIVERABLE_FINISH;
      FIELD_ARRAY[1232] = TaskField.BASELINE8_BUDGET_WORK;
      FIELD_ARRAY[1233] = TaskField.BASELINE8_BUDGET_COST;
      FIELD_ARRAY[1236] = TaskField.BASELINE9_FIXED_COST_ACCRUAL;
      FIELD_ARRAY[1237] = TaskField.BASELINE9_DELIVERABLE_START;
      FIELD_ARRAY[1238] = TaskField.BASELINE9_DELIVERABLE_FINISH;
      FIELD_ARRAY[1239] = TaskField.BASELINE9_BUDGET_WORK;
      FIELD_ARRAY[1240] = TaskField.BASELINE9_BUDGET_COST;
      FIELD_ARRAY[1243] = TaskField.BASELINE10_FIXED_COST_ACCRUAL;
      FIELD_ARRAY[1244] = TaskField.BASELINE10_DELIVERABLE_START;
      FIELD_ARRAY[1245] = TaskField.BASELINE10_DELIVERABLE_FINISH;
      FIELD_ARRAY[1246] = TaskField.BASELINE10_BUDGET_WORK;
      FIELD_ARRAY[1247] = TaskField.BASELINE10_BUDGET_COST;
      FIELD_ARRAY[1250] = TaskField.RECALC_OUTLINE_CODES;
      FIELD_ARRAY[1276] = TaskField.DELIVERABLE_NAME;
      FIELD_ARRAY[1279] = TaskField.ACTIVE;
      FIELD_ARRAY[1280] = TaskField.TASK_MODE;
      FIELD_ARRAY[1281] = TaskField.PLACEHOLDER;
      FIELD_ARRAY[1282] = TaskField.WARNING;
      FIELD_ARRAY[1283] = TaskField.START; // Labelled "Task Start" in MPP14
      FIELD_ARRAY[1284] = TaskField.FINISH; // Labelled "Task Finish" in MPP14
      FIELD_ARRAY[1285] = TaskField.START_TEXT;
      FIELD_ARRAY[1286] = TaskField.FINISH_TEXT;
      FIELD_ARRAY[1287] = TaskField.DURATION_TEXT;
      FIELD_ARRAY[1288] = TaskField.MANUAL_DURATION;
      FIELD_ARRAY[1289] = TaskField.MANUAL_DURATION_UNITS;
      FIELD_ARRAY[1295] = TaskField.IS_START_VALID;
      FIELD_ARRAY[1296] = TaskField.IS_FINISH_VALID;
      FIELD_ARRAY[1297] = TaskField.IS_DURATION_VALID;
      FIELD_ARRAY[1299] = TaskField.BASELINE_START;
      FIELD_ARRAY[1300] = TaskField.BASELINE_FINISH;
      FIELD_ARRAY[1301] = TaskField.BASELINE_DURATION;
      FIELD_ARRAY[1302] = TaskField.BASELINE1_START;
      FIELD_ARRAY[1303] = TaskField.BASELINE1_FINISH;
      FIELD_ARRAY[1304] = TaskField.BASELINE1_DURATION;
      FIELD_ARRAY[1305] = TaskField.BASELINE2_START;
      FIELD_ARRAY[1306] = TaskField.BASELINE2_FINISH;
      FIELD_ARRAY[1307] = TaskField.BASELINE2_DURATION;
      FIELD_ARRAY[1308] = TaskField.BASELINE3_START;
      FIELD_ARRAY[1309] = TaskField.BASELINE3_FINISH;
      FIELD_ARRAY[1310] = TaskField.BASELINE3_DURATION;
      FIELD_ARRAY[1311] = TaskField.BASELINE4_START;
      FIELD_ARRAY[1312] = TaskField.BASELINE4_FINISH;
      FIELD_ARRAY[1313] = TaskField.BASELINE4_DURATION;
      FIELD_ARRAY[1314] = TaskField.BASELINE5_START;
      FIELD_ARRAY[1315] = TaskField.BASELINE5_FINISH;
      FIELD_ARRAY[1316] = TaskField.BASELINE5_DURATION;
      FIELD_ARRAY[1317] = TaskField.BASELINE6_START;
      FIELD_ARRAY[1318] = TaskField.BASELINE6_FINISH;
      FIELD_ARRAY[1319] = TaskField.BASELINE6_DURATION;
      FIELD_ARRAY[1320] = TaskField.BASELINE7_START;
      FIELD_ARRAY[1321] = TaskField.BASELINE7_FINISH;
      FIELD_ARRAY[1322] = TaskField.BASELINE7_DURATION;
      FIELD_ARRAY[1323] = TaskField.BASELINE8_START;
      FIELD_ARRAY[1324] = TaskField.BASELINE8_FINISH;
      FIELD_ARRAY[1325] = TaskField.BASELINE8_DURATION;
      FIELD_ARRAY[1326] = TaskField.BASELINE9_START;
      FIELD_ARRAY[1327] = TaskField.BASELINE9_FINISH;
      FIELD_ARRAY[1328] = TaskField.BASELINE9_DURATION;
      FIELD_ARRAY[1329] = TaskField.BASELINE10_START;
      FIELD_ARRAY[1330] = TaskField.BASELINE10_FINISH;
      FIELD_ARRAY[1331] = TaskField.BASELINE10_DURATION;
      FIELD_ARRAY[1332] = TaskField.IGNORE_WARNINGS;
      FIELD_ARRAY[1335] = TaskField.PEAK;
      FIELD_ARRAY[1338] = TaskField.SCHEDULED_START;
      FIELD_ARRAY[1339] = TaskField.SCHEDULED_FINISH;
      FIELD_ARRAY[1340] = TaskField.SCHEDULED_DURATION;
      FIELD_ARRAY[1381] = TaskField.PATH_DRIVING_PREDECESSOR;
      FIELD_ARRAY[1382] = TaskField.PATH_PREDECESSOR;
      FIELD_ARRAY[1383] = TaskField.PATH_DRIVEN_SUCCESSOR;
      FIELD_ARRAY[1384] = TaskField.PATH_SUCCESSOR;
      FIELD_ARRAY[1405] = TaskField.TASK_SUMMARY_NAME;
      FIELD_ARRAY[1407] = TaskField.BOARD_STATUS;
      FIELD_ARRAY[1408] = TaskField.SHOW_ON_BOARD;
      FIELD_ARRAY[1409] = TaskField.SPRINT;
      FIELD_ARRAY[1410] = TaskField.SPRINT_START;
      FIELD_ARRAY[1411] = TaskField.SPRINT_FINISH;
      FIELD_ARRAY[1412] = TaskField.BOARD_STATUS_ID;
      FIELD_ARRAY[1413] = TaskField.SPRINT_ID;
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

   public static final int TASK_FIELD_BASE = 0x0B400000;
}
