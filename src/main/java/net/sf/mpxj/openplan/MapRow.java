package net.sf.mpxj.openplan;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

class MapRow implements Row
{
   public MapRow(Map<String, Object> map)
   {
      m_map = map;
   }

   @Override public String toString()
   {
      return "[MapRow\n"+ m_map.entrySet().stream().map(e -> "\t" + e.getKey() + "\t" + e.getValue() + " ("+e.getValue().getClass().getSimpleName()+")").collect(Collectors.joining("\n")) + "\n]";
   }

   // TODO change!
   public final Map<String, Object> m_map;

   @Override public String getString(String name)
   {
      return (String)m_map.get(name);
   }

   @Override public LocalDateTime getDate(String name)
   {
      return (LocalDateTime) m_map.get(name);
   }

   @Override public Double getDouble(String name)
   {
      return (Double)m_map.get(name);
   }

   @Override public Integer getInteger(String name)
   {
      return (Integer)m_map.get(name);
   }

   @Override public Boolean getBoolean(String name)
   {
      return (Boolean)m_map.get(name);
   }

   @Override public UUID getUuid(String name)
   {
      return (UUID)m_map.get(name);
   }
}
