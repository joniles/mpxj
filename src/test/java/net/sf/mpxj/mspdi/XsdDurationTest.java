package net.sf.mpxj.mspdi;

import net.sf.mpxj.Duration;
import net.sf.mpxj.TimeUnit;
import org.junit.Assert;
import org.junit.Test;

public class XsdDurationTest
{
   @Test public void testPrintFromNullDuration()
   {
      Duration duration = null;
      XsdDuration xsdDuration = new XsdDuration(duration);
      Assert.assertEquals("PT0H0M0S", xsdDuration.print(true));
   }

   @Test public void testPrintFromZeroDuration()
   {
      Duration duration = Duration.getInstance(0, TimeUnit.HOURS);
      XsdDuration xsdDuration = new XsdDuration(duration);
      Assert.assertEquals("PT0H0M0S", xsdDuration.print(true));
   }

   @Test public void testPrintFromMinutesDuration()
   {
      Duration duration;
      XsdDuration xsdDuration;

      duration = Duration.getInstance(1, TimeUnit.MINUTES);
      xsdDuration = new XsdDuration(duration);
      Assert.assertEquals("PT0H1M0S", xsdDuration.print(true));

      duration = Duration.getInstance(1.01678, TimeUnit.MINUTES);
      xsdDuration = new XsdDuration(duration);
      Assert.assertEquals("PT0H1M1S", xsdDuration.print(true));

      duration = Duration.getInstance(1.5, TimeUnit.MINUTES);
      xsdDuration = new XsdDuration(duration);
      Assert.assertEquals("PT0H1M30S", xsdDuration.print(true));

      duration = Duration.getInstance(70, TimeUnit.MINUTES);
      xsdDuration = new XsdDuration(duration);
      Assert.assertEquals("PT0H70M0S", xsdDuration.print(true));
   }

   @Test public void testPrintFromHoursDuration()
   {
      Duration duration;
      XsdDuration xsdDuration;

      duration = Duration.getInstance(1, TimeUnit.HOURS);
      xsdDuration = new XsdDuration(duration);
      Assert.assertEquals("PT1H0M0S", xsdDuration.print(true));

      duration = Duration.getInstance(1.000278, TimeUnit.HOURS);
      xsdDuration = new XsdDuration(duration);
      Assert.assertEquals("PT1H0M1S", xsdDuration.print(true));

      duration = Duration.getInstance(1.016944, TimeUnit.HOURS);
      xsdDuration = new XsdDuration(duration);
      Assert.assertEquals("PT1H1M1S", xsdDuration.print(true));

      duration = Duration.getInstance(20.5, TimeUnit.HOURS);
      xsdDuration = new XsdDuration(duration);
      Assert.assertEquals("PT20H30M0S", xsdDuration.print(true));

      duration = Duration.getInstance(20.99999, TimeUnit.HOURS);
      xsdDuration = new XsdDuration(duration);
      Assert.assertEquals("PT21H0M0S", xsdDuration.print(true));

      duration = Duration.getInstance(30, TimeUnit.HOURS);
      xsdDuration = new XsdDuration(duration);
      Assert.assertEquals("PT30H0M0S", xsdDuration.print(true));
   }

   @Test public void testPrintFromDaysDuration()
   {
      Duration duration;
      XsdDuration xsdDuration;

      duration = Duration.getInstance(1, TimeUnit.DAYS);
      xsdDuration = new XsdDuration(duration);
      Assert.assertEquals("P0Y0M1DT0H0M0S", xsdDuration.print(true));
   }
}
