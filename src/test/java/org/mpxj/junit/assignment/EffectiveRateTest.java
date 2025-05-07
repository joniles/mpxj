/*
 * file:       EffectiveRateTest.java
 * author:     Jon Iles
 * copyright:  (c) Packwood Software 2022
 * date:       2022-08-22
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

package org.mpxj.junit.assignment;

import java.time.LocalDateTime;

import org.mpxj.ProjectFile;
import org.mpxj.Rate;
import org.mpxj.RateSource;
import org.mpxj.ResourceAssignment;
import org.mpxj.TimeUnit;
import org.mpxj.junit.MpxjTestData;
import org.mpxj.reader.UniversalProjectReader;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * Tests to ensure effective rates are determined correctly for data from P6.
 */
public class EffectiveRateTest
{
   /**
    * Validate rates read from an XER file.
    */
   @Test public void testXer() throws Exception
   {
      testRates(new UniversalProjectReader().read(MpxjTestData.filePath("assignment/resource-rates-test.xer")));
   }

   /**
    * Validate rates read from a PMXML file.
    */
   @Test public void testXml() throws Exception
   {
      testRates(new UniversalProjectReader().read(MpxjTestData.filePath("assignment/resource-rates-test.xml")));
   }

   private void testRates(ProjectFile file)
   {
      LocalDateTime date1 = LocalDateTime.of(2022, 2, 1, 0, 0);
      LocalDateTime date2 = LocalDateTime.of(2023, 2, 1, 0, 0);

      ResourceAssignment assignment = file.getResourceAssignments().getByUniqueID(Integer.valueOf(6639));
      assertEquals(RateSource.RESOURCE, assignment.getRateSource());
      assertEquals(0, assignment.getRateIndex().intValue());
      assertEquals(new Rate(1.0, TimeUnit.HOURS), assignment.getEffectiveRate(date1));
      assertEquals(new Rate(21.0, TimeUnit.HOURS), assignment.getEffectiveRate(date2));

      assignment = file.getResourceAssignments().getByUniqueID(Integer.valueOf(6640));
      assertEquals(RateSource.RESOURCE, assignment.getRateSource());
      assertEquals(1, assignment.getRateIndex().intValue());
      assertEquals(new Rate(2.0, TimeUnit.HOURS), assignment.getEffectiveRate(date1));
      assertEquals(new Rate(22.0, TimeUnit.HOURS), assignment.getEffectiveRate(date2));

      assignment = file.getResourceAssignments().getByUniqueID(Integer.valueOf(6641));
      assertEquals(RateSource.ROLE, assignment.getRateSource());
      assertEquals(0, assignment.getRateIndex().intValue());
      assertEquals(new Rate(6.0, TimeUnit.HOURS), assignment.getEffectiveRate(date1));
      assertEquals(new Rate(31.0, TimeUnit.HOURS), assignment.getEffectiveRate(date2));

      assignment = file.getResourceAssignments().getByUniqueID(Integer.valueOf(6642));
      assertEquals(RateSource.ROLE, assignment.getRateSource());
      assertEquals(1, assignment.getRateIndex().intValue());
      assertEquals(new Rate(7.0, TimeUnit.HOURS), assignment.getEffectiveRate(date1));
      assertEquals(new Rate(32.0, TimeUnit.HOURS), assignment.getEffectiveRate(date2));

      assignment = file.getResourceAssignments().getByUniqueID(Integer.valueOf(6643));
      assertEquals(RateSource.OVERRIDE, assignment.getRateSource());
      assertEquals(new Rate(99.0, TimeUnit.HOURS), assignment.getEffectiveRate(date1));
      assertEquals(new Rate(99.0, TimeUnit.HOURS), assignment.getEffectiveRate(date2));
   }
}
