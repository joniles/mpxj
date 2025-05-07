/*
 * file:       TableDefinition.java
 * author:     Jon Iles
 * copyright:  (c) Packwood Software 2012
 * date:       29/04/2012
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

package org.mpxj.asta;

/**
 * Represents a table read from an Asta PowerProject file.
 */
class TableDefinition
{
   /**
    * Constructor.
    *
    * @param name table name
    * @param columns table column definitions
    */
   public TableDefinition(String name, ColumnDefinition[] columns)
   {
      m_name = name;
      m_columns = columns;
   }

   /**
    * Retrieve the table name.
    *
    * @return table name
    */
   public String getName()
   {
      return m_name;
   }

   /**
    * Retrieve the column definitions.
    *
    * @return column definitions
    */
   public ColumnDefinition[] getColumns()
   {
      return m_columns;
   }

   private final String m_name;
   private final ColumnDefinition[] m_columns;
}
