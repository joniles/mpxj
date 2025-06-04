/*
 * file:       MSPDIReader.java
 * author:     Jon Iles
 * copyright:  (c) Packwood Software 2005
 * date:       30/12/2005
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

package org.mpxj.mspdi;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.math.BigInteger;
import java.nio.charset.Charset;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Collections;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.JAXBElement;
import jakarta.xml.bind.JAXBException;
import javax.xml.parsers.ParserConfigurationException;

import org.mpxj.CalendarType;
import org.mpxj.ChildTaskContainer;
import org.mpxj.HasCharset;
import org.mpxj.UserDefinedField;
import org.mpxj.common.DayOfWeekHelper;
import org.mpxj.LocalDateRange;
import org.mpxj.LocalTimeRange;
import org.mpxj.TimephasedCost;
import org.mpxj.TimephasedCostContainer;
import org.mpxj.TimephasedWorkContainer;
import org.mpxj.common.DefaultTimephasedCostContainer;
import org.mpxj.common.LocalDateHelper;
import org.mpxj.common.LocalDateTimeHelper;
import org.mpxj.common.ObjectSequence;
import org.mpxj.mpp.MPPTimephasedBaselineCostNormaliser;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import org.mpxj.Availability;
import org.mpxj.AvailabilityTable;
import org.mpxj.CostRateTable;
import org.mpxj.CostRateTableEntry;
import org.mpxj.CustomField;
import org.mpxj.CustomFieldLookupTable;
import org.mpxj.CustomFieldValueDataType;
import org.mpxj.CustomFieldValueMask;
import java.time.DayOfWeek;
import org.mpxj.DayType;
import org.mpxj.Duration;
import org.mpxj.EventManager;
import org.mpxj.FieldType;
import org.mpxj.MPXJException;
import org.mpxj.ProjectCalendar;
import org.mpxj.ProjectCalendarException;
import org.mpxj.ProjectCalendarHours;
import org.mpxj.ProjectCalendarWeek;
import org.mpxj.ProjectConfig;
import org.mpxj.ProjectFile;
import org.mpxj.ProjectProperties;
import org.mpxj.Rate;
import org.mpxj.RecurrenceType;
import org.mpxj.RecurringData;
import org.mpxj.Relation;
import org.mpxj.RelationType;
import org.mpxj.Resource;
import org.mpxj.ResourceAssignment;
import org.mpxj.ResourceType;
import org.mpxj.ScheduleFrom;
import org.mpxj.Task;
import org.mpxj.TaskField;
import org.mpxj.TaskMode;
import org.mpxj.TimeUnit;
import org.mpxj.TimephasedWork;
import org.mpxj.common.BooleanHelper;
import org.mpxj.common.CharsetHelper;
import org.mpxj.common.DefaultTimephasedWorkContainer;
import org.mpxj.common.FieldTypeHelper;
import org.mpxj.common.NumberHelper;
import org.mpxj.common.Pair;
import org.mpxj.common.SplitTaskFactory;
import org.mpxj.common.UnmarshalHelper;
import org.mpxj.mpp.CustomFieldValueItem;
import org.mpxj.mspdi.schema.Project;
import org.mpxj.mspdi.schema.Project.Calendars.Calendar.WorkWeeks;
import org.mpxj.mspdi.schema.Project.Calendars.Calendar.WorkWeeks.WorkWeek;
import org.mpxj.mspdi.schema.Project.Calendars.Calendar.WorkWeeks.WorkWeek.WeekDays;
import org.mpxj.mspdi.schema.Project.Calendars.Calendar.WorkWeeks.WorkWeek.WeekDays.WeekDay;
import org.mpxj.mspdi.schema.Project.Resources.Resource.AvailabilityPeriods;
import org.mpxj.mspdi.schema.Project.Resources.Resource.AvailabilityPeriods.AvailabilityPeriod;
import org.mpxj.mspdi.schema.Project.Resources.Resource.Rates;
import org.mpxj.mspdi.schema.TimephasedDataType;
import org.mpxj.reader.AbstractProjectStreamReader;

/**
 * This class creates a new ProjectFile instance by reading an MSPDI file.
 */
public final class MSPDIReader extends AbstractProjectStreamReader implements HasCharset
{
   /**
    * Set the Charset used to read the file.
    *
    * @param charset Charset used when reading the file
    */
   @Override public void setCharset(Charset charset)
   {
      m_charset = charset;
   }

   /**
    * Retrieve the Charset used to read the file.
    *
    * @return Charset instance
    */
   @Override public Charset getCharset()
   {
      return m_charset;
   }

   @Override public ProjectFile read(InputStream stream) throws MPXJException
   {
      try
      {
         if (CONTEXT == null)
         {
            throw CONTEXT_EXCEPTION;
         }

         ProjectFile projectFile = new ProjectFile();
         DatatypeConverter.setContext(projectFile, m_ignoreErrors);
         @SuppressWarnings("unchecked")
         Project project = ((JAXBElement<Project>) UnmarshalHelper.unmarshal(CONTEXT, new InputSource(new InputStreamReader(stream, getCharset())), new NamespaceFilter(), !m_compatibleInput)).getValue();

         read(projectFile, project);

         return projectFile;
      }

      catch (ParserConfigurationException | IOException | SAXException | JAXBException ex)
      {
         throw new MPXJException("Failed to parse file", ex);
      }
   }

   void read(ProjectFile projectFile, Project project)
   {
      try
      {
         m_projectFile = projectFile;
         m_eventManager = m_projectFile.getEventManager();
         m_lookupTableMap = new HashMap<>();
         m_customFieldValueItems = new HashMap<>();
         m_externalProjects = new HashMap<>();

         ProjectConfig config = m_projectFile.getProjectConfig();
         config.setAutoTaskID(false);
         config.setAutoTaskUniqueID(false);
         config.setAutoResourceID(false);
         config.setAutoResourceUniqueID(false);
         config.setAutoOutlineLevel(false);
         config.setAutoOutlineNumber(false);
         config.setAutoWBS(false);
         config.setAutoCalendarUniqueID(false);
         config.setAutoAssignmentUniqueID(false);

         addListenersToProject(m_projectFile);

         HashMap<BigInteger, ProjectCalendar> calendarMap = new HashMap<>();

         readProjectProperties(project);
         readExtendedAttributeDefinitions(project);
         readOutlineCodeDefinitions(project);
         readCalendars(project, calendarMap);
         readResources(project, calendarMap);
         readTasks(project);
         readAssignments(project);

         //
         // Prune unused derived calendars.
         // Normally we'd just be getting rid of any odd resource calendars which might have
         // slipped in which aren't actually associated with a resource. When applications
         // other than Microsoft Project generate MSPDI files they can (incorrectly) produce more deeply
         // nested hierarchies, so we need to be careful here to only remove calendars which are derived
         // but are not referenced anywhere.
         //
         Map<Integer, List<Resource>> resourceCalendarMap = projectFile.getResources().stream().filter(r -> r.getCalendarUniqueID() != null).collect(Collectors.groupingBy(Resource::getCalendarUniqueID));
         Set<Integer> calendarReferences = new HashSet<>();
         calendarReferences.add(projectFile.getProjectProperties().getDefaultCalendarUniqueID());
         projectFile.getCalendars().stream().map(ProjectCalendar::getParentUniqueID).filter(Objects::nonNull).forEach(calendarReferences::add);
         projectFile.getTasks().stream().map(Task::getCalendarUniqueID).filter(Objects::nonNull).forEach(calendarReferences::add);
         calendarReferences.addAll(resourceCalendarMap.keySet());
         m_projectFile.getCalendars().removeIf(c -> c.isDerived() && !calendarReferences.contains(c.getUniqueID()));

         //
         // Resource calendar post processing
         //
         for (Resource resource : m_projectFile.getResources())
         {
            ProjectCalendar calendar = resource.getCalendar();
            if (calendar != null)
            {
               // Configure the calendar type
               if (calendar.isDerived())
               {
                  calendar.setType(CalendarType.RESOURCE);
                  calendar.setPersonal(resourceCalendarMap.computeIfAbsent(calendar.getUniqueID(), k -> Collections.emptyList()).size() == 1);
               }

               // Resource calendars without names inherit the resource name
               if (calendar.getName() == null || calendar.getName().isEmpty())
               {
                  String name = resource.getName();
                  if (name == null || name.isEmpty())
                  {
                     name = "Unnamed Resource";
                  }
                  calendar.setName(name);
               }
            }
         }

         m_projectFile.readComplete();

         //
         // Process any embedded external projects
         //
         for (Map.Entry<Task, Project> entry : m_externalProjects.entrySet())
         {
            MSPDIReader reader = new MSPDIReader();
            ProjectFile file = new ProjectFile();
            reader.read(file, entry.getValue());
            entry.getKey().setSubprojectObject(file);
         }
      }

      finally
      {
         m_projectFile = null;
         m_lookupTableMap = null;
         m_customFieldValueItems = null;
         m_externalProjects = null;
      }
   }

   /**
    * This method extracts project properties from an MSPDI file.
    *
    * @param project Root node of the MSPDI file
    */
   private void readProjectProperties(Project project)
   {
      ProjectProperties properties = m_projectFile.getProjectProperties();

      properties.setActualsInSync(BooleanHelper.getBoolean(project.isActualsInSync()));
      properties.setAdminProject(BooleanHelper.getBoolean(project.isAdminProject()));
      properties.setApplicationVersion(NumberHelper.getInteger(project.getSaveVersion()));
      properties.setAuthor(project.getAuthor());
      properties.setAutoAddNewResourcesAndTasks(BooleanHelper.getBoolean(project.isAutoAddNewResourcesAndTasks()));
      properties.setAutolink(BooleanHelper.getBoolean(project.isAutolink()));
      properties.setBaselineCalendarName(project.getBaselineCalendar());
      properties.setBaselineForEarnedValue(NumberHelper.getInteger(project.getBaselineForEarnedValue()));
      properties.setCategory(project.getCategory());
      properties.setCompany(project.getCompany());
      properties.setCreationDate(project.getCreationDate());
      properties.setCriticalSlackLimit(Duration.getInstance(NumberHelper.getInt(project.getCriticalSlackLimit()), TimeUnit.DAYS));
      properties.setCurrencyDigits(NumberHelper.getInteger(project.getCurrencyDigits()));
      properties.setCurrencyCode(project.getCurrencyCode());
      properties.setCurrencySymbol(project.getCurrencySymbol());
      properties.setCurrentDate(project.getCurrentDate());
      properties.setDaysPerMonth(NumberHelper.getInteger(project.getDaysPerMonth()));
      properties.setDefaultDurationUnits(DatatypeConverter.parseDurationTimeUnits(project.getDurationFormat()));
      properties.setDefaultEndTime(project.getDefaultFinishTime());
      properties.setDefaultFixedCostAccrual(project.getDefaultFixedCostAccrual());
      properties.setDefaultOvertimeRate(DatatypeConverter.parseRate(project.getDefaultOvertimeRate(), TimeUnit.HOURS));
      properties.setDefaultStandardRate(DatatypeConverter.parseRate(project.getDefaultStandardRate(), TimeUnit.HOURS));
      properties.setDefaultStartTime(project.getDefaultStartTime());
      properties.setDefaultTaskEarnedValueMethod(DatatypeConverter.parseEarnedValueMethod(project.getDefaultTaskEVMethod()));
      properties.setDefaultTaskType(project.getDefaultTaskType());
      properties.setDefaultWorkUnits(DatatypeConverter.parseWorkUnits(project.getWorkFormat()));
      properties.setEarnedValueMethod(DatatypeConverter.parseEarnedValueMethod(project.getEarnedValueMethod()));
      properties.setEditableActualCosts(BooleanHelper.getBoolean(project.isEditableActualCosts()));
      properties.setExtendedCreationDate(project.getExtendedCreationDate());
      properties.setFinishDate(project.getFinishDate());
      properties.setFiscalYearStart(BooleanHelper.getBoolean(project.isFiscalYearStart()));
      properties.setFiscalYearStartMonth(NumberHelper.getInteger(project.getFYStartDate()));
      properties.setGUID(project.getGUID());
      properties.setHonorConstraints(BooleanHelper.getBoolean(project.isHonorConstraints()));
      properties.setInsertedProjectsLikeSummary(BooleanHelper.getBoolean(project.isInsertedProjectsLikeSummary()));
      properties.setLastSaved(project.getLastSaved());
      properties.setManager(project.getManager());
      properties.setMicrosoftProjectServerURL(BooleanHelper.getBoolean(project.isMicrosoftProjectServerURL()));
      properties.setMinutesPerDay(NumberHelper.getInteger(project.getMinutesPerDay()));
      properties.setMinutesPerWeek(NumberHelper.getInteger(project.getMinutesPerWeek()));
      properties.setMoveCompletedEndsBack(BooleanHelper.getBoolean(project.isMoveCompletedEndsBack()));
      properties.setMoveCompletedEndsForward(BooleanHelper.getBoolean(project.isMoveCompletedEndsForward()));
      properties.setMoveRemainingStartsBack(BooleanHelper.getBoolean(project.isMoveRemainingStartsBack()));
      properties.setMoveRemainingStartsForward(BooleanHelper.getBoolean(project.isMoveRemainingStartsForward()));
      properties.setMultipleCriticalPaths(BooleanHelper.getBoolean(project.isMultipleCriticalPaths()));
      properties.setName(project.getName());
      properties.setNewTasksEffortDriven(BooleanHelper.getBoolean(project.isNewTasksEffortDriven()));
      properties.setNewTasksEstimated(BooleanHelper.getBoolean(project.isNewTasksEstimated()));
      properties.setNewTaskStartIsProjectStart(NumberHelper.getInt(project.getNewTaskStartDate()) == 0);
      properties.setNewTasksAreManual(BooleanHelper.getBoolean(project.isNewTasksAreManual()));
      properties.setProjectExternallyEdited(BooleanHelper.getBoolean(project.isProjectExternallyEdited()));
      properties.setProjectTitle(project.getTitle());
      properties.setRemoveFileProperties(BooleanHelper.getBoolean(project.isRemoveFileProperties()));
      properties.setRevision(NumberHelper.getInteger(project.getRevision()));
      properties.setScheduleFrom(BooleanHelper.getBoolean(project.isScheduleFromStart()) ? ScheduleFrom.START : ScheduleFrom.FINISH);
      properties.setSubject(project.getSubject());
      properties.setSplitInProgressTasks(BooleanHelper.getBoolean(project.isSplitsInProgressTasks()));
      properties.setSpreadActualCost(BooleanHelper.getBoolean(project.isSpreadActualCost()));
      properties.setSpreadPercentComplete(BooleanHelper.getBoolean(project.isSpreadPercentComplete()));
      properties.setStartDate(project.getStartDate());
      properties.setStatusDate(project.getStatusDate());
      properties.setSymbolPosition(project.getCurrencySymbolPosition());
      properties.setUpdatingTaskStatusUpdatesResourceStatus(BooleanHelper.getBoolean(project.isTaskUpdatesResource()));
      properties.setWeekStartDay(DatatypeConverter.parseDay(project.getWeekStartDay()));
      updateScheduleSource(properties);
   }

