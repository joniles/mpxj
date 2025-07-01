/*
 * file:       CalendarDirectoryReader.java
 * author:     Jon Iles
 * date:       2024-02-27
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

package org.mpxj.openplan;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.mpxj.DayType;
import org.mpxj.LocalTimeRange;
import org.mpxj.ProjectCalendarException;
import org.mpxj.ProjectCalendarHours;
import org.mpxj.ProjectFile;
import org.mpxj.RecurrenceType;
import org.mpxj.RecurringData;
import org.mpxj.ProjectCalendar;
import org.mpxj.common.HierarchyHelper;
import org.apache.poi.poifs.filesystem.DirectoryEntry;

/**
 * Populate the project with calendars
 * and create a map of calendar IDs to ProjectCalendar instances.
 */
class CalendarDirectoryReader extends DirectoryReader
{
   /**
    * Constructor.
    *
    * @param root parent directory
    * @param file parent project
    */
   public CalendarDirectoryReader(DirectoryEntry root, ProjectFile file)
   {
      m_root = root;
      m_file = file;
   }

   /**
    * Read the calendars from the named directory and populate the
    * parent project.
    *
    * @param name calendar directory name
    */
   public void read(String name)
   {
      /*
         CLD - Calendar Directory Record (just one row?)
         CLR - Calendar Detail
         CLH - Calendar Header
         ACL - Access Control
       */

      DirectoryEntry dir = getDirectoryEntry(m_root, name);

      //
      // Read headers
      //
      List<Row> rows = new TableReader(dir, "CLH").read();
      HierarchyHelper.sortHierarchy(rows, r -> r.getString("CLH_ID"), r -> OpenPlanHierarchyHelper.getParentID(r.getString("CLH_ID")), Comparator.comparing(o -> o.getString("CLH_ID")));

      for (Row row : rows)
      {
         ProjectCalendar calendar = m_file.addCalendar();
         ProjectCalendar parentCalendar = m_map.get(OpenPlanHierarchyHelper.getParentID(row.getString("CLH_ID")));
         if (parentCalendar != null)
         {
            calendar.setParent(parentCalendar);
         }

         String calendarName = row.getString("DESCRIPTION");
         if (calendarName == null || calendarName.isEmpty())
         {
            calendarName = row.getString("CLH_ID");

         }
         // CLH_ID: Calendar Header ID
         // CLH_UID: Calendar Header Unique ID
         calendar.setGUID(row.getUuid("CLH_UID"));
         // DESCRIPTION: Description
         calendar.setName(calendarName);
         // DIR_ID: Project Object Directory Name
         // DIR_UID: Project Object Directory UID
         // INTEGRATION_ID: External Unique ID
         // LASTUPDATE: Last Update Date
         // SEQUENCE: Update Count
         // SUPPRESS
         // USR_ID: Last Update User

         // Default all days to non-working
         Arrays.stream(DayOfWeek.values()).forEach(d -> calendar.setCalendarDayType(d, DayType.NON_WORKING));

         m_map.put(row.getString("CLH_ID"), calendar);
      }

      //
      // Read detail records
      //
      rows = new TableReader(dir, "CLR").read();
      for (Row row : rows)
      {
         // CLH_ID: Calendar Header ID
         // CLH_UID: Calendar Header Unique ID
         // CLR_UID: Calendar Detail Unique ID
         // DATESPEC: Day Name, Month and Day, or Date
         // DIR_ID: Project Object Directory Name
         // DIR_UID: Project Object Directory UID
         // OPFINISH: Finish Time
         // OPSTART: Start Time
         // OPWORK: Working Flag
         // SEQUENCE: Update Count

         ProjectCalendar calendar = m_map.get(row.getString("CLH_ID"));
         if (calendar == null)
         {
            continue;
         }

         if (isDayOfWeek(row))
         {
            readDayOfWeek(calendar, row);
            continue;
         }

         if (isDate(row))
         {
            readExceptionDate(calendar, row);
         }

         if (isDayAndMonth(row))
         {
            readDayAndMonth(calendar, row);
         }
      }
   }

   /**
    * Retrieve a map of calendar ID s to ProjectCalendar instances.
    *
    * @return calendar map
    */
   public Map<String, ProjectCalendar> getMap()
   {
      return m_map;
   }

   /**
    * Read a day of week specification for a calendar.
    *
    * @param calendar parent calendar
    * @param row calendar data
    */
   private void readDayOfWeek(ProjectCalendar calendar, Row row)
   {
      if (!row.getBoolean("OPWORK").booleanValue())
      {
         return;
      }

      DayOfWeek day = DAY_OF_WEEK_MAP.get(row.getString("DATESPEC"));
      calendar.setCalendarDayType(day, DayType.WORKING);
      ProjectCalendarHours hours = calendar.getHours(day);
      if (hours == null)
      {
         hours = calendar.addCalendarHours(day);
      }
      hours.add(new LocalTimeRange(row.getTime("OPSTART"), row.getTime("OPFINISH")));
   }

