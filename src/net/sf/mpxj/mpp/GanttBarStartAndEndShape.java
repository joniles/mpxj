/*
 * file:       GanttBarStartAndEndShape.java
 * author:     Tom Ollar
 * copyright:  (c) Packwood Software 2009
 * date:       26/03/2009
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

/**
 * Represents the style of the start and end of a Gantt bar.  
 */
public class GanttBarStartAndEndShape
{
   /**
    * Retrieve the color.
    *
    * @return the color
    */
   public ColorType getColor()
   {
      return (m_color);
   }

   /**
    * Set the color.
    *
    * @param colorType the color
    */
   public void setColor(ColorType colorType)
   {
      m_color = colorType;
   }

   /**
    * Retrieve an instance of GanttBarShape based on the shape and style code.
    *
    * @param shapeAndStyleCode the raw code read from the mpp file
    * @return GanttBarShape
    */
   public static GanttBarStartAndEndShape getInstance(int shapeAndStyleCode)
   {
      return new GanttBarStartAndEndShape(shapeAndStyleCode);
   }

   /**
    * Convert a shape and style code to a GanttBarShapeStyle ordinal value.
    *
    * @param shapeAndStyleCode the raw code read from the mpp file
    * @return GanttBarShapeStyle ordinal value
    */
   private int getStyleValue(int shapeAndStyleCode)
   {
      int style;

      if (shapeAndStyleCode > 42)
      {
         style = 2;
      }
      else
      {
         if (shapeAndStyleCode > 21)
         {
            style = 1;
         }
         else
         {
            style = 0;
         }
      }

      return style;
   }

   /**
    * Convert a shape and style code to a GanttBarShapeType ordinal value.
    *
    * @param shapeAndStyleCode the raw code read from the mpp file
    * @return GanttBarShapeType ordinal value
    */
   private int getShapeValue(int shapeAndStyleCode)
   {
      int shape;

      if (shapeAndStyleCode > 42)
      {
         shape = shapeAndStyleCode - 42;
      }
      else
      {
         if (shapeAndStyleCode > 21)
         {
            shape = shapeAndStyleCode - 21;
         }
         else
         {
            shape = shapeAndStyleCode;
         }
      }

      return shape;
   }

   /**
    * Create an instance of GanttBarShape that represents 
    * the shape and style code.
    *
    * @param shapeAndStyleCode the raw code read from the mpp file
    */
   public GanttBarStartAndEndShape(int shapeAndStyleCode)
   {
      int shapeValue = getShapeValue(shapeAndStyleCode);
      int styleValue = getStyleValue(shapeAndStyleCode);

      m_type = GanttBarStartAndEndShapeType.getInstance(shapeValue);
      m_style = GanttBarStartAndEndShapeStyle.getInstance(styleValue);
   }

   /**
    * Return the shape type for this Gantt bar.
    *
    * @return GanttBarShapeType
    */
   public GanttBarStartAndEndShapeType getType()
   {
      return m_type;
   }

   /**
    * Return the shape style for this Gantt bar.
    *
    * @return GanttBarShapeStyle
    */
   public GanttBarStartAndEndShapeStyle getStyle()
   {
      return m_style;
   }

   private GanttBarStartAndEndShapeType m_type;
   private GanttBarStartAndEndShapeStyle m_style;
   private ColorType m_color;
}
