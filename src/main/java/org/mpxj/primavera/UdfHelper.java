/*
 * file:       UdfHelper.java
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
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import org.mpxj.CustomField;
import org.mpxj.DataType;
import org.mpxj.FieldType;
import org.mpxj.ProjectFile;
import org.mpxj.common.FieldLists;
import org.mpxj.common.FieldTypeHelper;

/**
 * Common methods for working with user defined fields in P6 schedules.
 */
final class UdfHelper
{
   /**
    * Retrieve a set of FieldType instances representing all the fields
    * in a schedule which should be treated as user defined fields when
    * exported to P6.
    *
    * @param file schedule being exported
    * @return set of FieldType instances
    */
   public static Set<FieldType> getUserDefinedFieldsSet(ProjectFile file)
   {
      // All custom fields with configuration
      Set<FieldType> set = file.getCustomFields().stream().map(CustomField::getFieldType).filter(Objects::nonNull).collect(Collectors.toSet());

      // All user defined fields
      set.addAll(file.getUserDefinedFields());

      // All custom fields with values
      set.addAll(file.getPopulatedFields().stream().filter(FieldLists.CUSTOM_FIELDS_SET::contains).collect(Collectors.toSet()));

      // Remove unknown fields
      set.removeIf(f -> FieldTypeHelper.getFieldID(f) == -1);

      return set;
   }

   /**
    * Retrieve a UDF data type by its value from a PMXML file.
    *
    * @param value UDF data type value
    * @return DataType instance
    */
   public static DataType getDataTypeFromXml(String value)
   {
      return DATA_TYPE_FROM_XML.get(value);
   }

   /**
    * Retrieve a UDF data type by its value from an XER file or P6 database.
    *
    * @param value UDF data type value
    * @return DataType instance
    */
   public static DataType getDataTypeFromXer(String value)
   {
      return DATA_TYPE_FROM_XER.get(value);
   }

   /**
    * Retrieve the string value representing a UDF data type in a PMXML file.
    *
    * @param value DataType instance
    * @return string value
    */
   public static String getXmlFromDataType(DataType value)
   {
      String result = XML_FROM_DATA_TYPE.get(value);
      if (result == null)
      {
         throw new RuntimeException("Unconvertible data type: " + value);
      }
      return result;
   }

   /**
    * Retrieve the string value representing a UDF data type in an XER file.
    *
    * @param value DataType instance
    * @return string value
    */
   public static String getXerFromDataType(DataType value)
   {
      String result = XER_FROM_DATA_TYPE.get(value);
      if (result == null)
      {
         throw new RuntimeException("Unconvertible data type: " + value);
      }
      return result;
   }

   private static final Map<String, DataType> DATA_TYPE_FROM_XER = new HashMap<>();
   static
   {
      DATA_TYPE_FROM_XER.put("FT_TEXT", DataType.STRING);
      DATA_TYPE_FROM_XER.put("FT_MONEY", DataType.CURRENCY);
      DATA_TYPE_FROM_XER.put("FT_END_DATE", DataType.DATE);
      DATA_TYPE_FROM_XER.put("FT_STATICTYPE", DataType.STRING);
      DATA_TYPE_FROM_XER.put("FT_INT", DataType.INTEGER);
      DATA_TYPE_FROM_XER.put("FT_FLOAT", DataType.NUMERIC);
      DATA_TYPE_FROM_XER.put("FT_FLOAT_2_DECIMALS", DataType.NUMERIC);
      DATA_TYPE_FROM_XER.put("FT_START_DATE", DataType.DATE);
   }

   private static final Map<String, DataType> DATA_TYPE_FROM_XML = new HashMap<>();
   static
   {
      DATA_TYPE_FROM_XML.put("Text", DataType.STRING);
      DATA_TYPE_FROM_XML.put("Cost", DataType.CURRENCY);
      DATA_TYPE_FROM_XML.put("Finish Date", DataType.DATE);
      DATA_TYPE_FROM_XML.put("Indicator", DataType.STRING);
      DATA_TYPE_FROM_XML.put("Integer", DataType.INTEGER);
      DATA_TYPE_FROM_XML.put("Double", DataType.NUMERIC);
      DATA_TYPE_FROM_XML.put("Start Date", DataType.DATE);
   }

   private static final Map<DataType, String> XML_FROM_DATA_TYPE = new HashMap<>();
   static
   {
      XML_FROM_DATA_TYPE.put(DataType.BINARY, "Text");
      XML_FROM_DATA_TYPE.put(DataType.STRING, "Text");
      XML_FROM_DATA_TYPE.put(DataType.DURATION, "Text");
      XML_FROM_DATA_TYPE.put(DataType.DATE, "Start Date");
      XML_FROM_DATA_TYPE.put(DataType.NUMERIC, "Double");
      XML_FROM_DATA_TYPE.put(DataType.BOOLEAN, "Integer");
      XML_FROM_DATA_TYPE.put(DataType.INTEGER, "Integer");
      XML_FROM_DATA_TYPE.put(DataType.SHORT, "Integer");
      XML_FROM_DATA_TYPE.put(DataType.CURRENCY, "Cost");
   }

   private static final Map<DataType, String> XER_FROM_DATA_TYPE = new HashMap<>();
   static
   {
      XER_FROM_DATA_TYPE.put(DataType.CUSTOM, "FT_TEXT");
      XER_FROM_DATA_TYPE.put(DataType.BINARY, "FT_TEXT");
      XER_FROM_DATA_TYPE.put(DataType.STRING, "FT_TEXT");
      XER_FROM_DATA_TYPE.put(DataType.DURATION, "FT_TEXT");
      XER_FROM_DATA_TYPE.put(DataType.DATE, "FT_START_DATE");
      XER_FROM_DATA_TYPE.put(DataType.NUMERIC, "FT_FLOAT");
      XER_FROM_DATA_TYPE.put(DataType.BOOLEAN, "FT_INT");
      XER_FROM_DATA_TYPE.put(DataType.INTEGER, "FT_INT");
      XER_FROM_DATA_TYPE.put(DataType.SHORT, "FT_INT");
      XER_FROM_DATA_TYPE.put(DataType.CURRENCY, "FT_MONEY");
   }
}
