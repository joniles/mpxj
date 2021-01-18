/*
 * file:       TaskExtendedField.java
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

/**
 * Task extended fields used by readers and writers.
 */
public enum TaskExtendedField implements ExtendedFieldType
{
   SUSPEND_DATE("Suspend Date", TaskField.DATE1),
   RESUME_DATE("Resume Date", TaskField.DATE2),
   ACTIVITY_ID("Code", TaskField.TEXT1), // TODO deprecate, the name should be Activity ID
   ACTIVITY_TYPE("Activity Type", TaskField.TEXT2),
   STATUS("Status", TaskField.TEXT3),
   PRIMARY_RESOURCE_ID("Primary Resource Unique ID", TaskField.NUMBER1),
   PLANNED_WORK("Planned Work", TaskField.DURATION1),
   PLANNED_DURATION("Planned Duration", TaskField.DURATION2),
   PLANNED_START("Planned Start", TaskField.START1),
   PLANNED_FINISH("Planned Finish", TaskField.FINISH1),
   OVERALL_PERCENT_COMPLETE("Overall Percent Complete", TaskField.NUMBER2),
   DEPARTMENT("Department", TaskField.TEXT4),
   MANAGER("Manager", TaskField.TEXT5),
   SECTION("Section", TaskField.TEXT6),
   MAIL("Mail", TaskField.TEXT7),
   HAMMOCK_CODE("Hammock Code", TaskField.TEXT9),
   WORKERS_PER_DAY("Workers Per Day", TaskField.NUMBER3),
   RESPONSIBILITY_CODE("Responsibility Code", TaskField.TEXT10),
   WORK_AREA_CODE("Work Area Code", TaskField.TEXT11),
   MOD_OR_CLAIM_NO("Mod or Claim No", TaskField.TEXT12),
   BID_ITEM("Bid Item", TaskField.TEXT13),
   PHASE_OF_WORK("Phase of Work", TaskField.TEXT14),
   CATEGORY_OF_WORK("Category of Work", TaskField.TEXT15),
   FEATURE_OF_WORK("Feature of Work", TaskField.TEXT16),
   STORED_MATERIAL("Stored Material", TaskField.COST1);

   /**
    * Constructor.
    * 
    * @param name field name
    * @param type field type
    */
   private TaskExtendedField(String name, FieldType type)
   {
      m_name = name;
      m_type = type;
   }

   @Override public String getName()
   {
      return m_name;
   }

   @Override public FieldType getType()
   {
      return m_type;
   }

   private final String m_name;
   private final FieldType m_type;
}
