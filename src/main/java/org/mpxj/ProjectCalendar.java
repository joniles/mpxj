/*
 * file:       ProjectCalendar.java
 * author:     Jon Iles
 * copyright:  (c) Packwood Software 2002-2003
 * date:       28/11/2003
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
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.Year;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.UUID;
import java.util.WeakHashMap;
import java.util.stream.Collectors;

import org.mpxj.common.LocalDateHelper;
import org.mpxj.common.LocalDateTimeHelper;
import org.mpxj.common.LocalTimeHelper;
import org.mpxj.common.NumberHelper;
import org.mpxj.common.ProjectCalendarHelper;

/**
 * This class represents a Calendar Definition record. Both base calendars
 * and calendars derived from base calendars are represented by instances
 * of this class. The class is used to define the working and non-working days
 * of the week. The default calendar defines Monday to Friday as working days.
 */
public class ProjectCalendar extends ProjectCalendarDays implements ProjectEntityWithMutableUniqueID, TimeUnitDefaultsContainer
{
   /**
    * Default constructor.
    *
    * @param file the parent file to which this record belongs.
    */
   public ProjectCalendar(ProjectFile file)
   {
      this(file, false);
   }

   /**
    * Internal constructor to allow the temporary calendar flag to be set.
    *
    * @param file the parent file to which this record belongs.
    * @param temporaryCalendar true if this is a temporary calendar
    */
   protected ProjectCalendar(ProjectFile file, boolean temporaryCalendar)
   {
      m_projectFile = file;
      m_temporaryCalendar = temporaryCalendar;

      if (!temporaryCalendar && file.getProjectConfig().getAutoCalendarUniqueID())
      {
         setUniqueID(file.getUniqueIdObjectSequence(ProjectCalendar.class).getNext());
      }
   }

   /**
    * Retrieve the effective number of minutes per day for this calendar.
    * Will fall back to the parent calendar and project settings if
    * this calendar does not have an explicit value.
    *
    * @return minutes per day
    */
   @Override public Integer getMinutesPerDay()
   {
      Integer result = m_calendarMinutesPerDay;
      if (result == null)
      {
         if (m_parent == null)
         {
            result = getParentFile().getProjectProperties().getMinutesPerDay();
         }
         else
         {
            result = m_parent.getMinutesPerDay();
         }
      }

      return result;
   }

   /**
    * Retrieve the effective number of minutes per week for this calendar.
    * Will fall back to the parent calendar and project settings if
    * this calendar does not have an explicit value.
    *
    * @return minutes per week
    */
   @Override public Integer getMinutesPerWeek()
   {
      Integer result = m_calendarMinutesPerWeek;
      if (result == null)
      {
         if (m_parent == null)
         {
            result = getParentFile().getProjectProperties().getMinutesPerWeek();
         }
         else
         {
            result = m_parent.getMinutesPerWeek();
         }
      }

      return result;
   }

   /**
    * Retrieve the effective number of minutes per month for this calendar.
    * Will fall back to the parent calendar and project settings if
    * this calendar does not have an explicit value.
    *
    * @return minutes per month
    */
   @Override public Integer getMinutesPerMonth()
   {
      Integer result = m_calendarMinutesPerMonth;
      if (result == null)
      {
         if (m_parent == null)
         {
            result = getParentFile().getProjectProperties().getMinutesPerMonth();
         }
         else
         {
            result = m_parent.getMinutesPerMonth();
         }
      }

      return result;
   }

   /**
    * Retrieve the effective number of minutes per year for this calendar.
    * Will fall back to the parent calendar and project settings if
    * this calendar does not have an explicit value.
    *
    * @return minutes per year
    */
   @Override public Integer getMinutesPerYear()
   {
      Integer result = m_calendarMinutesPerYear;
      if (result == null)
      {
         if (m_parent == null)
         {
            result = getParentFile().getProjectProperties().getMinutesPerYear();
         }
         else
         {
            result = m_parent.getMinutesPerYear();
         }
      }

      return result;
   }

   /**
    * Retrieve the effective number of days per month for this calendar.
    *
    * @return days per month
    */
   @Override public Integer getDaysPerMonth()
   {
      // We actually don't store this as part of calendar presently,
      // so we'll use the value from the project properties.
      return getParentFile().getProjectProperties().getDaysPerMonth();
   }

   /**
    * Set the number of minutes per day for this calendar.
    *
    * @param minutes number of minutes
    */
   public void setCalendarMinutesPerDay(Integer minutes)
   {
      m_calendarMinutesPerDay = minutes;
   }

   /**
    * Retrieve the number of minutes per day for this calendar.
    *
    * @return minutes per day
    */
   public Integer getCalendarMinutesPerDay()
   {
      return m_calendarMinutesPerDay;
   }

   /**
    * Set the number of minutes per week for this calendar.
    *
    * @param minutes number of minutes
    */
   public void setCalendarMinutesPerWeek(Integer minutes)
   {
      m_calendarMinutesPerWeek = minutes;
   }

   /**
    * Retrieve the number of minutes per week for this calendar.
    *
    * @return minutes per week
    */
   public Integer getCalendarMinutesPerWeek()
   {
      return m_calendarMinutesPerWeek;
   }

   /**
    * Set the number of minutes per month for this calendar.
    *
    * @param minutes number of minutes
    */
   public void setCalendarMinutesPerMonth(Integer minutes)
   {
      m_calendarMinutesPerMonth = minutes;
   }

   /**
    * Retrieve the number of minutes per month for this calendar.
    *
    * @return minutes per month
    */
   public Integer getCalendarMinutesPerMonth()
   {
      return m_calendarMinutesPerMonth;
   }

   /**
    * Set the number of minutes per year for this calendar.
    *
    * @param minutes number of minutes
    */
   public void setCalendarMinutesPerYear(Integer minutes)
   {
      m_calendarMinutesPerYear = minutes;
   }

   /**
    * Retrieve the number of minutes per year for this calendar.
    *
    * @return minutes per year
    */
   public Integer getCalendarMinutesPerYear()
   {
      return m_calendarMinutesPerYear;
   }

   /**
    * Add an empty work week.
    *
    * @return new work week
    */
   public ProjectCalendarWeek addWorkWeek()
   {
      ProjectCalendarWeek week = new ProjectCalendarWeek();
      m_workWeeks.add(week);
      m_weeksSorted = false;
      clearWorkingDateCache();
      return week;
   }

   /**
    * Remove a work week from the calendar.
    *
    * @param week week to remove
    */
   public void removeWorkWeek(ProjectCalendarWeek week)
   {
      m_workWeeks.remove(week);
      clearWorkingDateCache();
   }

   /**
    * Clears the list of calendar exceptions.
    */
   public void clearWorkWeeks()
   {
      m_workWeeks.clear();
      m_weeksSorted = false;
      clearWorkingDateCache();
   }

