package net.sf.mpxj.cpm;


import net.sf.mpxj.Duration;

import net.sf.mpxj.Task;
import net.sf.mpxj.TimeUnit;
import net.sf.mpxj.common.NumberHelper;

public final class SchedulerHelper
{
   public static void populateDurationsFromPercentComplete(Task task) throws CpmException
   {
      if (task.getDuration() == null)
      {
         throw new CpmException("Task does not have a duration: " + task);
      }

      if (task.getPercentageComplete() == null)
      {
         throw new CpmException("Task does not have a percent complete: " + task);
      }

      if (task.getActualDuration() == null && task.getRemainingDuration() == null)
      {
         double durationValue = task.getDuration().getDuration();
         TimeUnit durationUnits = task.getDuration().getUnits();
         double percentComplete = NumberHelper.getDouble(task.getPercentageComplete());
         task.setActualDuration(Duration.getInstance((percentComplete * durationValue) / 100.0, durationUnits));
         task.setRemainingDuration(Duration.getInstance(((100.0 - percentComplete) * durationValue) / 100.0, durationUnits));
      }
   }
}
