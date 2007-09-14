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

package net.sf.mpxj;

import net.sf.mpxj.utility.MpxjEnum;


/**
 * This class is used to represent an accrue type. It provides a mapping
 * between the textual description of a accrue type found in an MPX
 * file, and an enumerated representation that can be more easily manipulated
 * programatically.
 */
public final class AccrueType implements MpxjEnum
{
   /**
    * This constructor takes the numeric enumerated representation of an
    * accrue type and populates the class instance appropriately.
    * Note that unrecognised values are treated as "Prorated".
    *
    * @param type int version of the accrue type
    */
   private AccrueType (int type)
   {
      if (type < START_VALUE || type > PRORATED_VALUE)
      {
         m_value = PRORATED_VALUE;
      }
      else
      {
         m_value = type;
      }
   }

   /**
    * This method takes a numeric enumerated accrue type value
    * and populates the class instance appropriately. Note that unrecognised
    * values are treated as "Prorated".
    *
    * @param type numeric enumerated accrue type
    * @return AccrueType class instance
    */
   public static AccrueType getInstance (Number type)
   {
      AccrueType result;

      if (type == null)
      {
         result = TYPE_VALUES[PRORATED_VALUE-1];
      }
      else
      {
         result = getInstance (type.intValue());
      }

      return (result);
   }

   /**
    * This method takes a numeric enumerated accrue type value
    * and populates the class instance appropriately. Note that unrecognised
    * values are treated as "Prorated".
    *
    * @param type numeric enumerated accrue type
    * @return AccrueType class instance
    */
   public static AccrueType getInstance (int type)
   {
      if (type < START_VALUE || type > PRORATED_VALUE)
      {
         type = PRORATED_VALUE;
      }

      return (TYPE_VALUES[type-1]);
   }


   /**
    * Accessor method used to retrieve the numeric representation of the
    * accrue type.
    *
    * @return int representation of the accrue type
    */
   public int getValue ()
   {
      return (m_value);
   }

   public static final int START_VALUE = 1;
   public static final int END_VALUE = 2;
   public static final int PRORATED_VALUE = 3;

   public static final AccrueType START = new AccrueType (START_VALUE);
   public static final AccrueType END = new AccrueType (END_VALUE);
   public static final AccrueType PRORATED = new AccrueType (PRORATED_VALUE);

   /**
    * Array of type values matching the above constants.
    */
   private static final AccrueType[] TYPE_VALUES =
   {
      START,
      END,
      PRORATED,
   };

   /**
    * Internal representation of the accrue type.
    */
   private int m_value;
}
