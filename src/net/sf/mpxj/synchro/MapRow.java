/*
 * file:       MapRow.java
 * author:     Jon Iles
 * copyright:  (c) Packwood Software 2018
 * date:       2018-10-11
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

package net.sf.mpxj.synchro;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import net.sf.mpxj.Duration;
import net.sf.mpxj.RelationType;
import net.sf.mpxj.common.BooleanHelper;

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
    * Retrieve a double value.
    *
    * @param name column name
    * @return double value
    */
   public final Double getDouble(String name)
   {
      return (Double) getObject(name);
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
   public final Date getDate(String name)
   {
      return (Date) getObject(name);
   }

   /**
    * Retrieve a UUID value.
    *
    * @param name column name
    * @return UUID value
    */
   public final UUID getUUID(String name)
   {
      return (UUID) getObject(name);
   }

   /**
    * Retrieve row from a nested table.
    *
    * @param name column name
    * @return nested table rows
    */
   @SuppressWarnings("unchecked") public final List<MapRow> getRows(String name)
   {
      return (List<MapRow>) getObject(name);
   }

   @Override public String toString()
   {
      StringBuilder sb = new StringBuilder();

      for (Map.Entry<String, Object> entry : m_map.entrySet())
      {

         if (sb.length() != 0)
         {
            sb.append(", ");
         }
         sb.append(entry.getKey());
         sb.append('=');
         sb.append(entry.getValue());

      }

      return "[MapRow " + sb.toString() + "]";
   }

   /**
    * Retrieve a value without being specific about its type.
    *
    * @param name column name
    * @return value
    */
   private final Object getObject(String name)
   {
      return m_map.get(name);
   }

   protected Map<String, Object> m_map;
}
