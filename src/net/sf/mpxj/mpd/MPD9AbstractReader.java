/*
 * file:       MPD9AbstractReader.java
 * author:     Jon Iles
 * copyright:  (c) Packwood Software 2007
 * date:       02/02/2006
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

package net.sf.mpxj.mpd;

import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import net.sf.mpxj.AccrueType;
import net.sf.mpxj.ConstraintType;
import net.sf.mpxj.DateRange;
import net.sf.mpxj.Day;
import net.sf.mpxj.Duration;
import net.sf.mpxj.FieldType;
import net.sf.mpxj.MPPResourceField;
import net.sf.mpxj.MPPTaskField;
import net.sf.mpxj.Priority;
import net.sf.mpxj.ProjectCalendar;
import net.sf.mpxj.ProjectCalendarException;
import net.sf.mpxj.ProjectCalendarHours;
import net.sf.mpxj.ProjectFile;
import net.sf.mpxj.ProjectHeader;
import net.sf.mpxj.Rate;
import net.sf.mpxj.RelationType;
import net.sf.mpxj.Resource;
import net.sf.mpxj.ResourceAssignment;
import net.sf.mpxj.ResourceField;
import net.sf.mpxj.ResourceType;
import net.sf.mpxj.ScheduleFrom;
import net.sf.mpxj.Task;
import net.sf.mpxj.TaskField;
import net.sf.mpxj.TaskType;
import net.sf.mpxj.TimeUnit;
import net.sf.mpxj.WorkContour;
import net.sf.mpxj.WorkGroup;
import net.sf.mpxj.utility.NumberUtility;
import net.sf.mpxj.utility.Pair;
import net.sf.mpxj.utility.RTFUtility;

/**
 * This class implements retrieval of data from a project database 
 * independently of whether the data is read directly from an MDB file,
 * or from a JDBC database connection.
 */
abstract class MPD9AbstractReader
{
   /**
    * Retrieve the details of a single project from the database.
    * 
    * @param result Map instance containing the results
    * @param row result set row read from the database
    */
   protected void processProjectListItem(Map<Integer, String> result, Row row)
   {
      Integer id = row.getInteger("PROJ_ID");
      String name = row.getString("PROJ_NAME");
      result.put(id, name);
   }

