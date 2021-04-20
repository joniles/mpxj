/*
 * file:       ProjectCommanderReader.java
 * author:     Jon Iles
 * copyright:  (c) Packwood Software 2020
 * date:       24/05/2020
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

package net.sf.mpxj.projectcommander;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import net.sf.mpxj.DateRange;
import net.sf.mpxj.Day;
import net.sf.mpxj.Duration;
import net.sf.mpxj.EventManager;
import net.sf.mpxj.MPXJException;
import net.sf.mpxj.ProjectCalendar;
import net.sf.mpxj.ProjectCalendarException;
import net.sf.mpxj.ProjectCalendarHours;
import net.sf.mpxj.ProjectConfig;
import net.sf.mpxj.ProjectFile;
import net.sf.mpxj.RelationType;
import net.sf.mpxj.Resource;
import net.sf.mpxj.ResourceAssignment;
import net.sf.mpxj.Task;
import net.sf.mpxj.TimeUnit;
import net.sf.mpxj.common.DateHelper;
import net.sf.mpxj.common.NumberHelper;
import net.sf.mpxj.reader.AbstractProjectStreamReader;

/**
 * Reads schedule data from a Project Commander file.
 */
public final class ProjectCommanderReader extends AbstractProjectStreamReader
{
   /**
    * {@inheritDoc}
    */
   @Override public ProjectFile read(InputStream is) throws MPXJException
   {
      try
      {
         m_projectFile = new ProjectFile();
         m_projectFile.getProjectProperties().setFileApplication("Project Commander");
         m_projectFile.getProjectProperties().setFileType("PC");

         ProjectConfig config = m_projectFile.getProjectConfig();
         config.setAutoTaskUniqueID(false);
         config.setAutoResourceUniqueID(false);

         m_eventManager = m_projectFile.getEventManager();
         m_taskMap = new TreeMap<>();
         m_childTaskCounts = new TreeMap<>();
         m_extraBarCounts = new HashMap<>();

         m_data = new ProjectCommanderData();
         //m_data.setLogFile("c:/temp/project-commander.log");
         m_data.process(is);

         readCalendars();
         readResources();
         readTasks();
         readRelationships();

         return m_projectFile;
      }

      catch (IOException ex)
      {
         throw new MPXJException(MPXJException.READ_ERROR, ex);
      }

      finally
      {
         m_eventManager = null;
         m_taskMap = null;
         m_childTaskCounts = null;
         m_extraBarCounts = null;
         m_data = null;
      }
   }

   /**
    * {@inheritDoc}
    */
   @Override public List<ProjectFile> readAll(InputStream inputStream) throws MPXJException
   {
      return Arrays.asList(read(inputStream));
   }

   /**
    * Read calendars from the file.
    */
   private void readCalendars()
   {
      m_data.getBlocks().stream().filter(block -> "CCalendar".equals(block.getName())).forEach(block -> readCalendar(block));
   }

   /**
    * Read tasks from the file.
    */
   private void readTasks()
   {
      m_data.getBlocks().stream().filter(block -> "CTask".equals(block.getName())).forEach(block -> readTask(block));
      updateStructure();
      updateDates();
   }

   /**
    * Read resources from the file.
    */
   private void readResources()
   {
      m_data.getBlocks().stream().filter(block -> "CResource".equals(block.getName())).forEach(block -> readResource(block));
      updateResourceUniqueIDValues();
   }

   /**
    * Read task relationships from the file.
    */
   private void readRelationships()
   {
      for (Entry<Task, Block> entry : m_taskMap.entrySet())
      {
         entry.getValue().getChildBlocks().stream().filter(x -> "CLink".equals(x.getName())).forEach(x -> readRelationships(entry.getKey(), x));
      }
   }

