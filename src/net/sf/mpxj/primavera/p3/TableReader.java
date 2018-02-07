package net.sf.mpxj.primavera.p3;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import net.sf.mpxj.mpp.MPPUtility;

public class TableReader
{
   public TableReader(TableDefinition definition)
   {
      m_definition = definition;
   }   
   
   public void read(File file) throws IOException
   {
      InputStream is = null;
      try
      {
         is = new FileInputStream(file);
         read(is);
      }
      
      finally
      {
         if (is != null)
         {
            try
            {
               is.close();
            }
            
            catch(IOException ex)
            {
               // Ignore
            }
         }
      }
   }
   
   private void read(InputStream is) throws IOException
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
         
         readPage(buffer);
      }
   }
   
   private void readPage(byte[] buffer)
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
            for (ColumnDefinition column : m_definition.getColumns())
            {
               System.out.println(column.getName() + ": " + column.read(index, buffer));
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
