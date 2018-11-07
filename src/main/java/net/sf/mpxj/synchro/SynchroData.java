/*
 * file:       SynchroData.java
 * author:     Jon Iles
 * copyright:  (c) Packwood Software 2018
 * date:       2018-10-11
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

package net.sf.mpxj.synchro;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.zip.DataFormatException;
import java.util.zip.Inflater;

import net.sf.mpxj.common.StreamHelper;

/**
 * Reads the raw table data from an S file, ready to be processed.
 * Note that we only extract data for the tables we're going to read.
 */
class SynchroData
{
   /**
    * Extract raw table data from the input stream.
    *
    * @param is input stream
    */
   public void process(InputStream is) throws Exception
   {
      byte[] header = new byte[20];
      is.read(header);
      m_offset += 20;
      SynchroLogger.log("HEADER", header);

      String version = DatatypeConverter.getString(is);
      m_offset += (2 + version.length()); // Assumes version is always < 255 bytes!
      SynchroLogger.log("VERSION", version);

      readTableData(readTableHeaders(is), is);
   }

   /**
    * Return an input stream to read the data from the named table.
    *
    * @param name table name
    * @return InputStream instance
    */
   public InputStream getTableData(String name)
   {
      return new ByteArrayInputStream(m_tableData.get(name));
   }

   /**
    * Read the table headers. This allows us to break the file into chunks
    * representing the individual tables.
    *
    * @param is input stream
    * @return list of tables in the file
    */
   private List<SynchroTable> readTableHeaders(InputStream is) throws IOException
   {
      // Read the headers
      List<SynchroTable> tables = new ArrayList<SynchroTable>();
      byte[] header = new byte[48];
      while (true)
      {
         is.read(header);
         m_offset += 48;
         SynchroTable table = readTableHeader(header);
         if (table == null)
         {
            break;
         }
         tables.add(table);
      }

      // Ensure sorted by offset
      Collections.sort(tables, new Comparator<SynchroTable>()
      {
         @Override public int compare(SynchroTable o1, SynchroTable o2)
         {
            return o1.getOffset() - o2.getOffset();
         }
      });

      // Calculate lengths
      SynchroTable previousTable = null;
      for (SynchroTable table : tables)
      {
         if (previousTable != null)
         {
            previousTable.setLength(table.getOffset() - previousTable.getOffset());
         }

         previousTable = table;
      }

      for (SynchroTable table : tables)
      {
         SynchroLogger.log("TABLE", table);
      }

      return tables;
   }

   /**
    * Read the header data for a single file.
    *
    * @param header header data
    * @return SynchroTable instance
    */
   private SynchroTable readTableHeader(byte[] header)
   {
      SynchroTable result = null;
      String tableName = DatatypeConverter.getSimpleString(header, 0);
      if (!tableName.isEmpty())
      {
         int offset = DatatypeConverter.getInt(header, 40);
         result = new SynchroTable(tableName, offset);
      }
      return result;
   }

   /**
    * Read the data for all of the tables we're interested in.
    *
    * @param tables list of all available tables
    * @param is input stream
    */
   private void readTableData(List<SynchroTable> tables, InputStream is) throws IOException
   {
      for (SynchroTable table : tables)
      {
         if (REQUIRED_TABLES.contains(table.getName()))
         {
            readTable(is, table);
         }
      }
   }

   /**
    * Read data for a single table and store it.
    *
    * @param is input stream
    * @param table table header
    */
   private void readTable(InputStream is, SynchroTable table) throws IOException
   {
      int skip = table.getOffset() - m_offset;
      if (skip != 0)
      {
         StreamHelper.skip(is, skip);
         m_offset += skip;
      }

      String tableName = DatatypeConverter.getString(is);
      int tableNameLength = 2 + tableName.length();
      m_offset += tableNameLength;

      int dataLength;
      if (table.getLength() == -1)
      {
         dataLength = is.available();
      }
      else
      {
         dataLength = table.getLength() - tableNameLength;
      }

      SynchroLogger.log("READ", tableName);

      byte[] compressedTableData = new byte[dataLength];
      is.read(compressedTableData);
      m_offset += dataLength;

      Inflater inflater = new Inflater();
      inflater.setInput(compressedTableData);
      ByteArrayOutputStream outputStream = new ByteArrayOutputStream(compressedTableData.length);
      byte[] buffer = new byte[1024];
      while (!inflater.finished())
      {
         int count;

         try
         {
            count = inflater.inflate(buffer);
         }
         catch (DataFormatException ex)
         {
            throw new IOException(ex);
         }
         outputStream.write(buffer, 0, count);
      }
      outputStream.close();
      byte[] uncompressedTableData = outputStream.toByteArray();

      SynchroLogger.log(uncompressedTableData);

      m_tableData.put(table.getName(), uncompressedTableData);
   }

   private int m_offset;
   private Map<String, byte[]> m_tableData = new HashMap<String, byte[]>();
   private static final Set<String> REQUIRED_TABLES = new HashSet<String>(Arrays.asList("Tasks", "Calendars", "Companies"));
}
