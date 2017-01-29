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

package net.sf.mpxj;

import java.io.ByteArrayOutputStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.WeakHashMap;

import net.sf.mpxj.common.DateHelper;
import net.sf.mpxj.common.NumberHelper;

/**
 * This class represents the a Calendar Definition record. Both base calendars
 * and calendars derived from base calendars are represented by instances
 * of this class. The class is used to define the working and non-working days
 * of the week. The default calendar defines Monday to Friday as working days.
 */
public final class ProjectCalendar extends ProjectCalendarWeek implements ProjectEntityWithUniqueID
{
   /**
    * Default constructor.
    *
    * @param file the parent file to which this record belongs.
    */
   public ProjectCalendar(ProjectFile file)
   {
      m_projectFile = file;

      if (file.getProjectConfig().getAutoCalendarUniqueID() == true)
      {
         setUniqueID(Integer.valueOf(file.getProjectConfig().getNextCalendarUniqueID()));
      }
   }

   /**
    * Retrieve the number of minutes per day for this calendar.
    *
    * @return minutes per day
    */
   public int getMinutesPerDay()
   {
      return m_minutesPerDay == null ? NumberHelper.getInt(getParentFile().getProjectProperties().getMinutesPerDay()) : m_minutesPerDay.intValue();
   }

   /**
    * Retrieve the number of minutes per week for this calendar.
    *
    * @return minutes per week
    */
   public int getMinutesPerWeek()
   {
      return m_minutesPerWeek == null ? NumberHelper.getInt(getParentFile().getProjectProperties().getMinutesPerWeek()) : m_minutesPerWeek.intValue();
   }

   /**
    * Retrieve the number of minutes per month for this calendar.
    *
    * @return minutes per month
    */
   public int getMinutesPerMonth()
   {
      return m_minutesPerMonth == null ? NumberHelper.getInt(getParentFile().getProjectProperties().getMinutesPerMonth()) : m_minutesPerMonth.intValue();
   }

   /**
    * Retrieve the number of minutes per year for this calendar.
    *
    * @return minutes per year
    */
   public int getMinutesPerYear()
   {
      return m_minutesPerYear == null ? NumberHelper.getInt(getParentFile().getProjectProperties().getMinutesPerYear()) : m_minutesPerYear.intValue();
   }

   /**
    * Set the number of minutes per day for this calendar.
    *
    * @param minutes number of minutes
    */
   public void setMinutesPerDay(Integer minutes)
   {
      m_minutesPerDay = minutes;
   }

   /**
    * Set the number of minutes per week for this calendar.
    *
    * @param minutes number of minutes
    */
   public void setMinutesPerWeek(Integer minutes)
   {
      m_minutesPerWeek = minutes;
   }

   /**
    * Set the number of minutes per month for this calendar.
    *
    * @param minutes number of minutes
    */
   public void setMinutesPerMonth(Integer minutes)
   {
      m_minutesPerMonth = minutes;
   }

   /**
    * Set the number of minutes per year for this calendar.
    *
    * @param minutes number of minutes
    */
   public void setMinutesPerYear(Integer minutes)
   {
      m_minutesPerYear = minutes;
   }

   /**
    * Add an empty work week.
    *
    * @return new work week
    */
   public ProjectCalendarWeek addWorkWeek()
   {
      ProjectCalendarWeek week = new ProjectCalendarWeek();
      week.setParent(this);
      m_workWeeks.add(week);
      return week;
   }

   /**
    * Retrieve the work weeks associated with this calendar.
    *
    * @return list of work weeks
    */
   public List<ProjectCalendarWeek> getWorkWeeks()
   {
      return m_workWeeks;
   }

   /**
    * Used to add exceptions to the calendar. The MPX standard defines
    * a limit of 250 exceptions per calendar.
    *
    * @param fromDate exception start date
    * @param toDate exception end date
    * @return ProjectCalendarException instance
    */
   public ProjectCalendarException addCalendarException(Date fromDate, Date toDate)
   {
      ProjectCalendarException bce = new ProjectCalendarException(fromDate, toDate);
      m_exceptions.add(bce);
      m_exceptionsSorted = false;
      clearWorkingDateCache();
      return (bce);
   }

   /**
    * This method retrieves a list of exceptions to the current calendar.
    *
    * @return List of calendar exceptions
    */
   public List<ProjectCalendarException> getCalendarExceptions()
   {
      if (!m_exceptionsSorted)
      {
         Collections.sort(m_exceptions);
      }
      return (m_exceptions);
   }

   /**
    * Used to add working hours to the calendar. Note that the MPX file
    * definition allows a maximum of 7 calendar hours records to be added to
    * a single calendar.
    *
    * @param day day number
    * @return new ProjectCalendarHours instance
    */
   @Override public ProjectCalendarHours addCalendarHours(Day day)
   {
      clearWorkingDateCache();
      return super.addCalendarHours(day);
   }

   /**
    * Attaches a pre-existing set of hours to the correct
    * day within the calendar.
    *
    * @param hours calendar hours instance
    */
   @Override public void attachHoursToDay(ProjectCalendarHours hours)
   {
      clearWorkingDateCache();
      super.attachHoursToDay(hours);
   }

   /**
    * Removes a set of calendar hours from the day to which they
    * are currently attached.
    *
    * @param hours calendar hours instance
    */
   @Override public void removeHoursFromDay(ProjectCalendarHours hours)
   {
      clearWorkingDateCache();
      super.removeHoursFromDay(hours);
   }

