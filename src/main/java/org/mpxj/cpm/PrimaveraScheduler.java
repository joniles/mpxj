/*
 * file:       PrimaveraScheduler.java
 * author:     Jon Iles
 * date:       2025-04-02
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

import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.mpxj.ActivityType;
import org.mpxj.ConstraintType;
import org.mpxj.DayType;
import org.mpxj.Duration;
import org.mpxj.LocalTimeRange;

import org.mpxj.PercentCompleteType;
import org.mpxj.ProjectCalendar;
import org.mpxj.ProjectCalendarHours;
import org.mpxj.ProjectFile;
import org.mpxj.Relation;
import org.mpxj.ResourceAssignment;
import org.mpxj.ResourceType;
import org.mpxj.Task;
import org.mpxj.TaskField;
import org.mpxj.TimeUnit;
import org.mpxj.common.LocalDateTimeHelper;

/**
 * Implements the Critical Path Method to schedule a project so
 * that the resulting schedule matches closely the results you'd see if
 * you scheduled the same project in Primavera P6.
 */
public class PrimaveraScheduler implements Scheduler
{
   @Override public void schedule(ProjectFile file, LocalDateTime startDate) throws CpmException
   {
      m_file = file;
      m_dataDate = file.getProjectProperties().getStatusDate();
      m_twentyFourHourCalendar = createTwentyFourHourCalendar();

      m_projectStartDate = startDate;

      List<Task> activities = new DepthFirstGraphSort(m_file, PrimaveraScheduler::isActivity).sort();
      if (activities.isEmpty())
      {
         return;
      }

      validateActivities(activities);

      clearDates();

      if (m_dataDate == null)
      {
         m_dataDate = m_projectStartDate;
      }
      else
      {
         if (startDate.isBefore(m_dataDate))
         {
            m_projectStartDate = m_dataDate;
         }
      }

      forwardPass(activities);

      LocalDateTime mustFinishBy = m_file.getProjectProperties().getMustFinishBy();
      LocalDateTime earlyFinish = activities.stream().map(Task::getEarlyFinish).max(Comparator.naturalOrder()).orElseThrow(() -> new CpmException("Missing early finish date"));

      if (mustFinishBy == null || earlyFinish.isAfter(mustFinishBy))
      {
         m_projectFinishDate = earlyFinish;
      }
      else
      {
         m_projectFinishDate = mustFinishBy;
      }

      backwardPass(activities);

      activities.forEach(this::updateDates);

      levelOfEffortPass();

      m_file.getChildTasks().forEach(this::rollupDates);

      wbsSummaryPass();
   }

   /**
    * Update Start, Finish, Planned Start, and Planned Finish dates.
    *
    * @param activity activity to update
    */
   private void updateDates(Task activity)
   {
      activity.setStart(activity.getActualStart() == null ? activity.getEarlyStart() : activity.getActualStart());
      activity.setFinish(activity.getActualFinish() == null ? activity.getEarlyFinish() : activity.getActualFinish());

      // P6 moves the planned start/finish dates for unstarted activities
      if (activity.getActualStart() == null)
      {
         activity.setPlannedStart(activity.getStart());
         activity.setPlannedFinish(activity.getFinish());
      }

      activity.getResourceAssignments().forEach(this::updateDates);
   }

   /**
    * Update the Remaining Early Start, Remaining Early Finish, Remaining Late Start, Remaining Late Finish, Start, Finish, Planned Start, and Planned Finish dates.
    *
    * @param assignment resource assignment to update
    */
   private void updateDates(ResourceAssignment assignment)
   {
      Task activity = assignment.getTask();
      if (activity.getActualFinish() != null)
      {
         assignment.setRemainingEarlyStart(null);
         assignment.setRemainingEarlyFinish(null);
         assignment.setRemainingLateStart(null);
         assignment.setRemainingLateFinish(null);
         return;
      }

      if (activity.getActualStart() == null)
      {
         assignment.setRemainingEarlyStart(assignment.getPlannedStart() != null && assignment.getPlannedStart().isAfter(activity.getRemainingEarlyStart()) ? assignment.getPlannedStart() : activity.getRemainingEarlyStart());
      }
      else
      {
         assignment.setRemainingEarlyStart(activity.getRemainingEarlyStart());
      }
      assignment.setRemainingLateFinish(activity.getRemainingLateFinish());

      if (activity.getActivityType() == ActivityType.LEVEL_OF_EFFORT || assignment.getResource().getType() == ResourceType.MATERIAL)
      {
         assignment.setRemainingEarlyFinish(activity.getRemainingEarlyFinish());
         assignment.setRemainingLateStart(activity.getRemainingLateStart());
      }
      else
      {
         // The case where a resource has zero work on a resource assignment is the
         // one case that we seem to have problems matching P6. Sometimes P6 will
         // set the Remaining Early Start and Remaining Early Finish to match the
         // activity. Sometimes it will set the Remaining Early Start to match the activity
         // and the Remaining Early Finish the same as the Remaining Early Start.
         // Can't get to the bottom of the logic it's using...
         if (assignment.getRemainingWork().getDuration() == 0.0)
         {
            if (assignment.getActualFinish() == null)
            {
               assignment.setRemainingEarlyFinish(activity.getRemainingEarlyFinish());
               assignment.setRemainingLateStart(activity.getRemainingLateStart());
            }
            else
            {
               assignment.setRemainingEarlyFinish(assignment.getRemainingEarlyStart());
               assignment.setRemainingLateStart(assignment.getRemainingLateFinish());
            }
         }
         else
         {
            assignment.setRemainingEarlyFinish(getEquivalentPreviousWorkFinish(getEffectiveCalendar(assignment), getDateFromWork(getEffectiveCalendar(assignment), assignment.getRemainingUnits(), assignment.getRemainingEarlyStart(), assignment.getRemainingWork())));
            assignment.setRemainingLateStart(getDateFromWork(getEffectiveCalendar(assignment), assignment.getRemainingUnits(), assignment.getRemainingLateFinish(), assignment.getRemainingWork().negate()));
         }
      }

      if (activity.getActualStart() == null && (assignment.getPlannedStart() == null || assignment.getRemainingEarlyStart().isAfter(assignment.getPlannedStart())))
      {
         assignment.setPlannedStart(assignment.getRemainingEarlyStart());
         assignment.setPlannedFinish(assignment.getRemainingEarlyFinish());
      }

      assignment.setStart(assignment.getActualStart() == null ? assignment.getRemainingEarlyStart() : assignment.getActualStart());
      assignment.setFinish(assignment.getActualFinish() == null ? assignment.getRemainingEarlyFinish() : assignment.getActualFinish());
   }

   /**
    * Determine the effective calendar to use for the given resource assignment.
    *
    * @param assignment resource assignment.
    * @return effective calendar
    */
   private ProjectCalendar getEffectiveCalendar(ResourceAssignment assignment)
   {
      return assignment.getTask().getActivityType() == ActivityType.RESOURCE_DEPENDENT ? assignment.getResource().getCalendar() : assignment.getEffectiveCalendar();
   }

   /**
    * Ensure that the activities in the project can be scheduled.
    *
    * @param tasks activities from the project
    */
   private void validateActivities(List<Task> tasks) throws CpmException
   {
      for (Task task : tasks)
      {
         if (task.getActivityType() == null)
         {
            throw new CpmException("Task has no activity type: " + task);
         }

         if (task.getActivityType() == ActivityType.RESOURCE_DEPENDENT && getResourceAssignmentStream(task).findAny().isPresent())
         {
            if (getResourceAssignmentStream(task).anyMatch(r -> r.getWork() == null))
            {
               throw new CpmException("Task has resource assignments without a work value: " + task);
            }

            if (getResourceAssignmentStream(task).anyMatch(r -> r.getRemainingWork() == null))
            {
               throw new CpmException("Task has resource assignments without a remaining work value: " + task);
            }
         }
         else
         {
            if (task.getDuration() == null)
            {
               throw new CpmException("Task has no duration value: " + task);
            }

            if (task.getRemainingDuration() == null)
            {
               throw new CpmException("Task has no remaining duration value: " + task);
            }
         }
      }
   }

   /**
    * Perform the CPM forward pass.
    *
    * @param tasks tasks in order for forward pass
    */
   private void forwardPass(List<Task> tasks) throws CpmException
   {
      for (Task task : tasks)
      {
         forwardPass(task);
      }
   }

   /**
    * Perform the CPM forward pass for this task.
    *
    * @param task task to schedule
    */
   private void forwardPass(Task task) throws CpmException
   {
      LocalDateTime earlyStart;

      LocalDateTime earlyFinish = null;
      List<Relation> predecessors = task.getPredecessors().stream().filter(r -> isActivity(r.getPredecessorTask())).collect(Collectors.toList());

      if (task.getActualStart() == null)
      {
         if (predecessors.isEmpty())
         {
            switch (getConstraintType(task))
            {
               case START_NO_EARLIER_THAN:
               {
                  earlyStart = task.getConstraintDate();
                  if (earlyStart.isBefore(m_dataDate))
                  {
                     earlyStart = m_dataDate;
                  }
                  break;
               }

               case FINISH_NO_EARLIER_THAN:
               {
                  earlyFinish = task.getConstraintDate();
                  earlyStart = getDateFromFinishAndDuration(task, earlyFinish);
                  break;
               }

               default:
               {
                  earlyStart = m_projectStartDate;
                  break;
               }
            }
         }
         else
         {
            earlyStart = predecessors.stream().map(this::calculateEarlyStart).max(Comparator.naturalOrder()).orElseThrow(() -> new CpmException("Missing early start date"));
         }

         switch (getConstraintType(task))
         {
            case START_NO_EARLIER_THAN:
            {
               if (earlyStart.isBefore(task.getConstraintDate()) || earlyStart.toLocalDate().isEqual(task.getConstraintDate().toLocalDate()))
               {
                  LocalDateTime constraintDate = task.getConstraintDate();

                  // I have an example where the applied constraint date seems to inherit the
                  // time component of the early start date it is replacing.
                  // Need more samples to determine if the logic here is anything like correct!
                  if (constraintDate.toLocalTime() == LocalTime.MIDNIGHT && earlyStart.toLocalTime() != LocalTime.MIDNIGHT)
                  {
                     LocalDateTime adjustedConstraintDate = LocalDateTime.of(constraintDate.toLocalDate(), earlyStart.toLocalTime());
                     if (adjustedConstraintDate.toLocalDate().isEqual(getNextWorkStart(task, adjustedConstraintDate).toLocalDate()))
                     {
                        constraintDate = adjustedConstraintDate;
                     }
                  }

                  earlyStart = constraintDate;
               }
               break;
            }

            case FINISH_NO_EARLIER_THAN:
            {
               LocalDateTime earliestStart = getDateFromFinishAndDuration(task, task.getConstraintDate());
               if (earlyStart.isBefore(earliestStart))
               {
                  earlyStart = earliestStart;
               }
               break;
            }

            case MUST_START_ON:
            {
               earlyStart = task.getConstraintDate();
               break;
            }

            case START_ON:
            {
               if (earlyStart.isBefore(task.getConstraintDate()))
               {
                  earlyStart = getNextWorkStart(task, task.getConstraintDate());
               }
               break;
            }

            case MUST_FINISH_ON:
            {
               earlyFinish = task.getConstraintDate();
               earlyStart = getDateFromFinishAndDuration(task, earlyFinish);
               break;
            }

            case FINISH_ON:
            {
               LocalDateTime startOn = getDateFromFinishAndDuration(task, task.getConstraintDate());
               if (startOn.isAfter(earlyStart))
               {
                  earlyFinish = task.getConstraintDate();
                  earlyStart = startOn;
               }
               break;
            }

            default:
            {
               break;
            }
         }

         // Don't adjust early start for a finish milestone
         if (task.getActivityType() != ActivityType.FINISH_MILESTONE)
         {
            // Next work start
            earlyStart = getNextWorkStart(task, earlyStart);
         }
      }
      else
      {
         if (task.getActualFinish() == null)
         {
            if (predecessors.isEmpty())
            {
               if (!hasActualDuration(task))
               {
                  earlyStart = getNextWorkStart(task, m_dataDate);
                  earlyFinish = getDateFromStartAndDuration(task, earlyStart);
               }
               else
               {
                  if (hasRemainingDuration(task))
                  {
                     earlyFinish = getDateFromStartAndDuration(task, task.getActualStart());
                     // Sometimes this instead... not sure why?
                     //earlyFinish = getNextWorkStart(task, getDateFromStart(task, task.getActualStart()));
                     earlyStart = getDateFromFinishAndRemainingDuration(task, earlyFinish);
                  }
                  else
                  {
                     earlyStart = getNextWorkStart(task, m_dataDate);
                     earlyFinish = earlyStart;
                  }
               }
            }
            else
            {
               earlyStart = getNextWorkStart(task, predecessors.stream().map(this::calculateEarlyStart).max(Comparator.naturalOrder()).orElseThrow(() -> new CpmException("Missing early start date")));
               earlyFinish = getDateFromStartAndRemainingDuration(task, earlyStart);
            }
         }
         else
         {
            if (predecessors.isEmpty())
            {
               earlyStart = m_dataDate;
               earlyFinish = m_dataDate;
            }
            else
            {
               earlyStart = predecessors.stream().map(this::calculateEarlyStart).max(Comparator.naturalOrder()).orElseThrow(() -> new CpmException("Missing early start date"));
               earlyFinish = getDateFromStartAndRemainingDuration(task, earlyStart);
            }
         }
      }

      if (task.getExternalEarlyStart() != null && task.getExternalEarlyStart().isAfter(earlyStart))
      {
         earlyStart = task.getExternalEarlyStart();
         earlyFinish = null;
      }

      if (earlyFinish == null)
      {
         earlyFinish = task.getActualFinish() == null ? getDateFromStartAndDuration(task, earlyStart) : task.getActualFinish();
      }

      task.setEarlyStart(earlyStart);
      task.setEarlyFinish(earlyFinish);

      setRemainingEarlyDates(task);
   }

