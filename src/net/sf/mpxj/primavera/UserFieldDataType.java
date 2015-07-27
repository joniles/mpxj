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

import net.sf.mpxj.AssignmentField;
import net.sf.mpxj.ConstraintField;
import net.sf.mpxj.DataType;
import net.sf.mpxj.FieldType;
import net.sf.mpxj.ProjectField;
import net.sf.mpxj.ResourceField;
import net.sf.mpxj.TaskField;

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
   FT_STATICTYPE("FLAG"),
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
    * @author kmahan 
    * @date 2014-09-24
    * @return string representation of data type
    */
   public static String inferUserFieldDataType(DataType dataType)
   {
      switch (dataType)
      {
         case STRING:
            return "Text";
         case DATE:
            return "Start Date";
         case NUMERIC:
            return "Double";
         case INTEGER:
         case SHORT:
            return "Integer";
         default:
            throw new RuntimeException("Unconvertible data type: " + dataType);
      }
   }

   /**
    * @author lsong
    * @date 2015-7-24
    * @return udf subject area
    */
   public static String inferUserFieldSubjectArea(FieldType fieldType)
   {
      if (fieldType instanceof TaskField)
         return "Activity";
      if (fieldType instanceof ResourceField)
         return "Resource";
      if (fieldType instanceof ProjectField)
         return "Project";
      if (fieldType instanceof AssignmentField)
         return "Assignment";
      if (fieldType instanceof ConstraintField)
         return "Constraint";
      throw new RuntimeException("Unrecognized field type: " + fieldType);
   }

   private String m_defaultFieldName;
}
