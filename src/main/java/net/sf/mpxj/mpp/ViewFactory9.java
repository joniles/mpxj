/*
 * file:       ViewFactory9.java
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
import net.sf.mpxj.View;
import net.sf.mpxj.ViewType;

/**
 * Default implementation of a view factory for MPP9 files.
 */
class ViewFactory9 implements ViewFactory
{
   @Override public View createView(ProjectFile file, byte[] fixedMeta, byte[] fixedData, Var2Data varData, Map<Integer, FontBase> fontBases) throws IOException
   {
      View view;
      int splitViewFlag = MPPUtility.getShort(fixedData, 110);
      if (splitViewFlag == 1)
      {
         view = new SplitView9(file, fixedData, varData);
      }
      else
      {
         ViewType type = ViewType.getInstance(MPPUtility.getShort(fixedData, 112));
         if (type == ViewType.GANTT_CHART)
         {
            view = new GanttChartView9(file, fixedMeta, fixedData, varData, fontBases);
         }
         else
         {
            view = new GenericView9(file, fixedData, varData);
         }
      }

      return (view);
   }
}
