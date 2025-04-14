/*
 * file:       XmlRelationshipLagCalendarTest.java
 * author:     Rohit Sinha
 * date:       22/09/2023
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

package org.mpxj.junit;

import org.mpxj.ProjectFile;
import org.mpxj.RelationshipLagCalendar;
import org.mpxj.primavera.PrimaveraPMFileReader;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * Validate relationship lag calendar read correctly.
 */
public class XmlRelationshipLagCalendarTest
{
   /**
    * Validate default lag calendar read correctly.
    */
   @Test public void testProjectDefaultCalendar() throws Exception
   {
      ProjectFile xml = new PrimaveraPMFileReader().read(MpxjTestData.filePath("ProjectDefaultCalendar.xml"));
      assertEquals(RelationshipLagCalendar.PROJECT_DEFAULT, xml.getProjectProperties().getRelationshipLagCalendar());
   }

   /**
    * Validate predecessor lag calendar read correctly.
    */
   @Test public void testPredecessorCalendar() throws Exception
   {
      ProjectFile xml = new PrimaveraPMFileReader().read(MpxjTestData.filePath("PredecessorCalendar.xml"));
      assertEquals(RelationshipLagCalendar.PREDECESSOR, xml.getProjectProperties().getRelationshipLagCalendar());
   }

   /**
    * Validate successor lag calendar read correctly.
    */
   @Test public void testSuccessorCalendar() throws Exception
   {
      ProjectFile xml = new PrimaveraPMFileReader().read(MpxjTestData.filePath("SuccessorCalendar.xml"));
      assertEquals(RelationshipLagCalendar.SUCCESSOR, xml.getProjectProperties().getRelationshipLagCalendar());
   }

   /**
    * Validate 24-hour lag calendar read correctly.
    */
   @Test public void testTwentyFourHourCalendar() throws Exception
   {
      ProjectFile xml = new PrimaveraPMFileReader().read(MpxjTestData.filePath("TwentyFourHourCalendar.xml"));
      assertEquals(RelationshipLagCalendar.TWENTY_FOUR_HOUR, xml.getProjectProperties().getRelationshipLagCalendar());
   }
}
