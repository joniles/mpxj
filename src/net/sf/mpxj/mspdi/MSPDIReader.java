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

package net.sf.mpxj.mspdi;

import java.io.InputStream;
import java.math.BigInteger;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.ValidationEvent;
import javax.xml.bind.ValidationEventHandler;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import javax.xml.transform.sax.SAXSource;

import net.sf.mpxj.Availability;
import net.sf.mpxj.AvailabilityTable;
import net.sf.mpxj.CostRateTable;
import net.sf.mpxj.CostRateTableEntry;
import net.sf.mpxj.DateRange;
import net.sf.mpxj.Day;
import net.sf.mpxj.DayType;
import net.sf.mpxj.Duration;
import net.sf.mpxj.MPPResourceField;
import net.sf.mpxj.MPPTaskField;
import net.sf.mpxj.MPXJException;
import net.sf.mpxj.ProjectCalendar;
import net.sf.mpxj.ProjectCalendarException;
import net.sf.mpxj.ProjectCalendarHours;
import net.sf.mpxj.ProjectFile;
import net.sf.mpxj.ProjectHeader;
import net.sf.mpxj.Rate;
import net.sf.mpxj.RelationType;
import net.sf.mpxj.Resource;
import net.sf.mpxj.ResourceAssignment;
import net.sf.mpxj.ResourceField;
import net.sf.mpxj.ResourceType;
import net.sf.mpxj.ScheduleFrom;
import net.sf.mpxj.SplitTaskFactory;
import net.sf.mpxj.SubProject;
import net.sf.mpxj.Task;
import net.sf.mpxj.TaskField;
import net.sf.mpxj.TaskMode;
import net.sf.mpxj.TimeUnit;
import net.sf.mpxj.TimephasedResourceAssignment;
import net.sf.mpxj.TimephasedResourceAssignmentNormaliser;
import net.sf.mpxj.mspdi.schema.Project;
import net.sf.mpxj.mspdi.schema.TimephasedDataType;
import net.sf.mpxj.mspdi.schema.Project.Resources.Resource.AvailabilityPeriods;
import net.sf.mpxj.mspdi.schema.Project.Resources.Resource.Rates;
import net.sf.mpxj.mspdi.schema.Project.Resources.Resource.AvailabilityPeriods.AvailabilityPeriod;
import net.sf.mpxj.reader.AbstractProjectReader;
import net.sf.mpxj.utility.BooleanUtility;
import net.sf.mpxj.utility.NumberUtility;
import net.sf.mpxj.utility.Pair;

import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;

/**
 * This class creates a new ProjectFile instance by reading an MSPDI file.
 */
