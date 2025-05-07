/*
 * file:       GanttChartView14.java
 * author:     Jon Iles
 * copyright:  (c) Packwood Software 2010
 * date:       16/04/2010
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
import java.util.Map;

import java.time.DayOfWeek;

import org.mpxj.common.ByteArrayHelper;
import org.mpxj.common.DayOfWeekHelper;
import org.mpxj.FieldType;
import org.mpxj.Filter;
import org.mpxj.GenericCriteria;
import org.mpxj.ProjectFile;
import org.mpxj.common.FieldTypeHelper;

/**
 * This class represents the set of properties used to define the appearance
 * of a Gantt chart view in MS Project.
 */
public final class GanttChartView14 extends GanttChartView
{
   @Override protected Integer getPropertiesID()
   {
      return (PROPERTIES);
   }

   @Override protected void processDefaultBarStyles(Props props)
   {
      populateBarStyles(new GanttBarStyleFactory14().processDefaultStyles(m_file, props));
   }

   @Override protected void processExceptionBarStyles(Props props)
   {
      GanttBarStyleFactory f = new GanttBarStyleFactory14();
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
      CriteriaReader criteria = new FilterCriteriaReader14();

      //
      // 16 byte header
      // followed by 4 bytes = field type
      // followed by 2 byte block size
      for (int loop = 0; loop < filterCount; loop++)
      {
         //
         // Invalid filter definition?
         //
         if (offset + 6 > data.length)
         {
            break;
         }

         FieldType field = getFieldType(data, offset);
         int blockSize = ByteArrayHelper.getShort(data, offset + 4);

         //
         // Steelray 12335: the block size may be zero
         //
         if (blockSize == 0)
         {
            break;
         }

         //System.out.println(ByteArrayHelper.hexdump(data, offset, 32, false));

         // may need to sort this out
         GenericCriteria c = criteria.process(m_file, data, offset + 12, -1, null, null, null);
         //System.out.println(c);

         Filter filter = new Filter();
         filter.setCriteria(c);
         m_autoFilters.add(filter);
         m_autoFiltersByType.put(field, filter);

         //
         // Move to the next filter
         //
         offset += blockSize;
      }
   }

   /**
    * Retrieves a field type from a location in a data block.
    *
    * @param data data block
    * @param offset offset into data block
    * @return field type
    */
   private FieldType getFieldType(byte[] data, int offset)
   {
      int fieldIndex = ByteArrayHelper.getInt(data, offset);
      return FieldTypeHelper.mapTextFields(FieldTypeHelper.getInstance(m_file, fieldIndex));
   }

