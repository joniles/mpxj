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

package net.sf.mpxj.explorer;

import java.awt.GridLayout;
import java.io.File;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.SwingConstants;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.TreePath;

import org.apache.poi.poifs.filesystem.DocumentEntry;

/**
 * Component representing the main view of an MPP file.
 */
public class MppFilePanel extends JPanel
{
   private PoiTreeModel m_treeModel;
   private PoiTreeController m_treeController;
   private PoiTreeView m_treeView;

   private HexDumpModel m_hexDumpModel;
   protected HexDumpController m_hexDumpController;
   private HexDumpView m_hexDumpView;

   /**
    * Constructor.
    *
    * @param file MPP file to be displayed in this view.
    */
   public MppFilePanel(File file)
   {
      m_treeModel = new PoiTreeModel();
      m_treeController = new PoiTreeController(m_treeModel);
      m_treeView = new PoiTreeView(m_treeModel);
      m_treeView.setShowsRootHandles(true);

      m_hexDumpModel = new HexDumpModel();
      m_hexDumpController = new HexDumpController(m_hexDumpModel);
      setLayout(new GridLayout(0, 1, 0, 0));
      m_hexDumpView = new HexDumpView(m_hexDumpModel);

      JSplitPane splitPane = new JSplitPane();
      splitPane.setDividerLocation(0.3);
      add(splitPane);

      JScrollPane scrollPane = new JScrollPane(m_treeView);
      splitPane.setLeftComponent(scrollPane);

      final JTabbedPane tabbedPane = new JTabbedPane(SwingConstants.TOP);
      splitPane.setRightComponent(tabbedPane);
      tabbedPane.add("Hex Dump", m_hexDumpView);

      m_treeView.addTreeSelectionListener(new TreeSelectionListener()
      {
         @Override public void valueChanged(TreeSelectionEvent e)
         {
            TreePath path = e.getPath();
            Object component = path.getLastPathComponent();
            if (component instanceof DocumentEntry)
            {
               m_hexDumpController.viewDocument((DocumentEntry) component);
            }
         }
      });

      m_treeController.loadFile(file);
   }

}
