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

package org.mpxj.primavera;

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
    * Return a string representing the StructuredTextRecord instance in structured text format.
    *
    * @param record StructuredTextRecord instance
    * @return string representation
    */
   public String write(StructuredTextRecord record)
   {
      m_buffer = new StringBuilder();
      writeRecord(record);
      String result = m_buffer.toString();
      m_buffer = null;
      return result;
   }

   /**
    * Recursively write StructuredTextRecord instances.
    *
    * @param record StructuredTextRecord instance
    */
   private void writeRecord(StructuredTextRecord record)
   {

      m_buffer.append("(");

      m_buffer.append(record.getRecordNumber());
      m_buffer.append("||");
      m_buffer.append(record.getRecordName());

      m_buffer.append("(");
      m_buffer.append(record.getAttributes().entrySet().stream().filter(e -> !IGNORED_KEYS.contains(e.getKey())).map(e -> e.getKey() + "|" + e.getValue()).collect(Collectors.joining("|")));
      m_buffer.append(")");

      m_buffer.append("(");
      for (StructuredTextRecord child : record.getChildren())
      {
         writeRecord(child);
      }
      m_buffer.append(")");

      m_buffer.append(")");
   }

   private StringBuilder m_buffer;

   private static final Set<String> IGNORED_KEYS = new HashSet<>(Arrays.asList(StructuredTextRecord.RECORD_NAME_ATTRIBUTE, StructuredTextRecord.RECORD_NUMBER_ATTRIBUTE));
}
