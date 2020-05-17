
package net.sf.mpxj.projectcommander;

import java.io.IOException;
import java.io.InputStream;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import net.sf.mpxj.DateRange;
import net.sf.mpxj.Day;
import net.sf.mpxj.Duration;
import net.sf.mpxj.EventManager;
import net.sf.mpxj.MPXJException;
import net.sf.mpxj.ProjectCalendar;
import net.sf.mpxj.ProjectCalendarHours;
import net.sf.mpxj.ProjectFile;
import net.sf.mpxj.RelationType;
import net.sf.mpxj.Resource;
import net.sf.mpxj.Task;
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
         m_eventManager = m_projectFile.getEventManager();

         m_data = new ProjectCommanderData();
         m_data.setLogFile("c:/temp/project-commander.log");
         m_data.process(is);

         readCalendars();
         readTasks();
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
         for (Day day : DAYS)
         {
            if (calendar.isWorkingDay(day))
            {
               readCalendarHours(data, offset, calendar.addCalendarHours(day));
            }
            offset += 16;
         }

         System.out.println("Calendar: " + calendar.getName());
         block.getChildBlocks().stream().filter(x -> "CDayFlag".equals(x.getName())).forEach(x -> readCalendarException(x.getData()));
         m_eventManager.fireCalendarReadEvent(calendar);
      }

      return calendar;
   }

   private void readCalendarHours(byte[] data, int offset, ProjectCalendarHours hours)
   {
      addRange(hours, DatatypeConverter.getInt(data, offset), DatatypeConverter.getInt(data, offset + 4));
      addRange(hours, DatatypeConverter.getInt(data, offset + 8), DatatypeConverter.getInt(data, offset + 12));
   }

   private void addRange(ProjectCalendarHours hours, int startMinutes, int endMinutes)
   {
      if (startMinutes != endMinutes)
      {
         Date start = DateHelper.getTimeFromMinutesPastMidnight(Integer.valueOf(startMinutes));
         Date end = DateHelper.getTimeFromMinutesPastMidnight(Integer.valueOf(endMinutes));
         hours.addRange(new DateRange(start, end));
      }
   }

   private void readCalendarException(byte[] data)
   {
      int flag = DatatypeConverter.getShort(data, 0);
      long timestampInDays = DatatypeConverter.getShort(data, 2, 0);
      long timestampInMilliseconds = timestampInDays * 24 * 60 * 60 * 1000;
      System.out.println("*** " + flag + "\t" + DateHelper.getTimestampFromLong(timestampInMilliseconds) + "\t" +ByteArrayHelper.hexdump(data, false));
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
      task.setName(name);
      m_eventManager.fireTaskReadEvent(task);

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

      // NOTE: summary tasks don't appear to have their own Unique ID values
      // Sumary task don't have CBar, CBaselineData, CUsageTask
      // The task Unique ID appears to be present in CBar and CBaselineData
      // Duration at CUsageTask 366????

      // Start Date      
      //      long timestampInSeconds = DatatypeConverter.getInt(cBarData, 5, 0);
      //      long timestampInMilliseconds = timestampInSeconds * 1000; 
      //      System.out.println(task.getID() + "\t" + DateHelper.getTimestampFromLong(timestampInMilliseconds));

      // This is the task unique ID as used by CLink
      //System.out.println(task.getID() + "\t" + DatatypeConverter.getShort(cBarData, 23, 0));

      // Other apparently unique identifiers      
      //      System.out.println(task.getID() + "\t" + DatatypeConverter.getShort(cBaselineData, 69, 0));
      //System.out.println(task.getID() + "\t" + DatatypeConverter.getShort(cUsageTaskData, 408, 0));
      //System.out.println(task.getID() + "\t" + DatatypeConverter.getShort(cUsageTaskBaselineData, 69, 0));

      // Duration
      // System.out.println(task.getID() + "\t" + DatatypeConverter.getDuration(cBaselineData, 366));

      block.getChildBlocks().stream().filter(x -> "CLink".equals(x.getName())).forEach(x -> dumpLink(task, x));
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

   private void dumpLink(Task task, Block block)
   {
      // 14 bytes or less, predecessor
      // more than 14 bytes successor

      byte[] data = block.getData();
      if (data.length > 14)
      {
         int taskID = DatatypeConverter.getShort(data, 0);
         Duration lag = DatatypeConverter.getDuration(data, 6);
         RelationType type = DatatypeConverter.getRelationType(data, 2);
         System.out.println(task.getID() + "\t" + taskID + "\t" + type + "\t" + lag + "\t" + ByteArrayHelper.hexdump(data, 0, data.length, false));
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
