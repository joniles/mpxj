/*
 * file:       MSPDIFile.java
 * author:     Jon Iles
 * copyright:  (c) Tapster Rock Limited 2002-2003
 * date:       20/02/2003
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

package com.tapsterrock.mspdi;

import com.tapsterrock.mpx.AccrueType;
import com.tapsterrock.mpx.BaseCalendar;
import com.tapsterrock.mpx.BaseCalendarException;
import com.tapsterrock.mpx.BaseCalendarHours;
import com.tapsterrock.mpx.ConstraintType;
import com.tapsterrock.mpx.CurrencySettings;
import com.tapsterrock.mpx.DateTimeSettings;
import com.tapsterrock.mpx.DefaultSettings;
import com.tapsterrock.mpx.MPXDuration;
import com.tapsterrock.mpx.MPXException;
import com.tapsterrock.mpx.MPXFile;
import com.tapsterrock.mpx.MPXRate;
import com.tapsterrock.mpx.Task;
import com.tapsterrock.mpx.TimeUnit;
import com.tapsterrock.mpx.Priority;
import com.tapsterrock.mpx.ProjectHeader;
import com.tapsterrock.mpx.Relation;
import com.tapsterrock.mpx.Resource;
import com.tapsterrock.mpx.ResourceAssignment;
import com.tapsterrock.mspdi.schema.Project;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.IOException;
import java.math.BigInteger;
import java.math.BigDecimal;
import java.net.URL;
import java.util.Date;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

/**
 * This class is used to represent a Microsoft Project Data Interchange
 * (MSPDI) XML file. This implementation allows the file to be read,
 * and the data it contains exported as a set of MPX objects.
 * These objects can be interrogated to retrieve any required data,
 * or stored as an MPX file.
 */
public class MSPDIFile extends MPXFile
{
   /**
    * Constructor allowing an MSPDI file to be read from an input stream
    *
    * @param stream an input stream
    * @throws MPXException on file read errors
    */
   public MSPDIFile (InputStream stream)
      throws MPXException
   {
      read (stream);
   }

   /**
    * Constructor allowing an MSPDI file to be read from a file object.
    *
    * @param file File object
    * @throws MPXException on file read errors
    */
   public MSPDIFile (File file)
      throws MPXException
   {
      read (file);
   }

   /**
    * Constructor allowing an MSPDI file to be read from a named file.
    *
    * @param filename File name
    * @throws MPXException on file read errors
    */
   public MSPDIFile (String filename)
      throws MPXException
   {
      read (new File (filename));
   }

   /**
    * This method implements reading from a File object and
    * maps any exceptions thrown into an MPXException.
    *
    * @param file File object
    * @throws MPXException on file read errors
    */
   private void read (File file)
     throws MPXException
   {
      try
      {
         FileInputStream fis = new FileInputStream (file);
         read (fis);
         fis.close();
      }

      catch (IOException ex)
      {
         throw new MPXException (MPXException.READ_ERROR, ex);
      }
   }

   /**
    * This method brings together all of the processing required to
    * extract data from an MSPDI file and populate the MPX data structures.
    *
    * @param stream Input stream
    * @throws MPXException on file read errors
    */
   private void read (InputStream stream)
      throws MPXException
   {
      try
      {
         //
         // Note that the line commented out below is the normal way to
         // initialise the context. A workaround has been applied to this
         // code to solve a problem in Sun's Beta 1.0 Reference Implementation
         // of JAXB. See the URL below for details.
         //
         // http://forum.java.sun.com/thread.jsp?forum=34&thread=320813
         //

         //JAXBContext context = JAXBContext.newInstance ("com.tapsterrock.mspdi.schema");
         JAXBContext context = JAXBContext.newInstance ("com.tapsterrock.mspdi.schema", new JAXBClassLoader(Thread.currentThread().getContextClassLoader()));

         Unmarshaller unmarshaller = context.createUnmarshaller ();
         Project project = (Project)unmarshaller.unmarshal (stream);
         HashMap calendarMap = new HashMap ();

         processCurrencySettings (project);
         processDateTimeSettings (project);
         processDefaultSettings (project);
         processProjectHeader (project);
         processCalendars (project, calendarMap);
         processResources (project, calendarMap);
         processTasks (project, calendarMap);
         processAssignments (project);
      }

      catch (JAXBException ex)
      {
         throw new MPXException ("Failed to parse file", ex);
      }
   }

