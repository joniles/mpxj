/*
 * file:       AbstractProjectReader.java
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

package net.sf.mpxj.reader;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import net.sf.mpxj.MPXJException;
import net.sf.mpxj.ProjectFile;

/**
 * Abstract implementation of the ProjectReader interface
 * which supplies implementations of the trivial read methods.
 */
public abstract class AbstractProjectReader implements ProjectReader
{
   /**
    * {@inheritDoc}
    */
   @Override public ProjectFile read(String fileName) throws MPXJException
   {
      FileInputStream fis = null;

      try
      {
         fis = new FileInputStream(fileName);
         ProjectFile projectFile = read(fis);
         fis.close();
         fis = null;
         return (projectFile);
      }

      catch (IOException ex)
      {
         throw new MPXJException(MPXJException.READ_ERROR, ex);
      }

      finally
      {
         if (fis != null)
         {
            try
            {
               fis.close();
            }

            catch (Exception ex)
            {
               // Silently ignore exceptions on close
            }
         }
      }
   }

   /**
    * {@inheritDoc}
    */
   @Override public ProjectFile read(File file) throws MPXJException
   {
      FileInputStream fis = null;

      try
      {
         fis = new FileInputStream(file);
         ProjectFile projectFile = read(fis);
         fis.close();
         return (projectFile);
      }

      catch (IOException ex)
      {
         throw new MPXJException(MPXJException.READ_ERROR, ex);
      }

      finally
      {
         if (fis != null)
         {
            try
            {
               fis.close();
            }

            catch (Exception ex)
            {
               // Silently ignore exceptions on close
            }
         }
      }
   }
}
