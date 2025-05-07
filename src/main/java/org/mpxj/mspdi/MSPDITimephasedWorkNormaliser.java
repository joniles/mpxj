/*
 * file:       MspdiTimephasedWorkNormaliser.java
 * author:     Jon Iles
 * copyright:  (c) Packwood Software 2009
 * date:       09/01/2009
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

package org.mpxj.mspdi;

import java.time.LocalDateTime;
import java.time.LocalTime;
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
import org.mpxj.common.NumberHelper;

/**
 * Normalise timephased data from an MSPDI file.
 */
public final class MSPDITimephasedWorkNormaliser extends AbstractTimephasedWorkNormaliser
{
   /**
    * Private constructor to prevent instantiation.
    */
   private MSPDITimephasedWorkNormaliser()
   {

   }

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
      //dumpList("raw", result);
      splitDays(calendar, list);
      //dumpList("split days", result);
      mergeSameDay(calendar, list);
      //dumpList("mergeSameDay", result);
      mergeSameWork(calendar, parent, list);
      //dumpList("mergeSameWork", result);
      validateSameDay(calendar, list);
      convertToHours(list);
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
      for (TimephasedWork item : list)
      {
         Duration calendarWork = calendar.getWork(item.getStart(), item.getFinish(), TimeUnit.MINUTES);
         while (item != null)
         {
            LocalDateTime startDay = LocalDateTimeHelper.getDayStartDate(item.getStart());
            LocalDateTime finishDay = LocalDateTimeHelper.getDayStartDate(item.getFinish());

            // special case - when the finishDay time is midnight, it's really the previous day...
            if (item.getFinish().equals(finishDay))
            {
               finishDay = finishDay.minusDays(1);
            }

            if (startDay.equals(finishDay))
            {
               result.add(item);
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
    * @param item timephased data
    * @param calendarWork working hours from the calendar
    * @return first day and remainder days
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
         if (calendar.isWorkingDate(LocalDateHelper.getLocalDate(itemStart)))
         {
            splitFinish = LocalTimeHelper.setEndTime(itemStart, calendar.getFinishTime(LocalDateHelper.getLocalDate(itemStart)));
            splitMinutes = calendar.getWork(itemStart, splitFinish, TimeUnit.MINUTES).getDuration();

            splitMinutes *= itemWork.getDuration();
            splitMinutes /= calendarWork.getDuration();
            splitMinutes = NumberHelper.round(splitMinutes, 2);

            Duration splitWork = Duration.getInstance(splitMinutes, TimeUnit.MINUTES);

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
   private void mergeSameDay(ProjectCalendar calendar, List<TimephasedWork> list)
   {
      List<TimephasedWork> result = new ArrayList<>();

      TimephasedWork previousItem = null;
      for (TimephasedWork item : list)
      {
         if (previousItem != null)
         {
            LocalDateTime previousItemStart = previousItem.getStart();
            LocalDateTime previousItemStartDay = LocalDateTimeHelper.getDayStartDate(previousItemStart);
            LocalDateTime itemStart = item.getStart();
            LocalDateTime itemStartDay = LocalDateTimeHelper.getDayStartDate(itemStart);

            if (previousItemStartDay.equals(itemStartDay))
            {
               Duration previousItemWork = previousItem.getTotalAmount();
               Duration itemWork = item.getTotalAmount();

               if (previousItemWork.getDuration() != 0 && itemWork.getDuration() == 0)
               {
                  continue;
               }

               result.remove(result.size() - 1);

               if (previousItemWork.getDuration() != 0 && itemWork.getDuration() != 0)
               {
                  double work = previousItem.getTotalAmount().getDuration();
                  work += item.getTotalAmount().getDuration();
                  Duration totalWork = Duration.getInstance(work, TimeUnit.MINUTES);

                  TimephasedWork merged = new TimephasedWork();
                  merged.setStart(previousItem.getStart());
                  merged.setFinish(item.getFinish());
                  merged.setTotalAmount(totalWork);
                  item = merged;
               }
               else
               {
                  if (itemWork.getDuration() == 0)
                  {
                     item = previousItem;
                  }
               }
            }

         }
         item.setAmountPerDay(item.getTotalAmount());
         result.add(item);

         Duration calendarWork = calendar.getWork(item.getStart(), item.getFinish(), TimeUnit.MINUTES);
         Duration itemWork = item.getTotalAmount();
         if (calendarWork.getDuration() == 0 && itemWork.getDuration() == 0)
         {
            result.remove(result.size() - 1);
         }
         else
         {
            previousItem = item;
         }
      }

      list.clear();
      list.addAll(result);
   }

   /**
    * Ensures that the start and end dates for ranges fit within the
    * working times for a given day.
    *
    * @param calendar current calendar
    * @param list timephased data
    */
   private void validateSameDay(ProjectCalendar calendar, List<TimephasedWork> list)
   {
      for (TimephasedWork item : list)
      {
         double totalWork = item.getTotalAmount().getDuration();

         LocalDateTime itemStart = item.getStart();
         LocalTime calendarStartTime = calendar.getStartTime(LocalDateHelper.getLocalDate(itemStart));
         LocalTime itemStartTime = LocalTimeHelper.getLocalTime(itemStart);
         if (itemStartTime != null && calendarStartTime != null)
         {
            if ((totalWork == 0 && !itemStartTime.equals(calendarStartTime)) || (itemStartTime.isBefore(calendarStartTime)))
            {
               itemStart = LocalTimeHelper.setTime(itemStart, calendarStartTime);
               item.setStart(itemStart);
            }
         }

         LocalDateTime itemFinish = item.getFinish();
         LocalTime calendarFinishTime = calendar.getFinishTime(LocalDateHelper.getLocalDate(itemFinish));
         LocalTime itemFinishTime = LocalTimeHelper.getLocalTime(itemFinish);
         if (itemFinishTime != null && calendarFinishTime != null)
         {
            if ((totalWork == 0 && !itemFinishTime.equals(calendarFinishTime)) || (calendarFinishTime != LocalTime.MIDNIGHT && itemFinishTime.isAfter(calendarFinishTime)))
            {
               itemFinish = LocalTimeHelper.setEndTime(itemFinish, calendarFinishTime);
               item.setFinish(itemFinish);
            }
         }
      }
   }

   public static final MSPDITimephasedWorkNormaliser INSTANCE = new MSPDITimephasedWorkNormaliser();
}
