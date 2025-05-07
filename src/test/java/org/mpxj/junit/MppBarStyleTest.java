/*
 * file:       MppBarStyleTest.java
 * author:     Jon Iles
 * copyright:  (c) Packwood Software 2010
 * date:       12/04/2010
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

import java.awt.Color;
import java.util.List;

import org.mpxj.ProjectFile;
import org.mpxj.TaskField;
import org.mpxj.View;
import org.mpxj.mpp.ChartPattern;
import org.mpxj.mpp.ColorType;
import org.mpxj.mpp.GanttBarCommonStyle;
import org.mpxj.mpp.GanttBarMiddleShape;
import org.mpxj.mpp.GanttBarStartEndShape;
import org.mpxj.mpp.GanttBarStartEndType;
import org.mpxj.mpp.GanttBarStyle;
import org.mpxj.mpp.GanttBarStyleException;
import org.mpxj.mpp.GanttChartView;
import org.mpxj.mpp.MPPReader;

import org.junit.Test;

/**
 * Tests to exercise MPP file read functionality for various versions of
 * MPP file.
 */
public class MppBarStyleTest
{

   /**
    * Test bar styles read from an MPP9 file.
    */
   @Test public void testMpp9DefaultBarStyles() throws Exception
   {
      ProjectFile mpp = new MPPReader().read(MpxjTestData.filePath("mpp9barstyle.mpp"));
      testDefaultBarStyles(mpp, DEFAULT_BAR_STYLES);
   }

   /**
    * Test bar styles read from an MPP9 file saved by Project 2007.
    */
   @Test public void testMpp9DefaultBarStylesFrom12() throws Exception
   {
      ProjectFile mpp = new MPPReader().read(MpxjTestData.filePath("mpp9barstyle-from12.mpp"));
      testDefaultBarStyles(mpp, DEFAULT_BAR_STYLES);
   }

   /**
    * Test bar styles read from an MPP9 file saved by Project 2010.
    */
   @Test public void testMpp9DefaultBarStylesFrom14() throws Exception
   {
      ProjectFile mpp = new MPPReader().read(MpxjTestData.filePath("mpp9barstyle-from14.mpp"));
      testDefaultBarStyles(mpp, DEFAULT_BAR_STYLES_FROM14);
   }

   /**
    * Test bar styles read from an MPP12 file.
    */
   @Test public void testMpp12DefaultBarStyles() throws Exception
   {
      ProjectFile mpp = new MPPReader().read(MpxjTestData.filePath("mpp12barstyle.mpp"));
      testDefaultBarStyles(mpp, DEFAULT_BAR_STYLES);
   }

   /**
    * Test bar styles read from an MPP12 file saved by Project 2010.
    */
   @Test public void testMpp12DefaultBarStylesFrom14() throws Exception
   {
      ProjectFile mpp = new MPPReader().read(MpxjTestData.filePath("mpp12barstyle-from14.mpp"));
      testDefaultBarStyles(mpp, DEFAULT_BAR_STYLES_FROM14);
   }

   /**
    * Test bar styles read from an MPP14 file.
    */
   @Test public void testMpp14DefaultBarStyles() throws Exception
   {
      ProjectFile mpp = new MPPReader().read(MpxjTestData.filePath("mpp14barstyle.mpp"));
      testDefaultBarStyles(mpp, DEFAULT_BAR_STYLES14);
   }

   /**
    * Test bar styles read from an MPP9 file.
    */
   @Test public void testMpp9ExceptionBarStyles() throws Exception
   {
      ProjectFile mpp = new MPPReader().read(MpxjTestData.filePath("mpp9barstyle.mpp"));
      testExceptionBarStyles(mpp);
   }

   /**
    * Test bar styles read from an MPP9 file saved by Project 2007.
    */
   @Test public void testMpp9ExceptionBarStylesFrom12() throws Exception
   {
      ProjectFile mpp = new MPPReader().read(MpxjTestData.filePath("mpp9barstyle-from12.mpp"));
      testExceptionBarStyles(mpp);
   }

   /**
    * Test bar styles read from an MPP9 file saved by Project 2010.
    */
   @Test public void testMpp9ExceptionBarStylesFrom14() throws Exception
   {
      ProjectFile mpp = new MPPReader().read(MpxjTestData.filePath("mpp9barstyle-from14.mpp"));
      testExceptionBarStyles(mpp);
   }

   /**
    * Test bar styles read from an MPP12 file.
    */
   @Test public void testMpp12ExceptionBarStyles() throws Exception
   {
      ProjectFile mpp = new MPPReader().read(MpxjTestData.filePath("mpp12barstyle.mpp"));
      testExceptionBarStyles(mpp);
   }

   /**
    * Test bar styles read from an MPP12 file saved by Project 2010.
    */
   @Test public void testMpp12ExceptionBarStylesFrom14() throws Exception
   {
      ProjectFile mpp = new MPPReader().read(MpxjTestData.filePath("mpp12barstyle-from14.mpp"));
      testExceptionBarStyles(mpp);
   }

   /**
    * Test bar styles read from an MPP14 file.
    */
   @Test public void testMpp14ExceptionBarStyles() throws Exception
   {
      ProjectFile mpp = new MPPReader().read(MpxjTestData.filePath("mpp14barstyle.mpp"));
      testExceptionBarStyles(mpp);
   }

   /**
    * Tests default bar styles.
    *
    * @param mpp The ProjectFile being tested.
    * @param styles styles to test
    */
   private void testDefaultBarStyles(ProjectFile mpp, Object[][] styles)
   {
      //
      // Retrieve the views
      //
      List<View> views = mpp.getViews();
      assertNotNull(views);
      assertFalse(views.isEmpty());

      //
      // Retrieve the Gantt Chart view
      //
      GanttChartView view = (GanttChartView) views.get(0);
      assertEquals("Gantt Chart", view.getName());

      //
      // Test the bar styles
      //
      GanttBarStyle[] barStyles = view.getBarStyles();
      for (int loop = 0; loop < barStyles.length; loop++)
      {
         assertStyleEquals(styles[loop], barStyles[loop]);
      }
   }

   /**
    * Tests exception bar styles.
    *
    * @param mpp The ProjectFile being tested.
    */
   private void testExceptionBarStyles(ProjectFile mpp)
   {
      //
      // Retrieve the views
      //
      List<View> views = mpp.getViews();
      assertNotNull(views);
      assertFalse(views.isEmpty());

      //
      // Retrieve the Gantt Chart view
      //
      GanttChartView view = (GanttChartView) views.get(0);
      assertEquals("Gantt Chart", view.getName());

      //
      // Test the exception styles
      //
      GanttBarStyleException[] exceptionStyles = view.getBarStyleExceptions();
      for (int loop = 0; loop < exceptionStyles.length; loop++)
      {
         assertStyleExceptionEquals(EXCEPTION_STYLES[loop], exceptionStyles[loop]);
      }
   }

   /**
    * Used to compare Gantt bar style data retrieved from an MPP
    * file to expected reference data.
    *
    * @param expected reference data
    * @param actual file data
    */
   private void assertStyleEquals(Object[] expected, GanttBarStyle actual)
   {
      assertEquals(expected[14], actual.getName());
      assertEquals(expected[15], Integer.valueOf(actual.getRow()));
      assertEquals(expected[16], actual.getFromField());
      assertEquals(expected[17], actual.getToField());
      assertEquals(expected[18], actual.getShowForTasks().toString());
      assertEqualsCommon(expected, actual);
   }

   /**
    * Used to compare Gantt bar style data retrieved from an MPP
    * file to expected reference data.
    *
    * @param expected reference data
    * @param actual file data
    */
   private void assertStyleExceptionEquals(Object[] expected, GanttBarStyleException actual)
   {
      assertEquals(expected[14], Integer.valueOf(actual.getTaskUniqueID()));
      assertEquals(expected[15], actual.getGanttBarStyleID());
      assertEqualsCommon(expected, actual);
   }

   /**
    * Used to compare Gantt bar style data retrieved from an MPP
    * file to expected reference data.
    *
    * @param expected reference data
    * @param actual file data
    */
   private void assertEqualsCommon(Object[] expected, GanttBarCommonStyle actual)
   {
      assertEquals(expected[0], actual.getLeftText());
      assertEquals(expected[1], actual.getRightText());
      assertEquals(expected[2], actual.getTopText());
      assertEquals(expected[3], actual.getBottomText());
      assertEquals(expected[4], actual.getInsideText());

      assertEquals(expected[5], actual.getStartShape());
      assertEquals(expected[6], actual.getStartType());
      assertColorEquals(expected[7], actual.getStartColor());

      assertEquals(expected[8], actual.getMiddleShape());
      assertEquals(expected[9], actual.getMiddlePattern());
      assertColorEquals(expected[10], actual.getMiddleColor());

      assertEquals(expected[11], actual.getEndShape());
      assertEquals(expected[12], actual.getEndType());
      assertColorEquals(expected[13], actual.getEndColor());
   }

   /**
    * Compares colors.
    *
    * @param expected expected color
    * @param actual actual color
    */
   private void assertColorEquals(Object expected, Color actual)
   {
      if (expected instanceof ColorType)
      {
         expected = ((ColorType) expected).getColor();
      }
      assertEquals(expected, actual);
   }

