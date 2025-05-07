/*
 * file:       AbstractCalendarFactory.java
 * author:     Jon Iles
 * copyright:  (c) Packwood Software 2017
 * date:       2017-10-04
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

package org.mpxj.mpp;

import java.io.IOException;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Stream;

import org.mpxj.common.ByteArrayHelper;
import org.mpxj.common.DayOfWeekHelper;
import org.mpxj.LocalTimeRange;
import org.apache.poi.poifs.filesystem.DirectoryEntry;
import org.apache.poi.poifs.filesystem.DocumentEntry;
import org.apache.poi.poifs.filesystem.DocumentInputStream;

import java.time.DayOfWeek;
import org.mpxj.DayType;
import org.mpxj.EventManager;
import org.mpxj.ProjectCalendar;
import org.mpxj.ProjectCalendarDays;
import org.mpxj.ProjectCalendarHours;
import org.mpxj.ProjectFile;
import org.mpxj.common.Pair;

/**
 * Shared code used to read calendar data from MPP files.
 */
abstract class AbstractCalendarFactory implements CalendarFactory
{
   /**
    * Constructor.
    *
    * @param file parent ProjectFile instance
    */
   public AbstractCalendarFactory(ProjectFile file)
   {
      m_file = file;
   }

   /**
    * The format of the calendar data is a 4 byte header followed
    * by 7x 60 byte blocks, one for each day of the week. Optionally
    * following this is a set of 64 byte blocks representing exceptions
    * to the calendar.
    *
    * @param projectDir project data directory in the MPP file
    * @param projectProps project properties
    * @param inputStreamFactory input stream factory
    * @param resourceMap map of resources to calendars
    */
   @Override public void processCalendarData(DirectoryEntry projectDir, Props projectProps, DocumentInputStreamFactory inputStreamFactory, HashMap<Integer, ProjectCalendar> resourceMap) throws IOException
   {
      DirectoryEntry calDir = (DirectoryEntry) projectDir.getEntry("TBkndCal");

      //MPPUtility.fileHexDump("c:\\temp\\varmeta.txt", new DocumentInputStream (((DocumentEntry)calDir.getEntry("VarMeta"))));

      VarMeta calVarMeta = getCalendarVarMeta(calDir);
      Var2Data calVarData = new Var2Data(m_file, calVarMeta, new DocumentInputStream((DocumentEntry) calDir.getEntry("Var2Data")));

      //      System.out.println(calVarMeta);
      //      System.out.println(calVarData);

      FixedMeta calFixedMeta = new FixedMeta(new DocumentInputStream(((DocumentEntry) calDir.getEntry("FixedMeta"))), 10);
      FixedData calFixedData = new FixedData(calFixedMeta, inputStreamFactory.getInstance(calDir, "FixedData"), 12);

      //      System.out.println(calFixedMeta);
      //      System.out.println(calFixedData);

      FixedData calFixed2Data = null;

      if (calDir.hasEntry("Fixed2Meta"))
      {
         FixedMeta calFixed2Meta = new FixedMeta(new DocumentInputStream(((DocumentEntry) calDir.getEntry("Fixed2Meta"))), 9);
         calFixed2Data = new FixedData(calFixed2Meta, inputStreamFactory.getInstance(calDir, "Fixed2Data"), 48);
      }

      //      System.out.println(calFixed2Meta);
      //      System.out.println(calFixed2Data);

      HashMap<Integer, ProjectCalendar> calendarMap = new HashMap<>();
      int items = calFixedData.getItemCount();
      List<Pair<ProjectCalendar, Integer>> baseCalendars = new ArrayList<>();
      byte[] defaultCalendarData = projectProps.getByteArray(Props.DEFAULT_CALENDAR_HOURS);
      ProjectCalendar defaultCalendar = new ProjectCalendar(m_file);
      processCalendarHours(defaultCalendarData, null, defaultCalendar, true);

      EventManager eventManager = m_file.getEventManager();

      for (int loop = 0; loop < items; loop++)
      {
         byte[] fixedData = calFixedData.getByteArrayValue(loop);
         byte[] fixedData2 = calFixed2Data == null ? null : calFixed2Data.getByteArrayValue(loop);

         if (fixedData != null && fixedData.length >= 8)
         {
            int offset = 0;

            //
            // Bug 890909, here we ensure that we have a complete 12 byte
            // block before attempting to process the data.
            //
            while (offset + 12 <= fixedData.length)
            {
               Integer calendarID = Integer.valueOf(ByteArrayHelper.getInt(fixedData, offset + getCalendarIDOffset()));
               int baseCalendarID = ByteArrayHelper.getInt(fixedData, offset + getBaseIDOffset());

               if (calendarID.intValue() > 0 && !calendarMap.containsKey(calendarID))
               {
                  byte[] varData = calVarData.getByteArray(calendarID, getCalendarDataVarDataType());
                  ProjectCalendar cal;

                  if (baseCalendarID <= 0 || baseCalendarID == calendarID.intValue())
                  {
                     if (varData != null || defaultCalendarData != null)
                     {
                        cal = m_file.addCalendar();
                        if (varData == null)
                        {
                           varData = defaultCalendarData;
                        }
                     }
                     else
                     {
                        cal = m_file.addDefaultBaseCalendar();
                     }

                     cal.setName(calVarData.getUnicodeString(calendarID, getCalendarNameVarDataType()));

                     // In theory, base calendars should not have a resource ID attached to them.
                     // In practice, I've seen a few sample files where this is the case.
                     // As long as the resource ID isn't already linked to a calendar, we'll
                     // use the resource ID.
                     Integer resourceID = Integer.valueOf(ByteArrayHelper.getInt(fixedData, offset + getResourceIDOffset()));
                     if (resourceID.intValue() > 0 && !resourceMap.containsKey(resourceID))
                     {
                        resourceMap.put(resourceID, cal);
                     }
                  }
                  else
                  {
                     if (varData != null)
                     {
                        cal = m_file.addCalendar();
                     }
                     else
                     {
                        cal = m_file.addDefaultDerivedCalendar();
                     }

                     baseCalendars.add(new Pair<>(cal, Integer.valueOf(baseCalendarID)));
                     Integer resourceID = Integer.valueOf(ByteArrayHelper.getInt(fixedData, offset + getResourceIDOffset()));
                     resourceMap.put(resourceID, cal);
                  }

                  cal.setUniqueID(calendarID);
                  cal.setGUID(MPPUtility.getGUID(fixedData2, 0));

                  if (varData == null)
                  {
                     if (baseCalendarID <= 0)
                     {
                        Stream.of(DayOfWeek.values()).forEach(cal::addCalendarHours);
                     }
                  }
                  else
                  {
                     processCalendarHours(varData, defaultCalendar, cal, baseCalendarID <= 0);
                     processCalendarExceptions(varData, cal);
                  }

                  calendarMap.put(calendarID, cal);
                  eventManager.fireCalendarReadEvent(cal);
               }

               offset += 12;
            }
         }
      }

      updateBaseCalendarNames(baseCalendars, calendarMap);
      ProjectCalendar projectDefaultCalendar = m_file.getCalendars().getByName(projectProps.getUnicodeString(Props.DEFAULT_CALENDAR_NAME));
      if (projectDefaultCalendar == null)
      {
         projectDefaultCalendar = m_file.getCalendars().findOrCreateDefaultCalendar();
      }
      m_file.getProjectProperties().setDefaultCalendar(projectDefaultCalendar);
   }

