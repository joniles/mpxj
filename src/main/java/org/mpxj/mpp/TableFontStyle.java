/*
 * file:       TableFontStyle.java
 * author:     Jon Iles
 * copyright:  (c) Packwood Software 2005
 * date:       Jun 23, 2005
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

import org.mpxj.FieldType;

/**
 * This class builds on the font style described by a FontStyle instance
 * to apply a style to a column, row, or individual cell of a table.
 */
public class TableFontStyle extends FontStyle
{
   /**
    * Constructor.
    *
    * @param rowUniqueID unique ID of the entity shown on the row
    * @param fieldType field type of the table column
    * @param fontBase font base
    * @param italic italic flag
    * @param bold bold flag
    * @param underline underline flag
    * @param strikethrough strikethrough flag
    * @param color color
    * @param backgroundColor background color
    * @param backgroundPattern background pattern
    * @param italicChanged italic changed flag
    * @param boldChanged bold changed flag
    * @param underlineChanged underline changed flag
    * @param strikethroughChanged strikethrough changed flag
    * @param colorChanged color changed flag
    * @param fontChanged font changed flag
    * @param backgroundColorChanged background color changed
    * @param backgroundPatternChanged background pattern changed
    */
   public TableFontStyle(int rowUniqueID, FieldType fieldType, FontBase fontBase, boolean italic, boolean bold, boolean underline, boolean strikethrough, Color color, Color backgroundColor, BackgroundPattern backgroundPattern, boolean italicChanged, boolean boldChanged, boolean underlineChanged, boolean strikethroughChanged, boolean colorChanged, boolean fontChanged, boolean backgroundColorChanged, boolean backgroundPatternChanged)
   {
      super(fontBase, italic, bold, underline, strikethrough, color, backgroundColor, backgroundPattern);

      m_rowUniqueID = rowUniqueID;
      m_fieldType = fieldType;

      m_italicChanged = italicChanged;
      m_boldChanged = boldChanged;
      m_underlineChanged = underlineChanged;
      m_strikethroughChanged = strikethroughChanged;
      m_colorChanged = colorChanged;
      m_fontChanged = fontChanged;
      m_backgroundColorChanged = backgroundColorChanged;
      m_backgroundPatternChanged = backgroundPatternChanged;
   }

   /**
    * Retrieves the unique ID of the entity shown on the row
    * affected by this style. This method will return -1 if the
    * style applies to all rows.
    *
    * @return row unique ID
    */
   public int getRowUniqueID()
   {
      return (m_rowUniqueID);
   }

   /**
    * Retrieve the field type of the column to which this style applies.
    *
    * @return field type
    */
   public FieldType getFieldType()
   {
      return (m_fieldType);
   }

   /**
    * Retrieve the bold changed flag.
    *
    * @return boolean flag
    */
   public boolean getBoldChanged()
   {
      return (m_boldChanged);
   }

   /**
    * Retrieve the color changed flag.
    *
    * @return boolean flag
    */
   public boolean getColorChanged()
   {
      return (m_colorChanged);
   }

   /**
    * Retrieve the italic change flag.
    *
    * @return boolean flag
    */
   public boolean getItalicChanged()
   {
      return (m_italicChanged);
   }

   /**
    * Retrieve the underline changed flag.
    *
    * @return boolean flag
    */
   public boolean getUnderlineChanged()
   {
      return (m_underlineChanged);
   }

   /**
    * Retrieve the strikethrough changed flag.
    *
    * @return boolean flag
    */
   public boolean getStrikethroughChanged()
   {
      return (m_strikethroughChanged);
   }

   /**
    * Retrieve the font changed flag.
    *
    * @return boolean flag
    */
   public boolean getFontChanged()
   {
      return (m_fontChanged);
   }

   /**
    * Retrieve the background color changed flag.
    *
    * @return boolean flag
    */
   public boolean getBackgroundColorChanged()
   {
      return (m_backgroundColorChanged);
   }

   /**
    * Retrieve the background pattern changed flag.
    *
    * @return boolean flag
    */
   public boolean getBackgroundPatternChanged()
   {
      return (m_backgroundPatternChanged);
   }

   @Override public String toString()
   {
      return ("[ColumnFontStyle rowUniqueID=" + m_rowUniqueID + " fieldType=" + m_fieldType + (m_italicChanged ? " italic=" + getItalic() : "") + (m_boldChanged ? " bold=" + getBold() : "") + (m_underlineChanged ? " underline=" + getUnderline() : "") + (m_strikethroughChanged ? " strikethrough=" + getStrikethrough() : "") + (m_fontChanged ? " font=" + getFontBase() : "") + (m_colorChanged ? " color=" + getColor() : "") + (m_backgroundColorChanged ? " backgroundColor=" + getBackgroundColor() : "") + (m_backgroundPatternChanged ? " backgroundPattern=" + getBackgroundPattern() : "") + "]");
   }

   private final int m_rowUniqueID;
   private final FieldType m_fieldType;
   private final boolean m_italicChanged;
   private final boolean m_boldChanged;
   private final boolean m_underlineChanged;
   private final boolean m_strikethroughChanged;
   private final boolean m_colorChanged;
   private final boolean m_fontChanged;
   private final boolean m_backgroundColorChanged;
   private final boolean m_backgroundPatternChanged;
}
