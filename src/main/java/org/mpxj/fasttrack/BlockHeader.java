/*
 * file:       BlockHeader.java
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
 * Common header structure which appears at the start of each block containing column data.
 */
class BlockHeader
{

   /**
    * Reads the header data from a block.
    *
    * @param buffer block data
    * @param offset current offset into block data
    * @param postHeaderSkipBytes bytes to skip after reading the header
    * @return current BlockHeader instance
    */
   public BlockHeader read(byte[] buffer, int offset, int postHeaderSkipBytes)
   {
      m_offset = offset;

      System.arraycopy(buffer, m_offset, m_header, 0, 8);
      m_offset += 8;

      int nameLength = FastTrackUtility.getInt(buffer, m_offset);
      m_offset += 4;

      if (nameLength < 1 || nameLength > 255)
      {
         throw new UnexpectedStructureException();
      }

      m_name = FastTrackUtility.getString(buffer, m_offset, nameLength);
      m_offset += nameLength;

      m_columnType = FastTrackUtility.getShort(buffer, m_offset);
      m_offset += 2;

      m_flags = FastTrackUtility.getShort(buffer, m_offset);
      m_offset += 2;

      m_skip = new byte[postHeaderSkipBytes];
      System.arraycopy(buffer, m_offset, m_skip, 0, postHeaderSkipBytes);
      m_offset += postHeaderSkipBytes;

      return this;
   }

   /**
    * Retrieve the offset after reading the header.
    *
    * @return offset
    */
   public int getOffset()
   {
      return m_offset;
   }

   /**
    * Retrieve the name of the column represented by this block.
    *
    * @return column name
    */
   public String getName()
   {
      return m_name;
   }

   /**
    * Retrieve the column type.
    *
    * @return column type
    */
   public int getColumnType()
   {
      return m_columnType;
   }

   /**
    * Retrieve additional flags present in the header.
    *
    * @return flags
    */
   public int getFlags()
   {
      return m_flags;
   }

   @Override public String toString()
   {
      ByteArrayOutputStream os = new ByteArrayOutputStream();
      PrintWriter pw = new PrintWriter(os);
      pw.println("  [BlockHeader");
      pw.print("    Header: " + FastTrackUtility.hexdump(m_header, 0, true, m_header.length, false, 16, ""));
      pw.println("    Name: " + m_name);
      pw.println("    Type: " + m_columnType);
      pw.println("    Flags: " + m_flags);
      pw.print("    Skip:\n" + FastTrackUtility.hexdump(m_skip, 0, true, m_skip.length, false, 16, "      "));
      pw.println("  ]");
      pw.flush();
      return (os.toString());

   }

   private final byte[] m_header = new byte[8];
   private byte[] m_skip;
   private int m_offset;
   private String m_name;
   private int m_columnType;
   private int m_flags;
}
