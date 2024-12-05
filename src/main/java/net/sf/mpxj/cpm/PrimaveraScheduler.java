package net.sf.mpxj.cpm;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import net.sf.mpxj.ActivityStatus;
import net.sf.mpxj.ActivityType;
import net.sf.mpxj.Duration;
import net.sf.mpxj.ProjectCalendar;
import net.sf.mpxj.ProjectFile;
import net.sf.mpxj.Relation;
import net.sf.mpxj.Task;
import net.sf.mpxj.TimeUnit;
import net.sf.mpxj.common.LocalTimeHelper;

public class PrimaveraScheduler implements Scheduler
{
   public PrimaveraScheduler(ProjectFile file)
   {
      m_file = file;
   }

   public void process(LocalDateTime projectStartDate) throws Exception
   {
      ///  Out of sequence test
//      for(Relation relation : m_file.getRelations())
//      {
//         if (outOfSequence(relation))
//         {
//            System.out.println("Out of sequence: " + relation);
//         }
//      }

      List<Task> tasks = new DepthFirstGraphSort(m_file).sort();
      if (tasks.isEmpty())
      {
         return;
      }

      forwardPass(projectStartDate, tasks);

      LocalDateTime projectFinishDate = m_file.getProjectProperties().getMustFinishBy();

      if (projectFinishDate== null)
      {
         projectFinishDate = tasks.stream().map(Task::getEarlyFinish).max(Comparator.naturalOrder()).orElseThrow(() -> new CpmException("Missing early finish date"));
      }

      backwardPass(projectFinishDate, tasks);
   }

   private void forwardPass(LocalDateTime projectStartDate, List<Task> tasks) throws CpmException
   {
      for (Task task : tasks)
      {
         ProjectCalendar calendar = task.getEffectiveCalendar();
         LocalDateTime earlyStart;

         LocalDateTime earlyFinish = null;
         List<Relation> predecessors = task.getPredecessors().stream().filter(r -> !ignoreTask(r.getPredecessorTask())).collect(Collectors.toList());
         boolean activityOutOfSequence = predecessors.stream().anyMatch(r -> activityOutOfSequence(r));

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
               earlyStart = predecessors.stream().map(r -> calculateEarlyStart(calendar, projectStartDate, activityOutOfSequence, r)).max(Comparator.naturalOrder()).orElseThrow(() -> new CpmException("Missing early start date"));
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
            if (task.getActualFinish() == null)
            {
               if (predecessors.isEmpty())
               {
                  earlyFinish = calendar.getDate(addLevelingDelay(calendar, task.getActualStart(), task.getLevelingDelay()), task.getDuration());
                  earlyStart = calendar.getDate(earlyFinish, task.getRemainingDuration().negate());
               }
               else
               {
                  earlyStart = calendar.getNextWorkStart(predecessors.stream().map(r -> calculateEarlyStart(calendar, projectStartDate, activityOutOfSequence, r)).max(Comparator.naturalOrder()).orElseThrow(() -> new CpmException("Missing early start date")));
                  earlyFinish = calendar.getDate(earlyStart, task.getRemainingDuration());
               }
            }
            else
            {
               if (activityOutOfSequence)
               {
                  earlyStart = predecessors.stream().map(r -> calculateEarlyStart(calendar, projectStartDate, activityOutOfSequence, r)).max(Comparator.naturalOrder()).orElseThrow(() -> new CpmException("Missing early start date"));
                  earlyFinish = calendar.getDate(earlyStart, task.getRemainingDuration());
               }
               else
               {
                  if (predecessors.isEmpty())
                  {
                     LocalDateTime dataDate = m_file.getProjectProperties().getStatusDate();
                     earlyFinish = dataDate;
                     earlyStart = dataDate;
                  }
                  else
                  {
                     LocalDateTime dataDate = m_file.getProjectProperties().getStatusDate();
                     earlyStart = predecessors.stream().map(r -> calculateEarlyStart(calendar, projectStartDate, activityOutOfSequence, r)).max(Comparator.naturalOrder()).orElseThrow(() -> new CpmException("Missing early start date"));
                     if (earlyStart.isBefore(dataDate))
                     {
                        earlyStart = dataDate;
                     }
                     earlyFinish = earlyStart;
                  }
               }
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

   private void backwardPass(LocalDateTime projectFinishDate, List<Task> forwardPassTasks) throws CpmException
   {
      List<Task> tasks = new ArrayList<>(forwardPassTasks);
      Collections.reverse(tasks);

      for (Task task : tasks)
      {
         List<Relation> successors = m_file.getRelations().getRawSuccessors(task).stream().filter(r -> !ignoreTask(r.getSuccessorTask())).collect(Collectors.toList());
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
                  lateFinish = previousWorkFinish;
               }
            }
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
         }

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

         LocalDateTime lateStart;
         Duration taskDuration = task.getActualStart() == null ? task.getDuration() : task.getRemainingDuration();
         lateStart = calendar.getDate(lateFinish, taskDuration.negate());
         if (task.getActivityType() == ActivityType.START_MILESTONE)
         {
            lateStart = calendar.getNextWorkStart(lateStart);
         }

         task.setLateStart(lateStart);
         task.setLateFinish(lateFinish);
      }
   }