   /**
    * For a given set of calendar data, this method sets the working
    * day status for each day, and if present, sets the hours for that
    * day.
    *
    * NOTE: MPP14 defines the concept of working weeks. MPXJ does not
    * currently support this, and thus we only read the working hours
    * for the default working week.
    *
    * @param data calendar data block
    * @param defaultCalendar calendar to use for default values
    * @param cal calendar instance
    * @param isBaseCalendar true if this is a base calendar
    */
   private void processCalendarHours(byte[] data, ProjectCalendar defaultCalendar, ProjectCalendar cal, boolean isBaseCalendar)
   {
      // Dump out the calendar related data and fields.
      //MPPUtility.dataDump(data, true, false, false, false, true, false, true);

      int offset;
      ProjectCalendarHours hours;
      int periodIndex;
      int index;
      int defaultFlag;
      int periodCount;
      long duration;
      DayOfWeek day;
      List<LocalTimeRange> dateRanges = new ArrayList<>(5);

      for (index = 0; index < 7; index++)
      {
         offset = getCalendarHoursOffset() + (60 * index);
         defaultFlag = data == null ? 1 : ByteArrayHelper.getShort(data, offset);
         day = DayOfWeekHelper.getInstance(index + 1);

         if (defaultFlag == 1)
         {
            if (isBaseCalendar)
            {
               hours = cal.addCalendarHours(day);
               if (defaultCalendar == null)
               {
                  cal.setWorkingDay(day, DEFAULT_WORKING_WEEK[index]);
                  if (cal.isWorkingDay(day))
                  {
                     hours.add(ProjectCalendarDays.DEFAULT_WORKING_MORNING);
                     hours.add(ProjectCalendarDays.DEFAULT_WORKING_AFTERNOON);
                  }
               }
               else
               {
                  boolean workingDay = defaultCalendar.isWorkingDay(day);
                  cal.setWorkingDay(day, workingDay);
                  if (workingDay)
                  {
                     hours.addAll(defaultCalendar.getHours(day));
                  }
               }
            }
            else
            {
               cal.setCalendarDayType(day, DayType.DEFAULT);
            }
         }
         else
         {
            dateRanges.clear();

            periodIndex = 0;
            periodCount = ByteArrayHelper.getShort(data, offset + 2);
            while (periodIndex < periodCount)
            {
               int startOffset = offset + 8 + (periodIndex * 2);
               LocalTime start = MPPUtility.getTime(data, startOffset);
               int durationOffset = offset + 20 + (periodIndex * 4);
               duration = MPPUtility.getDuration(data, durationOffset);
               LocalTime end = start.plus(duration, ChronoUnit.MILLIS);
               dateRanges.add(new LocalTimeRange(start, end));
               ++periodIndex;
            }

            if (dateRanges.isEmpty())
            {
               if (isBaseCalendar)
               {
                  cal.addCalendarHours(DayOfWeekHelper.getInstance(index + 1));
               }
               cal.setWorkingDay(day, false);
            }
            else
            {
               cal.setWorkingDay(day, true);
               hours = cal.addCalendarHours(DayOfWeekHelper.getInstance(index + 1));
               hours.addAll(dateRanges);
            }
         }
      }
   }

