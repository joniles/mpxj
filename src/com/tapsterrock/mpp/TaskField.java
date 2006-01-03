/*
 * file:       TaskField.java
 * author:     Jon Iles
 * copyright:  (c) Tapster Rock Limited 2005
 * date:       16/04/2005
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

package com.tapsterrock.mpp;

import java.util.Locale;

import com.tapsterrock.mpx.FieldType;

/**
 * Instances of this type represent Task fields.
 */
public final class TaskField implements FieldType
{
   /**
    * Private constructor.
    * 
    * @param value task field value
    */
   private TaskField (int value)
   {
      m_value = value;
   }

   /**
    * {@inheritDoc}
    */
   public String getName()
   {
      return (getName(Locale.getDefault()));
   }

   /**
    * {@inheritDoc}
    */
   public String getName (Locale locale)
   {      
      String[] titles = LocaleData.getStringArray(locale, LocaleData.TASK_COLUMNS);
      String result = null;
      
      if (m_value >= 0 && m_value < titles.length)
      {
         result = titles[m_value];
      }
      
      return (result);
   }
   
   /**
    * Retrieve an instance of this class based on the data read from an
    * MS Project file.
    * 
    * @param value value from an MS Project file
    * @return instance of this class
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
   
   /**
    * Retrieves the string representation of this instance.
    * 
    * @return string representation
    */
   public String toString ()
   {
      return (getName());
   }
   
