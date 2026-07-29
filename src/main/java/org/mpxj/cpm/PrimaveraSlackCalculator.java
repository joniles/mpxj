/*
 * file:       PrimaveraSlackCalculator.java
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

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import org.mpxj.ActivityType;
import org.mpxj.ConstraintType;
import org.mpxj.Duration;
import org.mpxj.ProjectFile;
import org.mpxj.ProjectProperties;
import org.mpxj.Relation;
import org.mpxj.SlackCalculator;
import org.mpxj.Task;
import org.mpxj.TimeUnit;
import org.mpxj.TotalSlackCalculationType;
import org.mpxj.common.LocalDateTimeHelper;

/**
 * Perform slack calculations to align with Primavera P6.
 */
public class PrimaveraSlackCalculator implements SlackCalculator
{
   /**
    * Constructor.
    *
    * @param file parent file
    */
   public PrimaveraSlackCalculator(ProjectFile file)
   {
      m_projectProperties = file.getProjectProperties();
      m_dateCalculator = new PrimaveraDateCalculator(file, this::getDataDate);
   }

   @Override public Duration calculateStartSlack(Task task)
   {
      LocalDateTime lateStart = task.getLateStart();
      LocalDateTime earlyStart = task.getEarlyStart();

      if (lateStart == null || earlyStart == null)
      {
         return null;
      }

      return LocalDateTimeHelper.getVariance(task.getEffectiveCalendar(), earlyStart, lateStart, TimeUnit.HOURS);
   }

   @Override public Duration calculateFinishSlack(Task task)
   {
      LocalDateTime earlyFinish = task.getEarlyFinish();
      LocalDateTime lateFinish = task.getLateFinish();

      if (earlyFinish == null || lateFinish == null)
      {
         return null;
      }

      return LocalDateTimeHelper.getVariance(task.getEffectiveCalendar(), earlyFinish, lateFinish, TimeUnit.HOURS);
   }

   @Override public Duration calculateFreeSlack(Task task)
   {
      if (task.getExpectedFinish() != null ||
         task.getActualFinish() != null || // If the task is complete, free slack is always zero
         task.getActivityType() == ActivityType.LEVEL_OF_EFFORT ||
         task.getSummary() || // TODO - do we want to populate this for WBS?
         task.getConstraintType() == ConstraintType.MUST_FINISH_ON)
      {
         return Duration.getInstance(0, TimeUnit.HOURS);
      }

      Map<Task, Duration> map = task.getSuccessors().stream()
         // Ignore LOE successors
         .filter(r -> r.getSuccessorTask().getActivityType() != ActivityType.LEVEL_OF_EFFORT)
         // Handle duplicate successor tasks
         .collect(Collectors.toMap(Relation::getSuccessorTask, this::calculateFreeSlack, this::mergeFreeSlack));

      Duration freeFloat = map.values().stream()
         .filter(Objects::nonNull)
         .min(Comparator.naturalOrder())
         .orElseGet(() -> calculateFreeSlackWithoutSuccessors(task));

      if (freeFloat.getDuration() < 0)
      {
         return Duration.getInstance(0, TimeUnit.HOURS);
      }

      return roundToMinutes(freeFloat);
   }

