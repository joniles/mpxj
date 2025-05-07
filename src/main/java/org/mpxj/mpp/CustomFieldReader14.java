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
class CustomFieldReader14
{
   /**
    * Constructor.
    *
    * @param file project file
    * @param data raw MP data
    */
   public CustomFieldReader14(ProjectFile file, byte[] data)
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

      // Skip past the alias block
      offset = 4 + aliasBlockSize;

      // Unknown block 1: size, size count
      if (offset + 4 > m_data.length)
      {
         return;
      }
      int unknownBlock1Size = ByteArrayHelper.getInt(m_data, offset);
      offset += 4;
      offset += unknownBlock1Size;
      if (offset > m_data.length)
      {
         return;
      }

      // Unknown block 2: size, size count
      if (offset + 4 > m_data.length)
      {
         return;
      }
      int unknownBlock2Size = ByteArrayHelper.getInt(m_data, offset);
      offset += 4;
      offset += unknownBlock2Size;
      if (offset > m_data.length)
      {
         return;
      }

      // Field definitions block
      if (offset + 8 > m_data.length)
      {
         return;
      }
      int numberOfDefinitions = ByteArrayHelper.getInt(m_data, offset);
      offset += 4;

      //int definitionsBlockSize = MPPUtility.getInt(m_data, offset);
      offset += 4;

      // 88 byte blocks
      for (int definitionIndex = 0; definitionIndex < numberOfDefinitions; definitionIndex++)
      {
         // stop if we've run out of data
         if (offset + 88 > m_data.length)
         {
            break;
         }

         DataType customFieldDataType = EnterpriseCustomFieldDataType.getDataTypeFromID(ByteArrayHelper.getShort(m_data, offset + 12));
         FieldTypeHelper.getInstance(m_file, ByteArrayHelper.getInt(m_data, offset), customFieldDataType);

         offset += 88;
      }

      aliasMap.forEach((k, v) -> m_fields.getOrCreate(FieldTypeHelper.getInstance(m_file, k.intValue())).setAlias(v));
   }

   private final ProjectFile m_file;
   private final CustomFieldContainer m_fields;
   private final byte[] m_data;
}
