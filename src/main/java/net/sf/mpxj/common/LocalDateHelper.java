package net.sf.mpxj.common;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

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

   /**
    * Returns a new Date instance whose value
    * represents the end of the day (i.e. the time of days is 11:59:59.999)
    *
    * @param date date to convert
    * @return day start date
    */
   public static LocalDateTime getDayEndDate(LocalDate date)
   {
      if (date == null)
      {
         return null;
      }
      return LocalDateTime.of(date, LocalTime.of(23, 59, 59));
   }

   public static final LocalDate TWO_DIGIT_YEAR_BASE_DATE = LocalDate.now().minusYears(80);
}
