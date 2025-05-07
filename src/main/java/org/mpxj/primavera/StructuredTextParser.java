/*
 * file:       StructuredTextParser.java
 * author:     Jon Iles
 * copyright:  (c) Packwood Software 2022
 * date:       06/02/2022
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

package org.mpxj.primavera;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PushbackReader;
import java.io.StringReader;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.mpxj.common.CharsetHelper;

/**
 * Parses structured text and returns a StructuredTextRecord
 * representing the data encoded as structured text.
 * See {@code StructuredTextRecord} for a brief description of
* the structured text format.
 */
public class StructuredTextParser
{
   /**
    * By default, if unexpected structure is encountered while parsing
    * structured text, the parser will raise a {@code StructuredTextParseException}.
    * In some cases the structured text already parsed will have yielded useful data.
    * Setting this flag to {@code true} will allow the parser to fail silently
    * and return as much of the data as it has been able to extract.
    *
    * @param raiseExceptionOnParseError set to true to ignore structure errors
    */
   public void setRaiseExceptionOnParseError(boolean raiseExceptionOnParseError)
   {
      m_raiseExceptionOnParseError = raiseExceptionOnParseError;
   }

   /**
    * Parse structured text from an {@code InputStream} and
    * return the resulting data. The default character set
    * (Windows 1252) is used.
    *
    * @param is {@code InputStream} instance
    * @return {@code StructuredTextRecord} instance
    */
   public StructuredTextRecord parse(InputStream is)
   {
      return parse(is, DEFAULT_CHARSET);
   }

   /**
    * Parse structured text from an {@code InputStream} and
    * return the resulting data. The caller supplies the
    * character set to be used.
    *
    * @param is {@code InputStream} instance
    * @param charset character set to use
    * @return {@code StructuredTextRecord} instance
    */
   public StructuredTextRecord parse(InputStream is, Charset charset)
   {
      try
      {
         return parse(new PushbackReader(new InputStreamReader(is, charset)));
      }

      catch (IOException ex)
      {
         throw new RuntimeException(ex);
      }
   }

   /**
    * Parse structured text from a {@code String} and
    * return the resulting data.
    *
    * @param text {@code String} containing structured text
    * @return {@code StructuredTextRecord} instance
    */
   public StructuredTextRecord parse(String text)
   {
      try
      {
         return parse(new PushbackReader(new StringReader(text)));
      }

      catch (IOException ex)
      {
         throw new RuntimeException(ex);
      }
   }

   /**
    * Configure the {@code PushbackReader} used by the parser, then parse.
    *
    * @param reader source for the structured text
    * @return {@code StructuredTextRecord} instance
    */
   private StructuredTextRecord parse(PushbackReader reader) throws IOException
   {
      m_reader = reader;
      return parse();
   }

   /**
    * Parse structure text.
    *
    * @return {@code StructuredTextRecord} instance
    */
   private StructuredTextRecord parse() throws IOException
   {
      Map<String, String> attributes = new HashMap<>();
      List<StructuredTextRecord> children = new ArrayList<>();

      try
      {
         while (true)
         {
            //
            // Find the start of the record and extract the record number.
            //
            char c = skipWhitespaceAndRead();
            if (c == ')')
            {
               break;
            }

            if (c != '(')
            {
               throw new StructuredTextParseException("Unexpected character: '" + c + "' expecting record start '('");
            }

            m_buffer.setLength(0);
            while (true)
            {
               c = read();
               if (!Character.isDigit(c))
               {
                  break;
               }
               m_buffer.append(c);
            }

            if (m_buffer.length() == 0)
            {
               throw new StructuredTextParseException("Missing record number");
            }
            attributes.put(StructuredTextRecord.RECORD_NUMBER_ATTRIBUTE, m_buffer.toString());

            //
            // Find the separator between the record number and record name
            //
            m_buffer.setLength(0);
            c = skipWhitespaceAndRead(c);
            while (c == '|')
            {
               m_buffer.append(c);
               c = read();
            }

            if (!m_buffer.toString().equals("||"))
            {
               throw new StructuredTextParseException("Missing record separator '||'");
            }

            //
            // Extract the record name if it is present
            //
            m_buffer.setLength(0);
            c = skipWhitespaceAndRead(c);
            while (c != '(')
            {
               m_buffer.append(c);
               c = read();
            }
            if (m_buffer.length() > 0)
            {
               attributes.put(StructuredTextRecord.RECORD_NAME_ATTRIBUTE, m_buffer.toString());
            }

            //
            // Extract any attributes as name/value pairs
            //
            c = read();
            while (c != ')')
            {
               m_buffer.setLength(0);
               while (c != '|')
               {
                  m_buffer.append(c);
                  c = read();
               }
               String fieldName = m_buffer.toString();

               m_buffer.setLength(0);
               c = read();
               while (c != '|' && c != ')')
               {
                  m_buffer.append(c);
                  c = read();
               }

               if (c == '|')
               {
                  c = read();
               }
               attributes.put(fieldName, m_buffer.toString());
            }

            //
            // Extract any child records
            //
            c = skipWhitespaceAndRead();
            if (c != '(')
            {
               throw new StructuredTextParseException("Unexpected character: '" + c + "' expecting child records start '('");
            }

            while (true)
            {
               c = skipWhitespaceAndRead();
               m_reader.unread(c);
               if (c != '(')
               {
                  break;
               }

               children.add(parse());
            }

            c = skipWhitespaceAndRead();
            if (c != ')')
            {
               throw new StructuredTextParseException("Unexpected character: '" + c + "' expecting child records end ')'");
            }
         }
      }

      catch (StructuredTextParseException ex)
      {
         if (m_raiseExceptionOnParseError)
         {
            throw ex;
         }
      }

      return new StructuredTextRecord(attributes, children);
   }

   /**
    * Skip whitespace and read the next character.
    *
    * @return next non-whitespace character
    */
   private char skipWhitespaceAndRead() throws IOException
   {
      return skipWhitespaceAndRead(read());
   }

   /**
    * Skip whitespace and read the next character.
    * We've already read a character at this point. Rather than
    * {@code unread} it, we'll check it here to see if we need to skip
    * it or return it.
    *
    * @param c next character already read
    * @return next non-whitespace character
    */
   private char skipWhitespaceAndRead(char c) throws IOException
   {
      while (Character.isWhitespace(c) || Character.isISOControl(c))
      {
         c = read();
      }
      return c;
   }

   /**
    * Read the next character, raise an exception is we reach the
    * end of the stream.
    *
    * @return next character
    */
   private char read() throws IOException
   {
      int c = m_reader.read();
      if (c == -1)
      {
         throw new StructuredTextParseException("End of stream reached");
      }
      return (char) c;
   }

   private boolean m_raiseExceptionOnParseError = true;
   private PushbackReader m_reader;
   private final StringBuilder m_buffer = new StringBuilder();

   public static final Charset DEFAULT_CHARSET = CharsetHelper.CP1252;
}