   /**
    * Populate the properties indicating the source of this schedule.
    *
    * @param properties project properties
    */
   private void updateScheduleSource(ProjectProperties properties)
   {
      // Rudimentary identification of schedule source
      if (properties.getCompany() != null && properties.getCompany().equals("Synchro Software Ltd"))
      {
         properties.setFileApplication("Synchro");
      }
      else
      {
         if (properties.getAuthor() != null && properties.getAuthor().equals("SG Project"))
         {
            properties.setFileApplication("Simple Genius");
         }
         else
         {
            properties.setFileApplication("Microsoft");
         }
      }
      properties.setFileType("MSPDI");
   }

   /**
    * This method extracts calendar data from an MSPDI file.
    *
    * @param project Root node of the MSPDI file
    * @param map Map of calendar UIDs to names
    */
   private void readCalendars(Project project, HashMap<BigInteger, ProjectCalendar> map)
   {
      Project.Calendars calendars = project.getCalendars();
      if (calendars != null)
      {
         List<Pair<ProjectCalendar, BigInteger>> baseCalendars = new ArrayList<>();
         for (Project.Calendars.Calendar cal : calendars.getCalendar())
         {
            readCalendar(cal, map, baseCalendars);
         }
         updateBaseCalendarNames(baseCalendars, map);
      }

      //
      // Ensure that the default calendar is set in the project properties
      //
      ProjectCalendar defaultCalendar = map.get(project.getCalendarUID());
      if (defaultCalendar == null)
      {
         defaultCalendar = m_projectFile.getCalendars().findOrCreateDefaultCalendar();
      }

      m_projectFile.setDefaultCalendar(defaultCalendar);
   }

   /**
    * The way calendars are stored in an MSPDI file means that there
    * can be forward references between the base calendar unique ID for a
    * derived calendar, and the base calendar itself. To get around this,
    * we initially populate the base calendar name attribute with the
    * base calendar unique ID, and now in this method we can convert those
    * ID values into the correct names.
    *
    * @param baseCalendars list of calendars and base calendar IDs
    * @param map map of calendar ID values and calendar objects
    */
   private static void updateBaseCalendarNames(List<Pair<ProjectCalendar, BigInteger>> baseCalendars, HashMap<BigInteger, ProjectCalendar> map)
   {
      for (Pair<ProjectCalendar, BigInteger> pair : baseCalendars)
      {
         ProjectCalendar cal = pair.getFirst();
         BigInteger baseCalendarID = pair.getSecond();
         ProjectCalendar baseCal = map.get(baseCalendarID);
         if (baseCal != null)
         {
            cal.setParent(baseCal);
         }
      }

   }

   /**
    * This method extracts data for a single calendar from an MSPDI file.
    *
    * @param calendar Calendar data
    * @param map Map of calendar UIDs to names
    * @param baseCalendars list of base calendars
    */
   private void readCalendar(Project.Calendars.Calendar calendar, HashMap<BigInteger, ProjectCalendar> map, List<Pair<ProjectCalendar, BigInteger>> baseCalendars)
   {
      ProjectCalendar bc = m_projectFile.addCalendar();
      bc.setUniqueID(NumberHelper.getInteger(calendar.getUID()));
      bc.setGUID(calendar.getGUID());
      bc.setName(calendar.getName());
      BigInteger baseCalendarID = calendar.getBaseCalendarUID();
      if (baseCalendarID != null)
      {
         baseCalendars.add(new Pair<>(bc, baseCalendarID));
      }

      readExceptions(calendar, bc);
      boolean readExceptionsFromDays = bc.getCalendarExceptions().isEmpty();

      Project.Calendars.Calendar.WeekDays days = calendar.getWeekDays();
      if (days != null)
      {
         for (Project.Calendars.Calendar.WeekDays.WeekDay weekDay : days.getWeekDay())
         {
            readDay(bc, weekDay, readExceptionsFromDays);
         }
      }
      else
      {
         bc.setCalendarDayType(DayOfWeek.SUNDAY, DayType.DEFAULT);
         bc.setCalendarDayType(DayOfWeek.MONDAY, DayType.DEFAULT);
         bc.setCalendarDayType(DayOfWeek.TUESDAY, DayType.DEFAULT);
         bc.setCalendarDayType(DayOfWeek.WEDNESDAY, DayType.DEFAULT);
         bc.setCalendarDayType(DayOfWeek.THURSDAY, DayType.DEFAULT);
         bc.setCalendarDayType(DayOfWeek.FRIDAY, DayType.DEFAULT);
         bc.setCalendarDayType(DayOfWeek.SATURDAY, DayType.DEFAULT);
      }

      readWorkWeeks(calendar, bc);

      map.put(calendar.getUID(), bc);

      m_eventManager.fireCalendarReadEvent(bc);
   }

   /**
    * This method extracts data for a single day from an MSPDI file.
    *
    * @param calendar Calendar data
    * @param day Day data
    * @param readExceptionsFromDays read exceptions form day definitions
    */
   private void readDay(ProjectCalendar calendar, Project.Calendars.Calendar.WeekDays.WeekDay day, boolean readExceptionsFromDays)
   {
      BigInteger dayType = day.getDayType();
      if (dayType != null)
      {
         if (dayType.intValue() == 0)
         {
            if (readExceptionsFromDays)
            {
               readExceptionDay(calendar, day);
            }
         }
         else
         {
            readNormalDay(calendar, day);
         }
      }
   }

   /**
    * This method extracts data for a normal working day from an MSPDI file.
    *
    * @param calendar Calendar data
    * @param weekDay Day data
    */
   private void readNormalDay(ProjectCalendar calendar, Project.Calendars.Calendar.WeekDays.WeekDay weekDay)
   {
      int dayNumber = weekDay.getDayType().intValue();
      DayOfWeek day = DayOfWeekHelper.getInstance(dayNumber);
      calendar.setWorkingDay(day, BooleanHelper.getBoolean(weekDay.isDayWorking()));
      ProjectCalendarHours hours = calendar.addCalendarHours(day);

      Project.Calendars.Calendar.WeekDays.WeekDay.WorkingTimes times = weekDay.getWorkingTimes();
      if (times != null)
      {
         for (Project.Calendars.Calendar.WeekDays.WeekDay.WorkingTimes.WorkingTime period : times.getWorkingTime())
         {
            LocalTime startTime = period.getFromTime();
            LocalTime endTime = period.getToTime();

            if (startTime != null && endTime != null)
            {
               hours.add(new LocalTimeRange(startTime, endTime));
            }
         }
      }
   }

   /**
    * This method extracts data for an exception day from an MSPDI file.
    *
    * @param calendar Calendar data
    * @param day Day data
    */
   private void readExceptionDay(ProjectCalendar calendar, Project.Calendars.Calendar.WeekDays.WeekDay day)
   {
      Project.Calendars.Calendar.WeekDays.WeekDay.TimePeriod timePeriod = day.getTimePeriod();
      LocalDate fromDate = LocalDateHelper.getLocalDate(timePeriod.getFromDate());
      LocalDate toDate = LocalDateHelper.getLocalDate(timePeriod.getToDate());
      Project.Calendars.Calendar.WeekDays.WeekDay.WorkingTimes times = day.getWorkingTimes();
      ProjectCalendarException exception = calendar.addCalendarException(fromDate, toDate);

      if (times != null)
      {
         List<Project.Calendars.Calendar.WeekDays.WeekDay.WorkingTimes.WorkingTime> time = times.getWorkingTime();
         for (Project.Calendars.Calendar.WeekDays.WeekDay.WorkingTimes.WorkingTime period : time)
         {
            LocalTime startTime = period.getFromTime();
            LocalTime endTime = period.getToTime();

            if (startTime != null && endTime != null)
            {
               exception.add(new LocalTimeRange(startTime, endTime));
            }
         }
      }
   }

   /**
    * Reads any exceptions present in the file. This is only used in MSPDI
    * file versions saved by Project 2007 and later.
    *
    * @param calendar XML calendar
    * @param bc MPXJ calendar
    */
   private void readExceptions(Project.Calendars.Calendar calendar, ProjectCalendar bc)
   {
      Project.Calendars.Calendar.Exceptions exceptions = calendar.getExceptions();
      if (exceptions != null)
      {
         for (Project.Calendars.Calendar.Exceptions.Exception exception : exceptions.getException())
         {
            readException(bc, exception);
         }
      }
   }

