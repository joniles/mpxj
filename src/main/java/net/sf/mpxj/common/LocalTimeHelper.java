package net.sf.mpxj.common;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Calendar;
import java.util.Date;

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

   public static LocalTime getLocalTime(Date date)
   {
      if (date == null)
      {
         return null;
      }

      //return date.toInstant().atZone(ZoneId.systemDefault()).toLocalTime();

      Calendar cal = DateHelper.popCalendar(date);
      LocalTime result = getLocalTime(cal);
      DateHelper.pushCalendar(cal);
      return result;
   }

   public static LocalTime getLocalTime(Calendar cal)
   {
      return LocalTime.of(cal.get(Calendar.HOUR_OF_DAY), cal.get(Calendar.MINUTE), cal.get(Calendar.SECOND));
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

   public static int compare(LocalTime start, LocalTime end, LocalTime target)
   {
      if (target.isBefore(start))
      {
         return -1;
      }

      if (end.equals(LocalTime.MIDNIGHT))
      {
         if (start.equals(LocalTime.MIDNIGHT))
         {
            return 0;
         }

         return end.equals(target) ? 1 : 0;
      }

      if (target.isAfter(end))
      {
         return 1;
      }

      return 0;
   }

   public static Date setTime(Date date, LocalTime time)
   {
      if (time == null)
      {
         return date;
      }

      Calendar cal = DateHelper.popCalendar(date);
      setTime(cal, time);
      Date result = cal.getTime();
      DateHelper.pushCalendar(cal);
      return result;
   }

   public static Date setEndTime(Date date, LocalTime time)
   {
      if (time == null)
      {
         return date;
      }

      Calendar cal = DateHelper.popCalendar(date);
      setEndTime(cal, time);
      Date result = cal.getTime();
      DateHelper.pushCalendar(cal);
      return result;
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

   public static void setTime(Calendar cal, LocalTime time)
   {
      if (time != null)
      {
         cal.set(Calendar.HOUR_OF_DAY, time.getHour());
         cal.set(Calendar.MINUTE, time.getMinute());
         cal.set(Calendar.SECOND, time.getSecond());
      }
   }

   public static void setEndTime(Calendar cal, LocalTime time)
   {
      if (time != null)
      {
         setTime(cal, time);
         if (time == LocalTime.MIDNIGHT)
         {
            cal.add(Calendar.DAY_OF_YEAR, 1);
         }
      }
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