/*
 * file:       GanttChartView.java
 * author:     Jon Iles
 * copyright:  (c) Packwood Software 2005
 * date:       Apr 7, 2005
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

package org.mpxj.mpp;

import java.awt.Color;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.time.LocalDateTime;
import java.util.ArrayList;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.mpxj.FieldType;
import org.mpxj.Filter;
import org.mpxj.FilterContainer;
import org.mpxj.ProjectFile;
import org.mpxj.common.ByteArrayHelper;
import org.mpxj.common.FieldTypeHelper;

/**
 * This class represents the set of properties used to define the appearance
 * of a Gantt chart view in MS Project.
 */
public abstract class GanttChartView extends GenericView
{
   /**
    * Extract the Gantt bar styles.
    *
    * @param props props structure containing the style definitions
    */
   protected abstract void processDefaultBarStyles(Props props);

   /**
    * Extract the exception Gantt bar styles.
    *
    * @param props props structure containing the style definitions
    */
   protected abstract void processExceptionBarStyles(Props props);

   /**
    * Extract autofilter definitions.
    *
    * @param data autofilters data block
    */
   protected abstract void processAutoFilters(byte[] data);

   /**
    * Extract view properties.
    *
    * @param fontBases font defintions
    * @param props Gantt chart view props
    */
   protected abstract void processViewProperties(Map<Integer, FontBase> fontBases, Props props);

   /**
    * Extract table font styles.
    *
    * @param fontBases font bases
    * @param data column data
    */
   protected abstract void processTableFontStyles(Map<Integer, FontBase> fontBases, byte[] data);

   /**
    * Extract progress line properties.
    *
    * @param fontBases font bases
    * @param data column data
    */
   protected abstract void processProgressLines(Map<Integer, FontBase> fontBases, byte[] data);

   /**
    * Create a GanttChartView from the fixed and var data blocks associated
    * with a view.
    *
    * @param parent parent MPP file
    * @param fixedMeta fixed meta data block
    * @param fixedData fixed data block
    * @param varData var data block
    * @param fontBases map of font bases
    */
   GanttChartView(ProjectFile parent, byte[] fixedMeta, byte[] fixedData, Var2Data varData, Map<Integer, FontBase> fontBases)
      throws IOException
   {
      super(parent, fixedData, varData);
      //      System.out.println(varData.getVarMeta());
      //      MPPUtility.fileDump("c:\\temp\\"+getName()+"-vardata.txt", varData.toString().getBytes());

      m_filters = parent.getFilters();
      m_showInMenu = (fixedMeta[8] & 0x08) != 0;

      byte[] propsData = varData.getByteArray(m_id, getPropertiesID());
      if (propsData != null)
      {
         Props9 props = new Props9(new ByteArrayInputStream(propsData));
         //MPPUtility.fileDump("c:\\temp\\props.txt", props.toString().getBytes());

         byte[] tableData = props.getByteArray(TABLE_PROPERTIES);
         if (tableData != null)
         {
            m_tableWidth = ByteArrayHelper.getShort(tableData, 35);
            m_highlightFilter = (tableData[7] != 0);
         }

         byte[] filterName = props.getByteArray(FILTER_NAME);
         if (filterName != null)
         {
            m_defaultFilterName = MPPUtility.getUnicodeString(filterName, 0);
         }

         byte[] groupName = props.getByteArray(GROUP_NAME);
         if (groupName != null)
         {
            m_groupName = MPPUtility.getUnicodeString(groupName, 0);
         }

         processViewProperties(fontBases, props);

         processDefaultBarStyles(props);

         processExceptionBarStyles(props);

         byte[] columnData = props.getByteArray(COLUMN_PROPERTIES);
         if (columnData != null)
         {
            processTableFontStyles(fontBases, columnData);
         }

         byte[] progressLineData = props.getByteArray(PROGRESS_LINE_PROPERTIES);
         if (progressLineData != null)
         {
            processProgressLines(fontBases, progressLineData);
         }

         byte[] autoFilterData = props.getByteArray(AUTO_FILTER_PROPERTIES);
         if (autoFilterData != null)
         {
            processAutoFilters(autoFilterData);
         }
      }

      //MPPUtility.fileDump("c:\\temp\\GanttChartView9.txt", toString().getBytes());
   }

   /**
    * Retrieve a grid lines definition.
    *
    * @return grid lines definition
    */
   public GridLines getSheetColumnsGridLines()
   {
      return (m_sheetColumnsGridLines);
   }

   /**
    * Retrieve a grid lines definition.
    *
    * @return grid lines definition
    */
   public GridLines getSheetRowsGridLines()
   {
      return (m_sheetRowsGridLines);
   }

   /**
    * Retrieve a grid lines definition.
    *
    * @return grid lines definition
    */
   public GridLines getStatusDateGridLines()
   {
      return (m_statusDateGridLines);
   }

   /**
    * Retrieve a grid lines definition.
    *
    * @return grid lines definition
    */
   public GridLines getTitleHorizontalGridLines()
   {
      return (m_titleHorizontalGridLines);
   }

   /**
    * Retrieve a grid lines definition.
    *
    * @return grid lines definition
    */
   public GridLines getTitleVerticalGridLines()
   {
      return (m_titleVerticalGridLines);
   }

   /**
    * Retrieve a grid lines definition.
    *
    * @return grid lines definition
    */
   public GridLines getBarRowsGridLines()
   {
      return (m_barRowsGridLines);
   }

