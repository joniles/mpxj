/*
 * file:       MppTaskTest.java
 * author:     Wade Golden
 * copyright:  (c) Packwood Software 2006
 * date:       22-August-2006
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

import static org.mpxj.junit.MpxjAssert.*;
import static org.junit.Assert.*;

import java.time.format.DateTimeFormatter;
import java.util.List;

import org.mpxj.AccrueType;
import org.mpxj.ConstraintType;
import org.mpxj.LocalDateTimeRange;
import org.mpxj.Duration;
import org.mpxj.Priority;
import org.mpxj.ProjectFile;
import org.mpxj.ProjectProperties;
import org.mpxj.Relation;
import org.mpxj.RelationType;
import org.mpxj.Resource;
import org.mpxj.Task;
import org.mpxj.TaskType;
import org.mpxj.TimeUnit;
import org.mpxj.mpd.MPDFileReader;
import org.mpxj.mpp.MPPReader;
import org.mpxj.mspdi.MSPDIReader;

import org.junit.Test;

/**
 * Tests to exercise MPP file read functionality for various versions of
 * MPP file.
 */
public class MppTaskTest
{

   /**
    * Test task data read from an MPP9 file.
    */
   @Test public void testMpp9Task() throws Exception
   {
      ProjectFile mpp = new MPPReader().read(MpxjTestData.filePath("mpp9task.mpp"));
      testBasicTask(mpp);
   }

   /**
    * Test task data read from an MPP9 file saved by Project 2007.
    */
   @Test public void testMpp9TaskFrom12() throws Exception
   {
      ProjectFile mpp = new MPPReader().read(MpxjTestData.filePath("mpp9task-from12.mpp"));
      testBasicTask(mpp);
   }

   /**
    * Test task data read from an MPP9 file saved by Project 2010.
    */
   @Test public void testMpp9TaskFrom14() throws Exception
   {
      ProjectFile mpp = new MPPReader().read(MpxjTestData.filePath("mpp9task-from14.mpp"));
      testBasicTask(mpp);
   }

   /**
    * Test task data read from an MPP12 file.
    */
   @Test public void testMpp12Task() throws Exception
   {
      ProjectFile mpp = new MPPReader().read(MpxjTestData.filePath("mpp12task.mpp"));
      testBasicTask(mpp);
   }

   /**
    * Test task data read from an MPP12 file saved by Project 2010.
    */
   @Test public void testMpp12TaskFrom14() throws Exception
   {
      ProjectFile mpp = new MPPReader().read(MpxjTestData.filePath("mpp12task-from14.mpp"));
      testBasicTask(mpp);
   }

   /**
    * Test task data read from an MPP14 file.
    */
   @Test public void testMpp14Task() throws Exception
   {
      ProjectFile mpp = new MPPReader().read(MpxjTestData.filePath("mpp14task.mpp"));
      testBasicTask(mpp);
   }

   /**
    * Test task data read from an MPP14 file.
    */
   @Test public void testMpp14TaskFromProject2013() throws Exception
   {
      ProjectFile mpp = new MPPReader().read(MpxjTestData.filePath("mpp14task-from2013.mpp"));
      testBasicTask(mpp);
   }

   /**
    * Test task data read from an MPD9 file.
    */
   @Test public void testMpd9Task() throws Exception
   {
      ProjectFile mpp = new MPDFileReader().read(MpxjTestData.filePath("mpp9task.mpd"));
      testBasicTask(mpp);
   }

   /**
    * Test task data read from an MPP9 file.
    */
   @Test public void testMpp9Baseline() throws Exception
   {
      ProjectFile mpp = new MPPReader().read(MpxjTestData.filePath("mpp9baseline.mpp"));
      testBaselineTasks(mpp);
   }

   /**
    * Test task data read from an MPP9 file saved from Project 2007.
    */
   @Test public void testMpp9BaselineFrom12() throws Exception
   {
      ProjectFile mpp = new MPPReader().read(MpxjTestData.filePath("mpp9baseline-from12.mpp"));
      testBaselineTasks(mpp);
   }

   /**
    * Test task data read from an MPP9 file saved from Project 2010.
    */
   @Test public void testMpp9BaselineFrom14() throws Exception
   {
      ProjectFile mpp = new MPPReader().read(MpxjTestData.filePath("mpp9baseline-from14.mpp"));
      testBaselineTasks(mpp);
   }

   /**
    * Test task data read from an MPP12 file.
    */
   @Test public void testMpp12Baseline() throws Exception
   {
      ProjectFile mpp = new MPPReader().read(MpxjTestData.filePath("mpp12baseline.mpp"));
      testBaselineTasks(mpp);
   }

   /**
    * Test task data read from an MPP12 file saved by Project 2010.
    */
   @Test public void testMpp12BaselineFrom14() throws Exception
   {
      ProjectFile mpp = new MPPReader().read(MpxjTestData.filePath("mpp12baseline-from14.mpp"));
      testBaselineTasks(mpp);
   }

