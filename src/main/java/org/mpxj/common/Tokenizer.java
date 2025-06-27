/*
 * file:       Tokenizer.java
 * author:     Jon Iles
 * copyright:  (c) Packwood Software 2002-2003
 * date:       03/01/2003
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

package org.mpxj.common;

import java.io.IOException;

/**
 * This class implements a tokenizer based loosely on
 * java.io.StreamTokenizer. This tokenizer is designed to parse records from
 * an MPX file correctly. In particular, it will handle empty fields,
 * represented by adjacent field delimiters.
 */
public abstract class Tokenizer
{
   /**
    * This method must be implemented to read the next character from the
    * data source.
    *
    * @return next character
    */
   protected abstract int read() throws IOException;

   /**
    * This method retrieves the next token and returns a constant representing
    * the type of token found.
    *
    * @return token type value
    */
   public int nextToken() throws IOException
   {
      int c;
      boolean quoted = false;
      int result = m_nextToken;
      if (m_nextToken != 0)
      {
         m_nextToken = 0;
      }

      m_buffer.setLength(0);

      while (result == 0)
      {
         if (m_nextCharacter != -1)
         {
            c = m_nextCharacter;
            m_nextCharacter = -1;
         }
         else
         {
            c = read();
         }

         switch (c)
         {
            case TT_EOF:
            {
               if (m_buffer.length() != 0)
               {
                  result = TT_WORD;
                  m_nextToken = TT_EOF;
               }
               else
               {
                  result = TT_EOF;
               }
               break;
            }

            case '\r':
            {
               m_nextCharacter = read();
               if (m_nextCharacter == '\n')
               {
                  m_nextCharacter = -1;
               }

               int length = m_buffer.length();
               if (length == 0)
               {
                  result = TT_EOL;
               }
               else
               {
                  result = TT_WORD;
                  m_nextToken = TT_EOL;
               }

               break;
            }

            case '\n':
            {
               int length = m_buffer.length();
               if (length == 0)
               {
                  result = TT_EOL;
               }
               else
               {
                  result = TT_WORD;
                  m_nextToken = TT_EOL;
               }

               break;
            }

            default:
            {
               char quote = '"';
               if (c == quote)
               {
                  if (!quoted && startQuotedIsValid(m_buffer))
                  {
                     quoted = true;
                  }
                  else
                  {
                     if (!quoted)
                     {
                        m_buffer.append((char) c);
                     }
                     else
                     {
                        m_nextCharacter = read();
                        if (m_nextCharacter == quote)
                        {
                           m_buffer.append((char) c);
                           m_nextCharacter = -1;
                        }
                        else
                        {
                           quoted = false;
                        }
                     }
                  }
               }
               else
               {
                  if (c == m_delimiter && !quoted)
                  {
                     result = TT_WORD;
                  }
                  else
                  {
                     m_buffer.append((char) c);
                  }
               }
            }
         }
      }

      m_type = result;

      return result;
   }

   /**
    * This method allows us to control the behaviour of the tokenizer for
    * quoted text. Normally quoted text begins with a quote character
    * at the first position within a field. As this method is protected,
    * subclasses can alter this behaviour if required.
    *
    * @param buffer the field contents read so far
    * @return true if it is valid to treat the subsequent text as quoted
    */
   protected boolean startQuotedIsValid(StringBuilder buffer)
   {
      return buffer.length() == 0;
   }

   /**
    * This method retrieves the text of the last token found.
    *
    * @return last token text
    */
   public String getToken()
   {
      return m_buffer.toString();
   }

   /**
    * This method retrieves the type of the last token found.
    *
    * @return last token type
    */
   public int getType()
   {
      return m_type;
   }

   /**
    * This method is used to set the delimiter character recognised
    * by the tokenizer.
    *
    * @param delimiter delimiter character
    */
   public void setDelimiter(char delimiter)
   {
      m_delimiter = delimiter;
   }

   public static final int TT_EOL = '\n';
   public static final int TT_EOF = -1;
   public static final int TT_WORD = -3;

   private char m_delimiter = ',';
   private int m_nextToken;
   private int m_nextCharacter = -1;
   private int m_type;
   private final StringBuilder m_buffer = new StringBuilder();
}
