/*
 * file:       FileCleanerView.java
 * author:     Jon Iles
 * copyright:  (c) Packwood Software 2021
 * date:       2021-04-03
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

import com.jgoodies.binding.beans.PropertyAdapter;

/**
 * Implements the view component of the FileSaver MVC.
 */
public class FileCleanerView
{
   protected final JFileChooser m_fileChooser;
   private final Component m_parent;
   private final FileCleanerModel m_model;

   /**
    * Constructor.
    *
    * @param parent parent component for the dialog
    * @param model file save model
    */
   public FileCleanerView(Component parent, FileCleanerModel model)
   {
      m_fileChooser = new JFileChooser();
      m_parent = parent;
      m_model = model;

      PropertyAdapter<FileCleanerModel> adapter = new PropertyAdapter<>(m_model, "showDialog", true);
      adapter.addValueChangeListener(evt -> openFileChooser());
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
            m_model.setFile(null);
            m_model.setFile(m_fileChooser.getSelectedFile());
         }
         m_model.setShowDialog(false);
      }
   }
}
