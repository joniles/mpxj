/*
 * file:       Column.java
 * author:     Jon Iles
 * copyright:  (c) Packwood Software 2003
 * date:       02/11/2003
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

package org.mpxj;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Locale;

/**
 * This class represents a column in an MS Project table. The attributes held
 * here describe the layout of the column, along with the title text that has
 * been associated with the column. The title text will either be the default
 * value supplied by MS Project, or it will be a user defined value.
 */
public final class Column
{
   /**
    * Constructor.
    *
    * @param project reference to the parent project
    */
   public Column(ProjectFile project)
   {
      m_project = project;
   }

   /**
    * Retrieves a value representing the alignment of data displayed in
    * the column.
    *
    * @return alignment type
    */
   public int getAlignData()
   {
      return (m_alignData);
   }

   /**
    * Retrieves a value representing the alignment of the column title text.
    *
    * @return alignment type
    */
   public int getAlignTitle()
   {
      return (m_alignTitle);
   }

   /**
    * Retrieves the type data displayed in the column. This identifier indicates
    * what data will appear in the column, and the default column title
    * that will appear if the user has not provided a user defined column title.
    *
    * @return field type
    */
   public FieldType getFieldType()
   {
      return (m_fieldType);
   }

   /**
    * Retrieves the column title.
    *
    * @return column title
    */
   public String getTitle()
   {
      return (getTitle(Locale.getDefault()));
   }

   /**
    * Retrieves the column title for the given locale.
    *
    * @param locale required locale for the default column title
    * @return column title
    */
   public String getTitle(Locale locale)
   {
      String result = null;

      if (m_title != null)
      {
         result = m_title;
      }
      else
      {
         if (m_fieldType != null)
         {
            CustomField cf = m_project.getCustomFields().get(m_fieldType);
            result = cf == null ? null : cf.getAlias();
            if (result == null || result.isEmpty())
            {
               result = m_fieldType.getName(locale);
            }
         }
      }

      return (result);
   }

   /**
    * Retrieves the width of the column represented as a number of
    * characters.
    *
    * @return column width
    */
   public int getWidth()
   {
      return m_width;
   }

   /**
    * Sets the alignment of the data in the column.
    *
    * @param alignment data alignment
    */
   public void setAlignData(int alignment)
   {
      m_alignData = alignment;
   }

   /**
    * Sets the alignment of the column title.
    *
    * @param alignment column title alignment
    */
   public void setAlignTitle(int alignment)
   {
      m_alignTitle = alignment;
   }

   /**
    * Sets the type data displayed in the column. This identifier indicates
    * what data will appear in the column, and the default column title
    * that will appear if the user has not provided a user defined column title.
    *
    * @param type field type
    */
   public void setFieldType(FieldType type)
   {
      m_fieldType = type;
   }

   /**
    * Sets the user defined column title.
    *
    * @param title user defined column title
    */
   public void setTitle(String title)
   {
      m_title = title;
   }

   /**
    * Sets the width of the column in characters.
    *
    * @param width column width
    */
   public void setWidth(int width)
   {
      m_width = width;
   }

   /**
    * This method dumps the contents of this column as a String.
    * Note that this facility is provided as a debugging aid.
    *
    * @return formatted contents of this column
    */
   @Override public String toString()
   {
      StringWriter sw = new StringWriter();
      PrintWriter pw = new PrintWriter(sw);

      pw.print("[Column type=");
      pw.print(m_fieldType);

      pw.print(" width=");
      pw.print(m_width);

      pw.print(" titleAlignment=");
      if (m_alignTitle == ALIGN_LEFT)
      {
         pw.print("LEFT");
      }
      else
      {
         if (m_alignTitle == ALIGN_CENTER)
         {
            pw.print("CENTER");
         }
         else
         {
            pw.print("RIGHT");
         }
      }

      pw.print(" dataAlignment=");
      if (m_alignData == ALIGN_LEFT)
      {
         pw.print("LEFT");
      }
      else
      {
         if (m_alignData == ALIGN_CENTER)
         {
            pw.print("CENTER");
         }
         else
         {
            pw.print("RIGHT");
         }
      }

      pw.print(" title=");
      pw.print(getTitle());
      pw.println("]");
      pw.close();

      return (sw.toString());
   }

   /**
    * Column alignment constants.
    */
   public static final int ALIGN_LEFT = 1;
   public static final int ALIGN_CENTER = 2;
   public static final int ALIGN_RIGHT = 3;

   private FieldType m_fieldType;
   private int m_width;
   private int m_alignTitle;
   private int m_alignData;
   private String m_title;
   private final ProjectFile m_project;
}
