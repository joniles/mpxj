package net.sf.mpxj.primavera;

import net.sf.mpxj.Duration;
import net.sf.mpxj.Task;
import net.sf.mpxj.TimeUnit;

class WorkHelper
{
   public static Duration zeroIfNull(Duration value)
   {
      return value == null ? Duration.getInstance(0, TimeUnit.HOURS) : value;
   }

   public static Duration getActualWorkLabor(Task task)
   {
      return getWork(task.getActualWork(), task.getActualWorkLabor(), task.getActualWorkNonlabor());
   }

   public static Duration getPlannedWorkLabor(Task task)
   {
      return getWork(task.getPlannedWork(), task.getPlannedWorkLabor(), task.getPlannedWorkNonlabor());
   }

   public static Duration getRemainingWorkLabor(Task task)
   {
      return getWork(task.getRemainingWork(), task.getRemainingWorkLabor(), task.getRemainingWorkNonlabor());
   }

   private static Duration getWork(Duration total, Duration labor, Duration nonlabor)
   {
      if (total == null && labor == null && nonlabor == null)
      {
         return Duration.getInstance(0, TimeUnit.HOURS);
      }

      if (total != null && labor == null && nonlabor == null)
      {
         return total;
      }

      return zeroIfNull(labor);
   }
}
