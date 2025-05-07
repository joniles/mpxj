/*
 * file:       PlannerWriter.java
 * author:     Jon Iles
 * copyright:  (c) Packwood Software 2005
 * date:       Mar 16, 2007
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

package org.mpxj.planner;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.JAXBException;
import jakarta.xml.bind.Marshaller;

import org.mpxj.ConstraintType;
import java.time.DayOfWeek;
import org.mpxj.common.DayOfWeekHelper;
import org.mpxj.Duration;
import org.mpxj.EventManager;
import org.mpxj.ProjectCalendar;
import org.mpxj.ProjectCalendarException;
import org.mpxj.ProjectCalendarHours;
import org.mpxj.ProjectFile;
import org.mpxj.ProjectProperties;
import org.mpxj.Relation;
import org.mpxj.RelationType;
import org.mpxj.Resource;
import org.mpxj.ResourceAssignment;
import org.mpxj.ResourceType;
import org.mpxj.Task;
import org.mpxj.TaskType;
import org.mpxj.LocalTimeRange;
import org.mpxj.TimeUnit;
import org.mpxj.common.LocalDateTimeHelper;
import org.mpxj.common.MarshallerHelper;
import org.mpxj.common.NumberHelper;
import org.mpxj.planner.schema.Allocation;
import org.mpxj.planner.schema.Allocations;
import org.mpxj.planner.schema.Calendar;
import org.mpxj.planner.schema.Calendars;
import org.mpxj.planner.schema.Constraint;
import org.mpxj.planner.schema.DayType;
import org.mpxj.planner.schema.Day;
import org.mpxj.planner.schema.DayTypes;
import org.mpxj.planner.schema.Days;
import org.mpxj.planner.schema.DefaultWeek;
import org.mpxj.planner.schema.Interval;
import org.mpxj.planner.schema.ObjectFactory;
import org.mpxj.planner.schema.OverriddenDayType;
import org.mpxj.planner.schema.OverriddenDayTypes;
import org.mpxj.planner.schema.Predecessor;
import org.mpxj.planner.schema.Predecessors;
import org.mpxj.planner.schema.Project;
import org.mpxj.planner.schema.Resources;
import org.mpxj.planner.schema.Tasks;
import org.mpxj.writer.AbstractProjectWriter;

/**
 * This class creates a new Planner file from the contents of
 * a ProjectFile instance.
 */
public final class PlannerWriter extends AbstractProjectWriter
{
   @Override public void write(ProjectFile projectFile, OutputStream stream) throws IOException
   {
      try
      {
         m_projectFile = projectFile;
         m_eventManager = projectFile.getEventManager();

         if (CONTEXT == null)
         {
            throw CONTEXT_EXCEPTION;
         }

         Marshaller marshaller = MarshallerHelper.create(CONTEXT);
         marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
         if (m_charset != null)
         {
            marshaller.setProperty(Marshaller.JAXB_ENCODING, m_charset.name());
         }

         //
         // The Planner implementation used  as the basis for this work, 0.14.1
         // does not appear to have a particularly robust parser, and rejects
         // files with the full XML declaration produced by JAXB. The
         // following property suppresses this declaration.
         //
         marshaller.setProperty(Marshaller.JAXB_FRAGMENT, Boolean.TRUE);

         m_factory = new ObjectFactory();
         m_plannerProject = m_factory.createProject();

         writeProjectProperties();
         writeCalendars();
         writeResources();
         writeTasks();
         writeAssignments();

         marshaller.marshal(m_plannerProject, stream);
      }

      catch (JAXBException ex)
      {
         throw new IOException(ex.toString());
      }

      finally
      {
         m_projectFile = null;
         m_factory = null;
         m_plannerProject = null;
      }
   }

   /**
    * This method writes project properties to a Planner file.
    */
   private void writeProjectProperties()
   {
      ProjectProperties properties = m_projectFile.getProjectProperties();
      String projectStart = properties.getStartDate() == null ? "" : getDateTimeString(properties.getStartDate());
      m_plannerProject.setCompany(properties.getCompany());
      m_plannerProject.setManager(properties.getManager());
      m_plannerProject.setName(getString(properties.getName()));
      m_plannerProject.setProjectStart(projectStart);
      m_plannerProject.setCalendar(getIntegerString(m_projectFile.getProjectProperties().getDefaultCalendarUniqueID()));
      m_plannerProject.setMrprojectVersion("2");
   }

