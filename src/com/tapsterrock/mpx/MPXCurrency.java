/*
 * file:       MPXCurrency.java
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
 * This class is used to represent the monetary amounts found in an MPX file.
 */
public class MPXCurrency
{
   /**
    * Constructor used to parse a string to extract details of the
    * currency amount.
    *
    * @param parent parent MPX file
    * @param amount string representation of a currency amount
    * @throws MPXException when string parse fails
    */
   public MPXCurrency (MPXFile parent, String amount)
      throws MPXException
   {
      m_format = parent.getCurrencyFormat();
      m_amount = m_format.parse(amount).floatValue();
   }

   /**
    * Constructor used to pass details of the currency amount as a float.
    *
    * @param parent parent MPX file
    * @param amount float representation of a currency amount
    */
   public MPXCurrency (MPXFile parent, float amount)
   {
      m_format = parent.getCurrencyFormat();
      m_amount = amount;
   }

   /**
    * Accessor method to retrieve the amount
    *
    * @return value
    */
   public float getAmount()
   {
      return (m_amount);
   }

   /**
    * This method builds a String representation of the amount represented
    * by this instance.
    *
    * @return string representation of the amount
    */
   public String toString ()
   {
      return (m_format.format (m_amount));
   }

   private MPXNumberFormat m_format;

   private float m_amount;
}
