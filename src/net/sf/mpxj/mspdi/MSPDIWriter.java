/*
 * file:       MSPDIWriter.java
 * author:     Jon Iles
 * copyright:  (c) Packwood Software Limited 2005
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
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.TreeSet;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;

import net.sf.mpxj.AccrueType;
import net.sf.mpxj.DateRange;
import net.sf.mpxj.Day;
import net.sf.mpxj.Duration;
import net.sf.mpxj.ExtendedAttributeResourceFields;
import net.sf.mpxj.ExtendedAttributeTaskFields;
import net.sf.mpxj.MPPResourceField;
import net.sf.mpxj.MPPTaskField;
import net.sf.mpxj.ProjectCalendar;
import net.sf.mpxj.ProjectCalendarException;
import net.sf.mpxj.ProjectCalendarHours;
import net.sf.mpxj.ProjectFile;
import net.sf.mpxj.ProjectHeader;
import net.sf.mpxj.Relation;
import net.sf.mpxj.RelationType;
import net.sf.mpxj.Resource;
import net.sf.mpxj.ResourceAssignment;
import net.sf.mpxj.ResourceField;
import net.sf.mpxj.ScheduleFrom;
import net.sf.mpxj.Task;
import net.sf.mpxj.TaskField;
import net.sf.mpxj.TimeUnit;
import net.sf.mpxj.mspdi.schema.ObjectFactory;
import net.sf.mpxj.mspdi.schema.Project;
import net.sf.mpxj.utility.NumberUtility;
import net.sf.mpxj.writer.AbstractProjectWriter;


/**
 * This class creates a new MSPDI file from the contents of an ProjectFile instance.
 */
public final class MSPDIWriter extends AbstractProjectWriter
{
   /**
    * {@inheritDoc}
    */
   public void write (ProjectFile projectFile, OutputStream stream)
      throws IOException
   {
      try
      {
         m_projectFile = projectFile;

         JAXBContext context = JAXBContext.newInstance ("net.sf.mpxj.mspdi.schema");
         Marshaller marshaller = context.createMarshaller();
         marshaller.setProperty (Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);

         ObjectFactory factory = new ObjectFactory ();
         Project project = factory.createProject();

         writeProjectHeader (project);
         writeProjectExtendedAttributes (factory, project);
         writeCalendars (factory, project);
         writeResources (factory, project);
         writeTasks (factory, project);
         writeAssignments (factory, project);

         DatatypeConverter.setParentFile(m_projectFile);
         marshaller.marshal (project, stream);
      }

      catch (JAXBException ex)
      {
         throw new IOException (ex.toString());
      }

      finally
      {
         m_projectFile = null;
      }
   }


   /**
    * This method writes project header data to an MSPDI file.
    *
    * @param project Root node of the MSPDI file
    */
   private void writeProjectHeader (Project project)
   {
      ProjectHeader header = m_projectFile.getProjectHeader ();

      project.setActualsInSync(Boolean.valueOf(header.getActualsInSync()));
      project.setAdminProject(Boolean.valueOf(header.getAdminProject()));
      project.setAuthor(header.getAuthor());
      project.setAutoAddNewResourcesAndTasks(Boolean.valueOf(header.getAutoAddNewResourcesAndTasks()));
      project.setAutolink(Boolean.valueOf(header.getAutolink()));
      project.setBaselineForEarnedValue(NumberUtility.getBigInteger(header.getBaselineForEarnedValue()));
      project.setCalendarUID(BigInteger.ONE);
      project.setCategory(header.getCategory());
      project.setCompany(header.getCompany());
      project.setCreationDate(DatatypeConverter.printDate(header.getCreationDate()));
      project.setCriticalSlackLimit(NumberUtility.getBigInteger(header.getCriticalSlackLimit()));
      project.setCurrencyCode(header.getCurrencyCode());
      project.setCurrencyDigits(BigInteger.valueOf (header.getCurrencyDigits().intValue()));
      project.setCurrencySymbol(header.getCurrencySymbol());
      project.setCurrencySymbolPosition(header.getSymbolPosition());
      project.setCurrentDate(DatatypeConverter.printDate(header.getCurrentDate()));
      project.setDaysPerMonth(NumberUtility.getBigInteger(header.getDaysPerMonth()));
      project.setDefaultFinishTime(DatatypeConverter.printTime (header.getDefaultEndTime()));
      project.setDefaultFixedCostAccrual(header.getDefaultFixedCostAccrual());
      project.setDefaultOvertimeRate(DatatypeConverter.printRate(header.getDefaultOvertimeRate()));
      project.setDefaultStandardRate(DatatypeConverter.printRate(header.getDefaultStandardRate()));
      project.setDefaultStartTime(DatatypeConverter.printTime (header.getDefaultStartTime()));
      project.setDefaultTaskEVMethod(DatatypeConverter.printEarnedValueMethod(header.getDefaultTaskEarnedValueMethod()));
      project.setDefaultTaskType(header.getDefaultTaskType());
      project.setDurationFormat(DatatypeConverter.printDurationTimeUnits(header.getDefaultDurationUnits()));
      project.setEarnedValueMethod(DatatypeConverter.printEarnedValueMethod(header.getEarnedValueMethod()));
      project.setEditableActualCosts(Boolean.valueOf(header.getEditableActualCosts()));
      project.setExtendedCreationDate(DatatypeConverter.printDate(header.getExtendedCreationDate()));
      project.setFinishDate(DatatypeConverter.printDate(header.getFinishDate()));
      project.setFiscalYearStart(Boolean.valueOf(header.getFiscalYearStart()));
      project.setFYStartDate(NumberUtility.getBigInteger(header.getFiscalYearStartMonth()));
      project.setHonorConstraints(Boolean.valueOf(header.getHonorConstraints()));
      project.setInsertedProjectsLikeSummary(Boolean.valueOf(header.getInsertedProjectsLikeSummary()));
      project.setLastSaved(DatatypeConverter.printDate(header.getLastSaved()));
      project.setManager(header.getManager());
      project.setMicrosoftProjectServerURL(Boolean.valueOf(header.getMicrosoftProjectServerURL()));
      project.setMinutesPerDay(NumberUtility.getBigInteger(header.getMinutesPerDay()));
      project.setMinutesPerWeek(NumberUtility.getBigInteger(header.getMinutesPerWeek()));
      project.setMoveCompletedEndsBack(Boolean.valueOf(header.getMoveCompletedEndsBack()));
      project.setMoveCompletedEndsForward(Boolean.valueOf(header.getMoveCompletedEndsForward()));
      project.setMoveRemainingStartsBack(Boolean.valueOf(header.getMoveRemainingStartsBack()));
      project.setMoveRemainingStartsForward(Boolean.valueOf(header.getMoveRemainingStartsForward()));
      project.setMultipleCriticalPaths(Boolean.valueOf(header.getMultipleCriticalPaths()));
      project.setName(header.getName());
      project.setNewTasksEffortDriven(Boolean.valueOf(header.getNewTasksEffortDriven()));
      project.setNewTasksEstimated(Boolean.valueOf(header.getNewTasksEstimated()));
      project.setNewTaskStartDate(header.getNewTaskStartIsProjectStart()==true?BigInteger.ZERO:BigInteger.ONE);
      project.setProjectExternallyEdited(Boolean.valueOf(header.getProjectExternallyEdited()));
      project.setRemoveFileProperties(Boolean.valueOf(header.getRemoveFileProperties()));
      project.setRevision(NumberUtility.getBigInteger(header.getRevision()));
      project.setScheduleFromStart(Boolean.valueOf(header.getScheduleFrom() == ScheduleFrom.START));
      project.setSplitsInProgressTasks(Boolean.valueOf(header.getSplitInProgressTasks()));
      project.setSpreadActualCost(Boolean.valueOf(header.getSpreadActualCost()));
      project.setSpreadPercentComplete(Boolean.valueOf(header.getSpreadPercentComplete()));
      project.setStartDate(DatatypeConverter.printDate(header.getStartDate()));
      project.setStatusDate(DatatypeConverter.printDate(header.getStatusDate()));
      project.setSubject(header.getSubject());
      project.setTaskUpdatesResource(Boolean.valueOf(header.getUpdatingTaskStatusUpdatesResourceStatus()));
      project.setTitle(header.getProjectTitle());
      project.setUID(header.getUniqueID());
      project.setWeekStartDay(DatatypeConverter.printDay(header.getWeekStartDay()));
      project.setWorkFormat(DatatypeConverter.printWorkUnits(header.getDefaultWorkUnits()));
   }

