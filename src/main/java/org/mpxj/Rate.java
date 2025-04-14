/*
 * file:       Rate.java
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
 * This class represents a currency rate per period of time (for example $10/h)
 * as found in an MPX file.
 */
public final class Rate
{
   /**
    * This constructor builds an instance of this class from a currency
    * amount and a time unit.
    *
    * @param amount currency amount
    * @param time time units
    */
   public Rate(Number amount, TimeUnit time)
   {
      m_amount = NumberHelper.getDouble(amount);
      m_units = time;
   }

   /**
    * This constructor builds an instance of this class from a currency
    * amount and a time unit.
    *
    * @param amount currency amount
    * @param time time units
    */
   public Rate(double amount, TimeUnit time)
   {
      m_amount = amount;
      m_units = time;
   }

   /**
    * Accessor method to retrieve the currency amount.
    *
    * @return amount component of the rate
    */
   public double getAmount()
   {
      return (m_amount);
   }

   /**
    * Accessor method to retrieve the time units.
    *
    * @return time component of the rate
    */
   public TimeUnit getUnits()
   {
      return (m_units);
   }

   /**
    * Compare two rates handling null values.
    *
    * @param rate1 rate to compare
    * @param rate2 rate ro compare
    * @return true if rates are equal/equivalent
    */
   public static boolean equals(Rate rate1, Rate rate2)
   {
      if ((rate1 == null || rate1.getAmount() == 0.0) && (rate2 == null || rate2.getAmount() == 0.0))
      {
         return true;
      }

      if (rate1 == null || rate2 == null)
      {
         return false;
      }

      return rate1.equals(rate2);
   }

   @Override public boolean equals(Object obj)
   {
      boolean result = false;
      if (obj instanceof Rate)
      {
         Rate rhs = (Rate) obj;
         result = amountComponentEquals(rhs) && m_units == rhs.m_units;
      }
      return result;
   }

   /**
    * Equality test for amount component of a Rate instance.
    * Note that this does not take into account the units - use with care!
    *
    * @param rhs rate to compare
    * @return true if amount components are equal, within the allowable delta
    */
   public boolean amountComponentEquals(Rate rhs)
   {
      return NumberHelper.equals(m_amount, rhs.m_amount, 0.00001);
   }

   @Override public int hashCode()
   {
      return ((int) m_amount + m_units.hashCode());
   }

   @Override public String toString()
   {
      return (m_amount + m_units.toString());
   }

   /**
    * Rate amount.
    */
   private final double m_amount;

   /**
    * Time type.
    */
   private final TimeUnit m_units;

   public static final Rate ZERO = new Rate(0, TimeUnit.HOURS);
}
