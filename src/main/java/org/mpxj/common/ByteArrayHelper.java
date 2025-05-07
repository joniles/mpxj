/*
 * file:       ByteArrayHelper.java
 * author:     Jon Iles
 * copyright:  (c) Packwood Software 2018
 * date:       2018-10-11
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

package org.mpxj.common;

import java.text.DecimalFormat;

/**
 * Helper methods for working with byte arrays.
 */
public final class ByteArrayHelper
{
   /**
    * This method reads a two byte integer from the input array.
    *
    * @param data the input array
    * @param offset offset of integer data in the array
    * @return integer value
    */
   public static final int getShort(byte[] data, int offset)
   {
      int result = 0;
      int i = offset;
      for (int shiftBy = 0; shiftBy < 16; shiftBy += 8)
      {
         result |= ((data[i] & 0xff)) << shiftBy;
         ++i;
      }
      return result;
   }

   /**
    * This method reads a four byte integer from the input array.
    *
    * @param data the input array
    * @param offset offset of integer data in the array
    * @return integer value
    */
   public static final int getInt(byte[] data, int offset)
   {
      int result = 0;
      int i = offset;
      for (int shiftBy = 0; shiftBy < 32; shiftBy += 8)
      {
         result |= ((data[i] & 0xff)) << shiftBy;
         ++i;
      }
      return result;
   }

   /**
    * This method reads an eight byte integer from the input array.
    *
    * @param data the input array
    * @param offset offset of integer data in the array
    * @return integer value
    */
   public static final long getLong(byte[] data, int offset)
   {
      long result = 0;
      int i = offset;
      for (int shiftBy = 0; shiftBy < 64; shiftBy += 8)
      {
         result |= ((long) (data[i] & 0xff)) << shiftBy;
         ++i;
      }
      return result;
   }

   /**
    * This method generates a formatted version of the data contained
    * in a byte array. The data is written both in hex, and as ASCII
    * characters.
    *
    * @param buffer data to be displayed
    * @param offset offset of start of data to be displayed
    * @param length length of data to be displayed
    * @param ascii flag indicating whether ASCII equivalent chars should also be displayed
    * @return formatted string
    */
   public static final String hexdump(byte[] buffer, int offset, int length, boolean ascii)
   {
      StringBuilder sb = new StringBuilder();

      if (buffer != null)
      {
         char c;
         int loop;
         int count = offset + length;

         for (loop = offset; loop < count; loop++)
         {
            sb.append(" ");
            sb.append(HEX_DIGITS[(buffer[loop] & 0xF0) >> 4]);
            sb.append(HEX_DIGITS[buffer[loop] & 0x0F]);
         }

         if (ascii)
         {
            sb.append("   ");

            for (loop = offset; loop < count; loop++)
            {
               c = (char) buffer[loop];

               if ((c > 200) || (c < 27))
               {
                  c = ' ';
               }

               sb.append(c);
            }
         }
      }

      return (sb.toString());
   }

   /**
    * This method generates a formatted version of the data contained
    * in a byte array. The data is written both in hex, and as ASCII
    * characters.
    *
    * @param buffer data to be displayed
    * @param ascii flag indicating whether ASCII equivalent chars should also be displayed
    * @return formatted string
    */
   public static final String hexdump(byte[] buffer, boolean ascii)
   {
      int length = 0;

      if (buffer != null)
      {
         length = buffer.length;
      }

      return (hexdump(buffer, 0, length, ascii));
   }

   /**
    * This method generates a formatted version of the data contained
    * in a byte array. The data is written both in hex, and as ASCII
    * characters. The data is organised into fixed width columns.
    *
    * @param buffer data to be displayed
    * @param ascii flag indicating whether ASCII equivalent chars should also be displayed
    * @param columns number of columns
    * @param prefix prefix to be added before the start of the data
    * @return formatted string
    */
   public static final String hexdump(byte[] buffer, boolean ascii, int columns, String prefix)
   {
      StringBuilder sb = new StringBuilder();
      if (buffer != null)
      {
         int index = 0;
         DecimalFormat df = new DecimalFormat("00000");

         while (index < buffer.length)
         {
            if (index + columns > buffer.length)
            {
               columns = buffer.length - index;
            }

            sb.append(prefix);
            sb.append(df.format(index));
            sb.append(":");
            sb.append(hexdump(buffer, index, columns, ascii));
            sb.append('\n');

            index += columns;
         }
      }

      return (sb.toString());
   }

   /**
    * This method generates a formatted version of the data contained
    * in a byte array. The data is written both in hex, and as ASCII
    * characters. The data is organised into fixed width columns.
    *
    * @param buffer data to be displayed
    * @param offset offset into buffer
    * @param length number of bytes to display
    * @param ascii flag indicating whether ASCII equivalent chars should also be displayed
    * @param columns number of columns
    * @param prefix prefix to be added before the start of the data
    * @return formatted string
    */
   public static final String hexdump(byte[] buffer, int offset, int length, boolean ascii, int columns, String prefix)
   {
      StringBuilder sb = new StringBuilder();
      if (buffer != null)
      {
         int index = offset;
         DecimalFormat df = new DecimalFormat("00000");

         while (index < (offset + length))
         {
            if (index + columns > (offset + length))
            {
               columns = (offset + length) - index;
            }

            sb.append(prefix);
            sb.append(df.format(index - offset));
            sb.append(":");
            sb.append(hexdump(buffer, index, columns, ascii));
            sb.append('\n');

            index += columns;
         }
      }

      return (sb.toString());
   }

   /**
    * Constants used to convert bytes to hex digits.
    */
   private static final char[] HEX_DIGITS =
   {
      '0',
      '1',
      '2',
      '3',
      '4',
      '5',
      '6',
      '7',
      '8',
      '9',
      'A',
      'B',
      'C',
      'D',
      'E',
      'F'
   };
}
