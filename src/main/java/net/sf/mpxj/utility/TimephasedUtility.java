/*
 * file:       TimephasedUtility.java
 * author:     Jon Iles
 * copyright:  (c) Packwood Software 2011
 * date:       2011-02-12
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

package net.sf.mpxj.utility;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import net.sf.mpxj.DateRange;
import net.sf.mpxj.Duration;
import net.sf.mpxj.ProjectCalendar;
import net.sf.mpxj.ProjectFile;
import net.sf.mpxj.TimeUnit;
import net.sf.mpxj.TimephasedCost;
import net.sf.mpxj.TimephasedItem;
import net.sf.mpxj.TimephasedWork;
import net.sf.mpxj.common.DateHelper;
import net.sf.mpxj.common.NumberHelper;
import net.sf.mpxj.mpp.TimescaleUnits;

/**
 * This class contains methods relating to manipulating timephased data.
 */
public final class TimephasedUtility
{
   /**
    * This is the main entry point used to convert the internal representation
    * of timephased work into an external form which can
    * be displayed to the user.
    *
    * @param projectCalendar calendar used by the resource assignment
    * @param work timephased resource assignment data
    * @param rangeUnits timescale units
    * @param dateList timescale date ranges
    * @return list of durations, one per timescale date range
    */
   public ArrayList<Duration> segmentWork(ProjectCalendar projectCalendar, List<TimephasedWork> work, TimescaleUnits rangeUnits, List<DateRange> dateList)
   {
      ArrayList<Duration> result = new ArrayList<Duration>(dateList.size());
      int lastStartIndex = 0;

      //
      // Iterate through the list of dates range we are interested in.
      // Each date range in this list corresponds to a column
      // shown on the "timescale" view by MS Project
      //
      for (DateRange range : dateList)
      {
         //
         // If the current date range does not intersect with any of the
         // assignment date ranges in the list, then we show a zero
         // duration for this date range.
         //
         int startIndex = lastStartIndex == -1 ? -1 : getStartIndex(range, work, lastStartIndex);
         if (startIndex == -1)
         {
            result.add(Duration.getInstance(0, TimeUnit.HOURS));
         }
         else
         {
            //
            // We have found an assignment which intersects with the current
            // date range, call the method below to determine how
            // much time from this resource assignment can be allocated
            // to the current date range.
            //
            result.add(getRangeDuration(projectCalendar, rangeUnits, range, work, startIndex));
            lastStartIndex = startIndex;
         }
      }

      return result;
   }

   /**
    * This is the main entry point used to convert the internal representation
    * of timephased baseline work into an external form which can
    * be displayed to the user.
    *
    * @param file parent project file
    * @param work timephased resource assignment data
    * @param rangeUnits timescale units
    * @param dateList timescale date ranges
    * @return list of durations, one per timescale date range
    */
   public ArrayList<Duration> segmentBaselineWork(ProjectFile file, List<TimephasedWork> work, TimescaleUnits rangeUnits, ArrayList<DateRange> dateList)
   {
      return segmentWork(file.getBaselineCalendar(), work, rangeUnits, dateList);
   }

   /**
    * This is the main entry point used to convert the internal representation
    * of timephased cost into an external form which can
    * be displayed to the user.
    *
    * @param projectCalendar calendar used by the resource assignment
    * @param cost timephased resource assignment data
    * @param rangeUnits timescale units
    * @param dateList timescale date ranges
    * @return list of durations, one per timescale date range
    */
   public ArrayList<Double> segmentCost(ProjectCalendar projectCalendar, List<TimephasedCost> cost, TimescaleUnits rangeUnits, ArrayList<DateRange> dateList)
   {
      ArrayList<Double> result = new ArrayList<Double>(dateList.size());
      int lastStartIndex = 0;

      //
      // Iterate through the list of dates range we are interested in.
      // Each date range in this list corresponds to a column
      // shown on the "timescale" view by MS Project
      //
      for (DateRange range : dateList)
      {
         //
         // If the current date range does not intersect with any of the
         // assignment date ranges in the list, then we show a zero
         // duration for this date range.
         //
         int startIndex = lastStartIndex == -1 ? -1 : getStartIndex(range, cost, lastStartIndex);
         if (startIndex == -1)
         {
            result.add(NumberHelper.DOUBLE_ZERO);
         }
         else
         {
            //
            // We have found an assignment which intersects with the current
            // date range, call the method below to determine how
            // much time from this resource assignment can be allocated
            // to the current date range.
            //
            result.add(getRangeCost(projectCalendar, rangeUnits, range, cost, startIndex));
            lastStartIndex = startIndex;
         }
      }

      return result;
   }

   /**
    * This is the main entry point used to convert the internal representation
    * of timephased baseline cost into an external form which can
    * be displayed to the user.
    *
    * @param file parent project file
    * @param cost timephased resource assignment data
    * @param rangeUnits timescale units
    * @param dateList timescale date ranges
    * @return list of durations, one per timescale date range
    */
   public ArrayList<Double> segmentBaselineCost(ProjectFile file, List<TimephasedCost> cost, TimescaleUnits rangeUnits, ArrayList<DateRange> dateList)
   {
      return segmentCost(file.getBaselineCalendar(), cost, rangeUnits, dateList);
   }

