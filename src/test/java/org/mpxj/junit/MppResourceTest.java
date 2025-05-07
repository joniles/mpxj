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

package org.mpxj.junit;

import static org.junit.Assert.*;

import java.time.format.DateTimeFormatter;
import java.util.List;

import org.mpxj.AccrueType;
import org.mpxj.Duration;
import org.mpxj.ProjectFile;
import org.mpxj.Rate;
import org.mpxj.Resource;
import org.mpxj.ResourceAssignment;
import org.mpxj.ResourceType;
import org.mpxj.Task;
import org.mpxj.TimeUnit;
import org.mpxj.WorkContour;
import org.mpxj.mpd.MPDFileReader;
import org.mpxj.mpp.MPPReader;
import org.mpxj.mspdi.MSPDIReader;

import org.junit.Test;

/**
 * Tests to exercise MPP file read functionality for various versions of
 * MPP file.
 */
public class MppResourceTest
{

   /**
    * Test resource data read from an MPP9 file.
    */
   @Test public void testMpp9Resource() throws Exception
   {
      MPPReader reader = new MPPReader();
      ProjectFile mpp = reader.read(MpxjTestData.filePath("mpp9resource.mpp"));
      testResources(mpp);
      testNotes(mpp);
      testResourceAssignments(mpp);
      testResourceOutlineCodes(mpp);
   }

   /**
    * Test resource data read from an MPP9 file saved by Project 2007.
    */
   @Test public void testMpp9ResourceFrom12() throws Exception
   {
      MPPReader reader = new MPPReader();
      ProjectFile mpp = reader.read(MpxjTestData.filePath("mpp9resource-from12.mpp"));
      testResources(mpp);
      testNotes(mpp);
      testResourceAssignments(mpp);
      testResourceOutlineCodes(mpp);
   }

   /**
    * Test resource data read from an MPP9 file saved by Project 2010.
    */
   @Test public void testMpp9ResourceFrom14() throws Exception
   {
      MPPReader reader = new MPPReader();
      ProjectFile mpp = reader.read(MpxjTestData.filePath("mpp9resource-from14.mpp"));
      testResources(mpp);
      testNotes(mpp);
      testResourceAssignments(mpp);
      testResourceOutlineCodes(mpp);
   }

   /**
    * Test resource data read from an MPP12 file.
    */
   @Test public void testMpp12Resource() throws Exception
   {
      MPPReader reader = new MPPReader();
      ProjectFile mpp = reader.read(MpxjTestData.filePath("mpp12resource.mpp"));
      testResources(mpp);
      testNotes(mpp);
      testResourceAssignments(mpp);
      testResourceOutlineCodes(mpp);
   }

   /**
    * Test resource data read from an MPP1 file saved by Project 2010.
    */
   @Test public void testMpp12ResourceFrom14() throws Exception
   {
      MPPReader reader = new MPPReader();
      ProjectFile mpp = reader.read(MpxjTestData.filePath("mpp12resource-from14.mpp"));
      testResources(mpp);
      testNotes(mpp);
      testResourceAssignments(mpp);
      testResourceOutlineCodes(mpp);
   }

   /**
    * Test resource data read from an MPP14 file.
    */
   @Test public void testMpp14Resource() throws Exception
   {
      MPPReader reader = new MPPReader();
      ProjectFile mpp = reader.read(MpxjTestData.filePath("mpp14resource.mpp"));
      testResources(mpp);
      testNotes(mpp);
      testResourceAssignments(mpp);
      testResourceOutlineCodes(mpp);
   }

   /**
    * Test resource data read from an MSPDI file.
    */
   @Test public void testMspdiResource() throws Exception
   {
      MSPDIReader reader = new MSPDIReader();
      ProjectFile mpp = reader.read(MpxjTestData.filePath("mspdiresource.xml"));
      testResources(mpp);
      testNotes(mpp);
      testResourceAssignments(mpp);
      //testResourceOutlineCodes(mpp); TODO: MSPDI resource outline code support
   }

