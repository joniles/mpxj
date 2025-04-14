/*
 * file:       WriteOptions.java
 * author:     Jon Iles
 * date:       2024-11-26
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

package org.mpxj.explorer;

/**
 * Represents options used when writing project files.
 */
class WriteOptions
{
   /**
    * Toggle the state of the write timephased data flag.
    */
   public void toggleWriteTimephasedData()
   {
      m_writeTimephasedData = !m_writeTimephasedData;
   }

   /**
    * Toggle the state of the split timephased data as days flag.
    */
   public void toggleSplitTimephasedDataAsDays()
   {
      m_splitTimephaseDataAsDays = !m_splitTimephaseDataAsDays;
   }

   /**
    * Retrieve the write timephased data flag.
    *
    * @return write timephased data flag
    */
   public boolean getWriteTimephasedData()
   {
      return m_writeTimephasedData;
   }

   /**
    * Retrieve the split timephased data as days flag.
    *
    * @return split timephased data as days flag
    */
   public boolean getSplitTimephasedDataAsDays()
   {
      return m_splitTimephaseDataAsDays;
   }

   private boolean m_writeTimephasedData;
   private boolean m_splitTimephaseDataAsDays;
}
