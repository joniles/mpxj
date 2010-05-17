/*
 * file:       MppGanttTest.java
 * author:     Jon Iles
 * copyright:  (c) Packwood Software 2010
 * date:       13/05/2010
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

import java.util.List;

import net.sf.mpxj.ProjectFile;
import net.sf.mpxj.View;
import net.sf.mpxj.mpp.ChartPattern;
import net.sf.mpxj.mpp.GanttChartView;
import net.sf.mpxj.mpp.MPPReader;
import net.sf.mpxj.mpp.NonWorkingTimeStyle;

/**
 * Tests to exercise MPP file read functionality for various versions of
 * MPP file.
 */
public class MppGanttTest extends MPXJTestCase
{

   /**
    * Test Gantt chart data read from an MPP9 file.
    * 
    * @throws Exception
    */
   public void testMpp9Gantt() throws Exception
   {
      ProjectFile mpp = new MPPReader().read(m_basedir + "/mpp9gantt.mpp");
      testSummaryData(mpp);
      testFontStyles(mpp);
      testGridlines(mpp);
   }

   /**
    * Test Gantt chart data read from an MPP12 file.
    * 
    * @throws Exception
    */
   public void testMpp12Gantt() throws Exception
   {
      ProjectFile mpp = new MPPReader().read(m_basedir + "/mpp12gantt.mpp");
      testSummaryData(mpp);
      testFontStyles(mpp);
      testGridlines(mpp);
   }

   /**
    * Test Gantt chart data read from an MPP14 file.
    * 
    * @throws Exception
    */
   public void testMpp14Gantt() throws Exception
   {
      ProjectFile mpp = new MPPReader().read(m_basedir + "/mpp14gantt.mpp");
      testSummaryData(mpp);
      testFontStyles(mpp);
      testGridlines(mpp);
   }

   /**
    * Test Gantt chart view summary data.
    * 
    * @param file project file
    */
   private void testSummaryData(ProjectFile file)
   {
      List<View> views = file.getViews();

      //
      // Retrieve the Gantt Chart view
      //
      GanttChartView view = (GanttChartView) views.get(0);
      assertEquals("Gantt Chart", view.getName());

      assertTrue(view.getShowInMenu());
      assertEquals(626, view.getTableWidth());
      assertFalse(view.getHighlightFilter());
      assertEquals("Entry", view.getTableName());
      assertEquals("&All Tasks", view.getDefaultFilterName());
      assertEquals("No Group", view.getGroupName());

      assertEquals("Standard", view.getNonWorkingDaysCalendarName());
      assertEquals("java.awt.Color[r=194,g=220,b=255]", view.getNonWorkingColor().toString());
      assertEquals(ChartPattern.LIGHTDOTTED, view.getNonWorkingPattern());
      assertEquals(NonWorkingTimeStyle.BEHIND, view.getNonWorkingStyle());

      assertEquals(12, view.getGanttBarHeight());
   }

   /**
    * Test the font styles associated with a Gantt chart view.
    * 
    * @param file project file
    */
   private void testFontStyles(ProjectFile file)
   {
      List<View> views = file.getViews();

      //
      // Retrieve the Gantt Chart view
      //
      GanttChartView view = (GanttChartView) views.get(0);
      assertEquals("Gantt Chart", view.getName());

      assertEquals("[FontStyle fontBase=[FontBase name=Arial size=8] italic=false bold=false underline=false color=java.awt.Color[r=0,g=0,b=255] backgroundColor=null backgroundPattern=Solid]", view.getHighlightedTasksFontStyle().toString());
      assertEquals("[FontStyle fontBase=[FontBase name=Arial size=8] italic=false bold=false underline=false color=null backgroundColor=null backgroundPattern=Solid]", view.getRowAndColumnFontStyle().toString());
      assertEquals("[FontStyle fontBase=[FontBase name=Berlin Sans FB size=8] italic=false bold=true underline=true color=null backgroundColor=null backgroundPattern=Solid]", view.getNonCriticalTasksFontStyle().toString());
      assertEquals("[FontStyle fontBase=[FontBase name=Arial size=8] italic=false bold=false underline=false color=null backgroundColor=null backgroundPattern=Solid]", view.getCriticalTasksFontStyle().toString());
      assertEquals("[FontStyle fontBase=[FontBase name=Arial size=8] italic=false bold=true underline=false color=null backgroundColor=null backgroundPattern=Solid]", view.getSummaryTasksFontStyle().toString());
      assertEquals("[FontStyle fontBase=[FontBase name=Baskerville Old Face size=9] italic=true bold=false underline=false color=null backgroundColor=null backgroundPattern=Solid]", view.getMilestoneTasksFontStyle().toString());
      assertEquals("[FontStyle fontBase=[FontBase name=Arial size=8] italic=false bold=false underline=false color=null backgroundColor=null backgroundPattern=Solid]", view.getMiddleTimescaleFontStyle().toString());
      assertEquals("[FontStyle fontBase=[FontBase name=Arial size=8] italic=false bold=false underline=false color=null backgroundColor=null backgroundPattern=Solid]", view.getBottomTimescaleFontStyle().toString());
      assertEquals("[FontStyle fontBase=[FontBase name=Arial size=8] italic=false bold=true underline=false color=null backgroundColor=null backgroundPattern=Solid]", view.getBarTextLeftFontStyle().toString());
      assertEquals("[FontStyle fontBase=[FontBase name=Arial size=8] italic=false bold=true underline=false color=null backgroundColor=null backgroundPattern=Solid]", view.getBarTextRightFontStyle().toString());
      assertEquals("[FontStyle fontBase=[FontBase name=Arial size=8] italic=false bold=true underline=false color=null backgroundColor=null backgroundPattern=Solid]", view.getBarTextTopFontStyle().toString());
      assertEquals("[FontStyle fontBase=[FontBase name=Arial size=8] italic=false bold=true underline=false color=null backgroundColor=null backgroundPattern=Solid]", view.getBarTextBottomFontStyle().toString());
      assertEquals("[FontStyle fontBase=[FontBase name=Arial size=8] italic=false bold=true underline=false color=java.awt.Color[r=0,g=0,b=0] backgroundColor=null backgroundPattern=Solid]", view.getBarTextInsideFontStyle().toString());
      assertEquals("[FontStyle fontBase=[FontBase name=BankGothic Lt BT size=8] italic=false bold=false underline=false color=null backgroundColor=null backgroundPattern=Solid]", view.getMarkedTasksFontStyle().toString());
      assertEquals("[FontStyle fontBase=[FontBase name=Arial size=10] italic=false bold=true underline=false color=null backgroundColor=null backgroundPattern=Solid]", view.getProjectSummaryTasksFontStyle().toString());
      assertEquals("[FontStyle fontBase=[FontBase name=Arial size=8] italic=false bold=false underline=false color=java.awt.Color[r=128,g=128,b=128] backgroundColor=null backgroundPattern=Solid]", view.getExternalTasksFontStyle().toString());
      assertEquals("[FontStyle fontBase=[FontBase name=Arial size=8] italic=false bold=false underline=false color=null backgroundColor=null backgroundPattern=Solid]", view.getTopTimescaleFontStyle().toString());
   }

