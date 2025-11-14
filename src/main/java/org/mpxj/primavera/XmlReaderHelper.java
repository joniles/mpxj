/*
 * file:       XmlReaderHelper.java
 * author:     Jon Iles
 * date:       2025-11-12
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

package org.mpxj.primavera;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.mpxj.ActivityCode;
import org.mpxj.ActivityCodeContainer;
import org.mpxj.ActivityCodeValue;
import org.mpxj.FieldContainer;
import org.mpxj.LocalTimeRange;
import org.mpxj.ProjectCalendar;
import org.mpxj.ProjectCalendarException;
import org.mpxj.ProjectCalendarHours;
import org.mpxj.ProjectContext;
import org.mpxj.UserDefinedField;
import org.mpxj.common.BooleanHelper;
import org.mpxj.common.ColorHelper;
import org.mpxj.common.HierarchyHelper;
import org.mpxj.common.LocalDateHelper;
import org.mpxj.common.NumberHelper;
import org.mpxj.primavera.schema.ActivityCodeType;
import org.mpxj.primavera.schema.ActivityCodeTypeType;
import org.mpxj.primavera.schema.CalendarType;
import org.mpxj.primavera.schema.UDFAssignmentType;
import org.mpxj.primavera.schema.WorkTimeType;

/**
 * Common methods for processing PMXML files.
 */
class XmlReaderHelper
{
   /**
    * Process activity code definitions.
    *
    * @param types list of activity code types
    * @param typeValues list of activity code values
    */
   public static void processActivityCodeDefinitions(ProjectContext context, List<ActivityCodeTypeType> types, List<ActivityCodeType> typeValues)
   {
      ActivityCodeContainer container = context.getActivityCodes();
      Map<Integer, ActivityCode> map = new HashMap<>();

      for (ActivityCodeTypeType type : types)
      {
         ActivityCode code = new ActivityCode.Builder(context)
            .uniqueID(type.getObjectId())
            .scope(ActivityCodeScopeHelper.getInstanceFromXml(type.getScope()))
            .scopeEpsUniqueID(type.getEPSObjectId())
            .scopeProjectUniqueID(type.getProjectObjectId())
            .sequenceNumber(type.getSequenceNumber())
            .name(type.getName())
            .secure(BooleanHelper.getBoolean(type.isIsSecureCode()))
            .maxLength(type.getLength())
            .build();
         container.add(code);
         map.put(code.getUniqueID(), code);
      }

      typeValues = HierarchyHelper.sortHierarchy(typeValues, ActivityCodeType::getObjectId, ActivityCodeType::getParentObjectId);
      for (ActivityCodeType typeValue : typeValues)
      {
         ActivityCode code = map.get(typeValue.getCodeTypeObjectId());
         if (code != null)
         {
            ActivityCodeValue value = new ActivityCodeValue.Builder(context)
               .activityCode(code)
               .uniqueID(typeValue.getObjectId())
               .sequenceNumber(typeValue.getSequenceNumber())
               .name(typeValue.getCodeValue())
               .description(typeValue.getDescription())
               .color(ColorHelper.parseHtmlColor(typeValue.getColor()))
               .parentValue(code.getValueByUniqueID(typeValue.getParentObjectId()))
               .build();
            code.addValue(value);
         }
      }
   }

   /**
    * Process project calendars.
    *
    * @param calendars list of calendar data
    */
   public static void processCalendars(ProjectContext context, List<CalendarType> calendars)
   {
      //
      // First pass: read calendar definitions
      //
      Map<ProjectCalendar, Integer> baseCalendarMap = new HashMap<>();
      for (CalendarType row : calendars)
      {
         ProjectCalendar calendar = processCalendar(context, row);
         Integer baseCalendarID = row.getBaseCalendarObjectId();
         if (baseCalendarID != null)
         {
            baseCalendarMap.put(calendar, baseCalendarID);
         }
      }

      //
      // Second pass: create calendar hierarchy
      //
      for (Map.Entry<ProjectCalendar, Integer> entry : baseCalendarMap.entrySet())
      {
         ProjectCalendar baseCalendar = context.getCalendars().getByUniqueID(entry.getValue());
         if (baseCalendar != null)
         {
            entry.getKey().setParent(baseCalendar);
         }
      }
   }

