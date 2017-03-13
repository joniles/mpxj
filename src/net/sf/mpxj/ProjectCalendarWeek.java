/*
 * file:       ProjectCalendarWeek.java
 * author:     Jon Iles
 * copyright:  (c) Packwood Software 2011
 * date:       08/11/2011
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

import net.sf.mpxj.common.DateHelper;

/**
 * This class represents a basic working week, with no exceptions.
 */
public class ProjectCalendarWeek
{
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
    * Retrieves the data range for which this week is valid.
    * Returns null if this is the default week.
    *
    * @return date range, or null
    */
   public DateRange getDateRange()
   {
      return m_dateRange;
   }

   /**
    * Sets the date range for this week. Set this to null to indicate
    * the default week.
    *
    * @param range date range, or null
    */
   public void setDateRange(DateRange range)
   {
      m_dateRange = range;
   }

   /**
    * If this week is derived from a another week, this method
    * will return the parent week.
    *
    * @return parent week
    */
   public ProjectCalendarWeek getParent()
   {
      return m_parent;
   }

   /**
    * Set the parent from which this week is derived.
    *
    * @param parent parent week
    */
   void setParent(ProjectCalendarWeek parent)
   {
      m_parent = parent;

      for (int loop = 0; loop < m_days.length; loop++)
      {
         if (m_days[loop] == null)
         {
            m_days[loop] = DayType.DEFAULT;
         }
      }
   }

   /**
    * Flag indicating if this week is derived from another week.
    *
    * @return true if this week is derived from another
    */
   public boolean isDerived()
   {
      return (m_parent != null);
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
         if (m_parent == null)
         {
            // Only add default hours for the day that is 'missing' to avoid overwriting real calendar hours
            addDefaultCalendarHours(day);
            result = getCalendarHours(day);
         }
         else
         {
            result = m_parent.getHours(day);
         }
      }
      return result;
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
         hours.addRange(DEFAULT_WORKING_MORNING);
         hours.addRange(DEFAULT_WORKING_AFTERNOON);
      }
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
      setWorkingDay(day, (working ? DayType.WORKING : DayType.NON_WORKING));
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
         if (isDerived())
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
    * Calendar name.
    */
   private String m_name;

   /**
    * Date range for which this week is valid, null if this is the default week.
    */
   private DateRange m_dateRange;

   /**
    * Parent week from which this is derived, if any.
    */
   private ProjectCalendarWeek m_parent;

   /**
    * Working hours for each day.
    */
   private ProjectCalendarHours[] m_hours = new ProjectCalendarHours[7];

   /**
    * Working/non-working/default flag for each day.
    */
   private DayType[] m_days = new DayType[7];

   /**
    * Constants representing the default working morning and afternoon hours.
    */
   public static final DateRange DEFAULT_WORKING_MORNING = new DateRange(DateHelper.getTime(8, 0), DateHelper.getTime(12, 0));
   public static final DateRange DEFAULT_WORKING_AFTERNOON = new DateRange(DateHelper.getTime(13, 0), DateHelper.getTime(17, 0));
}
