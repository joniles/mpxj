/*
 * file:       FileCleanerModel.java
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

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.io.File;

/**
 * Implements the model component of the FileChooser MVC.
 */
public class FileCleanerModel
{
   private final PropertyChangeSupport m_changeSupport = new PropertyChangeSupport(this);
   private boolean m_showDialog;
   private File m_file;

   /**
    * Retrieves the show dialog flag.
    *
    * @return show dialog flag
    */
   public boolean getShowDialog()
   {
      return m_showDialog;
   }

   /**
    * Sets the show dialog flag.
    *
    * @param showDialog show dialog flag
    */
   public void setShowDialog(boolean showDialog)
   {
      m_changeSupport.firePropertyChange("showDialog", m_showDialog, m_showDialog = showDialog);
   }

   /**
    * Retrieves the file selected by the user.
    *
    * @return file selected by the user
    */
   public File getFile()
   {
      return m_file;
   }

   /**
    * Sets the file selected by the user.
    *
    * @param file file selected by the user.
    */
   public void setFile(File file)
   {
      m_changeSupport.firePropertyChange("file", m_file, m_file = file);
   }

   /**
    * Add a property change listener.
    *
    * @param listener property change listener
    */
   public void addPropertyChangeListener(PropertyChangeListener listener)
   {
      m_changeSupport.addPropertyChangeListener(listener);
   }

   /**
    * Add a property change listener for a named property.
    *
    * @param propertyName property name
    * @param listener listener
    */
   public void addPropertyChangeListener(String propertyName, PropertyChangeListener listener)
   {
      m_changeSupport.addPropertyChangeListener(propertyName, listener);
   }

   /**
    * Remove a property change listener.
    *
    * @param listener property change listener
    */
   public void removePropertyChangeListener(PropertyChangeListener listener)
   {
      m_changeSupport.removePropertyChangeListener(listener);
   }
}
