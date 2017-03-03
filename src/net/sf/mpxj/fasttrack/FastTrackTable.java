
package net.sf.mpxj.fasttrack;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import net.sf.mpxj.TimeUnit;

class FastTrackTable implements Iterable<MapRow>
{
   public FastTrackTable(FastTrackData data, String name)
   {
      m_data = data;
      m_name = name;
   }

   public String getName()
   {
      return m_name;
   }

   public TimeUnit getDurationTimeUnit()
   {
      return m_data.getDurationTimeUnit();
   }

   public TimeUnit getWorkTimeUnit()
   {
      return m_data.getWorkTimeUnit();
   }

   public void addColumn(FastTrackColumn column)
   {
      String name = column.getName();
      Object[] data = column.getData();
      for (int index = 0; index < data.length; index++)
      {
         MapRow row = getRow(index);
         row.getMap().put(name, data[index]);
      }
   }

   @Override public Iterator<MapRow> iterator()
   {
      return m_rows.iterator();
   }

   private MapRow getRow(int index)
   {
      MapRow result;

      if (index == m_rows.size())
      {
         result = new MapRow(this, new HashMap<String, Object>());
         m_rows.add(result);
      }
      else
      {
         result = m_rows.get(index);
      }

      return result;
   }

   private final FastTrackData m_data;
   private final String m_name;
   private final ArrayList<MapRow> m_rows = new ArrayList<MapRow>();
}
