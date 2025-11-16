package org.mpxj.primavera;

import org.mpxj.Task;

class TemporaryWbs
{
   public TemporaryWbs(Task task, Integer outlineLevel)
   {
      m_task = task;
      m_outlineLevel = outlineLevel;
   }

   public Task getTask()
   {
      return m_task;
   }

   public Integer getOutlineLevel()
   {
      return m_outlineLevel;
   }

   private final Task m_task;
   private final Integer m_outlineLevel;
}