   /**
    * Perform the CPM backward pass.
    *
    * @param forwardPassTasks tasks in order for forward pass
    */
   private void backwardPass(List<Task> forwardPassTasks) throws CpmException
   {
      List<Task> tasks = new ArrayList<>(forwardPassTasks);
      Collections.reverse(tasks);

      for (Task task : tasks)
      {
         backwardPass(task);
      }
   }

   /**
    * Perform the CPM backward pass for this task.
    *
    * @param task task to schedule
    */
   private void backwardPass(Task task) throws CpmException
   {
      List<Relation> successors = m_file.getRelations().getSuccessors(task).stream().filter(r -> isActivity(r.getSuccessorTask())).collect(Collectors.toList());
      LocalDateTime lateFinish;

      if (task.getActualFinish() == null)
      {
         if (successors.isEmpty())
         {
            if (task.getExternalLateFinish() == null)
            {
               if (m_file.getProjectProperties().getMustFinishBy() != null)
               {
                  lateFinish = m_file.getProjectProperties().getMustFinishBy();
               }
               else
               {
                  lateFinish = m_projectFinishDate;
               }
            }
            else
            {
               lateFinish = task.getExternalLateFinish();
            }
         }
         else
         {
            lateFinish = successors.stream().map(this::calculateLateFinish).min(Comparator.naturalOrder()).orElseThrow(() -> new CpmException("Missing late start date"));
         }

         switch (getConstraintType(task))
         {
            case START_ON:
            {
               if (task.getActualStart() == null)
               {
                  LocalDateTime latestFinish = getDateFromStartAndDuration(task, task.getConstraintDate());
                  if (lateFinish.isAfter(latestFinish))
                  {
                     lateFinish = latestFinish;
                  }
               }
               break;
            }

            case MUST_START_ON:
            {
               if (task.getActualStart() == null)
               {
                  lateFinish = getDateFromStartAndDuration(task, task.getConstraintDate());
               }
               break;
            }

            case MUST_FINISH_ON:
            {
               lateFinish = task.getConstraintDate();
               break;
            }

            case START_NO_LATER_THAN:
            {
               LocalDateTime latestFinish = getDateFromStartAndDuration(task, task.getConstraintDate());
               if (lateFinish.isAfter(latestFinish))
               {
                  lateFinish = latestFinish;
               }
               break;
            }

            case FINISH_ON:
            case FINISH_NO_LATER_THAN:
            {
               if (lateFinish.isAfter(task.getConstraintDate()))
               {
                  lateFinish = task.getConstraintDate();
               }
               break;
            }

            default:
            {
               break;
            }
         }

         if (task.getSecondaryConstraintType() != null)
         {
            switch (task.getSecondaryConstraintType())
            {
               case START_NO_LATER_THAN:
               {
                  LocalDateTime latestFinish = getDateFromStartAndDuration(task, task.getSecondaryConstraintDate());
                  if (lateFinish.isAfter(latestFinish))
                  {
                     lateFinish = latestFinish;
                  }
                  break;
               }

               case FINISH_NO_LATER_THAN:
               {
                  if (lateFinish.isAfter(task.getSecondaryConstraintDate()))
                  {
                     lateFinish = task.getSecondaryConstraintDate();
                  }
                  break;
               }

               default:
               {
                  break;
               }
            }
         }

         // If we are at the start of the next period of work, we can move back to the end of the previous period of work
         lateFinish = getEquivalentPreviousWorkFinish(task, lateFinish);
      }
      else
      {
         if (successors.isEmpty())
         {
            if (m_file.getProjectProperties().getMustFinishBy() != null)
            {
               lateFinish = m_file.getProjectProperties().getMustFinishBy();
            }
            else
            {
               lateFinish = m_projectFinishDate;
            }
         }
         else
         {
            lateFinish = successors.stream().map(this::calculateLateFinish).min(Comparator.naturalOrder()).orElseThrow(() -> new CpmException("Missing late start date"));
         }
      }

      if (task.getExternalLateFinish() != null && task.getExternalLateFinish().isBefore(lateFinish))
      {
         lateFinish = task.getExternalLateFinish();
      }

      LocalDateTime lateStart = getDateFromFinishAndRemainingDuration(task, lateFinish);
      if (task.getActivityType() == ActivityType.START_MILESTONE)
      {
         lateStart = getNextWorkStart(task, lateStart);
      }
      else
      {
         if (task.getActivityType() != ActivityType.FINISH_MILESTONE && hasRemainingDuration(task))
         {
            lateStart = getNextWorkStart(task, lateStart);
         }
      }

      task.setLateStart(lateStart);
      task.setLateFinish(lateFinish);
      setRemainingLateDates(task);

      if (task.getConstraintType() == ConstraintType.AS_LATE_AS_POSSIBLE)
      {
         alapAdjust(task);
      }
   }

   /**
    * Calculate the early start for the successor task in this relationship.
    *
    * @param relation relationship between two tasks
    * @return calculated early start date
    */
   private LocalDateTime calculateEarlyStart(Relation relation)
   {
      switch (relation.getType())
      {
         case FINISH_START:
         {
            return calculateEarlyStartForFinishStart(relation);
         }

         case START_START:
         {
            return calculateEarlyStartForStartStart(relation);
         }

         case FINISH_FINISH:
         {
            return calculateEarlyStartForFinishFinish(relation);
         }

         case START_FINISH:
         {
            return calculateEarlyStartForStartFinish(relation);
         }

         default:
         {
            throw new UnsupportedOperationException();
         }
      }
   }

   /**
    * Calculate the early start for the successor task in a finish-start relationship.
    *
    * @param relation relationship between two tasks
    * @return calculated early start date
    */
   private LocalDateTime calculateEarlyStartForFinishStart(Relation relation)
   {
      Task predecessorTask = relation.getPredecessorTask();
      Task successorTask = relation.getSuccessorTask();

      if (predecessorTask.getActualStart() == null)
      {
         // Predecessor not started
         if (successorTask.getActualStart() == null)
         {
            // Successor not started
            if (relation.getLag().getDuration() == 0)
            {
               return predecessorTask.getEarlyFinish();
            }

            if (relation.getLag().getDuration() > 0)
            {
               return addLag(relation, predecessorTask.getEarlyFinish());
            }

            return addLag(relation, predecessorTask.getEarlyFinish());
         }

         // successor started
         if (successorTask.getActualFinish() == null)
         {
            // successor not finished
            if (relation.getLag().getDuration() == 0)
            {
               return addLag(relation, predecessorTask.getEarlyFinish());
            }

            if (relation.getLag().getDuration() > 0)
            {
               return addLag(relation, predecessorTask.getEarlyFinish());
            }

            return addLag(relation, predecessorTask.getEarlyFinish());
         }

         // successor finished
         if (relation.getLag().getDuration() == 0)
         {
            return addLag(relation, predecessorTask.getEarlyFinish());
         }

         if (relation.getLag().getDuration() > 0)
         {
            return addLag(relation, predecessorTask.getEarlyFinish());
         }

         return addLag(relation, predecessorTask.getEarlyFinish());
      }

      // Predecessor started
      if (predecessorTask.getActualFinish() != null)
      {
         // Predecessor finished
         if (successorTask.getActualStart() == null)
         {
            // Successor not started
            if (relation.getLag().getDuration() == 0)
            {
               return predecessorTask.getEarlyFinish();
            }

            if (relation.getLag().getDuration() > 0)
            {
               double actualLagDurationInHours = predecessorTask.getActualFinish().isAfter(m_dataDate) ? 0 : getLagCalendar(relation).getWork(predecessorTask.getActualFinish(), m_dataDate, TimeUnit.HOURS).getDuration();
               double lagDurationInHours = relation.getLag().convertUnits(TimeUnit.HOURS, m_file.getProjectProperties()).getDuration();
               if (lagDurationInHours > actualLagDurationInHours)
               {
                  Duration remainingLag = Duration.getInstance(lagDurationInHours - actualLagDurationInHours, TimeUnit.HOURS);
                  return addLag(relation, predecessorTask.getEarlyFinish(), remainingLag);
               }
               return predecessorTask.getEarlyFinish();
            }

            return predecessorTask.getEarlyFinish();
         }

         // successor started
         if (successorTask.getActualFinish() == null)
         {
            // successor not finished
            if (relation.getLag().getDuration() == 0)
            {
               return predecessorTask.getEarlyFinish();
            }

            if (relation.getLag().getDuration() > 0)
            {
               return predecessorTask.getEarlyFinish();
            }

            return predecessorTask.getEarlyFinish();
         }

         // successor finished
         if (relation.getLag().getDuration() == 0)
         {
            return predecessorTask.getEarlyFinish();
         }

         if (relation.getLag().getDuration() > 0)
         {
            return predecessorTask.getEarlyFinish();
         }

         return predecessorTask.getEarlyFinish();
      }

      // Predecessor not finished
      if (successorTask.getActualStart() == null)
      {
         // Successor not started
         if (relation.getLag().getDuration() == 0)
         {
            getNextWorkStart(successorTask, predecessorTask.getEarlyFinish());
         }

         if (relation.getLag().getDuration() > 0)
         {
            return addLag(relation, predecessorTask.getEarlyFinish());
         }

         return addLag(relation, predecessorTask.getEarlyFinish());
      }

      // successor started
      if (successorTask.getActualFinish() == null)
      {
         // successor not finished
         if (relation.getLag().getDuration() == 0)
         {
            return addLag(relation, predecessorTask.getEarlyFinish());
         }

         if (relation.getLag().getDuration() > 0)
         {
            return addLag(relation, predecessorTask.getEarlyFinish());
         }

         return addLag(relation, predecessorTask.getEarlyFinish());
      }

      // successor finished
      if (relation.getLag().getDuration() == 0)
      {
         return addLag(relation, predecessorTask.getEarlyFinish());
      }

      if (relation.getLag().getDuration() > 0)
      {
         return addLag(relation, predecessorTask.getEarlyFinish());
      }

      return addLag(relation, predecessorTask.getEarlyFinish());
   }

   /**
    * Calculate the early start for the successor task in a start-start relationship.
    *
    * @param relation relationship between two tasks
    * @return calculated early start date
    */
   private LocalDateTime calculateEarlyStartForStartStart(Relation relation)
   {
      Task predecessorTask = relation.getPredecessorTask();
      Task successorTask = relation.getSuccessorTask();

      if (predecessorTask.getActualStart() == null)
      {
         // Predecessor not started
         if (successorTask.getActualStart() == null)
         {
            // Successor not started
            if (relation.getLag().getDuration() == 0)
            {
               // why adjust the next work start with the lag calendar? not sure, but it seems to work ;-)
               return getLagCalendar(relation).getNextWorkStart(predecessorTask.getEarlyStart());
            }

            if (relation.getLag().getDuration() > 0)
            {
               return addLag(relation, predecessorTask.getEarlyStart());
            }

            return addLag(relation, predecessorTask.getEarlyStart());
         }

         // successor started
         if (successorTask.getActualFinish() == null)
         {
            // successor not finished
            if (relation.getLag().getDuration() == 0)
            {
               return addLag(relation, predecessorTask.getEarlyStart());
            }

            if (relation.getLag().getDuration() > 0)
            {
               return addLag(relation, predecessorTask.getEarlyStart());
            }

            return addLag(relation, predecessorTask.getEarlyStart());
         }

         // successor finished
         if (relation.getLag().getDuration() == 0)
         {
            return addLag(relation, predecessorTask.getEarlyStart());
         }

         if (relation.getLag().getDuration() > 0)
         {
            return addLag(relation, predecessorTask.getEarlyStart());
         }

         return addLag(relation, predecessorTask.getEarlyStart());
      }

      // Predecessor started
      if (predecessorTask.getActualFinish() != null)
      {
         // Predecessor finished
         if (successorTask.getActualStart() == null)
         {
            // Successor not started
            if (relation.getLag().getDuration() == 0)
            {
               LocalDateTime earlyStart = addLag(relation, predecessorTask.getActualStart());
               if (earlyStart.isBefore(m_dataDate))
               {
                  return predecessorTask.getEarlyStart();
               }
               return earlyStart;
            }

            if (relation.getLag().getDuration() > 0)
            {
               LocalDateTime earlyStart = addLag(relation, predecessorTask.getActualStart());
               if (earlyStart.isBefore(m_dataDate))
               {
                  return predecessorTask.getEarlyStart();
               }
               return earlyStart;
            }

            return addLag(relation, predecessorTask.getActualStart());
         }

         // successor started
         if (successorTask.getActualFinish() == null)
         {
            // successor not finished
            if (relation.getLag().getDuration() == 0)
            {
               return predecessorTask.getEarlyStart();
            }

            if (relation.getLag().getDuration() > 0)
            {
               LocalDateTime earlyStart = addLag(relation, predecessorTask.getActualStart());
               if (earlyStart.isBefore(m_dataDate))
               {
                  return predecessorTask.getEarlyStart();
               }
               return earlyStart;
            }

            return predecessorTask.getEarlyStart();
         }

         // successor finished
         if (relation.getLag().getDuration() == 0)
         {
            return predecessorTask.getEarlyStart();
         }

         if (relation.getLag().getDuration() > 0)
         {
            return predecessorTask.getEarlyStart();
         }

         return predecessorTask.getEarlyStart();
      }

      // Predecessor not finished
      if (successorTask.getActualStart() == null)
      {
         // Successor not started
         double lagDurationInHours = relation.getLag().convertUnits(TimeUnit.HOURS, m_file.getProjectProperties()).getDuration();
         double actualDurationInHours = predecessorTask.getActualDuration().convertUnits(TimeUnit.HOURS, m_file.getProjectProperties()).getDuration();

         if (actualDurationInHours == 0 || lagDurationInHours <= 0.0)
         {
            // We have a milestone, or we have no positive lag
            return predecessorTask.getEarlyStart();
         }

         if (actualDurationInHours >= lagDurationInHours)
         {
            // We have progressed more than the lag
            return predecessorTask.getEarlyStart();
         }

         // We still need to account for some or all of the lag
         Duration remainingLag = Duration.getInstance(lagDurationInHours - actualDurationInHours, TimeUnit.HOURS);
         return addLag(relation, predecessorTask.getEarlyStart(), remainingLag);
      }

      // successor started
      if (successorTask.getActualFinish() == null)
      {
         // successor not finished
         double lagDurationInHours = relation.getLag().convertUnits(TimeUnit.HOURS, m_file.getProjectProperties()).getDuration();
         if (lagDurationInHours <= 0.0)
         {
            // We have no positive lag
            return predecessorTask.getEarlyStart();
         }

         double actualDurationInHours = predecessorTask.getActualDuration().convertUnits(TimeUnit.HOURS, m_file.getProjectProperties()).getDuration();
         if (actualDurationInHours >= lagDurationInHours)
         {
            // We have progressed more than the lag
            return predecessorTask.getEarlyStart();
         }

         // We still need to account for some or all of the lag
         Duration remainingLag = Duration.getInstance(lagDurationInHours - actualDurationInHours, TimeUnit.HOURS);
         return addLag(relation, predecessorTask.getEarlyStart(), remainingLag);
      }

      // successor finished
      if (relation.getLag().getDuration() == 0)
      {
         return predecessorTask.getEarlyStart();
      }

      if (relation.getLag().getDuration() > 0)
      {
         return predecessorTask.getEarlyStart();
      }

      return predecessorTask.getEarlyStart();
   }

