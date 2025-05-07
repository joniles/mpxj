/*
 * file:       ViewStateReader.java
 * author:     Jon Iles
 * copyright:  (c) Packwood Software 2007
 * date:       Jan 07, 2007
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
import java.util.ArrayList;
import java.util.List;

import org.mpxj.ProjectFile;
import org.mpxj.ViewState;
import org.mpxj.common.ByteArrayHelper;

/**
 * This class allows the saved state of a view to be read from an MPP file.
 */
public abstract class ViewStateReader
{
   /**
    * Retrieves the props data using a file format specific method.
    *
    * @param varData var data block
    * @return props data
    */
   protected abstract Props getProps(Var2Data varData) throws IOException;

   /**
    * Entry point for processing saved view state.
    *
    * @param file project file
    * @param varData view state var data
    * @param fixedData view state fixed data
    */
   public void process(ProjectFile file, Var2Data varData, byte[] fixedData) throws IOException
   {
      Props props = getProps(varData);
      //System.out.println(props);
      if (props != null)
      {
         String viewName = MPPUtility.removeAmpersands(props.getUnicodeString(VIEW_NAME));
         byte[] listData = props.getByteArray(VIEW_CONTENTS);
         List<Integer> uniqueIdList = new ArrayList<>();
         if (listData != null)
         {
            for (int index = 0; index < listData.length; index += 4)
            {
               Integer uniqueID = Integer.valueOf(ByteArrayHelper.getInt(listData, index));

               //
               // Ensure that we have a valid task, and that if we have and
               // ID of zero, this is the first task shown.
               //
               if (file.getTaskByUniqueID(uniqueID) != null && (uniqueID.intValue() != 0 || index == 0))
               {
                  uniqueIdList.add(uniqueID);
               }
            }
         }

         int filterID = ByteArrayHelper.getShort(fixedData, 128);

         ViewState state = new ViewState(file, viewName, uniqueIdList, filterID);
         file.getViews().setViewState(state);
      }
   }

   private static final Integer VIEW_NAME = Integer.valueOf(641728536);
   private static final Integer VIEW_CONTENTS = Integer.valueOf(641728565);
}
