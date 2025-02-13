package net.sf.mpxj.cpm;

import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import net.sf.mpxj.ActivityType;
import net.sf.mpxj.ConstraintType;
import net.sf.mpxj.DayType;
import net.sf.mpxj.Duration;
import net.sf.mpxj.LocalTimeRange;
import net.sf.mpxj.ProjectCalendar;
import net.sf.mpxj.ProjectCalendarHours;
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
      m_dataDate = file.getProjectProperties().getStatusDate();
      m_twentyFourHourCalendar = createTwentyFourHourCalendar();
   }

   public void process(LocalDateTime projectStartDate) throws Exception
   {
      m_projectStartDate = projectStartDate;

      List<Task> tasks = new DepthFirstGraphSort(m_file, this::ignoreTask).sort();
      if (tasks.isEmpty())
      {
         return;
      }

      if (m_dataDate != null && projectStartDate.isBefore(m_dataDate))
      {
         m_projectStartDate = m_dataDate;
      }

      forwardPass(tasks);

      LocalDateTime mustFinishBy = m_file.getProjectProperties().getMustFinishBy();
      LocalDateTime earlyFinish = tasks.stream().map(Task::getEarlyFinish).max(Comparator.naturalOrder()).orElseThrow(() -> new CpmException("Missing early finish date"));

      if (mustFinishBy == null || earlyFinish.isAfter(mustFinishBy))
      {
         m_projectFinishDate = earlyFinish;
      }
      else
      {
         m_projectFinishDate = mustFinishBy;
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
      LocalDateTime earlyStart;

      LocalDateTime earlyFinish = null;
      List<Relation> predecessors = task.getPredecessors().stream().filter(r -> !ignoreTask(r.getPredecessorTask())).collect(Collectors.toList());

      if (task.getActualStart() == null)
      {
         if (predecessors.isEmpty())
         {
            switch (task.getConstraintType())
            {
               case START_NO_EARLIER_THAN:
               {
                  earlyStart = task.getConstraintDate();
                  if (m_dataDate != null && earlyStart.isBefore(m_dataDate))
                  {
                     earlyStart = m_dataDate;
                  }
                  break;
               }

               case FINISH_NO_EARLIER_THAN:
               {
                  earlyFinish = task.getConstraintDate();
                  earlyStart = getDateFromEnd(task, earlyFinish);
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

         switch (task.getConstraintType())
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
                     if (adjustedConstraintDate.toLocalDate().isEqual(task.getEffectiveCalendar().getNextWorkStart(adjustedConstraintDate).toLocalDate()))
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
               LocalDateTime earliestStart = getDateFromEnd(task, task.getConstraintDate());
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
                  earlyStart = task.getEffectiveCalendar().getNextWorkStart(task.getConstraintDate());
               }
               break;
            }

            case MUST_FINISH_ON:
            {
               earlyFinish = task.getConstraintDate();
               earlyStart = getDateFromEnd(task, earlyFinish);
               break;
            }

            case FINISH_ON:
            {
               LocalDateTime startOn = getDateFromEnd(task, task.getConstraintDate());
               if (startOn.isAfter(earlyStart))
               {
                  earlyFinish = task.getConstraintDate();
                  earlyStart = startOn;
               }
               break;
            }
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
               earlyStart = task.getEffectiveCalendar().getNextWorkStart(earlyStart);
               break;
            }
         }
      }
      else
      {
         if (task.getActualFinish() == null)
         {
            if (predecessors.isEmpty())
            {
               earlyFinish = getDateFromStart(task, task.getActualStart());
               earlyStart = getDateFromEndAndRemainingDuration(task, earlyFinish);
            }
            else
            {
               earlyStart = task.getEffectiveCalendar().getNextWorkStart(predecessors.stream().map(r -> calculateEarlyStart(r)).max(Comparator.naturalOrder()).orElseThrow(() -> new CpmException("Missing early start date")));
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
               earlyStart = predecessors.stream().map(r -> calculateEarlyStart(r)).max(Comparator.naturalOrder()).orElseThrow(() -> new CpmException("Missing early start date"));
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
         earlyFinish = task.getActualFinish() == null ? getDateFromStart(task, earlyStart) : task.getActualFinish();
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
            lateFinish = successors.stream().map(r -> calculateLateFinish(r)).min(Comparator.naturalOrder()).orElseThrow(() -> new CpmException("Missing late start date"));
         }

         switch (task.getConstraintType())
         {
            case START_ON:
            {
               if (task.getActualStart() == null)
               {
                  LocalDateTime latestFinish = getDateFromStart(task, task.getConstraintDate());
                  if (lateFinish.isAfter(latestFinish))
                  {
                     lateFinish = latestFinish;
                  }
               }
               break;
            }

            case MUST_START_ON:
            {
               lateFinish = getDateFromStart(task, task.getConstraintDate());
               break;
            }

            case FINISH_ON:
            {
               if (lateFinish.isAfter(task.getConstraintDate()))
               {
                  lateFinish = task.getConstraintDate();
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
               LocalDateTime latestFinish = getDateFromStart(task, task.getConstraintDate());
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

         if (task.getSecondaryConstraintType() != null)
         {
            switch (task.getSecondaryConstraintType())
            {
               case START_NO_LATER_THAN:
               {
                  LocalDateTime latestFinish = getDateFromStart(task, task.getSecondaryConstraintDate());
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
            }
         }

         // If we are at the start of the next period of work, we can move back to the end of the previous period of work
         LocalDateTime previousWorkFinish = calendar.getPreviousWorkFinish(lateFinish);

         if (calendar.getWork(previousWorkFinish, lateFinish, TimeUnit.HOURS).getDuration() == 0)
         {
            lateFinish = previousWorkFinish;
         }
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
            lateFinish = successors.stream().map(r -> calculateLateFinish(r)).min(Comparator.naturalOrder()).orElseThrow(() -> new CpmException("Missing late start date"));
         }
      }

      if (task.getExternalLateFinish() != null && task.getExternalLateFinish().isBefore(lateFinish))
      {
         lateFinish = task.getExternalLateFinish();
      }

      LocalDateTime lateStart = getDateFromEndAndRemainingDuration(task, lateFinish);
      if (task.getActivityType() == ActivityType.START_MILESTONE)
      {
         lateStart = calendar.getNextWorkStart(lateStart);
      }
      else
      {
         if (task.getActivityType() != ActivityType.FINISH_MILESTONE && task.getRemainingDuration().getDuration() != 0)
         {
            lateStart = calendar.getNextWorkStart(lateStart);
         }
      }

      task.setLateStart(lateStart);
      task.setLateFinish(lateFinish);

      if (task.getConstraintType() == ConstraintType.AS_LATE_AS_POSSIBLE)
      {
         alapAdjust(task);
      }
   }

   private LocalDateTime calculateEarlyStart(Relation relation)
   {
      Task predecessorTask = relation.getPredecessorTask();
      Task successorTask = relation.getSuccessorTask();

      switch (relation.getType())
      {
         case FINISH_START:
         {
            if (predecessorTask.getActualStart() == null)
            {
               // Predecessor not started
               if (successorTask.getActualStart() == null)
               {
                  // Successor not started
                  if (relation.getLag().getDuration() == 0)
                  {
                     return addLag(relation, predecessorTask.getEarlyFinish());
                  }

                  return addLag(relation, predecessorTask.getEarlyFinish());
               }
               else
               {
                  // successor started
                  if (successorTask.getActualFinish() == null)
                  {
                     // successor not finished
                     if (relation.getLag().getDuration() == 0)
                     {
                        return addLag(relation, predecessorTask.getEarlyFinish());
                     }

                     return addLag(relation, predecessorTask.getEarlyFinish());
                  }
                  else
                  {
                     // successor finished
                     if (relation.getLag().getDuration() == 0)
                     {
                        return addLag(relation, predecessorTask.getEarlyFinish());
                     }

                     return addLag(relation, predecessorTask.getEarlyFinish());
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
                     double actualLagDurationInHours = predecessorTask.getActualFinish().isAfter(m_dataDate) ? 0 : getLagCalendar(relation).getWork(predecessorTask.getActualFinish(), m_dataDate, TimeUnit.HOURS).getDuration();
                     double lagDurationInHours = relation.getLag().convertUnits(TimeUnit.HOURS, m_file.getProjectProperties()).getDuration();

                     if (lagDurationInHours > actualLagDurationInHours)
                     {
                        Duration remainingLag = Duration.getInstance(lagDurationInHours - actualLagDurationInHours, TimeUnit.HOURS);
                        return addLag(relation, predecessorTask.getEarlyFinish(), remainingLag);
                     }

                     return predecessorTask.getEarlyFinish();
                  }
                  else
                  {
                     // successor started
                     if (successorTask.getActualFinish() == null)
                     {
                        // successor not finished
                        if (relation.getLag().getDuration() == 0)
                        {
                           return predecessorTask.getEarlyFinish();
                        }
                        return predecessorTask.getEarlyFinish();
                     }
                     else
                     {
                        // successor finished
                        if (relation.getLag().getDuration() == 0)
                        {
                           return predecessorTask.getEarlyFinish();
                        }
                        return predecessorTask.getEarlyFinish();
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
                        return addLag(relation, predecessorTask.getEarlyFinish());
                     }
                     return addLag(relation, predecessorTask.getEarlyFinish());
                  }
                  else
                  {
                     // successor started
                     if (successorTask.getActualFinish() == null)
                     {
                        // successor not finished
                        if (relation.getLag().getDuration() == 0)
                        {
                           return addLag(relation, predecessorTask.getEarlyFinish());
                        }

                        return addLag(relation, predecessorTask.getEarlyFinish());
                     }
                     else
                     {
                        // successor finished
                        if (relation.getLag().getDuration() == 0)
                        {
                           return addLag(relation, predecessorTask.getEarlyFinish());
                        }

                        return addLag(relation, predecessorTask.getEarlyFinish());
                     }
                  }
               }
            }
         }

         case START_START:
         {
            if (predecessorTask.getActualStart() == null)
            {
               // Predecessor not started
               if (successorTask.getActualStart() == null)
               {
                  // Successor not started
                  if (relation.getLag().getDuration() != 0)
                  {
                     return addLag(relation, predecessorTask.getEarlyStart());
                  }

                  // why adjust the next work start with the lag calendar? not sure, but it seems to work ;-)
                  return getLagCalendar(relation).getNextWorkStart(predecessorTask.getEarlyStart());
               }
               else
               {
                  // successor started
                  if (successorTask.getActualFinish() == null)
                  {
                     // successor not finished
                     if (relation.getLag().getDuration() == 0)
                     {
                        return addLag(relation, predecessorTask.getEarlyStart());
                     }

                     return addLag(relation, predecessorTask.getEarlyStart());
                  }
                  else
                  {
                     // successor finished
                     if (relation.getLag().getDuration() == 0)
                     {
                        return addLag(relation, predecessorTask.getEarlyStart());
                     }

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
                     if (relation.getLag().getDuration() == 0)
                     {
                        LocalDateTime earlyStart = addLag(relation, predecessorTask.getActualStart());
                        if (earlyStart.isBefore(m_dataDate))
                        {
                           return predecessorTask.getEarlyStart();
                        }
                        return earlyStart;
                     }

                     LocalDateTime earlyStart = addLag(relation, predecessorTask.getActualStart());
                     if (earlyStart.isBefore(m_dataDate))
                     {
                        return predecessorTask.getEarlyStart();
                     }
                     return earlyStart;
                  }
                  else
                  {
                     // successor started
                     if (successorTask.getActualFinish() == null)
                     {
                        // successor not finished
                        if (relation.getLag().getDuration() == 0)
                        {
                           return predecessorTask.getEarlyStart();
                        }
                        return predecessorTask.getEarlyStart();
                     }
                     else
                     {
                        // successor finished
                        if (relation.getLag().getDuration() == 0)
                        {
                           return predecessorTask.getEarlyStart();
                        }
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
                  else
                  {
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
                     else
                     {
                        // successor finished
                        if (relation.getLag().getDuration() == 0)
                        {
                           return predecessorTask.getEarlyStart();
                        }
                        return predecessorTask.getEarlyStart();
                     }
                  }
               }
            }
         }

         case FINISH_FINISH:
         {
            LocalDateTime predecessorEarlyFinish;

            if (predecessorTask.getActualStart() == null)
            {
               // Predecessor not started
               if (successorTask.getActualStart() == null)
               {
                  // Successor not started
                  predecessorEarlyFinish = predecessorTask.getEarlyFinish();
               }
               else
               {
                  // successor started
                  if (successorTask.getActualFinish() == null)
                  {
                     // successor not finished
                     predecessorEarlyFinish = predecessorTask.getEarlyFinish();
                  }
                  else
                  {
                     // successor finished
                     predecessorEarlyFinish = predecessorTask.getEarlyFinish();
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
                     if (relation.getLag().getDuration() == 0.0)
                     {
                        predecessorEarlyFinish = predecessorTask.getActualFinish();
                     }
                     else
                     {
                        double lagDurationInHours = relation.getLag().convertUnits(TimeUnit.HOURS, m_file.getProjectProperties()).getDuration();
                        double actualDurationInHours = predecessorTask.getActualDuration().convertUnits(TimeUnit.HOURS, m_file.getProjectProperties()).getDuration();
                        if (lagDurationInHours > actualDurationInHours)
                        {
                           predecessorEarlyFinish = predecessorTask.getActualFinish();
                        }
                        else
                        {
                           predecessorEarlyFinish = addLag(relation, predecessorTask.getEarlyFinish());
                        }
                     }
                  }
                  else
                  {
                     // successor started
                     if (successorTask.getActualFinish() == null)
                     {
                        // successor not finished
                        predecessorEarlyFinish = predecessorTask.getActualFinish();
                     }
                     else
                     {
                        // successor finished
                        return m_dataDate; // return directly as we don't need to adjust further
                     }
                  }
               }
               else
               {
                  // Predecessor not finished
                  if (successorTask.getActualStart() == null)
                  {
                     // Successor not started
                     predecessorEarlyFinish = predecessorTask.getEarlyFinish();
                  }
                  else
                  {
                     // successor started
                     if (successorTask.getActualFinish() == null)
                     {
                        // successor not finished
                        predecessorEarlyFinish = predecessorTask.getEarlyFinish();
                     }
                     else
                     {
                        // successor finished
                        predecessorEarlyFinish = predecessorTask.getEarlyFinish();
                     }
                  }
               }
            }

            LocalDateTime earlyFinish = addLag(relation, predecessorEarlyFinish);
            LocalDateTime earlyStart = getDateFromEndAndRemainingDuration(successorTask, earlyFinish);
            if (earlyStart.isBefore(m_projectStartDate))
            {
               earlyStart = m_projectStartDate;
            }
            return earlyStart;
         }

         case START_FINISH:
         {
            LocalDateTime earlyStart;

            if (predecessorTask.getActualStart() == null)
            {
               // Predecessor not started
               if (successorTask.getActualStart() == null)
               {
                  // Successor not started
                  earlyStart = addLag(relation, getDateFromEnd(successorTask, predecessorTask.getEarlyStart()));
               }
               else
               {
                  // successor started
                  if (successorTask.getActualFinish() == null)
                  {
                     // successor not finished
                     earlyStart = addLag(relation, getDateFromEnd(successorTask, predecessorTask.getEarlyStart()));
                  }
                  else
                  {
                     // successor finished
                     earlyStart = predecessorTask.getEarlyStart();
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
                     earlyStart = addLag(relation, getDateFromEnd(successorTask, predecessorTask.getEarlyStart()));
                  }
                  else
                  {
                     // successor started
                     if (successorTask.getActualFinish() == null)
                     {
                        // successor not finished
                        double actualLagDurationInHours = predecessorTask.getActualFinish().isAfter(m_dataDate) ? 0 : getLagCalendar(relation).getWork(predecessorTask.getActualStart(), m_dataDate, TimeUnit.HOURS).getDuration();
                        double lagDurationInHours = relation.getLag().convertUnits(TimeUnit.HOURS, m_file.getProjectProperties()).getDuration();

                        if (lagDurationInHours > actualLagDurationInHours)
                        {
                           Duration remainingLag = Duration.getInstance(lagDurationInHours - actualLagDurationInHours, TimeUnit.HOURS);
                           LocalDateTime earlyFinish = addLag(relation, predecessorTask.getEarlyStart(), remainingLag);
                           earlyStart = getDateFromEndAndRemainingDuration(successorTask, earlyFinish);
                        }
                        else
                        {
                           earlyStart = predecessorTask.getEarlyStart();
                        }
                     }
                     else
                     {
                        // successor finished
                        earlyStart = addLag(relation, predecessorTask.getEarlyStart());
                        if (earlyStart.isAfter(m_dataDate))
                        {
                           earlyStart = m_dataDate;
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
                     earlyStart = addLag(relation, getDateFromEnd(successorTask, predecessorTask.getEarlyStart()));
                     if (earlyStart.isAfter(m_dataDate))
                     {
                        earlyStart = m_dataDate;
                     }
                  }
                  else
                  {
                     // successor started
                     if (successorTask.getActualFinish() == null)
                     {
                        // successor not finished
                        earlyStart = addLag(relation, getDateFromEnd(successorTask, predecessorTask.getEarlyStart()));
                     }
                     else
                     {
                        // successor finished
                        earlyStart = addLag(relation, getDateFromEnd(successorTask, predecessorTask.getEarlyStart()));
                        if (earlyStart.isAfter(m_dataDate))
                        {
                           earlyStart = m_dataDate;

                           LocalDateTime adjustedEarlyStart = successorTask.getEffectiveCalendar().getNextWorkStart(earlyStart);
                           Duration work = successorTask.getEffectiveCalendar().getWork(earlyStart, adjustedEarlyStart, TimeUnit.MINUTES);
                           if (work.getDuration() == 0)
                           {
                              earlyStart = adjustedEarlyStart;
                           }
                        }
                     }
                  }
               }
            }

            return earlyStart;
         }

         default:
         {
            throw new UnsupportedOperationException();
         }
      }
   }

   private LocalDateTime calculateLateFinish(Relation relation)
   {
      Task predecessorTask = relation.getPredecessorTask();
      Task successorTask = relation.getSuccessorTask();

      LocalDateTime lateFinish;

      switch (relation.getType())
      {
         case START_START:
         {
            if (successorTask.getActualStart() == null && predecessorTask.getActualStart() == null)
            {
               LocalDateTime lateStart = predecessorTask.getEffectiveCalendar().getNextWorkStart(removeLag(relation, successorTask.getLateStart()));
               lateFinish = getDateFromStartAndRemainingDuration(predecessorTask, lateStart);

               // Hmmm... dubious logic. Does this just work for indefensible-tedium or is this general?
               if (successorTask.getSuccessors().isEmpty() && successorTask.getLateFinish().isBefore(lateFinish))
               {
                  lateFinish = successorTask.getLateFinish();
               }
            }
            else
            {
               LocalDateTime lateStart;

               if (predecessorTask.getActualStart() == null)
               {
                  // Predecessor not started
                  if (successorTask.getActualStart() == null)
                  {
                     // Successor not started
                     lateStart = removeLag(relation, successorTask.getLateStart());
                  }
                  else
                  {
                     // successor started
                     if (successorTask.getActualFinish() == null)
                     {
                        // successor not finished
                        lateStart = removeLag(relation, successorTask.getLateStart());
                     }
                     else
                     {
                        // successor finished
                        lateStart = removeLag(relation, successorTask.getLateStart());
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
                        double actualLagDurationInHours = predecessorTask.getActualStart().isAfter(m_dataDate) ? 0 : getLagCalendar(relation).getWork(predecessorTask.getActualStart(), m_dataDate, TimeUnit.HOURS).getDuration();
                        double lagDurationInHours = relation.getLag().convertUnits(TimeUnit.HOURS, m_file.getProjectProperties()).getDuration();

                        if (lagDurationInHours > actualLagDurationInHours)
                        {
                           Duration remainingLag = Duration.getInstance(lagDurationInHours - actualLagDurationInHours, TimeUnit.HOURS);
                           lateStart =  removeLag(relation, successorTask.getLateStart(), remainingLag);
                        }
                        else
                        {
                           lateStart = successorTask.getLateStart();
                        }
                     }
                     else
                     {
                        // successor started
                        if (successorTask.getActualFinish() == null)
                        {
                           // successor not finished
                           lateStart = successorTask.getLateStart();
                        }
                        else
                        {
                           // successor finished
                           if (relation.getLag().getDuration() == 0.0)
                           {
                              lateStart = successorTask.getLateStart();
                           }
                           else
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
                        }
                     }
                  }
                  else
                  {
                     // Predecessor not finished
                     if (successorTask.getActualStart() == null)
                     {
                        // Successor not started
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
                        // successor started
                        if (successorTask.getActualFinish() == null)
                        {
                           // successor not finished
                           lateStart = successorTask.getLateStart();
                        }
                        else
                        {
                           // successor finished
                           lateStart = successorTask.getLateStart();
                        }
                     }
                  }
               }

               lateFinish = getDateFromStartAndRemainingDuration(predecessorTask, lateStart);
            }

            break;
         }

         case FINISH_FINISH:
         {
            if (predecessorTask.getActualStart() == null)
            {
               // Predecessor not started
               if (successorTask.getActualStart() == null)
               {
                  // Successor not started
                  lateFinish = removeLag(relation, successorTask.getLateFinish());
               }
               else
               {
                  // successor started
                  if (successorTask.getActualFinish() == null)
                  {
                     // successor not finished
                     lateFinish = removeLag(relation, successorTask.getLateFinish());
                  }
                  else
                  {
                     // successor finished
                     lateFinish = removeLag(relation, successorTask.getLateFinish());
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
                     lateFinish = successorTask.getLateFinish();
                  }
                  else
                  {
                     // successor started
                     if (successorTask.getActualFinish() == null)
                     {
                        // successor not finished
                        lateFinish = successorTask.getLateFinish();
                     }
                     else
                     {
                        // successor finished
                        double actualLagDurationInHours = predecessorTask.getActualFinish().isAfter(m_dataDate) ? 0 : getLagCalendar(relation).getWork(predecessorTask.getActualFinish(), m_dataDate, TimeUnit.HOURS).getDuration();
                        double lagDurationInHours = relation.getLag().convertUnits(TimeUnit.HOURS, m_file.getProjectProperties()).getDuration();

                        if (lagDurationInHours > actualLagDurationInHours)
                        {
                           Duration remainingLag = Duration.getInstance(lagDurationInHours - actualLagDurationInHours, TimeUnit.HOURS);
                           lateFinish = removeLag(relation, successorTask.getLateFinish(), remainingLag);
                        }
                        else
                        {
                           lateFinish = successorTask.getLateFinish();
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
                     lateFinish = removeLag(relation, successorTask.getLateFinish());
                  }
                  else
                  {
                     // successor started
                     if (successorTask.getActualFinish() == null)
                     {
                        // successor not finished
                        lateFinish = removeLag(relation, successorTask.getLateFinish());
                     }
                     else
                     {
                        // successor finished
                        lateFinish = removeLag(relation, successorTask.getLateFinish());
                     }
                  }
               }
            }

            break;
         }

         case START_FINISH:
         {
            if (predecessorTask.getActualStart() == null)
            {
               // Predecessor not started
               if (successorTask.getActualStart() == null)
               {
                  // Successor not started
                  lateFinish = removeLag(relation, getDateFromStart(predecessorTask, successorTask.getLateFinish()));
               }
               else
               {
                  // successor started
                  if (successorTask.getActualFinish() == null)
                  {
                     // successor not finished
                     lateFinish = removeLag(relation, getDateFromStart(predecessorTask, successorTask.getLateFinish()));
                  }
                  else
                  {
                     // successor finished
                     lateFinish = removeLag(relation, getDateFromStart(predecessorTask, successorTask.getLateFinish()));
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
                     lateFinish = removeLag(relation, getDateFromStart(predecessorTask, successorTask.getLateFinish()));
                  }
                  else
                  {
                     // successor started
                     if (successorTask.getActualFinish() == null)
                     {
                        // successor not finished
                        lateFinish = removeLag(relation, getDateFromStart(predecessorTask, successorTask.getLateFinish()));
                     }
                     else
                     {
                        // successor finished
                        lateFinish = successorTask.getLateFinish();
                     }
                  }
               }
               else
               {
                  // Predecessor not finished
                  if (successorTask.getActualStart() == null)
                  {
                     // Successor not started
                     lateFinish = getDateFromStartAndRemainingDuration(predecessorTask, successorTask.getLateFinish());
                  }
                  else
                  {
                     // successor started
                     if (successorTask.getActualFinish() == null)
                     {
                        // successor not finished
                        lateFinish = removeLag(relation, getDateFromStart(predecessorTask, successorTask.getLateFinish()));
                     }
                     else
                     {
                        // successor finished
                        double actualLagDurationInHours = predecessorTask.getActualStart().isAfter(m_dataDate) ? 0 : getLagCalendar(relation).getWork(predecessorTask.getActualStart(), m_dataDate, TimeUnit.HOURS).getDuration();
                        double lagDurationInHours = relation.getLag().convertUnits(TimeUnit.HOURS, m_file.getProjectProperties()).getDuration();

                        if (lagDurationInHours > actualLagDurationInHours)
                        {
                           Duration remainingLag = Duration.getInstance(lagDurationInHours - actualLagDurationInHours, TimeUnit.HOURS);
                           lateFinish = removeLag(relation, getDateFromStart(predecessorTask, successorTask.getLateFinish()), remainingLag);
                        }
                        else
                        {
                           lateFinish = getDateFromStartAndRemainingDuration(predecessorTask, successorTask.getLateFinish());
                        }
                     }
                  }
               }
            }

            break;
         }

         default:
         {
            if (predecessorTask.getActualStart() == null)
            {
               // Predecessor not started
               if (successorTask.getActualStart() == null)
               {
                  // Successor not started
                  lateFinish = removeLag(relation, successorTask.getLateStart());
               }
               else
               {
                  // successor started
                  if (successorTask.getActualFinish() == null)
                  {
                     // successor not finished
                     //lateFinish = successorTask.getLateStart();
                     lateFinish = removeLag(relation, successorTask.getLateStart());
                  }
                  else
                  {
                     // successor finished
                     lateFinish = successorTask.getLateStart();
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
                     if (relation.getLag().getDuration() > 0.0)
                     {
                        double actualLagDurationInHours = predecessorTask.getActualFinish().isAfter(m_dataDate) ? 0 : getLagCalendar(relation).getWork(predecessorTask.getActualFinish(), m_dataDate, TimeUnit.HOURS).getDuration();
                        double lagDurationInHours = relation.getLag().convertUnits(TimeUnit.HOURS, m_file.getProjectProperties()).getDuration();

                        if (lagDurationInHours > actualLagDurationInHours)
                        {
                           Duration remainingLag = Duration.getInstance(lagDurationInHours - actualLagDurationInHours, TimeUnit.HOURS);
                           lateFinish = removeLag(relation, successorTask.getLateStart(), remainingLag);
                        }
                        else
                        {
                           lateFinish = successorTask.getLateStart();
                        }
                     }
                     else
                     {
                        lateFinish = successorTask.getLateStart();
                     }
                  }
                  else
                  {
                     // successor started
                     if (successorTask.getActualFinish() == null)
                     {
                        // check for actual progress
                        if (successorTask.getActualDuration().getDuration() == 0.0)
                        {
                           lateFinish = removeLag(relation, successorTask.getLateStart());
                        }
                        else
                        {
                           lateFinish = successorTask.getLateStart();
                        }
                     }
                     else
                     {
                        // successor finished
                        double actualLagDurationInHours = predecessorTask.getActualFinish().isAfter(m_dataDate) ? 0 : getLagCalendar(relation).getWork(predecessorTask.getActualFinish(), m_dataDate, TimeUnit.HOURS).getDuration();
                        double lagDurationInHours = relation.getLag().convertUnits(TimeUnit.HOURS, m_file.getProjectProperties()).getDuration();

                        if (lagDurationInHours > actualLagDurationInHours)
                        {
                           Duration remainingLag = Duration.getInstance(lagDurationInHours - actualLagDurationInHours, TimeUnit.HOURS);
                           lateFinish = removeLag(relation, successorTask.getLateStart(), remainingLag);
                        }
                        else
                        {
                           lateFinish = successorTask.getLateStart();
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
                     lateFinish = removeLag(relation, successorTask.getLateStart());
                  }
                  else
                  {
                     // successor started
                     if (successorTask.getActualFinish() == null)
                     {
                        // check for actual progress
                        if (successorTask.getActualDuration().getDuration() == 0.0)
                        {
                           lateFinish = removeLag(relation, successorTask.getLateStart());
                        }
                        else
                        {
                           lateFinish = successorTask.getLateStart();
                        }
                     }
                     else
                     {
                        // successor finished
                        lateFinish = successorTask.getLateStart();
                     }
                  }
               }
            }

            break;
         }
      }

      if (lateFinish.isAfter(m_projectFinishDate))
      {
         // If we're between working periods, move back to the last work finish
         LocalDateTime previousWorkFinish = predecessorTask.getEffectiveCalendar().getPreviousWorkFinish(m_projectFinishDate);
         if (predecessorTask.getEffectiveCalendar().getWork(previousWorkFinish, m_projectFinishDate, TimeUnit.HOURS).getDuration() == 0)
         {
            return previousWorkFinish;
         }

         return m_projectFinishDate;
      }

      return lateFinish;
   }

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

   private LocalDateTime getDateFromStart(Task task, LocalDateTime date)
   {
      return getDate(task.getEffectiveCalendar(), date, task.getDuration());
   }

   private LocalDateTime getDateFromEnd(Task task, LocalDateTime date)
   {
      return getDate(task.getEffectiveCalendar(), date, task.getDuration().negate());
   }

   private LocalDateTime getDateFromStartAndRemainingDuration(Task task, LocalDateTime date)
   {
      return getDate(task.getEffectiveCalendar(), date, task.getRemainingDuration());
   }

   private LocalDateTime getDateFromEndAndRemainingDuration(Task task, LocalDateTime date)
   {
      return getDate(task.getEffectiveCalendar(), date, task.getRemainingDuration().negate());
   }

   private LocalDateTime addLag(Relation relation, LocalDateTime date)
   {
      return addLag(relation, date, relation.getLag());
   }

   private LocalDateTime addLag(Relation relation, LocalDateTime date, Duration lag)
   {
      return getDate(getLagCalendar(relation), date, lag);
   }

   private LocalDateTime removeLag(Relation relation, LocalDateTime date)
   {
      return removeLag(relation, date, relation.getLag());
   }

   private LocalDateTime removeLag(Relation relation, LocalDateTime date, Duration lag)
   {
      return getDate(getLagCalendar(relation), date, lag.negate());
   }

   public boolean ignoreTask(Task task)
   {
      return task.getSummary() || !task.getActive() || task.getNull() || task.getActivityType() == ActivityType.LEVEL_OF_EFFORT || task.getActivityType() == ActivityType.WBS_SUMMARY;
   }

   private void alapAdjust(Task task) throws CpmException
   {
      List<Relation> successors = task.getSuccessors().stream().filter(r -> !ignoreTask(r.getSuccessorTask())).collect(Collectors.toList());
      if (successors.isEmpty())
      {
         LocalDateTime earlyFinish = m_projectFinishDate;
         LocalDateTime earlyStart = getDateFromEndAndRemainingDuration(task, earlyFinish);
         task.setEarlyStart(earlyStart);
         task.setEarlyFinish(earlyFinish);
         return;
      }

      Relation relation  = successors.stream().min(Comparator.comparing(r -> getAlapEarlyStart(r))).orElseThrow(() -> new CpmException("Missing early start date"));

      LocalDateTime earlyStart = getAlapEarlyStart(relation);
      LocalDateTime earlyFinish = getDateFromStartAndRemainingDuration(task, earlyStart);
      task.setEarlyStart(earlyStart);
      task.setEarlyFinish(earlyFinish);
   }

   private LocalDateTime getAlapEarlyStart(Relation relation)
   {
      Task predecessorTask = relation.getPredecessorTask();
      ProjectCalendar calendar = predecessorTask.getEffectiveCalendar();
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
                  return removeLag(relation, getDateFromEndAndRemainingDuration(predecessorTask, successorTask.getEarlyStart()));
               }
               else
               {
                  // successor started
                  if (successorTask.getActualFinish() == null)
                  {
                     // successor not finished
                     return removeLag(relation, getDateFromEndAndRemainingDuration(predecessorTask, successorTask.getEarlyStart()));
                  }
                  else
                  {
                     // successor finished
                     return removeLag(relation, getDateFromEndAndRemainingDuration(predecessorTask, successorTask.getEarlyStart()));
                  }
               }
            }
            else
            {
               // Predecessor Started
               if (predecessorTask.getActualFinish() == null)
               {
                  // Predecessor not finished
                  if (successorTask.getActualStart() == null)
                  {
                     // Successor not started
                     return removeLag(relation, getDateFromEndAndRemainingDuration(predecessorTask, successorTask.getEarlyStart()));
                  }
                  else
                  {
                     // successor started
                     if (successorTask.getActualFinish() == null)
                     {
                        // successor not finished
                        return removeLag(relation, getDateFromEndAndRemainingDuration(predecessorTask, successorTask.getEarlyStart()));
                     }
                     else
                     {
                        // successor finished
                        return removeLag(relation, getDateFromEndAndRemainingDuration(predecessorTask, successorTask.getEarlyStart()));
                     }
                  }
               }
               else
               {
                  // Predecessor finished
                  if (successorTask.getActualStart() == null)
                  {
                     // Successor not started
                     LocalDateTime earlyStart = m_dataDate;
                     LocalDateTime adjustedEarlyStart = calendar.getNextWorkStart(earlyStart);
                     Duration work = calendar.getWork(earlyStart, adjustedEarlyStart, TimeUnit.MINUTES);
                     if (work.getDuration() == 0)
                     {
                        earlyStart = adjustedEarlyStart;
                     }
                     return earlyStart;
                  }
                  else
                  {
                     // successor started
                     if (successorTask.getActualFinish() == null)
                     {
                        // successor not finished
                        LocalDateTime earlyStart = m_dataDate;
                        LocalDateTime adjustedEarlyStart = calendar.getNextWorkStart(earlyStart);
                        Duration work = calendar.getWork(earlyStart, adjustedEarlyStart, TimeUnit.MINUTES);
                        if (work.getDuration() == 0)
                        {
                           earlyStart = adjustedEarlyStart;
                        }
                        return earlyStart;
                     }
                     else
                     {
                        // successor finished
                        return m_dataDate;
                     }
                  }
               }
            }
         }

         case FINISH_FINISH:
         {
            LocalDateTime earlyFinish = removeLag(relation, successorTask.getEarlyFinish());
            return getDateFromEndAndRemainingDuration(predecessorTask, earlyFinish);
         }

         default:
         {
            // TODO: need example to determine correct behaviour here
            return LocalDateTime.MAX;
         }
      }
   }

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

   private final ProjectFile m_file;
   private final LocalDateTime m_dataDate;
   private final ProjectCalendar m_twentyFourHourCalendar;
   private LocalDateTime m_projectStartDate;
   private LocalDateTime m_projectFinishDate;
}