   private static final Object[][] DEFAULT_BAR_STYLES =
   {
      {
         null,
         TaskField.RESOURCE_NAMES,
         null,
         null,
         null,
         GanttBarStartEndShape.NONE,
         GanttBarStartEndType.SOLID,
         ColorType.BLACK,
         GanttBarMiddleShape.ROUNDED,
         ChartPattern.SOLID,
         ColorType.BLUE,
         GanttBarStartEndShape.NONE,
         GanttBarStartEndType.SOLID,
         ColorType.BLACK,
         "Task",
         Integer.valueOf(1),
         TaskField.START,
         TaskField.FINISH,
         "[Normal]"
      },

      {
         null,
         null,
         null,
         null,
         null,
         GanttBarStartEndShape.NONE,
         GanttBarStartEndType.SOLID,
         ColorType.BLACK,
         GanttBarMiddleShape.BOTTOMLINE,
         ChartPattern.VERTICALSTRIPE,
         ColorType.BLUE,
         GanttBarStartEndShape.NONE,
         GanttBarStartEndType.SOLID,
         ColorType.BLACK,
         "Split",
         Integer.valueOf(1),
         TaskField.START,
         TaskField.FINISH,
         "[Normal, Split]"
      },

      {
         null,
         null,
         null,
         null,
         null,
         GanttBarStartEndShape.NONE,
         GanttBarStartEndType.SOLID,
         ColorType.BLACK,
         GanttBarMiddleShape.MIDDLETHINROUNDED,
         ChartPattern.SOLID,
         ColorType.BLACK,
         GanttBarStartEndShape.NONE,
         GanttBarStartEndType.SOLID,
         ColorType.BLACK,
         "Progress",
         Integer.valueOf(1),
         TaskField.ACTUAL_START,
         TaskField.COMPLETE_THROUGH,
         "[Normal]"
      },

      {
         null,
         TaskField.FINISH,
         null,
         null,
         null,
         GanttBarStartEndShape.DIAMOND,
         GanttBarStartEndType.SOLID,
         ColorType.BLACK,
         GanttBarMiddleShape.NONE,
         ChartPattern.SOLID,
         ColorType.BLACK,
         GanttBarStartEndShape.NONE,
         GanttBarStartEndType.SOLID,
         ColorType.BLACK,
         "Milestone",
         Integer.valueOf(1),
         TaskField.FINISH,
         TaskField.FINISH,
         "[Milestone]"
      },

      {
         null,
         null,
         null,
         null,
         null,
         GanttBarStartEndShape.SOUTHHOMEPLATE,
         GanttBarStartEndType.SOLID,
         ColorType.BLACK,
         GanttBarMiddleShape.TOPTHINROUNDED,
         ChartPattern.SOLID,
         ColorType.BLACK,
         GanttBarStartEndShape.SOUTHHOMEPLATE,
         GanttBarStartEndType.SOLID,
         ColorType.BLACK,
         "Summary",
         Integer.valueOf(1),
         TaskField.START,
         TaskField.FINISH,
         "[Summary]"
      },

      {
         null,
         null,
         null,
         null,
         null,
         GanttBarStartEndShape.SOUTHHOMEPLATE,
         GanttBarStartEndType.SOLID,
         ColorType.GRAY,
         GanttBarMiddleShape.TOPTHINROUNDED,
         ChartPattern.SOLID,
         ColorType.GRAY,
         GanttBarStartEndShape.SOUTHHOMEPLATE,
         GanttBarStartEndType.SOLID,
         ColorType.GRAY,
         "Project Summary",
         Integer.valueOf(1),
         TaskField.START,
         TaskField.FINISH,
         "[Project Summary]"
      },

      {
         null,
         null,
         null,
         null,
         null,
         GanttBarStartEndShape.SOUTHHOMEPLATE,
         GanttBarStartEndType.SOLID,
         ColorType.BLACK,
         GanttBarMiddleShape.TOPTHINROUNDED,
         ChartPattern.SOLID,
         ColorType.BLACK,
         GanttBarStartEndShape.SOUTHHOMEPLATE,
         GanttBarStartEndType.SOLID,
         ColorType.BLACK,
         "*Group By Summary",
         Integer.valueOf(1),
         TaskField.START,
         TaskField.FINISH,
         "[Group By Summary]"
      },

      {
         null,
         null,
         null,
         null,
         null,
         GanttBarStartEndShape.NONE,
         GanttBarStartEndType.SOLID,
         ColorType.BLACK,
         GanttBarMiddleShape.ROUNDED,
         ChartPattern.SOLID,
         ColorType.BLUE,
         GanttBarStartEndShape.NONE,
         GanttBarStartEndType.SOLID,
         ColorType.BLACK,
         "*Rolled Up Task",
         Integer.valueOf(1),
         TaskField.START,
         TaskField.FINISH,
         "[Normal, Rolled Up, Not Summary]"
      },

      {
         null,
         null,
         null,
         null,
         null,
         GanttBarStartEndShape.NONE,
         GanttBarStartEndType.SOLID,
         ColorType.BLACK,
         GanttBarMiddleShape.BOTTOMLINE,
         ChartPattern.VERTICALSTRIPE,
         ColorType.BLUE,
         GanttBarStartEndShape.NONE,
         GanttBarStartEndType.SOLID,
         ColorType.BLACK,
         "*Rolled Up Split",
         Integer.valueOf(1),
         TaskField.START,
         TaskField.FINISH,
         "[Normal, Rolled Up, Split, Not Summary]"
      },

      {
         null,
         null,
         null,
         null,
         null,
         GanttBarStartEndShape.NONE,
         GanttBarStartEndType.SOLID,
         ColorType.BLACK,
         GanttBarMiddleShape.MIDDLETHINROUNDED,
         ChartPattern.SOLID,
         ColorType.BLACK,
         GanttBarStartEndShape.NONE,
         GanttBarStartEndType.SOLID,
         ColorType.BLACK,
         "*Rolled Up Progress",
         Integer.valueOf(1),
         TaskField.ACTUAL_START,
         TaskField.COMPLETE_THROUGH,
         "[Normal, Rolled Up, Not Summary]"
      },

      {
         null,
         null,
         null,
         null,
         null,
         GanttBarStartEndShape.DIAMOND,
         GanttBarStartEndType.FRAMED,
         ColorType.BLACK,
         GanttBarMiddleShape.NONE,
         ChartPattern.SOLID,
         ColorType.BLACK,
         GanttBarStartEndShape.NONE,
         GanttBarStartEndType.SOLID,
         ColorType.BLACK,
         "*Rolled Up Milestone",
         Integer.valueOf(1),
         TaskField.FINISH,
         TaskField.FINISH,
         "[Milestone, Rolled Up, Not Summary]"
      },

      {
         null,
         null,
         null,
         null,
         null,
         GanttBarStartEndShape.NONE,
         GanttBarStartEndType.SOLID,
         ColorType.BLACK,
         GanttBarMiddleShape.ROUNDED,
         ChartPattern.SOLID,
         ColorType.GRAY,
         GanttBarStartEndShape.NONE,
         GanttBarStartEndType.SOLID,
         ColorType.BLACK,
         "External Tasks",
         Integer.valueOf(1),
         TaskField.START,
         TaskField.FINISH,
         "[External Tasks, Not Milestone]"
      },

      {
         null,
         TaskField.FINISH,
         null,
         null,
         null,
         GanttBarStartEndShape.DIAMOND,
         GanttBarStartEndType.SOLID,
         ColorType.GRAY,
         GanttBarMiddleShape.NONE,
         ChartPattern.SOLID,
         ColorType.BLACK,
         GanttBarStartEndShape.NONE,
         GanttBarStartEndType.SOLID,
         ColorType.BLACK,
         "External Milestone",
         Integer.valueOf(1),
         TaskField.FINISH,
         TaskField.FINISH,
         "[Milestone, External Tasks]"
      },

      {
         null,
         null,
         null,
         null,
         null,
         GanttBarStartEndShape.DOWNPOINTER,
         GanttBarStartEndType.FRAMED,
         ColorType.GREEN,
         GanttBarMiddleShape.NONE,
         ChartPattern.SOLID,
         ColorType.BLACK,
         GanttBarStartEndShape.NONE,
         GanttBarStartEndType.SOLID,
         ColorType.BLACK,
         "Deadline",
         Integer.valueOf(1),
         TaskField.DEADLINE,
         TaskField.DEADLINE,
         "[]"
      },

      {
         null,
         null,
         null,
         null,
         null,
         GanttBarStartEndShape.NORTHMINIHOMEPLATE,
         GanttBarStartEndType.SOLID,
         ColorType.RED,
         GanttBarMiddleShape.NONE,
         ChartPattern.SOLID,
         ColorType.BLACK,
         GanttBarStartEndShape.NONE,
         GanttBarStartEndType.SOLID,
         ColorType.BLACK,
         "*Deliverable Start",
         Integer.valueOf(1),
         TaskField.DELIVERABLE_START, // Displayed as Name by Project 2003
         TaskField.DELIVERABLE_START, // Displayed as Name by Project 2003
         "[Deliverable]" // Displayed empty by Project 2003
      },

      {
         null,
         null,
         null,
         null,
         null,
         GanttBarStartEndShape.NONE,
         GanttBarStartEndType.SOLID,
         ColorType.BLACK,
         GanttBarMiddleShape.NONE,
         ChartPattern.SOLID,
         ColorType.BLACK,
         GanttBarStartEndShape.NORTHMINIHOMEPLATE,
         GanttBarStartEndType.SOLID,
         ColorType.RED,
         "*Deliverable Finish",
         Integer.valueOf(1),
         TaskField.DELIVERABLE_FINISH, // Displayed as Name by Project 2003
         TaskField.DELIVERABLE_FINISH, // Displayed as Name by Project 2003
         "[Deliverable]" // Displayed empty by Project 2003
      },

      {
         null,
         null,
         null,
         null,
         null,
         GanttBarStartEndShape.NONE,
         GanttBarStartEndType.SOLID,
         ColorType.RED,
         GanttBarMiddleShape.BOTTOMLINE,
         ChartPattern.SOLID,
         ColorType.RED,
         GanttBarStartEndShape.NONE,
         GanttBarStartEndType.SOLID,
         ColorType.RED,
         "*Deliverable Duration",
         Integer.valueOf(1),
         TaskField.DELIVERABLE_START, // Displayed as Name by Project 2003
         TaskField.DELIVERABLE_FINISH, // Displayed as Name by Project 2003
         "[Deliverable]" // Displayed empty by Project 2003
      },

      {
         null,
         null,
         null,
         null,
         null,
         GanttBarStartEndShape.SOUTHMINIHOMEPLATE,
         GanttBarStartEndType.SOLID,
         ColorType.YELLOW,
         GanttBarMiddleShape.NONE,
         ChartPattern.SOLID,
         ColorType.BLACK,
         GanttBarStartEndShape.NONE,
         GanttBarStartEndType.SOLID,
         ColorType.BLACK,
         "*Dependency Start",
         Integer.valueOf(1),
         TaskField.DELIVERABLE_START, // Displayed as Name by Project 2003
         TaskField.DELIVERABLE_START, // Displayed as Name by Project 2003
         "[Dependency]" // Displayed empty by Project 2003
      },

      {
         null,
         null,
         null,
         null,
         null,
         GanttBarStartEndShape.NONE,
         GanttBarStartEndType.SOLID,
         ColorType.BLACK,
         GanttBarMiddleShape.NONE,
         ChartPattern.SOLID,
         ColorType.BLACK,
         GanttBarStartEndShape.SOUTHMINIHOMEPLATE,
         GanttBarStartEndType.SOLID,
         ColorType.YELLOW,
         "*Dependency Finish",
         Integer.valueOf(1),
         TaskField.DELIVERABLE_FINISH, // Displayed as Name by Project 2003
         TaskField.DELIVERABLE_FINISH, // Displayed as Name by Project 2003
         "[Dependency]" // Displayed empty by Project 2003
      },

      {
         null,
         null,
         null,
         null,
         null,
         GanttBarStartEndShape.NONE,
         GanttBarStartEndType.SOLID,
         ColorType.RED,
         GanttBarMiddleShape.TOPLINE,
         ChartPattern.SOLID,
         ColorType.YELLOW,
         GanttBarStartEndShape.NONE,
         GanttBarStartEndType.SOLID,
         ColorType.RED,
         "*Dependency Duration",
         Integer.valueOf(1),
         TaskField.DELIVERABLE_START, // Displayed as Name by Project 2003
         TaskField.DELIVERABLE_FINISH, // Displayed as Name by Project 2003
         "[Dependency]" // Displayed empty by Project 2003
      },

      {
         null,
         null,
         null,
         null,
         null,
         GanttBarStartEndShape.NONE,
         GanttBarStartEndType.SOLID,
         ColorType.BLACK,
         GanttBarMiddleShape.ROUNDED,
         ChartPattern.HORIZONTALSTRIPE,
         ColorType.AUTOMATIC,
         GanttBarStartEndShape.NONE,
         GanttBarStartEndType.SOLID,
         ColorType.BLACK,
         "Test1",
         Integer.valueOf(1),
         TaskField.START,
         TaskField.FINISH,
         "[Finished On Time, Flag1, Flag2, Flag3, Flag4, Flag5, Flag6, Flag7, Flag8, Flag9, Flag10, Rolled Up, Project Summary, Split, Flag11, Flag12]"
      },

      {
         null,
         null,
         null,
         null,
         null,
         GanttBarStartEndShape.NONE,
         GanttBarStartEndType.SOLID,
         ColorType.BLACK,
         GanttBarMiddleShape.ROUNDED,
         ChartPattern.GRID,
         ColorType.BLACK,
         GanttBarStartEndShape.NONE,
         GanttBarStartEndType.SOLID,
         ColorType.BLACK,
         "Test2",
         Integer.valueOf(1),
         TaskField.START,
         TaskField.FINISH,
         "[Normal, Milestone, Summary, Critical, Noncritical, Marked, Finished, In Progress, Not Finished, Not Started, Started Late, Finished Late, Started Early, Finished Early, Started On Time]"
      },

      {
         null,
         null,
         null,
         null,
         null,
         GanttBarStartEndShape.NONE,
         GanttBarStartEndType.SOLID,
         ColorType.BLACK,
         GanttBarMiddleShape.ROUNDED,
         ChartPattern.DOTTED,
         ColorType.RED,
         GanttBarStartEndShape.NONE,
         GanttBarStartEndType.SOLID,
         ColorType.BLACK,
         "Test3",
         Integer.valueOf(1),
         TaskField.START,
         TaskField.FINISH,
         "[External Tasks, Flag13, Flag14, Flag15, Flag16, Flag17, Flag18, Flag19, Flag20, Group By Summary]"
      },

      {
         null,
         null,
         null,
         null,
         null,
         GanttBarStartEndShape.NORTHHOMEPLATE,
         GanttBarStartEndType.FRAMED,
         ColorType.BLACK,
         GanttBarMiddleShape.ROUNDED,
         ChartPattern.TRANSPARENT,
         ColorType.BLACK,
         GanttBarStartEndShape.SOUTHHOMEPLATE,
         GanttBarStartEndType.DASHED,
         ColorType.BLACK,
         "Test4",
         Integer.valueOf(1),
         TaskField.START,
         TaskField.FINISH,
         "[Not Finished On Time, Not Flag1, Not Flag2, Not Flag3, Not Flag4, Not Flag5, Not Flag6, Not Flag7, Not Flag8, Not Flag9, Not Flag10, Not Rolled Up, Not Project Summary, Not Split, Not Flag11, Not Flag12]"
      },

      {
         null,
         null,
         null,
         null,
         null,
         GanttBarStartEndShape.DIAMOND,
         GanttBarStartEndType.SOLID,
         ColorType.BLACK,
         GanttBarMiddleShape.ROUNDED,
         ChartPattern.SOLID,
         ColorType.YELLOW,
         GanttBarStartEndShape.DIAMONDCIRCLED,
         GanttBarStartEndType.SOLID,
         ColorType.BLACK,
         "Test5",
         Integer.valueOf(1),
         TaskField.START,
         TaskField.FINISH,
         "[Critical, Noncritical, Not Finished, Not Started, Not Milestone, Not Summary, Not Marked, Not In Progress, Not Started Late, Not Finished Late, Not Started Early, Not Finished Early, Not Started On Time]"
      },

      {
         null,
         null,
         null,
         null,
         null,
         GanttBarStartEndShape.UPARROW,
         GanttBarStartEndType.SOLID,
         ColorType.BLACK,
         GanttBarMiddleShape.NONE,
         ChartPattern.LIGHTDOTTED,
         ColorType.LIME,
         GanttBarStartEndShape.DOWNARROW,
         GanttBarStartEndType.SOLID,
         ColorType.BLACK,
         "Test6",
         Integer.valueOf(1),
         TaskField.START,
         TaskField.FINISH,
         "[Not Normal, Not External Tasks, Not Flag13, Not Flag14, Not Flag15, Not Flag16, Not Flag17, Not Flag18, Not Flag19, Not Flag20, Not Group By Summary]"
      },

      {
         null,
         null,
         null,
         null,
         null,
         GanttBarStartEndShape.RIGHTARROW,
         GanttBarStartEndType.SOLID,
         ColorType.BLACK,
         GanttBarMiddleShape.TOPTHINROUNDED,
         ChartPattern.LIGHTDOTTED,
         ColorType.AQUA,
         GanttBarStartEndShape.LEFTARROW,
         GanttBarStartEndType.SOLID,
         ColorType.BLACK,
         "Test7",
         Integer.valueOf(1),
         TaskField.START,
         TaskField.FINISH,
         "[Not Normal]"
      },

      {
         null,
         null,
         null,
         null,
         null,
         GanttBarStartEndShape.UPARROWCIRCLED,
         GanttBarStartEndType.SOLID,
         ColorType.BLACK,
         GanttBarMiddleShape.MIDDLETHINROUNDED,
         ChartPattern.DOTTED,
         ColorType.BLUE,
         GanttBarStartEndShape.DOWNARROWCIRCLED,
         GanttBarStartEndType.SOLID,
         ColorType.BLACK,
         "Test8",
         Integer.valueOf(1),
         TaskField.START,
         TaskField.FINISH,
         "[Not Normal]"
      },

      {
         null,
         null,
         null,
         null,
         null,
         GanttBarStartEndShape.UPPOINTER,
         GanttBarStartEndType.SOLID,
         ColorType.FUSCHIA,
         GanttBarMiddleShape.BOTTOMTHINROUNDED,
         ChartPattern.HEAVYDOTTED,
         ColorType.WHITE,
         GanttBarStartEndShape.DOWNPOINTER,
         GanttBarStartEndType.SOLID,
         ColorType.MAROON,
         "Test9",
         Integer.valueOf(1),
         TaskField.START,
         TaskField.FINISH,
         "[Not Normal]"
      },

      {
         null,
         null,
         null,
         null,
         null,
         GanttBarStartEndShape.UPPOINTERCIRCLED,
         GanttBarStartEndType.SOLID,
         ColorType.GREEN,
         GanttBarMiddleShape.TOPLINE,
         ChartPattern.BACKSLASH,
         ColorType.OLIVE,
         GanttBarStartEndShape.DOWNPOINTERCIRCLED,
         GanttBarStartEndType.SOLID,
         ColorType.NAVY,
         "Test10",
         Integer.valueOf(1),
         TaskField.START,
         TaskField.FINISH,
         "[Not Normal]"
      },

      {
         null,
         null,
         null,
         null,
         null,
         GanttBarStartEndShape.SOUTHMINIHOMEPLATE,
         GanttBarStartEndType.SOLID,
         ColorType.PURPLE,
         GanttBarMiddleShape.MIDDLELINE,
         ChartPattern.FORWARDSLASH,
         ColorType.TEAL,
         GanttBarStartEndShape.NORTHMINIHOMEPLATE,
         GanttBarStartEndType.SOLID,
         ColorType.GRAY,
         "Test11",
         Integer.valueOf(1),
         TaskField.START,
         TaskField.FINISH,
         "[Not Normal]"
      },

      {
         null,
         null,
         null,
         null,
         null,
         GanttBarStartEndShape.VERTICALBAR,
         GanttBarStartEndType.SOLID,
         ColorType.BLACK,
         GanttBarMiddleShape.BOTTOMLINE,
         ChartPattern.CHECKERED,
         ColorType.SILVER,
         GanttBarStartEndShape.SQUARE,
         GanttBarStartEndType.SOLID,
         ColorType.BLACK,
         "Test12",
         Integer.valueOf(1),
         TaskField.START,
         TaskField.FINISH,
         "[Not Normal]"
      },

      {
         null,
         null,
         null,
         null,
         null,
         GanttBarStartEndShape.CIRCLE,
         GanttBarStartEndType.SOLID,
         ColorType.BLACK,
         GanttBarMiddleShape.ROUNDED,
         ChartPattern.CHECKERED,
         ColorType.BLACK,
         GanttBarStartEndShape.STAR,
         GanttBarStartEndType.SOLID,
         ColorType.BLACK,
         "Test13",
         Integer.valueOf(1),
         TaskField.START,
         TaskField.FINISH,
         "[Not Normal]"
      },

      {
         TaskField.NAME,
         TaskField.START,
         TaskField.FINISH,
         TaskField.DURATION,
         TaskField.WORK,
         GanttBarStartEndShape.NONE,
         GanttBarStartEndType.SOLID,
         ColorType.BLACK,
         GanttBarMiddleShape.ROUNDED,
         ChartPattern.DOTTED,
         ColorType.BLACK,
         GanttBarStartEndShape.NONE,
         GanttBarStartEndType.SOLID,
         ColorType.BLACK,
         "Test14",
         Integer.valueOf(1),
         TaskField.START,
         TaskField.FINISH,
         "[Not Normal]"
      }
   };

