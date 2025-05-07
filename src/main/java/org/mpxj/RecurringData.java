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

package org.mpxj;

import java.io.ByteArrayOutputStream;
import java.io.PrintWriter;
import java.text.DateFormatSymbols;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;

import org.mpxj.common.DayOfWeekHelper;
import org.mpxj.common.NumberHelper;

/**
 * This class provides a description of a recurring event.
 */
public class RecurringData
{
   /**
    * Gets the start date of this recurrence.
    *
    * @return recurrence start date
    */
   public LocalDate getStartDate()
   {
      return m_startDate;
   }

   /**
    * Sets the start date of this recurrence.
    *
    * @param val recurrence start date
    */
   public void setStartDate(LocalDate val)
   {
      m_startDate = val;
      clearDatesCache();
   }

   /**
    * Gets the finish date of this recurrence.
    *
    * @return recurrence finish date
    */
   public LocalDate getFinishDate()
   {
      return m_finishDate;
   }

   /**
    * Sets the finish date of this recurrence.
    *
    * @param val recurrence finish date
    */
   public void setFinishDate(LocalDate val)
   {
      m_finishDate = val;
      clearDatesCache();
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
      clearDatesCache();
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
      clearDatesCache();
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
      clearDatesCache();
   }

   /**
    * Returns true if daily recurrence applies to working days only.
    *
    * @return true if daily recurrence applies to working days only
    */
   public boolean getWorkingDaysOnly()
   {
      return m_workingDaysOnly;
   }

   /**
    * Set to true if daily recurrence applies to working days only.
    *
    * @param workingDaysOnly true if daily recurrence applies to working days only
    */
   public void setWorkingDaysOnly(boolean workingDaysOnly)
   {
      m_workingDaysOnly = workingDaysOnly;
      clearDatesCache();
   }

   /**
    * Returns true if this day is part of a weekly recurrence.
    *
    * @param day Day instance
    * @return true if this day is included
    */
   public boolean getWeeklyDay(DayOfWeek day)
   {
      return m_days.contains(day);
   }

   /**
    * Set the state of an individual day in a weekly recurrence.
    *
    * @param day Day instance
    * @param value true if this day is included in the recurrence
    */
   public void setWeeklyDay(DayOfWeek day, boolean value)
   {
      if (value)
      {
         m_days.add(day);
      }
      else
      {
         m_days.remove(day);
      }
      clearDatesCache();
   }

   /**
    * Converts from a bitmap to individual day flags for a weekly recurrence,
    * using the array of masks.
    *
    * @param days bitmap
    * @param masks array of mask values
    */
   public void setWeeklyDaysFromBitmap(Integer days, int[] masks)
   {
      if (days != null)
      {
         int value = days.intValue();
         for (DayOfWeek day : DayOfWeek.values())
         {
            setWeeklyDay(day, ((value & masks[DayOfWeekHelper.getValue(day)]) != 0));
         }
         clearDatesCache();
      }
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
      clearDatesCache();
   }

   /**
    * Retrieves the recurrence frequency.
    *
    * @return recurrence frequency
    */
   public Integer getFrequency()
   {
      return m_frequency;
   }

   /**
    * Set the recurrence frequency.
    *
    * @param frequency recurrence frequency
    */
   public void setFrequency(Integer frequency)
   {
      m_frequency = frequency;
      clearDatesCache();
   }

   /**
    * Retrieves the monthly or yearly relative day of the week.
    *
    * @return day of the week
    */
   public DayOfWeek getDayOfWeek()
   {
      DayOfWeek result = null;
      if (!m_days.isEmpty())
      {
         result = m_days.iterator().next();
      }
      return result;
   }

   /**
    * Sets the monthly or yearly relative day of the week.
    *
    * @param day day of the week
    */
   public void setDayOfWeek(DayOfWeek day)
   {
      m_days.clear();
      m_days.add(day);
      clearDatesCache();
   }

