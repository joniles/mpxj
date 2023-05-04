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
import java.io.File;
import java.util.List;

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
   protected JFrame m_frame;

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
         catch (Exception e)
         {
            e.printStackTrace();
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
      final FileChooserModel fileChooserModel = new FileChooserModel();
      final FileChooserController fileChooserController = new FileChooserController(fileChooserModel);
      @SuppressWarnings("unused")
      FileChooserView fileChooserView = new FileChooserView(m_frame, fileChooserModel);
      fileChooserModel.setExtensions(READ_EXTENSIONS);

      // Open All
      //
      final FileChooserModel openAllFileChooserModel = new FileChooserModel();
      final FileChooserController openAllFileChooserController = new FileChooserController(openAllFileChooserModel);
      @SuppressWarnings("unused")
      FileChooserView openAllFileChooserView = new FileChooserView(m_frame, openAllFileChooserModel);
      openAllFileChooserModel.setExtensions(READ_EXTENSIONS);

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

      JMenuItem mntmOpenAll = new JMenuItem("Open All...");
      mnFile.add(mntmOpenAll);

      final JMenuItem mntmSave = new JMenuItem("Save As...");
      mntmSave.setEnabled(false);
      mnFile.add(mntmSave);

      final JMenuItem mntmClean = new JMenuItem("Clean...");
      mntmClean.setEnabled(false);
      mnFile.add(mntmClean);

      //
      // Open
      //
      mntmOpen.addActionListener(e -> fileChooserController.openFileChooser());

      //
      // Open All
      //
      mntmOpenAll.addActionListener(e -> openAllFileChooserController.openFileChooser());

      //
      // Save
      //
      mntmSave.addActionListener(e -> fileSaverController.openFileSaver());

      //
      // Clean
      //
      mntmClean.addActionListener(e -> fileCleanerController.openFileCleaner());

      final JTabbedPane tabbedPane = new JTabbedPane(SwingConstants.TOP);
      m_frame.getContentPane().add(tabbedPane);

      PropertyAdapter<FileChooserModel> openAdapter = new PropertyAdapter<>(fileChooserModel, "file", true);
      openAdapter.addValueChangeListener(evt -> {
         try
         {
            File file = fileChooserModel.getFile();
            ProjectFile projectFile = new UniversalProjectReader().read(file);
            if (projectFile == null)
            {
               JOptionPane.showMessageDialog(m_frame, "Unsupported file type");
               return;
            }

            // If we want to automatically expand subprojects...
            //projectFile.setSubprojectWorkingDirectory(file.getParentFile());
            //projectFile.expandSubprojects();

            tabbedPane.add(file.getName(), new ProjectFilePanel(file, projectFile));
            mntmSave.setEnabled(true);
            mntmClean.setEnabled(true);
         }

         catch (MPXJException ex)
         {
            throw new IllegalArgumentException("Failed to read file", ex);
         }
      });

      PropertyAdapter<FileChooserModel> openAllAdapter = new PropertyAdapter<>(openAllFileChooserModel, "file", true);
      openAllAdapter.addValueChangeListener(evt -> {
         try
         {
            File file = openAllFileChooserModel.getFile();
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
               tabbedPane.add(name, new ProjectFilePanel(file, projectFile));
            }
            mntmSave.setEnabled(true);
            mntmClean.setEnabled(true);
         }

         catch (MPXJException ex)
         {
            throw new IllegalArgumentException("Failed to read file", ex);
         }
      });

      PropertyAdapter<FileSaverModel> saveAdapter = new PropertyAdapter<>(fileSaverModel, "file", true);
      saveAdapter.addValueChangeListener(evt -> {
         ProjectFilePanel panel = (ProjectFilePanel) tabbedPane.getSelectedComponent();
         panel.saveFile(fileSaverModel.getFile(), fileSaverModel.getType());
      });

      PropertyAdapter<FileCleanerModel> cleanAdapter = new PropertyAdapter<>(fileCleanerModel, "file", true);
      cleanAdapter.addValueChangeListener(evt -> {
         ProjectFilePanel panel = (ProjectFilePanel) tabbedPane.getSelectedComponent();
         panel.cleanFile(fileCleanerModel.getFile());
      });
   }

   private static final String[] READ_EXTENSIONS =
   {
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
