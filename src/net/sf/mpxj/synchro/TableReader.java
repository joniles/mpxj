
package net.sf.mpxj.synchro;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import net.sf.mpxj.common.ByteArray;
import net.sf.mpxj.mpp.MPPUtility;

abstract class TableReader
{
   public TableReader(InputStream stream)
   {
      m_stream = stream;
   }

   public List<MapRow> getRows()
   {
      return m_rows;
   }

   public TableReader read() throws IOException
   {
      int tableHeader = SynchroUtility.getInt(m_stream);
      if (tableHeader != 0x39AF547A)
      {
         throw new IllegalArgumentException("Unexpected file format");
      }

      int recordCount = SynchroUtility.getInt(m_stream);
      for (int loop = 0; loop < recordCount; loop++)
      {
         int rowMagicNumber = SynchroUtility.getInt(m_stream);
         if (rowMagicNumber != rowMagicNumber())
         {
            throw new IllegalArgumentException("Unexpected file format");
         }

         // We use a LinkedHashMap to preserve insertion order in iteration
         // Useful when debugging the file format.
         Map<String, Object> map = new LinkedHashMap<String, Object>();

         if (hasUUID())
         {
            // TODO: replace with StreamReader
            byte[] block1 = new byte[16];
            m_stream.read(block1);
            System.out.println("BLOCK1");
            System.out.println(MPPUtility.hexdump(block1, true, 16, ""));

            map.put("UNKNOWN0", new ByteArray(block1));
            map.put("UUID", SynchroUtility.getUUID(m_stream));
         }

         readRow(map);

         System.out.println(getClass().getSimpleName());
         for (Map.Entry<String, Object> entry : map.entrySet())
         {
            System.out.println(entry.getKey() + ": " + entry.getValue());
         }
         System.out.println();

         m_rows.add(new MapRow(map));
      }

      int tableTrailer = SynchroUtility.getInt(m_stream);
      if (tableTrailer != 0x6F99E416)
      {
         throw new IllegalArgumentException("Unexpected file format");
      }

      return this;
   }

   protected boolean hasUUID()
   {
      return true;
   }

   protected abstract int rowMagicNumber();

   protected abstract void readRow(Map<String, Object> map) throws IOException;

   protected final InputStream m_stream;
   private final List<MapRow> m_rows = new ArrayList<MapRow>();
}
