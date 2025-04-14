/*
 * file:       PlannerResourceTest.java
 * author:     Jon Iles
 * copyright:  (c) Packwood Software 2007
 * date:       23 February 2007
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
import org.mpxj.ProjectFile;
import org.mpxj.planner.PlannerReader;

import org.junit.Test;

/**
 * Tests to exercise Planner file read functionality.
 */
public class PlannerResourceTest
{

   /**
    * Test calendar data read from an MPP9 file.
    */
   @Test public void testPlannerResource() throws Exception
   {
      ProjectFile mpp = new PlannerReader().read(MpxjTestData.filePath("planner-resource.planner"));
      assertNotNull(mpp);
   }
}