   /**
    * Reads the project header.
    * 
    * @param row project header data
    */
   protected void processProjectHeader(Row row)
   {
      ProjectHeader header = m_project.getProjectHeader();

      header.setCurrencySymbol(row.getString("PROJ_OPT_CURRENCY_SYMBOL"));
      header.setSymbolPosition(MPDUtility.getSymbolPosition(row.getInt("PROJ_OPT_CURRENCY_POSITION")));
      header.setCurrencyDigits(row.getInteger("PROJ_OPT_CURRENCY_DIGITS"));
      //header.setThousandsSeparator();
      //header.setDecimalSeparator();
      header.setDefaultDurationUnits(MPDUtility.getDurationTimeUnits(row.getInt("PROJ_OPT_DUR_ENTRY_FMT")));
      //header.setDefaultDurationIsFixed();      
      header.setDefaultWorkUnits(MPDUtility.getDurationTimeUnits(row.getInt("PROJ_OPT_WORK_ENTRY_FMT")));
      header.setMinutesPerDay(row.getInteger("PROJ_OPT_MINUTES_PER_DAY"));
      header.setMinutesPerWeek(row.getInteger("PROJ_OPT_MINUTES_PER_WEEK"));
      header.setDefaultStandardRate(new Rate(row.getDouble("PROJ_OPT_DEF_STD_RATE"), TimeUnit.HOURS));
      header.setDefaultOvertimeRate(new Rate(row.getDouble("PROJ_OPT_DEF_OVT_RATE"), TimeUnit.HOURS));
      header.setUpdatingTaskStatusUpdatesResourceStatus(row.getBoolean("PROJ_OPT_TASK_UPDATES_RES"));
      header.setSplitInProgressTasks(row.getBoolean("PROJ_OPT_SPLIT_IN_PROGRESS"));
      //header.setDateOrder();
      //header.setTimeFormat();
      header.setDefaultStartTime(row.getDate("PROJ_OPT_DEF_START_TIME"));
      //header.setDateSeparator();
      //header.setTimeSeparator();
      //header.setAmText();
      //header.setPmText();
      //header.setDateFormat();
      //header.setBarTextDateFormat();
      header.setProjectTitle(row.getString("PROJ_PROP_TITLE"));
      header.setCompany(row.getString("PROJ_PROP_COMPANY"));
      header.setManager(row.getString("PROJ_PROP_MANAGER"));
      header.setCalendarName(row.getString("PROJ_INFO_CAL_NAME"));
      header.setStartDate(row.getDate("PROJ_INFO_START_DATE"));
      header.setFinishDate(row.getDate("PROJ_INFO_FINISH_DATE"));
      header.setScheduleFrom(ScheduleFrom.getInstance(1 - row.getInt("PROJ_INFO_SCHED_FROM")));
      header.setCurrentDate(row.getDate("PROJ_INFO_CURRENT_DATE"));
      //header.setComments();
      //header.setCost();
      //header.setBaselineCost();
      //header.setActualCost();
      //header.setWork();
      //header.setBaselineWork();
      //header.setActualWork();
      //header.setWork2();
      //header.setDuration();
      //header.setBaselineDuration();
      //header.setActualDuration();
      //header.setPercentageComplete();
      //header.setBaselineStart();
      //header.setBaselineFinish();
      //header.setActualStart();
      //header.setActualFinish();
      //header.setStartVariance();
      //header.setFinishVariance();
      header.setSubject(row.getString("PROJ_PROP_SUBJECT"));
      header.setAuthor(row.getString("PROJ_PROP_AUTHOR"));
      header.setKeywords(row.getString("PROJ_PROP_KEYWORDS"));
      header.setDefaultEndTime(row.getDate("PROJ_OPT_DEF_FINISH_TIME"));
      header.setProjectExternallyEdited(row.getBoolean("PROJ_EXT_EDITED_FLAG"));
      header.setCategory(row.getString("PROJ_PROP_CATEGORY"));
      header.setDaysPerMonth(row.getInteger("PROJ_OPT_DAYS_PER_MONTH"));
      header.setFiscalYearStart(row.getBoolean("PROJ_OPT_FY_USE_START_YR"));
      //header.setDefaultTaskEarnedValueMethod();
      //header.setRemoveFileProperties();
      //header.setMoveCompletedEndsBack();
      header.setNewTasksEstimated(row.getBoolean("PROJ_OPT_NEW_TASK_EST"));
      header.setSpreadActualCost(row.getBoolean("PROJ_OPT_SPREAD_ACT_COSTS"));
      header.setMultipleCriticalPaths(row.getBoolean("PROJ_OPT_MULT_CRITICAL_PATHS"));
      //header.setAutoAddNewResourcesAndTasks();
      header.setLastSaved(row.getDate("PROJ_LAST_SAVED"));
      header.setStatusDate(row.getDate("PROJ_INFO_STATUS_DATE"));
      //header.setMoveRemainingStartsBack();
      //header.setAutolink();
      //header.setMicrosoftProjectServerURL();
      header.setHonorConstraints(row.getBoolean("PROJ_OPT_HONOR_CONSTRAINTS"));
      //header.setAdminProject(row.getInt("PROJ_ADMINPROJECT")!=0); // Not in MPP9 MPD?
      //header.setInsertedProjectsLikeSummary();
      header.setName(row.getString("PROJ_NAME"));
      header.setSpreadPercentComplete(row.getBoolean("PROJ_OPT_SPREAD_PCT_COMP"));
      //header.setMoveCompletedEndsForward();
      //header.setEditableActualCosts();
      //header.setUniqueID();
      //header.setRevision();
      header.setNewTasksEffortDriven(row.getBoolean("PROJ_OPT_NEW_ARE_EFFORT_DRIVEN"));
      //header.setMoveRemainingStartsForward();
      //header.setActualsInSync(row.getInt("PROJ_ACTUALS_SYNCH") != 0); // Not in MPP9 MPD?
      header.setDefaultTaskType(TaskType.getInstance(row.getInt("PROJ_OPT_DEF_TASK_TYPE")));
      //header.setEarnedValueMethod();
      header.setCreationDate(row.getDate("PROJ_CREATION_DATE"));
      //header.setExtendedCreationDate(row.getDate("PROJ_CREATION_DATE_EX")); // Not in MPP9 MPD?
      header.setDefaultFixedCostAccrual(AccrueType.getInstance(row.getInt("PROJ_OPT_DEF_FIX_COST_ACCRUAL")));
      header.setCriticalSlackLimit(row.getInteger("PROJ_OPT_CRITICAL_SLACK_LIMIT"));
      //header.setBaselineForEarnedValue;
      header.setFiscalYearStartMonth(row.getInteger("PROJ_OPT_FY_START_MONTH"));
      //header.setNewTaskStartIsProjectStart();     
      header.setWeekStartDay(Day.getInstance(row.getInt("PROJ_OPT_WEEK_START_DAY") + 1));
      //header.setCalculateMultipleCriticalPaths();
      header.setMultipleCriticalPaths(row.getBoolean("PROJ_OPT_MULT_CRITICAL_PATHS"));

      //
      // Unused attributes
      //

      //    PROJ_OPT_CALC_ACT_COSTS
      //    PROJ_POOL_ATTACHED_TO
      //    PROJ_IS_RES_POOL
      //    PROJ_OPT_CALC_SUB_AS_SUMMARY
      //    PROJ_OPT_SHOW_EST_DUR
      //    PROJ_OPT_EXPAND_TIMEPHASED
      //    PROJ_PROJECT
      //    PROJ_VERSION
      //    PROJ_ENT_LIST_SEPARATOR
      //    PROJ_EXT_EDITED_DUR
      //    PROJ_EXT_EDITED_NUM
      //    PROJ_EXT_EDITED_FLAG
      //    PROJ_EXT_EDITED_CODE
      //    PROJ_EXT_EDITED_TEXT
      //    PROJ_IGNORE_FRONT_END
      //    PROJ_EXT_EDITED
      //    PROJ_DATA_SOURCE
      //    PROJ_READ_ONLY
      //    PROJ_READ_WRITE
      //    PROJ_READ_COUNT
      //    PROJ_LOCKED
      //    PROJ_MACHINE_ID
      //    PROJ_TYPE
      //    PROJ_CHECKEDOUT
      //    PROJ_CHECKEDOUTBY
      //    PROJ_CHECKEDOUTDATE
      //    RESERVED_BINARY_DATA      
   }

   /**
    * Read a calendar.
    * 
    * @param row calendar data
    */
   protected void processCalendar(Row row)
   {
      Integer uniqueID = row.getInteger("CAL_UID");
      if (NumberUtility.getInt(uniqueID) > 0)
      {
         boolean baseCalendar = row.getBoolean("CAL_IS_BASE_CAL");
         ProjectCalendar cal = null;
         if (baseCalendar == true)
         {
            cal = m_project.addBaseCalendar();
            cal.setName(row.getString("CAL_NAME"));
         }
         else
         {
            Integer resourceID = row.getInteger("RES_UID");
            if (resourceID != null && resourceID.intValue() != 0)
            {
               cal = m_project.addResourceCalendar();
               m_baseCalendars.add(new Pair<ProjectCalendar, Integer>(cal, row.getInteger("CAL_BASE_UID")));
               m_resourceMap.put(resourceID, cal);
            }
         }

         if (cal != null)
         {
            cal.setUniqueID(uniqueID);
            m_calendarMap.put(uniqueID, cal);
         }
      }
   }

   /**
    * Read calendar hours and exception data.
    * 
    * @param calendar parent calendar
    * @param row calendar hours and exception data
    */
   protected void processCalendarData(ProjectCalendar calendar, Row row)
   {
      int dayIndex = row.getInt("CD_DAY_OR_EXCEPTION");
      if (dayIndex == 0)
      {
         processCalendarException(calendar, row);
      }
      else
      {
         processCalendarHours(calendar, row, dayIndex);
      }
   }