   /**
    * Calculate the early start for the successor task in a finish-finish relationship.
    *
    * @param relation relationship between two tasks
    * @return calculated early start date
    */
   private LocalDateTime calculateEarlyStartForFinishFinish(Relation relation)
   {
      Task predecessorTask = relation.getPredecessorTask();
      Task successorTask = relation.getSuccessorTask();

      if (predecessorTask.getActualStart() == null)
      {
         // Predecessor not started
         if (successorTask.getActualStart() == null)
         {
            // Successor not started
            if (relation.getLag().getDuration() == 0)
            {
               return getEarlyStartFromEarlyFinish(successorTask, predecessorTask.getEarlyFinish());
            }

            if (relation.getLag().getDuration() > 0)
            {
               return getEarlyStartFromEarlyFinish(successorTask, addLag(relation, predecessorTask.getEarlyFinish()));
            }

            return getEarlyStartFromEarlyFinish(successorTask, addLag(relation, predecessorTask.getEarlyFinish()));
         }

         // successor started
         if (successorTask.getActualFinish() == null)
         {
            // successor not finished
            if (relation.getLag().getDuration() == 0)
            {
               return getEarlyStartFromEarlyFinish(successorTask, predecessorTask.getEarlyFinish());
            }

            if (relation.getLag().getDuration() > 0)
            {
               return getEarlyStartFromEarlyFinish(successorTask, addLag(relation, predecessorTask.getEarlyFinish()));
            }

            return getEarlyStartFromEarlyFinish(successorTask, addLag(relation, predecessorTask.getEarlyFinish()));
         }

         // successor finished
         if (relation.getLag().getDuration() == 0)
         {
            return getEarlyStartFromEarlyFinish(successorTask, predecessorTask.getEarlyFinish());
         }

         if (relation.getLag().getDuration() > 0)
         {
            return getEarlyStartFromEarlyFinish(successorTask, addLag(relation, predecessorTask.getEarlyFinish()));
         }

         return getEarlyStartFromEarlyFinish(successorTask, addLag(relation, predecessorTask.getEarlyFinish()));
      }

      // Predecessor started
      if (predecessorTask.getActualFinish() != null)
      {
         // Predecessor finished
         if (successorTask.getActualStart() == null)
         {
            // Successor not started
            if (relation.getLag().getDuration() == 0.0)
            {
               return getEarlyStartFromEarlyFinish(successorTask, predecessorTask.getActualFinish());
            }

            if (relation.getLag().getDuration() > 0.0)
            {
               double lagDurationInHours = relation.getLag().convertUnits(TimeUnit.HOURS, m_file.getProjectProperties()).getDuration();
               double actualDurationInHours = predecessorTask.getActualDuration().convertUnits(TimeUnit.HOURS, m_file.getProjectProperties()).getDuration();
               if (lagDurationInHours > actualDurationInHours)
               {
                  return getEarlyStartFromEarlyFinish(successorTask, addLag(relation, predecessorTask.getActualFinish()));
               }

               return getEarlyStartFromEarlyFinish(successorTask, addLag(relation, addLag(relation, predecessorTask.getEarlyFinish())));
            }

            return getEarlyStartFromEarlyFinish(successorTask, addLag(relation, addLag(relation, predecessorTask.getEarlyFinish())));
         }

         // successor started
         if (successorTask.getActualFinish() == null)
         {
            // successor not finished
            if (relation.getLag().getDuration() == 0.0)
            {
               return getEarlyStartFromEarlyFinish(successorTask, predecessorTask.getActualFinish());
            }

            if (relation.getLag().getDuration() > 0.0)
            {
               return getEarlyStartFromEarlyFinish(successorTask, addLag(relation, predecessorTask.getActualFinish()));
            }

            return getEarlyStartFromEarlyFinish(successorTask, addLag(relation, predecessorTask.getActualFinish()));
         }

         // successor finished
         if (relation.getLag().getDuration() == 0.0)
         {
            return m_dataDate;
            // but sometimes it is
            // getLagCalendar(relation).getNextWorkStart(m_dataDate)
            // why? remaining lag maybe?
         }

         if (relation.getLag().getDuration() > 0.0)
         {
            return m_dataDate;
            // but sometimes it is
            // getLagCalendar(relation).getNextWorkStart(m_dataDate)
            // why? remaining lag maybe?
         }

         return m_dataDate;
         // but sometimes it is
         // getLagCalendar(relation).getNextWorkStart(m_dataDate)
         // why? remaining lag maybe?
      }

      // Predecessor not finished
      if (successorTask.getActualStart() == null)
      {
         // Successor not started
         if (relation.getLag().getDuration() == 0.0)
         {
            return getEarlyStartFromEarlyFinish(successorTask, predecessorTask.getEarlyFinish());
         }

         if (relation.getLag().getDuration() > 0.0)
         {
            return getEarlyStartFromEarlyFinish(successorTask, addLag(relation, predecessorTask.getEarlyFinish()));
         }

         return getEarlyStartFromEarlyFinish(successorTask, addLag(relation, predecessorTask.getEarlyFinish()));
      }

      // successor started
      if (successorTask.getActualFinish() == null)
      {
         // successor not finished
         if (relation.getLag().getDuration() == 0.0)
         {
            return getEarlyStartFromEarlyFinish(successorTask, predecessorTask.getEarlyFinish());
         }

         if (relation.getLag().getDuration() > 0.0)
         {
            return getEarlyStartFromEarlyFinish(successorTask, addLag(relation, predecessorTask.getEarlyFinish()));
         }

         return getEarlyStartFromEarlyFinish(successorTask, addLag(relation, predecessorTask.getEarlyFinish()));
      }

      // successor finished
      if (relation.getLag().getDuration() == 0.0)
      {
         return getEarlyStartFromEarlyFinish(successorTask, predecessorTask.getEarlyFinish());
      }

      if (relation.getLag().getDuration() > 0.0)
      {
         return getEarlyStartFromEarlyFinish(successorTask, addLag(relation, predecessorTask.getEarlyFinish()));
      }

      return getEarlyStartFromEarlyFinish(successorTask, addLag(relation, predecessorTask.getEarlyFinish()));
   }

   /**
    * Calculate the early start for the successor task in a start-finish relationship.
    *
    * @param relation relationship between two tasks
    * @return calculated early start date
    */
   private LocalDateTime calculateEarlyStartForStartFinish(Relation relation)
   {
      Task predecessorTask = relation.getPredecessorTask();
      Task successorTask = relation.getSuccessorTask();
      LocalDateTime earlyStart;

      if (predecessorTask.getActualStart() == null)
      {
         // Predecessor not started
         if (successorTask.getActualStart() == null)
         {
            // Successor not started
            if (relation.getLag().getDuration() == 0)
            {
               earlyStart = addLag(relation, getDateFromFinishAndDuration(successorTask, predecessorTask.getEarlyStart()));
            }
            else
            {
               earlyStart = addLag(relation, getDateFromFinishAndDuration(successorTask, predecessorTask.getEarlyStart()));
            }
         }
         else
         {
            // successor started
            if (successorTask.getActualFinish() == null)
            {
               // successor not finished
               if (relation.getLag().getDuration() <= 0)
               {
                  LocalDateTime earlyFinish = getDateFromStartAndDuration(successorTask, successorTask.getActualStart());
                  earlyStart = getDateFromFinishAndRemainingDuration(successorTask, earlyFinish);
               }
               else
               {
                  LocalDateTime earlyFinish = addLag(relation, predecessorTask.getEarlyStart());
                  earlyStart = getDateFromFinishAndRemainingDuration(successorTask, earlyFinish);
               }
            }
            else
            {
               // successor finished
               if (relation.getLag().getDuration() == 0)
               {
                  earlyStart = predecessorTask.getEarlyStart();
               }
               else
               {
                  earlyStart = addLag(relation, predecessorTask.getEarlyStart());
               }
            }
         }
      }
      else
      {
         // Predecessor started
         if (predecessorTask.getActualFinish() != null)
         {
            // Predecessor finished
            if (successorTask.getActualStart() == null)
            {
               // Successor not started
               if (relation.getLag().getDuration() == 0)
               {
                  earlyStart = addLag(relation, getDateFromFinishAndDuration(successorTask, predecessorTask.getEarlyStart()));
               }
               else
               {
                  earlyStart = addLag(relation, getDateFromFinishAndDuration(successorTask, predecessorTask.getEarlyStart()));
               }
            }
            else
            {
               // successor started
               if (successorTask.getActualFinish() == null)
               {
                  // successor not finished
                  double actualLagDurationInHours = predecessorTask.getActualStart().isAfter(m_dataDate) ? 0 : getLagCalendar(relation).getWork(predecessorTask.getActualStart(), m_dataDate, TimeUnit.HOURS).getDuration();
                  double lagDurationInHours = relation.getLag().convertUnits(TimeUnit.HOURS, m_file.getProjectProperties()).getDuration();

                  if (lagDurationInHours > actualLagDurationInHours)
                  {
                     Duration remainingLag = Duration.getInstance(lagDurationInHours - actualLagDurationInHours, TimeUnit.HOURS);
                     LocalDateTime earlyFinish = addLag(relation, predecessorTask.getEarlyStart(), remainingLag);
                     earlyStart = getDateFromFinishAndRemainingDuration(successorTask, earlyFinish);
                  }
                  else
                  {
                     earlyStart = predecessorTask.getEarlyStart();
                  }
               }
               else
               {
                  // successor finished
                  if (relation.getLag().getDuration() == 0)
                  {
                     earlyStart = predecessorTask.getEarlyStart();
                  }
                  else
                  {
                     earlyStart = addLag(relation, predecessorTask.getEarlyStart());
                     if (earlyStart.isAfter(m_dataDate))
                     {
                        earlyStart = m_dataDate;
                     }
                  }
               }
            }
         }
         else
         {
            // Predecessor not finished
            if (successorTask.getActualStart() == null)
            {
               // Successor not started
               if (relation.getLag().getDuration() == 0)
               {
                  earlyStart = addLag(relation, getDateFromFinishAndDuration(successorTask, predecessorTask.getEarlyStart()));
                  if (earlyStart.isAfter(m_dataDate))
                  {
                     earlyStart = m_dataDate;
                  }
               }
               else
               {
                  earlyStart = addLag(relation, getDateFromFinishAndDuration(successorTask, predecessorTask.getEarlyStart()));
                  if (earlyStart.isAfter(m_dataDate))
                  {
                     earlyStart = m_dataDate;
                  }
               }
            }
            else
            {
               // successor started
               if (successorTask.getActualFinish() == null)
               {
                  // successor not finished
                  double actualLagDurationInHours = predecessorTask.getActualStart().isAfter(m_dataDate) ? 0 : getLagCalendar(relation).getWork(predecessorTask.getActualStart(), m_dataDate, TimeUnit.HOURS).getDuration();
                  double lagDurationInHours = relation.getLag().convertUnits(TimeUnit.HOURS, m_file.getProjectProperties()).getDuration();

                  if (lagDurationInHours > actualLagDurationInHours)
                  {
                     Duration remainingLag = Duration.getInstance(lagDurationInHours - actualLagDurationInHours, TimeUnit.HOURS);
                     LocalDateTime earlyFinish = addLag(relation, predecessorTask.getEarlyStart(), remainingLag);
                     earlyStart = getDateFromFinishAndRemainingDuration(successorTask, earlyFinish);
                  }
                  else
                  {
                     earlyStart = predecessorTask.getEarlyStart();
                  }
               }
               else
               {
                  if (relation.getLag().getDuration() == 0)
                  {
                     // successor finished
                     earlyStart = predecessorTask.getEarlyStart();
                  }
                  else
                  {
                     // successor finished
                     earlyStart = getEquivalentNextWorkStart(successorTask, m_dataDate);
                  }
               }
            }
         }
      }

      return earlyStart;
   }

   /**
    * Calculate the early start date from an early finish date and ensure this is at
    * or after the project start date.
    *
    * @param successorTask successor task
    * @param earlyFinish early finish date
    * @return early start date
    */
   private LocalDateTime getEarlyStartFromEarlyFinish(Task successorTask, LocalDateTime earlyFinish)
   {
      LocalDateTime earlyStart = getDateFromFinishAndRemainingDuration(successorTask, earlyFinish);
      if (earlyStart.isBefore(m_projectStartDate))
      {
         return m_projectStartDate;
      }
      return earlyStart;
   }

   /**
    * Calculate the late finish for the predecessor task in this relationship.
    *
    * @param relation relationship between two tasks
    * @return calculated late finish date
    */
   private LocalDateTime calculateLateFinish(Relation relation)
   {
      switch (relation.getType())
      {
         case START_START:
         {
            return adjustLateFinish(relation, calculateLateFinishForStartStart(relation));
         }

         case FINISH_FINISH:
         {
            return adjustLateFinish(relation, calculateLateFinishForFinishFinish(relation));
         }

         case START_FINISH:
         {
            return adjustLateFinish(relation, calculateLateFinishForStartFinish(relation));
         }

         case FINISH_START:
         {
            return adjustLateFinish(relation, calculateLateFinishForFinishStart(relation));
         }

         default:
         {
            throw new UnsupportedOperationException();
         }
      }
   }

