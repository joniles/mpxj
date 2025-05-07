/*
 * file:       DatatypeConverter.java
 * author:     Jon Iles
 * copyright:  (c) Packwood Software 2020
 * date:       24/05/2020
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

package org.mpxj.projectcommander;

import java.time.LocalDateTime;

import org.mpxj.Duration;
import org.mpxj.RelationType;
import org.mpxj.TimeUnit;
import org.mpxj.common.ByteArrayHelper;

/**
 * Contains methods to extract different data types from a byte array.
 */
class DatatypeConverter
{
   /**
    * Retrieve a single byte.
    *
    * @param data byte array
    * @param offset offset into byte array
    * @return byte value
    */
   public static final int getByte(byte[] data, int offset)
   {
      return (data[offset] & 0xFF);
   }

   /**
    * Retrieve a two byte integer.
    *
    * @param data byte array
    * @param offset offset into byte array
    * @return integer value
    */
   public static final int getShort(byte[] data, int offset)
   {
      return ByteArrayHelper.getShort(data, offset);
   }

   /**
    * Populates a byte array at the given offset with an integer
    * expressed as a two byte short.
    *
    * @param data target byte array
    * @param offset offset into byte array
    * @param value value to write
    */
   public static final void setShort(byte[] data, int offset, int value)
   {
      data[offset] = (byte) (value & 0x00FF);
      data[offset + 1] = (byte) ((value & 0xFF00) >> 8);
   }

   /**
    * Retrieve a four byte integer.
    *
    * @param data byte array
    * @param offset offset into byte array
    * @return integer value
    */
   public static final int getInt(byte[] data, int offset)
   {
      return ByteArrayHelper.getInt(data, offset);
   }

   /**
    * Retrieve a two byte integer, with a default if the offset is out of range.
    *
    * @param data byte array
    * @param offset offset into byte array
    * @param defaultValue default value
    * @return integer value
    */
   public static final int getShort(byte[] data, int offset, int defaultValue)
   {
      return offset + 2 > data.length ? defaultValue : getShort(data, offset);
   }

   /**
    * Retrieve a four byte integer, with a default if the offset is out of range.
    *
    * @param data byte array
    * @param offset offset into byte array
    * @param defaultValue default value
    * @return integer value
    */
   public static final int getInt(byte[] data, int offset, int defaultValue)
   {
      return offset + 4 > data.length ? defaultValue : getInt(data, offset);
   }

   /**
    * Read a string from a byte array, with a two byte length prefix.
    *
    * @param data byte array
    * @param offset offset into byte array
    * @return string value
    */
   public static final String getTwoByteLengthString(byte[] data, int offset)
   {
      int length = getShort(data, offset);
      String result;
      if (length == 0)
      {
         result = null;
      }
      else
      {
         result = new String(data, offset + 2, length);
      }
      return result;
   }

   /**
    * Read a string from a byte array, with a single byte length prefix.
    *
    * @param data byte array
    * @param offset offset into byte array
    * @return string value
    */
   public static final String getString(byte[] data, int offset)
   {
      int length = getByte(data, offset);
      String result;
      if (length == 0 || length + offset + 1 > data.length)
      {
         result = null;
      }
      else
      {
         result = new String(data, offset + 1, length);
      }
      return result;
   }

   /**
    * Read a string from a byte array, with a single byte length prefix and a default when the offset is out of bounds.
    *
    * @param data byte array
    * @param offset offset into byte array
    * @param defaultValue default value
    * @return string value
    */
   public static final String getString(byte[] data, int offset, String defaultValue)
   {
      return offset >= data.length ? defaultValue : getString(data, offset);
   }

   /**
    * Retrieve a duration in hours from the byte array.
    *
    * @param data byte array
    * @param offset offset into byte array
    * @return duration value
    */
   public static final Duration getDuration(byte[] data, int offset)
   {
      int durationInMinutes = getInt(data, offset, 0);
      return Duration.getInstance(durationInMinutes / 60, TimeUnit.HOURS);
   }

   /**
    * Retrieve a timestamp from the byte array.
    *
    * @param data byte array
    * @param offset offset into byte array
    * @return timestamp value
    */
   public static final LocalDateTime getTimestamp(byte[] data, int offset)
   {
      long timestampInSeconds = DatatypeConverter.getInt(data, offset, 0);
      return EPOCH.plusSeconds(timestampInSeconds);
   }

   /**
    * Retrieve a RelationType from the byte array.
    *
    * @param data byte array
    * @param offset offset into byte array
    * @return RelationType instance
    */
   public static final RelationType getRelationType(byte[] data, int offset)
   {
      RelationType result;
      int value = getByte(data, offset);
      switch (value)
      {
         case 5:
         {
            result = RelationType.START_FINISH;
            break;
         }

         case 6:
         {
            result = RelationType.START_START;
            break;
         }

         case 7:
         {
            result = RelationType.FINISH_FINISH;
            break;
         }

         case 8:
         default:
         {
            result = RelationType.FINISH_START;
            break;
         }
      }

      return result;
   }

   private static final LocalDateTime EPOCH = LocalDateTime.of(1970, 1, 1, 0, 0);
}
