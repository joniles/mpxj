/*
 * file:       FileFormat9006.java
 * author:     Jon Iles
 * copyright:  (c) Packwood Software 2016
 * date:       27/01/2016
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

package net.sf.mpxj.asta;

/**
 * Asta EasyProject 3 file format.
 */
class FileFormat9006 extends AbstractFileFormat
{
   @Override public boolean epochDateFormat()
   {
      return false;
   }

   @Override protected String[] barColumnsOrder()
   {
      return BAR_COLUMNS_ORDER;
   }

   @Override protected String[] calendarColumnsOrder()
   {
      return CALENDAR_COLUMNS_ORDER;
   }

   @Override protected String[] consumableResourceColumnsOrder()
   {
      return CONSUMABLE_RESOURCE_COLUMNS_ORDER;
   }

   @Override protected String[] exceptionColumnsOrder()
   {
      return EXCEPTIONN_COLUMNS_ORDER;
   }

   @Override protected String[] exceptionAssignmentColumnsOrder()
   {
      return EXCEPTION_ASSIGNMENT_COLUMNS_ORDER;
   }

   @Override protected String[] expandedTaskColumnsOrder()
   {
      return EXPANDED_TASK_COLUMNS_ORDER;
   }

   @Override protected String[] linkColumnsOrder()
   {
      return LINK_COLUMNS_ORDER;
   }

   @Override protected String[] milestoneColumnsOrder()
   {
      return MILESTONE_COLUMNS_ORDER;
   }

   @Override protected String[] permanentResourceSkillColumnsOrder()
   {
      return PERMANENT_RESOURCE_SKILL_COLUMNS_ORDER;
   }

   @Override protected String[] permanentResourceColumnsOrder()
   {
      return PERMANENT_RESOURCE_COLUMNS_ORDER;
   }

   @Override protected String[] permanentScheduleAllocationColumnsOrder()
   {
      return PERMANENT_SCHEDULE_ALLOCATION_COLUMNS_ORDER;
   }

   @Override protected String[] projectSummaryColumnsOrder()
   {
      return PROJECT_SUMMARY_COLUMNS_ORDER;
   }

   @Override protected String[] taskColumnsOrder()
   {
      return TASK_COLUMNS_ORDER;
   }

   @Override protected String[] timeEntryColumnsOrder()
   {
      return TIME_ENTRY_COLUMNS_ORDER;
   }

   @Override protected String[] workPatternColumnsOrder()
   {
      return WORK_PATTERN_COLUMNS_ORDER;
   }

   @Override protected String[] wbsEntryColumnsOrder()
   {
      return WBS_ENTRY_COLUMNS_ORDER;
   }

   private static final String[] BAR_COLUMNS_ORDER =
   {
      "BARID",
      "STARV",
      "ENF",
      "NATURAL_ORDER",
      "SPARI_INTEGER",
      "NAMH",
      "EXPANDED_TASK",
      "PRIORITY",
      "UNSCHEDULABLE",
      "SUBPROJECT_ID",
      "ALT_ID",
      "LAST_EDITED_DATE",
      "LAST_EDITED_BY",
   };

   private static final String[] CALENDAR_COLUMNS_ORDER =
   {
      "CALENDARID",
      "SPARL_INTEGER",
      "NAMK",
      "DOMINANT_WORK_PATTERN",
      "CALENDAR",
      "DISPLAY_THRESHOLD",
      "NO_WORKING_TIME_COLOUR",
      "WORKING_TIME_COLOUR",
      "NUMBERING",
      "SHOW_PAST_DATES",
      "CREATED_AS_FOLDER",
      "ALT_ID",
      "LAST_EDITED_DATE",
      "LAST_EDITED_BY",
   };

