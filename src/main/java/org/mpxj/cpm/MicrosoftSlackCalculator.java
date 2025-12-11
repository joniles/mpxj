package org.mpxj.cpm;

import java.util.Comparator;
import java.util.Objects;
import java.time.LocalDateTime;

import org.mpxj.Duration;
import org.mpxj.Relation;
import org.mpxj.SlackCalculator;
import org.mpxj.Task;
import org.mpxj.TimeUnit;
import org.mpxj.common.LocalDateTimeHelper;

public class MicrosoftSlackCalculator implements SlackCalculator
{
   public Duration calculateFreeSlack(Task task)
   {
      // If the task is complete, free slack is always zero
      if (task.getActualFinish() != null || task.getSummary()) // TODO - do we want to populate this for summary tasks?
      {
         Duration duration = task.getDuration();
         return Duration.getInstance(0, duration == null ? TimeUnit.HOURS : duration.getUnits());
      }

      return task.getSuccessors().stream()
         // Ignore completed successors
         .filter(r -> r.getSuccessorTask().getActualFinish() == null)
         .map(this::calculateFreeSlack)
         .filter(Objects::nonNull)
         .min(Comparator.naturalOrder())
         .orElseGet(task::getTotalSlack);
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