   /**
    * Test resource data read from an MPD9 file.
    */
   @Test public void testMpd9Resource() throws Exception
   {
      MPDFileReader reader = new MPDFileReader();
      ProjectFile mpp = reader.read(MpxjTestData.filePath("mpp9resource.mpd"));
      testResources(mpp);
      testNotes(mpp);
      testResourceAssignments(mpp);
      testResourceOutlineCodes(mpp);
   }

   /**
    * Tests fields related to Resources.
    *
    * @param mpp The ProjectFile being tested.
    */
   private void testResources(ProjectFile mpp)
   {

      /* MPP9 fields that return null:
       *
       * (would like these fixed in MPP9 as well)
       *
       * Material Label
       * Base Calendar
       */

      DateTimeFormatter df = DateTimeFormatter.ofPattern("dd/MM/yyyy");

      List<Resource> listAllResources = mpp.getResources();
      assertNotNull(listAllResources);
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

      assertEquals(1, resourceWade.getCost(1).intValue());
      assertEquals(2, resourceWade.getCost(2).intValue());
      assertEquals(3, resourceWade.getCost(3).intValue());
      assertEquals(4, resourceWade.getCost(4).intValue());
      assertEquals(5, resourceWade.getCost(5).intValue());
      assertEquals(6, resourceWade.getCost(6).intValue());
      assertEquals(7, resourceWade.getCost(7).intValue());
      assertEquals(8, resourceWade.getCost(8).intValue());
      assertEquals(9, resourceWade.getCost(9).intValue());
      assertEquals(10, resourceWade.getCost(10).intValue());

      assertEquals("wade.golden@steelray.com", resourceWade.getEmailAddress());

      assertEquals("01/01/2006", df.format(resourceWade.getDate(1)));
      assertEquals("02/01/2006", df.format(resourceWade.getDate(2)));
      assertEquals("03/01/2006", df.format(resourceWade.getDate(3)));
      assertEquals("04/01/2006", df.format(resourceWade.getDate(4)));
      assertEquals("05/01/2006", df.format(resourceWade.getDate(5)));
      assertEquals("06/01/2006", df.format(resourceWade.getDate(6)));
      assertEquals("07/01/2006", df.format(resourceWade.getDate(7)));
      assertEquals("08/01/2006", df.format(resourceWade.getDate(8)));
      assertEquals("09/01/2006", df.format(resourceWade.getDate(9)));
      assertEquals("10/01/2006", df.format(resourceWade.getDate(10)));

      assertEquals("01/02/2006", df.format(resourceWade.getStart(1)));
      assertEquals("02/02/2006", df.format(resourceWade.getStart(2)));
      assertEquals("03/02/2006", df.format(resourceWade.getStart(3)));
      assertEquals("04/02/2006", df.format(resourceWade.getStart(4)));
      assertEquals("05/02/2006", df.format(resourceWade.getStart(5)));
      assertEquals("06/02/2006", df.format(resourceWade.getStart(6)));
      assertEquals("07/02/2006", df.format(resourceWade.getStart(7)));
      assertEquals("08/02/2006", df.format(resourceWade.getStart(8)));
      assertEquals("09/02/2006", df.format(resourceWade.getStart(9)));
      assertEquals("10/02/2006", df.format(resourceWade.getStart(10)));

      assertEquals("01/03/2006", df.format(resourceWade.getFinish(1)));
      assertEquals("02/03/2006", df.format(resourceWade.getFinish(2)));
      assertEquals("03/03/2006", df.format(resourceWade.getFinish(3)));
      assertEquals("04/03/2006", df.format(resourceWade.getFinish(4)));
      assertEquals("05/03/2006", df.format(resourceWade.getFinish(5)));
      assertEquals("06/03/2006", df.format(resourceWade.getFinish(6)));
      assertEquals("07/03/2006", df.format(resourceWade.getFinish(7)));
      assertEquals("08/03/2006", df.format(resourceWade.getFinish(8)));
      assertEquals("09/03/2006", df.format(resourceWade.getFinish(9)));
      assertEquals("10/03/2006", df.format(resourceWade.getFinish(10)));

      assertEquals(1, (int) resourceWade.getDuration(1).getDuration());
      assertEquals(TimeUnit.DAYS, resourceWade.getDuration(1).getUnits());
      assertEquals(2, (int) resourceWade.getDuration(2).getDuration());
      assertEquals(TimeUnit.DAYS, resourceWade.getDuration(2).getUnits());
      assertEquals(3, (int) resourceWade.getDuration(3).getDuration());
      assertEquals(TimeUnit.DAYS, resourceWade.getDuration(3).getUnits());
      assertEquals(4, (int) resourceWade.getDuration(4).getDuration());
      assertEquals(TimeUnit.DAYS, resourceWade.getDuration(4).getUnits());
      assertEquals(5, (int) resourceWade.getDuration(5).getDuration());
      assertEquals(TimeUnit.DAYS, resourceWade.getDuration(5).getUnits());
      assertEquals(6, (int) resourceWade.getDuration(6).getDuration());
      assertEquals(TimeUnit.DAYS, resourceWade.getDuration(6).getUnits());
      assertEquals(7, (int) resourceWade.getDuration(7).getDuration());
      assertEquals(TimeUnit.DAYS, resourceWade.getDuration(7).getUnits());
      assertEquals(8, (int) resourceWade.getDuration(8).getDuration());
      assertEquals(TimeUnit.DAYS, resourceWade.getDuration(8).getUnits());
      assertEquals(9, (int) resourceWade.getDuration(9).getDuration());
      assertEquals(TimeUnit.DAYS, resourceWade.getDuration(9).getUnits());
      assertEquals(10, (int) resourceWade.getDuration(10).getDuration());
      assertEquals(TimeUnit.DAYS, resourceWade.getDuration(10).getUnits());

      assertEquals(1, resourceWade.getNumber(1).intValue());
      assertEquals(2, resourceWade.getNumber(2).intValue());
      assertEquals(3, resourceWade.getNumber(3).intValue());
      assertEquals(4, resourceWade.getNumber(4).intValue());
      assertEquals(5, resourceWade.getNumber(5).intValue());
      assertEquals(6, resourceWade.getNumber(6).intValue());
      assertEquals(7, resourceWade.getNumber(7).intValue());
      assertEquals(8, resourceWade.getNumber(8).intValue());
      assertEquals(9, resourceWade.getNumber(9).intValue());
      assertEquals(10, resourceWade.getNumber(10).intValue());
      assertEquals(11, resourceWade.getNumber(11).intValue());
      assertEquals(12, resourceWade.getNumber(12).intValue());
      assertEquals(13, resourceWade.getNumber(13).intValue());
      assertEquals(14, resourceWade.getNumber(14).intValue());
      assertEquals(15, resourceWade.getNumber(15).intValue());
      assertEquals(16, resourceWade.getNumber(16).intValue());
      assertEquals(17, resourceWade.getNumber(17).intValue());
      assertEquals(18, resourceWade.getNumber(18).intValue());
      assertEquals(19, resourceWade.getNumber(19).intValue());
      assertEquals(20, resourceWade.getNumber(20).intValue());

      assertEquals("1", resourceWade.getText(1));
      assertEquals("2", resourceWade.getText(2));
      assertEquals("3", resourceWade.getText(3));
      assertEquals("4", resourceWade.getText(4));
      assertEquals("5", resourceWade.getText(5));
      assertEquals("6", resourceWade.getText(6));
      assertEquals("7", resourceWade.getText(7));
      assertEquals("8", resourceWade.getText(8));
      assertEquals("9", resourceWade.getText(9));
      assertEquals("10", resourceWade.getText(10));
      assertEquals("11", resourceWade.getText(11));
      assertEquals("12", resourceWade.getText(12));
      assertEquals("13", resourceWade.getText(13));
      assertEquals("14", resourceWade.getText(14));
      assertEquals("15", resourceWade.getText(15));
      assertEquals("16", resourceWade.getText(16));
      assertEquals("17", resourceWade.getText(17));
      assertEquals("18", resourceWade.getText(18));
      assertEquals("19", resourceWade.getText(19));
      assertEquals("20", resourceWade.getText(20));
      assertEquals("21", resourceWade.getText(21));
      assertEquals("22", resourceWade.getText(22));
      assertEquals("23", resourceWade.getText(23));
      assertEquals("24", resourceWade.getText(24));
      assertEquals("25", resourceWade.getText(25));
      assertEquals("26", resourceWade.getText(26));
      assertEquals("27", resourceWade.getText(27));
      assertEquals("28", resourceWade.getText(28));
      assertEquals("29", resourceWade.getText(29));
      assertEquals("30", resourceWade.getText(30));

      //assertEquals("Standard", resourceWade.getBaseCalendar()); // both of these currently return null from MPP9
      //assertEquals("Night Shift", resourceBrian.getBaseCalendar());
   }

