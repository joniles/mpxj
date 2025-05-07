/*
 * Blast is a class which handles decompression of data compressed using
 * the PKWare Compression Library. (Blast being an alternative to explode -
 * the name of the original PKWare decompression routine).
 *
 * This is a translation to Java of blast.c from:
 *
 * https://github.com/madler/zlib/blob/master/contrib/blast/blast.c
 *
 * I have maintained as many of the original comments as possible.
 * I have applied minimal refactoring to the code so the structure
 * follows the original closely.
 */

/* blast.h -- interface for blast.c
  Copyright (C) 2003, 2012, 2013 Mark Adler
  version 1.3, 24 Aug 2013
  This software is provided 'as-is', without any express or implied
  warranty.  In no event will the author be held liable for any damages
  arising from the use of this software.
  Permission is granted to anyone to use this software for any purpose,
  including commercial applications, and to alter it and redistribute it
  freely, subject to the following restrictions:
  1. The origin of this software must not be misrepresented; you must not
     claim that you wrote the original software. If you use this software
     in a product, an acknowledgment in the product documentation would be
     appreciated but is not required.
  2. Altered source versions must be plainly marked as such, and must not be
     misrepresented as being the original software.
  3. This notice may not be removed or altered from any source distribution.
  Mark Adler    madler@alumni.caltech.edu
 */

/*
 * blast() decompresses the PKWare Data Compression Library (DCL) compressed
 * format.  It provides the same functionality as the explode() function in
 * that library.  (Note: PKWare overused the "implode" verb, and the format
 * used by their library implode() function is completely different and
 * incompatible with the implode compression method supported by PKZIP.)
 *
 * The binary mode for stdio functions should be used to assure that the
 * compressed data is not corrupted when read or written.  For example:
 * fopen(..., "rb") and fopen(..., "wb").
 */

/* Decompress input to output using the provided infun() and outfun() calls.
 * On success, the return value of blast() is zero.  If there is an error in
 * the source data, i.e. it is not in the proper format, then a negative value
 * is returned.  If there is not enough input available or there is not enough
 * output space, then a positive error is returned.
 *
 * The input function is invoked: len = infun(how, &buf), where buf is set by
 * infun() to point to the input buffer, and infun() returns the number of
 * available bytes there.  If infun() returns zero, then blast() returns with
 * an input error.  (blast() only asks for input if it needs it.)  inhow is for
 * use by the application to pass an input descriptor to infun(), if desired.
 *
 * If left and in are not NULL and *left is not zero when blast() is called,
 * then the *left bytes are *in are consumed for input before infun() is used.
 *
 * The output function is invoked: err = outfun(how, buf, len), where the bytes
 * to be written are buf[0..len-1].  If err is not zero, then blast() returns
 * with an output error.  outfun() is always called with len <= 4096.  outhow
 * is for use by the application to pass an output descriptor to outfun(), if
 * desired.
 *
 * If there is any unused input, *left is set to the number of bytes that were
 * read and *in points to them.  Otherwise *left is set to zero and *in is set
 * to NULL.  If left or in are NULL, then they are not set.
 *
 * The return codes are:
 *
 *   2:  ran out of input before completing decompression
 *   1:  output error before completing decompression
 *   0:  successful decompression
 *  -1:  literal flag not zero or one
 *  -2:  dictionary size not in 4..6
 *  -3:  distance is too far back
 *
 * At the bottom of blast.c is an example program that uses blast() that can be
 * compiled to produce a command-line decompression filter by defining TEST.
 */

/* blast.c
 * Copyright (C) 2003, 2012, 2013 Mark Adler
 * For conditions of distribution and use, see copyright notice in blast.h
 * version 1.3, 24 Aug 2013
 *
 * blast.c decompresses data compressed by the PKWare Compression Library.
 * This function provides functionality similar to the explode() function of
 * the PKWare library, hence the name "blast".
 *
 * This decompressor is based on the excellent format description provided by
 * Ben Rudiak-Gould in comp.compression on August 13, 2001.  Interestingly, the
 * example Ben provided in the post is incorrect.  The distance 110001 should
 * instead be 111000.  When corrected, the example byte stream becomes:
 *
 *    00 04 82 24 25 8f 80 7f
 *
 * which decompresses to "AIAIAIAIAIAIA" (without the quotes).
 */

package org.mpxj.primavera.common;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Blast class - see notes in comments above.
 */
