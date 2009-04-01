/*
 * file:       ProjectHeader.java
 * author:     Scott Melville
 *             Jon Iles
 * copyright:  (c) Packwood Software 2002-2003
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

import java.util.Calendar;
import java.util.Date;
import java.util.Map;

/**
 * This class represents the ProjectHeader record as found in an MPX
 * file. This record contains details of global settings relevant to the
 * project plan. Note that a number of the fields in this record are
 * calculated by Microsoft Project, and will therefore be ignored on import.
 */
public final class ProjectHeader extends ProjectEntity
{
   /**
    * Default constructor.
    *
    * @param file the parent file to which this record belongs.
    */
   ProjectHeader(ProjectFile file)
   {
      super(file);

      //
      // Configure Date Time Settings and Currency Settings Records
      //
      setCurrencySymbol("$");
      setSymbolPosition(CurrencySymbolPosition.BEFORE);
      setCurrencyDigits(Integer.valueOf(2));
      setThousandsSeparator(',');
      setDecimalSeparator('.');

      setDateOrder(DateOrder.DMY);
      setTimeFormat(ProjectTimeFormat.TWELVE_HOUR);
      setIntegerDefaultStartTime(Integer.valueOf(480));
      setDateSeparator('/');
      setTimeSeparator(':');
      setAMText("am");
      setPMText("pm");
      setDateFormat(ProjectDateFormat.DD_MM_YYYY);
      setBarTextDateFormat(ProjectDateFormat.DD_MM_YYYY);

      //
      // Configure Default Settings Record
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
      // Configure Project Header Record
      //
      setProjectTitle("Project1");
      setCompany(null);
      setManager(null);
      setCalendarName(DEFAULT_CALENDAR_NAME);
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
    * @return int constant
    * @see TimeUnit
    */
   public TimeUnit getDefaultDurationUnits()
   {
      return (m_defaultDurationUnits);
   }

   /**
    * Default duration units. The constants used to define the
    * duration units are defined by the <code>TimeUnit</code> class.
    *
    * @param units default time units
    * @see TimeUnit
    */
   public void setDefaultDurationUnits(TimeUnit units)
   {
      m_defaultDurationUnits = units;
   }

   /**
    * Retrieves a flag indicating if the default duration type is fixed.
    *
    * @return boolean flag
    */
   public boolean getDefaultDurationIsFixed()
   {
      return (m_defaultDurationIsFixed);
   }

   /**
    * Sets a flag indicating if the default duration type is fixed.
    *
    * @param fixed boolean flag
    */
   public void setDefaultDurationIsFixed(boolean fixed)
   {
      m_defaultDurationIsFixed = fixed;
   }

   /**
    * Default work units. The constants used to define the
    * work units are defined by the <code>TimeUnit</code> class.
    *
    * @return int representing default
    * @see TimeUnit
    */
   public TimeUnit getDefaultWorkUnits()
   {
      return (m_defaultWorkUnits);
   }

   /**
    * Default work units. The constants used to define the
    * work units are defined by the <code>TimeUnit</code> class.
    *
    * @param units  int representing default
    * @see TimeUnit
    */
   public void setDefaultWorkUnits(TimeUnit units)
   {
      m_defaultWorkUnits = units;
   }

   /**
    * Retrieves the default standard rate.
    *
    * @return default standard rate
    */
   public Rate getDefaultStandardRate()
   {
      return (m_defaultStandardRate);
   }

   /**
    * Sets the default standard rate.
    *
    * @param rate default standard rate
    */
   public void setDefaultStandardRate(Rate rate)
   {
      m_defaultStandardRate = rate;
   }

   /**
    * Get overtime rate.
    *
    * @return rate
    */
   public Rate getDefaultOvertimeRate()
   {
      return (m_defaultOvertimeRate);
   }

   /**
    * Set default overtime rate.
    *
    * @param rate default overtime rate
    */
   public void setDefaultOvertimeRate(Rate rate)
   {
      m_defaultOvertimeRate = rate;
   }

   /**
    * Flags whether updating Task status also updates resource status.
    *
    * @return boolean flag
    */
   public boolean getUpdatingTaskStatusUpdatesResourceStatus()
   {
      return (m_updatingTaskStatusUpdatesResourceStatus);
   }

   /**
    * Flags whether updating Task status also updates resource status.
    *
    * @param flag boolean flag
    */
   public void setUpdatingTaskStatusUpdatesResourceStatus(boolean flag)
   {
      m_updatingTaskStatusUpdatesResourceStatus = flag;
   }

   /**
    * Flag representing whether or not to split in-progress tasks.
    *
    * @return Boolean value
    */
   public boolean getSplitInProgressTasks()
   {
      return (m_splitInProgressTasks);
   }

   /**
    * Flag representing whether or not to split in-progress tasks.
    *
    * @param flag boolean value
    */
   public void setSplitInProgressTasks(boolean flag)
   {
      m_splitInProgressTasks = flag;
   }

   /**
    * Gets constant representing set Date order eg DMY, MDY.
    *
    * @return constant value for date order
    */
   public DateOrder getDateOrder()
   {
      return (m_dateOrder);
   }

   /**
    * Sets constant representing set Date order eg DMY, MDY.
    *
    * @param dateOrder date order value
    */
   public void setDateOrder(DateOrder dateOrder)
   {
      m_dateOrder = dateOrder;
   }

   /**
    * Gets constant representing the Time Format.
    *
    * @return time format constant
    */
   public ProjectTimeFormat getTimeFormat()
   {
      return (m_timeFormat);
   }

   /**
    * Sets constant representing the time format.
    *
    * @param timeFormat constant value
    */
   public void setTimeFormat(ProjectTimeFormat timeFormat)
   {
      m_timeFormat = timeFormat;
   }

   /**
    * This internal method is used to convert from an integer representing
    * minutes past midnight into a Date instance whose time component
    * represents the start time.
    *
    * @param time integer representing the start time in minutes past midnight
    */
   public void setIntegerDefaultStartTime(Integer time)
   {
      if (time != null)
      {
         int minutes = time.intValue();
         int hours = minutes / 60;
         minutes -= (hours * 60);

         Calendar cal = Calendar.getInstance();
         cal.set(Calendar.MILLISECOND, 0);
         cal.set(Calendar.SECOND, 0);
         cal.set(Calendar.MINUTE, minutes);
         cal.set(Calendar.HOUR_OF_DAY, hours);

         Date date = cal.getTime();
         setDefaultStartTime(date);
      }
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
      return (m_defaultStartTime);
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
      m_defaultStartTime = defaultStartTime;
   }

   /**
    * Gets the date separator.
    *
    * @return date separator as set.
    */
   public char getDateSeparator()
   {
      return (m_dateSeparator);
   }

   /**
    * Sets the date separator.
    *
    * @param dateSeparator date separator as set.
    */
   public void setDateSeparator(char dateSeparator)
   {
      m_dateSeparator = dateSeparator;
   }

   /**
    * Gets the time separator.
    *
    * @return time separator as set.
    */
   public char getTimeSeparator()
   {
      return (m_timeSeparator);
   }

   /**
    * Sets the time separator.
    *
    * @param timeSeparator time separator
    */
   public void setTimeSeparator(char timeSeparator)
   {
      m_timeSeparator = timeSeparator;
   }

   /**
    * Sets the time separator.
    *
    * @param timeSeparator time separator
    */
   public void setTimeSeparator(Character timeSeparator)
   {
      setTimeSeparator((timeSeparator == null ? DEFAULT_TIME_SEPARATOR : timeSeparator.charValue()));
   }

   /**
    * Gets the AM text.
    *
    * @return AM Text as set.
    */
   public String getAMText()
   {
      return (m_amText);
   }

   /**
    * Sets the AM text.
    *
    * @param amText AM Text as set.
    */
   public void setAMText(String amText)
   {
      m_amText = amText;
   }

   /**
    * Gets the PM text.
    *
    * @return PM Text as set.
    */
   public String getPMText()
   {
      return (m_pmText);
   }

   /**
    * Sets the PM text.
    *
    * @param pmText PM Text as set.
    */
   public void setPMText(String pmText)
   {
      m_pmText = pmText;
   }

   /**
    * Gets the set Date Format.
    *
    * @return int representing Date Format
    */
   public ProjectDateFormat getDateFormat()
   {
      return (m_dateFormat);
   }

   /**
    * Sets the set Date Format.
    *
    * @param dateFormat int representing Date Format
    */
   public void setDateFormat(ProjectDateFormat dateFormat)
   {
      m_dateFormat = dateFormat;
   }

   /**
    * Gets Bar Text Date Format.
    *
    * @return int value
    */
   public ProjectDateFormat getBarTextDateFormat()
   {
      return (m_barTextDateFormat);
   }

   /**
    * Sets Bar Text Date Format.
    *
    * @param dateFormat value to be set
    */
   public void setBarTextDateFormat(ProjectDateFormat dateFormat)
   {
      m_barTextDateFormat = dateFormat;
   }

   /**
    * Retrieves the default end time.
    *
    * @return End time
    */
   public Date getDefaultEndTime()
   {
      return (m_defaultEndTime);
   }

   /**
    * Sets the default end time.
    *
    * @param date End time
    */
   public void setDefaultEndTime(Date date)
   {
      m_defaultEndTime = date;
   }

   /**
    * Sets the project title.
    *
    * @param projectTitle project title
    */
   public void setProjectTitle(String projectTitle)
   {
      m_projectTitle = projectTitle;
   }

   /**
    * Gets the project title.
    *
    * @return project title
    */
   public String getProjectTitle()
   {
      return (m_projectTitle);
   }

   /**
    * Sets the company name.
    *
    * @param company company name
    */
   public void setCompany(String company)
   {
      m_company = company;
   }

   /**
    * Retrieves the company name.
    *
    * @return company name
    */
   public String getCompany()
   {
      return (m_company);
   }

   /**
    * Sets the manager name.
    *
    * @param manager manager name
    */
   public void setManager(String manager)
   {
      m_manager = manager;
   }

   /**
    * Retrieves the manager name.
    *
    * @return manager name
    */
   public String getManager()
   {
      return (m_manager);
   }

   /**
    * Sets the Calendar used. 'Standard' if no value is set.
    *
    * @param calendarName Calendar name
    */
   public void setCalendarName(String calendarName)
   {
      if (calendarName == null || calendarName.length() == 0)
      {
         calendarName = DEFAULT_CALENDAR_NAME;
      }

      m_calendarName = calendarName;
   }

   /**
    * Gets the Calendar used. 'Standard' if no value is set.
    *
    * @return Calendar name
    */
   public String getCalendarName()
   {
      return (m_calendarName);
   }

   /**
    * Sets the project start date.
    *
    * @param startDate project start date
    */
   public void setStartDate(Date startDate)
   {
      m_startDate = startDate;
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
      Date result = m_startDate;
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
      Date result = m_finishDate;
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
      m_finishDate = finishDate;
   }

   /**
    * Retrieves an enumerated value indicating if tasks in this project are
    * scheduled from a start or a finish date.
    *
    * @return schedule from flag
    */
   public ScheduleFrom getScheduleFrom()
   {
      return (m_scheduleFrom);
   }

   /**
    * Sets an enumerated value indicating if tasks in this project are
    * scheduled from a start or a finish date.
    *
    * @param scheduleFrom schedule from value
    */
   public void setScheduleFrom(ScheduleFrom scheduleFrom)
   {
      m_scheduleFrom = scheduleFrom;
   }

   /**
    * Retrieves the current date.
    *
    * @return current date
    */
   public Date getCurrentDate()
   {
      return (m_currentDate);
   }

   /**
    * Sets the current date.
    *
    * @param currentDate current date
    */
   public void setCurrentDate(Date currentDate)
   {
      m_currentDate = currentDate;
   }

   /**
    * Returns any comments.
    *
    * @return comments attached to the Project Header
    */
   public String getComments()
   {
      return (m_comments);
   }

   /**
    * Set comment text.
    *
    * @param comments comment text
    */
   public void setComments(String comments)
   {
      m_comments = comments;
   }

   /**
    * Retrieves the project cost.
    *
    * @return project cost
    */
   public Number getCost()
   {
      return (m_cost);
   }

   /**
    * Sets the project cost.
    *
    * @param cost project cost
    */
   public void setCost(Number cost)
   {
      m_cost = cost;
   }

   /**
    * Sets the baseline project cost.
    *
    * @param baselineCost baseline project cost
    */
   public void setBaselineCost(Number baselineCost)
   {
      m_baselineCost = baselineCost;
   }

   /**
    * Retrieves the baseline project cost.
    *
    * @return baseline project cost
    */
   public Number getBaselineCost()
   {
      return (m_baselineCost);
   }

   /**
    * Sets the actual project cost.
    *
    * @param actualCost actual project cost
    */
   public void setActualCost(Number actualCost)
   {
      m_actualCost = actualCost;
   }

   /**
    * Retrieves the actual project cost.
    *
    * @return actual project cost
    */
   public Number getActualCost()
   {
      return (m_actualCost);
   }

   /**
    * Sets the project work duration.
    *
    * @param work project work duration
    */
   public void setWork(Duration work)
   {
      m_work = work;
   }

   /**
    * Retrieves the project work duration.
    *
    * @return project work duration
    */
   public Duration getWork()
   {
      return (m_work);
   }

   /**
    * Set the baseline project work duration.
    *
    * @param baselineWork baseline project work duration
    */
   public void setBaselineWork(Duration baselineWork)
   {
      m_baselineWork = baselineWork;
   }

   /**
    * Retrieves the baseline project work duration.
    *
    * @return baseline project work duration
    */
   public Duration getBaselineWork()
   {
      return (m_baselineWork);
   }

   /**
    * Sets the actual project work duration.
    *
    * @param actualWork actual project work duration
    */
   public void setActualWork(Duration actualWork)
   {
      m_actualWork = actualWork;
   }

   /**
    * Retrieves the actual project work duration.
    *
    * @return actual project work duration
    */
   public Duration getActualWork()
   {
      return (m_actualWork);
   }

   /**
    * Retrieves the project's "Work 2" attribute.
    *
    * @return Work 2 attribute
    */
   public Number getWork2()
   {
      return (m_work2);
   }

   /**
    * Sets the project's "Work 2" attribute.
    *
    * @param work2 work2 percentage value
    */
   public void setWork2(Number work2)
   {
      m_work2 = work2;
   }

   /**
    * Retrieves the project duration.
    *
    * @return project duration
    */
   public Duration getDuration()
   {
      return (m_duration);
   }

   /**
    * Sets the project duration.
    *
    * @param duration project duration
    */
   public void setDuration(Duration duration)
   {
      m_duration = duration;
   }

   /**
    * Retrieves the baseline duration value.
    *
    * @return baseline project duration value
    */
   public Duration getBaselineDuration()
   {
      return (m_baselineDuration);
   }

   /**
    * Sets the baseline project duration value.
    *
    * @param baselineDuration baseline project duration
    */
   public void setBaselineDuration(Duration baselineDuration)
   {
      m_baselineDuration = baselineDuration;
   }

   /**
    * Retrieves the actual project duration.
    *
    * @return actual project duration
    */
   public Duration getActualDuration()
   {
      return (m_actualDuration);
   }

   /**
    * Sets the actual project duration.
    *
    * @param actualDuration actual project duration
    */
   public void setActualDuration(Duration actualDuration)
   {
      m_actualDuration = actualDuration;
   }

   /**
    * Retrieves the project percentage complete.
    *
    * @return percentage value
    */
   public Number getPercentageComplete()
   {
      return (m_percentageComplete);
   }

   /**
    * Sets project percentage complete.
    *
    * @param percentComplete project percent complete
    */
   public void setPercentageComplete(Number percentComplete)
   {
      m_percentageComplete = percentComplete;
   }

   /**
    * Sets the baseline project start date.
    *
    * @param baselineStartDate baseline project start date
    */
   public void setBaselineStart(Date baselineStartDate)
   {
      m_baselineStart = baselineStartDate;
   }

   /**
    * Retrieves the baseline project start date.
    *
    * @return baseline project start date
    */
   public Date getBaselineStart()
   {
      return (m_baselineStart);
   }

   /**
    * Sets the baseline project finish date.
    *
    * @param baselineFinishDate baseline project finish date
    */
   public void setBaselineFinish(Date baselineFinishDate)
   {
      m_baselineFinish = baselineFinishDate;
   }

   /**
    * Retrieves the baseline project finish date.
    *
    * @return baseline project finish date
    */
   public Date getBaselineFinish()
   {
      return (m_baselineFinish);
   }

   /**
    * Sets the actual project start date.
    *
    * @param actualStartDate actual project start date
    */
   public void setActualStart(Date actualStartDate)
   {
      m_actualStart = actualStartDate;
   }

   /**
    * Retrieves the actual project start date.
    *
    * @return actual project start date
    */
   public Date getActualStart()
   {
      return (m_actualStart);
   }

   /**
    * Sets the actual project finish date.
    *
    * @param actualFinishDate actual project finish date
    */
   public void setActualFinish(Date actualFinishDate)
   {
      m_actualFinish = actualFinishDate;
   }

   /**
    * Retrieves the actual project finish date.
    *
    * @return actual project finish date
    */
   public Date getActualFinish()
   {
      return (m_actualFinish);
   }

   /**
    * Retrieves the start variance duration.
    *
    * @return start date variance
    */
   public Duration getStartVariance()
   {
      return (m_startVariance);
   }

   /**
    * Sets the start variance duration.
    *
    * @param startVariance the start date variance
    */
   public void setStartVariance(Duration startVariance)
   {
      m_startVariance = startVariance;
   }

   /**
    * Retrieves the project finish variance duration.
    *
    * @return project finish variance duration
    */
   public Duration getFinishVariance()
   {
      return (m_finishVariance);
   }

   /**
    * Sets the project finish variance duration.
    *
    * @param finishVariance project finish variance duration
    */
   public void setFinishVariance(Duration finishVariance)
   {
      m_finishVariance = finishVariance;
   }

   /**
    * Returns the project subject text.
    *
    * @return subject text
    */
   public String getSubject()
   {
      return (m_subject);
   }

   /**
    * Sets the project subject text.
    *
    * @param subject subject text
    */
   public void setSubject(String subject)
   {
      m_subject = subject;
   }

   /**
    * Retrieves the project author text.
    *
    * @return author text
    */
   public String getAuthor()
   {
      return (m_author);
   }

   /**
    * Sets the project author text.
    *
    * @param author project author text
    */
   public void setAuthor(String author)
   {
      m_author = author;
   }

   /**
    * Retrieves the project keyword text.
    *
    * @return project keyword text
    */
   public String getKeywords()
   {
      return (m_keywords);
   }

   /**
    * Sets the project keyword text.
    *
    * @param keywords project keyword text
    */
   public void setKeywords(String keywords)
   {
      m_keywords = keywords;
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
         symbol = "$";
      }

      m_currencySymbol = symbol;
   }

