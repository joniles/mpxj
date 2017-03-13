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

package net.sf.mpxj.asta;

import java.util.Date;
import java.util.Map;

import net.sf.mpxj.Duration;
import net.sf.mpxj.TimeUnit;
import net.sf.mpxj.common.BooleanHelper;
import net.sf.mpxj.common.NumberHelper;

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

   /**
    * {@inheritDoc}
    */
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

   /**
    * {@inheritDoc}
    */
   @Override public Integer getInteger(String name)
   {
      Object result = getObject(name);
      if (result != null)
      {
         if (result instanceof Integer == false)
         {
            result = Integer.valueOf(((Number) result).intValue());
         }
      }
      return ((Integer) result);
   }

   /**
    * {@inheritDoc}
    */
   @Override public Double getDouble(String name)
   {
      Object result = getObject(name);
      if (result != null)
      {
         if (result instanceof Double == false)
         {
            result = Double.valueOf(((Number) result).doubleValue());
         }
      }
      return ((Double) result);
   }

   /**
    * {@inheritDoc}
    */
   @Override public Double getCurrency(String name)
   {
      Double value = getDouble(name);
      if (value != null)
      {
         value = Double.valueOf(value.doubleValue() / 100);
      }
      return (value);
   }

   /**
    * {@inheritDoc}
    */
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

   /**
    * {@inheritDoc}
    */
   @Override public int getInt(String name)
   {
      return (NumberHelper.getInt((Number) getObject(name)));
   }

   /**
    * {@inheritDoc}
    */
   @Override public Date getDate(String name)
   {
      return ((Date) getObject(name));
   }

   /**
    * {@inheritDoc}
    */
   @Override public Duration getDuration(String name)
   {
      return (Duration.getInstance(NumberHelper.getDouble(getDouble(name)), TimeUnit.HOURS));
   }

   /**
    * {@inheritDoc}
    */
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
   public Object getObject(String name)
   {
      Object result = m_map.get(name);
      return (result);
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

   protected Map<String, Object> m_map;
}