   /**
    * Read a single calendar exception.
    *
    * @param bc parent calendar
    * @param exception exception data
    */
   private void readException(ProjectCalendar bc, Project.Calendars.Calendar.Exceptions.Exception exception)
   {
      LocalDate fromDate = LocalDateHelper.getLocalDate(exception.getTimePeriod().getFromDate());
      LocalDate toDate = LocalDateHelper.getLocalDate(exception.getTimePeriod().getToDate());

      // Vico Schedule Planner seems to write start and end dates to FromTime and ToTime
      // rather than FromDate and ToDate. This is plain wrong, and appears to be ignored by MS Project,
      // so we will ignore it too!
      if (fromDate == null && toDate == null)
      {
         return;
      }

      ProjectCalendarException bce;
      RecurringData recurringData = readRecurringData(exception, fromDate, toDate);
      if (recurringData != null && !recurringData.isValid())
      {
         return;
      }

      if (recurringData == null)
      {
         bce = bc.addCalendarException(fromDate, toDate);
      }
      else
      {
         bce = bc.addCalendarException(recurringData);
      }
      bce.setName(exception.getName());

      Project.Calendars.Calendar.Exceptions.Exception.WorkingTimes times = exception.getWorkingTimes();
      if (times != null)
      {
         List<Project.Calendars.Calendar.Exceptions.Exception.WorkingTimes.WorkingTime> time = times.getWorkingTime();
         for (Project.Calendars.Calendar.Exceptions.Exception.WorkingTimes.WorkingTime period : time)
         {
            LocalTime startTime = period.getFromTime();
            LocalTime endTime = period.getToTime();

            if (startTime != null && endTime != null)
            {
               bce.add(new LocalTimeRange(startTime, endTime));
            }
         }
      }
   }

   private RecurringData readRecurringData(Project.Calendars.Calendar.Exceptions.Exception exception, LocalDate fromDate, LocalDate toDate)
   {
      RecurringData rd = null;
      RecurrenceType rt = getRecurrenceType(NumberHelper.getInt(exception.getType()));
      if (rt != null)
      {
         rd = new RecurringData();
         rd.setStartDate(fromDate);
         rd.setFinishDate(toDate);
         rd.setRecurrenceType(rt);
         rd.setRelative(getRelative(NumberHelper.getInt(exception.getType())));
         rd.setOccurrences(NumberHelper.getInteger(exception.getOccurrences()));

         switch (rd.getRecurrenceType())
         {
            case DAILY:
            {
               rd.setFrequency(getFrequency(exception));
               break;
            }

            case WEEKLY:
            {
               rd.setWeeklyDaysFromBitmap(NumberHelper.getInteger(exception.getDaysOfWeek()), DAY_MASKS);
               rd.setFrequency(getFrequency(exception));
               break;
            }

            case MONTHLY:
            {
               if (rd.getRelative())
               {
                  rd.setDayOfWeek(DayOfWeekHelper.getInstance(NumberHelper.getInt(exception.getMonthItem()) - 2));
                  rd.setDayNumber(Integer.valueOf(NumberHelper.getInt(exception.getMonthPosition()) + 1));
               }
               else
               {
                  rd.setDayNumber(NumberHelper.getInteger(exception.getMonthDay()));
               }
               rd.setFrequency(getFrequency(exception));
               break;
            }

            case YEARLY:
            {
               if (rd.getRelative())
               {
                  rd.setDayOfWeek(DayOfWeekHelper.getInstance(NumberHelper.getInt(exception.getMonthItem()) - 2));
                  rd.setDayNumber(Integer.valueOf(NumberHelper.getInt(exception.getMonthPosition()) + 1));
               }
               else
               {
                  rd.setDayNumber(NumberHelper.getInteger(exception.getMonthDay()));
               }
               rd.setMonthNumber(Integer.valueOf(NumberHelper.getInt(exception.getMonth()) + 1));
               break;
            }
         }

         //
         // Flatten daily recurring exceptions if they only result in one date range.
         //
         if (rd.getRecurrenceType() == RecurrenceType.DAILY && NumberHelper.getInt(rd.getFrequency()) == 1)
         {
            rd = null;
         }
      }
      return rd;
   }

   /**
    * Retrieve the recurrence type.
    *
    * @param value integer value
    * @return RecurrenceType instance
    */
   private RecurrenceType getRecurrenceType(int value)
   {
      RecurrenceType result;
      if (value < 0 || value >= RECURRENCE_TYPES.length)
      {
         result = null;
      }
      else
      {
         result = RECURRENCE_TYPES[value];
      }

      return result;
   }

   /**
    * Determine if the exception is relative based on the recurrence type integer value.
    *
    * @param value integer value
    * @return true if the recurrence is relative
    */
   private boolean getRelative(int value)
   {
      boolean result;
      if (value < 0 || value >= RELATIVE_MAP.length)
      {
         result = false;
      }
      else
      {
         result = RELATIVE_MAP[value];
      }

      return result;
   }

   /**
    * Retrieve the frequency of an exception.
    *
    * @param exception XML calendar exception
    * @return frequency
    */
   private Integer getFrequency(Project.Calendars.Calendar.Exceptions.Exception exception)
   {
      Integer period = NumberHelper.getInteger(exception.getPeriod());
      if (period == null)
      {
         period = Integer.valueOf(1);
      }
      return period;
   }

   /**
    * Read the work weeks associated with this calendar.
    *
    * @param xmlCalendar XML calendar object
    * @param mpxjCalendar MPXJ calendar object
    */
   private void readWorkWeeks(Project.Calendars.Calendar xmlCalendar, ProjectCalendar mpxjCalendar)
   {
      WorkWeeks ww = xmlCalendar.getWorkWeeks();
      if (ww != null)
      {
         for (WorkWeek xmlWeek : ww.getWorkWeek())
         {
            readWorkWeek(mpxjCalendar, xmlWeek);
         }
      }
   }

   /**
    * Read a single work week associated with a calendar.
    *
    * @param mpxjCalendar parent calendar
    * @param xmlWeek work week data
    */
   private void readWorkWeek(ProjectCalendar mpxjCalendar, WorkWeek xmlWeek)
   {
      ProjectCalendarWeek week = mpxjCalendar.addWorkWeek();
      week.setName(xmlWeek.getName());
      week.setDateRange(new LocalDateRange(LocalDateHelper.getLocalDate(xmlWeek.getTimePeriod().getFromDate()), LocalDateHelper.getLocalDate(xmlWeek.getTimePeriod().getToDate())));

      WeekDays xmlWeekDays = xmlWeek.getWeekDays();
      if (xmlWeekDays != null)
      {
         Map<DayOfWeek, WeekDay> map = xmlWeekDays.getWeekDay().stream().collect(Collectors.toMap(d -> DayOfWeekHelper.getInstance(d.getDayType().intValue()), d -> d));
         for (DayOfWeek day : DayOfWeek.values())
         {
            WeekDay xmlWeekDay = map.get(day);
            if (xmlWeekDay == null)
            {
               week.setWorkingDay(day, false);
            }
            else
            {
               readWorkWeekDay(week, day, xmlWeekDay);
            }
         }
      }
   }

   /**
    * Read a day from a work week associated with a calendar.
    *
    * @param week parent week
    * @param day day to read
    * @param xmlWeekDay day data
    */
   private void readWorkWeekDay(ProjectCalendarWeek week, DayOfWeek day, WeekDay xmlWeekDay)
   {
      week.setWorkingDay(day, BooleanHelper.getBoolean(xmlWeekDay.isDayWorking()));
      ProjectCalendarHours hours = week.addCalendarHours(day);

      Project.Calendars.Calendar.WorkWeeks.WorkWeek.WeekDays.WeekDay.WorkingTimes times = xmlWeekDay.getWorkingTimes();
      if (times != null)
      {
         for (Project.Calendars.Calendar.WorkWeeks.WorkWeek.WeekDays.WeekDay.WorkingTimes.WorkingTime period : times.getWorkingTime())
         {
            LocalTime startTime = period.getFromTime();
            LocalTime endTime = period.getToTime();

            if (startTime != null && endTime != null)
            {
               hours.add(new LocalTimeRange(startTime, endTime));
            }
         }
      }
   }

   /**
    * This method extracts project extended attribute data from an MSPDI file.
    *
    * @param project Root node of the MSPDI file
    */
   private void readExtendedAttributeDefinitions(Project project)
   {
      Project.ExtendedAttributes attributes = project.getExtendedAttributes();
      if (attributes != null)
      {
         for (Project.ExtendedAttributes.ExtendedAttribute ea : attributes.getExtendedAttribute())
         {
            readExtendedAttributeDefinition(ea);
         }
      }
   }

   /**
    * Read a single field alias from an extended attribute.
    *
    * @param attribute extended attribute
    */
   private void readExtendedAttributeDefinition(Project.ExtendedAttributes.ExtendedAttribute attribute)
   {
      FieldType field = FieldTypeHelper.getInstance(m_projectFile, Integer.parseInt(attribute.getFieldID()));
      m_lookupTableMap.put(attribute.getLtuid(), field);
      String alias = attribute.getAlias();

      // If we are dealing with an enterprise custom field (which will appear here as a UserDefinedField instance)
      // and we haven't been given an explicit alias, then fallback on the field name.
      if ((alias == null || alias.isEmpty()) && field instanceof UserDefinedField)
      {
         alias = attribute.getFieldName();
      }

      if (alias != null && !alias.isEmpty())
      {
         m_projectFile.getCustomFields().getOrCreate(field).setAlias(alias);
      }
   }

   /**
    * This method extracts resource data from an MSPDI file.
    *
    * @param project Root node of the MSPDI file
    * @param calendarMap Map of calendar UIDs to names
    */
   private void readResources(Project project, HashMap<BigInteger, ProjectCalendar> calendarMap)
   {
      Project.Resources resources = project.getResources();
      if (resources != null)
      {
         for (Project.Resources.Resource resource : resources.getResource())
         {
            readResource(resource, calendarMap);
         }
      }
   }

