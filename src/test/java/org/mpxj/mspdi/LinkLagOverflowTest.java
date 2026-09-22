/*
 * Tests for MSPDI predecessor LinkLag integer overflow.
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

package org.mpxj.mspdi;

import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mpxj.Duration;
import org.mpxj.MPXJException;
import org.mpxj.ProjectFile;
import org.mpxj.Relation;
import org.mpxj.TimeUnit;
import org.mpxj.junit.MpxjTestData;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mpxj.junit.MpxjAssert.assumeJvm;

/**
 * Tests that MSPDI predecessor LinkLag values cannot silently overflow.
 */
public class LinkLagOverflowTest
{
   @BeforeEach public void beforeMethod()
   {
      assumeJvm();
   }

   @Test public void testValidLinkLagValues() throws Exception
   {
      for (String value : new String[]
      {
         "-2147483648", "-4800", "0", "4800", "2147483647"
      })
      {
         ProjectFile project = readWithLinkLag(value);
         Relation relation = project.getTaskByUniqueID(Integer.valueOf(3)).getPredecessors().get(0);
         Duration lag = relation.getLag();

         Duration minutes = Duration.convertUnits(
            lag.getDuration(),
            lag.getUnits(),
            TimeUnit.MINUTES,
            project.getProjectProperties());

         assertEquals(Long.parseLong(value) / 10.0, minutes.getDuration(), 0.05, value);
         assertEquals(2, relation.getPredecessorTask().getUniqueID().intValue());
      }
   }

   @Test public void testMissingLinkLagDefaultsToZero() throws Exception
   {
      ProjectFile project = readWithLinkLag(null);
      Relation relation = project.getTaskByUniqueID(Integer.valueOf(3)).getPredecessors().get(0);

      assertEquals(0.0, relation.getLag().getDuration());
   }

   @Test public void testOverflowStopsImport() throws Exception
   {
      for (String value : new String[]
      {
         "-2147483649",
         "2147483648",
         "4294967296",
         "9223372036854775808"
      })
      {
         for (boolean ignoreErrors : new boolean[] { true, false })
         {
            MSPDIReader reader = new MSPDIReader();
            reader.setIgnoreErrors(ignoreErrors);

            MPXJException exception = assertThrows(
               MPXJException.class,
               () -> readWithLinkLag(reader, value));

            assertTrue(exception.getMessage().contains("LinkLag"), value);
            assertTrue(exception.getMessage().contains(value), value);
            assertInstanceOf(ArithmeticException.class, exception.getCause());
         }
      }
   }

   private ProjectFile readWithLinkLag(String value) throws Exception
   {
      return readWithLinkLag(new MSPDIReader(), value);
   }

   private ProjectFile readWithLinkLag(MSPDIReader reader, String value) throws Exception
   {
      String xml = new String(
         Files.readAllBytes(Paths.get(MpxjTestData.filePath("mspdirelations.xml"))),
         StandardCharsets.UTF_8);

      String original = "<LinkLag>4800</LinkLag>";
      assertEquals(1, xml.split(original, -1).length - 1);

      String replacement = value == null ? "" : "<LinkLag>" + value + "</LinkLag>";
      xml = xml.replace(original, replacement);

      return reader.read(new ByteArrayInputStream(xml.getBytes(StandardCharsets.UTF_8)));
   }
}
