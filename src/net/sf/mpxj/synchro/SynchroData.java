
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

class SynchroData
{
   public void process(InputStream is) throws Exception
   {
      byte[] header = new byte[20];
      is.read(header);
      m_offset += 20;
      SynchroLogger.log("HEADER", header);

      String version = SynchroUtility.getString(is);
      m_offset += (2 + version.length()); // Assumes version is always < 255 bytes!
      SynchroLogger.log("VERSION", version);

      readTableData(readTableHeaders(is), is);
   }

   public InputStream getTableData(String name)
   {
      return new ByteArrayInputStream(m_tableData.get(name));
   }

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

   private SynchroTable readTableHeader(byte[] header)
   {
      SynchroTable result = null;
      String tableName = SynchroUtility.getSimpleString(header, 0);
      if (!tableName.isEmpty())
      {
         int offset = SynchroUtility.getInt(header, 40);
         result = new SynchroTable(tableName, offset);
      }
      return result;
   }

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

   private void readTable(InputStream is, SynchroTable table) throws IOException
   {
      int skip = table.getOffset() - m_offset;
      if (skip != 0)
      {
         StreamHelper.skip(is, skip);
         m_offset += skip;
      }

      String tableName = SynchroUtility.getString(is);
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
