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
    */
   RecurringTask (MPXFile file)
   {
      super (file);
   }

   /**
    * Constructor used to create an instance of this class from data
    * taken from an MPXFile record.
    *
    * @param file the MPXFile object to which this record belongs.
    * @param record record containing the data for  this object.
    */
   RecurringTask (MPXFile file, Record record)
      throws MPXException
   {
      this(file);

      setID(record.getString(0));
      setStartDate(record.getDate(1));
      setFinishDate(record.getDate(2));
      setDuration(record.getInteger(3));
      setDurationType(record.getString(4));
      setNumberOfOccurances(record.getInteger(5));
      setRecurranceType(record.getByte(6));
      setNotSureIndex(record.getByte(7));
      setLengthRadioIndex(record.getInteger(8));
      setDailyBoxRadioIndex(record.getByte(9));
      setWeeklyBoxDayOfWeekIndex(record.getString(10));
      setMonthlyBoxRadioIndex(record.getByte(11));
      setYearlyBoxRadioIndex(record.getByte(12));
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
   public int getDuration ()
   {
      return (((Integer)get(DURATION)).intValue());
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
    * @param val byte type
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
   public int getNumberOfOccurances ()
   {
      return (((Integer)get(NO_OF_OCCURANCES)).intValue());
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
    * @return - byte representing type
    */
   public Byte getRecurranceType ()
   {
      return ((Byte)get(RECURRANCE_TYPE));
   }

   /**
    * Refers to the 'This Occurs..' box of the MSP Recurring Task infobox.
    * The options are :- 1 - DAILY, 4-WEEKLY , 8-MONTHLY , 16 - YEARLY
    * @see: #PERIOD_DAILY frequency constants
    *
    * @param val - byte representing type
    */
   public void setRecurranceType (Byte val)
   {
      put (RECURRANCE_TYPE, val);
   }

   /**
    * Gets the selection in Length Options box. Top option - to a specific date.  Value 1
    *  Bottom option - no of occurances.  value 0
    * @return  value of this option set
    */
   public int getLengthRadioIndex()
   {
      return (((Integer)get(LENGTH_RADIO_INDEX)).intValue());
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
    * @return - byte, values as above
    */
   public byte getDailyBoxRadioIndex ()
   {
      return (((Byte)get(DAILY_BOX_RADIO_INDEX)).byteValue());
   }

   /**
    * Referring to the 2 radio buttons in the 'Daily' option box of the
    * MSP Recurring Task infobox.
    * The top option (Day) = 0 .
    * The bottom option (Workday) = 1
    *
    * @param val - byte, values as above
    */
   public void setDailyBoxRadioIndex (Byte val)
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
   public byte getMonthlyBoxRadioIndex ()
   {
      return (((Byte)get(MONTHLY_BOX_RADIO_INDEX)).byteValue());
   }

   /**
    * Refers to the 'Monthly' option boxes of the MSP Recurring Task infobox.
    * The top option (Day x of...) = 0 .
    * The bottom option (The Xth day of...) = 1
    *
    * @param val 0 or 1
    */
   public void setMonthlyBoxRadioIndex (Byte val)
   {
      put (MONTHLY_BOX_RADIO_INDEX,val);
   }

   /**
    * Refers to the 'Yearly' option boxes of the MSP Recurring Task infobox.
    * The top option (Date) = 1 .
    * The bottom option (The Xth day of...) = 0
    * @return - 1 or 0, byte value
    */
   public byte getYearlyBoxRadioIndex()
   {
      return (((Byte)get(YEARLY_BOX_RADIO_INDEX)).byteValue());
   }

   /**
    * Refers to the 'Yearly' option boxes of the MSP Recurring Task infobox.
    * The top option (Date) = 1 .
    * The bottom option (The Xth day of...) = 0
    *
    * @param val - 1 or 0, byte value
    */
   public void setYearlyBoxRadioIndex (Byte val)
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
   public int getDailyBoxComboIndex ()
   {
      return (((Integer)get(DAILY_BOX_COMBO_INDEX)).intValue());
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
   public int getWeeklyBoxComboIndex()
   {
      return (((Integer)get(WEEKLY_BOX_COMBO_INDEX)).intValue());
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
   public int getMonthlyBoxFirstLastComboIndex ()
   {
      return (((Integer)get(MONTHLY_BOX_FIRSTLAST_COMBO_INDEX)).intValue());
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
   public int getMonthlyBoxDayComboIndex ()
   {
      return (((Integer)get(MONTHLY_BOX_DAY_COMBO_INDEX)).intValue());
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
   public int getMonthlyBoxBottomRadioFrequencyComboIndex()
   {
      return (((Integer)get(MONTHLY_BOX_BOTTOM_RADIO_FREQUENCY_COMBO_INDEX)).intValue());
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
   public int getMonthlyBoxDayIndex()
   {
      return (((Integer)get(MONTHLY_BOX_DAY_INDEX)).intValue());
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
   public int getMonthlyBoxTopRadioFrequencyComboIndex ()
   {
      return (((Integer)get(MONTHLY_BOX_TOP_RADIO_FREQUENCY_COMBO_INDEX)).intValue());
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
   public int getYearlyBoxFirstLastComboIndex ()
   {
      return (((Integer)get(FIRSTLAST_COMBO_INDEX_YEARLY_BOX)).intValue());
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
   public int getYearlyBoxDayComboIndex ()
   {
      return (((Integer)get(DAY_COMBO_INDEX_YEARLY_BOX)).intValue());
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
   public int getYearlyBoxMonthComboIndex ()
   {
      return (((Integer)get(MONTH_COMBO_INDEX_YEARLY_BOX)).intValue());
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
    * @return - Byte
    */
   public Byte getNotSureIndex ()
   {
      return ((Byte)get(NOT_SURE_INDEX));
   }

   /**
    * Not sure index?
    *
    * @param val - Byte
    */
   public void setNotSureIndex (Byte val)
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
   public static final Byte PERIOD_DAILY = new Byte((byte)1);

   /**
    * Constant representing frequency - Weekly
    */
   public static final Byte PERIOD_WEEKLY = new Byte((byte)4);

   /**
    * Constant representing frequency - Monthly
    */
   public static final Byte PERIOD_MONTHLY = new Byte((byte)8);

   /**
    * Constant representing frequency - Annually
    */
   public static final Byte PERIOD_YEARLY = new Byte((byte)16);

   /**
    * Constant representing day of week - Sunday
    */
   public static final Byte SUNDAY = new Byte((byte)0);

   /**
    * Constant representing day of week - Monday
    */
   public static final Byte MONDAY = new Byte((byte)1);

   /**
    * Constant representing day of week - Tuesday
    */
   public static final Byte TUESDAY = new Byte((byte)2);

   /**
    * Constant representing day of week - Wednesday
    */
   public static final Byte WEDNESDAY = new Byte((byte)3);

   /**
    * Constant representing day of week - Thursday
    */
   public static final Byte THURSDAY = new Byte((byte)4);

   /**
    * Constant representing day of week - Friday
    */
   public static final Byte FRIDAY = new Byte((byte)5);

   /**
    * Constant representing day of week - Saturday
    */
   public static final Byte SATURDAY = new Byte((byte)6);

   /**
    * Constant representing month of year - January
    */
   public static final Byte JANUARY = new Byte((byte)1);

   /**
    * Constant representing month of year - February
    */
   public static final Byte FEBUARY = new Byte((byte)2);

   /**
    * Constant representing month of year - March
    */
   public static final Byte MARCH = new Byte((byte)3);

   /**
    * Constant representing month of year - April
    */
   public static final Byte APRIL = new Byte((byte)4);

   /**
    * Constant representing month of year - May
    */
   public static final Byte MAY = new Byte((byte)5);

   /**
    * Constant representing month of year - June
    */
   public static final Byte JUNE = new Byte((byte)6);

   /**
    * Constant representing month of year - July
    */
   public static final Byte JULY = new Byte((byte)7);

   /**
    * Constant representing month of year - August
    */
   public static final Byte AUGUST = new Byte((byte)8);

   /**
    * Constant representing month of year - September
    */
   public static final Byte SEPTEMBER = new Byte((byte)9);

   /**
    * Constant representing month of year - Obtober
    */
   public static final Byte OCTOBER = new Byte((byte)10);

   /**
    * Constant representing month of year - November
    */
   public static final Byte NOVEMBER = new Byte((byte)11);

   /**
    * Constant representing month of year - December
    */
   public static final Byte DECEMBER = new Byte((byte)12);

   /**
    * Constant representing frequency of occurances
    */
   public static final Byte INDEX_TYPE_EVERY = new Byte((byte)1);

   /**
    * Constant representing frequency of occurances
    */
   public static final Byte INDEX_EVERY_OTHER = new Byte((byte)2);

   /**
    * Constant representing frequency of occurances
    */
   public static final Byte INDEX_EVERY_3RD = new Byte((byte)3);

   /**
    * Constant representing frequency of occurances
    */
   public static final Byte INDEX_EVERY_4TH = new Byte((byte)4);

   /**
    * Constant representing frequency of occurances
    */
   public static final Byte INDEX_EVERY_5TH = new Byte((byte)5);

   /**
    * Constant representing frequency of occurances
    */
   public static final Byte INDEX_EVERY_6TH = new Byte((byte)6);

   /**
    * Constant representing frequency of occurances
    */
   public static final Byte INDEX_EVERY_7TH = new Byte((byte)7);

   /**
    * Constant representing frequency of occurances
    */
   public static final Byte INDEX_EVERY_8TH = new Byte((byte)8);

   /**
    * Constant representing frequency of occurances
    */
   public static final Byte INDEX_EVERY_9TH = new Byte((byte)9);

   /**
    * Constant representing frequency of occurances
    */
   public static final Byte INDEX_EVERY_10TH = new Byte((byte)10);

   /**
    * Constant representing frequency of occurances
    */
   public static final Byte INDEX_EVERY_11TH = new Byte((byte)11);

   /**
    * Constant representing frequency of occurances
    */
   public static final Byte INDEX_EVERY_12TH = new Byte((byte)12);

   /**
    * Constant typically representing which Xday of the month
    */
   public static final Byte FIRST = new Byte((byte)1);

   /**
    * Constant typically representing which Xday of the month
    */
   public static final Byte SECOND = new Byte((byte)2);

   /**
    * Constant typically representing which Xday of the month
    */
   public static final Byte THIRD = new Byte((byte)3);

   /**
    * Constant typically representing which Xday of the month
    */
   public static final Byte FOURTH = new Byte((byte)4);

   /**
    * Constant typically representing which Xday of the month
    */
   public static final Byte LAST = new Byte((byte)5);

   /**
    * Unique ID. If this <tt>RecurrentTask</tt> is a child of another recurring task,
    * then only this field appears, signifying the parent.
    */
   private static final Integer ID = new Integer(0);

   /**
    * Start date of recurring task
    */
   private static final Integer START_DATE = new Integer(1);

   /**
    * End final date of recurring task
    */
   private static final Integer FINISH_DATE = new Integer(2);

   /**
    * Duration as minutes.
    */
   private static final Integer DURATION = new Integer(3);

   /**
    * Unimportant as Duration appears as minutes.
    */
   private static final Integer DURATION_TYPE = new Integer(4);

   /**
    * No of Occurances scheduled
    */
   private static final Integer NO_OF_OCCURANCES = new Integer(5);

   /**
    * Refers to the 'This Occurs..' box of the MSP Recurring Task infobox.
    * The options are :- 1 - DAILY, 4-WEEKLY , 8-MONTHLY , 16 - YEARLY
    * @see #PERIOD_DAILY frequency constants
    */
   private static final Integer RECURRANCE_TYPE = new Integer(6);

   /**
    * This field seems redundant
    */
   private static final Integer NOT_SURE_INDEX = new Integer(7);

  /**
   * Referring to the 2 radio buttons in the 'Length' option box
   * of the MSP Recurring Task infobox.
   * The top option (To X Date) = 1 .
   * The bottom option (For X Occurances) = 0
   */
   private static final Integer LENGTH_RADIO_INDEX = new Integer(8);

   /**
    * Referring to the 2 radio buttons in the 'Daily' option box
    * of the MSP Recurring Task infobox.
    * The top option (Day) = 0 .
    * The bottom option (Workday) = 1
    */
   private static final Integer DAILY_BOX_RADIO_INDEX = new Integer(9);

   /**
    * Refers to the 'Weekly' option boxes of the MSP Recurring Task infobox.
    * This is an index of days of the week and is represented as a 7
    * digit string of 0/1 values.
    * The string is eg '0010010' if 'Tue' and 'Fri' are selected
    * @see #PERIOD_DAILY frequency constants
    */
   private static final Integer WEEKLY_BOX_DAY_OF_WEEK_INDEX = new Integer(10);

   /**
    * Refers to the 'Monthly' option boxes of the MSP Recurring Task infobox.
    * The top option (Day x of...) = 0 .
    * The bottom option (The Xth day of...) = 1
    */
   private static final Integer MONTHLY_BOX_RADIO_INDEX = new Integer(11);

   /**
    * Refers to the 'Yearly' option boxes of the MSP Recurring Task infobox.
    * The top option (Date) = 1 .
    * The bottom option (The Xth day of...) = 0
    */
   private static final Integer YEARLY_BOX_RADIO_INDEX = new Integer(12);

   /**
    * Refers to the 'Daily' option boxes of the MSP Recurring Task infobox.
    * 'Every...' eg 'Every 7th'
    * @see #INDEX_TYPE_EVERY CONSTANTS eg  'Every 7th','Every Other','Every'
    */
   private static final Integer DAILY_BOX_COMBO_INDEX = new Integer(13);

   /**
    * Refers to the 'Weekly' option boxes of the MSP Recurring Task infobox.
    * 'Every...' eg 'Every 7th'
    * @see #INDEX_TYPE_EVERY CONSTANTS eg  'Every 7th','Every Other','Every'
    */
   private static final Integer WEEKLY_BOX_COMBO_INDEX = new Integer(14);

   /**
    * Refers to the 'The' (eg Second) 'Monthly' option boxes of the
    * MSP Recurring Task infobox.
    * eg first tueday.
    * Values for first,second,third,fourth and last.
    * @see #FIRST CONSTANTS eg 'FIRST'=1,'LAST'=5,'THIRD'=3
    */
   private static final Integer MONTHLY_BOX_FIRSTLAST_COMBO_INDEX = new Integer(15);

   /**
    * Refers to the 'Monthly' option boxes of the MSP Recurring Task infobox.
    * eg Wednesday.
    * Values for day of the week
    * @see #SUNDAY CONSTANTS eg 'MONDAY'=2,'FRIDAY'=5
    */
   private static final Integer MONTHLY_BOX_DAY_COMBO_INDEX = new Integer(16);

   /**
    * Refers to the 'Monthly' option boxes of the MSP Recurring Task infobox.
    * If the bottom radio button is selected (eg. The 3rd Tuesday), this value is the content of the
    * 'Every...' combo box. eg 'Every 5th Month'
    * @see #INDEX_TYPE_EVERY CONSTANTS eg  'Every 7th','Every Other','Every'
    */
   private static final Integer MONTHLY_BOX_BOTTOM_RADIO_FREQUENCY_COMBO_INDEX = new Integer(17);

   /**
    * Refers to the 'Monthly' option boxes of the MSP Recurring Task infobox.
    * This refers to the box Day X, (where X is 1-31) of the month.
    */
   private static final Integer MONTHLY_BOX_DAY_INDEX = new Integer(18);

   /**
    * Refers to the 'Monthly' option boxes of the MSP Recurring Task infobox.
    * If the top radio button is selected (eg. The Xth(day) of..), this value is the content of the
    * 'Every...' combo box. eg 'Every 5th Month'
    * @see #INDEX_TYPE_EVERY CONSTANTS eg  'Every 7th','Every Other','Every'
    */
   private static final Integer MONTHLY_BOX_TOP_RADIO_FREQUENCY_COMBO_INDEX = new Integer(19);

   /**
    * Refers to the 'Yearly' option boxes of the MSP Recurring Task infobox.
    * eg FIRST tueday of December.
    * Values for first,second,third,fourth and last.
    * @see #FIRST CONSTANTS eg 'FIRST'=1,'LAST'=5,'THIRD'=3
    */
   private static final Integer FIRSTLAST_COMBO_INDEX_YEARLY_BOX = new Integer(20);

   /**
    * Refers to the 'Yearly' option boxes of the MSP Recurring Task infobox.
    * eg first TUESDAY of December.
    * Values for day of the week
    * @see #SUNDAY CONSTANTS eg 'MONDAY'=2,'FRIDAY'=5
    */
   private static final Integer DAY_COMBO_INDEX_YEARLY_BOX = new Integer(21);

   /**
    * Refers to the 'Yearly' option boxes of the MSP Recurring Task infobox.
    * eg first tuesday of MARCH.
    * Values for month of the year
    * @see #JANUARY CONSTANTS eg 'JANUARY','MAY'
    */
   private static final Integer MONTH_COMBO_INDEX_YEARLY_BOX = new Integer(22);

   /**
    * Refers to the 'Yearly' option boxes of the MSP Recurring Task infobox.
    * This is the date shown when the top radio button is selected. eg 11 December.
    * This represents the first scheduled occurance of this <tt>RecurringTask</tt>
    */
   private static final Integer YEARLY_BOX_DATE = new Integer(23);

   /**
    * Constant containing the record number associated with this record.
    */
   public static final int RECORD_NUMBER = 72;
}
