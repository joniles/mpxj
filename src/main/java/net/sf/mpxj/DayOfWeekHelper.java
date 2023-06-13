package net.sf.mpxj;
import java.time.DayOfWeek;

import net.sf.mpxj.common.EnumHelper;

public final class DayOfWeekHelper
{
   /**
    * Retrieve a Day instance representing the supplied value.
    *
    * @param type type value
    * @return Day instance
    */
   public static Day getInstance(int type)
   {
      Day result;

      if (type < 0 || type >= TYPE_VALUES.length)
      {
         result = null;
      }
      else
      {
         result = TYPE_VALUES[type];
      }
      return result;
   }

   public static Day getInstance(DayOfWeek day)
   {
      return getInstance(day.getValue() == 7 ? 1 : day.getValue() + 1);
   }

   public static int getValue(Day day)
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
   private static final Day[] TYPE_VALUES =
      {
         null,
         Day.SUNDAY,
         Day.MONDAY,
         Day.TUESDAY,
         Day.WEDNESDAY,
         Day.THURSDAY,
         Day.FRIDAY,
         Day.SATURDAY
      };
}