   /**
    * Test task data read from an MPP14 file.
    */
   @Test public void testMpp14Baseline() throws Exception
   {
      ProjectFile mpp = new MPPReader().read(MpxjTestData.filePath("mpp14baseline.mpp"));
      testBaselineTasks(mpp);
   }

   /**
    * Test task data read from an MPD9 file.
    */
   @Test public void testMpd9Baseline() throws Exception
   {
      ProjectFile mpp = new MPDFileReader().read(MpxjTestData.filePath("mpp9baseline.mpd"));
      testBaselineTasks(mpp);
   }

   /**
    * Test Split Tasks in an MPP9 file.
    */
   @Test public void testMpp9Splits() throws Exception
   {
      ProjectFile mpp = new MPPReader().read(MpxjTestData.filePath("mpp9splittask.mpp"));
      testSplitTasks(mpp);
   }

   /**
    * Test Split Tasks in an MPP9 file saved by Project 2007.
    */
   @Test public void testMpp9SplitsFrom12() throws Exception
   {
      ProjectFile mpp = new MPPReader().read(MpxjTestData.filePath("mpp9splittask-from12.mpp"));
      testSplitTasks(mpp);
   }

   /**
    * Test Split Tasks in an MPP9 file saved by Project 2010.
    */
   @Test public void testMpp9SplitsFrom14() throws Exception
   {
      ProjectFile mpp = new MPPReader().read(MpxjTestData.filePath("mpp9splittask-from14.mpp"));
      testSplitTasks(mpp);
   }

   /**
    * Test Split Tasks in an MPP12 file.
    */
   @Test public void testMpp12Splits() throws Exception
   {
      ProjectFile mpp = new MPPReader().read(MpxjTestData.filePath("mpp12splittask.mpp"));
      testSplitTasks(mpp);
   }

   /**
    * Test Split Tasks in an MPP12 file saved by Project 2010.
    */
   @Test public void testMpp12SplitsFrom14() throws Exception
   {
      ProjectFile mpp = new MPPReader().read(MpxjTestData.filePath("mpp12splittask-from14.mpp"));
      testSplitTasks(mpp);
   }

   /**
    * Test Split Tasks in an MPP14 file.
    */
   @Test public void testMpp14Splits() throws Exception
   {
      ProjectFile mpp = new MPPReader().read(MpxjTestData.filePath("mpp14splittask.mpp"));
      testSplitTasks(mpp);
   }

   /**
    * Test Split Tasks in an MSPDI file.
    */
   @Test public void testMspdiSplits() throws Exception
   {
      ProjectFile mpp = new MSPDIReader().read(MpxjTestData.filePath("mspdisplittask.xml"));
      testSplitTasks(mpp);
   }

   /**
    * Test Split Tasks in an MPD9 file.
    *
    * Currently split tasks are not supported in MPD files.
    */
   @Test public void testMpd9Splits()
   {
      //       ProjectFile mpp = new MPDDatabaseReader().read (MpxjTestData.filePath("mpp9splittask.mpd");
      //       testSplitTasks(mpp);
   }

   /**
    * Tests Relations in an MPP9 file.
    */
   @Test public void testMpp9Relations() throws Exception
   {
      ProjectFile mpp = new MPPReader().read(MpxjTestData.filePath("mpp9relations.mpp"));
      testRelations(mpp);
   }

   /**
    * Tests Relations in an MPP9 file saved by Project 2007.
    */
   @Test public void testMpp9RelationsFrom12() throws Exception
   {
      ProjectFile mpp = new MPPReader().read(MpxjTestData.filePath("mpp9relations-from12.mpp"));
      testRelations(mpp);
   }

   /**
    * Tests Relations in an MPP9 file saved by Project 2010.
    */
   @Test public void testMpp9RelationsFrom14() throws Exception
   {
      ProjectFile mpp = new MPPReader().read(MpxjTestData.filePath("mpp9relations-from14.mpp"));
      testRelations(mpp);
   }

   /**
    * Tests Relations in an MPD9 file.
    */
   @Test public void testMpd9Relations() throws Exception
   {
      ProjectFile mpp = new MPDFileReader().read(MpxjTestData.filePath("mpp9relations.mpd"));
      testRelations(mpp);
   }

   /**
    * Tests Relations in an MPP12 file.
    */
   @Test public void testMpp12Relations() throws Exception
   {
      ProjectFile mpp = new MPPReader().read(MpxjTestData.filePath("mpp12relations.mpp"));
      testRelations(mpp);
   }

   /**
    * Tests Relations in an MPP12 file saved by Project 2010.
    */
   @Test public void testMpp12RelationsFrom14() throws Exception
   {
      ProjectFile mpp = new MPPReader().read(MpxjTestData.filePath("mpp12relations-from14.mpp"));
      testRelations(mpp);
   }