   @Override public Duration calculateTotalSlack(Task task)
   {
      Duration duration = task.getDuration();

      if (task.getActualFinish() != null)
      {
         return Duration.getInstance(0, duration == null ? TimeUnit.HOURS : duration.getUnits());
      }

      // Calculate these first to avoid clearing our total slack value
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
    * Determine the free slack value when there are multiple relations
    * between the same pair of activities.
    *
    * @param d1 Duration instance
    * @param d2 Duration instance
    * @return selected free slack duration
    */
   private Duration mergeFreeSlack(Duration d1, Duration d2)
   {
      if (d1.getDuration() >= 0 && d2.getDuration() >= 0)
      {
         return d1.compareTo(d2) < 0 ? d1 : d2;
      }

      return d1.compareTo(d2) > 0 ? d1 : d2;
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
      LocalDateTime predecessorEarlyStart;
      LocalDateTime predecessorEarlyFinish;
      if (predecessorTask.getActualStart() == null)
      {
         predecessorEarlyStart = predecessorTask.getEarlyStart();
         predecessorEarlyFinish = predecessorTask.getEarlyFinish();
      }
      else
      {
         predecessorEarlyStart = predecessorTask.getRemainingEarlyStart();
         predecessorEarlyFinish = predecessorTask.getRemainingEarlyFinish();
      }

      LocalDateTime predecessorDate;
      LocalDateTime successorDate;
      Task successorTask = relation.getSuccessorTask();
      double lagDurationInHours = relation.getLag().convertUnits(TimeUnit.HOURS, m_projectProperties).getDuration();

      switch (relation.getType())
      {
         case FINISH_START:
         {
            predecessorDate = predecessorEarlyFinish;
            successorDate = successorTask.getEarlyStart();
            break;
         }

         case START_START:
         {
            predecessorDate = predecessorEarlyStart;
            successorDate = successorTask.getEarlyStart();

            if (lagDurationInHours != 0 && predecessorTask.getActualDuration() != null)
            {
               double actualDurationInHours = predecessorTask.getActualDuration().convertUnits(TimeUnit.HOURS, m_projectProperties).getDuration();
               lagDurationInHours = actualDurationInHours >= lagDurationInHours ? 0 : lagDurationInHours - actualDurationInHours;
            }

            break;
         }

         case FINISH_FINISH:
         {
            predecessorDate = predecessorEarlyFinish;
            successorDate = successorTask.getEarlyFinish();
            break;
         }

         case START_FINISH:
         {
            predecessorDate = predecessorEarlyStart;
            successorDate = successorTask.getEarlyFinish();
            break;
         }

         default:
         {
            throw new RuntimeException("Invalid relation type");
         }
      }

      if (predecessorDate == null || successorDate == null)
      {
         return Duration.getInstance(0, TimeUnit.HOURS);
      }

      if (lagDurationInHours != 0)
      {
         predecessorDate = m_dateCalculator.addLag(relation, predecessorDate, Duration.getInstance(lagDurationInHours, TimeUnit.HOURS));
      }

      return LocalDateTimeHelper.getVariance(predecessorTask.getEffectiveCalendar(), predecessorDate, successorDate, TimeUnit.HOURS);
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
      if (date1 == null || date2 == null)
      {
         return Duration.getInstance(0, TimeUnit.HOURS);
      }

      Duration variance = LocalDateTimeHelper.getVariance(relation.getPredecessorTask().getEffectiveCalendar(), date1, date2, TimeUnit.HOURS);
      return removeLag(relation, variance);
   }

   /**
    * Calculate free slack for an activity without successors.
    *
    * @param task activity
    * @return free slack value
    */
   private Duration calculateFreeSlackWithoutSuccessors(Task task)
   {
      if (task.getConstraintType() == ConstraintType.MUST_FINISH_ON || task.getConstraintType() == ConstraintType.MUST_START_ON)
      {
         return Duration.getInstance(0, TimeUnit.HOURS);
      }

      LocalDateTime projectFinishDate = task.getParentFile().getProjectProperties().getScheduledFinish();
      return LocalDateTimeHelper.getVariance(task.getEffectiveCalendar(), task.getEarlyFinish(), projectFinishDate, TimeUnit.HOURS);
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

   /**
    * Round a duration to the nearest minute.
    *
    * @param duration target duration
    * @return rounded duration
    */
   private Duration roundToMinutes(Duration duration)
   {
      if (duration.getUnits() != TimeUnit.HOURS)
      {
         throw new IllegalArgumentException();
      }

      return Duration.getInstance(Math.round(duration.getDuration() * 60.0) / 60.0, TimeUnit.HOURS);
   }

   private LocalDateTime getDataDate()
   {
      LocalDateTime dataDate = m_projectProperties.getStatusDate();
      return dataDate == null ? m_projectProperties.getStartDate() : dataDate;
   }

   private final ProjectProperties m_projectProperties;
   private final PrimaveraDateCalculator m_dateCalculator;
}
