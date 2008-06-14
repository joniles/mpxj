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
    * Sets the number of occurrences.
    *
    * @return number of occurrences
    */
   public Integer getOccurrences ()
   {
      return (m_occurrences);
   }

   /**
    * Retrieves the number of occurrences.
    *
    * @param occurrences number of occurrences
    */
   public void setOccurrences (Integer occurrences)
   {
      m_occurrences = occurrences;
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
    * Retrieves a bit field representing days of the week.
    * MSB=Sunday, LSB=Saturday.
    *
    * @return integer bit field
    */
   public Integer getWeeklyDays ()
   {
      return (m_weeklyDays);
   }

   /**
    * Sets a bit field representing days of the week.
    * MSB=Sunday, LSB=Saturday.
    * 
    * @param days integer bit field
    */
   public void setWeeklyDays (Integer days)
   {
      m_weeklyDays = days;
   }

   /**
    * Retrieves the monthly relative flag.
    * 
    * @return boolean flag
    */
   public boolean getMonthlyRelative ()
   {
      return (m_monthlyRelative);
   }

   /**
    * Sets the monthly relative flag.
    *
    * @param relative boolean flag
    */
   public void setMonthlyRelative (boolean relative)
   {
      m_monthlyRelative = relative;
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
    * Retrieves the recurring task frequency.
    *
    * @return recurring task frequency
    */
   public Integer getWeeklyFrequency ()
   {
      return (m_weeklyFrequency);
   }

   /**
    * Set the recurring task frequency.
    *
    * @param frequency recurring task frequency
    */
   public void setWeeklyFrequency (Integer frequency)
   {
      m_weeklyFrequency = frequency;
   }

   /**
    * Retrieves the monthly relative ordinal value.
    *
    * @return monthly relative ordinal value
    */
   public Integer getMonthlyRelativeOrdinal ()
   {
      return (m_monthlyRelativeOrdinal);
   }

   /**
    * Sets the monthly relative ordinal value.
    *
    * @param ordinal monthly relative ordinal value
    */
   public void setMonthlyRelativeOrdinal (Integer ordinal)
   {
      m_monthlyRelativeOrdinal = ordinal;
   }

   /**
    * Retrieves the monthly relative day.
    *
    * @return monthly relative day
    */
   public Day getMonthlyRelativeDay ()
   {
      return (m_monthlyRelativeDay);
   }

   /**
    * Sets the monthly relative day.
    *
    * @param day monthly relative day
    */
   public void setMonthlyRelativeDay (Day day)
   {
      m_monthlyRelativeDay = day;
   }

   /**
    * Sets the monthly relative frequency.
    * 
    * @return monthly relative frequency
    */
   public Integer getMonthlyRelativeFrequency ()
   {
      return (m_monthlyRelativeFrequency);
   }

   /**
    * Retrieves the monthly relative frequency.
    *
    * @param frequency monthly relative frequency
    */
   public void setMonthlyRelativeFrequency (Integer frequency)
   {
      m_monthlyRelativeFrequency = frequency;
   }

   /**
    * Retrieves the monthly absolute day.
    *
    * @return monthly absolute day.
    */
   public Integer getMonthlyAbsoluteDay ()
   {
      return (m_monthlyAbsoluteDay);
   }

   /**
    * Sets the monthly absolute day.
    *
    * @param day monthly absolute day
    */
   public void setMonthlyAbsoluteDay (Integer day)
   {
      m_monthlyAbsoluteDay = day;
   }

   /**
    * Retrieves the monthly absolute frequency.
    *
    * @return monthly absolute frequency
    */
   public Integer getMonthlyAbsoluteFrequency ()
   {
      return (m_monthlyAbsoluteFrequency);
   }

   /**
    * Sets the monthly absolute frequency.
    *
    * @param frequency monthly absolute frequency
    */
   public void setMonthlyAbsoluteFrequency (Integer frequency)
   {
      m_monthlyAbsoluteFrequency = frequency;
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
               pw.print(" " + ORDINAL[m_weeklyFrequency.intValue()]);
               pw.print(" week on ");
               if ((m_weeklyDays.intValue() & 0x40) != 0)
               {
                  pw.print("Sunday");
               }

               if ((m_weeklyDays.intValue() & 0x20) != 0)
               {
                  pw.print("Monday");
               }

               if ((m_weeklyDays.intValue() & 0x10) != 0)
               {
                  pw.print("Tuesday");
               }
               
               if ((m_weeklyDays.intValue() & 0x08) != 0)
               {
                  pw.print("Wednesday");
               }
               
               if ((m_weeklyDays.intValue() & 0x04) != 0)
               {
                  pw.print("Thursday");
               }
               
               if ((m_weeklyDays.intValue() & 0x02) != 0)
               {
                  pw.print("Friday");
               }

               if ((m_weeklyDays.intValue() & 0x01) != 0)
               {
                  pw.print("Saturday");
               }

               break;
            }
   
            case MONTHLY:
            {
               if (m_monthlyRelative)
               {
                  pw.print(" on The ");
                  pw.print(DAY_ORDINAL[m_monthlyRelativeOrdinal.intValue()]);
                  pw.print(" ");
                  pw.print(DAY[m_monthlyRelativeDay.getValue()]);
                  pw.print(" of ");
                  pw.print(ORDINAL[m_monthlyRelativeFrequency.intValue()]);
               }
               else
               {
                  pw.print(" on Day ");
                  pw.print(m_monthlyAbsoluteDay); 
                  pw.print(" of ");
                  pw.print(ORDINAL[m_monthlyAbsoluteFrequency.intValue()]);
               }
               pw.print(" month");
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

   /**
    * List of ordinal names used to generate debugging output.
    */   
   private static final String[] DAY_ORDINAL =
   {
      null,
      "First",
      "Second",
      "Third",
      "Fourth",
      "Last"     
   };
   
   /**
    * List of day names used to generate debugging output.
    */
   private static final String[] DAY = 
   {
      null,
      "Sunday",
      "Monday",
      "Tuesday",
      "Wednesday",
      "Thursday",
      "Friday",
      "Saturday"
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
   private Integer m_weeklyFrequency;
   private Integer m_weeklyDays;
   
   //
   // Monthly recurrence attributes
   //
   private boolean m_monthlyRelative;
   private Integer m_monthlyRelativeOrdinal;
   private Day m_monthlyRelativeDay;
   private Integer m_monthlyRelativeFrequency;
   private Integer m_monthlyAbsoluteDay;
   private Integer m_monthlyAbsoluteFrequency;
   
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
