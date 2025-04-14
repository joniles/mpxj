/*
 * file:       AvailabilityTest.java
 * author:     Jon Iles
 * copyright:  (c) Packwood Software 2009
 * date:       08/06/2009
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

import static org.junit.Assert.*;

import java.time.LocalDateTime;

import org.mpxj.Availability;
import org.mpxj.AvailabilityTable;

import org.mpxj.common.LocalDateTimeHelper;
import org.junit.Test;

/**
 * Test resource availability table functionality.
 */
public class AvailabilityTableTest
{
   /**
    * Test the getEntryByDate method.
    */
   @Test public void testGetEntryByDate()
   {
      Availability availability1 = new Availability(LocalDateTime.of(2015, 2, 1, 8, 0), LocalDateTime.of(2015, 2, 3, 17, 0), null);
      Availability availability2 = new Availability(LocalDateTime.of(2015, 3, 1, 8, 0), LocalDateTime.of(2015, 3, 3, 17, 0), null);
      AvailabilityTable table = new AvailabilityTable();
      table.add(availability1);
      table.add(availability2);

      assertNull(table.getEntryByDate(LocalDateTime.of(2015, 1, 1, 9, 0)));
      assertNull(table.getEntryByDate(LocalDateTime.of(2015, 2, 1, 7, 0)));

      assertEquals(availability1, table.getEntryByDate(LocalDateTime.of(2015, 2, 1, 8, 0)));
      assertEquals(availability1, table.getEntryByDate(LocalDateTime.of(2015, 2, 2, 8, 0)));
      assertEquals(availability1, table.getEntryByDate(LocalDateTime.of(2015, 2, 3, 8, 0)));
      assertNull(table.getEntryByDate(LocalDateTime.of(2015, 2, 3, 18, 0)));

      assertEquals(availability2, table.getEntryByDate(LocalDateTime.of(2015, 3, 1, 8, 0)));
      assertEquals(availability2, table.getEntryByDate(LocalDateTime.of(2015, 3, 2, 8, 0)));
      assertEquals(availability2, table.getEntryByDate(LocalDateTime.of(2015, 3, 3, 8, 0)));
      assertNull(table.getEntryByDate(LocalDateTime.of(2015, 3, 3, 18, 0)));
   }

   /**
    * Test the availabilityFrom method.
    */
   @Test public void testAvailabilityFrom()
   {
      LocalDateTime date = LocalDateTime.of(2023, 10, 11, 8, 0);
      Double units = Double.valueOf(100.0);

      // NA ranges
      AvailabilityTable table = new AvailabilityTable();
      assertNull(table.availableFrom(date));

      table.add(new Availability(null, null, units));
      assertNull(table.availableFrom(date));

      table.clear();
      table.add(new Availability(LocalDateTimeHelper.START_DATE_NA, LocalDateTimeHelper.END_DATE_NA, units));
      assertNull(table.availableFrom(date));

      // date is before first range start
      table.clear();
      table.add(new Availability(LocalDateTime.of(2023, 10, 12, 0, 0), LocalDateTimeHelper.END_DATE_NA, units));
      assertNull(table.availableFrom(date));

      // date matches first range start
      table.clear();
      table.add(new Availability(date, LocalDateTimeHelper.END_DATE_NA, units));
      assertEquals(date, table.availableFrom(date));

      // date in range
      LocalDateTime rangeStart = LocalDateTime.of(2023, 10, 10, 0, 0);
      LocalDateTime rangeEnd = LocalDateTime.of(2023, 10, 17, 23, 59);

      table.clear();
      table.add(new Availability(rangeStart, LocalDateTimeHelper.END_DATE_NA, units));
      assertEquals(rangeStart, table.availableFrom(date));

      table.clear();
      table.add(new Availability(rangeStart, null, units));
      assertEquals(rangeStart, table.availableFrom(date));

      table.clear();
      table.add(new Availability(LocalDateTimeHelper.START_DATE_NA, rangeEnd, units));
      assertNull(table.availableFrom(date));

      table.clear();
      table.add(new Availability(null, rangeEnd, units));
      assertNull(table.availableFrom(date));

      table.clear();
      table.add(new Availability(rangeStart, rangeEnd, units));
      assertEquals(rangeStart, table.availableFrom(date));

      // date after range
      rangeStart = LocalDateTime.of(2023, 10, 2, 0, 0);
      rangeEnd = LocalDateTime.of(2023, 10, 10, 23, 59);

      table.clear();
      table.add(new Availability(rangeStart, rangeEnd, units));
      assertEquals(LocalDateTime.of(2023, 10, 11, 0, 0), table.availableFrom(date));

      // date between ranges
      LocalDateTime range2Start = LocalDateTime.of(2023, 10, 12, 0, 0);
      LocalDateTime range2End = LocalDateTime.of(2023, 10, 20, 23, 59);

      table.clear();
      table.add(new Availability(rangeStart, rangeEnd, units));
      table.add(new Availability(range2Start, range2End, units));
      assertEquals(LocalDateTime.of(2023, 10, 11, 0, 0), table.availableFrom(date));
   }