   /**
    * Retrieve the work weeks associated with this calendar.
    *
    * @return list of work weeks
    */
   public List<ProjectCalendarWeek> getWorkWeeks()
   {
      return Collections.unmodifiableList(m_workWeeks);
   }

   /**
    * Add an exception to the calendar.
    *
    * @param date exception date
    * @return ProjectCalendarException instance
    */
   public ProjectCalendarException addCalendarException(LocalDate date)
   {
      return addCalendarException(date, date, null);
   }

   /**
    * Add an exception to the calendar.
    *
    * @param fromDate exception start date
    * @param toDate exception end date
    * @return ProjectCalendarException instance
    */
   public ProjectCalendarException addCalendarException(LocalDate fromDate, LocalDate toDate)
   {
      return addCalendarException(fromDate, toDate, null);
   }

   /**
    * Add a recurring exception to the calendar.
    *
    * @param recurringData RecurringData instance used to define the exception occurrences
    * @return ProjectCalendarException instance
    */
   public ProjectCalendarException addCalendarException(RecurringData recurringData)
   {
      return addCalendarException(null, null, recurringData);
   }

   /**
    * Internal method to add a normal or recurring exception to the calendar.
    *
    * @param fromDate exception start date
    * @param toDate exception end date
    * @param recurringData RecurringData instance used to define the exception occurrences
    * @return ProjectCalendarException instance
    */
   private ProjectCalendarException addCalendarException(LocalDate fromDate, LocalDate toDate, RecurringData recurringData)
   {
      ProjectCalendarException bce = new ProjectCalendarException(fromDate, toDate, recurringData);
      m_exceptions.add(bce);
      m_expandedExceptions.clear();
      m_exceptionsSorted = false;
      clearWorkingDateCache();
      return bce;
   }

   /**
    * Remove an exception from the calendar.
    *
    * @param exception exception to remove
    */
   public void removeCalendarException(ProjectCalendarException exception)
   {
      m_exceptions.remove(exception);
      m_expandedExceptions.clear();
      clearWorkingDateCache();
   }

   /**
    * Clears the list of calendar exceptions.
    */
   public void clearCalendarExceptions()
   {
      m_exceptions.clear();
      m_expandedExceptions.clear();
      m_exceptionsSorted = false;
      clearWorkingDateCache();
   }

   /**
    * This method retrieves a list of exceptions to the current calendar.
    * Recurring exceptions are represented by a single exception which
    * contains the definition of the recurrence.
    *
    * @return List of calendar exceptions
    */
   public List<ProjectCalendarException> getCalendarExceptions()
   {
      sortExceptions();
      return Collections.unmodifiableList(m_exceptions);
   }

   /**
    * This method retrieves a list of exceptions to the current calendar.
    * Recurring exceptions are replaced by explicit exceptions representing
    * each recurrence.
    *
    * @return List of calendar exceptions
    */
   public List<ProjectCalendarException> getExpandedCalendarExceptions()
   {
      populateExpandedExceptions();
      return Collections.unmodifiableList(m_expandedExceptions);
   }

   /**
    * Expand any exceptions in the calendar, and include any working weeks
    * defined by this calendar as exceptions. This is typically used to communicate
    * working time accurately when the consuming application does not have the concept
    * of working weeks.
    *
    * @return List of calendar exceptions representing exceptions and working weeks
    */
   public List<ProjectCalendarException> getExpandedCalendarExceptionsWithWorkWeeks()
   {
      if (m_workWeeks.isEmpty())
      {
         return getExpandedCalendarExceptions();
      }

      ProjectCalendar temporaryCalendar = new TemporaryCalendar(getParentFile());
      ProjectCalendarHelper.mergeExceptions(temporaryCalendar, getCalendarExceptions());
      LocalDate earliestStartDate = LocalDateHelper.getLocalDate(m_projectFile.getEarliestStartDate());
      LocalDate latestFinishDate = LocalDateHelper.getLocalDate(m_projectFile.getLatestFinishDate());

      for (ProjectCalendarWeek week : getWorkWeeks())
      {
         ProjectCalendarHelper.mergeExceptions(temporaryCalendar, week.convertToRecurringExceptions(earliestStartDate, latestFinishDate));
      }

      return temporaryCalendar.getExpandedCalendarExceptions();
   }

   /**
    * Used to add working hours to the calendar.
    *
    * @param day day number
    * @return new ProjectCalendarHours instance
    */
   @Override public ProjectCalendarHours addCalendarHours(DayOfWeek day)
   {
      clearWorkingDateCache();
      return super.addCalendarHours(day);
   }

   /**
    * Used to remove working hours from the calendar.
    *
    * @param day target day
    */
   @Override public void removeCalendarHours(DayOfWeek day)
   {
      clearWorkingDateCache();
      super.removeCalendarHours(day);
   }

   /**
    * Sets the ProjectCalendar instance from which this calendar is derived.
    *
    * @param calendar base calendar instance
    */
   public void setParent(ProjectCalendar calendar)
   {
      // I've seen a malformed MSPDI file which sets the parent calendar to itself.
      // Silently ignore this here.
      if (calendar != this)
      {
         m_parent = calendar;
         Arrays.stream(DayOfWeek.values()).filter(d -> getCalendarDayType(d) == null).forEach(d -> setCalendarDayType(d, DayType.DEFAULT));
         clearWorkingDateCache();
      }
   }

   /**
    * Retrieve the parent calendar, or {@code null} if the
    * calendar does not have a parent.
    *
    * @return parent calendar
    */
   public ProjectCalendar getParent()
   {
      return m_parent;
   }

   /**
    * Retrieve the parent calendar unique ID.
    *
    * @return parent calendar unique ID, or null if there is no parent calendar
    */
   public Integer getParentUniqueID()
   {
      return m_parent == null ? null : m_parent.getUniqueID();
   }

   /**
    * This method is provided to allow an absolute period of time
    * represented by start and end dates into a duration in working
    * days based on this calendar instance. This method takes account
    * of any exceptions defined for this calendar.
    *
    * @param startDate start of the period
    * @param endDate end of the period
    * @return new Duration object
    */
   public Duration getDuration(LocalDateTime startDate, LocalDateTime endDate)
   {
      if (startDate == null || endDate == null)
      {
         return null;
      }

      int days = getDaysInRange(startDate, endDate);
      int duration = 0;

      while (days > 0)
      {
         if (isWorkingDate(LocalDateHelper.getLocalDate(startDate)))
         {
            ++duration;
         }

         --days;
         startDate = startDate.plusDays(1);
      }

      return (Duration.getInstance(duration, TimeUnit.DAYS));
   }

   /**
    * Retrieves the time at which work starts on the given date, or returns
    * null if this is a non-working day.
    *
    * @param date Date instance
    * @return start time, or null for non-working day
    */
   public LocalTime getStartTime(LocalDate date)
   {
      if (date == null)
      {
         return null;
      }

      LocalTime result = m_startTimeCache.get(date);
      if (result != null)
      {
         return result;
      }

      ProjectCalendarHours ranges = getRanges(date);
      if (ranges == null || ranges.isEmpty())
      {
         return null;
      }

      result = ranges.get(0).getStart();
      m_startTimeCache.put(date, result);

      return result;
   }

