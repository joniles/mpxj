/*
 * file:       ProjectProperties.java
 * author:     Jon Iles
 *             Scott Melville
 * copyright:  (c) Packwood Software 2002-2015
 * date:       15/08/2002
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

package org.mpxj;

import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Collections;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.function.Function;

import org.mpxj.common.BooleanHelper;
import org.mpxj.common.NumberHelper;
import org.mpxj.common.PopulatedFields;
import org.mpxj.common.ProjectFieldLists;

/**
 * This class represents a collection of properties relevant to the whole project.
 */
public final class ProjectProperties extends AbstractFieldContainer<ProjectProperties> implements TimeUnitDefaultsContainer
{
   /**
    * Default constructor.
    *
    * @param file the parent file to which this record belongs.
    */
   ProjectProperties(ProjectFile file)
   {
      super(file);

      //
      // Configure MPX File Creation Record Settings
      //
      setMpxDelimiter(DEFAULT_MPX_DELIMITER);
      setMpxProgramName("Microsoft Project for Windows");
      setMpxFileVersion(FileVersion.VERSION_4_0);
      setMpxCodePage(CodePage.ANSI);

      //
      // Configure MPX Date Time Settings and Currency Settings Records
      //
      setCurrencySymbol(DEFAULT_CURRENCY_SYMBOL);
      setSymbolPosition(DEFAULT_CURRENCY_SYMBOL_POSITION);
      setCurrencyDigits(DEFAULT_CURRENCY_DIGITS);
      setThousandsSeparator(DEFAULT_THOUSANDS_SEPARATOR);
      setDecimalSeparator(DEFAULT_DECIMAL_SEPARATOR);

      setDateOrder(DateOrder.DMY);
      setTimeFormat(ProjectTimeFormat.TWELVE_HOUR);
      setDefaultStartTime(LocalTime.of(8, 0));
      setDateSeparator(DEFAULT_DATE_SEPARATOR);
      setTimeSeparator(DEFAULT_TIME_SEPARATOR);
      setAMText("am");
      setPMText("pm");
      setDateFormat(ProjectDateFormat.DD_MM_YYYY);
      setBarTextDateFormat(ProjectDateFormat.DD_MM_YYYY);

      //
      // Configure MPX Default Settings Record
      //
      setDefaultDurationUnits(TimeUnit.DAYS);
      setDefaultDurationIsFixed(false);
      setDefaultWorkUnits(TimeUnit.HOURS);
      setMinutesPerDay(DEFAULT_MINUTES_PER_DAY);
      setMinutesPerWeek(DEFAULT_MINUTES_PER_WEEK);
      setDefaultStandardRate(new Rate(10, TimeUnit.HOURS));
      setDefaultOvertimeRate(new Rate(15, TimeUnit.HOURS));
      setUpdatingTaskStatusUpdatesResourceStatus(true);
      setSplitInProgressTasks(false);

      //
      // Configure MPX Project Header Record
      //
      setProjectTitle("Project1");
      setCompany(null);
      setManager(null);
      setStartDate(null);
      setFinishDate(null);
      setScheduleFrom(DEFAULT_SCHEDULE_FROM);
      setCurrentDate(LocalDateTime.now());
      setComments(null);
      setCost(DEFAULT_COST);
      setBaselineCost(DEFAULT_COST);
      setActualCost(DEFAULT_COST);
      setWork(DEFAULT_WORK);
      setBaselineWork(DEFAULT_WORK);
      setActualWork(DEFAULT_WORK);
      setWork2(DEFAULT_WORK2);
      setDuration(DEFAULT_DURATION);
      setBaselineDuration(DEFAULT_DURATION);
      setActualDuration(DEFAULT_DURATION);
      setPercentageComplete(DEFAULT_PERCENT_COMPLETE);
      setBaselineStart(null);
      setBaselineFinish(null);
      setActualStart(null);
      setActualFinish(null);
      setStartVariance(DEFAULT_DURATION);
      setFinishVariance(DEFAULT_DURATION);
      setSubject(null);
      setAuthor(null);
      setKeywords(null);

      //
      // Configure non-MPX attributes
      //
      setProjectExternallyEdited(false);
      setMinutesPerDay(DEFAULT_MINUTES_PER_DAY);
      setDaysPerMonth(DEFAULT_DAYS_PER_MONTH);
      setMinutesPerWeek(DEFAULT_MINUTES_PER_WEEK);
      setFiscalYearStart(false);
      setDefaultTaskEarnedValueMethod(EarnedValueMethod.PERCENT_COMPLETE);
      setNewTasksEstimated(true);
      setAutoAddNewResourcesAndTasks(true);
      setAutolink(true);
      setMicrosoftProjectServerURL(true);
      setDefaultTaskType(TaskType.FIXED_UNITS);
      setDefaultFixedCostAccrual(AccrueType.END);
      setCriticalSlackLimit(DEFAULT_CRITICAL_SLACK_LIMIT);
      setBaselineForEarnedValue(DEFAULT_BASELINE_FOR_EARNED_VALUE);
      setFiscalYearStartMonth(DEFAULT_FISCAL_YEAR_START_MONTH);
      setNewTaskStartIsProjectStart(true);
      setNewTasksAreManual(true);
      setWeekStartDay(DEFAULT_WEEK_START_DAY);
      setCriticalActivityType(CriticalActivityType.TOTAL_FLOAT);
      setTotalSlackCalculationType(TotalSlackCalculationType.SMALLEST_SLACK);
      setRelationshipLagCalendar(RelationshipLagCalendar.PREDECESSOR);
   }

   /**
    * Gets Default Duration units. The constants used to define the
    * duration units are defined by the {@code TimeUnit} class.
    *
    * @return default duration units
    * @see TimeUnit
    */
   public TimeUnit getDefaultDurationUnits()
   {
      return (TimeUnit) get(ProjectField.DEFAULT_DURATION_UNITS);
   }

   /**
    * Default duration units. The constants used to define the
    * duration units are defined by the {@code TimeUnit} class.
    *
    * @param units default duration units
    * @see TimeUnit
    */
   public void setDefaultDurationUnits(TimeUnit units)
   {
      set(ProjectField.DEFAULT_DURATION_UNITS, units);
   }

   /**
    * Retrieves a flag indicating if the default duration type is fixed.
    *
    * @return boolean flag
    */
   public boolean getDefaultDurationIsFixed()
   {
      return BooleanHelper.getBoolean((Boolean) get(ProjectField.DEFAULT_DURATION_IS_FIXED));
   }

   /**
    * Sets a flag indicating if the default duration type is fixed.
    *
    * @param fixed boolean flag
    */
   public void setDefaultDurationIsFixed(boolean fixed)
   {
      set(ProjectField.DEFAULT_DURATION_IS_FIXED, fixed);
   }

   /**
    * Default work units. The constants used to define the
    * work units are defined by the {@code TimeUnit} class.
    *
    * @return default work units
    * @see TimeUnit
    */
   public TimeUnit getDefaultWorkUnits()
   {
      return (TimeUnit) get(ProjectField.DEFAULT_WORK_UNITS);
   }

   /**
    * Default work units. The constants used to define the
    * work units are defined by the {@code TimeUnit} class.
    *
    * @param units  default work units
    * @see TimeUnit
    */
   public void setDefaultWorkUnits(TimeUnit units)
   {
      set(ProjectField.DEFAULT_WORK_UNITS, units);
   }

   /**
    * Retrieves the default standard rate.
    *
    * @return default standard rate
    */
   public Rate getDefaultStandardRate()
   {
      return (Rate) get(ProjectField.DEFAULT_STANDARD_RATE);
   }

   /**
    * Sets the default standard rate.
    *
    * @param rate default standard rate
    */
   public void setDefaultStandardRate(Rate rate)
   {
      set(ProjectField.DEFAULT_STANDARD_RATE, rate);
   }

   /**
    * Get overtime rate.
    *
    * @return rate
    */
   public Rate getDefaultOvertimeRate()
   {
      return (Rate) get(ProjectField.DEFAULT_OVERTIME_RATE);
   }

   /**
    * Set default overtime rate.
    *
    * @param rate default overtime rate
    */
   public void setDefaultOvertimeRate(Rate rate)
   {
      set(ProjectField.DEFAULT_OVERTIME_RATE, rate);
   }

   /**
    * Flags whether updating Task status also updates resource status.
    *
    * @return boolean flag
    */
   public boolean getUpdatingTaskStatusUpdatesResourceStatus()
   {
      return BooleanHelper.getBoolean((Boolean) get(ProjectField.UPDATING_TASK_STATUS_UPDATES_RESOURCE_STATUS));
   }

   /**
    * Flags whether updating Task status also updates resource status.
    *
    * @param flag boolean flag
    */
   public void setUpdatingTaskStatusUpdatesResourceStatus(boolean flag)
   {
      set(ProjectField.UPDATING_TASK_STATUS_UPDATES_RESOURCE_STATUS, flag);
   }

   /**
    * Flag representing whether to split in-progress tasks.
    *
    * @return Boolean value
    */
   public boolean getSplitInProgressTasks()
   {
      return BooleanHelper.getBoolean((Boolean) get(ProjectField.SPLIT_IN_PROGRESS_TASKS));
   }

   /**
    * Flag representing whether to split in-progress tasks.
    *
    * @param flag boolean value
    */
   public void setSplitInProgressTasks(boolean flag)
   {
      set(ProjectField.SPLIT_IN_PROGRESS_TASKS, flag);
   }

   /**
    * Gets constant representing set Date order eg DMY, MDY.
    *
    * @return constant value for date order
    */
   public DateOrder getDateOrder()
   {
      return (DateOrder) get(ProjectField.DATE_ORDER);
   }

   /**
    * Sets constant representing set Date order eg DMY, MDY.
    *
    * @param dateOrder date order value
    */
   public void setDateOrder(DateOrder dateOrder)
   {
      set(ProjectField.DATE_ORDER, dateOrder);
   }

   /**
    * Gets constant representing the Time Format.
    *
    * @return time format constant
    */
   public ProjectTimeFormat getTimeFormat()
   {
      return (ProjectTimeFormat) get(ProjectField.TIME_FORMAT);
   }

   /**
    * Sets constant representing the time format.
    *
    * @param timeFormat constant value
    */
   public void setTimeFormat(ProjectTimeFormat timeFormat)
   {
      set(ProjectField.TIME_FORMAT, timeFormat);
   }

   /**
    * Retrieve the default start time, specified using the Java Date type.
    * Note that this assumes that the value returned from
    * the getTime method starts at zero... i.e. the date part
    * of the date/time value has not been set.
    *
    * @return default start time
    */
   public LocalTime getDefaultStartTime()
   {
      return (LocalTime) get(ProjectField.DEFAULT_START_TIME);
   }

   /**
    * Set the default start time, specified using the Java Date type.
    * Note that this assumes that the value returned from
    * the getTime method starts at zero... i.e. the date part
    * of the date/time value has not been set.
    *
    * @param defaultStartTime default time
    */
   public void setDefaultStartTime(LocalTime defaultStartTime)
   {
      set(ProjectField.DEFAULT_START_TIME, defaultStartTime);
   }

   /**
    * Gets the date separator.
    *
    * @return date separator as set.
    */
   public char getDateSeparator()
   {
      return ((Character) get(ProjectField.DATE_SEPARATOR)).charValue();
   }

   /**
    * Sets the date separator.
    *
    * @param dateSeparator date separator as set.
    */
   public void setDateSeparator(char dateSeparator)
   {
      set(ProjectField.DATE_SEPARATOR, Character.valueOf(dateSeparator));
   }

   /**
    * Gets the time separator.
    *
    * @return time separator as set.
    */
   public char getTimeSeparator()
   {
      return ((Character) get(ProjectField.TIME_SEPARATOR)).charValue();
   }

   /**
    * Sets the time separator.
    *
    * @param timeSeparator time separator
    */
   public void setTimeSeparator(char timeSeparator)
   {
      set(ProjectField.TIME_SEPARATOR, Character.valueOf(timeSeparator));
   }

   /**
    * Gets the AM text.
    *
    * @return AM Text as set.
    */
   public String getAMText()
   {
      return (String) get(ProjectField.AM_TEXT);
   }

   /**
    * Sets the AM text.
    *
    * @param amText AM Text as set.
    */
   public void setAMText(String amText)
   {
      set(ProjectField.AM_TEXT, amText);
   }

   /**
    * Gets the PM text.
    *
    * @return PM Text as set.
    */
   public String getPMText()
   {
      return (String) get(ProjectField.PM_TEXT);
   }

   /**
    * Sets the PM text.
    *
    * @param pmText PM Text as set.
    */
   public void setPMText(String pmText)
   {
      set(ProjectField.PM_TEXT, pmText);
   }

   /**
    * Gets the set Date Format.
    *
    * @return project date format
    */
   public ProjectDateFormat getDateFormat()
   {
      return (ProjectDateFormat) get(ProjectField.DATE_FORMAT);
   }

   /**
    * Sets the set Date Format.
    *
    * @param dateFormat int representing Date Format
    */
   public void setDateFormat(ProjectDateFormat dateFormat)
   {
      set(ProjectField.DATE_FORMAT, dateFormat);
   }

