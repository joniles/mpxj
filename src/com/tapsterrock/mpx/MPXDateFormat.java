/*
 * file:       MPXDateFormat.java
 * author:     Scott Melville
 *             Jon Iles
 * copyright:  (c) Tapster Rock Limited 2002-2003
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

package com.tapsterrock.mpx;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.text.ParseException;

/**
 * This class wraps the functionality provided by the SimpleDateFormat class
 * to make it suitable for use with the date conventions used in MPX files.
 */
final class MPXDateFormat
{
   /**
    * This method is used to configure the format pattern.
    *
    * @param pattern new format pattern
    */
   public void applyPattern (String pattern)
   {
      m_format.applyPattern (pattern);
   }

   /**
    * This method returns a String containing the formatted version
    * of the date parameter.
    *
    * @param date date to be formatted
    * @return formatted date
    */
   public String format (Date date)
   {
      return (m_format.format(date));
   }


   /**
    * This method parses a String representation of a date and returns
    * an MPXDate object.
    *
    * @param str String representation of a date
    * @return MPXDate object
    * @throws MPXException Thrown on parse errors
    */
   public MPXDate parse (String str)
      throws MPXException
   {
      MPXDate result;

      if (str == null || str.equals("") == true)
      {
         result = null;
      }
      else
      {
         if (str.equals("NA") == true)
         {
            result = null;
         }
         else
         {
            try
            {
               result = new MPXDate (this, m_format.parse(str));
            }

            catch (ParseException ex)
            {
               throw new MPXException (MPXException.INVALID_DATE + " " + str, ex);
            }
         }
      }

      return (result);
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
   private SimpleDateFormat m_format = new SimpleDateFormat ("dd/MM/yyyy", Locale.ENGLISH);
}
