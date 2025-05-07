/*
 * file:       RateHelperTest.java
 * author:     Jon Iles
 * date:       2023-07-26
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

import org.mpxj.ProjectFile;
import org.mpxj.Rate;
import org.mpxj.TimeUnit;
import org.mpxj.common.RateHelper;
import org.junit.Assert;
import org.junit.Test;

/**
 * Test RateHelper methods.
 */
public class RateHelperTest
{
   /**
    * Test conversion of rates to hours.
    */
   @Test public void testConvertToHours()
   {
      ProjectFile file = new ProjectFile();
      file.setDefaultCalendar(file.addDefaultBaseCalendar());

      Assert.assertEquals(60, RateHelper.convertToHours(file.getProjectProperties(), new Rate(1, TimeUnit.MINUTES)), 0.0);
      Assert.assertEquals(1, RateHelper.convertToHours(file.getProjectProperties(), new Rate(1, TimeUnit.HOURS)), 0.0);
      Assert.assertEquals(1, RateHelper.convertToHours(file.getProjectProperties(), new Rate(8, TimeUnit.DAYS)), 0.0);
      Assert.assertEquals(1, RateHelper.convertToHours(file.getProjectProperties(), new Rate(40, TimeUnit.WEEKS)), 0.0);
      Assert.assertEquals(1, RateHelper.convertToHours(file.getProjectProperties(), new Rate(160, TimeUnit.MONTHS)), 0.0);
      Assert.assertEquals(1, RateHelper.convertToHours(file.getProjectProperties(), new Rate(2080, TimeUnit.YEARS)), 0.0);
   }

   /**
    * Test conversion of rates from hours.
    */
   @Test public void testConvertFromHours()
   {
      ProjectFile file = new ProjectFile();
      file.setDefaultCalendar(file.addDefaultBaseCalendar());

      Assert.assertEquals(1, RateHelper.convertFromHours(file.getProjectProperties(), new Rate(60, TimeUnit.HOURS), TimeUnit.MINUTES).getAmount(), 0.0);
      Assert.assertEquals(1, RateHelper.convertFromHours(file.getProjectProperties(), new Rate(1, TimeUnit.HOURS), TimeUnit.HOURS).getAmount(), 0.0);
      Assert.assertEquals(8, RateHelper.convertFromHours(file.getProjectProperties(), new Rate(1, TimeUnit.HOURS), TimeUnit.DAYS).getAmount(), 0.0);
      Assert.assertEquals(40, RateHelper.convertFromHours(file.getProjectProperties(), new Rate(1, TimeUnit.HOURS), TimeUnit.WEEKS).getAmount(), 0.0);
      Assert.assertEquals(160, RateHelper.convertFromHours(file.getProjectProperties(), new Rate(1, TimeUnit.HOURS), TimeUnit.MONTHS).getAmount(), 0.0);
      Assert.assertEquals(2080, RateHelper.convertFromHours(file.getProjectProperties(), new Rate(1, TimeUnit.HOURS), TimeUnit.YEARS).getAmount(), 0.0);
   }
}
