/*
 * file:       MppAssignmentTest.java
 * author:     Jon Iles
 * copyright:  (c) Packwood Software 2011
 * date:       09/06/2011
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

import org.mpxj.Duration;
import org.mpxj.ProjectFile;
import org.mpxj.ResourceAssignment;
import org.mpxj.Task;
import org.mpxj.TimeUnit;
import org.mpxj.common.NumberHelper;
import org.mpxj.mpd.MPDFileReader;
import org.mpxj.mpp.MPPReader;
import org.mpxj.mspdi.MSPDIReader;

import org.junit.Test;

/**
 * Tests to exercise file read functionality for various MS project file types.
 */
public class MppAssignmentTest
{

   /**
    * Test assignment data read from an MPP9 file.
    */
   @Test public void testMpp9CustomFields() throws Exception
   {
      MPPReader reader = new MPPReader();
      ProjectFile mpp = reader.read(MpxjTestData.filePath("mpp9assignmentcustom.mpp"));
      testCustomFields(mpp);
   }

   /**
    * Test assignment data read from an MPP9 file saved by Project 2007.
    */
   @Test public void testMpp9CustomFieldsFrom12() throws Exception
   {
      MPPReader reader = new MPPReader();
      ProjectFile mpp = reader.read(MpxjTestData.filePath("mpp9assignmentcustom-from12.mpp"));
      testCustomFields(mpp);
   }

   /**
    * Test assignment data read from an MPP12 file.
    */
   @Test public void testMpp12CustomFields() throws Exception
   {
      MPPReader reader = new MPPReader();
      ProjectFile mpp = reader.read(MpxjTestData.filePath("mpp12assignmentcustom.mpp"));
      testCustomFields(mpp);
   }

   /*
    * Test assignment data read from an MPP12 file saved  by Project 2010.
    *
    * @throws Exception
    */
   // Sadly this doesn't work, as we just don't understand how a couple of
   // the large var data index values actually work. See FieldMap14 for details
   //   @Test public void testMpp12CustomFieldsFrom14() throws Exception
   //   {
   //      MPPReader reader = new MPPReader();
   //      ProjectFile mpp = reader.read(MpxjTestData.filePath("mpp12assignmentcustom-from14.mpp"));
   //      testCustomFields(mpp);
   //   }

   /**
    * Test assignment data read from an MPP14 file.
    */
   @Test public void testMpp14CustomFields() throws Exception
   {
      MPPReader reader = new MPPReader();
      ProjectFile mpp = reader.read(MpxjTestData.filePath("mpp14assignmentcustom.mpp"));
      testCustomFields(mpp);
   }

   /**
    * Test assignment data read from an MSPDI file.
    */
   @Test public void testMspdiCustomFields() throws Exception
   {
      MSPDIReader reader = new MSPDIReader();
      ProjectFile mpp = reader.read(MpxjTestData.filePath("mspdiassignmentcustom.xml"));
      testCustomFields(mpp);
   }

   /**
    * Test assignment data read from an MPD file.
    */
   @Test public void testMpdCustomFields() throws Exception
   {
      MPDFileReader reader = new MPDFileReader();
      ProjectFile mpp = reader.read(MpxjTestData.filePath("mpdassignmentcustom.mpd"));
      testCustomFields(mpp);
   }

