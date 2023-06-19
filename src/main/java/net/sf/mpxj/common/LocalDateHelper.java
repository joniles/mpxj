package net.sf.mpxj.common;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Calendar;
import java.util.Date;

public final class LocalDateHelper
{
   public static LocalDateTime getLocalDateTime(LocalDate date)
   {
      if (date == null)
      {
         return null;
      }

      return date.atStartOfDay();
   }

   public static LocalDate getLocalDate(LocalDateTime date)
   {
      if (date == null)
      {
         return null;
      }

      return date.toLocalDate();
   }

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
      cal.set(Calendar.YEAR, date.getYear());
      cal.set(Calendar.MONTH, date.getMonthValue()-1);
      cal.set(Calendar.DAY_OF_MONTH, date.getDayOfMonth());
      cal.set(Calendar.HOUR_OF_DAY, 0);
      cal.set(Calendar.MINUTE, 0);
      cal.set(Calendar.SECOND, 0);
      cal.set(Calendar.MILLISECOND, 0);
      //cal.set(date.getYear(), date.getMonthValue()-1, date.getDayOfMonth(), 0, 0, 0);
      Date result = cal.getTime();
      DateHelper.pushCalendar(cal);
      return result;
   }

   public static int compare(LocalDate d1, LocalDate d2)
   {
      if (d1 == null || d2 == null)
      {
         return d1 == d2 ? 0 : (d1 == null ? 1 : -1);
      }
      return d1.compareTo(d2);
   }

   public static int compare(LocalDate startDate, LocalDate endDate, LocalDate targetDate)
   {
      int result = 0;
      if (targetDate.isBefore(startDate))
      {
         result = -1;
      }
      else
      {
         if (targetDate.isAfter(endDate))
         {
            result = 1;
         }
      }
      return (result);
   }
}
