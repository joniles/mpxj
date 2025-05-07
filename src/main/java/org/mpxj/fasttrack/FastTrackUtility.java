/*
 * file:       FastTrackUtility.java
 * author:     Jon Iles
 * copyright:  (c) Packwood Software 2017
 * date:       14/03/2016
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

import java.text.DecimalFormat;

import org.mpxj.TimeUnit;
import org.mpxj.common.ByteArrayHelper;

/**
 * Common methods used when reading an FTS file.
 */
final class FastTrackUtility
{
   /**
    * Ensure that a size value falls within sensible bounds.
    *
    * @param size value
    */
   public static final void validateSize(int size)
   {
      if (size < 0 || size > 100000)
      {
         throw new UnexpectedStructureException();
      }
   }

   /**
    * Ensure that an array index is in range.
    *
    * @param buffer array of data
    * @param offset index into array
    */
   public static final void validateOffset(byte[] buffer, int offset)
   {
      if (offset >= buffer.length)
      {
         throw new UnexpectedStructureException();
      }
   }

   /**
    * Read a string and gracefully handle failure.
    *
    * @param buffer array of data
    * @param offset offset into array
    * @param length string length
    * @return String instance
    */
   public static String getString(byte[] buffer, int offset, int length)
   {
      if (offset > buffer.length - length)
      {
         throw new UnexpectedStructureException();
      }

      String result = new String(buffer, offset, length, FastTrackData.getInstance().getCharset());

      // Strip trailing invalid characters
      while (!result.isEmpty() && isInvalidCharacter(result.charAt(result.length() - 1)))
      {
         result = result.substring(0, result.length() - 1);
      }
      return result;
   }

   private static boolean isInvalidCharacter(char c)
   {
      return Character.isISOControl(c) && c != '\r' && c != '\n' && c != '\t';
   }

   /**
    * Retrieve a four byte integer.
    *
    * @param data array of data
    * @param offset offset into array
    * @return int value
    */
   public static final int getInt(byte[] data, int offset)
   {
      if (offset + 4 > data.length)
      {
         throw new UnexpectedStructureException();
      }

      return ByteArrayHelper.getInt(data, offset);
   }

   /**
    * Retrieve a two byte integer.
    *
    * @param data array of data
    * @param offset offset into array
    * @return int value
    */
   public static final int getShort(byte[] data, int offset)
   {
      if (offset + 2 > data.length)
      {
         throw new UnexpectedStructureException();
      }

      return ByteArrayHelper.getShort(data, offset);
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
      if (offset + 8 > data.length)
      {
         throw new UnexpectedStructureException();
      }

      return ByteArrayHelper.getLong(data, offset);
   }

   /**
    * This method reads an eight byte double from the input array.
    *
    * @param data the input array
    * @param offset offset of double data in the array
    * @return double value
    */
   public static final Double getDouble(byte[] data, int offset)
   {
      Double result = null;
      long longValue = getLong(data, offset);
      if (longValue != NULL_DOUBLE)
      {
         double doubleValue = Double.longBitsToDouble(longValue);
         if (!Double.isNaN(doubleValue))
         {
            result = Double.valueOf(doubleValue);
         }
      }
      return result;
   }

   /**
    * Retrieve a single byte from an input array.
    *
    * @param data input array
    * @param offset offset into input array
    * @return byte value
    */
   public static final int getByte(byte[] data, int offset)
   {
      return (data[offset] & 0xFF);
   }

   /**
    * Convert an integer value into a TimeUnit instance.
    *
    * @param value time unit value
    * @return TimeUnit instance
    */
   public static final TimeUnit getTimeUnit(int value)
   {
      TimeUnit result = null;

      switch (value)
      {
         case 1:
         {
            // Appears to mean "use the document format"
            result = TimeUnit.ELAPSED_DAYS;
            break;
         }

         case 2:
         {
            result = TimeUnit.HOURS;
            break;
         }

         case 4:
         {
            result = TimeUnit.DAYS;
            break;
         }

         case 6:
         {
            result = TimeUnit.WEEKS;
            break;
         }

         case 8:
         case 10:
         {
            result = TimeUnit.MONTHS;
            break;
         }

         case 12:
         {
            result = TimeUnit.YEARS;
            break;
         }

         default:
         {
            break;
         }
      }

      return result;
   }

   /**
    * Skip to the next matching short value.
    *
    * @param buffer input data array
    * @param offset start offset into the input array
    * @param value value to match
    * @return offset of matching pattern
    */
   public static int skipToNextMatchingShort(byte[] buffer, int offset, int value)
   {
      int nextOffset = offset;
      while (getShort(buffer, nextOffset) != value)
      {
         ++nextOffset;
      }
      nextOffset += 2;

      return nextOffset;
   }

   /**
    * Dump raw data as hex.
    *
    * @param buffer buffer
    * @param offset offset into buffer
    * @param showRawOffset show raw offset, not offset from data start
    * @param length length of data to dump
    * @param ascii true if ASCII should also be printed
    * @param columns number of columns
    * @param prefix prefix when printing
    * @return hex dump
    */
   public static final String hexdump(byte[] buffer, int offset, boolean showRawOffset, int length, boolean ascii, int columns, String prefix)
   {
      StringBuilder sb = new StringBuilder();
      if (buffer != null)
      {
         int index = offset;
         int indexAdjust = showRawOffset ? 0 : offset;
         DecimalFormat df = new DecimalFormat("00000");

         while (index < (offset + length))
         {
            if (index + columns > (offset + length))
            {
               columns = (offset + length) - index;
            }

            sb.append(prefix);
            sb.append(df.format(index - indexAdjust));
            sb.append(":");
            sb.append(hexdump(buffer, index, columns, ascii));
            sb.append('\n');

            index += columns;
         }
      }

      return (sb.toString());
   }

   /**
    * Dump raw data as hex.
    *
    * @param buffer buffer
    * @param offset offset into buffer
    * @param length length of data to dump
    * @param ascii true if ASCII should also be printed
    * @return hex dump
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

   private static final long NULL_DOUBLE = 0x3949F623D5A8A733L;

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
