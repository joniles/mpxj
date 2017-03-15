
package net.sf.mpxj.fasttrack;

import java.nio.charset.Charset;
import java.text.DecimalFormat;

import net.sf.mpxj.TimeUnit;

final class FastTrackUtility
{
   public static final void validateSize(int size)
   {
      if (size < 0 || size > 100000)
      {
         throw new UnexpectedStructureException();
      }
   }

   public static final void validateOffset(byte[] buffer, int offset)
   {
      if (offset >= buffer.length)
      {
         throw new UnexpectedStructureException();
      }
   }

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

   public static final int getByte(byte[] data, int offset)
   {
      int result = (data[offset] & 0xFF);
      return result;
   }

   public static final TimeUnit getTimeUnit(int value)
   {
      TimeUnit result = null;

      switch (value)
      {
         case 1:
         {
            // TEMP - means same as document format?
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

   public static int skipTo(byte[] buffer, int offset, int value)
   {
      int nextOffset = offset;
      while (getShort(buffer, nextOffset) != value)
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

   public static final String nameToConstant(String name)
   {
      String result = name.toUpperCase();
      return result.replace(' ', '_');
   }

   private static final long NULL_DOUBLE = 0x3949F623D5A8A733L;

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
