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

package com.tapsterrock.mpp;

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
    */
   public TableFontStyle (int rowUniqueID, FieldType fieldType, FontBase fontBase, boolean italic, boolean bold, boolean underline, ColorType color)
   {
      super (fontBase, italic, bold, underline, color);
      
      m_rowUniqueID = rowUniqueID;
      m_fieldType = fieldType;
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
    * @see java.lang.Object#toString()
    */   
   public String toString ()
   {
      return ("[ColumnFontStyle rowUniqueID=" + m_rowUniqueID + " fieldType=" + m_fieldType + " " + super.toString() + "]");
   }
   
   private int m_rowUniqueID;
   private FieldType m_fieldType;
}
