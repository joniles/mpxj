package net.sf.mpxj.cpm;


import net.sf.mpxj.Duration;

import net.sf.mpxj.Task;
import net.sf.mpxj.TimeUnit;
import net.sf.mpxj.common.NumberHelper;

public final class SchedulerHelper
{
   public static void progressTask(Task task, double percentComplete) throws CpmException
   {
      if (task.getDuration() == null)
      {
         throw new CpmException("Task does not have a duration: " + task);
      }

      double durationValue = task.getDuration().getDuration();
      TimeUnit durationUnits = task.getDuration().getUnits();
      task.setPercentageComplete(percentComplete);
      task.setActualDuration(Duration.getInstance((percentComplete * durationValue) / 100.0, durationUnits));
      task.setRemainingDuration(Duration.getInstance(((100.0 - percentComplete) * durationValue) / 100.0, durationUnits));
   }
}
