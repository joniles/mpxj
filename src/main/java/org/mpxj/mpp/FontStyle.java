/*
 * file:       FontStyle.java
 * author:     Jon Iles
 * copyright:  (c) Packwood Software 2005
 * date:       May 24, 2005
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
 * This class builds on the font described by a FontBase instance
 * and add attributes for color, bold, italic and underline.
 */
public class FontStyle
{
   /**
    * Constructor.
    *
    * @param fontBase font base instance
    * @param italic italic flag
    * @param bold bold flag
    * @param underline underline flag
    * @param strikethrough strikethrough flag
    * @param color color type
    * @param backgroundColor background color
    * @param backgroundPattern background pattern
    */
   public FontStyle(FontBase fontBase, boolean italic, boolean bold, boolean underline, boolean strikethrough, Color color, Color backgroundColor, BackgroundPattern backgroundPattern)
   {
      m_fontBase = fontBase;
      m_italic = italic;
      m_bold = bold;
      m_underline = underline;
      m_strikethrough = strikethrough;
      m_color = color;
      m_backgroundColor = backgroundColor;
      m_backgroundPattern = backgroundPattern;
   }

   /**
    * Retrieve the font base instance.
    *
    * @return font base instance
    */
   public FontBase getFontBase()
   {
      return (m_fontBase);
   }

   /**
    * Retrieve the bold flag.
    *
    * @return bold flag
    */
   public boolean getBold()
   {
      return (m_bold);
   }

   /**
    * Retrieve the font color.
    *
    * @return font color
    */
   public Color getColor()
   {
      return (m_color);
   }

   /**
    * Retrieve the italic flag.
    *
    * @return italic flag
    */
   public boolean getItalic()
   {
      return (m_italic);
   }

   /**
    * Retrieve the underline flag.
    *
    * @return underline flag
    */
   public boolean getUnderline()
   {
      return (m_underline);
   }

   /**
    * Retrieve the strikethrough flag.
    *
    * @return strikethrough flag
    */
   public boolean getStrikethrough()
   {
      return (m_strikethrough);
   }

   /**
    * Retrieve the background color.
    *
    * @return background color
    */
   public Color getBackgroundColor()
   {
      return m_backgroundColor;
   }

   /**
    * Retrieve the background pattern.
    *
    * @return background pattern
    */
   public BackgroundPattern getBackgroundPattern()
   {
      return m_backgroundPattern;
   }

   @Override public String toString()
   {
      return ("[FontStyle fontBase=" + m_fontBase + " italic=" + m_italic + " bold=" + m_bold + " underline=" + m_underline + " strikethrough=" + m_strikethrough + " color=" + m_color + " backgroundColor=" + m_backgroundColor + " backgroundPattern=" + m_backgroundPattern + "]");
   }

   private final FontBase m_fontBase;
   private final boolean m_italic;
   private final boolean m_bold;
   private final boolean m_underline;
   private final boolean m_strikethrough;
   private final Color m_color;
   private final Color m_backgroundColor;
   private final BackgroundPattern m_backgroundPattern;
}
