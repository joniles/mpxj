/*
 * file:       MPPTaskField.java
 * author:     Jon Iles
 * copyright:  (c) Tapster Rock Limited 2005
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

package net.sf.mpxj.mpp;

import net.sf.mpxj.TaskField;

/**
 * Utility class used to map between the integer values held in MS Project
 * to represent a task field, and the enumerated type used to represent
 * task fields in MPXJ.
 */
final class MPPTaskField
{
   /**
    * Retrieve an instance of the TaskField class based on the data read from an
    * MS Project file.
    *
    * @param value value from an MS Project file
    * @return TaskField instance
    */
   public static TaskField getInstance (int value)
   {
      TaskField result = null;

      if (value >=0 && value < FIELD_ARRAY.length)
      {
         result = FIELD_ARRAY[value];
      }

      return (result);
   }
   
   private static final int WORK_VALUE = 0;
   private static final int BASELINE_WORK_VALUE = 1;
   private static final int ACTUAL_WORK_VALUE = 2;
   private static final int WORK_VARIANCE_VALUE = 3;
   private static final int REMAINING_WORK_VALUE = 4;
   private static final int COST_VALUE = 5;
   private static final int BASELINE_COST_VALUE = 6;
   private static final int ACTUAL_COST_VALUE = 7;
   private static final int FIXED_COST_VALUE = 8;
   private static final int COST_VARIANCE_VALUE = 9;
   private static final int REMAINING_COST_VALUE = 10;
   private static final int BCWP_VALUE = 11;
   private static final int BCWS_VALUE = 12;
   private static final int SV_VALUE = 13;
   private static final int NAME_VALUE = 14;
   private static final int WBS_VALUE = 16;
   private static final int CONSTRAINT_TYPE_VALUE = 17;
   private static final int CONSTRAINT_DATE_VALUE = 18;
   private static final int CRITICAL_VALUE = 19;
   private static final int LEVELING_DELAY_VALUE = 20;
   private static final int FREE_SLACK_VALUE = 21;
   private static final int TOTAL_SLACK_VALUE = 22;
   private static final int ID_VALUE = 23;
   private static final int MILESTONE_VALUE = 24;
   private static final int PRIORITY_VALUE = 25;
   private static final int SUBPROJECT_FILE_VALUE = 26;
   private static final int BASELINE_DURATION_VALUE = 27;
   private static final int ACTUAL_DURATION_VALUE = 28;
   private static final int DURATION_VALUE = 29;
   private static final int DURATION_VARIANCE_VALUE = 30;
   private static final int REMAINING_DURATION_VALUE = 31;
   private static final int PERCENT_COMPLETE_VALUE = 32;
   private static final int PERCENT_WORK_COMPLETE_VALUE = 33;
   private static final int START_VALUE = 35;
   private static final int FINISH_VALUE = 36;
   private static final int EARLY_START_VALUE = 37;
   private static final int EARLY_FINISH_VALUE = 38;
   private static final int LATE_START_VALUE = 39;
   private static final int LATE_FINISH_VALUE = 40;
   private static final int ACTUAL_START_VALUE = 41;
   private static final int ACTUAL_FINISH_VALUE = 42;
   private static final int BASELINE_START_VALUE = 43;
   private static final int BASELINE_FINISH_VALUE = 44;
   private static final int START_VARIANCE_VALUE = 45;
   private static final int FINISH_VARIANCE_VALUE = 46;
   private static final int PREDECESSORS_VALUE = 47;
   private static final int SUCCESSORS_VALUE = 48;
   private static final int RESOURCE_NAMES_VALUE = 49;
   private static final int RESOURCE_INITIALS_VALUE = 50;
   private static final int TEXT1_VALUE = 51;
   private static final int START1_VALUE = 52;
   private static final int FINISH1_VALUE = 53;
   private static final int TEXT2_VALUE = 54;
   private static final int START2_VALUE = 55;
   private static final int FINISH2_VALUE = 56;
   private static final int TEXT3_VALUE = 57;
   private static final int START3_VALUE = 58;
   private static final int FINISH3_VALUE = 59;
   private static final int TEXT4_VALUE = 60;
   private static final int START4_VALUE = 61;
   private static final int FINISH4_VALUE = 62;
   private static final int TEXT5_VALUE = 63;
   private static final int START5_VALUE = 64;
   private static final int FINISH5_VALUE = 65;
   private static final int TEXT6_VALUE = 66;
   private static final int TEXT7_VALUE = 67;
   private static final int TEXT8_VALUE = 68;
   private static final int TEXT9_VALUE = 69;
   private static final int TEXT10_VALUE = 70;
   private static final int MARKED_VALUE = 71;
   private static final int FLAG1_VALUE = 72;
   private static final int FLAG2_VALUE = 73;
   private static final int FLAG3_VALUE = 74;
   private static final int FLAG4_VALUE = 75;
   private static final int FLAG5_VALUE = 76;
   private static final int FLAG6_VALUE = 77;
   private static final int FLAG7_VALUE = 78;
   private static final int FLAG8_VALUE = 79;
   private static final int FLAG9_VALUE = 80;
   private static final int FLAG10_VALUE = 81;
   private static final int ROLLUP_VALUE = 82;
   private static final int CV_VALUE = 83;
   private static final int PROJECT_VALUE = 84;
   private static final int OUTLINE_LEVEL_VALUE = 85;
   private static final int UNIQUE_ID_VALUE = 86;
   private static final int NUMBER1_VALUE = 87;
   private static final int NUMBER2_VALUE = 88;
   private static final int NUMBER3_VALUE = 89;
   private static final int NUMBER4_VALUE = 90;
   private static final int NUMBER5_VALUE = 91;
   private static final int SUMMARY_VALUE = 92;
   private static final int CREATED_VALUE = 93;
   private static final int NOTES_VALUE = 94;
   private static final int UNIQUE_ID_PREDECESSORS_VALUE = 95;
   private static final int UNIQUE_ID_SUCCESSORS_VALUE = 96;
   private static final int OBJECTS_VALUE = 97;
   private static final int LINKED_FIELDS_VALUE = 98;
   private static final int RESUME_VALUE = 99;
   private static final int STOP_VALUE = 100;
   private static final int OUTLINE_NUMBER_VALUE = 102;
   private static final int DURATION1_VALUE = 103;
   private static final int DURATION2_VALUE = 104;
   private static final int DURATION3_VALUE = 105;
   private static final int COST1_VALUE = 106;
   private static final int COST2_VALUE = 107;
   private static final int COST3_VALUE = 108;
   private static final int HIDEBAR_VALUE = 109;
   private static final int CONFIRMED_VALUE = 110;
   private static final int UPDATE_NEEDED_VALUE = 111;
   private static final int CONTACT_VALUE = 112;
   private static final int RESOURCE_GROUP_VALUE = 113;
   private static final int ACWP_VALUE = 120;
   private static final int TYPE_VALUE = 128;
   private static final int RECURRING_VALUE = 129;
   private static final int EFFORT_DRIVEN_VALUE = 132;
   private static final int OVERTIME_WORK_VALUE = 163;
   private static final int ACTUAL_OVERTIME_WORK_VALUE = 164;
   private static final int REMAINING_OVERTIME_WORK_VALUE = 165;
   private static final int REGULAR_WORK_VALUE = 166;
   private static final int OVERTIME_COST_VALUE = 168;
   private static final int ACTUAL_OVERTIME_COST_VALUE = 169;
   private static final int REMAINING_OVERTIME_COST_VALUE = 170;
   private static final int FIXED_COST_ACCRUAL_VALUE = 200;
   private static final int INDICATORS_VALUE = 205;
   private static final int HYPERLINK_VALUE = 217;
   private static final int HYPERLINK_ADDRESS_VALUE = 218;
   private static final int HYPERLINK_SUBADDRESS_VALUE = 219;
   private static final int HYPERLINK_HREF_VALUE = 220;
   private static final int ASSIGNMENT_VALUE = 224;
   private static final int OVERALLOCATED_VALUE = 225;
   private static final int EXTERNAL_TASK_VALUE = 232;
   private static final int SUBPROJECT_READ_ONLY_VALUE = 246;
   private static final int RESPONSE_PENDING_VALUE = 250;
   private static final int TEAMSTATUS_PENDING_VALUE = 251;
   private static final int LEVELING_CAN_SPLIT_VALUE = 252;
   private static final int LEVEL_ASSIGNMENTS_VALUE = 253;
   private static final int WORK_CONTOUR_VALUE = 256;
   private static final int COST4_VALUE = 258;
   private static final int COST5_VALUE = 259;
   private static final int COST6_VALUE = 260;
   private static final int COST7_VALUE = 261;
   private static final int COST8_VALUE = 262;
   private static final int COST9_VALUE = 263;
   private static final int COST10_VALUE = 264;
   private static final int DATE1_VALUE = 265;
   private static final int DATE2_VALUE = 266;
   private static final int DATE3_VALUE = 267;
   private static final int DATE4_VALUE = 268;
   private static final int DATE5_VALUE = 269;
   private static final int DATE6_VALUE = 260;
   private static final int DATE7_VALUE = 271;
   private static final int DATE8_VALUE = 272;
   private static final int DATE9_VALUE = 273;
   private static final int DATE10_VALUE = 274;
   private static final int DURATION4_VALUE = 275;
   private static final int DURATION5_VALUE = 276;
   private static final int DURATION6_VALUE = 277;
   private static final int DURATION7_VALUE = 278;
   private static final int DURATION8_VALUE = 279;
   private static final int DURATION9_VALUE = 280;
   private static final int DURATION10_VALUE = 281;
   private static final int START6_VALUE = 282;
   private static final int FINISH6_VALUE = 283;
   private static final int START7_VALUE = 284;
   private static final int FINISH7_VALUE = 285;
   private static final int START8_VALUE = 286;
   private static final int FINISH8_VALUE = 287;
   private static final int START9_VALUE = 288;
   private static final int FINISH9_VALUE = 289;
   private static final int START10_VALUE = 290;
   private static final int FINISH10_VALUE = 291;
   private static final int FLAG11_VALUE = 292;
   private static final int FLAG12_VALUE = 293;
   private static final int FLAG13_VALUE = 294;
   private static final int FLAG14_VALUE = 295;
   private static final int FLAG15_VALUE = 296;
   private static final int FLAG16_VALUE = 297;
   private static final int FLAG17_VALUE = 298;
   private static final int FLAG18_VALUE = 299;
   private static final int FLAG19_VALUE = 300;
   private static final int FLAG20_VALUE = 301;
   private static final int NUMBER6_VALUE = 302;
   private static final int NUMBER7_VALUE = 303;
   private static final int NUMBER8_VALUE = 304;
   private static final int NUMBER9_VALUE = 305;
   private static final int NUMBER10_VALUE = 306;
   private static final int NUMBER11_VALUE = 307;
   private static final int NUMBER12_VALUE = 308;
   private static final int NUMBER13_VALUE = 309;
   private static final int NUMBER14_VALUE = 310;
   private static final int NUMBER15_VALUE = 311;
   private static final int NUMBER16_VALUE = 312;
   private static final int NUMBER17_VALUE = 313;
   private static final int NUMBER18_VALUE = 314;
   private static final int NUMBER19_VALUE = 315;
   private static final int NUMBER20_VALUE = 316;
   private static final int TEXT11_VALUE = 317;
   private static final int TEXT12_VALUE = 318;
   private static final int TEXT13_VALUE = 319;
   private static final int TEXT14_VALUE = 320;
   private static final int TEXT15_VALUE = 321;
   private static final int TEXT16_VALUE = 322;
   private static final int TEXT17_VALUE = 323;
   private static final int TEXT18_VALUE = 324;
   private static final int TEXT19_VALUE = 325;
   private static final int TEXT20_VALUE = 326;
   private static final int TEXT21_VALUE = 327;
   private static final int TEXT22_VALUE = 328;
   private static final int TEXT23_VALUE = 329;
   private static final int TEXT24_VALUE = 330;
   private static final int TEXT25_VALUE = 331;
   private static final int TEXT26_VALUE = 332;
   private static final int TEXT27_VALUE = 333;
   private static final int TEXT28_VALUE = 334;
   private static final int TEXT29_VALUE = 335;
   private static final int TEXT30_VALUE = 336;
   private static final int RESOURCE_PHONETICS_VALUE = 349;
   private static final int ASSIGNMENT_DELAY_VALUE = 366;
   private static final int ASSIGNMENT_UNITS_VALUE = 367;
   private static final int COST_RATE_TABLE_VALUE = 368;
   private static final int PRELEVELED_START_VALUE = 369;
   private static final int PRELEVELED_FINISH_VALUE = 370;
   private static final int ESTIMATED_VALUE = 396;
   private static final int IGNORE_RESOURCE_CALENDAR_VALUE = 399;
   private static final int CALENDAR_VALUE = 402;
   private static final int OUTLINE_CODE1_VALUE = 416;
   private static final int OUTLINE_CODE2_VALUE = 418;
   private static final int OUTLINE_CODE3_VALUE = 420;
   private static final int OUTLINE_CODE4_VALUE = 422;
   private static final int OUTLINE_CODE5_VALUE = 424;
   private static final int OUTLINE_CODE6_VALUE = 426;
   private static final int OUTLINE_CODE7_VALUE = 428;
   private static final int OUTLINE_CODE8_VALUE = 430;
   private static final int OUTLINE_CODE9_VALUE = 432;
   private static final int OUTLINE_CODE10_VALUE = 434;
   private static final int DEADLINE_VALUE = 437;
   private static final int START_SLACK_VALUE = 438;
   private static final int FINISH_SLACK_VALUE = 439;
   private static final int VAC_VALUE = 441;
   private static final int GROUP_BY_SUMMARY_VALUE = 446;
   private static final int WBS_PREDECESSORS_VALUE = 449;
   private static final int WBS_SUCCESSORS_VALUE = 450;
   private static final int RESOURCE_TYPE_VALUE = 451;

