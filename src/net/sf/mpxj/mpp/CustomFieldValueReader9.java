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

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import net.sf.mpxj.CustomField;
import net.sf.mpxj.CustomFieldContainer;
import net.sf.mpxj.CustomFieldLookupTable;
import net.sf.mpxj.DataType;
import net.sf.mpxj.Duration;
import net.sf.mpxj.FieldType;
import net.sf.mpxj.ProjectProperties;
import net.sf.mpxj.TimeUnit;
import net.sf.mpxj.common.FieldTypeHelper;

/**
 * MPP9 custom field value reader.
 */
public class CustomFieldValueReader9
{
   /**
    * Constructor.
    *
    * @param properties MPXJ project properties
    * @param projectProps MPP project properties
    * @param container cusotm field container
    */
   public CustomFieldValueReader9(ProjectProperties properties, Props projectProps, CustomFieldContainer container)
   {
      m_properties = properties;
      m_projectProps = projectProps;
      m_container = container;
   }

   /**
    * Reads custom field values and populates container.
    */
   public void process()
   {
      byte[] data = m_projectProps.getByteArray(Props.TASK_FIELD_ATTRIBUTES);
      if (data != null)
      {
         int index = 0;
         int offset = 0;
         // First the length
         int length = MPPUtility.getInt(data, offset);
         offset += 4;
         // Then the number of custom value lists
         int numberOfValueLists = MPPUtility.getInt(data, offset);
         offset += 4;

         // Then the value lists themselves
         FieldType field;
         int valueListOffset = 0;
         while (index < numberOfValueLists && offset < length)
         {
            // Each item consists of the Field ID (4 bytes) and the offset to the value list (4 bytes)

            // Get the Field
            field = FieldTypeHelper.getInstance(MPPUtility.getInt(data, offset));
            offset += 4;

            // Get the value list offset
            valueListOffset = MPPUtility.getInt(data, offset);
            offset += 4;
            // Read the value list itself
            if (valueListOffset < data.length)
            {
               int tempOffset = valueListOffset;
               tempOffset += 8;
               // Get the data offset
               int dataOffset = MPPUtility.getInt(data, tempOffset) + valueListOffset;
               tempOffset += 4;
               // Get the end of the data offset
               int endDataOffset = MPPUtility.getInt(data, tempOffset) + valueListOffset;
               tempOffset += 4;
               // Get the end of the description
               int endDescriptionOffset = MPPUtility.getInt(data, tempOffset) + valueListOffset;

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
    * Populate the container, converting raw data into Java types.
    *
    * @param field custom field to which these values belong
    * @param values raw value data
    * @param descriptions raw description data
    */
   private void populateContainer(FieldType field, byte[] values, byte[] descriptions)
   {
      CustomField config = m_container.getCustomField(field);
      CustomFieldLookupTable table = config.getLookupTable();

      List<Object> descriptionList = convertType(DataType.STRING, descriptions);
      List<Object> valueList = convertType(field.getDataType(), values);
      for (int index = 0; index < descriptionList.size(); index++)
      {
         CustomFieldValueItem item = new CustomFieldValueItem(Integer.valueOf(0));
         item.setDescription((String) descriptionList.get(index));
         if (index < valueList.size())
         {
            item.setValue(valueList.get(index));
         }
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
      List<Object> result = new ArrayList<Object>();
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
               Date value = MPPUtility.getTimestamp(data, index);
               result.add(value);
               index += 4;
               break;

            }

            case DURATION:
            {
               TimeUnit units = MPPUtility.getDurationTimeUnits(MPPUtility.getShort(data, index + 4), m_properties.getDefaultDurationUnits());
               Duration value = MPPUtility.getAdjustedDuration(m_properties, MPPUtility.getInt(data, index), units);
               result.add(value);
               index += 6;
               break;
            }

            case BOOLEAN:
            {
               Boolean value = Boolean.valueOf(MPPUtility.getShort(data, index) == 1);
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

   private final ProjectProperties m_properties;
   private final Props m_projectProps;
   private final CustomFieldContainer m_container;
}
