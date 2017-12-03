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

package net.sf.mpxj.mpp;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import org.apache.poi.poifs.filesystem.DirectoryEntry;
import org.apache.poi.poifs.filesystem.DocumentEntry;
import org.apache.poi.poifs.filesystem.DocumentInputStream;

import net.sf.mpxj.DateRange;
import net.sf.mpxj.Day;
import net.sf.mpxj.DayType;
import net.sf.mpxj.EventManager;
import net.sf.mpxj.ProjectCalendar;
import net.sf.mpxj.ProjectCalendarHours;
import net.sf.mpxj.ProjectCalendarWeek;
import net.sf.mpxj.ProjectFile;
import net.sf.mpxj.common.Pair;

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
    * @throws IOException
    */
   @Override public void processCalendarData(DirectoryEntry projectDir, Props projectProps, DocumentInputStreamFactory inputStreamFactory, HashMap<Integer, ProjectCalendar> resourceMap) throws IOException
   {
      DirectoryEntry calDir = (DirectoryEntry) projectDir.getEntry("TBkndCal");

      //MPPUtility.fileHexDump("c:\\temp\\varmeta.txt", new DocumentInputStream (((DocumentEntry)calDir.getEntry("VarMeta"))));

      VarMeta calVarMeta = getCalendarVarMeta(calDir);
      Var2Data calVarData = new Var2Data(calVarMeta, new DocumentInputStream((DocumentEntry) calDir.getEntry("Var2Data")));

      //      System.out.println(calVarMeta);
      //      System.out.println(calVarData);

      FixedMeta calFixedMeta = new FixedMeta(new DocumentInputStream(((DocumentEntry) calDir.getEntry("FixedMeta"))), 10);
      FixedData calFixedData = new FixedData(calFixedMeta, inputStreamFactory.getInstance(calDir, "FixedData"), 12);

      //      System.out.println(calFixedMeta);
      //      System.out.println(calFixedData);

      //      FixedMeta calFixed2Meta = new FixedMeta(new DocumentInputStream(((DocumentEntry) calDir.getEntry("Fixed2Meta"))), 9);
      //      FixedData calFixed2Data = new FixedData(calFixed2Meta, inputStreamFactory.getInstance(calDir, "Fixed2Data"), 48);
      //      System.out.println(calFixed2Meta);
      //      System.out.println(calFixed2Data);

      HashMap<Integer, ProjectCalendar> calendarMap = new HashMap<Integer, ProjectCalendar>();
      int items = calFixedData.getItemCount();
      List<Pair<ProjectCalendar, Integer>> baseCalendars = new LinkedList<Pair<ProjectCalendar, Integer>>();
      byte[] defaultCalendarData = projectProps.getByteArray(Props.DEFAULT_CALENDAR_HOURS);
      ProjectCalendar defaultCalendar = new ProjectCalendar(m_file);
      processCalendarHours(defaultCalendarData, null, defaultCalendar, true);

      EventManager eventManager = m_file.getEventManager();

      for (int loop = 0; loop < items; loop++)
      {
         byte[] fixedData = calFixedData.getByteArrayValue(loop);
         if (fixedData != null && fixedData.length >= 8)
         {
            int offset = 0;

            //
            // Bug 890909, here we ensure that we have a complete 12 byte
            // block before attempting to process the data.
            //
            while (offset + 12 <= fixedData.length)
            {
               Integer calendarID = Integer.valueOf(MPPUtility.getInt(fixedData, offset + getCalendarIDOffset()));
               int baseCalendarID = MPPUtility.getInt(fixedData, offset + getBaseIDOffset());

               if (calendarID.intValue() > 0 && calendarMap.containsKey(calendarID) == false)
               {
                  byte[] varData = calVarData.getByteArray(calendarID, getCalendarDataVarDataType());
                  ProjectCalendar cal;

                  if (baseCalendarID == 0 || baseCalendarID == -1 || baseCalendarID == calendarID.intValue())
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

                     baseCalendars.add(new Pair<ProjectCalendar, Integer>(cal, Integer.valueOf(baseCalendarID)));
                     Integer resourceID = Integer.valueOf(MPPUtility.getInt(fixedData, offset + getResourceIDOffset()));
                     resourceMap.put(resourceID, cal);
                  }

                  cal.setUniqueID(calendarID);

                  if (varData != null)
                  {
                     processCalendarHours(varData, defaultCalendar, cal, baseCalendarID == -1);
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
      Date start;
      long duration;
      Day day;
      List<DateRange> dateRanges = new ArrayList<DateRange>(5);

      for (index = 0; index < 7; index++)
      {
         offset = getCalendarHoursOffset() + (60 * index);
         defaultFlag = data == null ? 1 : MPPUtility.getShort(data, offset);
         day = Day.getInstance(index + 1);

         if (defaultFlag == 1)
         {
            if (isBaseCalendar)
            {
               if (defaultCalendar == null)
               {
                  cal.setWorkingDay(day, DEFAULT_WORKING_WEEK[index]);
                  if (cal.isWorkingDay(day))
                  {
                     hours = cal.addCalendarHours(Day.getInstance(index + 1));
                     hours.addRange(ProjectCalendarWeek.DEFAULT_WORKING_MORNING);
                     hours.addRange(ProjectCalendarWeek.DEFAULT_WORKING_AFTERNOON);
                  }
               }
               else
               {
                  boolean workingDay = defaultCalendar.isWorkingDay(day);
                  cal.setWorkingDay(day, workingDay);
                  if (workingDay)
                  {
                     hours = cal.addCalendarHours(Day.getInstance(index + 1));
                     for (DateRange range : defaultCalendar.getHours(day))
                     {
                        hours.addRange(range);
                     }
                  }
               }
            }
            else
            {
               cal.setWorkingDay(day, DayType.DEFAULT);
            }
         }
         else
         {
            dateRanges.clear();

            periodIndex = 0;
            periodCount = MPPUtility.getShort(data, offset + 2);
            while (periodIndex < periodCount)
            {
               int startOffset = offset + 8 + (periodIndex * 2);
               start = MPPUtility.getTime(data, startOffset);
               int durationOffset = offset + 20 + (periodIndex * 4);
               duration = MPPUtility.getDuration(data, durationOffset);
               Date end = new Date(start.getTime() + duration);
               dateRanges.add(new DateRange(start, end));
               ++periodIndex;
            }

            if (dateRanges.isEmpty())
            {
               cal.setWorkingDay(day, false);
            }
            else
            {
               cal.setWorkingDay(day, true);
               hours = cal.addCalendarHours(Day.getInstance(index + 1));

               for (DateRange range : dateRanges)
               {
                  hours.addRange(range);
               }
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
