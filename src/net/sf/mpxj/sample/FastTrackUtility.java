
package net.sf.mpxj.sample;

import java.nio.charset.Charset;
import java.text.DecimalFormat;

public final class FastTrackUtility
{

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
    * This method reads an eight byte double from the input array.
    *
    * @param data the input array
    * @param offset offset of double data in the array
    * @return double value
    */
   public static final double getDouble(byte[] data, int offset)
   {
      double result = Double.longBitsToDouble(getLong(data, offset));
      if (Double.isNaN(result))
      {
         result = 0;
      }
      return result;
   }

   public static int skipTo(int offset, byte[] buffer, int startIndex, int value)
   {
      int nextOffset = offset;
      while (getShort(buffer, startIndex + nextOffset) != value)
      {
         ++nextOffset;
      }
      nextOffset += 2;

      return nextOffset;
   }

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

         if (ascii == true)
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

   public static final Charset UTF16LE = Charset.forName("UTF-16LE");

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