   /**
    * Retrieve a grid lines definition.
    *
    * @return grid lines definition
    */
   public GridLines getCurrentDateGridLines()
   {
      return (m_currentDateGridLines);
   }

   /**
    * Retrieve a grid lines definition.
    *
    * @return grid lines definition
    */
   public GridLines getGanttRowsGridLines()
   {
      return (m_ganttRowsGridLines);
   }

   /**
    * Retrieve a grid lines definition.
    *
    * @return grid lines definition
    */
   public GridLines getTopTierColumnGridLines()
   {
      return (m_topTierColumnGridLines);
   }

   /**
    * Retrieve a grid lines definition.
    *
    * @return grid lines definition
    */
   public GridLines getMiddleTierColumnGridLines()
   {
      return (m_middleTierColumnGridLines);
   }

   /**
    * Retrieve a grid lines definition.
    *
    * @return grid lines definition
    */
   public GridLines getBottomTierColumnGridLines()
   {
      return (m_bottomTierColumnGridLines);
   }

   /**
    * Retrieve the name of the calendar used to define non-working days for
    * this view..
    *
    * @return calendar name
    */
   public String getNonWorkingDaysCalendarName()
   {
      return (m_nonWorkingDaysCalendarName);
   }

   /**
    * Retrieve a grid lines definition.
    *
    * @return grid lines definition
    */
   public GridLines getPageBreakGridLines()
   {
      return (m_pageBreakGridLines);
   }

   /**
    * Retrieve a grid lines definition.
    *
    * @return grid lines definition
    */
   public GridLines getProjectFinishGridLines()
   {
      return (m_projectFinishGridLines);
   }

   /**
    * Retrieve a grid lines definition.
    *
    * @return grid lines definition
    */
   public GridLines getProjectStartGridLines()
   {
      return (m_projectStartGridLines);
   }

   /**
    * Retrieve the height of the Gantt bars in this view.
    *
    * @return Gantt bar height
    */
   public int getGanttBarHeight()
   {
      return (m_ganttBarHeight);
   }

   /**
    * Retrieve a flag indicating if a separator is shown between the
    * major and minor scales.
    *
    * @return boolean flag
    */
   public boolean getTimescaleScaleSeparator()
   {
      return (m_timescaleScaleSeparator);
   }

   /**
    * Retrieves a timescale tier.
    *
    * @return timescale tier
    */
   public TimescaleTier getTimescaleTopTier()
   {
      return (m_timescaleTopTier);
   }

   /**
    * Retrieves a timescale tier.
    *
    * @return timescale tier
    */
   public TimescaleTier getTimescaleMiddleTier()
   {
      return (m_timescaleMiddleTier);
   }

   /**
    * Retrieves a timescale tier.
    *
    * @return timescale tier
    */
   public TimescaleTier getTimescaleBottomTier()
   {
      return (m_timescaleBottomTier);
   }

   /**
    * Retrieve the timescale size value. This is a percentage value.
    *
    * @return timescale size value
    */
   public int getTimescaleSize()
   {
      return (m_timescaleSize);
   }

   /**
    * Retrieve the number of timescale tiers to display.
    *
    * @return number of timescale tiers to show
    */
   public int getTimescaleShowTiers()
   {
      return m_timescaleShowTiers;
   }

   /**
    * Retrieve the non-working time color.
    *
    * @return non-working time color
    */
   public Color getNonWorkingColor()
   {
      return (m_nonWorkingColor);
   }

   /**
    * Retrieve the non-working time pattern. This is an integer between
    * 0 and 10 inclusive which represents the fixed set of patterns
    * supported by MS Project.
    *
    * @return non-working time pattern
    */
   public ChartPattern getNonWorkingPattern()
   {
      return (m_nonWorkingPattern);
   }

   /**
    * Retrieve the style used to draw non-working time.
    *
    * @return non working time style
    */
   public NonWorkingTimeStyle getNonWorkingStyle()
   {
      return (m_nonWorkingStyle);
   }

   /**
    * Retrieve the always rollup Gantt bars flag.
    *
    * @return always rollup Gantt bars flag
    */
   public boolean getAlwaysRollupGanttBars()
   {
      return (m_alwaysRollupGanttBars);
   }

   /**
    * Retrieve the bar date format.
    *
    * @return bar date format
    */
   public GanttBarDateFormat getBarDateFormat()
   {
      return (m_barDateFormat);
   }

   /**
    * Retrieve the hide rollup bars when summary expanded.
    *
    * @return hide rollup bars when summary expanded
    */
   public boolean getHideRollupBarsWhenSummaryExpanded()
   {
      return (m_hideRollupBarsWhenSummaryExpanded);
   }

   /**
    * Retrieve the bar link style.
    *
    * @return bar link style
    */
   public LinkStyle getLinkStyle()
   {
      return (m_linkStyle);
   }

   /**
    * Retrieve the round bars to whole days flag.
    *
    * @return round bars to whole days flag
    */
   public boolean getRoundBarsToWholeDays()
   {
      return (m_roundBarsToWholeDays);
   }

   /**
    * Retrieve the show bar splits flag.
    *
    * @return show bar splits flag
    */
   public boolean getShowBarSplits()
   {
      return (m_showBarSplits);
   }

   /**
    * Retrieve the show drawings flag.
    *
    * @return show drawings flag
    */
   public boolean getShowDrawings()
   {
      return (m_showDrawings);
   }

