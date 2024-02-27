/*
 * file:       ProjectExplorer.java
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

import java.awt.EventQueue;
import java.awt.GridLayout;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.File;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.List;
import java.util.prefs.BackingStoreException;
import java.util.prefs.Preferences;
import java.util.Arrays;

import javax.swing.JCheckBoxMenuItem;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JTabbedPane;
import javax.swing.SwingConstants;
import javax.swing.UIManager;

import com.jgoodies.binding.beans.PropertyAdapter;

import net.sf.mpxj.MPXJException;
import net.sf.mpxj.ProjectFile;
import net.sf.mpxj.reader.UniversalProjectReader;

/**
 * MppExplorer is a Swing UI used to examine the contents of a project file read by MPXJ.
 */
public class ProjectExplorer
{
   private static final int MAX_RECENTS = 5;
   private static final String RECENT_FILES = "RECENT_FILES";
   private static final String RECENT_FOLDERS = "RECENT_FOLDERS";

   protected JFrame m_frame;
   private boolean m_openAll;
   private boolean m_expandSubprojects;
   private boolean m_removeExternalTasks = true;
   private final JMenuItem m_saveMenu = new JMenuItem("Save As...");
   private final JMenuItem m_cleanMenu = new JMenuItem("Clean...");
   private final JMenu m_recentMenu = new JMenu("Open Recent");
   private final JTabbedPane m_tabbedPane = new JTabbedPane(SwingConstants.TOP);
   private final Deque<String> m_recentFiles = new ArrayDeque<>();
   private final Deque<String> m_recentFolders = new ArrayDeque<>();
   private final FileChooserModel m_fileChooserModel = new FileChooserModel();
   private final FileChooserController m_fileChooserController = new FileChooserController(m_fileChooserModel);
   private final FileChooserModel m_openAllFileChooserModel = new FileChooserModel();
   private final FileChooserController m_openAllFileChooserController = new FileChooserController(m_openAllFileChooserModel);

   /**
    * Launch the application.
    *
    * @param args command line arguments.
    */
   public static void main(String[] args)
   {
      EventQueue.invokeLater(() -> {
         try
         {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            ProjectExplorer window = new ProjectExplorer();
            window.m_frame.setVisible(true);
         }
         catch (Exception ex)
         {
            ex.printStackTrace();
         }
      });
   }

   /**
    * Create the application.
    */
   public ProjectExplorer()
   {
      initialize();
   }

