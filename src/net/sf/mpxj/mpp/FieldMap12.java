/*
 * file:       FieldMap12.java
 * author:     Jon Iles
 * copyright:  (c) Packwood Software 2011
 * date:       13/04/2010
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

package net.sf.mpxj.mpp;

import net.sf.mpxj.AssignmentField;
import net.sf.mpxj.ProjectFile;

/**
 * MPP12 field map.
 */
class FieldMap12 extends FieldMap
{
   /**
    * Constructor.
    * 
    * @param file parent file
    */
   public FieldMap12(ProjectFile file)
   {
      super(file);
   }

   /**
    * {@inheritDoc}
    */
   @Override protected Object[][] getDefaultTaskData()
   {
      return null;
   }

   /**
    * {@inheritDoc}
    */
   @Override protected Object[][] getDefaultResourceData()
   {
      return null;
   }

   /**
    * {@inheritDoc}
    */
   @Override protected Object[][] getDefaultAssignmentData()
   {
      return DEFAULT_ASSIGNMENT_MAP;
   }

   private static final Object[][] DEFAULT_ASSIGNMENT_MAP =
   {
      {
         AssignmentField.UNIQUE_ID,
         FieldLocation.FIXED_DATA,
         Integer.valueOf(0),
         Integer.valueOf(0)
      },
      {
         AssignmentField.TASK_UNIQUE_ID,
         FieldLocation.FIXED_DATA,
         Integer.valueOf(4),
         Integer.valueOf(0)
      },
      {
         AssignmentField.RESOURCE_UNIQUE_ID,
         FieldLocation.FIXED_DATA,
         Integer.valueOf(8),
         Integer.valueOf(0)
      },
      {
         AssignmentField.START,
         FieldLocation.FIXED_DATA,
         Integer.valueOf(12),
         Integer.valueOf(0)
      },
      {
         AssignmentField.FINISH,
         FieldLocation.FIXED_DATA,
         Integer.valueOf(16),
         Integer.valueOf(0)
      },
      {
         AssignmentField.ASSIGNMENT_DELAY,
         FieldLocation.FIXED_DATA,
         Integer.valueOf(24),
         Integer.valueOf(0)
      },
      {
         AssignmentField.LEVELING_DELAY_UNITS,
         FieldLocation.FIXED_DATA,
         Integer.valueOf(28),
         Integer.valueOf(0)
      },
      {
         AssignmentField.LEVELING_DELAY,
         FieldLocation.FIXED_DATA,
         Integer.valueOf(30),
         Integer.valueOf(0)
      },
      {
         AssignmentField.BASELINE_START,
         FieldLocation.FIXED_DATA,
         Integer.valueOf(36),
         Integer.valueOf(0)
      },
      {
         AssignmentField.BASELINE_FINISH,
         FieldLocation.FIXED_DATA,
         Integer.valueOf(40),
         Integer.valueOf(0)
      },
      {
         AssignmentField.VARIABLE_RATE_UNITS,
         FieldLocation.FIXED_DATA,
         Integer.valueOf(52),
         Integer.valueOf(0)
      },
      {
         AssignmentField.ASSIGNMENT_UNITS,
         FieldLocation.FIXED_DATA,
         Integer.valueOf(54),
         Integer.valueOf(0)
      },
      {
         AssignmentField.WORK,
         FieldLocation.FIXED_DATA,
         Integer.valueOf(62),
         Integer.valueOf(0)
      },
      {
         AssignmentField.ACTUAL_WORK,
         FieldLocation.FIXED_DATA,
         Integer.valueOf(70),
         Integer.valueOf(0)
      },
      {
         AssignmentField.REMAINING_WORK,
         FieldLocation.FIXED_DATA,
         Integer.valueOf(86),
         Integer.valueOf(0)
      },
      {
         AssignmentField.BASELINE_WORK,
         FieldLocation.FIXED_DATA,
         Integer.valueOf(94),
         Integer.valueOf(0)
      },
      {
         AssignmentField.COST,
         FieldLocation.FIXED_DATA,
         Integer.valueOf(102),
         Integer.valueOf(0)
      },
      {
         AssignmentField.ACTUAL_COST,
         FieldLocation.FIXED_DATA,
         Integer.valueOf(110),
         Integer.valueOf(0)
      },
      {
         AssignmentField.BASELINE_COST,
         FieldLocation.FIXED_DATA,
         Integer.valueOf(126),
         Integer.valueOf(0)
      },
      {
         AssignmentField.PLANNED_WORK_DATA,
         FieldLocation.VAR_DATA,
         Integer.valueOf(65535),
         Integer.valueOf(7)
      },
      {
         AssignmentField.COMPLETE_WORK_DATA,
         FieldLocation.VAR_DATA,
         Integer.valueOf(65535),
         Integer.valueOf(9)
      }

   };
}
