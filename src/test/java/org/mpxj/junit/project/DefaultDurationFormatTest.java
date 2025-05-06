/*
 * file:       DefaultDurationFormatTest.java
 * author:     Jon Iles
 * copyright:  (c) Packwood Software 2014
 * date:       25/08/2014
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

package org.mpxj.junit.project;

import static org.mpxj.junit.MpxjAssert.*;
import org.mpxj.ProjectFile;
import org.mpxj.Task;
import org.mpxj.TimeUnit;
import org.mpxj.junit.MpxjTestData;
import org.mpxj.mspdi.MSPDIReader;

import org.junit.Test;

/**
 * Default duration format test.
 */
public class DefaultDurationFormatTest
{
   /**
    * Ensure that where the duration format is listed as "21" in the MSPDI file, we use the default duration format,
    * as defined in the project properties.
    */
   @Test public void testDefaultDateFormat() throws Exception
   {
      ProjectFile file = new MSPDIReader().read(MpxjTestData.filePath("project/default-duration-format/DefaultDurationFormat.xml"));
      Task task = file.getTaskByID(Integer.valueOf(1));
      assertDurationEquals(2, TimeUnit.WEEKS, task.getDuration());
   }
}