   /**
    * Initialize the contents of the frame.
    */
   private void initialize()
   {
      m_frame = new JFrame();
      m_frame.setBounds(100, 100, 900, 451);
      m_frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
      m_frame.getContentPane().setLayout(new GridLayout(1, 0, 0, 0));

      //
      // Open
      //
      @SuppressWarnings("unused")
      FileChooserView fileChooserView = new FileChooserView(m_frame, m_fileChooserModel);
      m_fileChooserModel.setExtensions(READ_EXTENSIONS);

      // Open All
      //
      @SuppressWarnings("unused")
      FileChooserView openAllFileChooserView = new FileChooserView(m_frame, m_openAllFileChooserModel);
      m_openAllFileChooserModel.setExtensions(READ_EXTENSIONS);

      //
      // Save
      //
      final FileSaverModel fileSaverModel = new FileSaverModel();
      final FileSaverController fileSaverController = new FileSaverController(fileSaverModel);
      @SuppressWarnings("unused")
      FileSaverView fileSaverView = new FileSaverView(m_frame, fileSaverModel);
      fileSaverModel.setExtensions(WRITE_EXTENSIONS);

      //
      // Clean
      //
      final FileCleanerModel fileCleanerModel = new FileCleanerModel();
      final FileCleanerController fileCleanerController = new FileCleanerController(fileCleanerModel);
      @SuppressWarnings("unused")
      FileCleanerView fileCleanerView = new FileCleanerView(m_frame, fileCleanerModel);

      JMenuBar menuBar = new JMenuBar();
      m_frame.setJMenuBar(menuBar);

      JMenu mnFile = new JMenu("File");
      menuBar.add(mnFile);

      JMenuItem mntmOpen = new JMenuItem("Open...");
      mnFile.add(mntmOpen);

      mnFile.add(m_recentMenu);

      m_saveMenu.setEnabled(false);
      mnFile.add(m_saveMenu);

      m_cleanMenu.setEnabled(false);
      mnFile.add(m_cleanMenu);

      mnFile.addSeparator();

      final JMenuItem mntmOpenAll = new JCheckBoxMenuItem("Open All");
      mnFile.add(mntmOpenAll);

      final JMenuItem mntmExpandSubprojects = new JCheckBoxMenuItem("Expand Subprojects", m_expandSubprojects);
      mnFile.add(mntmExpandSubprojects);

      final JMenuItem mntmRemoveExternalTasks = new JCheckBoxMenuItem("Remove External Tasks", m_removeExternalTasks);
      mntmRemoveExternalTasks.setEnabled(m_expandSubprojects);
      mnFile.add(mntmRemoveExternalTasks);

      //
      // Open
      //
      mntmOpen.addActionListener(e -> {
         if (m_openAll)
         {
            m_openAllFileChooserController.openFileChooser();
         }
         else
         {
            m_fileChooserController.openFileChooser();
         }
      });

      //
      // Save
      //
      m_saveMenu.addActionListener(e -> fileSaverController.openFileSaver());

      //
      // Clean
      //
      m_cleanMenu.addActionListener(e -> fileCleanerController.openFileCleaner());

      //
      // Open All
      //
      mntmOpenAll.addActionListener(e -> m_openAll = !m_openAll);

      //
      // Expand Subprojects
      //
      mntmExpandSubprojects.addActionListener(e -> {
         m_expandSubprojects = !m_expandSubprojects;
         mntmRemoveExternalTasks.setEnabled(m_expandSubprojects);
      });

      //
      // Remove external tasks
      //
      mntmRemoveExternalTasks.addActionListener(e -> m_removeExternalTasks = !m_removeExternalTasks);

      m_frame.getContentPane().add(m_tabbedPane);

      PropertyAdapter<FileChooserModel> openAdapter = new PropertyAdapter<>(m_fileChooserModel, "file", true);
      openAdapter.addValueChangeListener(evt -> openFile(m_fileChooserModel.getFile()));

      PropertyAdapter<FileChooserModel> openAllAdapter = new PropertyAdapter<>(m_openAllFileChooserModel, "file", true);
      openAllAdapter.addValueChangeListener(evt -> openAll(m_openAllFileChooserModel.getFile()));

      PropertyAdapter<FileSaverModel> saveAdapter = new PropertyAdapter<>(fileSaverModel, "file", true);
      saveAdapter.addValueChangeListener(evt -> {
         ProjectFilePanel panel = (ProjectFilePanel) m_tabbedPane.getSelectedComponent();
         panel.saveFile(fileSaverModel.getFile(), fileSaverModel.getType());
      });

      PropertyAdapter<FileCleanerModel> cleanAdapter = new PropertyAdapter<>(fileCleanerModel, "file", true);
      cleanAdapter.addValueChangeListener(evt -> {
         ProjectFilePanel panel = (ProjectFilePanel) m_tabbedPane.getSelectedComponent();
         panel.cleanFile(fileCleanerModel.getFile());
      });

      m_frame.addWindowListener(new WindowListener()
      {
         @Override public void windowOpened(WindowEvent e)
         {
            // Unused
         }

         @Override public void windowClosing(WindowEvent e)
         {
            saveRecents();
         }

         @Override public void windowClosed(WindowEvent e)
         {
            // Unused
         }

         @Override public void windowIconified(WindowEvent e)
         {
            // Unused
         }

         @Override public void windowDeiconified(WindowEvent e)
         {
            // Unused
         }

         @Override public void windowActivated(WindowEvent e)
         {
            // Unused
         }

         @Override public void windowDeactivated(WindowEvent e)
         {
            // Unused
         }
      });

      loadRecents();
   }

   private void openFile(File file)
   {
      try
      {
         ProjectFile projectFile = new UniversalProjectReader().read(file);
         if (projectFile == null)
         {
            JOptionPane.showMessageDialog(m_frame, "Unsupported file type");
            return;
         }

         expandSubprojects(file, projectFile);
         m_tabbedPane.add(file.getName(), new ProjectFilePanel(file, projectFile));
         m_saveMenu.setEnabled(true);
         m_cleanMenu.setEnabled(true);
         updateRecents(file);
      }

      catch (MPXJException ex)
      {
         throw new IllegalArgumentException("Failed to read file", ex);
      }
   }

   private void openAll(File file)
   {
      try
      {
         List<ProjectFile> projectFiles = new UniversalProjectReader().readAll(file);
         if (projectFiles.isEmpty())
         {
            JOptionPane.showMessageDialog(m_frame, "Unsupported file type");
            return;
         }

         int index = 1;
         for (ProjectFile projectFile : projectFiles)
         {
            String name = projectFiles.size() == 1 ? file.getName() : file.getName() + " (" + (index++) + ")";
            expandSubprojects(file, projectFile);
            m_tabbedPane.add(name, new ProjectFilePanel(file, projectFile));
         }
         m_saveMenu.setEnabled(true);
         m_cleanMenu.setEnabled(true);
         updateRecents(file);
      }

      catch (MPXJException ex)
      {
         throw new IllegalArgumentException("Failed to read file", ex);
      }
   }

