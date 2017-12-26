
package net.sf.mpxj.ganttproject;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * This class contains methods used to perform the datatype conversions
 * required to read GanttProject files.
 */
public final class DatatypeConverter
{

   /**
    * Parse a date value.
    *
    * @param value string representation
    * @return date value
    */
   public static final Date parseDate(String value)
   {
      Date result = null;

      if (value != null && value.length() != 0)
      {
         try
         {
            result = getDateFormat().parse(value);
         }

         catch (ParseException ex)
         {
            // Ignore parse exception
         }
      }

      return (result);
   }

   /**
    * Print a date value.
    *
    * @param value time value
    * @return time value
    */
   public static final String printDate(Date value)
   {
      return (value == null ? null : getDateFormat().format(value));
   }

   /**
    * Retrieve a date formatter.
    *
    * @return DateFormat instance
    */
   private static final DateFormat getDateFormat()
   {
      DateFormat df = DATE_FORMAT.get();
      if (df == null)
      {
         df = new SimpleDateFormat("yyyy-MM-dd");
         df.setLenient(false);
      }
      return (df);
   }

   private static final ThreadLocal<DateFormat> DATE_FORMAT = new ThreadLocal<DateFormat>();
}
