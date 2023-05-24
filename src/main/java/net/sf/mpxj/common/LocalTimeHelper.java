package net.sf.mpxj.common;
import java.time.LocalTime;
import java.time.ZoneId;
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

      return date.toInstant().atZone(ZoneId.systemDefault()).toLocalTime();
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
         return end.equals(target) ? 1 : 0;
      }

      if (target.isAfter(end))
      {
         return 1;
      }

      return 0;
   }
}