/*
 * file:       GanttBarCommonStyle.java
 * author:     Jon Iles
 * copyright:  (c) Packwood Software 2005
 * date:       2005-04-13
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
import java.io.ByteArrayOutputStream;
import java.io.PrintWriter;

import org.mpxj.FieldType;

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
   public FieldType getBottomText()
   {
      return (m_bottomText);
   }

   /**
    * Sets the text appearing at the bottom of the bar.
    *
    * @param field bottom text
    */
   public void setBottomText(FieldType field)
   {
      m_bottomText = field;
   }

   /**
    * Retrieve the color of the end of the bar.
    *
    * @return end color
    */
   public Color getEndColor()
   {
      return (m_endColor);
   }

   /**
    * Sets the color of the end of the bar.
    *
    * @param color end color
    */
   public void setEndColor(Color color)
   {
      m_endColor = color;
   }

   /**
    * Retrieve the text appearing inside the Gantt bar.
    *
    * @return inside text
    */
   public FieldType getInsideText()
   {
      return (m_insideText);
   }

   /**
    * Sets the text appearing inside the Gantt bar.
    *
    * @param field inside text
    */
   public void setInsideText(FieldType field)
   {
      m_insideText = field;
   }

   /**
    * Retrieve the text appearing to the left of the bar.
    *
    * @return left text
    */
   public FieldType getLeftText()
   {
      return (m_leftText);
   }

   /**
    * Sets the text appearing to the left of the bar.
    *
    * @param field left text
    */
   public void setLeftText(FieldType field)
   {
      m_leftText = field;
   }

   /**
    * Retrieve the color of the middle section of the bar.
    *
    * @return middle color
    */
   public Color getMiddleColor()
   {
      return (m_middleColor);
   }

   /**
    * Sets the color of the middle section of the bar.
    *
    * @param color middle color
    */
   public void setMiddleColor(Color color)
   {
      m_middleColor = color;
   }

   /**
    * Retrieve the pattern appearing in the middle section of the bar.
    *
    * @return middle pattern
    */
   public ChartPattern getMiddlePattern()
   {
      return (m_middlePattern);
   }

   /**
    * Sets the pattern appearing in the middle section of the bar.
    *
    * @param pattern middle pattern
    */
   public void setMiddlePattern(ChartPattern pattern)
   {
      m_middlePattern = pattern;
   }

   /**
    * Retrieve the shape of the middle section of the bar.
    *
    * @return middle shape
    */
   public GanttBarMiddleShape getMiddleShape()
   {
      return (m_middleShape);
   }

   /**
    * Sets the shape of the middle section of the bar.
    *
    * @param shape middle shape
    */
   public void setMiddleShape(GanttBarMiddleShape shape)
   {
      m_middleShape = shape;
   }

   /**
    * Retrieve the text appearing to the right of the bar.
    *
    * @return right text
    */
   public FieldType getRightText()
   {
      return (m_rightText);
   }

   /**
    * Sets the text appearing to the right of the bar.
    *
    * @param field right text
    */
   public void setRightText(FieldType field)
   {
      m_rightText = field;
   }

   /**
    * Retrieve the color of the start of the bar.
    *
    * @return start color
    */
   public Color getStartColor()
   {
      return m_startColor;
   }

   /**
    * Sets the color of the start of the bar.
    *
    * @param color start color
    */
   public void setStartColor(Color color)
   {
      m_startColor = color;
   }

   /**
    * Retrieve the bar start shape.
    *
    * @return bar start shape
    */
   public GanttBarStartEndShape getStartShape()
   {
      return m_startShape;
   }

   /**
    * Sets the bar start shape.
    *
    * @param shape start shape
    */
   public void setStartShape(GanttBarStartEndShape shape)
   {
      m_startShape = shape;
   }

   /**
    * Retrieve the bar end shape.
    *
    * @return bar end shape
    */
   public GanttBarStartEndShape getEndShape()
   {
      return m_endShape;
   }

   /**
    * Sets the bar end shape.
    *
    * @param shape end shape
    */
   public void setEndShape(GanttBarStartEndShape shape)
   {
      m_endShape = shape;
   }

   /**
    * Retrieve the bar start type.
    *
    * @return bar start type
    */
   public GanttBarStartEndType getStartType()
   {
      return m_startType;
   }

   /**
    * Sets the bar start type.
    *
    * @param type bar start type
    */
   public void setStartType(GanttBarStartEndType type)
   {
      m_startType = type;
   }

   /**
    * Retrieve the bar end type.
    *
    * @return bar end type
    */
   public GanttBarStartEndType getEndType()
   {
      return m_endType;
   }

   /**
    * Sets the bar end type.
    *
    * @param type bar end type
    */
   public void setEndType(GanttBarStartEndType type)
   {
      m_endType = type;
   }

   /**
    * Retrieve the text which appears above the bar.
    *
    * @return top text
    */
   public FieldType getTopText()
   {
      return (m_topText);
   }

   /**
    * Sets the top text.
    *
    * @param field top text
    */
   public void setTopText(FieldType field)
   {
      m_topText = field;
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
      pw.println("      StartShape=" + m_startShape);
      pw.println("      StartType=" + m_startType);
      pw.println("      StartColor=" + m_startColor);
      pw.println("      MiddleShape=" + m_middleShape);
      pw.println("      MiddlePattern=" + m_middlePattern);
      pw.println("      MiddleColor=" + m_middleColor);
      pw.println("      EndShape=" + m_endShape);
      pw.println("      EndType=" + m_endType);
      pw.println("      EndColor=" + m_endColor);
      pw.println("      LeftText=" + m_leftText);
      pw.println("      RightText=" + m_rightText);
      pw.println("      TopText=" + m_topText);
      pw.println("      BottomText=" + m_bottomText);
      pw.println("      InsideText=" + m_insideText);
      pw.flush();
      return (os.toString());
   }

   private GanttBarStartEndShape m_startShape;
   private GanttBarStartEndType m_startType;
   private Color m_startColor;

   private GanttBarMiddleShape m_middleShape;
   private ChartPattern m_middlePattern;
   private Color m_middleColor;

   private GanttBarStartEndShape m_endShape;
   private GanttBarStartEndType m_endType;
   private Color m_endColor;

   private FieldType m_leftText;
   private FieldType m_rightText;
   private FieldType m_topText;
   private FieldType m_bottomText;
   private FieldType m_insideText;
}
