/*
 * file:       ConceptDrawProjectReader.java
 * author:     Jon Iles
 * copyright:  (c) Packwood Software 2018
 * date:       9 July 2018
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

package org.mpxj.conceptdraw;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.JAXBException;
import javax.xml.parsers.ParserConfigurationException;

import org.mpxj.CostRateTable;
import org.mpxj.CostRateTableEntry;
import org.mpxj.LocalTimeRange;
import org.mpxj.common.LocalDateTimeHelper;
import org.mpxj.common.NumberHelper;
import org.xml.sax.SAXException;

import org.mpxj.Duration;
import org.mpxj.EventManager;
import org.mpxj.MPXJException;
import org.mpxj.ProjectCalendar;
import org.mpxj.ProjectCalendarException;
import org.mpxj.ProjectCalendarHours;
import org.mpxj.ProjectConfig;
import org.mpxj.ProjectFile;
import org.mpxj.ProjectProperties;
import org.mpxj.Rate;
import org.mpxj.Relation;
import org.mpxj.Resource;
import org.mpxj.ResourceAssignment;
import org.mpxj.Task;
import org.mpxj.TimeUnit;
import org.mpxj.common.AlphanumComparator;
import org.mpxj.common.UnmarshalHelper;
import org.mpxj.conceptdraw.schema.Document;
import org.mpxj.conceptdraw.schema.Document.Calendars.Calendar;
import org.mpxj.conceptdraw.schema.Document.Calendars.Calendar.ExceptedDays.ExceptedDay;
import org.mpxj.conceptdraw.schema.Document.Calendars.Calendar.WeekDays.WeekDay;
import org.mpxj.conceptdraw.schema.Document.Links.Link;
import org.mpxj.conceptdraw.schema.Document.Projects.Project;
import org.mpxj.conceptdraw.schema.Document.WorkspaceProperties;
import org.mpxj.reader.AbstractProjectStreamReader;

/**
 * This class creates a new ProjectFile instance by reading a ConceptDraw Project file.
 */