   private static final int MAX_VALUE = 452;
   
   private static final TaskField[] FIELD_ARRAY = new TaskField[MAX_VALUE];

   static
   {
      FIELD_ARRAY[WORK_VALUE] = TaskField.WORK;
      FIELD_ARRAY[BASELINE_WORK_VALUE] = TaskField.BASELINE_WORK;
      FIELD_ARRAY[ACTUAL_WORK_VALUE] = TaskField.ACTUAL_WORK;
      FIELD_ARRAY[WORK_VARIANCE_VALUE] = TaskField.WORK_VARIANCE;
      FIELD_ARRAY[REMAINING_WORK_VALUE] = TaskField.REMAINING_WORK;
      FIELD_ARRAY[COST_VALUE] = TaskField.COST;
      FIELD_ARRAY[BASELINE_COST_VALUE] = TaskField.BASELINE_COST;
      FIELD_ARRAY[ACTUAL_COST_VALUE] = TaskField.ACTUAL_COST;
      FIELD_ARRAY[FIXED_COST_VALUE] = TaskField.FIXED_COST;
      FIELD_ARRAY[COST_VARIANCE_VALUE] = TaskField.COST_VARIANCE;
      FIELD_ARRAY[REMAINING_COST_VALUE] = TaskField.REMAINING_COST;
      FIELD_ARRAY[BCWP_VALUE] = TaskField.BCWP;
      FIELD_ARRAY[BCWS_VALUE] = TaskField.BCWS;
      FIELD_ARRAY[SV_VALUE] = TaskField.SV;
      FIELD_ARRAY[NAME_VALUE] = TaskField.NAME;
      FIELD_ARRAY[WBS_VALUE] = TaskField.WBS;
      FIELD_ARRAY[CONSTRAINT_TYPE_VALUE] = TaskField.CONSTRAINT_TYPE;
      FIELD_ARRAY[CONSTRAINT_DATE_VALUE] = TaskField.CONSTRAINT_DATE;
      FIELD_ARRAY[CRITICAL_VALUE] = TaskField.CRITICAL;
      FIELD_ARRAY[LEVELING_DELAY_VALUE] = TaskField.LEVELING_DELAY;
      FIELD_ARRAY[FREE_SLACK_VALUE] = TaskField.FREE_SLACK;
      FIELD_ARRAY[TOTAL_SLACK_VALUE] = TaskField.TOTAL_SLACK;
      FIELD_ARRAY[ID_VALUE] = TaskField.ID;
      FIELD_ARRAY[MILESTONE_VALUE] = TaskField.MILESTONE;
      FIELD_ARRAY[PRIORITY_VALUE] = TaskField.PRIORITY;
      FIELD_ARRAY[SUBPROJECT_FILE_VALUE] = TaskField.SUBPROJECT_FILE;
      FIELD_ARRAY[BASELINE_DURATION_VALUE] = TaskField.BASELINE_DURATION;
      FIELD_ARRAY[ACTUAL_DURATION_VALUE] = TaskField.ACTUAL_DURATION;
      FIELD_ARRAY[DURATION_VALUE] = TaskField.DURATION;
      FIELD_ARRAY[DURATION_VARIANCE_VALUE] = TaskField.DURATION_VARIANCE;
      FIELD_ARRAY[REMAINING_DURATION_VALUE] = TaskField.REMAINING_DURATION;
      FIELD_ARRAY[PERCENT_COMPLETE_VALUE] = TaskField.PERCENT_COMPLETE;
      FIELD_ARRAY[PERCENT_WORK_COMPLETE_VALUE] = TaskField.PERCENT_WORK_COMPLETE;
      FIELD_ARRAY[START_VALUE] = TaskField.START;
      FIELD_ARRAY[FINISH_VALUE] = TaskField.FINISH;
      FIELD_ARRAY[EARLY_START_VALUE] = TaskField.EARLY_START;
      FIELD_ARRAY[EARLY_FINISH_VALUE] = TaskField.EARLY_FINISH;
      FIELD_ARRAY[LATE_START_VALUE] = TaskField.LATE_START;
      FIELD_ARRAY[LATE_FINISH_VALUE] = TaskField.LATE_FINISH;
      FIELD_ARRAY[ACTUAL_START_VALUE] = TaskField.ACTUAL_START;
      FIELD_ARRAY[ACTUAL_FINISH_VALUE] = TaskField.ACTUAL_FINISH;
      FIELD_ARRAY[BASELINE_START_VALUE] = TaskField.BASELINE_START;
      FIELD_ARRAY[BASELINE_FINISH_VALUE] = TaskField.BASELINE_FINISH;
      FIELD_ARRAY[START_VARIANCE_VALUE] = TaskField.START_VARIANCE;
      FIELD_ARRAY[FINISH_VARIANCE_VALUE] = TaskField.FINISH_VARIANCE;
      FIELD_ARRAY[PREDECESSORS_VALUE] = TaskField.PREDECESSORS;
      FIELD_ARRAY[SUCCESSORS_VALUE] = TaskField.SUCCESSORS;
      FIELD_ARRAY[RESOURCE_NAMES_VALUE] = TaskField.RESOURCE_NAMES;
      FIELD_ARRAY[RESOURCE_INITIALS_VALUE] = TaskField.RESOURCE_INITIALS;
      FIELD_ARRAY[TEXT1_VALUE] = TaskField.TEXT1;
      FIELD_ARRAY[START1_VALUE] = TaskField.START1;
      FIELD_ARRAY[FINISH1_VALUE] = TaskField.FINISH1;
      FIELD_ARRAY[TEXT2_VALUE] = TaskField.TEXT2;
      FIELD_ARRAY[START2_VALUE] = TaskField.START2;
      FIELD_ARRAY[FINISH2_VALUE] = TaskField.FINISH2;
      FIELD_ARRAY[TEXT3_VALUE] = TaskField.TEXT3;
      FIELD_ARRAY[START3_VALUE] = TaskField.START3;
      FIELD_ARRAY[FINISH3_VALUE] = TaskField.FINISH3;
      FIELD_ARRAY[TEXT4_VALUE] = TaskField.TEXT4;
      FIELD_ARRAY[START4_VALUE] = TaskField.START4;
      FIELD_ARRAY[FINISH4_VALUE] = TaskField.FINISH4;
      FIELD_ARRAY[TEXT5_VALUE] = TaskField.TEXT5;
      FIELD_ARRAY[START5_VALUE] = TaskField.START5;
      FIELD_ARRAY[FINISH5_VALUE] = TaskField.FINISH5;
      FIELD_ARRAY[TEXT6_VALUE] = TaskField.TEXT6;
      FIELD_ARRAY[TEXT7_VALUE] = TaskField.TEXT7;
      FIELD_ARRAY[TEXT8_VALUE] = TaskField.TEXT8;
      FIELD_ARRAY[TEXT9_VALUE] = TaskField.TEXT9;
      FIELD_ARRAY[TEXT10_VALUE] = TaskField.TEXT10;
      FIELD_ARRAY[MARKED_VALUE] = TaskField.MARKED;
      FIELD_ARRAY[FLAG1_VALUE] = TaskField.FLAG1;
      FIELD_ARRAY[FLAG2_VALUE] = TaskField.FLAG2;
      FIELD_ARRAY[FLAG3_VALUE] = TaskField.FLAG3;
      FIELD_ARRAY[FLAG4_VALUE] = TaskField.FLAG4;
      FIELD_ARRAY[FLAG5_VALUE] = TaskField.FLAG5;
      FIELD_ARRAY[FLAG6_VALUE] = TaskField.FLAG6;
      FIELD_ARRAY[FLAG7_VALUE] = TaskField.FLAG7;
      FIELD_ARRAY[FLAG8_VALUE] = TaskField.FLAG8;
      FIELD_ARRAY[FLAG9_VALUE] = TaskField.FLAG9;
      FIELD_ARRAY[FLAG10_VALUE] = TaskField.FLAG10;
      FIELD_ARRAY[ROLLUP_VALUE] = TaskField.ROLLUP;
      FIELD_ARRAY[CV_VALUE] = TaskField.CV;
      FIELD_ARRAY[PROJECT_VALUE] = TaskField.PROJECT;
      FIELD_ARRAY[OUTLINE_LEVEL_VALUE] = TaskField.OUTLINE_LEVEL;
      FIELD_ARRAY[UNIQUE_ID_VALUE] = TaskField.UNIQUE_ID;
      FIELD_ARRAY[NUMBER1_VALUE] = TaskField.NUMBER1;
      FIELD_ARRAY[NUMBER2_VALUE] = TaskField.NUMBER2;
      FIELD_ARRAY[NUMBER3_VALUE] = TaskField.NUMBER3;
      FIELD_ARRAY[NUMBER4_VALUE] = TaskField.NUMBER4;
      FIELD_ARRAY[NUMBER5_VALUE] = TaskField.NUMBER5;
      FIELD_ARRAY[SUMMARY_VALUE] = TaskField.SUMMARY;
      FIELD_ARRAY[CREATED_VALUE] = TaskField.CREATED;
      FIELD_ARRAY[NOTES_VALUE] = TaskField.NOTES;
      FIELD_ARRAY[UNIQUE_ID_PREDECESSORS_VALUE] = TaskField.UNIQUE_ID_PREDECESSORS;
      FIELD_ARRAY[UNIQUE_ID_SUCCESSORS_VALUE] = TaskField.UNIQUE_ID_SUCCESSORS;
      FIELD_ARRAY[OBJECTS_VALUE] = TaskField.OBJECTS;
      FIELD_ARRAY[LINKED_FIELDS_VALUE] = TaskField.LINKED_FIELDS;
      FIELD_ARRAY[RESUME_VALUE] = TaskField.RESUME;
      FIELD_ARRAY[STOP_VALUE] = TaskField.STOP;
      FIELD_ARRAY[OUTLINE_NUMBER_VALUE] = TaskField.OUTLINE_NUMBER;
      FIELD_ARRAY[DURATION1_VALUE] = TaskField.DURATION1;
      FIELD_ARRAY[DURATION2_VALUE] = TaskField.DURATION2;
      FIELD_ARRAY[DURATION3_VALUE] = TaskField.DURATION3;
      FIELD_ARRAY[COST1_VALUE] = TaskField.COST1;
      FIELD_ARRAY[COST2_VALUE] = TaskField.COST2;
      FIELD_ARRAY[COST3_VALUE] = TaskField.COST3;
      FIELD_ARRAY[HIDEBAR_VALUE] = TaskField.HIDEBAR;
      FIELD_ARRAY[CONFIRMED_VALUE] = TaskField.CONFIRMED;
      FIELD_ARRAY[UPDATE_NEEDED_VALUE] = TaskField.UPDATE_NEEDED;
      FIELD_ARRAY[CONTACT_VALUE] = TaskField.CONTACT;
      FIELD_ARRAY[RESOURCE_GROUP_VALUE] = TaskField.RESOURCE_GROUP;
      FIELD_ARRAY[ACWP_VALUE] = TaskField.ACWP;
      FIELD_ARRAY[TYPE_VALUE] = TaskField.TYPE;
      FIELD_ARRAY[RECURRING_VALUE] = TaskField.RECURRING;
      FIELD_ARRAY[EFFORT_DRIVEN_VALUE] = TaskField.EFFORT_DRIVEN;
      FIELD_ARRAY[OVERTIME_WORK_VALUE] = TaskField.OVERTIME_WORK;
      FIELD_ARRAY[ACTUAL_OVERTIME_WORK_VALUE] = TaskField.ACTUAL_OVERTIME_WORK;
      FIELD_ARRAY[REMAINING_OVERTIME_WORK_VALUE] = TaskField.REMAINING_OVERTIME_WORK;
      FIELD_ARRAY[REGULAR_WORK_VALUE] = TaskField.REGULAR_WORK;
      FIELD_ARRAY[OVERTIME_COST_VALUE] = TaskField.OVERTIME_COST;
      FIELD_ARRAY[ACTUAL_OVERTIME_COST_VALUE] = TaskField.ACTUAL_OVERTIME_COST;
      FIELD_ARRAY[REMAINING_OVERTIME_COST_VALUE] = TaskField.REMAINING_OVERTIME_COST;
      FIELD_ARRAY[FIXED_COST_ACCRUAL_VALUE] = TaskField.FIXED_COST_ACCRUAL;
      FIELD_ARRAY[INDICATORS_VALUE] = TaskField.INDICATORS;
      FIELD_ARRAY[HYPERLINK_VALUE] = TaskField.HYPERLINK;
      FIELD_ARRAY[HYPERLINK_ADDRESS_VALUE] = TaskField.HYPERLINK_ADDRESS;
      FIELD_ARRAY[HYPERLINK_SUBADDRESS_VALUE] = TaskField.HYPERLINK_SUBADDRESS;
      FIELD_ARRAY[HYPERLINK_HREF_VALUE] = TaskField.HYPERLINK_HREF;
      FIELD_ARRAY[ASSIGNMENT_VALUE] = TaskField.ASSIGNMENT;
      FIELD_ARRAY[OVERALLOCATED_VALUE] = TaskField.OVERALLOCATED;
      FIELD_ARRAY[EXTERNAL_TASK_VALUE] = TaskField.EXTERNAL_TASK;
      FIELD_ARRAY[SUBPROJECT_READ_ONLY_VALUE] = TaskField.SUBPROJECT_READ_ONLY;
      FIELD_ARRAY[RESPONSE_PENDING_VALUE] = TaskField.RESPONSE_PENDING;
      FIELD_ARRAY[TEAMSTATUS_PENDING_VALUE] = TaskField.TEAMSTATUS_PENDING;
      FIELD_ARRAY[LEVELING_CAN_SPLIT_VALUE] = TaskField.LEVELING_CAN_SPLIT;
      FIELD_ARRAY[LEVEL_ASSIGNMENTS_VALUE] = TaskField.LEVEL_ASSIGNMENTS;
      FIELD_ARRAY[WORK_CONTOUR_VALUE] = TaskField.WORK_CONTOUR;
      FIELD_ARRAY[COST4_VALUE] = TaskField.COST4;
      FIELD_ARRAY[COST5_VALUE] = TaskField.COST5;
      FIELD_ARRAY[COST6_VALUE] = TaskField.COST6;
      FIELD_ARRAY[COST7_VALUE] = TaskField.COST7;
      FIELD_ARRAY[COST8_VALUE] = TaskField.COST8;
      FIELD_ARRAY[COST9_VALUE] = TaskField.COST9;
      FIELD_ARRAY[COST10_VALUE] = TaskField.COST10;
      FIELD_ARRAY[DATE1_VALUE] = TaskField.DATE1;
      FIELD_ARRAY[DATE2_VALUE] = TaskField.DATE2;
      FIELD_ARRAY[DATE3_VALUE] = TaskField.DATE3;
      FIELD_ARRAY[DATE4_VALUE] = TaskField.DATE4;
      FIELD_ARRAY[DATE5_VALUE] = TaskField.DATE5;
      FIELD_ARRAY[DATE6_VALUE] = TaskField.DATE6;
      FIELD_ARRAY[DATE7_VALUE] = TaskField.DATE7;
      FIELD_ARRAY[DATE8_VALUE] = TaskField.DATE8;
      FIELD_ARRAY[DATE9_VALUE] = TaskField.DATE9;
      FIELD_ARRAY[DATE10_VALUE] = TaskField.DATE10;
      FIELD_ARRAY[DURATION4_VALUE] = TaskField.DURATION4;
      FIELD_ARRAY[DURATION5_VALUE] = TaskField.DURATION5;
      FIELD_ARRAY[DURATION6_VALUE] = TaskField.DURATION6;
      FIELD_ARRAY[DURATION7_VALUE] = TaskField.DURATION7;
      FIELD_ARRAY[DURATION8_VALUE] = TaskField.DURATION8;
      FIELD_ARRAY[DURATION9_VALUE] = TaskField.DURATION9;
      FIELD_ARRAY[DURATION10_VALUE] = TaskField.DURATION10;
      FIELD_ARRAY[START6_VALUE] = TaskField.START6;
      FIELD_ARRAY[FINISH6_VALUE] = TaskField.FINISH6;
      FIELD_ARRAY[START7_VALUE] = TaskField.START7;
      FIELD_ARRAY[FINISH7_VALUE] = TaskField.FINISH7;
      FIELD_ARRAY[START8_VALUE] = TaskField.START8;
      FIELD_ARRAY[FINISH8_VALUE] = TaskField.FINISH8;
      FIELD_ARRAY[START9_VALUE] = TaskField.START9;
      FIELD_ARRAY[FINISH9_VALUE] = TaskField.FINISH9;
      FIELD_ARRAY[START10_VALUE] = TaskField.START10;
      FIELD_ARRAY[FINISH10_VALUE] = TaskField.FINISH10;
      FIELD_ARRAY[FLAG11_VALUE] = TaskField.FLAG11;
      FIELD_ARRAY[FLAG12_VALUE] = TaskField.FLAG12;
      FIELD_ARRAY[FLAG13_VALUE] = TaskField.FLAG13;
      FIELD_ARRAY[FLAG14_VALUE] = TaskField.FLAG14;
      FIELD_ARRAY[FLAG15_VALUE] = TaskField.FLAG15;
      FIELD_ARRAY[FLAG16_VALUE] = TaskField.FLAG16;
      FIELD_ARRAY[FLAG17_VALUE] = TaskField.FLAG17;
      FIELD_ARRAY[FLAG18_VALUE] = TaskField.FLAG18;
      FIELD_ARRAY[FLAG19_VALUE] = TaskField.FLAG19;
      FIELD_ARRAY[FLAG20_VALUE] = TaskField.FLAG20;
      FIELD_ARRAY[NUMBER6_VALUE] = TaskField.NUMBER6;
      FIELD_ARRAY[NUMBER7_VALUE] = TaskField.NUMBER7;
      FIELD_ARRAY[NUMBER8_VALUE] = TaskField.NUMBER8;
      FIELD_ARRAY[NUMBER9_VALUE] = TaskField.NUMBER9;
      FIELD_ARRAY[NUMBER10_VALUE] = TaskField.NUMBER10;
      FIELD_ARRAY[NUMBER11_VALUE] = TaskField.NUMBER11;
      FIELD_ARRAY[NUMBER12_VALUE] = TaskField.NUMBER12;
      FIELD_ARRAY[NUMBER13_VALUE] = TaskField.NUMBER13;
      FIELD_ARRAY[NUMBER14_VALUE] = TaskField.NUMBER14;
      FIELD_ARRAY[NUMBER15_VALUE] = TaskField.NUMBER15;
      FIELD_ARRAY[NUMBER16_VALUE] = TaskField.NUMBER16;
      FIELD_ARRAY[NUMBER17_VALUE] = TaskField.NUMBER17;
      FIELD_ARRAY[NUMBER18_VALUE] = TaskField.NUMBER18;
      FIELD_ARRAY[NUMBER19_VALUE] = TaskField.NUMBER19;
      FIELD_ARRAY[NUMBER20_VALUE] = TaskField.NUMBER20;
      FIELD_ARRAY[TEXT11_VALUE] = TaskField.TEXT11;
      FIELD_ARRAY[TEXT12_VALUE] = TaskField.TEXT12;
      FIELD_ARRAY[TEXT13_VALUE] = TaskField.TEXT13;
      FIELD_ARRAY[TEXT14_VALUE] = TaskField.TEXT14;
      FIELD_ARRAY[TEXT15_VALUE] = TaskField.TEXT15;
      FIELD_ARRAY[TEXT16_VALUE] = TaskField.TEXT16;
      FIELD_ARRAY[TEXT17_VALUE] = TaskField.TEXT17;
      FIELD_ARRAY[TEXT18_VALUE] = TaskField.TEXT18;
      FIELD_ARRAY[TEXT19_VALUE] = TaskField.TEXT19;
      FIELD_ARRAY[TEXT20_VALUE] = TaskField.TEXT20;
      FIELD_ARRAY[TEXT21_VALUE] = TaskField.TEXT21;
      FIELD_ARRAY[TEXT22_VALUE] = TaskField.TEXT22;
      FIELD_ARRAY[TEXT23_VALUE] = TaskField.TEXT23;
      FIELD_ARRAY[TEXT24_VALUE] = TaskField.TEXT24;
      FIELD_ARRAY[TEXT25_VALUE] = TaskField.TEXT25;
      FIELD_ARRAY[TEXT26_VALUE] = TaskField.TEXT26;
      FIELD_ARRAY[TEXT27_VALUE] = TaskField.TEXT27;
      FIELD_ARRAY[TEXT28_VALUE] = TaskField.TEXT28;
      FIELD_ARRAY[TEXT29_VALUE] = TaskField.TEXT29;
      FIELD_ARRAY[TEXT30_VALUE] = TaskField.TEXT30;
      FIELD_ARRAY[RESOURCE_PHONETICS_VALUE] = TaskField.RESOURCE_PHONETICS;
      FIELD_ARRAY[ASSIGNMENT_DELAY_VALUE] = TaskField.ASSIGNMENT_DELAY;
      FIELD_ARRAY[ASSIGNMENT_UNITS_VALUE] = TaskField.ASSIGNMENT_UNITS;
      FIELD_ARRAY[COST_RATE_TABLE_VALUE] = TaskField.COST_RATE_TABLE;
      FIELD_ARRAY[PRELEVELED_START_VALUE] = TaskField.PRELEVELED_START;
      FIELD_ARRAY[PRELEVELED_FINISH_VALUE] = TaskField.PRELEVELED_FINISH;
      FIELD_ARRAY[ESTIMATED_VALUE] = TaskField.ESTIMATED;
      FIELD_ARRAY[IGNORE_RESOURCE_CALENDAR_VALUE] = TaskField.IGNORE_RESOURCE_CALENDAR;
      FIELD_ARRAY[CALENDAR_VALUE] = TaskField.CALENDAR;
      FIELD_ARRAY[OUTLINE_CODE1_VALUE] = TaskField.OUTLINE_CODE1;
      FIELD_ARRAY[OUTLINE_CODE2_VALUE] = TaskField.OUTLINE_CODE2;
      FIELD_ARRAY[OUTLINE_CODE3_VALUE] = TaskField.OUTLINE_CODE3;
      FIELD_ARRAY[OUTLINE_CODE4_VALUE] = TaskField.OUTLINE_CODE4;
      FIELD_ARRAY[OUTLINE_CODE5_VALUE] = TaskField.OUTLINE_CODE5;
      FIELD_ARRAY[OUTLINE_CODE6_VALUE] = TaskField.OUTLINE_CODE6;
      FIELD_ARRAY[OUTLINE_CODE7_VALUE] = TaskField.OUTLINE_CODE7;
      FIELD_ARRAY[OUTLINE_CODE8_VALUE] = TaskField.OUTLINE_CODE8;
      FIELD_ARRAY[OUTLINE_CODE9_VALUE] = TaskField.OUTLINE_CODE9;
      FIELD_ARRAY[OUTLINE_CODE10_VALUE] = TaskField.OUTLINE_CODE10;
      FIELD_ARRAY[DEADLINE_VALUE] = TaskField.DEADLINE;
      FIELD_ARRAY[START_SLACK_VALUE] = TaskField.START_SLACK;
      FIELD_ARRAY[FINISH_SLACK_VALUE] = TaskField.FINISH_SLACK;
      FIELD_ARRAY[VAC_VALUE] = TaskField.VAC;
      FIELD_ARRAY[GROUP_BY_SUMMARY_VALUE] = TaskField.GROUP_BY_SUMMARY;
      FIELD_ARRAY[WBS_PREDECESSORS_VALUE] = TaskField.WBS_PREDECESSORS;
      FIELD_ARRAY[WBS_SUCCESSORS_VALUE] = TaskField.WBS_SUCCESSORS;
      FIELD_ARRAY[RESOURCE_TYPE_VALUE] = TaskField.RESOURCE_TYPE;
   }
}