   /**
    * This method extracts currency settings data from an MSPDI file.
    *
    * @param project Root node of the MSPDI file
    */
   private void processCurrencySettings (Project project)
   {
      CurrencySettings currency = getCurrencySettings();
      currency.setCurrencyDigits (getInteger(project.getCurrencyDigits()));
      currency.setCurrencySymbol (project.getCurrencySymbol());
      //currency.setDecimalSeparator ();
      currency.setSymbolPosition (getSymbolPosition(project.getCurrencySymbolPosition()));
      //currency.setThousandsSeparator ();
   }

   /**
    * This method extracts date and time settings data from an MSPDI file.
    *
    * @param project Root node of the MSPDI file
    */
   private void processDateTimeSettings (Project project)
   {
      DateTimeSettings settings = getDateTimeSettings();
      //settings.setAMText();
      //settings.setBarTextDateFormat();
      //settings.setDateFormat();
      //settings.setDateOrder();
      //settings.setDateSeparator();
      settings.setDefaultTime(getDate(project.getDefaultStartTime()));
      //settings.setPMText();
      //settings.setTimeFormat();
      //settings.setTimeSeparator();
   }


   /**
    * This method extracts default settings data from an MSPDI file.
    *
    * @param project Root node of the MSPDI file
    */
   private void processDefaultSettings (Project project)
   {
      DefaultSettings settings = getDefaultSettings();

      //settings.setDefaultDurationIsFixed();
      settings.setDefaultDurationUnits(getDurationUnits(project.getDurationFormat()));
      //settings.setDefaultHoursInDay();
      //settings.setDefaultHoursInWeek();
      // The default overtime rate always seems to be specified in hours
      settings.setDefaultOvertimeRate(new MPXRate(project.getDefaultOvertimeRate(), TimeUnit.HOURS));
      settings.setDefaultStandardRate(new MPXRate(project.getDefaultStandardRate(), TimeUnit.HOURS));
      settings.setDefaultWorkUnits(getWorkUnits (project.getWorkFormat()));
      settings.setSplitInProgressTasks(project.isSplitsInProgressTasks());
      settings.setUpdatingTaskStatusUpdatesResourceStatus(project.isTaskUpdatesResource());
   }

   /**
    * This method extracts project header data from an MSPDI file.
    *
    * @param project Root node of the MSPDI file
    */
   private void processProjectHeader (Project project)
   {
      ProjectHeader header = getProjectHeader ();
      //header.setActualCost();
      //header.setActualDuration();
      //header.setActualFinish();
      //header.setActualStart();
      //header.setActualWork();
      header.setAuthor(project.getAuthor());
      //header.setBaselineCost();
      //header.setBaselineDuration();
      //header.setBaselineFinish();
      //header.setBaselineStart();
      //header.setBaselineWork();
      //header.setCalendar();
      //header.setComments();
      header.setCompany(project.getCompany());
      //header.setCost();
      header.setCurrentDate(getDate (project.getCurrentDate()));
      //header.setDuration();
      header.setFinishDate(getDate (project.getFinishDate()));
      //header.setFinishVariance();
      //header.setKeywords();
      header.setManager(project.getManager());
      //header.setPercentageComplete();
      //header.setProjectTab();
      //header.setScheduleFrom();
      header.setStartDate(getDate (project.getStartDate()));
      //header.setStartVariance();
      header.setSubject(project.getSubject());
      //header.setWork();
      //header.setWork2();
   }

   /**
    * This method extracts calandar data from an MSPDI file.
    *
    * @param project Root node of the MSPDI file
    * @param map Map of calendar UIDs to names
    * @throws MPXException on file read errors
    */
   private void processCalendars (Project project, HashMap map)
      throws MPXException
   {
      Project.CalendarsType calendars = project.getCalendars();
      if (calendars != null)
      {
         List calendar = calendars.getCalendar();
         Iterator iter = calendar.iterator();

         while (iter.hasNext() == true)
         {
            processCalendar ((Project.CalendarsType.CalendarType)iter.next(), map);
         }
      }
   }

