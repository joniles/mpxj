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

package net.sf.mpxj.turboproject;

import java.util.Date;

import net.sf.mpxj.common.DateHelper;

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
    * Read a two byte integer.
    *
    * @param data byte array
    * @param offset offset into array
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
   public static final Date getStartDate(byte[] data, int offset)
   {
      Date result;
      long days = getShort(data, offset);

      if (days == 0x8000)
      {
         result = null;
      }
      else
      {
         result = DateHelper.getDateFromLong(EPOCH + (days * DateHelper.MS_PER_DAY));
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
   public static final Date getFinishDate(byte[] data, int offset)
   {
      Date result;
      long days = getShort(data, offset);

      if (days == 0x8000)
      {
         result = null;
      }
      else
      {
         result = DateHelper.getDateFromLong(EPOCH + ((days - 1) * DateHelper.MS_PER_DAY));
      }

      return (result);
   }

   private static final long EPOCH = 946598400000L;
}
