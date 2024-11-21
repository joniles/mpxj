package net.sf.mpxj.cpm;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import net.sf.mpxj.ConstraintType;
import net.sf.mpxj.Duration;
import net.sf.mpxj.ProjectCalendar;
import net.sf.mpxj.ProjectFile;
import net.sf.mpxj.Relation;
import net.sf.mpxj.Task;
import net.sf.mpxj.TaskMode;
import net.sf.mpxj.TimeUnit;
import net.sf.mpxj.common.LocalTimeHelper;

public class Schedule
{
   public Schedule(ScheduleStrategy strategy, ProjectFile file)
   {
      m_strategy = strategy;
      m_file = file;
   }

   public void process(LocalDateTime projectStartDate) throws Exception
   {
      List<Task> tasks = new DepthFirstGraphSort(m_file).sort();
      if (tasks.isEmpty())
      {
         return;
      }

      m_backwardPass = false;
      forwardPass(projectStartDate, tasks);

      LocalDateTime projectFinishDate = null;
      if (m_strategy == ScheduleStrategy.P6)
      {
         projectFinishDate = m_file.getProjectProperties().getMustFinishBy();
      }

      if (projectFinishDate== null)
      {
         projectFinishDate = tasks.stream().map(Task::getEarlyFinish).max(Comparator.naturalOrder()).orElseThrow(() -> new CpmException("Missing early finish date"));
      }

      backwardPass(projectFinishDate, tasks);
      m_backwardPass = true;

      if (tasks.stream().anyMatch(t -> t.getConstraintType() == ConstraintType.AS_LATE_AS_POSSIBLE))
      {
         forwardPass(projectStartDate, tasks);
      }
   }