public class Blast
{
   /**
    * Decode PKWare Compression Library stream.
    *
    * Format notes:
    *
    * - First byte is 0 if literals are uncoded or 1 if they are coded.  Second
    *   byte is 4, 5, or 6 for the number of extra bits in the distance code.
    *   This is the base-2 logarithm of the dictionary size minus six.
    *
    * - Compressed data is a combination of literals and length/distance pairs
    *   terminated by an end code.  Literals are either Huffman coded or
    *   uncoded bytes.  A length/distance pair is a coded length followed by a
    *   coded distance to represent a string that occurs earlier in the
    *   uncompressed data that occurs again at the current location.
    *
    * - A bit preceding a literal or length/distance pair indicates which comes
    *   next, 0 for literals, 1 for length/distance.
    *
    * - If literals are uncoded, then the next eight bits are the literal, in the
    *   normal bit order in the stream, i.e. no bit-reversal is needed. Similarly,
    *   no bit reversal is needed for either the length extra bits or the distance
    *   extra bits.
    *
    * - Literal bytes are simply written to the output.  A length/distance pair is
    *   an instruction to copy previously uncompressed bytes to the output.  The
    *   copy is from distance bytes back in the output stream, copying for length
    *   bytes.
    *
    * - Distances pointing before the beginning of the output data are not
    *   permitted.
    *
    * - Overlapped copies, where the length is greater than the distance, are
    *   allowed and common.  For example, a distance of one and a length of 518
    *   simply copies the last byte 518 times.  A distance of four and a length of
    *   twelve copies the last four bytes three times.  A simple forward copy
    *   ignoring whether the length is greater than the distance or not implements
    *   this correctly.
    *
    *  @param input InputStream instance
    *  @param output OutputStream instance
    *  @return status code
    */
   public int blast(InputStream input, OutputStream output) throws IOException
   {
      m_input = input;

      int lit; /* true if literals are coded */
      int dict; /* log2(dictionary size) - 6 */
      int symbol; /* decoded symbol, extra bits for distance */
      int len; /* length for copy */
      int dist; /* distance for copy */
      int copy; /* copy counter */
      //unsigned char *from, *to;   /* copy pointers */

      /* read header */
      lit = bits(8);
      if (lit > 1)
      {
         return -1;
      }
      dict = bits(8);
      if (dict < 4 || dict > 6)
      {
         return -2;
      }

      /* decode literals and length/distance pairs */
      do
      {
         if (bits(1) != 0)
         {
            /* get length */
            symbol = decode(LENCODE);
            len = BASE[symbol] + bits(EXTRA[symbol]);
            if (len == 519)
            {
               break; /* end code */
            }

            /* get distance */
            symbol = len == 2 ? 2 : dict;
            dist = decode(DISTCODE) << symbol;
            dist += bits(symbol);
            dist++;
            if (m_first != 0 && dist > m_next)
            {
               return -3; /* distance too far back */
            }

            /* copy length bytes from distance bytes back */
            do
            {
               //to = m_out + m_next;
               int to = m_next;
               int from = to - dist;
               copy = MAXWIN;
               if (m_next < dist)
               {
                  from += copy;
                  copy = dist;
               }
               copy -= m_next;
               if (copy > len)
               {
                  copy = len;
               }
               len -= copy;
               m_next += copy;
               do
               {
                  //*to++ = *from++;
                  m_out[to++] = m_out[from++];
               }
               while (--copy != 0);
               if (m_next == MAXWIN)
               {
                  //if (s->outfun(s->outhow, s->out, s->next)) return 1;
                  output.write(m_out, 0, m_next);
                  m_next = 0;
                  m_first = 0;
               }
            }
            while (len != 0);
         }
         else
         {
            /* get literal and write it */
            symbol = lit != 0 ? decode(LITCODE) : bits(8);
            m_out[m_next++] = (byte) symbol;
            if (m_next == MAXWIN)
            {
               //if (s->outfun(s->outhow, s->out, s->next)) return 1;
               output.write(m_out, 0, m_next);
               m_next = 0;
               m_first = 0;
            }
         }
      }
      while (true);

      if (m_next != 0)
      {
         output.write(m_out, 0, m_next);
      }

      return 0;
   }

   /**
    * Return need bits from the input stream.  This always leaves less than
    * eight bits in the buffer.  bits() works properly for need == 0.
    *
    * Format notes:
    *
    * - Bits are stored in bytes from the least significant bit to the most
    *   significant bit.  Therefore bits are dropped from the bottom of the bit
    *   buffer, using shift right, and new bytes are appended to the top of the
    *   bit buffer, using shift left.
    *
    * @param need number of bits required
    * @return bit values
    */
   private int bits(int need) throws IOException
   {
      int val; /* bit accumulator */

      /* load at least need bits into val */
      val = m_bitbuf;
      while (m_bitcnt < need)
      {
         if (m_left == 0)
         {
            m_in = m_input.read();
            m_left = m_in == -1 ? 0 : 1;
            if (m_left == 0)
            {
               throw new IOException("out of input"); /* out of input */
            }
         }
         val |= m_in << m_bitcnt; /* load eight bits */
         m_left--;
         m_bitcnt += 8;
      }

      /* drop need bits and update buffer, always zero to seven bits left */
      m_bitbuf = val >> need;
      m_bitcnt -= need;

      /* return need bits, zeroing the bits above that */
      return val & ((1 << need) - 1);
   }

