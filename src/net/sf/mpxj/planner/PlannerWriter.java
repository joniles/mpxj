/*
 * file:       PlannerWriter.java
 * author:     Jon Iles
 * copyright:  (c) Tapster Rock Limited 2005
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

package net.sf.mpxj.planner;

import java.io.IOException;
import java.io.OutputStream;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TreeSet;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;

import net.sf.mpxj.ConstraintType;
import net.sf.mpxj.DateRange;
import net.sf.mpxj.Day;
import net.sf.mpxj.Duration;
import net.sf.mpxj.ProjectCalendar;
import net.sf.mpxj.ProjectCalendarException;
import net.sf.mpxj.ProjectCalendarHours;
import net.sf.mpxj.ProjectFile;
import net.sf.mpxj.ProjectHeader;
import net.sf.mpxj.Relation;
import net.sf.mpxj.RelationType;
import net.sf.mpxj.Resource;
import net.sf.mpxj.ResourceAssignment;
import net.sf.mpxj.ResourceType;
import net.sf.mpxj.Task;
import net.sf.mpxj.TaskType;
import net.sf.mpxj.TimeUnit;
import net.sf.mpxj.planner.schema.Allocation;
import net.sf.mpxj.planner.schema.Allocations;
import net.sf.mpxj.planner.schema.Calendars;
import net.sf.mpxj.planner.schema.Constraint;
import net.sf.mpxj.planner.schema.DayType;
import net.sf.mpxj.planner.schema.DayTypes;
import net.sf.mpxj.planner.schema.Days;
import net.sf.mpxj.planner.schema.DefaultWeek;
import net.sf.mpxj.planner.schema.Interval;
import net.sf.mpxj.planner.schema.ObjectFactory;
import net.sf.mpxj.planner.schema.OverriddenDayType;
import net.sf.mpxj.planner.schema.OverriddenDayTypes;
import net.sf.mpxj.planner.schema.Predecessor;
import net.sf.mpxj.planner.schema.Predecessors;
import net.sf.mpxj.planner.schema.Project;
import net.sf.mpxj.planner.schema.Resources;
import net.sf.mpxj.planner.schema.Tasks;
import net.sf.mpxj.utility.DateUtility;
import net.sf.mpxj.utility.Sequence;
import net.sf.mpxj.writer.AbstractProjectWriter;


/**
 * This class creates a new Planner file from the contents of 
 * a ProjectFile instance.
 */