   public static final int WORK_VALUE = 0;
   public static final int BASELINE_WORK_VALUE = 1;
   public static final int ACTUAL_WORK_VALUE = 2;
   public static final int WORK_VARIANCE_VALUE = 3;
   public static final int REMAINING_WORK_VALUE = 4;
   public static final int COST_VALUE = 5;
   public static final int BASELINE_COST_VALUE = 6;
   public static final int ACTUAL_COST_VALUE = 7;
   public static final int FIXED_COST_VALUE = 8;
   public static final int COST_VARIANCE_VALUE = 9;
   public static final int REMAINING_COST_VALUE = 10;
   public static final int BCWP_VALUE = 11;
   public static final int BCWS_VALUE = 12;
   public static final int SV_VALUE = 13;
   public static final int NAME_VALUE = 14;
   public static final int WBS_VALUE = 16;
   public static final int CONSTRAINT_TYPE_VALUE = 17;
   public static final int CONSTRAINT_DATE_VALUE = 18;
   public static final int CRITICAL_VALUE = 19;
   public static final int LEVELING_DELAY_VALUE = 20;
   public static final int FREE_SLACK_VALUE = 21;
   public static final int TOTAL_SLACK_VALUE = 22;
   public static final int ID_VALUE = 23;
   public static final int MILESTONE_VALUE = 24;
   public static final int PRIORITY_VALUE = 25;
   public static final int SUBPROJECT_FILE_VALUE = 26;
   public static final int BASELINE_DURATION_VALUE = 27;
   public static final int ACTUAL_DURATION_VALUE = 28;
   public static final int DURATION_VALUE = 29;
   public static final int DURATION_VARIANCE_VALUE = 30;
   public static final int REMAINING_DURATION_VALUE = 31;
   public static final int PERCENT_COMPLETE_VALUE = 32;
   public static final int PERCENT_WORK_COMPLETE_VALUE = 33;
   public static final int START_VALUE = 35;
   public static final int FINISH_VALUE = 36;
   public static final int EARLY_START_VALUE = 37;
   public static final int EARLY_FINISH_VALUE = 38;
   public static final int LATE_START_VALUE = 39;
   public static final int LATE_FINISH_VALUE = 40;
   public static final int ACTUAL_START_VALUE = 41;
   public static final int ACTUAL_FINISH_VALUE = 42;
   public static final int BASELINE_START_VALUE = 43;
   public static final int BASELINE_FINISH_VALUE = 44;
   public static final int START_VARIANCE_VALUE = 45;
   public static final int FINISH_VARIANCE_VALUE = 46;
   public static final int PREDECESSORS_VALUE = 47;
   public static final int SUCCESSORS_VALUE = 48;
   public static final int RESOURCE_NAMES_VALUE = 49;
   public static final int RESOURCE_INITIALS_VALUE = 50;
   public static final int TEXT1_VALUE = 51;
   public static final int START1_VALUE = 52;
   public static final int FINISH1_VALUE = 53;
   public static final int TEXT2_VALUE = 54;
   public static final int START2_VALUE = 55;
   public static final int FINISH2_VALUE = 56;
   public static final int TEXT3_VALUE = 57;
   public static final int START3_VALUE = 58;
   public static final int FINISH3_VALUE = 59;
   public static final int TEXT4_VALUE = 60;
   public static final int START4_VALUE = 61;
   public static final int FINISH4_VALUE = 62;
   public static final int TEXT5_VALUE = 63;
   public static final int START5_VALUE = 64;
   public static final int FINISH5_VALUE = 65;
   public static final int TEXT6_VALUE = 66;
   public static final int TEXT7_VALUE = 67;
   public static final int TEXT8_VALUE = 68;
   public static final int TEXT9_VALUE = 69;
   public static final int TEXT10_VALUE = 70;
   public static final int MARKED_VALUE = 71;
   public static final int FLAG1_VALUE = 72;
   public static final int FLAG2_VALUE = 73;
   public static final int FLAG3_VALUE = 74;
   public static final int FLAG4_VALUE = 75;
   public static final int FLAG5_VALUE = 76;
   public static final int FLAG6_VALUE = 77;
   public static final int FLAG7_VALUE = 78;
   public static final int FLAG8_VALUE = 79;
   public static final int FLAG9_VALUE = 80;
   public static final int FLAG10_VALUE = 81;
   public static final int ROLLUP_VALUE = 82;
   public static final int CV_VALUE = 83;
   public static final int PROJECT_VALUE = 84;
   public static final int OUTLINE_LEVEL_VALUE = 85;
   public static final int UNIQUE_ID_VALUE = 86;
   public static final int NUMBER1_VALUE = 87;
   public static final int NUMBER2_VALUE = 88;
   public static final int NUMBER3_VALUE = 89;
   public static final int NUMBER4_VALUE = 90;
   public static final int NUMBER5_VALUE = 91;
   public static final int SUMMARY_VALUE = 92;
   public static final int CREATED_VALUE = 93;
   public static final int NOTES_VALUE = 94;
   public static final int UNIQUE_ID_PREDECESSORS_VALUE = 95;
   public static final int UNIQUE_ID_SUCCESSORS_VALUE = 96;
   public static final int OBJECTS_VALUE = 97;
   public static final int LINKED_FIELDS_VALUE = 98;
   public static final int RESUME_VALUE = 99;
   public static final int STOP_VALUE = 100;
   public static final int OUTLINE_NUMBER_VALUE = 102;
   public static final int DURATION1_VALUE = 103;
   public static final int DURATION2_VALUE = 104;
   public static final int DURATION3_VALUE = 105;
   public static final int COST1_VALUE = 106;
   public static final int COST2_VALUE = 107;
   public static final int COST3_VALUE = 108;
   public static final int HIDEBAR_VALUE = 109;
   public static final int CONFIRMED_VALUE = 110;
   public static final int UPDATE_NEEDED_VALUE = 111;
   public static final int CONTACT_VALUE = 112;
   public static final int RESOURCE_GROUP_VALUE = 113;
   public static final int ACWP_VALUE = 120;
   public static final int TYPE_VALUE = 128;
   public static final int RECURRING_VALUE = 129;
   public static final int EFFORT_DRIVEN_VALUE = 132;
   public static final int OVERTIME_WORK_VALUE = 163;
   public static final int ACTUAL_OVERTIME_WORK_VALUE = 164;
   public static final int REMAINING_OVERTIME_WORK_VALUE = 165;
   public static final int REGULAR_WORK_VALUE = 166;
   public static final int OVERTIME_COST_VALUE = 168;
   public static final int ACTUAL_OVERTIME_COST_VALUE = 169;
   public static final int REMAINING_OVERTIME_COST_VALUE = 170;
   public static final int FIXED_COST_ACCRUAL_VALUE = 200;
   public static final int INDICATORS_VALUE = 205;
   public static final int HYPERLINK_VALUE = 217;
   public static final int HYPERLINK_ADDRESS_VALUE = 218;
   public static final int HYPERLINK_SUBADDRESS_VALUE = 219;
   public static final int HYPERLINK_HREF_VALUE = 220;
   public static final int ASSIGNMENT_VALUE = 224;
   public static final int OVERALLOCATED_VALUE = 225;
   public static final int EXTERNAL_TASK_VALUE = 232;
   public static final int SUBPROJECT_READ_ONLY_VALUE = 246;
   public static final int RESPONSE_PENDING_VALUE = 250;
   public static final int TEAMSTATUS_PENDING_VALUE = 251;
   public static final int LEVELING_CAN_SPLIT_VALUE = 252;
   public static final int LEVEL_ASSIGNMENTS_VALUE = 253;
   public static final int WORK_CONTOUR_VALUE = 256;
   public static final int COST4_VALUE = 258;
   public static final int COST5_VALUE = 259;
   public static final int COST6_VALUE = 260;
   public static final int COST7_VALUE = 261;
   public static final int COST8_VALUE = 262;
   public static final int COST9_VALUE = 263;
   public static final int COST10_VALUE = 264;
   public static final int DATE1_VALUE = 265;
   public static final int DATE2_VALUE = 266;
   public static final int DATE3_VALUE = 267;
   public static final int DATE4_VALUE = 268;
   public static final int DATE5_VALUE = 269;
   public static final int DATE6_VALUE = 260;
   public static final int DATE7_VALUE = 271;
   public static final int DATE8_VALUE = 272;
   public static final int DATE9_VALUE = 273;
   public static final int DATE10_VALUE = 274;
   public static final int DURATION4_VALUE = 275;
   public static final int DURATION5_VALUE = 276;
   public static final int DURATION6_VALUE = 277;
   public static final int DURATION7_VALUE = 278;
   public static final int DURATION8_VALUE = 279;
   public static final int DURATION9_VALUE = 280;
   public static final int DURATION10_VALUE = 281;
   public static final int START6_VALUE = 282;
   public static final int FINISH6_VALUE = 283;
   public static final int START7_VALUE = 284;
   public static final int FINISH7_VALUE = 285;
   public static final int START8_VALUE = 286;
   public static final int FINISH8_VALUE = 287;
   public static final int START9_VALUE = 288;
   public static final int FINISH9_VALUE = 289;
   public static final int START10_VALUE = 290;
   public static final int FINISH10_VALUE = 291;
   public static final int FLAG11_VALUE = 292;
   public static final int FLAG12_VALUE = 293;
   public static final int FLAG13_VALUE = 294;
   public static final int FLAG14_VALUE = 295;
   public static final int FLAG15_VALUE = 296;
   public static final int FLAG16_VALUE = 297;
   public static final int FLAG17_VALUE = 298;
   public static final int FLAG18_VALUE = 299;
   public static final int FLAG19_VALUE = 300;
   public static final int FLAG20_VALUE = 301;
   public static final int NUMBER6_VALUE = 302;
   public static final int NUMBER7_VALUE = 303;
   public static final int NUMBER8_VALUE = 304;
   public static final int NUMBER9_VALUE = 305;
   public static final int NUMBER10_VALUE = 306;
   public static final int NUMBER11_VALUE = 307;
   public static final int NUMBER12_VALUE = 308;
   public static final int NUMBER13_VALUE = 309;
   public static final int NUMBER14_VALUE = 310;
   public static final int NUMBER15_VALUE = 311;
   public static final int NUMBER16_VALUE = 312;
   public static final int NUMBER17_VALUE = 313;
   public static final int NUMBER18_VALUE = 314;
   public static final int NUMBER19_VALUE = 315;
   public static final int NUMBER20_VALUE = 316;
   public static final int TEXT11_VALUE = 317;
   public static final int TEXT12_VALUE = 318;
   public static final int TEXT13_VALUE = 319;
   public static final int TEXT14_VALUE = 320;
   public static final int TEXT15_VALUE = 321;
   public static final int TEXT16_VALUE = 322;
   public static final int TEXT17_VALUE = 323;
   public static final int TEXT18_VALUE = 324;
   public static final int TEXT19_VALUE = 325;
   public static final int TEXT20_VALUE = 326;
   public static final int TEXT21_VALUE = 327;
   public static final int TEXT22_VALUE = 328;
   public static final int TEXT23_VALUE = 329;
   public static final int TEXT24_VALUE = 330;
   public static final int TEXT25_VALUE = 331;
   public static final int TEXT26_VALUE = 332;
   public static final int TEXT27_VALUE = 333;
   public static final int TEXT28_VALUE = 334;
   public static final int TEXT29_VALUE = 335;
   public static final int TEXT30_VALUE = 336;
   public static final int RESOURCE_PHONETICS_VALUE = 349;
   public static final int ASSIGNMENT_DELAY_VALUE = 366;
   public static final int ASSIGNMENT_UNITS_VALUE = 367;
   public static final int COST_RATE_TABLE_VALUE = 368;
   public static final int PRELEVELED_START_VALUE = 369;
   public static final int PRELEVELED_FINISH_VALUE = 370;
   public static final int ESTIMATED_VALUE = 396;
   public static final int IGNORE_RESOURCE_CALENDAR_VALUE = 399;
   public static final int CALENDAR_VALUE = 402;
   public static final int OUTLINE_CODE1_VALUE = 416;
   public static final int OUTLINE_CODE2_VALUE = 418;
   public static final int OUTLINE_CODE3_VALUE = 420;
   public static final int OUTLINE_CODE4_VALUE = 422;
   public static final int OUTLINE_CODE5_VALUE = 424;
   public static final int OUTLINE_CODE6_VALUE = 426;
   public static final int OUTLINE_CODE7_VALUE = 428;
   public static final int OUTLINE_CODE8_VALUE = 430;
   public static final int OUTLINE_CODE9_VALUE = 432;
   public static final int OUTLINE_CODE10_VALUE = 434;
   public static final int DEADLINE_VALUE = 437;
   public static final int START_SLACK_VALUE = 438;
   public static final int FINISH_SLACK_VALUE = 439;
   public static final int VAC_VALUE = 441;
   public static final int GROUP_BY_SUMMARY_VALUE = 446;
   public static final int WBS_PREDECESSORS_VALUE = 449;
   public static final int WBS_SUCCESSORS_VALUE = 450;
   public static final int RESOURCE_TYPE_VALUE = 451;

