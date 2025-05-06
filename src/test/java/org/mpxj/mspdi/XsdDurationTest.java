/*
 * file:       XsdDurationTest.java
 * author:     Jon Iles
 * copyright:  (c) Packwood Software 2023
 * date:       2023-04-03
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

package org.mpxj.mspdi;

import org.mpxj.Duration;
import org.mpxj.TimeUnit;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import static org.mpxj.junit.MpxjAssert.assumeJvm;

/**
 * XsdDuration tests.
 */
public class XsdDurationTest
{
   /**
    * Check that we're not in IKVM.
    */
   @Before public void beforeMethod()
   {
      assumeJvm();
   }

   /**
    * Ensure a null duration is handled.
    */
   @Test public void testPrintFromNullDuration()
   {
      XsdDuration xsdDuration = new XsdDuration((Duration) null);
      Assert.assertEquals("PT0H0M0S", xsdDuration.print(true));
   }

   /**
    * Ensure zero duration is handled.
    */
   @Test public void testPrintFromZeroDuration()
   {
      Duration duration = Duration.getInstance(0, TimeUnit.HOURS);
      XsdDuration xsdDuration = new XsdDuration(duration);
      Assert.assertEquals("PT0H0M0S", xsdDuration.print(true));
   }

   /**
    * Ensure a duration in minutes is handled.
    */
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

   /**
    * Ensure a duration in hours is handled.
    */
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

   /**
    * Ensure a duration in days is handled.
    */
   @Test public void testPrintFromDaysDuration()
   {
      Duration duration;
      XsdDuration xsdDuration;

      duration = Duration.getInstance(1, TimeUnit.DAYS);
      xsdDuration = new XsdDuration(duration);
      Assert.assertEquals("P0Y0M1DT0H0M0S", xsdDuration.print(true));

      duration = Duration.getInstance(1.000011574, TimeUnit.DAYS);
      xsdDuration = new XsdDuration(duration);
      Assert.assertEquals("P0Y0M1DT0H0M1S", xsdDuration.print(true));

      duration = Duration.getInstance(1.0006944, TimeUnit.DAYS);
      xsdDuration = new XsdDuration(duration);
      Assert.assertEquals("P0Y0M1DT0H1M0S", xsdDuration.print(true));

      duration = Duration.getInstance(1.04167, TimeUnit.DAYS);
      xsdDuration = new XsdDuration(duration);
      Assert.assertEquals("P0Y0M1DT1H0M0S", xsdDuration.print(true));

      duration = Duration.getInstance(33, TimeUnit.DAYS);
      xsdDuration = new XsdDuration(duration);
      Assert.assertEquals("P0Y0M33DT0H0M0S", xsdDuration.print(true));
   }

   /**
    * Ensure a duration in weeks is handled.
    */
   @Test public void testPrintFromWeeksDuration()
   {
      Duration duration;
      XsdDuration xsdDuration;

      duration = Duration.getInstance(1, TimeUnit.WEEKS);
      xsdDuration = new XsdDuration(duration);
      Assert.assertEquals("P0Y0M7DT0H0M0S", xsdDuration.print(true));

      duration = Duration.getInstance(1.000001653439153, TimeUnit.WEEKS);
      xsdDuration = new XsdDuration(duration);
      Assert.assertEquals("P0Y0M7DT0H0M1S", xsdDuration.print(true));

      duration = Duration.getInstance(1.000099206349206, TimeUnit.WEEKS);
      xsdDuration = new XsdDuration(duration);
      Assert.assertEquals("P0Y0M7DT0H1M0S", xsdDuration.print(true));

      duration = Duration.getInstance(1.005952380952381, TimeUnit.WEEKS);
      xsdDuration = new XsdDuration(duration);
      Assert.assertEquals("P0Y0M7DT1H0M0S", xsdDuration.print(true));

      duration = Duration.getInstance(5, TimeUnit.WEEKS);
      xsdDuration = new XsdDuration(duration);
      Assert.assertEquals("P0Y0M35DT0H0M0S", xsdDuration.print(true));
   }

   /**
    * Ensure a duration in months is handled.
    */
   @Test public void testPrintFromMonthsDuration()
   {
      Duration duration;
      XsdDuration xsdDuration;

      duration = Duration.getInstance(1, TimeUnit.MONTHS);
      xsdDuration = new XsdDuration(duration);
      Assert.assertEquals("P0Y1M0DT0H0M0S", xsdDuration.print(true));

      duration = Duration.getInstance(1.000000413359788, TimeUnit.MONTHS);
      xsdDuration = new XsdDuration(duration);
      Assert.assertEquals("P0Y1M0DT0H0M1S", xsdDuration.print(true));

      duration = Duration.getInstance(1.000024801587302, TimeUnit.MONTHS);
      xsdDuration = new XsdDuration(duration);
      Assert.assertEquals("P0Y1M0DT0H1M0S", xsdDuration.print(true));

      duration = Duration.getInstance(1.001488095238095, TimeUnit.MONTHS);
      xsdDuration = new XsdDuration(duration);
      Assert.assertEquals("P0Y1M0DT1H0M0S", xsdDuration.print(true));

      duration = Duration.getInstance(1.035714285714286, TimeUnit.MONTHS);
      xsdDuration = new XsdDuration(duration);
      Assert.assertEquals("P0Y1M1DT0H0M0S", xsdDuration.print(true));

      duration = Duration.getInstance(13, TimeUnit.MONTHS);
      xsdDuration = new XsdDuration(duration);
      Assert.assertEquals("P0Y13M0DT0H0M0S", xsdDuration.print(true));
   }

   /**
    * Ensure a duration in years is handled.
    */
   @Test public void testPrintFromYearsDuration()
   {
      Duration duration;
      XsdDuration xsdDuration;

      duration = Duration.getInstance(1, TimeUnit.YEARS);
      xsdDuration = new XsdDuration(duration);
      Assert.assertEquals("P1Y0M0DT0H0M0S", xsdDuration.print(true));

      duration = Duration.getInstance(1.000000034446649, TimeUnit.YEARS);
      xsdDuration = new XsdDuration(duration);
      Assert.assertEquals("P1Y0M0DT0H0M1S", xsdDuration.print(true));

      duration = Duration.getInstance(1.000002066798942, TimeUnit.YEARS);
      xsdDuration = new XsdDuration(duration);
      Assert.assertEquals("P1Y0M0DT0H1M0S", xsdDuration.print(true));

      duration = Duration.getInstance(1.000124007936508, TimeUnit.YEARS);
      xsdDuration = new XsdDuration(duration);
      Assert.assertEquals("P1Y0M0DT1H0M0S", xsdDuration.print(true));

      duration = Duration.getInstance(1.00297619047619, TimeUnit.YEARS);
      xsdDuration = new XsdDuration(duration);
      Assert.assertEquals("P1Y0M1DT0H0M0S", xsdDuration.print(true));

      duration = Duration.getInstance(1.083333333333333, TimeUnit.YEARS);
      xsdDuration = new XsdDuration(duration);
      Assert.assertEquals("P1Y1M0DT0H0M0S", xsdDuration.print(true));
   }
}
