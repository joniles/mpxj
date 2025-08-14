/*
 * file:       MicrosoftScheduler.java
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

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.mpxj.ConstraintType;
import org.mpxj.Duration;
import org.mpxj.ProjectCalendar;
import org.mpxj.ProjectFile;
import org.mpxj.Relation;
import org.mpxj.ResourceAssignment;
import org.mpxj.ResourceType;
import org.mpxj.Task;
import org.mpxj.TaskField;
import org.mpxj.TaskMode;
import org.mpxj.TaskType;
import org.mpxj.TimeUnit;
import org.mpxj.common.LocalDateTimeHelper;

/**
 * Implements the Critical Path Method to schedule a project so
 * that the resulting schedule matches closely the results you'd see if
 * you scheduled the same project in Microsoft Project.
 */
public class MicrosoftScheduler implements Scheduler
{
   @Override public void schedule(ProjectFile file, LocalDateTime startDate) throws CpmException
   {
      m_file = file;
      m_projectStartDate = startDate;
      m_calculatedLateStart.clear();

      List<Task> tasks = new DepthFirstGraphSort(m_file, this::isTask).sort();
      m_sortedTasks = tasks;
      if (tasks.isEmpty())
      {
         return;
      }

      validateTasks(tasks);

      clearDates();

      m_backwardPass = false;
      forwardPass(tasks);

      boolean summaryTasksHaveLogic = m_file.getTasks().stream().anyMatch(t -> t.getSummary() && (!t.getPredecessors().isEmpty() || !t.getSuccessors().isEmpty()));
      if (summaryTasksHaveLogic)
      {
         createSummaryTaskRelationships();

         tasks = new DepthFirstGraphSort(m_file, this::isTask)
         {
            @Override public List<Relation> getSuccessors(Task task)
            {
               List<Relation> successors = task.getSuccessors();
               List<Relation> summaryTaskSuccessors = m_summaryTaskSuccessors.get(task);
               if (summaryTaskSuccessors != null)
               {
                  successors = new ArrayList<>(successors);
                  successors.addAll(summaryTaskSuccessors);
               }
               return successors;
            }
         }.sort();

         m_sortedTasks = tasks;

         forwardPass(tasks);
      }

      m_projectFinishDate = tasks.stream().map(Task::getEarlyFinish).filter(Objects::nonNull).max(Comparator.naturalOrder()).orElseThrow(() -> new CpmException("Missing early finish date"));

      backwardPass(tasks);
      m_backwardPass = true;

      if (tasks.stream().anyMatch(t -> t.getConstraintType() == ConstraintType.AS_LATE_AS_POSSIBLE))
      {
         forwardPass(tasks);
      }

      for (Task task : tasks)
      {
         if (task.getExternalTask() || task.getExternalProject() || !task.getActive() || task.getTaskMode() == TaskMode.MANUALLY_SCHEDULED)
         {
            continue;
         }

         task.setStart(task.getActualStart() == null ? (task.getConstraintType() == ConstraintType.AS_LATE_AS_POSSIBLE ? task.getLateStart() : task.getEarlyStart()) : task.getActualStart());
         task.setFinish(task.getActualFinish() == null ? (task.getConstraintType() == ConstraintType.AS_LATE_AS_POSSIBLE ? task.getLateFinish() : task.getEarlyFinish()) : task.getActualFinish());

         if (!useTaskEffectiveCalendar(task))
         {
            // TODO: should be using union of resource calendars?
            task.setDuration(task.getEffectiveCalendar().getWork(task.getStart(), task.getFinish(), TimeUnit.DAYS));
         }
      }

      m_file.getChildTasks().forEach(this::rollupDates);
   }

   private void validateTasks(List<Task> tasks) throws CpmException
   {
      for (Task task : tasks)
      {
         if (task.getDuration() == null && (!getResourceAssignmentStream(task).findAny().isPresent() || getResourceAssignmentStream(task).noneMatch(r -> r.getWork() != null)))
         {
            throw new CpmException("Task has no duration value and no resource assignments with a work value: " + task);
         }

         if (task.getActualDuration() == null && (!getResourceAssignmentStream(task).findAny().isPresent() || getResourceAssignmentStream(task).noneMatch(r -> r.getActualWork() != null)))
         {
            throw new CpmException("Task has no actual duration value and no resource assignments with an actual work value: " + task);
         }

         if (task.getRemainingDuration() == null && (!getResourceAssignmentStream(task).findAny().isPresent() || getResourceAssignmentStream(task).noneMatch(r -> r.getRemainingWork() != null)))
         {
            throw new CpmException("Task has no remaining duration value and no resource assignments with a remaining work value: " + task);
         }
      }
   }

