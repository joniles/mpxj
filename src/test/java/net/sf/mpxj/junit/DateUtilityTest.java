
package net.sf.mpxj.junit;

import static org.junit.Assert.*;

import java.text.SimpleDateFormat;
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
   @Test public void testCompare() throws Exception
   {
      SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy");
      LocalDateTime date1 = df.parse("15/01/2014");
      LocalDateTime date2 = df.parse("20/01/2014");

      assertEquals(-1, DateHelper.compare(date1, date2));
      assertEquals(1, DateHelper.compare(date2, date1));
      assertEquals(0, DateHelper.compare(date1, date1));
   }

   /**
    * Validate that the DateUtility.min/max methods properly
    * handle null values.
    */
   @Test public void testMinMax() throws Exception
   {
      SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy");
      LocalDateTime date1 = df.parse("15/01/2014");
      LocalDateTime date2 = df.parse("20/01/2014");

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