   private void loadRecents()
   {
      Preferences prefs = Preferences.userNodeForPackage(this.getClass());

      m_recentFiles.clear();
      m_recentFolders.clear();

      String recentFilesString = prefs.get(RECENT_FILES, "");
      if (!recentFilesString.isEmpty())
      {
         m_recentFiles.addAll(Arrays.asList(recentFilesString.split("\\|")));
      }

      String recentFoldersString = prefs.get(RECENT_FOLDERS, "");
      if (!recentFoldersString.isEmpty())
      {
         m_recentFolders.addAll(Arrays.asList(recentFoldersString.split("\\|")));
      }

      updateRecentsMenu();
   }

   private void saveRecents()
   {
      try
      {
         Preferences prefs = Preferences.userNodeForPackage(this.getClass());
         prefs.put(RECENT_FILES, String.join("|", m_recentFiles));
         prefs.put(RECENT_FOLDERS, String.join("|", m_recentFolders));
         prefs.flush();
      }
      catch (BackingStoreException e)
      {
         throw new RuntimeException(e);
      }
   }

   private void updateRecents(File file)
   {
      updateRecents(file.getAbsolutePath(), m_recentFiles);
      updateRecents(file.getParentFile().getAbsolutePath(), m_recentFolders);
      updateRecentsMenu();
   }

   private void updateRecents(String name, Deque<String> recents)
   {
      if (recents.isEmpty())
      {
         recents.add(name);
      }
      else
      {
         if (!recents.peekFirst().equals(name))
         {
            recents.remove(name);
            recents.addFirst(name);
            if (recents.size() > MAX_RECENTS)
            {
               recents.removeLast();
            }
         }
      }
   }

   private void updateRecentsMenu()
   {
      m_recentMenu.removeAll();

      for (String path : m_recentFiles)
      {
         JMenuItem item = new JMenuItem(path);
         item.addActionListener(l -> openRecentFile(path));
         m_recentMenu.add(item);
      }

      if (m_recentMenu.getItemCount() != 0 && !m_recentFolders.isEmpty())
      {
         m_recentMenu.addSeparator();
      }

      for (String path : m_recentFolders)
      {
         JMenuItem item = new JMenuItem(path);
         item.addActionListener(l -> openRecentFolder(path));
         m_recentMenu.add(item);
      }

      if (m_recentMenu.getItemCount() != 0 && (!m_recentFolders.isEmpty() || !m_recentFiles.isEmpty()))
      {
         m_recentMenu.addSeparator();
      }

      if (!m_recentFiles.isEmpty())
      {
         JMenuItem clearFiles = new JMenuItem("Clear Recent Files");
         m_recentMenu.add(clearFiles);
         clearFiles.addActionListener(l -> {
            m_recentFiles.clear();
            updateRecentsMenu();
         });
      }

      if (!m_recentFolders.isEmpty())
      {
         JMenuItem clearFolders = new JMenuItem("Clear Recent Folders");
         m_recentMenu.add(clearFolders);
         clearFolders.addActionListener(l -> {
            m_recentFolders.clear();
            updateRecentsMenu();
         });
      }

      m_recentMenu.setEnabled(m_recentMenu.getItemCount() != 0);
   }

   private void openRecentFile(String path)
   {
      File file = new File(path);
      if (m_openAll)
      {
         openAll(file);
      }
      else
      {
         openFile(file);
      }
   }

   private void openRecentFolder(String path)
   {
      File file = new File(path);

      if (m_openAll)
      {
         m_openAllFileChooserModel.setCurrentDirectory(file);
         m_openAllFileChooserController.openFileChooser();
      }
      else
      {
         m_fileChooserModel.setCurrentDirectory(file);
         m_fileChooserController.openFileChooser();
      }
   }

   /**
    * If configured, expand subprojects.
    *
    * @param file selected file
    * @param projectFile schedule data from selected file
    */
   private void expandSubprojects(File file, ProjectFile projectFile)
   {
      if (m_expandSubprojects)
      {
         projectFile.getProjectConfig().setSubprojectWorkingDirectory(file.getParentFile());
         projectFile.expandSubprojects(m_removeExternalTasks);
      }
   }

   private static final String[] READ_EXTENSIONS =
   {
      "bk3",
      "cdpx",
      "cdpz",
      "exe",
      "fts",
      "gan",
      "gnt",
      "mdb",
      "mpd",
      "mpp",
      "mpx",
      "pc",
      "pep",
      "planner",
      "pmxml",
      "pod",
      "pp",
      "ppx",
      "prx",
      "schedule_grid",
      "sdef",
      "sp",
      "stx",
      "xer",
      "xml",
      "zip"
   };

   private static final String[] WRITE_EXTENSIONS =
   {
      "sdef",
      "sdef",
      "mpx",
      "mpx",
      "planner",
      "xml",
      "pmxml",
      "xml",
      "json",
      "json",
      "mspdi",
      "xml",
      "xer",
      "xer"
   };
}