   /**
    * Clear dates ready to be repopulated.
    */
   private void clearDates()
   {
      for (Task task : m_file.getTasks())
      {
         if (task.getExternalTask() || task.getExternalProject() || !task.getActive() || task.getTaskMode() == TaskMode.MANUALLY_SCHEDULED)
         {
            continue;
         }

         task.setStart(null);
         task.setFinish(null);
         task.setEarlyStart(null);
         task.setEarlyFinish(null);
         task.setLateStart(null);
         task.setLateFinish(null);

         // Clear the critical flag to force it to be recalculated
         task.set(TaskField.CRITICAL, null);
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
      if (task.getTaskMode() == TaskMode.MANUALLY_SCHEDULED)
      {
         // TODO: we need to be able to identify where NO start date has been supplied, which appears to trigger using ScheduledStart rather than Start
         task.setEarlyStart(task.getStart());
         task.setEarlyFinish(task.getFinish());
         return;
      }

      // We'll use external tasks as predecessors when scheduling, but we'll leave their early dates unchanged.
      if (task.getExternalTask() || task.getExternalProject())
      {
         return;
      }

      LocalDateTime earlyStart;

      LocalDateTime earlyFinish = null;
      List<Relation> predecessors = task.getPredecessors().stream().filter(r -> isTask(r.getPredecessorTask())).collect(Collectors.toList());
      List<Relation> summaryTaskPredecessors = m_summaryTaskPredecessors.get(task);
      if (summaryTaskPredecessors != null)
      {
         predecessors = new ArrayList<>(predecessors);
         predecessors.addAll(summaryTaskPredecessors);
      }

      if (task.getActualStart() == null)
      {
         if (predecessors.isEmpty())
         {
            switch (getConstraintType(task))
            {
               case START_NO_EARLIER_THAN:
               {
                  earlyStart = task.getConstraintDate();
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
                  earlyStart = addLevelingDelay(task, getNextWorkStart(task, m_projectStartDate));
                  break;
               }
            }
         }
         else
         {
            earlyStart = predecessors.stream().map(this::calculateEarlyStart).max(Comparator.naturalOrder()).orElseThrow(() -> new CpmException("Missing early start date"));
         }
         earlyStart = getNextWorkStart(task, earlyStart);

         switch (getConstraintType(task))
         {
            case START_NO_EARLIER_THAN:
            {
               if (earlyStart.isBefore(task.getConstraintDate()))
               {
                  earlyStart = task.getConstraintDate();
               }
               break;
            }

            case FINISH_NO_LATER_THAN:
            {
               LocalDateTime latestStart = getDateFromFinishAndDuration(task, task.getConstraintDate());
               if (earlyStart.isAfter(latestStart))
               {
                  earlyStart = latestStart;
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

            case START_NO_LATER_THAN:
            {
               if (earlyStart.isAfter(task.getConstraintDate()))
               {
                  earlyStart = task.getConstraintDate();
               }
               break;
            }

            case MUST_START_ON:
            case START_ON:
            {
               earlyStart = task.getConstraintDate();
               break;
            }

            case MUST_FINISH_ON:
            case FINISH_ON:
            {
               earlyFinish = task.getConstraintDate();
               earlyStart = getDateFromFinishAndDuration(task, earlyFinish);
               break;
            }

            default:
            {
               break;
            }
         }
      }
      else
      {
         earlyStart = task.getActualStart();
         if (task.getConstraintType() != null && task.getActualFinish() == null)
         {
            earlyFinish = getDateFromStartAndDuration(task, earlyStart);

            switch (task.getConstraintType())
            {
               case FINISH_NO_EARLIER_THAN:
               {
                  if (earlyFinish.isBefore(task.getConstraintDate()))
                  {
                     earlyFinish = task.getConstraintDate();
                  }
                  break;
               }

               default:
               {
                  // TODO: construct samples for other constraints and test
                  break;
               }
            }
         }
      }

      if (earlyFinish == null)
      {
         earlyFinish = task.getActualFinish() == null ? getDateFromStartAndDuration(task, earlyStart) : task.getActualFinish();
      }

      task.setEarlyStart(earlyStart);
      task.setEarlyFinish(earlyFinish);
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
      // We'll use external tasks as successors when scheduling, but we'll leave their late dates unchanged.
      if (task.getExternalTask() || task.getExternalProject())
      {
         return;
      }

      List<Relation> successors = m_file.getRelations().getSuccessors(task).stream().filter(r -> isTask(r.getSuccessorTask()) && r.getSuccessorTask().getActualFinish() == null).collect(Collectors.toList());
      List<Relation> summaryTaskSuccessors = m_summaryTaskSuccessors.get(task);
      if (summaryTaskSuccessors != null)
      {
         successors = new ArrayList<>(successors);
         successors.addAll(summaryTaskSuccessors);
      }

      LocalDateTime lateFinish;

      if (task.getActualFinish() == null)
      {
         if (successors.isEmpty())
         {
            lateFinish = m_projectFinishDate;
         }
         else
         {
            if (task.getMilestone() && task.getDuration().getDuration() == 0 && task.getActualStart() != null)
            {
               lateFinish = task.getActualStart();
            }
            else
            {
               lateFinish = successors.stream().map(this::calculateLateFinish).min(Comparator.naturalOrder()).orElseThrow(() -> new CpmException("Missing late start date"));
            }
         }

         switch (getConstraintType(task))
         {
            case MUST_START_ON:
            {
               lateFinish = getDateFromStartAndDuration(task, task.getConstraintDate());
               break;
            }

            case FINISH_ON:
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

         if (task.getDeadline() != null && lateFinish.isAfter(task.getDeadline()))
         {
            lateFinish = task.getDeadline();
         }

         // If we are at the start of the next period of work, we can move back to the end of the previous period of work
         lateFinish = getEquivalentPreviousWorkFinish(task, lateFinish);
      }
      else
      {
         lateFinish = task.getActualFinish();
      }

      if (task.getTaskMode() == TaskMode.MANUALLY_SCHEDULED)
      {
         LocalDateTime lateStart = getDateFromFinishAndDuration(task, lateFinish);
         m_calculatedLateStart.put(task, lateStart);

         if (task.getActualFinish() == null)
         {
            task.setLateStart(lateStart);
            task.setLateFinish(lateFinish);
         }
         else
         {
            task.setLateStart(task.getActualStart());
            task.setLateFinish(lateFinish);
         }
      }
      else
      {
         LocalDateTime lateStart = getDateFromFinishAndRemainingDuration(task, lateFinish);
         m_calculatedLateStart.put(task, lateStart);

         task.setLateStart(task.getActualStart() == null ? lateStart : task.getActualStart());
         task.setLateFinish(lateFinish);
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

      if (predecessorTask.getActualStart() == null)
      {
         // Predecessor not started
         // Successor not started
         LocalDateTime finish = isAlap(relation) ? predecessorTask.getLateFinish() : predecessorTask.getEarlyFinish();
         return addLag(relation, finish);
      }

      // Predecessor started
      if (predecessorTask.getActualFinish() != null)
      {
         // Predecessor finished
         // Successor not started
         LocalDateTime finish = isAlap(relation) ? predecessorTask.getLateFinish() : predecessorTask.getEarlyFinish();
         return addLag(relation, finish);
      }

      // Predecessor not finished
      // Successor not started
      LocalDateTime finish = isAlap(relation) ? predecessorTask.getLateFinish() : predecessorTask.getEarlyFinish();
      return addLag(relation, finish);
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

      if (predecessorTask.getActualStart() == null)
      {
         // Predecessor not started
         // Successor not started
         LocalDateTime start = isAlap(relation) ? predecessorTask.getLateStart() : predecessorTask.getEarlyStart();
         return addLag(relation, start);
      }

      // Predecessor started
      if (predecessorTask.getActualFinish() != null)
      {
         // Predecessor finished
         // Successor not started
         return addLag(relation, predecessorTask.getActualStart());
      }

      // Predecessor not finished
      // Successor not started
      LocalDateTime start = isAlap(relation) ? predecessorTask.getLateStart() : predecessorTask.getEarlyStart();
      return addLag(relation, start);
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
      if (predecessorTask.getActualStart() == null)
      {
         // Predecessor not started
         // Successor not started
         LocalDateTime start = isAlap(relation) ? predecessorTask.getLateStart() : predecessorTask.getEarlyStart();
         return addLag(relation, getDateFromFinishAndDuration(relation.getSuccessorTask(), start));
      }

      // Predecessor started
      if (predecessorTask.getActualFinish() != null)
      {
         // Predecessor finished
         // Successor not started
         LocalDateTime start = isAlap(relation) ? predecessorTask.getLateStart() : predecessorTask.getEarlyStart();
         return addLag(relation, getDateFromFinishAndDuration(relation.getSuccessorTask(), start));
      }

      // Predecessor not finished
      // Successor not started
      LocalDateTime start = isAlap(relation) ? predecessorTask.getLateStart() : predecessorTask.getEarlyStart();
      return addLag(relation, getDateFromFinishAndDuration(relation.getSuccessorTask(), start));
   }

   /**
    * Calculate the early start for the successor task in a finish-finish relationship.
    *
    * @param relation relationship between two tasks
    * @return calculated early start date
    */
   private LocalDateTime calculateEarlyStartForFinishFinish(Relation relation)
   {
      // There is an interesting bug in Project 2010, and possibly other versions, where the ES, and EF dates
      // for the predecessor of an FF task are not set correctly. Calculating the project shows the correct dates,
      // but when the file is saved and reopened, the incorrect dates are shown again. Current versions of MS Project (2024?)
      // seem to be unaffected.

      Task predecessorTask = relation.getPredecessorTask();

      if (predecessorTask.getActualStart() == null)
      {
         // Predecessor not started
         // Successor not started
         LocalDateTime finish = isAlap(relation) ? predecessorTask.getLateFinish() : predecessorTask.getEarlyFinish();
         LocalDateTime earlyStart = addLag(relation, getDateFromFinishAndRemainingDuration(relation.getSuccessorTask(), finish));
         return earlyStart.isBefore(m_projectStartDate) ? m_projectStartDate : earlyStart;
      }

      // Predecessor started
      if (predecessorTask.getActualFinish() != null)
      {
         // Predecessor finished
         // Successor not started
         LocalDateTime earlyStart = addLag(relation, getDateFromFinishAndRemainingDuration(relation.getSuccessorTask(), predecessorTask.getActualFinish()));
         return earlyStart.isBefore(m_projectStartDate) ? m_projectStartDate : earlyStart;
      }

      // Predecessor not finished
      // Successor not started
      LocalDateTime finish = isAlap(relation) ? predecessorTask.getLateFinish() : predecessorTask.getEarlyFinish();
      LocalDateTime earlyStart = addLag(relation, getDateFromFinishAndRemainingDuration(relation.getSuccessorTask(), finish));
      return earlyStart.isBefore(m_projectStartDate) ? m_projectStartDate : earlyStart;
   }

   /**
    * Calculate the late finish for the predecessor task in this relationship.
    *
    * @param relation relationship between two tasks
    * @return calculated late finish date
    */
   private LocalDateTime calculateLateFinish(Relation relation)
   {
      LocalDateTime lateFinish;

      switch (relation.getType())
      {
         case START_START:
         {
            lateFinish = calculateLateFinishForStartStart(relation);
            break;
         }

         case FINISH_FINISH:
         {
            lateFinish = calculateLateFinishForFinishFinish(relation);
            break;
         }

         case START_FINISH:
         {
            lateFinish = calculateLateFinishForStartFinish(relation);
            break;
         }

         case FINISH_START:
         {
            lateFinish = calculateLateFinishForFinishStart(relation);
            break;
         }

         default:
         {
            throw new UnsupportedOperationException();
         }
      }

      if (lateFinish.isAfter(m_projectFinishDate))
      {
         return m_projectFinishDate;
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
      ProjectCalendar calendar = predecessorTask.getEffectiveCalendar();

      LocalDateTime lateStart;
      LocalDateTime lateFinish;

      if (predecessorTask.getActualStart() == null)
      {
         // Predecessor not started
         if (successorTask.getActualStart() == null)
         {
            // Successor not started
            if (predecessorTask.getTaskMode() == TaskMode.MANUALLY_SCHEDULED && successorTask.getSuccessors().isEmpty())
            {
               lateFinish = successorTask.getLateFinish();
            }
            else
            {
               if (predecessorTask.getTaskMode() == TaskMode.MANUALLY_SCHEDULED)
               {
                  lateFinish = m_projectFinishDate;
               }
               else
               {
                  lateStart = removeLag(relation, calendar.getNextWorkStart(successorTask.getLateStart()));
                  lateFinish = getDateFromStartAndDuration(predecessorTask, lateStart);
               }
            }
         }
         else
         {
            // successor started
            // successor not finished
            if (successorTask.getSuccessors().isEmpty())
            {
               lateFinish = m_projectFinishDate;
            }
            else
            {
               lateStart = getDateFromStartAndActualDuration(successorTask, successorTask.getActualStart());
               lateFinish = getDateFromStartAndDuration(predecessorTask, lateStart);
            }
         }
      }
      else
      {
         // Predecessor Started
         // Predecessor not finished
         if (successorTask.getActualStart() == null)
         {
            // Successor not started
            lateFinish = m_projectFinishDate;
         }
         else
         {
            // successor started
            // successor not finished
            lateFinish = m_projectFinishDate;
         }
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
            return removeLag(relation, relation.getSuccessorTask().getLateFinish());
         }

         // Successor started
         // Successor not finished
         return removeLag(relation, relation.getSuccessorTask().getLateFinish());
      }

      // Predecessor Started
      // Predecessor not finished
      if (successorTask.getActualStart() == null)
      {
         // Successor not started
         return removeLag(relation, relation.getSuccessorTask().getLateFinish());
      }

      // Successor started
      // Successor not finished
      return removeLag(relation, relation.getSuccessorTask().getLateFinish());
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
            return removeLag(relation, m_calculatedLateStart.getOrDefault(successorTask, successorTask.getLateStart()));
         }

         // successor started
         // successor not finished
         return removeLag(relation, m_calculatedLateStart.getOrDefault(successorTask, successorTask.getLateStart()));
      }

      // Predecessor Started
      // Predecessor not finished
      if (successorTask.getActualStart() == null)
      {
         // Successor not started
         return removeLag(relation, m_calculatedLateStart.getOrDefault(successorTask, successorTask.getLateStart()));
      }

      // successor started
      // successor not finished
      return removeLag(relation, m_calculatedLateStart.getOrDefault(successorTask, successorTask.getLateStart()));
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
            return removeLag(relation, getDateFromStartAndDuration(predecessorTask, successorTask.getLateFinish()));
         }

         // successor started
         // successor not finished
         return removeLag(relation, getDateFromStartAndDuration(predecessorTask, successorTask.getLateFinish()));
      }

      // Predecessor Started
      // Predecessor not finished
      if (successorTask.getActualStart() == null)
      {
         // Successor not started
         return removeLag(relation, getDateFromStartAndDuration(predecessorTask, successorTask.getLateFinish()));
      }

      // successor started
      // successor not finished
      return removeLag(relation, getDateFromStartAndDuration(predecessorTask, successorTask.getLateFinish()));
   }

   /**
    * Add leveling delay to a start date.
    *
    * @param task parent task
    * @param date start date
    * @return date with leveling delay
    */
   private LocalDateTime addLevelingDelay(Task task, LocalDateTime date)
   {
      Duration delay = task.getLevelingDelay();
      if (delay == null || delay.getDuration() == 0)
      {
         return date;
      }

      if (!delay.getUnits().isElapsed())
      {
         TimeUnit newTimeUnit = DURATION_UNITS_MAP.get(delay.getUnits());
         if (newTimeUnit == null)
         {
            throw new UnsupportedOperationException("Unsupported TimeUnit " + delay.getUnits());
         }
         delay = Duration.getInstance(delay.getDuration(), newTimeUnit);
      }

      ProjectCalendar calendar = task.getEffectiveCalendar();
      // ensure we are in a working period
      date = calendar.getNextWorkStart(date);
      return calendar.getDate(date, delay);
   }

   /**
    * Method returns true if task should be processed as part of forward and backward pass.
    *
    * @param task task to test
    * @return true of task should be scheduled
    */
   public boolean isTask(Task task)
   {
      return !((task.getSummary() && !task.getExternalProject()) || !task.getActive() || task.getNull());
   }

   /**
    * Add lag to a date.
    *
    * @param relation relation between tasks
    * @param date date
    * @return date with lag
    */
   private LocalDateTime addLag(Relation relation, LocalDateTime date)
   {
      if (relation.getLag().getDuration() == 0)
      {
         return date;
      }

      Duration lag = relation.getLag();
      if (lag.getUnits() == TimeUnit.PERCENT)
      {
         Duration predecessorDuration = relation.getPredecessorTask().getDuration();
         lag = Duration.getInstance((predecessorDuration.getDuration() * lag.getDuration()) / 100.0, predecessorDuration.getUnits());
      }

      ProjectCalendar calendar = relation.getSuccessorTask().getEffectiveCalendar();
      return calendar.getDate(date, lag);
   }

   /**
    * Remove lag from a date.
    *
    * @param relation relation between tasks
    * @param date date with lag
    * @return date without lag
    */
   private LocalDateTime removeLag(Relation relation, LocalDateTime date)
   {
      if (relation.getLag().getDuration() == 0)
      {
         return date;
      }

      Duration lag = relation.getLag();
      if (lag.getUnits() == TimeUnit.PERCENT)
      {
         Duration predecessorDuration = relation.getPredecessorTask().getDuration();
         lag = Duration.getInstance((predecessorDuration.getDuration() * lag.getDuration()) / 100.0, predecessorDuration.getUnits());
      }

      ProjectCalendar calendar = relation.getSuccessorTask().getEffectiveCalendar();
      return calendar.getDate(date, lag.negate());
   }

   /**
    * Create temporary relationships between tasks to represent summary task logic.
    */
   private void createSummaryTaskRelationships()
   {
      m_file.getRelations().stream().filter(r -> r.getPredecessorTask().getSummary() || r.getSuccessorTask().getSummary()).forEach(this::createSummaryTaskRelationship);
   }

   /**
    * Create a temporary relationship to represent summary task logic.
    *
    * @param relation relationship representing summary task logic
    */
   private void createSummaryTaskRelationship(Relation relation)
   {
      List<Task> predecessors = Collections.singletonList(relation.getPredecessorTask());
      if (predecessors.get(0).getSummary())
      {
         switch (relation.getType())
         {
            case START_START:
            case START_FINISH:
            {
               predecessors = Collections.singletonList(findEarliestSubtask(predecessors.get(0)));
               break;
            }

            default:
            {
               predecessors = allChildTasks(predecessors.get(0));
               break;
            }
         }
      }

      List<Task> successors = Collections.singletonList(relation.getSuccessorTask());
      if (successors.get(0).getSummary())
      {
         successors = allChildTasks(successors.get(0));
      }

      for (Task predecessor : predecessors)
      {
         for (Task successor : successors)
         {
            Relation newRelation = new Relation.Builder().from(relation).predecessorTask(predecessor).successorTask(successor).build();

            m_summaryTaskPredecessors.computeIfAbsent(successor, k -> new ArrayList<>()).add(newRelation);
            m_summaryTaskSuccessors.computeIfAbsent(predecessor, k -> new ArrayList<>()).add(newRelation);
         }
      }
   }

   /**
    * Roll up dates to a summary task.
    *
    * @param parentTask parent summary task
    */
   private void rollupDates(Task parentTask)
   {
      // NOTE: summary tasks can be manually scheduled. We're currently ignoring this...
      if (!parentTask.hasChildTasks())
      {
         return;
      }

      int finished = 0;
      LocalDateTime startDate = parentTask.getStart();
      LocalDateTime finishDate = parentTask.getFinish();
      LocalDateTime actualStartDate = parentTask.getActualStart();
      LocalDateTime actualFinishDate = parentTask.getActualFinish();
      LocalDateTime earlyStartDate = parentTask.getEarlyStart();
      LocalDateTime earlyFinishDate = parentTask.getEarlyFinish();
      LocalDateTime lateStartDate = parentTask.getLateStart();
      LocalDateTime lateFinishDate = parentTask.getLateFinish();
      boolean critical = false;

      for (Task task : parentTask.getChildTasks())
      {
         if (task.getExternalTask())
         {
            continue;
         }

         rollupDates(task);

         // the child tasks can have null dates (e.g. for nested wbs elements with no task children) so we
         // still must protect against some children having null dates

         startDate = LocalDateTimeHelper.min(startDate, task.getStart());
         finishDate = LocalDateTimeHelper.max(finishDate, task.getFinish());
         actualStartDate = LocalDateTimeHelper.min(actualStartDate, task.getActualStart());
         actualFinishDate = LocalDateTimeHelper.max(actualFinishDate, task.getActualFinish());

         if (task.getConstraintType() == ConstraintType.AS_LATE_AS_POSSIBLE)
         {
            earlyStartDate = LocalDateTimeHelper.min(earlyStartDate, task.getLateStart());
            earlyFinishDate = LocalDateTimeHelper.max(earlyFinishDate, task.getLateFinish());
         }
         else
         {
            earlyStartDate = LocalDateTimeHelper.min(earlyStartDate, task.getEarlyStart());
            earlyFinishDate = LocalDateTimeHelper.max(earlyFinishDate, task.getEarlyFinish());
         }

         if (task.getTaskMode() == TaskMode.MANUALLY_SCHEDULED)
         {
            lateStartDate = LocalDateTimeHelper.min(lateStartDate, task.getEarlyStart());
            lateFinishDate = LocalDateTimeHelper.max(lateFinishDate, task.getEarlyFinish());
         }
         else
         {
            lateStartDate = LocalDateTimeHelper.min(lateStartDate, task.getLateStart());
            lateFinishDate = LocalDateTimeHelper.max(lateFinishDate, task.getLateFinish());
         }

         if (task.getActualFinish() != null)
         {
            ++finished;
         }

         critical = critical || task.getCritical();
      }

      parentTask.setStart(startDate);
      parentTask.setFinish(finishDate);
      parentTask.setActualStart(actualStartDate);
      parentTask.setEarlyStart(earlyStartDate);
      parentTask.setEarlyFinish(earlyFinishDate);
      parentTask.setLateStart(lateStartDate);
      parentTask.setLateFinish(lateFinishDate);

      //
      // Only if all child tasks have actual finish dates do we
      // set the actual finish date on the parent task.
      //
      if (finished == parentTask.getChildTasks().size())
      {
         parentTask.setActualFinish(actualFinishDate);
      }

      parentTask.setCritical(critical);

      parentTask.setDuration(parentTask.getEffectiveCalendar().getWork(startDate, finishDate, TimeUnit.DAYS));
   }

   /**
    * Find the earliest child tasks from entire child task hierarchy.
    *
    * @param summaryTask parent summary task
    * @return earliest child task
    */
   private Task findEarliestSubtask(Task summaryTask)
   {
      return allChildTasks(summaryTask).stream().min(Comparator.comparing(Task::getEarlyStart)).orElse(null);
   }

   /**
    * Find all child tasks from the entire child task hierarchy.
    *
    * @param summaryTask parent summary task
    * @return all child tasks
    */
   private List<Task> allChildTasks(Task summaryTask)
   {
      return allChildTasks(summaryTask, new ArrayList<>());
   }

   /**
    * Find all child tasks from the entire child task hierarchy,
    * populating the childTasks list.
    *
    * @param summaryTask parent summary task
    * @param childTasks task list to populate
    * @return task list
    */
   private List<Task> allChildTasks(Task summaryTask, List<Task> childTasks)
   {
      childTasks.addAll(summaryTask.getChildTasks().stream().filter(t -> !t.getSummary() && t.getActive()).collect(Collectors.toList()));
      summaryTask.getChildTasks().stream().filter(Task::getSummary).forEach(t -> allChildTasks(t, childTasks));
      return childTasks;
   }

   /**
    * Convenience method to allow sorted task list to be retrieved for testing.
    *
    * @return sorted task list
    */
   List<Task> getSortedTasks()
   {
      return m_sortedTasks;
   }

   /**
    * Add task duration to a date.
    *
    * @param task parent task
    * @param date date
    * @return date plus duration
    */
   private LocalDateTime getDateFromStartAndDuration(Task task, LocalDateTime date)
   {
      if (useTaskEffectiveCalendar(task))
      {
         return task.getEffectiveCalendar().getDate(date, task.getDuration());
      }

      return getDateFromStartAndWork(task, date);
   }

   /**
    * Add task actual duration to a date.
    *
    * @param task parent task
    * @param date date
    * @return date plus duration
    */
   private LocalDateTime getDateFromStartAndActualDuration(Task task, LocalDateTime date)
   {
      if (useTaskEffectiveCalendar(task))
      {
         return task.getEffectiveCalendar().getDate(date, task.getActualDuration());
      }

      return getDateFromStartAndActualWork(task, date);
   }

   /**
    * Subtract task duration from a date.
    *
    * @param task parent task
    * @param date date
    * @return date minus duration
    */
   private LocalDateTime getDateFromFinishAndDuration(Task task, LocalDateTime date)
   {
      if (useTaskEffectiveCalendar(task))
      {
         return task.getEffectiveCalendar().getDate(date, task.getDuration().negate());
      }

      return getDateFromFinishAndWork(task, date);
   }

   /**
    * Subtract task remaining duration from a date.
    *
    * @param task parent task
    * @param date date
    * @return date minus remaining duration
    */
   private LocalDateTime getDateFromFinishAndRemainingDuration(Task task, LocalDateTime date)
   {
      if (useTaskEffectiveCalendar(task))
      {
         return task.getEffectiveCalendar().getDate(date, task.getRemainingDuration().negate());
      }
      return getDateFromFinishAndRemainingWork(task, date);
   }

   /**
    * Determine if the tasks effective  calendar should be used when scheduling.
    *
    * @param task task
    * @return true if effective calendar should be used
    */
   private boolean useTaskEffectiveCalendar(Task task)
   {
      return task.getType() == TaskType.FIXED_DURATION || !getResourceAssignmentStream(task).findAny().isPresent();
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
    * Find latest date by adding resource assignment actual work to a date.
    *
    * @param task parent task
    * @param date date
    * @return date plus work
    */
   private LocalDateTime getDateFromStartAndActualWork(Task task, LocalDateTime date)
   {
      return getResourceAssignmentStream(task).map(r -> getDateFromWork(r, date, r.getActualWork())).max(Comparator.naturalOrder()).orElseGet(null);
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
    * Find the earliest date by subtracting resource assignment remaining work from a date.
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
    * Add work to a date using the effective calendar from a resource assignment.
    *
    * @param assignment resource assignment
    * @param date date
    * @param work amount of work to add
    * @return date plus work
    */
   private LocalDateTime getDateFromWork(ResourceAssignment assignment, LocalDateTime date, Duration work)
   {
      double units = assignment.getUnits().doubleValue();
      if (units != 100.0)
      {
         work = Duration.getInstance((work.getDuration() * 100.0) / units, work.getUnits());
      }

      return assignment.getEffectiveCalendar().getDate(date, work);
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
         return getNextWorkStart(task, task.getEffectiveCalendar(), date);
      }

      return getResourceAssignmentStream(task).map(r -> getNextWorkStart(task, r.getEffectiveCalendar(), date)).min(Comparator.naturalOrder()).orElse(null);
   }

   /**
    * Given a task, a calendar, and a start date potentially at the end of a working period,
    * determine if there is a later equivalent start date at the start of the
    * next working period.
    *
    * @param task parent task
    * @param calendar target calendar
    * @param date potential start date
    * @return start date
    */
   private LocalDateTime getNextWorkStart(Task task, ProjectCalendar calendar, LocalDateTime date)
   {
      LocalDateTime nextWorkStart = calendar.getNextWorkStart(date);
      if (nextWorkStart.isAfter(date) && task.getMilestone() && calendar.getPreviousWorkFinish(date).isEqual(date))
      {
         // A milestone can sit at the end of a working period.
         return date;
      }
      return nextWorkStart;
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
    * Determine if this relation should have ALAP logic applied.
    *
    * @param relation target relation
    * @return true if ALAP logic should be applied
    */
   private boolean isAlap(Relation relation)
   {
      return relation.getPredecessorTask().getConstraintType() == ConstraintType.AS_LATE_AS_POSSIBLE && relation.getSuccessorTask().getConstraintType() != ConstraintType.AS_LATE_AS_POSSIBLE && m_backwardPass;
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
   private List<Task> m_sortedTasks;
   private boolean m_backwardPass;
   private LocalDateTime m_projectStartDate;
   private LocalDateTime m_projectFinishDate;
   private final Map<Task, List<Relation>> m_summaryTaskPredecessors = new HashMap<>();
   private final Map<Task, List<Relation>> m_summaryTaskSuccessors = new HashMap<>();
   private final Map<Task, LocalDateTime> m_calculatedLateStart = new HashMap<>();

   private static final Map<TimeUnit, TimeUnit> DURATION_UNITS_MAP = new HashMap<>();
   static
   {
      DURATION_UNITS_MAP.put(TimeUnit.MINUTES, TimeUnit.ELAPSED_MINUTES);
      DURATION_UNITS_MAP.put(TimeUnit.HOURS, TimeUnit.ELAPSED_HOURS);
      DURATION_UNITS_MAP.put(TimeUnit.DAYS, TimeUnit.ELAPSED_DAYS);
      DURATION_UNITS_MAP.put(TimeUnit.WEEKS, TimeUnit.ELAPSED_WEEKS);
      DURATION_UNITS_MAP.put(TimeUnit.MONTHS, TimeUnit.ELAPSED_MONTHS);
      DURATION_UNITS_MAP.put(TimeUnit.YEARS, TimeUnit.ELAPSED_YEARS);
   }
}
