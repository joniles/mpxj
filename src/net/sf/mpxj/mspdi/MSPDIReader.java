/*
 * file:       MSPDIReader.java
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
import java.io.InputStream;
import java.math.BigInteger;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.ValidationEvent;
import javax.xml.bind.ValidationEventHandler;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import net.sf.mpxj.DateRange;
import net.sf.mpxj.Day;
import net.sf.mpxj.ProjectCalendar;
import net.sf.mpxj.ProjectCalendarException;
import net.sf.mpxj.ProjectCalendarHours;
import net.sf.mpxj.Duration;
import net.sf.mpxj.MPXJException;
import net.sf.mpxj.ProjectFile;
import net.sf.mpxj.ProjectHeader;
import net.sf.mpxj.Relation;
import net.sf.mpxj.RelationType;
import net.sf.mpxj.Resource;
import net.sf.mpxj.ResourceAssignment;
import net.sf.mpxj.ScheduleFrom;
import net.sf.mpxj.SubProject;
import net.sf.mpxj.Task;
import net.sf.mpxj.TaskType;
import net.sf.mpxj.TimeUnit;
import net.sf.mpxj.mspdi.schema.Project;
import net.sf.mpxj.reader.AbstractProjectReader;
import net.sf.mpxj.utility.NumberUtility;
import net.sf.mpxj.utility.Pair;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;


/**
 * This class creates a new MPXFile instance by reading an MSPDI file.
 */
public final class MSPDIReader extends AbstractProjectReader
{
   /**
    * {@inheritDoc}
    */
   public ProjectFile read (InputStream stream)
      throws MPXJException
   {
      try
      {
         m_projectFile = new ProjectFile ();
         
         DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
         dbf.setNamespaceAware(true);
         DocumentBuilder db = dbf.newDocumentBuilder();
         Document doc = db.parse(stream);

         //
         // If we are matching the behaviour of MS project, then we need to
         // remove empty element nodes to avoid schema validation problems.
         //
         if (m_compatibleInput == true)
         {
            removeEmptyElementNodes(doc, doc);
         }

         JAXBContext context = JAXBContext.newInstance ("net.sf.mpxj.mspdi.schema");
         Unmarshaller unmarshaller = context.createUnmarshaller ();

         //
         // If we are matching the behaviour of MS project, then we need to
         // ignore validation warnings.
         //
         if (m_compatibleInput == true)
         {
            unmarshaller.setEventHandler
            (
               new ValidationEventHandler()
               {
                  public boolean handleEvent (ValidationEvent event)
                  {
                     return (true);
                  }
               }
            );
         }

         Project project = (Project)unmarshaller.unmarshal (doc);
         
         HashMap calendarMap = new HashMap ();

         readProjectHeader (project);
         readProjectExtendedAttributes(project);
         readCalendars (project, calendarMap);
         readResources (project, calendarMap);
         readTasks (project);
         readAssignments (project);
         
         //
         // Ensure that the unique ID counters are correct
         //
         m_projectFile.updateUniqueCounters();         
         
         return (m_projectFile);
      }

      catch (ParserConfigurationException ex)
      {
         throw new MPXJException ("Failed to parse file", ex);
      }

      catch (JAXBException ex)
      {
         throw new MPXJException ("Failed to parse file", ex);
      }

      catch (SAXException ex)
      {
         throw new MPXJException ("Failed to parse file", ex);
      }

      catch (IOException ex)
      {
         throw new MPXJException ("Failed to parse file", ex);
      }
      
      finally
      {
         m_projectFile = null;
      }
   }

