/*
 * file:       PrimaveraField.java
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

package net.sf.mpxj.primavera;

import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import net.sf.mpxj.AssignmentField;
import net.sf.mpxj.FieldType;
import net.sf.mpxj.ResourceField;
import net.sf.mpxj.TaskField;

/**
 * Primavera fields represented as custom fields.
 */
enum PrimaveraField
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

   RESOURCE_ID("Resource ID", ResourceField.TEXT1),

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
   private PrimaveraField(String name, FieldType type)
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
   public static PrimaveraField getInstance(FieldType type)
   {
      return MAP.get(type);
   }

   private final String m_name;
   private final FieldType m_type;

   private static final Map<FieldType, PrimaveraField> MAP = Stream.of(PrimaveraField.values()).collect(Collectors.toMap(PrimaveraField::getType, f -> f));

}
