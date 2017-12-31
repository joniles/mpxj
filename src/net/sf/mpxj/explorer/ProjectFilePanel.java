/*
 * file:       ProjectFilePanel.java
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

package net.sf.mpxj.explorer;

import java.awt.GridLayout;
import java.io.File;
import java.util.HashMap;
import java.util.Map;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.SwingConstants;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.TreePath;

/**
 * Component representing the main view of a project file.
 */
public class ProjectFilePanel extends JPanel
{
   private final ProjectTreeModel m_treeModel;
   private final ProjectTreeController m_treeController;
   private final ProjectTreeView m_treeView;
   final Map<MpxjTreeNode, ObjectPropertiesPanel> m_openTabs;

   /**
    * Constructor.
    *
    * @param file MPP file to be displayed in this view.
    */
   public ProjectFilePanel(File file)
   {
      m_treeModel = new ProjectTreeModel();
      m_treeController = new ProjectTreeController(m_treeModel);
      setLayout(new GridLayout(0, 1, 0, 0));
      m_treeView = new ProjectTreeView(m_treeModel);
      m_treeView.setShowsRootHandles(true);

      JSplitPane splitPane = new JSplitPane();
      splitPane.setDividerLocation(0.3);
      add(splitPane);

      JScrollPane scrollPane = new JScrollPane(m_treeView);
      splitPane.setLeftComponent(scrollPane);

      final JTabbedPane tabbedPane = new JTabbedPane(SwingConstants.TOP);
      splitPane.setRightComponent(tabbedPane);

      m_openTabs = new HashMap<MpxjTreeNode, ObjectPropertiesPanel>();

      m_treeView.addTreeSelectionListener(new TreeSelectionListener()
      {
         @Override public void valueChanged(TreeSelectionEvent e)
         {
            TreePath path = e.getPath();
            MpxjTreeNode component = (MpxjTreeNode) path.getLastPathComponent();
            if (!(component.getUserObject() instanceof String))
            {
               ObjectPropertiesPanel panel = m_openTabs.get(component);
               if (panel == null)
               {
                  panel = new ObjectPropertiesPanel(component.getUserObject(), component.getExcludedMethods());
                  tabbedPane.add(component.toString(), panel);
                  m_openTabs.put(component, panel);
               }
               tabbedPane.setSelectedComponent(panel);
            }
         }
      });

      m_treeController.loadFile(file);
   }

   /**
    * Saves the project file displayed in this panel.
    *
    * @param file target file
    * @param type file type
    */
   public void saveFile(File file, String type)
   {
      if (file != null)
      {
         m_treeController.saveFile(file, type);
      }
   }
}
