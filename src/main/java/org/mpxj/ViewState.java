/*
 * file:       ViewState.java
 * author:     Jon Iles
 * copyright:  (c) Packwood Software 2007
 * date:       2007-01-08
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

package org.mpxj;

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
    * @param file parent project file
    * @param viewName view name
    * @param uniqueIdList unique ID list
    * @param filterID filter ID
    */
   public ViewState(ProjectFile file, String viewName, List<Integer> uniqueIdList, int filterID)
   {
      m_file = file;
      m_viewName = viewName;
      m_uniqueIdList = uniqueIdList;
      m_filterID = Integer.valueOf(filterID);
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
   public List<Integer> getUniqueIdList()
   {
      return m_uniqueIdList;
   }

   /**
    * Retrieve the currently applied filter.
    *
    * @return filter instance
    */
   public Filter getFilter()
   {
      return m_file.getFilters().getFilterByID(m_filterID);
   }

   private final ProjectFile m_file;
   private final String m_viewName;
   private final List<Integer> m_uniqueIdList;
   private final Integer m_filterID;
}
