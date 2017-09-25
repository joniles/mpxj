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

package net.sf.mpxj.common;

import java.text.DateFormat;
import java.text.FieldPosition;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * This class wraps the functionality provided by the SimpleDateFormat class
 * to make it suitable for use with the time conventions used in MPX files.
 */
public class MultiDateFormat extends DateFormat
{
   /**
    * Constructor. Optionally allows multiple patterns to be passed.
    *
    * @param patterns date format patterns
    */
   public MultiDateFormat(String... patterns)
   {
      m_formats = new SimpleDateFormat[patterns.length];
      for (int index = 0; index < patterns.length; index++)
      {
         m_formats[index] = new SimpleDateFormat(patterns[index]);
      }
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
         result = parseNonNullDate(str, pos);
      }

      return result;
   }

   /**
    * We have a non-null date, try each format in turn to see if it can be parsed.
    *
    * @param str date to parse
    * @param pos position at which to start parsing
    * @return Date instance
    */
   protected Date parseNonNullDate(String str, ParsePosition pos)
   {
      Date result = null;
      for (int index = 0; index < m_formats.length; index++)
      {
         result = m_formats[index].parse(str, pos);
         if (pos.getIndex() != 0)
         {
            break;
         }
         result = null;
      }
      return result;
   }

   /**
    * {@inheritDoc}
    */
   @Override public StringBuffer format(Date date, StringBuffer toAppendTo, FieldPosition fieldPosition)
   {
      return (m_formats[0].format(date, toAppendTo, fieldPosition));
   }

   protected SimpleDateFormat[] m_formats;
}
