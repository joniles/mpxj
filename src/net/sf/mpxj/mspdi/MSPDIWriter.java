/*
 * file:       MSPDIWriter.java
 * author:     Jon Iles
 * copyright:  (c) Tapster Rock Limited 2005
 * date:       Dec 30, 2005
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
import java.util.Date;
import java.util.Iterator;
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
import net.sf.mpxj.FieldConstants;
import net.sf.mpxj.FieldType;
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
import net.sf.mpxj.ResourceFieldConstants;
import net.sf.mpxj.ScheduleFrom;
import net.sf.mpxj.Task;
import net.sf.mpxj.TaskField;
import net.sf.mpxj.TaskFieldConstants;
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

         if (m_compatibleOutput == true)
         {
            stream = new CompatabilityOutputStream (stream);
         }

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

      project.setActualsInSync(header.getActualsInSync());
      project.setAdminProject(header.getAdminProject());
      project.setAuthor(header.getAuthor());
      project.setAutoAddNewResourcesAndTasks(header.getAutoAddNewResourcesAndTasks());
      project.setAutolink(header.getAutolink());
      project.setBaselineForEarnedValue(NumberUtility.getBigInteger(header.getBaselineForEarnedValue()));
      project.setCalendarUID(BigInteger.ONE);
      project.setCategory(header.getCategory());
      project.setCompany(header.getCompany());
      project.setCreationDate(DatatypeConverter.printDate(header.getCreationDate()));
      project.setCriticalSlackLimit(NumberUtility.getBigInteger(header.getCriticalSlackLimit()));
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
      project.setEditableActualCosts(header.getEditableActualCosts());
      project.setExtendedCreationDate(DatatypeConverter.printDate(header.getExtendedCreationDate()));
      project.setFinishDate(DatatypeConverter.printDate(header.getFinishDate()));
      project.setFiscalYearStart(header.getFiscalYearStart());
      project.setFYStartDate(NumberUtility.getBigInteger(header.getFiscalYearStartMonth()));
      project.setHonorConstraints(header.getHonorConstraints());
      project.setInsertedProjectsLikeSummary(header.getInsertedProjectsLikeSummary());
      project.setLastSaved(DatatypeConverter.printDate(header.getLastSaved()));
      project.setManager(header.getManager());
      project.setMicrosoftProjectServerURL(header.getMicrosoftProjectServerURL());
      project.setMinutesPerDay(NumberUtility.getBigInteger(header.getMinutesPerDay()));
      project.setMinutesPerWeek(NumberUtility.getBigInteger(header.getMinutesPerWeek()));
      project.setMoveCompletedEndsBack(header.getMoveCompletedEndsBack());
      project.setMoveCompletedEndsForward(header.getMoveCompletedEndsForward());
      project.setMoveRemainingStartsBack(header.getMoveRemainingStartsBack());
      project.setMoveRemainingStartsForward(header.getMoveRemainingStartsForward());
      project.setMultipleCriticalPaths(header.getMultipleCriticalPaths());
      project.setName(header.getName());
      project.setNewTasksEffortDriven(header.getNewTasksEffortDriven());
      project.setNewTasksEstimated(header.getNewTasksEstimated());
      project.setNewTaskStartDate(header.getNewTaskStartIsProjectStart()==true?BigInteger.ZERO:BigInteger.ONE);
      project.setProjectExternallyEdited(header.getProjectExternallyEdited());
      project.setRemoveFileProperties(header.getRemoveFileProperties());
      project.setRevision(NumberUtility.getBigInteger(header.getRevision()));
      project.setScheduleFromStart(header.getScheduleFrom() == ScheduleFrom.START);
      project.setSplitsInProgressTasks(header.getSplitInProgressTasks());
      project.setSpreadActualCost(header.getSpreadActualCost());
      project.setSpreadPercentComplete(header.getSpreadPercentComplete());
      project.setStartDate(DatatypeConverter.printDate(header.getStartDate()));
      project.setStatusDate(DatatypeConverter.printDate(header.getStatusDate()));
      project.setSubject(header.getSubject());
      project.setTaskUpdatesResource(header.getUpdatingTaskStatusUpdatesResourceStatus());
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
    * @throws JAXBException
    */
   private void writeProjectExtendedAttributes (ObjectFactory factory, Project project)
      throws JAXBException
   {
      Project.ExtendedAttributesType attributes = factory.createProjectTypeExtendedAttributesType();
      project.setExtendedAttributes(attributes);
      List list = attributes.getExtendedAttribute();

      writeFieldAliases (factory, m_projectFile.getTaskFieldAliasMap(), TaskFieldConstants.TASK_FIELD_MPXJ_TO_PROJECT_MAP, TaskFieldConstants.TASK_FIELD_MPXJ_TO_NAME_MAP, list);
      writeFieldAliases (factory, m_projectFile.getResourceFieldAliasMap(), ResourceFieldConstants.RESOURCE_FIELD_MPXJ_TO_PROJECT_MAP, ResourceFieldConstants.RESOURCE_FIELD_MPXJ_TO_NAME_MAP, list);
   }

   /**
    * This method handles writing field alias data into the MSPDI file.
    *
    * @param factory object factory
    * @param fieldAliasMap map of MPX field numbers to their aliases
    * @param mpxXmlMap map of mpx field numbers to MSPDI field numbers
    * @param mpxNameMap map of mpx field names to MSPDI field numbers
    * @param list list of extended attributes
    * @throws JAXBException
    */
   private void writeFieldAliases (ObjectFactory factory, Map fieldAliasMap, Map mpxXmlMap, Map mpxNameMap, List list)
      throws JAXBException
   {
      Iterator iter = mpxNameMap.keySet().iterator();
      FieldType key;
      Integer fieldID;
      String name;
      String alias;

      while (iter.hasNext() == true)
      {
         key = (FieldType)iter.next();
         fieldID = (Integer)mpxXmlMap.get(key);
         name = (String)mpxNameMap.get(key);
         alias = (String)fieldAliasMap.get(key);

         Project.ExtendedAttributesType.ExtendedAttributeType attribute = factory.createProjectTypeExtendedAttributesTypeExtendedAttributeType();
         list.add(attribute);
         attribute.setFieldID(fieldID.toString());
         attribute.setFieldName(name);
         attribute.setAlias(alias);
      }
   }

   /**
    * This method writes calandar data to an MSPDI file.
    *
    * @param factory ObjectFactory instance
    * @param project Root node of the MSPDI file
    * @throws JAXBException on xml creation errors
    */
   private void writeCalendars (ObjectFactory factory, Project project)
      throws JAXBException
   {
      //
      // First step, find all of the base calendars and resource calendars,
      // add them to a list ready for processing, and create a map between
      // names and unique IDs
      //
      LinkedList calendarList = new LinkedList(m_projectFile.getBaseCalendars ());
      Iterator iter = m_projectFile.getAllResources().iterator();
      ProjectCalendar cal;

      while (iter.hasNext() == true)
      {
         cal = ((Resource)iter.next()).getResourceCalendar();
         if (cal != null)
         {
            calendarList.add(cal);
         }
      }


      //
      // Create the new MSPDI calendar list
      //
      Project.CalendarsType calendars = factory.createProjectTypeCalendarsType();
      project.setCalendars (calendars);
      List calendar = calendars.getCalendar();

      //
      // Process each calendar in turn
      //
      iter = calendarList.iterator();
      factory.createProjectTypeCalendarsTypeCalendarType();

      while (iter.hasNext() == true)
      {
         cal = (ProjectCalendar)iter.next();
         calendar.add (writeCalendar (factory, cal));
      }
   }

   /**
    * This method writes data for a single calandar to an MSPDI file.
    *
    * @param factory ObjectFactory instance
    * @param bc Base calendar data
    * @return New MSPDI calendar instance
    * @throws JAXBException on xml creation errors
    */
   private Project.CalendarsType.CalendarType writeCalendar (ObjectFactory factory, ProjectCalendar bc)
      throws JAXBException
   {
      //
      // Create a calendar
      //
      Project.CalendarsType.CalendarType calendar = factory.createProjectTypeCalendarsTypeCalendarType();
      calendar.setUID(NumberUtility.getBigInteger(bc.getUniqueID()));
      calendar.setIsBaseCalendar(bc.isBaseCalendar());

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
      Project.CalendarsType.CalendarType.WeekDaysType days = factory.createProjectTypeCalendarsTypeCalendarTypeWeekDaysType();
      Project.CalendarsType.CalendarType.WeekDaysType.WeekDayType.WorkingTimesType times;
      Project.CalendarsType.CalendarType.WeekDaysType.WeekDayType.WorkingTimesType.WorkingTimeType time;
      ProjectCalendarHours bch;
      List timesList;

      calendar.setWeekDays (days);
      List dayList = days.getWeekDay();

      Project.CalendarsType.CalendarType.WeekDaysType.WeekDayType day;
      int loop;
      int workingFlag;
      DateRange range;
      Iterator rangeIter;

      for (loop=1; loop < 8; loop++)
      {
         workingFlag = bc.getWorkingDay(Day.getInstance(loop));

         if (workingFlag != ProjectCalendar.DEFAULT)
         {
            day = factory.createProjectTypeCalendarsTypeCalendarTypeWeekDaysTypeWeekDayType();
            dayList.add(day);
            day.setDayType(BigInteger.valueOf(loop));
            day.setDayWorking(workingFlag == ProjectCalendar.WORKING);

            if (workingFlag == ProjectCalendar.WORKING)
            {
               times = factory.createProjectTypeCalendarsTypeCalendarTypeWeekDaysTypeWeekDayTypeWorkingTimesType ();
               day.setWorkingTimes(times);
               timesList = times.getWorkingTime();

               bch = bc.getCalendarHours (Day.getInstance(loop));
               if (bch != null)
               {
                  rangeIter = bch.iterator();
                  while (rangeIter.hasNext() == true)
                  {
                     range = (DateRange)rangeIter.next();
                     if (range != null)
                     {
                        time = factory.createProjectTypeCalendarsTypeCalendarTypeWeekDaysTypeWeekDayTypeWorkingTimesTypeWorkingTimeType ();
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
      List exceptions = bc.getCalendarExceptions ();
      Iterator iter = exceptions.iterator();
      ProjectCalendarException exception;
      Project.CalendarsType.CalendarType.WeekDaysType.WeekDayType.TimePeriodType period;
      boolean working;

      while (iter.hasNext() == true)
      {
         exception = (ProjectCalendarException)iter.next();
         working = exception.getWorking();

         day = factory.createProjectTypeCalendarsTypeCalendarTypeWeekDaysTypeWeekDayType();
         dayList.add(day);
         day.setDayType(BIGINTEGER_ZERO);
         day.setDayWorking(working);

         period = factory.createProjectTypeCalendarsTypeCalendarTypeWeekDaysTypeWeekDayTypeTimePeriodType();
         day.setTimePeriod(period);
         period.setFromDate(DatatypeConverter.printDate(exception.getFromDate()));
         period.setToDate(DatatypeConverter.printDate (exception.getToDate()));

         if (working == true)
         {
            times = factory.createProjectTypeCalendarsTypeCalendarTypeWeekDaysTypeWeekDayTypeWorkingTimesType ();
            day.setWorkingTimes(times);
            timesList = times.getWorkingTime();

            time = factory.createProjectTypeCalendarsTypeCalendarTypeWeekDaysTypeWeekDayTypeWorkingTimesTypeWorkingTimeType ();
            timesList.add (time);

            time.setFromTime(DatatypeConverter.printTime(exception.getFromTime1()));
            time.setToTime(DatatypeConverter.printTime(exception.getToTime1()));

            time = factory.createProjectTypeCalendarsTypeCalendarTypeWeekDaysTypeWeekDayTypeWorkingTimesTypeWorkingTimeType ();
            timesList.add (time);

            time.setFromTime(DatatypeConverter.printTime(exception.getFromTime2()));
            time.setToTime(DatatypeConverter.printTime(exception.getToTime2()));

            time = factory.createProjectTypeCalendarsTypeCalendarTypeWeekDaysTypeWeekDayTypeWorkingTimesTypeWorkingTimeType ();
            timesList.add (time);

            time.setFromTime(DatatypeConverter.printTime(exception.getFromTime3()));
            time.setToTime(DatatypeConverter.printTime(exception.getToTime3()));
         }
      }

      return (calendar);
   }

   /**
    * This method writes resource data to an MSPDI file.
    *
    * @param factory ObjectFactory instance
    * @param project Root node of the MSPDI file
    * @throws JAXBException on xml creation errors
    */
   private void writeResources (ObjectFactory factory, Project project)
      throws JAXBException
   {
      Project.ResourcesType resources = factory.createProjectTypeResourcesType();
      project.setResources(resources);
      List list = resources.getResource();

      Iterator iter = m_projectFile.getAllResources().iterator();
      while (iter.hasNext() == true)
      {
         list.add (writeResource (factory, (Resource)iter.next()));
      }
   }

   /**
    * This method writes data for a single resource to an MSPDI file.
    *
    * @param factory ObjectFactory instance
    * @param mpx Resource data
    * @return New MSPDI resource instance
    * @throws JAXBException on xml creation errors
    */
   private Project.ResourcesType.ResourceType writeResource (ObjectFactory factory, Resource mpx)
      throws JAXBException
   {
      Project.ResourcesType.ResourceType xml = factory.createProjectTypeResourcesTypeResourceType();
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
      xml.setCanLevel(mpx.getCanLevel());
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
      xml.setIsEnterprise(mpx.getEnterprise());
      xml.setIsGeneric(mpx.getGeneric());
      xml.setIsInactive(mpx.getInactive());
      xml.setIsNull(mpx.getNull());
      xml.setMaterialLabel(mpx.getMaterialLabel());
      xml.setMaxUnits(DatatypeConverter.printUnits(mpx.getMaxUnits()));
      xml.setName(mpx.getName());
      xml.setNotes(mpx.getNotes());
      xml.setNTAccount(mpx.getNtAccount());
      xml.setOverAllocated(mpx.getOverAllocated());
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

      return (xml);
   }

   /**
    * This method writes extended attribute data for a resource.
    *
    * @param factory JAXB object factory
    * @param xml MSPDI resource
    * @param mpx MPXJ resource
    * @throws JAXBException
    */
   private void writeResourceExtendedAttributes (ObjectFactory factory, Project.ResourcesType.ResourceType xml, Resource mpx)
      throws JAXBException
   {
      Project.ResourcesType.ResourceType.ExtendedAttributeType attrib;
      List extendedAttributes = xml.getExtendedAttribute();
      Object value;
      ResourceField mpxFieldID;
      Integer xmlFieldID;

      for (int loop=0; loop < ResourceFieldConstants.RESOURCE_DATA.length; loop++)
      {
         mpxFieldID = (ResourceField)ResourceFieldConstants.RESOURCE_DATA[loop][FieldConstants.MPXJ_FIELD_ID];
         value = mpx.get(mpxFieldID);

         if (value != null)
         {
            xmlFieldID = (Integer)ResourceFieldConstants.RESOURCE_DATA[loop][FieldConstants.PROJECT_FIELD_ID];

            attrib = factory.createProjectTypeResourcesTypeResourceTypeExtendedAttributeType();
            extendedAttributes.add(attrib);
            attrib.setUID(BigInteger.valueOf(loop+1));
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
    * @throws JAXBException on xml creation errors
    */
   private void writeTasks (ObjectFactory factory, Project project)
      throws JAXBException
   {
      Project.TasksType tasks = factory.createProjectTypeTasksType();
      project.setTasks (tasks);
      List list = tasks.getTask();

      Iterator iter = m_projectFile.getAllTasks().iterator();
      while (iter.hasNext() == true)
      {
         list.add (writeTask (factory, (Task)iter.next()));
      }
   }


   /**
    * This method writes data for a single task to an MSPDI file.
    *
    * @param factory ObjectFactory instance
    * @param mpx Task data
    * @return new task instance
    * @throws JAXBException on xml creation errors
    */
   private Project.TasksType.TaskType writeTask (ObjectFactory factory, Task mpx)
      throws JAXBException
   {
      Project.TasksType.TaskType xml = factory.createProjectTypeTasksTypeTaskType();

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
      xml.setCritical(mpx.getCritical());
      xml.setCV(DatatypeConverter.printCurrency(mpx.getCV()));
      xml.setDeadline(DatatypeConverter.printDate(mpx.getDeadline()));
      xml.setDuration(DatatypeConverter.printDuration(this, mpx.getDuration()));
      xml.setDurationFormat(DatatypeConverter.printDurationTimeUnits(mpx.getDurationFormat()));
      xml.setDurationFormat(DatatypeConverter.printDurationTimeUnits(mpx.getDuration()));
      xml.setEarlyFinish(DatatypeConverter.printDate(mpx.getEarlyFinish()));
      xml.setEarlyStart(DatatypeConverter.printDate(mpx.getEarlyStart()));
      xml.setEarnedValueMethod(DatatypeConverter.printEarnedValueMethod(mpx.getEarnedValueMethod()));
      xml.setEffortDriven(mpx.getEffortDriven());
      xml.setEstimated(mpx.getEstimated());
      xml.setExternalTask(mpx.getExternalTask());
      xml.setExternalTaskProject(mpx.getExternalTaskProject());

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

      xml.setFreeSlack(BigInteger.valueOf((long)DatatypeConverter.printDurationInMinutes(mpx.getFreeSlack())*1000));
      xml.setHideBar(mpx.getHideBar());
      xml.setIsNull(mpx.getNull());
      xml.setIsSubproject(mpx.getSubProject()!=null);
      xml.setIsSubprojectReadOnly(mpx.getSubprojectReadOnly());
      xml.setHyperlink(mpx.getHyperlink());
      xml.setHyperlinkAddress(mpx.getHyperlinkAddress());
      xml.setHyperlinkSubAddress(mpx.getHyperlinkSubAddress());
      xml.setID(NumberUtility.getBigInteger(mpx.getID()));
      xml.setIgnoreResourceCalendar(mpx.getIgnoreResourceCalendar());
      xml.setLateFinish(DatatypeConverter.printDate(mpx.getLateFinish()));
      xml.setLateStart(DatatypeConverter.printDate(mpx.getLateStart()));
      xml.setLevelAssignments(mpx.getLevelAssignments());
      xml.setLevelingCanSplit(mpx.getLevelingCanSplit());

      if (mpx.getLevelingDelay() != null)
      {
         xml.setLevelingDelay(BigInteger.valueOf((long)mpx.getLevelingDelay().getDuration()));
         xml.setLevelingDelayFormat(DatatypeConverter.printDurationTimeUnits(mpx.getLevelingDelayFormat()));
      }

      xml.setMilestone(mpx.getMilestone());
      xml.setName(mpx.getName());
      xml.setNotes(mpx.getNotes());
      xml.setOutlineLevel(NumberUtility.getBigInteger(mpx.getOutlineLevel()));
      xml.setOutlineNumber(mpx.getOutlineNumber());
      xml.setOverAllocated(mpx.getOverAllocated());
      xml.setOvertimeCost(DatatypeConverter.printCurrency(mpx.getOvertimeCost()));
      xml.setOvertimeWork(DatatypeConverter.printDuration(this, mpx.getOvertimeWork()));
      xml.setPercentComplete(NumberUtility.getBigInteger(mpx.getPercentageComplete()));
      xml.setPercentWorkComplete(NumberUtility.getBigInteger(mpx.getPercentageWorkComplete()));
      xml.setPhysicalPercentComplete(NumberUtility.getBigInteger(mpx.getPhysicalPercentComplete()));
      xml.setPriority(DatatypeConverter.printPriority(mpx.getPriority()));
      xml.setRecurring(mpx.getRecurring());
      xml.setRegularWork(DatatypeConverter.printDuration(this, mpx.getRegularWork()));
      xml.setRemainingCost(DatatypeConverter.printCurrency(mpx.getRemainingCost()));

      if (m_compatibleOutput == true && mpx.getRemainingDuration() == null)
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
      xml.setResumeValid(mpx.getResumeValid());
      xml.setRollup(mpx.getRollup());
      xml.setStart(DatatypeConverter.printDate(mpx.getStart()));
      xml.setStartVariance(BigInteger.valueOf((long)DatatypeConverter.printDurationInMinutes(mpx.getStartVariance())*1000));
      xml.setStop(DatatypeConverter.printDate (mpx.getStop()));
      xml.setSubprojectName(mpx.getSubprojectName());
      xml.setSummary(mpx.getSummary());
      xml.setTotalSlack(BigInteger.valueOf((long)DatatypeConverter.printDurationInMinutes(mpx.getTotalSlack())*1000));
      xml.setType(mpx.getType());
      xml.setUID(mpx.getUniqueID());
      xml.setWBS(mpx.getWBS());
      xml.setWBSLevel(mpx.getWBSLevel());
      xml.setWork(DatatypeConverter.printDuration(this, mpx.getWork()));
      xml.setWorkVariance(new BigDecimal(DatatypeConverter.printDurationInMinutes(mpx.getWorkVariance())*1000));

      writePredecessors (factory, xml, mpx);

      writeTaskExtendedAttributes (factory, xml, mpx);

      return (xml);
   }

   /**
    * This method writes extended attribute data for a task.
    *
    * @param factory JAXB object factory
    * @param xml MSPDI task
    * @param mpx MPXJ task
    * @throws JAXBException
    */
   private void writeTaskExtendedAttributes (ObjectFactory factory, Project.TasksType.TaskType xml, Task mpx)
      throws JAXBException
   {
      Project.TasksType.TaskType.ExtendedAttributeType attrib;
      List extendedAttributes = xml.getExtendedAttribute();
      Object value;
      TaskField mpxFieldID;
      Integer xmlFieldID;

      for (int loop=0; loop < TaskFieldConstants.TASK_DATA.length; loop++)
      {
         mpxFieldID = (TaskField)TaskFieldConstants.TASK_DATA[loop][FieldConstants.MPXJ_FIELD_ID];
         value = mpx.get(mpxFieldID);

         if (value != null)
         {
            xmlFieldID = (Integer)TaskFieldConstants.TASK_DATA[loop][FieldConstants.PROJECT_FIELD_ID];

            attrib = factory.createProjectTypeTasksTypeTaskTypeExtendedAttributeType();
            extendedAttributes.add(attrib);
            attrib.setUID(BigInteger.valueOf(loop+1));
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
    * @throws JAXBException on xml creation errors
    */
   private void writePredecessors (ObjectFactory factory, Project.TasksType.TaskType xml, Task mpx)
      throws JAXBException
   {
      TreeSet set = new TreeSet ();
      Integer taskID;
      Relation rel;
      List list = xml.getPredecessorLink();
      Iterator iter;

      //
      // Process the list of predecessors specified by Unique ID
      //
      List predecessors = mpx.getUniqueIDPredecessors();
      if (predecessors != null)
      {
         iter = predecessors.iterator();
         while (iter.hasNext() == true)
         {
            rel = (Relation)iter.next();
            taskID = rel.getTaskID();
            set.add(taskID);
            list.add (writePredecessor (factory, taskID, rel.getType(), rel.getDuration()));
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
         Task task;
         iter = predecessors.iterator();
         while (iter.hasNext() == true)
         {
            rel = (Relation)iter.next();
            task = m_projectFile.getTaskByID(rel.getTaskID());
            if (task != null)
            {
               taskID = task.getUniqueID();
               if (set.contains(taskID) == false)
               {
                  list.add (writePredecessor (factory, taskID, rel.getType(), rel.getDuration()));
               }
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
    * @throws JAXBException on xml creation errors
    */
   private Project.TasksType.TaskType.PredecessorLinkType writePredecessor (ObjectFactory factory, Integer taskID, RelationType type, Duration lag)
      throws JAXBException
   {
      Project.TasksType.TaskType.PredecessorLinkType link = factory.createProjectTypeTasksTypeTaskTypePredecessorLinkType();

      link.setPredecessorUID (NumberUtility.getBigInteger(taskID));
      link.setType (BigInteger.valueOf(type.getType()));

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
    * @throws JAXBException on xml creation errors
    */
   private void writeAssignments (ObjectFactory factory, Project project)
      throws JAXBException
   {
      int uid = 0;
      Project.AssignmentsType assignments = factory.createProjectTypeAssignmentsType();
      project.setAssignments(assignments);
      List list = assignments.getAssignment();
      Iterator iter = m_projectFile.getAllResourceAssignments().iterator();
      while (iter.hasNext() == true)
      {
         list.add(writeAssignment (factory, (ResourceAssignment)iter.next(), uid));
         ++uid;
      }

      //
      // Check to see if we have any tasks that have a percent complete value
      // but do not have resource assignments. If any exist, then we must
      // write a dummy resource assignment record to ensure that the MSPDI
      // file shows the correct percent complete amount for the task.
      //
      iter = m_projectFile.getAllTasks().iterator();

      while (iter.hasNext() == true)
      {
         Task task = (Task)iter.next();
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

            dummy.setResourceUniqueID(ResourceFieldConstants.NULL_RESOURCE_ID);
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
    * @throws JAXBException on xml creation errors
    */
   private Project.AssignmentsType.AssignmentType writeAssignment (ObjectFactory factory, ResourceAssignment mpx, int uid)
      throws JAXBException
   {
      Project.AssignmentsType.AssignmentType xml = factory.createProjectTypeAssignmentsTypeAssignmentType();

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
    * This method is used to set a flag that determines whether
    * XML generated by this class is adjusted to be compatible with
    * Microsoft Project 2002.
    *
    * @param flag Compatibility flag
    */
   public void setMicrosoftProjectCompatibleOutput (boolean flag)
   {
      m_compatibleOutput = flag;
   }

   /**
    * This method retrieves a flag indicating whether the XML
    * output by this clas is compatible with Microsoft Project 2002.
    *
    * @return Boolean flag
    */
   public boolean getMicrosoftProjectCompatibleOutput ()
   {
      return (m_compatibleOutput);
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

   private boolean m_compatibleOutput = true;
   private ProjectFile m_projectFile;

   /**
    * This class is used to work around a number of problems with
    * Microsoft's XML implementation as used in Microsoft Project 2002.
    * Essentially this class implements a very simple find and replace
    * mechanism, allowing the output stream to be filtered on the fly
    * to change the contents.
    */
   private class CompatabilityOutputStream extends OutputStream
   {
      /**
       * Constructor. Takes the original output stream as an argument.
       *
       * @param parent Original output stream.
       */
      public CompatabilityOutputStream (OutputStream parent)
      {
         m_parent = parent;
         int max = 0;
         for (int loop=0; loop < m_find.length; loop++)
         {
            if (m_find[loop].length > max)
            {
               max = m_find[loop].length;
            }
         }
         m_buffer = new byte[max];
      }

      /**
       * This method writes a byte to the output stream. All of
       * the find and replace filtering takes place in this method.
       *
       * @param b Input byte
       * @throws IOException on write error
       */
      public void write (int b)
         throws IOException
      {
         if (m_match == -1)
         {
            for (int loop=0; loop < m_find.length; loop++)
            {
               if (b == m_find[loop][0])
               {
                  m_match = loop;
                  break;
               }
            }

            if (m_match != -1)
            {
               m_buffer[0] = (byte)b;
               m_index = 1;
            }
            else
            {
               m_parent.write (b);
            }
         }
         else
         {
            int find = m_find[m_match][m_index];

            if ((m_match > 0 && find == '?') || b == find)
            {
               m_buffer[m_index] = (byte)b;
               ++m_index;
               if (m_index == m_find[m_match].length)
               {
                  if (m_replace[m_match] != null)
                  {
                     m_parent.write(m_replace[m_match].getBytes());
                  }
                  m_match = -1;
               }
            }
            else
            {
               m_match = -1;
               m_parent.write(m_buffer, 0, m_index);
               write (b);
            }
         }
      }

      /**
       * This method passes the call on to the original output stream.
       *
       * @throws IOException on write error
       */
      public void flush()
         throws IOException
      {
         m_parent.flush();
      }

      /**
       * This method passes the call on to the original output stream.
       *
       * @throws IOException on write error
       */
      public void close()
         throws IOException
      {
         m_parent.close ();
      }

      private OutputStream m_parent;
      private int m_match = -1;
      private byte[] m_buffer;
      private int m_index;

      private byte[][] m_find =
      {
         "ns1:".getBytes(),
         ":ns1".getBytes(),
         ".000".getBytes(),
         "+??:??<".getBytes(),
         "-??:??<".getBytes(),
         "true<".getBytes(),
         "false<".getBytes(),
         ">0.0<".getBytes()
      };

      private String[] m_replace =
      {
         null,
         null,
         null,
         "<",
         "<",
         "1<",
         "0<",
         ">0<"
      };
   }

   private static final BigInteger BIGINTEGER_ZERO = BigInteger.valueOf(0);
}