   /**
    * This method extracts data for a single resource from an MSPDI file.
    *
    * @param xml Resource data
    * @param calendarMap Map of calendar UIDs to names
    */
   private void readResource(Project.Resources.Resource xml, HashMap<BigInteger, ProjectCalendar> calendarMap)
   {
      Resource mpx = m_projectFile.addResource();

      mpx.setAccrueAt(xml.getAccrueAt());
      mpx.setActiveDirectoryGUID(xml.getActiveDirectoryGUID());
      mpx.setActualCost(DatatypeConverter.parseCurrency(xml.getActualCost()));
      mpx.setActualOvertimeCost(DatatypeConverter.parseCurrency(xml.getActualOvertimeCost()));
      mpx.setActualOvertimeWork(DatatypeConverter.parseDuration(m_projectFile, null, xml.getActualOvertimeWork()));
      mpx.setActualOvertimeWorkProtected(DatatypeConverter.parseDuration(m_projectFile, null, xml.getActualOvertimeWorkProtected()));
      mpx.setActualWorkProtected(DatatypeConverter.parseDuration(m_projectFile, null, xml.getActualWorkProtected()));
      mpx.setACWP(DatatypeConverter.parseCurrency(xml.getACWP()));
      mpx.setBCWS(DatatypeConverter.parseCurrency(xml.getBCWS()));
      mpx.setBCWP(DatatypeConverter.parseCurrency(xml.getBCWP()));
      mpx.setBookingType(xml.getBookingType());
      //mpx.setBaseCalendar ();
      //mpx.setBaselineCost();
      //mpx.setBaselineWork();
      mpx.setBudget(BooleanHelper.getBoolean(xml.isIsBudget()));
      mpx.setCanLevel(BooleanHelper.getBoolean(xml.isCanLevel()));
      mpx.setCode(xml.getCode());
      mpx.setCost(DatatypeConverter.parseCurrency(xml.getCost()));
      mpx.setCostCenter(xml.getCostCenter());
      mpx.setCostVariance(DatatypeConverter.parseCurrency(xml.getCostVariance()));
      mpx.setCreationDate(xml.getCreationDate());
      mpx.setCV(DatatypeConverter.parseCurrency(xml.getCV()));
      mpx.setEmailAddress(xml.getEmailAddress());
      mpx.setGroup(xml.getGroup());
      mpx.setGUID(xml.getGUID());
      mpx.setHyperlink(xml.getHyperlink());
      mpx.setHyperlinkAddress(xml.getHyperlinkAddress());
      mpx.setHyperlinkSubAddress(xml.getHyperlinkSubAddress());
      mpx.setID(NumberHelper.getInteger(xml.getID()));
      mpx.setInitials(xml.getInitials());
      mpx.setEnterprise(BooleanHelper.getBoolean(xml.isIsEnterprise()));
      mpx.setGeneric(BooleanHelper.getBoolean(xml.isIsGeneric()));
      mpx.setActive(!BooleanHelper.getBoolean(xml.isIsInactive()));
      mpx.setIsNull(BooleanHelper.getBoolean(xml.isIsNull()));
      //mpx.setLinkedFields();
      mpx.setUnitOfMeasure(m_projectFile.getUnitsOfMeasure().getOrCreateByAbbreviation(xml.getMaterialLabel()));
      mpx.setName(xml.getName());
      if (xml.getNotes() != null && !xml.getNotes().isEmpty())
      {
         mpx.setNotes(xml.getNotes());
      }
      mpx.setNtAccount(xml.getNTAccount());
      //mpx.setObjects();
      mpx.setOvertimeCost(DatatypeConverter.parseCurrency(xml.getOvertimeCost()));
      mpx.setOvertimeWork(DatatypeConverter.parseDuration(m_projectFile, null, xml.getOvertimeWork()));
      mpx.setPeakUnits(DatatypeConverter.parseUnits(xml.getPeakUnits()));
      mpx.setPhonetics(xml.getPhonetics());
      mpx.setRegularWork(DatatypeConverter.parseDuration(m_projectFile, null, xml.getRegularWork()));
      mpx.setRemainingCost(DatatypeConverter.parseCurrency(xml.getRemainingCost()));
      mpx.setRemainingOvertimeCost(DatatypeConverter.parseCurrency(xml.getRemainingOvertimeCost()));
      mpx.setRemainingOvertimeWork(DatatypeConverter.parseDuration(m_projectFile, null, xml.getRemainingOvertimeWork()));
      mpx.setSV(DatatypeConverter.parseCurrency(xml.getSV()));
      mpx.setType(xml.getType());
      mpx.setUniqueID(NumberHelper.getInteger(xml.getUID()));
      mpx.setWork(DatatypeConverter.parseDuration(m_projectFile, null, xml.getWork()));
      mpx.setWorkGroup(xml.getWorkGroup());
      mpx.setWorkVariance(DatatypeConverter.parseDurationInThousanthsOfMinutes(xml.getWorkVariance()));

      if (mpx.getType() == ResourceType.MATERIAL && BooleanHelper.getBoolean(xml.isIsCostResource()))
      {
         mpx.setType(ResourceType.COST);
      }

      mpx.setActualWork(DatatypeConverter.parseDuration(m_projectFile, null, xml.getActualWork()));
      mpx.setRemainingWork(DatatypeConverter.parseDuration(m_projectFile, null, xml.getRemainingWork()));
      mpx.setPercentWorkComplete(xml.getPercentWorkComplete());

      readResourceExtendedAttributes(xml, mpx);
      readResourceOutlineCodes(xml, mpx);

      readResourceBaselines(xml, mpx);

      mpx.setCalendar(calendarMap.get(xml.getCalendarUID()));

      Rate standardRate = DatatypeConverter.parseRate(xml.getStandardRate(), DatatypeConverter.parseTimeUnit(xml.getStandardRateFormat()));
      Rate overtimeRate = DatatypeConverter.parseRate(xml.getOvertimeRate(), DatatypeConverter.parseTimeUnit(xml.getOvertimeRateFormat()));
      Number costPerUse = DatatypeConverter.parseCurrency(xml.getCostPerUse());
      readCostRateTables(mpx, standardRate, overtimeRate, costPerUse, xml.getRates());

      readAvailabilityTable(mpx, xml);

      // ensure that we cache this value
      mpx.setOverAllocated(BooleanHelper.getBoolean(xml.isOverAllocated()));

      m_eventManager.fireResourceReadEvent(mpx);
   }

   /**
    * Reads baseline values for the current resource.
    *
    * @param xmlResource MSPDI resource instance
    * @param mpxjResource MPXJ resource instance
    */
   private void readResourceBaselines(Project.Resources.Resource xmlResource, Resource mpxjResource)
   {
      for (Project.Resources.Resource.Baseline baseline : xmlResource.getBaseline())
      {
         int number = NumberHelper.getInt(baseline.getNumber());

         Double cost = DatatypeConverter.parseCurrency(baseline.getCost());
         Duration work = DatatypeConverter.parseDuration(m_projectFile, TimeUnit.HOURS, baseline.getWork());

         if (number == 0)
         {
            mpxjResource.setBaselineCost(cost);
            mpxjResource.setBaselineWork(work);
         }
         else
         {
            mpxjResource.setBaselineCost(number, cost);
            mpxjResource.setBaselineWork(number, work);
         }
      }
   }

   /**
    * This method processes any extended attributes associated with a resource.
    *
    * @param xml MSPDI resource instance
    * @param mpx MPX resource instance
    */
   private void readResourceExtendedAttributes(Project.Resources.Resource xml, Resource mpx)
   {
      for (Project.Resources.Resource.ExtendedAttribute attrib : xml.getExtendedAttribute())
      {
         FieldType mpxFieldID = FieldTypeHelper.getInstance(m_projectFile, Integer.parseInt(attrib.getFieldID()));
         TimeUnit durationFormat = DatatypeConverter.parseDurationTimeUnits(attrib.getDurationFormat(), null);
         DatatypeConverter.parseCustomField(m_projectFile, mpx, attrib.getValue(), mpxFieldID, durationFormat);
      }
   }

   /**
    * This method processes any outline codes associated with a resource.
    *
    * @param xml MSPDI resource instance
    * @param mpx MPX resource instance
    */
   private void readResourceOutlineCodes(Project.Resources.Resource xml, Resource mpx)
   {
      for (Project.Resources.Resource.OutlineCode attrib : xml.getOutlineCode())
      {
         if (attrib.getFieldID() == null)
         {
            continue;
         }

         FieldType mpxFieldID = FieldTypeHelper.getInstance(m_projectFile, Integer.parseInt(attrib.getFieldID()));
         mpx.set(mpxFieldID, getOutlineCodeValue(mpxFieldID, attrib.getValueID()));
      }
   }

   /**
    * Reads the cost rate tables from the file.
    *
    * @param resource parent resource
    * @param defaultStandardRate default standard rate
    * @param defaultOvertimeRate default overtime rate
    * @param defaultCostPerUse default cost per use
    * @param rates XML cost rate tables
    */
   private void readCostRateTables(Resource resource, Rate defaultStandardRate, Rate defaultOvertimeRate, Number defaultCostPerUse, Rates rates)
   {
      if (rates == null)
      {
         for (int index = 0; index < CostRateTable.MAX_TABLES; index++)
         {
            CostRateTable table = new CostRateTable();
            if (index == 0)
            {
               LocalDateTime startDate = CostRateTableEntry.DEFAULT_ENTRY.getStartDate();
               LocalDateTime endDate = CostRateTableEntry.DEFAULT_ENTRY.getEndDate();
               table.add(new CostRateTableEntry(startDate, endDate, defaultCostPerUse, defaultStandardRate, defaultOvertimeRate));
            }
            else
            {
               table.add(CostRateTableEntry.DEFAULT_ENTRY);
            }

            resource.setCostRateTable(index, table);
         }
      }
      else
      {
         CostRateTable[] tables = new CostRateTable[CostRateTable.MAX_TABLES];

         for (Project.Resources.Resource.Rates.Rate rate : rates.getRate())
         {
            if (rate.getRateTable() == null)
            {
               continue;
            }

            int tableIndex = rate.getRateTable().intValue();
            if (tableIndex < 0 || tableIndex >= CostRateTable.MAX_TABLES)
            {
               continue;
            }

            TimeUnit standardRateFormat = DatatypeConverter.parseTimeUnit(rate.getStandardRateFormat());
            Rate standardRate = DatatypeConverter.parseRate(rate.getStandardRate(), standardRateFormat);

            TimeUnit overtimeRateFormat = DatatypeConverter.parseTimeUnit(rate.getOvertimeRateFormat());
            Rate overtimeRate = DatatypeConverter.parseRate(rate.getOvertimeRate(), overtimeRateFormat);

            Double costPerUse = DatatypeConverter.parseCurrency(rate.getCostPerUse());
            LocalDateTime startDate = rate.getRatesFrom();
            LocalDateTime endDate = rate.getRatesTo();

            if (startDate.isBefore(LocalDateTimeHelper.START_DATE_NA))
            {
               startDate = LocalDateTimeHelper.START_DATE_NA;
            }

            if (endDate.isAfter(LocalDateTimeHelper.END_DATE_NA))
            {
               endDate = LocalDateTimeHelper.END_DATE_NA;
            }

            //
            // See the note in CostRateTableFactory for more details of this heuristic.
            //
            int minutes = endDate.getMinute();
            if ((minutes % 5) == 0)
            {
               endDate = endDate.minusMinutes(1);
            }

            CostRateTableEntry entry = new CostRateTableEntry(startDate, endDate, costPerUse, standardRate, overtimeRate);
            CostRateTable table = tables[tableIndex];
            if (table == null)
            {
               table = new CostRateTable();
               tables[tableIndex] = table;
            }
            table.add(entry);
         }

         for (int tableIndex = 0; tableIndex < tables.length; tableIndex++)
         {
            CostRateTable table = tables[tableIndex];
            if (table != null)
            {
               Collections.sort(table);
               resource.setCostRateTable(tableIndex, table);
            }
         }
      }
   }

   /**
    * Reads the availability table from the file.
    *
    * @param resource MPXJ resource instance
    * @param xml MSPDI resource
    */
   private void readAvailabilityTable(Resource resource, Project.Resources.Resource xml)
   {
      AvailabilityPeriods periods = xml.getAvailabilityPeriods();
      if (periods == null || periods.getAvailabilityPeriod().isEmpty())
      {
         LocalDateTime availableFrom = xml.getAvailableFrom();
         LocalDateTime availableTo = xml.getAvailableTo();
         availableFrom = availableFrom == null ? LocalDateTimeHelper.START_DATE_NA : availableFrom;
         availableTo = availableTo == null ? LocalDateTimeHelper.END_DATE_NA : availableTo;
         resource.getAvailability().add(new Availability(availableFrom, availableTo, DatatypeConverter.parseUnits(xml.getMaxUnits())));
      }
      else
      {
         AvailabilityTable table = resource.getAvailability();
         List<AvailabilityPeriod> list = periods.getAvailabilityPeriod();
         for (AvailabilityPeriod period : list)
         {
            LocalDateTime start = period.getAvailableFrom();
            LocalDateTime end = period.getAvailableTo();
            Number units = DatatypeConverter.parseUnits(period.getAvailableUnits());

            if (start == null || start.isBefore(LocalDateTimeHelper.START_DATE_NA))
            {
               start = LocalDateTimeHelper.START_DATE_NA;
            }

            if (end == null || end.isAfter(LocalDateTimeHelper.END_DATE_NA))
            {
               end = LocalDateTimeHelper.END_DATE_NA;
            }

            Availability availability = new Availability(start, end, units);
            table.add(availability);
         }
         Collections.sort(table);
      }
   }

   /**
    * This method extracts task data from an MSPDI file.
    *
    * @param project Root node of the MSPDI file
    */
   private void readTasks(Project project)
   {
      Project.Tasks tasks = project.getTasks();
      if (tasks != null)
      {
         int tasksWithoutIDCount = 0;

         for (Project.Tasks.Task task : tasks.getTask())
         {
            Task mpxjTask = readTask(task);
            if (mpxjTask.getID() == null)
            {
               ++tasksWithoutIDCount;
            }
         }

         for (Project.Tasks.Task task : tasks.getTask())
         {
            readPredecessors(task);
         }

         //
         // MS Project will happily read tasks from an MSPDI file without IDs,
         // it will just generate ID values based on the task order in the file.
         // If we find that there are no ID values present, we'll do the same.
         //
         if (tasksWithoutIDCount == tasks.getTask().size())
         {
            m_projectFile.getTasks().renumberIDs();
         }
      }

      m_projectFile.updateStructure();
   }