   private static final String[] CONSUMABLE_RESOURCE_COLUMNS_ORDER =
   {
      "CONSUMABLE_RESOURCEID",
      "UNKNOWN",
      "UNKNOWN",
      "UNKNOWN",
      "UNKNOWN",
      "UNKNOWN",
      "UNKNOWN",
      "UNKNOWN",
      "UNKNOWN",
      "COST_PER_UNITCURRENCZ",
      "COST_PER_UNITAMOUNT",
      "INCOME_PER_UNITCURRENCZ",
      "INCOME_PER_UNITAMOUNT",
      "COST_PER_USEDEFAULTSCURRENCZ",
      "COST_PER_USEDEFAULTSAMOUNT",
      "INCOME_P_USEDEFAULTSCURRENCZ",
      "INCOME_P_USEDEFAULTSAMOUNT",
      "DURATIOPDEFAULTSTYPF",
      "DURATIOPDEFAULTSELA_MONTHS",
      "DURATIOPDEFAULTSHOURS",
      "DELAZDEFAULTSTYPF",
      "DELAZDEFAULTSELA_MONTHS",
      "DELAZDEFAULTSHOURS",
      "DEFAULTSQUANTITY",
      "DEFAULTSACTIVITY_CONV_FACTOR",
      "DEFAULTSCONSUMPTION_RATE",
      "DEFAULTSCONSUMPTION_RAT_UNIT",
      "DEFAULTSDURATION_TIMG_UNIT",
      "DEFAULTSDELAY_TIMF_UNIT",
      "DEFAULTSEXPENDITURE_C_CENTRE",
      "DEFAULTSINCOME_COST_CENTRE",
      "DEFAULTSTYPM",
      "DEFAULTSCALCULATEE_PARAMETER",
      "DEFAULTSBALANCINH_PARAMETER",
      "DEFAULTSCONSUMPTION_RAT_TYPE",
      "DEFAULTSUSE_TASL_CALENDAR",
      "DEFAULTSALLOD_PROPORTIONALLY",
      "DEFAULTSCONSUMED",
      "DEFAULTSACCOUNTEDA_ELSEWHERE",
      "DEFAULTSMAY_BE_SHORTERA_TASK",
      "AVAILABLE_FROM",
      "AVAILABLE_TO",
      "UNKNOWN",
      "UNKNOWN",
      "UNKNOWN",
      "UNKNOWN",
      "MEASUREMENT",
      "CONSUMABLE_RESOURCE",
      "ARR_STOUT_STRES_APARROW_TYPE",
      "ARR_STOUT_STRES_APLENGTH",
      "ARR_STOUT_STRES_APEDGE",
      "ARR_STOUT_STRES_APBORDET_COL",
      "ARR_STOUT_STRES_APINSIDG_COL",
      "ARR_STOUT_STRES_APPLACEMENW",
      "BLI_STOUT_STRES_APBLIP_TYPE",
      "BLI_STOUT_STRES_APSCALEY",
      "BLI_STOUT_STRES_APSCALEZ",
      "BLI_STOUT_STRES_APGAP",
      "BLI_STOUT_STRES_APBORDES_COL",
      "BLI_STOUT_STRES_APINSIDF_COL",
      "BLI_STOUT_STRES_APPLACEMENV",
      "LIN_STOUT_STRES_APSCALEX",
      "LIN_STOUT_STRES_APWIDTH",
      "LIN_STOUT_STRES_APBORDER_COL",
      "LIN_STOUT_STRES_APINSIDE_COL",
      "LIN_STOUT_STRES_APLINE_TYPE",
      "RES_APFOREGROUND_FILL_COLOUR",
      "RES_APBACKGROUND_FILL_COLOUR",
      "RES_APPATTERN",
      "AVAILABILITY",
      "TOTAL_AVAILABILITY",
      "SPAWE_INTEGER",
      "NASE",
      "SHORT_NAME_SINGLE",
      "SHORT_NAME_PLURAL",
      "CALENDAV",
      "USE_PARENV_CALENDAR",
      "USE_LINE_STYLE_P_ALLOCATIONS",
      "CREATED_AS_FOLDER",
      "ALT_ID",
      "LAST_EDITED_DATE",
      "LAST_EDITED_BY"
   };

