/*
 * file:       ProjectCalendarHelper.java
 * author:     Jon Iles
 * copyright:  (c) Packwood Software 2022
 * date:       06/05/2022
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

package net.sf.mpxj.common;

import java.util.ArrayList;
import java.util.List;

import net.sf.mpxj.DateRange;
import net.sf.mpxj.Day;
import net.sf.mpxj.DayType;
import net.sf.mpxj.ProjectCalendar;
import net.sf.mpxj.ProjectCalendarException;
import net.sf.mpxj.ProjectCalendarHours;
import net.sf.mpxj.ProjectCalendarWeek;
import net.sf.mpxj.ProjectFile;
import net.sf.mpxj.Resource;

/**
 * Helper methods for working with {@code ProjectCalendar} instances.
 */
public final class ProjectCalendarHelper
{
   /**
    * Creates a temporary base calendar by flattening an existing calendar's
    * hierarchy. This is typically used to create a calendar which is compatible
    * with Microsoft Project.
    *
    * @param calendar calendar to flatten
    * @return flattened calendar
    */
   public static ProjectCalendar createTemporaryFlattenedCalendar(ProjectCalendar calendar)
   {
      if (!calendar.isDerived())
      {
         return calendar;
      }

      ProjectCalendar newCalendar = new ProjectCalendar(calendar.getParentFile());
      newCalendar.setName(calendar.getName());
      newCalendar.setUniqueID(calendar.getUniqueID());
      populateDays(newCalendar, calendar);
      populateWorkingWeeks(newCalendar, calendar);
      mergeExceptions(newCalendar, calendar);

      return newCalendar;
   }

   /**
    * Create a temporary derived calendar. This is typically used to
    * create a calendar which is compatible with Microsoft Project.
    *
    * @param baseCalendar calendar to derive from
    * @param resource link the new calendar to this resource
    * @return derived calendar
    */
   public static ProjectCalendar createTemporaryDerivedCalendar(ProjectCalendar baseCalendar, Resource resource)
   {
      ProjectFile file = baseCalendar.getParentFile();
      ProjectCalendar derivedCalendar = new ProjectCalendar(file);
      derivedCalendar.setParent(baseCalendar);
      derivedCalendar.setName(resource.getName());
      derivedCalendar.setWorkingDay(Day.SUNDAY, DayType.DEFAULT);
      derivedCalendar.setWorkingDay(Day.MONDAY, DayType.DEFAULT);
      derivedCalendar.setWorkingDay(Day.TUESDAY, DayType.DEFAULT);
      derivedCalendar.setWorkingDay(Day.WEDNESDAY, DayType.DEFAULT);
      derivedCalendar.setWorkingDay(Day.THURSDAY, DayType.DEFAULT);
      derivedCalendar.setWorkingDay(Day.FRIDAY, DayType.DEFAULT);
      derivedCalendar.setWorkingDay(Day.SATURDAY, DayType.DEFAULT);

      if (NumberHelper.getInt(derivedCalendar.getUniqueID()) == 0)
      {
         derivedCalendar.setUniqueID(Integer.valueOf(file.getProjectConfig().getNextCalendarUniqueID()));
      }

      return derivedCalendar;
   }

   /**
    * Expand any exceptions in the given calendar, and include any working weeks
    * defined by this calendar as exceptions. This is typically used to communicate
    * working time accurately when the consuming application does not have the concept
    * of working weeks.
    *
    * @param calendar calendar to process
    * @return expanded exceptions, including working weeks
    */
   public static List<ProjectCalendarException> getExpandedExceptionsWithWorkWeeks(ProjectCalendar calendar)
   {
      List<ProjectCalendarException> result;

      if (calendar.getWorkWeeks().isEmpty())
      {
         result = calendar.getExpandedCalendarExceptions();
      }
      else
      {
         ProjectCalendar temporaryCalendar = new ProjectCalendar(calendar.getParentFile());
         ProjectCalendarHelper.mergeExceptions(temporaryCalendar, calendar.getCalendarExceptions());
         for (ProjectCalendarWeek week : calendar.getWorkWeeks())
         {
            ProjectCalendarHelper.mergeExceptions(temporaryCalendar, week.convertToRecurringExceptions());
         }

         result = temporaryCalendar.getExpandedCalendarExceptions();
      }

      return result;
   }

