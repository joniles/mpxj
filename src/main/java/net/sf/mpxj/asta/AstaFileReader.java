/*
 * file:       AstaFileReader.java
 * author:     Jon Iles
 * copyright:  (c) Packwood Software 2012
 * date:       23/04/2012
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

package net.sf.mpxj.asta;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.List;

import net.sf.mpxj.MPXJException;
import net.sf.mpxj.ProjectFile;
import net.sf.mpxj.reader.AbstractProjectStreamReader;
import net.sf.mpxj.reader.ProjectReader;

/**
 * This class provides a generic front end to read project data from
 * an Asta PP file. Determines if the file is a text file or a SQLite database
 * and takes the appropriate action.
 */
public final class AstaFileReader extends AbstractProjectStreamReader
{
   /**
    * {@inheritDoc}
    */
   @Override public ProjectFile read(InputStream inputStream) throws MPXJException
   {
      try
      {
         BufferedInputStream is = new BufferedInputStream(inputStream);
         is.mark(100);
         byte[] buffer = new byte[SQLITE_TEXT.length()];
         is.read(buffer);
         is.reset();
         String actualText = new String(buffer);
         ProjectReader reader;
         if (SQLITE_TEXT.equals(actualText))
         {
            reader = new AstaDatabaseFileReader();
         }
         else
         {
            reader = new AstaTextFileReader();
         }

         addListenersToReader(reader);

         return reader.read(is);
      }

      catch (IOException ex)
      {
         throw new MPXJException("Failed to read file", ex);
      }
   }

   /**
    * {@inheritDoc}
    */
   @Override public List<ProjectFile> readAll(InputStream inputStream) throws MPXJException
   {
      return Arrays.asList(read(inputStream));
   }

   private static final String SQLITE_TEXT = "SQLite format";
}