   /**
    * Tests Relations in an MPP14 file.
    */
   @Test public void testMpp14Relations() throws Exception
   {
      ProjectFile mpp = new MPPReader().read(MpxjTestData.filePath("mpp14relations.mpp"));
      testRelations(mpp);
   }

   /**
    * Tests Relations in an MSPDI file.
    */
   @Test public void testMspdiRelations() throws Exception
   {
      ProjectFile mpp = new MSPDIReader().read(MpxjTestData.filePath("mspdirelations.xml"));
      testRelations(mpp);
   }

   /**
    * Tests dozens of basic fields of a Task.
    * @param mpp The ProjectFile being tested.
    *
    * <br><br>
    * Columns not tested:<br><br>
    *
    * Overtime Cost, Remaining Overtime Cost, Actual Overtime Cost<br>
    * Overtime Work, Remaining Overtime Work, Actual Overtime Work<br>
    * Actual Start<br>
    * Actual Finish<br>
    * Baseline Cost<br>
    * Baseline Start<br>
    * Baseline Finish<br>
    * Baseline Duration<br>
    * Baseline Work<br>
    * Confirmed (??? - don't know how to make this field 'Yes' in Project)<br>
    * Cost Rate Table (calculated in Project)<br>
    * Critical (Calculated in Steelray)<br>
    * Estimated (test in another method)<br>
    * External Task<br>
    * Group By Summary<br>
    * Hyperlink Subaddress<br>
    * Linked Fields<br>
    * Outline Code1-10<br>
    * Objects<br>
    * Overallocated<br>
    * Preleveled Start<br>
    * Preleveled Finish<br>
    * Recurring<br>
    * Resource Phonetics<br>
    * Resource Type<br>
    * Response Pending<br>
    * Subproject File<br>
    * Subproject Read Only<br>
    * Predecessors<br>
    * Summary<br>
    * Task Calendar<br>
    * Team Status Pending<br>
    * Type<br>
    * Unique ID Predecessors, Unique ID Successors<br>
    * Update Needed<br>
    * WBS Predecessors, WBS Succeessors<br>
    * Work Contour<br><br><br>
    *
    * Fields that are not supported in the MPP9 format (usually return null or false)<br><br>
    *
    * AWCP<br>
    * BCWP<br>
    * BCWS<br>
    * CV<br>
    * Hyperlink Href<br>
    * Project<br>
    * Regular Work<br>
    * Resource Group<br>
    * Resource Initials<br>
    * Resource Names<br>
    * Successors<br>
    * SV<br>
    * VAC<br>
    *
    */
   private void testBasicTask(ProjectFile mpp)
   {
      DateTimeFormatter df = DateTimeFormatter.ofPattern("dd/MM/yyyy");

      ProjectProperties properties = mpp.getProjectProperties();
      assertNotNull(properties);

      // test various global properties
      List<Task> listAllTasks = mpp.getTasks();
      List<Resource> listAllResources = mpp.getResources();
      assertNotNull(listAllTasks);
      assertNotNull(listAllResources);
      assertEquals(2, listAllTasks.size());
      // This will fail in MPP12 as we appear to have a summary resource
      //assertEquals(1, listAllResources.size());

      /* Test Specifics */

      // task 0 - the project task
      Task task = listAllTasks.get(0);
      assertNotNull(task);
      assertEquals(0, task.getID().intValue());
      assertEquals("MPP12 Test", task.getName());

      // task 1
      task = listAllTasks.get(1);
      assertNotNull(task);
      assertEquals(1, task.getID().intValue());
      // name
      assertEquals("Task #1", task.getName());
      // start and finish
      final String expectedStart = "23/08/2006";
      final String expectedFinish = "29/08/2006";
      assertEquals(expectedStart, df.format(task.getStart()));
      assertEquals(expectedFinish, df.format(task.getFinish()));
      // no predecessors
      assertTrue(task.getPredecessors().isEmpty());
      // duration
      Duration expectedDuration = Duration.getInstance(1, TimeUnit.WEEKS);
      // note that this is a direct comparison - TimeUnit must match
      assertEquals(expectedDuration, task.getDuration());
      // work
      expectedDuration = Duration.getInstance(40, TimeUnit.HOURS);
      assertEquals(expectedDuration, task.getWork());
      // percent complete
      Number expectedPctComp = Double.valueOf(45);
      assertEquals(expectedPctComp, task.getPercentageComplete());
      // percent work complete
      expectedPctComp = Double.valueOf(45);
      assertEquals(expectedPctComp, task.getPercentageWorkComplete());
      // cost
      Number expectedCost = Double.valueOf(5000);
      assertEquals(expectedCost, task.getCost());
      // actual cost
      expectedCost = Double.valueOf(2800);
      assertEquals(expectedCost, task.getActualCost());
      // fixed cost
      expectedCost = Double.valueOf(1000);
      assertEquals(expectedCost, task.getFixedCost());
      // remaining cost
      expectedCost = Double.valueOf(2200);
      assertEquals(expectedCost, task.getRemainingCost());
      // actual work
      expectedDuration = Duration.getInstance(18, TimeUnit.HOURS);
      assertEquals(expectedDuration, expectedDuration);
      // contact
      String expectedContact = "wade";
      assertEquals(expectedContact, task.getContact());
      // constraint date
      assertEquals(expectedStart, df.format(task.getConstraintDate()));
      // constraint type
      assertEquals(ConstraintType.MUST_START_ON, task.getConstraintType());
      // custom cost columns
      expectedCost = Double.valueOf(1);
      assertEquals(expectedCost, task.getCost(1));
      expectedCost = Double.valueOf(2);
      assertEquals(expectedCost, task.getCost(2));
      expectedCost = Double.valueOf(3);
      assertEquals(expectedCost, task.getCost(3));
      expectedCost = Double.valueOf(4);
      assertEquals(expectedCost, task.getCost(4));
      expectedCost = Double.valueOf(5);
      assertEquals(expectedCost, task.getCost(5));
      expectedCost = Double.valueOf(6);
      assertEquals(expectedCost, task.getCost(6));
      expectedCost = Double.valueOf(7);
      assertEquals(expectedCost, task.getCost(7));
      expectedCost = Double.valueOf(8);
      assertEquals(expectedCost, task.getCost(8));
      expectedCost = Double.valueOf(9);
      assertEquals(expectedCost, task.getCost(9));
      expectedCost = Double.valueOf(10);
      assertEquals(expectedCost, task.getCost(10));
      // cost variance
      expectedCost = Double.valueOf(5000);
      assertEquals(expectedCost, task.getCostVariance());
      // created
      //Date dateExpected = new Date(1156360320000L);
      assertEquals(expectedStart, df.format(task.getCreateDate()));
      // custom date columns
      assertEquals("25/08/2006", df.format(task.getDate(1)));
      assertEquals("26/08/2006", df.format(task.getDate(2)));
      assertEquals("27/08/2006", df.format(task.getDate(3)));
      assertEquals("28/08/2006", df.format(task.getDate(4)));
      assertEquals("29/08/2006", df.format(task.getDate(5)));
      assertEquals("30/08/2006", df.format(task.getDate(6)));
      assertEquals("31/08/2006", df.format(task.getDate(7)));
      assertEquals("01/09/2006", df.format(task.getDate(8)));
      assertEquals("02/09/2006", df.format(task.getDate(9)));
      assertEquals("03/09/2006", df.format(task.getDate(10)));
      // deadline
      assertEquals("30/08/2006", df.format(task.getDeadline()));
      // custom duration columns
      expectedDuration = Duration.getInstance(1, TimeUnit.DAYS);
      assertEquals(expectedDuration, task.getDuration(1));
      expectedDuration = Duration.getInstance(2, TimeUnit.DAYS);
      assertEquals(expectedDuration, task.getDuration(2));
      expectedDuration = Duration.getInstance(3, TimeUnit.DAYS);
      assertEquals(expectedDuration, task.getDuration(3));
      expectedDuration = Duration.getInstance(4, TimeUnit.DAYS);
      assertEquals(expectedDuration, task.getDuration(4));
      expectedDuration = Duration.getInstance(5, TimeUnit.DAYS);
      assertEquals(expectedDuration, task.getDuration(5));
      expectedDuration = Duration.getInstance(6, TimeUnit.DAYS);
      assertEquals(expectedDuration, task.getDuration(6));
      expectedDuration = Duration.getInstance(7, TimeUnit.DAYS);
      assertEquals(expectedDuration, task.getDuration(7));
      expectedDuration = Duration.getInstance(8, TimeUnit.DAYS);
      assertEquals(expectedDuration, task.getDuration(8));
      expectedDuration = Duration.getInstance(9, TimeUnit.DAYS);
      assertEquals(expectedDuration, task.getDuration(9));
      expectedDuration = Duration.getInstance(10, TimeUnit.DAYS);
      assertEquals(expectedDuration, task.getDuration(10));
      // duration variance
      expectedDuration = Duration.getInstance(1, TimeUnit.WEEKS);
      assertEquals(expectedDuration, task.getDurationVariance());
      // early start and finish
      assertEquals(expectedStart, df.format(task.getEarlyStart()));
      assertEquals(expectedFinish, df.format(task.getEarlyFinish()));
      // effort driven
      assertTrue("Effort Driven does not match", task.getEffortDriven()); // should return true
      // custom start columns
      assertEquals("25/08/2006", df.format(task.getStart(1)));
      assertEquals("26/08/2006", df.format(task.getStart(2)));
      assertEquals("27/08/2006", df.format(task.getStart(3)));
      assertEquals("28/08/2006", df.format(task.getStart(4)));
      assertEquals("29/08/2006", df.format(task.getStart(5)));
      assertEquals("30/08/2006", df.format(task.getStart(6)));
      assertEquals("31/08/2006", df.format(task.getStart(7)));
      assertEquals("01/09/2006", df.format(task.getStart(8)));
      assertEquals("02/09/2006", df.format(task.getStart(9)));
      assertEquals("03/09/2006", df.format(task.getStart(10)));
      // custom finish columns
      assertEquals("25/08/2006", df.format(task.getFinish(1)));
      assertEquals("26/08/2006", df.format(task.getFinish(2)));
      assertEquals("27/08/2006", df.format(task.getFinish(3)));
      assertEquals("28/08/2006", df.format(task.getFinish(4)));
      assertEquals("29/08/2006", df.format(task.getFinish(5)));
      assertEquals("30/08/2006", df.format(task.getFinish(6)));
      assertEquals("31/08/2006", df.format(task.getFinish(7)));
      assertEquals("01/09/2006", df.format(task.getFinish(8)));
      assertEquals("02/09/2006", df.format(task.getFinish(9)));
      assertEquals("03/09/2006", df.format(task.getFinish(10)));
      // finish slack
      expectedDuration = Duration.getInstance(0, TimeUnit.WEEKS); // use for Finish Slack, Start Slack, Free Slack
      assertEquals(expectedDuration, task.getFinishSlack());
      // start slack
      assertEquals(expectedDuration, task.getStartSlack());
      // free slack
      assertEquals(expectedDuration, task.getFreeSlack());
      // finish variance
      expectedDuration = Duration.getInstance(0, TimeUnit.DAYS);
      assertEquals(expectedDuration, task.getFinishVariance());
      // fixed cost accrual
      assertEquals(AccrueType.START, task.getFixedCostAccrual());
      // custom flag columns
      boolean expectedValue = true;
      assertBooleanEquals(expectedValue, task.getFlag(1));
      assertBooleanEquals(expectedValue, task.getFlag(2));
      assertBooleanEquals(expectedValue, task.getFlag(3));
      assertBooleanEquals(expectedValue, task.getFlag(4));
      assertBooleanEquals(expectedValue, task.getFlag(5));
      assertBooleanEquals(expectedValue, task.getFlag(6));
      assertBooleanEquals(expectedValue, task.getFlag(7));
      assertBooleanEquals(expectedValue, task.getFlag(8));
      assertBooleanEquals(expectedValue, task.getFlag(9));
      assertBooleanEquals(expectedValue, task.getFlag(10));
      assertBooleanEquals(expectedValue, task.getFlag(11));
      assertBooleanEquals(expectedValue, task.getFlag(12));
      assertBooleanEquals(expectedValue, task.getFlag(13));
      assertBooleanEquals(expectedValue, task.getFlag(14));
      assertBooleanEquals(expectedValue, task.getFlag(15));
      assertBooleanEquals(expectedValue, task.getFlag(16));
      assertBooleanEquals(expectedValue, task.getFlag(17));
      assertBooleanEquals(expectedValue, task.getFlag(18));
      assertBooleanEquals(expectedValue, task.getFlag(19));
      assertBooleanEquals(expectedValue, task.getFlag(20));
      // hide bar
      assertBooleanEquals(expectedValue, task.getHideBar());
      // hyperlink
      final String expectedHyperlink = "http://www.steelray.com";
      assertEquals(expectedHyperlink, (task.getHyperlink()));
      // hyperlink address
      assertEquals(expectedHyperlink, (task.getHyperlinkAddress()));
      // ignore resource calendar
      // (note that 'false' is the default value, so this isn't really a test of MPXJ -
      // I couldn't change the value in Project, though)
      assertFalse(task.getIgnoreResourceCalendar());
      // late start and finish
      assertEquals(expectedStart, df.format(task.getLateStart()));
      assertEquals(expectedFinish, df.format(task.getLateFinish()));
      // leveling
      assertFalse(task.getLevelAssignments());
      assertFalse(task.getLevelingCanSplit());
      // leveling delay
      expectedDuration = Duration.getInstance(0, TimeUnit.ELAPSED_DAYS);
      assertEquals(expectedDuration, task.getLevelingDelay());
      // marked
      assertTrue(task.getMarked());
      // milestone
      assertTrue(task.getMilestone());
      // Notes
      assertEquals("Notes Example", task.getNotes());
      // custom number columns
      assertEquals(Double.valueOf(1), task.getNumber(1));
      assertEquals(Double.valueOf(2), task.getNumber(2));
      assertEquals(Double.valueOf(3), task.getNumber(3));
      assertEquals(Double.valueOf(4), task.getNumber(4));
      assertEquals(Double.valueOf(5), task.getNumber(5));
      assertEquals(Double.valueOf(6), task.getNumber(6));
      assertEquals(Double.valueOf(7), task.getNumber(7));
      assertEquals(Double.valueOf(8), task.getNumber(8));
      assertEquals(Double.valueOf(9), task.getNumber(9));
      assertEquals(Double.valueOf(10), task.getNumber(10));
      assertEquals(Double.valueOf(11), task.getNumber(11));
      assertEquals(Double.valueOf(12), task.getNumber(12));
      assertEquals(Double.valueOf(13), task.getNumber(13));
      assertEquals(Double.valueOf(14), task.getNumber(14));
      assertEquals(Double.valueOf(15), task.getNumber(15));
      assertEquals(Double.valueOf(16), task.getNumber(16));
      assertEquals(Double.valueOf(17), task.getNumber(17));
      assertEquals(Double.valueOf(18), task.getNumber(18));
      assertEquals(Double.valueOf(19), task.getNumber(19));
      assertEquals(Double.valueOf(20), task.getNumber(20));
      // outline level
      assertEquals(Integer.valueOf(1), task.getOutlineLevel());
      // outline codes
      assertEquals("1", task.getOutlineNumber());
      assertEquals("1", task.getOutlineCode(1));
      assertEquals("A", task.getOutlineCode(2));
      assertEquals("a", task.getOutlineCode(3));
      assertEquals("Aa", task.getOutlineCode(4));
      assertEquals("5", task.getOutlineCode(5));
      assertEquals("6", task.getOutlineCode(6));
      assertEquals("7", task.getOutlineCode(7));
      assertEquals("8", task.getOutlineCode(8));
      assertEquals("9", task.getOutlineCode(9));
      assertEquals("10", task.getOutlineCode(10));
      // priority
      assertEquals(Priority.getInstance(600), task.getPriority());
      // remaining work
      expectedDuration = Duration.getInstance(22, TimeUnit.HOURS);
      assertEquals(expectedDuration, task.getRemainingWork());
      // remaining duration
      expectedDuration = Duration.getInstance(0.55, TimeUnit.WEEKS);
      assertEquals(expectedDuration, task.getRemainingDuration());
      // resume
      assertEquals("25/08/2006", df.format(task.getResume()));
      // rollup
      assertTrue(task.getRollup());
      // start slack
      expectedDuration = Duration.getInstance(0, TimeUnit.WEEKS);
      assertEquals(expectedDuration, task.getStartSlack());
      // start variance
      expectedDuration = Duration.getInstance(0, TimeUnit.DAYS);
      assertEquals(expectedDuration, task.getStartVariance());
      // stop
      assertEquals("25/08/2006", df.format(task.getStop()));
      // total slack
      expectedDuration = Duration.getInstance(0, TimeUnit.WEEKS);
      assertEquals(expectedDuration, task.getTotalSlack());
      // custom text columns
      assertEquals("1", task.getText(1));
      assertEquals("2", task.getText(2));
      assertEquals("3", task.getText(3));
      assertEquals("4", task.getText(4));
      assertEquals("5", task.getText(5));
      assertEquals("6", task.getText(6));
      assertEquals("7", task.getText(7));
      assertEquals("8", task.getText(8));
      assertEquals("9", task.getText(9));
      assertEquals("10", task.getText(10));
      assertEquals("11", task.getText(11));
      assertEquals("12", task.getText(12));
      assertEquals("13", task.getText(13));
      assertEquals("14", task.getText(14));
      assertEquals("15", task.getText(15));
      assertEquals("16", task.getText(16));
      assertEquals("17", task.getText(17));
      assertEquals("18", task.getText(18));
      assertEquals("19", task.getText(19));
      assertEquals("20", task.getText(20));
      assertEquals("21", task.getText(21));
      assertEquals("22", task.getText(22));
      assertEquals("23", task.getText(23));
      assertEquals("24", task.getText(24));
      assertEquals("25", task.getText(25));
      assertEquals("26", task.getText(26));
      assertEquals("27", task.getText(27));
      assertEquals("28", task.getText(28));
      assertEquals("29", task.getText(29));
      assertEquals("30", task.getText(30));
      // unique id
      assertEquals(Integer.valueOf(1), task.getUniqueID());
      // wbs
      assertEquals("1", task.getWBS());
      // work variance
      expectedDuration = Duration.getInstance(40, TimeUnit.HOURS);
      assertEquals(expectedDuration, task.getWorkVariance());
   }

