/*
 * file:       GanttChartView12.java
 * author:     Jon Iles
 * copyright:  (c) Packwood Software 2006
 * date:       27 September 2006
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
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;

import java.util.List;
import java.util.Map;

import java.time.DayOfWeek;

import org.mpxj.common.ByteArrayHelper;
import org.mpxj.common.DayOfWeekHelper;
import org.mpxj.FieldType;
import org.mpxj.Filter;
import org.mpxj.GenericCriteria;
import org.mpxj.ProjectFile;

/**
 * This class represents the set of properties used to define the appearance
 * of a Gantt chart view in MS Project.
 */
public final class GanttChartView12 extends GanttChartView
{
   @Override protected Integer getPropertiesID()
   {
      return (PROPERTIES);
   }

   @Override protected void processDefaultBarStyles(Props props)
   {
      populateBarStyles(new GanttBarStyleFactoryCommon().processDefaultStyles(m_file, props));
   }

   @Override protected void processExceptionBarStyles(Props props)
   {
      GanttBarStyleFactory f = new GanttBarStyleFactoryCommon();
      m_barStyleExceptions = f.processExceptionStyles(m_file, props);
   }

   @Override protected void processAutoFilters(byte[] data)
   {
      //System.out.println(ByteArrayHelper.hexdump(data, true, 16, ""));

      //
      // 16 byte block header containing the filter count
      //
      int filterCount = ByteArrayHelper.getShort(data, 8);
      int offset = 16;
      CriteriaReader criteria = new FilterCriteriaReader12();
      List<FieldType> fields = new ArrayList<>();

      //
      // Filter data: 24 byte header, plus 80 byte criteria blocks,
      // plus var data. Total block size is specified at the start of the
      // block.
      //
      for (int loop = 0; loop < filterCount; loop++)
      {
         //
         // Invalid filter definition?
         //
         if (offset + 6 > data.length)
         {
            break;
         }

         int blockSize = ByteArrayHelper.getShort(data, offset);

         //
         // Steelray 12335: the block size may be zero
         //
         if (blockSize == 0)
         {
            break;
         }

         //System.out.println(ByteArrayHelper.hexdump(data, offset, blockSize, true, 16, ""));

         int entryOffset = ByteArrayHelper.getShort(data, offset + 12);
         fields.clear();
         GenericCriteria c = criteria.process(m_file, data, offset + 4, entryOffset, null, fields, null);
         //System.out.println(c);

         if (!fields.isEmpty())
         {
            Filter filter = new Filter();
            filter.setCriteria(c);
            m_autoFilters.add(filter);
            m_autoFiltersByType.put(fields.get(0), filter);
         }

         //
         // Move to the next filter
         //
         offset += blockSize;
      }
   }

