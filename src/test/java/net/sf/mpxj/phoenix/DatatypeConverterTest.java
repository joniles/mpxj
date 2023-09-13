package net.sf.mpxj.phoenix;
import org.junit.Test;

import static org.junit.Assert.assertNotNull;

public class DatatypeConverterTest
{
   @Test
   public void testConvertingDates() {
      assertNotNull(DatatypeConverter.parseDateTime("20230215T000000"));
      assertNotNull(DatatypeConverter.parseDateTime("20230214T235959.999999"));
   }
}