   /**
    * Tests fields related to Baseline information, as well as actual
    * dates, estimated, and other fields (see below).
    *
    * @param mpp The ProjectFile being tested.
    *
    */
   private void testBaselineTasks(ProjectFile mpp)
   {
      /*
       * Columns tested:
       *
       * Actual Start
       * Actual Finish
       * Baseline Start
       * Baseline Finish
       * Baseline Duration
       * Baseline Work
       * Estimated
       * Predecessors
       * Summary
       * Outline Number
       * WBS
       */

      DateTimeFormatter df = DateTimeFormatter.ofPattern("dd/MM/yyyy");

      List<Task> listAllTasks = mpp.getTasks();
      List<Resource> listAllResources = mpp.getResources();
      assertNotNull(listAllTasks);
      assertNotNull(listAllResources);
      assertFalse(listAllTasks.isEmpty());
      assertFalse(listAllResources.isEmpty());

      Task baseTask, subtask1, subtask2, subtask3, subtask4, subtask5, completeTask, complexOutlineNumberTask, subtaskA, subtaskA1, subtaskA2, subtaskB, subtaskB1, subtaskB1a;

      // verify that the get()s match the right tasks
      baseTask = listAllTasks.get(1);
      assertEquals("Base Task", baseTask.getName());
      subtask1 = listAllTasks.get(2);
      assertEquals("Subtask 1", subtask1.getName());
      subtask2 = listAllTasks.get(3);
      assertEquals("Subtask 2", subtask2.getName());
      subtask3 = listAllTasks.get(4);
      assertEquals("Subtask 3", subtask3.getName());
      subtask4 = listAllTasks.get(5);
      assertEquals("Subtask 4", subtask4.getName());
      subtask5 = listAllTasks.get(6);
      assertEquals("Subtask 5", subtask5.getName());
      completeTask = listAllTasks.get(7);
      assertEquals("Complete", completeTask.getName());
      complexOutlineNumberTask = listAllTasks.get(8);
      assertEquals("Complex Outline Number", complexOutlineNumberTask.getName());
      subtaskA = listAllTasks.get(9);
      assertEquals("Subtask A", subtaskA.getName());
      subtaskA1 = listAllTasks.get(10);
      assertEquals("Subtask A1", subtaskA1.getName());
      subtaskA2 = listAllTasks.get(11);
      assertEquals("Subtask A2", subtaskA2.getName());
      subtaskB = listAllTasks.get(12);
      assertEquals("Subtask B", subtaskB.getName());
      subtaskB1 = listAllTasks.get(13);
      assertEquals("Subtask B1", subtaskB1.getName());
      subtaskB1a = listAllTasks.get(14);
      assertEquals("Subtask B1a", subtaskB1a.getName());

      // Baseline for 'Base Task'
      assertEquals("24/08/2006", df.format(baseTask.getBaselineStart()));
      assertEquals("13/09/2006", df.format(baseTask.getBaselineFinish()));

      // Actual for 'Base Task'
      assertEquals("24/08/2006", df.format(baseTask.getActualStart()));
      assertNull(baseTask.getActualFinish());

      // % Complete
      assertEquals(Double.valueOf(57), baseTask.getPercentageComplete());
      // Type for 'Base Task'
      assertEquals(TaskType.FIXED_DURATION, baseTask.getType());

      // Test 'Subtask 2' baseline opposed to planned and actual
      // Planned for 'Subtask 2'
      assertEquals("30/08/2006", df.format(subtask2.getStart()));
      assertEquals("05/09/2006", df.format(subtask2.getFinish()));

      // Actual for 'Subtask 2'
      assertEquals("30/08/2006", df.format(subtask2.getActualStart()));
      assertEquals("05/09/2006", df.format(subtask2.getActualFinish()));

      // Baseline for 'Subtask 2'
      assertEquals("29/08/2006", df.format(subtask2.getBaselineStart()));
      assertEquals("01/09/2006", df.format(subtask2.getBaselineFinish()));

      // Predecessor for Subtask 2
      List<Relation> predecessors = subtask2.getPredecessors();
      assertEquals(1, predecessors.size());
      Relation relation = predecessors.get(0);
      // check task unique id that's stored in the Relation
      assertEquals(subtask1.getUniqueID(), relation.getPredecessorTask().getUniqueID());
      // check task id stored in the Task that's stored in the relation
      Task predTask = relation.getPredecessorTask();
      assertEquals(predTask.getID(), subtask1.getID());
      // check task unique id stored in the Task that's stored in the relation
      assertEquals(predTask.getUniqueID(), subtask1.getUniqueID());
      // Type for 'Subtask 2'
      assertEquals(TaskType.FIXED_UNITS, subtask2.getType());

      // Predecessors for Subtask 5 (multiple predecessors)
      predecessors = subtask5.getPredecessors();
      assertEquals(2, predecessors.size());
      relation = predecessors.get(0);
      Relation relation2 = predecessors.get(1);
      assertEquals(subtask3.getUniqueID(), relation.getPredecessorTask().getUniqueID());
      assertEquals(subtask4.getUniqueID(), relation2.getPredecessorTask().getUniqueID());
      // Type for subtask 5
      assertEquals(TaskType.FIXED_WORK, subtask5.getType());

      // Summary for 'Subtask A1'
      assertTrue(subtaskA.getSummary());

      // Estimated for 'Subtask A1'
      assertTrue(subtaskA1.getEstimated());
      // Outline Number and WBS for 'Subtask A1'
      String outlineNumber = "2.1.1";
      assertEquals(outlineNumber, subtaskA1.getOutlineNumber());
      assertEquals(outlineNumber, subtaskA1.getWBS());

      // Outline Number and WBS for 'Subtask B1a'
      outlineNumber = "2.2.1.1";
      assertEquals(outlineNumber, subtaskB1a.getOutlineNumber());
      assertEquals(outlineNumber, subtaskB1a.getWBS());
   }

