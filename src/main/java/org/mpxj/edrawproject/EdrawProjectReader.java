

package org.mpxj.edrawproject;

import java.io.InputStream;
import java.time.DayOfWeek;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.JAXBException;
import javax.xml.parsers.ParserConfigurationException;

import org.mpxj.CostRateTable;
import org.mpxj.CostRateTableEntry;
import org.mpxj.LocalTimeRange;
import org.mpxj.ProjectCalendar;
import org.mpxj.ProjectCalendarException;
import org.mpxj.ProjectCalendarHours;
import org.mpxj.ProjectProperties;
import org.mpxj.Rate;
import org.mpxj.Resource;
import org.mpxj.ResourceType;
import org.mpxj.TimeUnit;
import org.mpxj.common.BooleanHelper;
import org.mpxj.common.LocalDateTimeHelper;
import org.mpxj.edrawproject.schema.Document;
import org.mpxj.mspdi.schema.Project;
import org.xml.sax.SAXException;

import org.mpxj.EventManager;
import org.mpxj.MPXJException;
import org.mpxj.ProjectConfig;
import org.mpxj.ProjectFile;
import org.mpxj.Task;
import org.mpxj.common.UnmarshalHelper;
import org.mpxj.reader.AbstractProjectStreamReader;

public final class EdrawProjectReader extends AbstractProjectStreamReader
{
   @Override public ProjectFile read(InputStream stream) throws MPXJException
   {
      try
      {
         if (CONTEXT == null)
         {
            throw CONTEXT_EXCEPTION;
         }

         m_projectFile = new ProjectFile();
         m_eventManager = m_projectFile.getEventManager();
         m_taskMap = new HashMap<>();

         ProjectConfig config = m_projectFile.getProjectConfig();
         config.setAutoWBS(false);
         config.setAutoResourceUniqueID(false);

         m_projectFile.getProjectProperties().setFileApplication("Edraw Project");
         m_projectFile.getProjectProperties().setFileType("EDPX");

         addListenersToProject(m_projectFile);

         Document document = (Document) UnmarshalHelper.unmarshal(CONTEXT, stream);

         processProperties(document);
         processCalendars(document);
         processResources(document);

         m_projectFile.readComplete();

         return m_projectFile;
      }

      catch (ParserConfigurationException | SAXException | JAXBException ex)
      {
         throw new MPXJException("Failed to parse file", ex);
      }

      finally
      {
         m_projectFile = null;
         m_eventManager = null;
         m_taskMap = null;
      }
   }

   private void processProperties(Document document)
   {
      ProjectProperties props = m_projectFile.getProjectProperties();
      //props.setGUID(document.getDocGuid());
      props.setCreationDate(document.getCreationDate().getV());
      props.setLastSaved(document.getLastSaved().getV());
      props.setAuthor(document.getCreator().getV());
      props.setLastAuthor(document.getModifier().getV());
      props.setMinutesPerDay(document.getMinutesPerDay().getV());
      props.setMinutesPerWeek(document.getMinutesPerWeek().getV());
      props.setDaysPerMonth(document.getDaysPerMonth().getV());
   }

   private void processCalendars(Document document)
   {
      for(Document.Calendars.Calendar xml : document.getCalendars().getCalendar())
      {
         ProjectCalendar calendar = m_projectFile.addCalendar();
         calendar.setUniqueID(xml.getUID());
         calendar.setName(xml.getName());

         for(Document.Calendars.Calendar.WeekDays.WeekDay xmlDay : xml.getWeekDays().getWeekDay())
         {
            // Exceptions are represent both as days with a day type of zero,
            // and with their own data in the calendar. We'll ignore day types
            // of zero for now.
            if (xmlDay.getDayType() == 0)
            {
               continue;
            }

            DayOfWeek day = DAY_OF_WEEK_MAP.get(xmlDay.getDayType());
            boolean workingDay = BooleanHelper.getBoolean(xmlDay.isDayWorking());
            calendar.setWorkingDay(day, workingDay);

            if (workingDay)
            {
               ProjectCalendarHours hours = calendar.addCalendarHours(day);
               for (Document.Calendars.Calendar.WeekDays.WeekDay.WorkingTimes.WorkingTime xmlTime : xmlDay.getWorkingTimes().getWorkingTime())
               {
                  hours.add(new LocalTimeRange(xmlTime.getFromTime(), xmlTime.getToTime()));
               }
            }
         }

         if (xml.getExceptions() == null)
         {
            continue;
         }

         for (Document.Calendars.Calendar.Exceptions.Exception xmlException : xml.getExceptions().getException())
         {
            ProjectCalendarException exception = calendar.addCalendarException(
               xmlException.getTimePeriod().getFromDate().toLocalDate(),
               xmlException.getTimePeriod().getToDate().toLocalDate());

            if (xmlException.getWorkingTimes() == null)
            {
               continue;
            }
            
            List<Document.Calendars.Calendar.Exceptions.Exception.WorkingTimes.WorkingTime> workingTimes = xmlException.getWorkingTimes().getWorkingTime();
            if(workingTimes == null || workingTimes.isEmpty())
            {
               continue;
            }

            for(Document.Calendars.Calendar.Exceptions.Exception.WorkingTimes.WorkingTime workingTime : workingTimes)
            {
               exception.add(new LocalTimeRange(workingTime.getFromTime(), workingTime.getToTime()));
            }
         }
      }
   }

