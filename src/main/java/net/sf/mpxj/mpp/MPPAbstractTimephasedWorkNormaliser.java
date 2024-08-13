/*
 * file:       MppAbstractTimephasedWorkNormaliser.java
 * author:     Jon Iles
 * copyright:  (c) Packwood Software 2011
 * date:       02/12/2011
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

package net.sf.mpxj.mpp;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;

import java.util.List;

import net.sf.mpxj.Duration;
import net.sf.mpxj.TimePeriodEntity;
import net.sf.mpxj.ProjectCalendar;
import net.sf.mpxj.TaskMode;
import net.sf.mpxj.TimeUnit;
import net.sf.mpxj.TimephasedWork;
import net.sf.mpxj.common.AbstractTimephasedWorkNormaliser;
import net.sf.mpxj.common.LocalDateHelper;
import net.sf.mpxj.common.LocalDateTimeHelper;
import net.sf.mpxj.common.LocalTimeHelper;

/**
 * Normalise timephased data from an MPP file.
 */
public abstract class MPPAbstractTimephasedWorkNormaliser extends AbstractTimephasedWorkNormaliser
{

   /**
    * This method converts the internal representation of timephased
    * data used by MS Project into a standardised
    * format to make it easy to work with.
    *
    * @param parent parent entity
    * @param list list of timephased data
    */
   @Override public void normalise(ProjectCalendar calendar, TimePeriodEntity parent, List<TimephasedWork> list)
   {
      if (list.isEmpty())
      {
         return;
      }

      //dumpList(list);
      splitDays(calendar, list, parent);
      //dumpList(list);
      mergeSameDay(calendar, list);
      //dumpList(list);
      mergeSameWork(calendar, parent, list);
      //dumpList(list);
      convertToHours(list);
      //dumpList(list);
   }

   /**
    * This method breaks down spans of time into individual days.
    *
    * @param calendar current project calendar
    * @param list list of timephased data
    * @param assingment parent entity
    */
   private void splitDays(ProjectCalendar calendar, List<TimephasedWork> list, TimePeriodEntity assignment)
   {
      List<TimephasedWork> result = new ArrayList<>();
      boolean remainderInserted = false;
      boolean taskIsManualScheduled = assignment.getTask().getTaskMode() == TaskMode.MANUALLY_SCHEDULED;
      LocalDate assignmentStart = assignment.getStart().toLocalDate();

      for (TimephasedWork item : list)
      {
         if (remainderInserted)
         {
            item.setStart(item.getStart().plusDays(1));
            remainderInserted = false;
         }

         Duration calendarWork = null;
         boolean firstDayOfAssignment = assignmentStart.equals(item.getStart().toLocalDate());

         if (taskIsManualScheduled
                  && firstDayOfAssignment
                  && !item.getStart().toLocalDate().equals(item.getFinish().toLocalDate()))
         {
            // Manual scheduled work on assignment start date can have work outside of work range.
            // But only on the first day. Therefore we must calculate this here. The getWork method is not aware of this.
            // Need to split getWork into two parts for it.
            LocalDateTime splitStart = item.getStart();
            LocalDateTime splitFinish = LocalTimeHelper.setEndTime(splitStart, calendar.getFinishTime(splitStart.toLocalDate(), taskIsManualScheduled));
            boolean addMissingMinute = false;
            if (splitFinish.isBefore(splitStart))
            {
               // Assumption work outside of normal working time. Therefore assumption work until end of day.
               splitFinish = LocalTimeHelper.setEndTime(splitStart, LocalTime.of(23, 59));
               addMissingMinute = true;
            }
            calendarWork = calendar.getWork(splitStart, splitFinish, TimeUnit.MINUTES, taskIsManualScheduled);

            if (addMissingMinute)
            {
               calendarWork = Duration.add(calendarWork, Duration.getInstance(1, TimeUnit.MINUTES), calendar);
            }

            splitStart = calendar.getNextWorkStart(splitFinish);
            splitFinish = item.getFinish();
            calendarWork = Duration.add(calendarWork, calendar.getWork(splitStart, splitFinish, TimeUnit.MINUTES, taskIsManualScheduled), calendar);
         }
         else
         {
            calendarWork = calendar.getWork(item.getStart(), item.getFinish(), TimeUnit.MINUTES, taskIsManualScheduled);
         }

         while (item != null)
         {
            LocalDateTime startDay = LocalDateTimeHelper.getDayStartDate(item.getStart());
            LocalDateTime finishDay = LocalDateTimeHelper.getDayStartDate(item.getFinish());

            // special case - when the finishday time is midnight, it's really the previous day...
            if (item.getFinish().equals(finishDay))
            {
               finishDay = finishDay.minusDays(1);
            }

            if (startDay.equals(finishDay))
            {
               Duration totalWork = item.getTotalAmount();
               Duration itemWork = getItemWork(calendar, item, taskIsManualScheduled);
               if ((totalWork.getDuration() - itemWork.getDuration()) > EQUALITY_DELTA)
               {
                  if (taskIsManualScheduled)
                  {
                     result.add(item);
                  }
                  else
                  {
                     item.setTotalAmount(itemWork);
                     result.add(item);
                     Duration remainingWork = Duration.getInstance(totalWork.getDuration() - itemWork.getDuration(), TimeUnit.MINUTES);

                     LocalDateTime remainderStart = finishDay.plusDays(1);
                     LocalDateTime remainderFinish = remainderStart.plusDays(1);

                     TimephasedWork remainder = new TimephasedWork();
                     remainder.setStart(remainderStart);
                     remainder.setFinish(remainderFinish);
                     remainder.setTotalAmount(remainingWork);
                     result.add(remainder);

                     remainderInserted = true;
                  }
               }
               else
               {
                  result.add(item);
               }
               break;
            }

            TimephasedWork[] split = splitFirstDay(calendar, item, calendarWork, taskIsManualScheduled);
            if (split[0] != null)
            {
               TimephasedWork firstDayItem = split[0];
               result.add(firstDayItem);
               Duration firstDayCalendarWork = calendar.getWork(firstDayItem.getStart(), firstDayItem.getFinish(), TimeUnit.MINUTES, taskIsManualScheduled);
               calendarWork = Duration.getInstance((calendarWork.getDuration() - firstDayCalendarWork.getDuration()), TimeUnit.MINUTES);
            }

            if (item.equals(split[1]))
            {
               break;
            }

            item = split[1];
         }
      }

      list.clear();
      list.addAll(result);
   }