   /**
    * Process a calendar exception.
    * 
    * @param calendar parent calendar
    * @param row calendar exception data
    */
   private void processCalendarException(ProjectCalendar calendar, Row row)
   {
      Date fromDate = row.getDate("CD_FROM_DATE");
      Date toDate = row.getDate("CD_TO_DATE");
      boolean working = row.getInt("CD_WORKING") != 0;
      ProjectCalendarException exception = calendar.addCalendarException(fromDate, toDate);
      if (working)
      {
         exception.addRange(new DateRange(row.getDate("CD_FROM_TIME1"), row.getDate("CD_TO_TIME1")));
         exception.addRange(new DateRange(row.getDate("CD_FROM_TIME2"), row.getDate("CD_TO_TIME2")));
         exception.addRange(new DateRange(row.getDate("CD_FROM_TIME3"), row.getDate("CD_TO_TIME3")));
         exception.addRange(new DateRange(row.getDate("CD_FROM_TIME4"), row.getDate("CD_TO_TIME4")));
         exception.addRange(new DateRange(row.getDate("CD_FROM_TIME5"), row.getDate("CD_TO_TIME5")));
      }
   }

   /**
    * Process calendar hours.
    * 
    * @param calendar parent calendar
    * @param row calendar hours data
    * @param dayIndex day index
    */
   private void processCalendarHours(ProjectCalendar calendar, Row row, int dayIndex)
   {
      Day day = Day.getInstance(dayIndex);
      boolean working = row.getInt("CD_WORKING") != 0;
      calendar.setWorkingDay(day, working);
      if (working == true)
      {
         ProjectCalendarHours hours = calendar.addCalendarHours(day);

         Date start = row.getDate("CD_FROM_TIME1");
         Date end = row.getDate("CD_TO_TIME1");
         if (start != null && end != null)
         {
            hours.addRange(new DateRange(start, end));
         }

         start = row.getDate("CD_FROM_TIME2");
         end = row.getDate("CD_TO_TIME2");
         if (start != null && end != null)
         {
            hours.addRange(new DateRange(start, end));
         }

         start = row.getDate("CD_FROM_TIME3");
         end = row.getDate("CD_TO_TIME3");
         if (start != null && end != null)
         {
            hours.addRange(new DateRange(start, end));
         }

         start = row.getDate("CD_FROM_TIME4");
         end = row.getDate("CD_TO_TIME4");
         if (start != null && end != null)
         {
            hours.addRange(new DateRange(start, end));
         }

         start = row.getDate("CD_FROM_TIME5");
         end = row.getDate("CD_TO_TIME5");
         if (start != null && end != null)
         {
            hours.addRange(new DateRange(start, end));
         }
      }
   }

   /**
    * The way calendars are stored in an MPP9 file means that there
    * can be forward references between the base calendar unique ID for a
    * derived calendar, and the base calendar itself. To get around this,
    * we initially populate the base calendar name attribute with the
    * base calendar unique ID, and now in this method we can convert those
    * ID values into the correct names.
    */
   protected void updateBaseCalendarNames()
   {
      for (Pair<ProjectCalendar, Integer> pair : m_baseCalendars)
      {
         ProjectCalendar cal = pair.getFirst();
         Integer baseCalendarID = pair.getSecond();
         ProjectCalendar baseCal = m_calendarMap.get(baseCalendarID);
         if (baseCal != null)
         {
            cal.setBaseCalendar(baseCal);
         }
      }
   }