   private void processResources(Document document)
   {
      for (Document.ResourceInfo.Column xml : document.getResourceInfo().getColumn())
      {
         Resource resource = m_projectFile.addResource();

         resource.setUniqueID(xml.getID());
         resource.setName(xml.getName());
         resource.setEmailAddress(xml.getEmail());
         resource.setNotes(xml.getNotes());
         resource.setType(RESOURCE_TYPE_MAP.getOrDefault(xml.getType(), ResourceType.WORK));
         resource.setGroup(xml.getGroup());

         Rate standardRate = new Rate(xml.getCost(), TIME_UNIT_MAP.getOrDefault(xml.getCostUnit(), TimeUnit.HOURS));
         Rate overtimeRate = new Rate(xml.getOvertimeCost(), TIME_UNIT_MAP.getOrDefault(xml.getOvertimeUnit(), TimeUnit.HOURS));

         CostRateTableEntry entry = new CostRateTableEntry(
            LocalDateTimeHelper.START_DATE_NA,
            LocalDateTimeHelper.END_DATE_NA,
            xml.getCostPer(),
            standardRate,
            overtimeRate);

         CostRateTable table = new CostRateTable();
         table.add(entry);
         resource.setCostRateTable(0, table);
      }
   }

   private ProjectFile m_projectFile;
   private EventManager m_eventManager;
   Map<String, Task> m_taskMap;

   private static final Map<Integer, ResourceType> RESOURCE_TYPE_MAP = new HashMap<>();
   static
   {
      RESOURCE_TYPE_MAP.put(Integer.valueOf(0), ResourceType.WORK);
      RESOURCE_TYPE_MAP.put(Integer.valueOf(1), ResourceType.MATERIAL);
      RESOURCE_TYPE_MAP.put(Integer.valueOf(2), ResourceType.COST);
   }

   private static final Map<Integer, TimeUnit> TIME_UNIT_MAP = new HashMap<>();
   static
   {
      TIME_UNIT_MAP.put(Integer.valueOf(0), TimeUnit.MINUTES);
      TIME_UNIT_MAP.put(Integer.valueOf(1), TimeUnit.HOURS);
      TIME_UNIT_MAP.put(Integer.valueOf(2), TimeUnit.DAYS); // Workday
      TIME_UNIT_MAP.put(Integer.valueOf(3), TimeUnit.DAYS);
      TIME_UNIT_MAP.put(Integer.valueOf(4), TimeUnit.WEEKS);
      TIME_UNIT_MAP.put(Integer.valueOf(5), TimeUnit.MONTHS);
   }

   private static final Map<Integer, DayOfWeek> DAY_OF_WEEK_MAP = new HashMap<>();
   static
   {
      DAY_OF_WEEK_MAP.put(Integer.valueOf(1), DayOfWeek.SUNDAY);
      DAY_OF_WEEK_MAP.put(Integer.valueOf(2), DayOfWeek.MONDAY);
      DAY_OF_WEEK_MAP.put(Integer.valueOf(3), DayOfWeek.TUESDAY);
      DAY_OF_WEEK_MAP.put(Integer.valueOf(4), DayOfWeek.WEDNESDAY);
      DAY_OF_WEEK_MAP.put(Integer.valueOf(5), DayOfWeek.THURSDAY);
      DAY_OF_WEEK_MAP.put(Integer.valueOf(6), DayOfWeek.FRIDAY);
      DAY_OF_WEEK_MAP.put(Integer.valueOf(7), DayOfWeek.SATURDAY);
   }

   /**
    * Cached context to minimise construction cost.
    */
   private static JAXBContext CONTEXT;

   /**
    * Note any error occurring during context construction.
    */
   private static JAXBException CONTEXT_EXCEPTION;

   static
   {
      try
      {
         //
         // JAXB RI property to speed up construction
         //
         System.setProperty("com.sun.xml.bind.v2.runtime.JAXBContextImpl.fastBoot", "true");

         //
         // Construct the context
         //
         CONTEXT = JAXBContext.newInstance("org.mpxj.edrawproject.schema", EdrawProjectReader.class.getClassLoader());
      }

      catch (JAXBException ex)
      {
         CONTEXT_EXCEPTION = ex;
         CONTEXT = null;
      }
   }
}
