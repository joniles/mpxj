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

package org.mpxj.mpp;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.UUID;

import org.mpxj.ProjectFile;
import org.apache.poi.poifs.filesystem.DirectoryEntry;
import org.apache.poi.poifs.filesystem.DocumentEntry;
import org.apache.poi.poifs.filesystem.DocumentInputStream;

import org.mpxj.CustomField;
import org.mpxj.CustomFieldContainer;
import org.mpxj.CustomFieldLookupTable;
import org.mpxj.CustomFieldValueDataType;
import org.mpxj.DataType;
import org.mpxj.Duration;
import org.mpxj.FieldType;
import org.mpxj.FieldTypeClass;
import org.mpxj.ProjectProperties;
import org.mpxj.TimeUnit;
import org.mpxj.common.ByteArrayHelper;
import org.mpxj.common.FieldTypeHelper;
import org.mpxj.common.Pair;

/**
 * MPP9 custom field value reader.
 */
public class CustomFieldValueReader9
{
   /**
    * Constructor.
    *
    * @param projectDir project directory
    * @param file project file
    * @param projectProps MPP project properties
    */
   public CustomFieldValueReader9(DirectoryEntry projectDir, ProjectFile file, Props projectProps)
   {
      m_projectDir = projectDir;
      m_file = file;
      m_properties = file.getProjectProperties();
      m_projectProps = projectProps;
      m_container = file.getCustomFields();
   }

   /**
    * Reads custom field values and populates container.
    */
   public void process() throws IOException
   {
      processCustomFieldValues();
      processOutlineCodeValues();
   }

   /**
    * Reads non outline code custom field values and populates container.
    */
   private void processCustomFieldValues()
   {
      byte[] data = m_projectProps.getByteArray(Props.TASK_FIELD_ATTRIBUTES);
      if (data != null)
      {
         int index = 0;
         int offset = 0;
         // First the length
         int length = ByteArrayHelper.getInt(data, offset);
         offset += 4;
         // Then the number of custom value lists
         int numberOfValueLists = ByteArrayHelper.getInt(data, offset);
         offset += 4;

         // Then the value lists themselves
         FieldType field;
         int valueListOffset;
         while (index < numberOfValueLists && offset < length)
         {
            // Each item consists of the Field ID (4 bytes) and the offset to the value list (4 bytes)

            // Get the Field
            field = FieldTypeHelper.getInstance(m_file, ByteArrayHelper.getInt(data, offset));
            offset += 4;

            // Get the value list offset
            valueListOffset = ByteArrayHelper.getInt(data, offset);
            offset += 4;
            // Read the value list itself
            if (valueListOffset < data.length)
            {
               int tempOffset = valueListOffset;
               tempOffset += 8;
               // Get the data offset
               int dataOffset = ByteArrayHelper.getInt(data, tempOffset) + valueListOffset;
               tempOffset += 4;
               // Get the end of the data offset
               int endDataOffset = ByteArrayHelper.getInt(data, tempOffset) + valueListOffset;
               tempOffset += 4;
               // Get the end of the description
               int endDescriptionOffset = ByteArrayHelper.getInt(data, tempOffset) + valueListOffset;

               // Get the values themselves
               int valuesLength = endDataOffset - dataOffset;
               byte[] values = new byte[valuesLength];
               MPPUtility.getByteArray(data, dataOffset, valuesLength, values, 0);

               // Get the descriptions
               int descriptionsLength = endDescriptionOffset - endDataOffset;
               byte[] descriptions = new byte[descriptionsLength];
               MPPUtility.getByteArray(data, endDataOffset, descriptionsLength, descriptions, 0);

               populateContainer(field, values, descriptions);
            }
            index++;
         }
      }
   }

   /**
    * Reads outline code custom field values and populates container.
    */
   private void processOutlineCodeValues() throws IOException
   {
      DirectoryEntry outlineCodeDir = (DirectoryEntry) m_projectDir.getEntry("TBkndOutlCode");
      FixedMeta fm = new FixedMeta(new DocumentInputStream(((DocumentEntry) outlineCodeDir.getEntry("FixedMeta"))), 10);
      FixedData fd = new FixedData(fm, new DocumentInputStream(((DocumentEntry) outlineCodeDir.getEntry("FixedData"))));

      Map<Integer, FieldType> map = new HashMap<>();

      int items = fm.getItemCount();
      for (int loop = 0; loop < items; loop++)
      {
         byte[] data = fd.getByteArrayValue(loop);
         if (data.length < 18)
         {
            continue;
         }

         int fieldID = ByteArrayHelper.getInt(data, 12);
         FieldType fieldType = FieldTypeHelper.getInstance(m_file, fieldID);
         if (fieldType != null && fieldType.getFieldTypeClass() != FieldTypeClass.UNKNOWN)
         {
            int index = ByteArrayHelper.getShort(data, 0);
            map.put(Integer.valueOf(index), fieldType);
         }
      }

      VarMeta outlineCodeVarMeta = new VarMeta9(new DocumentInputStream(((DocumentEntry) outlineCodeDir.getEntry("VarMeta"))));
      Var2Data outlineCodeVarData = new Var2Data(m_file, outlineCodeVarMeta, new DocumentInputStream(((DocumentEntry) outlineCodeDir.getEntry("Var2Data"))));

      Map<FieldType, List<Pair<String, String>>> valueMap = new HashMap<>();

      for (Integer id : outlineCodeVarMeta.getUniqueIdentifierArray())
      {
         FieldType fieldType = map.get(id);
         String value = outlineCodeVarData.getUnicodeString(id, VALUE);
         String description = outlineCodeVarData.getUnicodeString(id, DESCRIPTION);

         List<Pair<String, String>> list = valueMap.computeIfAbsent(fieldType, k -> new ArrayList<>());
         list.add(new Pair<>(value, description));
      }

      for (Entry<FieldType, List<Pair<String, String>>> entry : valueMap.entrySet())
      {
         populateContainer(entry.getKey(), entry.getValue());
      }
   }

