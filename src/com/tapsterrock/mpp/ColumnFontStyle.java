/*
 * file:       ColumnFontStyle.java
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
 * to apply a style to a particular column of a table.
 */
public class ColumnFontStyle extends FontStyle
{
   /**
    * Constructor.
    * 
    * @param fieldType field type of the table column
    * @param fontBase font base
    * @param italic italic flag
    * @param bold bold flag
    * @param underline underline flag
    * @param color color
    */
   public ColumnFontStyle (FieldType fieldType, FontBase fontBase, boolean italic, boolean bold, boolean underline, ColorType color)
   {
      super (fontBase, italic, bold, underline, color);
      
      m_fieldType = fieldType;
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
      return ("[ColumnFontStyle fieldType=" + m_fieldType + " " + super.toString() + "]");
   }
   
   private FieldType m_fieldType;
}
