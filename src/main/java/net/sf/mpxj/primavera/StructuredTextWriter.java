/*
 * file:       StructuredTextWriter.java
 * author:     Jon Iles
 * copyright:  (c) Packwood Software 2023
 * date:       07/03/2023
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

package net.sf.mpxj.primavera;

import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Writes a set of StructuredTextRecord instances as structured text.
 */
public class StructuredTextWriter
{
   /**
    * Write a StructuredTextRecord to the output stream using the default character set.
    *
    * @param stream output stream
    * @param record StructuredTextRecord instance
    */
   public void write(OutputStream stream, StructuredTextRecord record)  throws IOException
   {
      write(stream, record, StructuredTextParser.DEFAULT_CHARSET);
   }

   /**
    * Write a StructuredTextRecord to the output stream using the supplied character set.
    *
    * @param stream output stream
    * @param record StructuredTextRecord instance
    * @param charset character set
    */
   public void write(OutputStream stream, StructuredTextRecord record, Charset charset) throws IOException
   {
      m_writer = new OutputStreamWriter(stream, charset);
      write(record);
      m_writer.flush();
      m_writer = null;
   }

   /**
    * Recursively write StructuredTextRecord instances.
    *
    * @param record StructuredTextRecord instance
    */
   private void write(StructuredTextRecord record) throws IOException
   {
      m_writer.write("(");

      m_writer.write(record.getRecordNumber());
      m_writer.write("||");
      m_writer.write(record.getRecordName());

      m_writer.write("(");
      m_writer.write(record.getAttributes().entrySet().stream().filter(e -> !IGNORED_KEYS.contains(e.getKey())).map(e -> e.getKey() + "|" + e.getValue()).collect(Collectors.joining("|")));
      m_writer.write(")");

      m_writer.write("(");
      for (StructuredTextRecord child : record.getChildren())
      {
         write(child);
      }
      m_writer.write(")");

      m_writer.write(")");
   }

   private OutputStreamWriter m_writer;

   private static final Set<String> IGNORED_KEYS = new HashSet<>(Arrays.asList(StructuredTextRecord.RECORD_NAME_ATTRIBUTE, StructuredTextRecord.RECORD_NUMBER_ATTRIBUTE));
}
