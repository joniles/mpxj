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
import java.awt.Container;
import java.io.File;

import com.jgoodies.binding.beans.PropertyAdapter;
import javax.swing.JFileChooser;
import javax.swing.JTextField;
import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.plaf.FileChooserUI;
import javax.swing.plaf.basic.BasicFileChooserUI;

/**
 * Implements the view component of the FileSaver MVC.
 */
class FileSaverView
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
      m_fileChooser = new TypeAheadFileChooser();
      m_parent = parent;
      m_model = model;

      PropertyAdapter<FileSaverModel> adapter = new PropertyAdapter<>(m_model, "showDialog", true);
      adapter.addValueChangeListener(evt -> openFileChooser());

      PropertyAdapter<FileSaverModel> extensionsAdaptor = new PropertyAdapter<>(m_model, "extensions", true);
      extensionsAdaptor.addValueChangeListener(evt -> setFileFilter());

      m_fileChooser.addPropertyChangeListener(JFileChooser.FILE_FILTER_CHANGED_PROPERTY, evt -> updateFileNameExtension());
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

      setFileName(DEFAULT_FILE_NAME);
   }

   /**
    * When the selected file filter changes, update the extension
    * of the current file name to match.
    */
   private void updateFileNameExtension()
   {
      String name = getFileName();
      if (name == null || name.trim().isEmpty())
      {
         return;
      }

      int index = name.lastIndexOf('.');
      if (index > 0)
      {
         name = name.substring(0, index);
      }

      setFileName(name);
   }

   /**
    * Set the file name shown by the file chooser, adding the extension
    * associated with the currently selected file filter.
    *
    * @param name file name without extension
    */
   private void setFileName(String name)
   {
      String extension = getSelectedExtension();
      if (extension != null)
      {
         name = name + "." + extension;
      }
      m_fileChooser.setSelectedFile(new File(m_fileChooser.getCurrentDirectory(), name));
   }

   /**
    * Retrieve the extension associated with the currently selected file filter.
    *
    * @return extension or null
    */
   private String getSelectedExtension()
   {
      FileFilter filter = m_fileChooser.getFileFilter();
      if (filter instanceof FileNameExtensionFilter)
      {
         String[] extensions = ((FileNameExtensionFilter) filter).getExtensions();
         if (extensions.length != 0)
         {
            return extensions[0];
         }
      }
      return null;
   }

   /**
    * Retrieve the file name currently shown by the file chooser,
    * including any text typed by the user.
    *
    * @return file name or null
    */
   private String getFileName()
   {
      FileChooserUI ui = m_fileChooser.getUI();
      if (ui instanceof BasicFileChooserUI)
      {
         return ((BasicFileChooserUI) ui).getFileName();
      }

      // Other look and feels (for example macOS Aqua) don't expose
      // the file name, so read it from the file name text field.
      JTextField field = findTextField(m_fileChooser);
      if (field != null)
      {
         return field.getText();
      }

      File file = m_fileChooser.getSelectedFile();
      return file == null ? null : file.getName();
   }

   /**
    * Recursively search for a text field within a container.
    *
    * @param container parent container
    * @return text field or null
    */
   private JTextField findTextField(Container container)
   {
      for (Component component : container.getComponents())
      {
         if (component instanceof JTextField)
         {
            return (JTextField) component;
         }

         if (component instanceof Container)
         {
            JTextField field = findTextField((Container) component);
            if (field != null)
            {
               return field;
            }
         }
      }
      return null;
   }

   private static final String DEFAULT_FILE_NAME = "Project1";
}
