
package net.sf.mpxj.fasttrack;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

public class FastTrackTable implements Iterable<MapRow>
{
   public FastTrackTable(String name)
   {
      m_name = name;
   }

   public String getName()
   {
      return m_name;
   }

   public void addColumn(FastTrackBlock column)
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
         result = new MapRow(new HashMap<String, Object>());
         m_rows.add(result);
      }
      else
      {
         result = m_rows.get(index);
      }

      return result;
   }
   private final String m_name;
   private final ArrayList<MapRow> m_rows = new ArrayList<MapRow>();
}
