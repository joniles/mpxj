/*
 * file:       PrimaveraCurrentDateBaselineStrategy.java
 * author:     Jon Iles
 * copyright:  (c) Packwood Software 2021
 * date:       28/04/2023
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

import net.sf.mpxj.TaskField;

/**
 * Represents the baseline strategy used by P6 when the "Earned Value Calculation"
 * method is set to "At Completion values with current dates"
 * or "Budgeted values with current dates".
 */
public class PrimaveraCurrentDateBaselineStratgey extends PrimaveraBaselineStrategy
{
   @Override protected TaskField[] getSourceFields()
   {
      return SOURCE_FIELDS;
   }

   private static final TaskField[] SOURCE_FIELDS =
      {
         TaskField.COST,
         TaskField.DURATION,
         TaskField.FINISH,
         TaskField.FIXED_COST_ACCRUAL,
         TaskField.FIXED_COST,
         TaskField.START,
         TaskField.WORK
      };
}