   /**
    * Retrieves the time at which work finishes on the given date, or returns
    * null if this is a non-working day.
    *
    * @param date Date instance
    * @return finish time, or null for non-working day
    */
   public LocalTime getFinishTime(LocalDate date)
   {
      if (date == null)
      {
         return null;
      }

      ProjectCalendarHours ranges = getRanges(date);
      if (ranges == null || ranges.isEmpty())
      {
         return null;
      }

      return ranges.get(ranges.size() - 1).getEnd();
   }

   /**
    * Given a date and a duration, this method calculates the resulting date when the duration is added.
    * This method handles both positive and negative durations.
    *
    * @param date date
    * @param duration duration
    * @return date plus duration
    */
   public LocalDateTime getDate(LocalDateTime date, Duration duration)
   {
      if (duration.getUnits().isElapsed())
      {
         ProjectProperties properties = getParentFile().getProjectProperties();
         double elapsedMinutes = duration.convertUnits(TimeUnit.ELAPSED_MINUTES, properties).getDuration();
         return date.plusMinutes((long) elapsedMinutes);
      }

      return duration.getDuration() < 0 ? getDateFromNegativeDuration(date, duration) : getDateFromPositiveDuration(date, duration);
   }

   private LocalDateTime getDateFromPositiveDuration(LocalDateTime startDate, Duration duration)
   {
      ProjectProperties properties = getParentFile().getProjectProperties();
      long remainingMilliseconds = Math.round(NumberHelper.round(duration.convertUnits(TimeUnit.MINUTES, properties).getDuration(), 2) * 60000.0);
      if (remainingMilliseconds == 0)
      {
         return startDate;
      }

      //
      // Can we skip come computation by working forward from the
      // last call to this method?
      //
      LocalDateTime getDateLastStartDate = m_getDateLastStartDate;
      long getDateLastRemainingMilliseconds = m_getDateLastRemainingMilliseconds;

      m_getDateLastStartDate = startDate;
      m_getDateLastRemainingMilliseconds = remainingMilliseconds;

      if (m_getDateLastResult != null && LocalDateTimeHelper.compare(startDate, getDateLastStartDate) == 0 && remainingMilliseconds >= getDateLastRemainingMilliseconds)
      {
         startDate = m_getDateLastResult;
         remainingMilliseconds = remainingMilliseconds - getDateLastRemainingMilliseconds;
         if (remainingMilliseconds == 0)
         {
            return startDate;
         }
      }

      LocalDateTime currentDayStart = startDate;
      LocalDateTime currentDayEnd = LocalDateTimeHelper.getDayStartDate(currentDayStart).plusDays(1);

      while (remainingMilliseconds > 0)
      {
         long currentDateWorkingMilliseconds = Math.round(getWork(currentDayStart, currentDayEnd, TimeUnit.MINUTES).getDuration() * 60000.0);

         if (remainingMilliseconds == currentDateWorkingMilliseconds)
         {
            currentDayEnd = LocalTimeHelper.setEndTime(currentDayStart, getFinishTime(currentDayStart.toLocalDate()));
            break;
         }

         //
         // We have more than enough hours left
         //
         if (remainingMilliseconds > currentDateWorkingMilliseconds)
         {
            //
            // Deduct this day's hours from our total
            //
            remainingMilliseconds -= currentDateWorkingMilliseconds;

            //
            // Move the calendar forward to the next working day
            //
            LocalDateTime currentDay = currentDayStart;
            int nonWorkingDayCount = 0;
            do
            {
               currentDay = currentDay.plusDays(1);
               ++nonWorkingDayCount;
               if (nonWorkingDayCount > MAX_NONWORKING_DAYS)
               {
                  currentDay = currentDayStart.plusDays(1);
                  remainingMilliseconds = 0;
                  break;
               }
            }
            while (!isWorkingDate(LocalDateHelper.getLocalDate(currentDay)));

            currentDayStart = LocalTimeHelper.setTime(currentDay, getStartTime(LocalDateHelper.getLocalDate(currentDay)));
            currentDayEnd = LocalTimeHelper.setEndTime(currentDayStart, getFinishTime(LocalDateHelper.getLocalDate(currentDayStart)));
         }
         else
         {
            //
            // We have fewer hours to allocate than there are working hours
            // in this day. We need to calculate the time of day at which
            // our work ends.
            //
            ProjectCalendarHours ranges = getRanges(LocalDateHelper.getLocalDate(currentDayStart));

            LocalTime currentDayStartTime = LocalTimeHelper.getLocalTime(currentDayStart);
            boolean firstRange = true;
            for (LocalTimeRange range : ranges)
            {
               //
               // Skip this range if its end is before our start time
               //
               LocalTime rangeStart = range.getStart();
               LocalTime rangeEnd = range.getEnd();

               if (rangeStart == null || rangeEnd == null)
               {
                  continue;
               }

               if (firstRange && rangeEnd != LocalTime.MIDNIGHT && rangeEnd.isBefore(currentDayStartTime))
               {
                  continue;
               }

               //
               // Move the start of the range if our current start is
               // past the range start
               //
               if (firstRange && rangeStart.isBefore(currentDayStartTime))
               {
                  rangeStart = currentDayStartTime;
               }
               firstRange = false;

               long rangeMilliseconds = LocalTimeHelper.getMillisecondsInRange(rangeStart, rangeEnd);
               if (remainingMilliseconds > rangeMilliseconds)
               {
                  remainingMilliseconds -= rangeMilliseconds;
               }
               else
               {
                  if (remainingMilliseconds != rangeMilliseconds)
                  {
                     rangeEnd = rangeStart.plus(remainingMilliseconds, ChronoUnit.MILLIS);
                  }
                  currentDayEnd = LocalTimeHelper.setTime(currentDayStart, rangeEnd);
                  remainingMilliseconds = 0;
                  break;
               }
            }
         }
      }

      // Truncate to remove milliseconds
      if (currentDayEnd.getNano() != 0)
      {
         currentDayEnd = LocalDateTime.of(currentDayEnd.toLocalDate(), LocalTime.of(currentDayEnd.getHour(), currentDayEnd.getMinute(), currentDayEnd.getSecond()));
      }

      m_getDateLastResult = currentDayEnd;

      return currentDayEnd;
   }

