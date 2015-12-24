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

package net.sf.mpxj;

import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import net.sf.mpxj.common.BooleanHelper;
import net.sf.mpxj.common.DateHelper;
import net.sf.mpxj.common.NumberHelper;
import net.sf.mpxj.common.ProjectFieldLists;
import net.sf.mpxj.listener.FieldListener;

/**
 * This class represents a collection of properties relevant to the whole project.
 */
public final class ProjectProperties extends ProjectEntity implements FieldContainer
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
      setSymbolPosition(CurrencySymbolPosition.BEFORE);
      setCurrencyDigits(Integer.valueOf(2));
      setThousandsSeparator(DEFAULT_THOUSANDS_SEPARATOR);
      setDecimalSeparator(DEFAULT_DECIMAL_SEPARATOR);

      setDateOrder(DateOrder.DMY);
      setTimeFormat(ProjectTimeFormat.TWELVE_HOUR);
      setDefaultStartTime(DateHelper.getTimeFromMinutesPastMidnight(Integer.valueOf(480)));
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
      setMinutesPerDay(Integer.valueOf(480));
      setMinutesPerWeek(Integer.valueOf(2400));
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
      setDefaultCalendarName(DEFAULT_CALENDAR_NAME);
      setStartDate(null);
      setFinishDate(null);
      setScheduleFrom(DEFAULT_SCHEDULE_FROM);
      setCurrentDate(new Date());
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
      setWeekStartDay(DEFAULT_WEEK_START_DAY);
   }

   /**
    * Gets Default Duration units. The constants used to define the
    * duration units are defined by the <code>TimeUnit</code> class.
    *
    * @return default duration units
    * @see TimeUnit
    */
   public TimeUnit getDefaultDurationUnits()
   {
      return (TimeUnit) getCachedValue(ProjectField.DEFAULT_DURATION_UNITS);
   }

   /**
    * Default duration units. The constants used to define the
    * duration units are defined by the <code>TimeUnit</code> class.
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
      return BooleanHelper.getBoolean((Boolean) getCachedValue(ProjectField.DEFAULT_DURATION_IS_FIXED));
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
    * work units are defined by the <code>TimeUnit</code> class.
    *
    * @return default work units
    * @see TimeUnit
    */
   public TimeUnit getDefaultWorkUnits()
   {
      return (TimeUnit) getCachedValue(ProjectField.DEFAULT_WORK_UNITS);
   }

   /**
    * Default work units. The constants used to define the
    * work units are defined by the <code>TimeUnit</code> class.
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
      return (Rate) getCachedValue(ProjectField.DEFAULT_STANDARD_RATE);
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
      return (Rate) getCachedValue(ProjectField.DEFAULT_OVERTIME_RATE);
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
      return BooleanHelper.getBoolean((Boolean) getCachedValue(ProjectField.UPDATING_TASK_STATUS_UPDATES_RESOURCE_STATUS));
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
    * Flag representing whether or not to split in-progress tasks.
    *
    * @return Boolean value
    */
   public boolean getSplitInProgressTasks()
   {
      return BooleanHelper.getBoolean((Boolean) getCachedValue(ProjectField.SPLIT_IN_PROGRESS_TASKS));
   }

   /**
    * Flag representing whether or not to split in-progress tasks.
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
      return (DateOrder) getCachedValue(ProjectField.DATE_ORDER);
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
      return (ProjectTimeFormat) getCachedValue(ProjectField.TIME_FORMAT);
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
   public Date getDefaultStartTime()
   {
      return (Date) getCachedValue(ProjectField.DEFAULT_START_TIME);
   }

   /**
    * Set the default start time, specified using the Java Date type.
    * Note that this assumes that the value returned from
    * the getTime method starts at zero... i.e. the date part
    * of the date/time value has not been set.
    *
    * @param defaultStartTime default time
    */
   public void setDefaultStartTime(Date defaultStartTime)
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
      return getCachedCharValue(ProjectField.DATE_SEPARATOR, DEFAULT_DATE_SEPARATOR);
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
      return getCachedCharValue(ProjectField.TIME_SEPARATOR, DEFAULT_TIME_SEPARATOR);
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
      return (String) getCachedValue(ProjectField.AM_TEXT);
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
      return (String) getCachedValue(ProjectField.PM_TEXT);
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
      return (ProjectDateFormat) getCachedValue(ProjectField.DATE_FORMAT);
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
      return (ProjectDateFormat) getCachedValue(ProjectField.BAR_TEXT_DATE_FORMAT);
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
   public Date getDefaultEndTime()
   {
      return (Date) getCachedValue(ProjectField.DEFAULT_END_TIME);
   }

   /**
    * Sets the default end time.
    *
    * @param date End time
    */
   public void setDefaultEndTime(Date date)
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
      return (String) getCachedValue(ProjectField.PROJECT_TITLE);
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
      return (String) getCachedValue(ProjectField.COMPANY);
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
      return (String) getCachedValue(ProjectField.MANAGER);
   }

   /**
    * Sets the Calendar used. 'Standard' if no value is set.
    *
    * @param calendarName Calendar name
    */
   public void setDefaultCalendarName(String calendarName)
   {
      if (calendarName == null || calendarName.length() == 0)
      {
         calendarName = DEFAULT_CALENDAR_NAME;
      }

      set(ProjectField.DEFAULT_CALENDAR_NAME, calendarName);
   }

   /**
    * Gets the Calendar used. 'Standard' if no value is set.
    *
    * @return Calendar name
    */
   public String getDefaultCalendarName()
   {
      return (String) getCachedValue(ProjectField.DEFAULT_CALENDAR_NAME);
   }

   /**
    * Sets the project start date.
    *
    * @param startDate project start date
    */
   public void setStartDate(Date startDate)
   {
      set(ProjectField.START_DATE, startDate);
   }

   /**
    * Retrieves the project start date. If an explicit start date has not been
    * set, this method calculates the start date by looking for
    * the earliest task start date.
    *
    * @return project start date
    */
   public Date getStartDate()
   {
      Date result = (Date) getCachedValue(ProjectField.START_DATE);
      if (result == null)
      {
         result = getParentFile().getStartDate();
      }
      return (result);
   }

   /**
    * Retrieves the project finish date. If an explicit finish date has not been
    * set, this method calculates the finish date by looking for
    * the latest task finish date.
    *
    * @return Finish Date
    */
   public Date getFinishDate()
   {
      Date result = (Date) getCachedValue(ProjectField.FINISH_DATE);
      if (result == null)
      {
         result = getParentFile().getFinishDate();
      }
      return (result);
   }

   /**
    * Sets the project finish date.
    *
    * @param finishDate project finish date
    */
   public void setFinishDate(Date finishDate)
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
      return (ScheduleFrom) getCachedValue(ProjectField.SCHEDULE_FROM);
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
   public Date getCurrentDate()
   {
      return (Date) getCachedValue(ProjectField.CURRENT_DATE);
   }

   /**
    * Sets the current date.
    *
    * @param currentDate current date
    */
   public void setCurrentDate(Date currentDate)
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
      return (String) getCachedValue(ProjectField.COMMENTS);
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
      return (Number) getCachedValue(ProjectField.COST);
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
      return (Number) getCachedValue(ProjectField.BASELINE_COST);
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
      return (Number) getCachedValue(ProjectField.ACTUAL_COST);
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
      return (Duration) getCachedValue(ProjectField.WORK);
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
      return (Duration) getCachedValue(ProjectField.BASELINE_WORK);
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
      return (Duration) getCachedValue(ProjectField.ACTUAL_WORK);
   }

   /**
    * Retrieves the project's "Work 2" attribute.
    *
    * @return Work 2 attribute
    */
   public Number getWork2()
   {
      return (Number) getCachedValue(ProjectField.WORK2);
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
      return (Duration) getCachedValue(ProjectField.DURATION);
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
      return (Duration) getCachedValue(ProjectField.BASELINE_DURATION);
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
      return (Duration) getCachedValue(ProjectField.ACTUAL_DURATION);
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
      return (Number) getCachedValue(ProjectField.PERCENTAGE_COMPLETE);
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
   public void setBaselineStart(Date baselineStartDate)
   {
      set(ProjectField.BASELINE_START, baselineStartDate);
   }

   /**
    * Retrieves the baseline project start date.
    *
    * @return baseline project start date
    */
   public Date getBaselineStart()
   {
      return (Date) getCachedValue(ProjectField.BASELINE_START);
   }

   /**
    * Sets the baseline project finish date.
    *
    * @param baselineFinishDate baseline project finish date
    */
   public void setBaselineFinish(Date baselineFinishDate)
   {
      set(ProjectField.BASELINE_FINISH, baselineFinishDate);
   }

   /**
    * Retrieves the baseline project finish date.
    *
    * @return baseline project finish date
    */
   public Date getBaselineFinish()
   {
      return (Date) getCachedValue(ProjectField.BASELINE_FINISH);
   }

   /**
    * Sets the actual project start date.
    *
    * @param actualStartDate actual project start date
    */
   public void setActualStart(Date actualStartDate)
   {
      set(ProjectField.ACTUAL_START, actualStartDate);
   }

   /**
    * Retrieves the actual project start date.
    *
    * @return actual project start date
    */
   public Date getActualStart()
   {
      return (Date) getCachedValue(ProjectField.ACTUAL_START);
   }

   /**
    * Sets the actual project finish date.
    *
    * @param actualFinishDate actual project finish date
    */
   public void setActualFinish(Date actualFinishDate)
   {
      set(ProjectField.ACTUAL_FINISH, actualFinishDate);
   }

   /**
    * Retrieves the actual project finish date.
    *
    * @return actual project finish date
    */
   public Date getActualFinish()
   {
      return (Date) getCachedValue(ProjectField.ACTUAL_FINISH);
   }

   /**
    * Retrieves the start variance duration.
    *
    * @return start date variance
    */
   public Duration getStartVariance()
   {
      return (Duration) getCachedValue(ProjectField.START_VARIANCE);
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
      return (Duration) getCachedValue(ProjectField.FINISH_VARIANCE);
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
      return (String) getCachedValue(ProjectField.SUBJECT);
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
      return (String) getCachedValue(ProjectField.AUTHOR);
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
      return (String) getCachedValue(ProjectField.KEYWORDS);
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
      return (String) getCachedValue(ProjectField.CURRENCY_SYMBOL);
   }

   /**
    * Sets the position of the currency symbol.
    *
    * @param posn currency symbol position.
    */
   public void setSymbolPosition(CurrencySymbolPosition posn)
   {
      set(ProjectField.CURRENCY_SYMBOL_POSITION, posn);
   }

   /**
    * Retrieves a constant representing the position of the currency symbol.
    *
    * @return position
    */
   public CurrencySymbolPosition getSymbolPosition()
   {
      return (CurrencySymbolPosition) getCachedValue(ProjectField.CURRENCY_SYMBOL_POSITION);
   }

   /**
    * Sets no of currency digits.
    *
    * @param currDigs Available values, 0,1,2
    */
   public void setCurrencyDigits(Integer currDigs)
   {
      set(ProjectField.CURRENCY_DIGITS, currDigs);
   }

   /**
    * Gets no of currency digits.
    *
    * @return Available values, 0,1,2
    */
   public Integer getCurrencyDigits()
   {
      return (Integer) getCachedValue(ProjectField.CURRENCY_DIGITS);
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
      return getCachedCharValue(ProjectField.THOUSANDS_SEPARATOR, DEFAULT_THOUSANDS_SEPARATOR);
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
      return getCachedCharValue(ProjectField.DECIMAL_SEPARATOR, DEFAULT_DECIMAL_SEPARATOR);
   }

   /**
    * Retrieve the externally edited flag.
    *
    * @return externally edited flag
    */
   public boolean getProjectExternallyEdited()
   {
      return BooleanHelper.getBoolean((Boolean) getCachedValue(ProjectField.PROJECT_EXTERNALLY_EDITED));
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
      return (String) getCachedValue(ProjectField.CATEGORY);
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
   public Number getDaysPerMonth()
   {
      return (Number) getCachedValue(ProjectField.DAYS_PER_MONTH);
   }

   /**
    * Set the number of days per month.
    *
    * @param daysPerMonth days per month
    */
   public void setDaysPerMonth(Number daysPerMonth)
   {
      if (daysPerMonth != null)
      {
         set(ProjectField.DAYS_PER_MONTH, daysPerMonth);
      }
   }

   /**
    * Retrieve the number of minutes per day.
    *
    * @return minutes per day
    */
   public Number getMinutesPerDay()
   {
      return (Number) getCachedValue(ProjectField.MINUTES_PER_DAY);
   }

   /**
    * Set the number of minutes per day.
    *
    * @param minutesPerDay minutes per day
    */
   public void setMinutesPerDay(Number minutesPerDay)
   {
      if (minutesPerDay != null)
      {
         set(ProjectField.MINUTES_PER_DAY, minutesPerDay);
      }
   }

   /**
    * Retrieve the number of minutes per week.
    *
    * @return minutes per week
    */
   public Number getMinutesPerWeek()
   {
      return (Number) getCachedValue(ProjectField.MINUTES_PER_WEEK);
   }

   /**
    * Set the number of minutes per week.
    *
    * @param minutesPerWeek minutes per week
    */
   public void setMinutesPerWeek(Number minutesPerWeek)
   {
      if (minutesPerWeek != null)
      {
         set(ProjectField.MINUTES_PER_WEEK, minutesPerWeek);
      }
   }

   /**
    * Retrieve the default number of minutes per month.
    *
    * @return minutes per month
    */
   public Number getMinutesPerMonth()
   {
      return Integer.valueOf(NumberHelper.getInt(getMinutesPerDay()) * NumberHelper.getInt(getDaysPerMonth()));
   }

   /**
    * Retrieve the default number of minutes per year.
    *
    * @return minutes per year
    */
   public Number getMinutesPerYear()
   {
      return Integer.valueOf(NumberHelper.getInt(getMinutesPerDay()) * NumberHelper.getInt(getDaysPerMonth()) * 12);
   }

   /**
    * Retrieve the fiscal year start flag.
    *
    * @return fiscal year start flag
    */
   public boolean getFiscalYearStart()
   {
      return BooleanHelper.getBoolean((Boolean) getCachedValue(ProjectField.FISCAL_YEAR_START));
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
      return (EarnedValueMethod) getCachedValue(ProjectField.DEFAULT_TASK_EARNED_VALUE_METHOD);
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
      return BooleanHelper.getBoolean((Boolean) getCachedValue(ProjectField.REMOVE_FILE_PROPERTIES));
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
      return BooleanHelper.getBoolean((Boolean) getCachedValue(ProjectField.MOVE_COMPLETED_ENDS_BACK));
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
      return BooleanHelper.getBoolean((Boolean) getCachedValue(ProjectField.NEW_TASKS_ESTIMATED));
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
      return BooleanHelper.getBoolean((Boolean) getCachedValue(ProjectField.SPREAD_ACTUAL_COST));
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
      return BooleanHelper.getBoolean((Boolean) getCachedValue(ProjectField.MULTIPLE_CRITICAL_PATHS));
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
      return BooleanHelper.getBoolean((Boolean) getCachedValue(ProjectField.AUTO_ADD_NEW_RESOURCES_AND_TASKS));
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
   public Date getLastSaved()
   {
      return (Date) getCachedValue(ProjectField.LAST_SAVED);
   }

   /**
    * Set the last saved date.
    *
    * @param lastSaved last saved date
    */
   public void setLastSaved(Date lastSaved)
   {
      set(ProjectField.LAST_SAVED, lastSaved);
   }

   /**
    * Retrieve the status date.
    *
    * @return status date
    */
   public Date getStatusDate()
   {
      return (Date) getCachedValue(ProjectField.STATUS_DATE);
   }

   /**
    * Set the status date.
    *
    * @param statusDate status date
    */
   public void setStatusDate(Date statusDate)
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
      return BooleanHelper.getBoolean((Boolean) getCachedValue(ProjectField.MOVE_REMAINING_STARTS_BACK));
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
      return BooleanHelper.getBoolean((Boolean) getCachedValue(ProjectField.AUTO_LINK));
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
      return BooleanHelper.getBoolean((Boolean) getCachedValue(ProjectField.MICROSOFT_PROJECT_SERVER_URL));
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
      return BooleanHelper.getBoolean((Boolean) getCachedValue(ProjectField.HONOR_CONSTRAINTS));
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
      return BooleanHelper.getBoolean((Boolean) getCachedValue(ProjectField.ADMIN_PROJECT));
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
      return BooleanHelper.getBoolean((Boolean) getCachedValue(ProjectField.INSERTED_PROJECTS_LIKE_SUMMARY));
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
      return (String) getCachedValue(ProjectField.NAME);
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
      return BooleanHelper.getBoolean((Boolean) getCachedValue(ProjectField.SPREAD_PERCENT_COMPLETE));
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
      return BooleanHelper.getBoolean((Boolean) getCachedValue(ProjectField.MOVE_COMPLETED_ENDS_FORWARD));
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
      return BooleanHelper.getBoolean((Boolean) getCachedValue(ProjectField.EDITABLE_ACTUAL_COSTS));
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
   public String getUniqueID()
   {
      return (String) getCachedValue(ProjectField.UNIQUE_ID);
   }

   /**
    * Set the unique ID for this project.
    *
    * @param uniqueID unique ID
    */
   public void setUniqueID(String uniqueID)
   {
      set(ProjectField.UNIQUE_ID, uniqueID);
   }

   /**
    * Retrieve the project revision number.
    *
    * @return revision number
    */
   public Integer getRevision()
   {
      return (Integer) getCachedValue(ProjectField.REVISION);
   }

   /**
    * Retrieve the new tasks effort driven flag.
    *
    * @return new tasks effort driven flag
    */
   public boolean getNewTasksEffortDriven()
   {
      return BooleanHelper.getBoolean((Boolean) getCachedValue(ProjectField.NEW_TASKS_EFFORT_DRIVEN));
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
      return BooleanHelper.getBoolean((Boolean) getCachedValue(ProjectField.MOVE_REMAINING_STARTS_FORWARD));
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
      return BooleanHelper.getBoolean((Boolean) getCachedValue(ProjectField.ACTUALS_IN_SYNC));
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
      return (TaskType) getCachedValue(ProjectField.DEFAULT_TASK_TYPE);
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
      return (EarnedValueMethod) getCachedValue(ProjectField.EARNED_VALUE_METHOD);
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
   public Date getCreationDate()
   {
      return (Date) getCachedValue(ProjectField.CREATION_DATE);
   }

   /**
    * Set the project creation date.
    *
    * @param creationDate project creation date
    */
   public void setCreationDate(Date creationDate)
   {
      set(ProjectField.CREATION_DATE, creationDate);
   }

   /**
    * Retrieve the extended creation date.
    *
    * @return extended creation date
    */
   public Date getExtendedCreationDate()
   {
      return (Date) getCachedValue(ProjectField.EXTENDED_CREATION_DATE);
   }

   /**
    * Retrieve the default fixed cost accrual type.
    *
    * @return default fixed cost accrual type
    */
   public AccrueType getDefaultFixedCostAccrual()
   {
      return (AccrueType) getCachedValue(ProjectField.DEFAULT_FIXED_COST_ACCRUAL);
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
   public void setExtendedCreationDate(Date creationDate)
   {
      set(ProjectField.EXTENDED_CREATION_DATE, creationDate);
   }

   /**
    * Retrieve the critical slack limit.
    *
    * @return critical slack limit
    */
   public Integer getCriticalSlackLimit()
   {
      return (Integer) getCachedValue(ProjectField.CRITICAL_SLACK_LIMIT);
   }

   /**
    * Set the critical slack limit.
    *
    * @param criticalSlackLimit critical slack limit
    */
   public void setCriticalSlackLimit(Integer criticalSlackLimit)
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
      return (Integer) getCachedValue(ProjectField.BASELINE_FOR_EARNED_VALUE);
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
      return (Integer) getCachedValue(ProjectField.FISCAL_YEAR_START_MONTH);
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
      return BooleanHelper.getBoolean((Boolean) getCachedValue(ProjectField.NEW_TASK_START_IS_PROJECT_START));
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
    * Retrieve the week start day.
    *
    * @return week start day
    */
   public Day getWeekStartDay()
   {
      return (Day) getCachedValue(ProjectField.WEEK_START_DAY);
   }

   /**
    * Set the week start day.
    *
    * @param weekStartDay week start day
    */
   public void setWeekStartDay(Day weekStartDay)
   {
      set(ProjectField.WEEK_START_DAY, weekStartDay);
   }

   /**
    * Sets the calculate multiple critical paths flag.
    *
    * @param flag boolean flag
    */
   public void setCalculateMultipleCriticalPaths(boolean flag)
   {
      set(ProjectField.CALCULATE_MULTIPLE_CRITICAL_PATHS, flag);
   }

   /**
    * Retrieves the calculate multiple critical paths flag.
    *
    * @return boolean flag
    */
   public boolean getCalculateMultipleCriticalPaths()
   {
      return BooleanHelper.getBoolean((Boolean) getCachedValue(ProjectField.CALCULATE_MULTIPLE_CRITICAL_PATHS));
   }

   /**
    * Retrieve the currency code for this project.
    *
    * @return currency code
    */
   public String getCurrencyCode()
   {
      return (String) getCachedValue(ProjectField.CURRENCY_CODE);
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
      return (Map<String, Object>) getCachedValue(ProjectField.CUSTOM_PROPERTIES);
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
      return (String) getCachedValue(ProjectField.HYPERLINK_BASE);
   }

   /**
    * Retrieves the "show project summary task" flag.
    *
    * @return boolean flag
    */
   public boolean getShowProjectSummaryTask()
   {
      return BooleanHelper.getBoolean((Boolean) getCachedValue(ProjectField.SHOW_PROJECT_SUMMARY_TASK));
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
   public Date getBaselineDate()
   {
      return (Date) getCachedValue(ProjectField.BASELINE_DATE);
   }

   /**
    * Set a baseline value.
    *
    * @param value baseline value
    */
   public void setBaselineDate(Date value)
   {
      set(ProjectField.BASELINE_DATE, value);
   }

   /**
    * Retrieve a baseline value.
    *
    * @param baselineNumber baseline index (1-10)
    * @return baseline value
    */
   public Date getBaselineDate(int baselineNumber)
   {
      return (Date) getCachedValue(selectField(ProjectFieldLists.BASELINE_DATES, baselineNumber));
   }

   /**
    * Set a baseline value.
    *
    * @param baselineNumber baseline index (1-10)
    * @param value baseline value
    */
   public void setBaselineDate(int baselineNumber, Date value)
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
      return (String) getCachedValue(ProjectField.TEMPLATE);
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
      return (String) getCachedValue(ProjectField.LAST_AUTHOR);
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
   public Date getLastPrinted()
   {
      return (Date) getCachedValue(ProjectField.LASTPRINTED);
   }

   /**
    * Set the last printed property.
    *
    * @param lastPrinted property value
    */
   public void setLastPrinted(Date lastPrinted)
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
      return (String) getCachedValue(ProjectField.SHORT_APPLICATION_NAME);
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
      return (Integer) getCachedValue(ProjectField.EDITING_TIME);
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
      return (String) getCachedValue(ProjectField.PRESENTATION_FORMAT);
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
      return (String) getCachedValue(ProjectField.CONTENT_TYPE);
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
      return (String) getCachedValue(ProjectField.CONTENT_STATUS);
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
      return (String) getCachedValue(ProjectField.LANGUAGE);
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
      return (String) getCachedValue(ProjectField.DOCUMENT_VERSION);
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
      return getCachedCharValue(ProjectField.MPX_DELIMITER, DEFAULT_MPX_DELIMITER);
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
      return (String) getCachedValue(ProjectField.MPX_PROGRAM_NAME);
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
      return (FileVersion) getCachedValue(ProjectField.MPX_FILE_VERSION);
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
      return (CodePage) getCachedValue(ProjectField.MPX_CODE_PAGE);
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
      return (String) getCachedValue(ProjectField.PROJECT_FILE_PATH);
   }

   /**
    * Retrieves the name of the application used to create this project data.
    *
    * @return application name
    */
   public String getFullApplicationName()
   {
      return (String) getCachedValue(ProjectField.FULL_APPLICATION_NAME);
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
      return (Integer) getCachedValue(ProjectField.APPLICATION_VERSION);
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
    * that has been read. Currently this method will return the value 8 for
    * an MPP8 file (Project 98), 9 for an MPP9 file (Project 2000 and
    * Project 2002), 12 for an MPP12 file (Project 2003, Project 2007) and 14 for an
    * MPP14 file (Project 2010 and Project 2013).
    *
    * @return integer representing the file type
    */
   public Integer getMppFileType()
   {
      return (Integer) getCachedValue(ProjectField.MPP_FILE_TYPE);
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
      return BooleanHelper.getBoolean((Boolean) getCachedValue(ProjectField.AUTOFILTER));
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
    * {@inheritDoc}
    */
   @Override public void addFieldListener(FieldListener listener)
   {
      if (m_listeners == null)
      {
         m_listeners = new LinkedList<FieldListener>();
      }
      m_listeners.add(listener);
   }

   /**
    * {@inheritDoc}
    */
   @Override public void removeFieldListener(FieldListener listener)
   {
      if (m_listeners != null)
      {
         m_listeners.remove(listener);
      }
   }

   /**
    * Maps a field index to a TaskField instance.
    *
    * @param fields array of fields used as the basis for the mapping.
    * @param index required field index
    * @return TaskField instance
    */
   private ProjectField selectField(ProjectField[] fields, int index)
   {
      if (index < 1 || index > fields.length)
      {
         throw new IllegalArgumentException(index + " is not a valid field index");
      }
      return (fields[index - 1]);
   }

   /**
    * Handles retrieval of primitive char type.
    *
    * @param field required field
    * @param defaultValue default value if field is missing
    * @return char value
    */
   private char getCachedCharValue(FieldType field, char defaultValue)
   {
      Character c = (Character) getCachedValue(field);
      return c == null ? defaultValue : c.charValue();
   }

   /**
    * {@inheritDoc}
    */
   @Override public Object getCachedValue(FieldType field)
   {
      return (field == null ? null : m_array[field.getValue()]);
   }

   /**
    * {@inheritDoc}
    */
   @Override public Object getCurrentValue(FieldType field)
   {
      return (getCachedValue(field));
   }

   /**
    * {@inheritDoc}
    */
   @Override public void set(FieldType field, Object value)
   {
      if (field != null)
      {
         int index = field.getValue();
         m_array[index] = value;
      }
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

   /**
    * Array of field values.
    */
   private Object[] m_array = new Object[ProjectField.MAX_VALUE];

   /**
    * Listeners.
    */
   private List<FieldListener> m_listeners;

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
   private static final Integer DEFAULT_CRITICAL_SLACK_LIMIT = Integer.valueOf(0);

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
   private static final Day DEFAULT_WEEK_START_DAY = Day.MONDAY;

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
    * Default calendar name.
    */
   private static final String DEFAULT_CALENDAR_NAME = "Standard";

   /**
    * Default minutes per day.
    */
   private static final Integer DEFAULT_MINUTES_PER_DAY = Integer.valueOf(480);

   /**
    * Default days per month.
    */
   private static final Integer DEFAULT_DAYS_PER_MONTH = Integer.valueOf(20);

   /**
    * Default minutes per week.
    */
   private static final Integer DEFAULT_MINUTES_PER_WEEK = Integer.valueOf(2400);
}
