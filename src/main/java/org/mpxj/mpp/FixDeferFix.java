/*
 * file:       FixDeferFix.java
 * author:     Jon Iles
 * copyright:  (c) Packwood Software 2002-2003
 * date:       31/03/2003
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
import java.util.TreeSet;

import org.mpxj.common.ByteArrayHelper;
import org.mpxj.common.InputStreamHelper;

/**
 * This class represents the a block of variable length data items that appears
 * in the Microsoft Project 98 file format.
 */
final class FixDeferFix extends MPPComponent
{
   /**
    * Extract the variable size data items from the input stream.
    *
    * @param is Input stream
    * @throws IOException Thrown on read errors
    */
   FixDeferFix(InputStream is)
      throws IOException
   {
      m_data = InputStreamHelper.readAvailable(is);
   }

   /**
    * Retrieve a byte array of containing the data starting at the supplied
    * offset in the FixDeferFix file. Note that this method will return null
    * if the requested data is not found for some reason.
    *
    * @param offset Offset into the file
    * @return Byte array containing the requested data
    */
   public byte[] getByteArray(int offset)
   {
      byte[] result = null;

      if (offset > 0 && offset < m_data.length)
      {
         int nextBlockOffset = ByteArrayHelper.getInt(m_data, offset);
         offset += 4;

         int itemSize = ByteArrayHelper.getInt(m_data, offset);
         offset += 4;

         if (itemSize > 0 && itemSize < m_data.length)
         {
            int blockRemainingSize = 28;

            if (nextBlockOffset != -1 || itemSize <= blockRemainingSize)
            {
               int itemRemainingSize = itemSize;
               result = new byte[itemSize];
               int resultOffset = 0;

               while (nextBlockOffset != -1)
               {
                  MPPUtility.getByteArray(m_data, offset, blockRemainingSize, result, resultOffset);
                  resultOffset += blockRemainingSize;
                  offset += blockRemainingSize;
                  itemRemainingSize -= blockRemainingSize;

                  if (offset != nextBlockOffset)
                  {
                     offset = nextBlockOffset;
                  }

                  nextBlockOffset = ByteArrayHelper.getInt(m_data, offset);
                  offset += 4;
                  blockRemainingSize = 32;
               }

               MPPUtility.getByteArray(m_data, offset, itemRemainingSize, result, resultOffset);
            }
         }
      }

      return (result);
   }

   /**
    * This method retrieves the string at the specified offset.
    *
    * @param offset Offset into var data
    * @return String value
    */
   public String getString(int offset)
   {
      String result = null;
      byte[] data = getByteArray(offset);
      if (data != null)
      {
         result = new String(data);
      }

      return (result);
   }

   /**
    * This method retrieves the string at the specified offset.
    *
    * @param offset Offset into var data
    * @return String value
    */
   public String getUnicodeString(int offset)
   {
      String result = null;
      byte[] data = getByteArray(offset);
      if (data != null)
      {
         result = MPPUtility.getUnicodeString(data, 0);
      }

      return (result);
   }

   /**
    * This method dumps the contents of this FixDeferFix block as a String.
    * Note that this facility is provided as a debugging aid.
    *
    * @return formatted contents of this block
    */
   @Override public String toString()
   {
      StringWriter sw = new StringWriter();
      PrintWriter pw = new PrintWriter(sw);

      pw.println("BEGIN FixDeferFix");

      //
      // Calculate the block size
      //
      int available = m_data.length;

      //
      // Skip 4 byte header
      //
      int fileOffset = 4;

      //
      // Read data
      //
      int itemSize;
      int itemRemainingSize;
      int blockRemainingSize;
      int skip;
      int nextBlockOffset;
      byte[] buffer;
      int bufferOffset;
      TreeSet<Integer> skipped = new TreeSet<>();
      TreeSet<Integer> read = new TreeSet<>();
      int startOffset;

      while (fileOffset < available || !skipped.isEmpty())
      {
         Integer temp;

         if (fileOffset >= available)
         {
            temp = skipped.first();
            skipped.remove(temp);
            fileOffset = temp.intValue();
         }

         temp = Integer.valueOf(fileOffset);
         if (!read.add(temp))
         {
            fileOffset = available;
            continue;
         }

         startOffset = fileOffset;

         nextBlockOffset = ByteArrayHelper.getInt(m_data, fileOffset);
         fileOffset += 4;

         itemSize = ByteArrayHelper.getInt(m_data, fileOffset);
         fileOffset += 4;

         blockRemainingSize = 28;

         if (nextBlockOffset == -1 && itemSize > blockRemainingSize)
         {
            fileOffset += blockRemainingSize;
            continue;
         }

         itemRemainingSize = itemSize;
         buffer = new byte[itemSize];
         bufferOffset = 0;

         while (nextBlockOffset != -1)
         {
            MPPUtility.getByteArray(m_data, fileOffset, blockRemainingSize, buffer, bufferOffset);
            bufferOffset += blockRemainingSize;
            fileOffset += blockRemainingSize;
            itemRemainingSize -= blockRemainingSize;

            if (fileOffset != nextBlockOffset)
            {
               skipped.add(Integer.valueOf(fileOffset));
               fileOffset = nextBlockOffset;
            }

            temp = Integer.valueOf(fileOffset);
            if (!read.add(temp))
            {
               fileOffset = available;
               continue;
            }

            nextBlockOffset = ByteArrayHelper.getInt(m_data, fileOffset);
            fileOffset += 4;
            blockRemainingSize = 32;
         }

         MPPUtility.getByteArray(m_data, fileOffset, itemRemainingSize, buffer, bufferOffset);
         fileOffset += itemRemainingSize;

         if (itemRemainingSize < blockRemainingSize)
         {
            skip = blockRemainingSize - itemRemainingSize;
            fileOffset += skip;
         }

         pw.println("   Data: offset: " + startOffset + " size: " + buffer.length);
         pw.println("  " + ByteArrayHelper.hexdump(buffer, true));
      }

      pw.println("END FixDeferFix");
      pw.println();
      pw.close();

      return (sw.toString());
   }

   private final byte[] m_data;
}