   /**
    * Gets Bar Text Date Format.
    *
    * @return int value
    */
   public ProjectDateFormat getBarTextDateFormat()
   {
      return (ProjectDateFormat) get(ProjectField.BAR_TEXT_DATE_FORMAT);
   }

   /**
    * Sets Bar Text Date Format.
    *
    * @param dateFormat value to be set
    */
   public void setBarTextDateFormat(ProjectDateFormat dateFormat)
   {
      set(ProjectField.BAR_TEXT_DATE_FORMAT, dateFormat);
   }

   /**
    * Retrieves the default end time.
    *
    * @return End time
    */
   public LocalTime getDefaultEndTime()
   {
      return (LocalTime) get(ProjectField.DEFAULT_END_TIME);
   }

   /**
    * Sets the default end time.
    *
    * @param date End time
    */
   public void setDefaultEndTime(LocalTime date)
   {
      set(ProjectField.DEFAULT_END_TIME, date);
   }

   /**
    * Sets the project title.
    *
    * @param projectTitle project title
    */
   public void setProjectTitle(String projectTitle)
   {
      set(ProjectField.PROJECT_TITLE, projectTitle);
   }

   /**
    * Gets the project title.
    *
    * @return project title
    */
   public String getProjectTitle()
   {
      return (String) get(ProjectField.PROJECT_TITLE);
   }

   /**
    * Sets the company name.
    *
    * @param company company name
    */
   public void setCompany(String company)
   {
      set(ProjectField.COMPANY, company);
   }

   /**
    * Retrieves the company name.
    *
    * @return company name
    */
   public String getCompany()
   {
      return (String) get(ProjectField.COMPANY);
   }

   /**
    * Sets the manager name.
    *
    * @param manager manager name
    */
   public void setManager(String manager)
   {
      set(ProjectField.MANAGER, manager);
   }

   /**
    * Retrieves the manager name.
    *
    * @return manager name
    */
   public String getManager()
   {
      return (String) get(ProjectField.MANAGER);
   }

   /**
    * Set the default calendar unique ID for this project.
    *
    * @param id default calendar unique ID
    */
   public void setDefaultCalendarUniqueID(Integer id)
   {
      set(ProjectField.DEFAULT_CALENDAR_UNIQUE_ID, id);
   }

   /**
    * Retrieve the default calendar unique ID for this project.
    *
    * @return default calendar unique ID
    */
   public Integer getDefaultCalendarUniqueID()
   {
      return (Integer) get(ProjectField.DEFAULT_CALENDAR_UNIQUE_ID);
   }

   /**
    * Set the default calendar for this project.
    *
    * @param calendar default calendar
    */
   public void setDefaultCalendar(ProjectCalendar calendar)
   {
      set(ProjectField.DEFAULT_CALENDAR_UNIQUE_ID, calendar.getUniqueID());
   }

   /**
    * Retrieve the default calendar for this project.
    *
    * @return default calendar
    */
   public ProjectCalendar getDefaultCalendar()
   {
      return getParentFile().getCalendars().getByUniqueID((Integer) get(ProjectField.DEFAULT_CALENDAR_UNIQUE_ID));
   }

   /**
    * Sets the project start date.
    *
    * @param startDate project start date
    */
   public void setStartDate(LocalDateTime startDate)
   {
      set(ProjectField.START_DATE, startDate);
   }

   /**
    * Retrieves the project start date. If an explicit start date has not been
    * set, we fall back on the earliest start date in the file.
    *
    * @return project start date
    */
   public LocalDateTime getStartDate()
   {
      return (LocalDateTime) get(ProjectField.START_DATE);
   }

   /**
    * Retrieves the project finish date. If an explicit finish date has not been set we
    * fall back on the latest task finish date in the file.
    *
    * @return project finish date
    */
   public LocalDateTime getFinishDate()
   {
      return (LocalDateTime) get(ProjectField.FINISH_DATE);
   }

   /**
    * Sets the project finish date.
    *
    * @param finishDate project finish date
    */
   public void setFinishDate(LocalDateTime finishDate)
   {
      set(ProjectField.FINISH_DATE, finishDate);
   }

   /**
    * Retrieves an enumerated value indicating if tasks in this project are
    * scheduled from a start or a finish date.
    *
    * @return schedule from flag
    */
   public ScheduleFrom getScheduleFrom()
   {
      return (ScheduleFrom) get(ProjectField.SCHEDULE_FROM);
   }

   /**
    * Sets an enumerated value indicating if tasks in this project are
    * scheduled from a start or a finish date.
    *
    * @param scheduleFrom schedule from value
    */
   public void setScheduleFrom(ScheduleFrom scheduleFrom)
   {
      set(ProjectField.SCHEDULE_FROM, scheduleFrom);
   }

   /**
    * Retrieves the current date.
    *
    * @return current date
    */
   public LocalDateTime getCurrentDate()
   {
      return (LocalDateTime) get(ProjectField.CURRENT_DATE);
   }

   /**
    * Sets the current date.
    *
    * @param currentDate current date
    */
   public void setCurrentDate(LocalDateTime currentDate)
   {
      set(ProjectField.CURRENT_DATE, currentDate);
   }

   /**
    * Returns any comments.
    *
    * @return comments
    */
   public String getComments()
   {
      return (String) get(ProjectField.COMMENTS);
   }

   /**
    * Set comment text.
    *
    * @param comments comment text
    */
   public void setComments(String comments)
   {
      set(ProjectField.COMMENTS, comments);
   }

   /**
    * Retrieves the project cost.
    *
    * @return project cost
    */
   public Number getCost()
   {
      return (Number) get(ProjectField.COST);
   }

   /**
    * Sets the project cost.
    *
    * @param cost project cost
    */
   public void setCost(Number cost)
   {
      set(ProjectField.COST, cost);
   }

   /**
    * Sets the baseline project cost.
    *
    * @param baselineCost baseline project cost
    */
   public void setBaselineCost(Number baselineCost)
   {
      set(ProjectField.BASELINE_COST, baselineCost);
   }

   /**
    * Retrieves the baseline project cost.
    *
    * @return baseline project cost
    */
   public Number getBaselineCost()
   {
      return (Number) get(ProjectField.BASELINE_COST);
   }

   /**
    * Sets the actual project cost.
    *
    * @param actualCost actual project cost
    */
   public void setActualCost(Number actualCost)
   {
      set(ProjectField.ACTUAL_COST, actualCost);
   }

   /**
    * Retrieves the actual project cost.
    *
    * @return actual project cost
    */
   public Number getActualCost()
   {
      return (Number) get(ProjectField.ACTUAL_COST);
   }

   /**
    * Sets the project work duration.
    *
    * @param work project work duration
    */
   public void setWork(Duration work)
   {
      set(ProjectField.WORK, work);
   }

   /**
    * Retrieves the project work duration.
    *
    * @return project work duration
    */
   public Duration getWork()
   {
      return (Duration) get(ProjectField.WORK);
   }

   /**
    * Set the baseline project work duration.
    *
    * @param baselineWork baseline project work duration
    */
   public void setBaselineWork(Duration baselineWork)
   {
      set(ProjectField.BASELINE_WORK, baselineWork);
   }

   /**
    * Retrieves the baseline project work duration.
    *
    * @return baseline project work duration
    */
   public Duration getBaselineWork()
   {
      return (Duration) get(ProjectField.BASELINE_WORK);
   }

   /**
    * Sets the actual project work duration.
    *
    * @param actualWork actual project work duration
    */
   public void setActualWork(Duration actualWork)
   {
      set(ProjectField.ACTUAL_WORK, actualWork);
   }

   /**
    * Retrieves the actual project work duration.
    *
    * @return actual project work duration
    */
   public Duration getActualWork()
   {
      return (Duration) get(ProjectField.ACTUAL_WORK);
   }

   /**
    * Retrieves the project's "Work 2" attribute.
    *
    * @return Work 2 attribute
    */
   public Number getWork2()
   {
      return (Number) get(ProjectField.WORK2);
   }

   /**
    * Sets the project's "Work 2" attribute.
    *
    * @param work2 work2 percentage value
    */
   public void setWork2(Number work2)
   {
      set(ProjectField.WORK2, work2);
   }

   /**
    * Retrieves the project duration.
    *
    * @return project duration
    */
   public Duration getDuration()
   {
      return (Duration) get(ProjectField.DURATION);
   }

   /**
    * Sets the project duration.
    *
    * @param duration project duration
    */
   public void setDuration(Duration duration)
   {
      set(ProjectField.DURATION, duration);
   }

   /**
    * Retrieves the baseline duration value.
    *
    * @return baseline project duration value
    */
   public Duration getBaselineDuration()
   {
      return (Duration) get(ProjectField.BASELINE_DURATION);
   }

   /**
    * Sets the baseline project duration value.
    *
    * @param baselineDuration baseline project duration
    */
   public void setBaselineDuration(Duration baselineDuration)
   {
      set(ProjectField.BASELINE_DURATION, baselineDuration);
   }

   /**
    * Retrieves the actual project duration.
    *
    * @return actual project duration
    */
   public Duration getActualDuration()
   {
      return (Duration) get(ProjectField.ACTUAL_DURATION);
   }

   /**
    * Sets the actual project duration.
    *
    * @param actualDuration actual project duration
    */
   public void setActualDuration(Duration actualDuration)
   {
      set(ProjectField.ACTUAL_DURATION, actualDuration);
   }

   /**
    * Retrieves the project percentage complete.
    *
    * @return percentage value
    */
   public Number getPercentageComplete()
   {
      return (Number) get(ProjectField.PERCENTAGE_COMPLETE);
   }

   /**
    * Sets project percentage complete.
    *
    * @param percentComplete project percent complete
    */
   public void setPercentageComplete(Number percentComplete)
   {
      set(ProjectField.PERCENTAGE_COMPLETE, percentComplete);
   }

   /**
    * Sets the baseline project start date.
    *
    * @param baselineStartDate baseline project start date
    */
   public void setBaselineStart(LocalDateTime baselineStartDate)
   {
      set(ProjectField.BASELINE_START, baselineStartDate);
   }

   /**
    * Retrieves the baseline project start date.
    *
    * @return baseline project start date
    */
   public LocalDateTime getBaselineStart()
   {
      return (LocalDateTime) get(ProjectField.BASELINE_START);
   }

   /**
    * Sets the baseline project finish date.
    *
    * @param baselineFinishDate baseline project finish date
    */
   public void setBaselineFinish(LocalDateTime baselineFinishDate)
   {
      set(ProjectField.BASELINE_FINISH, baselineFinishDate);
   }

   /**
    * Retrieves the baseline project finish date.
    *
    * @return baseline project finish date
    */
   public LocalDateTime getBaselineFinish()
   {
      return (LocalDateTime) get(ProjectField.BASELINE_FINISH);
   }

   /**
    * Sets the actual project start date.
    *
    * @param actualStartDate actual project start date
    */
   public void setActualStart(LocalDateTime actualStartDate)
   {
      set(ProjectField.ACTUAL_START, actualStartDate);
   }

   /**
    * Retrieves the actual project start date.
    *
    * @return actual project start date
    */
   public LocalDateTime getActualStart()
   {
      return (LocalDateTime) get(ProjectField.ACTUAL_START);
   }

   /**
    * Sets the actual project finish date.
    *
    * @param actualFinishDate actual project finish date
    */
   public void setActualFinish(LocalDateTime actualFinishDate)
   {
      set(ProjectField.ACTUAL_FINISH, actualFinishDate);
   }

   /**
    * Retrieves the actual project finish date.
    *
    * @return actual project finish date
    */
   public LocalDateTime getActualFinish()
   {
      return (LocalDateTime) get(ProjectField.ACTUAL_FINISH);
   }

   /**
    * Retrieves the start variance duration.
    *
    * @return start date variance
    */
   public Duration getStartVariance()
   {
      return (Duration) get(ProjectField.START_VARIANCE);
   }

   /**
    * Sets the start variance duration.
    *
    * @param startVariance the start date variance
    */
   public void setStartVariance(Duration startVariance)
   {
      set(ProjectField.START_VARIANCE, startVariance);
   }

   /**
    * Retrieves the project finish variance duration.
    *
    * @return project finish variance duration
    */
   public Duration getFinishVariance()
   {
      return (Duration) get(ProjectField.FINISH_VARIANCE);
   }

   /**
    * Sets the project finish variance duration.
    *
    * @param finishVariance project finish variance duration
    */
   public void setFinishVariance(Duration finishVariance)
   {
      set(ProjectField.FINISH_VARIANCE, finishVariance);
   }

   /**
    * Returns the project subject text.
    *
    * @return subject text
    */
   public String getSubject()
   {
      return (String) get(ProjectField.SUBJECT);
   }

   /**
    * Sets the project subject text.
    *
    * @param subject subject text
    */
   public void setSubject(String subject)
   {
      set(ProjectField.SUBJECT, subject);
   }

   /**
    * Retrieves the project author text.
    *
    * @return author text
    */
   public String getAuthor()
   {
      return (String) get(ProjectField.AUTHOR);
   }

