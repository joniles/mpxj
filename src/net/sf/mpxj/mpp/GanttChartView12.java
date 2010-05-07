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

package net.sf.mpxj.mpp;

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
public final class GanttChartView12 extends GanttChartView
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
   @Override protected void processAutoFilters(byte[] data)
   {
      //System.out.println(MPPUtility.hexdump(data, true, 16, ""));

      //
      // 16 byte block header containing the filter count
      //
      int filterCount = MPPUtility.getShort(data, 8);
      int offset = 16;
      CriteriaReader criteria = new FilterCriteriaReader12();
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
   GanttChartView12(ProjectFile parent, byte[] fixedMeta, byte[] fixedData, Var2Data varData, Map<Integer, FontBase> fontBases)
      throws IOException
   {
      super(parent, fixedMeta, fixedData, varData, fontBases);
   }

   private static final Integer PROPERTIES = Integer.valueOf(6);
}
