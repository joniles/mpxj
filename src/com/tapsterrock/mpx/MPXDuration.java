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

import java.util.Locale;


/**
 * This represents time durations as specified in an MPX file.
 */
public final class MPXDuration implements ToStringRequiresFile
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
      this(dur, DEFAULT_DECIMAL_FORMAT, Locale.ENGLISH);
   }

   /**
    * Constructs an instance of this class from a String representation
    * of a duration, and an MPXNumberFormat instance to describe the format
    * of the string representation.
    *
    * @param dur formatted duration value
    * @param format format description
    * @param locale current file locale
    * @throws MPXException
    */
   public MPXDuration (String dur, MPXNumberFormat format, Locale locale)
      throws MPXException
   {
      int lastIndex = dur.length() - 1;
      int index = lastIndex;

      while ((index > 0) && (Character.isDigit(dur.charAt(index)) == false))
      {
         --index;
      }

      //
      // If we have no units suffix, assume days to allow for MPX3
      //
      if (index == lastIndex)
      {
         m_duration = format.parse(dur).doubleValue();
         m_units = TimeUnit.DAYS;
      }
      else
      {
         ++index;
         m_duration = format.parse(dur.substring(0, index)).doubleValue();
         m_units = TimeUnit.parse(dur.substring(index), locale);
      }
   }

   /**
    * Copy constructor.
    *
    * @param duration original MPXDuration instance
    */
   public MPXDuration (MPXDuration duration)
   {
      m_duration = duration.m_duration;
      m_units = duration.m_units;
   }

   /**
    * Constructs an instance of this class from a duration amount and
    * time unit type.
    *
    * @param duration amount of duration
    * @param type time unit of duration
    */
   public MPXDuration (double duration, TimeUnit type)
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
   public MPXDuration (int duration, TimeUnit type)
   {
      m_duration = duration;
      m_units = type;
   }

   /**
    * This method generates a string in MPX format representing the
    * contents of this record. Note that this method is useful for
    * testing but it is not used to write data into the MPX record.
    *
    * @return string containing the data for this record in MPX format.
    */
   public String toString ()
   {
      return (toString(DEFAULT_DECIMAL_FORMAT, Locale.ENGLISH));
   }

   /**
    * This method generates a string in MPX format representing the
    * contents of this record.
    *
    * @param mpx parent mpx file
    * @return string containing the data for this record in MPX format.
    */
   public String toString (MPXFile mpx)
   {
      return (toString(mpx.getDurationDecimalFormat(), mpx.getLocale()));
   }

   /**
    * This method generates a string in MPX format representing the
    * contents of this record.
    *
    * @param format number format to use for the duration value
    * @param locale target locale
    * @return string containing the data for this record in MPX format.
    */
   private String toString (MPXNumberFormat format, Locale locale)
   {
      return (format.format(m_duration) + TimeUnit.format(m_units, locale));
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
    * units. It does not take account of calendar details, and the results
    * obtained from it should therefore be treated with caution.
    *
    * @param type target duration type
    * @return new MPXDuration instance
    */
   public MPXDuration convertUnits (TimeUnit type)
   {
      MPXDuration result;

      //
      // If the types are not already the same, then attempt a conversion
      //
      if (type.getValue() == m_units.getValue())
      {
         result = this;
      }
      else
      {
         //
         // First convert the duration to days
         //
         double duration = m_duration;

         switch (m_units.getValue())
         {
            case TimeUnit.MINUTES_VALUE:
            case TimeUnit.ELAPSED_MINUTES_VALUE:
            {
               duration /= MINUTES_PER_DAY;
               break;
            }

            case TimeUnit.HOURS_VALUE:
            case TimeUnit.ELAPSED_HOURS_VALUE:
            {
               duration /= HOURS_PER_DAY;
               break;
            }

            case TimeUnit.WEEKS_VALUE:
            case TimeUnit.ELAPSED_WEEKS_VALUE:
            {
               duration *= DAYS_PER_WEEK;
               break;
            }

            case TimeUnit.MONTHS_VALUE:
            case TimeUnit.ELAPSED_MONTHS_VALUE:
            {
               duration *= DAYS_PER_MONTH;
               break;
            }

            case TimeUnit.YEARS_VALUE:
            case TimeUnit.ELAPSED_YEARS_VALUE:
            {
               duration *= DAYS_PER_YEAR;
               break;
            }
         }

         //
         // Now convert the duration to the target type
         //
         switch (type.getValue())
         {
            case TimeUnit.MINUTES_VALUE:
            case TimeUnit.ELAPSED_MINUTES_VALUE:
            {
               duration *= MINUTES_PER_DAY;
               break;
            }

            case TimeUnit.HOURS_VALUE:
            case TimeUnit.ELAPSED_HOURS_VALUE:
            {
               duration *= HOURS_PER_DAY;
               break;
            }

            case TimeUnit.WEEKS_VALUE:
            case TimeUnit.ELAPSED_WEEKS_VALUE:
            {
               duration /= DAYS_PER_WEEK;
               break;
            }

            case TimeUnit.MONTHS_VALUE:
            case TimeUnit.ELAPSED_MONTHS_VALUE:
            {
               duration /= DAYS_PER_MONTH;
               break;
            }

            case TimeUnit.YEARS_VALUE:
            case TimeUnit.ELAPSED_YEARS_VALUE:
            {
               duration /= DAYS_PER_YEAR;
               break;
            }
         }

         result = new MPXDuration(duration, type);
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
   private TimeUnit m_units;

   /**
    * Number formatter format string.
    */
   static final String DECIMAL_FORMAT_STRING = "#.#";

   /**
    * Number formatter.
    */
   private static final MPXNumberFormat DEFAULT_DECIMAL_FORMAT = new MPXNumberFormat(DECIMAL_FORMAT_STRING, '.', ',');

   /**
    * Constants used for duration type conversion.
    */
   private static final double MINUTES_PER_DAY = 1440;
   private static final double HOURS_PER_DAY = 24;
   private static final double DAYS_PER_WEEK = 7;
   private static final double DAYS_PER_MONTH = 28;
   private static final double DAYS_PER_YEAR = 365;
}