   public static final TaskField WORK = new TaskField(WORK_VALUE);
   public static final TaskField BASELINE_WORK = new TaskField(BASELINE_WORK_VALUE);
   public static final TaskField ACTUAL_WORK = new TaskField(ACTUAL_WORK_VALUE);
   public static final TaskField WORK_VARIANCE = new TaskField(WORK_VARIANCE_VALUE);
   public static final TaskField REMAINING_WORK = new TaskField(REMAINING_WORK_VALUE);
   public static final TaskField COST = new TaskField(COST_VALUE);
   public static final TaskField BASELINE_COST = new TaskField(BASELINE_COST_VALUE);
   public static final TaskField ACTUAL_COST = new TaskField(ACTUAL_COST_VALUE);
   public static final TaskField FIXED_COST = new TaskField(FIXED_COST_VALUE);
   public static final TaskField COST_VARIANCE = new TaskField(COST_VARIANCE_VALUE);
   public static final TaskField REMAINING_COST = new TaskField(REMAINING_COST_VALUE);
   public static final TaskField BCWP = new TaskField(BCWP_VALUE);
   public static final TaskField BCWS = new TaskField(BCWS_VALUE);
   public static final TaskField SV = new TaskField(SV_VALUE);
   public static final TaskField NAME = new TaskField(NAME_VALUE);
   public static final TaskField WBS = new TaskField(WBS_VALUE);
   public static final TaskField CONSTRAINT_TYPE = new TaskField(CONSTRAINT_TYPE_VALUE);
   public static final TaskField CONSTRAINT_DATE = new TaskField(CONSTRAINT_DATE_VALUE);
   public static final TaskField CRITICAL = new TaskField(CRITICAL_VALUE);
   public static final TaskField LEVELING_DELAY = new TaskField(LEVELING_DELAY_VALUE);
   public static final TaskField FREE_SLACK = new TaskField(FREE_SLACK_VALUE);
   public static final TaskField TOTAL_SLACK = new TaskField(TOTAL_SLACK_VALUE);
   public static final TaskField ID = new TaskField(ID_VALUE);
   public static final TaskField MILESTONE = new TaskField(MILESTONE_VALUE);
   public static final TaskField PRIORITY = new TaskField(PRIORITY_VALUE);
   public static final TaskField SUBPROJECT_FILE = new TaskField(SUBPROJECT_FILE_VALUE);
   public static final TaskField BASELINE_DURATION = new TaskField(BASELINE_DURATION_VALUE);
   public static final TaskField ACTUAL_DURATION = new TaskField(ACTUAL_DURATION_VALUE);
   public static final TaskField DURATION = new TaskField(DURATION_VALUE);
   public static final TaskField DURATION_VARIANCE = new TaskField(DURATION_VARIANCE_VALUE);
   public static final TaskField REMAINING_DURATION = new TaskField(REMAINING_DURATION_VALUE);
   public static final TaskField PERCENT_COMPLETE = new TaskField(PERCENT_COMPLETE_VALUE);
   public static final TaskField PERCENT_WORK_COMPLETE = new TaskField(PERCENT_WORK_COMPLETE_VALUE);
   public static final TaskField START = new TaskField(START_VALUE);
   public static final TaskField FINISH = new TaskField(FINISH_VALUE);
   public static final TaskField EARLY_START = new TaskField(EARLY_START_VALUE);
   public static final TaskField EARLY_FINISH = new TaskField(EARLY_FINISH_VALUE);
   public static final TaskField LATE_START = new TaskField(LATE_START_VALUE);
   public static final TaskField LATE_FINISH = new TaskField(LATE_FINISH_VALUE);
   public static final TaskField ACTUAL_START = new TaskField(ACTUAL_START_VALUE);
   public static final TaskField ACTUAL_FINISH = new TaskField(ACTUAL_FINISH_VALUE);
   public static final TaskField BASELINE_START = new TaskField(BASELINE_START_VALUE);
   public static final TaskField BASELINE_FINISH = new TaskField(BASELINE_FINISH_VALUE);
   public static final TaskField START_VARIANCE = new TaskField(START_VARIANCE_VALUE);
   public static final TaskField FINISH_VARIANCE = new TaskField(FINISH_VARIANCE_VALUE);
   public static final TaskField PREDECESSORS = new TaskField(PREDECESSORS_VALUE);
   public static final TaskField SUCCESSORS = new TaskField(SUCCESSORS_VALUE);
   public static final TaskField RESOURCE_NAMES = new TaskField(RESOURCE_NAMES_VALUE);
   public static final TaskField RESOURCE_INITIALS = new TaskField(RESOURCE_INITIALS_VALUE);
   public static final TaskField TEXT1 = new TaskField(TEXT1_VALUE);
   public static final TaskField START1 = new TaskField(START1_VALUE);
   public static final TaskField FINISH1 = new TaskField(FINISH1_VALUE);
   public static final TaskField TEXT2 = new TaskField(TEXT2_VALUE);
   public static final TaskField START2 = new TaskField(START2_VALUE);
   public static final TaskField FINISH2 = new TaskField(FINISH2_VALUE);
   public static final TaskField TEXT3 = new TaskField(TEXT3_VALUE);
   public static final TaskField START3 = new TaskField(START3_VALUE);
   public static final TaskField FINISH3 = new TaskField(FINISH3_VALUE);
   public static final TaskField TEXT4 = new TaskField(TEXT4_VALUE);
   public static final TaskField START4 = new TaskField(START4_VALUE);
   public static final TaskField FINISH4 = new TaskField(FINISH4_VALUE);
   public static final TaskField TEXT5 = new TaskField(TEXT5_VALUE);
   public static final TaskField START5 = new TaskField(START5_VALUE);
   public static final TaskField FINISH5 = new TaskField(FINISH5_VALUE);
   public static final TaskField TEXT6 = new TaskField(TEXT6_VALUE);
   public static final TaskField TEXT7 = new TaskField(TEXT7_VALUE);
   public static final TaskField TEXT8 = new TaskField(TEXT8_VALUE);
   public static final TaskField TEXT9 = new TaskField(TEXT9_VALUE);
   public static final TaskField TEXT10 = new TaskField(TEXT10_VALUE);
   public static final TaskField MARKED = new TaskField(MARKED_VALUE);
   public static final TaskField FLAG1 = new TaskField(FLAG1_VALUE);
   public static final TaskField FLAG2 = new TaskField(FLAG2_VALUE);
   public static final TaskField FLAG3 = new TaskField(FLAG3_VALUE);
   public static final TaskField FLAG4 = new TaskField(FLAG4_VALUE);
   public static final TaskField FLAG5 = new TaskField(FLAG5_VALUE);
   public static final TaskField FLAG6 = new TaskField(FLAG6_VALUE);
   public static final TaskField FLAG7 = new TaskField(FLAG7_VALUE);
   public static final TaskField FLAG8 = new TaskField(FLAG8_VALUE);
   public static final TaskField FLAG9 = new TaskField(FLAG9_VALUE);
   public static final TaskField FLAG10 = new TaskField(FLAG10_VALUE);
   public static final TaskField ROLLUP = new TaskField(ROLLUP_VALUE);
   public static final TaskField CV = new TaskField(CV_VALUE);
   public static final TaskField PROJECT = new TaskField(PROJECT_VALUE);
   public static final TaskField OUTLINE_LEVEL = new TaskField(OUTLINE_LEVEL_VALUE);
   public static final TaskField UNIQUE_ID = new TaskField(UNIQUE_ID_VALUE);
   public static final TaskField NUMBER1 = new TaskField(NUMBER1_VALUE);
   public static final TaskField NUMBER2 = new TaskField(NUMBER2_VALUE);
   public static final TaskField NUMBER3 = new TaskField(NUMBER3_VALUE);
   public static final TaskField NUMBER4 = new TaskField(NUMBER4_VALUE);
   public static final TaskField NUMBER5 = new TaskField(NUMBER5_VALUE);
   public static final TaskField SUMMARY = new TaskField(SUMMARY_VALUE);
   public static final TaskField CREATED = new TaskField(CREATED_VALUE);
   public static final TaskField NOTES = new TaskField(NOTES_VALUE);
   public static final TaskField UNIQUE_ID_PREDECESSORS = new TaskField(UNIQUE_ID_PREDECESSORS_VALUE);
   public static final TaskField UNIQUE_ID_SUCCESSORS = new TaskField(UNIQUE_ID_SUCCESSORS_VALUE);
   public static final TaskField OBJECTS = new TaskField(OBJECTS_VALUE);
   public static final TaskField LINKED_FIELDS = new TaskField(LINKED_FIELDS_VALUE);
   public static final TaskField RESUME = new TaskField(RESUME_VALUE);
   public static final TaskField STOP = new TaskField(STOP_VALUE);
   public static final TaskField OUTLINE_NUMBER = new TaskField(OUTLINE_NUMBER_VALUE);
   public static final TaskField DURATION1 = new TaskField(DURATION1_VALUE);
   public static final TaskField DURATION2 = new TaskField(DURATION2_VALUE);
   public static final TaskField DURATION3 = new TaskField(DURATION3_VALUE);
   public static final TaskField COST1 = new TaskField(COST1_VALUE);
   public static final TaskField COST2 = new TaskField(COST2_VALUE);
   public static final TaskField COST3 = new TaskField(COST3_VALUE);
   public static final TaskField HIDEBAR = new TaskField(HIDEBAR_VALUE);
   public static final TaskField CONFIRMED = new TaskField(CONFIRMED_VALUE);
   public static final TaskField UPDATE_NEEDED = new TaskField(UPDATE_NEEDED_VALUE);
   public static final TaskField CONTACT = new TaskField(CONTACT_VALUE);
   public static final TaskField RESOURCE_GROUP = new TaskField(RESOURCE_GROUP_VALUE);
   public static final TaskField ACWP = new TaskField(ACWP_VALUE);
   public static final TaskField TYPE = new TaskField(TYPE_VALUE);
   public static final TaskField RECURRING = new TaskField(RECURRING_VALUE);
   public static final TaskField EFFORT_DRIVEN = new TaskField(EFFORT_DRIVEN_VALUE);
   public static final TaskField OVERTIME_WORK = new TaskField(OVERTIME_WORK_VALUE);
   public static final TaskField ACTUAL_OVERTIME_WORK = new TaskField(ACTUAL_OVERTIME_WORK_VALUE);
   public static final TaskField REMAINING_OVERTIME_WORK = new TaskField(REMAINING_OVERTIME_WORK_VALUE);
   public static final TaskField REGULAR_WORK = new TaskField(REGULAR_WORK_VALUE);
   public static final TaskField OVERTIME_COST = new TaskField(OVERTIME_COST_VALUE);
   public static final TaskField ACTUAL_OVERTIME_COST = new TaskField(ACTUAL_OVERTIME_COST_VALUE);
   public static final TaskField REMAINING_OVERTIME_COST = new TaskField(REMAINING_OVERTIME_COST_VALUE);
   public static final TaskField FIXED_COST_ACCRUAL = new TaskField(FIXED_COST_ACCRUAL_VALUE);
   public static final TaskField INDICATORS = new TaskField(INDICATORS_VALUE);
   public static final TaskField HYPERLINK = new TaskField(HYPERLINK_VALUE);
   public static final TaskField HYPERLINK_ADDRESS = new TaskField(HYPERLINK_ADDRESS_VALUE);
   public static final TaskField HYPERLINK_SUBADDRESS = new TaskField(HYPERLINK_SUBADDRESS_VALUE);
   public static final TaskField HYPERLINK_HREF = new TaskField(HYPERLINK_HREF_VALUE);
   public static final TaskField ASSIGNMENT = new TaskField(ASSIGNMENT_VALUE);
   public static final TaskField OVERALLOCATED = new TaskField(OVERALLOCATED_VALUE);
   public static final TaskField EXTERNAL_TASK = new TaskField(EXTERNAL_TASK_VALUE);
   public static final TaskField SUBPROJECT_READ_ONLY = new TaskField(SUBPROJECT_READ_ONLY_VALUE);
   public static final TaskField RESPONSE_PENDING = new TaskField(RESPONSE_PENDING_VALUE);
   public static final TaskField TEAMSTATUS_PENDING = new TaskField(TEAMSTATUS_PENDING_VALUE);
   public static final TaskField LEVELING_CAN_SPLIT = new TaskField(LEVELING_CAN_SPLIT_VALUE);
   public static final TaskField LEVEL_ASSIGNMENTS = new TaskField(LEVEL_ASSIGNMENTS_VALUE);
   public static final TaskField WORK_CONTOUR = new TaskField(WORK_CONTOUR_VALUE);
   public static final TaskField COST4 = new TaskField(COST4_VALUE);
   public static final TaskField COST5 = new TaskField(COST5_VALUE);
   public static final TaskField COST6 = new TaskField(COST6_VALUE);
   public static final TaskField COST7 = new TaskField(COST7_VALUE);
   public static final TaskField COST8 = new TaskField(COST8_VALUE);
   public static final TaskField COST9 = new TaskField(COST9_VALUE);
   public static final TaskField COST10 = new TaskField(COST10_VALUE);
   public static final TaskField DATE1 = new TaskField(DATE1_VALUE);
   public static final TaskField DATE2 = new TaskField(DATE2_VALUE);
   public static final TaskField DATE3 = new TaskField(DATE3_VALUE);
   public static final TaskField DATE4 = new TaskField(DATE4_VALUE);
   public static final TaskField DATE5 = new TaskField(DATE5_VALUE);
   public static final TaskField DATE6 = new TaskField(DATE6_VALUE);
   public static final TaskField DATE7 = new TaskField(DATE7_VALUE);
   public static final TaskField DATE8 = new TaskField(DATE8_VALUE);
   public static final TaskField DATE9 = new TaskField(DATE9_VALUE);
   public static final TaskField DATE10 = new TaskField(DATE10_VALUE);
   public static final TaskField DURATION4 = new TaskField(DURATION4_VALUE);
   public static final TaskField DURATION5 = new TaskField(DURATION5_VALUE);
   public static final TaskField DURATION6 = new TaskField(DURATION6_VALUE);
   public static final TaskField DURATION7 = new TaskField(DURATION7_VALUE);
   public static final TaskField DURATION8 = new TaskField(DURATION8_VALUE);
   public static final TaskField DURATION9 = new TaskField(DURATION9_VALUE);
   public static final TaskField DURATION10 = new TaskField(DURATION10_VALUE);
   public static final TaskField START6 = new TaskField(START6_VALUE);
   public static final TaskField FINISH6 = new TaskField(FINISH6_VALUE);
   public static final TaskField START7 = new TaskField(START7_VALUE);
   public static final TaskField FINISH7 = new TaskField(FINISH7_VALUE);
   public static final TaskField START8 = new TaskField(START8_VALUE);
   public static final TaskField FINISH8 = new TaskField(FINISH8_VALUE);
   public static final TaskField START9 = new TaskField(START9_VALUE);
   public static final TaskField FINISH9 = new TaskField(FINISH9_VALUE);
   public static final TaskField START10 = new TaskField(START10_VALUE);
   public static final TaskField FINISH10 = new TaskField(FINISH10_VALUE);
   public static final TaskField FLAG11 = new TaskField(FLAG11_VALUE);
   public static final TaskField FLAG12 = new TaskField(FLAG12_VALUE);
   public static final TaskField FLAG13 = new TaskField(FLAG13_VALUE);
   public static final TaskField FLAG14 = new TaskField(FLAG14_VALUE);
   public static final TaskField FLAG15 = new TaskField(FLAG15_VALUE);
   public static final TaskField FLAG16 = new TaskField(FLAG16_VALUE);
   public static final TaskField FLAG17 = new TaskField(FLAG17_VALUE);
   public static final TaskField FLAG18 = new TaskField(FLAG18_VALUE);
   public static final TaskField FLAG19 = new TaskField(FLAG19_VALUE);
   public static final TaskField FLAG20 = new TaskField(FLAG20_VALUE);
   public static final TaskField NUMBER6 = new TaskField(NUMBER6_VALUE);
   public static final TaskField NUMBER7 = new TaskField(NUMBER7_VALUE);
   public static final TaskField NUMBER8 = new TaskField(NUMBER8_VALUE);
   public static final TaskField NUMBER9 = new TaskField(NUMBER9_VALUE);
   public static final TaskField NUMBER10 = new TaskField(NUMBER10_VALUE);
   public static final TaskField NUMBER11 = new TaskField(NUMBER11_VALUE);
   public static final TaskField NUMBER12 = new TaskField(NUMBER12_VALUE);
   public static final TaskField NUMBER13 = new TaskField(NUMBER13_VALUE);
   public static final TaskField NUMBER14 = new TaskField(NUMBER14_VALUE);
   public static final TaskField NUMBER15 = new TaskField(NUMBER15_VALUE);
   public static final TaskField NUMBER16 = new TaskField(NUMBER16_VALUE);
   public static final TaskField NUMBER17 = new TaskField(NUMBER17_VALUE);
   public static final TaskField NUMBER18 = new TaskField(NUMBER18_VALUE);
   public static final TaskField NUMBER19 = new TaskField(NUMBER19_VALUE);
   public static final TaskField NUMBER20 = new TaskField(NUMBER20_VALUE);
   public static final TaskField TEXT11 = new TaskField(TEXT11_VALUE);
   public static final TaskField TEXT12 = new TaskField(TEXT12_VALUE);
   public static final TaskField TEXT13 = new TaskField(TEXT13_VALUE);
   public static final TaskField TEXT14 = new TaskField(TEXT14_VALUE);
   public static final TaskField TEXT15 = new TaskField(TEXT15_VALUE);
   public static final TaskField TEXT16 = new TaskField(TEXT16_VALUE);
   public static final TaskField TEXT17 = new TaskField(TEXT17_VALUE);
   public static final TaskField TEXT18 = new TaskField(TEXT18_VALUE);
   public static final TaskField TEXT19 = new TaskField(TEXT19_VALUE);
   public static final TaskField TEXT20 = new TaskField(TEXT20_VALUE);
   public static final TaskField TEXT21 = new TaskField(TEXT21_VALUE);
   public static final TaskField TEXT22 = new TaskField(TEXT22_VALUE);
   public static final TaskField TEXT23 = new TaskField(TEXT23_VALUE);
   public static final TaskField TEXT24 = new TaskField(TEXT24_VALUE);
   public static final TaskField TEXT25 = new TaskField(TEXT25_VALUE);
   public static final TaskField TEXT26 = new TaskField(TEXT26_VALUE);
   public static final TaskField TEXT27 = new TaskField(TEXT27_VALUE);
   public static final TaskField TEXT28 = new TaskField(TEXT28_VALUE);
   public static final TaskField TEXT29 = new TaskField(TEXT29_VALUE);
   public static final TaskField TEXT30 = new TaskField(TEXT30_VALUE);
   public static final TaskField RESOURCE_PHONETICS = new TaskField(RESOURCE_PHONETICS_VALUE);
   public static final TaskField ASSIGNMENT_DELAY = new TaskField(ASSIGNMENT_DELAY_VALUE);
   public static final TaskField ASSIGNMENT_UNITS = new TaskField(ASSIGNMENT_UNITS_VALUE);
   public static final TaskField COST_RATE_TABLE = new TaskField(COST_RATE_TABLE_VALUE);
   public static final TaskField PRELEVELED_START = new TaskField(PRELEVELED_START_VALUE);
   public static final TaskField PRELEVELED_FINISH = new TaskField(PRELEVELED_FINISH_VALUE);
   public static final TaskField ESTIMATED = new TaskField(ESTIMATED_VALUE);
   public static final TaskField IGNORE_RESOURCE_CALENDAR = new TaskField(IGNORE_RESOURCE_CALENDAR_VALUE);
   public static final TaskField CALENDAR = new TaskField(CALENDAR_VALUE);
   public static final TaskField OUTLINE_CODE1 = new TaskField(OUTLINE_CODE1_VALUE);
   public static final TaskField OUTLINE_CODE2 = new TaskField(OUTLINE_CODE2_VALUE);
   public static final TaskField OUTLINE_CODE3 = new TaskField(OUTLINE_CODE3_VALUE);
   public static final TaskField OUTLINE_CODE4 = new TaskField(OUTLINE_CODE4_VALUE);
   public static final TaskField OUTLINE_CODE5 = new TaskField(OUTLINE_CODE5_VALUE);
   public static final TaskField OUTLINE_CODE6 = new TaskField(OUTLINE_CODE6_VALUE);
   public static final TaskField OUTLINE_CODE7 = new TaskField(OUTLINE_CODE7_VALUE);
   public static final TaskField OUTLINE_CODE8 = new TaskField(OUTLINE_CODE8_VALUE);
   public static final TaskField OUTLINE_CODE9 = new TaskField(OUTLINE_CODE9_VALUE);
   public static final TaskField OUTLINE_CODE10 = new TaskField(OUTLINE_CODE10_VALUE);
   public static final TaskField DEADLINE = new TaskField(DEADLINE_VALUE);
   public static final TaskField START_SLACK = new TaskField(START_SLACK_VALUE);
   public static final TaskField FINISH_SLACK = new TaskField(FINISH_SLACK_VALUE);
   public static final TaskField VAC = new TaskField(VAC_VALUE);
   public static final TaskField GROUP_BY_SUMMARY = new TaskField(GROUP_BY_SUMMARY_VALUE);
   public static final TaskField WBS_PREDECESSORS = new TaskField(WBS_PREDECESSORS_VALUE);
   public static final TaskField WBS_SUCCESSORS = new TaskField(WBS_SUCCESSORS_VALUE);
   public static final TaskField RESOURCE_TYPE = new TaskField(RESOURCE_TYPE_VALUE);

