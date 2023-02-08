/*
 * file:       CustomFieldValueReader12.java
 * author:     Jon Iles
 * copyright:  (c) Packwood Software 2015
 * date:       28/04/2015
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
import java.util.UUID;

import net.sf.mpxj.CustomFieldLookupTable;
import net.sf.mpxj.CustomFieldValueDataType;
import net.sf.mpxj.FieldType;
import net.sf.mpxj.ProjectFile;
import net.sf.mpxj.common.FieldTypeHelper;

/**
 * MPP12 custom field value reader.
 */
public class CustomFieldValueReader12 extends CustomFieldValueReader
{
   /**
    * Constructor.
    *
    * @param file project file
    * @param outlineCodeVarMeta raw mpp data
    * @param outlineCodeVarData raw mpp data
    * @param outlineCodeFixedData raw mpp data
    * @param outlineCodeFixedData2 raw mpp data
    * @param taskProps raw mpp data
    */
   public CustomFieldValueReader12(ProjectFile file, VarMeta outlineCodeVarMeta, Var2Data outlineCodeVarData, FixedData outlineCodeFixedData, FixedData outlineCodeFixedData2, Props taskProps)
   {
      super(file, outlineCodeVarMeta, outlineCodeVarData, outlineCodeFixedData, outlineCodeFixedData2, taskProps);
   }

   @Override public void process()
   {
      Integer[] uniqueid = m_outlineCodeVarMeta.getUniqueIdentifierArray();

      Map<UUID, FieldType> map = populateCustomFieldMap();

      for (int loop = 0; loop < uniqueid.length; loop++)
      {
         Integer id = uniqueid[loop];

         CustomFieldValueItem item = new CustomFieldValueItem(id);
         byte[] value = m_outlineCodeVarData.getByteArray(id, VALUE_LIST_VALUE);
         item.setDescription(m_outlineCodeVarData.getUnicodeString(id, VALUE_LIST_DESCRIPTION));
         item.setUnknown(m_outlineCodeVarData.getByteArray(id, VALUE_LIST_UNKNOWN));

         byte[] b = m_outlineCodeFixedData.getByteArrayValue(loop + 3);
         if (b != null)
         {
            item.setParent(Integer.valueOf(MPPUtility.getShort(b, 8)));
         }

         byte[] b2 = m_outlineCodeFixedData2.getByteArrayValue(loop + 3);
         item.setGUID(MPPUtility.getGUID(b2, 0));
         UUID lookupTableGuid = MPPUtility.getGUID(b2, 32);
         item.setType(CustomFieldValueDataType.getInstance(MPPUtility.getShort(b2, 48)));
         item.setValue(getTypedValue(item.getType(), value));

         m_container.registerValue(item);
         FieldType field = map.get(lookupTableGuid);
         if (field != null)
         {
            CustomFieldLookupTable table = m_container.getOrCreate(field).getLookupTable();
            table.add(item);
            // It's like this to avoid creating empty lookup tables. Need to refactor!
            table.setGUID(lookupTableGuid);
         }
      }
   }

   /**
    * Generate a map of UUID values to field types.
    *
    * @return UUID field value map
    */
   private Map<UUID, FieldType> populateCustomFieldMap()
   {
      Map<UUID, FieldType> map = new HashMap<>();
      byte[] data = m_taskProps.getByteArray(Props.CUSTOM_FIELDS);
      if (data != null)
      {
         int length = MPPUtility.getInt(data, 0);
         int index = length + 36;

         // 4 byte record count
         int recordCount = MPPUtility.getInt(data, index);
         index += 4;

         // 8 bytes per record
         index += (8 * recordCount);

         while (index + 176 <= data.length)
         {
            int blockLength = MPPUtility.getInt(data, index);
            if (blockLength <= 0 || index + blockLength > data.length)
            {
               break;
            }

            int customFieldID = MPPUtility.getInt(data, index + 4);
            FieldType field = FieldTypeHelper.getInstance(m_file, customFieldID);
            UUID lookupTableGuid = MPPUtility.getGUID(data, index + 160);
            map.put(lookupTableGuid, field);
            index += blockLength;
         }
      }
      return map;
   }
}
