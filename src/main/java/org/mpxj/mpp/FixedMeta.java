/*
 * file:       FixedMeta.java
 * author:     Jon Iles
 * copyright:  (c) Packwood Software 2002-2003
 * date:       03/01/2003
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

package org.mpxj.mpp;

import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.io.StringWriter;

import org.mpxj.common.ByteArrayHelper;

/**
 * This class is used to represent the "FixedMeta" file entries that are
 * found in a Microsoft Project MPP file. These file entries describe the
 * structure of the "FixedData" blocks with which they are associated.
 * The structure of the Fixed Meta block is not currently fully understood.
 *
 * Note that this class has package level access only, and is not intended
 * for use outside of this context.
 */
final class FixedMeta extends MPPComponent
{
   /**
    * Constructor. Reads the meta data from an input stream. Note that
    * this version of the constructor copes with more MSP inconsistencies.
    * We already know the block size, so we ignore the item count in the
    * block and work it out for ourselves.
    *
    * @param is input stream from which the meta data is read
    * @param itemSize size of each item in the block
    * @throws IOException on file read failure
    */
   FixedMeta(InputStream is, final int itemSize)
      throws IOException
   {
      this(is, (fileSize, itemCount) -> itemSize);
   }

   /**
    * Constructor. Supply an item size provider to allow different strategies to be
    * used to determine the correct item size.
    *
    * @param is input stream from which the meta data is read
    * @param itemSizeProvider item size provider used to calculate the item size
    */
   FixedMeta(InputStream is, FixedMetaItemSizeProvider itemSizeProvider)
      throws IOException
   {

      //
      // The POI file system guarantees that this is accurate
      //
      int fileSize = is.available();

      //
      // First 4 bytes
      //
      int magic = readInt(is);
      if (magic != MAGIC)
      {
         throw new IOException("Bad magic number: " + magic);
      }

      readInt(is);
      m_itemCount = readInt(is);
      readInt(is);

      int itemSize = itemSizeProvider.getItemSize(fileSize, m_itemCount);
      m_adjustedItemCount = (fileSize - HEADER_SIZE) / itemSize;

      m_array = new Object[m_adjustedItemCount];

      for (int loop = 0; loop < m_adjustedItemCount; loop++)
      {
         m_array[loop] = readByteArray(is, itemSize);
      }
   }

   /**
    * Constructor, allowing a selection of possible block sizes to be supplied.
    *
    * @param is input stream
    * @param otherFixedBlock  other fixed block to use as part of the heuristic
    * @param itemSizes list of potential block sizes
    */
   FixedMeta(InputStream is, final FixedData otherFixedBlock, final int... itemSizes)
      throws IOException
   {
      this(is, (fileSize, itemCount) -> {
         int itemSize = itemSizes[0];
         int available = fileSize - HEADER_SIZE;
         int distance = Integer.MIN_VALUE;
         int otherFixedBlockCount = otherFixedBlock.getItemCount();

         for (int testItemSize : itemSizes)
         {
            if (available % testItemSize == 0)
            {
               //
               // If we are testing a size which fits exactly into
               // the block size, and matches the number of items from
               // another block, we can be pretty certain we have the correct
               // size, so bail out at this point
               //
               if (available / testItemSize == otherFixedBlockCount)
               {
                  itemSize = testItemSize;
                  break;
               }

               //
               // Otherwise use a rule-of-thumb to decide on the closest match
               //
               int testDistance = (itemCount * testItemSize) - available;
               if (testDistance <= 0 && testDistance > distance)
               {
                  itemSize = testItemSize;
                  distance = testDistance;
               }
            }
         }

         return itemSize;
      });
   }

   /**
    * This method retrieves the number of items in the FixedData block, as reported in the block header.
    *
    * @return number of items in the fixed data block
    */
   public int getItemCount()
   {
      return (m_itemCount);
   }

   /**
    * This method retrieves the number of items in the FixedData block.
    * Where we don't trust the number of items reported by the block header
    * this value is adjusted based on what we know about the block size
    * and the size of the individual items.
    *
    * @return number of items in the fixed data block
    */
   public int getAdjustedItemCount()
   {
      return (m_adjustedItemCount);
   }

   /**
    * This method retrieves a byte array containing the data at the
    * given index in the block. If no data is found at the given index
    * this method returns null.
    *
    * @param index index of the data item to be retrieved
    * @return byte array containing the requested data
    */
   public byte[] getByteArrayValue(int index)
   {
      byte[] result = null;

      if (index >= 0 && index < m_array.length && m_array[index] != null)
      {
         result = (byte[]) m_array[index];
      }

      return (result);
   }

   /**
    * This method dumps the contents of this FixedMeta block as a String.
    * Note that this facility is provided as a debugging aid.
    *
    * @return formatted contents of this block
    */
   @Override public String toString()
   {
      StringWriter sw = new StringWriter();
      PrintWriter pw = new PrintWriter(sw);

      pw.println("BEGIN: FixedMeta");
      pw.println("   Adjusted Item count: " + m_adjustedItemCount);

      for (int loop = 0; loop < m_adjustedItemCount; loop++)
      {
         pw.println("   Data at index: " + loop);
         pw.println("  " + ByteArrayHelper.hexdump((byte[]) m_array[loop], true));
      }

      pw.println("END: FixedMeta");
      pw.println();

      pw.close();
      return (sw.toString());
   }

   /**
    * Number of items in the data block, as reported in the block header.
    */
   private final int m_itemCount;

   /**
    * Number of items in the data block, adjusted based on block size and item size.
    */
   private final int m_adjustedItemCount;

   /**
    * Unknown data items relating to each entry in the fixed data block.
    */
   private final Object[] m_array;

   /**
    * Constant representing the magic number appearing
    * at the start of the block.
    */
   private static final int MAGIC = 0xFADFADBA;

   /**
    * Header size.
    */
   private static final int HEADER_SIZE = 16;
}
