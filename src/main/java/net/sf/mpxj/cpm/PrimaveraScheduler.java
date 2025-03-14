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

import net.sf.mpxj.PercentCompleteType;
import net.sf.mpxj.ProjectCalendar;
import net.sf.mpxj.ProjectCalendarHours;
import net.sf.mpxj.ProjectFile;
import net.sf.mpxj.Relation;
import net.sf.mpxj.Task;
import net.sf.mpxj.TimeUnit;
import net.sf.mpxj.common.LocalDateTimeHelper;

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
      if (m_file.getTasks().stream().anyMatch(t -> t.getActivityType() == ActivityType.RESOURCE_DEPENDENT && !t.getResourceAssignments().isEmpty()))
      {
         throw new CpmException("Schedule contains Resource Dependent activities with resource assignments");
      }

      m_projectStartDate = projectStartDate;

      List<Task> activities = new DepthFirstGraphSort(m_file, PrimaveraScheduler::isActivity).sort();
      if (activities.isEmpty())
      {
         return;
      }

      clearDates();

      if (m_dataDate == null)
      {
         m_dataDate = m_projectStartDate;
      }
      else
      {
         if (projectStartDate.isBefore(m_dataDate))
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

      for (Task activity : activities)
      {
         activity.setStart(activity.getActualStart() == null ? activity.getEarlyStart() : activity.getActualStart());
         activity.setFinish(activity.getActualFinish() == null ? activity.getEarlyFinish() : activity.getActualFinish());
      }

      levelOfEffortPass();

      m_file.getChildTasks().forEach(t -> rollupDates(t));
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
      List<Relation> predecessors = task.getPredecessors().stream().filter(r -> isActivity(r.getPredecessorTask())).collect(Collectors.toList());

      if (task.getActualStart() == null)
      {
         if (predecessors.isEmpty())
         {
            switch (task.getConstraintType())
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
               if (task.getActualDuration().getDuration() == 0)
               {
                  earlyStart = task.getEffectiveCalendar().getNextWorkStart(m_dataDate);
                  earlyFinish = getDateFromStart(task, earlyStart);
               }
               else
               {
                  if (task.getRemainingDuration().getDuration() == 0)
                  {
                     earlyStart = task.getEffectiveCalendar().getNextWorkStart(m_dataDate);
                     earlyFinish = earlyStart;
                  }
                  else
                  {
                     earlyFinish = getDateFromStart(task, task.getActualStart());
                     // Sometimes this instead... not sure why?
                     //earlyFinish = task.getEffectiveCalendar().getNextWorkStart(getDateFromStart(task, task.getActualStart()));
                     earlyStart = getDateFromEndAndRemainingDuration(task, earlyFinish);
                  }
               }
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

      setRemainingEarlyDates(task);
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
      List<Relation> successors = m_file.getRelations().getRawSuccessors(task).stream().filter(r -> isActivity(r.getSuccessorTask())).collect(Collectors.toList());
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
      setRemainingLateDates(task);

      if (task.getConstraintType() == ConstraintType.AS_LATE_AS_POSSIBLE)
      {
         alapAdjust(task);
      }
   }

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

               if (relation.getLag().getDuration() > 0)
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

               if (relation.getLag().getDuration() > 0)
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

                  if (relation.getLag().getDuration() > 0)
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

                  if (relation.getLag().getDuration() > 0)
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
                  successorTask.getEffectiveCalendar().getNextWorkStart(predecessorTask.getEarlyFinish());
               }

               if (relation.getLag().getDuration() > 0)
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

                  if (relation.getLag().getDuration() > 0)
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

                  if (relation.getLag().getDuration() > 0)
                  {
                     return addLag(relation, predecessorTask.getEarlyFinish());
                  }

                  return addLag(relation, predecessorTask.getEarlyFinish());
               }
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

               if (relation.getLag().getDuration() > 0)
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

               if (relation.getLag().getDuration() > 0)
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

                  if (relation.getLag().getDuration() > 0)
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

                  if (relation.getLag().getDuration() > 0)
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

                  if (relation.getLag().getDuration() > 0)
                  {
                     return predecessorTask.getEarlyStart();
                  }

                  return predecessorTask.getEarlyStart();
               }
            }
         }
      }
   }

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
         else
         {
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
            else
            {
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
                  else
                  {
                     return getEarlyStartFromEarlyFinish(successorTask, addLag(relation, addLag(relation, predecessorTask.getEarlyFinish())));
                  }
               }

               return getEarlyStartFromEarlyFinish(successorTask, addLag(relation, addLag(relation, predecessorTask.getEarlyFinish())));
            }
            else
            {
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
               else
               {
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
            }
         }
         else
         {
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
            else
            {
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
               else
               {
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
            }
         }
      }
   }

   private LocalDateTime getEarlyStartFromEarlyFinish(Task successorTask, LocalDateTime earlyFinish)
   {
      LocalDateTime earlyStart = getDateFromEndAndRemainingDuration(successorTask, earlyFinish);
      if (earlyStart.isBefore(m_projectStartDate))
      {
         return m_projectStartDate;
      }
      return earlyStart;
   }

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
               earlyStart = addLag(relation, getDateFromEnd(successorTask, predecessorTask.getEarlyStart()));
            }
            else
            {
               earlyStart = addLag(relation, getDateFromEnd(successorTask, predecessorTask.getEarlyStart()));
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
                  LocalDateTime earlyFinish = successorTask.getEffectiveCalendar().getDate(successorTask.getActualStart(), successorTask.getDuration());
                  earlyStart = successorTask.getEffectiveCalendar().getDate(earlyFinish, successorTask.getRemainingDuration().negate());
               }
               else
               {
                  LocalDateTime earlyFinish = addLag(relation, predecessorTask.getEarlyStart());
                  earlyStart = getDateFromEndAndRemainingDuration(successorTask, earlyFinish);
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
                  earlyStart = addLag(relation, getDateFromEnd(successorTask, predecessorTask.getEarlyStart()));
               }
               else
               {
                  earlyStart = addLag(relation, getDateFromEnd(successorTask, predecessorTask.getEarlyStart()));
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
                  earlyStart = addLag(relation, getDateFromEnd(successorTask, predecessorTask.getEarlyStart()));
                  if (earlyStart.isAfter(m_dataDate))
                  {
                     earlyStart = m_dataDate;
                  }
               }
               else
               {
                  earlyStart = addLag(relation, getDateFromEnd(successorTask, predecessorTask.getEarlyStart()));
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
                     earlyStart = getDateFromEndAndRemainingDuration(successorTask, earlyFinish);
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
                     earlyStart = adjustToNextWorkStart(successorTask, m_dataDate);
                  }
               }
            }
         }
      }

      return earlyStart;
   }

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

   private LocalDateTime adjustLateFinish(Relation relation, LocalDateTime lateFinish)
   {
      if (lateFinish.isAfter(m_projectFinishDate))
      {
         // If we're between working periods, move back to the last work finish
         Task predecessorTask = relation.getPredecessorTask();
         LocalDateTime previousWorkFinish = predecessorTask.getEffectiveCalendar().getPreviousWorkFinish(m_projectFinishDate);
         if (predecessorTask.getEffectiveCalendar().getWork(previousWorkFinish, m_projectFinishDate, TimeUnit.HOURS).getDuration() == 0)
         {
            return previousWorkFinish;
         }

         return m_projectFinishDate;
      }

      return lateFinish;
   }

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
               lateStart = predecessorTask.getEffectiveCalendar().getNextWorkStart(successorTask.getLateStart());
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
                  lateStart = predecessorTask.getEffectiveCalendar().getNextWorkStart(removeLag(relation, successorTask.getLateStart()));
                  lateFinish = getDateFromStartAndRemainingDuration(predecessorTask, lateStart);
               }
               else
               {
                  lateStart = predecessorTask.getEffectiveCalendar().getNextWorkStart(removeLag(relation, successorTask.getLateStart()));
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
         else
         {
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
            else
            {
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
                  else
                  {
                     return successorTask.getLateFinish();
                  }
               }

               return successorTask.getLateFinish();
            }
            else
            {
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
               else
               {
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
                     else
                     {
                        return successorTask.getLateFinish();
                     }
                  }

                  return successorTask.getLateFinish();
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
                  return removeLag(relation, successorTask.getLateFinish());
               }

               if (relation.getLag().getDuration() > 0)
               {
                  return removeLag(relation, successorTask.getLateFinish());
               }

               return removeLag(relation, successorTask.getLateFinish());
            }
            else
            {
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
               else
               {
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
            }
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
            if (relation.getLag().getDuration() == 0)
            {
               return removeLag(relation, getDateFromStart(predecessorTask, successorTask.getLateFinish()));
            }

            if (relation.getLag().getDuration() > 0)
            {
               return removeLag(relation, getDateFromStart(predecessorTask, successorTask.getLateFinish()));
            }

            return removeLag(relation, getDateFromStart(predecessorTask, successorTask.getLateFinish()));
         }
         else
         {
            // successor started
            if (successorTask.getActualFinish() == null)
            {
               // successor not finished
               if (relation.getLag().getDuration() == 0)
               {
                  return removeLag(relation, getDateFromStart(predecessorTask, successorTask.getLateFinish()));
               }

               if (relation.getLag().getDuration() > 0)
               {
                  return removeLag(relation, getDateFromStart(predecessorTask, successorTask.getLateFinish()));
               }

               return removeLag(relation, getDateFromStart(predecessorTask, successorTask.getLateFinish()));
            }
            else
            {
               // successor finished
               if (relation.getLag().getDuration() == 0)
               {
                  return removeLag(relation, getDateFromStart(predecessorTask, successorTask.getLateFinish()));
               }

               if (relation.getLag().getDuration() > 0)
               {
                  return removeLag(relation, getDateFromStart(predecessorTask, successorTask.getLateFinish()));
               }

               return removeLag(relation, getDateFromStart(predecessorTask, successorTask.getLateFinish()));
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
                  return removeLag(relation, getDateFromStart(predecessorTask, successorTask.getLateFinish()));
               }

               if (relation.getLag().getDuration() > 0)
               {
                  return removeLag(relation, getDateFromStart(predecessorTask, successorTask.getLateFinish()));
               }

               return removeLag(relation, getDateFromStart(predecessorTask, successorTask.getLateFinish()));
            }
            else
            {
               // successor started
               if (successorTask.getActualFinish() == null)
               {
                  // successor not finished
                  if (relation.getLag().getDuration() == 0)
                  {
                     return removeLag(relation, getDateFromStart(predecessorTask, successorTask.getLateFinish()));
                  }

                  if (relation.getLag().getDuration() > 0)
                  {
                     return removeLag(relation, getDateFromStart(predecessorTask, successorTask.getLateFinish()));
                  }

                  return removeLag(relation, getDateFromStart(predecessorTask, successorTask.getLateFinish()));
               }
               else
               {
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
                  return getDateFromStartAndRemainingDuration(predecessorTask, successorTask.getLateFinish());
               }

               if (relation.getLag().getDuration() > 0)
               {
                  return getDateFromStartAndRemainingDuration(predecessorTask, successorTask.getLateFinish());
               }

               return getDateFromStartAndRemainingDuration(predecessorTask, successorTask.getLateFinish());
            }
            else
            {
               // successor started
               if (successorTask.getActualFinish() == null)
               {
                  // successor not finished
                  if (relation.getLag().getDuration() == 0)
                  {
                     return removeLag(relation, getDateFromStart(predecessorTask, successorTask.getLateFinish()));
                  }

                  if (relation.getLag().getDuration() > 0)
                  {
                     return removeLag(relation, getDateFromStart(predecessorTask, successorTask.getLateFinish()));
                  }

                  return removeLag(relation, getDateFromStart(predecessorTask, successorTask.getLateFinish()));
               }
               else
               {
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
                        return removeLag(relation, getDateFromStart(predecessorTask, successorTask.getLateFinish()), remainingLag);
                     }
                     else
                     {
                        return getDateFromStartAndRemainingDuration(predecessorTask, successorTask.getLateFinish());
                     }
                  }

                  return getDateFromStartAndRemainingDuration(predecessorTask, successorTask.getLateFinish());
               }
            }
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
         else
         {
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
            else
            {
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
                  else
                  {
                     return successorTask.getLateStart();
                  }
               }

               return successorTask.getLateStart();
            }
            else
            {
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
                     else
                     {
                        return successorTask.getLateStart();
                     }
                  }

                  if (successorTask.getActualDuration().getDuration() == 0.0)
                  {
                     return removeLag(relation, successorTask.getLateStart());
                  }
                  else
                  {
                     return successorTask.getLateStart();
                  }
               }
               else
               {
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
                     else
                     {
                        return successorTask.getLateStart();
                     }
                  }

                  return successorTask.getLateStart();
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
                  return removeLag(relation, successorTask.getLateStart());
               }

               if (relation.getLag().getDuration() > 0)
               {
                  return removeLag(relation, successorTask.getLateStart());
               }

               return removeLag(relation, successorTask.getLateStart());
            }
            else
            {
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
                     else
                     {
                        return successorTask.getLateStart();
                     }
                  }

                  if (relation.getLag().getDuration() > 0)
                  {
                     if (successorTask.getActualDuration().getDuration() == 0.0)
                     {
                        return removeLag(relation, successorTask.getLateStart());
                     }
                     else
                     {
                        return successorTask.getLateStart();
                     }
                  }

                  if (successorTask.getActualDuration().getDuration() == 0.0)
                  {
                     return removeLag(relation, successorTask.getLateStart());
                  }
                  else
                  {
                     return successorTask.getLateStart();
                  }
               }
               else
               {
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
            }
         }
      }
   }

   private LocalDateTime adjustToNextWorkStart(Task task, LocalDateTime date)
   {
      LocalDateTime adjustedDate = task.getEffectiveCalendar().getNextWorkStart(date);
      Duration work = task.getEffectiveCalendar().getWork(date, adjustedDate, TimeUnit.MINUTES);
      if (work.getDuration() == 0)
      {
         return adjustedDate;
      }
      return date;
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

   private LocalDateTime removeLag(Relation relation, LocalDateTime date)
   {
      return removeLag(relation, date, relation.getLag());
   }

   private LocalDateTime removeLag(Relation relation, LocalDateTime date, Duration lag)
   {
      if (date == null)
      {
         return null;
      }

      return getDate(getLagCalendar(relation), date, lag.negate());
   }

   static boolean isActivity(Task task)
   {
      return !(task.getSummary() || task.getActivityType() == ActivityType.LEVEL_OF_EFFORT || task.getActivityType() == ActivityType.WBS_SUMMARY);
   }

   static boolean isLevelOfEffortActivity(Task task)
   {
      return task.getActivityType() == ActivityType.LEVEL_OF_EFFORT;
   }

   private void alapAdjust(Task task) throws CpmException
   {
      LocalDateTime earlyStart;
      LocalDateTime earlyFinish;

      List<Relation> successors = task.getSuccessors().stream().filter(r -> isActivity(r.getSuccessorTask())).collect(Collectors.toList());
      if (successors.isEmpty())
      {
         earlyFinish = m_projectFinishDate;
         earlyStart = getDateFromEndAndRemainingDuration(task, earlyFinish);
      }
      else
      {
         Relation relation = successors.stream().min(Comparator.comparing(r -> getAlapEarlyStart(r))).orElseThrow(() -> new CpmException("Missing early start date"));
         earlyStart = getAlapEarlyStart(relation);
         earlyFinish = getDateFromStartAndRemainingDuration(task, earlyStart);
      }

      task.setEarlyStart(earlyStart);
      task.setEarlyFinish(earlyFinish);
      setRemainingEarlyDates(task);
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
                     return adjustToNextWorkStart(predecessorTask, m_dataDate);
                  }
                  else
                  {
                     // successor started
                     if (successorTask.getActualFinish() == null)
                     {
                        // successor not finished
                        return adjustToNextWorkStart(predecessorTask, m_dataDate);
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
            if (task.getActualStart().isAfter(m_dataDate))
            {
               remainingEarlyStart = task.getEarlyStart();
            }
            else
            {
               remainingEarlyStart = task.getCalendar().getNextWorkStart(m_dataDate);
            }
         }
      }

      task.setRemainingEarlyStart(remainingEarlyStart);
      task.setRemainingEarlyFinish(getDateFromStartAndRemainingDuration(task, remainingEarlyStart));
   }

   private void setRemainingLateDates(Task task)
   {
      if (task.getActualFinish() != null)
      {
         return;
      }

      task.setRemainingLateStart(task.getLateStart());
      task.setRemainingLateFinish(task.getLateFinish());
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
      }
   }

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

         earlyStartDate = LocalDateTimeHelper.min(earlyStartDate, task.getEarlyStart());
         earlyFinishDate = LocalDateTimeHelper.max(earlyFinishDate, task.getEarlyFinish());
         remainingEarlyStartDate = LocalDateTimeHelper.min(remainingEarlyStartDate, task.getRemainingEarlyStart());
         remainingEarlyFinishDate = LocalDateTimeHelper.max(remainingEarlyFinishDate, task.getRemainingEarlyFinish());
         lateStartDate = LocalDateTimeHelper.min(lateStartDate, task.getLateStart());
         lateFinishDate = LocalDateTimeHelper.max(lateFinishDate, task.getLateFinish());
         remainingLateStartDate = LocalDateTimeHelper.min(remainingLateStartDate, task.getRemainingLateStart());
         remainingLateFinishDate = LocalDateTimeHelper.max(remainingLateFinishDate, task.getRemainingLateFinish());

         baselineStartDate = LocalDateTimeHelper.min(baselineStartDate, task.getBaselineStart());
         baselineFinishDate = LocalDateTimeHelper.max(baselineFinishDate, task.getBaselineFinish());

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
      List<Task> activities = new DepthFirstGraphSort(m_file, t -> t.getActivityType() == ActivityType.LEVEL_OF_EFFORT).sort();
      if (activities.isEmpty())
      {
         return;
      }

      for (Task activity : activities)
      {
         levelOfEffortPass(activity);
      }
   }

   private void levelOfEffortPass(Task task)
   {
      // Foe LOE these are generated values, so we need to clear them
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
         earlyStart = AnnotatedDateTime.from(task.getEffectiveCalendar().getNextWorkStart(m_dataDate));
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

      AnnotatedDateTime start = earlyStart;
      AnnotatedDateTime finish = earlyFinish;


      if (earlyStart.isBefore(m_dataDate))
      {
         if (earlyStart.isActual())
         {
            earlyStart = AnnotatedDateTime.fromActual(m_dataDate);
         }
         else
         {
            // very dubious logic here
            earlyStart = AnnotatedDateTime.from(task.getEffectiveCalendar().getNextWorkStart(m_dataDate));
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

      if (earlyStart.isActual() || lateStart.isActual())
      {
         if (task.getCalendar().getWork(m_dataDate, task.getStart(), TimeUnit.HOURS).getDuration() <= 0)
         {
            task.setActualStart(task.getStart());
         }
      }

      if (earlyFinish.isActual()  || lateFinish.isActual())
      {
         if (task.getCalendar().getWork(m_dataDate, task.getFinish(), TimeUnit.HOURS).getDuration() <= 0)
         {
            task.setActualStart(task.getStart());
            task.setActualFinish(task.getFinish());
         }
      }

      task.setEarlyStart(earlyStart.getValue());
      task.setEarlyFinish(earlyFinish.getValue());
      task.setLateStart(lateStart.getValue());
      task.setLateFinish(lateFinish.getValue());

      if (task.getActualStart() == null || task.getActualFinish() == null)
      {
         task.setRemainingEarlyStart(earlyStart.getValue());
         task.setRemainingEarlyFinish(earlyFinish.getValue());
         task.setRemainingLateStart(lateStart.getValue());
         task.setRemainingLateFinish(lateFinish.getValue());
      }
   }

   private AnnotatedDateTime updateIfBefore(AnnotatedDateTime currentDate, AnnotatedDateTime newDate)
   {
      if (currentDate == null)
      {
         return newDate;
      }

      return newDate.isBefore(currentDate) ? newDate : currentDate;
   }

   private AnnotatedDateTime updateIfAfter(AnnotatedDateTime currentDate, AnnotatedDateTime newDate)
   {
      if (currentDate == null)
      {
         return newDate;
      }

      return newDate.isAfter(currentDate) ? newDate : currentDate;
   }

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

   private final ProjectFile m_file;
   private final ProjectCalendar m_twentyFourHourCalendar;
   private LocalDateTime m_dataDate;
   private LocalDateTime m_projectStartDate;
   private LocalDateTime m_projectFinishDate;
}
