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

package net.sf.mpxj.synchro;

import java.io.IOException;
import java.io.InputStream;
import java.util.Date;
import java.util.UUID;

import net.sf.mpxj.Duration;
import net.sf.mpxj.TimeUnit;
import net.sf.mpxj.common.DateHelper;

/**
 * Common data extraction/conversion conversion methods.
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
    * Read a short int from a byte array.
    *
    * @param data byte array
    * @param offset start offset
    * @return int value
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
    * Read a long int from a byte array.
    *
    * @param data byte array
    * @param offset start offset
    * @return long value
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
    * Read an int from an input stream.
    *
    * @param is input stream
    * @return int value
    */
   public static final int getInt(InputStream is) throws IOException
   {
      byte[] data = new byte[4];
      is.read(data);
      return getInt(data, 0);
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
      byte[] data = new byte[2];
      is.read(data);
      return getShort(data, 0);
   }

   /**
    * Read a long int from an input stream.
    *
    * @param is input stream
    * @return long value
    */
   public static final long getLong(InputStream is) throws IOException
   {
      byte[] data = new byte[8];
      is.read(data);
      return getLong(data, 0);
   }

   /**
    * Read a Synchro string from an input stream.
    *
    * @param is input stream
    * @return String instance
    */
   public static final String getString(InputStream is) throws IOException
   {
      int type = is.read();
      if (type != 1)
      {
         throw new IllegalArgumentException("Unexpected string format");
      }

      int length = is.read();
      if (length == 0xFF)
      {
         length = getShort(is);
      }

      String result;
      if (length == 0)
      {
         result = null;
      }
      else
      {
         byte[] stringData = new byte[length];
         is.read(stringData);
         result = new String(stringData);
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
      byte[] data = new byte[16];
      is.read(data);

      long long1 = 0;
      long1 |= ((long) (data[3] & 0xFF)) << 56;
      long1 |= ((long) (data[2] & 0xFF)) << 48;
      long1 |= ((long) (data[1] & 0xFF)) << 40;
      long1 |= ((long) (data[0] & 0xFF)) << 32;
      long1 |= ((long) (data[5] & 0xFF)) << 24;
      long1 |= ((long) (data[4] & 0xFF)) << 16;
      long1 |= ((long) (data[7] & 0xFF)) << 8;
      long1 |= ((long) (data[6] & 0xFF)) << 0;

      long long2 = 0;
      long2 |= ((long) (data[8] & 0xFF)) << 56;
      long2 |= ((long) (data[9] & 0xFF)) << 48;
      long2 |= ((long) (data[10] & 0xFF)) << 40;
      long2 |= ((long) (data[11] & 0xFF)) << 32;
      long2 |= ((long) (data[12] & 0xFF)) << 24;
      long2 |= ((long) (data[13] & 0xFF)) << 16;
      long2 |= ((long) (data[14] & 0xFF)) << 8;
      long2 |= ((long) (data[15] & 0xFF)) << 0;

      return new UUID(long1, long2);
   }

   /**
    * Read a Synchro date from an input stream.
    *
    * @param is input stream
    * @return Date instance
    */
   public static final Date getDate(InputStream is) throws IOException
   {
      long timeInSeconds = getInt(is);
      if (timeInSeconds == 0x93406FFF)
      {
         return null;
      }
      timeInSeconds -= 3600;
      timeInSeconds *= 1000;
      return DateHelper.getDateFromLong(timeInSeconds);
   }

   /**
    * Read a Synchro time from an input stream.
    *
    * @param is input stream
    * @return Date instance
    */
   public static final Date getTime(InputStream is) throws IOException
   {
      int timeValue = getInt(is);
      timeValue -= 86400;
      timeValue /= 60;
      return DateHelper.getTimeFromMinutesPastMidnight(Integer.valueOf(timeValue));
   }

   /**
    * Retrieve a Synchro Duration from an input stream.
    *
    * @param is input stream
    * @return Duration instance
    */
   public static final Duration getDuration(InputStream is) throws IOException
   {
      double durationInSeconds = getInt(is);
      durationInSeconds /= (60 * 60);
      return Duration.getInstance(durationInSeconds, TimeUnit.HOURS);
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
}
