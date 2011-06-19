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

package net.sf.mpxj.junit;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.List;

import net.sf.mpxj.ProjectFile;
import net.sf.mpxj.ResourceAssignment;
import net.sf.mpxj.Task;
import net.sf.mpxj.TimeUnit;
import net.sf.mpxj.mpd.MPDDatabaseReader;
import net.sf.mpxj.mpp.MPPReader;
import net.sf.mpxj.mspdi.MSPDIReader;

/**
 * Tests to exercise file read functionality for various MS project file types.
 */
public class MppAssignmentTest extends MPXJTestCase
{

   /**
    * Test assignment data read from an MPP9 file.
    * 
    * @throws Exception
    */
   public void testMpp9CustomFields() throws Exception
   {
      MPPReader reader = new MPPReader();
      ProjectFile mpp = reader.read(m_basedir + "/mpp9assignmentcustom.mpp");
      testCustomFields(mpp);
   }

   /**
    * Test assignment data read from an MPP9 file saved by Project 2007.
    * 
    * @throws Exception
    */
   public void testMpp9CustomFieldsFrom12() throws Exception
   {
      MPPReader reader = new MPPReader();
      ProjectFile mpp = reader.read(m_basedir + "/mpp9assignmentcustom-from12.mpp");
      testCustomFields(mpp);
   }

   /**
    * Test assignment data read from an MPP12 file.
    * 
    * @throws Exception
    */
   public void testMpp12CustomFields() throws Exception
   {
      MPPReader reader = new MPPReader();
      ProjectFile mpp = reader.read(m_basedir + "/mpp12assignmentcustom.mpp");
      testCustomFields(mpp);
   }

   /**
    * Test assignment data read from an MPP12 file saved  by Project 2010.
    * 
    * @throws Exception
    */
   // Sadly this doesn't work, as we just don't understand how a couple of
   // the large var data index values actually work. See FieldMap14 for details
   //   public void testMpp12CustomFieldsFrom14() throws Exception
   //   {
   //      MPPReader reader = new MPPReader();
   //      ProjectFile mpp = reader.read(m_basedir + "/mpp12assignmentcustom-from14.mpp");
   //      testCustomFields(mpp);
   //   }

   /**
    * Test assignment data read from an MPP14 file.
    * 
    * @throws Exception
    */
   public void testMpp14CustomFields() throws Exception
   {
      MPPReader reader = new MPPReader();
      ProjectFile mpp = reader.read(m_basedir + "/mpp14assignmentcustom.mpp");
      testCustomFields(mpp);
   }

   /**
    * Test assignment data read from an MSPDI file.
    * 
    * @throws Exception
    */
   public void testMspdiCustomFields() throws Exception
   {
      MSPDIReader reader = new MSPDIReader();
      ProjectFile mpp = reader.read(m_basedir + "/mspdiassignmentcustom.xml");
      testCustomFields(mpp);
   }

   /**
    * Test assignment data read from an MPD file.
    * 
    * @throws Exception
    */
   public void testMpdCustomFields() throws Exception
   {
      MPDDatabaseReader reader = new MPDDatabaseReader();
      ProjectFile mpp = reader.read(m_basedir + "/mpdassignmentcustom.mpd");
      testCustomFields(mpp);
   }

   /**
    * Validate custom field values.
    * 
    * @param mpp project file
    * @throws Exception
    */
   private void testCustomFields(ProjectFile mpp) throws Exception
   {
      DateFormat df = new SimpleDateFormat("dd/MM/yy HH:mm");
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
         assertEquals("Assignment 1 flag " + (loop + 1), CUSTOM_FLAG[0][loop], assignment1.getFlag(loop + 1));
         assertEquals("Assignment 2 flag " + (loop + 1), CUSTOM_FLAG[1][loop], assignment2.getFlag(loop + 1));
      }

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
