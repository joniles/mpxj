package net.sf.mpxj;

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

   public static DayOfWeek getInstance(java.time.DayOfWeek day)
   {
      return getInstance(day.getValue() == 7 ? 1 : day.getValue() + 1);
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
   private static final DayOfWeek[] TYPE_VALUES =
      {
         null,
         DayOfWeek.SUNDAY,
         DayOfWeek.MONDAY,
         DayOfWeek.TUESDAY,
         DayOfWeek.WEDNESDAY,
         DayOfWeek.THURSDAY,
         DayOfWeek.FRIDAY,
         DayOfWeek.SATURDAY
      };
}
