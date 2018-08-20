
package net.sf.mpxj.synchro;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.zip.DataFormatException;
import java.util.zip.Inflater;

import net.sf.mpxj.common.StreamHelper;
import net.sf.mpxj.mpp.MPPUtility;

class SynchroData
{
   public void process(InputStream is) throws Exception
   {
      byte[] header = new byte[20];
      is.read(header);
      m_offset += 20;
      System.out.println("HEADER: " + MPPUtility.hexdump(header, true));

      String version = getStringWithLength(is);
      m_offset += (2 + version.length());
      System.out.println("VERSION: " + version);

      readTableHeaders(is);
      readTableData(is);
   }

   private void readTableHeaders(InputStream is) throws IOException
   {
      // Read the headers
      m_tables = new ArrayList<SynchroTable>();
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
         m_tables.add(table);
      }

      // Ensure sorted by offset
      Collections.sort(m_tables, new Comparator<SynchroTable>()
      {
         @Override public int compare(SynchroTable o1, SynchroTable o2)
         {
            return o1.getOffset() - o2.getOffset();
         }
      });

      // Calculate lengths
      SynchroTable previousTable = null;
      for (SynchroTable table : m_tables)
      {
         if (previousTable != null)
         {
            previousTable.setLength(table.getOffset() - previousTable.getOffset());
         }

         previousTable = table;
      }

      for (SynchroTable table : m_tables)
      {
         System.out.println("OFFSET: " + table.getOffset() + "\tLENGTH: " + table.getLength() + "\tTABLE: " + table.getName());
      }
   }

   private SynchroTable readTableHeader(byte[] header)
   {
      SynchroTable result = null;
      String tableName = getString(header, 0);
      if (!tableName.isEmpty())
      {
         int offset = getInt(header, 40);
         result = new SynchroTable(tableName, offset);
      }
      return result;
   }

   private void readTableData(InputStream is) throws IOException
   {
      for (SynchroTable table : m_tables)
      {
         readTable(is, table);
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

      String tableName = getStringWithLength(is);
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

      System.out.println("READ: " + tableName);

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

      System.out.println(MPPUtility.hexdump(uncompressedTableData, true, 16, ""));
   }

   private String getStringWithLength(InputStream is) throws IOException
   {
      int type = is.read();
      if (type != 1)
      {
         throw new RuntimeException("Unexpected string format");
      }

      int length = is.read();
      byte[] stringData = new byte[length];
      is.read(stringData);
      return new String(stringData);
   }

   private String getString(byte[] data, int offset)
   {
      StringBuilder buffer = new StringBuilder();
      char c;

      for (int loop = 0; offset + loop < data.length; loop++)
      {
         c = (char) data[offset + loop];

         if (c == 0)
         {
            break;
         }

         buffer.append(c);
      }

      return (buffer.toString());
   }

   private int getInt(byte[] data, int offset)
   {
      int result = 0;
      int i = offset;
      for (int shiftBy = 0; shiftBy < 32; shiftBy += 8)
      {
         result |= ((data[i] & 0xff)) << shiftBy;
         ++i;
      }
      return result;
   }

   private int m_offset;
   private List<SynchroTable> m_tables;
}
