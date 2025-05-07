/*
 * file:       FieldMap.java
 * author:     Jon Iles
 * copyright:  (c) Packwood Software 2011
 * date:       13/04/2011
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

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.mpxj.AccrueType;
import org.mpxj.BookingType;
import org.mpxj.ConstraintType;
import org.mpxj.CustomFieldContainer;
import org.mpxj.DataType;
import org.mpxj.Duration;
import org.mpxj.EarnedValueMethod;
import org.mpxj.FieldContainer;
import org.mpxj.FieldType;
import org.mpxj.FieldTypeClass;
import org.mpxj.Priority;
import org.mpxj.ProjectFile;
import org.mpxj.ProjectProperties;
import org.mpxj.Rate;
import org.mpxj.ResourceRequestType;
import org.mpxj.RtfNotes;
import org.mpxj.TimeUnit;
import org.mpxj.UserDefinedField;
import org.mpxj.WorkGroup;
import org.mpxj.common.ByteArrayHelper;
import org.mpxj.common.FieldTypeHelper;
import org.mpxj.common.NumberHelper;

/**
 * This class is used to represent the mapping present in the MPP file
 * between fields and their locations in various data blocks.
 */
abstract class FieldMap
{
   /**
    * Constructor.
    *
    * @param file project file
    */
   public FieldMap(ProjectFile file)
   {
      m_file = file;
      m_properties = file.getProjectProperties();
      m_customFields = file.getCustomFields();
      m_stringVarDataReader = new StringVarDataFieldReader(m_customFields);
      m_doubleVarDataReader = new DoubleVarDataFieldReader(m_customFields);
      m_timestampVarDataReader = new TimestampVarDataFieldReader(m_customFields);
   }

   /**
    * When set to true, the selected field map will print details of the field structure.
    *
    * @param value pass try to enable output
    */
   public void setDebug(boolean value)
   {
      m_debug = value;
   }

   /**
    * Generic method used to create a field map from a block of data.
    *
    * @param data field map data
    */
   private void createFieldMap(byte[] data)
   {
      int index = 0;
      int lastDataBlockOffset = 0;
      int dataBlockIndex = 0;

      while (index < data.length)
      {
         long mask = ByteArrayHelper.getInt(data, index);
         //mask = mask << 4;

         int dataBlockOffset = ByteArrayHelper.getShort(data, index + 4);
         //int metaFlags = MPPUtility.getByte(data, index + 8);
         int typeValue = ByteArrayHelper.getInt(data, index + 12);
         FieldType type = getFieldType(typeValue);
         int category = ByteArrayHelper.getShort(data, index + 20);
         //int sizeInBytes = MPPUtility.getShort(data, index + 22);
         //int metaIndex = MPPUtility.getInt(data, index + 24);

         //
         // Categories
         //
         // 02 - Short values [RATE_UNITS, WORKGROUP, ACCRUE, TIME_UNITS, PRIORITY, TASK_TYPE, CONSTRAINT, ACCRUE, PERCENTAGE, SHORT, WORK_UNITS]  - BOOKING_TYPE, EARNED_VALUE_METHOD, DELIVERABLE_TYPE, RESOURCE_REQUEST_TYPE - we have as string in MPXJ????
         // 03 - Int values [DURATION, INTEGER] - Recalc outline codes as Boolean?
         // 05 - Rate, Number [RATE, NUMERIC]
         // 08 - String (and some durations!!!) [STRING, DURATION]
         // 0B - Boolean (meta block 0?) - [BOOLEAN]
         // 13 - Date - [DATE]
         // 48 - GUID - [GUID]
         // 64 - Boolean (meta block 1?)- [BOOLEAN]
         // 65 - Work, Currency [WORK, CURRENCY]
         // 66 - Units [UNITS]
         // 1D - Raw bytes [BINARY, ASCII_STRING] - Exception: outline code indexes, they are integers, but stored as part of a binary block

         int varDataKey;
         if (useTypeAsVarDataKey())
         {
            Integer substitute = substituteVarDataKey(type);
            if (substitute == null)
            {
               varDataKey = typeValue & 0x0000FFFF;
            }
            else
            {
               varDataKey = substitute.intValue();
            }
         }
         else
         {
            varDataKey = MPPUtility.getByte(data, index + 6);
         }

         FieldLocation location;
         int metaBlock;

         switch (category)
         {
            case 0x0B:
            {
               location = FieldLocation.META_DATA;
               metaBlock = 0;
               break;
            }

            case 0x64:
            {
               location = FieldLocation.META_DATA;
               metaBlock = 1;
               break;
            }

            default:
            {
               metaBlock = 0;
               if (dataBlockOffset != 65535)
               {
                  location = FieldLocation.FIXED_DATA;
                  if (dataBlockOffset < lastDataBlockOffset)
                  {
                     ++dataBlockIndex;
                  }
                  lastDataBlockOffset = dataBlockOffset;
                  int typeSize = getFixedDataFieldSize(type);

                  if (dataBlockOffset + typeSize > m_maxFixedDataSize[dataBlockIndex])
                  {
                     m_maxFixedDataSize[dataBlockIndex] = dataBlockOffset + typeSize;
                  }
               }
               else
               {
                  if (varDataKey != 0)
                  {
                     location = FieldLocation.VAR_DATA;
                  }
                  else
                  {
                     location = FieldLocation.UNKNOWN;
                  }
               }
               break;
            }
         }

         FieldItem item = new FieldItem(type, location, dataBlockIndex, dataBlockOffset, varDataKey, mask, metaBlock);
         if (m_debug)
         {
            System.out.println(ByteArrayHelper.hexdump(data, index, 28, false) + " " + item + " mpxjDataType=" + item.getType().getDataType() + " index=" + index);
         }
         m_map.put(type, item);

         index += 28;
      }
   }

