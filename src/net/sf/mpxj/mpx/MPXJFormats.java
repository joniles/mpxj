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

package net.sf.mpxj.mpx;

import java.text.DateFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

import net.sf.mpxj.DateOrder;
import net.sf.mpxj.ProjectFile;
import net.sf.mpxj.ProjectProperties;
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
      ProjectProperties properties = m_projectFile.getProjectProperties();
      char decimalSeparator = properties.getDecimalSeparator();
      char thousandsSeparator = properties.getThousandsSeparator();
      m_unitsDecimalFormat.applyPattern("#.##", null, decimalSeparator, thousandsSeparator);
      m_decimalFormat.applyPattern("0.00#", null, decimalSeparator, thousandsSeparator);
      m_durationDecimalFormat.applyPattern("#.##", null, decimalSeparator, thousandsSeparator);
      m_percentageDecimalFormat.applyPattern("##0.##", null, decimalSeparator, thousandsSeparator);
      updateCurrencyFormats(properties, decimalSeparator, thousandsSeparator);
      updateDateTimeFormats(properties);
   }

   /**
    * Update the currency format.
    *
    * @param properties project properties
    * @param decimalSeparator decimal separator
    * @param thousandsSeparator thousands separator
    */
   private void updateCurrencyFormats(ProjectProperties properties, char decimalSeparator, char thousandsSeparator)
   {
      String prefix = "";
      String suffix = "";
      String currencySymbol = quoteFormatCharacters(properties.getCurrencySymbol());

      switch (properties.getSymbolPosition())
      {
         case AFTER:
         {
            suffix = currencySymbol;
            break;
         }

         case BEFORE:
         {
            prefix = currencySymbol;
            break;
         }

         case AFTER_WITH_SPACE:
         {
            suffix = " " + currencySymbol;
            break;
         }

         case BEFORE_WITH_SPACE:
         {
            prefix = currencySymbol + " ";
            break;
         }
      }

      StringBuilder pattern = new StringBuilder(prefix);
      pattern.append("#0");

      int digits = properties.getCurrencyDigits().intValue();
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
      StringBuilder sb = new StringBuilder();
      int length = literal.length();
      char c;

      for (int loop = 0; loop < length; loop++)
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
               sb.append("'");
               sb.append(c);
               sb.append("'");
               break;
            }

            default:
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
    * @param properties project properties
    */
   private void updateDateTimeFormats(ProjectProperties properties)
   {
      String[] timePatterns = getTimePatterns(properties);
      String[] datePatterns = getDatePatterns(properties);
      String[] dateTimePatterns = getDateTimePatterns(properties, timePatterns);

      m_dateTimeFormat.applyPatterns(dateTimePatterns);
      m_dateFormat.applyPatterns(datePatterns);
      m_timeFormat.applyPatterns(timePatterns);

      m_dateTimeFormat.setLocale(m_locale);
      m_dateFormat.setLocale(m_locale);

      m_dateTimeFormat.setNullText(m_nullText);
      m_dateFormat.setNullText(m_nullText);
      m_timeFormat.setNullText(m_nullText);

      m_dateTimeFormat.setAmPmText(properties.getAMText(), properties.getPMText());
      m_timeFormat.setAmPmText(properties.getAMText(), properties.getPMText());
   }

   /**
    * Generate date patterns based on the project configuration.
    *
    * @param properties project properties
    * @return date patterns
    */
   private String[] getDatePatterns(ProjectProperties properties)
   {
      String pattern = "";

      char datesep = properties.getDateSeparator();
      DateOrder dateOrder = properties.getDateOrder();

      switch (dateOrder)
      {
         case DMY:
         {
            pattern = "dd" + datesep + "MM" + datesep + "yy";
            break;
         }

         case MDY:
         {
            pattern = "MM" + datesep + "dd" + datesep + "yy";
            break;
         }

         case YMD:
         {
            pattern = "yy" + datesep + "MM" + datesep + "dd";
            break;
         }
      }

      return new String[]
      {
         pattern
      };
   }

   /**
    * Generate datetime patterns based on the project configuration.
    *
    * @param properties project configuration
    * @param timePatterns time patterns
    * @return datetime patterns
    */
   private String[] getDateTimePatterns(ProjectProperties properties, String[] timePatterns)
   {
      List<String> patterns = new ArrayList<String>();
      char datesep = properties.getDateSeparator();
      DateOrder dateOrder = properties.getDateOrder();

      switch (properties.getDateFormat())
      {
         case DD_MM_YY_HH_MM:
         {
            switch (dateOrder)
            {
               case DMY:
               {
                  patterns.addAll(generateDateTimePatterns("dd" + datesep + "MM" + datesep + "yy", timePatterns));
                  break;
               }

               case MDY:
               {
                  patterns.addAll(generateDateTimePatterns("MM" + datesep + "dd" + datesep + "yy", timePatterns));
                  break;
               }

               case YMD:
               {
                  patterns.addAll(generateDateTimePatterns("yy" + datesep + "MM" + datesep + "dd", timePatterns));
                  break;
               }
            }
            break;
         }

         case DD_MM_YY:
         {
            switch (dateOrder)
            {
               case DMY:
               {
                  patterns.add("dd" + datesep + "MM" + datesep + "yy");
                  break;
               }

               case MDY:
               {
                  patterns.add("MM" + datesep + "dd" + datesep + "yy");
                  break;
               }

               case YMD:
               {
                  patterns.add("yy" + datesep + "MM" + datesep + "dd");
                  break;

               }
            }
            break;
         }

         case DD_MMMMM_YYYY_HH_MM:
         {
            switch (dateOrder)
            {
               case DMY:
               {
                  patterns.addAll(generateDateTimePatterns("dd MMMMM yyyy", timePatterns));
                  break;
               }

               case MDY:
               {
                  patterns.addAll(generateDateTimePatterns("MMMMM dd yyyy", timePatterns));
                  break;
               }

               case YMD:
               {
                  patterns.addAll(generateDateTimePatterns("yyyy MMMMM dd", timePatterns));
                  break;
               }
            }
            break;
         }

         case DD_MMMMM_YYYY:
         {
            switch (dateOrder)
            {
               case DMY:
               {
                  patterns.add("dd MMMMM yyyy");
                  break;
               }

               case MDY:
               {
                  patterns.add("MMMMM dd yyyy");
                  break;
               }

               case YMD:
               {
                  patterns.add("yyyy MMMMM dd");
                  break;
               }
            }
            break;
         }

         case DD_MMM_HH_MM:
         {
            switch (dateOrder)
            {
               case DMY:
               {
                  patterns.addAll(generateDateTimePatterns("dd MMM", timePatterns));
                  break;
               }

               case YMD:
               case MDY:
               {
                  patterns.addAll(generateDateTimePatterns("MMM dd", timePatterns));
                  break;
               }
            }
            break;
         }

         case DD_MMM_YY:
         {
            switch (dateOrder)
            {
               case DMY:
               {
                  patterns.add("dd MMM ''yy");
                  break;
               }

               case MDY:
               {
                  patterns.add("MMM dd ''yy");
                  break;
               }

               case YMD:
               {
                  patterns.add("''yy MMM dd");
                  break;
               }
            }
            break;
         }

         case DD_MMMMM:
         {
            switch (dateOrder)
            {
               case DMY:
               {
                  patterns.add("dd MMMMM");
                  break;
               }

               case YMD:
               case MDY:
               {
                  patterns.add("MMMMM dd");
                  break;
               }
            }
            break;
         }

         case DD_MMM:
         {
            switch (dateOrder)
            {
               case DMY:
               {
                  patterns.add("dd MMM");
                  break;
               }

               case YMD:
               case MDY:
               {
                  patterns.add("MMM dd");
                  break;
               }
            }
            break;
         }

         case EEE_DD_MM_YY_HH_MM:
         {
            switch (dateOrder)
            {
               case DMY:
               {
                  patterns.addAll(generateDateTimePatterns("EEE " + "dd" + datesep + "MM" + datesep + "yy", timePatterns));
                  break;
               }

               case MDY:
               {
                  patterns.addAll(generateDateTimePatterns("EEE " + "MM" + datesep + "dd" + datesep + "yy", timePatterns));
                  break;
               }

               case YMD:
               {
                  patterns.addAll(generateDateTimePatterns("EEE " + "yy" + datesep + "MM" + datesep + "dd", timePatterns));
                  break;
               }
            }
            break;
         }

         case EEE_DD_MM_YY:
         {
            switch (dateOrder)
            {
               case DMY:
               {
                  patterns.add("EEE dd" + datesep + "MM" + datesep + "yy");
                  break;
               }

               case MDY:
               {
                  patterns.add("EEE MM" + datesep + "dd" + datesep + "yy");
                  break;
               }

               case YMD:
               {
                  patterns.add("EEE yy" + datesep + "MM" + datesep + "dd");
                  break;
               }
            }
            break;
         }

         case EEE_DD_MMM_YY:
         {
            switch (dateOrder)
            {
               case DMY:
               {
                  patterns.add("EEE dd MMM ''yy");
                  break;
               }

               case MDY:
               {
                  patterns.add("EEE MMM dd ''yy");
                  break;
               }

               case YMD:
               {
                  patterns.add("EEE ''yy MMM dd");
                  break;
               }
            }
            break;
         }

         case EEE_HH_MM:
         {
            patterns.addAll(generateDateTimePatterns("EEE ", timePatterns));
            break;
         }

         case DD_MM:
         {
            switch (dateOrder)
            {
               case DMY:
               {
                  patterns.add("dd" + datesep + "MM");
                  break;
               }

               case YMD:
               case MDY:
               {
                  patterns.add("MM" + datesep + "dd");
                  break;
               }
            }
            break;
         }

         case DD:
         {
            patterns.add("dd");
            break;
         }

         case HH_MM:
         {
            patterns.addAll(Arrays.asList(timePatterns));
            break;
         }

         case EEE_DD_MMM:
         {
            switch (dateOrder)
            {
               case DMY:
               {
                  patterns.add("EEE dd MMM");
                  break;
               }

               case YMD:
               case MDY:
               {
                  patterns.add("EEE MMM dd");
                  break;
               }
            }
            break;
         }

         case EEE_DD_MM:
         {
            switch (dateOrder)
            {
               case DMY:
               {
                  patterns.add("EEE dd" + datesep + "MM");
                  break;
               }

               case YMD:
               case MDY:
               {
                  patterns.add("EEE MM" + datesep + "dd");
                  break;
               }
            }
            break;
         }

         case EEE_DD:
         {
            patterns.add("EEE dd");
            break;
         }

         case DD_WWW:
         {
            patterns.add("F" + datesep + "'W'ww");
            break;
         }

         case DD_WWW_YY_HH_MM:
         {
            patterns.addAll(generateDateTimePatterns("F" + datesep + "'W'ww" + datesep + "yy", timePatterns));
            break;
         }

         case DD_MM_YYYY:
         {
            switch (dateOrder)
            {
               case DMY:
               {
                  patterns.add("dd" + datesep + "MM" + datesep + "yyyy");
                  break;
               }

               case MDY:
               {
                  patterns.add("MM" + datesep + "dd" + datesep + "yyyy");
                  break;
               }

               case YMD:
               {
                  patterns.add("yyyy" + datesep + "MM" + datesep + "dd");
                  break;
               }
            }
            break;
         }
      }

      return patterns.toArray(new String[patterns.size()]);
   }

   /**
    * Generate a set of datetime patterns to accommodate variations in MPX files.
    *
    * @param datePattern date pattern element
    * @param timePatterns time patterns
    * @return datetime patterns
    */
   private List<String> generateDateTimePatterns(String datePattern, String[] timePatterns)
   {
      List<String> patterns = new ArrayList<String>();
      for (String timePattern : timePatterns)
      {
         patterns.add(datePattern + " " + timePattern);
      }

      // Always fall back on the date-only pattern
      patterns.add(datePattern);

      return patterns;
   }

   /**
    * Returns time elements considering 12/24 hour formatting.
    *
    * @param properties project properties
    * @return time formatting String
    */
   private String[] getTimePatterns(ProjectProperties properties)
   {
      String[] result;
      char timesep = properties.getTimeSeparator();
      ProjectTimeFormat format = properties.getTimeFormat();

      if (format == null || format == ProjectTimeFormat.TWELVE_HOUR)
      {
         result = new String[]
         {
            "hh" + timesep + "mm a",
            "hh" + timesep + "mma"
         };
      }
      else
      {
         result = new String[]
         {
            "HH" + timesep + "mm",
            "HH",
         };
      }

      return result;
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