   private LocalDateTime getDateFromNegativeDuration(LocalDateTime endDate, Duration duration)
   {
      ProjectProperties properties = getParentFile().getProjectProperties();
      long remainingMilliseconds = -Math.round(NumberHelper.round(duration.convertUnits(TimeUnit.MINUTES, properties).getDuration(), 2) * 60000.0);
      if (remainingMilliseconds == 0)
      {
         return endDate;
      }

      // Set the initial day start and end dates
      LocalDateTime currentDayStart;
      LocalDateTime currentDayEnd = endDate;

      if (currentDayEnd.toLocalTime() == LocalTime.MIDNIGHT)
      {
         currentDayStart = LocalDateTimeHelper.getDayStartDate(currentDayEnd.minusDays(1));
      }
      else
      {
         currentDayStart = LocalDateTimeHelper.getDayStartDate(currentDayEnd);
      }

      while (remainingMilliseconds > 0)
      {
         long currentDayWorkingMilliseconds = Math.round(getWork(currentDayStart, currentDayEnd, TimeUnit.MINUTES).getDuration() * 60000.0);

         //
         // We have exactly the time we need
         //
         if (remainingMilliseconds == currentDayWorkingMilliseconds)
         {
            currentDayStart = LocalTimeHelper.setTime(currentDayStart, getStartTime(currentDayStart.toLocalDate()));
            break;
         }

         if (remainingMilliseconds > currentDayWorkingMilliseconds)
         {
            //
            // Deduct this day's hours from our total
            //
            remainingMilliseconds -= currentDayWorkingMilliseconds;

            //
            // Move the calendar back to the previous working day
            //
            int nonWorkingDayCount = 0;
            LocalDateTime currentDay = currentDayStart;
            do
            {
               currentDay = currentDay.minusDays(1);
               ++nonWorkingDayCount;
               if (nonWorkingDayCount > MAX_NONWORKING_DAYS)
               {
                  currentDay = currentDayStart.minusDays(1);
                  remainingMilliseconds = 0;
                  break;
               }
            }
            while (!isWorkingDate(LocalDateHelper.getLocalDate(currentDay)));

            currentDayStart = currentDay;
            currentDayEnd = LocalTimeHelper.setEndTime(currentDayStart, getFinishTime(LocalDateHelper.getLocalDate(currentDayStart)));
         }
         else
         {
            //
            // We have fewer hours to allocate than there are working hours
            // in this day. We need to calculate the time of day at which
            // our work starts.
            //
            List<LocalTimeRange> ranges = new ArrayList<>(getRanges(LocalDateHelper.getLocalDate(currentDayStart)));
            Collections.reverse(ranges);

            //
            // Now we have the range of working hours for this day,
            // step through it to work out the end point
            //
            LocalTime currentDayEndTime = LocalTimeHelper.getLocalTime(currentDayEnd);
            boolean lastRange = true;
            for (LocalTimeRange range : ranges)
            {
               //
               // Skip this range if our end time is before the range start
               //
               LocalTime rangeStart = range.getStart();
               LocalTime rangeEnd = range.getEnd();

               if (rangeStart == null || rangeEnd == null)
               {
                  continue;
               }

               if (currentDayEndTime != LocalTime.MIDNIGHT && currentDayEndTime.isBefore(rangeStart))
               {
                  continue;
               }

               //
               // Move the end of the range if our current end is
               // before the range end
               //
               if (lastRange && (rangeEnd == LocalTime.MIDNIGHT || rangeEnd.isAfter(currentDayEndTime)))
               {
                  rangeEnd = currentDayEndTime;
               }
               lastRange = false;

               long rangeMilliseconds = LocalTimeHelper.getMillisecondsInRange(rangeStart, rangeEnd);
               if (remainingMilliseconds > rangeMilliseconds)
               {
                  remainingMilliseconds -= rangeMilliseconds;
               }
               else
               {
                  if (remainingMilliseconds != rangeMilliseconds)
                  {
                     rangeStart = rangeEnd.minus(remainingMilliseconds, ChronoUnit.MILLIS);
                  }
                  currentDayStart = LocalTimeHelper.setTime(currentDayStart, rangeStart);
                  remainingMilliseconds = 0;
                  break;
               }
            }
         }
      }

      // Truncate to remove milliseconds
      if (currentDayStart.getNano() != 0)
      {
         currentDayStart = LocalDateTime.of(currentDayStart.toLocalDate(), LocalTime.of(currentDayStart.getHour(), currentDayStart.getMinute(), currentDayStart.getSecond()));
      }

      return currentDayStart;
   }

   /**
    * Utility method to retrieve the next working date start time, given
    * a date and time as a starting point.
    *
    * @param date date and time start point
    * @return date and time of next work start
    */
   public LocalDateTime getNextWorkStart(LocalDateTime date)
   {
      LocalDateTime originalDate = date;

      //
      // Find the date ranges for the current day
      //
      ProjectCalendarHours ranges = getRanges(LocalDateHelper.getLocalDate(originalDate));

      if (ranges != null)
      {
         //
         // Do we have a start time today?
         //
         LocalTime calTime = date.toLocalTime();
         LocalTime startTime = null;
         for (LocalTimeRange range : ranges)
         {
            LocalTime rangeStart = range.getStart();
            LocalTime rangeEnd = range.getEnd();

            if (rangeEnd == LocalTime.MIDNIGHT || calTime.isBefore(rangeEnd))
            {
               if (calTime.isAfter(rangeStart))
               {
                  startTime = calTime;
               }
               else
               {
                  startTime = rangeStart;
               }
               break;
            }
         }

         //
         // If we don't have a start time today - find the next working day
         // then retrieve the start time.
         //
         if (startTime == null)
         {
            int nonWorkingDayCount = 0;
            do
            {
               date = date.plusDays(1);
               ++nonWorkingDayCount;
               if (nonWorkingDayCount > MAX_NONWORKING_DAYS)
               {
                  date = originalDate;
                  break;
               }
            }
            while (!isWorkingDate(LocalDateHelper.getLocalDate(date)));

            startTime = getStartTime(LocalDateHelper.getLocalDate(date));
         }

         date = LocalTimeHelper.setTime(date, startTime);
      }

      return date;
   }

   /**
    * Utility method to retrieve the previous working date finish time, given
    * a date and time as a starting point.
    *
    * @param date date and time start point
    * @return date and time of previous work finish
    */
   public LocalDateTime getPreviousWorkFinish(LocalDateTime date)
   {
      LocalDateTime originalDate = date;

      //
      // Find the date ranges for the current day
      //
      ProjectCalendarHours ranges = getRanges(LocalDateHelper.getLocalDate(originalDate));
      if (ranges != null)
      {
         //
         // Do we have a finish time today?
         //
         LocalTime calTime = LocalTimeHelper.getLocalTime(date);
         LocalTime finishTime = null;
         for (int index = ranges.size(); index-- > 0;)
         {
            LocalTimeRange range = ranges.get(index);
            if ((range.getEnd() == LocalTime.MIDNIGHT && calTime == LocalTime.MIDNIGHT) || !calTime.isBefore(range.getEnd()))
            {
               finishTime = range.getEnd();
               break;
            }
         }

         //
         // If we don't have a finish time today - find the previous working day
         // then retrieve the finish time.
         //
         if (finishTime == null)
         {
            int nonWorkingDayCount = 0;
            do
            {
               date = date.minusDays(1);
               ++nonWorkingDayCount;
               if (nonWorkingDayCount > MAX_NONWORKING_DAYS)
               {
                  date = originalDate;
                  break;
               }
            }
            while (!isWorkingDate(LocalDateHelper.getLocalDate(date)));

            finishTime = getFinishTime(LocalDateHelper.getLocalDate(date));
         }

         date = LocalTimeHelper.setEndTime(date, finishTime);
      }

      return date;
   }

