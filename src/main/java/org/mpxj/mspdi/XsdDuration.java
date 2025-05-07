/*
 * file:       XsdDuration.java
 * author:     Jon Iles
 * copyright:  (c) Packwood Software 2002-2003
 * date:       20/02/2003
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

package org.mpxj.mspdi;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;

import org.mpxj.Duration;

/**
 * This class parses and represents an xsd:duration value.
 */
final class XsdDuration
{
   /**
    * Constructor. Parses the xsd:duration value and extracts the
    * duration data from it.
    *
    * @param duration value formatted as an xsd:duration
    */
   public XsdDuration(String duration)
   {
      if (duration != null)
      {
         int length = duration.length();
         if (length > 0)
         {
            // We have come across schedules exported from Synchro which represent
            // zero duration as a plain `0` rather than a well-formed XSD Duration.
            // MS Project reads this, so we'll treat it as a special case.
            if (length == 1 && duration.charAt(0) == '0')
            {
               return;
            }

            if (duration.charAt(0) != 'P')
            {
               if (length < 2 || (duration.charAt(0) != '-' && duration.charAt(1) != 'P'))
               {
                  throw new IllegalArgumentException(duration);
               }
            }

            //
            // A minus sign at the start of the XSD duration is the correct way
            // to represent a negative duration according to the spec. MSPDI
            // disagrees and simply uses a negative number for the relevant
            // component of the duration. This code ensures we can parse a
            // spec-compliant value.
            //
            int index;
            boolean negative;
            if (duration.charAt(0) == '-')
            {
               index = 2;
               negative = true;
            }
            else
            {
               index = 1;
               negative = false;
            }

            while (index < length)
            {
               index = readComponent(duration, index, length);
            }

            if (negative)
            {
               m_years = -m_years;
               m_months = -m_months;
               m_days = -m_days;
               m_hours = -m_hours;
               m_minutes = -m_minutes;
               m_seconds = -m_seconds;
            }
         }
      }
   }

   /**
    * This constructor allows an xsd:duration to be created from
    * an MPX duration.
    *
    * @param duration An MPX duration.
    */
   public XsdDuration(Duration duration)
   {
      if (duration == null)
      {
         return;
      }

      double rawDuration = duration.getDuration();
      if (rawDuration == 0)
      {
         return;
      }

      long time;

      switch (duration.getUnits())
      {
         case MINUTES:
         case ELAPSED_MINUTES:
         {
            time = Math.round(rawDuration * 60.0);
            m_seconds = time % 60;
            time /= 60;
            m_minutes = time;
            break;
         }

         case HOURS:
         case ELAPSED_HOURS:
         {
            time = Math.round(rawDuration * 60.0 * 60.0);
            m_seconds = time % 60;
            time /= 60;
            m_minutes = time % 60;
            time /= 60;
            m_hours = time;
            break;
         }

         case DAYS:
         case ELAPSED_DAYS:
         {
            time = Math.round(rawDuration * 60.0 * 60.0 * 24.0);
            m_seconds = time % 60;
            time /= 60;
            m_minutes = time % 60;
            time /= 60;
            m_hours = time % 24;
            time /= 24;
            m_days = time;
            break;
         }

         case WEEKS:
         case ELAPSED_WEEKS:
         {
            time = Math.round(rawDuration * 60.0 * 60.0 * 24.0 * 7.0);
            m_seconds = time % 60;
            time /= 60;
            m_minutes = time % 60;
            time /= 60;
            m_hours = time % 24;
            time /= 24;
            m_days = time;
            break;
         }

         case MONTHS:
         case ELAPSED_MONTHS:
         {
            time = Math.round(rawDuration * 60.0 * 60.0 * 24.0 * 28.0);
            m_seconds = time % 60;
            time /= 60;
            m_minutes = time % 60;
            time /= 60;
            m_hours = time % 24;
            time /= 24;
            m_days = time % 28;
            time /= 28;
            m_months = time;
            break;
         }

         case YEARS:
         case ELAPSED_YEARS:
         {
            time = Math.round(rawDuration * 60.0 * 60.0 * 24.0 * 28.0 * 12.0);
            m_seconds = time % 60;
            time /= 60;
            m_minutes = time % 60;
            time /= 60;
            m_hours = time % 24;
            time /= 24;
            m_days = time % 28;
            time /= 28;
            m_months = time % 12;
            time /= 12;
            m_years = time;
            break;
         }

         default:
         {
            break;
         }
      }
   }

