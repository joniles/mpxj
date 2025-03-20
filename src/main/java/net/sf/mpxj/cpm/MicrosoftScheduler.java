package net.sf.mpxj.cpm;

import java.sql.Time;
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
      if (m_file.getTasks().stream().anyMatch(t -> t.getSummary() && (!t.getPredecessors().isEmpty() || !t.getSuccessors().isEmpty())))
      {
         throw new CpmException("Schedule contains summary tasks with predecessors or successors");
      }

      m_projectStartDate = projectStartDate;

      List<Task> tasks = new DepthFirstGraphSort(m_file, this::isTask).sort();
      if (tasks.isEmpty())
      {
         return;
      }

      // TODO
      // clearDates();

      m_backwardPass = false;
      forwardPass(tasks);

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
      ProjectCalendar calendar = task.getEffectiveCalendar();
      LocalDateTime earlyStart;

      LocalDateTime earlyFinish = null;
      List<Relation> predecessors = task.getPredecessors().stream().filter(r -> isTask(r.getPredecessorTask())).collect(Collectors.toList());

      if (task.getActualStart() == null)
      {
         if (task.getTaskMode() == TaskMode.MANUALLY_SCHEDULED)
         {
            // TODO: we need to be able to identify where NO start date has been supplied, which appears to trigger using ScheduledStart rather than Start
            task.setEarlyStart(task.getStart());
            task.setEarlyFinish(task.getFinish());
            return;
         }
         else
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
                     earlyStart = calendar.getDate(earlyFinish, task.getDuration().negate());
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
               earlyStart = predecessors.stream().map(r -> calculateEarlyStart(r)).max(Comparator.naturalOrder()).orElseThrow(() -> new CpmException("Missing early start date"));
            }
            earlyStart = calendar.getNextWorkStart(earlyStart);
         }

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
                  LocalDateTime latestStart = calendar.getDate(task.getConstraintDate(), task.getDuration().negate());
                  if (earlyStart.isAfter(latestStart))
                  {
                     earlyStart = latestStart;
                  }
                  break;
               }

               case FINISH_NO_EARLIER_THAN:
               {
                  LocalDateTime earliestStart = calendar.getDate(task.getConstraintDate(), task.getDuration().negate());
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
                  earlyStart = calendar.getDate(earlyFinish, task.getDuration().negate());
                  break;
               }
            }
         }
      }
      else
      {
         earlyStart = task.getActualStart();
      }

      if (earlyFinish == null)
      {
         earlyFinish = task.getActualFinish() == null ? calendar.getDate(addLevelingDelay(calendar, earlyStart, task.getLevelingDelay()), task.getDuration()) : task.getActualFinish();
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
         List<Relation> successors = m_file.getRelations().getRawSuccessors(task).stream().filter(r -> isTask(r.getSuccessorTask())).collect(Collectors.toList());
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
                  lateFinish = calendar.getDate(task.getConstraintDate(), task.getDuration());
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
                  LocalDateTime latestFinish = calendar.getDate(task.getConstraintDate(), task.getDuration());
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

         LocalDateTime lateStart = task.getActualStart() == null ? calendar.getDate(lateFinish, task.getDuration().negate()) : task.getActualStart();

         task.setLateStart(lateStart);
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
            if (predecessor.getActualStart() != null)
            {
               return predecessor.getEarlyStart();
            }
            return addLag(relation, predecessor.getEarlyStart());
         }

         case FINISH_FINISH:
         {
            // There is an interesting bug in Project 2010, and possibly other versions, where the ES, and EF dates
            // for the predecessor of an FF task are not set correctly. Calculating the project shows the correct dates,
            // but when the file is saved and reopened, the incorrect dates are shown again. Current versions of MS Project (2024?)
            // seem to be unaffected.
            LocalDateTime predecessorEarlyFinish = predecessor.getActualFinish() == null ? predecessor.getEarlyFinish() : predecessor.getActualFinish();
            LocalDateTime earlyStart = taskCalendar.getDate(predecessorEarlyFinish, relation.getSuccessorTask().getRemainingDuration().negate());
            earlyStart = addLag(relation, earlyStart);
            if (earlyStart.isBefore(m_projectStartDate))
            {
               earlyStart = m_projectStartDate;
            }
            return earlyStart;
         }

         case START_FINISH:
         {
            return addLag(relation, taskCalendar.getDate(predecessor.getEarlyStart(), relation.getSuccessorTask().getDuration().negate()));
         }

         default:
         {
            throw new UnsupportedOperationException();
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
            lateFinish = taskCalendar.getDate(successorTask.getLateFinish(), predecessorTask.getDuration());
            lateFinish = removeLag(relation, lateFinish);
            break;
         }

         default:
         {
            lateFinish = removeLag(relation, successorTask.getLateStart());
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
            lateStart = removeLag(relation, calendar.getNextWorkStart(successorTask.getLateStart()));
            lateFinish = calendar.getDate(lateStart, predecessorTask.getDuration());
         }
         else
         {
            // successor started
            if (successorTask.getActualFinish() == null)
            {
               // successor not finished
               lateStart = removeLag(relation, calendar.getNextWorkStart(successorTask.getLateStart()));
               lateFinish = calendar.getDate(lateStart, predecessorTask.getDuration());
            }
            else
            {
               // successor finished
               lateStart = removeLag(relation, calendar.getNextWorkStart(successorTask.getLateStart()));
               lateFinish = calendar.getDate(lateStart, predecessorTask.getDuration());
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
               lateFinish = calendar.getDate(lateStart, predecessorTask.getDuration());
            }
            else
            {
               // successor started
               if (successorTask.getActualFinish() == null)
               {
                  // successor not finished
                  lateStart = removeLag(relation, calendar.getNextWorkStart(successorTask.getLateStart()));
                  lateFinish = calendar.getDate(lateStart, predecessorTask.getDuration());
               }
               else
               {
                  // successor finished
                  lateStart = removeLag(relation, calendar.getNextWorkStart(successorTask.getLateStart()));
                  lateFinish = calendar.getDate(lateStart, predecessorTask.getDuration());
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
               lateFinish = calendar.getDate(lateStart, predecessorTask.getDuration());
            }
            else
            {
               // successor started
               if (successorTask.getActualFinish() == null)
               {
                  // successor not finished
                  lateStart = removeLag(relation, calendar.getNextWorkStart(successorTask.getLateStart()));
                  lateFinish = calendar.getDate(lateStart, predecessorTask.getDuration());
               }
               else
               {
                  // successor finished
                  lateStart = removeLag(relation, calendar.getNextWorkStart(successorTask.getLateStart()));
                  lateFinish = calendar.getDate(lateStart, predecessorTask.getDuration());
               }
            }
         }
      }

      return lateFinish;
   }

   private LocalDateTime addLevelingDelay(ProjectCalendar calendar, LocalDateTime date, Duration delay)
   {
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

      return calendar.getDate(date, delay);
   }

   public boolean isTask(Task task)
   {
      return !(task.getSummary() || !task.getActive() || task.getNull());
   }

   private LocalDateTime addLag(Relation relation, LocalDateTime date)
   {
      ProjectCalendar calendar = relation.getSuccessorTask().getEffectiveCalendar();
      return calendar.getDate(date, relation.getLag());
   }

   private LocalDateTime removeLag(Relation relation, LocalDateTime date)
   {
      ProjectCalendar calendar = relation.getPredecessorTask().getEffectiveCalendar();
      return calendar.getDate(date, relation.getLag().negate());
   }

   private final ProjectFile m_file;
   private boolean m_backwardPass;
   private LocalDateTime m_projectStartDate;
   private LocalDateTime m_projectFinishDate;

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
