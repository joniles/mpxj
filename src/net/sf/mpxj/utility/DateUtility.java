/*
 * file:       DateUtility.java
 * author:     Jon Iles
 * copyright:  (c) Tapster Rock Limited 2005
 * date:       Jan 18, 2006
 */
 
package net.sf.mpxj.utility;

import java.util.Calendar;
import java.util.Date;

/**
 * Utility methods for manipulating dates.
 */
public final class DateUtility
{
   /**
    * Constructor.
    */
   private DateUtility ()
   {
      // private constructor to prevent instantiation
   }
   
   /**
    * Returns a new Date instance whose value
    * represents the start of the day (i.e. the time of day is 00:00:00.000)
    *
    * @param date date to convert
    * @return day start date
    */
   public static Date getDayStartDate (Date date)
   {
      if (date != null)
      {
         Calendar cal = Calendar.getInstance();
         cal.setTime(date);
         cal.set(Calendar.HOUR_OF_DAY, 0);
         cal.set(Calendar.MINUTE, 0);
         cal.set(Calendar.SECOND, 0);
         cal.set(Calendar.MILLISECOND, 0);
         date = cal.getTime();
      }
      return (date);
   }

   /**
    * Returns a new Date instance whose value
    * represents the end of the day (i.e. the time of days is 11:59:59.999)
    *
    * @param date date to convert
    * @return day start date
    */
   public static Date getDayEndDate (Date date)
   {
      if (date != null)
      {
         Calendar cal = Calendar.getInstance();
         cal.setTime(date);
         cal.set(Calendar.MILLISECOND, 999);
         cal.set(Calendar.SECOND, 59);
         cal.set(Calendar.MINUTE, 59);
         cal.set(Calendar.HOUR_OF_DAY, 23);
         date = cal.getTime();
      }
      return (date);
   }   
}
