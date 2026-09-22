/*
 * file:       ProjectConversionOverflowTest.java
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

import org.junit.jupiter.api.Test;
import org.mpxj.MPXJException;
import org.mpxj.ProjectFile;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * Tests for overflowing MSPDI project conversion parameters.
 */
public class ProjectConversionOverflowTest
{
   @Test public void testRejectOutOfRangeMinutesPerDay()
   {
      String xml = "<Project xmlns=\"http://schemas.microsoft.com/project\">"
         + "<MinutesPerDay>2147483648</MinutesPerDay>"
         + "</Project>";

      ByteArrayInputStream input = new ByteArrayInputStream(
         xml.getBytes(StandardCharsets.UTF_8));

      assertThrows(MPXJException.class, () -> new MSPDIReader().read(input));
   }

   @Test public void testRejectOverflowInDerivedMinutesPerWeek()
   {
      String xml = "<Project xmlns=\"http://schemas.microsoft.com/project\">"
         + "<MinutesPerDay>429496730</MinutesPerDay>"
         + "</Project>";

      ByteArrayInputStream input = new ByteArrayInputStream(
         xml.getBytes(StandardCharsets.UTF_8));

      assertThrows(MPXJException.class, () -> new MSPDIReader().read(input));
   }

   private ProjectFile readProject(String fields) throws MPXJException
   {
      String xml = "<Project xmlns=\"http://schemas.microsoft.com/project\">"
         + fields + "</Project>";

      return new MSPDIReader().read(new ByteArrayInputStream(
         xml.getBytes(StandardCharsets.UTF_8)));
   }

   @Test public void testExplicitConversionBounds() throws MPXJException
   {
      for (int value : new int[] { Integer.MIN_VALUE, Integer.MAX_VALUE })
      {
         assertEquals(Integer.valueOf(value), readProject(
            "<MinutesPerDay>" + value + "</MinutesPerDay>"
               + "<MinutesPerWeek>0</MinutesPerWeek>"
         ).getProjectProperties().getMinutesPerDay());

         assertEquals(Integer.valueOf(value), readProject(
            "<MinutesPerWeek>" + value + "</MinutesPerWeek>"
         ).getProjectProperties().getMinutesPerWeek());

         assertEquals(Integer.valueOf(value), readProject(
            "<DaysPerMonth>" + value + "</DaysPerMonth>"
         ).getProjectProperties().getDaysPerMonth());
      }

      for (String field : new String[] { "MinutesPerDay", "MinutesPerWeek", "DaysPerMonth" })
      {
         for (String value : new String[] { "2147483648", "-2147483649" })
         {
            MPXJException ex = assertThrows(MPXJException.class, () ->
               readProject("<" + field + ">" + value + "</" + field + ">"));

            assertEquals(field + " exceeds the signed 32-bit integer range", ex.getMessage());
         }
      }
   }

   @Test public void testDerivedWeekBoundsAndExplicitOverride() throws MPXJException
   {
      for (int day : new int[] { -429496729, 429496729 })
      {
         assertEquals(Integer.valueOf(day * 5), readProject(
            "<MinutesPerDay>" + day + "</MinutesPerDay>"
         ).getProjectProperties().getMinutesPerWeek());
      }

      for (int day : new int[] { -429496730, 429496730 })
      {
         MPXJException ex = assertThrows(MPXJException.class, () ->
            readProject("<MinutesPerDay>" + day + "</MinutesPerDay>"));

         assertEquals(
            "Derived MinutesPerWeek exceeds the signed 32-bit integer range",
            ex.getMessage());
      }

      ProjectFile project = readProject(
         "<MinutesPerDay>429496730</MinutesPerDay>"
            + "<MinutesPerWeek>1</MinutesPerWeek>");

      assertEquals(Integer.valueOf(429496730),
         project.getProjectProperties().getMinutesPerDay());
      assertEquals(Integer.valueOf(1),
         project.getProjectProperties().getMinutesPerWeek());
   }
}