   /**
    * Read a single exception date specification for a calendar.
    *
    * @param calendar parent calendar
    * @param row calendar data
    */
   private void readExceptionDate(ProjectCalendar calendar, Row row)
   {
      LocalDate date = LocalDate.parse(row.getString("DATESPEC"), DATE_FORMAT);

      ProjectCalendarException exception = null;
      List<ProjectCalendarException> exceptions = calendar.getCalendarExceptions();

      // TODO: replace with a more efficient search!
      for (ProjectCalendarException ex : exceptions)
      {
         // We know that the exception records only cover one day
         // not a range of days, so we'll just compare the from date.
         if (ex.getFromDate().equals(date))
         {
            exception = ex;
            break;
         }

         // We know the list is sorted, so bail out if we have passed the
         // date we are interested in.
         if (date.isBefore(ex.getFromDate()))
         {
            break;
         }
      }

      // Create an exception if this is a new date
      if (exception == null)
      {
         exception = calendar.addCalendarException(date);
      }

      // We're done if this is a non-working period
      if (!row.getBoolean("OPWORK").booleanValue())
      {
         return;
      }

      // Add the time range
      exception.add(new LocalTimeRange(row.getTime("OPSTART"), row.getTime("OPFINISH")));
   }

   /**
    * Read an annually recurring exception.
    *
    * @param calendar parent calendar
    * @param row calendar data
    */
   private void readDayAndMonth(ProjectCalendar calendar, Row row)
   {
      String dateSpec = row.getString("DATESPEC");
      Integer day = Integer.valueOf(dateSpec.substring(dateSpec.length() - 2));
      Integer month = Integer.valueOf(dateSpec.substring(0, dateSpec.length() - 2));

      RecurringData recurrence = new RecurringData();
      recurrence.setStartDate(m_file.getProjectProperties().getStartDate().toLocalDate());
      recurrence.setFinishDate(m_file.getProjectProperties().getFinishDate().toLocalDate());
      recurrence.setRecurrenceType(RecurrenceType.YEARLY);
      recurrence.setDayNumber(day);
      recurrence.setMonthNumber(month);

      if (!recurrence.isValid())
      {
         return;
      }

      ProjectCalendarException exception = calendar.addCalendarException(recurrence);

      // We're done if this is a non-working period
      if (!row.getBoolean("OPWORK").booleanValue())
      {
         return;
      }

      // Add the time range
      exception.add(new LocalTimeRange(row.getTime("OPSTART"), row.getTime("OPFINISH")));
   }

   /**
    * Return true if the DATESPEC represents a day of the week.
    *
    * @param row calendar data
    * @return true if DATESPEC is a day of the week
    */
   private boolean isDayOfWeek(Row row)
   {
      return DAY_OF_WEEK_MAP.containsKey(row.getString("DATESPEC"));
   }

   /**
    * Return true if the DATESPEC represents a date.
    *
    * @param row calendar data
    * @return true if DATESPEC is a date
    */
   private boolean isDate(Row row)
   {
      String dateSpec = row.getString("DATESPEC");

      // IKVM doesn't like the chars() method
      //return dateSpec.length() == 8 && dateSpec.chars().allMatch(c -> Character.isDigit(c));

      if (dateSpec.length() != 8)
      {
         return false;
      }

      for (char c : dateSpec.toCharArray())
      {
         if (!Character.isDigit(c))
         {
            return false;
         }
      }

      return true;
   }

   /**
    * Return true if the DATESPEC represents a day and month.
    *
    * @param row calendar data
    * @return true if DATESPEC is a day and month
    */
   public boolean isDayAndMonth(Row row)
   {
      String dateSpec = row.getString("DATESPEC");

      // IKVM doesn't like the chars() method
      //return dateSpec.length() >= 3 && dateSpec.length() <= 4 && dateSpec.chars().allMatch(c -> Character.isDigit(c));

      if (dateSpec.length() < 3 || dateSpec.length() > 4)
      {
         return false;
      }

      for (char c : dateSpec.toCharArray())
      {
         if (!Character.isDigit(c))
         {
            return false;
         }
      }

      return true;
   }

   private final ProjectFile m_file;
   private final DirectoryEntry m_root;

   private final Map<String, ProjectCalendar> m_map = new HashMap<>();

   private static final DateTimeFormatter DATE_FORMAT = new DateTimeFormatterBuilder().parseLenient().appendPattern("yyyyMMdd").toFormatter();

   private static final Map<String, DayOfWeek> DAY_OF_WEEK_MAP = new HashMap<>();
   static
   {
      DAY_OF_WEEK_MAP.put("monday", DayOfWeek.MONDAY);
      DAY_OF_WEEK_MAP.put("tuesday", DayOfWeek.TUESDAY);
      DAY_OF_WEEK_MAP.put("wednesday", DayOfWeek.WEDNESDAY);
      DAY_OF_WEEK_MAP.put("thursday", DayOfWeek.THURSDAY);
      DAY_OF_WEEK_MAP.put("friday", DayOfWeek.FRIDAY);
      DAY_OF_WEEK_MAP.put("saturday", DayOfWeek.SATURDAY);
      DAY_OF_WEEK_MAP.put("sunday", DayOfWeek.SUNDAY);
   }
}
