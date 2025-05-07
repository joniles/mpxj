/*
 * file:       PEPUtility.java
 * author:     Jon Iles
 * copyright:  (c) Packwood Software 2018
 * date:       12/01/2018
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

package org.mpxj.turboproject;

import java.time.LocalDateTime;

import org.mpxj.common.ByteArrayHelper;

/**
 * Common utility methods for extracting data from a byte array.
 */
final class PEPUtility
{
   /**
    * Read a four byte integer.
    *
    * @param data byte array
    * @param offset offset into array
    * @return integer value
    */
   public static final int getInt(byte[] data, int offset)
   {
      return ByteArrayHelper.getInt(data, offset);
   }

   /**
    * Read a two byte integer.
    *
    * @param data byte array
    * @param offset offset into array
    * @return integer value
    */
   public static final int getShort(byte[] data, int offset)
   {
      return ByteArrayHelper.getShort(data, offset);
   }

   /**
    * Retrieve a string value.
    *
    * @param data byte array
    * @param offset offset into byte array
    * @return string value
    */
   public static final String getString(byte[] data, int offset)
   {
      return getString(data, offset, data.length - offset);
   }

   /**
    * Retrieve a string value with a maximum length.
    *
    * @param data byte array
    * @param offset offset into byte array
    * @param maxLength maximum string length
    * @return string
    */
   public static final String getString(byte[] data, int offset, int maxLength)
   {
      StringBuilder buffer = new StringBuilder();
      char c;

      for (int loop = 0; loop < maxLength; loop++)
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
    * Retrieve a start date.
    *
    * @param data byte array
    * @param offset offset into byte array
    * @return start date
    */
   public static final LocalDateTime getStartDate(byte[] data, int offset)
   {
      LocalDateTime result;
      long days = getShort(data, offset);

      if (days == 0x8000)
      {
         result = null;
      }
      else
      {
         result = EPOCH.plusDays(days);
      }

      return (result);
   }

   /**
    * Retrieve a finish date.
    *
    * @param data byte array
    * @param offset offset into byte array
    * @return finish date
    */
   public static final LocalDateTime getFinishDate(byte[] data, int offset)
   {
      LocalDateTime result;
      long days = getShort(data, offset);

      if (days == 0x8000)
      {
         result = null;
      }
      else
      {
         result = EPOCH.plusDays(days - 1);
      }

      return (result);
   }

   private static final LocalDateTime EPOCH = LocalDateTime.of(1999, 12, 31, 0, 0);
}