   /**
    * Retrieve the day type. If this is a derived calendar and the day type is
    * DEFAULT, recurse through the calendar hierarchy to find the effective
    * day type. Note that if the calendar hierarchy has been incorrectly
    * configured and that the base calendar defines the day as DEFAULT,
    * this implementation will assume Saturday and Sunday are non-working
    * days.
    *
    * @param day required day
    * @return day type
    */
   public DayType getDayType(DayOfWeek day)
   {
      DayType result = getCalendarDayType(day);
      if (result == DayType.DEFAULT)
      {
         if (m_parent == null)
         {
            result = (day == DayOfWeek.SATURDAY || day == DayOfWeek.SUNDAY) ? DayType.NON_WORKING : DayType.WORKING;
         }
         else
         {
            result = m_parent.getDayType(day);
         }
      }
      return result;
   }

   /**
    * Method indicating whether a day is a working or non-working day.
    *
    * @param day required day
    * @return true if this is a working day
    */
   public boolean isWorkingDay(DayOfWeek day)
   {
      return getDayType(day) == DayType.WORKING;
   }

   /**
    * This method allows the caller to determine if a given date is a
    * working day. This method takes account of calendar exceptions.
    *
    * @param date Date to be tested
    * @return boolean value
    */
   public boolean isWorkingDate(LocalDate date)
   {
      return !getRanges(date).isEmpty();
   }

   /**
    * This method calculates the absolute number of days between two dates.
    * Note that where two date objects are provided that fall on the same
    * day, this method will return one not zero. Note also that this method
    * assumes that the dates are passed in the correct order, i.e.
    * startDate < endDate.
    *
    * @param startDate Start date
    * @param endDate End date
    * @return number of days in the date range
    */
   private int getDaysInRange(LocalDateTime startDate, LocalDateTime endDate)
   {
      int result;
      int endDateYear = endDate.getYear();
      int endDateDayOfYear = endDate.getDayOfYear();

      LocalDateTime cal = startDate;

      if (endDateYear == cal.getYear())
      {
         result = (endDateDayOfYear - cal.getDayOfYear()) + 1;
      }
      else
      {
         result = 0;
         do
         {
            result += (Year.of(cal.getYear()).length() - cal.getDayOfYear()) + 1;
            cal = LocalDateTime.of(cal.getYear() + 1, 1, 1, 0, 0);
         }
         while (cal.getYear() < endDateYear);
         result += endDateDayOfYear;
      }

      return result;
   }

   /**
    * This method retrieves the calendar hours for the specified day.
    * Note that if this is a derived calendar, then this method
    * will refer to the base calendar where no hours are specified
    * in the derived calendar.
    *
    * @param day Day instance
    * @return calendar hours
    */
   public ProjectCalendarHours getHours(DayOfWeek day)
   {
      ProjectCalendarHours result = getCalendarHours(day);
      if (result == null && m_parent != null)
      {
         result = m_parent.getHours(day);
      }
      return result;
   }

   /**
    * This method retrieves the calendar hours for the specified date.
    * Note that if this is a derived calendar, then this method
    * will refer to the base calendar where no hours are specified
    * in the derived calendar.
    *
    * @param date target date
    * @return working hours on the given date
    */
   public ProjectCalendarHours getHours(LocalDate date)
   {
      return getRanges(date);
   }

   /**
    * This method retrieves the calendar hours for the specified date.
    * Note that if this is a derived calendar, then this method
    * will refer to the base calendar where no hours are specified
    * in the derived calendar.
    *
    * @param date target date
    * @return working hours on the given date
    */
   public ProjectCalendarHours getHours(LocalDateTime date)
   {
      return getHours(LocalDateHelper.getLocalDate(date));
   }

   /**
    * Modifier method to set the unique ID of this calendar.
    *
    * @param uniqueID unique identifier
    */
   @Override public void setUniqueID(Integer uniqueID)
   {
      // If we have a temporary calendar, we don't want to modify the unique ID map for calendars
      if (!m_temporaryCalendar)
      {
         getParentFile().getCalendars().updateUniqueID(this, m_uniqueID, uniqueID);
      }
      m_uniqueID = uniqueID;
   }

   /**
    * Accessor method to retrieve the unique ID of this calendar.
    *
    * @return calendar unique identifier
    */
   @Override public Integer getUniqueID()
   {
      return m_uniqueID;
   }

   /**
    * Retrieve the calendar's GUID.
    *
    * @return calendar GUID
    */
   public UUID getGUID()
   {
      return m_guid;
   }

   /**
    * Set the calendar's GUID.
    *
    * @param value calendar GUID
    */
   public void setGUID(UUID value)
   {
      m_guid = value;
   }

   /**
    * Retrieve a list of tasks which use this calendar.
    *
    * @return list of tasks
    */
   public List<Task> getTasks()
   {
      return Collections.unmodifiableList(getParentFile().getTasks().stream().filter(t -> m_uniqueID.equals(t.getCalendarUniqueID())).collect(Collectors.toList()));
   }

   /**
    * Retrieve a list of the resources which use this calendar.
    *
    * @return list of resources
    */
   public List<Resource> getResources()
   {
      return Collections.unmodifiableList(getParentFile().getResources().stream().filter(r -> m_uniqueID.equals(r.getCalendarUniqueID())).collect(Collectors.toList()));
   }

   /**
    * Retrieve the number of resources using this calendar.
    *
    * @return number of resources
    */
   public int getResourceCount()
   {
      return (int) getParentFile().getResources().stream().filter(r -> m_uniqueID.equals(r.getCalendarUniqueID())).count();
   }

   /**
    * Removes this calendar from the project.
    */
   public void remove()
   {
      getParentFile().removeCalendar(this);
   }

   /**
    * Retrieve a calendar exception which applies to this date.
    *
    * @param date target date
    * @return calendar exception, or null if none match this date
    */
   public ProjectCalendarException getException(LocalDate date)
   {
      if (date == null)
      {
         return null;
      }

      ProjectCalendarException exception = null;

      // We're working with expanded exceptions, which includes any recurring exceptions
      // expanded into individual entries.
      populateExpandedExceptions();
      if (!m_expandedExceptions.isEmpty())
      {
         int low = 0;
         int high = m_expandedExceptions.size() - 1;

         while (low <= high)
         {
            int mid = (low + high) >>> 1;
            ProjectCalendarException midVal = m_expandedExceptions.get(mid);
            int cmp = LocalDateHelper.compare(midVal.getFromDate(), midVal.getToDate(), date);

            if (cmp > 0)
            {
               low = mid + 1;
            }
            else
            {
               if (cmp < 0)
               {
                  high = mid - 1;
               }
               else
               {
                  exception = midVal;
                  break;
               }
            }
         }
      }

      if (exception == null && m_parent != null)
      {
         // Check base calendar as well for an exception.
         exception = m_parent.getException(date);
      }
      return exception;
   }