   /**
    * Retrieves the monthly or yearly absolute day number.
    *
    * @return absolute day number.
    */
   public Integer getDayNumber()
   {
      return m_dayNumber;
   }

   /**
    * Sets the monthly or yearly absolute day number.
    *
    * @param day absolute day number
    */
   public void setDayNumber(Integer day)
   {
      m_dayNumber = day;
      clearDatesCache();
   }

   /**
    * Retrieves the yearly month number.
    *
    * @return month number
    */
   public Integer getMonthNumber()
   {
      return m_monthNumber;
   }

   /**
    * Sets the yearly month number.
    *
    * @param month month number
    */
   public void setMonthNumber(Integer month)
   {
      m_monthNumber = month;
      clearDatesCache();
   }

   /**
    * Retrieve the set of start dates represented by this recurrence data.
    *
    * @return array of start dates
    */
   public LocalDate[] getDates()
   {
      populateDates();
      return m_dates;
   }

   /**
    * Returns true if the configuration is valid,
    * i.e. it returns one or more start dates.
    *
    * @return true if the configuration is valid
    */
   public boolean isValid()
   {
      populateDates();
      return m_dates.length > 0;
   }

   /**
    * Retrieve the first calculated date on which an exception
    * will actually occur. The user-supplied start date may
    * not align with the dates generated for the exception.
    *
    * @return first calculated exception date
    */
   public LocalDate getCalculatedFirstDate()
   {
      populateDates();
      return m_dates[0];
   }

   /**
    * Retrieve the last calculated date on which an exception
    * will actually occur.  The user-supplied finish date may
    * not align with the dates generated for the exception.
    *
    * @return last calculated exception date
    */
   public LocalDate getCalculatedLastDate()
   {
      populateDates();
      return m_dates[m_dates.length - 1];
   }

   private void populateDates()
   {
      if (m_dates != null)
      {
         return;
      }

      int frequency = NumberHelper.getInt(m_frequency);
      if (frequency < 1)
      {
         frequency = 1;
      }

      List<LocalDate> dates = new ArrayList<>();

      switch (m_recurrenceType)
      {
         case DAILY:
         {
            getDailyDates(frequency, dates);
            break;
         }

         case WEEKLY:
         {
            getWeeklyDates(frequency, dates);
            break;
         }

         case MONTHLY:
         {
            getMonthlyDates(frequency, dates);
            break;
         }

         case YEARLY:
         {
            getYearlyDates(dates);
            break;
         }
      }

      m_dates = dates.toArray(new LocalDate[0]);
   }

   /**
    * Determines if we need to calculate more dates.
    * If we do not have a finish date, this method falls back on using the
    * occurrences attribute. If we have a finish date, we'll use that instead.
    * We're assuming that the recurring data has one or other of those values.
    *
    * @param date current date
    * @param dates dates generated so far
    * @return true if we should calculate another date
    */
   private boolean moreDates(LocalDate date, List<LocalDate> dates)
   {
      boolean result;
      if (m_finishDate == null)
      {
         int occurrences = NumberHelper.getInt(m_occurrences);
         if (occurrences < 1)
         {
            occurrences = 1;
         }
         result = dates.size() < occurrences;
      }
      else
      {
         result = !date.isAfter(m_finishDate);
      }
      return result;
   }

   /**
    * Calculate start dates for a daily recurrence.
    *
    * @param frequency frequency
    * @param dates array of start dates
    */
   private void getDailyDates(int frequency, List<LocalDate> dates)
   {
      LocalDate date = m_startDate;
      while (moreDates(date, dates))
      {
         dates.add(date);
         date = date.plusDays(frequency);
      }
   }

