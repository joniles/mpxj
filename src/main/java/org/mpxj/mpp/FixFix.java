/*
 * file:       FixFix.java
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

import org.mpxj.common.ByteArrayHelper;

/**
 * This class represents the a block of fixed length data items that appears
 * in the Microsoft Project 98 file format.
 */
final class FixFix extends MPPComponent
{
   /**
    * Constructor. Extract fixed size data items from the input stream.
    * Note that for MSP98 we normally expect the data blocks to be a standard
    * size, as supplied in the itemSize parameter. However we have found
    * example files where the block size is larger, hence the requirement for
    * the code to check for a remainder when the overall size is divided by
    * the block size. If the remainder is non-zero, we iteratively increase the
    * itemSize until we find one that fits the overall available data size.
    *
    * @param itemSize Size of the items held in this block
    * @param is Input stream
    * @throws IOException Thrown when reading from the stream fails
    */
   FixFix(int itemSize, InputStream is)
      throws IOException
   {
      m_size = is.available();
      m_diff = m_size % itemSize;
      int itemCount = m_size / itemSize;
      m_array = new Object[itemCount];

      for (int loop = 0; loop < itemCount; loop++)
      {
         m_array[loop] = readByteArray(is, itemSize);
      }
   }

   /**
    * This method is used to retrieve the remainder obtained when the
    * available data size is divided by the expected item size. If this
    * value is non-zero, it suggests that the available data contains
    * items of a different size.
    *
    * @return remainder
    */
   public int getDiff()
   {
      return (m_diff);
   }

   /**
    * This method retrieves the overall data block size.
    *
    * @return data block size
    */
   public int getSize()
   {
      return (m_size);
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

      if (m_array[index] != null)
      {
         result = (byte[]) m_array[index];
      }

      return (result);
   }

   /**
    * Accessor method used to retrieve the number of items held in
    * this fixed data block.
    *
    * @return number of items in the block
    */
   public int getItemCount()
   {
      return (m_array.length);
   }

   /**
    * This method dumps the contents of this FixFix block as a String.
    * Note that this facility is provided as a debugging aid.
    *
    * @return formatted contents of this block
    */
   @Override public String toString()
   {
      StringWriter sw = new StringWriter();
      PrintWriter pw = new PrintWriter(sw);

      pw.println("BEGIN FixFix");
      for (int loop = 0; loop < m_array.length; loop++)
      {
         pw.println("   Data at index: " + loop);
         pw.println("  " + ByteArrayHelper.hexdump((byte[]) m_array[loop], true));
      }
      pw.println("END FixFix");

      pw.println();
      pw.close();
      return (sw.toString());
   }

   /**
    * An array containing all of the items of data held in this block.
    */
   private final Object[] m_array;

   /**
    * Overall data block size.
    */
   private final int m_size;

   /**
    * Variable containing the remainder after the available size has
    * been divided by the item size.
    */
   private final int m_diff;
}