   private LocalDateTime calculateEarlyStart(ProjectCalendar taskCalendar, LocalDateTime projectStartDate, boolean activityOutOfSequence, Relation relation)
   {
      Task predecessor = relation.getPredecessorTask();

      switch (relation.getType())
      {
         case FINISH_START:
         {
            return getLagCalendar(taskCalendar, relation).getDate(predecessor.getEarlyFinish(), relation.getLag());
         }

         case START_START:
         {
            if (predecessor.getActualStart() != null)
            {
               return predecessor.getEarlyStart();
            }
            return getLagCalendar(taskCalendar, relation).getDate(predecessor.getEarlyStart(), relation.getLag());
         }

         case FINISH_FINISH:
         {
            LocalDateTime predecessorEarlyFinish = predecessor.getActualFinish() == null ? predecessor.getEarlyFinish() : predecessor.getActualFinish();
            LocalDateTime earlyStart = taskCalendar.getDate(predecessorEarlyFinish, relation.getSuccessorTask().getRemainingDuration().negate());
            earlyStart = getLagCalendar(taskCalendar, relation).getDate(earlyStart, relation.getLag());
            if (earlyStart.isBefore(projectStartDate))
            {
               earlyStart = projectStartDate;
            }
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
            LocalDateTime lateStart;

            if (successorTask.getActualStart() == null)
            {
               lateStart = taskCalendar.getNextWorkStart(getLagCalendar(taskCalendar, relation).getDate(successorTask.getLateStart(), relation.getLag().negate()));
            }
            else
            {
               lateStart = successorTask.getLateStart();
            }

            lateFinish = taskCalendar.getDate(lateStart, predecessorTask.getRemainingDuration());
            break;
         }

         case FINISH_FINISH:
         {
            if (predecessorTask.getActualFinish() == null)
            {
               lateFinish = getLagCalendar(taskCalendar, relation).getDate(successorTask.getLateFinish(), relation.getLag().negate());
            }
            else
            {
               lateFinish = successorTask.getLateFinish();
            }
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

   private boolean activityOutOfSequence(Relation relation)
   {
      ActivityStatus predecessorStatus = relation.getPredecessorTask().getActivityStatus();
      ActivityStatus successorStatus = relation.getSuccessorTask().getActivityStatus();
      if (predecessorStatus == ActivityStatus.NOT_STARTED && successorStatus == ActivityStatus.NOT_STARTED)
      {
         return false;
      }

      switch (relation.getType())
      {
         case FINISH_START:
         {
            return (predecessorStatus == ActivityStatus.NOT_STARTED || predecessorStatus == ActivityStatus.IN_PROGRESS) && (successorStatus == ActivityStatus.IN_PROGRESS || successorStatus == ActivityStatus.COMPLETED);
         }

         case START_START:
         {
            return predecessorStatus == ActivityStatus.NOT_STARTED && (successorStatus == ActivityStatus.IN_PROGRESS || successorStatus == ActivityStatus.COMPLETED);
         }

         case FINISH_FINISH:
         {
            return (predecessorStatus == ActivityStatus.NOT_STARTED || predecessorStatus == ActivityStatus.IN_PROGRESS) && successorStatus == ActivityStatus.COMPLETED;
         }

         case START_FINISH:
         {
            return predecessorStatus == ActivityStatus.NOT_STARTED && successorStatus == ActivityStatus.COMPLETED;
         }

         default:
         {
            throw new UnsupportedOperationException("Unknown relation type");
         }
      }
   }

   public static boolean ignoreTask(Task task)
   {
      return task.getSummary() || !task.getActive() || task.getNull() || task.getActivityType() == ActivityType.LEVEL_OF_EFFORT || task.getActivityType() == ActivityType.WBS_SUMMARY;
   }

   private final ProjectFile m_file;
}
