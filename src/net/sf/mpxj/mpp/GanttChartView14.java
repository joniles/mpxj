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

package net.sf.mpxj.mpp;

import java.awt.Color;
import java.io.IOException;
import java.util.Map;

import net.sf.mpxj.FieldType;
import net.sf.mpxj.Filter;
import net.sf.mpxj.GenericCriteria;
import net.sf.mpxj.MPPResourceField;
import net.sf.mpxj.MPPResourceField14;
import net.sf.mpxj.MPPTaskField;
import net.sf.mpxj.MPPTaskField14;
import net.sf.mpxj.ProjectFile;

/**
 * This class represents the set of properties used to define the appearance
 * of a Gantt chart view in MS Project.
 */
public final class GanttChartView14 extends GanttChartView
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
      GanttBarStyleFactory f = new GanttBarStyleFactory14();
      m_barStyles = f.processDefaultStyles(props);
   }

   /**
    * {@inheritDoc}
    */
   @Override protected void processExceptionBarStyles(Props props)
   {
      GanttBarStyleFactory f = new GanttBarStyleFactory14();
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
      CriteriaReader criteria = new FilterCriteriaReader14();

      //
      // 16 byte header
      // followed by 4 bytes = field type
      // followed by 2 byte block size
      for (int loop = 0; loop < filterCount; loop++)
      {
         FieldType field = getFieldType(data, offset);
         int blockSize = MPPUtility.getShort(data, offset + 4);

         //
         // Steelray 12335: the block size may be zero
         //
         if (blockSize == 0)
         {
            break;
         }

         //System.out.println(MPPUtility.hexdump(data, offset, 32, false));

         // may need to sort this out
         GenericCriteria c = criteria.process(m_parent, data, offset + 12, -1, null, null, null);
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
      FieldType result = null;
      int fieldIndex = MPPUtility.getInt(data, offset);
      switch (fieldIndex & 0xFFFF0000)
      {
         case MPPTaskField.TASK_FIELD_BASE :
         {
            result = MPPTaskField14.getInstance(fieldIndex & 0xFFFF);
            break;
         }

         case MPPResourceField.RESOURCE_FIELD_BASE :
         {
            result = MPPResourceField14.getInstance(fieldIndex & 0xFFFF);
            break;
         }
      }
      return result;
   }

   /**
    * {@inheritDoc}
    */
   @Override protected void processViewProperties(Map<Integer, FontBase> fontBases, Props props)
   {
      byte[] viewPropertyData = props.getByteArray(VIEW_PROPERTIES);
      if (viewPropertyData != null)
      {
         //MPPUtility.fileDump("c:\\temp\\props.txt", MPPUtility.hexdump(viewPropertyData, false, 16, "").getBytes());

         m_highlightedTasksFontStyle = getFontStyle(viewPropertyData, 26, fontBases);
         m_rowAndColumnFontStyle = getFontStyle(viewPropertyData, 58, fontBases);
         m_nonCriticalTasksFontStyle = getFontStyle(viewPropertyData, 90, fontBases);
         m_criticalTasksFontStyle = getFontStyle(viewPropertyData, 122, fontBases);
         m_summaryTasksFontStyle = getFontStyle(viewPropertyData, 154, fontBases);
         m_milestoneTasksFontStyle = getFontStyle(viewPropertyData, 186, fontBases);
         m_middleTimescaleFontStyle = getFontStyle(viewPropertyData, 218, fontBases);
         m_bottomTimescaleFontStyle = getFontStyle(viewPropertyData, 250, fontBases);
         m_barTextLeftFontStyle = getFontStyle(viewPropertyData, 282, fontBases);
         m_barTextRightFontStyle = getFontStyle(viewPropertyData, 314, fontBases);
         m_barTextTopFontStyle = getFontStyle(viewPropertyData, 346, fontBases);
         m_barTextBottomFontStyle = getFontStyle(viewPropertyData, 378, fontBases);
         m_barTextInsideFontStyle = getFontStyle(viewPropertyData, 410, fontBases);
         m_markedTasksFontStyle = getFontStyle(viewPropertyData, 442, fontBases);
         m_projectSummaryTasksFontStyle = getFontStyle(viewPropertyData, 474, fontBases);
         m_externalTasksFontStyle = getFontStyle(viewPropertyData, 506, fontBases);
         m_topTimescaleFontStyle = getFontStyle(viewPropertyData, 538, fontBases);

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

         m_ganttBarHeight = mapGanttBarHeight(MPPUtility.getByte(viewPropertyData, 2244));
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
      //System.out.println(offset+ ": " + MPPUtility.hexdump(data, offset, 30, false));
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
    * @return FontStyle instance
    */
   @Override protected FontStyle getFontStyle(byte[] data, int offset, Map<Integer, FontBase> fontBases)
   {
      //System.out.println(MPPUtility.hexdump(data, offset, 32, false));

      Integer index = Integer.valueOf(MPPUtility.getByte(data, offset));
      FontBase fontBase = fontBases.get(index);
      int style = MPPUtility.getByte(data, offset + 3);
      Color color = MPPUtility.getColor(data, offset + 4);
      Color backgroundColor = MPPUtility.getColor(data, offset + 16);
      BackgroundPattern backgroundPattern = BackgroundPattern.getInstance(MPPUtility.getShort(data, offset + 28));

      boolean bold = ((style & 0x01) != 0);
      boolean italic = ((style & 0x02) != 0);
      boolean underline = ((style & 0x04) != 0);

      FontStyle fontStyle = new FontStyle(fontBase, italic, bold, underline, color, backgroundColor, backgroundPattern);
      //System.out.println(fontStyle);
      return fontStyle;
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
   GanttChartView14(ProjectFile parent, byte[] fixedMeta, byte[] fixedData, Var2Data varData, Map<Integer, FontBase> fontBases)
      throws IOException
   {
      super(parent, fixedMeta, fixedData, varData, fontBases);
   }

   private static final Integer PROPERTIES = Integer.valueOf(6);
}
