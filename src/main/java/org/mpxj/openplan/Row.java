/*
 * file:       Row.java
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
import java.util.UUID;

import org.mpxj.Duration;
import org.mpxj.ResourceType;

/**
 * Represents a row read from a table in a BK3 file.
 */
interface Row
{
   /**
    * Retrieve a string.
    *
    * @param name column name
    * @return string value
    */
   String getString(String name);

   /**
    * Retrieve a date.
    *
    * @param name column name
    * @return date value
    */
   LocalDateTime getDate(String name);

   /**
    * Retrieve a time.
    *
    * @param name column name
    * @return time value
    */
   LocalTime getTime(String name);

   /**
    * Retrieve a double.
    *
    * @param name column name
    * @return double value
    */
   Double getDouble(String name);

   /**
    * Retrieve an integer.
    *
    * @param name column name
    * @return integer value
    */
   Integer getInteger(String name);

   /**
    * Retrieve a Boolean.
    *
    * @param name column name
    * @return Boolean value
    */
   Boolean getBoolean(String name);

   /**
    * Retrieve a UUID.
    *
    * @param name column name
    * @return UUID value
    */
   UUID getUuid(String name);

   /**
    * Retrieve a duration.
    *
    * @param name column name
    * @return duration value
    */
   Duration getDuration(String name);

   /**
    * Retrieve a resource type.
    *
    * @param name column name
    * @return resource type value
    */
   ResourceType getResourceType(String name);
}
