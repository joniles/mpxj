/*
 * file:       ViewStateReader.java
 * author:     Jon Iles
 * copyright:  (c) Tapster Rock Limited 2007
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

package net.sf.mpxj.mpp;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

import net.sf.mpxj.ProjectFile;
import net.sf.mpxj.ViewState;

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
    * @throws IOException
    */
   protected abstract Props getProps (Var2Data varData)
      throws IOException;
   
   /**
    * Entry point for processing saved view state.
    * 
    * @param file project file
    * @param varData filter var data
    * @throws IOException
    */
   public void process (ProjectFile file, Var2Data varData)
      throws IOException
   {   
      Props props = getProps(varData);
      if (props != null)
      {
         String viewName = MPPUtility.removeAmpersands(props.getUnicodeString(VIEW_NAME));
         byte[] listData = props.getByteArray(VIEW_CONTENTS);
         List uniqueIdList = new LinkedList();
         if (listData != null)
         {               
            for (int index=0; index < listData.length; index += 4)
            {
               Integer uniqueID = new Integer(MPPUtility.getInt(listData, index));
               if (file.getTaskByUniqueID(uniqueID) == null)
               {
                  break;
               }
               uniqueIdList.add(uniqueID);                      
            }
         }
         ViewState state = new ViewState(viewName, uniqueIdList);
         
         file.setViewState(state);
      }
   }   
   
   private static final Integer PROPS_ID = new Integer (1);
   private static final Integer PROPS_TYPE = new Integer (1);
   
   private static final Integer VIEW_NAME = new Integer(641728536);
   private static final Integer VIEW_CONTENTS = new Integer(641728565);
}
