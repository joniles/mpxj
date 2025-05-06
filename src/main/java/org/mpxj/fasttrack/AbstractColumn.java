/*
 * file:       AbstractColumn.java
 * author:     Jon Iles
 * copyright:  (c) Packwood Software 2017
 * date:       14/03/2017
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

import java.io.ByteArrayOutputStream;
import java.io.PrintWriter;

/**
 * Implements common elements of the FastTrackColumn interface.
 */
abstract class AbstractColumn implements FastTrackColumn
{
   @Override public void read(FastTrackTableType tableType, byte[] buffer, int startIndex, int length)
   {
      m_header = new BlockHeader().read(buffer, startIndex, postHeaderSkipBytes());
      setFieldType(tableType);
      int offset = readData(buffer, m_header.getOffset());

      if (length > offset)
      {
         m_trailer = new byte[length - offset];
         System.arraycopy(buffer, startIndex + offset, m_trailer, 0, m_trailer.length);
      }
      else
      {
         m_trailer = new byte[0];
      }
   }

   /**
    * Number of bytes to skip once the header has been read.
    *
    * @return number of bytes
    */
   protected abstract int postHeaderSkipBytes();

   /**
    * Reads data in a format specific to the column type.
    *
    * @param buffer buffer containing column data
    * @param offset current offset into the buffer
    * @return offset after reading the column data
    */
   protected abstract int readData(byte[] buffer, int offset);

   /**
    * Dump the column data for debugging.
    *
    * @param pw debug output
    */
   protected abstract void dumpData(PrintWriter pw);

   @Override public String getName()
   {
      return m_header == null ? "<unknown>" : m_header.getName();
   }

   @Override public FastTrackField getType()
   {
      return m_type;
   }

   @Override public Object[] getData()
   {
      return m_data;
   }

   @Override public String toString()
   {
      ByteArrayOutputStream os = new ByteArrayOutputStream();
      PrintWriter pw = new PrintWriter(os);
      pw.println("[" + getClass().getSimpleName());
      pw.println(m_header.toString());
      dumpData(pw);
      pw.print("  Trailer: " + FastTrackUtility.hexdump(m_trailer, 0, true, m_trailer.length, false, 16, ""));
      pw.println("]");
      pw.flush();
      return (os.toString());
   }

   /**
    * Set the enum representing the type of this column.
    *
    * @param tableType type of table to which this column belongs
    */
   private void setFieldType(FastTrackTableType tableType)
   {
      switch (tableType)
      {
         case ACTBARS:
         {
            m_type = ActBarField.getInstance(m_header.getColumnType());
            break;
         }
         case ACTIVITIES:
         {
            m_type = ActivityField.getInstance(m_header.getColumnType());
            break;
         }
         case RESOURCES:
         {
            m_type = ResourceField.getInstance(m_header.getColumnType());
            break;
         }
      }
   }

   private BlockHeader m_header;
   private byte[] m_trailer;
   private FastTrackField m_type;
   protected Object[] m_data;
}
