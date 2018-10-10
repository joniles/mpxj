
package net.sf.mpxj.synchro;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import net.sf.mpxj.ChildTaskContainer;
import net.sf.mpxj.DateRange;
import net.sf.mpxj.Day;
import net.sf.mpxj.EventManager;
import net.sf.mpxj.MPXJException;
import net.sf.mpxj.ProjectCalendar;
import net.sf.mpxj.ProjectCalendarDateRanges;
import net.sf.mpxj.ProjectConfig;
import net.sf.mpxj.ProjectFile;
import net.sf.mpxj.Resource;
import net.sf.mpxj.Task;
import net.sf.mpxj.listener.ProjectListener;
import net.sf.mpxj.reader.AbstractProjectReader;

public final class SynchroReader extends AbstractProjectReader
{
   /**
    * {@inheritDoc}
    */
   @Override public void addProjectListener(ProjectListener listener)
   {
      if (m_projectListeners == null)
      {
         m_projectListeners = new LinkedList<ProjectListener>();
      }
      m_projectListeners.add(listener);
   }

   /**
    * {@inheritDoc}
    */
   @Override public ProjectFile read(InputStream inputStream) throws MPXJException
   {
      try
      {
         SynchroLogger.setLogFile("c:/temp/project1.txt");
         SynchroLogger.openLogFile();
         m_data = new SynchroData();
         m_data.process(inputStream);
         return read();
      }

      catch (Exception ex)
      {
         SynchroLogger.closeLogFile();
         throw new MPXJException(MPXJException.INVALID_FILE, ex);
      }
   }

   private ProjectFile read() throws Exception
   {
      m_project = new ProjectFile();
      m_eventManager = m_project.getEventManager();

      ProjectConfig config = m_project.getProjectConfig();
      m_project.getProjectProperties().setFileApplication("Synchro");
      m_project.getProjectProperties().setFileType("SP");

      m_eventManager.addProjectListeners(m_projectListeners);

      // processProject();
      processCalendars();
      processResources();
      processTasks();
      // processDependencies();
      // processAssignments();

      return m_project;
   }

   private void processCalendars() throws IOException
   {
      CalendarReader reader = new CalendarReader(m_data.getTableData("Calendars"));
      reader.read();

      for (MapRow row : reader.getRows())
      {
         processCalendar(row);
      }
   }

   private void processCalendar(MapRow row)
   {
      ProjectCalendar calendar = m_project.addCalendar();

      Map<UUID, List<DateRange>> dayTypeMap = processDayTypes(row.getRows("DAY_TYPES"));

      calendar.setName(row.getString("NAME"));

      processRanges(dayTypeMap.get(row.getUUID("SUNDAY_DAY_TYPE")), calendar.addCalendarHours(Day.SUNDAY));
      processRanges(dayTypeMap.get(row.getUUID("MONDAY_DAY_TYPE")), calendar.addCalendarHours(Day.MONDAY));
      processRanges(dayTypeMap.get(row.getUUID("TUESDAY_DAY_TYPE")), calendar.addCalendarHours(Day.TUESDAY));
      processRanges(dayTypeMap.get(row.getUUID("WEDNESDAY_DAY_TYPE")), calendar.addCalendarHours(Day.WEDNESDAY));
      processRanges(dayTypeMap.get(row.getUUID("THURSDAY_DAY_TYPE")), calendar.addCalendarHours(Day.THURSDAY));
      processRanges(dayTypeMap.get(row.getUUID("FRIDAY_DAY_TYPE")), calendar.addCalendarHours(Day.FRIDAY));
      processRanges(dayTypeMap.get(row.getUUID("SATURDAY_DAY_TYPE")), calendar.addCalendarHours(Day.SATURDAY));

      for (MapRow assignment : row.getRows("DAY_TYPE_ASSIGNMENTS"))
      {
         Date date = assignment.getDate("DATE");
         processRanges(dayTypeMap.get(assignment.getUUID("DAY_TYPE_UUID")), calendar.addCalendarException(date, date));
      }
   }

   private void processRanges(List<DateRange> ranges, ProjectCalendarDateRanges container)
   {
      if (ranges != null)
      {
         for (DateRange range : ranges)
         {
            container.addRange(range);
         }
      }
   }

   private Map<UUID, List<DateRange>> processDayTypes(List<MapRow> types)
   {
      Map<UUID, List<DateRange>> map = new HashMap<UUID, List<DateRange>>();
      for (MapRow row : types)
      {
         List<DateRange> ranges = new ArrayList<DateRange>();
         for (MapRow range : row.getRows("TIME_RANGES"))
         {
            ranges.add(new DateRange(range.getDate("START"), range.getDate("END")));
         }
         map.put(row.getUUID("UUID"), ranges);
      }

      return map;
   }

   private void processResources() throws IOException
   {
      CompanyReader reader = new CompanyReader(m_data.getTableData("Companies"));
      reader.read();
      for (MapRow companyRow : reader.getRows())
      {
         // TODO: need to sort by type as well as by name!
         for (MapRow resourceRow : sort(companyRow.getRows("RESOURCES"), "NAME"))
         {
            processResource(resourceRow);
         }
      }
   }

   private void processResource(MapRow row) throws IOException
   {
      Resource resource = m_project.addResource();
      //resource.setUniqueID(row.getInteger("UNIQUE_ID"));
      resource.setName(row.getString("NAME"));
      resource.setGUID(row.getUUID("UUID"));
      resource.setEmailAddress(row.getString("EMAIL"));
      resource.setHyperlink(row.getString("URL"));
      resource.setNotes(getNotes(row.getRows("COMMENTARY")));
      resource.setText(1, row.getString("DESCRIPTION"));
      resource.setText(2, row.getString("SUPPLY_REFERENCE"));
      resource.setActive(true);

      List<MapRow> resources = row.getRows("RESOURCES");
      if (resources != null)
      {
         for (MapRow childResource : sort(resources, "NAME"))
         {
            processResource(childResource);
         }
      }
   }

   private void processTasks() throws IOException
   {
      TaskReader reader = new TaskReader(m_data.getTableData("Tasks"));
      reader.read();
      for (MapRow row : reader.getRows())
      {
         processTask(m_project, row);
      }
   }

   private void processTask(ChildTaskContainer parent, MapRow row) throws IOException
   {
      Task task = parent.addTask();
      task.setName(row.getString("NAME"));
      task.setGUID(row.getUUID("UUID"));
      task.setText(1, row.getString("ID"));
      List<MapRow> tasks = row.getRows("TASKS");

      if (tasks != null)
      {
         for (MapRow childTask : tasks)
         {
            processTask(task, childTask);
         }
      }
   }

   private String getNotes(List<MapRow> rows)
   {
      String result = null;
      if (rows != null && !rows.isEmpty())
      {
         StringBuilder sb = new StringBuilder();
         for (MapRow row : rows)
         {
            sb.append(row.getString("TITLE"));
            sb.append('\n');
            sb.append(row.getString("TEXT"));
            sb.append("\n\n");
         }
         result = sb.toString();
      }
      return result;
   }

   private List<MapRow> sort(List<MapRow> rows, final String attribute)
   {
      Collections.sort(rows, new Comparator<MapRow>()
      {
         @Override public int compare(MapRow o1, MapRow o2)
         {
            String value1 = o1.getString(attribute);
            String value2 = o2.getString(attribute);
            return value1.compareTo(value2);
         }
      });
      return rows;
   }

   private SynchroData m_data;
   private ProjectFile m_project;
   private EventManager m_eventManager;
   private List<ProjectListener> m_projectListeners;
}
