/*
 * file:       ReaderTokenizerTest.java
 * author:     Petr Janeček
 * date:       2026-07-29
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

package org.mpxj.junit;

import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.mpxj.common.ReaderTokenizer;
import org.mpxj.common.Tokenizer;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * ReaderTokenizer tests, covering the block buffer it reads characters through.
 */
public class ReaderTokenizerTest
{
   /**
    * Test that empty input yields no tokens.
    *
    * @throws IOException if the tokenizer cannot read
    */
   @Test public void emptyInput() throws IOException
   {
      assertEquals(Collections.emptyList(), tokenize(""));
   }

   /**
    * Test that reading past the end keeps reporting the end.
    *
    * @throws IOException if the tokenizer cannot read
    */
   @Test public void repeatedReadsPastTheEnd() throws IOException
   {
      Tokenizer tokenizer = tokenizerFor("a");
      assertEquals(Tokenizer.TT_WORD, tokenizer.nextToken());
      assertEquals("a", tokenizer.getToken());

      for (int index = 0; index < 3; index++)
      {
         assertEquals(Tokenizer.TT_EOF, tokenizer.nextToken());
      }
   }

   /**
    * Test input shorter than, equal to and longer than the read buffer, with fields of
    * increasing width so that a token straddles each buffer boundary.
    *
    * @throws IOException if the tokenizer cannot read
    */
   @Test public void inputSpanningTheReadBuffer() throws IOException
   {
      int[] lengths = new int[]
      {
         1,
         8191,
         8192,
         8193,
         24576
      };

      for (int length : lengths)
      {
         List<String> expected = fieldsTotalling(length);
         assertEquals(expected, tokenize(join(expected, '\t')), "length " + length);
      }
   }

   /**
    * Test that a line ending is still recognised when it falls on a buffer boundary.
    *
    * @throws IOException if the tokenizer cannot read
    */
   @Test public void lineEndingOnTheBufferBoundary() throws IOException
   {
      String field = repeat('x', 8191);

      Tokenizer tokenizer = tokenizerFor(field + "\r\na\tb");
      assertEquals(Tokenizer.TT_WORD, tokenizer.nextToken());
      assertEquals(field, tokenizer.getToken());
      assertEquals(Tokenizer.TT_EOL, tokenizer.nextToken());
      assertEquals(Arrays.asList("a", "b"), remainingWords(tokenizer));
   }

   /**
    * Tab-separated fields of doubling width whose joined length is as given.
    *
    * @param length total length of the joined fields
    * @return field values
    */
   private List<String> fieldsTotalling(int length)
   {
      List<String> fields = new ArrayList<>();
      int remaining = length;
      int width = 1;
      while (remaining > 0)
      {
         int size = Math.min(width, remaining);
         fields.add(repeat('a', size));
         remaining -= size;
         if (remaining > 0)
         {
            remaining--; // the delimiter between this field and the next
         }
         width = (width * 2) + 1;
      }
      return fields;
   }

   /**
    * Every word token the tokenizer reads from the given text.
    *
    * @param text text to tokenize
    * @return token values
    * @throws IOException if the tokenizer cannot read
    */
   private List<String> tokenize(String text) throws IOException
   {
      return remainingWords(tokenizerFor(text));
   }

   /**
    * Every word token left in the given tokenizer.
    *
    * @param tokenizer tokenizer to drain
    * @return token values
    * @throws IOException if the tokenizer cannot read
    */
   private List<String> remainingWords(Tokenizer tokenizer) throws IOException
   {
      List<String> tokens = new ArrayList<>();
      while (tokenizer.nextToken() != Tokenizer.TT_EOF)
      {
         if (tokenizer.getType() == Tokenizer.TT_WORD)
         {
            tokens.add(tokenizer.getToken());
         }
      }
      return tokens;
   }

   /**
    * A tab-delimited tokenizer over the given text.
    *
    * @param text text to tokenize
    * @return tokenizer instance
    */
   private Tokenizer tokenizerFor(String text)
   {
      Tokenizer tokenizer = new ReaderTokenizer(new StringReader(text));
      tokenizer.setDelimiter('\t');
      return tokenizer;
   }

   /**
    * The given values joined by the given delimiter.
    *
    * @param values values to join
    * @param delimiter delimiter to join them with
    * @return joined value
    */
   private String join(List<String> values, char delimiter)
   {
      StringBuilder result = new StringBuilder();
      for (String value : values)
      {
         if (result.length() != 0)
         {
            result.append(delimiter);
         }
         result.append(value);
      }
      return result.toString();
   }

   /**
    * A string of the given character repeated.
    *
    * @param character character to repeat
    * @param count number of repetitions
    * @return repeated value
    */
   private String repeat(char character, int count)
   {
      char[] characters = new char[count];
      Arrays.fill(characters, character);
      return new String(characters);
   }
}
