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
