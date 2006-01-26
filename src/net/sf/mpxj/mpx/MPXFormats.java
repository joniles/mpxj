/*
 * file:       MPXFormats.java
 * author:     Jon Iles
 * copyright:  (c) Tapster Rock Limited 2005
 * date:       Jan 20, 2006
 */

package net.sf.mpxj.mpx;

import java.text.DateFormat;
import java.text.NumberFormat;
import java.util.Locale;

import net.sf.mpxj.CurrencySymbolPosition;
import net.sf.mpxj.DateOrder;
import net.sf.mpxj.ProjectDateFormat;
import net.sf.mpxj.ProjectFile;
import net.sf.mpxj.ProjectHeader;
import net.sf.mpxj.ProjectTimeFormat;


/**
 * This class manages the various objects required to parse and format
 * data items in MPX files.
 */
final class MPXFormats
{
   /**
    * Constructor.
    *
    * @param locale target locale
    * @param file parent file
    */
   public MPXFormats (Locale locale, ProjectFile file)
   {
      m_locale = locale;
      m_projectFile = file;
      update();
   }

   /**
    * Called to update the cached formats when something changes.
    */
   public void update ()
   {
      ProjectHeader header = m_projectFile.getProjectHeader();
      char decimalSeparator = header.getDecimalSeparator();
      char thousandsSeparator = header.getThousandsSeparator();
      m_unitsDecimalFormat = new MPXNumberFormat("#.##", decimalSeparator, thousandsSeparator);
      m_decimalFormat = new MPXNumberFormat("0.00#", decimalSeparator, thousandsSeparator);
      m_durationDecimalFormat = new MPXNumberFormat("#.#", decimalSeparator, thousandsSeparator);
      m_percentageDecimalFormat = new MPXNumberFormat("##0.##", decimalSeparator, thousandsSeparator);
      updateCurrencyFormats(header, decimalSeparator, thousandsSeparator);
      updateDateTimeFormats(header);
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
    * Updates the date and time formats.
    *
    * @param header projects header
    */
   private void updateDateTimeFormats (ProjectHeader header)
   {
      String datePattern = "";
      String dateTimePattern= "";
      String timePattern = getTimeElement(header);

      char datesep = header.getDateSeparator();
      int dateOrderValue = header.getDateOrder().getValue();

      switch (dateOrderValue)
      {
         case DateOrder.DMY_VALUE:
         {
            datePattern="dd"+datesep+"MM"+datesep+"yy";
            break;
         }

         case DateOrder.MDY_VALUE:
         {
            datePattern="MM"+datesep+"dd"+datesep+"yy";
            break;
         }

         case DateOrder.YMD_VALUE:
         {
            datePattern="yy"+datesep+"MM"+datesep+"dd";
            break;
         }
      }

      switch (header.getDateFormat().getValue())
      {
         case ProjectDateFormat.DD_MM_YY_HH_MM_VALUE:
         {
            switch (dateOrderValue)
            {
               case DateOrder.DMY_VALUE:
               {
                  dateTimePattern="dd"+datesep+"MM"+datesep+"yy "+timePattern;
                  break;
               }

               case DateOrder.MDY_VALUE:
               {
                  dateTimePattern="MM"+datesep+"dd"+datesep+"yy "+timePattern;
                  break;
               }

               case DateOrder.YMD_VALUE:
               {
                  dateTimePattern="yy"+datesep+"MM"+datesep+"dd "+timePattern;
                  break;
               }
            }
            break;
         }

         case ProjectDateFormat.DD_MM_YY_VALUE:
         {
            switch (dateOrderValue)
            {
               case DateOrder.DMY_VALUE:
               {
                  dateTimePattern="dd"+datesep+"MM"+datesep+"yy";
                  break;
               }

               case DateOrder.MDY_VALUE:
               {
                  dateTimePattern="MM"+datesep+"dd"+datesep+"yy";
                  break;
               }

               case DateOrder.YMD_VALUE:
               {
                  dateTimePattern="yy"+datesep+"MM"+datesep+"dd";
                  break;

               }
            }
            break;
         }

         case ProjectDateFormat.DD_MMMMM_YYYY_HH_MM_VALUE:
         {
            switch (dateOrderValue)
            {
               case DateOrder.DMY_VALUE:
               {
                  dateTimePattern="dd MMMMM yyyy "+timePattern;
                  break;
               }

               case DateOrder.MDY_VALUE:
               {
                  dateTimePattern="MMMMM dd yyyy "+timePattern;
                  break;
               }

               case DateOrder.YMD_VALUE:
               {
                  dateTimePattern="yyyy MMMMM dd "+timePattern;
                  break;
               }
            }
            break;
         }

         case ProjectDateFormat.DD_MMMMM_YYYY_VALUE:
         {
            switch (dateOrderValue)
            {
               case DateOrder.DMY_VALUE:
               {
                  dateTimePattern="dd MMMMM yyyy";
                  break;
               }

               case DateOrder.MDY_VALUE:
               {
                  dateTimePattern="MMMMM dd yyyy";
                  break;
               }

               case DateOrder.YMD_VALUE:
               {
                  dateTimePattern="yyyy MMMMM dd";
                  break;
               }
            }
            break;
         }

         case ProjectDateFormat.DD_MMM_HH_MM_VALUE:
         {
            switch (dateOrderValue)
            {
               case DateOrder.DMY_VALUE:
               {
                  dateTimePattern="dd MMM "+timePattern;
                  break;
               }

               case DateOrder.MDY_VALUE:
               {
                  dateTimePattern=" MMM dd "+timePattern;
                  break;
               }
            }
            break;
         }

         case ProjectDateFormat.DD_MMM_YY_VALUE:
         {
            switch (dateOrderValue)
            {
               case DateOrder.DMY_VALUE:
               {
                  dateTimePattern="dd MMM ''yy";
                  break;
               }

               case DateOrder.MDY_VALUE:
               {
                  dateTimePattern="MMM dd ''yy";
                  break;
               }

               case DateOrder.YMD_VALUE:
               {
                  dateTimePattern="''yy MMM dd";
                  break;
               }
            }
            break;
         }

         case ProjectDateFormat.DD_MMMMM_VALUE:
         {
            switch (dateOrderValue)
            {
               case DateOrder.DMY_VALUE:
               {
                  dateTimePattern="dd MMMMM";
                  break;
               }

               case DateOrder.MDY_VALUE:
               {
                  dateTimePattern="MMMMM dd";
                  break;
               }
            }
            break;
         }

         case ProjectDateFormat.DD_MMM_VALUE:
         {
            switch (dateOrderValue)
            {
               case DateOrder.DMY_VALUE:
               {
                  dateTimePattern="dd MMM";
                  break;
               }

               case DateOrder.MDY_VALUE:
               {
                  dateTimePattern="MMM dd";
                  break;
               }
            }
            break;
         }

         case ProjectDateFormat.EEE_DD_MM_YY_HH_MM_VALUE:
         {
            switch (dateOrderValue)
            {
               case DateOrder.DMY_VALUE:
               {
                  dateTimePattern="EEE "+"dd"+datesep+"MM"+datesep+"yy "+timePattern;
                  break;
               }

               case DateOrder.MDY_VALUE:
               {
                  dateTimePattern="EEE "+"MM"+datesep+"dd"+datesep+"yy "+timePattern;
                  break;
               }

               case DateOrder.YMD_VALUE:
               {
                  dateTimePattern="EEE "+"yy"+datesep+"MM"+datesep+"dd "+timePattern;
                  break;
               }
            }
            break;
         }

         case ProjectDateFormat.EEE_DD_MM_YY_VALUE:
         {
            switch (dateOrderValue)
            {
               case DateOrder.DMY_VALUE:
               {
                  dateTimePattern="EEE dd"+datesep+"MM"+datesep+"yy";
                  break;
               }

               case DateOrder.MDY_VALUE:
               {
                  dateTimePattern="EEE MM"+datesep+"dd"+datesep+"yy";
                  break;
               }

               case DateOrder.YMD_VALUE:
               {
                  dateTimePattern="EEE yy"+datesep+"MM"+datesep+"dd";
                  break;
               }
            }
            break;
         }

         case ProjectDateFormat.EEE_DD_MMM_YY_VALUE:
         {
            switch (dateOrderValue)
            {
               case DateOrder.DMY_VALUE:
               {
                  dateTimePattern="EEE dd MMM ''yy";
                  break;
               }

               case DateOrder.MDY_VALUE:
               {
                  dateTimePattern="EEE MM dd ''yy";
                  break;
               }

               case DateOrder.YMD_VALUE:
               {
                  dateTimePattern="EEE ''yy MMM dd";
                  break;
               }
            }
            break;
         }

         case ProjectDateFormat.EEE_HH_MM_VALUE:
         {
            dateTimePattern="EEE "+timePattern;
            break;
         }

         case ProjectDateFormat.DD_MM_VALUE:
         {
            switch (dateOrderValue)
            {
               case DateOrder.DMY_VALUE:
               {
                  dateTimePattern="dd"+datesep+"MM";
                  break;
               }

               case DateOrder.MDY_VALUE:
               {
                  dateTimePattern="MM"+datesep+"dd";
                  break;
               }
            }
            break;
         }

         case ProjectDateFormat.DD_VALUE:
         {
            dateTimePattern="dd";
            break;
         }

         case ProjectDateFormat.HH_MM_VALUE:
         {
            dateTimePattern = timePattern;
            break;
         }

         case ProjectDateFormat.EEE_DD_MMM_VALUE:
         {
            switch (dateOrderValue)
            {
               case DateOrder.DMY_VALUE:
               {
                  dateTimePattern="EEE dd MMM";
                  break;
               }

               case DateOrder.MDY_VALUE:
               {
                  dateTimePattern="EEE MMM dd";
                  break;
               }
            }
            break;
         }

         case ProjectDateFormat.EEE_DD_MM_VALUE:
         {
            switch (dateOrderValue)
            {
               case DateOrder.DMY_VALUE:
               {
                  dateTimePattern="EEE dd"+datesep+"MM";
                  break;
               }

               case DateOrder.MDY_VALUE:
               {
                  dateTimePattern="EEE MM"+datesep+"dd";
                  break;
               }
            }
            break;
         }

         case ProjectDateFormat.EEE_DD_VALUE:
         {
            dateTimePattern="EEE dd";
            break;
         }

         case ProjectDateFormat.DD_WWW_VALUE:
         {
            dateTimePattern="F"+datesep+"'W'ww";
            break;
         }

         case ProjectDateFormat.DD_WWW_YY_HH_MM_VALUE:
         {
            dateTimePattern="F"+datesep+"'W'ww"+datesep+"yy "+timePattern;
            break;
         }

         case ProjectDateFormat.DD_MM_YYYY_VALUE:
         {
            switch (dateOrderValue)
            {
               case DateOrder.DMY_VALUE:
               {
                  dateTimePattern="dd"+datesep+"MM"+datesep+"yyyy";
                  break;
               }

               case DateOrder.MDY_VALUE:
               {
                  dateTimePattern="MM"+datesep+"dd"+datesep+"yyyy";
                  break;
               }

               case DateOrder.YMD_VALUE:
               {
                  dateTimePattern="yyyy"+datesep+"MM"+datesep+"dd";
                  break;
               }
            }
            break;
         }
      }

      m_dateTimeFormat.applyPattern(dateTimePattern);
      m_dateFormat.applyPattern(datePattern);
      m_timeFormat.applyPattern(timePattern);

      m_dateTimeFormat.setLocale(m_locale);
      m_dateFormat.setLocale(m_locale);
      m_timeFormat.setLocale(m_locale);
   }

   /**
    * Returns time elements considering 12/24 hour formatting.
    *
    * @param header project header
    * @return time formatting String
    */
   private String getTimeElement(ProjectHeader header)
   {
      String time;
      char timesep = header.getTimeSeparator();
      ProjectTimeFormat format = header.getTimeFormat();

      if (format == null || format.getValue() == ProjectTimeFormat.TWELVE_HOUR_VALUE)
      {
         time = "hh"+timesep+"mm a";
      }
      else
      {
         time = "HH"+timesep+"mm";
      }

      return (time);
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

   /**
    * Retrieve the date time format.
    *
    * @return date time format
    */
   public DateFormat getDateTimeFormat ()
   {
      return (m_dateTimeFormat);
   }

   /**
    * Retrieve the date format.
    *
    * @return date format
    */
   public DateFormat getDateFormat ()
   {
      return (m_dateFormat);
   }

   /**
    * Retrieve the time format.
    *
    * @return time format
    */
   public DateFormat getTimeFormat ()
   {
      return (m_timeFormat);
   }

   private Locale m_locale;
   private ProjectFile m_projectFile;
   private NumberFormat m_unitsDecimalFormat;
   private NumberFormat m_decimalFormat;
   private MPXNumberFormat m_currencyFormat = new MPXNumberFormat();
   private NumberFormat m_durationDecimalFormat;
   private NumberFormat m_percentageDecimalFormat;
   private MPXDateFormat m_dateTimeFormat = new MPXDateFormat();
   private MPXDateFormat m_dateFormat = new MPXDateFormat();
   private MPXTimeFormat m_timeFormat = new MPXTimeFormat();
}