   /**
    * Used to determine what value is used as the var data key.
    *
    * @return true if the field type value is used as the var data key
    */
   protected abstract boolean useTypeAsVarDataKey();

   /**
    * Abstract method used by child classes to supply default data.
    *
    * @return default data
    */
   protected abstract FieldItem[] getDefaultTaskData();

   /**
    * Abstract method used by child classes to supply default data.
    *
    * @return default data
    */
   protected abstract FieldItem[] getDefaultResourceData();

   /**
    * Abstract method used by child classes to supply default data.
    *
    * @return default data
    */
   protected abstract FieldItem[] getDefaultAssignmentData();

   /**
    * Abstract method used by child classes to supply default data.
    *
    * @return default data
    */
   protected abstract FieldItem[] getDefaultRelationData();

   /**
    * Given a field ID, derive the field type.
    *
    * @param fieldID field ID
    * @return field type
    */
   protected FieldType getFieldType(int fieldID)
   {
      return FieldTypeHelper.getInstance(m_file, fieldID);
   }

   /**
    * In some circumstances the var data key used in the file
    * does not match the var data key derived from the type.
    * This method is used to perform a substitution so that
    * the correct value is used.
    *
    * @param type field type to be tested
    * @return substituted value, or null
    */
   protected abstract Integer substituteVarDataKey(FieldType type);

   /**
    * Creates a field map for tasks.
    *
    * @param props props data
    */
   public void createTaskFieldMap(Props props)
   {
      byte[] fieldMapData = null;
      for (Integer key : TASK_KEYS)
      {
         fieldMapData = props.getByteArray(key);
         if (fieldMapData != null)
         {
            break;
         }
      }

      if (fieldMapData == null)
      {
         populateDefaultData(getDefaultTaskData());
      }
      else
      {
         createFieldMap(fieldMapData);
      }
   }

   /**
    * Creates a field map for relations.
    *
    * @param props props data
    */
   public void createRelationFieldMap(Props props)
   {
      byte[] fieldMapData = null;
      for (Integer key : RELATION_KEYS)
      {
         fieldMapData = props.getByteArray(key);
         if (fieldMapData != null)
         {
            break;
         }
      }

      if (fieldMapData == null)
      {
         populateDefaultData(getDefaultRelationData());
      }
      else
      {
         createFieldMap(fieldMapData);
      }
   }

