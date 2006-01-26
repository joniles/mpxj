/*
 * file:       TableFontStyle.java
 * author:     Jon Iles
 * copyright:  (c) Tapster Rock Limited 2005
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

package net.sf.mpxj.mpp;

import net.sf.mpxj.FieldType;

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
    * @param color color
    * @param italicChanged italic changed flag
    * @param boldChanged bold changed flag
    * @param underlineChanged underline changed flag
    * @param colorChanged color changed flag
    * @param fontChanged font changed flag
    */
   public TableFontStyle (int rowUniqueID, FieldType fieldType, FontBase fontBase, boolean italic, boolean bold, boolean underline, ColorType color, boolean italicChanged, boolean boldChanged, boolean underlineChanged, boolean colorChanged, boolean fontChanged)
   {
      super (fontBase, italic, bold, underline, color);

      m_rowUniqueID = rowUniqueID;
      m_fieldType = fieldType;

      m_italicChanged = italicChanged;
      m_boldChanged = boldChanged;
      m_underlineChanged = underlineChanged;
      m_colorChanged = colorChanged;
      m_fontChanged = fontChanged;
   }

   /**
    * Retrieves the unique ID of the entity shown on the row
    * affected by this style. This method will return -1 if the
    * style applies to all rows.
    *
    * @return row unique ID
    */
   public int getRowUniqueID ()
   {
      return  (m_rowUniqueID);
   }

   /**
    * Retrieve the field type of the column to which this style applies.
    *
    * @return field type
    */
   public FieldType getFieldType ()
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
    * Retrieve the font changed flag.
    *
    * @return boolean flag
    */
   public boolean getFontChanged()
   {
      return (m_fontChanged);
   }

   /**
    * {@inheritDoc}
    */
   public String toString ()
   {
      return ("[ColumnFontStyle rowUniqueID=" + m_rowUniqueID + " fieldType=" + m_fieldType + (m_italicChanged?" italic="+getItalic():"") + (m_boldChanged?" bold="+getBold():"") + (m_underlineChanged?" underline="+getUnderline():"") + (m_fontChanged?" font="+getFontBase():"") + (m_colorChanged?" color="+getColor():"") + "]");
   }

   private int m_rowUniqueID;
   private FieldType m_fieldType;
   private boolean m_italicChanged;
   private boolean m_boldChanged;
   private boolean m_underlineChanged;
   private boolean m_colorChanged;
   private boolean m_fontChanged;
}
