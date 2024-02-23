package net.sf.mpxj.openplan;

import java.io.FileNotFoundException;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.sf.mpxj.DayType;
import net.sf.mpxj.LocalTimeRange;
import net.sf.mpxj.ProjectCalendarException;
import net.sf.mpxj.ProjectCalendarHours;
import net.sf.mpxj.ProjectFile;
import net.sf.mpxj.Resource;
import net.sf.mpxj.ProjectCalendar;
import net.sf.mpxj.common.HierarchyHelper;
import org.apache.poi.poifs.filesystem.DirectoryEntry;

class CalendarReader
{
   public CalendarReader(DirectoryEntry root, ProjectFile file)
   {
      m_root = root;
      m_file = file;
   }

   public void read(String name)
   {
      /*
         CLD - Calendar Directory Record (just one row?)
         CLR - Calendar Detail
         CLH - Calendar Header
         ACL
       */

      DirectoryEntry dir = getDirectoryEntry(m_root, name);

      //
      // Read headers
      //
      Map<String, ProjectCalendar> map = new HashMap<>();
      List<Row> rows = new TableReader(dir, "CLH").read();
      HierarchyHelper.sortHierarchy(rows, r -> r.getString("CLH_ID"), r -> getParentID(r.getString("CLH_ID")), Comparator.comparing(o -> o.getString("CLH_ID")));

      for (Row row : rows)
      {
         ProjectCalendar calendar = m_file.addCalendar();
         ProjectCalendar parentCalendar = map.get(getParentID(row.getString("CLH_ID")));
         if (parentCalendar != null)
         {
            calendar.setParent(parentCalendar);
         }

         String calendarName = row.getString("DESCRIPTION");
         if (calendarName == null || calendarName.isEmpty())
         {
            calendarName = row.getString("CLH_ID");

         }
         // CLH_ID: Calendar Header ID
         // CLH_UID: Calendar Header Unique ID
         calendar.setGUID(row.getUuid("CLH_UID"));
         // DESCRIPTION: Description
         calendar.setName(calendarName);
         // DIR_ID: Project Object Directory Name
         // DIR_UID: Project Object Directory UID
         // INTEGRATION_ID: External Unique ID
         // LASTUPDATE: Last Update Date
         // SEQUENCE: Update Count
         // SUPPRESS
         // USR_ID: Last Update User

         // Default all days to non-working
         Arrays.stream(DayOfWeek.values()).forEach(d -> calendar.setCalendarDayType(d, DayType.NON_WORKING));

         map.put(row.getString("CLH_ID"), calendar);
      }

      //
      // Read detail records
      //
      rows = new TableReader(dir, "CLR").read();
      for (Row row : rows)
      {
         ProjectCalendar calendar = map.get(row.getString("CLH_ID"));
         if (calendar == null)
         {
            continue;
         }

         if (isDayOfWeek(row))
         {
            readDayOfWeek(calendar, row);
            continue;
         }

         if (isDate(row))
         {
            readExceptionDate(calendar, row);
         }

         // CLH_ID: Calendar Header ID
         // CLH_UID: Calendar Header Unique ID
         // CLR_UID: Calendar Detail Unique ID
         // DATESPEC: Day Name, Month and Day, or Date
         // DIR_ID: Project Object Directory Name
         // DIR_UID: Project Object Directory UID
         // OPFINISH: Finish Time
         // OPSTART: Start Time
         // OPWORK: Working Flag
         // SEQUENCE: Update Count
      }
   }

   private void readDayOfWeek(ProjectCalendar calendar, Row row)
   {
      if (!row.getBoolean("OPWORK").booleanValue())
      {
         return;
      }

      DayOfWeek day = DAY_OF_WEEK_MAP.get(row.getString("DATESPEC"));
      calendar.setCalendarDayType(day, DayType.WORKING);
      ProjectCalendarHours hours = calendar.getHours(day);
      if (hours == null)
      {
         hours = calendar.addCalendarHours(day);
      }
      hours.add(new LocalTimeRange(row.getTime("OPSTART"), row.getTime("OPFINISH")));
   }

   private void readExceptionDate(ProjectCalendar calendar, Row row)
   {
      LocalDate date = LocalDate.parse(row.getString("DATESPEC"), DATE_FORMAT);

      // TODO: handle multiple rows when adding hours
      ProjectCalendarException exception = calendar.addCalendarException(date);

      if (!row.getBoolean("OPWORK").booleanValue())
      {
         return;
      }

      exception.add(new LocalTimeRange(row.getTime("OPSTART"), row.getTime("OPFINISH")));
   }

   // TODO: helper class
   private DirectoryEntry getDirectoryEntry(DirectoryEntry root, String name)
   {
      try
      {
         return (DirectoryEntry) root.getEntry(name);
      }

      catch (FileNotFoundException e)
      {
         throw new OpenPlanException(e);
      }
   }

   private boolean isDayOfWeek(Row row)
   {
      return DAY_OF_WEEK_MAP.containsKey(row.getString("DATESPEC"));
   }

   private boolean isDate(Row row)
   {
      String dateSpec = row.getString("DATESPEC");
      return dateSpec.length() == 8 && dateSpec.chars().allMatch(c -> Character.isDigit(c));
   }

   private String getParentID(String id)
   {
      int index = id.lastIndexOf('.');
      if (index == -1)
      {
         return null;
      }
      return id.substring(0,index);
   }

   private final ProjectFile m_file;
   private final DirectoryEntry m_root;

   private static final DateTimeFormatter DATE_FORMAT = new DateTimeFormatterBuilder().parseLenient().appendPattern("yyyyMMdd").toFormatter();

   private static final Map<String, DayOfWeek> DAY_OF_WEEK_MAP = new HashMap<>();
   static
   {
      DAY_OF_WEEK_MAP.put("monday", DayOfWeek.MONDAY);
      DAY_OF_WEEK_MAP.put("tuesday", DayOfWeek.TUESDAY);
      DAY_OF_WEEK_MAP.put("wednesday", DayOfWeek.WEDNESDAY);
      DAY_OF_WEEK_MAP.put("thursday", DayOfWeek.THURSDAY);
      DAY_OF_WEEK_MAP.put("friday", DayOfWeek.FRIDAY);
      DAY_OF_WEEK_MAP.put("saturday", DayOfWeek.SATURDAY);
      DAY_OF_WEEK_MAP.put("sunday", DayOfWeek.SUNDAY);
   }
}