   /**
    * Tests Split Tasks.
    *
    * @param mpp MPP file
    */
   private void testSplitTasks(ProjectFile mpp)
   {
      Task task1 = mpp.getTaskByID(Integer.valueOf(1));
      Task task2 = mpp.getTaskByID(Integer.valueOf(2));

      List<LocalDateTimeRange> listSplits1 = task1.getSplits();
      List<LocalDateTimeRange> listSplits2 = task2.getSplits();

      assertEquals(3, listSplits1.size());
      assertEquals(5, listSplits2.size());

      testSplit(listSplits1.get(0), "21/09/2006 08:00", "26/09/2006 17:00");
      testSplit(listSplits1.get(1), "27/09/2006 08:00", "29/09/2006 17:00");
      testSplit(listSplits1.get(2), "02/10/2006 08:00", "09/10/2006 17:00");

      testSplit(listSplits2.get(0), "21/09/2006 08:00", "25/09/2006 17:00");
      testSplit(listSplits2.get(1), "26/09/2006 08:00", "27/09/2006 17:00");
      testSplit(listSplits2.get(2), "28/09/2006 08:00", "04/10/2006 17:00");
      testSplit(listSplits2.get(3), "05/10/2006 08:00", "09/10/2006 17:00");
      testSplit(listSplits2.get(4), "10/10/2006 08:00", "18/10/2006 17:00");
   }

