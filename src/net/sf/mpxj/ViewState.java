/*
 * file:       ViewState.java
 * author:     Jon Iles
 * copyright:  (c) Tapster Rock Limited 2007
 * date:       Jan 8, 2007
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

package net.sf.mpxj;

import java.util.List;

/**
 * This class represents the state of a view which has been saved
 * as part of a project file.
 */
public final class ViewState
{   
   /**
    * Constructor.
    * 
    * @param viewName view name
    * @param uniqueIdList unique ID list
    */
   public ViewState (String viewName, List uniqueIdList)
   {
      m_viewName = viewName;
      m_uniqueIdList = uniqueIdList;
   }
   
   /**
    * Retrieve the name of the view associated with this state.
    * 
    * @return view name
    */
   public String getViewName()
   {
      return m_viewName;
   }

   /**
    * Retrieve a list of unique IDs representing the contents of this view.
    * 
    * @return unique ID list
    */
   public List getUniqueIdList()
   {
      return m_uniqueIdList;
   }

   private String m_viewName;
   private List m_uniqueIdList;
}