   /**
    * Common gridline tests.
    * 
    * @param file project file
    */
   private void testGridlines(ProjectFile file)
   {
      List<View> views = file.getViews();

      //
      // Retrieve the Gantt Chart view
      //
      GanttChartView view = (GanttChartView) views.get(0);
      assertEquals("Gantt Chart", view.getName());

      //
      // Test each set of grid line definitions
      //
      assertEquals("[GridLines NormalLineColor=null NormalLineStyle=None IntervalNumber=0 IntervalLineStyle=None IntervalLineColor=null]", view.getGanttRowsGridLines().toString());
      assertEquals("[GridLines NormalLineColor=java.awt.Color[r=255,g=0,b=0] NormalLineStyle=Solid IntervalNumber=0 IntervalLineStyle=None IntervalLineColor=null]", view.getBarRowsGridLines().toString());
      assertEquals("[GridLines NormalLineColor=java.awt.Color[r=0,g=0,b=255] NormalLineStyle=Dotted1 IntervalNumber=0 IntervalLineStyle=None IntervalLineColor=java.awt.Color[r=0,g=0,b=0]]", view.getMiddleTierColumnGridLines().toString());
      assertEquals("[GridLines NormalLineColor=java.awt.Color[r=0,g=128,b=0] NormalLineStyle=None IntervalNumber=0 IntervalLineStyle=None IntervalLineColor=null]", view.getBottomTierColumnGridLines().toString());
      assertEquals("[GridLines NormalLineColor=java.awt.Color[r=128,g=128,b=128] NormalLineStyle=Dotted1 IntervalNumber=0 IntervalLineStyle=None IntervalLineColor=java.awt.Color[r=128,g=128,b=128]]", view.getCurrentDateGridLines().toString());
      assertEquals("[GridLines NormalLineColor=java.awt.Color[r=192,g=192,b=192] NormalLineStyle=Solid IntervalNumber=5 IntervalLineStyle=None IntervalLineColor=java.awt.Color[r=192,g=192,b=192]]", view.getSheetRowsGridLines().toString());
      assertEquals("[GridLines NormalLineColor=java.awt.Color[r=192,g=192,b=192] NormalLineStyle=Solid IntervalNumber=2 IntervalLineStyle=Dotted1 IntervalLineColor=java.awt.Color[r=192,g=192,b=192]]", view.getSheetColumnsGridLines().toString());
      assertEquals("[GridLines NormalLineColor=java.awt.Color[r=128,g=128,b=128] NormalLineStyle=Solid IntervalNumber=0 IntervalLineStyle=None IntervalLineColor=java.awt.Color[r=128,g=128,b=128]]", view.getTitleVerticalGridLines().toString());
      assertEquals("[GridLines NormalLineColor=java.awt.Color[r=128,g=128,b=128] NormalLineStyle=Solid IntervalNumber=0 IntervalLineStyle=None IntervalLineColor=java.awt.Color[r=128,g=128,b=128]]", view.getTitleHorizontalGridLines().toString());
      assertEquals("[GridLines NormalLineColor=null NormalLineStyle=Dashed IntervalNumber=0 IntervalLineStyle=None IntervalLineColor=null]", view.getPageBreakGridLines().toString());
      assertEquals("[GridLines NormalLineColor=null NormalLineStyle=None IntervalNumber=0 IntervalLineStyle=None IntervalLineColor=null]", view.getProjectStartGridLines().toString());
      assertEquals("[GridLines NormalLineColor=null NormalLineStyle=None IntervalNumber=0 IntervalLineStyle=None IntervalLineColor=null]", view.getProjectFinishGridLines().toString());
      assertEquals("[GridLines NormalLineColor=null NormalLineStyle=None IntervalNumber=0 IntervalLineStyle=None IntervalLineColor=null]", view.getStatusDateGridLines().toString());
      assertEquals("[GridLines NormalLineColor=java.awt.Color[r=0,g=0,b=128] NormalLineStyle=None IntervalNumber=0 IntervalLineStyle=None IntervalLineColor=null]", view.getTopTierColumnGridLines().toString());
   }
}
