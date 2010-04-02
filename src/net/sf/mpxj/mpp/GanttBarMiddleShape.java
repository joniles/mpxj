/*
 * file:       GanttBarMiddleShape.java
 * author:     Tom Ollar
 * copyright:  (c) Packwood Software 2009
 * date:       04/04/2009
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
 * Represents the shape used for the middle section of a Gantt bar.
 */
public class GanttBarMiddleShape
{
   /**
    * Retrieve an instance of GanttBarShape based on the shape and style code.
    *
    * @param type middle shape type
    * @param pattern middle shape pattern
    * @return GanttBarShape
    */
   public static GanttBarMiddleShape getInstance(int type, int pattern)
   {
      return new GanttBarMiddleShape(type, pattern);
   }

   /**
    * Create an instance of GanttBarShape that represents 
    * the shape and style code.
    *
    * @param type middle shape type
    * @param pattern middle shape pattern
    */
   public GanttBarMiddleShape(int type, int pattern)
   {
      m_type = GanttBarMiddleShapeType.getInstance(type);
      m_pattern = GanttBarMiddleShapePattern.getInstance(pattern);
   }

   /**
    * Return the shape type for this Gantt bar.
    *
    * @return GanttBarShapeType
    */
   public GanttBarMiddleShapeType getType()
   {
      return m_type;
   }

   /**
    * Return the shape style for this Gantt bar.
    *
    * @return GanttBarShapeStyle
    */
   public GanttBarMiddleShapePattern getPattern()
   {
      return m_pattern;
   }

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
    * @param colorType  the color
    */
   public void setColor(ColorType colorType)
   {
      m_color = colorType;
   }

   private GanttBarMiddleShapeType m_type;
   private GanttBarMiddleShapePattern m_pattern;
   private ColorType m_color;
}
