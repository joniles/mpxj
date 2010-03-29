/*
 * file:       MppResourceTest.java
 * author:     Wade Golden
 * copyright:  (c) Packwood Software 2006
 * date:       19-September-2006
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

package net.sf.mpxj.junit;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.List;

import net.sf.mpxj.AccrueType;
import net.sf.mpxj.Duration;
import net.sf.mpxj.ProjectFile;
import net.sf.mpxj.Rate;
import net.sf.mpxj.Resource;
import net.sf.mpxj.ResourceAssignment;
import net.sf.mpxj.ResourceType;
import net.sf.mpxj.Task;
import net.sf.mpxj.TimeUnit;
import net.sf.mpxj.WorkContour;
import net.sf.mpxj.mpd.MPDDatabaseReader;
import net.sf.mpxj.mpp.MPPReader;
import net.sf.mpxj.mspdi.MSPDIReader;

/**
 * Tests to exercise MPP file read functionality for various versions of
 * MPP file.
 */
public class MppResourceTest extends MPXJTestCase
{

   /**
    * Test resource data read from an MPP9 file.
    * 
    * @throws Exception
    */
   public void testMpp9Resource() throws Exception
   {
      MPPReader reader = new MPPReader();
      reader.setPreserveNoteFormatting(false);
      ProjectFile mpp = reader.read(m_basedir + "/mpp9resource.mpp");
      testResources(mpp);
      testNotes(mpp);
      testResourceAssignments(mpp);
      testResourceOutlineCodes(mpp);
   }

   /**
    * Test resource data read from an MPP12 file.
    * 
    * @throws Exception
    */
   public void testMpp12Resource() throws Exception
   {
      MPPReader reader = new MPPReader();
      reader.setPreserveNoteFormatting(false);
      ProjectFile mpp = reader.read(m_basedir + "/mpp12resource.mpp");
      testResources(mpp);
      testNotes(mpp);
      testResourceAssignments(mpp);
      testResourceOutlineCodes(mpp);
   }

   /**
    * Test resource data read from an MPP14 file.
    * 
    * @throws Exception
    */
   public void testMpp14Resource() throws Exception
   {
      MPPReader reader = new MPPReader();
      reader.setPreserveNoteFormatting(false);
      ProjectFile mpp = reader.read(m_basedir + "/mpp14resource.mpp");
      testResources(mpp);
      testNotes(mpp);
      testResourceAssignments(mpp);
      testResourceOutlineCodes(mpp);
   }

   /**
    * Test resource data read from an MSPDI file.
    * 
    * @throws Exception
    */
   public void testMspdiResource() throws Exception
   {
      MSPDIReader reader = new MSPDIReader();
      ProjectFile mpp = reader.read(m_basedir + "/mspdiresource.xml");
      testResources(mpp);
      testNotes(mpp);
      testResourceAssignments(mpp);
      //testResourceOutlineCodes(mpp); TODO: MSPDI resource outline code support
   }

   /**
    * Test resource data read from an MPD9 file.
    * 
    * @throws Exception
    */
   public void testMpd9Resource() throws Exception
   {
      try
      {
         MPDDatabaseReader reader = new MPDDatabaseReader();
         reader.setPreserveNoteFormatting(false);
         ProjectFile mpp = reader.read(m_basedir + "/mpp9resource.mpd");
         testResources(mpp);
         testNotes(mpp);
         testResourceAssignments(mpp);
         testResourceOutlineCodes(mpp);
      }

      catch (Exception ex)
      {
         //
         // JDBC not supported in IKVM
         //
         if (!m_ikvm)
         {
            throw ex;
         }
      }
   }

