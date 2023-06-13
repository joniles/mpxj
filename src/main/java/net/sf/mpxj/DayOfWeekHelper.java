package net.sf.mpxj;

import java.time.DayOfWeek;

public final class DayOfWeekHelper
{
   /**
    * Retrieve a Day instance representing the supplied value.
    *
    * @param type type value
    * @return Day instance
    */
   public static DayOfWeek getInstance(int type)
   {
      DayOfWeek result;
      --type;

      if (type < 0 || type >= ORDERED_DAYS.length)
      {
         result = null;
      }
      else
      {
         result = ORDERED_DAYS[type];
      }
      return result;
   }

   public static int getValue(DayOfWeek day)
   {
      switch(day)
      {
         case SUNDAY:
            return 1;
         case MONDAY:
            return 2;
         case TUESDAY:
            return 3;
         case WEDNESDAY:
            return 4;
         case THURSDAY:
            return 5;
         case FRIDAY:
            return 6;
         case SATURDAY:
            return 7;
      }

      return 0;
   }

   /**
    * Array mapping int types to enums.
    */
   public static final DayOfWeek[] ORDERED_DAYS =
      {
         DayOfWeek.SUNDAY,
         DayOfWeek.MONDAY,
         DayOfWeek.TUESDAY,
         DayOfWeek.WEDNESDAY,
         DayOfWeek.THURSDAY,
         DayOfWeek.FRIDAY,
         DayOfWeek.SATURDAY
      };
}
