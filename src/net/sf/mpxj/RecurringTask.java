/*
 * file:       RecurringTask.java
 * author:     Jon Iles
 *             Scott Melville
 * copyright:  (c) Packwood Software 2002-2008
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
import java.text.DateFormatSymbols;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * This class represents the Recurring Task Record as found in an MPX file.
 */
public final class RecurringTask
{
   /**
    * Gets the start date of this recurring task.
    *
    * @return date to start recurring task
    */
   public Date getStartDate()
   {
      return (m_startDate);
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
      return (m_finishDate);
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
    * Retrieve the duration of the recurring task.
    *
    * @return duration of recurring task
    */
   public Duration getDuration()
   {
      return (m_duration);
   }

   /**
    * Set the duration of the recurring task.
    *
    * @param duration duration of the recurring task
    */
   public void setDuration(Duration duration)
   {
      m_duration = duration;
   }

   /**
    * Sets the number of occurrences.
    *
    * @return number of occurrences
    */
   public Integer getOccurrences()
   {
      return (m_occurrences);
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
      return (m_recurrenceType);
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
      return (m_useEndDate);
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
      return (m_dailyWorkday);
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
    * Retrieves a bit field representing days of the week.
    * MSB=Sunday, LSB=Saturday.
    *
    * @return integer bit field
    */
   public Integer getWeeklyDays()
   {
      return (m_weeklyDays);
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
    * Retrieves the monthly relative flag.
    *
    * @return boolean flag
    */
   public boolean getMonthlyRelative()
   {
      return (m_monthlyRelative);
   }

   /**
    * Sets the monthly relative flag.
    *
    * @param relative boolean flag
    */
   public void setMonthlyRelative(boolean relative)
   {
      m_monthlyRelative = relative;
   }

   /**
    * Retrieve the yearly relative flag.
    *
    * @return boolean flag
    */
   public boolean getYearlyAbsolute()
   {
      return (m_yearlyAbsolute);
   }

   /**
    * Set the yearly relative flag.
    *
    * @param absolute boolean flag
    */
   public void setYearlyAbsolute(boolean absolute)
   {
      m_yearlyAbsolute = absolute;
   }

   /**
    * Retrieves the recurring task frequency.
    *
    * @return recurring task frequency
    */
   public Integer getDailyFrequency()
   {
      return (m_dailyFrequency);
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
      return (m_weeklyFrequency);
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
      return (m_monthlyRelativeOrdinal);
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
      return (m_monthlyRelativeDay);
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
      return (m_monthlyRelativeFrequency);
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
      return (m_monthlyAbsoluteDay);
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
      return (m_monthlyAbsoluteFrequency);
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
      return (m_yearlyRelativeOrdinal);
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
      return (m_yearlyRelativeDay);
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
      return (m_yearlyRelativeMonth);
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
    * Retrieves the yearly absolute date.
    *
    * @return yearly absolute date
    */
   public Date getYearlyAbsoluteDate()
   {
      return (m_yearlyAbsoluteDate);
   }

   /**
    * Sets the yearly absolute date.
    *
    * @param date yearly absolute date
    */
   public void setYearlyAbsoluteDate(Date date)
   {
      m_yearlyAbsoluteDate = date;
   }

   /**
    * {@inheritDoc}
    */
   @Override public String toString()
   {
      DateFormatSymbols dfs = new DateFormatSymbols();
      SimpleDateFormat df = new SimpleDateFormat("d MMM");
      ByteArrayOutputStream os = new ByteArrayOutputStream();
      PrintWriter pw = new PrintWriter(os);
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
               pw.print(m_dailyWorkday ? " Workday" : " Day");
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
               if (m_yearlyAbsolute)
               {
                  pw.print(df.format(m_yearlyAbsoluteDate));
               }
               else
               {
                  pw.print(DAY_ORDINAL[m_yearlyRelativeOrdinal.intValue()]);
                  pw.print(" ");
                  pw.print(dfs.getWeekdays()[m_yearlyRelativeDay.getValue()]);
                  pw.print(" of ");
                  pw.print(dfs.getMonths()[m_yearlyRelativeMonth.intValue() - 1]);
               }
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

   //
   // Common attributes
   //
   private Date m_startDate;
   private Date m_finishDate;
   private Duration m_duration;
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
   private boolean m_monthlyRelative;
   private Integer m_monthlyRelativeOrdinal;
   private Day m_monthlyRelativeDay;
   private Integer m_monthlyRelativeFrequency;
   private Integer m_monthlyAbsoluteDay;
   private Integer m_monthlyAbsoluteFrequency;

   //
   // Yearly recurrence attributes
   //
   private boolean m_yearlyAbsolute;
   private Date m_yearlyAbsoluteDate;
   private Integer m_yearlyRelativeOrdinal;
   private Day m_yearlyRelativeDay;
   private Integer m_yearlyRelativeMonth;
}
