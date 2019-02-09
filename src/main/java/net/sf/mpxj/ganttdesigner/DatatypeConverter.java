

package net.sf.mpxj.ganttdesigner;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import net.sf.mpxj.Duration;
import net.sf.mpxj.TimeUnit;

public final class DatatypeConverter
{

   /**
    * Parse a timestamp value.
    *
    * @param value string representation
    * @return date value
    */
   public static final Date parseTimestamp(String value)
   {
      Date result = null;

      if (value != null && value.length() != 0)
      {
         try
         {
            result = TIMESTAMP_FORMAT.get().parse(value);
         }

         catch (ParseException ex)
         {
            // Ignore parse exception
         }
      }

      return (result);
   }

   /**
    * Print a timestamp value.
    *
    * @param value time value
    * @return time value
    */
   public static final String printTimestamp(Date value)
   {
      return (value == null ? null : TIMESTAMP_FORMAT.get().format(value));
   }

   public static final Duration parseDuration(String value)
   {
      return value == null ? null : Duration.getInstance(Double.parseDouble(value), TimeUnit.DAYS);
   }
   
   public static final String printDuration(Duration value)
   {
      return value == null ? null : Double.toString(value.getDuration());
   }
   
   public static final Date parseDate(String value)
   {
      Date result = null;

      if (value != null && value.length() != 0)
      {
         try
         {
            result = DATE_FORMAT.get().parse(value);
         }

         catch (ParseException ex)
         {
            // Ignore parse exception
         }
      }

      return (result);
   }

   public static final Double parsePercent(String value)
   {
      return value == null ? null : Double.valueOf(Double.parseDouble(value) * 100.0);
   }
   
   public static final String printPercent(Double value)
   {
      return value == null ? null : Double.toString(value.doubleValue() / 100.0);
   }

   /**
    * Print a timestamp value.
    *
    * @param value time value
    * @return time value
    */
   public static final String printDate(Date value)
   {
      return (value == null ? null : DATE_FORMAT.get().format(value));
   }

   private static final ThreadLocal<DateFormat> TIMESTAMP_FORMAT = new ThreadLocal<DateFormat>()
   {
      @Override protected DateFormat initialValue()
      {
         DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
         df.setLenient(false);
         return df;
      }
   };   
   
   private static final ThreadLocal<DateFormat> DATE_FORMAT = new ThreadLocal<DateFormat>()
   {
      @Override protected DateFormat initialValue()
      {
         DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
         df.setLenient(false);
         return df;
      }
   };            

}