   /**
    * Create a field map for enterprise custom fields.
    *
    * @param props props data
    * @param fieldTypeClass target class
    */
   public void createEnterpriseCustomFieldMap(Props props, FieldTypeClass fieldTypeClass)
   {
      byte[] fieldMapData = null;
      for (Integer key : ENTERPRISE_CUSTOM_KEYS)
      {
         fieldMapData = props.getByteArray(key);
         if (fieldMapData != null)
         {
            break;
         }
      }

      if (fieldMapData != null)
      {
         int index = 4;
         while (index < fieldMapData.length)
         {
            int typeValue = ByteArrayHelper.getInt(fieldMapData, index);
            FieldType type = getFieldType(typeValue);
            if (type != null && type.getFieldTypeClass() == fieldTypeClass && type instanceof UserDefinedField)
            {
               int varDataKey = (typeValue & 0xFFFF);
               FieldItem item = new FieldItem(type, FieldLocation.VAR_DATA, 0, 0, varDataKey, 0, 0);
               m_map.put(type, item);
               //System.out.println(item);
            }
            //System.out.println((type == null ? "?" : type.getClass().getSimpleName() + "." + type) + " " + Integer.toHexString(typeValue));

            index += 4;
         }
      }
   }

   /**
    * Creates a field map for resources.
    *
    * @param props props data
    */
   public void createResourceFieldMap(Props props)
   {
      byte[] fieldMapData = null;
      for (Integer key : RESOURCE_KEYS)
      {
         fieldMapData = props.getByteArray(key);
         if (fieldMapData != null)
         {
            break;
         }
      }

      if (fieldMapData == null)
      {
         populateDefaultData(getDefaultResourceData());
      }
      else
      {
         createFieldMap(fieldMapData);
      }
   }

   /**
    * Creates a field map for assignments.
    *
    * @param props props data
    */
   public void createAssignmentFieldMap(Props props)
   {
      //System.out.println("ASSIGN");
      byte[] fieldMapData = null;
      for (Integer key : ASSIGNMENT_KEYS)
      {
         fieldMapData = props.getByteArray(key);
         if (fieldMapData != null)
         {
            break;
         }
      }

      if (fieldMapData == null)
      {
         populateDefaultData(getDefaultAssignmentData());
      }
      else
      {
         createFieldMap(fieldMapData);
      }
   }

   /**
    * This method takes an array of data and uses this to populate the
    * field map.
    *
    * @param defaultData field map default data
    */
   private void populateDefaultData(FieldItem[] defaultData)
   {
      for (FieldItem item : defaultData)
      {
         m_map.put(item.getType(), item);
      }
   }

   /**
    * Given a container, and a set of raw data blocks, this method extracts
    * the field data and writes it into the container.
    *
    * @param fieldTypeClass expected type
    * @param container field container
    * @param id entity ID
    * @param fixedData fixed data block
    * @param varData var data block
    */
   public void populateContainer(FieldTypeClass fieldTypeClass, FieldContainer container, Integer id, byte[][] fixedData, Var2Data varData)
   {
      //System.out.println(container.getClass().getSimpleName()+": " + id);
      for (FieldItem item : m_map.values())
      {
         if (item.getType().getFieldTypeClass() == fieldTypeClass)
         {
            //System.out.println(item.m_type);
            Object value = item.read(id, fixedData, varData);
            //System.out.println(item.m_type.getClass().getSimpleName() + "." + item.m_type +  ": " + value);
            container.set(item.getType(), value);
         }
      }
   }

   /**
    * Retrieve the maximum offset in the fixed data block.
    *
    * @param blockIndex required block index
    * @return maximum offset
    */
   public int getMaxFixedDataSize(int blockIndex)
   {
      return m_maxFixedDataSize[blockIndex];
   }

   /**
    * Retrieve the fixed data offset for a specific field.
    *
    * @param type field type
    * @return offset
    */
   public int getFixedDataOffset(FieldType type)
   {
      int result;
      FieldItem item = m_map.get(type);
      if (item != null)
      {
         result = item.getFixedDataOffset();
      }
      else
      {
         result = -1;
      }
      return result;
   }

   /**
    * Retrieve the var data key for a specific field.
    *
    * @param type field type
    * @return var data key
    */
   public Integer getVarDataKey(FieldType type)
   {
      Integer result = null;
      FieldItem item = m_map.get(type);
      if (item != null)
      {
         result = item.getVarDataKey();
      }
      return result;
   }

