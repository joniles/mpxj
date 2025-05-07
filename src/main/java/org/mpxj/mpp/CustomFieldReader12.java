/*
 * file:       CustomFieldReader12.java
 * author:     Jon Iles
 * copyright:  (c) Packwood Software 2015
 * date:       2014-05-09
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

import org.mpxj.CustomFieldContainer;
import org.mpxj.DataType;
import org.mpxj.ProjectFile;
import org.mpxj.common.ByteArrayHelper;
import org.mpxj.common.FieldTypeHelper;

/**
 * Reads field aliases from an MPP file.
 */
class CustomFieldReader12
{
   /**
    * Constructor.
    *
    * @param file project file
    * @param data raw MP data
    */
   public CustomFieldReader12(ProjectFile file, byte[] data)
   {
      m_file = file;
      m_fields = file.getCustomFields();
      m_data = data;
   }

   /**
    * Process field aliases.
    */
   public void process()
   {
      if (m_data == null)
      {
         return;
      }

      Map<Integer, String> aliasMap = new HashMap<>();
      int index = 0;
      int offset = 0;
      // First the length (repeated twice)
      int aliasBlockSize = ByteArrayHelper.getInt(m_data, offset);
      offset += 8;
      // Then the number of custom columns
      int numberOfAliases = ByteArrayHelper.getInt(m_data, offset);
      offset += 4;

      // Then the aliases themselves
      while (index < numberOfAliases && offset < aliasBlockSize)
      {
         // Get the Field ID
         int fieldID = ByteArrayHelper.getInt(m_data, offset);
         offset += 4;

         // Get the alias offset (offset + 4 for some reason).
         int aliasOffset = ByteArrayHelper.getInt(m_data, offset) + 4;
         offset += 4;
         // Read the alias itself
         if (aliasOffset < m_data.length)
         {
            String alias = MPPUtility.getUnicodeString(m_data, aliasOffset);
            if (!alias.isEmpty())
            {
               aliasMap.put(Integer.valueOf(fieldID), alias);
            }
         }
         index++;
      }

      // NOTE: the blocks here all follow the same format.
      // 4 byte block size (excluding these 4 bytes)
      // 4 byte block size again
      // 4 byte item count
      // N bytes of data (if item count is non-zero)
      // The first block contains alias details
      // The last block may be enterprise custom field details (as per MPP14)
      // Not sure about the other blocks.

      // Skip past the alias block
      offset = 4 + aliasBlockSize;

      // Unknown block 1: size, size count
      int unknownBlock1Size = ByteArrayHelper.getInt(m_data, offset);
      offset += 4;
      offset += unknownBlock1Size;

      // Unknown block 2: size, size count
      int unknownBlock2Size = ByteArrayHelper.getInt(m_data, offset);
      offset += 4;
      offset += unknownBlock2Size;

      // Field definitions block
      // size repeated twice hence 8 bytes
      //int definitionsBlockSize = MPPUtility.getInt(m_data, offset);
      offset += 8;

      int numberOfDefinitions = ByteArrayHelper.getInt(m_data, offset);
      offset += 4;

      for (int definitionIndex = 0; definitionIndex < numberOfDefinitions; definitionIndex++)
      {
         // FieldType fieldType = FieldTypeHelper.getInstance(MPPUtility.getInt(m_data, offset));
         //CustomField customField = m_fields.getCustomField(FieldTypeHelper.getInstance(MPPUtility.getInt(m_data, offset)));
         //System.out.println(fieldType + "\t" + ByteArrayHelper.hexdump(m_data, offset, 8, false));
         offset += 8;
      }

      //System.out.println(ByteArrayHelper.hexdump(m_data, offset, m_data.length-offset, false));
      for (int definitionIndex = 0; definitionIndex < numberOfDefinitions; definitionIndex++)
      {
         if (offset + 2 > m_data.length)
         {
            break;
         }

         int blockSize = ByteArrayHelper.getShort(m_data, offset);
         if (offset + blockSize > m_data.length)
         {
            break;
         }

         DataType customFieldDataType = EnterpriseCustomFieldDataType.getDataTypeFromID(ByteArrayHelper.getShort(m_data, offset + 12));
         FieldTypeHelper.getInstance(m_file, ByteArrayHelper.getInt(m_data, offset + 4), customFieldDataType);

         offset += blockSize;
      }

      aliasMap.forEach((k, v) -> m_fields.getOrCreate(FieldTypeHelper.getInstance(m_file, k.intValue())).setAlias(v));
   }

   private final ProjectFile m_file;
   private final CustomFieldContainer m_fields;
   private final byte[] m_data;
}