   /**
    * This method extracts project header data from an MSPDI file.
    *
    * @param project Root node of the MSPDI file
    */
   private void readProjectHeader (Project project)
   {
      ProjectHeader header = m_projectFile.getProjectHeader ();

      header.setActualsInSync(project.isActualsInSync());
      header.setAdminProject(project.isAdminProject());
      header.setAuthor(project.getAuthor());
      header.setAutoAddNewResourcesAndTasks(project.isAutoAddNewResourcesAndTasks());
      header.setAutolink(project.isAutolink());
      header.setBaselineForEarnedValue(NumberUtility.getInteger(project.getBaselineForEarnedValue()));
      header.setCategory(project.getCategory());
      header.setCompany(project.getCompany());
      header.setCreationDate(DatatypeConverter.parseDate(project.getCreationDate()));
      header.setCriticalSlackLimit(NumberUtility.getInteger(project.getCriticalSlackLimit()));
      header.setCurrencyDigits (NumberUtility.getInteger(project.getCurrencyDigits()));
      header.setCurrencySymbol (project.getCurrencySymbol());
      header.setCurrentDate(DatatypeConverter.parseDate (project.getCurrentDate()));
      header.setDaysPerMonth(NumberUtility.getInteger(project.getDaysPerMonth()));
      header.setDefaultDurationUnits(DatatypeConverter.parseDurationTimeUnits(project.getDurationFormat()));
      header.setDefaultEndTime(DatatypeConverter.parseTime(project.getDefaultFinishTime()));
      header.setDefaultFixedCostAccrual(project.getDefaultFixedCostAccrual());
      header.setDefaultOvertimeRate(DatatypeConverter.parseRate(project.getDefaultOvertimeRate()));
      header.setDefaultStandardRate(DatatypeConverter.parseRate(project.getDefaultStandardRate()));
      header.setDefaultStartTime(DatatypeConverter.parseTime(project.getDefaultStartTime()));
      header.setDefaultTaskEarnedValueMethod(DatatypeConverter.parseEarnedValueMethod(project.getDefaultTaskEVMethod()));
      header.setDefaultTaskType(project.getDefaultTaskType());
      header.setDefaultWorkUnits(DatatypeConverter.parseWorkUnits (project.getWorkFormat()));
      header.setEarnedValueMethod(DatatypeConverter.parseEarnedValueMethod(project.getEarnedValueMethod()));
      header.setEditableActualCosts(project.isEditableActualCosts());
      header.setExtendedCreationDate(DatatypeConverter.parseDate(project.getExtendedCreationDate()));
      header.setFinishDate(DatatypeConverter.parseDate (project.getFinishDate()));
      header.setFiscalYearStart(project.isFiscalYearStart());
      header.setFiscalYearStartMonth(NumberUtility.getInteger(project.getFYStartDate()));
      header.setHonorConstraints(project.isHonorConstraints());
      header.setInsertedProjectsLikeSummary(project.isInsertedProjectsLikeSummary());
      header.setLastSaved(DatatypeConverter.parseDate(project.getLastSaved()));
      header.setManager(project.getManager());
      header.setMicrosoftProjectServerURL(project.isMicrosoftProjectServerURL());
      header.setMinutesPerDay(NumberUtility.getInteger(project.getMinutesPerDay()));
      header.setMinutesPerWeek(NumberUtility.getInteger(project.getMinutesPerWeek()));
      header.setMoveCompletedEndsBack(project.isMoveCompletedEndsBack());
      header.setMoveCompletedEndsForward(project.isMoveCompletedEndsForward());
      header.setMoveRemainingStartsBack(project.isMoveRemainingStartsBack());
      header.setMoveRemainingStartsForward(project.isMoveRemainingStartsForward());
      header.setMultipleCriticalPaths(project.isMultipleCriticalPaths());
      header.setName(project.getName());
      header.setNewTasksEffortDriven(project.isNewTasksEffortDriven());
      header.setNewTasksEstimated(project.isNewTasksEstimated());
      header.setNewTaskStartIsProjectStart(NumberUtility.getInt(project.getNewTaskStartDate())==0);
      header.setProjectExternallyEdited(project.isProjectExternallyEdited());
      header.setProjectTitle(project.getTitle());
      header.setRemoveFileProperties(project.isRemoveFileProperties());
      header.setRevision(NumberUtility.getInteger(project.getRevision()));
      header.setScheduleFrom(project.isScheduleFromStart()?ScheduleFrom.START:ScheduleFrom.FINISH);
      header.setSubject(project.getSubject());
      header.setSplitInProgressTasks(project.isSplitsInProgressTasks());
      header.setSpreadActualCost(project.isSpreadActualCost());
      header.setSpreadPercentComplete(project.isSpreadPercentComplete());
      header.setStartDate(DatatypeConverter.parseDate (project.getStartDate()));
      header.setStatusDate(DatatypeConverter.parseDate(project.getStatusDate()));
      header.setSymbolPosition (project.getCurrencySymbolPosition());
      header.setUniqueID(project.getUID());
      header.setUpdatingTaskStatusUpdatesResourceStatus(project.isTaskUpdatesResource());
      header.setWeekStartDay(DatatypeConverter.parseDay(project.getWeekStartDay()));
      
   }

