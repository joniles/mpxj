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
   ACTIVITY_TYPE("Activity Type", TaskField.TEXT2);
   
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
