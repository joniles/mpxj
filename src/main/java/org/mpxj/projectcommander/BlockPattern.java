/*
 * file:       BlockPattern.java
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

import java.util.Set;

import org.mpxj.common.ByteArrayHelper;

/**
 * Represents a two byte pattern used to identify the start of a block.
 */
final class BlockPattern
{
   /**
    * Constructor.
    *
    * @param name block name
    * @param validator optional validator
    * @param pattern byte pattern
    */
   public BlockPattern(String name, BlockPatternValidator validator, byte... pattern)
   {
      m_name = name;
      m_validator = validator;
      m_pattern = pattern;
   }

   /**
    * Constructor.
    *
    * @param name block name
    * @param pattern byte pattern
    */
   public BlockPattern(String name, byte... pattern)
   {
      this(name, null, pattern);
   }

   /**
    * Constructor.
    *
    * @param name block name
    * @param data buffer containing byte pattern
    * @param offset offset in buffer to byte pattern
    */
   public BlockPattern(String name, byte[] data, int offset)
   {
      this(name, data[offset], data[offset + 1]);
   }

   /**
    * Constructor.
    *
    * @param name block name
    * @param validator optional validator
    * @param data buffer containing byte pattern
    * @param offset offset in buffer to byte pattern
    */
   public BlockPattern(String name, BlockPatternValidator validator, byte[] data, int offset)
   {
      this(name, validator, data[offset], data[offset + 1]);
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
    * Retrieve the block pattern.
    *
    * @return block pattern
    */
   public byte[] getPattern()
   {
      return m_pattern;
   }

   /**
    * Invoke the optional validator.
    *
    * @param matchedPatternNames set of block names read so far
    *
    * @return true if the block pattern is valid in this location
    */
   public boolean getValid(Set<String> matchedPatternNames)
   {
      return m_validator == null || m_validator.valid(matchedPatternNames);
   }

   @Override public String toString()
   {
      return m_name + ": " + ByteArrayHelper.hexdump(m_pattern, false);
   }

   private final String m_name;
   private final byte[] m_pattern;
   private final BlockPatternValidator m_validator;
}
