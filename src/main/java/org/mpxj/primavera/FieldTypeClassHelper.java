/*
 * file:       FieldTypeClassHelper.java
 * author:     Jon Iles
 * copyright:  (c) Packwood Software 2023
 * date:       07/03/2023
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

import java.util.HashMap;
import java.util.Map;

import org.mpxj.FieldType;
import org.mpxj.FieldTypeClass;
import org.mpxj.UserDefinedField;

/**
 * Provides methods to convert to and from Primavera's representation
 * of FieldTypeClass instances.
 */
final class FieldTypeClassHelper
{
   /**
    * Retrieve a field type class instance by its value from a PMXML file.
    *
    * @param value field type class value
    * @return FieldTypeClass instance
    */
   public static FieldTypeClass getInstanceFromXml(String value)
   {
      return XML_TYPE_MAP.get(value);
   }

   /**
    * Retrieve a field type class instance by its value from an XER file or P6 database.
    *
    * @param value field type class value
    * @return FieldTypeClass instance
    */
   public static FieldTypeClass getInstanceFromXer(String value)
   {
      return XER_TYPE_MAP.get(value);
   }

   /**
    * Retrieve the string value representing a field type class in a PMXML file.
    *
    * @param fieldType FieldType instance
    * @return string value
    */
   public static String getXmlFromInstance(FieldType fieldType)
   {
      String result = TYPE_XML_MAP.get(fieldType.getFieldTypeClass());
      if (result == null)
      {
         throw new RuntimeException("Unrecognized field type: " + fieldType);
      }

      if (result.equals("Activity") && fieldType instanceof UserDefinedField && ((UserDefinedField) fieldType).getSummaryTaskOnly())
      {
         result = "WBS";
      }

      return result;
   }

   /**
    * Retrieve the string value representing a field type class in an XER file.
    *
    * @param fieldType FieldType instance
    * @return string value
    */
   public static String getXerFromInstance(FieldType fieldType)
   {
      String result = TYPE_XER_MAP.get(fieldType.getFieldTypeClass());
      if (result == null)
      {
         throw new RuntimeException("Unrecognized field type: " + fieldType);
      }

      if (result.equals("TASK") && fieldType instanceof UserDefinedField && ((UserDefinedField) fieldType).getSummaryTaskOnly())
      {
         result = "PROJWBS";
      }

      return result;
   }

   private static final Map<String, FieldTypeClass> XML_TYPE_MAP = new HashMap<>();
   static
   {
      XML_TYPE_MAP.put("Activity", FieldTypeClass.TASK);
      XML_TYPE_MAP.put("WBS", FieldTypeClass.TASK);
      XML_TYPE_MAP.put("Resource", FieldTypeClass.RESOURCE);
      XML_TYPE_MAP.put("Resource Assignment", FieldTypeClass.ASSIGNMENT);
      XML_TYPE_MAP.put("Project", FieldTypeClass.PROJECT);
   }

   private static final Map<String, FieldTypeClass> XER_TYPE_MAP = new HashMap<>();
   static
   {
      XER_TYPE_MAP.put("PROJWBS", FieldTypeClass.TASK);
      XER_TYPE_MAP.put("TASK", FieldTypeClass.TASK);
      XER_TYPE_MAP.put("RSRC", FieldTypeClass.RESOURCE);
      XER_TYPE_MAP.put("TASKRSRC", FieldTypeClass.ASSIGNMENT);
      XER_TYPE_MAP.put("PROJECT", FieldTypeClass.PROJECT);
   }

   private static final Map<FieldTypeClass, String> TYPE_XML_MAP = new HashMap<>();
   static
   {
      TYPE_XML_MAP.put(FieldTypeClass.TASK, "Activity");
      TYPE_XML_MAP.put(FieldTypeClass.RESOURCE, "Resource");
      TYPE_XML_MAP.put(FieldTypeClass.PROJECT, "Project");
      TYPE_XML_MAP.put(FieldTypeClass.ASSIGNMENT, "Resource Assignment");
      TYPE_XML_MAP.put(FieldTypeClass.CONSTRAINT, "Constraint");
   }

   private static final Map<FieldTypeClass, String> TYPE_XER_MAP = new HashMap<>();
   static
   {
      TYPE_XER_MAP.put(FieldTypeClass.TASK, "TASK");
      TYPE_XER_MAP.put(FieldTypeClass.RESOURCE, "RSRC");
      TYPE_XER_MAP.put(FieldTypeClass.ASSIGNMENT, "TASKRSRC");
      TYPE_XER_MAP.put(FieldTypeClass.PROJECT, "PROJECT");
   }
}