   /**
    * This method writes calendar data to a Planner file.
    *
    */
   private void writeCalendars()
   {
      //
      // Create the new Planner calendar list
      //
      Calendars calendars = m_factory.createCalendars();
      m_plannerProject.setCalendars(calendars);
      writeDayTypes(calendars);
      List<Calendar> calendar = calendars.getCalendar();

      //
      // Process each calendar in turn
      //
      List<ProjectCalendar> sortedCalendarList = m_projectFile.getCalendars().stream().filter(c -> !c.isDerived()).sorted((a, b) -> NumberHelper.compare(a.getUniqueID(), b.getUniqueID())).collect(Collectors.toList());
      for (ProjectCalendar mpxjCalendar : sortedCalendarList)
      {
         Calendar plannerCalendar = m_factory.createCalendar();
         calendar.add(plannerCalendar);
         writeCalendar(mpxjCalendar, plannerCalendar);
      }
   }

   /**
    * Write the standard set of day types.
    *
    * @param calendars parent collection of calendars
    */
   private void writeDayTypes(Calendars calendars)
   {
      DayTypes dayTypes = m_factory.createDayTypes();
      calendars.setDayTypes(dayTypes);
      List<DayType> typeList = dayTypes.getDayType();

      DayType dayType = m_factory.createDayType();
      typeList.add(dayType);
      dayType.setId("0");
      dayType.setName("Working");
      dayType.setDescription("A default working day");

      dayType = m_factory.createDayType();
      typeList.add(dayType);
      dayType.setId("1");
      dayType.setName("Nonworking");
      dayType.setDescription("A default non working day");

      dayType = m_factory.createDayType();
      typeList.add(dayType);
      dayType.setId("2");
      dayType.setName("Use base");
      dayType.setDescription("Use day from base calendar");
   }

   /**
    * This method writes data for a single calendar to a Planner file.
    *
    * @param mpxjCalendar MPXJ calendar instance
    * @param plannerCalendar Planner calendar instance
    */
   private void writeCalendar(ProjectCalendar mpxjCalendar, Calendar plannerCalendar)
   {
      //
      // Populate basic details
      //
      plannerCalendar.setId(getIntegerString(mpxjCalendar.getUniqueID()));
      plannerCalendar.setName(getString(mpxjCalendar.getName()));

      //
      // Set working and non working days
      //
      DefaultWeek dw = m_factory.createDefaultWeek();
      plannerCalendar.setDefaultWeek(dw);
      dw.setMon(getWorkingDayString(mpxjCalendar, DayOfWeek.MONDAY));
      dw.setTue(getWorkingDayString(mpxjCalendar, DayOfWeek.TUESDAY));
      dw.setWed(getWorkingDayString(mpxjCalendar, DayOfWeek.WEDNESDAY));
      dw.setThu(getWorkingDayString(mpxjCalendar, DayOfWeek.THURSDAY));
      dw.setFri(getWorkingDayString(mpxjCalendar, DayOfWeek.FRIDAY));
      dw.setSat(getWorkingDayString(mpxjCalendar, DayOfWeek.SATURDAY));
      dw.setSun(getWorkingDayString(mpxjCalendar, DayOfWeek.SUNDAY));

      //
      // Set working hours
      //
      OverriddenDayTypes odt = m_factory.createOverriddenDayTypes();
      plannerCalendar.setOverriddenDayTypes(odt);
      List<OverriddenDayType> typeList = odt.getOverriddenDayType();
      Sequence uniqueID = new Sequence(0);

      //
      // This is a bit arbitrary, so not ideal, however...
      // The idea here is that MS Project allows us to specify working hours
      // for each day of the week individually. Planner doesn't do this,
      // but instead allows us to specify working hours for each day type.
      // What we are doing here is stepping through the days of the week to
      // find the first working day, then using the hours for that day
      // as the hours for the working day type in Planner.
      //
      for (int dayLoop = 1; dayLoop < 8; dayLoop++)
      {
         DayOfWeek day = DayOfWeekHelper.getInstance(dayLoop);
         if (mpxjCalendar.isWorkingDay(day))
         {
            processWorkingHours(mpxjCalendar, uniqueID, day, typeList);
            break;
         }
      }

      //
      // Process exception days
      //
      Days plannerDays = m_factory.createDays();
      plannerCalendar.setDays(plannerDays);
      List<Day> dayList = plannerDays.getDay();
      processExceptionDays(mpxjCalendar, dayList);

      m_eventManager.fireCalendarWrittenEvent(mpxjCalendar);

      //
      // Process any derived calendars
      //
      List<Calendar> calendarList = plannerCalendar.getCalendar();
      List<ProjectCalendar> sortedCalendarList = new ArrayList<>(mpxjCalendar.getDerivedCalendars());
      sortedCalendarList.sort((a, b) -> NumberHelper.compare(a.getUniqueID(), b.getUniqueID()));

      for (ProjectCalendar mpxjDerivedCalendar : sortedCalendarList)
      {
         Calendar plannerDerivedCalendar = m_factory.createCalendar();
         calendarList.add(plannerDerivedCalendar);
         writeCalendar(mpxjDerivedCalendar, plannerDerivedCalendar);
      }
   }

