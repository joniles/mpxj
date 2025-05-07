/*
 * file:       SlackHelper.java
 * author:     Jon Iles
 * copyright:  (c) Packwood Software 2022
 * date:       2022-12-06
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

package org.mpxj.common;

import org.mpxj.Duration;
import org.mpxj.Task;

/**
 * This class provides functionality to assist calculating slack values.
 */
public final class SlackHelper
{
   /**
    * Given a task with only a total slack value,
    * infer and set the appropriate values for start and finish slack.
    *
    * @param task target task
    */
   public static void inferSlack(Task task)
   {
      if (task.getTotalSlack() != null)
      {
         Duration startSlack;
         Duration finishSlack;
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
