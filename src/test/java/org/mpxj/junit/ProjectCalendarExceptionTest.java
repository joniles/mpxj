/*
 * file:       ProjectCalendarExceptionTest.java
 * author:     Jon Iles
 * copyright:  (c) Packwood Software 2022
 * date:       26 April 2022
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

package org.mpxj.junit;

import java.time.LocalDate;

import static org.junit.Assert.*;

import org.mpxj.ProjectCalendar;
import org.mpxj.ProjectCalendarException;
import org.mpxj.ProjectFile;
import org.junit.Test;

/**
 * Test to exercise {@code ProjectCalendarException} functionality.
 */
public class ProjectCalendarExceptionTest
{
   /**
    * Test the contains method.
    */
   @Test public void testContains()
   {
      ProjectCalendar calendar = new ProjectCalendar(new ProjectFile());
      ProjectCalendarException exception = calendar.addCalendarException(LocalDate.of(2020, 1, 10), LocalDate.of(2020, 1, 12));

      //
      // Test single date
      //
      assertFalse(exception.contains(LocalDate.of(2020, 1, 9)));
      assertTrue(exception.contains(LocalDate.of(2020, 1, 10)));
      assertTrue(exception.contains(LocalDate.of(2020, 1, 11)));
      assertTrue(exception.contains(LocalDate.of(2020, 1, 12)));
      assertFalse(exception.contains(LocalDate.of(2020, 1, 13)));

      //
      // Test calendar exception
      //

      // Range is entirely before
      assertFalse(exception.contains(calendar.addCalendarException(LocalDate.of(2020, 1, 8), LocalDate.of(2020, 1, 9))));

      // Range is entirely after
      assertFalse(exception.contains(calendar.addCalendarException(LocalDate.of(2020, 1, 13), LocalDate.of(2020, 1, 14))));

      // Range matches exactly
      assertTrue(exception.contains(calendar.addCalendarException(LocalDate.of(2020, 1, 10), LocalDate.of(2020, 1, 12))));

      // Range overlaps start
      assertTrue(exception.contains(calendar.addCalendarException(LocalDate.of(2020, 1, 9), LocalDate.of(2020, 1, 11))));

      // Range overlaps end
      assertTrue(exception.contains(calendar.addCalendarException(LocalDate.of(2020, 1, 11), LocalDate.of(2020, 1, 14))));
   }
}
