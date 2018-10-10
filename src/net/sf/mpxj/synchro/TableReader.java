
package net.sf.mpxj.synchro;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

abstract class TableReader
{
   public TableReader(InputStream stream)
   {
      m_stream = new StreamReader(stream);
   }

   public List<MapRow> getRows()
   {
      return m_rows;
   }

   public TableReader read() throws IOException
   {
      int tableHeader = m_stream.readInt();
      if (tableHeader != 0x39AF547A)
      {
         throw new IllegalArgumentException("Unexpected file format");
      }

      int recordCount = m_stream.readInt();
      for (int loop = 0; loop < recordCount; loop++)
      {
         int rowMagicNumber = m_stream.readInt();
         if (rowMagicNumber != rowMagicNumber())
         {
            throw new IllegalArgumentException("Unexpected file format");
         }

         // We use a LinkedHashMap to preserve insertion order in iteration
         // Useful when debugging the file format.
         Map<String, Object> map = new LinkedHashMap<String, Object>();

         if (hasUUID())
         {
            map.put("UNKNOWN0", m_stream.readBytes(16));
            map.put("UUID", m_stream.readUUID());
         }

         readRow(m_stream, map);

         SynchroLogger.log("READER", getClass(), map);

         m_rows.add(new MapRow(map));
      }

      int tableTrailer = m_stream.readInt();
      if (tableTrailer != 0x6F99E416)
      {
         throw new IllegalArgumentException("Unexpected file format");
      }

      postTrailer(m_stream);

      return this;
   }

   protected boolean hasUUID()
   {
      return true;
   }

   protected void postTrailer(StreamReader stream) throws IOException
   {
      // Default implementation
   }

   protected abstract int rowMagicNumber();

   protected abstract void readRow(StreamReader stream, Map<String, Object> map) throws IOException;

   protected final StreamReader m_stream;
   private final List<MapRow> m_rows = new ArrayList<MapRow>();
}
