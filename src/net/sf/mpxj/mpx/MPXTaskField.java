/*
 * file:       MPXTaskField.java
 * author:     Jon Iles
 * copyright:  (c) Packwood Software Limited 2005
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

package net.sf.mpxj.mpx;

import net.sf.mpxj.TaskField;

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

   private static final int PERCENTAGE_COMPLETE = 44;
   private static final int PERCENTAGE_WORK_COMPLETE = 25;
   private static final int ACTUAL_COST = 32;
   private static final int ACTUAL_DURATION = 42;
   private static final int ACTUAL_FINISH = 59;
   private static final int ACTUAL_START = 58;
   private static final int ACTUAL_WORK = 22;
   private static final int BASELINE_COST = 31;
   private static final int BASELINE_DURATION = 41;
   private static final int BASELINE_FINISH = 57;
   private static final int BASELINE_START = 56;
   private static final int BASELINE_WORK = 21;
   private static final int BCWP = 86;
   private static final int BCWS = 85;
   private static final int CONFIRMED = 135;
   private static final int CONSTRAINT_DATE = 68;
   private static final int CONSTRAINT_TYPE = 91;
   private static final int CONTACT = 15;
   private static final int COST = 30;
   private static final int COST1 = 36;
   private static final int COST2 = 37;
   private static final int COST3 = 38;
   private static final int COST_VARIANCE = 34;
   private static final int CREATE_DATE = 125;
   private static final int CRITICAL = 82;
   private static final int CV = 88;
   private static final int DELAY = 92;
   private static final int DURATION = 40;
   private static final int DURATION1 = 46;
   private static final int DURATION2 = 47;
   private static final int DURATION3 = 48;
   private static final int DURATION_VARIANCE = 45;
   private static final int EARLY_FINISH = 53;
   private static final int EARLY_START = 52;
   private static final int FINISH = 51;
   private static final int FINISH1 = 61;
   private static final int FINISH2 = 63;
   private static final int FINISH3 = 65;
   private static final int FINISH4 = 127;
   private static final int FINISH5 = 129;
   private static final int FINISH_VARIANCE = 67;
   private static final int TYPE = 80;
   private static final int FIXED_COST = 35;
   private static final int FLAG1 = 110;
   private static final int FLAG2 = 111;
   private static final int FLAG3 = 112;
   private static final int FLAG4 = 113;
   private static final int FLAG5 = 114;
   private static final int FLAG6 = 115;
   private static final int FLAG7 = 116;
   private static final int FLAG8 = 117;
   private static final int FLAG9 = 118;
   private static final int FLAG10 = 119;
   private static final int FREE_SLACK = 93;
   private static final int HIDE_BAR = 123;
   private static final int ID = 90;
   private static final int LATE_FINISH = 55;
   private static final int LATE_START = 54;
   private static final int LINKED_FIELDS = 122;
   private static final int MARKED = 83;
   private static final int MILESTONE = 81;
   private static final int NAME = 1;
   //private static final int NOTES = 14;
   private static final int NUMBER1 = 140;
   private static final int NUMBER2 = 141;
   private static final int NUMBER3 = 142;
   private static final int NUMBER4 = 143;
   private static final int NUMBER5 = 144;
   private static final int OBJECTS = 121;
   private static final int OUTLINE_LEVEL = 3;
   private static final int OUTLINE_NUMBER = 99;
   private static final int PREDECESSORS = 70;
   private static final int PRIORITY = 95;
   private static final int PROJECT = 97;
   private static final int REMAINING_COST = 33;
   private static final int REMAINING_DURATION = 43;
   private static final int REMAINING_WORK = 23;
   private static final int RESOURCE_GROUP = 16;
   private static final int RESOURCE_INITIALS = 73;
   private static final int RESOURCE_NAMES = 72;
   private static final int RESUME = 151;
   private static final int RESUME_NO_EARLIER_THAN = 152;
   private static final int ROLLUP = 84;
   private static final int START = 50;
   private static final int START1 = 60;
   private static final int START2 = 62;
   private static final int START3 = 64;
   private static final int START4 = 126;
   private static final int START5 = 128;
   private static final int START_VARIANCE = 66;
   private static final int STOP = 150;
   private static final int SUBPROJECT_NAME = 96;
   private static final int SUCCESSORS = 71;
   private static final int SUMMARY = 120;
   private static final int SV = 87;
   private static final int TEXT1 = 4;
   private static final int TEXT2 = 5;
   private static final int TEXT3 = 6;
   private static final int TEXT4 = 7;
   private static final int TEXT5 = 8;
   private static final int TEXT6 = 9;
   private static final int TEXT7 = 10;
   private static final int TEXT8 = 11;
   private static final int TEXT9 = 12;
   private static final int TEXT10 = 13;
   private static final int TOTAL_SLACK = 94;
   private static final int UNIQUE_ID = 98;
   private static final int UNIQUE_ID_PREDECESSORS = 74;
   private static final int UNIQUE_ID_SUCCESSORS = 75;
   private static final int UPDATE_NEEDED = 136;
   private static final int WBS = 2;
   private static final int WORK = 20;
   private static final int WORK_VARIANCE = 24;

   public static final int MAX_FIELDS = 153;

   private static final TaskField[] MPX_MPXJ_ARRAY = new TaskField[MAX_FIELDS];

   static
   {
      MPX_MPXJ_ARRAY[PERCENTAGE_COMPLETE] = TaskField.PERCENT_COMPLETE;
      MPX_MPXJ_ARRAY[PERCENTAGE_WORK_COMPLETE] = TaskField.PERCENT_WORK_COMPLETE;
      MPX_MPXJ_ARRAY[ACTUAL_COST] = TaskField.ACTUAL_COST;
      MPX_MPXJ_ARRAY[ACTUAL_DURATION] = TaskField.ACTUAL_DURATION;
      MPX_MPXJ_ARRAY[ACTUAL_FINISH] = TaskField.ACTUAL_FINISH;
      MPX_MPXJ_ARRAY[ACTUAL_START] = TaskField.ACTUAL_START;
      MPX_MPXJ_ARRAY[ACTUAL_WORK] = TaskField.ACTUAL_WORK;
      MPX_MPXJ_ARRAY[BASELINE_COST] = TaskField.BASELINE_COST;
      MPX_MPXJ_ARRAY[BASELINE_DURATION] = TaskField.BASELINE_DURATION;
      MPX_MPXJ_ARRAY[BASELINE_FINISH] = TaskField.BASELINE_FINISH;
      MPX_MPXJ_ARRAY[BASELINE_START] = TaskField.BASELINE_START;
      MPX_MPXJ_ARRAY[BASELINE_WORK] = TaskField.BASELINE_WORK;
      MPX_MPXJ_ARRAY[BCWP] = TaskField.BCWP;
      MPX_MPXJ_ARRAY[BCWS] = TaskField.BCWS;
      MPX_MPXJ_ARRAY[CONFIRMED] = TaskField.CONFIRMED;
      MPX_MPXJ_ARRAY[CONSTRAINT_DATE] = TaskField.CONSTRAINT_DATE;
      MPX_MPXJ_ARRAY[CONSTRAINT_TYPE] = TaskField.CONSTRAINT_TYPE;
      MPX_MPXJ_ARRAY[CONTACT] = TaskField.CONTACT;
      MPX_MPXJ_ARRAY[COST] = TaskField.COST;
      MPX_MPXJ_ARRAY[COST1] = TaskField.COST1;
      MPX_MPXJ_ARRAY[COST2] = TaskField.COST2;
      MPX_MPXJ_ARRAY[COST3] = TaskField.COST3;
      MPX_MPXJ_ARRAY[COST_VARIANCE] = TaskField.COST_VARIANCE;
      MPX_MPXJ_ARRAY[CREATE_DATE] = TaskField.CREATED;
      MPX_MPXJ_ARRAY[CRITICAL] = TaskField.CRITICAL;
      MPX_MPXJ_ARRAY[CV] = TaskField.CV;
      MPX_MPXJ_ARRAY[DELAY] = TaskField.LEVELING_DELAY;
      MPX_MPXJ_ARRAY[DURATION] = TaskField.DURATION;
      MPX_MPXJ_ARRAY[DURATION1] = TaskField.DURATION1;
      MPX_MPXJ_ARRAY[DURATION2] = TaskField.DURATION2;
      MPX_MPXJ_ARRAY[DURATION3] = TaskField.DURATION3;
      MPX_MPXJ_ARRAY[DURATION_VARIANCE] = TaskField.DURATION_VARIANCE;
      MPX_MPXJ_ARRAY[EARLY_FINISH] = TaskField.EARLY_FINISH;
      MPX_MPXJ_ARRAY[EARLY_START] = TaskField.EARLY_START;
      MPX_MPXJ_ARRAY[FINISH] = TaskField.FINISH;
      MPX_MPXJ_ARRAY[FINISH1] = TaskField.FINISH1;
      MPX_MPXJ_ARRAY[FINISH2] = TaskField.FINISH2;
      MPX_MPXJ_ARRAY[FINISH3] = TaskField.FINISH3;
      MPX_MPXJ_ARRAY[FINISH4] = TaskField.FINISH4;
      MPX_MPXJ_ARRAY[FINISH5] = TaskField.FINISH5;
      MPX_MPXJ_ARRAY[FINISH_VARIANCE] = TaskField.FINISH_VARIANCE;
      MPX_MPXJ_ARRAY[TYPE] = TaskField.TYPE;
      MPX_MPXJ_ARRAY[FIXED_COST] = TaskField.FIXED_COST;
      MPX_MPXJ_ARRAY[FLAG1] = TaskField.FLAG1;
      MPX_MPXJ_ARRAY[FLAG2] = TaskField.FLAG2;
      MPX_MPXJ_ARRAY[FLAG3] = TaskField.FLAG3;
      MPX_MPXJ_ARRAY[FLAG4] = TaskField.FLAG4;
      MPX_MPXJ_ARRAY[FLAG5] = TaskField.FLAG5;
      MPX_MPXJ_ARRAY[FLAG6] = TaskField.FLAG6;
      MPX_MPXJ_ARRAY[FLAG7] = TaskField.FLAG7;
      MPX_MPXJ_ARRAY[FLAG8] = TaskField.FLAG8;
      MPX_MPXJ_ARRAY[FLAG9] = TaskField.FLAG9;
      MPX_MPXJ_ARRAY[FLAG10] = TaskField.FLAG10;
      MPX_MPXJ_ARRAY[FREE_SLACK] = TaskField.FREE_SLACK;
      MPX_MPXJ_ARRAY[HIDE_BAR] = TaskField.HIDEBAR;
      MPX_MPXJ_ARRAY[ID] = TaskField.ID;
      MPX_MPXJ_ARRAY[LATE_FINISH] = TaskField.LATE_FINISH;
      MPX_MPXJ_ARRAY[LATE_START] = TaskField.LATE_START;
      MPX_MPXJ_ARRAY[LINKED_FIELDS] = TaskField.LINKED_FIELDS;
      MPX_MPXJ_ARRAY[MARKED] = TaskField.MARKED;
      MPX_MPXJ_ARRAY[MILESTONE] = TaskField.MILESTONE;
      MPX_MPXJ_ARRAY[NAME] = TaskField.NAME;
      //MPX_MPXJ_ARRAY[NOTES] = TaskField.NOTES;
      MPX_MPXJ_ARRAY[NUMBER1] = TaskField.NUMBER1;
      MPX_MPXJ_ARRAY[NUMBER2] = TaskField.NUMBER2;
      MPX_MPXJ_ARRAY[NUMBER3] = TaskField.NUMBER3;
      MPX_MPXJ_ARRAY[NUMBER4] = TaskField.NUMBER4;
      MPX_MPXJ_ARRAY[NUMBER5] = TaskField.NUMBER5;
      MPX_MPXJ_ARRAY[OBJECTS] = TaskField.OBJECTS;
      MPX_MPXJ_ARRAY[OUTLINE_LEVEL] = TaskField.OUTLINE_LEVEL;
      MPX_MPXJ_ARRAY[OUTLINE_NUMBER] = TaskField.OUTLINE_NUMBER;
      MPX_MPXJ_ARRAY[PREDECESSORS] = TaskField.PREDECESSORS;
      MPX_MPXJ_ARRAY[PRIORITY] = TaskField.PRIORITY;
      MPX_MPXJ_ARRAY[PROJECT] = TaskField.PROJECT;
      MPX_MPXJ_ARRAY[REMAINING_COST] = TaskField.REMAINING_COST;
      MPX_MPXJ_ARRAY[REMAINING_DURATION] = TaskField.REMAINING_DURATION;
      MPX_MPXJ_ARRAY[REMAINING_WORK] = TaskField.REMAINING_WORK;
      MPX_MPXJ_ARRAY[RESOURCE_GROUP] = TaskField.RESOURCE_GROUP;
      MPX_MPXJ_ARRAY[RESOURCE_INITIALS] = TaskField.RESOURCE_INITIALS;
      MPX_MPXJ_ARRAY[RESOURCE_NAMES] = TaskField.RESOURCE_NAMES;
      MPX_MPXJ_ARRAY[RESUME] = TaskField.RESUME;
      MPX_MPXJ_ARRAY[RESUME_NO_EARLIER_THAN] = TaskField.RESUME;
      MPX_MPXJ_ARRAY[ROLLUP] = TaskField.ROLLUP;
      MPX_MPXJ_ARRAY[START] = TaskField.START;
      MPX_MPXJ_ARRAY[START1] = TaskField.START1;
      MPX_MPXJ_ARRAY[START2] = TaskField.START2;
      MPX_MPXJ_ARRAY[START3] = TaskField.START3;
      MPX_MPXJ_ARRAY[START4] = TaskField.START4;
      MPX_MPXJ_ARRAY[START5] = TaskField.START5;
      MPX_MPXJ_ARRAY[START_VARIANCE] = TaskField.START_VARIANCE;
      MPX_MPXJ_ARRAY[STOP] = TaskField.STOP;
      MPX_MPXJ_ARRAY[SUBPROJECT_NAME] = TaskField.SUBPROJECT_FILE;
      MPX_MPXJ_ARRAY[SUCCESSORS] = TaskField.SUCCESSORS;
      MPX_MPXJ_ARRAY[SUMMARY] = TaskField.SUMMARY;
      MPX_MPXJ_ARRAY[SV] = TaskField.SV;
      MPX_MPXJ_ARRAY[TEXT1] = TaskField.TEXT1;
      MPX_MPXJ_ARRAY[TEXT2] = TaskField.TEXT2;
      MPX_MPXJ_ARRAY[TEXT3] = TaskField.TEXT3;
      MPX_MPXJ_ARRAY[TEXT4] = TaskField.TEXT4;
      MPX_MPXJ_ARRAY[TEXT5] = TaskField.TEXT5;
      MPX_MPXJ_ARRAY[TEXT6] = TaskField.TEXT6;
      MPX_MPXJ_ARRAY[TEXT7] = TaskField.TEXT7;
      MPX_MPXJ_ARRAY[TEXT8] = TaskField.TEXT8;
      MPX_MPXJ_ARRAY[TEXT9] = TaskField.TEXT9;
      MPX_MPXJ_ARRAY[TEXT10] = TaskField.TEXT10;
      MPX_MPXJ_ARRAY[TOTAL_SLACK] = TaskField.TOTAL_SLACK;
      MPX_MPXJ_ARRAY[UNIQUE_ID] = TaskField.UNIQUE_ID;
      MPX_MPXJ_ARRAY[UNIQUE_ID_PREDECESSORS] = TaskField.UNIQUE_ID_PREDECESSORS;
      MPX_MPXJ_ARRAY[UNIQUE_ID_SUCCESSORS] = TaskField.UNIQUE_ID_SUCCESSORS;
      MPX_MPXJ_ARRAY[UPDATE_NEEDED] = TaskField.UPDATE_NEEDED;
      MPX_MPXJ_ARRAY[WBS] = TaskField.WBS;
      MPX_MPXJ_ARRAY[WORK] = TaskField.WORK;
      MPX_MPXJ_ARRAY[WORK_VARIANCE] = TaskField.WORK_VARIANCE;
   }

   private static final int[] MPXJ_MPX_ARRAY = new int[TaskField.MAX_VALUE];

   static
   {
      MPXJ_MPX_ARRAY[TaskField.PERCENT_COMPLETE_VALUE] = PERCENTAGE_COMPLETE;
      MPXJ_MPX_ARRAY[TaskField.PERCENT_WORK_COMPLETE_VALUE] = PERCENTAGE_WORK_COMPLETE;
      MPXJ_MPX_ARRAY[TaskField.ACTUAL_COST_VALUE] = ACTUAL_COST;
      MPXJ_MPX_ARRAY[TaskField.ACTUAL_DURATION_VALUE] = ACTUAL_DURATION;
      MPXJ_MPX_ARRAY[TaskField.ACTUAL_FINISH_VALUE] = ACTUAL_FINISH;
      MPXJ_MPX_ARRAY[TaskField.ACTUAL_START_VALUE] = ACTUAL_START;
      MPXJ_MPX_ARRAY[TaskField.ACTUAL_WORK_VALUE] = ACTUAL_WORK;
      MPXJ_MPX_ARRAY[TaskField.BASELINE_COST_VALUE] = BASELINE_COST;
      MPXJ_MPX_ARRAY[TaskField.BASELINE_DURATION_VALUE] = BASELINE_DURATION;
      MPXJ_MPX_ARRAY[TaskField.BASELINE_FINISH_VALUE] = BASELINE_FINISH;
      MPXJ_MPX_ARRAY[TaskField.BASELINE_START_VALUE] = BASELINE_START;
      MPXJ_MPX_ARRAY[TaskField.BASELINE_WORK_VALUE] = BASELINE_WORK;
      MPXJ_MPX_ARRAY[TaskField.BCWP_VALUE] = BCWP;
      MPXJ_MPX_ARRAY[TaskField.BCWS_VALUE] = BCWS;
      MPXJ_MPX_ARRAY[TaskField.CONFIRMED_VALUE] = CONFIRMED;
      MPXJ_MPX_ARRAY[TaskField.CONSTRAINT_DATE_VALUE] = CONSTRAINT_DATE;
      MPXJ_MPX_ARRAY[TaskField.CONSTRAINT_TYPE_VALUE] = CONSTRAINT_TYPE;
      MPXJ_MPX_ARRAY[TaskField.CONTACT_VALUE] = CONTACT;
      MPXJ_MPX_ARRAY[TaskField.COST_VALUE] = COST;
      MPXJ_MPX_ARRAY[TaskField.COST1_VALUE] = COST1;
      MPXJ_MPX_ARRAY[TaskField.COST2_VALUE] = COST2;
      MPXJ_MPX_ARRAY[TaskField.COST3_VALUE] = COST3;
      MPXJ_MPX_ARRAY[TaskField.COST_VARIANCE_VALUE] = COST_VARIANCE;
      MPXJ_MPX_ARRAY[TaskField.CREATED_VALUE] = CREATE_DATE;
      MPXJ_MPX_ARRAY[TaskField.CRITICAL_VALUE] = CRITICAL;
      MPXJ_MPX_ARRAY[TaskField.CV_VALUE] = CV;
      MPXJ_MPX_ARRAY[TaskField.LEVELING_DELAY_VALUE] = DELAY;
      MPXJ_MPX_ARRAY[TaskField.DURATION_VALUE] = DURATION;
      MPXJ_MPX_ARRAY[TaskField.DURATION1_VALUE] = DURATION1;
      MPXJ_MPX_ARRAY[TaskField.DURATION2_VALUE] = DURATION2;
      MPXJ_MPX_ARRAY[TaskField.DURATION3_VALUE] = DURATION3;
      MPXJ_MPX_ARRAY[TaskField.DURATION_VARIANCE_VALUE] = DURATION_VARIANCE;
      MPXJ_MPX_ARRAY[TaskField.EARLY_FINISH_VALUE] = EARLY_FINISH;
      MPXJ_MPX_ARRAY[TaskField.EARLY_START_VALUE] = EARLY_START;
      MPXJ_MPX_ARRAY[TaskField.FINISH_VALUE] = FINISH;
      MPXJ_MPX_ARRAY[TaskField.FINISH1_VALUE] = FINISH1;
      MPXJ_MPX_ARRAY[TaskField.FINISH2_VALUE] = FINISH2;
      MPXJ_MPX_ARRAY[TaskField.FINISH3_VALUE] = FINISH3;
      MPXJ_MPX_ARRAY[TaskField.FINISH4_VALUE] = FINISH4;
      MPXJ_MPX_ARRAY[TaskField.FINISH5_VALUE] = FINISH5;
      MPXJ_MPX_ARRAY[TaskField.FINISH_VARIANCE_VALUE] = FINISH_VARIANCE;
      MPXJ_MPX_ARRAY[TaskField.TYPE_VALUE] = TYPE;
      MPXJ_MPX_ARRAY[TaskField.FIXED_COST_VALUE] = FIXED_COST;
      MPXJ_MPX_ARRAY[TaskField.FLAG1_VALUE] = FLAG1;
      MPXJ_MPX_ARRAY[TaskField.FLAG2_VALUE] = FLAG2;
      MPXJ_MPX_ARRAY[TaskField.FLAG3_VALUE] = FLAG3;
      MPXJ_MPX_ARRAY[TaskField.FLAG4_VALUE] = FLAG4;
      MPXJ_MPX_ARRAY[TaskField.FLAG5_VALUE] = FLAG5;
      MPXJ_MPX_ARRAY[TaskField.FLAG6_VALUE] = FLAG6;
      MPXJ_MPX_ARRAY[TaskField.FLAG7_VALUE] = FLAG7;
      MPXJ_MPX_ARRAY[TaskField.FLAG8_VALUE] = FLAG8;
      MPXJ_MPX_ARRAY[TaskField.FLAG9_VALUE] = FLAG9;
      MPXJ_MPX_ARRAY[TaskField.FLAG10_VALUE] = FLAG10;
      MPXJ_MPX_ARRAY[TaskField.FREE_SLACK_VALUE] = FREE_SLACK;
      MPXJ_MPX_ARRAY[TaskField.HIDEBAR_VALUE] = HIDE_BAR;
      MPXJ_MPX_ARRAY[TaskField.ID_VALUE] = ID;
      MPXJ_MPX_ARRAY[TaskField.LATE_FINISH_VALUE] = LATE_FINISH;
      MPXJ_MPX_ARRAY[TaskField.LATE_START_VALUE] = LATE_START;
      MPXJ_MPX_ARRAY[TaskField.LINKED_FIELDS_VALUE] = LINKED_FIELDS;
      MPXJ_MPX_ARRAY[TaskField.MARKED_VALUE] = MARKED;
      MPXJ_MPX_ARRAY[TaskField.MILESTONE_VALUE] = MILESTONE;
      MPXJ_MPX_ARRAY[TaskField.NAME_VALUE] = NAME;
      //MPXJ_MPX_ARRAY[TaskField.NOTES_VALUE] = NOTES;
      MPXJ_MPX_ARRAY[TaskField.NUMBER1_VALUE] = NUMBER1;
      MPXJ_MPX_ARRAY[TaskField.NUMBER2_VALUE] = NUMBER2;
      MPXJ_MPX_ARRAY[TaskField.NUMBER3_VALUE] = NUMBER3;
      MPXJ_MPX_ARRAY[TaskField.NUMBER4_VALUE] = NUMBER4;
      MPXJ_MPX_ARRAY[TaskField.NUMBER5_VALUE] = NUMBER5;
      MPXJ_MPX_ARRAY[TaskField.OBJECTS_VALUE] = OBJECTS;
      MPXJ_MPX_ARRAY[TaskField.OUTLINE_LEVEL_VALUE] = OUTLINE_LEVEL;
      MPXJ_MPX_ARRAY[TaskField.OUTLINE_NUMBER_VALUE] = OUTLINE_NUMBER;
      MPXJ_MPX_ARRAY[TaskField.PREDECESSORS_VALUE] = PREDECESSORS;
      MPXJ_MPX_ARRAY[TaskField.PRIORITY_VALUE] = PRIORITY;
      MPXJ_MPX_ARRAY[TaskField.PROJECT_VALUE] = PROJECT;
      MPXJ_MPX_ARRAY[TaskField.REMAINING_COST_VALUE] = REMAINING_COST;
      MPXJ_MPX_ARRAY[TaskField.REMAINING_DURATION_VALUE] = REMAINING_DURATION;
      MPXJ_MPX_ARRAY[TaskField.REMAINING_WORK_VALUE] = REMAINING_WORK;
      MPXJ_MPX_ARRAY[TaskField.RESOURCE_GROUP_VALUE] = RESOURCE_GROUP;
      MPXJ_MPX_ARRAY[TaskField.RESOURCE_INITIALS_VALUE] = RESOURCE_INITIALS;
      MPXJ_MPX_ARRAY[TaskField.RESOURCE_NAMES_VALUE] = RESOURCE_NAMES;
      MPXJ_MPX_ARRAY[TaskField.RESUME_VALUE] = RESUME;
      MPXJ_MPX_ARRAY[TaskField.RESUME_VALUE] = RESUME_NO_EARLIER_THAN;
      MPXJ_MPX_ARRAY[TaskField.ROLLUP_VALUE] = ROLLUP;
      MPXJ_MPX_ARRAY[TaskField.START_VALUE] = START;
      MPXJ_MPX_ARRAY[TaskField.START1_VALUE] = START1;
      MPXJ_MPX_ARRAY[TaskField.START2_VALUE] = START2;
      MPXJ_MPX_ARRAY[TaskField.START3_VALUE] = START3;
      MPXJ_MPX_ARRAY[TaskField.START4_VALUE] = START4;
      MPXJ_MPX_ARRAY[TaskField.START5_VALUE] = START5;
      MPXJ_MPX_ARRAY[TaskField.START_VARIANCE_VALUE] = START_VARIANCE;
      MPXJ_MPX_ARRAY[TaskField.STOP_VALUE] = STOP;
      MPXJ_MPX_ARRAY[TaskField.SUBPROJECT_FILE_VALUE] = SUBPROJECT_NAME;
      MPXJ_MPX_ARRAY[TaskField.SUCCESSORS_VALUE] = SUCCESSORS;
      MPXJ_MPX_ARRAY[TaskField.SUMMARY_VALUE] = SUMMARY;
      MPXJ_MPX_ARRAY[TaskField.SV_VALUE] = SV;
      MPXJ_MPX_ARRAY[TaskField.TEXT1_VALUE] = TEXT1;
      MPXJ_MPX_ARRAY[TaskField.TEXT2_VALUE] = TEXT2;
      MPXJ_MPX_ARRAY[TaskField.TEXT3_VALUE] = TEXT3;
      MPXJ_MPX_ARRAY[TaskField.TEXT4_VALUE] = TEXT4;
      MPXJ_MPX_ARRAY[TaskField.TEXT5_VALUE] = TEXT5;
      MPXJ_MPX_ARRAY[TaskField.TEXT6_VALUE] = TEXT6;
      MPXJ_MPX_ARRAY[TaskField.TEXT7_VALUE] = TEXT7;
      MPXJ_MPX_ARRAY[TaskField.TEXT8_VALUE] = TEXT8;
      MPXJ_MPX_ARRAY[TaskField.TEXT9_VALUE] = TEXT9;
      MPXJ_MPX_ARRAY[TaskField.TEXT10_VALUE] = TEXT10;
      MPXJ_MPX_ARRAY[TaskField.TOTAL_SLACK_VALUE] = TOTAL_SLACK;
      MPXJ_MPX_ARRAY[TaskField.UNIQUE_ID_VALUE] = UNIQUE_ID;
      MPXJ_MPX_ARRAY[TaskField.UNIQUE_ID_PREDECESSORS_VALUE] = UNIQUE_ID_PREDECESSORS;
      MPXJ_MPX_ARRAY[TaskField.UNIQUE_ID_SUCCESSORS_VALUE] = UNIQUE_ID_SUCCESSORS;
      MPXJ_MPX_ARRAY[TaskField.UPDATE_NEEDED_VALUE] = UPDATE_NEEDED;
      MPXJ_MPX_ARRAY[TaskField.WBS_VALUE] = WBS;
      MPXJ_MPX_ARRAY[TaskField.WORK_VALUE] = WORK;
      MPXJ_MPX_ARRAY[TaskField.WORK_VARIANCE_VALUE] = WORK_VARIANCE;
   }

}
