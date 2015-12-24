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
import net.sf.mpxj.ProjectDateFormat;
import net.sf.mpxj.ProjectProperties;
import net.sf.mpxj.ProjectTimeFormat;
import net.sf.mpxj.common.DateHelper;

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
    * This method is called when the locale of the parent file is updated.
    * It resets the locale specific currency attributes to the default values
    * for the new locale.
    *
    * @param properties project properties
    * @param locale new locale
    */
   public static void setLocale(ProjectProperties properties, Locale locale)
   {
      properties.setMpxDelimiter(LocaleData.getChar(locale, LocaleData.FILE_DELIMITER));
      properties.setMpxProgramName(LocaleData.getString(locale, LocaleData.PROGRAM_NAME));
      properties.setMpxCodePage((CodePage) LocaleData.getObject(locale, LocaleData.CODE_PAGE));

      properties.setCurrencySymbol(LocaleData.getString(locale, LocaleData.CURRENCY_SYMBOL));
      properties.setSymbolPosition((CurrencySymbolPosition) LocaleData.getObject(locale, LocaleData.CURRENCY_SYMBOL_POSITION));
      properties.setCurrencyDigits(LocaleData.getInteger(locale, LocaleData.CURRENCY_DIGITS));
      properties.setThousandsSeparator(LocaleData.getChar(locale, LocaleData.CURRENCY_THOUSANDS_SEPARATOR));
      properties.setDecimalSeparator(LocaleData.getChar(locale, LocaleData.CURRENCY_DECIMAL_SEPARATOR));

      properties.setDateOrder((DateOrder) LocaleData.getObject(locale, LocaleData.DATE_ORDER));
      properties.setTimeFormat((ProjectTimeFormat) LocaleData.getObject(locale, LocaleData.TIME_FORMAT));
      properties.setDefaultStartTime(DateHelper.getTimeFromMinutesPastMidnight(LocaleData.getInteger(locale, LocaleData.DEFAULT_START_TIME)));
      properties.setDateSeparator(LocaleData.getChar(locale, LocaleData.DATE_SEPARATOR));
      properties.setTimeSeparator(LocaleData.getChar(locale, LocaleData.TIME_SEPARATOR));
      properties.setAMText(LocaleData.getString(locale, LocaleData.AM_TEXT));
      properties.setPMText(LocaleData.getString(locale, LocaleData.PM_TEXT));
      properties.setDateFormat((ProjectDateFormat) LocaleData.getObject(locale, LocaleData.DATE_FORMAT));
      properties.setBarTextDateFormat((ProjectDateFormat) LocaleData.getObject(locale, LocaleData.DATE_FORMAT));
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
