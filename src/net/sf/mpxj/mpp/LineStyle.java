/*
 * file:       GridLineStyle.java
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

/**
 * This class represents the grid line styles used by Microsoft Project.
 */
public final class LineStyle
{
   /**
    * Private constructor.
    * 
    * @param value grid line style
    */
   private LineStyle (int value)
   {
      m_value = value;
   }
   
   /**
    * Retrieve an instance of this type based on a line style from MS Project.
    * 
    * @param value line style
    * @return GridLineStyle instance
    */
   public static LineStyle getInstance (int value)
   {
      LineStyle style;
      
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
   
   public static final LineStyle NONE = new LineStyle (NONE_VALUE);
   public static final LineStyle SOLID = new LineStyle (SOLID_VALUE);
   public static final LineStyle DOTTED1 = new LineStyle (DOTTED1_VALUE);
   public static final LineStyle DOTTED2 = new LineStyle (DOTTED2_VALUE);
   public static final LineStyle DASHED = new LineStyle (DASHED_VALUE);
   
   private static final LineStyle[] STYLE_TYPES = 
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
