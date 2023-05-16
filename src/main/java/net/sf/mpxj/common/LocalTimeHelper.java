package net.sf.mpxj.common;
import java.time.LocalTime;
import java.util.Calendar;
import java.util.Date;

public class LocalTimeHelper
{
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

   public static int compare(LocalTime startDate, LocalTime endDate, LocalTime targetDate)
   {
      if (targetDate.isBefore(startDate))
      {
         return -1;
      }

      if (targetDate.isAfter(endDate))
      {
         return 1;
      }
      return 0;
   }
}