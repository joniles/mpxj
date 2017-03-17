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

package net.sf.mpxj.fasttrack;

import java.util.Calendar;
import java.util.Date;
import java.util.Map;
import java.util.UUID;

import net.sf.mpxj.Duration;
import net.sf.mpxj.common.BooleanHelper;
import net.sf.mpxj.common.NumberHelper;

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
   public Date getTimestamp(FastTrackField dateName, FastTrackField timeName)
   {
      Date result = null;
      Date date = getDate(dateName);
      if (date != null)
      {
         Calendar dateCal = Calendar.getInstance();
         dateCal.setTime(date);

         Date time = getDate(timeName);
         if (time != null)
         {
            Calendar timeCal = Calendar.getInstance();
            timeCal.setTime(time);
            dateCal.set(Calendar.HOUR_OF_DAY, timeCal.get(Calendar.HOUR_OF_DAY));
            dateCal.set(Calendar.MINUTE, timeCal.get(Calendar.MINUTE));
            dateCal.set(Calendar.SECOND, timeCal.get(Calendar.SECOND));
            dateCal.set(Calendar.MILLISECOND, timeCal.get(Calendar.MILLISECOND));
         }

         result = dateCal.getTime();
      }

      return result;
   }

   /**
    * Retrieve a date field.
    *
    * @param type field type
    * @return Date instance
    */
   public Date getDate(FastTrackField type)
   {
      return (Date) getObject(type);
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
      Object result = m_map.get(type);
      return (result);
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
      return value == null || value.isEmpty() ? null : UUID.fromString(value.substring(1, value.length() - 1));
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
