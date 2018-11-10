/*
 * file:       CustomProperty.java
 * author:     Jon Iles
 * copyright:  (c) Packwood Software 2017
 * date:       28 December 2017
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

package net.sf.mpxj.ganttproject;

import net.sf.mpxj.FieldType;

/**
 * This class is used to track which custom fields of a certain type
 * have been used when extracting custom properties from a GanttProject schedule.
 */
final class CustomProperty
{
   /**
    * Constructor.
    *
    * @param fields array of available fields
    */
   public CustomProperty(FieldType[] fields)
   {
      this(fields, 0);
   }

   /**
    * Constructor.
    *
    * @param fields array of available fields
    * @param index index into this array at which to start
    */
   public CustomProperty(FieldType[] fields, int index)
   {
      m_fields = fields;
      m_index = index;
   }

   /**
    * Retrieve the next available field.
    *
    * @return FieldType instance for the next available field
    */
   public FieldType getField()
   {
      FieldType result = null;
      if (m_index < m_fields.length)
      {
         result = m_fields[m_index++];
      }

      return result;
   }

   private final FieldType[] m_fields;
   private int m_index;
}
