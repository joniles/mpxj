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
public class RecurringTask extends MPXRecord
{
   /**
    * Default constructor.
    *
    * @param file the parent file to which this record belongs.
    * @throws MPXException normally thrown when parsing fails
    */
   RecurringTask (MPXFile file)
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
   RecurringTask (MPXFile file, Record record)
      throws MPXException
   {
      super (file, MAX_FIELDS);

      setID(record.getString(0));
      setStartDate(record.getDate(1));
      setFinishDate(record.getDate(2));
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
      setYearlyBoxDate(record.getDate(23));
   }


   /**
    * ID, Unique ID of task to which this refers
    *
    * @return - String ID
    */
   public String getID ()
   {
      return ((String)get(ID));
   }

   /**
    * ID, Unique ID of task to which this refers
    *
    * @param id - String ID
    */
   public void setID (String id)
   {
      put (ID, id);
   }

   /**
    * Gets the start date of this recurring task
    *
    * @return date to start recurring task
    */
   public Date getStartDate ()
   {
      return ((Date)get(START_DATE));
   }

   /**
    * Sets the start date of this recurring task
    *
    * @param val date to start recurring task
    */
   public void setStartDate (Date val)
   {
      putDate (START_DATE, val);
   }

   /**
    * Gets the finish date of this recurring task
    *
    * @return date to finish recurring task
    */
   public Date getFinishDate ()
   {
      return ((Date)get(FINISH_DATE));
   }

   /**
    * Sets the finish date of this recurring task
    *
    * @param val date to finish recurring task
    */
   public void setFinishDate (Date val)
   {
      putDate (FINISH_DATE, val);
   }

   /**
    * Duration in minutes
    *
    * @return int value of duration, in minutes
    */
   public int getDurationValue ()
   {
      return (getIntValue (DURATION));
   }

   /**
    * Duration in minutes
    *
    * @return int value of duration, in minutes
    */
   public Integer getDuration ()
   {
      return ((Integer)get (DURATION));
   }

   /**
    * Duration in minutes
    *
    * @param val Integer value of duration in minutes
    */
   public void setDuration (int val)
   {
      put (DURATION,val);
   }

   /**
    * Duration in minutes
    *
    * @param val Integer value of duration in minutes
    */
   public void setDuration (Integer val)
   {
      put (DURATION,val);
   }

   /**
    * Duration type
    *
    * @return String type
    */
   public String getDurationType ()
   {
      return ((String)get(DURATION_TYPE));
   }

   /**
    * Unimportant as Duration appears as minutes.
    *
    * @param val int type
    */
   public void setDurationType (String val)
   {
      put (DURATION_TYPE, val);
   }

   /**
    * Sets the number of occurance of the task
    *
    * @return number of occurances
    */
   public int getNumberOfOccurancesValue ()
   {
      return (getIntValue (NO_OF_OCCURANCES));
   }

   /**
    * Sets the number of occurance of the task
    *
    * @return number of occurances
    */
   public Integer getNumberOfOccurances ()
   {
      return ((Integer)get (NO_OF_OCCURANCES));
   }

   /**
    * Gets the number of occurance of the task
    *
    * @param val number of occurances
    */
   public void setNumberOfOccurances (int val)
   {
      put (NO_OF_OCCURANCES, val);
   }

   /**
    * Gets the number of occurance of the task
    *
    * @param val number of occurances
    */
   public void setNumberOfOccurances (Integer val)
   {
      put (NO_OF_OCCURANCES, val);
   }

   /**
    * Refers to the 'This Occurs..' box of the MSP Recurring Task infobox.
    * The options are :- 1 - DAILY, 4-WEEKLY , 8-MONTHLY , 16 - YEARLY
    * @see: #PERIOD_DAILY frequency constants
    *
    * @return - int representing type
    */
   public int getRecurranceTypeValue ()
   {
      return (getIntValue (RECURRANCE_TYPE));
   }

   /**
    * Refers to the 'This Occurs..' box of the MSP Recurring Task infobox.
    * The options are :- 1 - DAILY, 4-WEEKLY , 8-MONTHLY , 16 - YEARLY
    * @see: #PERIOD_DAILY frequency constants
    *
    * @return - int representing type
    */
   public Integer getRecurranceType ()
   {
      return ((Integer)get (RECURRANCE_TYPE));
   }

   /**
    * Refers to the 'This Occurs..' box of the MSP Recurring Task infobox.
    * The options are :- 1 - DAILY, 4-WEEKLY , 8-MONTHLY , 16 - YEARLY
    * @see: #PERIOD_DAILY frequency constants
    *
    * @param val - int representing type
    */
   public void setRecurranceType (int val)
   {
      put (RECURRANCE_TYPE, val);
   }

   /**
    * Refers to the 'This Occurs..' box of the MSP Recurring Task infobox.
    * The options are :- 1 - DAILY, 4-WEEKLY , 8-MONTHLY , 16 - YEARLY
    * @see: #PERIOD_DAILY frequency constants
    *
    * @param val - int representing type
    */
   public void setRecurranceType (Integer val)
   {
      put (RECURRANCE_TYPE, val);
   }

   /**
    * Gets the selection in Length Options box. Top option - to a specific date.  Value 1
    * Bottom option - no of occurances.  value 0
    * @return  value of this option set
    */
   public int getLengthRadioIndexValue ()
   {
      return (getIntValue (LENGTH_RADIO_INDEX));
   }

