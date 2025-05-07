/*
 * file:       WorkHelper.java
 * author:     Jon Iles
 * date:       2024-03-02
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

package org.mpxj.primavera;

import java.util.Arrays;
import java.util.Objects;

import org.mpxj.Duration;
import org.mpxj.Task;
import org.mpxj.TimeUnit;

/**
 * Helper methods for handling work values.
 */
class WorkHelper
{
   /**
    * Sum a number of work values, ignoring null values.
    *
    * @param values values to sum
    * @return result
    */
   public static Duration addWork(Duration... values)
   {
      return Duration.getInstance(Arrays.stream(values).filter(Objects::nonNull).mapToDouble(Duration::getDuration).sum(), TimeUnit.HOURS);
   }

   /**
    * Return the supplied duration, or a zero duration if null.
    *
    * @param value duration value or null
    * @return duration value
    */
   public static Duration zeroIfNull(Duration value)
   {
      return value == null ? Duration.getInstance(0, TimeUnit.HOURS) : value;
   }

   /**
    * Determine the actual work value for labor. See the getWork method for details.
    *
    * @param task Task instance
    * @return actual work value
    */
   public static Duration getActualWorkLabor(Task task)
   {
      return getWork(task.getActualWork(), task.getActualWorkLabor(), task.getActualWorkNonlabor());
   }

   /**
    * Determine the planned work value for labor. See the getWork method for details.
    *
    * @param task Task instance
    * @return actual work value
    */
   public static Duration getPlannedWorkLabor(Task task)
   {
      return getWork(task.getPlannedWork(), task.getPlannedWorkLabor(), task.getPlannedWorkNonlabor());
   }

   /**
    * Determine the remaining work value for labor. See the getWork method for details.
    *
    * @param task Task instance
    * @return actual work value
    */
   public static Duration getRemainingWorkLabor(Task task)
   {
      return getWork(task.getRemainingWork(), task.getRemainingWorkLabor(), task.getRemainingWorkNonlabor());
   }

   /**
    * Activities in P6 schedules include separate summary values for labor and nonlabor work.
    * If the schedule we are writing originated as a P6 schedule, we wil already have these
    * as separate values If the schedule we are writing did not originate in P6, we will just
    * have a single work value. We'll use a heuristic here to determine which situation we're in:
    * if we have both labor and nonlabor values, we'll use them as they are. If we only have a summary
    * work value we'll assume that this represents labor value, and we'll leave the nonlabor value as zero.
    *
    * @param total summary work value
    * @param labor labor work value
    * @param nonlabor nonlabor work value
    * @return work value
    */
   private static Duration getWork(Duration total, Duration labor, Duration nonlabor)
   {
      if (total == null && labor == null && nonlabor == null)
      {
         return Duration.getInstance(0, TimeUnit.HOURS);
      }

      if (total != null && labor == null && nonlabor == null)
      {
         return total;
      }

      return zeroIfNull(labor);
   }
}
