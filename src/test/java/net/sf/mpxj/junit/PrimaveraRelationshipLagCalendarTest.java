package net.sf.mpxj.junit;
import net.sf.mpxj.ProjectFile;
import net.sf.mpxj.RelationshipLagCalendar;
import net.sf.mpxj.primavera.PrimaveraPMFileReader;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class PrimaveraRelationshipLagCalendarTest
{
   @Test
   public void testProjectDefaultCalendar() throws Exception {
      ProjectFile xml = new PrimaveraPMFileReader().read(MpxjTestData.filePath("ProjectDefaultCalendar.xml"));
      assertEquals(xml.getProjectProperties().getRelationshipLagCalendar(), RelationshipLagCalendar.PROJECT_DEFAULT);
   }

   @Test
   public void testPredecessorCalendar() throws Exception {
      ProjectFile xml = new PrimaveraPMFileReader().read(MpxjTestData.filePath("PredecessorCalendar.xml"));
      assertEquals(xml.getProjectProperties().getRelationshipLagCalendar(), RelationshipLagCalendar.PREDECESSOR);
   }

   @Test
   public void testSuccessorCalendar() throws Exception {
      ProjectFile xml = new PrimaveraPMFileReader().read(MpxjTestData.filePath("SuccessorCalendar.xml"));
      assertEquals(xml.getProjectProperties().getRelationshipLagCalendar(), RelationshipLagCalendar.SUCCESSOR);
   }

   @Test
   public void testTwentyFourHourCalendar() throws Exception {
      ProjectFile xml = new PrimaveraPMFileReader().read(MpxjTestData.filePath("TwentyFourHourCalendar.xml"));
      assertEquals(xml.getProjectProperties().getRelationshipLagCalendar(), RelationshipLagCalendar.TWENTY_FOUR_HOUR);
   }
}
