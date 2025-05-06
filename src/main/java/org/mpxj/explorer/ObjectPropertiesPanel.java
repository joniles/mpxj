/*
 * file:       ObjectPropertiesPanel.java
 * author:     Jon Iles
 * copyright:  (c) Packwood Software 2014
 * date:       16/07/2014
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

import java.awt.GridLayout;
import java.util.Set;

import javax.swing.JPanel;

/**
 * This panel represents a Java object as a set of properties.
 * The properties are displayed in a table with the property names
 * in the first column, and the property values in the second column.
 */
public class ObjectPropertiesPanel extends JPanel
{

   /**
    * Constructor.
    *
    * @param object the object whose properties we will display
    * @param excludedMethods method names to exclude
    */
   public ObjectPropertiesPanel(Object object, Set<String> excludedMethods)
   {
      setLayout(new GridLayout(0, 1, 0, 0));

      ObjectPropertiesModel objectPropertiesModel = new ObjectPropertiesModel();
      ObjectPropertiesController objectPropertiesController = new ObjectPropertiesController(objectPropertiesModel);
      ObjectPropertiesView objectPropertiesView = new ObjectPropertiesView(objectPropertiesModel);

      add(objectPropertiesView);

      objectPropertiesController.loadObject(object, excludedMethods);
   }
}
