package net.sf.mpxj;

public class Currency implements ProjectEntityWithUniqueID
{
   private Currency(Builder builder)
   {
      m_uniqueID = builder.m_sequenceProvider.getUniqueIdObjectSequence(Currency.class).syncOrGetNext(builder.m_uniqueID);
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

   public String getCurrencyID()
   {
      return m_currencyID;
   }

   public String getName()
   {
      return m_name;
   }

   public String getSymbol()
   {
      return m_symbol;
   }

   public Double getExchangeRate()
   {
      return m_exchangeRate;
   }

   public String getDecimalSymbol()
   {
      return m_decimalSymbol;
   }

   public Integer getNumberOfDecimalPlaces()
   {
      return m_numberOfDecimalPlaces;
   }

   public String getDigitGroupingSymbol()
   {
      return m_digitGroupingSymbol;
   }

   public String getPositiveCurrencyFormat()
   {
      return m_positiveCurrencyFormat;
   }

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
       * Initialise the builder from an existing ExpenseCategory instance.
       *
       * @param value ExpenseCategory instance
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