public final class ConceptDrawProjectReader extends AbstractProjectStreamReader
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
         m_calendarMap = new HashMap<>();
         m_taskIdMap = new HashMap<>();

         ProjectConfig config = m_projectFile.getProjectConfig();
         config.setAutoResourceUniqueID(false);
         config.setAutoResourceID(false);
         config.setAutoRelationUniqueID(false);
         config.setAutoAssignmentUniqueID(false);

         m_projectFile.getProjectProperties().setFileApplication("ConceptDraw PROJECT");
         m_projectFile.getProjectProperties().setFileType("CDP");

         addListenersToProject(m_projectFile);

         Document cdp = (Document) UnmarshalHelper.unmarshal(CONTEXT, stream, new NamespaceFilter());

         readProjectProperties(cdp);
         readCalendars(cdp);
         readResources(cdp);
         readTasks(cdp);
         readRelationships(cdp);
         m_projectFile.readComplete();

         return m_projectFile;
      }

      catch (ParserConfigurationException | IOException | SAXException | JAXBException ex)
      {
         throw new MPXJException("Failed to parse file", ex);
      }

      finally
      {
         m_projectFile = null;
         m_eventManager = null;
         m_calendarMap = null;
         m_taskIdMap = null;
      }
   }

   /**
    * Extracts project properties from a ConceptDraw PROJECT file.
    *
    * @param cdp ConceptDraw PROJECT file
    */
   private void readProjectProperties(Document cdp)
   {
      WorkspaceProperties props = cdp.getWorkspaceProperties();
      ProjectProperties mpxjProps = m_projectFile.getProjectProperties();
      mpxjProps.setSymbolPosition(props.getCurrencyPosition());
      mpxjProps.setCurrencyDigits(props.getCurrencyDigits());
      mpxjProps.setCurrencySymbol(props.getCurrencySymbol());
      mpxjProps.setDaysPerMonth(props.getDaysPerMonth());
      // getHoursPerDay() actually returns minutes per day
      mpxjProps.setMinutesPerDay(props.getHoursPerDay());
      mpxjProps.setMinutesPerWeek(Integer.valueOf(NumberHelper.getInt(props.getHoursPerWeek()) * 60));

      m_workHoursPerDay = mpxjProps.getMinutesPerDay().doubleValue() / 60.0;
   }

   /**
   * Extracts calendar data from a ConceptDraw PROJECT file.
   *
   * @param cdp ConceptDraw PROJECT file
   */
   private void readCalendars(Document cdp)
   {
      for (Calendar calendar : cdp.getCalendars().getCalendar())
      {
         readCalendar(calendar);
      }

      for (Calendar calendar : cdp.getCalendars().getCalendar())
      {
         ProjectCalendar child = m_calendarMap.get(calendar.getID());
         ProjectCalendar parent = m_calendarMap.get(calendar.getBaseCalendarID());
         if (parent == null)
         {
            m_projectFile.setDefaultCalendar(child);
         }
         else
         {
            child.setParent(parent);
         }
      }
   }

   /**
    * Read a calendar.
    *
    * @param calendar ConceptDraw PROJECT calendar
    */
   private void readCalendar(Calendar calendar)
   {
      ProjectCalendar mpxjCalendar = m_projectFile.addCalendar();
      mpxjCalendar.setName(calendar.getName());
      m_calendarMap.put(calendar.getID(), mpxjCalendar);

      for (WeekDay day : calendar.getWeekDays().getWeekDay())
      {
         readWeekDay(mpxjCalendar, day);
      }

      for (ExceptedDay day : calendar.getExceptedDays().getExceptedDay())
      {
         readExceptionDay(mpxjCalendar, day);
      }

      m_eventManager.fireCalendarReadEvent(mpxjCalendar);
   }

   /**
    * Reads a single day for a calendar.
    *
    * @param mpxjCalendar ProjectCalendar instance
    * @param day ConceptDraw PROJECT week day
    */
   private void readWeekDay(ProjectCalendar mpxjCalendar, WeekDay day)
   {
      mpxjCalendar.setWorkingDay(day.getDay(), day.isIsDayWorking());
      ProjectCalendarHours hours = mpxjCalendar.addCalendarHours(day.getDay());
      if (day.isIsDayWorking())
      {
         for (Document.Calendars.Calendar.WeekDays.WeekDay.TimePeriods.TimePeriod period : day.getTimePeriods().getTimePeriod())
         {
            hours.add(new LocalTimeRange(period.getFrom(), period.getTo()));
         }
      }
   }

   /**
    * Read an exception day for a calendar.
    *
    * @param mpxjCalendar ProjectCalendar instance
    * @param day ConceptDraw PROJECT exception day
    */
   private void readExceptionDay(ProjectCalendar mpxjCalendar, ExceptedDay day)
   {
      ProjectCalendarException mpxjException = mpxjCalendar.addCalendarException(day.getDate());
      if (day.isIsDayWorking())
      {
         for (Document.Calendars.Calendar.ExceptedDays.ExceptedDay.TimePeriods.TimePeriod period : day.getTimePeriods().getTimePeriod())
         {
            mpxjException.add(new LocalTimeRange(period.getFrom(), period.getTo()));
         }
      }
   }

   /**
    * Reads resource data from a ConceptDraw PROJECT file.
    *
    * @param cdp ConceptDraw PROJECT file
    */
   private void readResources(Document cdp)
   {
      for (Document.Resources.Resource resource : cdp.getResources().getResource())
      {
         readResource(resource);
      }
   }

   /**
    * Reads a single resource from a ConceptDraw PROJECT file.
    *
    * @param resource ConceptDraw PROJECT resource
    */
   private void readResource(Document.Resources.Resource resource)
   {
      Resource mpxjResource = m_projectFile.addResource();
      mpxjResource.setName(resource.getName());
      mpxjResource.setCalendar(m_calendarMap.get(resource.getCalendarID()));
      mpxjResource.setEmailAddress(resource.getEMail());
      mpxjResource.setGroup(resource.getGroup());
      //resource.getHyperlinks()
      mpxjResource.setUniqueID(resource.getID());
      //resource.getMarkerID()
      mpxjResource.setNotes(resource.getNote());
      mpxjResource.setID(Integer.valueOf(resource.getOutlineNumber()));
      //resource.getStyleProject()
      mpxjResource.setType(resource.getSubType() == null ? resource.getType() : resource.getSubType());

      CostRateTable table = new CostRateTable();
      table.add(new CostRateTableEntry(LocalDateTimeHelper.START_DATE_NA, LocalDateTimeHelper.END_DATE_NA, NumberHelper.DOUBLE_ZERO, new Rate(resource.getCost(), resource.getCostTimeUnit())));
      mpxjResource.setCostRateTable(0, table);

      m_eventManager.fireResourceReadEvent(mpxjResource);
   }

   /**
    * Read the projects from a ConceptDraw PROJECT file as top level tasks.
    *
    * @param cdp ConceptDraw PROJECT file
    */
   private void readTasks(Document cdp)
   {
      for (Project project : cdp.getProjects().getProject())
      {
         readProject(project);
      }
   }

   /**
    * Read a project from a ConceptDraw PROJECT file.
    *
    * @param project ConceptDraw PROJECT project
    */
   private void readProject(Project project)
   {
      Task mpxjTask = m_projectFile.addTask();
      //project.getAuthor()
      mpxjTask.setBaselineCost(project.getBaselineCost());
      mpxjTask.setBaselineFinish(project.getBaselineFinishDate());
      mpxjTask.setBaselineStart(project.getBaselineStartDate());
      //project.getBudget();
      //project.getCompany()
      mpxjTask.setFinish(project.getFinishDate());
      //project.getGoal()
      //project.getHyperlinks()
      //project.getMarkerID()
      mpxjTask.setName(project.getName());
      mpxjTask.setNotes(project.getNote());
      mpxjTask.setPriority(project.getPriority());
      //      project.getSite()
      mpxjTask.setStart(project.getStartDate());
      //      project.getStyleProject()
      //      project.getTask()
      //      project.getTimeScale()
      //      project.getViewProperties()

      String projectIdentifier = project.getID().toString();
      mpxjTask.setGUID(UUID.nameUUIDFromBytes(projectIdentifier.getBytes()));

      //
      // Sort the tasks into the correct order
      //
      List<Document.Projects.Project.Task> tasks = new ArrayList<>(project.getTask());
      final AlphanumComparator comparator = new AlphanumComparator();

      tasks.sort((o1, o2) -> comparator.compare(o1.getOutlineNumber(), o2.getOutlineNumber()));

      Map<String, Task> map = new HashMap<>();
      map.put("", mpxjTask);

      for (Document.Projects.Project.Task task : tasks)
      {
         readTask(projectIdentifier, map, task);
      }
   }

   /**
    * Read a task from a ConceptDraw PROJECT file.
    *
    * @param projectIdentifier parent project identifier
    * @param map outline number to task map
    * @param task ConceptDraw PROJECT task
    */
   private void readTask(String projectIdentifier, Map<String, Task> map, Document.Projects.Project.Task task)
   {
      Task parentTask = map.get(getParentOutlineNumber(task.getOutlineNumber()));
      Task mpxjTask = parentTask.addTask();

      TimeUnit units = task.getBaseDurationTimeUnit();

      mpxjTask.setCost(task.getActualCost());
      mpxjTask.setDuration(getDuration(units, task.getActualDuration()));
      mpxjTask.setFinish(task.getActualFinishDate());
      mpxjTask.setStart(task.getActualStartDate());
      mpxjTask.setBaselineDuration(getDuration(units, task.getBaseDuration()));
      mpxjTask.setBaselineFinish(task.getBaseFinishDate());
      mpxjTask.setBaselineCost(task.getBaselineCost());
      //      task.getBaselineFinishDate()
      //      task.getBaselineFinishTemplateOffset()
      //      task.getBaselineStartDate()
      //      task.getBaselineStartTemplateOffset()
      mpxjTask.setBaselineStart(task.getBaseStartDate());
      //      task.getCallouts()
      mpxjTask.setPercentageComplete(task.getComplete());
      mpxjTask.setDeadline(task.getDeadlineDate());
      //      task.getDeadlineTemplateOffset()
      //      task.getHyperlinks()
      //      task.getMarkerID()
      mpxjTask.setName(task.getName());
      mpxjTask.setNotes(task.getNote());
      mpxjTask.setPriority(task.getPriority());
      //      task.getRecalcBase1()
      //      task.getRecalcBase2()
      mpxjTask.setType(task.getSchedulingType());
      //      task.getStyleProject()
      //      task.getTemplateOffset()
      //      task.getValidatedByProject()

      if (task.isIsMilestone())
      {
         mpxjTask.setMilestone(true);
         mpxjTask.setDuration(Duration.getInstance(0, TimeUnit.HOURS));
         mpxjTask.setBaselineDuration(Duration.getInstance(0, TimeUnit.HOURS));
      }

      String taskIdentifier = projectIdentifier + "." + task.getID();
      m_taskIdMap.put(task.getID(), mpxjTask);
      mpxjTask.setGUID(UUID.nameUUIDFromBytes(taskIdentifier.getBytes()));

      map.put(task.getOutlineNumber(), mpxjTask);

      m_eventManager.fireTaskReadEvent(mpxjTask);

      for (Document.Projects.Project.Task.ResourceAssignments.ResourceAssignment assignment : task.getResourceAssignments().getResourceAssignment())
      {
         readResourceAssignment(mpxjTask, assignment);
      }
   }

   /**
    * Read resource assignments.
    *
    * @param task Parent task
    * @param assignment ConceptDraw PROJECT resource assignment
    */
   private void readResourceAssignment(Task task, Document.Projects.Project.Task.ResourceAssignments.ResourceAssignment assignment)
   {
      Resource resource = m_projectFile.getResourceByUniqueID(assignment.getResourceID());
      if (resource != null)
      {
         ResourceAssignment mpxjAssignment = task.addResourceAssignment(resource);
         mpxjAssignment.setUniqueID(assignment.getID());
         mpxjAssignment.setWork(Duration.getInstance(assignment.getManHour().doubleValue() * m_workHoursPerDay, TimeUnit.HOURS));
         mpxjAssignment.setUnits(assignment.getUse());
         m_eventManager.fireAssignmentReadEvent(mpxjAssignment);
      }
   }

   /**
    * Read all task relationships from a ConceptDraw PROJECT file.
    *
    * @param cdp ConceptDraw PROJECT file
    */
   private void readRelationships(Document cdp)
   {
      for (Link link : cdp.getLinks().getLink())
      {
         readRelationship(link);
      }
   }

   /**
    * Read a task relationship.
    *
    * @param link ConceptDraw PROJECT task link
    */
   private void readRelationship(Link link)
   {
      Task sourceTask = m_taskIdMap.get(link.getSourceTaskID());
      Task destinationTask = m_taskIdMap.get(link.getDestinationTaskID());
      if (sourceTask != null && destinationTask != null)
      {
         Relation relation = destinationTask.addPredecessor(new Relation.Builder()
            .predecessorTask(sourceTask)
            .type(link.getType())
            .lag(getDuration(link.getLagUnit(), link.getLag()))
            .uniqueID(link.getID()));

         m_eventManager.fireRelationReadEvent(relation);
      }
   }

   /**
    * Read a duration.
    *
    * @param units duration units
    * @param duration duration value
    * @return Duration instance
    */
   private Duration getDuration(TimeUnit units, Double duration)
   {
      Duration result = null;
      if (duration != null)
      {
         double durationValue = duration.doubleValue() * 100.0;

         switch (units)
         {
            case MINUTES:
            {
               durationValue *= MINUTES_PER_DAY;
               break;
            }

            case HOURS:
            {
               durationValue *= HOURS_PER_DAY;
               break;
            }

            case DAYS:
            {
               durationValue *= 3.0;
               break;
            }

            case WEEKS:
            {
               durationValue *= 0.6;
               break;
            }

            case MONTHS:
            {
               durationValue *= 0.15;
               break;
            }

            default:
            {
               throw new IllegalArgumentException("Unsupported time units " + units);
            }
         }

         durationValue = Math.round(durationValue) / 100.0;

         result = Duration.getInstance(durationValue, units);
      }

      return result;
   }

   /**
    * Return the parent outline number, or an empty string if
    * we have a root task.
    *
    * @param outlineNumber child outline number
    * @return parent outline number
    */
   private String getParentOutlineNumber(String outlineNumber)
   {
      String result;
      int index = outlineNumber.lastIndexOf('.');
      if (index == -1)
      {
         result = "";
      }
      else
      {
         result = outlineNumber.substring(0, index);
      }
      return result;
   }

   private ProjectFile m_projectFile;
   private EventManager m_eventManager;
   private Map<Integer, ProjectCalendar> m_calendarMap;
   private Map<Integer, Task> m_taskIdMap;
   private double m_workHoursPerDay;

   private static final int HOURS_PER_DAY = 24;
   private static final int MINUTES_PER_DAY = HOURS_PER_DAY * 60;

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
         CONTEXT = JAXBContext.newInstance("org.mpxj.conceptdraw.schema", ConceptDrawProjectReader.class.getClassLoader());
      }

      catch (JAXBException ex)
      {
         CONTEXT_EXCEPTION = ex;
         CONTEXT = null;
      }
   }
}
