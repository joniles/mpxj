package org.mpxj.pwa;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import org.mpxj.AccrueType;
import org.mpxj.BookingType;
import org.mpxj.ConstraintType;
import org.mpxj.CurrencySymbolPosition;
import org.mpxj.DataType;
import org.mpxj.Duration;
import org.mpxj.Notes;
import org.mpxj.Priority;
import org.mpxj.ProjectFile;
import org.mpxj.Rate;
import org.mpxj.ScheduleFrom;
import org.mpxj.TaskMode;
import org.mpxj.TimeUnit;
import org.mpxj.common.BooleanHelper;
import org.mpxj.common.DayOfWeekHelper;
import org.mpxj.common.LocalDateTimeHelper;
import org.mpxj.common.NumberHelper;
import org.mpxj.mpp.TaskTypeHelper;
import org.mpxj.mpp.WorkContourHelper;

class MapRow extends LinkedHashMap<String, Object>
{
   public void setProject(ProjectFile project)
   {
      m_project = project;
   }

   public List<MapRow> getList(String key)
   {
      @SuppressWarnings("unchecked") List<MapRow> row = (List<MapRow>)get(key);
      return row == null ? Collections.emptyList() : row;
   }

   public MapRow getMapRow(String key)
   {
      return (MapRow) get(key);
   }

   public String getString(String key)
   {
      return String.valueOf(get(key));
   }

   public UUID getUUID(String key)
   {
      return (UUID) getObject(key, DataType.GUID);
   }

   public LocalDate getLocalDate(String key)
   {
      LocalDateTime result = (LocalDateTime) getObject(key, DataType.DATE);
      return result == null ? null : result.toLocalDate();
   }

   public Integer getInteger(String key)
   {
      return (Integer) getObject(key, DataType.INTEGER);
   }

   public int getInt(String key)
   {
      return NumberHelper.getInt(getInteger(key));
   }

   public boolean getBool(String key)
   {
      return BooleanHelper.getBoolean((Boolean)get(key));
   }

   public Object getObject(String key, DataType type)
   {
      Object value = get(key);
      if (value == null)
      {
         return null;
      }

      switch (type)
      {
         case STRING:
         {
            return String.valueOf(value);
         }

         case DATE:
         {
            return getDateFromString(String.valueOf(value));
         }

         case GUID:
         {
            return getUuidFromString(String.valueOf(value));
         }

         case DURATION:
         {
            if (key.startsWith("LocalCustom"))
            {
               return parseDuration(String.valueOf(value));
            }

            double time = Double.parseDouble(String.valueOf(value));
            if (key.endsWith("Milliseconds"))
            {
               time = time / (1000.0 * 60.0 * 60.0);
            }

            return Duration.getInstance(time, TimeUnit.HOURS);
         }

         case WORK:
         case DELAY:
         {
            double time = Double.parseDouble(String.valueOf(value));
            if (key.endsWith("Milliseconds"))
            {
               time = time / (1000.0 * 60.0 * 60.0);
            }

            return Duration.getInstance(time, TimeUnit.HOURS);
         }

         case CURRENCY:
         {
            return Double.parseDouble(String.valueOf(value));
         }

         case SCHEDULE_FROM:
         {
            return ((Boolean) value).booleanValue() ? ScheduleFrom.START : ScheduleFrom.FINISH;
         }

         case ACCRUE:
         {
            return AccrueType.getInstance((Integer) value);
         }

         case DAY:
         {
            return DayOfWeekHelper.getInstance(NumberHelper.getInt((Integer) value) + 1);
         }

         case TIME:
         {
            return LocalTime.parse(String.valueOf(value), DATE_TIME_FORMAT);
         }

         case TASK_TYPE:
         {
            return TaskTypeHelper.getInstance((Integer) value);
         }

         case CURRENCY_SYMBOL_POSITION:
         {
            return CurrencySymbolPosition.getInstance((Integer) value);
         }

         case RATE:
         {
            return new Rate((Number) value, TimeUnit.HOURS);
         }

         case UNITS:
         {
            return Double.valueOf(((Number) value).doubleValue() * 100);
         }

         case BOOKING_TYPE:
         {
            return BookingType.getInstance(NumberHelper.getInt((Integer) value));
         }

         case RATE_UNITS:
         {
            return TimeUnit.getInstance(NumberHelper.getInt((Integer) value) - 1);
         }

         case TASK_MODE:
         {
            return ((Boolean) value).booleanValue() ? TaskMode.MANUALLY_SCHEDULED : TaskMode.AUTO_SCHEDULED;
         }

         case PRIORITY:
         {
            return Priority.getInstance((Integer) value);
         }

         case CONSTRAINT:
         {
            return ConstraintType.getInstance(NumberHelper.getInt((Integer) value) - 1);
         }

         case NUMERIC:
         {
            if (value instanceof String)
            {
               return Double.valueOf((String)value);
            }
            return value;
         }

         case WORK_CONTOUR:
         {
            return WorkContourHelper.getInstance(m_project, ((Integer)value).intValue());
         }

         case NOTES:
         {
            return new Notes(String.valueOf(value));
         }

         case INTEGER:
         case PERCENTAGE:
         case BOOLEAN:
         case SHORT:
         {
            return value;
         }

         default:
         {
            throw new PwaException(type + " not handled");
         }
      }
   }

   private LocalDateTime getDateFromString(String value)
   {
      if (value.equals("0001-01-01T00:00:00"))
      {
         return null;
      }

      return LocalDateTimeHelper.parseBest(DATE_TIME_FORMAT, value);
   }

   private UUID getUuidFromString(String value)
   {
      if (value.equals("00000000-0000-0000-0000-000000000000"))
      {
         return null;
      }

      return UUID.fromString(value);
   }

   private Object parseDuration(String value)
   {
      Matcher match = DURATION_REGEX.matcher(value);
      if (!match.matches())
      {
         return value;
      }

      double duration = Double.parseDouble(match.group(1));
      TimeUnit unit = TIME_UNIT_MAP.get(match.group(2));
      if (unit == null)
      {
         return value;
      }

      return Duration.getInstance(duration, unit);
   }

   private ProjectFile m_project;

   private static final DateTimeFormatter DATE_TIME_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'[HH:mm:ss.SSS][HH:mm:ss.SS][HH:mm:ss.S][HH:mm:ss]");
   private static final Pattern DURATION_REGEX = Pattern.compile("(-?\\d+\\.\\d+|-?\\d+)(emo|mo|em|eh|ed|ew|ey|e%|m|h|d|w|%|y)");
   private static final Map<String, TimeUnit> TIME_UNIT_MAP = Arrays.stream(TimeUnit.values()).collect(Collectors.toMap(TimeUnit::getName, t -> t));
}
