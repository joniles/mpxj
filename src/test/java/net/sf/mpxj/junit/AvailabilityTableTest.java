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

package net.sf.mpxj.junit;

import static org.junit.Assert.*;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;

import net.sf.mpxj.Availability;
import net.sf.mpxj.AvailabilityTable;

import org.junit.Test;

/**
 * Test resource availability table functionality.
 */
public class AvailabilityTableTest
{
   /**
    * Test the getEntryByDate method.
    */
   @Test public void testGetEntryByDate() throws Exception
   {
      DateFormat df = new SimpleDateFormat("dd/MM/yyyy HH:mm");
      Availability availability1 = new Availability(LocalDateTime.of(2015, 2, 1, 8, 0, 0), df.parse("03/02/2015 17:00"), null);
      Availability availability2 = new Availability(df.parse("01/03/2015 08:00"), df.parse("03/03/2015 17:00"), null);
      AvailabilityTable table = new AvailabilityTable();
      table.add(availability1);
      table.add(availability2);

      assertNull(table.getEntryByDate(df.parse("01/01/2015 09:00")));
      assertNull(table.getEntryByDate(df.parse("01/02/2015 07:00")));

      assertEquals(availability1, table.getEntryByDate(df.parse("01/02/2015 08:00")));
      assertEquals(availability1, table.getEntryByDate(df.parse("02/02/2015 08:00")));
      assertEquals(availability1, table.getEntryByDate(df.parse("03/02/2015 08:00")));
      assertNull(table.getEntryByDate(df.parse("03/02/2015 18:00")));

      assertEquals(availability2, table.getEntryByDate(df.parse("01/03/2015 08:00")));
      assertEquals(availability2, table.getEntryByDate(df.parse("02/03/2015 08:00")));
      assertEquals(availability2, table.getEntryByDate(df.parse("03/03/2015 08:00")));
      assertNull(table.getEntryByDate(df.parse("03/03/2015 18:00")));
   }
}