   /**
    * Process the standard working hours for a given day.
    *
    * @param mpxjCalendar MPXJ Calendar instance
    * @param uniqueID unique ID sequence generation
    * @param day Day instance
    * @param typeList Planner list of days
    */
   private void processWorkingHours(ProjectCalendar mpxjCalendar, Sequence uniqueID, DayOfWeek day, List<OverriddenDayType> typeList)
   {
      if (isWorkingDay(mpxjCalendar, day))
      {
         ProjectCalendarHours mpxjHours = mpxjCalendar.getCalendarHours(day);
         if (mpxjHours != null)
         {
            OverriddenDayType odt = m_factory.createOverriddenDayType();
            typeList.add(odt);
            odt.setId(getIntegerString(uniqueID.next()));
            List<Interval> intervalList = odt.getInterval();
            for (LocalTimeRange mpxjRange : mpxjHours)
            {
               LocalTime rangeStart = mpxjRange.getStart();
               LocalTime rangeEnd = mpxjRange.getEnd();

               if (rangeStart != null && rangeEnd != null)
               {
                  Interval interval = m_factory.createInterval();
                  intervalList.add(interval);
                  interval.setStart(m_timeFormat.format(rangeStart));
                  interval.setEnd(m_timeFormat.format(rangeEnd));
               }
            }
         }
      }
   }

   /**
    * Process exception days.
    *
    * @param mpxjCalendar MPXJ Calendar instance
    * @param dayList Planner list of exception days
    */
   private void processExceptionDays(ProjectCalendar mpxjCalendar, List<Day> dayList)
   {
      List<ProjectCalendarException> expandedExceptions = mpxjCalendar.getExpandedCalendarExceptionsWithWorkWeeks();
      for (ProjectCalendarException mpxjCalendarException : expandedExceptions)
      {
         LocalDate rangeStartDay = mpxjCalendarException.getFromDate();
         LocalDate rangeEndDay = mpxjCalendarException.getToDate();
         if (rangeStartDay.equals(rangeEndDay))
         {
            //
            // Exception covers a single day
            //
            Day day = m_factory.createDay();
            dayList.add(day);
            day.setType("day-type");
            day.setDate(getDateString(mpxjCalendarException.getFromDate()));
            day.setId(mpxjCalendarException.getWorking() ? "0" : "1");
         }
         else
         {
            //
            // Exception covers a range of days
            //
            while (!rangeStartDay.isAfter(rangeEndDay))
            {
               Day day = m_factory.createDay();
               dayList.add(day);
               day.setType("day-type");
               day.setDate(getDateString(rangeStartDay));
               day.setId(mpxjCalendarException.getWorking() ? "0" : "1");
               rangeStartDay = rangeStartDay.plusDays(1);
            }
         }

         /*
          * TODO: we need to deal with date ranges here
          */
      }
   }

   /**
    * This method writes resource data to a Planner file.
    */
   private void writeResources()
   {
      Resources resources = m_factory.createResources();
      m_plannerProject.setResources(resources);
      List<org.mpxj.planner.schema.Resource> resourceList = resources.getResource();
      for (Resource mpxjResource : m_projectFile.getResources())
      {
         org.mpxj.planner.schema.Resource plannerResource = m_factory.createResource();
         resourceList.add(plannerResource);
         writeResource(mpxjResource, plannerResource);
      }
   }