   /**
    * Process a resource.
    * 
    * @param row resource data
    */
   protected void processResource(Row row)
   {
      Integer uniqueID = row.getInteger("RES_UID");
      if (uniqueID != null && uniqueID.intValue() >= 0)
      {
         Resource resource = m_project.addResource();
         resource.setAccrueAt(AccrueType.getInstance(row.getInt("RES_ACCRUE_AT")));
         resource.setActualCost(getDefaultOnNull(row.getCurrency("RES_ACT_COST"), NumberUtility.DOUBLE_ZERO));
         resource.setActualOvertimeCost(row.getCurrency("RES_ACT_OVT_COST"));
         resource.setActualOvertimeWork(row.getDuration("RES_ACT_OVT_WORK"));
         //resource.setActualOvertimeWorkProtected();
         resource.setActualWork(row.getDuration("RES_ACT_WORK"));
         //resource.setActualWorkProtected();
         //resource.setActveDirectoryGUID();
         resource.setACWP(row.getCurrency("RES_ACWP"));
         resource.setAvailableFrom(row.getDate("RES_AVAIL_FROM"));
         resource.setAvailableTo(row.getDate("RES_AVAIL_TO"));
         //resource.setBaseCalendar();
         resource.setBaselineCost(getDefaultOnNull(row.getCurrency("RES_BASE_COST"), NumberUtility.DOUBLE_ZERO));
         resource.setBaselineWork(row.getDuration("RES_BASE_WORK"));
         resource.setBCWP(row.getCurrency("RES_BCWP"));
         resource.setBCWS(row.getCurrency("RES_BCWS"));
         //resource.setBookingType();      
         resource.setCanLevel(row.getBoolean("RES_CAN_LEVEL"));
         //resource.setCode();
         resource.setCost(getDefaultOnNull(row.getCurrency("RES_COST"), NumberUtility.DOUBLE_ZERO));
         //resource.setCost1();
         //resource.setCost2();
         //resource.setCost3();
         //resource.setCost4();
         //resource.setCost5();
         //resource.setCost6();
         //resource.setCost7();
         //resource.setCost8();
         //resource.setCost9();
         //resource.setCost10();
         resource.setCostPerUse(row.getCurrency("RES_COST_PER_USE"));
         //resource.setCreationDate();
         //resource.setCV();      
         //resource.setDate1();
         //resource.setDate2();
         //resource.setDate3();
         //resource.setDate4();
         //resource.setDate5();
         //resource.setDate6();
         //resource.setDate7();
         //resource.setDate8();
         //resource.setDate9();
         //resource.setDate10();
         //resource.setDuration1();
         //resource.setDuration2();
         //resource.setDuration3();
         //resource.setDuration4();
         //resource.setDuration5();
         //resource.setDuration6();
         //resource.setDuration7();
         //resource.setDuration8();
         //resource.setDuration9();
         //resource.setDuration10();
         //resource.setEmailAddress();
         //resource.setFinish();
         //resource.setFinish1();
         //resource.setFinish2();
         //resource.setFinish3();
         //resource.setFinish4();
         //resource.setFinish5();
         //resource.setFinish6();
         //resource.setFinish7();
         //resource.setFinish8();
         //resource.setFinish9();
         //resource.setFinish10();
         //resource.setFlag1();
         //resource.setFlag2();
         //resource.setFlag3();
         //resource.setFlag4();
         //resource.setFlag5();
         //resource.setFlag6();
         //resource.setFlag7();
         //resource.setFlag8();
         //resource.setFlag9();
         //resource.setFlag10();
         //resource.setFlag11();
         //resource.setFlag12();
         //resource.setFlag13();
         //resource.setFlag14();
         //resource.setFlag15();
         //resource.setFlag16();
         //resource.setFlag17();
         //resource.setFlag18();
         //resource.setFlag19();
         //resource.setFlag20();
         //resource.setGroup();
         //resource.setHyperlink();
         //resource.setHyperlinkAddress();
         //resource.setHyperlinkSubAddress();
         resource.setID(row.getInteger("RES_ID"));
         resource.setInitials(row.getString("RES_INITIALS"));
         //resource.setIsEnterprise();
         //resource.setIsGeneric();
         //resource.setIsInactive();
         //resource.setIsNull();
         //resource.setLinkedFields();RES_HAS_LINKED_FIELDS = false ( java.lang.Boolean)
         resource.setMaterialLabel(row.getString("RES_MATERIAL_LABEL"));
         resource.setMaxUnits(Double.valueOf(NumberUtility.getDouble(row.getDouble("RES_MAX_UNITS")) * 100));
         resource.setName(row.getString("RES_NAME"));
         //resource.setNtAccount();
         //resource.setNumber1();
         //resource.setNumber2();
         //resource.setNumber3();
         //resource.setNumber4();
         //resource.setNumber5();
         //resource.setNumber6();
         //resource.setNumber7();
         //resource.setNumber8();
         //resource.setNumber9();
         //resource.setNumber10();
         //resource.setNumber11();
         //resource.setNumber12();
         //resource.setNumber13();
         //resource.setNumber14();
         //resource.setNumber15();
         //resource.setNumber16();
         //resource.setNumber17();
         //resource.setNumber18();
         //resource.setNumber19();
         //resource.setNumber20();
         resource.setObjects(getNullOnValue(row.getInteger("RES_NUM_OBJECTS"), 0));
         //resource.setOutlineCode1();
         //resource.setOutlineCode2();
         //resource.setOutlineCode3();
         //resource.setOutlineCode4();
         //resource.setOutlineCode5();
         //resource.setOutlineCode6();
         //resource.setOutlineCode7();
         //resource.setOutlineCode8();
         //resource.setOutlineCode9();
         //resource.setOutlineCode10();
         resource.setOverAllocated(row.getBoolean("RES_IS_OVERALLOCATED"));
         resource.setOvertimeCost(row.getCurrency("RES_OVT_COST"));
         resource.setOvertimeRate(new Rate(row.getDouble("RES_OVT_RATE"), TimeUnit.HOURS));
         resource.setOvertimeRateFormat(TimeUnit.getInstance(row.getInt("RES_OVT_RATE_FMT") - 1));
         resource.setOvertimeWork(row.getDuration("RES_OVT_WORK"));
         resource.setPeakUnits(Double.valueOf(NumberUtility.getDouble(row.getDouble("RES_PEAK")) * 100));
         //resource.setPercentWorkComplete();
         resource.setPhonetics(row.getString("RES_PHONETICS"));
         resource.setRegularWork(row.getDuration("RES_REG_WORK"));
         resource.setRemainingCost(getDefaultOnNull(row.getCurrency("RES_REM_COST"), NumberUtility.DOUBLE_ZERO));
         resource.setRemainingOvertimeCost(row.getCurrency("RES_REM_OVT_COST"));
         resource.setRemainingOvertimeWork(row.getDuration("RES_REM_OVT_WORK"));
         resource.setRemainingWork(row.getDuration("RES_REM_WORK"));
         //resource.setResourceCalendar();RES_CAL_UID = null ( ) // CHECK THIS
         resource.setStandardRate(new Rate(row.getDouble("RES_STD_RATE"), TimeUnit.HOURS));
         resource.setStandardRateFormat(TimeUnit.getInstance(row.getInt("RES_STD_RATE_FMT") - 1));
         //resource.setStart();
         //resource.setStart1();
         //resource.setStart2();
         //resource.setStart3();
         //resource.setStart4();
         //resource.setStart5();
         //resource.setStart6();
         //resource.setStart7();
         //resource.setStart8();
         //resource.setStart9();
         //resource.setStart10();
         //resource.setText1();
         //resource.setText2();
         //resource.setText3();
         //resource.setText4();
         //resource.setText5();
         //resource.setText6();
         //resource.setText7();
         //resource.setText8();
         //resource.setText9();
         //resource.setText10();
         //resource.setText11();
         //resource.setText12();
         //resource.setText13();
         //resource.setText14();
         //resource.setText15();
         //resource.setText16();
         //resource.setText17();
         //resource.setText18();
         //resource.setText19();
         //resource.setText20();
         //resource.setText21();
         //resource.setText22();
         //resource.setText23();
         //resource.setText24();
         //resource.setText25();
         //resource.setText26();
         //resource.setText27();
         //resource.setText28();
         //resource.setText29();
         //resource.setText30();
         resource.setType(row.getBoolean("RES_TYPE") ? ResourceType.WORK : ResourceType.MATERIAL);
         resource.setUniqueID(uniqueID);
         resource.setWork(row.getDuration("RES_WORK"));
         resource.setWorkGroup(WorkGroup.getInstance(row.getInt("RES_WORKGROUP_MESSAGING")));

         String notes = row.getString("RES_RTF_NOTES");
         if (notes != null)
         {
            if (m_preserveNoteFormatting == false)
            {
               notes = m_rtf.strip(notes);
            }
            resource.setNotes(notes);
         }

         resource.setResourceCalendar(m_project.getBaseCalendarByUniqueID(row.getInteger("RES_CAL_UID")));

         //
         // Calculate the cost variance
         //
         if (resource.getCost() != null && resource.getBaselineCost() != null)
         {
            resource.setCostVariance(NumberUtility.getDouble(resource.getCost().doubleValue() - resource.getBaselineCost().doubleValue()));
         }

         //
         // Calculate the work variance
         //
         if (resource.getWork() != null && resource.getBaselineWork() != null)
         {
            resource.setWorkVariance(Duration.getInstance(resource.getWork().getDuration() - resource.getBaselineWork().getDuration(), TimeUnit.HOURS));
         }

         //
         // Set the overallocated flag
         //
         resource.setOverAllocated(NumberUtility.getDouble(resource.getPeakUnits()) > NumberUtility.getDouble(resource.getMaxUnits()));

         m_project.fireResourceReadEvent(resource);

         //
         // Unused attributes
         //         
         //EXT_EDIT_REF_DATA = null ( )
         //RESERVED_DATA = null ( )             
      }
   }

