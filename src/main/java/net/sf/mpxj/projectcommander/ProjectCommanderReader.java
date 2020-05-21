
package net.sf.mpxj.projectcommander;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;

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
import net.sf.mpxj.Task;
import net.sf.mpxj.TimeUnit;
import net.sf.mpxj.common.ByteArrayHelper;
import net.sf.mpxj.common.DateHelper;
import net.sf.mpxj.common.NumberHelper;
import net.sf.mpxj.listener.ProjectListener;
import net.sf.mpxj.reader.AbstractProjectReader;

public final class ProjectCommanderReader extends AbstractProjectReader
{
   /**
    * {@inheritDoc}
    */
   @Override public void addProjectListener(ProjectListener listener)
   {
      if (m_projectListeners == null)
      {
         m_projectListeners = new LinkedList<>();
      }
      m_projectListeners.add(listener);
   }

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

         m_eventManager = m_projectFile.getEventManager();
         m_taskMap = new TreeMap<>();

         m_data = new ProjectCommanderData();
         m_data.setLogFile("c:/temp/project-commander.log");
         m_data.process(is);

         readCalendars();
         readTasks();
         readRelationships();
         readResources();

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
         m_data = null;
      }
   }

   private void readCalendars()
   {
      m_data.getBlocks().stream().filter(block -> "CCalendar".equals(block.getName())).forEach(block -> readCalendar(block));
   }

   private void readTasks()
   {
      Map<Integer, Integer> childTaskCounts = new TreeMap<>();
      m_data.getBlocks().stream().filter(block -> "CTask".equals(block.getName())).forEach(block -> readTask(block, childTaskCounts));
      createHierarchy(childTaskCounts);
   }

   private void readResources()
   {
      m_data.getBlocks().stream().filter(block -> "CResource".equals(block.getName())).forEach(block -> readResource(block));
   }

   private void readRelationships()
   {
      for (Entry<Task, Block> entry : m_taskMap.entrySet())
      {
         entry.getValue().getChildBlocks().stream().filter(x -> "CLink".equals(x.getName())).forEach(x -> readRelationships(entry.getKey(), x));
      }
   }

   private ProjectCalendar readCalendar(Block block)
   {
      ProjectCalendar calendar;

      byte[] data = block.getData();
      String name = DatatypeConverter.getString(data, 0, null);

      if (name == null)
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

         System.out.println("Calendar: " + calendar.getName());
         block.getChildBlocks().stream().filter(x -> "CDayFlag".equals(x.getName())).forEach(x -> readCalendarException(calendar, ranges, x.getData()));

         m_eventManager.fireCalendarReadEvent(calendar);
      }

      return calendar;
   }

   private List<DateRange> readCalendarHours(byte[] data, int offset)
   {
      List<DateRange> ranges = new ArrayList<>();
      addRange(ranges, DatatypeConverter.getInt(data, offset), DatatypeConverter.getInt(data, offset + 4));
      addRange(ranges, DatatypeConverter.getInt(data, offset + 8), DatatypeConverter.getInt(data, offset + 12));
      return ranges;
   }

   private void addRange(List<DateRange> ranges, int startMinutes, int endMinutes)
   {
      if (startMinutes != endMinutes)
      {
         Date start = DateHelper.getTimeFromMinutesPastMidnight(Integer.valueOf(startMinutes));
         Date end = DateHelper.getTimeFromMinutesPastMidnight(Integer.valueOf(endMinutes));
         ranges.add(new DateRange(start, end));
      }
   }

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

   private void readTask(Block block, Map<Integer, Integer> childTaskCounts)
   {
      int offset = 0;
      byte[] cTaskData = block.getData();
      String name = DatatypeConverter.getString(cTaskData, offset, null);
      if (name == null)
      {
         return;
      }

      Block cUsageTask = getChildBlock(block, "CUsageTask");
      byte[] cBaselineData = getByteArray(block, "CBaselineData");
      byte[] cBarData = getByteArray(block, "CBar");
      byte[] cUsageTaskData = getByteArray(block, "CUsageTask");
      byte[] cUsageTaskBaselineData = getByteArray(cUsageTask, "CBaselineData");

      Task task = m_projectFile.addTask();
      m_taskMap.put(task, block);
      task.setName(name);

      //    System.out.println(task.getID() + "\t" + ByteArrayHelper.hexdump(cTaskData, false));
      //    System.out.println(task.getID() + "\t" + ByteArrayHelper.hexdump(cBaselineData, false));
      //    System.out.println(task.getID() + "\t" + ByteArrayHelper.hexdump(cBarData, false));
      //    System.out.println(task.getID() + "\t" + ByteArrayHelper.hexdump(cUsageTaskData, false));
      //    System.out.println(task.getID() + "\t" + ByteArrayHelper.hexdump(cUsageTaskBaselineData, false));

      offset += ((task.getName() == null ? 0 : task.getName().length()) + 1);

      // Skip the task name when dumping data
      //System.out.println(task.getID() + "\t" + ByteArrayHelper.hexdump(cTaskData, offset, cTaskData.length - offset, false));
      //System.out.println(task.getID() + "\t" + ByteArrayHelper.hexdump(cBarData, 0, cBarData.length, false) + " length=" + cBarData.length);
      //System.out.println(task.getID() + "\t" + ByteArrayHelper.hexdump(cBaselineData, 0, cBaselineData.length, false) + " length=" + cBaselineData.length);
      //System.out.println(task.getID() + "\t" + ByteArrayHelper.hexdump(cUsageTaskData, 0, cUsageTaskData.length, false) + " length=" + cUsageTaskData.length);            
      //System.out.println(task.getID() + "\t" + ByteArrayHelper.hexdump(cUsageTaskBaselineData, 0, cUsageTaskBaselineData.length, false) + " length=" + cUsageTaskBaselineData.length);

      int childTaskCount = DatatypeConverter.getShort(cTaskData, offset + 405, 0);
      if (childTaskCount != 0)
      {
         childTaskCounts.put(task.getID(), Integer.valueOf(DatatypeConverter.getShort(cTaskData, offset + 405, 0)));
      }

      // Summary task don't have a Unique ID, CBar, CBaselineData or CUsageTask
      if (cBarData != null && cBaselineData != null)
      {
         // This is the unique ID as used by CLink
         int uniqueID = DatatypeConverter.getShort(cBarData, 23, 0);
         task.setUniqueID(Integer.valueOf(uniqueID));

         // Unique ID values from other blocks                        
         // System.out.println(task.getID() + "\t" + DatatypeConverter.getShort(cBaselineData, 69, 0));
         // System.out.println(task.getID() + "\t" + DatatypeConverter.getShort(cUsageTaskData, 408, 0));
         // System.out.println(task.getID() + "\t" + DatatypeConverter.getShort(cUsageTaskBaselineData, 69, 0));

         Duration duration = DatatypeConverter.getDuration(cUsageTaskBaselineData, 433);
         task.setDuration(duration.convertUnits(TimeUnit.DAYS, m_projectFile.getProjectProperties()));

         ProjectCalendar calendar = m_projectFile.getDefaultCalendar();
         Date startDate = DatatypeConverter.getTimestamp(cBarData, 5);
         task.setStart(DateHelper.setTime(startDate, calendar.getStartTime(startDate)));
         task.setFinish(calendar.getDate(task.getStart(), task.getDuration(), false));

         //block.getChildBlocks().stream().filter(x -> "CLink".equals(x.getName())).forEach(x -> readRelationships(task, x));
      }

      // TODO: looks like we can have multiple bars per task, and links can be created to distinct bars on the same line.
      // also looks like the MPX export breaks these out into separate tasks, which we should follow
      // does that mean there is a duration in cBarData?

      // Is the data organised around baselines, e.g.
      // CTask
      //  CBaselineData
      //  CBaselineData
      long barCount = block.getChildBlocks().stream().filter(x -> "CBar".equals(x.getName())).count();
      if (barCount > 1)
      {
         System.out.println(barCount + "\t" + task);
         block.getChildBlocks().stream().forEach(x -> System.out.println("\t\t" + x.getName()));
      }

      m_eventManager.fireTaskReadEvent(task);
   }

   private byte[] getByteArray(Block block, String name)
   {
      Block childBlock = getChildBlock(block, name);
      return childBlock == null ? EMPTY_BYTE_ARRAY : childBlock.getData();
   }

   private Block getChildBlock(Block block, String name)
   {
      Block result;
      if (block == null)
      {
         result = null;
      }
      else
      {
         result = block.getChildBlocks().stream().filter(x -> name.equals(x.getName())).findFirst().orElse(null);
      }
      return result;
   }

   private void readRelationships(Task task, Block block)
   {
      byte[] data = block.getData();
      int successorTaskUniqueID = DatatypeConverter.getShort(data, 0);
      Task successor = m_projectFile.getTaskByUniqueID(Integer.valueOf(successorTaskUniqueID));
      Duration lag = DatatypeConverter.getDuration(data, 6);
      RelationType type = DatatypeConverter.getRelationType(data, 2);
      //         System.out.println(task.getID() + "\t" + task.getUniqueID() + "\t" + successorTaskUniqueID + "\t" + type + "\t" + lag + "\t" + ByteArrayHelper.hexdump(data, 0, data.length, false));

      if (successor == null)
      {
         System.out.println("Missing target task " + successorTaskUniqueID);
      }
      else
      {
         System.out.println(task.getID() + ":" + task.getName() + "\t" + successor.getID() + ":" + successor.getName() + "\t" + type + "\t" + lag);
         successor.addPredecessor(task, type, lag);
      }
   }

   private void createHierarchy(Map<Integer, Integer> childTaskCounts)
   {
      for (Map.Entry<Integer, Integer> entry : childTaskCounts.entrySet())
      {
         Task task = m_projectFile.getTaskByID(entry.getKey());
         int startID = task.getID().intValue() + 1;
         int endID = startID + entry.getValue().intValue();

         for (int id = startID; id < endID; id++)
         {
            Task childTask = m_projectFile.getTaskByID(Integer.valueOf(id));
            if (childTask != null)
            {
               childTask.setOutlineLevel(Integer.valueOf(NumberHelper.getInt(childTask.getOutlineLevel()) + 1));
            }
            else
            {
               System.out.println("skip " + id);
            }
         }
      }
      m_projectFile.getTasks().updateStructure();
   }

   private void readResource(Block block)
   {
      byte[] data = block.getData();
      Resource resource = m_projectFile.addResource();
      resource.setName(DatatypeConverter.getString(data, 0));

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

   private ProjectFile m_projectFile;
   private EventManager m_eventManager;
   private List<ProjectListener> m_projectListeners;
   private ProjectCommanderData m_data;
   private Map<Task, Block> m_taskMap;

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
