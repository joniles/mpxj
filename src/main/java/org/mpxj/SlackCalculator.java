/*
 * file:       SlackCalculator.java
 * author:     Jon Iles
 * date:       2025-12-18
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

package org.mpxj;

/**
 * Classes implementing this interface provide slack calculations for a project.
 */
public interface SlackCalculator
{
   /**
    * Calculate the start slack for a task.
    *
    * @param task Task instance
    * @return start slack
    */
   Duration calculateStartSlack(Task task);

   /**
    * Calculate the finish slack for a task.
    *
    * @param task Task instance
    * @return finish slack
    */
   Duration calculateFinishSlack(Task task);

   /**
    * Calculate the free slack for a task.
    *
    * @param task Task instance
    * @return free slack
    */
   Duration calculateFreeSlack(Task task);

   /**
    * Calculate the total slack for a task.
    *
    * @param task Task instance
    * @return total slack
    */
   Duration calculateTotalSlack(Task task);
}
