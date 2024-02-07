package net.sf.mpxj.common;
import java.time.LocalDateTime;
import java.util.List;

import net.sf.mpxj.Duration;
import net.sf.mpxj.ProjectCalendar;
import net.sf.mpxj.TimePeriodEntity;
import net.sf.mpxj.TimeUnit;
import net.sf.mpxj.TimephasedWork;

public class NewWorkNormaliser implements TimephasedNormaliser<TimephasedWork>
{
   private NewWorkNormaliser()
   {

   }
   @Override public void normalise(ProjectCalendar calendar, TimePeriodEntity parent, List<TimephasedWork> list)
   {
      if (list == null)
      {
         return;
      }

      System.out.println("Initial Items");
      list.forEach(System.out::println);
      System.out.println();

      if (list.size() > 1)
      {
         mergeDays(calendar, list);

         System.out.println();
         System.out.println("Merged Items");
         list.forEach(System.out::println);
         System.out.println();
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
         System.out.println("Processing: " + item);

         Duration itemWork = item.getTotalAmount() == null ? Duration.getInstance(0, TimeUnit.HOURS) : item.getTotalAmount();
         Duration calendarWork = calendar.getWork(item.getStart(), item.getFinish(), itemWork.getUnits());
         if (itemWork.equals(calendarWork))
         {
            System.out.println("Item work equals calendar work");
            if (itemWork.getDuration() == 0.0)
            {
               // We have a zero duration item which agrees with the calendar,
               // so we can remove it as it provides no useful information.
               list.remove(index);
               System.out.println("Removing: item and calendar have zero work: " + item);
            }
            else
            {
               if (lastItemIsStandard)
               {
                  TimephasedWork lastItem = list.get(index-1);
                  System.out.println("Last item is standard: " + lastItem);
                  Duration lastItemWork = lastItem.getTotalAmount() == null ? Duration.getInstance(0, TimeUnit.HOURS) : lastItem.getTotalAmount().convertUnits(itemWork.getUnits(), calendar);
                  double combinedWork = itemWork.getDuration() + lastItemWork.getDuration();
                  Duration combinedCalendarWork = calendar.getWork(lastItem.getStart(), item.getFinish(), itemWork.getUnits());
                  if (combinedCalendarWork.getDuration() == combinedWork)
                  {
                     System.out.println("Combining: " + lastItem + " and " + item);
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
                     System.out.println("Leaving unchanged: " + item);
                  }
               }
               else
               {
                  System.out.println("Last item is not standard, leaving unchanged: " + item);
                  // We can't merge with the previous item, but this item is
                  // standard, so we leave it in the list and move forward.
                  index++;
                  lastItemIsStandard = true;
               }
            }
         }
         else
         {
            System.out.println("Item work is not equal to calendar work, leaving unchanged " + item);
            index++;
            lastItemIsStandard = false;
         }
      }
   }

   private void normaliseStarts(ProjectCalendar calendar, List<TimephasedWork> list)
   {
      System.out.println("Normalising Starts");
      for (TimephasedWork item : list)
      {
         System.out.println("Item: " + item);

         LocalDateTime nextWorkStart = calendar.getNextWorkStart(item.getStart());
         if (item.getStart().isEqual(nextWorkStart) || nextWorkStart.isAfter(item.getFinish()))
         {
            if (item.getStart().isEqual(nextWorkStart))
            {
               System.out.println("Item alreday at next work start");
            }
            else
            {
               System.out.println("Next work strat after item finish");
            }
            continue;
         }


         LocalDateTime previousWorkFinish = calendar.getPreviousWorkFinish(item.getStart());
         System.out.println("Item Start: " + item.getStart() + " prev work finish: " + previousWorkFinish + " next work start:" + nextWorkStart);
         if (item.getStart().isEqual(previousWorkFinish))
         {
            System.out.println("Updating");
            item.setStart(nextWorkStart);
         }
//         Duration calendarWork = calendar.getWork(nextWorkStart, item.getFinish(), item.getTotalAmount().getUnits());
//         System.out.println("Item Work:" + item.getTotalAmount() + " Calendar Work: " + calendarWork);
//         if (calendarWork.getDuration() == item.getTotalAmount().getDuration())
//         {
//            System.out.println("Work matches, updating start");
//            item.setStart(nextWorkStart);
//         }
//         else
//         {
//            System.out.println("Work does not match");
//         }
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

//         if (item.getFinish().isEqual(calendar.getNextWorkStart(item.getFinish())))
//         {
//            item.setFinish(previousWorkFinish);
//         }

         Duration calendarWork = calendar.getWork(item.getStart(), previousWorkFinish, item.getTotalAmount().getUnits());
         if (calendarWork.getDuration() == item.getTotalAmount().getDuration())
         {
            item.setFinish(previousWorkFinish);
         }
      }
   }

   public static final NewWorkNormaliser INSTANCE = new NewWorkNormaliser();
}
