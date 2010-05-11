/*
 * file:       LocaleUtility.java
 * author:     Jon Iles
 * copyright:  (c) Packwood Software 2005
 * date:       Jan 23, 2006
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

import java.util.Locale;

import net.sf.mpxj.CodePage;
import net.sf.mpxj.CurrencySymbolPosition;
import net.sf.mpxj.DateOrder;
import net.sf.mpxj.FileCreationRecord;
import net.sf.mpxj.ProjectDateFormat;
import net.sf.mpxj.ProjectFile;
import net.sf.mpxj.ProjectHeader;
import net.sf.mpxj.ProjectTimeFormat;

/**
 * This class contains methods used to configure the locale of an MPX file,
 * along with other common locale related methods.
 */
final class LocaleUtility
{
   /**
    * Constructor.
    */
   private LocaleUtility()
   {
      // Private constructor to prevent instantiation
   }

   /**
    * This method sets the locale to be used by an MPX file.
    *
    * @param file project file
    * @param locale locale to be used
    */
   public static void setLocale(ProjectFile file, Locale locale)
   {
      file.setDelimiter(LocaleData.getChar(locale, LocaleData.FILE_DELIMITER));
      setLocale(file.getFileCreationRecord(), locale);
      setLocale(file.getProjectHeader(), locale);
   }

   /**
    * This method is called when the locale of the parent file is updated.
    * It resets the locale specific currency attributes to the default values
    * for the new locale.
    *
    * @param fcr file creation record instance
    * @param locale new locale
    */
   private static void setLocale(FileCreationRecord fcr, Locale locale)
   {
      fcr.setDelimiter(LocaleData.getChar(locale, LocaleData.FILE_DELIMITER));
      fcr.setProgramName(LocaleData.getString(locale, LocaleData.PROGRAM_NAME));
      fcr.setCodePage((CodePage) LocaleData.getObject(locale, LocaleData.CODE_PAGE));
   }

   /**
    * This method is called when the locale of the parent file is updated.
    * It resets the locale specific currency attributes to the default values
    * for the new locale.
    *
    * @param header project header instance
    * @param locale new locale
    */
   private static void setLocale(ProjectHeader header, Locale locale)
   {
      header.setCurrencySymbol(LocaleData.getString(locale, LocaleData.CURRENCY_SYMBOL));
      header.setSymbolPosition((CurrencySymbolPosition) LocaleData.getObject(locale, LocaleData.CURRENCY_SYMBOL_POSITION));
      header.setCurrencyDigits(LocaleData.getInteger(locale, LocaleData.CURRENCY_DIGITS));
      header.setThousandsSeparator(LocaleData.getChar(locale, LocaleData.CURRENCY_THOUSANDS_SEPARATOR));
      header.setDecimalSeparator(LocaleData.getChar(locale, LocaleData.CURRENCY_DECIMAL_SEPARATOR));

      header.setDateOrder((DateOrder) LocaleData.getObject(locale, LocaleData.DATE_ORDER));
      header.setTimeFormat((ProjectTimeFormat) LocaleData.getObject(locale, LocaleData.TIME_FORMAT));
      header.setIntegerDefaultStartTime(LocaleData.getInteger(locale, LocaleData.DEFAULT_START_TIME));
      header.setDateSeparator(LocaleData.getChar(locale, LocaleData.DATE_SEPARATOR));
      header.setTimeSeparator(LocaleData.getChar(locale, LocaleData.TIME_SEPARATOR));
      header.setAMText(LocaleData.getString(locale, LocaleData.AM_TEXT));
      header.setPMText(LocaleData.getString(locale, LocaleData.PM_TEXT));
      header.setDateFormat((ProjectDateFormat) LocaleData.getObject(locale, LocaleData.DATE_FORMAT));
      header.setBarTextDateFormat((ProjectDateFormat) LocaleData.getObject(locale, LocaleData.DATE_FORMAT));
   }

   /**
    * Retrieves an array containing the locales supported by MPXJ's 
    * MPX functionality.
    * 
    * @return array of supported locales
    */
   public static Locale[] getSupportedLocales()
   {
      return (SUPPORTED_LOCALES);
   }

   /**
    * Array of locales supported by MPXJ's MPX functionality.
    */
   private static final Locale[] SUPPORTED_LOCALES =
   {
      new Locale("EN"),
      new Locale("DE"),
      new Locale("FR"),
      new Locale("IT"),
      new Locale("PT"),
      new Locale("SV"),
      new Locale("ZH"),
      new Locale("ES"),
      new Locale("RU")
   };
}
