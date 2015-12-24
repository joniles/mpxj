/*
 * file:       RecurrenceUtility.java
 * author:     Jon Iles
 * copyright:  (c) Packwood Software 2008
 * date:       13/06/2008
 */

/*
 * This library is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as published by the
 * Free Software Foundation; either version 2.1 of the License, or (at your
 * option) any later version.
 *
 * This library is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY
 * or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public
 * License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this library; if not, write to the Free Software Foundation, Inc.,
 * 59 Temple Place, Suite 330, Boston, MA 02111-1307, USA.
 */

package net.sf.mpxj.mpx;

import java.util.HashMap;
import java.util.Map;

import net.sf.mpxj.Day;
import net.sf.mpxj.Duration;
import net.sf.mpxj.ProjectProperties;
import net.sf.mpxj.RecurrenceType;
import net.sf.mpxj.RecurringTask;
import net.sf.mpxj.TimeUnit;

/**
 * This class contains method relating to managing Recurrence instances for MPX
 * files.
 */
final class RecurrenceUtility
{
   /**
    * Constructor.
    */
   private RecurrenceUtility()
   {
      // private constructor to prevent instantiation
   }

   /**
    * Convert the integer representation of a duration value and duration units
    * into an MPXJ Duration instance.
    *
    * @param properties project properties, used for duration units conversion
    * @param durationValue integer duration value
    * @param unitsValue integer units value
    * @return Duration instance
    */
   public static Duration getDuration(ProjectProperties properties, Integer durationValue, Integer unitsValue)
   {
      Duration result;
      if (durationValue == null)
      {
         result = null;
      }
      else
      {
         result = Duration.getInstance(durationValue.intValue(), TimeUnit.MINUTES);
         TimeUnit units = getDurationUnits(unitsValue);
         if (result.getUnits() != units)
         {
            result = result.convertUnits(units, properties);
         }
      }
      return (result);
   }

   /**
    * Convert an MPXJ Duration instance into an integer duration in minutes
    * ready to be written to an MPX file.
    *
    * @param properties project properties, used for duration units conversion
    * @param duration Duration instance
    * @return integer duration in minutes
    */
   public static Integer getDurationValue(ProjectProperties properties, Duration duration)
   {
      Integer result;
      if (duration == null)
      {
         result = null;
      }
      else
      {
         if (duration.getUnits() != TimeUnit.MINUTES)
         {
            duration = duration.convertUnits(TimeUnit.MINUTES, properties);
         }
         result = Integer.valueOf((int) duration.getDuration());
      }
      return (result);
   }

   /**
    * Converts a TimeUnit instance to an integer value suitable for
    * writing to an MPX file.
    *
    * @param recurrence RecurringTask instance
    * @return integer value
    */
   public static Integer getDurationUnits(RecurringTask recurrence)
   {
      Duration duration = recurrence.getDuration();
      Integer result = null;

      if (duration != null)
      {
         result = UNITS_MAP.get(duration.getUnits());
      }

      return (result);
   }

   /**
    * Maps a duration unit value from a recurring task record in an MPX file
    * to a TimeUnit instance. Defaults to days if any problems are encountered.
    *
    * @param value integer duration units value
    * @return TimeUnit instance
    */
   private static TimeUnit getDurationUnits(Integer value)
   {
      TimeUnit result = null;

      if (value != null)
      {
         int index = value.intValue();
         if (index >= 0 && index < DURATION_UNITS.length)
         {
            result = DURATION_UNITS[index];
         }
      }

      if (result == null)
      {
         result = TimeUnit.DAYS;
      }

      return (result);
   }

   /**
    * Converts the MPX file integer representation of a recurrence type
    * into a RecurrenceType instance.
    *
    * @param value MPX file integer recurrence type
    * @return RecurrenceType instance
    */
   public static RecurrenceType getRecurrenceType(Integer value)
   {
      return (RECURRENCE_TYPE_MAP.get(value));
   }

   /**
    * Converts a RecurrenceType instance into the integer representation
    * used in an MPX file.
    *
    * @param value RecurrenceType instance
    * @return integer representation
    */
   public static Integer getRecurrenceValue(RecurrenceType value)
   {
      return (RECURRENCE_VALUE_MAP.get(value));
   }

   /**
    * Converts the string representation of the days bit field into an integer.
    *
    * @param days string bit field
    * @return integer bit field
    */
   public static Integer getDays(String days)
   {
      Integer result = null;
      if (days != null)
      {
         result = Integer.valueOf(Integer.parseInt(days, 2));
      }
      return (result);
   }

