/*
 * file:       AbstractProjectWriter.java
 * author:     Jon Iles
 * copyright:  (c) Packwood Software 2005
 * date:       Dec 21, 2005
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

package org.mpxj.writer;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import org.mpxj.ProjectFile;

/**
 * Abstract implementation of the ProjectWriter interface
 * which supplies implementations of the trivial write methods.
 */
public abstract class AbstractProjectWriter implements ProjectWriter
{
   @Override public void write(ProjectFile projectFile, String fileName) throws IOException
   {
      FileOutputStream fos = new FileOutputStream(fileName);
      write(projectFile, fos);
      fos.flush();
      fos.close();
   }

   @Override public void write(ProjectFile projectFile, File file) throws IOException
   {
      FileOutputStream fos = new FileOutputStream(file);
      write(projectFile, fos);
      fos.flush();
      fos.close();
   }
}
