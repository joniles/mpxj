/*
 * file:       CurrencySettings.java
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
 * This class is used to represent the content of the currency options
 * record from an MPX file.
 */
public final class CurrencySettings extends MPXRecord
{
   /**
    * Default constructor.
    *
    * @param file the parent file to which this record belongs.
    */
   CurrencySettings (MPXFile file)
   {
      super (file, MAX_FIELDS);
      setLocale (file.getLocale());
   }

   /**
    * This method is calkled when the locale of the parent file is updated.
    * It resets the locale specific currency attributes to the default values
    * for the new locale.
    *
    * @param locale new locale
    */
   void setLocale (Locale locale)
   {
      m_update = false;
      setCurrencySymbol(LocaleData.getString(locale, LocaleData.CURRENCY_SYMBOL));
      setSymbolPosition(LocaleData.getInteger(locale, LocaleData.CURRENCY_SYMBOL_POSITION));
      setCurrencyDigits(LocaleData.getInteger(locale, LocaleData.CURRENCY_DIGITS));
      setThousandsSeparator(LocaleData.getChar(locale, LocaleData.CURRENCY_THOUSANDS_SEPARATOR));
      setDecimalSeparator(LocaleData.getChar(locale, LocaleData.CURRENCY_DECIMAL_SEPARATOR));
      m_update = true;
      updateFormats ();
   }

   /**
    * This method is used to update a currency settings instance with
    * new values read from an MPX file.
    *
    * @param record record containing the data for  this object.
    */
   void update (Record record)
   {
      m_update = false;
      setCurrencySymbol (record.getString(0));
      setSymbolPosition (record.getInteger(1));
      setCurrencyDigits (record.getInteger(2));
      setThousandsSeparator (record.getCharacter(3));
      setDecimalSeparator (record.getCharacter(4));
      m_update = true;

      updateFormats ();
   }

   /**
    * This method overrides the put method defined in MPXRecord.
    * It allows the formats to be updated immediately whenever a change
    * is made to any of the currency settings.
    *
    * @param key Field to be added/updated.
    * @param value new value for field.
    */
   protected void put (int key, Object value)
   {
      super.put (key, value);
      updateFormats ();
   }

   /**
    * This method overrides the put method defined in MPXRecord.
    * It allows the formats to be updated immediately whenever a change
    * is made to any of the currency settings.
    *
    * @param key Field to be added/updated.
    * @param value new value for field.
    */
   protected void put (int key, int value)
   {
      super.put (key, value);
      updateFormats ();
   }

   /**
    * This method overrides the put method defined in MPXRecord.
    * It allows the formats to be updated immediately whenever a change
    * is made to any of the currency settings.
    *
    * @param key Field to be added/updated.
    * @param value new value for field.
    */
   protected void putChar (int key, char value)
   {
      super.put (key, value);
      updateFormats ();
   }

   /**
    * Sets currency symbol ie $, £, DM
    *
    * @param symbol ie $, £, DM
    */
   public void setCurrencySymbol (String symbol)
   {
      put (CURRENCY_SYMBOL, symbol);
   }

   /**
    * Gets currency symbol ie $, £, DM
    *
    * @return ie $, £, DM
    */
   public String getCurrencySymbol ()
   {
      return ((String)get(CURRENCY_SYMBOL));
   }

   /**
    * Sets the position of the currency symbol.
    *
    * Permissable value are as follows:
    *
    * 0 = after
    * 1 = before
    * 2 = after with a space
    * 3 = before with a space
    *
    * The SYMBOLPOS_* are used a enumerated vaules for this parameter.
    *
    * @param posn currency symbol position.
    */
   public void setSymbolPosition (int posn)
   {
      put (SYMBOL_POSITION, posn);
   }

   /**
    * Sets the position of the currency symbol.
    *
    * Permissable value are as follows:
    *
    * 0 = after
    * 1 = before
    * 2 = after with a space
    * 3 = before with a space
    *
    * The SYMBOLPOS_* are used a enumerated vaules for this parameter.
    *
    * @param posn currency symbol position.
    */
   public void setSymbolPosition (Integer posn)
   {
      put (SYMBOL_POSITION, posn);
   }

   /**
    * Retrieves a constant representing the position of the currency symbol.
    *
    * @return position
    */
   public int getSymbolPositionValue ()
   {
      return (getIntValue (SYMBOL_POSITION));
   }

   /**
    * Retrieves a constant representing the position of the currency symbol.
    *
    * @return position
    */
   public Integer getSymbolPosition ()
   {
      return ((Integer)get (SYMBOL_POSITION));
   }

   /**
    * Sets no of currency digits.
    *
    * @param currDigs Available values, 0,1,2
    */
   public void setCurrencyDigits (int currDigs)
   {
      put (CURRENCY_DIGITS, currDigs);
   }

   /**
    * Sets no of currency digits.
    *
    * @param currDigs Available values, 0,1,2
    */
   public void setCurrencyDigits (Integer currDigs)
   {
      put (CURRENCY_DIGITS, currDigs);
   }

   /**
    * Gets no of currency digits.
    *
    * @return Available values, 0,1,2
    */
   public int getCurrencyDigitsValue ()
   {
      return (getIntValue (CURRENCY_DIGITS));
   }

   /**
    * Gets no of currency digits.
    *
    * @return Available values, 0,1,2
    */
   public Integer getCurrencyDigits ()
   {
      return ((Integer)get (CURRENCY_DIGITS));
   }

   /**
    * Sets the thousands separator.
    * Note that this separator defines the thousands separator for all decimal
    * numbers that appear in the MPX file.
    *
    * @param sep character
    */
   public void setThousandsSeparator (char sep)
   {
      putChar (THOUSANDS_SEPARATOR, sep);
      if (getParentFile().getThousandsSeparator() != sep)
      {
         getParentFile().setThousandsSeparator(sep);
      }
   }