   /**
    * This method writes project extended attribute data into an MSPDI file.
    *
    * @param factory object factory
    * @param project Root node of the MSPDI file
    */
   private void writeProjectExtendedAttributes (ObjectFactory factory, Project project)
   {
      Project.ExtendedAttributes attributes = factory.createProjectExtendedAttributes();
      project.setExtendedAttributes(attributes);
      List<Project.ExtendedAttributes.ExtendedAttribute> list = attributes.getExtendedAttribute();

      writeTaskFieldAliases(factory, list);
      writeResourceFieldAliases(factory, list);      
   }

   /**
    * Writes field aliases.
    * 
    * @param factory object factory
    * @param list field alias list
    */
   private void writeTaskFieldAliases (ObjectFactory factory, List<Project.ExtendedAttributes.ExtendedAttribute> list)
   {
      Map<TaskField, String> fieldAliasMap = m_projectFile.getTaskFieldAliasMap();
      
      for (int loop=0; loop < ExtendedAttributeTaskFields.FIELD_ARRAY.length; loop++)
      {
         TaskField key = ExtendedAttributeTaskFields.FIELD_ARRAY[loop];
         Integer fieldID = Integer.valueOf(MPPTaskField.getID(key) | MPPTaskField.TASK_FIELD_BASE);
         String name = key.getName();
         String alias = fieldAliasMap.get(key);
   
         Project.ExtendedAttributes.ExtendedAttribute attribute = factory.createProjectExtendedAttributesExtendedAttribute();
         list.add(attribute);
         attribute.setFieldID(fieldID.toString());
         attribute.setFieldName(name);
         attribute.setAlias(alias);
      }
   }

   /**
    * Writes field aliases.
    * 
    * @param factory object factory
    * @param list field alias list
    */   
   private void writeResourceFieldAliases (ObjectFactory factory, List<Project.ExtendedAttributes.ExtendedAttribute> list)
   {
      Map<ResourceField, String> fieldAliasMap = m_projectFile.getResourceFieldAliasMap();
      
      for (int loop=0; loop < ExtendedAttributeResourceFields.FIELD_ARRAY.length; loop++)
      {
         ResourceField key = ExtendedAttributeResourceFields.FIELD_ARRAY[loop];
         Integer fieldID = Integer.valueOf(MPPResourceField.getID(key) | MPPResourceField.RESOURCE_FIELD_BASE);
         String name = key.getName();
         String alias = fieldAliasMap.get(key);
   
         Project.ExtendedAttributes.ExtendedAttribute attribute = factory.createProjectExtendedAttributesExtendedAttribute();
         list.add(attribute);
         attribute.setFieldID(fieldID.toString());
         attribute.setFieldName(name);
         attribute.setAlias(alias);
      }
   }
   
