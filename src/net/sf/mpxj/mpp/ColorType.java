/*
 * file:       ColorType.java
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

package net.sf.mpxj.mpp;

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

   /**
    * Retrieve the value associated with this instance.
    * 
    * @return int value
    */
   public int getValue ()
   {
      return (m_value);
   }
   
   public static final int BLACK_VALUE = 0;
   public static final int RED_VALUE = 1;
   public static final int YELLOW_VALUE = 2;
   public static final int LIME_VALUE = 3;
   public static final int AQUA_VALUE = 4;
   public static final int BLUE_VALUE = 5;
   public static final int FUSCHIA_VALUE = 6;
   public static final int WHITE_VALUE = 7;
   public static final int MAROON_VALUE = 8;
   public static final int GREEN_VALUE = 9;
   public static final int OLIVE_VALUE = 10;
   public static final int NAVY_VALUE = 11;
   public static final int PURPLE_VALUE = 12;
   public static final int TEAL_VALUE = 13;
   public static final int GRAY_VALUE = 14;
   public static final int SILVER_VALUE = 15;
   public static final int AUTOMATIC_VALUE = 16;
   
   public static final ColorType BLACK = new ColorType (BLACK_VALUE);
   public static final ColorType RED = new ColorType (RED_VALUE);
   public static final ColorType YELLOW = new ColorType (YELLOW_VALUE);
   public static final ColorType LIME = new ColorType (LIME_VALUE);
   public static final ColorType AQUA = new ColorType (AQUA_VALUE);
   public static final ColorType BLUE = new ColorType (BLUE_VALUE);
   public static final ColorType FUSCHIA = new ColorType (FUSCHIA_VALUE);
   public static final ColorType WHITE = new ColorType (WHITE_VALUE);
   public static final ColorType MAROON = new ColorType (MAROON_VALUE);
   public static final ColorType GREEN = new ColorType (GREEN_VALUE);
   public static final ColorType OLIVE = new ColorType (OLIVE_VALUE);
   public static final ColorType NAVY = new ColorType (NAVY_VALUE);
   public static final ColorType PURPLE = new ColorType (PURPLE_VALUE);
   public static final ColorType TEAL = new ColorType (TEAL_VALUE);
   public static final ColorType GRAY = new ColorType (GRAY_VALUE);
   public static final ColorType SILVER = new ColorType (SILVER_VALUE);
   public static final ColorType AUTOMATIC = new ColorType (AUTOMATIC_VALUE);
   
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