   /**
    * Sets the ProjectCalendar instance from which this calendar is derived.
    *
    * @param calendar base calendar instance
    */
   public void setParent(ProjectCalendar calendar)
   {
      if (getParent() != null)
      {
         getParent().removeDerivedCalendar(this);
      }

      super.setParent(calendar);

      if (calendar != null)
      {
         calendar.addDerivedCalendar(this);
      }
      clearWorkingDateCache();

   }

   @Override public ProjectCalendar getParent()
   {
      return (ProjectCalendar) super.getParent();
   }

   /**
    * Method indicating whether a day is a working or non-working day.
    *
    * @param day required day
    * @return true if this is a working day
    */
   public boolean isWorkingDay(Day day)
   {
      DayType value = getWorkingDay(day);
      boolean result;

      if (value == DayType.DEFAULT)
      {
         ProjectCalendar cal = getParent();
         if (cal != null)
         {
            result = cal.isWorkingDay(day);
         }
         else
         {
            result = (day != Day.SATURDAY && day != Day.SUNDAY);
         }
      }
      else
      {
         result = (value == DayType.WORKING);
      }

      return (result);
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
   public Duration getDuration(Date startDate, Date endDate)
   {
      Calendar cal = Calendar.getInstance();
      cal.setTime(startDate);
      int days = getDaysInRange(startDate, endDate);
      int duration = 0;
      Day day = Day.getInstance(cal.get(Calendar.DAY_OF_WEEK));

      while (days > 0)
      {
         if (isWorkingDate(cal.getTime(), day) == true)
         {
            ++duration;
         }

         --days;
         day = day.getNextDay();
         cal.set(Calendar.DAY_OF_YEAR, cal.get(Calendar.DAY_OF_YEAR) + 1);
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
   public Date getStartTime(Date date)
   {
      Date result = m_startTimeCache.get(date);
      if (result == null)
      {
         ProjectCalendarDateRanges ranges = getRanges(date, null, null);
         if (ranges == null)
         {
            result = getParentFile().getProjectProperties().getDefaultStartTime();
         }
         else
         {
            result = ranges.getRange(0).getStart();
         }
         result = DateHelper.getCanonicalTime(result);
         m_startTimeCache.put(new Date(date.getTime()), result);
      }
      return result;
   }

   /**
    * Retrieves the time at which work finishes on the given date, or returns
    * null if this is a non-working day.
    *
    * @param date Date instance
    * @return finish time, or null for non-working day
    */
   public Date getFinishTime(Date date)
   {
      Date result = null;

      if (date != null)
      {
         ProjectCalendarDateRanges ranges = getRanges(date, null, null);
         if (ranges == null)
         {
            result = getParentFile().getProjectProperties().getDefaultEndTime();
            result = DateHelper.getCanonicalTime(result);
         }
         else
         {
            Date rangeStart = result = ranges.getRange(0).getStart();
            Date rangeFinish = ranges.getRange(ranges.getRangeCount() - 1).getEnd();
            Date startDay = DateHelper.getDayStartDate(rangeStart);
            Date finishDay = DateHelper.getDayStartDate(rangeFinish);

            result = DateHelper.getCanonicalTime(rangeFinish);

            //
            // Handle the case where the end of the range is at midnight -
            // this will show up as the start and end days not matching
            //
            if (startDay != null && finishDay != null && startDay.getTime() != finishDay.getTime())
            {
               Calendar calendar = Calendar.getInstance();
               calendar.setTime(result);
               calendar.add(Calendar.DAY_OF_YEAR, 1);
               result = calendar.getTime();
            }
         }
      }
      return result;
   }

   /**
    * Given a start date and a duration, this method calculates the
    * end date. It takes account of working hours in each day, non working
    * days and calendar exceptions. If the returnNextWorkStart parameter is
    * set to true, the method will return the start date and time of the next
    * working period if the end date is at the end of a working period.
    *
    * @param startDate start date
    * @param duration duration
    * @param returnNextWorkStart if set to true will return start of next working period
    * @return end date
    */
   public Date getDate(Date startDate, Duration duration, boolean returnNextWorkStart)
   {
      ProjectProperties properties = getParentFile().getProjectProperties();
      // Note: Using a double allows us to handle date values that are accurate up to seconds.
      //       However, it also means we need to truncate the value to 2 decimals to make the
      //       comparisons work as sometimes the double ends up with some extra e.g. .0000000000003
      //       that wreak havoc on the comparisons.
      double remainingMinutes = NumberHelper.truncate(duration.convertUnits(TimeUnit.MINUTES, properties).getDuration(), 2);
      Calendar cal = Calendar.getInstance();
      cal.setTime(startDate);
      Calendar endCal = Calendar.getInstance();

      while (remainingMinutes > 0)
      {
         //
         // Get the current date and time and determine how many
         // working hours remain
         //
         Date currentDate = cal.getTime();
         endCal.setTime(currentDate);
         endCal.add(Calendar.DAY_OF_YEAR, 1);
         Date currentDateEnd = DateHelper.getDayStartDate(endCal.getTime());
         double currentDateWorkingMinutes = getWork(currentDate, currentDateEnd, TimeUnit.MINUTES).getDuration();

         //
         // We have more than enough hours left
         //
         if (remainingMinutes > currentDateWorkingMinutes)
         {
            //
            // Deduct this day's hours from our total
            //
            remainingMinutes = NumberHelper.truncate(remainingMinutes - currentDateWorkingMinutes, 2);

            //
            // Move the calendar forward to the next working day
            //
            Day day;
            int nonWorkingDayCount = 0;
            do
            {
               cal.add(Calendar.DAY_OF_YEAR, 1);
               day = Day.getInstance(cal.get(Calendar.DAY_OF_WEEK));
               ++nonWorkingDayCount;
               if (nonWorkingDayCount > MAX_NONWORKING_DAYS)
               {
                  cal.setTime(startDate);
                  cal.add(Calendar.DAY_OF_YEAR, 1);
                  remainingMinutes = 0;
                  break;
               }
            }
            while (!isWorkingDate(cal.getTime(), day));

            //
            // Retrieve the start time for this day
            //
            Date startTime = getStartTime(cal.getTime());
            DateHelper.setTime(cal, startTime);
         }
         else
         {
            //
            // We have less hours to allocate than there are working hours
            // in this day. We need to calculate the time of day at which
            // our work ends.
            //
            ProjectCalendarDateRanges ranges = getRanges(cal.getTime(), cal, null);

            //
            // Now we have the range of working hours for this day,
            // step through it to work out the end point
            //
            Date endTime = null;
            Date currentDateStartTime = DateHelper.getCanonicalTime(currentDate);
            boolean firstRange = true;
            for (DateRange range : ranges)
            {
               //
               // Skip this range if its end is before our start time
               //
               Date rangeStart = range.getStart();
               Date rangeEnd = range.getEnd();

               if (rangeStart == null || rangeEnd == null)
               {
                  continue;
               }

               Date canonicalRangeEnd = DateHelper.getCanonicalTime(rangeEnd);
               Date canonicalRangeStart = DateHelper.getCanonicalTime(rangeStart);

               Date rangeStartDay = DateHelper.getDayStartDate(rangeStart);
               Date rangeEndDay = DateHelper.getDayStartDate(rangeEnd);

               if (rangeStartDay.getTime() != rangeEndDay.getTime())
               {
                  Calendar calendar = Calendar.getInstance();
                  calendar.setTime(canonicalRangeEnd);
                  calendar.add(Calendar.DAY_OF_YEAR, 1);
                  canonicalRangeEnd = calendar.getTime();
               }

               if (firstRange && canonicalRangeEnd.getTime() < currentDateStartTime.getTime())
               {
                  continue;
               }

               //
               // Move the start of the range if our current start is
               // past the range start
               //
               if (firstRange && canonicalRangeStart.getTime() < currentDateStartTime.getTime())
               {
                  canonicalRangeStart = currentDateStartTime;
               }
               firstRange = false;

               double rangeMinutes;

               rangeMinutes = canonicalRangeEnd.getTime() - canonicalRangeStart.getTime();
               rangeMinutes /= (1000 * 60);

               if (remainingMinutes > rangeMinutes)
               {
                  remainingMinutes = NumberHelper.truncate(remainingMinutes - rangeMinutes, 2);
               }
               else
               {
                  if (Duration.durationValueEquals(remainingMinutes, rangeMinutes))
                  {
                     endTime = canonicalRangeEnd;
                     if (rangeStartDay.getTime() != rangeEndDay.getTime())
                     {
                        // The range ends the next day, so let's adjust our date accordingly.
                        cal.add(Calendar.DAY_OF_YEAR, 1);
                     }
                  }
                  else
                  {
                     endTime = new Date((long) (canonicalRangeStart.getTime() + (remainingMinutes * (60 * 1000))));
                     returnNextWorkStart = false;
                  }
                  remainingMinutes = 0;
                  break;
               }
            }

            DateHelper.setTime(cal, endTime);
         }
      }

      if (returnNextWorkStart)
      {
         updateToNextWorkStart(cal);
      }

      return cal.getTime();
   }

   /**
    * Given a finish date and a duration, this method calculates backwards to the
    * start date. It takes account of working hours in each day, non working
    * days and calendar exceptions.
    *
    * @param finishDate finish date
    * @param duration duration
    * @return start date
    */
   public Date getStartDate(Date finishDate, Duration duration)
   {
      ProjectProperties properties = getParentFile().getProjectProperties();
      // Note: Using a double allows us to handle date values that are accurate up to seconds.
      //       However, it also means we need to truncate the value to 2 decimals to make the
      //       comparisons work as sometimes the double ends up with some extra e.g. .0000000000003
      //       that wreak havoc on the comparisons.
      double remainingMinutes = NumberHelper.truncate(duration.convertUnits(TimeUnit.MINUTES, properties).getDuration(), 2);
      Calendar cal = Calendar.getInstance();
      cal.setTime(finishDate);
      Calendar startCal = Calendar.getInstance();

      while (remainingMinutes > 0)
      {
         //
         // Get the current date and time and determine how many
         // working hours remain
         //
         Date currentDate = cal.getTime();
         startCal.setTime(currentDate);
         startCal.add(Calendar.DAY_OF_YEAR, -1);
         Date currentDateEnd = DateHelper.getDayEndDate(startCal.getTime());
         double currentDateWorkingMinutes = getWork(currentDateEnd, currentDate, TimeUnit.MINUTES).getDuration();

         //
         // We have more than enough hours left
         //
         if (remainingMinutes > currentDateWorkingMinutes)
         {
            //
            // Deduct this day's hours from our total
            //
            remainingMinutes = NumberHelper.truncate(remainingMinutes - currentDateWorkingMinutes, 2);

            //
            // Move the calendar backward to the previous working day
            //
            int count = 0;
            Day day;
            do
            {
               if (count > 7)
               {
                  break; // Protect against a calendar with all days non-working
               }
               count++;
               cal.add(Calendar.DAY_OF_YEAR, -1);
               day = Day.getInstance(cal.get(Calendar.DAY_OF_WEEK));
            }
            while (!isWorkingDate(cal.getTime(), day));

            if (count > 7)
            {
               // We have a calendar with no working days.
               return null;
            }

            //
            // Retrieve the finish time for this day
            //
            Date finishTime = getFinishTime(cal.getTime());
            DateHelper.setTime(cal, finishTime);
         }
         else
         {
            //
            // We have less hours to allocate than there are working hours
            // in this day. We need to calculate the time of day at which
            // our work starts.
            //
            ProjectCalendarDateRanges ranges = getRanges(cal.getTime(), cal, null);

            //
            // Now we have the range of working hours for this day,
            // step through it to work out the start point
            //
            Date startTime = null;
            Date currentDateFinishTime = DateHelper.getCanonicalTime(currentDate);
            boolean firstRange = true;
            // Traverse from end to start
            for (int i = ranges.getRangeCount() - 1; i >= 0; i--)
            {
               DateRange range = ranges.getRange(i);
               //
               // Skip this range if its start is after our end time
               //
               Date rangeStart = range.getStart();
               Date rangeEnd = range.getEnd();

               if (rangeStart == null || rangeEnd == null)
               {
                  continue;
               }

               Date canonicalRangeEnd = DateHelper.getCanonicalTime(rangeEnd);
               Date canonicalRangeStart = DateHelper.getCanonicalTime(rangeStart);

               Date rangeStartDay = DateHelper.getDayStartDate(rangeStart);
               Date rangeEndDay = DateHelper.getDayStartDate(rangeEnd);

               if (rangeStartDay.getTime() != rangeEndDay.getTime())
               {
                  Calendar calendar = Calendar.getInstance();
                  calendar.setTime(canonicalRangeEnd);
                  calendar.add(Calendar.DAY_OF_YEAR, 1);
                  canonicalRangeEnd = calendar.getTime();
               }

               if (firstRange && canonicalRangeStart.getTime() > currentDateFinishTime.getTime())
               {
                  continue;
               }

               //
               // Move the end of the range if our current end is
               // before the range end
               //
               if (firstRange && canonicalRangeEnd.getTime() > currentDateFinishTime.getTime())
               {
                  canonicalRangeEnd = currentDateFinishTime;
               }
               firstRange = false;

               double rangeMinutes;

               rangeMinutes = canonicalRangeEnd.getTime() - canonicalRangeStart.getTime();
               rangeMinutes /= (1000 * 60);

               if (remainingMinutes > rangeMinutes)
               {
                  remainingMinutes = NumberHelper.truncate(remainingMinutes - rangeMinutes, 2);
               }
               else
               {
                  if (Duration.durationValueEquals(remainingMinutes, rangeMinutes))
                  {
                     startTime = canonicalRangeStart;
                  }
                  else
                  {
                     startTime = new Date((long) (canonicalRangeEnd.getTime() - (remainingMinutes * (60 * 1000))));
                  }
                  remainingMinutes = 0;
                  break;
               }
            }

            DateHelper.setTime(cal, startTime);
         }
      }

      return cal.getTime();
   }

   /**
    * This method finds the start of the next working period.
    *
    * @param cal current Calendar instance
    */
   private void updateToNextWorkStart(Calendar cal)
   {
      Date originalDate = cal.getTime();

      //
      // Find the date ranges for the current day
      //
      ProjectCalendarDateRanges ranges = getRanges(originalDate, cal, null);

      if (ranges != null)
      {
         //
         // Do we have a start time today?
         //
         Date calTime = DateHelper.getCanonicalTime(cal.getTime());
         Date startTime = null;
         for (DateRange range : ranges)
         {
            Date rangeStart = DateHelper.getCanonicalTime(range.getStart());
            Date rangeEnd = DateHelper.getCanonicalTime(range.getEnd());
            Date rangeStartDay = DateHelper.getDayStartDate(range.getStart());
            Date rangeEndDay = DateHelper.getDayStartDate(range.getEnd());

            if (rangeStartDay.getTime() != rangeEndDay.getTime())
            {
               Calendar calendar = Calendar.getInstance();
               calendar.setTime(rangeEnd);
               calendar.add(Calendar.DAY_OF_YEAR, 1);
               rangeEnd = calendar.getTime();
            }

            if (calTime.getTime() < rangeEnd.getTime())
            {
               if (calTime.getTime() > rangeStart.getTime())
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
            Day day;
            int nonWorkingDayCount = 0;
            do
            {
               cal.set(Calendar.DAY_OF_YEAR, cal.get(Calendar.DAY_OF_YEAR) + 1);
               day = Day.getInstance(cal.get(Calendar.DAY_OF_WEEK));
               ++nonWorkingDayCount;
               if (nonWorkingDayCount > MAX_NONWORKING_DAYS)
               {
                  cal.setTime(originalDate);
                  break;
               }
            }
            while (!isWorkingDate(cal.getTime(), day));

            startTime = getStartTime(cal.getTime());
         }

         DateHelper.setTime(cal, startTime);
      }
   }

   /**
    * This method finds the finish of the previous working period.
    *
    * @param cal current Calendar instance
    */
   private void updateToPreviousWorkFinish(Calendar cal)
   {
      Date originalDate = cal.getTime();

      //
      // Find the date ranges for the current day
      //
      ProjectCalendarDateRanges ranges = getRanges(originalDate, cal, null);
      if (ranges != null)
      {
         //
         // Do we have a start time today?
         //
         Date calTime = DateHelper.getCanonicalTime(cal.getTime());
         Date finishTime = null;
         for (DateRange range : ranges)
         {
            Date rangeEnd = DateHelper.getCanonicalTime(range.getEnd());
            Date rangeStartDay = DateHelper.getDayStartDate(range.getStart());
            Date rangeEndDay = DateHelper.getDayStartDate(range.getEnd());

            if (rangeStartDay.getTime() != rangeEndDay.getTime())
            {
               Calendar calendar = Calendar.getInstance();
               calendar.setTime(rangeEnd);
               calendar.add(Calendar.DAY_OF_YEAR, 1);
               rangeEnd = calendar.getTime();
            }

            if (calTime.getTime() >= rangeEnd.getTime())
            {
               finishTime = rangeEnd;
               break;
            }
         }

         //
         // If we don't have a finish time today - find the previous working day
         // then retrieve the finish time.
         //
         if (finishTime == null)
         {
            Day day;
            int nonWorkingDayCount = 0;
            do
            {
               cal.set(Calendar.DAY_OF_YEAR, cal.get(Calendar.DAY_OF_YEAR) - 1);
               day = Day.getInstance(cal.get(Calendar.DAY_OF_WEEK));
               ++nonWorkingDayCount;
               if (nonWorkingDayCount > MAX_NONWORKING_DAYS)
               {
                  cal.setTime(originalDate);
                  break;
               }
            }
            while (!isWorkingDate(cal.getTime(), day));

            finishTime = getFinishTime(cal.getTime());
         }

         DateHelper.setTime(cal, finishTime);
      }
   }

   /**
    * Utility method to retrieve the next working date start time, given
    * a date and time as a starting point.
    *
    * @param date date and time start point
    * @return date and time of next work start
    */
   public Date getNextWorkStart(Date date)
   {
      Calendar cal = Calendar.getInstance();
      cal.setTime(date);
      updateToNextWorkStart(cal);
      return cal.getTime();
   }

   /**
    * Utility method to retrieve the previous working date finish time, given
    * a date and time as a starting point.
    *
    * @param date date and time start point
    * @return date and time of previous work finish
    */
   public Date getPreviousWorkFinish(Date date)
   {
      Calendar cal = Calendar.getInstance();
      cal.setTime(date);
      updateToPreviousWorkFinish(cal);
      return cal.getTime();
   }

   /**
    * This method allows the caller to determine if a given date is a
    * working day. This method takes account of calendar exceptions.
    *
    * @param date Date to be tested
    * @return boolean value
    */
   public boolean isWorkingDate(Date date)
   {
      Calendar cal = Calendar.getInstance();
      cal.setTime(date);
      Day day = Day.getInstance(cal.get(Calendar.DAY_OF_WEEK));
      return (isWorkingDate(date, day));
   }

   /**
    * This private method allows the caller to determine if a given date is a
    * working day. This method takes account of calendar exceptions. It assumes
    * that the caller has already calculated the day of the week on which
    * the given day falls.
    *
    * @param date Date to be tested
    * @param day Day of the week for the date under test
    * @return boolean flag
    */
   private boolean isWorkingDate(Date date, Day day)
   {
      ProjectCalendarDateRanges ranges = getRanges(date, null, day);
      return ranges.getRangeCount() != 0;
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
   private int getDaysInRange(Date startDate, Date endDate)
   {
      int result;
      Calendar cal = Calendar.getInstance();
      cal.setTime(endDate);
      int endDateYear = cal.get(Calendar.YEAR);
      int endDateDayOfYear = cal.get(Calendar.DAY_OF_YEAR);

      cal.setTime(startDate);

      if (endDateYear == cal.get(Calendar.YEAR))
      {
         result = (endDateDayOfYear - cal.get(Calendar.DAY_OF_YEAR)) + 1;
      }
      else
      {
         result = 0;
         do
         {
            result += (cal.getActualMaximum(Calendar.DAY_OF_YEAR) - cal.get(Calendar.DAY_OF_YEAR)) + 1;
            cal.roll(Calendar.YEAR, 1);
            cal.set(Calendar.DAY_OF_YEAR, 1);
         }
         while (cal.get(Calendar.YEAR) < endDateYear);
         result += endDateDayOfYear;
      }

      return (result);
   }

   /**
    * Modifier method to set the unique ID of this calendar.
    *
    * @param uniqueID unique identifier
    */
   @Override public void setUniqueID(Integer uniqueID)
   {
      ProjectFile parent = getParentFile();

      if (m_uniqueID != null)
      {
         parent.getCalendars().unmapUniqueID(m_uniqueID);
      }

      parent.getCalendars().mapUniqueID(uniqueID, this);

      m_uniqueID = uniqueID;
   }

   /**
    * Accessor method to retrieve the unique ID of this calendar.
    *
    * @return calendar unique identifier
    */
   @Override public Integer getUniqueID()
   {
      return (m_uniqueID);
   }

   /**
    * Retrieve the resource to which this calendar is linked.
    *
    * @return resource instance
    */
   public Resource getResource()
   {
      return (m_resource);
   }

   /**
    * Sets the resource to which this calendar is linked. Note that this
    * method updates the calendar's name to be the same as the resource name.
    * If the resource does not yet have a name, then the calendar is given
    * a default name.
    *
    * @param resource resource instance
    */
   public void setResource(Resource resource)
   {
      m_resource = resource;
      String name = m_resource.getName();
      if (name == null || name.length() == 0)
      {
         name = "Unnamed Resource";
      }
      setName(name);
   }

   /**
    * Removes this calendar from the project.
    */
   public void remove()
   {
      getParentFile().removeCalendar(this);
   }

   /**
    * Retrieve a calendar calendar exception which applies to this date.
    *
    * @param date target date
    * @return calendar exception, or null if none match this date
    */
   public ProjectCalendarException getException(Date date)
   {
      ProjectCalendarException exception = null;
      if (!m_exceptions.isEmpty())
      {
         if (!m_exceptionsSorted)
         {
            Collections.sort(m_exceptions);
            m_exceptionsSorted = true;
         }

         int low = 0;
         int high = m_exceptions.size() - 1;
         long targetDate = date.getTime();

         while (low <= high)
         {
            int mid = (low + high) >>> 1;
            ProjectCalendarException midVal = m_exceptions.get(mid);
            int cmp = 0 - DateHelper.compare(midVal.getFromDate(), midVal.getToDate(), targetDate);

            if (cmp < 0)
            {
               low = mid + 1;
            }
            else
            {
               if (cmp > 0)
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

      if (exception == null && getParent() != null)
      {
         // Check base calendar as well for an exception.
         exception = getParent().getException(date);
      }
      return (exception);
   }

   /**
    * Retrieves the amount of work on a given day, and
    * returns it in the specified format.
    *
    * @param date target date
    * @param format required format
    * @return work duration
    */
   public Duration getWork(Date date, TimeUnit format)
   {
      ProjectCalendarDateRanges ranges = getRanges(date, null, null);
      long time = getTotalTime(ranges);
      return convertFormat(time, format);
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
   public Duration getWork(Date startDate, Date endDate, TimeUnit format)
   {
      DateRange range = new DateRange(startDate, endDate);
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
         if (startDate.getTime() > endDate.getTime())
         {
            invert = true;
            Date temp = startDate;
            startDate = endDate;
            endDate = temp;
         }

         Date canonicalStartDate = DateHelper.getDayStartDate(startDate);
         Date canonicalEndDate = DateHelper.getDayStartDate(endDate);

         if (canonicalStartDate.getTime() == canonicalEndDate.getTime())
         {
            ProjectCalendarDateRanges ranges = getRanges(startDate, null, null);
            if (ranges.getRangeCount() != 0)
            {
               totalTime = getTotalTime(ranges, startDate, endDate);
            }
         }
         else
         {
            //
            // Find the first working day in the range
            //
            Date currentDate = startDate;
            Calendar cal = Calendar.getInstance();
            cal.setTime(startDate);
            Day day = Day.getInstance(cal.get(Calendar.DAY_OF_WEEK));
            while (isWorkingDate(currentDate, day) == false && currentDate.getTime() < canonicalEndDate.getTime())
            {
               cal.add(Calendar.DAY_OF_YEAR, 1);
               currentDate = cal.getTime();
               day = day.getNextDay();
            }

            if (currentDate.getTime() < canonicalEndDate.getTime())
            {
               //
               // Calculate the amount of working time for this day
               //
               totalTime += getTotalTime(getRanges(currentDate, null, day), currentDate, true);

               //
               // Process each working day until we reach the last day
               //
               while (true)
               {
                  cal.add(Calendar.DAY_OF_YEAR, 1);
                  currentDate = cal.getTime();
                  day = day.getNextDay();

                  //
                  // We have reached the last day
                  //
                  if (currentDate.getTime() >= canonicalEndDate.getTime())
                  {
                     break;
                  }

                  //
                  // Skip this day if it has no working time
                  //
                  ProjectCalendarDateRanges ranges = getRanges(currentDate, null, day);
                  if (ranges.getRangeCount() == 0)
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
            ProjectCalendarDateRanges ranges = getRanges(endDate, null, day);
            if (ranges.getRangeCount() != 0)
            {
               totalTime += getTotalTime(ranges, DateHelper.getDayStartDate(endDate), endDate);
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
            double minutesPerDay = getMinutesPerDay();
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
            double minutesPerWeek = getMinutesPerWeek();
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
            double minutesPerDay = getMinutesPerDay();
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
            duration /= (30 * 24 * 60 * 60 * 1000);
            break;
         }

         default:
         {
            throw new IllegalArgumentException("TimeUnit " + format + " not supported");
         }
      }

      return (Duration.getInstance(duration, format));
   }

   /**
    * Retrieves the amount of time represented by a calendar exception
    * before or after an intersection point.
    *
    * @param exception calendar exception
    * @param date intersection time
    * @param after true to report time after intersection, false to report time before
    * @return length of time in milliseconds
    */
   private long getTotalTime(ProjectCalendarDateRanges exception, Date date, boolean after)
   {
      long currentTime = DateHelper.getCanonicalTime(date).getTime();
      long total = 0;
      for (DateRange range : exception)
      {
         total += getTime(range.getStart(), range.getEnd(), currentTime, after);
      }
      return (total);
   }

   /**
    * Retrieves the amount of working time represented by
    * a calendar exception.
    *
    * @param exception calendar exception
    * @return length of time in milliseconds
    */
   private long getTotalTime(ProjectCalendarDateRanges exception)
   {
      long total = 0;
      for (DateRange range : exception)
      {
         total += getTime(range.getStart(), range.getEnd());
      }
      return (total);
   }

   /**
    * This method calculates the total amount of working time in a single
    * day, which intersects with the supplied time range.
    *
    * @param hours collection of working hours in a day
    * @param startDate time range start
    * @param endDate time range end
    * @return length of time in milliseconds
    */
   private long getTotalTime(ProjectCalendarDateRanges hours, Date startDate, Date endDate)
   {
      long total = 0;
      if (startDate.getTime() != endDate.getTime())
      {
         Date start = DateHelper.getCanonicalTime(startDate);
         Date end = DateHelper.getCanonicalTime(endDate);

         for (DateRange range : hours)
         {
            Date rangeStart = range.getStart();
            Date rangeEnd = range.getEnd();
            if (rangeStart != null && rangeEnd != null)
            {
               Date canoncialRangeStart = DateHelper.getCanonicalTime(rangeStart);
               Date canonicalRangeEnd = DateHelper.getCanonicalTime(rangeEnd);

               Date startDay = DateHelper.getDayStartDate(rangeStart);
               Date finishDay = DateHelper.getDayStartDate(rangeEnd);

               //
               // Handle the case where the end of the range is at midnight -
               // this will show up as the start and end days not matching
               //
               if (startDay.getTime() != finishDay.getTime())
               {
                  Calendar calendar = Calendar.getInstance();
                  calendar.setTime(canonicalRangeEnd);
                  calendar.add(Calendar.DAY_OF_YEAR, 1);
                  canonicalRangeEnd = calendar.getTime();
               }

               if (canoncialRangeStart.getTime() == canonicalRangeEnd.getTime() && rangeEnd.getTime() > rangeStart.getTime())
               {
                  total += (24 * 60 * 60 * 1000);
               }
               else
               {
                  total += getTime(start, end, canoncialRangeStart, canonicalRangeEnd);
               }
            }
         }
      }

      return (total);
   }

   /**
    * Calculates how much of a time range is before or after a
    * target intersection point.
    *
    * @param start time range start
    * @param end time range end
    * @param target target intersection point
    * @param after true if time after target required, false for time before
    * @return length of time in milliseconds
    */
   private long getTime(Date start, Date end, long target, boolean after)
   {
      long total = 0;
      if (start != null && end != null)
      {
         Date startTime = DateHelper.getCanonicalTime(start);
         Date endTime = DateHelper.getCanonicalTime(end);

         Date startDay = DateHelper.getDayStartDate(start);
         Date finishDay = DateHelper.getDayStartDate(end);

         //
         // Handle the case where the end of the range is at midnight -
         // this will show up as the start and end days not matching
         //
         if (startDay.getTime() != finishDay.getTime())
         {
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(endTime);
            calendar.add(Calendar.DAY_OF_YEAR, 1);
            endTime = calendar.getTime();
         }

         int diff = DateHelper.compare(startTime, endTime, target);
         if (diff == 0)
         {
            if (after == true)
            {
               total = (endTime.getTime() - target);
            }
            else
            {
               total = (target - startTime.getTime());
            }
         }
         else
         {
            if ((after == true && diff < 0) || (after == false && diff > 0))
            {
               total = (endTime.getTime() - startTime.getTime());
            }
         }
      }
      return (total);
   }

   /**
    * Retrieves the amount of time between two date time values. Note that
    * these values are converted into canonical values to remove the
    * date component.
    *
    * @param start start time
    * @param end end time
    * @return length of time
    */
   private long getTime(Date start, Date end)
   {
      long total = 0;
      if (start != null && end != null)
      {
         Date startTime = DateHelper.getCanonicalTime(start);
         Date endTime = DateHelper.getCanonicalTime(end);

         Date startDay = DateHelper.getDayStartDate(start);
         Date finishDay = DateHelper.getDayStartDate(end);

         //
         // Handle the case where the end of the range is at midnight -
         // this will show up as the start and end days not matching
         //
         if (startDay.getTime() != finishDay.getTime())
         {
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(endTime);
            calendar.add(Calendar.DAY_OF_YEAR, 1);
            endTime = calendar.getTime();
         }

         total = (endTime.getTime() - startTime.getTime());
      }
      return (total);
   }

   /**
    * This method returns the length of overlapping time between two time
    * ranges.
    *
    * @param start1 start of first range
    * @param end1 end of first range
    * @param start2 start start of second range
    * @param end2 end of second range
    * @return overlapping time in milliseconds
    */
   private long getTime(Date start1, Date end1, Date start2, Date end2)
   {
      long total = 0;

      if (start1 != null && end1 != null && start2 != null && end2 != null)
      {
         long start;
         long end;

         if (start1.getTime() < start2.getTime())
         {
            start = start2.getTime();
         }
         else
         {
            start = start1.getTime();
         }

         if (end1.getTime() < end2.getTime())
         {
            end = end1.getTime();
         }
         else
         {
            end = end2.getTime();
         }

         if (start < end)
         {
            total = end - start;
         }
      }

      return (total);
   }

   /**
    * Add a reference to a calendar derived from this one.
    *
    * @param calendar derived calendar instance
    */
   protected void addDerivedCalendar(ProjectCalendar calendar)
   {
      m_derivedCalendars.add(calendar);
   }

   /**
    * Remove a reference to a derived calendar.
    *
    * @param calendar derived calendar instance
    */
   protected void removeDerivedCalendar(ProjectCalendar calendar)
   {
      m_derivedCalendars.remove(calendar);
   }

   /**
    * Retrieve a list of derived calendars.
    *
    * @return list of derived calendars
    */
   public List<ProjectCalendar> getDerivedCalendars()
   {
      return (m_derivedCalendars);
   }

   /**
    * {@inheritDoc}
    */
   @Override public String toString()
   {
      ByteArrayOutputStream os = new ByteArrayOutputStream();
      PrintWriter pw = new PrintWriter(os);
      pw.println("[ProjectCalendar");
      pw.println("   ID=" + m_uniqueID);
      pw.println("   name=" + getName());
      pw.println("   baseCalendarName=" + (getParent() == null ? "" : getParent().getName()));
      pw.println("   resource=" + (m_resource == null ? "" : m_resource.getName()));

      String[] dayName =
      {
         "Sunday",
         "Monday",
         "Tuesday",
         "Wednesday",
         "Thursday",
         "Friday",
         "Saturday"
      };

      for (int loop = 0; loop < 7; loop++)
      {
         pw.println("   [Day " + dayName[loop]);
         pw.println("      type=" + getDays()[loop]);
         pw.println("      hours=" + getHours()[loop]);
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

      pw.println("]");
      pw.flush();
      return (os.toString());
   }

   /**
    * Create a calendar based on the intersection of a task calendar and a resource calendar.
    *
    * @param file the parent file to which this record belongs.
    * @param taskCalendar task calendar to merge
    * @param resourceCalendar resource calendar to merge
    */
   public ProjectCalendar(ProjectFile file, ProjectCalendar taskCalendar, ProjectCalendar resourceCalendar)
   {
      m_projectFile = file;

      // Set the resource
      setResource(resourceCalendar.getResource());

      // Merge the exceptions

      // Merge the hours
      for (int i = 1; i <= 7; i++)
      {
         Day day = Day.getInstance(i);

         // Set working/non-working days
         setWorkingDay(day, taskCalendar.isWorkingDay(day) && resourceCalendar.isWorkingDay(day));

         ProjectCalendarHours hours = addCalendarHours(day);

         int taskIndex = 0;
         int resourceIndex = 0;

         ProjectCalendarHours taskHours = taskCalendar.getHours(day);
         ProjectCalendarHours resourceHours = resourceCalendar.getHours(day);

         DateRange range1 = null;
         DateRange range2 = null;

         Date start = null;
         Date end = null;

         Date start1 = null;
         Date start2 = null;
         Date end1 = null;
         Date end2 = null;
         while (true)
         {
            // Find next range start
            if (taskHours.getRangeCount() > taskIndex)
            {
               range1 = taskHours.getRange(taskIndex);
            }
            else
            {
               break;
            }

            if (resourceHours.getRangeCount() > resourceIndex)
            {
               range2 = resourceHours.getRange(resourceIndex);
            }
            else
            {
               break;
            }

            start1 = range1.getStart();
            start2 = range2.getStart();
            end1 = range1.getEnd();
            end2 = range2.getEnd();

            // Get the later start
            if (start1.compareTo(start2) > 0)
            {
               start = start1;
            }
            else
            {
               start = start2;
            }

            // Get the earlier end
            if (end1.compareTo(end2) < 0)
            {
               end = end1;
               taskIndex++;
            }
            else
            {
               end = end2;
               resourceIndex++;
            }

            if (end != null && end.compareTo(start) > 0)
            {
               // Found a block
               hours.addRange(new DateRange(start, end));
            }
         }
      }
      // For now just combine the exceptions. Probably overkill (although would be more accurate) to also merge the exceptions.
      m_exceptions.addAll(taskCalendar.getCalendarExceptions());
      m_exceptions.addAll(resourceCalendar.getCalendarExceptions());
      m_exceptionsSorted = false;
   }

   /**
    * Copy the settings from another calendar to this calendar.
    *
    * @param cal calendar data source
    */
   public void copy(ProjectCalendar cal)
   {
      setName(cal.getName());
      setParent(cal.getParent());
      System.arraycopy(cal.getDays(), 0, getDays(), 0, getDays().length);
      for (ProjectCalendarException ex : cal.m_exceptions)
      {
         addCalendarException(ex.getFromDate(), ex.getToDate());
         for (DateRange range : ex)
         {
            ex.addRange(new DateRange(range.getStart(), range.getEnd()));
         }
      }

      for (ProjectCalendarHours hours : getHours())
      {
         if (hours != null)
         {
            ProjectCalendarHours copyHours = cal.addCalendarHours(hours.getDay());
            for (DateRange range : hours)
            {
               copyHours.addRange(new DateRange(range.getStart(), range.getEnd()));
            }
         }
      }
   }

   /**
    * Utility method to clear cached calendar data.
    */
   private void clearWorkingDateCache()
   {
      m_workingDateCache.clear();
      m_startTimeCache.clear();
      for (ProjectCalendar calendar : m_derivedCalendars)
      {
         calendar.clearWorkingDateCache();
      }
   }

   /**
    * Retrieves the working hours on the given date.
    *
    * @param date required date
    * @param cal optional calendar instance
    * @param day optional day instance
    * @return working hours
    */
   private ProjectCalendarDateRanges getRanges(Date date, Calendar cal, Day day)
   {
      ProjectCalendarDateRanges ranges = getException(date);
      if (ranges == null)
      {
         if (day == null)
         {
            if (cal == null)
            {
               cal = Calendar.getInstance();
               cal.setTime(date);
            }
            day = Day.getInstance(cal.get(Calendar.DAY_OF_WEEK));
         }
         ranges = getHours(day);
      }
      return ranges;
   }

   /**
    * Accessor method allowing retrieval of ProjectFile reference.
    *
    * @return reference to this the parent ProjectFile instance
    */
   public final ProjectFile getParentFile()
   {
      return (m_projectFile);
   }

   /**
    * Reference to parent ProjectFile.
    */
   private ProjectFile m_projectFile;

   /**
    * Unique identifier of this calendar.
    */
   private Integer m_uniqueID = Integer.valueOf(0);

   /**
    * List of exceptions to the base calendar.
    */
   private List<ProjectCalendarException> m_exceptions = new LinkedList<ProjectCalendarException>();

   /**
    * Flag indicating if the list of exceptions is sorted.
    */
   private boolean m_exceptionsSorted;

   /**
    * This resource to which this calendar is attached.
    */
   private Resource m_resource;

   /**
    * List of calendars derived from this calendar instance.
    */
   private ArrayList<ProjectCalendar> m_derivedCalendars = new ArrayList<ProjectCalendar>();

   /**
    * Caches used to speed up date calculations.
    */
   private Map<DateRange, Long> m_workingDateCache = new WeakHashMap<DateRange, Long>();
   private Map<Date, Date> m_startTimeCache = new WeakHashMap<Date, Date>();

   /**
    * Work week definitions.
    */
   private ArrayList<ProjectCalendarWeek> m_workWeeks = new ArrayList<ProjectCalendarWeek>();

   private Integer m_minutesPerDay;
   private Integer m_minutesPerWeek;
   private Integer m_minutesPerMonth;
   private Integer m_minutesPerYear;

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
}