   /**
    * Ensure a late finish date is before the project finish date, and adjust if required.
    *
    * @param relation parent relation
    * @param lateFinish late finish date
    * @return late finish
    */
   private LocalDateTime adjustLateFinish(Relation relation, LocalDateTime lateFinish)
   {
      if (lateFinish.isAfter(m_projectFinishDate))
      {
         // If we're between working periods, move back to the last work finish
         lateFinish = getEquivalentPreviousWorkFinish(relation.getPredecessorTask(), m_projectFinishDate);
      }

      return lateFinish;
   }

   /**
    * Calculate the late finish for the predecessor task in a start-start relationship.
    *
    * @param relation relationship between two tasks
    * @return calculated late finish date
    */
   private LocalDateTime calculateLateFinishForStartStart(Relation relation)
   {
      Task predecessorTask = relation.getPredecessorTask();
      Task successorTask = relation.getSuccessorTask();
      LocalDateTime lateFinish = null;

      LocalDateTime lateStart;

      if (predecessorTask.getActualStart() == null)
      {
         // Predecessor not started
         if (successorTask.getActualStart() == null)
         {
            // Successor not started

            if (relation.getLag().getDuration() == 0)
            {
               lateStart = getNextWorkStart(predecessorTask, successorTask.getLateStart());
               // Sometimes this - why?
               //lateStart = predecessorTask.getEffectiveCalendar().getPreviousWorkFinish(successorTask.getLateStart());
               lateFinish = getDateFromStartAndRemainingDuration(predecessorTask, lateStart);

               // Hmmm... dubious logic. Does this just work for indefensible-tedium or is this general?
               if (successorTask.getSuccessors().isEmpty() && successorTask.getLateFinish().isBefore(lateFinish))
               {
                  lateFinish = successorTask.getLateFinish();
               }
            }
            else
            {
               if (relation.getLag().getDuration() > 0)
               {
                  lateStart = getNextWorkStart(predecessorTask, removeLag(relation, successorTask.getLateStart()));
                  lateFinish = getDateFromStartAndRemainingDuration(predecessorTask, lateStart);
               }
               else
               {
                  lateStart = getNextWorkStart(predecessorTask, removeLag(relation, successorTask.getLateStart()));
                  lateFinish = getDateFromStartAndRemainingDuration(predecessorTask, lateStart);
               }
            }
         }
         else
         {
            // successor started
            if (successorTask.getActualFinish() == null)
            {
               // successor not finished
               if (relation.getLag().getDuration() == 0)
               {
                  lateStart = removeLag(relation, successorTask.getLateStart());
               }
               else
               {
                  if (relation.getLag().getDuration() > 0)
                  {
                     lateStart = removeLag(relation, successorTask.getLateStart());
                  }
                  else
                  {
                     lateStart = removeLag(relation, successorTask.getLateStart());
                  }
               }
            }
            else
            {
               // successor finished
               if (relation.getLag().getDuration() == 0)
               {
                  lateStart = removeLag(relation, successorTask.getLateStart());
               }
               else
               {
                  if (relation.getLag().getDuration() > 0)
                  {
                     lateStart = removeLag(relation, successorTask.getLateStart());
                  }
                  else
                  {
                     lateStart = removeLag(relation, successorTask.getLateStart());
                  }
               }
            }
         }
      }
      else
      {
         // Predecessor Started
         if (predecessorTask.getActualFinish() != null)
         {
            // Predecessor finished
            if (successorTask.getActualStart() == null)
            {
               // Successor not started
               if (relation.getLag().getDuration() == 0)
               {
                  lateStart = successorTask.getLateStart();
               }
               else
               {
                  if (relation.getLag().getDuration() > 0)
                  {
                     double actualLagDurationInHours = predecessorTask.getActualStart().isAfter(m_dataDate) ? 0 : getLagCalendar(relation).getWork(predecessorTask.getActualStart(), m_dataDate, TimeUnit.HOURS).getDuration();
                     double lagDurationInHours = relation.getLag().convertUnits(TimeUnit.HOURS, m_file.getProjectProperties()).getDuration();

                     if (lagDurationInHours > actualLagDurationInHours)
                     {
                        Duration remainingLag = Duration.getInstance(lagDurationInHours - actualLagDurationInHours, TimeUnit.HOURS);
                        lateStart = removeLag(relation, successorTask.getLateStart(), remainingLag);
                     }
                     else
                     {
                        lateStart = successorTask.getLateStart();
                     }
                  }
                  else
                  {
                     lateStart = successorTask.getLateStart();
                  }
               }
            }
            else
            {
               // successor started
               if (successorTask.getActualFinish() == null)
               {
                  // successor not finished
                  if (relation.getLag().getDuration() == 0)
                  {
                     lateStart = successorTask.getLateStart();
                  }
                  else
                  {
                     if (relation.getLag().getDuration() > 0)
                     {
                        lateStart = successorTask.getLateStart();
                     }
                     else
                     {
                        lateStart = successorTask.getLateStart();
                     }
                  }
               }
               else
               {
                  // successor finished
                  if (relation.getLag().getDuration() == 0)
                  {
                     lateStart = successorTask.getLateStart();
                  }
                  else
                  {
                     if (relation.getLag().getDuration() > 0)
                     {
                        double actualLagDurationInHours = predecessorTask.getActualStart().isAfter(m_dataDate) ? 0 : getLagCalendar(relation).getWork(predecessorTask.getActualStart(), m_dataDate, TimeUnit.HOURS).getDuration();
                        double lagDurationInHours = relation.getLag().convertUnits(TimeUnit.HOURS, m_file.getProjectProperties()).getDuration();

                        if (lagDurationInHours > actualLagDurationInHours)
                        {
                           Duration remainingLag = Duration.getInstance(lagDurationInHours - actualLagDurationInHours, TimeUnit.HOURS);
                           lateStart = removeLag(relation, successorTask.getLateStart(), remainingLag);
                        }
                        else
                        {
                           lateStart = successorTask.getLateStart();
                        }
                     }
                     else
                     {
                        lateStart = successorTask.getLateStart();
                     }
                  }
               }
            }
         }
         else
         {
            // Predecessor not finished
            if (successorTask.getActualStart() == null)
            {
               // Successor not started
               if (relation.getLag().getDuration() == 0)
               {
                  lateStart = successorTask.getLateStart();
               }
               else
               {
                  if (relation.getLag().getDuration() > 0)
                  {
                     double actualDurationInHours = predecessorTask.getActualDuration().convertUnits(TimeUnit.HOURS, m_file.getProjectProperties()).getDuration();
                     double lagDurationInHours = relation.getLag().convertUnits(TimeUnit.HOURS, m_file.getProjectProperties()).getDuration();

                     if (actualDurationInHours >= lagDurationInHours)
                     {
                        lateStart = successorTask.getLateStart();
                     }
                     else
                     {
                        Duration remainingLag = Duration.getInstance(lagDurationInHours - actualDurationInHours, TimeUnit.HOURS);
                        lateStart = removeLag(relation, successorTask.getLateStart(), remainingLag);
                     }
                  }
                  else
                  {
                     lateStart = successorTask.getLateStart();
                  }
               }
            }
            else
            {
               // successor started
               if (successorTask.getActualFinish() == null)
               {
                  // successor not finished
                  if (relation.getLag().getDuration() == 0)
                  {
                     lateStart = successorTask.getLateStart();
                  }
                  else
                  {
                     if (relation.getLag().getDuration() > 0)
                     {
                        double actualDurationInHours = predecessorTask.getActualDuration().convertUnits(TimeUnit.HOURS, m_file.getProjectProperties()).getDuration();
                        double lagDurationInHours = relation.getLag().convertUnits(TimeUnit.HOURS, m_file.getProjectProperties()).getDuration();

                        if (actualDurationInHours >= lagDurationInHours)
                        {
                           lateStart = successorTask.getLateStart();
                        }
                        else
                        {
                           Duration remainingLag = Duration.getInstance(lagDurationInHours - actualDurationInHours, TimeUnit.HOURS);
                           lateStart = removeLag(relation, successorTask.getLateStart(), remainingLag);
                        }
                     }
                     else
                     {
                        lateStart = successorTask.getLateStart();
                     }
                  }
               }
               else
               {
                  // successor finished
                  if (relation.getLag().getDuration() == 0)
                  {
                     lateStart = successorTask.getLateStart();
                  }
                  else
                  {
                     if (relation.getLag().getDuration() > 0)
                     {
                        lateStart = successorTask.getLateStart();
                     }
                     else
                     {
                        lateStart = successorTask.getLateStart();
                     }
                  }
               }
            }
         }
      }

      if (lateFinish == null)
      {
         lateFinish = getDateFromStartAndRemainingDuration(predecessorTask, lateStart);
      }

      return lateFinish;
   }

   /**
    * Calculate the late finish for the predecessor task in a finish-finish relationship.
    *
    * @param relation relationship between two tasks
    * @return calculated late finish date
    */
   private LocalDateTime calculateLateFinishForFinishFinish(Relation relation)
   {
      Task predecessorTask = relation.getPredecessorTask();
      Task successorTask = relation.getSuccessorTask();

      if (predecessorTask.getActualStart() == null)
      {
         // Predecessor not started
         if (successorTask.getActualStart() == null)
         {
            // Successor not started
            if (relation.getLag().getDuration() == 0)
            {
               return removeLag(relation, successorTask.getLateFinish());
            }

            if (relation.getLag().getDuration() > 0)
            {
               return removeLag(relation, successorTask.getLateFinish());
            }

            return removeLag(relation, successorTask.getLateFinish());
         }

         // successor started
         if (successorTask.getActualFinish() == null)
         {
            // successor not finished
            if (relation.getLag().getDuration() == 0)
            {
               return removeLag(relation, successorTask.getLateFinish());
            }

            if (relation.getLag().getDuration() > 0)
            {
               return removeLag(relation, successorTask.getLateFinish());
            }

            return removeLag(relation, successorTask.getLateFinish());
         }

         // successor finished
         if (relation.getLag().getDuration() == 0)
         {
            return removeLag(relation, successorTask.getLateFinish());
         }

         if (relation.getLag().getDuration() > 0)
         {
            return removeLag(relation, successorTask.getLateFinish());
         }

         return removeLag(relation, successorTask.getLateFinish());
      }
      // Predecessor Started
      if (predecessorTask.getActualFinish() != null)
      {
         // Predecessor finished
         if (successorTask.getActualStart() == null)
         {
            if (relation.getLag().getDuration() == 0)
            {
               return successorTask.getLateFinish();
            }

            if (relation.getLag().getDuration() > 0)
            {
               // Successor not started
               double actualLagDurationInHours = predecessorTask.getActualFinish().isAfter(m_dataDate) ? 0 : getLagCalendar(relation).getWork(predecessorTask.getActualFinish(), m_dataDate, TimeUnit.HOURS).getDuration();
               double lagDurationInHours = relation.getLag().convertUnits(TimeUnit.HOURS, m_file.getProjectProperties()).getDuration();

               if (lagDurationInHours > actualLagDurationInHours)
               {
                  Duration remainingLag = Duration.getInstance(lagDurationInHours - actualLagDurationInHours, TimeUnit.HOURS);
                  return removeLag(relation, successorTask.getLateFinish(), remainingLag);
               }

               return successorTask.getLateFinish();
            }

            return successorTask.getLateFinish();
         }

         // successor started
         if (successorTask.getActualFinish() == null)
         {
            // successor not finished
            if (relation.getLag().getDuration() == 0)
            {
               return successorTask.getLateFinish();
            }

            if (relation.getLag().getDuration() > 0)
            {
               return successorTask.getLateFinish();
            }

            return successorTask.getLateFinish();
         }

         // successor finished
         if (relation.getLag().getDuration() == 0)
         {
            return successorTask.getLateFinish();
         }

         if (relation.getLag().getDuration() > 0)
         {
            double actualLagDurationInHours = predecessorTask.getActualFinish().isAfter(m_dataDate) ? 0 : getLagCalendar(relation).getWork(predecessorTask.getActualFinish(), m_dataDate, TimeUnit.HOURS).getDuration();
            double lagDurationInHours = relation.getLag().convertUnits(TimeUnit.HOURS, m_file.getProjectProperties()).getDuration();

            if (lagDurationInHours > actualLagDurationInHours)
            {
               Duration remainingLag = Duration.getInstance(lagDurationInHours - actualLagDurationInHours, TimeUnit.HOURS);
               return removeLag(relation, successorTask.getLateFinish(), remainingLag);
            }

            return successorTask.getLateFinish();
         }

         return successorTask.getLateFinish();
      }

      // Predecessor not finished
      if (successorTask.getActualStart() == null)
      {
         // Successor not started
         if (relation.getLag().getDuration() == 0)
         {
            return removeLag(relation, successorTask.getLateFinish());
         }

         if (relation.getLag().getDuration() > 0)
         {
            return removeLag(relation, successorTask.getLateFinish());
         }

         return removeLag(relation, successorTask.getLateFinish());
      }

      // successor started
      if (successorTask.getActualFinish() == null)
      {
         // successor not finished
         if (relation.getLag().getDuration() == 0)
         {
            return removeLag(relation, successorTask.getLateFinish());
         }

         if (relation.getLag().getDuration() > 0)
         {
            return removeLag(relation, successorTask.getLateFinish());
         }

         return removeLag(relation, successorTask.getLateFinish());
      }

      // successor finished
      if (relation.getLag().getDuration() == 0)
      {
         return removeLag(relation, successorTask.getLateFinish());
      }

      if (relation.getLag().getDuration() > 0)
      {
         return removeLag(relation, successorTask.getLateFinish());
      }

      return removeLag(relation, successorTask.getLateFinish());
   }

