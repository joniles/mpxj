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

package org.mpxj.mpp;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;

import java.util.List;

import org.mpxj.Duration;
import org.mpxj.TimePeriodEntity;
import org.mpxj.ProjectCalendar;
import org.mpxj.TimeUnit;
import org.mpxj.TimephasedWork;
import org.mpxj.common.AbstractTimephasedWorkNormaliser;
import org.mpxj.common.LocalDateHelper;
import org.mpxj.common.LocalDateTimeHelper;
import org.mpxj.common.LocalTimeHelper;

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
      splitDays(calendar, list);
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
    */
   private void splitDays(ProjectCalendar calendar, List<TimephasedWork> list)
   {
      List<TimephasedWork> result = new ArrayList<>();
      boolean remainderInserted = false;

      for (TimephasedWork item : list)
      {
         if (remainderInserted)
         {
            item.setStart(item.getStart().plusDays(1));
            remainderInserted = false;
         }

         Duration calendarWork = calendar.getWork(item.getStart(), item.getFinish(), TimeUnit.MINUTES);

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
               Duration itemWork = getItemWork(calendar, item);
               if ((totalWork.getDuration() - itemWork.getDuration()) > EQUALITY_DELTA)
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
               else
               {
                  result.add(item);
               }
               break;
            }

            TimephasedWork[] split = splitFirstDay(calendar, item, calendarWork);
            if (split[0] != null)
            {
               TimephasedWork firstDayItem = split[0];
               result.add(firstDayItem);
               Duration firstDayCalendarWork = calendar.getWork(firstDayItem.getStart(), firstDayItem.getFinish(), TimeUnit.MINUTES);
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
    * @return first day and remainder items
    */
   private TimephasedWork[] splitFirstDay(ProjectCalendar calendar, TimephasedWork item, Duration calendarWork)
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
         if (calendar.isWorkingDate(itemStartAsLocalDate))
         {
            splitFinish = LocalTimeHelper.setEndTime(itemStart, calendar.getFinishTime(itemStartAsLocalDate));

            Duration calendarSplitWork = calendar.getWork(itemStart, splitFinish, TimeUnit.MINUTES);
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
   private Duration getItemWork(ProjectCalendar calendar, TimephasedWork item)
   {
      LocalDateTime splitFinish = LocalTimeHelper.setEndTime(item.getStart(), calendar.getFinishTime(LocalDateHelper.getLocalDate(item.getStart())));

      Duration calendarSplitWork = calendar.getWork(item.getStart(), splitFinish, TimeUnit.MINUTES);
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
