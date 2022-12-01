package net.sf.mpxj.common;
import net.sf.mpxj.Duration;
import net.sf.mpxj.Task;

public final class SlackHelper
{

   public static void inferSlack(Task task)
   {
      if (task.getTotalSlack() != null)
      {
         Duration startSlack = null;
         Duration finishSlack = null;
         Duration totalSlack = task.getTotalSlack();
         Duration zeroSlack = Duration.getInstance(0, totalSlack.getUnits());

         if (task.getActualFinish() == null)
         {
            finishSlack = totalSlack;
            if (task.getActualStart() == null)
            {
               startSlack = totalSlack;
            }
            else
            {
               startSlack = zeroSlack;
            }
         }
         else
         {
            startSlack = zeroSlack;
            finishSlack = zeroSlack;
            totalSlack = zeroSlack;
         }

         task.setStartSlack(startSlack);
         task.setFinishSlack(finishSlack);
         task.setTotalSlack(totalSlack);
      }
   }
}
