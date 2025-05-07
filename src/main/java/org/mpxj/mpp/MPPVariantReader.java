/*
 * file:       MPPVariantReader.java
 * author:     Jon Iles
 * copyright:  (c) Packwood Software 2005
 * date:       Dec 5, 2005
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

import org.mpxj.MPXJException;
import org.mpxj.ProjectFile;

import org.apache.poi.poifs.filesystem.DirectoryEntry;

/**
 * This interface is implemented by classes which understand how
 * to read one of the MPP file formats.
 */
interface MPPVariantReader
{
   /**
    * Reads an MPP file an populates the file data structure.
    *
    * @param reader parent file reader
    * @param file data structure to be populated
    * @param root Root of the POI file system.
    */
   void process(MPPReader reader, ProjectFile file, DirectoryEntry root) throws MPXJException, IOException;
}
