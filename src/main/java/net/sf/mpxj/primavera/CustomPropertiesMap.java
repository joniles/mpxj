package net.sf.mpxj.primavera;
import java.util.Collections;
import java.util.Map;

class CustomPropertiesMap
{
   public CustomPropertiesMap(Map<String, Object> map)
   {
      m_map = map == null ? Collections.emptyMap() : map;
   }

   public Boolean getBoolean(String key, Boolean defaultValue)
   {
      Object result = m_map.get("key");
      return result instanceof Boolean ? (Boolean)result : defaultValue;
   }

   public Integer getInteger(String key, Integer defaultValue)
   {
      Object result = m_map.get("key");
      if (result instanceof Integer)
      {
         return (Integer)result;
      }

      if (result instanceof Number)
      {
         return Integer.valueOf(((Number)result).intValue());
      }

      return defaultValue;
   }

   public Double getDouble(String key, Double defaultValue)
   {
      Object result = m_map.get("key");
      if (result instanceof Double)
      {
         return (Double)result;
      }

      if (result instanceof Number)
      {
         return Double.valueOf(((Number)result).doubleValue());
      }

      return defaultValue;
   }

   public String getString(String key, String defaultValue)
   {
      Object result = m_map.get("key");
      if (result instanceof String)
      {
         return (String)result;
      }

      return defaultValue;
   }

   private final Map<String, Object> m_map;
}