   /**
    * Sets the project author text.
    *
    * @param author project author text
    */
   public void setAuthor(String author)
   {
      set(ProjectField.AUTHOR, author);
   }

   /**
    * Retrieves the project keyword text.
    *
    * @return project keyword text
    */
   public String getKeywords()
   {
      return (String) get(ProjectField.KEYWORDS);
   }

   /**
    * Sets the project keyword text.
    *
    * @param keywords project keyword text
    */
   public void setKeywords(String keywords)
   {
      set(ProjectField.KEYWORDS, keywords);
   }

   /**
    * Sets currency symbol.
    *
    * @param symbol currency symbol
    */
   public void setCurrencySymbol(String symbol)
   {
      if (symbol == null)
      {
         symbol = DEFAULT_CURRENCY_SYMBOL;
      }

      set(ProjectField.CURRENCY_SYMBOL, symbol);
   }

   /**
    * Retrieves the currency symbol.
    *
    * @return currency symbol
    */
   public String getCurrencySymbol()
   {
      return (String) get(ProjectField.CURRENCY_SYMBOL);
   }

   /**
    * Sets the position of the currency symbol.
    *
    * @param value currency symbol position.
    */
   public void setSymbolPosition(CurrencySymbolPosition value)
   {
      if (value == null)
      {
         value = DEFAULT_CURRENCY_SYMBOL_POSITION;
      }
      set(ProjectField.CURRENCY_SYMBOL_POSITION, value);
   }

   /**
    * Retrieves a constant representing the position of the currency symbol.
    *
    * @return position
    */
   public CurrencySymbolPosition getSymbolPosition()
   {
      return (CurrencySymbolPosition) get(ProjectField.CURRENCY_SYMBOL_POSITION);
   }

   /**
    * Sets no of currency digits.
    *
    * @param currDigs Available values, 0,1,2
    */
   public void setCurrencyDigits(Integer currDigs)
   {
      if (currDigs == null)
      {
         currDigs = DEFAULT_CURRENCY_DIGITS;
      }
      set(ProjectField.CURRENCY_DIGITS, currDigs);
   }

   /**
    * Gets no of currency digits.
    *
    * @return Available values, 0,1,2
    */
   public Integer getCurrencyDigits()
   {
      return (Integer) get(ProjectField.CURRENCY_DIGITS);
   }

   /**
    * Sets the thousands separator.
    * Note that this separator defines the thousands separator for all decimal
    * numbers that appear in the MPX file.
    *
    * @param sep character
    */
   public void setThousandsSeparator(char sep)
   {
      set(ProjectField.THOUSANDS_SEPARATOR, Character.valueOf(sep));
   }

   /**
    * Gets the thousands separator.
    * Note that this separator defines the thousands separator for all decimal
    * numbers that appear in the MPX file.
    *
    * @return character
    */
   public char getThousandsSeparator()
   {
      return ((Character) get(ProjectField.THOUSANDS_SEPARATOR)).charValue();
   }

   /**
    * Sets the decimal separator.
    * Note that this separator defines the decimal separator for all decimal
    * numbers that appear in the MPX file.
    *
    * @param decSep character
    */
   public void setDecimalSeparator(char decSep)
   {
      set(ProjectField.DECIMAL_SEPARATOR, Character.valueOf(decSep));
   }

   /**
    * Gets the decimal separator.
    * Note that this separator defines the decimal separator for all decimal
    * numbers that appear in the MPX file.
    *
    * @return character
    */
   public char getDecimalSeparator()
   {
      return ((Character) get(ProjectField.DECIMAL_SEPARATOR)).charValue();
   }

   /**
    * Retrieve the externally edited flag.
    *
    * @return externally edited flag
    */
   public boolean getProjectExternallyEdited()
   {
      return BooleanHelper.getBoolean((Boolean) get(ProjectField.PROJECT_EXTERNALLY_EDITED));
   }

   /**
    * Set the externally edited flag.
    *
    * @param projectExternallyEdited externally edited flag
    */
   public void setProjectExternallyEdited(boolean projectExternallyEdited)
   {
      set(ProjectField.PROJECT_EXTERNALLY_EDITED, projectExternallyEdited);
   }

   /**
    * Retrieves the category text.
    *
    * @return category text
    */
   public String getCategory()
   {
      return (String) get(ProjectField.CATEGORY);
   }

   /**
    * Sets the category text.
    *
    * @param category category text
    */
   public void setCategory(String category)
   {
      set(ProjectField.CATEGORY, category);
   }

   /**
    * Retrieve the number of days per month.
    *
    * @return days per month
    */
   @Override public Integer getDaysPerMonth()
   {
      return (Integer) get(ProjectField.DAYS_PER_MONTH);
   }

   /**
    * Set the number of days per month.
    *
    * @param daysPerMonth days per month
    */
   public void setDaysPerMonth(Integer daysPerMonth)
   {
      set(ProjectField.DAYS_PER_MONTH, daysPerMonth);
   }

   /**
    * Retrieve the number of minutes per day.
    *
    * @return minutes per day
    */
   @Override public Integer getMinutesPerDay()
   {
      return (Integer) get(ProjectField.MINUTES_PER_DAY);
   }

   /**
    * Set the number of minutes per day.
    *
    * @param minutesPerDay minutes per day
    */
   public void setMinutesPerDay(Integer minutesPerDay)
   {
      set(ProjectField.MINUTES_PER_DAY, minutesPerDay);
   }

   /**
    * Retrieve the number of minutes per week.
    *
    * @return minutes per week
    */
   @Override public Integer getMinutesPerWeek()
   {
      return (Integer) get(ProjectField.MINUTES_PER_WEEK);
   }

   /**
    * Set the number of minutes per week.
    *
    * @param minutesPerWeek minutes per week
    */
   public void setMinutesPerWeek(Integer minutesPerWeek)
   {
      set(ProjectField.MINUTES_PER_WEEK, minutesPerWeek);
   }

   /**
    * Retrieve the default number of minutes per month.
    *
    * @return minutes per month
    */
   @Override public Integer getMinutesPerMonth()
   {
      return (Integer) get(ProjectField.MINUTES_PER_MONTH);
   }

   /**
    * Set the default number of minutes per month.
    *
    * @param minutesPerMonth minutes per month
    */
   public void setMinutesPerMonth(Integer minutesPerMonth)
   {
      set(ProjectField.MINUTES_PER_MONTH, minutesPerMonth);
   }

   /**
    * Retrieve the default number of minutes per year.
    *
    * @return minutes per year
    */
   @Override public Integer getMinutesPerYear()
   {
      return (Integer) get(ProjectField.MINUTES_PER_YEAR);
   }

   /**
    * Set the default number of minutes per year.
    *
    * @param minutesPerYear minutes per year
    */
   public void setMinutesPerYear(Integer minutesPerYear)
   {
      set(ProjectField.MINUTES_PER_YEAR, minutesPerYear);
   }

   /**
    * Retrieve the fiscal year start flag.
    *
    * @return fiscal year start flag
    */
   public boolean getFiscalYearStart()
   {
      return BooleanHelper.getBoolean((Boolean) get(ProjectField.FISCAL_YEAR_START));
   }

   /**
    * Set the fiscal year start flag.
    *
    * @param fiscalYearStart fiscal year start
    */
   public void setFiscalYearStart(boolean fiscalYearStart)
   {
      set(ProjectField.FISCAL_YEAR_START, fiscalYearStart);
   }

   /**
    * Retrieves the default task earned value method.
    *
    * @return default task earned value method
    */
   public EarnedValueMethod getDefaultTaskEarnedValueMethod()
   {
      return (EarnedValueMethod) get(ProjectField.DEFAULT_TASK_EARNED_VALUE_METHOD);
   }

   /**
    * Sets the default task earned value method.
    *
    * @param defaultTaskEarnedValueMethod default task earned value method
    */
   public void setDefaultTaskEarnedValueMethod(EarnedValueMethod defaultTaskEarnedValueMethod)
   {
      set(ProjectField.DEFAULT_TASK_EARNED_VALUE_METHOD, defaultTaskEarnedValueMethod);
   }

   /**
    * Retrieve the remove file properties flag.
    *
    * @return remove file properties flag
    */
   public boolean getRemoveFileProperties()
   {
      return BooleanHelper.getBoolean((Boolean) get(ProjectField.REMOVE_FILE_PROPERTIES));
   }

   /**
    * Set the remove file properties flag.
    *
    * @param removeFileProperties remove file properties flag
    */
   public void setRemoveFileProperties(boolean removeFileProperties)
   {
      set(ProjectField.REMOVE_FILE_PROPERTIES, removeFileProperties);
   }

   /**
    * Retrieve the move completed ends back flag.
    *
    * @return move completed ends back flag
    */
   public boolean getMoveCompletedEndsBack()
   {
      return BooleanHelper.getBoolean((Boolean) get(ProjectField.MOVE_COMPLETED_ENDS_BACK));
   }

   /**
    * Set the move completed ends back flag.
    *
    * @param moveCompletedEndsBack move completed ends back flag
    */
   public void setMoveCompletedEndsBack(boolean moveCompletedEndsBack)
   {
      set(ProjectField.MOVE_COMPLETED_ENDS_BACK, moveCompletedEndsBack);
   }

   /**
    * Retrieve the new tasks estimated flag.
    *
    * @return new tasks estimated flag
    */
   public boolean getNewTasksEstimated()
   {
      return BooleanHelper.getBoolean((Boolean) get(ProjectField.NEW_TASKS_ESTIMATED));
   }

   /**
    * Set the new tasks estimated flag.
    *
    * @param newTasksEstimated new tasks estimated flag
    */
   public void setNewTasksEstimated(boolean newTasksEstimated)
   {
      set(ProjectField.NEW_TASKS_ESTIMATED, newTasksEstimated);
   }

   /**
    * Retrieve the spread actual cost flag.
    *
    * @return spread actual cost flag
    */
   public boolean getSpreadActualCost()
   {
      return BooleanHelper.getBoolean((Boolean) get(ProjectField.SPREAD_ACTUAL_COST));
   }

   /**
    * Set the spread actual cost flag.
    *
    * @param spreadActualCost spread actual cost flag
    */
   public void setSpreadActualCost(boolean spreadActualCost)
   {
      set(ProjectField.SPREAD_ACTUAL_COST, spreadActualCost);
   }

   /**
    * Retrieve the multiple critical paths flag.
    *
    * @return multiple critical paths flag
    */
   public boolean getMultipleCriticalPaths()
   {
      return BooleanHelper.getBoolean((Boolean) get(ProjectField.MULTIPLE_CRITICAL_PATHS));
   }

   /**
    * Set the multiple critical paths flag.
    *
    * @param multipleCriticalPaths multiple critical paths flag
    */
   public void setMultipleCriticalPaths(boolean multipleCriticalPaths)
   {
      set(ProjectField.MULTIPLE_CRITICAL_PATHS, multipleCriticalPaths);
   }

   /**
    * Retrieve the auto add new resources and tasks flag.
    *
    * @return auto add new resources and tasks flag
    */
   public boolean getAutoAddNewResourcesAndTasks()
   {
      return BooleanHelper.getBoolean((Boolean) get(ProjectField.AUTO_ADD_NEW_RESOURCES_AND_TASKS));
   }

   /**
    * Set the auto add new resources and tasks flag.
    *
    * @param autoAddNewResourcesAndTasks auto add new resources and tasks flag
    */
   public void setAutoAddNewResourcesAndTasks(boolean autoAddNewResourcesAndTasks)
   {
      set(ProjectField.AUTO_ADD_NEW_RESOURCES_AND_TASKS, autoAddNewResourcesAndTasks);
   }

   /**
    * Retrieve the last saved date.
    *
    * @return last saved date
    */
   public LocalDateTime getLastSaved()
   {
      return (LocalDateTime) get(ProjectField.LAST_SAVED);
   }

   /**
    * Set the last saved date.
    *
    * @param lastSaved last saved date
    */
   public void setLastSaved(LocalDateTime lastSaved)
   {
      set(ProjectField.LAST_SAVED, lastSaved);
   }

   /**
    * Retrieve the status date.
    *
    * @return status date
    */
   public LocalDateTime getStatusDate()
   {
      return (LocalDateTime) get(ProjectField.STATUS_DATE);
   }

   /**
    * Set the status date.
    *
    * @param statusDate status date
    */
   public void setStatusDate(LocalDateTime statusDate)
   {
      set(ProjectField.STATUS_DATE, statusDate);
   }

   /**
    * Retrieves the move remaining starts back flag.
    *
    * @return move remaining starts back flag
    */
   public boolean getMoveRemainingStartsBack()
   {
      return BooleanHelper.getBoolean((Boolean) get(ProjectField.MOVE_REMAINING_STARTS_BACK));
   }

   /**
    * Sets the move remaining starts back flag.
    *
    * @param moveRemainingStartsBack remaining starts back flag
    */
   public void setMoveRemainingStartsBack(boolean moveRemainingStartsBack)
   {
      set(ProjectField.MOVE_REMAINING_STARTS_BACK, moveRemainingStartsBack);
   }

