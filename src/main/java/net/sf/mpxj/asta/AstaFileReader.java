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
import java.util.LinkedList;
import java.util.List;

import net.sf.mpxj.MPXJException;
import net.sf.mpxj.ProjectFile;
import net.sf.mpxj.listener.ProjectListener;
import net.sf.mpxj.reader.AbstractProjectReader;
import net.sf.mpxj.reader.ProjectReader;

/**
 * This class provides a generic front end to read project data from
 * an Asta PP file. Determines if the file is a text file or a SQLite database
 * and takes the appropriate action.
 */
public final class AstaFileReader extends AbstractProjectReader
{
   /**
    * {@inheritDoc}
    */
   @Override public void addProjectListener(ProjectListener listener)
   {
      if (m_projectListeners == null)
      {
         m_projectListeners = new LinkedList<ProjectListener>();
      }
      m_projectListeners.add(listener);
   }

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
         ProjectFile result;
         if (SQLITE_TEXT.equals(actualText))
         {
            result = readDatabaseFile(is);
         }
         else
         {
            result = readTextFile(is);
         }
         return result;
      }

      catch (IOException ex)
      {
         throw new MPXJException("Failed to read file", ex);
      }
   }

   /**
    * Adds any listeners attached to this reader to the reader created internally.
    *
    * @param reader internal project reader
    */
   private void addListeners(ProjectReader reader)
   {
      if (m_projectListeners != null)
      {
         for (ProjectListener listener : m_projectListeners)
         {
            reader.addProjectListener(listener);
         }
      }
   }

   /**
    * Process a text-based PP file.
    *
    * @param inputStream file input stream
    * @return ProjectFile instance
    */
   private ProjectFile readTextFile(InputStream inputStream) throws MPXJException
   {
      ProjectReader reader = new AstaTextFileReader();
      addListeners(reader);
      return reader.read(inputStream);
   }

   /**
    * Process a SQLite database PP file.
    *
    * @param inputStream file input stream
    * @return ProjectFile instance
    */
   private ProjectFile readDatabaseFile(InputStream inputStream) throws MPXJException
   {
      ProjectReader reader = new AstaDatabaseFileReader();
      addListeners(reader);
      return reader.read(inputStream);
   }

   private List<ProjectListener> m_projectListeners;

   private static final String SQLITE_TEXT = "SQLite format";
}