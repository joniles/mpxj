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
      m_projectFile = projectFile;
      m_writer = new OutputStreamWriter(new BufferedOutputStream(out), projectFile.getFileCreationRecord().getCodePage().getCharset());
      
      try
      {
         write();
      }
      
      finally
      {
         m_writer = null;
         m_projectFile = null;
         m_resourceModel = null;
         m_taskModel = null;
      }
   }
   
   /**
    * Writes the contents of the project file as MPX records.
    * 
    * @throws IOException
    */
   private void write ()
      throws IOException
   {       
      m_writer.write(m_projectFile.getFileCreationRecord().toString());
      m_writer.write(m_projectFile.getProjectHeader().toString());
      
      Iterator iter = m_projectFile.getBaseCalendars().iterator();   
      while (iter.hasNext())
      {
         m_writer.write((iter.next()).toString());
      }
   
      m_resourceModel = new ResourceModel(m_projectFile);
      m_writer.write(m_resourceModel.toString());
      iter = m_projectFile.getAllResources().iterator();   
      while (iter.hasNext())
      {
         m_writer.write(((Resource)iter.next()).toString(m_resourceModel));
      }      

      m_taskModel = new TaskModel(m_projectFile);
      m_writer.write(m_taskModel.toString());
      writeTasks (m_projectFile.getChildTasks());
      
      m_writer.flush();   
   }

   /**
    * Recursively write tasks.
    * 
    * @param tasks list of tasks
    * @throws IOException
    */
   private void writeTasks (List tasks)
      throws IOException
   {
      Iterator iter = tasks.iterator();
      while (iter.hasNext())
      {
         Task task = (Task)iter.next();
         m_writer.write(task.toString(m_taskModel));
         writeTasks(task.getChildTasks());
      }      
   }
   
   private ProjectFile m_projectFile;
   private OutputStreamWriter m_writer;
   private ResourceModel m_resourceModel;
   private TaskModel m_taskModel;
}