   private static final String[] EXCEPTIONN_COLUMNS_ORDER =
   {
      "EXCEPTIONNID",
      "ARR_STOUT_STAPPANDARROW_TYPE",
      "ARR_STOUT_STAPPANDLENGTH",
      "ARR_STOUT_STAPPANDEDGE",
      "ARR_STOUT_STAPPANDBORDET_COL",
      "ARR_STOUT_STAPPANDINSIDG_COL",
      "ARR_STOUT_STAPPANDPLACEMENW",
      "BLI_STOUT_STAPPANDBLIP_TYPE",
      "BLI_STOUT_STAPPANDSCALEY",
      "BLI_STOUT_STAPPANDSCALEZ",
      "BLI_STOUT_STAPPANDGAP",
      "BLI_STOUT_STAPPANDBORDES_COL",
      "BLI_STOUT_STAPPANDINSIDF_COL",
      "BLI_STOUT_STAPPANDPLACEMENV",
      "LIN_STOUT_STAPPANDSCALEX",
      "LIN_STOUT_STAPPANDWIDTH",
      "LIN_STOUT_STAPPANDBORDER_COL",
      "LIN_STOUT_STAPPANDINSIDE_COL",
      "LIN_STOUT_STAPPANDLINE_TYPE",
      "APPANDFOREGROUND_FILL_COLOUR",
      "APPANDBACKGROUND_FILL_COLOUR",
      "APPANDPATTERN",
      "UNIQUE_BIT_FIELD",
      "NAML",
      "TYPG",
      "ALT_ID",
      "LAST_EDITED_DATE",
      "LAST_EDITED_BY"
   };

   private static final String[] EXCEPTION_ASSIGNMENT_COLUMNS_ORDER =
   {
      "EXCEPTION_ASSIGNMENTID",
      "STARU_DATE",
      "ENE_DATE",
      "EXCEPTIOO",
   };