   /**
    * Validate custom field values.
    *
    * @param mpp project file
    */
   private void testCustomFields(ProjectFile mpp)
   {
      DateTimeFormatter df = DateTimeFormatter.ofPattern("dd/MM/yy HH:mm");
      Task task = mpp.getTaskByID(Integer.valueOf(1));
      assertEquals("Task One", task.getName());
      List<ResourceAssignment> assignments = task.getResourceAssignments();
      ResourceAssignment assignment1 = assignments.get(0);
      assertEquals("Resource One", assignment1.getResource().getName());
      ResourceAssignment assignment2 = assignments.get(1);
      assertEquals("Resource Two", assignment2.getResource().getName());

      for (int loop = 0; loop < 10; loop++)
      {
         assertEquals("Assignment 1 baseline cost " + (loop + 1), BASELINE_COSTS[0][loop], assignment1.getBaselineCost(loop + 1).intValue());
         assertEquals("Assignment 2 baseline cost " + (loop + 1), BASELINE_COSTS[1][loop], assignment2.getBaselineCost(loop + 1).intValue());

         assertEquals("Assignment 1 baseline work " + (loop + 1), BASELINE_WORKS[0][loop], (int) assignment1.getBaselineWork(loop + 1).getDuration());
         assertEquals("Assignment 2 baseline work " + (loop + 1), BASELINE_WORKS[1][loop], (int) assignment2.getBaselineWork(loop + 1).getDuration());
         assertEquals("Assignment 1 baseline work " + (loop + 1), TimeUnit.HOURS, assignment1.getBaselineWork(loop + 1).getUnits());
         assertEquals("Assignment 2 baseline work " + (loop + 1), TimeUnit.HOURS, assignment2.getBaselineWork(loop + 1).getUnits());

         assertEquals("Assignment 1 baseline start " + (loop + 1), BASELINE_STARTS[0][loop], df.format(assignment1.getBaselineStart(loop + 1)));
         assertEquals("Assignment 2 baseline start " + (loop + 1), BASELINE_STARTS[1][loop], df.format(assignment2.getBaselineStart(loop + 1)));

         assertEquals("Assignment 1 baseline finish " + (loop + 1), BASELINE_FINISHES[0][loop], df.format(assignment1.getBaselineFinish(loop + 1)));
         assertEquals("Assignment 2 baseline finish " + (loop + 1), BASELINE_FINISHES[1][loop], df.format(assignment2.getBaselineFinish(loop + 1)));

         assertEquals("Assignment 1 start " + (loop + 1), CUSTOM_START[0][loop], df.format(assignment1.getStart(loop + 1)));
         assertEquals("Assignment 2 start " + (loop + 1), CUSTOM_START[1][loop], df.format(assignment2.getStart(loop + 1)));

         assertEquals("Assignment 1 finish " + (loop + 1), CUSTOM_FINISH[0][loop], df.format(assignment1.getFinish(loop + 1)));
         assertEquals("Assignment 2 finish " + (loop + 1), CUSTOM_FINISH[1][loop], df.format(assignment2.getFinish(loop + 1)));

         assertEquals("Assignment 1 date " + (loop + 1), CUSTOM_DATE[0][loop], df.format(assignment1.getDate(loop + 1)));
         assertEquals("Assignment 2 date " + (loop + 1), CUSTOM_DATE[1][loop], df.format(assignment2.getDate(loop + 1)));

         assertEquals("Assignment 1 duration " + (loop + 1), CUSTOM_DURATION[0][loop], assignment1.getDuration(loop + 1).getDuration(), 0.01);
         assertEquals("Assignment 2 duration " + (loop + 1), CUSTOM_DURATION[1][loop], assignment2.getDuration(loop + 1).getDuration(), 0.01);
         assertEquals("Assignment 1 duration " + (loop + 1), TimeUnit.DAYS, assignment1.getDuration(loop + 1).getUnits());
         assertEquals("Assignment 2 duration " + (loop + 1), TimeUnit.DAYS, assignment2.getDuration(loop + 1).getUnits());

         assertEquals("Assignment 1 cost " + (loop + 1), CUSTOM_COST[0][loop], assignment1.getCost(loop + 1).doubleValue(), 0.01);
         assertEquals("Assignment 2 cost " + (loop + 1), CUSTOM_COST[1][loop], assignment2.getCost(loop + 1).doubleValue(), 0.01);
      }

      for (int loop = 0; loop < CUSTOM_TEXT.length; loop++)
      {
         assertEquals("Assignment 1 text " + (loop + 1), CUSTOM_TEXT[0][loop], assignment1.getText(loop + 1));
         assertEquals("Assignment 2 text " + (loop + 1), CUSTOM_TEXT[1][loop], assignment2.getText(loop + 1));
      }

      for (int loop = 0; loop < CUSTOM_NUMBER.length; loop++)
      {
         assertEquals("Assignment 1 number " + (loop + 1), CUSTOM_NUMBER[0][loop], assignment1.getNumber(loop + 1).intValue());
         assertEquals("Assignment 2 number " + (loop + 1), CUSTOM_NUMBER[1][loop], assignment2.getNumber(loop + 1).intValue());
      }

      for (int loop = 0; loop < CUSTOM_FLAG.length; loop++)
      {
         assertBooleanEquals("Assignment 1 flag " + (loop + 1), CUSTOM_FLAG[0][loop], assignment1.getFlag(loop + 1));
         assertBooleanEquals("Assignment 2 flag " + (loop + 1), CUSTOM_FLAG[1][loop], assignment2.getFlag(loop + 1));
      }
   }

