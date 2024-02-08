package net.sf.mpxj.openplan;
import java.util.Map;
import java.util.stream.Collectors;

class MapRow implements Row
{
   public MapRow(Map<String, Object> map)
   {
      m_map = map;
   }

   @Override public String toString()
   {
      return "[MapRow\n"+ m_map.entrySet().stream().map(e -> "\t" + e.getKey() + "\t" + e.getValue()).collect(Collectors.joining("\n")) + "\n]";
   }

   private final Map<String, Object> m_map;
}