   /**
    * Tests fields related to Resources.
    * 
    * @param mpp The ProjectFile being tested.
    * @throws Exception
    */
   private void testResources(ProjectFile mpp) throws Exception
   {

      /** MPP9 fields that return null:
       *
       * (would like these fixed in MPP9 as well)
       *
       * Material Label
       * Base Calendar
       */

      DateFormat df = new SimpleDateFormat("dd/MM/yyyy");

      List<Resource> listAllResources = mpp.getAllResources();
      assertTrue(listAllResources != null);
      // Fails for MPP12 as there is a summary resource
      //assertEquals(4, listAllResources.size());

      Resource resourceWade = mpp.getResourceByID(Integer.valueOf(1));
      Resource resourceJon = mpp.getResourceByID(Integer.valueOf(2));
      Resource resourceBrian = mpp.getResourceByID(Integer.valueOf(3));
      Resource resourceConcrete = mpp.getResourceByID(Integer.valueOf(4));
      // resource names
      assertEquals("Wade Golden", resourceWade.getName());
      assertEquals("Jon Iles", resourceJon.getName());
      assertEquals("Brian Leach", resourceBrian.getName());
      assertEquals("Concrete", resourceConcrete.getName());
      // type
      assertEquals(ResourceType.WORK, resourceWade.getType());
      assertEquals(ResourceType.MATERIAL, resourceConcrete.getType());
      // material label
      //assertEquals("ton", resourceConcrete.getMaterialLabel());
      // initials
      assertEquals("WG", resourceWade.getInitials());
      //  group
      assertEquals("Steelray", resourceWade.getGroup());
      assertEquals("Tapsterrock", resourceJon.getGroup());
      assertEquals("Steelray", resourceBrian.getGroup());
      assertEquals("Mat", resourceConcrete.getGroup());
      // max units
      assertEquals(Double.valueOf(100), resourceWade.getMaxUnits());
      // std rate
      Rate rate = new Rate(50, TimeUnit.HOURS);
      assertEquals(rate, resourceWade.getStandardRate());
      rate = new Rate(75, TimeUnit.HOURS);
      assertEquals(rate, resourceJon.getStandardRate());
      rate = new Rate(100, TimeUnit.HOURS);
      assertEquals(rate, resourceBrian.getStandardRate());
      // overtime rate
      rate = new Rate(100, TimeUnit.HOURS);
      assertEquals(rate, resourceWade.getOvertimeRate());
      rate = new Rate(150, TimeUnit.HOURS);
      assertEquals(rate, resourceJon.getOvertimeRate());
      rate = new Rate(200, TimeUnit.HOURS);
      assertEquals(rate, resourceBrian.getOvertimeRate());
      // cost per use
      assertEquals(Double.valueOf(500), resourceConcrete.getCostPerUse());
      // accrue type
      assertEquals(AccrueType.END, resourceWade.getAccrueAt());
      assertEquals(AccrueType.PRORATED, resourceJon.getAccrueAt());
      assertEquals(AccrueType.START, resourceConcrete.getAccrueAt());
      // code
      assertEquals("10", resourceWade.getCode());
      assertEquals("20", resourceJon.getCode());
      assertEquals("30", resourceBrian.getCode());

      assertEquals(1, resourceWade.getCost1().intValue());
      assertEquals(2, resourceWade.getCost2().intValue());
      assertEquals(3, resourceWade.getCost3().intValue());
      assertEquals(4, resourceWade.getCost4().intValue());
      assertEquals(5, resourceWade.getCost5().intValue());
      assertEquals(6, resourceWade.getCost6().intValue());
      assertEquals(7, resourceWade.getCost7().intValue());
      assertEquals(8, resourceWade.getCost8().intValue());
      assertEquals(9, resourceWade.getCost9().intValue());
      assertEquals(10, resourceWade.getCost10().intValue());

      assertEquals("wade.golden@steelray.com", resourceWade.getEmailAddress());

      assertEquals("01/01/2006", df.format(resourceWade.getDate1()));
      assertEquals("02/01/2006", df.format(resourceWade.getDate2()));
      assertEquals("03/01/2006", df.format(resourceWade.getDate3()));
      assertEquals("04/01/2006", df.format(resourceWade.getDate4()));
      assertEquals("05/01/2006", df.format(resourceWade.getDate5()));
      assertEquals("06/01/2006", df.format(resourceWade.getDate6()));
      assertEquals("07/01/2006", df.format(resourceWade.getDate7()));
      assertEquals("08/01/2006", df.format(resourceWade.getDate8()));
      assertEquals("09/01/2006", df.format(resourceWade.getDate9()));
      assertEquals("10/01/2006", df.format(resourceWade.getDate10()));

      assertEquals("01/02/2006", df.format(resourceWade.getStart1()));
      assertEquals("02/02/2006", df.format(resourceWade.getStart2()));
      assertEquals("03/02/2006", df.format(resourceWade.getStart3()));
      assertEquals("04/02/2006", df.format(resourceWade.getStart4()));
      assertEquals("05/02/2006", df.format(resourceWade.getStart5()));
      assertEquals("06/02/2006", df.format(resourceWade.getStart6()));
      assertEquals("07/02/2006", df.format(resourceWade.getStart7()));
      assertEquals("08/02/2006", df.format(resourceWade.getStart8()));
      assertEquals("09/02/2006", df.format(resourceWade.getStart9()));
      assertEquals("10/02/2006", df.format(resourceWade.getStart10()));

      assertEquals("01/03/2006", df.format(resourceWade.getFinish1()));
      assertEquals("02/03/2006", df.format(resourceWade.getFinish2()));
      assertEquals("03/03/2006", df.format(resourceWade.getFinish3()));
      assertEquals("04/03/2006", df.format(resourceWade.getFinish4()));
      assertEquals("05/03/2006", df.format(resourceWade.getFinish5()));
      assertEquals("06/03/2006", df.format(resourceWade.getFinish6()));
      assertEquals("07/03/2006", df.format(resourceWade.getFinish7()));
      assertEquals("08/03/2006", df.format(resourceWade.getFinish8()));
      assertEquals("09/03/2006", df.format(resourceWade.getFinish9()));
      assertEquals("10/03/2006", df.format(resourceWade.getFinish10()));

      assertEquals(1, (int) resourceWade.getDuration1().getDuration());
      assertEquals(TimeUnit.DAYS, resourceWade.getDuration1().getUnits());
      assertEquals(2, (int) resourceWade.getDuration2().getDuration());
      assertEquals(TimeUnit.DAYS, resourceWade.getDuration2().getUnits());
      assertEquals(3, (int) resourceWade.getDuration3().getDuration());
      assertEquals(TimeUnit.DAYS, resourceWade.getDuration3().getUnits());
      assertEquals(4, (int) resourceWade.getDuration4().getDuration());
      assertEquals(TimeUnit.DAYS, resourceWade.getDuration4().getUnits());
      assertEquals(5, (int) resourceWade.getDuration5().getDuration());
      assertEquals(TimeUnit.DAYS, resourceWade.getDuration5().getUnits());
      assertEquals(6, (int) resourceWade.getDuration6().getDuration());
      assertEquals(TimeUnit.DAYS, resourceWade.getDuration6().getUnits());
      assertEquals(7, (int) resourceWade.getDuration7().getDuration());
      assertEquals(TimeUnit.DAYS, resourceWade.getDuration7().getUnits());
      assertEquals(8, (int) resourceWade.getDuration8().getDuration());
      assertEquals(TimeUnit.DAYS, resourceWade.getDuration8().getUnits());
      assertEquals(9, (int) resourceWade.getDuration9().getDuration());
      assertEquals(TimeUnit.DAYS, resourceWade.getDuration9().getUnits());
      assertEquals(10, (int) resourceWade.getDuration10().getDuration());
      assertEquals(TimeUnit.DAYS, resourceWade.getDuration10().getUnits());

      assertEquals(1, resourceWade.getNumber1().intValue());
      assertEquals(2, resourceWade.getNumber2().intValue());
      assertEquals(3, resourceWade.getNumber3().intValue());
      assertEquals(4, resourceWade.getNumber4().intValue());
      assertEquals(5, resourceWade.getNumber5().intValue());
      assertEquals(6, resourceWade.getNumber6().intValue());
      assertEquals(7, resourceWade.getNumber7().intValue());
      assertEquals(8, resourceWade.getNumber8().intValue());
      assertEquals(9, resourceWade.getNumber9().intValue());
      assertEquals(10, resourceWade.getNumber10().intValue());
      assertEquals(11, resourceWade.getNumber11().intValue());
      assertEquals(12, resourceWade.getNumber12().intValue());
      assertEquals(13, resourceWade.getNumber13().intValue());
      assertEquals(14, resourceWade.getNumber14().intValue());
      assertEquals(15, resourceWade.getNumber15().intValue());
      assertEquals(16, resourceWade.getNumber16().intValue());
      assertEquals(17, resourceWade.getNumber17().intValue());
      assertEquals(18, resourceWade.getNumber18().intValue());
      assertEquals(19, resourceWade.getNumber19().intValue());
      assertEquals(20, resourceWade.getNumber20().intValue());

      assertEquals("1", resourceWade.getText1());
      assertEquals("2", resourceWade.getText2());
      assertEquals("3", resourceWade.getText3());
      assertEquals("4", resourceWade.getText4());
      assertEquals("5", resourceWade.getText5());
      assertEquals("6", resourceWade.getText6());
      assertEquals("7", resourceWade.getText7());
      assertEquals("8", resourceWade.getText8());
      assertEquals("9", resourceWade.getText9());
      assertEquals("10", resourceWade.getText10());
      assertEquals("11", resourceWade.getText11());
      assertEquals("12", resourceWade.getText12());
      assertEquals("13", resourceWade.getText13());
      assertEquals("14", resourceWade.getText14());
      assertEquals("15", resourceWade.getText15());
      assertEquals("16", resourceWade.getText16());
      assertEquals("17", resourceWade.getText17());
      assertEquals("18", resourceWade.getText18());
      assertEquals("19", resourceWade.getText19());
      assertEquals("20", resourceWade.getText20());
      assertEquals("21", resourceWade.getText21());
      assertEquals("22", resourceWade.getText22());
      assertEquals("23", resourceWade.getText23());
      assertEquals("24", resourceWade.getText24());
      assertEquals("25", resourceWade.getText25());
      assertEquals("26", resourceWade.getText26());
      assertEquals("27", resourceWade.getText27());
      assertEquals("28", resourceWade.getText28());
      assertEquals("29", resourceWade.getText29());
      assertEquals("30", resourceWade.getText30());

      //assertEquals("Standard", resourceWade.getBaseCalendar()); // both of these currently return null from MPP9
      //assertEquals("Night Shift", resourceBrian.getBaseCalendar());
   }