   /**
    * This method is called repeatedly to parse each duration component
    * from sorting data in xsd:duration format. Each component consists
    * of a number, followed by a letter representing the type.
    *
    * @param duration xsd:duration formatted string
    * @param index current position in the string
    * @param length length of string
    * @return current position in the string
    */
   private int readComponent(String duration, int index, int length)
   {
      char c = 0;
      StringBuilder number = new StringBuilder();

      while (index < length)
      {
         c = duration.charAt(index);

         //
         // We shouldn't see a minus sign here according to the spec,
         // but this is how MSPDI represents negative duration
         // values, so we ensure that we can handle this format.
         //
         if (Character.isDigit(c) || c == '.' || c == '-')
         {
            number.append(c);
         }
         else
         {
            break;
         }

         ++index;
      }

      switch (c)
      {
         case 'Y':
         {
            m_years = Long.parseLong(number.toString());
            break;
         }

         case 'M':
         {
            if (!m_hasTime)
            {
               m_months = Long.parseLong(number.toString());
            }
            else
            {
               m_minutes = Long.parseLong(number.toString());
            }
            break;
         }

         case 'D':
         {
            m_days = Long.parseLong(number.toString());
            break;
         }

         case 'T':
         {
            m_hasTime = true;
            break;
         }

         case 'H':
         {
            m_hours = Long.parseLong(number.toString());
            break;
         }

         case 'S':
         {
            m_seconds = Double.parseDouble(number.toString());
            break;
         }

         default:
         {
            throw new IllegalArgumentException(duration);
         }
      }

      ++index;

      return index;
   }

   /**
    * Retrieves the number of days.
    *
    * @return number of days
    */
   public long getDays()
   {
      return m_days;
   }

   /**
    * Retrieves the number of hours.
    *
    * @return number of hours
    */
   public long getHours()
   {
      return m_hours;
   }

   /**
    * Retrieves the number of minutes.
    *
    * @return number of minutes
    */
   public long getMinutes()
   {
      return m_minutes;
   }

   /**
    * Retrieves the number of months.
    *
    * @return number of months
    */
   public long getMonths()
   {
      return m_months;
   }

   /**
    * Retrieves the number of seconds.
    *
    * @return number of seconds
    */
   public double getSeconds()
   {
      return m_seconds;
   }

   /**
    * Retrieves the number of years.
    *
    * @return number of years
    */
   public long getYears()
   {
      return m_years;
   }

   /**
    * Prints the duration.
    *
    * @param microsoftProjectCompatible false for spec compliant, true for readable by Microsoft Project
    * @return XSD duration value
    */
   public String print(boolean microsoftProjectCompatible)
   {
      StringBuilder buffer = new StringBuilder("P");
      boolean negative = false;

      if (m_years != 0 || m_months != 0 || m_days != 0)
      {
         if (m_years < 0)
         {
            negative = true;
            buffer.append(-m_years);
         }
         else
         {
            buffer.append(m_years);
         }
         buffer.append("Y");

         if (m_months < 0)
         {
            negative = true;
            buffer.append(-m_months);
         }
         else
         {
            buffer.append(m_months);
         }
         buffer.append("M");

         if (m_days < 0)
         {
            negative = true;
            buffer.append(-m_days);
         }
         else
         {
            buffer.append(m_days);
         }
         buffer.append("D");
      }

      buffer.append("T");

      if (m_hours < 0)
      {
         negative = true;
         buffer.append(-m_hours);
      }
      else
      {
         buffer.append(m_hours);
      }
      buffer.append("H");

      if (m_minutes < 0)
      {
         negative = true;
         buffer.append(-m_minutes);
      }
      else
      {
         buffer.append(m_minutes);
      }
      buffer.append("M");

      if (m_seconds < 0)
      {
         negative = true;
         buffer.append(FORMAT.format(-m_seconds));
      }
      else
      {
         buffer.append(FORMAT.format(m_seconds));
      }
      buffer.append("S");

      if (negative)
      {
         if (microsoftProjectCompatible)
         {
            int index = 0;
            while (index < buffer.length())
            {
               char c = buffer.charAt(index);
               if (Character.isDigit(c) && c != '0')
               {
                  buffer.insert(index, '-');
                  break;
               }
               ++index;
            }
         }
         else
         {
            buffer.insert(0, '-');
         }
      }

      return buffer.toString();
   }

   /**
    * This method generates the string representation of an xsd:duration value.
    *
    * @return xsd:duration value
    */
   @Override public String toString()
   {
      return print(true);
   }

   private boolean m_hasTime;
   private long m_years;
   private long m_months;
   private long m_days;
   private long m_hours;
   private long m_minutes;
   private double m_seconds;

   /**
    * Configure the decimal separator to be independent of the
    * one used by the default locale.
    */
   private static final DecimalFormatSymbols SYMBOLS = new DecimalFormatSymbols();
   static
   {
      SYMBOLS.setDecimalSeparator('.');
   }

   private static final DecimalFormat FORMAT = new DecimalFormat("#", SYMBOLS);
}
