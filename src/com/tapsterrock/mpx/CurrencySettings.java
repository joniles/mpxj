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
      super (file, 0);
      setLocale (file.getLocale());
   }

   /**
    * This method is called when the locale of the parent file is updated.
    * It resets the locale specific currency attributes to the default values
    * for the new locale.
    *
    * @param locale new locale
    */
   void setLocale (Locale locale)
   {
      m_updateCurrencyFormat = false;
      setCurrencySymbol(LocaleData.getString(locale, LocaleData.CURRENCY_SYMBOL));
      setSymbolPosition(LocaleData.getInteger(locale, LocaleData.CURRENCY_SYMBOL_POSITION));
      setCurrencyDigits(LocaleData.getInteger(locale, LocaleData.CURRENCY_DIGITS));
      setThousandsSeparator(LocaleData.getChar(locale, LocaleData.CURRENCY_THOUSANDS_SEPARATOR));
      setDecimalSeparator(LocaleData.getChar(locale, LocaleData.CURRENCY_DECIMAL_SEPARATOR));
      m_updateCurrencyFormat = true;
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
      m_updateCurrencyFormat = false;
      setCurrencySymbol (record.getString(0));
      setSymbolPosition (record.getInteger(1));
      setCurrencyDigits (record.getInteger(2));
      setThousandsSeparator (record.getCharacter(3));
      setDecimalSeparator (record.getCharacter(4));
      m_updateCurrencyFormat = true;

      updateFormats ();
   }

   /**
    * Sets currency symbol ie $, £, DM
    *
    * @param symbol ie $, £, DM
    */
   public void setCurrencySymbol (String symbol)
   {
      m_currencySymbol = symbol;
      updateFormats();
   }

   /**
    * Gets currency symbol ie $, £, DM
    *
    * @return ie $, £, DM
    */
   public String getCurrencySymbol ()
   {
      return (m_currencySymbol);
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
      m_symbolPosition = posn;
      updateFormats();
   }

   /**
    * Retrieves a constant representing the position of the currency symbol.
    *
    * @return position
    */
   public Integer getSymbolPosition ()
   {
      return (m_symbolPosition);
   }

   /**
    * Sets no of currency digits.
    *
    * @param currDigs Available values, 0,1,2
    */
   public void setCurrencyDigits (Integer currDigs)
   {
      m_currencyDigits = currDigs;
      updateFormats();
   }

   /**
    * Gets no of currency digits.
    *
    * @return Available values, 0,1,2
    */
   public Integer getCurrencyDigits ()
   {
      return (m_currencyDigits);
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
      m_thousandsSeparator = sep;
      updateFormats();
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
   private void setThousandsSeparator (Character sep)
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
      return (m_thousandsSeparator);
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
      m_decimalSeparator = decSep;
      updateFormats();
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
   private void setDecimalSeparator (Character decSep)
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
      return (m_decimalSeparator);
   }

   /**
    * This method updates the formatters used to control the currency
    * formatting.
    */
   private void updateFormats ()
   {
      if (m_updateCurrencyFormat == true)
      {
         MPXFile parent = getParentFile();
         String prefix = "";
         String suffix = "";
         String currencySymbol = quoteFormatCharacters (getCurrencySymbol());

         switch (getSymbolPosition().intValue())
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


         int digits = getCurrencyDigits().intValue();
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
      StringBuffer buffer = new StringBuffer ();
      char delimiter = getParentFile().getDelimiter();

      buffer.append (RECORD_NUMBER);
      buffer.append (delimiter);
      buffer.append(format(delimiter, getCurrencySymbol()));
      buffer.append (delimiter);
      buffer.append(format(delimiter, getSymbolPosition()));
      buffer.append (delimiter);
      buffer.append(format(delimiter, getCurrencyDigits()));
      buffer.append (delimiter);
      buffer.append(format(delimiter, new Character(getThousandsSeparator())));
      buffer.append (delimiter);
      buffer.append(format(delimiter, new Character(getDecimalSeparator())));
      stripTrailingDelimiters(buffer, delimiter);
      buffer.append (MPXFile.EOL);
                  
      return (buffer.toString());      
   }

   private String m_currencySymbol;
   private Integer m_symbolPosition;
   private Integer m_currencyDigits;
   private char m_thousandsSeparator;
   private char m_decimalSeparator;
   
   /**
    * flag used to indicate whether the currency format
    * can be automatically updated. The default value for this
    * flag is false.
    */
   private boolean m_updateCurrencyFormat;

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
    * Constant containing the record number associated with this record.
    */
   static final int RECORD_NUMBER = 10;
}