   /**
    * This method extracts data for a single task from an MSPDI file.
    *
    * @param xml Task data
    * @return Task instance
    */
   private Task readTask(Project.Tasks.Task xml)
   {
      Task mpx = m_projectFile.addTask();
      mpx.setNull(BooleanHelper.getBoolean(xml.isIsNull()));
      mpx.setID(NumberHelper.getInteger(xml.getID()));
      mpx.setUniqueID(NumberHelper.getInteger(xml.getUID()));

      if (!mpx.getNull())
      {
         //
         // Set the duration format up front as this is required later
         //
         TimeUnit durationFormat = DatatypeConverter.parseDurationTimeUnits(xml.getDurationFormat());

         if (BooleanHelper.getBoolean(xml.isIsSubproject()))
         {
            mpx.setSubprojectFile(xml.getSubprojectName());
            mpx.setSubprojectReadOnly(BooleanHelper.getBoolean(xml.isIsSubprojectReadOnly()));
         }

         mpx.setActive(xml.isActive() == null || BooleanHelper.getBoolean(xml.isActive()));
         mpx.setActualCost(DatatypeConverter.parseCurrency(xml.getActualCost()));
         mpx.setActualDuration(DatatypeConverter.parseDuration(m_projectFile, durationFormat, xml.getActualDuration()));
         mpx.setActualFinish(xml.getActualFinish());
         mpx.setActualOvertimeCost(DatatypeConverter.parseCurrency(xml.getActualOvertimeCost()));
         mpx.setActualOvertimeWork(DatatypeConverter.parseDuration(m_projectFile, durationFormat, xml.getActualOvertimeWork()));
         mpx.setActualOvertimeWorkProtected(DatatypeConverter.parseDuration(m_projectFile, durationFormat, xml.getActualOvertimeWorkProtected()));
         mpx.setActualStart(xml.getActualStart());
         mpx.setActualWork(DatatypeConverter.parseDuration(m_projectFile, durationFormat, xml.getActualWork()));
         mpx.setActualWorkProtected(DatatypeConverter.parseDuration(m_projectFile, durationFormat, xml.getActualWorkProtected()));
         mpx.setACWP(DatatypeConverter.parseCurrency(xml.getACWP()));
         //mpx.setBaselineCost();
         //mpx.setBaselineDuration();
         //mpx.setBaselineFinish();
         //mpx.setBaselineStart();
         //mpx.setBaselineWork();
         //mpx.setBCWP();
         //mpx.setBCWS();
         mpx.setCalendar(getTaskCalendar(xml));
         //mpx.setConfirmed();
         mpx.setConstraintDate(xml.getConstraintDate());
         mpx.setConstraintType(DatatypeConverter.parseConstraintType(xml.getConstraintType()));
         mpx.setContact(xml.getContact());
         mpx.setCost(DatatypeConverter.parseCurrency(xml.getCost()));
         //mpx.setCost1();
         //mpx.setCost2();
         //mpx.setCost3();
         //mpx.setCostVariance();
         mpx.setCreateDate(xml.getCreateDate());
         mpx.setCV(DatatypeConverter.parseCurrency(xml.getCV()));
         mpx.setDeadline(xml.getDeadline());
         //mpx.setDelay();
         mpx.setDuration(DatatypeConverter.parseDuration(m_projectFile, durationFormat, xml.getDuration()));
         mpx.setDurationText(xml.getDurationText());
         //mpx.setDuration1();
         //mpx.setDuration2();
         //mpx.setDuration3();
         //mpx.setDurationVariance();
         mpx.setEarlyFinish(xml.getEarlyFinish());
         mpx.setEarlyStart(xml.getEarlyStart());
         mpx.setEarnedValueMethod(DatatypeConverter.parseEarnedValueMethod(xml.getEarnedValueMethod()));
         mpx.setEffortDriven(BooleanHelper.getBoolean(xml.isEffortDriven()));
         mpx.setEstimated(BooleanHelper.getBoolean(xml.isEstimated()));
         mpx.setExternalTask(BooleanHelper.getBoolean(xml.isExternalTask()));
         mpx.setProject(xml.getExternalTaskProject());
         mpx.setFinish(xml.getFinish());
         mpx.setFinishText(xml.getFinishText());
         //mpx.setFinish1();
         //mpx.setFinish2();
         //mpx.setFinish3();
         //mpx.setFinish4();
         //mpx.setFinish5();
         mpx.setFinishVariance(DatatypeConverter.parseDurationInTenthsOfMinutes(xml.getFinishVariance()));
         //mpx.setFixed();
         mpx.setFixedCost(DatatypeConverter.parseCurrency(xml.getFixedCost()));
         mpx.setFixedCostAccrual(xml.getFixedCostAccrual());
         //mpx.setFlag1();
         //mpx.setFlag2();
         //mpx.setFlag3();
         //mpx.setFlag4();
         //mpx.setFlag5();
         //mpx.setFlag6();
         //mpx.setFlag7();
         //mpx.setFlag8();
         //mpx.setFlag9();
         //mpx.setFlag10();
         // This is not correct?
         mpx.setGUID(xml.getGUID());
         mpx.setHideBar(BooleanHelper.getBoolean(xml.isHideBar()));
         mpx.setHyperlink(xml.getHyperlink());
         mpx.setHyperlinkAddress(xml.getHyperlinkAddress());
         mpx.setHyperlinkSubAddress(xml.getHyperlinkSubAddress());

         mpx.setIgnoreResourceCalendar(BooleanHelper.getBoolean(xml.isIgnoreResourceCalendar()));
         mpx.setLateFinish(xml.getLateFinish());
         mpx.setLateStart(xml.getLateStart());
         mpx.setLevelAssignments(BooleanHelper.getBoolean(xml.isLevelAssignments()));
         mpx.setLevelingCanSplit(BooleanHelper.getBoolean(xml.isLevelingCanSplit()));
         mpx.setLevelingDelayFormat(DatatypeConverter.parseDurationTimeUnits(xml.getLevelingDelayFormat()));
         if (xml.getLevelingDelay() != null && mpx.getLevelingDelayFormat() != null)
         {
            double duration = xml.getLevelingDelay().doubleValue();
            if (duration != 0)
            {
               mpx.setLevelingDelay(Duration.convertUnits(duration / 10, TimeUnit.MINUTES, mpx.getLevelingDelayFormat(), m_projectFile.getProjectProperties()));
            }
         }

         //mpx.setLinkedFields();
         //mpx.setMarked();
         mpx.setMilestone(BooleanHelper.getBoolean(xml.isMilestone()));
         mpx.setName(xml.getName());
         if (xml.getNotes() != null && !xml.getNotes().isEmpty())
         {
            mpx.setNotes(xml.getNotes());
         }
         //mpx.setNumber1();
         //mpx.setNumber2();
         //mpx.setNumber3();
         //mpx.setNumber4();
         //mpx.setNumber5();
         //mpx.setObjects();
         mpx.setOutlineLevel(NumberHelper.getInteger(xml.getOutlineLevel()));
         mpx.setOutlineNumber(xml.getOutlineNumber());
         mpx.setOverAllocated(BooleanHelper.getBoolean(xml.isOverAllocated()));
         mpx.setOvertimeCost(DatatypeConverter.parseCurrency(xml.getOvertimeCost()));
         mpx.setOvertimeWork(DatatypeConverter.parseDuration(m_projectFile, durationFormat, xml.getOvertimeWork()));
         mpx.setPercentageComplete(xml.getPercentComplete());
         mpx.setPercentageWorkComplete(xml.getPercentWorkComplete());
         mpx.setPhysicalPercentComplete(NumberHelper.getInteger(xml.getPhysicalPercentComplete()));
         mpx.setPreleveledFinish(xml.getPreLeveledFinish());
         mpx.setPreleveledStart(xml.getPreLeveledStart());
         mpx.setPriority(DatatypeConverter.parsePriority(xml.getPriority()));
         //mpx.setProject();
         mpx.setRecurring(BooleanHelper.getBoolean(xml.isRecurring()));
         mpx.setRegularWork(DatatypeConverter.parseDuration(m_projectFile, durationFormat, xml.getRegularWork()));
         mpx.setRemainingCost(DatatypeConverter.parseCurrency(xml.getRemainingCost()));
         mpx.setRemainingDuration(DatatypeConverter.parseDuration(m_projectFile, durationFormat, xml.getRemainingDuration()));
         mpx.setRemainingOvertimeCost(DatatypeConverter.parseCurrency(xml.getRemainingOvertimeCost()));
         mpx.setRemainingOvertimeWork(DatatypeConverter.parseDuration(m_projectFile, durationFormat, xml.getRemainingOvertimeWork()));
         mpx.setRemainingWork(DatatypeConverter.parseDuration(m_projectFile, durationFormat, xml.getRemainingWork()));
         //mpx.setResourceGroup();
         //mpx.setResourceInitials();
         //mpx.setResourceNames();
         mpx.setResume(xml.getResume());
         mpx.setResumeValid(BooleanHelper.getBoolean(xml.isResumeValid()));
         //mpx.setResumeNoEarlierThan();
         mpx.setRollup(BooleanHelper.getBoolean(xml.isRollup()));
         mpx.setStart(xml.getStart());
         mpx.setStartText(xml.getStartText());
         //mpx.setStart1();
         //mpx.setStart2();
         //mpx.setStart3();
         //mpx.setStart4();
         //mpx.setStart5();
         mpx.setStartVariance(DatatypeConverter.parseDurationInTenthsOfMinutes(xml.getStartVariance()));
         mpx.setStop(xml.getStop());
         //mpx.setSuccessors();
         // Rely on the presence of child tasks to determine if this is a summary task rather than using this attribute
         //mpx.setSummary(BooleanHelper.getBoolean(xml.isSummary()));
         //mpx.setSV();
         //mpx.setText1();
         //mpx.setText2();
         //mpx.setText3();
         //mpx.setText4();
         //mpx.setText5();
         //mpx.setText6();
         //mpx.setText7();
         //mpx.setText8();
         //mpx.setText9();
         //mpx.setText10();
         mpx.setTaskMode(BooleanHelper.getBoolean(xml.isManual()) ? TaskMode.MANUALLY_SCHEDULED : TaskMode.AUTO_SCHEDULED);
         mpx.setType(xml.getType());
         //mpx.setUpdateNeeded();
         mpx.setWBS(xml.getWBS());
         mpx.setWork(DatatypeConverter.parseDuration(m_projectFile, durationFormat, xml.getWork()));
         mpx.setWorkVariance(DatatypeConverter.parseDurationInThousanthsOfMinutes(xml.getWorkVariance()));

         validateFinishDate(mpx);

         // read last to ensure correct caching
         mpx.setStartSlack(DatatypeConverter.parseDurationInTenthsOfMinutes(xml.getStartSlack()));
         mpx.setFinishSlack(DatatypeConverter.parseDurationInTenthsOfMinutes(xml.getFinishSlack()));
         mpx.setFreeSlack(DatatypeConverter.parseDurationInTenthsOfMinutes(xml.getFreeSlack()));
         mpx.setTotalSlack(DatatypeConverter.parseDurationInTenthsOfMinutes(xml.getTotalSlack()));
         mpx.setCritical(BooleanHelper.getBoolean(xml.isCritical()));

         readTaskExtendedAttributes(xml, mpx);
         readTaskOutlineCodes(xml, mpx);

         readTaskBaselines(xml, mpx, durationFormat);

         if (mpx.getTaskMode() == TaskMode.MANUALLY_SCHEDULED)
         {
            mpx.setManualDuration(DatatypeConverter.parseDuration(m_projectFile, durationFormat, xml.getManualDuration()));
         }

         // Ensure consistency with data read from an MPP file
         if (mpx.getActualDuration() != null)
         {
            mpx.set(TaskField.ACTUAL_DURATION_UNITS, mpx.getActualDuration().getUnits());
         }

         //
         // When reading an MSPDI file, the project summary task contains
         // some of the values used to populate the project properties.
         //
         if (NumberHelper.getInt(mpx.getUniqueID()) == 0)
         {
            // ensure that the project summary task is always marked as a summary task
            mpx.setSummary(true);
            updateProjectProperties(mpx);
         }

         //
         // If we have an embedded external project, add it to the map
         // for processing once we have read the main project.
         // This avoids issues with DatatypeConverter.
         //
         if (xml.getProject() != null)
         {
            m_externalProjects.put(mpx, xml.getProject());
         }
      }

      m_eventManager.fireTaskReadEvent(mpx);

      return mpx;
   }

