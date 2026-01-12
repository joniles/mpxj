/*
 * file:       MicrosoftSlackCalculator.java
 * author:     Jon Iles
 * date:       2025-12-18
 */

/*
 * This library is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as published by the
 * Free Software Foundation; either version 2.1 of the License, or (at your
 * option) any later version.
 *
 * This library is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY
 * or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public
 * License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this library; if not, write to the Free Software Foundation, Inc.,
 * 59 Temple Place, Suite 330, Boston, MA 02111-1307, USA.
 */

package org.mpxj.cpm;

import java.util.Comparator;
import java.util.Objects;
import java.time.LocalDateTime;

import org.mpxj.ConstraintType;
import org.mpxj.Duration;
import org.mpxj.Relation;
import org.mpxj.SlackCalculator;
import org.mpxj.Task;
import org.mpxj.TimeUnit;
import org.mpxj.TotalSlackCalculationType;
import org.mpxj.common.LocalDateTimeHelper;

/**
 * Perform slack calculations to align with Microsoft Project.
 */
public class MicrosoftSlackCalculator implements SlackCalculator
{
   @Override public Duration calculateStartSlack(Task task)
   {
      Duration duration = task.getDuration();
      if (duration == null)
      {
         return null;
      }

      if (task.getConstraintType() == ConstraintType.AS_LATE_AS_POSSIBLE)
      {
         return Duration.getInstance(0, duration.getUnits());
      }

      LocalDateTime lateStart = task.getLateStart();
      LocalDateTime earlyStart = task.getEarlyStart();

      if (lateStart == null || earlyStart == null)
      {
         return null;
      }

      return LocalDateTimeHelper.getVariance(task.getEffectiveCalendar(), earlyStart, lateStart, duration.getUnits());
   }

   @Override public Duration calculateFinishSlack(Task task)
   {
      Duration duration = task.getDuration();
      if (duration == null)
      {
         return null;
      }

      if (task.getConstraintType() == ConstraintType.AS_LATE_AS_POSSIBLE)
      {
         return Duration.getInstance(0, duration.getUnits());
      }

      LocalDateTime earlyFinish = task.getEarlyFinish();
      LocalDateTime lateFinish = task.getLateFinish();

      if (earlyFinish == null || lateFinish == null)
      {
         return null;
      }

      return LocalDateTimeHelper.getVariance(task.getEffectiveCalendar(), earlyFinish, lateFinish, duration.getUnits());

   }

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
         Duration freeFloat = task.getChildTasks().stream().flatMap(t -> t.getSuccessors().stream())
            // Ignore completed successors
            .filter(r -> r.getSuccessorTask().getActualFinish() == null)
            .map(r -> calculateVariance(task, r.getSuccessorTask()))
            .filter(Objects::nonNull)
            .min(Comparator.naturalOrder())
            .orElseGet(task::getTotalSlack);

         if (freeFloat != null && freeFloat.getDuration() < 0)
         {
            return Duration.getInstance(0, freeFloat.getUnits());
         }

         return freeFloat;
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

   /**
    * Calculate the variance between the early finish of one task and the early start of another task.
    *
    * @param t1 first task
    * @param t2 second task
    * @return variance
    */
   private Duration calculateVariance(Task t1, Task t2)
   {
      Duration variance = LocalDateTimeHelper.getVariance(t1.getEffectiveCalendar(), t1.getEarlyFinish(), t1.getEarlyStart(), TimeUnit.HOURS);
      if (variance.getDuration() < 0)
      {
         return null;
      }
      return variance;
   }

   /**
    * Calculate the free slack between two tasks.
    *
    * @param relation relation
    * @return free slack
    */
   private Duration calculateFreeSlack(Relation relation)
   {
      Task predecessorTask = relation.getPredecessorTask();
      Task successorTask = relation.getSuccessorTask();

      LocalDateTime predecessorStart = predecessorTask.getConstraintType() == ConstraintType.AS_LATE_AS_POSSIBLE ? predecessorTask.getLateStart() : predecessorTask.getEarlyStart();
      LocalDateTime predecessorFinish = predecessorTask.getConstraintType() == ConstraintType.AS_LATE_AS_POSSIBLE ? predecessorTask.getLateFinish() : predecessorTask.getEarlyFinish();
      LocalDateTime successorStart = successorTask.getConstraintType() == ConstraintType.AS_LATE_AS_POSSIBLE ? successorTask.getLateStart() : successorTask.getEarlyStart();
      LocalDateTime successorFinish = successorTask.getConstraintType() == ConstraintType.AS_LATE_AS_POSSIBLE ? successorTask.getLateFinish() : successorTask.getEarlyFinish();

      switch (relation.getType())
      {
         case FINISH_START:
         {
            return calculateFreeSlackVariance(relation, predecessorFinish, successorStart);
         }

         case START_START:
         {
            return calculateFreeSlackVariance(relation, predecessorStart, successorStart);
         }

         case FINISH_FINISH:
         {
            return calculateFreeSlackVariance(relation, predecessorFinish, successorFinish);
         }

         case START_FINISH:
         {
            return calculateFreeSlackVariance(relation, predecessorStart, successorFinish);
         }
      }

      return null;
   }

   /**
    * Calculate the variance between two dates in the context of a Relation instance.
    *
    * @param relation Relation instance
    * @param date1 first date
    * @param date2 second date
    * @return variance value
    */
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

   /**
    * Remove lag from a duration.
    *
    * @param relation a Relation instance representing the lag to remove
    * @param duration remve lag from this duration
    * @return duration without lag
    */
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
