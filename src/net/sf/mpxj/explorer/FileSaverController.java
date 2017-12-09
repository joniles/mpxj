/*
 * file:       FileSaverController.java
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

package net.sf.mpxj.explorer;

/**
 * Implements the controller component of the FileSaver MVC.
 */
public class FileSaverController
{
   private final FileSaverModel m_model;

   /**
    * Constructor.
    *
    * @param model file chooser model
    */
   public FileSaverController(FileSaverModel model)
   {
      m_model = model;
   }

   /**
    * Open the file choose dialog.
    */
   public void openFileSaver()
   {
      m_model.setShowDialog(true);
   }
}
