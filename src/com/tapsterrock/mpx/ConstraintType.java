/*
 * file:       ConstraintType.java
 * author:     Jon Iles
 * copyright:  (c) Tapster Rock Limited 2002-2003
 * date:       01/02/2003
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
 * This class is used to represent a constraint type. It provides a mapping
 * between the textual description of a constraint type found in an MPX
 * file, and an enumerated representation that can be more easily manipulated
 * programatically.
 */
public class ConstraintType
{
   /**
    * This constructor takes the textual version of a constraint name
    * and populates the class instance appropriately. Note that unrecognised
    * values are treated as "As Soon As Possible" constraints.
    *
    * @param type text version of the constraint type
    */
   public ConstraintType (String type)
   {
      for (int loop=0; loop < TYPE_NAMES.length; loop++)
      {
         if (TYPE_NAMES[loop].equalsIgnoreCase(type) == true)
         {
            m_type = loop;
            break;
         }
      }
   }

   /**
    * This constructor takes the numeric enumerated representation of a
    * constraint type and populates the class instance appropriately.
    * Note that unrecognised values are treated as "As Soon As Possible"
    * constraints.
    *
    * @param type int version of the constraint type
    */
   public ConstraintType (int type)
   {
      if (type < 0 || type >= TYPE_NAMES.length)
      {
         m_type = 0;
      }
      else
      {
         m_type = type;
      }
   }

   /**
    * Accessor method used to retrieve the numeric representation of the
    * constraint type.
    *
    * @return int representation of the constraint type
    */
   public int getType ()
   {
      return (m_type);
   }

   /**
    * This method generates a string in MPX format representing the
    * contents of this record.
    *
    * @return string containing the data for this record in MPX format.
    */
   public String toString ()
   {
      return (TYPE_NAMES[m_type]);
   }

   /**
    * Integer representing the "As Soon As Possible" constraint type.
    */
   public static final int AS_SOON_AS_POSSIBLE = 0;

   /**
    * Integer representing the "As Late As Possible" constraint type.
    */
   public static final int AS_LATE_AS_POSSIBLE = 1;

   /**
    * Integer representing the "Must Start On" constraint type.
    */
   public static final int MUST_START_ON = 2;

   /**
    * Integer representing the "Must Finish On" constraint type.
    */
   public static final int MUST_FINISH_ON = 3;

   /**
    * Integer representing the "Start No Earlier Than" constraint type.
    */
   public static final int START_NO_EARLIER_THAN = 4;

   /**
    * Integer representing the "Start No Later Than" constraint type.
    */
   public static final int START_NO_LATER_THAN = 5;

   /**
    * Integer representing the "Finish No Earlier Than" constraint type.
    */
   public static final int FINISH_NO_EARLIER_THAN = 6;

   /**
    * Integer representing the "Finish No Later Than" constraint type.
    */
   public static final int FINISH_NO_LATER_THAN = 7;

   /**
    * Array of type names matching the above constants.
    */
   private static final String[] TYPE_NAMES =
   {
      "As Soon As Possible",
      "As Late As Possible",
      "Must Start On",
      "Must Finish On",
      "Start No Earlier Than",
      "Start No Later Than",
      "Finish No Earlier Than",
      "Finish No Later Than"
   };

   /**
    * Internal representation of the constraint type.
    */
   private int m_type;
}