   /**
    * This method extracts calandar data from an MSPDI file.
    *
    * @param project Root node of the MSPDI file
    * @param map Map of calendar UIDs to names
    */
   private void readCalendars (Project project, HashMap map)
   {
      Project.CalendarsType calendars = project.getCalendars();
      if (calendars != null)
      {
         List calendar = calendars.getCalendar();
         Iterator iter = calendar.iterator();
         LinkedList baseCalendars = new LinkedList();
         
         while (iter.hasNext() == true)
         {
            readCalendar ((Project.CalendarsType.CalendarType)iter.next(), map, baseCalendars);
         }

         updateBaseCalendarNames (baseCalendars, map);
      }
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
   private static void updateBaseCalendarNames (List baseCalendars, HashMap map)
   {
      Iterator iter = baseCalendars.iterator();
      Pair pair;
      ProjectCalendar cal;
      BigInteger baseCalendarID;
      ProjectCalendar baseCal;
      
      while (iter.hasNext() == true)
      {
         pair = (Pair)iter.next();
         cal = (ProjectCalendar)pair.getFirst();
         baseCalendarID = (BigInteger)pair.getSecond();
         
         baseCal = (ProjectCalendar)map.get(baseCalendarID);
         if (baseCal != null)
         {
            cal.setBaseCalendar(baseCal);
         }
      }
   }

   /**
    * This method extracts data for a single calandar from an MSPDI file.
    *
    * @param calendar Calendar data
    * @param map Map of calendar UIDs to names
    * @param baseCalendars list of base calendars
    */
   private void readCalendar (Project.CalendarsType.CalendarType calendar, HashMap map, LinkedList baseCalendars)
   {
      ProjectCalendar bc;
      Iterator iter;

      if (calendar.isIsBaseCalendar() == true)
      {
         bc = m_projectFile.addBaseCalendar();
      }
      else
      {
         bc = m_projectFile.getResourceCalendar();
      }

      bc.setUniqueID(calendar.getUID().intValue());
      bc.setName(calendar.getName());
      BigInteger baseCalendarID = calendar.getBaseCalendarUID();
      if (baseCalendarID != null)
      {
         baseCalendars.add(new Pair(bc, baseCalendarID));
      }

      Project.CalendarsType.CalendarType.WeekDaysType days = calendar.getWeekDays();
      if (days != null)
      {
         List day = days.getWeekDay();
         iter = day.iterator();

         while (iter.hasNext() == true)
         {
            readDay (bc, (Project.CalendarsType.CalendarType.WeekDaysType.WeekDayType)iter.next());
         }
      }

      map.put (calendar.getUID(), bc);
   }


   /**
    * This method extracts data for a single day from an MSPDI file.
    *
    * @param calendar Calendar data
    * @param day Day data
    */
   private void readDay (ProjectCalendar calendar, Project.CalendarsType.CalendarType.WeekDaysType.WeekDayType day)
   {
      BigInteger dayType = day.getDayType();
      if (dayType != null)
      {
         if (dayType.intValue() == 0)
         {
            readExceptionDay (calendar, day);
         }
         else
         {
            readNormalDay (calendar, day);
         }
      }
   }

   /**
    * This method extracts data for a normal working day from an MSPDI file.
    *
    * @param calendar Calendar data
    * @param weekDay Day data
    */
   private void readNormalDay (ProjectCalendar calendar, Project.CalendarsType.CalendarType.WeekDaysType.WeekDayType weekDay)
   {
      int dayNumber = weekDay.getDayType().intValue();
      Day day = Day.getInstance(dayNumber);
      calendar.setWorkingDay(day, weekDay.isDayWorking());
      ProjectCalendarHours hours = calendar.addCalendarHours(day);

      Project.CalendarsType.CalendarType.WeekDaysType.WeekDayType.WorkingTimesType times = weekDay.getWorkingTimes();
      if (times != null)
      {
         Project.CalendarsType.CalendarType.WeekDaysType.WeekDayType.WorkingTimesType.WorkingTimeType period;
         List time = times.getWorkingTime();
         Iterator iter = time.iterator();
         Date startTime;
         Date endTime;
         while (iter.hasNext() == true)
         {
            period = (Project.CalendarsType.CalendarType.WeekDaysType.WeekDayType.WorkingTimesType.WorkingTimeType)iter.next();
            startTime = DatatypeConverter.parseTime(period.getFromTime());
            endTime = DatatypeConverter.parseTime(period.getToTime());
            
            if (startTime != null && endTime != null)
            {
               hours.addDateRange(new DateRange(startTime, endTime));
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
   private void readExceptionDay (ProjectCalendar calendar, Project.CalendarsType.CalendarType.WeekDaysType.WeekDayType day)
   {
      ProjectCalendarException exception = calendar.addCalendarException();

      Project.CalendarsType.CalendarType.WeekDaysType.WeekDayType.TimePeriodType timePeriod = day.getTimePeriod();
      exception.setFromDate(DatatypeConverter.parseDate(timePeriod.getFromDate()));
      exception.setToDate(DatatypeConverter.parseDate(timePeriod.getToDate()));

      Project.CalendarsType.CalendarType.WeekDaysType.WeekDayType.WorkingTimesType times = day.getWorkingTimes();
      if (times != null)
      {
         Project.CalendarsType.CalendarType.WeekDaysType.WeekDayType.WorkingTimesType.WorkingTimeType period;
         List time = times.getWorkingTime();
         Iterator iter = time.iterator();

         if (iter.hasNext() == true)
         {
            period = (Project.CalendarsType.CalendarType.WeekDaysType.WeekDayType.WorkingTimesType.WorkingTimeType)iter.next();
            exception.setFromTime1(DatatypeConverter.parseTime(period.getFromTime()));
            exception.setToTime1(DatatypeConverter.parseTime(period.getToTime()));
         }

         if (iter.hasNext() == true)
         {
            period = (Project.CalendarsType.CalendarType.WeekDaysType.WeekDayType.WorkingTimesType.WorkingTimeType)iter.next();
            exception.setFromTime2(DatatypeConverter.parseTime(period.getFromTime()));
            exception.setToTime2(DatatypeConverter.parseTime(period.getToTime()));
         }

         if (iter.hasNext() == true)
         {
            period = (Project.CalendarsType.CalendarType.WeekDaysType.WeekDayType.WorkingTimesType.WorkingTimeType)iter.next();
            exception.setFromTime3(DatatypeConverter.parseTime(period.getFromTime()));
            exception.setToTime3(DatatypeConverter.parseTime(period.getToTime()));
         }
      }
   }

   /**
    * This method extracts project extended attribute data from an MSPDI file.
    *
    * @param project Root node of the MSPDI file
    */
   private void readProjectExtendedAttributes (Project project)
   {
      Project.ExtendedAttributesType attributes = project.getExtendedAttributes();
      if (attributes != null)
      {
         List attribute = attributes.getExtendedAttribute();
         Iterator iter = attribute.iterator();

         while (iter.hasNext() == true)
         {
            readFieldAlias ((Project.ExtendedAttributesType.ExtendedAttributeType)iter.next());
         }
      }
   }

   /**
    * Read a single field alias from an extended attribute.
    *
    * @param attribute extended attribute
    */
   private void readFieldAlias (Project.ExtendedAttributesType.ExtendedAttributeType attribute)
   {
      String alias = attribute.getAlias();

      if (alias != null && alias.length() != 0)
      {
         Integer id = new Integer (attribute.getFieldID());
         int prefix = id.intValue() / 100000;

         switch (prefix)
         {
            case MSPDIConstants.TASK_FIELD_PREFIX:
            {
               Integer taskField = (Integer)MSPDIConstants.TASK_FIELD_XML_TO_MPX_MAP.get(id);
               if (taskField != null)
               {
                  m_projectFile.setTaskFieldAlias (taskField.intValue(), attribute.getAlias());
               }
               break;
            }

            case MSPDIConstants.RESOURCE_FIELD_PREFIX:
            {
               Integer resourceField = (Integer)MSPDIConstants.RESOURCE_FIELD_XML_TO_MPX_MAP.get(id);
               if (resourceField != null)
               {
                  m_projectFile.setResourceFieldAlias (resourceField.intValue(), attribute.getAlias());
               }
               break;
            }
         }
      }
   }

   /**
    * This method extracts resource data from an MSPDI file.
    *
    * @param project Root node of the MSPDI file
    * @param calendarMap Map of calendar UIDs to names
    */
   private void readResources (Project project, HashMap calendarMap)
   {
      Project.ResourcesType resources = project.getResources();
      if (resources != null)
      {
         List resource = resources.getResource();
         Iterator iter = resource.iterator();
         while (iter.hasNext() == true)
         {
            readResource ((Project.ResourcesType.ResourceType)iter.next(), calendarMap);
         }
      }
   }

   /**
    * This method extracts data for a single resource from an MSPDI file.
    *
    * @param xml Resource data
    * @param calendarMap Map of calendar UIDs to names
    */
   private void readResource (Project.ResourcesType.ResourceType xml, HashMap calendarMap)
   {
      Resource mpx = m_projectFile.addResource();
      
      mpx.setAccrueAt(xml.getAccrueAt());
      mpx.setActveDirectoryGUID(xml.getActiveDirectoryGUID());
      mpx.setActualCost(DatatypeConverter.parseCurrency(xml.getActualCost()));
      mpx.setActualOvertimeCost(DatatypeConverter.parseCurrency(xml.getActualOvertimeCost()));
      mpx.setActualOvertimeWork(DatatypeConverter.parseDuration(m_projectFile,null,xml.getActualOvertimeWork()));
      mpx.setActualOvertimeWorkProtected(DatatypeConverter.parseDuration(m_projectFile,null,xml.getActualOvertimeWorkProtected()));
      mpx.setActualWork(DatatypeConverter.parseDuration (m_projectFile,null,xml.getActualWork()));
      mpx.setActualWorkProtected(DatatypeConverter.parseDuration(m_projectFile,null,xml.getActualWorkProtected()));
      mpx.setACWP(DatatypeConverter.parseCurrency(xml.getACWP()));
      mpx.setAvailableFrom(DatatypeConverter.parseDate(xml.getAvailableFrom()));
      mpx.setAvailableTo(DatatypeConverter.parseDate(xml.getAvailableTo()));
      mpx.setBCWS(DatatypeConverter.parseCurrency(xml.getBCWS()));
      mpx.setBCWP(DatatypeConverter.parseCurrency(xml.getBCWP()));
      mpx.setBookingType(xml.getBookingType());
      //mpx.setBaseCalendar ();
      //mpx.setBaselineCost();
      //mpx.setBaselineWork();
      mpx.setCanLevel(xml.isCanLevel());
      mpx.setCode(xml.getCode());
      mpx.setCost(DatatypeConverter.parseCurrency(xml.getCost()));
      mpx.setCostPerUse(DatatypeConverter.parseCurrency(xml.getCostPerUse()));
      mpx.setCostVariance(DatatypeConverter.parseCurrency(xml.getCostVariance()));
      mpx.setCreationDate(DatatypeConverter.parseDate(xml.getCreationDate()));
      mpx.setCV(DatatypeConverter.parseCurrency(xml.getCV()));
      mpx.setEmailAddress(xml.getEmailAddress());
      mpx.setFinish(DatatypeConverter.parseDate(xml.getFinish()));
      mpx.setGroup(xml.getGroup());
      mpx.setHyperlink(xml.getHyperlink());
      mpx.setHyperlinkAddress(xml.getHyperlinkAddress());
      mpx.setHyperlinkSubAddress(xml.getHyperlinkSubAddress());
      mpx.setID(NumberUtility.getInteger(xml.getID()));
      mpx.setInitials(xml.getInitials());
      mpx.setIsEnterprise(xml.isIsEnterprise());
      mpx.setIsGeneric(xml.isIsGeneric());
      mpx.setIsInactive(xml.isIsInactive());
      mpx.setIsNull(xml.isIsNull());
      //mpx.setLinkedFields();
      mpx.setMaterialLabel(xml.getMaterialLabel());
      mpx.setMaxUnits(DatatypeConverter.parseUnits(xml.getMaxUnits()));
      mpx.setName(xml.getName());
      if (xml.getNotes() != null && xml.getNotes().length() != 0)
      {
         mpx.setNotes(xml.getNotes());
      }
      mpx.setNtAccount(xml.getNTAccount());
      //mpx.setObjects();
      mpx.setOverAllocated(xml.isOverAllocated());
      mpx.setOvertimeCost(DatatypeConverter.parseCurrency(xml.getOvertimeCost()));
      mpx.setOvertimeRate(DatatypeConverter.parseRate(xml.getOvertimeRate()));
      mpx.setOvertimeRateFormat(DatatypeConverter.parseTimeUnit(xml.getOvertimeRateFormat()));
      mpx.setOvertimeWork(DatatypeConverter.parseDuration (m_projectFile,null,xml.getOvertimeWork()));
      mpx.setPeakUnits(DatatypeConverter.parseUnits(xml.getPeakUnits()));
      mpx.setPercentWorkComplete(xml.getPercentWorkComplete());
      mpx.setPhonetics(xml.getPhonetics());
      mpx.setRegularWork(DatatypeConverter.parseDuration(m_projectFile,null,xml.getRegularWork()));
      mpx.setRemainingCost(DatatypeConverter.parseCurrency(xml.getRemainingCost()));
      mpx.setRemainingOvertimeCost(DatatypeConverter.parseCurrency(xml.getRemainingOvertimeCost()));
      mpx.setRemainingWork(DatatypeConverter.parseDuration (m_projectFile,null,xml.getRemainingWork()));
      mpx.setRemainingOvertimeWork(DatatypeConverter.parseDuration(m_projectFile,null,xml.getRemainingOvertimeWork()));
      mpx.setStandardRate(DatatypeConverter.parseRate(xml.getStandardRate()));
      mpx.setStandardRateFormat(DatatypeConverter.parseTimeUnit(xml.getStandardRateFormat()));
      mpx.setStart(DatatypeConverter.parseDate(xml.getStart()));
      mpx.setSV(DatatypeConverter.parseCurrency(xml.getSV()));
      mpx.setType(xml.getType());
      mpx.setUniqueID(NumberUtility.getInteger(xml.getUID()));
      mpx.setWork(DatatypeConverter.parseDuration (m_projectFile,null,xml.getWork()));
      mpx.setWorkGroup(xml.getWorkGroup());
      mpx.setWorkVariance(DatatypeConverter.parseDurationInMinutes(xml.getWorkVariance()));

      readResourceExtendedAttributes (xml, mpx);

      mpx.setResourceCalendar((ProjectCalendar)calendarMap.get(xml.getCalendarUID()));
      m_projectFile.fireResourceReadEvent(mpx);
   }

   /**
    * This method processes any extended attributes associated with a resource.
    *
    * @param xml MSPDI resource instance
    * @param mpx MPX resource instance
    */
   private void readResourceExtendedAttributes (Project.ResourcesType.ResourceType xml, Resource mpx)
   {
      List extendedAttributes = xml.getExtendedAttribute();
      Iterator iter = extendedAttributes.iterator();
      Project.ResourcesType.ResourceType.ExtendedAttributeType attrib;
      Integer xmlFieldID;
      Integer mpxFieldID;
      int dataType;

      while (iter.hasNext() == true)
      {
         attrib = (Project.ResourcesType.ResourceType.ExtendedAttributeType)iter.next();
         xmlFieldID = new Integer (attrib.getFieldID());
         mpxFieldID = (Integer)MSPDIConstants.RESOURCE_FIELD_XML_TO_MPX_MAP.get(xmlFieldID);
         dataType = ((Integer)MSPDIConstants.RESOURCE_FIELD_MPX_TO_TYPE_MAP.get(mpxFieldID)).intValue();
         DatatypeConverter.parseExtendedAttribute(m_projectFile, mpx, attrib.getValue(), mpxFieldID, dataType);         
      }
   }

   /**
    * This method extracts task data from an MSPDI file.
    *
    * @param project Root node of the MSPDI file
    */
   private void readTasks (Project project)
   {
      Project.TasksType tasks = project.getTasks();
      if (tasks != null)
      {
         List task = tasks.getTask();
         Iterator iter = task.iterator();
         while (iter.hasNext() == true)
         {
            readTask ((Project.TasksType.TaskType)iter.next());
         }

         iter = task.iterator();
         while (iter.hasNext() == true)
         {
            readPredecessors ((Project.TasksType.TaskType)iter.next());
         }
      }

      m_projectFile.updateStructure ();
   }


   /**
    * This method extracts data for a single task from an MSPDI file.
    *
    * @param xml Task data
    */
   private void readTask (Project.TasksType.TaskType xml)
   {
      Task mpx = m_projectFile.addTask ();

      mpx.setDurationFormat(DatatypeConverter.parseDurationTimeUnits(xml.getDurationFormat()));      
      mpx.setActualCost(DatatypeConverter.parseCurrency (xml.getActualCost()));
      mpx.setActualDuration(DatatypeConverter.parseDuration (m_projectFile,mpx.getDurationFormat(),xml.getActualDuration()));
      mpx.setActualFinish(DatatypeConverter.parseDate (xml.getActualFinish()));
      mpx.setActualOvertimeCost(DatatypeConverter.parseCurrency(xml.getActualOvertimeCost()));
      mpx.setActualOvertimeWork(DatatypeConverter.parseDuration (m_projectFile,mpx.getDurationFormat(),xml.getActualOvertimeWork()));
      mpx.setActualOvertimeWorkProtected(DatatypeConverter.parseDuration(m_projectFile,mpx.getDurationFormat(),xml.getActualOvertimeWorkProtected()));
      mpx.setActualStart(DatatypeConverter.parseDate (xml.getActualStart()));
      mpx.setActualWork(DatatypeConverter.parseDuration (m_projectFile,mpx.getDurationFormat(),xml.getActualWork()));
      mpx.setActualWorkProtected(DatatypeConverter.parseDuration(m_projectFile,mpx.getDurationFormat(),xml.getActualWorkProtected()));
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
      mpx.setConstraintDate(DatatypeConverter.parseDate(xml.getConstraintDate()));
      mpx.setConstraintType(DatatypeConverter.parseConstraintType(xml.getConstraintType()));
      mpx.setContact(xml.getContact());
      mpx.setCost(DatatypeConverter.parseCurrency(xml.getCost()));
      //mpx.setCost1();
      //mpx.setCost2();
      //mpx.setCost3();
      //mpx.setCostVariance();
      mpx.setCreateDate(DatatypeConverter.parseDate(xml.getCreateDate()));
      mpx.setCritical(xml.isCritical());
      mpx.setCV(DatatypeConverter.parseCurrency(xml.getCV()));
      mpx.setDeadline(DatatypeConverter.parseDate(xml.getDeadline()));
      //mpx.setDelay();
      mpx.setDuration(DatatypeConverter.parseDuration (m_projectFile,mpx.getDurationFormat(),xml.getDuration()));      
      //mpx.setDuration1();
      //mpx.setDuration2();
      //mpx.setDuration3();
      //mpx.setDurationVariance();
      mpx.setEarlyFinish(DatatypeConverter.parseDate(xml.getEarlyFinish()));
      mpx.setEarlyStart(DatatypeConverter.parseDate(xml.getEarlyStart()));
      mpx.setEarnedValueMethod(DatatypeConverter.parseEarnedValueMethod(xml.getEarnedValueMethod()));
      mpx.setEffortDriven(xml.isEffortDriven());
      mpx.setEstimated(xml.isEstimated());
      mpx.setExternalTask(xml.isExternalTask());
      mpx.setExternalTaskProject(xml.getExternalTaskProject());
      mpx.setFinish(DatatypeConverter.parseDate(xml.getFinish()));
      //mpx.setFinish1();
      //mpx.setFinish2();
      //mpx.setFinish3();
      //mpx.setFinish4();
      //mpx.setFinish5();
      mpx.setFinishVariance(DatatypeConverter.parseDurationInMinutes(xml.getFinishVariance()));
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
      mpx.setFreeSlack(DatatypeConverter.parseDurationInMinutes(xml.getFreeSlack()));
      mpx.setHideBar(xml.isHideBar());
      mpx.setHyperlink(xml.getHyperlink());
      mpx.setHyperlinkAddress(xml.getHyperlinkAddress());
      mpx.setHyperlinkSubAddress(xml.getHyperlinkSubAddress());
      mpx.setID(NumberUtility.getInteger(xml.getID()));
      mpx.setIgnoreResourceCalendar(xml.isIgnoreResourceCalendar());
      mpx.setLateFinish(DatatypeConverter.parseDate(xml.getLateFinish()));
      mpx.setLateStart(DatatypeConverter.parseDate(xml.getLateStart()));
      mpx.setLevelAssignments(xml.isLevelAssignments());
      mpx.setLevelingCanSplit(xml.isLevelingCanSplit());
      mpx.setLevelingDelayFormat(DatatypeConverter.parseDurationTimeUnits(xml.getLevelingDelayFormat()));
      if (xml.getLevelingDelay() != null && mpx.getLevelingDelayFormat() != null)
      {
         mpx.setLevelingDelay(Duration.getInstance (xml.getLevelingDelay().doubleValue(), mpx.getLevelingDelayFormat()));
      }


      //mpx.setLinkedFields();
      //mpx.setMarked();
      mpx.setMilestone(xml.isMilestone());
      mpx.setName(xml.getName());
      if (xml.getNotes() != null && xml.getNotes().length() != 0)
      {
         mpx.setNotes(xml.getNotes());
      }
      //mpx.setNumber1();
      //mpx.setNumber2();
      //mpx.setNumber3();
      //mpx.setNumber4();
      //mpx.setNumber5();
      //mpx.setObjects();
      mpx.setNull(xml.isIsNull());
      mpx.setOutlineLevel(NumberUtility.getInteger(xml.getOutlineLevel()));
      mpx.setOutlineNumber(xml.getOutlineNumber());
      mpx.setOverAllocated(xml.isOverAllocated());
      mpx.setOvertimeCost(DatatypeConverter.parseCurrency(xml.getOvertimeCost()));
      mpx.setOvertimeWork(DatatypeConverter.parseDuration(m_projectFile,mpx.getDurationFormat(),xml.getOvertimeWork()));
      mpx.setPercentageComplete(xml.getPercentComplete());
      mpx.setPercentageWorkComplete(xml.getPercentWorkComplete());
      mpx.setPhysicalPercentComplete(NumberUtility.getInteger(xml.getPhysicalPercentComplete()));
      mpx.setPreleveledFinish(DatatypeConverter.parseDate(xml.getPreLeveledFinish()));
      mpx.setPreleveledStart(DatatypeConverter.parseDate(xml.getPreLeveledStart()));
      mpx.setPriority(DatatypeConverter.parsePriority(xml.getPriority()));
      //mpx.setProject();
      mpx.setRecurring(xml.isRecurring());
      mpx.setRegularWork(DatatypeConverter.parseDuration(m_projectFile,mpx.getDurationFormat(),xml.getRegularWork()));
      mpx.setRemainingCost(DatatypeConverter.parseCurrency(xml.getRemainingCost()));
      mpx.setRemainingDuration(DatatypeConverter.parseDuration(m_projectFile,mpx.getDurationFormat(),xml.getRemainingDuration()));
      mpx.setRemainingOvertimeCost(DatatypeConverter.parseCurrency(xml.getRemainingOvertimeCost()));
      mpx.setRemainingOvertimeWork(DatatypeConverter.parseDuration (m_projectFile,mpx.getDurationFormat(),xml.getRemainingOvertimeWork()));
      mpx.setRemainingWork(DatatypeConverter.parseDuration (m_projectFile,mpx.getDurationFormat(),xml.getRemainingWork()));
      //mpx.setResourceGroup();
      //mpx.setResourceInitials();
      //mpx.setResourceNames();
      mpx.setResume(DatatypeConverter.parseDate(xml.getResume()));
      mpx.setResumeValid(xml.isResumeValid());
      //mpx.setResumeNoEarlierThan();
      mpx.setRollup(xml.isRollup());
      mpx.setStart(DatatypeConverter.parseDate(xml.getStart()));
      //mpx.setStart1();
      //mpx.setStart2();
      //mpx.setStart3();
      //mpx.setStart4();
      //mpx.setStart5();
      mpx.setStartVariance(DatatypeConverter.parseDurationInMinutes(xml.getStartVariance()));
      mpx.setStop(DatatypeConverter.parseDate(xml.getStop()));
      mpx.setSubProject(xml.isIsSubproject()?new SubProject():null);
      mpx.setSubprojectName(xml.getSubprojectName());
      mpx.setSubprojectReadOnly(xml.isIsSubprojectReadOnly());
      //mpx.setSuccessors();
      mpx.setSummary(xml.isSummary());
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
      mpx.setTotalSlack(DatatypeConverter.parseDurationInMinutes(xml.getTotalSlack()));
      mpx.setType(xml.getType());
      mpx.setUniqueID(NumberUtility.getInteger(xml.getUID()));
      //mpx.setUpdateNeeded();
      mpx.setWBS(xml.getWBS());
      mpx.setWBSLevel (xml.getWBSLevel());
      mpx.setWork(DatatypeConverter.parseDuration(m_projectFile,mpx.getDurationFormat(),xml.getWork()));
      mpx.setWorkVariance(Duration.getInstance (NumberUtility.getDouble(xml.getWorkVariance())/1000, TimeUnit.MINUTES));

      readTaskExtendedAttributes(xml, mpx);

      //
      // Set the MPX file fixed flag
      //
      mpx.setFixed(mpx.getType() == TaskType.FIXED_DURATION);
      
      m_projectFile.fireTaskReadEvent(mpx);
   }

   /**
    * This method processes any extended attributes associated with a task.
    *
    * @param xml MSPDI task instance
    * @param mpx MPX task instance
    */
   private void readTaskExtendedAttributes (Project.TasksType.TaskType xml, Task mpx)
   {
      List extendedAttributes = xml.getExtendedAttribute();
      Iterator iter = extendedAttributes.iterator();
      Project.TasksType.TaskType.ExtendedAttributeType attrib;
      Integer xmlFieldID;
      Integer mpxFieldID;
      int dataType;

      while (iter.hasNext() == true)
      {
         attrib = (Project.TasksType.TaskType.ExtendedAttributeType)iter.next();
         xmlFieldID = new Integer (attrib.getFieldID());
         mpxFieldID = (Integer)MSPDIConstants.TASK_FIELD_XML_TO_MPX_MAP.get(xmlFieldID);
         dataType = ((Integer)MSPDIConstants.TASK_FIELD_MPX_TO_TYPE_MAP.get(mpxFieldID)).intValue();
         DatatypeConverter.parseExtendedAttribute(m_projectFile, mpx, attrib.getValue(), mpxFieldID, dataType);         
      }
   }

   /**
    * This method is used to retrieve the calendar associated
    * with a task. If no calendar is associated with a task, this method
    * returns null.
    *
    * @param task MSPDI task
    * @return calendar instance
    */
   private ProjectCalendar getTaskCalendar (Project.TasksType.TaskType task)
   {
      ProjectCalendar calendar = null;

      BigInteger calendarID = task.getCalendarUID();
      if (calendarID != null)
      {
         calendar = m_projectFile.getBaseCalendarByUniqueID(calendarID.intValue());
      }

      return (calendar);
   }


   /**
    * This method extracts predecessor data from an MSPDI file.
    *
    * @param task Task data
    */
   private void readPredecessors (Project.TasksType.TaskType task)
   {
      Integer uid = task.getUID();
      if (uid != null)
      {
         Task currTask = m_projectFile.getTaskByUniqueID(uid);
         if (currTask != null)
         {
            List predecessors = task.getPredecessorLink();
            Iterator iter = predecessors.iterator();

            while (iter.hasNext() == true)
            {
               readPredecessor (currTask, (Project.TasksType.TaskType.PredecessorLinkType)iter.next());
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
   private void readPredecessor (Task currTask, Project.TasksType.TaskType.PredecessorLinkType link)
   {
      BigInteger uid = link.getPredecessorUID();
      if (uid != null)
      {
         Task prevTask = m_projectFile.getTaskByUniqueID(new Integer(uid.intValue()));
         if (prevTask != null)
         {
            RelationType type;
            if (link.getType() != null)
            {
               type = RelationType.getInstance(link.getType().intValue());
            }
            else
            {
               type = RelationType.FINISH_START;
            }

            int lag;

            if (link.getLinkLag() != null)
            {
               lag = link.getLinkLag().intValue() / (10*60);
            }
            else
            {
               lag = 0;
            }

            Relation rel = currTask.addPredecessor(prevTask);
            rel.setType(type);
            rel.setDuration(Duration.getInstance (lag, TimeUnit.HOURS));
         }
      }
   }

   /**
    * This method extracts assignment data from an MSPDI file.
    *
    * @param project Root node of the MSPDI file
    */
   private void readAssignments (Project project)
   {
      Project.AssignmentsType assignments = project.getAssignments();
      if (assignments != null)
      {
         List assignment = assignments.getAssignment();
         Iterator iter = assignment.iterator();
         while (iter.hasNext() == true)
         {
            readAssignment ((Project.AssignmentsType.AssignmentType)iter.next());
         }
      }
   }


   /**
    * This method extracts data for a single assignment from an MSPDI file.
    *
    * @param assignment Assignment data
    */
   private void readAssignment (Project.AssignmentsType.AssignmentType assignment)
   {
      BigInteger taskUID = assignment.getTaskUID();
      BigInteger resourceUID = assignment.getResourceUID();
      if (taskUID != null && resourceUID != null)
      {
         Task task = m_projectFile.getTaskByUniqueID(new Integer(taskUID.intValue()));
         Resource resource = m_projectFile.getResourceByUniqueID(new Integer(resourceUID.intValue()));

         if (task != null && resource != null)
         {
            ResourceAssignment mpx = task.addResourceAssignment(resource);

            mpx.setActualCost(DatatypeConverter.parseCurrency(assignment.getActualCost()));
            //assignment.getActualFinish()
            //assignment.getActualOvertimeCost()
            //assignment.getActualOvertimeWork()
            //assignment.getActualOvertimeWorkProtected()
            //assignment.getActualStart()
            mpx.setActualWork(DatatypeConverter.parseDuration(m_projectFile,TimeUnit.HOURS,assignment.getActualWork()));
            //assignment.getActualWorkProtected()
            //assignment.getACWP()
            //assignment.getBaseline()
            //assignment.getBCWP()
            //assignment.getBCWS()
            //assignment.getBookingType()
            mpx.setCost(DatatypeConverter.parseCurrency(assignment.getCost()));
            //assignment.getCostRateTable()
            //assignment.getCostVariance()
            //assignment.getCreationDate()
            //assignment.getCV()
            mpx.setDelay(DatatypeConverter.parseDurationInMinutes(assignment.getDelay()));
            //assignment.getExtendedAttribute()
            mpx.setFinish(DatatypeConverter.parseDate(assignment.getFinish()));
            //assignment.getFinishVariance()
            //assignment.getHyperlink()
            //assignment.getHyperlinkAddress()
            //assignment.getHyperlinkSubAddress()
            //assignment.getLevelingDelay()
            //assignment.getLevelingDelayFormat()
            //assignment.getNotes()
            //assignment.getOvertimeCost()
            mpx.setOvertimeWork(DatatypeConverter.parseDuration(m_projectFile,TimeUnit.HOURS,assignment.getOvertimeWork()));
            //assignment.getPercentWorkComplete()
            //mpx.setPlannedCost();
            //mpx.setPlannedWork();
            //assignment.getRegularWork()
            //assignment.getRemainingCost()
            //assignment.getRemainingOvertimeCost()
            //assignment.getRemainingOvertimeWork()
            mpx.setRemainingWork(DatatypeConverter.parseDuration(m_projectFile,TimeUnit.HOURS,assignment.getRemainingWork()));
            //assignment.getResume()
            mpx.setStart(DatatypeConverter.parseDate(assignment.getStart()));
            //assignment.getStartVariance()
            //assignment.getStop()
            //assignment.getTimephasedData()
            mpx.setUnits(DatatypeConverter.parseUnits(assignment.getUnits()));
            //assignment.getVAC()
            mpx.setWork(DatatypeConverter.parseDuration(m_projectFile,TimeUnit.HOURS,assignment.getWork()));
            mpx.setWorkContour(assignment.getWorkContour());
            //assignment.getWorkVariance()
         }
      }
   }

   /**
    * This method is used to recursively remove any empty element nodes
    * found in the XML document.
    *
    * @param parent parent node
    * @param node child node
    */
   private void removeEmptyElementNodes (Node parent, Node node)
   {
      if (node.hasChildNodes() == true)
      {
         NodeList list = node.getChildNodes();
         for (int loop=0; loop < list.getLength(); loop++)
         {
            removeEmptyElementNodes(node, list.item(loop));
         }
      }
      else
      {
         if (node.getNodeType() == Node.ELEMENT_NODE)
         {
            parent.removeChild(node);
         }
      }
   }
   
   /**
    * Sets a flag indicating that this class will attempt to correct
    * and read XML which is not compliant with the XML Schema. This
    * behaviour matches that of Microsoft Project when reading the
    * same data.
    *
    * @param flag input compatibility flag
    */
   public void setMicrosoftProjectCompatibleInput (boolean flag)
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
   public boolean getMicrosoftProjectCompatibleInput ()
   {
      return (m_compatibleInput);
   }
   
   private boolean m_compatibleInput = true;
   
   private ProjectFile m_projectFile;      
}
