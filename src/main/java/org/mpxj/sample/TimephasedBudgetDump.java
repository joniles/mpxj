package org.mpxj.sample;

import java.util.List;

import org.mpxj.LocalDateTimeRange;
import org.mpxj.ProjectFile;
import org.mpxj.ProjectProperties;
import org.mpxj.Resource;
import org.mpxj.ResourceAssignment;
import org.mpxj.Task;
import org.mpxj.TimeUnit;
import org.mpxj.TimescaleUnits;
import org.mpxj.common.TimescaleHelper;
import org.mpxj.reader.UniversalProjectReader;

public class TimephasedBudgetDump
{

   public static void main(String[] argv) throws Exception
   {
      ProjectFile file = new UniversalProjectReader().read("/Users/joniles/Downloads/budget-test-2.mpp");
      ProjectProperties props = file.getProjectProperties();

      List<LocalDateTimeRange> ranges = new TimescaleHelper().createTimescale(props.getStartDate(), props.getFinishDate(), TimescaleUnits.DAYS);

      for(Task task : file.getTasks())
      {
         System.out.println(task);
         System.out.println("Budget Work\t" + task.getTimephasedBudgetWork(ranges, TimeUnit.HOURS));
         System.out.println("Budget Cost\t" + task.getTimephasedBudgetCost(ranges));
//         System.out.println("       Cost\t" + task.getTimephasedCost(ranges));
//         System.out.println("       Work\t" + task.getTimephasedWork(ranges, TimeUnit.HOURS));
         System.out.println();

         for (ResourceAssignment assignment : task.getResourceAssignments())
         {
            System.out.println(assignment);
            System.out.println("Budget Work\t" + assignment.getTimephasedBudgetWork(ranges, TimeUnit.HOURS));
            System.out.println("Budget Cost\t" + assignment.getTimephasedBudgetCost(ranges));
//            System.out.println("       Cost\t" + assignment.getTimephasedCost(ranges));
//            System.out.println("       Work\t" + assignment.getTimephasedWork(ranges, TimeUnit.HOURS));
         }
      }
   }
}