   /**
    * Gets the selection in Length Options box. Top option - to a specific date.  Value 1
    * Bottom option - no of occurances.  value 0
    * @return  value of this option set
    */
   public Integer getLengthRadioIndex ()
   {
      return ((Integer)get (LENGTH_RADIO_INDEX));
   }

   /**
    * Sets the selection in Length Options box.
    * Top option - to a specific date.  Value 1
    * Bottom option - no of occurances.  value 0
    *
    * @param val   value of this option set
    */
   public void setLengthRadioIndex (int val)
   {
      put (LENGTH_RADIO_INDEX, val);
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
      put (LENGTH_RADIO_INDEX, val);
   }

   /**
    * Referring to the 2 radio buttons in the 'Daily' option box
    * of the MSP Recurring Task infobox.
    * The top option (Day) = 0 .
    * The bottom option (Workday) = 1
    *
    * @return - int, values as above
    */
   public int getDailyBoxRadioIndexValue ()
   {
      return (getIntValue (DAILY_BOX_RADIO_INDEX));
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
      return ((Integer)get (DAILY_BOX_RADIO_INDEX));
   }

   /**
    * Referring to the 2 radio buttons in the 'Daily' option box of the
    * MSP Recurring Task infobox.
    * The top option (Day) = 0 .
    * The bottom option (Workday) = 1
    *
    * @param val - int, values as above
    */
   public void setDailyBoxRadioIndex (int val)
   {
      put (DAILY_BOX_RADIO_INDEX, val);
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
      put (DAILY_BOX_RADIO_INDEX, val);
   }

   /**
    * Refers to the 'Weekly' option boxes of the MSP Recurring Task infobox.
    * This is an index of days of the week and is represented as a 7 digit
    * string of 0/1 values.
    * The string is eg '0010010' if 'Tue' and 'Fri' are selected
    * @see: #PERIOD_DAILY frequency constants
    *
    * @return - String
    */
   public String getWeeklyBoxDayOfWeekIndex ()
   {
      return ((String)get(WEEKLY_BOX_DAY_OF_WEEK_INDEX));
   }

   /**
    * Refers to the 'Weekly' option boxes of the MSP Recurring Task infobox.
    * This is an index of days of the week and is represented as a
    * 7 digit string of 0/1 values.
    * The string is eg '0010010' if 'Tue' and 'Fri' are selected
    * @see #PERIOD_DAILY frequency constants
    * @param val - String list of day values
    */
   public void setWeeklyBoxDayOfWeekIndex (String val)
   {
      put (WEEKLY_BOX_DAY_OF_WEEK_INDEX, val);
   }

   /**
    * Refers to the 'Monthly' option boxes of the MSP Recurring Task infobox.
    * The top option (Day x of...) = 0 .
    * The bottom option (The Xth day of...) = 1
    * @return - 0 or 1
    */
   public int getMonthlyBoxRadioIndexValue ()
   {
      return (getIntValue(MONTHLY_BOX_RADIO_INDEX));
   }

   /**
    * Refers to the 'Monthly' option boxes of the MSP Recurring Task infobox.
    * The top option (Day x of...) = 0 .
    * The bottom option (The Xth day of...) = 1
    * @return - 0 or 1
    */
   public Integer getMonthlyBoxRadioIndex ()
   {
      return ((Integer)get (MONTHLY_BOX_RADIO_INDEX));
   }

   /**
    * Refers to the 'Monthly' option boxes of the MSP Recurring Task infobox.
    * The top option (Day x of...) = 0 .
    * The bottom option (The Xth day of...) = 1
    *
    * @param val 0 or 1
    */
   public void setMonthlyBoxRadioIndex (int val)
   {
      put (MONTHLY_BOX_RADIO_INDEX,val);
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
      put (MONTHLY_BOX_RADIO_INDEX,val);
   }

   /**
    * Refers to the 'Yearly' option boxes of the MSP Recurring Task infobox.
    * The top option (Date) = 1 .
    * The bottom option (The Xth day of...) = 0
    * @return - 1 or 0, int value
    */
   public int getYearlyBoxRadioIndexValue ()
   {
      return (getIntValue(YEARLY_BOX_RADIO_INDEX));
   }

   /**
    * Refers to the 'Yearly' option boxes of the MSP Recurring Task infobox.
    * The top option (Date) = 1 .
    * The bottom option (The Xth day of...) = 0
    * @return - 1 or 0, int value
    */
   public Integer getYearlyBoxRadioIndex ()
   {
      return ((Integer)get (YEARLY_BOX_RADIO_INDEX));
   }

   /**
    * Refers to the 'Yearly' option boxes of the MSP Recurring Task infobox.
    * The top option (Date) = 1 .
    * The bottom option (The Xth day of...) = 0
    *
    * @param val - 1 or 0, int value
    */
   public void setYearlyBoxRadioIndex (int val)
   {
      put (YEARLY_BOX_RADIO_INDEX, val);
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
      put (YEARLY_BOX_RADIO_INDEX, val);
   }

   /**
    * Refers to the 'Daily' option boxes of the MSP Recurring Task infobox.
    * 'Every...' eg 'Every 7th'
    * @see #INDEX_TYPE_EVERY CONSTANTS eg 'Every 7th','Every Other','Every'
    *
    * @return - int index - eg 7=7th,1=every
    */
   public int getDailyBoxComboIndexValue ()
   {
      return (getIntValue (DAILY_BOX_COMBO_INDEX));
   }

