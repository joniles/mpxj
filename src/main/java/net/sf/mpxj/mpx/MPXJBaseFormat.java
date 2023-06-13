/*
 * file:       MPXJBaseFormat.java
 * author:     Jon Iles
 * copyright:  (c) Packwood Software 2002-2017
 * date:       27/01/2017
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

import java.text.DateFormatSymbols;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;

import net.sf.mpxj.common.MultiDateFormat;

/**
 * This class wraps the functionality provided by the SimpleDateFormat class
 * to make it suitable for use with the time conventions used in MPX files.
 */
abstract class MPXJBaseFormat extends MultiDateFormat
{
   /**
    * This method allows the null text value to be set. In an English
    * locale this is typically "NA".
    *
    * @param nullText null text value
    */
   public void setNullText(String nullText)
   {
      m_null = nullText;
   }

   /**
    * This method is used to configure the format pattern.
    *
    * @param patterns new format patterns
    */
   public void applyPatterns(String[] patterns)
   {
      m_formats = new SimpleDateFormat[patterns.length];
      for (int index = 0; index < patterns.length; index++)
      {
         m_formats[index] = new SimpleDateFormat(patterns[index]);
      }
   }

   /**
    * Allows the AM/PM text to be set.
    *
    * @param am AM text
    * @param pm PM text
    */
   public void setAmPmText(String am, String pm)
   {
      for (SimpleDateFormat format : m_formats)
      {
         DateFormatSymbols symbols = format.getDateFormatSymbols();
         symbols.setAmPmStrings(new String[]
         {
            am,
            pm
         });
         format.setDateFormatSymbols(symbols);
      }
   }

   @Override protected LocalDateTime parseNonNullDate(String str, ParsePosition pos)
   {
      LocalDateTime result;
      if (str.equals(m_null))
      {
         result = null;
         pos.setIndex(-1);
      }
      else
      {
         result = super.parseNonNullDate(str, pos);
      }
      return result;
   }

   protected String m_null = "NA";
}