   /**
    * Used to map from a var data key to a field type. Note this
    * is designed for diagnostic use only, and uses an inefficient search.
    *
    * @param key var data key
    * @return field type
    */
   public FieldType getFieldTypeFromVarDataKey(Integer key)
   {
      FieldType result = null;
      for (Entry<FieldType, FieldMap.FieldItem> entry : m_map.entrySet())
      {
         if (entry.getValue().getFieldLocation() == FieldLocation.VAR_DATA && entry.getValue().getVarDataKey().equals(key))
         {
            result = entry.getKey();
            break;
         }
      }
      return result;
   }

   /**
    * Retrieve the field location for a specific field.
    *
    * @param type field type
    * @return field location
    */
   public FieldLocation getFieldLocation(FieldType type)
   {
      FieldLocation result = null;

      FieldItem item = m_map.get(type);
      if (item != null)
      {
         result = item.getFieldLocation();
      }
      return result;
   }

   /**
    * Retrieve a single field value.
    *
    * @param id parent entity ID
    * @param type field type
    * @param fixedData fixed data block
    * @param varData var data block
    * @return field value
    */
   protected Object getFieldData(Integer id, FieldType type, byte[][] fixedData, Var2Data varData)
   {
      Object result = null;

      FieldItem item = m_map.get(type);
      if (item != null)
      {
         result = item.read(id, fixedData, varData);
      }

      return result;
   }

   /**
    * Retrieve the project properties.
    *
    * @return project file
    */
   protected ProjectProperties getProjectProperties()
   {
      return m_properties;
   }

   /**
    * Clear the field map.
    */
   public void clear()
   {
      m_map.clear();
      Arrays.fill(m_maxFixedDataSize, 0);
   }

   /**
    * Diagnostic method used to dump known field map data.
    *
    * @param props props block containing field map data
    */
   public void dumpKnownFieldMaps(Props props)
   {
      //for (int key=131092; key < 131098; key++)
      for (int key = 50331668; key < 50331674; key++)
      {
         byte[] fieldMapData = props.getByteArray(Integer.valueOf(key));
         if (fieldMapData != null)
         {
            System.out.println("KEY: " + key);
            createFieldMap(fieldMapData);
            System.out.println(this);
            clear();
         }
      }
   }

   /**
    * Determine the size of a field in a fixed data block.
    *
    * @param type field data type
    * @return field size in bytes
    */
   private int getFixedDataFieldSize(FieldType type)
   {
      int result = 0;
      DataType dataType = type.getDataType();
      if (dataType != null)
      {
         switch (dataType)
         {
            case DATE:
            case INTEGER:
            case DURATION:
            {
               result = 4;
               break;
            }

            case TIME_UNITS:
            case CONSTRAINT:
            case PRIORITY:
            case PERCENTAGE:
            case TASK_TYPE:
            case ACCRUE:
            case SHORT:
            case BOOLEAN:
            case DELAY:
            case WORKGROUP:
            case RATE_UNITS:
            case EARNED_VALUE_METHOD:
            case RESOURCE_REQUEST_TYPE:
            {
               result = 2;
               break;
            }

            case CURRENCY:
            case UNITS:
            case RATE:
            case WORK:
            {
               result = 8;
               break;
            }

            case WORK_UNITS:
            {
               result = 1;
               break;
            }

            case GUID:
            {
               result = 16;
               break;
            }

            default:
            {
               result = 0;
               break;
            }
         }
      }

      return result;
   }

   @Override public String toString()
   {
      StringWriter sw = new StringWriter();
      PrintWriter pw = new PrintWriter(sw);

      ArrayList<FieldItem> items = new ArrayList<>(m_map.values());
      Collections.sort(items);

      pw.println("[FieldMap");

      for (int loop = 0; loop < m_maxFixedDataSize.length; loop++)
      {
         pw.print(" MaxFixedOffset (block ");
         pw.print(loop);
         pw.print(")=");
         pw.println(m_maxFixedDataSize[loop]);
      }

      for (FieldItem item : items)
      {
         pw.print(" ");
         pw.println(item);
      }
      pw.println("]");

      pw.close();
      return sw.toString();
   }
   /**
    * Enumeration representing the location of field data.
    */
   enum FieldLocation
   {
      FIXED_DATA,
      VAR_DATA,
      META_DATA,
      UNKNOWN
   }

