/*
 * file:       CustomFieldAliasReader.java
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

package net.sf.mpxj.mpp;

import net.sf.mpxj.CustomFieldContainer;
import net.sf.mpxj.common.FieldTypeHelper;

/**
 * Reads field aliases from an MPP file.
 */
class CustomFieldAliasReader
{
   /**
    * Constructor.
    *
    * @param fields field definition container
    * @param data raw MP data
    */
   public CustomFieldAliasReader(CustomFieldContainer fields, byte[] data)
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
         int length = MPPUtility.getInt(m_data, offset);
         offset += 8;
         // Then the number of custom columns
         int numberOfAliases = MPPUtility.getInt(m_data, offset);
         offset += 4;

         // Then the aliases themselves
         while (index < numberOfAliases && offset < length)
         {
            // Each item consists of the Field ID (2 bytes), 40 0B marker (2 bytes), and the
            // offset to the string (4 bytes)

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
      }
   }

   private final CustomFieldContainer m_fields;
   private final byte[] m_data;
}
