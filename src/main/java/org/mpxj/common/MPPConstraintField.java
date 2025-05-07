/*
 * file:       MPPConstraintField.java
 * author:     Jon Iles
 * copyright:  (c) Packwood Software 2014
 * date:       24/10/2014
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

package org.mpxj.common;

import java.util.Arrays;

import org.mpxj.ConstraintField;
import org.mpxj.FieldType;

/**
 * Utility class used to map between the integer values held in MS Project
 * to represent a constraint field, and the enumerated type used to represent
 * constraint fields in MPXJ.
 */
public class MPPConstraintField
{
   /**
    * Retrieve an instance of the ConstraintField class based on the data read from an
    * MS Project file.
    *
    * @param value value from an MS Project file
    * @return ConstraintField instance
    */
   public static FieldType getInstance(int value)
   {
      FieldType result = null;

      if (value >= 0 && value < FIELD_ARRAY.length)
      {
         result = FIELD_ARRAY[value];
      }

      return (result);
   }

   /**
    * Retrieve the ID of a field, as used by MS Project.
    *
    * @param value field instance
    * @return field ID
    */
   public static int getID(FieldType value)
   {
      return (ID_ARRAY[value.getValue()]);
   }

   private static final int MAX_VALUE = 10;

   private static final ConstraintField[] FIELD_ARRAY = new ConstraintField[MAX_VALUE];

   static
   {
      FIELD_ARRAY[9] = ConstraintField.UNIQUE_ID;
      FIELD_ARRAY[6] = ConstraintField.TASK1;
      FIELD_ARRAY[7] = ConstraintField.TASK2;
   }

   private static final int[] ID_ARRAY = new int[ConstraintField.MAX_VALUE];
   static
   {
      Arrays.fill(ID_ARRAY, -1);

      for (int loop = 0; loop < FIELD_ARRAY.length; loop++)
      {
         ConstraintField constraintField = FIELD_ARRAY[loop];
         if (constraintField != null)
         {
            ID_ARRAY[constraintField.getValue()] = loop;
         }
      }
   }

   public static final int CONSTRAINT_FIELD_BASE = 0xD400000;
}