   /**
    * Calculate start dates for a weekly recurrence.
    *
    * @param frequency frequency
    * @param dates array of start dates
    */
   private void getWeeklyDates(int frequency, List<LocalDate> dates)
   {
      LocalDate date = m_startDate;

      //
      // We need to work from the start of the week that contains the start date
      // and ignore any matches we get that are before the start date.
      //
      DayOfWeek currentDay = date.getDayOfWeek();
      if (currentDay != DayOfWeek.SUNDAY)
      {
         date = date.minusDays(currentDay.getValue());
         currentDay = DayOfWeek.SUNDAY;
      }

      while (moreDates(date, dates))
      {
         int offset = 0;

         for (int dayIndex = 0; dayIndex < 7; dayIndex++)
         {
            if (getWeeklyDay(currentDay))
            {
               if (offset != 0)
               {
                  date = date.plusDays(offset);
                  offset = 0;
               }
               if (!moreDates(date, dates))
               {
                  break;
               }

               if (!date.isBefore(m_startDate))
               {
                  dates.add(date);
               }
            }

            ++offset;
            currentDay = currentDay.plus(1);
         }

         if (frequency > 1)
         {
            offset += (7 * (frequency - 1));
         }
         date = date.plusDays(offset);
      }
   }

   /**
    * Calculate start dates for a monthly recurrence.
    *
    * @param frequency frequency
    * @param dates array of start dates
    */
   private void getMonthlyDates(int frequency, List<LocalDate> dates)
   {
      if (m_relative)
      {
         getMonthlyRelativeDates(frequency, dates);
      }
      else
      {
         getMonthlyAbsoluteDates(frequency, dates);
      }
   }

   /**
    * Calculate start dates for a monthly relative recurrence.
    *
    * @param frequency frequency
    * @param dates array of start dates
    */
   private void getMonthlyRelativeDates(int frequency, List<LocalDate> dates)
   {
      LocalDate date = LocalDate.of(m_startDate.getYear(), m_startDate.getMonth(), 1);
      int dayNumber = NumberHelper.getInt(m_dayNumber);

      while (moreDates(date, dates))
      {
         if (dayNumber > 4)
         {
            date = getLastRelativeDay(date);
         }
         else
         {
            date = getOrdinalRelativeDay(date, dayNumber);
         }

         if (!date.isBefore(m_startDate))
         {
            dates.add(date);
            if (!moreDates(date, dates))
            {
               break;
            }
         }

         date = LocalDate.of(date.getYear(), date.getMonth(), 1);
         date = date.plusMonths(frequency);
      }
   }

   /**
    * Calculate start dates for a monthly absolute recurrence.
    *
    * @param frequency frequency
    * @param dates array of start dates
    */
   private void getMonthlyAbsoluteDates(int frequency, List<LocalDate> dates)
   {
      LocalDate date = m_startDate;
      int currentDayNumber = date.getDayOfMonth();
      date = LocalDate.of(date.getYear(), date.getMonth(), 1);
      int requiredDayNumber = NumberHelper.getInt(m_dayNumber);
      if (requiredDayNumber < currentDayNumber)
      {
         date = date.plusMonths(1);
      }

      while (moreDates(date, dates))
      {
         int useDayNumber = requiredDayNumber;
         YearMonth month = YearMonth.of(date.getYear(), date.getMonth());
         int maxDayNumber = month.lengthOfMonth();
         if (useDayNumber > maxDayNumber)
         {
            useDayNumber = maxDayNumber;
         }

         date = LocalDate.of(date.getYear(), date.getMonth(), useDayNumber);
         dates.add(date);

         date = LocalDate.of(date.getYear(), date.getMonth(), 1);
         date = date.plusMonths(frequency);
      }
   }

   /**
    * Calculate start dates for a yearly recurrence.
    *
    * @param dates array of start dates
    */
   private void getYearlyDates(List<LocalDate> dates)
   {
      if (m_relative)
      {
         getYearlyRelativeDates(dates);
      }
      else
      {
         getYearlyAbsoluteDates(dates);
      }
   }

