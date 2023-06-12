/*
 * file:       ProjectCalendarStructuredTextWriter.java
 * author:     Jon Iles
 * copyright:  (c) Packwood Software 2023
 * date:       07/03/2023
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

package net.sf.mpxj.primavera;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import net.sf.mpxj.DateRange;
import net.sf.mpxj.Day;
import net.sf.mpxj.ProjectCalendar;
import net.sf.mpxj.ProjectCalendarException;
import net.sf.mpxj.ProjectCalendarHours;
import net.sf.mpxj.common.DateHelper;

/**
 * Encapsulates the functionality required to write a ProjectCalendar
 * instance to structured text.
 */
class ProjectCalendarStructuredTextWriter
{
   /**
    * Return te structured text form of the supplied ProjectCalendar instance.
    *
    * @param calendar ProjectCalendar instance
    * @return structured text representation
    */
   public String getCalendarData(ProjectCalendar calendar)
   {
      StructuredTextRecord root = new StructuredTextRecord();
      root.addAttribute(StructuredTextRecord.RECORD_NUMBER_ATTRIBUTE, "0");
      root.addAttribute(StructuredTextRecord.RECORD_NAME_ATTRIBUTE, "CalendarData");
      root.addChild(getDaysOfWeek(calendar));
      root.addChild(getExceptions(calendar));
      return new StructuredTextWriter().write(root);
   }

   /**
    * Generate a StructuredTextRecord for the days of the week.
    *
    * @param calendar ProjectCalendar instance
    * @return StructuredTextRecord instance
    */
   private StructuredTextRecord getDaysOfWeek(ProjectCalendar calendar)
   {
      StructuredTextRecord daysOfWeekRecord = new StructuredTextRecord();
      daysOfWeekRecord.addAttribute(StructuredTextRecord.RECORD_NUMBER_ATTRIBUTE, "0");
      daysOfWeekRecord.addAttribute(StructuredTextRecord.RECORD_NAME_ATTRIBUTE, "DaysOfWeek");

      for (Day day : Day.values())
      {
         StructuredTextRecord dayRecord = new StructuredTextRecord();
         daysOfWeekRecord.addChild(dayRecord);

         dayRecord.addAttribute(StructuredTextRecord.RECORD_NUMBER_ATTRIBUTE, "0");
         dayRecord.addAttribute(StructuredTextRecord.RECORD_NAME_ATTRIBUTE, Integer.toString(day.getValue()));

         // Working days/hours are not inherited between calendars, just exceptions.
         writeHours(dayRecord, calendar.getHours(day));
      }

      return daysOfWeekRecord;
   }

   /**
    * Generate a StructuredTextRecord instance for any exceptions.
    *
    * @param calendar ProjectCalendar instance
    * @return StructuredTextRecord instance
    */
   private StructuredTextRecord getExceptions(ProjectCalendar calendar)
   {
      StructuredTextRecord exceptionsRecord = new StructuredTextRecord();
      exceptionsRecord.addAttribute(StructuredTextRecord.RECORD_NUMBER_ATTRIBUTE, "0");
      exceptionsRecord.addAttribute(StructuredTextRecord.RECORD_NAME_ATTRIBUTE, "Exceptions");

      int exceptionIndex = 0;
      Calendar javaCalendar = DateHelper.popCalendar();
      Set<Date> exceptionDates = new HashSet<>();

      List<ProjectCalendarException> exceptions = net.sf.mpxj.common.ProjectCalendarHelper.getExpandedExceptionsWithWorkWeeks(calendar);
      for (ProjectCalendarException exception : exceptions)
      {
         javaCalendar.setTime(exception.getFromDate());
         while (javaCalendar.getTimeInMillis() < exception.getToDate().getTime())
         {
            Date exceptionDate = javaCalendar.getTime();
            javaCalendar.add(Calendar.DAY_OF_YEAR, 1);

            // Prevent duplicate exception dates being written.
            // P6 will fail to import files with duplicate exceptions.
            if (!exceptionDates.add(exceptionDate))
            {
               continue;
            }

            long dateValue = (long) Math.ceil((double) (DateHelper.getLongFromDate(exceptionDate) - PrimaveraReader.EXCEPTION_EPOCH) / DateHelper.MS_PER_DAY);

            StructuredTextRecord exceptionRecord = new StructuredTextRecord();
            exceptionsRecord.addChild(exceptionRecord);
            exceptionRecord.addAttribute(StructuredTextRecord.RECORD_NUMBER_ATTRIBUTE, "0");
            exceptionRecord.addAttribute(StructuredTextRecord.RECORD_NAME_ATTRIBUTE, Integer.toString(exceptionIndex++));
            exceptionRecord.addAttribute("d", Long.toString(dateValue));

            writeHours(exceptionRecord, exception);
         }
      }

      DateHelper.pushCalendar(javaCalendar);

      return exceptionsRecord;
   }

   /**
    * Populate a parent StructuredTextRecord with working hours.
    *
    * @param parentRecord StructuredTextRecord instance
    * @param hours working hours to add
    */
   private void writeHours(StructuredTextRecord parentRecord, ProjectCalendarHours hours)
   {
      int hoursIndex = 0;
      for (DateRange range : hours)
      {
         StructuredTextRecord hoursRecord = new StructuredTextRecord();
         parentRecord.addChild(hoursRecord);
         hoursRecord.addAttribute(StructuredTextRecord.RECORD_NUMBER_ATTRIBUTE, "0");
         hoursRecord.addAttribute(StructuredTextRecord.RECORD_NAME_ATTRIBUTE, Integer.toString(hoursIndex++));
         hoursRecord.addAttribute("f", m_timeFormat.format(range.getEnd()));
         hoursRecord.addAttribute("s", m_timeFormat.format(range.getStart()));
      }
   }

   private final DateFormat m_timeFormat = new SimpleDateFormat("HH:mm");
}
