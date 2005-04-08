/*
 * file:       TimescaleAlignment.java
 * author:     Jon Iles
 * copyright:  (c) Tapster Rock Limited 2005
 * date:       Apr 7, 2005
 */
 
package com.tapsterrock.mpp;

/**
 * Class representing the label alignment on a Gantt chart timescale.
 */
public final class TimescaleAlignment
{
   /**
    * Private constructor.
    * 
    * @param value alignment value from an MS Project file
    */
   private TimescaleAlignment (int value)
   {
      m_value = value;
   }
   
   /**
    * Retrieve an instance of this class based on the data read from an
    * MS Project file.
    * 
    * @param value value from an MS Project file
    * @return instance of this class
    */
   public static TimescaleAlignment getInstance (int value)
   {  
      TimescaleAlignment result;
      
      if (value < 0 || value >= ALIGNMENT_ARRAY.length)
      {
         value = 1;
      }
      
      result = ALIGNMENT_ARRAY[value];
      
      return (result);
   }

   /**
    * Retrieve the name of this alignment. Note that this is not
    * localised.
    * 
    * @return name of this alignment type
    */
   public String getName ()
   {
      return (ALIGNMENT_NAMES[m_value]);
   }
   
   /**
    * Generate a string representation of this instance.
    * 
    * @return string representation of this instance
    */
   public String toString ()
   {
      return (getName());
   }

   /**
    * Retrieve the value associated with this instance.
    * 
    * @return int value
    */
   public int getValue ()
   {
      return (m_value);
   }
   
   public static final int LEFT_VALUE = 0;
   public static final int CENTER_VALUE = 1;
   public static final int RIGHT_VALUE = 2;
   
   public static final TimescaleAlignment LEFT = new TimescaleAlignment (LEFT_VALUE);
   public static final TimescaleAlignment CENTER = new TimescaleAlignment (CENTER_VALUE);
   public static final TimescaleAlignment RIGHT = new TimescaleAlignment (RIGHT_VALUE);

   private static final TimescaleAlignment[] ALIGNMENT_ARRAY =
   {
      LEFT,
      CENTER,
      RIGHT
   };
   
   private static final String[] ALIGNMENT_NAMES = 
   {
      "Left",
      "Center",
      "Right",
   };
   
   private int m_value;
}