   /**
    * This method writes data for a single resource to a Planner file.
    *
    * @param mpxjResource MPXJ Resource instance
    * @param plannerResource Planner Resource instance
    */
   private void writeResource(Resource mpxjResource, org.mpxj.planner.schema.Resource plannerResource)
   {
      ProjectCalendar resourceCalendar = mpxjResource.getCalendar();
      if (resourceCalendar != null)
      {
         plannerResource.setCalendar(getIntegerString(resourceCalendar.getUniqueID()));
      }

      plannerResource.setEmail(mpxjResource.getEmailAddress());
      plannerResource.setId(getIntegerString(mpxjResource.getUniqueID()));
      plannerResource.setName(getString(mpxjResource.getName()));
      plannerResource.setNote(mpxjResource.getNotes());
      plannerResource.setShortName(mpxjResource.getInitials());
      plannerResource.setType(mpxjResource.getType() == ResourceType.MATERIAL ? "2" : "1");
      //plannerResource.setStdRate();
      //plannerResource.setOvtRate();
      plannerResource.setUnits("0");
      //plannerResource.setProperties();
      m_eventManager.fireResourceWrittenEvent(mpxjResource);
   }

   /**
    * This method writes task data to a Planner file.
    *
    */
   private void writeTasks()
   {
      Tasks tasks = m_factory.createTasks();
      m_plannerProject.setTasks(tasks);
      List<org.mpxj.planner.schema.Task> taskList = tasks.getTask();
      for (Task task : m_projectFile.getChildTasks())
      {
         writeTask(task, taskList);
      }
   }

   /**
    * This method writes data for a single task to a Planner file.
    *
    * @param mpxjTask MPXJ Task instance
    * @param taskList list of child tasks for current parent
    */
   private void writeTask(Task mpxjTask, List<org.mpxj.planner.schema.Task> taskList)
   {
      if (mpxjTask.getNull())
      {
         return;
      }

      org.mpxj.planner.schema.Task plannerTask = m_factory.createTask();
      taskList.add(plannerTask);
      plannerTask.setEnd(getDateTimeString(mpxjTask.getFinish()));
      plannerTask.setId(getIntegerString(mpxjTask.getUniqueID()));
      plannerTask.setName(getString(mpxjTask.getName()));
      plannerTask.setNote(mpxjTask.getNotes());
      plannerTask.setPercentComplete(getIntegerString(mpxjTask.getPercentageWorkComplete()));
      plannerTask.setPriority(mpxjTask.getPriority() == null ? null : getIntegerString(mpxjTask.getPriority().getValue() * 10));
      plannerTask.setScheduling(getScheduling(mpxjTask.getType()));
      plannerTask.setStart(getDateTimeString(LocalDateTimeHelper.getDayStartDate(mpxjTask.getStart())));
      plannerTask.setType(mpxjTask.getMilestone() ? "milestone" : "normal");
      plannerTask.setWork(getDurationString(getWork(mpxjTask)));
      plannerTask.setWorkStart(getDateTimeString(mpxjTask.getStart()));
      writeConstraint(mpxjTask, plannerTask);
      writePredecessors(mpxjTask, plannerTask);
      m_eventManager.fireTaskWrittenEvent(mpxjTask);

      //
      // Write child tasks
      //
      List<org.mpxj.planner.schema.Task> childTaskList = plannerTask.getTask();
      for (Task task : mpxjTask.getChildTasks())
      {
         writeTask(task, childTaskList);
      }
   }

   private void writeConstraint(Task mpxjTask, org.mpxj.planner.schema.Task plannerTask)
   {
      ConstraintType mpxjConstraintType = mpxjTask.getConstraintType();
      if (mpxjConstraintType != null && mpxjConstraintType != ConstraintType.AS_SOON_AS_POSSIBLE)
      {
         String plannerConstraintType = null;

         switch (mpxjConstraintType)
         {
            case MUST_START_ON:
            case START_ON:
            {
               plannerConstraintType = "must-start-on";
               break;
            }

            case START_NO_EARLIER_THAN:
            {
               plannerConstraintType = "start-no-earlier-than";
               break;
            }

            default:
            {
               break;
            }
         }

         if (plannerConstraintType != null)
         {
            Constraint plannerConstraint = m_factory.createConstraint();
            plannerTask.setConstraint(plannerConstraint);
            plannerConstraint.setType(plannerConstraintType);
            plannerConstraint.setTime(getDateTimeString(mpxjTask.getConstraintDate()));
         }
      }
   }

