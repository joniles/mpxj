/*
 * file:       SemVerTest.java
 * author:     Jon Iles
 * copyright:  (c) Packwood Software 2020
 * date:       2020-06-16
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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import org.mpxj.common.SemVer;

/**
 * Validate SemVer class functionality.
 */
public class SemVerTest
{
   /**
    * Ensure that versions constructed from integers behave as expected.
    */
   @Test public void testIntegerSemVer()
   {
      assertEquals(0, new SemVer(1, 0, 0).compareTo(new SemVer(1, 0, 0)));
      assertEquals(0, new SemVer(1, 0, 0).compareTo(new SemVer(1, 0)));
      assertTrue(new SemVer(1, 1, 0).compareTo(new SemVer(1, 0, 0)) > 0);
      assertTrue(new SemVer(1, 0, 0).compareTo(new SemVer(1, 1, 0)) < 0);
   }

   /**
    * Ensure that versions constructed from strings behave as expected.
    */
   @Test public void testStringSemVer()
   {
      assertEquals(0, new SemVer("1.0.0").compareTo(new SemVer("1.0.0")));
      assertEquals(0, new SemVer("1.0.0").compareTo(new SemVer("1.0")));
      assertTrue(new SemVer("1.1.0").compareTo(new SemVer("1.0.0")) > 0);
      assertTrue(new SemVer("1.0.0").compareTo(new SemVer("1.1.0")) < 0);
   }
}