   /**
    * Read a single text field extended attribute.
    * 
    * @param row field data
    */
   protected void processTextField(Row row)
   {
      processField(row, "TEXT_FIELD_ID", "TEXT_REF_UID", row.getString("TEXT_VALUE"));
   }

   /**
    * Read a single number field extended attribute.
    * 
    * @param row field data
    */
   protected void processNumberField(Row row)
   {
      processField(row, "NUM_FIELD_ID", "NUM_REF_UID", row.getDouble("NUM_VALUE"));
   }

   /**
    * Read a single flag field extended attribute.
    * 
    * @param row field data
    */
   protected void processFlagField(Row row)
   {
      processField(row, "FLAG_FIELD_ID", "FLAG_REF_UID", Boolean.valueOf(row.getBoolean("FLAG_VALUE")));
   }

   /**
    * Read a single duration field extended attribute.
    * 
    * @param row field data
    */
   protected void processDurationField(Row row)
   {
      processField(row, "DUR_FIELD_ID", "DUR_REF_UID", MPDUtility.getAdjustedDuration(m_project, row.getInt("DUR_VALUE"), MPDUtility.getDurationTimeUnits(row.getInt("DUR_FMT"))));
   }

   /**
    * Read a single date field extended attribute.
    * 
    * @param row field data
    */
   protected void processDateField(Row row)
   {
      processField(row, "DATE_FIELD_ID", "DATE_REF_UID", row.getDate("DATE_VALUE"));
   }

   /**
    * Read a single outline code field extended attribute.
    * 
    * @param entityID parent entity
    * @param row field data
    */
   protected void processOutlineCodeField(Integer entityID, Row row)
   {
      processField(row, "OC_FIELD_ID", entityID, row.getString("OC_NAME"));
   }

   /**
    * Generic method to process an extended attribute field.
    * 
    * @param row extended attribute data
    * @param fieldIDColumn column containing the field ID
    * @param entityIDColumn column containing the entity ID
    * @param value field value
    */
   protected void processField(Row row, String fieldIDColumn, String entityIDColumn, Object value)
   {
      processField(row, fieldIDColumn, row.getInteger(entityIDColumn), value);
   }

   /**
    * Generic method to process an extended attribute field.
    * 
    * @param row extended attribute data
    * @param fieldIDColumn column containing the field ID
    * @param entityID parent entity ID
    * @param value field value
    */
   protected void processField(Row row, String fieldIDColumn, Integer entityID, Object value)
   {
      int fieldID = row.getInt(fieldIDColumn);

      int prefix = fieldID & 0xFFFF0000;
      int index = fieldID & 0x0000FFFF;

      if (prefix == MPPTaskField.TASK_FIELD_BASE)
      {
         TaskField field = MPPTaskField.getInstance(index);
         if (field != null && field != TaskField.NOTES)
         {
            Task task = m_project.getTaskByUniqueID(entityID);
            if (task != null)
            {
               if (COST_FIELDS.contains(field))
               {
                  value = Double.valueOf(((Double) value).doubleValue() / 100);
               }
               task.set(field, value);
            }
         }
      }
      else
      {
         ResourceField field = MPPResourceField.getInstance(index);
         if (field != null && field != ResourceField.NOTES)
         {
            Resource resource = m_project.getResourceByUniqueID(entityID);
            if (resource != null)
            {
               if (COST_FIELDS.contains(field))
               {
                  value = Double.valueOf(((Double) value).doubleValue() / 100);
               }
               resource.set(field, value);
            }
         }
      }
   }