   /**
    * Test assignment fields read from an MPP9 file.
    */
   @Test public void testMpp9Fields() throws Exception
   {
      MPPReader reader = new MPPReader();
      ProjectFile mpp = reader.read(MpxjTestData.filePath("mpp9assignmentfields.mpp"));
      testFields(mpp, null, null);
   }

   /**
    * Test assignment fields read from an MPP9 file, saved by Project 2010.
    */
   @Test public void testMpp9FieldsFrom14() throws Exception
   {
      MPPReader reader = new MPPReader();
      ProjectFile mpp = reader.read(MpxjTestData.filePath("mpp9assignmentfields-from14.mpp"));
      testFields(mpp, null, null);
   }

   /**
    * Test assignment fields read from an MPP12 file.
    */
   @Test public void testMpp12Fields() throws Exception
   {
      MPPReader reader = new MPPReader();
      ProjectFile mpp = reader.read(MpxjTestData.filePath("mpp12assignmentfields.mpp"));
      testFields(mpp, "230CA12B-3792-4F3B-B69E-89ABAF1C9042", "C3FDB823-3C82-422B-A854-391F7E235EA2");
   }

   /**
    * Test assignment fields read from an MPP12 file, saved by Project 2010.
    */
   @Test public void testMpp12FieldsFrom14() throws Exception
   {
      MPPReader reader = new MPPReader();
      ProjectFile mpp = reader.read(MpxjTestData.filePath("mpp12assignmentfields-from14.mpp"));
      testFields(mpp, "230CA12B-3792-4F3B-B69E-89ABAF1C9042", "C3FDB823-3C82-422B-A854-391F7E235EA2");
   }

   /**
    * Test assignment fields read from an MPP14 file.
    */
   @Test public void testMpp14Fields() throws Exception
   {
      MPPReader reader = new MPPReader();
      ProjectFile mpp = reader.read(MpxjTestData.filePath("mpp14assignmentfields.mpp"));
      testFields(mpp, "81DC0978-D218-4D29-A139-EF691CDBF851", "0040EAF6-D0A2-41DF-9F67-A3CAEBCC8C5B");
   }

   /**
    * Test assignment fields read from an MSPDI file.
    */
   @Test public void testMspdiFields() throws Exception
   {
      MSPDIReader reader = new MSPDIReader();
      ProjectFile mpp = reader.read(MpxjTestData.filePath("mspdiassignmentfields.xml"));
      testFields(mpp, null, null);
   }

   /**
    * Test assignment fields read from an MPD file.
    */
   @Test public void testMpdFields() throws Exception
   {
      MPDFileReader reader = new MPDFileReader();
      ProjectFile mpp = reader.read(MpxjTestData.filePath("mpdassignmentfields.mpd"));
      testFields(mpp, null, null);
   }

