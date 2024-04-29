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

package net.sf.mpxj.primavera;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;

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
    * @param ignoreErrors true if errors reading values are ignored
    */
   public MapRow(Map<String, Object> map, boolean ignoreErrors)
   {
      m_map = map;
      m_ignoreErrors = ignoreErrors;
   }

   @Override public final String getString(String name)
   {
      try
      {
         Object value = getObject(name);
         String result;
         if (value == null)
         {
            result = null;
         }
         else
         {
            if (value instanceof byte[])
            {
               result = new String((byte[]) value);
            }
            else
            {
               result = value.toString();
            }
         }
         return result;
      }

      catch (Exception ex)
      {
         if (m_ignoreErrors)
         {
            return null;
         }
         throw ex;
      }
   }

   @Override public final Integer getInteger(String name)
   {
      try
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

      catch (Exception ex)
      {
         if (m_ignoreErrors)
         {
            return null;
         }
         throw ex;
      }
   }

   @Override public final Double getDouble(String name)
   {
      try
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

      catch (Exception ex)
      {
         if (m_ignoreErrors)
         {
            return null;
         }
         throw ex;
      }
   }

   @Override public final boolean getBoolean(String name)
   {
      try
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
               if (value instanceof Number)
               {
                  // generally all non-zero numbers are treated as truthy
                  result = ((Number) value).doubleValue() != 0.0;
               }
               else
               {
                  if (value instanceof String)
                  {
                     result = parseBoolean((String) value);
                  }
               }
            }
         }
         return result;
      }

      catch (Exception ex)
      {
         if (m_ignoreErrors)
         {
            return false;
         }
         throw ex;
      }
   }

   @Override public final int getInt(String name)
   {
      try
      {
         return (NumberHelper.getInt((Number) getObject(name)));
      }

      catch (Exception ex)
      {
         if (m_ignoreErrors)
         {
            return 0;
         }
         throw ex;
      }
   }

   @Override public final LocalDateTime getDate(String name)
   {
      try
      {
         return ((LocalDateTime) getObject(name));
      }

      catch (Exception ex)
      {
         if (m_ignoreErrors)
         {
            return null;
         }
         throw ex;
      }
   }

   @Override public final Duration getDuration(String name)
   {
      try
      {
         Double value = getDouble(name);
         if (value == null)
         {
            return null;
         }
         return Duration.getInstance(value.doubleValue(), TimeUnit.HOURS);
      }

      catch (Exception ex)
      {
         if (m_ignoreErrors)
         {
            return null;
         }
         throw ex;
      }
   }

   @Override public final UUID getUUID(String name)
   {
      return DatatypeConverter.parseUUID(getString(name));
   }

   /**
    * Retrieve a value from the map.
    *
    * @param name column name
    * @return column value
    */
   private Object getObject(String name)
   {
      return m_map.get(name);
   }

   /**
    * Parse a string representation of a Boolean value.
    * XER files sometimes have "N" and "Y" to indicate boolean
    *
    * @param value string representation
    * @return Boolean value
    */
   private boolean parseBoolean(String value)
   {
      return value != null && (value.equalsIgnoreCase("true") || value.equalsIgnoreCase("y") || value.equalsIgnoreCase("yes"));
   }

   protected final Map<String, Object> m_map;
   private final boolean m_ignoreErrors;
}
