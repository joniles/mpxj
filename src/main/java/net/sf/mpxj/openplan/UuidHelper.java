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

package net.sf.mpxj.openplan;

import java.util.Arrays;
import java.util.UUID;

final class UuidHelper
{
   private UuidHelper()
   {

   }

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
