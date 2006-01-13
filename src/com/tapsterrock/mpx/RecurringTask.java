/*
 * file:       RecurringTask.java
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

import java.util.Date;

/**
 * This class represents the Recurring Task Record as found in an MPX file.
 */
public final class RecurringTask extends MPXRecord
{
   /**
    * Default constructor.
    *
    * @param file the parent file to which this record belongs.
    * @throws MPXException normally thrown when parsing fails
    */
   RecurringTask (ProjectFile file)
      throws MPXException
   {
      this (file, Record.EMPTY_RECORD);
   }

   /**
    * Constructor used to create an instance of this class from data
    * taken from an MPXFile record.
    *
    * @param file the MPXFile object to which this record belongs.
    * @param record Record containing the data for this object.
    * @throws MPXException normally thrown when parsing fails
    */
   RecurringTask (ProjectFile file, Record record)
      throws MPXException
   {
      super (file, 0);

      setTaskUniqueID(record.getInteger(0));
      setStartDate(record.getDateTime(1));
      setFinishDate(record.getDateTime(2));
      setDuration(record.getInteger(3));
      setDurationType(record.getString(4));
      setNumberOfOccurances(record.getInteger(5));
      setRecurranceType(record.getInteger(6));
      setNotSureIndex(record.getInteger(7));      
      setLengthRadioIndex(record.getInteger(8));
      setDailyBoxRadioIndex(record.getInteger(9));
      setWeeklyBoxDayOfWeekIndex(record.getString(10));
      setMonthlyBoxRadioIndex(record.getInteger(11));
      setYearlyBoxRadioIndex(record.getInteger(12));
      setDailyBoxComboIndex(record.getInteger(13));
      setWeeklyBoxComboIndex(record.getInteger(14));
      setMonthlyBoxFirstLastComboIndex(record.getInteger(15));
      setMonthlyBoxDayComboIndex(record.getInteger(16));
      setMonthlyBoxBottomRadioFrequencyComboIndex(record.getInteger(17));
      setMonthlyBoxDayIndex(record.getInteger(18));
      setMonthlyBoxTopRadioFrequencyComboIndex(record.getInteger(19));
      setYearlyBoxFirstLastComboIndex(record.getInteger(20));
      setYearlyBoxDayComboIndex(record.getInteger(21));
      setYearlyBoxMonthComboIndex(record.getInteger(22));
      setYearlyBoxDate(record.getDateTime(23));
   }


   /**
    * ID, Unique ID of task to which this refers.
    *
    * @return - String ID
    */
   public Integer getTaskUniqueID ()
   {
      return (m_taskUniqueID);
   }

   /**
    * ID, Unique ID of task to which this refers.
    *
    * @param id - String ID
    */
   public void setTaskUniqueID (Integer id)
   {
      m_taskUniqueID = id;
   }

   /**
    * Gets the start date of this recurring task.
    *
    * @return date to start recurring task
    */
   public Date getStartDate ()
   {
      return (m_startDate);
   }

   /**
    * Sets the start date of this recurring task.
    *
    * @param val date to start recurring task
    */
   public void setStartDate (Date val)
   {
      m_startDate = toDate(val);
   }

   /**
    * Gets the finish date of this recurring task.
    *
    * @return date to finish recurring task
    */
   public Date getFinishDate ()
   {
      return (m_finishDate);
   }

   /**
    * Sets the finish date of this recurring task.
    *
    * @param val date to finish recurring task
    */
   public void setFinishDate (Date val)
   {
      m_finishDate = toDate(val);
   }

   /**
    * Duration in minutes.
    *
    * @return int value of duration, in minutes
    */
   public Integer getDuration ()
   {
      return (m_duration);
   }

   /**
    * Duration in minutes.
    *
    * @param val Integer value of duration in minutes
    */
   public void setDuration (Integer val)
   {
      m_duration = val;
   }

   /**
    * Duration type.
    *
    * @return String type
    */
   public String getDurationType ()
   {
      return (m_durationType);
   }

   /**
    * Duration type.
    *
    * @param val int type
    */
   public void setDurationType (String val)
   {
      m_durationType = val;
   }

   /**
    * Sets the number of occurance of the task.
    *
    * @return number of occurances
    */
   public Integer getNumberOfOccurances ()
   {
      return (m_numberOfOccurances);
   }

