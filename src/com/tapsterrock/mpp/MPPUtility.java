/*
 * file:       MPPUtility.java
 * author:     Jon Iles
 * copyright:  (c) Tapster Rock Limited 2002-2003
 * date:       05/01/2003
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

import java.util.Date;

/**
 * This class provides common functionality used by each of the classes
 * that read the different sections of the MPP file.
 */
class MPPUtility
{
   /**
    * Private constructor to prevent instantiation.
    */
   private MPPUtility ()
   {

   }

   /**
    * This method reads a single byte from the input array
    *
    * @param data byte array of data
    * @param offset offset of byte data in the array
    * @return byte value
    */
   public static final int getByte (byte[] data, int offset)
   {
      int result = (int)(data[offset] & 0x0F);
      result += (int)(data[offset] >> 4 & 0x0F) * 16;
      return (result);
   }

   /**
    * This method reads a single byte from the input array.
    * The byte is assumed to be at the start of the array.
    *
    * @param data byte array of data
    * @return byte value
    */
   public static final int getByte (byte[] data)
   {
      return (getByte (data, 0));
   }

   /**
    * This method reads a two byte integer from the input array.
    *
    * @param data the input array
    * @param offset offset of integer data in the array
    * @return integer value
    */
   public static final int getShort (byte[] data, int offset)
   {
      int result = (int)(data[offset] & 0x0F);
      result += (int)(data[offset] >> 4 & 0x0F) * 16;
      result += (int)(data[offset+1] & 0x0F) * 256;
      result += (int)(data[offset+1] >> 4 & 0x0F) * 4096;
      return (result);
   }

   /**
    * This method reads a two byte integer from the input array.
    * The integer is assumed to be at the start of the array.
    *
    * @param data the input array
    * @return integer value
    */
   public static final int getShort (byte[] data)
   {
      return (getShort (data, 0));
   }

   /**
    * This method reads a four byte integer from the input array.
    *
    * @param data the input array
    * @param offset offset of integer data in the array
    * @return integer value
    */
   public static final int getInt (byte[] data, int offset)
   {
      int result = (int)(data[offset] & 0x0F);
      result += (int)(data[offset] >> 4 & 0x0F) * 16;
      result += (int)(data[offset+1] & 0x0F) * 256;
      result += (int)(data[offset+1] >> 4 & 0x0F) * 4096;
      result += (int)(data[offset+2] & 0x0F) * 65536;
      result += (int)(data[offset+2] >> 4 & 0x0F) * 1048576;
      result += (int)(data[offset+3] & 0x0F) * 16777216;
      result += (int)(data[offset+3] >> 4 & 0x0F) * 268435456;
      return (result);
   }

   /**
    * This method reads a four byte integer from the input array.
    * The integer is assumed to be at the start of the array.
    *
    * @param data the input array
    * @return integer value
    */
   public static final int getInt (byte[] data)
   {
      return (getInt (data, 0));
   }

   /**
    * This method reads an eight byte integer from the input array.
    *
    * @param data the input array
    * @param offset offset of integer data in the array
    * @return integer value
    */
   public static final long getLong (byte[] data, int offset)
   {
      long result = (long)(data[offset] & 0x0F); // 0
      result += (long)(data[offset] >> 4 & 0x0F) * 16; // 1
      result += (long)(data[offset+1] & 0x0F) * 256; // 2
      result += (long)(data[offset+1] >> 4 & 0x0F) * 4096; // 3
      result += (long)(data[offset+2] & 0x0F) * 65536; // 4
      result += (long)(data[offset+2] >> 4 & 0x0F) * 1048576; // 5
      result += (long)(data[offset+3] & 0x0F) * 16777216; // 6
      result += (long)(data[offset+3] >> 4 & 0x0F) * 268435456; // 7
      result += (long)(data[offset+4] & 0x0F) * 4294967296L; // 8
      result += (long)(data[offset+4] >> 4 & 0x0F) * 68719476736L; // 9
      result += (long)(data[offset+5] & 0x0F) * 1099511627776L; // 10
      result += (long)(data[offset+5] >> 4 & 0x0F) * 17592186044416L; // 11
      result += (long)(data[offset+6] & 0x0F) * 281474976710656L; // 12
      result += (long)(data[offset+6] >> 4 & 0x0F) * 4503599627370496L; // 13
      result += (long)(data[offset+7] & 0x0F) * 72057594037927936L; // 14
      result += (long)(data[offset+7] >> 4 & 0x0F) * 1152921504606846976L; // 15
      return (result);
   }

   /**
    * This method reads a eight byte integer from the input array.
    * The integer is assumed to be at the start of the array.
    *
    * @param data the input array
    * @return integer value
    */
   public static final long getLong (byte[] data)
   {
      return (getLong (data, 0));
   }

