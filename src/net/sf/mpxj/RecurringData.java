/*
 * file:       RecurringData.java
 * author:     Jon Iles
 * copyright:  (c) Packwood Software 2017
 * date:       20/10/2017
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
import java.text.DateFormatSymbols;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * This class provides a description of a recurring event.
 */
public class RecurringData
{
   /**
    * Gets the start date of this recurring task.
    *
    * @return date to start recurring task
    */
   public Date getStartDate()
   {
      return m_startDate;
   }

   /**
    * Sets the start date of this recurring task.
    *
    * @param val date to start recurring task
    */
   public void setStartDate(Date val)
   {
      m_startDate = val;
   }

   /**
    * Gets the finish date of this recurring task.
    *
    * @return date to finish recurring task
    */
   public Date getFinishDate()
   {
      return m_finishDate;
   }

   /**
    * Sets the finish date of this recurring task.
    *
    * @param val date to finish recurring task
    */
   public void setFinishDate(Date val)
   {
      m_finishDate = val;
   }

   /**
    * Sets the number of occurrences.
    *
    * @return number of occurrences
    */
   public Integer getOccurrences()
   {
      return m_occurrences;
   }

   /**
    * Retrieves the number of occurrences.
    *
    * @param occurrences number of occurrences
    */
   public void setOccurrences(Integer occurrences)
   {
      m_occurrences = occurrences;
   }

   /**
    * Retrieves the recurrence type.
    *
    * @return RecurrenceType instance
    */
   public RecurrenceType getRecurrenceType()
   {
      return m_recurrenceType;
   }

   /**
    * Sets the recurrence type.
    *
    * @param type recurrence type
    */
   public void setRecurrenceType(RecurrenceType type)
   {
      m_recurrenceType = type;
   }

   /**
    * Retrieves the use end date flag.
    *
    * @return use end date flag
    */
   public boolean getUseEndDate()
   {
      return m_useEndDate;
   }

   /**
    * Sets the use end date flag.
    *
    * @param useEndDate use end date flag
    */
   public void setUseEndDate(boolean useEndDate)
   {
      m_useEndDate = useEndDate;
   }

   /**
    * Retrieve the daily workday flag.
    *
    * @return boolean flag
    */
   public boolean getDailyWorkday()
   {
      return m_dailyWorkday;
   }

   /**
    * Set the daily workday flag.
    *
    * @param workday workday flag
    */
   public void setDailyWorkday(boolean workday)
   {
      m_dailyWorkday = workday;
   }

   /**
    * Returns true if this day is part of a weekly recurrence.
    *
    * @param day Day instance
    * @return true if this day is included
    */
   public boolean getWeeklyDay(Day day)
   {
      return (m_weeklyDays.intValue() & DAY_MASKS[day.getValue()]) != 0;
   }

   /**
    * Retrieves a bit field representing days of the week.
    * MSB=Sunday, LSB=Saturday.
    *
    * @return integer bit field
    */
   public Integer getWeeklyDays()
   {
      return m_weeklyDays;
   }

   /**
    * Sets a bit field representing days of the week.
    * MSB=Sunday, LSB=Saturday.
    *
    * @param days integer bit field
    */
   public void setWeeklyDays(Integer days)
   {
      m_weeklyDays = days;
   }

   /**
    * Retrieves the relative flag. This is only relevant for monthly and yearly recurrence.
    *
    * @return boolean flag
    */
   public boolean getRelative()
   {
      return m_relative;
   }

   /**
    * Sets the relative flag. This is only relevant for monthly and yearly recurrence.
    *
    * @param relative boolean flag
    */
   public void setRelative(boolean relative)
   {
      m_relative = relative;
   }

   /**
    * Retrieves the recurring task frequency.
    *
    * @return recurring task frequency
    */
   public Integer getDailyFrequency()
   {
      return m_dailyFrequency;
   }

   /**
    * Set the recurring task frequency.
    *
    * @param frequency recurring task frequency
    */
   public void setDailyFrequency(Integer frequency)
   {
      m_dailyFrequency = frequency;
   }

   /**
    * Retrieves the recurring task frequency.
    *
    * @return recurring task frequency
    */
   public Integer getWeeklyFrequency()
   {
      return m_weeklyFrequency;
   }

   /**
    * Set the recurring task frequency.
    *
    * @param frequency recurring task frequency
    */
   public void setWeeklyFrequency(Integer frequency)
   {
      m_weeklyFrequency = frequency;
   }