   /**
    * Common field value tests for project files.
    *
    * @param mpp project file
    * @param guid1 expected GUID - varies between file types
    * @param guid2 expected GUID - varies between file types
    */
   private void testFields(ProjectFile mpp, String guid1, String guid2)
   {
      DateTimeFormatter df = DateTimeFormatter.ofPattern("dd/MM/yy HH:mm");

      //
      // Retrieve the summary task
      //
      Task task;
      List<ResourceAssignment> assignments;
      ResourceAssignment assignment;
      int mppFileType = NumberHelper.getInt(mpp.getProjectProperties().getMppFileType());

      if (mppFileType > 9)
      {
         task = mpp.getTaskByID(Integer.valueOf(0));

         assignments = task.getResourceAssignments();
         assignment = assignments.get(0);
         assertEquals("Budget Work Resource", assignment.getResource().getName());
         assertEquals(97, (int) assignment.getBudgetWork().getDuration());
         assertEquals(98, (int) assignment.getBaselineBudgetWork().getDuration());
         // test budget flag?

         for (int loop = 1; loop <= 10; loop++)
         {
            Number cost = assignment.getBaselineBudgetCost(loop);
            assertEquals(0, cost.intValue());
            Duration work = assignment.getBaselineBudgetWork(loop);
            assertEquals(loop, (int) work.getDuration());
            assertEquals(TimeUnit.HOURS, work.getUnits());
         }

         assignment = assignments.get(1);
         assertEquals("Budget Cost Resource", assignment.getResource().getName());
         assertEquals(96, assignment.getBudgetCost().intValue());
         assertEquals(95, assignment.getBaselineBudgetCost().intValue());
         for (int loop = 1; loop <= 10; loop++)
         {
            Number cost = assignment.getBaselineBudgetCost(loop);
            assertEquals(loop, cost.intValue());
            Duration work = assignment.getBaselineBudgetWork(loop);
            assertEquals(0, (int) work.getDuration());
         }
      }

      task = mpp.getTaskByID(Integer.valueOf(1));
      assignments = task.getResourceAssignments();
      assignment = assignments.get(0);
      assertEquals("Resource One", assignment.getResource().getName());

      assertDurationEquals(2, TimeUnit.HOURS, assignment.getActualWork());
      assertDurationEquals(71, TimeUnit.HOURS, assignment.getRegularWork());
      assertDurationEquals(1.1, TimeUnit.HOURS, assignment.getActualOvertimeWork());
      assertDurationEquals(7.9, TimeUnit.HOURS, assignment.getRemainingOvertimeWork());
      assertEquals(540, assignment.getOvertimeCost().intValue());

      //
      // Bizarre MPP12 bug? - shows as zero in MS Project
      //
      if (mppFileType != 12)
      {
         assertEquals(3978.92, assignment.getRemainingCost().doubleValue(), 0.005);
      }

      assertEquals(66.08, assignment.getActualOvertimeCost().doubleValue(), 0.005);
      assertEquals(473.92, assignment.getRemainingOvertimeCost().doubleValue(), 0.005);
      //assertEquals(111.08, assignment.getACWP().doubleValue(), 0.001);
      //assertEquals(-111.08, assignment.getCV().doubleValue(), 0.001);
      assertEquals(4090.00, assignment.getCostVariance().doubleValue(), 0.001);
      assertEquals(3.0, assignment.getPercentageWorkComplete().doubleValue(), 0.5);
      assertEquals("Assignment Notes", assignment.getNotes().trim());

      if (mppFileType != 0)
      {
         assertTrue(assignment.getConfirmed());
         assertTrue(assignment.getResponsePending());
         assertFalse(assignment.getTeamStatusPending());
      }

      MpxjAssert.assertDurationEquals(80, TimeUnit.HOURS, assignment.getWorkVariance());
      MpxjAssert.assertDurationEquals(2, TimeUnit.DAYS, assignment.getStartVariance());
      MpxjAssert.assertDurationEquals(-2.12, TimeUnit.DAYS, assignment.getFinishVariance());
      assertEquals(0, assignment.getCostRateTableIndex());

      //
      // Can't reliably find the create date in MPP9
      //
      if (mppFileType > 9)
      {
         assertEquals("06/07/11 12:09", df.format(assignment.getCreateDate()));
      }

      if (guid1 != null)
      {
         assertEquals(guid1, assignment.getGUID().toString().toUpperCase());
      }

      assignment = assignments.get(1);
      assertEquals("Resource Two", assignment.getResource().getName());

      MpxjAssert.assertDurationEquals(5, TimeUnit.HOURS, assignment.getActualWork());
      MpxjAssert.assertDurationEquals(3, TimeUnit.HOURS, assignment.getRegularWork());
      MpxjAssert.assertDurationEquals(2, TimeUnit.HOURS, assignment.getActualOvertimeWork());
      MpxjAssert.assertDurationEquals(18, TimeUnit.HOURS, assignment.getRemainingOvertimeWork());
      assertEquals(860, assignment.getOvertimeCost().intValue());
      assertEquals(774, assignment.getRemainingCost().doubleValue(), 0.005);
      assertEquals(86, assignment.getActualOvertimeCost().doubleValue(), 0.005);
      assertEquals(774, assignment.getRemainingOvertimeCost().doubleValue(), 0.005);
      //assertEquals(188, assignment.getACWP().doubleValue(), 0.001);
      //assertEquals(-188, assignment.getCV().doubleValue(), 0.001);
      assertEquals(962, assignment.getCostVariance().doubleValue(), 0.001);
      assertEquals(22, assignment.getPercentageWorkComplete().doubleValue(), 0.5);
      assertEquals("", assignment.getNotes());
      MpxjAssert.assertDurationEquals(23, TimeUnit.HOURS, assignment.getWorkVariance());
      MpxjAssert.assertDurationEquals(1.11, TimeUnit.DAYS, assignment.getStartVariance());
      MpxjAssert.assertDurationEquals(-10.39, TimeUnit.DAYS, assignment.getFinishVariance());
      assertEquals(1, assignment.getCostRateTableIndex());

      if (mppFileType != 0)
      {
         assertFalse(assignment.getConfirmed());
         assertFalse(assignment.getResponsePending());
         assertTrue(assignment.getTeamStatusPending());
         assertEquals("Test Hyperlink Screen Tip", assignment.getHyperlinkScreenTip());
      }

      if (mppFileType > 9)
      {
         assertEquals("06/07/11 15:31", df.format(assignment.getCreateDate()));
      }

      if (guid2 != null)
      {
         assertEquals(guid2, assignment.getGUID().toString().toUpperCase());
      }

      assertEquals("Test Hyperlink Display Text", assignment.getHyperlink());
      assertEquals("http://news.bbc.co.uk", assignment.getHyperlinkAddress());
      assertEquals("x", assignment.getHyperlinkSubAddress());
   }