   private static final String[] EXPANDED_TASK_COLUMNS_ORDER =
   {
      "EXPANDED_TASKID",
      "COMM_ATTSSCALE1",
      "COMM_ATTSSCALE2",
      "COMM_ATTSSCALE3",
      "UNKNOWN",
      "UNKNOWN",
      "UNKNOWN",
      "UNKNOWN",
      "PERCENTAGE_LIKELIHOOD",
      "PROJ_RISK",
      "PROJ_PRIORITY",
      "ISSUE_DATE",
      "REVISION_DATE",
      "DRAWN_BY",
      "REVISION_COMMENT",
      "CHART_MANAGER",
      "REVISION_NUMBER",
      "PROGRAMME_NUMBER",
      "COMMENU",
      "PROJ_TYPE",
      "PROJ_STATUS",
      "UNKNOWN",
      "PROGRESU_PERIOD",
      "TYPH",
      "UNKNOWN",
      "UNKNOWN",
      "MANAGER_RESOURCE",
      "CUMULATIVH_COSTCURRENCZ",
      "CUMULATIVH_COSTAMOUNT",
      "CUMULATIVH_INCOMECURRENCZ",
      "CUMULATIVH_INCOMEAMOUNT",
      "CUMULATIVE_ACTU_COSTCURRENCZ",
      "CUMULATIVE_ACTU_COSTAMOUNT",
      "CUMULATIV_DURATIONTYPF",
      "CUMULATIV_DURATIONELA_MONTHS",
      "CUMULATIV_DURATIONHOURS",
      "ACTUAL_CU_DURATIONTYPF",
      "ACTUAL_CU_DURATIONELA_MONTHS",
      "ACTUAL_CU_DURATIONHOURS",
      "ACTUAL_CUMULATIVE_QUANTITY",
      "CUMULATIVE_QUANTIT_REMAINING",
      "CUMULATIVE_EFFORT_P_COMPLETE",
      "CUMULATIVE_WORK_PER_COMPLETE",
      "CUMULATIVE_QUANTITY_COMPLETE",
      "MILESTONE_PERCENT_COMPLETE",
      "FIRST_PREFERRED_START",
      "CALCULATED_PROGRESS_DATE",
      "LATEST_PROGRESS_DATE",
      "EARLIEST_PROGRESS_DATE",
      "EARLY_END_DATE_RT",
      "LATE_END_DATE_RT",
      "FREE_END_DATE_RT",
      "CUMULATIVE_DEMANE_EFFORT",
      "CUMULATIVE_SCHEDULEE_EFFORT",
      "ACTUAL_CUMULATIVF_EFFORT",
      "CUMULATIVE_EFFORU_REMAINING",
      "ACTUAL_CUMULATIVE_WORK",
      "CUMULATIVE_WORK_REMAINING",
      "MILESTONES_DONE",
      "MILESTONES_REMAINING",
      "CUMULATIVE_EFFORT_TIME_UNIT",
      "CUMULATIVE_LATEST_PRO_PERIOD",
      "UNKNOWN",
      "UNKNOWN",
      "UNKNOWN",
      "UNKNOWN",
      "UNKNOWN",
      "UNKNOWN",
      "ACTUAL_DURATIONTYPF",
      "ACTUAL_DURATIONELA_MONTHS",
      "ACTUAL_DURATIONHOURS",
      "EARLY_START_DATE",
      "LATE_START_DATE",
      "FREE_START_DATE",
      "START_CONSTRAINT_DATE",
      "END_CONSTRAINT_DATE",
      "EFFORT_BUDGET",
      "NATURAO_ORDER",
      "UNKNOWN",
      "UNKNOWN",
      "UNKNOWN",
      "LOGICAL_PRECEDENCE",
      "UNKNOWN",
      "UNKNOWN",
      "SPAVE_INTEGER",
      "SWIM_LANE",
      "USER_PERCENT_COMPLETE",
      "OVERALL_PERCENT_COMPL_WEIGHT",
      "NARE",
      "WBN_CODE",
      "NOTET",
      "UNIQUE_TASK_ID",
      "CALENDAU",
      "EFFORT_TIMI_UNIT",
      "WORL_UNIT",
      "LATEST_ALLOC_PROGRESS_PERIOD",
      "WORN",
      "UNKNOWN",
      "BAR",
      "CONSTRAINU",
      "PRIORITB",
      "USE_PARENU_CALENDAR",
      "CRITICAM",
      "BUFFER_TASK",
      "MARK_FOS_HIDING",
      "OWNED_BY_TIMESHEEV_X",
      "START_ON_NEX_DAY",
      "DURATIOTHOURS",
      "STARZ",
      "ENJ",
      "DURATION_TIMJ_UNIT",
      "UNSCHEDULABLG",
      "SUBPROJECT_ID",
      "ALT_ID",
      "LAST_EDITED_DATE",
      "LAST_EDITED_BY",
   };

   private static final String[] LINK_COLUMNS_ORDER =
   {
      "LINKID",
      "START_LAG_TIMETYPF",
      "START_LAG_TIMEELA_MONTHS",
      "START_LAG_TIMEHOURS",
      "END_LAG_TIMETYPF",
      "END_LAG_TIMEELA_MONTHS",
      "END_LAG_TIMEHOURS",
      "MAXIMUM_LAGTYPF",
      "MAXIMUM_LAGELA_MONTHS",
      "MAXIMUM_LAGHOURS",
      "STARV_DATE",
      "ENF_DATE",
      "CURVATURE_PERCENTAGE",
      "COMMENTS",
      "LINK_CATEGORY",
      "START_LAG_TIME_UNIT",
      "END_LAG_TIME_UNIT",
      "MAXIMUM_LAG_TIME_UNIT",
      "START_TASK",
      "END_TASK",
      "START_LAG_PERCENT_FLOAT",
      "START_LAG_TYPE",
      "TYPI",
      "MAINTAIN_TASK_OFFSETS",
      "END_LAG_TYPE",
      "UNSCHEDULABLF",
      "CRITICAL",
      "ON_LOOP",
      "MAXIMUM_LAG_MODE",
      "ANNOTATE_LEAD_LAG",
      "START_REPOSITION_ON_TAS_MOVE",
      "END_REPOSITION_ON_TASK_MOVE",
      "DRAW_CURVED_IF_VERTICAL",
      "AUTOMATIC_CURVED_LI_SETTINGS",
      "DRAW_CURVED_LINK_TO_LEFT",
      "LOCAL_LINK",
      "DRIVING",
      "ALT_ID",
      "LAST_EDITED_DATE",
      "LAST_EDITED_BY",
   };

