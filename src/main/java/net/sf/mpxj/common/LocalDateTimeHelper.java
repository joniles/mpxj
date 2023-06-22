package net.sf.mpxj.common;
import java.time.LocalDateTime;
import java.time.LocalTime;
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

   /**
    * Returns a new Date instance whose value
    * represents the start of the day (i.e. the time of day is 00:00:00.000)
    *
    * @param date date to convert
    * @return day start date
    */
   public static LocalDateTime getDayStartDate(LocalDateTime date)
   {
      if (date == null)
      {
         return null;
      }

      return LocalDateTime.of(date.toLocalDate(), LocalTime.of(0,0));
   }

   /**
    * This method compares a target date with a date range. The method will
    * return 0 if the date is within the range, less than zero if the date
    * is before the range starts, and greater than zero if the date is after
    * the range ends.
    *
    * @param startDate range start date
    * @param endDate range end date
    * @param targetDate target date in milliseconds
    * @return comparison result
    */
   public static int compare(LocalDateTime startDate, LocalDateTime endDate, LocalDateTime targetDate)
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
      return result;
   }

   /**
    * Compare two dates, handling null values.
    * TODO: correct the comparison order to align with Date.compareTo
    *
    * @param d1 Date instance
    * @param d2 Date instance
    * @return int comparison result
    */
   public static int compare(LocalDateTime d1, LocalDateTime d2)
   {
      if (d1 == null || d2 == null)
      {
         return d1 == d2 ? 0 : (d1 == null ? 1 : -1);
      }
      return d1.compareTo(d2);
   }

   /**
    * Returns the earlier of two dates, handling null values. A non-null Date
    * is always considered to be earlier than a null Date.
    *
    * @param d1 Date instance
    * @param d2 Date instance
    * @return Date earliest date
    */
   public static LocalDateTime min(LocalDateTime d1, LocalDateTime d2)
   {
      LocalDateTime result;
      if (d1 == null)
      {
         result = d2;
      }
      else
      {
         if (d2 == null)
         {
            result = d1;
         }
         else
         {
            result = (d1.compareTo(d2) < 0) ? d1 : d2;
         }
      }
      return result;
   }

   /**
    * Returns the later of two dates, handling null values. A non-null Date
    * is always considered to be later than a null Date.
    *
    * @param d1 Date instance
    * @param d2 Date instance
    * @return Date latest date
    */
   public static LocalDateTime max(LocalDateTime d1, LocalDateTime d2)
   {
      LocalDateTime result;
      if (d1 == null)
      {
         result = d2;
      }
      else
         if (d2 == null)
         {
            result = d1;
         }
         else
         {
            result = (d1.compareTo(d2) > 0) ? d1 : d2;
         }
      return result;
   }
}
