/*
 * file:       RecurrenceUtility.java
 * author:     Jon Iles
 * copyright:  (c) Packwood Software Limited 2008
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

import net.sf.mpxj.Duration;
import net.sf.mpxj.ProjectHeader;
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
    * @param header project header for duration units conversion 
    * @param durationValue integer duration value
    * @param unitsValue integer units value
    * @return Duration instance
    */
   public static Duration getDuration (ProjectHeader header, Integer durationValue, Integer unitsValue)
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
            result = result.convertUnits(units, header);
         }
      }
      return (result);
   }
   
   /**
    * Convert an MPXJ Duration instance into an integer duration in minutes
    * ready to be written to an MPX file.
    * 
    * @param header project header for duration units conversion
    * @param duration Duration instance
    * @return integer duration in minutes
    */
   public static Integer getDurationValue (ProjectHeader header, Duration duration)
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
            duration = duration.convertUnits(TimeUnit.MINUTES, header);
         }
         result = new Integer((int)duration.getDuration());
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
   public static Integer getDurationUnits (RecurringTask recurrence)
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
      for (int loop=0; loop < DURATION_UNITS.length; loop++)
      {
         UNITS_MAP.put(DURATION_UNITS[loop], new Integer(loop));
      }
   }
}
