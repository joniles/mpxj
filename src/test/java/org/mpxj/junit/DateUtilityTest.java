
package org.mpxj.junit;

import static org.junit.Assert.*;

import java.time.LocalDateTime;

import org.mpxj.common.LocalDateTimeHelper;
import org.junit.Test;

/**
 * Unit tests for the DateUtility class.
 */
public class DateUtilityTest
{

   /**
    * Validate that the DateUtility.compare method matches the semantics
    * of the Date.compareTo method when used with non-null values.
    */
   @Test public void testCompare()
   {
      LocalDateTime date1 = LocalDateTime.of(2014, 1, 15, 0, 0);
      LocalDateTime date2 = LocalDateTime.of(2014, 1, 20, 0, 0);

      assertTrue(LocalDateTimeHelper.compare(date1, date2) < 0);
      assertTrue(LocalDateTimeHelper.compare(date2, date1) > 0);
      assertEquals(0, LocalDateTimeHelper.compare(date1, date1));
   }

   /**
    * Validate that the DateUtility.min/max methods properly
    * handle null values.
    */
   @Test public void testMinMax()
   {
      LocalDateTime date1 = LocalDateTime.of(2014, 1, 15, 0, 0);
      LocalDateTime date2 = LocalDateTime.of(2014, 1, 20, 0, 0);

      assertNull(LocalDateTimeHelper.min(null, null));
      assertEquals(date1, LocalDateTimeHelper.min(null, date1));
      assertEquals(date1, LocalDateTimeHelper.min(date1, null));
      assertEquals(date1, LocalDateTimeHelper.min(date1, date2));
      assertEquals(date1, LocalDateTimeHelper.min(date2, date1));

      assertNull(LocalDateTimeHelper.max(null, null));
      assertEquals(date1, LocalDateTimeHelper.max(null, date1));
      assertEquals(date1, LocalDateTimeHelper.max(date1, null));
      assertEquals(date2, LocalDateTimeHelper.max(date1, date2));
      assertEquals(date2, LocalDateTimeHelper.max(date2, date1));
   }
}
