/*
 * file:       SumTest.java
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

package org.mpxj.junit;

import java.util.Arrays;

import org.junit.jupiter.api.Test;
import org.mpxj.common.NumberHelper;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Tests for NumberHelper.sumAsDouble, which sums costs.
 *
 * This uses the JDK's compensated summation, whose last bits are not the same on every JDK: both
 * DoubleStream.sum and DoubleSummaryStatistics add the compensation on Java 8 and subtract it on
 * later versions. So these tests pin the behaviour callers rely on, and the agreement between the
 * two JDK routes, rather than hard coding sums which would only hold on one runtime.
 */
public class SumTest
{
   /**
    * Test that summing matches the stream the callers previously used, whichever JDK this is.
    */
   @Test public void matchesDoubleStreamSum()
   {
      double[][] cases = new double[][]
      {
         {
            52873.71,
            82254.83
         },
         {
            12892.19,
            56542.29
         },
         {
            44290.35,
            5978.17,
            69428.46
         },
         {
            0.1,
            0.2
         },
         {
            1e16,
            1.0,
            -1e16
         }
      };

      for (double[] values : cases)
      {
         Number[] boxed = new Number[values.length];
         for (int index = 0; index < values.length; index++)
         {
            boxed[index] = Double.valueOf(values[index]);
         }

         assertEquals(Double.valueOf(Arrays.stream(values).sum()), NumberHelper.sumAsDouble(boxed), Arrays.toString(values));
      }
   }

   /**
    * Test that exactly representable values sum exactly, and that no values sum to zero.
    */
   @Test public void exactValuesSumExactly()
   {
      assertEquals(Double.valueOf(0.75), NumberHelper.sumAsDouble(Double.valueOf(0.5), Double.valueOf(0.25)));
      assertEquals(Double.valueOf(0), NumberHelper.sumAsDouble(Double.valueOf(1.5), Double.valueOf(-1.5)));
      assertEquals(Double.valueOf(0), NumberHelper.sumAsDouble());
   }

   /**
    * Test that a null value counts as zero, and that a zero result is a positive zero. Double.equals
    * compares bits, so a negative zero would not be equal to the zero callers expect.
    */
   @Test public void nullValuesCountAsZero()
   {
      assertEquals(Double.valueOf(0), NumberHelper.sumAsDouble((Number) null, null));
      assertEquals(Double.valueOf(7.5), NumberHelper.sumAsDouble(Double.valueOf(7.5), null));
      assertEquals(Double.valueOf(0), NumberHelper.sumAsDouble(Double.valueOf(-0.0), Double.valueOf(-0.0)));
   }

   /**
    * Test that same signed infinities sum to that infinity rather than NaN, which is what the
    * simple sum held alongside the compensated one is for.
    */
   @Test public void sameSignedInfinitiesDoNotBecomeNaN()
   {
      assertEquals(Double.valueOf(Double.POSITIVE_INFINITY), NumberHelper.sumAsDouble(Double.valueOf(Double.POSITIVE_INFINITY), Double.valueOf(Double.POSITIVE_INFINITY)));
      assertEquals(Double.valueOf(Double.NEGATIVE_INFINITY), NumberHelper.sumAsDouble(Double.valueOf(Double.NEGATIVE_INFINITY), Double.valueOf(Double.NEGATIVE_INFINITY)));
   }

   /**
    * Test that mixed number types are summed by their double value.
    */
   @Test public void mixedNumberTypesAreSummed()
   {
      assertEquals(Double.valueOf(6.5), NumberHelper.sumAsDouble(Integer.valueOf(2), Long.valueOf(3), Double.valueOf(1.5)));
   }
}
