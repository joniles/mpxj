/*
 * file:       VarDataFieldReader.java
 * author:     Jon Iles
 * copyright:  (c) Packwood Software 2019
 * date:       18/09/2019
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

import java.util.UUID;

import org.mpxj.CustomFieldContainer;
import org.mpxj.common.ByteArrayHelper;

/**
 * Core implementation to read fields from var data, including
 * handling for retrieving items from lookup tables.
 */
abstract class VarDataFieldReader
{
   /**
    * Constructor.
    *
    * @param customFields custom fields container
    */
   public VarDataFieldReader(CustomFieldContainer customFields)
   {
      m_customFields = customFields;
   }

   /**
    * Retrieve a value from the var data block.
    *
    * @param varData var data block
    * @param id value ID
    * @param type value type
    * @return value from var data block
    */
   public Object getValue(Var2Data varData, Integer id, Integer type)
   {
      Object result;

      int flag = varData.getShort(id, type);
      if (flag == VALUE_LIST_WITH_ID_MASK || flag == VALUE_LIST_WITHOUT_ID_MASK)
      {
         byte[] data = varData.getByteArray(id, type);
         if (data.length == 4)
         {
            result = MPPUtility.getDate(data, 2);
         }
         else
         {
            result = getValueByID(data);
         }
      }
      else
      {
         result = readValue(varData, id, type);
      }

      return result;
   }

   /**
    * Handle a block representing the value as an integer unique ID and a GUID.
    *
    * @param data block containing value
    * @return item value
    */
   private Object getValueByID(byte[] data)
   {
      // 26 bytes in total: 2 byte mask, 4 byte unique ID, 16 byte GUID, 4 bytes unknown
      int uniqueId = ByteArrayHelper.getInt(data, 2);
      UUID guid = MPPUtility.getGUID(data, 6);

      CustomFieldValueItem item;
      if (uniqueId == -1)
      {
         item = m_customFields.getCustomFieldValueItemByGuid(guid);
      }
      else
      {
         item = m_customFields.getCustomFieldValueItemByUniqueID(uniqueId);
         if (item == null)
         {
            // If the unique ID doesn't give us a result, fall back on the guid
            item = m_customFields.getCustomFieldValueItemByGuid(guid);
         }
      }

      return item == null ? guid : coerceValue(item.getValue());
   }

   /**
    * Sub classes override this method to read a value of the appropriate type.
    *
    * @param varData var data block
    * @param id value ID
    * @param type value type
    * @return value
    */
   protected abstract Object readValue(Var2Data varData, Integer id, Integer type);

   /**
    * Sub classes override this method to transform the data type of the value
    * read from the var data block.
    *
    * @param value value read from var data block
    * @return coerced value
    */
   protected abstract Object coerceValue(Object value);

   private final CustomFieldContainer m_customFields;
   private static final int VALUE_LIST_WITH_ID_MASK = 0x0701;
   private static final int VALUE_LIST_WITHOUT_ID_MASK = 0x0401;
}
