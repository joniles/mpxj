package net.sf.mpxj.junit;
import net.sf.mpxj.ProjectFile;
import net.sf.mpxj.RelationshipLagCalendar;
import net.sf.mpxj.primavera.PrimaveraPMFileReader;
import net.sf.mpxj.primavera.PrimaveraXERFileReader;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class XerRelationshipLagCalendarTest
{
   @Test
   public void testProjectDefaultCalendar() throws Exception {
      ProjectFile xml = new PrimaveraXERFileReader().read(MpxjTestData.filePath("ProjectDefaultCalendar.xer"));
      assertEquals(xml.getProjectProperties().getRelationshipLagCalendar(), RelationshipLagCalendar.PROJECT_DEFAULT);
   }

   @Test
   public void testPredecessorCalendar() throws Exception {
      ProjectFile xml = new PrimaveraXERFileReader().read(MpxjTestData.filePath("PredecessorCalendar.xer"));
      assertEquals(xml.getProjectProperties().getRelationshipLagCalendar(), RelationshipLagCalendar.PREDECESSOR);
   }

   @Test
   public void testSuccessorCalendar() throws Exception {
      ProjectFile xml = new PrimaveraXERFileReader().read(MpxjTestData.filePath("SuccessorCalendar.xer"));
      assertEquals(xml.getProjectProperties().getRelationshipLagCalendar(), RelationshipLagCalendar.SUCCESSOR);
   }

   @Test
   public void testTwentyFourHourCalendar() throws Exception {
      ProjectFile xml = new PrimaveraXERFileReader().read(MpxjTestData.filePath("TwentyFourHourCalendar.xer"));
      assertEquals(xml.getProjectProperties().getRelationshipLagCalendar(), RelationshipLagCalendar.TWENTY_FOUR_HOUR);
   }
}
