/*
 * file:       ProjectHeader.java
 * author:     Scott Melville
 *             Jon Iles
 * copyright:  (c) Tapster Rock Limited 2002-2003
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

package com.tapsterrock.mpx;

import java.util.Calendar;
import java.util.Date;
import java.util.Locale;


/**
 * This class represents the ProjectHeader record as found in an MPX
 * file. This record contains details of global settings relevant to the
 * project plan. Note that a number of the fields in this record are
 * calculated by Microsoft Project, and will therefore be ignored on import.
 */
public final class ProjectHeader extends MPXRecord
{
   /**
    * Default constructor.
    *
    * @param file the parent file to which this record belongs.
    */
   ProjectHeader (MPXFile file)
   {
      super (file, 0);

      //
      // Configure Date Time Settings and Currency Settings Records
      //
      setLocale (file.getLocale());

      //
      // Configure Default Settings Record
      //
      setDefaultDurationUnits(TimeUnit.DAYS);
      setDefaultDurationIsFixed(false);
      setDefaultWorkUnits(TimeUnit.HOURS);
      setDefaultHoursInDay(new Float(8));
      setDefaultHoursInWeek(new Float (40));
      setDefaultStandardRate(new MPXRate(10, TimeUnit.HOURS));
      setDefaultOvertimeRate(new MPXRate(15, TimeUnit.HOURS));
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
    * This method updates a default settings instance with data from
    * an MPX file.
    *
    * @param record record containing the data for  this object.
    * @throws MPXException Thrown on parse errors
    */
   void updateDefaultSettings (Record record)
      throws MPXException
   {
      setDefaultDurationUnits(record.getTimeUnit(0));
      setNumericBooleanDefaultDurationIsFixed(record.getNumericBoolean(1));
      setDefaultWorkUnits(record.getTimeUnit(2));
      setDefaultHoursInDay(record.getFloat(3));
      setDefaultHoursInWeek(record.getFloat(4));
      setDefaultStandardRate(record.getRate(5));
      setDefaultOvertimeRate(record.getRate(6));
      setNumericBooleanUpdatingTaskStatusUpdatesResourceStatus(record.getNumericBoolean(7));
      setNumericBooleanSplitInProgressTasks(record.getNumericBoolean(8));
   }

   /**
    * Gets Default Duration units. The constants used to define the
    * duration units are defined by the <code>TimeUnit</code> class.
    *
    * @return int constant
    * @see TimeUnit
    */
   public TimeUnit getDefaultDurationUnits ()
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
   public void setDefaultDurationUnits (TimeUnit units)
   {
      m_defaultDurationUnits = units;
   }

   /**
    * Retrieves a flag indicating if the default duration type is fixed.
    *
    * @return boolean flag
    */
   private NumericBoolean getNumericBooleanDefaultDurationIsFixed ()
   {
      return (m_defaultDurationIsFixed);
   }

   /**
    * Sets a flag indicating if the default duration type is fixed.
    *
    * @param fixed boolean flag
    */
   private void setNumericBooleanDefaultDurationIsFixed (NumericBoolean fixed)
   {
      m_defaultDurationIsFixed = fixed;
   }

   /**
    * Retrieves a flag indicating if the default duration type is fixed.
    *
    * @return boolean flag
    */
   public boolean getDefaultDurationIsFixed ()
   {
      return (m_defaultDurationIsFixed.booleanValue());
   }

   /**
    * Sets a flag indicating if the default duration type is fixed.
    *
    * @param fixed boolean flag
    */
   public void setDefaultDurationIsFixed (boolean fixed)
   {
      m_defaultDurationIsFixed = NumericBoolean.getInstance(fixed);
   }

   /**
    * Default work units. The constants used to define the
    * work units are defined by the <code>TimeUnit</code> class.
    *
    * @return int representing default
    * @see TimeUnit
    */
   public TimeUnit getDefaultWorkUnits ()
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
   public void setDefaultWorkUnits (TimeUnit units)
   {
      m_defaultWorkUnits = units;
   }

   /**
    * Gets the default number of hours in a day
    *
    * @return number of hours
    */
   public Float getDefaultHoursInDay ()
   {
      return (m_defaultHoursInDay);
   }

   /**
    * Sets the default number of hours in a day
    *
    * @param hours number of hours
    */
   public void setDefaultHoursInDay (Float hours)
   {
      m_defaultHoursInDay = hours;
   }

   /**
    * Gets the default number of hours in a week
    *
    * @return number of hours
    */
   public Float getDefaultHoursInWeek ()
   {
      return (m_defaultHoursInWeek);
   }

   /**
    * Sets the default number of hours in a week
    *
    * @param hours number of hours
    */
   public void setDefaultHoursInWeek (Float hours)
   {
      m_defaultHoursInWeek = hours;
   }

   /**
    * Get rate
    *
    * @return rate
    */
   public MPXRate getDefaultStandardRate ()
   {
      return (m_defaultStandardRate);
   }

   /**
    * Set rate
    *
    * @param rate default standard rate
    */
   public void setDefaultStandardRate (MPXRate rate)
   {
      m_defaultStandardRate = rate;
   }

   /**
    * Get overtime rate
    *
    * @return rate
    */
   public MPXRate getDefaultOvertimeRate ()
   {
      return (m_defaultOvertimeRate);
   }

   /**
    * Set default overtime rate
    *
    * @param rate default overtime rate
    */
   public void setDefaultOvertimeRate (MPXRate rate)
   {
      m_defaultOvertimeRate = rate;
   }

   /**
    * Flags whether updating Task status also updates resource status.
    *
    * @return boolean flag
    */
   private NumericBoolean getNumericBooleanUpdatingTaskStatusUpdatesResourceStatus ()
   {
      return (m_updatingTaskStatusUpdatesResourceStatus);
   }

   /**
    * Flags whether updating Task status also updates resource status.
    *
    * @param flag boolean flag
    */
   public void setNumericBooleanUpdatingTaskStatusUpdatesResourceStatus (NumericBoolean flag)
   {
      m_updatingTaskStatusUpdatesResourceStatus = flag;
   }

   /**
    * Flags whether updating Task status also updates resource status.
    *
    * @return boolean flag
    */
   public boolean getUpdatingTaskStatusUpdatesResourceStatus ()
   {
      return (m_updatingTaskStatusUpdatesResourceStatus.booleanValue());
   }

   /**
    * Flags whether updating Task status also updates resource status.
    *
    * @param flag boolean flag
    */
   public void setUpdatingTaskStatusUpdatesResourceStatus (boolean flag)
   {
      m_updatingTaskStatusUpdatesResourceStatus = NumericBoolean.getInstance(flag);
   }

   /**
    * Flag representing whether or not to split in-progress tasks.
    *
    * @return Boolean value
    */
   private NumericBoolean getNumericBooleanSplitInProgressTasks ()
   {
      return (m_splitInProgressTasks);
   }

   /**
    * Flag representing whether or not to split in-progress tasks.
    *
    * @param flag boolean value
    */
   private void setNumericBooleanSplitInProgressTasks (NumericBoolean flag)
   {
      m_splitInProgressTasks = flag;
   }

   /**
    * Flag representing whether or not to split in-progress tasks.
    *
    * @return Boolean value
    */
   public boolean getSplitInProgressTasks ()
   {
      return (m_splitInProgressTasks.booleanValue());
   }

   /**
    * Flag representing whether or not to split in-progress tasks.
    *
    * @param flag boolean value
    */
   public void setSplitInProgressTasks (boolean flag)
   {
      m_splitInProgressTasks = NumericBoolean.getInstance(flag);
   }

   /**
    * This method is calkled when the locale of the parent file is updated.
    * It resets the locale specific currency attributes to the default values
    * for the new locale.
    *
    * @param locale new locale
    */
   void setLocale (Locale locale)
   {
      m_updateCurrencyFormat = false;
      setCurrencySymbol(LocaleData.getString(locale, LocaleData.CURRENCY_SYMBOL));
      setSymbolPosition((CurrencySymbolPosition)LocaleData.getObject(locale, LocaleData.CURRENCY_SYMBOL_POSITION));
      setCurrencyDigits(LocaleData.getInteger(locale, LocaleData.CURRENCY_DIGITS));
      setThousandsSeparator(LocaleData.getChar(locale, LocaleData.CURRENCY_THOUSANDS_SEPARATOR));
      setDecimalSeparator(LocaleData.getChar(locale, LocaleData.CURRENCY_DECIMAL_SEPARATOR));
      m_updateCurrencyFormat = true;
      updateCurrencyFormats ();

      m_updateDateTimeFormats = false;
      setDateOrder((DateOrder)LocaleData.getObject(locale, LocaleData.DATE_ORDER));
      setTimeFormat((ProjectTimeFormat)LocaleData.getObject(locale, LocaleData.TIME_FORMAT));
      setIntegerDefaultStartTime (LocaleData.getInteger(locale, LocaleData.DEFAULT_START_TIME));
      setDateSeparator(LocaleData.getChar(locale, LocaleData.DATE_SEPARATOR));
      setTimeSeparator(LocaleData.getChar(locale, LocaleData.TIME_SEPARATOR));
      setAMText(LocaleData.getString(locale, LocaleData.AM_TEXT));
      setPMText(LocaleData.getString(locale, LocaleData.PM_TEXT));
      setDateFormat((ProjectDateFormat)LocaleData.getObject(locale, LocaleData.DATE_FORMAT));
      setBarTextDateFormat((ProjectDateFormat)LocaleData.getObject(locale, LocaleData.DATE_FORMAT));
      m_updateDateTimeFormats = true;
      updateDateTimeFormats ();
   }

   /**
    * This method is used to update a date time settings instance with
    * data from an MPX file.
    *
    * @param record record containing the data for  this object.
    */
   void updateDateTimeSettings (Record record)
   {
      m_updateDateTimeFormats = false;
      setDateOrder(record.getDateOrder(0));
      setTimeFormat(record.getTimeFormat(1));
      setIntegerDefaultStartTime(record.getInteger(2));
      setDateSeparator(record.getCharacter(3));
      setTimeSeparator(record.getCharacter(4));
      setAMText(record.getString(5));
      setPMText(record.getString(6));
      setDateFormat(record.getDateFormat(7));
      setBarTextDateFormat(record.getDateFormat(8));
      m_updateDateTimeFormats = true;

      updateDateTimeFormats ();
   }

   /**
    * This method updates the formatters used to control time and date
    * formatting.
    */
   private void updateDateTimeFormats ()
   {
      if (m_updateDateTimeFormats == true)
      {
         String datePattern = "";
         String dateTimePattern= "";
         String timePattern = getTimeElement();

         char datesep = getDateSeparator();
         int dateOrderValue = getDateOrder().getValue();

         switch (dateOrderValue)
         {
            case DateOrder.DMY_VALUE:
            {
               datePattern="dd"+datesep+"MM"+datesep+"yy";
               break;
            }

            case DateOrder.MDY_VALUE:
            {
               datePattern="MM"+datesep+"dd"+datesep+"yy";
               break;
            }

            case DateOrder.YMD_VALUE:
            {
               datePattern="yy"+datesep+"MM"+datesep+"dd";
               break;
            }
         }

         switch (getDateFormat().getValue())
         {
            case ProjectDateFormat.DD_MM_YY_HH_MM_VALUE:
            {
               switch (dateOrderValue)
               {
                  case DateOrder.DMY_VALUE:
                  {
                     dateTimePattern="dd"+datesep+"MM"+datesep+"yy "+timePattern;
                     break;
                  }

                  case DateOrder.MDY_VALUE:
                  {
                     dateTimePattern="MM"+datesep+"dd"+datesep+"yy "+timePattern;
                     break;
                  }

                  case DateOrder.YMD_VALUE:
                  {
                     dateTimePattern="yy"+datesep+"MM"+datesep+"dd "+timePattern;
                     break;
                  }
               }
               break;
            }

            case ProjectDateFormat.DD_MM_YY_VALUE:
            {
               switch (dateOrderValue)
               {
                  case DateOrder.DMY_VALUE:
                  {
                     dateTimePattern="dd"+datesep+"MM"+datesep+"yy";
                     break;
                  }

                  case DateOrder.MDY_VALUE:
                  {
                     dateTimePattern="MM"+datesep+"dd"+datesep+"yy";
                     break;
                  }

                  case DateOrder.YMD_VALUE:
                  {
                     dateTimePattern="yy"+datesep+"MM"+datesep+"dd";
                     break;

                  }
               }
               break;
            }

            case ProjectDateFormat.DD_MMMMM_YYYY_HH_MM_VALUE:
            {
               switch (dateOrderValue)
               {
                  case DateOrder.DMY_VALUE:
                  {
                     dateTimePattern="dd MMMMM yyyy "+timePattern;
                     break;
                  }

                  case DateOrder.MDY_VALUE:
                  {
                     dateTimePattern="MMMMM dd yyyy "+timePattern;
                     break;
                  }

                  case DateOrder.YMD_VALUE:
                  {
                     dateTimePattern="yyyy MMMMM dd "+timePattern;
                     break;
                  }
               }
               break;
            }

            case ProjectDateFormat.DD_MMMMM_YYYY_VALUE:
            {
               switch (dateOrderValue)
               {
                  case DateOrder.DMY_VALUE:
                  {
                     dateTimePattern="dd MMMMM yyyy";
                     break;
                  }

                  case DateOrder.MDY_VALUE:
                  {
                     dateTimePattern="MMMMM dd yyyy";
                     break;
                  }

                  case DateOrder.YMD_VALUE:
                  {
                     dateTimePattern="yyyy MMMMM dd";
                     break;
                  }
               }
               break;
            }

            case ProjectDateFormat.DD_MMM_HH_MM_VALUE:
            {
               switch (dateOrderValue)
               {
                  case DateOrder.DMY_VALUE:
                  {
                     dateTimePattern="dd MMM "+timePattern;
                     break;
                  }

                  case DateOrder.MDY_VALUE:
                  {
                     dateTimePattern=" MMM dd "+timePattern;
                     break;
                  }
               }
               break;
            }

            case ProjectDateFormat.DD_MMM_YY_VALUE:
            {
               switch (dateOrderValue)
               {
                  case DateOrder.DMY_VALUE:
                  {
                     dateTimePattern="dd MMM ''yy";
                     break;
                  }

                  case DateOrder.MDY_VALUE:
                  {
                     dateTimePattern="MMM dd ''yy";
                     break;
                  }

                  case DateOrder.YMD_VALUE:
                  {
                     dateTimePattern="''yy MMM dd";
                     break;
                  }
               }
               break;
            }

            case ProjectDateFormat.DD_MMMMM_VALUE:
            {
               switch (dateOrderValue)
               {
                  case DateOrder.DMY_VALUE:
                  {
                     dateTimePattern="dd MMMMM";
                     break;
                  }

                  case DateOrder.MDY_VALUE:
                  {
                     dateTimePattern="MMMMM dd";
                     break;
                  }
               }
               break;
            }

            case ProjectDateFormat.DD_MMM_VALUE:
            {
               switch (dateOrderValue)
               {
                  case DateOrder.DMY_VALUE:
                  {
                     dateTimePattern="dd MMM";
                     break;
                  }

                  case DateOrder.MDY_VALUE:
                  {
                     dateTimePattern="MMM dd";
                     break;
                  }
               }
               break;
            }

            case ProjectDateFormat.EEE_DD_MM_YY_HH_MM_VALUE:
            {
               switch (dateOrderValue)
               {
                  case DateOrder.DMY_VALUE:
                  {
                     dateTimePattern="EEE "+"dd"+datesep+"MM"+datesep+"yy "+timePattern;
                     break;
                  }

                  case DateOrder.MDY_VALUE:
                  {
                     dateTimePattern="EEE "+"MM"+datesep+"dd"+datesep+"yy "+timePattern;
                     break;
                  }

                  case DateOrder.YMD_VALUE:
                  {
                     dateTimePattern="EEE "+"yy"+datesep+"MM"+datesep+"dd "+timePattern;
                     break;
                  }
               }
               break;
            }

            case ProjectDateFormat.EEE_DD_MM_YY_VALUE:
            {
               switch (dateOrderValue)
               {
                  case DateOrder.DMY_VALUE:
                  {
                     dateTimePattern="EEE dd"+datesep+"MM"+datesep+"yy";
                     break;
                  }

                  case DateOrder.MDY_VALUE:
                  {
                     dateTimePattern="EEE MM"+datesep+"dd"+datesep+"yy";
                     break;
                  }

                  case DateOrder.YMD_VALUE:
                  {
                     dateTimePattern="EEE yy"+datesep+"MM"+datesep+"dd";
                     break;
                  }
               }
               break;
            }

            case ProjectDateFormat.EEE_DD_MMM_YY_VALUE:
            {
               switch (dateOrderValue)
               {
                  case DateOrder.DMY_VALUE:
                  {
                     dateTimePattern="EEE dd MMM ''yy";
                     break;
                  }

                  case DateOrder.MDY_VALUE:
                  {
                     dateTimePattern="EEE MM dd ''yy";
                     break;
                  }

                  case DateOrder.YMD_VALUE:
                  {
                     dateTimePattern="EEE ''yy MMM dd";
                     break;
                  }
               }
               break;
            }

            case ProjectDateFormat.EEE_HH_MM_VALUE:
            {
               dateTimePattern="EEE "+timePattern;
               break;
            }

            case ProjectDateFormat.DD_MM_VALUE:
            {
               switch (dateOrderValue)
               {
                  case DateOrder.DMY_VALUE:
                  {
                     dateTimePattern="dd"+datesep+"MM";
                     break;
                  }

                  case DateOrder.MDY_VALUE:
                  {
                     dateTimePattern="MM"+datesep+"dd";
                     break;
                  }
               }
               break;
            }

            case ProjectDateFormat.DD_VALUE:
            {
               dateTimePattern="dd";
               break;
            }

            case ProjectDateFormat.HH_MM_VALUE:
            {
               dateTimePattern = timePattern;
               break;
            }

            case ProjectDateFormat.EEE_DD_MMM_VALUE:
            {
               switch (dateOrderValue)
               {
                  case DateOrder.DMY_VALUE:
                  {
                     dateTimePattern="EEE dd MMM";
                     break;
                  }

                  case DateOrder.MDY_VALUE:
                  {
                     dateTimePattern="EEE MMM dd";
                     break;
                  }
               }
               break;
            }

            case ProjectDateFormat.EEE_DD_MM_VALUE:
            {
               switch (dateOrderValue)
               {
                  case DateOrder.DMY_VALUE:
                  {
                     dateTimePattern="EEE dd"+datesep+"MM";
                     break;
                  }

                  case DateOrder.MDY_VALUE:
                  {
                     dateTimePattern="EEE MM"+datesep+"dd";
                     break;
                  }
               }
               break;
            }

            case ProjectDateFormat.EEE_DD_VALUE:
            {
               dateTimePattern="EEE dd";
               break;
            }

            case ProjectDateFormat.DD_WWW_VALUE:
            {
               dateTimePattern="F"+datesep+"'W'ww";
               break;
            }

            case ProjectDateFormat.DD_WWW_YY_HH_MM_VALUE:
            {
               dateTimePattern="F"+datesep+"'W'ww"+datesep+"yy "+timePattern;
               break;
            }

            case ProjectDateFormat.DD_MM_YYYY_VALUE:
            {
               switch (dateOrderValue)
               {
                  case DateOrder.DMY_VALUE:
                  {
                     dateTimePattern="dd"+datesep+"MM"+datesep+"yyyy";
                     break;
                  }

                  case DateOrder.MDY_VALUE:
                  {
                     dateTimePattern="MM"+datesep+"dd"+datesep+"yyyy";
                     break;
                  }

                  case DateOrder.YMD_VALUE:
                  {
                     dateTimePattern="yyyy"+datesep+"MM"+datesep+"dd";
                     break;
                  }
               }
               break;
            }
         }

         MPXFile parent = getParentFile();
         ((MPXDateFormat)parent.getDateTimeFormat()).applyPattern(dateTimePattern);
         ((MPXDateFormat)parent.getDateFormat()).applyPattern(datePattern);
         ((MPXTimeFormat)parent.getTimeFormat()).applyPattern(timePattern);
      }
   }

   /**
    * Returns time elements considering 12/24 hour formatting.
    *
    * @return time formatting String
    */
   private String getTimeElement()
   {
      String time;
      char timesep = getTimeSeparator();
      ProjectTimeFormat format = getTimeFormat();

      if (format == null || format.getValue() == ProjectTimeFormat.TWELVE_HOUR_VALUE)
      {
         time = "hh"+timesep+"mm a";
      }
      else
      {
         time = "HH"+timesep+"mm";
      }

      return (time);
   }

   /**
    * Gets constant representing set Date order eg DMY, MDY
    *
    * @return constant value for date order
    */
   public DateOrder getDateOrder ()
   {
      return (m_dateOrder);
   }

   /**
    * Sets constant representing set Date order eg DMY, MDY
    *
    * @param dateOrder date order value
    */
   public void setDateOrder (DateOrder dateOrder)
   {
      m_dateOrder = dateOrder;
      updateDateTimeFormats();
   }

   /**
    * Gets constant representing the Time Format
    *
    * @return time format constant
    */
   public ProjectTimeFormat getTimeFormat ()
   {
      return (m_timeFormat);
   }

   /**
    * Sets constant representing the time format
    *
    * @param timeFormat constant value
    */
   public void setTimeFormat (ProjectTimeFormat timeFormat)
   {
      m_timeFormat = timeFormat;
      updateDateTimeFormats();
   }

   /**
    * This internal method is used to convert from an integer representing
    * minutes past midnight into a Date instance whose time component
    * represents the start time.
    *
    * @param time integer representing the start time in minutes past midnight
    */
   private void setIntegerDefaultStartTime (Integer time)
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
    * This internal method is used to convert from a Date instance to an
    * integer representing the number of minutes past midnight.
    *
    * @return minutes past midnight as an integer
    */
   private Integer getIntegerDefaultStartTime ()
   {
      Integer result = null;
      Date date = getDefaultStartTime();
      if (date != null)
      {
         Calendar cal = Calendar.getInstance();
         cal.setTime(date);
         int time = cal.get(Calendar.HOUR_OF_DAY) * 60;
         time += cal.get(Calendar.MINUTE);
         result = new Integer (time);
      }
      return (result);
   }

   /**
    * Retrieve the default start time, specified using the Java Date type.
    * Note that this assumes that the value returned from
    * the getTime method starts at zero... i.e. the date part
    * of the date/time value has not been set.
    *
    * @return default start time
    */
   public Date getDefaultStartTime ()
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
   public void setDefaultStartTime (Date defaultStartTime)
   {
      m_defaultStartTime = defaultStartTime;
   }

   /**
    * Gets the date separator.
    *
    * @return date separator as set.
    */
   public char getDateSeparator ()
   {
      return (m_dateSeparator);
   }

   /**
    * Sets the date separator.
    *
    * @param dateSeparator date separator as set.
    */
   public void setDateSeparator (char dateSeparator)
   {
      m_dateSeparator = dateSeparator;
      updateDateTimeFormats();
   }

   /**
    * Sets the date separator.
    *
    * @param dateSeparator date separator as set.
    */
   private void setDateSeparator (Character dateSeparator)
   {
      setDateSeparator ((dateSeparator==null?DEFAULT_DATE_SEPARATOR:dateSeparator.charValue()));
   }

   /**
    * Gets the time separator.
    *
    * @return time separator as set.
    */
   public char getTimeSeparator ()
   {
      return (m_timeSeparator);
   }

   /**
    * Sets the time separator.
    *
    * @param timeSeparator time separator
    */
   public void setTimeSeparator (char timeSeparator)
   {
      m_timeSeparator = timeSeparator;
      updateDateTimeFormats();
   }

   /**
    * Sets the time separator.
    *
    * @param timeSeparator time separator
    */
   public void setTimeSeparator (Character timeSeparator)
   {
      setTimeSeparator ((timeSeparator==null?DEFAULT_TIME_SEPARATOR:timeSeparator.charValue()));
   }

   /**
    * Gets the AM text.
    *
    * @return AM Text as set.
    */
   public String getAMText ()
   {
      return (m_amText);
   }

   /**
    * Sets the AM text.
    *
    * @param amText AM Text as set.
    */
   public void setAMText (String amText)
   {
      m_amText = amText;
      updateDateTimeFormats();
   }

   /**
    * Gets the PM text.
    *
    * @return PM Text as set.
    */
   public String getPMText ()
   {
      return (m_pmText);
   }

   /**
    * Sets the PM text.
    *
    * @param pmText PM Text as set.
    */
   public void setPMText (String pmText)
   {
      m_pmText = pmText;
      updateDateTimeFormats();
   }

   /**
    * Gets the set Date Format.
    *
    * @return int representing Date Format
    */
   public ProjectDateFormat getDateFormat ()
   {
      return (m_dateFormat);
   }

   /**
    * Sets the set Date Format.
    *
    * @param dateFormat int representing Date Format
    */
   public void setDateFormat (ProjectDateFormat dateFormat)
   {
      m_dateFormat = dateFormat;
      updateDateTimeFormats();
   }

   /**
    * Gets Bar Text Date Format
    *
    * @return int value
    */
   public ProjectDateFormat getBarTextDateFormat ()
   {
      return (m_barTextDateFormat);
   }

   /**
    * Sets Bar Text Date Format.
    *
    * @param dateFormat value to be set
    */
   public void setBarTextDateFormat (ProjectDateFormat dateFormat)
   {
      m_barTextDateFormat = dateFormat;
   }

   /**
    * Retrieves the default end time.
    *
    * @return End time
    */
   public Date getDefaultEndTime ()
   {
      return (m_defaultEndTime);
   }

   /**
    * Sets the default end time.
    *
    * @param date End time
    */
   public void setDefaultEndTime (Date date)
   {
      m_defaultEndTime = date;
   }

   /**
    * This method allows an existing instance of a ProjectHeader object
    * to be updated with data taken from a record in an MPX file.
    *
    * @param record record containing the data for  this object.
    * @throws MPXException normally thrown when parsing fails
    */
   void updateProjectHeader (Record record)
      throws MPXException
   {
      setProjectTitle(record.getString(0));
      setCompany(record.getString(1));
      setManager(record.getString(2));
      setCalendarName(record.getString(3));
      setStartDate(record.getDateTime(4));
      setFinishDate(record.getDateTime(5));
      setScheduleFrom(record.getScheduleFrom(6));
      setCurrentDate(record.getDateTime(7));
      setComments(record.getString(8));
      setCost(record.getCurrency(9));
      setBaselineCost(record.getCurrency(10));
      setActualCost(record.getCurrency(11));
      setWork(record.getDuration(12));
      setBaselineWork(record.getDuration(13));
      setActualWork(record.getDuration(14));
      setWork2(record.getPercentage(15));
      setDuration(record.getDuration(16));
      setBaselineDuration(record.getDuration(17));
      setActualDuration(record.getDuration(18));
      setPercentageComplete(record.getPercentage(19));
      setBaselineStart(record.getDateTime(20));
      setBaselineFinish(record.getDateTime(21));
      setActualStart(record.getDateTime(22));
      setActualFinish(record.getDateTime(23));
      setStartVariance(record.getDuration(24));
      setFinishVariance(record.getDuration(25));
      setSubject(record.getString(26));
      setAuthor(record.getString(27));
      setKeywords(record.getString(28));
   }

   /**
    * Sets the project title
    *
    * @param projectTitle project title
    */
   public void setProjectTitle (String projectTitle)
   {
      m_projectTitle = projectTitle;
   }

   /**
    * Gets the project title
    *
    * @return project title
    */
   public String getProjectTitle ()
   {
      return (m_projectTitle);
   }

   /**
    * Sets the company name
    *
    * @param company company name
    */
   public void setCompany (String company)
   {
      m_company = company;
   }

   /**
    * Retrieves the company name
    *
    * @return company name
    */
   public String getCompany ()
   {
      return (m_company);
   }

   /**
    * Sets the manager name
    *
    * @param manager manager name
    */
   public void setManager (String manager)
   {
      m_manager = manager;
   }

   /**
    * Retrieves the manager name
    *
    * @return manager name
    */
   public String getManager ()
   {
      return (m_manager);
   }

   /**
    * Sets the Calendar used. 'Standard' if no value is set
    *
    * @param calendarName Calendar name
    */
   public void setCalendarName (String calendarName)
   {
      if (calendarName == null || calendarName.length() == 0)
      {
         calendarName = DEFAULT_CALENDAR_NAME;
      }

      m_calendarName = calendarName;
   }

   /**
    * Gets the Calendar used. 'Standard' if no value is set
    *
    * @return Calendar name
    */
   public String getCalendarName ()
   {
      return (m_calendarName);
   }

   /**
    * Sets the project start date
    *
    * @param startDate project start date
    */
   public void setStartDate (Date startDate)
   {
      m_startDate = toDate(startDate);
   }

   /**
    * Retrieves the project start date. If an explicit start date has not been
    * set, this method calculates the start date by looking for
    * the earliest task start date.
    *
    * @return project start date
    */
   public Date getStartDate ()
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
   public Date getFinishDate ()
   {
      Date result = m_finishDate;
      if (result == null)
      {
         result = getParentFile().getFinishDate();
      }
      return (result);
   }

   /**
    * Sets the project finish date
    *
    * @param finishDate project finish date
    */
   public void setFinishDate (Date finishDate)
   {
      m_finishDate = toDate(finishDate);
   }

   /**
    * Retrieves an enumerated value indicating if tasks in this project are
    * scheduled from a start or a finish date.
    *
    * @return schedule from flag
    */
   public ScheduleFrom getScheduleFrom ()
   {
      return (m_scheduleFrom);
   }

   /**
    * Sets an enumerated value indicating if tasks in this project are
    * scheduled from a start or a finish date.
    *
    * @param scheduleFrom schedule from value
    */
   public void setScheduleFrom (ScheduleFrom scheduleFrom)
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
   public void setCurrentDate (Date currentDate)
   {
      m_currentDate = toDate(currentDate);
   }

   /**
    * Returns any comments.
    *
    * @return comments attached to the Project Header
    */
   public String getComments ()
   {
      return (m_comments);
   }

   /**
    * Set comment text.
    *
    * @param comments comment text
    */
   public void setComments (String comments)
   {
      m_comments = comments;
   }

   /**
    * Retrieves the project cost.
    *
    * @return project cost
    */
   public Number getCost ()
   {
      return (m_cost);
   }

   /**
    * Sets the project cost.
    *
    * @param cost project cost
    */
   public void setCost (Number cost)
   {
      m_cost = toCurrency(cost);
   }

   /**
    * Sets the baseline project cost.
    *
    * @param baselineCost baseline project cost
    */
   public void setBaselineCost (Number baselineCost)
   {
      m_baselineCost = toCurrency(baselineCost);
   }

   /**
    * Retrieves the baseline project cost.
    *
    * @return baseline project cost
    */
   public Number getBaselineCost ()
   {
      return (m_baselineCost);
   }

   /**
    * Sets the actual project cost.
    *
    * @param actualCost actual project cost
    */
   public void setActualCost (Number actualCost)
   {
      m_actualCost = toCurrency(actualCost);
   }

   /**
    * Retrieves the actual project cost.
    *
    * @return actual project cost
    */
   public Number getActualCost ()
   {
      return (m_actualCost);
   }

   /**
    * Sets the project work duration
    *
    * @param work project work duration
    */
   public void setWork (MPXDuration work)
   {
      m_work = work;
   }

   /**
    * Retrieves the project work duration
    *
    * @return project work duration
    */
   public MPXDuration getWork ()
   {
      return (m_work);
   }

   /**
    * Set the baseline project work duration
    *
    * @param baselineWork baseline project work duration
    */
   public void setBaselineWork (MPXDuration baselineWork)
   {
      m_baselineWork = baselineWork;
   }

   /**
    * Retrieves the baseline project work duration
    *
    * @return baseline project work duration
    */
   public MPXDuration getBaselineWork ()
   {
      return (m_baselineWork);
   }

   /**
    * Sets the actual project work duration
    *
    * @param actualWork actual project work duration
    */
   public void setActualWork (MPXDuration actualWork)
   {
      m_actualWork = actualWork;
   }

   /**
    * Retrieves the actual project work duration
    *
    * @return actual project work duration
    */
   public MPXDuration getActualWork ()
   {
      return (m_actualWork);
   }

   /**
    * Retrieves the project's "Work 2" attribute.
    *
    * @return Work 2 attribute
    */
   public Number getWork2 ()
   {
      return (m_work2);
   }

   /**
    * Sets the project's "Work 2" attribute.
    *
    * @param work2 work2 percentage value
    */
   public void setWork2 (Number work2)
   {
      m_work2 = toPercentage(work2);
   }

   /**
    * Retrieves the project duration
    *
    * @return project duration
    */
   public MPXDuration getDuration ()
   {
      return (m_duration);
   }

   /**
    * Sets the project duration.
    *
    * @param duration project duration
    */
   public void setDuration (MPXDuration duration)
   {
      m_duration = duration;
   }

   /**
    * Retrieves the baseline duration value.
    *
    * @return baseline project duration value
    */
   public MPXDuration getBaselineDuration ()
   {
      return (m_baselineDuration);
   }

   /**
    * Sets the baseline project duration value.
    *
    * @param baselineDuration baseline project duration
    */
   public void setBaselineDuration (MPXDuration baselineDuration)
   {
      m_baselineDuration = baselineDuration;
   }

   /**
    * Retrieves the actual project duration.
    *
    * @return actual project duration
    */
   public MPXDuration getActualDuration ()
   {
      return (m_actualDuration);
   }

   /**
    * Sets the actual project duration.
    *
    * @param actualDuration actual project duration
    */
   public void setActualDuration (MPXDuration actualDuration)
   {
      m_actualDuration = actualDuration;
   }

   /**
    * Retrieves the project percentage complete
    *
    * @return percentage value
    */
   public Number getPercentageComplete ()
   {
      return (m_percentageComplete);
   }

   /**
    * Sets project percentage complete
    *
    * @param percentComplete project percent complete
    */
   public void setPercentageComplete (Number percentComplete)
   {
      m_percentageComplete = toPercentage(percentComplete);
   }

   /**
    * Sets the baseline project start date.
    *
    * @param baselineStartDate baseline project start date
    */
   public void setBaselineStart (Date baselineStartDate)
   {
      m_baselineStart = toDate(baselineStartDate);
   }

   /**
    * Retrieves the baseline project start date.
    *
    * @return baseline project start date
    */
   public Date getBaselineStart ()
   {
      return (m_baselineStart);
   }

   /**
    * Sets the baseline project finish date
    *
    * @param baselineFinishDate baseline project finish date
    */
   public void setBaselineFinish (Date baselineFinishDate)
   {
      m_baselineFinish = toDate(baselineFinishDate);
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
    * Sets the actual project start date
    *
    * @param actualStartDate actual project start date
    */
   public void setActualStart (Date actualStartDate)
   {
      m_actualStart = toDate(actualStartDate);
   }

   /**
    * Retrieves the actual project start date.
    *
    * @return actual project start date
    */
   public Date getActualStart ()
   {
      return (m_actualStart);
   }

   /**
    * Sets the actual project finish date.
    *
    * @param actualFinishDate actual project finish date
    */
   public void setActualFinish (Date actualFinishDate)
   {
      m_actualFinish = toDate(actualFinishDate);
   }

   /**
    * Retrieves the actual project finish date.
    *
    * @return actual project finish date
    */
   public Date getActualFinish ()
   {
      return (m_actualFinish);
   }

   /**
    * Retrieves the start variance duration.
    *
    * @return start date variance
    */
   public MPXDuration getStartVariance()
   {
      return (m_startVariance);
   }

   /**
    * Sets the start variance duration.
    *
    * @param startVariance the start date variance
    */
   public void setStartVariance (MPXDuration startVariance)
   {
      m_startVariance = startVariance;
   }

   /**
    * Retrieves the project finish variance duration
    *
    * @return project finish variance duration
    */
   public MPXDuration getFinishVariance ()
   {
      return (m_finishVariance);
   }

   /**
    * Sets the project finish variance duration
    *
    * @param finishVariance project finish variance duration
    */
   public void setFinishVariance (MPXDuration finishVariance)
   {
      m_finishVariance = finishVariance;
   }

   /**
    * Returns the project subject text.
    *
    * @return subject text
    */
   public String getSubject ()
   {
      return (m_subject);
   }

   /**
    * Sets the project subject text.
    *
    * @param subject subject text
    */
   public void setSubject (String subject)
   {
      m_subject = subject;
   }

   /**
    * Retrieves the project author text.
    *
    * @return author text
    */
   public String getAuthor ()
   {
      return (m_author);
   }

   /**
    * Sets the project author text
    *
    * @param author project author text
    */
   public void setAuthor (String author)
   {
      m_author = author;
   }

   /**
    * Retrieves the project keyword text.
    *
    * @return project keyword text
    */
   public String getKeywords ()
   {
      return (m_keywords);
   }

   /**
    * Sets the project keyword text
    *
    * @param keywords project keyword text
    */
   public void setKeywords (String keywords)
   {
      m_keywords = keywords;
   }

   /**
    * This method generates a string in MPX format representing the
    * contents of this record.
    *
    * @return string containing the data for this record in MPX format.
    */
   public String toString ()
   {
      StringBuffer buffer = new StringBuffer ();
      char delimiter = getParentFile().getDelimiter();

      //
      // Currency Settings Record
      //
      buffer.append (CURRENCY_SETTINGS_RECORD_NUMBER);
      buffer.append (delimiter);
      buffer.append(format(delimiter, getCurrencySymbol()));
      buffer.append (delimiter);
      buffer.append(format(delimiter, getSymbolPosition()));
      buffer.append (delimiter);
      buffer.append(format(delimiter, getCurrencyDigits()));
      buffer.append (delimiter);
      buffer.append(format(delimiter, new Character(getThousandsSeparator())));
      buffer.append (delimiter);
      buffer.append(format(delimiter, new Character(getDecimalSeparator())));
      stripTrailingDelimiters(buffer, delimiter);
      buffer.append (MPXFile.EOL);

      //
      // Default Settings Record
      //
      buffer.append (DEFAULT_SETTINGS_RECORD_NUMBER);
      buffer.append (delimiter);
      buffer.append(format(delimiter,getDefaultDurationUnits()));
      buffer.append (delimiter);
      buffer.append(format(delimiter,getNumericBooleanDefaultDurationIsFixed()));
      buffer.append (delimiter);
      buffer.append(format(delimiter,getDefaultWorkUnits()));
      buffer.append (delimiter);
      buffer.append(format(delimiter,getDefaultHoursInDay()));
      buffer.append (delimiter);
      buffer.append(format(delimiter,getDefaultHoursInWeek()));
      buffer.append (delimiter);
      buffer.append(format(delimiter,getDefaultStandardRate()));
      buffer.append (delimiter);
      buffer.append(format(delimiter,getDefaultOvertimeRate()));
      buffer.append (delimiter);
      buffer.append(format(delimiter,getNumericBooleanUpdatingTaskStatusUpdatesResourceStatus()));
      buffer.append (delimiter);
      buffer.append(format(delimiter,getNumericBooleanSplitInProgressTasks()));
      stripTrailingDelimiters(buffer, delimiter);
      buffer.append (MPXFile.EOL);

      //
      // Date Time Settings Record
      //
      buffer.append (DATE_TIME_SETTINGS_RECORD_NUMBER);
      buffer.append (delimiter);
      buffer.append(format(delimiter,getDateOrder()));
      buffer.append (delimiter);
      buffer.append(format(delimiter,getTimeFormat()));
      buffer.append (delimiter);
      buffer.append(format(delimiter,getIntegerDefaultStartTime()));
      buffer.append (delimiter);
      buffer.append(format(delimiter, new Character(getDateSeparator())));
      buffer.append (delimiter);
      buffer.append(format(delimiter, new Character(getTimeSeparator())));
      buffer.append (delimiter);
      buffer.append(format(delimiter,getAMText()));
      buffer.append (delimiter);
      buffer.append(format(delimiter,getPMText()));
      buffer.append (delimiter);
      buffer.append(format(delimiter,getDateFormat()));
      buffer.append (delimiter);
      buffer.append(format(delimiter,getBarTextDateFormat()));
      stripTrailingDelimiters(buffer, delimiter);
      buffer.append (MPXFile.EOL);

      //
      // Project Header Record
      //
      buffer.append (PROJECT_HEADER_RECORD_NUMBER);
      buffer.append (delimiter);
      buffer.append(format(delimiter,getProjectTitle()));
      buffer.append (delimiter);
      buffer.append(format(delimiter,getCompany()));
      buffer.append (delimiter);
      buffer.append(format(delimiter,getManager()));
      buffer.append (delimiter);
      buffer.append(format(delimiter,getCalendarName()));
      buffer.append (delimiter);
      buffer.append(format(delimiter,getStartDate()));
      buffer.append (delimiter);
      buffer.append(format(delimiter,getFinishDate()));
      buffer.append (delimiter);
      buffer.append(format(delimiter,getScheduleFrom()));
      buffer.append (delimiter);
      buffer.append(format(delimiter,getCurrentDate()));
      buffer.append (delimiter);
      buffer.append(format(delimiter,getComments()));
      buffer.append (delimiter);
      buffer.append(format(delimiter,getCost()));
      buffer.append (delimiter);
      buffer.append(format(delimiter,getBaselineCost()));
      buffer.append (delimiter);
      buffer.append(format(delimiter,getActualCost()));
      buffer.append (delimiter);
      buffer.append(format(delimiter,getWork()));
      buffer.append (delimiter);
      buffer.append(format(delimiter,getBaselineWork()));
      buffer.append (delimiter);
      buffer.append(format(delimiter,getActualWork()));
      buffer.append (delimiter);
      buffer.append(format(delimiter,getWork2()));
      buffer.append (delimiter);
      buffer.append(format(delimiter,getDuration()));
      buffer.append (delimiter);
      buffer.append(format(delimiter,getBaselineDuration()));
      buffer.append (delimiter);
      buffer.append(format(delimiter,getActualDuration()));
      buffer.append (delimiter);
      buffer.append(format(delimiter,getPercentageComplete()));
      buffer.append (delimiter);
      buffer.append(format(delimiter,getBaselineStart()));
      buffer.append (delimiter);
      buffer.append(format(delimiter,getBaselineFinish()));
      buffer.append (delimiter);
      buffer.append(format(delimiter,getActualStart()));
      buffer.append (delimiter);
      buffer.append(format(delimiter,getActualFinish()));
      buffer.append (delimiter);
      buffer.append(format(delimiter,getStartVariance()));
      buffer.append (delimiter);
      buffer.append(format(delimiter,getFinishVariance()));
      buffer.append (delimiter);
      buffer.append(format(delimiter,getSubject()));
      buffer.append (delimiter);
      buffer.append(format(delimiter,getAuthor()));
      buffer.append (delimiter);
      buffer.append(format(delimiter,getKeywords()));
      stripTrailingDelimiters(buffer, delimiter);
      buffer.append (MPXFile.EOL);

      return (buffer.toString());
   }

   /**
    * This method is used to update a currency settings instance with
    * new values read from an MPX file.
    *
    * @param record record containing the data for  this object.
    */
   void updateCurrencySettings (Record record)
   {
      m_updateCurrencyFormat = false;
      setCurrencySymbol (record.getString(0));
      setSymbolPosition (record.getCurrencySymbolPosition(1));
      setCurrencyDigits (record.getInteger(2));
      setThousandsSeparator (record.getCharacter(3));
      setDecimalSeparator (record.getCharacter(4));
      m_updateCurrencyFormat = true;

      updateCurrencyFormats ();
   }

   /**
    * Sets currency symbol ie $, £, DM
    *
    * @param symbol ie $, £, DM
    */
   public void setCurrencySymbol (String symbol)
   {
      if (symbol == null)
      {
         symbol = "$";
      }
      
      m_currencySymbol = symbol;
      updateCurrencyFormats();
   }

   /**
    * Gets currency symbol ie $, £, DM
    *
    * @return ie $, £, DM
    */
   public String getCurrencySymbol ()
   {
      return (m_currencySymbol);
   }

   /**
    * Sets the position of the currency symbol.
    *
    * @param posn currency symbol position.
    */
   public void setSymbolPosition (CurrencySymbolPosition posn)
   {
      m_symbolPosition = posn;
      updateCurrencyFormats();
   }

   /**
    * Retrieves a constant representing the position of the currency symbol.
    *
    * @return position
    */
   public CurrencySymbolPosition getSymbolPosition ()
   {
      return (m_symbolPosition);
   }

   /**
    * Sets no of currency digits.
    *
    * @param currDigs Available values, 0,1,2
    */
   public void setCurrencyDigits (Integer currDigs)
   {
      m_currencyDigits = currDigs;
      updateCurrencyFormats();
   }

   /**
    * Gets no of currency digits.
    *
    * @return Available values, 0,1,2
    */
   public Integer getCurrencyDigits ()
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
   public void setThousandsSeparator (char sep)
   {
      m_thousandsSeparator = sep;
      updateCurrencyFormats();
      if (getParentFile().getThousandsSeparator() != sep)
      {
         getParentFile().setThousandsSeparator(sep);
      }
   }

   /**
    * Sets the thousands separator.
    * Note that this separator defines the thousands separator for all decimal
    * numbers that appear in the MPX file.
    *
    * @param sep character
    */
   private void setThousandsSeparator (Character sep)
   {
      if (sep != null)
      {
         setThousandsSeparator (sep.charValue());
      }
   }

   /**
    * Gets the thousands separator.
    * Note that this separator defines the thousands separator for all decimal
    * numbers that appear in the MPX file.
    *
    * @return character
    */
   public char getThousandsSeparator ()
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
   public void setDecimalSeparator (char decSep)
   {
      m_decimalSeparator = decSep;
      updateCurrencyFormats();
      if (getParentFile().getDecimalSeparator() != decSep)
      {
         getParentFile().setDecimalSeparator(decSep);
      }
   }

   /**
    * Sets the decimal separator.
    * Note that this separator defines the decimal separator for all decimal
    * numbers that appear in the MPX file.
    *
    * @param decSep character
    */
   private void setDecimalSeparator (Character decSep)
   {
      if (decSep != null)
      {
         setDecimalSeparator (decSep.charValue());
      }
   }

   /**
    * Gets the decimal separator.
    * Note that this separator defines the decimal separator for all decimal
    * numbers that appear in the MPX file.
    *
    * @return character
    */
   public char getDecimalSeparator ()
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
    * Set the externally edited flag
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
   public String getCategory ()
   {
      return (m_category);
   }

   /**
    * Sets the category text.
    *
    * @param category category text
    */
   public void setCategory (String category)
   {
      m_category = category;
   }

   /**
    * Retrieve the number of days per month.
    *
    * @return days per month
    */
   public Integer getDaysPerMonth()
   {
      return (m_daysPerMonth);
   }

   /**
    * Set the number of days per month.
    *
    * @param daysPerMonth days per month
    */
   public void setDaysPerMonth(Integer daysPerMonth)
   {
      m_daysPerMonth = daysPerMonth;
   }

   /**
    * Retrieve the number of minutes per day.
    *
    * @return minutes per day
    */
   public Integer getMinutesPerDay()
   {
      return (m_minutesPerDay);
   }

   /**
    * Set the number of minutes per day.
    *
    * @param minutesPerDay minutes per day
    */
   public void setMinutesPerDay(Integer minutesPerDay)
   {
      m_minutesPerDay = minutesPerDay;
   }

   /**
    * Retrieve the number of minutes per week.
    *
    * @return minutes per week
    */
   public Integer getMinutesPerWeek()
   {
      return m_minutesPerWeek;
   }

   /**
    * Set the number of minutes per week
    *
    * @param minutesPerWeek minutes per week
    */
   public void setMinutesPerWeek(Integer minutesPerWeek)
   {
      m_minutesPerWeek = minutesPerWeek;
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
    * Retrieves the default task earned value method
    *
    * @return default task earned value method
    */
   public EarnedValueMethod getDefaultTaskEarnedValueMethod()
   {
      return m_defaultTaskEarnedValueMethod;
   }

   /**
    * Sets the default task earned value method
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
   public boolean getRemoveFileProperties ()
   {
      return (m_removeFileProperties);
   }

   /**
    * Set the remove file properties flag.
    *
    * @param removeFileProperties remove file properties flag
    */
   public void setRemoveFileProperties (boolean removeFileProperties)
   {
      m_removeFileProperties = removeFileProperties;
   }

   /**
    * Retrieve the move completed ends back flag.
    *
    * @return move completed ends back flag
    */
   public boolean getMoveCompletedEndsBack ()
   {
      return (m_moveCompletedEndsBack);
   }

   /**
    * Set the move completed ends back flag.
    *
    * @param moveCompletedEndsBack move completed ends back flag
    */
   public void setMoveCompletedEndsBack (boolean moveCompletedEndsBack)
   {
      m_moveCompletedEndsBack = moveCompletedEndsBack;
   }

   /**
    * Retrieve the new tasks estimated flag.
    *
    * @return new tasks estimated flag
    */
   public boolean getNewTasksEstimated ()
   {
      return (m_newTasksEstimated);
   }

   /**
    * Set the new tasks estimated flag.
    *
    * @param newTasksEstimated new tasks estimated flag
    */
   public void setNewTasksEstimated (boolean newTasksEstimated)
   {
      m_newTasksEstimated = newTasksEstimated;
   }

   /**
    * Retrieve the spread actual cost flag.
    *
    * @return spread actual cost flag
    */
   public boolean getSpreadActualCost ()
   {
      return (m_spreadActualCost);
   }

   /**
    * Set the spread actual cost flag.
    *
    * @param spreadActualCost spread actual cost flag
    */
   public void setSpreadActualCost (boolean spreadActualCost)
   {
      m_spreadActualCost = spreadActualCost;
   }

   /**
    * Retrieve the multiple critical paths flag.
    *
    * @return multiple critical paths flag
    */
   public boolean getMultipleCriticalPaths ()
   {
      return(m_multipleCriticalPaths);
   }

   /**
    * Set the multiple critical paths flag.
    *
    * @param multipleCriticalPaths multiple critical paths flag
    */
   public void setMultipleCriticalPaths (boolean multipleCriticalPaths)
   {
      m_multipleCriticalPaths = multipleCriticalPaths;
   }

   /**
    * Retrieve the auto add new resources and tasks flag.
    *
    * @return auto add new resources and tasks flag
    */
   public boolean getAutoAddNewResourcesAndTasks ()
   {
      return (m_autoAddNewResourcesAndTasks);
   }

   /**
    * Set the auto add new resources and tasks flag.
    *
    * @param autoAddNewResourcesAndTasks auto add new resources and tasks flag
    */
   public void setAutoAddNewResourcesAndTasks (boolean autoAddNewResourcesAndTasks)
   {
      m_autoAddNewResourcesAndTasks = autoAddNewResourcesAndTasks;
   }

   /**
    * Retrieve the last saved date.
    *
    * @return last saved date
    */
   public Date getLastSaved ()
   {
      return (m_lastSaved);
   }

   /**
    * Set the last saved date.
    *
    * @param lastSaved last saved date
    */
   public void setLastSaved (Date lastSaved)
   {
      m_lastSaved = lastSaved;
   }

   /**
    * Retrieve the status date.
    *
    * @return status date
    */
   public Date getStatusDate ()
   {
      return (m_statusDate);
   }

   /**
    * Set the status date.
    *
    * @param statusDate status date
    */
   public void setStatusDate (Date statusDate)
   {
      m_statusDate = statusDate;
   }

   /**
    * Retrieves the move remaining starts back flag.
    *
    * @return move remaining starts back flag
    */
   public boolean getMoveRemainingStartsBack ()
   {
      return (m_moveRemainingStartsBack);
   }

   /**
    * Sets the move remaining starts back flag.
    *
    * @param moveRemainingStartsBack remaining starts back flag
    */
   public void setMoveRemainingStartsBack (boolean moveRemainingStartsBack)
   {
      m_moveRemainingStartsBack = moveRemainingStartsBack;
   }

   /**
    * Retrieves the autolink flag.
    *
    * @return autolink flag
    */
   public boolean getAutolink ()
   {
      return (m_autolink);
   }

   /**
    * Sets the autolink flag.
    *
    * @param autolink autolink flag
    */
   public void setAutolink (boolean autolink)
   {
      m_autolink = autolink;
   }

   /**
    * Retrieves the Microsoft Project Server URL flag.
    *
    * @return Microsoft Project Server URL flag
    */
   public boolean getMicrosoftProjectServerURL ()
   {
      return (m_microsoftProjectServerURL);
   }

   /**
    * Sets the Microsoft Project Server URL flag.
    *
    * @param microsoftProjectServerURL Microsoft Project Server URL flag
    */
   public void setMicrosoftProjectServerURL (boolean microsoftProjectServerURL)
   {
      m_microsoftProjectServerURL = microsoftProjectServerURL;
   }

   /**
    * Retrieves the honor constraints flag.
    *
    * @return honor constraints flag
    */
   public boolean getHonorConstraints ()
   {
      return(m_honorConstraints);
   }

   /**
    * Sets the honor constraints flag.
    *
    * @param honorConstraints honor constraints flag
    */
   public void setHonorConstraints (boolean honorConstraints)
   {
      m_honorConstraints = honorConstraints;
   }

   /**
    * Retrieve the admin project flag.
    *
    * @return admin project flag
    */
   public boolean getAdminProject ()
   {
      return (m_adminProject);
   }

   /**
    * Set the admin project flag.
    *
    * @param adminProject admin project flag
    */
   public void setAdminProject (boolean adminProject)
   {
      m_adminProject = adminProject;
   }

   /**
    * Retrieves the inserted projects like summary flag.
    *
    * @return inserted projects like summary flag
    */
   public boolean getInsertedProjectsLikeSummary ()
   {
      return (m_insertedProjectsLikeSummary);
   }

   /**
    * Sets the inserted projects like summary flag.
    *
    * @param insertedProjectsLikeSummary inserted projects like summary flag
    */
   public void setInsertedProjectsLikeSummary (boolean insertedProjectsLikeSummary)
   {
      m_insertedProjectsLikeSummary = insertedProjectsLikeSummary;
   }

   /**
    * Retrieves the project name.
    *
    * @return project name
    */
   public String getName ()
   {
      return (m_name);
   }

   /**
    * Sets the project name.
    *
    * @param name project name
    */
   public void setName (String name)
   {
      m_name = name;
   }

   /**
    * Retrieves the spread percent complete flag.
    *
    * @return spread percent complete flag
    */
   public boolean getSpreadPercentComplete ()
   {
      return (m_spreadPercentComplete);
   }

   /**
    * Sets the spread percent complete flag.
    *
    * @param spreadPercentComplete spread percent complete flag
    */
   public void setSpreadPercentComplete (boolean spreadPercentComplete)
   {
      m_spreadPercentComplete = spreadPercentComplete;
   }

   /**
    * Retrieve the move completed ends forward flag.
    *
    * @return move completed ends forward flag
    */
   public boolean getMoveCompletedEndsForward ()
   {
      return (m_moveCompletedEndsForward);
   }

   /**
    * Sets the move completed ends forward flag.
    *
    * @param moveCompletedEndsForward move completed ends forward flag
    */
   public void setMoveCompletedEndsForward (boolean moveCompletedEndsForward)
   {
      m_moveCompletedEndsForward = moveCompletedEndsForward;
   }

   /**
    * Retrieve the editable actual costs flag.
    *
    * @return editable actual costs flag
    */
   public boolean getEditableActualCosts ()
   {
      return (m_editableActualCosts);
   }

   /**
    * Set the editable actual costs flag
    *
    * @param editableActualCosts editable actual costs flag
    */
   public void setEditableActualCosts (boolean editableActualCosts)
   {
      m_editableActualCosts = editableActualCosts;
   }

   /**
    * Retrieve the unique ID for this project.
    *
    * @return unique ID
    */
   public String getUniqueID ()
   {
      return (m_uniqueID);
   }

   /**
    * Set the unique ID for this project.
    *
    * @param uniqueID unique ID
    */
   public void setUniqueID (String uniqueID)
   {
      m_uniqueID = uniqueID;
   }

   /**
    * Retrieve the project revision number.
    *
    * @return revision number
    */
   public Integer getRevision ()
   {
      return (m_revision);
   }

   /**
    * Retrieve the new tasks effort driven flag.
    *
    * @return new tasks effort driven flag
    */
   public boolean getNewTasksEffortDriven ()
   {
      return (m_newTasksEffortDriven);
   }

   /**
    * Sets the new tasks effort driven flag.
    *
    * @param newTasksEffortDriven new tasks effort driven flag
    */
   public void setNewTasksEffortDriven (boolean newTasksEffortDriven)
   {
      m_newTasksEffortDriven = newTasksEffortDriven;
   }

   /**
    * Set the project revision number.
    *
    * @param revision revision number
    */
   public void setRevision (Integer revision)
   {
      m_revision = revision;
   }

   /**
    * Retrieve the move remaining starts forward flag.
    *
    * @return move remaining starts forward flag
    */
   public boolean getMoveRemainingStartsForward ()
   {
      return (m_moveRemainingStartsForward);
   }

   /**
    * Set the move remaining starts forward flag.
    *
    * @param moveRemainingStartsForward move remaining starts forward flag
    */
   public void setMoveRemainingStartsForward (boolean moveRemainingStartsForward)
   {
      m_moveRemainingStartsForward = moveRemainingStartsForward;
   }

   /**
    * Retrieve the actuals in sync flag.
    *
    * @return actuals in sync flag
    */
   public boolean getActualsInSync ()
   {
      return (m_actualsInSync);
   }

   /**
    * Set the actuals in sync flag.
    *
    * @param actualsInSync actuals in sync flag
    */
   public void setActualsInSync (boolean actualsInSync)
   {
      m_actualsInSync = actualsInSync;
   }

   /**
    * Retrieve the default task type.
    *
    * @return default task type
    */
   public TaskType getDefaultTaskType ()
   {
      return (m_defaultTaskType);
   }

   /**
    * Set the default task type
    *
    * @param defaultTaskType default task type
    */
   public void setDefaultTaskType (TaskType defaultTaskType)
   {
      m_defaultTaskType = defaultTaskType;
   }

   /**
    * Retrieve the earned value method.
    *
    * @return earned value method
    */
   public EarnedValueMethod getEarnedValueMethod ()
   {
      return (m_earnedValueMethod);
   }

   /**
    * Set the earned value method.
    *
    * @param earnedValueMethod earned value method
    */
   public void setEarnedValueMethod (EarnedValueMethod earnedValueMethod)
   {
      m_earnedValueMethod = earnedValueMethod;
   }

   /**
    * Retrieve the project creation date.
    *
    * @return project creation date
    */
   public Date getCreationDate ()
   {
      return (m_creationDate);
   }

   /**
    * Set the project creation date.
    *
    * @param creationDate project creation date
    */
   public void setCreationDate (Date creationDate)
   {
      m_creationDate = creationDate;
   }

   /**
    * Retrieve the extended creation date.
    *
    * @return extended creation date
    */
   public Date getExtendedCreationDate ()
   {
      return (m_extendedCreationDate);
   }

   /**
    * Retrieve the default fixed cost accrual type.
    *
    * @return default fixed cost accrual type
    */
   public AccrueType getDefaultFixedCostAccrual ()
   {
      return (m_defaultFixedCostAccrual);
   }

   /**
    * Sets the default fixed cost accrual type.
    *
    * @param defaultFixedCostAccrual default fixed cost accrual type
    */
   public void setDefaultFixedCostAccrual (AccrueType defaultFixedCostAccrual)
   {
      m_defaultFixedCostAccrual = defaultFixedCostAccrual;
   }

   /**
    * Set the extended creation date.
    *
    * @param creationDate extended creation date
    */
   public void setExtendedCreationDate (Date creationDate)
   {
      m_extendedCreationDate = creationDate;
   }

   /**
    * Retrieve the critical slack limit.
    *
    * @return critical slack limit
    */
   public Integer getCriticalSlackLimit ()
   {
      return (m_criticalSlackLimit);
   }

   /**
    * Set the critical slack limit.
    *
    * @param criticalSlackLimit critical slack limit
    */
   public void setCriticalSlackLimit (Integer criticalSlackLimit)
   {
      m_criticalSlackLimit = criticalSlackLimit;
   }

   /**
    * Retrieve the number of the baseline to use for earned value
    * calculations.
    *
    * @return baseline for earned value
    */
   public Integer getBaselineForEarnedValue ()
   {
      return (m_baselineForEarnedValue);
   }

   /**
    * Set the number of the baseline to use for earned value
    * calculations.
    *
    * @param baselineForEarnedValue baseline for earned value
    */
   public void setBaselineForEarnedValue (Integer baselineForEarnedValue)
   {
      m_baselineForEarnedValue = baselineForEarnedValue;
   }

   /**
    * Retrieves the fiscal year start month (January=1, December=12).
    *
    * @return fiscal year start month
    */
   public Integer getFiscalYearStartMonth ()
   {
      return (m_fiscalYearStartMonth);
   }

   /**
    * Sets the fiscal year start month (January=1, December=12).
    *
    * @param fiscalYearStartMonth fiscal year start month
    */
   public void setFiscalYearStartMonth (Integer fiscalYearStartMonth)
   {
      m_fiscalYearStartMonth = fiscalYearStartMonth;
   }

   /**
    * Retrieve the flag indicating if new tasks should default to the
    * project start date (true) or the current date (false).
    *
    * @return new task start is project start
    */
   public boolean getNewTaskStartIsProjectStart ()
   {
      return (m_newTaskStartIsProjectStart);
   }

   /**
    * Sets the flag indicating if new tasks should default to the
    * project start date (true) or the current date (false).
    *
    * @param newTaskStartIsProjectStart new task start is project start
    */
   public void setNewTaskStartIsProjectStart (boolean newTaskStartIsProjectStart)
   {
      m_newTaskStartIsProjectStart = newTaskStartIsProjectStart;
   }

   /**
    * Retrieve the week start day.
    *
    * @return week start day
    */
   public Day getWeekStartDay ()
   {
      return (m_weekStartDay);
   }

   /**
    * Set the week start day.
    *
    * @param weekStartDay week start day
    */
   public void setWeekStartDay (Day weekStartDay)
   {
      m_weekStartDay = weekStartDay;
   }

   /**
    * This method updates the formatters used to control the currency
    * formatting.
    */
   private void updateCurrencyFormats ()
   {
      if (m_updateCurrencyFormat == true)
      {
         MPXFile parent = getParentFile();
         String prefix = "";
         String suffix = "";
         String currencySymbol = quoteFormatCharacters (getCurrencySymbol());

         switch (getSymbolPosition().getValue())
         {
            case CurrencySymbolPosition.AFTER_VALUE:
            {
               suffix = currencySymbol;
               break;
            }

            case CurrencySymbolPosition.BEFORE_VALUE:
            {
               prefix = currencySymbol;
               break;
            }

            case CurrencySymbolPosition.AFTER_WITH_SPACE_VALUE:
            {
               suffix = " " + currencySymbol;
               break;
            }

            case CurrencySymbolPosition.BEFORE_WITH_SPACE_VALUE:
            {
               prefix = currencySymbol + " ";
               break;
            }
         }

         StringBuffer pattern = new StringBuffer(prefix);
         pattern.append("#0");

         int digits = getCurrencyDigits().intValue();
         if (digits > 0)
         {
            pattern.append('.');
            for(int i = 0 ; i < digits ; i++)
            {
               pattern.append("0");
            }
         }

         pattern.append(suffix);

         String primaryPattern = pattern.toString();
         
         String[] alternativePatterns = new String[7];
         alternativePatterns[0] = primaryPattern + ";(" + primaryPattern + ")";
         pattern.insert(prefix.length(), "#,#");
         String secondaryPattern = pattern.toString();
         alternativePatterns[1] = secondaryPattern;
         alternativePatterns[2] = secondaryPattern + ";(" + secondaryPattern + ")";

         pattern.setLength(0);
         pattern.append("#0");

         if (digits > 0)
         {
            pattern.append('.');
            for(int i = 0 ; i < digits ; i++)
            {
               pattern.append("0");
            }
         }
         
         String noSymbolPrimaryPattern = pattern.toString();
         alternativePatterns[3] = noSymbolPrimaryPattern;
         alternativePatterns[4] = noSymbolPrimaryPattern + ";(" + noSymbolPrimaryPattern + ")";
         pattern.insert(0, "#,#");
         String noSymbolSecondaryPattern = pattern.toString();
         alternativePatterns[5] = noSymbolSecondaryPattern;
         alternativePatterns[6] = noSymbolSecondaryPattern + ";(" + noSymbolSecondaryPattern + ")";

         parent.setCurrencyFormat(primaryPattern, alternativePatterns, getDecimalSeparator(), getThousandsSeparator());
      }
   }

   /**
    * This method is used to quote any special characters that appear in
    * literal text that is required as part of the currency format.
    *
    * @param literal Literal text
    * @return literal text with special characters in quotes
    */
   private String quoteFormatCharacters (String literal)
   {
      StringBuffer sb = new StringBuffer ();
      int length = literal.length();
      char c;

      for (int loop=0; loop <length; loop++)
      {
         c = literal.charAt(loop);
         switch (c)
         {
            case '0':
            case '#':
            case '.':
            case '-':
            case ',':
            case 'E':
            case ';':
            case '%':
            {
               sb.append ("'");
               sb.append (c);
               sb.append ("'");
               break;
            }

            default:
            {
               sb.append (c);
               break;
            }
         }
      }

      return (sb.toString());
   }

   private String m_currencySymbol;
   private CurrencySymbolPosition m_symbolPosition;
   private Integer m_currencyDigits;
   private char m_thousandsSeparator;
   private char m_decimalSeparator;

   /**
    * flag used to indicate whether the currency format
    * can be automatically updated. The default value for this
    * flag is false.
    */
   private boolean m_updateCurrencyFormat;

   /**
    * Constant containing the record number associated with this record.
    */
   static final int CURRENCY_SETTINGS_RECORD_NUMBER = 10;

   /**
    * Default Settings Attributes
    */
   private TimeUnit m_defaultDurationUnits;
   private NumericBoolean m_defaultDurationIsFixed;
   private TimeUnit m_defaultWorkUnits;
   private Float m_defaultHoursInDay;
   private Float m_defaultHoursInWeek;
   private MPXRate m_defaultStandardRate;
   private MPXRate m_defaultOvertimeRate;
   private NumericBoolean m_updatingTaskStatusUpdatesResourceStatus;
   private NumericBoolean m_splitInProgressTasks;

   /**
    * Date Time Settings Attributes
    */
   private DateOrder m_dateOrder;
   private ProjectTimeFormat m_timeFormat;
   private Date m_defaultStartTime;
   private char m_dateSeparator;
   private char m_timeSeparator;
   private String m_amText;
   private String m_pmText;
   private ProjectDateFormat m_dateFormat;
   private ProjectDateFormat m_barTextDateFormat;

   /**
    * Project Header Attributes
    */
   private String m_projectTitle;
   private String m_company;
   private String m_manager;
   private String m_calendarName;
   private MPXDate m_startDate;
   private MPXDate m_finishDate;
   private ScheduleFrom m_scheduleFrom;
   private MPXDate m_currentDate;
   private String m_comments;
   private MPXCurrency m_cost;
   private MPXCurrency m_baselineCost;
   private MPXCurrency m_actualCost;
   private MPXDuration m_work;
   private MPXDuration m_baselineWork;
   private MPXDuration m_actualWork;
   private MPXPercentage m_work2;
   private MPXDuration m_duration;
   private MPXDuration m_baselineDuration;
   private MPXDuration m_actualDuration;
   private MPXPercentage m_percentageComplete;
   private MPXDate m_baselineStart;
   private MPXDate m_baselineFinish;
   private MPXDate m_actualStart;
   private MPXDate m_actualFinish;
   private MPXDuration m_startVariance;
   private MPXDuration m_finishVariance;
   private String m_subject;
   private String m_author;
   private String m_keywords;

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
   private Integer m_minutesPerDay;
   private Integer m_daysPerMonth;
   private Integer m_minutesPerWeek;
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

   /*
    * Missing MSPDI attributes
    *
       // this is probably the schedule from value, we could remove
       // the ScheduleFrom type, and replace it with a boolean
       // we just need to ensure that the MPX read/write works OK
       void setScheduleFromStart(boolean value);
    */

   /**
    * Flag used to indicate whether the formats can be automatically updated.
    * The default value for this flag is false.
    */
   private boolean m_updateDateTimeFormats;

   /**
    * Default date separator character.
    */
   private static final char DEFAULT_DATE_SEPARATOR = '/';

   /**
    * Default time separator character.
    */
   private static final char DEFAULT_TIME_SEPARATOR = ':';

   /**
    * Default cost value.
    */
   private static final Double DEFAULT_COST = new Double (0);

   /**
    * Default critical slack limit.
    */
   private static final Integer DEFAULT_CRITICAL_SLACK_LIMIT = new Integer (0);

   /**
    * Default baseline for earned value.
    */
   private static final Integer DEFAULT_BASELINE_FOR_EARNED_VALUE = new Integer (0);

   /**
    * Default fiscal year start month.
    */
   private static final Integer DEFAULT_FISCAL_YEAR_START_MONTH = new Integer (1);

   /**
    * Default week start day.
    */
   private static final Day DEFAULT_WEEK_START_DAY = Day.MONDAY;

   /**
    * Default work value.
    */
   private static final MPXDuration DEFAULT_WORK = MPXDuration.getInstance (0, TimeUnit.HOURS);

   /**
    * Default work 2 value.
    */
   private static final MPXPercentage DEFAULT_WORK2 = MPXPercentage.getInstance (0);

   /**
    * Default duration value.
    */
   private static final MPXDuration DEFAULT_DURATION = MPXDuration.getInstance (0, TimeUnit.DAYS);

   /**
    * Default schedule from value.
    */
   private static final ScheduleFrom DEFAULT_SCHEDULE_FROM = ScheduleFrom.START;

   /**
    * Default percent complete value.
    */
   private static final Integer DEFAULT_PERCENT_COMPLETE = new Integer (0);

   /**
    * Default calendar name.
    */
   private static final String DEFAULT_CALENDAR_NAME = "Standard";

   /**
    * Default minutes per day.
    */
   private static final Integer DEFAULT_MINUTES_PER_DAY = new Integer(480);

   /**
    * Default days per month.
    */
   private static final Integer DEFAULT_DAYS_PER_MONTH = new Integer (20);

   /**
    * Default minutes per week.
    */
   private static final Integer DEFAULT_MINUTES_PER_WEEK = new Integer (2400);

   /**
    * Constant containing the record number associated with this record.
    */
   static final int DEFAULT_SETTINGS_RECORD_NUMBER = 11;

   /**
    * Constant containing the record number associated with this record.
    */
   static final int DATE_TIME_SETTINGS_RECORD_NUMBER = 12;

   /**
    * Constant containing the record number associated with this record.
    */
   static final int PROJECT_HEADER_RECORD_NUMBER = 30;
}