   /**
    * This class is used to collect together the attributes necessary to
    * describe the location of each field within the MPP file. It also provides
    * the methods used to extract an individual field value.
    */
   public class FieldItem implements Comparable<FieldItem>
   {
      /**
       * Constructor.
       *
       * @param type field type
       * @param location identifies which block the field is present in
       * @param fixedDataBlockIndex identifies which block the data comes from
       * @param fixedDataOffset fixed data block offset
       * @param varDataKey var data block key
       * @param mask TODO
       * @param metaBlock TODO
       */
      FieldItem(FieldType type, FieldLocation location, int fixedDataBlockIndex, int fixedDataOffset, int varDataKey, long mask, int metaBlock)
      {
         m_type = type;
         m_location = location;
         m_fixedDataBlockIndex = fixedDataBlockIndex;
         m_fixedDataOffset = fixedDataOffset;
         m_varDataKey = Integer.valueOf(varDataKey);
         m_mask = mask;
         m_metaBlock = metaBlock;
      }

      /**
       * Reads a single field value.
       *
       * @param id parent entity ID
       * @param fixedData fixed data block
       * @param varData var data block
       * @return field value
       */
      public Object read(Integer id, byte[][] fixedData, Var2Data varData)
      {
         Object result = null;

         switch (m_location)
         {
            case FIXED_DATA:
            {
               result = readFixedData(id, fixedData, varData);
               break;
            }

            case VAR_DATA:
            {
               result = readVarData(m_type.getDataType(), id, fixedData, varData);
               break;
            }

            case META_DATA:
            {
               // We know that the Boolean flags are stored in the
               // "meta data" block, and can see that the first
               // four bytes of each row read from the field map
               // data in the MPP file represents a bit mask... but
               // we just haven't worked out how to convert this into
               // the actual location in the data. For now we rely on
               // the location in the file being fixed. This is why
               // we ignore the META_DATA case.
               break;
            }

            default:
            {
               // Unknown location - ignore this.
               break;
            }
         }

         return result;
      }

