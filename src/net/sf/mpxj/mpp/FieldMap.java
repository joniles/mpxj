/*
 * file:       FieldMap.java
 * author:     Jon Iles
 * copyright:  (c) Packwood Software 2011
 * date:       13/04/2010
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

import net.sf.mpxj.AccrueType;
import net.sf.mpxj.ConstraintType;
import net.sf.mpxj.Duration;
import net.sf.mpxj.FieldContainer;
import net.sf.mpxj.FieldType;
import net.sf.mpxj.MPPResourceField;
import net.sf.mpxj.MPPTaskField;
import net.sf.mpxj.Priority;
import net.sf.mpxj.ProjectFile;
import net.sf.mpxj.Rate;
import net.sf.mpxj.TaskType;
import net.sf.mpxj.TimeUnit;
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
   }

   /**
    * Generic method used to create a field map from a block of data. 
    * 
    * @param data field map data
    */
   private void createFieldMap(byte[] data)
   {
      int index = 0;
      while (index < data.length)
      {
         FieldType type = getFieldType(MPPUtility.getInt(data, index + 12));
         int dataBlockOffset = MPPUtility.getShort(data, index + 4);
         int varDataKey = MPPUtility.getByte(data, index + 6);
         int mask = MPPUtility.getInt(data, index + 0);

         FieldLocation location;

         if (dataBlockOffset != 65535)
         {
            location = FieldLocation.FIXED_DATA;
            if (dataBlockOffset > m_maxFixedDataOffset)
            {
               m_maxFixedDataOffset = dataBlockOffset;
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
               if (mask != 0)
               {
                  location = FieldLocation.META_DATA;
               }
               else
               {
                  location = FieldLocation.UNKNOWN;
               }
            }
         }

         //System.out.println(MPPUtility.hexdump(data, index, 28, false) + " " + type + " " + (type == null ? "unknown" : type.getDataType()) + " " + location + " " + dataBlockOffset + " " + varDataKey);

         if (type != null)
         {
            //            if (location != FieldLocation.META_DATA)
            //            {
            //               System.out.println("{ResourceField."+type+", FieldLocation."+location+", Integer.valueOf("+dataBlockOffset+"), Integer.valueOf("+varDataKey+")},");
            //            }
            m_map.put(type, new FieldItem(type, location, dataBlockOffset, varDataKey));
         }

         index += 28;
      }
   }

   /**
    * Abstract method used by child classes to supply default data.
    * 
    * @return default data
    */
   protected abstract Object[][] getDefaultTaskData();

   /**
    * Abstract method used by child classes to supply default data.
    * 
    * @return default data
    */
   protected abstract Object[][] getDefaultResourceData();

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
    * This method takes an array of data and uses this to populate the
    * field map.
    * 
    * @param defaultData field map default data
    */
   private void populateDefaultData(Object[][] defaultData)
   {
      for (Object[] item : defaultData)
      {
         m_map.put((FieldType) item[0], new FieldItem((FieldType) item[0], (FieldLocation) item[1], ((Integer) item[2]).intValue(), ((Integer) item[3]).intValue()));
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
   public void populateContainer(FieldContainer container, Integer id, byte[] fixedData, Var2Data varData)
   {
      //System.out.println("Object: " + id);
      for (FieldItem item : m_map.values())
      {
         Object value = item.read(id, fixedData, varData);
         //System.out.println(item.m_type + ": " + value);
         container.set(item.getType(), value);
      }
   }

   /**
    * Retrieve the maximum offset in the fixed data block.
    * 
    * @return maximum offset
    */
   public int getMaxFixedDataOffset()
   {
      return m_maxFixedDataOffset;
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
    * Given a field ID, derive the field type.
    * 
    * @param fieldID field ID
    * @return field type
    */
   private FieldType getFieldType(int fieldID)
   {
      FieldType result;
      int prefix = fieldID & 0xFFFF0000;
      int index = fieldID & 0x0000FFFF;

      if (prefix == MPPTaskField.TASK_FIELD_BASE)
      {
         result = MPPTaskField.getInstance(index);
      }
      else
      {
         result = MPPResourceField.getInstance(index);
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
   protected Object getFieldData(Integer id, FieldType type, byte[] fixedData, Var2Data varData)
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
    * descrube the location of each field within the MPP file. It also provides
    * the methods used to extract an individual field value.
    */
   public class FieldItem
   {
      /**
       * Constructor.
       * 
       * @param type field type
       * @param location identifies which block the field is present in
       * @param fixedDataOffset fixed data block offset
       * @param varDataKey var data block key
       */
      FieldItem(FieldType type, FieldLocation location, int fixedDataOffset, int varDataKey)
      {
         m_type = type;
         m_location = location;
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
      public Object read(Integer id, byte[] fixedData, Var2Data varData)
      {
         Object result = null;

         switch (m_location)
         {
            case FIXED_DATA :
            {
               result = readFixedData(id, fixedData, varData);
               break;
            }

            case VAR_DATA :
            {
               result = readVarData(id, fixedData, varData);
               break;
            }

            case META_DATA :
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

            default :
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
      private Object readFixedData(Integer id, byte[] fixedData, Var2Data varData)
      {
         Object result = null;

         switch (m_type.getDataType())
         {
            case DATE :
            {
               result = MPPUtility.getTimestamp(fixedData, m_fixedDataOffset);
               break;
            }

            case INTEGER :
            {
               result = Integer.valueOf(MPPUtility.getInt(fixedData, m_fixedDataOffset));
               break;
            }

            case DURATION :
            {
               FieldType unitsType = m_type.getUnitsType();
               TimeUnit units = (TimeUnit) getFieldData(id, unitsType, fixedData, varData);
               if (units == null)
               {
                  units = TimeUnit.HOURS;
               }

               result = MPPUtility.getAdjustedDuration(getProjectFile(), MPPUtility.getInt(fixedData, m_fixedDataOffset), units);
               break;
            }

            case TIME_UNITS :
            {
               result = MPPUtility.getDurationTimeUnits(MPPUtility.getShort(fixedData, m_fixedDataOffset));
               break;
            }

            case CONSTRAINT :
            {
               result = ConstraintType.getInstance(MPPUtility.getShort(fixedData, m_fixedDataOffset));
               break;
            }

            case PRIORITY :
            {
               result = Priority.getInstance(MPPUtility.getShort(fixedData, m_fixedDataOffset));
               break;
            }

            case PERCENTAGE :
            {
               result = MPPUtility.getPercentage(fixedData, m_fixedDataOffset);
               break;
            }

            case TASK_TYPE :
            {
               result = TaskType.getInstance(MPPUtility.getShort(fixedData, m_fixedDataOffset));
               break;
            }

            case ACCRUE :
            {
               result = AccrueType.getInstance(MPPUtility.getShort(fixedData, m_fixedDataOffset));
               break;
            }

            case CURRENCY :
            {
               result = NumberUtility.getDouble(MPPUtility.getDouble(fixedData, m_fixedDataOffset) / 100);
               break;
            }

            case UNITS :
            {
               result = NumberUtility.getDouble(MPPUtility.getDouble(fixedData, m_fixedDataOffset) / 100);
               break;
            }

            case RATE :
            {
               result = new Rate(MPPUtility.getDouble(fixedData, m_fixedDataOffset), TimeUnit.HOURS);
               break;
            }

            case STRING :
            {
               // Resource Workgroup not sure of format in fixed data block?
               break;
            }

            case WORK :
            {
               result = Duration.getInstance(MPPUtility.getDouble(fixedData, m_fixedDataOffset) / 60000, TimeUnit.HOURS);
               break;
            }

            case SHORT :
            {
               result = Integer.valueOf(MPPUtility.getShort(fixedData, m_fixedDataOffset));
               break;
            }

            case BOOLEAN :
            {
               result = Boolean.valueOf(MPPUtility.getShort(fixedData, m_fixedDataOffset) != 0);
               break;
            }

            default :
            {
               //System.out.println("**** UNSUPPORTED FIXED DATA TYPE");
               break;
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
      private Object readVarData(Integer id, byte[] fixedData, Var2Data varData)
      {
         Object result = null;

         switch (m_type.getDataType())
         {
            case DURATION :
            {
               FieldType unitsType = m_type.getUnitsType();
               TimeUnit units = (TimeUnit) getFieldData(id, unitsType, fixedData, varData);
               if (units == null)
               {
                  units = TimeUnit.HOURS;
               }
               result = MPPUtility.getAdjustedDuration(getProjectFile(), varData.getInt(id, m_varDataKey), units);
               break;
            }

            case TIME_UNITS :
            {
               result = MPPUtility.getDurationTimeUnits(varData.getShort(id, m_varDataKey));
               break;
            }

            case CURRENCY :
            {
               result = NumberUtility.getDouble(varData.getDouble(id, m_varDataKey) / 100);
               break;
            }

            case STRING :
            {
               result = varData.getUnicodeString(id, m_varDataKey);
               break;
            }

            case DATE :
            {
               result = varData.getTimestamp(id, m_varDataKey);
               break;
            }

            case NUMERIC :
            {
               result = NumberUtility.getDouble(varData.getDouble(id, m_varDataKey));
               break;
            }

            case INTEGER :
            {
               result = Integer.valueOf(varData.getInt(id, m_varDataKey));
               break;
            }

            case WORK :
            {
               result = Duration.getInstance(varData.getDouble(id, m_varDataKey) / 60000, TimeUnit.HOURS);
               break;
            }

            case ASCII_STRING :
            {
               result = varData.getString(id, m_varDataKey);
               break;
            }

            case BINARY :
            {
               // Do nothing for binary data
               break;
            }

            default :
            {
               //System.out.println("**** UNSUPPORTED VAR DATA TYPE");
               break;
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

      private FieldType m_type;
      private FieldLocation m_location;
      private int m_fixedDataOffset;
      private Integer m_varDataKey;
   }

   private ProjectFile m_file;
   private Map<FieldType, FieldItem> m_map = new HashMap<FieldType, FieldItem>();
   private int m_maxFixedDataOffset;
   private static final Integer[] TASK_KEYS =
   {
      Props.TASK_FIELD_MAP,
      Props.TASK_FIELD_MAP2
   };
   private static final Integer[] RESOURCE_KEYS =
   {
      Props.RESOURCE_FIELD_MAP,
      Props.RESOURCE_FIELD_MAP2
   };
}
