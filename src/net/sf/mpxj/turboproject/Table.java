
package net.sf.mpxj.turboproject;

import java.io.IOException;
import java.io.InputStream;
import java.util.Iterator;
import java.util.Map;
import java.util.TreeMap;

class Table implements Iterable<MapRow>
{
   @Override public Iterator<MapRow> iterator()
   {
      return m_rows.values().iterator();
   }

   public void read(InputStream is) throws IOException
   {
      byte[] headerBlock = new byte[20];
      is.read(headerBlock);

      int headerLength = PEPUtility.getShort(headerBlock, 8);
      int recordCount = PEPUtility.getInt(headerBlock, 10);
      int recordLength = PEPUtility.getInt(headerBlock, 16);

      is.skip(headerLength - headerBlock.length);

      byte[] record = new byte[recordLength];
      for (int recordIndex = 1; recordIndex <= recordCount; recordIndex++)
      {
         is.read(record);
         readRow(recordIndex, record);
      }
   }

   public MapRow find(Integer uniqueID)
   {
      return m_rows.get(uniqueID);
   }

   protected void readRow(int uniqueID, byte[] data)
   {

   }

   protected void addRow(int uniqueID, Map<String, Object> map)
   {
      m_rows.put(Integer.valueOf(uniqueID), new MapRow(map));
   }

   private final Map<Integer, MapRow> m_rows = new TreeMap<Integer, MapRow>();
}
