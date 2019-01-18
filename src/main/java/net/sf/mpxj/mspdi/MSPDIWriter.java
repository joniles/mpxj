/*
 * file:       MSPDIWriter.java
 * author:     Jon Iles
 * copyright:  (c) Packwood Software 2005
 * date:       2005-12-30
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

package net.sf.mpxj.mspdi;

import java.io.IOException;
import java.io.OutputStream;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;

import net.sf.mpxj.AccrueType;
import net.sf.mpxj.AssignmentField;
import net.sf.mpxj.Availability;
import net.sf.mpxj.CostRateTable;
import net.sf.mpxj.CostRateTableEntry;
import net.sf.mpxj.CustomField;
import net.sf.mpxj.CustomFieldContainer;
import net.sf.mpxj.DateRange;
import net.sf.mpxj.Day;
import net.sf.mpxj.DayType;
import net.sf.mpxj.Duration;
import net.sf.mpxj.EventManager;
import net.sf.mpxj.FieldType;
import net.sf.mpxj.ProjectCalendar;
import net.sf.mpxj.ProjectCalendarException;
import net.sf.mpxj.ProjectCalendarHours;
import net.sf.mpxj.ProjectCalendarWeek;
import net.sf.mpxj.ProjectConfig;
import net.sf.mpxj.ProjectFile;
import net.sf.mpxj.ProjectProperties;
import net.sf.mpxj.RecurringData;
import net.sf.mpxj.Relation;
import net.sf.mpxj.RelationType;
import net.sf.mpxj.Resource;
import net.sf.mpxj.ResourceAssignment;
import net.sf.mpxj.ResourceField;
import net.sf.mpxj.ResourceType;
import net.sf.mpxj.ScheduleFrom;
import net.sf.mpxj.Task;
import net.sf.mpxj.TaskField;
import net.sf.mpxj.TaskMode;
import net.sf.mpxj.TimeUnit;
import net.sf.mpxj.TimephasedWork;
import net.sf.mpxj.common.AssignmentFieldLists;
import net.sf.mpxj.common.DateHelper;
import net.sf.mpxj.common.FieldTypeHelper;
import net.sf.mpxj.common.MPPAssignmentField;
import net.sf.mpxj.common.MPPResourceField;
import net.sf.mpxj.common.MPPTaskField;
import net.sf.mpxj.common.NumberHelper;
import net.sf.mpxj.common.ResourceFieldLists;
import net.sf.mpxj.common.TaskFieldLists;
import net.sf.mpxj.mspdi.schema.ObjectFactory;
import net.sf.mpxj.mspdi.schema.Project;
import net.sf.mpxj.mspdi.schema.Project.Calendars.Calendar.Exceptions;
import net.sf.mpxj.mspdi.schema.Project.Calendars.Calendar.WorkWeeks;
import net.sf.mpxj.mspdi.schema.Project.Calendars.Calendar.WorkWeeks.WorkWeek;
import net.sf.mpxj.mspdi.schema.Project.Calendars.Calendar.WorkWeeks.WorkWeek.TimePeriod;
import net.sf.mpxj.mspdi.schema.Project.Calendars.Calendar.WorkWeeks.WorkWeek.WeekDays;
import net.sf.mpxj.mspdi.schema.Project.Resources.Resource.AvailabilityPeriods;
import net.sf.mpxj.mspdi.schema.Project.Resources.Resource.AvailabilityPeriods.AvailabilityPeriod;
import net.sf.mpxj.mspdi.schema.Project.Resources.Resource.Rates;
import net.sf.mpxj.mspdi.schema.TimephasedDataType;
import net.sf.mpxj.writer.AbstractProjectWriter;

/**
 * This class creates a new MSPDI file from the contents of an ProjectFile instance.
 */
public final class MSPDIWriter extends AbstractProjectWriter
{
   /**
    * Sets a flag to control whether timephased assignment data is split
    * into days. The default is true.
    *
    * @param flag boolean flag
    */
   public void setSplitTimephasedAsDays(boolean flag)
   {
      m_splitTimephasedAsDays = flag;
   }

   /**
    * Retrieves a flag to control whether timephased assignment data is split
    * into days. The default is true.
    *
    * @return boolean true
    */
   public boolean getSplitTimephasedAsDays()
   {
      return m_splitTimephasedAsDays;
   }

   /**
    * Sets a flag to control whether timephased resource assignment data
    * is written to the file. The default is false.
    *
    * @param value boolean flag
    */
   public void setWriteTimephasedData(boolean value)
   {
      m_writeTimphasedData = value;
   }

   /**
    * Retrieves the state of the flag which controls whether timephased
    * resource assignment data is written to the file. The default is false.
    *
    * @return boolean flag
    */
   public boolean getWriteTimephasedData()
   {
      return m_writeTimphasedData;
   }

   /**
    * Set the save version to use when generating an MSPDI file.
    *
    * @param version save version
    */
   public void setSaveVersion(SaveVersion version)
   {
      m_saveVersion = version;
   }

   /**
    * Retrieve the save version current set.
    *
    * @return current save version
    */
   public SaveVersion getSaveVersion()
   {
      return m_saveVersion;
   }

   /**
    * {@inheritDoc}
    */
   @Override public void write(ProjectFile projectFile, OutputStream stream) throws IOException
   {
      try
      {
         if (CONTEXT == null)
         {
            throw CONTEXT_EXCEPTION;
         }

         m_projectFile = projectFile;
         m_projectFile.validateUniqueIDsForMicrosoftProject();
         m_eventManager = m_projectFile.getEventManager();
         DatatypeConverter.setParentFile(m_projectFile);

         Marshaller marshaller = CONTEXT.createMarshaller();
         marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);

         m_extendedAttributesInUse = new HashSet<FieldType>();

         m_factory = new ObjectFactory();
         Project project = m_factory.createProject();

         writeProjectProperties(project);
         writeCalendars(project);
         writeResources(project);
         writeTasks(project);
         writeAssignments(project);
         writeProjectExtendedAttributes(project);

         marshaller.marshal(project, stream);
      }

      catch (JAXBException ex)
      {
         throw new IOException(ex.toString());
      }