   /**
    * Calculate the late finish for the predecessor task in a start-finish relationship.
    *
    * @param relation relationship between two tasks
    * @return calculated late finish date
    */
   private LocalDateTime calculateLateFinishForStartFinish(Relation relation)
   {
      Task predecessorTask = relation.getPredecessorTask();
      Task successorTask = relation.getSuccessorTask();

      if (predecessorTask.getActualStart() == null)
      {
         // Predecessor not started
         if (successorTask.getActualStart() == null)
         {
            // Successor not started
            if (relation.getLag().getDuration() == 0)
            {
               return removeLag(relation, getDateFromStartAndDuration(predecessorTask, successorTask.getLateFinish()));
            }

            if (relation.getLag().getDuration() > 0)
            {
               return removeLag(relation, getDateFromStartAndDuration(predecessorTask, successorTask.getLateFinish()));
            }

            return removeLag(relation, getDateFromStartAndDuration(predecessorTask, successorTask.getLateFinish()));
         }

         // successor started
         if (successorTask.getActualFinish() == null)
         {
            // successor not finished
            if (relation.getLag().getDuration() == 0)
            {
               return removeLag(relation, getDateFromStartAndDuration(predecessorTask, successorTask.getLateFinish()));
            }

            if (relation.getLag().getDuration() > 0)
            {
               return removeLag(relation, getDateFromStartAndDuration(predecessorTask, successorTask.getLateFinish()));
            }

            return removeLag(relation, getDateFromStartAndDuration(predecessorTask, successorTask.getLateFinish()));
         }

         // successor finished
         if (relation.getLag().getDuration() == 0)
         {
            return removeLag(relation, getDateFromStartAndDuration(predecessorTask, successorTask.getLateFinish()));
         }

         if (relation.getLag().getDuration() > 0)
         {
            return removeLag(relation, getDateFromStartAndDuration(predecessorTask, successorTask.getLateFinish()));
         }

         return removeLag(relation, getDateFromStartAndDuration(predecessorTask, successorTask.getLateFinish()));
      }

      // Predecessor Started
      if (predecessorTask.getActualFinish() != null)
      {
         // Predecessor finished
         if (successorTask.getActualStart() == null)
         {
            // Successor not started
            if (relation.getLag().getDuration() == 0)
            {
               return removeLag(relation, getDateFromStartAndDuration(predecessorTask, successorTask.getLateFinish()));
            }

            if (relation.getLag().getDuration() > 0)
            {
               return removeLag(relation, getDateFromStartAndDuration(predecessorTask, successorTask.getLateFinish()));
            }

            return removeLag(relation, getDateFromStartAndDuration(predecessorTask, successorTask.getLateFinish()));
         }

         // successor started
         if (successorTask.getActualFinish() == null)
         {
            // successor not finished
            if (relation.getLag().getDuration() == 0)
            {
               return removeLag(relation, getDateFromStartAndDuration(predecessorTask, successorTask.getLateFinish()));
            }

            if (relation.getLag().getDuration() > 0)
            {
               return removeLag(relation, getDateFromStartAndDuration(predecessorTask, successorTask.getLateFinish()));
            }

            return removeLag(relation, getDateFromStartAndDuration(predecessorTask, successorTask.getLateFinish()));
         }

         // successor finished
         if (relation.getLag().getDuration() == 0)
         {
            return successorTask.getLateFinish();
         }

         if (relation.getLag().getDuration() > 0)
         {
            return successorTask.getLateFinish();
         }

         return successorTask.getLateFinish();
      }

      // Predecessor not finished
      if (successorTask.getActualStart() == null)
      {
         // Successor not started
         if (relation.getLag().getDuration() == 0)
         {
            return getDateFromStartAndRemainingDuration(predecessorTask, successorTask.getLateFinish());
         }

         if (relation.getLag().getDuration() > 0)
         {
            return getDateFromStartAndRemainingDuration(predecessorTask, successorTask.getLateFinish());
         }

         return getDateFromStartAndRemainingDuration(predecessorTask, successorTask.getLateFinish());
      }

      // successor started
      if (successorTask.getActualFinish() == null)
      {
         // successor not finished
         if (relation.getLag().getDuration() == 0)
         {
            return removeLag(relation, getDateFromStartAndDuration(predecessorTask, successorTask.getLateFinish()));
         }

         if (relation.getLag().getDuration() > 0)
         {
            return removeLag(relation, getDateFromStartAndDuration(predecessorTask, successorTask.getLateFinish()));
         }

         return removeLag(relation, getDateFromStartAndDuration(predecessorTask, successorTask.getLateFinish()));
      }

      // successor finished
      if (relation.getLag().getDuration() == 0)
      {
         return getDateFromStartAndRemainingDuration(predecessorTask, successorTask.getLateFinish());
      }

      if (relation.getLag().getDuration() > 0)
      {
         double actualLagDurationInHours = predecessorTask.getActualStart().isAfter(m_dataDate) ? 0 : getLagCalendar(relation).getWork(predecessorTask.getActualStart(), m_dataDate, TimeUnit.HOURS).getDuration();
         double lagDurationInHours = relation.getLag().convertUnits(TimeUnit.HOURS, m_file.getProjectProperties()).getDuration();

         if (lagDurationInHours > actualLagDurationInHours)
         {
            Duration remainingLag = Duration.getInstance(lagDurationInHours - actualLagDurationInHours, TimeUnit.HOURS);
            return removeLag(relation, getDateFromStartAndDuration(predecessorTask, successorTask.getLateFinish()), remainingLag);
         }

         return getDateFromStartAndRemainingDuration(predecessorTask, successorTask.getLateFinish());
      }

      return getDateFromStartAndRemainingDuration(predecessorTask, successorTask.getLateFinish());
   }

   /**
    * Calculate the late finish for the predecessor task in a finish-start relationship.
    *
    * @param relation relationship between two tasks
    * @return calculated late finish date
    */
   private LocalDateTime calculateLateFinishForFinishStart(Relation relation)
   {
      Task predecessorTask = relation.getPredecessorTask();
      Task successorTask = relation.getSuccessorTask();

      if (predecessorTask.getActualStart() == null)
      {
         // Predecessor not started
         if (successorTask.getActualStart() == null)
         {
            // Successor not started
            if (relation.getLag().getDuration() == 0)
            {
               return removeLag(relation, successorTask.getLateStart());
            }

            if (relation.getLag().getDuration() > 0)
            {
               return removeLag(relation, successorTask.getLateStart());
            }

            return removeLag(relation, successorTask.getLateStart());
         }

         // successor started
         if (successorTask.getActualFinish() == null)
         {
            // successor not finished
            if (relation.getLag().getDuration() == 0)
            {
               return removeLag(relation, successorTask.getLateStart());
            }

            if (relation.getLag().getDuration() > 0)
            {
               return removeLag(relation, successorTask.getLateStart());
            }

            return removeLag(relation, successorTask.getLateStart());
         }

         // successor finished
         if (relation.getLag().getDuration() == 0)
         {
            return successorTask.getLateStart();
         }

         if (relation.getLag().getDuration() > 0)
         {
            return successorTask.getLateStart();
         }

         return successorTask.getLateStart();
      }

      // Predecessor Started
      if (predecessorTask.getActualFinish() != null)
      {
         // Predecessor finished
         if (successorTask.getActualStart() == null)
         {
            // Successor not started
            if (relation.getLag().getDuration() == 0)
            {
               return successorTask.getLateStart();
            }

            if (relation.getLag().getDuration() > 0.0)
            {
               double actualLagDurationInHours = predecessorTask.getActualFinish().isAfter(m_dataDate) ? 0 : getLagCalendar(relation).getWork(predecessorTask.getActualFinish(), m_dataDate, TimeUnit.HOURS).getDuration();
               double lagDurationInHours = relation.getLag().convertUnits(TimeUnit.HOURS, m_file.getProjectProperties()).getDuration();

               if (lagDurationInHours > actualLagDurationInHours)
               {
                  Duration remainingLag = Duration.getInstance(lagDurationInHours - actualLagDurationInHours, TimeUnit.HOURS);
                  return removeLag(relation, successorTask.getLateStart(), remainingLag);
               }

               return successorTask.getLateStart();
            }

            return successorTask.getLateStart();
         }

         // successor started
         if (successorTask.getActualFinish() == null)
         {
            // check for actual progress
            if (relation.getLag().getDuration() == 0)
            {
               return successorTask.getLateStart();
            }

            if (relation.getLag().getDuration() > 0)
            {
               if (successorTask.getActualDuration().getDuration() == 0.0)
               {
                  return removeLag(relation, successorTask.getLateStart());
               }

               return successorTask.getLateStart();
            }

            if (successorTask.getActualDuration().getDuration() == 0.0)
            {
               return removeLag(relation, successorTask.getLateStart());
            }

            return successorTask.getLateStart();
         }

         // successor finished
         if (relation.getLag().getDuration() == 0)
         {
            return successorTask.getLateStart();
         }

         if (relation.getLag().getDuration() > 0)
         {
            double actualLagDurationInHours = predecessorTask.getActualFinish().isAfter(m_dataDate) ? 0 : getLagCalendar(relation).getWork(predecessorTask.getActualFinish(), m_dataDate, TimeUnit.HOURS).getDuration();
            double lagDurationInHours = relation.getLag().convertUnits(TimeUnit.HOURS, m_file.getProjectProperties()).getDuration();

            if (lagDurationInHours > actualLagDurationInHours)
            {
               Duration remainingLag = Duration.getInstance(lagDurationInHours - actualLagDurationInHours, TimeUnit.HOURS);
               return removeLag(relation, successorTask.getLateStart(), remainingLag);
            }

            return successorTask.getLateStart();
         }

         return successorTask.getLateStart();
      }

      // Predecessor not finished
      if (successorTask.getActualStart() == null)
      {
         // Successor not started
         if (relation.getLag().getDuration() == 0)
         {
            return removeLag(relation, successorTask.getLateStart());
         }

         if (relation.getLag().getDuration() > 0)
         {
            return removeLag(relation, successorTask.getLateStart());
         }

         return removeLag(relation, successorTask.getLateStart());
      }
      // successor started
      if (successorTask.getActualFinish() == null)
      {
         // check for actual progress
         if (relation.getLag().getDuration() == 0)
         {
            if (successorTask.getActualDuration().getDuration() == 0.0)
            {
               return removeLag(relation, successorTask.getLateStart());
            }

            return successorTask.getLateStart();
         }

         if (relation.getLag().getDuration() > 0)
         {
            if (successorTask.getActualDuration().getDuration() == 0.0)
            {
               return removeLag(relation, successorTask.getLateStart());
            }

            return successorTask.getLateStart();
         }

         if (successorTask.getActualDuration().getDuration() == 0.0)
         {
            return removeLag(relation, successorTask.getLateStart());
         }

         return successorTask.getLateStart();
      }

      // successor finished
      if (relation.getLag().getDuration() == 0)
      {
         return successorTask.getLateStart();
      }

      if (relation.getLag().getDuration() > 0)
      {
         return successorTask.getLateStart();
      }

      return successorTask.getLateStart();
   }

   /**
    * Retrieve the lag calendar to use when adding/removing lag.
    *
    * @param relation parent relation
    * @return lag calendar
    */
   private ProjectCalendar getLagCalendar(Relation relation)
   {
      switch (m_file.getProjectProperties().getRelationshipLagCalendar())
      {
         case PREDECESSOR:
         {
            return relation.getPredecessorTask().getEffectiveCalendar();
         }

         case SUCCESSOR:
         {
            return relation.getSuccessorTask().getEffectiveCalendar();
         }

         case PROJECT_DEFAULT:
         {
            return m_file.getProjectProperties().getDefaultCalendar();
         }

         case TWENTY_FOUR_HOUR:
         default:
         {
            return m_twentyFourHourCalendar;
         }
      }
   }

   /**
    * Using the supplied calendar, add a duration to the supplied date.
    *
    * @param calendar parent calendar
    * @param date date
    * @param duration duration
    * @return date plus duration
    */
   private LocalDateTime getDate(ProjectCalendar calendar, LocalDateTime date, Duration duration)
   {
      LocalDateTime result = calendar.getDate(date, duration);

      // P6 appears to work to the nearest minute
      if (result.getSecond() != 0)
      {
         boolean negativeDuration = duration.getDuration() < 0;
         boolean roundUp = (negativeDuration && result.getSecond() > 30) || (!negativeDuration && result.getSecond() >= 30);
         LocalTime newTime = LocalTime.of(result.getHour(), result.getMinute());
         result = LocalDateTime.of(result.toLocalDate(), newTime);
         if (roundUp)
         {
            result = result.plusMinutes(1);
         }
      }
      return result;
   }

   /**
    * Determine if the task's effective calendar should be used when scheduling.
    *
    * @param task task
    * @return true if effective calendar should be used
    */
   private boolean useTaskEffectiveCalendar(Task task)
   {
      return task.getActivityType() != ActivityType.RESOURCE_DEPENDENT || task.getResourceAssignments().stream().noneMatch(r -> r.getResource().getType() == ResourceType.WORK);
   }

   /**
    * Calculate a date from a start date plus the task duration.
    *
    * @param task parent task
    * @param date start date
    * @return start date plus task duration
    */
   private LocalDateTime getDateFromStartAndDuration(Task task, LocalDateTime date)
   {
      if (useTaskEffectiveCalendar(task))
      {
         return getDate(task.getEffectiveCalendar(), date, task.getDuration());
      }

      return getDateFromStartAndWork(task, date);
   }

   /**
    * Find latest date by adding resource assignment work to a date.
    *
    * @param task parent task
    * @param date date
    * @return date plus work
    */
   private LocalDateTime getDateFromStartAndWork(Task task, LocalDateTime date)
   {
      return getResourceAssignmentStream(task).map(r -> getDateFromWork(r, date, r.getWork())).max(Comparator.naturalOrder()).orElseGet(null);
   }

   /**
    * Calculate a date from a finish date minus the task duration.
    *
    * @param task parent task
    * @param date finish date
    * @return finish date minus task duration
    */
   private LocalDateTime getDateFromFinishAndDuration(Task task, LocalDateTime date)
   {
      if (useTaskEffectiveCalendar(task))
      {
         return getDate(task.getEffectiveCalendar(), date, task.getDuration().negate());
      }

      return getDateFromFinishAndWork(task, date);
   }