   private static final Object[][] EXCEPTION_STYLES =
   {
      {
         TaskField.NAME,
         TaskField.START,
         TaskField.FINISH,
         TaskField.DURATION,
         TaskField.WORK,
         GanttBarStartEndShape.NORTHHOMEPLATE,
         GanttBarStartEndType.SOLID,
         ColorType.BLACK,
         GanttBarMiddleShape.ROUNDED,
         ChartPattern.SOLID,
         ColorType.BLUE,
         GanttBarStartEndShape.SOUTHHOMEPLATE,
         GanttBarStartEndType.FRAMED,
         ColorType.RED,
         Integer.valueOf(1),
         Integer.valueOf(1)
      },

      {
         TaskField.RESOURCE_NAMES,
         null,
         null,
         null,
         null,
         GanttBarStartEndShape.NONE,
         GanttBarStartEndType.SOLID,
         ColorType.BLACK,
         GanttBarMiddleShape.ROUNDED,
         ChartPattern.SOLID,
         ColorType.BLUE,
         GanttBarStartEndShape.NONE,
         GanttBarStartEndType.SOLID,
         ColorType.BLACK,
         Integer.valueOf(2),
         Integer.valueOf(1)
      },

      {
         null,
         null,
         TaskField.RESOURCE_NAMES,
         null,
         null,
         GanttBarStartEndShape.NONE,
         GanttBarStartEndType.SOLID,
         ColorType.BLACK,
         GanttBarMiddleShape.ROUNDED,
         ChartPattern.SOLID,
         ColorType.BLUE,
         GanttBarStartEndShape.NONE,
         GanttBarStartEndType.SOLID,
         ColorType.BLACK,
         Integer.valueOf(3),
         Integer.valueOf(1)
      },

      {
         null,
         null,
         null,
         TaskField.RESOURCE_NAMES,
         null,
         GanttBarStartEndShape.NONE,
         GanttBarStartEndType.SOLID,
         ColorType.BLACK,
         GanttBarMiddleShape.ROUNDED,
         ChartPattern.SOLID,
         ColorType.BLUE,
         GanttBarStartEndShape.NONE,
         GanttBarStartEndType.SOLID,
         ColorType.BLACK,
         Integer.valueOf(4),
         Integer.valueOf(1)
      },

      {
         null,
         null,
         null,
         null,
         TaskField.RESOURCE_NAMES,
         GanttBarStartEndShape.NONE,
         GanttBarStartEndType.SOLID,
         ColorType.BLACK,
         GanttBarMiddleShape.ROUNDED,
         ChartPattern.SOLID,
         ColorType.BLUE,
         GanttBarStartEndShape.NONE,
         GanttBarStartEndType.SOLID,
         ColorType.BLACK,
         Integer.valueOf(5),
         Integer.valueOf(1)
      },

      {
         null,
         null,
         null,
         null,
         null,
         GanttBarStartEndShape.NONE,
         GanttBarStartEndType.SOLID,
         ColorType.BLACK,
         GanttBarMiddleShape.ROUNDED,
         ChartPattern.SOLID,
         ColorType.BLUE,
         GanttBarStartEndShape.NONE,
         GanttBarStartEndType.SOLID,
         ColorType.BLACK,
         Integer.valueOf(6),
         Integer.valueOf(1)
      },

      {
         TaskField.DATE1,
         TaskField.DATE2,
         TaskField.DATE3,
         TaskField.DATE4,
         TaskField.DATE5,
         GanttBarStartEndShape.RIGHTARROW,
         GanttBarStartEndType.DASHED,
         ColorType.YELLOW,
         GanttBarMiddleShape.MIDDLETHINROUNDED,
         ChartPattern.CHECKERED,
         ColorType.LIME,
         GanttBarStartEndShape.LEFTARROW,
         GanttBarStartEndType.FRAMED,
         ColorType.AQUA,
         Integer.valueOf(7),
         Integer.valueOf(34)
      },

   };

