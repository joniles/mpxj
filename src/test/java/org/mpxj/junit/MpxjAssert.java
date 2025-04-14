/*
 * file:       MpxjAssert.java
 * author:     Jon Iles
 * copyright:  (c) Packwood Software 2014
 * date:       20/08/2014
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

import org.mpxj.Duration;
import org.mpxj.TimeUnit;
import org.mpxj.common.JvmHelper;
import org.junit.Assume;

/**
 * MPXJ's own unit test assertion methods.
 */
public final class MpxjAssert
{
   /**
    * Private constructor to prevent instantiation.
    */
   private MpxjAssert()
   {

   }

   /**
    * Allows a test to be ignored if it is running under IKVM (i.e. not a regular JVM)
    */
   public static void assumeJvm()
   {
      Assume.assumeFalse(JvmHelper.isIkvm());
   }

   /**
    * Assert equality for booleans. This method allows us to maintain autoboxing warnings for the rest of the code.
    *
    * @param message error message
    * @param expected expected value
    * @param actual actual value
    */
   public static void assertBooleanEquals(String message, boolean expected, boolean actual)
   {
      org.junit.Assert.assertEquals(message, Boolean.valueOf(expected), Boolean.valueOf(actual));
   }

   /**
    * Assert equality for booleans. This method allows us to maintain autoboxing warnings for the rest of the code.
    *
    * @param expected expected value
    * @param actual actual value
    */
   public static void assertBooleanEquals(boolean expected, boolean actual)
   {
      org.junit.Assert.assertEquals(Boolean.valueOf(expected), Boolean.valueOf(actual));
   }

   /**
    * Assert method used to test durations.
    *
    * @param expectedDuration expected duration size
    * @param expectedUnits expected duration units
    * @param duration duration under test
    */
   public static void assertDurationEquals(double expectedDuration, TimeUnit expectedUnits, Duration duration)
   {
      org.junit.Assert.assertEquals(expectedDuration, duration.getDuration(), 0.005);
      org.junit.Assert.assertEquals(expectedUnits, duration.getUnits());
   }
}
