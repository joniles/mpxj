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

package com.tapsterrock.mpx;

/**
 * This represents time durations as specified in an MPX file.
 */
public final class MPXDuration
{
   /**
    * Constructs an instance of this class from a String representation
    * of a duration.
    *
    * @param dur String representation of a duration
    * @throws MPXException normally indicating that parsing the string has failed
    */
   public MPXDuration (String dur)
      throws MPXException
   {
      this(dur, DEFAULT_DECIMAL_FORMAT);
   }

   /**
    * Constructs an instance of this class from a String representation
    * of a duration, and an MPXNumberFormat instance to describe the format
    * of the string representation.
    *
    * @param dur formatted duration value
    * @param format format description
    * @throws MPXException
    */
   public MPXDuration (String dur, MPXNumberFormat format)
      throws MPXException
   {

      int index = dur.length() - 1;

      while (index > 0 && Character.isDigit(dur.charAt(index)) == false)
      {
         --index;
      }

      if (index == -1)
      {
         throw new MPXException (MPXException.INVALID_DURATION + " " + dur);
      }

      ++index;

      m_duration = format.parse(dur.substring(0, index)).doubleValue();
      m_type = TimeUnit.parse(dur.substring(index));
   }

   /**
    * Copy constructor.
    *
    * @param duration original MPXDuration instance
    */
   public MPXDuration (MPXDuration duration)
   {
      m_duration = duration.m_duration;
      m_type = duration.m_type;
   }

   /**
    * Constructs an instance of this class from a duration amount and
    * time unit type.
    *
    * @param duration amount of duration
    * @param type time unit of duration
    */
   public MPXDuration (double duration, int type)
   {
      m_duration = duration;
      m_type = type;
   }

   /**
    * Constructs an instance of this class from a duration amount and
    * time unit type.
    *
    * @param duration amount of duration
    * @param type time unit of duration
    */
   public MPXDuration (int duration, int type)
   {
      m_duration = (double)duration;
      m_type = type;
   }

   /**
    * This method generates a string in MPX format representing the
    * contents of this record.
    *
    * @return string containing the data for this record in MPX format.
    */
   public String toString ()
   {
      return (toString(DEFAULT_DECIMAL_FORMAT));
   }

   /**
    * This method generates a string in MPX format representing the
    * contents of this record.
    *
    * @param format number format to use for the duration value
    * @return string containing the data for this record in MPX format.
    */
   String toString (MPXNumberFormat format)
   {
      return (format.format(m_duration) + TimeUnit.format(m_type));
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
   public int getType ()
   {
      return (m_type);
   }

   /**
    * This method provides an <i>approximate</i> conversion between duration
    * units. It does not take account of calendar details, and the results
    * obtained from it should therefore be treated with caution.
    *
    * @param type target duration type
    * @return new MPXDuration instance
    */
   public MPXDuration convertUnits (int type)
   {
      MPXDuration result;

      //
      // If the types are not already the same, then attempt a conversion
      //
      if (type == m_type)
      {
         result = this;
      }
      else
      {
         //
         // First convert the duration to days
         //
         double duration = m_duration;

         switch (m_type)
         {
            case TimeUnit.MINUTES:
            case TimeUnit.ELAPSED_MINUTES:
            {
               duration /= MINUTES_PER_DAY;
               break;
            }

            case TimeUnit.HOURS:
            case TimeUnit.ELAPSED_HOURS:
            {
               duration /= HOURS_PER_DAY;
               break;
            }

            case TimeUnit.WEEKS:
            case TimeUnit.ELAPSED_WEEKS:
            {
               duration *= DAYS_PER_WEEK;
               break;
            }

            case TimeUnit.MONTHS:
            case TimeUnit.ELAPSED_MONTHS:
            {
               duration *= DAYS_PER_MONTH;
               break;
            }

            case TimeUnit.YEARS:
            case TimeUnit.ELAPSED_YEARS:
            {
               duration *= DAYS_PER_YEAR;
               break;
            }
         }

         //
         // Now convert the duration to the target type
         //
         switch (type)
         {
            case TimeUnit.MINUTES:
            case TimeUnit.ELAPSED_MINUTES:
            {
               duration *= MINUTES_PER_DAY;
               break;
            }

            case TimeUnit.HOURS:
            case TimeUnit.ELAPSED_HOURS:
            {
               duration *= HOURS_PER_DAY;
               break;
            }

            case TimeUnit.WEEKS:
            case TimeUnit.ELAPSED_WEEKS:
            {
               duration /= DAYS_PER_WEEK;
               break;
            }

            case TimeUnit.MONTHS:
            case TimeUnit.ELAPSED_MONTHS:
            {
               duration /= DAYS_PER_MONTH;
               break;
            }

            case TimeUnit.YEARS:
            case TimeUnit.ELAPSED_YEARS:
            {
               duration /= DAYS_PER_YEAR;
               break;
            }
         }

         result = new MPXDuration (duration, type);
      }

      return (result);
   }

   /**
    * Duration amount.
    */
   private double m_duration;

   /**
    * Duration type.
    */
   private int m_type;

   /**
    * Number formatter format string.
    */
   static final String DECIMAL_FORMAT_STRING = "#.#";

   /**
    * Number formatter.
    */
   private static final MPXNumberFormat DEFAULT_DECIMAL_FORMAT = new MPXNumberFormat (DECIMAL_FORMAT_STRING, '.', ',');

   /**
    * Constants used for duration type conversion.
    */
   private static final double MINUTES_PER_DAY = 1440;
   private static final double HOURS_PER_DAY = 24;
   private static final double DAYS_PER_WEEK = 7;
   private static final double DAYS_PER_MONTH = 28;
   private static final double DAYS_PER_YEAR = 365;
}