   /**
    * Read an individual calendar from the file.
    *
    * @param block CCalendar block
    * @return ProjectCalendar instance
    */
   private ProjectCalendar readCalendar(Block block)
   {
      ProjectCalendar calendar;

      byte[] data = block.getData();
      String name = DatatypeConverter.getString(data, 0, null);

      if (name == null || name.trim().isEmpty())
      {
         calendar = null;
      }
      else
      {
         int offset = 1 + name.length();

         calendar = m_projectFile.addCalendar();
         calendar.setName(name);

         // This is guesswork - need some samples with more variation
         int workingDays = DatatypeConverter.getByte(data, offset);
         calendar.setWorkingDay(Day.SATURDAY, (workingDays & 0x40) != 0);
         calendar.setWorkingDay(Day.SUNDAY, (workingDays & 0x20) != 0);
         calendar.setWorkingDay(Day.MONDAY, (workingDays & 0x10) != 0);
         calendar.setWorkingDay(Day.TUESDAY, (workingDays & 0x08) != 0);
         calendar.setWorkingDay(Day.WEDNESDAY, (workingDays & 0x04) != 0);
         calendar.setWorkingDay(Day.THURSDAY, (workingDays & 0x02) != 0);
         calendar.setWorkingDay(Day.FRIDAY, (workingDays & 0x01) != 0);
         offset += 28;

         Map<Day, List<DateRange>> ranges = new HashMap<>();
         ranges.put(Day.SATURDAY, readCalendarHours(data, offset + 0));
         ranges.put(Day.SUNDAY, readCalendarHours(data, offset + 16));
         ranges.put(Day.MONDAY, readCalendarHours(data, offset + 32));
         ranges.put(Day.TUESDAY, readCalendarHours(data, offset + 48));
         ranges.put(Day.WEDNESDAY, readCalendarHours(data, offset + 64));
         ranges.put(Day.THURSDAY, readCalendarHours(data, offset + 80));
         ranges.put(Day.FRIDAY, readCalendarHours(data, offset + 96));

         for (Day day : DAYS)
         {
            if (calendar.isWorkingDay(day))
            {
               ProjectCalendarHours hours = calendar.addCalendarHours(day);
               ranges.get(day).stream().forEach(range -> hours.addRange(range));
            }
         }

         block.getChildBlocks().stream().filter(x -> "CDayFlag".equals(x.getName())).forEach(x -> readCalendarException(calendar, ranges, x.getData()));

         m_eventManager.fireCalendarReadEvent(calendar);
      }

      return calendar;
   }

   /**
    * Read working hours for a day from a byte array.
    *
    * @param data calendar data
    * @param offset offset into calendar data
    * @return list of DateRange instances representing working hours
    */
   private List<DateRange> readCalendarHours(byte[] data, int offset)
   {
      List<DateRange> ranges = new ArrayList<>();
      addRange(ranges, DatatypeConverter.getInt(data, offset), DatatypeConverter.getInt(data, offset + 4));
      addRange(ranges, DatatypeConverter.getInt(data, offset + 8), DatatypeConverter.getInt(data, offset + 12));
      return ranges;
   }

   /**
    * Read a valid start and end time from a byte aray.
    *
    * @param ranges target DateRange list
    * @param startMinutes start time in minutes
    * @param endMinutes end time in minutes
    */
   private void addRange(List<DateRange> ranges, int startMinutes, int endMinutes)
   {
      if (startMinutes != endMinutes)
      {
         Date start = DateHelper.getTimeFromMinutesPastMidnight(Integer.valueOf(startMinutes));
         Date end = DateHelper.getTimeFromMinutesPastMidnight(Integer.valueOf(endMinutes));
         ranges.add(new DateRange(start, end));
      }
   }

   /**
    * Read a calendar exception.
    *
    * @param calendar parent calendar
    * @param ranges default day of week working times
    * @param data byte array
    */
   private void readCalendarException(ProjectCalendar calendar, Map<Day, List<DateRange>> ranges, byte[] data)
   {
      long timestampInDays = DatatypeConverter.getShort(data, 2, 0);

      // Heuristic to filter out odd exception dates
      if (timestampInDays > 0xFF)
      {
         long timestampInMilliseconds = timestampInDays * 24 * 60 * 60 * 1000;
         Date exceptionDate = DateHelper.getTimestampFromLong(timestampInMilliseconds);

         Calendar cal = DateHelper.popCalendar();
         cal.setTime(exceptionDate);
         Day day = Day.getInstance(cal.get(Calendar.DAY_OF_WEEK));
         DateHelper.pushCalendar(cal);

         ProjectCalendarException ex = calendar.addCalendarException(exceptionDate, exceptionDate);
         if (!calendar.isWorkingDay(day))
         {
            ranges.get(day).stream().forEach(range -> ex.addRange(range));
         }
      }
   }

