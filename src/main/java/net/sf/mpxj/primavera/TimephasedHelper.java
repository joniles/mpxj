package net.sf.mpxj.primavera;

import java.text.DecimalFormat;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import net.sf.mpxj.Duration;
import net.sf.mpxj.ProjectCalendar;
import net.sf.mpxj.TimeUnit;
import net.sf.mpxj.TimephasedWork;
import net.sf.mpxj.TimephasedWorkContainer;

final class TimephasedHelper
{
   public static TimephasedWorkContainer read(ProjectCalendar calendar, LocalDateTime start, String values)
   {
      if (values == null || values.isEmpty())
      {
         return null;
      }

      if (values.indexOf(':') == -1)
      {
         return null;
      }

      List<TimephasedWork> list = new ArrayList<>();
      LocalDateTime currentStart = calendar.getNextWorkStart(start);

      for (String value : values.split(";"))
      {
         String[] item = value.split(":");
         if (item.length != 2)
         {
            return null;
         }

         Duration workHours = Duration.getInstance(Double.valueOf(item[0]), TimeUnit.HOURS);
         Duration periodHours = Duration.getInstance(Double.valueOf(item[1]), TimeUnit.HOURS);
         LocalDateTime currentFinish = calendar.getDate(currentStart, periodHours);
         Duration calendarHours = calendar.getWork(currentStart, currentFinish, TimeUnit.HOURS);

         if (workHours.getDuration() != 0 || calendarHours.getDuration() != 0)
         {
            double days = calendar.getDuration(currentStart, currentFinish).getDuration();

            TimephasedWork timephasedItem = new TimephasedWork();
            timephasedItem.setStart(currentStart);
            timephasedItem.setFinish(currentFinish);
            timephasedItem.setTotalAmount(workHours);
            timephasedItem.setAmountPerDay(Duration.getInstance(workHours.getDuration()/days, TimeUnit.HOURS));
            list.add(timephasedItem);
         }

         currentStart = calendar.getNextWorkStart(currentFinish);
      }

      return new TimephasedWorkContainer()
      {
         @Override public List<TimephasedWork> getData()
         {
            return list;
         }

         @Override public boolean hasData()
         {
            return true;
         }

         @Override public TimephasedWorkContainer applyFactor(double perDayFactor, double totalFactor)
         {
            throw new UnsupportedOperationException();
         }
      };
   }

   public static String write(ProjectCalendar calendar, List<TimephasedWork> items)
   {
      if (items == null || items.isEmpty())
      {
         return null;
      }

      StringBuilder result = new StringBuilder();
      LocalDateTime previousFinish = null;

      for (TimephasedWork item : items)
      {
         if (previousFinish != null)
         {
            Duration workToNextItem = calendar.getWork(previousFinish, item.getStart(), TimeUnit.HOURS);
            if(workToNextItem.getDuration() != 0)
            {
               if (result.length() != 0)
               {
                  result.append(";");
               }

               result.append("0:");
               result.append((int)workToNextItem.getDuration());
            }
         }

         Duration workHours = item.getTotalAmount().convertUnits(TimeUnit.HOURS, calendar);
         Duration periodHours = calendar.getWork(item.getStart(), item.getFinish(), TimeUnit.HOURS);

         if (result.length() != 0)
         {
            result.append(";");
         }

         result.append(FORMAT.format(workHours.getDuration()));
         result.append(':');
         result.append(FORMAT.format(periodHours.getDuration()));

         previousFinish = item.getFinish();
      }

      return result.toString();
   }

   private static final DecimalFormat FORMAT = new DecimalFormat("#.#");
}
