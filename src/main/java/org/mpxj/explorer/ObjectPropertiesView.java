/*
 * file:       ObjectPropertiesView.java
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

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.SpringLayout;

import com.jgoodies.binding.beans.PropertyConnector;

/**
 * Implements the view component of the ObjectProperties MVC.
 */
public class ObjectPropertiesView extends JPanel
{
   /**
    * Constructor.
    *
    * @param model model used by this view
    */
   public ObjectPropertiesView(ObjectPropertiesModel model)
   {
      SpringLayout springLayout = new SpringLayout();
      setLayout(springLayout);

      JTable table = new JTable();
      JScrollPane scrollPane = new JScrollPane(table);
      springLayout.putConstraint(SpringLayout.NORTH, scrollPane, 0, SpringLayout.NORTH, this);
      springLayout.putConstraint(SpringLayout.WEST, scrollPane, 0, SpringLayout.WEST, this);
      springLayout.putConstraint(SpringLayout.SOUTH, scrollPane, 0, SpringLayout.SOUTH, this);
      springLayout.putConstraint(SpringLayout.EAST, scrollPane, 0, SpringLayout.EAST, this);
      add(scrollPane);

      PropertyConnector.connect(table, "model", model, "tableModel");
   }
}