   /**
    * Retrieves the currency symbol.
    *
    * @return currency symbol
    */
   public String getCurrencySymbol()
   {
      return (m_currencySymbol);
   }

   /**
    * Sets the position of the currency symbol.
    *
    * @param posn currency symbol position.
    */
   public void setSymbolPosition(CurrencySymbolPosition posn)
   {
      m_symbolPosition = posn;
   }

   /**
    * Retrieves a constant representing the position of the currency symbol.
    *
    * @return position
    */
   public CurrencySymbolPosition getSymbolPosition()
   {
      return (m_symbolPosition);
   }

   /**
    * Sets no of currency digits.
    *
    * @param currDigs Available values, 0,1,2
    */
   public void setCurrencyDigits(Number currDigs)
   {
      m_currencyDigits = currDigs;
   }

   /**
    * Gets no of currency digits.
    *
    * @return Available values, 0,1,2
    */
   public Number getCurrencyDigits()
   {
      return (m_currencyDigits);
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
      m_thousandsSeparator = sep;
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
      return (m_thousandsSeparator);
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
      m_decimalSeparator = decSep;
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
      return (m_decimalSeparator);
   }

   /**
    * Retrieve the externally edited flag.
    *
    * @return externally edited flag
    */
   public boolean getProjectExternallyEdited()
   {
      return (m_projectExternallyEdited);
   }

