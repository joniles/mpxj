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

package org.mpxj.synchro;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.zip.DataFormatException;
import java.util.zip.Inflater;

import org.mpxj.common.InputStreamHelper;
import org.mpxj.common.SemVer;

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
      readHeader(is);
      readVersion(is);
      readTableData(readTableHeaders(is), is);
   }

   /**
    * Retrieve the file version.
    *
    * @return file version
    */
   public SemVer getVersion()
   {
      return m_version;
   }

   /**
    * Return an input stream to read the data from the named table.
    *
    * @param name table name
    * @return InputStream instance
    */
   public StreamReader getTableData(String name) throws IOException
   {
      InputStream stream = new ByteArrayInputStream(m_tableData.get(name));
      if (m_version.atLeast(Synchro.VERSION_6_0_0))
      {
         SynchroLogger.log("TABLE HEADER", InputStreamHelper.read(stream, 24));
      }
      return new StreamReader(m_version, stream);
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
      List<SynchroTable> tables = new ArrayList<>();
      byte[] header = new byte[48];
      while (true)
      {
         InputStreamHelper.read(is, header);
         m_offset += 48;
         SynchroTable table = readTableHeader(header);
         if (table == null)
         {
            break;
         }
         tables.add(table);
      }

      // Ensure sorted by offset
      tables.sort(Comparator.comparingInt(SynchroTable::getOffset));

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
         InputStreamHelper.skip(is, skip);
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

      byte[] compressedTableData = InputStreamHelper.read(is, dataLength);
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

   /**
    * Read the file header data.
    *
    * @param is input stream
    */
   private void readHeader(InputStream is) throws IOException
   {
      byte[] header = InputStreamHelper.read(is, 20);
      m_offset += 20;
      SynchroLogger.log("HEADER", header);
   }

   /**
    * Read the version number.
    *
    * @param is input stream
    */
   private void readVersion(InputStream is) throws IOException
   {
      BytesReadInputStream bytesReadStream = new BytesReadInputStream(is);
      String version = DatatypeConverter.getString(bytesReadStream);
      m_offset += bytesReadStream.getBytesRead();
      SynchroLogger.log("VERSION", version);
      m_version = new SemVer(version);
   }

   private SemVer m_version;
   private int m_offset;
   private final Map<String, byte[]> m_tableData = new HashMap<>();
   private static final Set<String> REQUIRED_TABLES = new HashSet<>(Arrays.asList("Tasks", "Calendars", "Companies", "Resources"));
}