   /**
    * Refers to the 'Daily' option boxes of the MSP Recurring Task infobox.
    * 'Every...' eg 'Every 7th'
    * @see #INDEX_TYPE_EVERY CONSTANTS eg 'Every 7th','Every Other','Every'
    *
    * @return - int index - eg 7=7th,1=every
    */
   public Integer getDailyBoxComboIndex ()
   {
      return ((Integer)get (DAILY_BOX_COMBO_INDEX));
   }

   /**
    * Refers to the 'Daily' option boxes of the MSP Recurring Task infobox.
    * 'Every...' eg 'Every 7th'
    * @see #INDEX_TYPE_EVERY CONSTANTS eg 'Every 7th','Every Other','Every'
    *
    * @param val - int index - eg 7=7th,1=every
    */
   public void setDailyBoxComboIndex (int val)
   {
      put (DAILY_BOX_COMBO_INDEX,val);
   }

   /**
    * Refers to the 'Daily' option boxes of the MSP Recurring Task infobox.
    * 'Every...' eg 'Every 7th'
    * @see #INDEX_TYPE_EVERY CONSTANTS eg 'Every 7th','Every Other','Every'
    *
    * @param val - int index - eg 7=7th,1=every
    */
   public void setDailyBoxComboIndex (Integer val)
   {
      put (DAILY_BOX_COMBO_INDEX,val);
   }

   /**
    * Refers to the 'Weekly' option boxes of the MSP Recurring Task infobox.
    * 'Every...' eg 'Every 7th'
    * @see #INDEX_TYPE_EVERY CONSTANTS eg 'Every 7th','Every Other','Every'
    *
    * @return -  int index - eg 7=7th,1=every
    */
   public int getWeeklyBoxComboIndexValue ()
   {
      return (getIntValue (WEEKLY_BOX_COMBO_INDEX));
   }

   /**
    * Refers to the 'Weekly' option boxes of the MSP Recurring Task infobox.
    * 'Every...' eg 'Every 7th'
    * @see #INDEX_TYPE_EVERY CONSTANTS eg 'Every 7th','Every Other','Every'
    *
    * @return -  int index - eg 7=7th,1=every
    */
   public Integer getWeeklyBoxComboIndex ()
   {
      return ((Integer)get (WEEKLY_BOX_COMBO_INDEX));
   }

   /**
    * Refers to the 'Weekly' option boxes of the MSP Recurring Task infobox.
    * 'Every...' eg 'Every 7th'
    * @see #INDEX_TYPE_EVERY CONSTANTS eg 'Every 7th','Every Other','Every'
    *
    * @param val - int index - eg 7=7th,1=every
    */
   public void setWeeklyBoxComboIndex (int val)
   {
      put (WEEKLY_BOX_COMBO_INDEX, val);
   }

   /**
    * Refers to the 'Weekly' option boxes of the MSP Recurring Task infobox.
    * 'Every...' eg 'Every 7th'
    * @see #INDEX_TYPE_EVERY CONSTANTS eg 'Every 7th','Every Other','Every'
    *
    * @param val - int index - eg 7=7th,1=every
    */
   public void setWeeklyBoxComboIndex (Integer val)
   {
      put (WEEKLY_BOX_COMBO_INDEX, val);
   }

   /**
    * Refers to the 'The' (eg Second) 'Monthly' option boxes of the MSP
    * Recurring Task infobox.
    * eg first tueday.
    * Values for first,second,third,fourth and last.
    * @see #FIRST CONSTANTS eg 'FIRST'=1,'LAST'=5,'THIRD'=3
    *
    * @return - int value  of constant
    */
   public int getMonthlyBoxFirstLastComboIndexValue ()
   {
      return (getIntValue (MONTHLY_BOX_FIRSTLAST_COMBO_INDEX));
   }

   /**
    * Refers to the 'The' (eg Second) 'Monthly' option boxes of the MSP
    * Recurring Task infobox.
    * eg first tueday.
    * Values for first,second,third,fourth and last.
    * @see #FIRST CONSTANTS eg 'FIRST'=1,'LAST'=5,'THIRD'=3
    *
    * @return - int value  of constant
    */
   public Integer getMonthlyBoxFirstLastComboIndex ()
   {
      return ((Integer) get (MONTHLY_BOX_FIRSTLAST_COMBO_INDEX));
   }

   /**
    * Refers to the 'The' (eg Second) 'Monthly' option boxes of the
    * MSP Recurring Task infobox.
    * eg first tueday.
    * Values for first,second,third,fourth and last.
    * @see #FIRST CONSTANTS eg 'FIRST'=1,'LAST'=5,'THIRD'=3
    *
    * @param val - int value  of constant
    */
   public void setMonthlyBoxFirstLastComboIndex (int val)
   {
      put (MONTHLY_BOX_FIRSTLAST_COMBO_INDEX, val);
   }

