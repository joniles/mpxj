/*
 * file:       MppFilePanel.java
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

import java.awt.GridLayout;
import java.io.File;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.SwingConstants;
import javax.swing.tree.TreePath;

import org.apache.poi.poifs.filesystem.DocumentEntry;

/**
 * Component representing the main view of an MPP file.
 */
public class MppFilePanel extends JPanel
{

   protected final HexDumpController m_hexDumpController;

   /**
    * Constructor.
    *
    * @param file MPP file to be displayed in this view.
    */
   public MppFilePanel(File file)
   {
      PoiTreeModel treeModel = new PoiTreeModel();
      PoiTreeController treeController = new PoiTreeController(treeModel);
      PoiTreeView treeView = new PoiTreeView(treeModel);
      treeView.setShowsRootHandles(true);

      HexDumpModel hexDumpModel = new HexDumpModel();
      m_hexDumpController = new HexDumpController(hexDumpModel);
      setLayout(new GridLayout(0, 1, 0, 0));
      HexDumpView hexDumpView = new HexDumpView(hexDumpModel);

      JSplitPane splitPane = new JSplitPane();
      splitPane.setDividerLocation(0.3);
      add(splitPane);

      JScrollPane scrollPane = new JScrollPane(treeView);
      splitPane.setLeftComponent(scrollPane);

      final JTabbedPane tabbedPane = new JTabbedPane(SwingConstants.TOP);
      splitPane.setRightComponent(tabbedPane);
      tabbedPane.add("Hex Dump", hexDumpView);

      treeView.addTreeSelectionListener(e -> {
         TreePath path = e.getPath();
         Object component = path.getLastPathComponent();
         if (component instanceof DocumentEntry)
         {
            m_hexDumpController.viewDocument((DocumentEntry) component);
         }
      });

      treeController.loadFile(file);
   }

}
