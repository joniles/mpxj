/*
 * file:       ViewFactory14.java
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

import java.io.IOException;
import java.util.Map;

import org.mpxj.ProjectFile;
import org.mpxj.View;
import org.mpxj.ViewType;
import org.mpxj.common.ByteArrayHelper;

/**
 * Default implementation of a view factory for MPP14 files.
 */
class ViewFactory14 implements ViewFactory
{
   @Override public View createView(ProjectFile file, byte[] fixedMeta, byte[] fixedData, Var2Data varData, Map<Integer, FontBase> fontBases) throws IOException
   {
      View view;
      int splitViewFlag = ByteArrayHelper.getShort(fixedData, 110);
      if (splitViewFlag == 1)
      {
         view = new SplitView9(file, fixedData, varData);
      }
      else
      {
         ViewType type = ViewType.getInstance(ByteArrayHelper.getShort(fixedData, 112));
         if (type == ViewType.GANTT_CHART)
         {
            view = new GanttChartView14(file, fixedMeta, fixedData, varData, fontBases);
         }
         else
         {
            view = new GenericView14(file, fixedData, varData);
         }
      }

      return (view);
   }
}
