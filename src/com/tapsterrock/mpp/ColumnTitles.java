/*
 * file:       ColumnTitles.java
 * author:     Jon Iles
 * copyright:  (c) Tapster Rock Limited 2003
 * date:       05/11/2003
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

import java.util.ListResourceBundle;

/**
 * Resource bundle representing the default titles of columns that can
 * appear on tables of task and resource data. Translations of this
 * resource bundle are welcome! Note that the titles that appear here
 * should be identical to the titles as they appear in MS Project, so
 * assuming you have access to a non-English version MS Project, generating
 * a translation should be very easy.
 */
public class ColumnTitles extends ListResourceBundle
{
   public Object[][] getContents()
   {
      return (RESOURCES);
   }

   private static final int MAX_RESOURCE_COLUMNS = 312;
   private static final int MAX_TASK_COLUMNS = 452;
      
   private static final String[] RESOURCE_COLUMNS = new String[MAX_RESOURCE_COLUMNS];
   private static final String[] TASK_COLUMNS = new String[MAX_TASK_COLUMNS];
   
   static
   {
      TASK_COLUMNS[Column.TASK_WORK] = "Work";
      TASK_COLUMNS[Column.TASK_BASELINE_WORK] = "Baseline Work";
      TASK_COLUMNS[Column.TASK_ACTUAL_WORK] = "Actual Work";
      TASK_COLUMNS[Column.TASK_WORK_VARIANCE] = "Work Variance";
      TASK_COLUMNS[Column.TASK_REMAINING_WORK] = "Remaining Work";
      TASK_COLUMNS[Column.TASK_COST] = "Cost";
      TASK_COLUMNS[Column.TASK_BASELINE_COST] = "Baseline Cost";
      TASK_COLUMNS[Column.TASK_ACTUAL_COST] = "Actual Cost";
      TASK_COLUMNS[Column.TASK_FIXED_COST] = "Fixed Cost";
      TASK_COLUMNS[Column.TASK_COST_VARIANCE] = "Cost Variance";
      TASK_COLUMNS[Column.TASK_REMAINING_COST] = "Remaining Cost";
      TASK_COLUMNS[Column.TASK_BCWP] = "BCWP";
      TASK_COLUMNS[Column.TASK_BCWS] = "BCWS";
      TASK_COLUMNS[Column.TASK_SV] = "SV";
      TASK_COLUMNS[Column.TASK_NAME] = "Name";
      TASK_COLUMNS[Column.TASK_WBS] = "WBS";
      TASK_COLUMNS[Column.TASK_CONSTRAINT_TYPE] = "Constraint Type";
      TASK_COLUMNS[Column.TASK_CONSTRAINT_DATE] = "Constraint Date";
      TASK_COLUMNS[Column.TASK_CRITICAL] = "Critical";
      TASK_COLUMNS[Column.TASK_LEVELING_DELAY] = "Leveling Delay";
      TASK_COLUMNS[Column.TASK_FREE_SLACK] = "Free Slack";
      TASK_COLUMNS[Column.TASK_TOTAL_SLACK] = "Total Slack";
      TASK_COLUMNS[Column.TASK_ID] = "ID";
      TASK_COLUMNS[Column.TASK_MILESTONE] = "Milestone";
      TASK_COLUMNS[Column.TASK_PRIORITY] = "Priority";
      TASK_COLUMNS[Column.TASK_SUBPROJECT_FILE] = "Subproject File";
      TASK_COLUMNS[Column.TASK_BASELINE_DURATION] = "Baseline Duration";
      TASK_COLUMNS[Column.TASK_ACTUAL_DURATION] = "Actual Duration";
      TASK_COLUMNS[Column.TASK_DURATION] = "Duration";
      TASK_COLUMNS[Column.TASK_DURATION_VARIANCE] = "Duration Variance";
      TASK_COLUMNS[Column.TASK_REMAINING_DURATION] = "Remaining Duration";
      TASK_COLUMNS[Column.TASK_PERCENT_COMPLETE] = "% Complete";
      TASK_COLUMNS[Column.TASK_PERCENT_WORK_COMPLETE] = "% Work Complete";
      TASK_COLUMNS[Column.TASK_START] = "Start";
      TASK_COLUMNS[Column.TASK_FINISH] = "Finish";
      TASK_COLUMNS[Column.TASK_EARLY_START] = "Early Start";
      TASK_COLUMNS[Column.TASK_EARLY_FINISH] = "Early Finish";
      TASK_COLUMNS[Column.TASK_LATE_START] = "Late Start";
      TASK_COLUMNS[Column.TASK_LATE_FINISH] = "Late Finish";
      TASK_COLUMNS[Column.TASK_ACTUAL_START] = "Actual Start";
      TASK_COLUMNS[Column.TASK_ACTUAL_FINISH] = "Actual Finish";
      TASK_COLUMNS[Column.TASK_BASELINE_START] = "Baseline Start";
      TASK_COLUMNS[Column.TASK_BASELINE_FINISH] = "Baseline Finish";
      TASK_COLUMNS[Column.TASK_START_VARIANCE] = "Start Variance";
      TASK_COLUMNS[Column.TASK_FINISH_VARIANCE] = "Finish Variance";
      TASK_COLUMNS[Column.TASK_PREDECESSORS] = "Predecessors";
      TASK_COLUMNS[Column.TASK_SUCCESSORS] = "Successors";
      TASK_COLUMNS[Column.TASK_RESOURCE_NAMES] = "Resource Names";
      TASK_COLUMNS[Column.TASK_RESOURCE_INITIALS] = "Resource Initials";
      TASK_COLUMNS[Column.TASK_TEXT1] = "Text1";
      TASK_COLUMNS[Column.TASK_START1] = "Start1";
      TASK_COLUMNS[Column.TASK_FINISH1] = "Finish1";
      TASK_COLUMNS[Column.TASK_TEXT2] = "Text2";
      TASK_COLUMNS[Column.TASK_START2] = "Start2";
      TASK_COLUMNS[Column.TASK_FINISH2] = "Finish2";
      TASK_COLUMNS[Column.TASK_TEXT3] = "Text3";
      TASK_COLUMNS[Column.TASK_START3] = "Start3";
      TASK_COLUMNS[Column.TASK_FINISH3] = "Finish3";
      TASK_COLUMNS[Column.TASK_TEXT4] = "Text4";
      TASK_COLUMNS[Column.TASK_START4] = "Start4";
      TASK_COLUMNS[Column.TASK_FINISH4] = "Finish4";
      TASK_COLUMNS[Column.TASK_TEXT5] = "Text5";
      TASK_COLUMNS[Column.TASK_START5] = "Start5";
      TASK_COLUMNS[Column.TASK_FINISH5] = "Finish5";
      TASK_COLUMNS[Column.TASK_TEXT6] = "Text6";
      TASK_COLUMNS[Column.TASK_TEXT7] = "Text7";
      TASK_COLUMNS[Column.TASK_TEXT8] = "Text8";
      TASK_COLUMNS[Column.TASK_TEXT9] = "Text9";
      TASK_COLUMNS[Column.TASK_TEXT10] = "Text10";
      TASK_COLUMNS[Column.TASK_MARKED] = "Marked";
      TASK_COLUMNS[Column.TASK_FLAG1] = "Flag1";
      TASK_COLUMNS[Column.TASK_FLAG2] = "Flag2";
      TASK_COLUMNS[Column.TASK_FLAG3] = "Flag3";
      TASK_COLUMNS[Column.TASK_FLAG4] = "Flag4";
      TASK_COLUMNS[Column.TASK_FLAG5] = "Flag5";
      TASK_COLUMNS[Column.TASK_FLAG6] = "Flag6";
      TASK_COLUMNS[Column.TASK_FLAG7] = "Flag7";
      TASK_COLUMNS[Column.TASK_FLAG8] = "Flag8";
      TASK_COLUMNS[Column.TASK_FLAG9] = "Flag9";
      TASK_COLUMNS[Column.TASK_FLAG10] = "Flag10";
      TASK_COLUMNS[Column.TASK_ROLLUP] = "Rollup";
      TASK_COLUMNS[Column.TASK_CV] = "CV";
      TASK_COLUMNS[Column.TASK_PROJECT] = "Project";
      TASK_COLUMNS[Column.TASK_OUTLINE_LEVEL] = "Outline Level";
      TASK_COLUMNS[Column.TASK_UNIQUE_ID] = "Unique ID";
      TASK_COLUMNS[Column.TASK_NUMBER1] = "Number1";
      TASK_COLUMNS[Column.TASK_NUMBER2] = "Number2";
      TASK_COLUMNS[Column.TASK_NUMBER3] = "Number3";
      TASK_COLUMNS[Column.TASK_NUMBER4] = "Number4";
      TASK_COLUMNS[Column.TASK_NUMBER5] = "Number5";
      TASK_COLUMNS[Column.TASK_SUMMARY] = "Summary";
      TASK_COLUMNS[Column.TASK_CREATED] = "Created";
      TASK_COLUMNS[Column.TASK_NOTES] = "Notes";
      TASK_COLUMNS[Column.TASK_UNIQUE_ID_PREDECESSORS] = "Predecessors";
      TASK_COLUMNS[Column.TASK_UNIQUE_ID_SUCCESSORS] = "Sucessors";
      TASK_COLUMNS[Column.TASK_OBJECTS] = "Objects";
      TASK_COLUMNS[Column.TASK_LINKED_FIELDS] = "Linked Fields";
      TASK_COLUMNS[Column.TASK_RESUME] = "Resume";
      TASK_COLUMNS[Column.TASK_STOP] = "Stop";
      TASK_COLUMNS[Column.TASK_OUTLINE_NUMBER] = "Outline Number";
      TASK_COLUMNS[Column.TASK_DURATION1] = "Duration1";
      TASK_COLUMNS[Column.TASK_DURATION2] = "Duration2";
      TASK_COLUMNS[Column.TASK_DURATION3] = "Duration3";
      TASK_COLUMNS[Column.TASK_COST1] = "Cost1";
      TASK_COLUMNS[Column.TASK_COST2] = "Cost2";
      TASK_COLUMNS[Column.TASK_COST3] = "Cost3";
      TASK_COLUMNS[Column.TASK_HIDEBAR] = "Hide Bar";
      TASK_COLUMNS[Column.TASK_CONFIRMED] = "Confirmed";
      TASK_COLUMNS[Column.TASK_UPDATE_NEEDED] = "Update Needed";
      TASK_COLUMNS[Column.TASK_CONTACT] = "Contact";
      TASK_COLUMNS[Column.TASK_RESOURCE_GROUP] = "Resource Group";
      TASK_COLUMNS[Column.TASK_ACWP] = "ACWP";
      TASK_COLUMNS[Column.TASK_TYPE] = "Type";
      TASK_COLUMNS[Column.TASK_RECURRING] = "Recurring";
      TASK_COLUMNS[Column.TASK_EFFORT_DRIVEN] = "Effort Driven";
      TASK_COLUMNS[Column.TASK_OVERTIME_WORK] = "Overtime Work";
      TASK_COLUMNS[Column.TASK_ACTUAL_OVERTIME_WORK] = "Actual Overtime Work";
      TASK_COLUMNS[Column.TASK_REMAINING_OVERTIME_WORK] = "Remaining Overtime Work";
      TASK_COLUMNS[Column.TASK_REGULAR_WORK] = "Regular Work";
      TASK_COLUMNS[Column.TASK_OVERTIME_COST] = "Overtime Cost";
      TASK_COLUMNS[Column.TASK_ACTUAL_OVERTIME_COST] = "Actual Overtime Cost";
      TASK_COLUMNS[Column.TASK_REMAINING_OVERTIME_COST] = "Remaining Overtime Cost";
      TASK_COLUMNS[Column.TASK_FIXED_COST_ACCRUAL] = "Fixed Cost Accrual";
      TASK_COLUMNS[Column.TASK_INDICATORS] = "Indicators";
      TASK_COLUMNS[Column.TASK_HYPERLINK] = "Hyperlink";
      TASK_COLUMNS[Column.TASK_HYPERLINK_ADDRESS] = "Hyperlink Address";
      TASK_COLUMNS[Column.TASK_HYPERLINK_SUBADDRESS] = "Hyperlink SubAddress";
      TASK_COLUMNS[Column.TASK_HYPERLINK_HREF] = "Hyperlink Href";
      TASK_COLUMNS[Column.TASK_ASSIGNMENT] = "Assignment";
      TASK_COLUMNS[Column.TASK_OVERALLOCATED] = "Overallocated";
      TASK_COLUMNS[Column.TASK_EXTERNAL_TASK] = "External Task";
      TASK_COLUMNS[Column.TASK_SUBPROJECT_READ_ONLY] = "Subproject Read Only";
      TASK_COLUMNS[Column.TASK_RESPONSE_PENDING] = "Response Pending";
      TASK_COLUMNS[Column.TASK_TEAMSTATUS_PENDING] = "TeamStatus Pending";
      TASK_COLUMNS[Column.TASK_LEVELING_CAN_SPLIT] = "Leveling Can Split";
      TASK_COLUMNS[Column.TASK_LEVEL_ASSIGNMENTS] = "Level Assignments";
      TASK_COLUMNS[Column.TASK_WORK_CONTOUR] = "Work Contour";
      TASK_COLUMNS[Column.TASK_COST4] = "Cost4";
      TASK_COLUMNS[Column.TASK_COST5] = "Cost5";
      TASK_COLUMNS[Column.TASK_COST6] = "Cost6";
      TASK_COLUMNS[Column.TASK_COST7] = "Cost7";
      TASK_COLUMNS[Column.TASK_COST8] = "Cost8";
      TASK_COLUMNS[Column.TASK_COST9] = "Cost9";
      TASK_COLUMNS[Column.TASK_COST10] = "Cost10";
      TASK_COLUMNS[Column.TASK_DATE1] = "Date1";
      TASK_COLUMNS[Column.TASK_DATE2] = "Date2";
      TASK_COLUMNS[Column.TASK_DATE3] = "Date3";
      TASK_COLUMNS[Column.TASK_DATE4] = "Date4";
      TASK_COLUMNS[Column.TASK_DATE5] = "Date5";
      TASK_COLUMNS[Column.TASK_DATE6] = "Date6";
      TASK_COLUMNS[Column.TASK_DATE7] = "Date7";
      TASK_COLUMNS[Column.TASK_DATE8] = "Date8";
      TASK_COLUMNS[Column.TASK_DATE9] = "Date9";
      TASK_COLUMNS[Column.TASK_DATE10] = "Date10";
      TASK_COLUMNS[Column.TASK_DURATION4] = "Duration4";
      TASK_COLUMNS[Column.TASK_DURATION5] = "Duration5";
      TASK_COLUMNS[Column.TASK_DURATION6] = "Duration6";
      TASK_COLUMNS[Column.TASK_DURATION7] = "Duration7";
      TASK_COLUMNS[Column.TASK_DURATION8] = "Duration8";
      TASK_COLUMNS[Column.TASK_DURATION9] = "Duration9";
      TASK_COLUMNS[Column.TASK_DURATION10] = "Duration10";
      TASK_COLUMNS[Column.TASK_START6] = "Start6";
      TASK_COLUMNS[Column.TASK_FINISH6] = "Finish6";
      TASK_COLUMNS[Column.TASK_START7] = "Start7";
      TASK_COLUMNS[Column.TASK_FINISH7] = "Finish7";
      TASK_COLUMNS[Column.TASK_START8] = "Start8";
      TASK_COLUMNS[Column.TASK_FINISH8] = "Finish8";
      TASK_COLUMNS[Column.TASK_START9] = "Start9";
      TASK_COLUMNS[Column.TASK_FINISH9] = "Finish9";
      TASK_COLUMNS[Column.TASK_START10] = "Start10";
      TASK_COLUMNS[Column.TASK_FINISH10] = "Finish10";
      TASK_COLUMNS[Column.TASK_FLAG11] = "Flag11";
      TASK_COLUMNS[Column.TASK_FLAG12] = "Flag12";
      TASK_COLUMNS[Column.TASK_FLAG13] = "Flag13";
      TASK_COLUMNS[Column.TASK_FLAG14] = "Flag14";
      TASK_COLUMNS[Column.TASK_FLAG15] = "Flag15";
      TASK_COLUMNS[Column.TASK_FLAG16] = "Flag16";
      TASK_COLUMNS[Column.TASK_FLAG17] = "Flag17";
      TASK_COLUMNS[Column.TASK_FLAG18] = "Flag18";
      TASK_COLUMNS[Column.TASK_FLAG19] = "Flag19";
      TASK_COLUMNS[Column.TASK_FLAG20] = "Flag20";
      TASK_COLUMNS[Column.TASK_NUMBER6] = "Number6";
      TASK_COLUMNS[Column.TASK_NUMBER7] = "Number7";
      TASK_COLUMNS[Column.TASK_NUMBER8] = "Number8";
      TASK_COLUMNS[Column.TASK_NUMBER9] = "Number9";
      TASK_COLUMNS[Column.TASK_NUMBER10] = "Number10";
      TASK_COLUMNS[Column.TASK_NUMBER11] = "Number11";
      TASK_COLUMNS[Column.TASK_NUMBER12] = "Number12";
      TASK_COLUMNS[Column.TASK_NUMBER13] = "Number13";
      TASK_COLUMNS[Column.TASK_NUMBER14] = "Number14";
      TASK_COLUMNS[Column.TASK_NUMBER15] = "Number15";
      TASK_COLUMNS[Column.TASK_NUMBER16] = "Number16";
      TASK_COLUMNS[Column.TASK_NUMBER17] = "Number17";
      TASK_COLUMNS[Column.TASK_NUMBER18] = "Number18";
      TASK_COLUMNS[Column.TASK_NUMBER19] = "Number19";
      TASK_COLUMNS[Column.TASK_NUMBER20] = "Number20";
      TASK_COLUMNS[Column.TASK_TEXT11] = "Text11";
      TASK_COLUMNS[Column.TASK_TEXT12] = "Text12";
      TASK_COLUMNS[Column.TASK_TEXT13] = "Text13";
      TASK_COLUMNS[Column.TASK_TEXT14] = "Text14";
      TASK_COLUMNS[Column.TASK_TEXT15] = "Text15";
      TASK_COLUMNS[Column.TASK_TEXT16] = "Text16";
      TASK_COLUMNS[Column.TASK_TEXT17] = "Text17";
      TASK_COLUMNS[Column.TASK_TEXT18] = "Text18";
      TASK_COLUMNS[Column.TASK_TEXT19] = "Text19";
      TASK_COLUMNS[Column.TASK_TEXT20] = "Text20";
      TASK_COLUMNS[Column.TASK_TEXT21] = "Text21";
      TASK_COLUMNS[Column.TASK_TEXT22] = "Text22";
      TASK_COLUMNS[Column.TASK_TEXT23] = "Text23";
      TASK_COLUMNS[Column.TASK_TEXT24] = "Text24";
      TASK_COLUMNS[Column.TASK_TEXT25] = "Text25";
      TASK_COLUMNS[Column.TASK_TEXT26] = "Text26";
      TASK_COLUMNS[Column.TASK_TEXT27] = "Text27";
      TASK_COLUMNS[Column.TASK_TEXT28] = "Text28";
      TASK_COLUMNS[Column.TASK_TEXT29] = "Text29";
      TASK_COLUMNS[Column.TASK_TEXT30] = "Text30";
      TASK_COLUMNS[Column.TASK_RESOURCE_PHONETICS] = "Phonetics";
      TASK_COLUMNS[Column.TASK_ASSIGNMENT_DELAY] = "Assignment Delay";
      TASK_COLUMNS[Column.TASK_ASSIGNMENT_UNITS] = "Assignment Units";
      TASK_COLUMNS[Column.TASK_COST_RATE_TABLE] = "Cost Rate Table";
      TASK_COLUMNS[Column.TASK_PRELEVELED_START] = "Preleveled Start";
      TASK_COLUMNS[Column.TASK_PRELEVELED_FINISH] = "Preleveled Finish";
      TASK_COLUMNS[Column.TASK_ESTIMATED] = "Estimated";
      TASK_COLUMNS[Column.TASK_IGNORE_RESOURCE_CALENDAR] = "Ignore Resource Calendar";
      TASK_COLUMNS[Column.TASK_TASK_CALENDAR] = "Task Calendar";
      TASK_COLUMNS[Column.TASK_OUTLINE_CODE1] = "Outline Code1";
      TASK_COLUMNS[Column.TASK_OUTLINE_CODE2] = "Outline Code2";
      TASK_COLUMNS[Column.TASK_OUTLINE_CODE3] = "Outline Code3";
      TASK_COLUMNS[Column.TASK_OUTLINE_CODE4] = "Outline Code4";
      TASK_COLUMNS[Column.TASK_OUTLINE_CODE5] = "Outline Code5";
      TASK_COLUMNS[Column.TASK_OUTLINE_CODE6] = "Outline Code6";
      TASK_COLUMNS[Column.TASK_OUTLINE_CODE7] = "Outline Code7";
      TASK_COLUMNS[Column.TASK_OUTLINE_CODE8] = "Outline Code8";
      TASK_COLUMNS[Column.TASK_OUTLINE_CODE9] = "Outline Code9";
      TASK_COLUMNS[Column.TASK_OUTLINE_CODE10] = "Outline Code10";
      TASK_COLUMNS[Column.TASK_DEADLINE] = "Deadline";
      TASK_COLUMNS[Column.TASK_START_SLACK] = "Start Slack";
      TASK_COLUMNS[Column.TASK_FINISH_SLACK] = "Finish Slack";
      TASK_COLUMNS[Column.TASK_VAC] = "VAC";
      TASK_COLUMNS[Column.TASK_GROUP_BY_SUMMARY] = "Group By Summary";
      TASK_COLUMNS[Column.TASK_WBS_PREDECESSORS] = "Predecesors";
      TASK_COLUMNS[Column.TASK_WBS_SUCCESSORS] = "Successors";
      TASK_COLUMNS[Column.TASK_RESOURCE_TYPE] = "Resource Type";
      
      RESOURCE_COLUMNS[Column.RESOURCE_ID] = "ID";
      RESOURCE_COLUMNS[Column.RESOURCE_NAME] = "Name";
      RESOURCE_COLUMNS[Column.RESOURCE_INITIALS] = "Initials";
      RESOURCE_COLUMNS[Column.RESOURCE_GROUP] = "Group";
      RESOURCE_COLUMNS[Column.RESOURCE_MAX_UNITS] = "Max Units";
      RESOURCE_COLUMNS[Column.RESOURCE_BASE_CALENDAR] = "Base Calendar";
      RESOURCE_COLUMNS[Column.RESOURCE_STANDARD_RATE] = "Standard Rate";
      RESOURCE_COLUMNS[Column.RESOURCE_OVERTIME_RATE] = "Overtime Rate";
      RESOURCE_COLUMNS[Column.RESOURCE_TEXT1] = "Text1";
      RESOURCE_COLUMNS[Column.RESOURCE_TEXT2] = "Text2";
      RESOURCE_COLUMNS[Column.RESOURCE_CODE] = "Code";
      RESOURCE_COLUMNS[Column.RESOURCE_ACTUAL_COST] = "Actual Cost";
      RESOURCE_COLUMNS[Column.RESOURCE_COST] = "Cost";
      RESOURCE_COLUMNS[Column.RESOURCE_WORK] = "Work";
      RESOURCE_COLUMNS[Column.RESOURCE_ACTUAL_WORK] = "Actual Work";
      RESOURCE_COLUMNS[Column.RESOURCE_BASELINE_WORK] = "Baseline Work";
      RESOURCE_COLUMNS[Column.RESOURCE_OVERTIME_WORK] = "Overtime Work";
      RESOURCE_COLUMNS[Column.RESOURCE_BASELINE_COST] = "Baseline Cost";
      RESOURCE_COLUMNS[Column.RESOURCE_COST_PER_USE] = "Cost Per Use";
      RESOURCE_COLUMNS[Column.RESOURCE_ACCRUE_AT] = "Accrue At";
      RESOURCE_COLUMNS[Column.RESOURCE_REMAINING_COST] = "Remaining Cost";
      RESOURCE_COLUMNS[Column.RESOURCE_REMAINING_WORK] = "Remaining Work";
      RESOURCE_COLUMNS[Column.RESOURCE_WORK_VARIANCE] = "Work Variance";
      RESOURCE_COLUMNS[Column.RESOURCE_COST_VARIANCE] = "Cost Variance";
      RESOURCE_COLUMNS[Column.RESOURCE_OVERALLOCATED] = "Overallocated";
      RESOURCE_COLUMNS[Column.RESOURCE_PEAK] = "Peak";
      RESOURCE_COLUMNS[Column.RESOURCE_UNIQUE_ID] = "Unique ID";
      RESOURCE_COLUMNS[Column.RESOURCE_NOTES] = "Notes";
      RESOURCE_COLUMNS[Column.RESOURCE_PERCENT_WORK_COMPLETE] = "% Work Complete";
      RESOURCE_COLUMNS[Column.RESOURCE_TEXT3] = "Text3";
      RESOURCE_COLUMNS[Column.RESOURCE_TEXT4] = "Text4";
      RESOURCE_COLUMNS[Column.RESOURCE_TEXT5] = "Text5";
      RESOURCE_COLUMNS[Column.RESOURCE_OBJECTS] = "Object";
      RESOURCE_COLUMNS[Column.RESOURCE_LINKED_FIELDS] = "Linked Fields";
      RESOURCE_COLUMNS[Column.RESOURCE_EMAIL_ADDRESS] = "Email Address";
      RESOURCE_COLUMNS[Column.RESOURCE_REGULAR_WORK] = "Regular Work";
      RESOURCE_COLUMNS[Column.RESOURCE_ACTUAL_OVERTIME_WORK] = "Actual Overtime Work";
      RESOURCE_COLUMNS[Column.RESOURCE_REMAINING_OVERTIME_WORK] = "Remaining Overtime Work";
      RESOURCE_COLUMNS[Column.RESOURCE_OVERTIME_COST] = "Overtime Cost";
      RESOURCE_COLUMNS[Column.RESOURCE_ACTUAL_OVERTIME_COST] = "Actual Overtime Cost";
      RESOURCE_COLUMNS[Column.RESOURCE_REMAINING_OVERTIME_COST] = "Remaining Overtime Cost";
      RESOURCE_COLUMNS[Column.RESOURCE_BCWS] = "BCWS";
      RESOURCE_COLUMNS[Column.RESOURCE_BCWP] = "BCWP";
      RESOURCE_COLUMNS[Column.RESOURCE_ACWP] = "ACWP";
      RESOURCE_COLUMNS[Column.RESOURCE_SV] = "SV";
      RESOURCE_COLUMNS[Column.RESOURCE_AVAILABLE_FROM] = "Available From";
      RESOURCE_COLUMNS[Column.RESOURCE_AVAILABLE_TO] = "Available To";
      RESOURCE_COLUMNS[Column.RESOURCE_INDICATORS] = "Indicators";
      RESOURCE_COLUMNS[Column.RESOURCE_TEXT6] = "Text6";
      RESOURCE_COLUMNS[Column.RESOURCE_TEXT7] = "Text7";
      RESOURCE_COLUMNS[Column.RESOURCE_TEXT8] = "Text8";
      RESOURCE_COLUMNS[Column.RESOURCE_TEXT9] = "Text9";
      RESOURCE_COLUMNS[Column.RESOURCE_TEXT10] = "Text10";
      RESOURCE_COLUMNS[Column.RESOURCE_START1] = "Start1";
      RESOURCE_COLUMNS[Column.RESOURCE_START2] = "Start2";
      RESOURCE_COLUMNS[Column.RESOURCE_START3] = "Start3";
      RESOURCE_COLUMNS[Column.RESOURCE_START4] = "Start4";
      RESOURCE_COLUMNS[Column.RESOURCE_START5] = "Start5";
      RESOURCE_COLUMNS[Column.RESOURCE_FINISH1] = "Finish1";
      RESOURCE_COLUMNS[Column.RESOURCE_FINISH2] = "Finish2";
      RESOURCE_COLUMNS[Column.RESOURCE_FINISH3] = "Finish3";
      RESOURCE_COLUMNS[Column.RESOURCE_FINISH4] = "Finish4";
      RESOURCE_COLUMNS[Column.RESOURCE_FINISH5] = "Finish5";
      RESOURCE_COLUMNS[Column.RESOURCE_NUMBER1] = "Number1";
      RESOURCE_COLUMNS[Column.RESOURCE_NUMBER2] = "Number2";
      RESOURCE_COLUMNS[Column.RESOURCE_NUMBER3] = "Number3";
      RESOURCE_COLUMNS[Column.RESOURCE_NUMBER4] = "Number4";
      RESOURCE_COLUMNS[Column.RESOURCE_NUMBER5] = "Number5";
      RESOURCE_COLUMNS[Column.RESOURCE_DURATION1] = "Duration1";
      RESOURCE_COLUMNS[Column.RESOURCE_DURATION2] = "Duration2";
      RESOURCE_COLUMNS[Column.RESOURCE_DURATION3] = "Duration3";
      RESOURCE_COLUMNS[Column.RESOURCE_COST1] = "Cost1";
      RESOURCE_COLUMNS[Column.RESOURCE_COST2] = "Cost2";
      RESOURCE_COLUMNS[Column.RESOURCE_COST3] = "Cost3";
      RESOURCE_COLUMNS[Column.RESOURCE_FLAG10] = "Flag10";
      RESOURCE_COLUMNS[Column.RESOURCE_FLAG1] = "Flag1";
      RESOURCE_COLUMNS[Column.RESOURCE_FLAG2] = "Flag2";
      RESOURCE_COLUMNS[Column.RESOURCE_FLAG3] = "Flag3";
      RESOURCE_COLUMNS[Column.RESOURCE_FLAG4] = "Flag4";
      RESOURCE_COLUMNS[Column.RESOURCE_FLAG5] = "Flag5";
      RESOURCE_COLUMNS[Column.RESOURCE_FLAG6] = "Flag6";
      RESOURCE_COLUMNS[Column.RESOURCE_FLAG7] = "Flag7";
      RESOURCE_COLUMNS[Column.RESOURCE_FLAG8] = "Flag8";
      RESOURCE_COLUMNS[Column.RESOURCE_FLAG9] = "Flag9";
      RESOURCE_COLUMNS[Column.RESOURCE_HYPERLINK] = "Hyperlink";
      RESOURCE_COLUMNS[Column.RESOURCE_HYPERLINK_ADDRESS] = "Hyperlink Address";
      RESOURCE_COLUMNS[Column.RESOURCE_HYPERLINK_SUBADDRESS] = "Hyperlink SubAddress";
      RESOURCE_COLUMNS[Column.RESOURCE_HYPERLINK_HREF] = "Hyperlink Href";
      RESOURCE_COLUMNS[Column.RESOURCE_ASSIGNMENT] = "Assignment";
      RESOURCE_COLUMNS[Column.RESOURCE_TASK_SUMMARY_NAME] = "Task Summary Name";
      RESOURCE_COLUMNS[Column.RESOURCE_CAN_LEVEL] = "Can Level";
      RESOURCE_COLUMNS[Column.RESOURCE_WORK_CONTOUR] = "Work Contour";
      RESOURCE_COLUMNS[Column.RESOURCE_COST4] = "Cost4";
      RESOURCE_COLUMNS[Column.RESOURCE_COST5] = "Cost5";
      RESOURCE_COLUMNS[Column.RESOURCE_COST6] = "Cost6";
      RESOURCE_COLUMNS[Column.RESOURCE_COST7] = "Cost7";
      RESOURCE_COLUMNS[Column.RESOURCE_COST8] = "Cost8";
      RESOURCE_COLUMNS[Column.RESOURCE_COST9] = "Cost9";
      RESOURCE_COLUMNS[Column.RESOURCE_COST10] = "Cost10";
      RESOURCE_COLUMNS[Column.RESOURCE_DATE1] = "Date1";
      RESOURCE_COLUMNS[Column.RESOURCE_DATE2] = "Date2";
      RESOURCE_COLUMNS[Column.RESOURCE_DATE3] = "Date3";
      RESOURCE_COLUMNS[Column.RESOURCE_DATE4] = "Date4";
      RESOURCE_COLUMNS[Column.RESOURCE_DATE5] = "Date5";
      RESOURCE_COLUMNS[Column.RESOURCE_DATE6] = "Date6";
      RESOURCE_COLUMNS[Column.RESOURCE_DATE7] = "Date7";
      RESOURCE_COLUMNS[Column.RESOURCE_DATE8] = "Date8";
      RESOURCE_COLUMNS[Column.RESOURCE_DATE9] = "Date9";
      RESOURCE_COLUMNS[Column.RESOURCE_DATE10] = "Date10";
      RESOURCE_COLUMNS[Column.RESOURCE_DURATION4] = "Duration4";
      RESOURCE_COLUMNS[Column.RESOURCE_DURATION5] = "Duration5";
      RESOURCE_COLUMNS[Column.RESOURCE_DURATION6] = "Duration6";
      RESOURCE_COLUMNS[Column.RESOURCE_DURATION7] = "Duration7";
      RESOURCE_COLUMNS[Column.RESOURCE_DURATION8] = "Duration8";
      RESOURCE_COLUMNS[Column.RESOURCE_DURATION9] = "Duration9";
      RESOURCE_COLUMNS[Column.RESOURCE_DURATION10] = "Duration10";
      RESOURCE_COLUMNS[Column.RESOURCE_FINISH6] = "Finish6";
      RESOURCE_COLUMNS[Column.RESOURCE_FINISH7] = "Finish7";
      RESOURCE_COLUMNS[Column.RESOURCE_FINISH8] = "Finish8";
      RESOURCE_COLUMNS[Column.RESOURCE_FINISH9] = "Finish9";
      RESOURCE_COLUMNS[Column.RESOURCE_FINISH10] = "Finish10";
      RESOURCE_COLUMNS[Column.RESOURCE_FLAG11] = "Flag11";
      RESOURCE_COLUMNS[Column.RESOURCE_FLAG12] = "Flag12";
      RESOURCE_COLUMNS[Column.RESOURCE_FLAG13] = "Flag13";
      RESOURCE_COLUMNS[Column.RESOURCE_FLAG14] = "Flag14";
      RESOURCE_COLUMNS[Column.RESOURCE_FLAG15] = "Flag15";
      RESOURCE_COLUMNS[Column.RESOURCE_FLAG16] = "Flag16";
      RESOURCE_COLUMNS[Column.RESOURCE_FLAG17] = "Flag17";
      RESOURCE_COLUMNS[Column.RESOURCE_FLAG18] = "Flag18";
      RESOURCE_COLUMNS[Column.RESOURCE_FLAG19] = "Flag19";
      RESOURCE_COLUMNS[Column.RESOURCE_FLAG20] = "Flag20";
      RESOURCE_COLUMNS[Column.RESOURCE_NUMBER6] = "Number6";
      RESOURCE_COLUMNS[Column.RESOURCE_NUMBER7] = "Number7";
      RESOURCE_COLUMNS[Column.RESOURCE_NUMBER8] = "Number8";
      RESOURCE_COLUMNS[Column.RESOURCE_NUMBER9] = "Number9";
      RESOURCE_COLUMNS[Column.RESOURCE_NUMBER10] = "Number10";
      RESOURCE_COLUMNS[Column.RESOURCE_NUMBER11] = "Number11";
      RESOURCE_COLUMNS[Column.RESOURCE_NUMBER12] = "Number12";
      RESOURCE_COLUMNS[Column.RESOURCE_NUMBER13] = "Number13";
      RESOURCE_COLUMNS[Column.RESOURCE_NUMBER14] = "Number14";
      RESOURCE_COLUMNS[Column.RESOURCE_NUMBER15] = "Number15";
      RESOURCE_COLUMNS[Column.RESOURCE_NUMBER16] = "Number16";
      RESOURCE_COLUMNS[Column.RESOURCE_NUMBER17] = "Number17";
      RESOURCE_COLUMNS[Column.RESOURCE_NUMBER18] = "Number18";
      RESOURCE_COLUMNS[Column.RESOURCE_NUMBER19] = "Number19";
      RESOURCE_COLUMNS[Column.RESOURCE_NUMBER20] = "Number20";
      RESOURCE_COLUMNS[Column.RESOURCE_START6] = "Start6";
      RESOURCE_COLUMNS[Column.RESOURCE_START7] = "Start7";
      RESOURCE_COLUMNS[Column.RESOURCE_START8] = "Start8";
      RESOURCE_COLUMNS[Column.RESOURCE_START9] = "Start9";
      RESOURCE_COLUMNS[Column.RESOURCE_START10] = "Start10";
      RESOURCE_COLUMNS[Column.RESOURCE_TEXT11] = "Text11";
      RESOURCE_COLUMNS[Column.RESOURCE_TEXT12] = "Text12";
      RESOURCE_COLUMNS[Column.RESOURCE_TEXT13] = "Text13";
      RESOURCE_COLUMNS[Column.RESOURCE_TEXT14] = "Text14";
      RESOURCE_COLUMNS[Column.RESOURCE_TEXT15] = "Text15";
      RESOURCE_COLUMNS[Column.RESOURCE_TEXT16] = "Text16";
      RESOURCE_COLUMNS[Column.RESOURCE_TEXT17] = "Text17";
      RESOURCE_COLUMNS[Column.RESOURCE_TEXT18] = "Text18";
      RESOURCE_COLUMNS[Column.RESOURCE_TEXT19] = "Text19";
      RESOURCE_COLUMNS[Column.RESOURCE_TEXT20] = "Text20";
      RESOURCE_COLUMNS[Column.RESOURCE_TEXT21] = "Text21";
      RESOURCE_COLUMNS[Column.RESOURCE_TEXT22] = "Text22";
      RESOURCE_COLUMNS[Column.RESOURCE_TEXT23] = "Text23";
      RESOURCE_COLUMNS[Column.RESOURCE_TEXT24] = "Text24";
      RESOURCE_COLUMNS[Column.RESOURCE_TEXT25] = "Text25";
      RESOURCE_COLUMNS[Column.RESOURCE_TEXT26] = "Text26";
      RESOURCE_COLUMNS[Column.RESOURCE_TEXT27] = "Text27";
      RESOURCE_COLUMNS[Column.RESOURCE_TEXT28] = "Text28";
      RESOURCE_COLUMNS[Column.RESOURCE_TEXT29] = "Text29";
      RESOURCE_COLUMNS[Column.RESOURCE_TEXT30] = "Text30";
      RESOURCE_COLUMNS[Column.RESOURCE_PHONETICS] = "Phonetics";
      RESOURCE_COLUMNS[Column.RESOURCE_ASSIGNMENT_DELAY] = "Assignment Delay";
      RESOURCE_COLUMNS[Column.RESOURCE_ASSIGNMENT_UNITS] = "Assignment Units";
      RESOURCE_COLUMNS[Column.RESOURCE_BASELINE_START] = "Baseline Start";
      RESOURCE_COLUMNS[Column.RESOURCE_BASELINE_FINISH] = "Baseline Finish";
      RESOURCE_COLUMNS[Column.RESOURCE_CONFIRMED] = "Confirmed";
      RESOURCE_COLUMNS[Column.RESOURCE_FINISH] = "Finish";
      RESOURCE_COLUMNS[Column.RESOURCE_LEVELING_DELAY] = "Leveling Delay";
      RESOURCE_COLUMNS[Column.RESOURCE_RESPONSE_PENDING] = "Response Pending";
      RESOURCE_COLUMNS[Column.RESOURCE_START] = "Start";
      RESOURCE_COLUMNS[Column.RESOURCE_TEAMSTATUS_PENDING] = "TeamStatus Pending";
      RESOURCE_COLUMNS[Column.RESOURCE_CV] = "CV";
      RESOURCE_COLUMNS[Column.RESOURCE_UPDATE_NEEDED] = "Update Needed";
      RESOURCE_COLUMNS[Column.RESOURCE_COST_RATE_TABLE] = "Cost Rate Table";
      RESOURCE_COLUMNS[Column.RESOURCE_ACTUAL_START] = "Actual Start";
      RESOURCE_COLUMNS[Column.RESOURCE_ACTUAL_FINISH] = "Actual Finish";
      RESOURCE_COLUMNS[Column.RESOURCE_WORKGROUP] = "Workgroup";
      RESOURCE_COLUMNS[Column.RESOURCE_PROJECT] = "Project";
      RESOURCE_COLUMNS[Column.RESOURCE_OUTLINE_CODE1] = "Outline Code";
      RESOURCE_COLUMNS[Column.RESOURCE_OUTLINE_CODE2] = "Outline Code2";
      RESOURCE_COLUMNS[Column.RESOURCE_OUTLINE_CODE3] = "Outline Code3";
      RESOURCE_COLUMNS[Column.RESOURCE_OUTLINE_CODE4] = "Outline Code4";
      RESOURCE_COLUMNS[Column.RESOURCE_OUTLINE_CODE5] = "Outline Code5";
      RESOURCE_COLUMNS[Column.RESOURCE_OUTLINE_CODE6] = "Outline Code6";
      RESOURCE_COLUMNS[Column.RESOURCE_OUTLINE_CODE7] = "Outline Code7";
      RESOURCE_COLUMNS[Column.RESOURCE_OUTLINE_CODE8] = "Outline Code8";
      RESOURCE_COLUMNS[Column.RESOURCE_OUTLINE_CODE9] = "Outline Code9";
      RESOURCE_COLUMNS[Column.RESOURCE_OUTLINE_CODE10] = "Outline Code10";
      RESOURCE_COLUMNS[Column.RESOURCE_MATERIAL_LABEL] = "Material Label";
      RESOURCE_COLUMNS[Column.RESOURCE_TYPE] = "Type";
      RESOURCE_COLUMNS[Column.RESOURCE_VAC] = "VAC";
      RESOURCE_COLUMNS[Column.RESOURCE_GROUP_BY_SUMMARY] = "Group By Summary";
      RESOURCE_COLUMNS[Column.RESOURCE_WINDOWS_USER_ACCOUNT] = "Windows User Account";      
   }
      
   private static final Object RESOURCES [][] =
   {
      {"TASK_COLUMNS", TASK_COLUMNS},
      {"RESOURCE_COLUMNS", RESOURCE_COLUMNS}
   };
}