   /**
    * This method extracts data for a single calandar from an MSPDI file.
    *
    * @param calendar Calendar data
    * @param map Map of calendar UIDs to names
    * @throws MPXException on file read errors
    */
   private void processCalendar (Project.CalendarsType.CalendarType calendar, HashMap map)
      throws MPXException
   {
      BaseCalendar bc;
      Iterator iter;

      bc = addBaseCalendar();
      bc.setName(calendar.getName());

      Project.CalendarsType.CalendarType.WeekDaysType days = calendar.getWeekDays();
      if (days != null)
      {
         List day = days.getWeekDay();
         iter = day.iterator();

         while (iter.hasNext() == true)
         {
            processDay (bc, (Project.CalendarsType.CalendarType.WeekDaysType.WeekDayType)iter.next());
         }
      }

      map.put(calendar.getBaseCalendarUID(), bc.getName());
   }

   /**
    * This method extracts data for a single day from an MSPDI file.
    *
    * @param calendar Calendar data
    * @param day Day data
    * @throws MPXException on file read errors
    */
   private void processDay (BaseCalendar calendar, Project.CalendarsType.CalendarType.WeekDaysType.WeekDayType day)
      throws MPXException
   {
      BigInteger dayType = day.getDayType();
      if (dayType != null)
      {
         if (dayType.intValue() == 0)
         {
            processExceptionDay (calendar, day);
         }
         else
         {
            processNormalDay (calendar, day);
         }
      }
   }

   /**
    * This method extracts data for a normal working day from an MSPDI file.
    *
    * @param calendar Calendar data
    * @param day Day data
    * @throws MPXException on file read errors
    */
   private void processNormalDay (BaseCalendar calendar, Project.CalendarsType.CalendarType.WeekDaysType.WeekDayType day)
      throws MPXException
   {
      int dayNumber = day.getDayType().intValue() + 1;
      if (dayNumber == 8)
      {
         dayNumber = 1;
      }

      calendar.setWorkingDay(dayNumber, day.isDayWorking());
      BaseCalendarHours hours = calendar.addBaseCalendarHours();
      hours.setDay(dayNumber);

      Project.CalendarsType.CalendarType.WeekDaysType.WeekDayType.WorkingTimesType times = day.getWorkingTimes();
      if (times != null)
      {
         Project.CalendarsType.CalendarType.WeekDaysType.WeekDayType.WorkingTimesType.WorkingTimeType period;
         List time = times.getWorkingTime();
         Iterator iter = time.iterator();

         if (iter.hasNext() == true)
         {
            period = (Project.CalendarsType.CalendarType.WeekDaysType.WeekDayType.WorkingTimesType.WorkingTimeType)iter.next();
            hours.setFromTime1(getDate(period.getFromTime()));
            hours.setToTime1(getDate(period.getToTime()));
         }

         if (iter.hasNext() == true)
         {
            period = (Project.CalendarsType.CalendarType.WeekDaysType.WeekDayType.WorkingTimesType.WorkingTimeType)iter.next();
            hours.setFromTime2(getDate(period.getFromTime()));
            hours.setToTime2(getDate(period.getToTime()));
         }

         if (iter.hasNext() == true)
         {
            period = (Project.CalendarsType.CalendarType.WeekDaysType.WeekDayType.WorkingTimesType.WorkingTimeType)iter.next();
            hours.setFromTime3(getDate(period.getFromTime()));
            hours.setToTime3(getDate(period.getToTime()));
         }
      }
   }


   /**
    * This method extracts data for an exception day from an MSPDI file.
    *
    * @param calendar Calendar data
    * @param day Day data
    * @throws MPXException on file read errors
    */
   private void processExceptionDay (BaseCalendar calendar, Project.CalendarsType.CalendarType.WeekDaysType.WeekDayType day)
      throws MPXException
   {
      BaseCalendarException exception = calendar.addBaseCalendarException();

      Project.CalendarsType.CalendarType.WeekDaysType.WeekDayType.TimePeriodType timePeriod = day.getTimePeriod();
      exception.setFromDate(getDate(timePeriod.getFromDate()));
      exception.setToDate(getDate(timePeriod.getToDate()));

      Project.CalendarsType.CalendarType.WeekDaysType.WeekDayType.WorkingTimesType times = day.getWorkingTimes();
      if (times != null)
      {
         Project.CalendarsType.CalendarType.WeekDaysType.WeekDayType.WorkingTimesType.WorkingTimeType period;
         List time = times.getWorkingTime();
         Iterator iter = time.iterator();

         if (iter.hasNext() == true)
         {
            period = (Project.CalendarsType.CalendarType.WeekDaysType.WeekDayType.WorkingTimesType.WorkingTimeType)iter.next();
            exception.setFromTime1(getDate(period.getFromTime()));
            exception.setToTime1(getDate(period.getToTime()));
         }

         if (iter.hasNext() == true)
         {
            period = (Project.CalendarsType.CalendarType.WeekDaysType.WeekDayType.WorkingTimesType.WorkingTimeType)iter.next();
            exception.setFromTime2(getDate(period.getFromTime()));
            exception.setToTime2(getDate(period.getToTime()));
         }

         if (iter.hasNext() == true)
         {
            period = (Project.CalendarsType.CalendarType.WeekDaysType.WeekDayType.WorkingTimesType.WorkingTimeType)iter.next();
            exception.setFromTime3(getDate(period.getFromTime()));
            exception.setToTime3(getDate(period.getToTime()));
         }
      }
   }

