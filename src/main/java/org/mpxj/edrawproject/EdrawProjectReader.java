

package org.mpxj.edrawproject;

import java.io.InputStream;
import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.JAXBException;
import javax.xml.parsers.ParserConfigurationException;

import org.mpxj.ChildTaskContainer;
import org.mpxj.CostRateTable;
import org.mpxj.CostRateTableEntry;
import org.mpxj.Duration;
import org.mpxj.LocalTimeRange;
import org.mpxj.Priority;
import org.mpxj.ProjectCalendar;
import org.mpxj.ProjectCalendarException;
import org.mpxj.ProjectCalendarHours;
import org.mpxj.ProjectProperties;
import org.mpxj.Rate;
import org.mpxj.Resource;
import org.mpxj.ResourceAssignment;
import org.mpxj.ResourceType;
import org.mpxj.TaskMode;
import org.mpxj.TimeUnit;
import org.mpxj.common.BooleanHelper;
import org.mpxj.common.HierarchyHelper;
import org.mpxj.common.LocalDateTimeHelper;
import org.mpxj.common.NumberHelper;
import org.mpxj.edrawproject.schema.Document;
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
         config.setAutoTaskUniqueID(false);
         config.setAutoTaskID(false);
         config.setAutoWBS(false);

         m_projectFile.getProjectProperties().setFileApplication("Edraw Project");
         m_projectFile.getProjectProperties().setFileType("EDPX");

         addListenersToProject(m_projectFile);

         Document document = (Document) UnmarshalHelper.unmarshal(CONTEXT, stream);

         processProperties(document);
         processCalendars(document);
         processResources(document);
         processTasks(document);

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
      document.getCalendars().getCalendar().forEach(this::processCalendar);
   }

   private void processCalendar(Document.Calendars.Calendar xml)
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
         return;
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

   private void processResources(Document document)
   {
      document.getResourceInfo().getColumn().forEach(this::processResource);
   }

   private void processResource(Document.ResourceInfo.Column xml)
   {
      Resource resource = m_projectFile.addResource();

      resource.setUniqueID(xml.getID());
      resource.setName(xml.getName());
      resource.setEmailAddress(xml.getEmail());
      resource.setNotes(xml.getNotes());
      resource.setType(RESOURCE_TYPE_MAP.getOrDefault(xml.getType(), ResourceType.WORK));
      resource.setGroup(xml.getGroup());

      Rate standardRate = new Rate(xml.getCost(), RATE_TIME_UNIT_MAP.getOrDefault(xml.getCostUnit(), TimeUnit.HOURS));
      Rate overtimeRate = new Rate(xml.getOvertimeCost(), RATE_TIME_UNIT_MAP.getOrDefault(xml.getOvertimeUnit(), TimeUnit.HOURS));

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

   private void processTasks(Document document)
   {
      List<Document.TaskList.Task> tasks = HierarchyHelper.sortHierarchy(document.getTaskList().getTask(), Document.TaskList.Task::getID, Document.TaskList.Task::getParentID, Comparator.comparing(Document.TaskList.Task::getRowID));
      tasks.forEach(this::processTask);
   }

   private void processTask(Document.TaskList.Task xml)
   {
      ChildTaskContainer parent = m_projectFile.getTaskByUniqueID(xml.getParentID());
      Task task = (parent == null ? m_projectFile : parent).addTask();
      task.setMilestone(xml.isMilestone());
      task.setCritical(xml.isCriticalPath());
      task.setUniqueID(xml.getID());
      task.setID(xml.getRowID());
      task.setBaselineStart(xml.getDateBaseStart());
      task.setBaselineFinish(xml.getDateBaseFinish());
      task.setBaselineCost(xml.getBaselineCost());
      task.setDuration(getDuration(xml.getDurationSecs(), xml.getDurationUnits()));
      task.setLateStart(xml.getDateLateStart());
      task.setActualStart(getDateFromLong(xml.getActualStart()));
      task.setWork(Duration.getInstance(xml.getWork(), TimeUnit.HOURS));
      task.setCost(xml.getCost());
      task.setStart(xml.getDateStart());
      task.setName(xml.getName());
      task.setActualFinish(getDateFromLong(xml.getActualFinish()));
      task.setLateFinish(xml.getDateLateFinish());
      task.setPriority(PRIORITY_MAP.get(xml.getPriority()));
      task.setFinish(xml.getDateFinish());
      task.setWBS(xml.getWbs());
      task.setNotes(xml.getNotes());
      task.setPercentageComplete(NumberHelper.getDouble(xml.getPercent()) * 100.0);
      task.setRemainingCost(xml.getRemainingCost());
      task.setTaskMode(BooleanHelper.getBoolean(xml.isManual()) ? TaskMode.MANUALLY_SCHEDULED : TaskMode.AUTO_SCHEDULED);

      // Not sure what this is used for
      //StartText

      // You would expect separate records to be kept for
      // each number baseline, but that doesn't appear to
      // be the case
      //BaseLineNumber

      // ActualDuration is only populated in edraw for completed tasks
      //ActualDuration

      // Level is the outline level - calculated by MPXJ
      //Level

      // The Start, Finish, and Duration attributes appear to
      // contain the same values as the manual attributes below.
      //DateManualStart
      //DateManualFinish
      //ManualDurationSecs

      processResourceAssignments(task, xml.getResourceList());
   }

   private void processResourceAssignments(Task task, Document.TaskList.Task.ResourceList xmlResourceList)
   {
      if (xmlResourceList == null)
      {
         return;
      }

      List<Document.TaskList.Task.ResourceList.Resource> xmlResources = xmlResourceList.getResource();
      if (xmlResources == null || xmlResources.isEmpty())
      {
         return;
      }

      for (Document.TaskList.Task.ResourceList.Resource xml : xmlResources)
      {
         Resource resource = m_projectFile.getResourceByUniqueID(xml.getID());
         if (resource == null)
         {
            continue;
         }

         ResourceAssignment assignment = task.addResourceAssignment(resource);
         assignment.setUnits(NumberHelper.getDouble(xml.getPercent()) * 100.0);
         assignment.setWork(getDuration(xml.getWorkSecs(), 5));
      }
   }

   private Duration getDuration(Long seconds, Integer units)
   {
      // Note: durations are rounded to 1 decimal place,
      // so 1 day expressed as months should be 0.05mo
      // but is actually shown in edraw as 0.1mo
      if (seconds == null || seconds.longValue() == -1 || seconds.longValue() == 4294967295L)
      {
         return null;
      }

      double durationValue = seconds.doubleValue();
      TimeUnit durationUnits = TimeUnit.HOURS;

      switch (units.intValue())
      {
         case 6:
         {
            // Minutes
            durationValue /= 60.0;
            durationUnits = TimeUnit.MINUTES;
            break;
         }

         case 5:
         {
            // Hours
            durationValue /= (60.0 * 60.0);
            durationUnits = TimeUnit.HOURS;
            break;
         }

         case 7:
         {
            // Workday
            durationValue /= (60.0 * m_projectFile.getProjectProperties().getMinutesPerDay());
            durationUnits = TimeUnit.DAYS;
            break;
         }

         case 10:
         {
            // Day
            durationValue /= (60.0 * m_projectFile.getProjectProperties().getMinutesPerDay());
            durationUnits = TimeUnit.DAYS;
            break;
         }

         case 8:
         {
            // Weeks
            durationValue /= (60.0 * m_projectFile.getProjectProperties().getMinutesPerWeek());
            durationUnits = TimeUnit.WEEKS;
            break;
         }

         case 9:
         {
            // Months
            durationValue /= (60.0 * m_projectFile.getProjectProperties().getMinutesPerDay() * m_projectFile.getProjectProperties().getDaysPerMonth());
            durationUnits = TimeUnit.MONTHS;
            break;
         }

         default:
         {
            throw new IllegalArgumentException("Unsupported duration units: " + units);
         }
      }

      return Duration.getInstance(durationValue, durationUnits);
   }

   private LocalDateTime getDateFromLong(Long value)
   {
      if (value == null || value.longValue() == 0)
      {
         return null;
      }

      return EPOCH.plusSeconds(value.longValue());
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

   private static final Map<Integer, TimeUnit> RATE_TIME_UNIT_MAP = new HashMap<>();
   static
   {
      RATE_TIME_UNIT_MAP.put(Integer.valueOf(0), TimeUnit.MINUTES);
      RATE_TIME_UNIT_MAP.put(Integer.valueOf(1), TimeUnit.HOURS);
      RATE_TIME_UNIT_MAP.put(Integer.valueOf(2), TimeUnit.DAYS); // Workday
      RATE_TIME_UNIT_MAP.put(Integer.valueOf(3), TimeUnit.DAYS);
      RATE_TIME_UNIT_MAP.put(Integer.valueOf(4), TimeUnit.WEEKS);
      RATE_TIME_UNIT_MAP.put(Integer.valueOf(5), TimeUnit.MONTHS);
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

   private static final Map<Integer, Priority> PRIORITY_MAP = new HashMap<>();
   static
   {
      PRIORITY_MAP.put(Integer.valueOf(1), Priority.getInstance(Priority.HIGHEST));
      PRIORITY_MAP.put(Integer.valueOf(2), Priority.getInstance(Priority.HIGH));
      PRIORITY_MAP.put(Integer.valueOf(3), Priority.getInstance(Priority.MEDIUM));
      PRIORITY_MAP.put(Integer.valueOf(4), Priority.getInstance(Priority.LOW));
      PRIORITY_MAP.put(Integer.valueOf(5), Priority.getInstance(Priority.LOWEST));
   }

   private static final LocalDateTime EPOCH = LocalDateTime.of(1970, 1, 1, 1, 0);

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
