package net.sf.mpxj.common;
import net.sf.mpxj.DataType;
import net.sf.mpxj.FieldType;
import net.sf.mpxj.FieldTypeClass;
import net.sf.mpxj.ProjectFile;
import net.sf.mpxj.UserDefinedField;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class UserDefinedFieldMap
{
   public UserDefinedFieldMap(ProjectFile file)
   {
      Set<FieldType> populated = new HashSet<>();
      populated.addAll(file.getTasks().getPopulatedFields());
      populated.addAll(file.getResources().getPopulatedFields());
      populated.addAll(file.getResourceAssignments().getPopulatedFields());

      for(List<? extends FieldType> fieldList : Arrays.asList(TaskFieldLists.EXTENDED_FIELDS, ResourceFieldLists.EXTENDED_FIELDS, AssignmentFieldLists.EXTENDED_FIELDS))
      {
         fieldList.stream().filter(f -> !populated.contains(f)).forEach(f -> getFieldList(f).add(f));
      }
   }

   public FieldType getMapping(UserDefinedField field)
   {
      return m_map.computeIfAbsent(field, this::generateMapping);
   }

   private FieldType generateMapping(UserDefinedField field)
   {
      List<FieldType> fieldList = getFieldList(field);
      if (fieldList.isEmpty())
      {
         // Fall back to string if the desired type is not available
         fieldList = getFieldList(field.getFieldTypeClass(), DataType.STRING);
      }

      return fieldList.isEmpty() ? null : fieldList.remove(0);
   }

   private List<FieldType> getFieldList(FieldType field)
   {
      return getFieldList(field.getFieldTypeClass(), field.getDataType());
   }

   private List<FieldType> getFieldList(FieldTypeClass fieldTypeClass, DataType dataType)
   {
      Map<DataType, List<FieldType>> typeMap = m_fields.computeIfAbsent(fieldTypeClass, c -> new HashMap<>());
      return typeMap.computeIfAbsent(dataType, f -> new ArrayList<>());
   }

   private DataType normaliseDataType(DataType type)
   {
      switch(type)
      {
         case STRING:
         case DATE:
         case CURRENCY:
         case BOOLEAN:
         case NUMERIC:
         case DURATION:
         {
            break;
         }

         case PERCENTAGE:
         case INTEGER:
         case SHORT:
         {
            type = DataType.NUMERIC;
            break;
         }

         case WORK:
         {
            type = DataType.DURATION;
            break;
         }

         case BINARY:
         {
            type = null;
            break;
         }

         case DELAY:
         {
            type = DataType.DURATION;
            break;
         }

         default:
         {
            type = DataType.STRING;
            break;
         }
      }

      return type;
   }

   private final Map<UserDefinedField, FieldType> m_map = new HashMap<>();
   private final Map<FieldTypeClass, Map<DataType, List<FieldType>>> m_fields = new HashMap<>();
}
