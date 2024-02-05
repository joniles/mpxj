package net.sf.mpxj.common;
import java.time.LocalDateTime;
import java.util.List;

import net.sf.mpxj.Duration;
import net.sf.mpxj.ProjectCalendar;
import net.sf.mpxj.TimePeriodEntity;
import net.sf.mpxj.TimeUnit;
import net.sf.mpxj.TimephasedWork;

//
// TODO: why are the units changing? Consider logic for tidying up start/end dates - next work start etc
//
public class NewWorkNormaliser implements TimephasedNormaliser<TimephasedWork>
{
   @Override public void normalise(ProjectCalendar calendar, TimePeriodEntity parent, List<TimephasedWork> list)
   {
      if (list == null)
      {
         return;
      }

      if (list.size() > 1)
      {
         mergeDays(calendar, list);
      }

      normaliseStarts(calendar, list);
      normaliseFinishes(calendar, list);
   }

   private void mergeDays(ProjectCalendar calendar, List<TimephasedWork> list)
   {
      int index = 0;
      boolean lastItemIsStandard = false;

      while (index < list.size())
      {
         TimephasedWork item = list.get(index);
         Duration itemWork = item.getTotalAmount() == null ? Duration.getInstance(0, TimeUnit.HOURS) : item.getTotalAmount();
         Duration calendarWork = calendar.getWork(item.getStart(), item.getFinish(), itemWork.getUnits());
         if (itemWork.equals(calendarWork))
         {
            if (itemWork.getDuration() == 0.0)
            {
               // We have a zero duration item which agrees with the calendar,
               // so we can remove it as it provides no useful information.
               list.remove(index);
            }
            else
            {
               if (lastItemIsStandard)
               {
                  TimephasedWork lastItem = list.get(index-1);
                  Duration lastItemWork = lastItem.getTotalAmount() == null ? Duration.getInstance(0, TimeUnit.HOURS) : lastItem.getTotalAmount().convertUnits(itemWork.getUnits(), calendar);
                  double combinedWork = itemWork.getDuration() + lastItemWork.getDuration();
                  Duration combinedCalendarWork = calendar.getWork(lastItem.getStart(), item.getFinish(), itemWork.getUnits());
                  if (combinedCalendarWork.getDuration() == combinedWork)
                  {
                     lastItem.setFinish(item.getFinish());
                     lastItem.setTotalAmount(Duration.getInstance(combinedWork, itemWork.getUnits()));
                     lastItemIsStandard = true;
                     list.remove(index);
                  }
                  else
                  {
                     // We can't merge with the previous item, but this item is
                     // standard, so we leave it in the list and move forward.
                     index++;
                     lastItemIsStandard = true;
                  }
               }
               else
               {
                  // We can't merge with the previous item, but this item is
                  // standard, so we leave it in the list and move forward.
                  index++;
                  lastItemIsStandard = true;
               }
            }
         }
         else
         {
            index++;
            lastItemIsStandard = false;
         }
      }
   }

   private void normaliseStarts(ProjectCalendar calendar, List<TimephasedWork> list)
   {
      for (TimephasedWork item : list)
      {
         LocalDateTime nextWorkStart = calendar.getNextWorkStart(item.getStart());
         if (item.getStart().isEqual(nextWorkStart) || nextWorkStart.isAfter(item.getFinish()))
         {
            continue;
         }

         Duration calendarWork = calendar.getWork(nextWorkStart, item.getFinish(), item.getTotalAmount().getUnits());
         if (calendarWork.getDuration() == item.getTotalAmount().getDuration())
         {
            item.setStart(nextWorkStart);
         }
      }
   }

   private void normaliseFinishes(ProjectCalendar calendar, List<TimephasedWork> list)
   {
      for (TimephasedWork item : list)
      {
         LocalDateTime previousWorkFinish = calendar.getPreviousWorkFinish(item.getFinish());
         if (item.getStart().isEqual(previousWorkFinish) || item.getStart().isAfter(previousWorkFinish))
         {
            continue;
         }

         Duration calendarWork = calendar.getWork(item.getStart(), previousWorkFinish, item.getTotalAmount().getUnits());
         if (calendarWork.getDuration() == item.getTotalAmount().getDuration())
         {
            item.setFinish(previousWorkFinish);
         }
      }
   }

   public static final NewWorkNormaliser INSTANCE = new NewWorkNormaliser();
}
