/*
 * file:       FileChooserView.java
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

import java.awt.Component;

import javax.swing.JFileChooser;
import javax.swing.filechooser.FileNameExtensionFilter;

import com.jgoodies.binding.beans.PropertyAdapter;

/**
 * Implements the view component of the FileChooser MVC.
 */
public class FileChooserView
{
   protected final JFileChooser m_fileChooser;
   private final Component m_parent;
   private final FileChooserModel m_model;

   /**
    * Constructor.
    *
    * @param parent parent component for the dialog
    * @param model file choose model
    */
   public FileChooserView(Component parent, FileChooserModel model)
   {
      m_fileChooser = new JFileChooser();
      m_fileChooser.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
      m_parent = parent;
      m_model = model;

      PropertyAdapter<FileChooserModel> adapter = new PropertyAdapter<>(m_model, "showDialog", true);
      adapter.addValueChangeListener(evt -> openFileChooser());

      PropertyAdapter<FileChooserModel> extensionsAdaptor = new PropertyAdapter<>(m_model, "extensions", true);
      extensionsAdaptor.addValueChangeListener(evt -> setFileFilter());
   }

   /**
    * Command to open the file chooser.
    */
   protected void openFileChooser()
   {
      if (m_model.getShowDialog())
      {
         m_fileChooser.setCurrentDirectory(m_model.getCurrentDirectory());
         if (m_fileChooser.showOpenDialog(m_parent) == JFileChooser.APPROVE_OPTION)
         {
            m_model.setFile(m_fileChooser.getSelectedFile());
         }
         m_model.setCurrentDirectory(m_fileChooser.getCurrentDirectory());
         m_model.setShowDialog(false);
      }
   }

   /**
    * Update the file chooser's filter settings.
    */
   protected void setFileFilter()
   {
      String[] extensions = m_model.getExtensions();
      for (String extension : extensions)
      {
         m_fileChooser.addChoosableFileFilter(new FileNameExtensionFilter(extension.toUpperCase() + " Files", extension));
      }
      m_fileChooser.addChoosableFileFilter(new FileNameExtensionFilter("Project Files", m_model.getExtensions()));
   }
}