   /**
    * This method writes calendar data to an MSPDI file.
    *
    * @param factory ObjectFactory instance
    * @param project Root node of the MSPDI file
    */
   private void writeCalendars (ObjectFactory factory, Project project)
   {
      //
      // First step, find all of the base calendars and resource calendars,
      // add them to a list ready for processing, and create a map between
      // names and unique IDs
      //
      LinkedList<ProjectCalendar> calendarList = new LinkedList<ProjectCalendar>(m_projectFile.getBaseCalendars ());

      for (Resource resource : m_projectFile.getAllResources())
      {
         ProjectCalendar cal = resource.getResourceCalendar();
         if (cal != null)
         {
            calendarList.add(cal);
         }
      }

      //
      // Create the new MSPDI calendar list
      //
      Project.Calendars calendars = factory.createProjectCalendars();
      project.setCalendars (calendars);
      List<Project.Calendars.Calendar> calendar = calendars.getCalendar();

      //
      // Process each calendar in turn
      //
      factory.createProjectCalendarsCalendar();
      for (ProjectCalendar cal : calendarList)
      {
         calendar.add (writeCalendar (factory, cal));
      }
   }

   /**
    * This method writes data for a single calendar to an MSPDI file.
    *
    * @param factory ObjectFactory instance
    * @param bc Base calendar data
    * @return New MSPDI calendar instance
    */
   private Project.Calendars.Calendar writeCalendar (ObjectFactory factory, ProjectCalendar bc)
   {
      //
      // Create a calendar
      //
      Project.Calendars.Calendar calendar = factory.createProjectCalendarsCalendar();
      calendar.setUID(NumberUtility.getBigInteger(bc.getUniqueID()));
      calendar.setIsBaseCalendar(Boolean.valueOf(bc.isBaseCalendar()));

      if (bc.isBaseCalendar() == false)
      {
         ProjectCalendar base = bc.getBaseCalendar();
         if (base != null)
         {
            calendar.setBaseCalendarUID(NumberUtility.getBigInteger(base.getUniqueID()));
         }
      }

      calendar.setName(bc.getName());

      //
      // Create a list of normal days
      //
      Project.Calendars.Calendar.WeekDays days = factory.createProjectCalendarsCalendarWeekDays();
      Project.Calendars.Calendar.WeekDays.WeekDay.WorkingTimes.WorkingTime time;
      ProjectCalendarHours bch;

      List<Project.Calendars.Calendar.WeekDays.WeekDay> dayList = days.getWeekDay();

      for (int loop=1; loop < 8; loop++)
      {
         int workingFlag = bc.getWorkingDay(Day.getInstance(loop));

         if (workingFlag != ProjectCalendar.DEFAULT)
         {
            Project.Calendars.Calendar.WeekDays.WeekDay day = factory.createProjectCalendarsCalendarWeekDaysWeekDay();
            dayList.add(day);
            day.setDayType(BigInteger.valueOf(loop));
            day.setDayWorking(Boolean.valueOf(workingFlag == ProjectCalendar.WORKING));
            
            if (workingFlag == ProjectCalendar.WORKING)
            {
               Project.Calendars.Calendar.WeekDays.WeekDay.WorkingTimes times = factory.createProjectCalendarsCalendarWeekDaysWeekDayWorkingTimes ();
               day.setWorkingTimes(times);
               List<Project.Calendars.Calendar.WeekDays.WeekDay.WorkingTimes.WorkingTime> timesList = times.getWorkingTime();

               bch = bc.getCalendarHours (Day.getInstance(loop));
               if (bch != null)
               {
                  for (DateRange range : bch)
                  {
                     if (range != null)
                     {
                        time = factory.createProjectCalendarsCalendarWeekDaysWeekDayWorkingTimesWorkingTime ();
                        timesList.add (time);

                        time.setFromTime(DatatypeConverter.printTime(range.getStartDate()));
                        time.setToTime(DatatypeConverter.printTime(range.getEndDate()));
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
      Collections.sort(exceptions);

      for (ProjectCalendarException exception : exceptions)
      {
         boolean working = exception.getWorking();

         Project.Calendars.Calendar.WeekDays.WeekDay day = factory.createProjectCalendarsCalendarWeekDaysWeekDay();
         dayList.add(day);
         day.setDayType(BIGINTEGER_ZERO);
         day.setDayWorking(Boolean.valueOf(working));

         Project.Calendars.Calendar.WeekDays.WeekDay.TimePeriod period = factory.createProjectCalendarsCalendarWeekDaysWeekDayTimePeriod();
         day.setTimePeriod(period);
         period.setFromDate(DatatypeConverter.printDate(exception.getFromDate()));
         period.setToDate(DatatypeConverter.printDate (exception.getToDate()));

         if (working == true)
         {
            Project.Calendars.Calendar.WeekDays.WeekDay.WorkingTimes times = factory.createProjectCalendarsCalendarWeekDaysWeekDayWorkingTimes ();
            day.setWorkingTimes(times);
            List<Project.Calendars.Calendar.WeekDays.WeekDay.WorkingTimes.WorkingTime> timesList = times.getWorkingTime();

            for (DateRange range : exception)
            {
               time = factory.createProjectCalendarsCalendarWeekDaysWeekDayWorkingTimesWorkingTime ();
               timesList.add (time);

               time.setFromTime(DatatypeConverter.printTime(range.getStartDate()));
               time.setToTime(DatatypeConverter.printTime(range.getEndDate()));
            }            
         }                 
      }

      //
      // Do not add a weekdays tag to the calendar unless it
      // has valid entries.
      // Fixes SourceForge bug 1854747: MPXJ and MSP 2007 XML formats
      //
      if (!dayList.isEmpty())
      {
         calendar.setWeekDays (days);   
      }
      
      return (calendar);
   }

   /**
    * This method writes resource data to an MSPDI file.
    *
    * @param factory ObjectFactory instance
    * @param project Root node of the MSPDI file
    */
   private void writeResources (ObjectFactory factory, Project project)
   {
      Project.Resources resources = factory.createProjectResources();
      project.setResources(resources);
      List<Project.Resources.Resource> list = resources.getResource();

      for (Resource resource : m_projectFile.getAllResources())
      {
         list.add (writeResource (factory, resource));
      }
   }

   /**
    * This method writes data for a single resource to an MSPDI file.
    *
    * @param factory ObjectFactory instance
    * @param mpx Resource data
    * @return New MSPDI resource instance
    */
   private Project.Resources.Resource writeResource (ObjectFactory factory, Resource mpx)
   {
      Project.Resources.Resource xml = factory.createProjectResourcesResource();
      ProjectCalendar cal = mpx.getResourceCalendar();
      if (cal != null)
      {
         xml.setCalendarUID(NumberUtility.getBigInteger(cal.getUniqueID()));
      }

      xml.setAccrueAt(mpx.getAccrueAt());
      xml.setActiveDirectoryGUID(mpx.getActiveDirectoryGUID());
      xml.setActualCost(DatatypeConverter.printCurrency (mpx.getActualCost()));
      xml.setActualOvertimeCost(DatatypeConverter.printCurrency(mpx.getActualOvertimeCost()));
      xml.setActualOvertimeWork(DatatypeConverter.printDuration(this, mpx.getActualOvertimeWork()));
      xml.setActualOvertimeWorkProtected(DatatypeConverter.printDuration(this, mpx.getActualOvertimeWorkProtected()));
      xml.setActualWork(DatatypeConverter.printDuration (this, mpx.getActualWork()));
      xml.setActualWorkProtected(DatatypeConverter.printDuration(this, mpx.getActualWorkProtected()));
      xml.setACWP(DatatypeConverter.printCurrency(mpx.getACWP()));
      xml.setAvailableFrom(DatatypeConverter.printDate(mpx.getAvailableFrom()));
      xml.setAvailableTo(DatatypeConverter.printDate(mpx.getAvailableTo()));
      xml.setBCWS(DatatypeConverter.printCurrency(mpx.getBCWS()));
      xml.setBCWP(DatatypeConverter.printCurrency(mpx.getBCWP()));
      xml.setBookingType(mpx.getBookingType());
      xml.setCanLevel(Boolean.valueOf(mpx.getCanLevel()));
      xml.setCode(mpx.getCode());
      xml.setCost(DatatypeConverter.printCurrency(mpx.getCost()));
      xml.setCostPerUse(DatatypeConverter.printCurrency(mpx.getCostPerUse()));
      xml.setCostVariance(DatatypeConverter.printCurrency(mpx.getCostVariance()));
      xml.setCreationDate(DatatypeConverter.printDate(mpx.getCreationDate()));
      xml.setCV(DatatypeConverter.printCurrency(mpx.getCV()));
      xml.setEmailAddress(mpx.getEmailAddress());
      xml.setFinish(DatatypeConverter.printDate(mpx.getFinish()));
      xml.setGroup(mpx.getGroup());
      xml.setHyperlink(mpx.getHyperlink());
      xml.setHyperlinkAddress(mpx.getHyperlinkAddress());
      xml.setHyperlinkSubAddress(mpx.getHyperlinkSubAddress());
      xml.setID(NumberUtility.getBigInteger(mpx.getID()));
      xml.setInitials(mpx.getInitials());
      xml.setIsEnterprise(Boolean.valueOf(mpx.getEnterprise()));
      xml.setIsGeneric(Boolean.valueOf(mpx.getGeneric()));
      xml.setIsInactive(Boolean.valueOf(mpx.getInactive()));
      xml.setIsNull(Boolean.valueOf(mpx.getNull()));
      xml.setMaterialLabel(mpx.getMaterialLabel());
      xml.setMaxUnits(DatatypeConverter.printUnits(mpx.getMaxUnits()));
      xml.setName(mpx.getName());
      xml.setNotes(mpx.getNotes());
      xml.setNTAccount(mpx.getNtAccount());
      xml.setOverAllocated(Boolean.valueOf(mpx.getOverAllocated()));
      xml.setOvertimeCost(DatatypeConverter.printCurrency(mpx.getOvertimeCost()));
      xml.setOvertimeRate(DatatypeConverter.printRate (mpx.getOvertimeRate()));
      xml.setOvertimeRateFormat(DatatypeConverter.printTimeUnit(mpx.getOvertimeRateFormat()));
      xml.setOvertimeWork(DatatypeConverter.printDuration (this, mpx.getOvertimeWork()));
      xml.setPeakUnits(DatatypeConverter.printUnits(mpx.getPeakUnits()));
      xml.setPercentWorkComplete(NumberUtility.getBigInteger(mpx.getPercentWorkComplete()));
      xml.setPhonetics(mpx.getPhonetics());
      xml.setRegularWork(DatatypeConverter.printDuration(this, mpx.getRegularWork()));
      xml.setRemainingCost(DatatypeConverter.printCurrency(mpx.getRemainingCost()));
      xml.setRemainingOvertimeCost(DatatypeConverter.printCurrency(mpx.getRemainingOvertimeCost()));
      xml.setRemainingOvertimeWork(DatatypeConverter.printDuration(this, mpx.getRemainingOvertimeWork()));
      xml.setRemainingWork(DatatypeConverter.printDuration(this, mpx.getRemainingWork()));
      xml.setStandardRate(DatatypeConverter.printRate (mpx.getStandardRate()));
      xml.setStandardRateFormat(DatatypeConverter.printTimeUnit(mpx.getStandardRateFormat()));
      xml.setStart(DatatypeConverter.printDate(mpx.getStart()));
      xml.setSV(DatatypeConverter.printCurrency(mpx.getSV()));
      xml.setType(mpx.getType());
      xml.setUID(mpx.getUniqueID());
      xml.setWork(DatatypeConverter.printDuration(this, mpx.getWork()));
      xml.setWorkGroup(mpx.getWorkGroup());
      xml.setWorkVariance(new BigDecimal(DatatypeConverter.printDurationInMinutes(mpx.getWorkVariance())*1000));

      writeResourceExtendedAttributes (factory, xml, mpx);

      writeResourceBaselines(factory, xml, mpx);
      
      return (xml);
   }

   /**
    * Writes resource baseline data.
    * 
    * @param factory JAXB object factory
    * @param xmlResource MSPDI resource
    * @param mpxjResource MPXJ resource
    */
   private void writeResourceBaselines (ObjectFactory factory, Project.Resources.Resource xmlResource, Resource mpxjResource)
   {
      Project.Resources.Resource.Baseline baseline = factory.createProjectResourcesResourceBaseline();
      xmlResource.getBaseline().add(baseline);
      
      baseline.setNumber(BigInteger.ZERO);
      baseline.setCost(DatatypeConverter.printCurrency(mpxjResource.getBaselineCost()));
      baseline.setWork(DatatypeConverter.printDuration(this, mpxjResource.getBaselineWork()));
      
      for (int loop=1; loop <=10; loop++)
      {
         baseline = factory.createProjectResourcesResourceBaseline();
         xmlResource.getBaseline().add(baseline);
         
         baseline.setNumber(BigInteger.valueOf(loop));
         baseline.setCost(DatatypeConverter.printCurrency(mpxjResource.getBaselineCost(loop)));
         baseline.setWork(DatatypeConverter.printDuration(this, mpxjResource.getBaselineWork(loop)));         
      }
   }

   /**
    * This method writes extended attribute data for a resource.
    *
    * @param factory JAXB object factory
    * @param xml MSPDI resource
    * @param mpx MPXJ resource
    */
   private void writeResourceExtendedAttributes (ObjectFactory factory, Project.Resources.Resource xml, Resource mpx)
   {
      Project.Resources.Resource.ExtendedAttribute attrib;
      List<Project.Resources.Resource.ExtendedAttribute> extendedAttributes = xml.getExtendedAttribute();
      Object value;
      ResourceField mpxFieldID;
      Integer xmlFieldID;

      for (int loop=0; loop < ExtendedAttributeResourceFields.FIELD_ARRAY.length; loop++)
      {
         mpxFieldID = ExtendedAttributeResourceFields.FIELD_ARRAY[loop];
         value = mpx.getCachedValue(mpxFieldID);

         if (value != null)
         {
            xmlFieldID = Integer.valueOf(MPPResourceField.getID(mpxFieldID) | MPPResourceField.RESOURCE_FIELD_BASE);

            attrib = factory.createProjectResourcesResourceExtendedAttribute();
            extendedAttributes.add(attrib);
            attrib.setFieldID(xmlFieldID.toString());
            attrib.setValue(DatatypeConverter.printExtendedAttribute(this, value, mpxFieldID.getDataType()));
            attrib.setDurationFormat(printExtendedAttributeDurationFormat(value));
         }
      }
   }

   /**
    * This method writes task data to an MSPDI file.
    *
    * @param factory ObjectFactory instance
    * @param project Root node of the MSPDI file
    */
   private void writeTasks (ObjectFactory factory, Project project)
   {
      Project.Tasks tasks = factory.createProjectTasks();
      project.setTasks (tasks);
      List<Project.Tasks.Task> list = tasks.getTask();

      for (Task task : m_projectFile.getAllTasks())
      {
         list.add (writeTask (factory, task));
      }
   }


   /**
    * This method writes data for a single task to an MSPDI file.
    *
    * @param factory ObjectFactory instance
    * @param mpx Task data
    * @return new task instance
    */
   private Project.Tasks.Task writeTask (ObjectFactory factory, Task mpx)
   {
      Project.Tasks.Task xml = factory.createProjectTasksTask();

      xml.setActualCost(DatatypeConverter.printCurrency(mpx.getActualCost()));
      xml.setActualDuration(DatatypeConverter.printDuration(this, mpx.getActualDuration()));
      xml.setActualFinish(DatatypeConverter.printDate(mpx.getActualFinish()));
      xml.setActualOvertimeCost(DatatypeConverter.printCurrency(mpx.getActualOvertimeCost()));
      xml.setActualOvertimeWork(DatatypeConverter.printDuration(this, mpx.getActualOvertimeWork()));
      xml.setActualOvertimeWorkProtected(DatatypeConverter.printDuration(this, mpx.getActualOvertimeWorkProtected()));
      xml.setActualStart(DatatypeConverter.printDate(mpx.getActualStart()));
      xml.setActualWork(DatatypeConverter.printDuration(this, mpx.getActualWork()));
      xml.setActualWorkProtected(DatatypeConverter.printDuration(this, mpx.getActualWorkProtected()));
      xml.setACWP(DatatypeConverter.printCurrency(mpx.getACWP()));
      xml.setBCWP(DatatypeConverter.printCurrency(mpx.getBCWP()));
      xml.setBCWS(DatatypeConverter.printCurrency(mpx.getBCWS()));
      xml.setCalendarUID(getTaskCalendarID(mpx));
      xml.setConstraintDate(DatatypeConverter.printDate(mpx.getConstraintDate()));
      xml.setConstraintType(DatatypeConverter.printConstraintType(mpx.getConstraintType()));
      xml.setContact(mpx.getContact());
      xml.setCost(DatatypeConverter.printCurrency(mpx.getCost()));
      xml.setCreateDate(DatatypeConverter.printDate(mpx.getCreateDate()));
      xml.setCritical(Boolean.valueOf(mpx.getCritical()));
      xml.setCV(DatatypeConverter.printCurrency(mpx.getCV()));
      xml.setDeadline(DatatypeConverter.printDate(mpx.getDeadline()));
      xml.setDuration(DatatypeConverter.printDuration(this, mpx.getDuration()));
      xml.setDurationFormat(DatatypeConverter.printDurationTimeUnits(mpx.getDurationFormat()));
      xml.setDurationFormat(DatatypeConverter.printDurationTimeUnits(mpx.getDuration()));
      xml.setEarlyFinish(DatatypeConverter.printDate(mpx.getEarlyFinish()));
      xml.setEarlyStart(DatatypeConverter.printDate(mpx.getEarlyStart()));
      xml.setEarnedValueMethod(DatatypeConverter.printEarnedValueMethod(mpx.getEarnedValueMethod()));
      xml.setEffortDriven(Boolean.valueOf(mpx.getEffortDriven()));
      xml.setEstimated(Boolean.valueOf(mpx.getEstimated()));
      xml.setExternalTask(Boolean.valueOf(mpx.getExternalTask()));
      xml.setExternalTaskProject(mpx.getProject());

      Date finishDate = mpx.getFinish();
      if (finishDate != null)
      {
         xml.setFinish(DatatypeConverter.printDate(finishDate));
      }

      xml.setFinishVariance(BigInteger.valueOf((long)DatatypeConverter.printDurationInMinutes(mpx.getFinishVariance())*1000));
      xml.setFixedCost(DatatypeConverter.printCurrency(mpx.getFixedCost()));

      AccrueType fixedCostAccrual = mpx.getFixedCostAccrual();
      if (fixedCostAccrual == null)
      {
         fixedCostAccrual = AccrueType.PRORATED;
      }
      xml.setFixedCostAccrual(fixedCostAccrual);

      // This is not correct
      //xml.setFreeSlack(BigInteger.valueOf((long)DatatypeConverter.printDurationInMinutes(mpx.getFreeSlack())*1000));
      xml.setFreeSlack(BIGINTEGER_ZERO);
      xml.setHideBar(Boolean.valueOf(mpx.getHideBar()));
      xml.setIsNull(Boolean.valueOf(mpx.getNull()));
      xml.setIsSubproject(Boolean.valueOf(mpx.getSubProject()!=null));
      xml.setIsSubprojectReadOnly(Boolean.valueOf(mpx.getSubprojectReadOnly()));
      xml.setHyperlink(mpx.getHyperlink());
      xml.setHyperlinkAddress(mpx.getHyperlinkAddress());
      xml.setHyperlinkSubAddress(mpx.getHyperlinkSubAddress());
      xml.setID(NumberUtility.getBigInteger(mpx.getID()));
      xml.setIgnoreResourceCalendar(Boolean.valueOf(mpx.getIgnoreResourceCalendar()));
      xml.setLateFinish(DatatypeConverter.printDate(mpx.getLateFinish()));
      xml.setLateStart(DatatypeConverter.printDate(mpx.getLateStart()));
      xml.setLevelAssignments(Boolean.valueOf(mpx.getLevelAssignments()));
      xml.setLevelingCanSplit(Boolean.valueOf(mpx.getLevelingCanSplit()));

      if (mpx.getLevelingDelay() != null)
      {
         xml.setLevelingDelay(BigInteger.valueOf((long)mpx.getLevelingDelay().getDuration()));
         xml.setLevelingDelayFormat(DatatypeConverter.printDurationTimeUnits(mpx.getLevelingDelayFormat()));
      }

      xml.setMilestone(Boolean.valueOf(mpx.getMilestone()));
      xml.setName(mpx.getName());
      xml.setNotes(mpx.getNotes());
      xml.setOutlineLevel(NumberUtility.getBigInteger(mpx.getOutlineLevel()));
      xml.setOutlineNumber(mpx.getOutlineNumber());
      xml.setOverAllocated(Boolean.valueOf(mpx.getOverAllocated()));
      xml.setOvertimeCost(DatatypeConverter.printCurrency(mpx.getOvertimeCost()));
      xml.setOvertimeWork(DatatypeConverter.printDuration(this, mpx.getOvertimeWork()));
      xml.setPercentComplete(NumberUtility.getBigInteger(mpx.getPercentageComplete()));
      xml.setPercentWorkComplete(NumberUtility.getBigInteger(mpx.getPercentageWorkComplete()));
      xml.setPhysicalPercentComplete(NumberUtility.getBigInteger(mpx.getPhysicalPercentComplete()));
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
            amount -= ((amount * NumberUtility.getDouble(mpx.getPercentageComplete()))/100);
            xml.setRemainingDuration(DatatypeConverter.printDuration(this, Duration.getInstance (amount, duration.getUnits())));
         }
      }
      else
      {
         xml.setRemainingDuration(DatatypeConverter.printDuration(this, mpx.getRemainingDuration()));
      }

      xml.setRemainingOvertimeCost(DatatypeConverter.printCurrency(mpx.getRemainingOvertimeCost()));
      xml.setRemainingOvertimeWork(DatatypeConverter.printDuration(this, mpx.getRemainingOvertimeWork()));
      xml.setRemainingWork(DatatypeConverter.printDuration(this, mpx.getRemainingWork()));
      xml.setResume(DatatypeConverter.printDate(mpx.getResume()));
      xml.setResumeValid(Boolean.valueOf(mpx.getResumeValid()));
      xml.setRollup(Boolean.valueOf(mpx.getRollup()));
      xml.setStart(DatatypeConverter.printDate(mpx.getStart()));
      xml.setStartVariance(BigInteger.valueOf((long)DatatypeConverter.printDurationInMinutes(mpx.getStartVariance())*1000));
      xml.setStop(DatatypeConverter.printDate (mpx.getStop()));
      xml.setSubprojectName(mpx.getSubprojectName());
      xml.setSummary(Boolean.valueOf(mpx.getSummary()));
      xml.setTotalSlack(BigInteger.valueOf((long)DatatypeConverter.printDurationInMinutes(mpx.getTotalSlack())*1000));
      xml.setType(mpx.getType());
      xml.setUID(mpx.getUniqueID());
      xml.setWBS(mpx.getWBS());
      xml.setWBSLevel(mpx.getWBSLevel());
      xml.setWork(DatatypeConverter.printDuration(this, mpx.getWork()));
      xml.setWorkVariance(new BigDecimal(DatatypeConverter.printDurationInMinutes(mpx.getWorkVariance())*1000));

      writePredecessors (factory, xml, mpx);

      writeTaskExtendedAttributes (factory, xml, mpx);

      writeTaskBaselines (factory, xml, mpx);
      
      return (xml);
   }

   /**
    * Writes task baseline data.
    * 
    * @param factory JAXB object factory
    * @param xmlTask MSPDI task
    * @param mpxjTask MPXJ task
    */
   private void writeTaskBaselines (ObjectFactory factory, Project.Tasks.Task xmlTask, Task mpxjTask)
   {
      Project.Tasks.Task.Baseline baseline = factory.createProjectTasksTaskBaseline();
      xmlTask.getBaseline().add(baseline);
      
      baseline.setNumber(BigInteger.ZERO);
      baseline.setCost(DatatypeConverter.printCurrency(mpxjTask.getBaselineCost()));
      baseline.setDuration(DatatypeConverter.printDuration(this, mpxjTask.getBaselineDuration()));
      baseline.setDurationFormat(DatatypeConverter.printDurationTimeUnits(mpxjTask.getBaselineDuration()));
      baseline.setFinish(DatatypeConverter.printDate(mpxjTask.getBaselineFinish()));
      baseline.setStart(DatatypeConverter.printDate(mpxjTask.getBaselineStart()));
      baseline.setWork(DatatypeConverter.printDuration(this, mpxjTask.getBaselineWork()));
      
      for (int loop=1; loop <=10; loop++)
      {
         baseline = factory.createProjectTasksTaskBaseline();
         xmlTask.getBaseline().add(baseline);
         
         baseline.setNumber(BigInteger.valueOf(loop));
         baseline.setCost(DatatypeConverter.printCurrency(mpxjTask.getBaselineCost(loop)));
         baseline.setDuration(DatatypeConverter.printDuration(this, mpxjTask.getBaselineDuration(loop)));
         baseline.setDurationFormat(DatatypeConverter.printDurationTimeUnits(mpxjTask.getBaselineDuration(loop)));
         baseline.setFinish(DatatypeConverter.printDate(mpxjTask.getBaselineFinish(loop)));
         baseline.setStart(DatatypeConverter.printDate(mpxjTask.getBaselineStart(loop)));
         baseline.setWork(DatatypeConverter.printDuration(this, mpxjTask.getBaselineWork(loop)));         
      }
   }
   
   /**
    * This method writes extended attribute data for a task.
    *
    * @param factory JAXB object factory
    * @param xml MSPDI task
    * @param mpx MPXJ task
    */
   private void writeTaskExtendedAttributes (ObjectFactory factory, Project.Tasks.Task xml, Task mpx)
   {
      Project.Tasks.Task.ExtendedAttribute attrib;
      List<Project.Tasks.Task.ExtendedAttribute> extendedAttributes = xml.getExtendedAttribute();
      Object value;
      TaskField mpxFieldID;
      Integer xmlFieldID;

      for (int loop=0; loop < ExtendedAttributeTaskFields.FIELD_ARRAY.length; loop++)
      {
         mpxFieldID = ExtendedAttributeTaskFields.FIELD_ARRAY[loop];
         value = mpx.getCachedValue(mpxFieldID);

         if (value != null)
         {
            xmlFieldID = Integer.valueOf(MPPTaskField.getID(mpxFieldID) | MPPTaskField.TASK_FIELD_BASE);

            attrib = factory.createProjectTasksTaskExtendedAttribute();
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
   private BigInteger printExtendedAttributeDurationFormat (Object value)
   {
      BigInteger result = null;
      if (value instanceof Duration)
      {
         result = DatatypeConverter.printDurationTimeUnits(((Duration)value).getUnits());
      }
      return (result);
   }

   /**
    * This method retrieves the UID for a calendar associated with a task.
    *
    * @param mpx MPX Task instance
    * @return calendar UID
    */
   private BigInteger getTaskCalendarID (Task mpx)
   {
      BigInteger result = null;
      ProjectCalendar cal = mpx.getCalendar();
      if (cal != null)
      {
         result = NumberUtility.getBigInteger(cal.getUniqueID());
      }
      else
      {
         result = BigInteger.valueOf(-1);
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
    * @param factory ObjectFactory instance
    * @param xml MSPDI task data
    * @param mpx MPX task data
    */
   private void writePredecessors (ObjectFactory factory, Project.Tasks.Task xml, Task mpx)
   {
      TreeSet<Integer> set = new TreeSet<Integer> ();
      List<Project.Tasks.Task.PredecessorLink> list = xml.getPredecessorLink();

      //
      // Process the list of predecessors specified by Unique ID
      //
      List<Relation> predecessors = mpx.getUniqueIDPredecessors();
      if (predecessors != null)
      {
         for (Relation rel : predecessors)
         {
            Integer taskUniqueID = rel.getTaskUniqueID();
            set.add(taskUniqueID);
            list.add (writePredecessor (factory, taskUniqueID, rel.getType(), rel.getDuration()));
         }
      }

      //
      // Process the list of predecessors specified by ID.
      // Note that this code ensures that if both lists are populated,
      // we avoid creating duplicate links.
      //
      predecessors = mpx.getPredecessors();
      if (predecessors != null)
      {
         for (Relation rel : predecessors)
         {
            Integer taskUniqueID = rel.getTaskUniqueID();
            if (set.contains(taskUniqueID) == false)
            {
               list.add (writePredecessor (factory, taskUniqueID, rel.getType(), rel.getDuration()));
            }
         }
      }
   }

   /**
    * This method writes a single predecessor link to the MSPDI file.
    *
    * @param factory ObjectFactory instance
    * @param taskID The task UID
    * @param type The predecessor type
    * @param lag The lag duration
    * @return A new link to be added to the MSPDI file
    */
   private Project.Tasks.Task.PredecessorLink writePredecessor (ObjectFactory factory, Integer taskID, RelationType type, Duration lag)
   {
      Project.Tasks.Task.PredecessorLink link = factory.createProjectTasksTaskPredecessorLink();

      link.setPredecessorUID (NumberUtility.getBigInteger(taskID));
      link.setType (BigInteger.valueOf(type.getValue()));

      if (lag != null && lag.getDuration() != 0)
      {
         link.setLinkLag(BigInteger.valueOf((long)DatatypeConverter.printDurationInMinutes(lag)*10));
         link.setLagFormat(DatatypeConverter.printDurationTimeUnits (lag.getUnits()));
      }

      return (link);
   }


   /**
    * This method writes assignment data to an MSPDI file.
    *
    * @param factory ObjectFactory instance
    * @param project Root node of the MSPDI file
    */
   private void writeAssignments (ObjectFactory factory, Project project)
   {
      int uid = 0;
      Project.Assignments assignments = factory.createProjectAssignments();
      project.setAssignments(assignments);
      List<Project.Assignments.Assignment> list = assignments.getAssignment();
      
      for (ResourceAssignment assignment : m_projectFile.getAllResourceAssignments())
      {
         list.add(writeAssignment (factory, assignment, uid));
         ++uid;
      }

      //
      // Check to see if we have any tasks that have a percent complete value
      // but do not have resource assignments. If any exist, then we must
      // write a dummy resource assignment record to ensure that the MSPDI
      // file shows the correct percent complete amount for the task.
      //
      for (Task task : m_projectFile.getAllTasks())
      {
         double percentComplete = NumberUtility.getDouble(task.getPercentageComplete());
         if (percentComplete != 0 && task.getResourceAssignments().isEmpty() == true)
         {
            ResourceAssignment dummy = m_projectFile.newResourceAssignment (task);
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

            list.add(writeAssignment (factory, dummy, uid));
            ++uid;
         }
      }
   }


   /**
    * This method writes data for a single assignment to an MSPDI file.
    *
    * @param factory ObjectFactory instance
    * @param mpx Resource assignment data
    * @param uid Unique ID for the new assignment
    * @return New MSPDI assignment instance
    */
   private Project.Assignments.Assignment writeAssignment (ObjectFactory factory, ResourceAssignment mpx, int uid)
   {
      Project.Assignments.Assignment xml = factory.createProjectAssignmentsAssignment();

      xml.setActualCost(DatatypeConverter.printCurrency (mpx.getActualCost()));
      xml.setActualWork(DatatypeConverter.printDuration (this, mpx.getActualWork()));
      xml.setCost(DatatypeConverter.printCurrency (mpx.getCost()));
      xml.setDelay(BigInteger.valueOf((long)DatatypeConverter.printDurationInMinutes(mpx.getDelay())*1000));
      xml.setFinish(DatatypeConverter.printDate(mpx.getFinish()));
      xml.setOvertimeWork(DatatypeConverter.printDuration(this, mpx.getOvertimeWork()));
      xml.setRemainingWork(DatatypeConverter.printDuration (this, mpx.getRemainingWork()));
      xml.setResourceUID(BigInteger.valueOf(NumberUtility.getInt(mpx.getResourceUniqueID())));
      xml.setStart(DatatypeConverter.printDate (mpx.getStart()));
      xml.setTaskUID(NumberUtility.getBigInteger(mpx.getTask().getUniqueID()));
      xml.setUID(BigInteger.valueOf(uid));
      xml.setUnits(DatatypeConverter.printUnits(mpx.getUnits()));
      xml.setWork(DatatypeConverter.printDuration (this, mpx.getWork()));
      xml.setWorkContour(mpx.getWorkContour());
      return (xml);
   }

   /**
    * Package-private accessor method used to retrieve the project file
    * currently being processed by this writer.
    *
    * @return project file instance
    */
   ProjectFile getProjectFile ()
   {
      return (m_projectFile);
   }

   private ProjectFile m_projectFile;
   
   private static final BigInteger BIGINTEGER_ZERO = BigInteger.valueOf(0);
   
   private static final Integer NULL_RESOURCE_ID = Integer.valueOf(-65535);   
}
