package net.sf.mpxj.mpp;
import net.sf.mpxj.DataType;
import net.sf.mpxj.FieldType;
import net.sf.mpxj.FieldTypeClass;
import net.sf.mpxj.ProjectFile;
import net.sf.mpxj.common.AssignmentFieldLists;
import net.sf.mpxj.common.ResourceFieldLists;
import net.sf.mpxj.common.TaskFieldLists;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class UserDefinedFieldMap
{
   public static UserDefinedFieldMap getEmptyInstance()
   {
      return EMPTY_INSTANCE;
   }

   public static UserDefinedFieldMap getInstanceWithoutMappings(ProjectFile file)
   {
      return new UserDefinedFieldMap(file, false);
   }

   public static UserDefinedFieldMap getInstanceWithMappings(ProjectFile file)
   {
      return new UserDefinedFieldMap(file, true);
   }

   private UserDefinedFieldMap(ProjectFile file, boolean generateMappingNow)
   {
      // No action required if we have no user defined fields
      if (file == null || file.getUserDefinedFields().isEmpty())
      {
         return;
      }

      // Determine which fields are in use
      Set<FieldType> populated = new HashSet<>();
      populated.addAll(file.getTasks().getPopulatedFields());
      populated.addAll(file.getResources().getPopulatedFields());
      populated.addAll(file.getResourceAssignments().getPopulatedFields());

      // Build a collection of potential target fields
      for(List<? extends FieldType> fieldList : Arrays.asList(TaskFieldLists.EXTENDED_FIELDS, ResourceFieldLists.EXTENDED_FIELDS, AssignmentFieldLists.EXTENDED_FIELDS))
      {
         fieldList.stream().filter(f -> !populated.contains(f)).forEach(f -> getFieldList(f).add(f));
      }

      if (generateMappingNow)
      {
         // Generate a mapping for each user defined field
         file.getUserDefinedFields().forEach(this::createMapping);
      }
   }

   public FieldType createMapping(FieldType source)
   {
      return generateMapping(source);
   }

   public FieldType getTarget(FieldType source)
   {
      FieldType target = m_targetMap.get(source);
      return target == null ? source : target;
   }

   public FieldType getSource(FieldType target)
   {
      FieldType source = m_sourceMap.get(target);
      return source == null ? target : source;
   }

   private FieldType generateMapping(FieldType source)
   {
      List<FieldType> fieldList = getFieldList(source);
      if (fieldList.isEmpty())
      {
         // Fall back to string if the desired type is not available
         fieldList = getFieldList(source.getFieldTypeClass(), DataType.STRING);
      }

      FieldType target = fieldList.isEmpty() ? null : fieldList.remove(0);
      if (target != null)
      {
         m_targetMap.put(source, target);
         m_sourceMap.put(target, source);
      }
      else
      {
         System.out.println("here");
      }

      return target;
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

   private final Map<FieldType, FieldType> m_targetMap = new HashMap<>();
   private final Map<FieldType, FieldType> m_sourceMap = new HashMap<>();
   private final Map<FieldTypeClass, Map<DataType, List<FieldType>>> m_fields = new HashMap<>();

   private static final UserDefinedFieldMap EMPTY_INSTANCE = new UserDefinedFieldMap(null, false);
}
