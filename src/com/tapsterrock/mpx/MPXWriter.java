/*
 * file:       MPXWriter.java
 * author:     Jon Iles
 * copyright:  (c) Tapster Rock Limited 2005
 * date:       Jan 3, 2006
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

package com.tapsterrock.mpx;

import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.Iterator;
import java.util.List;

/**
 * This class creates a new MPX file from the contents of an MPXFile instance.
 */
public final class MPXWriter extends AbstractProjectWriter
{
   /**
    * {@inheritDoc}
    */
   public void write (ProjectFile projectFile, OutputStream out)
      throws IOException
   {
      write(projectFile, new OutputStreamWriter(new BufferedOutputStream(out), projectFile.getFileCreationRecord().getCodePage().getCharset()));
   }
   
   /**
    * Writes the contents of the project file as MPX records.
    * 
    * @param projectFile project file
    * @param w output stream
    * @throws IOException
    */
   private void write (ProjectFile projectFile, OutputStreamWriter w)
      throws IOException
   {       
      w.write(projectFile.getFileCreationRecord().toString());
      w.write(projectFile.getProjectHeader().toString());
      
      Iterator iter = projectFile.getBaseCalendars().iterator();   
      while (iter.hasNext())
      {
         w.write((iter.next()).toString());
      }
   
      w.write(projectFile.getResourceModel().toString());
      iter = projectFile.getAllResources().iterator();   
      while (iter.hasNext())
      {
         w.write((iter.next()).toString());
      }      

      w.write(projectFile.getTaskModel().toString());      
      writeTasks (projectFile.getChildTasks(), w);
      
      w.flush();   
   }

   /**
    * Recursively write tasks.
    * 
    * @param tasks list of tasks
    * @param w output stream writer
    * @throws IOException
    */
   private void writeTasks (List tasks, OutputStreamWriter w)
      throws IOException
   {
      Iterator iter = tasks.iterator();
      while (iter.hasNext())
      {
         Task task = (Task)iter.next();
         w.write(task.toString());
         writeTasks(task.getChildTasks(), w);
      }      
   }
}
