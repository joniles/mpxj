/*
 * file:       BlockReference.java
 * author:     Jon Iles
 * copyright:  (c) Packwood Software 2020
 * date:       24/05/2020
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

package org.mpxj.projectcommander;

/**
 * Used to record the location of a block start in the file,
 * and which pattern the block start matched.
 */
final class BlockReference
{
   /**
    * Constructor.
    *
    * @param pattern pattern sued to match block start
    * @param index offset into the file which the block match was found
    */
   public BlockReference(BlockPattern pattern, int index)
   {
      m_pattern = pattern;
      m_index = index;
   }

   /**
    * Retrieve the pattern used to match this block.
    *
    * @return block pattern
    */
   public BlockPattern getPattern()
   {
      return m_pattern;
   }

   /**
    * Retrieve the offset into the file where this block match was found.
    *
    * @return offset into file
    */
   public int getIndex()
   {
      return m_index;
   }

   @Override public String toString()
   {
      return "[BlockReference name=" + m_pattern.getName() + " index=" + m_index;
   }

   private final BlockPattern m_pattern;
   private final int m_index;
}