   /**
    * Refers to the 'The' (eg Second) 'Monthly' option boxes of the
    * MSP Recurring Task infobox.
    * eg first tueday.
    * Values for first,second,third,fourth and last.
    * @see #FIRST CONSTANTS eg 'FIRST'=1,'LAST'=5,'THIRD'=3
    *
    * @param val - int value  of constant
    */
   public void setMonthlyBoxFirstLastComboIndex (Integer val)
   {
      put (MONTHLY_BOX_FIRSTLAST_COMBO_INDEX, val);
   }

   /**
    * Refers to the 'Monthly' option boxes of the MSP Recurring Task infobox.
    * eg Wednesday.
    * Values for day of the week
    * @see #SUNDAY CONSTANTS eg 'MONDAY'=2,'FRIDAY'=5
    * @return - int value of day
    */
   public int getMonthlyBoxDayComboIndexValue ()
   {
      return (getIntValue (MONTHLY_BOX_DAY_COMBO_INDEX));
   }

   /**
    * Refers to the 'Monthly' option boxes of the MSP Recurring Task infobox.
    * eg Wednesday.
    * Values for day of the week
    * @see #SUNDAY CONSTANTS eg 'MONDAY'=2,'FRIDAY'=5
    * @return - int value of day
    */
   public Integer getMonthlyBoxDayComboIndex ()
   {
      return ((Integer)get (MONTHLY_BOX_DAY_COMBO_INDEX));
   }

   /**
    * Refers to the 'Monthly' option boxes of the MSP Recurring Task infobox.
    * eg Wednesday.
    * Values for day of the week
    * @param val - int value of day
    * @see #SUNDAY CONSTANTS eg 'MONDAY'=2,'FRIDAY'=5
    */
   public void setMonthlyBoxDayComboIndex (int val)
   {
      put (MONTHLY_BOX_DAY_COMBO_INDEX, val);
   }

   /**
    * Refers to the 'Monthly' option boxes of the MSP Recurring Task infobox.
    * eg Wednesday.
    * Values for day of the week
    * @param val - int value of day
    * @see #SUNDAY CONSTANTS eg 'MONDAY'=2,'FRIDAY'=5
    */
   public void setMonthlyBoxDayComboIndex (Integer val)
   {
      put (MONTHLY_BOX_DAY_COMBO_INDEX, val);
   }

   /**
    * Refers to the 'Monthly' option boxes of the MSP Recurring Task infobox.
    * If the bottom radio button is selected (eg. The 3rd Tuesday),
    * this value is the content of the
    * 'Every...' combo box. eg 'Every 5th Month'
    * @see #INDEX_TYPE_EVERY CONSTANTS eg 'Every 7th','Every Other','Every'
    *
    * @return - int value of constant
    */
   public int getMonthlyBoxBottomRadioFrequencyComboIndexValue ()
   {
      return (getIntValue (MONTHLY_BOX_BOTTOM_RADIO_FREQUENCY_COMBO_INDEX));
   }

   /**
    * Refers to the 'Monthly' option boxes of the MSP Recurring Task infobox.
    * If the bottom radio button is selected (eg. The 3rd Tuesday),
    * this value is the content of the
    * 'Every...' combo box. eg 'Every 5th Month'
    * @see #INDEX_TYPE_EVERY CONSTANTS eg 'Every 7th','Every Other','Every'
    *
    * @return - int value of constant
    */
   public Integer getMonthlyBoxBottomRadioFrequencyComboIndex ()
   {
      return ((Integer)get (MONTHLY_BOX_BOTTOM_RADIO_FREQUENCY_COMBO_INDEX));
   }

   /**
    * Refers to the 'Monthly' option boxes of the MSP Recurring Task infobox.
    * If the bottom radio button is selected (eg. The 3rd Tuesday), this value
    * is the content of the
    * 'Every...' combo box. eg 'Every 5th Month'
    * @see #INDEX_TYPE_EVERY CONSTANTS eg 'Every 7th','Every Other','Every'
    *
    * @param val - int value of constant
    */
   public void setMonthlyBoxBottomRadioFrequencyComboIndex (int val)
   {
      put (MONTHLY_BOX_BOTTOM_RADIO_FREQUENCY_COMBO_INDEX, val);
   }

   /**
    * Refers to the 'Monthly' option boxes of the MSP Recurring Task infobox.
    * If the bottom radio button is selected (eg. The 3rd Tuesday), this value
    * is the content of the
    * 'Every...' combo box. eg 'Every 5th Month'
    * @see #INDEX_TYPE_EVERY CONSTANTS eg 'Every 7th','Every Other','Every'
    *
    * @param val - int value of constant
    */
   public void setMonthlyBoxBottomRadioFrequencyComboIndex (Integer val)
   {
      put (MONTHLY_BOX_BOTTOM_RADIO_FREQUENCY_COMBO_INDEX, val);
   }

   /**
    * Refers to the 'Monthly' option boxes of the MSP Recurring Task infobox.
    * This refers to the box Day X, (where X is 1-31) of the month.
    * @return - int value of day 1-31
    */
   public int getMonthlyBoxDayIndexValue ()
   {
      return (getIntValue (MONTHLY_BOX_DAY_INDEX));
   }

   /**
    * Refers to the 'Monthly' option boxes of the MSP Recurring Task infobox.
    * This refers to the box Day X, (where X is 1-31) of the month.
    * @return - int value of day 1-31
    */
   public Integer getMonthlyBoxDayIndex ()
   {
      return ((Integer)get (MONTHLY_BOX_DAY_INDEX));
   }

