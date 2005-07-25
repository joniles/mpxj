/*
 * file:       GridLines.java
 * author:     Jon Iles
 * copyright:  (c) Tapster Rock Limited 2005
 * date:       Apr 7, 2005
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
 * This class represents the set of properties that define the position
 * and appearance of a set of grid lines.
 */
public class GridLines
{
   /**
    * Constructor.
    * 
    * @param data properties data
    * @param offset offset into properties data
    */
   public GridLines (byte[] data, int offset)
   {
      m_normalLineColor = ColorType.getInstance(data[offset]);
      m_normalLineStyle = LineStyle.getInstance(data[offset+3]);
      m_intervalNumber = data[offset+4];
      m_intervalLineStyle = LineStyle.getInstance(data[offset+5]);
      m_intervalLineColor = ColorType.getInstance(data[offset+6]);
   }
   
   /**
    * Retrieve the interval line color.
    * 
    * @return interval line color
    */
   public ColorType getIntervalLineColor()
   {
      return (m_intervalLineColor);
   }
   
   /**
    * Retrieve the interval line style.
    * 
    * @return interval line style
    */
   public LineStyle getIntervalLineStyle()
   {
      return (m_intervalLineStyle);
   }
   
   /**
    * Retrieve the interval number
    * 
    * @return interval number
    */
   public int getIntervalNumber()
   {
      return (m_intervalNumber);
   }
   
   /**
    * Retrieve the normal line color.
    * 
    * @return line color
    */
   public ColorType getNormalLineColor()
   {
      return (m_normalLineColor);
   }
   
   /**
    * Retrieve the normal line style.
    * 
    * @return line style
    */
   public LineStyle getNormalLineStyle()
   {
      return m_normalLineStyle;
   }
   
   /**
    * Generate a string representation of this instance.
    * 
    * @return string representation of this instance
    */
   public String toString ()
   {
      return ("[GridLines NormalLineColor=" + m_normalLineColor+" NormalLineStyle=" + m_normalLineStyle + " IntervalNumber=" + m_intervalNumber + " IntervalLineStyle=" + m_intervalLineStyle + " IntervalLineColor=" + m_intervalLineColor);
   }
   
   private ColorType m_normalLineColor;
   private LineStyle m_normalLineStyle;
   private int m_intervalNumber;
   private LineStyle m_intervalLineStyle;
   private ColorType m_intervalLineColor;
}
