/*
 * file:       MPXJTimeFormat.java
 * author:     Jon Iles
 *             Scott Melville
 * copyright:  (c) Packwood Software 2002-2006
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

package net.sf.mpxj.utility;

import java.text.DateFormatSymbols;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * This class wraps the functionality provided by the SimpleDateFormat class
 * to make it suitable for use with the time conventions used in MPX files.
 */
public final class MPXJTimeFormat extends SimpleDateFormat
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
    * {@inheritDoc}
    */
   @Override public Date parse(String str) throws ParseException
   {
      Date result;

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
            result = super.parse(str);
         }
      }

      return (result);
   }

   /**
    * Allows the AM/PM text to be set.
    * 
    * @param am AM text
    * @param pm PM text
    */
   public void setAmPmText(String am, String pm)
   {
      DateFormatSymbols symbols = getDateFormatSymbols();
      symbols.setAmPmStrings(new String[]
      {
         am,
         pm
      });
      setDateFormatSymbols(symbols);
   }

   private String m_null = "NA";
}
