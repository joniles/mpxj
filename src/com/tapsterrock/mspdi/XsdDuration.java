/*
 * file:       XsdDuration.java
 * author:     Jon Iles
 * copyright:  (c) Tapster Rock Limited 2002-2003
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

package com.tapsterrock.mspdi;

import com.tapsterrock.mpx.MPXDuration;
import com.tapsterrock.mpx.TimeUnit;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;

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
   XsdDuration (String duration)
   {
      if (duration != null)
      {
         int length = duration.length();
         if (length > 0)
         {
            if (duration.charAt(0) != 'P')
            {
               if (length < 2 || (duration.charAt(0) != '-' && duration.charAt(1) != 'P'))
               {
                  throw new IllegalArgumentException (duration);
               }
            }

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
               index = readComponent (duration, index, length);
            }

            if (negative == true)
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
   XsdDuration (MPXDuration duration)
   {
      if (duration != null)
      {
         double amount = duration.getDuration();

         if (amount != 0)
         {
            switch (duration.getUnits().getValue())
            {
               case TimeUnit.MINUTES_VALUE:
               case TimeUnit.ELAPSED_MINUTES_VALUE:
               {
                  m_minutes = (int)amount;
                  m_seconds = (amount * 60) - (m_minutes * 60);
                  break;
               }

               case TimeUnit.HOURS_VALUE:
               case TimeUnit.ELAPSED_HOURS_VALUE:
               {
                  m_hours = (int)amount;
                  amount = (amount * 60) - (m_hours * 60);
                  m_minutes = (int)amount;
                  m_seconds = (amount * 60) - (m_minutes * 60);
                  break;
               }

               case TimeUnit.DAYS_VALUE:
               case TimeUnit.ELAPSED_DAYS_VALUE:
               {
                  m_days = (int)amount;
                  amount = (amount * 24) - (m_days * 24);
                  m_hours = (int)amount;
                  amount = (amount * 60) - (m_hours * 60);
                  m_minutes = (int)amount;
                  m_seconds = (amount * 60) - (m_minutes * 60);
                  break;
               }

               case TimeUnit.WEEKS_VALUE:
               case TimeUnit.ELAPSED_WEEKS_VALUE:
               {
                  amount *= 7;
                  m_days = (int)amount;
                  amount = (amount * 24) - (m_days * 24);
                  m_hours = (int)amount;
                  amount = (amount * 60) - (m_hours * 60);
                  m_minutes = (int)amount;
                  m_seconds = (amount * 60) - (m_minutes * 60);
                  break;
               }

               case TimeUnit.MONTHS_VALUE:
               case TimeUnit.ELAPSED_MONTHS_VALUE:
               {
                  m_months = (int)amount;
                  amount = (amount * 28) - (m_months * 28);
                  m_days = (int)amount;
                  amount = (amount * 24) - (m_days * 24);
                  m_hours = (int)amount;
                  amount = (amount * 60) - (m_hours * 60);
                  m_minutes = (int)amount;
                  m_seconds = (amount * 60) - (m_minutes * 60);
                  break;
               }

               case TimeUnit.YEARS_VALUE:
               case TimeUnit.ELAPSED_YEARS_VALUE:
               {
                  m_years = (int)amount;
                  amount = (amount * 12) - (m_years * 12);
                  m_months = (int)amount;
                  amount = (amount * 28) - (m_months * 28);
                  m_days = (int)amount;
                  amount = (amount * 24) - (m_days * 24);
                  m_hours = (int)amount;
                  amount = (amount * 60) - (m_hours * 60);
                  m_minutes = (int)amount;
                  m_seconds = (amount * 60) - (m_minutes * 60);
                  break;
               }
            }
         }
      }
   }

   /**
    * This method is called repeatedly to parse each duration component
    * from srting data in xsd:duration format. Each component consists
    * of a number, followed by a letter representing the type.
    *
    * @param duration xsd:duration formatted string
    * @param index current position in the string
    * @param length length of string
    * @return current position in the string
    */
   private int readComponent (String duration, int index, int length)
   {
      char c = 0;
      StringBuffer number = new StringBuffer ();

      while (index < length)
      {
         c = duration.charAt(index);
         if (Character.isDigit(c) == true || c == '.')
         {
            number.append (c);
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
            m_years = Integer.parseInt(number.toString());
            break;
         }

         case 'M':
         {
            if (m_hasTime == false)
            {
               m_months = Integer.parseInt(number.toString());
            }
            else
            {
               m_minutes = Integer.parseInt(number.toString());
            }
            break;
         }

         case 'D':
         {
            m_days = Integer.parseInt(number.toString());
            break;
         }

         case 'T':
         {
            m_hasTime = true;
            break;
         }

         case 'H':
         {
            m_hours = Integer.parseInt(number.toString());
            break;
         }

         case 'S':
         {
            m_seconds = Double.parseDouble(number.toString());
            break;
         }

         default:
         {
            throw new IllegalArgumentException (duration);
         }
      }

      ++index;

      return (index);
   }


   /**
    * Retrieves the number of days.
    *
    * @return int
    */
   public int getDays()
   {
      return (m_days);
   }

   /**
    * Retrieves the number of hours.
    *
    * @return int
    */
   public int getHours()
   {
      return (m_hours);
   }

   /**
    * Retrieves the number of minutes.
    *
    * @return int
    */
   public int getMinutes()
   {
      return (m_minutes);
   }

   /**
    * Retrieves the number of months.
    *
    * @return int
    */
   public int getMonths()
   {
      return (m_months);
   }

   /**
    * Retrieves the number of seconds.
    *
    * @return double
    */
   public double getSeconds()
   {
      return (m_seconds);
   }

   /**
    * Retrieves the number of years.
    *
    * @return int
    */
   public int getYears()
   {
      return (m_years);
   }

   /**
    * This method generates the string representation of an xsd:duration value.
    *
    * @return xsd:duration value
    */
   public String toString ()
   {
      StringBuffer buffer = new StringBuffer ("P");
      boolean negative = false;

      if (m_years != 0 || m_months != 0 || m_days != 0)
      {
         if (m_years < 0)
         {
            negative = true;
            buffer.append (-m_years);
         }
         else
         {
            buffer.append (m_years);
         }
         buffer.append ("Y");

         if (m_months < 0)
         {
            negative = true;
            buffer.append (-m_months);
         }
         else
         {
            buffer.append (m_months);
         }
         buffer.append ("M");

         if (m_days < 0)
         {
            negative = true;
            buffer.append (-m_days);
         }
         else
         {
            buffer.append (m_days);
         }
         buffer.append ("D");
      }

      buffer.append ("T");

      if (m_hours < 0)
      {
         negative = true;
         buffer.append (-m_hours);
      }
      else
      {
         buffer.append (m_hours);
      }
      buffer.append ("H");

      if (m_minutes < 0)
      {
         negative = true;
         buffer.append (-m_minutes);
      }
      else
      {
         buffer.append (m_minutes);
      }
      buffer.append ("M");

      if (m_seconds < 0)
      {
         negative = true;
         buffer.append (FORMAT.format(-m_seconds));
      }
      else
      {
         buffer.append (FORMAT.format(m_seconds));
      }
      buffer.append ("S");

      if (negative == true)
      {
         buffer.insert(0, '-');
      }

      return (buffer.toString());
   }

   private boolean m_hasTime;
   private int m_years;
   private int m_months;
   private int m_days;
   private int m_hours;
   private int m_minutes;
   private double m_seconds;

   /**
    * Configure the decimal separator to be independent of the
    * one used by the default locale.
    */
   private static final DecimalFormatSymbols SYMBOLS = new DecimalFormatSymbols ();
   static
   {
      SYMBOLS.setDecimalSeparator('.');
   }

   private static final DecimalFormat FORMAT = new DecimalFormat ("#", SYMBOLS);
}

