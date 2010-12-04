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

import net.sf.mpxj.utility.DateUtility;
import net.sf.mpxj.utility.NumberUtility;

/**
 * This class represents the a Calendar Definition record. Both base calendars
 * and calendars derived from base calendars are represented by instances
 * of this class. The class is used to define the working and non-working days
 * of the week. The default calendar defines Monday to Friday as working days.
 */
public final class ProjectCalendar extends ProjectEntity
{
   /**
    * Default constructor.
    *
    * @param file the parent file to which this record belongs.
    */
   ProjectCalendar(ProjectFile file)
   {
      super(file);

      if (file.getAutoCalendarUniqueID() == true)
      {
         setUniqueID(Integer.valueOf(file.getCalendarUniqueID()));
      }
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
   public ProjectCalendarHours addCalendarHours(Day day)
   {
      ProjectCalendarHours bch = new ProjectCalendarHours(this);
      bch.setDay(day);
      m_hours[day.getValue() - 1] = bch;
      clearWorkingDateCache();
      return (bch);
   }

   /**
    * Adds a set of hours to this calendar without assigning them to
    * a particular day.
    *
    * @return calendar hours instance
    */
   public ProjectCalendarHours addCalendarHours()
   {
      return (new ProjectCalendarHours(this));
   }

   /**
    * Attaches a pre-existing set of hours to the correct
    * day within the calendar.
    *
    * @param hours calendar hours instance
    */
   public void attachHoursToDay(ProjectCalendarHours hours)
   {
      if (hours.getParentCalendar() != this)
      {
         throw new IllegalArgumentException();
      }
      m_hours[hours.getDay().getValue() - 1] = hours;
      clearWorkingDateCache();
   }

   /**
    * Removes a set of calendar hours from the day to which they
    * are currently attached.
    *
    * @param hours calendar hours instance
    */
   public void removeHoursFromDay(ProjectCalendarHours hours)
   {
      if (hours.getParentCalendar() != this)
      {
         throw new IllegalArgumentException();
      }
      m_hours[hours.getDay().getValue() - 1] = null;
      clearWorkingDateCache();
   }

   /**
    * This method retrieves the calendar hours for the specified day.
    * Note that this method only returns the hours specification for the
    * current calendar.If this is a derived calendar, it does not refer to 
    * the base calendar. 
    *
    * @param day Day instance
    * @return calendar hours
    */
   public ProjectCalendarHours getCalendarHours(Day day)
   {
      return (m_hours[day.getValue() - 1]);
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
   public ProjectCalendarHours getHours(Day day)
   {
      ProjectCalendarHours result = getCalendarHours(day);
      if (result == null)
      {
         //
         // If this is a base calendar and we have no hours, then we
         // have a problem - so we add the default hours and try again
         //
         if (m_baseCalendar == null)
         {
            // Only add default hours for the day that is 'missing' to avoid overwriting real calendar hours
            addDefaultCalendarHours(day);
            result = getCalendarHours(day);
         }
         else
         {
            result = m_baseCalendar.getHours(day);
         }
      }
      return result;
   }

   /**
    * Retrieve an array representing all of the calendar hours defined
    * by this calendar.
    *
    * @return array of calendar hours
    */
   public ProjectCalendarHours[] getHours()
   {
      return (m_hours);
   }

   /**
    * Calendar name.
    *
    * @param name calendar name
    */
   public void setName(String name)
   {
      m_name = name;
   }

   /**
    * Calendar name.
    *
    * @return calendar name
    */
   public String getName()
   {
      return (m_name);
   }

   /**
    * Sets the ProjectCalendar instance from which this calendar is derived.
    *
    * @param calendar base calendar instance
    */
   public void setBaseCalendar(ProjectCalendar calendar)
   {
      if (m_baseCalendar != null)
      {
         m_baseCalendar.removeDerivedCalendar(this);
      }

      m_baseCalendar = calendar;

      if (m_baseCalendar != null)
      {
         m_baseCalendar.addDerivedCalendar(this);
      }
      clearWorkingDateCache();
   }

   /**
    * Retrieve the ProjectCalendar instance from which this calendar is derived.
    *
    * @return ProjectCalendar instance
    */
   public ProjectCalendar getBaseCalendar()
   {
      return (m_baseCalendar);
   }

   /**
    * Method indicating whether a day is a working or non-working day.
    *
    * @param day required day
    * @return true if this is a working day
    */
   public boolean isWorkingDay(Day day)
   {
      DayType value = m_days[day.getValue() - 1];
      boolean result;

      if (value == DayType.DEFAULT)
      {
         ProjectCalendar cal = getBaseCalendar();
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
    * Retrieve an array representing the days of the week for this calendar.
    *
    * @return array of days of the week
    */
   public DayType[] getDays()
   {
      return (m_days);
   }

   /**
    * This method allows the retrieval of the actual working day flag,
    * which can take the values DEFAULT, WORKING, or NONWORKING. This differs
    * from the isWorkingDay method as it retrieves the actual flag value.
    * The isWorkingDay method will always refer back to the base calendar
    * to get a boolean value if the underlying flag value is DEFAULT. If
    * isWorkingDay were the only method available to access this flag,
    * it would not be possible to determine that a resource calendar
    * had one or more flags set to DEFAULT.
    *
    * @param day required day
    * @return value of underlying working day flag
    */
   public DayType getWorkingDay(Day day)
   {
      return (m_days[day.getValue() - 1]);
   }

   /**
    * convenience method for setting working or non-working days.
    *
    * @param day required day
    * @param working flag indicating if the day is a working day
    */
   public void setWorkingDay(Day day, boolean working)
   {
      setWorkingDay(day, (working == true ? DayType.WORKING : DayType.NON_WORKING));
   }

   /**
    * This is a convenience method provided to allow a day to be set
    * as working or non-working, by using the day number to
    * identify the required day.
    *
    * @param day required day
    * @param working flag indicating if the day is a working day
    */
   public void setWorkingDay(Day day, DayType working)
   {
      DayType value;

      if (working == null)
      {
         if (isBaseCalendar() == false)
         {
            value = DayType.DEFAULT;
         }
         else
         {
            value = DayType.WORKING;
         }
      }
      else
      {
         value = working;
      }

      m_days[day.getValue() - 1] = value;
   }

   /**
    * This is a convenience method used to add a default set of calendar
    * hours to a calendar.
    */
   public void addDefaultCalendarHours()
   {
      for (int i = 1; i <= 7; i++)
      {
         addDefaultCalendarHours(Day.getInstance(i));
      }
   }

   /**
    * This is a convenience method used to add a default set of calendar
    * hours to a calendar.
    * 
    * @param day Day for which to add default hours for
    */
   public void addDefaultCalendarHours(Day day)
   {
      ProjectCalendarHours hours = addCalendarHours(day);

      if (day != Day.SATURDAY && day != Day.SUNDAY)
      {
         hours.addRange(new DateRange(DEFAULT_START1, DEFAULT_END1));
         hours.addRange(new DateRange(DEFAULT_START2, DEFAULT_END2));
      }
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
      int dayIndex = cal.get(Calendar.DAY_OF_WEEK);
      int days = getDaysInRange(startDate, endDate);
      int duration = 0;

      while (days > 0)
      {
         if (isWorkingDate(cal.getTime(), Day.getInstance(dayIndex)) == true)
         {
            ++duration;
         }

         --days;

         ++dayIndex;
         if (dayIndex > 7)
         {
            dayIndex = 1;
         }

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
         ProjectCalendarDateRanges ranges = getException(date);
         if (ranges == null)
         {
            Calendar cal = Calendar.getInstance();
            cal.setTime(date);
            Day day = Day.getInstance(cal.get(Calendar.DAY_OF_WEEK));
            ranges = getHours(day);
         }

         if (ranges == null)
         {
            result = getParentFile().getProjectHeader().getDefaultStartTime();
         }
         else
         {
            result = ranges.getRange(0).getStart();
         }
         result = DateUtility.getCanonicalTime(result);
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
         ProjectCalendarDateRanges ranges = getException(date);
         if (ranges == null)
         {
            Calendar cal = Calendar.getInstance();
            cal.setTime(date);
            Day day = Day.getInstance(cal.get(Calendar.DAY_OF_WEEK));
            ranges = getHours(day);
         }

         if (ranges == null)
         {
            result = getParentFile().getProjectHeader().getDefaultEndTime();
            result = DateUtility.getCanonicalTime(result);
         }
         else
         {
            Date rangeStart = result = ranges.getRange(0).getStart();
            Date rangeFinish = ranges.getRange(ranges.getRangeCount() - 1).getEnd();
            Date startDay = DateUtility.getDayStartDate(rangeStart);
            Date finishDay = DateUtility.getDayStartDate(rangeFinish);

            result = DateUtility.getCanonicalTime(rangeFinish);

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
      ProjectHeader header = getParentFile().getProjectHeader();
      // Note: Using a double allows us to handle date values that are accurate up to seconds.
      //       However, it also means we need to truncate the value to 2 decimals to make the
      //       comparisons work as sometimes the double ends up with some extra e.g. .0000000000003
      //       that wreak havoc on the comparisons.
      double remainingMinutes = NumberUtility.truncate(duration.convertUnits(TimeUnit.MINUTES, header).getDuration(), 2);
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
         Date currentDateEnd = DateUtility.getDayStartDate(endCal.getTime());
         //Date currentDateEnd = DateUtility.getDayEndDate(currentDate); XXX
         double currentDateWorkingMinutes = getWork(currentDate, currentDateEnd, TimeUnit.MINUTES).getDuration();

         //
         // We have more than enough hours left
         //
         if (remainingMinutes > currentDateWorkingMinutes)
         {
            //
            // Deduct this day's hours from our total
            //
            remainingMinutes = NumberUtility.truncate(remainingMinutes - currentDateWorkingMinutes, 2);

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
            DateUtility.setTime(cal, startTime);
         }
         else
         {
            //
            // We have less hours to allocate than there are working hours
            // in this day. We need to calculate the time of day at which 
            // our work ends.
            //
            ProjectCalendarDateRanges ranges = getException(cal.getTime());
            if (ranges == null)
            {
               Day day = Day.getInstance(cal.get(Calendar.DAY_OF_WEEK));
               ranges = getHours(day);
            }

            //
            // Now we have the range of working hours for this day,
            // step through it to work out the end point
            //
            Date endTime = null;
            Date currentDateStartTime = DateUtility.getCanonicalTime(currentDate);
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

               Date canonicalRangeEnd = DateUtility.getCanonicalTime(rangeEnd);
               Date canonicalRangeStart = DateUtility.getCanonicalTime(rangeStart);

               Date rangeStartDay = DateUtility.getDayStartDate(rangeStart);
               Date rangeEndDay = DateUtility.getDayStartDate(rangeEnd);

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
                  remainingMinutes = NumberUtility.truncate(remainingMinutes - rangeMinutes, 2);
               }
               else
               {
                  if (remainingMinutes == rangeMinutes)
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

            DateUtility.setTime(cal, endTime);
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
      ProjectHeader header = getParentFile().getProjectHeader();
      // Note: Using a double allows us to handle date values that are accurate up to seconds.
      //       However, it also means we need to truncate the value to 2 decimals to make the
      //       comparisons work as sometimes the double ends up with some extra e.g. .0000000000003
      //       that wreak havoc on the comparisons.
      double remainingMinutes = NumberUtility.truncate(duration.convertUnits(TimeUnit.MINUTES, header).getDuration(), 2);
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
         Date currentDateEnd = DateUtility.getDayEndDate(startCal.getTime());
         double currentDateWorkingMinutes = getWork(currentDateEnd, currentDate, TimeUnit.MINUTES).getDuration();

         //
         // We have more than enough hours left
         //
         if (remainingMinutes > currentDateWorkingMinutes)
         {
            //
            // Deduct this day's hours from our total
            //
            remainingMinutes = NumberUtility.truncate(remainingMinutes - currentDateWorkingMinutes, 2);

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
            DateUtility.setTime(cal, finishTime);
         }
         else
         {
            //
            // We have less hours to allocate than there are working hours
            // in this day. We need to calculate the time of day at which 
            // our work starts.
            //
            ProjectCalendarDateRanges ranges = getException(cal.getTime());
            if (ranges == null)
            {
               Day day = Day.getInstance(cal.get(Calendar.DAY_OF_WEEK));
               ranges = getHours(day);
            }

            //
            // Now we have the range of working hours for this day,
            // step through it to work out the start point
            //
            Date startTime = null;
            Date currentDateFinishTime = DateUtility.getCanonicalTime(currentDate);
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

               Date canonicalRangeEnd = DateUtility.getCanonicalTime(rangeEnd);
               Date canonicalRangeStart = DateUtility.getCanonicalTime(rangeStart);

               Date rangeStartDay = DateUtility.getDayStartDate(rangeStart);
               Date rangeEndDay = DateUtility.getDayStartDate(rangeEnd);

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
                  remainingMinutes = NumberUtility.truncate(remainingMinutes - rangeMinutes, 2);
               }
               else
               {
                  if (remainingMinutes == rangeMinutes)
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

            DateUtility.setTime(cal, startTime);
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
      ProjectCalendarDateRanges ranges = getException(cal.getTime());
      if (ranges == null)
      {
         Day day = Day.getInstance(cal.get(Calendar.DAY_OF_WEEK));
         ranges = getHours(day);
      }

      if (ranges != null)
      {
         //
         // Do we have a start time today?
         // 
         Date calTime = DateUtility.getCanonicalTime(cal.getTime());
         Date startTime = null;
         for (DateRange range : ranges)
         {
            Date rangeStart = DateUtility.getCanonicalTime(range.getStart());
            Date rangeEnd = DateUtility.getCanonicalTime(range.getEnd());
            Date rangeStartDay = DateUtility.getDayStartDate(range.getStart());
            Date rangeEndDay = DateUtility.getDayStartDate(range.getEnd());

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

         DateUtility.setTime(cal, startTime);
      }
   }

   /**
    * Utility method to retrieve the next working date time, given
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
      boolean result;
      ProjectCalendarException exception = getException(date);

      if (exception != null)
      {
         result = exception.getWorking();
      }
      else
      {
         result = isWorkingDay(day);
      }

      return result;
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
    * This method returns a flag indicating if this ProjectCalendar instance
    * represents a base calendar.
    *
    * @return boolean flag
    */
   public boolean isBaseCalendar()
   {
      return (m_baseCalendar == null);
   }

   /**
    * Modifier method to set the unique ID of this calendar.
    *
    * @param uniqueID unique identifier
    */
   public void setUniqueID(Integer uniqueID)
   {
      ProjectFile parent = getParentFile();

      if (m_uniqueID != null)
      {
         parent.unmapTaskUniqueID(m_uniqueID);
      }

      parent.mapCalendarUniqueID(uniqueID, this);

      m_uniqueID = uniqueID;
   }

   /**
    * Accessor method to retrieve the unique ID of this calendar.
    *
    * @return calendar unique identifier
    */
   public Integer getUniqueID()
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
      m_name = m_resource.getName();
      if (m_name == null || m_name.length() == 0)
      {
         m_name = "Unnamed Resource";
      }
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
            int cmp = 0 - DateUtility.compare(midVal.getFromDate(), midVal.getToDate(), targetDate);

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

      if (exception == null && m_baseCalendar != null)
      {
         // Check base calendar as well for an exception.
         exception = m_baseCalendar.getException(date);
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
      ProjectCalendarDateRanges ranges = getException(date);
      if (ranges == null)
      {
         Calendar cal = Calendar.getInstance();
         cal.setTime(date);
         Day day = Day.getInstance(cal.get(Calendar.DAY_OF_WEEK));
         ranges = getHours(day);
      }

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

         Date canonicalStartDate = DateUtility.getDayStartDate(startDate);
         Date canonicalEndDate = DateUtility.getDayStartDate(endDate);

         if (canonicalStartDate.getTime() == canonicalEndDate.getTime())
         {
            Calendar cal = Calendar.getInstance();
            cal.setTime(startDate);
            Day day = Day.getInstance(cal.get(Calendar.DAY_OF_WEEK));

            if (isWorkingDate(startDate, day) == true)
            {
               ProjectCalendarDateRanges ranges = getException(startDate);
               if (ranges == null)
               {
                  ranges = getHours(day);
               }

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
               ProjectCalendarException exception = getException(currentDate);
               if (exception == null)
               {
                  totalTime += getTotalTime(getHours(day), currentDate, true);
               }
               else
               {
                  totalTime += getTotalTime(exception, currentDate, true);
               }

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
                  if (isWorkingDate(currentDate, day) == false)
                  {
                     continue;
                  }

                  //
                  // Add the working time for the whole day
                  //
                  exception = getException(currentDate);
                  if (exception == null)
                  {
                     totalTime += getTotalTime(getHours(day));
                  }
                  else
                  {
                     totalTime += getTotalTime(exception);
                  }
               }
            }

            //
            // We are now at the last day
            //
            if (isWorkingDate(endDate, day) == true)
            {
               ProjectCalendarDateRanges ranges = getException(endDate);
               if (ranges == null)
               {
                  ranges = getHours(day);
               }
               totalTime += getTotalTime(ranges, DateUtility.getDayStartDate(endDate), endDate);
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
      double duration = 0;
      double minutesPerDay = getParentFile().getProjectHeader().getMinutesPerDay().doubleValue();
      double minutesPerWeek = getParentFile().getProjectHeader().getMinutesPerWeek().doubleValue();
      double daysPerMonth = getParentFile().getProjectHeader().getDaysPerMonth().doubleValue();
      switch (format)
      {
         case MINUTES :
         {
            duration = totalTime;
            duration /= (60 * 1000);
            break;
         }

         case HOURS :
         {
            duration = totalTime;
            duration /= (60 * 60 * 1000);
            break;
         }

         case DAYS :
         {
            duration = totalTime;
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

         case WEEKS :
         {
            duration = totalTime;
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

         case MONTHS :
         {
            duration = totalTime;
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

         case ELAPSED_MINUTES :
         {
            duration = totalTime / (60 * 1000);
            break;
         }

         case ELAPSED_HOURS :
         {
            duration = totalTime / (60 * 60 * 1000);
            break;
         }

         case ELAPSED_DAYS :
         {
            duration = totalTime / (24 * 60 * 60 * 1000);
            break;
         }

         case ELAPSED_WEEKS :
         {
            duration = totalTime / (7 * 24 * 60 * 60 * 1000);
            break;
         }

         case ELAPSED_MONTHS :
         {
            duration = totalTime / (30 * 24 * 60 * 60 * 1000);
            break;
         }

         default :
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
      long currentTime = DateUtility.getCanonicalTime(date).getTime();
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
    * Retrieves the amount of working time in a day before or after an
    * intersection point.
    * 
    * @param hours collection of working time in a day
    * @param date intersection time
    * @param after true returns time after intersect, false returns time before
    * @return length of time in milliseconds
    */
   private long getTotalTime(ProjectCalendarHours hours, Date date, boolean after)
   {
      long total = 0;
      Date current = DateUtility.getCanonicalTime(date);
      long currentTime = current.getTime();

      for (DateRange range : hours)
      {
         total += getTime(range.getStart(), range.getEnd(), currentTime, after);
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
         Date start = DateUtility.getCanonicalTime(startDate);
         Date end = DateUtility.getCanonicalTime(endDate);

         for (DateRange range : hours)
         {
            Date rangeStart = range.getStart();
            Date rangeEnd = range.getEnd();
            if (rangeStart != null && rangeEnd != null)
            {
               Date canoncialRangeStart = DateUtility.getCanonicalTime(rangeStart);
               Date canonicalRangeEnd = DateUtility.getCanonicalTime(rangeEnd);

               Date startDay = DateUtility.getDayStartDate(rangeStart);
               Date finishDay = DateUtility.getDayStartDate(rangeEnd);

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
    * This method calculates the total amount of working time represented by
    * a single day of work.
    * 
    * @param hours collection of working hours
    * @return length of time in milliseconds
    */
   private long getTotalTime(ProjectCalendarHours hours)
   {
      long total = 0;

      for (DateRange range : hours)
      {
         total += getTime(range.getStart(), range.getEnd());
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
         Date startTime = DateUtility.getCanonicalTime(start);
         Date endTime = DateUtility.getCanonicalTime(end);

         Date startDay = DateUtility.getDayStartDate(start);
         Date finishDay = DateUtility.getDayStartDate(end);

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

         int diff = DateUtility.compare(startTime, endTime, target);
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
         Date startTime = DateUtility.getCanonicalTime(start);
         Date endTime = DateUtility.getCanonicalTime(end);

         Date startDay = DateUtility.getDayStartDate(start);
         Date finishDay = DateUtility.getDayStartDate(end);

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
    * {@inheritDoc}
    */
   @Override public String toString()
   {
      ByteArrayOutputStream os = new ByteArrayOutputStream();
      PrintWriter pw = new PrintWriter(os);
      pw.println("[ProjectCalendar");
      pw.println("   ID=" + m_uniqueID);
      pw.println("   name=" + m_name);
      pw.println("   baseCalendarName=" + (m_baseCalendar == null ? "" : m_baseCalendar.getName()));
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
         pw.println("      type=" + m_days[loop]);
         pw.println("      hours=" + m_hours[loop]);
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
      super(file);

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

            if (end.compareTo(start) > 0)
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
    * Unique identifier of this calendar.
    */
   private Integer m_uniqueID = Integer.valueOf(0);

   /**
    * Calendar name.
    */
   private String m_name;

   /**
    * Base calendar from which this calendar is derived.
    */
   private ProjectCalendar m_baseCalendar;

   /**
    * Array holding working/non-working/default flags for each
    * day of the week.
    */
   private DayType[] m_days = new DayType[7];

   /**
    * List of exceptions to the base calendar.
    */
   private List<ProjectCalendarException> m_exceptions = new LinkedList<ProjectCalendarException>();

   /**
    * Flag indicating if the list of exceptions is sorted.
    */
   private boolean m_exceptionsSorted;

   /**
    * List of working hours for the base calendar.
    */
   private ProjectCalendarHours[] m_hours = new ProjectCalendarHours[7];

   /**
    * This resource to which this calendar is attached.
    */
   private Resource m_resource;

   /**
    * List of calendars derived from this calendar instance.
    */
   private ArrayList<ProjectCalendar> m_derivedCalendars = new ArrayList<ProjectCalendar>();

   private Map<DateRange, Long> m_workingDateCache = new WeakHashMap<DateRange, Long>();
   private Map<Date, Date> m_startTimeCache = new WeakHashMap<Date, Date>();

   /**
    * Default base calendar name to use when none is supplied.
    */
   public static final String DEFAULT_BASE_CALENDAR_NAME = "Standard";

   public static final Date DEFAULT_START1 = DateUtility.getTime(8, 0);
   public static final Date DEFAULT_END1 = DateUtility.getTime(12, 0);
   public static final Date DEFAULT_START2 = DateUtility.getTime(13, 0);
   public static final Date DEFAULT_END2 = DateUtility.getTime(17, 0);

   /**
    * It is possible for a project calendar to be configured with no working
    * days. This will result in an infinite loop when looking for the next
    * working day from a date, so we use this constant to set a limit on the
    * maximum number of non-working days we'll skip before we bail out
    * and take an alternative approach.
    */
   private static final int MAX_NONWORKING_DAYS = 1000;
}
