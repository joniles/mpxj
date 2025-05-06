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

package org.mpxj.junit;

import static org.junit.Assert.*;

import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import java.time.DayOfWeek;
import org.mpxj.common.DayOfWeekHelper;
import org.mpxj.ProjectFile;
import org.mpxj.View;
import org.mpxj.mpp.ChartPattern;
import org.mpxj.mpp.GanttBarDateFormat;
import org.mpxj.mpp.GanttChartView;
import org.mpxj.mpp.Interval;
import org.mpxj.mpp.LineStyle;
import org.mpxj.mpp.LinkStyle;
import org.mpxj.mpp.MPPReader;
import org.mpxj.mpp.NonWorkingTimeStyle;
import org.mpxj.mpp.ProgressLineDay;
import org.mpxj.mpp.TableFontStyle;

import org.junit.Test;

/**
 * Tests to exercise MPP file read functionality for various versions of
 * MPP file.
 */
public class MppGanttTest
{
   /**
    * Test Gantt chart data read from an MPP9 file.
    */
   @Test public void testMpp9Gantt() throws Exception
   {
      ProjectFile mpp = new MPPReader().read(MpxjTestData.filePath("mpp9gantt.mpp"));
      testAll(mpp);
   }

   /**
    * Test Gantt chart data read from an MPP9 file saved by Project 2007.
    */
   @Test public void testMpp9GanttFrom12() throws Exception
   {
      ProjectFile mpp = new MPPReader().read(MpxjTestData.filePath("mpp9gantt-from12.mpp"));
      testAll(mpp);
   }

   /**
    * Test Gantt chart data read from an MPP9 file saved by Project 2010.
    */
   @Test public void testMpp9GanttFrom14()
   {
      //ProjectFile mpp = new MPPReader().read(MpxjTestData.filePath("mpp9gantt-from14.mpp"));
      //testAll(mpp);
   }

   /**
    * Test Gantt chart data read from an MPP12 file.
    */
   @Test public void testMpp12Gantt() throws Exception
   {
      ProjectFile mpp = new MPPReader().read(MpxjTestData.filePath("mpp12gantt.mpp"));
      testAll(mpp);
   }

   /**
    * Test Gantt chart data read from an MPP12 file saved by Project 2010.
    */
   @Test public void testMpp12GanttFrom14()
   {
      //ProjectFile mpp = new MPPReader().read(MpxjTestData.filePath("mpp12gantt-from14.mpp"));
      //testAll(mpp);
   }

   /**
    * Test Gantt chart data read from an MPP14 file.
    */
   @Test public void testMpp14Gantt() throws Exception
   {
      ProjectFile mpp = new MPPReader().read(MpxjTestData.filePath("mpp14gantt.mpp"));
      testAll(mpp);
   }

