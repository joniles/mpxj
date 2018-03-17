
package net.sf.mpxj.primavera.suretrak;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import net.sf.mpxj.DateRange;
import net.sf.mpxj.Day;
import net.sf.mpxj.EventManager;
import net.sf.mpxj.FieldType;
import net.sf.mpxj.MPXJException;
import net.sf.mpxj.ProjectCalendar;
import net.sf.mpxj.ProjectCalendarException;
import net.sf.mpxj.ProjectCalendarHours;
import net.sf.mpxj.ProjectConfig;
import net.sf.mpxj.ProjectFile;
import net.sf.mpxj.RecurrenceType;
import net.sf.mpxj.RecurringData;
import net.sf.mpxj.TaskField;
import net.sf.mpxj.listener.ProjectListener;
import net.sf.mpxj.primavera.p3.MapRow;
import net.sf.mpxj.primavera.p3.Table;
import net.sf.mpxj.reader.ProjectReader;

public final class SureTrakDatabaseReader implements ProjectReader
{
   /**
    * Convenience method which locates the first P3 database in a directory
    * and opens it.
    *
    * @param directory directory containing a P3 database
    * @return ProjectFile instance
    */
   public static final ProjectFile setPrefixAndRead(File directory) throws MPXJException
   {
      File[] files = directory.listFiles(new FilenameFilter()
      {
         @Override public boolean accept(File dir, String name)
         {
            return name.toUpperCase().endsWith(".DIR");
         }
      });

      if (files != null && files.length != 0)
      {
         String fileName = files[0].getName();
         String prefix = fileName.substring(0, fileName.length() - 4).toUpperCase();
         SureTrakDatabaseReader reader = new SureTrakDatabaseReader();
         reader.setPrefix(prefix);
         return reader.read(directory);
      }

      return null;
   }

   @Override public void addProjectListener(ProjectListener listener)
   {
      if (m_projectListeners == null)
      {
         m_projectListeners = new LinkedList<ProjectListener>();
      }
      m_projectListeners.add(listener);
   }

   @Override public ProjectFile read(String fileName) throws MPXJException
   {
      return read(new File(fileName));
   }

   @Override public ProjectFile read(InputStream inputStream)
   {
      throw new UnsupportedOperationException();
   }

   /**
    * Set the prefix used to identify which database is read from the directory.
    * There may potentially be more than one database in a directory.
    *
    * @param prefix file name prefix
    */
   public void setPrefix(String prefix)
   {
      m_prefix = prefix;
   }

   @Override public ProjectFile read(File directory) throws MPXJException
   {
      if (!directory.isDirectory())
      {
         throw new MPXJException("Directory expected");
      }

      try
      {
         m_projectFile = new ProjectFile();
         m_eventManager = m_projectFile.getEventManager();

         ProjectConfig config = m_projectFile.getProjectConfig();
         config.setAutoResourceID(true);
         config.setAutoResourceUniqueID(true);
         config.setAutoTaskID(true);
         config.setAutoTaskUniqueID(true);
         config.setAutoOutlineLevel(true);
         config.setAutoOutlineNumber(true);
         config.setAutoWBS(false);

         // Activity ID
         m_projectFile.getCustomFields().getCustomField(TaskField.TEXT1).setAlias("Code");

         m_projectFile.getProjectProperties().setFileApplication("SureTrak");
         m_projectFile.getProjectProperties().setFileType("STW");

         m_eventManager.addProjectListeners(m_projectListeners);

         m_tables = new DatabaseReader().process(directory, m_prefix);
         m_calendarMap = new HashMap<Integer, ProjectCalendar>();

         readProjectHeader();
         readCalendars();
         readHolidays();
         readResources();
         readTasks();
         readRelationships();
         readResourceAssignments();

         return m_projectFile;
      }

      catch (IOException ex)
      {
         throw new MPXJException("Failed to parse file", ex);
      }

      finally
      {
         m_projectFile = null;
         m_eventManager = null;
         m_projectListeners = null;
         m_tables = null;
         m_calendarMap = null;
      }
   }

   /**
    * Read general project properties.
    */
   private void readProjectHeader()
   {
   }

   /**
    * Read project calendars.
    */
   private void readCalendars()
   {
      Table cal = m_tables.get("CAL");
      for (MapRow row : cal)
      {
         ProjectCalendar calendar = m_projectFile.addCalendar();
         m_calendarMap.put(row.getInteger("CALENDAR_ID"), calendar);

         calendar.setName(row.getString("NAME"));
         readHours(calendar, Day.SUNDAY, row.getInteger("SUNDAY_HOURS"));
         readHours(calendar, Day.MONDAY, row.getInteger("MONDAY_HOURS"));
         readHours(calendar, Day.TUESDAY, row.getInteger("TUESDAY_HOURS"));
         readHours(calendar, Day.WEDNESDAY, row.getInteger("WEDNESDAY_HOURS"));
         readHours(calendar, Day.THURSDAY, row.getInteger("THURSDAY_HOURS"));
         readHours(calendar, Day.FRIDAY, row.getInteger("FRIDAY_HOURS"));
         readHours(calendar, Day.SATURDAY, row.getInteger("SATURDAY_HOURS"));
      }
   }