   /**
    * Retrieve an array representing bar styles which have been defined
    * by the user for a specific task.
    *
    * @return array of bar style exceptions
    */
   public GanttBarStyleException[] getBarStyleExceptions()
   {
      return m_barStyleExceptions;
   }

   /**
    * Retrieve a list of Gantt Bar Styles for this view which match the supplied  ID.
    *
    * @param id Gantt Bar Style ID
    * @return GanttBarStyle or null
    */
   public List<GanttBarStyle> getGanttBarStyleByID(Integer id)
   {
      return m_barStylesMap.get(id);
   }

   /**
    * Retrieve an array of bar styles which are applied to all Gantt
    * chart bars, unless an exception has been defined.
    *
    * @return array of bar styles
    */
   public GanttBarStyle[] getBarStyles()
   {
      return m_barStyles;
   }

   /**
    * Retrieve the width of the table part of the view.
    *
    * @return table width
    */
   public int getTableWidth()
   {
      return (m_tableWidth);
   }

   /**
    * Retrieve the name of the filter applied to this view.
    *
    * @return filter name
    */
   public String getDefaultFilterName()
   {
      return (m_defaultFilterName);
   }

   /**
    * Convenience method used to retrieve the default filter instance
    * associated with this view.
    *
    * @return filter instance, null if no filter associated with view
    */
   public Filter getDefaultFilter()
   {
      return (m_filters.getFilterByName(m_defaultFilterName));
   }

   /**
    * Retrieve the name of the grouping applied to this view.
    *
    * @return group name
    */
   public String getGroupName()
   {
      return (m_groupName);
   }

   /**
    * Retrieve the highlight filter flag.
    *
    * @return highlight filter flag
    */
   public boolean getHighlightFilter()
   {
      return (m_highlightFilter);
   }

   /**
    * Retrieve the show in menu flag.
    *
    * @return show in menu flag
    */
   public boolean getShowInMenu()
   {
      return (m_showInMenu);
   }

   /**
    * Retrieve a FontStyle instance.
    *
    * @return FontStyle instance
    */
   public FontStyle getBarTextBottomFontStyle()
   {
      return (m_barTextBottomFontStyle);
   }

   /**
    * Retrieve a FontStyle instance.
    *
    * @return FontStyle instance
    */
   public FontStyle getBarTextInsideFontStyle()
   {
      return (m_barTextInsideFontStyle);
   }

   /**
    * Retrieve a FontStyle instance.
    *
    * @return FontStyle instance
    */
   public FontStyle getBarTextLeftFontStyle()
   {
      return (m_barTextLeftFontStyle);
   }

   /**
    * Retrieve a FontStyle instance.
    *
    * @return FontStyle instance
    */
   public FontStyle getBarTextRightFontStyle()
   {
      return (m_barTextRightFontStyle);
   }

   /**
    * Retrieve a FontStyle instance.
    *
    * @return FontStyle instance
    */
   public FontStyle getBarTextTopFontStyle()
   {
      return (m_barTextTopFontStyle);
   }

   /**
    * Retrieve a FontStyle instance.
    *
    * @return FontStyle instance
    */
   public FontStyle getCriticalTasksFontStyle()
   {
      return (m_criticalTasksFontStyle);
   }

   /**
    * Retrieve a FontStyle instance.
    *
    * @return FontStyle instance
    */
   public FontStyle getExternalTasksFontStyle()
   {
      return (m_externalTasksFontStyle);
   }

   /**
    * Retrieve a FontStyle instance.
    *
    * @return FontStyle instance
    */
   public FontStyle getHighlightedTasksFontStyle()
   {
      return (m_highlightedTasksFontStyle);
   }

   /**
    * Retrieve a FontStyle instance.
    *
    * @return FontStyle instance
    */
   public FontStyle getTopTimescaleFontStyle()
   {
      return (m_topTimescaleFontStyle);
   }

   /**
    * Retrieve a FontStyle instance.
    *
    * @return FontStyle instance
    */
   public FontStyle getMiddleTimescaleFontStyle()
   {
      return (m_middleTimescaleFontStyle);
   }

   /**
    * Retrieve a FontStyle instance.
    *
    * @return FontStyle instance
    */
   public FontStyle getMarkedTasksFontStyle()
   {
      return (m_markedTasksFontStyle);
   }

   /**
    * Retrieve a FontStyle instance.
    *
    * @return FontStyle instance
    */
   public FontStyle getMilestoneTasksFontStyle()
   {
      return (m_milestoneTasksFontStyle);
   }

   /**
    * Retrieve a FontStyle instance.
    *
    * @return FontStyle instance
    */
   public FontStyle getBottomTimescaleFontStyle()
   {
      return (m_bottomTimescaleFontStyle);
   }

   /**
    * Retrieve a FontStyle instance.
    *
    * @return FontStyle instance
    */
   public FontStyle getNonCriticalTasksFontStyle()
   {
      return (m_nonCriticalTasksFontStyle);
   }

   /**
    * Retrieve a FontStyle instance.
    *
    * @return FontStyle instance
    */
   public FontStyle getProjectSummaryTasksFontStyle()
   {
      return (m_projectSummaryTasksFontStyle);
   }

   /**
    * Retrieve a FontStyle instance.
    *
    * @return FontStyle instance
    */
   public FontStyle getRowAndColumnFontStyle()
   {
      return (m_rowAndColumnFontStyle);
   }

