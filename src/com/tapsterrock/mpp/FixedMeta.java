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
class FixedMeta extends MPPComponent
{
   /**
    * Constructor. Reads the meta data from an input stream.
    *
    * @param is input stream from whic the meta data is read
    * @throws IOException on file read failure
    */
   public FixedMeta (InputStream is)
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

      m_unknown1 = readInt (is);
      m_itemCount = readInt (is);
      m_dataSize = readInt (is);

      int itemSize;

      if (m_itemCount == 0)
      {
         itemSize = 0;
      }
      else
      {
         itemSize = (fileSize - 16) / m_itemCount;
      }

      m_size = new int [m_itemCount];
      m_offset = new int [m_itemCount];
      m_unknown2 = new ByteArray[m_itemCount];

      for (int loop=0; loop < m_itemCount; loop++)
      {
         m_size[loop] = readInt (is);
         m_offset[loop] = readInt (is);
         m_unknown2[loop] = new ByteArray (readByteArray (is, itemSize - 8));
      }
   }

   // copes with more inconsistencies... we already know the block size
   // we also ignore the item count in the block and work it out for ourselves
   public FixedMeta (InputStream is, int itemSize)
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

      m_unknown1 = readInt (is);
      m_itemCount = readInt (is);
      m_dataSize = readInt (is);

      m_itemCount = (fileSize - 16) / itemSize;

      m_size = new int [m_itemCount];
      m_offset = new int [m_itemCount];
      m_unknown2 = new ByteArray[m_itemCount];

      for (int loop=0; loop < m_itemCount; loop++)
      {
         m_size[loop] = readInt (is);
         m_offset[loop] = readInt (is);
         m_unknown2[loop] = new ByteArray (readByteArray (is, itemSize - 8));
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
    * This method retrieves the size of the FixedData block.
    *
    * @return the size of the fixed data block
    */
   public int getDataSize ()
   {
      return (m_dataSize);
   }

   /**
    * This method retrieves the offset of a given item of data within
    * the fixed data block.
    *
    * @param item index of the required item
    * @return the offset of the item within the data block
    */
   public int getItemOffset (int item)
   {
      return (m_offset[item]);
   }

   /**
    * This method retrieves the size of a given item of data within
    * the fixed data block.
    *
    * @param item index of the required item
    * @return the size of the item within the data block
    */
   public int getItemSize (int item)
   {
      return (m_size[item]);
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
      pw.println ("   Data size: " + m_dataSize);

      for (int loop=0; loop < m_itemCount; loop++)
      {
         pw.println ("   Offset: " + m_offset[loop] + " size=" + m_size[loop]);
      }

      pw.println ("END: FixedMeta");
      pw.println ();

      pw.close ();
      return (sw.toString ());
   }

   /**
    * Unknown data item
    */
   private int m_unknown1;

   /**
    * Number of items in the data block
    */
   private int m_itemCount;

   /**
    * Overall size of the data block
    */
   private int m_dataSize;

   /**
    * Size of each item within the data block
    */
   private int[] m_size;

   /**
    * Offset of each item within the data block
    */
   private int[] m_offset;

   /**
    * Unknown data items relating to each entry in the fixed data block.
    */
   private ByteArray[] m_unknown2;

   /**
    * Constant representing the magic number appearing
    * at the start of the block.
    */
   private static final int MAGIC = 0xFADFADBA;
}
