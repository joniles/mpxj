package org.mpxj.mpp;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mpxj.LocalDateTimeRange;
import org.mpxj.LocalTimeRange;
import org.mpxj.ProjectCalendarHours;

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

   @Test public void testPopulateAdditionalWorkingHours()
   {
      ProjectCalendarHours originalHours = new ProjectCalendarHours();
      originalHours.add(new LocalTimeRange(LocalTime.of(9, 0), LocalTime.of(12, 0)));
      originalHours.add(new LocalTimeRange(LocalTime.of(13, 0), LocalTime.of(17, 0)));
      List<LocalDateTimeRange> list = new ArrayList<>();

      // Invalid range
      LocalDateTimeRange invalidRange = new LocalDateTimeRange(LocalDateTime.of(2026, 11, 9, 9, 0), LocalDateTime.of(2026, 11, 10, 12, 0));
      assertThrows(IllegalArgumentException.class, () -> TimephasedAdditionalHoursHelper.populateAdditionalWorkingHours(originalHours, list, invalidRange));

      // Range entirely within an existing working range
      LocalDate rangeDate = LocalDate.of(2026, 11, 9);
      LocalDateTimeRange range = new LocalDateTimeRange(LocalDateTime.of(rangeDate, LocalTime.of(10, 0)), LocalDateTime.of(rangeDate, LocalTime.of(11, 0)));
      TimephasedAdditionalHoursHelper.populateAdditionalWorkingHours(originalHours, list, range);
      assertEquals(0, list.size());
      range = new LocalDateTimeRange(LocalDateTime.of(2026, 11, 9, 14, 0), LocalDateTime.of(2026, 11, 9, 16, 0));
      TimephasedAdditionalHoursHelper.populateAdditionalWorkingHours(originalHours, list, range);
      assertEquals(0, list.size());

      // Range is exactly an existing working range
      range = new LocalDateTimeRange(LocalDateTime.of(rangeDate, LocalTime.of(9, 0)), LocalDateTime.of(rangeDate, LocalTime.of(12, 0)));
      TimephasedAdditionalHoursHelper.populateAdditionalWorkingHours(originalHours, list, range);
      assertEquals(0, list.size());
      range = new LocalDateTimeRange(LocalDateTime.of(2026, 11, 9, 13, 0), LocalDateTime.of(2026, 11, 9, 17, 0));
      TimephasedAdditionalHoursHelper.populateAdditionalWorkingHours(originalHours, list, range);
      assertEquals(0, list.size());

      // Range overlaps the start of an existing working range
      range = new LocalDateTimeRange(LocalDateTime.of(rangeDate, LocalTime.of(8, 0)), LocalDateTime.of(rangeDate, LocalTime.of(10, 0)));
      TimephasedAdditionalHoursHelper.populateAdditionalWorkingHours(originalHours, list, range);
      assertEquals(1, list.size());
      assertEquals(new LocalDateTimeRange(LocalDateTime.of(rangeDate, LocalTime.of(8,0)), LocalDateTime.of(rangeDate, LocalTime.of(9,0))), list.get(0));
      list.clear();
      range = new LocalDateTimeRange(LocalDateTime.of(rangeDate, LocalTime.of(12, 0)), LocalDateTime.of(rangeDate, LocalTime.of(14, 0)));
      TimephasedAdditionalHoursHelper.populateAdditionalWorkingHours(originalHours, list, range);
      assertEquals(1, list.size());
      assertEquals(new LocalDateTimeRange(LocalDateTime.of(rangeDate, LocalTime.of(12,0)), LocalDateTime.of(rangeDate, LocalTime.of(13,0))), list.get(0));
      list.clear();
      range = new LocalDateTimeRange(LocalDateTime.of(rangeDate, LocalTime.of(0, 0)), LocalDateTime.of(rangeDate, LocalTime.of(10, 0)));
      TimephasedAdditionalHoursHelper.populateAdditionalWorkingHours(originalHours, list, range);
      assertEquals(1, list.size());
      assertEquals(new LocalDateTimeRange(LocalDateTime.of(rangeDate, LocalTime.of(0,0)), LocalDateTime.of(rangeDate, LocalTime.of(9,0))), list.get(0));
      list.clear();

      // Range overlaps the end of an existing working range
      range = new LocalDateTimeRange(LocalDateTime.of(rangeDate, LocalTime.of(11, 0)), LocalDateTime.of(rangeDate, LocalTime.of(13, 0)));
      TimephasedAdditionalHoursHelper.populateAdditionalWorkingHours(originalHours, list, range);
      assertEquals(1, list.size());
      assertEquals(new LocalDateTimeRange(LocalDateTime.of(rangeDate, LocalTime.of(12,0)), LocalDateTime.of(rangeDate, LocalTime.of(13,0))), list.get(0));
      list.clear();
      range = new LocalDateTimeRange(LocalDateTime.of(rangeDate, LocalTime.of(16, 0)), LocalDateTime.of(rangeDate, LocalTime.of(18, 0)));
      TimephasedAdditionalHoursHelper.populateAdditionalWorkingHours(originalHours, list, range);
      assertEquals(1, list.size());
      assertEquals(new LocalDateTimeRange(LocalDateTime.of(rangeDate, LocalTime.of(17,0)), LocalDateTime.of(rangeDate, LocalTime.of(18,0))), list.get(0));
      list.clear();
      range = new LocalDateTimeRange(LocalDateTime.of(rangeDate, LocalTime.of(16, 0)), LocalDateTime.of(rangeDate.plusDays(1), LocalTime.of(0, 0)));
      TimephasedAdditionalHoursHelper.populateAdditionalWorkingHours(originalHours, list, range);
      assertEquals(1, list.size());
      assertEquals(new LocalDateTimeRange(LocalDateTime.of(rangeDate, LocalTime.of(17,0)), LocalDateTime.of(rangeDate, LocalTime.of(0,0))), list.get(0));
      list.clear();

      // Range starts and ends within two consecutive working ranges
      range = new LocalDateTimeRange(LocalDateTime.of(rangeDate, LocalTime.of(11, 0)), LocalDateTime.of(rangeDate, LocalTime.of(14, 0)));
      TimephasedAdditionalHoursHelper.populateAdditionalWorkingHours(originalHours, list, range);
      assertEquals(1, list.size());
      assertEquals(new LocalDateTimeRange(LocalDateTime.of(rangeDate, LocalTime.of(12,0)), LocalDateTime.of(rangeDate, LocalTime.of(13,0))), list.get(0));
      list.clear();

      // Range starts and ends exactly covering two consecutive working ranges
      range = new LocalDateTimeRange(LocalDateTime.of(rangeDate, LocalTime.of(9, 0)), LocalDateTime.of(rangeDate, LocalTime.of(17, 0)));
      TimephasedAdditionalHoursHelper.populateAdditionalWorkingHours(originalHours, list, range);
      assertEquals(1, list.size());
      assertEquals(new LocalDateTimeRange(LocalDateTime.of(rangeDate, LocalTime.of(12,0)), LocalDateTime.of(rangeDate, LocalTime.of(13,0))), list.get(0));
      list.clear();

      // Range overlaps two consecutive working ranges
      range = new LocalDateTimeRange(LocalDateTime.of(rangeDate, LocalTime.of(8, 0)), LocalDateTime.of(rangeDate, LocalTime.of(18, 0)));
      TimephasedAdditionalHoursHelper.populateAdditionalWorkingHours(originalHours, list, range);
      assertEquals(3, list.size());
      assertEquals(new LocalDateTimeRange(LocalDateTime.of(rangeDate, LocalTime.of(8,0)), LocalDateTime.of(rangeDate, LocalTime.of(9,0))), list.get(0));
      assertEquals(new LocalDateTimeRange(LocalDateTime.of(rangeDate, LocalTime.of(12,0)), LocalDateTime.of(rangeDate, LocalTime.of(13,0))), list.get(1));
      assertEquals(new LocalDateTimeRange(LocalDateTime.of(rangeDate, LocalTime.of(17,0)), LocalDateTime.of(rangeDate, LocalTime.of(18,0))), list.get(2));
      list.clear();
      range = new LocalDateTimeRange(LocalDateTime.of(rangeDate, LocalTime.of(0, 0)), LocalDateTime.of(rangeDate.plusDays(1), LocalTime.of(0, 0)));
      TimephasedAdditionalHoursHelper.populateAdditionalWorkingHours(originalHours, list, range);
      assertEquals(3, list.size());
      assertEquals(new LocalDateTimeRange(LocalDateTime.of(rangeDate, LocalTime.of(0,0)), LocalDateTime.of(rangeDate, LocalTime.of(9,0))), list.get(0));
      assertEquals(new LocalDateTimeRange(LocalDateTime.of(rangeDate, LocalTime.of(12,0)), LocalDateTime.of(rangeDate, LocalTime.of(13,0))), list.get(1));
      assertEquals(new LocalDateTimeRange(LocalDateTime.of(rangeDate, LocalTime.of(17,0)), LocalDateTime.of(rangeDate, LocalTime.of(0,0))), list.get(2));
      list.clear();

      // Range is entirely before the first working range
      range = new LocalDateTimeRange(LocalDateTime.of(rangeDate, LocalTime.of(7, 0)), LocalDateTime.of(rangeDate, LocalTime.of(8, 0)));
      TimephasedAdditionalHoursHelper.populateAdditionalWorkingHours(originalHours, list, range);
      assertEquals(1, list.size());
      assertEquals(new LocalDateTimeRange(LocalDateTime.of(rangeDate, LocalTime.of(7,0)), LocalDateTime.of(rangeDate, LocalTime.of(8,0))), list.get(0));
      list.clear();

      // Range is entirely after the last working range
      range = new LocalDateTimeRange(LocalDateTime.of(rangeDate, LocalTime.of(19, 0)), LocalDateTime.of(rangeDate, LocalTime.of(20, 0)));
      TimephasedAdditionalHoursHelper.populateAdditionalWorkingHours(originalHours, list, range);
      assertEquals(1, list.size());
      assertEquals(new LocalDateTimeRange(LocalDateTime.of(rangeDate, LocalTime.of(19,0)), LocalDateTime.of(rangeDate, LocalTime.of(20,0))), list.get(0));
      list.clear();

      // Original hours starts at midnight
      originalHours.clear();
      originalHours.add(new LocalTimeRange(LocalTime.of(0, 0), LocalTime.of(12, 0)));
      originalHours.add(new LocalTimeRange(LocalTime.of(13, 0), LocalTime.of(17, 0)));

      // Range overlaps end
      range = new LocalDateTimeRange(LocalDateTime.of(rangeDate, LocalTime.of(11, 0)), LocalDateTime.of(rangeDate, LocalTime.of(13, 0)));
      TimephasedAdditionalHoursHelper.populateAdditionalWorkingHours(originalHours, list, range);
      assertEquals(1, list.size());
      assertEquals(new LocalDateTimeRange(LocalDateTime.of(rangeDate, LocalTime.of(12,0)), LocalDateTime.of(rangeDate, LocalTime.of(13,0))), list.get(0));
      list.clear();
      range = new LocalDateTimeRange(LocalDateTime.of(rangeDate, LocalTime.of(0, 0)), LocalDateTime.of(rangeDate, LocalTime.of(13, 0)));
      TimephasedAdditionalHoursHelper.populateAdditionalWorkingHours(originalHours, list, range);
      assertEquals(1, list.size());
      assertEquals(new LocalDateTimeRange(LocalDateTime.of(rangeDate, LocalTime.of(12,0)), LocalDateTime.of(rangeDate, LocalTime.of(13,0))), list.get(0));
      list.clear();

      // Original hours ends at midnight
      originalHours.clear();
      originalHours.add(new LocalTimeRange(LocalTime.of(9, 0), LocalTime.of(12, 0)));
      originalHours.add(new LocalTimeRange(LocalTime.of(13, 0), LocalTime.of(0, 0)));

      // Range overlaps start
      range = new LocalDateTimeRange(LocalDateTime.of(rangeDate, LocalTime.of(12, 0)), LocalDateTime.of(rangeDate, LocalTime.of(14, 0)));
      TimephasedAdditionalHoursHelper.populateAdditionalWorkingHours(originalHours, list, range);
      assertEquals(1, list.size());
      assertEquals(new LocalDateTimeRange(LocalDateTime.of(rangeDate, LocalTime.of(12,0)), LocalDateTime.of(rangeDate, LocalTime.of(13,0))), list.get(0));
      list.clear();
      range = new LocalDateTimeRange(LocalDateTime.of(rangeDate, LocalTime.of(12, 0)), LocalDateTime.of(rangeDate.plusDays(1), LocalTime.of(0, 0)));
      TimephasedAdditionalHoursHelper.populateAdditionalWorkingHours(originalHours, list, range);
      assertEquals(1, list.size());
      assertEquals(new LocalDateTimeRange(LocalDateTime.of(rangeDate, LocalTime.of(12,0)), LocalDateTime.of(rangeDate, LocalTime.of(13,0))), list.get(0));
      list.clear();

      // Original hours run from midnight to midnight
      originalHours.clear();
      originalHours.add(new LocalTimeRange(LocalTime.of(0, 0), LocalTime.of(0, 0)));

      // Range is within the 24h range
      range = new LocalDateTimeRange(LocalDateTime.of(rangeDate, LocalTime.of(12, 0)), LocalDateTime.of(rangeDate, LocalTime.of(14, 0)));
      TimephasedAdditionalHoursHelper.populateAdditionalWorkingHours(originalHours, list, range);
      assertEquals(0, list.size());

      // Range is at the start of the 24h range
      range = new LocalDateTimeRange(LocalDateTime.of(rangeDate, LocalTime.of(0, 0)), LocalDateTime.of(rangeDate, LocalTime.of(14, 0)));
      TimephasedAdditionalHoursHelper.populateAdditionalWorkingHours(originalHours, list, range);
      assertEquals(0, list.size());

      // Range is at the end of the 24h range
      range = new LocalDateTimeRange(LocalDateTime.of(rangeDate, LocalTime.of(14, 0)), LocalDateTime.of(rangeDate.plusDays(1), LocalTime.of(0, 0)));
      TimephasedAdditionalHoursHelper.populateAdditionalWorkingHours(originalHours, list, range);
      assertEquals(0, list.size());

      // Range covers the 24h range
      range = new LocalDateTimeRange(LocalDateTime.of(rangeDate, LocalTime.of(0, 0)), LocalDateTime.of(rangeDate.plusDays(1), LocalTime.of(0, 0)));
      TimephasedAdditionalHoursHelper.populateAdditionalWorkingHours(originalHours, list, range);
      assertEquals(0, list.size());
   }
}