   private static final Object[][] DEFAULT_BAR_STYLES14 =
   {
      {
         null,
         TaskField.RESOURCE_NAMES,
         null,
         null,
         null,
         GanttBarStartEndShape.NONE,
         GanttBarStartEndType.SOLID,
         ColorType.BLACK,
         GanttBarMiddleShape.ROUNDED,
         ChartPattern.SOLID,
         ColorType.BLUE,
         GanttBarStartEndShape.NONE,
         GanttBarStartEndType.SOLID,
         ColorType.BLACK,
         "Task",
         Integer.valueOf(1),
         TaskField.START,
         TaskField.FINISH,
         "[Normal, Active, Not Manually Scheduled]"
      },

      {
         null,
         null,
         null,
         null,
         null,
         GanttBarStartEndShape.NONE,
         GanttBarStartEndType.SOLID,
         ColorType.BLACK,
         GanttBarMiddleShape.BOTTOMLINE,
         ChartPattern.VERTICALSTRIPE,
         ColorType.BLUE,
         GanttBarStartEndShape.NONE,
         GanttBarStartEndType.SOLID,
         ColorType.BLACK,
         "Split",
         Integer.valueOf(1),
         TaskField.START,
         TaskField.FINISH,
         "[Normal, Split, Active, Not Manually Scheduled]"
      },

      {
         null,
         TaskField.FINISH,
         null,
         null,
         null,
         GanttBarStartEndShape.DIAMOND,
         GanttBarStartEndType.SOLID,
         ColorType.BLACK,
         GanttBarMiddleShape.NONE,
         ChartPattern.SOLID,
         ColorType.BLACK,
         GanttBarStartEndShape.NONE,
         GanttBarStartEndType.SOLID,
         ColorType.BLACK,
         "Milestone",
         Integer.valueOf(1),
         TaskField.FINISH,
         TaskField.FINISH,
         "[Milestone, Active, Not Group By Summary, Not Manually Scheduled]"
      },

      {
         null,
         null,
         null,
         null,
         null,
         GanttBarStartEndShape.SOUTHHOMEPLATE,
         GanttBarStartEndType.SOLID,
         ColorType.BLACK,
         GanttBarMiddleShape.TOPTHINROUNDED,
         ChartPattern.SOLID,
         ColorType.BLACK,
         GanttBarStartEndShape.SOUTHHOMEPLATE,
         GanttBarStartEndType.SOLID,
         ColorType.BLACK,
         "Summary",
         Integer.valueOf(1),
         TaskField.START,
         TaskField.FINISH,
         "[Summary, Active, Not Manually Scheduled]"
      },

      {
         null,
         null,
         null,
         null,
         null,
         GanttBarStartEndShape.SOUTHHOMEPLATE,
         GanttBarStartEndType.SOLID,
         ColorType.GRAY,
         GanttBarMiddleShape.TOPTHINROUNDED,
         ChartPattern.SOLID,
         ColorType.GRAY,
         GanttBarStartEndShape.SOUTHHOMEPLATE,
         GanttBarStartEndType.SOLID,
         ColorType.GRAY,
         "Project Summary",
         Integer.valueOf(1),
         TaskField.START,
         TaskField.FINISH,
         "[Project Summary]"
      },

      {
         null,
         null,
         null,
         null,
         null,
         GanttBarStartEndShape.SOUTHHOMEPLATE,
         GanttBarStartEndType.SOLID,
         ColorType.BLACK,
         GanttBarMiddleShape.TOPTHINROUNDED,
         ChartPattern.SOLID,
         ColorType.BLACK,
         GanttBarStartEndShape.SOUTHHOMEPLATE,
         GanttBarStartEndType.SOLID,
         ColorType.BLACK,
         "*Group By Summary",
         Integer.valueOf(1),
         TaskField.START,
         TaskField.FINISH,
         "[Group By Summary]"
      },

      {
         null,
         null,
         null,
         null,
         null,
         GanttBarStartEndShape.NONE,
         GanttBarStartEndType.SOLID,
         ColorType.BLACK,
         GanttBarMiddleShape.ROUNDED,
         ChartPattern.SOLID,
         ColorType.BLUE,
         GanttBarStartEndShape.NONE,
         GanttBarStartEndType.SOLID,
         ColorType.BLACK,
         "*Rolled Up Task",
         Integer.valueOf(1),
         TaskField.START,
         TaskField.FINISH,
         "[Normal, Rolled Up, Not Summary]"
      },

      {
         null,
         null,
         null,
         null,
         null,
         GanttBarStartEndShape.NONE,
         GanttBarStartEndType.SOLID,
         ColorType.BLACK,
         GanttBarMiddleShape.BOTTOMLINE,
         ChartPattern.VERTICALSTRIPE,
         ColorType.BLUE,
         GanttBarStartEndShape.NONE,
         GanttBarStartEndType.SOLID,
         ColorType.BLACK,
         "*Rolled Up Split",
         Integer.valueOf(1),
         TaskField.START,
         TaskField.FINISH,
         "[Normal, Rolled Up, Split, Not Summary]"
      },

      {
         null,
         null,
         null,
         null,
         null,
         GanttBarStartEndShape.NONE,
         GanttBarStartEndType.SOLID,
         ColorType.BLACK,
         GanttBarMiddleShape.MIDDLETHINROUNDED,
         ChartPattern.SOLID,
         ColorType.BLACK,
         GanttBarStartEndShape.NONE,
         GanttBarStartEndType.SOLID,
         ColorType.BLACK,
         "*Rolled Up Progress",
         Integer.valueOf(1),
         TaskField.ACTUAL_START,
         TaskField.COMPLETE_THROUGH,
         "[Normal, Rolled Up, Not Summary]"
      },

      {
         null,
         null,
         null,
         null,
         null,
         GanttBarStartEndShape.DIAMOND,
         GanttBarStartEndType.FRAMED,
         ColorType.BLACK,
         GanttBarMiddleShape.NONE,
         ChartPattern.SOLID,
         ColorType.BLACK,
         GanttBarStartEndShape.NONE,
         GanttBarStartEndType.SOLID,
         ColorType.BLACK,
         "*Rolled Up Milestone",
         Integer.valueOf(1),
         TaskField.FINISH,
         TaskField.FINISH,
         "[Milestone, Rolled Up, Not Summary]"
      },

      {
         null,
         null,
         null,
         null,
         null,
         GanttBarStartEndShape.NONE,
         GanttBarStartEndType.SOLID,
         ColorType.BLACK,
         GanttBarMiddleShape.ROUNDED,
         ChartPattern.SOLID,
         ColorType.GRAY,
         GanttBarStartEndShape.NONE,
         GanttBarStartEndType.SOLID,
         ColorType.BLACK,
         "External Tasks",
         Integer.valueOf(1),
         TaskField.START,
         TaskField.FINISH,
         "[External Tasks, Not Milestone]"
      },

      {
         null,
         TaskField.FINISH,
         null,
         null,
         null,
         GanttBarStartEndShape.DIAMOND,
         GanttBarStartEndType.SOLID,
         ColorType.GRAY,
         GanttBarMiddleShape.NONE,
         ChartPattern.SOLID,
         ColorType.BLACK,
         GanttBarStartEndShape.NONE,
         GanttBarStartEndType.SOLID,
         ColorType.BLACK,
         "External Milestone",
         Integer.valueOf(1),
         TaskField.FINISH,
         TaskField.FINISH,
         "[Milestone, External Tasks]"
      },

      {
         null,
         null,
         null,
         null,
         null,
         GanttBarStartEndShape.NORTHMINIHOMEPLATE,
         GanttBarStartEndType.SOLID,
         ColorType.RED,
         GanttBarMiddleShape.NONE,
         ChartPattern.SOLID,
         ColorType.BLACK,
         GanttBarStartEndShape.NONE,
         GanttBarStartEndType.SOLID,
         ColorType.BLACK,
         "*Deliverable Start",
         Integer.valueOf(1),
         TaskField.DELIVERABLE_START, // Displayed as Name by Project 2003
         TaskField.DELIVERABLE_START, // Displayed as Name by Project 2003
         "[Deliverable]" // Displayed empty by Project 2003
      },

      {
         null,
         null,
         null,
         null,
         null,
         GanttBarStartEndShape.NONE,
         GanttBarStartEndType.SOLID,
         ColorType.BLACK,
         GanttBarMiddleShape.NONE,
         ChartPattern.SOLID,
         ColorType.BLACK,
         GanttBarStartEndShape.NORTHMINIHOMEPLATE,
         GanttBarStartEndType.SOLID,
         ColorType.RED,
         "*Deliverable Finish",
         Integer.valueOf(1),
         TaskField.DELIVERABLE_FINISH, // Displayed as Name by Project 2003
         TaskField.DELIVERABLE_FINISH, // Displayed as Name by Project 2003
         "[Deliverable]" // Displayed empty by Project 2003
      },

      {
         null,
         null,
         null,
         null,
         null,
         GanttBarStartEndShape.NONE,
         GanttBarStartEndType.SOLID,
         ColorType.RED,
         GanttBarMiddleShape.BOTTOMLINE,
         ChartPattern.SOLID,
         ColorType.RED,
         GanttBarStartEndShape.NONE,
         GanttBarStartEndType.SOLID,
         ColorType.RED,
         "*Deliverable Duration",
         Integer.valueOf(1),
         TaskField.DELIVERABLE_START, // Displayed as Name by Project 2003
         TaskField.DELIVERABLE_FINISH, // Displayed as Name by Project 2003
         "[Deliverable]" // Displayed empty by Project 2003
      },

      {
         null,
         null,
         null,
         null,
         null,
         GanttBarStartEndShape.SOUTHMINIHOMEPLATE,
         GanttBarStartEndType.SOLID,
         ColorType.YELLOW,
         GanttBarMiddleShape.NONE,
         ChartPattern.SOLID,
         ColorType.BLACK,
         GanttBarStartEndShape.NONE,
         GanttBarStartEndType.SOLID,
         ColorType.BLACK,
         "*Dependency Start",
         Integer.valueOf(1),
         TaskField.DELIVERABLE_START, // Displayed as Name by Project 2003
         TaskField.DELIVERABLE_START, // Displayed as Name by Project 2003
         "[Dependency]" // Displayed empty by Project 2003
      },

      {
         null,
         null,
         null,
         null,
         null,
         GanttBarStartEndShape.NONE,
         GanttBarStartEndType.SOLID,
         ColorType.BLACK,
         GanttBarMiddleShape.NONE,
         ChartPattern.SOLID,
         ColorType.BLACK,
         GanttBarStartEndShape.SOUTHMINIHOMEPLATE,
         GanttBarStartEndType.SOLID,
         ColorType.YELLOW,
         "*Dependency Finish",
         Integer.valueOf(1),
         TaskField.DELIVERABLE_FINISH, // Displayed as Name by Project 2003
         TaskField.DELIVERABLE_FINISH, // Displayed as Name by Project 2003
         "[Dependency]" // Displayed empty by Project 2003
      },

      {
         null,
         null,
         null,
         null,
         null,
         GanttBarStartEndShape.NONE,
         GanttBarStartEndType.SOLID,
         ColorType.RED,
         GanttBarMiddleShape.TOPLINE,
         ChartPattern.SOLID,
         ColorType.YELLOW,
         GanttBarStartEndShape.NONE,
         GanttBarStartEndType.SOLID,
         ColorType.RED,
         "*Dependency Duration",
         Integer.valueOf(1),
         TaskField.DELIVERABLE_START, // Displayed as Name by Project 2003
         TaskField.DELIVERABLE_FINISH, // Displayed as Name by Project 2003
         "[Dependency]" // Displayed empty by Project 2003
      },

      {
         null,
         null,
         null,
         null,
         null,
         GanttBarStartEndShape.NONE,
         GanttBarStartEndType.SOLID,
         ColorType.BLACK,
         GanttBarMiddleShape.ROUNDED,
         ChartPattern.HORIZONTALSTRIPE,
         ColorType.AUTOMATIC,
         GanttBarStartEndShape.NONE,
         GanttBarStartEndType.SOLID,
         ColorType.BLACK,
         "Test1",
         Integer.valueOf(1),
         TaskField.START,
         TaskField.FINISH,
         "[Finished On Time, Flag1, Flag2, Flag3, Flag4, Flag5, Flag6, Flag7, Flag8, Flag9, Flag10, Rolled Up, Project Summary, Split, Flag11, Flag12]"
      },

      {
         null,
         null,
         null,
         null,
         null,
         GanttBarStartEndShape.NONE,
         GanttBarStartEndType.SOLID,
         ColorType.BLACK,
         GanttBarMiddleShape.ROUNDED,
         ChartPattern.GRID,
         ColorType.BLACK,
         GanttBarStartEndShape.NONE,
         GanttBarStartEndType.SOLID,
         ColorType.BLACK,
         "Test2",
         Integer.valueOf(1),
         TaskField.START,
         TaskField.FINISH,
         "[Normal, Milestone, Summary, Critical, Noncritical, Marked, Finished, In Progress, Not Finished, Not Started, Started Late, Finished Late, Started Early, Finished Early, Started On Time]"
      },

      {
         null,
         null,
         null,
         null,
         null,
         GanttBarStartEndShape.NONE,
         GanttBarStartEndType.SOLID,
         ColorType.BLACK,
         GanttBarMiddleShape.ROUNDED,
         ChartPattern.DOTTED,
         ColorType.RED,
         GanttBarStartEndShape.NONE,
         GanttBarStartEndType.SOLID,
         ColorType.BLACK,
         "Test3",
         Integer.valueOf(1),
         TaskField.START,
         TaskField.FINISH,
         "[External Tasks, Flag13, Flag14, Flag15, Flag16, Flag17, Flag18, Flag19, Flag20, Group By Summary]"
      },

      {
         null,
         null,
         null,
         null,
         null,
         GanttBarStartEndShape.NORTHHOMEPLATE,
         GanttBarStartEndType.FRAMED,
         ColorType.BLACK,
         GanttBarMiddleShape.ROUNDED,
         ChartPattern.TRANSPARENT,
         ColorType.BLACK,
         GanttBarStartEndShape.SOUTHHOMEPLATE,
         GanttBarStartEndType.DASHED,
         ColorType.BLACK,
         "Test4",
         Integer.valueOf(1),
         TaskField.START,
         TaskField.FINISH,
         "[Not Finished On Time, Not Flag1, Not Flag2, Not Flag3, Not Flag4, Not Flag5, Not Flag6, Not Flag7, Not Flag8, Not Flag9, Not Flag10, Not Rolled Up, Not Project Summary, Not Split, Not Flag11, Not Flag12]"
      },

      {
         null,
         null,
         null,
         null,
         null,
         GanttBarStartEndShape.DIAMOND,
         GanttBarStartEndType.SOLID,
         ColorType.BLACK,
         GanttBarMiddleShape.ROUNDED,
         ChartPattern.SOLID,
         ColorType.YELLOW,
         GanttBarStartEndShape.DIAMONDCIRCLED,
         GanttBarStartEndType.SOLID,
         ColorType.BLACK,
         "Test5",
         Integer.valueOf(1),
         TaskField.START,
         TaskField.FINISH,
         "[Critical, Noncritical, Not Finished, Not Started, Not Milestone, Not Summary, Not Marked, Not In Progress, Not Started Late, Not Finished Late, Not Started Early, Not Finished Early, Not Started On Time]"
      },

      {
         null,
         null,
         null,
         null,
         null,
         GanttBarStartEndShape.UPARROW,
         GanttBarStartEndType.SOLID,
         ColorType.BLACK,
         GanttBarMiddleShape.NONE,
         ChartPattern.LIGHTDOTTED,
         ColorType.LIME,
         GanttBarStartEndShape.DOWNARROW,
         GanttBarStartEndType.SOLID,
         ColorType.BLACK,
         "Test6",
         Integer.valueOf(1),
         TaskField.START,
         TaskField.FINISH,
         "[Not Normal, Not External Tasks, Not Flag13, Not Flag14, Not Flag15, Not Flag16, Not Flag17, Not Flag18, Not Flag19, Not Flag20, Not Group By Summary]"
      },

      {
         null,
         null,
         null,
         null,
         null,
         GanttBarStartEndShape.RIGHTARROW,
         GanttBarStartEndType.SOLID,
         ColorType.BLACK,
         GanttBarMiddleShape.TOPTHINROUNDED,
         ChartPattern.LIGHTDOTTED,
         ColorType.AQUA,
         GanttBarStartEndShape.LEFTARROW,
         GanttBarStartEndType.SOLID,
         ColorType.BLACK,
         "Test7",
         Integer.valueOf(1),
         TaskField.START,
         TaskField.FINISH,
         "[Not Normal]"
      },

      {
         null,
         null,
         null,
         null,
         null,
         GanttBarStartEndShape.UPARROWCIRCLED,
         GanttBarStartEndType.SOLID,
         ColorType.BLACK,
         GanttBarMiddleShape.MIDDLETHINROUNDED,
         ChartPattern.DOTTED,
         ColorType.BLUE,
         GanttBarStartEndShape.DOWNARROWCIRCLED,
         GanttBarStartEndType.SOLID,
         ColorType.BLACK,
         "Test8",
         Integer.valueOf(1),
         TaskField.START,
         TaskField.FINISH,
         "[Not Normal]"
      },

      {
         null,
         null,
         null,
         null,
         null,
         GanttBarStartEndShape.UPPOINTER,
         GanttBarStartEndType.SOLID,
         ColorType.FUSCHIA,
         GanttBarMiddleShape.BOTTOMTHINROUNDED,
         ChartPattern.HEAVYDOTTED,
         ColorType.WHITE,
         GanttBarStartEndShape.DOWNPOINTER,
         GanttBarStartEndType.SOLID,
         ColorType.MAROON,
         "Test9",
         Integer.valueOf(1),
         TaskField.START,
         TaskField.FINISH,
         "[Not Normal]"
      },

      {
         null,
         null,
         null,
         null,
         null,
         GanttBarStartEndShape.UPPOINTERCIRCLED,
         GanttBarStartEndType.SOLID,
         ColorType.GREEN,
         GanttBarMiddleShape.TOPLINE,
         ChartPattern.BACKSLASH,
         ColorType.OLIVE,
         GanttBarStartEndShape.DOWNPOINTERCIRCLED,
         GanttBarStartEndType.SOLID,
         ColorType.NAVY,
         "Test10",
         Integer.valueOf(1),
         TaskField.START,
         TaskField.FINISH,
         "[Not Normal]"
      },

      {
         null,
         null,
         null,
         null,
         null,
         GanttBarStartEndShape.SOUTHMINIHOMEPLATE,
         GanttBarStartEndType.SOLID,
         ColorType.PURPLE,
         GanttBarMiddleShape.MIDDLELINE,
         ChartPattern.FORWARDSLASH,
         ColorType.TEAL,
         GanttBarStartEndShape.NORTHMINIHOMEPLATE,
         GanttBarStartEndType.SOLID,
         ColorType.GRAY,
         "Test11",
         Integer.valueOf(1),
         TaskField.START,
         TaskField.FINISH,
         "[Not Normal]"
      },

      {
         null,
         null,
         null,
         null,
         null,
         GanttBarStartEndShape.VERTICALBAR,
         GanttBarStartEndType.SOLID,
         ColorType.BLACK,
         GanttBarMiddleShape.BOTTOMLINE,
         ChartPattern.CHECKERED,
         ColorType.SILVER,
         GanttBarStartEndShape.SQUARE,
         GanttBarStartEndType.SOLID,
         ColorType.BLACK,
         "Test12",
         Integer.valueOf(1),
         TaskField.START,
         TaskField.FINISH,
         "[Not Normal]"
      },

      {
         null,
         null,
         null,
         null,
         null,
         GanttBarStartEndShape.CIRCLE,
         GanttBarStartEndType.SOLID,
         ColorType.BLACK,
         GanttBarMiddleShape.ROUNDED,
         ChartPattern.CHECKERED,
         ColorType.BLACK,
         GanttBarStartEndShape.STAR,
         GanttBarStartEndType.SOLID,
         ColorType.BLACK,
         "Test13",
         Integer.valueOf(1),
         TaskField.START,
         TaskField.FINISH,
         "[Not Normal]"
      },

      {
         TaskField.NAME,
         TaskField.START,
         TaskField.FINISH,
         TaskField.DURATION,
         TaskField.WORK,
         GanttBarStartEndShape.NONE,
         GanttBarStartEndType.SOLID,
         ColorType.BLACK,
         GanttBarMiddleShape.ROUNDED,
         ChartPattern.DOTTED,
         ColorType.BLACK,
         GanttBarStartEndShape.NONE,
         GanttBarStartEndType.SOLID,
         ColorType.BLACK,
         "Test14",
         Integer.valueOf(1),
         TaskField.START,
         TaskField.FINISH,
         "[Not Normal]"
      },

      {
         null,
         TaskField.RESOURCE_NAMES,
         null,
         null,
         null,
         GanttBarStartEndShape.NONE,
         GanttBarStartEndType.SOLID,
         ColorType.BLACK,
         GanttBarMiddleShape.ROUNDED,
         ChartPattern.TRANSPARENT,
         new Color(127, 127, 127),
         GanttBarStartEndShape.NONE,
         GanttBarStartEndType.SOLID,
         ColorType.BLACK,
         "Inactive Task",
         Integer.valueOf(1),
         TaskField.START,
         TaskField.FINISH,
         "[Normal, Not Active, Not Placeholder]"
      },

      {
         null,
         null,
         null,
         null,
         null,
         GanttBarStartEndShape.NONE,
         GanttBarStartEndType.SOLID,
         ColorType.BLACK,
         GanttBarMiddleShape.BOTTOMLINE,
         ChartPattern.VERTICALSTRIPE,
         new Color(127, 127, 127),
         GanttBarStartEndShape.NONE,
         GanttBarStartEndType.SOLID,
         ColorType.BLACK,
         "*Inactive Split",
         Integer.valueOf(1),
         TaskField.START,
         TaskField.FINISH,
         "[Normal, Split, Not Active, Not Placeholder]"
      },

      {
         null,
         TaskField.FINISH,
         null,
         null,
         null,
         GanttBarStartEndShape.DIAMOND,
         GanttBarStartEndType.DASHED,
         new Color(127, 127, 127),
         GanttBarMiddleShape.NONE,
         ChartPattern.SOLID,
         ColorType.BLACK,
         GanttBarStartEndShape.NONE,
         GanttBarStartEndType.SOLID,
         ColorType.BLACK,
         "Inactive Milestone",
         Integer.valueOf(1),
         TaskField.FINISH,
         TaskField.FINISH,
         "[Milestone, Not Active, Not Placeholder]"
      },

      {
         null,
         null,
         null,
         null,
         null,
         GanttBarStartEndShape.SOUTHHOMEPLATE,
         GanttBarStartEndType.FRAMED,
         new Color(127, 127, 127),
         GanttBarMiddleShape.TOPTHINROUNDED,
         ChartPattern.TRANSPARENT,
         new Color(127, 127, 127),
         GanttBarStartEndShape.SOUTHHOMEPLATE,
         GanttBarStartEndType.FRAMED,
         new Color(127, 127, 127),
         "Inactive Summary",
         Integer.valueOf(1),
         TaskField.START,
         TaskField.FINISH,
         "[Summary, Not Active, Not Placeholder]"
      },

      {
         null,
         TaskField.RESOURCE_NAMES,
         null,
         null,
         null,
         GanttBarStartEndShape.LEFTBRACKET,
         GanttBarStartEndType.SOLID,
         ColorType.BLACK,
         GanttBarMiddleShape.ROUNDED,
         ChartPattern.SOLID,
         new Color(19, 154, 161),
         GanttBarStartEndShape.RIGHTBRACKET,
         GanttBarStartEndType.SOLID,
         ColorType.BLACK,
         "Manual Task",
         Integer.valueOf(1),
         TaskField.START,
         TaskField.FINISH,
         "[Normal, Active, Manually Scheduled, Not Warning, Not Placeholder]"
      },

      {
         null,
         null,
         null,
         null,
         null,
         GanttBarStartEndShape.NONE,
         GanttBarStartEndType.SOLID,
         ColorType.BLACK,
         GanttBarMiddleShape.BOTTOMLINE,
         ChartPattern.VERTICALSTRIPE,
         new Color(19, 154, 161),
         GanttBarStartEndShape.NONE,
         GanttBarStartEndType.SOLID,
         ColorType.BLACK,
         "*Manual Split",
         Integer.valueOf(1),
         TaskField.START,
         TaskField.FINISH,
         "[Normal, Split, Active, Manually Scheduled, Not Placeholder]"
      },

      {
         null,
         TaskField.FINISH,
         null,
         null,
         null,
         GanttBarStartEndShape.DIAMOND,
         GanttBarStartEndType.SOLID,
         new Color(19, 154, 162),
         GanttBarMiddleShape.NONE,
         ChartPattern.SOLID,
         ColorType.BLACK,
         GanttBarStartEndShape.NONE,
         GanttBarStartEndType.SOLID,
         ColorType.BLACK,
         "*Manual Milestone",
         Integer.valueOf(1),
         TaskField.FINISH,
         TaskField.FINISH,
         "[Milestone, Active, Manually Scheduled, Not Placeholder]"
      },

      {
         null,
         TaskField.RESOURCE_NAMES,
         null,
         null,
         null,
         GanttBarStartEndShape.LEFTGRADIENT,
         GanttBarStartEndType.SOLID,
         ColorType.WHITE,
         GanttBarMiddleShape.ROUNDED,
         ChartPattern.SOLID,
         new Color(185, 220, 230),
         GanttBarStartEndShape.RIGHTGRADIENT,
         GanttBarStartEndType.SOLID,
         ColorType.WHITE,
         "Duration-only",
         Integer.valueOf(1),
         TaskField.START,
         TaskField.FINISH,
         "[Active, Manually Scheduled, Placeholder (Duration), Not Milestone]"
      },

      {
         null,
         null,
         null,
         null,
         null,
         GanttBarStartEndShape.NONE,
         GanttBarStartEndType.SOLID,
         ColorType.BLACK,
         GanttBarMiddleShape.BOTTOMTHINROUNDED,
         ChartPattern.SOLID,
         new Color(69, 98, 135),
         GanttBarStartEndShape.NONE,
         GanttBarStartEndType.SOLID,
         ColorType.BLACK,
         "Manual Summary Rollup",
         Integer.valueOf(1),
         TaskField.SCHEDULED_START,
         TaskField.SCHEDULED_FINISH,
         "[Summary, Active, Manually Scheduled]"
      },

      {
         null,
         TaskField.RESOURCE_NAMES,
         null,
         null,
         null,
         GanttBarStartEndShape.LEFTBRACKET,
         GanttBarStartEndType.SOLID,
         ColorType.BLACK,
         GanttBarMiddleShape.ROUNDED,
         ChartPattern.SOLIDHAIRY,
         new Color(19, 154, 161),
         GanttBarStartEndShape.RIGHTBRACKET,
         GanttBarStartEndType.SOLID,
         ColorType.BLACK,
         "*Manual Task (Warning)",
         Integer.valueOf(1),
         TaskField.START,
         TaskField.FINISH,
         "[Normal, Active, Manually Scheduled, Warning, Not Placeholder]"
      },

      {
         null,
         null,
         null,
         null,
         null,
         GanttBarStartEndShape.NONE,
         GanttBarStartEndType.SOLID,
         ColorType.BLACK,
         GanttBarMiddleShape.BOTTOMLINE,
         ChartPattern.VERTICALSTRIPE,
         new Color(19, 154, 161),
         GanttBarStartEndShape.NONE,
         GanttBarStartEndType.SOLID,
         ColorType.BLACK,
         "*Manual Split (Warning)",
         Integer.valueOf(1),
         TaskField.START,
         TaskField.FINISH,
         "[Normal, Split, Active, Manually Scheduled, Warning, Not Placeholder]"
      },

      {
         null,
         TaskField.FINISH,
         null,
         null,
         null,
         GanttBarStartEndShape.DIAMOND,
         GanttBarStartEndType.SOLID,
         new Color(19, 154, 162),
         GanttBarMiddleShape.NONE,
         ChartPattern.SOLID,
         ColorType.BLACK,
         GanttBarStartEndShape.NONE,
         GanttBarStartEndType.SOLID,
         ColorType.BLACK,
         "*Manual Milestone (Warning)",
         Integer.valueOf(1),
         TaskField.FINISH,
         TaskField.FINISH,
         "[Milestone, Active, Manually Scheduled, Warning, Not Placeholder]"
      },

      {
         null,
         null,
         null,
         null,
         null,
         GanttBarStartEndShape.NONE,
         GanttBarStartEndType.SOLID,
         ColorType.BLACK,
         GanttBarMiddleShape.BOTTOMTHINROUNDED,
         ChartPattern.SOLID,
         new Color(74, 18, 18),
         GanttBarStartEndShape.NONE,
         GanttBarStartEndType.SOLID,
         ColorType.BLACK,
         "*Manual Summary Rollup (Warning)",
         Integer.valueOf(1),
         TaskField.SCHEDULED_START,
         TaskField.SCHEDULED_FINISH,
         "[Summary, Active, Manually Scheduled, Warning]"
      },

      {
         null,
         null,
         null,
         null,
         null,
         GanttBarStartEndShape.SOUTHHOMEPLATE,
         GanttBarStartEndType.SOLID,
         ColorType.BLACK,
         GanttBarMiddleShape.TOPTHINROUNDED,
         ChartPattern.SOLID,
         ColorType.BLACK,
         GanttBarStartEndShape.SOUTHHOMEPLATE,
         GanttBarStartEndType.SOLID,
         ColorType.BLACK,
         "Manual Summary",
         Integer.valueOf(1),
         TaskField.START,
         TaskField.FINISH,
         "[Summary, Active, Manually Scheduled, Not Placeholder]"
      },

      {
         null,
         TaskField.RESOURCE_NAMES,
         null,
         null,
         null,
         GanttBarStartEndShape.LEFTBRACKET,
         GanttBarStartEndType.SOLID,
         ColorType.BLACK,
         GanttBarMiddleShape.NONE,
         ChartPattern.SOLID,
         ColorType.BLACK,
         GanttBarStartEndShape.NONE,
         GanttBarStartEndType.SOLID,
         ColorType.BLACK,
         "Start-only",
         Integer.valueOf(1),
         TaskField.START,
         TaskField.START,
         "[Manually Scheduled, Placeholder (Start), Not Milestone]"
      },

      {
         null,
         TaskField.RESOURCE_NAMES,
         null,
         null,
         null,
         GanttBarStartEndShape.NONE,
         GanttBarStartEndType.SOLID,
         ColorType.BLACK,
         GanttBarMiddleShape.NONE,
         ChartPattern.SOLID,
         ColorType.BLACK,
         GanttBarStartEndShape.RIGHTBRACKET,
         GanttBarStartEndType.SOLID,
         ColorType.BLACK,
         "Finish-only",
         Integer.valueOf(1),
         TaskField.FINISH,
         TaskField.FINISH,
         "[Manually Scheduled, Placeholder (Finish), Not Milestone]"
      },

      {
         null,
         TaskField.FINISH,
         null,
         null,
         null,
         GanttBarStartEndShape.DIAMOND,
         GanttBarStartEndType.SOLID,
         new Color(206, 217, 232),
         GanttBarMiddleShape.NONE,
         ChartPattern.SOLID,
         ColorType.BLACK,
         GanttBarStartEndShape.NONE,
         GanttBarStartEndType.SOLID,
         ColorType.BLACK,
         "*Duration-only Milestone",
         Integer.valueOf(1),
         TaskField.FINISH,
         TaskField.FINISH,
         "[Milestone, Active, Manually Scheduled, Placeholder (Duration)]"
      },

      {
         null,
         TaskField.START,
         null,
         null,
         null,
         GanttBarStartEndShape.DIAMOND,
         GanttBarStartEndType.SOLID,
         new Color(206, 217, 232),
         GanttBarMiddleShape.NONE,
         ChartPattern.SOLID,
         ColorType.BLACK,
         GanttBarStartEndShape.NONE,
         GanttBarStartEndType.SOLID,
         ColorType.BLACK,
         "*Start-only Milestone",
         Integer.valueOf(1),
         TaskField.START,
         TaskField.START,
         "[Milestone, Manually Scheduled, Placeholder (Start)]"
      },

      {
         null,
         TaskField.FINISH,
         null,
         null,
         null,
         GanttBarStartEndShape.DIAMOND,
         GanttBarStartEndType.SOLID,
         new Color(206, 217, 232),
         GanttBarMiddleShape.NONE,
         ChartPattern.SOLID,
         ColorType.BLACK,
         GanttBarStartEndShape.NONE,
         GanttBarStartEndType.SOLID,
         ColorType.BLACK,
         "*Finish-only Milestone",
         Integer.valueOf(1),
         TaskField.FINISH,
         TaskField.FINISH,
         "[Milestone, Manually Scheduled, Placeholder (Finish)]"
      },

      {
         null,
         null,
         null,
         null,
         null,
         GanttBarStartEndShape.NONE,
         GanttBarStartEndType.SOLID,
         ColorType.BLACK,
         GanttBarMiddleShape.ROUNDED,
         ChartPattern.SOLID,
         new Color(19, 154, 161),
         GanttBarStartEndShape.NONE,
         GanttBarStartEndType.SOLID,
         ColorType.BLACK,
         "*Rolled Up Manual Task",
         Integer.valueOf(1),
         TaskField.START,
         TaskField.FINISH,
         "[Normal, Rolled Up, Active, Manually Scheduled, Not Summary, Not Placeholder]"
      },

      {
         null,
         null,
         null,
         null,
         null,
         GanttBarStartEndShape.NONE,
         GanttBarStartEndType.SOLID,
         ColorType.BLACK,
         GanttBarMiddleShape.MIDDLETHINROUNDED,
         ChartPattern.SOLID,
         ColorType.BLACK,
         GanttBarStartEndShape.NONE,
         GanttBarStartEndType.SOLID,
         ColorType.BLACK,
         "Progress",
         Integer.valueOf(1),
         TaskField.ACTUAL_START,
         TaskField.COMPLETE_THROUGH,
         "[Normal]"
      },

      {
         null,
         null,
         null,
         null,
         null,
         GanttBarStartEndShape.DOWNPOINTER,
         GanttBarStartEndType.FRAMED,
         ColorType.GREEN,
         GanttBarMiddleShape.NONE,
         ChartPattern.SOLID,
         ColorType.BLACK,
         GanttBarStartEndShape.NONE,
         GanttBarStartEndType.SOLID,
         ColorType.BLACK,
         "Deadline",
         Integer.valueOf(1),
         TaskField.DEADLINE,
         TaskField.DEADLINE,
         "[]"
      }
            /*
            {
               null,
               TaskField.RESOURCE_NAMES,
               null,
               null,
               null,
               GanttBarStartEndShape.NONE,
               GanttBarStartEndType.SOLID,
               ColorType.BLACK,
               GanttBarMiddleShape.ROUNDED,
               GanttBarMiddlePattern.SOLID,
               ColorType.BLUE,
               GanttBarStartEndShape.NONE,
               GanttBarStartEndType.SOLID,
               ColorType.BLACK,
               "Task",
               Integer.valueOf(1),
               TaskField.START,
               TaskField.FINISH,
               "[Normal, Active, Not Manually Scheduled]"
            },
            */
   };

