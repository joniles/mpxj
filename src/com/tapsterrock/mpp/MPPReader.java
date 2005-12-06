/*
 * file:       MPPReader.java
 * author:     Jon Iles
 * copyright:  (c) Tapster Rock Limited 2005
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

package com.tapsterrock.mpp;

import java.io.IOException;

import org.apache.poi.poifs.filesystem.DirectoryEntry;

import com.tapsterrock.mpx.MPXException;

/**
 * This interface is implemented by classes which understand how
 * to read one of the MPP file formats.
 */
public interface MPPReader
{
   /**
    * Reads an MPP file an populates the file data structure.
    *
    * @param file data structure to be populated
    * @param root Root of the POI file system.
    */   
   public void process (MPPFile file, DirectoryEntry root)
      throws MPXException, IOException;
}