   /**
    * Process data for an individual calendar.
    *
    * @param row calendar data
    * @return ProjectCalendar instance
    */
   public static ProjectCalendar processCalendar(ProjectContext context, CalendarType row)
   {
      ProjectCalendar calendar = context.getCalendars().add();

      Integer id = row.getObjectId();
      calendar.setUniqueID(id);
      calendar.setName(row.getName());
      calendar.setType(CalendarTypeHelper.getInstanceFromXml(row.getType()));
      calendar.setProjectUniqueID(row.getProjectObjectId());
      calendar.setPersonal(BooleanHelper.getBoolean(row.isIsPersonal()));

      if (BooleanHelper.getBoolean(row.isIsDefault()) && context.getCalendars().getDefaultCalendarUniqueID() == null)
      {
         calendar.setDefault();
      }

      Map<DayOfWeek, CalendarType.StandardWorkWeek.StandardWorkHours> hoursMap = new HashMap<>();
      CalendarType.StandardWorkWeek stdWorkWeek = row.getStandardWorkWeek();
      if (stdWorkWeek != null)
      {
         for (CalendarType.StandardWorkWeek.StandardWorkHours hours : stdWorkWeek.getStandardWorkHours())
         {
            hoursMap.put(DAY_MAP.get(hours.getDayOfWeek()), hours);
         }
      }

      for (DayOfWeek day : DayOfWeek.values())
      {
         // If we don't have an entry for a day, use default values
         CalendarType.StandardWorkWeek.StandardWorkHours hours = hoursMap.get(day);
         if (hours == null)
         {
            calendar.setWorkingDay(day, day != DayOfWeek.SATURDAY && day != DayOfWeek.SUNDAY);
            if (calendar.isWorkingDay(day))
            {
               calendar.addCalendarHours(day).add(ProjectCalendarHelper.getDefaultCalendarHours());
            }
            continue;
         }

         ProjectCalendarHours calendarHours = calendar.addCalendarHours(day);
         List<WorkTimeType> workTime = hours.getWorkTime();
         if (workTime.isEmpty() || workTime.get(0) == null)
         {
            calendar.setWorkingDay(day, false);
         }
         else
         {
            calendar.setWorkingDay(day, true);
            for (WorkTimeType work : workTime)
            {
               if (work != null)
               {
                  calendarHours.add(new LocalTimeRange(work.getStart(), getEndTime(work.getFinish())));
               }
            }
         }
      }

      CalendarType.HolidayOrExceptions hoe = row.getHolidayOrExceptions();
      if (hoe != null)
      {
         for (CalendarType.HolidayOrExceptions.HolidayOrException ex : hoe.getHolidayOrException())
         {
            LocalDate startDate = LocalDateHelper.getLocalDate(ex.getDate());
            LocalDate endDate = LocalDateHelper.getLocalDate(ex.getDate());
            ProjectCalendarException pce = calendar.addCalendarException(startDate, endDate);

            List<WorkTimeType> workTime = ex.getWorkTime();

            // Special case: a single entry for 00:00-23:59 is treated by P6 as a non-working day
            if (workTime.size() == 1)
            {
               WorkTimeType work = workTime.get(0);
               if (work == null || (LocalTime.MIDNIGHT.equals(work.getStart()) && NON_WORKING_END_TIME.equals(work.getFinish())))
               {
                  continue;
               }
            }

            for (WorkTimeType work : workTime)
            {
               if (work != null && work.getStart() != null && work.getFinish() != null)
               {
                  pce.add(new LocalTimeRange(work.getStart(), getEndTime(work.getFinish())));
               }
            }
         }
      }

      ProjectCalendarHelper.ensureWorkingTime(calendar);

      //
      // Try and extract minutes per period from the calendar row
      //
      Double rowHoursPerDay = row.getHoursPerDay();
      Double rowHoursPerWeek = row.getHoursPerWeek();
      Double rowHoursPerMonth = row.getHoursPerMonth();
      Double rowHoursPerYear = row.getHoursPerYear();

      calendar.setCalendarMinutesPerDay(Integer.valueOf((int) (NumberHelper.getDouble(rowHoursPerDay) * 60)));
      calendar.setCalendarMinutesPerWeek(Integer.valueOf((int) (NumberHelper.getDouble(rowHoursPerWeek) * 60)));
      calendar.setCalendarMinutesPerMonth(Integer.valueOf((int) (NumberHelper.getDouble(rowHoursPerMonth) * 60)));
      calendar.setCalendarMinutesPerYear(Integer.valueOf((int) (NumberHelper.getDouble(rowHoursPerYear) * 60)));

      //
      // If we're missing any of these figures, generate them.
      // Note that P6 allows users to enter arbitrary hours per period,
      // as far as I can see they aren't validated to see if they make sense,
      // so the figures here won't necessarily match what you'd see in P6.
      //
      if (rowHoursPerDay == null || rowHoursPerWeek == null || rowHoursPerMonth == null || rowHoursPerYear == null)
      {
         int minutesPerWeek = 0;
         int workingDays = 0;

         for (DayOfWeek day : DayOfWeek.values())
         {
            ProjectCalendarHours hours = calendar.getCalendarHours(day);
            if (hours == null)
            {
               continue;
            }

            if (!hours.isEmpty())
            {
               ++workingDays;
               for (LocalTimeRange range : hours)
               {
                  minutesPerWeek += (range.getDurationAsMilliseconds() / (1000 * 60));
               }
            }
         }

         int minutesPerDay = minutesPerWeek / workingDays;
         int minutesPerMonth = minutesPerWeek * 4;
         int minutesPerYear = minutesPerMonth * 12;

         if (rowHoursPerDay == null)
         {
            calendar.setCalendarMinutesPerDay(Integer.valueOf(minutesPerDay));
         }

         if (rowHoursPerWeek == null)
         {
            calendar.setCalendarMinutesPerWeek(Integer.valueOf(minutesPerWeek));
         }

         if (rowHoursPerMonth == null)
         {
            calendar.setCalendarMinutesPerMonth(Integer.valueOf(minutesPerMonth));
         }

         if (rowHoursPerYear == null)
         {
            calendar.setCalendarMinutesPerYear(Integer.valueOf(minutesPerYear));
         }
      }

      context.getEventManager().fireCalendarReadEvent(calendar);

      return calendar;
   }

