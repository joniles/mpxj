/*
 * file:       LocaleData.java
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

package net.sf.mpxj;

import java.util.ListResourceBundle;
import java.util.Locale;
import java.util.ResourceBundle;


/**
 * Locale data for MPXJ.
 */
public class LocaleData extends ListResourceBundle
{
   /**
    * {@inheritDoc}
    */
   public Object[][] getContents()
   {
      return (RESOURCES);
   }

   /**
    * Convenience method for retrieving a String[] resource.
    *
    * @param locale locale identifier
    * @param key resource key
    * @return resource value
    */
   public static final String[] getStringArray (Locale locale, String key)
   {
      ResourceBundle bundle = ResourceBundle.getBundle(LocaleData.class.getName(), locale);
      return (bundle.getStringArray(key));
   }

   public static final String TASK_COLUMNS = "TASK_COLUMNS";
   public static final String RESOURCE_COLUMNS = "RESOURCE_COLUMNS";

   private static final String[] RESOURCE_COLUMNS_ARRAY = new String[ResourceField.MAX_VALUE];
   private static final String[] TASK_COLUMNS_ARRAY = new String[TaskField.MAX_VALUE];

   static
   {
      TASK_COLUMNS_ARRAY[TaskField.WORK_VALUE] = "Work";
      TASK_COLUMNS_ARRAY[TaskField.BASELINE_WORK_VALUE] = "Baseline Work";
      TASK_COLUMNS_ARRAY[TaskField.ACTUAL_WORK_VALUE] = "Actual Work";
      TASK_COLUMNS_ARRAY[TaskField.WORK_VARIANCE_VALUE] = "Work Variance";
      TASK_COLUMNS_ARRAY[TaskField.REMAINING_WORK_VALUE] = "Remaining Work";
      TASK_COLUMNS_ARRAY[TaskField.COST_VALUE] = "Cost";
      TASK_COLUMNS_ARRAY[TaskField.BASELINE_COST_VALUE] = "Baseline Cost";
      TASK_COLUMNS_ARRAY[TaskField.ACTUAL_COST_VALUE] = "Actual Cost";
      TASK_COLUMNS_ARRAY[TaskField.FIXED_COST_VALUE] = "Fixed Cost";
      TASK_COLUMNS_ARRAY[TaskField.COST_VARIANCE_VALUE] = "Cost Variance";
      TASK_COLUMNS_ARRAY[TaskField.REMAINING_COST_VALUE] = "Remaining Cost";
      TASK_COLUMNS_ARRAY[TaskField.BCWP_VALUE] = "BCWP";
      TASK_COLUMNS_ARRAY[TaskField.BCWS_VALUE] = "BCWS";
      TASK_COLUMNS_ARRAY[TaskField.SV_VALUE] = "SV";
      TASK_COLUMNS_ARRAY[TaskField.NAME_VALUE] = "Task Name";
      TASK_COLUMNS_ARRAY[TaskField.WBS_VALUE] = "WBS";
      TASK_COLUMNS_ARRAY[TaskField.CONSTRAINT_TYPE_VALUE] = "Constraint Type";
      TASK_COLUMNS_ARRAY[TaskField.CONSTRAINT_DATE_VALUE] = "Constraint Date";
      TASK_COLUMNS_ARRAY[TaskField.CRITICAL_VALUE] = "Critical";
      TASK_COLUMNS_ARRAY[TaskField.LEVELING_DELAY_VALUE] = "Leveling Delay";
      TASK_COLUMNS_ARRAY[TaskField.FREE_SLACK_VALUE] = "Free Slack";
      TASK_COLUMNS_ARRAY[TaskField.TOTAL_SLACK_VALUE] = "Total Slack";
      TASK_COLUMNS_ARRAY[TaskField.ID_VALUE] = "ID";
      TASK_COLUMNS_ARRAY[TaskField.MILESTONE_VALUE] = "Milestone";
      TASK_COLUMNS_ARRAY[TaskField.PRIORITY_VALUE] = "Priority";
      TASK_COLUMNS_ARRAY[TaskField.SUBPROJECT_FILE_VALUE] = "Subproject File";
      TASK_COLUMNS_ARRAY[TaskField.BASELINE_DURATION_VALUE] = "Baseline Duration";
      TASK_COLUMNS_ARRAY[TaskField.ACTUAL_DURATION_VALUE] = "Actual Duration";
      TASK_COLUMNS_ARRAY[TaskField.DURATION_VALUE] = "Duration";
      TASK_COLUMNS_ARRAY[TaskField.DURATION_VARIANCE_VALUE] = "Duration Variance";
      TASK_COLUMNS_ARRAY[TaskField.REMAINING_DURATION_VALUE] = "Remaining Duration";
      TASK_COLUMNS_ARRAY[TaskField.PERCENT_COMPLETE_VALUE] = "% Complete";
      TASK_COLUMNS_ARRAY[TaskField.PERCENT_WORK_COMPLETE_VALUE] = "% Work Complete";
      TASK_COLUMNS_ARRAY[TaskField.START_VALUE] = "Start";
      TASK_COLUMNS_ARRAY[TaskField.FINISH_VALUE] = "Finish";
      TASK_COLUMNS_ARRAY[TaskField.EARLY_START_VALUE] = "Early Start";
      TASK_COLUMNS_ARRAY[TaskField.EARLY_FINISH_VALUE] = "Early Finish";
      TASK_COLUMNS_ARRAY[TaskField.LATE_START_VALUE] = "Late Start";
      TASK_COLUMNS_ARRAY[TaskField.LATE_FINISH_VALUE] = "Late Finish";
      TASK_COLUMNS_ARRAY[TaskField.ACTUAL_START_VALUE] = "Actual Start";
      TASK_COLUMNS_ARRAY[TaskField.ACTUAL_FINISH_VALUE] = "Actual Finish";
      TASK_COLUMNS_ARRAY[TaskField.BASELINE_START_VALUE] = "Baseline Start";
      TASK_COLUMNS_ARRAY[TaskField.BASELINE_FINISH_VALUE] = "Baseline Finish";
      TASK_COLUMNS_ARRAY[TaskField.START_VARIANCE_VALUE] = "Start Variance";
      TASK_COLUMNS_ARRAY[TaskField.FINISH_VARIANCE_VALUE] = "Finish Variance";
      TASK_COLUMNS_ARRAY[TaskField.PREDECESSORS_VALUE] = "Predecessors";
      TASK_COLUMNS_ARRAY[TaskField.SUCCESSORS_VALUE] = "Successors";
      TASK_COLUMNS_ARRAY[TaskField.RESOURCE_NAMES_VALUE] = "Resource Names";
      TASK_COLUMNS_ARRAY[TaskField.RESOURCE_INITIALS_VALUE] = "Resource Initials";
      TASK_COLUMNS_ARRAY[TaskField.TEXT1_VALUE] = "Text1";
      TASK_COLUMNS_ARRAY[TaskField.START1_VALUE] = "Start1";
      TASK_COLUMNS_ARRAY[TaskField.FINISH1_VALUE] = "Finish1";
      TASK_COLUMNS_ARRAY[TaskField.TEXT2_VALUE] = "Text2";
      TASK_COLUMNS_ARRAY[TaskField.START2_VALUE] = "Start2";
      TASK_COLUMNS_ARRAY[TaskField.FINISH2_VALUE] = "Finish2";
      TASK_COLUMNS_ARRAY[TaskField.TEXT3_VALUE] = "Text3";
      TASK_COLUMNS_ARRAY[TaskField.START3_VALUE] = "Start3";
      TASK_COLUMNS_ARRAY[TaskField.FINISH3_VALUE] = "Finish3";
      TASK_COLUMNS_ARRAY[TaskField.TEXT4_VALUE] = "Text4";
      TASK_COLUMNS_ARRAY[TaskField.START4_VALUE] = "Start4";
      TASK_COLUMNS_ARRAY[TaskField.FINISH4_VALUE] = "Finish4";
      TASK_COLUMNS_ARRAY[TaskField.TEXT5_VALUE] = "Text5";
      TASK_COLUMNS_ARRAY[TaskField.START5_VALUE] = "Start5";
      TASK_COLUMNS_ARRAY[TaskField.FINISH5_VALUE] = "Finish5";
      TASK_COLUMNS_ARRAY[TaskField.TEXT6_VALUE] = "Text6";
      TASK_COLUMNS_ARRAY[TaskField.TEXT7_VALUE] = "Text7";
      TASK_COLUMNS_ARRAY[TaskField.TEXT8_VALUE] = "Text8";
      TASK_COLUMNS_ARRAY[TaskField.TEXT9_VALUE] = "Text9";
      TASK_COLUMNS_ARRAY[TaskField.TEXT10_VALUE] = "Text10";
      TASK_COLUMNS_ARRAY[TaskField.MARKED_VALUE] = "Marked";
      TASK_COLUMNS_ARRAY[TaskField.FLAG1_VALUE] = "Flag1";
      TASK_COLUMNS_ARRAY[TaskField.FLAG2_VALUE] = "Flag2";
      TASK_COLUMNS_ARRAY[TaskField.FLAG3_VALUE] = "Flag3";
      TASK_COLUMNS_ARRAY[TaskField.FLAG4_VALUE] = "Flag4";
      TASK_COLUMNS_ARRAY[TaskField.FLAG5_VALUE] = "Flag5";
      TASK_COLUMNS_ARRAY[TaskField.FLAG6_VALUE] = "Flag6";
      TASK_COLUMNS_ARRAY[TaskField.FLAG7_VALUE] = "Flag7";
      TASK_COLUMNS_ARRAY[TaskField.FLAG8_VALUE] = "Flag8";
      TASK_COLUMNS_ARRAY[TaskField.FLAG9_VALUE] = "Flag9";
      TASK_COLUMNS_ARRAY[TaskField.FLAG10_VALUE] = "Flag10";
      TASK_COLUMNS_ARRAY[TaskField.ROLLUP_VALUE] = "Rollup";
      TASK_COLUMNS_ARRAY[TaskField.CV_VALUE] = "CV";
      TASK_COLUMNS_ARRAY[TaskField.PROJECT_VALUE] = "Project";
      TASK_COLUMNS_ARRAY[TaskField.OUTLINE_LEVEL_VALUE] = "Outline Level";
      TASK_COLUMNS_ARRAY[TaskField.UNIQUE_ID_VALUE] = "Unique ID";
      TASK_COLUMNS_ARRAY[TaskField.NUMBER1_VALUE] = "Number1";
      TASK_COLUMNS_ARRAY[TaskField.NUMBER2_VALUE] = "Number2";
      TASK_COLUMNS_ARRAY[TaskField.NUMBER3_VALUE] = "Number3";
      TASK_COLUMNS_ARRAY[TaskField.NUMBER4_VALUE] = "Number4";
      TASK_COLUMNS_ARRAY[TaskField.NUMBER5_VALUE] = "Number5";
      TASK_COLUMNS_ARRAY[TaskField.SUMMARY_VALUE] = "Summary";
      TASK_COLUMNS_ARRAY[TaskField.CREATED_VALUE] = "Created";
      TASK_COLUMNS_ARRAY[TaskField.NOTES_VALUE] = "Notes";
      TASK_COLUMNS_ARRAY[TaskField.UNIQUE_ID_PREDECESSORS_VALUE] = "Predecessors";
      TASK_COLUMNS_ARRAY[TaskField.UNIQUE_ID_SUCCESSORS_VALUE] = "Sucessors";
      TASK_COLUMNS_ARRAY[TaskField.OBJECTS_VALUE] = "Objects";
      TASK_COLUMNS_ARRAY[TaskField.LINKED_FIELDS_VALUE] = "Linked Fields";
      TASK_COLUMNS_ARRAY[TaskField.RESUME_VALUE] = "Resume";
      TASK_COLUMNS_ARRAY[TaskField.STOP_VALUE] = "Stop";
      TASK_COLUMNS_ARRAY[TaskField.OUTLINE_NUMBER_VALUE] = "Outline Number";
      TASK_COLUMNS_ARRAY[TaskField.DURATION1_VALUE] = "Duration1";
      TASK_COLUMNS_ARRAY[TaskField.DURATION2_VALUE] = "Duration2";
      TASK_COLUMNS_ARRAY[TaskField.DURATION3_VALUE] = "Duration3";
      TASK_COLUMNS_ARRAY[TaskField.COST1_VALUE] = "Cost1";
      TASK_COLUMNS_ARRAY[TaskField.COST2_VALUE] = "Cost2";
      TASK_COLUMNS_ARRAY[TaskField.COST3_VALUE] = "Cost3";
      TASK_COLUMNS_ARRAY[TaskField.HIDEBAR_VALUE] = "Hide Bar";
      TASK_COLUMNS_ARRAY[TaskField.CONFIRMED_VALUE] = "Confirmed";
      TASK_COLUMNS_ARRAY[TaskField.UPDATE_NEEDED_VALUE] = "Update Needed";
      TASK_COLUMNS_ARRAY[TaskField.CONTACT_VALUE] = "Contact";
      TASK_COLUMNS_ARRAY[TaskField.RESOURCE_GROUP_VALUE] = "Resource Group";
      TASK_COLUMNS_ARRAY[TaskField.ACWP_VALUE] = "ACWP";
      TASK_COLUMNS_ARRAY[TaskField.TYPE_VALUE] = "Type";
      TASK_COLUMNS_ARRAY[TaskField.RECURRING_VALUE] = "Recurring";
      TASK_COLUMNS_ARRAY[TaskField.EFFORT_DRIVEN_VALUE] = "Effort Driven";
      TASK_COLUMNS_ARRAY[TaskField.OVERTIME_WORK_VALUE] = "Overtime Work";
      TASK_COLUMNS_ARRAY[TaskField.ACTUAL_OVERTIME_WORK_VALUE] = "Actual Overtime Work";
      TASK_COLUMNS_ARRAY[TaskField.REMAINING_OVERTIME_WORK_VALUE] = "Remaining Overtime Work";
      TASK_COLUMNS_ARRAY[TaskField.REGULAR_WORK_VALUE] = "Regular Work";
      TASK_COLUMNS_ARRAY[TaskField.OVERTIME_COST_VALUE] = "Overtime Cost";
      TASK_COLUMNS_ARRAY[TaskField.ACTUAL_OVERTIME_COST_VALUE] = "Actual Overtime Cost";
      TASK_COLUMNS_ARRAY[TaskField.REMAINING_OVERTIME_COST_VALUE] = "Remaining Overtime Cost";
      TASK_COLUMNS_ARRAY[TaskField.FIXED_COST_ACCRUAL_VALUE] = "Fixed Cost Accrual";
      TASK_COLUMNS_ARRAY[TaskField.INDICATORS_VALUE] = "Indicators";
      TASK_COLUMNS_ARRAY[TaskField.HYPERLINK_VALUE] = "Hyperlink";
      TASK_COLUMNS_ARRAY[TaskField.HYPERLINK_ADDRESS_VALUE] = "Hyperlink Address";
      TASK_COLUMNS_ARRAY[TaskField.HYPERLINK_SUBADDRESS_VALUE] = "Hyperlink SubAddress";
      TASK_COLUMNS_ARRAY[TaskField.HYPERLINK_HREF_VALUE] = "Hyperlink Href";
      TASK_COLUMNS_ARRAY[TaskField.ASSIGNMENT_VALUE] = "Assignment";
      TASK_COLUMNS_ARRAY[TaskField.OVERALLOCATED_VALUE] = "Overallocated";
      TASK_COLUMNS_ARRAY[TaskField.EXTERNAL_TASK_VALUE] = "External Task";
      TASK_COLUMNS_ARRAY[TaskField.SUBPROJECT_READ_ONLY_VALUE] = "Subproject Read Only";
      TASK_COLUMNS_ARRAY[TaskField.RESPONSE_PENDING_VALUE] = "Response Pending";
      TASK_COLUMNS_ARRAY[TaskField.TEAMSTATUS_PENDING_VALUE] = "TeamStatus Pending";
      TASK_COLUMNS_ARRAY[TaskField.LEVELING_CAN_SPLIT_VALUE] = "Leveling Can Split";
      TASK_COLUMNS_ARRAY[TaskField.LEVEL_ASSIGNMENTS_VALUE] = "Level Assignments";
      TASK_COLUMNS_ARRAY[TaskField.WORK_CONTOUR_VALUE] = "Work Contour";
      TASK_COLUMNS_ARRAY[TaskField.COST4_VALUE] = "Cost4";
      TASK_COLUMNS_ARRAY[TaskField.COST5_VALUE] = "Cost5";
      TASK_COLUMNS_ARRAY[TaskField.COST6_VALUE] = "Cost6";
      TASK_COLUMNS_ARRAY[TaskField.COST7_VALUE] = "Cost7";
      TASK_COLUMNS_ARRAY[TaskField.COST8_VALUE] = "Cost8";
      TASK_COLUMNS_ARRAY[TaskField.COST9_VALUE] = "Cost9";
      TASK_COLUMNS_ARRAY[TaskField.COST10_VALUE] = "Cost10";
      TASK_COLUMNS_ARRAY[TaskField.DATE1_VALUE] = "Date1";
      TASK_COLUMNS_ARRAY[TaskField.DATE2_VALUE] = "Date2";
      TASK_COLUMNS_ARRAY[TaskField.DATE3_VALUE] = "Date3";
      TASK_COLUMNS_ARRAY[TaskField.DATE4_VALUE] = "Date4";
      TASK_COLUMNS_ARRAY[TaskField.DATE5_VALUE] = "Date5";
      TASK_COLUMNS_ARRAY[TaskField.DATE6_VALUE] = "Date6";
      TASK_COLUMNS_ARRAY[TaskField.DATE7_VALUE] = "Date7";
      TASK_COLUMNS_ARRAY[TaskField.DATE8_VALUE] = "Date8";
      TASK_COLUMNS_ARRAY[TaskField.DATE9_VALUE] = "Date9";
      TASK_COLUMNS_ARRAY[TaskField.DATE10_VALUE] = "Date10";
      TASK_COLUMNS_ARRAY[TaskField.DURATION4_VALUE] = "Duration4";
      TASK_COLUMNS_ARRAY[TaskField.DURATION5_VALUE] = "Duration5";
      TASK_COLUMNS_ARRAY[TaskField.DURATION6_VALUE] = "Duration6";
      TASK_COLUMNS_ARRAY[TaskField.DURATION7_VALUE] = "Duration7";
      TASK_COLUMNS_ARRAY[TaskField.DURATION8_VALUE] = "Duration8";
      TASK_COLUMNS_ARRAY[TaskField.DURATION9_VALUE] = "Duration9";
      TASK_COLUMNS_ARRAY[TaskField.DURATION10_VALUE] = "Duration10";
      TASK_COLUMNS_ARRAY[TaskField.START6_VALUE] = "Start6";
      TASK_COLUMNS_ARRAY[TaskField.FINISH6_VALUE] = "Finish6";
      TASK_COLUMNS_ARRAY[TaskField.START7_VALUE] = "Start7";
      TASK_COLUMNS_ARRAY[TaskField.FINISH7_VALUE] = "Finish7";
      TASK_COLUMNS_ARRAY[TaskField.START8_VALUE] = "Start8";
      TASK_COLUMNS_ARRAY[TaskField.FINISH8_VALUE] = "Finish8";
      TASK_COLUMNS_ARRAY[TaskField.START9_VALUE] = "Start9";
      TASK_COLUMNS_ARRAY[TaskField.FINISH9_VALUE] = "Finish9";
      TASK_COLUMNS_ARRAY[TaskField.START10_VALUE] = "Start10";
      TASK_COLUMNS_ARRAY[TaskField.FINISH10_VALUE] = "Finish10";
      TASK_COLUMNS_ARRAY[TaskField.FLAG11_VALUE] = "Flag11";
      TASK_COLUMNS_ARRAY[TaskField.FLAG12_VALUE] = "Flag12";
      TASK_COLUMNS_ARRAY[TaskField.FLAG13_VALUE] = "Flag13";
      TASK_COLUMNS_ARRAY[TaskField.FLAG14_VALUE] = "Flag14";
      TASK_COLUMNS_ARRAY[TaskField.FLAG15_VALUE] = "Flag15";
      TASK_COLUMNS_ARRAY[TaskField.FLAG16_VALUE] = "Flag16";
      TASK_COLUMNS_ARRAY[TaskField.FLAG17_VALUE] = "Flag17";
      TASK_COLUMNS_ARRAY[TaskField.FLAG18_VALUE] = "Flag18";
      TASK_COLUMNS_ARRAY[TaskField.FLAG19_VALUE] = "Flag19";
      TASK_COLUMNS_ARRAY[TaskField.FLAG20_VALUE] = "Flag20";
      TASK_COLUMNS_ARRAY[TaskField.NUMBER6_VALUE] = "Number6";
      TASK_COLUMNS_ARRAY[TaskField.NUMBER7_VALUE] = "Number7";
      TASK_COLUMNS_ARRAY[TaskField.NUMBER8_VALUE] = "Number8";
      TASK_COLUMNS_ARRAY[TaskField.NUMBER9_VALUE] = "Number9";
      TASK_COLUMNS_ARRAY[TaskField.NUMBER10_VALUE] = "Number10";
      TASK_COLUMNS_ARRAY[TaskField.NUMBER11_VALUE] = "Number11";
      TASK_COLUMNS_ARRAY[TaskField.NUMBER12_VALUE] = "Number12";
      TASK_COLUMNS_ARRAY[TaskField.NUMBER13_VALUE] = "Number13";
      TASK_COLUMNS_ARRAY[TaskField.NUMBER14_VALUE] = "Number14";
      TASK_COLUMNS_ARRAY[TaskField.NUMBER15_VALUE] = "Number15";
      TASK_COLUMNS_ARRAY[TaskField.NUMBER16_VALUE] = "Number16";
      TASK_COLUMNS_ARRAY[TaskField.NUMBER17_VALUE] = "Number17";
      TASK_COLUMNS_ARRAY[TaskField.NUMBER18_VALUE] = "Number18";
      TASK_COLUMNS_ARRAY[TaskField.NUMBER19_VALUE] = "Number19";
      TASK_COLUMNS_ARRAY[TaskField.NUMBER20_VALUE] = "Number20";
      TASK_COLUMNS_ARRAY[TaskField.TEXT11_VALUE] = "Text11";
      TASK_COLUMNS_ARRAY[TaskField.TEXT12_VALUE] = "Text12";
      TASK_COLUMNS_ARRAY[TaskField.TEXT13_VALUE] = "Text13";
      TASK_COLUMNS_ARRAY[TaskField.TEXT14_VALUE] = "Text14";
      TASK_COLUMNS_ARRAY[TaskField.TEXT15_VALUE] = "Text15";
      TASK_COLUMNS_ARRAY[TaskField.TEXT16_VALUE] = "Text16";
      TASK_COLUMNS_ARRAY[TaskField.TEXT17_VALUE] = "Text17";
      TASK_COLUMNS_ARRAY[TaskField.TEXT18_VALUE] = "Text18";
      TASK_COLUMNS_ARRAY[TaskField.TEXT19_VALUE] = "Text19";
      TASK_COLUMNS_ARRAY[TaskField.TEXT20_VALUE] = "Text20";
      TASK_COLUMNS_ARRAY[TaskField.TEXT21_VALUE] = "Text21";
      TASK_COLUMNS_ARRAY[TaskField.TEXT22_VALUE] = "Text22";
      TASK_COLUMNS_ARRAY[TaskField.TEXT23_VALUE] = "Text23";
      TASK_COLUMNS_ARRAY[TaskField.TEXT24_VALUE] = "Text24";
      TASK_COLUMNS_ARRAY[TaskField.TEXT25_VALUE] = "Text25";
      TASK_COLUMNS_ARRAY[TaskField.TEXT26_VALUE] = "Text26";
      TASK_COLUMNS_ARRAY[TaskField.TEXT27_VALUE] = "Text27";
      TASK_COLUMNS_ARRAY[TaskField.TEXT28_VALUE] = "Text28";
      TASK_COLUMNS_ARRAY[TaskField.TEXT29_VALUE] = "Text29";
      TASK_COLUMNS_ARRAY[TaskField.TEXT30_VALUE] = "Text30";
      TASK_COLUMNS_ARRAY[TaskField.RESOURCE_PHONETICS_VALUE] = "Phonetics";
      TASK_COLUMNS_ARRAY[TaskField.ASSIGNMENT_DELAY_VALUE] = "Assignment Delay";
      TASK_COLUMNS_ARRAY[TaskField.ASSIGNMENT_UNITS_VALUE] = "Assignment Units";
      TASK_COLUMNS_ARRAY[TaskField.COST_RATE_TABLE_VALUE] = "Cost Rate Table";
      TASK_COLUMNS_ARRAY[TaskField.PRELEVELED_START_VALUE] = "Preleveled Start";
      TASK_COLUMNS_ARRAY[TaskField.PRELEVELED_FINISH_VALUE] = "Preleveled Finish";
      TASK_COLUMNS_ARRAY[TaskField.ESTIMATED_VALUE] = "Estimated";
      TASK_COLUMNS_ARRAY[TaskField.IGNORE_RESOURCE_CALENDAR_VALUE] = "Ignore Resource Calendar";
      TASK_COLUMNS_ARRAY[TaskField.CALENDAR_VALUE] = "Task Calendar";
      TASK_COLUMNS_ARRAY[TaskField.OUTLINE_CODE1_VALUE] = "Outline Code1";
      TASK_COLUMNS_ARRAY[TaskField.OUTLINE_CODE2_VALUE] = "Outline Code2";
      TASK_COLUMNS_ARRAY[TaskField.OUTLINE_CODE3_VALUE] = "Outline Code3";
      TASK_COLUMNS_ARRAY[TaskField.OUTLINE_CODE4_VALUE] = "Outline Code4";
      TASK_COLUMNS_ARRAY[TaskField.OUTLINE_CODE5_VALUE] = "Outline Code5";
      TASK_COLUMNS_ARRAY[TaskField.OUTLINE_CODE6_VALUE] = "Outline Code6";
      TASK_COLUMNS_ARRAY[TaskField.OUTLINE_CODE7_VALUE] = "Outline Code7";
      TASK_COLUMNS_ARRAY[TaskField.OUTLINE_CODE8_VALUE] = "Outline Code8";
      TASK_COLUMNS_ARRAY[TaskField.OUTLINE_CODE9_VALUE] = "Outline Code9";
      TASK_COLUMNS_ARRAY[TaskField.OUTLINE_CODE10_VALUE] = "Outline Code10";
      TASK_COLUMNS_ARRAY[TaskField.DEADLINE_VALUE] = "Deadline";
      TASK_COLUMNS_ARRAY[TaskField.START_SLACK_VALUE] = "Start Slack";
      TASK_COLUMNS_ARRAY[TaskField.FINISH_SLACK_VALUE] = "Finish Slack";
      TASK_COLUMNS_ARRAY[TaskField.VAC_VALUE] = "VAC";
      TASK_COLUMNS_ARRAY[TaskField.GROUP_BY_SUMMARY_VALUE] = "Group By Summary";
      TASK_COLUMNS_ARRAY[TaskField.WBS_PREDECESSORS_VALUE] = "Predecesors";
      TASK_COLUMNS_ARRAY[TaskField.WBS_SUCCESSORS_VALUE] = "Successors";
      TASK_COLUMNS_ARRAY[TaskField.RESOURCE_TYPE_VALUE] = "Resource Type";

      RESOURCE_COLUMNS_ARRAY[ResourceField.ID_VALUE] = "ID";
      RESOURCE_COLUMNS_ARRAY[ResourceField.NAME_VALUE] = "Name";
      RESOURCE_COLUMNS_ARRAY[ResourceField.INITIALS_VALUE] = "Initials";
      RESOURCE_COLUMNS_ARRAY[ResourceField.GROUP_VALUE] = "Group";
      RESOURCE_COLUMNS_ARRAY[ResourceField.MAX_UNITS_VALUE] = "Max Units";
      RESOURCE_COLUMNS_ARRAY[ResourceField.BASE_CALENDAR_VALUE] = "Base Calendar";
      RESOURCE_COLUMNS_ARRAY[ResourceField.STANDARD_RATE_VALUE] = "Standard Rate";
      RESOURCE_COLUMNS_ARRAY[ResourceField.OVERTIME_RATE_VALUE] = "Overtime Rate";
      RESOURCE_COLUMNS_ARRAY[ResourceField.TEXT1_VALUE] = "Text1";
      RESOURCE_COLUMNS_ARRAY[ResourceField.TEXT2_VALUE] = "Text2";
      RESOURCE_COLUMNS_ARRAY[ResourceField.CODE_VALUE] = "Code";
      RESOURCE_COLUMNS_ARRAY[ResourceField.ACTUAL_COST_VALUE] = "Actual Cost";
      RESOURCE_COLUMNS_ARRAY[ResourceField.COST_VALUE] = "Cost";
      RESOURCE_COLUMNS_ARRAY[ResourceField.WORK_VALUE] = "Work";
      RESOURCE_COLUMNS_ARRAY[ResourceField.ACTUAL_WORK_VALUE] = "Actual Work";
      RESOURCE_COLUMNS_ARRAY[ResourceField.BASELINE_WORK_VALUE] = "Baseline Work";
      RESOURCE_COLUMNS_ARRAY[ResourceField.OVERTIME_WORK_VALUE] = "Overtime Work";
      RESOURCE_COLUMNS_ARRAY[ResourceField.BASELINE_COST_VALUE] = "Baseline Cost";
      RESOURCE_COLUMNS_ARRAY[ResourceField.COST_PER_USE_VALUE] = "Cost Per Use";
      RESOURCE_COLUMNS_ARRAY[ResourceField.ACCRUE_AT_VALUE] = "Accrue At";
      RESOURCE_COLUMNS_ARRAY[ResourceField.REMAINING_COST_VALUE] = "Remaining Cost";
      RESOURCE_COLUMNS_ARRAY[ResourceField.REMAINING_WORK_VALUE] = "Remaining Work";
      RESOURCE_COLUMNS_ARRAY[ResourceField.WORK_VARIANCE_VALUE] = "Work Variance";
      RESOURCE_COLUMNS_ARRAY[ResourceField.COST_VARIANCE_VALUE] = "Cost Variance";
      RESOURCE_COLUMNS_ARRAY[ResourceField.OVERALLOCATED_VALUE] = "Overallocated";
      RESOURCE_COLUMNS_ARRAY[ResourceField.PEAK_VALUE] = "Peak";
      RESOURCE_COLUMNS_ARRAY[ResourceField.UNIQUE_ID_VALUE] = "Unique ID";
      RESOURCE_COLUMNS_ARRAY[ResourceField.NOTES_VALUE] = "Notes";
      RESOURCE_COLUMNS_ARRAY[ResourceField.PERCENT_WORK_COMPLETE_VALUE] = "% Work Complete";
      RESOURCE_COLUMNS_ARRAY[ResourceField.TEXT3_VALUE] = "Text3";
      RESOURCE_COLUMNS_ARRAY[ResourceField.TEXT4_VALUE] = "Text4";
      RESOURCE_COLUMNS_ARRAY[ResourceField.TEXT5_VALUE] = "Text5";
      RESOURCE_COLUMNS_ARRAY[ResourceField.OBJECTS_VALUE] = "Object";
      RESOURCE_COLUMNS_ARRAY[ResourceField.LINKED_FIELDS_VALUE] = "Linked Fields";
      RESOURCE_COLUMNS_ARRAY[ResourceField.EMAIL_ADDRESS_VALUE] = "Email Address";
      RESOURCE_COLUMNS_ARRAY[ResourceField.REGULAR_WORK_VALUE] = "Regular Work";
      RESOURCE_COLUMNS_ARRAY[ResourceField.ACTUAL_OVERTIME_WORK_VALUE] = "Actual Overtime Work";
      RESOURCE_COLUMNS_ARRAY[ResourceField.REMAINING_OVERTIME_WORK_VALUE] = "Remaining Overtime Work";
      RESOURCE_COLUMNS_ARRAY[ResourceField.OVERTIME_COST_VALUE] = "Overtime Cost";
      RESOURCE_COLUMNS_ARRAY[ResourceField.ACTUAL_OVERTIME_COST_VALUE] = "Actual Overtime Cost";
      RESOURCE_COLUMNS_ARRAY[ResourceField.REMAINING_OVERTIME_COST_VALUE] = "Remaining Overtime Cost";
      RESOURCE_COLUMNS_ARRAY[ResourceField.BCWS_VALUE] = "BCWS";
      RESOURCE_COLUMNS_ARRAY[ResourceField.BCWP_VALUE] = "BCWP";
      RESOURCE_COLUMNS_ARRAY[ResourceField.ACWP_VALUE] = "ACWP";
      RESOURCE_COLUMNS_ARRAY[ResourceField.SV_VALUE] = "SV";
      RESOURCE_COLUMNS_ARRAY[ResourceField.AVAILABLE_FROM_VALUE] = "Available From";
      RESOURCE_COLUMNS_ARRAY[ResourceField.AVAILABLE_TO_VALUE] = "Available To";
      RESOURCE_COLUMNS_ARRAY[ResourceField.INDICATORS_VALUE] = "Indicators";
      RESOURCE_COLUMNS_ARRAY[ResourceField.TEXT6_VALUE] = "Text6";
      RESOURCE_COLUMNS_ARRAY[ResourceField.TEXT7_VALUE] = "Text7";
      RESOURCE_COLUMNS_ARRAY[ResourceField.TEXT8_VALUE] = "Text8";
      RESOURCE_COLUMNS_ARRAY[ResourceField.TEXT9_VALUE] = "Text9";
      RESOURCE_COLUMNS_ARRAY[ResourceField.TEXT10_VALUE] = "Text10";
      RESOURCE_COLUMNS_ARRAY[ResourceField.START1_VALUE] = "Start1";
      RESOURCE_COLUMNS_ARRAY[ResourceField.START2_VALUE] = "Start2";
      RESOURCE_COLUMNS_ARRAY[ResourceField.START3_VALUE] = "Start3";
      RESOURCE_COLUMNS_ARRAY[ResourceField.START4_VALUE] = "Start4";
      RESOURCE_COLUMNS_ARRAY[ResourceField.START5_VALUE] = "Start5";
      RESOURCE_COLUMNS_ARRAY[ResourceField.FINISH1_VALUE] = "Finish1";
      RESOURCE_COLUMNS_ARRAY[ResourceField.FINISH2_VALUE] = "Finish2";
      RESOURCE_COLUMNS_ARRAY[ResourceField.FINISH3_VALUE] = "Finish3";
      RESOURCE_COLUMNS_ARRAY[ResourceField.FINISH4_VALUE] = "Finish4";
      RESOURCE_COLUMNS_ARRAY[ResourceField.FINISH5_VALUE] = "Finish5";
      RESOURCE_COLUMNS_ARRAY[ResourceField.NUMBER1_VALUE] = "Number1";
      RESOURCE_COLUMNS_ARRAY[ResourceField.NUMBER2_VALUE] = "Number2";
      RESOURCE_COLUMNS_ARRAY[ResourceField.NUMBER3_VALUE] = "Number3";
      RESOURCE_COLUMNS_ARRAY[ResourceField.NUMBER4_VALUE] = "Number4";
      RESOURCE_COLUMNS_ARRAY[ResourceField.NUMBER5_VALUE] = "Number5";
      RESOURCE_COLUMNS_ARRAY[ResourceField.DURATION1_VALUE] = "Duration1";
      RESOURCE_COLUMNS_ARRAY[ResourceField.DURATION2_VALUE] = "Duration2";
      RESOURCE_COLUMNS_ARRAY[ResourceField.DURATION3_VALUE] = "Duration3";
      RESOURCE_COLUMNS_ARRAY[ResourceField.COST1_VALUE] = "Cost1";
      RESOURCE_COLUMNS_ARRAY[ResourceField.COST2_VALUE] = "Cost2";
      RESOURCE_COLUMNS_ARRAY[ResourceField.COST3_VALUE] = "Cost3";
      RESOURCE_COLUMNS_ARRAY[ResourceField.FLAG10_VALUE] = "Flag10";
      RESOURCE_COLUMNS_ARRAY[ResourceField.FLAG1_VALUE] = "Flag1";
      RESOURCE_COLUMNS_ARRAY[ResourceField.FLAG2_VALUE] = "Flag2";
      RESOURCE_COLUMNS_ARRAY[ResourceField.FLAG3_VALUE] = "Flag3";
      RESOURCE_COLUMNS_ARRAY[ResourceField.FLAG4_VALUE] = "Flag4";
      RESOURCE_COLUMNS_ARRAY[ResourceField.FLAG5_VALUE] = "Flag5";
      RESOURCE_COLUMNS_ARRAY[ResourceField.FLAG6_VALUE] = "Flag6";
      RESOURCE_COLUMNS_ARRAY[ResourceField.FLAG7_VALUE] = "Flag7";
      RESOURCE_COLUMNS_ARRAY[ResourceField.FLAG8_VALUE] = "Flag8";
      RESOURCE_COLUMNS_ARRAY[ResourceField.FLAG9_VALUE] = "Flag9";
      RESOURCE_COLUMNS_ARRAY[ResourceField.HYPERLINK_VALUE] = "Hyperlink";
      RESOURCE_COLUMNS_ARRAY[ResourceField.HYPERLINK_ADDRESS_VALUE] = "Hyperlink Address";
      RESOURCE_COLUMNS_ARRAY[ResourceField.HYPERLINK_SUBADDRESS_VALUE] = "Hyperlink SubAddress";
      RESOURCE_COLUMNS_ARRAY[ResourceField.HYPERLINK_HREF_VALUE] = "Hyperlink Href";
      RESOURCE_COLUMNS_ARRAY[ResourceField.ASSIGNMENT_VALUE] = "Assignment";
      RESOURCE_COLUMNS_ARRAY[ResourceField.TASK_SUMMARY_NAME_VALUE] = "Task Summary Name";
      RESOURCE_COLUMNS_ARRAY[ResourceField.CAN_LEVEL_VALUE] = "Can Level";
      RESOURCE_COLUMNS_ARRAY[ResourceField.WORK_CONTOUR_VALUE] = "Work Contour";
      RESOURCE_COLUMNS_ARRAY[ResourceField.COST4_VALUE] = "Cost4";
      RESOURCE_COLUMNS_ARRAY[ResourceField.COST5_VALUE] = "Cost5";
      RESOURCE_COLUMNS_ARRAY[ResourceField.COST6_VALUE] = "Cost6";
      RESOURCE_COLUMNS_ARRAY[ResourceField.COST7_VALUE] = "Cost7";
      RESOURCE_COLUMNS_ARRAY[ResourceField.COST8_VALUE] = "Cost8";
      RESOURCE_COLUMNS_ARRAY[ResourceField.COST9_VALUE] = "Cost9";
      RESOURCE_COLUMNS_ARRAY[ResourceField.COST10_VALUE] = "Cost10";
      RESOURCE_COLUMNS_ARRAY[ResourceField.DATE1_VALUE] = "Date1";
      RESOURCE_COLUMNS_ARRAY[ResourceField.DATE2_VALUE] = "Date2";
      RESOURCE_COLUMNS_ARRAY[ResourceField.DATE3_VALUE] = "Date3";
      RESOURCE_COLUMNS_ARRAY[ResourceField.DATE4_VALUE] = "Date4";
      RESOURCE_COLUMNS_ARRAY[ResourceField.DATE5_VALUE] = "Date5";
      RESOURCE_COLUMNS_ARRAY[ResourceField.DATE6_VALUE] = "Date6";
      RESOURCE_COLUMNS_ARRAY[ResourceField.DATE7_VALUE] = "Date7";
      RESOURCE_COLUMNS_ARRAY[ResourceField.DATE8_VALUE] = "Date8";
      RESOURCE_COLUMNS_ARRAY[ResourceField.DATE9_VALUE] = "Date9";
      RESOURCE_COLUMNS_ARRAY[ResourceField.DATE10_VALUE] = "Date10";
      RESOURCE_COLUMNS_ARRAY[ResourceField.DURATION4_VALUE] = "Duration4";
      RESOURCE_COLUMNS_ARRAY[ResourceField.DURATION5_VALUE] = "Duration5";
      RESOURCE_COLUMNS_ARRAY[ResourceField.DURATION6_VALUE] = "Duration6";
      RESOURCE_COLUMNS_ARRAY[ResourceField.DURATION7_VALUE] = "Duration7";
      RESOURCE_COLUMNS_ARRAY[ResourceField.DURATION8_VALUE] = "Duration8";
      RESOURCE_COLUMNS_ARRAY[ResourceField.DURATION9_VALUE] = "Duration9";
      RESOURCE_COLUMNS_ARRAY[ResourceField.DURATION10_VALUE] = "Duration10";
      RESOURCE_COLUMNS_ARRAY[ResourceField.FINISH6_VALUE] = "Finish6";
      RESOURCE_COLUMNS_ARRAY[ResourceField.FINISH7_VALUE] = "Finish7";
      RESOURCE_COLUMNS_ARRAY[ResourceField.FINISH8_VALUE] = "Finish8";
      RESOURCE_COLUMNS_ARRAY[ResourceField.FINISH9_VALUE] = "Finish9";
      RESOURCE_COLUMNS_ARRAY[ResourceField.FINISH10_VALUE] = "Finish10";
      RESOURCE_COLUMNS_ARRAY[ResourceField.FLAG11_VALUE] = "Flag11";
      RESOURCE_COLUMNS_ARRAY[ResourceField.FLAG12_VALUE] = "Flag12";
      RESOURCE_COLUMNS_ARRAY[ResourceField.FLAG13_VALUE] = "Flag13";
      RESOURCE_COLUMNS_ARRAY[ResourceField.FLAG14_VALUE] = "Flag14";
      RESOURCE_COLUMNS_ARRAY[ResourceField.FLAG15_VALUE] = "Flag15";
      RESOURCE_COLUMNS_ARRAY[ResourceField.FLAG16_VALUE] = "Flag16";
      RESOURCE_COLUMNS_ARRAY[ResourceField.FLAG17_VALUE] = "Flag17";
      RESOURCE_COLUMNS_ARRAY[ResourceField.FLAG18_VALUE] = "Flag18";
      RESOURCE_COLUMNS_ARRAY[ResourceField.FLAG19_VALUE] = "Flag19";
      RESOURCE_COLUMNS_ARRAY[ResourceField.FLAG20_VALUE] = "Flag20";
      RESOURCE_COLUMNS_ARRAY[ResourceField.NUMBER6_VALUE] = "Number6";
      RESOURCE_COLUMNS_ARRAY[ResourceField.NUMBER7_VALUE] = "Number7";
      RESOURCE_COLUMNS_ARRAY[ResourceField.NUMBER8_VALUE] = "Number8";
      RESOURCE_COLUMNS_ARRAY[ResourceField.NUMBER9_VALUE] = "Number9";
      RESOURCE_COLUMNS_ARRAY[ResourceField.NUMBER10_VALUE] = "Number10";
      RESOURCE_COLUMNS_ARRAY[ResourceField.NUMBER11_VALUE] = "Number11";
      RESOURCE_COLUMNS_ARRAY[ResourceField.NUMBER12_VALUE] = "Number12";
      RESOURCE_COLUMNS_ARRAY[ResourceField.NUMBER13_VALUE] = "Number13";
      RESOURCE_COLUMNS_ARRAY[ResourceField.NUMBER14_VALUE] = "Number14";
      RESOURCE_COLUMNS_ARRAY[ResourceField.NUMBER15_VALUE] = "Number15";
      RESOURCE_COLUMNS_ARRAY[ResourceField.NUMBER16_VALUE] = "Number16";
      RESOURCE_COLUMNS_ARRAY[ResourceField.NUMBER17_VALUE] = "Number17";
      RESOURCE_COLUMNS_ARRAY[ResourceField.NUMBER18_VALUE] = "Number18";
      RESOURCE_COLUMNS_ARRAY[ResourceField.NUMBER19_VALUE] = "Number19";
      RESOURCE_COLUMNS_ARRAY[ResourceField.NUMBER20_VALUE] = "Number20";
      RESOURCE_COLUMNS_ARRAY[ResourceField.START6_VALUE] = "Start6";
      RESOURCE_COLUMNS_ARRAY[ResourceField.START7_VALUE] = "Start7";
      RESOURCE_COLUMNS_ARRAY[ResourceField.START8_VALUE] = "Start8";
      RESOURCE_COLUMNS_ARRAY[ResourceField.START9_VALUE] = "Start9";
      RESOURCE_COLUMNS_ARRAY[ResourceField.START10_VALUE] = "Start10";
      RESOURCE_COLUMNS_ARRAY[ResourceField.TEXT11_VALUE] = "Text11";
      RESOURCE_COLUMNS_ARRAY[ResourceField.TEXT12_VALUE] = "Text12";
      RESOURCE_COLUMNS_ARRAY[ResourceField.TEXT13_VALUE] = "Text13";
      RESOURCE_COLUMNS_ARRAY[ResourceField.TEXT14_VALUE] = "Text14";
      RESOURCE_COLUMNS_ARRAY[ResourceField.TEXT15_VALUE] = "Text15";
      RESOURCE_COLUMNS_ARRAY[ResourceField.TEXT16_VALUE] = "Text16";
      RESOURCE_COLUMNS_ARRAY[ResourceField.TEXT17_VALUE] = "Text17";
      RESOURCE_COLUMNS_ARRAY[ResourceField.TEXT18_VALUE] = "Text18";
      RESOURCE_COLUMNS_ARRAY[ResourceField.TEXT19_VALUE] = "Text19";
      RESOURCE_COLUMNS_ARRAY[ResourceField.TEXT20_VALUE] = "Text20";
      RESOURCE_COLUMNS_ARRAY[ResourceField.TEXT21_VALUE] = "Text21";
      RESOURCE_COLUMNS_ARRAY[ResourceField.TEXT22_VALUE] = "Text22";
      RESOURCE_COLUMNS_ARRAY[ResourceField.TEXT23_VALUE] = "Text23";
      RESOURCE_COLUMNS_ARRAY[ResourceField.TEXT24_VALUE] = "Text24";
      RESOURCE_COLUMNS_ARRAY[ResourceField.TEXT25_VALUE] = "Text25";
      RESOURCE_COLUMNS_ARRAY[ResourceField.TEXT26_VALUE] = "Text26";
      RESOURCE_COLUMNS_ARRAY[ResourceField.TEXT27_VALUE] = "Text27";
      RESOURCE_COLUMNS_ARRAY[ResourceField.TEXT28_VALUE] = "Text28";
      RESOURCE_COLUMNS_ARRAY[ResourceField.TEXT29_VALUE] = "Text29";
      RESOURCE_COLUMNS_ARRAY[ResourceField.TEXT30_VALUE] = "Text30";
      RESOURCE_COLUMNS_ARRAY[ResourceField.PHONETICS_VALUE] = "Phonetics";
      RESOURCE_COLUMNS_ARRAY[ResourceField.ASSIGNMENT_DELAY_VALUE] = "Assignment Delay";
      RESOURCE_COLUMNS_ARRAY[ResourceField.ASSIGNMENT_UNITS_VALUE] = "Assignment Units";
      RESOURCE_COLUMNS_ARRAY[ResourceField.BASELINE_START_VALUE] = "Baseline Start";
      RESOURCE_COLUMNS_ARRAY[ResourceField.BASELINE_FINISH_VALUE] = "Baseline Finish";
      RESOURCE_COLUMNS_ARRAY[ResourceField.CONFIRMED_VALUE] = "Confirmed";
      RESOURCE_COLUMNS_ARRAY[ResourceField.FINISH_VALUE] = "Finish";
      RESOURCE_COLUMNS_ARRAY[ResourceField.LEVELING_DELAY_VALUE] = "Leveling Delay";
      RESOURCE_COLUMNS_ARRAY[ResourceField.RESPONSE_PENDING_VALUE] = "Response Pending";
      RESOURCE_COLUMNS_ARRAY[ResourceField.START_VALUE] = "Start";
      RESOURCE_COLUMNS_ARRAY[ResourceField.TEAMSTATUS_PENDING_VALUE] = "TeamStatus Pending";
      RESOURCE_COLUMNS_ARRAY[ResourceField.CV_VALUE] = "CV";
      RESOURCE_COLUMNS_ARRAY[ResourceField.UPDATE_NEEDED_VALUE] = "Update Needed";
      RESOURCE_COLUMNS_ARRAY[ResourceField.COST_RATE_TABLE_VALUE] = "Cost Rate Table";
      RESOURCE_COLUMNS_ARRAY[ResourceField.ACTUAL_START_VALUE] = "Actual Start";
      RESOURCE_COLUMNS_ARRAY[ResourceField.ACTUAL_FINISH_VALUE] = "Actual Finish";
      RESOURCE_COLUMNS_ARRAY[ResourceField.WORKGROUP_VALUE] = "Workgroup";
      RESOURCE_COLUMNS_ARRAY[ResourceField.PROJECT_VALUE] = "Project";
      RESOURCE_COLUMNS_ARRAY[ResourceField.OUTLINE_CODE1_VALUE] = "Outline Code1";
      RESOURCE_COLUMNS_ARRAY[ResourceField.OUTLINE_CODE2_VALUE] = "Outline Code2";
      RESOURCE_COLUMNS_ARRAY[ResourceField.OUTLINE_CODE3_VALUE] = "Outline Code3";
      RESOURCE_COLUMNS_ARRAY[ResourceField.OUTLINE_CODE4_VALUE] = "Outline Code4";
      RESOURCE_COLUMNS_ARRAY[ResourceField.OUTLINE_CODE5_VALUE] = "Outline Code5";
      RESOURCE_COLUMNS_ARRAY[ResourceField.OUTLINE_CODE6_VALUE] = "Outline Code6";
      RESOURCE_COLUMNS_ARRAY[ResourceField.OUTLINE_CODE7_VALUE] = "Outline Code7";
      RESOURCE_COLUMNS_ARRAY[ResourceField.OUTLINE_CODE8_VALUE] = "Outline Code8";
      RESOURCE_COLUMNS_ARRAY[ResourceField.OUTLINE_CODE9_VALUE] = "Outline Code9";
      RESOURCE_COLUMNS_ARRAY[ResourceField.OUTLINE_CODE10_VALUE] = "Outline Code10";
      RESOURCE_COLUMNS_ARRAY[ResourceField.MATERIAL_LABEL_VALUE] = "Material Label";
      RESOURCE_COLUMNS_ARRAY[ResourceField.TYPE_VALUE] = "Type";
      RESOURCE_COLUMNS_ARRAY[ResourceField.VAC_VALUE] = "VAC";
      RESOURCE_COLUMNS_ARRAY[ResourceField.GROUP_BY_SUMMARY_VALUE] = "Group By Summary";
      RESOURCE_COLUMNS_ARRAY[ResourceField.WINDOWS_USER_ACCOUNT_VALUE] = "Windows User Account";
   }

   private static final Object[][] RESOURCES =
   {
      {"TASK_COLUMNS", TASK_COLUMNS_ARRAY},
      {"RESOURCE_COLUMNS", RESOURCE_COLUMNS_ARRAY}
   };
}
