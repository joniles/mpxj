/*
 * file:       LocalDateTimeHelperTest.java
 * author:     Jon Iles
 * date:       2026-02-13
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

import java.time.LocalDateTime;

import org.junit.jupiter.api.Test;
import org.mpxj.common.LocalDateTimeHelper;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * LocalDateTimeHelper help method tests.
 */
public class LocalDateTimeHelperTest
{
   /**
    * Test  range comparison method.
    */
   @Test public void rangeComparisonTests()
   {
      LocalDateTime startRange1 = LocalDateTime.of(2026, 2, 1, 9, 0);
      LocalDateTime endRange1 = LocalDateTime.of(2026, 2, 2, 17, 0);

      LocalDateTime startRange2 = LocalDateTime.of(2026, 2, 7, 9, 0);
      LocalDateTime endRange2 = LocalDateTime.of(2026, 2, 8, 17, 0);

      // range 1 is before range 2
      assertEquals(-1, LocalDateTimeHelper.compare(startRange1, endRange1, startRange2, endRange2));

      // range 2 is after range 1
      assertEquals(1, LocalDateTimeHelper.compare(startRange2, endRange2, startRange1, endRange1));

      // range 1 is exactly range 1
      assertEquals(0, LocalDateTimeHelper.compare(startRange1, endRange1, startRange1, endRange1));

      // range 1 overlaps the start of range 2
      startRange1 = LocalDateTime.of(2026, 2, 6, 9, 0);
      endRange1 = LocalDateTime.of(2026, 2, 7, 17, 0);
      assertEquals(0, LocalDateTimeHelper.compare(startRange1, endRange1, startRange1, endRange1));

      // range 1 starts at the start of range 2
      startRange1 = startRange2;
      endRange1 = LocalDateTime.of(2026, 2, 7, 17, 0);
      assertEquals(0, LocalDateTimeHelper.compare(startRange1, endRange1, startRange1, endRange1));

      // range 1 entirely within range 2
      startRange1 = LocalDateTime.of(2026, 2, 7, 12, 0);
      endRange1 = LocalDateTime.of(2026, 2, 7, 17, 0);
      assertEquals(0, LocalDateTimeHelper.compare(startRange1, endRange1, startRange1, endRange1));

      // range 1 overlaps the end of range 2
      startRange1 = LocalDateTime.of(2026, 2, 8, 9, 0);
      endRange1 = LocalDateTime.of(2026, 2, 9, 17, 0);
      assertEquals(0, LocalDateTimeHelper.compare(startRange1, endRange1, startRange1, endRange1));

      // range 1 ends at the end of range 2
      startRange1 = LocalDateTime.of(2026, 2, 8, 9, 0);
      endRange1 = endRange2;
      assertEquals(0, LocalDateTimeHelper.compare(startRange1, endRange1, startRange1, endRange1));
   }
}