   /**
    * Retrieves the monthly relative ordinal value.
    *
    * @return monthly relative ordinal value
    */
   public Integer getMonthlyRelativeOrdinal()
   {
      return m_monthlyRelativeOrdinal;
   }

   /**
    * Sets the monthly relative ordinal value.
    *
    * @param ordinal monthly relative ordinal value
    */
   public void setMonthlyRelativeOrdinal(Integer ordinal)
   {
      m_monthlyRelativeOrdinal = ordinal;
   }

   /**
    * Retrieves the monthly relative day.
    *
    * @return monthly relative day
    */
   public Day getMonthlyRelativeDay()
   {
      return m_monthlyRelativeDay;
   }

   /**
    * Sets the monthly relative day.
    *
    * @param day monthly relative day
    */
   public void setMonthlyRelativeDay(Day day)
   {
      m_monthlyRelativeDay = day;
   }

   /**
    * Sets the monthly relative frequency.
    *
    * @return monthly relative frequency
    */
   public Integer getMonthlyRelativeFrequency()
   {
      return m_monthlyRelativeFrequency;
   }

   /**
    * Retrieves the monthly relative frequency.
    *
    * @param frequency monthly relative frequency
    */
   public void setMonthlyRelativeFrequency(Integer frequency)
   {
      m_monthlyRelativeFrequency = frequency;
   }

   /**
    * Retrieves the monthly absolute day.
    *
    * @return monthly absolute day.
    */
   public Integer getMonthlyAbsoluteDay()
   {
      return m_monthlyAbsoluteDay;
   }

   /**
    * Sets the monthly absolute day.
    *
    * @param day monthly absolute day
    */
   public void setMonthlyAbsoluteDay(Integer day)
   {
      m_monthlyAbsoluteDay = day;
   }

   /**
    * Retrieves the monthly absolute frequency.
    *
    * @return monthly absolute frequency
    */
   public Integer getMonthlyAbsoluteFrequency()
   {
      return m_monthlyAbsoluteFrequency;
   }

   /**
    * Sets the monthly absolute frequency.
    *
    * @param frequency monthly absolute frequency
    */
   public void setMonthlyAbsoluteFrequency(Integer frequency)
   {
      m_monthlyAbsoluteFrequency = frequency;
   }

   /**
    * Retrieves the yearly relative ordinal.
    *
    * @return yearly relative ordinal
    */
   public Integer getYearlyRelativeOrdinal()
   {
      return m_yearlyRelativeOrdinal;
   }

   /**
    * Sets the yearly relative ordinal.
    *
    * @param ordinal yearly relative ordinal
    */
   public void setYearlyRelativeOrdinal(Integer ordinal)
   {
      m_yearlyRelativeOrdinal = ordinal;
   }

   /**
    * Retrieve the yearly relative day.
    *
    * @return yearly relative day
    */
   public Day getYearlyRelativeDay()
   {
      return m_yearlyRelativeDay;
   }

   /**
    * Sets the yearly relative day.
    *
    * @param day yearly relative day
    */
   public void setYearlyRelativeDay(Day day)
   {
      m_yearlyRelativeDay = day;
   }

   /**
    * Retrieves the yearly relative month.
    *
    * @return yearly relative month
    */
   public Integer getYearlyRelativeMonth()
   {
      return m_yearlyRelativeMonth;
   }

   /**
    * Sets the yearly relative month.
    *
    * @param month yearly relative month
    */
   public void setYearlyRelativeMonth(Integer month)
   {
      m_yearlyRelativeMonth = month;
   }

   /**
    * Retrieve the yearly absolute day.
    *
    * @return yearly absolute day
    */
   public Integer getYearlyAbsoluteDay()
   {
      return m_yearlyAbsoluteDay;
   }

   /**
    * Set the yearly absolute day.
    *
    * @param yearlyAbsoluteDay day of month
    */
   public void setYearlyAbsoluteDay(Integer yearlyAbsoluteDay)
   {
      m_yearlyAbsoluteDay = yearlyAbsoluteDay;
   }

   /**
    * Retrieve the yearly absolute month.
    *
    * @return month number
    */
   public Integer getYearlyAbsoluteMonth()
   {
      return m_yearlyAbsoluteMonth;
   }

   /**
    * Set the yearly absolute month.
    *
    * @param yearlyAbsoluteMonth month number
    */
   public void setYearlyAbsoluteMonth(Integer yearlyAbsoluteMonth)
   {
      m_yearlyAbsoluteMonth = yearlyAbsoluteMonth;
   }

