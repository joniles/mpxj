/*
 * file:       PlannerReader.java
 * author:     Jon Iles
 * copyright:  (c) Packwood Software 2007
 * date:       22 February 2007
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

package net.sf.mpxj.planner;

import java.io.InputStream;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.parsers.ParserConfigurationException;

import org.xml.sax.SAXException;

import net.sf.mpxj.ConstraintType;
import net.sf.mpxj.DateRange;
import net.sf.mpxj.Day;
import net.sf.mpxj.DayType;
import net.sf.mpxj.Duration;
import net.sf.mpxj.EventManager;
import net.sf.mpxj.MPXJException;
import net.sf.mpxj.Priority;
import net.sf.mpxj.ProjectCalendar;
import net.sf.mpxj.ProjectCalendarException;
import net.sf.mpxj.ProjectCalendarHours;
import net.sf.mpxj.ProjectConfig;
import net.sf.mpxj.ProjectFile;
import net.sf.mpxj.ProjectProperties;
import net.sf.mpxj.Relation;
import net.sf.mpxj.RelationType;
import net.sf.mpxj.Resource;
import net.sf.mpxj.ResourceAssignment;
import net.sf.mpxj.ResourceType;
import net.sf.mpxj.Task;
import net.sf.mpxj.TaskType;
import net.sf.mpxj.TimeUnit;
import net.sf.mpxj.common.DateHelper;
import net.sf.mpxj.common.NumberHelper;
import net.sf.mpxj.common.UnmarshalHelper;
import net.sf.mpxj.listener.ProjectListener;
import net.sf.mpxj.planner.schema.Allocation;
import net.sf.mpxj.planner.schema.Allocations;
import net.sf.mpxj.planner.schema.Calendars;
import net.sf.mpxj.planner.schema.Constraint;
import net.sf.mpxj.planner.schema.Days;
import net.sf.mpxj.planner.schema.DefaultWeek;
import net.sf.mpxj.planner.schema.Interval;
import net.sf.mpxj.planner.schema.OverriddenDayType;
import net.sf.mpxj.planner.schema.OverriddenDayTypes;
import net.sf.mpxj.planner.schema.Predecessor;
import net.sf.mpxj.planner.schema.Predecessors;
import net.sf.mpxj.planner.schema.Project;
import net.sf.mpxj.planner.schema.Resources;
import net.sf.mpxj.planner.schema.Tasks;
import net.sf.mpxj.reader.AbstractProjectReader;

/**
 * This class creates a new ProjectFile instance by reading a Planner file.
 */
public final class PlannerReader extends AbstractProjectReader
{
   /**
    * {@inheritDoc}
    */
   @Override public void addProjectListener(ProjectListener listener)
   {
      if (m_projectListeners == null)
      {
         m_projectListeners = new ArrayList<>();
      }
      m_projectListeners.add(listener);
   }

   /**
    * {@inheritDoc}
    */
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

         ProjectConfig config = m_projectFile.getProjectConfig();
         config.setAutoTaskUniqueID(false);
         config.setAutoResourceUniqueID(false);
         config.setAutoOutlineLevel(false);
         config.setAutoOutlineNumber(false);
         config.setAutoWBS(false);

         m_projectFile.getProjectProperties().setFileApplication("Planner");
         m_projectFile.getProjectProperties().setFileType("XML");

         m_eventManager.addProjectListeners(m_projectListeners);

         Project plannerProject = (Project) UnmarshalHelper.unmarshal(CONTEXT, stream);

         readProjectProperties(plannerProject);
         readCalendars(plannerProject);
         readResources(plannerProject);
         readTasks(plannerProject);
         readAssignments(plannerProject);

