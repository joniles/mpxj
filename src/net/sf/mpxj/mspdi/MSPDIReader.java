/*
 * file:       MSPDIReader.java
 * author:     Jon Iles
 * copyright:  (c) Packwood Software Limited 2005
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
import net.sf.mpxj.Duration;
import net.sf.mpxj.MPPResourceField;
import net.sf.mpxj.MPPTaskField;
import net.sf.mpxj.MPXJException;
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
import net.sf.mpxj.SubProject;
import net.sf.mpxj.Task;
import net.sf.mpxj.TaskField;
import net.sf.mpxj.TimeUnit;
import net.sf.mpxj.mspdi.schema.Project;
import net.sf.mpxj.reader.AbstractProjectReader;
import net.sf.mpxj.utility.BooleanUtility;
import net.sf.mpxj.utility.NumberUtility;
import net.sf.mpxj.utility.Pair;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;


/**
 * This class creates a new ProjectFile instance by reading an MSPDI file.
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

         HashMap<BigInteger, ProjectCalendar> calendarMap = new HashMap<BigInteger, ProjectCalendar> ();

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

      header.setActualsInSync(BooleanUtility.getBoolean(project.isActualsInSync()));
      header.setAdminProject(BooleanUtility.getBoolean(project.isAdminProject()));
      header.setAuthor(project.getAuthor());
      header.setAutoAddNewResourcesAndTasks(BooleanUtility.getBoolean(project.isAutoAddNewResourcesAndTasks()));
      header.setAutolink(BooleanUtility.getBoolean(project.isAutolink()));
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
      header.setEditableActualCosts(BooleanUtility.getBoolean(project.isEditableActualCosts()));
      header.setExtendedCreationDate(DatatypeConverter.parseDate(project.getExtendedCreationDate()));
      header.setFinishDate(DatatypeConverter.parseDate (project.getFinishDate()));
      header.setFiscalYearStart(BooleanUtility.getBoolean(project.isFiscalYearStart()));
      header.setFiscalYearStartMonth(NumberUtility.getInteger(project.getFYStartDate()));
      header.setHonorConstraints(BooleanUtility.getBoolean(project.isHonorConstraints()));
      header.setInsertedProjectsLikeSummary(BooleanUtility.getBoolean(project.isInsertedProjectsLikeSummary()));
      header.setLastSaved(DatatypeConverter.parseDate(project.getLastSaved()));
      header.setManager(project.getManager());
      header.setMicrosoftProjectServerURL(BooleanUtility.getBoolean(project.isMicrosoftProjectServerURL()));
      header.setMinutesPerDay(NumberUtility.getInteger(project.getMinutesPerDay()));
      header.setMinutesPerWeek(NumberUtility.getInteger(project.getMinutesPerWeek()));
      header.setMoveCompletedEndsBack(BooleanUtility.getBoolean(project.isMoveCompletedEndsBack()));
      header.setMoveCompletedEndsForward(BooleanUtility.getBoolean(project.isMoveCompletedEndsForward()));
      header.setMoveRemainingStartsBack(BooleanUtility.getBoolean(project.isMoveRemainingStartsBack()));
      header.setMoveRemainingStartsForward(BooleanUtility.getBoolean(project.isMoveRemainingStartsForward()));
      header.setMultipleCriticalPaths(BooleanUtility.getBoolean(project.isMultipleCriticalPaths()));
      header.setName(project.getName());
      header.setNewTasksEffortDriven(BooleanUtility.getBoolean(project.isNewTasksEffortDriven()));
      header.setNewTasksEstimated(BooleanUtility.getBoolean(project.isNewTasksEstimated()));
      header.setNewTaskStartIsProjectStart(NumberUtility.getInt(project.getNewTaskStartDate())==0);
      header.setProjectExternallyEdited(BooleanUtility.getBoolean(project.isProjectExternallyEdited()));
      header.setProjectTitle(project.getTitle());
      header.setRemoveFileProperties(BooleanUtility.getBoolean(project.isRemoveFileProperties()));
      header.setRevision(NumberUtility.getInteger(project.getRevision()));
      header.setScheduleFrom(BooleanUtility.getBoolean(project.isScheduleFromStart())?ScheduleFrom.START:ScheduleFrom.FINISH);
      header.setSubject(project.getSubject());
      header.setSplitInProgressTasks(BooleanUtility.getBoolean(project.isSplitsInProgressTasks()));
      header.setSpreadActualCost(BooleanUtility.getBoolean(project.isSpreadActualCost()));
      header.setSpreadPercentComplete(BooleanUtility.getBoolean(project.isSpreadPercentComplete()));
      header.setStartDate(DatatypeConverter.parseDate (project.getStartDate()));
      header.setStatusDate(DatatypeConverter.parseDate(project.getStatusDate()));
      header.setSymbolPosition (project.getCurrencySymbolPosition());
      header.setUniqueID(project.getUID());
      header.setUpdatingTaskStatusUpdatesResourceStatus(BooleanUtility.getBoolean(project.isTaskUpdatesResource()));
      header.setWeekStartDay(DatatypeConverter.parseDay(project.getWeekStartDay()));

   }

   /**
    * This method extracts calendar data from an MSPDI file.
    *
    * @param project Root node of the MSPDI file
    * @param map Map of calendar UIDs to names
    */
   private void readCalendars (Project project, HashMap<BigInteger, ProjectCalendar> map)
   {
      Project.Calendars calendars = project.getCalendars();
      if (calendars != null)
      {
         LinkedList<Pair<ProjectCalendar, BigInteger>> baseCalendars = new LinkedList<Pair<ProjectCalendar, BigInteger>>();
         for (Project.Calendars.Calendar cal : calendars.getCalendar())
         {
            readCalendar (cal, map, baseCalendars);
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
   private static void updateBaseCalendarNames (List<Pair<ProjectCalendar, BigInteger>> baseCalendars, HashMap<BigInteger, ProjectCalendar> map)
   {
      for (Pair<ProjectCalendar, BigInteger> pair : baseCalendars)
      {         
         ProjectCalendar cal = pair.getFirst();
         BigInteger baseCalendarID = pair.getSecond();
         ProjectCalendar baseCal = map.get(baseCalendarID);
         if (baseCal != null)
         {
            cal.setBaseCalendar(baseCal);
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
   private void readCalendar (Project.Calendars.Calendar calendar, HashMap<BigInteger, ProjectCalendar> map, LinkedList<Pair<ProjectCalendar, BigInteger>> baseCalendars)
   {
      ProjectCalendar bc;

      if (BooleanUtility.getBoolean(calendar.isIsBaseCalendar()) == true)
      {
         bc = m_projectFile.addBaseCalendar();
      }
      else
      {
         bc = m_projectFile.addResourceCalendar();
      }

      bc.setUniqueID(NumberUtility.getInteger(calendar.getUID()));
      bc.setName(calendar.getName());
      BigInteger baseCalendarID = calendar.getBaseCalendarUID();
      if (baseCalendarID != null)
      {
         baseCalendars.add(new Pair<ProjectCalendar, BigInteger>(bc, baseCalendarID));
      }

      Project.Calendars.Calendar.WeekDays days = calendar.getWeekDays();
      if (days != null)
      {
         for (Project.Calendars.Calendar.WeekDays.WeekDay weekDay : days.getWeekDay())
         {
            readDay (bc, weekDay);
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
   private void readDay (ProjectCalendar calendar, Project.Calendars.Calendar.WeekDays.WeekDay day)
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
   private void readNormalDay (ProjectCalendar calendar, Project.Calendars.Calendar.WeekDays.WeekDay weekDay)
   {
      int dayNumber = weekDay.getDayType().intValue();
      Day day = Day.getInstance(dayNumber);
      calendar.setWorkingDay(day, BooleanUtility.getBoolean(weekDay.isDayWorking()));
      ProjectCalendarHours hours = calendar.addCalendarHours(day);

      Project.Calendars.Calendar.WeekDays.WeekDay.WorkingTimes times = weekDay.getWorkingTimes();
      if (times != null)
      {
         for (Project.Calendars.Calendar.WeekDays.WeekDay.WorkingTimes.WorkingTime period : times.getWorkingTime())
         {
            Date startTime = DatatypeConverter.parseTime(period.getFromTime());
            Date endTime = DatatypeConverter.parseTime(period.getToTime());

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
   private void readExceptionDay (ProjectCalendar calendar, Project.Calendars.Calendar.WeekDays.WeekDay day)
   {
      ProjectCalendarException exception = calendar.addCalendarException();

      Project.Calendars.Calendar.WeekDays.WeekDay.TimePeriod timePeriod = day.getTimePeriod();
      exception.setFromDate(DatatypeConverter.parseDate(timePeriod.getFromDate()));
      exception.setToDate(DatatypeConverter.parseDate(timePeriod.getToDate()));

      Project.Calendars.Calendar.WeekDays.WeekDay.WorkingTimes times = day.getWorkingTimes();
      if (times != null)
      {
         Project.Calendars.Calendar.WeekDays.WeekDay.WorkingTimes.WorkingTime period;
         List<Project.Calendars.Calendar.WeekDays.WeekDay.WorkingTimes.WorkingTime> time = times.getWorkingTime();
         Iterator<Project.Calendars.Calendar.WeekDays.WeekDay.WorkingTimes.WorkingTime> iter = time.iterator();

         if (iter.hasNext() == true)
         {
            period = iter.next();
            exception.setFromTime1(DatatypeConverter.parseTime(period.getFromTime()));
            exception.setToTime1(DatatypeConverter.parseTime(period.getToTime()));
         }

         if (iter.hasNext() == true)
         {
            period = iter.next();
            exception.setFromTime2(DatatypeConverter.parseTime(period.getFromTime()));
            exception.setToTime2(DatatypeConverter.parseTime(period.getToTime()));
         }

         if (iter.hasNext() == true)
         {
            period = iter.next();
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
      Project.ExtendedAttributes attributes = project.getExtendedAttributes();
      if (attributes != null)
      {
         for (Project.ExtendedAttributes.ExtendedAttribute ea : attributes.getExtendedAttribute())
         {
            readFieldAlias (ea);
         }
      }
   }

   /**
    * Read a single field alias from an extended attribute.
    *
    * @param attribute extended attribute
    */
   private void readFieldAlias (Project.ExtendedAttributes.ExtendedAttribute attribute)
   {
      String alias = attribute.getAlias();

      if (alias != null && alias.length() != 0)
      {
         int id = Integer.parseInt(attribute.getFieldID());
         int base = id & 0xFFFF0000;
         int index = id & 0x0000FFFF;
         
         switch (base)
         {
            case MPPTaskField.TASK_FIELD_BASE:
            {
               TaskField taskField = MPPTaskField.getInstance(index);
               if (taskField != null)
               {
                  m_projectFile.setTaskFieldAlias (taskField, attribute.getAlias());
               }
               break;
            }

            case MPPResourceField.RESOURCE_FIELD_BASE:
            {
               ResourceField resourceField = MPPResourceField.getInstance(index);
               if (resourceField != null)
               {
                  m_projectFile.setResourceFieldAlias (resourceField, attribute.getAlias());
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
   private void readResources (Project project, HashMap<BigInteger, ProjectCalendar> calendarMap)
   {
      Project.Resources resources = project.getResources();
      if (resources != null)
      {
         for (Project.Resources.Resource resource : resources.getResource())
         {
            readResource (resource, calendarMap);
         }
      }
   }

   /**
    * This method extracts data for a single resource from an MSPDI file.
    *
    * @param xml Resource data
    * @param calendarMap Map of calendar UIDs to names
    */
   private void readResource (Project.Resources.Resource xml, HashMap<BigInteger, ProjectCalendar> calendarMap)
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
      mpx.setCanLevel(BooleanUtility.getBoolean(xml.isCanLevel()));
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
      mpx.setIsEnterprise(BooleanUtility.getBoolean(xml.isIsEnterprise()));
      mpx.setIsGeneric(BooleanUtility.getBoolean(xml.isIsGeneric()));
      mpx.setIsInactive(BooleanUtility.getBoolean(xml.isIsInactive()));
      mpx.setIsNull(BooleanUtility.getBoolean(xml.isIsNull()));
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

      readResourceBaselines (xml, mpx);
      
      mpx.setResourceCalendar(calendarMap.get(xml.getCalendarUID()));
      
      // ensure that we cache this value
      mpx.setOverAllocated(BooleanUtility.getBoolean(xml.isOverAllocated()));
      
      m_projectFile.fireResourceReadEvent(mpx);
   }

   /**
    * Reads baseline values for the current resource.
    * 
    * @param xmlResource MSPDI resource instance
    * @param mpxjResource MPXJ resource instance
    */
   private void readResourceBaselines (Project.Resources.Resource xmlResource, Resource mpxjResource)
   {
	   for (Project.Resources.Resource.Baseline baseline: xmlResource.getBaseline())
	   {
		   int number = NumberUtility.getInt(baseline.getNumber());
		   
		   Double cost = DatatypeConverter.parseCurrency(baseline.getCost());
		   Duration work = DatatypeConverter.parseDuration(m_projectFile,TimeUnit.HOURS,baseline.getWork());
		   
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
   private void readResourceExtendedAttributes (Project.Resources.Resource xml, Resource mpx)
   {
      for (Project.Resources.Resource.ExtendedAttribute attrib : xml.getExtendedAttribute())
      {
         int xmlFieldID = Integer.parseInt(attrib.getFieldID()) & 0x0000FFFF;
         ResourceField mpxFieldID = MPPResourceField.getInstance(xmlFieldID);
         DatatypeConverter.parseExtendedAttribute(m_projectFile, mpx, attrib.getValue(), mpxFieldID);
      }
   }

   /**
    * This method extracts task data from an MSPDI file.
    *
    * @param project Root node of the MSPDI file
    */
   private void readTasks (Project project)
   {
      Project.Tasks tasks = project.getTasks();
      if (tasks != null)
      {
         for (Project.Tasks.Task task : tasks.getTask())
         {
            readTask (task);
         }

         for (Project.Tasks.Task task : tasks.getTask())
         {
            readPredecessors (task);
         }
      }

      m_projectFile.updateStructure ();
   }


   /**
    * This method extracts data for a single task from an MSPDI file.
    *
    * @param xml Task data
    */
   private void readTask (Project.Tasks.Task xml)
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
      mpx.setEffortDriven(BooleanUtility.getBoolean(xml.isEffortDriven()));
      mpx.setEstimated(BooleanUtility.getBoolean(xml.isEstimated()));
      mpx.setExternalTask(BooleanUtility.getBoolean(xml.isExternalTask()));
      mpx.setProject(xml.getExternalTaskProject());
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
      // This is not correct?
      mpx.setFreeSlack(DatatypeConverter.parseDurationInMinutes(xml.getFreeSlack()));
      mpx.setHideBar(BooleanUtility.getBoolean(xml.isHideBar()));
      mpx.setHyperlink(xml.getHyperlink());
      mpx.setHyperlinkAddress(xml.getHyperlinkAddress());
      mpx.setHyperlinkSubAddress(xml.getHyperlinkSubAddress());
      mpx.setID(NumberUtility.getInteger(xml.getID()));
      mpx.setIgnoreResourceCalendar(BooleanUtility.getBoolean(xml.isIgnoreResourceCalendar()));
      mpx.setLateFinish(DatatypeConverter.parseDate(xml.getLateFinish()));
      mpx.setLateStart(DatatypeConverter.parseDate(xml.getLateStart()));
      mpx.setLevelAssignments(BooleanUtility.getBoolean(xml.isLevelAssignments()));
      mpx.setLevelingCanSplit(BooleanUtility.getBoolean(xml.isLevelingCanSplit()));
      mpx.setLevelingDelayFormat(DatatypeConverter.parseDurationTimeUnits(xml.getLevelingDelayFormat()));
      if (xml.getLevelingDelay() != null && mpx.getLevelingDelayFormat() != null)
      {
         mpx.setLevelingDelay(Duration.getInstance (xml.getLevelingDelay().doubleValue(), mpx.getLevelingDelayFormat()));
      }


      //mpx.setLinkedFields();
      //mpx.setMarked();
      mpx.setMilestone(BooleanUtility.getBoolean(xml.isMilestone()));
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
      mpx.setNull(BooleanUtility.getBoolean(xml.isIsNull()));
      mpx.setOutlineLevel(NumberUtility.getInteger(xml.getOutlineLevel()));
      mpx.setOutlineNumber(xml.getOutlineNumber());
      mpx.setOverAllocated(BooleanUtility.getBoolean(xml.isOverAllocated()));
      mpx.setOvertimeCost(DatatypeConverter.parseCurrency(xml.getOvertimeCost()));
      mpx.setOvertimeWork(DatatypeConverter.parseDuration(m_projectFile,mpx.getDurationFormat(),xml.getOvertimeWork()));
      mpx.setPercentageComplete(xml.getPercentComplete());
      mpx.setPercentageWorkComplete(xml.getPercentWorkComplete());
      mpx.setPhysicalPercentComplete(NumberUtility.getInteger(xml.getPhysicalPercentComplete()));
      mpx.setPreleveledFinish(DatatypeConverter.parseDate(xml.getPreLeveledFinish()));
      mpx.setPreleveledStart(DatatypeConverter.parseDate(xml.getPreLeveledStart()));
      mpx.setPriority(DatatypeConverter.parsePriority(xml.getPriority()));
      //mpx.setProject();
      mpx.setRecurring(BooleanUtility.getBoolean(xml.isRecurring()));
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
      mpx.setResumeValid(BooleanUtility.getBoolean(xml.isResumeValid()));
      //mpx.setResumeNoEarlierThan();
      mpx.setRollup(BooleanUtility.getBoolean(xml.isRollup()));
      mpx.setStart(DatatypeConverter.parseDate(xml.getStart()));
      //mpx.setStart1();
      //mpx.setStart2();
      //mpx.setStart3();
      //mpx.setStart4();
      //mpx.setStart5();
      mpx.setStartVariance(DatatypeConverter.parseDurationInMinutes(xml.getStartVariance()));
      mpx.setStop(DatatypeConverter.parseDate(xml.getStop()));
      mpx.setSubProject(BooleanUtility.getBoolean(xml.isIsSubproject())?new SubProject():null);
      mpx.setSubprojectName(xml.getSubprojectName());
      mpx.setSubprojectReadOnly(BooleanUtility.getBoolean(xml.isIsSubprojectReadOnly()));
      //mpx.setSuccessors();
      mpx.setSummary(BooleanUtility.getBoolean(xml.isSummary()));
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
      mpx.setType(xml.getType());
      mpx.setUniqueID(NumberUtility.getInteger(xml.getUID()));
      //mpx.setUpdateNeeded();
      mpx.setWBS(xml.getWBS());
      mpx.setWBSLevel (xml.getWBSLevel());
      mpx.setWork(DatatypeConverter.parseDuration(m_projectFile,mpx.getDurationFormat(),xml.getWork()));
      mpx.setWorkVariance(Duration.getInstance (NumberUtility.getDouble(xml.getWorkVariance())/1000, TimeUnit.MINUTES));

      // read last to ensure correct caching
      mpx.setTotalSlack(DatatypeConverter.parseDurationInMinutes(xml.getTotalSlack()));
      mpx.setCritical(BooleanUtility.getBoolean(xml.isCritical()));
      
      readTaskExtendedAttributes(xml, mpx);

      readTaskBaselines (xml, mpx);
      
      m_projectFile.fireTaskReadEvent(mpx);
   }

   /**
    * Reads baseline values for the current task.
    * 
    * @param xmlTask MSPDI task instance
    * @param mpxjTask MPXJ task instance
    */
   private void readTaskBaselines (Project.Tasks.Task xmlTask, Task mpxjTask)
   {
	   for (Project.Tasks.Task.Baseline baseline: xmlTask.getBaseline())
	   {
		   int number = NumberUtility.getInt(baseline.getNumber());
		   
		   Double cost = DatatypeConverter.parseCurrency(baseline.getCost());
		   Duration duration = DatatypeConverter.parseDuration (m_projectFile, mpxjTask.getDurationFormat(), baseline.getDuration());
		   Date finish = DatatypeConverter.parseDate(baseline.getFinish());
		   Date start = DatatypeConverter.parseDate(baseline.getStart());
		   Duration work = DatatypeConverter.parseDuration(m_projectFile,TimeUnit.HOURS,baseline.getWork());
		   
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
   private void readTaskExtendedAttributes (Project.Tasks.Task xml, Task mpx)
   {
      for (Project.Tasks.Task.ExtendedAttribute attrib : xml.getExtendedAttribute())
      {
         int xmlFieldID = Integer.parseInt(attrib.getFieldID()) & 0x0000FFFF;
         TaskField mpxFieldID = MPPTaskField.getInstance(xmlFieldID);
         if (mpxFieldID != null)
         {
            DatatypeConverter.parseExtendedAttribute(m_projectFile, mpx, attrib.getValue(), mpxFieldID);
         }
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
   private ProjectCalendar getTaskCalendar (Project.Tasks.Task task)
   {
      ProjectCalendar calendar = null;

      BigInteger calendarID = task.getCalendarUID();
      if (calendarID != null)
      {
         calendar = m_projectFile.getBaseCalendarByUniqueID(new Integer(calendarID.intValue()));
      }

      return (calendar);
   }


   /**
    * This method extracts predecessor data from an MSPDI file.
    *
    * @param task Task data
    */
   private void readPredecessors (Project.Tasks.Task task)
   {
      Integer uid = task.getUID();
      if (uid != null)
      {
         Task currTask = m_projectFile.getTaskByUniqueID(uid);
         if (currTask != null)
         {
            for (Project.Tasks.Task.PredecessorLink link : task.getPredecessorLink())
            {
               readPredecessor (currTask, link);
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
   private void readPredecessor (Task currTask, Project.Tasks.Task.PredecessorLink link)
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
      Project.Assignments assignments = project.getAssignments();
      if (assignments != null)
      {
         for (Project.Assignments.Assignment assignment : assignments.getAssignment())
         {
            readAssignment (assignment);
         }
      }
   }


   /**
    * This method extracts data for a single assignment from an MSPDI file.
    *
    * @param assignment Assignment data
    */
   private void readAssignment (Project.Assignments.Assignment assignment)
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