   /**
    * Retrieve a FontStyle instance.
    *
    * @return FontStyle instance
    */
   public FontStyle getSummaryTasksFontStyle()
   {
      return (m_summaryTasksFontStyle);
   }

   /**
    * Retrieve any column font styles which the user has defined.
    *
    * @return column font styles array
    */
   public TableFontStyle[] getTableFontStyles()
   {
      return (m_tableFontStyles);
   }

   /**
    * Retrieve the progress lines actual plan flag.
    *
    * @return boolean flag
    */
   public boolean getProgressLinesActualPlan()
   {
      return (m_progressLinesActualPlan);
   }

   /**
    * Retrieve the progress lines at current date flag.
    *
    * @return boolean flag
    */
   public boolean getProgressLinesAtCurrentDate()
   {
      return (m_progressLinesAtCurrentDate);
   }

   /**
    * Retrieve the progress lines at recurring intervals flag.
    *
    * @return boolean flag
    */
   public boolean getProgressLinesAtRecurringIntervals()
   {
      return (m_progressLinesAtRecurringIntervals);
   }

   /**
    * Retrieve the progress lines begin at date.
    *
    * @return progress lines begin at date
    */
   public LocalDateTime getProgressLinesBeginAtDate()
   {
      return (m_progressLinesBeginAtDate);
   }

   /**
    * Retrieve the progress lines begin at project start flag.
    *
    * @return boolean flag
    */
   public boolean getProgressLinesBeginAtProjectStart()
   {
      return (m_progressLinesBeginAtProjectStart);
   }

   /**
    * Retrieve the progress lines current line color.
    *
    * @return current line color
    */
   public Color getProgressLinesCurrentLineColor()
   {
      return (m_progressLinesCurrentLineColor);
   }

   /**
    * Retrieve the progress lines current line style.
    *
    * @return current line style
    */
   public LineStyle getProgressLinesCurrentLineStyle()
   {
      return (m_progressLinesCurrentLineStyle);
   }

   /**
    * Retrieve the current progress point color.
    *
    * @return current progress point color
    */
   public Color getProgressLinesCurrentProgressPointColor()
   {
      return (m_progressLinesCurrentProgressPointColor);
   }

   /**
    * Retrieve the current progress point shape.
    *
    * @return current progress point shape
    */
   public int getProgressLinesCurrentProgressPointShape()
   {
      return (m_progressLinesCurrentProgressPointShape);
   }

   /**
    * Retrieve the progress lines daily day number.
    *
    * @return progress lines daily day number
    */
   public int getProgressLinesIntervalDailyDayNumber()
   {
      return (m_progressLinesIntervalDailyDayNumber);
   }

   /**
    * Retrieve the progress lines daily workday flag.
    *
    * @return daily workday flag
    */
   public boolean isProgressLinesIntervalDailyWorkday()
   {
      return (m_progressLinesIntervalDailyWorkday);
   }

   /**
    * Retrieve the progress line date format.
    *
    * @return progress line date format.
    */
   public int getProgressLinesDateFormat()
   {
      return (m_progressLinesDateFormat);
   }

   /**
    * Retrieves the flag indicating if selected dates have been supplied
    * for progress line display.
    *
    * @return boolean flag
    */
   public boolean getProgressLinesDisplaySelected()
   {
      return (m_progressLinesDisplaySelected);
   }

   /**
    * Retrieves an array of selected dates for progress line display,
    * or returns null if no dates have been supplied.
    *
    * @return array of selected dates
    */
   public LocalDateTime[] getProgressLinesDisplaySelectedDates()
   {
      return (m_progressLinesDisplaySelectedDates);
   }

   /**
    * Retrieves the progress lines display type.
    *
    * @return progress lines display type
    */
   public int getProgressLinesDisplayType()
   {
      return (m_progressLinesDisplayType);
   }

   /**
    * Retrieves the progress lines enabled flag.
    *
    * @return boolean flag
    */
   public boolean getProgressLinesEnabled()
   {
      return (m_progressLinesEnabled);
   }

   /**
    * Retrieves the progress lines font style.
    *
    * @return progress lines font style
    */
   public FontStyle getProgressLinesFontStyle()
   {
      return (m_progressLinesFontStyle);
   }

   /**
    * Retrieves the progress line interval.
    *
    * @return progress line interval
    */
   public Interval getProgressLinesInterval()
   {
      return (m_progressLinesInterval);
   }

   /**
    * Retrieves the progress lines monthly day.
    *
    * @return progress lines monthly day
    */
   public ProgressLineDay getProgressLinesIntervalMonthlyFirstLastDay()
   {
      return (m_progressLinesIntervalMonthlyFirstLastDay);
   }

   /**
    * Retrieves the progress lines month number for the monthly first last type.
    *
    * @return month number
    */
   public int getProgressLinesIntervalMonthlyFirstLastMonthNumber()
   {
      return m_progressLinesIntervalMonthlyFirstLastMonthNumber;
   }

   /**
    * Retrieves the progress lines monthly day number.
    *
    * @return progress lines monthly day number
    */
   public int getProgressLinesIntervalMonthlyDayDayNumber()
   {
      return (m_progressLinesIntervalMonthlyDayDayNumber);
   }

   /**
    * Retrieves the progress lines monthly day of month.
    *
    * @return progress lines monthly day of month
    */
   public boolean getProgressLinesIntervalMonthlyDay()
   {
      return (m_progressLinesIntervalMonthlyDay);
   }