   /**
    * Sets the thousands separator.
    * Note that this separator defines the thousands separator for all decimal
    * numbers that appear in the MPX file.
    *
    * @param sep character
    */
   public void setThousandsSeparator (Character sep)
   {
      if (sep != null)
      {
         setThousandsSeparator (sep.charValue());
      }
   }

   /**
    * Gets the thousands separator.
    * Note that this separator defines the thousands separator for all decimal
    * numbers that appear in the MPX file.
    *
    * @return character
    */
   public char getThousandsSeparator ()
   {
      return (getCharValue(THOUSANDS_SEPARATOR));
   }

   /**
    * Sets the decimal separator.
    * Note that this separator defines the decimal separator for all decimal
    * numbers that appear in the MPX file.
    *
    * @param decSep character
    */
   public void setDecimalSeparator (char decSep)
   {
      putChar (DECIMAL_SEPARATOR, decSep);
      if (getParentFile().getDecimalSeparator() != decSep)
      {
         getParentFile().setDecimalSeparator(decSep);
      }
   }

   /**
    * Sets the decimal separator.
    * Note that this separator defines the decimal separator for all decimal
    * numbers that appear in the MPX file.
    *
    * @param decSep character
    */
   public void setDecimalSeparator (Character decSep)
   {
      if (decSep != null)
      {
         setDecimalSeparator (decSep.charValue());
      }
   }

   /**
    * Gets the decimal separator.
    * Note that this separator defines the decimal separator for all decimal
    * numbers that appear in the MPX file.
    *
    * @return character
    */
   public char getDecimalSeparator ()
   {
      return (((Character)get(DECIMAL_SEPARATOR)).charValue());
   }

   /**
    * This method updates the formatters used to control the currency
    * formatting.
    */
   private void updateFormats ()
   {
      if (m_update == true)
      {
         MPXFile parent = getParentFile();
         String prefix = "";
         String suffix = "";
         String currencySymbol = quoteFormatCharacters (getCurrencySymbol());

         switch (getSymbolPositionValue())
         {
            case SYMBOLPOS_AFTER:
            {
               suffix = currencySymbol;
               break;
            }

            case SYMBOLPOS_BEFORE:
            {
               prefix = currencySymbol;
               break;
            }

            case SYMBOLPOS_AFTER_WITH_SPACE:
            {
               suffix = " " + currencySymbol;
               break;
            }

            case SYMBOLPOS_BEFORE_WITH_SPACE:
            {
               prefix = currencySymbol + " ";
               break;
            }
         }

         StringBuffer pattern = new StringBuffer(prefix);
         pattern.append("#");
         if (parent.getIgnoreThousandsSeparator() == false)
         {
            pattern.append(',');
         }
         pattern.append("##0");


         int digits = getCurrencyDigitsValue();
         if (digits > 0)
         {
            pattern.append('.');
            for(int i = 0 ; i < digits ; i++)
            {
               pattern.append("0");
            }
         }

         pattern.append(suffix);

         parent.getCurrencyFormat().applyPattern(pattern.toString(), getDecimalSeparator(), getThousandsSeparator());
      }
   }

   /**
    * This method is used to quote any special characters that appear in
    * literal text that is required as part of the currency format.
    *
    * @param literal Literal text
    * @return literal text with special characters in quotes
    */
   private String quoteFormatCharacters (String literal)
   {
      StringBuffer sb = new StringBuffer ();
      int length = literal.length();
      char c;

      for (int loop=0; loop <length; loop++)
      {
         c = literal.charAt(loop);
         switch (c)
         {
            case '0':
            case '#':
            case '.':
            case '-':
            case ',':
            case 'E':
            case ';':
            case '%':
            {
               sb.append ("'");
               sb.append (c);
               sb.append ("'");
               break;
            }

            default:
            {
               sb.append (c);
               break;
            }
         }
      }

      return (sb.toString());
   }

   /**
    * This method generates a string in MPX format representing the
    * contents of this record.
    *
    * @return string containing the data for this record in MPX format.
    */
   public String toString ()
   {
      return (toString (RECORD_NUMBER));
   }

   /**
    * flag used to indicate whether the currency format
    * can be automatically updated. The default value for this
    * flag is false.
    */
   private boolean m_update;

   /**
    * Represents a constant from Symbol Position field
    */
   public static final int SYMBOLPOS_AFTER = 0;

   /**
    * Represents a constant from Symbol Position field
    */
   public static final int SYMBOLPOS_BEFORE = 1;

   /**
    * Represents a constant from Symbol Position field
    */
   public static final int SYMBOLPOS_AFTER_WITH_SPACE = 2;

   /**
    * Represents a constant from Symbol Position field
    */
   public static final int SYMBOLPOS_BEFORE_WITH_SPACE = 3;


   /**
    * Constant referring to Currency Symbol field.
    */
   private static final int CURRENCY_SYMBOL = 0;

   /**
    * Constant referring to Symbol Position field.
    */
   private static final int SYMBOL_POSITION = 1;

   /**
    * Constant referring to Currency Digits field.
    */
   private static final int CURRENCY_DIGITS = 2;

   /**
    * Constant referring to Thousands Separator field.
    */
   private static final int THOUSANDS_SEPARATOR = 3;

   /**
    * Constant referring to Decimal Separator field.
    */
   private static final int DECIMAL_SEPARATOR = 4;

   /**
    * Maximum number of fields in this record.
    */
   private static final int MAX_FIELDS = 5;

   /**
    * Constant containing the record number associated with this record.
    */
   static final int RECORD_NUMBER = 10;
}