   /**
    * Main entry point for common tests.
    *
    * @param mpp project file to be tested
    */
   private void testAll(ProjectFile mpp)
   {
      testSummaryData(mpp);
      testFontStyles(mpp);
      testGridlines(mpp);
      testTimescales(mpp);
      testLayout(mpp);
      testTableFontStyles(mpp);
      testProgressLines(mpp);
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
      assertEquals(778, view.getTableWidth());
      assertFalse(view.getHighlightFilter());
      assertEquals("Entry", view.getTableName());
      assertEquals("&All Tasks", view.getDefaultFilterName());
      assertEquals("No Group", view.getGroupName());

      assertEquals("Standard", view.getNonWorkingDaysCalendarName());
      assertEquals("java.awt.Color[r=194,g=220,b=255]", view.getNonWorkingColor().toString());
      assertEquals(ChartPattern.LIGHTDOTTED, view.getNonWorkingPattern());
      assertEquals(NonWorkingTimeStyle.BEHIND, view.getNonWorkingStyle());

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

      assertEquals("[FontStyle fontBase=[FontBase name=Arial size=8] italic=false bold=false underline=false strikethrough=false color=java.awt.Color[r=0,g=0,b=255] backgroundColor=null backgroundPattern=Solid]", view.getHighlightedTasksFontStyle().toString());
      assertEquals("[FontStyle fontBase=[FontBase name=Arial size=8] italic=false bold=false underline=false strikethrough=false color=null backgroundColor=null backgroundPattern=Solid]", view.getRowAndColumnFontStyle().toString());
      assertEquals("[FontStyle fontBase=[FontBase name=Berlin Sans FB size=8] italic=false bold=true underline=true strikethrough=false color=null backgroundColor=null backgroundPattern=Solid]", view.getNonCriticalTasksFontStyle().toString());
      assertEquals("[FontStyle fontBase=[FontBase name=Arial size=8] italic=false bold=false underline=false strikethrough=false color=null backgroundColor=null backgroundPattern=Solid]", view.getCriticalTasksFontStyle().toString());
      assertEquals("[FontStyle fontBase=[FontBase name=Arial size=8] italic=false bold=true underline=false strikethrough=false color=null backgroundColor=null backgroundPattern=Solid]", view.getSummaryTasksFontStyle().toString());
      assertEquals("[FontStyle fontBase=[FontBase name=Baskerville Old Face size=9] italic=true bold=false underline=false strikethrough=false color=null backgroundColor=null backgroundPattern=Solid]", view.getMilestoneTasksFontStyle().toString());
      assertEquals("[FontStyle fontBase=[FontBase name=Arial size=8] italic=false bold=false underline=false strikethrough=false color=null backgroundColor=null backgroundPattern=Solid]", view.getMiddleTimescaleFontStyle().toString());
      assertEquals("[FontStyle fontBase=[FontBase name=Arial size=8] italic=false bold=false underline=false strikethrough=false color=null backgroundColor=null backgroundPattern=Solid]", view.getBottomTimescaleFontStyle().toString());
      assertEquals("[FontStyle fontBase=[FontBase name=Arial size=8] italic=false bold=true underline=false strikethrough=false color=null backgroundColor=null backgroundPattern=Solid]", view.getBarTextLeftFontStyle().toString());
      assertEquals("[FontStyle fontBase=[FontBase name=Arial size=8] italic=false bold=true underline=false strikethrough=false color=null backgroundColor=null backgroundPattern=Solid]", view.getBarTextRightFontStyle().toString());
      assertEquals("[FontStyle fontBase=[FontBase name=Arial size=8] italic=false bold=true underline=false strikethrough=false color=null backgroundColor=null backgroundPattern=Solid]", view.getBarTextTopFontStyle().toString());
      assertEquals("[FontStyle fontBase=[FontBase name=Arial size=8] italic=false bold=true underline=false strikethrough=false color=null backgroundColor=null backgroundPattern=Solid]", view.getBarTextBottomFontStyle().toString());
      assertEquals("[FontStyle fontBase=[FontBase name=Arial size=8] italic=false bold=true underline=false strikethrough=false color=java.awt.Color[r=0,g=0,b=0] backgroundColor=null backgroundPattern=Solid]", view.getBarTextInsideFontStyle().toString());
      assertEquals("[FontStyle fontBase=[FontBase name=BankGothic Lt BT size=8] italic=false bold=false underline=false strikethrough=false color=null backgroundColor=null backgroundPattern=Solid]", view.getMarkedTasksFontStyle().toString());
      assertEquals("[FontStyle fontBase=[FontBase name=Arial size=10] italic=false bold=true underline=false strikethrough=false color=null backgroundColor=null backgroundPattern=Solid]", view.getProjectSummaryTasksFontStyle().toString());
      assertEquals("[FontStyle fontBase=[FontBase name=Arial size=8] italic=false bold=false underline=false strikethrough=false color=java.awt.Color[r=128,g=128,b=128] backgroundColor=null backgroundPattern=Solid]", view.getExternalTasksFontStyle().toString());
      assertEquals("[FontStyle fontBase=[FontBase name=Arial size=8] italic=false bold=false underline=false strikethrough=false color=null backgroundColor=null backgroundPattern=Solid]", view.getTopTimescaleFontStyle().toString());
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

   /**
    * Test the timescale settings.
    *
    * @param file project file
    */
   private void testTimescales(ProjectFile file)
   {
      List<View> views = file.getViews();

      //
      // Retrieve the Gantt Chart view
      //
      GanttChartView view = (GanttChartView) views.get(0);
      assertEquals("Gantt Chart", view.getName());

      assertEquals(2, view.getTimescaleShowTiers());
      assertEquals(100, view.getTimescaleSize());
      assertTrue(view.getTimescaleScaleSeparator());

      assertEquals("[TimescaleTier UsesFiscalYear=true TickLines=true Units=None Count=1 Format=[None] Alignment=Center]", view.getTimescaleTopTier().toString());
      assertEquals("[TimescaleTier UsesFiscalYear=true TickLines=true Units=Weeks Count=1 Format=[January 27, '02] Alignment=Left]", view.getTimescaleMiddleTier().toString());
      assertEquals("[TimescaleTier UsesFiscalYear=true TickLines=true Units=Days Count=1 Format=[S, M, T, ...] Alignment=Center]", view.getTimescaleBottomTier().toString());
   }

   /**
    * Test the layout settings.
    *
    * @param file project file
    */
   private void testLayout(ProjectFile file)
   {
      List<View> views = file.getViews();

      //
      // Retrieve the Gantt Chart view
      //
      GanttChartView view = (GanttChartView) views.get(0);
      assertEquals("Gantt Chart", view.getName());

      assertTrue(view.getShowDrawings());
      assertTrue(view.getRoundBarsToWholeDays());
      assertTrue(view.getShowBarSplits());
      assertFalse(view.getAlwaysRollupGanttBars());
      assertFalse(view.getHideRollupBarsWhenSummaryExpanded());
      assertEquals(12, view.getGanttBarHeight());
      assertEquals(GanttBarDateFormat.DDMM, view.getBarDateFormat());
      assertEquals(LinkStyle.END_TOP, view.getLinkStyle());
   }

   /**
    * Test the table font style settings.
    *
    * @param file project file
    */
   private void testTableFontStyles(ProjectFile file)
   {
      List<View> views = file.getViews();

      //
      // Retrieve the Gantt Chart view
      //
      GanttChartView view = (GanttChartView) views.get(0);
      assertEquals("Gantt Chart", view.getName());

      TableFontStyle[] tfs = view.getTableFontStyles();
      assertEquals(TABLE_FONT_STYLES.length, tfs.length);

      for (TableFontStyle tf : tfs)
      {
         assertTrue(TABLE_FONT_STYLES_SET.contains(tf.toString()));
      }
   }

   /**
    * Test the progress line settings.
    *
    * @param file project file
    */
   private void testProgressLines(ProjectFile file)
   {
      DateTimeFormatter df = DateTimeFormatter.ofPattern("dd/MM/yyyy");

      List<View> views = file.getViews();

      //
      // Retrieve the Gantt Chart view
      //
      GanttChartView view = (GanttChartView) views.get(0);
      assertEquals("Gantt Chart", view.getName());

      assertTrue(view.getProgressLinesEnabled());
      assertFalse(view.getProgressLinesAtCurrentDate());
      assertTrue(view.getProgressLinesAtRecurringIntervals());
      assertEquals(Interval.WEEKLY, view.getProgressLinesInterval());
      assertEquals(1, view.getProgressLinesIntervalDailyDayNumber());
      assertTrue(view.isProgressLinesIntervalDailyWorkday());
      boolean[] weeklyDay = view.getProgressLinesIntervalWeeklyDay();
      assertFalse(weeklyDay[DayOfWeekHelper.getValue(DayOfWeek.SUNDAY)]);
      assertTrue(weeklyDay[DayOfWeekHelper.getValue(DayOfWeek.MONDAY)]);
      assertFalse(weeklyDay[DayOfWeekHelper.getValue(DayOfWeek.TUESDAY)]);
      assertFalse(weeklyDay[DayOfWeekHelper.getValue(DayOfWeek.WEDNESDAY)]);
      assertFalse(weeklyDay[DayOfWeekHelper.getValue(DayOfWeek.THURSDAY)]);
      assertFalse(weeklyDay[DayOfWeekHelper.getValue(DayOfWeek.FRIDAY)]);
      assertFalse(weeklyDay[DayOfWeekHelper.getValue(DayOfWeek.SATURDAY)]);
      assertEquals(1, view.getProgressLinesIntervalWeekleyWeekNumber());
      assertFalse(view.getProgressLinesIntervalMonthlyDay());
      assertEquals(1, view.getProgressLinesIntervalMonthlyDayMonthNumber());
      assertEquals(1, view.getProgressLinesIntervalMonthlyDayDayNumber());
      assertEquals(ProgressLineDay.DAY, view.getProgressLinesIntervalMonthlyFirstLastDay());
      assertTrue(view.getProgressLinesIntervalMonthlyFirstLast());
      assertEquals(1, view.getProgressLinesIntervalMonthlyFirstLastMonthNumber());

      assertFalse(view.getProgressLinesBeginAtProjectStart());
      assertEquals("13/05/2010", df.format(view.getProgressLinesBeginAtDate()));
      assertTrue(view.getProgressLinesDisplaySelected());
      assertTrue(view.getProgressLinesActualPlan());
      assertEquals(0, view.getProgressLinesDisplayType());
      assertFalse(view.getProgressLinesShowDate());
      assertEquals(26, view.getProgressLinesDateFormat());
      assertEquals("[FontStyle fontBase=[FontBase name=Arial size=8] italic=false bold=false underline=false strikethrough=false color=java.awt.Color[r=0,g=0,b=0] backgroundColor=null backgroundPattern=Solid]", view.getProgressLinesFontStyle().toString());
      assertEquals("java.awt.Color[r=255,g=0,b=0]", view.getProgressLinesCurrentLineColor().toString());
      assertEquals(LineStyle.SOLID, view.getProgressLinesCurrentLineStyle());
      assertEquals("java.awt.Color[r=255,g=0,b=0]", view.getProgressLinesCurrentProgressPointColor().toString());
      assertEquals(13, view.getProgressLinesCurrentProgressPointShape());
      assertNull(view.getProgressLinesOtherLineColor());
      assertEquals(LineStyle.SOLID, view.getProgressLinesOtherLineStyle());
      assertNull(view.getProgressLinesOtherProgressPointColor());
      assertEquals(0, view.getProgressLinesOtherProgressPointShape());
      assertEquals(2, view.getProgressLinesDisplaySelectedDates().length);
      assertEquals("01/02/2010", df.format(view.getProgressLinesDisplaySelectedDates()[0]));
      assertEquals("01/01/2010", df.format(view.getProgressLinesDisplaySelectedDates()[1]));
   }

   private static final String[] TABLE_FONT_STYLES =
   {
      "[ColumnFontStyle rowUniqueID=3 fieldType=Text2 color=java.awt.Color[r=0,g=0,b=255]]",
      "[ColumnFontStyle rowUniqueID=-1 fieldType=Task Name italic=false bold=true underline=false font=[FontBase name=Arial Black size=8] color=null backgroundColor=java.awt.Color[r=0,g=0,b=0] backgroundPattern=Transparent]",
      "[ColumnFontStyle rowUniqueID=-1 fieldType=Duration italic=false bold=true underline=false font=[FontBase name=Arial size=8] color=null backgroundColor=java.awt.Color[r=0,g=0,b=0] backgroundPattern=Transparent]",
      "[ColumnFontStyle rowUniqueID=-1 fieldType=Start italic=true bold=false underline=false font=[FontBase name=Arial size=8] color=null backgroundColor=java.awt.Color[r=0,g=0,b=0] backgroundPattern=Transparent]",
      "[ColumnFontStyle rowUniqueID=-1 fieldType=Finish italic=true bold=true underline=false font=[FontBase name=Arial size=8] color=null backgroundColor=java.awt.Color[r=0,g=0,b=0] backgroundPattern=Transparent]",
      "[ColumnFontStyle rowUniqueID=-1 fieldType=Predecessors italic=false bold=false underline=false font=[FontBase name=Arial size=10] color=null backgroundColor=java.awt.Color[r=0,g=0,b=0] backgroundPattern=Transparent]",
      "[ColumnFontStyle rowUniqueID=-1 fieldType=Text1 italic=false bold=false underline=true font=[FontBase name=Arial size=8] color=null backgroundColor=java.awt.Color[r=0,g=0,b=0] backgroundPattern=Transparent]",
      "[ColumnFontStyle rowUniqueID=-1 fieldType=Text2 italic=false bold=false underline=false font=[FontBase name=Arial size=8] color=java.awt.Color[r=255,g=0,b=0] backgroundColor=java.awt.Color[r=0,g=0,b=0] backgroundPattern=Transparent]"
   };

   private static final Set<String> TABLE_FONT_STYLES_SET = new HashSet<>();
   static
   {
      Collections.addAll(TABLE_FONT_STYLES_SET, TABLE_FONT_STYLES);
   }
}