   /**
    * Find the earliest date by subtracting resource assignment work from a date.
    *
    * @param task parent task
    * @param date date
    * @return date less work
    */
   private LocalDateTime getDateFromFinishAndWork(Task task, LocalDateTime date)
   {
      return getResourceAssignmentStream(task).map(r -> getDateFromWork(r, date, r.getWork().negate())).min(Comparator.naturalOrder()).orElseGet(null);
   }

   /**
    * Calculate a date from a start date plus the task remaining duration.
    *
    * @param task parent task
    * @param date start date
    * @return start date plus task remaining duration
    */
   private LocalDateTime getDateFromStartAndRemainingDuration(Task task, LocalDateTime date)
   {
      if (useTaskEffectiveCalendar(task))
      {
         return getDate(task.getEffectiveCalendar(), date, task.getRemainingDuration());
      }

      return getDateFromStartAndRemainingWork(task, date);
   }

   /**
    * Find latest date by adding resource assignment work to a date.
    *
    * @param task parent task
    * @param date date
    * @return date plus work
    */
   private LocalDateTime getDateFromStartAndRemainingWork(Task task, LocalDateTime date)
   {
      return getResourceAssignmentStream(task).map(r -> getDateFromWork(r, date, r.getRemainingWork())).max(Comparator.naturalOrder()).orElseGet(null);
   }

   /**
    * Calculate a date from a finish date minus the task remaining duration.
    *
    * @param task parent task
    * @param date finish date
    * @return finish date minus task remaining duration
    */
   private LocalDateTime getDateFromFinishAndRemainingDuration(Task task, LocalDateTime date)
   {
      if (useTaskEffectiveCalendar(task))
      {
         return getDate(task.getEffectiveCalendar(), date, task.getRemainingDuration().negate());
      }

      return getDateFromFinishAndRemainingWork(task, date);
   }

   /**
    * Find the earliest date by subtracting resource assignment work from a date.
    *
    * @param task parent task
    * @param date date
    * @return date less work
    */
   private LocalDateTime getDateFromFinishAndRemainingWork(Task task, LocalDateTime date)
   {
      return getResourceAssignmentStream(task).map(r -> getDateFromWork(r, date, r.getRemainingWork().negate())).min(Comparator.naturalOrder()).orElseGet(null);
   }

   /**
    * Add relation lag to a date.
    *
    * @param relation parent relation
    * @param date date
    * @return date plus lag
    */
   private LocalDateTime addLag(Relation relation, LocalDateTime date)
   {
      return addLag(relation, date, relation.getLag());
   }

   /**
    * Add lag to a date.
    *
    * @param relation parent relation
    * @param date date
    * @param lag lag
    * @return date plus lag
    */
   private LocalDateTime addLag(Relation relation, LocalDateTime date, Duration lag)
   {
      if (date == null)
      {
         return null;
      }

      LocalDateTime result = getDate(getLagCalendar(relation), date, lag);
      if (lag.getDuration() < 0 && result.isBefore(m_dataDate))
      {
         result = m_dataDate;
      }

      return result;
   }

   /**
    * Remove lag from a date.
    *
    * @param relation parent relation
    * @param date date
    * @return date minus relation lag
    */
   private LocalDateTime removeLag(Relation relation, LocalDateTime date)
   {
      return removeLag(relation, date, relation.getLag());
   }

   /**
    * Remove lag from a date.
    *
    * @param relation parent relation
    * @param date date
    * @param lag lag
    * @return date minus lag
    */
   private LocalDateTime removeLag(Relation relation, LocalDateTime date, Duration lag)
   {
      if (date == null)
      {
         return null;
      }

      return getDate(getLagCalendar(relation), date, lag.negate());
   }

   /**
    * Determine if the given MPXJ Task is a P6 Activity.
    *
    * @param task Task instance
    * @return true if this is a P6 Activity
    */
   static boolean isActivity(Task task)
   {
      return !(task.getSummary() || task.getActivityType() == ActivityType.LEVEL_OF_EFFORT || task.getActivityType() == ActivityType.WBS_SUMMARY);
   }

   /**
    * Determine if the given MPXJ Task is a P6 Level of Effort Activity.
    *
    * @param task Task instance
    * @return true if this is a P6 Level of Effort Activity
    */
   static boolean isLevelOfEffortActivity(Task task)
   {
      return task.getActivityType() == ActivityType.LEVEL_OF_EFFORT;
   }

   /**
    * Determine if the given MPXJ Task is a P6 WBS Summary Activity.
    *
    * @param task Task instance
    * @return true if this is a P6 WBS Summary Activity
    */
   static boolean isWbsSummary(Task task)
   {
      return task.getActivityType() == ActivityType.WBS_SUMMARY;
   }

   /**
    * Adjust Early Start and Early Finish dates for an activity with
    * an "As Late As Possible" constraint.
    *
    * @param task ALAP constrained task
    */
   private void alapAdjust(Task task) throws CpmException
   {
      LocalDateTime earlyStart;
      LocalDateTime earlyFinish;

      List<Relation> successors = task.getSuccessors().stream().filter(r -> isActivity(r.getSuccessorTask())).collect(Collectors.toList());
      if (successors.isEmpty())
      {
         earlyFinish = m_projectFinishDate;
         earlyStart = getDateFromFinishAndRemainingDuration(task, earlyFinish);
      }
      else
      {
         Relation relation = successors.stream().min(Comparator.comparing(this::getAlapEarlyStart)).orElseThrow(() -> new CpmException("Missing early start date"));
         earlyStart = getAlapEarlyStart(relation);
         earlyFinish = getDateFromStartAndRemainingDuration(task, earlyStart);
      }

      task.setEarlyStart(earlyStart);
      task.setEarlyFinish(earlyFinish);
      setRemainingEarlyDates(task);
   }

   /**
    * Calculate the ALAP Early Start from a relation's successor task.
    *
    * @param relation target relation
    * @return early start date
    */
   private LocalDateTime getAlapEarlyStart(Relation relation)
   {
      Task predecessorTask = relation.getPredecessorTask();
      Task successorTask = relation.getSuccessorTask();

      switch (relation.getType())
      {
         case START_START:
         {
            return removeLag(relation, successorTask.getEarlyStart());
         }

         case FINISH_START:
         {
            if (predecessorTask.getActualStart() == null)
            {
               // Predecessor not started
               if (successorTask.getActualStart() == null)
               {
                  // Successor not started
                  return removeLag(relation, getDateFromFinishAndRemainingDuration(predecessorTask, successorTask.getEarlyStart()));
               }

               // successor started
               if (successorTask.getActualFinish() == null)
               {
                  // successor not finished
                  return removeLag(relation, getDateFromFinishAndRemainingDuration(predecessorTask, successorTask.getEarlyStart()));
               }

               // successor finished
               return removeLag(relation, getDateFromFinishAndRemainingDuration(predecessorTask, successorTask.getEarlyStart()));
            }

            // Predecessor Started
            if (predecessorTask.getActualFinish() == null)
            {
               // Predecessor not finished
               if (successorTask.getActualStart() == null)
               {
                  // Successor not started
                  return removeLag(relation, getDateFromFinishAndRemainingDuration(predecessorTask, successorTask.getEarlyStart()));
               }

               // successor started
               if (successorTask.getActualFinish() == null)
               {
                  // successor not finished
                  return removeLag(relation, getDateFromFinishAndRemainingDuration(predecessorTask, successorTask.getEarlyStart()));
               }

               // successor finished
               return removeLag(relation, getDateFromFinishAndRemainingDuration(predecessorTask, successorTask.getEarlyStart()));
            }

            // Predecessor finished
            if (successorTask.getActualStart() == null)
            {
               // Successor not started
               return getEquivalentNextWorkStart(predecessorTask, m_dataDate);
            }

            // successor started
            if (successorTask.getActualFinish() == null)
            {
               // successor not finished
               return getEquivalentNextWorkStart(predecessorTask, m_dataDate);
            }

            // successor finished
            return m_dataDate;
         }

         case FINISH_FINISH:
         {
            LocalDateTime earlyFinish = removeLag(relation, successorTask.getEarlyFinish());
            return getDateFromFinishAndRemainingDuration(predecessorTask, earlyFinish);
         }

         default:
         {
            // TODO: need example to determine correct behaviour here
            return LocalDateTime.MAX;
         }
      }
   }

   /**
    * Populate the remaining early dates.
    *
    * @param task target task
    */
   private void setRemainingEarlyDates(Task task)
   {
      if (task.getActualFinish() != null)
      {
         return;
      }

      LocalDateTime remainingEarlyStart;

      if (task.getActualStart() == null)
      {
         remainingEarlyStart = task.getEarlyStart();
      }
      else
      {
         if (task.getActualDuration() != null && task.getActualDuration().getDuration() != 0)
         {
            remainingEarlyStart = task.getEarlyStart();
         }
         else
         {
            if ((task.getActualDuration() == null || task.getActualDuration().getDuration() == 0) || task.getActualStart().isAfter(m_dataDate))
            {
               remainingEarlyStart = task.getEarlyStart();
            }
            else
            {
               remainingEarlyStart = getNextWorkStart(task, m_dataDate);
            }
         }
      }

      task.setRemainingEarlyStart(remainingEarlyStart);
      task.setRemainingEarlyFinish(getDateFromStartAndRemainingDuration(task, remainingEarlyStart));
   }

   /**
    * Populate the remaining late dates.
    *
    * @param task target task
    */
   private void setRemainingLateDates(Task task)
   {
      if (task.getActualFinish() != null)
      {
         return;
      }

      task.setRemainingLateStart(task.getLateStart());
      task.setRemainingLateFinish(task.getLateFinish());
   }

   /**
    * Create a temporary 24-hour calendar for this project.
    *
    * @return 24-hour calendar
    */
   private ProjectCalendar createTwentyFourHourCalendar()
   {
      ProjectCalendar calendar = new ProjectCalendar(m_file);
      for (DayOfWeek day : DayOfWeek.values())
      {
         calendar.setCalendarDayType(day, DayType.WORKING);
         ProjectCalendarHours hours = calendar.addCalendarHours(day);
         hours.add(new LocalTimeRange(LocalTime.MIDNIGHT, LocalTime.MIDNIGHT));
      }
      return calendar;
   }

   /**
    * Clear dates ready to be repopulated.
    */
   private void clearDates()
   {
      for (Task task : m_file.getTasks())
      {
         task.setStart(null);
         task.setFinish(null);
         task.setEarlyStart(null);
         task.setEarlyFinish(null);
         task.setLateStart(null);
         task.setLateFinish(null);
         task.setRemainingEarlyStart(null);
         task.setRemainingEarlyFinish(null);
         task.setRemainingLateStart(null);
         task.setRemainingLateFinish(null);

         // Clear the critical flag to force it to be recalculated
         task.set(TaskField.CRITICAL, null);
      }
   }