   private static final String[] MILESTONE_COLUMNS_ORDER =
   {
      "MILESTONEID",
      "GIVEN_DATE_TIME",
      "PROGREST_PERIOD",
      "SYMBOL_APPEARANCE",
      "MILESTONE_TYPE",
      "PLACEMENU",
      "COMPLETED",
      "UNKNOWN",
      "UNKNOWN",
      "UNKNOWN",
      "UNKNOWN",

      "UNKNOWN",
      "UNKNOWN",
      "UNKNOWN",
      "UNKNOWN",
      "UNKNOWN",

      "EARLY_START_DATE",
      "LATE_START_DATE",
      "FREE_START_DATE",
      "START_CONSTRAINT_DATE",
      "END_CONSTRAINT_DATE",
      "EFFORT_BUDGET",
      "NATURAO_ORDER",

      "LOGICAL_PRECEDENCE",
      "SPAVE_INTEGER",
      "SWIM_LANE",
      "USER_PERCENT_COMPLETE",
      "UNKNOWN",
      "UNKNOWN",
      "UNKNOWN",
      "UNKNOWN",
      "UNKNOWN",
      "OVERALL_PERCENT_COMPL_WEIGHT",
      "NARE",
      "WBN_CODE",
      "NOTET",
      "UNIQUE_TASK_ID",
      "CALENDAU",
      "EFFORT_TIMI_UNIT",
      "WORL_UNIT",
      "LATEST_ALLOC_PROGRESS_PERIOD",
      "WORN",
      "UNKNOWN",
      "BAR",
      "CONSTRAINU",
      "PRIORITB",
      "UNKNOWN",
      "CRITICAM",
      "USE_PARENU_CALENDAR",
      "BUFFER_TASK",
      "MARK_FOS_HIDING",
      "OWNED_BY_TIMESHEEV_X",
      "UNKNOWN",
      "STARZ",
      "ENJ",
      "DURATION_TIMJ_UNIT",
      "UNSCHEDULABLG",
      "SUBPROJECT_ID",
      "ALT_ID",
      "LAST_EDITED_DATE",
      "LAST_EDITED_BY",
   };