   /**
    * Process a task.
    * 
    * @param row task data
    */
   protected void processTask(Row row)
   {
      Integer uniqueID = row.getInteger("TASK_UID");
      if (uniqueID != null && uniqueID.intValue() >= 0)
      {
         Task task = m_project.addTask();
         TimeUnit durationFormat = MPDUtility.getDurationTimeUnits(row.getInt("TASK_DUR_FMT"));

         task.setActualCost(row.getCurrency("TASK_ACT_COST"));
         task.setActualDuration(MPDUtility.getAdjustedDuration(m_project, row.getInt("TASK_ACT_DUR"), durationFormat));
         task.setActualFinish(row.getDate("TASK_ACT_FINISH"));
         task.setActualOvertimeCost(row.getCurrency("TASK_ACT_OVT_COST"));
         task.setActualOvertimeWork(row.getDuration("TASK_ACT_OVT_WORK"));
         //task.setActualOvertimeWorkProtected();
         task.setActualStart(row.getDate("TASK_ACT_START"));
         task.setActualWork(row.getDuration("TASK_ACT_WORK"));
         //task.setActualWorkProtected();
         task.setACWP(row.getCurrency("TASK_ACWP"));
         task.setBaselineCost(row.getCurrency("TASK_BASE_COST"));
         task.setBaselineDuration(MPDUtility.getAdjustedDuration(m_project, row.getInt("TASK_BASE_DUR"), durationFormat));
         task.setBaselineFinish(row.getDate("TASK_BASE_FINISH"));
         task.setBaselineStart(row.getDate("TASK_BASE_START"));
         task.setBaselineWork(row.getDuration("TASK_BASE_WORK"));
         //task.setBCWP(row.getCurrency("TASK_BCWP")); //@todo FIXME
         //task.setBCWS(row.getCurrency("TASK_BCWS")); //@todo FIXME
         task.setCalendar(m_project.getBaseCalendarByUniqueID(row.getInteger("TASK_CAL_UID")));
         //task.setConfirmed();
         task.setConstraintDate(row.getDate("TASK_CONSTRAINT_DATE"));
         task.setConstraintType(ConstraintType.getInstance(row.getInt("TASK_CONSTRAINT_TYPE")));
         //task.setContact();
         task.setCost(row.getCurrency("TASK_COST"));
         //task.setCost1();
         //task.setCost2();
         //task.setCost3();
         //task.setCost4();      
         //task.setCost5();
         //task.setCost6();
         //task.setCost7();
         //task.setCost8();
         //task.setCost9();
         //task.setCost10();
         //task.setCostVariance();
         task.setCreateDate(row.getDate("TASK_CREATION_DATE"));
         //task.setCritical(row.getBoolean("TASK_IS_CRITICAL")); @todo FIX ME
         //task.setCV();
         //task.setDate1();
         //task.setDate2();
         //task.setDate3();
         //task.setDate4();
         //task.setDate5();
         //task.setDate6();
         //task.setDate7();
         //task.setDate8();
         //task.setDate9();
         //task.setDate10();
         task.setDeadline(row.getDate("TASK_DEADLINE"));
         //task.setDelay();
         task.setDuration(MPDUtility.getAdjustedDuration(m_project, row.getInt("TASK_DUR"), durationFormat));

         //task.setDuration1();
         //task.setDuration2();
         //task.setDuration3();
         //task.setDuration4();
         //task.setDuration5();
         //task.setDuration6();
         //task.setDuration7();
         //task.setDuration8();
         //task.setDuration9();
         //task.setDuration10();

         task.setDurationVariance(MPDUtility.getAdjustedDuration(m_project, row.getInt("TASK_DUR_VAR"), durationFormat));
         task.setEarlyFinish(row.getDate("TASK_EARLY_FINISH"));
         task.setEarlyStart(row.getDate("TASK_EARLY_START"));
         //task.setEarnedValueMethod();
         task.setEffortDriven(row.getBoolean("TASK_IS_EFFORT_DRIVEN"));
         task.setEstimated(row.getBoolean("TASK_DUR_IS_EST"));
         task.setExpanded(!row.getBoolean("TASK_IS_COLLAPSED"));
         task.setExternalTask(row.getBoolean("TASK_IS_EXTERNAL"));
         //task.setExternalTaskProject();
         task.setFinish(row.getDate("TASK_FINISH_DATE"));
         //task.setFinish1();
         //task.setFinish2();
         //task.setFinish3();
         //task.setFinish4();
         //task.setFinish5();
         //task.setFinish6();
         //task.setFinish7();
         //task.setFinish8();
         //task.setFinish9();
         //task.setFinish10();
         //task.setFinishVariance(MPDUtility.getAdjustedDuration(m_project, row.getInt("TASK_FINISH_VAR"), durationFormat)); // Calculate for consistent results?
         //task.setFixed();
         task.setFixedCost(row.getCurrency("TASK_FIXED_COST"));
         task.setFixedCostAccrual(AccrueType.getInstance(row.getInt("TASK_FIXED_COST_ACCRUAL")));
         //task.setFlag1();
         //task.setFlag2();
         //task.setFlag3();
         //task.setFlag4();
         //task.setFlag5();
         //task.setFlag6();
         //task.setFlag7();
         //task.setFlag8();
         //task.setFlag9();
         //task.setFlag10();
         //task.setFlag11();
         //task.setFlag12();
         //task.setFlag13();
         //task.setFlag14();
         //task.setFlag15();
         //task.setFlag16();
         //task.setFlag17();
         //task.setFlag18();
         //task.setFlag19();
         //task.setFlag20();
         task.setFreeSlack(row.getDuration("TASK_FREE_SLACK").convertUnits(durationFormat, m_project.getProjectHeader()));
         task.setHideBar(row.getBoolean("TASK_BAR_IS_HIDDEN"));
         //task.setHyperlink();
         //task.setHyperlinkAddress();
         //task.setHyperlinkSubAddress();
         task.setID(row.getInteger("TASK_ID"));
         task.setIgnoreResourceCalendar(row.getBoolean("TASK_IGNORES_RES_CAL"));
         task.setLateFinish(row.getDate("TASK_LATE_FINISH"));
         task.setLateStart(row.getDate("TASK_LATE_START"));
         task.setLevelAssignments(row.getBoolean("TASK_LEVELING_ADJUSTS_ASSN"));
         task.setLevelingCanSplit(row.getBoolean("TASK_LEVELING_CAN_SPLIT"));
         task.setLevelingDelayFormat(MPDUtility.getDurationTimeUnits(row.getInt("TASK_LEVELING_DELAY_FMT")));
         task.setLevelingDelay(MPDUtility.getAdjustedDuration(m_project, row.getInt("TASK_LEVELING_DELAY"), task.getLevelingDelayFormat()));
         //task.setLinkedFields(row.getBoolean("TASK_HAS_LINKED_FIELDS")); @todo FIXME
         task.setMarked(row.getBoolean("TASK_IS_MARKED"));
         task.setMilestone(row.getBoolean("TASK_IS_MILESTONE"));
         task.setName(row.getString("TASK_NAME"));
         //task.setNull();
         //task.setNumber1();
         //task.setNumber2();
         //task.setNumber3();
         //task.setNumber4();
         //task.setNumber5();
         //task.setNumber6();
         //task.setNumber7();
         //task.setNumber8();
         //task.setNumber9();
         //task.setNumber10();
         //task.setNumber11();
         //task.setNumber12();
         //task.setNumber13();
         //task.setNumber14();
         //task.setNumber15();
         //task.setNumber16();
         //task.setNumber17();
         //task.setNumber18();
         //task.setNumber19();
         //task.setNumber20();
         task.setObjects(getNullOnValue(row.getInteger("TASK_NUM_OBJECTS"), 0));
         //task.setOutlineCode1();
         //task.setOutlineCode2();
         //task.setOutlineCode3();
         //task.setOutlineCode4();
         //task.setOutlineCode5();
         //task.setOutlineCode6();
         //task.setOutlineCode7();
         //task.setOutlineCode8();
         //task.setOutlineCode9();
         //task.setOutlineCode10();
         task.setOutlineLevel(row.getInteger("TASK_OUTLINE_LEVEL"));
         task.setOutlineNumber(row.getString("TASK_OUTLINE_NUM"));
         task.setOverAllocated(row.getBoolean("TASK_IS_OVERALLOCATED"));
         task.setOvertimeCost(row.getCurrency("TASK_OVT_COST"));
         //task.setOvertimeWork();
         task.setPercentageComplete(row.getDouble("TASK_PCT_COMP"));
         task.setPercentageWorkComplete(row.getDouble("TASK_PCT_WORK_COMP"));
         //task.setPhysicalPercentComplete();
         task.setPreleveledFinish(row.getDate("TASK_PRELEVELED_FINISH"));
         task.setPreleveledStart(row.getDate("TASK_PRELEVELED_START"));
         task.setPriority(Priority.getInstance(row.getInt("TASK_PRIORITY")));
         task.setRecurring(row.getBoolean("TASK_IS_RECURRING"));
         task.setRegularWork(row.getDuration("TASK_REG_WORK"));
         task.setRemainingCost(row.getCurrency("TASK_REM_COST"));
         task.setRemainingDuration(MPDUtility.getAdjustedDuration(m_project, row.getInt("TASK_REM_DUR"), durationFormat));
         task.setRemainingOvertimeCost(row.getCurrency("TASK_REM_OVT_COST"));
         task.setRemainingOvertimeWork(row.getDuration("TASK_REM_OVT_WORK"));
         task.setRemainingWork(row.getDuration("TASK_REM_WORK"));
         //task.setResourceGroup();
         //task.setResourceInitials();
         //task.setResourceNames();
         task.setResume(row.getDate("TASK_RESUME_DATE"));
         //task.setResumeNoEarlierThan();
         //task.setResumeValid();
         task.setRollup(row.getBoolean("TASK_IS_ROLLED_UP"));
         task.setStart(row.getDate("TASK_START_DATE"));
         //task.setStart1();
         //task.setStart2();
         //task.setStart3();
         //task.setStart4();
         //task.setStart5();
         //task.setStart6();
         //task.setStart7();
         //task.setStart8();
         //task.setStart9();
         //task.setStart10();
         //task.setStartVariance(MPDUtility.getAdjustedDuration(m_project, row.getInt("TASK_START_VAR"), durationFormat)); // more accurate by calculation?
         task.setStop(row.getDate("TASK_STOP_DATE"));
         task.setSummary(row.getBoolean("TASK_IS_SUMMARY"));
         //task.setText1();
         //task.setText2();
         //task.setText3();
         //task.setText4();
         //task.setText5();
         //task.setText6();
         //task.setText7();
         //task.setText8();
         //task.setText9();
         //task.setText10();
         //task.setText11();
         //task.setText12();
         //task.setText13();
         //task.setText14();
         //task.setText15();
         //task.setText16();
         //task.setText17();
         //task.setText18();
         //task.setText19();
         //task.setText20();
         //task.setText21();
         //task.setText22();
         //task.setText23();
         //task.setText24();
         //task.setText25();
         //task.setText26();
         //task.setText27();
         //task.setText28();
         //task.setText29();
         //task.setText30();
         //task.setTotalSlack(row.getDuration("TASK_TOTAL_SLACK")); //@todo FIX ME
         task.setType(TaskType.getInstance(row.getInt("TASK_TYPE")));
         task.setUniqueID(uniqueID);
         //task.setUpdateNeeded();
         task.setWBS(row.getString("TASK_WBS"));
         //task.setWBSLevel();
         task.setWork(row.getDuration("TASK_WORK"));
         //task.setWorkVariance();

         //TASK_HAS_NOTES = false ( java.lang.Boolean)
         //TASK_RTF_NOTES = null ( )     
         String notes = row.getString("TASK_RTF_NOTES");
         if (notes != null)
         {
            if (m_preserveNoteFormatting == false)
            {
               notes = m_rtf.strip(notes);
            }
            task.setNotes(notes);
         }

         //
         // Calculate the cost variance
         //
         if (task.getCost() != null && task.getBaselineCost() != null)
         {
            task.setCostVariance(NumberUtility.getDouble(task.getCost().doubleValue() - task.getBaselineCost().doubleValue()));
         }

         //
         // Set default flag values
         //
         task.setFlag1(false);
         task.setFlag2(false);
         task.setFlag3(false);
         task.setFlag4(false);
         task.setFlag5(false);
         task.setFlag6(false);
         task.setFlag7(false);
         task.setFlag8(false);
         task.setFlag9(false);
         task.setFlag10(false);

         //
         // If we have a WBS value from the MPD file, don't autogenerate
         //
         if (task.getWBS() != null)
         {
            m_autoWBS = false;
         }

         //
         // Attempt to identify null tasks
         //
         if (task.getName() == null && task.getStart() == null && task.getFinish() == null)
         {
            task.setNull(true);
         }
      }
   }

