/*
 * file:       Row.java
 * author:     Jon Iles
 * copyright:  (c) Packwood Software 2011
 * date:       07/04/2011
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

package net.sf.mpxj.asta;

import java.time.LocalDateTime;
import java.util.List;

import net.sf.mpxj.Duration;

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
    * Retrieve an Object representing the value of an attribute.
    *
    * @param name attribute name
    * @return attribute value
    */
   public Object getObject(String name);

   /**
    * Retrieve a string attribute.
    *
    * @param name attribute name
    * @return attribute value
    */
   public String getString(String name);

   /**
    * Retrieve an Integer attribute.
    *
    * @param name attribute name
    * @return attribute value
    */
   public Integer getInteger(String name);

   /**
    * Retrieve a Double attribute.
    *
    * @param name attribute name
    * @return attribute value
    */
   public Double getDouble(String name);

   /**
    * Retrieve a Percentage attribute, rounded to two decimal places.
    *
    * @param name attribute name
    * @return attribute value
    */
   public Double getPercent(String name);

   /**
    * Retrieve a currency attribute.
    *
    * @param name attribute name
    * @return attribute value
    */
   public Double getCurrency(String name);

   /**
    * Retrieve a boolean attribute.
    *
    * @param name attribute name
    * @return attribute value
    */
   public boolean getBoolean(String name);

   /**
    * Retrieve an in attribute.
    *
    * @param name attribute name
    * @return attribute value
    */
   public int getInt(String name);

   /**
    * Retrieve a date attribute.
    *
    * @param name attribute name
    * @return attribute value
    */
   public LocalDateTime getDate(String name);

   /**
    * Retrieve a duration attribute.
    *
    * @param name attribute name
    * @return attribute value
    */
   public Duration getDuration(String name);

   /**
    * Retrieve a duration attribute.
    *
    * @param name attribute name
    * @return attribute value
    */
   public Duration getWork(String name);

   /**
    * Add a child Row to this row.
    *
    * @param row child row
    */
   public void addChild(Row row);

   /**
    * Retrieve a list of child rows.
    *
    * @return list of child rows
    */
   public List<Row> getChildRows();

   /**
    * Merge the columns from another row with this row.
    *
    * @param row row to merge
    * @param prefix prefix used to avoid name collisions
    */
   public void merge(Row row, String prefix);
}