      /**
       * Read a field from the fixed data block.
       *
       * @param id parent entity ID
       * @param fixedData fixed data block
       * @param varData var data block
       * @return field value
       */
      private Object readFixedData(Integer id, byte[][] fixedData, Var2Data varData)
      {
         Object result = null;
         if (m_fixedDataBlockIndex < fixedData.length)
         {
            byte[] data = fixedData[m_fixedDataBlockIndex];
            if (data != null && m_fixedDataOffset < data.length)
            {
               switch (m_type.getDataType())
               {
                  case DATE:
                  {
                     result = MPPUtility.getTimestamp(data, m_fixedDataOffset);
                     break;
                  }

                  case INTEGER:
                  {
                     result = Integer.valueOf(ByteArrayHelper.getInt(data, m_fixedDataOffset));
                     break;
                  }

                  case DURATION:
                  {
                     FieldType unitsType = m_type.getUnitsType();
                     TimeUnit units = (TimeUnit) getFieldData(id, unitsType, fixedData, varData);
                     if (units == null)
                     {
                        units = getProjectProperties().getDefaultDurationUnits();
                     }

                     result = MPPUtility.getAdjustedDuration(getProjectProperties(), ByteArrayHelper.getInt(data, m_fixedDataOffset), units);
                     break;
                  }

                  case TIME_UNITS:
                  {
                     result = MPPUtility.getDurationTimeUnits(ByteArrayHelper.getShort(data, m_fixedDataOffset), getProjectProperties().getDefaultDurationUnits());
                     break;
                  }

                  case CONSTRAINT:
                  {
                     result = ConstraintType.getInstance(ByteArrayHelper.getShort(data, m_fixedDataOffset));
                     break;
                  }

                  case PRIORITY:
                  {
                     result = Priority.getInstance(ByteArrayHelper.getShort(data, m_fixedDataOffset));
                     break;
                  }

                  case PERCENTAGE:
                  {
                     result = MPPUtility.getPercentage(data, m_fixedDataOffset);
                     break;
                  }

                  case TASK_TYPE:
                  {
                     result = TaskTypeHelper.getInstance(ByteArrayHelper.getShort(data, m_fixedDataOffset));
                     break;
                  }

                  case ACCRUE:
                  {
                     result = AccrueType.getInstance(ByteArrayHelper.getShort(data, m_fixedDataOffset));
                     break;
                  }

                  case CURRENCY:
                  case UNITS:
                  {
                     // Ignore the amount if result will be less than 0.1 cent or 0.1%
                     double amount = MPPUtility.getDouble(data, m_fixedDataOffset);
                     amount = Math.abs(amount) < 0.1 ? 0 : amount;
                     result = NumberHelper.getDouble(amount / 100);
                     break;
                  }

                  case RATE:
                  {
                     result = new Rate(MPPUtility.getDouble(data, m_fixedDataOffset), TimeUnit.HOURS);
                     break;
                  }

                  case WORK:
                  {
                     // Ignore the duration if result will be less than 1 minute
                     double duration = MPPUtility.getDouble(data, m_fixedDataOffset);
                     duration = Math.abs(duration) < 1000 ? 0 : duration;
                     result = Duration.getInstance(duration / 60000, TimeUnit.HOURS);
                     break;
                  }

                  case SHORT:
                  {
                     result = Integer.valueOf(ByteArrayHelper.getShort(data, m_fixedDataOffset));
                     break;
                  }

                  case BOOLEAN:
                  {
                     result = Boolean.valueOf(ByteArrayHelper.getShort(data, m_fixedDataOffset) != 0);
                     break;
                  }

                  case DELAY:
                  {
                     result = MPPUtility.getDuration(ByteArrayHelper.getShort(data, m_fixedDataOffset), TimeUnit.HOURS);
                     break;
                  }

                  case WORK_UNITS:
                  {
                     int variableRateUnitsValue = MPPUtility.getByte(data, m_fixedDataOffset);
                     result = variableRateUnitsValue == 0 ? null : MPPUtility.getWorkTimeUnits(variableRateUnitsValue);
                     break;
                  }

                  case WORKGROUP:
                  {
                     result = WorkGroup.getInstance(ByteArrayHelper.getShort(data, m_fixedDataOffset));
                     break;
                  }

                  case RATE_UNITS:
                  {
                     result = TimeUnit.getInstance(ByteArrayHelper.getShort(data, m_fixedDataOffset) - 1);
                     break;
                  }

                  case EARNED_VALUE_METHOD:
                  {
                     result = EarnedValueMethod.getInstance(ByteArrayHelper.getShort(data, m_fixedDataOffset));
                     break;
                  }

                  case RESOURCE_REQUEST_TYPE:
                  {
                     result = ResourceRequestType.getInstance(ByteArrayHelper.getShort(data, m_fixedDataOffset));
                     break;
                  }

                  case GUID:
                  {
                     result = MPPUtility.getGUID(data, m_fixedDataOffset);
                     break;
                  }

                  case BINARY:
                  {
                     // Do nothing for binary data
                     break;
                  }

                  default:
                  {
                     //System.out.println("**** UNSUPPORTED FIXED DATA TYPE");
                     break;
                  }
               }
            }
         }
         return result;
      }

