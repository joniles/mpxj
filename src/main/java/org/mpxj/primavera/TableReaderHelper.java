/*
 * file:       TableReaderHelper.java
 * author:     Jon Iles
 * date:       2025-11-12
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

package org.mpxj.primavera;

import java.util.List;
import java.util.Map;

import org.mpxj.DataType;
import org.mpxj.FieldContainer;
import org.mpxj.FieldType;
import org.mpxj.FieldTypeClass;
import org.mpxj.ProjectContext;

/**
 * Common methods used when redaing tabular P6 data.
 */
class TableReaderHelper
{
   /**
    * Generic method to extract Primavera fields and assign to MPXJ fields.
    *
    * @param map map of MPXJ field types and Primavera field names
    * @param row Primavera data container
    * @param container MPXJ data contain
    */
   public static void processFields(Map<FieldType, String> map, Row row, FieldContainer container)
   {
      for (Map.Entry<FieldType, String> entry : map.entrySet())
      {
         FieldType field = entry.getKey();
         String name = entry.getValue();

         Object value;
         switch (field.getDataType())
         {
            case INTEGER:
            {
               value = row.getInteger(name);
               break;
            }

            case BOOLEAN:
            {
               value = row.getBooleanObject(name);
               break;
            }

            case DATE:
            {
               value = row.getDate(name);
               break;
            }

            case CURRENCY:
            case NUMERIC:
            case PERCENTAGE:
            {
               value = row.getDouble(name);
               break;
            }

            case DELAY:
            case WORK:
            case DURATION:
            {
               value = row.getDuration(name);
               break;
            }

            case RESOURCE_TYPE:
            {
               value = ResourceTypeHelper.getInstanceFromXer(row.getString(name));
               break;
            }

            case TASK_TYPE:
            {
               value = TaskTypeHelper.getInstanceFromXer(row.getString(name));
               break;
            }

            case CONSTRAINT:
            {
               value = ConstraintTypeHelper.getInstanceFromXer(row.getString(name));
               break;
            }

            case PRIORITY:
            {
               value = PriorityHelper.getInstanceFromXer(row.getString(name));
               break;
            }

            case GUID:
            {
               value = row.getUUID(name);
               break;
            }

            default:
            {
               value = row.getString(name);
               break;
            }
         }

         container.set(field, value);
      }
   }

   /**
    * Populate the UDF values for this entity.
    *
    * @param state shared state data
    * @param tableName parent table name
    * @param type entity type
    * @param container entity
    * @param uniqueID entity Unique ID
    */
   public static void populateUserDefinedFieldValues(TableReaderState state, String tableName, FieldTypeClass type, FieldContainer container, Integer uniqueID)
   {
      Map<Integer, List<Row>> tableData = state.getUdfValues().get(tableName);
      if (tableData != null)
      {
         List<Row> udf = tableData.get(uniqueID);
         if (udf != null)
         {
            for (Row r : udf)
            {
               addUDFValue(state.getContext(), type, container, r);
            }
         }
      }
   }

   /**
    * Adds a user defined field value to a task.
    *
    * @param context project context
    * @param fieldType field type
    * @param container FieldContainer instance
    * @param row UDF data
    */
   private static void addUDFValue(ProjectContext context, FieldTypeClass fieldType, FieldContainer container, Row row)
   {
      Integer fieldId = row.getInteger("udf_type_id");
      FieldType field = context.getUserDefinedFields().getByUniqueID(fieldId);
      if (field == null)
      {
         return;
      }

      Object value;
      DataType fieldDataType = field.getDataType();

      switch (fieldDataType)
      {
         case DATE:
         {
            value = row.getDate("udf_date");
            break;
         }

         case CURRENCY:
         case NUMERIC:
         {
            value = row.getDouble("udf_number");
            break;
         }

         case INTEGER:
         {
            value = row.getInteger("udf_number");
            break;
         }

         default:
         {
            value = row.getString("udf_text");
            break;
         }
      }

      container.set(field, value);
   }
}
