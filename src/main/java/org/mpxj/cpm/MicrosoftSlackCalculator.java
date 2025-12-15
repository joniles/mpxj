package org.mpxj.cpm;

import java.util.Comparator;
import java.util.Objects;
import java.time.LocalDateTime;

import org.mpxj.Duration;
import org.mpxj.Relation;
import org.mpxj.SlackCalculator;
import org.mpxj.Task;
import org.mpxj.TimeUnit;
import org.mpxj.TotalSlackCalculationType;
import org.mpxj.common.LocalDateTimeHelper;

public class MicrosoftSlackCalculator implements SlackCalculator
{
   @Override public Duration calculateFreeSlack(Task task)
   {
      // If the task is complete, free slack is always zero
      if (task.getActualFinish() != null)
      {
         Duration duration = task.getDuration();
         return Duration.getInstance(0, duration == null ? TimeUnit.HOURS : duration.getUnits());
      }

      if (task.getSummary())
      {
         return task.getChildTasks().stream().flatMap(t -> t.getSuccessors().stream())
            // Ignore completed successors
            .filter(r -> r.getSuccessorTask().getActualFinish() == null)
            .map(r -> calculateVariance(task, r.getSuccessorTask()))
            .filter(Objects::nonNull)
            .min(Comparator.naturalOrder())
            .orElseGet(task::getTotalSlack);
      }

      Duration freeFloat = task.getSuccessors().stream()
         // Ignore completed successors
         .filter(r -> r.getSuccessorTask().getActualFinish() == null)
         .map(this::calculateFreeSlack)
         .filter(Objects::nonNull)
         .min(Comparator.naturalOrder())
         .orElseGet(task::getTotalSlack);


      if (freeFloat != null && freeFloat.getDuration() < 0)
      {
         return Duration.getInstance(0, TimeUnit.HOURS);
      }

      return freeFloat;
   }

   private Duration calculateVariance(Task t1, Task t2)
   {
      Duration variance =  LocalDateTimeHelper.getVariance(t1.getEffectiveCalendar(), t1.getEarlyFinish(), t1.getEarlyStart(), TimeUnit.HOURS);
      if (variance.getDuration() < 0)
      {
         return null;
      }
      return variance;
   }

   @Override public Duration calculateTotalSlack(Task task)
   {
      // Calculate these first to avoid clearing our total slack value
      Duration duration = task.getDuration();
      Duration startSlack = task.getStartSlack();
      Duration finishSlack = task.getFinishSlack();

      TotalSlackCalculationType calculationType = task.getParentFile().getProjectProperties().getTotalSlackCalculationType();

      if (calculationType == TotalSlackCalculationType.START_SLACK)
      {
         return startSlack;
      }

      if (calculationType == TotalSlackCalculationType.FINISH_SLACK)
      {
         return finishSlack;
      }

      if (task.getActualStart() != null)
      {
         return finishSlack;
      }

      if (duration == null)
      {
         return null;
      }

      if (startSlack == null)
      {
         return null;
      }

      if (finishSlack == null)
      {
         return null;
      }

      TimeUnit units = duration.getUnits();
      if (startSlack.getUnits() != units)
      {
         startSlack = startSlack.convertUnits(units, task.getParentFile().getProjectProperties());
      }

      if (finishSlack.getUnits() != units)
      {
         finishSlack = finishSlack.convertUnits(units, task.getParentFile().getProjectProperties());
      }

      Duration totalSlack;
      double startSlackDuration = startSlack.getDuration();
      double finishSlackDuration = finishSlack.getDuration();

      if (startSlackDuration < finishSlackDuration)
      {
         totalSlack = startSlack;
      }
      else
      {
         totalSlack = finishSlack;
      }

      return totalSlack;
   }

   private Duration calculateFreeSlack(Relation relation)
   {
      Task predecessorTask = relation.getPredecessorTask();
      Task successorTask = relation.getSuccessorTask();

      switch (relation.getType())
      {
         case FINISH_START:
         {
            return calculateFreeSlackVariance(relation, predecessorTask.getEarlyFinish(), successorTask.getEarlyStart());
         }

         case START_START:
         {
            return calculateFreeSlackVariance(relation, predecessorTask.getEarlyStart(), successorTask.getEarlyStart());
         }

         case FINISH_FINISH:
         {
            return calculateFreeSlackVariance(relation, predecessorTask.getEarlyFinish(), successorTask.getEarlyFinish());
         }

         case START_FINISH:
         {
            return predecessorTask.getTotalSlack();
         }
      }

      return null;
   }

   private Duration calculateFreeSlackVariance(Relation relation, LocalDateTime date1, LocalDateTime date2)
   {
      Task predecessorTask = relation.getPredecessorTask();
      TimeUnit format = predecessorTask.getDuration() == null ? TimeUnit.HOURS : predecessorTask.getDuration().getUnits();

      if (date1 == null || date2 == null)
      {
         return Duration.getInstance(0, format);
      }

      return removeLag(relation, LocalDateTimeHelper.getVariance(predecessorTask.getEffectiveCalendar(), date1, date2, format));
   }

   private Duration removeLag(Relation relation, Duration duration)
   {
      Duration lag = relation.getLag();
      if (lag.getDuration() == 0.0)
      {
         return duration;
      }

      TimeUnit lagUnits = lag.getUnits();
      TimeUnit durationUnits = duration.getUnits();
      if (lagUnits != durationUnits)
      {
         if (lagUnits == TimeUnit.PERCENT)
         {
            Duration predecessorDuration = relation.getPredecessorTask().getDuration();
            lag = Duration.getInstance((predecessorDuration.getDuration() * lag.getDuration()) / 100.0, predecessorDuration.getUnits());
         }
         else
         {
            lag = lag.convertUnits(durationUnits, relation.getPredecessorTask().getEffectiveCalendar());
         }
      }

      return Duration.getInstance(duration.getDuration() - lag.getDuration(), durationUnits);
   }
}
