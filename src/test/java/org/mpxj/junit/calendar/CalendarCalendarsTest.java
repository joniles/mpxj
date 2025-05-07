/*
 * file:       CalendarCalendarsTest.java
 * author:     Jon Iles
 * copyright:  (c) Packwood Software 2015
 * date:       29/04/2015
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

package org.mpxj.junit.calendar;

import static org.junit.Assert.*;

import java.io.File;

import org.mpxj.reader.UniversalProjectReader;
import org.junit.Test;

import org.mpxj.MPXJException;
import org.mpxj.ProjectCalendarContainer;
import org.mpxj.ProjectFile;
import org.mpxj.junit.MpxjTestData;

/**
 * Tests to ensure basic calendar details are read correctly.
 */
public class CalendarCalendarsTest
{
   /**
    * Test to validate calendars in files saved by different versions of MS Project.
    */
   @Test public void testCalendars() throws MPXJException
   {
      for (File file : MpxjTestData.listFiles("generated/calendar-calendars", "calendar-calendars"))
      {
         testCalendars(file);
      }
   }

   /**
    * Test an individual project.
    *
    * @param file project file
    */
   private void testCalendars(File file) throws MPXJException
   {
      ProjectFile project = new UniversalProjectReader().read(file);
      ProjectCalendarContainer calendars = project.getCalendars();

      int id = 1;
      assertEquals("Standard", calendars.getByUniqueID(Integer.valueOf(id++)).getName());

      if (!file.getName().endsWith(".mpx"))
      {
         id++;
      }

      assertEquals("Calendar1", calendars.getByUniqueID(Integer.valueOf(id++)).getName());
      assertEquals("Calendar2", calendars.getByUniqueID(Integer.valueOf(id++)).getName());
      assertEquals("Resource One", calendars.getByUniqueID(Integer.valueOf(id++)).getName());
      assertEquals("Resource Two", calendars.getByUniqueID(Integer.valueOf(id)).getName());
   }
}
