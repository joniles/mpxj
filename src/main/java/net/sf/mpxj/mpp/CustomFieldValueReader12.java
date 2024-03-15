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

import java.util.Map;
import java.util.UUID;

import net.sf.mpxj.CustomFieldLookupTable;
import net.sf.mpxj.CustomFieldValueDataType;
import net.sf.mpxj.FieldType;
import net.sf.mpxj.ProjectFile;

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
    */
   public CustomFieldValueReader12(ProjectFile file, Map<UUID, FieldType> lookupTableMap, VarMeta outlineCodeVarMeta, Var2Data outlineCodeVarData, FixedData outlineCodeFixedData, FixedData outlineCodeFixedData2)
   {
      super(file, lookupTableMap, outlineCodeVarMeta, outlineCodeVarData, outlineCodeFixedData, outlineCodeFixedData2);
   }

   @Override public void process()
   {
      int parentOffset = 8;
      int typeOffset = 48;
      int fieldOffset = 32;

      Integer[] uniqueid = m_outlineCodeVarMeta.getUniqueIdentifierArray();

      for (int loop = 0; loop < uniqueid.length; loop++)
      {
         byte[] fixedData2 = m_outlineCodeFixedData2.getByteArrayValue(loop + 3);
         if (fixedData2 == null)
         {
            continue;
         }

         Integer id = uniqueid[loop];
         CustomFieldValueItem item = new CustomFieldValueItem(id);
         byte[] value = m_outlineCodeVarData.getByteArray(id, VALUE_LIST_VALUE);
         item.setDescription(m_outlineCodeVarData.getUnicodeString(id, VALUE_LIST_DESCRIPTION));
         item.setUnknown(m_outlineCodeVarData.getByteArray(id, VALUE_LIST_UNKNOWN));

         byte[] fixedData = m_outlineCodeFixedData.getByteArrayValue(loop + 3);
         if (fixedData != null)
         {
            item.setParentUniqueID(Integer.valueOf(MPPUtility.getShort(fixedData, parentOffset)));
         }

         item.setGUID(MPPUtility.getGUID(fixedData2, 0));
         UUID lookupTableGuid = MPPUtility.getGUID(fixedData2, fieldOffset);
         item.setType(CustomFieldValueDataType.getInstance(MPPUtility.getShort(fixedData2, typeOffset)));
         item.setValue(getTypedValue(item.getType(), value));

         m_container.registerValue(item);
         FieldType field = m_lookupTableMap.get(lookupTableGuid);
         if (field != null)
         {
            CustomFieldLookupTable table = m_container.getOrCreate(field).getLookupTable();
            table.add(item);
            // It's like this to avoid creating empty lookup tables. Need to refactor!
            table.setGUID(lookupTableGuid);
         }
      }
   }
}