   /**
    * Converts an integer bit field into a string representation.
    *
    * @param days integer bit field
    * @return string representation
    */
   public static String getDays(Integer days)
   {
      String result = null;
      if (days != null)
      {
         StringBuilder sb = new StringBuilder("0000000");
         sb.append(Integer.toBinaryString(days.intValue()));
         result = sb.toString().substring(sb.length() - 7);
      }
      return (result);
   }

   /**
    * Convert MPX day index to Day instance.
    *
    * @param day day index
    * @return Day instance
    */
   public static Day getDay(Integer day)
   {
      Day result = null;
      if (day != null)
      {
         result = DAY_ARRAY[day.intValue()];
      }
      return (result);
   }

   /**
    * Convert Day instance to MPX day index.
    *
    * @param day Day instance
    * @return day index
    */
   public static Integer getDay(Day day)
   {
      Integer result = null;
      if (day != null)
      {
         result = DAY_MAP.get(day);
      }
      return (result);
   }

   /**
    * Array to map from the integer representation of a
    * duration's units in the recurring task record to
    * a TimeUnit instance.
    */
   private static final TimeUnit[] DURATION_UNITS =
   {
      TimeUnit.DAYS,
      TimeUnit.WEEKS,
      TimeUnit.HOURS,
      TimeUnit.MINUTES
   };

   /**
    * Map to allow conversion of a TimeUnit instance back to an integer.
    */
   private static final Map<TimeUnit, Integer> UNITS_MAP = new HashMap<TimeUnit, Integer>();
   static
   {
      for (int loop = 0; loop < DURATION_UNITS.length; loop++)
      {
         UNITS_MAP.put(DURATION_UNITS[loop], Integer.valueOf(loop));
      }
   }

   /**
    * Map of integer values to RecurrenceType instances.
    */
   private static final Map<Integer, RecurrenceType> RECURRENCE_TYPE_MAP = new HashMap<Integer, RecurrenceType>();
   static
   {
      RECURRENCE_TYPE_MAP.put(Integer.valueOf(1), RecurrenceType.DAILY);
      RECURRENCE_TYPE_MAP.put(Integer.valueOf(4), RecurrenceType.WEEKLY);
      RECURRENCE_TYPE_MAP.put(Integer.valueOf(8), RecurrenceType.MONTHLY);
      RECURRENCE_TYPE_MAP.put(Integer.valueOf(16), RecurrenceType.YEARLY);
   }

   /**
    * Map of  RecurrenceType instances to integer values.
    */
   private static final Map<RecurrenceType, Integer> RECURRENCE_VALUE_MAP = new HashMap<RecurrenceType, Integer>();
   static
   {
      RECURRENCE_VALUE_MAP.put(RecurrenceType.DAILY, Integer.valueOf(1));
      RECURRENCE_VALUE_MAP.put(RecurrenceType.WEEKLY, Integer.valueOf(4));
      RECURRENCE_VALUE_MAP.put(RecurrenceType.MONTHLY, Integer.valueOf(8));
      RECURRENCE_VALUE_MAP.put(RecurrenceType.YEARLY, Integer.valueOf(16));
   }

   /**
    * Array mapping from MPX day index to Day instances.
    */
   private static final Day[] DAY_ARRAY =
   {
      null,
      Day.MONDAY,
      Day.TUESDAY,
      Day.WEDNESDAY,
      Day.THURSDAY,
      Day.FRIDAY,
      Day.SATURDAY,
      Day.SUNDAY
   };

   /**
    * Map from Day instance to MPX day index.
    */
   private static final Map<Day, Integer> DAY_MAP = new HashMap<Day, Integer>();
   static
   {
      DAY_MAP.put(Day.MONDAY, Integer.valueOf(1));
      DAY_MAP.put(Day.TUESDAY, Integer.valueOf(2));
      DAY_MAP.put(Day.WEDNESDAY, Integer.valueOf(3));
      DAY_MAP.put(Day.THURSDAY, Integer.valueOf(4));
      DAY_MAP.put(Day.FRIDAY, Integer.valueOf(5));
      DAY_MAP.put(Day.SATURDAY, Integer.valueOf(6));
      DAY_MAP.put(Day.SUNDAY, Integer.valueOf(7));
   }
}
