/*
 * file:       MapRow.java
 * author:     Jon Iles
 * copyright:  (c) Packwood Software 2007
 * date:       08-Feb-2006
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

package org.mpxj.mpd;

import java.time.LocalDateTime;
import java.util.Map;

import org.mpxj.Duration;
import org.mpxj.TimeUnit;
import org.mpxj.common.BooleanHelper;
import org.mpxj.common.NumberHelper;

/**
 * Implementation of the Row interface, wrapping a Map.
 */
class MapRow implements Row
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

   @Override public final String getString(String name)
   {
      Object value = getObject(name);
      String result;
      if (value instanceof byte[])
      {
         result = new String((byte[]) value);
      }
      else
      {
         result = (String) value;
      }
      return (result);
   }

   @Override public final Integer getInteger(String name)
   {
      Object result = getObject(name);
      if (result != null)
      {
         if (!(result instanceof Integer))
         {
            result = Integer.valueOf(((Number) result).intValue());
         }
      }
      return ((Integer) result);
   }

   @Override public final Double getDouble(String name)
   {
      Object result = getObject(name);
      if (result != null)
      {
         if (!(result instanceof Double))
         {
            result = Double.valueOf(((Number) result).doubleValue());
         }
      }
      return ((Double) result);
   }

   @Override public final Double getCurrency(String name)
   {
      Double value = getDouble(name);
      if (value != null)
      {
         value = Double.valueOf(value.doubleValue() / 100);
      }
      return (value);
   }

   @Override public final boolean getBoolean(String name)
   {
      boolean result = false;
      Object value = getObject(name);
      if (value != null)
      {
         if (value instanceof Boolean)
         {
            result = BooleanHelper.getBoolean((Boolean) value);
         }
         else
         {
            result = (((Number) value).intValue() == 1);
         }
      }
      return result;
   }

   @Override public final int getInt(String name)
   {
      return (NumberHelper.getInt((Number) getObject(name)));
   }

   @Override public final LocalDateTime getDate(String name)
   {
      return ((LocalDateTime) getObject(name));
   }

   @Override public final Duration getDuration(String name)
   {
      return (Duration.getInstance(NumberHelper.getDouble(getDouble(name)) / 60000, TimeUnit.HOURS));
   }

   /**
    * Retrieve a value from the map, ensuring that a key exists in the map
    * with the specified name.
    *
    * @param name column name
    * @return column value
    */
   private Object getObject(String name)
   {
      if (!m_map.containsKey(name))
      {
         throw new IllegalArgumentException("Invalid column name " + name);
      }

      return m_map.get(name);
   }

   protected final Map<String, Object> m_map;
}
