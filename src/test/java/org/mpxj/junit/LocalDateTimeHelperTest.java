package org.mpxj.junit;

import java.time.LocalDateTime;

import org.junit.jupiter.api.Test;
import org.mpxj.common.LocalDateTimeHelper;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class LocalDateTimeHelperTest
{
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
