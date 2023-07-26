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

package net.sf.mpxj.junit;

import net.sf.mpxj.ProjectFile;
import net.sf.mpxj.Rate;
import net.sf.mpxj.TimeUnit;
import net.sf.mpxj.common.RateHelper;
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

      Assert.assertEquals(RateHelper.convertToHours(file.getProjectProperties(), new Rate(1, TimeUnit.MINUTES)), 60, 0.0);
      Assert.assertEquals(RateHelper.convertToHours(file.getProjectProperties(), new Rate(1, TimeUnit.HOURS)), 1, 0.0);
      Assert.assertEquals(RateHelper.convertToHours(file.getProjectProperties(), new Rate(8, TimeUnit.DAYS)), 1, 0.0);
      Assert.assertEquals(RateHelper.convertToHours(file.getProjectProperties(), new Rate(40, TimeUnit.WEEKS)), 1, 0.0);
      Assert.assertEquals(RateHelper.convertToHours(file.getProjectProperties(), new Rate(160, TimeUnit.MONTHS)), 1, 0.0);
      Assert.assertEquals(RateHelper.convertToHours(file.getProjectProperties(), new Rate(2080, TimeUnit.YEARS)), 1, 0.0);
   }

   /**
    * Test conversion of rates from hours.
    */
   @Test public void testConvertFromHours()
   {
      ProjectFile file = new ProjectFile();
      file.setDefaultCalendar(file.addDefaultBaseCalendar());

      Assert.assertEquals(RateHelper.convertFromHours(file.getProjectProperties(), new Rate(60, TimeUnit.HOURS), TimeUnit.MINUTES).getAmount(), 1, 0.0);
      Assert.assertEquals(RateHelper.convertFromHours(file.getProjectProperties(), new Rate(1, TimeUnit.HOURS), TimeUnit.HOURS).getAmount(), 1, 0.0);
      Assert.assertEquals(RateHelper.convertFromHours(file.getProjectProperties(), new Rate(1, TimeUnit.HOURS), TimeUnit.DAYS).getAmount(), 8, 0.0);
      Assert.assertEquals(RateHelper.convertFromHours(file.getProjectProperties(), new Rate(1, TimeUnit.HOURS), TimeUnit.WEEKS).getAmount(), 40, 0.0);
      Assert.assertEquals(RateHelper.convertFromHours(file.getProjectProperties(), new Rate(1, TimeUnit.HOURS), TimeUnit.MONTHS).getAmount(), 160, 0.0);
      Assert.assertEquals(RateHelper.convertFromHours(file.getProjectProperties(), new Rate(1, TimeUnit.HOURS), TimeUnit.YEARS).getAmount(), 2080, 0.0);
   }
}