   @Override protected void processViewProperties(Map<Integer, FontBase> fontBases, Props props)
   {
      byte[] viewPropertyData = props.getByteArray(VIEW_PROPERTIES);
      if (viewPropertyData != null && viewPropertyData.length > 41360)
      {
         //MPPUtility.fileDump("c:\\temp\\props.txt", ByteArrayHelper.hexdump(viewPropertyData, false, 16, "").getBytes());

         m_highlightedTasksFontStyle = getFontStyle(viewPropertyData, 26, fontBases, false);
         m_rowAndColumnFontStyle = getFontStyle(viewPropertyData, 58, fontBases, false);
         m_nonCriticalTasksFontStyle = getFontStyle(viewPropertyData, 90, fontBases, false);
         m_criticalTasksFontStyle = getFontStyle(viewPropertyData, 122, fontBases, false);
         m_summaryTasksFontStyle = getFontStyle(viewPropertyData, 154, fontBases, false);
         m_milestoneTasksFontStyle = getFontStyle(viewPropertyData, 186, fontBases, false);
         m_middleTimescaleFontStyle = getFontStyle(viewPropertyData, 218, fontBases, false);
         m_bottomTimescaleFontStyle = getFontStyle(viewPropertyData, 250, fontBases, false);
         m_barTextLeftFontStyle = getFontStyle(viewPropertyData, 282, fontBases, false);
         m_barTextRightFontStyle = getFontStyle(viewPropertyData, 314, fontBases, false);
         m_barTextTopFontStyle = getFontStyle(viewPropertyData, 346, fontBases, false);
         m_barTextBottomFontStyle = getFontStyle(viewPropertyData, 378, fontBases, false);
         m_barTextInsideFontStyle = getFontStyle(viewPropertyData, 410, fontBases, false);
         m_markedTasksFontStyle = getFontStyle(viewPropertyData, 442, fontBases, false);
         m_projectSummaryTasksFontStyle = getFontStyle(viewPropertyData, 474, fontBases, false);
         m_externalTasksFontStyle = getFontStyle(viewPropertyData, 506, fontBases, false);
         m_topTimescaleFontStyle = getFontStyle(viewPropertyData, 538, fontBases, false);

         m_sheetRowsGridLines = getGridLines(viewPropertyData, 667);
         m_sheetColumnsGridLines = getGridLines(viewPropertyData, 697);
         m_titleVerticalGridLines = getGridLines(viewPropertyData, 727);
         m_titleHorizontalGridLines = getGridLines(viewPropertyData, 757);
         m_middleTierColumnGridLines = getGridLines(viewPropertyData, 787);
         m_bottomTierColumnGridLines = getGridLines(viewPropertyData, 817);
         m_ganttRowsGridLines = getGridLines(viewPropertyData, 847);
         m_barRowsGridLines = getGridLines(viewPropertyData, 877);
         m_currentDateGridLines = getGridLines(viewPropertyData, 907);
         m_pageBreakGridLines = getGridLines(viewPropertyData, 937);
         m_projectStartGridLines = getGridLines(viewPropertyData, 967);
         m_projectFinishGridLines = getGridLines(viewPropertyData, 997);
         m_statusDateGridLines = getGridLines(viewPropertyData, 1027);
         m_topTierColumnGridLines = getGridLines(viewPropertyData, 1057);

         m_nonWorkingDaysCalendarName = MPPUtility.getUnicodeString(viewPropertyData, 1422);
         m_nonWorkingColor = MPPUtility.getColor(viewPropertyData, 2223);
         m_nonWorkingPattern = ChartPattern.getInstance(viewPropertyData[2235]);
         m_nonWorkingStyle = NonWorkingTimeStyle.getInstance(viewPropertyData[2222]);

         m_timescaleShowTiers = viewPropertyData[41255];
         m_timescaleSize = viewPropertyData[1180];

         int flags = viewPropertyData[1086];
         m_timescaleScaleSeparator = (flags & 0x04) != 0;

         m_timescaleTopTier = new TimescaleTier();

         m_timescaleTopTier.setTickLines(viewPropertyData[41349] != 0);
         m_timescaleTopTier.setUsesFiscalYear((viewPropertyData[41361] & 0x01) != 0);
         m_timescaleTopTier.setUnits(TimescaleUnits.getInstance(viewPropertyData[41311]));
         m_timescaleTopTier.setCount(viewPropertyData[41313]);
         m_timescaleTopTier.setFormat(TimescaleFormat.getInstance(ByteArrayHelper.getShort(viewPropertyData, 41315)));
         m_timescaleTopTier.setAlignment(TimescaleAlignment.getInstance(viewPropertyData[41317]));

         m_timescaleMiddleTier = new TimescaleTier();
         m_timescaleMiddleTier.setTickLines((flags & 0x01) != 0);
         m_timescaleMiddleTier.setUsesFiscalYear((flags & 0x08) != 0);
         m_timescaleMiddleTier.setUnits(TimescaleUnits.getInstance(viewPropertyData[1152]));
         m_timescaleMiddleTier.setCount(viewPropertyData[1156]);
         m_timescaleMiddleTier.setFormat(TimescaleFormat.getInstance(ByteArrayHelper.getShort(viewPropertyData, 1160)));
         m_timescaleMiddleTier.setAlignment(TimescaleAlignment.getInstance(viewPropertyData[1166]));

         m_timescaleBottomTier = new TimescaleTier();
         m_timescaleBottomTier.setTickLines((flags & 0x02) != 0);
         m_timescaleBottomTier.setUsesFiscalYear((flags & 0x10) != 0);
         m_timescaleBottomTier.setUnits(TimescaleUnits.getInstance(viewPropertyData[1154]));
         m_timescaleBottomTier.setCount(viewPropertyData[1158]);
         m_timescaleBottomTier.setFormat(TimescaleFormat.getInstance(ByteArrayHelper.getShort(viewPropertyData, 1162)));
         m_timescaleBottomTier.setAlignment(TimescaleAlignment.getInstance(viewPropertyData[1164]));

         m_showDrawings = (viewPropertyData[2237] != 0);
         m_roundBarsToWholeDays = (viewPropertyData[2239] != 0);
         m_showBarSplits = (viewPropertyData[2241] != 0);
         m_alwaysRollupGanttBars = (viewPropertyData[2251] != 0);
         m_hideRollupBarsWhenSummaryExpanded = (viewPropertyData[2253] != 0);
         m_ganttBarHeight = mapGanttBarHeight(MPPUtility.getByte(viewPropertyData, 2244));

         m_barDateFormat = GanttBarDateFormat.getInstance(viewPropertyData[2247] + 1);
         m_linkStyle = LinkStyle.getInstance(viewPropertyData[2236]);
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
      //System.out.println(offset+ ": " + ByteArrayHelper.hexdump(data, offset, 30, false));
      Color normalLineColor = MPPUtility.getColor(data, offset);
      LineStyle normalLineStyle = LineStyle.getInstance(data[offset + 13]);
      int intervalNumber = data[offset + 14];
      LineStyle intervalLineStyle = LineStyle.getInstance(data[offset + 15]);
      Color intervalLineColor = MPPUtility.getColor(data, offset + 16);
      return new GridLines(normalLineColor, normalLineStyle, intervalNumber, intervalLineStyle, intervalLineColor);
   }

   /**
    * Retrieve font details from a block of property data.
    *
    * @param data property data
    * @param offset offset into property data
    * @param fontBases map of font bases
    * @param ignoreBackground set background to default values
    * @return FontStyle instance
    */
   private FontStyle getFontStyle(byte[] data, int offset, Map<Integer, FontBase> fontBases, boolean ignoreBackground)
   {
      //System.out.println(ByteArrayHelper.hexdump(data, offset, 32, false));

      Integer index = Integer.valueOf(MPPUtility.getByte(data, offset));
      FontBase fontBase = fontBases.get(index);
      int style = MPPUtility.getByte(data, offset + 3);
      Color color = MPPUtility.getColor(data, offset + 4);
      Color backgroundColor;
      BackgroundPattern backgroundPattern;

      if (ignoreBackground)
      {
         backgroundColor = null;
         backgroundPattern = BackgroundPattern.SOLID;
      }
      else
      {
         backgroundColor = MPPUtility.getColor(data, offset + 16);
         backgroundPattern = BackgroundPattern.getInstance(ByteArrayHelper.getShort(data, offset + 28));
      }

      boolean bold = ((style & 0x01) != 0);
      boolean italic = ((style & 0x02) != 0);
      boolean underline = ((style & 0x04) != 0);
      boolean strikethrough = ((style & 0x08) != 0);

      //System.out.println(fontStyle);
      return new FontStyle(fontBase, italic, bold, underline, strikethrough, color, backgroundColor, backgroundPattern);
   }

   @Override protected void processTableFontStyles(Map<Integer, FontBase> fontBases, byte[] columnData)
   {
      //MPPUtility.fileDump("c:\\temp\\props.txt", ByteArrayHelper.hexdump(columnData, false, 44, "").getBytes());

      m_tableFontStyles = new TableFontStyle[columnData.length / 44];
      int offset = 0;
      for (int loop = 0; loop < m_tableFontStyles.length; loop++)
      {
         m_tableFontStyles[loop] = getColumnFontStyle(m_file, columnData, offset, fontBases);
         offset += 44;
      }
   }

   @Override protected TableFontStyle getColumnFontStyle(ProjectFile file, byte[] data, int offset, Map<Integer, FontBase> fontBases)
   {
      int uniqueID = ByteArrayHelper.getInt(data, offset);
      FieldType fieldType = FieldTypeHelper.getInstance(file, ByteArrayHelper.getInt(data, offset + 4));
      Integer index = Integer.valueOf(MPPUtility.getByte(data, offset + 8));
      int style = MPPUtility.getByte(data, offset + 11);
      Color color = MPPUtility.getColor(data, offset + 12);
      int change = ByteArrayHelper.getShort(data, offset + 40);
      Color backgroundColor = MPPUtility.getColor(data, offset + 24);
      BackgroundPattern backgroundPattern = BackgroundPattern.getInstance(ByteArrayHelper.getShort(data, offset + 36));

      FontBase fontBase = fontBases.get(index);

      boolean bold = ((style & 0x01) != 0);
      boolean italic = ((style & 0x02) != 0);
      boolean underline = ((style & 0x04) != 0);
      boolean strikethrough = ((style & 0x08) != 0);

      boolean boldChanged = ((change & 0x01) != 0);
      boolean underlineChanged = ((change & 0x02) != 0);
      boolean italicChanged = ((change & 0x04) != 0);
      boolean colorChanged = ((change & 0x08) != 0);
      boolean fontChanged = ((change & 0x10) != 0);
      boolean backgroundColorChanged = ((change & 0x40) != 0);
      boolean backgroundPatternChanged = ((change & 0x80) != 0);
      boolean strikethroughChanged = ((change & 0x100) != 0);

      //System.out.println(tfs);
      return new TableFontStyle(uniqueID, fieldType, fontBase, italic, bold, underline, strikethrough, color, backgroundColor, backgroundPattern, italicChanged, boldChanged, underlineChanged, strikethroughChanged, colorChanged, fontChanged, backgroundColorChanged, backgroundPatternChanged);
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
      m_progressLinesIntervalWeekleyWeekNumber = progressLineData[12];
      m_progressLinesIntervalWeeklyDay[DayOfWeekHelper.getValue(DayOfWeek.SUNDAY)] = (progressLineData[14] != 0);
      m_progressLinesIntervalWeeklyDay[DayOfWeekHelper.getValue(DayOfWeek.MONDAY)] = (progressLineData[16] != 0);
      m_progressLinesIntervalWeeklyDay[DayOfWeekHelper.getValue(DayOfWeek.TUESDAY)] = (progressLineData[18] != 0);
      m_progressLinesIntervalWeeklyDay[DayOfWeekHelper.getValue(DayOfWeek.WEDNESDAY)] = (progressLineData[20] != 0);
      m_progressLinesIntervalWeeklyDay[DayOfWeekHelper.getValue(DayOfWeek.THURSDAY)] = (progressLineData[22] != 0);
      m_progressLinesIntervalWeeklyDay[DayOfWeekHelper.getValue(DayOfWeek.FRIDAY)] = (progressLineData[24] != 0);
      m_progressLinesIntervalWeeklyDay[DayOfWeekHelper.getValue(DayOfWeek.SATURDAY)] = (progressLineData[26] != 0);
      m_progressLinesIntervalMonthlyDay = (progressLineData[32] != 0);
      m_progressLinesIntervalMonthlyDayDayNumber = progressLineData[34];
      m_progressLinesIntervalMonthlyDayMonthNumber = progressLineData[28];
      m_progressLinesIntervalMonthlyFirstLast = (progressLineData[40] == 1);
      m_progressLinesIntervalMonthlyFirstLastDay = ProgressLineDay.getInstance(progressLineData[36]);
      m_progressLinesIntervalMonthlyFirstLastMonthNumber = progressLineData[30];
      m_progressLinesBeginAtProjectStart = (progressLineData[44] != 0);
      m_progressLinesBeginAtDate = MPPUtility.getDate(progressLineData, 46);
      m_progressLinesDisplaySelected = (progressLineData[48] != 0);
      m_progressLinesActualPlan = (progressLineData[52] != 0);
      m_progressLinesDisplayType = ByteArrayHelper.getShort(progressLineData, 54);
      m_progressLinesShowDate = (progressLineData[56] != 0);
      m_progressLinesDateFormat = ByteArrayHelper.getShort(progressLineData, 58);
      m_progressLinesFontStyle = getFontStyle(progressLineData, 60, fontBases, true);
      m_progressLinesCurrentLineColor = MPPUtility.getColor(progressLineData, 92);
      m_progressLinesCurrentLineStyle = LineStyle.getInstance(progressLineData[104]);
      m_progressLinesCurrentProgressPointColor = MPPUtility.getColor(progressLineData, 105);
      m_progressLinesCurrentProgressPointShape = progressLineData[117];
      m_progressLinesOtherLineColor = MPPUtility.getColor(progressLineData, 118);
      m_progressLinesOtherLineStyle = LineStyle.getInstance(progressLineData[130]);
      m_progressLinesOtherProgressPointColor = MPPUtility.getColor(progressLineData, 131);
      m_progressLinesOtherProgressPointShape = progressLineData[143];

      int dateCount = ByteArrayHelper.getShort(progressLineData, 50);
      if (dateCount != 0)
      {
         m_progressLinesDisplaySelectedDates = new LocalDateTime[dateCount];
         int offset = 144;
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
   GanttChartView14(ProjectFile parent, byte[] fixedMeta, byte[] fixedData, Var2Data varData, Map<Integer, FontBase> fontBases)
      throws IOException
   {
      super(parent, fixedMeta, fixedData, varData, fontBases);
   }

   private static final Integer PROPERTIES = Integer.valueOf(6);
}