   /**
    * Process a relationship between two tasks.
    * 
    * @param row relationship data
    */
   protected void processLink(Row row)
   {
      Task predecessorTask = m_project.getTaskByUniqueID(row.getInteger("LINK_PRED_UID"));
      Task successorTask = m_project.getTaskByUniqueID(row.getInteger("LINK_SUCC_UID"));
      if (predecessorTask != null && successorTask != null)
      {
         RelationType type = RelationType.getInstance(row.getInt("LINK_TYPE"));
         TimeUnit durationUnits = MPDUtility.getDurationTimeUnits(row.getInt("LINK_LAG_FMT"));
         Duration duration = MPDUtility.getDuration(row.getDouble("LINK_LAG").doubleValue(), durationUnits);
         successorTask.addPredecessor(predecessorTask, type, duration);
      }
   }

   /**
    * Process a resource assignment.
    * 
    * @param row resource assignment data
    */
   protected void processAssignment(Row row)
   {
      Resource resource = m_project.getResourceByUniqueID(row.getInteger("RES_UID"));
      Task task = m_project.getTaskByUniqueID(row.getInteger("TASK_UID"));

      if (task != null && resource != null)
      {
         ResourceAssignment assignment = task.addResourceAssignment(resource);
         assignment.setActualCost(row.getCurrency("ASSN_ACT_COST"));
         assignment.setActualFinish(row.getDate("ASSN_ACT_FINISH"));
         assignment.setActualStart(row.getDate("ASSN_ACT_START"));
         assignment.setActualWork(row.getDuration("ASSN_ACT_WORK"));
         assignment.setCost(row.getCurrency("ASSN_COST"));
         assignment.setDelay(row.getDuration("ASSN_DELAY"));
         assignment.setFinish(row.getDate("ASSN_FINISH_DATE"));
         assignment.setLevelingDelay(MPDUtility.getAdjustedDuration(m_project, row.getInt("ASSN_LEVELING_DELAY"), MPDUtility.getDurationTimeUnits(row.getInt("ASSN_DELAY_FMT"))));
         assignment.setOvertimeWork(row.getDuration("ASSN_OVT_WORK"));
         assignment.setRemainingWork(row.getDuration("ASSN_REM_WORK"));
         assignment.setStart(row.getDate("ASSN_START_DATE"));
         assignment.setUnits(Double.valueOf(row.getDouble("ASSN_UNITS").doubleValue() * 100.0d));
         assignment.setWork(row.getDuration("ASSN_WORK"));
         assignment.setWorkContour(WorkContour.getInstance(row.getInt("ASSN_WORK_CONTOUR")));
         assignment.setBaselineCost(row.getCurrency("ASSN_BASE_COST"));
         assignment.setBaselineFinish(row.getDate("ASSN_BASE_FINISH"));
         assignment.setBaselineStart(row.getDate("ASSN_BASE_START"));
         assignment.setBaselineWork(row.getDuration("ASSN_BASE_WORK"));
      }
   }

