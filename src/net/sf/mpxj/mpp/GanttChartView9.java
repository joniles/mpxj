/*
 * file:       GanttChartView9.java
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

package net.sf.mpxj.mpp;

import java.awt.Color;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import net.sf.mpxj.FieldType;
import net.sf.mpxj.Filter;
import net.sf.mpxj.GenericCriteria;
import net.sf.mpxj.ProjectFile;

/**
 * This class represents the set of properties used to define the appearance
 * of a Gantt chart view in MS Project.
 */
public final class GanttChartView9 extends GanttChartView
{
   /**
    * {@inheritDoc}
    */
   @Override protected Integer getPropertiesID()
   {
      return (PROPERTIES);
   }

   /**
    * {@inheritDoc}
    */
   @Override protected void processDefaultBarStyles(Props props)
   {
      GanttBarStyleFactory f = new GanttBarStyleFactoryCommon();
      m_barStyles = f.processDefaultStyles(props);
   }

   /**
    * {@inheritDoc}
    */
   @Override protected void processExceptionBarStyles(Props props)
   {
      GanttBarStyleFactory f = new GanttBarStyleFactoryCommon();
      m_barStyleExceptions = f.processExceptionStyles(props);
   }

   /**
    * {@inheritDoc}
    */
   @Override protected void processAutoFilters(byte[] data)
   {
      //System.out.println(MPPUtility.hexdump(data, true, 16, ""));

      //
      // 16 byte block header containing the filter count
      //
      int filterCount = MPPUtility.getShort(data, 8);
      int offset = 16;
      CriteriaReader criteria = new FilterCriteriaReader9();
      List<FieldType> fields = new LinkedList<FieldType>();

      //
      // Filter data: 24 byte header, plus 80 byte criteria blocks, 
      // plus var data. Total block size is specified at the start of the
      // block.
      //
      for (int loop = 0; loop < filterCount; loop++)
      {
         int blockSize = MPPUtility.getShort(data, offset);

         //
         // Steelray 12335: the block size may be zero
         //
         if (blockSize == 0)
         {
            break;
         }

         //System.out.println(MPPUtility.hexdump(data, offset, blockSize, true, 16, ""));

         int entryOffset = MPPUtility.getShort(data, offset + 12);
         fields.clear();
         GenericCriteria c = criteria.process(m_parent, data, offset + 4, entryOffset, null, fields, null);
         //System.out.println(c);

         Filter filter = new Filter();
         filter.setCriteria(c);
         m_autoFilters.add(filter);
         m_autoFiltersByType.put(fields.get(0), filter);

         //
         // Move to the next filter
         //
         offset += blockSize;
      }
   }

   /**
    * {@inheritDoc}
    */
   @Override protected void processViewProperties(Map<Integer, FontBase> fontBases, Props props)
   {
      byte[] viewPropertyData = props.getByteArray(VIEW_PROPERTIES);
      if (viewPropertyData != null)
      {
         //System.out.println(MPPUtility.hexdump(viewPropertyData, false, 16, ""));

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
         m_timescaleMiddleTier.setFormat(TimescaleFormat.getInstance(viewPropertyData[250]));
         m_timescaleMiddleTier.setAlignment(TimescaleAlignment.getInstance(viewPropertyData[256] - 32));

         m_timescaleBottomTier = new TimescaleTier();
         m_timescaleBottomTier.setTickLines((flags & 0x02) != 0);
         m_timescaleBottomTier.setUsesFiscalYear((flags & 0x10) != 0);
         m_timescaleBottomTier.setUnits(TimescaleUnits.getInstance(viewPropertyData[244]));
         m_timescaleBottomTier.setCount(viewPropertyData[248]);
         m_timescaleBottomTier.setFormat(TimescaleFormat.getInstance(viewPropertyData[252]));
         m_timescaleBottomTier.setAlignment(TimescaleAlignment.getInstance(viewPropertyData[254] - 32));

         m_timescaleSeparator = (flags & 0x04) != 0;
         m_timescaleSize = viewPropertyData[268];

         m_showDrawings = (viewPropertyData[1156] != 0);
         m_roundBarsToWholeDays = (viewPropertyData[1158] != 0);
         m_showBarSplits = (viewPropertyData[1160] != 0);
         m_alwaysRollupGanttBars = (viewPropertyData[1186] != 0);
         m_hideRollupBarsWhenSummaryExpanded = (viewPropertyData[1188] != 0);
         m_barDateFormat = viewPropertyData[1182];
         m_linkStyle = LinkStyle.getInstance(viewPropertyData[1155]);
      }

      byte[] topTierData = props.getByteArray(TOP_TIER_PROPERTIES);
      if (topTierData != null)
      {
         m_timescaleTopTier = new TimescaleTier();

         m_timescaleTopTier.setTickLines(topTierData[48] != 0);
         m_timescaleTopTier.setUsesFiscalYear(topTierData[60] != 0);
         m_timescaleTopTier.setUnits(TimescaleUnits.getInstance(topTierData[30]));
         m_timescaleTopTier.setCount(topTierData[32]);
         m_timescaleTopTier.setFormat(TimescaleFormat.getInstance(topTierData[34]));
         m_timescaleTopTier.setAlignment(TimescaleAlignment.getInstance(topTierData[36] - 20));

         m_topTierColumnGridLines = getGridLines(topTierData, 39);
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

   /**
    * Create a GanttChartView from the fixed and var data blocks associated
    * with a view.
    *
    * @param parent parent MPP file
    * @param fixedMeta fixed meta data block
    * @param fixedData fixed data block
    * @param varData var data block
    * @param fontBases map of font bases
    * @throws IOException
    */
   GanttChartView9(ProjectFile parent, byte[] fixedMeta, byte[] fixedData, Var2Data varData, Map<Integer, FontBase> fontBases)
      throws IOException
   {
      super(parent, fixedMeta, fixedData, varData, fontBases);
   }

   private static final Integer PROPERTIES = Integer.valueOf(1);
}
