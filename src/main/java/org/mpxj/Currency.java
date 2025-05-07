/*
 * file:       Currency.java
 * author:     Jon Iles
 * date:       2025-01-08
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
 * Represents a currency.
 */
public final class Currency implements ProjectEntityWithUniqueID
{
   /**
    * Constructor.
    *
    * @param builder currency builder
    */
   private Currency(Builder builder)
   {
      m_uniqueID = builder.m_sequenceProvider == null ? builder.m_uniqueID : builder.m_sequenceProvider.getUniqueIdObjectSequence(Currency.class).syncOrGetNext(builder.m_uniqueID);
      m_currencyID = builder.m_currencyID;
      m_name = builder.m_name;
      m_symbol = builder.m_symbol;
      m_exchangeRate = builder.m_exchangeRate;
      m_decimalSymbol = builder.m_decimalSymbol;
      m_numberOfDecimalPlaces = builder.m_numberOfDecimalPlaces;
      m_digitGroupingSymbol = builder.m_digitGroupingSymbol;
      m_positiveCurrencyFormat = builder.m_positiveCurrencyFormat;
      m_negativeCurrencyFormat = builder.m_negativeCurrencyFormat;
   }

   @Override public Integer getUniqueID()
   {
      return m_uniqueID;
   }

   /**
    * Retrieve the currency ID.
    *
    * @return currency ID
    */
   public String getCurrencyID()
   {
      return m_currencyID;
   }

   /**
    * Retrieve the currency name.
    *
    * @return currency name
    */
   public String getName()
   {
      return m_name;
   }

   /**
    * Retrieve the currency symbol.
    *
    * @return currency symbol
    */
   public String getSymbol()
   {
      return m_symbol;
   }

   /**
    * Retrieve the exchange rate.
    *
    * @return exchange rate
    */
   public Double getExchangeRate()
   {
      return m_exchangeRate;
   }

   /**
    * Retrieve the decimal symbol.
    *
    * @return decimal symbol
    */
   public String getDecimalSymbol()
   {
      return m_decimalSymbol;
   }

   /**
    * Retrieve the number of decimal places to be displayed.
    *
    * @return decimal places
    */
   public Integer getNumberOfDecimalPlaces()
   {
      return m_numberOfDecimalPlaces;
   }

   /**
    * Retrieve the digit grouping symbol.
    *
    * @return digit grouping symbol
    */
   public String getDigitGroupingSymbol()
   {
      return m_digitGroupingSymbol;
   }

   /**
    * Retrieve the positive currency display format.
    *
    * @return positive currency format
    */
   public String getPositiveCurrencyFormat()
   {
      return m_positiveCurrencyFormat;
   }

   /**
    * Retrieve the negative currency display format.
    *
    * @return negative currency display format
    */
   public String getNegativeCurrencyFormat()
   {
      return m_negativeCurrencyFormat;
   }

   private final Integer m_uniqueID;
   private final String m_currencyID;
   private final String m_name;
   private final String m_symbol;
   private final Double m_exchangeRate;
   private final String m_decimalSymbol;
   private final Integer m_numberOfDecimalPlaces;
   private final String m_digitGroupingSymbol;
   private final String m_positiveCurrencyFormat;
   private final String m_negativeCurrencyFormat;

   /**
    * Currency builder.
    */
   public static class Builder
   {
      /**
       * Constructor.
       *
       * @param sequenceProvider parent file
       */
      public Builder(UniqueIdObjectSequenceProvider sequenceProvider)
      {
         m_sequenceProvider = sequenceProvider;
      }

      /**
       * Initialise the builder from an existing Currency instance.
       *
       * @param value Currency instance
       * @return builder
       */
      public Builder from(Currency value)
      {
         m_uniqueID = value.m_uniqueID;
         m_currencyID = value.m_currencyID;
         m_name = value.m_name;
         m_symbol = value.m_symbol;
         m_exchangeRate = value.m_exchangeRate;
         m_decimalSymbol = value.m_decimalSymbol;
         m_numberOfDecimalPlaces = value.m_numberOfDecimalPlaces;
         m_digitGroupingSymbol = value.m_digitGroupingSymbol;
         m_positiveCurrencyFormat = value.m_positiveCurrencyFormat;
         m_negativeCurrencyFormat = value.m_negativeCurrencyFormat;
         return this;
      }

      /**
       * Add the unique ID.
       *
       * @param value unique ID value
       * @return builder
       */
      public Builder uniqueID(Integer value)
      {
         m_uniqueID = value;
         return this;
      }

      /**
       * Add the currency ID.
       *
       * @param value currency ID value
       * @return builder
       */
      public Builder currencyID(String value)
      {
         m_currencyID = value;
         return this;
      }

      /**
       * Add the name.
       *
       * @param value name
       * @return builder
       */
      public Builder name(String value)
      {
         m_name = value;
         return this;
      }

      /**
       * Add the symbol.
       *
       * @param value symbol
       * @return builder
       */
      public Builder symbol(String value)
      {
         m_symbol = value;
         return this;
      }

      /**
       * Add the exchange rate.
       *
       * @param value exchange rate
       * @return builder
       */
      public Builder exchangeRate(Double value)
      {
         m_exchangeRate = value;
         return this;
      }

      /**
       * Add the decimal symbol.
       *
       * @param value decimal symbol
       * @return builder
       */
      public Builder decimalSymbol(String value)
      {
         m_decimalSymbol = value;
         return this;
      }

      /**
       * Add the number of decimal places.
       *
       * @param value number of decimal places
       * @return builder
       */
      public Builder numberOfDecimalPlaces(Integer value)
      {
         m_numberOfDecimalPlaces = value;
         return this;
      }

      /**
       * Add the digit grouping symbol.
       *
       * @param value digit grouping symbol
       * @return builder
       */
      public Builder digitGroupingSymbol(String value)
      {
         m_digitGroupingSymbol = value;
         return this;
      }

      /**
       * Add the positive currency format.
       *
       * @param value positive currency format
       * @return builder
       */
      public Builder positiveCurrencyFormat(String value)
      {
         m_positiveCurrencyFormat = value;
         return this;
      }

      /**
       * Add the negative currency format.
       *
       * @param value negative currency format
       * @return builder
       */
      public Builder negativeCurrencyFormat(String value)
      {
         m_negativeCurrencyFormat = value;
         return this;
      }

      /**
       * Build a Currency instance.
       *
       * @return Currency instance
       */
      public Currency build()
      {
         return new Currency(this);
      }

      private final UniqueIdObjectSequenceProvider m_sequenceProvider;
      private Integer m_uniqueID;
      private String m_currencyID;
      private String m_name;
      private String m_symbol;
      private Double m_exchangeRate;
      private String m_decimalSymbol;
      private Integer m_numberOfDecimalPlaces;
      private String m_digitGroupingSymbol;
      private String m_positiveCurrencyFormat;
      private String m_negativeCurrencyFormat;
   }
}