   /**
    * This method extracts resource data from an MSPDI file.
    *
    * @param project Root node of the MSPDI file
    * @param calendarMap Map of calendar UIDs to names
    * @throws MPXException on file read errors
    */
   private void processResources (Project project, HashMap calendarMap)
      throws MPXException
   {
      Project.ResourcesType resources = project.getResources();
      if (resources != null)
      {
         List resource = resources.getResource();
         Iterator iter = resource.iterator();
         while (iter.hasNext() == true)
         {
            processResource ((Project.ResourcesType.ResourceType)iter.next(), calendarMap);
         }
      }
   }

   /**
    * This method extracts data for a single resource from an MSPDI file.
    *
    * @param resource Resource data
    * @param calendarMap Map of calendar UIDs to names
    * @throws MPXException on file read errors
    * @todo handle null values for new XXX(resource.getXXX())
    */
   private void processResource (Project.ResourcesType.ResourceType resource, HashMap calendarMap)
      throws MPXException
   {
      Resource mpx = addResource();

      mpx.setAccrueAt(AccrueType.getInstance(resource.getAccrueAt()));
      mpx.setActualCost(getCurrency(resource.getActualCost()));
      mpx.setActualWork(getDuration (resource.getActualWork()));
      mpx.setBaseCalendar ((String)calendarMap.get(resource.getCalendarUID()));
      //mpx.setBaselineCost();
      //mpx.setBaselineWork();
      mpx.setCode(resource.getCode());
      mpx.setCost(getCurrency(resource.getCost()));
      mpx.setCostPerUse(getCurrency(resource.getCostPerUse()));
      mpx.setCostVariance(resource.getCostVariance()/100);
      mpx.setEmailAddress(resource.getEmailAddress());
      mpx.setGroup(resource.getGroup());
      mpx.setID(getInteger(resource.getID()));
      mpx.setInitials(resource.getInitials());
      //mpx.setLinkedFields();
      mpx.setMaxUnits(resource.getMaxUnits()*100);
      mpx.setName(resource.getName());
      mpx.setNotes(resource.getNotes());
      //mpx.setObjects();
      //mpx.setOverallocated();
      mpx.setOvertimeRate(new MPXRate (resource.getOvertimeRate(), TimeUnit.HOURS));
      mpx.setOvertimeWork(getDuration (resource.getOvertimeWork()));
      mpx.setPeak(resource.getPeakUnits() * 100);
      mpx.setPercentageWorkComplete(resource.getPercentWorkComplete());
      mpx.setRemainingCost(getCurrency(resource.getRemainingCost()));
      mpx.setRemainingWork(getDuration (resource.getRemainingWork()));
      mpx.setStandardRate(new MPXRate (resource.getStandardRate(), TimeUnit.HOURS));
      //mpx.setText1();
      //mpx.setText2();
      //mpx.setText3();
      //mpx.setText4();
      //mpx.setText5();
      mpx.setUniqueID(getInteger(resource.getUID()));
      mpx.setWork(getDuration (resource.getWork()));
      mpx.setWorkVariance(new MPXDuration (resource.getWorkVariance()/1000, TimeUnit.MINUTES));
   }


   /**
    * This method extracts task data from an MSPDI file.
    *
    * @param project Root node of the MSPDI file
    * @param calendarMap Map of calendar UIDs to names
    * @throws MPXException on file read errors
    */
   private void processTasks (Project project, HashMap calendarMap)
      throws MPXException
   {
      Project.TasksType tasks = project.getTasks();
      if (tasks != null)
      {
         List task = tasks.getTask();
         Iterator iter = task.iterator();
         while (iter.hasNext() == true)
         {
            processTask ((Project.TasksType.TaskType)iter.next(), calendarMap);
         }

         iter = task.iterator();
         while (iter.hasNext() == true)
         {
            processPredecessors ((Project.TasksType.TaskType)iter.next());
         }
      }

      updateStructure ();
   }


