/*
 * file:       ProjectReader.java
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

package org.mpxj.reader;

import java.io.File;
import java.io.InputStream;
import java.util.List;

import org.mpxj.MPXJException;
import org.mpxj.ProjectFile;
import org.mpxj.listener.ProjectListener;

/**
 * This interface is implemented by all classes which can read project
 * files of any type and generate an ProjectFile instance from the contents
 * of the file.
 */
public interface ProjectReader
{
   /**
    * Add a listener to receive events as a project is being read.
    *
    * @param listener ProjectListener instance
    */
   void addProjectListener(ProjectListener listener);

   /**
    * Read a single schedule from a file where the file name is supplied.
    *
    * @param fileName file name
    * @return ProjectFile instance
    */
   ProjectFile read(String fileName) throws MPXJException;

   /**
    * Read all schedules from a file where the file name is supplied.
    *
    * @param fileName file name
    * @return ProjectFile instance
    */
   List<ProjectFile> readAll(String fileName) throws MPXJException;

   /**
    * Read a single schedule from a file where a File instance is supplied.
    *
    * @param file File instance
    * @return ProjectFile instance
    */
   ProjectFile read(File file) throws MPXJException;

   /**
    * Read all schedules from a file where a File instance is supplied.
    *
    * @param file File instance
    * @return ProjectFile instance
    */
   List<ProjectFile> readAll(File file) throws MPXJException;

   /**
    * Read a single schedule from a file where the contents of the project file
    * are supplied via an input stream.
    *
    * @param inputStream InputStream instance
    * @return ProjectFile instance
    */
   ProjectFile read(InputStream inputStream) throws MPXJException;

   /**
    * Read all schedules from a file where the contents of the project file
    * are supplied via an input stream.
    *
    * @param inputStream InputStream instance
    * @return ProjectFile instance
    */
   List<ProjectFile> readAll(InputStream inputStream) throws MPXJException;

}