   /**
    * The end of a Primavera time range finishes on the last minute
    * of the period, so a range of 12:00 -> 13:00 is represented by
    * Primavera as 12:00 -> 12:59.
    *
    * @param date Primavera end time
    * @return date MPXJ end time
    */
   public static LocalTime getEndTime(LocalTime date)
   {
      return date.plusMinutes(1);
   }

   /**
    * Process UDFs for a specific object.
    *
    * @param mpxj field container
    * @param udfs UDF values
    */
   public static void populateUserDefinedFieldValues(ProjectContext context, FieldContainer mpxj, List<UDFAssignmentType> udfs)
   {
      for (UDFAssignmentType udf : udfs)
      {
         UserDefinedField fieldType = context.getUserDefinedFields().getByUniqueID(Integer.valueOf(udf.getTypeObjectId()));
         if (fieldType != null)
         {
            mpxj.set(fieldType, getUdfValue(udf));
         }
      }
   }

   /**
    * Retrieve the value of a UDF.
    *
    * @param udf UDF value holder
    * @return UDF value
    */
   private static Object getUdfValue(UDFAssignmentType udf)
   {
      if (udf.getCostValue() != null)
      {
         return udf.getCostValue();
      }

      if (udf.getDoubleValue() != null)
      {
         return udf.getDoubleValue();
      }

      if (udf.getFinishDateValue() != null)
      {
         return udf.getFinishDateValue();
      }

      if (udf.getIndicatorValue() != null)
      {
         return udf.getIndicatorValue();
      }

      if (udf.getIntegerValue() != null)
      {
         return udf.getIntegerValue();
      }

      if (udf.getStartDateValue() != null)
      {
         return udf.getStartDateValue();
      }

      if (udf.getTextValue() != null)
      {
         return udf.getTextValue();
      }

      return null;
   }

   private static final Map<String, DayOfWeek> DAY_MAP = new HashMap<>();
   static
   {
      // Current PMXML schema
      DAY_MAP.put("Monday", DayOfWeek.MONDAY);
      DAY_MAP.put("Tuesday", DayOfWeek.TUESDAY);
      DAY_MAP.put("Wednesday", DayOfWeek.WEDNESDAY);
      DAY_MAP.put("Thursday", DayOfWeek.THURSDAY);
      DAY_MAP.put("Friday", DayOfWeek.FRIDAY);
      DAY_MAP.put("Saturday", DayOfWeek.SATURDAY);
      DAY_MAP.put("Sunday", DayOfWeek.SUNDAY);

      // Older (6.2?) schema
      DAY_MAP.put("1", DayOfWeek.SUNDAY);
      DAY_MAP.put("2", DayOfWeek.MONDAY);
      DAY_MAP.put("3", DayOfWeek.TUESDAY);
      DAY_MAP.put("4", DayOfWeek.WEDNESDAY);
      DAY_MAP.put("5", DayOfWeek.THURSDAY);
      DAY_MAP.put("6", DayOfWeek.FRIDAY);
      DAY_MAP.put("7", DayOfWeek.SATURDAY);
   }

   private static final LocalTime NON_WORKING_END_TIME = LocalTime.of(23, 59);
}
