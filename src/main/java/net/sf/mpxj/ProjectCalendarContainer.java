/*
 * file:       ProjectCalendarContainer.java
 * author:     Jon Iles
 * copyright:  (c) Packwood Software 2002-2015
 * date:       20/04/2015
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

import java.util.Iterator;

/**
 * Manages the collection of calendars belonging to a project.
 */
public class ProjectCalendarContainer extends ProjectEntityContainer<ProjectCalendar>
{
   /**
    * Constructor.
    *
    * @param projectFile parent project
    */
   public ProjectCalendarContainer(ProjectFile projectFile)
   {
      super(projectFile);
   }

   @Override public void removed(ProjectCalendar calendar)
   {
      Resource resource = calendar.getResource();
      if (resource != null)
      {
         resource.setResourceCalendar(null);
      }

      calendar.setParent(null);
   }

   /**
    * Add a calendar to the project.
    *
    * @return new task instance
    */
   public ProjectCalendar add()
   {
      ProjectCalendar calendar = new ProjectCalendar(m_projectFile);
      add(calendar);
      return calendar;
   }

   /**
    * This is a convenience method used to add a calendar called
    * "Standard" to the project, and populate it with a default working week
    * and default working hours.
    *
    * @return a new default calendar
    */
   public ProjectCalendar addDefaultBaseCalendar()
   {
      ProjectCalendar calendar = add();

      calendar.setName(ProjectCalendar.DEFAULT_BASE_CALENDAR_NAME);

      calendar.setWorkingDay(Day.SUNDAY, false);
      calendar.setWorkingDay(Day.MONDAY, true);
      calendar.setWorkingDay(Day.TUESDAY, true);
      calendar.setWorkingDay(Day.WEDNESDAY, true);
      calendar.setWorkingDay(Day.THURSDAY, true);
      calendar.setWorkingDay(Day.FRIDAY, true);
      calendar.setWorkingDay(Day.SATURDAY, false);

      calendar.addDefaultCalendarHours();

      return (calendar);
   }

   /**
    * This is a convenience method to add a default derived
    * calendar to the project.
    *
    * @return new ProjectCalendar instance
    */
   public ProjectCalendar addDefaultDerivedCalendar()
   {
      ProjectCalendar calendar = add();

      calendar.setWorkingDay(Day.SUNDAY, DayType.DEFAULT);
      calendar.setWorkingDay(Day.MONDAY, DayType.DEFAULT);
      calendar.setWorkingDay(Day.TUESDAY, DayType.DEFAULT);
      calendar.setWorkingDay(Day.WEDNESDAY, DayType.DEFAULT);
      calendar.setWorkingDay(Day.THURSDAY, DayType.DEFAULT);
      calendar.setWorkingDay(Day.FRIDAY, DayType.DEFAULT);
      calendar.setWorkingDay(Day.SATURDAY, DayType.DEFAULT);

      return (calendar);
   }

   /**
    * Retrieves the named calendar. This method will return
    * null if the named calendar is not located.
    *
    * @param calendarName name of the required calendar
    * @return ProjectCalendar instance
    */
   public ProjectCalendar getByName(String calendarName)
   {
      ProjectCalendar calendar = null;

      if (calendarName != null && calendarName.length() != 0)
      {
         Iterator<ProjectCalendar> iter = iterator();
         while (iter.hasNext() == true)
         {
            calendar = iter.next();
            String name = calendar.getName();

            if ((name != null) && (name.equalsIgnoreCase(calendarName) == true))
            {
               break;
            }

            calendar = null;
         }
      }

      return (calendar);
   }
}
