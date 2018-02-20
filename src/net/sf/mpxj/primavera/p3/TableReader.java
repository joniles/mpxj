
package net.sf.mpxj.primavera.p3;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import net.sf.mpxj.mpp.MPPUtility;

public class TableReader
{
   public TableReader(TableDefinition definition)
   {
      m_definition = definition;
   }

   public void read(File file, Table table) throws IOException
   {
      InputStream is = null;
      try
      {
         is = new FileInputStream(file);
         read(is, table);
      }

      finally
      {
         if (is != null)
         {
            try
            {
               is.close();
            }

            catch (IOException ex)
            {
               // Ignore
            }
         }
      }
   }

   private void read(InputStream is, Table table) throws IOException
   {
      byte[] buffer = new byte[m_definition.getPageSize()];
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

         readPage(buffer, table);
      }
   }

   private void readPage(byte[] buffer, Table table)
   {
      int magicNumber = getShort(buffer, 0);
      if (magicNumber == 0x4400)
      {
         System.out.println(MPPUtility.hexdump(buffer, 0, 6, true, 16, ""));
         int recordSize = m_definition.getRecordSize();

         int index = 6;
         while (index + recordSize <= buffer.length)
         {
            System.out.println(MPPUtility.hexdump(buffer, index, recordSize, true, 16, ""));
            int btrieveValue = getShort(buffer, index);
            if (btrieveValue != 0)
            {
               Map<String, Object> row = new HashMap<String, Object>();
               row.put("ROW_VERSION", Integer.valueOf(btrieveValue));
               for (ColumnDefinition column : m_definition.getColumns())
               {
                  Object value = column.read(index, buffer);
                  System.out.println(column.getName() + ": " + value);
                  row.put(column.getName(), value);
               }
               table.addRow(m_definition.getPrimaryKeyColumnName(), row);
            }
            index += recordSize;
         }
      }
   }

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
