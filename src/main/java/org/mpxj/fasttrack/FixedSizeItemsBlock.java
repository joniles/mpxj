/*
 * file:       FixedSizeItemsBlock.java
 * author:     Jon Iles
 * copyright:  (c) Packwood Software 2017
 * date:       14/03/2016
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

package org.mpxj.fasttrack;

/**
 * Reads data from a block containing fixed size items of data.
 */
class FixedSizeItemsBlock
{
   /**
    * Read data, return the current instance.
    *
    * @param buffer buffer containing data
    * @param offset offset into buffer
    * @return current FixedSizeItemsBlock instance
    */
   public FixedSizeItemsBlock read(byte[] buffer, int offset)
   {
      // Offset to data
      offset += 2;

      int numberOfItems = FastTrackUtility.getInt(buffer, offset);
      offset += 4;

      FastTrackUtility.validateSize(numberOfItems);

      int itemLength = FastTrackUtility.getShort(buffer, offset);
      offset += 2;

      FastTrackUtility.validateSize(itemLength);

      // Offset to end
      offset += 4;

      m_data = new byte[numberOfItems][];
      for (int index = 0; index < m_data.length; index++)
      {
         byte[] item = new byte[itemLength];
         m_data[index] = item;

         FastTrackUtility.validateOffset(buffer, offset + itemLength);

         System.arraycopy(buffer, offset, item, 0, itemLength);
         offset += itemLength;
      }

      m_offset = offset;

      return this;
   }

   /**
    * Retrieve the data read from this block.
    *
    * @return data
    */
   public byte[][] getData()
   {
      return m_data;
   }

   /**
    * Retrieve the offset into the block after the data has been read.
    *
    * @return offset
    */
   public int getOffset()
   {
      return m_offset;
   }

   private byte[][] m_data;
   private int m_offset;
}
