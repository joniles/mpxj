/*
 * file:       MPXJFormats.java
 * author:     Jon Iles
 * copyright:  (c) Packwood Software 2006
 * date:       Jan 20, 2006
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

package net.sf.mpxj.utility;

import java.text.DateFormat;
import java.text.NumberFormat;
import java.util.Locale;

import net.sf.mpxj.DateOrder;
import net.sf.mpxj.ProjectFile;
import net.sf.mpxj.ProjectHeader;
import net.sf.mpxj.ProjectTimeFormat;

/**
 * This class manages the various objects required to parse and format
 * data items in MPX files.
 */
public final class MPXJFormats
{
   /**
    * Constructor.
    *
    * @param locale target locale
    * @param nullText locale specific text to represent a value which has not been set, normally "NA"
    * @param file parent file
    */
   public MPXJFormats(Locale locale, String nullText, ProjectFile file)
   {
      m_locale = locale;
      m_nullText = nullText;
      m_projectFile = file;
      update();
   }

   /**
    * Called to update the cached formats when something changes.
    */
   public void update()
   {
      ProjectHeader header = m_projectFile.getProjectHeader();
      char decimalSeparator = header.getDecimalSeparator();
      char thousandsSeparator = header.getThousandsSeparator();
      m_unitsDecimalFormat.applyPattern("#.##", null, decimalSeparator, thousandsSeparator);
      m_decimalFormat.applyPattern("0.00#", null, decimalSeparator, thousandsSeparator);
      m_durationDecimalFormat.applyPattern("#.##", null, decimalSeparator, thousandsSeparator);
      m_percentageDecimalFormat.applyPattern("##0.##", null, decimalSeparator, thousandsSeparator);
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
   private void updateCurrencyFormats(ProjectHeader header, char decimalSeparator, char thousandsSeparator)
   {
      String prefix = "";
      String suffix = "";
      String currencySymbol = quoteFormatCharacters(header.getCurrencySymbol());

      switch (header.getSymbolPosition())
      {
         case AFTER :
         {
            suffix = currencySymbol;
            break;
         }

         case BEFORE :
         {
            prefix = currencySymbol;
            break;
         }

         case AFTER_WITH_SPACE :
         {
            suffix = " " + currencySymbol;
            break;
         }

         case BEFORE_WITH_SPACE :
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
         for (int i = 0; i < digits; i++)
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
         for (int i = 0; i < digits; i++)
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
   private String quoteFormatCharacters(String literal)
   {
      StringBuffer sb = new StringBuffer();
      int length = literal.length();
      char c;

      for (int loop = 0; loop < length; loop++)
      {
         c = literal.charAt(loop);
         switch (c)
         {
            case '0' :
            case '#' :
            case '.' :
            case '-' :
            case ',' :
            case 'E' :
            case ';' :
            case '%' :
            {
               sb.append("'");
               sb.append(c);
               sb.append("'");
               break;
            }

            default :
            {
               sb.append(c);
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
   private void updateDateTimeFormats(ProjectHeader header)
   {
      String datePattern = "";
      String dateTimePattern = "";
      String timePattern = getTimeElement(header);

      char datesep = header.getDateSeparator();
      DateOrder dateOrder = header.getDateOrder();

      switch (dateOrder)
      {
         case DMY :
         {
            datePattern = "dd" + datesep + "MM" + datesep + "yy";
            break;
         }

         case MDY :
         {
            datePattern = "MM" + datesep + "dd" + datesep + "yy";
            break;
         }

         case YMD :
         {
            datePattern = "yy" + datesep + "MM" + datesep + "dd";
            break;
         }
      }

      switch (header.getDateFormat())
      {
         case DD_MM_YY_HH_MM :
         {
            switch (dateOrder)
            {
               case DMY :
               {
                  dateTimePattern = "dd" + datesep + "MM" + datesep + "yy " + timePattern;
                  break;
               }

               case MDY :
               {
                  dateTimePattern = "MM" + datesep + "dd" + datesep + "yy " + timePattern;
                  break;
               }

               case YMD :
               {
                  dateTimePattern = "yy" + datesep + "MM" + datesep + "dd " + timePattern;
                  break;
               }
            }
            break;
         }

         case DD_MM_YY :
         {
            switch (dateOrder)
            {
               case DMY :
               {
                  dateTimePattern = "dd" + datesep + "MM" + datesep + "yy";
                  break;
               }

               case MDY :
               {
                  dateTimePattern = "MM" + datesep + "dd" + datesep + "yy";
                  break;
               }

               case YMD :
               {
                  dateTimePattern = "yy" + datesep + "MM" + datesep + "dd";
                  break;

               }
            }
            break;
         }

         case DD_MMMMM_YYYY_HH_MM :
         {
            switch (dateOrder)
            {
               case DMY :
               {
                  dateTimePattern = "dd MMMMM yyyy " + timePattern;
                  break;
               }

               case MDY :
               {
                  dateTimePattern = "MMMMM dd yyyy " + timePattern;
                  break;
               }

               case YMD :
               {
                  dateTimePattern = "yyyy MMMMM dd " + timePattern;
                  break;
               }
            }
            break;
         }

         case DD_MMMMM_YYYY :
         {
            switch (dateOrder)
            {
               case DMY :
               {
                  dateTimePattern = "dd MMMMM yyyy";
                  break;
               }

               case MDY :
               {
                  dateTimePattern = "MMMMM dd yyyy";
                  break;
               }

               case YMD :
               {
                  dateTimePattern = "yyyy MMMMM dd";
                  break;
               }
            }
            break;
         }

         case DD_MMM_HH_MM :
         {
            switch (dateOrder)
            {
               case DMY :
               {
                  dateTimePattern = "dd MMM " + timePattern;
                  break;
               }

               case YMD :
               case MDY :
               {
                  dateTimePattern = " MMM dd " + timePattern;
                  break;
               }
            }
            break;
         }

         case DD_MMM_YY :
         {
            switch (dateOrder)
            {
               case DMY :
               {
                  dateTimePattern = "dd MMM ''yy";
                  break;
               }

               case MDY :
               {
                  dateTimePattern = "MMM dd ''yy";
                  break;
               }

               case YMD :
               {
                  dateTimePattern = "''yy MMM dd";
                  break;
               }
            }
            break;
         }

         case DD_MMMMM :
         {
            switch (dateOrder)
            {
               case DMY :
               {
                  dateTimePattern = "dd MMMMM";
                  break;
               }

               case YMD :
               case MDY :
               {
                  dateTimePattern = "MMMMM dd";
                  break;
               }
            }
            break;
         }

         case DD_MMM :
         {
            switch (dateOrder)
            {
               case DMY :
               {
                  dateTimePattern = "dd MMM";
                  break;
               }

               case YMD :
               case MDY :
               {
                  dateTimePattern = "MMM dd";
                  break;
               }
            }
            break;
         }

         case EEE_DD_MM_YY_HH_MM :
         {
            switch (dateOrder)
            {
               case DMY :
               {
                  dateTimePattern = "EEE " + "dd" + datesep + "MM" + datesep + "yy " + timePattern;
                  break;
               }

               case MDY :
               {
                  dateTimePattern = "EEE " + "MM" + datesep + "dd" + datesep + "yy " + timePattern;
                  break;
               }

               case YMD :
               {
                  dateTimePattern = "EEE " + "yy" + datesep + "MM" + datesep + "dd " + timePattern;
                  break;
               }
            }
            break;
         }

         case EEE_DD_MM_YY :
         {
            switch (dateOrder)
            {
               case DMY :
               {
                  dateTimePattern = "EEE dd" + datesep + "MM" + datesep + "yy";
                  break;
               }

               case MDY :
               {
                  dateTimePattern = "EEE MM" + datesep + "dd" + datesep + "yy";
                  break;
               }

               case YMD :
               {
                  dateTimePattern = "EEE yy" + datesep + "MM" + datesep + "dd";
                  break;
               }
            }
            break;
         }

         case EEE_DD_MMM_YY :
         {
            switch (dateOrder)
            {
               case DMY :
               {
                  dateTimePattern = "EEE dd MMM ''yy";
                  break;
               }

               case MDY :
               {
                  dateTimePattern = "EEE MM dd ''yy";
                  break;
               }

               case YMD :
               {
                  dateTimePattern = "EEE ''yy MMM dd";
                  break;
               }
            }
            break;
         }

         case EEE_HH_MM :
         {
            dateTimePattern = "EEE " + timePattern;
            break;
         }

         case DD_MM :
         {
            switch (dateOrder)
            {
               case DMY :
               {
                  dateTimePattern = "dd" + datesep + "MM";
                  break;
               }

               case YMD :
               case MDY :
               {
                  dateTimePattern = "MM" + datesep + "dd";
                  break;
               }
            }
            break;
         }

         case DD :
         {
            dateTimePattern = "dd";
            break;
         }

         case HH_MM :
         {
            dateTimePattern = timePattern;
            break;
         }

         case EEE_DD_MMM :
         {
            switch (dateOrder)
            {
               case DMY :
               {
                  dateTimePattern = "EEE dd MMM";
                  break;
               }

               case YMD :
               case MDY :
               {
                  dateTimePattern = "EEE MMM dd";
                  break;
               }
            }
            break;
         }

         case EEE_DD_MM :
         {
            switch (dateOrder)
            {
               case DMY :
               {
                  dateTimePattern = "EEE dd" + datesep + "MM";
                  break;
               }

               case YMD :
               case MDY :
               {
                  dateTimePattern = "EEE MM" + datesep + "dd";
                  break;
               }
            }
            break;
         }

         case EEE_DD :
         {
            dateTimePattern = "EEE dd";
            break;
         }

         case DD_WWW :
         {
            dateTimePattern = "F" + datesep + "'W'ww";
            break;
         }

         case DD_WWW_YY_HH_MM :
         {
            dateTimePattern = "F" + datesep + "'W'ww" + datesep + "yy " + timePattern;
            break;
         }

         case DD_MM_YYYY :
         {
            switch (dateOrder)
            {
               case DMY :
               {
                  dateTimePattern = "dd" + datesep + "MM" + datesep + "yyyy";
                  break;
               }

               case MDY :
               {
                  dateTimePattern = "MM" + datesep + "dd" + datesep + "yyyy";
                  break;
               }

               case YMD :
               {
                  dateTimePattern = "yyyy" + datesep + "MM" + datesep + "dd";
                  break;
               }
            }
            break;
         }
      }

      m_dateTimeFormat.applyPattern(dateTimePattern);
      m_dateFormat.applyPattern(datePattern);
      m_timeFormat.applyPattern(timePattern);

      m_dateTimeFormat.setLocale(m_locale, m_nullText);
      m_dateFormat.setLocale(m_locale, m_nullText);
      m_timeFormat.setNullText(m_nullText);

      m_dateTimeFormat.setAmPmText(header.getAMText(), header.getPMText());
      m_timeFormat.setAmPmText(header.getAMText(), header.getPMText());
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

      if (format == null || format == ProjectTimeFormat.TWELVE_HOUR)
      {
         time = "hh" + timesep + "mm a";
      }
      else
      {
         time = "HH" + timesep + "mm";
      }

      return (time);
   }

   /**
    * Retrieve the units decimal format.
    *
    * @return units decimal format
    */
   public NumberFormat getUnitsDecimalFormat()
   {
      return (m_unitsDecimalFormat);
   }

   /**
    * Retrieve the decimal format.
    *
    * @return decimal format
    */
   public NumberFormat getDecimalFormat()
   {
      return (m_decimalFormat);
   }

   /**
    * Retrieve the currency format.
    *
    * @return currency format
    */
   public NumberFormat getCurrencyFormat()
   {
      return (m_currencyFormat);
   }

   /**
    * Retrieve the duration decimal format.
    *
    * @return duration decimal format
    */
   public NumberFormat getDurationDecimalFormat()
   {
      return (m_durationDecimalFormat);
   }

   /**
    * Retrieve the percentage decimal format.
    *
    * @return percentage decimal format
    */
   public NumberFormat getPercentageDecimalFormat()
   {
      return (m_percentageDecimalFormat);
   }

   /**
    * Retrieve the date time format.
    *
    * @return date time format
    */
   public DateFormat getDateTimeFormat()
   {
      return (m_dateTimeFormat);
   }

   /**
    * Retrieve the date format.
    *
    * @return date format
    */
   public DateFormat getDateFormat()
   {
      return (m_dateFormat);
   }

   /**
    * Retrieve the time format.
    *
    * @return time format
    */
   public DateFormat getTimeFormat()
   {
      return (m_timeFormat);
   }

   /**
    * Retrieve the text representing a null value.
    * 
    * @return null text
    */
   public String getNullText()
   {
      return (m_nullText);
   }

   private Locale m_locale;
   private String m_nullText;
   private ProjectFile m_projectFile;
   private MPXJNumberFormat m_unitsDecimalFormat = new MPXJNumberFormat();
   private MPXJNumberFormat m_decimalFormat = new MPXJNumberFormat();
   private MPXJNumberFormat m_currencyFormat = new MPXJNumberFormat();
   private MPXJNumberFormat m_durationDecimalFormat = new MPXJNumberFormat();
   private MPXJNumberFormat m_percentageDecimalFormat = new MPXJNumberFormat();
   private MPXJDateFormat m_dateTimeFormat = new MPXJDateFormat();
   private MPXJDateFormat m_dateFormat = new MPXJDateFormat();
   private MPXJTimeFormat m_timeFormat = new MPXJTimeFormat();
}
