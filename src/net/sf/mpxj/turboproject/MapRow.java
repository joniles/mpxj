/*
 * file:       MapRow.java
 * author:     Jon Iles
 * copyright:  (c) Packwood Software 2010
 * date:       22/03/2010
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

package net.sf.mpxj.turboproject;

import java.util.Date;
import java.util.Map;

import net.sf.mpxj.Duration;
import net.sf.mpxj.TimeUnit;
import net.sf.mpxj.common.BooleanHelper;

/**
 * Implementation of the Row interface, wrapping a Map.
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
    * {@inheritDoc}
    */
   public final String getString(String name)
   {
      return (String) getObject(name);
   }

   /**
    * {@inheritDoc}
    */
   public final Integer getInteger(String name)
   {
      return (Integer) getObject(name);
   }

   /**
    * {@inheritDoc}
    */
   public final Double getCurrency(String name)
   {
      Double result = null;
      Integer value = (Integer) getObject(name);
      if (value != null)
      {
         result = Double.valueOf(value.doubleValue() / 100);
      }
      return result;
   }

   /**
    * {@inheritDoc}
    */
   public final boolean getBoolean(String name)
   {
      boolean result = false;
      Boolean value = (Boolean) getObject(name);
      if (value != null)
      {
         result = BooleanHelper.getBoolean((Boolean) value);
      }
      return result;
   }

   /**
    * {@inheritDoc}
    */
   public final Date getDate(String name)
   {
      return (Date) getObject(name);
   }

   /**
    * {@inheritDoc}
    */
   public final Duration getDuration(String name)
   {
      Duration result = null;
      Integer duration = (Integer) getObject(name);
      if (duration != null)
      {
         result = Duration.getInstance(duration.intValue(), TimeUnit.DAYS);
      }
      return result;
   }

   /**
    * Retrieve a value from the map.
    *
    * @param name column name
    * @return column value
    */
   public final Object getObject(String name)
   {
      Object result = m_map.get(name);
      return (result);
   }

   protected Map<String, Object> m_map;
}
