/*
 * file:       ColumnDefinition.java
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
 * Represents the definition of a column read from an Asta file.
 */
class ColumnDefinition
{
   /**
    * Constructor.
    *
    * @param name column name
    * @param type column data type
    */
   public ColumnDefinition(String name, int type)
   {
      m_name = name;
      m_type = type;
   }

   /**
    * Retrieves the column name.
    *
    * @return column name
    */
   public String getName()
   {
      return m_name;
   }

   /**
    * Retrieves the column ata type.
    *
    * @return data type
    */
   public int getType()
   {
      return m_type;
   }

   private final String m_name;
   private final int m_type;
}
