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
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.temporal.ChronoField;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import net.sf.mpxj.DateOrder;
import net.sf.mpxj.ProjectFile;
import net.sf.mpxj.ProjectProperties;
import net.sf.mpxj.ProjectTimeFormat;
import net.sf.mpxj.common.LocalDateHelper;

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

      updateNumericFormats(decimalSeparator, thousandsSeparator);
      updateCurrencyFormats(properties, decimalSeparator, thousandsSeparator);
      updateDateTimeFormats(properties);
   }

   /**
    * Update numeric formats.
    *
    * @param decimalSeparator decimal separator
    * @param thousandsSeparator thousands separator
    */
   private void updateNumericFormats(char decimalSeparator, char thousandsSeparator)
   {
      m_unitsDecimalFormat.applyPattern("#.##", null, decimalSeparator, thousandsSeparator);
      m_decimalFormat.applyPattern("0.00#", null, decimalSeparator, thousandsSeparator);
      m_durationDecimalFormat.applyPattern("#.##", null, decimalSeparator, thousandsSeparator);
      m_percentageDecimalFormat.applyPattern("##0.##", null, decimalSeparator, thousandsSeparator);
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
      populateTimePatterns(properties);
      populateDatePatterns(properties);
      String[] datePatterns = getDatePatterns(properties);
      String[] dateTimePatterns = getDateTimePatterns(properties);

      m_dateTimeFormat.applyPatterns(dateTimePatterns);
      m_dateTimeFormat.setLocale(m_locale);
      m_dateTimeFormat.setNullText(m_nullText);

      m_dateTimeFormat.setAmPmText(properties.getAMText(), properties.getPMText());
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

   private DateTimeFormatterBuilder getPrintDateBuilder(ProjectProperties properties)
   {
      DateTimeFormatterBuilder builder = new DateTimeFormatterBuilder();
      builder.parseCaseInsensitive();

      char datesep = properties.getDateSeparator();
      DateOrder dateOrder = properties.getDateOrder();

      switch (dateOrder)
      {
         case DMY:
         {
            builder.appendPattern("dd" + datesep + "MM" + datesep + "yy");
            break;
         }

         case MDY:
         {
            builder.appendPattern("MM" + datesep + "dd" + datesep + "yy");
            break;
         }

         case YMD:
         {
            builder.appendPattern("yy" + datesep + "MM" + datesep + "dd");
            break;
         }
      }

      return builder;
   }

   private DateTimeFormatterBuilder getParseDateBuilder(ProjectProperties properties)
   {
      DateTimeFormatterBuilder builder = new DateTimeFormatterBuilder();
      builder.parseCaseInsensitive();

      char datesep = properties.getDateSeparator();
      DateOrder dateOrder = properties.getDateOrder();

      switch (dateOrder)
      {
         case DMY:
         {
            builder.appendPattern("[dd][d]" + datesep + "[MM][M]" + datesep + "[yyyy]");
            builder.optionalStart();
            builder.appendValueReduced(ChronoField.YEAR, 2, 2, LocalDateHelper.TWO_DIGIT_YEAR_BASE_DATE);
            builder.optionalEnd();
            break;
         }

         case MDY:
         {
            builder.appendPattern("[MM][M]" + datesep + "[dd][d]" + datesep + "[yyyy]");
            builder.optionalStart();
            builder.appendValueReduced(ChronoField.YEAR, 2, 2, LocalDateHelper.TWO_DIGIT_YEAR_BASE_DATE);
            builder.optionalEnd();
            break;
         }

         case YMD:
         {
            builder.appendPattern("[yyyy]");
            builder.optionalStart();
            builder.appendValueReduced(ChronoField.YEAR, 2, 2, LocalDateHelper.TWO_DIGIT_YEAR_BASE_DATE);
            builder.optionalEnd();
            builder.appendPattern(datesep + "[MM][M]" + datesep + "[dd][d]");
            break;
         }
      }


      return builder;
   }

   /**
    * Generate datetime patterns based on the project configuration.
    *
    * @param properties project configuration
    * @return datetime patterns
    */
   private String[] getDateTimePatterns(ProjectProperties properties)
   {
      String[] timePatterns;
      char timesep = properties.getTimeSeparator();
      ProjectTimeFormat format = properties.getTimeFormat();

      if (format == null || format == ProjectTimeFormat.TWELVE_HOUR)
      {
         timePatterns = new String[]
            {
               "hh" + timesep + "mm a",
               "hh" + timesep + "mma"
            };
      }
      else
      {
         timePatterns = new String[]
            {
               "HH" + timesep + "mm",
               "HH",
            };
      }


      List<String> patterns = new ArrayList<>();
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

      return patterns.toArray(new String[0]);
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
      List<String> patterns = new ArrayList<>();
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
    */
   private void populateTimePatterns(ProjectProperties properties)
   {
      DateTimeFormatterBuilder parseBuilder = new DateTimeFormatterBuilder();
      parseBuilder.parseCaseInsensitive();
      applyTimeParsePattern(properties, parseBuilder);
      m_parseTimeFormat = parseBuilder.toFormatter();

      DateTimeFormatterBuilder printBuilder = new DateTimeFormatterBuilder();
      applyTimePrintPattern(properties, printBuilder);
      m_printTimeFormat = printBuilder.toFormatter();
   }

   private void applyTimeParsePattern(ProjectProperties properties, DateTimeFormatterBuilder builder)
   {
      char timesep = properties.getTimeSeparator();
      ProjectTimeFormat format = properties.getTimeFormat();

      if (format == null || format == ProjectTimeFormat.TWELVE_HOUR)
      {
         Map<Long, String> ampmMap = new HashMap<>();
         ampmMap.put(Long.valueOf(0), properties.getAMText());
         ampmMap.put(Long.valueOf(1), properties.getPMText());

         builder.optionalStart();
         builder.appendPattern("[hh][h]" + timesep + "mm[ ]");
         builder.appendText(ChronoField.AMPM_OF_DAY, ampmMap);
         builder.optionalEnd();
         builder.appendPattern("[[HH][H]" + timesep + "mm]");
      }
      else
      {
         builder.appendPattern("[HH][H][" + timesep + "mm]");
      }
   }

   private void applyTimePrintPattern(ProjectProperties properties, DateTimeFormatterBuilder printBuilder)
   {
      char timesep = properties.getTimeSeparator();
      ProjectTimeFormat format = properties.getTimeFormat();

      if (format == null || format == ProjectTimeFormat.TWELVE_HOUR)
      {
         Map<Long, String> ampmMap = new HashMap<>();
         ampmMap.put(Long.valueOf(0), properties.getAMText());
         ampmMap.put(Long.valueOf(1), properties.getPMText());

         printBuilder.appendPattern("hh" + timesep + "mm ");
         printBuilder.appendText(ChronoField.AMPM_OF_DAY, ampmMap);
      }
      else
      {
         printBuilder.appendPattern("HH" + timesep + "mm");
      }
   }

   private void populateDatePatterns(ProjectProperties properties)
   {
      m_parseDateFormat = getParseDateBuilder(properties).toFormatter();
      m_printDateFormat = getPrintDateBuilder(properties).toFormatter();
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
    * Retrieve the time format.
    *
    * @return time format
    */
   public DateTimeFormatter getParseTimeFormat()
   {
      return m_parseTimeFormat;
   }

   public DateTimeFormatter getPrintTimeFormat()
   {
      return m_printTimeFormat;
   }

   public LocalDateTime parseDate(String value)
   {
      if (m_nullText.equals(value))
      {
         return null;
      }

      return LocalDate.parse(value, m_parseDateFormat).atStartOfDay();
   }

   public String printDate(LocalDate value)
   {
      return value == null ? null : m_printDateFormat.format(value);
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

   private final Locale m_locale;
   private final String m_nullText;
   private final ProjectFile m_projectFile;
   private final MPXJNumberFormat m_unitsDecimalFormat = new MPXJNumberFormat();
   private final MPXJNumberFormat m_decimalFormat = new MPXJNumberFormat();
   private final MPXJNumberFormat m_currencyFormat = new MPXJNumberFormat();
   private final MPXJNumberFormat m_durationDecimalFormat = new MPXJNumberFormat();
   private final MPXJNumberFormat m_percentageDecimalFormat = new MPXJNumberFormat();
   private final MPXJDateFormat m_dateTimeFormat = new MPXJDateFormat();
   private DateTimeFormatter m_parseDateFormat;
   private DateTimeFormatter m_printDateFormat;
   private DateTimeFormatter m_parseTimeFormat;
   private DateTimeFormatter m_printTimeFormat;
}
