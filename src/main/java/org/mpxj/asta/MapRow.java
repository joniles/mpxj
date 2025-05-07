/*
 * file:       MapRow.java
 * author:     Jon Iles
 * copyright:  (c) Packwood Software 2012
 * date:       29/04/2012
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

import java.time.LocalDateTime;
import java.util.ArrayList;

import java.util.List;
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

   @Override public String getString(String name)
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

   @Override public Integer getInteger(String name)
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

   @Override public Double getDouble(String name)
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

   @Override public Double getPercent(String name)
   {
      Object result = getObject(name);
      if (result != null)
      {
         result = Double.valueOf(Math.round(((Number) result).doubleValue() * 100.0) / 100.0);
      }
      return ((Double) result);
   }

   @Override public Double getCurrency(String name)
   {
      Double value = getDouble(name);
      if (value != null)
      {
         value = Double.valueOf(value.doubleValue() / 100);
      }
      return (value);
   }

   @Override public boolean getBoolean(String name)
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

   @Override public int getInt(String name)
   {
      return (NumberHelper.getInt((Number) getObject(name)));
   }

   @Override public LocalDateTime getDate(String name)
   {
      return ((LocalDateTime) getObject(name));
   }

   @Override public Duration getDuration(String name)
   {
      return (Duration.getInstance(NumberHelper.getDouble(getDouble(name)), TimeUnit.HOURS));
   }

   @Override public Duration getWork(String name)
   {
      return (Duration.getInstance(NumberHelper.getDouble(getDouble(name)) / 3600, TimeUnit.HOURS));
   }

   /**
    * Retrieve a value from the map.
    *
    * @param name column name
    * @return column value
    */
   @Override public Object getObject(String name)
   {
      return m_map.get(name);
   }

   /**
    * Retrieve the internal Map instance used to hold row data.
    *
    * @return Map instance
    */
   public Map<String, Object> getMap()
   {
      return m_map;
   }

   @Override public void addChild(Row row)
   {
      m_childRows.add(row);
   }

   @Override public List<Row> getChildRows()
   {
      return m_childRows;
   }

   @Override public void merge(Row row, String prefix)
   {
      Map<String, Object> otherMap = ((MapRow) row).m_map;
      for (Map.Entry<String, Object> entry : otherMap.entrySet())
      {
         m_map.put(prefix + entry.getKey(), entry.getValue());
      }
   }
   protected final Map<String, Object> m_map;
   private final List<Row> m_childRows = new ArrayList<>();
}
