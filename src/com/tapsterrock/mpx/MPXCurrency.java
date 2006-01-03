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

import java.text.NumberFormat;
import java.text.ParseException;

/**
 * This class is used to represent the monetary amounts found in an MPX file.
 */
public final class MPXCurrency extends Number
{
   /**
    * Constructor used to pass details of the currency amount as a double.
    *
    * @param format Currency format
    * @param amount double representation of a currency amount
    */
   MPXCurrency (NumberFormat format, double amount)
   {
      m_format = format;
      m_amount = amount;
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

   /**
    * Returns the value of the specified number as an <code>int</code>.
    * This may involve rounding.
    *
    * @return  the numeric value represented by this object after conversion
    *          to type <code>int</code>.
    */
   public int intValue()
   {
      return ((int)m_amount);
   }

   /**
    * Returns the value of the specified number as a <code>long</code>.
    * This may involve rounding.
    *
    * @return  the numeric value represented by this object after conversion
    *          to type <code>long</code>.
    */
   public long longValue()
   {
      return ((long)m_amount);
   }

   /**
    * Returns the value of the specified number as a <code>float</code>.
    * This may involve rounding.
    *
    * @return  the numeric value represented by this object after conversion
    *          to type <code>float</code>.
    */
   public float floatValue()
   {
      return ((float)m_amount);
   }

   /**
    * Returns the value of the specified number as a <code>double</code>.
    * This may involve rounding.
    *
    * @return  the numeric value represented by this object after conversion
    *          to type <code>double</code>.
    */
   public double doubleValue ()
   {
      return (m_amount);
   }

   /**
    * Retrieve an MPXCurrency instance to represent the supplied value.
    * This method improves memory efficiency by using shared objects
    * to represent common values.
    * 
    * @param parent parent file
    * @param amount string representation of the currency amount
    * @return MPXCurrency instance
    * @throws MPXException
    */
   public static final MPXCurrency getInstance (ProjectFile parent, String amount)
      throws MPXException
   {
      try
      {
         NumberFormat format = parent.getCurrencyFormat();
         double value = format.parse(amount).doubleValue();
         MPXCurrency result;
         
         if (value == 0)
         {
            result = parent.getZeroCurrency();
         }
         else
         {
            result = new MPXCurrency(format, value);
         }
         
         return (result);
      }
      
      catch (ParseException ex)
      {
         throw new MPXException ("Failed to parse currency", ex);
      }
   }
   
   /**
    * Formatter used to format the currency amount.
    */
   private NumberFormat m_format;

   /**
    * Internal representation of the currency amount.
    */
   private double m_amount;
}
