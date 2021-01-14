/*
 * file:       ExtendedFieldType.java
 * author:     Jon Iles
 * copyright:  (c) Packwood Software 2021
 * date:       10/01/2021
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

package net.sf.mpxj;

import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Extended fields used by readers and writers.
 */
public enum ExtendedFieldType
{
   ACTIVITY_SUSPEND_DATE("Suspend Date", TaskField.DATE1),
   ACTIVITY_RESUME_DATE("Resume Date", TaskField.DATE2),
   ACTIVITY_ID("Code", TaskField.TEXT1), // TODO deprecate, the name should be Activity ID
   ACTIVITY_TYPE("Activity Type", TaskField.TEXT2),
   ACTIVITY_STATUS("Status", TaskField.TEXT3),
   ACTIVITY_PRIMARY_RESOURCE_ID("Primary Resource Unique ID", TaskField.NUMBER1),
   ACTIVITY_PLANNED_WORK("Planned Work", TaskField.DURATION1),
   ACTIVITY_PLANNED_DURATION("Planned Duration", TaskField.DURATION2),
   ACTIVITY_PLANNED_START("Planned Start", TaskField.START1),
   ACTIVITY_PLANNED_FINISH("Planned Finish", TaskField.FINISH1),
   ACTIVITY_OVERALL_PERCENT_COMPLETE("Overall Percent Complete", TaskField.NUMBER2),
   ACTIVITY_DEPARTMENT("Department", TaskField.TEXT4),
   ACTIVITY_MANAGER("Manager", TaskField.TEXT5),
   ACTIVITY_SECTION("Section", TaskField.TEXT6),
   ACTIVITY_MAIL("Mail", TaskField.TEXT7),

   RESOURCE_ID("Resource ID", ResourceField.TEXT1),
   RESOURCE_DESCRIPTION("Description", ResourceField.TEXT2),
   RESOURCE_SUPPLY_REFERENCE("Supply Reference", ResourceField.TEXT3),
   
   ASSIGNMENT_PLANNED_START("Planned Start", AssignmentField.START1),
   ASSIGNMENT_PLANNED_FINISH("Planned Finish", AssignmentField.FINISH1),
   ASSIGNMENT_PLANNED_COST("Planned Cost", AssignmentField.COST1),
   ASSIGNMENT_PLANNED_WORK("Planned Work", AssignmentField.DURATION1);

   /**
    * Constructor.
    * 
    * @param name field name
    * @param type field type
    */
   private ExtendedFieldType(String name, FieldType type)
   {
      m_name = name;
      m_type = type;
   }

   /**
    * Retrieve the field name.
    * 
    * @return field name
    */
   public String getName()
   {
      return m_name;
   }

   /**
    * Retrieve the field type.
    * 
    * @return field type
    */
   public FieldType getType()
   {
      return m_type;
   }

   /**
    * Retrieve the PrimaveraField instance represented by a particular field type.
    * 
    * @param type field type
    * @return PrimaveraField instance
    */
   public static ExtendedFieldType getInstance(FieldType type)
   {
      return MAP.get(type);
   }

   private final String m_name;
   private final FieldType m_type;

   public static final ExtendedFieldType[] PRIMAVERA =
   {
      ACTIVITY_SUSPEND_DATE,
      ACTIVITY_RESUME_DATE,
      ACTIVITY_ID,
      ACTIVITY_TYPE,
      ACTIVITY_STATUS,
      ACTIVITY_PRIMARY_RESOURCE_ID,
      ACTIVITY_PLANNED_WORK,
      ACTIVITY_PLANNED_DURATION,
      ACTIVITY_PLANNED_START,
      ACTIVITY_PLANNED_FINISH,
      RESOURCE_ID,
      ASSIGNMENT_PLANNED_START,
      ASSIGNMENT_PLANNED_FINISH,
      ASSIGNMENT_PLANNED_COST,
      ASSIGNMENT_PLANNED_WORK
   };

   public static final ExtendedFieldType[] P3 =
   {
      ACTIVITY_ID
   };

   public static final ExtendedFieldType[] ASTA =
   {
      ACTIVITY_ID,
      ACTIVITY_OVERALL_PERCENT_COMPLETE
   };

   public static final ExtendedFieldType[] SURETRAK =
   {
      ACTIVITY_ID,
      ACTIVITY_DEPARTMENT,
      ACTIVITY_MANAGER,
      ACTIVITY_SECTION,
      ACTIVITY_MAIL
   };

   public static final ExtendedFieldType[] SYNCHRO =
   {
      ACTIVITY_ID,
      RESOURCE_DESCRIPTION,
      RESOURCE_SUPPLY_REFERENCE
   };

   public static final ExtendedFieldType[] PHOENIX =
   {
      ACTIVITY_ID
   };

   private static final Map<FieldType, ExtendedFieldType> MAP = Stream.of(ExtendedFieldType.values()).collect(Collectors.toMap(ExtendedFieldType::getType, f -> f));
}