   private static final int[][] BASELINE_COSTS =
   {
      {
         1,
         2,
         3,
         4,
         5,
         6,
         7,
         8,
         9,
         10
      },
      {
         11,
         12,
         13,
         14,
         15,
         16,
         17,
         18,
         19,
         20
      }
   };

   private static final int[][] BASELINE_WORKS =
   {
      {
         1,
         2,
         3,
         4,
         5,
         6,
         7,
         8,
         9,
         10
      },
      {
         11,
         12,
         13,
         14,
         15,
         16,
         17,
         18,
         19,
         20
      }
   };

   private static final String[][] BASELINE_STARTS =
   {
      {
         "01/01/10 08:00",
         "02/01/10 08:00",
         "03/01/10 08:00",
         "04/01/10 08:00",
         "05/01/10 08:00",
         "06/01/10 08:00",
         "07/01/10 08:00",
         "08/01/10 08:00",
         "09/01/10 08:00",
         "10/01/10 08:00"
      },
      {
         "01/02/10 08:00",
         "02/02/10 08:00",
         "03/02/10 08:00",
         "04/02/10 08:00",
         "05/02/10 08:00",
         "06/02/10 08:00",
         "07/02/10 08:00",
         "08/02/10 08:00",
         "09/02/10 08:00",
         "10/02/10 08:00"
      }
   };

   private static final String[][] BASELINE_FINISHES =
   {
      {
         "01/01/09 17:00",
         "02/01/09 17:00",
         "03/01/09 17:00",
         "04/01/09 17:00",
         "05/01/09 17:00",
         "06/01/09 17:00",
         "07/01/09 17:00",
         "08/01/09 17:00",
         "09/01/09 17:00",
         "10/01/09 17:00"
      },
      {
         "01/02/09 17:00",
         "02/02/09 17:00",
         "03/02/09 17:00",
         "04/02/09 17:00",
         "05/02/09 17:00",
         "06/02/09 17:00",
         "07/02/09 17:00",
         "08/02/09 17:00",
         "09/02/09 17:00",
         "10/02/09 17:00"
      }
   };

   private static final String[][] CUSTOM_TEXT =
   {
      {
         "t1",
         "t2",
         "t3",
         "t4",
         "t5",
         "t6",
         "t7",
         "t8",
         "t9",
         "t10",
         "t11",
         "t12",
         "t13",
         "t14",
         "t15",
         "t16",
         "t17",
         "t18",
         "t19",
         "t20",
         "t21",
         "t22",
         "t23",
         "t24",
         "t25",
         "t26",
         "t27",
         "t28",
         "t29",
         "t30"
      },
      {
         "a1",
         "a2",
         "a3",
         "a4",
         "a5",
         "a6",
         "a7",
         "a8",
         "a9",
         "a10",
         "a11",
         "a12",
         "a13",
         "a14",
         "a15",
         "a16",
         "a17",
         "a18",
         "a19",
         "a20",
         "a21",
         "a22",
         "a23",
         "a24",
         "a25",
         "a26",
         "a27",
         "a28",
         "a29",
         "a30"
      }
   };