   /**
    * This method extracts data for a single task from an MSPDI file.
    *
    * @param task Task data
    * @param calendarMap Map of calendar UIDs to names
    * @throws MPXException on file read errors
    */
   private void processTask (Project.TasksType.TaskType task, HashMap calendarMap)
      throws MPXException
   {
      Task mpx = addTask ();

      mpx.setActualCost(getCurrency (task.getActualCost()));
      mpx.setActualDuration(getDuration (task.getActualDuration()));
      mpx.setActualFinish(getDate (task.getActualFinish()));
      mpx.setActualStart(getDate (task.getActualStart()));
      mpx.setActualWork(getDuration (task.getActualWork()));
      //mpx.setBaselineCost();
      //mpx.setBaselineDuration();
      //mpx.setBaselineFinish();
      //mpx.setBaselineStart();
      //mpx.setBaselineWork();
      //mpx.setBCWP();
      //mpx.setBCWS();
      //mpx.setConfirmed();
      mpx.setConstraintDate(getDate(task.getConstraintDate()));
      mpx.setConstraintType(ConstraintType.getInstance(task.getConstraintType()));
      mpx.setContact(task.getContact());
      mpx.setCost(getCurrency(task.getCost()));
      //mpx.setCost1();
      //mpx.setCost2();
      //mpx.setCost3();
      //mpx.setCostVariance();
      mpx.setCreated(getDate(task.getCreateDate()));
      mpx.setCritical(task.isCritical());
      mpx.setCV(task.getCV()/100);
      //mpx.setDelay();
      mpx.setDuration(getDuration (task.getDuration()));
      //mpx.setDuration1();
      //mpx.setDuration2();
      //mpx.setDuration3();
      //mpx.setDurationVariance();
      mpx.setEarlyFinish(getDate(task.getEarlyFinish()));
      mpx.setEarlyStart(getDate(task.getEarlyStart()));
      mpx.setFinish(getDate(task.getFinish()));
      //mpx.setFinish1();
      //mpx.setFinish2();
      //mpx.setFinish3();
      //mpx.setFinish4();
      //mpx.setFinish5();
      mpx.setFinishVariance(getMinutesDuration(task.getFinishVariance()));
      //mpx.setFixed();
      mpx.setFixedCost(task.getFixedCost()/100);
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
      mpx.setFreeSlack(getMinutesDuration(task.getFreeSlack()));
      //mpx.setHideBar();
      mpx.setID(getInteger(task.getID()));
      mpx.setLateFinish(getDate(task.getLateFinish()));
      mpx.setLateStart(getDate(task.getLateStart()));
      //mpx.setLinkedFields();
      //mpx.setMarked();
      mpx.setMilestone(task.isMilestone());
      mpx.setName(task.getName());
      mpx.setNotes(task.getNotes());
      //mpx.setNumber1();
      //mpx.setNumber2();
      //mpx.setNumber3();
      //mpx.setNumber4();
      //mpx.setNumber5();
      //mpx.setObjects();
      mpx.setOutlineLevel(getInteger(task.getOutlineLevel()));
      mpx.setOutlineNumber(task.getOutlineNumber());
      mpx.setPercentageComplete(task.getPercentComplete());
      mpx.setPercentageWorkComplete(task.getPercentWorkComplete());
      mpx.setPriority(getPriority(task.getPriority()));
      //mpx.setProject();
      mpx.setRemainingCost(getCurrency(task.getRemainingCost()));
      mpx.setRemainingDuration(getDuration(task.getRemainingDuration()));
      mpx.setRemainingWork(getDuration (task.getRemainingWork()));
      //mpx.setResourceGroup();
      //mpx.setResourceInitials();
      //mpx.setResourceNames();
      mpx.setResume(getDate(task.getResume()));
      //mpx.setResumeNoEarlierThan();
      mpx.setRollup(task.isRollup());
      mpx.setStart(getDate(task.getStart()));
      //mpx.setStart1();
      //mpx.setStart2();
      //mpx.setStart3();
      //mpx.setStart4();
      //mpx.setStart5();
      mpx.setStartVariance(getMinutesDuration(task.getStartVariance()));
      mpx.setStop(getDate(task.getStop()));
      //mpx.setSubprojectFile();
      //mpx.setSuccessors();
      mpx.setSummary(task.isSummary());
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
      mpx.setTotalSlack(getMinutesDuration(task.getTotalSlack()));
      mpx.setUniqueID(getInteger(task.getUID()));
      //mpx.setUpdateNeeded();
      mpx.setWBS(task.getWBS());
      mpx.setWork(getDuration(task.getWork()));
      mpx.setWorkVariance(new MPXDuration (task.getWorkVariance()/1000, TimeUnit.MINUTES));
   }