   /**
    * Retrieves the autolink flag.
    *
    * @return autolink flag
    */
   public boolean getAutolink()
   {
      return BooleanHelper.getBoolean((Boolean) get(ProjectField.AUTO_LINK));
   }

   /**
    * Sets the autolink flag.
    *
    * @param autolink autolink flag
    */
   public void setAutolink(boolean autolink)
   {
      set(ProjectField.AUTO_LINK, autolink);
   }

   /**
    * Retrieves the Microsoft Project Server URL flag.
    *
    * @return Microsoft Project Server URL flag
    */
   public boolean getMicrosoftProjectServerURL()
   {
      return BooleanHelper.getBoolean((Boolean) get(ProjectField.MICROSOFT_PROJECT_SERVER_URL));
   }

   /**
    * Sets the Microsoft Project Server URL flag.
    *
    * @param microsoftProjectServerURL Microsoft Project Server URL flag
    */
   public void setMicrosoftProjectServerURL(boolean microsoftProjectServerURL)
   {
      set(ProjectField.MICROSOFT_PROJECT_SERVER_URL, microsoftProjectServerURL);
   }

   /**
    * Retrieves the honor constraints flag.
    *
    * @return honor constraints flag
    */
   public boolean getHonorConstraints()
   {
      return BooleanHelper.getBoolean((Boolean) get(ProjectField.HONOR_CONSTRAINTS));
   }

   /**
    * Sets the honor constraints flag.
    *
    * @param honorConstraints honor constraints flag
    */
   public void setHonorConstraints(boolean honorConstraints)
   {
      set(ProjectField.HONOR_CONSTRAINTS, honorConstraints);
   }

   /**
    * Retrieve the admin project flag.
    *
    * @return admin project flag
    */
   public boolean getAdminProject()
   {
      return BooleanHelper.getBoolean((Boolean) get(ProjectField.ADMIN_PROJECT));
   }

   /**
    * Set the admin project flag.
    *
    * @param adminProject admin project flag
    */
   public void setAdminProject(boolean adminProject)
   {
      set(ProjectField.ADMIN_PROJECT, adminProject);
   }

   /**
    * Retrieves the inserted projects like summary flag.
    *
    * @return inserted projects like summary flag
    */
   public boolean getInsertedProjectsLikeSummary()
   {
      return BooleanHelper.getBoolean((Boolean) get(ProjectField.INSERTED_PROJECTS_LIKE_SUMMARY));
   }

   /**
    * Sets the inserted projects like summary flag.
    *
    * @param insertedProjectsLikeSummary inserted projects like summary flag
    */
   public void setInsertedProjectsLikeSummary(boolean insertedProjectsLikeSummary)
   {
      set(ProjectField.INSERTED_PROJECTS_LIKE_SUMMARY, insertedProjectsLikeSummary);
   }

   /**
    * Retrieves the project name.
    *
    * @return project name
    */
   public String getName()
   {
      return (String) get(ProjectField.NAME);
   }

   /**
    * Sets the project name.
    *
    * @param name project name
    */
   public void setName(String name)
   {
      set(ProjectField.NAME, name);
   }

   /**
    * Retrieves the spread percent complete flag.
    *
    * @return spread percent complete flag
    */
   public boolean getSpreadPercentComplete()
   {
      return BooleanHelper.getBoolean((Boolean) get(ProjectField.SPREAD_PERCENT_COMPLETE));
   }

   /**
    * Sets the spread percent complete flag.
    *
    * @param spreadPercentComplete spread percent complete flag
    */
   public void setSpreadPercentComplete(boolean spreadPercentComplete)
   {
      set(ProjectField.SPREAD_PERCENT_COMPLETE, spreadPercentComplete);
   }

   /**
    * Retrieve the move completed ends forward flag.
    *
    * @return move completed ends forward flag
    */
   public boolean getMoveCompletedEndsForward()
   {
      return BooleanHelper.getBoolean((Boolean) get(ProjectField.MOVE_COMPLETED_ENDS_FORWARD));
   }

   /**
    * Sets the move completed ends forward flag.
    *
    * @param moveCompletedEndsForward move completed ends forward flag
    */
   public void setMoveCompletedEndsForward(boolean moveCompletedEndsForward)
   {
      set(ProjectField.MOVE_COMPLETED_ENDS_FORWARD, moveCompletedEndsForward);
   }

   /**
    * Retrieve the editable actual costs flag.
    *
    * @return editable actual costs flag
    */
   public boolean getEditableActualCosts()
   {
      return BooleanHelper.getBoolean((Boolean) get(ProjectField.EDITABLE_ACTUAL_COSTS));
   }

   /**
    * Set the editable actual costs flag.
    *
    * @param editableActualCosts editable actual costs flag
    */
   public void setEditableActualCosts(boolean editableActualCosts)
   {
      set(ProjectField.EDITABLE_ACTUAL_COSTS, editableActualCosts);
   }

   /**
    * Retrieve the unique ID for this project.
    *
    * @return unique ID
    */
   public Integer getUniqueID()
   {
      return (Integer) get(ProjectField.UNIQUE_ID);
   }

   /**
    * Set the unique ID for this project.
    *
    * @param uniqueID unique ID
    */
   public void setUniqueID(Integer uniqueID)
   {
      set(ProjectField.UNIQUE_ID, uniqueID);
   }

   /**
    * Retrieve the GUID for this project.
    *
    * @return unique ID
    */
   public UUID getGUID()
   {
      return (UUID) get(ProjectField.GUID);
   }

   /**
    * Set the GUID for this project.
    *
    * @param guid GUID
    */
   public void setGUID(UUID guid)
   {
      set(ProjectField.GUID, guid);
   }

   /**
    * Retrieve the project revision number.
    *
    * @return revision number
    */
   public Integer getRevision()
   {
      return (Integer) get(ProjectField.REVISION);
   }

   /**
    * Retrieve the new tasks effort driven flag.
    *
    * @return new tasks effort driven flag
    */
   public boolean getNewTasksEffortDriven()
   {
      return BooleanHelper.getBoolean((Boolean) get(ProjectField.NEW_TASKS_EFFORT_DRIVEN));
   }

   /**
    * Sets the new tasks effort driven flag.
    *
    * @param newTasksEffortDriven new tasks effort driven flag
    */
   public void setNewTasksEffortDriven(boolean newTasksEffortDriven)
   {
      set(ProjectField.NEW_TASKS_EFFORT_DRIVEN, newTasksEffortDriven);
   }

   /**
    * Set the project revision number.
    *
    * @param revision revision number
    */
   public void setRevision(Integer revision)
   {
      set(ProjectField.REVISION, revision);
   }

   /**
    * Retrieve the move remaining starts forward flag.
    *
    * @return move remaining starts forward flag
    */
   public boolean getMoveRemainingStartsForward()
   {
      return BooleanHelper.getBoolean((Boolean) get(ProjectField.MOVE_REMAINING_STARTS_FORWARD));
   }

   /**
    * Set the move remaining starts forward flag.
    *
    * @param moveRemainingStartsForward move remaining starts forward flag
    */
   public void setMoveRemainingStartsForward(boolean moveRemainingStartsForward)
   {
      set(ProjectField.MOVE_REMAINING_STARTS_FORWARD, moveRemainingStartsForward);
   }

   /**
    * Retrieve the actuals in sync flag.
    *
    * @return actuals in sync flag
    */
   public boolean getActualsInSync()
   {
      return BooleanHelper.getBoolean((Boolean) get(ProjectField.ACTUALS_IN_SYNC));
   }

   /**
    * Set the actuals in sync flag.
    *
    * @param actualsInSync actuals in sync flag
    */
   public void setActualsInSync(boolean actualsInSync)
   {
      set(ProjectField.ACTUALS_IN_SYNC, actualsInSync);
   }

   /**
    * Retrieve the default task type.
    *
    * @return default task type
    */
   public TaskType getDefaultTaskType()
   {
      return (TaskType) get(ProjectField.DEFAULT_TASK_TYPE);
   }

   /**
    * Set the default task type.
    *
    * @param defaultTaskType default task type
    */
   public void setDefaultTaskType(TaskType defaultTaskType)
   {
      set(ProjectField.DEFAULT_TASK_TYPE, defaultTaskType);
   }

   /**
    * Retrieve the earned value method.
    *
    * @return earned value method
    */
   public EarnedValueMethod getEarnedValueMethod()
   {
      return (EarnedValueMethod) get(ProjectField.EARNED_VALUE_METHOD);
   }

   /**
    * Set the earned value method.
    *
    * @param earnedValueMethod earned value method
    */
   public void setEarnedValueMethod(EarnedValueMethod earnedValueMethod)
   {
      set(ProjectField.EARNED_VALUE_METHOD, earnedValueMethod);
   }

   /**
    * Retrieve the project creation date.
    *
    * @return project creation date
    */
   public LocalDateTime getCreationDate()
   {
      return (LocalDateTime) get(ProjectField.CREATION_DATE);
   }

   /**
    * Set the project creation date.
    *
    * @param creationDate project creation date
    */
   public void setCreationDate(LocalDateTime creationDate)
   {
      set(ProjectField.CREATION_DATE, creationDate);
   }

   /**
    * Retrieve the extended creation date.
    *
    * @return extended creation date
    */
   public LocalDateTime getExtendedCreationDate()
   {
      return (LocalDateTime) get(ProjectField.EXTENDED_CREATION_DATE);
   }

   /**
    * Retrieve the default fixed cost accrual type.
    *
    * @return default fixed cost accrual type
    */
   public AccrueType getDefaultFixedCostAccrual()
   {
      return (AccrueType) get(ProjectField.DEFAULT_FIXED_COST_ACCRUAL);
   }

   /**
    * Sets the default fixed cost accrual type.
    *
    * @param defaultFixedCostAccrual default fixed cost accrual type
    */
   public void setDefaultFixedCostAccrual(AccrueType defaultFixedCostAccrual)
   {
      set(ProjectField.DEFAULT_FIXED_COST_ACCRUAL, defaultFixedCostAccrual);
   }

   /**
    * Set the extended creation date.
    *
    * @param creationDate extended creation date
    */
   public void setExtendedCreationDate(LocalDateTime creationDate)
   {
      set(ProjectField.EXTENDED_CREATION_DATE, creationDate);
   }

   /**
    * Retrieve the critical slack limit.
    *
    * @return critical slack limit
    */
   public Duration getCriticalSlackLimit()
   {
      return (Duration) get(ProjectField.CRITICAL_SLACK_LIMIT);
   }

   /**
    * Set the critical slack limit.
    *
    * @param criticalSlackLimit critical slack limit
    */
   public void setCriticalSlackLimit(Duration criticalSlackLimit)
   {
      set(ProjectField.CRITICAL_SLACK_LIMIT, criticalSlackLimit);
   }

   /**
    * Retrieve the number of the baseline to use for earned value
    * calculations.
    *
    * @return baseline for earned value
    */
   public Integer getBaselineForEarnedValue()
   {
      return (Integer) get(ProjectField.BASELINE_FOR_EARNED_VALUE);
   }

   /**
    * Set the number of the baseline to use for earned value
    * calculations.
    *
    * @param baselineForEarnedValue baseline for earned value
    */
   public void setBaselineForEarnedValue(Integer baselineForEarnedValue)
   {
      set(ProjectField.BASELINE_FOR_EARNED_VALUE, baselineForEarnedValue);
   }

   /**
    * Retrieves the fiscal year start month (January=1, December=12).
    *
    * @return fiscal year start month
    */
   public Integer getFiscalYearStartMonth()
   {
      return (Integer) get(ProjectField.FISCAL_YEAR_START_MONTH);
   }

   /**
    * Sets the fiscal year start month (January=1, December=12).
    *
    * @param fiscalYearStartMonth fiscal year start month
    */
   public void setFiscalYearStartMonth(Integer fiscalYearStartMonth)
   {
      set(ProjectField.FISCAL_YEAR_START_MONTH, fiscalYearStartMonth);
   }

   /**
    * Retrieve the flag indicating if new tasks should default to the
    * project start date (true) or the current date (false).
    *
    * @return new task start is project start
    */
   public boolean getNewTaskStartIsProjectStart()
   {
      return BooleanHelper.getBoolean((Boolean) get(ProjectField.NEW_TASK_START_IS_PROJECT_START));
   }

   /**
    * Sets the flag indicating if new tasks should default to the
    * project start date (true) or the current date (false).
    *
    * @param newTaskStartIsProjectStart new task start is project start
    */
   public void setNewTaskStartIsProjectStart(boolean newTaskStartIsProjectStart)
   {
      set(ProjectField.NEW_TASK_START_IS_PROJECT_START, newTaskStartIsProjectStart);
   }

   /**
    * Retrieve the flag indicating if new tasks task mode should default to
    * manual (true) or automatic (false).
    *
    * @return new task type is manual or auto
    */
   public boolean getNewTasksAreManual()
   {
      return BooleanHelper.getBoolean((Boolean) get(ProjectField.NEW_TASKS_ARE_MANUAL));
   }

   /**
    * Set the flag indicating if new tasks task mode should default to
    * manual (true) or automatic (false).
    *
    * @param newTasksAreManual new task type is manual or auto
    */
   public void setNewTasksAreManual(boolean newTasksAreManual)
   {
      set(ProjectField.NEW_TASKS_ARE_MANUAL, newTasksAreManual);
   }

