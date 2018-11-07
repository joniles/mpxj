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

package net.sf.mpxj.mpd;

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

   /**
    * {@inheritDoc}
    */
   @Override public final Integer getInteger(String name)
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
   @Override public final Double getDouble(String name)
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
   @Override public final Double getCurrency(String name)
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

   /**
    * {@inheritDoc}
    */
   @Override public final int getInt(String name)
   {
      return (NumberHelper.getInt((Number) getObject(name)));
   }

   /**
    * {@inheritDoc}
    */
   @Override public final Date getDate(String name)
   {
      return ((Date) getObject(name));
   }

   /**
    * {@inheritDoc}
    */
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
   private final Object getObject(String name)
   {
      if (m_map.containsKey(name) == false)
      {
         throw new IllegalArgumentException("Invalid column name " + name);
      }

      Object result = m_map.get(name);

      return (result);
   }

   protected Map<String, Object> m_map;
}
