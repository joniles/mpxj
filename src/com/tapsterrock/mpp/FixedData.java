/*
 * file:       FixedData.java
 * author:     Jon Iles
 * copyright:  (c) Tapster Rock Limited 2002-2003
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

package com.tapsterrock.mpp;

import java.io.InputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;

/**
 * This class is used to represent the "FixedData" file entries that are
 * found in a Microsoft Project MPP file. The name "Fixed Data" appears to
 * refer to the fact that the items held in these blocks have a known maximum
 * size, rather than all of the items being identically sized records.
 *
 * Note that this class has package level access only, and is not intended
 * for use outside of this context.
 */
class FixedData extends MPPComponent
{
   /**
    * This constructor retrieves the data from the input stream. It
    * makes use of the meta data regarding this data block that has
    * already been read in from the MPP file.
    *
    * @param meta meta data about the contents of this fixed data block
    * @param is input stream from which the data is read
    * @throws IOException on file read failure
    */
   public FixedData (FixedMeta meta, InputStream is)
      throws IOException
   {
      int itemCount = meta.getItemCount();

      m_array = new ByteArray[itemCount];

      int index;
      int offset = 0;
      int itemOffset;
      int itemSize;
      int available;

      for (int loop=0; loop < itemCount; loop++)
      {
         available = is.available();
         if (available == 0)
         {
            break;
         }

         itemOffset = meta.getItemOffset (loop);

         if (loop == itemCount-1)
         {
            itemSize = meta.getDataSize() - itemOffset;
         }
         else
         {
            itemSize = meta.getItemOffset(loop+1) - itemOffset;
         }

         if (offset != itemOffset)
         {
            is.skip (itemOffset-offset);
            offset = itemOffset;
         }

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

         m_array[loop] = new ByteArray (readByteArray (is, itemSize));

         offset += itemSize;
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
   public FixedData (int itemSize, InputStream is)
      throws IOException
   {
      int itemCount = is.available() / itemSize;
      m_array = new ByteArray[itemCount];

      for (int loop=0; loop < itemCount; loop++)
      {
         m_array[loop] = new ByteArray (readByteArray (is, itemSize));
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
   public byte[] getByteArrayValue (int index)
   {
      byte[] result = null;

      if (m_array[index] != null)
      {
         result = m_array[index].byteArrayValue();
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
   public int getItemCount ()
   {
      return (m_array.length);
   }

   /**
    * This method dumps the contents of this FixedData block as a String.
    * Note that this facility is provided as a debugging aid.
    *
    * @return formatted contents of this block
    */
   public String toString ()
   {
      StringWriter sw = new StringWriter ();
      PrintWriter pw = new PrintWriter (sw);

      pw.println ("BEGIN FixedData");
      for (int loop=0; loop < m_array.length; loop++)
      {
         pw.println ("   Data at index: " + loop);
         pw.println ("  " + MPPUtility.hexdump (m_array[loop].byteArrayValue(), true));
      }
      pw.println ("END FixedData");

      pw.println ();
      pw.close();
      return (sw.toString());
   }

   /**
    * An array containing all of the items of data held in this block.
    */
   private ByteArray[] m_array;

   /**
    * Constant representing the magic number appearing
    * at the start of the block.
    */
   private static final int MAGIC = 0xFADFADBA;
}