   /**
    * Retrieve the week start day.
    *
    * @return week start day
    */
   public DayOfWeek getWeekStartDay()
   {
      return (DayOfWeek) get(ProjectField.WEEK_START_DAY);
   }

   /**
    * Set the week start day.
    *
    * @param weekStartDay week start day
    */
   public void setWeekStartDay(DayOfWeek weekStartDay)
   {
      set(ProjectField.WEEK_START_DAY, weekStartDay);
   }

   /**
    * Retrieve the currency code for this project.
    *
    * @return currency code
    */
   public String getCurrencyCode()
   {
      return (String) get(ProjectField.CURRENCY_CODE);
   }

   /**
    * Set the currency code for this project.
    *
    * @param currencyCode currency code
    */
   public void setCurrencyCode(String currencyCode)
   {
      set(ProjectField.CURRENCY_CODE, currencyCode);
   }

   /**
    * Sets a map of custom document properties.
    *
    * @param customProperties The Document Summary Information Map
    */
   public void setCustomProperties(Map<String, Object> customProperties)
   {
      set(ProjectField.CUSTOM_PROPERTIES, customProperties);
   }

   /**
    * Retrieve a map of custom document properties.
    *
    * @return the Document Summary Information Map
    */
   @SuppressWarnings("unchecked") public Map<String, Object> getCustomProperties()
   {
      return (Map<String, Object>) get(ProjectField.CUSTOM_PROPERTIES);
   }

   /**
    * Sets the hyperlink base for this Project.
    *
    * @param hyperlinkBase Hyperlink base
    */
   public void setHyperlinkBase(String hyperlinkBase)
   {
      set(ProjectField.HYPERLINK_BASE, hyperlinkBase);
   }

   /**
    * Gets the hyperlink base for this Project. If any.
    *
    * @return Hyperlink base
    */
   public String getHyperlinkBase()
   {
      return (String) get(ProjectField.HYPERLINK_BASE);
   }

   /**
    * Retrieves the "show project summary task" flag.
    *
    * @return boolean flag
    */
   public boolean getShowProjectSummaryTask()
   {
      return BooleanHelper.getBoolean((Boolean) get(ProjectField.SHOW_PROJECT_SUMMARY_TASK));
   }

   /**
    * Sets the "show project summary task" flag.
    *
    * @param value boolean flag
    */
   public void setShowProjectSummaryTask(boolean value)
   {
      set(ProjectField.SHOW_PROJECT_SUMMARY_TASK, value);
   }

   /**
    * Retrieve a baseline value.
    *
    * @return baseline value
    */
   public LocalDateTime getBaselineDate()
   {
      return (LocalDateTime) get(ProjectField.BASELINE_DATE);
   }

   /**
    * Set a baseline value.
    *
    * @param value baseline value
    */
   public void setBaselineDate(LocalDateTime value)
   {
      set(ProjectField.BASELINE_DATE, value);
   }

   /**
    * Retrieve a baseline value.
    *
    * @param baselineNumber baseline index (1-10)
    * @return baseline value
    */
   public LocalDateTime getBaselineDate(int baselineNumber)
   {
      return (LocalDateTime) get(selectField(ProjectFieldLists.BASELINE_DATES, baselineNumber));
   }

   /**
    * Set a baseline value.
    *
    * @param baselineNumber baseline index (1-10)
    * @param value baseline value
    */
   public void setBaselineDate(int baselineNumber, LocalDateTime value)
   {
      set(selectField(ProjectFieldLists.BASELINE_DATES, baselineNumber), value);
   }

   /**
    * Retrieve the template property.
    *
    * @return template property
    */
   public String getTemplate()
   {
      return (String) get(ProjectField.TEMPLATE);
   }

   /**
    * Set the template property.
    *
    * @param template property value
    */
   public void setTemplate(String template)
   {
      set(ProjectField.TEMPLATE, template);
   }

   /**
    * Retrieve the project user property.
    *
    * @return project user property
    */
   public String getLastAuthor()
   {
      return (String) get(ProjectField.LAST_AUTHOR);
   }

   /**
    * Set the project user property.
    *
    * @param projectUser project user property
    */
   public void setLastAuthor(String projectUser)
   {
      set(ProjectField.LAST_AUTHOR, projectUser);
   }

   /**
    * Retrieve the last printed property.
    *
    * @return last printed property
    */
   public LocalDateTime getLastPrinted()
   {
      return (LocalDateTime) get(ProjectField.LASTPRINTED);
   }

   /**
    * Set the last printed property.
    *
    * @param lastPrinted property value
    */
   public void setLastPrinted(LocalDateTime lastPrinted)
   {
      set(ProjectField.LASTPRINTED, lastPrinted);
   }

   /**
    * Retrieve the application property.
    *
    * @return property value
    */
   public String getShortApplicationName()
   {
      return (String) get(ProjectField.SHORT_APPLICATION_NAME);
   }

   /**
    * Set the application property.
    *
    * @param application property value
    */
   public void setShortApplicationName(String application)
   {
      set(ProjectField.SHORT_APPLICATION_NAME, application);
   }

   /**
    * Retrieve the editing time property.
    *
    * @return property value
    */
   public Integer getEditingTime()
   {
      return (Integer) get(ProjectField.EDITING_TIME);
   }

   /**
    * Set the editing time property.
    *
    * @param editingTime editing time property
    */
   public void setEditingTime(Integer editingTime)
   {
      set(ProjectField.EDITING_TIME, editingTime);
   }

   /**
    * Retrieve the format property.
    *
    * @return property value
    */
   public String getPresentationFormat()
   {
      return (String) get(ProjectField.PRESENTATION_FORMAT);
   }

   /**
    * Set the format property.
    *
    * @param format property value
    */
   public void setPresentationFormat(String format)
   {
      set(ProjectField.PRESENTATION_FORMAT, format);
   }

   /**
    * Retrieve the content type property.
    *
    * @return content type property
    */
   public String getContentType()
   {
      return (String) get(ProjectField.CONTENT_TYPE);
   }

   /**
    * Set the content type property.
    *
    * @param contentType property value
    */
   public void setContentType(String contentType)
   {
      set(ProjectField.CONTENT_TYPE, contentType);
   }

   /**
    * Retrieve the content status property.
    *
    * @return property value
    */
   public String getContentStatus()
   {
      return (String) get(ProjectField.CONTENT_STATUS);
   }

   /**
    * Set the content status property.
    *
    * @param contentStatus property value
    */
   public void setContentStatus(String contentStatus)
   {
      set(ProjectField.CONTENT_STATUS, contentStatus);
   }

   /**
    * Retrieve the language property.
    *
    * @return property value
    */
   public String getLanguage()
   {
      return (String) get(ProjectField.LANGUAGE);
   }

   /**
    * Set the language property.
    *
    * @param language property value
    */
   public void setLanguage(String language)
   {
      set(ProjectField.LANGUAGE, language);
   }

   /**
    * Retrieve the document version property.
    *
    * @return property value
    */
   public String getDocumentVersion()
   {
      return (String) get(ProjectField.DOCUMENT_VERSION);
   }

   /**
    * Set the document version property.
    *
    * @param documentVersion property value
    */
   public void setDocumentVersion(String documentVersion)
   {
      set(ProjectField.DOCUMENT_VERSION, documentVersion);
   }

   /**
    * Sets the delimiter character, "," by default.
    *
    * @param delimiter delimiter character
    */
   public void setMpxDelimiter(char delimiter)
   {
      set(ProjectField.MPX_DELIMITER, Character.valueOf(delimiter));
   }

   /**
    * Retrieves the delimiter character, "," by default.
    *
    * @return delimiter character
    */
   public char getMpxDelimiter()
   {
      return ((Character) get(ProjectField.MPX_DELIMITER)).charValue();
   }

   /**
    * Program name file created by.
    *
    * @param programName system name
    */
   public void setMpxProgramName(String programName)
   {
      set(ProjectField.MPX_PROGRAM_NAME, programName);
   }

   /**
    * Program name file created by.
    *
    * @return program name
    */
   public String getMpxProgramName()
   {
      return (String) get(ProjectField.MPX_PROGRAM_NAME);
   }

   /**
    * Version of the MPX file.
    *
    * @param version MPX file version
    */
   public void setMpxFileVersion(FileVersion version)
   {
      set(ProjectField.MPX_FILE_VERSION, version);
   }

   /**
    * Version of the MPX file.
    *
    * @return MPX file version
    */
   public FileVersion getMpxFileVersion()
   {
      return (FileVersion) get(ProjectField.MPX_FILE_VERSION);
   }

   /**
    * Sets the codepage.
    *
    * @param codePage code page type
    */
   public void setMpxCodePage(CodePage codePage)
   {
      set(ProjectField.MPX_CODE_PAGE, codePage);
   }

   /**
    * Retrieves the codepage.
    *
    * @return code page type
    */
   public CodePage getMpxCodePage()
   {
      return (CodePage) get(ProjectField.MPX_CODE_PAGE);
   }

   /**
    * Sets the project file path.
    *
    * @param projectFilePath project file path
    */
   public void setProjectFilePath(String projectFilePath)
   {
      set(ProjectField.PROJECT_FILE_PATH, projectFilePath);
   }

   /**
    * Gets the project file path.
    *
    * @return project file path
    */
   public String getProjectFilePath()
   {
      return (String) get(ProjectField.PROJECT_FILE_PATH);
   }

   /**
    * Retrieves the name of the application used to create this project data.
    *
    * @return application name
    */
   public String getFullApplicationName()
   {
      return (String) get(ProjectField.FULL_APPLICATION_NAME);
   }

   /**
    * Sets the name of the application used to create this project data.
    *
    * @param name application name
    */
   public void setFullApplicationName(String name)
   {
      set(ProjectField.FULL_APPLICATION_NAME, name);
   }

   /**
    * Retrieves the version of the application used to create this project.
    *
    * @return application name
    */
   public Integer getApplicationVersion()
   {
      return (Integer) get(ProjectField.APPLICATION_VERSION);
   }

   /**
    * Sets the version of the application used to create this project.
    *
    * @param version application version
    */
   public void setApplicationVersion(Integer version)
   {
      set(ProjectField.APPLICATION_VERSION, version);
   }

   /**
    * This method retrieves a value representing the type of MPP file
    * that has been read. Currently, this method will return the value 8 for
    * an MPP8 file (Project 98), 9 for an MPP9 file (Project 2000 and
    * Project 2002), 12 for an MPP12 file (Project 2003, Project 2007) and 14 for an
    * MPP14 file (Project 2010 and Project 2013).
    *
    * @return integer representing the file type
    */
   public Integer getMppFileType()
   {
      return (Integer) get(ProjectField.MPP_FILE_TYPE);
   }

   /**
    * Used internally to set the file type.
    *
    * @param fileType file type
    */
   public void setMppFileType(Integer fileType)
   {
      set(ProjectField.MPP_FILE_TYPE, fileType);
   }

   /**
    * Retrieve a flag indicating if auto filter is enabled.
    *
    * @return auto filter flag
    */
   public boolean getAutoFilter()
   {
      return BooleanHelper.getBoolean((Boolean) get(ProjectField.AUTOFILTER));
   }

   /**
    * Sets a flag indicating if auto filter is enabled.
    *
    * @param autoFilter boolean flag
    */
   public void setAutoFilter(boolean autoFilter)
   {
      set(ProjectField.AUTOFILTER, autoFilter);
   }

   /**
    * Retrieves the vendor of the file used to populate this ProjectFile instance.
    *
    * @return file type
    */
   public String getFileApplication()
   {
      return (String) get(ProjectField.FILE_APPLICATION);
   }

   /**
    * Sets the vendor of file used to populate this ProjectFile instance.
    *
    * @param type file type
    */
   public void setFileApplication(String type)
   {
      set(ProjectField.FILE_APPLICATION, type);
   }

   /**
    * Retrieves the type of file used to populate this ProjectFile instance.
    *
    * @return file type
    */
   public String getFileType()
   {
      return (String) get(ProjectField.FILE_TYPE);
   }

   /**
    * Sets the type of file used to populate this ProjectFile instance.
    *
    * @param type file type
    */
   public void setFileType(String type)
   {
      set(ProjectField.FILE_TYPE, type);
   }

   /**
    * Retrieves the export flag used to specify if the project was chosen to export from P6.
    * Projects that have external relationships may be included in an export, even when not
    * specifically flagged in the export. This flag differentiates those projects
    *
    * @return export boolean flag
    */
   public boolean getExportFlag()
   {
      return BooleanHelper.getBoolean((Boolean) get(ProjectField.EXPORT_FLAG));
   }

   /**
    * Sets the export flag to populate this ProjectFile instance.
    *
    * @param value boolean flag
    */
   public void setExportFlag(boolean value)
   {
      set(ProjectField.EXPORT_FLAG, value);
   }

   /**
    * Retrieve the baseline project unique ID for this project.
    *
    * @return baseline project unique ID
    */
   public Integer getBaselineProjectUniqueID()
   {
      return (Integer) get(ProjectField.BASELINE_PROJECT_UNIQUE_ID);
   }

