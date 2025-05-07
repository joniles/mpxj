/*
 * file:       ColorHelper.java
 * author:     Jon Iles
 * copyright:  (c) Packwood Software 2023
 * date:       21/03/2023
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

import java.awt.Color;

/**
 * Provides helper methods for working with colors.
 */
public class ColorHelper
{

   /**
    * Convert a color into an HTML color representation.
    *
    * @param color Color instance
    * @return HTML representation
    */
   public static String getHtmlColor(Color color)
   {
      if (color == null)
      {
         return null;
      }

      return "#" + getHexColor(color);
   }

   /**
    * Convert a color into a string representation as hex digits.
    *
    * @param color Color instance
    * @return string representation
    */
   public static String getHexColor(Color color)
   {
      if (color == null)
      {
         return null;
      }

      String result = "000000" + Integer.toHexString(color.getRGB()).toUpperCase();
      return result.substring(result.length() - 6);
   }

   /**
    * Create a Color instance by parsing a hex string representation of a color.
    *
    * @param value hex string representation of a color
    * @return Color instance
    */
   public static Color parseHexColor(String value)
   {
      Color result = null;
      if (value != null && !value.isEmpty())
      {
         result = new Color(Integer.parseInt(value, 16));
      }
      return result;
   }

   /**
    * Create a Color instance by parsing an HTML representation of a color.
    *
    * @param value HTML representation of a color
    * @return Color instance
    */
   public static Color parseHtmlColor(String value)
   {
      Color result = null;
      if (value != null && value.length() > 1 && value.charAt(0) == '#')
      {
         result = new Color(Integer.parseInt(value.substring(1), 16));
      }
      return result;
   }
}
