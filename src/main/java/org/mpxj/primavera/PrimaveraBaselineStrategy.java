/*
 * file:       PrimaveraBaselineStrategy.java
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

package org.mpxj.primavera;

import org.mpxj.AbstractBaselineStrategy;
import org.mpxj.Task;
import org.mpxj.TaskField;

/**
 * Strategies used to assign baselines for Primavera schedules.
 */
public final class PrimaveraBaselineStrategy extends AbstractBaselineStrategy
{
   /**
    * Private constructor.
    *
    * @param sourceFields baseline source fields
    */
   private PrimaveraBaselineStrategy(TaskField[] sourceFields)
   {
      m_sourceFields = sourceFields;
   }

   @Override protected TaskField[] getSourceFields()
   {
      return m_sourceFields;
   }

   @Override protected Object getKeyForTask(Task task)
   {
      String activityID = task.getCanonicalActivityID();

      // For Activities, the Activity ID is sufficient to uniquely identify a
      // task. For WBS entries the value in Activity ID may not be unique.
      // For WBS entries we include an additional value to get around this.
      return task.getSummary() ? activityID + " " + task.getOutlineLevel() : activityID;
   }

   private final TaskField[] m_sourceFields;

   public static final PrimaveraBaselineStrategy PLANNED_ATTRIBUTES = new PrimaveraBaselineStrategy(new TaskField[]
   {
      TaskField.PLANNED_COST,
      TaskField.PLANNED_DURATION,
      TaskField.PLANNED_FINISH,
      TaskField.FIXED_COST_ACCRUAL,
      TaskField.FIXED_COST,
      TaskField.PLANNED_START,
      TaskField.PLANNED_WORK
   });

   public static final PrimaveraBaselineStrategy CURRENT_ATTRIBUTES = new PrimaveraBaselineStrategy(new TaskField[]
   {
      TaskField.COST,
      TaskField.DURATION,
      TaskField.FINISH,
      TaskField.FIXED_COST_ACCRUAL,
      TaskField.FIXED_COST,
      TaskField.START,
      TaskField.WORK
   });
}
