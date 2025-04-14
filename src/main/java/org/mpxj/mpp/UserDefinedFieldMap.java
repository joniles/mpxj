/*
 * file:       UserDefinedFieldMap.java
 * author:     Jon Iles
 * copyright:  (c) Timephased Limited 2023
 * date:       2023-02-05
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

import org.mpxj.DataType;
import org.mpxj.FieldType;
import org.mpxj.FieldTypeClass;
import org.mpxj.ProjectFile;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * This class is used to generate a mapping between a set of user defined fields
 * and a set of custom fields. This allows schedules exported from applications
 * which make use of user defined fields to be imported into Microsoft Project
 * with the values originally in user defined fields preserved and made available in
 * Microsoft Project in custom fields.
 */
public class UserDefinedFieldMap
{
   /**
    * Constructor.
    *
    * @param file parent project
    * @param targetFieldList list of custom fields we could potentially map user defined fields to
    */
   public UserDefinedFieldMap(ProjectFile file, List<FieldType> targetFieldList)
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
      targetFieldList.stream().filter(f -> !populated.contains(f)).forEach(f -> getFieldList(f).add(f));
   }

   /**
    * Given a source field, return the target field it should be mapped to.
    * If no mapping is in place this method will return the source field
    * supplied by the caller.
    *
    * @param source source field
    * @return target field
    */
   public FieldType getTarget(FieldType source)
   {
      FieldType target = m_targetMap.get(source);
      return target == null ? source : target;
   }

   /**
    * Given a target field, determine which field is being used as its source.
    * If no mapping is in place this method will return the target field
    * supplied by the caller.
    *
    * @param target target field
    * @return source field
    */
   public FieldType getSource(FieldType target)
   {
      FieldType source = m_sourceMap.get(target);
      return source == null ? target : source;
   }

   /**
    * Generate a mapping for a source field.
    * If we have run out of target fields this method
    * will return null.
    *
    * @param source source field
    * @return target field or null
    */
   public FieldType generateMapping(FieldType source)
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

      return target;
   }

   private List<FieldType> getFieldList(FieldType field)
   {
      return getFieldList(field.getFieldTypeClass(), normaliseDataType(field.getDataType()));
   }

   private List<FieldType> getFieldList(FieldTypeClass fieldTypeClass, DataType dataType)
   {
      Map<DataType, List<FieldType>> typeMap = m_fields.computeIfAbsent(fieldTypeClass, c -> new HashMap<>());
      return typeMap.computeIfAbsent(dataType, f -> new ArrayList<>());
   }

   private DataType normaliseDataType(DataType type)
   {
      switch (type)
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
}
