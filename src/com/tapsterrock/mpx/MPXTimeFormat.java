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

import java.text.SimpleDateFormat;
import java.util.Date;
import java.text.ParseException;

/**
 * This class wraps the functionality provided by the SimpleDateFormat class
 * to make it suitable for use with the time conventions used in MPX files.
 */
class MPXTimeFormat
{
   /**
    * Default constructor
    */
   public MPXTimeFormat()
   {
   }

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
    * of the time parameter.
    *
    * @param time date to be formatted
    * @return formatted time
    */
   public String format (Date time)
   {
      return (m_format.format(time));
   }


   /**
    * This method parses a String representation of a time and returns
    * an MPXTime object.
    *
    * @param str String representation of a time
    * @return MPXTime object
    * @throws MPXException Thrown on parse errors
    */
   public MPXTime parse (String str)
      throws MPXException
   {
      MPXTime result;

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
               result = new MPXTime (this, m_format.parse(str));
            }

            catch (ParseException ex)
            {
               throw new MPXException (MPXException.INVALID_TIME + str, ex);
            }
         }
      }

      return (result);
   }

   /**
    * Internal SimpleDateFormat object used to carry out the formatting work.
    */
   private SimpleDateFormat m_format = new SimpleDateFormat ();
}