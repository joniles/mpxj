package net.sf.mpxj;

import java.util.function.Function;

class FieldContainerEntry
{
   private FieldContainerEntry(Function<Task, Object> function, boolean cacheResult)
   {
      m_function = function;
      m_cacheResult = cacheResult;
   }

   public static FieldContainerEntry cached(Function<Task, Object> function)
   {
      return new FieldContainerEntry(function, true);
   }

   public static FieldContainerEntry calculated(Function<Task, Object> function)
   {
      return new FieldContainerEntry(function, false);
   }

   public Function<Task, Object> getFunction()
   {
      return m_function;
   }

   public boolean cacheResult()
   {
      return m_cacheResult;
   }

   private final Function<Task, Object> m_function;
   private final boolean m_cacheResult;
}
