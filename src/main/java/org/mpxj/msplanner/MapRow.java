/*
 * file:       MapRow.java
 * author:     Jon Iles
 * date:       2026-01-11
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

package org.mpxj.msplanner;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.UUID;

import org.mpxj.DataType;
import org.mpxj.Duration;
import org.mpxj.RelationType;
import org.mpxj.TimeUnit;
import org.mpxj.common.BooleanHelper;
import org.mpxj.common.LocalDateTimeHelper;
import org.mpxj.common.NumberHelper;

/**
 * Represents data deserialized from JSON. Provides method to return correctly typed values.
 */
class MapRow extends LinkedHashMap<String, Object>
{
   /**
    * Retrieve a deserialized list as a list of MapRow instances.
    *
    * @param key map key
    * @return list of MapRow instances
    */
   public List<MapRow> getList(String key)
   {
      @SuppressWarnings("unchecked")
      List<MapRow> row = (List<MapRow>) get(key);
      return row == null ? Collections.emptyList() : row;
   }

   /**
    * Retrieve a string value.
    *
    * @param key map key
    * @return string value
    */
   public String getString(String key)
   {
      Object value = get(key);
      return value == null ? null : String.valueOf(value);
   }

   /**
    * Retrieve a UUID value.
    *
    * @param key map key
    * @return UUID value
    */
   public UUID getUUID(String key)
   {
      return (UUID) getObject(key, DataType.GUID);
   }

   public LocalDateTime getDate(String key)
   {
      return (LocalDateTime) getObject(key, DataType.DATE);
   }

   /**
    * Retrieve an Integer value.
    *
    * @param key map key
    * @return Integer value
    */
   public Integer getInteger(String key)
   {
      return (Integer) getObject(key, DataType.INTEGER);
   }

   public Double getDouble(String key)
   {
      return (Double) getObject(key, DataType.NUMERIC);
   }

   public double getDoubleValue(String key)
   {
      return NumberHelper.getDouble(getDouble(key));
   }

   /**
    * Retrieve an int value.
    *
    * @param key map key
    * @return int value
    */
   public int getInt(String key)
   {
      return NumberHelper.getInt(getInteger(key));
   }

   /**
    * Retrieve a boolean value.
    *
    * @param key map key
    * @return boolean value
    */
   public boolean getBool(String key)
   {
      return BooleanHelper.getBoolean((Boolean) get(key));
   }

   public RelationType getRelationType(String key)
   {
      Integer value = getInteger(key);
      if (value == null)
      {
         return null;
      }

      int index = value.intValue();
      if (index < 0 || index >= RELATION_TYPE.length)
      {
         return null;
      }

      return RELATION_TYPE[index];
   }

   /**
    * Retrieve a value expressed as a specific type.
    *
    * @param key map key
    * @param type required type
    * @return value as the required type
    */
   public Object getObject(String key, DataType type)
   {
      Object value = get(key);
      if (value == null)
      {
         return null;
      }

      switch (type)
      {
         case STRING:
         {
            return String.valueOf(value);
         }

         case DATE:
         {
            return getDateFromString(String.valueOf(value));
         }

         case GUID:
         {
            return getUuidFromString(String.valueOf(value));
         }

         case DURATION:
         {
            double time = Double.parseDouble(String.valueOf(value));
            return Duration.getInstance(time, TimeUnit.DAYS);
         }

         case WORK:
         case DELAY:
         {
            double time = Double.parseDouble(String.valueOf(value));
            return Duration.getInstance(time, TimeUnit.HOURS);
         }

         case NUMERIC:
         {
            if (value instanceof String)
            {
               return Double.valueOf((String) value);
            }
            return value;
         }

         case PERCENTAGE:
         {
            return Double.valueOf(Double.parseDouble(String.valueOf(value)) * 100.0);
         }

         case INTEGER:
         case BOOLEAN:
         case SHORT:
         {
            return value;
         }

         default:
         {
            throw new MsPlannerException(type + " not handled");
         }
      }
   }

   /**
    * Retrieve a LocalDateTime instance from a string representation.
    *
    * @param value string value
    * @return LocalDateTime instance
    */
   private LocalDateTime getDateFromString(String value)
   {
      if (value.equals("2000-01-01T00:00:00Z"))
      {
         return null;
      }

      return LocalDateTimeHelper.parseBest(DATE_TIME_FORMAT, value);
   }

   /**
    * Retrieve a UUID from a string representation.
    *
    * @param value string value
    * @return UUID instance
    */
   private UUID getUuidFromString(String value)
   {
      return UUID.fromString(value);
   }

   private static final DateTimeFormatter DATE_TIME_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss'Z'");

   private static final RelationType[] RELATION_TYPE = new RelationType[]
   {
      RelationType.FINISH_FINISH,
      RelationType.FINISH_START,
      RelationType.START_FINISH,
      RelationType.START_START
   };
}