   /**
    * This method reads an eight byte double from the input array.
    *
    * @param data the input array
    * @param offset offset of double data in the array
    * @return double value
    */
   public static final double getDouble (byte[] data, int offset)
   {
      return (Double.longBitsToDouble(getLong (data, offset)));
   }

   /**
    * This method reads an eight byte double from the input array.
    * The double is assumed to be at the start of the array.
    *
    * @param data the input array
    * @return double value
    */
   public static final double getDouble (byte[] data)
   {
      return (Double.longBitsToDouble(getLong (data, 0)));
   }

   /**
    * Reads a date value. Note that an NA is represented as 65535 in the
    * MPP file. We represent this in Java using a null value. The actual
    * value in the MPP file is number of days since 31/12/1983.
    *
    * @param data byte array of data
    * @param offset location of data as offset into the array
    * @return date value
    */
   public static final Date getDate (byte[] data, int offset)
   {
      Date result;
      long days = (long)getShort (data, offset);
      if (days == 65535)
      {
         result = null;
      }
      else
      {
         result = new Date (EPOCH + (days * MS_PER_DAY));
      }

      return (result);
   }

   /**
    * Reads a time value. The time is represented as tenths of a
    * minute since midnight.
    *
    * @param data byte array of data
    * @param offset location of data as offset into the array
    * @return time value
    */
   public static final Date getTime (byte[] data, int offset)
   {
      long time = (long)getShort (data, offset);

      Date result = new Date ((time * MS_PER_MINUTE)/10);

      return (result);
   }

   /**
    * Reads a combined date and time value.
    *
    * @param data byte array of data
    * @param offset location of data as offset into the array
    * @return time value
    */
   public static final Date getTimestamp (byte[] data, int offset)
   {
      Date result;

      long days = (long)getShort (data, offset+2);
      if (days == 65535)
      {
         result = null;
      }
      else
      {
         long time = (long)getShort (data, offset);
         result = new Date (EPOCH + (days * MS_PER_DAY) + ((time * MS_PER_MINUTE)/10));
      }

      return (result);
   }

   /**
    * Reads a combined date and time value.
    * The value is assumed to be at the start of the array.
    *
    * @param data byte array of data
    * @return time value
    */
   public static final Date getTimestamp (byte[] data)
   {
      return (getTimestamp (data, 0));
   }

   /**
    * Reads a string of two byte characters from the input array.
    * This method assumes that the string finishes either at the
    * end of the array, or when char zero is encountered.
    * The value is assumed to be at the start of the array.
    *
    * @param data byte array of data
    * @return string value
    */
   public static final String getUnicodeString (byte[] data)
   {
      StringBuffer buffer = new StringBuffer ();
      char c;

      for (int loop=0; loop < data.length; loop += 2)
      {
         c = (char)getShort (data, loop);
         if (c == 0)
         {
            break;
         }
         buffer.append (c);
      }

      return (buffer.toString());
   }

   /**
    * Reads a string of single byte characters from the input array.
    * This method assumes that the string finishes either at the
    * end of the array, or when char zero is encountered.
    * The value is assumed to be at the start of the array.
    *
    * @param data byte array of data
    * @return string value
    */
   public static final String getString (byte[] data)
   {
      StringBuffer buffer = new StringBuffer ();
      char c;

      for (int loop=0; loop < data.length; loop++)
      {
         c = (char)data[loop];
         if (c == 0)
         {
            break;
         }
         buffer.append (c);
      }

      return (buffer.toString());
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
   public static final String hexdump (byte[] buffer, int offset, int length, boolean ascii)
   {
      char c;
      int loop;
      StringBuffer sb = new StringBuffer ();
      int count = offset+length;

      for (loop=offset; loop < count; loop++)
      {
         sb.append (" ");
         sb.append (HEX_DIGITS[(buffer[loop] & 0xF0) >> 4]);
         sb.append (HEX_DIGITS[buffer[loop] & 0x0F]);
      }

      if (ascii == true)
      {
         sb.append ("   ");

         for (loop=offset; loop < count; loop++)
         {
            c = (char)buffer[loop];
            if (c > 200 || c < 27)
            {
               c = ' ';
            }

            sb.append (c);
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
   public static final String hexdump (byte[] buffer, boolean ascii)
   {
      return (hexdump (buffer, 0, buffer.length, ascii));
   }

   /**
    * Epoch date for MPP date calculation is 31/12/1983. This constant
    * is that date expressed in milliseconds using the Java date epoch.
    */
   private static final long EPOCH = 441676800000L;

   /**
    * Number of milliseconds per day.
    */
   private static final long MS_PER_DAY = 24 * 60 * 60 * 1000;

   /**
    * Number of milliseconds per minute.
    */
   private static final long MS_PER_MINUTE = 60 * 1000;

   /**
    * Constants used to convert bytes to hex digits
    */
   private static final char[] HEX_DIGITS =
   {
      '0', '1', '2', '3', '4', '5', '6', '7', '8', '9',
      'A', 'B', 'C', 'D', 'E', 'F'
   };
}