   /**
    * Retrieves the progress line month number for the monthly day type.
    *
    * @return month number
    */
   public int getProgressLinesIntervalMonthlyDayMonthNumber()
   {
      return m_progressLinesIntervalMonthlyDayMonthNumber;
   }

   /**
    * Retrieves the progress lines monthly first flag.
    *
    * @return progress lines monthly first flag
    */
   public boolean getProgressLinesIntervalMonthlyFirstLast()
   {
      return (m_progressLinesIntervalMonthlyFirstLast);
   }

   /**
    * Retrieves the progress lines other line color.
    *
    * @return progress lines other line color
    */
   public Color getProgressLinesOtherLineColor()
   {
      return (m_progressLinesOtherLineColor);
   }

   /**
    * Retrieves the progress lines other line style.
    *
    * @return progress lines other line style
    */
   public LineStyle getProgressLinesOtherLineStyle()
   {
      return (m_progressLinesOtherLineStyle);
   }

   /**
    * Retrieves the progress lines other progress point color.
    *
    * @return progress lines other progress point color
    */
   public Color getProgressLinesOtherProgressPointColor()
   {
      return (m_progressLinesOtherProgressPointColor);
   }

   /**
    * Retrieves the progress lines other progress point shape.
    *
    * @return progress lines other progress point shape
    */
   public int getProgressLinesOtherProgressPointShape()
   {
      return (m_progressLinesOtherProgressPointShape);
   }

   /**
    * Retrieves the progress lines show date flag.
    *
    * @return progress lines show date flag
    */
   public boolean getProgressLinesShowDate()
   {
      return (m_progressLinesShowDate);
   }

   /**
    * Retrieves the progress lines weekly week number.
    *
    * @return progress lines weekly week number
    */
   public int getProgressLinesIntervalWeekleyWeekNumber()
   {
      return (m_progressLinesIntervalWeekleyWeekNumber);
   }

   /**
    * Retrieves the progress lines weekly day.
    * Note that this is designed to be used with the constants defined
    * by the Day class, for example use Day.MONDAY.getValue() as the
    * index into this array for the Monday flag.
    *
    * @return progress lines weekly day
    */
   public boolean[] getProgressLinesIntervalWeeklyDay()
   {
      return (m_progressLinesIntervalWeeklyDay);
   }

   /**
    * This method maps the encoded height of a Gantt bar to
    * the height in pixels.
    *
    * @param height encoded height
    * @return height in pixels
    */
   protected int mapGanttBarHeight(int height)
   {
      switch (height)
      {
         case 0:
         {
            height = 6;
            break;
         }

         case 1:
         {
            height = 8;
            break;
         }

         case 2:
         {
            height = 10;
            break;
         }

         case 3:
         {
            height = 12;
            break;
         }

         case 4:
         {
            height = 14;
            break;
         }

         case 5:
         {
            height = 18;
            break;
         }

         case 6:
         {
            height = 24;
            break;
         }
      }

      return (height);
   }

   /**
    * Retrieve font details from a block of property data.
    *
    * @param data property data
    * @param offset offset into property data
    * @param fontBases map of font bases
    * @return FontStyle instance
    */
   protected FontStyle getFontStyle(byte[] data, int offset, Map<Integer, FontBase> fontBases)
   {
      Integer index = Integer.valueOf(MPPUtility.getByte(data, offset));
      FontBase fontBase = fontBases.get(index);
      int style = MPPUtility.getByte(data, offset + 1);
      ColorType color = ColorType.getInstance(MPPUtility.getByte(data, offset + 2));

      boolean bold = ((style & 0x01) != 0);
      boolean italic = ((style & 0x02) != 0);
      boolean underline = ((style & 0x04) != 0);

      //System.out.println(fontStyle);
      return new FontStyle(fontBase, italic, bold, underline, false, color.getColor(), null, BackgroundPattern.SOLID);
   }

   /**
    * Retrieve column font details from a block of property data.
    *
    * @param file parent file
    * @param data property data
    * @param offset offset into property data
    * @param fontBases map of font bases
    * @return ColumnFontStyle instance
    */
   protected TableFontStyle getColumnFontStyle(ProjectFile file, byte[] data, int offset, Map<Integer, FontBase> fontBases)
   {
      int uniqueID = ByteArrayHelper.getInt(data, offset);
      FieldType fieldType = FieldTypeHelper.getInstance(file, ByteArrayHelper.getInt(data, offset + 4));
      Integer index = Integer.valueOf(MPPUtility.getByte(data, offset + 8));
      int style = MPPUtility.getByte(data, offset + 9);
      ColorType color = ColorType.getInstance(MPPUtility.getByte(data, offset + 10));
      int change = MPPUtility.getByte(data, offset + 12);

      FontBase fontBase = fontBases.get(index);

      boolean bold = ((style & 0x01) != 0);
      boolean italic = ((style & 0x02) != 0);
      boolean underline = ((style & 0x04) != 0);

      boolean boldChanged = ((change & 0x01) != 0);
      boolean underlineChanged = ((change & 0x02) != 0);
      boolean italicChanged = ((change & 0x04) != 0);
      boolean colorChanged = ((change & 0x08) != 0);
      boolean fontChanged = ((change & 0x10) != 0);
      boolean backgroundColorChanged = (uniqueID == -1);
      boolean backgroundPatternChanged = (uniqueID == -1);

      return (new TableFontStyle(uniqueID, fieldType, fontBase, italic, bold, underline, false, color.getColor(), Color.BLACK, BackgroundPattern.TRANSPARENT, italicChanged, boldChanged, underlineChanged, false, colorChanged, fontChanged, backgroundColorChanged, backgroundPatternChanged));
   }