   /**
    * Used to locate the first timephased resource assignment block which
    * intersects with the target date range.
    *
    * @param <T> payload type
    * @param range target date range
    * @param assignments timephased resource assignments
    * @param startIndex index at which to start the search
    * @return index of timephased resource assignment which intersects with the target date range
    */
   private <T extends TimephasedItem<?>> int getStartIndex(DateRange range, List<T> assignments, int startIndex)
   {
      int result = -1;
      if (assignments != null)
      {
         long rangeStart = range.getStart().getTime();
         long rangeEnd = range.getEnd().getTime();

         for (int loop = startIndex; loop < assignments.size(); loop++)
         {
            T assignment = assignments.get(loop);
            int compareResult = DateHelper.compare(assignment.getStart(), assignment.getFinish(), rangeStart);

            //
            // The start of the target range falls after the assignment end -
            // move on to test the next assignment.
            //
            if (compareResult > 0)
            {
               continue;
            }

            //
            // The start of the target range  falls within the assignment -
            // return the index of this assignment to the caller.
            //
            if (compareResult == 0)
            {
               result = loop;
               break;
            }

            //
            // At this point, we know that the start of the target range is before
            // the assignment start. We need to determine if the end of the
            // target range overlaps the assignment.
            //
            compareResult = DateHelper.compare(assignment.getStart(), assignment.getFinish(), rangeEnd);
            if (compareResult >= 0)
            {
               result = loop;
               break;
            }
         }
      }
      return result;
   }

   /**
    * For a given date range, determine the duration of work, based on the
    * timephased resource assignment data.
    *
    * @param projectCalendar calendar used for the resource assignment calendar
    * @param rangeUnits timescale units
    * @param range target date range
    * @param assignments timephased resource assignments
    * @param startIndex index at which to start searching through the timephased resource assignments
    * @return work duration
    */
   private Duration getRangeDuration(ProjectCalendar projectCalendar, TimescaleUnits rangeUnits, DateRange range, List<TimephasedWork> assignments, int startIndex)
   {
      Duration result;

      switch (rangeUnits)
      {
         case MINUTES:
         case HOURS:
         {
            result = getRangeDurationSubDay(projectCalendar, rangeUnits, range, assignments, startIndex);
            break;
         }

         default:
         {
            result = getRangeDurationWholeDay(projectCalendar, rangeUnits, range, assignments, startIndex);
            break;
         }
      }

      return result;
   }

   /**
    * For a given date range, determine the duration of work, based on the
    * timephased resource assignment data.
    *
    * This method deals with timescale units of less than a day.
    *
    * @param projectCalendar calendar used for the resource assignment calendar
    * @param rangeUnits timescale units
    * @param range target date range
    * @param assignments timephased resource assignments
    * @param startIndex index at which to start searching through the timephased resource assignments
    * @return work duration
    */
   private Duration getRangeDurationSubDay(ProjectCalendar projectCalendar, TimescaleUnits rangeUnits, DateRange range, List<TimephasedWork> assignments, int startIndex)
   {
      throw new UnsupportedOperationException("Please request this functionality from the MPXJ maintainer");
   }

   /**
    * For a given date range, determine the duration of work, based on the
    * timephased resource assignment data.
    *
    * This method deals with timescale units of one day or more.
    *
    * @param projectCalendar calendar used for the resource assignment calendar
    * @param rangeUnits timescale units
    * @param range target date range
    * @param assignments timephased resource assignments
    * @param startIndex index at which to start searching through the timephased resource assignments
    * @return work duration
    */
   private Duration getRangeDurationWholeDay(ProjectCalendar projectCalendar, TimescaleUnits rangeUnits, DateRange range, List<TimephasedWork> assignments, int startIndex)
   {
      // option 1:
      // Our date range starts before the start of the TRA at the start index.
      // We can guarantee that we don't need to look at any earlier TRA blocks so just start here

      // option 2:
      // Our date range starts at the same point as the first TRA: do nothing...

      // option 3:
      // Our date range starts somewhere inside the first TRA...

      // if it's option 1 just set the start date to the start of the TRA block
      // for everything else we just use the start date of our date range.
      // start counting forwards one day at a time until we reach the end of
      // the date range, or until we reach the end of the block.

      // if we have not reached the end of the range, move to the next block and
      // see if the date range overlaps it. if it does not overlap, then we're
      // done.

      // if it does overlap, then move to the next block and repeat

      int totalDays = 0;
      double totalWork = 0;
      TimephasedWork assignment = assignments.get(startIndex);
      boolean done = false;

      do
      {
         //
         // Select the correct start date
         //
         long startDate = range.getStart().getTime();
         long assignmentStart = assignment.getStart().getTime();
         if (startDate < assignmentStart)
         {
            startDate = assignmentStart;
         }

         long rangeEndDate = range.getEnd().getTime();
         long traEndDate = assignment.getFinish().getTime();

         Calendar cal = Calendar.getInstance();
         cal.setTimeInMillis(startDate);
         Date calendarDate = cal.getTime();

         //
         // Start counting forwards
         //
         while (startDate < rangeEndDate && startDate < traEndDate)
         {
            if (projectCalendar == null || projectCalendar.isWorkingDate(calendarDate))
            {
               ++totalDays;
            }
            cal.add(Calendar.DAY_OF_YEAR, 1);
            startDate = cal.getTimeInMillis();
            calendarDate = cal.getTime();
         }

         //
         // If we still haven't reached the end of our range
         // check to see if the next TRA can be used.
         //
         done = true;
         totalWork += (assignment.getAmountPerDay().getDuration() * totalDays);
         if (startDate < rangeEndDate)
         {
            ++startIndex;
            if (startIndex < assignments.size())
            {
               assignment = assignments.get(startIndex);
               totalDays = 0;
               done = false;
            }
         }
      }
      while (!done);

      return Duration.getInstance(totalWork, assignment.getAmountPerDay().getUnits());
   }

