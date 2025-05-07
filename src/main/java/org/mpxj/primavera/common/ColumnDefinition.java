/*
 * file:       ColumnDefinition.java
 * author:     Jon Iles
 * copyright:  (c) Packwood Software 2018
 * date:       01/03/2018
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

package org.mpxj.primavera.common;

/**
 * Classes which implement this interface define how columns
 * of a specific type can be read from the P3 database.
 */
public interface ColumnDefinition
{
   /**
    * Retrieve the name of the column.
    *
    * @return column name
    */
   String getName();

   /**
    * Read the column data.
    *
    * @param offset current offset into the table data block
    * @param data table data block
    * @return column value
    */
   Object read(int offset, byte[] data);
}
