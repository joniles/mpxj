/*
 * file:       Row.java
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

package org.mpxj.primavera;

import java.time.LocalDateTime;
import java.util.UUID;

import org.mpxj.Duration;

/**
 * This interface represents a row in a database table. It is envisaged that
 * rows could be retrieved from a database either via Jackcess, in which case
 * the row data will be in the form of a Map, or from a JDBC data source,
 * in which case the row will be in the form of a Result set. Classes that
 * implement this interface will wrap one of these types to provide a consistent
 * interface to MPXJ.
 */
interface Row
{
   /**
    * Retrieve a string attribute.
    *
    * @param name attribute name
    * @return attribute value
    */
   String getString(String name);

   /**
    * Retrieve an Integer attribute.
    *
    * @param name attribute name
    * @return attribute value
    */
   Integer getInteger(String name);

   /**
    * Retrieve a Double attribute.
    *
    * @param name attribute name
    * @return attribute value
    */
   Double getDouble(String name);

   /**
    * Retrieve a boolean attribute.
    *
    * @param name attribute name
    * @return attribute value
    */
   boolean getBoolean(String name);

   /**
    * Retrieve a boolean attribute.
    *
    * @param name attribute name
    * @return attribute value
    */
   Boolean getBooleanObject(String name);

   /**
    * Retrieve an in attribute.
    *
    * @param name attribute name
    * @return attribute value
    */
   int getInt(String name);

   /**
    * Retrieve a date attribute.
    *
    * @param name attribute name
    * @return attribute value
    */
   LocalDateTime getDate(String name);

   /**
    * Retrieve a duration attribute.
    *
    * @param name attribute name
    * @return attribute value
    */
   Duration getDuration(String name);

   /**
    * Retrieve a UUID attribute.
    *
    * @param name attribute name
    * @return attribute value
    */
   UUID getUUID(String name);
}