   /**
    * Gets the number of occurance of the task.
    *
    * @param val number of occurances
    */
   public void setNumberOfOccurances (Integer val)
   {
      m_numberOfOccurances = val;
   }

   /**
    * Refers to the 'This Occurs..' box of the MSP Recurring Task infobox.
    * The options are :- 1 - DAILY, 4-WEEKLY , 8-MONTHLY , 16 - YEARLY
    *
    * @return - int representing type
    */
   public Integer getRecurranceType ()
   {
      return (m_recurranceType);
   }

   /**
    * Refers to the 'This Occurs..' box of the MSP Recurring Task infobox.
    * The options are :- 1 - DAILY, 4-WEEKLY , 8-MONTHLY , 16 - YEARLY
    *
    * @param val - int representing type
    */
   public void setRecurranceType (Integer val)
   {
      m_recurranceType = val;
   }

   /**
    * Gets the selection in Length Options box.
    * Top option - to a specific date.  Value 1
    * Bottom option - no of occurances.  value 0
    *
    * @return  value of this option set
    */
   public Integer getLengthRadioIndex ()
   {
      return (m_lengthRadioIndex);
   }

   /**
    * Sets the selection in Length Options box.
    * Top option - to a specific date.  Value 1
    * Bottom option - no of occurances.  value 0
    *
    * @param val   value of this option set
    */
   public void setLengthRadioIndex (Integer val)
   {
      m_lengthRadioIndex = val;
   }

   /**
    * Referring to the 2 radio buttons in the 'Daily' option box
    * of the MSP Recurring Task infobox.
    * The top option (Day) = 0 .
    * The bottom option (Workday) = 1
    *
    * @return - int, values as above
    */
   public Integer getDailyBoxRadioIndex ()
   {
      return (m_dailyBoxRadioIndex);
   }

   /**
    * Referring to the 2 radio buttons in the 'Daily' option box of the
    * MSP Recurring Task infobox.
    * The top option (Day) = 0 .
    * The bottom option (Workday) = 1
    *
    * @param val - int, values as above
    */
   public void setDailyBoxRadioIndex (Integer val)
   {
      m_dailyBoxRadioIndex = val;
   }

   /**
    * Refers to the 'Weekly' option boxes of the MSP Recurring Task infobox.
    * This is an index of days of the week and is represented as a 7 digit
    * string of 0/1 values.
    * The string is eg '0010010' if 'Tue' and 'Fri' are selected
    *
    * @return - String
    */
   public String getWeeklyBoxDayOfWeekIndex ()
   {
      return (m_weeklyBoxDayOfWeekIndex);
   }

   /**
    * Refers to the 'Weekly' option boxes of the MSP Recurring Task infobox.
    * This is an index of days of the week and is represented as a
    * 7 digit string of 0/1 values.
    * The string is eg '0010010' if 'Tue' and 'Fri' are selected

    * @param val - String list of day values
    */
   public void setWeeklyBoxDayOfWeekIndex (String val)
   {
      m_weeklyBoxDayOfWeekIndex = val;
   }

   /**
    * Refers to the 'Monthly' option boxes of the MSP Recurring Task infobox.
    * The top option (Day x of...) = 0 .
    * The bottom option (The Xth day of...) = 1
    * @return - 0 or 1
    */
   public Integer getMonthlyBoxRadioIndex ()
   {
      return (m_monthlyBoxRadioIndex);
   }

   /**
    * Refers to the 'Monthly' option boxes of the MSP Recurring Task infobox.
    * The top option (Day x of...) = 0 .
    * The bottom option (The Xth day of...) = 1
    *
    * @param val 0 or 1
    */
   public void setMonthlyBoxRadioIndex (Integer val)
   {
      m_monthlyBoxRadioIndex = val;
   }

   /**
    * Refers to the 'Yearly' option boxes of the MSP Recurring Task infobox.
    * The top option (Date) = 1 .
    * The bottom option (The Xth day of...) = 0
    * @return - 1 or 0, int value
    */
   public Integer getYearlyBoxRadioIndex ()
   {
      return (m_yearlyBoxRadioIndex);
   }

   /**
    * Refers to the 'Yearly' option boxes of the MSP Recurring Task infobox.
    * The top option (Date) = 1 .
    * The bottom option (The Xth day of...) = 0
    *
    * @param val - 1 or 0, int value
    */
   public void setYearlyBoxRadioIndex (Integer val)
   {
      m_yearlyBoxRadioIndex = val;
   }

