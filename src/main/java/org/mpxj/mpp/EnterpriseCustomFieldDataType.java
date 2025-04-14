/*
 * file:       EnterpriseCustomFieldDataType.java
 * author:     Jon Iles
 * copyright:  (c) Packwood Software 2022
 * date:       2022-08-09
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

package org.mpxj.mpp;

import java.util.HashMap;
import java.util.Map;

import org.mpxj.DataType;

/**
 * Lookup table for enterprise custom field types.
 */
public final class EnterpriseCustomFieldDataType
{
   /**
    * Look up the type of an enterprise custom field based on its integer ID.
    *
    * @param value integer type ID
    * @return DataType instance
    */
   public static DataType getDataTypeFromID(int value)
   {
      return DATA_TYPE_MAP.getOrDefault(Integer.valueOf(value), DataType.CUSTOM);
   }

   /**
    * Look up the integer ID of an enterprise custom field data type.
    *
    * @param type data type
    * @return integer id
    */
   public static Integer getIDFromDataType(DataType type)
   {
      return ID_MAP.get(type);
   }

   // https://docs.microsoft.com/en-us/office-project/xml-data-interchange/cftype-element?view=project-client-2016
   private static final Map<Integer, DataType> DATA_TYPE_MAP = new HashMap<>();
   static
   {
      DATA_TYPE_MAP.put(Integer.valueOf(0), DataType.CURRENCY);
      DATA_TYPE_MAP.put(Integer.valueOf(1), DataType.DATE);
      DATA_TYPE_MAP.put(Integer.valueOf(2), DataType.DURATION);
      DATA_TYPE_MAP.put(Integer.valueOf(3), DataType.DATE); // Finish
      DATA_TYPE_MAP.put(Integer.valueOf(4), DataType.BOOLEAN);
      DATA_TYPE_MAP.put(Integer.valueOf(5), DataType.NUMERIC);
      DATA_TYPE_MAP.put(Integer.valueOf(6), DataType.DATE); // Start
      DATA_TYPE_MAP.put(Integer.valueOf(7), DataType.STRING);
   }

   private static final Map<DataType, Integer> ID_MAP = new HashMap<>();
   static
   {
      ID_MAP.put(DataType.CURRENCY, Integer.valueOf(0));
      ID_MAP.put(DataType.DATE, Integer.valueOf(1));
      ID_MAP.put(DataType.DURATION, Integer.valueOf(2));
      ID_MAP.put(DataType.BOOLEAN, Integer.valueOf(4));
      ID_MAP.put(DataType.NUMERIC, Integer.valueOf(5));
      ID_MAP.put(DataType.STRING, Integer.valueOf(7));
   }
}