   /**
    * Update the project properties from the project summary task.
    *
    * @param task project summary task
    */
   private void updateProjectProperties(Task task)
   {
      ProjectProperties props = m_projectFile.getProjectProperties();
      props.setComments(task.getNotes());
   }

   /**
    * When projectmanager.com exports schedules as MSPDI (via Aspose tasks)
    * they do not have finish dates, just a start date and a duration.
    * This method populates finish dates.
    *
    * @param task task to validate
    */
   private void validateFinishDate(Task task)
   {
      if (task.getFinish() == null)
      {
         LocalDateTime startDate = task.getStart();
         if (startDate != null)
         {
            if (task.getMilestone())
            {
               task.setFinish(startDate);
            }
            else
            {
               Duration duration = task.getDuration();
               if (duration != null)
               {
                  ProjectCalendar calendar = task.getEffectiveCalendar();
                  task.setFinish(calendar.getDate(startDate, duration));
               }
            }
         }
      }
   }

   /**
    * Reads baseline values for the current task.
    *
    * @param xmlTask MSPDI task instance
    * @param mpxjTask MPXJ task instance
    * @param durationFormat duration format to use
    */
   private void readTaskBaselines(Project.Tasks.Task xmlTask, Task mpxjTask, TimeUnit durationFormat)
   {
      for (Project.Tasks.Task.Baseline baseline : xmlTask.getBaseline())
      {
         int number = NumberHelper.getInt(baseline.getNumber());
         if (number < 0 || number > 10)
         {
            continue;
         }

         Double cost = DatatypeConverter.parseCurrency(baseline.getCost());
         Duration duration = DatatypeConverter.parseDuration(m_projectFile, durationFormat, baseline.getDuration());
         LocalDateTime finish = baseline.getFinish();
         LocalDateTime start = baseline.getStart();
         Duration work = DatatypeConverter.parseDuration(m_projectFile, TimeUnit.HOURS, baseline.getWork());

         if (number == 0)
         {
            mpxjTask.setBaselineCost(cost);
            mpxjTask.setBaselineDuration(duration);
            mpxjTask.setBaselineFinish(finish);
            mpxjTask.setBaselineStart(start);
            mpxjTask.setBaselineWork(work);
         }
         else
         {
            mpxjTask.setBaselineCost(number, cost);
            mpxjTask.setBaselineDuration(number, duration);
            mpxjTask.setBaselineFinish(number, finish);
            mpxjTask.setBaselineStart(number, start);
            mpxjTask.setBaselineWork(number, work);
         }
      }
   }

   /**
    * This method processes any extended attributes associated with a task.
    *
    * @param xml MSPDI task instance
    * @param mpx MPX task instance
    */
   private void readTaskExtendedAttributes(Project.Tasks.Task xml, Task mpx)
   {
      for (Project.Tasks.Task.ExtendedAttribute attrib : xml.getExtendedAttribute())
      {
         FieldType mpxFieldID = FieldTypeHelper.getInstance(m_projectFile, Integer.parseInt(attrib.getFieldID()));
         TimeUnit durationFormat = DatatypeConverter.parseDurationTimeUnits(attrib.getDurationFormat(), null);
         DatatypeConverter.parseCustomField(m_projectFile, mpx, attrib.getValue(), mpxFieldID, durationFormat);
      }
   }

   /**
    * This method processes any outline codes associated with a task.
    *
    * @param xml MSPDI task instance
    * @param mpx MPX task instance
    */
   private void readTaskOutlineCodes(Project.Tasks.Task xml, Task mpx)
   {
      for (Project.Tasks.Task.OutlineCode attrib : xml.getOutlineCode())
      {
         if (attrib.getFieldID() == null)
         {
            continue;
         }

         FieldType mpxFieldID = FieldTypeHelper.getInstance(m_projectFile, Integer.parseInt(attrib.getFieldID()));
         mpx.set(mpxFieldID, getOutlineCodeValue(mpxFieldID, attrib.getValueID()));
      }
   }

   private String getOutlineCodeValue(FieldType mpxFieldID, BigInteger valueID)
   {
      String result = null;
      CustomFieldValueItem item = getValueItem(mpxFieldID, valueID);
      if (item != null && item.getValue() != null)
      {
         result = item.getValue().toString();

         Integer parentID = item.getParentUniqueID();
         if (parentID != null)
         {
            String parentResult = getOutlineCodeValue(mpxFieldID, BigInteger.valueOf(parentID.longValue()));
            if (parentResult != null)
            {
               // TODO: use the code mask
               result = parentResult + "." + result;
            }
         }
      }
      return result;
   }

   /**
    * Given a value ID, retrieve the equivalent lookup table entry.
    *
    * @param fieldType field type
    * @param valueID value ID
    * @return lookup table entry
    */
   private CustomFieldValueItem getValueItem(FieldType fieldType, BigInteger valueID)
   {
      return m_customFieldValueItems.computeIfAbsent(fieldType, k -> getCustomFieldValueItemMap(fieldType)).get(valueID);
   }

   /**
    * Populate a cache of lookup table entries.
    *
    * @param fieldType field type
    * @return cache of lookup table entries
    */
   private Map<BigInteger, CustomFieldValueItem> getCustomFieldValueItemMap(FieldType fieldType)
   {
      CustomField field = m_projectFile.getCustomFields().get(fieldType);
      if (field == null)
      {
         return Collections.emptyMap();
      }

      return field.getLookupTable().stream().collect(Collectors.toMap(i -> BigInteger.valueOf(i.getUniqueID().intValue()), i -> i));
   }

   /**
    * This method is used to retrieve the calendar associated
    * with a task. If no calendar is associated with a task, this method
    * returns null.
    *
    * @param task MSPDI task
    * @return calendar instance
    */
   private ProjectCalendar getTaskCalendar(Project.Tasks.Task task)
   {
      ProjectCalendar calendar = null;

      BigInteger calendarID = task.getCalendarUID();
      if (calendarID != null)
      {
         calendar = m_projectFile.getCalendarByUniqueID(Integer.valueOf(calendarID.intValue()));
      }

      return (calendar);
   }

   /**
    * This method extracts predecessor data from an MSPDI file.
    *
    * @param task Task data
    */
   private void readPredecessors(Project.Tasks.Task task)
   {
      Integer uid = task.getUID();
      if (uid != null)
      {
         Task currTask = m_projectFile.getTaskByUniqueID(uid);
         if (currTask != null)
         {
            for (Project.Tasks.Task.PredecessorLink link : task.getPredecessorLink())
            {
               readPredecessor(currTask, link);
            }
         }
      }
   }

   /**
    * This method extracts data for a single predecessor from an MSPDI file.
    *
    * @param currTask Current task object
    * @param link Predecessor data
    */
   private void readPredecessor(Task currTask, Project.Tasks.Task.PredecessorLink link)
   {
      BigInteger uid = link.getPredecessorUID();
      if (uid == null)
      {
         return;
      }

      Task prevTask;
      if (BooleanHelper.getBoolean(link.isCrossProject()))
      {
         prevTask = createExternalTaskPlaceholder(currTask, link);
      }
      else
      {
         prevTask = m_projectFile.getTaskByUniqueID(Integer.valueOf(uid.intValue()));
      }

      if (prevTask == null)
      {
         return;
      }

      RelationType type;
      if (link.getType() != null)
      {
         type = RelationType.getInstance(link.getType().intValue());
      }
      else
      {
         type = RelationType.FINISH_START;
      }

      TimeUnit lagUnits = DatatypeConverter.parseDurationTimeUnits(link.getLagFormat());

      Duration lagDuration;
      int lag = NumberHelper.getInt(link.getLinkLag());
      if (lag == 0)
      {
         lagDuration = Duration.getInstance(0, lagUnits);
      }
      else
      {
         if (lagUnits == TimeUnit.PERCENT || lagUnits == TimeUnit.ELAPSED_PERCENT)
         {
            lagDuration = Duration.getInstance(lag, lagUnits);
         }
         else
         {
            lagDuration = Duration.convertUnits(lag / 10.0, TimeUnit.MINUTES, lagUnits, m_projectFile.getProjectProperties());
         }
      }

      Relation relation = currTask.addPredecessor(new Relation.Builder()
         .predecessorTask(prevTask)
         .type(type)
         .lag(lagDuration));

      m_eventManager.fireRelationReadEvent(relation);
   }

   /**
    * We try to use the minimal data present in an MSPDI file to recreate the structure
    * we'd see if we read the equivalent MPP file containing external tasks.
    *
    * @param currTask current task
    * @param link link data
    * @return external task placeholder for predecessor task
    */
   private Task createExternalTaskPlaceholder(Task currTask, Project.Tasks.Task.PredecessorLink link)
   {
      String crossProjectName = link.getCrossProjectName();
      if (crossProjectName == null || crossProjectName.isEmpty())
      {
         return null;
      }

      int splitIndex = crossProjectName.lastIndexOf('\\');
      String subprojectFile = splitIndex == -1 ? crossProjectName : crossProjectName.substring(0, splitIndex);
      Integer subprojectTaskID = splitIndex + 1 >= crossProjectName.length() ? null : Integer.valueOf(crossProjectName.substring(splitIndex + 1));

      Integer taskUniqueID = NumberHelper.getInteger(link.getPredecessorUID());
      Task task = m_projectFile.getTaskByUniqueID(taskUniqueID);
      if (task == null)
      {
         task = m_projectFile.addTask();
         task.setName("External Task");
         task.setExternalTask(true);
         task.setSubprojectFile(subprojectFile);
         task.setSubprojectTaskID(subprojectTaskID);
         task.setOutlineLevel(currTask.getOutlineLevel());
         task.setUniqueID(NumberHelper.getInteger(link.getPredecessorUID()));
         task.setID(currTask.getID());
         currTask.setID(Integer.valueOf(currTask.getID().intValue() + 1));

         ChildTaskContainer container = currTask.getParentTask() == null ? m_projectFile : currTask.getParentTask();
         int insertionIndex = container.getChildTasks().indexOf(currTask);
         container.getChildTasks().add(insertionIndex, task);
      }

      return task;
   }

   /**
    * This method extracts assignment data from an MSPDI file.
    *
    * @param project Root node of the MSPDI file
    */
   private void readAssignments(Project project)
   {
      Project.Assignments assignments = project.getAssignments();
      if (assignments != null)
      {
         for (Project.Assignments.Assignment assignment : assignments.getAssignment())
         {
            readAssignment(assignment);
         }
      }
   }

   /**
    * This method extracts outline code/custom field data from an MSPDI file.
    *
    * @param project Root node of the MSPDI file
    */
   private void readOutlineCodeDefinitions(Project project)
   {
      Project.OutlineCodes outlineCodes = project.getOutlineCodes();
      if (outlineCodes != null)
      {
         for (Project.OutlineCodes.OutlineCode outlineCode : outlineCodes.getOutlineCode())
         {
            readOutlineCodeDefinition(outlineCode);
         }
      }
   }

   /**
    * This method extracts the definition of a single outline code/custom lookup table from an MSPDI file.
    *
    * @param outlineCode outline code data from the MSPDI file
    */
   private void readOutlineCodeDefinition(Project.OutlineCodes.OutlineCode outlineCode)
   {
      FieldType fieldType;
      String fieldID = outlineCode.getFieldID();
      if (fieldID == null)
      {
         fieldType = m_lookupTableMap.get(outlineCode.getGuid());
      }
      else
      {
         fieldType = FieldTypeHelper.getInstance(m_projectFile, NumberHelper.getInt(outlineCode.getFieldID()));
      }

      if (fieldType != null)
      {
         String newAlias = outlineCode.getAlias();
         if (newAlias != null && !newAlias.isEmpty())
         {
            CustomField field = m_projectFile.getCustomFields().get(fieldType);
            String currentAlias = field == null ? null : field.getAlias();

            // Don't overwrite an alias we've read from extended attributes
            if (currentAlias == null || currentAlias.isEmpty())
            {
               if (field == null)
               {
                  field = m_projectFile.getCustomFields().getOrCreate(fieldType);
               }
               field.setAlias(newAlias);
            }
         }

         readOutlineCodeValues(outlineCode, fieldType);
         readOutlineCodeMasks(outlineCode, fieldType);
      }
   }

