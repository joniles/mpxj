/*
 * file:       MppExplorer.java
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

import java.awt.EventQueue;
import java.awt.GridLayout;
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
 * MppExplorer is a Swing UI used to examine the contents of an MPP file.
 */
public class MppExplorer
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
            MppExplorer window = new MppExplorer();
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
   public MppExplorer()
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
      fileChooserModel.setExtensions("mpp");

      JMenuBar menuBar = new JMenuBar();
      m_frame.setJMenuBar(menuBar);

      JMenu mnFile = new JMenu("File");
      menuBar.add(mnFile);

      JMenuItem mntmOpen = new JMenuItem("Open");
      mnFile.add(mntmOpen);

      //
      // Open file
      //
      mntmOpen.addActionListener(e -> fileChooserController.openFileChooser());

      final JTabbedPane tabbedPane = new JTabbedPane(SwingConstants.TOP);
      m_frame.getContentPane().add(tabbedPane);

      PropertyAdapter<FileChooserModel> adapter = new PropertyAdapter<>(fileChooserModel, "file", true);
      adapter.addValueChangeListener(evt -> {
         File file = fileChooserModel.getFile();
         tabbedPane.add(file.getName(), new MppFilePanel(file));
      });
   }
}
