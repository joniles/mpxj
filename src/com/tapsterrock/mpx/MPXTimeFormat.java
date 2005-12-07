/*
 * file:       MPXTimeFormat.java
 * author:     Scott Melville
 *             Jon Iles
 * copyright:  (c) Tapster Rock Limited 2002-2003
 * date:       15/8/2002
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

package com.tapsterrock.mpx;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * This class wraps the functionality provided by the SimpleDateFormat class
 * to make it suitable for use with the time conventions used in MPX files.
 */
final class MPXTimeFormat extends SimpleDateFormat
{
   /**
    * This method is called when the locale of the parent file is updated.
    * It resets the locale specific time attributes to the default values
    * for the new locale.
    *
    * @param locale new locale
    */
   void setLocale (Locale locale)
   {
      m_null = LocaleData.getString(locale, LocaleData.NA);
   }

   /**
    * {@inheritDoc}
    */
   public Date parse (String str)
      throws ParseException
   {
      MPXTime result;

      if (str == null || str.trim().length() == 0)
      {
         result = null;
      }
      else
      {
         if (str.equals(m_null) == true)
         {
            result = null;
         }
         else
         {
            result = new MPXTime (this, super.parse(str));
         }
      }

      return (result);
   }

   private String m_null = "NA";
}
