
package net.sf.mpxj.mpp;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import net.sf.mpxj.CustomFieldConfigContainer;
import net.sf.mpxj.FieldType;
import net.sf.mpxj.ProjectProperties;
import net.sf.mpxj.common.FieldTypeHelper;

/**
 * MPP12 custom field value reader.
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
   public CustomFieldValueReader14(ProjectProperties properties, CustomFieldConfigContainer container, VarMeta outlineCodeVarMeta, Var2Data outlineCodeVarData, FixedData outlineCodeFixedData, FixedData outlineCodeFixedData2, Props taskProps)
   {
      super(properties, container, outlineCodeVarMeta, outlineCodeVarData, outlineCodeFixedData, outlineCodeFixedData2, taskProps);
   }

   @Override public void process()
   {
      Integer[] uniqueid = m_outlineCodeVarMeta.getUniqueIdentifierArray();
      int parentOffset = m_properties.getFullApplicationName().equals("Microsoft.Project 15.0") ? 10 : 8;

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
         item.setGuid(MPPUtility.getGUID(b2, 0));
         UUID parentField = MPPUtility.getGUID(b2, 16);
         int type = MPPUtility.getShort(b2, 32);
         item.setValue(getTypedValue(type, value));

         FieldType field = map.get(parentField);

         m_container.getCustomFieldConfig(field).getLookupTable().add(item);
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
