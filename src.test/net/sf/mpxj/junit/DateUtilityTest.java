
package net.sf.mpxj.junit;

import static org.junit.Assert.*;

import java.text.SimpleDateFormat;
import java.util.Date;

import net.sf.mpxj.utility.DateHelper;

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
      Date date1 = df.parse("15/01/2014");
      Date date2 = df.parse("20/01/2014");

      assertEquals(-1, date1.compareTo(date2));
      assertEquals(1, date2.compareTo(date1));
      assertEquals(0, date1.compareTo(date1));

      assertEquals(-1, DateHelper.compare(date1, date2));
      assertEquals(1, DateHelper.compare(date2, date1));
      assertEquals(0, DateHelper.compare(date1, date1));
   }
}
