/*
 * file:       GridLineStyle.java
 * author:     Jon Iles
 * copyright:  (c) Tapster Rock Limited 2005
 * date:       Apr 7, 2005
 */
 
package com.tapsterrock.mpp;

/**
 * This class represents the grid line styles used by Microsoft Project.
 */
public final class GridLineStyle
{
   /**
    * Private constructor.
    * 
    * @param value grid line style
    */
   private GridLineStyle (int value)
   {
      m_value = value;
   }
   
   /**
    * Retrieve an instance of this type based on a line style from MS Project.
    * 
    * @param value line style
    * @return GridLineStyle instance
    */
   public static GridLineStyle getInstance (int value)
   {
      GridLineStyle style;
      
      if (value < 0 || value >= STYLE_TYPES.length)
      {
         style = NONE;
      }
      else
      {
         style = STYLE_TYPES[value];
      }
      
      return (style);
   }
   
   /**
    * Retrieve the line style name. Currently this is not localised.
    * 
    * @return style name
    */
   public String getName ()
   {
      return (STYLE_NAMES[m_value]);      
   }
   
   /**
    * Retrieve the String representation of this line style.
    * 
    * @return String representation of this line style
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
   
   public static final int NONE_VALUE = 0;
   public static final int SOLID_VALUE = 1;
   public static final int DOTTED1_VALUE = 2;
   public static final int DOTTED2_VALUE = 3;
   public static final int DASHED_VALUE = 4;
   
   public static final GridLineStyle NONE = new GridLineStyle (NONE_VALUE);
   public static final GridLineStyle SOLID = new GridLineStyle (SOLID_VALUE);
   public static final GridLineStyle DOTTED1 = new GridLineStyle (DOTTED1_VALUE);
   public static final GridLineStyle DOTTED2 = new GridLineStyle (DOTTED2_VALUE);
   public static final GridLineStyle DASHED = new GridLineStyle (DASHED_VALUE);
   
   private static final GridLineStyle[] STYLE_TYPES = 
   {
      NONE,
      SOLID,
      DOTTED1,
      DOTTED2,
      DASHED            
   };
   
   private static final String[] STYLE_NAMES =
   {
      "None",
      "Solid",
      "Dotted1",
      "Dotted2",
      "Dashed"
   };
   
   private int m_value;
}
