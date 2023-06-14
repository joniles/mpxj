
package net.sf.mpxj.junit;

import static org.junit.Assert.*;

import java.time.LocalDateTime;

import net.sf.mpxj.common.DateHelper;

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
      LocalDateTime date1 = LocalDateTime.of(2014,1,15, 0, 0, 0);
      LocalDateTime date2 = LocalDateTime.of(2014,1,20, 0, 0, 0);

      assertEquals(-1, DateHelper.compare(date1, date2));
      assertEquals(1, DateHelper.compare(date2, date1));
      assertEquals(0, DateHelper.compare(date1, date1));
   }

   /**
    * Validate that the DateUtility.min/max methods properly
    * handle null values.
    */
   @Test public void testMinMax()
   {
      LocalDateTime date1 = LocalDateTime.of(2014,1,15, 0, 0, 0);
      LocalDateTime date2 = LocalDateTime.of(2014,1,20, 0, 0, 0);

      assertNull(DateHelper.min(null, null));
      assertEquals(date1, DateHelper.min(null, date1));
      assertEquals(date1, DateHelper.min(date1, null));
      assertEquals(date1, DateHelper.min(date1, date2));
      assertEquals(date1, DateHelper.min(date2, date1));

      assertNull(DateHelper.max(null, null));
      assertEquals(date1, DateHelper.max(null, date1));
      assertEquals(date1, DateHelper.max(date1, null));
      assertEquals(date2, DateHelper.max(date1, date2));
      assertEquals(date2, DateHelper.max(date2, date1));
   }
}