   private static final String[] PERMANENT_RESOURCE_SKILL_COLUMNS_ORDER =
   {
      "PERM_RESOURCE_SKILLID",
      "UNKNOWN",
      "UNKNOWN",
      "UNKNOWN",
      "UNKNOWN",
      "ARR_STOUT_STSKI_APARROW_TYPE",
      "ARR_STOUT_STSKI_APLENGTH",
      "ARR_STOUT_STSKI_APEDGE",
      "ARR_STOUT_STSKI_APBORDET_COL",
      "ARR_STOUT_STSKI_APINSIDG_COL",
      "ARR_STOUT_STSKI_APPLACEMENW",
      "BLI_STOUT_STSKI_APBLIP_TYPE",
      "BLI_STOUT_STSKI_APSCALEY",
      "BLI_STOUT_STSKI_APSCALEZ",
      "BLI_STOUT_STSKI_APGAP",
      "BLI_STOUT_STSKI_APBORDES_COL",
      "BLI_STOUT_STSKI_APINSIDF_COL",
      "BLI_STOUT_STSKI_APPLACEMENV",
      "LIN_STOUT_STSKI_APSCALEX",
      "LIN_STOUT_STSKI_APWIDTH",
      "LIN_STOUT_STSKI_APBORDER_COL",
      "LIN_STOUT_STSKI_APINSIDE_COL",
      "LIN_STOUT_STSKI_APLINE_TYPE",
      "SKI_APFOREGROUND_FILL_COLOUR",
      "SKI_APBACKGROUND_FILL_COLOUR",
      "SKI_APPATTERN",
      "DURATIOODEFAULTTTYPF",
      "DURATIOODEFAULTTELA_MONTHS",
      "DURATIOODEFAULTTHOURS",
      "DELAYDEFAULTTTYPF",
      "DELAYDEFAULTTELA_MONTHS",
      "DELAYDEFAULTTHOURS",
      "DEFAULTTALLOCATION",
      "DEFAULTTWORK_FROM_ACT_FACTOR",
      "DEFAULTTEFFORT",
      "DEFAULTTWORL",
      "DEFAULTTWORK_RATE",
      "DEFAULTTWORK_UNIT",
      "DEFAULTTWORK_RATE_TIME_UNIT",
      "DEFAULTTEFFORT_TIMG_UNIT",
      "DEFAULTTDURATION_TIMF_UNIT",
      "DEFAULTTDELAY_TIME_UNIT",
      "DEFAULTTTYPL",
      "DEFAULTTCALCULATED_PARAMETER",
      "DEFAULTTBALANCING_PARAMETER",
      "DEFAULTTWORK_RATE_TYPE",
      "DEFAULTTUSE_TASK_CALENDAR",
      "DEFAULTTALLOC_PROPORTIONALLY",
      "DEFAULTTCAN_BE_SPLIT",
      "DEFAULTTCAN_BE_DELAYED",
      "DEFAULTTCAN_BE_STRETCHED",
      "DEFAULTTACCOUNTED__ELSEWHERE",
      "DEFAULTTCONTRIBUTES_T_EFFORT",
      "DEFAULTTMAY_BE_SHORTER__TASK",
      "DEFAULTTSHARED_EFFORT",
      "ABILITY",
      "AVAILABLF_FROM",
      "AVAILABLF_TO",
      "UNKNOWN",
      "UNKNOWN",
      "UNKNOWN",
      "UNKNOWN",
      "UNKNOWN",
      "UNKNOWN",
      "SPARO_INTEGER",
      "EFFORT_TIMF_UNIT",
      "ROLE",
      "PLAYER",
      "CREATED_AS_FOLDER",
      "ALT_ID",
      "LAST_EDITED_DATE",
      "LAST_EDITED_BY",
   };

   private static final String[] PERMANENT_RESOURCE_COLUMNS_ORDER =
   {
      "PERMANENT_RESOURCEID",
      "UNKNOWN",
      "UNKNOWN",
      "UNKNOWN",
      "UNKNOWN",
      "UNKNOWN",
      "UNKNOWN",
      "UNKNOWN",
      "UNKNOWN",
      "UNKNOWN",
      "UNKNOWN",
      "UNKNOWN",
      "UNKNOWN",
      "UNKNOWN",
      "UNKNOWN",
      "UNKNOWN",
      "UNKNOWN",
      "UNKNOWN",
      "EFFORT_TIME_UNIT",
      "PURE_TREE",
      "ARR_STOUT_STRES_APARROW_TYPE",
      "ARR_STOUT_STRES_APLENGTH",
      "ARR_STOUT_STRES_APEDGE",
      "ARR_STOUT_STRES_APBORDET_COL",
      "ARR_STOUT_STRES_APINSIDG_COL",
      "ARR_STOUT_STRES_APPLACEMENW",
      "BLI_STOUT_STRES_APBLIP_TYPE",
      "BLI_STOUT_STRES_APSCALEY",
      "BLI_STOUT_STRES_APSCALEZ",
      "BLI_STOUT_STRES_APGAP",
      "BLI_STOUT_STRES_APBORDES_COL",
      "BLI_STOUT_STRES_APINSIDF_COL",
      "BLI_STOUT_STRES_APPLACEMENV",
      "LIN_STOUT_STRES_APSCALEX",
      "LIN_STOUT_STRES_APWIDTH",
      "LIN_STOUT_STRES_APBORDER_COL",
      "LIN_STOUT_STRES_APINSIDE_COL",
      "LIN_STOUT_STRES_APLINE_TYPE",
      "RES_APFOREGROUND_FILL_COLOUR",
      "RES_APBACKGROUND_FILL_COLOUR",
      "RES_APPATTERN",
      "AVAILABILITY",
      "TOTAL_AVAILABILITY",
      "SPAWE_INTEGER",
      "NASE",
      "SHORT_NAME_SINGLE",
      "SHORT_NAME_PLURAL",
      "CALENDAV",
      "USE_PARENV_CALENDAR",
      "USE_LINE_STYLE_P_ALLOCATIONS",
      "ALT_ID",
      "UNKNOWN",
      "LAST_EDITED_DATE",
      "LAST_EDITED_BY"
   };

