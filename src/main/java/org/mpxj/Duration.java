/*
 * file:       Duration.java
 * author:     Scott Melville
 *             Jon Iles
 * copyright:  (c) Packwood Software 2002-2003
 * date:       15/08/2002
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

package org.mpxj;

import org.mpxj.common.NumberHelper;

/**
 * This represents time durations as specified in an MPX file.
 */
public final class Duration implements Comparable<Duration>
{
   /**
    * Constructs an instance of this class from a duration amount and
    * time unit type.
    *
    * @param duration amount of duration
    * @param type time unit of duration
    */
   private Duration(double duration, TimeUnit type)
   {
      m_duration = duration;
      m_units = type;
   }

   /**
    * Constructs an instance of this class from a duration amount and
    * time unit type.
    *
    * @param duration amount of duration
    * @param type time unit of duration
    */
   private Duration(int duration, TimeUnit type)
   {
      m_duration = duration;
      m_units = type;
   }

   /**
    * This method is used to retrieve the size of the duration.
    *
    * @return size of the duration
    */
   public double getDuration()
   {
      return (m_duration);
   }

   /**
    * This method is used to retrieve the type of units the duration
    * is expressed in. The valid types of units are found in the TimeUnit
    * class.
    *
    * @return type of units
    */
   public TimeUnit getUnits()
   {
      return (m_units);
   }

   /**
    * This method provides an <i>approximate</i> conversion between duration
    * units. It does take into account the project defaults for number of hours
    * in a day and a week, but it does not take account of calendar details.
    * The results obtained from it should therefore be treated with caution.
    *
    * @param type target duration type
    * @param defaults project properties containing default values
    * @return new Duration instance
    */
   public Duration convertUnits(TimeUnit type, TimeUnitDefaultsContainer defaults)
   {
      if (type == m_units)
      {
         return this;
      }

      return convertUnits(m_duration, m_units, type, defaults);
   }

   /**
    * This method provides an <i>approximate</i> conversion between duration
    * units. It does take into account the project defaults for number of hours
    * in a day and a week, but it does not take account of calendar details.
    * The results obtained from it should therefore be treated with caution.
    *
    * @param duration duration value
    * @param fromUnits units to convert from
    * @param toUnits units to convert to
    * @param defaults project properties containing default values
    * @return new Duration instance
    */
   public static Duration convertUnits(double duration, TimeUnit fromUnits, TimeUnit toUnits, TimeUnitDefaultsContainer defaults)
   {
      return convertUnits(duration, fromUnits, toUnits, defaults.getMinutesPerDay().doubleValue(), defaults.getMinutesPerWeek().doubleValue(), defaults.getDaysPerMonth().doubleValue());
   }

   /**
    * This method provides an <i>approximate</i> conversion between duration
    * units. It does take into account the project defaults for number of hours
    * in a day and a week, but it does not take account of calendar details.
    * The results obtained from it should therefore be treated with caution.
    *
    * @param duration duration value
    * @param fromUnits units to convert from
    * @param toUnits units to convert to
    * @param minutesPerDay number of minutes per day
    * @param minutesPerWeek number of minutes per week
    * @param daysPerMonth number of days per month
    * @return new Duration instance
    */
   public static Duration convertUnits(double duration, TimeUnit fromUnits, TimeUnit toUnits, double minutesPerDay, double minutesPerWeek, double daysPerMonth)
   {
      if (fromUnits == toUnits)
      {
         return Duration.getInstance(duration, fromUnits);
      }

      switch (fromUnits)
      {
         case YEARS:
         {
            duration *= (minutesPerWeek * 52);
            break;
         }

         case ELAPSED_YEARS:
         {
            duration *= (60 * 24 * 7 * 52);
            break;
         }

         case MONTHS:
         {
            duration *= (minutesPerDay * daysPerMonth);
            break;
         }

         case ELAPSED_MONTHS:
         {
            duration *= (60 * 24 * 30);
            break;
         }

         case WEEKS:
         {
            duration *= minutesPerWeek;
            break;
         }

         case ELAPSED_WEEKS:
         {
            duration *= (60 * 24 * 7);
            break;
         }

         case DAYS:
         {
            duration *= minutesPerDay;
            break;
         }

         case ELAPSED_DAYS:
         {
            duration *= (60 * 24);
            break;
         }

         case HOURS:
         case ELAPSED_HOURS:
         {
            duration *= 60;
            break;
         }

         default:
         {
            break;
         }
      }

      if (toUnits != TimeUnit.MINUTES && toUnits != TimeUnit.ELAPSED_MINUTES)
      {
         switch (toUnits)
         {
            case HOURS:
            case ELAPSED_HOURS:
            {
               duration /= 60;
               break;
            }

            case DAYS:
            {
               if (minutesPerDay != 0)
               {
                  duration /= minutesPerDay;
               }
               else
               {
                  duration = 0;
               }
               break;
            }

            case ELAPSED_DAYS:
            {
               duration /= (60 * 24);
               break;
            }

            case WEEKS:
            {
               if (minutesPerWeek != 0)
               {
                  duration /= minutesPerWeek;
               }
               else
               {
                  duration = 0;
               }
               break;
            }

            case ELAPSED_WEEKS:
            {
               duration /= (60 * 24 * 7);
               break;
            }

            case MONTHS:
            {
               if (minutesPerDay != 0 && daysPerMonth != 0)
               {
                  duration /= (minutesPerDay * daysPerMonth);
               }
               else
               {
                  duration = 0;
               }
               break;
            }

            case ELAPSED_MONTHS:
            {
               duration /= (60 * 24 * 30);
               break;
            }

            case YEARS:
            {
               if (minutesPerWeek != 0)
               {
                  duration /= (minutesPerWeek * 52);
               }
               else
               {
                  duration = 0;
               }
               break;
            }

            case ELAPSED_YEARS:
            {
               duration /= (60 * 24 * 7 * 52);
               break;
            }

            default:
            {
               break;
            }
         }
      }

      return (Duration.getInstance(duration, toUnits));
   }