   /**
    * Retrieves a list of all auto filters associated with this view.
    *
    * @return list of filter instances
    */
   public List<Filter> getAutoFilters()
   {
      return (m_autoFilters);
   }

   /**
    * Retrieves the auto filter definition associated with an
    * individual column. Returns null if there is no filter defined for
    * the supplied column type.
    *
    * @param type field type
    * @return filter instance
    */
   public Filter getAutoFilterByType(FieldType type)
   {
      return m_autoFiltersByType.get(type);
   }

   /**
    * Set the array of Gantt Bar Styles, and populate the ID map for these styles.
    *
    * @param barStyles Gantt Bar Styles array
    */
   protected void populateBarStyles(GanttBarStyle[] barStyles)
   {
      m_barStyles = barStyles;
      m_barStylesMap = new HashMap<>();
      Arrays.stream(m_barStyles).forEach(style -> m_barStylesMap.computeIfAbsent(style.getID(), k -> new ArrayList<>()).add(style));
   }

   /**
    * Generate a string representation of this instance.
    *
    * @return string representation of this instance
    */
   @Override public String toString()
   {
      ByteArrayOutputStream os = new ByteArrayOutputStream();
      PrintWriter pw = new PrintWriter(os);
      pw.println("[GanttChartView");
      pw.println("   " + super.toString());

      pw.println("   highlightedTasksFontStyle=" + m_highlightedTasksFontStyle);
      pw.println("   rowAndColumnFontStyle=" + m_rowAndColumnFontStyle);
      pw.println("   nonCriticalTasksFontStyle=" + m_nonCriticalTasksFontStyle);
      pw.println("   criticalTasksFontStyle=" + m_criticalTasksFontStyle);
      pw.println("   summaryTasksFontStyle=" + m_summaryTasksFontStyle);
      pw.println("   milestoneTasksFontStyle=" + m_milestoneTasksFontStyle);
      pw.println("   topTimescaleFontStyle=" + m_topTimescaleFontStyle);
      pw.println("   middleTimescaleFontStyle=" + m_middleTimescaleFontStyle);
      pw.println("   bottomTimescaleFontStyle=" + m_bottomTimescaleFontStyle);
      pw.println("   barTextLeftFontStyle=" + m_barTextLeftFontStyle);
      pw.println("   barTextRightFontStyle=" + m_barTextRightFontStyle);
      pw.println("   barTextTopFontStyle=" + m_barTextTopFontStyle);
      pw.println("   barTextBottomFontStyle=" + m_barTextBottomFontStyle);
      pw.println("   barTextInsideFontStyle=" + m_barTextInsideFontStyle);
      pw.println("   markedTasksFontStyle=" + m_markedTasksFontStyle);
      pw.println("   projectSummaryTasksFontStyle=" + m_projectSummaryTasksFontStyle);
      pw.println("   externalTasksFontStyle=" + m_externalTasksFontStyle);

      pw.println("   SheetRowsGridLines=" + m_sheetRowsGridLines);
      pw.println("   SheetColumnsGridLines=" + m_sheetColumnsGridLines);
      pw.println("   TitleVerticalGridLines=" + m_titleVerticalGridLines);
      pw.println("   TitleHorizontalGridLines=" + m_titleHorizontalGridLines);
      pw.println("   TopTierColumnGridLines=" + m_topTierColumnGridLines);
      pw.println("   MiddleTierColumnGridLines=" + m_middleTierColumnGridLines);
      pw.println("   BottomTierColumnGridLines=" + m_bottomTierColumnGridLines);
      pw.println("   GanttRowsGridLines=" + m_ganttRowsGridLines);
      pw.println("   BarRowsGridLines=" + m_barRowsGridLines);
      pw.println("   CurrentDateGridLines=" + m_currentDateGridLines);
      pw.println("   PageBreakGridLines=" + m_pageBreakGridLines);
      pw.println("   ProjectStartGridLines=" + m_projectStartGridLines);
      pw.println("   ProjectFinishGridLines=" + m_projectFinishGridLines);
      pw.println("   StatusDateGridLines=" + m_statusDateGridLines);
      pw.println("   GanttBarHeight=" + m_ganttBarHeight);
      pw.println("   TimescaleTopTier=" + m_timescaleTopTier);
      pw.println("   TimescaleMiddleTier=" + m_timescaleMiddleTier);
      pw.println("   TimescaleBottomTier=" + m_timescaleBottomTier);
      pw.println("   TimescaleSeparator=" + m_timescaleScaleSeparator);
      pw.println("   TimescaleSize=" + m_timescaleSize + "%");
      pw.println("   NonWorkingDaysCalendarName=" + m_nonWorkingDaysCalendarName);
      pw.println("   NonWorkingColor=" + m_nonWorkingColor);
      pw.println("   NonWorkingPattern=" + m_nonWorkingPattern);
      pw.println("   NonWorkingStyle=" + m_nonWorkingStyle);
      pw.println("   ShowDrawings=" + m_showDrawings);
      pw.println("   RoundBarsToWholeDays=" + m_roundBarsToWholeDays);
      pw.println("   ShowBarSplits=" + m_showBarSplits);
      pw.println("   AlwaysRollupGanttBars=" + m_alwaysRollupGanttBars);
      pw.println("   HideRollupBarsWhenSummaryExpanded=" + m_hideRollupBarsWhenSummaryExpanded);
      pw.println("   BarDateFormat=" + m_barDateFormat);
      pw.println("   LinkStyle=" + m_linkStyle);

      pw.println("   ProgressLinesEnabled=" + m_progressLinesEnabled);
      pw.println("   ProgressLinesAtCurrentDate=" + m_progressLinesAtCurrentDate);
      pw.println("   ProgressLinesAtRecurringIntervals=" + m_progressLinesAtRecurringIntervals);
      pw.println("   ProgressLinesInterval=" + m_progressLinesInterval);
      pw.println("   ProgressLinesDailyDayNumber=" + m_progressLinesIntervalDailyDayNumber);
      pw.println("   ProgressLinesDailyWorkday=" + m_progressLinesIntervalDailyWorkday);

      pw.print("   ProgressLinesWeeklyDay=[");
      for (int loop = 0; loop < m_progressLinesIntervalWeeklyDay.length; loop++)
      {
         if (loop != 0)
         {
            pw.print(",");
         }
         pw.print(m_progressLinesIntervalWeeklyDay[loop]);
      }
      pw.println("]");

      pw.println("   ProgressLinesWeeklyWeekNumber=" + m_progressLinesIntervalWeekleyWeekNumber);
      pw.println("   ProgressLinesMonthlyDayOfMonth=" + m_progressLinesIntervalMonthlyDay);
      pw.println("   ProgressLinesMonthDayNumber=" + m_progressLinesIntervalMonthlyDayDayNumber);
      pw.println("   ProgressLinesMonthlyDay=" + m_progressLinesIntervalMonthlyFirstLastDay);
      pw.println("   ProgressLinesMonthlyFirst=" + m_progressLinesIntervalMonthlyFirstLast);
      pw.println("   ProgressLinesBeginAtProjectStart=" + m_progressLinesBeginAtProjectStart);
      pw.println("   ProgressLinesBeginAtDate=" + m_progressLinesBeginAtDate);
      pw.println("   ProgressLinesDisplaySelected=" + m_progressLinesDisplaySelected);

      pw.print("   ProgressLinesDisplaySelectedDates=[");
      if (m_progressLinesDisplaySelectedDates != null)
      {
         for (int loop = 0; loop < m_progressLinesDisplaySelectedDates.length; loop++)
         {
            if (loop != 0)
            {
               pw.print(",");
            }
            pw.print(m_progressLinesDisplaySelectedDates[loop]);
         }
      }
      pw.println("]");

      pw.println("   ProgressLinesActualPlan=" + m_progressLinesActualPlan);
      pw.println("   ProgressLinesDisplayType=" + m_progressLinesDisplayType);
      pw.println("   ProgressLinesShowDate=" + m_progressLinesShowDate);
      pw.println("   ProgressLinesDateFormat=" + m_progressLinesDateFormat);
      pw.println("   ProgressLinesFontStyle=" + m_progressLinesFontStyle);
      pw.println("   ProgressLinesCurrentLineColor=" + m_progressLinesCurrentLineColor);
      pw.println("   ProgressLinesCurrentLineStyle=" + m_progressLinesCurrentLineStyle);
      pw.println("   ProgressLinesCurrentProgressPointColor=" + m_progressLinesCurrentProgressPointColor);
      pw.println("   ProgressLinesCurrentProgressPointShape=" + m_progressLinesCurrentProgressPointShape);
      pw.println("   ProgressLinesOtherLineColor=" + m_progressLinesOtherLineColor);
      pw.println("   ProgressLinesOtherLineStyle=" + m_progressLinesOtherLineStyle);
      pw.println("   ProgressLinesOtherProgressPointColor=" + m_progressLinesOtherProgressPointColor);
      pw.println("   ProgressLinesOtherProgressPointShape=" + m_progressLinesOtherProgressPointShape);

      pw.println("   TableWidth=" + m_tableWidth);
      pw.println("   DefaultFilterName=" + m_defaultFilterName);
      pw.println("   GroupName=" + m_groupName);
      pw.println("   HighlightFilter=" + m_highlightFilter);
      pw.println("   ShowInMenu=" + m_showInMenu);

      if (m_tableFontStyles != null)
      {
         for (TableFontStyle tableFontStyle : m_tableFontStyles)
         {
            pw.println("   ColumnFontStyle=" + tableFontStyle);
         }
      }

      if (m_barStyles != null)
      {
         for (GanttBarStyle barStyle : m_barStyles)
         {
            pw.println("   BarStyle=" + barStyle);
         }
      }

      for (GanttBarStyleException barStyleException : m_barStyleExceptions)
      {
         pw.println("   BarStyleException=" + barStyleException);
      }

      if (!m_autoFilters.isEmpty())
      {
         for (Filter f : m_autoFilters)
         {
            pw.println("   AutoFilter=" + f);
         }
      }

      pw.println("]");
      pw.flush();
      return (os.toString());
   }