   /**
    * Refers to the 'Monthly' option boxes of the MSP Recurring Task infobox.
    * This refers to the box Day X, (where X is 1-31) of the month.
    * @param val - int value of day 1-31
    */
   public void setMonthlyBoxDayIndex (int val)
   {
      put (MONTHLY_BOX_DAY_INDEX, val);
   }

   /**
    * Refers to the 'Monthly' option boxes of the MSP Recurring Task infobox.
    * This refers to the box Day X, (where X is 1-31) of the month.
    * @param val - int value of day 1-31
    */
   public void setMonthlyBoxDayIndex (Integer val)
   {
      put (MONTHLY_BOX_DAY_INDEX, val);
   }

   /**
    * Refers to the 'Monthly' option boxes of the MSP Recurring Task infobox.
    * If the top radio button is selected (eg. The Xth(day) of..),
    * this value is the content of the
    * 'Every...' combo box. eg 'Every 5th Month'
    * @return - int value of index constant
    * @see #INDEX_TYPE_EVERY CONSTANTS eg 'Every 7th','Every Other','Every'
    */
   public int getMonthlyBoxTopRadioFrequencyComboIndexValue ()
   {
      return (getIntValue (MONTHLY_BOX_TOP_RADIO_FREQUENCY_COMBO_INDEX));
   }

   /**
    * Refers to the 'Monthly' option boxes of the MSP Recurring Task infobox.
    * If the top radio button is selected (eg. The Xth(day) of..),
    * this value is the content of the
    * 'Every...' combo box. eg 'Every 5th Month'
    * @return - int value of index constant
    * @see #INDEX_TYPE_EVERY CONSTANTS eg 'Every 7th','Every Other','Every'
    */
   public Integer getMonthlyBoxTopRadioFrequencyComboIndex ()
   {
      return ((Integer)get (MONTHLY_BOX_TOP_RADIO_FREQUENCY_COMBO_INDEX));
   }

   /**
    * Refers to the 'Monthly' option boxes of the MSP Recurring Task infobox.
    * If the top radio button is selected (eg. The Xth(day) of..), this value
    * is the content of the
    * 'Every...' combo box. eg 'Every 5th Month'
    * @param val - int value of index constant
    *
    * @see #INDEX_TYPE_EVERY CONSTANTS eg 'Every 7th','Every Other','Every'
    */
   public void setMonthlyBoxTopRadioFrequencyComboIndex (int val)
   {
      put (MONTHLY_BOX_TOP_RADIO_FREQUENCY_COMBO_INDEX,val);
   }

   /**
    * Refers to the 'Monthly' option boxes of the MSP Recurring Task infobox.
    * If the top radio button is selected (eg. The Xth(day) of..), this value
    * is the content of the
    * 'Every...' combo box. eg 'Every 5th Month'
    * @param val - int value of index constant
    *
    * @see #INDEX_TYPE_EVERY CONSTANTS eg 'Every 7th','Every Other','Every'
    */
   public void setMonthlyBoxTopRadioFrequencyComboIndex (Integer val)
   {
      put (MONTHLY_BOX_TOP_RADIO_FREQUENCY_COMBO_INDEX,val);
   }

   /**
    * Refers to the 'Yearly' option boxes of the MSP Recurring Task infobox.
    * eg FIRST tueday of December.
    * Values for first,second,third,fourth and last.
    * @see #FIRST CONSTANTS eg 'FIRST'=1,'LAST'=5,'THIRD'=3
    *
    * @return - int value of index
    */
   public int getYearlyBoxFirstLastComboIndexValue ()
   {
      return (getIntValue (FIRSTLAST_COMBO_INDEX_YEARLY_BOX));
   }

   /**
    * Refers to the 'Yearly' option boxes of the MSP Recurring Task infobox.
    * eg FIRST tueday of December.
    * Values for first,second,third,fourth and last.
    * @see #FIRST CONSTANTS eg 'FIRST'=1,'LAST'=5,'THIRD'=3
    *
    * @return - int value of index
    */
   public Integer getYearlyBoxFirstLastComboIndex ()
   {
      return ((Integer)get (FIRSTLAST_COMBO_INDEX_YEARLY_BOX));
   }

   /**
    * Refers to the 'Yearly' option boxes of the MSP Recurring Task infobox.
    * eg FIRST tueday of December.
    * Values for first,second,third,fourth and last.
    * @see #FIRST CONSTANTS eg 'FIRST'=1,'LAST'=5,'THIRD'=3
    *
    * @param val - int value of index
    */
   public void setYearlyBoxFirstLastComboIndex (int val)
   {
      put (FIRSTLAST_COMBO_INDEX_YEARLY_BOX, val);
   }

   /**
    * Refers to the 'Yearly' option boxes of the MSP Recurring Task infobox.
    * eg FIRST tueday of December.
    * Values for first,second,third,fourth and last.
    * @see #FIRST CONSTANTS eg 'FIRST'=1,'LAST'=5,'THIRD'=3
    *
    * @param val - int value of index
    */
   public void setYearlyBoxFirstLastComboIndex (Integer val)
   {
      put (FIRSTLAST_COMBO_INDEX_YEARLY_BOX, val);
   }