   /**
    * This method extracts predecessor data from an MSPDI file.
    *
    * @param task Task data
    */
   private void processPredecessors (Project.TasksType.TaskType task)
   {
      BigInteger uid = task.getUID();
      if (uid != null)
      {
         Task currTask = getTaskByUniqueID(uid.intValue());
         if (currTask != null)
         {
            List predecessors = task.getPredecessorLink();
            Iterator iter = predecessors.iterator();

            while (iter.hasNext() == true)
            {
               processPredecessor (currTask, (Project.TasksType.TaskType.PredecessorLinkType)iter.next());
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
   private void processPredecessor (Task currTask, Project.TasksType.TaskType.PredecessorLinkType link)
   {
      BigInteger uid = link.getPredecessorUID();
      if (uid != null)
      {
         Task prevTask = getTaskByUniqueID(uid.intValue());
         if (prevTask != null)
         {
            int type;
            if (link.getType() != null)
            {
               type = link.getType().intValue();
            }
            else
            {
               type = Relation.FINISH_START;
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
            rel.setDuration(new MPXDuration (lag, TimeUnit.HOURS));
         }
      }
   }

   /**
    * This method extracts assignment data from an MSPDI file.
    *
    * @param project Root node of the MSPDI file
    * @throws MPXException on file read errors
    */
   private void processAssignments (Project project)
      throws MPXException
   {
      Project.AssignmentsType assignments = project.getAssignments();
      if (assignments != null)
      {
         List assignment = assignments.getAssignment();
         Iterator iter = assignment.iterator();
         while (iter.hasNext() == true)
         {
            processAssignment ((Project.AssignmentsType.AssignmentType)iter.next());
         }
      }
   }


   /**
    * This method extracts data for a single assignment from an MSPDI file.
    *
    * @param assignment Assignment data
    * @throws MPXException on file read errors
    */
   private void processAssignment (Project.AssignmentsType.AssignmentType assignment)
      throws MPXException
   {
      BigInteger taskUID = assignment.getTaskUID();
      BigInteger resourceUID = assignment.getResourceUID();
      if (taskUID != null && resourceUID != null)
      {
         Task task = getTaskByUniqueID(taskUID.intValue());
         Resource resource = getResourceByUniqueID(resourceUID.intValue());

         if (task != null && resource != null)
         {
            ResourceAssignment mpx = task.addResourceAssignment(resource);
            mpx.setActualCost(getCurrency(assignment.getActualCost()));
            mpx.setActualWork(getDuration(assignment.getActualWork()));
            mpx.setCost(getCurrency(assignment.getCost()));
            mpx.setDelay(getMinutesDuration(assignment.getDelay()));
            mpx.setFinish(getDate(assignment.getFinish()));
            mpx.setOvertimeWork(getDuration(assignment.getOvertimeWork()));
            //mpx.setPlannedCost();
            //mpx.setPlannedWork();
            mpx.setStart(getDate(assignment.getStart()));
            mpx.setUnits(assignment.getUnits()*100);
            mpx.setWork(getDuration(assignment.getWork()));
         }
      }
   }

   /**
    * Utility method used to convert a BigInteger into an Integer.
    *
    * @param value BigInteger value
    * @return Integer value
    */
   private Integer getInteger (BigInteger value)
   {
      Integer result = null;

      if (value != null)
      {
         result = new Integer (value.intValue());
      }

      return (result);
   }

   /**
    * Utility to convert a Calendar instance into a Date instance.
    *
    * @param value Calendar value
    * @return Date value
    */
   private Date getDate (Calendar value)
   {
      Date result = null;

      if (value != null)
      {
         result = value.getTime ();
      }

      return (result);
   }

   /**
    * Utility method to convert a BigInteger into
    * work units.
    *
    * @param value BigInteger value
    * @return work units
    */
   private int getWorkUnits (BigInteger value)
   {
      int result = TimeUnit.HOURS;

      if (value != null)
      {
         switch (value.intValue())
         {
            case 1:
            {
               result = TimeUnit.MINUTES;
               break;
            }

            case 3:
            {
               result = TimeUnit.DAYS;
               break;
            }

            case 4:
            {
               result = TimeUnit.WEEKS;
               break;
            }

            case 5:
            {
               result = TimeUnit.MONTHS;
               break;
            }

            case 7:
            {
               result = TimeUnit.YEARS;
               break;
            }

            default:
            case 2:
            {
               result = TimeUnit.HOURS;
               break;
            }
         }
      }

      return (result);
   }

   /**
    * Utility method to convert an xsd:duration into an MPXDuration.
    *
    * @param text xsd:duration value
    * @return MPXDuration
    */
   private MPXDuration getDuration (String text)
   {
      MPXDuration result = null;

      if (text != null && text.length() != 0)
      {
         XsdDuration xsd = new XsdDuration (text);
         int units = TimeUnit.DAYS;

         if (xsd.getSeconds() != 0 || xsd.getMinutes() != 0)
         {
            units = TimeUnit.MINUTES;
         }

         if (xsd.getHours() != 0)
         {
            units = TimeUnit.HOURS;
         }

         if (xsd.getDays() != 0)
         {
            units = TimeUnit.DAYS;
         }

         if (xsd.getMonths() != 0)
         {
            units = TimeUnit.MONTHS;
         }

         if (xsd.getYears() != 0)
         {
            units = TimeUnit.YEARS;
         }

         int duration = 0;

         switch (units)
         {
            case TimeUnit.YEARS:
            case TimeUnit.ELAPSED_YEARS:
            {
               duration += xsd.getYears();
               duration += (xsd.getMonths() / 12);
               duration += (xsd.getDays() / 365);
               duration += (xsd.getHours() / (365 * 24));
               duration += (xsd.getMinutes() / (365 * 24 * 60));
               duration += (xsd.getSeconds() / (365 * 24 * 60 * 60));
               break;
            }

            case TimeUnit.MONTHS:
            case TimeUnit.ELAPSED_MONTHS:
            {
               duration += (xsd.getYears() * 12);
               duration += xsd.getMonths();
               duration += (xsd.getDays() / 30);
               duration += (xsd.getHours() / (30 * 24));
               duration += (xsd.getMinutes() / (30 * 24 * 60));
               duration += (xsd.getSeconds() / (30 * 24 * 60 * 60));
               break;
            }

            case TimeUnit.WEEKS:
            case TimeUnit.ELAPSED_WEEKS:
            {
               duration += (xsd.getYears() * 52);
               duration += (xsd.getMonths() * 4);
               duration += (xsd.getDays() / 7);
               duration += (xsd.getHours() / (7 * 24));
               duration += (xsd.getMinutes() / (7 * 24 * 60));
               duration += (xsd.getSeconds() / (7 * 24 * 60 * 60));
               break;
            }

            case TimeUnit.DAYS:
            case TimeUnit.ELAPSED_DAYS:
            {
               duration += (xsd.getYears() * 365);
               duration += (xsd.getMonths() * 30);
               duration += xsd.getDays();
               duration += (xsd.getHours() / 24);
               duration += (xsd.getMinutes() / (24 * 60));
               duration += (xsd.getSeconds() / (24 * 60 * 60));
               break;
            }

            case TimeUnit.HOURS:
            case TimeUnit.ELAPSED_HOURS:
            {
               duration += (xsd.getYears() * (365 * 24));
               duration += (xsd.getMonths() * (30 * 24));
               duration += (xsd.getDays() * 24);
               duration += xsd.getHours();
               duration += (xsd.getMinutes() / 60);
               duration += (xsd.getSeconds() / (60 * 60));
               break;
            }

            case TimeUnit.MINUTES:
            case TimeUnit.ELAPSED_MINUTES:
            {
               duration += (xsd.getYears() * (365 * 24 * 60));
               duration += (xsd.getMonths() * (30 * 24 * 60));
               duration += (xsd.getDays() * (24 * 60));
               duration += (xsd.getHours() * 60);
               duration += xsd.getMinutes();
               duration += (xsd.getSeconds() / 60);
               break;
            }
         }

         result = new MPXDuration (duration, units);
      }

      return (result);
   }

   /**
    * Utility method to convert a BigInteger into a symbol position.
    *
    * @param position BigInteger position value
    * @return Symbol position
    */
   private int getSymbolPosition (BigInteger position)
   {
      int result = CurrencySettings.SYMBOLPOS_BEFORE;

      if (position != null)
      {
         switch (position.intValue())
         {
            case 0:
            {
               result = CurrencySettings.SYMBOLPOS_BEFORE;
               break;
            }

            case 1:
            {
               result = CurrencySettings.SYMBOLPOS_AFTER;
               break;
            }

            case 2:
            {
               result = CurrencySettings.SYMBOLPOS_BEFORE_WITH_SPACE;
               break;
            }

            case 3:
            {
               result = CurrencySettings.SYMBOLPOS_AFTER_WITH_SPACE;
               break;
            }
         }
      }

      return (result);
   }

   /**
    * Utility method to convert a BigDecimal into a currency value.
    *
    * @param value BigDecimal value
    * @return Currency value
    */
   private Double getCurrency (BigDecimal value)
   {
      Double result = null;

      if (value != null)
      {
         result = new Double (value.doubleValue() / 100);
      }

      return (result);
   }

   /**
    * Utility method to convert a BigInteger value into duration units.
    * Note that we don't differentiate between confirmed and unconfirmed
    * durations. Unrecognised duration types are default to hours.
    *
    * @param value BigInteger value
    * @return Duration units
    */
   private int getDurationUnits (BigInteger value)
   {
      int result = TimeUnit.HOURS;

      if (value != null)
      {
         switch (value.intValue())
         {
            case 3:
            case 35:
            {
               result = TimeUnit.MONTHS;
               break;
            }

            case 4:
            case 36:
            {
               result = TimeUnit.ELAPSED_MONTHS;
               break;
            }

            case 5:
            case 37:
            {
               result = TimeUnit.HOURS;
               break;
            }

            case 6:
            case 38:
            {
               result = TimeUnit.ELAPSED_HOURS;
               break;
            }

            case 7:
            case 39:
            {
               result = TimeUnit.DAYS;
               break;
            }

            case 8:
            case 40:
            {
               result = TimeUnit.ELAPSED_DAYS;
               break;
            }

            case 9:
            case 41:
            {
               result = TimeUnit.WEEKS;
               break;
            }

            case 10:
            case 42:
            {
               result = TimeUnit.ELAPSED_WEEKS;
               break;
            }

            case 11:
            case 43:
            {
               result = TimeUnit.MONTHS;
               break;
            }

            case 12:
            case 44:
            {
               result = TimeUnit.ELAPSED_MONTHS;
               break;
            }

            case 19:
            case 51:
            {
               result = TimeUnit.PERCENT;
               break;
            }

            case 20:
            case 52:
            {
               result = TimeUnit.ELAPSED_PERCENT;
               break;
            }
         }
      }

      return (result);
   }

   /**
    * Utility method to convert a BigInteger value
    * into a priority.
    *
    * @param priority BigInteger value
    * @return Priority value
    */
   private Priority getPriority (BigInteger priority)
   {
      int result = Priority.MEDIUM;

      if (priority != null)
      {
         if (priority.intValue() >= 1000)
         {
            result = Priority.DO_NOT_LEVEL;
         }
         else
         {
            result = priority.intValue() / 100;
         }
      }

      return (Priority.getInstance (result));
   }

   /**
    * Utility method to convert a duration expressed in minutes * 1000
    * as a BigInteger into an MPXDuration.
    *
    * @param value BigInteger value
    * @return MPXDuration
    */
   private MPXDuration getMinutesDuration (BigInteger value)
   {
      MPXDuration result = null;

      if (value != null)
      {
         result = new MPXDuration (value.intValue()/1000, TimeUnit.MINUTES);
      }

      return (result);
   }

   /**
    * This class is used to provide a workaround for a bug in the Beta 1.0
    * release of Sun's JAXB Reference Implementation. See the URL below
    * for details.
    *
    * http://forum.java.sun.com/thread.jsp?forum=34&thread=320813
    */
   private class JAXBClassLoader extends ClassLoader
   {
      public JAXBClassLoader (ClassLoader aClassLoader)
      {
         super (aClassLoader);
      }

      public URL getResource (String name)
      {
         return (super.getResource(name.replace('\\','/')));
      }
   }
}