   protected GridLines m_sheetRowsGridLines;
   protected GridLines m_sheetColumnsGridLines;
   protected GridLines m_titleVerticalGridLines;
   protected GridLines m_titleHorizontalGridLines;
   protected GridLines m_middleTierColumnGridLines;
   protected GridLines m_bottomTierColumnGridLines;
   protected GridLines m_ganttRowsGridLines;
   protected GridLines m_barRowsGridLines;
   protected GridLines m_currentDateGridLines;
   protected GridLines m_pageBreakGridLines;
   protected GridLines m_projectStartGridLines;
   protected GridLines m_projectFinishGridLines;
   protected GridLines m_statusDateGridLines;
   protected GridLines m_topTierColumnGridLines;

   protected int m_ganttBarHeight;

   protected TimescaleTier m_timescaleTopTier;
   protected TimescaleTier m_timescaleMiddleTier;
   protected TimescaleTier m_timescaleBottomTier;
   protected boolean m_timescaleScaleSeparator;
   protected int m_timescaleSize;
   protected int m_timescaleShowTiers;

   protected String m_nonWorkingDaysCalendarName;
   protected Color m_nonWorkingColor;
   protected ChartPattern m_nonWorkingPattern;
   protected NonWorkingTimeStyle m_nonWorkingStyle;