   private void forwardPass(LocalDateTime projectStartDate, List<Task> tasks) throws CpmException
   {
      for (Task task : tasks)
      {
         ProjectCalendar calendar = task.getEffectiveCalendar();
         LocalDateTime earlyStart;

         LocalDateTime earlyFinish = null;
         if (task.getActualStart() == null)
         {
            if (task.getTaskMode() == TaskMode.MANUALLY_SCHEDULED)
            {
               // TODO: we need to be able to identify where NO start date has been supplied, which appears to trigger using ScheduledStart rather than Start
               task.setEarlyStart(task.getStart());
               task.setEarlyFinish(task.getFinish());
               continue;
            }
            else
            {
               List<Relation> predecessors = task.getPredecessors().stream().filter(r -> r.getPredecessorTask().getActive()).collect(Collectors.toList());
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
                        earlyStart = projectStartDate;
                        break;
                     }
                  }
               }
               else
               {
                  earlyStart = predecessors.stream().map(r -> calculateEarlyStart(calendar, r)).max(Comparator.naturalOrder()).orElseThrow(() -> new CpmException("Missing early start date"));
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
                  {
                     earlyStart = task.getConstraintDate();
                     break;
                  }

                  case MUST_FINISH_ON:
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
            if (m_strategy == ScheduleStrategy.P6)
            {
               LocalDateTime dataDate = m_file.getProjectProperties().getStatusDate();
               earlyFinish = task.getActualFinish() == null ? calendar.getDate(addLevelingDelay(calendar, task.getActualStart(), task.getLevelingDelay()), task.getDuration()) : dataDate;
               earlyStart = dataDate;
            }
            else
            {
               earlyStart = task.getActualStart();
            }
         }

         if (earlyFinish == null)
         {
            earlyFinish = task.getActualFinish() == null ? calendar.getDate(addLevelingDelay(calendar, earlyStart, task.getLevelingDelay()), task.getDuration()) : task.getActualFinish();
         }

         task.setEarlyStart(earlyStart);
         task.setEarlyFinish(earlyFinish);
      }
   }

   private void backwardPass(LocalDateTime projectFinishDate, List<Task> x) throws CpmException
   {
      List<Task> tasks = new ArrayList<>(x);
      Collections.reverse(tasks);

      for (Task task : tasks)
      {
         List<Relation> successors = m_file.getRelations().getRawSuccessors(task).stream().filter(r -> r.getSuccessorTask().getActive()).collect(Collectors.toList());
         ProjectCalendar calendar = task.getEffectiveCalendar();
         LocalDateTime lateFinish;


         if (task.getActualFinish() == null)
         {
            // Special case: if we have a milestone with only an actual start set, actual start (and hence late finish) must have the same date
            if (task.getMilestone() && task.getActualStart() != null)
            {
               lateFinish = task.getActualStart();
            }
            else
            {
               if (successors.isEmpty())
               {
                  lateFinish = projectFinishDate;
               }
               else
               {
                  lateFinish = successors.stream().map(r -> calculateLateFinish(calendar, projectFinishDate, r)).min(Comparator.naturalOrder()).orElseThrow(() -> new CpmException("Missing late start date"));
               }

               switch (task.getConstraintType())
               {
                  case MUST_START_ON:
                  {
                     lateFinish = calendar.getDate(task.getConstraintDate(), task.getDuration());
                     break;
                  }

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
                  // TODO: this condition may need work, particularly for MS Project.
                  // In some/many cases it allows late finish to be at the start of the next working day.
                  if (m_strategy == ScheduleStrategy.P6 || !previousWorkFinish.isBefore(lateFinish))
                  {
                     lateFinish = previousWorkFinish;
                  }
               }
            }
         }
         else
         {
            if (m_strategy == ScheduleStrategy.P6)
            {
               if (successors.isEmpty())
               {
                  lateFinish = m_file.getProjectProperties().getStatusDate();
               }
               else
               {
                  lateFinish = successors.stream().map(r -> calculateLateFinish(calendar, projectFinishDate, r)).min(Comparator.naturalOrder()).orElseThrow(() -> new CpmException("Missing late start date"));
               }
            }
            else
            {
               lateFinish = task.getActualFinish();
            }
         }

         if (m_strategy == ScheduleStrategy.P6)
         {
            // P6 moves the late finish date to the end of the working period on that day.
            LocalDateTime adjustedLateFinish = LocalTimeHelper.setEndTime(lateFinish, calendar.getFinishTime(lateFinish.toLocalDate()));

            // There is some variability in how P6 represents this, e.g. 16:59 and 17:00 are equivalent
            // Don't adjust the date if they are 1 minute apart to ensure the dates we produce are aligned with P6.
            // Also, there may be an upper limit to how much P6 will push the end date forward,
            // heer we're assuming no more than 4 hours (14400 seconds).
            long differenceInSeconds = lateFinish.until(adjustedLateFinish, ChronoUnit.SECONDS);
            if (differenceInSeconds > 60 && differenceInSeconds < 14400)
            {
               lateFinish = adjustedLateFinish;
            }
         }

         LocalDateTime lateStart;
         if (m_strategy == ScheduleStrategy.P6)
         {
            Duration taskDuration = task.getActualStart() == null ? task.getDuration() : task.getRemainingDuration();
            lateStart = calendar.getDate(lateFinish, taskDuration.negate());
         }
         else
         {
            lateStart = task.getActualStart() == null ? calendar.getDate(lateFinish, task.getDuration().negate()) : task.getActualStart();
         }

         task.setLateStart(lateStart);
         task.setLateFinish(lateFinish);
      }
   }

   private LocalDateTime calculateEarlyStart(ProjectCalendar taskCalendar, Relation relation)
   {
      Task predecessor = relation.getPredecessorTask();

      switch (relation.getType())
      {
         case FINISH_START:
         {
            // MS Project only: If predecessor is ALAP task and backward pass has been run then use predecessor late finish
            if (m_strategy == ScheduleStrategy.MICROSOFT_PROJECT && predecessor.getConstraintType() == ConstraintType.AS_LATE_AS_POSSIBLE && relation.getSuccessorTask().getConstraintType() != ConstraintType.AS_LATE_AS_POSSIBLE && m_backwardPass)
            {
               return getLagCalendar(taskCalendar, relation).getDate(predecessor.getLateFinish(), relation.getLag());
            }
            else
            {
               return getLagCalendar(taskCalendar, relation).getDate(predecessor.getEarlyFinish(), relation.getLag());
            }
         }

         case START_START:
         {
            return getLagCalendar(taskCalendar, relation).getDate(predecessor.getEarlyStart(), relation.getLag());
         }

         case FINISH_FINISH:
         {
            LocalDateTime earlyStart = taskCalendar.getDate(predecessor.getEarlyFinish(), relation.getSuccessorTask().getDuration().negate());

            earlyStart = getLagCalendar(taskCalendar, relation).getDate(earlyStart, relation.getLag());

//            if (earlyStart.isBefore(task.getEarlyStart()))
//            {
//               earlyStart = task.getEarlyStart();
//            }
            return earlyStart;
         }

         case START_FINISH:
         {
            return getLagCalendar(taskCalendar, relation).getDate(taskCalendar.getDate(predecessor.getEarlyStart(), relation.getSuccessorTask().getDuration().negate()), relation.getLag());
         }

         default:
         {
            throw new UnsupportedOperationException();
         }
      }
   }

   private LocalDateTime calculateLateFinish(ProjectCalendar taskCalendar, LocalDateTime projectFinishDate, Relation relation)
   {
      Task predecessorTask = relation.getPredecessorTask();
      Task successorTask = relation.getSuccessorTask();
      LocalDateTime lateFinish;

      switch (relation.getType())
      {
         case START_START:
         {
            if (m_strategy == ScheduleStrategy.P6)
            {
               LocalDateTime lateStart = taskCalendar.getNextWorkStart(getLagCalendar(taskCalendar, relation).getDate(successorTask.getLateStart(), relation.getLag().negate()));
               lateFinish = taskCalendar.getDate(lateStart, predecessorTask.getDuration());
            }
            else
            {
               lateFinish = projectFinishDate;
            }
            break;
         }

         case FINISH_FINISH:
         {
            //lateFinish = calendar.getDate(projectFinishDate, relation.getLag().negate());
            lateFinish = getLagCalendar(taskCalendar, relation).getDate(successorTask.getLateFinish(), relation.getLag().negate());
            break;
         }

         case START_FINISH:
         {
            lateFinish = taskCalendar.getDate(successorTask.getLateFinish(), predecessorTask.getDuration());
            lateFinish = getLagCalendar(taskCalendar, relation).getDate(lateFinish, relation.getLag().negate());
            break;
         }

         default:
         {
            lateFinish = getLagCalendar(taskCalendar, relation).getDate(successorTask.getLateStart(), relation.getLag().negate());
            break;
         }
      }

      if (lateFinish.isAfter(projectFinishDate))
      {
         return projectFinishDate;
      }

//      if (lateFinish.isBefore(predecessorTask.getEarlyFinish()))
//      {
//         return predecessorTask.getEarlyFinish();
//      }

      return lateFinish;
   }

   private LocalDateTime addLevelingDelay(ProjectCalendar calendar, LocalDateTime date, Duration delay)
   {
      if (delay == null || delay.getDuration() == 0)
      {
         return date;
      }

      // Original duration
      double duration = delay.getDuration();

      // Convert to minutes
      switch (delay.getUnits())
      {
         case HOURS:
         case ELAPSED_HOURS:
         {
            duration = duration * 60.0;
            break;
         }

         case DAYS:
         case ELAPSED_DAYS:
         {
            duration = duration * 1440.0;
            break;
         }

         case WEEKS:
         case ELAPSED_WEEKS:
         {
            duration = duration * 1440.0 * 7.0;
            break;
         }

         case MONTHS:
         case ELAPSED_MONTHS:
         {
            duration = duration * 1440.0 * 30;
            break;
         }

         case YEARS:
         case ELAPSED_YEARS:
         {
            duration = duration * 1440.0 * 365.0;
            break;
         }

         default:
         {
            throw new UnsupportedOperationException("Unsupported TimeUnit " + delay.getUnits());
         }
      }

      return date.plusMinutes((long)duration);
   }

   private ProjectCalendar getLagCalendar(ProjectCalendar taskCalendar, Relation relation)
   {
      if (m_strategy == ScheduleStrategy.MICROSOFT_PROJECT)
      {
         return taskCalendar;
      }

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
            throw new UnsupportedOperationException("TODO: implemenet standard 24 hour calendar");
         }
      }
   }

   private final ScheduleStrategy m_strategy;
   private final ProjectFile m_file;
   private boolean m_backwardPass;
}