   /**
    * Read a task from a CTask block.
    *
    * @param block CTask block byte array
    */
   private void readTask(Block block)
   {
      byte[] cTaskData = block.getData();
      int offset = 0;

      String name = DatatypeConverter.getString(cTaskData, offset, null);
      if (name == null)
      {
         return;
      }

      Block[] baselines = getChildBlocks(block, "CBaselineData").toArray(x -> new Block[x]);
      if (baselines.length == 0)
      {
         readSummaryTask(block, name);
      }
      else
      {
         readChildTasks(block, name, baselines[0]);
      }
   }

   /**
    * Read a summary task.
    *
    * @param block task data
    * @param name task name
    */
   private void readSummaryTask(Block block, String name)
   {
      byte[] cTaskData = block.getData();
      int offset = name.length() + 1;

      Task task = m_projectFile.addTask();
      task.setName(name);

      int childTaskCount = DatatypeConverter.getShort(cTaskData, offset + 405, 0);
      if (childTaskCount != 0)
      {
         m_childTaskCounts.put(task.getID(), Integer.valueOf(DatatypeConverter.getShort(cTaskData, offset + 405, 0)));
      }

      // We don't have early/late start/finish.
      // Set attributes here to avoid trying to calculate them.
      task.setStartSlack(Duration.getInstance(0, TimeUnit.DAYS));
      task.setFinishSlack(Duration.getInstance(0, TimeUnit.DAYS));
      task.setCritical(false);
            
      m_eventManager.fireTaskReadEvent(task);
   }

   /**
    * Read one or more child tasks.
    *
    * @param block task data
    * @param name task name
    * @param baseline task baseline data
    */
   private void readChildTasks(Block block, String name, Block baseline)
   {
      Block cUsageTask = getChildBlock(block, "CUsageTask");
      byte[] cUsageTaskBaselineData = getByteArray(cUsageTask, "CBaselineData");
      Resource resource = readChildTaskResource(cUsageTask);
      List<Task> tasks = getChildBlocks(baseline, "CBar").map(bar -> readChildTask(name, bar, cUsageTaskBaselineData, resource)).collect(Collectors.toList());
      if (tasks.size() > 1)
      {
         m_extraBarCounts.put(tasks.get(0), Integer.valueOf(tasks.size() - 1));
      }
   }

   /**
    * Read resource assignments.
    *
    * @param cUsageTask task data
    * @return assigned resource
    */
   private Resource readChildTaskResource(Block cUsageTask)
   {
      Resource result;
      if (cUsageTask == null)
      {
         result = null;
      }
      else
      {
         Integer resourceUniqueID = Integer.valueOf(DatatypeConverter.getShort(cUsageTask.getData(), 9));
         result = m_projectFile.getResourceByUniqueID(resourceUniqueID);
      }
      return result;
   }

