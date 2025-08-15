package org.mpxj.pwa;

import java.util.List;
import java.util.Map;

class ListContainer
{
   public List<MapRow> getValue()
   {
      return m_value;
   }

   public void setValue(List<MapRow> value)
   {
      m_value = value;
   }

   private List<MapRow> m_value;
}