      /**
       * Read a field value from a var data block.
       *
       * @param dataType target data type
       * @param id parent entity ID
       * @param fixedData fixed data block
       * @param varData var data block
       * @return field value
       */
      private Object readVarData(DataType dataType, Integer id, byte[][] fixedData, Var2Data varData)
      {
         Object result = null;

         switch (dataType)
         {
            case DURATION:
            {
               FieldType unitsType = m_type.getUnitsType();
               TimeUnit units = (TimeUnit) getFieldData(id, unitsType, fixedData, varData);
               if (units == null)
               {
                  units = TimeUnit.HOURS;
               }
               result = getCustomFieldDurationValue(varData, id, m_varDataKey, units);
               break;
            }

            case TIME_UNITS:
            {
               result = MPPUtility.getDurationTimeUnits(varData.getShort(id, m_varDataKey), getProjectProperties().getDefaultDurationUnits());
               break;
            }

            case CURRENCY:
            {
               // Ignore the amount if result will be less than 0.1 cent
               double amount = varData.getDouble(id, m_varDataKey);
               amount = Math.abs(amount) < 0.1 ? 0 : amount;
               result = NumberHelper.getDouble(amount / 100);
               break;
            }

            case STRING:
            {
               result = m_stringVarDataReader.getValue(varData, id, m_varDataKey);
               break;
            }

            case DATE:
            {
               result = m_timestampVarDataReader.getValue(varData, id, m_varDataKey);
               break;
            }

            case NUMERIC:
            {
               result = m_doubleVarDataReader.getValue(varData, id, m_varDataKey);
               break;
            }

            case INTEGER:
            {
               result = Integer.valueOf(varData.getInt(id, m_varDataKey));
               break;
            }

            case WORK:
            {
               // Ignore the duration if result will be less than 1 minute
               double duration = varData.getDouble(id, m_varDataKey);
               duration = Math.abs(duration) < 1000 ? 0 : duration;
               result = Duration.getInstance(duration / 60000, TimeUnit.HOURS);
               break;
            }

            case NOTES:
            {
               String notes = varData.getString(id, m_varDataKey);
               result = notes == null ? null : new RtfNotes(notes);
               break;
            }

            case DELAY:
            {
               result = MPPUtility.getDuration(varData.getShort(id, m_varDataKey), TimeUnit.HOURS);
               break;
            }

            case WORK_UNITS:
            {
               int variableRateUnitsValue = varData.getByte(id, m_varDataKey);
               result = variableRateUnitsValue == 0 ? null : MPPUtility.getWorkTimeUnits(variableRateUnitsValue);
               break;
            }

            case RATE_UNITS:
            {
               result = TimeUnit.getInstance(varData.getShort(id, m_varDataKey) - 1);
               break;
            }

            case EARNED_VALUE_METHOD:
            {
               result = EarnedValueMethod.getInstance(varData.getShort(id, m_varDataKey));
               break;
            }

            case RESOURCE_REQUEST_TYPE:
            {
               result = ResourceRequestType.getInstance(varData.getShort(id, m_varDataKey));
               break;
            }

            case ACCRUE:
            {
               result = AccrueType.getInstance(varData.getShort(id, m_varDataKey));
               break;
            }

            case PERCENTAGE:
            case SHORT:
            {
               result = Integer.valueOf(varData.getShort(id, m_varDataKey));
               break;
            }

            case BOOLEAN:
            {
               result = Boolean.valueOf(varData.getShort(id, m_varDataKey) != 0);
               break;
            }

            case WORKGROUP:
            {
               result = WorkGroup.getInstance(varData.getShort(id, m_varDataKey));
               break;
            }

            case GUID:
            {
               result = MPPUtility.getGUID(varData.getByteArray(id, m_varDataKey), 0);
               break;
            }

            case BOOKING_TYPE:
            {
               result = BookingType.getInstance(varData.getShort(id, m_varDataKey));
               break;
            }

            case BINARY:
            {
               result = varData.getByteArray(id, m_varDataKey);
               break;
            }

            default:
            {
               //System.out.println("**** UNSUPPORTED VAR DATA TYPE");
               break;
            }
         }

         return result;
      }

      /**
       * Retrieve custom field value.
       *
       * @param varData var data block
       * @param id item ID
       * @param type item type
       * @param units duration units
       * @return item value
       */
      private Object getCustomFieldDurationValue(Var2Data varData, Integer id, Integer type, TimeUnit units)
      {
         Object result = null;

         byte[] data = varData.getByteArray(id, type);

         if (data != null)
         {
            if (data.length == 512)
            {
               result = MPPUtility.getUnicodeString(data, 0);
            }
            else
            {
               if (data.length >= 4)
               {
                  int duration = ByteArrayHelper.getInt(data, 0);
                  result = MPPUtility.getAdjustedDuration(getProjectProperties(), duration, units);
               }
            }
         }

         return result;
      }

      /**
       * Retrieve the field type.
       *
       * @return field type
       */
      public FieldType getType()
      {
         return m_type;
      }

