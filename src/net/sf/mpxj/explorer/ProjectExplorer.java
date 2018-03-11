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
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;

import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JTabbedPane;
import javax.swing.SwingConstants;
import javax.swing.UIManager;

import com.jgoodies.binding.beans.PropertyAdapter;

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
      EventQueue.invokeLater(new Runnable()
      {
         @Override public void run()
         {
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

      final FileChooserModel fileChooserModel = new FileChooserModel();
      final FileChooserController fileChooserController = new FileChooserController(fileChooserModel);
      @SuppressWarnings("unused")
      FileChooserView fileChooserView = new FileChooserView(m_frame, fileChooserModel);
      fileChooserModel.setExtensions("mpp", "mpx", "xml", "planner", "xer", "pmxml", "pp", "zip", "ppx", "fts", "pod", "mdb", "zip", "gan", "pep", "prx");

      final FileSaverModel fileSaverModel = new FileSaverModel();
      final FileSaverController fileSaverController = new FileSaverController(fileSaverModel);
      @SuppressWarnings("unused")
      FileSaverView fileSaverView = new FileSaverView(m_frame, fileSaverModel);
      fileSaverModel.setExtensions("sdef", "sdef", "mpx", "mpx", "planner", "xml", "pmxml", "xml", "json", "json", "mspdi", "xml");

      JMenuBar menuBar = new JMenuBar();
      m_frame.setJMenuBar(menuBar);

      JMenu mnFile = new JMenu("File");
      menuBar.add(mnFile);

      JMenuItem mntmOpen = new JMenuItem("Open File...");
      mnFile.add(mntmOpen);

      final JMenuItem mntmSave = new JMenuItem("Save As...");
      mntmSave.setEnabled(false);
      mnFile.add(mntmSave);

      //
      // Open file
      //
      mntmOpen.addActionListener(new ActionListener()
      {
         @Override public void actionPerformed(ActionEvent e)
         {
            fileChooserController.openFileChooser();
         }
      });

      //
      // Save file
      //
      mntmSave.addActionListener(new ActionListener()
      {
         @Override public void actionPerformed(ActionEvent e)
         {
            fileSaverController.openFileSaver();
         }
      });

      final JTabbedPane tabbedPane = new JTabbedPane(SwingConstants.TOP);
      m_frame.getContentPane().add(tabbedPane);

      PropertyAdapter<FileChooserModel> openAdapter = new PropertyAdapter<FileChooserModel>(fileChooserModel, "file", true);
      openAdapter.addValueChangeListener(new PropertyChangeListener()
      {
         @Override public void propertyChange(PropertyChangeEvent evt)
         {
            File file = fileChooserModel.getFile();
            tabbedPane.add(file.getName(), new ProjectFilePanel(file));
            mntmSave.setEnabled(true);
         }
      });

      PropertyAdapter<FileSaverModel> saveAdapter = new PropertyAdapter<FileSaverModel>(fileSaverModel, "file", true);
      saveAdapter.addValueChangeListener(new PropertyChangeListener()
      {
         @Override public void propertyChange(PropertyChangeEvent evt)
         {
            ProjectFilePanel panel = (ProjectFilePanel) tabbedPane.getSelectedComponent();
            panel.saveFile(fileSaverModel.getFile(), fileSaverModel.getType());
         }
      });

   }
}
