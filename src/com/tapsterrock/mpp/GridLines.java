/*
 * file:       GridLines.java
 * author:     Jon Iles
 * copyright:  (c) Tapster Rock Limited 2005
 * date:       Apr 7, 2005
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
      m_normalLineStyle = GridLineStyle.getInstance(data[offset+3]);
      m_intervalNumber = data[offset+4];
      m_intervalLineStyle = GridLineStyle.getInstance(data[offset+5]);
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
   public GridLineStyle getIntervalLineStyle()
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
   public GridLineStyle getNormalLineStyle()
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
   private GridLineStyle m_normalLineStyle;
   private int m_intervalNumber;
   private GridLineStyle m_intervalLineStyle;
   private ColorType m_intervalLineColor;
}
