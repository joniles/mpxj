/*
 * file:       AstaBaselineStrategy.java
 * author:     Jon Iles
 * copyright:  (c) Packwood Software 2021
 * date:       23/02/2021
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

package net.sf.mpxj.asta;

import net.sf.mpxj.DefaultBaselineStrategy;
import net.sf.mpxj.Task;

/**
 * Strategy used to assign baselines for Asta schedules.
 */
public class AstaBaselineStrategy extends DefaultBaselineStrategy
{
   @Override protected Object getKeyForTask(Task task)
   {
      // It looks like Powerproject uses a single ID generator for all entities,
      // so we should be able to match on Unique ID only (no overlap between bar, task, milestone etc.)
      // To be on the safe side we'll build a key which includes the summary and milestone flags.
      return task.getUniqueID() + ":" + task.getSummary() + ":" + task.getMilestone();
   }
}