      /**
       * Retrieve the index of the fixed data block containing this item.
       *
       * @return fixed data block index
       */
      public int getFixedDataBlockIndex()
      {
         return m_fixedDataBlockIndex;
      }

      /**
       * Retrieve the fixed data offset for this field.
       *
       * @return fixed data offset
       */
      public int getFixedDataOffset()
      {
         return m_fixedDataOffset;
      }

      /**
       * Retrieve the var data key for this field.
       *
       * @return var data key
       */
      public Integer getVarDataKey()
      {
         return m_varDataKey;
      }

      /**
       * Retrieve the field location for this field.
       *
       * @return field location
       */
      public FieldLocation getFieldLocation()
      {
         return m_location;
      }

      /**
       * Implements the only method in the Comparable interface to allow
       * FieldItem instances to be sorted.
       *
       * @param item item to compare with
       * @return comparison result
       */
      @Override public int compareTo(FieldItem item)
      {
         int result = m_location.compareTo(item.m_location);
         if (result == 0)
         {
            switch (m_location)
            {
               case FIXED_DATA:
               {
                  result = m_fixedDataBlockIndex - item.m_fixedDataBlockIndex;
                  if (result == 0)
                  {
                     result = m_fixedDataOffset - item.m_fixedDataOffset;
                  }
                  break;
               }

               case VAR_DATA:
               {
                  result = m_varDataKey.intValue() - item.m_varDataKey.intValue();
                  break;
               }

               default:
               {
                  break;
               }
            }
         }
         return result;
      }

      @Override public String toString()
      {
         StringBuilder buffer = new StringBuilder();
         buffer.append("[FieldItem type=");
         buffer.append(m_type.getFieldTypeClass());
         buffer.append('.');
         buffer.append(m_type);
         buffer.append(" location=");
         buffer.append(m_location);

         switch (m_location)
         {
            case FIXED_DATA:
            {
               buffer.append(" fixedDataBlockIndex=");
               buffer.append(m_fixedDataBlockIndex);
               buffer.append(" fixedDataBlockOffset=");
               buffer.append(m_fixedDataOffset);
               break;
            }

            case VAR_DATA:
            {
               buffer.append(" varDataKey=");
               buffer.append(m_varDataKey);
               break;
            }

            case META_DATA:
            {
               buffer.append(" mask=");
               buffer.append(Long.toHexString(m_mask));
               buffer.append(" block=");
               buffer.append(m_metaBlock);

               break;
            }

            default:
            {
               break;
            }
         }

         buffer.append("]");

         return buffer.toString();
      }
      private final FieldType m_type;
      private final FieldLocation m_location;
      private final int m_fixedDataBlockIndex;
      private final int m_fixedDataOffset;
      private final Integer m_varDataKey;
      private final long m_mask;
      private final int m_metaBlock;
   }

   protected final ProjectFile m_file;
   private final ProjectProperties m_properties;
   final CustomFieldContainer m_customFields;
   final VarDataFieldReader m_stringVarDataReader;
   final VarDataFieldReader m_doubleVarDataReader;
   final VarDataFieldReader m_timestampVarDataReader;
   private final Map<FieldType, FieldItem> m_map = new HashMap<>();
   private final int[] m_maxFixedDataSize = new int[MAX_FIXED_DATA_BLOCKS];
   private boolean m_debug;

   private static final Integer[] TASK_KEYS =
   {
      Props.TASK_FIELD_MAP,
      Props.TASK_FIELD_MAP2
   };

   private static final Integer[] ENTERPRISE_CUSTOM_KEYS =
   {
      Props.ENTERPRISE_CUSTOM_FIELD_MAP
   };

   private static final Integer[] RESOURCE_KEYS =
   {
      Props.RESOURCE_FIELD_MAP,
      Props.RESOURCE_FIELD_MAP2
   };

   private static final Integer[] ASSIGNMENT_KEYS =
   {
      Props.ASSIGNMENT_FIELD_MAP,
      Props.ASSIGNMENT_FIELD_MAP2
   };

   private static final Integer[] RELATION_KEYS =
   {
      Props.RELATION_FIELD_MAP
   };

   private static final int MAX_FIXED_DATA_BLOCKS = 2;
}
