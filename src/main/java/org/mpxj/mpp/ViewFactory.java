/*
 * file:       ViewFactory.java
 * author:     Jon Iles
 * copyright:  (c) Packwood Software 2005
 * date:       Apr 7, 2005
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

package org.mpxj.mpp;

import java.io.IOException;
import java.util.Map;

import org.mpxj.ProjectFile;
import org.mpxj.View;

/**
 * This interface is implemented by classes which can create View classes
 * from the data extracted from an MS Project file.
 */
interface ViewFactory
{
   /**
    * This method is called to create a view.
    *
    * @param file parent MPP file
    * @param fixedMeta fixed meta data
    * @param fixedData view fixed data
    * @param varData view var data
    * @param fontBases map of font bases
    * @return View instance
    */
   View createView(ProjectFile file, byte[] fixedMeta, byte[] fixedData, Var2Data varData, Map<Integer, FontBase> fontBases) throws IOException;
}
