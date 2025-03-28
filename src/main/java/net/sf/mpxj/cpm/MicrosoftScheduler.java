package net.sf.mpxj.cpm;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import net.sf.mpxj.ConstraintType;
import net.sf.mpxj.Duration;
import net.sf.mpxj.ProjectCalendar;
import net.sf.mpxj.ProjectFile;
import net.sf.mpxj.Relation;
import net.sf.mpxj.Task;
import net.sf.mpxj.TaskMode;
import net.sf.mpxj.TimeUnit;

public class MicrosoftScheduler implements Scheduler
{
   public MicrosoftScheduler(ProjectFile file)
   {
      m_file = file;
   }

   public void process(LocalDateTime projectStartDate) throws Exception
   {
      boolean summaryTasksHaveLogic = m_file.getTasks().stream().anyMatch(t -> t.getSummary() && (!t.getPredecessors().isEmpty() || !t.getSuccessors().isEmpty()));
//      if (summaryTasksHaveLogic)
//      {
//         throw new CpmException("Schedule contains summary tasks with predecessors or successors");
//      }

      m_projectStartDate = projectStartDate;
      m_calculatedEarlyStart.clear();
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
                  earlyStart = addLevelingDelay(task,  calendar.getNextWorkStart(m_projectStartDate));
                  break;
               }
            }
         }
         else
         {
            earlyStart = predecessors.stream().map(r -> calculateEarlyStart(r)).max(Comparator.naturalOrder()).orElseThrow(() -> new CpmException("Missing early start date"));
         }
         earlyStart = calendar.getNextWorkStart(earlyStart);

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
         // We'll use external tasks as successors when scheduling, but we'll leave their late dates unchanged.
         if (task.getExternalTask() || task.getExternalProject())
         {
            continue;
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
            LocalDateTime previousWorkFinish = calendar.getPreviousWorkFinish(lateFinish);

            if (calendar.getWork(previousWorkFinish, lateFinish, TimeUnit.HOURS).getDuration() == 0)
            {
               // TODO: this condition may need work for MS Project.
               // In some/many cases it allows late finish to be at the start of the next working day.
               if (!previousWorkFinish.isBefore(lateFinish))
               {
                  lateFinish = previousWorkFinish;
               }
            }
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
   }

   private LocalDateTime calculateEarlyStart(Relation relation)
   {
      ProjectCalendar taskCalendar = relation.getSuccessorTask().getEffectiveCalendar();
      Task predecessor = relation.getPredecessorTask();

      switch (relation.getType())
      {
         case FINISH_START:
         {
            // MS Project only: If predecessor is ALAP task and backward pass has been run then use predecessor late finish
            if (predecessor.getConstraintType() == ConstraintType.AS_LATE_AS_POSSIBLE && relation.getSuccessorTask().getConstraintType() != ConstraintType.AS_LATE_AS_POSSIBLE && m_backwardPass)
            {
               return addLag(relation, predecessor.getLateFinish());
            }
            else
            {
               return addLag(relation, predecessor.getEarlyFinish());
            }
         }

         case START_START:
         {
            return calculateEarlyStartForStartStart(relation);
//            if (predecessor.getActualStart() != null)
//            {
//               return predecessor.getEarlyStart();
//            }
//            return addLag(relation, predecessor.getEarlyStart());
         }

         case FINISH_FINISH:
         {
            // There is an interesting bug in Project 2010, and possibly other versions, where the ES, and EF dates
            // for the predecessor of an FF task are not set correctly. Calculating the project shows the correct dates,
            // but when the file is saved and reopened, the incorrect dates are shown again. Current versions of MS Project (2024?)
            // seem to be unaffected.
            LocalDateTime predecessorEarlyFinish = predecessor.getActualFinish() == null ? predecessor.getEarlyFinish() : predecessor.getActualFinish();
            LocalDateTime earlyStart = getDateFromFinishAndRemainingDuration(relation.getSuccessorTask(), predecessorEarlyFinish);
            earlyStart = addLag(relation, earlyStart);
            if (earlyStart.isBefore(m_projectStartDate))
            {
               earlyStart = m_projectStartDate;
            }
            return earlyStart;
         }

         case START_FINISH:
         {
            return addLag(relation, getDateFromFinishAndDuration(relation.getSuccessorTask(),predecessor.getEarlyStart()));
         }

         default:
         {
            throw new UnsupportedOperationException();
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
         if (successorTask.getActualStart() == null)
         {
            // Successor not started
            return addLag(relation, predecessorTask.getEarlyStart());
         }
         else
         {
            // successor started
            if (successorTask.getActualFinish() == null)
            {
               // successor not finished
               return addLag(relation, predecessorTask.getEarlyStart());
            }
            else
            {
               // successor finished
               return addLag(relation, predecessorTask.getEarlyStart());
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
               return addLag(relation, predecessorTask.getActualStart());
            }
            else
            {
               // successor started
               if (successorTask.getActualFinish() == null)
               {
                  return predecessorTask.getEarlyStart();
               }
               else
               {
                  // successor finished
                  return predecessorTask.getEarlyStart();
               }
            }
         }
         else
         {
            // Predecessor not finished
            if (successorTask.getActualStart() == null)
            {
               // Successor not started
               return addLag(relation, predecessorTask.getEarlyStart());
            }
            else
            {
               // successor started
               if (successorTask.getActualFinish() == null)
               {
                  // successor not finished
                  return addLag(relation, predecessorTask.getEarlyStart());
               }
               else
               {
                  // successor finished
                  return predecessorTask.getEarlyStart();
               }
            }
         }
      }
   }

   private LocalDateTime calculateLateFinish(Relation relation)
   {
      ProjectCalendar taskCalendar = relation.getPredecessorTask().getEffectiveCalendar();
      Task predecessorTask = relation.getPredecessorTask();
      Task successorTask = relation.getSuccessorTask();
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
            lateFinish = removeLag(relation, successorTask.getLateFinish());
            break;
         }

         case START_FINISH:
         {
            lateFinish = getDateFromStartAndDuration(predecessorTask, successorTask.getLateFinish());
            lateFinish = removeLag(relation, lateFinish);
            break;
         }

         default:
         {
            lateFinish = removeLag(relation, m_calculatedLateStart.getOrDefault(successorTask, successorTask.getLateStart()));
            break;
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
            if (successorTask.getActualFinish() == null)
            {
               // successor not finished
               lateFinish = m_projectFinishDate;
            }
            else
            {
               // successor finished
               lateStart = removeLag(relation, calendar.getNextWorkStart(successorTask.getLateStart()));
               lateFinish = getDateFromStartAndDuration(predecessorTask, lateStart);
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
               lateStart = removeLag(relation, calendar.getNextWorkStart(successorTask.getLateStart()));
               lateFinish = getDateFromStartAndDuration(predecessorTask, lateStart);
            }
            else
            {
               // successor started
               if (successorTask.getActualFinish() == null)
               {
                  // successor not finished
                  lateStart = removeLag(relation, calendar.getNextWorkStart(successorTask.getLateStart()));
                  lateFinish = getDateFromStartAndDuration(predecessorTask, lateStart);
               }
               else
               {
                  // successor finished
                  lateStart = removeLag(relation, calendar.getNextWorkStart(successorTask.getLateStart()));
                  lateFinish = getDateFromStartAndDuration(predecessorTask, lateStart);
               }
            }
         }
         else
         {
            // Predecessor not finished
            if (successorTask.getActualStart() == null)
            {
               // Successor not started
               lateStart = removeLag(relation, calendar.getNextWorkStart(successorTask.getLateStart()));
               lateFinish = getDateFromStartAndDuration(predecessorTask, lateStart);
            }
            else
            {
               // successor started
               if (successorTask.getActualFinish() == null)
               {
                  // successor not finished
                  lateFinish = m_projectFinishDate;
               }
               else
               {
                  // successor finished
                  lateStart = removeLag(relation, calendar.getNextWorkStart(successorTask.getLateStart()));
                  lateFinish = getDateFromStartAndDuration(predecessorTask, lateStart);
               }
            }
         }
      }

      return lateFinish;
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

   private Task findLatestSubtask(Task summaryTask)
   {
      return allChildTasks(summaryTask).stream().max(Comparator.comparing(Task::getEarlyFinish)).orElse(null);
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
      return getDate(task, date, task.getDuration());
   }

   private LocalDateTime getDateFromFinishAndDuration(Task task, LocalDateTime date)
   {
      return getDate(task, date, task.getDuration().negate());
   }

   private LocalDateTime getDateFromFinishAndRemainingDuration(Task task, LocalDateTime date)
   {
      return getDate(task, date, task.getRemainingDuration().negate());
   }

   private LocalDateTime getDate(Task task, LocalDateTime date, Duration duration)
   {
      return task.getEffectiveCalendar().getDate(date, duration);
   }

   private final ProjectFile m_file;
   private List<Task> m_sortedTasks;
   private boolean m_backwardPass;
   private LocalDateTime m_projectStartDate;
   private LocalDateTime m_projectFinishDate;
   private final Map<Task, List<Relation>> m_summaryTaskPredecessors = new HashMap<>();
   private final Map<Task, List<Relation>> m_summaryTaskSuccessors = new HashMap<>();

   private final Map<Task, LocalDateTime> m_calculatedEarlyStart = new HashMap<>();
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
