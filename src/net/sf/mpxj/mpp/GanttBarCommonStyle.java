/*
 * file:       GanttBarCommonStyle.java
 * author:     Jon Iles
 * copyright:  (c) Packwood Software Limited 2005
 * date:       Apr 13, 2005
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

import java.io.ByteArrayOutputStream;
import java.io.PrintWriter;

import net.sf.mpxj.TaskField;

/**
 * This class represents common elements of the Gantt char bar styles
 * shared between the normal styles, and the individual task bar
 * exception styles.
 */
public class GanttBarCommonStyle
{
   /**
    * Retrieve the text appearing at the bottom of the bar.
    *
    * @return bottom text
    */
   public TaskField getBottomText()
   {
      return (m_bottomText);
   }

   /**
    * Retrieve the color of the end of the bar.
    *
    * @return end color
    */
   public ColorType getEndColor()
   {
      return (m_endColor);
   }

   /**
    * Retrieve the shape and style of the end of the bar.
    *
    * @return end shape and style
    */
   public int getEndShapeAndStyle()
   {
      return (m_endShapeAndStyle);
   }

   /**
    * Retrieve the text appearing inside the Gantt bar.
    *
    * @return inside text
    */
   public TaskField getInsideText()
   {
      return (m_insideText);
   }

   /**
    * Retrieve the text appearing to the left of the bar.
    *
    * @return left text
    */
   public TaskField getLeftText()
   {
      return (m_leftText);
   }

   /**
    * Retrieve the color of the middle section of the bar.
    *
    * @return middle color
    */
   public ColorType getMiddleColor()
   {
      return (m_middleColor);
   }

   /**
    * Retrieve the pattern appearing in the middle section of the bar.
    *
    * @return middle pattern
    */
   public int getMiddlePattern()
   {
      return (m_middlePattern);
   }

   /**
    * Retrieve the shape of the middle section of the bar.
    *
    * @return middle shape
    */
   public int getMiddleShape()
   {
      return (m_middleShape);
   }

   /**
    * Retrieve the text appearing to the right of the bar.
    *
    * @return right text
    */
   public TaskField getRightText()
   {
      return (m_rightText);
   }

   /**
    * Retrieve the color of the start of the bar.
    *
    * @return start color
    */
   public ColorType getStartColor()
   {
      return m_startColor;
   }

   /**
    * Retrieve the shae and style of the start of the bar.
    *
    * @return start shape and style
    */
   public int getStartShapeAndStyle()
   {
      return (m_startShapeAndStyle);
   }

   /**
    * Retrieve the text which appears above the bar.
    *
    * @return top text
    */
   public TaskField getTopText()
   {
      return (m_topText);
   }

   /**
    * Generate a string representation of this instance.
    *
    * @return string representation of this instance
    */
   @Override public String toString()
   {
      ByteArrayOutputStream os = new ByteArrayOutputStream();
      PrintWriter pw = new PrintWriter(os);
      pw.println("      MiddleShape=" + m_middleShape);
      pw.println("      MiddlePattern=" + m_middlePattern);
      pw.println("      MiddleColor=" + m_middleColor);
      pw.println("      StartShapeAndStyle=" + m_startShapeAndStyle);
      pw.println("      StartColor=" + m_startColor);
      pw.println("      EndShapeAndStyle=" + m_endShapeAndStyle);
      pw.println("      EndColor=" + m_endColor);
      pw.println("      LeftText=" + m_leftText);
      pw.println("      RightText=" + m_rightText);
      pw.println("      TopText=" + m_topText);
      pw.println("      BottomText=" + m_bottomText);
      pw.println("      InsideText=" + m_insideText);
      pw.flush();
      return (os.toString());
   }

   protected int m_middleShape;
   protected int m_middlePattern;
   protected ColorType m_middleColor;
   protected int m_startShapeAndStyle;
   protected ColorType m_startColor;
   protected int m_endShapeAndStyle;
   protected ColorType m_endColor;

   protected TaskField m_leftText;
   protected TaskField m_rightText;
   protected TaskField m_topText;
   protected TaskField m_bottomText;
   protected TaskField m_insideText;
}