   /**
    * Test resource outline codes.
    * 
    * @param mpp project file
    * @throws Exception
    */
   private void testResourceOutlineCodes(ProjectFile mpp) throws Exception
   {
      Resource resourceWade = mpp.getResourceByID(Integer.valueOf(1));
      assertEquals("AAA", resourceWade.getOutlineCode1());
      assertEquals("BBB", resourceWade.getOutlineCode2());
      assertEquals("CCC", resourceWade.getOutlineCode3());
      assertEquals("DDD", resourceWade.getOutlineCode4());
      assertEquals("EEE", resourceWade.getOutlineCode5());
      assertEquals("FFF", resourceWade.getOutlineCode6());
      assertEquals("GGG", resourceWade.getOutlineCode7());
      assertEquals("HHH", resourceWade.getOutlineCode8());
      assertEquals("III", resourceWade.getOutlineCode9());
      assertEquals("JJJ", resourceWade.getOutlineCode10());
   }

   /**
    * Tests fields related to Resource Assignments.
    * 
    * @param mpp The ProjectFile being tested.
    */
   private void testResourceAssignments(ProjectFile mpp)
   {
      Integer intOne = Integer.valueOf(1);
      DateFormat df = new SimpleDateFormat("dd/MM/yyyy");

      List<ResourceAssignment> listResourceAssignments = mpp.getAllResourceAssignments();

      ResourceAssignment ra = listResourceAssignments.get(0);
      // id
      assertEquals(intOne, ra.getResource().getID());
      assertEquals(intOne, ra.getResourceUniqueID());

      // start and finish
      assertEquals("25/08/2006", df.format(ra.getStart()));
      assertEquals("29/08/2006", df.format(ra.getFinish()));

      // task
      Task task = ra.getTask();
      assertEquals(intOne, task.getID());
      assertEquals(Integer.valueOf(2), task.getUniqueID());
      assertEquals("Task A", task.getName());

      // units
      assertEquals(Double.valueOf(100), ra.getUnits());

      // work and remaining work
      Duration dur24Hours = Duration.getInstance(24, TimeUnit.HOURS);
      assertEquals(dur24Hours, ra.getWork());
      assertEquals(dur24Hours, ra.getRemainingWork());

      //
      // Baseline values
      //
      assertEquals("01/01/2006", df.format(ra.getBaselineStart()));
      assertEquals("02/01/2006", df.format(ra.getBaselineFinish()));
      assertEquals(1, ra.getBaselineCost().intValue());
      assertEquals("2.0h", ra.getBaselineWork().toString());

      // Task 2
      // contour
      ResourceAssignment ra2 = listResourceAssignments.get(3);
      assertEquals(WorkContour.TURTLE, ra2.getWorkContour());

      // Task 3
      // completed
      task = mpp.getTaskByUniqueID(Integer.valueOf(4));
      assertEquals("Completed Task", task.getName());
      ResourceAssignment ra3 = task.getResourceAssignments().get(0);

      //
      // Actual values
      //
      // actual start 26/08/06
      assertEquals("26/08/2006", df.format(ra3.getActualStart()));
      // actual finish 29/08/06
      assertEquals("29/08/2006", df.format(ra3.getActualFinish()));
      // actual work 16h
      assertEquals("16.0h", ra3.getActualWork().toString());
      // actual cost $800
      assertEquals(800, ra3.getActualCost().intValue());

   }

   /**
    * Validates that we are retrieving the notes correctly for each resource.
    * 
    * @param file project file
    */
   private void testNotes(ProjectFile file)
   {
      for (Resource resource : file.getAllResources())
      {
         int id = resource.getID().intValue();
         if (id != 0)
         {
            assertEquals("Resource Notes " + id, resource.getNotes().trim());
         }
      }
   }
}
