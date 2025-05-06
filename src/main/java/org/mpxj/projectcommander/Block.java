/*
 * file:       Block.java
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

import java.util.ArrayList;
import java.util.List;

/**
 * Named data block, optionally with child blocks.
 */
final class Block
{
   /**
    * Constructor.
    *
    * @param name block name
    * @param data block data
    */
   public Block(String name, byte[] data)
   {
      m_name = name;
      m_data = data;
   }

   /**
    * Retrieve the block name.
    *
    * @return block name
    */
   public String getName()
   {
      return m_name;
   }

   /**
    * Retrieve the block data.
    *
    * @return block data
    */
   public byte[] getData()
   {
      return m_data;
   }

   /**
    * Retrieve any child blocks.
    *
    * @return list of child blocks
    */
   public List<Block> getChildBlocks()
   {
      return m_childBlocks;
   }

   /**
    * Dump block hierarchy to aid debugging.
    *
    * @param prefix indentation prefix
    */
   public void dumpBlock(String prefix)
   {
      System.out.println(prefix + getName());
      prefix += " ";
      for (Block childBlock : m_childBlocks)
      {
         childBlock.dumpBlock(prefix);
      }
   }

   private final String m_name;
   private final byte[] m_data;
   private final List<Block> m_childBlocks = new ArrayList<>();
}