   private static final String[] PERMANENT_SCHEDULE_ALLOCATION_COLUMNS_ORDER =
   {
      "PERMANENT_SCHEDUL_ALLOCATIONID",
      "REQUIREE_BY",
      "EFFORW",
      "GIVEN_EFFORT",
      "UNKNOWN",
      "WORK_FROM_TASK_FACTOR",
      "ALLOCATIOO",
      "GIVEN_ALLOCATION",
      "ALLOCATIOP_OF",
      "WORM_UNIT",
      "WORK_RATE_TIMF_UNIT",
      "EFFORT_TIMJ_UNIT",
      "WORO",
      "GIVEN_WORK",
      "UNKNOWN",
      "WORL_RATE",
      "GIVEN_WORK_RATE",
      "TYPV",
      "CALCULATEG_PARAMETER",
      "BALANCINJ_PARAMETER",
      "SHAREE_EFFORT",

      "UNKNOWN",
      "UNKNOWN",
      "UNKNOWN",

      "CONTRIBUTES_TO_ACTIVI_EFFORT",
      "DELAATYPF",
      "DELAAELA_MONTHS",
      "DELAAHOURS",
      "GIVEO_DURATIONTYPF",
      "UNKNOWN",
      "GIVEO_DURATIONHOURS",
      "DELAY_TIMI_UNIT",
      "RATE_TYPE",
      "USE_TASM_CALENDAR",
      "IGNORF",
      "ELAPSEE",
      "MAY_BE_SHORTER_THAN_TASK",

      "UNKNOWN",
      "UNKNOWN",
      "UNKNOWN",
      "UNKNOWN",

      "RESUMF",
      "SPAXE_INTEGER",
      "PERCENT_COMPLETE",
      "USER_PERCENU_COMPLETE",
      "ALLOCATIOR_GROUP",
      "ALLOCATEE_TO",
      "PRIORITC",
      "ACCOUNTED_FOR_ELSEWHERE",
      "DURATIOTTYPF",
      "DURATIOTELA_MONTHS",
      "DURATIOTHOURS",
      "STARZ",
      "ENJ",
      "DURATION_TIMJ_UNIT",
      "UNSCHEDULABLG",
      "SUBPROJECT_ID",
      "ALT_ID",
      "LAST_EDITED_DATE",
      "LAST_EDITED_BY",
   };

   private static final String[] PROJECT_SUMMARY_COLUMNS_ORDER =
   {
      "PROJECT_SUMMARYID",
      "DURATIONTYPF",
      "DURATIONELA_MONTHS",
      "DURATIONHOURS",
      "STARU",
      "ENE",
      "FISCAL_YEAR_START",
      "DS_ID_BOOKED_FROM",
      "LAST_ID_USED_IN_BASELINE",
      "UNKNOWN",
      "WBN_CONSTRAINT",
      "WBN_RANGE_FROM",
      "WBN_RANGE_TO",
      "WBN_INCREMENT",
      "WBN_MINIMUM_WIDTH",
      "SPARF_INTEGER",
      "UTID_CONSTRAINT",
      "UTID_START_VALUE",
      "UTID_INCREMENT",
      "UTID_SUB_INCREMENT",
      "UTID_MINIMUM_WIDTH",
      "INITIAL_VIEW",
      "POINT_RELEASE",
      "TIMESHEET_PROJECT_ID",
      "LAST_ID_USED_IN_ARCHIVES",
      "BOOKOUT_SET_UNIQUE_ID",
      "NUMBER_BOOKED_OUT_SETS",
      "SHORT_NAME",
      "LONG_NAME",
      "LOCAL_FILE_BOOKED_FROM",
      "WBN_START_VALUE",
      "WBN_PATHNAME_SEPARATOR",
      "WBN_TASK_SEPARATOR",
      "WBN_PREFIX",
      "LAST_WBN_USED",
      "PROJECT_FOR",
      "PROJECT_BY",
      "PATH_SEPARATOR",
      "CHART_PATH_SEPARATOR",
      "UTID_PREFIX",
      "TIMESHEET_CONNECTION",
      "DURATION_TIME_UNIT",
      "SECURITY_CODELIBRARY",
      "BOOKOUT_COUNTER",
      "PROGRESS_METHOD",
      "WBN_ENABLED",
      "OLD_START_VALUE",
      "IGNORE_SATISFIED_COSTS",
      "UTID_ENABLE_SUB_INCREMENTS",
      "EXCLUSIVE_CUSTOM_TIME_UNITS",
      "LAST_EDITED_DATE",
      "LAST_EDITED_BY",
   };