   /**
    * Retrieve a work week which applies to this date.
    *
    * @param date target date
    * @return work week, or null if none match this date
    */
   public ProjectCalendarWeek getWorkWeek(LocalDate date)
   {
      if (date == null)
      {
         return null;
      }

      ProjectCalendarWeek week = null;
      if (!m_workWeeks.isEmpty())
      {
         sortWorkWeeks();

         int low = 0;
         int high = m_workWeeks.size() - 1;

         while (low <= high)
         {
            int mid = (low + high) >>> 1;
            ProjectCalendarWeek midVal = m_workWeeks.get(mid);
            int cmp = LocalDateHelper.compare(midVal.getDateRange().getStart(), midVal.getDateRange().getEnd(), date);

            if (cmp > 0)
            {
               low = mid + 1;
            }
            else
            {
               if (cmp < 0)
               {
                  high = mid - 1;
               }
               else
               {
                  week = midVal;
                  break;
               }
            }
         }
      }

      if (week == null && m_parent != null)
      {
         // Check base calendar as well for a work week.
         week = m_parent.getWorkWeek(date);
      }
      return week;
   }

   /**
    * Retrieves the amount of work on a given day, and
    * returns it in the specified format. Note that
    * as we're working with a day rather than a specific date,
    * we'll be providing the "default" amount of work,
    * unchanged by exceptions or working weeks.
    *
    * @param day target day
    * @param format required format
    * @return work duration
    */
   public Duration getWork(DayOfWeek day, TimeUnit format)
   {
      return convertFormat(getTotalTime(getRanges(day)), format);
   }

   /**
    * Retrieves the amount of work on a given date, and
    * returns it in the specified format.
    *
    * @param date target date
    * @param format required format
    * @return work duration
    */
   public Duration getWork(LocalDate date, TimeUnit format)
   {
      return convertFormat(getTotalTime(getRanges(date)), format);
   }

   /**
    * This method retrieves a Duration instance representing the amount of
    * work between two dates based on this calendar.
    *
    * @param startDate start date
    * @param endDate end date
    * @param format required duration format
    * @return amount of work
    */
   public Duration getWork(LocalDateTime startDate, LocalDateTime endDate, TimeUnit format)
   {
      if (startDate == null || endDate == null)
      {
         return null;
      }

      LocalDateTimeRange range = new LocalDateTimeRange(startDate, endDate);
      Long cachedResult = m_workingDateCache.get(range);
      long totalTime = 0;

      if (cachedResult == null)
      {
         //
         // We want the start date to be the earliest date, and the end date
         // to be the latest date. Set a flag here to indicate if we have swapped
         // the order of the supplied date.
         //
         boolean invert = false;
         if (startDate.isAfter(endDate))
         {
            invert = true;
            LocalDateTime temp = startDate;
            startDate = endDate;
            endDate = temp;
         }

         if (isSameDay(startDate, endDate))
         {
            ProjectCalendarHours ranges = getRanges(LocalDateHelper.getLocalDate(startDate));
            if (!ranges.isEmpty())
            {
               totalTime = getTotalTime(ranges, LocalTimeHelper.getLocalTime(startDate), LocalTimeHelper.getLocalTime(endDate));
            }
         }
         else
         {
            LocalDateTime canonicalEndDate = LocalDateTimeHelper.getDayStartDate(endDate);

            //
            // Find the first working day in the range
            //
            LocalDateTime currentDate = startDate;
            LocalDateTime cal = startDate;
            while (!isWorkingDate(LocalDateHelper.getLocalDate(currentDate)) && currentDate.isBefore(canonicalEndDate))
            {
               cal = cal.plusDays(1);
               currentDate = cal;
            }

            if (currentDate.isBefore(canonicalEndDate))
            {
               // If the first working day is the same as the start date, we leave
               // the date alone to preserve the start time. If we have moved past
               // the start date to find the first working day, reset the time
               // of day to ensure that we use all working hours on this day.
               LocalTime targetTime = currentDate.equals(startDate) ? LocalTimeHelper.getLocalTime(currentDate) : LocalTime.of(0, 0);

               //
               // Calculate the amount of working time for this day
               //
               totalTime += getTotalTime(getRanges(LocalDateHelper.getLocalDate(currentDate)), targetTime);

               //
               // Process each working day until we reach the last day
               //
               while (true)
               {
                  cal = cal.plusDays(1);
                  currentDate = cal;

                  //
                  // We have reached the last day
                  //
                  if (!currentDate.isBefore(canonicalEndDate))
                  {
                     break;
                  }

                  //
                  // Skip this day if it has no working time
                  //
                  ProjectCalendarHours ranges = getRanges(LocalDateHelper.getLocalDate(currentDate));
                  if (ranges.isEmpty())
                  {
                     continue;
                  }

                  //
                  // Add the working time for the whole day
                  //
                  totalTime += getTotalTime(ranges);
               }
            }

            //
            // We are now at the last day
            //
            ProjectCalendarHours ranges = getRanges(LocalDateHelper.getLocalDate(endDate));
            if (!ranges.isEmpty())
            {
               totalTime += getTotalTime(ranges, LocalTime.of(0, 0), LocalTimeHelper.getLocalTime(endDate));
            }
         }

         if (invert)
         {
            totalTime = -totalTime;
         }

         m_workingDateCache.put(range, Long.valueOf(totalTime));
      }
      else
      {
         totalTime = cachedResult.longValue();
      }

      return convertFormat(totalTime, format);
   }

   private boolean isSameDay(LocalDateTime d1, LocalDateTime d2)
   {
      if (d1 == null || d2 == null)
      {
         return false;
      }

      return d1.getYear() == d2.getYear() && d1.getDayOfYear() == d2.getDayOfYear();
   }

