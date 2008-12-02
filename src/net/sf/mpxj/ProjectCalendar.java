/*
 * file:       ProjectCalendar.java
 * author:     Jon Iles
 * copyright:  (c) Packwood Software Limited 2002-2003
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
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import net.sf.mpxj.utility.DateUtility;

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
    * @return ProjectCalendarException instance
    */
   public ProjectCalendarException addCalendarException()
   {
      ProjectCalendarException bce = new ProjectCalendarException();
      m_exceptions.add(bce);
      return (bce);
   }

   /**
    * This method retrieves a list of exceptions to the current calendar.
    *
    * @return List of calendar exceptions
    */
   public List<ProjectCalendarException> getCalendarExceptions()
   {
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
      if (result == null && m_baseCalendar != null)
      {
         result = m_baseCalendar.getHours(day);
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
      int value = m_days[day.getValue() - 1];
      boolean result;

      if (value == DEFAULT)
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
         result = (value == WORKING);
      }

      return (result);
   }

   /**
    * Retrieve an array representing the days of the week for this calendar.
    *
    * @return array of days of the week
    */
   public int[] getDays()
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
   public int getWorkingDay(Day day)
   {
      return (m_days[day.getValue() - 1]);
   }

   /**
    * This is a convenience method provided to allow a day to be set
    * as working or non-working, by using the day number to
    * identify the required day.
    *
    * @param day required day
    * @param working flag indicating if the day is working/non-working/default
    */
   public void setWorkingDay(Day day, int working)
   {
      m_days[day.getValue() - 1] = working;
   }

   /**
    * convenience method for setting working or non-working days.
    *
    * @param day required day
    * @param working flag indicating if the day is a working day
    */
   public void setWorkingDay(Day day, boolean working)
   {
      setWorkingDay(day, (working == true ? WORKING : NON_WORKING));
   }

   /**
    * This is a convenience method provided to allow a day to be set
    * as working or non-working, by using the day number to
    * identify the required day.
    *
    * @param day required day
    * @param working flag indicating if the day is a working day
    */
   public void setWorkingDay(Day day, Integer working)
   {
      int value;

      if (working == null)
      {
         if (isBaseCalendar() == false)
         {
            value = DEFAULT;
         }
         else
         {
            value = WORKING;
         }
      }
      else
      {
         value = working.intValue();
      }

      setWorkingDay(day, value);
   }

   /**
    * This is a convenience method used to add a default set of calendar
    * hours to a calendar.
    */
   public void addDefaultCalendarHours()
   {
      ProjectCalendarHours hours = addCalendarHours(Day.SUNDAY);

      hours = addCalendarHours(Day.MONDAY);
      hours.addRange(new DateRange(DEFAULT_START1, DEFAULT_END1));
      hours.addRange(new DateRange(DEFAULT_START2, DEFAULT_END2));

      hours = addCalendarHours(Day.TUESDAY);
      hours.addRange(new DateRange(DEFAULT_START1, DEFAULT_END1));
      hours.addRange(new DateRange(DEFAULT_START2, DEFAULT_END2));

      hours = addCalendarHours(Day.WEDNESDAY);
      hours.addRange(new DateRange(DEFAULT_START1, DEFAULT_END1));
      hours.addRange(new DateRange(DEFAULT_START2, DEFAULT_END2));

      hours = addCalendarHours(Day.THURSDAY);
      hours.addRange(new DateRange(DEFAULT_START1, DEFAULT_END1));
      hours.addRange(new DateRange(DEFAULT_START2, DEFAULT_END2));

      hours = addCalendarHours(Day.FRIDAY);
      hours.addRange(new DateRange(DEFAULT_START1, DEFAULT_END1));
      hours.addRange(new DateRange(DEFAULT_START2, DEFAULT_END2));

      addCalendarHours(Day.SATURDAY);
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
      ProjectCalendarDateRanges ranges = getException(date);
      if (ranges == null)
      {
         Calendar cal = Calendar.getInstance();
         cal.setTime(date);
         Day day = Day.getInstance(cal.get(Calendar.DAY_OF_WEEK));
         ranges = getHours(day);
      }
      Date result;
      if (ranges == null)
      {
         result = getParentFile().getProjectHeader().getDefaultStartTime();
      }
      else
      {
         result = ranges.getRange(0).getStart();
      }
      return DateUtility.getCanonicalTime(result);
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
      ProjectCalendarDateRanges ranges = getException(date);
      if (ranges == null)
      {
         Calendar cal = Calendar.getInstance();
         cal.setTime(date);
         Day day = Day.getInstance(cal.get(Calendar.DAY_OF_WEEK));
         ranges = getHours(day);
      }
      Date result;
      if (ranges == null)
      {
         result = getParentFile().getProjectHeader().getDefaultEndTime();
      }
      else
      {
         result = ranges.getRange(ranges.getRangeCount() - 1).getEnd();
      }
      return DateUtility.getCanonicalTime(result);
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
      long remainingMinutes = (long) duration.convertUnits(TimeUnit.MINUTES, header).getDuration();
      Calendar cal = Calendar.getInstance();
      cal.setTime(startDate);

      while (remainingMinutes > 0)
      {
         //
         // Get the current date and time and determine how many 
         // working hours remain
         //
         Date currentDate = cal.getTime();
         Date currentDateEnd = DateUtility.getDayEndDate(currentDate);
         long currentDateWorkingMinutes = (long) getWork(currentDate, currentDateEnd, TimeUnit.MINUTES).getDuration();

         //
         // We have more than enough hours left
         //
         if (remainingMinutes > currentDateWorkingMinutes)
         {
            //
            // Deduct this day's hours from our total
            //
            remainingMinutes -= currentDateWorkingMinutes;

            //
            // Move the calendar forward to the next working day
            //            
            Day day;
            do
            {
               cal.set(Calendar.DAY_OF_YEAR, cal.get(Calendar.DAY_OF_YEAR) + 1);
               day = Day.getInstance(cal.get(Calendar.DAY_OF_WEEK));
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
               Date canonicalRangeEnd = DateUtility.getCanonicalTime(rangeEnd);
               Date canonicalRangeStart = DateUtility.getCanonicalTime(rangeStart);

               boolean hours24 = false;
               if (canonicalRangeStart.getTime() == canonicalRangeEnd.getTime() && rangeStart.getTime() < rangeEnd.getTime())
               {
                  hours24 = true;
               }

               if (firstRange && !hours24 && canonicalRangeEnd.getTime() < currentDateStartTime.getTime())
               {
                  continue;
               }

               //
               // Move the start of the range if our current start is 
               // past the range start
               //               
               if (firstRange && !hours24 && canonicalRangeStart.getTime() < currentDateStartTime.getTime())
               {
                  canonicalRangeStart = currentDateStartTime;
               }
               firstRange = false;

               long rangeMinutes;

               if (hours24)
               {
                  rangeMinutes = 24 * 60;
               }
               else
               {
                  rangeMinutes = canonicalRangeEnd.getTime() - canonicalRangeStart.getTime();
                  rangeMinutes /= (1000 * 60);
               }

               if (remainingMinutes > rangeMinutes)
               {
                  remainingMinutes -= rangeMinutes;
               }
               else
               {
                  if (remainingMinutes == rangeMinutes)
                  {
                     endTime = canonicalRangeEnd;
                  }
                  else
                  {
                     endTime = new Date(canonicalRangeStart.getTime() + (remainingMinutes * (60 * 1000)));
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
    * This method finds the start of the next working period.
    * 
    * @param cal current Calendar instance
    */
   private void updateToNextWorkStart(Calendar cal)
   {
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
            Date rangeEnd = DateUtility.getCanonicalTime(range.getEnd());
            if (calTime.getTime() < rangeEnd.getTime())
            {
               Date rangeStart = DateUtility.getCanonicalTime(range.getStart());
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
            do
            {
               cal.set(Calendar.DAY_OF_YEAR, cal.get(Calendar.DAY_OF_YEAR) + 1);
               day = Day.getInstance(cal.get(Calendar.DAY_OF_WEEK));
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

      return (result);
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
      Iterator<ProjectCalendarException> iter = m_exceptions.iterator();
      ProjectCalendarException exception = null;

      while (iter.hasNext() == true)
      {
         exception = iter.next();
         if (exception.contains(date) == true)
         {
            break;
         }
         exception = null;
      }
      return (exception);
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
      long totalTime = 0;

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
               if (currentDate.getTime() > canonicalEndDate.getTime())
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
            ProjectCalendarException exception = getException(endDate);
            if (exception == null)
            {
               totalTime += getTotalTime(getHours(day), endDate, false);
            }
            else
            {
               totalTime += getTotalTime(exception, endDate, false);
            }
         }
      }

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

         default :
         {
            throw new IllegalArgumentException("TimeUnit " + format + " not supported");
         }
      }

      if (invert == true)
      {
         duration = -duration;
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
   private long getTotalTime(ProjectCalendarException exception, Date date, boolean after)
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
   private long getTotalTime(ProjectCalendarException exception)
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
      long currentTime = DateUtility.getCanonicalTime(date).getTime();

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
      pw.println("   name=" + m_name);
      pw.println("   baseCalendarName=" + (m_baseCalendar == null ? "" : m_baseCalendar.getName()));
      pw.println("   resource=" + (m_resource == null ? "" : m_resource.getName()));

      String[] dayType =
      {
         "Non-working",
         "Working",
         "Default"
      };
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
         pw.println("      type=" + dayType[m_days[loop]]);
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
   private int[] m_days = new int[7];

   /**
    * List of exceptions to the base calendar.
    */
   private LinkedList<ProjectCalendarException> m_exceptions = new LinkedList<ProjectCalendarException>();

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
   private List<ProjectCalendar> m_derivedCalendars = new LinkedList<ProjectCalendar>();

   /**
    * Default base calendar name to use when none is supplied.
    */
   public static final String DEFAULT_BASE_CALENDAR_NAME = "Standard";

   /**
    * Constant used to represent a non-working day.
    */
   public static final int NON_WORKING = 0;

   /**
    * Constant used to represent a working day.
    */
   public static final int WORKING = 1;

   /**
    * Constant used to represent that a day in a derived calendar used
    * the value specified in the base calendar to indicate if it is working
    * or not.
    */
   public static final int DEFAULT = 2;

   public static final Date DEFAULT_START1 = DateUtility.getTime(8, 0);
   public static final Date DEFAULT_END1 = DateUtility.getTime(12, 0);
   public static final Date DEFAULT_START2 = DateUtility.getTime(13, 0);
   public static final Date DEFAULT_END2 = DateUtility.getTime(17, 0);
}