   private static final String[][] CUSTOM_START =
   {
      {
         "01/01/11 08:00",
         "02/01/11 08:00",
         "03/01/11 08:00",
         "04/01/11 08:00",
         "05/06/11 08:00",
         "06/01/11 08:00",
         "07/01/11 08:00",
         "08/01/11 08:00",
         "09/01/11 08:00",
         "10/01/11 08:00"
      },
      {
         "01/02/11 08:00",
         "02/02/11 08:00",
         "03/02/11 08:00",
         "04/02/11 08:00",
         "05/02/11 08:00",
         "06/02/11 08:00",
         "07/02/11 08:00",
         "08/02/11 08:00",
         "09/02/11 08:00",
         "10/02/11 08:00"
      }
   };

   private static final String[][] CUSTOM_FINISH =
   {
      {
         "01/03/11 17:00",
         "02/03/11 17:00",
         "03/03/11 17:00",
         "04/03/11 17:00",
         "05/03/11 17:00",
         "06/03/11 17:00",
         "07/03/11 17:00",
         "08/03/11 17:00",
         "09/03/11 17:00",
         "10/03/11 17:00"
      },
      {
         "01/04/11 17:00",
         "02/04/11 17:00",
         "03/04/11 17:00",
         "04/04/11 17:00",
         "05/04/11 17:00",
         "06/04/11 17:00",
         "07/04/11 17:00",
         "08/04/11 17:00",
         "09/04/11 17:00",
         "10/04/11 17:00"
      }
   };

   private static final String[][] CUSTOM_DATE =
   {
      {
         "01/05/11 08:00",
         "02/05/11 08:00",
         "03/05/11 08:00",
         "04/05/11 08:00",
         "05/05/11 08:00",
         "06/05/11 08:00",
         "07/05/11 08:00",
         "08/05/11 08:00",
         "09/05/11 08:00",
         "10/05/11 08:00"
      },
      {
         "01/06/11 08:00",
         "02/06/11 08:00",
         "03/06/11 08:00",
         "04/06/11 08:00",
         "05/06/11 08:00",
         "06/06/11 08:00",
         "07/06/11 08:00",
         "08/06/11 08:00",
         "09/06/11 08:00",
         "10/06/11 08:00"
      }
   };

   private static final int[][] CUSTOM_NUMBER =
   {
      {
         1,
         2,
         3,
         4,
         5,
         6,
         7,
         8,
         9,
         10,
         11,
         12,
         13,
         14,
         15,
         16,
         17,
         18,
         19,
         20
      },
      {
         21,
         22,
         23,
         24,
         25,
         26,
         27,
         28,
         29,
         30,
         31,
         32,
         33,
         34,
         35,
         36,
         37,
         38,
         39,
         40
      }
   };

   private static final double[][] CUSTOM_DURATION =
   {
      {
         0.13,
         0.25,
         0.38,
         0.5,
         0.63,
         0.75,
         0.88,
         1,
         1.13,
         1.25
      },
      {
         1.38,
         1.5,
         1.63,
         1.75,
         1.88,
         2,
         2.13,
         2.25,
         2.38,
         2.5
      }
   };

   private static final double[][] CUSTOM_COST =
   {
      {
         0.01,
         0.20,
         0.03,
         0.04,
         0.05,
         0.06,
         0.07,
         0.08,
         0.09,
         0.10
      },
      {
         0.11,
         0.12,
         0.13,
         0.14,
         0.15,
         0.16,
         0.17,
         0.18,
         0.19,
         0.20
      }
   };

   private static final boolean[][] CUSTOM_FLAG =
   {
      {
         true,
         false,
         true,
         false,
         true,
         false,
         true,
         false,
         true,
         false,
         true,
         false,
         true,
         false,
         true,
         false,
         true,
         false,
         true,
         false
      },
      {
         false,
         true,
         false,
         true,
         false,
         true,
         false,
         true,
         false,
         true,
         false,
         true,
         false,
         true,
         false,
         true,
         false,
         true,
         false,
         true
      }
   };
}
