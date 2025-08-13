package org.mpxj.pwa;

import java.util.List;
import java.util.Map;

class ListContainer
{
   public List<Map<String, Object>> getValue()
   {
      return m_value;
   }

   public void setValue(List<Map<String, Object>> value)
   {
      m_value = value;
   }

   private List<Map<String, Object>> m_value;
}