   /**
    * Decode a code from the stream s using huffman table h.  Return the symbol or
    * a negative value if there is an error.  If all of the lengths are zero, i.e.
    * an empty code, or if the code is incomplete and an invalid code is received,
    * then -9 is returned after reading MAXBITS bits.
    *
    * Format notes:
    *
    * - The codes as stored in the compressed data are bit-reversed relative to
    *   a simple integer ordering of codes of the same lengths.  Hence below the
    *   bits are pulled from the compressed data one at a time and used to
    *   build the code value reversed from what is in the stream in order to
    *   permit simple integer comparisons for decoding.
    *
    * - The first code for the shortest length is all ones.  Subsequent codes of
    *   the same length are simply integer decrements of the previous code.  When
    *   moving up a length, a one bit is appended to the code.  For a complete
    *   code, the last code of the longest length will be all zeros.  To support
    *   this ordering, the bits pulled during decoding are inverted to apply the
    *   more "natural" ordering starting with all zeros and incrementing.
    *
    * @param h Huffman table
    * @return status code
    */
   private int decode(Huffman h) throws IOException
   {
      int len; /* current number of bits in code */
      int code; /* len bits being decoded */
      int first; /* first code of length len */
      int count; /* number of codes of length len */
      int index; /* index of first code of length len in symbol table */
      int bitbuf; /* bits from stream */
      int left; /* bits left in next or left to process */
      //short *next;        /* next number of codes */

      bitbuf = m_bitbuf;
      left = m_bitcnt;
      code = first = index = 0;
      len = 1;
      int nextIndex = 1; // next = h->count + 1;
      while (true)
      {
         while (left-- != 0)
         {
            code |= (bitbuf & 1) ^ 1; /* invert code */
            bitbuf >>= 1;
            //count = *next++;
            count = h.m_count[nextIndex++];
            if (code < first + count)
            { /* if length len, return symbol */
               m_bitbuf = bitbuf;
               m_bitcnt = (m_bitcnt - len) & 7;
               return h.m_symbol[index + (code - first)];
            }
            index += count; /* else update for next length */
            first += count;
            first <<= 1;
            code <<= 1;
            len++;
         }
         left = (MAXBITS + 1) - len;
         if (left == 0)
         {
            break;
         }
         if (m_left == 0)
         {
            m_in = m_input.read();
            m_left = m_in == -1 ? 0 : 1;
            if (m_left == 0)
            {
               throw new IOException("out of input"); /* out of input */
            }
         }
         bitbuf = m_in;
         m_left--;
         if (left > 8)
         {
            left = 8;
         }
      }
      return -9; /* ran out of codes */
   }

   /**
    * Given a list of repeated code lengths rep[0..n-1], where each byte is a
    * count (high four bits + 1) and a code length (low four bits), generate the
    * list of code lengths.  This compaction reduces the size of the object code.
    * Then given the list of code lengths length[0..n-1] representing a canonical
    * Huffman code for n symbols, construct the tables required to decode those
    * codes.  Those tables are the number of codes of each length, and the symbols
    * sorted by length, retaining their original order within each length.  The
    * return value is zero for a complete code set, negative for an over-
    * subscribed code set, and positive for an incomplete code set.  The tables
    * can be used if the return value is zero or positive, but they cannot be used
    * if the return value is negative.  If the return value is zero, it is not
    * possible for decode() using that table to return an error--any stream of
    * enough bits will resolve to a symbol.  If the return value is positive, then
    * it is possible for decode() using that table to return an error for received
    * codes past the end of the incomplete lengths.
    *
    * @param h Huffman table
    * @param rep repeated code lengths
    * @param n number of repeated codes
    * @return zero if successful
    */
   private static int construct(Huffman h, int[] rep, int n)
   {
      int symbol; /* current symbol when stepping through length[] */
      int len; /* current length when stepping through h->count[] */
      int left; /* number of possible codes left of current length */
      short[] offs = new short[MAXBITS + 1]; /* offsets in symbol table for each length */
      short[] length = new short[256]; /* code lengths */

      /* convert compact repeat counts into symbol bit length list */
      symbol = 0;
      int repIndex = 0;
      do
      {
         len = rep[repIndex++];
         left = (len >> 4) + 1;
         len &= 15;
         do
         {
            length[symbol++] = (short) len;
         }
         while (--left != 0);
      }
      while (--n != 0);
      n = symbol;

      /* count number of codes of each length */
      for (len = 0; len <= MAXBITS; len++)
      {
         h.m_count[len] = 0;
      }

      for (symbol = 0; symbol < n; symbol++)
      {
         (h.m_count[length[symbol]])++; /* assumes lengths are within bounds */
      }

      if (h.m_count[0] == n) /* no codes! */
      {
         return 0; /* complete, but decode() will fail */
      }

      /* check for an over-subscribed or incomplete set of lengths */
      left = 1; /* one possible code of zero length */
      for (len = 1; len <= MAXBITS; len++)
      {
         left <<= 1; /* one more bit, double codes left */
         left -= h.m_count[len]; /* deduct count from possible codes */
         if (left < 0)
         {
            return left; /* over-subscribed--return negative */
         }
      } /* left > 0 means incomplete */

      /* generate offsets into symbol table for each length for sorting */
      offs[1] = 0;
      for (len = 1; len < MAXBITS; len++)
      {
         offs[len + 1] = (short) (offs[len] + h.m_count[len]);
      }

      /*
       * put symbols in table sorted by length, by symbol order within each
       * length
       */
      for (symbol = 0; symbol < n; symbol++)
      {
         if (length[symbol] != 0)
         {
            h.m_symbol[offs[length[symbol]]++] = (short) symbol;
         }
      }

      /* return zero for complete set, positive for incomplete set */
      return left;
   }