   /**
    * Set the baseline project unique ID for this project.
    *
    * @param uniqueID baseline project unique ID
    */
   public void setBaselineProjectUniqueID(Integer uniqueID)
   {
      set(ProjectField.BASELINE_PROJECT_UNIQUE_ID, uniqueID);
   }

   /**
    * Retrieve the project ID for this project.
    *
    * @return baseline project unique ID
    */
   public String getProjectID()
   {
      return (String) get(ProjectField.PROJECT_ID);
   }

   /**
    * Set the project ID for this project.
    *
    * @param id project ID
    */
   public void setProjectID(String id)
   {
      set(ProjectField.PROJECT_ID, id);
   }

   /**
    * Retrieve the critical activity type for this project.
    *
    * @return critical activity type
    */
   public CriticalActivityType getCriticalActivityType()
   {
      return (CriticalActivityType) get(ProjectField.CRITICAL_ACTIVITY_TYPE);
   }

   /**
    * Set the critical activity type for this project.
    *
    * @param value critical activity type
    */
   public void setCriticalActivityType(CriticalActivityType value)
   {
      set(ProjectField.CRITICAL_ACTIVITY_TYPE, value);
   }

   /**
    * Sets the must finish by date for this project.
    *
    * @param date must finish by date
    */
   public void setMustFinishBy(LocalDateTime date)
   {
      set(ProjectField.MUST_FINISH_BY, date);
   }

   /**
    * Retrieves the must finish by date for this project.
    *
    * @return must finish by date
    */
   public LocalDateTime getMustFinishBy()
   {
      return (LocalDateTime) get(ProjectField.MUST_FINISH_BY);
   }

   /**
    * Sets the scheduled finish by date for this project.
    *
    * @param date scheduled finish by date
    */
   public void setScheduledFinish(LocalDateTime date)
   {
      set(ProjectField.SCHEDULED_FINISH, date);
   }

   /**
    * Retrieves the scheduled finish by date for this project.
    *
    * @return scheduled finish by date
    */
   public LocalDateTime getScheduledFinish()
   {
      return (LocalDateTime) get(ProjectField.SCHEDULED_FINISH);
   }

   /**
    * Sets the planned start by date for this project.
    *
    * @param date planned start by date
    */
   public void setPlannedStart(LocalDateTime date)
   {
      set(ProjectField.PLANNED_START, date);
   }

   /**
    * Retrieves the planned start by date for this project.
    *
    * @return planned start by date
    */
   public LocalDateTime getPlannedStart()
   {
      return (LocalDateTime) get(ProjectField.PLANNED_START);
   }

   /**
    * Retrieves the location unique ID.
    *
    * @return location unique ID
    */
   public Integer getLocationUniqueID()
   {
      return (Integer) get(ProjectField.LOCATION_UNIQUE_ID);
   }

   /**
    * Sets the location unique ID.
    *
    * @param uniqueID location unique ID
    */
   public void setLocationUniqueID(Integer uniqueID)
   {
      set(ProjectField.LOCATION_UNIQUE_ID, uniqueID);
   }

   /**
    * Retrieves the location.
    *
    * @return location.
    */
   public Location getLocation()
   {
      return getParentFile().getLocations().getByUniqueID(getLocationUniqueID());
   }

   /**
    * Sets the location.
    *
    * @param location location
    */
   public void setLocation(Location location)
   {
      setLocationUniqueID(location == null ? null : location.getUniqueID());
   }

   /**
    * Retrieve the resource pool file associated with this project.
    *
    * @return resource pool file
    */
   public String getResourcePoolFile()
   {
      return (String) get(ProjectField.RESOURCE_POOL_FILE);
   }

   /**
    * Set the resource pool file associated with this project.
    *
    * @param file resource pool file
    */
   public void setResourcePoolFile(String file)
   {
      set(ProjectField.RESOURCE_POOL_FILE, file);
   }

   /**
    * Retrieve a ProjectFile instance representing the resource pool for this project
    * Returns null if this project does not have a resource pool or the file cannot be read.
    *
    * @return ProjectFile instance for the resource pool
    */
   public ProjectFile getResourcePoolObject()
   {
      return getParentFile().readExternalProject(getResourcePoolFile());
   }

   /**
    * Set the total slack calculation type.
    *
    * @param type total slack calculation type
    */
   public void setTotalSlackCalculationType(TotalSlackCalculationType type)
   {
      set(ProjectField.TOTAL_SLACK_CALCULATION_TYPE, type);
   }

   /**
    * Retrieve the total slack calculation type.
    *
    * @return total slack calculation type
    */
   public TotalSlackCalculationType getTotalSlackCalculationType()
   {
      return (TotalSlackCalculationType) get(ProjectField.TOTAL_SLACK_CALCULATION_TYPE);
   }

   /**
    * Set the relationship lag calendar.
    *
    * @param calendar relationship lag calendar
    */
   public void setRelationshipLagCalendar(RelationshipLagCalendar calendar)
   {
      set(ProjectField.RELATIONSHIP_LAG_CALENDAR, calendar);
   }

   /**
    * Retrieve the relationship lag calendar.
    *
    * @return relationship lag calendar
    */
   public RelationshipLagCalendar getRelationshipLagCalendar()
   {
      return (RelationshipLagCalendar) get(ProjectField.RELATIONSHIP_LAG_CALENDAR);
   }

   /**
    * Retrieve the WBS Code separator character.
    *
    * @return WBS Code separator character
    */
   public String getWbsCodeSeparator()
   {
      return (String) get(ProjectField.WBS_CODE_SEPARATOR);
   }

   /**
    * Set the WBS Code separator character.
    *
    * @param value WBS Code separator character
    */
   public void setWbsCodeSeparator(String value)
   {
      set(ProjectField.WBS_CODE_SEPARATOR, value);
   }

   /**
    * Retrieve the consider assignments in other projects when leveling flag.
    *
    * @return consider assignments in other projects flag
    */
   public boolean getConsiderAssignmentsInOtherProjects()
   {
      return BooleanHelper.getBoolean((Boolean) get(ProjectField.CONSIDER_ASSIGNMENTS_IN_OTHER_PROJECTS));
   }

   /**
    * Set the consider assignments in other projects when leveling flag.
    *
    * @param value consider assignments in other projects fla
    */
   public void setConsiderAssignmentsInOtherProjects(boolean value)
   {
      set(ProjectField.CONSIDER_ASSIGNMENTS_IN_OTHER_PROJECTS, value);
   }

   /**
    * Retrieve the priority of assignment in other projects to consider when leveling.
    *
    * @return assignment priority
    */
   public Integer getConsiderAssignmentsInOtherProjectsWithPriorityEqualHigherThan()
   {
      return (Integer) get(ProjectField.CONSIDER_ASSIGNMENTS_IN_OTHER_PROJECTS_WITH_PRIORITY_EQUAL_HIGHER_THAN);
   }

   /**
    * Set the priority of assignment in other projects to consider when leveling.
    *
    * @param value assignment priority
    */
   public void setConsiderAssignmentsInOtherProjectsWithPriorityEqualHigherThan(Integer value)
   {
      set(ProjectField.CONSIDER_ASSIGNMENTS_IN_OTHER_PROJECTS_WITH_PRIORITY_EQUAL_HIGHER_THAN, value);
   }

   /**
    * Retrieve the preserve scheduled early and late dates flag.
    *
    * @return preserve scheduled early and late dates flag
    */
   public boolean getPreserveScheduledEarlyAndLateDates()
   {
      return BooleanHelper.getBoolean((Boolean) get(ProjectField.PRESERVE_SCHEDULED_EARLY_AND_LATE_DATES));
   }

   /**
    * Set the preserve scheduled early and late dates flag.
    *
    * @param value preserve scheduled early and late dates flag
    */
   public void setPreserveScheduledEarlyAndLateDates(boolean value)
   {
      set(ProjectField.PRESERVE_SCHEDULED_EARLY_AND_LATE_DATES, value);
   }

   /**
    * Retrieve the level all resources flag.
    *
    * @return level all resources flag
    */
   public boolean getLevelAllResources()
   {
      return BooleanHelper.getBoolean((Boolean) get(ProjectField.LEVEL_ALL_RESOURCES));
   }

   /**
    * Set the level all resources flag.
    *
    * @param value level all resources flag
    */
   public void setLevelAllResources(boolean value)
   {
      set(ProjectField.LEVEL_ALL_RESOURCES, value);
   }

   /**
    * Retrieve the level resources only within activity total float flag.
    *
    * @return level resources only within activity total float flag
    */
   public boolean getLevelResourcesOnlyWithinActivityTotalFloat()
   {
      return BooleanHelper.getBoolean((Boolean) get(ProjectField.LEVEL_RESOURCES_ONLY_WITHIN_ACTIVITY_TOTAL_FLOAT));
   }

   /**
    * Set the level resources only within activity total float flag.
    *
    * @param value level resources only within activity total float flag
    */
   public void setLevelResourcesOnlyWithinActivityTotalFloat(boolean value)
   {
      set(ProjectField.LEVEL_RESOURCES_ONLY_WITHIN_ACTIVITY_TOTAL_FLOAT, value);
   }

   /**
    * Retrieve the preserve minimum float when leveling value.
    *
    * @return float to preserve when leveling
    */
   public Duration getPreserveMinimumFloatWhenLeveling()
   {
      return (Duration) get(ProjectField.PRESERVE_MINIMUM_FLOAT_WHEN_LEVELING);
   }

   /**
    * Set the preserve minimum float when leveling value.
    *
    * @param value float to preserve when leveling
    */
   public void setPreserveMinimumFloatWhenLeveling(Duration value)
   {
      set(ProjectField.PRESERVE_MINIMUM_FLOAT_WHEN_LEVELING, value);
   }

   /**
    * Retrieve the maximum percentage to overallocate resources.
    *
    * @return maximum percentage to overallocate resources
    */
   public Number getMaxPercentToOverallocateResources()
   {
      return (Number) get(ProjectField.MAX_PERCENT_TO_OVERALLOCATE_RESOURCES);
   }

   /**
    * Set the maximum percentage to overallocate resources.
    *
    * @param value maximum percentage to overallocate resources
    */
   public void setMaxPercentToOverallocateResources(Number value)
   {
      set(ProjectField.MAX_PERCENT_TO_OVERALLOCATE_RESOURCES, value);
   }

   /**
    * Retrieve the leveling priorities expression.
    *
    * @return leveling priorities expression
    */
   public String getLevelingPriorities()
   {
      return (String) get(ProjectField.LEVELING_PRIORITIES);
   }

   /**
    * Set the leveling priorities expression.
    *
    * @param value leveling priorities expression
    */
   public void setLevelingPriorities(String value)
   {
      set(ProjectField.LEVELING_PRIORITIES, value);
   }

   /**
    * Retrieve the data date and planned start set to project forecast start flag.
    *
    * @return data date and planned start set to project forecast start flag
    */
   public boolean getDataDateAndPlannedStartSetToProjectForecastStart()
   {
      return BooleanHelper.getBoolean((Boolean) get(ProjectField.DATA_DATE_AND_PLANNED_START_SET_TO_PROJECT_FORECAST_START));
   }

   /**
    * Set the data date and planned start set to project forecast start flag.
    *
    * @param value data date and planned start set to project forecast start flag
    */
   public void setDataDateAndPlannedStartSetToProjectForecastStart(boolean value)
   {
      set(ProjectField.DATA_DATE_AND_PLANNED_START_SET_TO_PROJECT_FORECAST_START, value);
   }

   /**
    * Retrieve the ignore relationships to and from other projects flag.
    *
    * @return ignore relationships to and from other projects flag
    */
   public boolean getIgnoreRelationshipsToAndFromOtherProjects()
   {
      return BooleanHelper.getBoolean((Boolean) get(ProjectField.IGNORE_RELATIONSHIPS_TO_AND_FROM_OTHER_PROJECTS));
   }

   /**
    * Set the ignore relationships to and from other projects flag.
    *
    * @param value ignore relationships to and from other projects flag
    */
   public void setIgnoreRelationshipsToAndFromOtherProjects(boolean value)
   {
      set(ProjectField.IGNORE_RELATIONSHIPS_TO_AND_FROM_OTHER_PROJECTS, value);
   }

   /**
    * Retrieve the mark open-ended activities as critical flag.
    *
    * @return mark open-ended activities as critical flag
    */
   public boolean getMakeOpenEndedActivitiesCritical()
   {
      return BooleanHelper.getBoolean((Boolean) get(ProjectField.MAKE_OPEN_ENDED_ACTIVITIES_CRITICAL));
   }

   /**
    * Set the mark open-ended activities as critical flag.
    *
    * @param value mark open-ended activities as critical flag
    */
   public void setMakeOpenEndedActivitiesCritical(boolean value)
   {
      set(ProjectField.MAKE_OPEN_ENDED_ACTIVITIES_CRITICAL, value);
   }

   /**
    * Retrieve the use expected finish dates flag.
    *
    * @return use expected finish dates flag
    */
   public boolean getUseExpectedFinishDates()
   {
      return BooleanHelper.getBoolean((Boolean) get(ProjectField.USE_EXPECTED_FINISH_DATES));
   }

