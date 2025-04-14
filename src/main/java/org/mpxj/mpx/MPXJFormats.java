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

package org.mpxj.mpx;

import java.text.NumberFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.temporal.ChronoField;
import java.time.temporal.TemporalAccessor;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import org.mpxj.DateOrder;
import org.mpxj.ProjectFile;
import org.mpxj.ProjectProperties;
import org.mpxj.ProjectTimeFormat;
import org.mpxj.common.LocalDateHelper;
import org.mpxj.common.LocalDateTimeHelper;

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
      populateDateTimePatterns(properties);
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
    */
   private void populateDateTimePatterns(ProjectProperties properties)
   {
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
                  createParseFormatterWithTime(properties, "dd" + datesep + "MM" + datesep, "yy");
                  m_printDateTimeFormat = createPrintFormatterWithTime(properties, "dd" + datesep + "MM" + datesep + "yy");
                  break;
               }

               case MDY:
               {
                  createParseFormatterWithTime(properties, "MM" + datesep + "dd" + datesep, "yy");
                  m_printDateTimeFormat = createPrintFormatterWithTime(properties, "MM" + datesep + "dd" + datesep + "yy");
                  break;
               }

               case YMD:
               {
                  createParseFormatterWithTime(properties, "yy", datesep + "MM" + datesep + "dd");
                  m_printDateTimeFormat = createPrintFormatterWithTime(properties, "yy" + datesep + "MM" + datesep + "dd");
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
                  createParseFormatterWithTime(properties, "dd" + datesep + "MM" + datesep, "yy");
                  createPrintFormatter("dd" + datesep + "MM" + datesep + "yy");
                  break;
               }

               case MDY:
               {
                  createParseFormatterWithTime(properties, "MM" + datesep + "dd" + datesep, "yy");
                  createPrintFormatter("MM" + datesep + "dd" + datesep + "yy");
                  break;
               }

               case YMD:
               {
                  createParseFormatterWithTime(properties, "yy", datesep + "MM" + datesep + "dd");
                  createPrintFormatter("yy" + datesep + "MM" + datesep + "dd");
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
                  createParseFormatterWithTime(properties, "dd MMMM yyyy");
                  m_printDateTimeFormat = createPrintFormatterWithTime(properties, "dd MMMM yyyy");
                  break;
               }

               case MDY:
               {
                  createParseFormatterWithTime(properties, "MMMM dd yyyy");
                  m_printDateTimeFormat = createPrintFormatterWithTime(properties, "MMMM dd yyyy");
                  break;
               }

               case YMD:
               {
                  createParseFormatterWithTime(properties, "yyyy MMMM dd");
                  m_printDateTimeFormat = createPrintFormatterWithTime(properties, "yyyy MMMM dd");
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
                  createParseFormatterWithTime(properties, "dd MMMM yyyy");
                  createPrintFormatter("dd MMMM yyyy");
                  break;
               }

               case MDY:
               {
                  createParseFormatterWithTime(properties, "MMMM dd yyyy");
                  createPrintFormatter("MMMM dd yyyy");
                  break;
               }

               case YMD:
               {
                  createParseFormatterWithTime(properties, "yyyy MMMM dd");
                  createPrintFormatter("yyyy MMMM dd");
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
                  createParseFormatterWithTime(properties, "dd MMM");
                  createPrintFormatterWithTime(properties, "dd MMM");
                  break;
               }

               case YMD:
               case MDY:
               {
                  createParseFormatterWithTime(properties, "MMM dd");
                  createPrintFormatterWithTime(properties, "MMM dd");
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
                  createParseFormatterWithTime(properties, "dd MMM ''", "yy");
                  createPrintFormatter("dd MMM ''yy");
                  break;
               }

               case MDY:
               {
                  createParseFormatterWithTime(properties, "MMM dd ''", "yy");
                  createPrintFormatter("MMM dd ''yy");
                  break;
               }

               case YMD:
               {
                  createParseFormatterWithTime(properties, "''", "yy", " MMM dd");
                  createPrintFormatter("''yy MMM dd");
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
                  createParseFormatterWithTime(properties, "dd MMMM");
                  createPrintFormatter("dd MMMM");
                  break;
               }

               case YMD:
               case MDY:
               {
                  createParseFormatterWithTime(properties, "MMMM dd");
                  createPrintFormatter("MMMM dd");
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
                  createParseFormatterWithTime(properties, "dd MMM");
                  createPrintFormatter("dd MMM");
                  break;
               }

               case YMD:
               case MDY:
               {
                  createParseFormatterWithTime(properties, "MMM dd");
                  createPrintFormatter("MMM dd");
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
                  createParseFormatterWithTime(properties, "[EEE ]dd" + datesep + "MM" + datesep, "yy");
                  createPrintFormatterWithTime(properties, "EEE dd" + datesep + "MM" + datesep + "yy");
                  break;
               }

               case MDY:
               {
                  createParseFormatterWithTime(properties, "[EEE ]MM" + datesep + "dd" + datesep, "yy");
                  createPrintFormatterWithTime(properties, "EEE MM" + datesep + "dd" + datesep + "yy");
                  break;
               }

               case YMD:
               {
                  createParseFormatterWithTime(properties, "[EEE ]", "yy", datesep + "MM" + datesep + "dd");
                  createPrintFormatterWithTime(properties, "EEE yy" + datesep + "MM" + datesep + "dd");
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
                  createParseFormatterWithTime(properties, "[EEE ]dd" + datesep + "MM" + datesep, "yy");
                  createPrintFormatter("EEE dd" + datesep + "MM" + datesep + "yy");
                  break;
               }

               case MDY:
               {
                  createParseFormatterWithTime(properties, "[EEE ]MM" + datesep + "dd" + datesep, "yy");
                  createPrintFormatter("EEE MM" + datesep + "dd" + datesep + "yy");
                  break;
               }

               case YMD:
               {
                  createParseFormatterWithTime(properties, "[EEE ]", "yy", datesep + "MM" + datesep + "dd");
                  createPrintFormatter("EEE yy" + datesep + "MM" + datesep + "dd");
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
                  createParseFormatterWithTime(properties, "[EEE ]dd MMM ''", "yy");
                  createPrintFormatter("EEE dd MMM ''yy");
                  break;
               }

               case MDY:
               {
                  createParseFormatterWithTime(properties, "[EEE ]MMM dd ''", "yy");
                  createPrintFormatter("EEE MMM dd ''yy");
                  break;
               }

               case YMD:
               {
                  createParseFormatterWithTime(properties, "[EEE ]''", "yy", " MMM dd");
                  createPrintFormatter("EEE ''yy MMM dd");
                  break;
               }
            }
            break;
         }

         case EEE_HH_MM:
         {
            createParseFormatterWithTime(properties, "[EEE ]");
            m_printDateTimeFormat = createPrintFormatterWithTime(properties, "EEE ");
            break;
         }

         case DD_MM:
         {
            switch (dateOrder)
            {
               case DMY:
               {
                  createParseFormatterWithTime(properties, "dd" + datesep + "MM");
                  createPrintFormatter("dd" + datesep + "MM");
                  break;
               }

               case YMD:
               case MDY:
               {
                  createParseFormatterWithTime(properties, "MM" + datesep + "dd");
                  createPrintFormatter("MM" + datesep + "dd");
                  break;
               }
            }
            break;
         }

         case DD:
         {
            createParseFormatterWithTime(properties, "dd");
            createPrintFormatter("dd");
            break;
         }

         case HH_MM:
         {
            DateTimeFormatterBuilder parseBuilder = new DateTimeFormatterBuilder();
            parseBuilder.parseCaseInsensitive();
            applyTimeParsePattern(properties, parseBuilder);
            m_parseDateTimeFormat = parseBuilder.toFormatter().withLocale(m_locale);

            parseBuilder = new DateTimeFormatterBuilder();
            parseBuilder.parseCaseInsensitive();
            applyTimePrintPattern(properties, parseBuilder);
            m_printDateTimeFormat = parseBuilder.toFormatter().withLocale(m_locale);
            break;
         }

         case EEE_DD_MMM:
         {
            switch (dateOrder)
            {
               case DMY:
               {
                  createParseFormatterWithTime(properties, "[EEE ]dd MMM");
                  createPrintFormatter("EEE dd MMM");
                  break;
               }

               case YMD:
               case MDY:
               {
                  createParseFormatterWithTime(properties, "[EEE ]MMM dd");
                  createPrintFormatter("EEE MMM dd");
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
                  createParseFormatterWithTime(properties, "[EEE ]dd" + datesep + "MM");
                  createPrintFormatter("EEE dd" + datesep + "MM");
                  break;
               }

               case YMD:
               case MDY:
               {
                  createParseFormatterWithTime(properties, "[EEE ]MM" + datesep + "dd");
                  createPrintFormatter("EEE MM" + datesep + "dd");
                  break;
               }
            }
            break;
         }

         case EEE_DD:
         {
            createParseFormatterWithTime(properties, "[EEE ]dd");
            createPrintFormatter("EEE dd");
            break;
         }

         case DD_WWW:
         {
            createParseFormatterWithTime(properties, "F" + datesep + "'W'ww");
            createPrintFormatter("F" + datesep + "'W'ww");
            break;
         }

         case DD_WWW_YY_HH_MM:
         {
            createParseFormatterWithTime(properties, "F" + datesep + "'W'ww" + datesep, "yy");
            m_printDateTimeFormat = createPrintFormatterWithTime(properties, "F" + datesep + "'W'ww" + datesep + "yy");
            break;
         }

         case DD_MM_YYYY:
         {
            switch (dateOrder)
            {
               case DMY:
               {
                  createParseFormatterWithTime(properties, "dd" + datesep + "MM" + datesep + "yyyy");
                  createPrintFormatter("dd" + datesep + "MM" + datesep + "yyyy");
                  break;
               }

               case MDY:
               {
                  createParseFormatterWithTime(properties, "MM" + datesep + "dd" + datesep + "yyyy");
                  createPrintFormatter("MM" + datesep + "dd" + datesep + "yyyy");
                  break;
               }

               case YMD:
               {
                  createParseFormatterWithTime(properties, "yyyy" + datesep + "MM" + datesep + "dd");
                  createPrintFormatter("yyyy" + datesep + "MM" + datesep + "dd");
                  break;
               }
            }
            break;
         }
      }
   }

   private void createParseFormatterWithTime(ProjectProperties properties, String... patterns)
   {
      DateTimeFormatterBuilder builder = new DateTimeFormatterBuilder();
      builder.parseCaseInsensitive();
      builder.parseLenient();

      for (String pattern : patterns)
      {
         if (pattern.equals("yy"))
         {
            builder.appendValueReduced(ChronoField.YEAR, 2, 2, LocalDateHelper.TWO_DIGIT_YEAR_BASE_DATE);
         }
         else
         {
            builder.appendPattern(pattern);
         }
      }

      builder.optionalStart();
      builder.appendPattern(" ");
      applyTimeParsePattern(properties, builder);
      builder.optionalEnd();
      m_parseDateTimeFormat = builder.toFormatter().withLocale(m_locale);

      if (patterns[0].startsWith("[EEE ]"))
      {
         m_parseDateTimeSkipDayName = true;
      }
   }

   private DateTimeFormatter createPrintFormatterWithTime(ProjectProperties properties, String pattern)
   {
      DateTimeFormatterBuilder builder = new DateTimeFormatterBuilder();
      builder.appendPattern(pattern);
      builder.appendPattern(" ");
      applyTimePrintPattern(properties, builder);
      return builder.toFormatter().withLocale(m_locale);
   }

   private void createPrintFormatter(String pattern)
   {
      DateTimeFormatterBuilder builder = new DateTimeFormatterBuilder();
      builder.appendPattern(pattern);
      m_printDateTimeFormat = builder.toFormatter().withLocale(m_locale);
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
    * Use the configured format to parse a time value.
    *
    * @param value time value
    * @return LocalTime instance
    */
   public LocalTime parseTime(String value)
   {
      return LocalTime.parse(value, m_parseTimeFormat);
   }

   /**
    * Use the configured format to print a time value.
    *
    * @param value LocalTime instance
    * @return formatted time value
    */
   public String printTime(LocalTime value)
   {
      return (value == null ? null : m_printTimeFormat.format(value));
   }

   /**
    * Use the configured format to parse a date value.
    *
    * @param value date value
    * @return LocalDateTime instance
    */
   public LocalDateTime parseDate(String value)
   {
      if (m_nullText.equals(value))
      {
         return null;
      }

      return LocalDate.parse(value, m_parseDateFormat).atStartOfDay();
   }

   /**
    * Use the configured format to print a date value.
    *
    * @param value LocalDate instance
    * @return formatted time value
    */
   public String printDate(LocalDate value)
   {
      return value == null ? null : m_printDateFormat.format(value);
   }

   /**
    * Use the configured format to parse a timestamp value.
    *
    * @param value timestamp value
    * @return LocalDateTime instance
    */
   public LocalDateTime parseDateTime(String value)
   {
      if (m_nullText.equals(value))
      {
         return null;
      }

      if (m_parseDateTimeSkipDayName)
      {
         value = value.substring(value.indexOf(' ') + 1);
      }

      return LocalDateTimeHelper.parseBest(m_parseDateTimeFormat, value);
   }

   /**
    * Use the configured format to print a timestamp value.
    *
    * @param value temporal value
    * @return formatted timestamp value
    */
   public String printDateTime(TemporalAccessor value)
   {
      if (value == null)
      {
         return null;
      }

      return m_printDateTimeFormat.format(value);
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
   private boolean m_parseDateTimeSkipDayName;
   private DateTimeFormatter m_parseDateTimeFormat;
   private DateTimeFormatter m_printDateTimeFormat;
   private DateTimeFormatter m_parseDateFormat;
   private DateTimeFormatter m_printDateFormat;
   private DateTimeFormatter m_parseTimeFormat;
   private DateTimeFormatter m_printTimeFormat;
}
