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


/**
 * This class is used to represent the content of the currency options
 * record from an MPX file.
 */
public class CurrencySettings extends MPXRecord
{
   /**
    * Default constructor.
    *
    * @param file the parent file to which this record belongs.
    */
   CurrencySettings (MPXFile file)
   {
      super (file);

      m_update = false;
      setCurrencySymbol("$");
      setSymbolPosition(new Byte((byte)1));
      setCurrencyDigits(new Byte((byte)2));
      setThousandsSeparator(new Character (','));
      setDecimalSeparator(new Character ('.'));
      m_update = true;

      updateFormats ();
   }

   /**
    * This method is used to update a currency settings instance with
    * new values read from an MPX file.
    */
   void update (Record record)
      throws MPXException
   {
      m_update = false;
      setCurrencySymbol (record.getString(0));
      setSymbolPosition (record.getByte(1));
      setCurrencyDigits (record.getByte(2));
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
   protected void put (Integer key, Object value)
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
   public void setSymbolPosition (Byte posn)
   {
      put (SYMBOL_POSITION,posn);
   }

   /**
    * Retrieves a constant representing the position of the currency symbol.
    *
    * @return position
    */
   public byte getSymbolPosition ()
   {
      return (((Byte)get(SYMBOL_POSITION)).byteValue());
   }

   /**
    * Sets no of currency digits.
    *
    * @param currDigs Available values, 0,1,2
    */
   public void setCurrencyDigits (Byte currDigs)
   {
      put (CURRENCY_DIGITS,currDigs);
   }

   /**
    * Gets no of currency digits.
    *
    * @return Available values, 0,1,2
    */
   public byte getCurrencyDigits ()
   {
      return (((Byte)get(CURRENCY_DIGITS)).byteValue());
   }

   /**
    * Sets the thousands separator. Normally ','
    *
    * @param sep character
    */
   public void setThousandsSeparator (Character sep)
   {
      put (THOUSANDS_SEPARATOR, sep);
   }

   /**
    * Gets the thousands separator. Normally ','
    *
    * @return character
    */
   public char getThousandsSeparator ()
   {
      return (((Character)get(THOUSANDS_SEPARATOR)).charValue());
   }

   /**
    * Sets the decimal separator. Normally '.'
    *
    * @param decSep character
    */
   public void setDecimalSeparator (Character decSep)
   {
      put (DECIMAL_SEPARATOR, decSep);
   }

   /**
    * Gets the decimal separator. Normally '.'
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
         MPXFile parent = getParent();
         MPXNumberFormat currencyFormat = parent.getCurrencyFormat();
         String prefix = "";
         String suffix = "";

         switch (getSymbolPosition())
         {
            case SYMBOLPOS_AFTER:
            {
               suffix = getCurrencySymbol();
               break;
            }

            case SYMBOLPOS_BEFORE:
            {
               prefix = getCurrencySymbol();
               break;
            }

            case SYMBOLPOS_AFTER_W_SPACE:
            {
               suffix = " " + getCurrencySymbol();
               break;
            }

            case SYMBOLPOS_BEFORE_W_SPACE:
            {
               prefix = getCurrencySymbol() + " ";
               break;
            }
         }

         StringBuffer pattern = new StringBuffer(prefix);
         pattern.append("#");
         if (parent.getIgnoreCurrencyThousandsSeparator() == false)
         {
            pattern.append(getThousandsSeparator());
         }
         pattern.append("##0");

         pattern.append(getDecimalSeparator());

         int digits = getCurrencyDigits();
         for(int i = 0 ; i < digits ; i++)
         {
            pattern.append("0");
         }
         pattern.append(suffix);

         parent.getCurrencyFormat().applyPattern(pattern.toString());
      }
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
    * can be automatically updated.
    */
   private boolean m_update = false;

   /**
    * Represents a constant from Symbol Position field
    */
   private static final byte SYMBOLPOS_AFTER = 0;

   /**
    * Represents a constant from Symbol Position field
    */
   private static final byte SYMBOLPOS_BEFORE = 1;

   /**
    * Represents a constant from Symbol Position field
    */
   private static final byte SYMBOLPOS_AFTER_W_SPACE = 2;

   /**
    * Represents a constant from Symbol Position field
    */
   private static final byte SYMBOLPOS_BEFORE_W_SPACE = 3;

   /**
    * Constant referring to Currency Symbol field.
    */
   private static final Integer CURRENCY_SYMBOL = new Integer(0);

   /**
    * Constant referring to Symbol Position field.
    */
   private static final Integer SYMBOL_POSITION = new Integer(1);

   /**
    * Constant referring to Currency Digits field.
    */
   private static final Integer CURRENCY_DIGITS = new Integer(2);

   /**
    * Constant referring to Thousands Separator field.
    */
   private static final Integer THOUSANDS_SEPARATOR = new Integer(3);

   /**
    * Constant referring to Decimal Separator field.
    */
   private static final Integer DECIMAL_SEPARATOR = new Integer(4);

   /**
    * Constant containing the record number associated with this record.
    */
   public static final int RECORD_NUMBER = 10;
}
