package net.sf.mpxj.common;
import java.time.LocalTime;
import java.util.Calendar;
import java.util.Date;

public class LocalTimeHelper
{
   public static LocalTime getLocalTime(Date date)
   {
      if (date == null)
      {
         return null;
      }

      //return date.toInstant().atZone(ZoneId.systemDefault()).toLocalTime();

      Calendar cal = DateHelper.popCalendar(date);
      LocalTime result = LocalTime.of(cal.get(Calendar.HOUR_OF_DAY), cal.get(Calendar.MINUTE), cal.get(Calendar.SECOND));
      DateHelper.pushCalendar(cal);
      return result;
   }

   public static Date getDate(LocalTime date)
   {
      if (date == null)
      {
         return null;
      }

      Calendar cal = DateHelper.popCalendar();
      cal.set(Calendar.DAY_OF_YEAR, 1);
      cal.set(Calendar.YEAR, 1);
      cal.set(Calendar.HOUR_OF_DAY, date.getHour());
      cal.set(Calendar.MINUTE, date.getMinute());
      cal.set(Calendar.SECOND, date.getSecond());
      cal.set(Calendar.MILLISECOND, 0);
      Date result = cal.getTime();
      DateHelper.pushCalendar(cal);

      return result;
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

   public static final Date RANGE_START_MIDNIGHT;
   static
   {
      Calendar cal = DateHelper.popCalendar();
      cal.set(Calendar.DAY_OF_YEAR, 1);
      cal.set(Calendar.YEAR, 1);
      cal.set(Calendar.HOUR_OF_DAY, 0);
      cal.set(Calendar.MINUTE, 0);
      cal.set(Calendar.SECOND, 0);
      cal.set(Calendar.MILLISECOND, 0);
      RANGE_START_MIDNIGHT = cal.getTime();
      DateHelper.pushCalendar(cal);
   }

   public static final Date RANGE_END_MIDNIGHT;
   static
   {
      Calendar cal = DateHelper.popCalendar();
      cal.set(Calendar.DAY_OF_YEAR, 2);
      cal.set(Calendar.YEAR, 1);
      cal.set(Calendar.HOUR_OF_DAY, 0);
      cal.set(Calendar.MINUTE, 0);
      cal.set(Calendar.SECOND, 0);
      cal.set(Calendar.MILLISECOND, 0);
      RANGE_END_MIDNIGHT = cal.getTime();
      DateHelper.pushCalendar(cal);
   }
}