   /**
    * Refers to the 'Yearly' option boxes of the MSP Recurring Task infobox.
    * eg first TUESDAY of December.
    * Values for day of the week
    * @see #SUNDAY CONSTANTS eg 'MONDAY'=2,'FRIDAY'=5
    *
    * @return - int value of constant
    */
   public int getYearlyBoxDayComboIndexValue ()
   {
      return (getIntValue (DAY_COMBO_INDEX_YEARLY_BOX));
   }

   /**
    * Refers to the 'Yearly' option boxes of the MSP Recurring Task infobox.
    * eg first TUESDAY of December.
    * Values for day of the week
    * @see #SUNDAY CONSTANTS eg 'MONDAY'=2,'FRIDAY'=5
    *
    * @return - int value of constant
    */
   public Integer getYearlyBoxDayComboIndex ()
   {
      return ((Integer)get (DAY_COMBO_INDEX_YEARLY_BOX));
   }

   /**
    * Refers to the 'Yearly' option boxes of the MSP Recurring Task infobox.
    * eg first TUESDAY of December.
    * Values for day of the week
    * @see #SUNDAY CONSTANTS eg 'MONDAY'=2,'FRIDAY'=5
    *
    * @param val - int value of constant
    */
   public void setYearlyBoxDayComboIndex (int val)
   {
      put (DAY_COMBO_INDEX_YEARLY_BOX, val);
   }

   /**
    * Refers to the 'Yearly' option boxes of the MSP Recurring Task infobox.
    * eg first TUESDAY of December.
    * Values for day of the week
    * @see #SUNDAY CONSTANTS eg 'MONDAY'=2,'FRIDAY'=5
    *
    * @param val - int value of constant
    */
   public void setYearlyBoxDayComboIndex (Integer val)
   {
      put (DAY_COMBO_INDEX_YEARLY_BOX, val);
   }

   /**
    * Refers to the 'Yearly' option boxes of the MSP Recurring Task infobox.
    * eg first tuesday of MARCH.
    * Values for month of the year
    * @see  #JANUARY CONSTANTS eg 'JANUARY','MAY'
    *
    * @return - int value of index
    */
   public int getYearlyBoxMonthComboIndexValue ()
   {
      return (getIntValue (MONTH_COMBO_INDEX_YEARLY_BOX));
   }

   /**
    * Refers to the 'Yearly' option boxes of the MSP Recurring Task infobox.
    * eg first tuesday of MARCH.
    * Values for month of the year
    * @see  #JANUARY CONSTANTS eg 'JANUARY','MAY'
    *
    * @return - int value of index
    */
   public Integer getYearlyBoxMonthComboIndex ()
   {
      return ((Integer)get (MONTH_COMBO_INDEX_YEARLY_BOX));
   }

   /**
    * Refers to the 'Yearly' option boxes of the MSP Recurring Task infobox.
    * eg first tuesday of MARCH.
    * Values for month of the year
    * @see #JANUARY CONSTANTS eg 'JANUARY','MAY'
    *
    * @param val - int value of index
    */
   public void setYearlyBoxMonthComboIndex (int val)
   {
      put (MONTH_COMBO_INDEX_YEARLY_BOX, val);
   }

   /**
    * Refers to the 'Yearly' option boxes of the MSP Recurring Task infobox.
    * eg first tuesday of MARCH.
    * Values for month of the year
    * @see #JANUARY CONSTANTS eg 'JANUARY','MAY'
    *
    * @param val - int value of index
    */
   public void setYearlyBoxMonthComboIndex (Integer val)
   {
      put (MONTH_COMBO_INDEX_YEARLY_BOX, val);
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
      return ((Date)get(YEARLY_BOX_DATE));
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
      putDate (YEARLY_BOX_DATE, val);
   }

   /**
    * Not sure index?
    *
    * @return - Integer
    */
   public int getNotSureIndexValue ()
   {
      return (getIntValue (NOT_SURE_INDEX));
   }

   /**
    * Not sure index?
    *
    * @return - Integer
    */
   public Integer getNotSureIndex ()
   {
      return ((Integer)get (NOT_SURE_INDEX));
   }

   /**
    * Not sure index?
    *
    * @param val - Integer
    */
   public void setNotSureIndex (int val)
   {
      put (NOT_SURE_INDEX, val);
   }

   /**
    * Not sure index?
    *
    * @param val - Integer
    */
   public void setNotSureIndex (Integer val)
   {
      put (NOT_SURE_INDEX, val);
   }

   /**
    * This method generates a string in MPX format representing the
    * contents of this record.
    *
    * @return string containing the data for this record in MPX format.
    */
   public String toString()
   {
      return (toString(RECORD_NUMBER));
   }


   /**
    * Constant representing frequency - Daily
    */
   public static final Integer PERIOD_DAILY = new Integer(1);

   /**
    * Constant representing frequency - Weekly
    */
   public static final Integer PERIOD_WEEKLY = new Integer(4);

   /**
    * Constant representing frequency - Monthly
    */
   public static final Integer PERIOD_MONTHLY = new Integer(8);

   /**
    * Constant representing frequency - Annually
    */
   public static final Integer PERIOD_YEARLY = new Integer(16);

   /**
    * Constant representing day of week - Sunday
    */
   public static final Integer SUNDAY = new Integer(0);

   /**
    * Constant representing day of week - Monday
    */
   public static final Integer MONDAY = new Integer(1);

   /**
    * Constant representing day of week - Tuesday
    */
   public static final Integer TUESDAY = new Integer(2);