   /**
    * Set the externally edited flag.
    *
    * @param projectExternallyEdited externally edited flag
    */
   public void setProjectExternallyEdited(boolean projectExternallyEdited)
   {
      m_projectExternallyEdited = projectExternallyEdited;
   }

   /**
    * Retrieves the category text.
    *
    * @return category text
    */
   public String getCategory()
   {
      return (m_category);
   }

   /**
    * Sets the category text.
    *
    * @param category category text
    */
   public void setCategory(String category)
   {
      m_category = category;
   }

   /**
    * Retrieve the number of days per month.
    *
    * @return days per month
    */
   public Number getDaysPerMonth()
   {
      return (m_daysPerMonth);
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
         m_daysPerMonth = daysPerMonth;
      }
   }

   /**
    * Retrieve the number of minutes per day.
    *
    * @return minutes per day
    */
   public Number getMinutesPerDay()
   {
      return (m_minutesPerDay);
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
         m_minutesPerDay = minutesPerDay;
      }
   }

   /**
    * Retrieve the number of minutes per week.
    *
    * @return minutes per week
    */
   public Number getMinutesPerWeek()
   {
      return m_minutesPerWeek;
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
         m_minutesPerWeek = minutesPerWeek;
      }
   }

   /**
    * Retrieve the fiscal year start flag.
    *
    * @return fiscal year start flag
    */
   public boolean getFiscalYearStart()
   {
      return (m_fiscalYearStart);
   }

   /**
    * Set the fiscal year start flag.
    *
    * @param fiscalYearStart fiscal year start
    */
   public void setFiscalYearStart(boolean fiscalYearStart)
   {
      m_fiscalYearStart = fiscalYearStart;
   }

   /**
    * Retrieves the default task earned value method.
    *
    * @return default task earned value method
    */
   public EarnedValueMethod getDefaultTaskEarnedValueMethod()
   {
      return m_defaultTaskEarnedValueMethod;
   }

   /**
    * Sets the default task earned value method.
    *
    * @param defaultTaskEarnedValueMethod default task earned value method
    */
   public void setDefaultTaskEarnedValueMethod(EarnedValueMethod defaultTaskEarnedValueMethod)
   {
      m_defaultTaskEarnedValueMethod = defaultTaskEarnedValueMethod;
   }

   /**
    * Retrieve the remove file properties flag.
    *
    * @return remove file properties flag
    */
   public boolean getRemoveFileProperties()
   {
      return (m_removeFileProperties);
   }

   /**
    * Set the remove file properties flag.
    *
    * @param removeFileProperties remove file properties flag
    */
   public void setRemoveFileProperties(boolean removeFileProperties)
   {
      m_removeFileProperties = removeFileProperties;
   }

   /**
    * Retrieve the move completed ends back flag.
    *
    * @return move completed ends back flag
    */
   public boolean getMoveCompletedEndsBack()
   {
      return (m_moveCompletedEndsBack);
   }

   /**
    * Set the move completed ends back flag.
    *
    * @param moveCompletedEndsBack move completed ends back flag
    */
   public void setMoveCompletedEndsBack(boolean moveCompletedEndsBack)
   {
      m_moveCompletedEndsBack = moveCompletedEndsBack;
   }

   /**
    * Retrieve the new tasks estimated flag.
    *
    * @return new tasks estimated flag
    */
   public boolean getNewTasksEstimated()
   {
      return (m_newTasksEstimated);
   }

   /**
    * Set the new tasks estimated flag.
    *
    * @param newTasksEstimated new tasks estimated flag
    */
   public void setNewTasksEstimated(boolean newTasksEstimated)
   {
      m_newTasksEstimated = newTasksEstimated;
   }

   /**
    * Retrieve the spread actual cost flag.
    *
    * @return spread actual cost flag
    */
   public boolean getSpreadActualCost()
   {
      return (m_spreadActualCost);
   }

   /**
    * Set the spread actual cost flag.
    *
    * @param spreadActualCost spread actual cost flag
    */
   public void setSpreadActualCost(boolean spreadActualCost)
   {
      m_spreadActualCost = spreadActualCost;
   }

   /**
    * Retrieve the multiple critical paths flag.
    *
    * @return multiple critical paths flag
    */
   public boolean getMultipleCriticalPaths()
   {
      return (m_multipleCriticalPaths);
   }

   /**
    * Set the multiple critical paths flag.
    *
    * @param multipleCriticalPaths multiple critical paths flag
    */
   public void setMultipleCriticalPaths(boolean multipleCriticalPaths)
   {
      m_multipleCriticalPaths = multipleCriticalPaths;
   }

   /**
    * Retrieve the auto add new resources and tasks flag.
    *
    * @return auto add new resources and tasks flag
    */
   public boolean getAutoAddNewResourcesAndTasks()
   {
      return (m_autoAddNewResourcesAndTasks);
   }

   /**
    * Set the auto add new resources and tasks flag.
    *
    * @param autoAddNewResourcesAndTasks auto add new resources and tasks flag
    */
   public void setAutoAddNewResourcesAndTasks(boolean autoAddNewResourcesAndTasks)
   {
      m_autoAddNewResourcesAndTasks = autoAddNewResourcesAndTasks;
   }

   /**
    * Retrieve the last saved date.
    *
    * @return last saved date
    */
   public Date getLastSaved()
   {
      return (m_lastSaved);
   }

   /**
    * Set the last saved date.
    *
    * @param lastSaved last saved date
    */
   public void setLastSaved(Date lastSaved)
   {
      m_lastSaved = lastSaved;
   }

   /**
    * Retrieve the status date.
    *
    * @return status date
    */
   public Date getStatusDate()
   {
      return (m_statusDate);
   }

   /**
    * Set the status date.
    *
    * @param statusDate status date
    */
   public void setStatusDate(Date statusDate)
   {
      m_statusDate = statusDate;
   }

   /**
    * Retrieves the move remaining starts back flag.
    *
    * @return move remaining starts back flag
    */
   public boolean getMoveRemainingStartsBack()
   {
      return (m_moveRemainingStartsBack);
   }

   /**
    * Sets the move remaining starts back flag.
    *
    * @param moveRemainingStartsBack remaining starts back flag
    */
   public void setMoveRemainingStartsBack(boolean moveRemainingStartsBack)
   {
      m_moveRemainingStartsBack = moveRemainingStartsBack;
   }

   /**
    * Retrieves the autolink flag.
    *
    * @return autolink flag
    */
   public boolean getAutolink()
   {
      return (m_autolink);
   }

   /**
    * Sets the autolink flag.
    *
    * @param autolink autolink flag
    */
   public void setAutolink(boolean autolink)
   {
      m_autolink = autolink;
   }

   /**
    * Retrieves the Microsoft Project Server URL flag.
    *
    * @return Microsoft Project Server URL flag
    */
   public boolean getMicrosoftProjectServerURL()
   {
      return (m_microsoftProjectServerURL);
   }

   /**
    * Sets the Microsoft Project Server URL flag.
    *
    * @param microsoftProjectServerURL Microsoft Project Server URL flag
    */
   public void setMicrosoftProjectServerURL(boolean microsoftProjectServerURL)
   {
      m_microsoftProjectServerURL = microsoftProjectServerURL;
   }

   /**
    * Retrieves the honor constraints flag.
    *
    * @return honor constraints flag
    */
   public boolean getHonorConstraints()
   {
      return (m_honorConstraints);
   }

   /**
    * Sets the honor constraints flag.
    *
    * @param honorConstraints honor constraints flag
    */
   public void setHonorConstraints(boolean honorConstraints)
   {
      m_honorConstraints = honorConstraints;
   }

   /**
    * Retrieve the admin project flag.
    *
    * @return admin project flag
    */
   public boolean getAdminProject()
   {
      return (m_adminProject);
   }

   /**
    * Set the admin project flag.
    *
    * @param adminProject admin project flag
    */
   public void setAdminProject(boolean adminProject)
   {
      m_adminProject = adminProject;
   }

   /**
    * Retrieves the inserted projects like summary flag.
    *
    * @return inserted projects like summary flag
    */
   public boolean getInsertedProjectsLikeSummary()
   {
      return (m_insertedProjectsLikeSummary);
   }

   /**
    * Sets the inserted projects like summary flag.
    *
    * @param insertedProjectsLikeSummary inserted projects like summary flag
    */
   public void setInsertedProjectsLikeSummary(boolean insertedProjectsLikeSummary)
   {
      m_insertedProjectsLikeSummary = insertedProjectsLikeSummary;
   }

   /**
    * Retrieves the project name.
    *
    * @return project name
    */
   public String getName()
   {
      return (m_name);
   }

   /**
    * Sets the project name.
    *
    * @param name project name
    */
   public void setName(String name)
   {
      m_name = name;
   }

   /**
    * Retrieves the spread percent complete flag.
    *
    * @return spread percent complete flag
    */
   public boolean getSpreadPercentComplete()
   {
      return (m_spreadPercentComplete);
   }

   /**
    * Sets the spread percent complete flag.
    *
    * @param spreadPercentComplete spread percent complete flag
    */
   public void setSpreadPercentComplete(boolean spreadPercentComplete)
   {
      m_spreadPercentComplete = spreadPercentComplete;
   }

   /**
    * Retrieve the move completed ends forward flag.
    *
    * @return move completed ends forward flag
    */
   public boolean getMoveCompletedEndsForward()
   {
      return (m_moveCompletedEndsForward);
   }

   /**
    * Sets the move completed ends forward flag.
    *
    * @param moveCompletedEndsForward move completed ends forward flag
    */
   public void setMoveCompletedEndsForward(boolean moveCompletedEndsForward)
   {
      m_moveCompletedEndsForward = moveCompletedEndsForward;
   }

   /**
    * Retrieve the editable actual costs flag.
    *
    * @return editable actual costs flag
    */
   public boolean getEditableActualCosts()
   {
      return (m_editableActualCosts);
   }

   /**
    * Set the editable actual costs flag.
    *
    * @param editableActualCosts editable actual costs flag
    */
   public void setEditableActualCosts(boolean editableActualCosts)
   {
      m_editableActualCosts = editableActualCosts;
   }

   /**
    * Retrieve the unique ID for this project.
    *
    * @return unique ID
    */
   public String getUniqueID()
   {
      return (m_uniqueID);
   }

   /**
    * Set the unique ID for this project.
    *
    * @param uniqueID unique ID
    */
   public void setUniqueID(String uniqueID)
   {
      m_uniqueID = uniqueID;
   }

   /**
    * Retrieve the project revision number.
    *
    * @return revision number
    */
   public Integer getRevision()
   {
      return (m_revision);
   }

   /**
    * Retrieve the new tasks effort driven flag.
    *
    * @return new tasks effort driven flag
    */
   public boolean getNewTasksEffortDriven()
   {
      return (m_newTasksEffortDriven);
   }

   /**
    * Sets the new tasks effort driven flag.
    *
    * @param newTasksEffortDriven new tasks effort driven flag
    */
   public void setNewTasksEffortDriven(boolean newTasksEffortDriven)
   {
      m_newTasksEffortDriven = newTasksEffortDriven;
   }

   /**
    * Set the project revision number.
    *
    * @param revision revision number
    */
   public void setRevision(Integer revision)
   {
      m_revision = revision;
   }

   /**
    * Retrieve the move remaining starts forward flag.
    *
    * @return move remaining starts forward flag
    */
   public boolean getMoveRemainingStartsForward()
   {
      return (m_moveRemainingStartsForward);
   }

   /**
    * Set the move remaining starts forward flag.
    *
    * @param moveRemainingStartsForward move remaining starts forward flag
    */
   public void setMoveRemainingStartsForward(boolean moveRemainingStartsForward)
   {
      m_moveRemainingStartsForward = moveRemainingStartsForward;
   }

   /**
    * Retrieve the actuals in sync flag.
    *
    * @return actuals in sync flag
    */
   public boolean getActualsInSync()
   {
      return (m_actualsInSync);
   }

   /**
    * Set the actuals in sync flag.
    *
    * @param actualsInSync actuals in sync flag
    */
   public void setActualsInSync(boolean actualsInSync)
   {
      m_actualsInSync = actualsInSync;
   }

   /**
    * Retrieve the default task type.
    *
    * @return default task type
    */
   public TaskType getDefaultTaskType()
   {
      return (m_defaultTaskType);
   }

   /**
    * Set the default task type.
    *
    * @param defaultTaskType default task type
    */
   public void setDefaultTaskType(TaskType defaultTaskType)
   {
      m_defaultTaskType = defaultTaskType;
   }

   /**
    * Retrieve the earned value method.
    *
    * @return earned value method
    */
   public EarnedValueMethod getEarnedValueMethod()
   {
      return (m_earnedValueMethod);
   }

   /**
    * Set the earned value method.
    *
    * @param earnedValueMethod earned value method
    */
   public void setEarnedValueMethod(EarnedValueMethod earnedValueMethod)
   {
      m_earnedValueMethod = earnedValueMethod;
   }

   /**
    * Retrieve the project creation date.
    *
    * @return project creation date
    */
   public Date getCreationDate()
   {
      return (m_creationDate);
   }

   /**
    * Set the project creation date.
    *
    * @param creationDate project creation date
    */
   public void setCreationDate(Date creationDate)
   {
      m_creationDate = creationDate;
   }

   /**
    * Retrieve the extended creation date.
    *
    * @return extended creation date
    */
   public Date getExtendedCreationDate()
   {
      return (m_extendedCreationDate);
   }

   /**
    * Retrieve the default fixed cost accrual type.
    *
    * @return default fixed cost accrual type
    */
   public AccrueType getDefaultFixedCostAccrual()
   {
      return (m_defaultFixedCostAccrual);
   }

   /**
    * Sets the default fixed cost accrual type.
    *
    * @param defaultFixedCostAccrual default fixed cost accrual type
    */
   public void setDefaultFixedCostAccrual(AccrueType defaultFixedCostAccrual)
   {
      m_defaultFixedCostAccrual = defaultFixedCostAccrual;
   }

   /**
    * Set the extended creation date.
    *
    * @param creationDate extended creation date
    */
   public void setExtendedCreationDate(Date creationDate)
   {
      m_extendedCreationDate = creationDate;
   }

   /**
    * Retrieve the critical slack limit.
    *
    * @return critical slack limit
    */
   public Integer getCriticalSlackLimit()
   {
      return (m_criticalSlackLimit);
   }

   /**
    * Set the critical slack limit.
    *
    * @param criticalSlackLimit critical slack limit
    */
   public void setCriticalSlackLimit(Integer criticalSlackLimit)
   {
      m_criticalSlackLimit = criticalSlackLimit;
   }

   /**
    * Retrieve the number of the baseline to use for earned value
    * calculations.
    *
    * @return baseline for earned value
    */
   public Integer getBaselineForEarnedValue()
   {
      return (m_baselineForEarnedValue);
   }

   /**
    * Set the number of the baseline to use for earned value
    * calculations.
    *
    * @param baselineForEarnedValue baseline for earned value
    */
   public void setBaselineForEarnedValue(Integer baselineForEarnedValue)
   {
      m_baselineForEarnedValue = baselineForEarnedValue;
   }

   /**
    * Retrieves the fiscal year start month (January=1, December=12).
    *
    * @return fiscal year start month
    */
   public Integer getFiscalYearStartMonth()
   {
      return (m_fiscalYearStartMonth);
   }

   /**
    * Sets the fiscal year start month (January=1, December=12).
    *
    * @param fiscalYearStartMonth fiscal year start month
    */
   public void setFiscalYearStartMonth(Integer fiscalYearStartMonth)
   {
      m_fiscalYearStartMonth = fiscalYearStartMonth;
   }

   /**
    * Retrieve the flag indicating if new tasks should default to the
    * project start date (true) or the current date (false).
    *
    * @return new task start is project start
    */
   public boolean getNewTaskStartIsProjectStart()
   {
      return (m_newTaskStartIsProjectStart);
   }

   /**
    * Sets the flag indicating if new tasks should default to the
    * project start date (true) or the current date (false).
    *
    * @param newTaskStartIsProjectStart new task start is project start
    */
   public void setNewTaskStartIsProjectStart(boolean newTaskStartIsProjectStart)
   {
      m_newTaskStartIsProjectStart = newTaskStartIsProjectStart;
   }

   /**
    * Retrieve the week start day.
    *
    * @return week start day
    */
   public Day getWeekStartDay()
   {
      return (m_weekStartDay);
   }

   /**
    * Set the week start day.
    *
    * @param weekStartDay week start day
    */
   public void setWeekStartDay(Day weekStartDay)
   {
      m_weekStartDay = weekStartDay;
   }

   /**
    * Sets the calculate multiple critical paths flag.
    *
    * @param flag boolean flag
    */
   public void setCalculateMultipleCriticalPaths(boolean flag)
   {
      m_calculateMultipleCriticalPaths = flag;
   }

   /**
    * Retrieves the calculate multiple critical paths flag.
    *
    * @return boolean flag
    */
   public boolean getCalculateMultipleCriticalPaths()
   {
      return (m_calculateMultipleCriticalPaths);
   }

   /**
    * Retrieve the currency code for this project.
    * 
    * @return currency code
    */
   public String getCurrencyCode()
   {
      return (m_currencyCode);
   }

   /**
    * Set the currency code for this project.
    * 
    * @param currencyCode currency code
    */
   public void setCurrencyCode(String currencyCode)
   {
      m_currencyCode = currencyCode;
   }

   /**
    * Set the Document Summary Information.
    *
    * @param documentSummaryInformation The Document Summary Information Map
    */
   public void setDocumentSummaryInformation(Map<Integer, Object> documentSummaryInformation)
   {
      m_documentSummaryInformation = documentSummaryInformation;
   }

   /**
    * Retrieve the Document Summary Information. This allows the caller
    * to examine custom document summary fields which may be present in
    * the project.
    *
    * @return the Document Summary Information Map
    */
   public Map<Integer, Object> getDocumentSummaryInformation()
   {
      return (m_documentSummaryInformation);
   }

   /**
    * Sets the hyperlink base for this Project.
    *
    * @param hyperlinkBase Hyperlink base
    */
   public void setHyperlinkBase(String hyperlinkBase)
   {
      m_hyperlinkBase = hyperlinkBase;
   }

   /**
    * Gets the hyperlink base for this Project. If any.
    *
    * @return Hyperlink base
    */
   public String getHyperlinkBase()
   {
      return (m_hyperlinkBase);
   }

   /**
    * Retrieves the "show project summary task" flag.
    * 
    * @return boolean flag
    */
   public boolean getShowProjectSummaryTask()
   {
      return m_showProjectSummaryTask;
   }

   /**
    * Sets the "show project summary task" flag.
    * 
    * @param value boolean flag
    */
   public void setShowProjectSummaryTask(boolean value)
   {
      m_showProjectSummaryTask = value;
   }

   private String m_currencySymbol;
   private CurrencySymbolPosition m_symbolPosition = CurrencySymbolPosition.BEFORE;
   private Number m_currencyDigits;
   private char m_thousandsSeparator;
   private char m_decimalSeparator;

   /**
    * Default Settings Attributes.
    */
   private TimeUnit m_defaultDurationUnits = TimeUnit.DAYS;
   private boolean m_defaultDurationIsFixed;
   private TimeUnit m_defaultWorkUnits;
   private Rate m_defaultStandardRate;
   private Rate m_defaultOvertimeRate;
   private boolean m_updatingTaskStatusUpdatesResourceStatus;
   private boolean m_splitInProgressTasks;

   /**
    * Date Time Settings Attributes.
    */
   private DateOrder m_dateOrder = DateOrder.MDY;
   private ProjectTimeFormat m_timeFormat = ProjectTimeFormat.TWELVE_HOUR;
   private Date m_defaultStartTime;
   private char m_dateSeparator;
   private char m_timeSeparator;
   private String m_amText;
   private String m_pmText;
   private ProjectDateFormat m_dateFormat = ProjectDateFormat.DD_MM_YY;
   private ProjectDateFormat m_barTextDateFormat;

   /**
    * Project Header Attributes.
    */
   private String m_projectTitle;
   private String m_company;
   private String m_manager;
   private String m_calendarName;
   private Date m_startDate;
   private Date m_finishDate;
   private ScheduleFrom m_scheduleFrom = ScheduleFrom.START;
   private Date m_currentDate;
   private String m_comments;
   private Number m_cost;
   private Number m_baselineCost;
   private Number m_actualCost;
   private Duration m_work;
   private Duration m_baselineWork;
   private Duration m_actualWork;
   private Number m_work2;
   private Duration m_duration;
   private Duration m_baselineDuration;
   private Duration m_actualDuration;
   private Number m_percentageComplete;
   private Date m_baselineStart;
   private Date m_baselineFinish;
   private Date m_actualStart;
   private Date m_actualFinish;
   private Duration m_startVariance;
   private Duration m_finishVariance;
   private String m_subject;
   private String m_author;
   private String m_keywords;
   private String m_hyperlinkBase;

   /**
    * The following member variables are extended attributes. They are
    * do not form part of the MPX file format definition, and are neither
    * loaded from an MPX file, or saved to an MPX file. Their purpose
    * is to provide storage for attributes which are defined by later versions
    * of Microsoft Project. This allows these attributes to be manipulated
    * when they have been retrieved from file formats other than MPX.
    */
   private Date m_defaultEndTime;
   private boolean m_projectExternallyEdited;
   private String m_category;
   private Number m_minutesPerDay;
   private Number m_daysPerMonth;
   private Number m_minutesPerWeek;
   private boolean m_fiscalYearStart;
   private EarnedValueMethod m_defaultTaskEarnedValueMethod;
   private boolean m_removeFileProperties;
   private boolean m_moveCompletedEndsBack;
   private boolean m_newTasksEstimated;
   private boolean m_spreadActualCost;
   private boolean m_multipleCriticalPaths;
   private boolean m_autoAddNewResourcesAndTasks;
   private Date m_lastSaved;
   private Date m_statusDate;
   private boolean m_moveRemainingStartsBack;
   private boolean m_autolink;
   private boolean m_microsoftProjectServerURL;
   private boolean m_honorConstraints;
   private boolean m_adminProject;
   private boolean m_insertedProjectsLikeSummary;
   private String m_name;
   private boolean m_spreadPercentComplete;
   private boolean m_moveCompletedEndsForward;
   private boolean m_editableActualCosts;
   private String m_uniqueID;
   private Integer m_revision;
   private boolean m_newTasksEffortDriven;
   private boolean m_moveRemainingStartsForward;
   private boolean m_actualsInSync;
   private TaskType m_defaultTaskType;
   private EarnedValueMethod m_earnedValueMethod;
   private Date m_creationDate;
   private Date m_extendedCreationDate;
   private AccrueType m_defaultFixedCostAccrual;
   private Integer m_criticalSlackLimit;
   private Integer m_baselineForEarnedValue;
   private Integer m_fiscalYearStartMonth;
   private boolean m_newTaskStartIsProjectStart;
   private Day m_weekStartDay;
   private boolean m_calculateMultipleCriticalPaths;
   private Map<Integer, Object> m_documentSummaryInformation;
   private String m_currencyCode;
   private boolean m_showProjectSummaryTask;

   /*
    * Missing MSPDI attributes
    *
       // this is probably the schedule from value, we could remove
       // the ScheduleFrom type, and replace it with a boolean
       // we just need to ensure that the MPX read/write works OK
       void setScheduleFromStart(boolean value);
    */

   /**
    * Default time separator character.
    */
   private static final char DEFAULT_TIME_SEPARATOR = ':';

   /**
    * Default cost value.
    */
   private static final Double DEFAULT_COST = Double.valueOf(0);

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
