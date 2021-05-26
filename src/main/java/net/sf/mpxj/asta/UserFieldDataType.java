
package net.sf.mpxj.asta;

import java.util.HashMap;
import java.util.Map;

import net.sf.mpxj.FieldType;

class UserFieldDataType<E extends Enum<E> & FieldType>
{
   public UserFieldDataType(Class<E> clazz)
   {
      m_class = clazz;
   }

   public E nextField(Integer dataType)
   {
      try
      {
         String customFieldName = TYPE_MAP.get(dataType);
         int index = m_counters.compute(customFieldName, (k, v) -> v == null ? Integer.valueOf(1) : Integer.valueOf(v.intValue() + 1)).intValue();
         E e = Enum.valueOf(m_class, customFieldName + index);
         return e;
      }

      catch (Exception ex)
      {
         // If we've run out of a particular custom field type, we'll end up here.
         // We'll return null to ignore this type.
         return null;
      }
   }

   private final Class<E> m_class;
   private final Map<String, Integer> m_counters = new HashMap<>();

   private static final Map<Integer, String> TYPE_MAP = new HashMap<>();
   static
   {
      TYPE_MAP.put(Integer.valueOf(0), "FLAG"); // Boolean      
      TYPE_MAP.put(Integer.valueOf(6), "NUMBER"); // Integer
      TYPE_MAP.put(Integer.valueOf(8), "NUMBER"); // Float
      TYPE_MAP.put(Integer.valueOf(9), "TEXT"); // String
      TYPE_MAP.put(Integer.valueOf(13), "DATE"); // Date
      TYPE_MAP.put(Integer.valueOf(15), "DURATION"); // Duration
      TYPE_MAP.put(Integer.valueOf(24), "TEXT"); // URL
   }
}
