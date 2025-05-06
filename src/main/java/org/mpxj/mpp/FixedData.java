/*
 * file:       FixedData.java
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
import org.mpxj.common.InputStreamHelper;

/**
 * This class is used to represent the "FixedData" file entries that are
 * found in a Microsoft Project MPP file. The name "Fixed Data" appears to
 * refer to the fact that the items held in these blocks have a known maximum
 * size, rather than all of the items being identically sized records.
 *
 * Note that this class has package level access only, and is not intended
 * for use outside of this context.
 */
final class FixedData extends MPPComponent
{
   /**
    * This constructor retrieves the data from the input stream. It
    * makes use of the meta data regarding this data block that has
    * already been read in from the MPP file.
    *
    * Note that we actually read in the entire data block in one go.
    * This is due to the fact that MS Project sometimes describes data
    * using offsets that are out of sequence, and items that may overlap.
    * Ideally this data would be read directly from the input stream, but
    * this was problematic, so this less than ideal solution has been
    * adopted.
    *
    * @param meta meta data about the contents of this fixed data block
    * @param is input stream from which the data is read
    * @throws IOException on file read failure
    */
   FixedData(FixedMeta meta, InputStream is)
      throws IOException
   {
      this(meta, is, 0);
   }

   /**
    * This version of the above constructor allows us to limit the
    * size of blocks we copy where we have an idea of the maximum expected
    * block size. This prevents us from reading ridiculously large amounts
    * of unnecessary data, causing OutOfMemory exceptions.
    *
    * @param meta meta data about the contents of this fixed data block
    * @param is input stream from which the data is read
    * @param maxExpectedSize maximum expected block size
    * @throws IOException on file read failure
    */
   FixedData(FixedMeta meta, InputStream is, int maxExpectedSize)
      throws IOException
   {
      this(meta, is, maxExpectedSize, 0);
   }

   /**
    * This version of the above constructor allows us to limit the
    * size of blocks we copy where we have an idea of the maximum expected
    * block size. This prevents us from reading ridiculously large amounts
    * of unnecessary data, causing OutOfMemory exceptions.
    *
    * This constructor will also use the given minimum size in the case that the
    * meta data block reports a size of 0
    *
    * @param meta meta data about the contents of this fixed data block
    * @param maxExpectedSize maximum expected block size
    * @param minSize minimum size that will be read if size of block is reported as 0.
    * @param is input stream from which the data is read
    * @throws IOException on file read failure
    */
   FixedData(FixedMeta meta, InputStream is, int maxExpectedSize, int minSize)
      throws IOException
   {
      byte[] buffer = InputStreamHelper.readAvailable(is);

      int itemCount = meta.getAdjustedItemCount();
      m_array = new Object[itemCount];
      m_offset = new int[itemCount];

      int available;

      for (int loop = 0; loop < itemCount; loop++)
      {
         byte[] metaData = meta.getByteArrayValue(loop);
         int itemOffset = ByteArrayHelper.getInt(metaData, 4);

         if (itemOffset < 0 || itemOffset > buffer.length)
         {
            continue;
         }

         int itemSize;
         if (loop + 1 == itemCount)
         {
            itemSize = buffer.length - itemOffset;
         }
         else
         {
            byte[] nextMetaData = meta.getByteArrayValue(loop + 1);
            int nextItemOffset = ByteArrayHelper.getInt(nextMetaData, 4);
            itemSize = nextItemOffset - itemOffset;
         }

         if (itemSize == 0)
         {
            itemSize = minSize;
         }

         available = buffer.length - itemOffset;

         if (itemSize < 0 || itemSize > available)
         {
            if (maxExpectedSize == 0)
            {
               itemSize = available;
            }
            else
            {
               itemSize = Math.min(maxExpectedSize, available);
            }
         }

         if (maxExpectedSize != 0 && itemSize > maxExpectedSize)
         {
            itemSize = maxExpectedSize;
         }

         if (itemSize > 0)
         {
            m_array[loop] = MPPUtility.cloneSubArray(buffer, itemOffset, itemSize);
            m_offset[loop] = itemOffset;
         }
      }
   }

