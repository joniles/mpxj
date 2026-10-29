/*
 * file:       ColorType.java
 * author:     Jon Iles
 * copyright:  (c) Packwood Software 2005
 * date:       Apr 7, 2005
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

package org.mpxj.mpp;

import java.awt.Color;

/**
 * This enum represents the colors used by Microsoft Project.
 */
public enum ColorType
{
   BLACK(Color.BLACK),
   RED(Color.RED),
   YELLOW(Color.YELLOW),
   LIME(new Color(148, 255, 148)),
   AQUA(new Color(194, 220, 255)),
   BLUE(Color.BLUE),
   FUSCHIA(Color.MAGENTA),
   WHITE(Color.WHITE),
   MAROON(new Color(128, 0, 0)),
   GREEN(new Color(0, 128, 0)),
   OLIVE(new Color(128, 128, 0)),
   NAVY(new Color(0, 0, 128)),
   PURPLE(new Color(128, 0, 128)),
   TEAL(new Color(0, 128, 128)),
   GRAY(new Color(128, 128, 128)),
   SILVER(new Color(192, 192, 192)),
   AUTOMATIC(null);

   /**
    * Private constructor.
    *
    * @param color Java color instance
    */
   ColorType(Color color)
   {
      m_color = color;
   }

   /**
    * Retrieve a Java Color instance matching the color used in MS Project.
    * Note that this will return null if the color type is automatic.
    *
    * @return Color instance
    */
   public Color getColor()
   {
      return m_color;
   }

   private final Color m_color;
}