   /**
    * Refers to the 'Daily' option boxes of the MSP Recurring Task infobox.
    * 'Every...' eg 'Every 7th'
    *
    * @return - int index - eg 7=7th,1=every
    */
   public Integer getDailyBoxComboIndex ()
   {
      return (m_dailyBoxComboIndex);
   }

   /**
    * Refers to the 'Daily' option boxes of the MSP Recurring Task infobox.
    * 'Every...' eg 'Every 7th'
    *
    * @param val - int index - eg 7=7th,1=every
    */
   public void setDailyBoxComboIndex (Integer val)
   {
      m_dailyBoxComboIndex = val;
   }

   /**
    * Refers to the 'Weekly' option boxes of the MSP Recurring Task infobox.
    * 'Every...' eg 'Every 7th'
    *
    * @return -  int index - eg 7=7th,1=every
    */
   public Integer getWeeklyBoxComboIndex ()
   {
      return (m_weeklyBoxComboIndex);
   }

   /**
    * Refers to the 'Weekly' option boxes of the MSP Recurring Task infobox.
    * 'Every...' eg 'Every 7th'
    *
    * @param val - int index - eg 7=7th,1=every
    */
   public void setWeeklyBoxComboIndex (Integer val)
   {
      m_weeklyBoxComboIndex = val;
   }

   /**
    * Refers to the 'The' (eg Second) 'Monthly' option boxes of the MSP
    * Recurring Task infobox.
    * eg first tueday.
    * Values for first,second,third,fourth and last.
    *
    * @return - int value  of constant
    */
   public Integer getMonthlyBoxFirstLastComboIndex ()
   {
      return (m_monthlyBoxFirstLastComboIndex);
   }

   /**
    * Refers to the 'The' (eg Second) 'Monthly' option boxes of the
    * MSP Recurring Task infobox.
    * eg first tueday.
    * Values for first,second,third,fourth and last.
    *
    * @param val - int value  of constant
    */
   public void setMonthlyBoxFirstLastComboIndex (Integer val)
   {
      m_monthlyBoxFirstLastComboIndex = val;
   }

   /**
    * Refers to the 'Monthly' option boxes of the MSP Recurring Task infobox.
    * eg Wednesday.
    * Values for day of the week
    *
    * @return - int value of day
    */
   public Integer getMonthlyBoxDayComboIndex ()
   {
      return (m_monthlyBoxDayComboIndex);
   }

   /**
    * Refers to the 'Monthly' option boxes of the MSP Recurring Task infobox.
    * eg Wednesday.
    * Values for day of the week
    *
    * @param val - int value of day
    */
   public void setMonthlyBoxDayComboIndex (Integer val)
   {
      m_monthlyBoxDayComboIndex = val;
   }

   /**
    * Refers to the 'Monthly' option boxes of the MSP Recurring Task infobox.
    * If the bottom radio button is selected (eg. The 3rd Tuesday),
    * this value is the content of the
    * 'Every...' combo box. eg 'Every 5th Month'
    *
    * @return - int value of constant
    */
   public Integer getMonthlyBoxBottomRadioFrequencyComboIndex ()
   {
      return (m_monthlyBoxBottomRadioFrequencyComboIndex);
   }

   /**
    * Refers to the 'Monthly' option boxes of the MSP Recurring Task infobox.
    * If the bottom radio button is selected (eg. The 3rd Tuesday), this value
    * is the content of the
    * 'Every...' combo box. eg 'Every 5th Month'
    *
    * @param val - int value of constant
    */
   public void setMonthlyBoxBottomRadioFrequencyComboIndex (Integer val)
   {
      m_monthlyBoxBottomRadioFrequencyComboIndex = val;
   }

   /**
    * Refers to the 'Monthly' option boxes of the MSP Recurring Task infobox.
    * This refers to the box Day X, (where X is 1-31) of the month.
    *
    * @return - int value of day 1-31
    */
   public Integer getMonthlyBoxDayIndex ()
   {
      return (m_monthlyBoxDayIndex);
   }

   /**
    * Refers to the 'Monthly' option boxes of the MSP Recurring Task infobox.
    * This refers to the box Day X, (where X is 1-31) of the month.
    *
    * @param val - int value of day 1-31
    */
   public void setMonthlyBoxDayIndex (Integer val)
   {
      m_monthlyBoxDayIndex = val;
   }

