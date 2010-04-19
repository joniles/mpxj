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

import java.io.IOException;
import java.util.Map;

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
