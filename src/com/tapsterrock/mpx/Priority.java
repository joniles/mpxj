/*
 * file:       Priority.java
 * author:     Jon Iles
 * copyright:  (c) Tapster Rock Limited 2002-2003
 * date:       18/02/2003
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

package com.tapsterrock.mpx;

/**
 * This class is used to represent a priority. It provides a mapping
 * between the textual description of a priority found in an MPX
 * file, and an enumerated representation that can be more easily manipulated
 * programatically.
 */
public class Priority
{
   /**
    * This constructor takes the textual version of a priority
    * and populates the class instance appropriately. Note that unrecognised
    * values are treated as medium priority.
    *
    * @param type text version of the priority
    */
   Priority (String priority)
   {
      m_priority = MEDIUM;

      if (priority != null)
      {
         for (int loop=0; loop < TEXT.length; loop++)
         {
            if (TEXT[loop].equalsIgnoreCase(priority) == true)
            {
               m_priority = loop;
               break;
            }
         }
      }
   }

   /**
    * This constructor takes the numeric enumerated representation of a
    * priority and populates the class instance appropriately.
    * Note that unrecognised values are treated as medium priorities.
    *
    * @param priority int representation of the priority
    */
   public Priority (int priority)
   {
      if (priority < LOWEST || priority > DO_NOT_LEVEL)
      {
         m_priority = MEDIUM;
      }
      else
      {
         m_priority = priority;
      }
   }

   /**
    * Accessor method used to retrieve the numeric representation of the
    * priority.
    *
    * @return int representation of the priority
    */
   public int getPriority ()
   {
      return (m_priority);
   }

   /**
    * This method generates a string in MPX format representing the
    * contents of this record.
    *
    * @return string containing the data for this record in MPX format.
    */
   public String toString ()
   {
      return (TEXT[m_priority]);
   }

   /**
    * Constant for lowest priority
    */
   public static final int LOWEST = 0;

   /**
    * Constant for low priority
    */
   public static final int VERY_LOW = 1;

   /**
    * Constant for lower priority
    */
   public static final int LOWER = 2;

   /**
    * Constant for low priority
    */
   public static final int LOW = 3;

   /**
    * Constant for medium priority
    */
   public static final int MEDIUM = 4;

   /**
    * Constant for high priority
    */
   public static final int HIGH = 5;

   /**
    * Constant for higher priority
    */
   public static final int HIGHER = 6;

   /**
    * Constant for very high priority
    */
   public static final int VERY_HIGH = 7;

   /**
    * Constant for highest priority
    */
   public static final int HIGHEST = 8;

   /**
    * Constant for do not level
    */
   public static final int DO_NOT_LEVEL = 9;

   /**
    * Array of type names matching the above constants.
    */
   private static final String[] TEXT =
   {
      "Lowest",
      "Very Low",
      "Lower",
      "Low",
      "Medium",
      "High",
      "Higher",
      "Very High",
      "Highest",
      "Do Not Level"
   };

   /**
    * Internal representation of the priority.
    */
   private int m_priority;
}