   @Override protected void processViewProperties(Map<Integer, FontBase> fontBases, Props props)
   {
      byte[] viewPropertyData = props.getByteArray(VIEW_PROPERTIES);
      if (viewPropertyData != null)
      {
         //System.out.println(ByteArrayHelper.hexdump(viewPropertyData, false, 16, ""));

         m_highlightedTasksFontStyle = getFontStyle(viewPropertyData, 26, fontBases);
         m_rowAndColumnFontStyle = getFontStyle(viewPropertyData, 30, fontBases);
         m_nonCriticalTasksFontStyle = getFontStyle(viewPropertyData, 34, fontBases);
         m_criticalTasksFontStyle = getFontStyle(viewPropertyData, 38, fontBases);
         m_summaryTasksFontStyle = getFontStyle(viewPropertyData, 42, fontBases);
         m_milestoneTasksFontStyle = getFontStyle(viewPropertyData, 46, fontBases);
         m_middleTimescaleFontStyle = getFontStyle(viewPropertyData, 50, fontBases);
         m_bottomTimescaleFontStyle = getFontStyle(viewPropertyData, 54, fontBases);
         m_barTextLeftFontStyle = getFontStyle(viewPropertyData, 58, fontBases);
         m_barTextRightFontStyle = getFontStyle(viewPropertyData, 62, fontBases);
         m_barTextTopFontStyle = getFontStyle(viewPropertyData, 66, fontBases);
         m_barTextBottomFontStyle = getFontStyle(viewPropertyData, 70, fontBases);
         m_barTextInsideFontStyle = getFontStyle(viewPropertyData, 74, fontBases);
         m_markedTasksFontStyle = getFontStyle(viewPropertyData, 78, fontBases);
         m_projectSummaryTasksFontStyle = getFontStyle(viewPropertyData, 82, fontBases);
         m_externalTasksFontStyle = getFontStyle(viewPropertyData, 86, fontBases);
         m_topTimescaleFontStyle = getFontStyle(viewPropertyData, 90, fontBases);

         m_sheetRowsGridLines = getGridLines(viewPropertyData, 99);
         m_sheetColumnsGridLines = getGridLines(viewPropertyData, 109);
         m_titleVerticalGridLines = getGridLines(viewPropertyData, 119);
         m_titleHorizontalGridLines = getGridLines(viewPropertyData, 129);
         m_middleTierColumnGridLines = getGridLines(viewPropertyData, 139);
         m_bottomTierColumnGridLines = getGridLines(viewPropertyData, 149);
         m_ganttRowsGridLines = getGridLines(viewPropertyData, 159);
         m_barRowsGridLines = getGridLines(viewPropertyData, 169);
         m_currentDateGridLines = getGridLines(viewPropertyData, 179);
         m_pageBreakGridLines = getGridLines(viewPropertyData, 189);
         m_projectStartGridLines = getGridLines(viewPropertyData, 199);
         m_projectFinishGridLines = getGridLines(viewPropertyData, 209);
         m_statusDateGridLines = getGridLines(viewPropertyData, 219);

         m_nonWorkingDaysCalendarName = MPPUtility.getUnicodeString(viewPropertyData, 352);
         m_nonWorkingColor = ColorType.getInstance(viewPropertyData[1153]).getColor();
         m_nonWorkingPattern = ChartPattern.getInstance(viewPropertyData[1154]);
         m_nonWorkingStyle = NonWorkingTimeStyle.getInstance(viewPropertyData[1152]);

         m_ganttBarHeight = mapGanttBarHeight(MPPUtility.getByte(viewPropertyData, 1163));

         byte flags = viewPropertyData[228];

         m_timescaleMiddleTier = new TimescaleTier();
         m_timescaleMiddleTier.setTickLines((flags & 0x01) != 0);
         m_timescaleMiddleTier.setUsesFiscalYear((flags & 0x08) != 0);
         m_timescaleMiddleTier.setUnits(TimescaleUnits.getInstance(viewPropertyData[242]));
         m_timescaleMiddleTier.setCount(viewPropertyData[246]);
         m_timescaleMiddleTier.setFormat(TimescaleFormat.getInstance(ByteArrayHelper.getShort(viewPropertyData, 250)));
         m_timescaleMiddleTier.setAlignment(TimescaleAlignment.getInstance(viewPropertyData[256] - 32));

         m_timescaleBottomTier = new TimescaleTier();
         m_timescaleBottomTier.setTickLines((flags & 0x02) != 0);
         m_timescaleBottomTier.setUsesFiscalYear((flags & 0x10) != 0);
         m_timescaleBottomTier.setUnits(TimescaleUnits.getInstance(viewPropertyData[244]));
         m_timescaleBottomTier.setCount(viewPropertyData[248]);
         m_timescaleBottomTier.setFormat(TimescaleFormat.getInstance(ByteArrayHelper.getShort(viewPropertyData, 252)));
         m_timescaleBottomTier.setAlignment(TimescaleAlignment.getInstance(viewPropertyData[254] - 32));

         m_timescaleScaleSeparator = (flags & 0x04) != 0;
         m_timescaleSize = viewPropertyData[268];

         m_showDrawings = (viewPropertyData[1156] != 0);
         m_roundBarsToWholeDays = (viewPropertyData[1158] != 0);
         m_showBarSplits = (viewPropertyData[1160] != 0);
         m_alwaysRollupGanttBars = (viewPropertyData[1186] != 0);
         m_hideRollupBarsWhenSummaryExpanded = (viewPropertyData[1188] != 0);
         m_barDateFormat = GanttBarDateFormat.getInstance(viewPropertyData[1182] + 1);
         m_linkStyle = LinkStyle.getInstance(viewPropertyData[1155]);
      }

      byte[] timescaleData = props.getByteArray(TIMESCALE_PROPERTIES);
      if (timescaleData != null)
      {
         m_timescaleTopTier = new TimescaleTier();

         m_timescaleTopTier.setTickLines(timescaleData[48] != 0);
         m_timescaleTopTier.setUsesFiscalYear(timescaleData[60] != 0);
         m_timescaleTopTier.setUnits(TimescaleUnits.getInstance(timescaleData[30]));
         m_timescaleTopTier.setCount(timescaleData[32]);
         m_timescaleTopTier.setFormat(TimescaleFormat.getInstance(ByteArrayHelper.getShort(timescaleData, 34)));
         m_timescaleTopTier.setAlignment(TimescaleAlignment.getInstance(timescaleData[36] - 20));

         m_topTierColumnGridLines = getGridLines(timescaleData, 39);

         m_timescaleShowTiers = timescaleData[0];
      }
   }

   /**
    * Creates a new GridLines instance.
    *
    * @param data data block
    * @param offset offset into data block
    * @return new GridLines instance
    */
   private GridLines getGridLines(byte[] data, int offset)
   {
      Color normalLineColor = ColorType.getInstance(data[offset]).getColor();
      LineStyle normalLineStyle = LineStyle.getInstance(data[offset + 3]);
      int intervalNumber = data[offset + 4];
      LineStyle intervalLineStyle = LineStyle.getInstance(data[offset + 5]);
      Color intervalLineColor = ColorType.getInstance(data[offset + 6]).getColor();
      return new GridLines(normalLineColor, normalLineStyle, intervalNumber, intervalLineStyle, intervalLineColor);
   }

