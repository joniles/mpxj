/*
 * file:       MPXNumberFormat.java
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

import java.text.DecimalFormat;
import java.text.ParseException;

/**
 * This class is used to provide a simple wrapper around the functionality
 * of the DecimalFormat class. In particuar this class handles ParseExceptions
 * and wraps them in an MPXException.
 */
final class MPXNumberFormat
{
   /**
    * Default constructor.
    */
   MPXNumberFormat ()
   {

   }

   /**
    * Constructor allowing format pattern to be supplied.
    *
    * @param pattern new format pattern
    */
   MPXNumberFormat (String pattern)
   {
      m_format.applyPattern (pattern);
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
    * of the number parameter.
    *
    * @param number number to be formatted
    * @return formatted number
    */
   public String format (float number)
   {
      return (m_format.format(number));
   }

   /**
    * This method returns a String containing the formatted version
    * of the number parameter.
    *
    * @param number number to be formatted
    * @return formatted number
    */
   public String format (double number)
   {
      return (m_format.format(number));
   }

   /**
    * This method parses a String representation of a number and returns
    * an Number object.
    *
    * @param str String representation of a number
    * @return Number object
    * @throws MPXException when the string parse fails
    */
   public Number parse (String str)
     throws MPXException
   {
      try
      {
         return m_format.parse (str);
      }

      catch (ParseException ex)
      {
         throw new MPXException (MPXException.INVALID_NUMBER + " " + str);
      }
   }

   /**
    * Number formatter.
    */
   private DecimalFormat m_format = new DecimalFormat ();
}