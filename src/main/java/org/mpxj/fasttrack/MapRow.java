/*
 * file:       MapRow.java
 * author:     Jon Iles
 * copyright:  (c) Packwood Software 2017
 * date:       14/03/2016
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

package org.mpxj.fasttrack;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

import java.util.Map;
import java.util.UUID;

import org.mpxj.Duration;
import org.mpxj.common.BooleanHelper;
import org.mpxj.common.NumberHelper;

/**
 * Implementation of the Row interface, wrapping a Map.
 */
class MapRow
{
   /**
    * Constructor.
    *
    * @param table parent table
    * @param map map to be wrapped by this instance
    */
   public MapRow(FastTrackTable table, Map<FastTrackField, Object> map)
   {
      m_table = table;
      m_map = map;
   }

   /**
    * Retrieve a string field.
    *
    * @param type field type
    * @return field data
    */
   public String getString(FastTrackField type)
   {
      return (String) getObject(type);
   }

   /**
    * Retrieve an integer field.
    *
    * @param type field type
    * @return field data
    */
   public Integer getInteger(FastTrackField type)
   {
      return (Integer) getObject(type);
   }

   /**
    * Retrieve a double field.
    *
    * @param type field type
    * @return field data
    */
   public Double getDouble(FastTrackField type)
   {
      return (Double) getObject(type);
   }

   /**
    * Retrieve a currency field.
    *
    * @param type field type
    * @return field data
    */
   public Double getCurrency(FastTrackField type)
   {
      return getDouble(type);
   }

   /**
    * Retrieve a boolean field.
    *
    * @param type field type
    * @return field data
    */
   public boolean getBoolean(FastTrackField type)
   {
      boolean result = false;
      Object value = getObject(type);
      if (value != null)
      {
         result = BooleanHelper.getBoolean((Boolean) value);
      }
      return result;
   }

   /**
    * Retrieve an integer field as an int.
    *
    * @param type field type
    * @return field data
    */
   public int getInt(FastTrackField type)
   {
      return (NumberHelper.getInt((Number) getObject(type)));
   }

   /**
    * Retrieve a timestamp field.
    *
    * @param dateName field containing the date component
    * @param timeName field containing the time component
    * @return Date instance
    */
   public LocalDateTime getTimestamp(FastTrackField dateName, FastTrackField timeName)
   {
      LocalDateTime result = null;
      LocalDate date = getDate(dateName);
      if (date != null)
      {
         Object timeObject = getObject(timeName);
         // TODO: we should probably associated a type with each column and validate as we read
         if (timeObject instanceof LocalTime)
         {
            LocalTime time = (LocalTime) timeObject;
            result = LocalDateTime.of(date, time);
         }
         else
         {
            result = date.atStartOfDay();
         }
      }

      return result;
   }

   public LocalDateTime getTimestamp(FastTrackField dateName)
   {
      LocalDateTime result = null;
      LocalDate date = getDate(dateName);
      if (date != null)
      {
         result = date.atStartOfDay();
      }
      return result;
   }

   /**
    * Retrieve a date field.
    *
    * @param type field type
    * @return Date instance
    */
   public LocalDate getDate(FastTrackField type)
   {
      return (LocalDate) getObject(type);
   }

   /**
    * Retrieve a duration field.
    *
    * @param type field type
    * @return Duration instance
    */
   public Duration getDuration(FastTrackField type)
   {
      Double value = (Double) getObject(type);
      return value == null ? null : Duration.getInstance(value.doubleValue(), m_table.getDurationTimeUnit());
   }

   /**
    * Retrieve a work field.
    *
    * @param type field type
    * @return Duration instance
    */
   public Duration getWork(FastTrackField type)
   {
      Double value = (Double) getObject(type);
      return value == null ? null : Duration.getInstance(value.doubleValue(), m_table.getWorkTimeUnit());
   }

   /**
    * Retrieve a value from the map.
    *
    * @param type column name
    * @return column value
    */
   public Object getObject(FastTrackField type)
   {
      return m_map.get(type);
   }

   /**
    * Retrieve a UUID field.
    *
    * @param type field type
    * @return UUID instance
    */
   public UUID getUUID(FastTrackField type)
   {
      String value = getString(type);
      UUID result = null;
      if (value != null && value.length() >= 36)
      {
         if (value.startsWith("{"))
         {
            value = value.substring(1, value.length() - 1);
         }
         if (value.length() > 16)
         {
            value = value.substring(0, 36);
         }
         result = UUID.fromString(value);
      }
      return result;
   }

   /**
    * Retrieve the internal Map instance used to hold row data.
    *
    * @return Map instance
    */
   public Map<FastTrackField, Object> getMap()
   {
      return m_map;
   }

   protected final Map<FastTrackField, Object> m_map;
   private final FastTrackTable m_table;
}
