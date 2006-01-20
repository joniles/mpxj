/*
 * file:       MPXFormats.java
 * author:     Jon Iles
 * copyright:  (c) Tapster Rock Limited 2005
 * date:       Jan 20, 2006
 */
 
package com.tapsterrock.mpx;

import java.text.NumberFormat;

/**
 * This class manages the various objects required to parse and format
 * data items in MPX files.
 */
public final class MPXFormats
{
   /**
    * Constructor.
    * 
    * @param file parent file
    */
   public MPXFormats (ProjectFile file)
   {
      m_projectFile = file;
      update();
   }
   
   /**
    * Called to update the cached formats when something changes.
    */
   public void update ()
   {
      // @todo sort out this separator caching!
      ProjectHeader header = m_projectFile.getProjectHeader();      
      char decimalSeparator = header.getDecimalSeparator();
      char thousandsSeparator = header.getThousandsSeparator();
      m_unitsDecimalFormat = new MPXNumberFormat("#.##", decimalSeparator, thousandsSeparator);
      m_decimalFormat = new MPXNumberFormat("0.00#", decimalSeparator, thousandsSeparator);
      m_durationDecimalFormat = new MPXNumberFormat(MPXDuration.DECIMAL_FORMAT_STRING, decimalSeparator, thousandsSeparator);
      m_percentageDecimalFormat = new MPXNumberFormat("##0.##", decimalSeparator, thousandsSeparator);
      updateCurrencyFormats(header, decimalSeparator, thousandsSeparator);
   }
   
   /**
    * Update the currency format.
    * 
    * @param header project header
    * @param decimalSeparator decimal separator
    * @param thousandsSeparator thousands separator
    */
   private void updateCurrencyFormats (ProjectHeader header, char decimalSeparator, char thousandsSeparator)
   {      
      String prefix = "";
      String suffix = "";
      String currencySymbol = quoteFormatCharacters (header.getCurrencySymbol());

      switch (header.getSymbolPosition().getValue())
      {
         case CurrencySymbolPosition.AFTER_VALUE:
         {
            suffix = currencySymbol;
            break;
         }

         case CurrencySymbolPosition.BEFORE_VALUE:
         {
            prefix = currencySymbol;
            break;
         }

         case CurrencySymbolPosition.AFTER_WITH_SPACE_VALUE:
         {
            suffix = " " + currencySymbol;
            break;
         }

         case CurrencySymbolPosition.BEFORE_WITH_SPACE_VALUE:
         {
            prefix = currencySymbol + " ";
            break;
         }
      }

      StringBuffer pattern = new StringBuffer(prefix);
      pattern.append("#0");

      int digits = header.getCurrencyDigits().intValue();
      if (digits > 0)
      {
         pattern.append('.');
         for(int i = 0 ; i < digits ; i++)
         {
            pattern.append("0");
         }
      }

      pattern.append(suffix);

      String primaryPattern = pattern.toString();
      
      String[] alternativePatterns = new String[7];
      alternativePatterns[0] = primaryPattern + ";(" + primaryPattern + ")";
      pattern.insert(prefix.length(), "#,#");
      String secondaryPattern = pattern.toString();
      alternativePatterns[1] = secondaryPattern;
      alternativePatterns[2] = secondaryPattern + ";(" + secondaryPattern + ")";

      pattern.setLength(0);
      pattern.append("#0");

      if (digits > 0)
      {
         pattern.append('.');
         for(int i = 0 ; i < digits ; i++)
         {
            pattern.append("0");
         }
      }
      
      String noSymbolPrimaryPattern = pattern.toString();
      alternativePatterns[3] = noSymbolPrimaryPattern;
      alternativePatterns[4] = noSymbolPrimaryPattern + ";(" + noSymbolPrimaryPattern + ")";
      pattern.insert(0, "#,#");
      String noSymbolSecondaryPattern = pattern.toString();
      alternativePatterns[5] = noSymbolSecondaryPattern;
      alternativePatterns[6] = noSymbolSecondaryPattern + ";(" + noSymbolSecondaryPattern + ")";

      m_currencyFormat.applyPattern(primaryPattern, alternativePatterns, decimalSeparator, thousandsSeparator);      
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
    * Retrieve the units decimal format.
    * 
    * @return units decimal format
    */
   public NumberFormat getUnitsDecimalFormat ()
   {
      return (m_unitsDecimalFormat);
   }
   
   /**
    * Retrieve the decimal format.
    * 
    * @return decimal format
    */
   public NumberFormat getDecimalFormat ()
   {
      return (m_decimalFormat);
   }

   /**
    * Retrieve the currency format.
    * 
    * @return currency format
    */
   public NumberFormat getCurrencyFormat ()
   {
      return (m_currencyFormat);
   }

   /**
    * Retrieve the duration decimal format.
    * 
    * @return duration decimal format
    */
   public NumberFormat getDurationDecimalFormat ()
   {
      return (m_durationDecimalFormat);
   }
   
   /**
    * Retrieve the percentage decimal format.
    * 
    * @return percentage decimal format
    */
   public NumberFormat getPercentageDecimalFormat ()
   {
      return (m_percentageDecimalFormat);
   }
   
   private ProjectFile m_projectFile;
   private NumberFormat m_unitsDecimalFormat;
   private NumberFormat m_decimalFormat;
   private MPXNumberFormat m_currencyFormat = new MPXNumberFormat();
   private NumberFormat m_durationDecimalFormat;
   private NumberFormat m_percentageDecimalFormat;
}
