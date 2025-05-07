/*
 * file:       SynchroTable.java
 * author:     Jon Iles
 * copyright:  (c) Packwood Software 2018
 * date:       2018-10-11
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

package org.mpxj.synchro;

/**
 * Represents the definition of a Synchro table.
 */
class SynchroTable
{
   /**
    * Constructor.
    *
    * @param name table name
    * @param offset offset to start of data
    */
   public SynchroTable(String name, int offset)
   {
      m_name = name;
      m_offset = offset;
   }

   /**
    * Retrieve the table offset.
    *
    * @return table offset
    */
   public int getOffset()
   {
      return m_offset;
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
    * Retrieve the table length.
    *
    * @return table length
    */
   public int getLength()
   {
      return m_length;
   }

   /**
    * Set the table length.
    *
    * @param length table length
    */
   public void setLength(int length)
   {
      m_length = length;
   }

   @Override public String toString()
   {
      return "[SynchroTable\t name=" + m_name + "\toffset=" + m_offset + "\tlength=" + m_length + "]";
   }

   private final String m_name;
   private final int m_offset;
   private int m_length = -1;
}