   private Duration getWork(Task task)
   {
      Duration result = task.getWork();
      return result != null && result.getDuration() != 0 ? result : task.getDuration();
   }

   /**
    * This method writes predecessor data to a Planner file.
    * We have to deal with a slight anomaly in this method that is introduced
    * by the MPX file format. It would be possible for someone to create an
    * MPX file with both the predecessor list and the unique ID predecessor
    * list populated... which means that we must process both and avoid adding
    * duplicate predecessors. Also interesting to note is that MSP98 populates
    * the predecessor list, not the unique ID predecessor list, as you might
    * expect.
    *
    * @param mpxjTask MPXJ task instance
    * @param plannerTask planner task instance
    */
   private void writePredecessors(Task mpxjTask, org.mpxj.planner.schema.Task plannerTask)
   {
      List<Relation> predecessors = mpxjTask.getPredecessors();
      if (predecessors.isEmpty())
      {
         return;
      }

      Predecessors plannerPredecessors = m_factory.createPredecessors();
      plannerTask.setPredecessors(plannerPredecessors);
      List<Predecessor> predecessorList = plannerPredecessors.getPredecessor();
      int id = 0;

      for (Relation rel : predecessors)
      {
         Integer taskUniqueID = rel.getPredecessorTask().getUniqueID();
         Predecessor plannerPredecessor = m_factory.createPredecessor();
         plannerPredecessor.setId(getIntegerString(++id));
         plannerPredecessor.setPredecessorId(getIntegerString(taskUniqueID));
         plannerPredecessor.setLag(getDurationString(getLag(rel)));
         plannerPredecessor.setType(RELATIONSHIP_TYPES.get(rel.getType()));
         predecessorList.add(plannerPredecessor);
         m_eventManager.fireRelationWrittenEvent(rel);
      }
   }

   /**
    * Determine the correct value for lag.
    *
    * @param relation relation data
    * @return required lag value
    */
   private Duration getLag(Relation relation)
   {
      Duration lag = relation.getLag();

      // No lag? No change required.
      if (lag == null || lag.getDuration() == 0 || lag.getUnits().isElapsed())
      {
         return lag;
      }

      // Calculate the effect of percent lag
      if (lag.getUnits() == TimeUnit.PERCENT)
      {
         Duration targetDuration = relation.getPredecessorTask().getDuration();
         double percentValue = lag.getDuration();
         double durationValue = targetDuration.getDuration();
         durationValue = (durationValue * percentValue) / 100.0;
         lag = Duration.getInstance(durationValue, targetDuration.getUnits());
      }

      // Bail out if we already have elapsed units
      if (lag.getUnits().isElapsed())
      {
         return lag;
      }

      // Convert the lag to an elapsed duration.
      LocalDateTime predecessorDate;
      LocalDateTime successorDate;

      switch (relation.getType())
      {
         case START_START:
         {
            predecessorDate = relation.getPredecessorTask().getStart();
            successorDate = relation.getSuccessorTask().getStart();
            break;
         }

         case FINISH_FINISH:
         {
            predecessorDate = relation.getPredecessorTask().getFinish();
            successorDate = relation.getSuccessorTask().getFinish();
            break;
         }

         case START_FINISH:
         {
            predecessorDate = relation.getPredecessorTask().getStart();
            successorDate = relation.getSuccessorTask().getFinish();
            break;
         }

         default:
         {
            predecessorDate = relation.getPredecessorTask().getFinish();
            successorDate = relation.getSuccessorTask().getStart();
            break;
         }
      }

      // Bail if we don't have two dates
      if (successorDate == null || predecessorDate == null)
      {
         return lag;
      }
      long milliseconds = predecessorDate.until(successorDate, ChronoUnit.MILLIS);
      double minutes = milliseconds / (1000.0 * 60.0);
      return Duration.getInstance(minutes, TimeUnit.ELAPSED_MINUTES);
   }