   /**
    * Retrieves the yearly absolute date.
    *
    * @return yearly absolute date
    */
   public Date getYearlyAbsoluteAsDate()
   {
      Date result;
      if (m_yearlyAbsoluteDay == null || m_yearlyAbsoluteMonth == null)
      {
         result = null;
      }
      else
      {
         Calendar cal = Calendar.getInstance();
         cal.setTime(m_startDate);
         cal.set(Calendar.MONTH, m_yearlyAbsoluteMonth.intValue() - 1);
         cal.set(Calendar.DAY_OF_MONTH, m_yearlyAbsoluteDay.intValue());
         result = cal.getTime();
      }
      return result;
   }

   /**
    * Sets the yearly absolute date.
    *
    * @param date yearly absolute date
    */
   public void setYearlyAbsoluteFromDate(Date date)
   {
      if (date != null)
      {
         Calendar cal = Calendar.getInstance();
         cal.setTime(date);
         m_yearlyAbsoluteDay = Integer.valueOf(cal.get(Calendar.DAY_OF_MONTH));
         m_yearlyAbsoluteMonth = Integer.valueOf(cal.get(Calendar.MONTH) + 1);
      }
   }

   /**
    * {@inheritDoc}
    */
   @Override public String toString()
   {
      DateFormatSymbols dfs = new DateFormatSymbols();
      ByteArrayOutputStream os = new ByteArrayOutputStream();
      PrintWriter pw = new PrintWriter(os);
      pw.print("[RecurringData");
      pw.print(m_recurrenceType);

      switch (m_recurrenceType)
      {
         case DAILY:
         {
            pw.print(" " + ORDINAL[m_dailyFrequency.intValue()]);
            pw.print(m_dailyWorkday ? " Workday" : " Day");
            break;
         }

         case WEEKLY:
         {
            pw.print(" " + ORDINAL[m_weeklyFrequency.intValue()]);
            pw.print(" week on ");

            StringBuilder sb = new StringBuilder();
            for (Day day : Day.values())
            {
               if (getWeeklyDay(day))
               {
                  if (sb.length() != 0)
                  {
                     sb.append(", ");
                  }
                  sb.append(dfs.getWeekdays()[day.getValue()]);
               }
            }
            pw.print(sb.toString());
            break;
         }

         case MONTHLY:
         {
            if (m_relative)
            {
               pw.print(" on The ");
               pw.print(DAY_ORDINAL[m_monthlyRelativeOrdinal.intValue()]);
               pw.print(" ");
               pw.print(dfs.getWeekdays()[m_monthlyRelativeDay.getValue()]);
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
            pw.print(" on the ");
            if (m_relative)
            {
               pw.print(DAY_ORDINAL[m_yearlyRelativeOrdinal.intValue()]);
               pw.print(" ");
               pw.print(dfs.getWeekdays()[m_yearlyRelativeDay.getValue()]);
               pw.print(" of ");
               pw.print(dfs.getMonths()[m_yearlyRelativeMonth.intValue() - 1]);
            }
            else
            {
               SimpleDateFormat df = new SimpleDateFormat("d MMM");
               pw.print(df.format(getYearlyAbsoluteAsDate()));
            }
            break;
         }
      }

      pw.print(" From " + m_startDate);
      pw.print(" For " + m_occurrences + " occurrences");
      pw.print(" To " + m_finishDate);

      pw.println("]");
      pw.flush();
      return os.toString();
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

   private static final int[] DAY_MASKS =
   {
      0x00,
      0x40, // Sunday
      0x20, // Monday
      0x10, // Tuesday
      0x08, // Wednesday
      0x04, // Thursday
      0x02, // Friday
      0x01, // Saturday
   };

   //
   // Common attributes
   //
   private Date m_startDate;
   private Date m_finishDate;
   private Integer m_occurrences;
   private RecurrenceType m_recurrenceType;
   private boolean m_useEndDate;

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
   private Integer m_monthlyRelativeOrdinal;
   private Day m_monthlyRelativeDay;
   private Integer m_monthlyRelativeFrequency;
   private Integer m_monthlyAbsoluteDay;
   private Integer m_monthlyAbsoluteFrequency;

   //
   // Yearly recurrence attributes
   //
   private Integer m_yearlyAbsoluteDay;
   private Integer m_yearlyAbsoluteMonth;
   private Integer m_yearlyRelativeOrdinal;
   private Day m_yearlyRelativeDay;
   private Integer m_yearlyRelativeMonth;

   //
   // Absolute/relative flag.
   // Only relevant to monthly and yearly.
   //
   private boolean m_relative;

}
