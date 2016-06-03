/*
 * file:       CustomFieldValueReader14.java
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

import net.sf.mpxj.CustomFieldContainer;
import net.sf.mpxj.FieldType;
import net.sf.mpxj.ProjectProperties;
import net.sf.mpxj.common.FieldTypeHelper;
import net.sf.mpxj.common.NumberHelper;

/**
 * MPP14 custom field value reader.
 */
public class CustomFieldValueReader14 extends CustomFieldValueReader
{
   /**
    * Constructor.
    *
    * @param properties project properties
    * @param container custom field config
    * @param outlineCodeVarMeta raw mpp data
    * @param outlineCodeVarData raw mpp data
    * @param outlineCodeFixedData raw mpp data
    * @param outlineCodeFixedData2 raw mpp data
    * @param taskProps raw mpp data
    */
   public CustomFieldValueReader14(ProjectProperties properties, CustomFieldContainer container, VarMeta outlineCodeVarMeta, Var2Data outlineCodeVarData, FixedData outlineCodeFixedData, FixedData outlineCodeFixedData2, Props taskProps)
   {
      super(properties, container, outlineCodeVarMeta, outlineCodeVarData, outlineCodeFixedData, outlineCodeFixedData2, taskProps);
   }

   @Override public void process()
   {
      Integer[] uniqueid = m_outlineCodeVarMeta.getUniqueIdentifierArray();
      int parentOffset;
      int typeOffset;
      int fieldOffset;

      if (NumberHelper.getInt(m_properties.getApplicationVersion()) > ApplicationVersion.PROJECT_2010)
      {
         typeOffset = 16;
         fieldOffset = 18;
         parentOffset = 10;
      }
      else
      {
         fieldOffset = 16;
         typeOffset = 32;
         parentOffset = 8;
      }

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
            item.setParent(Integer.valueOf(MPPUtility.getShort(b, parentOffset)));
         }

         byte[] b2 = m_outlineCodeFixedData2.getByteArrayValue(loop + 3);
         if (b2 != null)
         {
            item.setGuid(MPPUtility.getGUID(b2, 0));
            UUID parentField = MPPUtility.getGUID(b2, fieldOffset);
            int type = MPPUtility.getShort(b2, typeOffset);
            item.setValue(getTypedValue(type, value));
            FieldType field = map.get(parentField);
            m_container.getCustomField(field).getLookupTable().add(item);
         }
      }
   }

   /**
    * Generate a map of UUID values to field types.
    *
    * @return uUID field value map
    */
   private Map<UUID, FieldType> populateCustomFieldMap()
   {
      byte[] data = m_taskProps.getByteArray(Props.CUSTOM_FIELDS);

      Map<UUID, FieldType> map = new HashMap<UUID, FieldType>();
      int index = 44;
      while (index + 88 <= data.length)
      {
         FieldType field = FieldTypeHelper.getInstance(MPPUtility.getInt(data, index + 0));
         UUID guid = MPPUtility.getGUID(data, index + 36);
         map.put(guid, field);

         index += 88;
      }
      return map;
   }
}
