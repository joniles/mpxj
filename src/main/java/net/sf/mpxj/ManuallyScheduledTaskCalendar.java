/*
 * file:       ProjectCalendar.java
 * author:     Fabian Schmidt
 * date:       16/08/2024
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

package net.sf.mpxj;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

public class ManuallyScheduledTaskCalendar extends ProjectCalendar {
   public ManuallyScheduledTaskCalendar(ProjectCalendar calendar, ResourceAssignment assignment)
   {
      super(calendar.getParentFile(), true);
      m_calendar = calendar;
      m_assignment = assignment;
   }
   
   public LocalDateTime getDate(LocalDateTime date, Duration duration)
   {
      throw new UnsupportedOperationException();
   }

   public Duration getWork(LocalDateTime startDate, LocalDateTime endDate, TimeUnit format)
   {
      throw new UnsupportedOperationException();
   }

   public LocalTime getFinishTime(LocalDate date)
   {
      throw new UnsupportedOperationException();
   }

   public LocalDateTime getNextWorkStart(LocalDateTime date)
   {
      throw new UnsupportedOperationException();
   }

   private final ProjectCalendar m_calendar;
   private final ResourceAssignment m_assignment;
}
