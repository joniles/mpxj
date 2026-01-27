package org.mpxj.junit;

import java.sql.Time;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.mpxj.Duration;
import org.mpxj.LocalDateTimeRange;
import org.mpxj.ProjectCalendar;
import org.mpxj.ProjectFile;
import org.mpxj.TimeUnit;
import org.mpxj.TimephasedWork;
import org.mpxj.utility.TimephasedUtility;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

public class TimephasedUtilityTest
{

   @Test public void testSegmentation()
   {
      ProjectFile file = new ProjectFile();
      ProjectCalendar calendar = file.addDefaultBaseCalendar();

      List<LocalDateTimeRange> ranges = new ArrayList<>();
      ranges.add(new LocalDateTimeRange(LocalDateTime.of(2026, 1, 27, 6, 0), LocalDateTime.of(2026, 1, 27, 7, 0)));
      ranges.add(new LocalDateTimeRange(LocalDateTime.of(2026, 1, 27, 7, 0), LocalDateTime.of(2026, 1, 27, 8, 0)));

      // No work
      List<Duration> result = TimephasedUtility.segmentWork(calendar, Collections.emptyList(), ranges);
      assertEquals(2,  result.size());
      assertNull(result.get(0));
      assertNull(result.get(1));

      // Work starts after ranges
      TimephasedWork item = new TimephasedWork();
      item.setStart(LocalDateTime.of(2026, 1, 28, 8, 0));
      item.setFinish(LocalDateTime.of(2026, 1, 28, 17, 0));
      item.setTotalAmount(Duration.getInstance(8, TimeUnit.HOURS));
      item.setAmountPerHour(Duration.getInstance(60, TimeUnit.MINUTES));
      List<TimephasedWork> items = Collections.singletonList(item);

      result = TimephasedUtility.segmentWork(calendar, items, ranges);
      assertEquals(2,  result.size());
      assertNull(result.get(0));
      assertNull(result.get(1));

      // Range overlaps work start
      ranges.clear();
      ranges.add(new LocalDateTimeRange(LocalDateTime.of(2026, 1, 28, 7, 0), LocalDateTime.of(2026, 1, 28, 9, 0)));
      result = TimephasedUtility.segmentWork(calendar, items, ranges);
      assertEquals(1,  result.size());
      assertEquals(Duration.getInstance(60, TimeUnit.MINUTES), result.get(0));

      // Range starts at work start
      ranges.clear();
      ranges.add(new LocalDateTimeRange(LocalDateTime.of(2026, 1, 28, 8, 0), LocalDateTime.of(2026, 1, 28, 9, 0)));
      result = TimephasedUtility.segmentWork(calendar, items, ranges);
      assertEquals(1,  result.size());
      assertEquals(Duration.getInstance(60, TimeUnit.MINUTES), result.get(0));

      // Range starts at work start, with multiple hours
      ranges.clear();
      ranges.add(new LocalDateTimeRange(LocalDateTime.of(2026, 1, 28, 8, 0), LocalDateTime.of(2026, 1, 28, 10, 0)));
      result = TimephasedUtility.segmentWork(calendar, items, ranges);
      assertEquals(1,  result.size());
      assertEquals(Duration.getInstance(120, TimeUnit.MINUTES), result.get(0));

      // Range is within the work item
      ranges.clear();
      ranges.add(new LocalDateTimeRange(LocalDateTime.of(2026, 1, 28, 9, 0), LocalDateTime.of(2026, 1, 28, 10, 0)));
      result = TimephasedUtility.segmentWork(calendar, items, ranges);
      assertEquals(1,  result.size());
      assertEquals(Duration.getInstance(60, TimeUnit.MINUTES), result.get(0));

      // Range is within the work item with multiple hours
      ranges.clear();
      ranges.add(new LocalDateTimeRange(LocalDateTime.of(2026, 1, 28, 9, 0), LocalDateTime.of(2026, 1, 28, 11, 0)));
      result = TimephasedUtility.segmentWork(calendar, items, ranges);
      assertEquals(1,  result.size());
      assertEquals(Duration.getInstance(120, TimeUnit.MINUTES), result.get(0));

      // Range crosses non-working time start
      ranges.clear();
      ranges.add(new LocalDateTimeRange(LocalDateTime.of(2026, 1, 28, 11, 0), LocalDateTime.of(2026, 1, 28, 13, 0)));
      result = TimephasedUtility.segmentWork(calendar, items, ranges);
      assertEquals(1,  result.size());
      assertEquals(Duration.getInstance(60, TimeUnit.MINUTES), result.get(0));

      // Range is exactly non-working time
      ranges.clear();
      ranges.add(new LocalDateTimeRange(LocalDateTime.of(2026, 1, 28, 12, 0), LocalDateTime.of(2026, 1, 28, 13, 0)));
      result = TimephasedUtility.segmentWork(calendar, items, ranges);
      assertEquals(1,  result.size());
      assertNull(result.get(0));

      // Range is within non-working time
      ranges.clear();
      ranges.add(new LocalDateTimeRange(LocalDateTime.of(2026, 1, 28, 12, 15), LocalDateTime.of(2026, 1, 28, 12, 45)));
      result = TimephasedUtility.segmentWork(calendar, items, ranges);
      assertEquals(1,  result.size());
      assertNull(result.get(0));

      // Range crosses non-working time end
      ranges.clear();
      ranges.add(new LocalDateTimeRange(LocalDateTime.of(2026, 1, 28, 12, 0), LocalDateTime.of(2026, 1, 28, 14, 0)));
      result = TimephasedUtility.segmentWork(calendar, items, ranges);
      assertEquals(1,  result.size());
      assertEquals(Duration.getInstance(60, TimeUnit.MINUTES), result.get(0));

      // Range is exactly work
      ranges.clear();
      ranges.add(new LocalDateTimeRange(LocalDateTime.of(2026, 1, 28, 8, 0), LocalDateTime.of(2026, 1, 28, 17, 0)));
      result = TimephasedUtility.segmentWork(calendar, items, ranges);
      assertEquals(1,  result.size());
      assertEquals(Duration.getInstance(8*60, TimeUnit.MINUTES), result.get(0));

      // Range ends at work end
      ranges.clear();
      ranges.add(new LocalDateTimeRange(LocalDateTime.of(2026, 1, 28, 16, 0), LocalDateTime.of(2026, 1, 28, 17, 0)));
      result = TimephasedUtility.segmentWork(calendar, items, ranges);
      assertEquals(1,  result.size());
      assertEquals(Duration.getInstance(60, TimeUnit.MINUTES), result.get(0));

      // Range ends at work end with multiple hours
      ranges.clear();
      ranges.add(new LocalDateTimeRange(LocalDateTime.of(2026, 1, 28, 15, 0), LocalDateTime.of(2026, 1, 28, 17, 0)));
      result = TimephasedUtility.segmentWork(calendar, items, ranges);
      assertEquals(1,  result.size());
      assertEquals(Duration.getInstance(120, TimeUnit.MINUTES), result.get(0));

      // Range overlaps work end
      ranges.clear();
      ranges.add(new LocalDateTimeRange(LocalDateTime.of(2026, 1, 28, 16, 0), LocalDateTime.of(2026, 1, 28, 18, 0)));
      result = TimephasedUtility.segmentWork(calendar, items, ranges);
      assertEquals(1,  result.size());
      assertEquals(Duration.getInstance(60, TimeUnit.MINUTES), result.get(0));

      // Hourly ranges overlapping the whole work item
      ranges.clear();
      ranges.add(new LocalDateTimeRange(LocalDateTime.of(2026, 1, 28, 7, 0), LocalDateTime.of(2026, 1, 28, 8, 0)));
      ranges.add(new LocalDateTimeRange(LocalDateTime.of(2026, 1, 28, 8, 0), LocalDateTime.of(2026, 1, 28, 9, 0)));
      ranges.add(new LocalDateTimeRange(LocalDateTime.of(2026, 1, 28, 9, 0), LocalDateTime.of(2026, 1, 28, 10, 0)));
      ranges.add(new LocalDateTimeRange(LocalDateTime.of(2026, 1, 28, 10, 0), LocalDateTime.of(2026, 1, 28, 11, 0)));
      ranges.add(new LocalDateTimeRange(LocalDateTime.of(2026, 1, 28, 11, 0), LocalDateTime.of(2026, 1, 28, 12, 0)));
      ranges.add(new LocalDateTimeRange(LocalDateTime.of(2026, 1, 28, 12, 0), LocalDateTime.of(2026, 1, 28, 13, 0)));
      ranges.add(new LocalDateTimeRange(LocalDateTime.of(2026, 1, 28, 13, 0), LocalDateTime.of(2026, 1, 28, 14, 0)));
      ranges.add(new LocalDateTimeRange(LocalDateTime.of(2026, 1, 28, 14, 0), LocalDateTime.of(2026, 1, 28, 15, 0)));
      ranges.add(new LocalDateTimeRange(LocalDateTime.of(2026, 1, 28, 15, 0), LocalDateTime.of(2026, 1, 28, 16, 0)));
      ranges.add(new LocalDateTimeRange(LocalDateTime.of(2026, 1, 28, 16, 0), LocalDateTime.of(2026, 1, 28, 17, 0)));
      ranges.add(new LocalDateTimeRange(LocalDateTime.of(2026, 1, 28, 17, 0), LocalDateTime.of(2026, 1, 28, 18, 0)));

      result = TimephasedUtility.segmentWork(calendar, items, ranges);
      assertEquals(11,  result.size());
      assertNull(result.get(0));
      assertEquals(Duration.getInstance(60, TimeUnit.MINUTES), result.get(1));
      assertEquals(Duration.getInstance(60, TimeUnit.MINUTES), result.get(2));
      assertEquals(Duration.getInstance(60, TimeUnit.MINUTES), result.get(3));
      assertEquals(Duration.getInstance(60, TimeUnit.MINUTES), result.get(4));
      assertNull(result.get(5));
      assertEquals(Duration.getInstance(60, TimeUnit.MINUTES), result.get(6));
      assertEquals(Duration.getInstance(60, TimeUnit.MINUTES), result.get(7));
      assertEquals(Duration.getInstance(60, TimeUnit.MINUTES), result.get(8));
      assertEquals(Duration.getInstance(60, TimeUnit.MINUTES), result.get(9));
      assertNull(result.get(10));

      // Multiple normal work items
      items = new ArrayList<>();

      item = new TimephasedWork();
      item.setStart(LocalDateTime.of(2026, 1, 28, 8, 0));
      item.setFinish(LocalDateTime.of(2026, 1, 28, 10, 0));
      item.setTotalAmount(Duration.getInstance(2, TimeUnit.HOURS));
      item.setAmountPerHour(Duration.getInstance(60, TimeUnit.MINUTES));
      items.add(item);

      item = new TimephasedWork();
      item.setStart(LocalDateTime.of(2026, 1, 28, 10, 0));
      item.setFinish(LocalDateTime.of(2026, 1, 28, 12, 0));
      item.setTotalAmount(Duration.getInstance(1, TimeUnit.HOURS));
      item.setAmountPerHour(Duration.getInstance(30, TimeUnit.MINUTES));
      items.add(item);

      item = new TimephasedWork();
      item.setStart(LocalDateTime.of(2026, 1, 28, 13, 0));
      item.setFinish(LocalDateTime.of(2026, 1, 28, 15, 0));
      item.setTotalAmount(Duration.getInstance(2, TimeUnit.HOURS));
      item.setAmountPerHour(Duration.getInstance(60, TimeUnit.MINUTES));
      items.add(item);

      item = new TimephasedWork();
      item.setStart(LocalDateTime.of(2026, 1, 28, 15, 0));
      item.setFinish(LocalDateTime.of(2026, 1, 28, 17, 0));
      item.setTotalAmount(Duration.getInstance(1, TimeUnit.HOURS));
      item.setAmountPerHour(Duration.getInstance(30, TimeUnit.MINUTES));
      items.add(item);

      // Overlaps first two ranges
      ranges.clear();
      ranges.add(new LocalDateTimeRange(LocalDateTime.of(2026, 1, 28, 0, 0), LocalDateTime.of(2026, 1, 28, 12, 0)));
      result = TimephasedUtility.segmentWork(calendar, items, ranges);
      assertEquals(1,  result.size());
      assertEquals(Duration.getInstance(3 * 60, TimeUnit.MINUTES), result.get(0));

      // Overlaps last two ranges
      ranges.clear();
      ranges.add(new LocalDateTimeRange(LocalDateTime.of(2026, 1, 28, 12, 0), LocalDateTime.of(2026, 1, 29, 0, 0)));
      result = TimephasedUtility.segmentWork(calendar, items, ranges);
      assertEquals(1,  result.size());
      assertEquals(Duration.getInstance(3 * 60, TimeUnit.MINUTES), result.get(0));

      // Overlaps all ranges
      ranges.clear();
      ranges.add(new LocalDateTimeRange(LocalDateTime.of(2026, 1, 28, 0, 0), LocalDateTime.of(2026, 1, 29, 0, 0)));
      result = TimephasedUtility.segmentWork(calendar, items, ranges);
      assertEquals(1,  result.size());
      assertEquals(Duration.getInstance(6 * 60, TimeUnit.MINUTES), result.get(0));


      // work in non-working time
      items.clear();

      item = new TimephasedWork();
      item.setStart(LocalDateTime.of(2026, 1, 28, 7, 0));
      item.setFinish(LocalDateTime.of(2026, 1, 28, 8, 0));
      item.setTotalAmount(Duration.getInstance(1, TimeUnit.HOURS));
      item.setAmountPerHour(Duration.getInstance(60, TimeUnit.MINUTES));
      items.add(item);

      item = new TimephasedWork();
      item.setStart(LocalDateTime.of(2026, 1, 28, 8, 0));
      item.setFinish(LocalDateTime.of(2026, 1, 28, 17, 0));
      item.setTotalAmount(Duration.getInstance(8, TimeUnit.HOURS));
      item.setAmountPerHour(Duration.getInstance(60, TimeUnit.MINUTES));
      items.add(item);

      // Hourly ranges
      ranges.clear();
      ranges.add(new LocalDateTimeRange(LocalDateTime.of(2026, 1, 28, 6, 0), LocalDateTime.of(2026, 1, 28, 7, 0)));
      ranges.add(new LocalDateTimeRange(LocalDateTime.of(2026, 1, 28, 7, 0), LocalDateTime.of(2026, 1, 28, 8, 0)));
      ranges.add(new LocalDateTimeRange(LocalDateTime.of(2026, 1, 28, 8, 0), LocalDateTime.of(2026, 1, 28, 9, 0)));
      ranges.add(new LocalDateTimeRange(LocalDateTime.of(2026, 1, 28, 9, 0), LocalDateTime.of(2026, 1, 28, 10, 0)));

      result = TimephasedUtility.segmentWork(calendar, items, ranges);
      assertEquals(4,  result.size());
      assertNull(result.get(0));
      assertEquals(Duration.getInstance(60, TimeUnit.MINUTES), result.get(1));
      assertEquals(Duration.getInstance(60, TimeUnit.MINUTES), result.get(2));
      assertEquals(Duration.getInstance(60, TimeUnit.MINUTES), result.get(3));

      // no work in working time
      items.clear();

      item = new TimephasedWork();
      item.setStart(LocalDateTime.of(2026, 1, 28, 8, 0));
      item.setFinish(LocalDateTime.of(2026, 1, 28, 9, 0));
      item.setTotalAmount(Duration.getInstance(0, TimeUnit.HOURS));
      item.setAmountPerHour(Duration.getInstance(0, TimeUnit.MINUTES));
      items.add(item);

      item = new TimephasedWork();
      item.setStart(LocalDateTime.of(2026, 1, 28, 9, 0));
      item.setFinish(LocalDateTime.of(2026, 1, 28, 17, 0));
      item.setTotalAmount(Duration.getInstance(7, TimeUnit.HOURS));
      item.setAmountPerHour(Duration.getInstance(60, TimeUnit.MINUTES));
      items.add(item);

      result = TimephasedUtility.segmentWork(calendar, items, ranges);
      assertEquals(4,  result.size());
      assertNull(result.get(0));
      assertNull(result.get(1));
      assertEquals(Duration.getInstance(0, TimeUnit.MINUTES), result.get(2));
      assertEquals(Duration.getInstance(60, TimeUnit.MINUTES), result.get(3));
   }
}