   /**
    * Calculate start dates for a yearly relative recurrence.
    *
    * @param dates array of start dates
    */
   private void getYearlyRelativeDates(List<LocalDate> dates)
   {
      LocalDate date = LocalDate.of(m_startDate.getYear(), NumberHelper.getInt(m_monthNumber), 1);

      int dayNumber = NumberHelper.getInt(m_dayNumber);
      while (moreDates(date, dates))
      {
         if (dayNumber > 4)
         {
            date = getLastRelativeDay(date);
         }
         else
         {
            date = getOrdinalRelativeDay(date, dayNumber);
         }

         if (!date.isBefore(m_startDate))
         {
            dates.add(date);
            if (!moreDates(date, dates))
            {
               break;
            }
         }

         date = LocalDate.of(date.getYear() + 1, date.getMonth(), 1);
      }
   }

   /**
    * Calculate start dates for a yearly absolute recurrence.
    *
    * @param dates array of start dates
    */
   private void getYearlyAbsoluteDates(List<LocalDate> dates)
   {
      LocalDate date = LocalDate.of(m_startDate.getYear(), NumberHelper.getInt(m_monthNumber), 1);
      int requiredDayNumber = NumberHelper.getInt(m_dayNumber);

      while (moreDates(date, dates))
      {
         int useDayNumber = requiredDayNumber;
         YearMonth month = YearMonth.of(date.getYear(), date.getMonth());
         int maxDayNumber = month.lengthOfMonth();
         if (useDayNumber > maxDayNumber)
         {
            useDayNumber = maxDayNumber;
         }

         date = LocalDate.of(date.getYear(), date.getMonth(), useDayNumber);
         if (date.isBefore(m_startDate))
         {
            date = date.plusYears(1);
         }

         dates.add(date);
         date = LocalDate.of(date.getYear(), date.getMonth(), 1);
         date = date.plusYears(1);
      }
   }

   /**
    * Moves a calendar to the nth named day of the month.
    *
    * @param date current date
    * @param dayNumber nth day
    * @return ordinal relative day
    */
   private LocalDate getOrdinalRelativeDay(LocalDate date, int dayNumber)
   {
      int currentDayOfWeek = DayOfWeekHelper.getValue(date.getDayOfWeek());
      int requiredDayOfWeek = DayOfWeekHelper.getValue(getDayOfWeek());
      int dayOfWeekOffset = 0;
      if (requiredDayOfWeek > currentDayOfWeek)
      {
         dayOfWeekOffset = requiredDayOfWeek - currentDayOfWeek;
      }
      else
      {
         if (requiredDayOfWeek < currentDayOfWeek)
         {
            dayOfWeekOffset = 7 - (currentDayOfWeek - requiredDayOfWeek);
         }
      }

      if (dayOfWeekOffset != 0)
      {
         date = date.plusDays(dayOfWeekOffset);
      }

      if (dayNumber > 1)
      {
         date = date.plusDays((7 * ((long) dayNumber - 1)));
      }

      return date;
   }

   /**
    * Moves a calendar to the last named day of the month.
    *
    * @param date current date
    * @return last relative day
    */
   private LocalDate getLastRelativeDay(LocalDate date)
   {
      YearMonth month = YearMonth.of(date.getYear(), date.getMonth());
      date = LocalDate.of(date.getYear(), date.getMonth(), month.lengthOfMonth());

      int currentDayOfWeek = DayOfWeekHelper.getValue(date.getDayOfWeek());
      int requiredDayOfWeek = DayOfWeekHelper.getValue(getDayOfWeek());
      int dayOfWeekOffset = 0;

      if (currentDayOfWeek > requiredDayOfWeek)
      {
         dayOfWeekOffset = requiredDayOfWeek - currentDayOfWeek;
      }
      else
      {
         if (currentDayOfWeek < requiredDayOfWeek)
         {
            dayOfWeekOffset = -7 + (requiredDayOfWeek - currentDayOfWeek);
         }
      }

      if (dayOfWeekOffset != 0)
      {
         date = date.plusDays(dayOfWeekOffset);
      }

      return date;
   }

