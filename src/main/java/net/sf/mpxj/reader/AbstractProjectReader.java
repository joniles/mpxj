/*
 * file:       AbstractProjectReader.java
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

package net.sf.mpxj.reader;

import java.beans.Statement;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import net.sf.mpxj.ProjectFile;
import net.sf.mpxj.listener.ProjectListener;

/**
 * Abstract implementation of the ProjectReader interface
 * for readers which consume a file.
 */
public abstract class AbstractProjectReader implements ProjectReader
{
   @Override public ProjectReader setProperties(Properties props)
   {
      if (props == null)
      {
         return this;
      }

      String className = getClass().getName() + ".";

      props.entrySet().stream().filter(e -> ((String) e.getKey()).startsWith(className)).forEach(e -> {
         String methodName = "set" + ((String) e.getKey()).substring(className.length());
         Boolean propertyValue = Boolean.valueOf((String) e.getValue());

         try
         {
            new Statement(this, methodName, new Object[]
            {
               propertyValue
            }).execute();
         }

         catch (Exception ex)
         {
            // Silently ignore failures attempting to set properties
         }
      });

      return this;
   }

   @Override public void addProjectListener(ProjectListener listener)
   {
      if (m_projectListeners == null)
      {
         m_projectListeners = new ArrayList<>();
      }
      m_projectListeners.add(listener);
   }

   /**
    * Common method to add listeners to a project.
    *
    * @param project target project
    */
   protected void addListenersToProject(ProjectFile project)
   {
      project.getEventManager().addProjectListeners(m_projectListeners);
   }

   /**
    * Common method to add listeners to a reader.
    *
    * @param reader target reader
    */
   protected void addListenersToReader(ProjectReader reader)
   {
      if (m_projectListeners != null)
      {
         m_projectListeners.forEach(reader::addProjectListener);
      }
   }

   /**
    * Default "do nothing" implementation, as most readers do not need
    * to be provided with an explicit encoding.
    */
   @Override public void setCharset(Charset charset)
   {
      // default implementation - do nothing
   }

   private List<ProjectListener> m_projectListeners;
}
