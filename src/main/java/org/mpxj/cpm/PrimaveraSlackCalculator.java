package org.mpxj.cpm;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.Objects;
import java.util.stream.Collectors;

import org.mpxj.ActivityType;
import org.mpxj.ConstraintType;
import org.mpxj.Duration;
import org.mpxj.Relation;
import org.mpxj.SlackCalculator;
import org.mpxj.Task;
import org.mpxj.TimeUnit;
import org.mpxj.common.LocalDateTimeHelper;

public class PrimaveraSlackCalculator implements SlackCalculator
{
   public Duration calculateFreeSlack(Task task)
   {
      // If the task is complete, free slack is always zero
      if (task.getExpectedFinish() != null || task.getActualFinish() != null || task.getActivityType() == ActivityType.LEVEL_OF_EFFORT || task.getSummary()) // TODO - do we want to populate this for WBS?
      {
         return Duration.getInstance(0, TimeUnit.HOURS);
      }

      return task.getSuccessors().stream()
         // Ignore LOE successors
         .filter(r -> r.getSuccessorTask().getActivityType() != ActivityType.LEVEL_OF_EFFORT)
         // Handle duplicate successor tasks
         .collect(Collectors.toMap(Relation::getSuccessorTask, this::calculateFreeSlack, this::mergeFreeSlack))
         .values().stream()
         .filter(Objects::nonNull)
         .min(Comparator.naturalOrder())
         .orElseGet(() -> calculateFreeSlackWithoutSuccessors(task));
   }

   private Duration mergeFreeSlack(Duration d1, Duration d2)
   {
      if (d1.getDuration() >= 0 && d2.getDuration() >= 0)
      {
         return d1.compareTo(d2) < 0 ? d1 : d2;
      }

      return d1.compareTo(d2) > 0 ? d1 : d2;
   }

   private Duration calculateFreeSlack(Relation relation)
   {
      Task predecessorTask = relation.getPredecessorTask();
      if (predecessorTask.getConstraintType() == ConstraintType.MUST_FINISH_ON)
      {
         return Duration.getInstance(0, TimeUnit.HOURS);
      }


      Task successorTask = relation.getSuccessorTask();

      switch (relation.getType())
      {
         case FINISH_START:
         {
            return calculateFreeSlackVariance(relation, predecessorTask.getEarlyFinish(), successorTask.getEarlyStart());
         }

         case START_START:
         {
            if (relation.getPredecessorTask().getActualStart() == null)
            {
               return calculateFreeSlackVariance(relation, predecessorTask.getEarlyStart(), successorTask.getEarlyStart());
            }
            return Duration.getInstance(0, TimeUnit.HOURS);
         }

         case FINISH_FINISH:
         {
            return calculateFreeSlackVariance(relation, predecessorTask.getEarlyFinish(), successorTask.getEarlyFinish());
         }

         case START_FINISH:
         {
            return calculateFreeSlackVariance(relation, predecessorTask.getEarlyStart(), successorTask.getEarlyFinish());
         }
      }

      return null;
   }

   private Duration calculateFreeSlackVariance(Relation relation, LocalDateTime date1, LocalDateTime date2)
   {
      if (date1 == null || date2 == null)
      {
         return Duration.getInstance(0, TimeUnit.HOURS);
      }

      Duration variance = LocalDateTimeHelper.getVariance(relation.getPredecessorTask().getEffectiveCalendar(), date1, date2, TimeUnit.HOURS);
      return removeLag(relation, variance);
   }

   private Duration calculateFreeSlackWithoutSuccessors(Task task)
   {
      if (task.getConstraintType() == ConstraintType.MUST_FINISH_ON || task.getConstraintType() == ConstraintType.MUST_START_ON)
      {
         return Duration.getInstance(0, TimeUnit.HOURS);
      }

      LocalDateTime projectFinishDate = task.getParentFile().getProjectProperties().getScheduledFinish();
      return LocalDateTimeHelper.getVariance(task.getEffectiveCalendar(), task.getEarlyFinish(), projectFinishDate, TimeUnit.HOURS);
   }

   private Duration removeLag(Relation relation, Duration duration)
   {
      Duration lag = relation.getLag();
      double lagDuration = lag.getDuration();
      if (lagDuration == 0.0)
      {
         return duration;
      }

      TimeUnit lagUnits = lag.getUnits();
      TimeUnit durationUnits = duration.getUnits();
      if (lagUnits != durationUnits)
      {
         lag = lag.convertUnits(durationUnits, relation.getPredecessorTask().getEffectiveCalendar());
      }

      return Duration.getInstance(duration.getDuration() - lag.getDuration(), durationUnits);
   }
}

