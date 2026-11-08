package org.mpxj.mpp;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mpxj.LocalDateTimeRange;
import org.mpxj.LocalTimeRange;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mpxj.junit.MpxjAssert.assumeJvm;

public class TimephasedAdditionalHoursHelperTest
{
   /**
    * Check that we're not in IKVM.
    */
   @BeforeEach public void beforeMethod()
   {
      assumeJvm();
   }

   @Test public void testSplitIntoDayRanges()
   {
      LocalDateTime start = LocalDateTime.of(2026, 11, 8, 9, 0);
      LocalDateTime end = LocalDateTime.of(2026, 11, 8, 17, 0);

      // Simple case
      List<LocalDateTimeRange> ranges = TimephasedAdditionalHoursHelper.splitIntoDayRanges(start, end);
      assertEquals(1, ranges.size());
      assertEquals(start, ranges.get(0).getStart());
      assertEquals(end, ranges.get(0).getEnd());

      // Midnight start, end during the day
      start = LocalDateTime.of(2026, 11, 8, 0, 0);
      ranges = TimephasedAdditionalHoursHelper.splitIntoDayRanges(start, end);
      assertEquals(1, ranges.size());
      assertEquals(start, ranges.get(0).getStart());
      assertEquals(end, ranges.get(0).getEnd());

      // Start during the day, midnight end
      start = LocalDateTime.of(2026, 11, 8, 9, 0);
      end = LocalDateTime.of(2026, 11, 9, 0, 0);
      ranges = TimephasedAdditionalHoursHelper.splitIntoDayRanges(start, end);
      assertEquals(1, ranges.size());
      assertEquals(start, ranges.get(0).getStart());
      assertEquals(end, ranges.get(0).getEnd());

      // Overlap across two days
      start = LocalDateTime.of(2026, 11, 8, 9, 0);
      end = LocalDateTime.of(2026, 11, 9, 9, 0);
      ranges = TimephasedAdditionalHoursHelper.splitIntoDayRanges(start, end);
      assertEquals(2, ranges.size());
      assertEquals(start, ranges.get(0).getStart());
      assertEquals(LocalDateTime.of(2026, 11, 9, 0, 0), ranges.get(0).getEnd());
      assertEquals(LocalDateTime.of(2026, 11, 9, 0, 0), ranges.get(1).getStart());
      assertEquals(end, ranges.get(1).getEnd());

      // Overlap across multiple days
      start = LocalDateTime.of(2026, 11, 8, 9, 0);
      end = LocalDateTime.of(2026, 11, 10, 9, 0);
      ranges = TimephasedAdditionalHoursHelper.splitIntoDayRanges(start, end);
      assertEquals(3, ranges.size());

      assertEquals(start, ranges.get(0).getStart());
      assertEquals(LocalDateTime.of(2026, 11, 9, 0, 0), ranges.get(0).getEnd());

      assertEquals(LocalDateTime.of(2026, 11, 9, 0, 0), ranges.get(1).getStart());
      assertEquals(LocalDateTime.of(2026, 11, 10, 0, 0), ranges.get(1).getEnd());

      assertEquals(LocalDateTime.of(2026, 11, 10, 0, 0), ranges.get(2).getStart());
      assertEquals(end, ranges.get(2).getEnd());
   }

   @Test public void testCreateHoursMap()
   {
      LocalDateTimeRange range1 = new LocalDateTimeRange(LocalDateTime.of(2026, 11, 8, 9, 0), LocalDateTime.of(2026, 11, 8, 12, 0));
      Map<LocalDate, List<LocalTimeRange>> map = TimephasedAdditionalHoursHelper.createHoursMap(Collections.singletonList(range1));
      assertEquals(1, map.size());
      List<LocalTimeRange> times = map.get(LocalDate.of(2026,11, 8));
      assertNotNull(times);
      assertEquals(1, times.size());
      assertEquals(new LocalTimeRange(LocalTime.of(9, 0), LocalTime.of(12, 0)), times.get(0));

      LocalDateTimeRange range2 = new LocalDateTimeRange(LocalDateTime.of(2026, 11, 8, 13, 0), LocalDateTime.of(2026, 11, 8, 17, 0));
      map = TimephasedAdditionalHoursHelper.createHoursMap(Arrays.asList(range1, range2));
      assertEquals(1, map.size());
      times = map.get(LocalDate.of(2026,11, 8));
      assertNotNull(times);
      assertEquals(2, times.size());
      assertEquals(new LocalTimeRange(LocalTime.of(9, 0), LocalTime.of(12, 0)), times.get(0));
      assertEquals(new LocalTimeRange(LocalTime.of(13, 0), LocalTime.of(17, 0)), times.get(1));

      LocalDateTimeRange range3 = new LocalDateTimeRange(LocalDateTime.of(2026, 11, 9, 9, 0), LocalDateTime.of(2026, 11, 9, 12, 0));
      map = TimephasedAdditionalHoursHelper.createHoursMap(Arrays.asList(range1, range2, range3));
      assertEquals(2, map.size());
      times = map.get(LocalDate.of(2026,11, 8));
      assertNotNull(times);
      assertEquals(2, times.size());
      assertEquals(new LocalTimeRange(LocalTime.of(9, 0), LocalTime.of(12, 0)), times.get(0));
      assertEquals(new LocalTimeRange(LocalTime.of(13, 0), LocalTime.of(17, 0)), times.get(1));

      times = map.get(LocalDate.of(2026,11, 9));
      assertNotNull(times);
      assertEquals(1, times.size());
      assertEquals(new LocalTimeRange(LocalTime.of(9, 0), LocalTime.of(12, 0)), times.get(0));

      LocalDateTimeRange range4 = new LocalDateTimeRange(LocalDateTime.of(2026, 11, 9, 9, 0), LocalDateTime.of(2026, 11, 10, 12, 0));
      assertThrows(IllegalArgumentException.class, () -> TimephasedAdditionalHoursHelper.createHoursMap(Arrays.asList(range1, range2, range3, range4)));
   }
}
