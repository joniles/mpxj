/*
 * file:       WorkHelperTest.java
 * author:     Petr Janeček
 * date:       2026-07-29
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

package org.mpxj.primavera;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mpxj.Duration;
import org.mpxj.TimeUnit;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mpxj.junit.MpxjAssert.assumeJvm;

/**
 * Tests for WorkHelper.addWork. In this package because WorkHelper is package private.
 */
public class WorkHelperTest
{
   /**
    * Check that we're not in IKVM.
    */
   @BeforeEach public void beforeMethod()
   {
      assumeJvm();
   }

   /**
    * Test that null work values are ignored, and that the result is in hours either way.
    */
   @Test public void nullValuesAreIgnored()
   {
      Duration sum = WorkHelper.addWork(Duration.getInstance(8, TimeUnit.HOURS), null);
      assertEquals(8.0, sum.getDuration(), 0.0);
      assertEquals(TimeUnit.HOURS, sum.getUnits());

      Duration none = WorkHelper.addWork(null, null);
      assertNotNull(none);
      assertEquals(0.0, none.getDuration(), 0.0);
      assertEquals(TimeUnit.HOURS, none.getUnits());
   }

   /**
    * Test that work is summed by its own value and the result is labelled hours, without converting
    * between units. Recorded because the values being summed carry units of their own.
    */
   @Test public void valuesAreSummedWithoutConversion()
   {
      Duration sum = WorkHelper.addWork(Duration.getInstance(1, TimeUnit.DAYS), Duration.getInstance(2, TimeUnit.HOURS));
      assertEquals(3.0, sum.getDuration(), 0.0);
      assertEquals(TimeUnit.HOURS, sum.getUnits());
   }

   /**
    * Test that same signed infinities sum to that infinity rather than NaN.
    */
   @Test public void sameSignedInfinitiesDoNotBecomeNaN()
   {
      Duration sum = WorkHelper.addWork(Duration.getInstance(Double.POSITIVE_INFINITY, TimeUnit.HOURS), Duration.getInstance(Double.POSITIVE_INFINITY, TimeUnit.HOURS));
      assertEquals(Double.POSITIVE_INFINITY, sum.getDuration(), 0.0);
   }
}