   /**
    * This method splits the first day off of a time span.
    *
    * @param calendar current calendar
    * @param item timephased item
    * @param calendarWork working hours for item from the calendar
    * @param taskIsManualScheduled task is manual scheduled. Therefore it is allowed to have work outside of range.
    * @return first day and remainder items
    */
   private TimephasedWork[] splitFirstDay(ProjectCalendar calendar, TimephasedWork item, Duration calendarWork, boolean taskIsManualScheduled)
   {
      TimephasedWork[] result = new TimephasedWork[2];

      //
      // Retrieve data used to calculate the pro-rata work split
      //
      LocalDateTime itemStart = item.getStart();
      LocalDateTime itemFinish = item.getFinish();
      Duration itemWork = item.getTotalAmount();

      if (calendarWork.getDuration() != 0)
      {
         //
         // Split the first day
         //
         LocalDateTime splitFinish;
         double splitMinutes;
         LocalDate itemStartAsLocalDate = LocalDateHelper.getLocalDate(itemStart);
         if (taskIsManualScheduled || calendar.isWorkingDate(itemStartAsLocalDate))
         {
            splitFinish = LocalTimeHelper.setEndTime(itemStart, calendar.getFinishTime(itemStartAsLocalDate, taskIsManualScheduled));
            boolean addMissingMinute = false;
            if (taskIsManualScheduled && splitFinish.isBefore(itemStart))
            {
               // Assumption work outside of normal working time. Therefore assumption work until end of day.
               splitFinish = LocalTimeHelper.setEndTime(itemStart, LocalTime.of(23, 59));
               addMissingMinute = true;
            }

            Duration calendarSplitWork = calendar.getWork(itemStart, splitFinish, TimeUnit.MINUTES, taskIsManualScheduled);
            if (addMissingMinute)
            {
               calendarSplitWork = Duration.add(calendarSplitWork, Duration.getInstance(1, TimeUnit.MINUTES), calendar);
            }
            Duration itemWorkPerDay = item.getAmountPerDay();
            Duration splitWork;

            splitMinutes = itemWorkPerDay.getDuration();
            splitMinutes *= calendarSplitWork.getDuration();
            splitMinutes /= (8 * 60); // this appears to be a fixed value
            splitWork = Duration.getInstance(splitMinutes, TimeUnit.MINUTES);

            TimephasedWork split = new TimephasedWork();
            split.setStart(itemStart);
            split.setFinish(splitFinish);
            split.setTotalAmount(splitWork);

            result[0] = split;
         }
         else
         {
            splitFinish = itemStart;
            splitMinutes = 0;
         }

         //
         // Split the remainder
         //
         LocalDateTime splitStart = calendar.getNextWorkStart(splitFinish);
         splitFinish = itemFinish;
         TimephasedWork split;
         if (splitStart.isAfter(splitFinish))
         {
            split = null;
         }
         else
         {
            splitMinutes = itemWork.getDuration() - splitMinutes;
            Duration splitWork = Duration.getInstance(splitMinutes, TimeUnit.MINUTES);

            split = new TimephasedWork();
            split.setStart(splitStart);
            split.setFinish(splitFinish);
            split.setTotalAmount(splitWork);
            split.setAmountPerDay(item.getAmountPerDay());
         }

         result[1] = split;
      }
      return result;
   }

   /**
    * This method merges together timephased data for the same day.
    *
    * @param calendar current calendar
    * @param list timephased data
    */
   protected abstract void mergeSameDay(ProjectCalendar calendar, List<TimephasedWork> list);

   /**
    * Retrieves the pro rata work carried out on a given day.
    *
    * @param calendar current calendar
    * @param item current item.
    * @return item work duration
    */
   private Duration getItemWork(ProjectCalendar calendar, TimephasedWork item, boolean taskIsManualScheduled)
   {
      LocalDateTime splitFinish = LocalTimeHelper.setEndTime(item.getStart(), calendar.getFinishTime(LocalDateHelper.getLocalDate(item.getStart())));

      Duration calendarSplitWork = calendar.getWork(item.getStart(), splitFinish, TimeUnit.MINUTES, taskIsManualScheduled);
      Duration itemWorkPerDay = item.getAmountPerDay();
      Duration splitWork;

      double splitMinutes = itemWorkPerDay.getDuration();
      splitMinutes *= calendarSplitWork.getDuration();
      splitMinutes /= (8 * 60); // this appears to be a fixed value
      splitWork = Duration.getInstance(splitMinutes, TimeUnit.MINUTES);
      return splitWork;
   }

   private static final double EQUALITY_DELTA = 0.2;
}
