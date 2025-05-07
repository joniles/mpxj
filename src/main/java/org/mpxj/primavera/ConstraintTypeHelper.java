/*
 * file:       ConstraintTypeHelper.java
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

import org.mpxj.ConstraintType;

/**
 * Provides methods to convert to and from Primavera's representation
 * of constraint types.
 */
final class ConstraintTypeHelper
{
   /**
    * Retrieve a constraint type by its value from a PMXML file.
    *
    * @param value constraint type value
    * @return ConstraintType instance
    */
   public static ConstraintType getInstanceFromXml(String value)
   {
      return XML_TYPE_MAP.get(value);
   }

   /**
    * Retrieve a constraint type by its value from an XER file or P6 database.
    *
    * @param value constraint type value
    * @return ConstraintType instance
    */
   public static ConstraintType getInstanceFromXer(String value)
   {
      return XER_TYPE_MAP.get(value);
   }

   /**
    * Retrieve the string value representing a constraint type in a PMXML file.
    *
    * @param value ConstraintType instance
    * @return string value
    */
   public static String getXmlFromInstance(ConstraintType value)
   {
      return TYPE_XML_MAP.get(value);
   }

   /**
    * Retrieve the string value representing a constraint type in an XER file.
    *
    * @param value ConstraintType instance
    * @return string value
    */
   public static String getXerFromInstance(ConstraintType value)
   {
      return TYPE_XER_MAP.get(value);
   }

   private static final Map<String, ConstraintType> XML_TYPE_MAP = new HashMap<>();
   static
   {
      XML_TYPE_MAP.put("Start On", ConstraintType.START_ON);
      XML_TYPE_MAP.put("Start On or Before", ConstraintType.START_NO_LATER_THAN);
      XML_TYPE_MAP.put("Start On or After", ConstraintType.START_NO_EARLIER_THAN);
      XML_TYPE_MAP.put("Finish On", ConstraintType.FINISH_ON);
      XML_TYPE_MAP.put("Finish On or Before", ConstraintType.FINISH_NO_LATER_THAN);
      XML_TYPE_MAP.put("Finish On or After", ConstraintType.FINISH_NO_EARLIER_THAN);
      XML_TYPE_MAP.put("As Late As Possible", ConstraintType.AS_LATE_AS_POSSIBLE);
      XML_TYPE_MAP.put("Mandatory Start", ConstraintType.MUST_START_ON);
      XML_TYPE_MAP.put("Mandatory Finish", ConstraintType.MUST_FINISH_ON);
   }

   private static final Map<ConstraintType, String> TYPE_XML_MAP = new HashMap<>();
   static
   {
      TYPE_XML_MAP.put(ConstraintType.START_ON, "Start On");
      TYPE_XML_MAP.put(ConstraintType.START_NO_LATER_THAN, "Start On or Before");
      TYPE_XML_MAP.put(ConstraintType.START_NO_EARLIER_THAN, "Start On or After");
      TYPE_XML_MAP.put(ConstraintType.FINISH_ON, "Finish On");
      TYPE_XML_MAP.put(ConstraintType.FINISH_NO_LATER_THAN, "Finish On or Before");
      TYPE_XML_MAP.put(ConstraintType.FINISH_NO_EARLIER_THAN, "Finish On or After");
      TYPE_XML_MAP.put(ConstraintType.AS_LATE_AS_POSSIBLE, "As Late As Possible");
      TYPE_XML_MAP.put(ConstraintType.MUST_START_ON, "Mandatory Start");
      TYPE_XML_MAP.put(ConstraintType.MUST_FINISH_ON, "Mandatory Finish");
   }

   private static final Map<String, ConstraintType> XER_TYPE_MAP = new HashMap<>();
   static
   {
      XER_TYPE_MAP.put("CS_MSO", ConstraintType.START_ON);
      XER_TYPE_MAP.put("CS_MSOB", ConstraintType.START_NO_LATER_THAN);
      XER_TYPE_MAP.put("CS_MSOA", ConstraintType.START_NO_EARLIER_THAN);
      XER_TYPE_MAP.put("CS_MEO", ConstraintType.FINISH_ON);
      XER_TYPE_MAP.put("CS_MEOB", ConstraintType.FINISH_NO_LATER_THAN);
      XER_TYPE_MAP.put("CS_MEOA", ConstraintType.FINISH_NO_EARLIER_THAN);
      XER_TYPE_MAP.put("CS_ALAP", ConstraintType.AS_LATE_AS_POSSIBLE);
      XER_TYPE_MAP.put("CS_MANDSTART", ConstraintType.MUST_START_ON);
      XER_TYPE_MAP.put("CS_MANDFIN", ConstraintType.MUST_FINISH_ON);
   }

   private static final Map<ConstraintType, String> TYPE_XER_MAP = new HashMap<>();
   static
   {
      TYPE_XER_MAP.put(ConstraintType.AS_SOON_AS_POSSIBLE, "");
      TYPE_XER_MAP.put(ConstraintType.START_ON, "CS_MSO");
      TYPE_XER_MAP.put(ConstraintType.START_NO_LATER_THAN, "CS_MSOB");
      TYPE_XER_MAP.put(ConstraintType.START_NO_EARLIER_THAN, "CS_MSOA");
      TYPE_XER_MAP.put(ConstraintType.FINISH_ON, "CS_MEO");
      TYPE_XER_MAP.put(ConstraintType.FINISH_NO_LATER_THAN, "CS_MEOB");
      TYPE_XER_MAP.put(ConstraintType.FINISH_NO_EARLIER_THAN, "CS_MEOA");
      TYPE_XER_MAP.put(ConstraintType.AS_LATE_AS_POSSIBLE, "CS_ALAP");
      TYPE_XER_MAP.put(ConstraintType.MUST_START_ON, "CS_MANDSTART");
      TYPE_XER_MAP.put(ConstraintType.MUST_FINISH_ON, "CS_MANDFIN");
   }
}
