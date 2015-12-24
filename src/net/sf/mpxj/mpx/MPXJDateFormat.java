/*
 * file:       MPXJDateFormat.java
 * author:     Jon Iles
 *             Scott Melville
 * copyright:  (c) Packwood Software 2002-2006
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

package net.sf.mpxj.mpx;

import java.text.DateFormat;
import java.text.DateFormatSymbols;
import java.text.FieldPosition;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * This class wraps the functionality provided by the SimpleDateFormat class
 * to make it suitable for use with the date conventions used in MPX files.
 */
public final class MPXJDateFormat extends DateFormat
{
   /**
    * This method is called when the locale of the parent file is updated.
    * It resets the locale specific date attributes to the default values
    * for the new locale.
    *
    * @param locale new locale
    * @param nullText locale-specific text representing a null value
    */
   public void setLocale(Locale locale, String nullText)
   {
      m_format = new SimpleDateFormat(m_format.toPattern(), locale);
      m_alternativeFormat = new SimpleDateFormat(m_alternativeFormat.toPattern(), locale);
      m_null = nullText;
   }

   /**
    * This method is used to configure the format pattern.
    *
    * @param pattern new format pattern
    */
   public void applyPattern(String pattern)
   {
      m_format.applyPattern(pattern);

      if (pattern.endsWith(" a") == true)
      {
         pattern = pattern.substring(0, pattern.length() - 2) + "a";
      }

      m_alternativeFormat.applyPattern(pattern);
   }

   /**
    * {@inheritDoc}
    */
   @Override public Date parse(String str, ParsePosition pos)
   {
      Date result;

      if (str == null || str.trim().length() == 0)
      {
         result = null;
         pos.setIndex(-1);
      }
      else
      {
         if (str.equals(m_null) == true)
         {
            result = null;
            pos.setIndex(-1);
         }
         else
         {
            Date javaDate = m_format.parse(str, pos);
            if (pos.getIndex() == 0)
            {
               javaDate = m_alternativeFormat.parse(str, pos);
            }

            if (pos.getIndex() != 0)
            {
               result = javaDate;
            }
            else
            {
               result = null;
            }
         }
      }

      return (result);
   }

   /**
    * {@inheritDoc}
    */
   @Override public StringBuffer format(Date date, StringBuffer toAppendTo, FieldPosition fieldPosition)
   {
      return (m_format.format(date, toAppendTo, fieldPosition));
   }

   /**
    * Allows the AM/PM text to be set.
    *
    * @param am AM text
    * @param pm PM text
    */
   public void setAmPmText(String am, String pm)
   {
      String[] symbolData = new String[]
      {
         am,
         pm
      };

      DateFormatSymbols symbols = m_format.getDateFormatSymbols();
      symbols.setAmPmStrings(symbolData);
      m_format.setDateFormatSymbols(symbols);

      symbols = m_alternativeFormat.getDateFormatSymbols();
      symbols.setAmPmStrings(symbolData);
      m_alternativeFormat.setDateFormatSymbols(symbols);
   }

   /**
    * Internal SimpleDateFormat object used to carry out the formatting work.
    * Note that we force the locale to English at this point. This is done
    * because it is not clear whether MPX date formats that contain the names
    * of the days of the week support localised day names, or just the
    * English names. To make things consistent and to ensure we generate
    * MPX files that can be moved between locales, we default to using the
    * English day names.
    */
   private SimpleDateFormat m_format = new SimpleDateFormat("dd/MM/yyyy", Locale.ENGLISH);
   private SimpleDateFormat m_alternativeFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.ENGLISH);
   private String m_null = "NA";
}
