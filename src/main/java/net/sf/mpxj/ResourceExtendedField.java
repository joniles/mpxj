/*
 * file:       ResourceExtendedField.java
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
 * Resource extended fields used by readers and writers.
 */
public enum ResourceExtendedField implements ExtendedFieldType
{
   RESOURCE_ID("Resource ID", ResourceField.TEXT1),
   DESCRIPTION("Description", ResourceField.TEXT2),
   SUPPLY_REFERENCE("Supply Reference", ResourceField.TEXT3),
   RATE("Rate", ResourceField.NUMBER1),
   POOL("Pool", ResourceField.NUMBER2),
   PER_DAY("Per Day", ResourceField.NUMBER3),
   PRIORITY("Priority", ResourceField.NUMBER4),
   PERIOD_DUR("Period Dur", ResourceField.NUMBER5),
   EXPENSES_ONLY("Expenses Only", ResourceField.FLAG1),
   MODIFY_ON_INTEGRATE("Modify On Integrate", ResourceField.FLAG2),
   UNIT("Unit", ResourceField.TEXT4);

   /**
    * Constructor.
    *
    * @param name field name
    * @param type field type
    */
   private ResourceExtendedField(String name, FieldType type)
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
