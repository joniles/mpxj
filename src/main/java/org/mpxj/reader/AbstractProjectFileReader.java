/*
 * file:       AbstractProjectFileReader.java
 * author:     Jon Iles
 * copyright:  (c) Packwood Software 2020
 * date:       04/09/2020
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

package org.mpxj.reader;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collections;
import java.util.List;

import org.mpxj.MPXJException;
import org.mpxj.ProjectFile;
import org.mpxj.common.FileHelper;
import org.mpxj.common.InputStreamHelper;

/**
 * Abstract implementation of the ProjectReader interface
 * for readers which consume a file.
 */
public abstract class AbstractProjectFileReader extends AbstractProjectReader
{
   @Override public ProjectFile read(String fileName) throws MPXJException
   {
      return read(new File(fileName));
   }

   /**
    * Default implementation of readAll to support file
    * formats which do not contain multiple schedules.
    */
   @Override public List<ProjectFile> readAll(String fileName) throws MPXJException
   {
      return readAll(new File(fileName));
   }

   @Override public ProjectFile read(InputStream inputStream) throws MPXJException
   {
      File tempFile = null;

      try
      {
         tempFile = InputStreamHelper.writeStreamToTempFile(inputStream, "tmp");
         return read(tempFile);
      }

      catch (IOException ex)
      {
         throw new MPXJException("Failed to read file", ex);
      }

      finally
      {
         FileHelper.deleteQuietly(tempFile);
      }
   }

   /**
    * Default implementation of readAll to support file
    * formats which do not contain multiple schedules.
    */
   @Override public List<ProjectFile> readAll(InputStream inputStream) throws MPXJException
   {
      File tempFile = null;

      try
      {
         tempFile = InputStreamHelper.writeStreamToTempFile(inputStream, "tmp");
         return readAll(tempFile);
      }

      catch (IOException ex)
      {
         throw new MPXJException("Failed to read file", ex);
      }

      finally
      {
         FileHelper.deleteQuietly(tempFile);
      }
   }

   /**
    * Default implementation of readAll. Reads a single project,
    * if successful, returns a list with a single entry. If unsuccessful,
    * returns an empty list.
    *
    * @param file File instance
    * @return list of projects
    */
   @Override public List<ProjectFile> readAll(File file) throws MPXJException
   {
      ProjectFile projectFile = read(file);
      return projectFile == null ? Collections.emptyList() : Collections.singletonList(projectFile);
   }
}
