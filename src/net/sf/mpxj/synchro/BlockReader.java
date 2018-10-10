
package net.sf.mpxj.synchro;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

abstract class BlockReader
{
   public BlockReader(InputStream stream)
   {
      m_stream = new StreamReader(stream);
   }

   public List<MapRow> read() throws IOException
   {
      List<MapRow> result = new ArrayList<MapRow>();
      int fileCount = m_stream.readInt();
      if (fileCount != 0)
      {
         for (int index = 0; index < fileCount; index++)
         {
            // We use a LinkedHashMap to preserve insertion order in iteration
            // Useful when debugging the file format.
            Map<String, Object> map = new LinkedHashMap<String, Object>();
            readBlock(map);
            result.add(new MapRow(map));
         }
      }
      return result;
   }

   protected abstract void readBlock(Map<String, Object> map) throws IOException;

   protected final StreamReader m_stream;
}
