package net.sf.mpxj.junit;
import net.sf.mpxj.ProjectFile;
import net.sf.mpxj.Rate;
import net.sf.mpxj.TimeUnit;
import net.sf.mpxj.common.RateHelper;
import org.junit.Assert;
import org.junit.Test;

public class RateHelperTest
{
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