   /**
    * This method extracts the lookup table values for an outline code/custom field from an MSPDI file.
    *
    * @param outlineCode outline code data from the MSPDI file
    * @param fieldType target field type
    */
   private void readOutlineCodeValues(Project.OutlineCodes.OutlineCode outlineCode, FieldType fieldType)
   {
      Project.OutlineCodes.OutlineCode.Values values = outlineCode.getValues();
      if (values == null)
      {
         return;
      }

      // I have come across one example MSPDI file with duplicate unique IDs
      // for custom field values, possibly written by P6.

      // Ensure our ObjectSequence is in sync with the values from the file
      ObjectSequence uniqueIdSequence = m_projectFile.getUniqueIdObjectSequence(CustomFieldValueItem.class);
      values.getValue().forEach(v -> uniqueIdSequence.sync(NumberHelper.getInteger(v.getValueID())));

      CustomField field = m_projectFile.getCustomFields().getOrCreate(fieldType);
      CustomFieldLookupTable table = field.getLookupTable();

      table.setEnterprise(BooleanHelper.getBoolean(outlineCode.isEnterprise()));
      table.setAllLevelsRequired(BooleanHelper.getBoolean(outlineCode.isAllLevelsRequired()));
      table.setGUID(outlineCode.getGuid());
      table.setLeafOnly(BooleanHelper.getBoolean(outlineCode.isLeafOnly()));
      table.setOnlyTableValuesAllowed(BooleanHelper.getBoolean(outlineCode.isOnlyTableValuesAllowed()));
      table.setResourceSubstitutionEnabled(BooleanHelper.getBoolean(outlineCode.isResourceSubstitutionEnabled()));
      table.setShowIndent(BooleanHelper.getBoolean(outlineCode.isShowIndent()));

      Set<Integer> uniqueIdValues = new HashSet<>();
      for (Project.OutlineCodes.OutlineCode.Values.Value value : values.getValue())
      {
         Integer uniqueID = NumberHelper.getInteger(value.getValueID());
         if (uniqueIdValues.contains(uniqueID))
         {
            // Assign a new unique ID using the object sequence
            uniqueID = uniqueIdSequence.getNext();
         }
         uniqueIdValues.add(uniqueID);

         CustomFieldValueItem item = new CustomFieldValueItem(uniqueID);
         item.setDescription(value.getDescription());
         item.setGUID(value.getFieldGUID());
         item.setCollapsed(BooleanHelper.getBoolean(value.isIsCollapsed()));
         item.setParentUniqueID(NumberHelper.getInteger(value.getParentValueID()));
         item.setType(CustomFieldValueDataType.getInstance(NumberHelper.getInt(value.getType())));
         item.setValue(DatatypeConverter.parseOutlineCodeValue(value.getValue(), field.getFieldType().getDataType()));
         table.add(item);
      }
   }

   /**
    * This method extracts the lookup table masks for an outline code/custom field from an MSPDI file.
    *
    * @param outlineCode outline code data from the MSPDI file
    * @param fieldType target field type
    */
   private void readOutlineCodeMasks(Project.OutlineCodes.OutlineCode outlineCode, FieldType fieldType)
   {
      Project.OutlineCodes.OutlineCode.Masks masks = outlineCode.getMasks();
      if (masks != null && !masks.getMask().isEmpty())
      {
         CustomField field = m_projectFile.getCustomFields().getOrCreate(fieldType);
         List<CustomFieldValueMask> maskList = field.getMasks();
         for (Project.OutlineCodes.OutlineCode.Masks.Mask mask : masks.getMask())
         {
            int length = NumberHelper.getInt(mask.getLength());
            int level = NumberHelper.getInt(mask.getLevel());
            String separator = mask.getSeparator();
            CustomFieldValueDataType type = CustomFieldValueDataType.getInstanceByMaskValue(NumberHelper.getInt(mask.getType()));
            if (type == null)
            {
               type = CustomFieldValueDataType.TEXT;
            }
            CustomFieldValueMask item = new CustomFieldValueMask(length, level, separator, type);
            maskList.add(item);
         }
      }
   }

   /**
    * This method extracts data for a single assignment from an MSPDI file.
    *
    * @param assignment Assignment data
    */
   private void readAssignment(Project.Assignments.Assignment assignment)
   {
      BigInteger taskUID = assignment.getTaskUID();
      BigInteger resourceUID = assignment.getResourceUID();
      if (taskUID != null && resourceUID != null)
      {
         Task task = m_projectFile.getTaskByUniqueID(Integer.valueOf(taskUID.intValue()));
         if (task != null)
         {
            ResourceAssignment mpx = task.addResourceAssignment(m_projectFile.getResourceByUniqueID(Integer.valueOf(resourceUID.intValue())));
            ProjectCalendar calendar = mpx.getEffectiveCalendar();

            List<TimephasedWork> timephasedComplete = readTimephasedWork(assignment, 2);
            List<TimephasedWork> timephasedPlanned = readTimephasedWork(assignment, 1);
            boolean raw = true;

            // TODO: this assumes that timephased data for all assignments of a task is the same
            if (isSplit(calendar, timephasedComplete) || isSplit(calendar, timephasedPlanned))
            {
               MSPDITimephasedWorkNormaliser.INSTANCE.normalise(calendar, mpx, timephasedComplete);
               MSPDITimephasedWorkNormaliser.INSTANCE.normalise(calendar, mpx, timephasedPlanned);
               SplitTaskFactory.processSplitData(mpx, timephasedComplete, timephasedPlanned);
               raw = false;
            }

            DefaultTimephasedWorkContainer timephasedCompleteData = new DefaultTimephasedWorkContainer(mpx, MSPDITimephasedWorkNormaliser.INSTANCE, timephasedComplete, raw);
            DefaultTimephasedWorkContainer timephasedPlannedData = new DefaultTimephasedWorkContainer(mpx, MSPDITimephasedWorkNormaliser.INSTANCE, timephasedPlanned, raw);

            mpx.setActualCost(DatatypeConverter.parseCurrency(assignment.getActualCost()));
            mpx.setActualFinish(assignment.getActualFinish());
            mpx.setActualOvertimeCost(DatatypeConverter.parseCurrency(assignment.getActualOvertimeCost()));
            mpx.setActualOvertimeWork(DatatypeConverter.parseDuration(m_projectFile, TimeUnit.HOURS, assignment.getActualOvertimeWork()));
            //assignment.getActualOvertimeWorkProtected()
            mpx.setActualStart(assignment.getActualStart());
            //assignment.getActualWorkProtected()
            mpx.setACWP(DatatypeConverter.parseCurrency(assignment.getACWP()));
            mpx.setBCWP(DatatypeConverter.parseCurrency(assignment.getBCWP()));
            mpx.setBCWS(DatatypeConverter.parseCurrency(assignment.getBCWS()));
            //assignment.getBookingType()
            mpx.setBudgetCost(DatatypeConverter.parseCurrency(assignment.getBudgetCost()));
            mpx.setBudgetWork(DatatypeConverter.parseDuration(m_projectFile, TimeUnit.HOURS, assignment.getBudgetWork()));
            mpx.setCost(DatatypeConverter.parseCurrency(assignment.getCost()));
            mpx.setCostRateTableIndex(NumberHelper.getInt(assignment.getCostRateTable()));
            mpx.setCreateDate(assignment.getCreationDate());
            mpx.setCV(DatatypeConverter.parseCurrency(assignment.getCV()));
            mpx.setDelay(DatatypeConverter.parseDurationInTenthsOfMinutes(assignment.getDelay()));
            mpx.setFinish(assignment.getFinish());
            mpx.setVariableRateUnits(BooleanHelper.getBoolean(assignment.isHasFixedRateUnits()) ? null : DatatypeConverter.parseTimeUnit(assignment.getRateScale()));
            mpx.setGUID(assignment.getGUID());
            mpx.setHyperlink(assignment.getHyperlink());
            mpx.setHyperlinkAddress(assignment.getHyperlinkAddress());
            mpx.setHyperlinkSubAddress(assignment.getHyperlinkSubAddress());
            mpx.setLevelingDelay(DatatypeConverter.parseDurationInTenthsOfMinutes(m_projectFile.getProjectProperties(), assignment.getLevelingDelay(), DatatypeConverter.parseDurationTimeUnits(assignment.getLevelingDelayFormat())));
            mpx.setNotes(assignment.getNotes());
            mpx.setOvertimeCost(DatatypeConverter.parseCurrency(assignment.getOvertimeCost()));
            mpx.setOvertimeWork(DatatypeConverter.parseDuration(m_projectFile, TimeUnit.HOURS, assignment.getOvertimeWork()));
            //mpx.setPlannedCost();
            //mpx.setPlannedWork();
            mpx.setRegularWork(DatatypeConverter.parseDuration(m_projectFile, TimeUnit.HOURS, assignment.getRegularWork()));
            mpx.setRemainingCost(DatatypeConverter.parseCurrency(assignment.getRemainingCost()));
            mpx.setRemainingOvertimeCost(DatatypeConverter.parseCurrency(assignment.getRemainingOvertimeCost()));
            mpx.setRemainingOvertimeWork(DatatypeConverter.parseDuration(m_projectFile, TimeUnit.HOURS, assignment.getRemainingOvertimeWork()));
            mpx.setResume(assignment.getResume());
            mpx.setStart(assignment.getStart());
            mpx.setStop(assignment.getStop());
            mpx.setSV(DatatypeConverter.parseCurrency(assignment.getSV()));
            mpx.setUniqueID(NumberHelper.getInteger(assignment.getUID()));
            mpx.setUnits(DatatypeConverter.parseUnits(assignment.getUnits()));
            mpx.setVAC(DatatypeConverter.parseCurrency(assignment.getVAC()));
            mpx.setWork(DatatypeConverter.parseDuration(m_projectFile, TimeUnit.HOURS, assignment.getWork()));
            mpx.setWorkContour(assignment.getWorkContour());

            mpx.setTimephasedActualWork(timephasedCompleteData);
            mpx.setTimephasedWork(timephasedPlannedData);

            readAssignmentExtendedAttributes(assignment, mpx);

            readAssignmentBaselines(assignment, mpx);

            // Read last to ensure caching works as expected
            mpx.setActualWork(DatatypeConverter.parseDuration(m_projectFile, TimeUnit.HOURS, assignment.getActualWork()));
            mpx.setRemainingWork(DatatypeConverter.parseDuration(m_projectFile, TimeUnit.HOURS, assignment.getRemainingWork()));
            mpx.setPercentageWorkComplete(assignment.getPercentWorkComplete());

            mpx.setCostVariance(DatatypeConverter.parseCurrency(assignment.getCostVariance()));
            mpx.setWorkVariance(DatatypeConverter.parseDurationInThousanthsOfMinutes(m_projectFile.getProjectProperties(), assignment.getWorkVariance(), TimeUnit.HOURS));
            mpx.setStartVariance(DatatypeConverter.parseDurationInTenthsOfMinutes(m_projectFile.getProjectProperties(), assignment.getStartVariance(), TimeUnit.DAYS));
            mpx.setFinishVariance(DatatypeConverter.parseDurationInTenthsOfMinutes(m_projectFile.getProjectProperties(), assignment.getFinishVariance(), TimeUnit.DAYS));

            // Process baseline timephased work
            for (Map.Entry<Integer, TimephasedWorkAssignmentFunction> entry : TIMEPHASED_BASELINE_WORK_MAP.entrySet())
            {
               List<TimephasedWork> timephasedData = readTimephasedWork(assignment, entry.getKey().intValue());
               if (!timephasedData.isEmpty())
               {
                  entry.getValue().apply(mpx, new DefaultTimephasedWorkContainer(mpx, MSPDITimephasedWorkNormaliser.INSTANCE, timephasedData, true));
               }
            }

            // Process baseline timephased cost
            for (Map.Entry<Integer, TimephasedCostAssignmentFunction> entry : TIMEPHASED_BASELINE_COST_MAP.entrySet())
            {
               List<TimephasedCost> timephasedData = readTimephasedCost(assignment, entry.getKey().intValue());
               if (!timephasedData.isEmpty())
               {
                  entry.getValue().apply(mpx, new DefaultTimephasedCostContainer(mpx, MPPTimephasedBaselineCostNormaliser.INSTANCE, timephasedData, true));
               }
            }

            m_eventManager.fireAssignmentReadEvent(mpx);
         }
      }
   }

