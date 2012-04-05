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

package net.sf.mpxj.mpp;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import net.sf.mpxj.AccrueType;
import net.sf.mpxj.ConstraintType;
import net.sf.mpxj.Duration;
import net.sf.mpxj.FieldContainer;
import net.sf.mpxj.FieldType;
import net.sf.mpxj.Priority;
import net.sf.mpxj.ProjectFile;
import net.sf.mpxj.Rate;
import net.sf.mpxj.TaskType;
import net.sf.mpxj.TimeUnit;
import net.sf.mpxj.WorkGroup;
import net.sf.mpxj.utility.NumberUtility;

/**
 * This class is used to represent the mapping present in the MPP file
 * between fields and their locations in various data blocks.
 */
abstract class FieldMap
{
   /**
    * Constructor.
    * 
    * @param file parent project file 
    */
   public FieldMap(ProjectFile file)
   {
      m_file = file;
      m_defaultProjectTimeUnits = m_file.getProjectHeader().getDefaultDurationUnits();
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
         FieldType type = getFieldType(MPPUtility.getInt(data, index + 12));
         int dataBlockOffset = MPPUtility.getShort(data, index + 4);
         int mask = MPPUtility.getInt(data, index + 0);

         int varDataKey;
         if (useTypeAsVarDataKey())
         {
            Integer substitute = substituteVarDataKey(type);
            if (substitute == null)
            {
               varDataKey = (MPPUtility.getInt(data, index + 12) & 0x0000FFFF);
            }
            else
            {
               varDataKey = substitute.intValue();
               mask = 0; // If we've made a substitution, force this to be a var data block
            }
         }
         else
         {
            varDataKey = MPPUtility.getByte(data, index + 6);
         }

         FieldLocation location;

         if (dataBlockOffset != 65535)
         {
            location = FieldLocation.FIXED_DATA;
         }
         else
         {
            if (mask != 0)
            {
               location = FieldLocation.META_DATA;
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
         }

         if (location == FieldLocation.FIXED_DATA)
         {
            if (dataBlockOffset < lastDataBlockOffset)
            {
               ++dataBlockIndex;
            }
            lastDataBlockOffset = dataBlockOffset;

            if (dataBlockOffset > m_maxFixedDataOffset[dataBlockIndex])
            {
               m_maxFixedDataOffset[dataBlockIndex] = dataBlockOffset;
            }

            //System.out.println(MPPUtility.hexdump(data, index, 28, false) + " " + MPPUtility.getShort(data, index + 12) + " " + type + " " + (type == null ? "unknown" : type.getDataType()) + " " + location + " " + dataBlockIndex + " " + dataBlockOffset + " " + varDataKey);
         }

         //         if (location != FieldLocation.META_DATA)
         //         {
         //            System.out.println((type == null ? "?" : type.getClass().getSimpleName() + "." + type) + " " + dataBlockOffset + " " + varDataKey + " " + (MPPUtility.getInt(data, index + 12) & 0x0000FFFF) + " " + Integer.toHexString((MPPUtility.getInt(data, index + 12))));
         //         }

         if (type != null)
         {
            //                        if (location != FieldLocation.META_DATA)
            //                        {               
            //                           System.out.println("new FieldItem("+type.getClass().getSimpleName()+"."+type + ", FieldLocation." + location +", " +dataBlockIndex+", "+dataBlockOffset + ", " + varDataKey+"),");
            //                        }

            //System.out.println(MPPUtility.hexdump(data, index, 28, false) + " " + (type instanceof net.sf.mpxj.TaskField ? "TaskField" : type instanceof net.sf.mpxj.ResourceField ? "ResourceField" : "AssignmentField") + " " + type);

            m_map.put(type, new FieldItem(type, location, dataBlockIndex, dataBlockOffset, varDataKey));
         }

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
    * Given a field ID, derive the field type.
    * 
    * @param fieldID field ID
    * @return field type
    */
   protected abstract FieldType getFieldType(int fieldID);

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
    * Create a field map for enterprise custom fields.
    * 
    * @param props props data
    * @param c target class
    */
   public void createEnterpriseCustomFieldMap(Props props, Class<?> c)
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
            int typeValue = MPPUtility.getInt(fieldMapData, index);
            FieldType type = getFieldType(typeValue);
            if (type != null && type.getClass() == c && type.toString().startsWith("Enterprise Custom Field"))
            {
               int varDataKey = (typeValue & 0xFFFF);
               m_map.put(type, new FieldItem(type, FieldLocation.VAR_DATA, 0, 0, varDataKey));
               //System.out.println(type.getClass().getSimpleName() + "." + type + " " + Integer.toHexString(typeValue));
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
    * @param container field container
    * @param id entity ID
    * @param fixedData fixed data block
    * @param varData var data block
    */
   public void populateContainer(FieldContainer container, Integer id, byte[][] fixedData, Var2Data varData)
   {
      //System.out.println(container.getClass().getSimpleName()+": " + id);
      for (FieldItem item : m_map.values())
      {
         //System.out.println(item.m_type);
         Object value = item.read(id, fixedData, varData);
         //System.out.println(item.m_type.getClass().getSimpleName() + "." + item.m_type +  ": " + value);
         container.set(item.getType(), value);
      }
   }

   /**
    * Retrieve the maximum offset in the fixed data block.
    * 
    * @param blockIndex required block index
    * @return maximum offset
    */
   public int getMaxFixedDataOffset(int blockIndex)
   {
      return m_maxFixedDataOffset[blockIndex];
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
    * Retrieve the parent project file.
    * 
    * @return project file
    */
   protected ProjectFile getProjectFile()
   {
      return m_file;
   }

   /**
    * Clear the field map.
    */
   public void clear()
   {
      m_map.clear();
      Arrays.fill(m_maxFixedDataOffset, 0);
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
            System.out.println(toString());
            clear();
         }
      }
   }

   /**
    * {@inheritDoc}
    */
   @Override public String toString()
   {
      StringWriter sw = new StringWriter();
      PrintWriter pw = new PrintWriter(sw);

      ArrayList<FieldItem> items = new ArrayList<FieldItem>(m_map.values());
      Collections.sort(items);

      pw.println("[FieldMap");

      for (int loop = 0; loop < m_maxFixedDataOffset.length; loop++)
      {
         pw.print(" MaxFixedOffset (block ");
         pw.print(loop);
         pw.print(")=");
         pw.println(m_maxFixedDataOffset[loop]);
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
       */
      FieldItem(FieldType type, FieldLocation location, int fixedDataBlockIndex, int fixedDataOffset, int varDataKey)
      {
         m_type = type;
         m_location = location;
         m_fixedDataBlockIndex = fixedDataBlockIndex;
         m_fixedDataOffset = fixedDataOffset;
         m_varDataKey = Integer.valueOf(varDataKey);
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
               result = readVarData(id, fixedData, varData);
               break;
            }

            case META_DATA:
            {
               // We know that the Boolean flags are stored in the
               // "meta data" block, and can see that the first
               // four bytes of each row read from the field map
               // data in the MPP file represtn a bit mask... but
               // we just haven't worked out how to convert this into
               // the actual location in the data. For now we rely on
               // the ocation in the file being fixed. This is why
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
                     result = Integer.valueOf(MPPUtility.getInt(data, m_fixedDataOffset));
                     break;
                  }

                  case DURATION:
                  {
                     FieldType unitsType = m_type.getUnitsType();
                     TimeUnit units = (TimeUnit) getFieldData(id, unitsType, fixedData, varData);
                     if (units == null)
                     {
                        units = getProjectFile().getProjectHeader().getDefaultDurationUnits();
                     }

                     result = MPPUtility.getAdjustedDuration(getProjectFile(), MPPUtility.getInt(data, m_fixedDataOffset), units);
                     break;
                  }

                  case TIME_UNITS:
                  {
                     result = MPPUtility.getDurationTimeUnits(MPPUtility.getShort(data, m_fixedDataOffset), m_defaultProjectTimeUnits);
                     break;
                  }

                  case CONSTRAINT:
                  {
                     result = ConstraintType.getInstance(MPPUtility.getShort(data, m_fixedDataOffset));
                     break;
                  }

                  case PRIORITY:
                  {
                     result = Priority.getInstance(MPPUtility.getShort(data, m_fixedDataOffset));
                     break;
                  }

                  case PERCENTAGE:
                  {
                     result = MPPUtility.getPercentage(data, m_fixedDataOffset);
                     break;
                  }

                  case TASK_TYPE:
                  {
                     result = TaskType.getInstance(MPPUtility.getShort(data, m_fixedDataOffset));
                     break;
                  }

                  case ACCRUE:
                  {
                     result = AccrueType.getInstance(MPPUtility.getShort(data, m_fixedDataOffset));
                     break;
                  }

                  case CURRENCY:
                  {
                     result = NumberUtility.getDouble(MPPUtility.getDouble(data, m_fixedDataOffset) / 100);
                     break;
                  }

                  case UNITS:
                  {
                     result = NumberUtility.getDouble(MPPUtility.getDouble(data, m_fixedDataOffset) / 100);
                     break;
                  }

                  case RATE:
                  {
                     result = new Rate(MPPUtility.getDouble(data, m_fixedDataOffset), TimeUnit.HOURS);
                     break;
                  }

                  case WORK:
                  {
                     result = Duration.getInstance(MPPUtility.getDouble(data, m_fixedDataOffset) / 60000, TimeUnit.HOURS);
                     break;
                  }

                  case SHORT:
                  {
                     result = Integer.valueOf(MPPUtility.getShort(data, m_fixedDataOffset));
                     break;
                  }

                  case BOOLEAN:
                  {
                     result = Boolean.valueOf(MPPUtility.getShort(data, m_fixedDataOffset) != 0);
                     break;
                  }

                  case DELAY:
                  {
                     result = MPPUtility.getDuration(MPPUtility.getShort(data, m_fixedDataOffset), TimeUnit.HOURS);
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
                     result = WorkGroup.getInstance(MPPUtility.getShort(data, m_fixedDataOffset));
                     break;
                  }

                  case RATE_UNITS:
                  {
                     result = TimeUnit.getInstance(MPPUtility.getShort(data, m_fixedDataOffset) - 1);
                     break;
                  }

                  case GUID:
                  {
                     result = MPPUtility.getGUID(data, m_fixedDataOffset);
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
       * @param id parent entity ID
       * @param fixedData fixed data block
       * @param varData var data block
       * @return field value
       */
      private Object readVarData(Integer id, byte[][] fixedData, Var2Data varData)
      {
         Object result = null;

         switch (m_type.getDataType())
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
               result = MPPUtility.getDurationTimeUnits(varData.getShort(id, m_varDataKey), m_defaultProjectTimeUnits);
               break;
            }

            case CURRENCY:
            {
               result = NumberUtility.getDouble(varData.getDouble(id, m_varDataKey) / 100);
               break;
            }

            case STRING:
            {
               result = getCustomFieldUnicodeStringValue(varData, id, m_varDataKey);
               break;
            }

            case DATE:
            {
               result = getCustomFieldTimestampValue(varData, id, m_varDataKey);
               break;
            }

            case NUMERIC:
            {
               result = getCustomFieldDoubleValue(varData, id, m_varDataKey);
               break;
            }

            case INTEGER:
            {
               result = Integer.valueOf(varData.getInt(id, m_varDataKey));
               break;
            }

            case WORK:
            {
               result = Duration.getInstance(varData.getDouble(id, m_varDataKey) / 60000, TimeUnit.HOURS);
               break;
            }

            case ASCII_STRING:
            {
               result = varData.getString(id, m_varDataKey);
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

            case ACCRUE:
            {
               result = AccrueType.getInstance(varData.getShort(id, m_varDataKey));
               break;
            }

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

            case BINARY:
            {
               // Do nothing for binary data
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
       * @return item value
       */
      private Object getCustomFieldTimestampValue(Var2Data varData, Integer id, Integer type)
      {
         Object result = null;

         int mask = varData.getShort(id, type);
         if ((mask & 0xFF00) != VALUE_LIST_MASK)
         {
            byte[] data = varData.getByteArray(id, type);

            if (data != null)
            {
               if (data.length == 512)
               {
                  result = MPPUtility.getUnicodeString(data);
               }
               else
               {
                  if (data.length >= 4)
                  {
                     result = MPPUtility.getTimestamp(data);
                  }
               }
            }
         }
         else
         {
            int uniqueId = varData.getInt(id, 2, type);
            CustomFieldValueItem item = getProjectFile().getCustomFieldValueItem(Integer.valueOf(uniqueId));
            if (item != null && item.getValue() != null)
            {
               result = MPPUtility.getTimestamp(item.getValue());
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
               result = MPPUtility.getUnicodeString(data);
            }
            else
            {
               if (data.length >= 4)
               {
                  int duration = MPPUtility.getInt(data);
                  result = MPPUtility.getAdjustedDuration(getProjectFile(), duration, units);
               }
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
       * @return item value
       */
      private Double getCustomFieldDoubleValue(Var2Data varData, Integer id, Integer type)
      {
         double result = 0;

         int mask = varData.getShort(id, type);
         if ((mask & 0xFF00) != VALUE_LIST_MASK)
         {
            result = varData.getDouble(id, type);
         }
         else
         {
            int uniqueId = varData.getInt(id, 2, type);
            CustomFieldValueItem item = getProjectFile().getCustomFieldValueItem(Integer.valueOf(uniqueId));
            if (item != null && item.getValue() != null)
            {
               result = MPPUtility.getDouble(item.getValue());
            }
         }
         return NumberUtility.getDouble(result);
      }

      /**
       * Retrieve custom field value.
       * 
       * @param varData var data block
       * @param id item ID
       * @param type item type
       * @return item value
       */
      private String getCustomFieldUnicodeStringValue(Var2Data varData, Integer id, Integer type)
      {
         String result = null;

         int mask = varData.getShort(id, type);
         if ((mask & 0xFF00) != VALUE_LIST_MASK)
         {
            result = varData.getUnicodeString(id, type);
         }
         else
         {
            int uniqueId = varData.getInt(id, 2, type);
            CustomFieldValueItem item = getProjectFile().getCustomFieldValueItem(Integer.valueOf(uniqueId));
            if (item != null && item.getValue() != null)
            {
               result = MPPUtility.getUnicodeString(item.getValue());
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

      /**
       * {@inheritDoc}
       */
      @Override public String toString()
      {
         StringBuffer buffer = new StringBuffer();
         buffer.append("[FieldItem type=");
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

            default:
            {
               break;
            }
         }

         buffer.append("]");

         return buffer.toString();
      }

      private FieldType m_type;
      private FieldLocation m_location;
      private int m_fixedDataBlockIndex;
      private int m_fixedDataOffset;
      private Integer m_varDataKey;
   }

   private ProjectFile m_file;
   protected TimeUnit m_defaultProjectTimeUnits;
   private Map<FieldType, FieldItem> m_map = new HashMap<FieldType, FieldItem>();
   private int[] m_maxFixedDataOffset = new int[MAX_FIXED_DATA_BLOCKS];

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

   private static final int VALUE_LIST_MASK = 0x0700;

   private static final int MAX_FIXED_DATA_BLOCKS = 2;
}