   /**
    * Utility method used to convert an integer time representation into a
    * Duration instance.
    *
    * @param totalTime integer time representation
    * @param format required time format
    * @return new Duration instance
    */
   private Duration convertFormat(long totalTime, TimeUnit format)
   {
      double duration = totalTime;

      switch (format)
      {
         case MINUTES:
         case ELAPSED_MINUTES:
         {
            duration /= (60 * 1000);
            break;
         }

         case HOURS:
         case ELAPSED_HOURS:
         {
            duration /= (60 * 60 * 1000);
            break;
         }

         case DAYS:
         {
            double minutesPerDay = NumberHelper.getDouble(getMinutesPerDay());
            if (minutesPerDay != 0)
            {
               duration /= (minutesPerDay * 60 * 1000);
            }
            else
            {
               duration = 0;
            }
            break;
         }

         case WEEKS:
         {
            double minutesPerWeek = NumberHelper.getDouble(getMinutesPerWeek());
            if (minutesPerWeek != 0)
            {
               duration /= (minutesPerWeek * 60 * 1000);
            }
            else
            {
               duration = 0;
            }
            break;
         }

         case MONTHS:
         {
            double daysPerMonth = getParentFile().getProjectProperties().getDaysPerMonth().doubleValue();
            double minutesPerDay = NumberHelper.getDouble(getMinutesPerDay());
            if (daysPerMonth != 0 && minutesPerDay != 0)
            {
               duration /= (daysPerMonth * minutesPerDay * 60 * 1000);
            }
            else
            {
               duration = 0;
            }
            break;
         }

         case ELAPSED_DAYS:
         {
            duration /= (24 * 60 * 60 * 1000);
            break;
         }

         case ELAPSED_WEEKS:
         {
            duration /= (7 * 24 * 60 * 60 * 1000);
            break;
         }

         case ELAPSED_MONTHS:
         {
            duration /= (30.0 * 24.0 * 60.0 * 60.0 * 1000.0);
            break;
         }

         default:
         {
            throw new IllegalArgumentException("TimeUnit " + format + " not supported");
         }
      }

      return Duration.getInstance(duration, format);
   }

   /**
    * Retrieves the amount of time represented by a set of calendar hours
    * before or after an intersection point.
    *
    * @param hours calendar hours
    * @param targetTime intersection time
    * @return length of time in milliseconds
    */
   private long getTotalTime(ProjectCalendarHours hours, LocalTime targetTime)
   {
      long total = 0;
      for (LocalTimeRange range : hours)
      {
         if (range.getEnd() == LocalTime.MIDNIGHT || !targetTime.isAfter(range.getEnd()))
         {
            total += getTime(range.getStart(), range.getEnd(), targetTime, range.getEnd());
         }
      }
      return total;
   }

   /**
    * Retrieves the amount of working time represented by
    * a calendar exception.
    *
    * @param hours calendar exception
    * @return length of time in milliseconds
    */
   private long getTotalTime(ProjectCalendarHours hours)
   {
      return hours.stream().mapToLong(LocalTimeRange::getDurationAsMilliseconds).sum();
   }

   /**
    * This method calculates the total amount of working time in a single
    * day, which intersects with the supplied time range.
    *
    * @param hours collection of working hours in a day
    * @param start time range start
    * @param end time range end
    * @return length of time in milliseconds
    */
   private long getTotalTime(ProjectCalendarHours hours, LocalTime start, LocalTime end)
   {
      if (start.equals(end))
      {
         return 0;
      }

      long total = 0;

      for (LocalTimeRange range : hours)
      {
         total += getTime(start, end, range.getStart(), range.getEnd());
      }

      return total;
   }

   /**
    * This method returns the length of overlapping time between two time
    * ranges.
    *
    * @param start1 start of first range
    * @param end1 end of first range
    * @param start2 start of second range
    * @param end2 end of second range
    * @return overlapping time in milliseconds
    */
   private long getTime(LocalTime start1, LocalTime end1, LocalTime start2, LocalTime end2)
   {
      if (start1 == null || end1 == null || start2 == null || end2 == null)
      {
         return 0;
      }

      LocalTime maxStart = start1.isAfter(start2) ? start1 : start2;

      LocalTime minEnd;
      if (end1 == LocalTime.MIDNIGHT && end2 != LocalTime.MIDNIGHT)
      {
         minEnd = end2;
      }
      else
      {
         if (end1 != LocalTime.MIDNIGHT && end2 == LocalTime.MIDNIGHT)
         {
            minEnd = end1;
         }
         else
         {
            minEnd = end1.isBefore(end2) ? end1 : end2;
         }
      }

      if (minEnd == LocalTime.MIDNIGHT || maxStart.isBefore(minEnd))
      {
         return LocalTimeHelper.getMillisecondsInRange(maxStart, minEnd);
      }

      return 0;
   }

   /**
    * Retrieve a list of derived calendars.
    *
    * @return list of derived calendars
    */
   public List<ProjectCalendar> getDerivedCalendars()
   {
      return Collections.unmodifiableList(m_projectFile.getCalendars().stream().filter(c -> c.m_parent != null && m_uniqueID != null && m_uniqueID.equals(c.m_parent.m_uniqueID)).collect(Collectors.toList()));
   }

   @Override public String toString()
   {
      ByteArrayOutputStream os = new ByteArrayOutputStream();
      PrintWriter pw = new PrintWriter(os);
      pw.println("[ProjectCalendar");
      pw.println("   ID=" + m_uniqueID);
      pw.println("   name=" + getName());
      pw.println("   baseCalendarName=" + (m_parent == null ? "" : m_parent.getName()));

      for (DayOfWeek day : DayOfWeek.values())
      {
         pw.println("   [Day " + day);
         pw.println("      type=" + getCalendarDayType(day));
         pw.println("      hours=" + getHours(day));
         pw.println("   ]");
      }

      if (!m_exceptions.isEmpty())
      {
         pw.println("   [Exceptions=");
         for (ProjectCalendarException ex : m_exceptions)
         {
            pw.println("      " + ex.toString());
         }
         pw.println("   ]");
      }

      if (!m_workWeeks.isEmpty())
      {
         pw.println("   [WorkWeeks=");
         for (ProjectCalendarWeek week : m_workWeeks)
         {
            pw.println("      " + week.toString());
         }
         pw.println("   ]");
      }

      pw.println("]");
      pw.flush();
      return (os.toString());
   }

   /**
    * Utility method to clear cached calendar data.
    */
   private void clearWorkingDateCache()
   {
      m_workingDateCache.clear();
      m_startTimeCache.clear();
      m_getDateLastResult = null;
      getDerivedCalendars().forEach(ProjectCalendar::clearWorkingDateCache);
   }

   /**
    * Retrieves the working hours on the given date.
    *
    * @param date required date
    * @return working hours
    */
   protected ProjectCalendarHours getRanges(LocalDate date)
   {
      // Check for exceptions for this date in this calendar and any base calendars
      ProjectCalendarHours ranges = getException(date);
      if (ranges != null)
      {
         return ranges;
      }

      // Determine which week definition to use
      ProjectCalendarDays week = getWorkWeek(date);
      if (week == null)
      {
         week = this;
      }

      // Use the day type to retrieve the ranges
      DayOfWeek day = date.getDayOfWeek();
      switch (week.getCalendarDayType(date.getDayOfWeek()))
      {
         case NON_WORKING:
         {
            ranges = EMPTY_DATE_RANGES;
            break;
         }

         case WORKING:
         {
            ranges = week.getCalendarHours(day);
            break;
         }

         case DEFAULT:
         {
            ranges = m_parent == null ? EMPTY_DATE_RANGES : m_parent.getHours(day);
            break;
         }
      }

      return ranges;
   }