   /**
    * Roll up dates to a WBS entity.
    *
    * @param parentTask parent WBS
    */
   private void rollupDates(Task parentTask)
   {
      // NOTE: LOE and WBS Summary are ignore at this point as they have null dates
      if (!parentTask.hasChildTasks())
      {
         return;
      }

      int finished = 0;
      LocalDateTime startDate = parentTask.getStart();
      LocalDateTime finishDate = parentTask.getFinish();
      LocalDateTime plannedStartDate = parentTask.getPlannedStart();
      LocalDateTime plannedFinishDate = parentTask.getPlannedFinish();
      LocalDateTime actualStartDate = parentTask.getActualStart();
      LocalDateTime actualFinishDate = parentTask.getActualFinish();
      LocalDateTime earlyStartDate = parentTask.getEarlyStart();
      LocalDateTime earlyFinishDate = parentTask.getEarlyFinish();
      LocalDateTime lateStartDate = parentTask.getLateStart();
      LocalDateTime lateFinishDate = parentTask.getLateFinish();
      LocalDateTime baselineStartDate = parentTask.getBaselineStart();
      LocalDateTime baselineFinishDate = parentTask.getBaselineFinish();
      LocalDateTime remainingEarlyStartDate = parentTask.getRemainingEarlyStart();
      LocalDateTime remainingEarlyFinishDate = parentTask.getRemainingEarlyFinish();
      LocalDateTime remainingLateStartDate = parentTask.getRemainingLateStart();
      LocalDateTime remainingLateFinishDate = parentTask.getRemainingLateFinish();
      boolean critical = false;

      for (Task task : parentTask.getChildTasks())
      {
         rollupDates(task);

         // the child tasks can have null dates (e.g. for nested wbs elements with no task children) so we
         // still must protect against some children having null dates

         startDate = LocalDateTimeHelper.min(startDate, task.getStart());
         finishDate = LocalDateTimeHelper.max(finishDate, task.getFinish());
         plannedStartDate = LocalDateTimeHelper.min(plannedStartDate, task.getPlannedStart());
         plannedFinishDate = LocalDateTimeHelper.max(plannedFinishDate, task.getPlannedFinish());
         actualStartDate = LocalDateTimeHelper.min(actualStartDate, task.getActualStart());
         actualFinishDate = LocalDateTimeHelper.max(actualFinishDate, task.getActualFinish());
         baselineStartDate = LocalDateTimeHelper.min(baselineStartDate, task.getBaselineStart());
         baselineFinishDate = LocalDateTimeHelper.max(baselineFinishDate, task.getBaselineFinish());

         if (task.getActivityType() != ActivityType.LEVEL_OF_EFFORT || task.getActualFinish() == null)
         {
            earlyStartDate = LocalDateTimeHelper.min(earlyStartDate, task.getEarlyStart());
            earlyFinishDate = LocalDateTimeHelper.max(earlyFinishDate, task.getEarlyFinish());
            remainingEarlyStartDate = LocalDateTimeHelper.min(remainingEarlyStartDate, task.getRemainingEarlyStart());
            remainingEarlyFinishDate = LocalDateTimeHelper.max(remainingEarlyFinishDate, task.getRemainingEarlyFinish());
            lateStartDate = LocalDateTimeHelper.min(lateStartDate, task.getLateStart());
            lateFinishDate = LocalDateTimeHelper.max(lateFinishDate, task.getLateFinish());
            remainingLateStartDate = LocalDateTimeHelper.min(remainingLateStartDate, task.getRemainingLateStart());
            remainingLateFinishDate = LocalDateTimeHelper.max(remainingLateFinishDate, task.getRemainingLateFinish());
         }

         if (task.getActualFinish() != null)
         {
            ++finished;
         }

         critical = critical || task.getCritical();
      }

      parentTask.setStart(startDate);
      parentTask.setFinish(finishDate);
      parentTask.setPlannedStart(plannedStartDate);
      parentTask.setPlannedFinish(plannedFinishDate);
      parentTask.setActualStart(actualStartDate);
      parentTask.setEarlyStart(earlyStartDate);
      parentTask.setEarlyFinish(earlyFinishDate);
      parentTask.setRemainingEarlyStart(remainingEarlyStartDate);
      parentTask.setRemainingEarlyFinish(remainingEarlyFinishDate);
      parentTask.setLateStart(lateStartDate);
      parentTask.setLateFinish(lateFinishDate);
      parentTask.setRemainingLateStart(remainingLateStartDate);
      parentTask.setRemainingLateFinish(remainingLateFinishDate);
      parentTask.setBaselineStart(baselineStartDate);
      parentTask.setBaselineFinish(baselineFinishDate);

      //
      // Only if all child tasks have actual finish dates do we
      // set the actual finish date on the parent task.
      //
      if (finished == parentTask.getChildTasks().size())
      {
         parentTask.setActualFinish(actualFinishDate);
      }

      Duration plannedDuration = null;
      if (plannedStartDate != null && plannedFinishDate != null)
      {
         plannedDuration = parentTask.getEffectiveCalendar().getWork(plannedStartDate, plannedFinishDate, TimeUnit.HOURS);
         parentTask.setPlannedDuration(plannedDuration);
      }

      Duration actualDuration = null;
      Duration remainingDuration = null;
      if (parentTask.getActualFinish() == null)
      {
         LocalDateTime taskStartDate = parentTask.getRemainingEarlyStart();
         if (taskStartDate == null)
         {
            taskStartDate = parentTask.getEarlyStart();
            if (taskStartDate == null)
            {
               taskStartDate = plannedStartDate;
            }
         }

         LocalDateTime taskFinishDate = parentTask.getRemainingEarlyFinish();
         if (taskFinishDate == null)
         {
            taskFinishDate = parentTask.getEarlyFinish();
            if (taskFinishDate == null)
            {
               taskFinishDate = plannedFinishDate;
            }
         }

         if (taskStartDate != null)
         {
            if (parentTask.getActualStart() != null)
            {
               actualDuration = parentTask.getEffectiveCalendar().getWork(parentTask.getActualStart(), taskStartDate, TimeUnit.HOURS);
            }

            if (taskFinishDate != null)
            {
               remainingDuration = parentTask.getEffectiveCalendar().getWork(taskStartDate, taskFinishDate, TimeUnit.HOURS);
            }
         }
      }
      else
      {
         actualDuration = parentTask.getEffectiveCalendar().getWork(parentTask.getActualStart(), parentTask.getActualFinish(), TimeUnit.HOURS);
         remainingDuration = Duration.getInstance(0, TimeUnit.HOURS);
      }

      if (actualDuration != null && actualDuration.getDuration() < 0)
      {
         actualDuration = null;
      }

      if (remainingDuration != null && remainingDuration.getDuration() < 0)
      {
         remainingDuration = null;
      }

      parentTask.setActualDuration(actualDuration);
      parentTask.setRemainingDuration(remainingDuration);
      parentTask.setDuration(Duration.add(actualDuration, remainingDuration, parentTask.getEffectiveCalendar()));

      if (plannedDuration != null && remainingDuration != null && plannedDuration.getDuration() != 0)
      {
         double durationPercentComplete = ((plannedDuration.getDuration() - remainingDuration.getDuration()) / plannedDuration.getDuration()) * 100.0;
         if (durationPercentComplete < 0)
         {
            durationPercentComplete = 0;
         }
         else
         {
            if (durationPercentComplete > 100)
            {
               durationPercentComplete = 100;
            }
         }
         parentTask.setPercentageComplete(Double.valueOf(durationPercentComplete));
         parentTask.setPercentCompleteType(PercentCompleteType.DURATION);
      }

      // Force total slack calculation to avoid overwriting the critical flag
      parentTask.getTotalSlack();
      parentTask.setCritical(critical);
   }

   private void levelOfEffortPass() throws CpmException
   {
      List<Task> activities = new DepthFirstGraphSort(m_file, PrimaveraScheduler::isLevelOfEffortActivity).sort();
      if (activities.isEmpty())
      {
         return;
      }

      for (Task activity : activities)
      {
         levelOfEffortPass(activity);
      }
   }

   /**
    * Schedule a Level of Effort activity.
    *
    * @param task target activity
    */
   private void levelOfEffortPass(Task task)
   {
      // For LOE these are generated values, so we need to clear them
      task.setActualStart(null);
      task.setActualFinish(null);

      AnnotatedDateTime earlyStartFromPredecessor = null;
      AnnotatedDateTime earlyFinishFromPredecessor = null;
      AnnotatedDateTime lateStartFromPredecessor = null;
      AnnotatedDateTime lateFinishFromPredecessor = null;

      for (Relation relation : task.getPredecessors())
      {
         Task predecessor = relation.getPredecessorTask();

         switch (relation.getType())
         {
            case START_START:
            {
               if (predecessor.getActualStart() == null)
               {
                  earlyStartFromPredecessor = updateIfBefore(earlyStartFromPredecessor, AnnotatedDateTime.from(addLag(relation, predecessor.getEarlyStart())));
                  lateStartFromPredecessor = updateIfBefore(lateStartFromPredecessor, AnnotatedDateTime.from(addLag(relation, predecessor.getLateStart())));
               }
               else
               {
                  earlyStartFromPredecessor = updateIfBefore(earlyStartFromPredecessor, AnnotatedDateTime.fromActual(addLag(relation, predecessor.getActualStart())));
                  lateStartFromPredecessor = updateIfBefore(lateStartFromPredecessor, AnnotatedDateTime.from(addLag(relation, predecessor.getLateStart())));
               }
               break;
            }

            case FINISH_START:
            {
               if (predecessor.getActualFinish() == null)
               {
                  earlyStartFromPredecessor = updateIfBefore(earlyStartFromPredecessor, AnnotatedDateTime.from(addLag(relation, predecessor.getEarlyFinish())));
                  lateStartFromPredecessor = updateIfBefore(lateStartFromPredecessor, AnnotatedDateTime.from(addLag(relation, predecessor.getLateFinish())));
               }
               else
               {
                  earlyStartFromPredecessor = updateIfBefore(earlyStartFromPredecessor, AnnotatedDateTime.fromActual(addLag(relation, predecessor.getActualFinish())));
                  lateStartFromPredecessor = updateIfBefore(lateStartFromPredecessor, AnnotatedDateTime.fromActual(addLag(relation, predecessor.getActualFinish())));
               }
               break;
            }

            case START_FINISH:
            {
               if (predecessor.getActualStart() == null)
               {
                  earlyFinishFromPredecessor = updateIfAfter(earlyFinishFromPredecessor, AnnotatedDateTime.from(adjustFinish(task, addLag(relation, predecessor.getEarlyStart()))));
                  lateFinishFromPredecessor = updateIfAfter(lateFinishFromPredecessor, AnnotatedDateTime.from(adjustFinish(task, addLag(relation, predecessor.getLateStart()))));
               }
               else
               {
                  earlyFinishFromPredecessor = updateIfAfter(earlyFinishFromPredecessor, AnnotatedDateTime.fromActual(addLag(relation, predecessor.getActualStart())));
                  lateFinishFromPredecessor = updateIfAfter(lateFinishFromPredecessor, AnnotatedDateTime.fromActual(addLag(relation, predecessor.getActualStart())));
               }
               break;
            }

            case FINISH_FINISH:
            {
               if (predecessor.getActualFinish() == null)
               {
                  earlyFinishFromPredecessor = updateIfAfter(earlyFinishFromPredecessor, AnnotatedDateTime.from(adjustFinish(task, addLag(relation, predecessor.getEarlyFinish()))));
                  lateFinishFromPredecessor = updateIfAfter(lateFinishFromPredecessor, AnnotatedDateTime.from(adjustFinish(task, addLag(relation, predecessor.getLateFinish()))));
               }
               else
               {
                  earlyFinishFromPredecessor = updateIfAfter(earlyFinishFromPredecessor, AnnotatedDateTime.fromActual(addLag(relation, predecessor.getActualFinish())));
                  lateFinishFromPredecessor = updateIfAfter(lateFinishFromPredecessor, AnnotatedDateTime.fromActual(addLag(relation, predecessor.getActualFinish())));
               }
               break;
            }
         }
      }

      AnnotatedDateTime earlyStartFromSuccessor = null;
      AnnotatedDateTime earlyFinishFromSuccessor = null;
      AnnotatedDateTime lateStartFromSuccessor = null;
      AnnotatedDateTime lateFinishFromSuccessor = null;

      for (Relation relation : task.getSuccessors())
      {
         Task successor = relation.getSuccessorTask();

         switch (relation.getType())
         {
            case START_START:
            {
               if (successor.getActualStart() == null)
               {
                  earlyStartFromSuccessor = updateIfBefore(earlyStartFromSuccessor, AnnotatedDateTime.from(removeLag(relation, successor.getEarlyStart())));
                  lateStartFromSuccessor = updateIfBefore(lateStartFromSuccessor, AnnotatedDateTime.from(removeLag(relation, successor.getLateStart())));
               }
               else
               {
                  earlyStartFromSuccessor = updateIfBefore(earlyStartFromSuccessor, AnnotatedDateTime.fromActual(removeLag(relation, successor.getActualStart())));
                  lateStartFromSuccessor = updateIfBefore(lateStartFromSuccessor, AnnotatedDateTime.fromActual(removeLag(relation, successor.getActualStart())));
               }
               break;
            }

            case FINISH_START:
            {
               if (successor.getActualStart() == null)
               {
                  earlyFinishFromSuccessor = updateIfAfter(earlyFinishFromSuccessor, AnnotatedDateTime.from(adjustFinish(task, removeLag(relation, successor.getEarlyStart()))));
                  lateFinishFromSuccessor = updateIfAfter(lateFinishFromSuccessor, AnnotatedDateTime.from(adjustFinish(task, removeLag(relation, successor.getLateStart()))));
               }
               else
               {
                  earlyFinishFromSuccessor = updateIfAfter(earlyFinishFromSuccessor, AnnotatedDateTime.fromActual(removeLag(relation, successor.getActualStart())));
                  lateFinishFromSuccessor = updateIfAfter(lateFinishFromSuccessor, AnnotatedDateTime.fromActual(removeLag(relation, successor.getActualStart())));
               }
               break;
            }

            case START_FINISH:
            {
               if (successor.getActualFinish() == null)
               {
                  earlyStartFromSuccessor = updateIfBefore(earlyStartFromSuccessor, AnnotatedDateTime.from(removeLag(relation, successor.getEarlyFinish())));
                  lateStartFromSuccessor = updateIfBefore(lateStartFromSuccessor, AnnotatedDateTime.from(removeLag(relation, successor.getLateFinish())));
               }
               else
               {
                  earlyStartFromSuccessor = updateIfBefore(earlyStartFromSuccessor, AnnotatedDateTime.fromActual(removeLag(relation, successor.getActualFinish())));
                  lateStartFromSuccessor = updateIfBefore(lateStartFromSuccessor, AnnotatedDateTime.fromActual(removeLag(relation, successor.getActualFinish())));
               }
               break;
            }

            case FINISH_FINISH:
            {
               if (successor.getActualFinish() == null)
               {
                  earlyFinishFromSuccessor = updateIfAfter(earlyFinishFromSuccessor, AnnotatedDateTime.from(adjustFinish(task, removeLag(relation, successor.getEarlyFinish()))));
                  lateFinishFromSuccessor = updateIfAfter(lateFinishFromSuccessor, AnnotatedDateTime.from(adjustFinish(task, removeLag(relation, successor.getLateFinish()))));
               }
               else
               {
                  earlyFinishFromSuccessor = updateIfAfter(earlyFinishFromSuccessor, AnnotatedDateTime.fromActual(removeLag(relation, successor.getActualFinish())));
                  lateFinishFromSuccessor = updateIfAfter(lateFinishFromSuccessor, AnnotatedDateTime.fromActual(removeLag(relation, successor.getActualFinish())));
               }
               break;
            }
         }
      }

      AnnotatedDateTime earlyStart;
      if (earlyStartFromPredecessor == null && earlyStartFromSuccessor == null)
      {
         earlyStart = AnnotatedDateTime.from(getNextWorkStart(task, m_dataDate));
      }
      else
      {
         if (earlyStartFromPredecessor != null && earlyStartFromSuccessor != null)
         {
            earlyStart = earlyStartFromPredecessor.isBefore(earlyStartFromSuccessor) ? earlyStartFromPredecessor : earlyStartFromSuccessor;
         }
         else
         {
            earlyStart = earlyStartFromPredecessor == null ? earlyStartFromSuccessor : earlyStartFromPredecessor;
         }
      }

      AnnotatedDateTime earlyFinish;
      if (earlyFinishFromPredecessor == null && earlyFinishFromSuccessor == null)
      {
         earlyFinish = earlyStart;
      }
      else
      {
         if (earlyFinishFromPredecessor != null && earlyFinishFromSuccessor != null)
         {
            // Not correct: how do we determine which date to use?
            earlyFinish = earlyFinishFromSuccessor.isAfter(earlyFinishFromPredecessor) ? earlyFinishFromSuccessor : earlyFinishFromPredecessor;
         }
         else
         {
            earlyFinish = earlyFinishFromPredecessor == null ? earlyFinishFromSuccessor : earlyFinishFromPredecessor;
         }
      }

      AnnotatedDateTime lateFinish;
      if (lateFinishFromPredecessor == null && lateFinishFromSuccessor == null)
      {
         lateFinish = AnnotatedDateTime.from(m_projectFinishDate);
      }
      else
      {
         if (lateFinishFromPredecessor != null && lateFinishFromSuccessor != null)
         {
            lateFinish = lateFinishFromSuccessor.isAfter(lateFinishFromPredecessor) ? lateFinishFromSuccessor : lateFinishFromPredecessor;
         }
         else
         {
            lateFinish = lateFinishFromPredecessor == null ? lateFinishFromSuccessor : lateFinishFromPredecessor;
         }
      }

      AnnotatedDateTime lateStart;
      if (lateStartFromPredecessor == null && lateStartFromSuccessor == null)
      {
         lateStart = lateFinish;
      }
      else
      {
         if (lateStartFromPredecessor != null && lateStartFromSuccessor != null)
         {
            lateStart = lateStartFromPredecessor.isBefore(lateStartFromSuccessor) ? lateStartFromPredecessor : lateStartFromSuccessor;
         }
         else
         {
            lateStart = lateStartFromPredecessor == null ? lateStartFromSuccessor : lateStartFromPredecessor;
         }
      }

      if (earlyStart == null || earlyFinish == null || lateStart == null || lateFinish == null)
      {
         return;
      }

      AnnotatedDateTime start = earlyStart;

      if (earlyStart.isBefore(m_dataDate))
      {
         if (earlyStart.isActual())
         {
            earlyStart = AnnotatedDateTime.fromActual(m_dataDate);
         }
         else
         {
            // very dubious logic here
            earlyStart = AnnotatedDateTime.from(getNextWorkStart(task, m_dataDate));
            start = AnnotatedDateTime.fromActual(start.getValue());
            task.setActualStart(start.getValue());
         }
      }

      if (!earlyFinish.isActual())
      {
         if (earlyFinish.isBefore(m_dataDate))
         {
            earlyFinish = AnnotatedDateTime.from(m_dataDate);
         }

         if (earlyFinish.isBefore(earlyStart))
         {
            if (earlyFinish.isActual())
            {
               earlyFinish = AnnotatedDateTime.fromActual(earlyStart.getValue());
            }
            else
            {
               earlyFinish = earlyStart;
            }
         }
      }

      if (lateStart.isAfter(lateFinish))
      {
         lateStart = lateFinish;
      }

      task.setStart(start.isActual() ? start.getValue() : earlyStart.getValue());
      task.setFinish(earlyFinish.getValue());

      if (earlyStart.isActual())
      {
         if (task.getCalendar().getWork(m_dataDate, task.getStart(), TimeUnit.HOURS).getDuration() <= 0)
         {
            task.setActualStart(task.getStart());
         }
      }

      if (earlyFinish.isActual() || lateFinish.isActual())
      {
         if (task.getCalendar().getWork(m_dataDate, task.getFinish(), TimeUnit.HOURS).getDuration() <= 0)
         {
            task.setActualStart(task.getStart());
            task.setActualFinish(task.getFinish());
         }
      }

      task.setEarlyStart(task.getCalendar().getNextWorkStart(earlyStart.getValue()));
      task.setEarlyFinish(earlyFinish.getValue());
      task.setLateStart(task.getCalendar().getNextWorkStart(lateStart.getValue()));
      task.setLateFinish(lateFinish.getValue());

      // P6 moves the planned start/finish dates for unstarted activities
      if (task.getActualStart() == null)
      {
         task.setPlannedStart(task.getStart());
         task.setPlannedFinish(task.getFinish());
      }

      if (task.getActualStart() == null || task.getActualFinish() == null)
      {
         task.setRemainingEarlyStart(earlyStart.getValue());
         task.setRemainingEarlyFinish(earlyFinish.getValue());
         task.setRemainingLateStart(lateStart.getValue());
         task.setRemainingLateFinish(lateFinish.getValue());
      }

      task.getResourceAssignments().forEach(this::updateDates);
   }

