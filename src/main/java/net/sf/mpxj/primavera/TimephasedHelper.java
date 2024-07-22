package net.sf.mpxj.primavera;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
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

      List<TimephasedWork> list = new ArrayList<TimephasedWork>();
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

         if (workHours.getDuration() != 0)
         {
            double days = calendar.getDuration(currentStart, currentFinish).getDuration();

            TimephasedWork timephasedItem = new TimephasedWork();
            timephasedItem.setStart(currentStart);
            timephasedItem.setFinish(currentFinish);
            timephasedItem.setTotalAmount(workHours);
            timephasedItem.setAmountPerDay(Duration.getInstance(workHours.getDuration()/days, TimeUnit.HOURS));
            list.add(timephasedItem);
         }

         // TODO: get this to work for round-trip XER/PMXML read/write
         // BUT: add a flag which prevents export to MSPDI until we have sorted out normalisation and correct MSPDI export
         // CHECK: that the segmentation tools give the expected result
         
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
}