   /**
    * The way calendars are stored in an MPP14 file means that there
    * can be forward references between the base calendar unique ID for a
    * derived calendar, and the base calendar itself. To get around this,
    * we initially populate the base calendar name attribute with the
    * base calendar unique ID, and now in this method we can convert those
    * ID values into the correct names.
    *
    * @param baseCalendars list of calendars and base calendar IDs
    * @param map map of calendar ID values and calendar objects
    */
   private void updateBaseCalendarNames(List<Pair<ProjectCalendar, Integer>> baseCalendars, HashMap<Integer, ProjectCalendar> map)
   {
      for (Pair<ProjectCalendar, Integer> pair : baseCalendars)
      {
         ProjectCalendar cal = pair.getFirst();
         Integer baseCalendarID = pair.getSecond();
         ProjectCalendar baseCal = map.get(baseCalendarID);
         if (baseCal != null && baseCal.getName() != null)
         {
            cal.setParent(baseCal);
         }
         else
         {
            // Remove invalid calendar to avoid serious problems later.
            m_file.removeCalendar(cal);
         }
      }
   }

   /**
    * Retrieve the Calendar ID offset.
    *
    * @return Calendar ID offset
    */
   protected abstract int getCalendarIDOffset();

   /**
    * Retrieve the Base Calendar ID offset.
    *
    * @return BaseCalendar ID offset
    */
   protected abstract int getBaseIDOffset();

   /**
    * Retrieve the Resource ID offset.
    *
    * @return Resource ID offset
    */
   protected abstract int getResourceIDOffset();

   /**
    * Retrieve the calendar VarMeta data.
    *
    * @param directory calendar directory
    * @return VarMeta instance
    */
   protected abstract VarMeta getCalendarVarMeta(DirectoryEntry directory) throws IOException;

   /**
    * Retrieve the offset to the start of each calendar hours block.
    *
    * @return calendar hours offset
    */
   protected abstract int getCalendarHoursOffset();

   /**
    * Retrieve the VarData type containing the calendar name.
    *
    * @return VarData type
    */
   protected abstract Integer getCalendarNameVarDataType();

   /**
    * Retrieve the VarData type containing the calendar data.
    *
    * @return VarData type
    */
   protected abstract Integer getCalendarDataVarDataType();

   /**
    * This method extracts any exceptions associated with a calendar.
    *
    * @param data calendar data block
    * @param cal calendar instance
    */
   protected abstract void processCalendarExceptions(byte[] data, ProjectCalendar cal);

   /**
    * Default working week.
    */
   private static final boolean[] DEFAULT_WORKING_WEEK =
   {
      false,
      true,
      true,
      true,
      true,
      true,
      false
   };

   private final ProjectFile m_file;
}