   /**
    * This method writes assignment data to a Planner file.
    *
    */
   private void writeAssignments()
   {
      Allocations allocations = m_factory.createAllocations();
      m_plannerProject.setAllocations(allocations);

      List<Allocation> allocationList = allocations.getAllocation();

      // As we now allow a resource to be assigned multiple times to a task
      // we need to handle this for file formats which allow a resource to be
      // assigned only once. The code below attempts to preserve the original
      // behaviour when we ignored multiple assignments of the same resource.
      // TODO: implement more intelligent rollup of multiple resource assignments
      Function<ResourceAssignment, String> assignmentKey = (a) -> a.getTaskUniqueID() + " " + a.getResourceUniqueID();
      Map<String, ResourceAssignment> map = m_projectFile.getResourceAssignments().stream().collect(Collectors.toMap(assignmentKey, Function.identity(), (a1, a2) -> a1));
      m_projectFile.getResourceAssignments().stream().filter(a -> a.getResourceUniqueID() != null && map.get(assignmentKey.apply(a)) == a).forEach(a -> allocationList.add(writeAssignment(a)));
   }

   private Allocation writeAssignment(ResourceAssignment mpxjAssignment)
   {
      Allocation plannerAllocation = m_factory.createAllocation();

      plannerAllocation.setTaskId(getIntegerString(mpxjAssignment.getTask().getUniqueID()));
      plannerAllocation.setResourceId(getIntegerString(mpxjAssignment.getResourceUniqueID()));
      plannerAllocation.setUnits(getIntegerString(mpxjAssignment.getUnits()));

      m_eventManager.fireAssignmentWrittenEvent(mpxjAssignment);

      return plannerAllocation;
   }

   /**
    * Convert an Integer value into a String.
    *
    * @param value Integer value
    * @return String value
    */
   private String getIntegerString(Number value)
   {
      return (value == null ? null : Integer.toString(value.intValue()));
   }

   /**
    * Convert an int value into a String.
    *
    * @param value int value
    * @return String value
    */
   private String getIntegerString(int value)
   {
      return (Integer.toString(value));
   }

   /**
    * Used to determine if a particular day of the week is normally
    * a working day.
    *
    * @param mpxjCalendar ProjectCalendar instance
    * @param day Day instance
    * @return boolean flag
    */
   private boolean isWorkingDay(ProjectCalendar mpxjCalendar, DayOfWeek day)
   {
      boolean result = false;
      org.mpxj.DayType type = mpxjCalendar.getCalendarDayType(day);
      if (type == null)
      {
         type = org.mpxj.DayType.DEFAULT;
      }

      switch (type)
      {
         case WORKING:
         {
            result = true;
            break;
         }

         case NON_WORKING:
         {
            result = false;
            break;
         }

         case DEFAULT:
         {
            result = mpxjCalendar.getParent() != null && isWorkingDay(mpxjCalendar.getParent(), day);
            break;
         }
      }

      return (result);
   }

   /**
    * Returns a flag represented as a String, indicating if
    * the supplied day is a working day.
    *
    * @param mpxjCalendar MPXJ ProjectCalendar instance
    * @param day Day instance
    * @return boolean flag as a string
    */
   private String getWorkingDayString(ProjectCalendar mpxjCalendar, DayOfWeek day)
   {
      String result = null;
      org.mpxj.DayType type = mpxjCalendar.getCalendarDayType(day);
      if (type == null)
      {
         type = org.mpxj.DayType.DEFAULT;
      }

      switch (type)
      {
         case WORKING:
         {
            result = "0";
            break;
         }

         case NON_WORKING:
         {
            result = "1";
            break;
         }

         case DEFAULT:
         {
            result = "2";
            break;
         }
      }

      return (result);
   }

   /**
    * Convert a Java date into a Planner date.
    *
    * 20070222
    *
    * @param value Java Date instance
    * @return Planner date
    */
   private String getDateString(LocalDate value)
   {
      return m_dateFormat.format(value);
   }

   /**
    * Convert a Java date into a Planner date-time string.
    *
    * 20070222T080000Z
    *
    * @param value Java date
    * @return Planner date-time string
    */
   private String getDateTimeString(LocalDateTime value)
   {
      if (value == null)
      {
         return null;
      }

      return m_dateTimeFormat.format(value);
   }

