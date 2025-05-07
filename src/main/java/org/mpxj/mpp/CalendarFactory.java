/*
 * file:       CalendarFactory.java
 * author:     Jon Iles
 * copyright:  (c) Packwood Software 2017
 * date:       2017-10-04
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

package org.mpxj.mpp;

import java.io.IOException;
import java.util.HashMap;

import org.apache.poi.poifs.filesystem.DirectoryEntry;

import org.mpxj.ProjectCalendar;

/**
 * Read calendar data from MPP files.
 */
interface CalendarFactory
{

   /**
    * The format of the calendar data is a 4 byte header followed
    * by 7x 60 byte blocks, one for each day of the week. Optionally
    * following this is a set of 64 byte blocks representing exceptions
    * to the calendar.
    *
    * @param projectDir project data directory in the MPP file
    * @param projectProps project properties
    * @param inputStreamFactory input stream factory
    * @param resourceMap map of resources to calendars
    */
   void processCalendarData(DirectoryEntry projectDir, Props projectProps, DocumentInputStreamFactory inputStreamFactory, HashMap<Integer, ProjectCalendar> resourceMap) throws IOException;
}