public final class MSPDIReader extends AbstractProjectReader
{
   /**
    * {@inheritDoc}
    */
   public ProjectFile read(InputStream stream) throws MPXJException
   {
      try
      {
         m_projectFile = new ProjectFile();

         m_projectFile.setAutoTaskID(false);
         m_projectFile.setAutoTaskUniqueID(false);
         m_projectFile.setAutoResourceID(false);
         m_projectFile.setAutoResourceUniqueID(false);
         m_projectFile.setAutoOutlineLevel(false);
         m_projectFile.setAutoOutlineNumber(false);
         m_projectFile.setAutoWBS(false);
         m_projectFile.setAutoCalendarUniqueID(false);

         SAXParserFactory factory = SAXParserFactory.newInstance();
         factory.setNamespaceAware(true);
         SAXParser saxParser = factory.newSAXParser();
         XMLReader xmlReader = saxParser.getXMLReader();
         SAXSource doc = new SAXSource(xmlReader, new InputSource(stream));

         if (CONTEXT == null)
         {
            throw CONTEXT_EXCEPTION;
         }

         Unmarshaller unmarshaller = CONTEXT.createUnmarshaller();

         //
         // If we are matching the behaviour of MS project, then we need to
         // ignore validation warnings.
         //
         if (m_compatibleInput == true)
         {
            unmarshaller.setEventHandler(new ValidationEventHandler()
            {
               public boolean handleEvent(ValidationEvent event)
               {
                  return (true);
               }
            });
         }

         Project project = (Project) unmarshaller.unmarshal(doc);

         HashMap<BigInteger, ProjectCalendar> calendarMap = new HashMap<BigInteger, ProjectCalendar>();

         readProjectHeader(project);
         readProjectExtendedAttributes(project);
         readCalendars(project, calendarMap);
         readResources(project, calendarMap);
         readTasks(project);
         readAssignments(project);

         //
         // Ensure that the unique ID counters are correct
         //
         m_projectFile.updateUniqueCounters();

         //
         // Ensure that the default calendar name is set in the project header
         //
         ProjectCalendar defaultCalendar = calendarMap.get(project.getCalendarUID());
         if (defaultCalendar != null)
         {
            m_projectFile.getProjectHeader().setCalendarName(defaultCalendar.getName());
         }

         return (m_projectFile);
      }

      catch (ParserConfigurationException ex)
      {
         throw new MPXJException("Failed to parse file", ex);
      }

      catch (JAXBException ex)
      {
         throw new MPXJException("Failed to parse file", ex);
      }

      catch (SAXException ex)
      {
         throw new MPXJException("Failed to parse file", ex);
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
   private void readProjectHeader(Project project)
   {
      ProjectHeader header = m_projectFile.getProjectHeader();

      header.setActualsInSync(BooleanUtility.getBoolean(project.isActualsInSync()));
      header.setAdminProject(BooleanUtility.getBoolean(project.isAdminProject()));
      header.setAuthor(project.getAuthor());
      header.setAutoAddNewResourcesAndTasks(BooleanUtility.getBoolean(project.isAutoAddNewResourcesAndTasks()));
      header.setAutolink(BooleanUtility.getBoolean(project.isAutolink()));
      header.setBaselineForEarnedValue(NumberUtility.getInteger(project.getBaselineForEarnedValue()));
      header.setCalendarName(project.getCalendarUID() == null ? null : project.getCalendarUID().toString());
      header.setCategory(project.getCategory());
      header.setCompany(project.getCompany());
      header.setCreationDate(DatatypeConverter.parseDate(project.getCreationDate()));
      header.setCriticalSlackLimit(NumberUtility.getInteger(project.getCriticalSlackLimit()));
      header.setCurrencyDigits(NumberUtility.getInteger(project.getCurrencyDigits()));
      header.setCurrencyCode(project.getCurrencyCode());
      header.setCurrencySymbol(project.getCurrencySymbol());
      header.setCurrentDate(DatatypeConverter.parseDate(project.getCurrentDate()));
      header.setDaysPerMonth(NumberUtility.getInteger(project.getDaysPerMonth()));
      header.setDefaultDurationUnits(DatatypeConverter.parseDurationTimeUnits(project.getDurationFormat()));
      header.setDefaultEndTime(DatatypeConverter.parseTime(project.getDefaultFinishTime()));
      header.setDefaultFixedCostAccrual(project.getDefaultFixedCostAccrual());
      header.setDefaultOvertimeRate(DatatypeConverter.parseRate(project.getDefaultOvertimeRate()));
      header.setDefaultStandardRate(DatatypeConverter.parseRate(project.getDefaultStandardRate()));
      header.setDefaultStartTime(DatatypeConverter.parseTime(project.getDefaultStartTime()));
      header.setDefaultTaskEarnedValueMethod(DatatypeConverter.parseEarnedValueMethod(project.getDefaultTaskEVMethod()));
      header.setDefaultTaskType(project.getDefaultTaskType());
      header.setDefaultWorkUnits(DatatypeConverter.parseWorkUnits(project.getWorkFormat()));
      header.setEarnedValueMethod(DatatypeConverter.parseEarnedValueMethod(project.getEarnedValueMethod()));
      header.setEditableActualCosts(BooleanUtility.getBoolean(project.isEditableActualCosts()));
      header.setExtendedCreationDate(DatatypeConverter.parseDate(project.getExtendedCreationDate()));
      header.setFinishDate(DatatypeConverter.parseDate(project.getFinishDate()));
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
      header.setNewTaskStartIsProjectStart(NumberUtility.getInt(project.getNewTaskStartDate()) == 0);
      header.setProjectExternallyEdited(BooleanUtility.getBoolean(project.isProjectExternallyEdited()));
      header.setProjectTitle(project.getTitle());
      header.setRemoveFileProperties(BooleanUtility.getBoolean(project.isRemoveFileProperties()));
      header.setRevision(NumberUtility.getInteger(project.getRevision()));
      header.setScheduleFrom(BooleanUtility.getBoolean(project.isScheduleFromStart()) ? ScheduleFrom.START : ScheduleFrom.FINISH);
      header.setSubject(project.getSubject());
      header.setSplitInProgressTasks(BooleanUtility.getBoolean(project.isSplitsInProgressTasks()));
      header.setSpreadActualCost(BooleanUtility.getBoolean(project.isSpreadActualCost()));
      header.setSpreadPercentComplete(BooleanUtility.getBoolean(project.isSpreadPercentComplete()));
      header.setStartDate(DatatypeConverter.parseDate(project.getStartDate()));
      header.setStatusDate(DatatypeConverter.parseDate(project.getStatusDate()));
      header.setSymbolPosition(project.getCurrencySymbolPosition());
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
   private void readCalendars(Project project, HashMap<BigInteger, ProjectCalendar> map)
   {
      Project.Calendars calendars = project.getCalendars();
      if (calendars != null)
      {
         LinkedList<Pair<ProjectCalendar, BigInteger>> baseCalendars = new LinkedList<Pair<ProjectCalendar, BigInteger>>();
         for (Project.Calendars.Calendar cal : calendars.getCalendar())
         {
            readCalendar(cal, map, baseCalendars);
         }
         updateBaseCalendarNames(baseCalendars, map);
      }

      try
      {
         ProjectHeader header = m_projectFile.getProjectHeader();
         BigInteger calendarID = new BigInteger(header.getCalendarName());
         ProjectCalendar calendar = map.get(calendarID);
         m_projectFile.setCalendar(calendar);
      }

      catch (Exception ex)
      {
         // Ignore exceptions
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
   private static void updateBaseCalendarNames(List<Pair<ProjectCalendar, BigInteger>> baseCalendars, HashMap<BigInteger, ProjectCalendar> map)
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
   private void readCalendar(Project.Calendars.Calendar calendar, HashMap<BigInteger, ProjectCalendar> map, LinkedList<Pair<ProjectCalendar, BigInteger>> baseCalendars)
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
            readDay(bc, weekDay);
         }
      }
      else
      {
         bc.setWorkingDay(Day.SUNDAY, DayType.DEFAULT);
         bc.setWorkingDay(Day.MONDAY, DayType.DEFAULT);
         bc.setWorkingDay(Day.TUESDAY, DayType.DEFAULT);
         bc.setWorkingDay(Day.WEDNESDAY, DayType.DEFAULT);
         bc.setWorkingDay(Day.THURSDAY, DayType.DEFAULT);
         bc.setWorkingDay(Day.FRIDAY, DayType.DEFAULT);
         bc.setWorkingDay(Day.SATURDAY, DayType.DEFAULT);
      }

      map.put(calendar.getUID(), bc);
   }

   /**
    * This method extracts data for a single day from an MSPDI file.
    *
    * @param calendar Calendar data
    * @param day Day data
    */
   private void readDay(ProjectCalendar calendar, Project.Calendars.Calendar.WeekDays.WeekDay day)
   {
      BigInteger dayType = day.getDayType();
      if (dayType != null)
      {
         if (dayType.intValue() == 0)
         {
            readExceptionDay(calendar, day);
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
               if (startTime.getTime() >= endTime.getTime())
               {
                  Calendar cal = Calendar.getInstance();
                  cal.setTime(endTime);
                  cal.add(Calendar.DAY_OF_YEAR, 1);
                  endTime = cal.getTime();
               }

               hours.addRange(new DateRange(startTime, endTime));
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
      Date fromDate = DatatypeConverter.parseDate(timePeriod.getFromDate());
      Date toDate = DatatypeConverter.parseDate(timePeriod.getToDate());
      Project.Calendars.Calendar.WeekDays.WeekDay.WorkingTimes times = day.getWorkingTimes();
      ProjectCalendarException exception = calendar.addCalendarException(fromDate, toDate);

      if (times != null)
      {
         List<Project.Calendars.Calendar.WeekDays.WeekDay.WorkingTimes.WorkingTime> time = times.getWorkingTime();
         for (Project.Calendars.Calendar.WeekDays.WeekDay.WorkingTimes.WorkingTime period : time)
         {
            Date startTime = DatatypeConverter.parseTime(period.getFromTime());
            Date endTime = DatatypeConverter.parseTime(period.getToTime());

            if (startTime != null && endTime != null)
            {
               if (startTime.getTime() >= endTime.getTime())
               {
                  Calendar cal = Calendar.getInstance();
                  cal.setTime(endTime);
                  cal.add(Calendar.DAY_OF_YEAR, 1);
                  endTime = cal.getTime();
               }

               exception.addRange(new DateRange(startTime, endTime));
            }
         }
      }
   }

   /**
    * This method extracts project extended attribute data from an MSPDI file.
    *
    * @param project Root node of the MSPDI file
    */
   private void readProjectExtendedAttributes(Project project)
   {
      Project.ExtendedAttributes attributes = project.getExtendedAttributes();
      if (attributes != null)
      {
         for (Project.ExtendedAttributes.ExtendedAttribute ea : attributes.getExtendedAttribute())
         {
            readFieldAlias(ea);
         }
      }
   }

   /**
    * Read a single field alias from an extended attribute.
    *
    * @param attribute extended attribute
    */
   private void readFieldAlias(Project.ExtendedAttributes.ExtendedAttribute attribute)
   {
      String alias = attribute.getAlias();

      if (alias != null && alias.length() != 0)
      {
         int id = Integer.parseInt(attribute.getFieldID());
         int base = id & 0xFFFF0000;
         int index = id & 0x0000FFFF;

         switch (base)
         {
            case MPPTaskField.TASK_FIELD_BASE :
            {
               TaskField taskField = MPPTaskField.getInstance(index);
               if (taskField != null)
               {
                  m_projectFile.setTaskFieldAlias(taskField, attribute.getAlias());
               }
               break;
            }

            case MPPResourceField.RESOURCE_FIELD_BASE :
            {
               ResourceField resourceField = MPPResourceField.getInstance(index);
               if (resourceField != null)
               {
                  m_projectFile.setResourceFieldAlias(resourceField, attribute.getAlias());
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
      mpx.setActveDirectoryGUID(xml.getActiveDirectoryGUID());
      mpx.setActualCost(DatatypeConverter.parseCurrency(xml.getActualCost()));
      mpx.setActualOvertimeCost(DatatypeConverter.parseCurrency(xml.getActualOvertimeCost()));
      mpx.setActualOvertimeWork(DatatypeConverter.parseDuration(m_projectFile, null, xml.getActualOvertimeWork()));
      mpx.setActualOvertimeWorkProtected(DatatypeConverter.parseDuration(m_projectFile, null, xml.getActualOvertimeWorkProtected()));
      mpx.setActualWork(DatatypeConverter.parseDuration(m_projectFile, null, xml.getActualWork()));
      mpx.setActualWorkProtected(DatatypeConverter.parseDuration(m_projectFile, null, xml.getActualWorkProtected()));
      mpx.setACWP(DatatypeConverter.parseCurrency(xml.getACWP()));
      mpx.setAvailableFrom(DatatypeConverter.parseDate(xml.getAvailableFrom()));
      mpx.setAvailableTo(DatatypeConverter.parseDate(xml.getAvailableTo()));
      mpx.setBCWS(DatatypeConverter.parseCurrency(xml.getBCWS()));
      mpx.setBCWP(DatatypeConverter.parseCurrency(xml.getBCWP()));
      mpx.setBookingType(xml.getBookingType());
      //mpx.setBaseCalendar ();
      //mpx.setBaselineCost();
      //mpx.setBaselineWork();
      mpx.setBudget(BooleanUtility.getBoolean(xml.isIsBudget()));
      mpx.setCanLevel(BooleanUtility.getBoolean(xml.isCanLevel()));
      mpx.setCode(xml.getCode());
      mpx.setCost(DatatypeConverter.parseCurrency(xml.getCost()));
      mpx.setCostPerUse(DatatypeConverter.parseCurrency(xml.getCostPerUse()));
      mpx.setCostVariance(DatatypeConverter.parseCurrency(xml.getCostVariance()));
      mpx.setCreationDate(DatatypeConverter.parseDate(xml.getCreationDate()));
      mpx.setCV(DatatypeConverter.parseCurrency(xml.getCV()));
      mpx.setEmailAddress(xml.getEmailAddress());
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
      mpx.setOvertimeWork(DatatypeConverter.parseDuration(m_projectFile, null, xml.getOvertimeWork()));
      mpx.setPeakUnits(DatatypeConverter.parseUnits(xml.getPeakUnits()));
      mpx.setPercentWorkComplete(xml.getPercentWorkComplete());
      mpx.setPhonetics(xml.getPhonetics());
      mpx.setRegularWork(DatatypeConverter.parseDuration(m_projectFile, null, xml.getRegularWork()));
      mpx.setRemainingCost(DatatypeConverter.parseCurrency(xml.getRemainingCost()));
      mpx.setRemainingOvertimeCost(DatatypeConverter.parseCurrency(xml.getRemainingOvertimeCost()));
      mpx.setRemainingWork(DatatypeConverter.parseDuration(m_projectFile, null, xml.getRemainingWork()));
      mpx.setRemainingOvertimeWork(DatatypeConverter.parseDuration(m_projectFile, null, xml.getRemainingOvertimeWork()));
      mpx.setStandardRate(DatatypeConverter.parseRate(xml.getStandardRate()));
      mpx.setStandardRateFormat(DatatypeConverter.parseTimeUnit(xml.getStandardRateFormat()));
      mpx.setSV(DatatypeConverter.parseCurrency(xml.getSV()));
      mpx.setType(xml.getType());
      mpx.setUniqueID(NumberUtility.getInteger(xml.getUID()));
      mpx.setWork(DatatypeConverter.parseDuration(m_projectFile, null, xml.getWork()));
      mpx.setWorkGroup(xml.getWorkGroup());
      mpx.setWorkVariance(DatatypeConverter.parseDurationInThousanthsOfMinutes(xml.getWorkVariance()));

      if (mpx.getType() == ResourceType.MATERIAL && BooleanUtility.getBoolean(xml.isIsCostResource()))
      {
         mpx.setType(ResourceType.COST);
      }

      readResourceExtendedAttributes(xml, mpx);

      readResourceBaselines(xml, mpx);

      mpx.setResourceCalendar(calendarMap.get(xml.getCalendarUID()));

      // ensure that we cache this value
      mpx.setOverAllocated(BooleanUtility.getBoolean(xml.isOverAllocated()));

      readCostRateTables(mpx, xml.getRates());

      readAvailabilityTable(mpx, xml.getAvailabilityPeriods());

      m_projectFile.fireResourceReadEvent(mpx);
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
         int number = NumberUtility.getInt(baseline.getNumber());

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
         int xmlFieldID = Integer.parseInt(attrib.getFieldID()) & 0x0000FFFF;
         ResourceField mpxFieldID = MPPResourceField.getInstance(xmlFieldID);
         DatatypeConverter.parseExtendedAttribute(m_projectFile, mpx, attrib.getValue(), mpxFieldID);
      }
   }

   /**
    * Reads the cost rate tables from the file.
    * 
    * @param resource parent resource
    * @param rates XML cot rate tables
    */
   private void readCostRateTables(Resource resource, Rates rates)
   {
      if (rates == null)
      {
         CostRateTable table = new CostRateTable();
         table.add(CostRateTableEntry.DEFAULT_ENTRY);
         resource.setCostRateTable(0, table);

         table = new CostRateTable();
         table.add(CostRateTableEntry.DEFAULT_ENTRY);
         resource.setCostRateTable(1, table);

         table = new CostRateTable();
         table.add(CostRateTableEntry.DEFAULT_ENTRY);
         resource.setCostRateTable(2, table);

         table = new CostRateTable();
         table.add(CostRateTableEntry.DEFAULT_ENTRY);
         resource.setCostRateTable(3, table);

         table = new CostRateTable();
         table.add(CostRateTableEntry.DEFAULT_ENTRY);
         resource.setCostRateTable(4, table);
      }
      else
      {
         Set<CostRateTable> tables = new HashSet<CostRateTable>();

         for (net.sf.mpxj.mspdi.schema.Project.Resources.Resource.Rates.Rate rate : rates.getRate())
         {
            Rate standardRate = DatatypeConverter.parseRate(rate.getStandardRate());
            TimeUnit standardRateFormat = DatatypeConverter.parseTimeUnit(rate.getStandardRateFormat());
            Rate overtimeRate = DatatypeConverter.parseRate(rate.getOvertimeRate());
            TimeUnit overtimeRateFormat = DatatypeConverter.parseTimeUnit(rate.getOvertimeRateFormat());
            Double costPerUse = DatatypeConverter.parseCurrency(rate.getCostPerUse());
            Date endDate = DatatypeConverter.parseDate(rate.getRatesTo());

            CostRateTableEntry entry = new CostRateTableEntry(standardRate, standardRateFormat, overtimeRate, overtimeRateFormat, costPerUse, endDate);

            int tableIndex = rate.getRateTable().intValue();
            CostRateTable table = resource.getCostRateTable(tableIndex);
            if (table == null)
            {
               table = new CostRateTable();
               resource.setCostRateTable(tableIndex, table);
            }
            table.add(entry);
            tables.add(table);
         }

         for (CostRateTable table : tables)
         {
            Collections.sort(table);
         }
      }
   }

   /**
    * Reads the availability table from the file.
    * 
    * @param resource MPXJ resource instance
    * @param periods MSPDI availability periods
    */
   private void readAvailabilityTable(Resource resource, AvailabilityPeriods periods)
   {
      if (periods != null)
      {
         AvailabilityTable table = resource.getAvailability();
         List<AvailabilityPeriod> list = periods.getAvailabilityPeriod();
         for (AvailabilityPeriod period : list)
         {
            Date start = DatatypeConverter.parseDate(period.getAvailableFrom());
            Date end = DatatypeConverter.parseDate(period.getAvailableTo());
            Number units = DatatypeConverter.parseUnits(period.getAvailableUnits());
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
         for (Project.Tasks.Task task : tasks.getTask())
         {
            readTask(task);
         }

         for (Project.Tasks.Task task : tasks.getTask())
         {
            readPredecessors(task);
         }
      }

      m_projectFile.updateStructure();
   }

   /**
    * This method extracts data for a single task from an MSPDI file.
    *
    * @param xml Task data
    */
   private void readTask(Project.Tasks.Task xml)
   {
      Task mpx = m_projectFile.addTask();
      mpx.setNull(BooleanUtility.getBoolean(xml.isIsNull()));
      mpx.setID(NumberUtility.getInteger(xml.getID()));
      mpx.setUniqueID(NumberUtility.getInteger(xml.getUID()));

      if (!mpx.getNull())
      {
         //
         // Set the duration format up front as this is required later
         //
         TimeUnit durationFormat = DatatypeConverter.parseDurationTimeUnits(xml.getDurationFormat());

         mpx.setActive(BooleanUtility.getBoolean(xml.isActive()));
         mpx.setActualCost(DatatypeConverter.parseCurrency(xml.getActualCost()));
         mpx.setActualDuration(DatatypeConverter.parseDuration(m_projectFile, durationFormat, xml.getActualDuration()));
         mpx.setActualFinish(DatatypeConverter.parseDate(xml.getActualFinish()));
         mpx.setActualOvertimeCost(DatatypeConverter.parseCurrency(xml.getActualOvertimeCost()));
         mpx.setActualOvertimeWork(DatatypeConverter.parseDuration(m_projectFile, durationFormat, xml.getActualOvertimeWork()));
         mpx.setActualOvertimeWorkProtected(DatatypeConverter.parseDuration(m_projectFile, durationFormat, xml.getActualOvertimeWorkProtected()));
         mpx.setActualStart(DatatypeConverter.parseDate(xml.getActualStart()));
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
         mpx.setDuration(DatatypeConverter.parseDuration(m_projectFile, durationFormat, xml.getDuration()));
         mpx.setDurationText(xml.getDurationText());
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
         mpx.setFinishText(xml.getFinishText());
         //mpx.setFinish1();
         //mpx.setFinish2();
         //mpx.setFinish3();
         //mpx.setFinish4();
         //mpx.setFinish5();
         mpx.setFinishVariance(DatatypeConverter.parseDurationInThousanthsOfMinutes(xml.getFinishVariance()));
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
         mpx.setFreeSlack(DatatypeConverter.parseDurationInThousanthsOfMinutes(xml.getFreeSlack()));
         mpx.setHideBar(BooleanUtility.getBoolean(xml.isHideBar()));
         mpx.setHyperlink(xml.getHyperlink());
         mpx.setHyperlinkAddress(xml.getHyperlinkAddress());
         mpx.setHyperlinkSubAddress(xml.getHyperlinkSubAddress());

         mpx.setIgnoreResourceCalendar(BooleanUtility.getBoolean(xml.isIgnoreResourceCalendar()));
         mpx.setLateFinish(DatatypeConverter.parseDate(xml.getLateFinish()));
         mpx.setLateStart(DatatypeConverter.parseDate(xml.getLateStart()));
         mpx.setLevelAssignments(BooleanUtility.getBoolean(xml.isLevelAssignments()));
         mpx.setLevelingCanSplit(BooleanUtility.getBoolean(xml.isLevelingCanSplit()));
         mpx.setLevelingDelayFormat(DatatypeConverter.parseDurationTimeUnits(xml.getLevelingDelayFormat()));
         if (xml.getLevelingDelay() != null && mpx.getLevelingDelayFormat() != null)
         {
            mpx.setLevelingDelay(Duration.getInstance(xml.getLevelingDelay().doubleValue(), mpx.getLevelingDelayFormat()));
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
         mpx.setOutlineLevel(NumberUtility.getInteger(xml.getOutlineLevel()));
         mpx.setOutlineNumber(xml.getOutlineNumber());
         mpx.setOverAllocated(BooleanUtility.getBoolean(xml.isOverAllocated()));
         mpx.setOvertimeCost(DatatypeConverter.parseCurrency(xml.getOvertimeCost()));
         mpx.setOvertimeWork(DatatypeConverter.parseDuration(m_projectFile, durationFormat, xml.getOvertimeWork()));
         mpx.setPercentageComplete(xml.getPercentComplete());
         mpx.setPercentageWorkComplete(xml.getPercentWorkComplete());
         mpx.setPhysicalPercentComplete(NumberUtility.getInteger(xml.getPhysicalPercentComplete()));
         mpx.setPreleveledFinish(DatatypeConverter.parseDate(xml.getPreLeveledFinish()));
         mpx.setPreleveledStart(DatatypeConverter.parseDate(xml.getPreLeveledStart()));
         mpx.setPriority(DatatypeConverter.parsePriority(xml.getPriority()));
         //mpx.setProject();
         mpx.setRecurring(BooleanUtility.getBoolean(xml.isRecurring()));
         mpx.setRegularWork(DatatypeConverter.parseDuration(m_projectFile, durationFormat, xml.getRegularWork()));
         mpx.setRemainingCost(DatatypeConverter.parseCurrency(xml.getRemainingCost()));
         mpx.setRemainingDuration(DatatypeConverter.parseDuration(m_projectFile, durationFormat, xml.getRemainingDuration()));
         mpx.setRemainingOvertimeCost(DatatypeConverter.parseCurrency(xml.getRemainingOvertimeCost()));
         mpx.setRemainingOvertimeWork(DatatypeConverter.parseDuration(m_projectFile, durationFormat, xml.getRemainingOvertimeWork()));
         mpx.setRemainingWork(DatatypeConverter.parseDuration(m_projectFile, durationFormat, xml.getRemainingWork()));
         //mpx.setResourceGroup();
         //mpx.setResourceInitials();
         //mpx.setResourceNames();
         mpx.setResume(DatatypeConverter.parseDate(xml.getResume()));
         mpx.setResumeValid(BooleanUtility.getBoolean(xml.isResumeValid()));
         //mpx.setResumeNoEarlierThan();
         mpx.setRollup(BooleanUtility.getBoolean(xml.isRollup()));
         mpx.setStart(DatatypeConverter.parseDate(xml.getStart()));
         mpx.setStartText(xml.getStartText());
         //mpx.setStart1();
         //mpx.setStart2();
         //mpx.setStart3();
         //mpx.setStart4();
         //mpx.setStart5();
         mpx.setStartVariance(DatatypeConverter.parseDurationInThousanthsOfMinutes(xml.getStartVariance()));
         mpx.setStop(DatatypeConverter.parseDate(xml.getStop()));
         mpx.setSubProject(BooleanUtility.getBoolean(xml.isIsSubproject()) ? new SubProject() : null);
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
         mpx.setTaskMode(BooleanUtility.getBoolean(xml.isManual()) ? TaskMode.MANUALLY_SCHEDULED : TaskMode.AUTO_SCHEDULED);
         mpx.setType(xml.getType());
         //mpx.setUpdateNeeded();
         mpx.setWBS(xml.getWBS());
         mpx.setWBSLevel(xml.getWBSLevel());
         mpx.setWork(DatatypeConverter.parseDuration(m_projectFile, durationFormat, xml.getWork()));
         mpx.setWorkVariance(Duration.getInstance(NumberUtility.getDouble(xml.getWorkVariance()) / 1000, TimeUnit.MINUTES));

         // read last to ensure correct caching
         mpx.setTotalSlack(DatatypeConverter.parseDurationInThousanthsOfMinutes(xml.getTotalSlack()));
         mpx.setCritical(BooleanUtility.getBoolean(xml.isCritical()));

         readTaskExtendedAttributes(xml, mpx);

         readTaskBaselines(xml, mpx, durationFormat);
      }

      m_projectFile.fireTaskReadEvent(mpx);
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
         int number = NumberUtility.getInt(baseline.getNumber());

         Double cost = DatatypeConverter.parseCurrency(baseline.getCost());
         Duration duration = DatatypeConverter.parseDuration(m_projectFile, durationFormat, baseline.getDuration());
         Date finish = DatatypeConverter.parseDate(baseline.getFinish());
         Date start = DatatypeConverter.parseDate(baseline.getStart());
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
   private ProjectCalendar getTaskCalendar(Project.Tasks.Task task)
   {
      ProjectCalendar calendar = null;

      BigInteger calendarID = task.getCalendarUID();
      if (calendarID != null)
      {
         calendar = m_projectFile.getBaseCalendarByUniqueID(Integer.valueOf(calendarID.intValue()));
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
      if (uid != null)
      {
         Task prevTask = m_projectFile.getTaskByUniqueID(Integer.valueOf(uid.intValue()));
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
               lag = link.getLinkLag().intValue() / 10;
            }
            else
            {
               lag = 0;
            }

            TimeUnit lagUnits = DatatypeConverter.parseDurationTimeUnits(link.getLagFormat());
            Duration lagDuration = Duration.convertUnits(lag, TimeUnit.MINUTES, lagUnits, m_projectFile.getProjectHeader());

            currTask.addPredecessor(prevTask, type, lagDuration);
         }
      }
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
         SplitTaskFactory splitFactory = new SplitTaskFactory();
         TimephasedResourceAssignmentNormaliser normaliser = new MSPDITimephasedResourceAssignmentNormaliser();
         for (Project.Assignments.Assignment assignment : assignments.getAssignment())
         {
            readAssignment(assignment, splitFactory, normaliser);
         }
      }
   }

   /**
    * This method extracts data for a single assignment from an MSPDI file.
    *
    * @param assignment Assignment data
    * @param splitFactory split task handling
    * @param normaliser timephased resource assignment normaliser
    */
   private void readAssignment(Project.Assignments.Assignment assignment, SplitTaskFactory splitFactory, TimephasedResourceAssignmentNormaliser normaliser)
   {
      BigInteger taskUID = assignment.getTaskUID();
      BigInteger resourceUID = assignment.getResourceUID();
      if (taskUID != null && resourceUID != null)
      {
         Task task = m_projectFile.getTaskByUniqueID(Integer.valueOf(taskUID.intValue()));
         Resource resource = m_projectFile.getResourceByUniqueID(Integer.valueOf(resourceUID.intValue()));

         //System.out.println(task);
         ProjectCalendar calendar = null;
         if (resource != null)
         {
            calendar = resource.getResourceCalendar();
         }

         if (calendar == null)
         {
            calendar = task.getCalendar();
         }

         if (calendar == null)
         {
            calendar = m_projectFile.getCalendar();
         }

         LinkedList<TimephasedResourceAssignment> timephasedComplete = readTimephasedAssignment(calendar, assignment, 2);
         LinkedList<TimephasedResourceAssignment> timephasedPlanned = readTimephasedAssignment(calendar, assignment, 1);
         boolean raw = true;

         if (isSplit(calendar, timephasedComplete) || isSplit(calendar, timephasedPlanned))
         {
            task.setSplits(new LinkedList<DateRange>());
            normaliser.normalise(calendar, timephasedComplete);
            normaliser.normalise(calendar, timephasedPlanned);
            splitFactory.processSplitData(task, timephasedComplete, timephasedPlanned);
            raw = false;
         }

         if (task != null)
         {
            ResourceAssignment mpx = task.addResourceAssignment(resource);
            mpx.setTimephasedNormaliser(normaliser);

            mpx.setActualCost(DatatypeConverter.parseCurrency(assignment.getActualCost()));
            mpx.setActualFinish(DatatypeConverter.parseDate(assignment.getActualFinish()));
            //assignment.getActualOvertimeCost()
            //assignment.getActualOvertimeWork()
            //assignment.getActualOvertimeWorkProtected()
            mpx.setActualStart(DatatypeConverter.parseDate(assignment.getActualStart()));
            mpx.setActualWork(DatatypeConverter.parseDuration(m_projectFile, TimeUnit.HOURS, assignment.getActualWork()));
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
            mpx.setDelay(DatatypeConverter.parseDurationInThousanthsOfMinutes(assignment.getDelay()));
            //assignment.getExtendedAttribute()
            mpx.setFinish(DatatypeConverter.parseDate(assignment.getFinish()));
            mpx.setVariableRateUnits(BooleanUtility.getBoolean(assignment.isHasFixedRateUnits()) ? null : DatatypeConverter.parseTimeUnit(assignment.getRateScale()));
            //assignment.getFinishVariance()
            //assignment.getHyperlink()
            //assignment.getHyperlinkAddress()
            //assignment.getHyperlinkSubAddress()
            mpx.setLevelingDelay(DatatypeConverter.parseDurationInTenthsOfMinutes(m_projectFile.getProjectHeader(), assignment.getLevelingDelay(), DatatypeConverter.parseDurationTimeUnits(assignment.getLevelingDelayFormat())));
            //assignment.getNotes()
            //assignment.getOvertimeCost()
            mpx.setOvertimeWork(DatatypeConverter.parseDuration(m_projectFile, TimeUnit.HOURS, assignment.getOvertimeWork()));
            //assignment.getPercentWorkComplete()
            //mpx.setPlannedCost();
            //mpx.setPlannedWork();
            //assignment.getRegularWork()
            //assignment.getRemainingCost()
            //assignment.getRemainingOvertimeCost()
            //assignment.getRemainingOvertimeWork()
            mpx.setRemainingWork(DatatypeConverter.parseDuration(m_projectFile, TimeUnit.HOURS, assignment.getRemainingWork()));
            //assignment.getResume()
            mpx.setStart(DatatypeConverter.parseDate(assignment.getStart()));
            //assignment.getStartVariance()
            //assignment.getStop()
            //assignment.getTimephasedData()
            mpx.setUnits(DatatypeConverter.parseUnits(assignment.getUnits()));
            //assignment.getVAC()
            mpx.setWork(DatatypeConverter.parseDuration(m_projectFile, TimeUnit.HOURS, assignment.getWork()));
            mpx.setWorkContour(assignment.getWorkContour());
            //assignment.getWorkVariance()
            mpx.setTimephasedComplete(timephasedComplete, raw);
            mpx.setTimephasedPlanned(timephasedPlanned, raw);

            readAssignmentBaselines(assignment, mpx);
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
         int number = NumberUtility.getInt(baseline.getNumber());

         Number cost = DatatypeConverter.parseExtendedAttributeCurrency(baseline.getCost());
         Date finish = DatatypeConverter.parseExtendedAttributeDate(baseline.getFinish());
         Date start = DatatypeConverter.parseExtendedAttributeDate(baseline.getStart());
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
            // TODO: implement extended baseline support
         }
      }
   }

   /**
    * Test to determine if this is a split task.
    * 
    * @param calendar current calendar
    * @param list timephased resource assignment list
    * @return boolean flag
    */
   private boolean isSplit(ProjectCalendar calendar, List<TimephasedResourceAssignment> list)
   {
      boolean result = false;
      for (TimephasedResourceAssignment assignment : list)
      {
         if (assignment.getTotalWork().getDuration() == 0)
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
    * Reads timephased assignment data.
    * 
    * @param calendar current calendar
    * @param assignment assignment data
    * @param type flag indicating if this is planned or complete work
    * @return list of timephased resource assignment instances
    */
   private LinkedList<TimephasedResourceAssignment> readTimephasedAssignment(ProjectCalendar calendar, Project.Assignments.Assignment assignment, int type)
   {
      LinkedList<TimephasedResourceAssignment> result = new LinkedList<TimephasedResourceAssignment>();

      for (TimephasedDataType item : assignment.getTimephasedData())
      {
         if (NumberUtility.getInt(item.getType()) != type)
         {
            continue;
         }

         Date startDate = DatatypeConverter.parseDate(item.getStart());
         Date finishDate = DatatypeConverter.parseDate(item.getFinish());
         Duration work = DatatypeConverter.parseDuration(m_projectFile, TimeUnit.MINUTES, item.getValue());
         if (work == null)
         {
            work = Duration.getInstance(0, TimeUnit.MINUTES);
         }
         else
         {
            work = Duration.getInstance(NumberUtility.truncate(work.getDuration(), 2), TimeUnit.MINUTES);
         }

         TimephasedResourceAssignment tra = new TimephasedResourceAssignment();
         tra.setStart(startDate);
         tra.setFinish(finishDate);
         tra.setTotalWork(work);

         result.add(tra);
      }

      return result;
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
      return (m_compatibleInput);
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
         CONTEXT = JAXBContext.newInstance("net.sf.mpxj.mspdi.schema", MSPDIReader.class.getClassLoader());
      }

      catch (JAXBException ex)
      {
         CONTEXT_EXCEPTION = ex;
         CONTEXT = null;
      }
   }

   private boolean m_compatibleInput = true;

   private ProjectFile m_projectFile;
}