   /**
    * Read an individual child task.
    *
    * @param name task name
    * @param bar bar block
    * @param cUsageTaskBaselineData baseline data
    * @param resource assigned resource
    * @return Task instance
    */
   private Task readChildTask(String name, Block bar, byte[] cUsageTaskBaselineData, Resource resource)
   {
      Task task = m_projectFile.addTask();
      m_taskMap.put(task, bar);
      task.setName(name);

      byte[] cBarData = bar.getData();

      int uniqueID = DatatypeConverter.getShort(cBarData, 23, 0);
      task.setUniqueID(Integer.valueOf(uniqueID));

      if (cBarData[0] == 0x02)
      {
         ProjectCalendar calendar = m_projectFile.getDefaultCalendar();
         task.setDuration(Duration.getInstance(0, TimeUnit.DAYS));
         task.setMilestone(true);
         Date startDate = DatatypeConverter.getTimestamp(cBarData, 7);
         task.setStart(DateHelper.setTime(startDate, calendar.getStartTime(startDate)));
         task.setFinish(calendar.getDate(task.getStart(), task.getDuration(), false));
      }
      else
      {
         if (cUsageTaskBaselineData.length != 0)
         {
            Duration durationInHours = null;

            // If we're not the first bar, is our duration different to the first bar?
            // This is very much a heuristic!
            int potentialDuration = DatatypeConverter.getInt(cBarData, 97, 0);
            if (potentialDuration != 0 && (potentialDuration & 0xFF000000) == 0)
            {
               durationInHours = DatatypeConverter.getDuration(cBarData, 97);
            }
            else
            {
               potentialDuration = DatatypeConverter.getInt(cUsageTaskBaselineData, 433, 0);
               if (potentialDuration != 0 && (potentialDuration & 0xFF000000) == 0)
               {
                  durationInHours = DatatypeConverter.getDuration(cUsageTaskBaselineData, 433);
               }
               else
               {
                  durationInHours = Duration.getInstance(0, TimeUnit.HOURS);
               }
            }

            task.setDuration(durationInHours.convertUnits(TimeUnit.DAYS, m_projectFile.getProjectProperties()));
            task.setWork(durationInHours);

            ProjectCalendar calendar = m_projectFile.getDefaultCalendar();
            Date startDate = DatatypeConverter.getTimestamp(cBarData, 5);
            task.setStart(DateHelper.setTime(startDate, calendar.getStartTime(startDate)));
            task.setFinish(calendar.getDate(task.getStart(), task.getDuration(), false));

            if (resource != null)
            {
               ResourceAssignment assignment = task.addResourceAssignment(resource);
               assignment.setWork(durationInHours);
               assignment.setRemainingWork(durationInHours);
            }
         }
      }

      // We don't have early/late start/finish.
      // Set attributes here to avoid trying to calculate them.
      task.setStartSlack(Duration.getInstance(0, TimeUnit.DAYS));
      task.setFinishSlack(Duration.getInstance(0, TimeUnit.DAYS));
      task.setCritical(false);

      m_eventManager.fireTaskReadEvent(task);

      return task;
   }

   /**
    * Retrieve a byte array representing the content of a child block.
    * Returns an empty array if the child block is not present.
    *
    * @param block parent block
    * @param name child block name
    * @return child block byte array
    */
   private byte[] getByteArray(Block block, String name)
   {
      Block childBlock = getChildBlock(block, name);
      return childBlock == null ? EMPTY_BYTE_ARRAY : childBlock.getData();
   }

   /**
    * Retrieve a named child block.
    *
    * @param block parent block
    * @param name child block name
    * @return child block, or null if not present
    */
   private Block getChildBlock(Block block, String name)
   {
      Block result;
      if (block == null)
      {
         result = null;
      }
      else
      {
         result = getChildBlocks(block, name).findFirst().orElse(null);
      }
      return result;
   }

   /**
    * Retrieve a list of named child blocks.
    *
    * @param block parent block
    * @param name child block name
    * @return list of child blocks
    */
   private Stream<Block> getChildBlocks(Block block, String name)
   {
      Stream<Block> result;
      if (block == null)
      {
         result = null;
      }
      else
      {
         result = block.getChildBlocks().stream().filter(x -> name.equals(x.getName()));
      }
      return result;
   }

   /**
    * Read any relationships for a single task.
    *
    * @param task parent task
    * @param block relationship data
    */
   private void readRelationships(Task task, Block block)
   {
      byte[] data = block.getData();
      int successorTaskUniqueID = DatatypeConverter.getShort(data, 0);
      Task successor = m_projectFile.getTaskByUniqueID(Integer.valueOf(successorTaskUniqueID));

      if (successor == null || task.isSucessor(successor) || task.isPredecessor(successor) || data.length == 14)
      {
         return;
      }

      Duration lag = DatatypeConverter.getDuration(data, 6);
      RelationType type = DatatypeConverter.getRelationType(data, 2);

      successor.addPredecessor(task, type, lag);
   }

