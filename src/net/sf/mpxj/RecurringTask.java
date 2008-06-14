/*
 * file:       RecurringTask.java
 * author:     Jon Iles             
 *             Scott Melville
 * copyright:  (c) Packwood Software Limited 2002-2008
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

import java.io.ByteArrayOutputStream;
import java.io.PrintWriter;
import java.util.Date;

/**
 * This class represents the Recurring Task Record as found in an MPX file.
 */
public final class RecurringTask
{
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
      m_startDate = val;
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
      m_finishDate = val;
   }

   /**
    * Retrieve the duration of the recurring task.
    *
    * @return duration of recurring task
    */
   public Duration getDuration ()
   {
      return (m_duration);
   }

   /**
    * Set the duration of the recurring task.
    *
    * @param duration duration of the recurring task
    */
   public void setDuration (Duration duration)
   {
      m_duration = duration;
   }

   /**
    * Sets the number of occurance of the task.
    *
    * @return number of occurances
    */
   public Integer getOccurrences ()
   {
      return (m_occurrences);
   }

   /**
    * Gets the number of occurance of the task.
    *
    * @param val number of occurances
    */
   public void setOccurrences (Integer val)
   {
      m_occurrences = val;
   }

   /**
    * Retrieves the recurrence type.
    *
    * @return RecurrenceType instance
    */
   public RecurrenceType getRecurrenceType ()
   {
      return (m_recurrenceType);
   }

   /**
    * Sets the recurrence type.
    * 
    * @param type recurrence type
    */
   public void setRecurrenceType (RecurrenceType type)
   {
      m_recurrenceType = type;
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
    * Retrieve the daily workday flag.
    *
    * @return boolean flag
    */
   public boolean getDailyWorkday ()
   {
      return (m_dailyWorkday);
   }

   /**
    * Set the daily workday flag.
    *
    * @param workday workday flag
    */
   public void setDailyWorkday (boolean workday)
   {
      m_dailyWorkday = workday;
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
    * Retrieves the recurring task frequency.
    *
    * @return recurring task frequency
    */
   public Integer getDailyFrequency ()
   {
      return (m_dailyFrequency);
   }

   /**
    * Set the recurring task frequency.
    *
    * @param frequency recurring task frequency
    */
   public void setDailyFrequency (Integer frequency)
   {
      m_dailyFrequency = frequency;
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
      m_yearlyBoxDate = val;
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
    * {@inheritDoc}
    */
   @Override
   public String toString()
   {
      ByteArrayOutputStream os = new ByteArrayOutputStream();
      PrintWriter pw = new PrintWriter (os);
      pw.print("[RecurringTask");
      
      if (m_duration != null)
      {
         pw.print(" Duration " + m_duration);
         pw.print(" This Occurs " + m_recurrenceType); 
         
         switch (m_recurrenceType)
         {
            case DAILY:
            {
               pw.print(" " + ORDINAL[m_dailyFrequency.intValue()]);
               pw.print(m_dailyWorkday?" Workday":" Day");
               break;
            }
   
            case WEEKLY:
            {
             
               break;
            }
   
            case MONTHLY:
            {
             
               break;
            }
   
            case YEARLY:
            {
             
               break;
            }
         }
         
         pw.print(" From " + m_startDate);
         pw.print(" For " + m_occurrences + " occurrences");
         pw.print(" To " + m_finishDate);
      }
      pw.println("]");
      pw.flush();
      return (os.toString());           
   }
   
   /**
    * List of ordinal names used to generate debugging output.
    */
   private static final String[] ORDINAL =
   {
      null,
      "every",
      "every other",
      "every 3rd",
      "every 4th",
      "every 5th",
      "every 6th",
      "every 7th",
      "every 8th",
      "every 9th",
      "every 10th",
      "every 11th",
      "every 12th"
   };
   
   //
   // Common attributes
   //
   private Date m_startDate;
   private Date m_finishDate;
   private Duration m_duration;
   private Integer m_occurrences;
   private RecurrenceType m_recurrenceType;

   //
   // Daily recurrence attributes
   //
   private Integer m_dailyFrequency;
   private boolean m_dailyWorkday;
   
   //
   // Weekly recurrence attributes
   //
   private Integer m_weeklyBoxComboIndex;
   private String m_weeklyBoxDayOfWeekIndex;
   
   //
   // Monthly recurrence attributes
   //
   private Integer m_monthlyBoxRadioIndex;
   private Integer m_monthlyBoxFirstLastComboIndex;
   private Integer m_monthlyBoxDayComboIndex;
   private Integer m_monthlyBoxBottomRadioFrequencyComboIndex;
   private Integer m_monthlyBoxDayIndex;
   private Integer m_monthlyBoxTopRadioFrequencyComboIndex;
   
   //
   // Yearly recurrence attributes
   //   
   private Integer m_yearlyBoxRadioIndex;
   private Integer m_yearlyBoxFirstLastComboIndex;
   private Integer m_yearlyBoxDayComboIndex;
   private Integer m_yearlyBoxMonthComboIndex;
   private Date m_yearlyBoxDate;
   
   private Integer m_lengthRadioIndex;   
   private Integer m_notSureIndex;
   private Integer m_taskUniqueID; // not sure what this is - NOT task unique ID though - always 1?   
}