   /**
    * Refers to the 'Monthly' option boxes of the MSP Recurring Task infobox.
    * If the top radio button is selected (eg. The Xth(day) of..),
    * this value is the content of the
    * 'Every...' combo box. eg 'Every 5th Month'
    *
    * @return - int value of index constant
    */
   public Integer getMonthlyBoxTopRadioFrequencyComboIndex ()
   {
      return (m_monthlyBoxTopRadioFrequencyComboIndex);
   }

   /**
    * Refers to the 'Monthly' option boxes of the MSP Recurring Task infobox.
    * If the top radio button is selected (eg. The Xth(day) of..), this value
    * is the content of the
    * 'Every...' combo box. eg 'Every 5th Month'
    *
    * @param val - int value of index constant
    */
   public void setMonthlyBoxTopRadioFrequencyComboIndex (Integer val)
   {
      m_monthlyBoxTopRadioFrequencyComboIndex = val;
   }

   /**
    * Refers to the 'Yearly' option boxes of the MSP Recurring Task infobox.
    * eg FIRST tueday of December.
    * Values for first,second,third,fourth and last.
    *
    * @return - int value of index
    */
   public Integer getYearlyBoxFirstLastComboIndex ()
   {
      return (m_yearlyBoxFirstLastComboIndex);
   }

   /**
    * Refers to the 'Yearly' option boxes of the MSP Recurring Task infobox.
    * eg FIRST tueday of December.
    * Values for first,second,third,fourth and last.
    *
    * @param val - int value of index
    */
   public void setYearlyBoxFirstLastComboIndex (Integer val)
   {
      m_yearlyBoxFirstLastComboIndex = val;
   }

   /**
    * Refers to the 'Yearly' option boxes of the MSP Recurring Task infobox.
    * eg first TUESDAY of December.
    * Values for day of the week
    *
    * @return - int value of constant
    */
   public Integer getYearlyBoxDayComboIndex ()
   {
      return (m_yearlyBoxDayComboIndex);
   }

   /**
    * Refers to the 'Yearly' option boxes of the MSP Recurring Task infobox.
    * eg first TUESDAY of December.
    * Values for day of the week
    *
    * @param val - int value of constant
    */
   public void setYearlyBoxDayComboIndex (Integer val)
   {
      m_yearlyBoxDayComboIndex = val;
   }

   /**
    * Refers to the 'Yearly' option boxes of the MSP Recurring Task infobox.
    * eg first tuesday of MARCH.
    * Values for month of the year
    *
    * @return - int value of index
    */
   public Integer getYearlyBoxMonthComboIndex ()
   {
      return (m_yearlyBoxMonthComboIndex);
   }

   /**
    * Refers to the 'Yearly' option boxes of the MSP Recurring Task infobox.
    * eg first tuesday of MARCH.
    * Values for month of the year
    *
    * @param val - int value of index
    */
   public void setYearlyBoxMonthComboIndex (Integer val)
   {
      m_yearlyBoxMonthComboIndex = val;
   }

   /**
    * Refers to the 'Yearly' option boxes of the MSP Recurring Task infobox.
    * This is the date shown when the top radio button is selected. eg 11 December.
    * This represents the first scheduled occurance of this <tt>RecurringTask</tt>
    *
    * @return - Date in box
    */
   public Date getYearlyBoxDate ()
   {
      return (m_yearlyBoxDate);
   }

   /**
    * Refers to the 'Yearly' option boxes of the MSP Recurring Task infobox.
    * This is the date shown when the top radio button is selected. eg 11 December.
    * This represents the first scheduled occurance of this <tt>RecurringTask</tt>
    *
    * @param val - Date in box
    */
   public void setYearlyBoxDate (Date val)
   {
      m_yearlyBoxDate = toDate(val);
   }

   /**
    * Not sure index?
    *
    * @return - Integer
    */
   public Integer getNotSureIndex ()
   {
      return (m_notSureIndex);
   }

   /**
    * Not sure index?
    *
    * @param val - Integer
    */
   public void setNotSureIndex (Integer val)
   {
      m_notSureIndex = val;
   }