   /**
    * Set the use expected finish dates flag.
    *
    * @param value use expected finish dates flag
    */
   public void setUseExpectedFinishDates(boolean value)
   {
      set(ProjectField.USE_EXPECTED_FINISH_DATES, value);
   }

   /**
    * Retrieve the compute start to start lag from early start flag.
    *
    * @return start to start lag from early start flag
    */
   public boolean getComputeStartToStartLagFromEarlyStart()
   {
      return BooleanHelper.getBoolean((Boolean) get(ProjectField.COMPUTE_START_TO_START_LAG_FROM_EARLY_START));
   }

   /**
    * Set the compute start to start lag from early start flag.
    *
    * @param value compute start to start lag from early start flag
    */
   public void setComputeStartToStartLagFromEarlyStart(boolean value)
   {
      set(ProjectField.COMPUTE_START_TO_START_LAG_FROM_EARLY_START, value);
   }

   /**
    * Set the calculate float based on finish date of each project flag.
    *
    * @return calculate float based on finish date of each project flag
    */
   public boolean getCalculateFloatBasedOnFinishDateOfEachProject()
   {
      return BooleanHelper.getBoolean((Boolean) get(ProjectField.CALCULATE_FLOAT_BASED_ON_FINISH_DATE_OF_EACH_PROJECT));
   }

   /**
    * Set the calculate float based on finish date of each project flag.
    *
    * @param value calculate float based on finish date of each project flag
    */
   public void setCalculateFloatBasedOnFinishDateOfEachProject(boolean value)
   {
      set(ProjectField.CALCULATE_FLOAT_BASED_ON_FINISH_DATE_OF_EACH_PROJECT, value);
   }

   /**
    * Get the calculate multiple float paths flag.
    *
    * @return calculate multiple float paths flag
    */
   public boolean getCalculateMultipleFloatPaths()
   {
      return BooleanHelper.getBoolean((Boolean) get(ProjectField.CALCULATE_MULTIPLE_FLOAT_PATHS));
   }

   /**
    * Set the calculate multiple float paths flag.
    *
    * @param value calculate multiple float paths flag
    */
   public void setCalculateMultipleFloatPaths(boolean value)
   {
      set(ProjectField.CALCULATE_MULTIPLE_FLOAT_PATHS, value);
   }

   /**
    * Retrieve the calculate multiple float paths using total float flag.
    *
    * @return calculate multiple float paths using total float flag
    */
   public boolean getCalculateMultipleFloatPathsUsingTotalFloat()
   {
      return BooleanHelper.getBoolean((Boolean) get(ProjectField.CALCULATE_MULTIPLE_FLOAT_PATHS_USING_TOTAL_FLOAT));
   }

   /**
    * Set the calculate multiple float paths using total float flag.
    *
    * @param value calculate multiple float paths using total float flag
    */
   public void setCalculateMultipleFloatPathsUsingTotalFloat(boolean value)
   {
      set(ProjectField.CALCULATE_MULTIPLE_FLOAT_PATHS_USING_TOTAL_FLOAT, value);
   }

   /**
    * Retrieve the display multiple float paths ending with activity unique ID value.
    *
    * @return display multiple float paths ending with activity unique ID value
    */
   public Integer getDisplayMultipleFloatPathsEndingWithActivityUniqueID()
   {
      return (Integer) get(ProjectField.DISPLAY_MULTIPLE_FLOAT_PATHS_ENDING_WITH_ACTIVITY_UNIQUE_ID);
   }

   /**
    * Set the display multiple float paths ending with activity unique ID value.
    *
    * @param value display multiple float paths ending with activity unique ID value
    */
   public void setDisplayMultipleFloatPathsEndingWithActivityUniqueID(Integer value)
   {
      set(ProjectField.DISPLAY_MULTIPLE_FLOAT_PATHS_ENDING_WITH_ACTIVITY_UNIQUE_ID, value);
   }

   /**
    * Retrieve the limit number of paths to calculate flag.
    *
    * @return limit number of paths to calculate flag
    */
   public boolean getLimitNumberOfFloatPathsToCalculate()
   {
      return BooleanHelper.getBoolean((Boolean) get(ProjectField.LIMIT_NUMBER_OF_FLOAT_PATHS_TO_CALCULATE));
   }

   /**
    * Set the limit number of paths to calculate flag.
    *
    * @param value limit number of paths to calculate flag
    */
   public void setLimitNumberOfFloatPathsToCalculate(boolean value)
   {
      set(ProjectField.LIMIT_NUMBER_OF_FLOAT_PATHS_TO_CALCULATE, value);
   }

   /**
    * Retrieve the maximum number of float paths to calculate.
    *
    * @return maximum number of float paths to calculate.
    */
   public Integer getMaximumNumberOfFloatPathsToCalculate()
   {
      return (Integer) get(ProjectField.MAXIMUM_NUMBER_OF_FLOAT_PATHS_TO_CALCULATE);
   }

   /**
    * Set the method used when scheduling progressed activities.
    *
    * @param value scheduling progressed activities method
    */
   public void setSchedulingProgressedActivities(SchedulingProgressedActivities value)
   {
      set(ProjectField.SCHEDULING_PROGRESSED_ACTIVITIES, value);
   }

   /**
    * Retrieve the method used when scheduling progressed activities.
    *
    * @return maximum number of float paths to calculate.
    */
   public SchedulingProgressedActivities getSchedulingProgressedActivities()
   {
      return (SchedulingProgressedActivities) get(ProjectField.SCHEDULING_PROGRESSED_ACTIVITIES);
   }

   /**
    * Set the maximum number of float paths to calculate.
    *
    * @param value maximum number of float paths to calculate
    */
   public void setMaximumNumberOfFloatPathsToCalculate(Integer value)
   {
      set(ProjectField.MAXIMUM_NUMBER_OF_FLOAT_PATHS_TO_CALCULATE, value);
   }

   /**
    * Retrieve the name of the baseline type associated with this project.
    *
    * @return baseline type name
    */
   public String getBaselineTypeName()
   {
      return (String) get(ProjectField.BASELINE_TYPE_NAME);
   }

   /**
    * Set the name of the baseline type associated with this project.
    *
    * @param value baseline type name
    */
   public void setBaselineTypeName(String value)
   {
      set(ProjectField.BASELINE_TYPE_NAME, value);
   }

   /**
    * Retrieve the unique ID of the baseline type associated with this project.
    *
    * @return baseline type unique ID
    */
   public Integer getBaselineTypeUniqueID()
   {
      return (Integer) get(ProjectField.BASELINE_TYPE_UNIQUE_ID);
   }

   /**
    * Set the unique ID of the baseline type associated with this project.
    *
    * @param value baseline type unique ID
    */
   public void setBaselineTypeUniqueID(Integer value)
   {
      set(ProjectField.BASELINE_TYPE_UNIQUE_ID, value);
   }

   /**
    * Retrieve the last baseline update date.
    *
    * @return last baseline update date
    */
   public LocalDateTime getLastBaselineUpdateDate()
   {
      return (LocalDateTime) get(ProjectField.LAST_BASELINE_UPDATE_DATE);
   }

   /**
    * Set the last baseline update date.
    *
    * @param value last baseline update date
    */
   public void setLastBaselineUpdateDate(LocalDateTime value)
   {
      set(ProjectField.LAST_BASELINE_UPDATE_DATE, value);
   }

   /**
    * Retrieve the prefix used when creating an Activity ID.
    *
    * @return activity ID prefix
    */
   public String getActivityIdPrefix()
   {
      return (String) get(ProjectField.ACTIVITY_ID_PREFIX);
   }

   /**
    * Set the prefix used when creating an Activity ID.
    *
    * @param value activity ID prefix
    */
   public void setActivityIdPrefix(String value)
   {
      set(ProjectField.ACTIVITY_ID_PREFIX, value);
   }

   /**
    * Retrieve the suffix used when creating an Activity ID.
    *
    * @return activity ID suffix
    */
   public Integer getActivityIdSuffix()
   {
      return (Integer) get(ProjectField.ACTIVITY_ID_SUFFIX);
   }

   /**
    * Set the suffix used when creating an Activity ID.
    *
    * @param value activity ID suffix
    */
   public void setActivityIdSuffix(Integer value)
   {
      set(ProjectField.ACTIVITY_ID_SUFFIX, value);
   }

   /**
    * Retrieve the increment used when creating Activity ID values.
    *
    * @return activity ID increment
    */
   public Integer getActivityIdIncrement()
   {
      return (Integer) get(ProjectField.ACTIVITY_ID_INCREMENT);
   }

   /**
    * Set the increment used when creating Activity ID values.
    *
    * @param value activity ID increment
    */
   public void setActivityIdIncrement(Integer value)
   {
      set(ProjectField.ACTIVITY_ID_INCREMENT, value);
   }

   /**
    * Retrieve the "increment activity ID based on selected activity" flag.
    *
    * @return "increment activity ID based on selected activity" flag
    */
   public boolean getActivityIdIncrementBasedOnSelectedActivity()
   {
      return BooleanHelper.getBoolean((Boolean) get(ProjectField.ACTIVITY_ID_INCREMENT_BASED_ON_SELECTED_ACTIVITY));
   }

   /**
    * Set the "increment activity ID based on selected activity" flag.
    *
    * @param value "increment activity ID based on selected activity" flag
    */
   public void setActivityIdIncrementBasedOnSelectedActivity(boolean value)
   {
      set(ProjectField.ACTIVITY_ID_INCREMENT_BASED_ON_SELECTED_ACTIVITY, value);
   }

   /**
    * Set the baseline calendar name.
    *
    * @return baseline calendar name
    */
   public String getBaselineCalendarName()
   {
      return (String) get(ProjectField.BASELINE_CALENDAR_NAME);
   }

   /**
    * Retrieve the baseline calendar name.
    *
    * @param value baseline calendar name
    */
   public void setBaselineCalendarName(String value)
   {
      set(ProjectField.BASELINE_CALENDAR_NAME, value);
   }

   /**
    * Returns true if this ProjectFile instance represents a baseline.
    * This is useful where readers can return a list of all
    * schedules from a data source which may include both projects
    * and baselines.
    *
    * @return true if this ProjectFile instance represents a baseline
    */
   public boolean getProjectIsBaseline()
   {
      return BooleanHelper.getBoolean((Boolean) get(ProjectField.PROJECT_IS_BASELINE));
   }

   /**
    * Set the flag to indicate if this ProjectFile instance
    * represents a baseline.
    *
    * @param value true if this ProjectFile instance represents a baseline
    */
   public void setProjectIsBaseline(boolean value)
   {
      set(ProjectField.PROJECT_IS_BASELINE, value);
   }

   /**
    * Retrieve the project website URL.
    *
    * @return project website URL
    */
   public String getProjectWebsiteUrl()
   {
      return (String) get(ProjectField.PROJECT_WEBSITE_URL);
   }

   /**
    * Retrieve the project notes.
    *
    * @return project notes
    */
   public String getNotes()
   {
      Object notes = get(TaskField.NOTES);
      return notes == null ? "" : notes.toString();
   }

   /**
    * Set the project notes.
    *
    * @param notes project notes
    */
   public void setNotes(String notes)
   {
      set(TaskField.NOTES, notes == null ? null : new Notes(notes));
   }

   /**
    * Retrieve the project notes object.
    *
    * @return project notes object
    */
   public Notes getNotesObject()
   {
      return (Notes) get(TaskField.NOTES);
   }

   /**
    * Set the project notes object.
    *
    * @param notes project notes object
    */
   public void setNotesObject(Notes notes)
   {
      set(TaskField.NOTES, notes);
   }

   /**
    * Set the project website URL.
    *
    * @param value project website url
    */
   public void setProjectWebsiteUrl(String value)
   {
      set(ProjectField.PROJECT_WEBSITE_URL, value);
   }

   /**
    * Retrieve the project code values associated with this project.
    *
    * @return map of project code values
    */
   @SuppressWarnings("unchecked") public Map<ProjectCode, ProjectCodeValue> getProjectCodeValues()
   {
      return (Map<ProjectCode, ProjectCodeValue>) get(ProjectField.PROJECT_CODE_VALUES);
   }

   /**
    * Assign a project code value to this project.
    *
    * @param value project code value
    */
   @SuppressWarnings("unchecked") public void addProjectCodeValue(ProjectCodeValue value)
   {
      ((Map<ProjectCode, ProjectCodeValue>) get(ProjectField.PROJECT_CODE_VALUES)).put(value.getParentCode(), value);
   }

   /**
    * Retrieve the enable publication flag.
    *
    * @return enable publication flag
    */
   public boolean getEnablePublication()
   {
      return BooleanHelper.getBoolean((Boolean) get(ProjectField.ENABLE_PUBLICATION));
   }

   /**
    * Set the enable publication flag.
    *
    * @param value enable publication flag
    */
   public void setEnablePublication(boolean value)
   {
      set(ProjectField.ENABLE_PUBLICATION, value);
   }