   /**
    * Merge exceptions recursively from the source calendar (and any calendars from which it is derived)
    * into the target calendar.
    *
    * @param target target calendar to receive exceptions
    * @param source source calendar from which exceptions are read
    */
   public static void mergeExceptions(ProjectCalendar target, ProjectCalendar source)
   {
      mergeExceptions(target, source.getCalendarExceptions());

      // Work down the hierarchy adding any exceptions which haven't been overridden
      // by calendars higher up the hierarchy.
      ProjectCalendar parent = source.getParent();
      if (parent != null)
      {
         mergeExceptions(target, parent);
      }
   }

   /**
    * Merge the supplied list of exceptions into the target calendar.
    *
    * @param target calendar into which the exceptions are merged
    * @param sourceExceptions exceptions to merge
    */
   public static void mergeExceptions(ProjectCalendar target, List<ProjectCalendarException> sourceExceptions)
   {
      List<ProjectCalendarException> expandedTargetExceptions = new ArrayList<>(target.getExpandedCalendarExceptions());

      for (ProjectCalendarException sourceException : sourceExceptions)
      {
         // For each source exception we need to see if it collides with an existing exception
         // in the target calendar. To do this we compare the expanded version of the source exception
         // with the expanded version of all the target calendar exceptions.
         boolean collision = false;
         List<ProjectCalendarException> expandedSourceExceptions = sourceException.getExpandedExceptions();
         for (ProjectCalendarException expandedSourceException : expandedSourceExceptions)
         {
            collision = expandedTargetExceptions.stream().anyMatch(e -> e.contains(expandedSourceException));
            if (collision)
            {
               break;
            }
         }

         if (collision)
         {
            // If we have a collision then we can't add the exception in its original form.
            // We'll expand it and add any of the expanded exception which don't collide.
            // This gives us a union of the exceptions, allowing the target calendar
            // exceptions to override those in the source calendar where they collide.
            for (ProjectCalendarException expandedSourceException : expandedSourceExceptions)
            {
               if (expandedTargetExceptions.stream().noneMatch(e -> e.contains(expandedSourceException)))
               {
                  ProjectCalendarException newException = target.addCalendarException(expandedSourceException.getFromDate(), expandedSourceException.getToDate());
                  for (DateRange range : expandedSourceException)
                  {
                     newException.addRange(range);
                  }
               }
            }
         }
         else
         {
            // There is no collision between the source exception and the exceptions in the target calendar.
            // We can just add a verbatim copy of the source exception.
            ProjectCalendarException newException = target.addCalendarException(sourceException.getFromDate(), sourceException.getToDate());
            newException.setRecurring(sourceException.getRecurring());
            for (DateRange range : sourceException)
            {
               newException.addRange(range);
            }
         }
      }
   }

   /**
    * Copies days and hours from one calendar to another.
    *
    * @param target target calendar
    * @param source source calendar
    */
   private static void populateDays(ProjectCalendar target, ProjectCalendar source)
   {
      for (Day day : Day.values())
      {
         // Populate day types and hours
         ProjectCalendarHours hours = source.getHours(day);
         ProjectCalendarHours newHours = target.addCalendarHours(day);
         if (hours == null || hours.getRangeCount() == 0)
         {
            target.setWorkingDay(day, DayType.NON_WORKING);
         }
         else
         {
            target.setWorkingDay(day, DayType.WORKING);
            for (DateRange range : hours)
            {
               newHours.addRange(range);
            }
         }
      }
   }

   /**
    * Copies working weeks from one calendar to another.
    *
    * @param target target calendar
    * @param source source calendar
    */
   private static void populateWorkingWeeks(ProjectCalendar target, ProjectCalendar source)
   {
      for (ProjectCalendarWeek sourceWeek : source.getWorkWeeks())
      {
         ProjectCalendarWeek targetWeek = target.addWorkWeek();
         for (Day day : Day.values())
         {
            targetWeek.setWorkingDay(day, sourceWeek.getWorkingDay(day));
            ProjectCalendarHours sourceHours = sourceWeek.getCalendarHours(day);
            if (sourceHours != null)
            {
               ProjectCalendarHours targetHours = targetWeek.addCalendarHours(day);
               for(DateRange range : sourceHours)
               {
                  targetHours.addRange(range);
               }
            }
         }
      }
   }
}
