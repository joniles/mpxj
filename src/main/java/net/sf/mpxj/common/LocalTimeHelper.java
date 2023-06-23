package net.sf.mpxj.common;
import java.time.LocalDateTime;
import java.time.LocalTime;

public class LocalTimeHelper
{
   public static LocalTime getLocalTime(LocalDateTime date)
   {
      if (date == null)
      {
         return null;
      }

      return date.toLocalTime();
   }

   public static int compare(LocalTime d1, LocalTime d2)
   {
      int result;
      if (d1 == null || d2 == null)
      {
         result = (d1 == d2 ? 0 : (d1 == null ? 1 : -1));
      }
      else
      {
         result = d1.compareTo(d2);
      }
      return result;
   }

   public static LocalDateTime setTime(LocalDateTime date, LocalTime time)
   {
      if (time == null)
      {
         return date;
      }

      return LocalDateTime.of(date.toLocalDate(), time);
   }

   public static LocalDateTime setEndTime(LocalDateTime date, LocalTime time)
   {
      if (time == null)
      {
         return date;
      }

      date = LocalDateTime.of(date.toLocalDate(), time);
      if (time == LocalTime.MIDNIGHT)
      {
         date = date.plusDays(1);
      }

      return date;
   }

   public static long getMillisecondsInRange(LocalTime rangeStart, LocalTime rangeEnd)
   {
      if (rangeStart == null || rangeEnd == null)
      {
         return 0;
      }

      return rangeEnd == LocalTime.MIDNIGHT ? DateHelper.MS_PER_DAY - (rangeStart.toSecondOfDay() * 1000L) : (rangeEnd.toSecondOfDay() - rangeStart.toSecondOfDay()) * 1000L;
   }
}