   /**
    * For a given date range, determine the cost, based on the
    * timephased resource assignment data.
    *
    * @param projectCalendar calendar used for the resource assignment calendar
    * @param rangeUnits timescale units
    * @param range target date range
    * @param assignments timephased resource assignments
    * @param startIndex index at which to start searching through the timephased resource assignments
    * @return work duration
    */
   private Double getRangeCost(ProjectCalendar projectCalendar, TimescaleUnits rangeUnits, DateRange range, List<TimephasedCost> assignments, int startIndex)
   {
      Double result;

      switch (rangeUnits)
      {
         case MINUTES:
         case HOURS:
         {
            result = getRangeCostSubDay(projectCalendar, rangeUnits, range, assignments, startIndex);
            break;
         }

         default:
         {
            result = getRangeCostWholeDay(projectCalendar, rangeUnits, range, assignments, startIndex);
            break;
         }
      }

      return result;
   }

   /**
    * For a given date range, determine the cost, based on the
    * timephased resource assignment data.
    *
    * This method deals with timescale units of one day or more.
    *
    * @param projectCalendar calendar used for the resource assignment calendar
    * @param rangeUnits timescale units
    * @param range target date range
    * @param assignments timephased resource assignments
    * @param startIndex index at which to start searching through the timephased resource assignments
    * @return work duration
    */
   private Double getRangeCostWholeDay(ProjectCalendar projectCalendar, TimescaleUnits rangeUnits, DateRange range, List<TimephasedCost> assignments, int startIndex)
   {
      int totalDays = 0;
      double totalCost = 0;
      TimephasedCost assignment = assignments.get(startIndex);
      boolean done = false;

      do
      {
         //
         // Select the correct start date
         //
         long startDate = range.getStart().getTime();
         long assignmentStart = assignment.getStart().getTime();
         if (startDate < assignmentStart)
         {
            startDate = assignmentStart;
         }

         long rangeEndDate = range.getEnd().getTime();
         long traEndDate = assignment.getFinish().getTime();

         Calendar cal = Calendar.getInstance();
         cal.setTimeInMillis(startDate);
         Date calendarDate = cal.getTime();

         //
         // Start counting forwards
         //
         while (startDate < rangeEndDate && startDate < traEndDate)
         {
            if (projectCalendar == null || projectCalendar.isWorkingDate(calendarDate))
            {
               ++totalDays;
            }
            cal.add(Calendar.DAY_OF_YEAR, 1);
            startDate = cal.getTimeInMillis();
            calendarDate = cal.getTime();
         }

         //
         // If we still haven't reached the end of our range
         // check to see if the next TRA can be used.
         //
         done = true;
         totalCost += (assignment.getAmountPerDay().doubleValue() * totalDays);
         if (startDate < rangeEndDate)
         {
            ++startIndex;
            if (startIndex < assignments.size())
            {
               assignment = assignments.get(startIndex);
               totalDays = 0;
               done = false;
            }
         }
      }
      while (!done);

      return Double.valueOf(totalCost);
   }

   /**
    * For a given date range, determine the cost, based on the
    * timephased resource assignment data.
    *
    * This method deals with timescale units of less than a day.
    *
    * @param projectCalendar calendar used for the resource assignment calendar
    * @param rangeUnits timescale units
    * @param range target date range
    * @param assignments timephased resource assignments
    * @param startIndex index at which to start searching through the timephased resource assignments
    * @return work duration
    */
   private Double getRangeCostSubDay(ProjectCalendar projectCalendar, TimescaleUnits rangeUnits, DateRange range, List<TimephasedCost> assignments, int startIndex)
   {
      throw new UnsupportedOperationException("Please request this functionality from the MPXJ maintainer");
   }
}