   /**
    * Retrieves the working hours on the given day of the week.
    *
    * @param day required day
    * @return working hours
    */
   protected ProjectCalendarHours getRanges(DayOfWeek day)
   {
      switch (getCalendarDayType(day))
      {
         case NON_WORKING:
         {
            return EMPTY_DATE_RANGES;
         }

         case WORKING:
         {
            return getCalendarHours(day);
         }

         case DEFAULT:
         {
            return m_parent == null ? EMPTY_DATE_RANGES : m_parent.getHours(day);
         }
      }

      return EMPTY_DATE_RANGES;
   }

   /**
    * Ensure exceptions are sorted.
    */
   private void sortExceptions()
   {
      if (!m_exceptionsSorted)
      {
         Collections.sort(m_exceptions);
         m_exceptionsSorted = true;
      }
   }

   /**
    * Populate the expanded exceptions list based on the main exceptions list.
    * Where we find recurring exception definitions, we generate individual
    * exceptions for each recurrence to ensure that we account for them correctly.
    */
   private void populateExpandedExceptions()
   {
      if (m_exceptions.isEmpty() || !m_expandedExceptions.isEmpty())
      {
         return;
      }

      // Separate exceptions into recurring and non-recurring.
      // Non-recurring exceptions are grouped by their recurrence type.
      List<ProjectCalendarException> nonRecurring = new ArrayList<>();
      Map<RecurrenceType, List<ProjectCalendarException>> recurring = new HashMap<>();
      for (ProjectCalendarException exception : m_exceptions)
      {
         List<ProjectCalendarException> expanded = exception.getExpandedExceptions();
         if (expanded.size() == 1)
         {
            nonRecurring.add(expanded.get(0));
         }
         else
         {
            recurring.computeIfAbsent(exception.getRecurring().getRecurrenceType(), k -> new ArrayList<>()).add(exception);
         }
      }

      // Process the recurring exceptions in reverse priority order
      // to ensure that the final contents of the map reflect the effective
      // exception on each date.
      Map<LocalDate, ProjectCalendarException> map = new TreeMap<>();
      for (RecurrenceType type : ORDERED_RECURRENCE_TYPES)
      {
         recurring.computeIfAbsent(type, k -> Collections.emptyList()).forEach(e -> e.getExpandedExceptions().forEach(x -> map.put(x.getFromDate(), x)));
      }

      // Overlay the map with non-recurring exceptions
      // Non-recurring exceptions have the highest priority and override
      // expanded exceptions from recurring exceptions.
      for (ProjectCalendarException exception : nonRecurring)
      {
         map.put(exception.getFromDate(), exception);
      }

      // Note the use of TreeMap ensures our expanded exceptions are sorted
      m_expandedExceptions.addAll(map.values());
   }

   /**
    * Ensure work weeks are sorted.
    */
   private void sortWorkWeeks()
   {
      if (!m_weeksSorted)
      {
         Collections.sort(m_workWeeks);
         m_weeksSorted = true;
      }
   }

   /**
    * Accessor method allowing retrieval of ProjectFile reference.
    *
    * @return reference to this the parent ProjectFile instance
    */
   public final ProjectFile getParentFile()
   {
      return m_projectFile;
   }

   /**
    * Determine if this calendar is derived from another.
    *
    * @return true if this calendar is derived from another calendar
    */
   public boolean isDerived()
   {
      return m_parent != null;
   }

   /**
    * Retrieve the calendar type. Defaults to Global.
    *
    * @return calendar type
    */
   public CalendarType getType()
   {
      return m_type;
   }

   /**
    * Set the calendar type.
    * This will ignore any attempt to set the type to {@code null}
    *
    * @param type calendar type
    */
   public void setType(CalendarType type)
   {
      if (type != null)
      {
         m_type = type;
      }
   }

   /**
    * Returns true if this is a personal calendar.
    * Defaults to false.
    *
    * @return true if a personal calendar
    */
   public boolean getPersonal()
   {
      return m_personal;
   }

   /**
    * Set the flag to indicate if this is  personal calendar.
    *
    * @param personal true if this is a personal calendar
    */
   public void setPersonal(boolean personal)
   {
      m_personal = personal;
   }

   /**
    * Parent calendar.
    */
   private ProjectCalendar m_parent;

   /**
    * Parent project.
    */
   private final ProjectFile m_projectFile;

   /**
    * Unique identifier of this calendar.
    */
   private Integer m_uniqueID;

   /**
    * Calendar GUID.
    */
   private UUID m_guid;

   /**
    * List of exceptions to the base calendar.
    */
   private final List<ProjectCalendarException> m_exceptions = new ArrayList<>();

   /**
    * List of exceptions, including expansion of recurring exceptions.
    */
   private final List<ProjectCalendarException> m_expandedExceptions = new ArrayList<>();

   /**
    * Flag indicating if the list of exceptions is sorted.
    */
   private boolean m_exceptionsSorted;

   /**
    * Flag indicating if the list of weeks is sorted.
    */
   private boolean m_weeksSorted;

   /**
    * Caches used to speed up date calculations.
    */
   private final Map<LocalDateTimeRange, Long> m_workingDateCache = new WeakHashMap<>();
   private final Map<LocalDate, LocalTime> m_startTimeCache = new WeakHashMap<>();
   private LocalDateTime m_getDateLastStartDate;
   private long m_getDateLastRemainingMilliseconds;
   private LocalDateTime m_getDateLastResult;

   /**
    * Work week definitions.
    */
   private final ArrayList<ProjectCalendarWeek> m_workWeeks = new ArrayList<>();

   private Integer m_calendarMinutesPerDay;
   private Integer m_calendarMinutesPerWeek;
   private Integer m_calendarMinutesPerMonth;
   private Integer m_calendarMinutesPerYear;
   private CalendarType m_type = CalendarType.GLOBAL;
   private boolean m_personal;
   private final boolean m_temporaryCalendar;

   /**
    * Default base calendar name to use when none is supplied.
    */
   public static final String DEFAULT_BASE_CALENDAR_NAME = "Standard";

   /**
    * It is possible for a project calendar to be configured with no working
    * days. This will result in an infinite loop when looking for the next
    * working day from a date, so we use this constant to set a limit on the
    * maximum number of non-working days we'll skip before we bail out
    * and take an alternative approach.
    */
   private static final int MAX_NONWORKING_DAYS = 1000;

   private static final RecurrenceType[] ORDERED_RECURRENCE_TYPES =
   {
      RecurrenceType.WEEKLY,
      RecurrenceType.MONTHLY,
      RecurrenceType.YEARLY,
      RecurrenceType.DAILY
   };

   private static final ProjectCalendarHours EMPTY_DATE_RANGES = new ProjectCalendarHours()
   {
      // No implementation
   };
}
