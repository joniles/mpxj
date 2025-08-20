/*
 * file:       FileSaverView.java
 * author:     Jon Iles
 * copyright:  (c) Packwood Software 2017
 * date:       2017-11-23
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
 * Implements the view component of the FileSaver MVC.
 */
public class FileSaverView
{
   protected final JFileChooser m_fileChooser;
   private final Component m_parent;
   private final FileSaverModel m_model;

   /**
    * Constructor.
    *
    * @param parent parent component for the dialog
    * @param model file save model
    */
   public FileSaverView(Component parent, FileSaverModel model)
   {
      m_fileChooser = new JFileChooser();
      m_parent = parent;
      m_model = model;

      PropertyAdapter<FileSaverModel> adapter = new PropertyAdapter<>(m_model, "showDialog", true);
      adapter.addValueChangeListener(evt -> openFileChooser());

      PropertyAdapter<FileSaverModel> extensionsAdaptor = new PropertyAdapter<>(m_model, "extensions", true);
      extensionsAdaptor.addValueChangeListener(evt -> setFileFilter());
   }

   /**
    * Command to open the file chooser.
    */
   protected void openFileChooser()
   {
      if (m_model.getShowDialog())
      {
         if (m_fileChooser.showSaveDialog(m_parent) == JFileChooser.APPROVE_OPTION)
         {
            FileNameExtensionFilter filter = (FileNameExtensionFilter) m_fileChooser.getFileFilter();
            String description = filter.getDescription();
            m_model.setType(description.substring(0, description.indexOf(' ')));
            m_model.setFile(null);
            m_model.setFile(m_fileChooser.getSelectedFile());
         }
         m_model.setShowDialog(false);
      }
   }

   /**
    * Update the file chooser's filter settings.
    */
   protected void setFileFilter()
   {
      m_fileChooser.setAcceptAllFileFilterUsed(false);
      String[] extensions = m_model.getExtensions();
      for (int extensionIndex = 0; extensionIndex < extensions.length; extensionIndex += 2)
      {
         m_fileChooser.addChoosableFileFilter(new FileNameExtensionFilter(extensions[extensionIndex].toUpperCase() + " File", extensions[extensionIndex + 1]));
      }
   }
}
