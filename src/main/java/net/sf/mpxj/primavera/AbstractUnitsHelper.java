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

package net.sf.mpxj.primavera;

import java.util.Optional;

import net.sf.mpxj.Duration;
import net.sf.mpxj.ProjectFile;
import net.sf.mpxj.ResourceAssignment;
import net.sf.mpxj.ResourceType;
import net.sf.mpxj.Task;
import net.sf.mpxj.TimeUnit;
import net.sf.mpxj.common.NumberHelper;

/**
 * Common implementation for calculating planned and remaining units ready for writing to PMXML and XER files.
 */
abstract class AbstractUnitsHelper
{
   public AbstractUnitsHelper(ResourceAssignment assignment)
   {
      if (assignment.getResource().getType() == ResourceType.MATERIAL)
      {
         materialResource(assignment);
      }
      else
      {
         otherResource(assignment);
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
    * Units calculations for a material  resource.
    *
    * @param assignment resource assignment
    */
   private void materialResource(ResourceAssignment assignment)
   {
      // Planned
      ProjectFile file = assignment.getParentFile();
      Task task = assignment.getTask();
      double units = NumberHelper.getDouble(getDurationInHours(file, Optional.ofNullable(assignment.getPlannedWork()).orElseGet(assignment::getWork)));
      double time = NumberHelper.getDouble(getDurationInHours(file, Optional.ofNullable(task.getPlannedDuration()).orElseGet(task::getDuration)));
      double unitsPerTime = time == 0 ? 0 : units / time;
      m_plannedUnits = Double.valueOf(units);
      m_plannedUnitsPerTime = Double.valueOf(unitsPerTime);

      // Remaining
      if (assignment.getActualStart() == null)
      {
         // The assignment has not started
         m_remainingUnits = m_plannedUnits;
         m_remainingUnitsPerTime = m_plannedUnitsPerTime;
      }
      else
      {
         if (assignment.getActualFinish() == null)
         {
            // The assignment is in progress
            double remainingTime = NumberHelper.getDouble(getDurationInHours(file, task.getRemainingDuration()));
            double remainingUnits = NumberHelper.getDouble(getDurationInHours(file, assignment.getRemainingWork()));
            double remainingUnitsPerTime = remainingTime == 0 ? 0 : remainingUnits / remainingTime;
            m_remainingUnits = Double.valueOf(remainingUnits);
            m_remainingUnitsPerTime = Double.valueOf(remainingUnitsPerTime);
         }
         else
         {
            // The assignment is complete
            m_remainingUnits = NumberHelper.DOUBLE_ZERO;
            m_remainingUnitsPerTime = m_plannedUnitsPerTime;
         }
      }
   }

   /**
    * Units calculations for a non-material  resource.
    *
    * @param assignment resource assignment
    */
   private void otherResource(ResourceAssignment assignment)
   {
      // Planned
      ProjectFile file = assignment.getParentFile();
      Task task = assignment.getTask();
      m_plannedUnits = getDurationInHours(file, Optional.ofNullable(assignment.getPlannedWork()).orElseGet(assignment::getWork));
      m_plannedUnitsPerTime = getPercentage(assignment.getUnits());

      // Remaining
      if (assignment.getActualStart() == null)
      {
         // The assignment has not started
         m_remainingUnits = m_plannedUnits;
         m_remainingUnitsPerTime = m_plannedUnitsPerTime;
      }
      else
      {
         double remainingDuration = NumberHelper.getDouble(getDurationInHours(file, task.getRemainingDuration()));
         if (assignment.getActualFinish() == null)
         {
            // The assignment is in progress
            double remainingWork = NumberHelper.getDouble(getDurationInHours(file, assignment.getRemainingWork()));
            double units = remainingDuration == 0 ? 0 : remainingWork / remainingDuration;
            m_remainingUnits = Double.valueOf(remainingWork);
            m_remainingUnitsPerTime = Double.valueOf(units);
         }
         else
         {
            // The assignment is complete
            m_remainingUnits = NumberHelper.DOUBLE_ZERO;
            m_remainingUnitsPerTime = m_plannedUnitsPerTime;
         }
      }
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

   private Double m_plannedUnits;
   private Double m_plannedUnitsPerTime;
   private Double m_remainingUnits;
   private Double m_remainingUnitsPerTime;
}
