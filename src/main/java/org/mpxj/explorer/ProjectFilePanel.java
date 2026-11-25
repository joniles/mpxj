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

package org.mpxj.explorer;

import java.awt.GridLayout;
import java.awt.event.ContainerAdapter;
import java.awt.event.ContainerEvent;
import java.io.File;
import java.util.HashMap;
import java.util.Map;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.SwingConstants;
import javax.swing.tree.TreePath;
import org.mpxj.ProjectFile;

/**
 * Component representing the main view of a project file.
 */
class ProjectFilePanel extends JPanel
{
   private final ProjectTreeController m_treeController;
   final Map<MpxjTreeNode, ObjectPropertiesPanel> m_openTabs;

   /**
    * Constructor.
    *
    * @param file original file
    * @param projectFile MPP file to be displayed in this view.
    * @param writeOptions writer options
    */
   public ProjectFilePanel(File file, ProjectFile projectFile, WriteOptions writeOptions)
   {
      ProjectTreeModel treeModel = new ProjectTreeModel(writeOptions);
      m_treeController = new ProjectTreeController(treeModel);
      setLayout(new GridLayout(0, 1, 0, 0));
      ProjectTreeView treeView = new ProjectTreeView(treeModel);
      treeView.setShowsRootHandles(true);

      JSplitPane splitPane = new JSplitPane();
      splitPane.setDividerLocation(0.3);
      add(splitPane);

      JScrollPane scrollPane = new JScrollPane(treeView);
      splitPane.setLeftComponent(scrollPane);

      final JTabbedPane tabbedPane = new JTabbedPane(SwingConstants.TOP);
      splitPane.setRightComponent(tabbedPane);

      m_openTabs = new HashMap<>();

      // Forget a properties panel once its tab is closed, so it is recreated if selected again.
      // If the closed tab belongs to the selected node, clear the tree selection so the node
      // can be clicked again to reopen its tab.
      tabbedPane.addContainerListener(new ContainerAdapter()
      {
         @Override public void componentRemoved(ContainerEvent e)
         {
            Object selectedNode = treeView.getLastSelectedPathComponent();
            if (selectedNode != null && m_openTabs.get(selectedNode) == e.getChild())
            {
               treeView.clearSelection();
            }
            m_openTabs.values().remove(e.getChild());
         }
      });

      treeView.addTreeSelectionListener(e -> {
         TreePath path = e.getNewLeadSelectionPath();
         if (path == null)
         {
            // Selection cleared
            return;
         }

         MpxjTreeNode component = (MpxjTreeNode) path.getLastPathComponent();
         if (!(component.getUserObject() instanceof String))
         {
            ObjectPropertiesPanel panel = m_openTabs.get(component);
            if (panel == null)
            {
               panel = new ObjectPropertiesPanel(component.getUserObject(), component.getExcludedMethods());
               String title = component.toString();
               tabbedPane.add(title, panel);
               tabbedPane.setTabComponentAt(tabbedPane.indexOfComponent(panel), new CloseableTabComponent(tabbedPane, title));
               m_openTabs.put(component, panel);
            }
            tabbedPane.setSelectedComponent(panel);
         }
      });

      m_treeController.loadFile(file, projectFile);
   }

   /**
    * Retrieve the file this panel's project was read from.
    *
    * @return source file, or null if the project was not read from a file
    */
   public File getFile()
   {
      return m_treeController.getFile();
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

   /**
    * Saves an anonymized version of the project file displayed in this panel.
    *
    * @param file target file
    */
   public void cleanFile(File file)
   {
      if (file != null)
      {
         m_treeController.cleanFile(file);
      }
   }
}
