package net.sf.mpxj.cpm;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import net.sf.mpxj.ActivityStatus;
import net.sf.mpxj.ActivityType;
import net.sf.mpxj.ConstraintType;
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
      m_projectStartDate = projectStartDate;

      List<Task> tasks = new DepthFirstGraphSort(m_file).sort();
      if (tasks.isEmpty())
      {
         return;
      }

      LocalDateTime dataDate = m_file.getProjectProperties().getStatusDate();
      if (dataDate != null && projectStartDate.isBefore(dataDate))
      {
         m_projectStartDate = dataDate;
      }

      forwardPass(tasks);

      m_projectFinishDate = m_file.getProjectProperties().getMustFinishBy();

      if (m_projectFinishDate== null)
      {
         m_projectFinishDate = tasks.stream().map(Task::getEarlyFinish).max(Comparator.naturalOrder()).orElseThrow(() -> new CpmException("Missing early finish date"));
      }

      backwardPass(tasks);
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
      List<Relation> predecessors = task.getPredecessors().stream().filter(r -> !ignoreTask(r.getPredecessorTask())).collect(Collectors.toList());
      //boolean activityOutOfSequence = predecessors.stream().anyMatch(r -> activityOutOfSequence(r));

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
                  earlyStart = getDate(calendar, earlyFinish, task.getDuration().negate());
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
            earlyStart = predecessors.stream().map(r -> calculateEarlyStart(calendar, r)).max(Comparator.naturalOrder()).orElseThrow(() -> new CpmException("Missing early start date"));
         }

         switch (task.getActivityType())
         {
            case FINISH_MILESTONE:
            {
               // Don't adjust early start
               break;
            }

            case RESOURCE_DEPENDENT:
            {
               throw new UnsupportedOperationException("Resource Dependent Activities not currently supported");
            }

            default:
            {
               // Next work start
               earlyStart = calendar.getNextWorkStart(earlyStart);
               break;
            }
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
                  //                     LocalDateTime latestStart = getDate(calendar, task.getConstraintDate(), task.getDuration().negate());
                  //                     if (earlyStart.isAfter(latestStart))
                  //                     {
                  //                        earlyStart = latestStart;
                  //                     }
                  break;
               }

               case FINISH_NO_EARLIER_THAN:
               {
                  LocalDateTime earliestStart = getDate(calendar, task.getConstraintDate(), task.getDuration().negate());
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
               {
                  earlyFinish = task.getConstraintDate();
                  earlyStart = getDate(calendar, earlyFinish, task.getDuration().negate());
                  break;
               }

               case FINISH_ON:
               {
                  LocalDateTime startOn = getDate(calendar, task.getConstraintDate(), task.getDuration().negate());
                  if (startOn.isAfter(earlyStart))
                  {
                     earlyFinish = task.getConstraintDate();
                     earlyStart = startOn;
                  }
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
               earlyFinish = getDate(calendar, task.getActualStart(), task.getDuration());
               earlyStart = getDate(calendar, earlyFinish, task.getRemainingDuration().negate());
            }
            else
            {
               earlyStart = calendar.getNextWorkStart(predecessors.stream().map(r -> calculateEarlyStart(calendar, r)).max(Comparator.naturalOrder()).orElseThrow(() -> new CpmException("Missing early start date")));
               earlyFinish = getDate(calendar, earlyStart, task.getRemainingDuration());
            }
         }
         else
         {
            if (predecessors.isEmpty())
            {
               LocalDateTime dataDate = m_file.getProjectProperties().getStatusDate();
               earlyStart = dataDate;
               earlyFinish = dataDate;
            }
            else
            {
               earlyStart = predecessors.stream().map(r -> calculateEarlyStart(calendar, r)).max(Comparator.naturalOrder()).orElseThrow(() -> new CpmException("Missing early start date"));
               earlyFinish = getDate(calendar, earlyStart, task.getRemainingDuration());
            }
         }
      }

      if (earlyFinish == null)
      {
         earlyFinish = task.getActualFinish() == null ? getDate(calendar, earlyStart, task.getDuration()) : task.getActualFinish();
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
               lateFinish = m_projectFinishDate;
            }
            else
            {
               lateFinish = successors.stream().map(r -> calculateLateFinish(calendar, r)).min(Comparator.naturalOrder()).orElseThrow(() -> new CpmException("Missing late start date"));
            }

            switch (task.getConstraintType())
            {
               case START_ON:
               {
                  if (task.getActualStart() == null)
                  {
                     lateFinish = getDate(calendar, task.getConstraintDate(), task.getDuration());
                  }
                  break;
               }

               case MUST_START_ON:
               {
                  lateFinish = getDate(calendar, task.getConstraintDate(), task.getDuration());
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
                  LocalDateTime latestFinish = getDate(calendar, task.getConstraintDate(), task.getDuration());
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
            lateFinish = m_projectFinishDate;
         }
         else
         {
            lateFinish = successors.stream().map(r -> calculateLateFinish(calendar, r)).min(Comparator.naturalOrder()).orElseThrow(() -> new CpmException("Missing late start date"));
         }
      }

      // TODO: maybe the same task duration logic as below?
      if (task.getDuration().getDuration() != 0)
      {
         // P6 moves the late finish date to the end of the working period on that day.
         LocalDateTime adjustedLateFinish = LocalTimeHelper.setEndTime(lateFinish, calendar.getFinishTime(lateFinish.toLocalDate()));

         // There is some variability in how P6 represents this, e.g. 16:59 and 17:00 are equivalent
         // Don't adjust the date if they are 1 minute apart to ensure the dates we produce are aligned with P6.
         // Also, there also appears to be an upper limit to how much P6 will push the end date forward.
         long differenceInSeconds = lateFinish.until(adjustedLateFinish, ChronoUnit.SECONDS);
         if (differenceInSeconds > 60 && differenceInSeconds < 180)
         {
            lateFinish = adjustedLateFinish;
         }
      }

      LocalDateTime lateStart;
      Duration taskDuration = task.getActualStart() == null ? task.getDuration() : task.getRemainingDuration();
      lateStart = getDate(calendar, lateFinish, taskDuration.negate());
      if (task.getActivityType() == ActivityType.START_MILESTONE)
      {
         lateStart = calendar.getNextWorkStart(lateStart);
      }
      else
      {
         if (task.getActivityType() != ActivityType.FINISH_MILESTONE && taskDuration.getDuration() != 0)
         {
            LocalDateTime adjustedLateStart = calendar.getNextWorkStart(lateStart);
            Duration work = calendar.getWork(lateStart, adjustedLateStart, TimeUnit.MINUTES);
            if (work.getDuration() == 0)
            {
               lateStart = adjustedLateStart;
            }
         }
      }

      task.setLateStart(lateStart);
      task.setLateFinish(lateFinish);
   }

   private LocalDateTime calculateEarlyStart(ProjectCalendar taskCalendar, Relation relation)
   {
      Task predecessor = relation.getPredecessorTask();

      switch (relation.getType())
      {
         case FINISH_START:
         {
            if (predecessor.getActualFinish() != null)
            {
               return predecessor.getEarlyFinish();
            }
            return getDate(getLagCalendar(taskCalendar, relation), predecessor.getEarlyFinish(), relation.getLag());
         }

         case START_START:
         {
            if (predecessor.getActualStart() != null)
            {
               return predecessor.getEarlyStart();
            }
            return getDate(getLagCalendar(taskCalendar, relation), predecessor.getEarlyStart(), relation.getLag());
         }

         case FINISH_FINISH:
         {
            LocalDateTime predecessorEarlyFinish;

            if (predecessor.getActualFinish() == null)
            {
               predecessorEarlyFinish = predecessor.getEarlyFinish();
            }
            else
            {
               if (predecessor.getActualFinish().isBefore(predecessor.getEarlyFinish()))
               {
                  predecessorEarlyFinish = predecessor.getActualFinish();
               }
               else
               {
                  predecessorEarlyFinish = predecessor.getEarlyFinish();
               }
            }

            LocalDateTime earlyStart = getDate(taskCalendar, predecessorEarlyFinish, relation.getSuccessorTask().getRemainingDuration().negate());
            earlyStart = getDate(getLagCalendar(taskCalendar, relation), earlyStart, relation.getLag());
            if (earlyStart.isBefore(m_projectStartDate))
            {
               earlyStart = m_projectStartDate;
            }
            return earlyStart;
         }

         case START_FINISH:
         {
            return getDate(getLagCalendar(taskCalendar, relation), getDate(taskCalendar, predecessor.getEarlyStart(), relation.getSuccessorTask().getDuration().negate()), relation.getLag());
         }

         default:
         {
            throw new UnsupportedOperationException();
         }
      }
   }

   private LocalDateTime calculateLateFinish(ProjectCalendar taskCalendar, Relation relation)
   {
      Task predecessorTask = relation.getPredecessorTask();
      Task successorTask = relation.getSuccessorTask();
      LocalDateTime lateFinish;

      switch (relation.getType())
      {
         case START_START:
         {
            LocalDateTime lateStart;

            if (successorTask.getActualStart() == null && predecessorTask.getActualStart() == null)
            {
               lateStart = taskCalendar.getNextWorkStart(getDate(getLagCalendar(taskCalendar, relation), successorTask.getLateStart(), relation.getLag().negate()));
            }
            else
            {
               lateStart = successorTask.getLateStart();
            }

            lateFinish = getDate(taskCalendar, lateStart, predecessorTask.getRemainingDuration());
            break;
         }

         case FINISH_FINISH:
         {
            if (predecessorTask.getActualFinish() == null)
            {
               lateFinish = getDate(getLagCalendar(taskCalendar, relation), successorTask.getLateFinish(), relation.getLag().negate());
            }
            else
            {
               lateFinish = successorTask.getLateFinish();
            }
            break;
         }

         case START_FINISH:
         {
            lateFinish = getDate(taskCalendar, successorTask.getLateFinish(), predecessorTask.getDuration());
            lateFinish = getDate(getLagCalendar(taskCalendar, relation), lateFinish, relation.getLag().negate());
            break;
         }

         default:
         {
            if (predecessorTask.getActualStart() != null || successorTask.getActualStart() != null)
            {
               lateFinish = successorTask.getLateStart();
            }
            else
            {
               lateFinish = getDate(getLagCalendar(taskCalendar, relation), successorTask.getLateStart(), relation.getLag().negate());
            }
            break;
         }
      }

      if (lateFinish.isAfter(m_projectFinishDate))
      {
         return m_projectFinishDate;
      }

      return lateFinish;
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

   private LocalDateTime getDate(ProjectCalendar calendar, LocalDateTime date, Duration duration)
   {
      LocalDateTime result = calendar.getDate(date, duration);

      // P6 appears to work to the nearest minute
      if (result.getSecond() != 0)
      {
         boolean roundUp = result.getSecond() >= 30;
         LocalTime newTime = LocalTime.of(result.getHour(), result.getMinute());
         result = LocalDateTime.of(result.toLocalDate(), newTime);
         if (roundUp)
         {
            result = result.plusMinutes(1);
         }
      }
      return result;
   }

   public static boolean ignoreTask(Task task)
   {
      return task.getSummary() || !task.getActive() || task.getNull() || task.getActivityType() == ActivityType.LEVEL_OF_EFFORT || task.getActivityType() == ActivityType.WBS_SUMMARY;
   }

   private final ProjectFile m_file;
   private LocalDateTime m_projectStartDate;
   private LocalDateTime m_projectFinishDate;
}
