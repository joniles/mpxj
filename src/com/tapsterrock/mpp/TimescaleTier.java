/*
 * file:       TimescaleTier.java
 * author:     Jon Iles
 * copyright:  (c) Tapster Rock Limited 2005
 * date:       Apr 7, 2005
 */
 
package com.tapsterrock.mpp;

/**
 * This class collects to gether the properties which represent a 
 * single tier of the timscale on a Gantt chart.
 */
public final class TimescaleTier
{
   /**
    * Retrieves the tier count.
    * 
    * @return tier count
    */
   public int getCount()
   {
      return (m_count);
   }
   
   /**
    * Sets the tier count.
    * 
    * @param count tier count
    */
   public void setCount(int count)
   {
      m_count = count;
   }
   
   /**
    * Retrieves the tier label format.
    * 
    * @return tier label format
    */
   public int getFormat()
   {
      return (m_format);
   }
   
   /**
    * Sets the tier label format.
    * 
    * @param format tier label format
    */
   public void setFormat(int format)
   {
      m_format = format;
   }
   
   /**
    * Retrieves the tick lines flag.
    * 
    * @return tick lines flag
    */
   public boolean getTickLines()
   {
      return (m_tickLines);
   }
   
   /**
    * Sets the tick lines flag
    * 
    * @param tickLines tick lines flag
    */
   public void setTickLines(boolean tickLines)
   {
      m_tickLines = tickLines;
   }
   
   /**
    * Retrieves the timescale units.
    * 
    * @return timescale units
    */
   public TimescaleUnits getUnits()
   {
      return (m_units);
   }
   
   /**
    * Sets the timescale units.
    * 
    * @param units timescale units
    */
   public void setUnits(TimescaleUnits units)
   {
      m_units = units;
   }
   
   /**
    * Retrieves the uses fiscal year flag
    * 
    * @return uses fiscal year flag
    */
   public boolean getUsesFiscalYear()
   {
      return (m_usesFiscalYear);
   }
   
   /**
    * Sets the uses fiscal year flag.
    * 
    * @param usesFiscalYear uses fiscal year flag
    */
   public void setUsesFiscalYear(boolean usesFiscalYear)
   {
      m_usesFiscalYear = usesFiscalYear;
   }

   /**
    * Retrieve the timescale lable alignment.
    * 
    * @return label alignment
    */
   public TimescaleAlignment getAlignment ()
   {
      return (m_alignment);
   }
   
   /**
    * Set the timescale label alignment.
    * 
    * @param alignment label alignment
    */
   public void setAlignment (TimescaleAlignment alignment)
   {
      m_alignment = alignment;
   }
   
   /**
    * Generate a string representation of this instance.
    * 
    * @return string representation of this instance
    */   
   public String toString ()
   {
      return 
      (
         "[TimescaleTier UsesFiscalYear=" + m_usesFiscalYear + 
         " TickLines=" + m_tickLines +
         " Units=" + m_units +
         " Count=" + m_count +    
         " Format=" + m_format + 
         " Alignment=" + m_alignment +             
         "]"
      );      
   }
   
   private boolean m_usesFiscalYear;
   private boolean m_tickLines;
   private TimescaleUnits m_units;
   private int m_count;
   private int m_format;
   private TimescaleAlignment m_alignment;
}
