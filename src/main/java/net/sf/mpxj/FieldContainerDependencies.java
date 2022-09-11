package net.sf.mpxj;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

class FieldContainerDependencies<T>
{
   public FieldContainerDependencies(Map<T, List<T>> map)
   {
      m_map = map;
   }

   public FieldContainerDependencies<T> calculatedField(T type)
   {
      m_currentField = type;
      return this;
   }

   @SafeVarargs public final void dependsOn(T... fields)
   {
      Arrays.stream(fields).forEach(field -> m_map.computeIfAbsent(field, f -> new ArrayList<>()).add(m_currentField));
   }

   private T m_currentField;
   private final Map<T, List<T>> m_map;
}
