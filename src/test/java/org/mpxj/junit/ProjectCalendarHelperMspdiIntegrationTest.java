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

package org.mpxj.junit;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilderFactory;

import org.junit.jupiter.api.Test;
import org.mpxj.DayType;
import org.mpxj.LocalDateRange;
import org.mpxj.LocalTimeRange;
import org.mpxj.ProjectCalendar;
import org.mpxj.ProjectCalendarWeek;
import org.mpxj.ProjectFile;
import org.mpxj.TemporaryCalendar;
import org.mpxj.listener.DefaultProjectListener;
import org.mpxj.mspdi.MSPDIWriter;
import org.mpxj.mspdi.SaveVersion;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

/**
 * Exercise work week metadata through a real MSPDI export which flattens a derived calendar.
 */
public class ProjectCalendarHelperMspdiIntegrationTest
{
   /**
    * A derived calendar without resources is flattened before MSPDI serialization.
    * Check both native work weeks and the legacy dated exceptions actually written.
    */
   @Test public void testFlattenedWorkWeekExport() throws Exception
   {
      ProjectFile project = new ProjectFile();
      ProjectCalendar base = project.addDefaultBaseCalendar();
      ProjectCalendar derived = project.addCalendar();
      derived.setName("U04a derived");
      derived.setParent(base);

      ProjectCalendarWeek week = derived.addWorkWeek();
      week.setName("U04A_TEST");
      week.setDateRange(new LocalDateRange(LocalDate.of(2026, 11, 2), LocalDate.of(2026, 11, 8)));
      for (DayOfWeek day : DayOfWeek.values())
      {
         derived.setCalendarDayType(day, DayType.NON_WORKING);
         week.setCalendarDayType(day, DayType.NON_WORKING);
      }
      week.setCalendarDayType(DayOfWeek.MONDAY, DayType.WORKING);
      week.getCalendarHours(DayOfWeek.MONDAY).add(new LocalTimeRange(LocalTime.of(8, 0), LocalTime.of(12, 0)));
      assertTrue(project.getResources().isEmpty());
      assertTrue(base.getWorkWeeks().isEmpty());
      assertTrue(derived.isDerived());

      List<ProjectCalendar> written = new ArrayList<>();
      project.getEventManager().addProjectListener(new DefaultProjectListener()
      {
         @Override public void calendarWritten(ProjectCalendar calendar)
         {
            if (derived.getUniqueID().equals(calendar.getUniqueID()))
            {
               written.add(calendar);
            }
         }
      });

      // Retain the generated XML as an experimental artifact under Maven's output directory.
      Path output = Paths.get("target", "u04a-mspdi-integration", "export.xml");
      Files.createDirectories(output.getParent());
      MSPDIWriter writer = new MSPDIWriter();
      writer.setSaveVersion(SaveVersion.Project2016);
      assertDoesNotThrow(() -> writer.write(project, output.toFile()));

      assertEquals(1, written.size());
      ProjectCalendar flattened = written.get(0);
      assertTrue(flattened instanceof TemporaryCalendar);
      assertNotSame(derived, flattened);
      assertNull(flattened.getParent());
      assertSame(base, derived.getParent());
      assertEquals(1, flattened.getWorkWeeks().size());
      assertNotSame(week, flattened.getWorkWeeks().get(0));
      assertEquals(week.getName(), flattened.getWorkWeeks().get(0).getName());
      assertEquals(week.getDateRange(), flattened.getWorkWeeks().get(0).getDateRange());

      DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
      factory.setNamespaceAware(true);
      factory.setFeature("http://apache.org/xml/features/disallow-doctype-decl", true);
      Document document = factory.newDocumentBuilder().parse(output.toFile());
      NodeList calendars = document.getElementsByTagNameNS(NAMESPACE, "Calendar");
      assertEquals(2, calendars.getLength());
      Element calendar = null;
      for (int index = 0; index < calendars.getLength(); index++)
      {
         Element candidate = (Element) calendars.item(index);
         if ("U04a derived".equals(child(candidate, "Name").getTextContent()))
         {
            calendar = candidate;
         }
      }
      assertTrue(calendar != null, "Derived calendar is missing from exported XML");
      assertEquals("1", child(calendar, "IsBaseCalendar").getTextContent());
      assertEquals("-1", child(calendar, "BaseCalendarUID").getTextContent());

      NodeList weeks = child(calendar, "WorkWeeks").getElementsByTagNameNS(NAMESPACE, "WorkWeek");
      assertEquals(1, weeks.getLength());
      Element xmlWeek = (Element) weeks.item(0);
      assertEquals("U04A_TEST", child(xmlWeek, "Name").getTextContent());
      assertPeriod(child(xmlWeek, "TimePeriod"), LocalDate.of(2026, 11, 2), LocalDate.of(2026, 11, 8));
      NodeList weekDays = child(xmlWeek, "WeekDays").getElementsByTagNameNS(NAMESPACE, "WeekDay");
      assertEquals(7, weekDays.getLength());
      for (int index = 0; index < weekDays.getLength(); index++)
      {
         Element day = (Element) weekDays.item(index);
         assertEquals(Integer.toString(index + 1), child(day, "DayType").getTextContent());
         assertHours(day, index == 1);
      }

      NodeList calendarDays = child(calendar, "WeekDays").getElementsByTagNameNS(NAMESPACE, "WeekDay");
      int exceptionCount = 0;
      for (int index = 0; index < calendarDays.getLength(); index++)
      {
         Element day = (Element) calendarDays.item(index);
         if ("0".equals(child(day, "DayType").getTextContent()))
         {
            LocalDate date = LocalDate.of(2026, 11, 2).plusDays(exceptionCount);
            assertPeriod(child(day, "TimePeriod"), date, date);
            assertHours(day, exceptionCount == 0);
            exceptionCount++;
         }
      }
      assertEquals(7, exceptionCount);
   }

