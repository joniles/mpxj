
package net.sf.mpxj.primavera.suretrak;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import net.sf.mpxj.common.StreamHelper;
import net.sf.mpxj.mpp.MPPUtility;
import net.sf.mpxj.primavera.p3.ColumnDefinition;
import net.sf.mpxj.primavera.p3.Table;
import net.sf.mpxj.primavera.p3.TableDefinition;

public class TableReader
{
   /**
    * Constructor.
    *
    * @param definition table structure definition
    */
   public TableReader(TableDefinition definition)
   {
      m_definition = definition;
   }

   /**
    * Read the table from the file and populate the supplied Table instance.
    *
    * @param file database file
    * @param table Table instance
    */
   public void read(File file, Table table) throws IOException
   {
      System.out.println("Reading " + file.getName());
      InputStream is = null;
      try
      {
         is = new FileInputStream(file);
         read(is, table);
      }

      finally
      {
         StreamHelper.closeQuietly(is);
      }
   }

   /**
    * Read the table from an input stream and populate the supplied Table instance.
    *
    * @param is input stream from table file
    * @param table Table instance
    */
   private void read(InputStream is, Table table) throws IOException
   {
      byte[] headerBytes = new byte[6];
      is.read(headerBytes);

      byte[] recordCountBytes = new byte[2];
      is.read(recordCountBytes);
      int recordCount = getShort(recordCountBytes, 0);

      System.out.println("Header: " + new String(headerBytes) + " Record count:" + recordCount);

      byte[] buffer = new byte[m_definition.getRecordSize()];
      while (true)
      {
         int bytesRead = is.read(buffer);
         if (bytesRead == -1)
         {
            break;
         }

         if (bytesRead != buffer.length)
         {
            throw new IOException("Unexpected end of file");
         }

         if (buffer[0] == 0)
         {
            readRecord(buffer, table);
         }
      }
   }

   private void readRecord(byte[] buffer, Table table)
   {
      System.out.println(MPPUtility.hexdump(buffer, true, 16, ""));
      int deletedFlag = getShort(buffer, 0);
      if (deletedFlag != 0)
      {
         Map<String, Object> row = new HashMap<String, Object>();
         for (ColumnDefinition column : m_definition.getColumns())
         {
            Object value = column.read(0, buffer);
            System.out.println(column.getName() + ": " + value);
            row.put(column.getName(), value);
         }

         table.addRow(m_definition.getPrimaryKeyColumnName(), row);
      }
   }

   /**
    * Read a two byte integer from a byte array.
    *
    * @param data byte array
    * @param offset offset into byte array
    * @return int value
    */
   private int getShort(byte[] data, int offset)
   {
      int result = 0;
      int i = offset;
      for (int shiftBy = 0; shiftBy < 16; shiftBy += 8)
      {
         result |= ((data[i] & 0xff)) << shiftBy;
         ++i;
      }
      return result;
   }

   private final TableDefinition m_definition;
}
