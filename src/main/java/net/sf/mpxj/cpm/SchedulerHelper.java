package net.sf.mpxj.cpm;


import net.sf.mpxj.Duration;

import net.sf.mpxj.ResourceAssignment;
import net.sf.mpxj.Task;
import net.sf.mpxj.TimeUnit;

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

   public static void progressAssignment(ResourceAssignment assignment, double percentComplete) throws CpmException
   {
      if (assignment.getWork() == null)
      {
         throw new CpmException("Resource assignment does not have work: " + assignment);
      }

      double workValue = assignment.getWork().getDuration();
      TimeUnit workUnits = assignment.getWork().getUnits();
      assignment.setPercentageWorkComplete(percentComplete);
      assignment.setActualWork(Duration.getInstance((percentComplete * workValue) / 100.0, workUnits));
      assignment.setRemainingWork(Duration.getInstance(((100.0 - percentComplete) * workValue) / 100.0, workUnits));
   }
}
