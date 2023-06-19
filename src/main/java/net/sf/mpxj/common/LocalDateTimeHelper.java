package net.sf.mpxj.common;
import java.time.LocalDateTime;
import java.util.Calendar;
import java.util.Date;

public final class LocalDateTimeHelper
{

   public static LocalDateTime getLocalDateTime(Date date)
   {
      if (date == null)
      {
         return null;
      }

      Calendar cal = DateHelper.popCalendar(date);
      LocalDateTime result = LocalDateTime.of(cal.get(Calendar.YEAR), cal.get(Calendar.MONTH)+1, cal.get(Calendar.DAY_OF_MONTH), cal.get(Calendar.HOUR_OF_DAY), cal.get(Calendar.MINUTE), cal.get(Calendar.SECOND));
      DateHelper.pushCalendar(cal);
      return result;
   }


   public static Date getDate(LocalDateTime date)
   {
      if (date == null)
      {
         return null;
      }

      Calendar cal = DateHelper.popCalendar();
      cal.set(Calendar.YEAR, date.getYear());
      cal.set(Calendar.MONTH, date.getMonthValue()-1);
      cal.set(Calendar.DAY_OF_MONTH, date.getDayOfMonth());
      cal.set(Calendar.HOUR_OF_DAY, date.getHour());
      cal.set(Calendar.MINUTE, date.getMinute());
      cal.set(Calendar.SECOND, date.getSecond());
      cal.set(Calendar.MILLISECOND, 0);
      Date result = cal.getTime();
      DateHelper.pushCalendar(cal);
      return result;
   }
}
