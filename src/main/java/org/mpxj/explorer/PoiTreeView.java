/*
 * file:       PoiTreeView.java
 * author:     Jon Iles
 * copyright:  (c) Packwood Software 2014
 * date:       06/07/2014
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

package org.mpxj.explorer;

import javax.swing.JTree;

import org.apache.poi.poifs.filesystem.Entry;

/**
 * Implements the view component of the PoiTree MVC.
 */
public class PoiTreeView extends JTree
{
   /**
    * Constructor.
    *
    * @param model tree model to display
    */
   public PoiTreeView(final PoiTreeModel model)
   {
      setModel(model);
   }

   @Override public String convertValueToText(Object value, boolean selected, boolean expanded, boolean leaf, int row, boolean hasFocus)
   {
      String result = "";
      if (value instanceof Entry)
      {
         result = ((Entry) value).getName();
      }
      return result;
   }
}
