/*
 * file:       FixedMeta.java
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
    * @param is input stream from whic the meta data is read
    * @param itemSize sie of each item in the block
    * @throws IOException on file read failure
    */
   FixedMeta (InputStream is, int itemSize)
      throws IOException
   {
      //
      // The POI file system guarantees that this is accurate
      //
      int fileSize = is.available();

      //
      // First 8 bytes
      //
      if (readInt (is) != MAGIC)
      {
         throw new IOException ("Bad magic number");
      }

      int unknown1 = readInt (is);
      m_itemCount = readInt (is);
      int dataSize = readInt (is);

      m_itemCount = (fileSize - 16) / itemSize;

      m_array = new ByteArray[m_itemCount];

      for (int loop=0; loop < m_itemCount; loop++)
      {
         m_array[loop] = new ByteArray (readByteArray (is, itemSize));         
      }
   }

   /**
    * This method retrieves the number of items in the FixedData block.
    *
    * @return number of items in the fixed data block
    */
   public int getItemCount ()
   {
      return (m_itemCount);
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
    * This method dumps the contents of this FixedMeta block as a String.
    * Note that this facility is provided as a debugging aid.
    *
    * @return formatted contents of this block
    */
   public String toString ()
   {
      StringWriter sw = new StringWriter ();
      PrintWriter pw = new PrintWriter (sw);

      pw.println ("BEGIN: FixedMeta");
      pw.println ("   Item count: " + m_itemCount);

      for (int loop=0; loop < m_itemCount; loop++)
      {
         pw.println ("   Data at index: " + loop);
         pw.println ("  " + MPPUtility.hexdump (m_array[loop].byteArrayValue(), true));         
      }

      pw.println ("END: FixedMeta");
      pw.println ();

      pw.close ();
      return (sw.toString ());
   }

   /**
    * Number of items in the data block
    */
   private int m_itemCount;

   /**
    * Unknown data items relating to each entry in the fixed data block.
    */
   private ByteArray[] m_array;

   /**
    * Constant representing the magic number appearing
    * at the start of the block.
    */
   private static final int MAGIC = 0xFADFADBA;
}
