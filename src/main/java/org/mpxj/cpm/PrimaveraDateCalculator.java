/*
 * file:       PrimaveraDateCalculator.java
 * author:     Jon Iles
 * date:       2026-07-29
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

package org.mpxj.cpm;

import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.function.Supplier;

import org.mpxj.DayType;
import org.mpxj.Duration;
import org.mpxj.LocalTimeRange;
import org.mpxj.ProjectCalendar;
import org.mpxj.ProjectCalendarHours;
import org.mpxj.ProjectFile;
import org.mpxj.Relation;

/**
 * Implementation of common date calculations which are used by the Primavera scheduer and elsewhere.
 */
class PrimaveraDateCalculator
{
   public PrimaveraDateCalculator(ProjectFile file, Supplier<LocalDateTime> dataDateSupplier)
   {
      m_file = file;
      m_twentyFourHourCalendar = createTwentyFourHourCalendar();
      m_dataDateSupplier = dataDateSupplier;
   }

   /**
    * Add relation lag to a date.
    *
    * @param relation parent relation
    * @param date date
    * @return date plus lag
    */
   public LocalDateTime addLag(Relation relation, LocalDateTime date)
   {
      return addLag(relation, date, relation.getLag());
   }

   /**
    * Add lag to a date.
    *
    * @param relation parent relation
    * @param date date
    * @param lag lag
    * @return date plus lag
    */
   public LocalDateTime addLag(Relation relation, LocalDateTime date, Duration lag)
   {
      if (date == null)
      {
         return null;
      }

      LocalDateTime result = getDate(getLagCalendar(relation), date, lag);
      LocalDateTime dataDate = m_dataDateSupplier.get();
      if (lag.getDuration() < 0 && result.isBefore(dataDate))
      {
         result = dataDate;
      }

      return result;
   }

   /**
    * Remove lag from a date.
    *
    * @param relation parent relation
    * @param date date
    * @return date minus relation lag
    */
   public LocalDateTime removeLag(Relation relation, LocalDateTime date)
   {
      return removeLag(relation, date, relation.getLag());
   }

   /**
    * Remove lag from a date.
    *
    * @param relation parent relation
    * @param date date
    * @param lag lag
    * @return date minus lag
    */
   public LocalDateTime removeLag(Relation relation, LocalDateTime date, Duration lag)
   {
      if (date == null)
      {
         return null;
      }

      return getDate(getLagCalendar(relation), date, lag.negate());
   }

   /**
    * Retrieve the lag calendar to use when adding/removing lag.
    *
    * @param relation parent relation
    * @return lag calendar
    */
   public ProjectCalendar getLagCalendar(Relation relation)
   {
      switch (m_file.getProjectProperties().getRelationshipLagCalendar())
      {
         case PREDECESSOR:
         {
            return relation.getPredecessorTask().getEffectiveCalendar();
         }

         case SUCCESSOR:
         {
            return relation.getSuccessorTask().getEffectiveCalendar();
         }

         case PROJECT_DEFAULT:
         {
            return m_file.getProjectProperties().getDefaultCalendar();
         }

         case TWENTY_FOUR_HOUR:
         default:
         {
            return m_twentyFourHourCalendar;
         }
      }
   }

   /**
    * Using the supplied calendar, add a duration to the supplied date.
    *
    * @param calendar parent calendar
    * @param date date
    * @param duration duration
    * @return date plus duration
    */
   public LocalDateTime getDate(ProjectCalendar calendar, LocalDateTime date, Duration duration)
   {
      LocalDateTime result = calendar.getDate(date, duration);

      // P6 appears to work to the nearest minute
      if (result.getSecond() != 0)
      {
         boolean negativeDuration = duration.getDuration() < 0;
         boolean roundUp = (negativeDuration && result.getSecond() > 30) || (!negativeDuration && result.getSecond() >= 30);
         LocalTime newTime = LocalTime.of(result.getHour(), result.getMinute());
         result = LocalDateTime.of(result.toLocalDate(), newTime);
         if (roundUp)
         {
            result = result.plusMinutes(1);
         }
      }
      return result;
   }

   /**
    * Create a temporary 24-hour calendar for this project.
    *
    * @return 24-hour calendar
    */
   private ProjectCalendar createTwentyFourHourCalendar()
   {
      ProjectCalendar calendar = new ProjectCalendar(m_file);
      for (DayOfWeek day : DayOfWeek.values())
      {
         calendar.setCalendarDayType(day, DayType.WORKING);
         ProjectCalendarHours hours = calendar.addCalendarHours(day);
         hours.add(new LocalTimeRange(LocalTime.MIDNIGHT, LocalTime.MIDNIGHT));
      }
      return calendar;
   }

   private final ProjectFile m_file;
   private final ProjectCalendar m_twentyFourHourCalendar;
   private final Supplier<LocalDateTime> m_dataDateSupplier;
}