   @Override protected void processTableFontStyles(Map<Integer, FontBase> fontBases, byte[] columnData)
   {
      m_tableFontStyles = new TableFontStyle[columnData.length / 16];
      int offset = 0;
      for (int loop = 0; loop < m_tableFontStyles.length; loop++)
      {
         m_tableFontStyles[loop] = getColumnFontStyle(m_file, columnData, offset, fontBases);
         offset += 16;
      }
   }

   @Override protected void processProgressLines(Map<Integer, FontBase> fontBases, byte[] progressLineData)
   {
      //MPPUtility.fileDump("c:\\temp\\props.txt", ByteArrayHelper.hexdump(progressLineData, false, 16, "").getBytes());
      m_progressLinesEnabled = (progressLineData[0] != 0);
      m_progressLinesAtCurrentDate = (progressLineData[2] != 0);
      m_progressLinesAtRecurringIntervals = (progressLineData[4] != 0);
      m_progressLinesInterval = Interval.getInstance(progressLineData[6]);
      m_progressLinesIntervalDailyDayNumber = progressLineData[8];
      m_progressLinesIntervalDailyWorkday = (progressLineData[10] != 0);
      m_progressLinesIntervalWeeklyDay[DayOfWeekHelper.getValue(DayOfWeek.SUNDAY)] = (progressLineData[14] != 0);
      m_progressLinesIntervalWeeklyDay[DayOfWeekHelper.getValue(DayOfWeek.MONDAY)] = (progressLineData[16] != 0);
      m_progressLinesIntervalWeeklyDay[DayOfWeekHelper.getValue(DayOfWeek.TUESDAY)] = (progressLineData[18] != 0);
      m_progressLinesIntervalWeeklyDay[DayOfWeekHelper.getValue(DayOfWeek.WEDNESDAY)] = (progressLineData[20] != 0);
      m_progressLinesIntervalWeeklyDay[DayOfWeekHelper.getValue(DayOfWeek.THURSDAY)] = (progressLineData[22] != 0);
      m_progressLinesIntervalWeeklyDay[DayOfWeekHelper.getValue(DayOfWeek.FRIDAY)] = (progressLineData[24] != 0);
      m_progressLinesIntervalWeeklyDay[DayOfWeekHelper.getValue(DayOfWeek.SATURDAY)] = (progressLineData[26] != 0);
      m_progressLinesIntervalWeekleyWeekNumber = progressLineData[30];
      m_progressLinesIntervalMonthlyDay = (progressLineData[32] != 0);
      m_progressLinesIntervalMonthlyDayDayNumber = progressLineData[34];
      m_progressLinesIntervalMonthlyDayMonthNumber = progressLineData[28];
      m_progressLinesIntervalMonthlyFirstLastDay = ProgressLineDay.getInstance(progressLineData[36]);
      m_progressLinesIntervalMonthlyFirstLast = (progressLineData[40] == 1);
      m_progressLinesIntervalMonthlyFirstLastMonthNumber = progressLineData[30];
      m_progressLinesBeginAtProjectStart = (progressLineData[44] != 0);
      m_progressLinesBeginAtDate = MPPUtility.getDate(progressLineData, 46);
      m_progressLinesDisplaySelected = (progressLineData[48] != 0);
      m_progressLinesActualPlan = (progressLineData[52] != 0);
      m_progressLinesDisplayType = ByteArrayHelper.getShort(progressLineData, 54);
      m_progressLinesShowDate = (progressLineData[56] != 0);
      m_progressLinesDateFormat = ByteArrayHelper.getShort(progressLineData, 58);
      m_progressLinesFontStyle = getFontStyle(progressLineData, 60, fontBases);
      m_progressLinesCurrentLineColor = ColorType.getInstance(progressLineData[64]).getColor();
      m_progressLinesCurrentLineStyle = LineStyle.getInstance(progressLineData[65]);
      m_progressLinesCurrentProgressPointColor = ColorType.getInstance(progressLineData[66]).getColor();
      m_progressLinesCurrentProgressPointShape = progressLineData[67];
      m_progressLinesOtherLineColor = ColorType.getInstance(progressLineData[68]).getColor();
      m_progressLinesOtherLineStyle = LineStyle.getInstance(progressLineData[69]);
      m_progressLinesOtherProgressPointColor = ColorType.getInstance(progressLineData[70]).getColor();
      m_progressLinesOtherProgressPointShape = progressLineData[71];

      int dateCount = ByteArrayHelper.getShort(progressLineData, 50);
      if (dateCount != 0)
      {
         m_progressLinesDisplaySelectedDates = new LocalDateTime[dateCount];
         int offset = 72;
         int count = 0;
         while (count < dateCount && offset < progressLineData.length)
         {
            m_progressLinesDisplaySelectedDates[count] = MPPUtility.getDate(progressLineData, offset);
            offset += 2;
            ++count;
         }
      }
   }

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
   GanttChartView12(ProjectFile parent, byte[] fixedMeta, byte[] fixedData, Var2Data varData, Map<Integer, FontBase> fontBases)
      throws IOException
   {
      super(parent, fixedMeta, fixedData, varData, fontBases);
   }

   private static final Integer PROPERTIES = Integer.valueOf(6);
}
