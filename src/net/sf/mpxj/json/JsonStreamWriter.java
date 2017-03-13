/*
 * file:       JsonStreamWriter.java
 * author:     Jon Iles
 * copyright:  (c) Packwood Software 2015
 * date:       18/02/2015
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

package net.sf.mpxj.json;

import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.nio.charset.Charset;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Deque;
import java.util.LinkedList;

/**
 * Writes JSON data to an output stream.
 */
public class JsonStreamWriter
{
   /**
    * Constructor.
    *
    * @param stream target output stream
    * @param encoding target encoding
    */
   public JsonStreamWriter(OutputStream stream, Charset encoding)
   {
      m_writer = new OutputStreamWriter(stream, encoding);
      m_firstNameValuePair.push(Boolean.TRUE);
   }

   /**
    * Retrieve the pretty-print flag.
    *
    * @return true if pretty printing is enabled
    */
   public boolean getPretty()
   {
      return m_pretty;
   }

   /**
    * Set the pretty-print flag.
    *
    * @param pretty true if pretty printing is enabled
    */
   public void setPretty(boolean pretty)
   {
      m_pretty = pretty;
   }

   /**
    * Flush the output stream.
    */
   public void flush() throws IOException
   {
      m_writer.flush();
   }

   /**
    * Begin writing a named object attribute.
    *
    * @param name attribute name
    */
   public void writeStartObject(String name) throws IOException
   {
      writeComma();
      writeNewLineIndent();

      if (name != null)
      {
         writeName(name);
         writeNewLineIndent();
      }

      m_writer.write("{");
      increaseIndent();
   }

   /**
    * Begin writing a named list attribute.
    *
    * @param name attribute name
    */
   public void writeStartList(String name) throws IOException
   {
      writeComma();
      writeNewLineIndent();
      writeName(name);
      writeNewLineIndent();
      m_writer.write("[");
      increaseIndent();
   }

   /**
    * End writing an object.
    */
   public void writeEndObject() throws IOException
   {
      decreaseIndent();
      m_writer.write("}");
   }

   /**
    * End writing a list.
    */
   public void writeEndList() throws IOException
   {
      decreaseIndent();
      m_writer.write("]");
   }

   /**
    * Write a string attribute.
    *
    * @param name attribute name
    * @param value attribute value
    */
   public void writeNameValuePair(String name, String value) throws IOException
   {
      internalWriteNameValuePair(name, escapeString(value));
   }

   /**
    * Write an int attribute.
    *
    * @param name attribute name
    * @param value attribute value
    */
   public void writeNameValuePair(String name, int value) throws IOException
   {
      internalWriteNameValuePair(name, Integer.toString(value));
   }

   /**
    * Write a long attribute.
    *
    * @param name attribute name
    * @param value attribute value
    */
   public void writeNameValuePair(String name, long value) throws IOException
   {
      internalWriteNameValuePair(name, Long.toString(value));
   }

   /**
    * Write a double attribute.
    *
    * @param name attribute name
    * @param value attribute value
    */
   public void writeNameValuePair(String name, double value) throws IOException
   {
      internalWriteNameValuePair(name, Double.toString(value));
   }

   /**
    * Write a boolean attribute.
    *
    * @param name attribute name
    * @param value attribute value
    */
   public void writeNameValuePair(String name, boolean value) throws IOException
   {
      internalWriteNameValuePair(name, value ? "true" : "false");
   }

   /**
    * Write a Date attribute.
    *
    * @param name attribute name
    * @param value attribute value
    */
   public void writeNameValuePair(String name, Date value) throws IOException
   {
      internalWriteNameValuePair(name, m_format.format(value));
   }

   /**
    * Core write attribute implementation.
    *
    * @param name attribute name
    * @param value attribute value
    */
   private void internalWriteNameValuePair(String name, String value) throws IOException
   {
      writeComma();
      writeNewLineIndent();
      writeName(name);

      if (m_pretty)
      {
         m_writer.write(' ');
      }

      m_writer.write(value);
   }

   /**
    * Escape text to ensure valid JSON.
    *
    * @param value value
    * @return escaped value
    */
   private String escapeString(String value)
   {
      m_buffer.setLength(0);
      m_buffer.append('"');
      for (int index = 0; index < value.length(); index++)
      {
         char c = value.charAt(index);
         switch (c)
         {
            case '"':
            {
               m_buffer.append("\\\"");
               break;
            }

            case '\\':
            {
               m_buffer.append("\\\\");
               break;
            }

            case '/':
            {
               m_buffer.append("\\/");
               break;
            }

            case '\b':
            {
               m_buffer.append("\\b");
               break;
            }

            case '\f':
            {
               m_buffer.append("\\f");
               break;
            }

            case '\n':
            {
               m_buffer.append("\\n");
               break;
            }

            case '\r':
            {
               m_buffer.append("\\r");
               break;
            }

            case '\t':
            {
               m_buffer.append("\\t");
               break;
            }

            default:
            {
               // Append if it's not a control character (0x00 to 0x1f)
               if (c > 0x1f)
               {
                  m_buffer.append(c);
               }
               break;
            }
         }
      }
      m_buffer.append('"');
      return m_buffer.toString();
   }

   /**
    * Write a comma to the output stream if required.
    */
   private void writeComma() throws IOException
   {
      if (m_firstNameValuePair.peek().booleanValue())
      {
         m_firstNameValuePair.pop();
         m_firstNameValuePair.push(Boolean.FALSE);
      }
      else
      {
         m_writer.write(',');
      }
   }

   /**
    * Write a new line and indent.
    */
   private void writeNewLineIndent() throws IOException
   {
      if (m_pretty)
      {
         if (!m_indent.isEmpty())
         {
            m_writer.write('\n');
            m_writer.write(m_indent);
         }
      }
   }

   /**
    * Write an attribute name.
    *
    * @param name attribute name
    */
   private void writeName(String name) throws IOException
   {
      m_writer.write('"');
      m_writer.write(name);
      m_writer.write('"');
      m_writer.write(":");
   }

   /**
    * Increase the indent level.
    */
   private void increaseIndent()
   {
      m_firstNameValuePair.push(Boolean.TRUE);
      if (m_pretty)
      {
         m_indent += INDENT;
      }
   }

   /**
    * Decrease the indent level.
    */
   private void decreaseIndent() throws IOException
   {
      if (m_pretty)
      {
         m_writer.write('\n');
         m_indent = m_indent.substring(0, m_indent.length() - INDENT.length());
         m_writer.write(m_indent);
      }
      m_firstNameValuePair.pop();
   }

   private final StringBuilder m_buffer = new StringBuilder();
   private final OutputStreamWriter m_writer;
   private final Deque<Boolean> m_firstNameValuePair = new LinkedList<Boolean>();
   private boolean m_pretty;
   private String m_indent = "";
   private DateFormat m_format = new SimpleDateFormat("\"yyyy-MM-dd'T'HH:mm:ss.S\"");

   private static final String INDENT = "  ";
}