   private static final TaskField[] FIELD_ARRAY = new TaskField[452];
   
   static
   {
      FIELD_ARRAY[WORK_VALUE] = WORK;
      FIELD_ARRAY[BASELINE_WORK_VALUE] = BASELINE_WORK;
      FIELD_ARRAY[ACTUAL_WORK_VALUE] = ACTUAL_WORK;
      FIELD_ARRAY[WORK_VARIANCE_VALUE] = WORK_VARIANCE;
      FIELD_ARRAY[REMAINING_WORK_VALUE] = REMAINING_WORK;
      FIELD_ARRAY[COST_VALUE] = COST;
      FIELD_ARRAY[BASELINE_COST_VALUE] = BASELINE_COST;
      FIELD_ARRAY[ACTUAL_COST_VALUE] = ACTUAL_COST;
      FIELD_ARRAY[FIXED_COST_VALUE] = FIXED_COST;
      FIELD_ARRAY[COST_VARIANCE_VALUE] = COST_VARIANCE;
      FIELD_ARRAY[REMAINING_COST_VALUE] = REMAINING_COST;
      FIELD_ARRAY[BCWP_VALUE] = BCWP;
      FIELD_ARRAY[BCWS_VALUE] = BCWS;
      FIELD_ARRAY[SV_VALUE] = SV;
      FIELD_ARRAY[NAME_VALUE] = NAME;
      FIELD_ARRAY[WBS_VALUE] = WBS;
      FIELD_ARRAY[CONSTRAINT_TYPE_VALUE] = CONSTRAINT_TYPE;
      FIELD_ARRAY[CONSTRAINT_DATE_VALUE] = CONSTRAINT_DATE;
      FIELD_ARRAY[CRITICAL_VALUE] = CRITICAL;
      FIELD_ARRAY[LEVELING_DELAY_VALUE] = LEVELING_DELAY;
      FIELD_ARRAY[FREE_SLACK_VALUE] = FREE_SLACK;
      FIELD_ARRAY[TOTAL_SLACK_VALUE] = TOTAL_SLACK;
      FIELD_ARRAY[ID_VALUE] = ID;
      FIELD_ARRAY[MILESTONE_VALUE] = MILESTONE;
      FIELD_ARRAY[PRIORITY_VALUE] = PRIORITY;
      FIELD_ARRAY[SUBPROJECT_FILE_VALUE] = SUBPROJECT_FILE;
      FIELD_ARRAY[BASELINE_DURATION_VALUE] = BASELINE_DURATION;
      FIELD_ARRAY[ACTUAL_DURATION_VALUE] = ACTUAL_DURATION;
      FIELD_ARRAY[DURATION_VALUE] = DURATION;
      FIELD_ARRAY[DURATION_VARIANCE_VALUE] = DURATION_VARIANCE;
      FIELD_ARRAY[REMAINING_DURATION_VALUE] = REMAINING_DURATION;
      FIELD_ARRAY[PERCENT_COMPLETE_VALUE] = PERCENT_COMPLETE;
      FIELD_ARRAY[PERCENT_WORK_COMPLETE_VALUE] = PERCENT_WORK_COMPLETE;
      FIELD_ARRAY[START_VALUE] = START;
      FIELD_ARRAY[FINISH_VALUE] = FINISH;
      FIELD_ARRAY[EARLY_START_VALUE] = EARLY_START;
      FIELD_ARRAY[EARLY_FINISH_VALUE] = EARLY_FINISH;
      FIELD_ARRAY[LATE_START_VALUE] = LATE_START;
      FIELD_ARRAY[LATE_FINISH_VALUE] = LATE_FINISH;
      FIELD_ARRAY[ACTUAL_START_VALUE] = ACTUAL_START;
      FIELD_ARRAY[ACTUAL_FINISH_VALUE] = ACTUAL_FINISH;
      FIELD_ARRAY[BASELINE_START_VALUE] = BASELINE_START;
      FIELD_ARRAY[BASELINE_FINISH_VALUE] = BASELINE_FINISH;
      FIELD_ARRAY[START_VARIANCE_VALUE] = START_VARIANCE;
      FIELD_ARRAY[FINISH_VARIANCE_VALUE] = FINISH_VARIANCE;
      FIELD_ARRAY[PREDECESSORS_VALUE] = PREDECESSORS;
      FIELD_ARRAY[SUCCESSORS_VALUE] = SUCCESSORS;
      FIELD_ARRAY[RESOURCE_NAMES_VALUE] = RESOURCE_NAMES;
      FIELD_ARRAY[RESOURCE_INITIALS_VALUE] = RESOURCE_INITIALS;
      FIELD_ARRAY[TEXT1_VALUE] = TEXT1;
      FIELD_ARRAY[START1_VALUE] = START1;
      FIELD_ARRAY[FINISH1_VALUE] = FINISH1;
      FIELD_ARRAY[TEXT2_VALUE] = TEXT2;
      FIELD_ARRAY[START2_VALUE] = START2;
      FIELD_ARRAY[FINISH2_VALUE] = FINISH2;
      FIELD_ARRAY[TEXT3_VALUE] = TEXT3;
      FIELD_ARRAY[START3_VALUE] = START3;
      FIELD_ARRAY[FINISH3_VALUE] = FINISH3;
      FIELD_ARRAY[TEXT4_VALUE] = TEXT4;
      FIELD_ARRAY[START4_VALUE] = START4;
      FIELD_ARRAY[FINISH4_VALUE] = FINISH4;
      FIELD_ARRAY[TEXT5_VALUE] = TEXT5;
      FIELD_ARRAY[START5_VALUE] = START5;
      FIELD_ARRAY[FINISH5_VALUE] = FINISH5;
      FIELD_ARRAY[TEXT6_VALUE] = TEXT6;
      FIELD_ARRAY[TEXT7_VALUE] = TEXT7;
      FIELD_ARRAY[TEXT8_VALUE] = TEXT8;
      FIELD_ARRAY[TEXT9_VALUE] = TEXT9;
      FIELD_ARRAY[TEXT10_VALUE] = TEXT10;
      FIELD_ARRAY[MARKED_VALUE] = MARKED;
      FIELD_ARRAY[FLAG1_VALUE] = FLAG1;
      FIELD_ARRAY[FLAG2_VALUE] = FLAG2;
      FIELD_ARRAY[FLAG3_VALUE] = FLAG3;
      FIELD_ARRAY[FLAG4_VALUE] = FLAG4;
      FIELD_ARRAY[FLAG5_VALUE] = FLAG5;
      FIELD_ARRAY[FLAG6_VALUE] = FLAG6;
      FIELD_ARRAY[FLAG7_VALUE] = FLAG7;
      FIELD_ARRAY[FLAG8_VALUE] = FLAG8;
      FIELD_ARRAY[FLAG9_VALUE] = FLAG9;
      FIELD_ARRAY[FLAG10_VALUE] = FLAG10;
      FIELD_ARRAY[ROLLUP_VALUE] = ROLLUP;
      FIELD_ARRAY[CV_VALUE] = CV;
      FIELD_ARRAY[PROJECT_VALUE] = PROJECT;
      FIELD_ARRAY[OUTLINE_LEVEL_VALUE] = OUTLINE_LEVEL;
      FIELD_ARRAY[UNIQUE_ID_VALUE] = UNIQUE_ID;
      FIELD_ARRAY[NUMBER1_VALUE] = NUMBER1;
      FIELD_ARRAY[NUMBER2_VALUE] = NUMBER2;
      FIELD_ARRAY[NUMBER3_VALUE] = NUMBER3;
      FIELD_ARRAY[NUMBER4_VALUE] = NUMBER4;
      FIELD_ARRAY[NUMBER5_VALUE] = NUMBER5;
      FIELD_ARRAY[SUMMARY_VALUE] = SUMMARY;
      FIELD_ARRAY[CREATED_VALUE] = CREATED;
      FIELD_ARRAY[NOTES_VALUE] = NOTES;
      FIELD_ARRAY[UNIQUE_ID_PREDECESSORS_VALUE] = UNIQUE_ID_PREDECESSORS;
      FIELD_ARRAY[UNIQUE_ID_SUCCESSORS_VALUE] = UNIQUE_ID_SUCCESSORS;
      FIELD_ARRAY[OBJECTS_VALUE] = OBJECTS;
      FIELD_ARRAY[LINKED_FIELDS_VALUE] = LINKED_FIELDS;
      FIELD_ARRAY[RESUME_VALUE] = RESUME;
      FIELD_ARRAY[STOP_VALUE] = STOP;
      FIELD_ARRAY[OUTLINE_NUMBER_VALUE] = OUTLINE_NUMBER;
      FIELD_ARRAY[DURATION1_VALUE] = DURATION1;
      FIELD_ARRAY[DURATION2_VALUE] = DURATION2;
      FIELD_ARRAY[DURATION3_VALUE] = DURATION3;
      FIELD_ARRAY[COST1_VALUE] = COST1;
      FIELD_ARRAY[COST2_VALUE] = COST2;
      FIELD_ARRAY[COST3_VALUE] = COST3;
      FIELD_ARRAY[HIDEBAR_VALUE] = HIDEBAR;
      FIELD_ARRAY[CONFIRMED_VALUE] = CONFIRMED;
      FIELD_ARRAY[UPDATE_NEEDED_VALUE] = UPDATE_NEEDED;
      FIELD_ARRAY[CONTACT_VALUE] = CONTACT;
      FIELD_ARRAY[RESOURCE_GROUP_VALUE] = RESOURCE_GROUP;
      FIELD_ARRAY[ACWP_VALUE] = ACWP;
      FIELD_ARRAY[TYPE_VALUE] = TYPE;
      FIELD_ARRAY[RECURRING_VALUE] = RECURRING;
      FIELD_ARRAY[EFFORT_DRIVEN_VALUE] = EFFORT_DRIVEN;
      FIELD_ARRAY[OVERTIME_WORK_VALUE] = OVERTIME_WORK;
      FIELD_ARRAY[ACTUAL_OVERTIME_WORK_VALUE] = ACTUAL_OVERTIME_WORK;
      FIELD_ARRAY[REMAINING_OVERTIME_WORK_VALUE] = REMAINING_OVERTIME_WORK;
      FIELD_ARRAY[REGULAR_WORK_VALUE] = REGULAR_WORK;
      FIELD_ARRAY[OVERTIME_COST_VALUE] = OVERTIME_COST;
      FIELD_ARRAY[ACTUAL_OVERTIME_COST_VALUE] = ACTUAL_OVERTIME_COST;
      FIELD_ARRAY[REMAINING_OVERTIME_COST_VALUE] = REMAINING_OVERTIME_COST;
      FIELD_ARRAY[FIXED_COST_ACCRUAL_VALUE] = FIXED_COST_ACCRUAL;
      FIELD_ARRAY[INDICATORS_VALUE] = INDICATORS;
      FIELD_ARRAY[HYPERLINK_VALUE] = HYPERLINK;
      FIELD_ARRAY[HYPERLINK_ADDRESS_VALUE] = HYPERLINK_ADDRESS;
      FIELD_ARRAY[HYPERLINK_SUBADDRESS_VALUE] = HYPERLINK_SUBADDRESS;
      FIELD_ARRAY[HYPERLINK_HREF_VALUE] = HYPERLINK_HREF;
      FIELD_ARRAY[ASSIGNMENT_VALUE] = ASSIGNMENT;
      FIELD_ARRAY[OVERALLOCATED_VALUE] = OVERALLOCATED;
      FIELD_ARRAY[EXTERNAL_TASK_VALUE] = EXTERNAL_TASK;
      FIELD_ARRAY[SUBPROJECT_READ_ONLY_VALUE] = SUBPROJECT_READ_ONLY;
      FIELD_ARRAY[RESPONSE_PENDING_VALUE] = RESPONSE_PENDING;
      FIELD_ARRAY[TEAMSTATUS_PENDING_VALUE] = TEAMSTATUS_PENDING;
      FIELD_ARRAY[LEVELING_CAN_SPLIT_VALUE] = LEVELING_CAN_SPLIT;
      FIELD_ARRAY[LEVEL_ASSIGNMENTS_VALUE] = LEVEL_ASSIGNMENTS;
      FIELD_ARRAY[WORK_CONTOUR_VALUE] = WORK_CONTOUR;
      FIELD_ARRAY[COST4_VALUE] = COST4;
      FIELD_ARRAY[COST5_VALUE] = COST5;
      FIELD_ARRAY[COST6_VALUE] = COST6;
      FIELD_ARRAY[COST7_VALUE] = COST7;
      FIELD_ARRAY[COST8_VALUE] = COST8;
      FIELD_ARRAY[COST9_VALUE] = COST9;
      FIELD_ARRAY[COST10_VALUE] = COST10;
      FIELD_ARRAY[DATE1_VALUE] = DATE1;
      FIELD_ARRAY[DATE2_VALUE] = DATE2;
      FIELD_ARRAY[DATE3_VALUE] = DATE3;
      FIELD_ARRAY[DATE4_VALUE] = DATE4;
      FIELD_ARRAY[DATE5_VALUE] = DATE5;
      FIELD_ARRAY[DATE6_VALUE] = DATE6;
      FIELD_ARRAY[DATE7_VALUE] = DATE7;
      FIELD_ARRAY[DATE8_VALUE] = DATE8;
      FIELD_ARRAY[DATE9_VALUE] = DATE9;
      FIELD_ARRAY[DATE10_VALUE] = DATE10;
      FIELD_ARRAY[DURATION4_VALUE] = DURATION4;
      FIELD_ARRAY[DURATION5_VALUE] = DURATION5;
      FIELD_ARRAY[DURATION6_VALUE] = DURATION6;
      FIELD_ARRAY[DURATION7_VALUE] = DURATION7;
      FIELD_ARRAY[DURATION8_VALUE] = DURATION8;
      FIELD_ARRAY[DURATION9_VALUE] = DURATION9;
      FIELD_ARRAY[DURATION10_VALUE] = DURATION10;
      FIELD_ARRAY[START6_VALUE] = START6;
      FIELD_ARRAY[FINISH6_VALUE] = FINISH6;
      FIELD_ARRAY[START7_VALUE] = START7;
      FIELD_ARRAY[FINISH7_VALUE] = FINISH7;
      FIELD_ARRAY[START8_VALUE] = START8;
      FIELD_ARRAY[FINISH8_VALUE] = FINISH8;
      FIELD_ARRAY[START9_VALUE] = START9;
      FIELD_ARRAY[FINISH9_VALUE] = FINISH9;
      FIELD_ARRAY[START10_VALUE] = START10;
      FIELD_ARRAY[FINISH10_VALUE] = FINISH10;
      FIELD_ARRAY[FLAG11_VALUE] = FLAG11;
      FIELD_ARRAY[FLAG12_VALUE] = FLAG12;
      FIELD_ARRAY[FLAG13_VALUE] = FLAG13;
      FIELD_ARRAY[FLAG14_VALUE] = FLAG14;
      FIELD_ARRAY[FLAG15_VALUE] = FLAG15;
      FIELD_ARRAY[FLAG16_VALUE] = FLAG16;
      FIELD_ARRAY[FLAG17_VALUE] = FLAG17;
      FIELD_ARRAY[FLAG18_VALUE] = FLAG18;
      FIELD_ARRAY[FLAG19_VALUE] = FLAG19;
      FIELD_ARRAY[FLAG20_VALUE] = FLAG20;
      FIELD_ARRAY[NUMBER6_VALUE] = NUMBER6;
      FIELD_ARRAY[NUMBER7_VALUE] = NUMBER7;
      FIELD_ARRAY[NUMBER8_VALUE] = NUMBER8;
      FIELD_ARRAY[NUMBER9_VALUE] = NUMBER9;
      FIELD_ARRAY[NUMBER10_VALUE] = NUMBER10;
      FIELD_ARRAY[NUMBER11_VALUE] = NUMBER11;
      FIELD_ARRAY[NUMBER12_VALUE] = NUMBER12;
      FIELD_ARRAY[NUMBER13_VALUE] = NUMBER13;
      FIELD_ARRAY[NUMBER14_VALUE] = NUMBER14;
      FIELD_ARRAY[NUMBER15_VALUE] = NUMBER15;
      FIELD_ARRAY[NUMBER16_VALUE] = NUMBER16;
      FIELD_ARRAY[NUMBER17_VALUE] = NUMBER17;
      FIELD_ARRAY[NUMBER18_VALUE] = NUMBER18;
      FIELD_ARRAY[NUMBER19_VALUE] = NUMBER19;
      FIELD_ARRAY[NUMBER20_VALUE] = NUMBER20;
      FIELD_ARRAY[TEXT11_VALUE] = TEXT11;
      FIELD_ARRAY[TEXT12_VALUE] = TEXT12;
      FIELD_ARRAY[TEXT13_VALUE] = TEXT13;
      FIELD_ARRAY[TEXT14_VALUE] = TEXT14;
      FIELD_ARRAY[TEXT15_VALUE] = TEXT15;
      FIELD_ARRAY[TEXT16_VALUE] = TEXT16;
      FIELD_ARRAY[TEXT17_VALUE] = TEXT17;
      FIELD_ARRAY[TEXT18_VALUE] = TEXT18;
      FIELD_ARRAY[TEXT19_VALUE] = TEXT19;
      FIELD_ARRAY[TEXT20_VALUE] = TEXT20;
      FIELD_ARRAY[TEXT21_VALUE] = TEXT21;
      FIELD_ARRAY[TEXT22_VALUE] = TEXT22;
      FIELD_ARRAY[TEXT23_VALUE] = TEXT23;
      FIELD_ARRAY[TEXT24_VALUE] = TEXT24;
      FIELD_ARRAY[TEXT25_VALUE] = TEXT25;
      FIELD_ARRAY[TEXT26_VALUE] = TEXT26;
      FIELD_ARRAY[TEXT27_VALUE] = TEXT27;
      FIELD_ARRAY[TEXT28_VALUE] = TEXT28;
      FIELD_ARRAY[TEXT29_VALUE] = TEXT29;
      FIELD_ARRAY[TEXT30_VALUE] = TEXT30;
      FIELD_ARRAY[RESOURCE_PHONETICS_VALUE] = RESOURCE_PHONETICS;
      FIELD_ARRAY[ASSIGNMENT_DELAY_VALUE] = ASSIGNMENT_DELAY;
      FIELD_ARRAY[ASSIGNMENT_UNITS_VALUE] = ASSIGNMENT_UNITS;
      FIELD_ARRAY[COST_RATE_TABLE_VALUE] = COST_RATE_TABLE;
      FIELD_ARRAY[PRELEVELED_START_VALUE] = PRELEVELED_START;
      FIELD_ARRAY[PRELEVELED_FINISH_VALUE] = PRELEVELED_FINISH;
      FIELD_ARRAY[ESTIMATED_VALUE] = ESTIMATED;
      FIELD_ARRAY[IGNORE_RESOURCE_CALENDAR_VALUE] = IGNORE_RESOURCE_CALENDAR;
      FIELD_ARRAY[CALENDAR_VALUE] = CALENDAR;
      FIELD_ARRAY[OUTLINE_CODE1_VALUE] = OUTLINE_CODE1;
      FIELD_ARRAY[OUTLINE_CODE2_VALUE] = OUTLINE_CODE2;
      FIELD_ARRAY[OUTLINE_CODE3_VALUE] = OUTLINE_CODE3;
      FIELD_ARRAY[OUTLINE_CODE4_VALUE] = OUTLINE_CODE4;
      FIELD_ARRAY[OUTLINE_CODE5_VALUE] = OUTLINE_CODE5;
      FIELD_ARRAY[OUTLINE_CODE6_VALUE] = OUTLINE_CODE6;
      FIELD_ARRAY[OUTLINE_CODE7_VALUE] = OUTLINE_CODE7;
      FIELD_ARRAY[OUTLINE_CODE8_VALUE] = OUTLINE_CODE8;
      FIELD_ARRAY[OUTLINE_CODE9_VALUE] = OUTLINE_CODE9;
      FIELD_ARRAY[OUTLINE_CODE10_VALUE] = OUTLINE_CODE10;
      FIELD_ARRAY[DEADLINE_VALUE] = DEADLINE;
      FIELD_ARRAY[START_SLACK_VALUE] = START_SLACK;
      FIELD_ARRAY[FINISH_SLACK_VALUE] = FINISH_SLACK;
      FIELD_ARRAY[VAC_VALUE] = VAC;
      FIELD_ARRAY[GROUP_BY_SUMMARY_VALUE] = GROUP_BY_SUMMARY;
      FIELD_ARRAY[WBS_PREDECESSORS_VALUE] = WBS_PREDECESSORS;
      FIELD_ARRAY[WBS_SUCCESSORS_VALUE] = WBS_SUCCESSORS;
      FIELD_ARRAY[RESOURCE_TYPE_VALUE] = RESOURCE_TYPE;      
   }
   
   private int m_value;
}
