/*
 * file:       MPXDuration.java
 * author:     Scott Melville
 *             Jon Iles
 * copyright:  (c) Tapster Rock Limited 2002-2003
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

package net.sf.mpxj;

import java.text.NumberFormat;
import java.text.ParseException;
import java.util.Locale;


/**
 * This represents time durations as specified in an MPX file.
 */
public final class Duration
{
   /**
    * Constructs an instance of this class from a duration amount and
    * time unit type.
    *
    * @param duration amount of duration
    * @param type time unit of duration
    */
   private Duration (double duration, TimeUnit type)
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
   private Duration (int duration, TimeUnit type)
   {
      m_duration = duration;
      m_units = type;
   }

   /**
    * This method is used to retrieve the size of the duration.
    *
    * @return size of the duration
    */
   public double getDuration ()
   {
      return (m_duration);
   }

   /**
    * This method is used to retreve the type of units the duration
    * is expressed in. The valid types of units are found in the TimeUnit
    * class.
    *
    * @return type of units
    */
   public TimeUnit getUnits ()
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
    * @param defaults project header containing default values
    * @return new MPXDuration instance
    */
   public Duration convertUnits (TimeUnit type, ProjectHeader defaults)
   {
      return (convertUnits(m_duration, m_units, type, defaults));
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
    * @param defaults project header containing default values
    * @return new MPXDuration instance
    */
   public static Duration convertUnits (double duration, TimeUnit fromUnits, TimeUnit toUnits, ProjectHeader defaults)
   {
      switch (fromUnits.getValue())
      {
         case TimeUnit.YEARS_VALUE:
         {
            duration *= (defaults.getDefaultHoursInWeek().doubleValue() * 52);            
            break;
         }

         case TimeUnit.ELAPSED_YEARS_VALUE:
         {
            duration *= (24 * 7 * 52);
            break;
         }
         
         case TimeUnit.MONTHS_VALUE:
         {
            duration *= (defaults.getDefaultHoursInWeek().doubleValue() * 4);            
            break;
         }

         case TimeUnit.ELAPSED_MONTHS_VALUE:
         {
            duration *= (24 * 7 * 4);            
            break;
         }
         
         case TimeUnit.WEEKS_VALUE:
         {
            duration *= defaults.getDefaultHoursInWeek().doubleValue();            
            break;
         }

         case TimeUnit.ELAPSED_WEEKS_VALUE:
         {
            duration *= (24 * 7);            
            break;
         }
         
         case TimeUnit.DAYS_VALUE:
         {
            duration *= defaults.getDefaultHoursInDay().doubleValue();            
            break;
         }

         case TimeUnit.ELAPSED_DAYS_VALUE:
         {
            duration *= 24;            
            break;
         }
         

         case TimeUnit.MINUTES_VALUE:
         case TimeUnit.ELAPSED_MINUTES_VALUE:
         {
            duration /= 60;            
            break;
         }
      }

      
      if (toUnits != TimeUnit.HOURS && toUnits != TimeUnit.ELAPSED_HOURS)
      {
         switch (toUnits.getValue())
         {
            case TimeUnit.MINUTES_VALUE:
            case TimeUnit.ELAPSED_MINUTES_VALUE:
            {
               duration *= 60;
               break;
            }

            case TimeUnit.DAYS_VALUE:
            {
               duration /= defaults.getDefaultHoursInDay().doubleValue();
               break;
            }

            case TimeUnit.ELAPSED_DAYS_VALUE:
            {
               duration /= 24;
               break;
            }
            
            case TimeUnit.WEEKS_VALUE:
            {
               duration /= defaults.getDefaultHoursInWeek().doubleValue();
               break;
            }

            case TimeUnit.ELAPSED_WEEKS_VALUE:
            {
               duration /= (24 * 7);
               break;
            }
            
            case TimeUnit.MONTHS_VALUE:
            {
               duration /= (defaults.getDefaultHoursInWeek().doubleValue() * 4);
               break;
            }

            case TimeUnit.ELAPSED_MONTHS_VALUE:
            {
               duration /= (24 * 7 * 4);
               break;
            }
            
            case TimeUnit.YEARS_VALUE:
            {
               duration /= (defaults.getDefaultHoursInWeek().doubleValue() * 52);
               break;
            }
            
            case TimeUnit.ELAPSED_YEARS_VALUE:
            {
               duration /= (24 * 7 * 52);
               break;
            }                           
         }
      }
      
      return (Duration.getInstance (duration, toUnits));      
   }   
   
   /**
    * Retrieve an MPXDuration instance. Use shared objects to
    * represent common values for memory efficiency.
    * 
    * @param duration duration value
    * @param type duration type
    * @return MPXDuration instance
    */
   public static Duration getInstance (double duration, TimeUnit type)
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
      return(result);
   }

   /**
    * Retrieve an MPXDuration instance. Use shared objects to
    * represent common values for memory efficiency.
    * 
    * @param duration duration value
    * @param type duration type
    * @return MPXDuration instance
    */   
   public static Duration getInstance (int duration, TimeUnit type)
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
      return(result);
   }

   /**
    * Retrieve an MPXDuration instance. Use shared objects to
    * represent common values for memory efficiency.
    * 
    * @param dur duration formatted as a string
    * @param format number format
    * @param locale target locale
    * @return MPXDuration instance
    * @throws MPXJException
    */
   public static Duration getInstance (String dur, NumberFormat format, Locale locale)
      throws MPXJException
   {
      try
      {
         int lastIndex = dur.length() - 1;
         int index = lastIndex;
         double duration;
         TimeUnit units;
         
         while ((index > 0) && (Character.isDigit(dur.charAt(index)) == false))
         {
            --index;
         }
      
         //
         // If we have no units suffix, assume days to allow for MPX3
         //
         if (index == lastIndex)
         {
            duration = format.parse(dur).doubleValue();
            units = TimeUnit.DAYS;
         }
         else
         {
            ++index;
            duration = format.parse(dur.substring(0, index)).doubleValue();
            units = TimeUnit.parse(dur.substring(index), locale);
         }
         
         return (getInstance(duration, units));
      }
      
      catch (ParseException ex)
      {
         throw new MPXJException ("Failed to parse duration", ex);
      }
   }

   /**
    * {@inheritDoc}
    */
   public boolean equals (Object o)
   {
      Duration rhs = (Duration)o;
      return (m_duration == rhs.m_duration && m_units == rhs.m_units);
   }
   
   /**
    * {@inheritDoc}
    */
   public int hashCode ()
   {
      return (m_units.getValue() + (int)m_duration);
   }
        
   /**
    * Duration amount.
    */
   private double m_duration;

   /**
    * Duration type.
    */
   private TimeUnit m_units;

   
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
