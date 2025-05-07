/*
 * file:       CustomFieldValueReader.java
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

import java.util.Map;
import java.util.UUID;

import org.mpxj.CustomFieldContainer;
import org.mpxj.CustomFieldLookupTable;
import org.mpxj.CustomFieldValueDataType;
import org.mpxj.FieldType;
import org.mpxj.ProjectFile;
import org.mpxj.ProjectProperties;
import org.mpxj.TimeUnit;
import org.mpxj.common.ByteArrayHelper;

/**
 * Common implementation detail shared by custom field value readers.
 */
abstract class CustomFieldValueReader
{
   /**
    * Constructor.
    *
    * @param file project file
    * @param lookupTableMap map of GUIDs to lookup tables
    * @param outlineCodeVarMeta raw mpp data
    * @param outlineCodeVarData raw mpp data
    * @param outlineCodeFixedData raw mpp data
    * @param outlineCodeFixedData2 raw mpp data
    */
   public CustomFieldValueReader(ProjectFile file, Map<UUID, FieldType> lookupTableMap, VarMeta outlineCodeVarMeta, Var2Data outlineCodeVarData, FixedData outlineCodeFixedData, FixedData outlineCodeFixedData2)
   {
      m_lookupTableMap = lookupTableMap;
      m_properties = file.getProjectProperties();
      m_container = file.getCustomFields();
      m_outlineCodeVarMeta = outlineCodeVarMeta;
      m_outlineCodeVarData = outlineCodeVarData;
      m_outlineCodeFixedData = outlineCodeFixedData;
      m_outlineCodeFixedData2 = outlineCodeFixedData2;
   }

   /**
    * Read custom field lookup values, register them by their unique ID and GUID, and add
    * them to their parent lookup table.
    */
   public void process()
   {
      for (int loop = 0; loop < m_outlineCodeFixedData.getItemCount(); loop++)
      {
         byte[] fixedData = m_outlineCodeFixedData.getByteArrayValue(loop);
         if (fixedData == null)
         {
            continue;
         }

         Integer id = Integer.valueOf(ByteArrayHelper.getShort(fixedData, UNJQUE_ID_OFFSET));
         if (!m_outlineCodeVarMeta.containsKey(id))
         {
            continue;
         }

         byte[] value = m_outlineCodeVarData.getByteArray(id, VALUE_LIST_VALUE);
         if (value == null)
         {
            continue;
         }

         CustomFieldValueItem item = new CustomFieldValueItem(id);
         item.setDescription(m_outlineCodeVarData.getUnicodeString(id, VALUE_LIST_DESCRIPTION));
         item.setUnknown(m_outlineCodeVarData.getByteArray(id, VALUE_LIST_UNKNOWN));
         item.setParentUniqueID(Integer.valueOf(ByteArrayHelper.getShort(fixedData, m_parentOffset)));

         byte[] fixedData2 = m_outlineCodeFixedData2.getByteArrayValue(loop);
         item.setGUID(MPPUtility.getGUID(fixedData2, 0));
         UUID lookupTableGuid = MPPUtility.getGUID(fixedData2, m_fieldOffset);
         item.setType(CustomFieldValueDataType.getInstance(ByteArrayHelper.getShort(fixedData2, m_typeOffset)));
         item.setValue(getTypedValue(item.getType(), value));

         m_container.registerValue(item);
         FieldType field = m_lookupTableMap.get(lookupTableGuid);
         if (field != null)
         {
            CustomFieldLookupTable table = m_container.getOrCreate(field).getLookupTable();

            // Check that this isn't a duplicate entry. We're assuming that unique ID is enough to spot duplicates
            // TODO: consider indexing the lookup tables to make this more efficient?
            if (table.stream().noneMatch(i -> i.getUniqueID().equals(item.getUniqueID())))
            {
               // It's the first time we've seen this value so add it, and set the table's GUID
               table.add(item);
               // It's like this to avoid creating empty lookup tables. Need to refactor!
               table.setGUID(lookupTableGuid);
            }
            else
            {
               // Replace the original instance in the table.
               // This ensures that the same instance is in the container and the lookup table.
               // TODO: is there a better way to do this? Should we generate the lookup table contents dynamically from the container?
               for (int index = 0; index < table.size(); index++)
               {
                  if (table.get(index).getUniqueID().equals(item.getUniqueID()))
                  {
                     table.set(index, item);
                     break;
                  }
               }
            }
         }
      }
   }

   /**
    * Convert raw value as read from the MPP file into a Java type.
    *
    * @param type MPP value type
    * @param value raw value data
    * @return Java object
    */
   protected Object getTypedValue(CustomFieldValueDataType type, byte[] value)
   {
      Object result;

      if (type == null)
      {
         result = valueAsString(value);
      }
      else
      {
         switch (type)
         {
            case DATE:
            case FINISH_DATE:
            {
               result = MPPUtility.getTimestamp(value, 0);
               break;
            }

            case DURATION:
            {
               TimeUnit units = MPPUtility.getDurationTimeUnits(ByteArrayHelper.getShort(value, 4), m_properties.getDefaultDurationUnits());
               result = MPPUtility.getAdjustedDuration(m_properties, ByteArrayHelper.getInt(value, 0), units);
               break;
            }

            case COST:
            {
               result = Double.valueOf(MPPUtility.getDouble(value, 0) / 100);
               break;
            }

            case NUMBER:
            {
               result = Double.valueOf(MPPUtility.getDouble(value, 0));
               break;
            }

            case TEXT:
            {
               result = MPPUtility.getUnicodeString(value, 0);
               break;
            }

            default:
            {
               result = valueAsString(value);
               break;
            }
         }
      }

      return result;
   }

   /**
    * Try to convert a byte array into a string. In the event of a
    * failure, fall back to dumping the byte array contents
    * as string of hex bytes.
    *
    * @param value byte array
    * @return String instance
    */
   private String valueAsString(byte[] value)
   {
      String result;

      //
      // We don't know what this is, let's try making a string
      //
      try
      {
         result = MPPUtility.getUnicodeString(value, 0);
      }

      catch (Exception ex)
      {
         //
         // Handle failure gracefully and dump the byte array contents
         //
         result = ByteArrayHelper.hexdump(value, false);
      }
      return result;
   }

   protected final Map<UUID, FieldType> m_lookupTableMap;
   protected final ProjectProperties m_properties;
   protected final CustomFieldContainer m_container;
   protected final VarMeta m_outlineCodeVarMeta;
   protected final Var2Data m_outlineCodeVarData;
   protected final FixedData m_outlineCodeFixedData;
   protected final FixedData m_outlineCodeFixedData2;

   protected int m_parentOffset;
   protected int m_typeOffset;
   protected int m_fieldOffset;

   public static final int UNJQUE_ID_OFFSET = 4;
   public static final Integer VALUE_LIST_VALUE = Integer.valueOf(22);
   public static final Integer VALUE_LIST_DESCRIPTION = Integer.valueOf(8);
   public static final Integer VALUE_LIST_UNKNOWN = Integer.valueOf(23);
}
