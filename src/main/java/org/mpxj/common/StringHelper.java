/*
 * file:       StringHelper.java
 * author:     Jon Iles
 * date:       2023-10-16
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
 * Helper methods relating to strings.
 */
public final class StringHelper
{
   /**
    * Strip control characters from the supplied text.
    *
    * @param text text to strip
    * @return text without control characters
    */
   public static String stripControlCharacters(String text)
   {
      if (text == null || text.isEmpty())
      {
         return text;
      }

      int index = 0;
      while (index < text.length())
      {
         if (Character.isISOControl(text.charAt(index)))
         {
            return stripControlCharacters(text, index);
         }
         ++index;
      }

      return text;
   }

   /**
    * Strip control characters from the supplied text.
    * index represents the first control character in the text.
    *
    * @param text text to strip
    * @param index index of first control character
    * @return text without control characters
    */
   private static String stripControlCharacters(String text, int index)
   {
      StringBuilder sb = new StringBuilder();
      if (index != 0)
      {
         sb.append(text, 0, index);
      }

      ++index;

      while (index < text.length())
      {
         char c = text.charAt(index);
         if (!Character.isISOControl(c))
         {
            sb.append(c);
         }
         ++index;
      }

      return sb.toString();
   }
}