   protected boolean m_showDrawings;
   protected boolean m_roundBarsToWholeDays;
   protected boolean m_showBarSplits;
   protected boolean m_alwaysRollupGanttBars;
   protected boolean m_hideRollupBarsWhenSummaryExpanded;
   protected GanttBarDateFormat m_barDateFormat;
   protected LinkStyle m_linkStyle;

   protected GanttBarStyle[] m_barStyles;
   protected Map<Integer, List<GanttBarStyle>> m_barStylesMap;
   protected GanttBarStyleException[] m_barStyleExceptions;

   private int m_tableWidth;
   private String m_defaultFilterName;
   private String m_groupName;
   private boolean m_highlightFilter;
   private final boolean m_showInMenu;

   protected FontStyle m_highlightedTasksFontStyle;
   protected FontStyle m_rowAndColumnFontStyle;
   protected FontStyle m_nonCriticalTasksFontStyle;
   protected FontStyle m_criticalTasksFontStyle;
   protected FontStyle m_summaryTasksFontStyle;
   protected FontStyle m_milestoneTasksFontStyle;
   protected FontStyle m_topTimescaleFontStyle;
   protected FontStyle m_middleTimescaleFontStyle;
   protected FontStyle m_bottomTimescaleFontStyle;
   protected FontStyle m_barTextLeftFontStyle;
   protected FontStyle m_barTextRightFontStyle;
   protected FontStyle m_barTextTopFontStyle;
   protected FontStyle m_barTextBottomFontStyle;
   protected FontStyle m_barTextInsideFontStyle;
   protected FontStyle m_markedTasksFontStyle;
   protected FontStyle m_projectSummaryTasksFontStyle;
   protected FontStyle m_externalTasksFontStyle;

   protected TableFontStyle[] m_tableFontStyles;

   protected boolean m_progressLinesEnabled;
   protected boolean m_progressLinesAtCurrentDate;
   protected boolean m_progressLinesAtRecurringIntervals;
   protected Interval m_progressLinesInterval;
   protected int m_progressLinesIntervalDailyDayNumber;
   protected boolean m_progressLinesIntervalDailyWorkday;
   protected final boolean[] m_progressLinesIntervalWeeklyDay = new boolean[8];
   protected int m_progressLinesIntervalWeekleyWeekNumber;
   protected boolean m_progressLinesIntervalMonthlyDay;
   protected int m_progressLinesIntervalMonthlyDayDayNumber;
   protected int m_progressLinesIntervalMonthlyDayMonthNumber;
   protected ProgressLineDay m_progressLinesIntervalMonthlyFirstLastDay;
   protected boolean m_progressLinesIntervalMonthlyFirstLast;
   protected int m_progressLinesIntervalMonthlyFirstLastMonthNumber;
   protected boolean m_progressLinesBeginAtProjectStart;
   protected LocalDateTime m_progressLinesBeginAtDate;
   protected boolean m_progressLinesDisplaySelected;
   protected LocalDateTime[] m_progressLinesDisplaySelectedDates;
   protected boolean m_progressLinesActualPlan;
   protected int m_progressLinesDisplayType;
   protected boolean m_progressLinesShowDate;
   protected int m_progressLinesDateFormat;
   protected FontStyle m_progressLinesFontStyle;
   protected Color m_progressLinesCurrentLineColor;
   protected LineStyle m_progressLinesCurrentLineStyle;
   protected Color m_progressLinesCurrentProgressPointColor;
   protected int m_progressLinesCurrentProgressPointShape;
   protected Color m_progressLinesOtherLineColor;
   protected LineStyle m_progressLinesOtherLineStyle;
   protected Color m_progressLinesOtherProgressPointColor;
   protected int m_progressLinesOtherProgressPointShape;
   protected final List<Filter> m_autoFilters = new ArrayList<>();
   protected final Map<FieldType, Filter> m_autoFiltersByType = new HashMap<>();

   private final FilterContainer m_filters;

   protected static final Integer VIEW_PROPERTIES = Integer.valueOf(574619656);
   protected static final Integer TIMESCALE_PROPERTIES = Integer.valueOf(574619678);
   private static final Integer TABLE_PROPERTIES = Integer.valueOf(574619655);
   private static final Integer FILTER_NAME = Integer.valueOf(574619659);
   private static final Integer GROUP_NAME = Integer.valueOf(574619672);
   private static final Integer COLUMN_PROPERTIES = Integer.valueOf(574619660);
   private static final Integer PROGRESS_LINE_PROPERTIES = Integer.valueOf(574619671);
   private static final Integer AUTO_FILTER_PROPERTIES = Integer.valueOf(574619669);
}
