/*
 * file:       MapRow.java
 * author:     Jon Iles
 * date:       2024-02-27
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

package org.mpxj.openplan;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

import org.mpxj.Duration;
import org.mpxj.ResourceType;

/**
 * Implementation of the Row interface using a Map for storage.
 */
class MapRow implements Row
{
   public MapRow(Map<String, Object> map)
   {
      m_map = map;
   }

   @Override public String toString()
   {
      return "[MapRow\n" + m_map.entrySet().stream().sorted(Map.Entry.comparingByKey()).map(e -> "\t" + e.getKey() + "\t" + e.getValue() + " (" + e.getValue().getClass().getSimpleName() + ")").collect(Collectors.joining("\n")) + "\n]";
   }

   // TODO change!
   public final Map<String, Object> m_map;

   @Override public String getString(String name)
   {
      return getValue(name, String.class);
   }

   @Override public LocalDateTime getDate(String name)
   {
      return getValue(name, LocalDateTime.class);
   }

   @Override public LocalTime getTime(String name)
   {
      return getValue(name, LocalTime.class);
   }

   @Override public Double getDouble(String name)
   {
      return getValue(name,  Double.class);
   }

   @Override public Integer getInteger(String name)
   {
      return getValue(name, Integer.class);
   }

   @Override public Boolean getBoolean(String name)
   {
      return getValue(name, Boolean.class);
   }

   @Override public UUID getUuid(String name)
   {
      return getValue(name, UUID.class);
   }

   @Override public Duration getDuration(String name)
   {
      return getValue(name, Duration.class);
   }

   @Override public ResourceType getResourceType(String name)
   {
      return getValue(name, ResourceType.class);
   }

   private <T> T getValue(String name, Class<T> c)
   {
      Object value = m_map.get(name);
      if (value == null)
      {
         return null;
      }

      if (c.isAssignableFrom(value.getClass()))
      {
         return (T)value;
      }

      throw new RuntimeException("Unexpected type for column `" + name + "` with value `" + value+ "`");
   }
}
