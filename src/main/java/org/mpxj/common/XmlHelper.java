/*
 * file:       XmlHelper.java
 * author:     Jon Iles
 * copyright:  (c) Packwood Software 2022
 * date:       01/12/2022
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

package org.mpxj.common;

/**
 * Helper methods for working with XML.
 */
public final class XmlHelper
{
   /**
    * Returns true if this is a valid XML 1.0 character.
    *
    * @param c character value
    * @return true if this is a valid XML 1.0 character
    */
   public static boolean validXmlChar(int c)
   {
      return c == 0x9 || c == 0xA || c == 0xD || (c >= 0x20 && c <= 0xD7FF) || (c >= 0xE000 && c <= 0xFFFD) || (c >= 0x10000 && c <= 0x10FFFF);
   }

   /**
    * Return a string with any character invalid for XML 1.0 replaced
    * with the Unicode replacement character.
    *
    * @param value string to clean up
    * @return string without invalid characters
    */
   public static String replaceInvalidXmlChars(String value)
   {
      if (value == null || value.isEmpty())
      {
         return value;
      }

      for (int index = 0; index < value.length(); index++)
      {
         if (!validXmlChar(value.charAt(index)))
         {
            return replaceInvalidXmlChars(value, index);
         }
      }

      return value;
   }

   private static String replaceInvalidXmlChars(String value, int firstInvalidIndex)
   {
      StringBuilder sb = firstInvalidIndex == 0 ? new StringBuilder() : new StringBuilder(value.substring(0, firstInvalidIndex));
      for (int index = firstInvalidIndex; index < value.length(); index++)
      {
         char c = value.charAt(index);
         sb.append(validXmlChar(c) ? c : REPLACEMENT_CHAR);
      }
      return sb.toString();
   }

   public static final int REPLACEMENT_CHAR = 0xFFFD;
}
