package net.sf.mpxj.common;
import java.time.LocalDateTime;
import java.util.List;

import net.sf.mpxj.Duration;
import net.sf.mpxj.ProjectCalendar;
import net.sf.mpxj.TimePeriodEntity;
import net.sf.mpxj.TimeUnit;
import net.sf.mpxj.TimephasedCost;
import net.sf.mpxj.TimephasedWork;

public class NewCostNormaliser implements TimephasedNormaliser<TimephasedCost>
{
   private NewCostNormaliser()
   {

   }

   @Override public void normalise(ProjectCalendar calendar, TimePeriodEntity parent, List<TimephasedCost> list)
   {
      if (list == null)
      {
         return;
      }

      if (list.size() > 1)
      {
         mergeDays(calendar, list);
      }
   }

   private void mergeDays(ProjectCalendar calendar, List<TimephasedCost> list)
   {
      int index = 0;
      double previousItemRate = Double.MIN_VALUE;

      while (index < list.size())
      {
         TimephasedCost item = list.get(index);
         double itemWork = calendar.getWork(item.getStart(), item.getFinish(), TimeUnit.HOURS).getDuration();
         double itemAmount = NumberHelper.getDouble(item.getTotalAmount());

         if (itemAmount == 0 && itemWork == 0)
         {
            // there is zero work in this period, and zero cost in this item,
            // so it provides us with no information.
            list.remove(index);
            continue;
         }

         double itemRate = itemAmount / itemWork;
         if (itemRate != previousItemRate)
         {
            // the rate for this item is different to the previous item,
            // so we won't merge them.
            previousItemRate = itemRate;
            index++;
            continue;
         }

         TimephasedCost previousItem = list.get(index-1);
         double combinedWork = calendar.getWork(previousItem.getStart(), item.getFinish(), TimeUnit.HOURS).getDuration();
         double combinedAmount = itemAmount + NumberHelper.getDouble(previousItem.getTotalAmount());
         double combinedRate = combinedAmount / combinedWork;

         if (itemRate != combinedRate)
         {
            // combining the two items doesn't give us the same rate as the two individual items,
            // so we leave them separate.
            index++;
            continue;
         }

         // We can combine the items
         previousItem.setFinish(item.getFinish());
         previousItem.setTotalAmount(Double.valueOf(combinedAmount));
         list.remove(index);
      }
   }

   public static final NewCostNormaliser INSTANCE = new NewCostNormaliser();
}