/*
 * file:       ObjectType.java
 * author:     Jon Iles
 * copyright:  (c) Timephased Ltd 2023
 * date:       2023-02-04
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

import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Identifies the different entities to which user defined fields may be attached
 * in an Asta schedule.
 */
enum ObjectType
{
   BAR_OBJECT_TYPE(16),
   TASK_OBJECT_TYPE(20),
   MILESTONE_OBJECT_TYPE(21),
   CONSUMABLE_RESOURCE_OBJECT_TYPE(50),
   PERMANENT_RESOURCE_OBJECT_TYPE(51),
   PERMANENT_SCHEDULE_ALLOCATION_OBJECT_TYPE(59);

   /**
    * Constructor.
    *
    * @param value object type id
    */
   ObjectType(int value)
   {
      m_value = value;
   }

   /**
    * Retrieve an ObjectType instance by ID.
    *
    * @param value ID value
    * @return ObjectType instance
    */
   public static ObjectType getInstance(Integer value)
   {
      return MAP.get(value);
   }

   private final int m_value;

   private static final Map<Integer, ObjectType> MAP = Arrays.stream(ObjectType.values()).collect(Collectors.toMap(o -> Integer.valueOf(o.m_value), o -> o));
}