   /**
    * This method generates a string in MPX format representing the
    * contents of this record.
    *
    * @return string containing the data for this record in MPX format.
    */
   public String toString()
   {
      StringBuffer buf = new StringBuffer ();
      char delimiter = getParentFile().getDelimiter();
      
      buf.append(RECORD_NUMBER);
      buf.append(delimiter);      
      buf.append(format(delimiter, getTaskUniqueID()));
      buf.append(delimiter);
      buf.append(format(delimiter, getStartDate()));
      buf.append(delimiter);
      buf.append(format(delimiter, getFinishDate()));
      buf.append(delimiter);
      buf.append(format(delimiter, getDuration()));
      buf.append(delimiter);
      buf.append(format(delimiter, getDurationType()));
      buf.append(delimiter);
      buf.append(format(delimiter, getNumberOfOccurances()));
      buf.append(delimiter);
      buf.append(format(delimiter, getRecurranceType()));
      buf.append(delimiter);
      buf.append(format(delimiter, getNotSureIndex()));
      buf.append(delimiter);
      buf.append(format(delimiter, getLengthRadioIndex()));
      buf.append(delimiter);
      buf.append(format(delimiter, getDailyBoxRadioIndex()));
      buf.append(delimiter);
      buf.append(format(delimiter, getWeeklyBoxDayOfWeekIndex()));
      buf.append(delimiter);
      buf.append(format(delimiter, getMonthlyBoxRadioIndex()));
      buf.append(delimiter);
      buf.append(format(delimiter, getYearlyBoxRadioIndex()));
      buf.append(delimiter);
      buf.append(format(delimiter, getDailyBoxComboIndex()));
      buf.append(delimiter);
      buf.append(format(delimiter, getWeeklyBoxComboIndex()));
      buf.append(delimiter);
      buf.append(format(delimiter, getMonthlyBoxFirstLastComboIndex()));
      buf.append(delimiter);
      buf.append(format(delimiter, getMonthlyBoxDayComboIndex()));
      buf.append(delimiter);
      buf.append(format(delimiter, getMonthlyBoxBottomRadioFrequencyComboIndex()));
      buf.append(delimiter);
      buf.append(format(delimiter, getMonthlyBoxDayIndex()));
      buf.append(delimiter);
      buf.append(format(delimiter, getMonthlyBoxTopRadioFrequencyComboIndex()));
      buf.append(delimiter);
      buf.append(format(delimiter, getYearlyBoxFirstLastComboIndex()));
      buf.append(delimiter);
      buf.append(format(delimiter, getYearlyBoxDayComboIndex()));
      buf.append(delimiter);
      buf.append(format(delimiter, getYearlyBoxMonthComboIndex()));
      buf.append(delimiter);
      buf.append(format(delimiter, getYearlyBoxDate()));
      
      stripTrailingDelimiters(buf, delimiter);
      buf.append (ProjectFile.EOL);
      return (buf.toString());      
   }

   private Integer m_taskUniqueID;
   private Date m_startDate;
   private Date m_finishDate;
   private Integer m_duration;
   private String m_durationType;
   private Integer m_numberOfOccurances;
   private Integer m_recurranceType;
   private Integer m_lengthRadioIndex;
   private Integer m_dailyBoxRadioIndex;
   private String m_weeklyBoxDayOfWeekIndex;
   private Integer m_monthlyBoxRadioIndex;
   private Integer m_yearlyBoxRadioIndex;
   private Integer m_dailyBoxComboIndex;
   private Integer m_weeklyBoxComboIndex;
   private Integer m_monthlyBoxFirstLastComboIndex;
   private Integer m_monthlyBoxDayComboIndex;
   private Integer m_monthlyBoxBottomRadioFrequencyComboIndex;
   private Integer m_monthlyBoxDayIndex;
   private Integer m_monthlyBoxTopRadioFrequencyComboIndex;
   private Integer m_yearlyBoxFirstLastComboIndex;
   private Integer m_yearlyBoxDayComboIndex;
   private Integer m_yearlyBoxMonthComboIndex;
   private Date m_yearlyBoxDate;
   private Integer m_notSureIndex;

   /**
    * Constant representing frequency - Daily.
    */
   public static final Integer PERIOD_DAILY = new Integer(1);

   /**
    * Constant representing frequency - Weekly.
    */
   public static final Integer PERIOD_WEEKLY = new Integer(4);

   /**
    * Constant representing frequency - Monthly.
    */
   public static final Integer PERIOD_MONTHLY = new Integer(8);

   /**
    * Constant representing frequency - Annually.
    */
   public static final Integer PERIOD_YEARLY = new Integer(16);

