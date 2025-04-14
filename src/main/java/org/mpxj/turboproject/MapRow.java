/*
 * file:       MapRow.java
 * author:     Jon Iles
 * copyright:  (c) Packwood Software 2018
 * date:       12/01/2018
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

package org.mpxj.turboproject;

import java.time.LocalDateTime;
import java.util.Map;

import org.mpxj.Duration;
import org.mpxj.RelationType;
import org.mpxj.common.BooleanHelper;

/**
 * Wraps a simple map which contains name value
 * pairs representing the column values
 * from an individual row. Provides type-specific
 * methods to retrieve the column values.
 */
class MapRow
{
   /**
    * Constructor.
    *
    * @param map map to be wrapped by this instance
    */
   public MapRow(Map<String, Object> map)
   {
      m_map = map;
   }

   /**
    * Retrieve a string value.
    *
    * @param name column name
    * @return string value
    */
   public final String getString(String name)
   {
      return (String) getObject(name);
   }

   /**
    * Retrieve an integer value.
    *
    * @param name column name
    * @return integer value
    */
   public final Integer getInteger(String name)
   {
      return (Integer) getObject(name);
   }

   /**
    * Retrieve a relation type value.
    *
    * @param name column name
    * @return relation type value
    */
   public final RelationType getRelationType(String name)
   {
      return (RelationType) getObject(name);
   }

   /**
    * Retrieve a boolean value.
    *
    * @param name column name
    * @return boolean value
    */
   public final boolean getBoolean(String name)
   {
      boolean result = false;
      Boolean value = (Boolean) getObject(name);
      if (value != null)
      {
         result = BooleanHelper.getBoolean(value);
      }
      return result;
   }

   /**
    * Retrieve a duration value.
    *
    * @param name column name
    * @return duration value
    */
   public final Duration getDuration(String name)
   {
      return (Duration) getObject(name);
   }

   /**
    * Retrieve a date value.
    *
    * @param name column name
    * @return date value
    */
   public final LocalDateTime getDate(String name)
   {
      return (LocalDateTime) getObject(name);
   }

   /**
    * Retrieve a value without being specific about its type.
    *
    * @param name column name
    * @return value
    */
   public final Object getObject(String name)
   {
      return m_map.get(name);
   }

   protected final Map<String, Object> m_map;
}