   /**
    * Test the availabilityTo method.
    */
   @Test public void testAvailabilityTo()
   {
      LocalDateTime date = LocalDateTime.of(2023, 10, 11, 8, 0);
      Double units = Double.valueOf(100.0);

      // NA ranges
      AvailabilityTable table = new AvailabilityTable();
      assertNull(table.availableTo(date));

      table.add(new Availability(null, null, units));
      assertNull(table.availableTo(date));

      table.clear();
      table.add(new Availability(LocalDateTimeHelper.START_DATE_NA, LocalDateTimeHelper.END_DATE_NA, units));
      assertNull(table.availableTo(date));

      // date is before first range start
      table.clear();
      LocalDateTime rangeStart = LocalDateTime.of(2023, 10, 12, 0, 0);
      table.add(new Availability(rangeStart, LocalDateTimeHelper.END_DATE_NA, units));
      assertEquals(rangeStart.minusMinutes(1), table.availableTo(date));

      // date matches first range start
      table.clear();
      rangeStart = LocalDateTime.of(2023, 10, 11, 0, 0);
      LocalDateTime rangeEnd = LocalDateTime.of(2023, 10, 17, 23, 59);

      table.add(new Availability(rangeStart, rangeEnd, units));
      assertEquals(rangeEnd, table.availableTo(date));

      // date in range
      rangeStart = LocalDateTime.of(2023, 10, 10, 0, 0);
      rangeEnd = LocalDateTime.of(2023, 10, 17, 23, 59);

      table.clear();
      table.add(new Availability(rangeStart, LocalDateTimeHelper.END_DATE_NA, units));
      assertNull(table.availableTo(date));

      table.clear();
      table.add(new Availability(rangeStart, null, units));
      assertNull(table.availableTo(date));

      table.clear();
      table.add(new Availability(LocalDateTimeHelper.START_DATE_NA, rangeEnd, units));
      assertEquals(rangeEnd, table.availableTo(date));

      table.clear();
      table.add(new Availability(null, rangeEnd, units));
      assertEquals(rangeEnd, table.availableTo(date));

      table.clear();
      table.add(new Availability(rangeStart, rangeEnd, units));
      assertEquals(rangeEnd, table.availableTo(date));

      // date after range
      rangeStart = LocalDateTime.of(2023, 10, 2, 0, 0);
      rangeEnd = LocalDateTime.of(2023, 10, 10, 23, 59);

      table.clear();
      table.add(new Availability(rangeStart, rangeEnd, units));
      assertNull(table.availableTo(date));

      // date between ranges
      LocalDateTime range2Start = LocalDateTime.of(2023, 10, 12, 0, 0);
      LocalDateTime range2End = LocalDateTime.of(2023, 10, 20, 23, 59);

      table.clear();
      table.add(new Availability(rangeStart, rangeEnd, units));
      table.add(new Availability(range2Start, range2End, units));
      assertEquals(range2Start.minusMinutes(1), table.availableTo(date));
   }
}
