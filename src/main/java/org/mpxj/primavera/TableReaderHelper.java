package org.mpxj.primavera;

import java.util.List;
import java.util.Map;

import org.mpxj.DataType;
import org.mpxj.FieldContainer;
import org.mpxj.FieldType;
import org.mpxj.FieldTypeClass;
import org.mpxj.ProjectContext;
import org.mpxj.Rate;
import org.mpxj.TimeUnit;

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
