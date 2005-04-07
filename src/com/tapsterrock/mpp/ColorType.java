/*
 * file:       ColorType.java
 * author:     Jon Iles
 * copyright:  (c) Tapster Rock Limited 2005
 * date:       Apr 7, 2005
 */
 
package com.tapsterrock.mpp;

import java.awt.Color;

/**
 * This class represents the colors used by Microsoft Project.
 */
public final class ColorType
{
   /**
    * Private constructor.
    * 
    * @param value color number
    */
   private ColorType (int value)
   {
      m_value = value;
   }

   /**
    * Retrieve an instance of this type based on a color number from MS Project.
    * 
    * @param value color number
    * @return ColorType instance
    */
   public static ColorType getInstance (int value)
   {
      ColorType color;
      
      if (value < 0 || value >= COLOR_TYPES.length)
      {
         color = AUTOMATIC;
      }
      else
      {
         color = COLOR_TYPES[value];
      }
      
      return (color);
   }
   
   /**
    * Retrieve the color name. Currently this is not localised.
    * 
    * @return color name
    */
   public String getName ()
   {
      return (COLOR_NAMES[m_value]);      
   }
   
   /**
    * Retrieve a Java Color instance matching the color used in MS Project.
    * Note that this will return null if the color type is automatic.
    * 
    * @return Color instance
    */
   public Color getColor ()
   {
      return (COLOR_OBJECTS[m_value]);
   }
   
   /**
    * Retrieve the String representation of this color.
    * 
    * @return String representation of this color
    */
   public String toString ()
   {
      return (getName());
   }
   
   public static final ColorType BLACK = new ColorType (0);
   public static final ColorType RED = new ColorType (1);
   public static final ColorType YELLOW = new ColorType (2);
   public static final ColorType LIME = new ColorType (3);
   public static final ColorType AQUA = new ColorType (4);
   public static final ColorType BLUE = new ColorType (5);
   public static final ColorType FUSCHIA = new ColorType (6);
   public static final ColorType WHITE = new ColorType (7);
   public static final ColorType MAROON = new ColorType (8);
   public static final ColorType GREEN = new ColorType (9);
   public static final ColorType OLIVE = new ColorType (10);
   public static final ColorType NAVY = new ColorType (11);
   public static final ColorType PURPLE = new ColorType (12);
   public static final ColorType TEAL = new ColorType (13);
   public static final ColorType GRAY = new ColorType (14);
   public static final ColorType SILVER = new ColorType (15);
   public static final ColorType AUTOMATIC = new ColorType (16);
   
   private static final ColorType[] COLOR_TYPES = 
   {
      BLACK,
      RED,
      YELLOW,
      LIME,
      AQUA,
      BLUE,
      FUSCHIA,
      WHITE,
      MAROON,
      GREEN,
      OLIVE,
      NAVY,
      PURPLE,
      TEAL,
      GRAY,
      SILVER,
      AUTOMATIC            
   };

   private static final String[] COLOR_NAMES = 
   {
      "Black",
      "Red",
      "Yellow",
      "Lime",
      "Aqua",
      "Blue",
      "Fuschia",
      "White",
      "Maroon",
      "Green",
      "Olive",
      "Navy",
      "Purple",
      "Teal",
      "Gray",
      "Silver",
      "Automatic"                        
   };

   private static final Color[] COLOR_OBJECTS =
   {
      Color.BLACK,   
      Color.RED,   
      Color.YELLOW,   
      Color.GREEN,   
      Color.CYAN,   
      Color.BLUE,   
      Color.MAGENTA,   
      Color.WHITE,   
      new Color(132, 0, 0),   
      new Color(0, 130, 0),   
      new Color(132, 130, 0),   
      new Color(0, 0, 132),   
      new Color(132, 0, 132),   
      new Color(0, 130, 132),   
      new Color(132, 130, 132),   
      new Color(198, 195, 198),   
      null
   };
   
   private int m_value;
}
