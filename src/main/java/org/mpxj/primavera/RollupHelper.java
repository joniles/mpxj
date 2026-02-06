/*
 * file:       RollupHelper.java
 * author:     Jon Iles
 * date:       2025-11-12
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

package org.mpxj.primavera;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import org.mpxj.Duration;
import org.mpxj.PercentCompleteType;
import org.mpxj.ProjectCalendar;
import org.mpxj.ProjectFile;
import org.mpxj.Task;
import org.mpxj.TimeUnit;
import org.mpxj.common.LocalDateTimeHelper;
import org.mpxj.common.NumberHelper;

/**
 * Common methods for rolling up data from activities to WBS.
 */
class RollupHelper
{
   /**
    * Roll up all relevant values from activities to parent WBS entries.
    *
    * @param file ProjectFile instance
    */
   public static void rollupValues(ProjectFile file)
   {
      file.getChildTasks().forEach(RollupHelper::rollupCalendars);
      file.getChildTasks().forEach(RollupHelper::rollupDates);
      file.getChildTasks().forEach(RollupHelper::rollupWork);
      file.getChildTasks().forEach(RollupHelper::rollupCosts);

      if (file.getProjectProperties().getBaselineProjectUniqueID() == null)
      {
         file.getTasks().stream().filter(Task::getSummary).forEach(BaselineHelper::populateBaselineFromCurrentProject);
      }
   }

   /**
    * This method sets the calendar used by a WBS entry. In P6 if all activities
    * under a WBS entry use the same calendar, the WBS entry uses this calendar
    * for date calculation. If the activities use different calendars, the WBS
    * entry will use the project's default calendar.
    *
    * @param task task to validate
    * @return calendar used by this task
    */
   private static ProjectCalendar rollupCalendars(Task task)
   {
      ProjectCalendar result = null;

      if (task.hasChildTasks())
      {
         List<ProjectCalendar> calendars = task.getChildTasks().stream().map(RollupHelper::rollupCalendars).distinct().collect(Collectors.toList());

         if (calendars.size() == 1)
         {
            ProjectCalendar firstCalendar = calendars.get(0);
            if (firstCalendar != null && firstCalendar != task.getParentFile().getDefaultCalendar())
            {
               result = firstCalendar;
               task.setCalendar(result);
            }
         }
      }
      else
      {
         result = task.getCalendar();
      }

      return result;
   }

   /**
    * The Primavera WBS entries we read in as tasks don't have work entered. We try
    * to compensate for this by summing the child tasks' work. This method recursively
    * descends through the tasks to do this.
    *
    * @param parentTask parent task.
    */
   private static void rollupWork(Task parentTask)
   {
      if (!parentTask.hasChildTasks())
      {
         return;
      }

      ProjectCalendar calendar = parentTask.getEffectiveCalendar();

      Duration actualWork = null;
      Duration plannedWork = null;
      Duration remainingWork = null;
      Duration work = null;

      for (Task task : parentTask.getChildTasks())
      {
         rollupWork(task);

         actualWork = Duration.add(actualWork, task.getActualWork(), calendar);
         plannedWork = Duration.add(plannedWork, task.getPlannedWork(), calendar);
         remainingWork = Duration.add(remainingWork, task.getRemainingWork(), calendar);
         work = Duration.add(work, task.getWork(), calendar);
      }

      parentTask.setActualWork(actualWork);
      parentTask.setPlannedWork(plannedWork);
      parentTask.setRemainingWork(remainingWork);
      parentTask.setWork(work);
   }

   /**
    * Recursively descend through the task hierarchy summarising costs
    * to the WBS entries.
    *
    * @param parentTask parent task
    */
   private static void rollupCosts(Task parentTask)
   {
      if (!parentTask.hasChildTasks())
      {
         return;
      }

      double plannedCost = 0;
      double actualCost = 0;
      double remainingCost = 0;
      double cost = 0;
      double fixedCost = 0;

      //process children first before adding their costs
      for (Task child : parentTask.getChildTasks())
      {
         rollupCosts(child);
         plannedCost += NumberHelper.getDouble(child.getPlannedCost());
         actualCost += NumberHelper.getDouble(child.getActualCost());
         remainingCost += NumberHelper.getDouble(child.getRemainingCost());
         cost += NumberHelper.getDouble(child.getCost());
         fixedCost += NumberHelper.getDouble(child.getFixedCost());
      }

      parentTask.setPlannedCost(NumberHelper.getDouble(plannedCost));
      parentTask.setActualCost(NumberHelper.getDouble(actualCost));
      parentTask.setRemainingCost(NumberHelper.getDouble(remainingCost));
      parentTask.setCost(NumberHelper.getDouble(cost));
      parentTask.setFixedCost(NumberHelper.getDouble(fixedCost));
   }

   /**
    * The Primavera WBS entries we read in as tasks have user-entered start and end dates
    * which aren't calculated or adjusted based on the child task dates. We try
    * to compensate for this by using these user-entered dates as baseline dates, and
    * deriving the planned start, actual start, planned finish and actual finish from
    * the child tasks. This method recursively descends through the tasks to do this.
    *
    * @param parentTask parent task.
    */
   private static void rollupDates(Task parentTask)
   {
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
      Duration actualDuration = null;
      Duration remainingDuration = null;
      Duration duration = null;

      ProjectCalendar effectiveCalendar = parentTask.getEffectiveCalendar();
      if (effectiveCalendar != null)
      {
         if (plannedStartDate != null && plannedFinishDate != null)
         {
            plannedDuration = effectiveCalendar.getWork(plannedStartDate, plannedFinishDate, TimeUnit.HOURS);
            parentTask.setPlannedDuration(plannedDuration);
         }

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
                  actualDuration = effectiveCalendar.getWork(parentTask.getActualStart(), taskStartDate, TimeUnit.HOURS);
               }

               if (taskFinishDate != null)
               {
                  remainingDuration = effectiveCalendar.getWork(taskStartDate, taskFinishDate, TimeUnit.HOURS);
               }
            }
         }
         else
         {
            actualDuration = effectiveCalendar.getWork(parentTask.getActualStart(), parentTask.getActualFinish(), TimeUnit.HOURS);
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

         duration = Duration.add(actualDuration, remainingDuration, effectiveCalendar);
      }

      parentTask.setActualDuration(actualDuration);
      parentTask.setRemainingDuration(remainingDuration);
      parentTask.setDuration(duration);

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
}