   /**
    * Updates the hierarchical structure to ensure that child tasks
    * are nested under the correct parent tasks.
    */
   private void updateStructure()
   {
      int maxUniqueID = m_projectFile.getChildTasks().stream().mapToInt(task -> NumberHelper.getInt(task.getUniqueID())).max().orElse(0);
      int uniqueID = (((maxUniqueID + 1000) / 1000) + 1) * 1000;

      for (Map.Entry<Integer, Integer> entry : m_childTaskCounts.entrySet())
      {
         Task task = m_projectFile.getTaskByID(entry.getKey());
         task.setUniqueID(Integer.valueOf(uniqueID++));
         int startID = task.getID().intValue() + 1;
         int offset = entry.getValue().intValue();

         for (int id = startID; id < startID + offset; id++)
         {
            Task childTask = m_projectFile.getTaskByID(Integer.valueOf(id));
            if (childTask != null)
            {
               childTask.setOutlineLevel(Integer.valueOf(NumberHelper.getInt(childTask.getOutlineLevel()) + 1));
               offset += NumberHelper.getInt(m_extraBarCounts.get(childTask));
            }
         }
      }
      m_projectFile.getTasks().updateStructure();
   }

   /**
    * Read data for an individual resource.
    *
    * @param block CResource block
    */
   private void readResource(Block block)
   {
      byte[] data = block.getData();
      Resource resource = m_projectFile.addResource();
      resource.setName(DatatypeConverter.getString(data, 0));

      Block resourceTask = getChildBlock(block, "CResourceTask");
      if (resourceTask != null)
      {
         resource.setUniqueID(Integer.valueOf(DatatypeConverter.getShort(resourceTask.getData(), 9)));
      }

      Block calendarBlock = getChildBlock(block, "CCalendar");
      if (calendarBlock != null)
      {
         ProjectCalendar calendar = readCalendar(calendarBlock);
         if (calendar != null)
         {
            calendar.setResource(resource);
         }
      }

      m_eventManager.fireResourceReadEvent(resource);
   }

   /**
    * Propagate start and end dates to summary tasks.
    */
   private void updateDates()
   {
      for (Task task : m_projectFile.getChildTasks())
      {
         updateDates(task);
      }
   }

   /**
    * Propagate start and end dates to summary tasks.
    *
    * @param parentTask parent task
    */
   private void updateDates(Task parentTask)
   {
      if (parentTask.hasChildTasks())
      {
         Date plannedStartDate = parentTask.getStart();
         Date plannedFinishDate = parentTask.getFinish();

         for (Task task : parentTask.getChildTasks())
         {
            updateDates(task);

            plannedStartDate = DateHelper.min(plannedStartDate, task.getStart());
            plannedFinishDate = DateHelper.max(plannedFinishDate, task.getFinish());
         }

         parentTask.setStart(plannedStartDate);
         parentTask.setFinish(plannedFinishDate);
      }
   }

   /**
    * Provide unique ID values for resources which don't have one.
    */
   private void updateResourceUniqueIDValues()
   {
      int maxUniqueID = m_projectFile.getResources().stream().mapToInt(task -> NumberHelper.getInt(task.getUniqueID())).max().getAsInt();
      int uniqueID = (((maxUniqueID + 1000) / 1000) + 1) * 1000;
      for (Resource resource : m_projectFile.getResources())
      {
         if (resource.getUniqueID() == null)
         {
            resource.setUniqueID(Integer.valueOf(uniqueID++));
         }
      }
   }

   private ProjectFile m_projectFile;
   private EventManager m_eventManager;
   private ProjectCommanderData m_data;
   private Map<Task, Block> m_taskMap;
   private Map<Integer, Integer> m_childTaskCounts;
   private Map<Task, Integer> m_extraBarCounts;

   private static final byte[] EMPTY_BYTE_ARRAY = new byte[0];

   private static final Day[] DAYS =
   {
      Day.SATURDAY,
      Day.SUNDAY,
      Day.MONDAY,
      Day.TUESDAY,
      Day.WEDNESDAY,
      Day.THURSDAY,
      Day.FRIDAY
   };
}