   private void readHours(ProjectCalendar calendar, Day day, Integer hours)
   {
      int value = hours.intValue();
      int startHour = 0;
      ProjectCalendarHours calendarHours = null;

      Calendar cal = Calendar.getInstance();
      cal.set(Calendar.HOUR_OF_DAY, 0);
      cal.set(Calendar.MINUTE, 0);
      cal.set(Calendar.SECOND, 0);
      cal.set(Calendar.MILLISECOND, 0);

      while (value != 0)
      {
         // Move forward until we find a working hour
         while (startHour < 24 && (value & 0x1) == 0)
         {
            value = value >> 1;
            ++startHour;
         }

         // No more working hours, bail out
         if (startHour >= 24)
         {
            break;
         }

         // Move forward until we find the end of the working hours
         int endHour = startHour;
         while (endHour < 24 && (value & 0x1) != 0)
         {
            value = value >> 1;
            ++endHour;
         }

         cal.set(Calendar.HOUR_OF_DAY, startHour);
         Date startDate = cal.getTime();
         cal.set(Calendar.HOUR_OF_DAY, endHour);
         Date endDate = cal.getTime();

         if (calendarHours == null)
         {
            calendarHours = calendar.addCalendarHours(day);
         }
         calendarHours.addRange(new DateRange(startDate, endDate));
         startHour = endHour;
      }
   }

   private void readHolidays()
   {
      for (MapRow row : m_tables.get("HOL"))
      {
         ProjectCalendar calendar = m_calendarMap.get(row.getInteger("CALENDAR_ID"));
         if (calendar != null)
         {
            Date date = row.getDate("DATE");
            ProjectCalendarException exception = calendar.addCalendarException(date, date);
            if (row.getBoolean("ANNUAL"))
            {
               RecurringData recurring = new RecurringData();
               recurring.setRecurrenceType(RecurrenceType.YEARLY);
               recurring.setYearlyAbsoluteFromDate(date);
               recurring.setStartDate(date);
               exception.setRecurring(recurring);
               // TODO set end date based on project end date
            }
         }
      }
   }

   /**
    * Read resources.
    */
   private void readResources()
   {
   }

   /**
    * Read tasks.
    */
   private void readTasks()
   {
   }

   /**
    * Read task relationships.
    */
   private void readRelationships()
   {
   }

   /**
    * Read resource assignments.
    */
   private void readResourceAssignments()
   {
   }

   /**
    * Set the value of one or more fields based on the contents of a database row.
    *
    * @param map column to field map
    * @param row database row
    * @param container field container
    */
   //   private void setFields(Map<String, FieldType> map, MapRow row, FieldContainer container)
   //   {
   //      if (row != null)
   //      {
   //         for (Map.Entry<String, FieldType> entry : map.entrySet())
   //         {
   //            container.set(entry.getValue(), row.getObject(entry.getKey()));
   //         }
   //      }
   //   }

   /**
    * Configure the mapping between a database column and a field.
    *
    * @param container column to field map
    * @param name column name
    * @param type field type
    */
   //   private static void defineField(Map<String, FieldType> container, String name, FieldType type)
   //   {
   //      defineField(container, name, type, null);
   //   }

   /**
    * Configure the mapping between a database column and a field, including definition of
    * an alias.
    *
    * @param container column to field map
    * @param name column name
    * @param type field type
    * @param alias field alias
    */
   //   private static void defineField(Map<String, FieldType> container, String name, FieldType type, String alias)
   //   {
   //      container.put(name, type);
   //      //      if (alias != null)
   //      //      {
   //      //         ALIASES.put(type, alias);
   //      //      }
   //   }

   private String m_prefix;
   private ProjectFile m_projectFile;
   private EventManager m_eventManager;
   private List<ProjectListener> m_projectListeners;
   private Map<String, Table> m_tables;
   private Map<Integer, ProjectCalendar> m_calendarMap;

   private static final Map<String, FieldType> PROJECT_FIELDS = new HashMap<String, FieldType>();
   private static final Map<String, FieldType> RESOURCE_FIELDS = new HashMap<String, FieldType>();
   private static final Map<String, FieldType> TASK_FIELDS = new HashMap<String, FieldType>();

   static
   {
      //      defineField(PROJECT_FIELDS, "PROJECT_START_DATE", ProjectField.START_DATE);
      //      defineField(PROJECT_FIELDS, "PROJECT_FINISH_DATE", ProjectField.FINISH_DATE);
      //      defineField(PROJECT_FIELDS, "CURRENT_DATA_DATE", ProjectField.STATUS_DATE);
      //      defineField(PROJECT_FIELDS, "COMPANY_TITLE", ProjectField.COMPANY);
      //      defineField(PROJECT_FIELDS, "PROJECT_TITLE", ProjectField.NAME);
      //
      //      defineField(RESOURCE_FIELDS, "RES_TITLE", ResourceField.NAME);
      //      defineField(RESOURCE_FIELDS, "RES_ID", ResourceField.CODE);
      //
      //      defineField(TASK_FIELDS, "ACTIVITY_TITLE", TaskField.NAME);
      //      defineField(TASK_FIELDS, "ACTIVITY_ID", TaskField.TEXT1);
      //      defineField(TASK_FIELDS, "ORIGINAL_DURATION", TaskField.DURATION);
      //      defineField(TASK_FIELDS, "REMAINING_DURATION", TaskField.REMAINING_DURATION);
      //      defineField(TASK_FIELDS, "PERCENT_COMPLETE", TaskField.PERCENT_COMPLETE);
      //      defineField(TASK_FIELDS, "EARLY_START", TaskField.EARLY_START);
      //      defineField(TASK_FIELDS, "LATE_START", TaskField.LATE_START);
      //      defineField(TASK_FIELDS, "EARLY_FINISH", TaskField.EARLY_FINISH);
      //      defineField(TASK_FIELDS, "LATE_FINISH", TaskField.LATE_FINISH);
      //      defineField(TASK_FIELDS, "FREE_FLOAT", TaskField.FREE_SLACK);
      //      defineField(TASK_FIELDS, "TOTAL_FLOAT", TaskField.TOTAL_SLACK);
   }

}
