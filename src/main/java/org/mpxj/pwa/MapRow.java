package org.mpxj.pwa;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;
import java.util.UUID;

import org.mpxj.AccrueType;
import org.mpxj.BookingType;
import org.mpxj.ConstraintType;
import org.mpxj.CurrencySymbolPosition;
import org.mpxj.DataType;
import org.mpxj.Duration;
import org.mpxj.Priority;
import org.mpxj.Rate;
import org.mpxj.ScheduleFrom;
import org.mpxj.TaskMode;
import org.mpxj.TimeUnit;
import org.mpxj.common.DayOfWeekHelper;
import org.mpxj.common.LocalDateTimeHelper;
import org.mpxj.common.NumberHelper;
import org.mpxj.mpp.TaskTypeHelper;

class MapRow
{
   public MapRow(Map<String, Object> map)
   {
      m_map = map;
   }

   public Object getObject(String key)
   {
      return m_map.get(key);
   }

   public String getString(String key)
   {
      return String.valueOf(getObject(key));
   }

   public UUID getUUID(String key)
   {
      return (UUID)getObject(key, DataType.GUID);
   }

   public LocalDate getLocalDate(String key)
   {
      LocalDateTime result = (LocalDateTime)getObject(key, DataType.DATE);
      return result == null ? null : result.toLocalDate();
   }

   public int getInt(String key)
   {
      return NumberHelper.getInt((Integer)getObject(key, DataType.INTEGER));
   }

   public Object getObject(String key, DataType type)
   {
      Object value = m_map.get(key);
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
            return Duration.getInstance(Double.parseDouble(String.valueOf(value)), TimeUnit.HOURS);
         }

         case WORK:
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
            return Priority.getInstance((Integer)value);
         }

         case CONSTRAINT:
         {
            return ConstraintType.getInstance(NumberHelper.getInt((Integer) value) - 1);
         }

         case INTEGER:
         case NUMERIC:
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

   private final Map<String, Object> m_map;

   private static final DateTimeFormatter DATE_TIME_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'[HH:mm:ss.SSS][HH:mm:ss.SS][HH:mm:ss.S][HH:mm:ss]");
}