   /**
    * Utility method to test a split task date range.
    *
    * @param range DateRange instance
    * @param start expected start date
    * @param end expected end date
    */
   private void testSplit(LocalDateTimeRange range, String start, String end)
   {
      assertEquals(start, m_df.format(range.getStart()));
      assertEquals(end, m_df.format(range.getEnd()));
   }

   /**
    * Tests Relations.
    *
    * @param mpp mpp file
    */
   private void testRelations(ProjectFile mpp)
   {
      List<Task> listAllTasks = mpp.getTasks();
      assertNotNull(listAllTasks);

      Task task1 = mpp.getTaskByID(Integer.valueOf(1));
      Task task2 = mpp.getTaskByID(Integer.valueOf(2));
      Task task3 = mpp.getTaskByID(Integer.valueOf(3));
      Task task4 = mpp.getTaskByID(Integer.valueOf(4));
      Task task5 = mpp.getTaskByID(Integer.valueOf(5));

      List<Relation> listPreds = task2.getPredecessors();
      Relation relation = listPreds.get(0);
      assertEquals(1, relation.getPredecessorTask().getUniqueID().intValue());
      assertEquals(RelationType.FINISH_START, relation.getType());
      assertEquals(task1, relation.getPredecessorTask());

      listPreds = task3.getPredecessors();
      relation = listPreds.get(0);
      assertEquals(2, relation.getPredecessorTask().getUniqueID().intValue());
      assertEquals(RelationType.START_START, relation.getType());
      Duration duration = relation.getLag();
      if (duration.getUnits() == TimeUnit.DAYS)
      {
         assertEquals(1, (int) duration.getDuration());
      }
      else
      {
         if (duration.getUnits() == TimeUnit.HOURS)
         {
            assertEquals(8, (int) duration.getDuration());
         }
      }

      listPreds = task4.getPredecessors();
      relation = listPreds.get(0);
      assertEquals(3, relation.getPredecessorTask().getUniqueID().intValue());
      assertEquals(RelationType.FINISH_FINISH, relation.getType());

      boolean removed = task4.removePredecessor(relation.getPredecessorTask(), relation.getType(), relation.getLag());
      assertTrue(removed);
      listPreds = task4.getPredecessors();
      assertTrue(listPreds.isEmpty());

      task4.addPredecessor(new Relation.Builder().from(relation));
      task4.addPredecessor(new Relation.Builder().predecessorTask(task2));
      assertEquals(2, task4.getPredecessors().size());

      removed = task4.removePredecessor(task2, RelationType.FINISH_FINISH, Duration.getInstance(0, TimeUnit.DAYS));
      assertFalse(removed);

      task4.addPredecessor(new Relation.Builder().predecessorTask(task2));
      assertEquals(2, task4.getPredecessors().size());
      removed = task4.removePredecessor(task2, RelationType.FINISH_START, Duration.getInstance(0, TimeUnit.DAYS));
      assertTrue(removed);

      listPreds = task4.getPredecessors();
      relation = listPreds.get(0);
      assertEquals(3, relation.getPredecessorTask().getUniqueID().intValue());
      assertEquals(RelationType.FINISH_FINISH, relation.getType());

      listPreds = task5.getPredecessors();
      relation = listPreds.get(0);
      assertEquals(4, relation.getPredecessorTask().getUniqueID().intValue());
      assertEquals(RelationType.START_FINISH, relation.getType());
   }

   private final DateTimeFormatter m_df = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
}
