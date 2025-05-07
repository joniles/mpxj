/*
 * file:       BaselineStrategy.java
 * author:     Jon Iles
 * date:       2021-09-30
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
 * Classes implementing this interface manage population of baseline attributes
 * in one schedule by comparing it to another schedule.
 */
public interface BaselineStrategy
{

   /**
    * Clear the requested baseline for the supplied project.
    *
    * @param project target project
    * @param index baseline to populate (0-10)
    */
   void clearBaseline(ProjectFile project, int index);

   /**
    * Use the supplied baseline project to set the baselineN cost, duration, finish,
    * fixed cost accrual, fixed cost, start and work attributes for the tasks
    * in the supplied project.
    * <p/>
    * The index argument selects which of the 10 baselines to populate. Passing
    * an index of 0 populates the main baseline.
    *
    * @param project target project
    * @param baseline baseline project
    * @param index baseline to populate (0-10)
    */
   void populateBaseline(ProjectFile project, ProjectFile baseline, int index);
}