         //
         // Ensure that the unique ID counters are correct
         //
         config.updateUniqueCounters();

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
         m_defaultCalendar = null;
      }
   }

   /**
    * This method extracts project properties from a Planner file.
    *
    * @param project Root node of the Planner file
    */
   private void readProjectProperties(Project project) throws MPXJException
   {
      ProjectProperties properties = m_projectFile.getProjectProperties();

      properties.setCompany(project.getCompany());
      properties.setManager(project.getManager());
      properties.setName(project.getName());
      properties.setStartDate(getDateTime(project.getProjectStart()));
   }

   /**
    * This method extracts calendar data from a Planner file.
    *
    * @param project Root node of the Planner file
    */
   private void readCalendars(Project project) throws MPXJException
   {
      Calendars calendars = project.getCalendars();
      if (calendars != null)
      {
         for (net.sf.mpxj.planner.schema.Calendar cal : calendars.getCalendar())
         {
            readCalendar(cal, null);
         }

         Integer defaultCalendarID = getInteger(project.getCalendar());
         m_defaultCalendar = m_projectFile.getCalendarByUniqueID(defaultCalendarID);
         if (m_defaultCalendar != null)
         {
            m_projectFile.getProjectProperties().setDefaultCalendarName(m_defaultCalendar.getName());
         }
      }
   }

   /**
    * This method extracts data for a single calendar from a Planner file.
    *
    * @param plannerCalendar Calendar data
    * @param parentMpxjCalendar parent of derived calendar
    */
   private void readCalendar(net.sf.mpxj.planner.schema.Calendar plannerCalendar, ProjectCalendar parentMpxjCalendar) throws MPXJException
   {
      //
      // Create a calendar instance
      //
      ProjectCalendar mpxjCalendar = m_projectFile.addCalendar();

      //
      // Populate basic details
      //
      mpxjCalendar.setUniqueID(getInteger(plannerCalendar.getId()));
      mpxjCalendar.setName(plannerCalendar.getName());
      mpxjCalendar.setParent(parentMpxjCalendar);

      //
      // Set working and non working days
      //
      DefaultWeek dw = plannerCalendar.getDefaultWeek();
      setWorkingDay(mpxjCalendar, Day.MONDAY, dw.getMon());
      setWorkingDay(mpxjCalendar, Day.TUESDAY, dw.getTue());
      setWorkingDay(mpxjCalendar, Day.WEDNESDAY, dw.getWed());
      setWorkingDay(mpxjCalendar, Day.THURSDAY, dw.getThu());
      setWorkingDay(mpxjCalendar, Day.FRIDAY, dw.getFri());
      setWorkingDay(mpxjCalendar, Day.SATURDAY, dw.getSat());
      setWorkingDay(mpxjCalendar, Day.SUNDAY, dw.getSun());

      //
      // Set working hours
      //
      processWorkingHours(mpxjCalendar, plannerCalendar);

      //
      // Process exception days
      //
      processExceptionDays(mpxjCalendar, plannerCalendar);

      m_eventManager.fireCalendarReadEvent(mpxjCalendar);

      //
      // Process any derived calendars
      //
      List<net.sf.mpxj.planner.schema.Calendar> calendarList = plannerCalendar.getCalendar();
      for (net.sf.mpxj.planner.schema.Calendar cal : calendarList)
      {
         readCalendar(cal, mpxjCalendar);
      }
   }

   /**
    * Set the working/non-working status of a weekday.
    *
    * @param mpxjCalendar MPXJ calendar
    * @param mpxjDay day of the week
    * @param plannerDay planner day type
    */
   private void setWorkingDay(ProjectCalendar mpxjCalendar, Day mpxjDay, String plannerDay)
   {
      DayType dayType = DayType.DEFAULT;

      if (plannerDay != null)
      {
         switch (getInt(plannerDay))
         {
            case 0:
            {
               dayType = DayType.WORKING;
               break;
            }

            case 1:
            {
               dayType = DayType.NON_WORKING;
               break;
            }
         }
      }

      mpxjCalendar.setWorkingDay(mpxjDay, dayType);
   }

   /**
    * Add the appropriate working hours to each working day.
    *
    * @param mpxjCalendar MPXJ calendar
    * @param plannerCalendar Planner calendar
    */
   private void processWorkingHours(ProjectCalendar mpxjCalendar, net.sf.mpxj.planner.schema.Calendar plannerCalendar) throws MPXJException
   {
      OverriddenDayTypes types = plannerCalendar.getOverriddenDayTypes();
      if (types != null)
      {
         List<OverriddenDayType> typeList = types.getOverriddenDayType();
         Iterator<OverriddenDayType> iter = typeList.iterator();
         OverriddenDayType odt = null;
         while (iter.hasNext())
         {
            odt = iter.next();
            if (getInt(odt.getId()) != 0)
            {
               odt = null;
               continue;
            }

            break;
         }

         if (odt != null)
         {
            List<Interval> intervalList = odt.getInterval();
            ProjectCalendarHours mondayHours = null;
            ProjectCalendarHours tuesdayHours = null;
            ProjectCalendarHours wednesdayHours = null;
            ProjectCalendarHours thursdayHours = null;
            ProjectCalendarHours fridayHours = null;
            ProjectCalendarHours saturdayHours = null;
            ProjectCalendarHours sundayHours = null;

            if (mpxjCalendar.isWorkingDay(Day.MONDAY))
            {
               mondayHours = mpxjCalendar.addCalendarHours(Day.MONDAY);
            }

            if (mpxjCalendar.isWorkingDay(Day.TUESDAY))
            {
               tuesdayHours = mpxjCalendar.addCalendarHours(Day.TUESDAY);
            }

            if (mpxjCalendar.isWorkingDay(Day.WEDNESDAY))
            {
               wednesdayHours = mpxjCalendar.addCalendarHours(Day.WEDNESDAY);
            }

            if (mpxjCalendar.isWorkingDay(Day.THURSDAY))
            {
               thursdayHours = mpxjCalendar.addCalendarHours(Day.THURSDAY);
            }

            if (mpxjCalendar.isWorkingDay(Day.FRIDAY))
            {
               fridayHours = mpxjCalendar.addCalendarHours(Day.FRIDAY);
            }

            if (mpxjCalendar.isWorkingDay(Day.SATURDAY))
            {
               saturdayHours = mpxjCalendar.addCalendarHours(Day.SATURDAY);
            }

            if (mpxjCalendar.isWorkingDay(Day.SUNDAY))
            {
               sundayHours = mpxjCalendar.addCalendarHours(Day.SUNDAY);
            }

            for (Interval interval : intervalList)
            {
               Date startTime = getTime(interval.getStart());
               Date endTime = getTime(interval.getEnd());

               m_defaultWorkingHours.add(new DateRange(startTime, endTime));

               if (mondayHours != null)
               {
                  mondayHours.addRange(new DateRange(startTime, endTime));
               }

               if (tuesdayHours != null)
               {
                  tuesdayHours.addRange(new DateRange(startTime, endTime));
               }

               if (wednesdayHours != null)
               {
                  wednesdayHours.addRange(new DateRange(startTime, endTime));
               }

               if (thursdayHours != null)
               {
                  thursdayHours.addRange(new DateRange(startTime, endTime));
               }

               if (fridayHours != null)
               {
                  fridayHours.addRange(new DateRange(startTime, endTime));
               }

               if (saturdayHours != null)
               {
                  saturdayHours.addRange(new DateRange(startTime, endTime));
               }

               if (sundayHours != null)
               {
                  sundayHours.addRange(new DateRange(startTime, endTime));
               }
            }
         }
      }
   }

   /**
    * Process exception days.
    *
    * @param mpxjCalendar MPXJ calendar
    * @param plannerCalendar Planner calendar
    */
   private void processExceptionDays(ProjectCalendar mpxjCalendar, net.sf.mpxj.planner.schema.Calendar plannerCalendar) throws MPXJException
   {
      Days days = plannerCalendar.getDays();
      if (days != null)
      {
         List<net.sf.mpxj.planner.schema.Day> dayList = days.getDay();
         for (net.sf.mpxj.planner.schema.Day day : dayList)
         {
            if (day.getType().equals("day-type"))
            {
               Date exceptionDate = getDate(day.getDate());
               ProjectCalendarException exception = mpxjCalendar.addCalendarException(exceptionDate, exceptionDate);
               if (getInt(day.getId()) == 0)
               {
                  for (int hoursIndex = 0; hoursIndex < m_defaultWorkingHours.size(); hoursIndex++)
                  {
                     DateRange range = m_defaultWorkingHours.get(hoursIndex);
                     exception.addRange(range);
                  }
               }
            }
         }
      }
   }

   /**
    * This method extracts resource data from a Planner file.
    *
    * @param plannerProject Root node of the Planner file
    */
   private void readResources(Project plannerProject) throws MPXJException
   {
      Resources resources = plannerProject.getResources();
      if (resources != null)
      {
         for (net.sf.mpxj.planner.schema.Resource res : resources.getResource())
         {
            readResource(res);
         }
      }
   }

   /**
    * This method extracts data for a single resource from a Planner file.
    *
    * @param plannerResource Resource data
    */
   private void readResource(net.sf.mpxj.planner.schema.Resource plannerResource) throws MPXJException
   {
      Resource mpxjResource = m_projectFile.addResource();

      //mpxjResource.setResourceCalendar(m_projectFile.getBaseCalendarByUniqueID(getInteger(plannerResource.getCalendar())));
      mpxjResource.setEmailAddress(plannerResource.getEmail());
      mpxjResource.setUniqueID(getInteger(plannerResource.getId()));
      mpxjResource.setName(plannerResource.getName());
      mpxjResource.setNotes(plannerResource.getNote());
      mpxjResource.setInitials(plannerResource.getShortName());
      mpxjResource.setType(getInt(plannerResource.getType()) == 2 ? ResourceType.MATERIAL : ResourceType.WORK);
      //plannerResource.getStdRate();
      //plannerResource.getOvtRate();
      //plannerResource.getUnits();
      //plannerResource.getProperties();

      ProjectCalendar calendar = mpxjResource.addResourceCalendar();

      calendar.setWorkingDay(Day.SUNDAY, DayType.DEFAULT);
      calendar.setWorkingDay(Day.MONDAY, DayType.DEFAULT);
      calendar.setWorkingDay(Day.TUESDAY, DayType.DEFAULT);
      calendar.setWorkingDay(Day.WEDNESDAY, DayType.DEFAULT);
      calendar.setWorkingDay(Day.THURSDAY, DayType.DEFAULT);
      calendar.setWorkingDay(Day.FRIDAY, DayType.DEFAULT);
      calendar.setWorkingDay(Day.SATURDAY, DayType.DEFAULT);

      ProjectCalendar baseCalendar = m_projectFile.getCalendarByUniqueID(getInteger(plannerResource.getCalendar()));
      if (baseCalendar == null)
      {
         baseCalendar = m_defaultCalendar;
      }
      calendar.setParent(baseCalendar);

      m_eventManager.fireResourceReadEvent(mpxjResource);
   }

   /**
    * This method extracts task data from a Planner file.
    *
    * @param plannerProject Root node of the Planner file
    */
   private void readTasks(Project plannerProject) throws MPXJException
   {
      Tasks tasks = plannerProject.getTasks();
      if (tasks != null)
      {
         for (net.sf.mpxj.planner.schema.Task task : tasks.getTask())
         {
            readTask(null, task);
         }

         for (net.sf.mpxj.planner.schema.Task task : tasks.getTask())
         {
            readPredecessors(task);
         }
      }

      m_projectFile.updateStructure();
   }

   /**
    * This method extracts data for a single task from a Planner file.
    *
    * @param parentTask parent task
    * @param plannerTask Task data
    */
   private void readTask(Task parentTask, net.sf.mpxj.planner.schema.Task plannerTask) throws MPXJException
   {
      Task mpxjTask;

      if (parentTask == null)
      {
         mpxjTask = m_projectFile.addTask();
         mpxjTask.setOutlineLevel(Integer.valueOf(1));
      }
      else
      {
         mpxjTask = parentTask.addTask();
         mpxjTask.setOutlineLevel(Integer.valueOf(parentTask.getOutlineLevel().intValue() + 1));
      }

      //
      // Read task attributes from Planner
      //
      Integer percentComplete = getInteger(plannerTask.getPercentComplete());
      //plannerTask.getDuration(); calculate from end - start, not in file?
      //plannerTask.getEffort(); not set?
      mpxjTask.setFinish(getDateTime(plannerTask.getEnd()));
      mpxjTask.setUniqueID(getInteger(plannerTask.getId()));
      mpxjTask.setName(plannerTask.getName());
      mpxjTask.setNotes(plannerTask.getNote());
      mpxjTask.setPercentageComplete(percentComplete);
      mpxjTask.setPercentageWorkComplete(percentComplete);
      mpxjTask.setPriority(Priority.getInstance(getInt(plannerTask.getPriority()) / 10));
      mpxjTask.setType(getTaskType(plannerTask.getScheduling()));
      //plannerTask.getStart(); // Start day, time is always 00:00?
      mpxjTask.setMilestone(plannerTask.getType().equals("milestone"));

      mpxjTask.setWork(getDuration(plannerTask.getWork()));

      mpxjTask.setStart(getDateTime(plannerTask.getWorkStart()));

      //
      // Read constraint
      //
      ConstraintType mpxjConstraintType = ConstraintType.AS_SOON_AS_POSSIBLE;
      Constraint constraint = plannerTask.getConstraint();
      if (constraint != null)
      {
         if (constraint.getType().equals("start-no-earlier-than"))
         {
            mpxjConstraintType = ConstraintType.START_NO_EARLIER_THAN;
         }
         else
         {
            if (constraint.getType().equals("must-start-on"))
            {
               mpxjConstraintType = ConstraintType.MUST_START_ON;
            }
         }

         mpxjTask.setConstraintDate(getDateTime(constraint.getTime()));
      }
      mpxjTask.setConstraintType(mpxjConstraintType);

      //
      // Calculate missing attributes
      //
      ProjectCalendar calendar = m_projectFile.getDefaultCalendar();
      if (calendar != null)
      {
         Duration duration = calendar.getWork(mpxjTask.getStart(), mpxjTask.getFinish(), TimeUnit.HOURS);
         double durationDays = duration.getDuration() / 8;
         if (durationDays > 0)
         {
            duration = Duration.getInstance(durationDays, TimeUnit.DAYS);
         }
         mpxjTask.setDuration(duration);

         if (percentComplete.intValue() != 0)
         {
            mpxjTask.setActualStart(mpxjTask.getStart());

            if (percentComplete.intValue() == 100)
            {
               mpxjTask.setActualFinish(mpxjTask.getFinish());
               mpxjTask.setActualDuration(duration);
               mpxjTask.setActualWork(mpxjTask.getWork());
               mpxjTask.setRemainingWork(Duration.getInstance(0, TimeUnit.HOURS));
            }
            else
            {
               Duration work = mpxjTask.getWork();
               Duration actualWork = Duration.getInstance((work.getDuration() * percentComplete.doubleValue()) / 100.0d, work.getUnits());

               mpxjTask.setActualDuration(Duration.getInstance((duration.getDuration() * percentComplete.doubleValue()) / 100.0d, duration.getUnits()));
               mpxjTask.setActualWork(actualWork);
               mpxjTask.setRemainingWork(Duration.getInstance(work.getDuration() - actualWork.getDuration(), work.getUnits()));
            }
         }
      }
      mpxjTask.setEffortDriven(true);

      m_eventManager.fireTaskReadEvent(mpxjTask);

      //
      // Process child tasks
      //
      List<net.sf.mpxj.planner.schema.Task> childTasks = plannerTask.getTask();
      for (net.sf.mpxj.planner.schema.Task childTask : childTasks)
      {
         readTask(mpxjTask, childTask);
      }
   }

   /**
    * This method extracts predecessor data from a Planner file.
    *
    * @param plannerTask Task data
    */
   private void readPredecessors(net.sf.mpxj.planner.schema.Task plannerTask)
   {
      Task mpxjTask = m_projectFile.getTaskByUniqueID(getInteger(plannerTask.getId()));

      Predecessors predecessors = plannerTask.getPredecessors();
      if (predecessors != null)
      {
         List<Predecessor> predecessorList = predecessors.getPredecessor();
         for (Predecessor predecessor : predecessorList)
         {
            Integer predecessorID = getInteger(predecessor.getPredecessorId());
            Task predecessorTask = m_projectFile.getTaskByUniqueID(predecessorID);
            if (predecessorTask != null)
            {
               Duration lag = getDuration(predecessor.getLag());
               if (lag == null)
               {
                  lag = Duration.getInstance(0, TimeUnit.HOURS);
               }
               Relation relation = mpxjTask.addPredecessor(predecessorTask, RELATIONSHIP_TYPES.get(predecessor.getType()), lag);
               m_eventManager.fireRelationReadEvent(relation);
            }
         }
      }

      //
      // Process child tasks
      //
      List<net.sf.mpxj.planner.schema.Task> childTasks = plannerTask.getTask();
      for (net.sf.mpxj.planner.schema.Task childTask : childTasks)
      {
         readPredecessors(childTask);
      }
   }

   /**
    * This method extracts assignment data from a Planner file.
    *
    * @param plannerProject Root node of the Planner file
    */
   private void readAssignments(Project plannerProject)
   {
      Allocations allocations = plannerProject.getAllocations();
      List<Allocation> allocationList = allocations.getAllocation();
      Set<Task> tasksWithAssignments = new HashSet<>();

      for (Allocation allocation : allocationList)
      {
         Integer taskID = getInteger(allocation.getTaskId());
         Integer resourceID = getInteger(allocation.getResourceId());
         Integer units = getInteger(allocation.getUnits());

         Task task = m_projectFile.getTaskByUniqueID(taskID);
         Resource resource = m_projectFile.getResourceByUniqueID(resourceID);

         if (task != null && resource != null)
         {
            Duration work = task.getWork();
            int percentComplete = NumberHelper.getInt(task.getPercentageComplete());

            ResourceAssignment assignment = task.addResourceAssignment(resource);
            assignment.setUnits(units);
            assignment.setWork(work);

            if (percentComplete != 0)
            {
               Duration actualWork = Duration.getInstance((work.getDuration() * percentComplete) / 100, work.getUnits());
               assignment.setActualWork(actualWork);
               assignment.setRemainingWork(Duration.getInstance(work.getDuration() - actualWork.getDuration(), work.getUnits()));
            }
            else
            {
               assignment.setRemainingWork(work);
            }

            assignment.setStart(task.getStart());
            assignment.setFinish(task.getFinish());

            tasksWithAssignments.add(task);

            m_eventManager.fireAssignmentReadEvent(assignment);
         }
      }

      //
      // Adjust work per assignment for tasks with multiple assignments
      //
      for (Task task : tasksWithAssignments)
      {
         List<ResourceAssignment> assignments = task.getResourceAssignments();
         if (assignments.size() > 1)
         {
            double maxUnits = 0;
            for (ResourceAssignment assignment : assignments)
            {
               maxUnits += assignment.getUnits().doubleValue();
            }

            for (ResourceAssignment assignment : assignments)
            {
               Duration work = assignment.getWork();
               double factor = assignment.getUnits().doubleValue() / maxUnits;

               work = Duration.getInstance(work.getDuration() * factor, work.getUnits());
               assignment.setWork(work);
               Duration actualWork = assignment.getActualWork();
               if (actualWork != null)
               {
                  actualWork = Duration.getInstance(actualWork.getDuration() * factor, actualWork.getUnits());
                  assignment.setActualWork(actualWork);
               }

               Duration remainingWork = assignment.getRemainingWork();
               if (remainingWork != null)
               {
                  remainingWork = Duration.getInstance(remainingWork.getDuration() * factor, remainingWork.getUnits());
                  assignment.setRemainingWork(remainingWork);
               }
            }
         }
      }
   }

   /**
    * Convert a Planner date-time value into a Java date.
    *
    * 20070222T080000Z
    *
    * @param value Planner date-time
    * @return Java Date instance
    */
   private Date getDateTime(String value) throws MPXJException
   {
      try
      {
         Number year = m_fourDigitFormat.parse(value.substring(0, 4));
         Number month = m_twoDigitFormat.parse(value.substring(4, 6));
         Number day = m_twoDigitFormat.parse(value.substring(6, 8));

         Number hours = m_twoDigitFormat.parse(value.substring(9, 11));
         Number minutes = m_twoDigitFormat.parse(value.substring(11, 13));

         Calendar cal = DateHelper.popCalendar();
         cal.set(Calendar.YEAR, year.intValue());
         cal.set(Calendar.MONTH, month.intValue() - 1);
         cal.set(Calendar.DAY_OF_MONTH, day.intValue());

         cal.set(Calendar.HOUR_OF_DAY, hours.intValue());
         cal.set(Calendar.MINUTE, minutes.intValue());

         cal.set(Calendar.SECOND, 0);
         cal.set(Calendar.MILLISECOND, 0);
         Date result = cal.getTime();
         DateHelper.pushCalendar(cal);

         return result;
      }

      catch (ParseException ex)
      {
         throw new MPXJException("Failed to parse date-time " + value, ex);
      }
   }

   /**
    * Convert a Planner date into a Java date.
    *
    * 20070222
    *
    * @param value Planner date
    * @return Java Date instance
    */
   private Date getDate(String value) throws MPXJException
   {
      try
      {
         Number year = m_fourDigitFormat.parse(value.substring(0, 4));
         Number month = m_twoDigitFormat.parse(value.substring(4, 6));
         Number day = m_twoDigitFormat.parse(value.substring(6, 8));

         Calendar cal = DateHelper.popCalendar();
         cal.set(Calendar.YEAR, year.intValue());
         cal.set(Calendar.MONTH, month.intValue() - 1);
         cal.set(Calendar.DAY_OF_MONTH, day.intValue());

         cal.set(Calendar.HOUR_OF_DAY, 0);
         cal.set(Calendar.MINUTE, 0);
         cal.set(Calendar.SECOND, 0);
         cal.set(Calendar.MILLISECOND, 0);
         Date result = cal.getTime();
         DateHelper.pushCalendar(cal);

         return result;
      }

      catch (ParseException ex)
      {
         throw new MPXJException("Failed to parse date " + value, ex);
      }
   }

   /**
    * Convert a Planner time into a Java date.
    *
    * 0800
    *
    * @param value Planner time
    * @return Java Date instance
    */
   private Date getTime(String value) throws MPXJException
   {
      try
      {
         Number hours = m_twoDigitFormat.parse(value.substring(0, 2));
         Number minutes = m_twoDigitFormat.parse(value.substring(2, 4));

         Calendar cal = DateHelper.popCalendar();
         cal.set(Calendar.HOUR_OF_DAY, hours.intValue());
         cal.set(Calendar.MINUTE, minutes.intValue());
         cal.set(Calendar.SECOND, 0);
         cal.set(Calendar.MILLISECOND, 0);
         Date result = cal.getTime();
         DateHelper.pushCalendar(cal);

         return result;
      }

      catch (ParseException ex)
      {
         throw new MPXJException("Failed to parse time " + value, ex);
      }
   }

   /**
    * Convert a string into an Integer.
    *
    * @param value integer represented as a string
    * @return Integer instance
    */
   private Integer getInteger(String value)
   {
      return (NumberHelper.getInteger(value));
   }

   /**
    * Convert a string into an int.
    *
    * @param value integer represented as a string
    * @return int value
    */
   private int getInt(String value)
   {
      return (Integer.parseInt(value));
   }

   /**
    * Convert a string into a long.
    *
    * @param value long represented as a string
    * @return long value
    */
   private long getLong(String value)
   {
      return (Long.parseLong(value));
   }

   /**
    * Convert a string representation of the task type
    * into a TaskType instance.
    *
    * @param value string value
    * @return TaskType value
    */
   private TaskType getTaskType(String value)
   {
      TaskType result = TaskType.FIXED_UNITS;
      if (value != null && value.equals("fixed-duration"))
      {
         result = TaskType.FIXED_DURATION;
      }
      return (result);
   }

   /**
    * Converts the string representation of a Planner duration into
    * an MPXJ Duration instance.
    *
    * Planner represents durations as a number of seconds in its
    * file format, however it displays durations as days and hours,
    * and seems to assume that a working day is 8 hours.
    *
    * @param value string representation of a duration
    * @return Duration instance
    */
   private Duration getDuration(String value)
   {
      Duration result = null;

      if (value != null && value.length() != 0)
      {
         double seconds = getLong(value);
         double hours = seconds / (60 * 60);
         double days = hours / 8;

         if (days < 1)
         {
            result = Duration.getInstance(hours, TimeUnit.HOURS);
         }
         else
         {
            double durationDays = hours / 8;
            result = Duration.getInstance(durationDays, TimeUnit.DAYS);
         }
      }

      return (result);
   }

   private ProjectFile m_projectFile;
   private EventManager m_eventManager;
   private ProjectCalendar m_defaultCalendar;
   private NumberFormat m_twoDigitFormat = new DecimalFormat("00");
   private NumberFormat m_fourDigitFormat = new DecimalFormat("0000");
   private List<DateRange> m_defaultWorkingHours = new ArrayList<>();
   private List<ProjectListener> m_projectListeners;

   private static Map<String, RelationType> RELATIONSHIP_TYPES = new HashMap<>();
   static
   {
      RELATIONSHIP_TYPES.put("FF", RelationType.FINISH_FINISH);
      RELATIONSHIP_TYPES.put("FS", RelationType.FINISH_START);
      RELATIONSHIP_TYPES.put("SF", RelationType.START_FINISH);
      RELATIONSHIP_TYPES.put("SS", RelationType.START_START);
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
         CONTEXT = JAXBContext.newInstance("net.sf.mpxj.planner.schema", PlannerReader.class.getClassLoader());
      }

      catch (JAXBException ex)
      {
         CONTEXT_EXCEPTION = ex;
         CONTEXT = null;
      }
   }
}
