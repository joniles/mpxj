/*
 * file:       MPXRate.java
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

import java.text.NumberFormat;
import java.text.ParseException;
import java.util.Locale;

/**
 * This class represents a currency rate per period of time (for example $10/h)
 * as found in an MPX file.
 */
public final class MPXRate implements ToStringRequiresFile
{
   /**
    * This constructor builds an instance of this class from a formatted String.
    *
    * @param format Number formatter
    * @param rate string containing rate value
    * @param locale target locale
    * @throws MPXException when string parse fails
    */
   MPXRate (NumberFormat format, String rate, Locale locale)
      throws MPXException
   {
      try
      {
         int index = rate.indexOf('/');
   
         if (index == -1)
         {
            m_amount = format.parse(rate).doubleValue();
            m_units = TimeUnit.HOURS;
         }
         else
         {
            m_amount = format.parse( rate.substring (0, index)).doubleValue();
            m_units = TimeUnit.parse(rate.substring (index+1), locale);
         }
      }
      
      catch (ParseException ex)
      {
         throw new MPXException ("Failed to parse rate", ex);
      }
   }

   /**
    * This constructor builds an instance of this class from a currency
    * amount and a time unit.
    *
    * @param amount currency amount
    * @param time time units
    */
   public MPXRate (Number amount, TimeUnit time)
   {
      if (amount == null)
      {
         m_amount = 0;
      }
      else
      {
         m_amount = amount.doubleValue();
      }

      m_units = time;
   }

   /**
    * This constructor builds an instance of this class from a currency
    * amount and a time unit.
    *
    * @param amount currency amount
    * @param time time units
    */
   public MPXRate (double amount, TimeUnit time)
   {
      m_amount = amount;
      m_units = time;
   }


   /**
    * Accessor method to retrieve the currency amount
    *
    * @return amount component of the rate
    */
   public double getAmount ()
   {
      return (m_amount);
   }

   /**
    * Accessor method to retrieve the time units
    *
    * @return time component of the rate
    */
   public TimeUnit getUnits ()
   {
      return (m_units);
   }

   /**
    * This method builds a String representation of the rate represented
    * by this instance.
    *
    * @param mpx parent mpx file
    * @return string representation of the rate
    */
   public String toString (MPXFile mpx)
   {
      NumberFormat format = mpx.getCurrencyFormat();
      Locale locale = mpx.getLocale();
      StringBuffer buffer = new StringBuffer (format.format(m_amount));
      buffer.append ("/");
      buffer.append (TimeUnit.format(m_units, locale));
      return (buffer.toString());
   }

   /**
    * {@inheritDoc}
    */
   public String toString ()
   {
      return (m_amount + "/" + m_units);
   }
   
   /**
    * {@inheritDoc}
    */
   public boolean equals (Object obj)
   {
      MPXRate rhs = (MPXRate)obj;
      return (m_amount == rhs.m_amount && m_units == rhs.m_units);
   }

   /**
    * {@inheritDoc}
    */
   public int hashCode()
   {
      return ((int)m_amount + m_units.hashCode());
   }
   
   /**
    * Rate amount.
    */
   private double m_amount;

   /**
    * Time type.
    */
   private TimeUnit m_units;
}