   /**
    * Carry out any post-processing required to tidy up
    * the data read from the database.
    */
   protected void postProcessing()
   {
      //
      // Update the internal structure. We'll take this opportunity to
      // generate outline numbers for the tasks as they don't appear to
      // be present in the MPP file.
      //
      m_project.setAutoWBS(m_autoWBS);
      m_project.setAutoOutlineNumber(true);
      m_project.updateStructure();
      m_project.setAutoOutlineNumber(false);

      //
      // Perform post-processing to set the summary flag
      //
      for (Task task : m_project.getAllTasks())
      {
         task.setSummary(task.getChildTasks().size() != 0);
      }

      //
      // Ensure that the unique ID counters are correct
      //
      m_project.updateUniqueCounters();
   }

   /**
    * This method returns the value it is passed, or null if the value
    * matches the nullValue argument.
    * 
    * @param value value under test
    * @param nullValue return null if value under test matches this value
    * @return value or null
    */
   //   private Duration getNullOnValue (Duration value, Duration nullValue)
   //   {      
   //      return (value.equals(nullValue)?null:value);
   //   }
   /**
    * This method returns the value it is passed, or null if the value
    * matches the nullValue argument.
    * 
    * @param value value under test
    * @param nullValue return null if value under test matches this value
    * @return value or null
    */
   private Integer getNullOnValue(Integer value, int nullValue)
   {
      return (NumberUtility.getInt(value) == nullValue ? null : value);
   }

   /**
    * Returns a default value if a null value is found.
    * 
    * @param value value under test
    * @param defaultValue default if value is null
    * @return value
    */
   public Double getDefaultOnNull(Double value, Double defaultValue)
   {
      return (value == null ? defaultValue : value);
   }

   /**
    * Returns a default value if a null value is found.
    * 
    * @param value value under test
    * @param defaultValue default if value is null
    * @return value
    */
   public Integer getDefaultOnNull(Integer value, Integer defaultValue)
   {
      return (value == null ? defaultValue : value);
   }

   /**
    * Sets the ID of the project to be read.
    * 
    * @param projectID project ID
    */
   public void setProjectID(Integer projectID)
   {
      m_projectID = projectID;
   }

   /**
    * This method sets a flag to indicate whether the RTF formatting associated
    * with notes should be preserved or removed. By default the formatting
    * is removed.
    *
    * @param preserveNoteFormatting boolean flag
    */
   public void setPreserveNoteFormatting(boolean preserveNoteFormatting)
   {
      m_preserveNoteFormatting = preserveNoteFormatting;
   }

   protected Integer m_projectID;
   private boolean m_preserveNoteFormatting;
   protected ProjectFile m_project;
   private Map<Integer, ProjectCalendar> m_calendarMap = new HashMap<Integer, ProjectCalendar>();
   private List<Pair<ProjectCalendar, Integer>> m_baseCalendars = new LinkedList<Pair<ProjectCalendar, Integer>>();
   private Map<Integer, ProjectCalendar> m_resourceMap = new HashMap<Integer, ProjectCalendar>();
   private RTFUtility m_rtf = new RTFUtility();
   private boolean m_autoWBS = true;

   //private static final Duration ZERO_DURATION = Duration.getInstance(0, TimeUnit.HOURS);

   private static final Set<FieldType> COST_FIELDS = new HashSet<FieldType>();
   static
   {
      COST_FIELDS.add(TaskField.COST1);
      COST_FIELDS.add(TaskField.COST2);
      COST_FIELDS.add(TaskField.COST3);
      COST_FIELDS.add(TaskField.COST4);
      COST_FIELDS.add(TaskField.COST5);
      COST_FIELDS.add(TaskField.COST6);
      COST_FIELDS.add(TaskField.COST7);
      COST_FIELDS.add(TaskField.COST8);
      COST_FIELDS.add(TaskField.COST9);
      COST_FIELDS.add(TaskField.COST10);
      COST_FIELDS.add(ResourceField.COST1);
      COST_FIELDS.add(ResourceField.COST2);
      COST_FIELDS.add(ResourceField.COST3);
      COST_FIELDS.add(ResourceField.COST4);
      COST_FIELDS.add(ResourceField.COST5);
      COST_FIELDS.add(ResourceField.COST6);
      COST_FIELDS.add(ResourceField.COST7);
      COST_FIELDS.add(ResourceField.COST8);
      COST_FIELDS.add(ResourceField.COST9);
      COST_FIELDS.add(ResourceField.COST10);
   }
}

/*
TASK_VAC = 0.0 ( java.lang.Double)
EXT_EDIT_REF_DATA = null ( )
TASK_IS_SUBPROJ = false ( java.lang.Boolean)
TASK_IS_FROM_FINISH_SUBPROJ = false ( java.lang.Boolean)
TASK_IS_RECURRING_SUMMARY = false ( java.lang.Boolean)
TASK_IS_READONLY_SUBPROJ = false ( java.lang.Boolean)
TASK_BASE_DUR_FMT = 39 ( java.lang.Short)
TASK_WBS_RIGHTMOST_LEVEL = null ( )
*/

