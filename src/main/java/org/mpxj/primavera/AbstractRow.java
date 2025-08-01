/*
 * file:       AbstractRow.java
 * author:     Jon Iles
 * date:       2025-06-29
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

package org.mpxj.primavera;

import java.time.LocalDateTime;
import java.util.UUID;

import org.mpxj.Duration;
import org.mpxj.TimeUnit;
import org.mpxj.common.NumberHelper;

/**
 * Common implementation detail for the Row interface.
 */
abstract class AbstractRow implements Row
{
   public AbstractRow(boolean ignoreErrors)
   {
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
      Boolean result = getBooleanObject(name);
      return result != null && result.booleanValue();
   }

   @Override public final Boolean getBooleanObject(String name)
   {
      try
      {
         Object value = getObject(name);
         if (value == null)
         {
            return null;
         }

         if (value instanceof Boolean)
         {
            return (Boolean) value;
         }

         if (value instanceof Number)
         {
            // generally all non-zero numbers are treated as truthy
            return Boolean.valueOf(((Number) value).doubleValue() != 0.0);
         }

         if (value instanceof String)
         {
            return parseBoolean((String) value);
         }

         return null;
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
      try
      {
         return DatatypeConverter.parseUUID(getString(name));
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

   /**
    * Retrieve a value from the map.
    *
    * @param name column name
    * @return column value
    */
   protected abstract Object getObject(String name);

   /**
    * Parse a string representation of a Boolean value.
    * XER files sometimes have "N" and "Y" to indicate boolean
    *
    * @param value string representation
    * @return Boolean value
    */
   private Boolean parseBoolean(String value)
   {
      return Boolean.valueOf(value != null && (value.equalsIgnoreCase("true") || value.equalsIgnoreCase("y") || value.equalsIgnoreCase("yes")));
   }

   private final boolean m_ignoreErrors;
}
