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

package org.mpxj;

import java.time.DayOfWeek;

import org.mpxj.common.NumberHelper;

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
      m_projectFile = projectFile;
   }

   @Override public void removed(ProjectCalendar calendar)
   {
      super.removed(calendar);
      calendar.getDerivedCalendars().forEach(c -> c.setParent(null));
      calendar.getResources().forEach(r -> r.setCalendar(null));
      calendar.getTasks().forEach(t -> t.setCalendar(null));
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
      calendar.addDefaultCalendarDays();
      calendar.addDefaultCalendarHours();

      return calendar;
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

      calendar.setCalendarDayType(DayOfWeek.SUNDAY, DayType.DEFAULT);
      calendar.setCalendarDayType(DayOfWeek.MONDAY, DayType.DEFAULT);
      calendar.setCalendarDayType(DayOfWeek.TUESDAY, DayType.DEFAULT);
      calendar.setCalendarDayType(DayOfWeek.WEDNESDAY, DayType.DEFAULT);
      calendar.setCalendarDayType(DayOfWeek.THURSDAY, DayType.DEFAULT);
      calendar.setCalendarDayType(DayOfWeek.FRIDAY, DayType.DEFAULT);
      calendar.setCalendarDayType(DayOfWeek.SATURDAY, DayType.DEFAULT);

      return (calendar);
   }

   /**
    * If we're calling this method, we don't have a reliable way to identify
    * the default calendar. As that's the case we'll try to find a calendar
    * called "Standard". If that doesn't exist, but we have some calendars,
    * we'll just use the first one. Finally, if we have no calendars we'll
    * create ourselves a default one.
    *
    * @return default project calendar
    */
   public ProjectCalendar findOrCreateDefaultCalendar()
   {
      ProjectCalendar result = getByName(ProjectCalendar.DEFAULT_BASE_CALENDAR_NAME);
      if (result == null)
      {
         if (!isEmpty())
         {
            result = get(0);
         }
         else
         {
            result = addDefaultBaseCalendar();
            if (NumberHelper.getInt(result.getUniqueID()) == 0)
            {
               result.setUniqueID(m_projectFile.getUniqueIdObjectSequence(ProjectCalendar.class).getNext());
            }
         }
      }

      return result;
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

      if (calendarName != null && !calendarName.isEmpty())
      {
         for (ProjectCalendar projectCalendar : this)
         {
            calendar = projectCalendar;
            String name = calendar.getName();

            if ((name != null) && name.equalsIgnoreCase(calendarName))
            {
               break;
            }

            calendar = null;
         }
      }

      return (calendar);
   }

   private final ProjectFile m_projectFile;
}