   /**
    * Populate the container, converting raw data into Java types.
    *
    * @param field custom field to which these values belong
    * @param values raw value data
    * @param descriptions raw description data
    */
   private void populateContainer(FieldType field, byte[] values, byte[] descriptions)
   {
      CustomField config = m_container.getOrCreate(field);
      CustomFieldLookupTable table = config.getLookupTable();
      String fieldTypeName = config.getFieldType().getName();
      table.setGUID(UUID.nameUUIDFromBytes(fieldTypeName.getBytes()));

      List<Object> descriptionList = convertType(DataType.STRING, descriptions);
      List<Object> valueList = convertType(field.getDataType(), values);
      CustomFieldValueDataType itemType = getDataType(field);
      for (int index = 0; index < descriptionList.size(); index++)
      {
         CustomFieldValueItem item = new CustomFieldValueItem(Integer.valueOf(++m_valueUniqueIDCounter));
         item.setDescription((String) descriptionList.get(index));
         item.setType(itemType);
         item.setGUID(UUID.nameUUIDFromBytes((fieldTypeName + item.getUniqueID()).getBytes()));
         if (index < valueList.size())
         {
            item.setValue(valueList.get(index));
         }
         m_container.registerValue(item);
         table.add(item);
      }
   }

   /**
    * Populate the container from outline code data.
    *
    * @param field field type
    * @param items pairs of values and descriptions
    */
   private void populateContainer(FieldType field, List<Pair<String, String>> items)
   {
      CustomField config = m_container.getOrCreate(field);
      CustomFieldLookupTable table = config.getLookupTable();
      String fieldTypeName = field == null ? "Unknown" : field.getName();
      table.setGUID(UUID.nameUUIDFromBytes(fieldTypeName.getBytes()));
      CustomFieldValueDataType itemType = getDataType(field);

      for (Pair<String, String> pair : items)
      {
         CustomFieldValueItem item = new CustomFieldValueItem(Integer.valueOf(++m_valueUniqueIDCounter));
         item.setValue(pair.getFirst());
         item.setDescription(pair.getSecond());
         item.setType(itemType);
         item.setGUID(UUID.nameUUIDFromBytes((fieldTypeName + item.getUniqueID()).getBytes()));
         table.add(item);
      }
   }

   /**
    * Convert raw data into Java types.
    *
    * @param type data type
    * @param data raw data
    * @return list of Java object
    */
   private List<Object> convertType(DataType type, byte[] data)
   {
      List<Object> result = new ArrayList<>();
      int index = 0;

      while (index < data.length)
      {
         switch (type)
         {
            case STRING:
            {
               String value = MPPUtility.getUnicodeString(data, index);
               result.add(value);
               index += ((value.length() + 1) * 2);
               break;
            }

            case CURRENCY:
            {
               Double value = Double.valueOf(MPPUtility.getDouble(data, index) / 100);
               result.add(value);
               index += 8;
               break;
            }

            case NUMERIC:
            {
               Double value = Double.valueOf(MPPUtility.getDouble(data, index));
               result.add(value);
               index += 8;
               break;
            }

            case DATE:
            {
               LocalDateTime value = MPPUtility.getTimestamp(data, index);
               result.add(value);
               index += 4;
               break;

            }

            case DURATION:
            {
               TimeUnit units = MPPUtility.getDurationTimeUnits(ByteArrayHelper.getShort(data, index + 4), m_properties.getDefaultDurationUnits());
               Duration value = MPPUtility.getAdjustedDuration(m_properties, ByteArrayHelper.getInt(data, index), units);
               result.add(value);
               index += 6;
               break;
            }

            case BOOLEAN:
            {
               Boolean value = Boolean.valueOf(ByteArrayHelper.getShort(data, index) == 1);
               result.add(value);
               index += 2;
               break;
            }

            default:
            {
               index = data.length;
               break;
            }
         }
      }

      return result;
   }

   /**
    * Retrieve the CustomFieldValueDataType instance for a custom field.
    *
    * @param field custom field
    * @return CustomFieldValueDataType instance
    */
   private CustomFieldValueDataType getDataType(FieldType field)
   {
      CustomFieldValueDataType result = null;
      if (field != null)
      {
         result = TYPE_MAP.get(field.getDataType());
      }

      if (result == null)
      {
         result = CustomFieldValueDataType.TEXT;
      }
      return result;
   }

   private final DirectoryEntry m_projectDir;
   private final ProjectFile m_file;
   private final ProjectProperties m_properties;
   private final Props m_projectProps;
   private final CustomFieldContainer m_container;
   private int m_valueUniqueIDCounter;
   private static final Integer VALUE = Integer.valueOf(1);
   private static final Integer DESCRIPTION = Integer.valueOf(2);

   private static final Map<DataType, CustomFieldValueDataType> TYPE_MAP = new HashMap<>();
   static
   {
      TYPE_MAP.put(DataType.STRING, CustomFieldValueDataType.TEXT);
      TYPE_MAP.put(DataType.CURRENCY, CustomFieldValueDataType.COST);
      TYPE_MAP.put(DataType.NUMERIC, CustomFieldValueDataType.NUMBER);
      TYPE_MAP.put(DataType.DATE, CustomFieldValueDataType.DATE);
      TYPE_MAP.put(DataType.DURATION, CustomFieldValueDataType.DURATION);
      TYPE_MAP.put(DataType.BOOLEAN, CustomFieldValueDataType.FLAG);
   }
}