   /**
    * Return the earliest of the current date and the new date.
    *
    * @param currentDate current date
    * @param newDate new date
    * @return earliest date
    */
   private AnnotatedDateTime updateIfBefore(AnnotatedDateTime currentDate, AnnotatedDateTime newDate)
   {
      if (currentDate == null)
      {
         return newDate;
      }

      return newDate.isBefore(currentDate) ? newDate : currentDate;
   }

   /**
    * Return the latest of the current date and the new date.
    *
    * @param currentDate current date
    * @param newDate new date
    * @return latest date
    */
   private AnnotatedDateTime updateIfAfter(AnnotatedDateTime currentDate, AnnotatedDateTime newDate)
   {
      if (currentDate == null)
      {
         return newDate;
      }

      return newDate.isAfter(currentDate) ? newDate : currentDate;
   }

   /**
    * Adjust a Level of Effort activity finish date back to the end of the last working period.
    *
    * @param task parent task
    * @param finish finish date
    * @return adjusted date
    */
   private LocalDateTime adjustFinish(Task task, LocalDateTime finish)
   {
      if (finish == null)
      {
         return null;
      }

      ProjectCalendar calendar = task.getEffectiveCalendar();
      LocalDateTime previousWorkFinish = calendar.getPreviousWorkFinish(finish);

      if (calendar.getWork(previousWorkFinish, finish, TimeUnit.HOURS).getDuration() == 0)
      {
         return previousWorkFinish;
      }

      return finish;
   }

   /**
    * Schedule WBS Summary activities.
    */
   private void wbsSummaryPass()
   {
      List<Task> activities = m_file.getTasks().stream().filter(PrimaveraScheduler::isWbsSummary).collect(Collectors.toList());
      if (activities.isEmpty())
      {
         return;
      }

      for (Task activity : activities)
      {
         wbsSummaryPass(activity);
      }
   }

   /**
    * Schedule a WBS Summary activity.
    *
    * @param task target task
    */
   private void wbsSummaryPass(Task task)
   {
      Task wbs = task.getParentTask();
      if (wbs == null)
      {
         return;
      }

      // These values can be used directly from the parent WBS
      task.setStart(wbs.getStart());
      task.setFinish(wbs.getFinish());
      task.setActualStart(wbs.getActualStart());
      task.setActualFinish(wbs.getActualFinish());

      // These values can only be rolled up from not started or in progress child activities of the WBS
      // so we need to descend the wbs hierarchy find all child activities which match these criteria.
      List<Task> childTasks = allWbsChildTasks(wbs, new ArrayList<>());
      task.setEarlyStart(childTasks.stream().map(Task::getEarlyStart).filter(Objects::nonNull).min(Comparator.naturalOrder()).orElse(null));
      task.setEarlyFinish(childTasks.stream().map(Task::getEarlyFinish).filter(Objects::nonNull).max(Comparator.naturalOrder()).orElse(null));
      task.setRemainingEarlyStart(childTasks.stream().map(Task::getRemainingEarlyStart).filter(Objects::nonNull).min(Comparator.naturalOrder()).orElse(null));
      task.setRemainingEarlyFinish(childTasks.stream().map(Task::getRemainingEarlyFinish).filter(Objects::nonNull).max(Comparator.naturalOrder()).orElse(null));
      task.setLateStart(childTasks.stream().map(Task::getLateStart).filter(Objects::nonNull).min(Comparator.naturalOrder()).orElse(null));
      task.setLateFinish(childTasks.stream().map(Task::getLateFinish).filter(Objects::nonNull).max(Comparator.naturalOrder()).orElse(null));
      task.setRemainingLateStart(childTasks.stream().map(Task::getRemainingLateStart).filter(Objects::nonNull).min(Comparator.naturalOrder()).orElse(null));
      task.setRemainingLateFinish(childTasks.stream().map(Task::getRemainingLateFinish).filter(Objects::nonNull).max(Comparator.naturalOrder()).orElse(null));
   }

   /**
    * Descend through the entire hierarchy beneath a WBS entry collecting all child tasks.
    *
    * @param wbs WBS entry
    * @param childTasks list to populated with child tasks
    * @return list of child tasks
    */
   private List<Task> allWbsChildTasks(Task wbs, List<Task> childTasks)
   {
      childTasks.addAll(wbs.getChildTasks().stream().filter(t -> !t.getSummary() && t.getActivityType() != ActivityType.WBS_SUMMARY && t.getActualFinish() == null).collect(Collectors.toList()));
      wbs.getChildTasks().stream().filter(Task::getSummary).forEach(t -> allWbsChildTasks(t, childTasks));
      return childTasks;
   }

   /**
    * Given a task and a finish date potentially at the start of a working period,
    * determine if there is an earlier equivalent finish date at the end of working
    * period which can be used instead.
    *
    * @param task parent task
    * @param date potential finish date
    * @return finish date
    */
   private LocalDateTime getEquivalentPreviousWorkFinish(Task task, LocalDateTime date)
   {
      if (useTaskEffectiveCalendar(task))
      {
         return getEquivalentPreviousWorkFinish(task.getEffectiveCalendar(), date);
      }

      return getResourceAssignmentStream(task).map(r -> getEquivalentPreviousWorkFinish(r.getEffectiveCalendar(), date)).max(Comparator.naturalOrder()).orElse(null);
   }

   /**
    * Given a calendar and a finish date potentially at the start of a working period,
    * determine if there is an earlier equivalent finish date at the end of working
    * period which can be used instead.
    *
    * @param calendar target calendar
    * @param date potential finish date
    * @return finish date
    */
   private LocalDateTime getEquivalentPreviousWorkFinish(ProjectCalendar calendar, LocalDateTime date)
   {
      LocalDateTime previousWorkFinish = calendar.getPreviousWorkFinish(date);
      if (calendar.getWork(previousWorkFinish, date, TimeUnit.HOURS).getDuration() == 0)
      {
         return previousWorkFinish;
      }
      return date;
   }

   /**
    * Adjust a start date which sits on or after a working period to the
    * start of the next working period.
    *
    * @param task parent task
    * @param date date
    * @return adjusted date
    */
   private LocalDateTime getEquivalentNextWorkStart(Task task, LocalDateTime date)
   {
      if (useTaskEffectiveCalendar(task))
      {
         return getEquivalentNextWorkStart(task.getEffectiveCalendar(), date);
      }

      return getResourceAssignmentStream(task).map(r -> getEquivalentNextWorkStart(r.getEffectiveCalendar(), date)).min(Comparator.naturalOrder()).orElse(null);
   }

   private LocalDateTime getEquivalentNextWorkStart(ProjectCalendar calendar, LocalDateTime date)
   {
      LocalDateTime adjustedDate = calendar.getNextWorkStart(date);
      if (calendar.getWork(date, adjustedDate, TimeUnit.MINUTES).getDuration() == 0)
      {
         return adjustedDate;
      }
      return date;
   }

   /**
    * Retrieve a stream of resource assignments which can be used when scheduling the parent task.
    *
    * @param task parent task
    * @return resource assignment stream
    */
   private Stream<ResourceAssignment> getResourceAssignmentStream(Task task)
   {
      return task.getResourceAssignments().stream().filter(r -> r.getResource() != null && r.getResource().getType() == ResourceType.WORK && r.getUnits().doubleValue() > 0.0);
   }

   /**
    * Add work to a date using the effective calendar from a resource assignment.
    *
    * @param assignment resource assignment
    * @param date date
    * @param work amount of work to add
    * @return date plus work
    */
   private LocalDateTime getDateFromWork(ResourceAssignment assignment, LocalDateTime date, Duration work)
   {
      return getDateFromWork(assignment.getResource().getCalendar(), assignment.getUnits(), date, work);
   }

   private LocalDateTime getDateFromWork(ProjectCalendar calendar, Number units, LocalDateTime date, Duration work)
   {
      double unitsValue = units.doubleValue();
      if (unitsValue == 0.0)
      {
         return date;
      }

      if (unitsValue != 100.0)
      {
         work = Duration.getInstance((work.getDuration() * 100.0) / unitsValue, work.getUnits());
      }

      return getDate(calendar, date, work);
   }

   /**
    * Given a task and a start date potentially at the end of a working period,
    * determine if there is a later equivalent start date at the start of the
    * next working period.
    *
    * @param task parent task
    * @param date potential start date
    * @return start date
    */
   private LocalDateTime getNextWorkStart(Task task, LocalDateTime date)
   {
      if (useTaskEffectiveCalendar(task))
      {
         return task.getEffectiveCalendar().getNextWorkStart(date);
      }

      return getResourceAssignmentStream(task).map(r -> r.getEffectiveCalendar().getNextWorkStart(date)).min(Comparator.naturalOrder()).orElse(null);
   }

   private boolean hasRemainingDuration(Task task)
   {
      Duration remaining;
      if (useTaskEffectiveCalendar(task))
      {
         remaining = task.getRemainingDuration();
      }
      else
      {
         remaining = getResourceAssignmentStream(task).map(ResourceAssignment::getRemainingWork).max(Comparator.naturalOrder()).orElse(null);
      }

      return remaining.getDuration() != 0.0;
   }

   private boolean hasActualDuration(Task task)
   {
      Duration actual;
      if (useTaskEffectiveCalendar(task))
      {
         actual = task.getActualDuration();
      }
      else
      {
         actual = getResourceAssignmentStream(task).map(ResourceAssignment::getActualWork).max(Comparator.naturalOrder()).orElse(null);
      }

      return actual.getDuration() != 0.0;
   }

   /**
    * Retrieve the constraint type and default to As Soon As Possible
    * if no constraint type is present.
    *
    * @param task target task
    * @return constraint type
    */
   private ConstraintType getConstraintType(Task task)
   {
      return task.getConstraintType() == null ? ConstraintType.AS_SOON_AS_POSSIBLE : task.getConstraintType();
   }

   private ProjectFile m_file;
   private ProjectCalendar m_twentyFourHourCalendar;
   private LocalDateTime m_dataDate;
   private LocalDateTime m_projectStartDate;
   private LocalDateTime m_projectFinishDate;
}