   private static final Object[][] DEFAULT_BAR_STYLES_FROM14 =
   {
      {
         null,
         TaskField.RESOURCE_NAMES,
         null,
         null,
         null,
         GanttBarStartEndShape.NONE,
         GanttBarStartEndType.SOLID,
         ColorType.BLACK,
         GanttBarMiddleShape.ROUNDED,
         ChartPattern.SOLID,
         ColorType.BLUE,
         GanttBarStartEndShape.NONE,
         GanttBarStartEndType.SOLID,
         ColorType.BLACK,
         "Task",
         Integer.valueOf(1),
         TaskField.START,
         TaskField.FINISH,
         "[Normal]"
      },

      {
         null,
         null,
         null,
         null,
         null,
         GanttBarStartEndShape.NONE,
         GanttBarStartEndType.SOLID,
         ColorType.BLACK,
         GanttBarMiddleShape.BOTTOMLINE,
         ChartPattern.VERTICALSTRIPE,
         ColorType.BLUE,
         GanttBarStartEndShape.NONE,
         GanttBarStartEndType.SOLID,
         ColorType.BLACK,
         "Split",
         Integer.valueOf(1),
         TaskField.START,
         TaskField.FINISH,
         "[Normal, Split]"
      },

      {
         null,
         TaskField.FINISH,
         null,
         null,
         null,
         GanttBarStartEndShape.DIAMOND,
         GanttBarStartEndType.SOLID,
         ColorType.BLACK,
         GanttBarMiddleShape.NONE,
         ChartPattern.SOLID,
         ColorType.BLACK,
         GanttBarStartEndShape.NONE,
         GanttBarStartEndType.SOLID,
         ColorType.BLACK,
         "Milestone",
         Integer.valueOf(1),
         TaskField.FINISH,
         TaskField.FINISH,
         "[Milestone, Not Group By Summary]"
      },

      {
         null,
         null,
         null,
         null,
         null,
         GanttBarStartEndShape.SOUTHHOMEPLATE,
         GanttBarStartEndType.SOLID,
         ColorType.BLACK,
         GanttBarMiddleShape.TOPTHINROUNDED,
         ChartPattern.SOLID,
         ColorType.BLACK,
         GanttBarStartEndShape.SOUTHHOMEPLATE,
         GanttBarStartEndType.SOLID,
         ColorType.BLACK,
         "Summary",
         Integer.valueOf(1),
         TaskField.START,
         TaskField.FINISH,
         "[Summary]"
      },

      {
         null,
         null,
         null,
         null,
         null,
         GanttBarStartEndShape.SOUTHHOMEPLATE,
         GanttBarStartEndType.SOLID,
         ColorType.GRAY,
         GanttBarMiddleShape.TOPTHINROUNDED,
         ChartPattern.SOLID,
         ColorType.GRAY,
         GanttBarStartEndShape.SOUTHHOMEPLATE,
         GanttBarStartEndType.SOLID,
         ColorType.GRAY,
         "Project Summary",
         Integer.valueOf(1),
         TaskField.START,
         TaskField.FINISH,
         "[Project Summary]"
      },

      {
         null,
         null,
         null,
         null,
         null,
         GanttBarStartEndShape.SOUTHHOMEPLATE,
         GanttBarStartEndType.SOLID,
         ColorType.BLACK,
         GanttBarMiddleShape.TOPTHINROUNDED,
         ChartPattern.SOLID,
         ColorType.BLACK,
         GanttBarStartEndShape.SOUTHHOMEPLATE,
         GanttBarStartEndType.SOLID,
         ColorType.BLACK,
         "*Group By Summary",
         Integer.valueOf(1),
         TaskField.START,
         TaskField.FINISH,
         "[Group By Summary]"
      },

      {
         null,
         null,
         null,
         null,
         null,
         GanttBarStartEndShape.NONE,
         GanttBarStartEndType.SOLID,
         ColorType.BLACK,
         GanttBarMiddleShape.ROUNDED,
         ChartPattern.SOLID,
         ColorType.BLUE,
         GanttBarStartEndShape.NONE,
         GanttBarStartEndType.SOLID,
         ColorType.BLACK,
         "*Rolled Up Task",
         Integer.valueOf(1),
         TaskField.START,
         TaskField.FINISH,
         "[Normal, Rolled Up, Not Summary]"
      },

      {
         null,
         null,
         null,
         null,
         null,
         GanttBarStartEndShape.NONE,
         GanttBarStartEndType.SOLID,
         ColorType.BLACK,
         GanttBarMiddleShape.BOTTOMLINE,
         ChartPattern.VERTICALSTRIPE,
         ColorType.BLUE,
         GanttBarStartEndShape.NONE,
         GanttBarStartEndType.SOLID,
         ColorType.BLACK,
         "*Rolled Up Split",
         Integer.valueOf(1),
         TaskField.START,
         TaskField.FINISH,
         "[Normal, Rolled Up, Split, Not Summary]"
      },

      {
         null,
         null,
         null,
         null,
         null,
         GanttBarStartEndShape.NONE,
         GanttBarStartEndType.SOLID,
         ColorType.BLACK,
         GanttBarMiddleShape.MIDDLETHINROUNDED,
         ChartPattern.SOLID,
         ColorType.BLACK,
         GanttBarStartEndShape.NONE,
         GanttBarStartEndType.SOLID,
         ColorType.BLACK,
         "*Rolled Up Progress",
         Integer.valueOf(1),
         TaskField.ACTUAL_START,
         TaskField.COMPLETE_THROUGH,
         "[Normal, Rolled Up, Not Summary]"
      },

      {
         null,
         null,
         null,
         null,
         null,
         GanttBarStartEndShape.DIAMOND,
         GanttBarStartEndType.FRAMED,
         ColorType.BLACK,
         GanttBarMiddleShape.NONE,
         ChartPattern.SOLID,
         ColorType.BLACK,
         GanttBarStartEndShape.NONE,
         GanttBarStartEndType.SOLID,
         ColorType.BLACK,
         "*Rolled Up Milestone",
         Integer.valueOf(1),
         TaskField.FINISH,
         TaskField.FINISH,
         "[Milestone, Rolled Up, Not Summary]"
      },

      {
         null,
         null,
         null,
         null,
         null,
         GanttBarStartEndShape.NONE,
         GanttBarStartEndType.SOLID,
         ColorType.BLACK,
         GanttBarMiddleShape.ROUNDED,
         ChartPattern.SOLID,
         ColorType.GRAY,
         GanttBarStartEndShape.NONE,
         GanttBarStartEndType.SOLID,
         ColorType.BLACK,
         "External Tasks",
         Integer.valueOf(1),
         TaskField.START,
         TaskField.FINISH,
         "[External Tasks, Not Milestone]"
      },

      {
         null,
         TaskField.FINISH,
         null,
         null,
         null,
         GanttBarStartEndShape.DIAMOND,
         GanttBarStartEndType.SOLID,
         ColorType.GRAY,
         GanttBarMiddleShape.NONE,
         ChartPattern.SOLID,
         ColorType.BLACK,
         GanttBarStartEndShape.NONE,
         GanttBarStartEndType.SOLID,
         ColorType.BLACK,
         "External Milestone",
         Integer.valueOf(1),
         TaskField.FINISH,
         TaskField.FINISH,
         "[Milestone, External Tasks]"
      },
      {
         null,
         null,
         null,
         null,
         null,
         GanttBarStartEndShape.NORTHMINIHOMEPLATE,
         GanttBarStartEndType.SOLID,
         ColorType.RED,
         GanttBarMiddleShape.NONE,
         ChartPattern.SOLID,
         ColorType.BLACK,
         GanttBarStartEndShape.NONE,
         GanttBarStartEndType.SOLID,
         ColorType.BLACK,
         "*Deliverable Start",
         Integer.valueOf(1),
         TaskField.DELIVERABLE_START, // Displayed as Name by Project 2003
         TaskField.DELIVERABLE_START, // Displayed as Name by Project 2003
         "[Deliverable]" // Displayed empty by Project 2003
      },

      {
         null,
         null,
         null,
         null,
         null,
         GanttBarStartEndShape.NONE,
         GanttBarStartEndType.SOLID,
         ColorType.BLACK,
         GanttBarMiddleShape.NONE,
         ChartPattern.SOLID,
         ColorType.BLACK,
         GanttBarStartEndShape.NORTHMINIHOMEPLATE,
         GanttBarStartEndType.SOLID,
         ColorType.RED,
         "*Deliverable Finish",
         Integer.valueOf(1),
         TaskField.DELIVERABLE_FINISH, // Displayed as Name by Project 2003
         TaskField.DELIVERABLE_FINISH, // Displayed as Name by Project 2003
         "[Deliverable]" // Displayed empty by Project 2003
      },

      {
         null,
         null,
         null,
         null,
         null,
         GanttBarStartEndShape.NONE,
         GanttBarStartEndType.SOLID,
         ColorType.RED,
         GanttBarMiddleShape.BOTTOMLINE,
         ChartPattern.SOLID,
         ColorType.RED,
         GanttBarStartEndShape.NONE,
         GanttBarStartEndType.SOLID,
         ColorType.RED,
         "*Deliverable Duration",
         Integer.valueOf(1),
         TaskField.DELIVERABLE_START, // Displayed as Name by Project 2003
         TaskField.DELIVERABLE_FINISH, // Displayed as Name by Project 2003
         "[Deliverable]" // Displayed empty by Project 2003
      },

      {
         null,
         null,
         null,
         null,
         null,
         GanttBarStartEndShape.SOUTHMINIHOMEPLATE,
         GanttBarStartEndType.SOLID,
         ColorType.YELLOW,
         GanttBarMiddleShape.NONE,
         ChartPattern.SOLID,
         ColorType.BLACK,
         GanttBarStartEndShape.NONE,
         GanttBarStartEndType.SOLID,
         ColorType.BLACK,
         "*Dependency Start",
         Integer.valueOf(1),
         TaskField.DELIVERABLE_START, // Displayed as Name by Project 2003
         TaskField.DELIVERABLE_START, // Displayed as Name by Project 2003
         "[Dependency]" // Displayed empty by Project 2003
      },

      {
         null,
         null,
         null,
         null,
         null,
         GanttBarStartEndShape.NONE,
         GanttBarStartEndType.SOLID,
         ColorType.BLACK,
         GanttBarMiddleShape.NONE,
         ChartPattern.SOLID,
         ColorType.BLACK,
         GanttBarStartEndShape.SOUTHMINIHOMEPLATE,
         GanttBarStartEndType.SOLID,
         ColorType.YELLOW,
         "*Dependency Finish",
         Integer.valueOf(1),
         TaskField.DELIVERABLE_FINISH, // Displayed as Name by Project 2003
         TaskField.DELIVERABLE_FINISH, // Displayed as Name by Project 2003
         "[Dependency]" // Displayed empty by Project 2003
      },

      {
         null,
         null,
         null,
         null,
         null,
         GanttBarStartEndShape.NONE,
         GanttBarStartEndType.SOLID,
         ColorType.RED,
         GanttBarMiddleShape.TOPLINE,
         ChartPattern.SOLID,
         ColorType.YELLOW,
         GanttBarStartEndShape.NONE,
         GanttBarStartEndType.SOLID,
         ColorType.RED,
         "*Dependency Duration",
         Integer.valueOf(1),
         TaskField.DELIVERABLE_START, // Displayed as Name by Project 2003
         TaskField.DELIVERABLE_FINISH, // Displayed as Name by Project 2003
         "[Dependency]" // Displayed empty by Project 2003
      },

      {
         null,
         null,
         null,
         null,
         null,
         GanttBarStartEndShape.NONE,
         GanttBarStartEndType.SOLID,
         ColorType.BLACK,
         GanttBarMiddleShape.ROUNDED,
         ChartPattern.HORIZONTALSTRIPE,
         ColorType.AUTOMATIC,
         GanttBarStartEndShape.NONE,
         GanttBarStartEndType.SOLID,
         ColorType.BLACK,
         "Test1",
         Integer.valueOf(1),
         TaskField.START,
         TaskField.FINISH,
         "[Finished On Time, Flag1, Flag2, Flag3, Flag4, Flag5, Flag6, Flag7, Flag8, Flag9, Flag10, Rolled Up, Project Summary, Split, Flag11, Flag12]"
      },

      {
         null,
         null,
         null,
         null,
         null,
         GanttBarStartEndShape.NONE,
         GanttBarStartEndType.SOLID,
         ColorType.BLACK,
         GanttBarMiddleShape.ROUNDED,
         ChartPattern.GRID,
         ColorType.BLACK,
         GanttBarStartEndShape.NONE,
         GanttBarStartEndType.SOLID,
         ColorType.BLACK,
         "Test2",
         Integer.valueOf(1),
         TaskField.START,
         TaskField.FINISH,
         "[Normal, Milestone, Summary, Critical, Noncritical, Marked, Finished, In Progress, Not Finished, Not Started, Started Late, Finished Late, Started Early, Finished Early, Started On Time]"
      },

      {
         null,
         null,
         null,
         null,
         null,
         GanttBarStartEndShape.NONE,
         GanttBarStartEndType.SOLID,
         ColorType.BLACK,
         GanttBarMiddleShape.ROUNDED,
         ChartPattern.DOTTED,
         ColorType.RED,
         GanttBarStartEndShape.NONE,
         GanttBarStartEndType.SOLID,
         ColorType.BLACK,
         "Test3",
         Integer.valueOf(1),
         TaskField.START,
         TaskField.FINISH,
         "[External Tasks, Flag13, Flag14, Flag15, Flag16, Flag17, Flag18, Flag19, Flag20, Group By Summary]"
      },

      {
         null,
         null,
         null,
         null,
         null,
         GanttBarStartEndShape.NORTHHOMEPLATE,
         GanttBarStartEndType.FRAMED,
         ColorType.BLACK,
         GanttBarMiddleShape.ROUNDED,
         ChartPattern.TRANSPARENT,
         ColorType.BLACK,
         GanttBarStartEndShape.SOUTHHOMEPLATE,
         GanttBarStartEndType.DASHED,
         ColorType.BLACK,
         "Test4",
         Integer.valueOf(1),
         TaskField.START,
         TaskField.FINISH,
         "[Not Finished On Time, Not Flag1, Not Flag2, Not Flag3, Not Flag4, Not Flag5, Not Flag6, Not Flag7, Not Flag8, Not Flag9, Not Flag10, Not Rolled Up, Not Project Summary, Not Split, Not Flag11, Not Flag12]"
      },

      {
         null,
         null,
         null,
         null,
         null,
         GanttBarStartEndShape.DIAMOND,
         GanttBarStartEndType.SOLID,
         ColorType.BLACK,
         GanttBarMiddleShape.ROUNDED,
         ChartPattern.SOLID,
         ColorType.YELLOW,
         GanttBarStartEndShape.DIAMONDCIRCLED,
         GanttBarStartEndType.SOLID,
         ColorType.BLACK,
         "Test5",
         Integer.valueOf(1),
         TaskField.START,
         TaskField.FINISH,
         "[Critical, Noncritical, Not Finished, Not Started, Not Milestone, Not Summary, Not Marked, Not In Progress, Not Started Late, Not Finished Late, Not Started Early, Not Finished Early, Not Started On Time]"
      },

      {
         null,
         null,
         null,
         null,
         null,
         GanttBarStartEndShape.UPARROW,
         GanttBarStartEndType.SOLID,
         ColorType.BLACK,
         GanttBarMiddleShape.NONE,
         ChartPattern.LIGHTDOTTED,
         ColorType.LIME,
         GanttBarStartEndShape.DOWNARROW,
         GanttBarStartEndType.SOLID,
         ColorType.BLACK,
         "Test6",
         Integer.valueOf(1),
         TaskField.START,
         TaskField.FINISH,
         "[Not Normal, Not External Tasks, Not Flag13, Not Flag14, Not Flag15, Not Flag16, Not Flag17, Not Flag18, Not Flag19, Not Flag20, Not Group By Summary]"
      },

      {
         null,
         null,
         null,
         null,
         null,
         GanttBarStartEndShape.RIGHTARROW,
         GanttBarStartEndType.SOLID,
         ColorType.BLACK,
         GanttBarMiddleShape.TOPTHINROUNDED,
         ChartPattern.LIGHTDOTTED,
         ColorType.AQUA,
         GanttBarStartEndShape.LEFTARROW,
         GanttBarStartEndType.SOLID,
         ColorType.BLACK,
         "Test7",
         Integer.valueOf(1),
         TaskField.START,
         TaskField.FINISH,
         "[Not Normal]"
      },

      {
         null,
         null,
         null,
         null,
         null,
         GanttBarStartEndShape.UPARROWCIRCLED,
         GanttBarStartEndType.SOLID,
         ColorType.BLACK,
         GanttBarMiddleShape.MIDDLETHINROUNDED,
         ChartPattern.DOTTED,
         ColorType.BLUE,
         GanttBarStartEndShape.DOWNARROWCIRCLED,
         GanttBarStartEndType.SOLID,
         ColorType.BLACK,
         "Test8",
         Integer.valueOf(1),
         TaskField.START,
         TaskField.FINISH,
         "[Not Normal]"
      },

      {
         null,
         null,
         null,
         null,
         null,
         GanttBarStartEndShape.UPPOINTER,
         GanttBarStartEndType.SOLID,
         ColorType.FUSCHIA,
         GanttBarMiddleShape.BOTTOMTHINROUNDED,
         ChartPattern.HEAVYDOTTED,
         ColorType.WHITE,
         GanttBarStartEndShape.DOWNPOINTER,
         GanttBarStartEndType.SOLID,
         ColorType.MAROON,
         "Test9",
         Integer.valueOf(1),
         TaskField.START,
         TaskField.FINISH,
         "[Not Normal]"
      },

      {
         null,
         null,
         null,
         null,
         null,
         GanttBarStartEndShape.UPPOINTERCIRCLED,
         GanttBarStartEndType.SOLID,
         ColorType.GREEN,
         GanttBarMiddleShape.TOPLINE,
         ChartPattern.BACKSLASH,
         ColorType.OLIVE,
         GanttBarStartEndShape.DOWNPOINTERCIRCLED,
         GanttBarStartEndType.SOLID,
         ColorType.NAVY,
         "Test10",
         Integer.valueOf(1),
         TaskField.START,
         TaskField.FINISH,
         "[Not Normal]"
      },

      {
         null,
         null,
         null,
         null,
         null,
         GanttBarStartEndShape.SOUTHMINIHOMEPLATE,
         GanttBarStartEndType.SOLID,
         ColorType.PURPLE,
         GanttBarMiddleShape.MIDDLELINE,
         ChartPattern.FORWARDSLASH,
         ColorType.TEAL,
         GanttBarStartEndShape.NORTHMINIHOMEPLATE,
         GanttBarStartEndType.SOLID,
         ColorType.GRAY,
         "Test11",
         Integer.valueOf(1),
         TaskField.START,
         TaskField.FINISH,
         "[Not Normal]"
      },

      {
         null,
         null,
         null,
         null,
         null,
         GanttBarStartEndShape.VERTICALBAR,
         GanttBarStartEndType.SOLID,
         ColorType.BLACK,
         GanttBarMiddleShape.BOTTOMLINE,
         ChartPattern.CHECKERED,
         ColorType.SILVER,
         GanttBarStartEndShape.SQUARE,
         GanttBarStartEndType.SOLID,
         ColorType.BLACK,
         "Test12",
         Integer.valueOf(1),
         TaskField.START,
         TaskField.FINISH,
         "[Not Normal]"
      },

      {
         null,
         null,
         null,
         null,
         null,
         GanttBarStartEndShape.CIRCLE,
         GanttBarStartEndType.SOLID,
         ColorType.BLACK,
         GanttBarMiddleShape.ROUNDED,
         ChartPattern.CHECKERED,
         ColorType.BLACK,
         GanttBarStartEndShape.STAR,
         GanttBarStartEndType.SOLID,
         ColorType.BLACK,
         "Test13",
         Integer.valueOf(1),
         TaskField.START,
         TaskField.FINISH,
         "[Not Normal]"
      },

      {
         TaskField.NAME,
         TaskField.START,
         TaskField.FINISH,
         TaskField.DURATION,
         TaskField.WORK,
         GanttBarStartEndShape.NONE,
         GanttBarStartEndType.SOLID,
         ColorType.BLACK,
         GanttBarMiddleShape.ROUNDED,
         ChartPattern.DOTTED,
         ColorType.BLACK,
         GanttBarStartEndShape.NONE,
         GanttBarStartEndType.SOLID,
         ColorType.BLACK,
         "Test14",
         Integer.valueOf(1),
         TaskField.START,
         TaskField.FINISH,
         "[Not Normal]"
      },

      {
         null,
         null,
         null,
         null,
         null,
         GanttBarStartEndShape.NONE,
         GanttBarStartEndType.SOLID,
         ColorType.BLACK,
         GanttBarMiddleShape.MIDDLETHINROUNDED,
         ChartPattern.SOLID,
         ColorType.BLACK,
         GanttBarStartEndShape.NONE,
         GanttBarStartEndType.SOLID,
         ColorType.BLACK,
         "Progress",
         Integer.valueOf(1),
         TaskField.ACTUAL_START,
         TaskField.COMPLETE_THROUGH,
         "[Normal]"
      },

      {
         null,
         null,
         null,
         null,
         null,
         GanttBarStartEndShape.DOWNPOINTER,
         GanttBarStartEndType.FRAMED,
         ColorType.GREEN,
         GanttBarMiddleShape.NONE,
         ChartPattern.SOLID,
         ColorType.BLACK,
         GanttBarStartEndShape.NONE,
         GanttBarStartEndType.SOLID,
         ColorType.BLACK,
         "Deadline",
         Integer.valueOf(1),
         TaskField.DEADLINE,
         TaskField.DEADLINE,
         "[]"
      }

   };

}