   /**
    * Sets the yearly absolute date.
    *
    * @param date yearly absolute date
    */
   public void setYearlyAbsoluteFromDate(LocalDate date)
   {
      if (date != null)
      {
         m_dayNumber = Integer.valueOf(date.getDayOfMonth());
         m_monthNumber = Integer.valueOf(date.getMonthValue());
         clearDatesCache();
      }
   }

   /**
    * Retrieve the ordinal text for a given integer.
    *
    * @param value integer value
    * @return ordinal text
    */
   private String getOrdinal(Integer value)
   {
      String result;
      int index = value.intValue();
      if (index >= ORDINAL.length)
      {
         result = "every " + index + "th";
      }
      else
      {
         result = ORDINAL[index];
      }
      return result;
   }

   private void clearDatesCache()
   {
      m_dates = null;
   }

   @Override public String toString()
   {
      DateFormatSymbols dfs = new DateFormatSymbols();
      ByteArrayOutputStream os = new ByteArrayOutputStream();
      PrintWriter pw = new PrintWriter(os);
      pw.print("[RecurringData ");
      pw.print(m_recurrenceType);

      switch (m_recurrenceType)
      {
         case DAILY:
         {
            pw.print(" " + getOrdinal(m_frequency));
            pw.print(m_workingDaysOnly ? " Working day" : " Day");
            break;
         }

         case WEEKLY:
         {
            pw.print(" " + getOrdinal(m_frequency));
            pw.print(" week on ");

            StringBuilder sb = new StringBuilder();
            for (DayOfWeek day : DayOfWeek.values())
            {
               if (getWeeklyDay(day))
               {
                  if (sb.length() != 0)
                  {
                     sb.append(", ");
                  }
                  sb.append(dfs.getWeekdays()[DayOfWeekHelper.getValue(day)]);
               }
            }
            pw.print(sb);
            break;
         }

         case MONTHLY:
         {
            if (m_relative)
            {
               pw.print(" on The ");
               pw.print(DAY_ORDINAL[m_dayNumber.intValue()]);
               pw.print(" ");
               pw.print(dfs.getWeekdays()[DayOfWeekHelper.getValue(getDayOfWeek())]);
               pw.print(" of ");
               pw.print(getOrdinal(m_frequency));
            }
            else
            {
               pw.print(" on Day ");
               pw.print(m_dayNumber);
               pw.print(" of ");
               pw.print(getOrdinal(m_frequency));
            }
            pw.print(" month");
            break;
         }

         case YEARLY:
         {
            pw.print(" on the ");
            if (m_relative)
            {
               pw.print(DAY_ORDINAL[m_dayNumber.intValue()]);
               pw.print(" ");
               pw.print(dfs.getWeekdays()[DayOfWeekHelper.getValue(getDayOfWeek())]);
               pw.print(" of ");
               pw.print(dfs.getMonths()[m_monthNumber.intValue() - 1]);
            }
            else
            {
               pw.print(m_dayNumber + " " + dfs.getMonths()[m_monthNumber.intValue() - 1]);
            }
            break;
         }
      }

      if (m_startDate != null)
      {
         pw.print(" From " + m_startDate);
      }

      if (m_occurrences != null)
      {
         pw.print(" For " + m_occurrences + " occurrences");
      }

      if (m_finishDate != null)
      {
         pw.print(" To " + m_finishDate);
      }

      pw.print("]");
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
      "every 3rd"
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
   private LocalDate m_startDate;
   private LocalDate m_finishDate;
   private Integer m_occurrences;
   private RecurrenceType m_recurrenceType;
   private boolean m_relative;
   private boolean m_workingDaysOnly;
   private boolean m_useEndDate;
   private Integer m_frequency;
   private Integer m_dayNumber;
   private Integer m_monthNumber;
   private LocalDate[] m_dates;
   private final EnumSet<DayOfWeek> m_days = EnumSet.noneOf(DayOfWeek.class);
}
