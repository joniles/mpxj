/*
 * file:       AccrueType.java
 * author:     Jon Iles
 * copyright:  (c) Tapster Rock Limited 2002-2003
 * date:       05/02/2003
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
 * This class is used to represent an accrue type. It provides a mapping
 * between the textual description of a accrue type found in an MPX
 * file, and an enumerated representation that can be more easily manipulated
 * programatically.
 */
public class AccrueType
{
   /**
    * This constructor takes the textual version of a accrue name
    * and populates the class instance appropriately. Note that unrecognised
    * values are treated as "Prorated".
    *
    * @param type text version of the accrue type
    */
   public AccrueType (String type)
   {
      for (int loop=0; loop < TYPE_NAMES.length; loop++)
      {
         if (TYPE_NAMES[loop].equalsIgnoreCase(type) == true)
         {
            m_type = loop+1;
            break;
         }
      }
   }

   /**
    * This constructor takes the numeric enumerated representation of an
    * accrue type and populates the class instance appropriately.
    * Note that unrecognised values are treated as "Prorated".
    *
    * @param type int version of the accrue type
    */
   public AccrueType (int type)
   {
      if (type < START || type > PRORATED)
      {
         m_type = PRORATED;
      }
      else
      {
         m_type = type;
      }
   }

   /**
    * Accessor method used to retrieve the numeric representation of the
    * accrue type.
    *
    * @return int representation of the accrue type
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
      return (TYPE_NAMES[m_type-1]);
   }

   /**
    * Integer representing the "Start" accrue type.
    */
   public static final int START = 1;

   /**
    * Integer representing the "End" accrue type.
    */
   public static final int END = 2;

   /**
    * Integer representing the "Prorated" accrue type.
    */
   public static final int PRORATED = 3;

   /**
    * Array of type names matching the above constants.
    */
   private static final String[] TYPE_NAMES =
   {
      "Start",
      "End",
      "Prorated"
   };

   /**
    * Internal representation of the accrue type.
    */
   private int m_type;
}