   /**
    * This constructor does the same job as the one above, but assumes that
    * the item size reported in the meta information is wrong, and
    * instead uses the supplied item size.
    *
    * @param meta meta data about the contents of this fixed data block
    * @param itemSize expected item size
    * @param is input stream from which the data is read
    */
   FixedData(FixedMeta meta, int itemSize, InputStream is)
      throws IOException
   {
      byte[] buffer = InputStreamHelper.readAvailable(is);

      int itemCount = meta.getAdjustedItemCount();
      m_array = new Object[itemCount];
      m_offset = new int[itemCount];

      byte[] metaData;
      int itemOffset;
      int available;

      for (int loop = 0; loop < itemCount; loop++)
      {
         metaData = meta.getByteArrayValue(loop);
         itemOffset = ByteArrayHelper.getInt(metaData, 4);

         if (itemOffset < 0 || itemOffset > buffer.length)
         {
            continue;
         }

         available = buffer.length - itemOffset;

         if (itemSize < 0)
         {
            itemSize = available;
         }
         else
         {
            if (itemSize > available)
            {
               itemSize = available;
            }
         }

         m_array[loop] = MPPUtility.cloneSubArray(buffer, itemOffset, itemSize);
         m_offset[loop] = itemOffset;
      }
   }

   /**
    * This constructor is provided to allow the contents of a fixed data
    * block to be read when the size of the items in the data block is
    * fixed and known in advance. This is used in one particular instance
    * where the contents of the meta data block do not appear to be
    * consistent.
    *
    * @param itemSize the size of the data items in the block
    * @param is input stream from which the data is read
    * @throws IOException on file read failure
    */
   FixedData(int itemSize, InputStream is)
      throws IOException
   {
      this(itemSize, is, false);
   }

   /**
    * This constructor is provided to allow the contents of a fixed data
    * block to be read when the size of the items in the data block is
    * fixed and known in advance. This is used in one particular instance
    * where the contents of the meta data block do not appear to be
    * consistent.
    *
    * @param itemSize the size of the data items in the block
    * @param is input stream from which the data is read
    * @param readRemainderBlock read the final block even if it is not full size
    * @throws IOException on file read failure
    */
   FixedData(int itemSize, InputStream is, boolean readRemainderBlock)
      throws IOException
   {
      int offset = 0;
      int itemCount = is.available() / itemSize;
      if (readRemainderBlock && is.available() % itemSize != 0)
      {
         ++itemCount;
      }

      m_array = new Object[itemCount];
      m_offset = new int[itemCount];

      for (int loop = 0; loop < itemCount; loop++)
      {
         m_offset[loop] = offset;

         int currentItemSize = itemSize;
         if (readRemainderBlock && is.available() < itemSize)
         {
            currentItemSize = is.available();
         }
         m_array[loop] = readByteArray(is, currentItemSize);
         offset += itemSize;
      }
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
    * Accessor method used to retrieve the number of items held in
    * this fixed data block. Note that this item count is made without
    * reference to the meta data associated with this block.
    *
    * @return number of items in the block
    */
   public int getItemCount()
   {
      return (m_array.length);
   }

   /**
    * Returns a flag indicating if the supplied offset is valid for
    * the data in this fixed data block.
    *
    * @param offset offset value
    * @return boolean flag
    */
   public boolean isValidOffset(Integer offset)
   {
      return (offset != null && isValidOffset(offset.intValue()));
   }

   /**
    * Returns a flag indicating if the supplied offset is valid for
    * the data in this fixed data block.
    *
    * @param offset offset value
    * @return boolean flag
    */
   public boolean isValidOffset(int offset)
   {
      return (offset >= 0 && offset < m_array.length);
   }

   /**
    * This method converts an offset value into an array index, which in
    * turn allows the data present in the fixed block to be retrieved. Note
    * that if the requested offset is not found, then this method returns -1.
    *
    * @param offset Offset of the data in the fixed block
    * @return Index of data item within the fixed data block
    */
   public int getIndexFromOffset(int offset)
   {
      int result = -1;

      for (int loop = 0; loop < m_offset.length; loop++)
      {
         if (m_offset[loop] == offset)
         {
            result = loop;
            break;
         }
      }

      return (result);
   }

   /**
    * This method dumps the contents of this FixedData block as a String.
    * Note that this facility is provided as a debugging aid.
    *
    * @return formatted contents of this block
    */
   @Override public String toString()
   {
      StringWriter sw = new StringWriter();
      PrintWriter pw = new PrintWriter(sw);

      pw.println("BEGIN FixedData");
      for (int loop = 0; loop < m_array.length; loop++)
      {
         pw.println("   Data at index: " + loop + " offset: " + m_offset[loop]);
         pw.println("  " + ByteArrayHelper.hexdump((byte[]) m_array[loop], true));
      }
      pw.println("END FixedData");

      pw.println();
      pw.close();
      return (sw.toString());
   }

   /**
    * An array containing all of the items of data held in this block.
    */
   private final Object[] m_array;

   /**
    * Array containing offset values for each item in the array.
    */
   private final int[] m_offset;

}
