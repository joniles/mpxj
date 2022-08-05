/*
 * file:       CustomFieldReader14.java
 * author:     Jon Iles
 * copyright:  (c) Packwood Software 2022
 * date:       2022-08-05
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

import java.util.HashMap;
import java.util.Map;

import net.sf.mpxj.CustomField;
import net.sf.mpxj.CustomFieldContainer;
import net.sf.mpxj.DataType;
import net.sf.mpxj.FieldType;
import net.sf.mpxj.common.ByteArrayHelper;
import net.sf.mpxj.common.FieldTypeHelper;

/**
 * Reads field aliases from an MPP file.
 */
class CustomFieldReader14
{
   /**
    * Constructor.
    *
    * @param fields field definition container
    * @param data raw MP data
    */
   public CustomFieldReader14(CustomFieldContainer fields, byte[] data)
   {
      m_fields = fields;
      m_data = data;
   }

   /**
    * Process field aliases.
    */
   public void process()
   {
      if (m_data != null)
      {
         int index = 0;
         int offset = 0;
         // First the length (repeated twice)
         int aliasBlockSize = MPPUtility.getInt(m_data, offset);
         offset += 8;
         // Then the number of custom columns
         int numberOfAliases = MPPUtility.getInt(m_data, offset);
         offset += 4;

         // Then the aliases themselves
         while (index < numberOfAliases && offset < aliasBlockSize)
         {
            // Get the Field ID
            int fieldID = MPPUtility.getInt(m_data, offset);
            offset += 4;

            // Get the alias offset (offset + 4 for some reason).
            int aliasOffset = MPPUtility.getInt(m_data, offset) + 4;
            offset += 4;
            // Read the alias itself
            if (aliasOffset < m_data.length)
            {
               String alias = MPPUtility.getUnicodeString(m_data, aliasOffset);
               m_fields.getCustomField(FieldTypeHelper.getInstance(fieldID)).setAlias(alias);
            }
            index++;
         }

         // Skip past the alias block
         offset = 4 + aliasBlockSize;

         // Unknown block 1: size, size count
         int unknownBlock1Size = MPPUtility.getInt(m_data, offset);
         offset += 4;
         offset += unknownBlock1Size;

         // Unknown block 2: size, size count
         int unknownBlock2Size = MPPUtility.getInt(m_data, offset);
         offset += 4;
         offset += unknownBlock2Size;

         // Field definitions block
         int numberOfDefinitions = MPPUtility.getInt(m_data, offset);
         offset += 4;

         int definitionsBlockSize = MPPUtility.getInt(m_data, offset);
         offset += 4;

         // 88 byte blocks
         for (int definitionIndex=0; definitionIndex < numberOfDefinitions; definitionIndex++)
         {
            CustomField customField = m_fields.getCustomField(FieldTypeHelper.getInstance(MPPUtility.getInt(m_data, offset)));
            int dataTypeValue = MPPUtility.getShort(m_data, offset + 12);
            customField.setDataType(getDataType(dataTypeValue));
            //System.out.println(customField.getFieldType() + "\t" + customField.getAlias() + "\t" + customField.getDataType() + "\t" + dataTypeValue);
            //System.out.println(customField.getFieldType() + "\t" + ByteArrayHelper.hexdump(m_data, offset, 88, false));
            offset += 88;
         }
      }
   }

   private DataType getDataType(int value)
   {
      return DATA_TYPES.get(Integer.valueOf(value));
   }

   private final CustomFieldContainer m_fields;
   private final byte[] m_data;

   private static final Map<Integer, DataType> DATA_TYPES = new HashMap<>();
   static
   {
      DATA_TYPES.put(Integer.valueOf(0), DataType.CURRENCY);
      DATA_TYPES.put(Integer.valueOf(1), DataType.DATE);
      DATA_TYPES.put(Integer.valueOf(2), DataType.DURATION);
      DATA_TYPES.put(Integer.valueOf(4), DataType.BOOLEAN);
      DATA_TYPES.put(Integer.valueOf(5), DataType.NUMERIC);
      DATA_TYPES.put(Integer.valueOf(7), DataType.STRING);
   };
}
