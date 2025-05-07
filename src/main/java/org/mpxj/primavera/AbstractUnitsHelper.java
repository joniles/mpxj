/*
 * file:       AbstractUnitsHelper.java
 * author:     Jon Iles
 * date:       2023-12-15
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

import java.util.Optional;

import org.mpxj.Duration;
import org.mpxj.ProjectFile;
import org.mpxj.ResourceAssignment;
import org.mpxj.TimeUnit;
import org.mpxj.common.NumberHelper;

/**
 * Common implementation for calculating planned and remaining units ready for writing to PMXML and XER files.
 */
abstract class AbstractUnitsHelper
{
   public AbstractUnitsHelper(ResourceAssignment assignment)
   {
      // Planned
      ProjectFile file = assignment.getParentFile();
      m_plannedUnits = getDurationInHours(file, Optional.ofNullable(assignment.getPlannedWork()).orElseGet(assignment::getWork));
      m_plannedUnitsPerTime = getPercentage(assignment.getUnits());
      m_remainingUnitsPerTime = getPercentage(assignment.getRemainingUnits());

      // Remaining
      if (assignment.getActualStart() == null && (assignment.getActualWork() == null || assignment.getActualWork().getDuration() == 0))
      {
         // The assignment has not started
         m_remainingUnits = m_plannedUnits;
      }
      else
      {
         Double actualUnits = getDurationInHours(file, assignment.getActualWork());
         Double atCompletionUnits = getDurationInHours(file, assignment.getWork());
         if (assignment.getActualFinish() == null && NumberHelper.getDouble(actualUnits) < NumberHelper.getDouble(atCompletionUnits))
         {
            // The assignment is in progress
            m_remainingUnits = getDurationInHours(file, assignment.getRemainingWork());
         }
         else
         {
            // The assignment is complete
            m_remainingUnits = NumberHelper.DOUBLE_ZERO;
         }
      }
   }

   /**
    * Retrieve the planned units.
    *
    * @return planned units
    */
   public Double getPlannedUnits()
   {
      return m_plannedUnits;
   }

   /**
    * Retrieve the planned units per time.
    *
    * @return planned units per time
    */
   public Double getPlannedUnitsPerTime()
   {
      return m_plannedUnitsPerTime;
   }

   /**
    * Retrieve the remaining units.
    *
    * @return remaining units
    */
   public Double getRemainingUnits()
   {
      return m_remainingUnits;
   }

   /**
    * Retrieve the remaining units per time.
    *
    * @return remaining units per time
    */
   public Double getRemainingUnitsPerTime()
   {
      return m_remainingUnitsPerTime;
   }

   /**
    * Retrieve a duration in the form required by Primavera.
    *
    * @param duration Duration instance
    * @param file parent file
    * @return formatted duration
    */
   private Double getDurationInHours(ProjectFile file, Duration duration)
   {
      Double result;
      if (duration == null)
      {
         result = null;
      }
      else
      {
         if (duration.getUnits() != TimeUnit.HOURS)
         {
            duration = duration.convertUnits(TimeUnit.HOURS, file.getProjectProperties());
         }

         // Round result appropriately for target file type
         result = Double.valueOf(Math.round(duration.getDuration() * getScale()) / getScale());
      }
      return result;
   }

   /**
    * Formats a percentage value.
    *
    * @param number MPXJ percentage value
    * @return Primavera percentage value
    */
   private Double getPercentage(Number number)
   {
      Double result = null;

      if (number != null)
      {
         result = Double.valueOf(number.doubleValue() / 100);
      }

      return result;
   }

   /**
    * Retrieve the scale used for rounding.
    *
    * @return rounding scale
    */
   protected abstract double getScale();

   private final Double m_plannedUnits;
   private final Double m_plannedUnitsPerTime;
   private final Double m_remainingUnits;
   private final Double m_remainingUnitsPerTime;
}