   /**
    * Check the inclusive dates and full-day bounds emitted by the writer.
    *
    * @param period XML period
    * @param from first date
    * @param to last date
    */
   private void assertPeriod(Element period, LocalDate from, LocalDate to)
   {
      assertEquals(from.atStartOfDay(), LocalDateTime.parse(child(period, "FromDate").getTextContent()));
      assertEquals(to.atTime(23, 59, 59), LocalDateTime.parse(child(period, "ToDate").getTextContent()));
   }

   /**
    * Check Monday's hours, or an explicitly non-working day with no hours.
    *
    * @param day XML day
    * @param working expected working flag
    */
   private void assertHours(Element day, boolean working)
   {
      assertEquals(working ? "1" : "0", child(day, "DayWorking").getTextContent());
      NodeList times = day.getElementsByTagNameNS(NAMESPACE, "WorkingTime");
      assertEquals(working ? 1 : 0, times.getLength());
      if (working)
      {
         Element time = (Element) times.item(0);
         assertEquals(LocalTime.of(8, 0), LocalTime.parse(child(time, "FromTime").getTextContent()));
         assertEquals(LocalTime.of(12, 0), LocalTime.parse(child(time, "ToTime").getTextContent()));
      }
   }

   /**
    * Find a direct child in the MSPDI namespace.
    *
    * @param parent XML parent
    * @param name child local name
    * @return child element
    */
   private Element child(Element parent, String name)
   {
      for (Node node = parent.getFirstChild(); node != null; node = node.getNextSibling())
      {
         if (NAMESPACE.equals(node.getNamespaceURI()) && name.equals(node.getLocalName()))
         {
            return (Element) node;
         }
      }
      fail("Missing " + name + " in " + parent.getLocalName());
      return null;
   }

   private static final String NAMESPACE = "http://schemas.microsoft.com/project";
}