   private int m_bitbuf;
   private int m_bitcnt;
   private InputStream m_input;
   private int m_left;
   private int m_in;
   private int m_first;
   private int m_next;
   private final byte[] m_out = new byte[MAXWIN];

   private static final int MAXBITS = 13; /* maximum code length */
   private static final int MAXWIN = 4096; /* maximum window size */

   /* bit lengths of literal codes */
   private static final int[] LITLEN =
   {
      11,
      124,
      8,
      7,
      28,
      7,
      188,
      13,
      76,
      4,
      10,
      8,
      12,
      10,
      12,
      10,
      8,
      23,
      8,
      9,
      7,
      6,
      7,
      8,
      7,
      6,
      55,
      8,
      23,
      24,
      12,
      11,
      7,
      9,
      11,
      12,
      6,
      7,
      22,
      5,
      7,
      24,
      6,
      11,
      9,
      6,
      7,
      22,
      7,
      11,
      38,
      7,
      9,
      8,
      25,
      11,
      8,
      11,
      9,
      12,
      8,
      12,
      5,
      38,
      5,
      38,
      5,
      11,
      7,
      5,
      6,
      21,
      6,
      10,
      53,
      8,
      7,
      24,
      10,
      27,
      44,
      253,
      253,
      253,
      252,
      252,
      252,
      13,
      12,
      45,
      12,
      45,
      12,
      61,
      12,
      45,
      44,
      173
   };

   /* bit lengths of length codes 0..15 */
   private static final int[] LENLEN =
   {
      2,
      35,
      36,
      53,
      38,
      23
   };

   /* bit lengths of distance codes 0..63 */
   private static final int[] DISTLEN =
   {
      2,
      20,
      53,
      230,
      247,
      151,
      248
   };

   private static final short[] BASE =
   { /* base for length codes */
      3,
      2,
      4,
      5,
      6,
      7,
      8,
      9,
      10,
      12,
      16,
      24,
      40,
      72,
      136,
      264
   };

   private static final int[] EXTRA =
   { /* extra bits for length codes */
      0,
      0,
      0,
      0,
      0,
      0,
      0,
      0,
      1,
      2,
      3,
      4,
      5,
      6,
      7,
      8
   };

   private static final Huffman LITCODE = new Huffman(MAXBITS + 1, 256); /* length code */
   private static final Huffman LENCODE = new Huffman(MAXBITS + 1, 16); /* length code */
   private static final Huffman DISTCODE = new Huffman(MAXBITS + 1, 64);/* distance code */

   static
   {
      construct(LITCODE, LITLEN, LITLEN.length);
      construct(LENCODE, LENLEN, LENLEN.length);
      construct(DISTCODE, DISTLEN, DISTLEN.length);
   }
}

/**
 * Class to represent a Huffman table.
 */
class Huffman
{
   /**
    * Constructor.
    *
    * @param countSize number of counts
    * @param symbolSize number of symbols
    */
   public Huffman(int countSize, int symbolSize)
   {
      m_count = new short[countSize];
      m_symbol = new short[symbolSize];
   }

   final short[] m_count;
   final short[] m_symbol;
}
