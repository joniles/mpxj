package net.sf.mpxj.cpm;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import net.sf.mpxj.ConstraintType;
import net.sf.mpxj.Duration;
import net.sf.mpxj.ProjectCalendar;
import net.sf.mpxj.ProjectFile;
import net.sf.mpxj.Relation;
import net.sf.mpxj.ResourceAssignment;
import net.sf.mpxj.ResourceType;
import net.sf.mpxj.Task;
import net.sf.mpxj.TaskMode;
import net.sf.mpxj.TaskType;
import net.sf.mpxj.TimeUnit;

public class MicrosoftScheduler implements Scheduler
{
   public MicrosoftScheduler(ProjectFile file)
   {
      m_file = file;
   }

   public void process(LocalDateTime projectStartDate) throws Exception
   {
      m_projectStartDate = projectStartDate;
      m_calculatedLateStart.clear();

      List<Task> tasks = new DepthFirstGraphSort(m_file, this::isTask).sort();
      m_sortedTasks = tasks;
      if (tasks.isEmpty())
      {
         return;
      }

      // TODO
      // clearDates();

      m_backwardPass = false;
      forwardPass(tasks);

      boolean summaryTasksHaveLogic = m_file.getTasks().stream().anyMatch(t -> t.getSummary() && (!t.getPredecessors().isEmpty() || !t.getSuccessors().isEmpty()));
      if (summaryTasksHaveLogic)
      {
         createSummaryTaskRelationships();

         tasks = new DepthFirstGraphSort(m_file, this::isTask) {
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

      m_projectFinishDate = tasks.stream().map(Task::getEarlyFinish).max(Comparator.naturalOrder()).orElseThrow(() -> new CpmException("Missing early finish date"));

      backwardPass(tasks);
      m_backwardPass = true;

      if (tasks.stream().anyMatch(t -> t.getConstraintType() == ConstraintType.AS_LATE_AS_POSSIBLE))
      {
         forwardPass(tasks);
      }
   }

   private void forwardPass(List<Task> tasks) throws CpmException
   {
      for (Task task : tasks)
      {
         forwardPass(task);
      }
   }

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

      ProjectCalendar calendar = task.getEffectiveCalendar();
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
            switch (task.getConstraintType())
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
                  earlyStart = addLevelingDelay(task,  getNextWorkStart(task, m_projectStartDate));
                  break;
               }
            }
         }
         else
         {
            earlyStart = predecessors.stream().map(r -> calculateEarlyStart(r)).max(Comparator.naturalOrder()).orElseThrow(() -> new CpmException("Missing early start date"));
         }
         earlyStart = getNextWorkStart(task, earlyStart);

         if (task.getConstraintType() != null)
         {
            switch (task.getConstraintType())
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
                     break;
                  }
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

   private void backwardPass(List<Task> forwardPassTasks) throws CpmException
   {
      List<Task> tasks = new ArrayList<>(forwardPassTasks);
      Collections.reverse(tasks);

      for (Task task : tasks)
      {
         backwardPass(task);
      }
   }

   private void backwardPass(Task task) throws CpmException
   {
      // We'll use external tasks as successors when scheduling, but we'll leave their late dates unchanged.
      if (task.getExternalTask() || task.getExternalProject())
      {
         return;
      }

      List<Relation> successors = m_file.getRelations().getRawSuccessors(task).stream().filter(r -> isTask(r.getSuccessorTask()) && r.getSuccessorTask().getActualFinish() == null).collect(Collectors.toList());
      List<Relation> summaryTaskSuccessors = m_summaryTaskSuccessors.get(task);
      if (summaryTaskSuccessors != null)
      {
         successors = new ArrayList<>(successors);
         successors.addAll(summaryTaskSuccessors);
      }

      ProjectCalendar calendar = task.getEffectiveCalendar();
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
               lateFinish = successors.stream().map(r -> calculateLateFinish(r)).min(Comparator.naturalOrder()).orElseThrow(() -> new CpmException("Missing late start date"));
            }
         }

         switch (task.getConstraintType())
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
            task.setLateFinish(task.getLateFinish());
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

   private LocalDateTime calculateEarlyStart(Relation relation)
   {
      ProjectCalendar taskCalendar = relation.getSuccessorTask().getEffectiveCalendar();
      Task predecessor = relation.getPredecessorTask();

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

   private LocalDateTime calculateEarlyStartForFinishStart(Relation relation)
   {
      Task predecessorTask = relation.getPredecessorTask();
      Task successorTask = relation.getSuccessorTask();

      if (predecessorTask.getActualStart() == null)
      {
         // Predecessor not started
         // Successor not started
         // If predecessor is ALAP task and backward pass has been run then use predecessor late finish
         if (predecessorTask.getConstraintType() == ConstraintType.AS_LATE_AS_POSSIBLE && relation.getSuccessorTask().getConstraintType() != ConstraintType.AS_LATE_AS_POSSIBLE && m_backwardPass)
         {
            return addLag(relation, predecessorTask.getLateFinish());
         }
         else
         {
            return addLag(relation, predecessorTask.getEarlyFinish());
         }
      }
      else
      {
         // Predecessor started
         if (predecessorTask.getActualFinish() != null)
         {
            // Predecessor finished
            // Successor not started
            // If predecessor is ALAP task and backward pass has been run then use predecessor late finish
            if (predecessorTask.getConstraintType() == ConstraintType.AS_LATE_AS_POSSIBLE && relation.getSuccessorTask().getConstraintType() != ConstraintType.AS_LATE_AS_POSSIBLE && m_backwardPass)
            {
               return addLag(relation, predecessorTask.getLateFinish());
            }
            else
            {
               return addLag(relation, predecessorTask.getEarlyFinish());
            }
         }
         else
         {
            // Predecessor not finished
            // Successor not started
            // If predecessor is ALAP task and backward pass has been run then use predecessor late finish
            if (predecessorTask.getConstraintType() == ConstraintType.AS_LATE_AS_POSSIBLE && relation.getSuccessorTask().getConstraintType() != ConstraintType.AS_LATE_AS_POSSIBLE && m_backwardPass)
            {
               return addLag(relation, predecessorTask.getLateFinish());
            }
            else
            {
               return addLag(relation, predecessorTask.getEarlyFinish());
            }
         }
      }
   }

   private LocalDateTime calculateEarlyStartForStartStart(Relation relation)
   {
      Task predecessorTask = relation.getPredecessorTask();
      Task successorTask = relation.getSuccessorTask();

      if (predecessorTask.getActualStart() == null)
      {
         // Predecessor not started
         // Successor not started
         return addLag(relation, predecessorTask.getEarlyStart());
      }
      else
      {
         // Predecessor started
         if (predecessorTask.getActualFinish() != null)
         {
            // Predecessor finished
            // Successor not started
            return addLag(relation, predecessorTask.getActualStart());
         }
         else
         {
            // Predecessor not finished
            // Successor not started
            return addLag(relation, predecessorTask.getEarlyStart());
         }
      }
   }

   private LocalDateTime calculateEarlyStartForStartFinish(Relation relation)
   {
      Task predecessorTask = relation.getPredecessorTask();
      Task successorTask = relation.getSuccessorTask();

      if (predecessorTask.getActualStart() == null)
      {
         // Predecessor not started
         // Successor not started
         return addLag(relation, getDateFromFinishAndDuration(relation.getSuccessorTask(),predecessorTask.getEarlyStart()));
      }
      else
      {
         // Predecessor started
         if (predecessorTask.getActualFinish() != null)
         {
            // Predecessor finished
            // Successor not started
            return addLag(relation, getDateFromFinishAndDuration(relation.getSuccessorTask(),predecessorTask.getEarlyStart()));
         }
         else
         {
            // Predecessor not finished
            // Successor not started
            return addLag(relation, getDateFromFinishAndDuration(relation.getSuccessorTask(),predecessorTask.getEarlyStart()));
         }
      }
   }

   private LocalDateTime calculateEarlyStartForFinishFinish(Relation relation)
   {
      // There is an interesting bug in Project 2010, and possibly other versions, where the ES, and EF dates
      // for the predecessor of an FF task are not set correctly. Calculating the project shows the correct dates,
      // but when the file is saved and reopened, the incorrect dates are shown again. Current versions of MS Project (2024?)
      // seem to be unaffected.

      Task predecessorTask = relation.getPredecessorTask();
      Task successorTask = relation.getSuccessorTask();

      if (predecessorTask.getActualStart() == null)
      {
         // Predecessor not started
         // Successor not started
         LocalDateTime earlyStart = getDateFromFinishAndRemainingDuration(relation.getSuccessorTask(), predecessorTask.getEarlyFinish());
         earlyStart = addLag(relation, earlyStart);
         return earlyStart.isBefore(m_projectStartDate) ? m_projectStartDate : earlyStart;
      }
      else
      {
         // Predecessor started
         if (predecessorTask.getActualFinish() != null)
         {
            // Predecessor finished
            // Successor not started
            LocalDateTime earlyStart = getDateFromFinishAndRemainingDuration(relation.getSuccessorTask(), predecessorTask.getActualFinish());
            earlyStart = addLag(relation, earlyStart);
            return earlyStart.isBefore(m_projectStartDate) ? m_projectStartDate : earlyStart;
         }
         else
         {
            // Predecessor not finished
            // Successor not started
            LocalDateTime earlyStart = getDateFromFinishAndRemainingDuration(relation.getSuccessorTask(), predecessorTask.getEarlyFinish());
            earlyStart = addLag(relation, earlyStart);
            return earlyStart.isBefore(m_projectStartDate) ? m_projectStartDate : earlyStart;
         }
      }
   }

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
         else
         {
            // Successor started
            // Successor not finished
            return removeLag(relation, relation.getSuccessorTask().getLateFinish());
         }
      }
      else
      {
         // Predecessor Started
         // Predecessor not finished
         if (successorTask.getActualStart() == null)
         {
            // Successor not started
            return removeLag(relation, relation.getSuccessorTask().getLateFinish());
         }
         else
         {
            // Successor started
            // Successor not finished
            return removeLag(relation, relation.getSuccessorTask().getLateFinish());
         }
      }
   }

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
         else
         {
            // successor started
            // successor not finished
            return removeLag(relation, m_calculatedLateStart.getOrDefault(successorTask, successorTask.getLateStart()));
         }
      }
      else
      {
         // Predecessor Started
         // Predecessor not finished
         if (successorTask.getActualStart() == null)
         {
            // Successor not started
            return removeLag(relation, m_calculatedLateStart.getOrDefault(successorTask, successorTask.getLateStart()));
         }
         else
         {
            // successor started
            // successor not finished
            return removeLag(relation, m_calculatedLateStart.getOrDefault(successorTask, successorTask.getLateStart()));
         }
      }
   }

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
         else
         {
            // successor started
            // successor not finished
            return removeLag(relation, getDateFromStartAndDuration(predecessorTask, successorTask.getLateFinish()));
         }
      }
      else
      {
         // Predecessor Started
         // Predecessor not finished
         if (successorTask.getActualStart() == null)
         {
            // Successor not started
            return removeLag(relation, getDateFromStartAndDuration(predecessorTask, successorTask.getLateFinish()));
         }
         else
         {
            // successor started
            // successor not finished
            return removeLag(relation, getDateFromStartAndDuration(predecessorTask, successorTask.getLateFinish()));
         }
      }
   }

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
         delay = Duration.getInstance(delay.getDuration() , newTimeUnit);
      }

      ProjectCalendar calendar = task.getEffectiveCalendar();
      // ensure we are in a working period
      date = calendar.getNextWorkStart(date);
      return calendar.getDate(date, delay);
   }

   public boolean isTask(Task task)
   {
      return !((task.getSummary() && !task.getExternalProject()) || !task.getActive() || task.getNull());
   }

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
         lag = Duration.getInstance((predecessorDuration.getDuration() * lag.getDuration())/100.0, predecessorDuration.getUnits());
      }

      ProjectCalendar calendar = relation.getSuccessorTask().getEffectiveCalendar();
      return calendar.getDate(date, lag);
   }

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
         lag = Duration.getInstance((predecessorDuration.getDuration() * lag.getDuration())/100.0, predecessorDuration.getUnits());
      }

      ProjectCalendar calendar = relation.getSuccessorTask().getEffectiveCalendar();
      return calendar.getDate(date, lag.negate());
   }

   private void createSummaryTaskRelationships()
   {
      m_file.getRelations().stream().filter(r -> r.getPredecessorTask().getSummary() || r.getSuccessorTask().getSummary()).forEach(r -> createSummaryTaskRelationship(r));
   }

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

   private Task findEarliestSubtask(Task summaryTask)
   {
      return allChildTasks(summaryTask).stream().min(Comparator.comparing(Task::getEarlyStart)).orElse(null);
   }

   private List<Task> allChildTasks(Task summaryTask)
   {
      return allChildTasks(summaryTask, new ArrayList<>());
   }

   private List<Task> allChildTasks(Task summaryTask, List<Task> childTasks)
   {
      childTasks.addAll(summaryTask.getChildTasks().stream().filter(t -> !t.getSummary() && t.getActive()).collect(Collectors.toList()));
      summaryTask.getChildTasks().stream().filter(Task::getSummary).forEach(t -> allChildTasks(t, childTasks));
      return childTasks;
   }

   public List<Task> getSortedTasks()
   {
      return m_sortedTasks;
   }

   private LocalDateTime getDateFromStartAndDuration(Task task, LocalDateTime date)
   {
      if (useTaskEffectiveCalendar(task))
      {
         return task.getEffectiveCalendar().getDate(date, task.getDuration());
      }

      return getDateFromStartAndWork(task, date);
   }

   private LocalDateTime getDateFromStartAndActualDuration(Task task, LocalDateTime date)
   {
      if (useTaskEffectiveCalendar(task))
      {
         return task.getEffectiveCalendar().getDate(date, task.getActualDuration());
      }

      return getDateFromStartAndActualWork(task, date);
   }

   private LocalDateTime getDateFromFinishAndDuration(Task task, LocalDateTime date)
   {
      if (useTaskEffectiveCalendar(task))
      {
         return task.getEffectiveCalendar().getDate(date, task.getDuration().negate());
      }

      return getDateFromFinishAndWork(task, date);
   }

   private LocalDateTime getDateFromFinishAndRemainingDuration(Task task, LocalDateTime date)
   {
      if (useTaskEffectiveCalendar(task))
      {
         return task.getEffectiveCalendar().getDate(date, task.getRemainingDuration().negate());
      }
      return getDateFromFinishAndRemainingWork(task, date);
   }

   private boolean useTaskEffectiveCalendar(Task task)
   {
      return task.getType() == TaskType.FIXED_DURATION || !getResourceAssignmentStream(task).findAny().isPresent();
   }

   private LocalDateTime getDateFromStartAndWork(Task task, LocalDateTime date)
   {
      return getResourceAssignmentStream(task).map(r -> getDateFromWork(r, date, r.getWork())).max(Comparator.naturalOrder()).orElseGet(null);
   }

   private LocalDateTime getDateFromStartAndActualWork(Task task, LocalDateTime date)
   {
      return getResourceAssignmentStream(task).map(r -> getDateFromWork(r, date, r.getActualWork())).max(Comparator.naturalOrder()).orElseGet(null);
   }

   private LocalDateTime getDateFromFinishAndWork(Task task, LocalDateTime date)
   {
      return getResourceAssignmentStream(task).map(r -> getDateFromWork(r, date, r.getWork().negate())).min(Comparator.naturalOrder()).orElseGet(null);
   }

   private LocalDateTime getDateFromFinishAndRemainingWork(Task task, LocalDateTime date)
   {
      return getResourceAssignmentStream(task).map(r -> getDateFromWork(r, date, r.getRemainingWork().negate())).min(Comparator.naturalOrder()).orElseGet(null);
   }

   private LocalDateTime getDateFromWork(ResourceAssignment assignment, LocalDateTime date, Duration work)
   {
      double units = assignment.getUnits().doubleValue();
      if (units != 100.0)
      {
         work = Duration.getInstance((work.getDuration() * 100.0) / units, work.getUnits());
      }

      return assignment.getEffectiveCalendar().getDate(date, work);
   }

   private LocalDateTime getEquivalentPreviousWorkFinish(Task task, LocalDateTime date)
   {
      if (useTaskEffectiveCalendar(task))
      {
         return getEquivalentPreviousWorkFinish(task.getEffectiveCalendar(), date);
      }

      return getResourceAssignmentStream(task).map(r -> getEquivalentPreviousWorkFinish(r.getEffectiveCalendar(), date)).max(Comparator.naturalOrder()).orElse(null);
   }

   private LocalDateTime getEquivalentPreviousWorkFinish(ProjectCalendar calendar, LocalDateTime date)
   {
      LocalDateTime previousWorkFinish = calendar.getPreviousWorkFinish(date);
      if (calendar.getWork(previousWorkFinish, date, TimeUnit.HOURS).getDuration() == 0)
      {
         return previousWorkFinish;
      }
      return date;
   }

   private LocalDateTime getNextWorkStart(Task task, LocalDateTime date)
   {
      if (useTaskEffectiveCalendar(task))
      {
         return  getNextWorkStart(task, task.getEffectiveCalendar(), date);
      }

      return getResourceAssignmentStream(task).map(r -> getNextWorkStart(task, r.getEffectiveCalendar(), date)).min(Comparator.naturalOrder()).orElse(null);
   }

   private LocalDateTime getNextWorkStart(Task task, ProjectCalendar calendar, LocalDateTime date)
   {
      LocalDateTime nextWorkStart = calendar.getNextWorkStart(date);
      if (nextWorkStart.isAfter(date) && task.getMilestone() && calendar.getPreviousWorkFinish(date).isEqual(date))
      {
         // A milestone can sit at  the end of a working period.
         return date;
      }
      return nextWorkStart;
   }

   private Stream<ResourceAssignment> getResourceAssignmentStream(Task task)
   {
      return task.getResourceAssignments().stream().filter(r -> r.getResource() != null && r.getResource().getType() == ResourceType.WORK && r.getUnits().doubleValue() > 0.0);
   }

   private final ProjectFile m_file;
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
