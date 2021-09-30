/*
 * file:       BaselineManager.java
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

package net.sf.mpxj.primavera;

import net.sf.mpxj.DefaultBaselineStrategy;
import net.sf.mpxj.TaskField;

/**
 * Handles setting baseline fields in one project using values read from another project.
 */
public class PrimaveraBaselineStrategy extends DefaultBaselineStrategy
{
   @Override protected TaskField[] getSourceFields()
   {
      return SOURCE_FIELDS;
   }

   private static final TaskField[] SOURCE_FIELDS =
   {
      TaskField.PLANNED_COST,
      TaskField.PLANNED_DURATION,
      TaskField.PLANNED_FINISH,
      TaskField.FIXED_COST_ACCRUAL,
      TaskField.FIXED_COST,
      TaskField.PLANNED_START,
      TaskField.PLANNED_WORK
   };
}