      finally
      {
         m_projectFile = null;
         m_factory = null;
         m_extendedAttributesInUse = null;
      }
   }

   /**
    * This method writes project properties to an MSPDI file.
    *
    * @param project Root node of the MSPDI file
    */
   private void writeProjectProperties(Project project)
   {
      ProjectProperties properties = m_projectFile.getProjectProperties();

      project.setActualsInSync(Boolean.valueOf(properties.getActualsInSync()));
      project.setAdminProject(Boolean.valueOf(properties.getAdminProject()));
      project.setAuthor(properties.getAuthor());
      project.setAutoAddNewResourcesAndTasks(Boolean.valueOf(properties.getAutoAddNewResourcesAndTasks()));
      project.setAutolink(Boolean.valueOf(properties.getAutolink()));
      project.setBaselineForEarnedValue(NumberHelper.getBigInteger(properties.getBaselineForEarnedValue()));
      project.setCalendarUID(m_projectFile.getDefaultCalendar() == null ? BigInteger.ONE : NumberHelper.getBigInteger(m_projectFile.getDefaultCalendar().getUniqueID()));
      project.setCategory(properties.getCategory());
      project.setCompany(properties.getCompany());
      project.setCreationDate(properties.getCreationDate());
      project.setCriticalSlackLimit(NumberHelper.getBigInteger(properties.getCriticalSlackLimit()));
      project.setCurrencyCode(properties.getCurrencyCode());
      project.setCurrencyDigits(BigInteger.valueOf(properties.getCurrencyDigits().intValue()));
      project.setCurrencySymbol(properties.getCurrencySymbol());
      project.setCurrencySymbolPosition(properties.getSymbolPosition());
      project.setCurrentDate(properties.getCurrentDate());
      project.setDaysPerMonth(NumberHelper.getBigInteger(properties.getDaysPerMonth()));
      project.setDefaultFinishTime(properties.getDefaultEndTime());
      project.setDefaultFixedCostAccrual(properties.getDefaultFixedCostAccrual());
      project.setDefaultOvertimeRate(DatatypeConverter.printRate(properties.getDefaultOvertimeRate()));
      project.setDefaultStandardRate(DatatypeConverter.printRate(properties.getDefaultStandardRate()));
      project.setDefaultStartTime(properties.getDefaultStartTime());
      project.setDefaultTaskEVMethod(DatatypeConverter.printEarnedValueMethod(properties.getDefaultTaskEarnedValueMethod()));
      project.setDefaultTaskType(properties.getDefaultTaskType());
      project.setDurationFormat(DatatypeConverter.printDurationTimeUnits(properties.getDefaultDurationUnits(), false));
      project.setEarnedValueMethod(DatatypeConverter.printEarnedValueMethod(properties.getEarnedValueMethod()));
      project.setEditableActualCosts(Boolean.valueOf(properties.getEditableActualCosts()));
      project.setExtendedCreationDate(properties.getExtendedCreationDate());
      project.setFinishDate(properties.getFinishDate());
      project.setFiscalYearStart(Boolean.valueOf(properties.getFiscalYearStart()));
      project.setFYStartDate(NumberHelper.getBigInteger(properties.getFiscalYearStartMonth()));
      project.setHonorConstraints(Boolean.valueOf(properties.getHonorConstraints()));
      project.setInsertedProjectsLikeSummary(Boolean.valueOf(properties.getInsertedProjectsLikeSummary()));
      project.setLastSaved(properties.getLastSaved());
      project.setManager(properties.getManager());
      project.setMicrosoftProjectServerURL(Boolean.valueOf(properties.getMicrosoftProjectServerURL()));
      project.setMinutesPerDay(NumberHelper.getBigInteger(properties.getMinutesPerDay()));
      project.setMinutesPerWeek(NumberHelper.getBigInteger(properties.getMinutesPerWeek()));
      project.setMoveCompletedEndsBack(Boolean.valueOf(properties.getMoveCompletedEndsBack()));
      project.setMoveCompletedEndsForward(Boolean.valueOf(properties.getMoveCompletedEndsForward()));
      project.setMoveRemainingStartsBack(Boolean.valueOf(properties.getMoveRemainingStartsBack()));
      project.setMoveRemainingStartsForward(Boolean.valueOf(properties.getMoveRemainingStartsForward()));
      project.setMultipleCriticalPaths(Boolean.valueOf(properties.getMultipleCriticalPaths()));
      project.setName(properties.getName());
      project.setNewTasksEffortDriven(Boolean.valueOf(properties.getNewTasksEffortDriven()));
      project.setNewTasksEstimated(Boolean.valueOf(properties.getNewTasksEstimated()));
      project.setNewTaskStartDate(properties.getNewTaskStartIsProjectStart() == true ? BigInteger.ZERO : BigInteger.ONE);
      project.setProjectExternallyEdited(Boolean.valueOf(properties.getProjectExternallyEdited()));
      project.setRemoveFileProperties(Boolean.valueOf(properties.getRemoveFileProperties()));
      project.setRevision(NumberHelper.getBigInteger(properties.getRevision()));
      project.setSaveVersion(BigInteger.valueOf(m_saveVersion.getValue()));
      project.setScheduleFromStart(Boolean.valueOf(properties.getScheduleFrom() == ScheduleFrom.START));
      project.setSplitsInProgressTasks(Boolean.valueOf(properties.getSplitInProgressTasks()));
      project.setSpreadActualCost(Boolean.valueOf(properties.getSpreadActualCost()));
      project.setSpreadPercentComplete(Boolean.valueOf(properties.getSpreadPercentComplete()));
      project.setStartDate(properties.getStartDate());
      project.setStatusDate(properties.getStatusDate());
      project.setSubject(properties.getSubject());
      project.setTaskUpdatesResource(Boolean.valueOf(properties.getUpdatingTaskStatusUpdatesResourceStatus()));
      project.setTitle(properties.getProjectTitle());
      project.setUID(properties.getUniqueID());
      project.setWeekStartDay(DatatypeConverter.printDay(properties.getWeekStartDay()));
      project.setWorkFormat(DatatypeConverter.printWorkUnits(properties.getDefaultWorkUnits()));
   }

   /**
    * This method writes project extended attribute data into an MSPDI file.
    *
    * @param project Root node of the MSPDI file
    */
   private void writeProjectExtendedAttributes(Project project)
   {
      Project.ExtendedAttributes attributes = m_factory.createProjectExtendedAttributes();
      project.setExtendedAttributes(attributes);
      List<Project.ExtendedAttributes.ExtendedAttribute> list = attributes.getExtendedAttribute();

      Set<FieldType> customFields = new HashSet<FieldType>();
      for (CustomField customField : m_projectFile.getCustomFields())
      {
         FieldType fieldType = customField.getFieldType();
         if (fieldType != null)
         {
            customFields.add(fieldType);
         }
      }

      customFields.addAll(m_extendedAttributesInUse);
      
      List<FieldType> customFieldsList = new ArrayList<FieldType>();
      customFieldsList.addAll(customFields);
      

      // Sort to ensure consistent order in file
      final CustomFieldContainer customFieldContainer =  m_projectFile.getCustomFields();
      Collections.sort(customFieldsList, new Comparator<FieldType>()
      {
         @Override public int compare(FieldType o1, FieldType o2)
         {
            CustomField customField1 = customFieldContainer.getCustomField(o1);
            CustomField customField2 = customFieldContainer.getCustomField(o2);
            String name1 = o1.getClass().getSimpleName() + "." + o1.getName() + " " + customField1.getAlias();
            String name2 = o2.getClass().getSimpleName() + "." + o2.getName() + " " + customField2.getAlias();
            return name1.compareTo(name2);
         }
      });

      for (FieldType fieldType : customFieldsList)
      {
         Project.ExtendedAttributes.ExtendedAttribute attribute = m_factory.createProjectExtendedAttributesExtendedAttribute();
         list.add(attribute);
         attribute.setFieldID(String.valueOf(FieldTypeHelper.getFieldID(fieldType)));
         attribute.setFieldName(fieldType.getName());

         CustomField customField = customFieldContainer.getCustomField(fieldType);
         attribute.setAlias(customField.getAlias());
      }
   }

   /**
    * This method writes calendar data to an MSPDI file.
    *
    * @param project Root node of the MSPDI file
    */
   private void writeCalendars(Project project)
   {
      //
      // Create the new MSPDI calendar list
      //
      Project.Calendars calendars = m_factory.createProjectCalendars();
      project.setCalendars(calendars);
      List<Project.Calendars.Calendar> calendar = calendars.getCalendar();

      //
      // Process each calendar in turn
      //
      for (ProjectCalendar cal : m_projectFile.getCalendars())
      {
         calendar.add(writeCalendar(cal));
      }
   }

   /**
    * This method writes data for a single calendar to an MSPDI file.
    *
    * @param bc Base calendar data
    * @return New MSPDI calendar instance
    */
   private Project.Calendars.Calendar writeCalendar(ProjectCalendar bc)
   {
      //
      // Create a calendar
      //
      Project.Calendars.Calendar calendar = m_factory.createProjectCalendarsCalendar();
      calendar.setUID(NumberHelper.getBigInteger(bc.getUniqueID()));
      calendar.setIsBaseCalendar(Boolean.valueOf(!bc.isDerived()));

      ProjectCalendar base = bc.getParent();
      // SF-329: null default required to keep Powerproject happy when importing MSPDI files
      calendar.setBaseCalendarUID(base == null ? NULL_CALENDAR_ID : NumberHelper.getBigInteger(base.getUniqueID()));
      calendar.setName(bc.getName());

      //
      // Create a list of normal days
      //
      Project.Calendars.Calendar.WeekDays days = m_factory.createProjectCalendarsCalendarWeekDays();
      Project.Calendars.Calendar.WeekDays.WeekDay.WorkingTimes.WorkingTime time;
      ProjectCalendarHours bch;

      List<Project.Calendars.Calendar.WeekDays.WeekDay> dayList = days.getWeekDay();

      for (int loop = 1; loop < 8; loop++)
      {
         DayType workingFlag = bc.getWorkingDay(Day.getInstance(loop));

         if (workingFlag != DayType.DEFAULT)
         {
            Project.Calendars.Calendar.WeekDays.WeekDay day = m_factory.createProjectCalendarsCalendarWeekDaysWeekDay();
            dayList.add(day);
            day.setDayType(BigInteger.valueOf(loop));
            day.setDayWorking(Boolean.valueOf(workingFlag == DayType.WORKING));

            if (workingFlag == DayType.WORKING)
            {
               Project.Calendars.Calendar.WeekDays.WeekDay.WorkingTimes times = m_factory.createProjectCalendarsCalendarWeekDaysWeekDayWorkingTimes();
               day.setWorkingTimes(times);
               List<Project.Calendars.Calendar.WeekDays.WeekDay.WorkingTimes.WorkingTime> timesList = times.getWorkingTime();

               bch = bc.getCalendarHours(Day.getInstance(loop));
               if (bch != null)
               {
                  for (DateRange range : bch)
                  {
                     if (range != null)
                     {
                        time = m_factory.createProjectCalendarsCalendarWeekDaysWeekDayWorkingTimesWorkingTime();
                        timesList.add(time);

                        time.setFromTime(range.getStart());
                        time.setToTime(range.getEnd());
                     }
                  }
               }
            }
         }
      }

      //
      // Create a list of exceptions
      //
      // A quirk of MS Project is that these exceptions must be
      // in date order in the file, otherwise they are ignored
      //
      List<ProjectCalendarException> exceptions = new ArrayList<ProjectCalendarException>(bc.getCalendarExceptions());
      if (!exceptions.isEmpty())
      {
         Collections.sort(exceptions);
         writeExceptions(calendar, dayList, exceptions);
      }

      //
      // Do not add a weekdays tag to the calendar unless it
      // has valid entries.
      // Fixes SourceForge bug 1854747: MPXJ and MSP 2007 XML formats
      //
      if (!dayList.isEmpty())
      {
         calendar.setWeekDays(days);
      }

      writeWorkWeeks(calendar, bc);

      m_eventManager.fireCalendarWrittenEvent(bc);

      return (calendar);
   }

   /**
    * Main entry point used to determine the format used to write
    * calendar exceptions.
    *
    * @param calendar parent calendar
    * @param dayList list of calendar days
    * @param exceptions list of exceptions
    */
   private void writeExceptions(Project.Calendars.Calendar calendar, List<Project.Calendars.Calendar.WeekDays.WeekDay> dayList, List<ProjectCalendarException> exceptions)
   {
      // Always write legacy exception data:
      // Powerproject appears not to recognise new format data at all,
      // and legacy data is ignored in preference to new data post MSP 2003
      writeExceptions9(dayList, exceptions);

      if (m_saveVersion.getValue() > SaveVersion.Project2003.getValue())
      {
         writeExceptions12(calendar, exceptions);
      }
   }

   /**
    * Write exceptions in the format used by MSPDI files prior to Project 2007.
    *
    * @param dayList list of calendar days
    * @param exceptions list of exceptions
    */
   private void writeExceptions9(List<Project.Calendars.Calendar.WeekDays.WeekDay> dayList, List<ProjectCalendarException> exceptions)
   {
      for (ProjectCalendarException exception : exceptions)
      {
         boolean working = exception.getWorking();

         Project.Calendars.Calendar.WeekDays.WeekDay day = m_factory.createProjectCalendarsCalendarWeekDaysWeekDay();
         dayList.add(day);
         day.setDayType(BIGINTEGER_ZERO);
         day.setDayWorking(Boolean.valueOf(working));

         Project.Calendars.Calendar.WeekDays.WeekDay.TimePeriod period = m_factory.createProjectCalendarsCalendarWeekDaysWeekDayTimePeriod();
         day.setTimePeriod(period);
         period.setFromDate(exception.getFromDate());
         period.setToDate(exception.getToDate());

         if (working)
         {
            Project.Calendars.Calendar.WeekDays.WeekDay.WorkingTimes times = m_factory.createProjectCalendarsCalendarWeekDaysWeekDayWorkingTimes();
            day.setWorkingTimes(times);
            List<Project.Calendars.Calendar.WeekDays.WeekDay.WorkingTimes.WorkingTime> timesList = times.getWorkingTime();

            for (DateRange range : exception)
            {
               Project.Calendars.Calendar.WeekDays.WeekDay.WorkingTimes.WorkingTime time = m_factory.createProjectCalendarsCalendarWeekDaysWeekDayWorkingTimesWorkingTime();
               timesList.add(time);

               time.setFromTime(range.getStart());
               time.setToTime(range.getEnd());
            }
         }
      }
   }

   /**
    * Write exceptions into the format used by MSPDI files from
    * Project 2007 onwards.
    *
    * @param calendar parent calendar
    * @param exceptions list of exceptions
    */
   private void writeExceptions12(Project.Calendars.Calendar calendar, List<ProjectCalendarException> exceptions)
   {
      Exceptions ce = m_factory.createProjectCalendarsCalendarExceptions();
      calendar.setExceptions(ce);
      List<Exceptions.Exception> el = ce.getException();

      for (ProjectCalendarException exception : exceptions)
      {
         Exceptions.Exception ex = m_factory.createProjectCalendarsCalendarExceptionsException();
         el.add(ex);

         ex.setName(exception.getName());
         boolean working = exception.getWorking();
         ex.setDayWorking(Boolean.valueOf(working));

         if (exception.getRecurring() == null)
         {
            ex.setEnteredByOccurrences(Boolean.FALSE);
            ex.setOccurrences(BigInteger.ONE);
            ex.setType(BigInteger.ONE);
         }
         else
         {
            populateRecurringException(exception, ex);
         }

         Project.Calendars.Calendar.Exceptions.Exception.TimePeriod period = m_factory.createProjectCalendarsCalendarExceptionsExceptionTimePeriod();
         ex.setTimePeriod(period);
         period.setFromDate(exception.getFromDate());
         period.setToDate(exception.getToDate());

         if (working)
         {
            Project.Calendars.Calendar.Exceptions.Exception.WorkingTimes times = m_factory.createProjectCalendarsCalendarExceptionsExceptionWorkingTimes();
            ex.setWorkingTimes(times);
            List<Project.Calendars.Calendar.Exceptions.Exception.WorkingTimes.WorkingTime> timesList = times.getWorkingTime();

            for (DateRange range : exception)
            {
               Project.Calendars.Calendar.Exceptions.Exception.WorkingTimes.WorkingTime time = m_factory.createProjectCalendarsCalendarExceptionsExceptionWorkingTimesWorkingTime();
               timesList.add(time);

               time.setFromTime(range.getStart());
               time.setToTime(range.getEnd());
            }
         }
      }
   }

   /**
    * Writes the details of a recurring exception.
    *
    * @param mpxjException source MPXJ calendar exception
    * @param xmlException target MSPDI exception
    */
   private void populateRecurringException(ProjectCalendarException mpxjException, Exceptions.Exception xmlException)
   {
      RecurringData data = mpxjException.getRecurring();
      xmlException.setEnteredByOccurrences(Boolean.TRUE);
      xmlException.setOccurrences(NumberHelper.getBigInteger(data.getOccurrences()));

      switch (data.getRecurrenceType())
      {
         case DAILY:
         {
            xmlException.setType(BigInteger.valueOf(7));
            xmlException.setPeriod(NumberHelper.getBigInteger(data.getFrequency()));
            break;
         }

         case WEEKLY:
         {
            xmlException.setType(BigInteger.valueOf(6));
            xmlException.setPeriod(NumberHelper.getBigInteger(data.getFrequency()));
            xmlException.setDaysOfWeek(getDaysOfTheWeek(data));
            break;
         }

         case MONTHLY:
         {
            xmlException.setPeriod(NumberHelper.getBigInteger(data.getFrequency()));
            if (data.getRelative())
            {
               xmlException.setType(BigInteger.valueOf(5));
               xmlException.setMonthItem(BigInteger.valueOf(data.getDayOfWeek().getValue() + 2));
               xmlException.setMonthPosition(BigInteger.valueOf(NumberHelper.getInt(data.getDayNumber()) - 1));
            }
            else
            {
               xmlException.setType(BigInteger.valueOf(4));
               xmlException.setMonthDay(NumberHelper.getBigInteger(data.getDayNumber()));
            }
            break;
         }

         case YEARLY:
         {
            xmlException.setMonth(BigInteger.valueOf(NumberHelper.getInt(data.getMonthNumber()) - 1));
            if (data.getRelative())
            {
               xmlException.setType(BigInteger.valueOf(3));
               xmlException.setMonthItem(BigInteger.valueOf(data.getDayOfWeek().getValue() + 2));
               xmlException.setMonthPosition(BigInteger.valueOf(NumberHelper.getInt(data.getDayNumber()) - 1));
            }
            else
            {
               xmlException.setType(BigInteger.valueOf(2));
               xmlException.setMonthDay(NumberHelper.getBigInteger(data.getDayNumber()));
            }
         }
      }
   }

   /**
    * Converts days of the week into a bit field.
    *
    * @param data recurring data
    * @return bit field
    */
   private BigInteger getDaysOfTheWeek(RecurringData data)
   {
      int value = 0;
      for (Day day : Day.values())
      {
         if (data.getWeeklyDay(day))
         {
            value = value | DAY_MASKS[day.getValue()];
         }
      }
      return BigInteger.valueOf(value);
   }

   /**
    * Write the work weeks associated with this calendar.
    *
    * @param xmlCalendar XML calendar instance
    * @param mpxjCalendar MPXJ calendar instance
    */
   private void writeWorkWeeks(Project.Calendars.Calendar xmlCalendar, ProjectCalendar mpxjCalendar)
   {
      List<ProjectCalendarWeek> weeks = mpxjCalendar.getWorkWeeks();
      if (!weeks.isEmpty())
      {
         WorkWeeks xmlWorkWeeks = m_factory.createProjectCalendarsCalendarWorkWeeks();
         xmlCalendar.setWorkWeeks(xmlWorkWeeks);
         List<WorkWeek> xmlWorkWeekList = xmlWorkWeeks.getWorkWeek();

         for (ProjectCalendarWeek week : weeks)
         {
            WorkWeek xmlWeek = m_factory.createProjectCalendarsCalendarWorkWeeksWorkWeek();
            xmlWorkWeekList.add(xmlWeek);

            xmlWeek.setName(week.getName());
            TimePeriod xmlTimePeriod = m_factory.createProjectCalendarsCalendarWorkWeeksWorkWeekTimePeriod();
            xmlWeek.setTimePeriod(xmlTimePeriod);
            xmlTimePeriod.setFromDate(week.getDateRange().getStart());
            xmlTimePeriod.setToDate(week.getDateRange().getEnd());

            WeekDays xmlWeekDays = m_factory.createProjectCalendarsCalendarWorkWeeksWorkWeekWeekDays();
            xmlWeek.setWeekDays(xmlWeekDays);

            List<Project.Calendars.Calendar.WorkWeeks.WorkWeek.WeekDays.WeekDay> dayList = xmlWeekDays.getWeekDay();

            for (int loop = 1; loop < 8; loop++)
            {
               DayType workingFlag = week.getWorkingDay(Day.getInstance(loop));

               if (workingFlag != DayType.DEFAULT)
               {
                  Project.Calendars.Calendar.WorkWeeks.WorkWeek.WeekDays.WeekDay day = m_factory.createProjectCalendarsCalendarWorkWeeksWorkWeekWeekDaysWeekDay();
                  dayList.add(day);
                  day.setDayType(BigInteger.valueOf(loop));
                  day.setDayWorking(Boolean.valueOf(workingFlag == DayType.WORKING));

                  if (workingFlag == DayType.WORKING)
                  {
                     Project.Calendars.Calendar.WorkWeeks.WorkWeek.WeekDays.WeekDay.WorkingTimes times = m_factory.createProjectCalendarsCalendarWorkWeeksWorkWeekWeekDaysWeekDayWorkingTimes();
                     day.setWorkingTimes(times);
                     List<Project.Calendars.Calendar.WorkWeeks.WorkWeek.WeekDays.WeekDay.WorkingTimes.WorkingTime> timesList = times.getWorkingTime();

                     ProjectCalendarHours bch = week.getCalendarHours(Day.getInstance(loop));
                     if (bch != null)
                     {
                        for (DateRange range : bch)
                        {
                           if (range != null)
                           {
                              Project.Calendars.Calendar.WorkWeeks.WorkWeek.WeekDays.WeekDay.WorkingTimes.WorkingTime time = m_factory.createProjectCalendarsCalendarWorkWeeksWorkWeekWeekDaysWeekDayWorkingTimesWorkingTime();
                              timesList.add(time);

                              time.setFromTime(range.getStart());
                              time.setToTime(range.getEnd());
                           }
                        }
                     }
                  }
               }
            }
         }
      }
   }

   /**
    * This method writes resource data to an MSPDI file.
    *
    * @param project Root node of the MSPDI file
    */
   private void writeResources(Project project)
   {
      Project.Resources resources = m_factory.createProjectResources();
      project.setResources(resources);
      List<Project.Resources.Resource> list = resources.getResource();

      for (Resource resource : m_projectFile.getResources())
      {
         list.add(writeResource(resource));
      }
   }

   /**
    * This method writes data for a single resource to an MSPDI file.
    *
    * @param mpx Resource data
    * @return New MSPDI resource instance
    */
   private Project.Resources.Resource writeResource(Resource mpx)
   {
      Project.Resources.Resource xml = m_factory.createProjectResourcesResource();
      ProjectCalendar cal = mpx.getResourceCalendar();
      if (cal != null)
      {
         xml.setCalendarUID(NumberHelper.getBigInteger(cal.getUniqueID()));
      }

      xml.setAccrueAt(mpx.getAccrueAt());
      xml.setActiveDirectoryGUID(mpx.getActiveDirectoryGUID());
      xml.setActualCost(DatatypeConverter.printCurrency(mpx.getActualCost()));
      xml.setActualOvertimeCost(DatatypeConverter.printCurrency(mpx.getActualOvertimeCost()));
      xml.setActualOvertimeWork(DatatypeConverter.printDuration(this, mpx.getActualOvertimeWork()));
      xml.setActualOvertimeWorkProtected(DatatypeConverter.printDuration(this, mpx.getActualOvertimeWorkProtected()));
      xml.setActualWork(DatatypeConverter.printDuration(this, mpx.getActualWork()));
      xml.setActualWorkProtected(DatatypeConverter.printDuration(this, mpx.getActualWorkProtected()));
      xml.setACWP(DatatypeConverter.printCurrency(mpx.getACWP()));
      xml.setAvailableFrom(mpx.getAvailableFrom());
      xml.setAvailableTo(mpx.getAvailableTo());
      xml.setBCWS(DatatypeConverter.printCurrency(mpx.getBCWS()));
      xml.setBCWP(DatatypeConverter.printCurrency(mpx.getBCWP()));
      xml.setBookingType(mpx.getBookingType());
      xml.setIsBudget(Boolean.valueOf(mpx.getBudget()));
      xml.setCanLevel(Boolean.valueOf(mpx.getCanLevel()));
      xml.setCode(mpx.getCode());
      xml.setCost(DatatypeConverter.printCurrency(mpx.getCost()));
      xml.setCostPerUse(DatatypeConverter.printCurrency(mpx.getCostPerUse()));
      xml.setCostVariance(DatatypeConverter.printCurrency(mpx.getCostVariance()));
      xml.setCreationDate(mpx.getCreationDate());
      xml.setCV(DatatypeConverter.printCurrency(mpx.getCV()));
      xml.setEmailAddress(mpx.getEmailAddress());
      xml.setFinish(mpx.getFinish());
      xml.setGroup(mpx.getGroup());
      xml.setGUID(mpx.getGUID());
      xml.setHyperlink(mpx.getHyperlink());
      xml.setHyperlinkAddress(mpx.getHyperlinkAddress());
      xml.setHyperlinkSubAddress(mpx.getHyperlinkSubAddress());
      xml.setID(NumberHelper.getBigInteger(mpx.getID()));
      xml.setInitials(mpx.getInitials());
      xml.setIsEnterprise(Boolean.valueOf(mpx.getEnterprise()));
      xml.setIsGeneric(Boolean.valueOf(mpx.getGeneric()));
      xml.setIsInactive(Boolean.valueOf(!mpx.getActive()));
      xml.setIsNull(Boolean.valueOf(mpx.getNull()));
      xml.setMaterialLabel(mpx.getMaterialLabel());
      xml.setMaxUnits(DatatypeConverter.printUnits(mpx.getMaxUnits()));
      xml.setName(mpx.getName());

      if (!mpx.getNotes().isEmpty())
      {
         xml.setNotes(mpx.getNotes());
      }

      xml.setNTAccount(mpx.getNtAccount());
      xml.setOverAllocated(Boolean.valueOf(mpx.getOverAllocated()));
      xml.setOvertimeCost(DatatypeConverter.printCurrency(mpx.getOvertimeCost()));
      xml.setOvertimeRate(DatatypeConverter.printRate(mpx.getOvertimeRate()));
      xml.setOvertimeRateFormat(DatatypeConverter.printTimeUnit(mpx.getOvertimeRateUnits()));
      xml.setOvertimeWork(DatatypeConverter.printDuration(this, mpx.getOvertimeWork()));
      xml.setPeakUnits(DatatypeConverter.printUnits(mpx.getPeakUnits()));
      xml.setPercentWorkComplete(NumberHelper.getBigInteger(mpx.getPercentWorkComplete()));
      xml.setPhonetics(mpx.getPhonetics());
      xml.setRegularWork(DatatypeConverter.printDuration(this, mpx.getRegularWork()));
      xml.setRemainingCost(DatatypeConverter.printCurrency(mpx.getRemainingCost()));
      xml.setRemainingOvertimeCost(DatatypeConverter.printCurrency(mpx.getRemainingOvertimeCost()));
      xml.setRemainingOvertimeWork(DatatypeConverter.printDuration(this, mpx.getRemainingOvertimeWork()));
      xml.setRemainingWork(DatatypeConverter.printDuration(this, mpx.getRemainingWork()));
      xml.setStandardRate(DatatypeConverter.printRate(mpx.getStandardRate()));
      xml.setStandardRateFormat(DatatypeConverter.printTimeUnit(mpx.getStandardRateUnits()));
      xml.setStart(mpx.getStart());
      xml.setSV(DatatypeConverter.printCurrency(mpx.getSV()));
      xml.setUID(mpx.getUniqueID());
      xml.setWork(DatatypeConverter.printDuration(this, mpx.getWork()));
      xml.setWorkGroup(mpx.getWorkGroup());
      xml.setWorkVariance(DatatypeConverter.printDurationInDecimalThousandthsOfMinutes(mpx.getWorkVariance()));

      if (mpx.getType() == ResourceType.COST)
      {
         xml.setType(ResourceType.MATERIAL);
         xml.setIsCostResource(Boolean.TRUE);
      }
      else
      {
         xml.setType(mpx.getType());
      }

      writeResourceExtendedAttributes(xml, mpx);

      writeResourceBaselines(xml, mpx);

      writeCostRateTables(xml, mpx);

      writeAvailability(xml, mpx);

      return (xml);
   }

   /**
    * Writes resource baseline data.
    *
    * @param xmlResource MSPDI resource
    * @param mpxjResource MPXJ resource
    */
   private void writeResourceBaselines(Project.Resources.Resource xmlResource, Resource mpxjResource)
   {
      Project.Resources.Resource.Baseline baseline = m_factory.createProjectResourcesResourceBaseline();
      boolean populated = false;

      Number cost = mpxjResource.getBaselineCost();
      if (cost != null && cost.intValue() != 0)
      {
         populated = true;
         baseline.setCost(DatatypeConverter.printCurrency(cost));
      }

      Duration work = mpxjResource.getBaselineWork();
      if (work != null && work.getDuration() != 0)
      {
         populated = true;
         baseline.setWork(DatatypeConverter.printDuration(this, work));
      }

      if (populated)
      {
         xmlResource.getBaseline().add(baseline);
         baseline.setNumber(BigInteger.ZERO);
      }

      for (int loop = 1; loop <= 10; loop++)
      {
         baseline = m_factory.createProjectResourcesResourceBaseline();
         populated = false;

         cost = mpxjResource.getBaselineCost(loop);
         if (cost != null && cost.intValue() != 0)
         {
            populated = true;
            baseline.setCost(DatatypeConverter.printCurrency(cost));
         }

         work = mpxjResource.getBaselineWork(loop);
         if (work != null && work.getDuration() != 0)
         {
            populated = true;
            baseline.setWork(DatatypeConverter.printDuration(this, work));
         }

         if (populated)
         {
            xmlResource.getBaseline().add(baseline);
            baseline.setNumber(BigInteger.valueOf(loop));
         }
      }
   }

   /**
    * This method writes extended attribute data for a resource.
    *
    * @param xml MSPDI resource
    * @param mpx MPXJ resource
    */
   private void writeResourceExtendedAttributes(Project.Resources.Resource xml, Resource mpx)
   {
      Project.Resources.Resource.ExtendedAttribute attrib;
      List<Project.Resources.Resource.ExtendedAttribute> extendedAttributes = xml.getExtendedAttribute();

      for (ResourceField mpxFieldID : getAllResourceExtendedAttributes())
      {
         Object value = mpx.getCachedValue(mpxFieldID);

         if (FieldTypeHelper.valueIsNotDefault(mpxFieldID, value))
         {
            m_extendedAttributesInUse.add(mpxFieldID);

            Integer xmlFieldID = Integer.valueOf(MPPResourceField.getID(mpxFieldID) | MPPResourceField.RESOURCE_FIELD_BASE);

            attrib = m_factory.createProjectResourcesResourceExtendedAttribute();
            extendedAttributes.add(attrib);
            attrib.setFieldID(xmlFieldID.toString());
            attrib.setValue(DatatypeConverter.printExtendedAttribute(this, value, mpxFieldID.getDataType()));
            attrib.setDurationFormat(printExtendedAttributeDurationFormat(value));
         }
      }
   }

   /**
    * Writes a resource's cost rate tables.
    *
    * @param xml MSPDI resource
    * @param mpx MPXJ resource
    */
   private void writeCostRateTables(Project.Resources.Resource xml, Resource mpx)
   {
      //Rates rates = m_factory.createProjectResourcesResourceRates();
      //xml.setRates(rates);
      //List<Project.Resources.Resource.Rates.Rate> ratesList = rates.getRate();

      List<Project.Resources.Resource.Rates.Rate> ratesList = null;

      for (int tableIndex = 0; tableIndex < 5; tableIndex++)
      {
         CostRateTable table = mpx.getCostRateTable(tableIndex);
         if (table != null)
         {
            Date from = DateHelper.FIRST_DATE;
            for (CostRateTableEntry entry : table)
            {
               if (costRateTableWriteRequired(entry, from))
               {
                  if (ratesList == null)
                  {
                     Rates rates = m_factory.createProjectResourcesResourceRates();
                     xml.setRates(rates);
                     ratesList = rates.getRate();
                  }

                  Project.Resources.Resource.Rates.Rate rate = m_factory.createProjectResourcesResourceRatesRate();
                  ratesList.add(rate);

                  rate.setCostPerUse(DatatypeConverter.printCurrency(entry.getCostPerUse()));
                  rate.setOvertimeRate(DatatypeConverter.printRate(entry.getOvertimeRate()));
                  rate.setOvertimeRateFormat(DatatypeConverter.printTimeUnit(entry.getOvertimeRateFormat()));
                  rate.setRatesFrom(from);
                  from = entry.getEndDate();
                  rate.setRatesTo(from);
                  rate.setRateTable(BigInteger.valueOf(tableIndex));
                  rate.setStandardRate(DatatypeConverter.printRate(entry.getStandardRate()));
                  rate.setStandardRateFormat(DatatypeConverter.printTimeUnit(entry.getStandardRateFormat()));
               }
            }
         }
      }
   }

   /**
    * This method determines whether the cost rate table should be written.
    * A default cost rate table should not be written to the file.
    *
    * @param entry cost rate table entry
    * @param from from date
    * @return boolean flag
    */
   private boolean costRateTableWriteRequired(CostRateTableEntry entry, Date from)
   {
      boolean fromDate = (DateHelper.compare(from, DateHelper.FIRST_DATE) > 0);
      boolean toDate = (DateHelper.compare(entry.getEndDate(), DateHelper.LAST_DATE) > 0);
      boolean costPerUse = (NumberHelper.getDouble(entry.getCostPerUse()) != 0);
      boolean overtimeRate = (entry.getOvertimeRate() != null && entry.getOvertimeRate().getAmount() != 0);
      boolean standardRate = (entry.getStandardRate() != null && entry.getStandardRate().getAmount() != 0);
      return (fromDate || toDate || costPerUse || overtimeRate || standardRate);
   }

   /**
    * This method writes a resource's availability table.
    *
    * @param xml MSPDI resource
    * @param mpx MPXJ resource
    */
   private void writeAvailability(Project.Resources.Resource xml, Resource mpx)
   {
      AvailabilityPeriods periods = m_factory.createProjectResourcesResourceAvailabilityPeriods();
      xml.setAvailabilityPeriods(periods);
      List<AvailabilityPeriod> list = periods.getAvailabilityPeriod();
      for (Availability availability : mpx.getAvailability())
      {
         AvailabilityPeriod period = m_factory.createProjectResourcesResourceAvailabilityPeriodsAvailabilityPeriod();
         list.add(period);
         DateRange range = availability.getRange();

         period.setAvailableFrom(range.getStart());
         period.setAvailableTo(range.getEnd());
         period.setAvailableUnits(DatatypeConverter.printUnits(availability.getUnits()));
      }
   }

   /**
    * This method writes task data to an MSPDI file.
    *
    * @param project Root node of the MSPDI file
    */
   private void writeTasks(Project project)
   {
      Project.Tasks tasks = m_factory.createProjectTasks();
      project.setTasks(tasks);
      List<Project.Tasks.Task> list = tasks.getTask();

      for (Task task : m_projectFile.getTasks())
      {
         list.add(writeTask(task));
      }
   }

   /**
    * This method writes data for a single task to an MSPDI file.
    *
    * @param mpx Task data
    * @return new task instance
    */
   private Project.Tasks.Task writeTask(Task mpx)
   {
      Project.Tasks.Task xml = m_factory.createProjectTasksTask();

      xml.setActive(Boolean.valueOf(mpx.getActive()));
      xml.setActualCost(DatatypeConverter.printCurrency(mpx.getActualCost()));
      xml.setActualDuration(DatatypeConverter.printDuration(this, mpx.getActualDuration()));
      xml.setActualFinish(mpx.getActualFinish());
      xml.setActualOvertimeCost(DatatypeConverter.printCurrency(mpx.getActualOvertimeCost()));
      xml.setActualOvertimeWork(DatatypeConverter.printDuration(this, mpx.getActualOvertimeWork()));
      xml.setActualOvertimeWorkProtected(DatatypeConverter.printDuration(this, mpx.getActualOvertimeWorkProtected()));
      xml.setActualStart(mpx.getActualStart());
      xml.setActualWork(DatatypeConverter.printDuration(this, mpx.getActualWork()));
      xml.setActualWorkProtected(DatatypeConverter.printDuration(this, mpx.getActualWorkProtected()));
      xml.setACWP(DatatypeConverter.printCurrency(mpx.getACWP()));
      xml.setBCWP(DatatypeConverter.printCurrency(mpx.getBCWP()));
      xml.setBCWS(DatatypeConverter.printCurrency(mpx.getBCWS()));
      xml.setCalendarUID(getTaskCalendarID(mpx));
      xml.setConstraintDate(mpx.getConstraintDate());
      xml.setConstraintType(DatatypeConverter.printConstraintType(mpx.getConstraintType()));
      xml.setContact(mpx.getContact());
      xml.setCost(DatatypeConverter.printCurrency(mpx.getCost()));
      xml.setCreateDate(mpx.getCreateDate());
      xml.setCritical(Boolean.valueOf(mpx.getCritical()));
      xml.setCV(DatatypeConverter.printCurrency(mpx.getCV()));
      xml.setDeadline(mpx.getDeadline());
      xml.setDuration(DatatypeConverter.printDurationMandatory(this, mpx.getDuration()));
      xml.setDurationText(mpx.getDurationText());
      xml.setDurationFormat(DatatypeConverter.printDurationTimeUnits(mpx.getDuration(), mpx.getEstimated()));
      xml.setEarlyFinish(mpx.getEarlyFinish());
      xml.setEarlyStart(mpx.getEarlyStart());
      xml.setEarnedValueMethod(DatatypeConverter.printEarnedValueMethod(mpx.getEarnedValueMethod()));
      xml.setEffortDriven(Boolean.valueOf(mpx.getEffortDriven()));
      xml.setEstimated(Boolean.valueOf(mpx.getEstimated()));
      xml.setExternalTask(Boolean.valueOf(mpx.getExternalTask()));
      xml.setExternalTaskProject(mpx.getProject());
      xml.setFinish(mpx.getFinish());
      xml.setFinishSlack(DatatypeConverter.printDurationInIntegerTenthsOfMinutes(mpx.getFinishSlack()));
      xml.setFinishText(mpx.getFinishText());
      xml.setFinishVariance(DatatypeConverter.printDurationInIntegerThousandthsOfMinutes(mpx.getFinishVariance()));
      xml.setFixedCost(DatatypeConverter.printCurrency(mpx.getFixedCost()));

      AccrueType fixedCostAccrual = mpx.getFixedCostAccrual();
      if (fixedCostAccrual == null)
      {
         fixedCostAccrual = AccrueType.PRORATED;
      }
      xml.setFixedCostAccrual(fixedCostAccrual);
      xml.setFreeSlack(DatatypeConverter.printDurationInIntegerTenthsOfMinutes(mpx.getFreeSlack()));
      xml.setGUID(mpx.getGUID());
      xml.setHideBar(Boolean.valueOf(mpx.getHideBar()));
      xml.setIsNull(Boolean.valueOf(mpx.getNull()));
      xml.setIsSubproject(Boolean.valueOf(mpx.getSubProject() != null));
      xml.setIsSubprojectReadOnly(Boolean.valueOf(mpx.getSubprojectReadOnly()));
      xml.setHyperlink(mpx.getHyperlink());
      xml.setHyperlinkAddress(mpx.getHyperlinkAddress());
      xml.setHyperlinkSubAddress(mpx.getHyperlinkSubAddress());
      xml.setID(NumberHelper.getBigInteger(mpx.getID()));
      xml.setIgnoreResourceCalendar(Boolean.valueOf(mpx.getIgnoreResourceCalendar()));
      xml.setLateFinish(mpx.getLateFinish());
      xml.setLateStart(mpx.getLateStart());
      xml.setLevelAssignments(Boolean.valueOf(mpx.getLevelAssignments()));
      xml.setLevelingCanSplit(Boolean.valueOf(mpx.getLevelingCanSplit()));

      if (mpx.getLevelingDelay() != null)
      {
         Duration levelingDelay = mpx.getLevelingDelay();
         double tenthMinutes = 10.0 * Duration.convertUnits(levelingDelay.getDuration(), levelingDelay.getUnits(), TimeUnit.MINUTES, m_projectFile.getProjectProperties()).getDuration();
         xml.setLevelingDelay(BigInteger.valueOf((long) tenthMinutes));
         xml.setLevelingDelayFormat(DatatypeConverter.printDurationTimeUnits(levelingDelay, false));
      }

      xml.setManual(Boolean.valueOf(mpx.getTaskMode() == TaskMode.MANUALLY_SCHEDULED));

      if (mpx.getTaskMode() == TaskMode.MANUALLY_SCHEDULED)
      {
         xml.setManualDuration(DatatypeConverter.printDuration(this, mpx.getDuration()));
         xml.setManualFinish(mpx.getFinish());
         xml.setManualStart(mpx.getStart());
      }

      xml.setMilestone(Boolean.valueOf(mpx.getMilestone()));
      xml.setName(mpx.getName());

      if (!mpx.getNotes().isEmpty())
      {
         xml.setNotes(mpx.getNotes());
      }

      xml.setOutlineLevel(NumberHelper.getBigInteger(mpx.getOutlineLevel()));
      xml.setOutlineNumber(mpx.getOutlineNumber());
      xml.setOverAllocated(Boolean.valueOf(mpx.getOverAllocated()));
      xml.setOvertimeCost(DatatypeConverter.printCurrency(mpx.getOvertimeCost()));
      xml.setOvertimeWork(DatatypeConverter.printDuration(this, mpx.getOvertimeWork()));
      xml.setPercentComplete(NumberHelper.getBigInteger(mpx.getPercentageComplete()));
      xml.setPercentWorkComplete(NumberHelper.getBigInteger(mpx.getPercentageWorkComplete()));
      xml.setPhysicalPercentComplete(NumberHelper.getBigInteger(mpx.getPhysicalPercentComplete()));
      xml.setPriority(DatatypeConverter.printPriority(mpx.getPriority()));
      xml.setRecurring(Boolean.valueOf(mpx.getRecurring()));
      xml.setRegularWork(DatatypeConverter.printDuration(this, mpx.getRegularWork()));
      xml.setRemainingCost(DatatypeConverter.printCurrency(mpx.getRemainingCost()));

      if (mpx.getRemainingDuration() == null)
      {
         Duration duration = mpx.getDuration();

         if (duration != null)
         {
            double amount = duration.getDuration();
            amount -= ((amount * NumberHelper.getDouble(mpx.getPercentageComplete())) / 100);
            xml.setRemainingDuration(DatatypeConverter.printDuration(this, Duration.getInstance(amount, duration.getUnits())));
         }
      }
      else
      {
         xml.setRemainingDuration(DatatypeConverter.printDuration(this, mpx.getRemainingDuration()));
      }

      xml.setRemainingOvertimeCost(DatatypeConverter.printCurrency(mpx.getRemainingOvertimeCost()));
      xml.setRemainingOvertimeWork(DatatypeConverter.printDuration(this, mpx.getRemainingOvertimeWork()));
      xml.setRemainingWork(DatatypeConverter.printDuration(this, mpx.getRemainingWork()));
      xml.setResume(mpx.getResume());
      xml.setResumeValid(Boolean.valueOf(mpx.getResumeValid()));
      xml.setRollup(Boolean.valueOf(mpx.getRollup()));
      xml.setStart(mpx.getStart());
      xml.setStartSlack(DatatypeConverter.printDurationInIntegerTenthsOfMinutes(mpx.getStartSlack()));
      xml.setStartText(mpx.getStartText());
      xml.setStartVariance(DatatypeConverter.printDurationInIntegerThousandthsOfMinutes(mpx.getStartVariance()));
      xml.setStop(mpx.getStop());
      xml.setSubprojectName(mpx.getSubprojectName());
      xml.setSummary(Boolean.valueOf(mpx.getSummary()));
      xml.setTotalSlack(DatatypeConverter.printDurationInIntegerTenthsOfMinutes(mpx.getTotalSlack()));
      xml.setType(mpx.getType());
      xml.setUID(mpx.getUniqueID());
      xml.setWBS(mpx.getWBS());
      xml.setWBSLevel(mpx.getWBSLevel());
      xml.setWork(DatatypeConverter.printDuration(this, mpx.getWork()));
      xml.setWorkVariance(DatatypeConverter.printDurationInDecimalThousandthsOfMinutes(mpx.getWorkVariance()));

      if (mpx.getTaskMode() == TaskMode.MANUALLY_SCHEDULED)
      {
         xml.setManualDuration(DatatypeConverter.printDuration(this, mpx.getManualDuration()));
      }

      writePredecessors(xml, mpx);

      writeTaskExtendedAttributes(xml, mpx);

      writeTaskBaselines(xml, mpx);

      return (xml);
   }

   /**
    * Writes task baseline data.
    *
    * @param xmlTask MSPDI task
    * @param mpxjTask MPXJ task
    */
   private void writeTaskBaselines(Project.Tasks.Task xmlTask, Task mpxjTask)
   {
      Project.Tasks.Task.Baseline baseline = m_factory.createProjectTasksTaskBaseline();
      boolean populated = false;

      Number cost = mpxjTask.getBaselineCost();
      if (cost != null && cost.intValue() != 0)
      {
         populated = true;
         baseline.setCost(DatatypeConverter.printCurrency(cost));
      }

      Duration duration = mpxjTask.getBaselineDuration();
      if (duration != null && duration.getDuration() != 0)
      {
         populated = true;
         baseline.setDuration(DatatypeConverter.printDuration(this, duration));
         baseline.setDurationFormat(DatatypeConverter.printDurationTimeUnits(duration, false));
      }

      Date date = mpxjTask.getBaselineFinish();
      if (date != null)
      {
         populated = true;
         baseline.setFinish(date);
      }

      date = mpxjTask.getBaselineStart();
      if (date != null)
      {
         populated = true;
         baseline.setStart(date);
      }

      duration = mpxjTask.getBaselineWork();
      if (duration != null && duration.getDuration() != 0)
      {
         populated = true;
         baseline.setWork(DatatypeConverter.printDuration(this, duration));
      }

      if (populated)
      {
         baseline.setNumber(BigInteger.ZERO);
         xmlTask.getBaseline().add(baseline);
      }

      for (int loop = 1; loop <= 10; loop++)
      {
         baseline = m_factory.createProjectTasksTaskBaseline();
         populated = false;

         cost = mpxjTask.getBaselineCost(loop);
         if (cost != null && cost.intValue() != 0)
         {
            populated = true;
            baseline.setCost(DatatypeConverter.printCurrency(cost));
         }

         duration = mpxjTask.getBaselineDuration(loop);
         if (duration != null && duration.getDuration() != 0)
         {
            populated = true;
            baseline.setDuration(DatatypeConverter.printDuration(this, duration));
            baseline.setDurationFormat(DatatypeConverter.printDurationTimeUnits(duration, false));
         }

         date = mpxjTask.getBaselineFinish(loop);
         if (date != null)
         {
            populated = true;
            baseline.setFinish(date);
         }

         date = mpxjTask.getBaselineStart(loop);
         if (date != null)
         {
            populated = true;
            baseline.setStart(date);
         }

         duration = mpxjTask.getBaselineWork(loop);
         if (duration != null && duration.getDuration() != 0)
         {
            populated = true;
            baseline.setWork(DatatypeConverter.printDuration(this, duration));
         }

         if (populated)
         {
            baseline.setNumber(BigInteger.valueOf(loop));
            xmlTask.getBaseline().add(baseline);
         }
      }
   }

   /**
    * This method writes extended attribute data for a task.
    *
    * @param xml MSPDI task
    * @param mpx MPXJ task
    */
   private void writeTaskExtendedAttributes(Project.Tasks.Task xml, Task mpx)
   {
      Project.Tasks.Task.ExtendedAttribute attrib;
      List<Project.Tasks.Task.ExtendedAttribute> extendedAttributes = xml.getExtendedAttribute();

      for (TaskField mpxFieldID : getAllTaskExtendedAttributes())
      {
         Object value = mpx.getCachedValue(mpxFieldID);

         if (FieldTypeHelper.valueIsNotDefault(mpxFieldID, value))
         {
            m_extendedAttributesInUse.add(mpxFieldID);

            Integer xmlFieldID = Integer.valueOf(MPPTaskField.getID(mpxFieldID) | MPPTaskField.TASK_FIELD_BASE);

            attrib = m_factory.createProjectTasksTaskExtendedAttribute();
            extendedAttributes.add(attrib);
            attrib.setFieldID(xmlFieldID.toString());
            attrib.setValue(DatatypeConverter.printExtendedAttribute(this, value, mpxFieldID.getDataType()));
            attrib.setDurationFormat(printExtendedAttributeDurationFormat(value));
         }
      }
   }

   /**
    * Converts a duration to duration time units.
    *
    * @param value duration value
    * @return duration time units
    */
   private BigInteger printExtendedAttributeDurationFormat(Object value)
   {
      BigInteger result = null;
      if (value instanceof Duration)
      {
         result = DatatypeConverter.printDurationTimeUnits(((Duration) value).getUnits(), false);
      }
      return (result);
   }

   /**
    * This method retrieves the UID for a calendar associated with a task.
    *
    * @param mpx MPX Task instance
    * @return calendar UID
    */
   private BigInteger getTaskCalendarID(Task mpx)
   {
      BigInteger result = null;
      ProjectCalendar cal = mpx.getCalendar();
      if (cal != null)
      {
         result = NumberHelper.getBigInteger(cal.getUniqueID());
      }
      else
      {
         result = NULL_CALENDAR_ID;
      }
      return (result);
   }

   /**
    * This method writes predecessor data to an MSPDI file.
    * We have to deal with a slight anomaly in this method that is introduced
    * by the MPX file format. It would be possible for someone to create an
    * MPX file with both the predecessor list and the unique ID predecessor
    * list populated... which means that we must process both and avoid adding
    * duplicate predecessors. Also interesting to note is that MSP98 populates
    * the predecessor list, not the unique ID predecessor list, as you might
    * expect.
    *
    * @param xml MSPDI task data
    * @param mpx MPX task data
    */
   private void writePredecessors(Project.Tasks.Task xml, Task mpx)
   {
      List<Project.Tasks.Task.PredecessorLink> list = xml.getPredecessorLink();

      List<Relation> predecessors = mpx.getPredecessors();
      for (Relation rel : predecessors)
      {
         Integer taskUniqueID = rel.getTargetTask().getUniqueID();
         list.add(writePredecessor(taskUniqueID, rel.getType(), rel.getLag()));
         m_eventManager.fireRelationWrittenEvent(rel);
      }
   }

   /**
    * This method writes a single predecessor link to the MSPDI file.
    *
    * @param taskID The task UID
    * @param type The predecessor type
    * @param lag The lag duration
    * @return A new link to be added to the MSPDI file
    */
   private Project.Tasks.Task.PredecessorLink writePredecessor(Integer taskID, RelationType type, Duration lag)
   {
      Project.Tasks.Task.PredecessorLink link = m_factory.createProjectTasksTaskPredecessorLink();

      link.setPredecessorUID(NumberHelper.getBigInteger(taskID));
      link.setType(BigInteger.valueOf(type.getValue()));
      link.setCrossProject(Boolean.FALSE); // SF-300: required to keep P6 happy when importing MSPDI files

      if (lag != null && lag.getDuration() != 0)
      {
         double linkLag = lag.getDuration();
         if (lag.getUnits() != TimeUnit.PERCENT && lag.getUnits() != TimeUnit.ELAPSED_PERCENT)
         {
            linkLag = 10.0 * Duration.convertUnits(linkLag, lag.getUnits(), TimeUnit.MINUTES, m_projectFile.getProjectProperties()).getDuration();
         }
         link.setLinkLag(BigInteger.valueOf((long) linkLag));
         link.setLagFormat(DatatypeConverter.printDurationTimeUnits(lag.getUnits(), false));
      }
      else
      {
         // SF-329: default required to keep Powerproject happy when importing MSPDI files
         link.setLinkLag(BIGINTEGER_ZERO);
         link.setLagFormat(DatatypeConverter.printDurationTimeUnits(m_projectFile.getProjectProperties().getDefaultDurationUnits(), false));
      }

      return (link);
   }

   /**
    * This method writes assignment data to an MSPDI file.
    *
    * @param project Root node of the MSPDI file
    */
   private void writeAssignments(Project project)
   {
      Project.Assignments assignments = m_factory.createProjectAssignments();
      project.setAssignments(assignments);
      List<Project.Assignments.Assignment> list = assignments.getAssignment();

      for (ResourceAssignment assignment : m_projectFile.getResourceAssignments())
      {
         list.add(writeAssignment(assignment));
      }

      //
      // Check to see if we have any tasks that have a percent complete value
      // but do not have resource assignments. If any exist, then we must
      // write a dummy resource assignment record to ensure that the MSPDI
      // file shows the correct percent complete amount for the task.
      //
      ProjectConfig config = m_projectFile.getProjectConfig();
      boolean autoUniqueID = config.getAutoAssignmentUniqueID();
      if (!autoUniqueID)
      {
         config.setAutoAssignmentUniqueID(true);
      }

      for (Task task : m_projectFile.getTasks())
      {
         double percentComplete = NumberHelper.getDouble(task.getPercentageComplete());
         if (percentComplete != 0 && task.getResourceAssignments().isEmpty() == true)
         {
            ResourceAssignment dummy = new ResourceAssignment(m_projectFile, task);
            Duration duration = task.getDuration();
            if (duration == null)
            {
               duration = Duration.getInstance(0, TimeUnit.HOURS);
            }
            double durationValue = duration.getDuration();
            TimeUnit durationUnits = duration.getUnits();
            double actualWork = (durationValue * percentComplete) / 100;
            double remainingWork = durationValue - actualWork;

            dummy.setResourceUniqueID(NULL_RESOURCE_ID);
            dummy.setWork(duration);
            dummy.setActualWork(Duration.getInstance(actualWork, durationUnits));
            dummy.setRemainingWork(Duration.getInstance(remainingWork, durationUnits));

            list.add(writeAssignment(dummy));
         }
      }

      config.setAutoAssignmentUniqueID(autoUniqueID);
   }

   /**
    * This method writes data for a single assignment to an MSPDI file.
    *
    * @param mpx Resource assignment data
    * @return New MSPDI assignment instance
    */
   private Project.Assignments.Assignment writeAssignment(ResourceAssignment mpx)
   {
      Project.Assignments.Assignment xml = m_factory.createProjectAssignmentsAssignment();

      xml.setActualCost(DatatypeConverter.printCurrency(mpx.getActualCost()));
      xml.setActualFinish(mpx.getActualFinish());
      xml.setActualOvertimeCost(DatatypeConverter.printCurrency(mpx.getActualOvertimeCost()));
      xml.setActualOvertimeWork(DatatypeConverter.printDuration(this, mpx.getActualOvertimeWork()));
      xml.setActualStart(mpx.getActualStart());
      xml.setActualWork(DatatypeConverter.printDuration(this, mpx.getActualWork()));
      xml.setACWP(DatatypeConverter.printCurrency(mpx.getACWP()));
      xml.setBCWP(DatatypeConverter.printCurrency(mpx.getBCWP()));
      xml.setBCWS(DatatypeConverter.printCurrency(mpx.getBCWS()));
      xml.setBudgetCost(DatatypeConverter.printCurrency(mpx.getBudgetCost()));
      xml.setBudgetWork(DatatypeConverter.printDuration(this, mpx.getBudgetWork()));
      xml.setCost(DatatypeConverter.printCurrency(mpx.getCost()));

      if (mpx.getCostRateTableIndex() != 0)
      {
         xml.setCostRateTable(BigInteger.valueOf(mpx.getCostRateTableIndex()));
      }

      xml.setCreationDate(mpx.getCreateDate());
      xml.setCV(DatatypeConverter.printCurrency(mpx.getCV()));
      xml.setDelay(DatatypeConverter.printDurationInIntegerTenthsOfMinutes(mpx.getDelay()));
      xml.setFinish(mpx.getFinish());
      xml.setGUID(mpx.getGUID());
      xml.setHasFixedRateUnits(Boolean.valueOf(mpx.getVariableRateUnits() == null));
      xml.setFixedMaterial(Boolean.valueOf(mpx.getResource() != null && mpx.getResource().getType() == ResourceType.MATERIAL));
      xml.setHyperlink(mpx.getHyperlink());
      xml.setHyperlinkAddress(mpx.getHyperlinkAddress());
      xml.setHyperlinkSubAddress(mpx.getHyperlinkSubAddress());
      xml.setLevelingDelay(DatatypeConverter.printDurationInIntegerTenthsOfMinutes(mpx.getLevelingDelay()));
      xml.setLevelingDelayFormat(DatatypeConverter.printDurationTimeUnits(mpx.getLevelingDelay(), false));

      if (!mpx.getNotes().isEmpty())
      {
         xml.setNotes(mpx.getNotes());
      }

      xml.setOvertimeCost(DatatypeConverter.printCurrency(mpx.getOvertimeCost()));
      xml.setOvertimeWork(DatatypeConverter.printDuration(this, mpx.getOvertimeWork()));
      xml.setPercentWorkComplete(NumberHelper.getBigInteger(mpx.getPercentageWorkComplete()));
      xml.setRateScale(mpx.getVariableRateUnits() == null ? null : DatatypeConverter.printTimeUnit(mpx.getVariableRateUnits()));
      xml.setRegularWork(DatatypeConverter.printDuration(this, mpx.getRegularWork()));
      xml.setRemainingCost(DatatypeConverter.printCurrency(mpx.getRemainingCost()));
      xml.setRemainingOvertimeCost(DatatypeConverter.printCurrency(mpx.getRemainingOvertimeCost()));
      xml.setRemainingOvertimeWork(DatatypeConverter.printDuration(this, mpx.getRemainingOvertimeWork()));
      xml.setRemainingWork(DatatypeConverter.printDuration(this, mpx.getRemainingWork()));
      xml.setResourceUID(mpx.getResource() == null ? BigInteger.valueOf(NULL_RESOURCE_ID.intValue()) : BigInteger.valueOf(NumberHelper.getInt(mpx.getResourceUniqueID())));
      xml.setResume(mpx.getResume());
      xml.setStart(mpx.getStart());
      xml.setStop(mpx.getStop());
      xml.setSV(DatatypeConverter.printCurrency(mpx.getSV()));
      xml.setTaskUID(NumberHelper.getBigInteger(mpx.getTask().getUniqueID()));
      xml.setUID(NumberHelper.getBigInteger(mpx.getUniqueID()));
      xml.setUnits(DatatypeConverter.printUnits(mpx.getUnits()));
      xml.setVAC(DatatypeConverter.printCurrency(mpx.getVAC()));
      xml.setWork(DatatypeConverter.printDuration(this, mpx.getWork()));
      xml.setWorkContour(mpx.getWorkContour());

      xml.setCostVariance(DatatypeConverter.printCurrency(mpx.getCostVariance()));
      xml.setWorkVariance(DatatypeConverter.printDurationInDecimalThousandthsOfMinutes(mpx.getWorkVariance()));
      xml.setStartVariance(DatatypeConverter.printDurationInIntegerThousandthsOfMinutes(mpx.getStartVariance()));
      xml.setFinishVariance(DatatypeConverter.printDurationInIntegerThousandthsOfMinutes(mpx.getFinishVariance()));

      writeAssignmentBaselines(xml, mpx);

      writeAssignmentExtendedAttributes(xml, mpx);

      writeAssignmentTimephasedData(mpx, xml);

      m_eventManager.fireAssignmentWrittenEvent(mpx);

      return (xml);
   }

   /**
    * Writes assignment baseline data.
    *
    * @param xml MSPDI assignment
    * @param mpxj MPXJ assignment
    */
   private void writeAssignmentBaselines(Project.Assignments.Assignment xml, ResourceAssignment mpxj)
   {
      Project.Assignments.Assignment.Baseline baseline = m_factory.createProjectAssignmentsAssignmentBaseline();
      boolean populated = false;

      Number cost = mpxj.getBaselineCost();
      if (cost != null && cost.intValue() != 0)
      {
         populated = true;
         baseline.setCost(DatatypeConverter.printExtendedAttributeCurrency(cost));
      }

      Date date = mpxj.getBaselineFinish();
      if (date != null)
      {
         populated = true;
         baseline.setFinish(DatatypeConverter.printExtendedAttributeDate(date));
      }

      date = mpxj.getBaselineStart();
      if (date != null)
      {
         populated = true;
         baseline.setStart(DatatypeConverter.printExtendedAttributeDate(date));
      }

      Duration duration = mpxj.getBaselineWork();
      if (duration != null && duration.getDuration() != 0)
      {
         populated = true;
         baseline.setWork(DatatypeConverter.printDuration(this, duration));
      }

      if (populated)
      {
         baseline.setNumber("0");
         xml.getBaseline().add(baseline);
      }

      for (int loop = 1; loop <= 10; loop++)
      {
         baseline = m_factory.createProjectAssignmentsAssignmentBaseline();
         populated = false;

         cost = mpxj.getBaselineCost(loop);
         if (cost != null && cost.intValue() != 0)
         {
            populated = true;
            baseline.setCost(DatatypeConverter.printExtendedAttributeCurrency(cost));
         }

         date = mpxj.getBaselineFinish(loop);
         if (date != null)
         {
            populated = true;
            baseline.setFinish(DatatypeConverter.printExtendedAttributeDate(date));
         }

         date = mpxj.getBaselineStart(loop);
         if (date != null)
         {
            populated = true;
            baseline.setStart(DatatypeConverter.printExtendedAttributeDate(date));
         }

         duration = mpxj.getBaselineWork(loop);
         if (duration != null && duration.getDuration() != 0)
         {
            populated = true;
            baseline.setWork(DatatypeConverter.printDuration(this, duration));
         }

         if (populated)
         {
            baseline.setNumber(Integer.toString(loop));
            xml.getBaseline().add(baseline);
         }
      }
   }

   /**
    * This method writes extended attribute data for an assignment.
    *
    * @param xml MSPDI assignment
    * @param mpx MPXJ assignment
    */
   private void writeAssignmentExtendedAttributes(Project.Assignments.Assignment xml, ResourceAssignment mpx)
   {
      Project.Assignments.Assignment.ExtendedAttribute attrib;
      List<Project.Assignments.Assignment.ExtendedAttribute> extendedAttributes = xml.getExtendedAttribute();

      for (AssignmentField mpxFieldID : getAllAssignmentExtendedAttributes())
      {
         Object value = mpx.getCachedValue(mpxFieldID);

         if (FieldTypeHelper.valueIsNotDefault(mpxFieldID, value))
         {
            m_extendedAttributesInUse.add(mpxFieldID);

            Integer xmlFieldID = Integer.valueOf(MPPAssignmentField.getID(mpxFieldID) | MPPAssignmentField.ASSIGNMENT_FIELD_BASE);

            attrib = m_factory.createProjectAssignmentsAssignmentExtendedAttribute();
            extendedAttributes.add(attrib);
            attrib.setFieldID(xmlFieldID.toString());
            attrib.setValue(DatatypeConverter.printExtendedAttribute(this, value, mpxFieldID.getDataType()));
            attrib.setDurationFormat(printExtendedAttributeDurationFormat(value));
         }
      }
   }

   /**
    * Writes the timephased data for a resource assignment.
    *
    * @param mpx MPXJ assignment
    * @param xml MSDPI assignment
    */
   private void writeAssignmentTimephasedData(ResourceAssignment mpx, Project.Assignments.Assignment xml)
   {
      if (m_writeTimphasedData && mpx.getHasTimephasedData())
      {
         List<TimephasedDataType> list = xml.getTimephasedData();
         ProjectCalendar calendar = mpx.getCalendar();
         BigInteger assignmentID = xml.getUID();

         List<TimephasedWork> complete = mpx.getTimephasedActualWork();
         List<TimephasedWork> planned = mpx.getTimephasedWork();

         if (m_splitTimephasedAsDays)
         {
            TimephasedWork lastComplete = null;
            if (complete != null && !complete.isEmpty())
            {
               lastComplete = complete.get(complete.size() - 1);
            }

            TimephasedWork firstPlanned = null;
            if (planned != null && !planned.isEmpty())
            {
               firstPlanned = planned.get(0);
            }

            if (planned != null)
            {
               planned = splitDays(calendar, mpx.getTimephasedWork(), null, lastComplete);
            }

            if (complete != null)
            {
               complete = splitDays(calendar, complete, firstPlanned, null);
            }
         }

         if (planned != null)
         {
            writeAssignmentTimephasedData(assignmentID, list, planned, 1);
         }

         if (complete != null)
         {
            writeAssignmentTimephasedData(assignmentID, list, complete, 2);
         }
      }
   }

   /**
    * Splits timephased data into individual days.
    *
    * @param calendar current calendar
    * @param list list of timephased assignment data
    * @param first first planned assignment
    * @param last last completed assignment
    * @return list of timephased data ready for output
    */
   private List<TimephasedWork> splitDays(ProjectCalendar calendar, List<TimephasedWork> list, TimephasedWork first, TimephasedWork last)
   {
      List<TimephasedWork> result = new LinkedList<TimephasedWork>();

      for (TimephasedWork assignment : list)
      {
         Date startDate = assignment.getStart();
         Date finishDate = assignment.getFinish();
         Date startDay = DateHelper.getDayStartDate(startDate);
         Date finishDay = DateHelper.getDayStartDate(finishDate);
         if (startDay.getTime() == finishDay.getTime())
         {
            Date startTime = calendar.getStartTime(startDay);
            Date currentStart = DateHelper.setTime(startDay, startTime);
            if (startDate.getTime() > currentStart.getTime())
            {
               boolean paddingRequired = true;

               if (last != null)
               {
                  Date lastFinish = last.getFinish();
                  if (lastFinish.getTime() == startDate.getTime())
                  {
                     paddingRequired = false;
                  }
                  else
                  {
                     Date lastFinishDay = DateHelper.getDayStartDate(lastFinish);
                     if (startDay.getTime() == lastFinishDay.getTime())
                     {
                        currentStart = lastFinish;
                     }
                  }
               }

               if (paddingRequired)
               {
                  Duration zeroHours = Duration.getInstance(0, TimeUnit.HOURS);
                  TimephasedWork padding = new TimephasedWork();
                  padding.setStart(currentStart);
                  padding.setFinish(startDate);
                  padding.setTotalAmount(zeroHours);
                  padding.setAmountPerDay(zeroHours);
                  result.add(padding);
               }
            }

            result.add(assignment);

            Date endTime = calendar.getFinishTime(startDay);
            Date currentFinish = DateHelper.setTime(startDay, endTime);
            if (finishDate.getTime() < currentFinish.getTime())
            {
               boolean paddingRequired = true;

               if (first != null)
               {
                  Date firstStart = first.getStart();
                  if (firstStart.getTime() == finishDate.getTime())
                  {
                     paddingRequired = false;
                  }
                  else
                  {
                     Date firstStartDay = DateHelper.getDayStartDate(firstStart);
                     if (finishDay.getTime() == firstStartDay.getTime())
                     {
                        currentFinish = firstStart;
                     }
                  }
               }

               if (paddingRequired)
               {
                  Duration zeroHours = Duration.getInstance(0, TimeUnit.HOURS);
                  TimephasedWork padding = new TimephasedWork();
                  padding.setStart(finishDate);
                  padding.setFinish(currentFinish);
                  padding.setTotalAmount(zeroHours);
                  padding.setAmountPerDay(zeroHours);
                  result.add(padding);
               }
            }
         }
         else
         {
            Date currentStart = startDate;
            boolean isWorking = calendar.isWorkingDate(currentStart);
            while (currentStart.getTime() < finishDate.getTime())
            {
               if (isWorking)
               {
                  Date endTime = calendar.getFinishTime(currentStart);
                  Date currentFinish = DateHelper.setTime(currentStart, endTime);
                  if (currentFinish.getTime() > finishDate.getTime())
                  {
                     currentFinish = finishDate;
                  }

                  TimephasedWork split = new TimephasedWork();
                  split.setStart(currentStart);
                  split.setFinish(currentFinish);
                  split.setTotalAmount(assignment.getAmountPerDay());
                  split.setAmountPerDay(assignment.getAmountPerDay());
                  result.add(split);
               }

               Calendar cal = DateHelper.popCalendar(currentStart);
               cal.add(Calendar.DAY_OF_YEAR, 1);
               currentStart = cal.getTime();
               isWorking = calendar.isWorkingDate(currentStart);
               if (isWorking)
               {
                  Date startTime = calendar.getStartTime(currentStart);
                  DateHelper.setTime(cal, startTime);
                  currentStart = cal.getTime();
               }
               DateHelper.pushCalendar(cal);
            }
         }
      }

      return result;
   }

   /**
    * Writes a list of timephased data to the MSPDI file.
    *
    * @param assignmentID current assignment ID
    * @param list output list of timephased data items
    * @param data input list of timephased data
    * @param type list type (planned or completed)
    */
   private void writeAssignmentTimephasedData(BigInteger assignmentID, List<TimephasedDataType> list, List<TimephasedWork> data, int type)
   {
      for (TimephasedWork mpx : data)
      {
         TimephasedDataType xml = m_factory.createTimephasedDataType();
         list.add(xml);

         xml.setStart(mpx.getStart());
         xml.setFinish(mpx.getFinish());
         xml.setType(BigInteger.valueOf(type));
         xml.setUID(assignmentID);
         xml.setUnit(DatatypeConverter.printDurationTimeUnits(mpx.getTotalAmount(), false));
         xml.setValue(DatatypeConverter.printDuration(this, mpx.getTotalAmount()));
      }
   }

   /**
    * Retrieve list of assignment extended attributes.
    *
    * @return list of extended attributes
    */
   private List<AssignmentField> getAllAssignmentExtendedAttributes()
   {
      ArrayList<AssignmentField> result = new ArrayList<AssignmentField>();
      result.addAll(Arrays.asList(AssignmentFieldLists.CUSTOM_COST));
      result.addAll(Arrays.asList(AssignmentFieldLists.CUSTOM_DATE));
      result.addAll(Arrays.asList(AssignmentFieldLists.CUSTOM_DURATION));
      result.addAll(Arrays.asList(AssignmentFieldLists.ENTERPRISE_COST));
      result.addAll(Arrays.asList(AssignmentFieldLists.ENTERPRISE_DATE));
      result.addAll(Arrays.asList(AssignmentFieldLists.ENTERPRISE_DURATION));
      result.addAll(Arrays.asList(AssignmentFieldLists.ENTERPRISE_FLAG));
      result.addAll(Arrays.asList(AssignmentFieldLists.ENTERPRISE_NUMBER));
      result.addAll(Arrays.asList(AssignmentFieldLists.ENTERPRISE_RESOURCE_MULTI_VALUE));
      result.addAll(Arrays.asList(AssignmentFieldLists.ENTERPRISE_RESOURCE_OUTLINE_CODE));
      result.addAll(Arrays.asList(AssignmentFieldLists.ENTERPRISE_TEXT));
      result.addAll(Arrays.asList(AssignmentFieldLists.CUSTOM_FINISH));
      result.addAll(Arrays.asList(AssignmentFieldLists.CUSTOM_FLAG));
      result.addAll(Arrays.asList(AssignmentFieldLists.CUSTOM_NUMBER));
      result.addAll(Arrays.asList(AssignmentFieldLists.CUSTOM_START));
      result.addAll(Arrays.asList(AssignmentFieldLists.CUSTOM_TEXT));
      return result;
   }

   /**
    * Retrieve list of task extended attributes.
    *
    * @return list of extended attributes
    */
   private List<TaskField> getAllTaskExtendedAttributes()
   {
      ArrayList<TaskField> result = new ArrayList<TaskField>();
      result.addAll(Arrays.asList(TaskFieldLists.CUSTOM_TEXT));
      result.addAll(Arrays.asList(TaskFieldLists.CUSTOM_START));
      result.addAll(Arrays.asList(TaskFieldLists.CUSTOM_FINISH));
      result.addAll(Arrays.asList(TaskFieldLists.CUSTOM_COST));
      result.addAll(Arrays.asList(TaskFieldLists.CUSTOM_DATE));
      result.addAll(Arrays.asList(TaskFieldLists.CUSTOM_FLAG));
      result.addAll(Arrays.asList(TaskFieldLists.CUSTOM_NUMBER));
      result.addAll(Arrays.asList(TaskFieldLists.CUSTOM_DURATION));
      result.addAll(Arrays.asList(TaskFieldLists.CUSTOM_OUTLINE_CODE));
      result.addAll(Arrays.asList(TaskFieldLists.ENTERPRISE_COST));
      result.addAll(Arrays.asList(TaskFieldLists.ENTERPRISE_DATE));
      result.addAll(Arrays.asList(TaskFieldLists.ENTERPRISE_DURATION));
      result.addAll(Arrays.asList(TaskFieldLists.ENTERPRISE_FLAG));
      result.addAll(Arrays.asList(TaskFieldLists.ENTERPRISE_NUMBER));
      result.addAll(Arrays.asList(TaskFieldLists.ENTERPRISE_TEXT));
      return result;
   }

   /**
    * Retrieve list of resource extended attributes.
    *
    * @return list of extended attributes
    */
   private List<ResourceField> getAllResourceExtendedAttributes()
   {
      ArrayList<ResourceField> result = new ArrayList<ResourceField>();
      result.addAll(Arrays.asList(ResourceFieldLists.CUSTOM_TEXT));
      result.addAll(Arrays.asList(ResourceFieldLists.CUSTOM_START));
      result.addAll(Arrays.asList(ResourceFieldLists.CUSTOM_FINISH));
      result.addAll(Arrays.asList(ResourceFieldLists.CUSTOM_COST));
      result.addAll(Arrays.asList(ResourceFieldLists.CUSTOM_DATE));
      result.addAll(Arrays.asList(ResourceFieldLists.CUSTOM_FLAG));
      result.addAll(Arrays.asList(ResourceFieldLists.CUSTOM_NUMBER));
      result.addAll(Arrays.asList(ResourceFieldLists.CUSTOM_DURATION));
      result.addAll(Arrays.asList(ResourceFieldLists.CUSTOM_OUTLINE_CODE));
      result.addAll(Arrays.asList(ResourceFieldLists.ENTERPRISE_COST));
      result.addAll(Arrays.asList(ResourceFieldLists.ENTERPRISE_DATE));
      result.addAll(Arrays.asList(ResourceFieldLists.ENTERPRISE_DURATION));
      result.addAll(Arrays.asList(ResourceFieldLists.ENTERPRISE_FLAG));
      result.addAll(Arrays.asList(ResourceFieldLists.ENTERPRISE_NUMBER));
      result.addAll(Arrays.asList(ResourceFieldLists.ENTERPRISE_TEXT));
      return result;
   }

   /**
    * Package-private accessor method used to retrieve the project file
    * currently being processed by this writer.
    *
    * @return project file instance
    */
   ProjectFile getProjectFile()
   {
      return (m_projectFile);
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
         CONTEXT = JAXBContext.newInstance("net.sf.mpxj.mspdi.schema", MSPDIWriter.class.getClassLoader());
      }

      catch (JAXBException ex)
      {
         CONTEXT_EXCEPTION = ex;
         CONTEXT = null;
      }
   }

   // TODO share this
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

   private ObjectFactory m_factory;

   private ProjectFile m_projectFile;

   private EventManager m_eventManager;

   private Set<FieldType> m_extendedAttributesInUse;

   private boolean m_splitTimephasedAsDays = true;

   private boolean m_writeTimphasedData;

   private SaveVersion m_saveVersion = SaveVersion.Project2016;

   private static final BigInteger BIGINTEGER_ZERO = BigInteger.valueOf(0);

   private static final Integer NULL_RESOURCE_ID = Integer.valueOf(-65535);

   private static final BigInteger NULL_CALENDAR_ID = BigInteger.valueOf(-1);
}
