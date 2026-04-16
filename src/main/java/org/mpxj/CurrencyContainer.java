/*
 * file:       CurrencyContainer.java
 * author:     Jon Iles
 * date:       2025-01-07
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

/**
 * Represents the currencies available to the current project.
 */
public class CurrencyContainer extends ProjectEntityContainer<Currency>
{
   /**
    * Constructor.
    *
    * @param sequenceProvider sequence provider
    */
   public CurrencyContainer(UniqueIdObjectSequenceProvider sequenceProvider)
   {
      super(sequenceProvider);
   }

   /**
    * Set the default currency unique ID.
    *
    * @param value default currency unique ID
    */
   public void setDefaultCurrencyUniqueID(Integer value)
   {
      m_defaultCurrencyUniqueID = value;
   }

   /**
    * Retrieve the default currency ID.
    *
    * @return default currency ID
    */
   public Integer getDefaultCurrencyUniqueID()
   {
      return m_defaultCurrencyUniqueID;
   }

   /**
    * Retrieve the default Currency instance.
    *
    * @return default Currency instance
    */
   public Currency getDefaultCurrency()
   {
      if (m_defaultCurrencyUniqueID != null)
      {
         return getByUniqueID(m_defaultCurrencyUniqueID);
      }

      Currency currency = getByUniqueID(Integer.valueOf(1));
      if (currency != null)
      {
         return currency;
      }

      return DEFAULT_CURRENCY;
   }

   private Integer m_defaultCurrencyUniqueID;

   public static final Currency DEFAULT_CURRENCY = new Currency.Builder(null)
      .uniqueID(Integer.valueOf(1))
      .numberOfDecimalPlaces(Integer.valueOf(2))
      .symbol("$")
      .decimalSymbol(".")
      .digitGroupingSymbol(",")
      .positiveCurrencyFormat("#1.1")
      .negativeCurrencyFormat("(#1.1)")
      .name("US Dollar")
      .currencyID("USD")
      .exchangeRate(Double.valueOf(1.0))
      .build();
}
