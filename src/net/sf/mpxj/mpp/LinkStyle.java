/*
 * file:       LinkStyle.java
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
 * Class representing how links are drawn on a Gantt chart.
 */
public final class LinkStyle
{
   /**
    * Private constructor.
    * 
    * @param value alignment value from an MS Project file
    */
   private LinkStyle (int value)
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
   public static LinkStyle getInstance (int value)
   {  
      LinkStyle result;
      
      if (value < 0 || value >= STYLE_ARRAY.length)
      {
         value = 1;
      }
      
      result = STYLE_ARRAY[value];
      
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
      return (STYLE_NAMES[m_value]);
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
   
   public static final int NONE_VALUE = 0;
   public static final int END_TOP_VALUE = 1;
   public static final int END_START_VALUE = 2;
   
   public static final LinkStyle NONE = new LinkStyle (NONE_VALUE);
   public static final LinkStyle END_TOP = new LinkStyle (END_TOP_VALUE);
   public static final LinkStyle END_START = new LinkStyle (END_START_VALUE);

   private static final LinkStyle[] STYLE_ARRAY =
   {
      NONE,
      END_TOP,
      END_START
   };
   
   private static final String[] STYLE_NAMES = 
   {
      "None",
      "End Top",
      "End Start",
   };
   
   private int m_value;
}