   /**
    * Converts an MPXJ Duration instance into the string representation
    * of a Planner duration.
    *
    * Planner represents durations as a number of seconds in its
    * file format, however it displays durations as days and hours,
    * and seems to assume that a working day is 8 hours.
    *
    * @param value string representation of a duration
    * @return Duration instance
    */
   private String getDurationString(Duration value)
   {
      String result = null;

      if (value != null)
      {
         double seconds = 0;

         switch (value.getUnits())
         {
            case MINUTES:
            case ELAPSED_MINUTES:
            {
               seconds = value.getDuration() * 60;
               break;
            }

            case HOURS:
            case ELAPSED_HOURS:
            {
               seconds = value.getDuration() * (60 * 60);
               break;
            }

            case DAYS:
            {
               double minutesPerDay = m_projectFile.getProjectProperties().getMinutesPerDay().doubleValue();
               seconds = value.getDuration() * (minutesPerDay * 60);
               break;
            }

            case ELAPSED_DAYS:
            {
               seconds = value.getDuration() * (24 * 60 * 60);
               break;
            }

            case WEEKS:
            {
               double minutesPerWeek = m_projectFile.getProjectProperties().getMinutesPerWeek().doubleValue();
               seconds = value.getDuration() * (minutesPerWeek * 60);
               break;
            }

            case ELAPSED_WEEKS:
            {
               seconds = value.getDuration() * (7 * 24 * 60 * 60);
               break;
            }

            case MONTHS:
            {
               double minutesPerDay = m_projectFile.getProjectProperties().getMinutesPerDay().doubleValue();
               double daysPerMonth = m_projectFile.getProjectProperties().getDaysPerMonth().doubleValue();
               seconds = value.getDuration() * (daysPerMonth * minutesPerDay * 60);
               break;
            }

            case ELAPSED_MONTHS:
            {
               seconds = value.getDuration() * (30 * 24 * 60 * 60);
               break;
            }

            case YEARS:
            {
               double minutesPerDay = m_projectFile.getProjectProperties().getMinutesPerDay().doubleValue();
               double daysPerMonth = m_projectFile.getProjectProperties().getDaysPerMonth().doubleValue();
               seconds = value.getDuration() * (12 * daysPerMonth * minutesPerDay * 60);
               break;
            }

            case ELAPSED_YEARS:
            {
               seconds = value.getDuration() * (365 * 24 * 60 * 60);
               break;
            }

            default:
            {
               break;
            }
         }

         result = Long.toString((long) seconds);
      }

      return (result);
   }

   /**
    * Convert a string representation of the task type
    * into a TaskType instance.
    *
    * @param value string value
    * @return TaskType value
    */
   private String getScheduling(TaskType value)
   {
      String result = "fixed-work";
      if (value == TaskType.FIXED_DURATION || value == TaskType.FIXED_DURATION_AND_UNITS)
      {
         result = "fixed-duration";
      }
      return (result);
   }

   /**
    * Writes a string value, ensuring that null is mapped to an empty string.
    *
    * @param value string value
    * @return string value
    */
   private String getString(String value)
   {
      return (value == null ? "" : value);
   }

   /**
    * Set the charset used to write the file. By default, UTF-8 is used.
    *
    * @param charset charset
    */
   public void setCharset(Charset charset)
   {
      m_charset = charset;
   }

   /**
    * Retrieve the charset used to write the file. If this value is null,
    * UTF-8 is used.
    *
    * @return charset
    */
   public Charset getCharset()
   {
      return m_charset;
   }

   private Charset m_charset;
   private ProjectFile m_projectFile;
   private EventManager m_eventManager;
   private ObjectFactory m_factory;
   private Project m_plannerProject;

   private final DateTimeFormatter m_timeFormat = DateTimeFormatter.ofPattern("HHmm");
   private final DateTimeFormatter m_dateFormat = DateTimeFormatter.ofPattern("yyyyMMdd");
   private final DateTimeFormatter m_dateTimeFormat = DateTimeFormatter.ofPattern("yyyyMMdd'T'HHmmss'Z'");

   private static final Map<RelationType, String> RELATIONSHIP_TYPES = new HashMap<>();
   static
   {
      RELATIONSHIP_TYPES.put(RelationType.FINISH_FINISH, "FF");
      RELATIONSHIP_TYPES.put(RelationType.FINISH_START, "FS");
      RELATIONSHIP_TYPES.put(RelationType.START_FINISH, "SF");
      RELATIONSHIP_TYPES.put(RelationType.START_START, "SS");
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
         CONTEXT = JAXBContext.newInstance("org.mpxj.planner.schema", PlannerWriter.class.getClassLoader());
      }

      catch (JAXBException ex)
      {
         CONTEXT_EXCEPTION = ex;
         CONTEXT = null;
      }
   }
}