   /**
    * Constant representing day of week - Sunday.
    */
   public static final Integer SUNDAY = new Integer(0);

   /**
    * Constant representing day of week - Monday.
    */
   public static final Integer MONDAY = new Integer(1);

   /**
    * Constant representing day of week - Tuesday.
    */
   public static final Integer TUESDAY = new Integer(2);

   /**
    * Constant representing day of week - Wednesday.
    */
   public static final Integer WEDNESDAY = new Integer(3);

   /**
    * Constant representing day of week - Thursday.
    */
   public static final Integer THURSDAY = new Integer(4);

   /**
    * Constant representing day of week - Friday.
    */
   public static final Integer FRIDAY = new Integer(5);

   /**
    * Constant representing day of week - Saturday.
    */
   public static final Integer SATURDAY = new Integer(6);

   /**
    * Constant representing month of year - January.
    */
   public static final Integer JANUARY = new Integer(1);

   /**
    * Constant representing month of year - February.
    */
   public static final Integer FEBUARY = new Integer(2);

   /**
    * Constant representing month of year - March.
    */
   public static final Integer MARCH = new Integer(3);

   /**
    * Constant representing month of year - April.
    */
   public static final Integer APRIL = new Integer(4);

   /**
    * Constant representing month of year - May.
    */
   public static final Integer MAY = new Integer(5);

   /**
    * Constant representing month of year - June.
    */
   public static final Integer JUNE = new Integer(6);

   /**
    * Constant representing month of year - July.
    */
   public static final Integer JULY = new Integer(7);

   /**
    * Constant representing month of year - August.
    */
   public static final Integer AUGUST = new Integer(8);

   /**
    * Constant representing month of year - September.
    */
   public static final Integer SEPTEMBER = new Integer(9);

   /**
    * Constant representing month of year - Obtober.
    */
   public static final Integer OCTOBER = new Integer(10);

   /**
    * Constant representing month of year - November.
    */
   public static final Integer NOVEMBER = new Integer(11);

   /**
    * Constant representing month of year - December.
    */
   public static final Integer DECEMBER = new Integer(12);

   /**
    * Constant representing frequency of occurances.
    */
   public static final Integer INDEX_TYPE_EVERY = new Integer(1);

   /**
    * Constant representing frequency of occurances.
    */
   public static final Integer INDEX_EVERY_OTHER = new Integer(2);

   /**
    * Constant representing frequency of occurances.
    */
   public static final Integer INDEX_EVERY_3RD = new Integer(3);

   /**
    * Constant representing frequency of occurances.
    */
   public static final Integer INDEX_EVERY_4TH = new Integer(4);

   /**
    * Constant representing frequency of occurances.
    */
   public static final Integer INDEX_EVERY_5TH = new Integer(5);

   /**
    * Constant representing frequency of occurances.
    */
   public static final Integer INDEX_EVERY_6TH = new Integer(6);

   /**
    * Constant representing frequency of occurances.
    */
   public static final Integer INDEX_EVERY_7TH = new Integer(7);

   /**
    * Constant representing frequency of occurances.
    */
   public static final Integer INDEX_EVERY_8TH = new Integer(8);

   /**
    * Constant representing frequency of occurances.
    */
   public static final Integer INDEX_EVERY_9TH = new Integer(9);

   /**
    * Constant representing frequency of occurances.
    */
   public static final Integer INDEX_EVERY_10TH = new Integer(10);

   /**
    * Constant representing frequency of occurances.
    */
   public static final Integer INDEX_EVERY_11TH = new Integer(11);

   /**
    * Constant representing frequency of occurances.
    */
   public static final Integer INDEX_EVERY_12TH = new Integer(12);

   /**
    * Constant typically representing which Xday of the month.
    */
   public static final Integer FIRST = new Integer(1);

   /**
    * Constant typically representing which Xday of the month.
    */
   public static final Integer SECOND = new Integer(2);

   /**
    * Constant typically representing which Xday of the month.
    */
   public static final Integer THIRD = new Integer(3);

   /**
    * Constant typically representing which Xday of the month.
    */
   public static final Integer FOURTH = new Integer(4);

   /**
    * Constant typically representing which Xday of the month.
    */
   public static final Integer LAST = new Integer(5);
   
   /**
    * Constant containing the record number associated with this record.
    */
   static final int RECORD_NUMBER = 72;
}
