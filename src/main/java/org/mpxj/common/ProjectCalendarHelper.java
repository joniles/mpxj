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

package org.mpxj.common;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.TreeSet;

import org.mpxj.DayType;
import org.mpxj.LocalDateRange;
import org.mpxj.ProjectCalendar;
import org.mpxj.ProjectCalendarException;
import org.mpxj.ProjectCalendarHours;
import org.mpxj.ProjectCalendarWeek;
import org.mpxj.Resource;
import org.mpxj.TemporaryCalendar;

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

      ProjectCalendar newCalendar = new TemporaryCalendar(calendar.getProjectContext());
      newCalendar.setName(calendar.getName());
      newCalendar.setUniqueID(calendar.getUniqueID());
      newCalendar.setType(calendar.getType());
      newCalendar.setProjectUniqueID(calendar.getProjectUniqueID());
      newCalendar.setPersonal(calendar.getPersonal());
      newCalendar.setCalendarMinutesPerDay(calendar.getCalendarMinutesPerDay());
      newCalendar.setCalendarMinutesPerWeek(calendar.getCalendarMinutesPerWeek());
      newCalendar.setCalendarMinutesPerMonth(calendar.getMinutesPerMonth());
      newCalendar.setCalendarMinutesPerYear(calendar.getCalendarMinutesPerYear());

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
      ProjectCalendar derivedCalendar = new TemporaryCalendar(baseCalendar.getProjectContext());
      derivedCalendar.setParent(baseCalendar);
      derivedCalendar.setName(resource.getName());
      derivedCalendar.setCalendarDayType(DayOfWeek.SUNDAY, DayType.DEFAULT);
      derivedCalendar.setCalendarDayType(DayOfWeek.MONDAY, DayType.DEFAULT);
      derivedCalendar.setCalendarDayType(DayOfWeek.TUESDAY, DayType.DEFAULT);
      derivedCalendar.setCalendarDayType(DayOfWeek.WEDNESDAY, DayType.DEFAULT);
      derivedCalendar.setCalendarDayType(DayOfWeek.THURSDAY, DayType.DEFAULT);
      derivedCalendar.setCalendarDayType(DayOfWeek.FRIDAY, DayType.DEFAULT);
      derivedCalendar.setCalendarDayType(DayOfWeek.SATURDAY, DayType.DEFAULT);

      if (NumberHelper.getInt(derivedCalendar.getUniqueID()) == 0)
      {
         derivedCalendar.setUniqueID(baseCalendar.getProjectContext().getUniqueIdObjectSequence(ProjectCalendar.class).getNext());
      }

      return derivedCalendar;
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
                  newException.addAll(expandedSourceException);
               }
            }
         }
         else
         {
            // There is no collision between the source exception and the exceptions in the target calendar.
            // We can just add a verbatim copy of the source exception.
            ProjectCalendarException newException;
            if (sourceException.getRecurring() == null)
            {
               newException = target.addCalendarException(sourceException.getFromDate(), sourceException.getToDate());
            }
            else
            {
               newException = target.addCalendarException(sourceException.getRecurring());
            }
            newException.addAll(sourceException);
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
      for (DayOfWeek day : DayOfWeek.values())
      {
         // Populate day types and hours
         ProjectCalendarHours hours = source.getHours(day);
         ProjectCalendarHours newHours = target.addCalendarHours(day);
         if (hours == null || hours.isEmpty())
         {
            target.setCalendarDayType(day, DayType.NON_WORKING);
         }
         else
         {
            target.setCalendarDayType(day, DayType.WORKING);
            newHours.addAll(hours);
         }
      }
   }

   /**
    * Copies effective working weeks into a calendar without a parent.
    * The native selector makes the same choice throughout an interval between
    * consecutive starts and inclusive ends of all weeks in the hierarchy:
    * none of its date comparisons can change within such an interval.
    *
    * @param target target calendar
    * @param source source calendar
    */
   private static void populateWorkingWeeks(ProjectCalendar target, ProjectCalendar source)
   {
      TreeSet<LocalDate> boundaries = new TreeSet<>();
      for (ProjectCalendar calendar = source; calendar != null; calendar = calendar.getParent())
      {
         for (ProjectCalendarWeek week : calendar.getWorkWeeks())
         {
            LocalDateRange range = week.getDateRange();
            if (range == null || range.getStart() == null || range.getEnd() == null || range.getStart().isAfter(range.getEnd()))
            {
               throw new IllegalArgumentException("Cannot flatten work week without an ordered date range: " + week.getName());
            }
            boundaries.add(range.getStart());
            if (!range.getEnd().equals(LocalDate.MAX))
            {
               boundaries.add(range.getEnd().plusDays(1));
            }
         }
      }

      List<LocalDate> dates = new ArrayList<>(boundaries);
      ProjectCalendarWeek previousWeek = null;
      ProjectCalendarWeek targetWeek = null;
      for (int index = 0; index < dates.size(); ++index)
      {
         LocalDate start = dates.get(index);
         LocalDate end = index + 1 == dates.size() ? LocalDate.MAX : dates.get(index + 1).minusDays(1);
         ProjectCalendarWeek sourceWeek = source.getWorkWeek(start);
         if (sourceWeek != null)
         {
            if (sourceWeek == previousWeek)
            {
               targetWeek.setDateRange(new LocalDateRange(targetWeek.getDateRange().getStart(), end));
            }
            else
            {
               targetWeek = copyWorkingWeek(target, source, sourceWeek, new LocalDateRange(start, end));
            }
         }
         previousWeek = sourceWeek;
      }
   }

   /**
    * Copy a selected week for its effective period. DEFAULT uses the ordinary
    * hours of the source calendar's immediate parent, just as getRanges does,
    * even when the selected week belongs to a more distant ancestor.
    * Exceptions remain separate and are handled by mergeExceptions.
    *
    * @param target target calendar
    * @param source source calendar
    * @param sourceWeek selected week
    * @param range effective period
    * @return independent week with no dependency on a parent calendar
    */
   private static ProjectCalendarWeek copyWorkingWeek(ProjectCalendar target, ProjectCalendar source, ProjectCalendarWeek sourceWeek, LocalDateRange range)
   {
      ProjectCalendarWeek targetWeek = target.addWorkWeek();
      targetWeek.setName(sourceWeek.getName());
      targetWeek.setDateRange(range);
      for (DayOfWeek day : DayOfWeek.values())
      {
         DayType dayType = sourceWeek.getCalendarDayType(day);
         ProjectCalendarHours hours = sourceWeek.getCalendarHours(day);
         if (dayType == DayType.DEFAULT)
         {
            hours = source.getParent().getHours(day);
            dayType = hours == null || hours.isEmpty() ? DayType.NON_WORKING : DayType.WORKING;
         }
         targetWeek.setCalendarDayType(day, dayType);
         if (hours != null)
         {
            targetWeek.addCalendarHours(day).addAll(hours);
         }
      }
      return targetWeek;
   }
}
