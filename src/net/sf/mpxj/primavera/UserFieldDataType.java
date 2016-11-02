/*
 * file:       UserFieldCounters.java
 * author:     Mario Fuentes
 * copyright:  (c) Packwood Software 2013
 * date:       22/03/2010
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

package net.sf.mpxj.primavera;

import java.util.HashMap;
import java.util.Map;

import net.sf.mpxj.DataType;
import net.sf.mpxj.FieldType;
import net.sf.mpxj.FieldTypeClass;

/**
 * User defined field data types.
 */
public enum UserFieldDataType
{
   FT_TEXT("TEXT"),
   FT_START_DATE("START"),
   FT_END_DATE("FINISH"),
   FT_FLOAT_2_DECIMALS("NUMBER"),
   FT_INT("NUMBER"),
   FT_STATICTYPE("TEXT"),
   FT_MONEY("COST");

   /**
    * Constructor.
    *
    * @param fieldName default field name used to
    * store user defined data of this type.
    */
   private UserFieldDataType(String fieldName)
   {
      this.m_defaultFieldName = fieldName;
   }

   /**
    * Retrieve the default field name.
    *
    * @return default field name
    */
   public String getDefaultFieldName()
   {
      return m_defaultFieldName;
   }

   /**
    * Infers the Primavera user defined field data type from the MPXJ data type.
    *
    * @author kmahan
    * @date 2014-09-24
    * @param dataType MPXJ data type
    * @return string representation of data type
    */
   public static String inferUserFieldDataType(DataType dataType)
   {
      switch (dataType)
      {
         case BINARY:
         case STRING:
         case DURATION:
            return "Text";
         case DATE:
            return "Start Date";
         case NUMERIC:
            return "Double";
         case BOOLEAN:
         case INTEGER:
         case SHORT:
            return "Integer";
         case CURRENCY:
            return "Cost";
         default:
            throw new RuntimeException("Unconvertible data type: " + dataType);
      }
   }

   /**
    * Infers the Primavera entity type based on the MPXJ field type.
    *
    * @author lsong
    * @date 2015-7-24
    * @param fieldType MPXJ field type
    * @return UDF subject area
    */
   public static String inferUserFieldSubjectArea(FieldType fieldType)
   {
      String result = SUBJECT_AREA_MAP.get(fieldType.getFieldTypeClass());
      if (result == null)
      {
         throw new RuntimeException("Unrecognized field type: " + fieldType);
      }
      return result;
   }

   private String m_defaultFieldName;

   private static final Map<FieldTypeClass, String> SUBJECT_AREA_MAP = new HashMap<FieldTypeClass, String>();
   static
   {
      SUBJECT_AREA_MAP.put(FieldTypeClass.TASK, "Activity");
      SUBJECT_AREA_MAP.put(FieldTypeClass.RESOURCE, "Resource");
      SUBJECT_AREA_MAP.put(FieldTypeClass.PROJECT, "Project");
      SUBJECT_AREA_MAP.put(FieldTypeClass.ASSIGNMENT, "Assignment");
      SUBJECT_AREA_MAP.put(FieldTypeClass.CONSTRAINT, "Constraint");
   }
}
