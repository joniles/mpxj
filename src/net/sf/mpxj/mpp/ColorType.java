/*
 * file:       ColorType.java
 * author:     Jon Iles
 * copyright:  (c) Packwood Software Limited 2005
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
import java.util.EnumSet;

import net.sf.mpxj.utility.MpxjEnum;
import net.sf.mpxj.utility.NumberUtility;

/**
 * This enum represents the colors used by Microsoft Project.
 */
public enum ColorType implements MpxjEnum
{
   BLACK (0, "Black", Color.BLACK),
   RED (1, "Red", Color.RED),
   YELLOW (2, "Yellow", Color.YELLOW),
   LIME (3, "Lime", Color.GREEN),
   AQUA (4, "Aqua", Color.CYAN),
   BLUE (5, "Blue", Color.BLUE),
   FUSCHIA (6, "Fuschia", Color.MAGENTA),
   WHITE (7, "White", Color.WHITE),
   MAROON (8, "Maroon", new Color(132, 0, 0)),
   GREEN (9, "Green", new Color(0, 130, 0)),
   OLIVE (10, "Olive", new Color(132, 130, 0)),
   NAVY (11, "Navy", new Color(0, 0, 132)),
   PURPLE (12, "Purple", new Color(132, 0, 132)),
   TEAL (13, "Teal", new Color(0, 130, 132)),
   GRAY (14, "Gray", new Color(132, 130, 132)),
   SILVER (15, "Silver", new Color(198, 195, 198)),
   AUTOMATIC (16, "Automatic", null);
  
   /**
    * Private constructor.
    * 
    * @param type int version of the enum
    * @param name color name
    * @param color Java color instance
    */
   private ColorType (int type, String name, Color color)
   {
      m_value = type;    
      m_name = name;
      m_color = color;
   }


   /**
    * Retrieve an instance of the enum based on its int value.
    *
    * @param type int type
    * @return enum instance
    */
   public static ColorType getInstance (int type)
   {      
      if (type < 0 || type >= TYPE_VALUES.length)
      {
         type = AUTOMATIC.getValue();
      }
      return (TYPE_VALUES[type]);
   }

   /**
    * Retrieve an instance of the enum based on its int value.
    *
    * @param type int type
    * @return enum instance
    */
   public static ColorType getInstance (Number type)
   {
      int value;
      if (type == null)
      {
         value = -1;
      }
      else
      {
         value = NumberUtility.getInt(type);
      }
      return (getInstance(value));
   }

   /**
    * Retrieve the color name. Currently this is not localised.
    *
    * @return color name
    */
   public String getName ()
   {
      return (m_name);
   }

   /**
    * Retrieve a Java Color instance matching the color used in MS Project.
    * Note that this will return null if the color type is automatic.
    *
    * @return Color instance
    */
   public Color getColor ()
   {
      return (m_color);
   }

   /**
    * Accessor method used to retrieve the numeric representation of the enum. 
    *
    * @return int representation of the enum
    */
   public int getValue ()
   {
      return (m_value);
   }


   /**
    * Array mapping int types to enums.
    */
   private static final ColorType[] TYPE_VALUES = new ColorType[17];
   static
   {      
      for (ColorType e : EnumSet.range(ColorType.BLACK, ColorType.AUTOMATIC))
      {
         TYPE_VALUES[e.getValue()] = e;
      }
   }


   /**
    * Internal representation of the enum int type.
    */
   private int m_value;
   private String m_name;
   private Color m_color;
}