public final class PlannerWriter extends AbstractProjectWriter
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

         JAXBContext context = JAXBContext.newInstance ("net.sf.mpxj.planner.schema");
         Marshaller marshaller = context.createMarshaller();
         marshaller.setProperty (Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
         
         //
         // The Planner implementation used  as the basis for this work, 0.14.1
         // does not appear to have a particularly robust parser, and rejects
         // files with the full XML declaration produced by JAXB. The
         // following property suppresses this declaration.
         //
         marshaller.setProperty("com.sun.xml.bind.xmlDeclaration", Boolean.FALSE);
         
         m_factory = new ObjectFactory ();
         m_plannerProject = m_factory.createProject();

         writeProjectHeader ();
         writeCalendars ();
         writeResources ();
         writeTasks ();
         writeAssignments ();

         marshaller.marshal (m_plannerProject, stream);
      }

      catch (JAXBException ex)
      {
         throw new IOException (ex.toString());
      }

      finally
      {
         m_projectFile = null;
         m_factory = null;
         m_plannerProject = null;
      }
   }


   /**
    * This method writes project header data to a Planner file.
    */
   private void writeProjectHeader ()
   {
      ProjectHeader mpxjHeader = m_projectFile.getProjectHeader ();      
      
      m_plannerProject.setCompany(mpxjHeader.getCompany());
      m_plannerProject.setManager(mpxjHeader.getManager());
      m_plannerProject.setName(getString(mpxjHeader.getName()));
      m_plannerProject.setProjectStart(getDateTime(mpxjHeader.getStartDate()));
      
      m_plannerProject.setMrprojectVersion("2");
   }



   
   /**
    * This method writes calandar data to a Planner file.
    *
    * @throws JAXBException on xml creation errors
    */
   private void writeCalendars ()
      throws JAXBException
   {
      //
      // Create the new Planner calendar list
      //
      Calendars calendars = m_factory.createCalendars();
      m_plannerProject.setCalendars (calendars);
      writeDayTypes(calendars);
      List<net.sf.mpxj.planner.schema.Calendar> calendar = calendars.getCalendar();
      
      //
      // Process each calendar in turn
      //
      Iterator<ProjectCalendar> iter = m_projectFile.getBaseCalendars().iterator();    
      while (iter.hasNext() == true)
      {
         ProjectCalendar mpxjCalendar = iter.next();
         
         net.sf.mpxj.planner.schema.Calendar plannerCalendar = m_factory.createCalendar();         
         calendar.add (plannerCalendar);
         writeCalendar (mpxjCalendar, plannerCalendar);
      }
   }

   /**
    * Write the standard set of day types.
    * 
    * @param calendars parent collection of calendars
    */
   private void writeDayTypes (Calendars calendars)
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
    * @throws JAXBException on xml creation errors
    */
   private void writeCalendar (ProjectCalendar mpxjCalendar, net.sf.mpxj.planner.schema.Calendar plannerCalendar)
      throws JAXBException
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
      dw.setMon(getWorkingDayString(mpxjCalendar, Day.MONDAY));
      dw.setTue(getWorkingDayString(mpxjCalendar, Day.TUESDAY));
      dw.setWed(getWorkingDayString(mpxjCalendar, Day.WEDNESDAY));
      dw.setThu(getWorkingDayString(mpxjCalendar, Day.THURSDAY));
      dw.setFri(getWorkingDayString(mpxjCalendar, Day.FRIDAY));
      dw.setSat(getWorkingDayString(mpxjCalendar, Day.SATURDAY));
      dw.setSun(getWorkingDayString(mpxjCalendar, Day.SUNDAY));
      
                  
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
      for (int dayLoop=1; dayLoop < 8; dayLoop++)
      {
         Day day = Day.getInstance(dayLoop);
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
      List<net.sf.mpxj.planner.schema.Day> dayList = plannerDays.getDay();
      processExceptionDays (mpxjCalendar, dayList);

      //
      // Process any derived calendars
      //
      List<net.sf.mpxj.planner.schema.Calendar> calendarList = plannerCalendar.getCalendar();
      
      Iterator<ProjectCalendar> iter = mpxjCalendar.getDerivedCalendars().iterator();
      while (iter.hasNext())
      {
         ProjectCalendar mpxjDerivedCalendar = iter.next();
         net.sf.mpxj.planner.schema.Calendar plannerDerivedCalendar = m_factory.createCalendar();   
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
   private void processWorkingHours (ProjectCalendar mpxjCalendar, Sequence uniqueID, Day day, List<OverriddenDayType> typeList)
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
            Iterator<DateRange> iter = mpxjHours.iterator();
            while (iter.hasNext())
            {
               DateRange mpxjRange = iter.next();
               Interval interval = m_factory.createInterval();
               intervalList.add(interval);
               interval.setStart(getTimeString(mpxjRange.getStartDate()));
               interval.setEnd(getTimeString(mpxjRange.getEndDate()));
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
   private void processExceptionDays (ProjectCalendar mpxjCalendar, List<net.sf.mpxj.planner.schema.Day> dayList)
   {
      Iterator<ProjectCalendarException> iter = mpxjCalendar.getCalendarExceptions().iterator();
      while (iter.hasNext())
      {
         ProjectCalendarException mpxjCalendarException = iter.next();                  
         
         
         Date rangeStartDay = mpxjCalendarException.getFromDate();
         Date rangeEndDay = mpxjCalendarException.getToDate();         
         if (DateUtility.getDayStartDate(rangeStartDay).getTime() == DateUtility.getDayEndDate(rangeEndDay).getTime())
         {
            //
            // Exception covers a single day
            //
            net.sf.mpxj.planner.schema.Day day = m_factory.createDay();
            dayList.add(day);
            day.setType("day-type");
            day.setDate(getDateString(mpxjCalendarException.getFromDate()));
            day.setId(mpxjCalendarException.getWorking()?"0":"1");
         }
         else
         {
            //
            // Exception covers a range of days
            //
            Calendar cal = Calendar.getInstance();
            cal.setTime(rangeStartDay);
            
            while (cal.getTime().getTime() < rangeEndDay.getTime())
            {
               net.sf.mpxj.planner.schema.Day day = m_factory.createDay();
               dayList.add(day);
               day.setType("day-type");
               day.setDate(getDateString(cal.getTime()));
               day.setId(mpxjCalendarException.getWorking()?"0":"1");               
               cal.add(Calendar.DAY_OF_YEAR, 1);
            }
         }
         
         /**
          * @TODO we need to deal with date ranges here
          */
      }
   }
   
   /**
    * This method writes resource data to a Planner file.
    */
   private void writeResources ()
   {
      Resources resources = m_factory.createResources();
      m_plannerProject.setResources(resources);
      List<net.sf.mpxj.planner.schema.Resource> resourceList = resources.getResource();
      Iterator<Resource> iter = m_projectFile.getAllResources().iterator();
      while (iter.hasNext())
      {
         Resource mpxjResource = iter.next();
         net.sf.mpxj.planner.schema.Resource plannerResource = m_factory.createResource();
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
   private void writeResource (Resource mpxjResource, net.sf.mpxj.planner.schema.Resource plannerResource)
   {
      ProjectCalendar resourceCalendar = mpxjResource.getResourceCalendar();
      if (resourceCalendar != null)
      {
         plannerResource.setCalendar(getIntegerString(resourceCalendar.getUniqueID()));
      }
      
      plannerResource.setEmail(mpxjResource.getEmailAddress());
      plannerResource.setId(getIntegerString(mpxjResource.getUniqueID()));
      plannerResource.setName(mpxjResource.getName());
      plannerResource.setNote(mpxjResource.getNotes());
      plannerResource.setShortName(mpxjResource.getInitials());
      plannerResource.setType(mpxjResource.getType()==ResourceType.MATERIAL?"2":"1");
      //plannerResource.setStdRate();
      //plannerResource.setOvtRate();      
      plannerResource.setUnits("0");
      //plannerResource.setProperties();      
   }


   /**
    * This method writes task data to a Planner file.
    *
    * @throws JAXBException on xml creation errors
    */
   private void writeTasks ()
      throws JAXBException
   {
      Tasks tasks = m_factory.createTasks();
      m_plannerProject.setTasks(tasks);
      List<net.sf.mpxj.planner.schema.Task> taskList = tasks.getTask();
      Iterator<Task> iter = m_projectFile.getChildTasks().iterator();
      while (iter.hasNext())
      {
         writeTask(iter.next(), taskList);
      }
   }


   /**
    * This method writes data for a single task to a Planner file.
    *
    * @param mpxjTask MPXJ Task instance
    * @param taskList list of child tasks for current parent
    */
   private void writeTask (Task mpxjTask, List<net.sf.mpxj.planner.schema.Task> taskList)
      throws JAXBException
   {
      net.sf.mpxj.planner.schema.Task plannerTask = m_factory.createTask();
      taskList.add(plannerTask);
      plannerTask.setEnd(getDateTimeString(mpxjTask.getFinish()));
      plannerTask.setId(getIntegerString(mpxjTask.getUniqueID()));
      plannerTask.setName(getString(mpxjTask.getName()));
      plannerTask.setNote(mpxjTask.getNotes());
      plannerTask.setPercentComplete(getIntegerString(mpxjTask.getPercentageWorkComplete()));
      plannerTask.setPriority(getIntegerString(mpxjTask.getPriority().getValue()*10));
      plannerTask.setScheduling(getScheduling(mpxjTask.getType()));
      plannerTask.setStart(getDateTimeString(DateUtility.getDayStartDate(mpxjTask.getStart())));
      if (mpxjTask.getMilestone())
      {
         plannerTask.setType("milestone");
      }
      else
      {
         plannerTask.setType("normal");
      }
      plannerTask.setWork(getDurationString(mpxjTask.getWork()));
      plannerTask.setWorkStart(getDateTimeString(mpxjTask.getStart()));
      
      ConstraintType mpxjConstraintType = mpxjTask.getConstraintType();
      if (mpxjConstraintType != ConstraintType.AS_SOON_AS_POSSIBLE)
      {
         Constraint plannerConstraint = m_factory.createConstraint();
         plannerTask.setConstraint(plannerConstraint);
         if (mpxjConstraintType == ConstraintType.START_NO_EARLIER_THAN)
         {
            plannerConstraint.setType("start-no-earlier-than");
         }
         else
         {
            if (mpxjConstraintType == ConstraintType.MUST_START_ON)
            {
               plannerConstraint.setType("must-start-on");
            }
         }
         
         plannerConstraint.setTime(getDateTimeString(mpxjTask.getConstraintDate()));
      }

      //
      // Write predecessors
      //
      writePredecessors(mpxjTask, plannerTask);
      
      //
      // Write child tasks
      //
      List<net.sf.mpxj.planner.schema.Task> childTaskList = plannerTask.getTask();
      Iterator<Task> iter = mpxjTask.getChildTasks().iterator();
      while (iter.hasNext())
      {
         writeTask(iter.next(), childTaskList);
      }
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
   private void writePredecessors (Task mpxjTask, net.sf.mpxj.planner.schema.Task plannerTask)
   {      
      TreeSet<Integer> set = new TreeSet<Integer> ();
      Integer taskID;

      Predecessors plannerPredecessors = m_factory.createPredecessors();
      plannerTask.setPredecessors(plannerPredecessors);
      List<Predecessor> predecessorList = plannerPredecessors.getPredecessor();
      int id = 0;
      
      //
      // Process the list of predecessors specified by Unique ID
      //
      List<Relation> predecessors = mpxjTask.getUniqueIDPredecessors();
      if (predecessors != null)
      {         
         for (Relation rel : predecessors)
         {
            taskID = rel.getTaskUniqueID();
            set.add(taskID);
            
            Predecessor plannerPredecessor = m_factory.createPredecessor();
            plannerPredecessor.setId(getIntegerString(++id));
            plannerPredecessor.setPredecessorId(getIntegerString(taskID));            
            plannerPredecessor.setLag(getDurationString(rel.getDuration()));
            plannerPredecessor.setType(RELATIONSHIP_TYPES.get(rel.getType()));
            predecessorList.add(plannerPredecessor);
         }
      }

      //
      // Process the list of predecessors specified by ID.
      // Note that this code ensures that if both lists are populated,
      // we avoid creating duplicate links.
      //
      predecessors = mpxjTask.getPredecessors();
      if (predecessors != null)
      {
         Task task;
         for (Relation rel : predecessors)
         {
            task = m_projectFile.getTaskByID(rel.getTaskID());
            if (task != null)
            {
               taskID = task.getUniqueID();
               if (set.contains(taskID) == false)
               {
                  Predecessor plannerPredecessor = m_factory.createPredecessor();                  
                  plannerPredecessor.setId(getIntegerString(++id));
                  plannerPredecessor.setPredecessorId(getIntegerString(taskID));
                  plannerPredecessor.setLag(getDurationString(rel.getDuration()));
                  plannerPredecessor.setType(RELATIONSHIP_TYPES.get(rel.getType()));
                  predecessorList.add(plannerPredecessor);
               }
            }
         }
      }
   }

   /**
    * This method writes assignment data to a Planner file.
    *
    */
   private void writeAssignments ()
   {
      Allocations allocations = m_factory.createAllocations();
      m_plannerProject.setAllocations(allocations);
      
      List<Allocation> allocationList = allocations.getAllocation();
      Iterator<ResourceAssignment> iter = m_projectFile.getAllResourceAssignments().iterator();
      while (iter.hasNext())
      {
         ResourceAssignment mpxjAssignment = iter.next();
         Allocation plannerAllocation = m_factory.createAllocation();
         allocationList.add(plannerAllocation);
         
         plannerAllocation.setTaskId(getIntegerString(mpxjAssignment.getTask().getUniqueID()));
         plannerAllocation.setResourceId(getIntegerString(mpxjAssignment.getResourceUniqueID()));
         plannerAllocation.setUnits(getIntegerString(mpxjAssignment.getUnits()));        
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
   private String getDateTime (Date value)
   {
      StringBuffer result = new StringBuffer(16);
      
      if (value != null)
      {
         Calendar cal = Calendar.getInstance();
         cal.setTime(value);
         
         result.append(m_fourDigitFormat.format(cal.get(Calendar.YEAR)));
         result.append(m_twoDigitFormat.format(cal.get(Calendar.MONTH)+1));
         result.append(m_twoDigitFormat.format(cal.get(Calendar.DAY_OF_MONTH)));
         result.append("T");
         result.append(m_twoDigitFormat.format(cal.get(Calendar.HOUR_OF_DAY)));
         result.append(m_twoDigitFormat.format(cal.get(Calendar.MINUTE)));
         result.append(m_twoDigitFormat.format(cal.get(Calendar.SECOND)));
         result.append("Z");
      }
      
      return (result.toString());      
   }

   /**
    * Convert an Integer value into a String.
    * 
    * @param value Integer value
    * @return String value
    */
   private String getIntegerString (Number value)
   {    
      return (value==null?null:Integer.toString(value.intValue()));
   }

   /**
    * Convert an int value into a String.
    * 
    * @param value int value
    * @return String value
    */
   private String getIntegerString (int value)
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
   private boolean isWorkingDay (ProjectCalendar mpxjCalendar, Day day)
   {
      boolean result = false;
      
      switch(mpxjCalendar.getWorkingDay(day))
      {
         case ProjectCalendar.WORKING:
         {
            result = true;
            break;
         }
         
         case ProjectCalendar.NON_WORKING:
         {
            result = false;
            break;
         }
         
         case ProjectCalendar.DEFAULT:
         {
            result = isWorkingDay(mpxjCalendar.getBaseCalendar(), day);
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
   private String getWorkingDayString (ProjectCalendar mpxjCalendar, Day day)
   { 
      String result = null;
      
      switch(mpxjCalendar.getWorkingDay(day))
      {
         case ProjectCalendar.WORKING:
         {
            result = "0";
            break;
         }
         
         case ProjectCalendar.NON_WORKING:
         {
            result = "1";
            break;
         }
         
         case ProjectCalendar.DEFAULT:
         {
            result = "2";
            break;
         }
      }      

      return (result);
   }
   
   /**
    * Convert a Java date into a Planner time.
    * 
    * 0800
    * 
    * @param value Java Date instance
    * @return Planner time value
    */
   private String getTimeString (Date value)
   {
      Calendar cal = Calendar.getInstance();
      cal.setTime(value);
      int hours = cal.get(Calendar.HOUR_OF_DAY);
      int minutes = cal.get(Calendar.MINUTE);
      
      StringBuffer sb = new StringBuffer(4);
      sb.append(m_twoDigitFormat.format(hours));
      sb.append(m_twoDigitFormat.format(minutes));
      
      return (sb.toString());
   }
   
   /**
    * Convert a Java date into a Planner date.
    * 
    * 20070222
    * 
    * @param value Java Date instance
    * @return Planner date
    */
   private String getDateString (Date value)
   {
      Calendar cal = Calendar.getInstance();
      cal.setTime(value);
      
      int year = cal.get(Calendar.YEAR);
      int month = cal.get(Calendar.MONTH)+1;
      int day = cal.get(Calendar.DAY_OF_MONTH);
      
      StringBuffer sb = new StringBuffer(8);
      sb.append(m_fourDigitFormat.format(year));
      sb.append(m_twoDigitFormat.format(month));
      sb.append(m_twoDigitFormat.format(day));
      
      return (sb.toString());
   }

   /**
    * Convert a Java date into a Planner date-time string.
    * 
    * 20070222T080000Z
    * 
    * @param value Java date
    * @return Planner date-time string
    */
   private String getDateTimeString (Date value)
   {
      Calendar cal = Calendar.getInstance();
      cal.setTime(value);
      StringBuffer sb = new StringBuffer(16);
      sb.append(m_fourDigitFormat.format(cal.get(Calendar.YEAR)));
      sb.append(m_twoDigitFormat.format(cal.get(Calendar.MONTH)+1));
      sb.append(m_twoDigitFormat.format(cal.get(Calendar.DAY_OF_MONTH)));
      sb.append('T');
      sb.append(m_twoDigitFormat.format(cal.get(Calendar.HOUR_OF_DAY)));
      sb.append(m_twoDigitFormat.format(cal.get(Calendar.MINUTE)));
      sb.append(m_twoDigitFormat.format(cal.get(Calendar.SECOND)));
      sb.append('Z');
      return (sb.toString());      
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
   private String getDurationString (Duration value)
   {
      String result = null;
      
      if (value != null)
      {
         double seconds = 0;
         
         switch (value.getUnits().getValue())
         {
            case TimeUnit.MINUTES_VALUE:
            case TimeUnit.ELAPSED_MINUTES_VALUE:
            {
               seconds = value.getDuration() * 60;
               break;
            }

            case TimeUnit.HOURS_VALUE:
            case TimeUnit.ELAPSED_HOURS_VALUE:
            {
               seconds = value.getDuration() * (8 * 60 * 60);
               break;
            }

            case TimeUnit.DAYS_VALUE:
            case TimeUnit.ELAPSED_DAYS_VALUE:
            {
               double minutesPerDay = m_projectFile.getProjectHeader().getMinutesPerDay().doubleValue();
               seconds = value.getDuration() * (minutesPerDay * 60);
               break;
            }
            
            case TimeUnit.WEEKS_VALUE:
            case TimeUnit.ELAPSED_WEEKS_VALUE:
            {
               double minutesPerWeek = m_projectFile.getProjectHeader().getMinutesPerWeek().doubleValue();
               seconds = value.getDuration() * (minutesPerWeek * 60);
               break;
            }

            case TimeUnit.MONTHS_VALUE:
            case TimeUnit.ELAPSED_MONTHS_VALUE:
            {
               double minutesPerDay = m_projectFile.getProjectHeader().getMinutesPerDay().doubleValue();
               double daysPerMonth = m_projectFile.getProjectHeader().getDaysPerMonth().doubleValue();
               seconds = value.getDuration() * (daysPerMonth * minutesPerDay * 60);
               break;
            }

            case TimeUnit.YEARS_VALUE:
            case TimeUnit.ELAPSED_YEARS_VALUE:
            {
               double minutesPerDay = m_projectFile.getProjectHeader().getMinutesPerDay().doubleValue();
               double daysPerMonth = m_projectFile.getProjectHeader().getDaysPerMonth().doubleValue();
               seconds = value.getDuration() * (12 * daysPerMonth * minutesPerDay * 60);
               break;
            }            
         }
         
         result = Long.toString((long)seconds);
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
   private String getScheduling (TaskType value)
   {
      String result = "fixed-work";
      if (value != null && value == TaskType.FIXED_DURATION)
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
   private String getString (String value)
   {
      return (value==null?"":value);
   }
   
   private ProjectFile m_projectFile;
   private ObjectFactory m_factory;
   private Project m_plannerProject;
   
   private NumberFormat m_twoDigitFormat = new DecimalFormat("00");
   private NumberFormat m_fourDigitFormat = new DecimalFormat("0000");
   
   private static Map<RelationType, String> RELATIONSHIP_TYPES = new HashMap<RelationType, String>();
   static
   {
      RELATIONSHIP_TYPES.put(RelationType.FINISH_FINISH, "FF");
      RELATIONSHIP_TYPES.put(RelationType.FINISH_START, "FS");
      RELATIONSHIP_TYPES.put(RelationType.START_FINISH, "SF");
      RELATIONSHIP_TYPES.put(RelationType.START_START, "SS");
   }

}