   /**
    * Test resource outline codes.
    *
    * @param mpp project file
    */
   private void testResourceOutlineCodes(ProjectFile mpp)
   {
      Resource resourceWade = mpp.getResourceByID(Integer.valueOf(1));
      assertEquals("AAA", resourceWade.getOutlineCode(1));
      assertEquals("BBB", resourceWade.getOutlineCode(2));
      assertEquals("CCC", resourceWade.getOutlineCode(3));
      assertEquals("DDD", resourceWade.getOutlineCode(4));
      assertEquals("EEE", resourceWade.getOutlineCode(5));
      assertEquals("FFF", resourceWade.getOutlineCode(6));
      assertEquals("GGG", resourceWade.getOutlineCode(7));
      assertEquals("HHH", resourceWade.getOutlineCode(8));
      assertEquals("III", resourceWade.getOutlineCode(9));
      assertEquals("JJJ", resourceWade.getOutlineCode(10));
   }

   /**
    * Tests fields related to Resource Assignments.
    *
    * @param mpp The ProjectFile being tested.
    */
   private void testResourceAssignments(ProjectFile mpp)
   {
      Integer intOne = Integer.valueOf(1);
      DateTimeFormatter df = DateTimeFormatter.ofPattern("dd/MM/yyyy");

      List<ResourceAssignment> listResourceAssignments = mpp.getResourceAssignments();

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
      for (Resource resource : file.getResources())
      {
         int id = resource.getID().intValue();
         if (id != 0)
         {
            assertEquals("Resource Notes " + id, resource.getNotes().trim());
         }
      }
   }