   /**
    * Extracts assignment baseline data.
    *
    * @param assignment xml assignment
    * @param mpx mpxj assignment
    */
   private void readAssignmentBaselines(Project.Assignments.Assignment assignment, ResourceAssignment mpx)
   {
      for (Project.Assignments.Assignment.Baseline baseline : assignment.getBaseline())
      {
         int number = NumberHelper.getInt(baseline.getNumber());

         //baseline.getBCWP()
         //baseline.getBCWS()
         Number cost = DatatypeConverter.parseCustomFieldCurrency(baseline.getCost());
         LocalDateTime finish = DatatypeConverter.parseCustomFieldDate(baseline.getFinish());
         //baseline.getNumber()
         LocalDateTime start = DatatypeConverter.parseCustomFieldDate(baseline.getStart());
         Duration work = DatatypeConverter.parseDuration(m_projectFile, TimeUnit.HOURS, baseline.getWork());

         if (number == 0)
         {
            mpx.setBaselineCost(cost);
            mpx.setBaselineFinish(finish);
            mpx.setBaselineStart(start);
            mpx.setBaselineWork(work);
         }
         else
         {
            mpx.setBaselineCost(number, cost);
            mpx.setBaselineWork(number, work);
            mpx.setBaselineStart(number, start);
            mpx.setBaselineFinish(number, finish);
         }
      }
   }

   /**
    * This method processes any extended attributes associated with a
    * resource assignment.
    *
    * @param xml MSPDI resource assignment instance
    * @param mpx MPX task instance
    */
   private void readAssignmentExtendedAttributes(Project.Assignments.Assignment xml, ResourceAssignment mpx)
   {
      for (Project.Assignments.Assignment.ExtendedAttribute attrib : xml.getExtendedAttribute())
      {
         FieldType mpxFieldID = FieldTypeHelper.getInstance(m_projectFile, Integer.parseInt(attrib.getFieldID()));
         TimeUnit durationFormat = DatatypeConverter.parseDurationTimeUnits(attrib.getDurationFormat(), null);
         DatatypeConverter.parseCustomField(m_projectFile, mpx, attrib.getValue(), mpxFieldID, durationFormat);
      }
   }

   /**
    * Test to determine if this is a split task.
    *
    * @param calendar current calendar
    * @param list timephased resource assignment list
    * @return boolean flag
    */
   private boolean isSplit(ProjectCalendar calendar, List<TimephasedWork> list)
   {
      boolean result = false;
      for (TimephasedWork assignment : list)
      {
         if (calendar != null && assignment.getTotalAmount().getDuration() == 0)
         {
            Duration calendarWork = calendar.getWork(assignment.getStart(), assignment.getFinish(), TimeUnit.MINUTES);
            if (calendarWork.getDuration() != 0)
            {
               result = true;
               break;
            }
         }
      }
      return result;
   }

   /**
    * Reads timephased work data.
    *
    * @param assignment assignment data
    * @param type flag indicating the type of timephased data to read
    * @return list of TimephasedWork instances
    */
   private List<TimephasedWork> readTimephasedWork(Project.Assignments.Assignment assignment, int type)
   {
      // Note that we exclude ranges which don't have a start and end date.
      // These seem to be generated by Synchro and have a zero duration.
      return assignment.getTimephasedData().stream().filter(i -> NumberHelper.getInt(i.getType()) == type && i.getStart() != null && i.getFinish() != null).map(this::readTimephasedWork).collect(Collectors.toList());
   }

   /**
    * Reads timephased cost data.
    *
    * @param assignment assignment data
    * @param type flag indicating the type of timephased data to read
    * @return list of TimephasedCost instances
    */
   private List<TimephasedCost> readTimephasedCost(Project.Assignments.Assignment assignment, int type)
   {
      // Note that we exclude ranges which don't have a start and end date.
      // These seem to be generated by Synchro and have a zero cost.
      return assignment.getTimephasedData().stream().filter(i -> NumberHelper.getInt(i.getType()) == type && i.getStart() != null && i.getFinish() != null).map(this::readTimephasedCost).collect(Collectors.toList());
   }

   private TimephasedWork readTimephasedWork(TimephasedDataType item)
   {
      Duration work = DatatypeConverter.parseDuration(m_projectFile, TimeUnit.MINUTES, item.getValue());
      if (work == null)
      {
         work = Duration.getInstance(0, TimeUnit.MINUTES);
      }
      else
      {
         work = Duration.getInstance(NumberHelper.round(work.getDuration(), 2), TimeUnit.MINUTES);
      }

      TimephasedWork timephasedWork = new TimephasedWork();
      timephasedWork.setStart(item.getStart());
      timephasedWork.setFinish(item.getFinish());
      timephasedWork.setTotalAmount(work);
      return timephasedWork;
   }

   private TimephasedCost readTimephasedCost(TimephasedDataType item)
   {
      String value = item.getValue();
      Double currency = value == null || value.isEmpty() ? NumberHelper.DOUBLE_ZERO : Double.valueOf(item.getValue());
      TimephasedCost timephasedCost = new TimephasedCost();
      timephasedCost.setStart(item.getStart());
      timephasedCost.setFinish(item.getFinish());
      timephasedCost.setTotalAmount(DatatypeConverter.parseCurrency(currency));
      return timephasedCost;
   }

   /**
    * Sets a flag indicating that this class will attempt to correct
    * and read XML which is not compliant with the XML Schema. This
    * behaviour matches that of Microsoft Project when reading the
    * same data.
    *
    * @param flag input compatibility flag
    */
   public void setMicrosoftProjectCompatibleInput(boolean flag)
   {
      m_compatibleInput = flag;
   }

   /**
    * Retrieves a flag indicating that this class will attempt to correct
    * and read XML which is not compliant with the XML Schema. This
    * behaviour matches that of Microsoft Project when reading the
    * same data.
    *
    * @return Boolean flag
    */
   public boolean getMicrosoftProjectCompatibleInput()
   {
      return m_compatibleInput;
   }

   /**
    * Set a flag to determine if datatype parse errors can be ignored.
    * Defaults to true.
    *
    * @param ignoreErrors pass true to ignore errors
    */
   public void setIgnoreErrors(boolean ignoreErrors)
   {
      m_ignoreErrors = ignoreErrors;
   }

   /**
    * Retrieve the flag which determines if datatype parse errors can be ignored.
    * Defaults to true.
    *
    * @return true if datatype parse errors are ignored
    */
   public boolean getIgnoreErrors()
   {
      return m_ignoreErrors;
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
         CONTEXT = JAXBContext.newInstance("org.mpxj.mspdi.schema", MSPDIReader.class.getClassLoader());
      }

      catch (JAXBException ex)
      {
         CONTEXT_EXCEPTION = ex;
         CONTEXT = null;
      }
   }

   private boolean m_compatibleInput = true;
   private boolean m_ignoreErrors = true;
   private Charset m_charset = CharsetHelper.UTF8;
   private ProjectFile m_projectFile;
   private EventManager m_eventManager;
   private Map<UUID, FieldType> m_lookupTableMap;
   private Map<FieldType, Map<BigInteger, CustomFieldValueItem>> m_customFieldValueItems;
   private Map<Task, Project> m_externalProjects;

   private static final RecurrenceType[] RECURRENCE_TYPES =
   {
      null,
      RecurrenceType.DAILY,
      RecurrenceType.YEARLY, // Absolute
      RecurrenceType.YEARLY, // Relative
      RecurrenceType.MONTHLY, // Absolute
      RecurrenceType.MONTHLY, // Relative
      RecurrenceType.WEEKLY,
      RecurrenceType.DAILY
   };

   private static final boolean[] RELATIVE_MAP =
   {
      false,
      false,
      false,
      true,
      false,
      true
   };

   private static final int[] DAY_MASKS =
   {
      0x00,
      0x01, // Sunday
      0x02, // Monday
      0x04, // Tuesday
      0x08, // Wednesday
      0x10, // Thursday
      0x20, // Friday
      0x40, // Saturday
   };

   interface TimephasedWorkAssignmentFunction
   {
      void apply(ResourceAssignment assignment, TimephasedWorkContainer container);
   }

   interface TimephasedCostAssignmentFunction
   {
      void apply(ResourceAssignment assignment, TimephasedCostContainer container);
   }

   private static final Map<Integer, TimephasedWorkAssignmentFunction> TIMEPHASED_BASELINE_WORK_MAP = new HashMap<>();
   static
   {
      TIMEPHASED_BASELINE_WORK_MAP.put(Integer.valueOf(4), (a, c) -> a.setTimephasedBaselineWork(0, c));
      TIMEPHASED_BASELINE_WORK_MAP.put(Integer.valueOf(16), (a, c) -> a.setTimephasedBaselineWork(1, c));
      TIMEPHASED_BASELINE_WORK_MAP.put(Integer.valueOf(22), (a, c) -> a.setTimephasedBaselineWork(2, c));
      TIMEPHASED_BASELINE_WORK_MAP.put(Integer.valueOf(28), (a, c) -> a.setTimephasedBaselineWork(3, c));
      TIMEPHASED_BASELINE_WORK_MAP.put(Integer.valueOf(34), (a, c) -> a.setTimephasedBaselineWork(4, c));
      TIMEPHASED_BASELINE_WORK_MAP.put(Integer.valueOf(40), (a, c) -> a.setTimephasedBaselineWork(5, c));
      TIMEPHASED_BASELINE_WORK_MAP.put(Integer.valueOf(46), (a, c) -> a.setTimephasedBaselineWork(6, c));
      TIMEPHASED_BASELINE_WORK_MAP.put(Integer.valueOf(52), (a, c) -> a.setTimephasedBaselineWork(7, c));
      TIMEPHASED_BASELINE_WORK_MAP.put(Integer.valueOf(58), (a, c) -> a.setTimephasedBaselineWork(8, c));
      TIMEPHASED_BASELINE_WORK_MAP.put(Integer.valueOf(64), (a, c) -> a.setTimephasedBaselineWork(9, c));
      TIMEPHASED_BASELINE_WORK_MAP.put(Integer.valueOf(70), (a, c) -> a.setTimephasedBaselineWork(10, c));
   }

   private static final Map<Integer, TimephasedCostAssignmentFunction> TIMEPHASED_BASELINE_COST_MAP = new HashMap<>();
   static
   {
      TIMEPHASED_BASELINE_COST_MAP.put(Integer.valueOf(5), (a, c) -> a.setTimephasedBaselineCost(0, c));
      TIMEPHASED_BASELINE_COST_MAP.put(Integer.valueOf(17), (a, c) -> a.setTimephasedBaselineCost(1, c));
      TIMEPHASED_BASELINE_COST_MAP.put(Integer.valueOf(23), (a, c) -> a.setTimephasedBaselineCost(2, c));
      TIMEPHASED_BASELINE_COST_MAP.put(Integer.valueOf(29), (a, c) -> a.setTimephasedBaselineCost(3, c));
      TIMEPHASED_BASELINE_COST_MAP.put(Integer.valueOf(35), (a, c) -> a.setTimephasedBaselineCost(4, c));
      TIMEPHASED_BASELINE_COST_MAP.put(Integer.valueOf(41), (a, c) -> a.setTimephasedBaselineCost(5, c));
      TIMEPHASED_BASELINE_COST_MAP.put(Integer.valueOf(47), (a, c) -> a.setTimephasedBaselineCost(6, c));
      TIMEPHASED_BASELINE_COST_MAP.put(Integer.valueOf(53), (a, c) -> a.setTimephasedBaselineCost(7, c));
      TIMEPHASED_BASELINE_COST_MAP.put(Integer.valueOf(59), (a, c) -> a.setTimephasedBaselineCost(8, c));
      TIMEPHASED_BASELINE_COST_MAP.put(Integer.valueOf(65), (a, c) -> a.setTimephasedBaselineCost(9, c));
      TIMEPHASED_BASELINE_COST_MAP.put(Integer.valueOf(71), (a, c) -> a.setTimephasedBaselineCost(10, c));
   }
}