   /**
    * Constant representing day of week - Wednesday
    */
   public static final Integer WEDNESDAY = new Integer(3);

   /**
    * Constant representing day of week - Thursday
    */
   public static final Integer THURSDAY = new Integer(4);

   /**
    * Constant representing day of week - Friday
    */
   public static final Integer FRIDAY = new Integer(5);

   /**
    * Constant representing day of week - Saturday
    */
   public static final Integer SATURDAY = new Integer(6);

   /**
    * Constant representing month of year - January
    */
   public static final Integer JANUARY = new Integer(1);

   /**
    * Constant representing month of year - February
    */
   public static final Integer FEBUARY = new Integer(2);

   /**
    * Constant representing month of year - March
    */
   public static final Integer MARCH = new Integer(3);

   /**
    * Constant representing month of year - April
    */
   public static final Integer APRIL = new Integer(4);

   /**
    * Constant representing month of year - May
    */
   public static final Integer MAY = new Integer(5);

   /**
    * Constant representing month of year - June
    */
   public static final Integer JUNE = new Integer(6);

   /**
    * Constant representing month of year - July
    */
   public static final Integer JULY = new Integer(7);

   /**
    * Constant representing month of year - August
    */
   public static final Integer AUGUST = new Integer(8);

   /**
    * Constant representing month of year - September
    */
   public static final Integer SEPTEMBER = new Integer(9);

   /**
    * Constant representing month of year - Obtober
    */
   public static final Integer OCTOBER = new Integer(10);

   /**
    * Constant representing month of year - November
    */
   public static final Integer NOVEMBER = new Integer(11);

   /**
    * Constant representing month of year - December
    */
   public static final Integer DECEMBER = new Integer(12);

   /**
    * Constant representing frequency of occurances
    */
   public static final Integer INDEX_TYPE_EVERY = new Integer(1);

   /**
    * Constant representing frequency of occurances
    */
   public static final Integer INDEX_EVERY_OTHER = new Integer(2);

   /**
    * Constant representing frequency of occurances
    */
   public static final Integer INDEX_EVERY_3RD = new Integer(3);

   /**
    * Constant representing frequency of occurances
    */
   public static final Integer INDEX_EVERY_4TH = new Integer(4);

   /**
    * Constant representing frequency of occurances
    */
   public static final Integer INDEX_EVERY_5TH = new Integer(5);

   /**
    * Constant representing frequency of occurances
    */
   public static final Integer INDEX_EVERY_6TH = new Integer(6);

   /**
    * Constant representing frequency of occurances
    */
   public static final Integer INDEX_EVERY_7TH = new Integer(7);

   /**
    * Constant representing frequency of occurances
    */
   public static final Integer INDEX_EVERY_8TH = new Integer(8);

   /**
    * Constant representing frequency of occurances
    */
   public static final Integer INDEX_EVERY_9TH = new Integer(9);

   /**
    * Constant representing frequency of occurances
    */
   public static final Integer INDEX_EVERY_10TH = new Integer(10);

   /**
    * Constant representing frequency of occurances
    */
   public static final Integer INDEX_EVERY_11TH = new Integer(11);

   /**
    * Constant representing frequency of occurances
    */
   public static final Integer INDEX_EVERY_12TH = new Integer(12);

   /**
    * Constant typically representing which Xday of the month
    */
   public static final Integer FIRST = new Integer(1);

   /**
    * Constant typically representing which Xday of the month
    */
   public static final Integer SECOND = new Integer(2);

   /**
    * Constant typically representing which Xday of the month
    */
   public static final Integer THIRD = new Integer(3);

   /**
    * Constant typically representing which Xday of the month
    */
   public static final Integer FOURTH = new Integer(4);

   /**
    * Constant typically representing which Xday of the month
    */
   public static final Integer LAST = new Integer(5);

   /**
    * Unique ID. If this <tt>RecurrentTask</tt> is a child of another recurring task,
    * then only this field appears, signifying the parent.
    */
   private static final int ID = 0;

   /**
    * Start date of recurring task
    */
   private static final int START_DATE = 1;

   /**
    * End final date of recurring task
    */
   private static final int FINISH_DATE = 2;

   /**
    * Duration as minutes.
    */
   private static final int DURATION = 3;

   /**
    * Unimportant as Duration appears as minutes.
    */
   private static final int DURATION_TYPE = 4;

   /**
    * No of Occurances scheduled
    */
   private static final int NO_OF_OCCURANCES = 5;

   /**
    * Refers to the 'This Occurs..' box of the MSP Recurring Task infobox.
    * The options are :- 1 - DAILY, 4-WEEKLY , 8-MONTHLY , 16 - YEARLY
    * @see #PERIOD_DAILY frequency constants
    */
   private static final int RECURRANCE_TYPE = 6;

   /**
    * This field seems redundant
    */
   private static final int NOT_SURE_INDEX = 7;

  /**
   * Referring to the 2 radio buttons in the 'Length' option box
   * of the MSP Recurring Task infobox.
   * The top option (To X Date) = 1 .
   * The bottom option (For X Occurances) = 0
   */
   private static final int LENGTH_RADIO_INDEX = 8;

   /**
    * Referring to the 2 radio buttons in the 'Daily' option box
    * of the MSP Recurring Task infobox.
    * The top option (Day) = 0 .
    * The bottom option (Workday) = 1
    */
   private static final int DAILY_BOX_RADIO_INDEX = 9;