   /**
    * In the original MPP14 reader implementation, the ID and Unique ID
    * resource fields were read the wrong way around. This test validates
    * that the values are read correctly, especially when the ID != Unique ID.
    */
   @Test public void testResourceIdAndUniqueID() throws Exception
   {
      MPPReader reader = new MPPReader();

      ProjectFile file = reader.read(MpxjTestData.filePath("ResourceIdAndUniqueId-project2013-mpp14.mpp"));
      validateIdValues(file);

      file = reader.read(MpxjTestData.filePath("ResourceIdAndUniqueId-project2010-mpp14.mpp"));
      validateIdValues(file);
   }

   /**
    * Validate the ID, Unique ID and name attributes.
    *
    * @param file project file
    */
   private void validateIdValues(ProjectFile file)
   {
      assertEquals(4, file.getResources().size());

      Resource resource = file.getResourceByUniqueID(Integer.valueOf(11));
      assertEquals(1, resource.getID().intValue());
      assertEquals("Resource One", resource.getName());

      resource = file.getResourceByUniqueID(Integer.valueOf(12));
      assertEquals(2, resource.getID().intValue());
      assertEquals("Resource Two", resource.getName());

      resource = file.getResourceByUniqueID(Integer.valueOf(13));
      assertEquals(3, resource.getID().intValue());
      assertEquals("Resource Three", resource.getName());

   }
}
