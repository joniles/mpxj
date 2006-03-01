/*
 * file:       RelationType.java
 * author:     Jon Iles
 * copyright:  (c) Tapster Rock Limited 2002-2003
 * date:       10/05/2005
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


/**
 * This class is used to represent a relation type. It provides a mapping
 * between the textual description of a relation type found in an MPX
 * file, and an enumerated representation that can be more easily manipulated
 * programatically.
 */
public final class RelationType
{
   /**
    * This constructor takes the numeric enumerated representation of a
    * relation type and populates the class instance appropriately.
    * Note that unrecognised values are treated as finish-start.
    *
    * @param type int version of the relation type
    */
   private RelationType (int type)
   {
      if (type < 0 || type > MAX_TYPE_VALUES)
      {
         m_type = FINISH_START_VALUE;
      }
      else
      {
         m_type = type;
      }
   }

   /**
    * This method takes the integer enumeration of a relation type
    * and returns an appropriate class instance. Note that unrecognised
    * values are treated as finish-start.
    *
    * @param type integer relation type enumeration
    * @return RelationType instance
    */
   public static RelationType getInstance (int type)
   {
      if (type < 0 || type >= TYPE_VALUES.length)
      {
         type = FINISH_START_VALUE;
      }

      return (TYPE_VALUES[type]);
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
    * {@inheritDoc}
    */
   public String toString ()
   {
      return (TYPE_NAMES[m_type]);
   }
   
   public static final int FINISH_FINISH_VALUE = 0;
   public static final int FINISH_START_VALUE = 1;
   public static final int START_FINISH_VALUE = 2;
   public static final int START_START_VALUE = 3;
   public static final int MAX_TYPE_VALUES = 4;

   public static final RelationType FINISH_FINISH = new RelationType (FINISH_FINISH_VALUE);
   public static final RelationType FINISH_START = new RelationType (FINISH_START_VALUE);
   public static final RelationType START_FINISH = new RelationType (START_FINISH_VALUE);
   public static final RelationType START_START = new RelationType (START_START_VALUE);

   /**
    * Array of type values matching the above constants.
    */
   private static final RelationType[] TYPE_VALUES =
   {
      FINISH_FINISH,
      FINISH_START,
      START_FINISH,
      START_START
   };

   /**
    * For debugging only.
    */
   private static final String[] TYPE_NAMES =
   {
      "FF",
      "FS",
      "SF",
      "SS"
   };
   
   /**
    * Internal representation.
    */
   private int m_type;
}
