/*
 * file:       Priority.java
 * author:     Jon Iles
 * copyright:  (c) Packwood Software 2002-2003
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

package org.mpxj;

/**
 * This class is used to represent a priority. It provides a mapping
 * between the textual description of a priority found in an MPX
 * file, and an enumerated representation that can be more easily manipulated
 * programmatically.
 */
public final class Priority
{
   /**
    * This constructor takes the numeric enumerated representation of a
    * priority and populates the class instance appropriately.
    * Note that unrecognised values are treated as medium priorities.
    *
    * @param priority int representation of the priority
    */
   private Priority(int priority)
   {
      if (priority < 0 || priority > DO_NOT_LEVEL)
      {
         m_value = MEDIUM;
      }
      else
      {
         m_value = priority;
      }
   }

   /**
    * This method takes an integer enumeration of a priority
    * and returns an appropriate instance of this class. Note that unrecognised
    * values are treated as medium priority.
    *
    * @param priority int version of the priority
    * @return Priority class instance
    */
   public static Priority getInstance(int priority)
   {
      Priority result;

      if (priority >= LOWEST && priority <= DO_NOT_LEVEL && (priority % 100 == 0))
      {
         result = VALUE[(priority / 100) - 1];
      }
      else
      {
         result = new Priority(priority);
      }

      return (result);
   }

   /**
    * Accessor method used to retrieve the numeric representation of the
    * priority.
    *
    * @return int representation of the priority
    */
   public int getValue()
   {
      return (m_value);
   }

   @Override public String toString()
   {
      return ("[Priority value=" + m_value + "]");
   }

   /**
    * Constant for lowest priority.
    */
   public static final int LOWEST = 100;

   /**
    * Constant for low priority.
    */
   public static final int VERY_LOW = 200;

   /**
    * Constant for lower priority.
    */
   public static final int LOWER = 300;

   /**
    * Constant for low priority.
    */
   public static final int LOW = 400;

   /**
    * Constant for medium priority.
    */
   public static final int MEDIUM = 500;

   /**
    * Constant for high priority.
    */
   public static final int HIGH = 600;

   /**
    * Constant for higher priority.
    */
   public static final int HIGHER = 700;

   /**
    * Constant for very high priority.
    */
   public static final int VERY_HIGH = 800;

   /**
    * Constant for highest priority.
    */
   public static final int HIGHEST = 900;

   /**
    * Constant for do not level.
    */
   public static final int DO_NOT_LEVEL = 1000;

   /**
    * Array of type values matching the above constants.
    */
   private static final Priority[] VALUE =
   {
      new Priority(LOWEST),
      new Priority(VERY_LOW),
      new Priority(LOWER),
      new Priority(LOW),
      new Priority(MEDIUM),
      new Priority(HIGH),
      new Priority(HIGHER),
      new Priority(VERY_HIGH),
      new Priority(HIGHEST),
      new Priority(DO_NOT_LEVEL)
   };

   /**
    * Internal representation of the priority.
    */
   private final int m_value;
}