   /**
    * Retrieve a Duration instance. Use shared objects to
    * represent common values for memory efficiency.
    *
    * @param duration duration value
    * @param type duration type
    * @return Duration instance
    */
   public static Duration getInstance(double duration, TimeUnit type)
   {
      Duration result;
      if (duration == 0)
      {
         result = ZERO_DURATIONS[type.getValue()];
      }
      else
      {
         result = new Duration(duration, type);
      }
      return (result);
   }

   /**
    * Retrieve a Duration instance. Use shared objects to
    * represent common values for memory efficiency.
    *
    * @param duration duration value
    * @param type duration type
    * @return Duration instance
    */
   public static Duration getInstance(int duration, TimeUnit type)
   {
      Duration result;
      if (duration == 0)
      {
         result = ZERO_DURATIONS[type.getValue()];
      }
      else
      {
         result = new Duration(duration, type);
      }
      return (result);
   }

   @Override public boolean equals(Object o)
   {
      boolean result = false;
      if (o instanceof Duration)
      {
         Duration rhs = (Duration) o;
         result = durationComponentEquals(rhs) && m_units == rhs.m_units;
      }
      return result;
   }

   @Override public int hashCode()
   {
      return (m_units.getValue() + (int) m_duration);
   }

   @Override public int compareTo(Duration rhs)
   {
      if (m_units != rhs.m_units)
      {
         rhs = convertUnits(rhs.m_duration, rhs.m_units, m_units, (8 * 60), (5 * 8 * 60), 20);
      }

      return durationComponentEquals(rhs) ? 0 : m_duration < rhs.m_duration ? -1 : 1;
   }

   /**
    * Equality test for duration component of a Duration instance.
    * Note that this does not take into account the units - use with care!
    *
    * @param rhs duration to compare
    * @return true if duration components are equal, within the allowable delta
    */
   public boolean durationComponentEquals(Duration rhs)
   {
      return durationValueEquals(m_duration, rhs.m_duration);
   }

   /**
    * Equality test for two duration values.
    *
    * @param lhs duration value
    * @param rhs duration value
    * @return true if duration values are equal, within the allowable delta
    */
   public static boolean durationValueEquals(double lhs, double rhs)
   {
      return NumberHelper.equals(lhs, rhs, 0.00001);
   }

   /**
    * If a and b are not null, returns a new duration of a + b.
    * If a is null and b is not null, returns b.
    * If a is not null and b is null, returns a.
    * If a and b are null, returns null.
    * If needed, b is converted to a's time unit using the project properties.
    *
    * @param a first duration
    * @param b second duration
    * @param defaults project properties containing default values
    * @return a + b
    */
   public static Duration add(Duration a, Duration b, TimeUnitDefaultsContainer defaults)
   {
      if (a == null && b == null)
      {
         return null;
      }
      if (a == null)
      {
         return b;
      }
      if (b == null)
      {
         return a;
      }
      TimeUnit unit = a.getUnits();
      if (b.getUnits() != unit)
      {
         b = b.convertUnits(unit, defaults);
      }

      return Duration.getInstance(a.getDuration() + b.getDuration(), unit);
   }

   /**
    * Negate this duration.
    *
    * @return negated duration
    */
   public Duration negate()
   {
      return m_duration == 0 ? this : Duration.getInstance(-m_duration, m_units);
   }

   @Override public String toString()
   {
      return (m_duration + m_units.toString());
   }

   /**
    * Duration amount.
    */
   private final double m_duration;

   /**
    * Duration type.
    */
   private final TimeUnit m_units;

   private static final Duration[] ZERO_DURATIONS =
   {
      new Duration(0, TimeUnit.MINUTES),
      new Duration(0, TimeUnit.HOURS),
      new Duration(0, TimeUnit.DAYS),
      new Duration(0, TimeUnit.WEEKS),
      new Duration(0, TimeUnit.MONTHS),
      new Duration(0, TimeUnit.YEARS),
      new Duration(0, TimeUnit.PERCENT),
      new Duration(0, TimeUnit.ELAPSED_MINUTES),
      new Duration(0, TimeUnit.ELAPSED_HOURS),
      new Duration(0, TimeUnit.ELAPSED_DAYS),
      new Duration(0, TimeUnit.ELAPSED_WEEKS),
      new Duration(0, TimeUnit.ELAPSED_MONTHS),
      new Duration(0, TimeUnit.ELAPSED_YEARS),
      new Duration(0, TimeUnit.ELAPSED_PERCENT)
   };
}
