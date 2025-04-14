/*
 * file:       UuidHelper.java
 * author:     Jon Iles
 * date:       2024-02-27
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

/*
 * Code in this class derived from
 * https://github.com/jakartaee/jaxb-api/blob/master/api/src/main/java/jakarta/xml/bind/DatatypeConverterImpl.java
 * Original copyright notice appears below.
 */

/*
 * Copyright (c) 2007, 2023 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

package org.mpxj.openplan;

import java.util.Arrays;
import java.util.UUID;

/**
 * Helper class for working with UUIDs as stored in BK3 files.
 * Note that Open Plan stores UUIDs using a modified Base 64
 * format, whose alphabet is case-insensitive (i.e. includes no lowercase letters)
 * but as a consequence includes symbols.
 * Note that I have not obtained any sample data which allows me to compare
 * the actual UUID value with the encoded value, therefore the actual mapping
 * of bytes to the alphabet, and the byte order in the Base 64 representation are
 * unknown at present.
 */
final class UuidHelper
{
   /**
    * Private constructor to prevent instantiation.
    */
   private UuidHelper()
   {

   }

   /**
    * Generate the string representation of a UUID value.
    *
    * @param value UUID instance
    * @return string representation
    */
   public static String print(UUID value)
   {
      byte[] data = new byte[16];
      long lsb = value.getLeastSignificantBits();
      long msb = value.getMostSignificantBits();

      data[15] = (byte) (lsb & 0xff);
      data[14] = (byte) (lsb >> 8 & 0xff);
      data[13] = (byte) (lsb >> 16 & 0xff);
      data[12] = (byte) (lsb >> 24 & 0xff);
      data[11] = (byte) (lsb >> 32 & 0xff);
      data[10] = (byte) (lsb >> 40 & 0xff);
      data[9] = (byte) (lsb >> 48 & 0xff);
      data[8] = (byte) (lsb >> 56 & 0xff);

      data[6] = (byte) (msb & 0xff);
      data[7] = (byte) (msb >> 8 & 0xff);
      data[4] = (byte) (msb >> 16 & 0xff);
      data[5] = (byte) (msb >> 24 & 0xff);
      data[0] = (byte) (msb >> 32 & 0xff);
      data[1] = (byte) (msb >> 40 & 0xff);
      data[2] = (byte) (msb >> 48 & 0xff);
      data[3] = (byte) (msb >> 56 & 0xff);

      char[] buf = new char[24];

      int remaining = data.length;
      int ptr = 0;

      int i;
      for (i = 0; remaining >= 3; i += 3)
      {
         buf[ptr++] = encode(data[i] >> 2);
         buf[ptr++] = encode((data[i] & 3) << 4 | data[i + 1] >> 4 & 15);
         buf[ptr++] = encode((data[i + 1] & 15) << 2 | data[i + 2] >> 6 & 3);
         buf[ptr++] = encode(data[i + 2] & 63);
         remaining -= 3;
      }

      if (remaining == 1)
      {
         buf[ptr++] = encode(data[i] >> 2);
         buf[ptr++] = encode((data[i] & 3) << 4);
         buf[ptr++] = PADDING;
         buf[ptr] = PADDING;
      }
      else
      {
         if (remaining == 2)
         {
            buf[ptr++] = encode(data[i] >> 2);
            buf[ptr++] = encode((data[i] & 3) << 4 | data[i + 1] >> 4 & 15);
            buf[ptr++] = encode((data[i + 1] & 15) << 2);
            buf[ptr] = PADDING;
         }
      }

      return new String(buf);
   }

   /**
    * Parse a string representation of a UUID value.
    *
    * @param text string representation of a UUID value.
    * @return UUID instance
    */
   public static UUID parse(String text)
   {
      byte[] data = new byte[16];
      int o = 0;
      int len = text.length();
      byte[] quadruplet = new byte[4];
      int q = 0;

      for (int i = 0; i < len; ++i)
      {
         char ch = text.charAt(i);
         byte v = DECODE_MAP[ch];
         if (v != -1)
         {
            quadruplet[q++] = v;
         }

         if (q == 4)
         {
            data[o++] = (byte) (quadruplet[0] << 2 | quadruplet[1] >> 4);
            if (quadruplet[2] != PADDING_VALUE)
            {
               data[o++] = (byte) (quadruplet[1] << 4 | quadruplet[2] >> 2);
            }

            if (quadruplet[3] != PADDING_VALUE)
            {
               data[o++] = (byte) (quadruplet[2] << 6 | quadruplet[3]);
            }

            q = 0;
         }
      }

      long msb = (data[3] & 0xff);
      msb = (msb << 8) | (data[2] & 0xff);
      msb = (msb << 8) | (data[1] & 0xff);
      msb = (msb << 8) | (data[0] & 0xff);
      msb = (msb << 8) | (data[5] & 0xff);
      msb = (msb << 8) | (data[4] & 0xff);
      msb = (msb << 8) | (data[7] & 0xff);
      msb = (msb << 8) | (data[6] & 0xff);

      long lsb = 0;
      for (int i = 8; i < 16; i++)
      {
         lsb = (lsb << 8) | (data[i] & 0xff);
      }

      return new UUID(msb, lsb);

   }

   private static char encode(int i)
   {
      return ENCODE_MAP[i & 63];
   }

   private static final char PADDING = ' ';
   private static final int PADDING_VALUE = 127;

   private static final char[] ENCODE_MAP = new char[]
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
      'F',
      'G',
      'H',
      'I',
      'J',
      'K',
      'L',
      'M',
      'N',
      'O',
      'P',
      'Q',
      'R',
      'S',
      'T',
      'U',
      'V',
      'W',
      'X',
      'Y',
      'Z',
      '[',
      '\\',
      ']',
      '^',
      '_',
      '{',
      '|',
      '}',
      '~',
      '!',
      '#',
      '$',
      '%',
      '&',
      '(',
      ')',
      '*',
      '+',
      '-',
      '.',
      '/',
      ':',
      ';',
      '<',
      '=',
      '>',
      '?',
      '@',
   };

   private static final byte[] DECODE_MAP = new byte[255];
   static
   {
      Arrays.fill(DECODE_MAP, (byte) -1);
      for (byte loop = 0; loop < ENCODE_MAP.length; loop++)
      {
         DECODE_MAP[ENCODE_MAP[loop]] = loop;
      }
      DECODE_MAP[PADDING] = PADDING_VALUE;
   }
}
