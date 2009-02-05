/*
 * file:       ProjectListener.java
 * author:     Jon Iles
 * copyright:  (c) Packwood Software 2005
 * date:       Dec 13, 2005
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

package net.sf.mpxj.listener;

import net.sf.mpxj.Resource;
import net.sf.mpxj.Task;

/**
 * Classes implementing this interface can be used to receive notification
 * of events occurring within the project file.
 */
public interface ProjectListener
{
   /**
    * This method is called when a task is read from a file.
    *
    * @param task task instance
    */
   public void taskRead(Task task);

   /**
    * This method is called when a task is written to a file.
    *
    * @param task task instance
    */
   public void taskWritten(Task task);

   /**
    * This method is called when a resource is read from a file.
    *
    * @param resource resource instance
    */
   public void resourceRead(Resource resource);

   /**
    * This method is called when a resource is written to a file.
    *
    * @param resource resource instance
    */
   public void resourceWritten(Resource resource);
}