   /**
    * Retrieve the enable summarization flag.
    *
    * @return enable summarization flag
    */
   public boolean getEnableSummarization()
   {
      return BooleanHelper.getBoolean((Boolean) get(ProjectField.ENABLE_SUMMARIZATION));
   }

   /**
    * Set the enable summarization flg.
    *
    * @param value enable summarization flag
    */
   public void setEnableSummarization(boolean value)
   {
      set(ProjectField.ENABLE_SUMMARIZATION, value);
   }

   /**
    * Maps a field index to a ProjectField instance.
    *
    * @param fields array of fields used as the basis for the mapping.
    * @param index required field index
    * @return ProjectField instance
    */
   private ProjectField selectField(ProjectField[] fields, int index)
   {
      if (index < 1 || index > fields.length)
      {
         throw new IllegalArgumentException(index + " is not a valid field index");
      }
      return (fields[index - 1]);
   }

   @Override void handleFieldChange(FieldType field, Object oldValue, Object newValue)
   {
      // No action required
   }

   @Override boolean getAlwaysCalculatedField(FieldType field)
   {
      return false;
   }

   @Override Function<ProjectProperties, Object> getCalculationMethod(FieldType field)
   {
      return CALCULATED_FIELD_MAP.get(field);
   }

   /**
    * Retrieve the set of populated fields for this project.
    *
    * @return set of populated fields
    */
   public Set<FieldType> getPopulatedFields()
   {
      return new PopulatedFields<>(getParentFile(), ProjectField.class, getParentFile().getUserDefinedFields().getProjectFields(), Collections.singletonList(this)).getPopulatedFields();
   }

   /**
    * This method inserts a name value pair into internal storage.
    *
    * @param field task field
    * @param value attribute value
    */
   private void set(FieldType field, boolean value)
   {
      set(field, (value ? Boolean.TRUE : Boolean.FALSE));
   }

   private LocalDateTime calculateStartDate()
   {
      return getParentFile().getEarliestStartDate();
   }

   private LocalDateTime calculateFinishDate()
   {
      return getParentFile().getLatestFinishDate();
   }

   private LocalDateTime calculateActualStart()
   {
      return getParentFile().getActualStart();
   }

   private LocalDateTime calculateActualFinish()
   {
      return getParentFile().getActualFinish();
   }

   private Integer calculateMinutesPerWeek()
   {
      return Integer.valueOf(DEFAULT_DAYS_PER_WEEK * NumberHelper.getInt(getMinutesPerDay()));
   }

   private Integer calculateMinutesPerMonth()
   {
      return Integer.valueOf(NumberHelper.getInt(getMinutesPerDay()) * NumberHelper.getInt(getDaysPerMonth()));
   }

   private Integer calculateMinutesPerYear()
   {
      return Integer.valueOf(NumberHelper.getInt(getMinutesPerDay()) * NumberHelper.getInt(getDaysPerMonth()) * 12);
   }

   /**
    * Default time separator character.
    */
   private static final char DEFAULT_TIME_SEPARATOR = ':';

   /**
    * Default date separator character.
    */
   private static final char DEFAULT_DATE_SEPARATOR = '/';

   /**
    * Default thousands separator character.
    */
   private static final char DEFAULT_THOUSANDS_SEPARATOR = ',';

   /**
    * Default decimal separator character.
    */
   private static final char DEFAULT_DECIMAL_SEPARATOR = '.';

   /**
    * Default currency symbol.
    */
   private static final String DEFAULT_CURRENCY_SYMBOL = "$";

   /**
    * Default currency digits.
    */
   private static final Integer DEFAULT_CURRENCY_DIGITS = Integer.valueOf(2);

   /**
    * Default currency symbol position.
    */
   private static final CurrencySymbolPosition DEFAULT_CURRENCY_SYMBOL_POSITION = CurrencySymbolPosition.BEFORE;

   /**
    * Default cost value.
    */
   private static final Double DEFAULT_COST = Double.valueOf(0);

   /**
    * Default MPX delimiter.
    */
   private static final char DEFAULT_MPX_DELIMITER = ',';

   /**
    * Default critical slack limit.
    */
   private static final Duration DEFAULT_CRITICAL_SLACK_LIMIT = Duration.getInstance(0, TimeUnit.DAYS);

   /**
    * Default baseline for earned value.
    */
   private static final Integer DEFAULT_BASELINE_FOR_EARNED_VALUE = Integer.valueOf(0);

   /**
    * Default fiscal year start month.
    */
   private static final Integer DEFAULT_FISCAL_YEAR_START_MONTH = Integer.valueOf(1);

   /**
    * Default week start day.
    */
   private static final DayOfWeek DEFAULT_WEEK_START_DAY = DayOfWeek.MONDAY;

   /**
    * Default work value.
    */
   private static final Duration DEFAULT_WORK = Duration.getInstance(0, TimeUnit.HOURS);

   /**
    * Default work 2 value.
    */
   private static final Double DEFAULT_WORK2 = Double.valueOf(0);

   /**
    * Default duration value.
    */
   private static final Duration DEFAULT_DURATION = Duration.getInstance(0, TimeUnit.DAYS);

   /**
    * Default schedule from value.
    */
   private static final ScheduleFrom DEFAULT_SCHEDULE_FROM = ScheduleFrom.START;

   /**
    * Default percent complete value.
    */
   private static final Double DEFAULT_PERCENT_COMPLETE = Double.valueOf(0);

   /**
    * Default days per week.
    */
   private static final int DEFAULT_DAYS_PER_WEEK = 5;

   /**
    * Default days per month.
    */
   private static final Integer DEFAULT_DAYS_PER_MONTH = Integer.valueOf(20);

   /**
    * Default minutes per day.
    */
   private static final Integer DEFAULT_MINUTES_PER_DAY = Integer.valueOf(480);

   /**
    * Default minutes per week.
    */
   private static final Integer DEFAULT_MINUTES_PER_WEEK = Integer.valueOf(2400);

   private static final Integer DEFAULT_OTHER_PROJECT_ASSIGNMENT_PRIORITY = Integer.valueOf(5);

   private static final Duration DEFAULT_MINIMUM_FLOAT = Duration.getInstance(1, TimeUnit.HOURS);

   private static final Double DEFAULT_OVERALLOCATION = Double.valueOf(25.0);

   private static final Integer DEFAULT_FLOAT_PATHS = Integer.valueOf(10);

   private static final Map<FieldType, Function<ProjectProperties, Object>> CALCULATED_FIELD_MAP = new HashMap<>();
   static
   {
      CALCULATED_FIELD_MAP.put(ProjectField.START_DATE, ProjectProperties::calculateStartDate);
      CALCULATED_FIELD_MAP.put(ProjectField.FINISH_DATE, ProjectProperties::calculateFinishDate);
      CALCULATED_FIELD_MAP.put(ProjectField.ACTUAL_START, ProjectProperties::calculateActualStart);
      CALCULATED_FIELD_MAP.put(ProjectField.ACTUAL_FINISH, ProjectProperties::calculateActualFinish);
      CALCULATED_FIELD_MAP.put(ProjectField.MINUTES_PER_WEEK, ProjectProperties::calculateMinutesPerWeek);
      CALCULATED_FIELD_MAP.put(ProjectField.MINUTES_PER_MONTH, ProjectProperties::calculateMinutesPerMonth);
      CALCULATED_FIELD_MAP.put(ProjectField.MINUTES_PER_YEAR, ProjectProperties::calculateMinutesPerYear);

      CALCULATED_FIELD_MAP.put(ProjectField.DAYS_PER_MONTH, p -> DEFAULT_DAYS_PER_MONTH);
      CALCULATED_FIELD_MAP.put(ProjectField.MINUTES_PER_DAY, p -> DEFAULT_MINUTES_PER_DAY);
      CALCULATED_FIELD_MAP.put(ProjectField.DATE_SEPARATOR, p -> Character.valueOf(DEFAULT_DATE_SEPARATOR));
      CALCULATED_FIELD_MAP.put(ProjectField.TIME_SEPARATOR, p -> Character.valueOf(DEFAULT_TIME_SEPARATOR));
      CALCULATED_FIELD_MAP.put(ProjectField.THOUSANDS_SEPARATOR, p -> Character.valueOf(DEFAULT_THOUSANDS_SEPARATOR));
      CALCULATED_FIELD_MAP.put(ProjectField.DECIMAL_SEPARATOR, p -> Character.valueOf(DEFAULT_DECIMAL_SEPARATOR));
      CALCULATED_FIELD_MAP.put(ProjectField.MPX_DELIMITER, p -> Character.valueOf(DEFAULT_MPX_DELIMITER));
      CALCULATED_FIELD_MAP.put(ProjectField.CUSTOM_PROPERTIES, p -> new HashMap<String, Object>());
      CALCULATED_FIELD_MAP.put(ProjectField.WBS_CODE_SEPARATOR, p -> ".");
      CALCULATED_FIELD_MAP.put(ProjectField.CONSIDER_ASSIGNMENTS_IN_OTHER_PROJECTS, p -> Boolean.FALSE);
      CALCULATED_FIELD_MAP.put(ProjectField.CONSIDER_ASSIGNMENTS_IN_OTHER_PROJECTS_WITH_PRIORITY_EQUAL_HIGHER_THAN, p -> DEFAULT_OTHER_PROJECT_ASSIGNMENT_PRIORITY);
      CALCULATED_FIELD_MAP.put(ProjectField.PRESERVE_SCHEDULED_EARLY_AND_LATE_DATES, p -> Boolean.TRUE);
      CALCULATED_FIELD_MAP.put(ProjectField.LEVEL_ALL_RESOURCES, p -> Boolean.TRUE);
      CALCULATED_FIELD_MAP.put(ProjectField.LEVEL_RESOURCES_ONLY_WITHIN_ACTIVITY_TOTAL_FLOAT, p -> Boolean.FALSE);
      CALCULATED_FIELD_MAP.put(ProjectField.PRESERVE_MINIMUM_FLOAT_WHEN_LEVELING, p -> DEFAULT_MINIMUM_FLOAT);
      CALCULATED_FIELD_MAP.put(ProjectField.MAX_PERCENT_TO_OVERALLOCATE_RESOURCES, p -> DEFAULT_OVERALLOCATION);
      CALCULATED_FIELD_MAP.put(ProjectField.LEVELING_PRIORITIES, p -> "(0||priority_type(sort_type|ASC)())");
      CALCULATED_FIELD_MAP.put(ProjectField.DATA_DATE_AND_PLANNED_START_SET_TO_PROJECT_FORECAST_START, p -> Boolean.FALSE);
      CALCULATED_FIELD_MAP.put(ProjectField.IGNORE_RELATIONSHIPS_TO_AND_FROM_OTHER_PROJECTS, p -> Boolean.FALSE);
      CALCULATED_FIELD_MAP.put(ProjectField.MAKE_OPEN_ENDED_ACTIVITIES_CRITICAL, p -> Boolean.FALSE);
      CALCULATED_FIELD_MAP.put(ProjectField.USE_EXPECTED_FINISH_DATES, p -> Boolean.TRUE);
      CALCULATED_FIELD_MAP.put(ProjectField.COMPUTE_START_TO_START_LAG_FROM_EARLY_START, p -> Boolean.TRUE);
      CALCULATED_FIELD_MAP.put(ProjectField.CALCULATE_FLOAT_BASED_ON_FINISH_DATE_OF_EACH_PROJECT, p -> Boolean.TRUE);
      CALCULATED_FIELD_MAP.put(ProjectField.CALCULATE_MULTIPLE_FLOAT_PATHS, p -> Boolean.FALSE);
      CALCULATED_FIELD_MAP.put(ProjectField.CALCULATE_MULTIPLE_FLOAT_PATHS_USING_TOTAL_FLOAT, p -> Boolean.TRUE);
      CALCULATED_FIELD_MAP.put(ProjectField.LIMIT_NUMBER_OF_FLOAT_PATHS_TO_CALCULATE, p -> Boolean.TRUE);
      CALCULATED_FIELD_MAP.put(ProjectField.MAXIMUM_NUMBER_OF_FLOAT_PATHS_TO_CALCULATE, p -> DEFAULT_FLOAT_PATHS);
      CALCULATED_FIELD_MAP.put(ProjectField.SCHEDULING_PROGRESSED_ACTIVITIES, p -> SchedulingProgressedActivities.RETAINED_LOGIC);
      CALCULATED_FIELD_MAP.put(ProjectField.ACTIVITY_ID_PREFIX, p -> "A");
      CALCULATED_FIELD_MAP.put(ProjectField.ACTIVITY_ID_SUFFIX, p -> Integer.valueOf(1000));
      CALCULATED_FIELD_MAP.put(ProjectField.ACTIVITY_ID_INCREMENT, p -> Integer.valueOf(10));
      CALCULATED_FIELD_MAP.put(ProjectField.ACTIVITY_ID_INCREMENT_BASED_ON_SELECTED_ACTIVITY, p -> Boolean.TRUE);
      CALCULATED_FIELD_MAP.put(ProjectField.PROJECT_IS_BASELINE, p -> Boolean.FALSE);
      CALCULATED_FIELD_MAP.put(ProjectField.PROJECT_CODE_VALUES, p -> new HashMap<>());
   }
}
