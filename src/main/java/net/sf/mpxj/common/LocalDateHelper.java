package net.sf.mpxj.common;
import java.time.LocalDate;
import java.util.Calendar;
import java.util.Date;

public final class LocalDateHelper
{
   public static LocalDate getLocalDate(Date date)
   {
      if (date == null)
      {
         return null;
      }

      Calendar cal = DateHelper.popCalendar(date);
      LocalDate result = LocalDate.of(cal.get(Calendar.YEAR), cal.get(Calendar.MONTH)+1, cal.get(Calendar.DAY_OF_MONTH));
      DateHelper.pushCalendar(cal);
      return result;
   }

   public static Date getDate(LocalDate date)
   {
      if (date == null)
      {
         return null;
      }

      Calendar cal = DateHelper.popCalendar();
      cal.set(date.getYear(), date.getMonthValue()-1, date.getDayOfMonth(), 0, 0, 0);
      Date result = cal.getTime();
      DateHelper.pushCalendar(cal);
      return result;
   }
}