   private static final String[] TASK_COLUMNS_ORDER =
   {
      "TASKID",
      "GIVEN_DURATIONTYPF",
      "GIVEN_DURATIONELA_MONTHS",
      "GIVEN_DURATIONHOURS",
      "RESUME",
      "GIVEN_START",
      "LATEST_PROGRESS_PERIOD",
      "TASK_WORK_RATE",
      "PLACEMENT",
      "BEEN_SPLIT",

      "UNKNOWN1",
      "UNKNOWN2",
      "UNKNOWN3",
      "UNKNOWN4",
      "UNKNOWN5",
      "UNKNOWN6",
      "UNKNOWN7",
      "UNKNOWN8",
      "UNKNOWN9",
      "EARLY_START_DATE",
      "LATE_START_DATE",
      "FREE_START_DATE",
      "START_CONSTRAINT_DATE",
      "END_CONSTRAINT_DATE",
      "EFFORT_BUDGET",
      "USER_PERCENT_COMPLETE",
      "SPAVE_INTEGER",
      "SWIM_LANE",
      "NATURAO_ORDER",
      "LOGICAL_PRECEDENCE",
      "UNKNOWN",
      "UNKNOWN",
      "UNKNOWN",
      "UNKNOWN",
      "UNKNOWN",
      "OVERALL_PERCENT_COMPL_WEIGHT",
      "NARE",
      "WBN_CODE",
      "NOTET",
      "UNIQUE_TASK_ID",
      "CALENDAU",
      "EFFORT_TIMI_UNIT",
      "WORL_UNIT",
      "LATEST_ALLOC_PROGRESS_PERIOD",
      "WORN",
      "UNKNOWN",
      "BAR",
      "CONSTRAINU",
      "PRIORITB",
      "MARK_FOS_HIDING",
      "LONGEST_PATH",
      "START_ON_NEX_DAY",
      "OWNED_BY_TIMESHEEV_X",
      "DURATIOTTYPF",
      "DURATIOTELA_MONTHS",
      "DURATIOTHOURS",
      "STARZ",
      "ENJ",
      "DURATION_TIMJ_UNIT",
      "UNSCHEDULABLG",
      "SUBPROJECT_ID",
      "ALT_ID",
      "LAST_EDITED_DATE",
      "LAST_EDITED_BY",
   };

   private static final String[] TIME_ENTRY_COLUMNS_ORDER =
   {
      "TIME_ENTRYID",
      "EXCEPTIOP",
      "START_TIME",
      "END_TIME",
   };

   private static final String[] WORK_PATTERN_COLUMNS_ORDER =
   {
      "WORK_PATTERNID",
      "DEFAULT_OFFSET",
      "NAMN",
      "DEFAULT_ALIGNMENT_DATE",
      "CREATED_AS_FOLDER",
      "ALT_ID",
      "LAST_EDITED_DATE",
      "LAST_EDITED_BY",
   };

   private static final String[] WBS_ENTRY_COLUMNS_ORDER =
   {
      "WBS_ENTRYID",
      "NATURAP_ORDER",
      "WBT_CODE",
      "WBT_NAME",
      "WBS_ENTRY",
      "CREATED_AS_FOLDER",
      "ALT_ID",
      "LAST_EDITED_DATE",
      "LAST_EDITED_BY"
   };
}
