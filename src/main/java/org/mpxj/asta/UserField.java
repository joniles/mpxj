/*
 * file:       UserField.java
 * author:     Jon Iles
 * copyright:  (c) Packwood Software 2021
 * date:       26/05/2021
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

package org.mpxj.asta;

import org.mpxj.FieldType;

/**
 * Relates the MPXJ field type to Asta type attributes.
 */
class UserField
{
   /**
    * Constructor.
    *
    * @param field MPXJ field
    * @param objectType Asta object
    * @param dataType Asta data type
    */
   public UserField(FieldType field, int objectType, int dataType)
   {
      m_field = field;
      m_objectType = objectType;
      m_dataType = dataType;
   }

   /**
    * Retrieve the MPXJ field type.
    *
    * @return MPXJ field type
    */
   public FieldType getField()
   {
      return m_field;
   }

   /**
    * Retrieve the Asta object type.
    *
    * @return Asta object type
    */
   public int getObjectType()
   {
      return m_objectType;
   }

   /**
    * Retrieve the Asta data type.
    *
    * @return Asta data type
    */
   public int getDataType()
   {
      return m_dataType;
   }

   private final FieldType m_field;
   private final int m_objectType;
   private final int m_dataType;
}
