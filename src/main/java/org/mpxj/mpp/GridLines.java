/*
 * file:       GridLines.java
 * author:     Jon Iles
 * copyright:  (c) Packwood Software 2005
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

package org.mpxj.mpp;

import java.awt.Color;

/**
 * This class represents the set of properties that define the position
 * and appearance of a set of grid lines.
 */
public class GridLines
{
   /**
    * Constructor.
    *
    * @param normalLineColor normal line color
    * @param normalLineStyle normal line style
    * @param intervalNumber interval number
    * @param intervalLineStyle interval line style
    * @param intervalLineColor interval line color
    */
   public GridLines(Color normalLineColor, LineStyle normalLineStyle, int intervalNumber, LineStyle intervalLineStyle, Color intervalLineColor)
   {
      m_normalLineColor = normalLineColor;
      m_normalLineStyle = normalLineStyle;
      m_intervalNumber = intervalNumber;
      m_intervalLineStyle = intervalLineStyle;
      m_intervalLineColor = intervalLineColor;
   }

   /**
    * Retrieve the interval line color.
    *
    * @return interval line color
    */
   public Color getIntervalLineColor()
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
    * Retrieve the interval number.
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
   public Color getNormalLineColor()
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
   @Override public String toString()
   {
      return ("[GridLines NormalLineColor=" + m_normalLineColor + " NormalLineStyle=" + m_normalLineStyle + " IntervalNumber=" + m_intervalNumber + " IntervalLineStyle=" + m_intervalLineStyle + " IntervalLineColor=" + m_intervalLineColor + "]");
   }

   private final Color m_normalLineColor;
   private final LineStyle m_normalLineStyle;
   private final int m_intervalNumber;
   private final LineStyle m_intervalLineStyle;
   private final Color m_intervalLineColor;
}
