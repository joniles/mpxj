/*
 * file:       DatatypeConverter.java
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

package org.mpxj.synchro;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.time.LocalDateTime;
import java.time.LocalTime;

import java.util.UUID;

import org.mpxj.Duration;
import org.mpxj.TimeUnit;
import org.mpxj.common.ByteArrayHelper;
import org.mpxj.common.CharsetHelper;
import org.mpxj.common.InputStreamHelper;

/**
 * Common data extraction/conversion methods.
 */
final class DatatypeConverter
{
   /**
    * Extract a simple nul-terminated string from a byte array.
    *
    * @param data byte array
    * @param offset start offset
    * @return String instance
    */
   public static String getSimpleString(byte[] data, int offset)
   {
      StringBuilder buffer = new StringBuilder();
      char c;

      for (int loop = 0; offset + loop < data.length; loop++)
      {
         c = (char) data[offset + loop];

         if (c == 0)
         {
            break;
         }

         buffer.append(c);
      }

      return (buffer.toString());
   }

   /**
    * Read an int from a byte array.
    *
    * @param data byte array
    * @param offset start offset
    * @return int value
    */
   public static final int getInt(byte[] data, int offset)
   {
      return ByteArrayHelper.getInt(data, offset);
   }

   /**
    * Read a short int from a byte array.
    *
    * @param data byte array
    * @param offset start offset
    * @return int value
    */
   public static final int getShort(byte[] data, int offset)
   {
      return ByteArrayHelper.getShort(data, offset);
   }

   /**
    * Read a long int from a byte array.
    *
    * @param data byte array
    * @param offset start offset
    * @return long value
    */
   public static final long getLong(byte[] data, int offset)
   {
      return ByteArrayHelper.getLong(data, offset);
   }

   /**
    * Read an int from an input stream.
    *
    * @param is input stream
    * @return int value
    */
   public static final int getInt(InputStream is) throws IOException
   {
      return getInt(InputStreamHelper.read(is, 4), 0);
   }

   /**
    * Read an Integer from an input stream.
    *
    * @param is input stream
    * @return Integer instance
    */
   public static final Integer getInteger(InputStream is) throws IOException
   {
      return Integer.valueOf(getInt(is));
   }

   /**
    * Read a short int from an input stream.
    *
    * @param is input stream
    * @return int value
    */
   public static final int getShort(InputStream is) throws IOException
   {
      return getShort(InputStreamHelper.read(is, 2), 0);
   }

   /**
    * Read a long int from an input stream.
    *
    * @param is input stream
    * @return long value
    */
   public static final long getLong(InputStream is) throws IOException
   {
      return getLong(InputStreamHelper.read(is, 8), 0);
   }

   /**
    * Read a Synchro string from an input stream.
    *
    * @param is input stream
    * @return String instance
    */
   public static final String getString(InputStream is) throws IOException
   {
      int length;
      int type = is.read();
      if (type == 0xFF)
      {
         length = 0xFF;
      }
      else
      {
         if (type != 1)
         {
            throw new IllegalArgumentException("Unexpected string format");
         }
         length = is.read();
      }

      Charset charset = CharsetHelper.UTF8;
      if (length == 0xFF)
      {
         length = getShort(is);
         if (length == 0xFFFE)
         {
            charset = CharsetHelper.UTF16LE;
            length = (is.read() * 2);
         }
      }

      String result;
      if (length == 0)
      {
         result = null;
      }
      else
      {
         result = new String(InputStreamHelper.read(is, length), charset);
      }
      return result;
   }

   /**
    * Retrieve a boolean from an input stream.
    *
    * @param is input stream
    * @return boolean value
    */
   public static boolean getBoolean(InputStream is) throws IOException
   {
      int value = is.read();
      return value != 0;
   }

   /**
    * Retrieve a UUID from an input stream.
    *
    * @param is input stream
    * @return UUID instance
    */
   public static final UUID getUUID(InputStream is) throws IOException
   {
      byte[] data = InputStreamHelper.read(is, 16);

      long long1 = 0;
      long1 |= ((long) (data[3] & 0xFF)) << 56;
      long1 |= ((long) (data[2] & 0xFF)) << 48;
      long1 |= ((long) (data[1] & 0xFF)) << 40;
      long1 |= ((long) (data[0] & 0xFF)) << 32;
      long1 |= ((long) (data[5] & 0xFF)) << 24;
      long1 |= ((long) (data[4] & 0xFF)) << 16;
      long1 |= ((long) (data[7] & 0xFF)) << 8;
      long1 |= data[6] & 0xFF;

      long long2 = 0;
      long2 |= ((long) (data[8] & 0xFF)) << 56;
      long2 |= ((long) (data[9] & 0xFF)) << 48;
      long2 |= ((long) (data[10] & 0xFF)) << 40;
      long2 |= ((long) (data[11] & 0xFF)) << 32;
      long2 |= ((long) (data[12] & 0xFF)) << 24;
      long2 |= ((long) (data[13] & 0xFF)) << 16;
      long2 |= ((long) (data[14] & 0xFF)) << 8;
      long2 |= data[15] & 0xFF;

      return new UUID(long1, long2);
   }

   /**
    * Read a Synchro date from an input stream.
    *
    * @param is input stream
    * @return Date instance
    */
   public static final LocalDateTime getDate(InputStream is) throws IOException
   {
      long timeInSeconds = getInt(is);
      if (timeInSeconds == NULL_SECONDS)
      {
         return null;
      }
      return EPOCH.plusSeconds(timeInSeconds);
   }

   /**
    * Read a Synchro time from an input stream.
    *
    * @param is input stream
    * @return Date instance
    */
   public static final LocalTime getTime(InputStream is) throws IOException
   {
      int timeValue = getInt(is);
      timeValue -= 86400;
      if (timeValue == 86400)
      {
         timeValue = 0;
      }
      return LocalTime.ofSecondOfDay(timeValue);
   }

   /**
    * Retrieve a Synchro Duration from an input stream.
    *
    * @param is input stream
    * @return Duration instance
    */
   public static final Duration getDuration(InputStream is) throws IOException
   {
      return getDurationFromSeconds(getInt(is));
   }

   /**
    * Retrieve a Synchro Duration from an input stream.
    *
    * @param is input stream
    * @return Duration instance
    */
   public static final Duration getDurationFromLong(InputStream is) throws IOException
   {
      return getDurationFromSeconds((int) getLong(is));
   }

   /**
    * Convert a duration in seconds to a Duration instance.
    *
    * @param durationInSeconds duration in seconds
    * @return Duration instance
    */
   private static Duration getDurationFromSeconds(int durationInSeconds)
   {
      if (durationInSeconds == NULL_SECONDS)
      {
         return null;
      }
      double durationInHours = durationInSeconds;
      durationInHours /= (60 * 60);
      return Duration.getInstance(durationInHours, TimeUnit.HOURS);
   }

   /**
    * Retrieve a Double from an input stream.
    *
    * @param is input stream
    * @return Double instance
    */
   public static final Double getDouble(InputStream is) throws IOException
   {
      double result = Double.longBitsToDouble(getLong(is));
      if (Double.isNaN(result))
      {
         result = 0;
      }
      return Double.valueOf(result);
   }

   private static final long NULL_SECONDS = 0x093406FFF;
   private static final LocalDateTime EPOCH = LocalDateTime.of(1970, 1, 1, 0, 0);
}
