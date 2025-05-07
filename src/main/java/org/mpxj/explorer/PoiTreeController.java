/*
 * file:       PoiTreeController.java
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

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;

import org.apache.poi.poifs.filesystem.POIFSFileSystem;

import org.mpxj.common.AutoCloseableHelper;

/**
 * Implements the controller component of the PoiTree MVC.
 */
public class PoiTreeController
{
   private final PoiTreeModel m_model;

   /**
    * Constructor.
    *
    * @param model PoiTree model
    */
   public PoiTreeController(PoiTreeModel model)
   {
      m_model = model;
   }

   /**
    * Command to load a file.
    *
    * @param file file to load
    */
   public void loadFile(File file)
   {
      InputStream is = null;

      try
      {
         is = Files.newInputStream(file.toPath());
         POIFSFileSystem fs = new POIFSFileSystem(is);
         m_model.setFile(fs);
      }

      catch (IOException ex)
      {
         throw new RuntimeException(ex);
      }

      finally
      {
         AutoCloseableHelper.closeQuietly(is);
      }
   }

}