   /**
    * Refers to the 'Weekly' option boxes of the MSP Recurring Task infobox.
    * This is an index of days of the week and is represented as a 7
    * digit string of 0/1 values.
    * The string is eg '0010010' if 'Tue' and 'Fri' are selected
    * @see #PERIOD_DAILY frequency constants
    */
   private static final int WEEKLY_BOX_DAY_OF_WEEK_INDEX = 10;

   /**
    * Refers to the 'Monthly' option boxes of the MSP Recurring Task infobox.
    * The top option (Day x of...) = 0 .
    * The bottom option (The Xth day of...) = 1
    */
   private static final int MONTHLY_BOX_RADIO_INDEX = 11;

   /**
    * Refers to the 'Yearly' option boxes of the MSP Recurring Task infobox.
    * The top option (Date) = 1 .
    * The bottom option (The Xth day of...) = 0
    */
   private static final int YEARLY_BOX_RADIO_INDEX = 12;

   /**
    * Refers to the 'Daily' option boxes of the MSP Recurring Task infobox.
    * 'Every...' eg 'Every 7th'
    * @see #INDEX_TYPE_EVERY CONSTANTS eg  'Every 7th','Every Other','Every'
    */
   private static final int DAILY_BOX_COMBO_INDEX = 13;

   /**
    * Refers to the 'Weekly' option boxes of the MSP Recurring Task infobox.
    * 'Every...' eg 'Every 7th'
    * @see #INDEX_TYPE_EVERY CONSTANTS eg  'Every 7th','Every Other','Every'
    */
   private static final int WEEKLY_BOX_COMBO_INDEX = 14;

   /**
    * Refers to the 'The' (eg Second) 'Monthly' option boxes of the
    * MSP Recurring Task infobox.
    * eg first tueday.
    * Values for first,second,third,fourth and last.
    * @see #FIRST CONSTANTS eg 'FIRST'=1,'LAST'=5,'THIRD'=3
    */
   private static final int MONTHLY_BOX_FIRSTLAST_COMBO_INDEX = 15;

   /**
    * Refers to the 'Monthly' option boxes of the MSP Recurring Task infobox.
    * eg Wednesday.
    * Values for day of the week
    * @see #SUNDAY CONSTANTS eg 'MONDAY'=2,'FRIDAY'=5
    */
   private static final int MONTHLY_BOX_DAY_COMBO_INDEX = 16;

   /**
    * Refers to the 'Monthly' option boxes of the MSP Recurring Task infobox.
    * If the bottom radio button is selected (eg. The 3rd Tuesday), this value is the content of the
    * 'Every...' combo box. eg 'Every 5th Month'
    * @see #INDEX_TYPE_EVERY CONSTANTS eg  'Every 7th','Every Other','Every'
    */
   private static final int MONTHLY_BOX_BOTTOM_RADIO_FREQUENCY_COMBO_INDEX = 17;

   /**
    * Refers to the 'Monthly' option boxes of the MSP Recurring Task infobox.
    * This refers to the box Day X, (where X is 1-31) of the month.
    */
   private static final int MONTHLY_BOX_DAY_INDEX = 18;

   /**
    * Refers to the 'Monthly' option boxes of the MSP Recurring Task infobox.
    * If the top radio button is selected (eg. The Xth(day) of..), this value is the content of the
    * 'Every...' combo box. eg 'Every 5th Month'
    * @see #INDEX_TYPE_EVERY CONSTANTS eg  'Every 7th','Every Other','Every'
    */
   private static final int MONTHLY_BOX_TOP_RADIO_FREQUENCY_COMBO_INDEX = 19;

   /**
    * Refers to the 'Yearly' option boxes of the MSP Recurring Task infobox.
    * eg FIRST tueday of December.
    * Values for first,second,third,fourth and last.
    * @see #FIRST CONSTANTS eg 'FIRST'=1,'LAST'=5,'THIRD'=3
    */
   private static final int FIRSTLAST_COMBO_INDEX_YEARLY_BOX = 20;

   /**
    * Refers to the 'Yearly' option boxes of the MSP Recurring Task infobox.
    * eg first TUESDAY of December.
    * Values for day of the week
    * @see #SUNDAY CONSTANTS eg 'MONDAY'=2,'FRIDAY'=5
    */
   private static final int DAY_COMBO_INDEX_YEARLY_BOX = 21;

   /**
    * Refers to the 'Yearly' option boxes of the MSP Recurring Task infobox.
    * eg first tuesday of MARCH.
    * Values for month of the year
    * @see #JANUARY CONSTANTS eg 'JANUARY','MAY'
    */
   private static final int MONTH_COMBO_INDEX_YEARLY_BOX = 22;

   /**
    * Refers to the 'Yearly' option boxes of the MSP Recurring Task infobox.
    * This is the date shown when the top radio button is selected. eg 11 December.
    * This represents the first scheduled occurance of this <tt>RecurringTask</tt>
    */
   private static final int YEARLY_BOX_DATE = 23;

   /**
    * Maximum number of fields in this record.
    */
   private static final int MAX_FIELDS = 24;

   /**
    * Constant containing the record number associated with this record.
    */
   public static final int RECORD_NUMBER = 72;
}
